# cics-event-consumer
This is a Java EE 7 web application that _consumes_ events produced by the CICS TS event processing support as a result of a CICS application event, a CICS system event, or a CICS policy event action.

Once the application is installed, it can be called:
* by CICS directly as a [custom event processing adapter](http://www.ibm.com/support/knowledgecenter/SSGMCP_5.3.0/com.ibm.cics.ts.eventprocessing.doc/concepts/dfhep_event_processing_adapters.html).
* by the CICS [HTTP EP adapter](http://www.ibm.com/support/knowledgecenter/SSGMCP_5.3.0/com.ibm.cics.ts.eventprocessing.doc/concepts/dfhep_event_processing_http_adapter.html) with events formatted using the [common base event REST format](http://www.ibm.com/support/knowledgecenter/SSGMCP_5.3.0/com.ibm.cics.ts.eventprocessing.doc/reference/dfhep_event_processing_cberformat.html).
* by your program using the [EXEC CICS LINK PROGRAM(CA1Y)](http://www.ibm.com/support/knowledgecenter/SSGMCP_5.3.0/com.ibm.cics.ts.applicationprogramming.doc/commands/dfhp4_link.html) command.
* by your program using the [EXEC CICS START TRANSID(CA1Y) CHANNEL()](http://www.ibm.com/support/knowledgecenter/SSGMCP_5.3.0/com.ibm.cics.ts.applicationprogramming.doc/commands/dfhp4_starttransidchannel.html) command.

The received event can be formatted and:
* sent as an email.
* sent as an instant message on the Jabber network.
* stored in a zFS file, MVS file, or temporary storage queue.
* written out as a message to the MVS operator console.
* submitted as an MVS job.

The application was originally released as the [IBM CA1Y: Send email from CICS Transaction Server for z/OS](http://www-01.ibm.com/support/docview.wss?uid=swg24033197) and referred to here as CA1Y for short. 

## Requirements
* CICS TS TS V5.3, or above.
* A configured CICS Liberty JVM server, as described in topic [Configuring a Liberty JVM server](http://www.ibm.com/support/knowledgecenter/SSGMCP_5.3.0/com.ibm.cics.ts.java.doc/JVMserver/config_jvmserver_liberty.html). 
* CICS Explorer SDK 5.3, or above.
* Access to the Maven Central Repository to resolve dependencies.

## Installation
* Define, install and enable a CICS transaction with NAME(CA1Y),PROGRAM(CA1Y),TASKDATALOC(ANY).
* Update the Liberty server configuration file server.xml to include the following features:

```xml
<feature>cicst:link-1.0</feature>
<feature>jaxrs-2.0</feature>
<feature>jaxb-2.2</feature>
<feature>javaMail-1.5</feature>
```
* Clone this repository, or download the repository [cics-event-consumer-master.zip](https://github.com/cicsdev/cics-event-consumer/archive/master.zip) and expand it.
* In CICS Explorer, select `File` > `Import...` > `Existing Projects into Workspace` > `Select root directory` > `Browse` and select the repository `projects` directory. Select all the projects, then `Copy projects into workspace`, then `Finish`.
* Export the `com.ibm.cics.ca1y.web.cicsbundle` CICS Bundle project to zFS using the wizard `Export Bundle Project to z/OS UNIX File System...` to a directory on zFS, eg. /usr/lpp/ca1y/com.ibm.cics.ca1y.web.cicsbundle_1.7.1
* Define, install and enable CICS bundle with NAME(CA1Y),BUNDLEDIR(/usr/lpp/ca1y/com.ibm.cics.ca1y.web.cicsbundle_1.7.1).

Alternatively you can export the `com.ibm.cics.ca1y.web` project as a WAR file and install it into the Liberty JVM server using an application entry in server.xml.

## Documentation
For details on using the application to capture and process events see [ca1y.pdf](https://github.com/cicsdev/cics-event-consumer/blob/master/documentation/ca1y.pdf). Note Chapter 2 Installation is no longer relevant and should be ignored.
    
## Logging
The application uses the standard Java logging framework (java.util.logging) to write messages. These can be directed to the JVM server standard error (STDERR) file as follows: 

* Disable the JVM server.
* Create a logging.properties file on zFS to specify the logging parameters. For example create /usr/lpp/ca1y/examples/logging.properties containing the following:

```properties
# Logging levels
java.util.logging.ConsoleHandler.level=ALL
com.ibm.cics.ca1y.level=ALL

# Logging handlers
com.ibm.cics.ca1y.handlers=java.util.logging.ConsoleHandler
com.ibm.cics.ca1y.useParentHandlers=false
java.util.logging.ConsoleHandler.formatter=java.util.logging.SimpleFormatter
java.util.logging.SimpleFormatter.format=%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$s %2$s - %5$s %6$s%n

# Add the following line to the JVM server profile:
-Djava.util.logging.config.file=/usr/lpp/ca1y/examples/logging.properties
```
* Add the following to the JVM server profile.

```properties
-Djava.util.logging.config.file=/u/cockerm/ca1y/examples/logging.properties
```

* Enable the JVM server.
