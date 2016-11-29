/*
 * Licensed Materials - Property of IBM
 * 
 * CICS SupportPac CA1Y - CICS® TS support for sending emails
 * 
 * © Copyright IBM Corporation 2013 - 2016. All Rights Reserved.
 * 
 *  US Government Users Restricted Rights - Use, duplication, or disclosure
 *  restricted by GSA ADP Schedule Contract with IBM Corporation.
 */

package com.ibm.cics.ca1y;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * Class to use the Apache FOP project to convert streams.
 * 
 * @author Mark Cocker <mark_cocker@uk.ibm.com>
 * @version 1.1
 * @since 2013-01-20
 */
public class ConvertXML {

	/**
	 * Copyright statement to be included in the compiled class.
	 */
	static final String COPYRIGHT = "Licensed Materials - Property of IBM. "
			+ "CICS SupportPac CA1Y (c) Copyright IBM Corporation 2012 - 2015. All Rights Reserved. "
			+ "US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corporation";

	/**
	 * List of MIME types that can be transformed from and to using a XSLT stylesheet.
	 */
	final static List<String> xsltFromMimeTypes = Arrays.asList("text/xml", "application/xml");
	final static List<String> xsltToMimeTypes = Arrays.asList("text/xml", "text/html");
	
	final static String TRANSFORMER_FACTORY = "javax.xml.transform.TransformerFactory";
	
	/**
	 * Convert an XML input stream to an output stream based on an XSLT.
	 * 
	 */
	//public static boolean convertXSLT(InputStream inStream, InputStream xsltStream, OutputStream outStream, Properties props) {
	public static String convertXSLT(String xml, String xslt, Properties props) {
		if ((xml == null) || (xslt == null) || (props == null)) {
			return null;
		}
		
		StringWriter result = new StringWriter();
		TransformerFactory factory = TransformerFactory.newInstance();
		
		try {
			factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer(new StreamSource(new StringReader(xslt)));
			transformer.transform( new StreamSource(new StringReader(xml)), new StreamResult(result)); 
					
			} catch (Exception e) {
				e.printStackTrace(System.err);
		}
		
		return result.toString();
	}
}