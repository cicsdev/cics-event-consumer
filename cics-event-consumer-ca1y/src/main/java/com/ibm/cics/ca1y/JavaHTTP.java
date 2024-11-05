/*
 * Licensed Materials - Property of IBM
 * 
 * CICS SupportPac CA1Y - CICS® TS support for sending emails
 * 
 * © Copyright IBM Corporation 2012 - 2017. All Rights Reserved.
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
 * @version 1.8.0
 * @since 2016-12-19
 */
public class JavaHTTP implements EmitAdapter {

	/**
	 * Copyright statement to be included in the compiled class.
	 */
	static final String COPYRIGHT = "Licensed Materials - Property of IBM. "
			+ "CICS SupportPac CA1Y (c) Copyright IBM Corporation 2012 - 2016. All Rights Reserved. "
			+ "US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corporation";

	/**
	 * Logging
	 */
	private static Logger logger = Logger.getLogger(JavaHTTP.class.getName());

	/**
	 * Property to specify the target URI for the HTTP request.
	 */
	private static final String JAVA_HTTP_URI = "java.http.uri";

	/**
	 * Property to specify the HTTP entity body.
	 */
	private static final String JAVA_HTTP_CONTENT = "java.http.content";

	/**
	 * The prefix for properties to specify HTTP headers to be added to the HTTP
	 * request. Any property with this prefix will be added, excluding this
	 * prefix, as an HTTP header.
	 */
	private static final String JAVA_HTTP_HEADER_PREFIX = "java.http.header.";

	/**
	 * The prefix for properties to be passed to the HTTP filter.
	 */
	private static final String JAVA_HTTP_FILTER_PREFIX = "java.http.filter.";

	/**
	 * The HTTP filter class to be registered when creating the HTTP request.
	 */
	private static final String JAVA_HTTP_FILTER_CLASS = "java.http.filterclass";

	/**
	 * Property to specify how many times to retry the HTTP request.
	 */
	private static final String JAVA_HTTP_RETRY = "java.http.retry";

	/**
	 * The number of times to retry the request if the java.http.retry property
	 * is not set.
	 */
	private static final int JAVA_HTTP_RETRY_DEFAULT = 0;

	/**
	 * Property to specify the time in milliseconds to delay before trying the
	 * HTTP request.
	 */
	private static final String JAVA_HTTP_RETRY_DELAY = "java.http.retrydelay";

	/**
	 * The default time in milliseconds to delay before trying the HTTP request
	 * if java.http.retrydelay is not specified.
	 */
	private static final int JAVA_HTTP_RETRY_DELAY_DEFAULT = 500;

	/**
	 * Property to specify the HTTP method for the request.
	 */
	private static final String JAVA_HTTP_METHOD = "java.http.method";

	/**
	 * The default HTTP method for the request if java.http.method not
	 * specified.
	 */

	private static final String JAVA_HTTP_METHOD_DEFAULT = "PUT";

	/**
	 * The default MIME type to use for the HTTP content.
	 */
	private static final String JAVA_HTTP_CONTENT_MIME_TEXT_DEFAULT = "text/plain";

	private EmitProperties props;

	public JavaHTTP(EmitProperties p) {
		props = p;
	}

	public boolean send() {
		if (props.getPropertyMime(JAVA_HTTP_CONTENT) == null) {
			props.setPropertyMime(JAVA_HTTP_CONTENT, JAVA_HTTP_CONTENT_MIME_TEXT_DEFAULT);
		}

		int retry = JAVA_HTTP_RETRY_DEFAULT;
		if (props.containsKey(JAVA_HTTP_RETRY)) {
			try {
				retry = Integer.parseUnsignedInt(props.getProperty(JAVA_HTTP_RETRY));

			} catch (NumberFormatException e) {
				// Ignore invalid values and use default
			}
		}

		// Prepare HTTP request
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(props.getProperty(JAVA_HTTP_URI));

		// Prepare HTTP filter
		if (props.getProperty(JAVA_HTTP_FILTER_CLASS) != null) {
			logger.fine(Emit.messages.getString("HTTPRegisteringFilterClass") + " - " + props.getProperty(JAVA_HTTP_FILTER_CLASS));

			try {
				target.register(Class.forName(props.getProperty(JAVA_HTTP_FILTER_CLASS)));

			} catch (ClassNotFoundException e) {
				logger.warning(Emit.messages.getString("HTTPFilterClassNotFound") + " - " + props.getProperty(JAVA_HTTP_FILTER_CLASS));
			}
		}

		// Add all properties that start with the prefix as HTTP headers to the
		// HTTP request
		Builder builder = target.request(props.getPropertyMime(JAVA_HTTP_CONTENT));
		Enumeration<?> list = props.propertyNames();
		while (list.hasMoreElements()) {
			String key = (String) list.nextElement();

			if (key.startsWith(JAVA_HTTP_HEADER_PREFIX)) {
				// Set the HTTP header
				builder.header(key.substring(JAVA_HTTP_HEADER_PREFIX.length()), props.getProperty(key));

			} else if (key.startsWith(JAVA_HTTP_FILTER_PREFIX)) {
				// Set the HTTP request property for use by the HTTP filter
				builder.property(key, props.getProperty(key));
			}
		}

		// Prepare the HTTP content in an entity
		Entity<String> entity = Entity.entity(props.getProperty(JAVA_HTTP_CONTENT), props.getPropertyMime(JAVA_HTTP_CONTENT));

		// Send HTTP request
		long retryDelay;
		Response response = null;

		for (; retry >= 0; retry--) {

			// Send the HTTP request
			response = builder.method(props.getProperty(JAVA_HTTP_METHOD, JAVA_HTTP_METHOD_DEFAULT), entity);

			if ((response.getStatus() == Response.Status.OK.getStatusCode()) || (response.getStatus() == Response.Status.CREATED.getStatusCode())
					|| (response.getStatus() == Response.Status.ACCEPTED.getStatusCode())) {
				// HTTP request was processed successfully

				// Get the HTTP response body
				String responseHttpBody = "";
				if (response.getEntity() != null) {
					try {
						responseHttpBody = IOUtils.toString((InputStream) response.getEntity(), StandardCharsets.UTF_8);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				// Log the HTTP request was successful
				if (logger.isLoggable(Level.INFO)) {
					logger.info(Emit.messages.getString("HTTPLogSuccess") + " - " + getMessageSummary() + ",HTTP response status:" + response.getStatus()
							+ ",HTTP response body:" + responseHttpBody);
				}

				return true;
			}

			if (retry > 0) {
				// Delay for the specified period
				retryDelay = JAVA_HTTP_RETRY_DELAY_DEFAULT;

				if (props.containsKey(JAVA_HTTP_RETRY_DELAY)) {
					try {
						retryDelay = Integer.parseUnsignedInt(props.getProperty(JAVA_HTTP_RETRY_DELAY));

					} catch (NumberFormatException e) {
						// Ignore invalid values and use default
					}
				}

				if (logger.isLoggable(Level.FINE)) {
					logger.fine(Emit.messages.getString("HTTP_RETRYING") + " " + retryDelay + "ms");
				}

				try {
					Thread.sleep(retryDelay);

				} catch (InterruptedException e) {
					// Thread cancelled so do not retry
					break;
				}
			}
		}

		if (logger.isLoggable(Level.INFO)) {
			logger.info(Emit.messages.getString("HTTPLogFail") + " - " + getMessageSummary() + ",HTTP response status:" + response.getStatus());
		}

		return false;
	}

	/**
	 * Return true if we should attempt to emit using this class.
	 * 
	 * @return boolean
	 */
	public static boolean validForEmission(EmitProperties props) {
		return (props.containsKey(JavaHTTP.JAVA_HTTP_CONTENT) && props.containsKey(JavaHTTP.JAVA_HTTP_URI));
	}

	/**
	 * Return a short summary of the contents to be sent to the HTTP server.
	 * 
	 * @return String
	 */
	private String getMessageSummary() {
		StringBuilder sb = new StringBuilder();

		sb.append(JAVA_HTTP_METHOD).append(":").append(props.getProperty(JAVA_HTTP_METHOD, JAVA_HTTP_METHOD_DEFAULT));

		if (props.containsKey(JAVA_HTTP_URI)) {
			sb.append(",").append(JAVA_HTTP_URI).append(":").append(props.getProperty(JAVA_HTTP_URI));
		}

		if (props.getPropertyMime(JAVA_HTTP_CONTENT) != null) {
			sb.append(",").append("MIME:").append(props.getPropertyMime(JAVA_HTTP_CONTENT));
		}

		return sb.toString();
	}
}