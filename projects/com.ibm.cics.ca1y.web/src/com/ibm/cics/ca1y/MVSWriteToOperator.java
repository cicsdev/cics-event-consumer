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

import com.ibm.jzos.MvsConsole;
import com.ibm.jzos.WtoConstants;
import com.ibm.jzos.WtoMessage;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class to issue an MVS operator message with optional route and descriptor
 * codes.
 * 
 * @author Mark Cocker <mark_cocker@uk.ibm.com>
 * @version 1.0
 * @since 2013-09-17
 */
public class MVSWriteToOperator implements EmitAdapter {

	/**
	 * Copyright statement to be included in the compiled class.
	 */
	static final String COPYRIGHT = "Licensed Materials - Property of IBM. "
			+ "CICS SupportPac CA1Y (c) Copyright IBM Corporation 2012 - 2015. All Rights Reserved. "
			+ "US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corporation";

	/**
	 * Logging
	 */
	private static Logger logger = Logger.getLogger(MVSWriteToOperator.class.getName());

	/**
	 * Property to specify the MVS job content.
	 */
	private static final String MVSWTO_CONTENT = "mvswto.content";

	/**
	 * Property to specify the MVS WTO route code.
	 */
	private static final String MVSWTO_ROUTE = "mvswto.route";

	/**
	 * Property to specify the MVS WTO descriptor code.
	 */
	private static final String MVSWTO_DESCRIPTOR = "mvswto.descriptor";

	private int descriptorCode;
	private int routeCode;

	/**
	 * Properties from which to find the job to be submitted.
	 */
	private EmitProperties props;

	public MVSWriteToOperator(EmitProperties p) {
		props = p;
		descriptorCode = getDescriptorCode(props.getProperty(MVSWTO_DESCRIPTOR));
		routeCode = getRouteCode(props.getProperty(MVSWTO_ROUTE));
	}

	public boolean send() {
		WtoMessage wto = new WtoMessage(props.getProperty(MVSWTO_CONTENT));

		if (descriptorCode != -1) {
			wto.setDescrs(descriptorCode);
		}

		if (routeCode != -1) {
			wto.setRoutcdes(routeCode);
		}

		try {
			MvsConsole.wto(wto);

		} catch (Exception e) {
			logger.warning(Emit.messages.getString("MVSWTO_LOG_FAIL_MESSAGE") + " - " + getMessageSummary());
			e.printStackTrace();
			return false;
		}

		if (logger.isLoggable(Level.INFO)) {
			logger.info(Emit.messages.getString("MVSWTO_LOG_SUCCESS_MESSAGE") + " - " + getMessageSummary());
		}

		return true;
	}
	
	/**
	 * Return true if we should attempt to emit 
	 * 
	 * @return true if the entries in EmitProperties are valid for emission. 
	 */
	public static boolean validForEmission(EmitProperties props) {
		return props.containsKey(MVSWriteToOperator.MVSWTO_CONTENT);
	}


	/**
	 * Return an int representing the provided descriptor code.
	 * 
	 * @param route
	 *            - number or string constant representing the descriptor code
	 * @return int - descriptor code, or -1 if invalid.
	 */
	private static int getDescriptorCode(String descriptor) {

		if ((descriptor == null) || descriptor.isEmpty()) {
			return -1;
		}

		if (descriptor.startsWith("DESC_")) {
			if (descriptor.equalsIgnoreCase("DESC_CRITICAL_EVENTUAL_ACTION_REQUESTED")) {
				return WtoConstants.DESC_CRITICAL_EVENTUAL_ACTION_REQUESTED;

			} else if (descriptor.equalsIgnoreCase("DESC_DYNAMIC_STATUS_DISPLAYS")) {
				return WtoConstants.DESC_DYNAMIC_STATUS_DISPLAYS;

			} else if (descriptor.equalsIgnoreCase("DESC_EVENTUAL_ACTION_REQUIRED")) {
				return WtoConstants.DESC_EVENTUAL_ACTION_REQUIRED;

			} else if (descriptor.equalsIgnoreCase("DESC_IMMEDIATE_ACTION_REQUIRED")) {
				return WtoConstants.DESC_IMMEDIATE_ACTION_REQUIRED;

			} else if (descriptor.equalsIgnoreCase("DESC_IMMEDIATE_COMMAND_RESPONSE")) {
				return WtoConstants.DESC_IMMEDIATE_COMMAND_RESPONSE;

			} else if (descriptor.equalsIgnoreCase("DESC_IMPORTANT_INFORMATION_MESSAGES")) {
				return WtoConstants.DESC_IMPORTANT_INFORMATION_MESSAGES;

			} else if (descriptor.equalsIgnoreCase("DESC_JOB_STATUS")) {
				return WtoConstants.DESC_JOB_STATUS;

			} else if (descriptor.equalsIgnoreCase("DESC_MESSAGE_PREVIOUSLY_AUTOMATED")) {
				return WtoConstants.DESC_MESSAGE_PREVIOUSLY_AUTOMATED;

			} else if (descriptor.equalsIgnoreCase("DESC_OPERATOR_REQUEST")) {
				return WtoConstants.DESC_OPERATOR_REQUEST;

			} else if (descriptor.equalsIgnoreCase("DESC_OUT_OF_LINE_MESSAGE")) {
				return WtoConstants.DESC_OUT_OF_LINE_MESSAGE;

			} else if (descriptor.equalsIgnoreCase("DESC_RETAIN")) {
				return WtoConstants.DESC_RETAIN;

			} else if (descriptor.equalsIgnoreCase("DESC_SYSTEM_FAILURE")) {
				return WtoConstants.DESC_SYSTEM_FAILURE;

			} else if (descriptor.equalsIgnoreCase("DESC_SYSTEM_STATUS")) {
				return WtoConstants.DESC_SYSTEM_STATUS;
			}
		}

		try {
			return Integer.parseInt(descriptor);
		} catch (Exception e) {
			return -1;
		}
	}

	/**
	 * Return an int representing the provided route code.
	 * 
	 * @param route
	 *            - number or string constant representing the route code
	 * @return int - route code, or -1 if invalid.
	 */
	private static int getRouteCode(String route) {

		if ((route == null) || route.isEmpty()) {
			return -1;
		}

		if (route.startsWith("ROUTCDE_")) {
			if (route.equalsIgnoreCase("ROUTCDE_DASD_POOL")) {
				return WtoConstants.ROUTCDE_DASD_POOL;

			} else if (route.equalsIgnoreCase("ROUTCDE_DISK_LIBRARY")) {
				return WtoConstants.ROUTCDE_DISK_LIBRARY;

			} else if (route.equalsIgnoreCase("ROUTCDE_EMULATORS")) {
				return WtoConstants.ROUTCDE_EMULATORS;

			} else if (route.equalsIgnoreCase("ROUTCDE_MASTER_CONSOLE_ACTION")) {
				return WtoConstants.ROUTCDE_MASTER_CONSOLE_ACTION;

			} else if (route.equalsIgnoreCase("ROUTCDE_MASTER_CONSOLE_INFORMATION")) {
				return WtoConstants.ROUTCDE_MASTER_CONSOLE_INFORMATION;

			} else if (route.equalsIgnoreCase("ROUTCDE_PROGRAMMER_INFORMATION")) {
				return WtoConstants.ROUTCDE_PROGRAMMER_INFORMATION;

			} else if (route.equalsIgnoreCase("ROUTCDE_SYSPROG_INFORMATION")) {
				return WtoConstants.ROUTCDE_SYSPROG_INFORMATION;

			} else if (route.equalsIgnoreCase("ROUTCDE_SYSTEM_SECURITY")) {
				return WtoConstants.ROUTCDE_SYSTEM_SECURITY;

			} else if (route.equalsIgnoreCase("ROUTCDE_TAPE_LIBRARY")) {
				return WtoConstants.ROUTCDE_TAPE_LIBRARY;

			} else if (route.equalsIgnoreCase("ROUTCDE_TAPE_POOL")) {
				return WtoConstants.ROUTCDE_TAPE_POOL;

			} else if (route.equalsIgnoreCase("ROUTCDE_TELEPROCESSING_CONTROL")) {
				return WtoConstants.ROUTCDE_TELEPROCESSING_CONTROL;

			} else if (route.equalsIgnoreCase("ROUTCDE_UNIT_RECORD_POOL")) {
				return WtoConstants.ROUTCDE_UNIT_RECORD_POOL;
			}
		}

		try {
			return Integer.parseInt(route);
		} catch (Exception e) {
			return -1;
		}
	}

	/**
	 * Return a short summary of the WTO message.
	 * 
	 * @return short summary of the WTO message.
	 */
	private String getMessageSummary() {
		String codes = "";

		if (routeCode != -1) {
			codes += MVSWTO_ROUTE + "=" + routeCode + ",";
		}

		if (descriptorCode != -1) {
			codes += MVSWTO_DESCRIPTOR + "=" + descriptorCode + ",";
		}

		return codes + "WTO:" + props.getProperty(MVSWTO_CONTENT);
	}
}