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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.cics.server.*;
import com.ibm.cics.server.invocation.CICSProgram;
import com.ibm.cics.ca1y.epadapterinterface.*;
import com.ibm.etools.marshall.util.MarshallExternalDecimalUtils;
import com.ibm.etools.marshall.util.MarshallFloatUtils;
import com.ibm.etools.marshall.util.MarshallIntegerUtils;
import com.ibm.etools.marshall.util.MarshallPackedDecimalUtils;
import com.ibm.jzos.ZFile;
import com.ibm.jzos.ZUtil;

import java.math.BigInteger;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPHTTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.commons.net.util.TrustManagerUtils;

/**
 * Emit a message.
 * 
 * @author Mark Cocker <mark_cocker@uk.ibm.com>
 * @version 1.7
 * @since 2012-07-19
 */
public class Emit {

	/**
	 * Copyright statement to be included in the compiled class.
	 */
	static final String COPYRIGHT = "Licensed Materials - Property of IBM. "
			+ "CICS SupportPac CA1Y (c) Copyright IBM Corporation 2012 - 2016. All Rights Reserved. "
			+ "US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corporation";

	/**
	 * Indicator to answer if JCICS API is available
	 */
	private static boolean isCICS = Util.isCICS();

	/**
	 * Java subsystem logger
	 */
	private static Logger logger = Logger.getLogger(Emit.class.getName());

	/**
	 * Property to specify the regex that defines how to find tokens.
	 */
	private static final String TOKEN_REGEX = "token.regex";

	/**
	 * Default regex to find tokens.
	 */
	private static final String TOKEN_REGEX_DEFAULT = "\\{(.+?)\\}";

	/**
	 * Properties to specify a file of additional properties to load.
	 */
	private static final String IMPORT = "import";
	public static final String CA1Y_IMPORT = "ca1y.import";

	/**
	 * Properties to specify a file of additional properties to load that should not be logged.
	 */
	public static final String IMPORT_PRIVATE = "import.private";
	public static final String CA1Y_IMPORT_PRIVATE = "ca1y.import.private";

	/**
	 * Property to specify a CICS program to link to when an emission fails.
	 */
	public static final String ONFAILURE = "onfailure";

	/**
	 * Assume the DFHEP.ADAPTERPARM container is available on the CICS event processing custom EP Adapter interface.
	 */
	private static boolean dfhepAdaptparm = true;

	/**
	 * Version of the DFHEP.CONTEXT container provided on the CICS event processing custom EP Adapter interface.
	 */
	private static int epcxVersion;

	/**
	 * The default character set for the JVM.
	 */
	private static final String defaultCharset = Charset.defaultCharset().toString();

	/*
	 * MIME type for zip files
	 */
	private static final String MIME_ZIP = "application/zip";

	/*
	 * MIME type for XML files
	 */
	private static final String MIME_XML = "text/xml";

	/*
	 * MIME type for JSON files
	 */
	private static final String MIME_JSON = "application/json";

	/*
	 * Default channel name
	 */
	private static final String CA1Y_CHANNEL = "CA1Y";

	/*
	 * Default container name
	 */
	private static final String CA1Y_CONTAINER = "CA1Y";

	/*
	 * Default abend code
	 */
	private static final String CA1Y_ABEND_CODE = "CA1Y";

	/*
	 * CICS region local CCSID, or use file.encoding if not present
	 */
	private static String LOCAL_CCSID = System.getProperty("com.ibm.cics.jvmserver.local.ccsid", System.getProperty("file.encoding"));

	/*
	 * Property to set if the event adapter is required to only use recoverable emission methods
	 */
	private static final String CA1Y_RECOVERABLE = "CA1YRecoverable";

	/*
	 * XML header
	 */
	private static final String XML_HEADER = "<?xml version=\"1.0\"?>";

	/*
	 * Date format that conforms to RFC 3339
	 */
	private static final SimpleDateFormat rfc3339 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sssZ");

	/*
	 * Maximum and minimum container name sizes
	 */
	private static final int CONTAINER_NAME_LENGTH_MIN = 1;
	private static final int CONTAINER_NAME_LENGTH_MAX = 16;

	/*
	 * Resource bundle for messages based on JVM locale.
	 */
	public static ResourceBundle messages = ResourceBundle.getBundle("com/ibm/cics/ca1y/messages");

	/**
	 * Format a request from the JVM standard in and command line arguments. Each line in standard in, or argument on
	 * the command line should follow the conventions required to be loaded by the Java Properties class, eg. name=value
	 * 
	 * @param args
	 *            - zero or more arguments specified on the command line
	 * @return exit code - 0 if successfull, non-zero if there was at least one problem actioning the request
	 */
	public static void main(String[] args) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine(messages.getString("EmitStart"));
		}

		EmitProperties props = new EmitProperties();

		addPropertiesFromInputStream(props, System.in);

		addPropertiesFromString(props, args);

		boolean returnCode = Emit.emit(props, null, false);

		if (logger.isLoggable(Level.FINE)) {
			logger.fine(messages.getString("EmitFinish"));
		}

		System.exit(returnCode ? 0 : 8);
	}

	/**
	 * Format a request from Java Properties.
	 * 
	 * @param props
	 *            - properties to use as parameters.
	 * @return boolean - true if the emission was successful.
	 */
	public static boolean emit(Properties props) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine(messages.getString("EmitStart"));
		}

		EmitProperties p = new EmitProperties();
		p.putAll(props);

		boolean success = Emit.emit(p, null, false);

		if (logger.isLoggable(Level.FINE)) {
			logger.fine(messages.getString("EmitFinish"));
		}

		return success;
	}

	/**
	 * Format a request from a CICS event, channel, commarea, or start data and emit it.
	 * 
	 * Can be called as a CICS custom event adapter or as a CICS program.
	 * 
	 * If called with a CICS channel where the channel name begins with 'DFHEP.' or 'DFHMP.' it is assumed to be a
	 * custom event adapter and as such the parameters are expected to be provided in containers DFHEP.ADAPTER,
	 * DFHEP_DESCRIPTOR, DFHEP_CONTEXT, and DFHEP_ADAPTPARM.
	 * 
	 * Otherwise parameters are expected to be in a CICS commarea. or CICS start data, or a CICS container named CA1Y.
	 * 
	 * @param commAreaHolder
	 *            - the CICS commarea to use as parameters.
	 * @return void - required to be void by CICS.
	 */
	@CICSProgram("CA1Y")
	public static void main() {
		main((CommAreaHolder) null);
	}

	public static void main(CommAreaHolder commAreaHolder) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine(messages.getString("EmitStart") + " " + messages.getString("Name") + " " + messages.getString("Version") + " "
					+ messages.getString("Copyright"));
		}

		Channel channel = Task.getTask().getCurrentChannel();
		if (channel == null) {
			try {
				// Create channel in case it is needed later for linking to other CICS programs
				channel = Task.getTask().createChannel(CA1Y_CHANNEL);

			} catch (ChannelErrorException e) {
				logger.warning(messages.getString("CouldNotCreateChannelCA1Y"));
				e.printStackTrace();

				// As there is no CICS channel, cannot return an error container, so perform a CICS ABEND
				Task.getTask().abend(CA1Y_ABEND_CODE);
			}
		}

		if (logger.isLoggable(Level.FINE)) {
			logger.fine(messages.getString("EmitStartChannel") + " " + channel.getName());
		}

		boolean isStartedWithEPAdpter = false;
		boolean isStartedWithCommarea = false;
		boolean isStartedWithCA1YContainer = false;
		EmitProperties props = new EmitProperties();

		if ((channel.getName().startsWith("DFHEP.")) || (channel.getName().startsWith("DFHMP."))) {
			// Configuration is passed as an event adapter container.
			//
			// A CICS event from an event binding is passed in a channel name
			// starting with 'DFHEP.'.
			// A CICS event from a policy is passed in a channel name starting with 'DFHMP.'.

			// Avoid having to make this test again by keeping a cache
			isStartedWithEPAdpter = true;

			if (addContainerDFHEP_ADAPTER(props, channel) == false) {
				logger.warning(messages.getString("MissingContainerDfhepAdapter"));

				putContainerCA1YRESPONSE(false, channel);
				return;
			}

		} else if ((commAreaHolder != null) && (commAreaHolder.getValue().length > 0)) {
			// Configuration is passed in a CICS commarea
			isStartedWithCommarea = true;

			addCommarea(props, commAreaHolder);

		} else if (Task.getTask().getSTARTCODE().equals("SD")) {
			// Configuration is passed in the CICS start data
			addRetrieveData(props);

		} else {
			// Configuration is passed in a CICS container named CA1Y
			isStartedWithCA1YContainer = true;

			if (addContainerCA1Y(props, channel) == false) {
				logger.warning(messages.getString("MissingContainerCA1Y"));

				putContainerCA1YRESPONSE(false, channel);
				return;
			}
		}

		if (isStartedWithEPAdpter == false) {
			addPropertiesFromTask(props, Task.getTask());
			addPropertiesFromCICSRegion(props);
		}

		boolean emissionSuccessful = emit(props, channel, isStartedWithEPAdpter);

		// Create a response container for each property that needs to be returned
		for (Map.Entry<String, String> entry : props.getReturnContainers().entrySet()) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine(messages.getString("CreateResponseContainer") + " " + EmitProperties.getPropertySummary(props, entry.getKey()));
			}

			try {
				Container container = channel.createContainer(entry.getValue());

				if (props.getPropertyAttachment(entry.getKey()) == null) {
					// Create a container of type CHAR
					container.putString(props.getProperty(entry.getKey()));

				} else {
					// Creates a container of type BIT
					container.put(props.getPropertyAttachment(entry.getKey()));
				}

			} catch (Exception e) {
				logger.fine(messages.getString("FailedToCreateResponseContainer") + " " + entry.getValue());
				e.printStackTrace();
				emissionSuccessful = false;
			}
		}

		if ((emissionSuccessful == false) && (props.containsKey(ONFAILURE))) {
			// Link to onfailure program
			String onfailure = props.getProperty(ONFAILURE).trim();

			if (logger.isLoggable(Level.FINE)) {
				logger.fine(messages.getString("LinkToOnfailure") + " " + onfailure);
			}

			Program p = new Program();
			p.setName(onfailure);

			try {
				p.link(channel);

				// As we successfully called the specified program to handle the failure, treat this as a successful
				// emission
				emissionSuccessful = true;

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (logger.isLoggable(Level.FINE)) {
			logger.fine(messages.getString("EmitFinish") + " " + messages.getString("Name") + " " + messages.getString("Version") + " "
					+ messages.getString("Copyright"));

		}

		if (isStartedWithEPAdpter) {
			if (emissionSuccessful == false) {
				// CICS event processing expects the adapter to issue a CICS abend if the event could not be emitted to
				// update CICS statistics
				Task.getTask().abend(CA1Y_ABEND_CODE);
			}

		} else if (isStartedWithCA1YContainer) {
			putContainerCA1YRESPONSE(emissionSuccessful, channel);

		} else if (isStartedWithCommarea) {
			putCommarea(emissionSuccessful, commAreaHolder);
		}
	}

	/**
	 * Emit a message using configuration from a set of properties, and data from the CICS channel.
	 *
	 * @param props
	 *            - properties to use
	 * @param cicsChannel
	 *            - CICS channel
	 * @param isStartedWithEPAdpter
	 *            - true if the CICS current channel is the architected EP adapter channel
	 * @return true if emission was successful
	 */
	public static boolean emit(EmitProperties props, Channel cicsChannel, boolean isStartedWithEPAdpter) {

		boolean emissionSuccessful = true;
		try {
			// Create a compiled regex from either the default pattern or the specified override
			Pattern pattern = getPattern(props);

			if (System.getProperties().containsKey(CA1Y_IMPORT)) {
				// Merge properties from the JVM system property ca1y.import
				if (logger.isLoggable(Level.FINE)) {
					logger.fine(messages.getString("LoadingPropertiesFromImport") + " " + CA1Y_IMPORT);
				}

				props.setProperty(CA1Y_IMPORT, System.getProperties().getProperty(CA1Y_IMPORT));

				resolveTokensInKey(CA1Y_IMPORT, pattern, props, cicsChannel, isStartedWithEPAdpter, false);

				if (!Util.loadProperties(props, props.getProperty(CA1Y_IMPORT))) {
					logger.warning(Emit.messages.getString("InvalidImportProperties") + " " + CA1Y_IMPORT);
				}

				// Remove the IMPORT property otherwise it will go through unnecessary token processing
				props.remove(CA1Y_IMPORT);

				// Re-evaluate token regex as the imported properties may have changed it
				pattern = getPattern(props);
			}

			if (props.containsKey(IMPORT)) {
				// Merge properties from the supplied property import
				if (logger.isLoggable(Level.FINE)) {
					logger.fine(messages.getString("LoadingPropertiesFromImport") + " " + IMPORT);
				}

				resolveTokensInKey(IMPORT, pattern, props, cicsChannel, isStartedWithEPAdpter, false);

				if (!Util.loadProperties(props, props.getProperty(IMPORT))) {
					logger.warning(Emit.messages.getString("InvalidImportProperties") + " " + IMPORT);
				}

				// Remove the IMPORT property otherwise it will go through unnecessary token processing
				props.remove(IMPORT);

				// Re-evaluate token regex as the imported properties may have changed it
				pattern = getPattern(props);
			}

			if (props.containsKey(IMPORT_PRIVATE)) {
				// Merge properties from the import.private location, marking each of the merged properties as private
				if (logger.isLoggable(Level.FINE)) {
					logger.fine(messages.getString("LoadingPropertiesFromImport") + " " + IMPORT_PRIVATE);
				}

				props.setPropertyPrivacy(IMPORT_PRIVATE, true);

				resolveTokensInKey(IMPORT_PRIVATE, pattern, props, cicsChannel, isStartedWithEPAdpter, false);

				// Use a temporary object to load IMPORT_PRIVATE into as this is the only way to then set the privacy
				// for each property
				Properties privateProperties = new Properties();

				if (!Util.loadProperties(privateProperties, props.getProperty(IMPORT_PRIVATE))) {
					logger.warning(Emit.messages.getString("InvalidImportProperties") + " " + IMPORT_PRIVATE);
				}

				// Remove IMPORT_PRIVATE property otherwise it will be logged and will go through unnecessary token
				// processing.
				props.setPropertyPrivacy(IMPORT_PRIVATE, false);
				props.remove(IMPORT_PRIVATE);

				// Copy each property (note putAll method is discouraged) and set the privacy attribute to true
				Enumeration<?> e = privateProperties.propertyNames();
				while (e.hasMoreElements()) {
					String key = (String) e.nextElement();
					props.setProperty(key, privateProperties.getProperty(key));
					props.setPropertyPrivacy(key, true);
				}
			}

			if (isStartedWithEPAdpter) {
				// Load properties from CICS event containers
				addPropertiesFromDFHEP_DESCRIPTOR(props, cicsChannel);
				addPropertiesFromDFHEP_CONTEXT(props, cicsChannel);
				addPropertiesFromDFHEP_ADAPTPARM(props, cicsChannel);
			}

			// Resolve tokens in each property value in property name order as this is likely to be easier to predict
			Enumeration<?> e = props.propertyNamesOrdered();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				resolveTokensInKey(key, pattern, props, cicsChannel, isStartedWithEPAdpter, true);
			}

			// Convert property values from one MIME type to another
			e = props.propertyNamesOrdered();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();

				if ((props.getPropertyAlternateName(key) != null) && (props.getProperty(props.getPropertyAlternateName(key)) != null)) {
					props.setPropertyAlternateName(key, props.getProperty(key));
				}

				if ((props.getPropertyMime(key) != null) && (props.getPropertyMimeTo(key) != null)) {
					emissionSuccessful = convertProperty(key, props);

					if (!emissionSuccessful) {
						break;
					}

					if ((logger.isLoggable(Level.FINE)) && (!props.getPropertyPrivacy(key))) {
						logger.fine(messages.getString("AfterConversion") + " " + EmitProperties.getPropertySummary(props, key));
					}
				}
			}

			// Create zip files
			e = props.propertyNamesOrdered();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();

				if (props.getPropertyZip(key) != null) {
					props.setPropertyAttachment(key, createZip(props.getPropertyZipInclude(key), props));
					props.setPropertyAlternateName(key, props.getPropertyZip(key));
					props.setPropertyMime(key, MIME_ZIP);

					if (!emissionSuccessful) {
						break;
					}

					if ((logger.isLoggable(Level.FINE)) && (!props.getPropertyPrivacy(key))) {
						logger.fine(messages.getString("AfterZip") + " " + EmitProperties.getPropertySummary(props, key));
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			emissionSuccessful = false;
		}

		if (emissionSuccessful) {
			emissionSuccessful = callAdapters(props);
		}

		return emissionSuccessful;
	}

	/**
	 * Call a set of adapters in a pre-defined sequence providing the properties required by the adapter are present.
	 * 
	 * @param props
	 *            - properties to use
	 * @return true if adapters successfully emitted data
	 */
	private static boolean callAdapters(EmitProperties props) {
		int adaptersSuccessful = 0;
		int adaptersFailed = 0;
		
		// This method would benefit from using an array of the clases that implement the EmitAdapter interface
		// however this would require use of Java 8 that introduced support for the static method validForEmission on the interface.

		if (logger.isLoggable(Level.FINE)) {
			logger.fine(messages.getString("AdaptersAboutToCall"));
		}
		
		if (Email.validForEmission(props)) {
			// Emit message to SMTP server

			if (props.isPropertyTrue(CA1Y_RECOVERABLE)) {
				adaptersFailed++;
				logger.warning(messages.getString("RecoverableEventNotSupportedEmail"));

			} else {
				Email email = new Email(props);

				if (email.send()) {
					adaptersSuccessful++;
				} else {
					adaptersFailed++;
				}
			}
		}

		if (CICSQueue.validForEmission(props)) {
			// Emit message to a CICS queue. Note it is not possible to establish via JCICS if the queue is recoverable
			// or not, so cannot guard against using a recoverable event with a non-recoverable queue
			CICSQueue cicsQueue = new CICSQueue(props);
			
			if (cicsQueue.send()) {
				adaptersSuccessful++;
			} else {
				adaptersFailed++;
			}
		}

		if (MVSJob.validForEmission(props)) {
			// Emit message as an MVS job

			if (props.isPropertyTrue(CA1Y_RECOVERABLE)) {
				// Events required to be recoverable are not supported
				adaptersFailed++;
				logger.warning(messages.getString("RecoverableEventNotSupportedMVSJOB"));

			} else {
				MVSJob mvsJob = new MVSJob(props);

				if (mvsJob.send()) {
					adaptersSuccessful++;
				} else {
					adaptersFailed++;
				}
			}
		}

		if (MVSWriteToOperator.validForEmission(props)) {
			// Emit message as an MVS console message

			if (props.isPropertyTrue(CA1Y_RECOVERABLE)) {
				// Events required to be recoverable are not supported
				adaptersFailed++;
				logger.warning(messages.getString("RecoverableEventNotSupportedMVSWTO"));

			} else {
				MVSWriteToOperator mvsWriteToOperator = new MVSWriteToOperator(props);

				if (mvsWriteToOperator.send()) {
					adaptersSuccessful++;
				} else {
					adaptersFailed++;
				}
			}
		}

		if (JavaHTTP.validForEmission(props)) {
			// Emit message to an HTTP server

			if (props.isPropertyTrue(CA1Y_RECOVERABLE)) {
				// Events required to be recoverable are not supported
				adaptersFailed++;
				logger.warning(messages.getString("RecoverableEventNotSupportedHTTP"));

			} else {
				JavaHTTP http = new JavaHTTP(props);

				if (http.send()) {
					adaptersSuccessful++;
				} else {
					adaptersFailed++;
				}
			}
		}
		
		if (CICSHTTP.validForEmission(props)) {
			// Emit message to an HTTP server

			if (props.isPropertyTrue(CA1Y_RECOVERABLE)) {
				// Events required to be recoverable are not supported
				adaptersFailed++;
				logger.warning(messages.getString("RecoverableEventNotSupportedHTTP"));

			} else {
				CICSHTTP cicsHTTP = new CICSHTTP(props);

				if (cicsHTTP.send()) {
					adaptersSuccessful++;
				} else {
					adaptersFailed++;
				}
			}
		}

		if (JZOSFile.validForEmission(props)) {
			// Emit message to a File via JZOS

			if (props.isPropertyTrue(CA1Y_RECOVERABLE)) {
				// Events required to be recoverable are not supported
				adaptersFailed++;
				logger.warning(messages.getString("RecoverableEventNotSupportedJZOS"));

			} else {
				JZOSFile pdsFile = new JZOSFile(props);

				if (pdsFile.send()) {
					adaptersSuccessful++;
				} else {
					adaptersFailed++;
				}
			}
		}

		if (logger.isLoggable(Level.FINE)) {
			logger.fine(MessageFormat.format(messages.getString("AdaptersCalled"), adaptersSuccessful, adaptersFailed));
		}
		
		if ((adaptersSuccessful == 0) || (adaptersFailed > 0)) {
			// Return false if there were adapters called successfully or any adapters had a failure
			return false;
		}
		
		return true;
	}

	/**
	 * Create a zip file containing a file entry for each of the required properties.
	 * 
	 * @param properties
	 *            - comma-separated list of properties to include in the zip
	 * @param props
	 *            - EmitProperties that contains the properties to include
	 * @return byte[] that represents the zip
	 */
	private static byte[] createZip(String properties, EmitProperties props) throws IOException {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine(messages.getString("AboutToCreateZip") + " " + properties);
		}

		List<String> propertyList = Arrays.asList(properties.split("\\s*,\\s*"));
		Iterator<String> i = propertyList.iterator();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ZipOutputStream zipfile = new ZipOutputStream(bos);
		ZipEntry zipentry = null;

		while (i.hasNext()) {
			String key = (String) i.next();

			// Set the file name of zip entry
			if (props.getPropertyAlternateName(key) != null) {
				zipentry = new ZipEntry(props.getPropertyAlternateName(key));

			} else {
				zipentry = new ZipEntry(key);
			}

			// Put the zip entry into the zip
			zipfile.putNextEntry(zipentry);

			// Write the property contents into the zip entry
			if (props.getPropertyAttachment(key) != null) {
				zipfile.write(props.getPropertyAttachment(key));

			} else {
				// Windows requires text files use CR+LF, so replace \n with \r\n
				String property = props.getProperty(key);

				if (property != null) {
					zipfile.write(property.replaceAll("\n", "\r\n").getBytes("UTF-8"));
				}
			}

			// Remove the property MIME to prevent it from becoming an attachment. Do not remove the property itself as
			// it may be used elsewhere
			props.setPropertyMime(key, null);
		}

		zipfile.close();

		return bos.toByteArray();
	}

	/**
	 * Convert a property from XML using either JAX processor and a stylesheet, or the FOP Apache processor and
	 * optionally a stylesheet.
	 * 
	 * @param key
	 *            - property to convert
	 * @param props
	 *            - EmitProperties that contains the property to convert
	 * @return true if conversion was successful
	 */
	private static boolean convertProperty(String key, EmitProperties props) {

		if ((ConvertXML.xsltFromMimeTypes.contains(props.getPropertyMime(key))) && (ConvertXML.xsltToMimeTypes.contains(props.getPropertyMimeTo(key)))) {
			// Use JAXP to convert
			String result = ConvertXML.convertXSLT(props.getProperty(key), props.getProperty(props.getPropertyXSLT(key)), props);

			if (result == null) {
				return false;
			}
			props.setProperty(key, result);

			// Set the MIME type to the converted MIME type
			props.setPropertyMime(key, props.getPropertyMimeTo(key));
			props.setPropertyMimeTo(key, null);

			return true;
		}

		if ((ConvertFOP.fopFromMimeTypes.contains(props.getPropertyMime(key))) && (ConvertFOP.fopToMimeTypes.contains(props.getPropertyMimeTo(key)))) {
			// Use Apache FOP to convert
			ByteArrayInputStream bais = null;
			ByteArrayInputStream xsltStream = null;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			// Create input stream from the property key or attachment
			if (props.getPropertyAttachment(key) == null) {
				try {
					bais = new ByteArrayInputStream(props.getProperty(key).getBytes("UTF-8"));

				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}

			} else {
				bais = new ByteArrayInputStream(props.getPropertyAttachment(key));
			}

			// Create input stream from XSLT
			if (props.getPropertyXSLT(key) != null) {
				String xslt = props.getProperty(props.getPropertyXSLT(key));

				if (xslt == null) {
					logger.warning(messages.getString("XSLTMissing") + " " + props.getPropertyXSLT(key));

				} else {
					try {
						xsltStream = new ByteArrayInputStream(xslt.getBytes("UTF-8"));

					} catch (Exception e) {
						e.printStackTrace();
						return false;
					}
				}
			}

			try {
				int pageCount = ConvertFOP.convertFOP(bais, baos, xsltStream, props.getPropertyMimeTo(key), props);

				if (pageCount > 0) {
					props.setPropertyAttachment(key, baos.toByteArray());
					props.setPropertyMime(key, props.getPropertyMimeTo(key));
					props.setPropertyMimeTo(key, null);
				}

			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}

		return true;
	}

	/**
	 * Create a compiled pattern from the regex supplied in the properties, or a default.
	 * 
	 * @param props
	 *            - the properties to find the regex in
	 * @return compiled pattern
	 */
	private static Pattern getPattern(Properties props) {

		if (props.containsKey(TOKEN_REGEX)) {
			try {
				String regex = props.getProperty(TOKEN_REGEX);

				// Remove regex from properties to prevent it from being processed further
				props.remove(TOKEN_REGEX);

				return Pattern.compile(regex);

			} catch (Exception e) {
				logger.warning(messages.getString("InvalidRegex") + " " + TOKEN_REGEX_DEFAULT);
			}
		}

		return Pattern.compile(TOKEN_REGEX_DEFAULT);
	}

	/**
	 * Add name / value properties from a String array.
	 * 
	 * @param props
	 *            - the properties to add to
	 */
	private static boolean addPropertiesFromString(Properties props, String[] args) {

		if ((args.length > 0) && (logger.isLoggable(Level.FINE))) {
			logger.fine(messages.getString("LoadingPropertiesFromArgs") + ":");
		}

		/* Read the Java command line arguments as properties */
		for (String s : args) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine(s);
			}

			try {
				props.load(new StringReader(s));

				return true;

			} catch (Exception e) {

			}
		}

		return false;
	}

	/**
	 * Add name / value properties from an InputStream.
	 * 
	 * @param props
	 *            - the properties to add to
	 * @return true if successful
	 */
	private static boolean addPropertiesFromInputStream(Properties props, InputStream is) {
		if (is == null)
			return false;

		if (props == null)
			props = new Properties();

		// Read the Java standard input stream as properties
		try {
			if (is.available() > 0) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine(messages.getString("LoadingPropertiesFromStandardIn"));
				}

				try {
					props.load(is);

					return true;

				} catch (IOException e) {
					// The data in System.in could not be interpreted as a set of properties
					e.printStackTrace();
				}
			}

		} catch (IOException e) {
			// Could not read the input stream
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * Add name / value properties from the contents of the commarea.
	 * 
	 * @param props
	 *            - the properties to add to.
	 * @param channel
	 *            - the channel to get the container from.
	 * @return true if successful
	 */
	private static boolean addCommarea(Properties props, CommAreaHolder commAreaHolder) {
		String configuration;

		try {
			configuration = new String(commAreaHolder.getValue(), LOCAL_CCSID);

		} catch (Exception e) {
			logger.warning(messages.getString("InvalidCommarea") + " " + LOCAL_CCSID + ":" + e.getMessage());

			return false;
		}

		if (logger.isLoggable(Level.FINE)) {
			logger.fine(messages.getString("LoadingPropertiesFromCommarea") + ":" + configuration);
		}

		return Util.loadProperties(props, configuration);
	}

	/**
	 * Add name / value properties from the contents of container CA1Y.
	 * 
	 * @param props
	 *            - the properties to add to.
	 * @param channel
	 *            - the channel to get the container from.
	 * @return true if successful
	 */
	private static boolean addContainerCA1Y(Properties props, Channel channel) {
		try {
			// CA1Y container provides the initial configuration. Let CICS convert the container
			String s = new String(channel.createContainer(CA1Y_CONTAINER).get("UTF-8"), "UTF-8");

			if (logger.isLoggable(Level.FINE)) {
				logger.fine(messages.getString("UsingContainerCA1Y") + ":" + s);
			}

			return Util.loadProperties(props, s);

		} catch (Exception e) {
			logger.warning(messages.getString("InvalidContainerCA1Y") + ":" + e.getMessage());
		}

		return false;
	}

	/**
	 * Add name / value properties from the contents of CICS start data.
	 * 
	 * @param props
	 *            - the properties to add to.
	 * @return true if successful
	 */
	private static boolean addRetrieveData(Properties props) {
		BitSet bs = new BitSet();
		bs.set(RetrieveBits.DATA, true);
		RetrievedDataHolder rdh = new RetrievedDataHolder();

		try {
			Task.getTask().retrieve(bs, rdh);

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		String configuration;

		try {
			configuration = new String(rdh.getValue().getData(), LOCAL_CCSID);

		} catch (Exception e) {
			logger.warning(messages.getString("InvalidStartData") + " " + LOCAL_CCSID + ":" + e.getMessage());

			return false;
		}

		if (logger.isLoggable(Level.FINE)) {
			logger.fine(messages.getString("LoadingPropertiesFromStartData") + ":" + configuration);
		}

		return Util.loadProperties(props, configuration);
	}

	/**
	 * Put a response value in container CA1YRESPONSE.
	 * 
	 * @param b
	 * @param channel
	 * @return true if container was created
	 */
	private static boolean putContainerCA1YRESPONSE(boolean b, Channel channel) {
		try {
			Container container = channel.createContainer("CA1YRESPONSE");
			container.putString(b ? "true" : "false");

		} catch (Exception e) {
			e.printStackTrace();

			return false;
		}

		return true;
	}

	/**
	 * Put a response value in the CICS commarea.
	 * 
	 * @param b
	 * @param commAreaHolder
	 * @return true if the response was copied into the commarea, false otherwise.
	 */
	private static boolean putCommarea(boolean b, CommAreaHolder commAreaHolder) {
		byte[] response;

		if (commAreaHolder == null) {
			return false;
		}

		try {
			// Overwrite the commarea with spaces in the local CCSID
			Arrays.fill(commAreaHolder.getValue(), (" ".getBytes(LOCAL_CCSID))[0]);

			response = Boolean.toString(b).getBytes(LOCAL_CCSID);

		} catch (UnsupportedEncodingException e) {
			// Could not use the LOCAL CCSID
			e.printStackTrace();

			return false;
		}

		if (response.length > commAreaHolder.getValue().length) {
			// The commarea is too small for the response, and we are not allowed to change the size
			return false;
		}

		// Copy the response into the commarea
		System.arraycopy(response, 0, commAreaHolder.getValue(), 0, response.length);

		return true;
	}

	/**
	 * Add name=value properties from contents of container 'DFHEP.ADAPTER'. This container is created from the CICS EP
	 * custom adapter configuration specified in the Adapter tab in the CICS Explorer .epadapter and .evbind editors.
	 * 
	 * @param props
	 *            - the properties to add to.
	 * @param channel
	 *            - the channel to get the container from.
	 * @return true if properties added successfully, false otherwise.
	 */
	private static boolean addContainerDFHEP_ADAPTER(Properties props, Channel channel) {
		String dfhepAdapter;

		try {
			dfhepAdapter = channel.createContainer("DFHEP.ADAPTER").getString();

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		if ((dfhepAdapter == null) || (dfhepAdapter.length() == 0)) {
			return false;
		}

		if (logger.isLoggable(Level.FINE)) {
			logger.fine(messages.getString("UsingContainerDfhepPAdapter") + " " + dfhepAdapter);
		}

		return Util.loadProperties(props, dfhepAdapter);
	}

	/**
	 * Add CICS task information as name / value properties.
	 * 
	 * @param props
	 *            - The properties to add to
	 * @param t
	 *            - The CICS task
	 * @return true if properties added successfully
	 */
	private static boolean addPropertiesFromTask(EmitProperties props, Task t) {
		try {
			props.setPropertyAndResolved("TASK_TRANID", t.getTransactionName().trim());
			props.setPropertyAndResolved("TASK_USERID", t.getUSERID().trim());
			props.setPropertyAndResolved("TASK_PROGRAM", t.getProgramName().trim());
			props.setPropertyAndResolved("TASK_NUMBER", Integer.toString(t.getTaskNumber()));

		} catch (Exception e) {
			return false;
		}

		return true;
	}

	/**
	 * Add CICS region information as name / value properties.
	 * 
	 * @param props
	 *            - The properties to add to
	 * @return true if properties added successfully
	 */
	private static boolean addPropertiesFromCICSRegion(EmitProperties props) {
		props.setPropertyAndResolved("REGION_SYSID", Region.getSYSID().trim());
		props.setPropertyAndResolved("REGION_APPLID", Region.getAPPLID().trim());

		return true;
	}

	/**
	 * Add name / value properties from contents of container DFHEP.DESCRIPTOR and business information items from
	 * containers DFHEP.DATA.nnnnn
	 * 
	 * @param props
	 *            - The properties to add entries to
	 * @param channel
	 *            - The channel to get the container from
	 * @return true if properties added successfully
	 */
	private static boolean addPropertiesFromDFHEP_DESCRIPTOR(EmitProperties props, Channel channel) throws ContainerErrorException, ChannelErrorException,
			CCSIDErrorException, CodePageErrorException {

		// DFHEP.DESCRIPTOR container is mapped by COBOL copybook
		// hlq.DFHCOB(DFHEPDEO).
		// Use createContainer to create a proxy container object otherwise
		// JCICS will use JNI to CICS just to create this object.
		EPDEv1 epde = new EPDEv1();
		epde.setBytes(channel.createContainer("DFHEP.DESCRIPTOR").get());

		// Get event business information items
		Epde_epde__item[] epdeitems = epde.getEpde__item();
		int index = 0;

		for (Epde_epde__item epdeitem : epdeitems) {
			// Container DFHEP.DATA.nnnnn has the business information item data
			String datatype = epdeitem.getEpde__datatype();

			// Use createContainer to create a proxy container object otherwise
			// JCICS will use JNI to CICS just to create this object.
			Container data = channel.createContainer("DFHEP.DATA." + String.format("%05d", ++index));
			if (data == null) {
				// If no container has been captured for this information item,
				// ignore
				continue;
			}

			String dataString = null;
			Float dataFloat = null;
			BigInteger dataBigInteger = null;
			Long dataLong = null;
			String conversionFormat = null;
			String conversionSummary = "";
			byte[] dataToConvert = null;
			int dataToConvertLength = 0;

			if ((epdeitem.isEpde__char(datatype)) || (epdeitem.isEpde__charz(datatype))) {
				// Use CICS container API to convert captured data into UTF-8 as
				// Java may not support all CICS codepages

				try {
					dataToConvert = data.get("UTF-8");

				} catch (ContainerErrorException e) {
					// Container could not be found because it was not available
					// to be captured by CICS. Ignore.
					//
					// Details are in CICS topic "Writing a custom EP adapter"
					// that says
					// "If a data item was not available for data capture, the
					// corresponding data item container is not present
					// in the CICS event object. This situation can happen, for
					// example, when CAPTURESPEC specifies a capture data item
					// associated with an optional parameter that was not
					// present on the API command that caused the event."

				} catch (Exception e) {
					// Data captured by CICS does not match the codepage
					// specified in the event binding information source
					e.printStackTrace();
				}

			} else {
				dataToConvert = data.getNoConvert();
			}

			if (dataToConvert != null) {
				dataToConvertLength = dataToConvert.length;
			}

			if (dataToConvert == null) {
				// No data to convert

			} else if ((epdeitem.isEpde__char(datatype) || epdeitem.isEpde__charz(datatype))) {
				// Handle EPDE-DATATYPE=EPDE-CHAR
				// Handle EPDE-DATATYPE=EPDE-CHARZ
				try {
					dataString = new String(dataToConvert, "UTF-8");

				} catch (Exception e) {
					// Data captured by CICS does not match the codepage
					// specified in the event binding information source
					e.printStackTrace();
				}

				if (epdeitem.isEpde__charz(datatype)) {
					// Truncate string before first 0x00 character
					int n = dataString.indexOf(0x00);

					if (n > 0) {
						dataString = dataString.substring(0, n - 1);
					}
				}

			} else if (epdeitem.isEpde__hex(datatype)) {
				// Handle EPDE-DATATYPE=EPDE-HEX

				// No conversion required.

			} else if (epdeitem.isEpde__hexz(datatype)) {
				// Handle EPDE-DATATYPE=EPDE-HEXZ

				// Find first byte that is x'00' in byte array
				for (dataToConvertLength = 0; dataToConvertLength < dataToConvert.length; dataToConvertLength++) {

					if (dataToConvert[dataToConvertLength] == 0x00) {

						if (dataToConvertLength == 0) {
							// Nothing to copy
							dataToConvert = new byte[0];
							break;
						} else {
							// Create a new array of the correct length and copy
							// contents
							--dataToConvertLength;
							byte[] bcopy = new byte[dataToConvertLength];
							System.arraycopy(dataToConvert, 0, bcopy, 0, dataToConvertLength);
							dataToConvert = bcopy;

							break;
						}
					}
				}

			} else if (epdeitem.isEpde__uhword(datatype)) {
				// Handle EPDE-DATATYPE=EPDE-UHWORD
				dataLong = new Long(MarshallIntegerUtils.unmarshallTwoByteIntegerFromBuffer(dataToConvert, 0, true,
						MarshallIntegerUtils.SIGN_CODING_UNSIGNED_BINARY));

			} else if (epdeitem.isEpde__ufword(datatype)) {
				// Handle EPDE-DATATYPE=EPDE-UFWORD
				dataLong = new Long(MarshallIntegerUtils.unmarshallFourByteIntegerFromBuffer(dataToConvert, 0, true,
						MarshallIntegerUtils.SIGN_CODING_UNSIGNED_BINARY));

			} else if (epdeitem.isEpde__shword(datatype)) {
				// Handle EPDE-DATATYPE=EPDE-SHWORD
				dataLong = new Long(MarshallIntegerUtils.unmarshallTwoByteIntegerFromBuffer(dataToConvert, 0, true,
						MarshallIntegerUtils.SIGN_CODING_TWOS_COMPLEMENT));

			} else if (epdeitem.isEpde__sfword(datatype)) {
				// Handle EPDE-DATATYPE=EPDE-SFWORD
				dataLong = new Long(MarshallIntegerUtils.unmarshallFourByteIntegerFromBuffer(dataToConvert, 0, true,
						MarshallIntegerUtils.SIGN_CODING_TWOS_COMPLEMENT));

			} else if (epdeitem.isEpde__packed(datatype)) {
				// Handle EPDE-DATATYPE=EPDE-PACKED
				dataBigInteger = MarshallPackedDecimalUtils.unmarshallBigIntegerFromBuffer(dataToConvert, 0, dataToConvertLength);

			} else if (epdeitem.isEpde__zoned(datatype)) {
				// Handle EPDE-DATATYPE=EPDE-ZONED
				boolean sign = false;
				int signFormat = 0;
				byte first = dataToConvert[0];
				byte last = dataToConvert[dataToConvert.length - 1];

				if ((first == 0x4E) || (first == 0x60)) {
					// 0x4E is the EBCDIC character "+" and 0x60 is the EBCDIC
					// character "-"
					sign = true;
					signFormat = MarshallExternalDecimalUtils.SIGN_FORMAT_LEADING_SEPARATE;

				} else if ((last == 0x4E) || (last == 0x60)) {
					sign = true;
					signFormat = MarshallExternalDecimalUtils.SIGN_FORMAT_TRAILING_SEPARATE;

				} else if (((first & 0xF0) == 0xC0) || ((first & 0xF0) == 0xD0)) {
					sign = true;
					signFormat = MarshallExternalDecimalUtils.SIGN_FORMAT_LEADING;

				} else if (((last & 0xF0) == 0xC0) || ((last & 0xF0) == 0xD0)) {
					sign = true;
					signFormat = MarshallExternalDecimalUtils.SIGN_FORMAT_TRAILING;
				}

				dataBigInteger = MarshallExternalDecimalUtils.unmarshallBigIntegerFromBuffer(dataToConvert, 0, dataToConvertLength, sign, signFormat,
						MarshallExternalDecimalUtils.EXTERNAL_DECIMAL_SIGN_EBCDIC);

				if (logger.isLoggable(Level.FINE)) {
					if (signFormat == MarshallExternalDecimalUtils.SIGN_FORMAT_LEADING_SEPARATE)
						conversionSummary = "sign type MarshallExternalDecimalUtils.SIGN_FORMAT_LEADING_SEPARATE";

					else if (signFormat == MarshallExternalDecimalUtils.SIGN_FORMAT_TRAILING_SEPARATE)
						conversionSummary = "sign type MarshallExternalDecimalUtils.SIGN_FORMAT_TRAILING_SEPARATE";

					else if (signFormat == MarshallExternalDecimalUtils.SIGN_FORMAT_LEADING)
						conversionSummary = "sign type MarshallExternalDecimalUtils.SIGN_FORMAT_LEADING";

					else if (signFormat == MarshallExternalDecimalUtils.SIGN_FORMAT_TRAILING)
						conversionSummary = "sign type MarshallExternalDecimalUtils.SIGN_FORMAT_TRAILING";
					else
						conversionSummary = "found sign:" + sign;
				}

			} else if (epdeitem.isEpde__hexfloat(datatype)) {
				// Handle EPDE-DATATYPE=EPDE-HEXFLOAT
				if (dataToConvertLength == 4)
					dataFloat = new Float(MarshallFloatUtils.unmarshallFloatFromBuffer(dataToConvert, 0, true, MarshallFloatUtils.FLOAT_FORMAT_IBM_390_HEX,
							dataToConvertLength));
				else
					dataFloat = new Float(MarshallFloatUtils.unmarshallDoubleFromBuffer(dataToConvert, 0, true, MarshallFloatUtils.FLOAT_FORMAT_IBM_390_HEX,
							dataToConvertLength));

			} else if (epdeitem.isEpde__binfloat(datatype)) {
				// Handle EPDE-DATATYPE=EPDE-BINFLOAT
				if (dataToConvertLength == 4)
					dataFloat = new Float(MarshallFloatUtils.unmarshallFloatFromBuffer(dataToConvert, 0, true,
							MarshallFloatUtils.FLOAT_FORMAT_IEEE_EXTENTED_OS390, dataToConvertLength));
				else
					dataFloat = new Float(MarshallFloatUtils.unmarshallDoubleFromBuffer(dataToConvert, 0, true,
							MarshallFloatUtils.FLOAT_FORMAT_IEEE_EXTENTED_OS390, dataToConvertLength));

			} else if (epdeitem.isEpde__decfloat(datatype)) {
				// Handle EPDE-DATATYPE=EPDE-DECFLOAT
				if (dataToConvertLength == 4)
					dataFloat = new Float(MarshallFloatUtils.unmarshallFloatFromBuffer(dataToConvert, 0, true, MarshallFloatUtils.FLOAT_FORMAT_IEEE_DFP_IBM,
							dataToConvertLength));
				else
					dataFloat = new Float(MarshallFloatUtils.unmarshallDoubleFromBuffer(dataToConvert, 0, true, MarshallFloatUtils.FLOAT_FORMAT_IEEE_DFP_IBM,
							dataToConvertLength));
			}

			// Format as type attachment, text, numeric, or scientific

			if ((epdeitem.isEpde__hex(datatype)) || (epdeitem.isEpde__hexz(datatype))) {

				// No need to format Hex and Hexz data types as they will be
				// attachments
				dataString = null;

			} else if (epdeitem.isEpde__text(epdeitem.getEpde__formattype())) {

				// Format type is text

				if (dataString == null) {
					// Data not captured
					dataString = "";

				} else if (epdeitem.isEpde__formatlen__auto(epdeitem.getEpde__formatlen())) {
					// Format length is automatic, so just remove trailing
					// blanks
					dataString = dataString.trim();

				} else if (dataString.length() != epdeitem.getEpde__formatlen()) {
					// Format length is specified, so truncate or pad with
					// blanks
					dataString = Util.rightPad(dataString, epdeitem.getEpde__formatlen());
				}

			} else if (epdeitem.isEpde__numeric(epdeitem.getEpde__formattype())) {

				// Format type is numeric

				if (epdeitem.isEpde__formatlen__auto(epdeitem.getEpde__formatlen())) {
					// Format length is automatic
					conversionFormat = "#." + StringUtils.repeat("#", 10);

				} else if (epdeitem.isEpde__formatprec__auto(epdeitem.getEpde__formatprecision())) {
					// Format precision is automatic
					conversionFormat = StringUtils.repeat("0", epdeitem.getEpde__formatlen()) + "." + StringUtils.repeat("#", 10);

				} else {
					conversionFormat = StringUtils.repeat("0", epdeitem.getEpde__formatlen()) + "."
							+ StringUtils.repeat("#", epdeitem.getEpde__formatprecision());
				}

				if (dataLong != null) {
					dataString = (new DecimalFormat(conversionFormat)).format(dataLong);

				} else if (dataFloat != null) {
					dataString = (new DecimalFormat(conversionFormat)).format(dataFloat);

				} else if (dataBigInteger != null) {
					dataString = (new DecimalFormat(conversionFormat)).format(dataBigInteger);
				}

				if (dataString.endsWith(".")) {
					// Remove trailing "."
					dataString = dataString.substring(0, dataString.length() - 1);
				}

			} else if (epdeitem.isEpde__scientific(epdeitem.getEpde__formattype())) {

				// Format type is scientific

				if (epdeitem.isEpde__formatlen__auto(epdeitem.getEpde__formatlen())) {
					conversionFormat = "%e";

				} else if (epdeitem.isEpde__formatprec__auto(epdeitem.getEpde__formatprecision())) {
					conversionFormat = "%" + epdeitem.getEpde__formatlen() + "e";

				} else {
					conversionFormat = "%" + epdeitem.getEpde__formatlen() + "." + epdeitem.getEpde__formatprecision() + "e";
				}

				if (dataFloat != null) {
					dataString = String.format(conversionFormat, dataFloat);

				} else if (dataBigInteger != null) {
					dataString = String.format(conversionFormat, dataBigInteger);
				}
			}

			if (dataString == null) {
				// set property to an empty string and mark as resolved
				props.setPropertyAndResolved(epdeitem.getEpde__dataname().trim(), "");

				// Add as attachment
				props.setPropertyAttachment(epdeitem.getEpde__dataname().trim(), dataToConvert);

				if (logger.isLoggable(Level.FINE)) {
					conversionSummary = "added as attachment";
				}

			} else {
				props.setProperty(epdeitem.getEpde__dataname().trim(), dataString);
			}

			// Keep list of business information items so we can later retrieve them in the specific order presented
			// here, including the format length
			if (dataString == null) {
				props.putBusinessInformationItem(epdeitem.getEpde__dataname().trim(), dataToConvert.length);
			} else {
				if (epdeitem.isEpde__formatlen__auto(epdeitem.getEpde__formatlen())) {
					props.putBusinessInformationItem(epdeitem.getEpde__dataname().trim(), dataToConvertLength);
				} else {
					props.putBusinessInformationItem(epdeitem.getEpde__dataname().trim(), dataString.length());
				}
			}

			if (logger.isLoggable(Level.FINE)) {
				StringBuilder sb = new StringBuilder();

				sb.append(messages.getString("AddedProperty")).append(" ").append(epdeitem.getEpde__dataname().trim()).append(",")
						.append(messages.getString("CapturedDataLength")).append(":").append(dataToConvertLength).append(",")
						.append(messages.getString("CapturedDataInHex")).append(":0x").append(Util.getHexSummary(dataToConvert)).append(",")
						.append(messages.getString("CapturedType")).append(":").append(datatype.trim()).append(",")
						.append(messages.getString("CapturedPrecision")).append(":").append(epdeitem.getEpde__dataprecision()).append(",")
						.append(conversionSummary).append(",").append(messages.getString("FormatType")).append(":")
						.append(epdeitem.getEpde__formattype().trim()).append(",").append(messages.getString("FormatLength")).append(":")
						.append(epdeitem.isEpde__formatlen__auto(epdeitem.getEpde__formatlen()) ? "auto" : epdeitem.getEpde__formatlen()).append(",")
						.append(messages.getString("FormatPrecision")).append(":").append(epdeitem.getEpde__formatprecision());

				if (dataFloat != null) {
					sb.append(",Java Float:").append(dataFloat);
				}

				if (dataBigInteger != null) {
					sb.append(",Java BigInteger:").append(dataBigInteger);
				}

				if (dataLong != null) {
					sb.append(",Java Long:").append(dataLong);
				}

				if (conversionFormat != null) {
					sb.append(",").append(messages.getString("ConversionFormat")).append(":").append(conversionFormat);
				}

				if (dataString != null) {
					sb.append(",").append(messages.getString("ResultOfConversion")).append(":").append(dataString);
				}

				logger.fine(sb.toString());
			}
		}

		return true;
	}

	/**
	 * Add name / value properties from contents of container DFHEP.ADAPTPARM that is is mapped by COBOL copybook
	 * hlq.DFHCOB(DFHEPAPO).
	 * 
	 * @param props
	 *            - The Properties to append to
	 * @param channel
	 *            - The Channel to get the container from
	 * @return true if properties added successfully
	 */
	private static boolean addPropertiesFromDFHEP_ADAPTPARM(EmitProperties props, Channel channel) {
		if (dfhepAdaptparm) {
			EPAPv1 epapv1 = new EPAPv1();

			try {
				epapv1.setBytes(channel.createContainer("DFHEP.ADAPTPARM").get());
				props.setPropertyAndResolved("EPAP_VERSION", Long.toString(epapv1.getEpap__version()));
				props.setPropertyAndResolved("EPAP_ADAPTER_NAME", epapv1.getEpap__adapter__name().trim());
				props.setPropertyAndResolved("EPAP_RECOVER", epapv1.getEpap__recover());
				props.setPropertyAndResolved(CA1Y_RECOVERABLE, (epapv1.isEpap__recoverable(epapv1.getEpap__recover()) ? "true" : "false"));

			} catch (Exception e) {
				// If this container is not available, ignore and do not try again on subsequent requests
				dfhepAdaptparm = false;

				return false;
			}
		}

		return true;
	}

	/**
	 * Add name / value properties from contents of container DFHEP.CONTEXT.
	 * 
	 * @param props
	 *            - The properties to add to
	 * @param channel
	 *            - The channel to get the container from
	 * @return true if properties added successfully
	 */
	private static boolean addPropertiesFromDFHEP_CONTEXT(EmitProperties props, Channel channel) {
		// DFHEP.CONTEXT container is mapped by COBOL copybook
		// hlq.DFHCOB(DFHEPCXO).
		// There are two versions of this interface.
		// 1st time through assume version 2 then find out the real version and
		// save in static to speed things up for subsequent requests.
		try {
			if ((epcxVersion == 0) || (epcxVersion == 2)) {
				EPCXv2 epcxv2 = new EPCXv2();
				epcxv2.setBytes(channel.createContainer("DFHEP.CONTEXT").get());

				if (epcxVersion == 0) {
					epcxVersion = epcxv2.getEpcx__version();
				}

				if (epcxVersion == 2) {
					// EPCX version 2
					props.setPropertyAndResolved("EPCX_VERSION", Long.toString(epcxv2.getEpcx__version()));
					props.setPropertyAndResolved("EPCX_SCHEMA_VERSION", Short.toString(epcxv2.getEpcx__schema__version()));
					props.setPropertyAndResolved("EPCX_SCHEMA_RELEASE", Short.toString(epcxv2.getEpcx__schema__release()));
					props.setPropertyAndResolved("EPCX_EVENT_BINDING", epcxv2.getEpcx__event__binding().trim());
					props.setPropertyAndResolved("EPCX_CS_NAME", epcxv2.getEpcx__cs__name().trim());
					props.setPropertyAndResolved("EPCX_EBUSERTAG", epcxv2.getEpcx__ebusertag().trim());
					props.setPropertyAndResolved("EPCX_BUSINESSEVENT", epcxv2.getEpcx__businessevent().trim());
					props.setPropertyAndResolved("EPCX_NETQUAL", epcxv2.getEpcx__netqual().trim());
					props.setPropertyAndResolved("EPCX_APPLID", epcxv2.getEpcx__applid().trim());
					props.setPropertyAndResolved("EPCX_TRANID", epcxv2.getEpcx__tranid().trim());
					props.setPropertyAndResolved("EPCX_USERID", epcxv2.getEpcx__userid().trim());
					props.setPropertyAndResolved("EPCX_ABSTIME", Long.toString(epcxv2.getEpcx__abstime()));
					props.setPropertyAndResolved("EPCX_EVENT_TYPE", epcxv2.getEpcx__event__type().trim());
					props.setPropertyAndResolved("EPCX_PROGRAM", epcxv2.getEpcx__program().trim());
					props.setPropertyAndResolved("EPCX_RESP", Integer.toString(epcxv2.getEpcx__resp()));
					props.setPropertyAndResolved("EPCX_UOWID", Util.getHex(epcxv2.getEpcx__uowid().getBytes()));
				}
			}

			if (epcxVersion == 1) {
				// EPCX version 1
				EPCXv1 epcxv1 = new EPCXv1();
				epcxv1.setBytes(channel.createContainer("DFHEP.CONTEXT").get());

				props.setPropertyAndResolved("EPCX_STRUCTID", epcxv1.getEpcx__strucid().trim());
				props.setPropertyAndResolved("EPCX_VERSION", Long.toString(epcxv1.getEpcx__version()));
				props.setPropertyAndResolved("EPCX_SCHEMA_VERSION", Short.toString(epcxv1.getEpcx__schema__version()));
				props.setPropertyAndResolved("EPCX_SCHEMA_RELEASE", Short.toString(epcxv1.getEpcx__schema__release()));
				props.setPropertyAndResolved("EPCX_EVENT_BINDING", epcxv1.getEpcx__event__binding().trim());
				props.setPropertyAndResolved("EPCX_CS_NAME", epcxv1.getEpcx__cs__name().trim());
				props.setPropertyAndResolved("EPCX_EBUSERTAG", epcxv1.getEpcx__ebusertag().trim());
				props.setPropertyAndResolved("EPCX_BUSINESSEVENT", epcxv1.getEpcx__businessevent().trim());
				props.setPropertyAndResolved("EPCX_NETQUAL", epcxv1.getEpcx__netqual().trim());
				props.setPropertyAndResolved("EPCX_APPLID", epcxv1.getEpcx__applid().trim());
				props.setPropertyAndResolved("EPCX_TRANID", epcxv1.getEpcx__tranid().trim());
				props.setPropertyAndResolved("EPCX_USERID", epcxv1.getEpcx__userid().trim());
				props.setPropertyAndResolved("EPCX_ABSTIME", Long.toString(epcxv1.getEpcx__abstime()));
				props.setPropertyAndResolved("EPCX_PROGRAM", epcxv1.getEpcx__program().trim());
				props.setPropertyAndResolved("EPCX_RESP", Integer.toString(epcxv1.getEpcx__resp()));
				props.setPropertyAndResolved("EPCX_UOWID", Util.getHex(epcxv1.getEpcx__uowid().getBytes()));
			}

		} catch (Exception e) {
			e.printStackTrace();

			return false;
		}

		return true;
	}

	/**
	 * Using the key and pattern to identify a token, replace all tokens for the key value in the property.
	 * 
	 * @param key
	 *            - The string to search for tokens
	 * @param pattern
	 *            - The pattern to use to find tokens
	 * @param props
	 *            - The properties to use as the lookup table
	 * @param cicsChannel
	 *            - The channel to get containers from
	 * @param channelDFHEP
	 *            - True if the name of the current CICS channel is identified as event processing
	 * @param allowReevaluateForTokens
	 *            - reevaluated the key for tokens if at least one token was replaced with a value that could contain
	 *            another token
	 * @return true if at least one token was replaced
	 */
	private static boolean resolveTokensInKey(String key, Pattern pattern, EmitProperties props, Channel cicsChannel, boolean channelDFHEP,
			boolean allowReevaluateForTokens) {

		if ((key == null) || (pattern == null) || (props == null) || (props.getProperty(key) == null)) {
			return false;
		}

		if (props.getPropertyResolved(key)) {
			// Property is already resolved
			if (logger.isLoggable(Level.FINE)) {
				logger.fine(messages.getString("PropertyResolved") + " " + EmitProperties.getPropertySummary(props, key));
			}

			return false;
		}

		if ((logger.isLoggable(Level.FINE))) {
			logger.fine(messages.getString("PropertyResolving") + " " + EmitProperties.getPropertySummary(props, key));
		}

		// Do not change the order of the command options, as later processing relies on this order
		final String[] nomoretokensCommand = { "nomoretokens" };
		final String[] noinnertokensCommand = { "noinnertokens" };
		final String[] datetimeCommand = { "datetime=" };
		final String[] mimeCommand = { "mime=", ":to=", ":xslt=" };
		final String[] nameCommand = { "name=" };
		final String[] responsecontainerCommand = { "responsecontainer=" };
		final String[] fileCommand = { "file=", ":encoding=", ":binary", ":options=" };
		final String[] zipCommand = { "zip=", ":include=" };
		final String[] doctemplateCommand = { "doctemplate=", ":addPropertiesAsSymbols", ":binary" };
		final String[] transformCommand = { "transform=", ":xmltransform=", ":jsontransform=", ":jvmserver=" };
		final String[] ftpCommand = { "ftp=", ":server=", ":username=", ":userpassword=", ":transfer=", ":mode=", ":epsv=", ":protocol=", ":trustmgr=",
				":datatimeout=", ":proxyserver=", ":proxyusername=", ":proxypassword=" };
		final String[] hexCommand = { "hex=" };
		final String[] systemsymbolCommand = { "systemsymbol=" };
		final String[] commonBaseEventRESTCommand = { "commonbaseeventrest" };
		final String[] commonBaseEventCommandCommand = { "commonbaseevent" };
		final String[] cicsFlattenedEventCommand = { "cicsflattenedevent" };
		final String[] jsonCommand = { "json", ":properties=" };
		final String[] emitCommand = { "emit" };
		final String[] linkCommand = { "link=" };
		final String[] putcontainerCommand = { "putcontainer=" };
		final String[] htmltableCommand = { "htmltable", ":properties=", ":containers=", ":summary" };
		final String[] texttableCommand = { "texttable", ":properties=", ":containers=", ":summary" };
		final String[] trimCommand = { "trim" };

		final int maxTimesToResolveTokens = 10;

		boolean tokenReplaced = false;
		boolean allowPropertyToBeReevaluatedForTokens = true;
		String putContainer = null;

		// Indicate the property has been resolved at this point to prevent recursion resolving tokens within the
		// property
		props.setPropertyResolved(key, true);

		// As a token's replacement may itself contain tokens we need to iterate token searching, up to a maximum number
		// of times
		for (int i = 0; (i < maxTimesToResolveTokens) && (allowPropertyToBeReevaluatedForTokens); i++) {
			boolean reevaluateForTokens = false;
			Matcher matcher = pattern.matcher(props.getProperty(key));
			StringBuffer buffer = new StringBuffer();

			while (matcher.find()) {
				String token = matcher.group(1).trim();

				if (logger.isLoggable(Level.FINE)) {
					logger.fine(Emit.messages.getString("ResolvingToken") + " " + token);
				}

				if (token.startsWith(nomoretokensCommand[0])) {
					// Do not process any more tokens in the key
					tokenReplaced = true;
					matcher.appendReplacement(buffer, "");
					reevaluateForTokens = false;
					break;

				} else if (token.startsWith(noinnertokensCommand[0])) {
					// Processing all tokens in this property but prevent re-evaluation tokens in resolve tokens
					// eg. token is "noinntertokens"
					tokenReplaced = true;
					matcher.appendReplacement(buffer, "");
					allowPropertyToBeReevaluatedForTokens = false;

				} else if (token.startsWith(transformCommand[0])) {
					tokenReplaced = true;
					matcher.appendReplacement(buffer, "");
					String[] options = Util.getOptions(token, transformCommand);

					if (isCICS == false) {
						// if CICS API not available, ignore this token

					} else if (options[1] != null) {
						// eg. {transform=property:xmltranform=resource}
						try {
							Container inputContainer = cicsChannel.createContainer("CA1Y-TRANSFORM-I");
							Container outputContainer = cicsChannel.createContainer("CA1Y-TRANSFORM-O");

							if (props.getPropertyAttachment(options[0]) == null) {
								// property is text
								inputContainer.putString(props.getProperty(options[0]));

							} else {
								// property is binary
								inputContainer.put(props.getPropertyAttachment(options[0]));
							}

							XmlTransform xmltransform = new XmlTransform(options[1]);
							TransformInput transforminput = new TransformInput();
							transforminput.setChannel(cicsChannel);
							transforminput.setDataContainer(inputContainer);
							transforminput.setXmlContainer(outputContainer);
							transforminput.setXmltransform(xmltransform);

							Transform.dataToXML(transforminput);

							buffer.append(new String(outputContainer.get(Emit.defaultCharset)));

							inputContainer.delete();
							outputContainer.delete();

						} catch (Exception e) {
							e.printStackTrace();
						}

					} else if (options[2] != null) {
						// eg. {transform=propertyName:jsontransform=jsontransfrmResource:jvmserver=jvmResource}
						try {
							Container inputContainer = cicsChannel.createContainer("DFHJSON-DATA");
							inputContainer.put(props.getPropertyAttachment(options[0]));

							inputContainer = cicsChannel.createContainer("DFHJSON-TRANSFRM");
							inputContainer.putString(options[2]);

							if (options[3] != null) {
								inputContainer = cicsChannel.createContainer("DFHJSON-JVMSERVR");
								inputContainer.putString(options[3]);
							}

							Program p = new Program();
							p.setName("DFHJSON");
							p.link(cicsChannel);

							Container outputContainer = (cicsChannel).createContainer("DFHSON-JSON");
							buffer.append(outputContainer.get("UTF-8"));

							inputContainer.delete();
							outputContainer.delete();

						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				} else if (token.startsWith(mimeCommand[0])) {
					// Store the MIME type in the property
					// eg. token is "mime=application/octet-stream"
					// eg. token is "mime=application/xml"
					// eg. token is "mime=text/xsl:to=application/pdf"
					// eg. token is "mime=text/xml:to=application/pdf:xslt=xsltProperty"
					tokenReplaced = true;
					matcher.appendReplacement(buffer, "");
					String[] options = Util.getOptions(token, mimeCommand);

					props.setPropertyMime(key, options[0]);

					if (options[1] != null) {
						props.setPropertyMimeTo(key, options[1]);
					}

					if (options[2] != null) {
						props.setPropertyXSLT(key, options[2]);
					}

				} else if (token.startsWith(nameCommand[0])) {
					// Store the name in the property
					// eg. token is "name=My Picture"
					tokenReplaced = true;
					matcher.appendReplacement(buffer, "");
					String[] options = Util.getOptions(token, nameCommand);
					props.setPropertyAlternateName(key, options[0]);

				} else if (token.startsWith(datetimeCommand[0])) {
					// For tokens that start with DATETIME_PREFIX, replace the token with the resolved date and time
					// format
					// eg. token is "datetime=yyyy.MM.dd G 'at' HH:mm:ss z"
					tokenReplaced = true;
					matcher.appendReplacement(buffer, "");
					String[] options = Util.getOptions(token, datetimeCommand);

					Date date = null;
					if (props.containsKey("EPCX_ABSTIME")) {
						date = Util.getDateFromAbstime(props.getProperty("EPCX_ABSTIME"));

					} else {
						date = new Date();
					}

					if (options[0].isEmpty()) {
						buffer.append(date.toString());

					} else {
						buffer.append((new SimpleDateFormat(options[0])).format(date));
					}

				} else if (token.startsWith(zipCommand[0])) {
					// Store the zip in the property
					// eg. token is "zip="
					// eg. token is "zip=MyZip.zip"
					// eg. token is "zip=MyZip.zip:include=property1"
					// eg. token is "zip=MyZip.zip:include=property1,property2"
					tokenReplaced = true;
					matcher.appendReplacement(buffer, "");
					String[] options = Util.getOptions(token, zipCommand);

					if (options[0].isEmpty()) {
						props.setPropertyZip(key, key + ".zip");

					} else {
						props.setPropertyZip(key, options[0]);
					}

					if (options[1] != null) {
						props.setPropertyZipInclude(key, options[1]);

					} else {
						props.setPropertyZipInclude(key, key);
					}

				} else if (token.startsWith(fileCommand[0])) {
					// Replace the token with the content of the file
					// eg. token is "file=//dataset"
					// eg. token is "file=//dataset:encoding=Cp1047"
					// eg. token is "file=//dataset:binary"
					tokenReplaced = true;
					matcher.appendReplacement(buffer, "");
					String[] options = Util.getOptions(token, fileCommand);

					if (options[0].startsWith("//")) {
						ZFile zf = null;

						if (options[2] != null) {

							try {
								// Read dataset in binary using IBM JZOS library
								zf = new ZFile(options[0], (options[3] == null) ? "rb" : options[3]);

								if (props.getPropertyAlternateName(key) == null) {
									props.setPropertyAlternateName(key, zf.getActualFilename());
								}

								if (props.getPropertyMime(key) == null) {
									props.setPropertyMimeDefault(key, zf.getActualFilename());
								}

								props.setPropertyAttachment(key, IOUtils.toByteArray(zf.getInputStream()));

							} catch (Exception e) {
								e.printStackTrace();

							} finally {
								if (zf != null) {
									try {
										zf.close();
									} catch (Exception e2) {
									}
								}
							}

						} else {
							try {
								// Read dataset in text using IBM JZOS library
								zf = new ZFile(options[0], (options[3] == null) ? "r" : options[3]);

								if (props.getPropertyAlternateName(key) == null) {
									props.setPropertyAlternateName(key, zf.getActualFilename());
								}

								buffer.append(IOUtils.toString(zf.getInputStream(), (options[1] == null) ? ZUtil.getDefaultPlatformEncoding() : options[1]));
								reevaluateForTokens = true;

							} catch (Exception e) {
								e.printStackTrace();

							} finally {
								if (zf != null) {
									try {
										zf.close();
									} catch (Exception e2) {
									}
								}
							}
						}

					} else {
						File f = null;

						if (options[2] != null) {
							try {
								f = new File(options[0]);
								// Read zFS file as binary

								if (props.getPropertyAlternateName(key) == null) {
									props.setPropertyAlternateName(key, f.getName());
								}

								if (props.getPropertyMime(key) == null) {
									props.setPropertyMimeDefault(key, f.getName());
								}

								props.setPropertyAttachment(key, FileUtils.readFileToByteArray(f));

							} catch (Exception e) {
								e.printStackTrace();
							}

						} else {
							try {
								// Read zFS file as text
								f = new File(options[0]);

								if (props.getPropertyAlternateName(key) == null) {
									props.setPropertyAlternateName(key, f.getName());
								}

								buffer.append(FileUtils.readFileToString(f, options[1]));
								reevaluateForTokens = true;

							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}

				} else if (token.startsWith(doctemplateCommand[0])) {
					// Replace the token with the contents of the CICS document template resource
					// eg. token is "doctemplate=MyTemplate"
					// eg. token is "doctemplate=MyTemplate:addPropertiesAsSymbols"
					// eg. token is "doctemplate=MyTemplate:binary"
					tokenReplaced = true;
					matcher.appendReplacement(buffer, "");
					String[] options = Util.getOptions(token, doctemplateCommand);

					if (options[2] == null) {
						try {
							// Read doctemplate as text
							if (props.getPropertyAlternateName(key) == null) {
								props.setPropertyAlternateName(key, options[0]);
							}

							Document document = new Document();

							if (options[1] != null) {
								// Add all properties except the current property as symbols to document
								Enumeration<?> e = props.propertyNamesOrdered();

								while (e.hasMoreElements()) {
									String key2 = (String) e.nextElement();

									// Only add property if it is not the current key and is not private
									if ((key.equals(key2) == false) && (props.getPropertyPrivacy(key2) == false)) {
										try {
											if ((props.getPropertyResolved(key2) == false) && (allowPropertyToBeReevaluatedForTokens)) {
												// resolve the token before attempting to use it
												resolveTokensInKey(key2, pattern, props, cicsChannel, channelDFHEP, allowReevaluateForTokens);
											}

											document.addSymbol(key2, props.getProperty(key2));

										} catch (Exception e2) {
											// Continue even if there are errors adding a symbol
										}
									}
								}
							}

							document.createTemplate(options[0]);
							buffer.append(new String(document.retrieve("UTF-8", true), "UTF-8"));
							reevaluateForTokens = true;

						} catch (Exception e) {
							e.printStackTrace();
						}

					} else {
						try {
							// Read doctemplate as binary
							if (props.getPropertyAlternateName(key) == null) {
								props.setPropertyAlternateName(key, options[0]);
							}

							if (props.getPropertyMime(key) == null) {
								props.setPropertyMimeDefault(key, options[0]);
							}

							Document document = new Document();
							document.createTemplate(options[0]);
							props.setPropertyAttachment(key, document.retrieve());

						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				} else if (token.startsWith(commonBaseEventRESTCommand[0])) {
					tokenReplaced = true;
					matcher.appendReplacement(buffer, "");
					buffer.append(getCommonBaseEventREST(props));
					reevaluateForTokens = true;

					if (props.getPropertyMime(key) == null) {
						props.setPropertyMime(key, MIME_XML);
					}

				} else if (token.startsWith(commonBaseEventCommandCommand[0])) {
					tokenReplaced = true;
					matcher.appendReplacement(buffer, "");
					buffer.append(getCommonBaseEvent(props));
					reevaluateForTokens = true;

					if (props.getPropertyMime(key) == null) {
						props.setPropertyMime(key, MIME_XML);
					}

				} else if (token.startsWith(cicsFlattenedEventCommand[0])) {
					tokenReplaced = true;
					matcher.appendReplacement(buffer, "");
					buffer.append(getCicsFlattenedEvent(props));
					reevaluateForTokens = true;

				} else if (token.startsWith(jsonCommand[0])) {
					tokenReplaced = true;
					matcher.appendReplacement(buffer, "");
					String options[] = Util.getOptions(token, jsonCommand);
					buffer.append(getJson(props, options[1]));
					reevaluateForTokens = true;

					if (props.getPropertyMime(key) == null) {
						props.setPropertyMime(key, MIME_JSON);
					}

				} else if (token.startsWith(htmltableCommand[0])) {
					tokenReplaced = true;
					matcher.appendReplacement(buffer, "");
					String options[] = Util.getOptions(token, htmltableCommand);
					buffer.append(getHtmlTable(props, options[1], options[2], options[3], isCICS));

				} else if (token.startsWith(texttableCommand[0])) {
					tokenReplaced = true;
					matcher.appendReplacement(buffer, "");
					String options[] = Util.getOptions(token, texttableCommand);
					buffer.append(getTextTable(props, options[1], options[2], options[3]));

				} else if (token.startsWith(ftpCommand[0])) {
					tokenReplaced = true;
					matcher.appendReplacement(buffer, "");
					String options[] = Util.getOptions(token, ftpCommand);
					String defaultanonymouspassword = null;

					if (isCICS) {
						defaultanonymouspassword = channelDFHEP ? props.getProperty("EPCX_USERID") : props.getProperty("TASK_USERID");

						if (defaultanonymouspassword != null) {
							try {
								defaultanonymouspassword = (new StringBuilder(String.valueOf(defaultanonymouspassword))).append("@")
										.append(InetAddress.getLocalHost().getHostName()).toString();
							} catch (Exception _ex) {
							}
						}
					}

					byte b[] = getFileUsingFTP(options[0], options[1], options[2], options[3], options[4], options[5], options[6], options[7], options[8],
							options[9], options[10], options[11], options[12], defaultanonymouspassword);

					if (b != null) {
						if ("binary".equalsIgnoreCase(options[4])) {
							String filename = FilenameUtils.getName(options[0]);

							if (props.getPropertyAlternateName(key) == null) {
								props.setPropertyAlternateName(key, filename);
							}

							if (props.getPropertyMime(key) == null) {
								props.setPropertyMimeDefault(key, filename);
							}

							props.setPropertyAttachment(key, b);

						} else {
							try {
								buffer.append(new String(b, "US-ASCII"));
								reevaluateForTokens = true;

							} catch (Exception _ex) {
								// Ignore
							}
						}
					}

				} else if (token.startsWith(responsecontainerCommand[0])) {
					// Store the name in the property
					// eg. token is "responsecontainer="
					// eg. token is "responsecontainer=MyContainer"
					tokenReplaced = true;
					matcher.appendReplacement(buffer, "");
					String[] options = Util.getOptions(token, responsecontainerCommand);
					props.setPropertyReturnContainer(key, (options[0].isEmpty()) ? key : options[0]);

				} else if (token.startsWith(hexCommand[0])) {
					tokenReplaced = true;
					matcher.appendReplacement(buffer, "");
					String options[] = Util.getOptions(token, hexCommand);

					if (props.getProperty(options[0]) != null) {
						buffer.append(Util.getHex(props.getProperty(options[0]).getBytes()));

					} else {
						if (props.getPropertyAttachment(options[0]) != null)
							buffer.append(Util.getHex(props.getPropertyAttachment(options[0])));
					}

				} else if (token.startsWith(emitCommand[0])) {
					// Set this property to be emitted
					tokenReplaced = true;
					matcher.appendReplacement(buffer, "");
					props.putBusinessInformationItem(key, 0);

				} else if (token.startsWith(systemsymbolCommand[0])) {
					// Get the z/OS system symbol
					tokenReplaced = true;
					matcher.appendReplacement(buffer, "");
					String options[] = Util.getOptions(token, systemsymbolCommand);

					try {
						buffer.append(Util.getSystemSymbol(options[0]));
					} catch (Exception e) {
					}

				} else if (token.startsWith(linkCommand[0])) {
					// Link to CICS program
					tokenReplaced = true;
					matcher.appendReplacement(buffer, "");
					String options[] = Util.getOptions(token, linkCommand);

					if (isCICS) {
						// Only execute if CICS API is available

						if (logger.isLoggable(Level.FINE)) {
							logger.fine(messages.getString("LinkTo") + " " + options[0]);
						}

						Program p = new Program();
						p.setName(options[0]);

						try {
							p.link(cicsChannel);

						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				} else if (token.startsWith(putcontainerCommand[0])) {
					// Put the value of this property into a CICS container once the tokens have been resolve
					tokenReplaced = true;
					matcher.appendReplacement(buffer, "");
					String options[] = Util.getOptions(token, putcontainerCommand);

					putContainer = options[0];

				} else if (token.startsWith(trimCommand[0])) {
					tokenReplaced = true;
					matcher.appendReplacement(buffer, "");

					// Remove leading and training spaces from the buffer up to the point of the token
					buffer = new StringBuffer(buffer.toString().trim());

				} else if (props.containsKey(token)) {
					// The token refers to a property
					tokenReplaced = true;
					matcher.appendReplacement(buffer, "");

					// Do not copy property if it is the current key or it is marked as private
					if ((key.equals(token) == false) && (props.getPropertyPrivacy(key) == false)) {

						if ((props.getPropertyResolved(token) == false) && (allowPropertyToBeReevaluatedForTokens)) {
							// Resolve the tokens in the property before attempting to use it
							resolveTokensInKey(token, pattern, props, cicsChannel, channelDFHEP, allowReevaluateForTokens);
						}

						if (props.getPropertyAttachment(token) == null) {
							// Copy the tokens' property
							buffer.append(props.getProperty(token));

						} else {
							// Copy the tokens' property attachment

							if (props.getPropertyAttachment(key) == null) {
								// current property does not have an attachment
								props.setPropertyAttachment(key, Arrays.copyOf(props.getPropertyAttachment(token), props.getPropertyAttachment(token).length));

							} else {
								// Current property has an attachment, so append the tokens' attachment
								ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

								try {
									outputStream.write(props.getPropertyAttachment(key));
									outputStream.write(props.getPropertyAttachment(token));

								} catch (Exception e) {
								}

								props.setPropertyAttachment(key, outputStream.toByteArray());
							}
						}

						props.setPropertyAlternateName(key, props.getPropertyAlternateName(token));
					}

				} else if (System.getProperty(token) != null) {
					// The token refers to a system property
					tokenReplaced = true;
					matcher.appendReplacement(buffer, "");
					buffer.append(System.getProperty(token));

				} else if (isCICS && (token.getBytes().length >= CONTAINER_NAME_LENGTH_MIN) && (token.getBytes().length <= CONTAINER_NAME_LENGTH_MAX)) {
					// If this is not an EP event and the token is a max of 16 characters, attempt to replace token with
					// contents of the CICS container named by the token in current channel
					tokenReplaced = true;
					matcher.appendReplacement(buffer, "");

					if (logger.isLoggable(Level.FINE)) {
						logger.fine(messages.getString("GetContainer") + " " + token);
					}

					try {
						Container container = (cicsChannel).createContainer(token);

						try {
							// Assume container is of type CHAR and get CICS to convert it
							buffer.append(container.getString());
							reevaluateForTokens = true;

						} catch (CodePageErrorException e) {
							// As container could not be converted, assume it is of type BIT and copy the contents into
							// an attachment

							if (props.getPropertyAttachment(key) == null) {
								props.setPropertyAttachment(key, container.get());

							} else {
								// Append the property named by token to the existing attachment
								ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

								try {
									outputStream.write(props.getPropertyAttachment(key));
									outputStream.write(container.get());

								} catch (Exception e2) {
								}

								props.setPropertyAttachment(key, outputStream.toByteArray());
							}
						}

						if (props.getPropertyAlternateName(key) == null) {
							props.setPropertyAlternateName(key, token);
						}

					} catch (Exception e) {
						if (logger.isLoggable(Level.FINE)) {
							logger.fine(messages.getString("GetContainerError") + " " + token);
						}
					}

				} else {
					// Token could not be resolved, so just remove it
					matcher.appendReplacement(buffer, "");

					if (logger.isLoggable(Level.FINE)) {
						logger.fine(messages.getString("UnResolvedToken") + " " + token);
					}
				}
			}

			matcher.appendTail(buffer);
			props.setProperty(key, buffer.toString());

			if ((reevaluateForTokens == false) || (allowReevaluateForTokens == false)) {
				break;
			}
		}

		if ((isCICS) && (putContainer != null) && (putContainer.length() >= CONTAINER_NAME_LENGTH_MIN) && (putContainer.length() <= CONTAINER_NAME_LENGTH_MAX)) {
			try {
				Container container = cicsChannel.createContainer(putContainer);

				if (props.getPropertyAttachment(key) == null) {
					// Create a container of type CHAR.
					// Should use container.putString but this was only added in
					// CICS TS V5.1
					if (logger.isLoggable(Level.FINE)) {
						logger.fine(messages.getString("PutContainerChar") + " " + putContainer);
					}

					container.putString(props.getProperty(key));

				} else {
					// Creates a container of type BIT
					if (logger.isLoggable(Level.FINE)) {
						logger.fine(messages.getString("PutContainerBit") + " " + putContainer);
					}

					container.put(props.getPropertyAttachment(key));
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (logger.isLoggable(Level.FINE) && (tokenReplaced)) {
			logger.fine(messages.getString("PropertyReplaced") + " " + EmitProperties.getPropertySummary(props, key));
		}

		return tokenReplaced;
	}

	/**
	 * Return the CICS flattened event format in fix-width fields for the business information items in the properties.
	 * 
	 * @param props
	 *            - the properties to use
	 * @return block of text with fixed-width fields conforming to CICS TS COBOL copybook DFHEPFEO.cpy
	 */
	private static String getCicsFlattenedEvent(EmitProperties props) {
		StringBuilder sb = new StringBuilder();

		// Add header equivalent to CICS TS COBOL copybook DFHEPFEO.cpy
		sb.append("EPFE0002").append(Util.rightPad(props.getProperty("EPCX_EVENT_BINDING"), 32)).append(Util.rightPad(props.getProperty("EPCX_EBUSERTAG"), 8))
				.append(Util.rightPad(props.getProperty("EPCX_BUSINESSEVENT"), 32)).append(Util.rightPad(props.getProperty("EPCX_UOWID"), 54))
				.append(Util.rightPad(props.getProperty("EPCX_NETQUAL") + "." + props.getProperty("EPCX_APPLID"), 17))
				.append(Util.rightPad(rfc3339.format(new Date()), 29)).append(Util.rightPad(props.getProperty("EPCX_CS_NAME"), 32))
				.append(Util.rightPad(" ", 16));

		// Add business information items in the order defined in the event binding
		for (Map.Entry<String, Integer> entry : props.getBusinessInformationItems().entrySet()) {
			if (entry.getValue() == 0) {
				sb.append(props.getProperty(entry.getKey()));

			} else {
				sb.append(Util.rightPad(props.getProperty(entry.getKey()), entry.getValue()));
			}
		}

		return sb.toString();
	}

	/**
	 * Return an HTML document containing a table of properties and containers that match the provided regular
	 * expressions.
	 * 
	 * @param props
	 *            - properties to use
	 * @param propertiesExpression
	 *            - regex of properties to include
	 * @param containersExpression
	 *            - regex of containers to include
	 * @param summary
	 * @return HTML document
	 */
	private static String getHtmlTable(EmitProperties props, String propertiesExpression, String containersExpression, String summary, boolean isCICS) {

		if ((propertiesExpression == null) && (containersExpression == null)) {
			// if neither properties or containers expression are provided, default both
			propertiesExpression = "";
			containersExpression = "";
		}

		// Return an HTML document as a string containing a table of the property keys and values
		StringBuilder sb = new StringBuilder();
		sb.append("<!DOCTYPE html><html><body>");

		if (propertiesExpression != null) {
			if (propertiesExpression.length() == 0) {
				// Default to all CICS containers
				propertiesExpression = ".*";
			}

			sb.append("<table border='1' cellspacing='0' cellpadding='2'><thead><tr><th>Property</th><th>Value</th><th>MIME type</th><th>Attachment</hd></tr></thead><tbody>");

			Enumeration<?> e = props.propertyNamesOrdered();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();

				try {
					if (!key.matches(propertiesExpression)) {
						continue;
					}

				} catch (Exception e2) {
					// ignore problems with regular expression
				}

				sb.append("<tr><td>").append(org.apache.commons.lang3.StringEscapeUtils.escapeHtml4(key)).append("</td><td>");

				if ((props.getPropertyPrivacy(key) == false) && (props.getProperty(key) != null)) {
					sb.append(org.apache.commons.lang3.StringEscapeUtils.escapeHtml4(props.getProperty(key)));
				}

				sb.append("</td><td>");

				if (props.getPropertyMime(key) != null) {
					sb.append(props.getPropertyMime(key));
				}

				sb.append("</td><td><pre>");

				if ((props.getPropertyPrivacy(key) == false) && (props.getPropertyAttachment(key) != null)) {
					if (summary == null) {
						sb.append(Util.getHexDump(props.getPropertyAttachment(key), 32, "<br/>"));

					} else {
						sb.append(Util.getHexSummary(props.getPropertyAttachment(key)));
					}
				}

				sb.append("</pre></td></tr>");
			}

			sb.append("</tbody></table><p/>");
		}

		if ((isCICS) && (containersExpression != null)) {
			if (containersExpression.length() == 0) {
				// Default to all CICS containers
				containersExpression = ".*";
			}

			sb.append("<table border='1' cellspacing='0' cellpadding='2'><thead><tr><th>Container</th><th>Value in hex</th></tr></thead><tbody>");
			Task t = Task.getTask();
			ContainerIterator ci = t.containerIterator();
			while ((ci != null) && (ci.hasNext())) {
				Container container = ci.next();
				String key = container.getName().trim();

				try {
					if (!key.matches(containersExpression)) {
						continue;
					}

				} catch (Exception e) {
					// Ignore issues with regular expression
				}

				sb.append("<tr><td>").append(org.apache.commons.lang3.StringEscapeUtils.escapeHtml4(key)).append("</td><td><pre>");

				try {
					if (summary == null) {
						sb.append(Util.getHexDump(container.get(), 32, "<br/>"));

					} else {
						sb.append(Util.getHexSummary(container.get()));
					}

				} catch (Exception e1) {
					// ignore
				}

				sb.append("</pre></td><tr>");
			}

			sb.append("</tbody></table>");
		}

		sb.append("</body></html>");

		return sb.toString();
	}

	/**
	 * Return an text document containing a table of properties and containers that match the provided regular
	 * expressions.
	 * 
	 * @param props
	 *            - properties to use
	 * @param propertiesExpression
	 *            - regex of properties to include
	 * @param containersExpression
	 *            - regex of containers to include
	 * @param summary
	 * @return character table
	 */
	private static String getTextTable(EmitProperties props, String propertiesExpression, String containersExpression, String summary) {

		if ((propertiesExpression == null) && (containersExpression == null)) {
			// if neither properties or containers expression are provided, default both
			propertiesExpression = "";
			containersExpression = "";
		}

		StringBuilder sb = new StringBuilder();

		if (propertiesExpression != null) {
			if (propertiesExpression.length() == 0) {
				// Default to all CICS containers
				propertiesExpression = ".*";
			}

			Enumeration<?> e = props.propertyNamesOrdered();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();

				try {
					if (!key.matches(propertiesExpression)) {
						continue;
					}
				} catch (Exception e2) {
					// ignore problems with regular expression
				}

				String title = "Property:" + key;
				sb.append(title).append("\n").append(StringUtils.repeat("=", title.length())).append("\n");

				sb.append("Value:");
				if ((props.getPropertyPrivacy(key) == false) && (props.getProperty(key) != null)) {
					sb.append(props.getProperty(key)).append("\n");
				} else {
					sb.append("<hidden>");
				}
				sb.append("\n");

				if (props.getPropertyMime(key) != null) {
					sb.append("Mime:").append(props.getPropertyMime(key)).append("\n");
				}

				if ((props.getPropertyPrivacy(key) == false) && (props.getPropertyAttachment(key) != null)) {
					if (summary == null) {
						sb.append(Util.getHexDump(props.getPropertyAttachment(key), 32, "\n"));
					} else {
						sb.append(Util.getHexSummary(props.getPropertyAttachment(key)));
					}
				}

				sb.append("\n");
			}
		}

		if (containersExpression != null) {
			if (containersExpression.length() == 0) {
				// Default to all CICS containers
				containersExpression = ".*";
			}

			Task t = Task.getTask();
			ContainerIterator ci = t.containerIterator();
			while (ci.hasNext()) {
				Container container = ci.next();
				String key = container.getName().trim();

				try {
					if (!key.matches(containersExpression)) {
						continue;
					}

				} catch (Exception e) {
					// Ignore issues with regular expression
				}

				String title = "Container:" + key;
				sb.append(title).append("\n").append(StringUtils.repeat("=", title.length())).append("\n");

				try {
					if (summary == null) {
						sb.append(Util.getHexDump(container.get(), 32, "\n"));

					} else {
						sb.append(Util.getHexSummary(container.get()));
					}

				} catch (Exception e1) {
					// ignore
				}

				sb.append("\n");
			}
		}

		return sb.toString();
	}

	/**
	 * Return a JSON representation of the business information items in the CICS event.
	 * 
	 * @param props
	 *            - the properties table to use
	 * @return a JSON document, or empty document if errors when generating JSON
	 */
	private static String getJson(EmitProperties props, String propertiesExpression) {
		final ObjectMapper mapper = new ObjectMapper();

		try {
			return mapper.writeValueAsString(props.getBusinessInformationItemsValues(propertiesExpression));

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	/**
	 * Return an XML representation of a CICS event referred to as the common base event format.
	 * 
	 * @param props
	 *            - the properties table to use
	 * @return XML document
	 */
	private static String getCommonBaseEvent(EmitProperties props) {
		StringBuilder sb = new StringBuilder();

		sb.append(XML_HEADER)
				.append("<cbe:CommonBaseEvent xmlns:cbe=\"http://www.ibm.com/AC/commonbaseevent1_0_1\" ")
				.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" version=\"1.0.1\" creationTime=\"")
				.append(rfc3339.format(new Date()))
				.append("\"><cbe:sourceComponentId component=\"")
				.append(messages.getString("Name"))
				.append("#")
				.append(messages.getString("Version"))
				.append("\" componentIdType=\"ProductName\" executionEnvironment=\"IBM z/OS\" instanceId=\"")
				.append(props.getProperty("EPCX_NETQUAL"))
				.append(".")
				.append(props.getProperty("EPCX_APPLID"))
				.append("\" location=\"")
				.append(Util.getSystemSymbol("&SYSNAME."))
				.append("\" locationType=\"Hostname\" subComponent=\"CICS EP\" componentType=\"http://www.ibm.com/xmlns/prod/cics/eventprocessing\" />")
				.append("<cbe:situation categoryName=\"OtherSituation\"><cbe:situationType xsi:type=\"OtherSituation\" reasoningScope=\"EXTERNAL\"><CICSApplicationEvent /></cbe:situationType></cbe:situation>")
				.append(getCommonBaseEventSection(props)).append("</cbe:CommonBaseEvent>");

		return sb.toString();
	}

	/**
	 * Return an XML representation of a CICS event referred to as the common base event REST format.
	 * 
	 * @param props
	 *            - the properties table to use
	 * @return XML document
	 */
	private static String getCommonBaseEventREST(EmitProperties props) {
		StringBuilder sb = new StringBuilder();
		return sb.append(XML_HEADER).append(getCommonBaseEventSection(props)).toString();
	}

	/**
	 * Return an XML representation of elements that are common to both the common base event and common base event REST
	 * format.
	 * 
	 * @param props
	 *            - the properties table to use
	 * @return XML fragment
	 */
	private static StringBuilder getCommonBaseEventSection(EmitProperties props) {
		StringBuilder sb = new StringBuilder();

		sb.append("<cics:event xmlns:cics=\"http://www.ibm.com/xmlns/prod/cics/events/CBE\">");

		sb.append("<cics:context-info>");

		sb.append("<cics:eventname>").append(props.getProperty("EPCX_BUSINESSEVENT")).append("</cics:eventname>");
		sb.append("<cics:usertag>").append(props.getProperty("EPCX_EBUSERTAG")).append("</cics:usertag>");
		sb.append("<cics:networkapplid>").append(props.getProperty("EPCX_NETQUAL")).append(".").append(props.getProperty("EPCX_APPLID"))
				.append("</cics:networkapplid>");
		sb.append("<cics:timestamp>").append(rfc3339.format(new Date())).append("</cics:timestamp>");
		sb.append("<cics:bindingname>").append(props.getProperty("EPCX_EVENT__BINDING")).append("</cics:bindingname>");
		sb.append("<cics:capturespecname>").append(props.getProperty("EPCX_CS__NAME")).append("</cics:capturespecname>");
		sb.append("<cics:UOWid>").append(props.getProperty("EPCX_UOWID")).append("</cics:UOWid>");

		sb.append("</cics:context-info>");

		sb.append("<cics:payload-data>");

		sb.append("<data:payload xmlns:data=\"http://www.ibm.com/prod/cics/").append(props.getProperty("EPCX_EBUSERTAG")).append("/")
				.append(props.getProperty("EPCX_BUSINESSEVENT")).append("\">");

		for (Map.Entry<String, Integer> entry : props.getBusinessInformationItems().entrySet()) {
			String key = entry.getKey();

			sb.append("<data:").append(key).append(">");

			if (props.getPropertyAttachment(key) == null) {
				sb.append(props.getProperty(key));

			} else {
				sb.append(DatatypeConverter.printBase64Binary(props.getPropertyAttachment(key)));
			}

			sb.append("</data:").append(key).append(">");
		}

		sb.append("</data:payload>");

		sb.append("</cics:payload-data>");

		sb.append("</cics:event>");

		return sb;
	}

	/**
	 * Get the contents of a file from an FTP server.
	 * 
	 * @param filename
	 *            -
	 * @param server
	 *            -
	 * @param useraname
	 *            -
	 * @param userpassword
	 *            -
	 * @param transfer
	 *            -
	 * @param mode
	 *            -
	 * @param epsv
	 *            -
	 * @param protocol
	 *            -
	 * @param trustmgr
	 *            -
	 * @param datatimeout
	 *            -
	 * @param proxyserver
	 *            -
	 * @param proxyusername
	 *            -
	 * @param proxypassword
	 *            -
	 * @param anonymouspassword
	 *            -
	 * @return byte[] representing the retrieved file, null otherwise.
	 */
	private static byte[] getFileUsingFTP(String filename, String server, String username, String userpassword, String transfer, String mode, String epsv,
			String protocol, String trustmgr, String datatimeout, String proxyserver, String proxyusername, String proxypassword, String anonymouspassword) {

		FTPClient ftp;

		if (filename == null || server == null)
			return null;
		int port = 0;
		if (server != null) {
			String parts[] = server.split(":");
			if (parts.length == 2) {
				server = parts[0];
				try {
					port = Integer.parseInt(parts[1]);
				} catch (Exception _ex) {
				}
			}
		}

		int proxyport = 0;
		if (proxyserver != null) {
			String parts[] = proxyserver.split(":");

			if (parts.length == 2) {
				proxyserver = parts[0];

				try {
					proxyport = Integer.parseInt(parts[1]);

				} catch (Exception _ex) {
				}
			}
		}

		if (username == null) {
			username = "anonymous";

			if (userpassword == null)
				userpassword = anonymouspassword;
		}

		if (protocol == null) {
			if (proxyserver != null)
				ftp = new FTPHTTPClient(proxyserver, proxyport, proxyusername, proxypassword);
			else
				ftp = new FTPClient();

		} else {
			FTPSClient ftps = null;

			if ("true".equalsIgnoreCase(protocol)) {
				ftps = new FTPSClient(true);

			} else if ("false".equalsIgnoreCase(protocol)) {
				ftps = new FTPSClient(false);

			} else if (protocol != null) {
				String parts[] = protocol.split(",");

				if (parts.length == 1)
					ftps = new FTPSClient(protocol);
				else
					ftps = new FTPSClient(parts[0], Boolean.parseBoolean(parts[1]));
			}

			ftp = ftps;

			if ("all".equalsIgnoreCase(trustmgr)) {
				ftps.setTrustManager(TrustManagerUtils.getAcceptAllTrustManager());

			} else if ("valid".equalsIgnoreCase(trustmgr)) {
				ftps.setTrustManager(TrustManagerUtils.getValidateServerCertificateTrustManager());

			} else if ("none".equalsIgnoreCase(trustmgr)) {
				ftps.setTrustManager(null);
			}
		}

		if (datatimeout != null) {
			try {
				ftp.setDataTimeout(Integer.parseInt(datatimeout));

			} catch (Exception _ex) {
				ftp.setDataTimeout(-1);
			}
		}

		if (port <= 0) {
			port = ftp.getDefaultPort();
		}

		if (logger.isLoggable(Level.FINE)) {
			StringBuilder sb = new StringBuilder();

			sb.append(Emit.messages.getString("FTPAboutToGetFileFromServer")).append(" - ").append("file name:").append(filename).append(",server:")
					.append(server).append(":").append(port).append(",username:").append(username).append(",userpassword:")
					.append(userpassword != null && !"anonymous".equals(username) ? "<obscured>" : userpassword).append(",transfer:").append(transfer)
					.append(",mode:").append(mode).append(",epsv:").append(epsv).append(",protocol:").append(protocol).append(",trustmgr:").append(trustmgr)
					.append(",datatimeout:").append(datatimeout).append(",proxyserver:").append(proxyserver).append(":").append(proxyport)
					.append(",proxyusername:").append(proxyusername).append(",proxypassword:").append(proxypassword != null ? "<obscured>" : null);

			logger.fine(sb.toString());
		}

		try {
			if (port > 0) {
				ftp.connect(server, port);
			} else {
				ftp.connect(server);
			}

			int reply = ftp.getReplyCode();

			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				logger.warning(Emit.messages.getString("FTPCouldNotConnectToServer"));
				return null;
			}

		} catch (IOException _ex) {
			logger.warning(Emit.messages.getString("FTPCouldNotConnectToServer"));

			if (ftp.isConnected())
				try {
					ftp.disconnect();
				} catch (IOException _ex2) {
				}

			return null;
		}

		ByteArrayOutputStream output;
		try {
			if (!ftp.login(username, userpassword)) {
				ftp.logout();
				logger.warning(Emit.messages.getString("FTPServerRefusedLoginCredentials"));
				return null;
			}
			if ("binary".equalsIgnoreCase(transfer)) {
				ftp.setFileType(2);
			} else {
				ftp.setFileType(0);
			}

			if ("localactive".equalsIgnoreCase(mode)) {
				ftp.enterLocalActiveMode();
			} else {
				ftp.enterLocalPassiveMode();
			}

			ftp.setUseEPSVwithIPv4("true".equalsIgnoreCase(epsv));
			output = new ByteArrayOutputStream();
			ftp.retrieveFile(filename, output);
			output.close();
			ftp.noop();
			ftp.logout();

		} catch (IOException _ex) {
			logger.warning(Emit.messages.getString("FTPFailedToTransferFile"));

			if (ftp.isConnected())
				try {
					ftp.disconnect();
				} catch (IOException _ex2) {
				}
			return null;
		}

		return output.toByteArray();
	}
}
