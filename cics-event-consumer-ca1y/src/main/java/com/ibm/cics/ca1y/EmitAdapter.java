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

/**
 * Interface with required methods for EmitAdapters.
 * 
 * @author Mark Cocker <mark_cocker@uk.ibm.com>
 * @version 1.6
 * @since 2014-01-19
 */
public interface EmitAdapter {
	
	public boolean send();
	
	// Would like to add the following but static methods are not allowed in interfaces until Java 8.  
	// public static boolean validForEmission(EmitProperties props);
}
