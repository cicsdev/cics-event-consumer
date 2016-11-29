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

import java.util.Arrays;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class SimpleFormatter extends Formatter {

	public SimpleFormatter() {
		super();
	}

	@Override
	public String format(LogRecord r) {
		Object[] parms = r.getParameters();
		
		Object[] newparms = Arrays.copyOf(parms, parms.length + 1);
		
		newparms[parms.length] = r.getThreadID();
		
		r.setParameters(newparms);
		
		return formatMessage(r);
	}
}
