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

import java.io.StringReader;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.io.IOUtils;

import com.ibm.cics.server.IsCICS;
import com.ibm.jzos.RcException;

/**
 * Class for utility methods.
 * 
 * @author Mark Cocker <mark_cocker@uk.ibm.com>
 * @version 1.1
 * @since 2012-07-19
 */
public class Util {

	/**
	 * Copyright statement to be included in the compiled class.
	 */
	static final String COPYRIGHT = "Licensed Materials - Property of IBM. "
			+ "CICS SupportPac CA1Y (c) Copyright IBM Corporation 2012 - 2016. All Rights Reserved. "
			+ "US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corporation";
	
	/**
	 * Return true if running in CICS and CICS API is allowed, false otherwise.
	 * 
	 * @return boolean - true if running in CICS and CICS API is allowed, false otherwise
	 */
	public static boolean isCICS() {
		try {
			if (IsCICS.getApiStatus() == IsCICS.CICS_REGION_AND_API_ALLOWED) {
				return true;
			}
				
		} catch (Exception e) {
			// JCICS not loaded
		}

		return false;
	}
	
	/**
	 * Return a Date object based on the supplied CICS ABSTIME. If the CICS ABSTIME is invalid, return the current Java date.
	 * 
	 * @param s     - A string representing CICS ABSTIME 
	 * @return Date - A new Date object based on the supplied CICS ABSTIME
	 */
	public static Date getDateFromAbstime(String s) {
		
		try {
			/* Subtract the difference between the number of milliseconds between January 1, 1900, 00:00:00 GMT used by CICS ASKTIME ABSTIME() and
			January 1, 1970, 00:00:00 GMT used by Java http://docs.oracle.com/javase/7/docs/api/java/util/Date.html#Date() */
			long milliseconds = Long.parseLong(s) - 2208985200000L;
			
			if (milliseconds > 0) {
				return new Date(milliseconds);
			}
			
		} catch (Exception e) {
		}

		return new Date();
	}
		
	/**
	 * Return an 2 dimensional array. The first ordinal is the number of chunks
	 * the supplied string has, based on the supplied chunk size. The second
	 * ordinal is the start and ending indexes into the string for the chunk.
	 * 
	 * @param s
	 *            - String to break into chunks
	 * @param chunksize
	 *            - The maximum size of each chunk.
	 * @return int[][] - [n] is the number of chunks. [n][0] is the starting
	 *         index of the chunk. [n][1] is the ending index of the chunk.
	 */
	public static int[][] getChunks(String s, String chunkSize) {
		if ((s == null) || (s.length() == 0))
			return new int[][] { { 0, 0 } };

		if (chunkSize == null) {
			// Optimise case when chunkSize is not set - as this is the primary
			// use case
			return new int[][] { { 0, s.length() } };
		}

		int chunkSizeInt = 0;
		int chunks = 1;

		try {
			chunkSizeInt = Integer.parseInt(chunkSize);
		} catch (Exception e) {
		}

		if (chunkSizeInt > 1) {
			chunks = (s.length() + chunkSizeInt - 1) / chunkSizeInt;
		}

		int[][] chunksArray = new int[chunks][2];
		for (int i = 0; i < chunksArray.length; i++) {
			chunksArray[i][0] = i * chunkSizeInt;

			if ((i + 1) == chunksArray.length) {
				chunksArray[i][1] = s.length();

			} else {
				chunksArray[i][1] = (i + 1) * chunkSizeInt;
			}
		}

		return chunksArray;
	}

	/**
	 * Return an 2 dimensional array. The first ordinal is the number of chunks
	 * the supplied byte array has, based on the supplied chunk size. The second
	 * ordinal is the start and ending indexes into the byte array for the
	 * chunk.
	 * 
	 * @param b
	 *            - byte array to break into chunks
	 * @param chunksize
	 *            - The maximum size of each chunk.
	 * @return int[][] - [n] is the number of chunks. [n][0] is the starting
	 *         index of the chunk. [n][1] is the ending index of the chunk.
	 */
	public static int[][] getChunks(byte[] b, String chunkSize) {
		if ((b == null) || (b.length == 0))
			return new int[][] { { 0, 0 } };

		if (chunkSize == null) {
			// Optimise case when chunkSize is not set - as this is the primary
			// use case
			return new int[][] { { 0, b.length } };
		}

		int chunkSizeInt = 0;
		int chunks = 1;

		try {
			chunkSizeInt = Integer.parseInt(chunkSize);
		} catch (Exception e) {
		}

		if (chunkSizeInt > 1) {
			chunks = (b.length + chunkSizeInt - 1) / chunkSizeInt;
		}

		int[][] chunksArray = new int[chunks][2];
		for (int i = 0; i < chunksArray.length; i++) {
			chunksArray[i][0] = i * chunkSizeInt;

			if ((i + 1) == chunksArray.length) {
				chunksArray[i][1] = b.length;

			} else {
				chunksArray[i][1] = (i + 1) * chunkSizeInt;
			}
		}

		return chunksArray;
	}

	/**
	 * Return a string of hexadecimal that represents the byte array. If
	 * byteArray is null an empty string is returned.
	 * 
	 * @param byteArray
	 *            - byte array to use.
	 * @return String of hexadecimal.
	 */
	public static String getHex(byte[] byteArray) {
		// Return a hex string that represents the byte array byteArray
		if ((byteArray == null) || (byteArray.length == 0)) {
			return "";
		}

		return "0x" + DatatypeConverter.printHexBinary(byteArray);
	}

	/**
	 * Return a string of hexadecimal of byteArray. If the length of byteArray
	 * is greater than 16, return the first 8 bytes + "..." + the last 8 bytes.
	 * 
	 * @param byteArray
	 *            - byte array to use.
	 * @return String of hexadecimal.
	 */
	public static String getHexSummary(byte[] byteArray) {
		if (byteArray == null) {
			return "";
		}

		if (byteArray.length <= 16) {
			return getHex(byteArray);
		}

		// H
		return getHex(Arrays.copyOfRange(byteArray, 0, 8)) + "..."
				+ getHex(Arrays.copyOfRange(byteArray, byteArray.length - 8, byteArray.length));
	}
	
	/**
	 * Return a string of hexadecimal of byteArray with line separators suitable for trace.
	 * 
	 * @param byteArray
	 *        	- byte array to output
	 * @param bytesPerLine
	 * 			- number of bytes to output per line
	 * @param lineSeperator
	 * 			- string to append at the end of each line
	 * @return String of hexadecimal.
	 */
	public static String getHexDump(byte[] byteArray, int bytesPerLine, String lineSeperator) {
		StringBuilder sb = new StringBuilder();

		String format = null;
		if (byteArray.length > bytesPerLine) {
			format = "%0" + (String.format("%X", byteArray.length)).length() + "X";
		}
    	
		for (int i = 0; i < byteArray.length; i += bytesPerLine) {
			
			if (format != null) {
				sb.append("|").append(String.format(format, i)).append("|");
			}
				
			for (int j = 0; (j < bytesPerLine) && ((i+j) < byteArray.length); j++) {
				
				if (j  == 0) { }
				else if (j % 16 == 0) {
					sb.append("  ");
				} else if (j % 4 == 0) {
					sb.append(" ");
				}
				
				sb.append(String.format("%02X", byteArray[i+j]));
			}
			
			sb.append(lineSeperator);
		}
		
		return sb.toString();
	}


	/*
	 * Add name / value properties from the byte array.
	 * 
	 * @param props - The properties to add to.
	 * 
	 * @param s - The string to load the properties. If the first character is
	 * "<" the string is assumed to be a valid XML document as defined by the
	 * Properties loadFromXML method. Otherwise the string is assumed to be a
	 * UTF-8 string of name=value pairs as defined by the Properties load
	 * method.
	 * 
	 * @return true if properties added successfully, false otherwise.
	 */
	public static boolean loadProperties(Properties props, String s) {
		if ((s == null) || (s.length() == 0)) {
			return false;
		}
		
		try {
			if (s.startsWith("<")) {
				// Assume the configuration is in XML format
				props.loadFromXML(IOUtils.toInputStream(s, "UTF-8"));
			} else {
				// Assume the configuration is in Java Properties format of name=value pairs
				props.load(new StringReader(s));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	/**
	 * Return a string padded with spaces.
	 * 
	 * @param s
	 *            - the string to pad
	 * @param length
	 *            - length of padded string to return
	 * @return padded string
	 */
	public static String rightPad(String s, int length) {
		return String.format("%1$-" + length + "s", s);
	}

	/**
	 * Resolve z/OS system symbols.
	 * 
	 * @param symbols
	 *            - string with embedded symbols
	 * @return string with symbols resolved
	 */
	public static String getSystemSymbol(String symbols) throws RcException {
		return com.ibm.jzos.ZUtil.substituteSystemSymbols(symbols, false);
	}

	/**
	 * Extract the command options from a string into an array.
	 * 
	 * @param commandLine
	 *            - String in the form of
	 *            "command=value-0:option1=value-1:optionN=value-N" where the
	 *            options are in the order specified in validOptions.
	 * @param validOptions
	 *            - String array of all valid options, where entries [0] =
	 *            "command" [1] = ":option1=" ... [n] = ":optionN="
	 * @return String array where [0] = "value-0" [1] = "value-1" ... [n] =
	 *         "value-N"
	 */
	public static String[] getOptions(String commandLine, String[] validOptions) {
		// remove the command name
		commandLine = commandLine.substring(validOptions[0].length());
	
		String[] commandOptions = new String[validOptions.length];
	
		for (int i = validOptions.length - 1; i > 0; i--) {
			int pos = commandLine.lastIndexOf(validOptions[i]);
	
			if (pos >= 0) {
				commandOptions[i] = commandLine.substring(pos + validOptions[i].length()).trim();
				
				// remove option
				commandLine = commandLine.substring(0, pos);
			}
		}
	
		commandOptions[0] = commandLine.trim();
	
		return commandOptions;
	}
}