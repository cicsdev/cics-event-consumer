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

package com.ibm.cics.ca1y.web;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;

import com.ibm.cics.ca1y.Emit;

/**
 * Log REST request
 * 
 * @author Mark Cocker <mark_cocker@uk.ibm.com>
 * @version 1.7.1
 * @since 2016-12-21
 */
@Path("/log")
public class Log {
	/**
	 * Java subsystem logger
	 */
	private static Logger logger = Logger.getLogger(Emit.class.getName());
	
	/*
	 * Resource bundle for messages based on JVM locale.
	 */
	public static ResourceBundle messages = ResourceBundle.getBundle("com/ibm/cics/ca1y/web/messages");

	@PUT
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces(MediaType.APPLICATION_JSON)
	public Response createEvent(InputStream httpBody, @Context HttpHeaders httpHeaders) {
		
		String json = "";
		try {
			json = IOUtils.toString(httpBody, StandardCharsets.UTF_8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Log HTTP headers and body
		if (logger.isLoggable(Level.FINE)) {
			String httpHeadersList = "";
			
			for(String httpHeader : httpHeaders.getRequestHeaders().keySet()){
				httpHeadersList += httpHeader + "=" + httpHeaders.getRequestHeader(httpHeader) + ",";
			}
			
			logger.fine(messages.getString("EventReceived") + ": HTTP headers: " + httpHeadersList + "HTTP body:" + json );
		}
		
		return Response.status(Response.Status.OK).build();
	}
}