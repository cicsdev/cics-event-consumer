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

import com.ibm.cics.server.*;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class to emit a message to a file or a CICS KSDS, ESDS, or RRDS file.
 * 
 * @author Mark Cocker <mark_cocker@uk.ibm.com>
 * @version 1.1
 * @since 2012-10-14
 */
public class CICSFile {

	/**
	 * Copyright statement to be included in the compiled class.
	 */
	static final String COPYRIGHT = "Licensed Materials - Property of IBM. "
			+ "CICS SupportPac CA1Y (c) Copyright IBM Corporation 2012 - 2014. All Rights Reserved. "
			+ "US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corporation";

	/**
	 * Logging
	 */
	private static Logger logger = Logger.getLogger(CICSFile.class.getName());

	/**
	 * Property to specify the file name. For a zFS file, following zFS
	 * directory and file naming rules. For a CICS file, follow the CICS naming
	 * rules.
	 */
	public static final String FILE = "file";

	/**
	 * Property to specify the CICS file type. The value should be ksds, esds,
	 * or rrds.
	 */
	public static final String FILE_TYPE = "file.type";
	public static final String FILE_TYPE_KSDS = "ksds";
	public static final String FILE_TYPE_ESDS = "esds";
	public static final String FILE_TYPE_RRDS = "rrds";

	/**
	 * Property to specify the content to be written to the file.
	 */
	public static final String FILE_CONTENT = "file.content";

	/**
	 * Property to specify the maximum length of content to be written to the
	 * file. Content over this length will be truncated.
	 */
	// public static final String FILE_CONTENT_LENGTH_MAX =
	// "file.content.length.max";

	/**
	 * Property to specify the size of records the content should be broken up
	 * into. Each chunk will be written as a separate record. Note other
	 * concurrent tasks may also write records that become interspersed with the
	 * chunks written here.
	 */
	// public static final String FILE_CONTENT_LENGTH_CHUNK =
	// "file.content.length.chunk";

	/**
	 * Property to specify content should be written with the specified
	 * encoding.
	 */
	public static final String FILE_CONTENT_ENCODING = "file.content.encoding";

	/**
	 * Property to specify the file key.
	 */
	public static final String FILE_KEY = "file.key";

	/**
	 * Property to specify the system identifier to route the request to. Follow
	 * the character and size rules defined by CICS.
	 */
	public static final String FILE_SYSID = "file.sysid";

	public static final String FILE_ONLYWHEN = "file.onlywhen";
	public static final String FILE_ONLYWHEN_HTTPFAILED = "httpfailed";

	private EmitProperties props;
	private KSDS ksds;
	private ESDS esds;
	private RRDS rrds;

	public CICSFile(EmitProperties p) {
		props = p;

		try {
			if (props.isPropertyEqualsIgnoreCase(FILE_TYPE, FILE_TYPE_ESDS)) {
				esds = new ESDS();
				esds.setName(props.getProperty(FILE));

				if (props.containsKey(FILE_SYSID)) {
					esds.setSysId(props.getProperty(FILE_SYSID));
				}

			} else if (props.isPropertyEqualsIgnoreCase(FILE_TYPE, FILE_TYPE_RRDS)) {
				rrds = new RRDS();
				rrds.setName(props.getProperty(FILE));

				if (props.containsKey(FILE_SYSID)) {
					rrds.setSysId(props.getProperty(FILE_SYSID));
				}
				
			} else {
				ksds = new KSDS();
				ksds.setName(props.getProperty(FILE));

				if (props.containsKey(FILE_SYSID)) {
					ksds.setSysId(props.getProperty(FILE_SYSID));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean send() {
		byte[] fileContent = null;
		byte[] fileKey = null;

		if (props.containsKey(FILE_CONTENT_ENCODING)) {
			try {
				fileContent = props.getProperty(FILE_CONTENT).getBytes(props.getProperty(FILE_CONTENT_ENCODING));
				
				if (props.containsKey(FILE_KEY)) {
					fileKey = props.getProperty(FILE_KEY).getBytes(props.getProperty(FILE_CONTENT_ENCODING));
				}
				
			} catch (Exception e) {
				// TODO: handle exception
			}
			
		} else {
			fileContent = props.getProperty(FILE_CONTENT).getBytes();

			if (props.containsKey(FILE_KEY)) {
				fileKey = props.getProperty(FILE_KEY).getBytes();
			}
		}

		try {
			if (ksds != null) {
				ksds.write(fileKey, fileContent);
			}

			if (esds != null) {
				if (fileKey == null) {
					esds.write(fileContent);

				} else {
					esds.write(fileKey, fileContent);
				}
			}

			if (rrds != null) {
				rrds.write(Long.parseLong(props.getProperty(FILE_KEY)), fileContent);
			}

		} catch (Exception e) {
			logger.warning(Emit.messages.getString("FILE_LOG_FAIL_MESSAGE") + " " + getMessageSummary());
			
			e.printStackTrace();
			
			return false;
		}

		if (logger.isLoggable(Level.INFO)) {
			logger.warning(Emit.messages.getString("FILE_LOG_SUCCESS_MESSAGE") + " " + getMessageSummary());
		}

		return true;
	}

	/**
	 * Return a short summary of the contents to be written to the file.
	 * 
	 * @return short summary of the contents to be written to the file.
	 */
	private String getMessageSummary() {
		StringBuilder sb = new StringBuilder();

		sb.append(FILE).append(":").append(props.getProperty(FILE, ""));
		sb.append(",").append(FILE_TYPE).append(":").append(props.getProperty(FILE_TYPE, ""));
		sb.append(",").append(FILE_KEY).append(":").append(props.getProperty(FILE_KEY, ""));
		sb.append(",").append(FILE_SYSID).append(":").append(props.getProperty(FILE_SYSID, ""));
		sb.append(",").append(FILE_CONTENT_ENCODING).append(":").append(props.getProperty(FILE_CONTENT_ENCODING, ""));
		sb.append(",").append(FILE_CONTENT).append(":").append(props.getProperty(FILE_CONTENT, ""));

		return sb.toString();
	}
}