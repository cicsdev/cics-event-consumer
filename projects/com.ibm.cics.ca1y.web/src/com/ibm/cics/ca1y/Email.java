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

import java.util.Date;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.activation.DataHandler;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

/**
 * Class to send a mail to a mail server.
 * 
 * @author Mark Cocker <mark_cocker@uk.ibm.com>
 * @version 1.1
 * @since 2012-02-10
 */
public class Email implements EmitAdapter {

	/**
	 * Copyright statement to be included in the compiled class.
	 */
	static final String COPYRIGHT = "Licensed Materials - Property of IBM. "
			+ "CICS SupportPac CA1Y (c) Copyright IBM Corporation 2012 - 2016. All Rights Reserved. "
			+ "US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corporation";

	/**
	 * Logging
	 */
	private static Logger logger = Logger.getLogger(Email.class.getName());

	/**
	 * Property to specify the mail content. For example:
	 * 'mail.content=Greetings from CICS'
	 */
	private static final String MAIL_CONTENT = "mail.content";

	/**
	 * Property to specify the email address of the sender.
	 */
	private static final String MAIL_FROM = "mail.from";

	/**
	 * Property to specify the email address who should receive replies to this
	 * mail.
	 */
	private static final String MAIL_REPLY_TO = "mail.replyto";

	/**
	 * Property to specify list of one or more email addresses to send this
	 * email to. The list should be a comma separated sequence of addresses that
	 * must follow RFC822 syntax.
	 */
	private static final String MAIL_TO = "mail.to";

	/**
	 * Property to specify list of one or more email addresses to send this
	 * email to as carbon copies.
	 */
	private static final String MAIL_CC = "mail.cc";

	/**
	 * Property to specify list of one or more email addresses to send this
	 * email to as blind carbon copies.
	 */
	private static final String MAIL_BCC = "mail.bcc";

	/**
	 * Property to specify the mail subject line.
	 */
	private static final String MAIL_SUBJECT = "mail.subject";

	/**
	 * Property to specify the user id to log onto the mail server with.
	 */
	private static final String MAIL_USER = "mail.user";

	/**
	 * Property to specify the password to log onto the mail server with.
	 */
	private static final String MAIL_PASSWORD = "mail.password";

	/**
	 * The default MIME type to use for the mail content.
	 */
	private static final String MAIL_CONTENT_MIME_TEXT_DEFAULT = "text/plain; charset=utf-8";

	/**
	 * The default MIME type to use for the mail content.
	 */
	private static final String MAIL_CONTENT_MIME_HTML_DEFAULT = "text/html; charset=utf-8";

	/**
	 * Properties to use as configuration to send the email.
	 */
	private EmitProperties props;

	/**
	 * @author Mark Cocker <mark_cocker@uk.ibm.com>
	 * @version 1.0
	 * @since 2012-02-10
	 * @param p
	 *            - The properties to be associated with this mail.
	 */
	public Email(EmitProperties p) {
		props = p;
	}

	/**
	 * Send a mail using the information in properties.
	 * 
	 * @return boolean - true if the mail was successfully sent, false
	 *         otherwise.
	 */
	public boolean send() {
		Authenticator auth = new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(props.getProperty(MAIL_USER), props.getProperty(MAIL_PASSWORD));
			}
		};

		Session session = Session.getInstance(props, auth);

		try {
			MimeMessage message = new MimeMessage(session);

			if (props.containsKey(MAIL_FROM)) {
				InternetAddress[] addresses = InternetAddress.parse(props.getProperty(MAIL_FROM));

				if ((addresses != null) && (addresses.length > 0)) {
					message.addFrom(addresses);
				}
			}

			if (props.containsKey(MAIL_REPLY_TO)) {
				InternetAddress[] addresses = InternetAddress.parse(props.getProperty(MAIL_REPLY_TO));

				if ((addresses != null) && (addresses.length > 0)) {
					message.setReplyTo(addresses);
				}
			}

			if (props.containsKey(MAIL_TO)) {
				InternetAddress[] addresses = InternetAddress.parse(props.getProperty(MAIL_TO));

				if ((addresses != null) && (addresses.length > 0)) {
					message.addRecipients(Message.RecipientType.TO, addresses);
				}
			}

			if (props.containsKey(MAIL_CC)) {
				InternetAddress[] addresses = InternetAddress.parse(props.getProperty(MAIL_CC));

				if ((addresses != null) && (addresses.length > 0)) {
					message.addRecipients(Message.RecipientType.CC, addresses);
				}
			}

			if (props.containsKey(MAIL_BCC)) {
				InternetAddress[] addresses = InternetAddress.parse(props.getProperty(MAIL_BCC));

				if ((addresses != null) && (addresses.length > 0)) {
					message.addRecipients(Message.RecipientType.BCC, addresses);
				}
			}
			
			if (props.containsKey("EPCX_ABSTIME")) {
				message.setSentDate(Util.getDateFromAbstime(props.getProperty("EPCX_ABSTIME")));

			} else if (props.containsKey("mail.sentdate")) {
				message.setSentDate(Util.getDateFromAbstime(props.getProperty("mail.sentdate")));
			}

			// Set subject.
			// Doc for setSubject method says the application must ensure that the subject does not contain any line breaks.
			// Need to default subject to UTF-8 as properties are Java strings, otherwise the setSubject method defaults to platform default encoding that may be an EBCDIC codepage.  
			message.setSubject(props.getProperty(MAIL_SUBJECT).replaceAll("[\\n\\r]+"," "), "UTF-8");

			// Create a multipart message to hold the message parts
			Multipart mimeMultipart = new MimeMultipart();

			// Add the mail body (content) as a message part.
			addBody(mimeMultipart);

			// Add the attachments as message parts to the mail.
			addAttachments(mimeMultipart);

			if (mimeMultipart.getCount() == 0) {
				// JavaMail requires at least a blank string to be set as the content, otherwise it will throw an exception when sending.
				message.setText("");
				
			} else {
				// Add the multipart to the message.
				message.setContent(mimeMultipart);
			}

			// Add todays date
			message.setSentDate(new Date());

			// Change the default classloader to work around issues documented
			// at site
			// http://blog.hpxn.net/2009/12/02/tomcat-java-6-and-javamail-cant-load-dch/
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			try {
				Thread.currentThread().setContextClassLoader(Transport.class.getClassLoader());

				// Send the message. Note this calls the saveChanges method on
				// the message before sending.
				Transport.send(message);

			} finally {
				Thread.currentThread().setContextClassLoader(cl);
			}

		} catch (Exception e) {
			logger.warning(Emit.messages.getString("MailLogFail") + " - " + getMessageSummary());

			e.printStackTrace();

			return false;
		}

		if (logger.isLoggable(Level.INFO)) {
			logger.info(Emit.messages.getString("MailLogSuccess") + " - " + getMessageSummary());
		}

		return true;
	}

	/**
	 * Add property mail.content to to the multipart message.
	 * 
	 * @param multipart
	 *            - The multipart message to add the attachments to.
	 * @throws MessagingException
	 */
	private void addBody(Multipart multipart) throws MessagingException {
		if (props.getProperty(MAIL_CONTENT) == null) {
			return;
		}
		
		BodyPart mimeBodyPart = new MimeBodyPart();

		if (props.getPropertyMime(MAIL_CONTENT) == null) {
			// If the MIME has not being specified, set the default
			if (props.getProperty(MAIL_CONTENT).startsWith("<")) {
				props.setPropertyMime(MAIL_CONTENT, MAIL_CONTENT_MIME_HTML_DEFAULT);

			} else {
				props.setPropertyMime(MAIL_CONTENT, MAIL_CONTENT_MIME_TEXT_DEFAULT);
			}
		}

		mimeBodyPart.setContent(props.getProperty(MAIL_CONTENT), props.getPropertyMime(MAIL_CONTENT));
		multipart.addBodyPart(mimeBodyPart);
	}

	/**
	 * Add attachments in the properties to the multipart message.
	 * 
	 * @param multipart
	 *            - The multipart message to add the attachments to.
	 * @throws MessagingException
	 */
	private void addAttachments(Multipart multipart) throws MessagingException {
		Enumeration<?> properties = props.keys();

		while (properties.hasMoreElements()) {
			String key = (String) properties.nextElement();

			if ((props.getPropertyMime(key) == null) || (key.equals(Email.MAIL_CONTENT)) || (key.equals(Email.MAIL_SUBJECT))|| (props.getPropertyPrivacy(key) == true)) {
				continue;
			}

			byte[] emailAttachment = props.getPropertyAttachment(key);
			String emailAttachmentName = null;

			if (emailAttachment == null) {
				try {
					emailAttachment = props.getProperty(key).getBytes("UTF-8");
				} catch (Exception e) {
				}
			}

			if ((emailAttachment == null) || (emailAttachment.length == 0)) {
				continue;
			}
			
			if (props.getPropertyAlternateName(key) != null) {
				emailAttachmentName = props.getPropertyAlternateName(key);
				
			} else {
				emailAttachmentName = key;
			}

			if (logger.isLoggable(Level.FINE)) {
				logger.fine(Emit.messages.getString("AddAttachment") + " " + emailAttachmentName + ",property:" + key + ",MIME:" + props.getPropertyMime(key) + ",length:" + emailAttachment.length);
			}

			BodyPart messageBodyPart = new MimeBodyPart();
			ByteArrayDataSource bads = new ByteArrayDataSource(emailAttachment, props.getPropertyMime(key));
			messageBodyPart.setDataHandler(new DataHandler(bads));
			messageBodyPart.setFileName(emailAttachmentName);
			multipart.addBodyPart(messageBodyPart);
		}
	}

	/**
	 * Return true if we should attempt to emit 
	 * 
	 * @return true if the entries in EmitProperties are valid for emission. 
	 */
	public static boolean validForEmission(EmitProperties props) {
		return (props.containsKey(Email.MAIL_TO) || props.containsKey(Email.MAIL_CC) || props.containsKey(Email.MAIL_BCC));
	}
	
	/**
	 * Return a short text summary of the mail.
	 * 
	 * @return short summary of the mail.
	 */
	private String getMessageSummary() {
		StringBuilder sb = new StringBuilder();
		sb.append(MAIL_FROM).append(":").append(props.getProperty(MAIL_FROM, ""));

		if (props.containsKey(MAIL_REPLY_TO)) {
			sb.append(",").append(MAIL_REPLY_TO).append(":").append(props.getProperty(MAIL_REPLY_TO));
		}

		sb.append(",").append(MAIL_TO).append(":").append(props.getProperty(MAIL_TO));

		if (props.containsKey(MAIL_CC)) {
			sb.append(",").append(MAIL_CC).append(":").append(props.getProperty(MAIL_CC));
		}

		if (props.containsKey(MAIL_CC)) {
			sb.append(",").append(MAIL_BCC).append(":").append(props.getProperty(MAIL_BCC));
		}

		sb.append(",").append(MAIL_SUBJECT).append(":").append(props.getProperty(MAIL_SUBJECT, ""));

		return sb.toString();
	}
}