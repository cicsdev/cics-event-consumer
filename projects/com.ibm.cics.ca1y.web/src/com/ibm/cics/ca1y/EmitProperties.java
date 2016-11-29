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

import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TreeSet;

/**
 * Class to store properties with additional attributes.
 * 
 * @author Mark Cocker <mark_cocker@uk.ibm.com>
 * @version 1.1
 * @since 2012-02-10
 */
public class EmitProperties extends Properties {

	/**
	 * Copyright statement to be included in the compiled class.
	 */
	static final String COPYRIGHT = "Licensed Materials - Property of IBM. "
			+ "CICS SupportPac CA1Y (c) Copyright IBM Corporation 2012 - 2015. All Rights Reserved. "
			+ "US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corporation";

	/**
	 * Serial version ID of this class.
	 */
	public static final long serialVersionUID = 1L;

	/**
	 * The default MIME type to use for the attachments if it cannot be established from contents.
	 */
	private static final String MIME_DEFAULT = "application/octet-stream";

	/**
	 * Vector to store names of event business information items. Need to
	 * retrieve in order of insertion. Duplicate keys are not supported by
	 * LinkedHasMap.
	 */
	private final LinkedHashMap<String, Integer> businessInformationItems = new LinkedHashMap<String, Integer>();

	/**
	 * Hastable to store and associate a property key with an attachment.
	 */
	private final Hashtable<String, byte[]> attachments = new Hashtable<String, byte[]>();

	/**
	 * Hashtable to store and associate a property key with the attachment file
	 * name.
	 */
	private final Hashtable<String, String> alternateNames = new Hashtable<String, String>();

	/**
	 * Hashtable to store and associate a property key with a privacy state.
	 */
	private final Hashtable<String, Boolean> privacy = new Hashtable<String, Boolean>();

	/**
	 * Hashtable to store if the property key has been resolved.
	 */
	private final Hashtable<String, Boolean> resolved = new Hashtable<String, Boolean>();

	/**
	 * Hashtable to store and associate a property key with the mime type.
	 */
	private final Hashtable<String, String> mime = new Hashtable<String, String>();

	/**
	 * Hashtable to store and associate a property key with the mime type to be
	 * converted to.
	 */
	private final Hashtable<String, String> mimeTo = new Hashtable<String, String>();

	/**
	 * Hashtable to store and associate a property key with an XSLT to be used
	 * when converting from a MIME to another MIME.
	 */
	private final Hashtable<String, String> xslt = new Hashtable<String, String>();

	/**
	 * Hashtable to store and associate a property key with a list of properties
	 * to zip.
	 */
	private final Hashtable<String, String> zip = new Hashtable<String, String>();

	/**
	 * Hashtable to store and associate a property key with a list of properties
	 * to zip.
	 */
	private final Hashtable<String, String> zipInclude = new Hashtable<String, String>();

	/**
	 * Hashtable to store and associate a property key with the name of the
	 * container in which to return this property.
	 */
	private final Hashtable<String, String> returnContainer = new Hashtable<String, String>();

	/**
	 * Get the event business information items.
	 * 
	 * @param none
	 * @return Enumeration<String>
	 */
	public Map<String, Integer> getBusinessInformationItems() {
		return businessInformationItems;
	}

	/**
	 * Get the event business information items and values.
	 * 
	 * @param none
	 * @return Enumeration<String>
	 */
	public Map<String, Object> getBusinessInformationItemsValues() {
		LinkedHashMap<String, Object> lhm = new LinkedHashMap<String, Object>();

		for (Map.Entry<String, Integer> entry : getBusinessInformationItems().entrySet()) {
			String key = entry.getKey();

			String name = getPropertyAlternateName(key);
			if (name == null) {
				name = key;
			}

			Object value = getPropertyAttachment(key);
			if (value == null) {
				value = getProperty(key);
			}

			lhm.put(name, value);
		}

		return lhm;
	}

	/**
	 * Add the property to the list of event business information items.
	 * 
	 * @param String
	 *            - name of the property to add
	 */
	public boolean putBusinessInformationItem(String key, int length) {
		if (key == null) {
			return false;
		}

		businessInformationItems.put(key, length);

		return true;
	}

	/**
	 * Returns true if property value of the specified key matches the
	 * string 'true'.
	 * 
	 * @param key
	 *            - property key to evaluate
	 * @return true if key in properties is equal to 'true' ignoring
	 *         case
	 */
	public boolean isPropertyTrue(String key) {
		return Boolean.parseBoolean(getProperty(key));
	}

	/**
	 * Returns true if property entry specified by name is equal ignoring case
	 * to specified value.
	 * 
	 * @param name
	 *            - property name
	 * @param value
	 *            - value to compare against
	 * @return boolean - true if the property specified by name in Properties
	 *         props is equal ignoring case to the value specified
	 */
	public boolean isPropertyEqualsIgnoreCase(String name, String value) {
		return value.equalsIgnoreCase(getProperty(name, ""));
	}

	/**
	 * Get the attachment associated with the property key, otherwise null if the property or key does not exist.
	 * 
	 * @param key
	 *            - property key
	 * @return the attachment
	 */
	public byte[] getPropertyAttachment(String key) {
		if (key == null) {
			return null;
		}

		return (byte[]) attachments.get(key);
	}

	/**
	 * Get the attachment file name associated with the key.
	 * 
	 * @param key
	 *            - property key
	 * @return the file name
	 */
	public String getPropertyAlternateName(String key) {
		if (key == null) {
			return null;
		}

		return (String) alternateNames.get(key);
	}

	/**
	 * Get the name of the return container associated with the key if one exists, null otherwise. 
	 * 
	 * @param key
	 *            - property key
	 * @return String - container name
	 */
	public String getPropertyReturnContainer(String key) {
		if (key == null) {
			return null;
		}

		return (String) returnContainer.get(key);
	}

	/**
	 * Get the privacy associated with the key if one exists, false otherwise.
	 * 
	 * @param key
	 *            - property key
	 * @return boolean - true if the property is private
	 */
	public boolean getPropertyPrivacy(String key) {
		if (key == null) {
			return false;
		}

		Boolean b = (Boolean) privacy.get(key);

		if (b == null) {
			return false;
		}
		
		return b;
	}

	/**
	 * Get whether a property has been resolved.
	 * 
	 * @param key
	 *            - property key
	 * @return true if the property has been resolved
	 */
	public boolean getPropertyResolved(String key) {
		if (key == null) {
			return false;
		}

		Boolean b = (Boolean) resolved.get(key);

		if ((b != null) && (b == true)) {
			return true;
		}

		return false;
	}

	/**
	 * Get the MIME associated with the key if one exists, null otherwise.
	 * 
	 * @param key
	 *            - property key
	 * @return MIME
	 */
	public String getPropertyMime(String key) {
		if (key == null) {
			return null;
		}

		return (String) mime.get(key);
	}

	/**
	 * Get the MIME associated with the key if one exists, null otherwise.
	 * 
	 * @param key
	 *            - property key
	 * @return MIME
	 */
	public String getPropertyMime(String key, String defaultMime) {
		if (key == null) {
			return defaultMime;
		}

		return (String) mime.get(key);
	}


	/**
	 * Get the MIME the property needs to be converted to if one exists, null otherwise.
	 * 
	 * @param key
	 *            - property key
	 * @return String - MIME
	 */
	public String getPropertyMimeTo(String key) {
		if (key == null) {
			return null;
		}

		return (String) mimeTo.get(key);
	}

	/**
	 * Get the XSLT associated with the property key if one exists, null otherwise.
	 * 
	 * @param key
	 *            - property key
	 * @return String - XSLT.
	 */
	public String getPropertyXSLT(String key) {
		if (key == null) {
			return null;
		}

		return (String) xslt.get(key);
	}

	/**
	 * Get the zip name associated with the key if one exists, null otherwise.
	 * 
	 * @param key
	 *            - property key
	 * @return String - zip name
	 */
	public String getPropertyZip(String key) {
		if (key == null) {
			return null;
		}

		return (String) zip.get(key);
	}

	/**
	 * Get the list of properties to include in the zip if they exist, null otherwise.
	 * 
	 * @param key
	 *            - property key
	 * @return String - list of properties
	 */
	public String getPropertyZipInclude(String key) {
		if (key == null) {
			return null;
		}

		return (String) zipInclude.get(key);
	}

	/**
	 * Associate the property key with an attachment.
	 * 
	 * @param key
	 *            - property key
	 * @param attachment
	 *            - attachment to associate
	 * @return true
	 */
	public boolean setPropertyAttachment(String key, byte[] attachment) {
		if (key == null) {
			return false;
		}

		if ((attachment == null) || (attachment.length == 0)) {
			attachments.remove(key);

		} else {
			attachments.put(key, attachment);
		}

		return true;
	}

	/**
	 * Associate the property key with an alternate name.
	 * 
	 * @param key
	 *            - property key
	 * @param attachmentName
	 *            - name
	 * @return true
	 */
	public boolean setPropertyAlternateName(String key, String attachmentName) {
		if (key == null) {
			return false;
		}

		if (attachmentName == null) {
			alternateNames.remove(key);

		} else {
			alternateNames.put(key, attachmentName.trim());
		}

		return true;
	}

	/**
	 * Get a Map of the properties to be used to create containers.
	 *
	 * @return Map<String, String> -
	 */
	public Map<String, String> getReturnContainers() {
		return returnContainer;
	}

	/**
	 * Associate the property key with the name of a container.
	 * 
	 * @param key
	 *            - property key
	 * @param returnContainerName
	 *            - container name
	 * @return true
	 */
	public boolean setPropertyReturnContainer(String key, String returnContainerName) {
		if (key == null) {
			return false;
		}

		if (returnContainerName == null) {
			returnContainer.remove(key);

		} else {
			returnContainer.put(key, returnContainerName);
		}

		return true;
	}

	/**
	 * Indicate if the property is private.
	 * 
	 * @param key
	 *            - property key
	 * @param b
	 *            - true if the property is private
	 * @return boolean - true if successful
	 */
	public boolean setPropertyPrivacy(String key, boolean b) {
		if (key == null) {
			return false;
		}

		privacy.put(key, b);
		
		return true;
	}

	/**
	 * Set the property value and indicate it as been fully resolved.
	 * 
	 * @param key
	 *            - property key
	 * @param value
	 *            - property value
	 * @return boolean - true successful, false otherwise.
	 */
	public boolean setPropertyAndResolved(String key, String value) {
		if (key == null) {
			return false;
		}

		setProperty(key, value);
		resolved.put(key, true);
		
		return true; 
	}

	/**
	 * Indicate if the property has been resolved.
	 * 
	 * @param key
	 *            - property key
	 * @param b
	 *            - true if the property has been fully resolved
	 * @return boolean - true if successfully stored file name, false otherwise.
	 */
	public boolean setPropertyResolved(String key, boolean b) {
		if (key == null) {
			return false;
		}

		resolved.put(key, b);
		
		return true; 
	}

	/**
	 * Associate the property with a MIME type.
	 * 
	 * @param key
	 *            - property key
	 * @param mimeType
	 *            - MIME type
	 * @return boolean - true if successful, false otherwise.
	 */
	public boolean setPropertyMime(String key, String mimeType) {
		if (key == null) {
			return false;
		}

		if (mimeType == null) {
			mime.remove(key);

		} else {
			mime.put(key, mimeType);
		}

		return true;
	}

	/**
	 * Associate the property with a MIME associated with the provided file name.
	 * 
	 * @param key
	 *            - property key
	 * @param fileName
	 *            - file name
	 * @return boolean - true if successful, false otherwise.
	 */
	public boolean setPropertyMimeDefault(String key, String fileName) {
		if (key == null) {
			return false;
		}

		String m = null;

		if ((fileName != null) && (!fileName.isEmpty())) {
			// Map the MIME type based on name & content
			FileNameMap fileNameMap = URLConnection.getFileNameMap();
			m = fileNameMap.getContentTypeFor(fileName);
		}

		if ((m == null) || (m.isEmpty())) {
			// MIME could not be mapped, so default
			m = MIME_DEFAULT;
		}

		mime.put(key, m);

		return true;
	}

	/**
	 * Associate and store the mimeTo with the key.
	 * 
	 * @param key
	 *            - key used to associate the mimeTo with.
	 * @param attachmentFile
	 *            - file name.
	 * @return boolean - true if successfully stored the mimeTo, false
	 *         otherwise.
	 */
	public boolean setPropertyMimeTo(String key, String s) {
		if (key == null) {
			return false;
		}

		if (s == null) {
			mimeTo.remove(key);

		} else {
			mimeTo.put(key, s);
		}

		return true;
	}

	/**
	 * Associate and store the mimeTo with the key.
	 * 
	 * @param key
	 *            - key used to associate the mimeTo with.
	 * @param attachmentFile
	 *            - file name.
	 * @return boolean - true if successfully stored the mimeTo, false
	 *         otherwise.
	 */
	public boolean setPropertyXSLT(String key, String s) {
		if (key == null) {
			return false;
		}

		if (s == null) {
			xslt.remove(key);

		} else {
			xslt.put(key, s);
		}

		return true;
	}

	/**
	 * Associate and store the zip with the key.
	 * 
	 * @param key
	 *            - key used to associate the zip with.
	 * @param propertyList
	 *            - list of properties to include in the zip, comma separated.
	 * @return boolean - true if successfully stored the zip, false otherwise.
	 */
	public boolean setPropertyZip(String key, String z) {
		if (key == null) {
			return false;
		}

		if (z == null) {
			zip.remove(key);

		} else {
			zip.put(key, z);
		}

		return true;
	}

	/**
	 * Associate and store the zip with the key.
	 * 
	 * @param key
	 *            - key used to associate the zip with.
	 * @param propertyList
	 *            - list of properties to include in the zip, comma separated.
	 * @return boolean - true if successfully stored the zip, false otherwise.
	 */
	public boolean setPropertyZipInclude(String key, String include) {
		if (key == null) {
			return false;
		}

		if (include == null) {
			zipInclude.remove(key);

		} else {
			zipInclude.put(key, include);
		}

		return true;
	}

	/**
	 * Provide an ordered enumeration.
	 * 
	 * @return Set - ordered enumeration.
	 */
	public synchronized Enumeration<Object> propertyNamesOrdered() {
		return Collections.enumeration(new TreeSet<Object>(keySet()));
	}

	/**
	 * Summary of property.
	 * 
	 * @param props
	 * @param key
	 * @return summary of the properties
	 */
	public static String getPropertySummary(EmitProperties props, String key) {
		StringBuilder sb = new StringBuilder();

		sb.append(key).append("=").append((props.getPropertyPrivacy(key) ? Emit.messages.getString("PropertyHidden") : props.getProperty(key)));
		
		if (props.getPropertyPrivacy(key)) {
			return sb.toString();
		}
		
		if (props.getPropertyAttachment(key) != null) {
			sb.append(",attachment:" + Util.getHexSummary(props.getPropertyAttachment(key)) + ",length:" + props.getPropertyAttachment(key).length);
		}

		if (props.getPropertyAlternateName(key) != null) {
			sb.append(",name:" + props.getPropertyAlternateName(key));
		}

		if (props.getPropertyMime(key) != null) {
			sb.append(",MIME:" + props.getPropertyMime(key));
		}

		if (props.getPropertyMimeTo(key) != null) {
			sb.append(",MIME to:" + props.getPropertyMimeTo(key));
		}

		if (props.getPropertyXSLT(key) != null) {
			sb.append(",XSLT:" + props.getPropertyXSLT(key));
		}

		if (props.getPropertyZip(key) != null) {
			sb.append(",zip name:" + props.getPropertyZip(key));
		}

		if (props.getPropertyZipInclude(key) != null) {
			sb.append(",zip include:" + props.getPropertyZipInclude(key));
		}

		return sb.toString();
	}
}
