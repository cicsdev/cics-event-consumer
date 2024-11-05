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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.sax.SAXResult;

import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;

/**
 * Class to use the Apache FOP project to convert streams.
 * 
 * @author Mark Cocker <mark_cocker@uk.ibm.com>
 * @version 1.1
 * @since 2013-01-20
 */
public class ConvertFOP {

	/**
	 * Copyright statement to be included in the compiled class.
	 */
	static final String COPYRIGHT = "Licensed Materials - Property of IBM. "
			+ "CICS SupportPac CA1Y (c) Copyright IBM Corporation 2012 - 2016. All Rights Reserved. "
			+ "US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corporation";

	// Lazily initialise this instance to avoid calling Apache FOP during class load as it may not be available.
	private static FopFactory fopFactory;
	
	/**
	 * Apache FOP configuration file is documented at
	 * http://xmlgraphics.apache.org/fop/1.1/configuration.html
	 */
	public static final String FOP_CONFIG_FILE = "fop.config.file";
	
	final static List<String> fopFromMimeTypes = Arrays.asList("text/xml", "application/xml", MimeConstants.MIME_XSL_FO);
	final static List<String> fopToMimeTypes = Arrays.asList(
			MimeConstants.MIME_PDF,
			MimeConstants.MIME_RTF, MimeConstants.MIME_RTF_ALT1, MimeConstants.MIME_RTF_ALT2,
			MimeConstants.MIME_PLAIN_TEXT,
			MimeConstants.MIME_TIFF,
			MimeConstants.MIME_JPEG,
			MimeConstants.MIME_SVG,
			MimeConstants.MIME_PNG,
			MimeConstants.MIME_GIF);
	
	/**
	 * Convert an XML or FO input stream to an output stream based on the specified xslt and target MIME type.
	 */
	public static int convertFOP(InputStream inStream, OutputStream outStream, InputStream xsltStream, String mime, Properties props) {
		if ((inStream == null) || (outStream == null)) {
			return 0;
		}
		
		int pageCount = 0;

		try {
			FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
			
			fopFactory = FopFactory.newInstance(new File(props.getProperty(FOP_CONFIG_FILE)));
			
			// Construct fop with desired output format
			Fop fop = fopFactory.newFop(mime, foUserAgent, outStream);
			
			try {
				
				TransformerFactory factory = TransformerFactory.newInstance();
				Transformer transformer;
				
				if (xsltStream == null) {
					transformer = factory.newTransformer();
					
				} else {
					Source xsltSource = new StreamSource(xsltStream);
					transformer = factory.newTransformer(xsltSource);
				}
				
				// Setup input stream
				Source src = new StreamSource(inStream);
				
				// Resulting SAX events (the generated FO) must be piped through to
				// FOP
				Result res = new SAXResult(fop.getDefaultHandler());
				
				// Start XSLT transformation and FOP processing
				transformer.transform(src, res);
				
				// Result processing
				pageCount = fop.getResults().getPageCount();
					
			} catch (Exception e) {
				e.printStackTrace(System.err);
			}

		} catch (Exception e) {
			e.printStackTrace(System.err);
			
		} finally {
			try {
				outStream.close();
				
			} catch (Exception e2) {
				// Ignore
			}
		}

		return pageCount;
	}

	/**
	 * Minimal test harness to convert a FO file to a PDF using the Java
	 * command line.
	 * 
	 * @param args
	 *            [0] FO file to convert args[1] PDF file to create
	 */
	public static void main(String[] args) {
		String inFile = "~/fop/fop-1.1/examples/fo/basic/simple.fo";
		String outFile = "~/fop/fop-1.1/examples/fo/basic/simple.pdf";
		String xsltFile = null;
		String configFile = null;
		String mime = MimeConstants.MIME_PDF;

		if (args.length > 0) {
			inFile = args[0];
		}

		if (args.length > 1) {
			outFile = args[1];
		}
		
		if (args.length > 2) {
			xsltFile = args[2];
		}

		if (args.length > 3) {
			mime = args[3];
		}

		if (args.length > 4) {
			configFile = args[4];
		}

		try {
			System.out.println("Preparing...");

			// Setup input and output files
			File fofile = new File(inFile);
			File pdffile = new File(outFile);
			File xsltfile = null;
			
			if (xsltFile != null) {
				xsltfile = new File(xsltFile);
			}

			System.out.println("Input XSL-FO: " + fofile);
			System.out.println("Output PDF..: " + pdffile);
			System.out.println("XSLT........: " + xsltfile);
			System.out.println("MIME........: " + mime);
			System.out.println("Config file.: " + configFile);
			System.out.println();
			System.out.println("Transforming...");

			// Setup output stream. Note: Using BufferedOutputStream
			// for performance reasons (helpful with FileOutputStreams).
			InputStream inStream = new FileInputStream(fofile);
			OutputStream outStream = new BufferedOutputStream(new FileOutputStream(pdffile));
			InputStream xsltStream = null;

			if (xsltFile != null) {
				xsltStream = new FileInputStream(xsltfile);
			}
			
			System.getProperties().setProperty(FOP_CONFIG_FILE, configFile);

			ConvertFOP.convertFOP(inStream, outStream, xsltStream, mime, System.getProperties());

			System.out.println("Succeeded");
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}
}