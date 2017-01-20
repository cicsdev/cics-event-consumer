# Properties to send an HTTP request using CICS web support
Use these properties to send a request to an HTTP server using the CICS web support facilities.

The SupportPac uses the CICS WEB OPEN and WEB SEND commands to connect to the HTTP server and send the request. The timeout for these commands is specified in the DTIMOUT attribute of the TRANSACTION definition under which the SupportPac is started. If started as a custom event adapter or START CHANNEL command, the default transaction ID is CA1Y. If started by a LINK command, the transaction ID is that of the caller.

It is recommended to define a CICS [URIMAP resource](http://www.ibm.com/support/knowledgecenter/SSGMCP_5.3.0/com.ibm.cics.ts.internet.doc/topics/dfhtl_urioutbound.html) to specify the HTTP server details, then specify the resource name with the `http.urimap` property. CICS provides useful monitoring data and can reuse the connection when a URIMAP resource is used.

If the HTTP server returns a 401 response to indicate credentials are required, and you specify `http.urimap`, or you specify `http.uri` without a user and password, then CICS will call the [XWBSNDO](http://www.ibm.com/support/knowledgecenter/SSGMCP_5.3.0/com.ibm.cics.ts.internet.doc/topics/dfhtl_xwbsndo.html) and [XWBAUTH](http://www.ibm.com/support/knowledgecenter/SSGMCP_5.3.0/com.ibm.cics.ts.internet.doc/topics/dfhtl_xwbauth.html) exits as described in topic [Providing credentials for basic authentication](http://www.ibm.com/support/knowledgecenter/SSGMCP_5.3.0/com.ibm.cics.ts.internet.doc/topics/dfhtl_outmaking_basicauth.html) to obtain the credentials and resend the request.

Property | Usage
--- | ---
http.certificate&nbsp;=&nbsp;_label_ | Set the label of the X.509 certificate to use if connecting to the HTTP server using SSL. For more details see the [WEB OPEN](http://www.ibm.com/support/knowledgecenter/SSGMCP_5.3.0/com.ibm.cics.ts.applicationprogramming.doc/commands/dfhp4_webopen.html) command.
http.characterset&nbsp;=&nbsp;_codepage_ | Set the character set of the HTTP content. The default is iso-8859-1. For more details see the [WEB SEND](http://www.ibm.com/support/knowledgecenter/SSGMCP_5.3.0/com.ibm.cics.ts.applicationprogramming.doc/commands/dfhp4_websendclient.html) command.
http.content&nbsp;=&nbsp;_value_ | **Required.** Specify the content to send to the HTTP server. It is important that the MIME is set appropriately, otherwise the HTTP server will fail to understand the content. The default MIME text/plain will be used if not specified using the mime token.
http.method&nbsp;=&nbsp;_method_ | Set the HTTP request method as described in [RFC2616](http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html). The default is PUT.  For more details see the [WEB SEND](http://www.ibm.com/support/knowledgecenter/SSGMCP_5.3.0/com.ibm.cics.ts.applicationprogramming.doc/commands/dfhp4_websendclient.html) command.
http.retry&nbsp;=&nbsp;_number_ | Set the number of times the request should be retried. The default is 0, meaning do not retry. The request will only be retried when:<br>1. A socket error is received.<br>2. A response is not received due to a timeout.<br>3. An HTTP error response is received and the header Retry-After is present. In this case the value in seconds specified in this header is used to delay the retry. However if the delay retry is greater than 60 seconds the request is failed immediately.
http.retrydelay&nbsp;=&nbsp;_milliseconds_ | Set the time in milliseconds to wait after each HTTP failure before retrying. The default is 500ms.
http.uri&nbsp;=&nbsp;_uri_ | Set the scheme, hostname, and optionally user name, password, port, path and query string to be used to connect to the HTTP server. The URI format is described by [RFC3986](http://tools.ietf.org/html/rfc3986). This property will be ignored if http.urimap is also specified.
http.urimap&nbsp;=&nbsp;_urimap_ | Set the CICS URIMAP resource name to use to connect to the HTTP server. The URIMAP resource should have usage set to CLIENT, and include the scheme, hostname, path and optionally port and query string.

## Examples
* Send the text Hello to an HTTP server.
 ```properties
http.content=Hello
 ```
* Send a JSON document and set the MIME type to application/json.
 ```properties
http.content={json}
 ```
* Send a Common Base Event Rest document and set the MIME type to text/xml.
 ```properties
http.content={commonbaseeventrest}
 ```
* Use POST to create a new resource.
 ```properties
http.method=POST
 ```
* Use PUT to add content to an existing resource.
 ```properties
http.method=PUT``http.certificate=MyCertificate
 ```
* Delay for 10 seconds before retrying.
 ```properties
http.retrydelay=10000
 ```
* Use the CICS URIMAP resource MyServer.
 ```properties
http.urimap=MyServer
 ```
* Call the HTTP server using a URI.
 ```properties
http.uri=http://myserver.example.com/resource
 ```
* Call the HTTP server using a URI including basic authentication userid, password and a query string.
 ```properties
http.uri=http://username:password@server.example.com:8080/path?name=fred`
 ```
* Set the HTTP characterset encoding.
 ```properties
http.characterset=UTF-8
 ```
* Retry the request up to 3 times.
 ```properties
http.retry=3
 ```