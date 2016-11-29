/*
 * Licensed Materials - Property of IBM
 * 
 * CICS SupportPac CA1Y - CICS® TS support for sending emails
 * 
 * © Copyright IBM Corporation 2012 - 2016. All Rights Reserved.
 * 
 *  US Government Users Restricted Rights - Use, duplication, or disclosure
 *  restricted by GSA ADP Schedule Contract with IBM Corporation.
 */

package com.ibm.cics.ca1y;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.jzos.ZFile;
import com.ibm.jzos.ZFileConstants;
import com.ibm.jzos.ZFileException;

/**
 * Class to emit a message to a JZOS File.
 * 
 * @author Mark Cocker <mark_cocker@uk.ibm.com>
 * @version 1.0
 * @since 2014-02-13
 */
public class JZOSFile implements EmitAdapter {
	/**
	 * Copyright statement to be included in the compiled class.
	 */
	static final String COPYRIGHT = "Licensed Materials - Property of IBM. "
			+ "CICS SupportPac CA1Y (c) Copyright IBM Corporation 2012 - 2015. All Rights Reserved. "
			+ "US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corporation";

	/**
	 * Logging
	 */
	private static Logger logger = Logger.getLogger(JZOSFile.class.getName());

	/**
	 * Property to specify the queue name. Following the character and size
	 * rules defined by CICS.
	 */
	private static final String JZOSFILE = "file";
	private static final String JZOSFILE_OPTIONS = "file.options";
	private static final String JZOSFILE_OPTIONS_BINARY_DEFAULT = "ab";
	private static final String JZOSFILE_OPTIONS_TEXT_DEFAULT = "at";
	private static final String JZOSFILE_FLAGS = "file.flags";
	private static final String JZOSFILE_FLAGS_DEFAULT = "FLAG_DISP_SHR";

	/**
	 * Property to specify the content to be written to the queue.
	 */
	private static final String JZOSFILE_CONTENT = "file.content";

	/**
	 * Property to specify the size of records the content should be broken up
	 * into. Each chunk will be written as a separate record. Note other
	 * concurrent tasks may also write records that become interspersed with the
	 * chunks written here.
	 */
	private static final String JZOSFILE_CONTENT_LENGTH_CHUNK = "file.content.length.chunk";

	/**
	 * Property to specify the maximum length of content to be written to the
	 * queue. Content over this length will be truncated.
	 */
	private static final String JZOSFILE_CONTENT_LENGTH_MAX = "file.content.length.max";

	/**
	 * Property to specify content should be written with the specified
	 * encoding.
	 */
	private static final String JZOSFILE_CONTENT_ENCODING = "file.content.encoding";

	private EmitProperties props;

	public JZOSFile(EmitProperties p) {
		props = p;
	}

	public boolean send() {
		ZFile zFile = null;
		
		String zFileFlagsString = props.getProperty(JZOSFILE_FLAGS, JZOSFILE_FLAGS_DEFAULT);
		
		int zFileFlags = 0;
		if (zFileFlagsString.indexOf("FLAG_DISP_SHR") >= 0) {
			zFileFlags += ZFileConstants.FLAG_DISP_SHR;
		}
		
		if (zFileFlagsString.indexOf("FLAG_PDS_ENQ") >= 0) {
			zFileFlags += ZFileConstants.FLAG_PDS_ENQ;
		}

		if (zFileFlagsString.indexOf("FLAG_DISP_OLD") >= 0) {
			zFileFlags += ZFileConstants.FLAG_DISP_OLD;
		}

		if (zFileFlagsString.indexOf("FLAG_DISP_MOD") >= 0) {
			zFileFlags += ZFileConstants.FLAG_DISP_MOD;
		}

		try {
			if (props.getPropertyAttachment(JZOSFILE_CONTENT) == null) {
				// Open file with text write as the default
				zFile = new ZFile(props.getProperty(JZOSFILE), props.getProperty(JZOSFILE_OPTIONS, JZOSFILE_OPTIONS_TEXT_DEFAULT), zFileFlags);
				
			} else {
				// Open file with binary write as the default
				zFile = new ZFile(props.getProperty(JZOSFILE), props.getProperty(JZOSFILE_OPTIONS, JZOSFILE_OPTIONS_BINARY_DEFAULT), zFileFlags);
			}

		} catch (ZFileException e1) {
			logger.warning(Emit.messages.getString("JZOSFile_LOG_FAIL_MESSAGE") + " " + getMessageSummary());

			e1.printStackTrace();
			
			return false;
		}
		
		if (props.getPropertyAttachment(JZOSFILE_CONTENT) != null) {
			// Content is binary
			
			byte[] queueContent = props.getPropertyAttachment(JZOSFILE_CONTENT);

			if (props.getProperty(JZOSFILE_CONTENT_LENGTH_MAX) != null) {
				int queueContentLengthMax = Integer.parseInt(props.getProperty(JZOSFILE_CONTENT_LENGTH_MAX, ""));

				if (queueContent.length > queueContentLengthMax) {
					// Truncate at maximum length
					queueContent = Arrays.copyOf(queueContent, queueContentLengthMax);
				}
			}

			int[][] chunks = Util.getChunks(queueContent, props.getProperty(JZOSFILE_CONTENT_LENGTH_CHUNK));

			for (int[] chunk : chunks) {
				byte[] b = null;

				if ((chunk[0] == 0) && (chunk[1] == queueContent.length)) {
					// Write all content
					b = queueContent;

				} else {
					// Write a chunk of content
					b = Arrays.copyOfRange(queueContent, chunk[0], chunk[1]);
				}

				try {

					zFile.write(b);
					

				} catch (Exception e) {
					logger.warning(Emit.messages.getString("JZOSFile_LOG_FAIL_MESSAGE") + " " + getMessageSummary());

					e.printStackTrace();

					try {
						zFile.close();
					} catch (Exception e1) {
					}

					return false;
				}
			}

		} else {
			// Content is text
			String jzosFileContent = props.getProperty(JZOSFILE_CONTENT);

			if (props.getProperty(JZOSFILE_CONTENT_LENGTH_MAX) != null) {
				int queueContentLengthMax = Integer.parseInt(props.getProperty(JZOSFILE_CONTENT_LENGTH_MAX, ""));

				if (jzosFileContent.length() > queueContentLengthMax) {
					// Truncate at maximum length
					jzosFileContent = jzosFileContent.substring(0, queueContentLengthMax);
				}
			}

			int[][] chunks = Util.getChunks(jzosFileContent, props.getProperty(JZOSFILE_CONTENT_LENGTH_CHUNK));

			for (int[] chunk : chunks) {
				try {
					if (props.containsKey(JZOSFILE_CONTENT_ENCODING)) {
						zFile.write(jzosFileContent.substring(chunk[0], chunk[1]).getBytes(props.getProperty(JZOSFILE_CONTENT_ENCODING)));

					} else {
						zFile.write(jzosFileContent.substring(chunk[0], chunk[1]).getBytes());
					}
					
				} catch (Exception e) {
					logger.warning(Emit.messages.getString("JZOSFile_LOG_FAIL_MESSAGE") + " " + getMessageSummary());

					e.printStackTrace();

					try {
						zFile.close();
					} catch (Exception e1) {
					}

					return false;
				}
			}
		}

		if (logger.isLoggable(Level.INFO)) {
			logger.info(Emit.messages.getString("JZOSFile_LOG_SUCCESS_MESSAGE") + " " + getMessageSummary());
		}
		
		try {
			zFile.close();
		} catch (Exception e) {
		}

		return true;
	}
	
	/**
	 * Return true if we should attempt to emit 
	 * 
	 * @return true if the entries in EmitProperties are valid for emission. 
	 */
	public static boolean validForEmission(EmitProperties props) {
		return props.containsKey(JZOSFile.JZOSFILE_CONTENT);
	}

	/**
	 * Return a short summary of the contents to be written to the queue.
	 * 
	 * @return short summary of the contents to be written to the queue.
	 */
	private String getMessageSummary() {
		StringBuilder sb = new StringBuilder();

		sb.append(JZOSFILE).append(":").append(props.getProperty(JZOSFILE, ""));

		if (props.containsKey(JZOSFILE_OPTIONS))
			sb.append(",").append(JZOSFILE_OPTIONS).append(":").append(props.getProperty(JZOSFILE_OPTIONS, ""));

		if (props.containsKey(JZOSFILE_FLAGS))
			sb.append(",").append(JZOSFILE_FLAGS).append(":").append(props.getProperty(JZOSFILE_FLAGS, ""));

		if (props.containsKey(JZOSFILE_CONTENT_LENGTH_CHUNK))
			sb.append(",").append(JZOSFILE_CONTENT_LENGTH_CHUNK).append(":").append(props.getProperty(JZOSFILE_CONTENT_LENGTH_CHUNK, ""));

		if (props.containsKey(JZOSFILE_CONTENT_LENGTH_MAX))
			sb.append(",").append(JZOSFILE_CONTENT_LENGTH_MAX).append(":").append(props.getProperty(JZOSFILE_CONTENT_LENGTH_MAX, ""));

		if (props.containsKey(JZOSFILE_CONTENT_ENCODING))
			sb.append(",").append(JZOSFILE_CONTENT_ENCODING).append(":").append(props.getProperty(JZOSFILE_CONTENT_ENCODING, ""));

		return sb.toString();
	}
}