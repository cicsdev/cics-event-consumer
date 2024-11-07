/* Licensed Materials - Property of IBM                                   */
/*                                                                        */
/* cics-event-consumer                                                    */
/*                                                                        */
/* (c) Copyright IBM Corp. 2012 - 2024 All Rights Reserved                */
/*                                                                        */
/* US Government Users Restricted Rights - Use, duplication or disclosure */
/* restricted by GSA ADP Schedule Contract with IBM Corp                  */
/*                                                                        */

package com.ibm.cics.ca1y;

import org.apache.http.client.utils.DateUtils;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.cics.server.HttpClientRequest;
import com.ibm.cics.server.HttpClientResponse;
import com.ibm.cics.server.HttpSession;
import com.ibm.cics.server.IOErrorException;
import com.ibm.cics.server.TimedOutException;

/**
 * Class to emit a message to an HTTP server.
 * 
 * @author Mark Cocker <mark_cocker@uk.ibm.com>
 * @version 1.0
 * @since 2013-09-25
 */
public class CICSHTTP implements EmitAdapter {

	/**
	 * Copyright statement to be included in the compiled class.
	 */
	static final String COPYRIGHT = "Licensed Materials - Property of IBM. "
			+ "CICS SupportPac CA1Y (c) Copyright IBM Corporation 2012 - 2016. All Rights Reserved. "
			+ "US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corporation";

	/**
	 * Logging
	 */
	private static Logger logger = Logger.getLogger(CICSHTTP.class.getName());

	/**
	 * Property to specify the CICS URIMAP resource name to use to send the HTTP
	 * request.
	 */
	private static final String HTTP_URIMAP = "http.urimap";

	/**
	 * Property to specify the HTTP path.
	 */
	private static final String HTTP_URI = "http.uri";

	/**
	 * Property to specify the HTTP entity body content.
	 */
	private static final String CICS_HTTP_CONTENT = "http.content";

	/**
	 * Property to specify the HTTP content characterset
	 */
	private static final String HTTP_CHARACTERSET = "http.characterset";
	private static final String HTTP_CHARACTERSET_DEFAULT = "utf-8";

	/**
	 * Property to specify the HTTP session certificate
	 */
	private static final String HTTP_CERTIFICATE = "http.certificate";

	/**
	 * Property to specify how many times to retry the HTTP request.
	 */
	private static final String HTTP_RETRY = "http.retry";
	private static final int HTTP_RETRY_DEFAULT = 0;

	private static final String HTTP_RETRY_DELAY = "http.retrydelay";
	private static final int HTTP_RETRY_DELAY_DEFAULT = 500;
	private static final int HTTP_RETRY_DELAY_MAX = 60 * 1000;

	/**
	 * Property to specify the HTTP method.
	 */
	private static final String HTTP_METHOD = "http.method";
	private static final String HTTP_METHOD_DEFAULT = "PUT";

	/**
	 * The default MIME type to use for the HTTP content.
	 */
	private static final String HTTP_CONTENT_MIME_TEXT_DEFAULT = "text/plain";

	private EmitProperties props;

	public CICSHTTP(EmitProperties p) {
		props = p;
	}

	public boolean send() {
		HttpSession httpSession;

		if (props.getPropertyMime(CICS_HTTP_CONTENT) == null) {
			props.setPropertyMime(CICS_HTTP_CONTENT, HTTP_CONTENT_MIME_TEXT_DEFAULT);
		}

		HttpClientRequest httpClientRequest = new HttpClientRequest(props.getProperty(HTTP_METHOD, HTTP_METHOD_DEFAULT));

		// Do not close the socket once the request completes providing a HTTP response 2nn is received
		httpClientRequest.setNoClose();

		if (props.containsKey(HTTP_URIMAP)) {
			// Use the CICS URIMAP resource
			httpSession = new HttpSession(props.getProperty(HTTP_URIMAP));

		} else if (props.containsKey(HTTP_URI)) {
			// Use the URI class as a convenience to parse the URI and copy the
			// URI elements that CICS needs setting individually
			URI uri;

			try {
				uri = new URI(props.getProperty(HTTP_URI));

			} catch (Exception e) {
				logger.warning(Emit.messages.getString("HTTP_INVALID_URI") + " " + getMessageSummary());
				return false;
			}

			if (uri.getHost() == null) {
				logger.warning(Emit.messages.getString("HTTP_NO_HOST") + " " + getMessageSummary());
				return false;
			}

			if (uri.getScheme() == null) {
				logger.warning(Emit.messages.getString("HTTP_NO_SCHEME") + " " + getMessageSummary());
				return false;
			}

			if (uri.getQuery() != null) {
				httpClientRequest.setQueryString(uri.getQuery());
			}

			if (uri.getPath() != null) {
				httpClientRequest.setPath(uri.getPath());
			}

			if (uri.getUserInfo() != null) {
				String[] userInfo = uri.getUserInfo().split(":");

				if (userInfo.length == 2) {
					httpClientRequest.setAuthenticationBasic(userInfo[0], userInfo[1]);
				} else {
					logger.warning(Emit.messages.getString("HTTP_NO_PASSOWRD") + " " + getMessageSummary());
					return false;
				}
			}

			if (uri.getPort() == -1) {
				httpSession = new HttpSession(uri.getHost(), uri.getScheme());

			} else {
				httpSession = new HttpSession(uri.getHost(), uri.getScheme(), uri.getPort());
			}

		} else {
			logger.warning(Emit.messages.getString("HTTP_NO_URIMAP_OR_URI") + " " + getMessageSummary());
			return false;
		}

		if (props.containsKey(HTTP_CERTIFICATE)) {
			httpSession.setCertificate(props.getProperty(HTTP_CERTIFICATE));
		}

		int retry = HTTP_RETRY_DEFAULT;
		if (props.containsKey(HTTP_RETRY)) {
			try {
				retry = Integer.parseInt(props.getProperty(HTTP_RETRY));

				if (retry < 0) {
					retry = 0;
				}

			} catch (Exception e) {
				// Silently ignore invalid values and use default 
			}
		}
		
		// Set the HTTP header character set
		String characterSet = null;
		if ((props.containsKey(HTTP_CHARACTERSET) == false) && (props.getPropertyAttachment(CICS_HTTP_CONTENT) == null)) {
			// If there is no character set passed in, and the content to send is not an attachment, use default
			characterSet = HTTP_CHARACTERSET_DEFAULT;
			
		} else {
			// Use the character set passed in if present
			characterSet = props.getProperty(HTTP_CHARACTERSET);
		}
		
		String contentType = props.getPropertyMime(CICS_HTTP_CONTENT, HTTP_CONTENT_MIME_TEXT_DEFAULT);
		
		if (characterSet != null) {
			// Append the character set to the end of the ContentType
			contentType += "; charset=" + characterSet;
		}

		int statusCode = 0;
		String statusText = "";
		long retryDelay;

		while (retry >= 0) {
			retry--;

			retryDelay = HTTP_RETRY_DELAY_DEFAULT;
			if (props.containsKey(HTTP_RETRY_DELAY)) {
				try {
					retryDelay = Integer.parseInt(props.getProperty(HTTP_RETRY_DELAY));
					
				} catch (NumberFormatException e) {
					// Ignore invalid values and use default
				}
			}

			try {
				httpSession.open(false);

				httpClientRequest.writeHeader(httpSession, "Content-Type", contentType);
				httpClientRequest.setNoClientConvert();
				
				if (props.getPropertyAttachment(CICS_HTTP_CONTENT) == null) {
					
					if (characterSet != null) {
						httpClientRequest.sendFrom(httpSession, (props.getProperty(CICS_HTTP_CONTENT)).getBytes(characterSet));
					} else {
						httpClientRequest.sendFrom(httpSession, props.getProperty(CICS_HTTP_CONTENT));
					}

				} else {
					httpClientRequest.sendFrom(httpSession, props.getPropertyAttachment(CICS_HTTP_CONTENT));
				}

				HttpClientResponse httpClientResponse = new HttpClientResponse();

				// As the response is not going to be used prevent any wasted conversion
				httpClientResponse.setNoClientConvert();
				
				// getContent performs the send the request and returns the response and status code
				// Ignore the response
				httpClientResponse.getContent(httpSession);
				
				statusCode = httpClientResponse.getStatusCode();
				statusText = httpClientResponse.getStatusText();

				if ((statusCode >= 200) && (statusCode <= 299)) {
					/* A good HTTP response was received */
					if (logger.isLoggable(Level.INFO)) {
						logger.info(Emit.messages.getString("HTTP_LOG_SUCCESS_MESSAGE") + " " + statusCode + " "
								+ statusText + "," + getMessageSummary());
					}

					return true;

				} else if (statusCode == 503) {
					// Use the Retry-After header if present as the delay period
					String retryAfterHTTPHeader = httpClientResponse.getHeader(httpSession, "Retry-After");

					try {
						// Convert the HTTP header from seconds into milliseconds to wait
						retryDelay = (retryAfterHTTPHeader == null ? retryDelay : Integer.parseInt(retryAfterHTTPHeader) * 1000);

					} catch (NumberFormatException e) {
						// Convert the HTTP from an HTTP-date into seconds to milliseconds to wait
						try {
							retryDelay = System.currentTimeMillis() - DateUtils.parseDate(retryAfterHTTPHeader).getTime();

						} catch (Exception e2) {
							// Could not convert header, so use the default retryDelay
						}
					}

					if ((retryDelay < 0) || (retryDelay > HTTP_RETRY_DELAY_MAX)) {
						// We are not willing to wait for the length of time
						// asked for by the HTTP server so fail the request
						logger.warning(Emit.messages.getString("HTTP_RESPONSE_RETRYAFTER_NOT_ACCEPTABLE") + " - "
								+ retryAfterHTTPHeader);

						retry = 0;
					}

				} else {
					retry = 0;
				}

			} catch (TimedOutException t) {
				// If HTTP open, send, or response times out then retry
				statusCode = -1;
				statusText = t.getMessage();

			} catch (IOErrorException i) {
				// If general socket error then retry
				statusCode = -1;
				statusText = i.getMessage();

			} catch (Exception e) {
				// Otherwise do not retry
				e.printStackTrace();
				
				statusCode = -1;
				statusText = e.getMessage();
				retry = 0;
			}

			if (retry > 0) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine(Emit.messages.getString("HTTP_RETRYING") + " " + retryDelay + "ms");
				}
				
				try {
					Thread.sleep(retryDelay);

				} catch (InterruptedException e) {
					// Ignore
				}
			}
		}

		logger.warning(Emit.messages.getString("HTTP_LOG_RESPONSE_FAIL_MESSAGE") + " " + (statusCode == -1 ? "none" : statusCode) + ", " + statusText);

		return false;
	}

	/**
	 * Return true if we should attempt to emit 
	 * 
	 * @return true if the entries in EmitProperties are valid for emission. 
	 */
	public static boolean validForEmission(EmitProperties props) {
		return props.containsKey(CICSHTTP.CICS_HTTP_CONTENT);
	}
	
	/**
	 * Return a short summary of the contents to be written to the queue.
	 * 
	 * @return short summary of the contents to be written to the queue.
	 */
	private String getMessageSummary() {
		StringBuilder sb = new StringBuilder();

		sb.append(HTTP_METHOD).append(":").append(props.getProperty(HTTP_METHOD, HTTP_METHOD_DEFAULT));
		
		if (props.containsKey(HTTP_URIMAP)) {
			sb.append(",").append(HTTP_URIMAP).append(":").append(props.getProperty(HTTP_URIMAP));
		}

		if (props.containsKey(HTTP_URI)) {
			sb.append(",").append(HTTP_URI).append(":").append(props.getProperty(HTTP_URI));
		}

		if (props.getPropertyMime(CICS_HTTP_CONTENT) != null) {
			sb.append(",").append("MIME:").append(props.getPropertyMime(CICS_HTTP_CONTENT));
		}

		if (props.containsKey(HTTP_CERTIFICATE)) {
			sb.append(",").append(HTTP_CERTIFICATE).append(":").append(props.getProperty(HTTP_CERTIFICATE, ""));
		}

		if (props.containsKey(HTTP_CHARACTERSET)) {
			sb.append(",").append(HTTP_CHARACTERSET).append(":").append(props.getProperty(HTTP_CHARACTERSET, ""));
		}

		if (props.containsKey(HTTP_RETRY)) {
			sb.append(",").append(HTTP_RETRY).append(":").append(props.getProperty(HTTP_RETRY, ""));
		}

		if (props.containsKey(HTTP_RETRY_DELAY)) {
			sb.append(",").append(HTTP_RETRY_DELAY).append(":").append(props.getProperty(HTTP_RETRY_DELAY, ""));
		}

		return sb.toString();
	}
}