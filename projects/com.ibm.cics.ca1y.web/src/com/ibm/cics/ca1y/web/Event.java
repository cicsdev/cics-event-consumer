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

package com.ibm.cics.ca1y.web;

import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.cics.ca1y.Emit;
import com.ibm.cics.ca1y.EmitProperties;

/**
 * Consumes CICS Common base event REST format
 * 
 * For details of this format see the CICS Knowledge Center
 * http://www.ibm.com/support
 * /knowledgecenter/SSGMCP_5.3.0/com.ibm.cics.ts.eventprocessing
 * .doc/reference/dfhep_event_processing_cberformat.html
 * 
 * @author Mark Cocker <mark_cocker@uk.ibm.com>
 * @version 1.8.0
 * @since 2016-11-28
 */
@Path("/cberest")
public class Event {
	/**
	 * Java subsystem logger
	 */
	private static Logger logger = Logger.getLogger(Emit.class.getName());
	
	/*
	 * Resource bundle for messages based on JVM locale.
	 */
	public static ResourceBundle messages = ResourceBundle.getBundle("com/ibm/cics/ca1y/web/messages");

	@POST
	@Consumes({ MediaType.APPLICATION_XML, MediaType.TEXT_XML })
	@Produces(MediaType.TEXT_PLAIN)
	public Response createEvent(String eventXML, @Context UriInfo info) {
		
		if (logger.isLoggable(Level.FINE)) {
			logger.fine(messages.getString("EventReceived") + ": " + eventXML);
		}
		
		if (eventXML == null) {
			// Return error response to client as the event data is missing
			return Response.status(Response.Status.NO_CONTENT).build();
		}
		
		// Whether event was successfully processed
		boolean emitEvent = false;

		// Properties for event extracted from received XML
		EmitProperties props = new EmitProperties();
		
		// JAXB generated class is used to hold the parsed XML data
		com.ibm.xmlns.prod.cics.events.cbe.Event event = null;

		try {
			/*
			 * The com.ibm.xmlns.prod.cics.events.cbe.Event.class was generated from the JDK provided JAXB command line utility xjc
			 * as follows:
			 * 
			 * ./xjc -target 2.1 -d ~/Desktop/src ~/CICSExplorerSDK.data/com.ibm.cics.ca1y.web/src/com/ibm/cics/ca1y/web/cics_cbe_static.xsd
			 */
			JAXBContext ctx = JAXBContext.newInstance(com.ibm.xmlns.prod.cics.events.cbe.Event.class);
			Unmarshaller jaxbUnmarshaller = ctx.createUnmarshaller();
			event = (com.ibm.xmlns.prod.cics.events.cbe.Event) jaxbUnmarshaller.unmarshal(new StringReader(eventXML));
			
		} catch (Exception e) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine(messages.getString("EventFailedXMLParsing") + ": " + e);
			}
			event = null;
		}
		
		// Only process event if all context-info and pay load data is present
		if ((event != null) && (event.getContextInfo() != null)	&& (event.getPayloadData() != null) && (event.getContextInfo().getTimestamp() != null)) {

			// Place each query parameter name and and value into the properties
			MultivaluedMap<String, String> queryParameters = info.getQueryParameters();
			if (queryParameters != null && queryParameters.size() > 0) {
				Iterator<String> iterator = queryParameters.keySet().iterator();
				
				while (iterator.hasNext()) {
					String key = iterator.next();
					
					List<String> list = queryParameters.get(key);
					for (String value : list) {
						props.setProperty(key, value);
					}
				}
			}
			
			// Add each context-info element as an event property
			props.setProperty("eventname", event.getContextInfo().getEventname());
			props.setProperty("usertag", event.getContextInfo().getUsertag());
			props.setProperty("networkapplid", event.getContextInfo().getNetworkapplid());
			props.setProperty("bindingname", event.getContextInfo().getBindingname());
			props.setProperty("capturespecname", event.getContextInfo().getCapturespecname());
			props.setProperty("uowid", event.getContextInfo().getUOWid());
			props.setProperty("timestamp", event.getContextInfo().getTimestamp().toString());
			
			// There can be 0-n elements of pay load data
			List<Element> payloadData = event.getPayloadData().getAny();
			
			if (payloadData.get(0) != null) {
				// Add each pay load data element as a property
				NodeList payload = payloadData.get(0).getChildNodes();
				
				for (int i = 0; i < payload.getLength(); i++) {
					Node node = payload.item(i);
					
					// Only add instances of Element and skip over whitespace
					if (node instanceof Element) {
						// CICS does not allow the . character for the name of business information items.
						// As CA1Y requires some property names to have a . in them, such as mail.content,
						// replace double underscores with .
						String key = node.getLocalName().replaceAll("__", ".");
						props.setProperty(key, node.getTextContent());
						
						// Mark property as a business information item
						props.putBusinessInformationItem(key, node.getTextContent().length());
					}
				}
			}
			
			if (logger.isLoggable(Level.FINE)) {
				logger.fine(messages.getString("EventAboutToProcessEvent") + ": " + props);
			}
			
			// Emit the event
			emitEvent = Emit.emit(props, null, false);
		}
		
		if (logger.isLoggable(Level.FINE)) {
			logger.fine(messages.getString("EventProcessingResult") + ": " + emitEvent);
		}

		if (emitEvent) {
			return Response.status(Response.Status.OK).build();
		}
			
		return Response.status(Response.Status.BAD_REQUEST).build();
	}
}