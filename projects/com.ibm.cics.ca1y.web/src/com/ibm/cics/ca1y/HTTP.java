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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
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
	 * The prefix for properties to specify headers to be added to the HTTP request.
	 */
	private static final String HTTP_HEADER_PREFIX= "java.http.header.";

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
		Builder builder = target.request(props.getPropertyMime(HTTP_CONTENT));
		
		// Add all properties that start with the prefix as HTTP headers to the HTTP request
		Enumeration<?> list = props.propertyNames();
		while (list.hasMoreElements()) {
			String key = (String) list.nextElement();
			
			if (key.startsWith(HTTP_HEADER_PREFIX)) {
				builder.header(key.substring(HTTP_HEADER_PREFIX.length()), props.getProperty(key));
			}
		}

		// Prepare the HTTP content in an entity
		Entity<String> entity = Entity.entity(props.getProperty(HTTP_CONTENT), props.getPropertyMime(HTTP_CONTENT));
		
		// Make the HTTP call
		Response response = builder.method(props.getProperty(HTTP_METHOD, HTTP_METHOD_DEFAULT), entity);
		
		// Get the HTTP response body
		String responseHttpBody = "";
		try {
			responseHttpBody = IOUtils.toString((InputStream) response.getEntity(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// HTTP request status suggests it was processed successfully
		if ((response.getStatus() == 200) || (response.getStatus() == 201) || (response.getStatus() == 202)) { 
			
			if (logger.isLoggable(Level.INFO)) {
				logger.info(Emit.messages.getString("HTTPLogSuccess") + " - " + getMessageSummary() + ",HTTP response status:" + response.getStatus() + ",HTTP response body:" + responseHttpBody);
			}

			return true;
		}

		if (logger.isLoggable(Level.INFO)) {
			logger.info(Emit.messages.getString("HTTPLogFail") + " - " + getMessageSummary() + ",HTTP response status:" + response.getStatus() + ",HTTP response body:" + responseHttpBody);
		}

		// HTTP request status suggests it failed to be processed
		return false;
	}

	/**
	 * Return true if we should attempt to emit using this class.
	 * 
	 * @return boolean 
	 */
	public static boolean validForEmission(EmitProperties props) {
		return (props.containsKey(HTTP.HTTP_CONTENT) && props.containsKey(HTTP.HTTP_URI));
	}
	
	/**
	 * Return a short summary of the contents to be sent to the HTTP server.
	 * 
	 * @return String
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