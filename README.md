# cics-event-consumer

cics-event-consumer is a Java EE 7 web application that _consumes_ events produced by [CICS Transaction Server](https://www.ibm.com/products/cics-transaction-server). Events are produced as a result of a CICS application event, a CICS system event, or a CICS policy event action. The application can also be called directly using the CICS LINK or START command.

cics-event-consumer can be called:

* by CICS directly as a [custom event processing adapter](https://www.ibm.com/docs/en/cics-ts/6.x?topic=formats-event-processing-adapters).
* by the CICS [HTTP EP adapter](https://www.ibm.com/docs/en/cics-ts/6.x?topic=adapters-http-ep-adapter) with events formatted using the [common base event REST format](https://www.ibm.com/docs/en/cics-ts/6.x?topic=formats-common-base-event-rest-format).
* by your program using the `EXEC CICS LINK PROGRAM(CA1Y)`command, passing a COMMAREA or CHANNEL.
* by your program using the `EXEC CICS START TRANSID(CA1Y) CHANNEL()` command.

The received event, channel, or commarea can be formatted and:

* sent as an email using the [Simple Mail Transfer Protocol \(SMTP\)](https://en.wikipedia.org/wiki/Simple_Mail_Transfer_Protocol).
* sent as an instant message using the [Extensible Messaging and Presence Protocol \(XMPP\)](https://en.wikipedia.org/wiki/XMPP).
* stored in a zFS file, MVS file, or a CICS defined VSAM dataset.
* stored in a CICS temporary storage or temporary data queue.
* written out as a message to the MVS operator console.
* submitted as an MVS job.

The application was originally released as the *IBM CA1Y: Send email from CICS Transaction Server for z/OS* and referred to here as CA1Y for short.

## Requirements

* [IBM CICS SDK for Java EE, Jakarta EE and Liberty](https://ibm.github.io/mainframe-downloads/eclipse-tools.html) or any IDE that supports usage of the dependencies defined in [pom.xml](cics-event-consumer-ca1y/pom.xml) 
* CICS TS V5.5 or later
* A CICS integrated-mode Liberty JVM server, as described in topic [Configuring a Liberty JVM server](https://www.ibm.com/docs/en/cics-ts/6.x?topic=server-configuring-liberty-jvm).

## Installation


### Building the Example

You can build the sample using an IDE of your choice, or the sample can be built using the supplied Maven build files to produce a WAR file and optionally a CICS Bundle archive.

#### Maven (command line)

First install the generated JAR file into the local Maven repository by running the following Maven command in a local command prompt

```sh
mvn org.apache.maven.plugins:maven-install-plugin:3.1.3:install-file -Dfile=cics-event-consumer-ca1y/WebContent/WEB-INF/lib/com.ibm.etools.marshall.runtime_6.1.200.v20120502_1750.jar -DgroupId=com.ibm.cicsdev -DartifactId=com.ibm.etools.marshall.jar -Dversion=1.0 -Dpackaging=jar -DlocalRepositoryPath=cics-event-consumer-ca1y/local-repo
```

Run the following in a local command prompt which will create a WAR file for deployment.

```sh
mvn clean verify
```

This creates a WAR file in the `target` directory. 

If building a CICS bundle ZIP the CICS bundle plugin bundle goal is driven using the maven verify phase. The CICS JVM server name can be modified in the <cics.jvmserver> property in the [`pom.xml`](cics-event-consumer-bundle/pom.xml) to match the required CICS JVMSERVER resource name, or alternatively can be set on the command line as follows. 

```sh
mvn clean verify -Dcics.jvmserver=MYJVM
```


### To start a JVM server in CICS:

1. Enable Java support in the CICS region by setting the USSHOME and the JVMPROFILEDIR SIT parameters.
1. Define a Liberty JVM server called DFHWLP using the CICS supplied sample definition DFHWLP in the CSD group DFH$WLP.
1. Copy the CICS sample DFHWLP.jvmprofile zFS file to the CICS JVMPROFILEDIR directory and ensure the JAVA_HOME variable is set correctly.
1. Add the following  features to the Liberty server.xml depending on your version of Java EE. 

   ```xml
   <feature>cicsts:link-1.0</feature>
   <feature>jaxrs-2.0</feature>
   <feature>jaxb-2.2</feature>
   <feature>javaMail-1.5</feature>
   ```
1. Install the DFHWLP JVM server resource and ensure it becomes enabled.

### To deploy the samples into a CICS region:

1. Export the CICS bundle project to a zFS directory. The samples use the directory `/u/ca1y/com.ibm.cics.ca1y.web.cicsbundle_1.8.1`
1. Using the supplied [DFHCSDUP file](examples/DFHCSD.txt) create a CICS transaction CA1Y and a CICS BUNDLE definition referencing the zFS directory created in step 1
1. Install the CICS BUNDLE and TRANSACTION resoures.
1. Download and compile the supplied COBOL programs and deploy into CICS.

Alternatively you can export the cics-event-consumer-ca1y project as a WAR file and install it into the Liberty JVM server using the dropins directory, or add an application entry in server.xml as described in topic [Deploying web applications directly to a Liberty JVM server](https://www.ibm.com/docs/en/cics-ts/6.x?topic=dajs-deploying-enterprise-java-applications-directly-liberty-jvm-server).

## Usage

See the [documentation](./SUMMARY.md) and the [examples](./examples).

## Motivation

This project was started in 2012 as to demonstrate how to write a CICS event adapter, and how the many APIs available in Java could be used to consume and process the events. Several customers raised the requirement to send an email from a CICS application asynchronously to the task, and the combination of event processing and the JavaMail API lead to the project being made available. As detailed in the [CHANGELOG](./CHANGELOG.md), the project evolved to meet new requirements - some motivated by customer need, and others to learn about Java, CICS, and JZOS APIs. It was moved to GitHub and the license changed to encourage community engagement.

## License

This project is licensed under [Apache License Version 2.0](./LICENSE).

