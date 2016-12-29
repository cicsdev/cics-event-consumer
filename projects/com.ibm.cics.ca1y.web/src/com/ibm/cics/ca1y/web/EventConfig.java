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

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

public class EventConfig extends Application {
    //List the JAX-RS classes that contain annotations
    public Set<Class<?>> getClasses()
    {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        classes.add(com.ibm.cics.ca1y.web.Event.class);
        classes.add(com.ibm.cics.ca1y.web.Log.class);
        return classes;
    }
}
