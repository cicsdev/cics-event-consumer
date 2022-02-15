# cics-event-consumer

cics-event-consumer is a Java EE 7 web application that _consumes_ events produced by [CICS Transaction Server](https://www.ibm.com/products/cics-transaction-server). Events are produced as a result of a CICS application event, a CICS system event, or a CICS policy event action. The application can also be called directly using the CICS LINK or START command.

cics-event-consumer can be called:

* by CICS directly as a [custom event processing adapter](https://www.ibm.com/support/knowledgecenter/SSGMCP_5.6.0/reference-applications/event-processing/dfhep_event_processing_adapters.html).
* by the CICS [HTTP EP adapter](https://www.ibm.com/support/knowledgecenter/SSGMCP_5.6.0/reference-applications/event-processing/dfhep_event_processing_http_adapter.html) with events formatted using the [common base event REST format](https://www.ibm.com/support/knowledgecenter/SSGMCP_5.6.0/reference-applications/event-processing/dfhep_event_processing_cberformat.html).
* by your program using the [EXEC CICS LINK PROGRAM\(CA1Y\)](https://www.ibm.com/support/knowledgecenter/SSGMCP_5.6.0/reference-applications/commands-api/dfhp4_link.html) command, passing a COMMAREA or CHANNEL.
* by your program using the [EXEC CICS START TRANSID\(CA1Y\) CHANNEL\(\)](https://www.ibm.com/support/knowledgecenter/SSGMCP_5.6.0/reference-applications/commands-api/dfhp4_starttransidchannel.html) command.

The received event, channel, or commarea can be formatted and:

* sent as an email using the [Simple Mail Transfer Protocol \(SMTP\)](https://en.wikipedia.org/wiki/Simple_Mail_Transfer_Protocol).
* sent as an instant message using the [Extensible Messaging and Presence Protocol \(XMPP\)](https://en.wikipedia.org/wiki/XMPP).
* stored in a zFS file, MVS file, or a CICS defined VSAM dataset.
* stored in a CICS temporary storage or temporary data queue.
* written out as a message to the MVS operator console.
* submitted as an MVS job.

The application was originally released as the [IBM CA1Y: Send email from CICS Transaction Server for z/OS](http://www-01.ibm.com/support/docview.wss?uid=swg24033197) and referred to here as CA1Y for short.

## Requirements

To install or make changes to the application:

* [IBM CICS SDK for Java EE, Jakarta EE and Liberty](https://ibm.github.io/mainframe-downloads/eclipse-tools.html) 5.3.0.8, or later
* or any IDE that supports usage of the dependencies defined in [pom.xml](./projects/com.ibm.cics.ca1y.web/pom.xml) 

To run the application:

* CICS TS V5.3 with APAR [PI63005](http://www.ibm.com/support/docview.wss?crawler=1&uid=swg1PI63005), or later.
* A configured CICS integrated-mode Liberty JVM server, as described in topic [Configuring a Liberty JVM server](https://www.ibm.com/support/knowledgecenter/SSGMCP_5.6.0/configuring/java/config_jvmserver_liberty.html).

## Installation

1. Define, install and enable a CICS TRANSACTION resource with attributes NAME\(CA1Y\), PROGRAM\(CA1Y\), TASKDATALOC\(ANY\).
2. Update the Liberty server configuration file server.xml to include the following features:

   ```xml
   <feature>cicsts:link-1.0</feature>
   <feature>jaxrs-2.0</feature>
   <feature>jaxb-2.2</feature>
   <feature>javaMail-1.5</feature>
   ```

3. Clone this repository, or download the repository [cics-event-consumer-main.zip](https://github.com/cicsdev/cics-event-consumer/archive/main.zip) and expand it.
4. In CICS Explorer, select `File` → `Import...` → `Existing Projects into Workspace` → `Select root directory` → `Browse` and select the repository `projects` directory. Select all the projects, then `Copy projects into workspace`, then `Finish`.
5. If your JVMSERVER resource name is not DFH$WLP, expand project com.ibm.cics.ca1y.web.cicsbundle, then edit com.ibm.cics.ca1y.web.warbundle, and update the value for jvmserver.
6. Export the `com.ibm.cics.ca1y.web.cicsbundle` CICS Bundle project using the wizard `Export Bundle Project to z/OS UNIX File System...` to a directory on zFS such as /usr/lpp/ca1y/com.ibm.cics.ca1y.web.cicsbundle\_1.8.0
7. Define, install and enable the CICS BUNDLE resource with attributes NAME\(CA1Y\), BUNDLEDIR\(/usr/lpp/ca1y/com.ibm.cics.ca1y.web.cicsbundle\_1.8.0\).

Alternatively you can export the `com.ibm.cics.ca1y.web` project as a WAR file and install it into the Liberty JVM server using the dropins directory, or add an application entry in server.xml as described in topic [Deploying web applications directly to a Liberty JVM server](https://www.ibm.com/support/knowledgecenter/SSGMCP_5.6.0/applications/deploying/create_libertyapp.html).

## Usage

See the [documentation](https://cicsdev.github.io/cics-event-consumer/) and the [examples](./examples).

## Motivation

This project was started in 2012 as to demonstrate how to write a CICS event adapter, and how the many APIs available in Java could be used to consume and process the events. Several customers raised the requirement to send an email from a CICS application asynchronously to the task, and the combination of event processing and the JavaMail API lead to the project being made available. As detailed in the [CHANGELOG](./CHANGELOG.md), the project evolved to meet new requirements - some motivated by customer need, and others to learn about Java, CICS, and JZOS APIs. It was moved to GitHub and the license changed to encourage community engagement.

## License

This project is licensed under [Apache License Version 2.0](./LICENSE).

