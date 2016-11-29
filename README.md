# cics-event-consumer
This is a Java EE 7 web application that _consumes_ events produced by the CICS TS event processing support as a result of a CICS application event, a CICS system event, or a CICS policy event action.

Once the application is installed, it can be called:
* by CICS directly as a custom event adapter
* by the CICS HTTP event adapter as a RESTfull service
* by your program using the EXEC CICS LINK command
* by your program using the EXEC CICS START command

The received event can be formatted and:
* sent as an email
* sent an instant message on the Jabber network
* stored in a zFS file, MVS file, or temporary storage queue
* written out as a message to the MVS operator console
* submitted as an MVS job

The application was originally released as the [IBM CA1Y: Send email from CICS Transaction Server for z/OS](http://www-01.ibm.com/support/docview.wss?uid=swg24033197) and referred to here as CA1Y for short. 

## Requirements
* CICS TS TS V5.3, or above
* A configured CICS Liberty JVM server, as described in topic [Configuring a Liberty JVM server](http://www.ibm.com/support/knowledgecenter/SSGMCP_5.3.0/com.ibm.cics.ts.java.doc/JVMserver/config_jvmserver_liberty.html) 
* CICS Explorer SDK 5.3, or above
* Access to the Maven Central Repository to resolve dependencies

## Installation
* Define, install and enable CICS transaction with NAME(CA1Y),PROGRAM(CA1Y),TASKDATALOC(ANY)
* Update the Liberty server configuration file server.xml to include the following features:

```xml
<feature>cicst:link-1.0</feature>
<feature>jaxrs-2.0</feature>
<feature>jaxb-2.2</feature>
<feature>javaMail-1.5</feature>
```
* Clone this repository, or download the repository [ZIP](https://github.com/cicsdev/cics-event-consumer/archive/master.zip) and expand it.
* In CICS Explorer, select `File` > `Import...` > `Existing Projects into Workspace` > `Select root directory` > `Browse` and select the repository `projects` directory. Select all the projects, then `Copy projects into workspace`, then `Finish`.
* Export the `com.ibm.cics.ca1y.web.cicsbundle` CICS Bundle project to zFS using the wizard `Export Bundle Project to z/OS UNIX File System...` to a directory on zFS, eg. /usr/lpp/ca1y/com.ibm.cics.ca1y.web.cicsbundle_1.7.1
* Define, install and enable CICS bundle with NAME(CA1Y),BUNDLEDIR(/usr/lpp/ca1y/com.ibm.cics.ca1y.web.cicsbundle_1.7.1)

Alternatively you can export the `com.ibm.cics.ca1y.web` project as a WAR file and install it into the Liberty JVM server using an application entry in server.xml.

## Documentation
Details how to use CA1Y see the [CA1Y.pdf](https://github.com/cicsdev/cics-event-consumer/tree/master/documentation/CA1Y.pdf).
    
## CAY logging
CA1Y uses java.util.logging facilities to trace the processing of consumed events. Logging can be enabled and the output
directed to the JVM standard error (STDERR) by doing the folowing:

1. Create a logging.properties file on zFS to specify the logging parameters.
For example create /usr/lpp/ca1y/examples/logging.properties with the following content:

! Logging levels
java.util.logging.ConsoleHandler.level=ALL
com.ibm.cics.ca1y.level=ALL

! Logging handlers
com.ibm.cics.ca1y.handlers=java.util.logging.ConsoleHandler
com.ibm.cics.ca1y.useParentHandlers=false
java.util.logging.ConsoleHandler.formatter=java.util.logging.SimpleFormatter
java.util.logging.SimpleFormatter.format=%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$s %2$s - %5$s %6$s%n

2. Add the following line to the JVM server profile:
-Djava.util.logging.config.file=/usr/lpp/ca1y/examples/logging.properties
