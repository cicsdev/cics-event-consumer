# Properties to send an email using JavaMail
Use these properties to send an email to a SMTP mail server. Most of the properties are used by the JavaMail API to construct email headers and content, and to interact with a mail server. These and other advanced properties to secure the connection are detailed in Package [com.sun.mail.smtp](https://javamail.java.net/nonav/docs/api/com/sun/mail/smtp/package-summary.html).

You are required to set at least one of the `mail.to`, `mail.cc`, or `mail.bcc` properties.
To add an attachment to the email, define a property with the attachment contents and include a mime token to set the MIME type. The attachment name can be set using the name token.

If the email could not be delivered to one of the recipients, for example due to an incorrect email address, the SMTP server will send a failure report to the senders' email address specified in the mail.from property. Delivery failures may take some time to arrive due to interactions with relay servers.

Property | Usage
--- | ---
mail.bcc&nbsp;=&nbsp;_rfc822_ | Email address list in [RFC 822](http://www.w3.org/Protocols/rfc822/) syntax of who should receive the email as a blind carbon copy.
mail.cc&nbsp;=&nbsp;_rfc822_ | Email address list in [RFC 822](http://www.w3.org/Protocols/rfc822/) syntax of who should receive the email as a carbon copy.
mail.content&nbsp;=&nbsp;_value_ | Content of the mail, typically plain text or HTML.
mail.from&nbsp;=&nbsp;_rfc822_ | Email address in [RFC 822](http://www.w3.org/Protocols/rfc822/) syntax of who sent the email.
mail.sentdate&nbsp;=&nbsp;_abstime_ | CICS ABSTIME, returned via a EXEC CICS ASKTIME ABSTIME(), to use for the email sent date and time. If not specified and CA1Y is started by a CICS event, the date and time of the CICS event is used, otherwise the Java date and time used.
mail.host&nbsp;=&nbsp;_host_ | SMTP server host name.
mail.password&nbsp;=&nbsp;_password_ | SMTP user password.
mail.reply.to&nbsp;=&nbsp;_rfc822_ | Email address in [RFC 822](http://www.w3.org/Protocols/rfc822/) syntax of who replies should be sent.
mail.smtp.auth=_true/false_ | If true, the userid and password will be used to authenticate with the mail server.
mail.smtp.port&nbsp;=&nbsp;_port_ | SMTP server port number. The default is 25.
mail.subject&nbsp;=&nbsp;_value_ | One line of text describing the email subject.
mail.to&nbsp;=&nbsp;_rfc822_ | Email address list in [RFC 822](http://www.w3.org/Protocols/rfc822/) syntax of who should receive the email.
mail.transport.protocol&nbsp;=&nbsp;_protocol_ | Mail server protocol.
mail.user&nbsp;=&nbsp;_userid_ | SMTP user name.

## Examples
* RFC 822 examples.
 ```properties
mail.to=user@example.com
 ```
 ```properties
mail.to="Joe Bloggs" <j.bloggs@example.com>
 ```
 ```properties
mail.to="Joe Bloggs" <j.bloggs@example.com>, “A N Other” <a.n.other@example.com>
 ```
* To set the email send date.
```properties
mail.sentdate=003609661860917
 ```
* Complete [emailServer.properties](../examples/emailServer.properties)

### Examples using tokens for email attachments
You can add one or more properties to an email as attachments. Email clients can typically preview and save attachments, or start other applications to handle them. Tokens can be used in combination to specify the attachment contents, name and mime type.

Examples of how to use tokens to add a property as an attachment:
* Attach a file from zFS as a picture with the default name picture.png and default MIME type image/png.
 ```properties
picture={file=/path/picture.png:binary}
 ```
* Attach a terms and conditions PDF with the default MIME type application/pdf and specify the name Terms and Conditions.pdf.
 ```properties
terms={name=Terms and conditions.pdf} {file=/path/terms.pdf:binary}
 ```
* Attach a zip file YourDocuments.zip that contains a PDF and picture.
 ```properties
picture={file=/path/picture.png:binary}
terms={file=/path/terms.pdf:binary}
attach={zip=YourDocuments.zip:include=picture,terms}
 ```
* Attach an invoice PDF created from the XML in the property and a stylesheet:
 ```properties
myXSLT={file=/path/stylesheet.xslt:encoding=UTF-8}
invoice={name=Invoice.pdf} {mime=text/xml:to=application /pdf:xslt=myXSLT}<?xml version="1.0" encoding="UTF-8" ?> <name>Joe Bloggs</name>
 ```