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

package com.ibm.cics.ca1y.experimental;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;

import com.ibm.cics.ca1y.Emit;
import com.ibm.cics.ca1y.Util;

public class CICSXMPP {
	
	/**
	 * Copyright statement to be included in the compiled class.
	 */
	static final String COPYRIGHT = "Licensed Materials - Property of IBM. "
			+ "CICS SupportPac CA1Y (c) Copyright IBM Corporation 2012 - 2014. All Rights Reserved. "
			+ "US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corporation";
	
	/**
	 * Logging
	 */
	private static Logger logger = Logger.getLogger(CICSXMPP.class.getName());

	/**
	 * Property to specify ...
	 */
	public static final String XMPP_HOST = "xmpp.host";
	public static final String XMPP_HOST_PORT = "xmpp.host.port";
	public static final String XMPP_HOST_PORT_DEFAULT = "5222";
	public static final String XMPP_HOST_SERVICENAME = "xmpp.host.servicename";
	public static final String XMPP_USERID = "xmpp.userid";
	public static final String XMPP_PASSWORD = "xmpp.password";
	public static final String XMPP_TO = "xmpp.to";
	public static final String XMPP_CONTENT = "xmpp.content";
	public static final String XMPP_CONTENT_LENGTH_MAX = "xmpp.content.length.max";
	public static final String XMPP_CONTENT_LENGTH_CHUNK = "xmpp.content.length.chunk";

	private Properties props;
	private Chat chat;
	private ChatManager chatmanager;
	private XMPPConnection connection;
	
	public CICSXMPP(Properties p) {
		props = p;
		
		XMPPConnection.DEBUG_ENABLED = true;

		ConnectionConfiguration config = new ConnectionConfiguration(props.getProperty(XMPP_HOST), Integer.parseInt(props.getProperty(
				XMPP_HOST_PORT, XMPP_HOST_PORT_DEFAULT)), props.getProperty(XMPP_HOST_SERVICENAME, XMPP_HOST));

		// Use encryption before exchanging authentication userid & password
		// config.setSecurityMode(SecurityMode.required);
		//config.setSASLAuthenticationEnabled(true);

		// Do not automatically load the client roster at login
		//config.setRosterLoadedAtLogin(true);

		// Do not automatically send a presence at login
		// config.setSendPresence(false);

		// Do not use compression
		// config.setCompressionEnabled(false);

		connection = new XMPPConnection(config);

		try {
			connection.connect();

			connection.login(props.getProperty(XMPP_USERID), props.getProperty(XMPP_PASSWORD));

			connection.sendPacket(new Presence(Presence.Type.unavailable));

			chatmanager = connection.getChatManager();
			chat = chatmanager.createChat(props.getProperty(XMPP_TO), new JabberListener());
			
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}

	public boolean send() {
		String jabberContent = props.getProperty(XMPP_CONTENT);

		if (props.getProperty(XMPP_CONTENT_LENGTH_MAX) != null) {
			int lengthMax = Integer.parseInt(props.getProperty(XMPP_CONTENT_LENGTH_MAX, ""));

			if (jabberContent.length() > lengthMax) {
				// Truncate string at maximum length
				jabberContent = jabberContent.substring(0, lengthMax);
			}
		}

		int[][] chunks = Util.getChunks(jabberContent, props.getProperty(XMPP_CONTENT_LENGTH_CHUNK));

		if (connection.isConnected()) {
			try {
				Message msg = new Message(props.getProperty(XMPP_TO), Message.Type.chat);

				for (int[] chunk : chunks) {
					msg.setBody(jabberContent.substring(chunk[0], chunk[1]));
					chat.sendMessage(msg);
				}

				if (logger.isLoggable(Level.INFO)) {
					logger.info(Emit.messages.getString("XMPP_LOG_MESSAGES_SUCCESS") + " - " + getMessageSummary());
				}

				return true;

			} catch (XMPPException e) {
				e.printStackTrace();
				
				if (logger.isLoggable(Level.INFO)) {
					logger.info(Emit.messages.getString("XMPP_LOG_MESSAGES_FAIL") + " - " + getMessageSummary());
				}
			}
		}

		return false;
	}

	public String getMessageSummary() {
		return new String(XMPP_USERID + ":" + props.getProperty(XMPP_USERID, "") + ", " + XMPP_TO + ":"
				+ props.getProperty(XMPP_TO, "") + ", " + XMPP_HOST + ":" + props.getProperty(XMPP_HOST, "") + ", "
				+ XMPP_HOST_PORT + ":" + props.getProperty(XMPP_HOST_PORT, "") + ", " + XMPP_HOST_SERVICENAME + ":"
				+ props.getProperty(XMPP_HOST_SERVICENAME, "") + ", " + XMPP_CONTENT_LENGTH_MAX + ":"
				+ props.getProperty(XMPP_CONTENT_LENGTH_MAX, "") + ", " + XMPP_CONTENT + ":"
				+ props.getProperty(XMPP_CONTENT, ""));
	}

	public static class JabberListener implements MessageListener {
		public void processMessage(Chat chat, Message message) {
			// Message received in message.getBody()
		}
	}

	public void destroy() {
		if (connection != null && connection.isConnected()) {
			connection.disconnect();
		}
	}
}