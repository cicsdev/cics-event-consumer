# Sending an email from CICS
This SupportPac provides a choice of two interfaces to send an email from CICS.

1. Capture an event and emit it to the SupportPac event adapter.
 * Use the CICS Explorer event binding editor to define when and what information to capture from your application or system event.
 * You can also use the CICS Explorer policy definition editor to define policy actions that you want to emit as a system event.
 * Use the CICS Explorer event adapter editor to define SupportPac transaction CA1Y as the custom event adapter and specify the email headers, content, attachments, etc. using name=value properties.
 * This interface is unlikely to require changes to the application, and because it can process the event asynchronous it is unlikely to change the applications' response time or qualities of service.
 * This is the recommended approach.
* Write an application to call the SupportPac.
 * From your application, create one or more CICS containers to specify the email headers, content, attachments, etc., then issue a LINK CHANNEL command to program CA1Y, or START CHANNEL command to transaction CA1Y. Alternatively, you can use a CICS communications area (commarea) or START FROM area instead of a container, but these are restricted in size to less than 32KB and the content are required to be encoded using the CICS region local CCSID in which CA1Y executes.
 * The LINK CHANNEL and LINK COMMAREA commands will synchronously send an email from an application. Your application will wait for the email to be sent and could react if there is an error.
 * The START CHANNEL command will asynchronous send an email from an application. CICS will schedule the CA1Y transaction for execution and your application will not be aware of the success or failure of the request.

Both of these interfaces are easy to use and flexible, allowing you to specify:
* **Email address headers for recipients** including; from, to, carbon copy (cc), blind carbon copy (bcc), and reply to. These headers are specified using the Internet Engineering Task Force (IETF) RFC 822 format, for example; <sam.smith@company.com>, “John Doe” <j.doe@example.com>
* **Email subject** formatted as a single line of plain text.
* **Email content** typically formatted as plain text or HTML.
* **Email attachments** including a name and Multipurpose Internet Mail Extensions (MIME) type as defined by RFC 2046 and IANA. For example photos, Portable Document Format (PDF), or spreadsheets could be attached. The SupportPac can compress attachments into a zip to save network bandwidth and mail server storage.
* **Tokens** placed anywhere in the subject, content and other properties are automatically replaced with CICS event information items, current time & date, CICS containers, DOCTEMPLATE resources, MVS files, or zFS files. This token replacement Restrictions
The SupportPac uses the JavaMail API to interact with the mail server. The SupportPac has been tested with the Simple Mail Transfer Protocol (SMTP) service provider. The JavaMail API provides additional protocol service providers such as IMAP and POP3 that may work, but they have not been tested.is similar in concept to mail merge in word processors and mass mail systems.
* **Conversion of content and attachments** from XML into other document types. The SupportPac processes the XML in combination with your XML stylesheet (XSLT) to generate HTML, XHTML, or other XML documents. In addition the SupportPac can work in combination with the open source Apache™ FOP Project (Formatting Objects Processor) to convert XSL formatting objects (XSL-FO) into PDF, Rich Text Format (RTF), and other printed documents. For example this enables CICS event information to be combined with other XML and a stylesheet to create an invoice in PDF format that is emailed to a customer.
* **SMTP server configuration** including IP name or address, IP port, and security credentials.
* **Importing of common configuration** from zFS and other locations enabling SMTP server configuration to be secured and managed separate to the application.

The following figure shows how the SupportPac can receive requests from a variety of application and system events and from applications issuing LINK commands. The requests are sent using SMTP to any mail server on any platform, ready for retrieval by an email client, or relaying on to external email systems.

![](/assets/email.png)

## Restrictions
The SupportPac uses the JavaMail API to interact with the mail server. The SupportPac has been tested with the Simple Mail Transfer Protocol (SMTP) service provider. The JavaMail API provides additional protocol service providers such as IMAP and POP3 that may work, but they have not been tested.