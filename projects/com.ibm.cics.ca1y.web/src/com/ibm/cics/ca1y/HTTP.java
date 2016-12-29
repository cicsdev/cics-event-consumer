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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.logging.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;

/**
 * Class to emit a message to an HTTP server.
 * 
 * @author Mark Cocker <mark_cocker@uk.ibm.com>
 * @version 1.7.1
 * @since 2016-12-19
 */
public class HTTP implements EmitAdapter {

	/**
	 * Copyright statement to be included in the compiled class.
	 */
	static final String COPYRIGHT = "Licensed Materials - Property of IBM. "
			+ "CICS SupportPac CA1Y (c) Copyright IBM Corporation 2012 - 2016. All Rights Reserved. "
			+ "US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corporation";

	/**
	 * Logging
	 */
	private static Logger logger = Logger.getLogger(HTTP.class.getName());

	/**
	 * Property to specify the HTTP path.
	 */
	private static final String HTTP_URI = "java.http.uri";

	/**
	 * Property to specify the HTTP entity body content.
	 */
	private static final String HTTP_CONTENT = "java.http.content";

	/**
	 * Property to specify the HTTP method.
	 */
	private static final String HTTP_METHOD = "java.http.method";
	private static final String HTTP_METHOD_DEFAULT = "PUT";

	/**
	 * The default MIME type to use for the HTTP content.
	 */
	private static final String HTTP_CONTENT_MIME_TEXT_DEFAULT = "text/plain";

	private EmitProperties props;

	public HTTP(EmitProperties p) {
		props = p;
	}

	public boolean send() {
		if (props.getPropertyMime(HTTP_CONTENT) == null) {
			props.setPropertyMime(HTTP_CONTENT, HTTP_CONTENT_MIME_TEXT_DEFAULT);
		}

		// Prepare HTTP request
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(props.getProperty(HTTP_URI));
		target.request(props.getPropertyMime(HTTP_CONTENT));
		Invocation.Builder builder = target.request();
		
		// Add all properties that start with the prefix as HTTP headers to the request
		String prefix = "java.http.header.";

		Enumeration<?> e = props.propertyNames();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			
			if (key.startsWith(prefix)) {
				builder.header(key.substring(prefix.length()), props.getProperty(key));
			}
		}

		Entity<String> entity = Entity.entity(props.getProperty(HTTP_CONTENT), props.getPropertyMime(HTTP_CONTENT));
		
		Response response = builder.method(props.getProperty(HTTP_METHOD, HTTP_METHOD_DEFAULT), entity);
		
		String httpBody = "";
		
		try {
			httpBody = IOUtils.toString((InputStream) response.getEntity(), StandardCharsets.UTF_8);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		logger.info(getMessageSummary() + ",HTTP response status:" + response.getStatus() + ",HTTP response body:" + httpBody);

		// HTTP request status suggests it was processed successfully
		if ((response.getStatus() == 200) || (response.getStatus() == 201) || (response.getStatus() == 202)) { 
			return true;
		}
		
		// HTTP request status suggests it failed to be processed
		return false;
	}

	/**
	 * Return true if we should attempt to emit 
	 * 
	 * @return true if the entries in EmitProperties are valid for emission. 
	 */
	public static boolean validForEmission(EmitProperties props) {
		return props.containsKey(HTTP.HTTP_CONTENT);
	}
	
	/**
	 * Return a short summary of the contents to be written to the queue.
	 * 
	 * @return short summary of the contents to be written to the queue.
	 */
	private String getMessageSummary() {
		StringBuilder sb = new StringBuilder();

		sb.append(HTTP_METHOD).append(":").append(props.getProperty(HTTP_METHOD, HTTP_METHOD_DEFAULT));
		
		if (props.containsKey(HTTP_URI)) {
			sb.append(",").append(HTTP_URI).append(":").append(props.getProperty(HTTP_URI));
		}

		if (props.getPropertyMime(HTTP_CONTENT) != null) {
			sb.append(",").append("MIME:").append(props.getPropertyMime(HTTP_CONTENT));
		}

		return sb.toString();
	}
}