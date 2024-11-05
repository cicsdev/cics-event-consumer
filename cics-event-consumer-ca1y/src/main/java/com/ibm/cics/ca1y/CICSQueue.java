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

import com.ibm.cics.server.*;

/**
 * Class to emit a message to a CICS temporary data (TD) or temporary storage
 * (TS) queue.
 * 
 * @author Mark Cocker <mark_cocker@uk.ibm.com>
 * @version 1.0
 * @since 2012-02-10
 */
public class CICSQueue implements EmitAdapter {
	/**
	 * Copyright statement to be included in the compiled class.
	 */
	static final String COPYRIGHT = "Licensed Materials - Property of IBM. "
			+ "CICS SupportPac CA1Y (c) Copyright IBM Corporation 2012 - 2016. All Rights Reserved. "
			+ "US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corporation";

	/**
	 * Logging
	 */
	private static Logger logger = Logger.getLogger(CICSQueue.class.getName());

	/**
	 * Property to specify the queue name. Following the character and size
	 * rules defined by CICS.
	 */
	private static final String QUEUE = "queue";

	/**
	 * Property to specify the system identifier to route the request to. Follow
	 * the character and size rules defined by CICS.
	 */
	private static final String QUEUE_SYSID = "queue.sysid";

	/**
	 * Property to specify if this is a temporary storage or temporary data
	 * queue. Temporary storage is the default.
	 */
	private static final String QUEUE_TYPE = "queue.type";
	private static final String QUEUE_TYPE_TS = "ts";
	private static final String QUEUE_TYPE_TD = "td";
	private static final String QUEUE_TYPE_DEFAULT = QUEUE_TYPE_TS;

	/**
	 * Property for temporary storage queues if the queue is not already
	 * defined, if it should be backed by auxiliary or main storage. Main
	 * storage is the default.
	 */
	private static final String QUEUE_TS_STORAGE = "queue.ts.storage";
	private static final String QUEUE_TS_STORAGE_MAIN = "main";
	private static final String QUEUE_TS_STORAGE_AUXILIARY = "auxiliary";
	private static final String QUEUE_TS_STORAGE_DEFAULT = QUEUE_TS_STORAGE_MAIN;

	/**
	 * Property to specify the content to be written to the queue.
	 */
	private static final String QUEUE_CONTENT = "queue.content";

	/**
	 * Property to specify the size of records the content should be broken up
	 * into. Each chunk will be written as a separate record. Note other
	 * concurrent tasks may also write records that become interspersed with the
	 * chunks written here.
	 */
	private static final String QUEUE_CONTENT_LENGTH_CHUNK = "queue.content.length.chunk";

	/**
	 * Property to specify the maximum length of content to be written to the
	 * queue. Content over this length will be truncated.
	 */
	private static final String QUEUE_CONTENT_LENGTH_MAX = "queue.content.length.max";

	/**
	 * Property to specify content should be written with the specified
	 * encoding.
	 */
	private static final String QUEUE_CONTENT_ENCODING = "queue.content.encoding";

	private EmitProperties props;
	private TSQ tsq;
	private TDQ tdq;

	public CICSQueue(EmitProperties p) {
		props = p;

		if (!props.containsKey(QUEUE_TYPE)) {
			props.setProperty(QUEUE_TYPE, QUEUE_TYPE_DEFAULT);
		}

		if (props.isPropertyEqualsIgnoreCase(QUEUE_TYPE, QUEUE_TYPE_TS)) {
			tsq = new TSQ();
			tsq.setName(props.getProperty(QUEUE));

			if (!props.containsKey(QUEUE_TS_STORAGE)) {
				props.setProperty(QUEUE_TS_STORAGE, QUEUE_TS_STORAGE_DEFAULT);
			}

			if (props.isPropertyEqualsIgnoreCase(QUEUE_TS_STORAGE, QUEUE_TS_STORAGE_MAIN)) {
				tsq.setType(TSQType.MAIN);

			} else if (props.isPropertyEqualsIgnoreCase(QUEUE_TS_STORAGE, QUEUE_TS_STORAGE_AUXILIARY)) {
				tsq.setType(TSQType.AUXILIARY);
			}
			
		} else if (props.isPropertyEqualsIgnoreCase(QUEUE_TYPE, QUEUE_TYPE_TD)) {
			tdq = new TDQ();
			tdq.setName(props.getProperty(QUEUE));
		}

		if ((props.getProperty(QUEUE_SYSID) != null) && (!props.getProperty(QUEUE_SYSID).isEmpty())
				&& (props.getProperty(QUEUE_SYSID).length() <= 4)) {
			try {
				if (tsq != null) {
					tsq.setSysId(props.getProperty(QUEUE_SYSID));
				}

				if (tdq != null) {
					tdq.setSysId(props.getProperty(QUEUE_SYSID));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public boolean send() {

		if (props.getPropertyAttachment(QUEUE_CONTENT) != null) {
			// Content is binary
			byte[] queueContent = props.getPropertyAttachment(QUEUE_CONTENT);

			if (props.getProperty(QUEUE_CONTENT_LENGTH_MAX) != null) {
				int queueContentLengthMax = Integer.parseInt(props.getProperty(QUEUE_CONTENT_LENGTH_MAX, ""));

				if (queueContent.length > queueContentLengthMax) {
					// Truncate at maximum length
					queueContent = Arrays.copyOf(queueContent, queueContentLengthMax);
				}
			}

			int[][] chunks = Util.getChunks(queueContent, props.getProperty(QUEUE_CONTENT_LENGTH_CHUNK));

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

					if (tsq != null) {
						tsq.writeItemConditional(b);
					}

					if (tdq != null) {
						tdq.writeData(b);
					}

				} catch (Exception e) {
					logger.warning(Emit.messages.getString("QUEUE_LOG_FAIL_MESSAGE") + " " + getMessageSummary());

					e.printStackTrace();

					return false;
				}
			}

		} else {
			// Content is text
			String queueContent = props.getProperty(QUEUE_CONTENT);

			if (props.getProperty(QUEUE_CONTENT_LENGTH_MAX) != null) {
				int queueContentLengthMax = Integer.parseInt(props.getProperty(QUEUE_CONTENT_LENGTH_MAX, ""));

				if (queueContent.length() > queueContentLengthMax) {
					// Truncate at maximum length
					queueContent = queueContent.substring(0, queueContentLengthMax);
				}
			}

			int[][] chunks = Util.getChunks(queueContent, props.getProperty(QUEUE_CONTENT_LENGTH_CHUNK));

			String queueContentEncoding = props.getProperty(QUEUE_CONTENT_ENCODING);

			for (int[] chunk : chunks) {
				try {
					if (tsq != null) {
						if (queueContentEncoding == null) {
							tsq.writeItemConditional(queueContent.substring(chunk[0], chunk[1]).getBytes());

						} else {
							tsq.writeItemConditional(queueContent.substring(chunk[0], chunk[1]).getBytes(queueContentEncoding));
						}
					}

					if (tdq != null) {
						if (queueContentEncoding == null) {
							tdq.writeData(queueContent.substring(chunk[0], chunk[1]).getBytes());

						} else {
							tdq.writeData(queueContent.substring(chunk[0], chunk[1]).getBytes(queueContentEncoding));
						}
					}
					
				} catch (Exception e) {
					logger.warning(Emit.messages.getString("QUEUE_LOG_FAIL_MESSAGE") + " " + getMessageSummary());

					e.printStackTrace();

					return false;
				}
			}
		}

		if (logger.isLoggable(Level.INFO)) {
			logger.info(Emit.messages.getString("QUEUE_LOG_SUCCESS_MESSAGE") + " " + getMessageSummary());
		}

		return true;
	}
	
	/**
	 * Return true if we should attempt to emit 
	 * 
	 * @return true if the entries in EmitProperties are valid for emission. 
	 */
	public static boolean validForEmission(EmitProperties props) {
		return props.containsKey(CICSQueue.QUEUE_CONTENT);
	}

	/**
	 * Return a short summary of the contents to be written to the queue.
	 * 
	 * @return short summary of the contents to be written to the queue.
	 */
	private String getMessageSummary() {
		StringBuilder sb = new StringBuilder();

		sb.append(QUEUE).append(":").append(props.getProperty(QUEUE, ""));

		if (props.containsKey(QUEUE_SYSID))
			sb.append(",").append(QUEUE_SYSID).append(":").append(props.getProperty(QUEUE_SYSID, ""));

		sb.append(",").append(QUEUE_TYPE).append(":").append(props.getProperty(QUEUE_TYPE, ""));

		if (props.containsKey(QUEUE_TS_STORAGE))
			sb.append(",").append(QUEUE_TS_STORAGE).append(":").append(props.getProperty(QUEUE_TS_STORAGE, ""));

		if (props.containsKey(QUEUE_CONTENT_LENGTH_CHUNK))
			sb.append(",").append(QUEUE_CONTENT_LENGTH_CHUNK).append(":").append(props.getProperty(QUEUE_CONTENT_LENGTH_CHUNK, ""));

		if (props.containsKey(QUEUE_CONTENT_LENGTH_MAX))
			sb.append(",").append(QUEUE_CONTENT_LENGTH_MAX).append(":").append(props.getProperty(QUEUE_CONTENT_LENGTH_MAX, ""));

		if (props.containsKey(QUEUE_CONTENT_ENCODING))
			sb.append(",").append(QUEUE_CONTENT_ENCODING).append(":").append(props.getProperty(QUEUE_CONTENT_ENCODING, ""));

		return sb.toString();
	}
}