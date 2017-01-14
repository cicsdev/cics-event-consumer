# Changelog

## Version 1.8.0 - January 2017 - in development
* Move project from [IBM SupportPac CA1Y](http://www-01.ibm.com/support/docview.wss?uid=swg24033197) to [cics-event-consumer](https://github.com/cicsdev/cics-event-consumer).
* New support to use the Java JAX-RS framework to send HTTP requests to a server.
* Breaking changes:
  * Change packaging from an OSGi application into a web application (.war) that results in the runtime requirement changing from an OSGi JVM server to a Liberty JVM server.

## Version 1.7.1 - June 2016
* The doctemplate token has a new addPropertiesAsSymbols option to add the properties passed to CA1Y as document symbols. The symbols can then be used in the template and will be substituted by CICS.
* New trim token can be used to removed leading and trailing spaces from the property. This is useful to remove unwanted spaces from COBOL strings that are typically padded with spaces.

## Version 1.7.0 - February 2016
* New support to call the SupportPac from a z/OS JCL batch job, a USS script, or using the CICS command START FROM().
* New texttable token will create a hexadecimal dump of the conents of all properties and containers, or only those whos name matches a specified regular expression. The htmltable token provides similar data but in an HTML table form suitable for use in an email.
* Fix to set the email header time and date to the CICS local time and date (ABSTIME) when the event was captured. If you wish to set an alternative date and time, set the new mail.sentdate property.
* Fix to not issue an abend when CICS is unable to capture a data item. For example if your event binding specified a FILE ENABLE STATUS system capture point and made use of DSNAME, but the file resource did not specify a data set name then DSNAME will not be captured.

## Version 1.6 - February 2014
* New support for specifying properties in a CICS commarea.
* New support to read JVM system properties.
* Optimization to reduce the number of JNI calls to get CICS containers.
* The property mail.propstable has been replaced with token htmltable to allow it to be used in more situations.
* Fixes to allow UTF-8 characters in the mail subject and content, and to allow emails to be sent when there is a subject but no content.
* Fix to not overwrite a property alternate name with a container name.

## Version 1.5 - December 2013
* New support to link to a CICS program with the current channel using the link token.
* New support to copy the data from a property into a container using the putcontainer token.
* New support for using the Saxon XSLT and XQuery processor to transform XML into another XML or xHTML document.
* New support to send an HTTP request using the http.content property that provides options to retry after network failures.
* New support to LINK to a CICS program if there is a failure to process the request using the onfailure property.
* New  commonbaseevent, commonbaseeventrest, and json tokens to create XML and JSON documents that represent a CICS event.
* New nomoretokens and noinnertokens tokens to avoid unnecessary searching for tokens in large properties.
Fix. In V1.3 and V1.4 when a file token was used to read an MVS file it remained open until the JVMSERVER was disabled. In V1.5 the file is closed after being read.

## Version 1.4 - September 2013
* New support to submit an MVS job using the mvsjob.content property.
* New support to issue an MVS console message using the mvswto.content property.
* New support to get files from an FTP server using the ftp token.
* New support to insert z/OS system symbols using the systemsymbol token.
* New support to insert a hexadecimal representation of a property using the hex token.
name token can now set the name of a property to the value of another property, useful to dynamically name attachments. For example, you can include a date stamp in the name.

## Version 1.3 - May 2013
* New support to read sequential datasets and partitioned datasets (PDS) using tokens file=//'dataset' and file=//DD:ddName.
* New support to compress one or more properties into a zip file using tokens zip=zipFilename and zip=zipFilename:include=propertyList.
* New support to write content to a TD or TS queue using the queue properties.
* New support to transform application data into XML using the CICS XMLTRANSFORM resource with the token transform=property:xmltransform=resource.
* Added example logging properties.

## Version 1.2 - April 2013
* New support to read DOCTEMPLATE resources using the doctemplate token.
* Fix for error “java.lang.NoSuchMethodError: com/ibm/cics/server/Container.putString(Ljava/lang/String;)” when using LINK to call SupportPac V1.1 with CICS TS V4.2.

## Version 1.1 - February 2013
* New support for converting XSL Formatting Objects (XSL-FO) into PDF or other print documents. This support requires the Apache FOP Project.
* New support for converting XML using XSLT (Extensible Stylesheet Language Transformations) into other document types.
* New support for returning properties to the application using the token returncontainer. For example the application can use the SupportPac to generate a PDF that is returned to the application for archiving or returning to a client.
* The MIME type for mail contents and other properties is now set using token mime. The properties mail.contentmime and mail.attmime.n from version 1 are no longer used.
* A property is now automatically attached to the email if it includes the mime token. The property mail.att.n from version 1 is no longer used to specify email attachments.
* A property is now named using the name token. The property mail.attname.n from version 1 is no longer used.
* Log messages are now written using the Java SE java.util.logging package for greater flexibility to select the log records of interest, directing log records to sets of files, and integration with other Java applications.
* Breaking changes:
  * The property mail.log.success from version 1 is no longer used. When an email is successfully sent a log record is written if the log level is set to INFO.
  * The property mail.log.fail from version 1 is no longer used. When a failure occurs a log record is written if the log level is set to WARNING.
  * The properties log.tokens and log.epconversions from version 1.0 are no longer used. The processing of tokens and adding event processing information items are written if the log level is set to FINE.

## Version 1 - October 2012
* Support for sending emails and attachments.