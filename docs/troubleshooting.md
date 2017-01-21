# Troubleshooting
Use the following checklist to diagnose problems.

1. The application will issue the CICS abend CA1Y if it is started as a custom event adapter and the event could not be emitted.
* Ensure the steps listed under [Installation](https://github.com/cicsdev/cics-event-consumer/blob/master/README.md#installation) were followed.
* If starting the application as a CICS event adapter, ensure that CICS event processing is enabled and the event is being captured. To view the status of event processing and the number of captured events, use the CICS Explorer views available in the CICS SM perspective, under menu Operations → Event Processing.
* Review the contents of the JVM server stderr and stdout files. For example if the mail server could not be reached there will likely be javax.mail.MessagingException entries with further exception information and a backtrace.
* Review the contents of the Liberty log files, as described in [Diagnostics for Java](http://www.ibm.com/support/knowledgecenter/SSGMCP_5.3.0/com.ibm.cics.ts.java.doc/topics/dfhpjei.html).
* Enable [Java Logging](http://docs.oracle.com/javase/7/docs/technotes/guides/logging/overview.html) for the application. See [Logging](https://github.com/cicsdev/cics-event-consumer/blob/master/README.md#logging).
* If there are issues transforming XML using an XML stylesheet, check the output from JAXP by adding the following to the JVM server profile, disable and enable the JVMSERVER, then retry the scenario: `‑Djaxp.debug=1`
* If you are trying to send an email:
 * check the TCP/IP stack on the LPAR in which the application is running is able to reach the target SMTP server and there are no firewalls that block access.  For example, log onto z/OS with telnet and use: `traceroute <mail_server_hostname>`
 * Once the application prepares the email it uses the JavaMail API to interact with the mail server. You may therefore find useful diagnostic information in the [JAVAMAIL API FAQ](http://www.oracle.com/technetwork/java/faq-135477.html).
 * Check the email is not being removed as junk by your email provider.
 * If the Java stderr contains javax.mail.MessagingException: Could not connect to SMTP host: localhost, port: 25 then it is likely the mail server properties are not defined or being imported correctly. Check the encoding of the file is the same as that used on the file token. <br> For example, if the file is stored at `/usr/lpp/ca1y/examples/emailServer.properties` in EBCDIC codepage 1047, the event adapter configuration should import the properties with: <br> `import={file=/usr/lpp/ca1y/examples/emailServer.properties: encoding=Cp1047}`
* If the Java stderr contains java.io.FileNotFoundException: File '/usr/lpp/ca1y/examples/emailServer.properties' does not exist then it is likely the file path is incorrect, or the file or directory permissions are not set correctly. Note that the directory needs to have execute bit on for files to be read.
* Check you are using the latest version of the project as it may contain fixes and it will be easier to diagnose and fix your issue.
* If you are having issues creating a Java regular expression, as used in the property token.regex and tokens htmltable and texttable, you may find site http://www.regexplanet.com/advanced/java/index.html useful to try and evaluate expressions.


If you suspect the project has a bug, please use the GitHub [issue](https://github.com/cicsdev/cics-event-consumer/issues) page.
