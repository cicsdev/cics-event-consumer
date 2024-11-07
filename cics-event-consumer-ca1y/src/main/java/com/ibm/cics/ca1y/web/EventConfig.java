/* Licensed Materials - Property of IBM                                   */
/*                                                                        */
/* cics-event-consumer                                                    */
/*                                                                        */
/* (c) Copyright IBM Corp. 2012 - 2024 All Rights Reserved                */
/*                                                                        */
/* US Government Users Restricted Rights - Use, duplication or disclosure */
/* restricted by GSA ADP Schedule Contract with IBM Corp                  */
/*                                                                        */

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
        return classes;
    }
}
