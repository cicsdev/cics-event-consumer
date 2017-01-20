# General properties
Use these properties to import properties from other sources, to link to a program when there is a failure to process the request, and to define the regular expression to identify tokens.

Property | Usage
--- | ---
import&nbsp;=&nbsp;_value_ | Import additional properties.
import.private&nbsp;=&nbsp;_value_ | Import additional properties and prevent the values of these properties from appearing in log files, or being copied using tokens. This is useful if the tokens include passwords such as for mail servers.
onfailure&nbsp;=&nbsp;_program_ | Specify a CICS program to link to if there is a failure to process the request. The program has access to the containers passed to CA1Y and those created by specifying the responsecontainer token in a property.
token.regex&nbsp;=&nbsp;_pattern_ | Specify a regular expression pattern to identify the start and end of a token. Valid patterns are detailed in the Java [Pattern](http://docs.oracle.com/javase/6/docs/api/java/util/regex/Pattern.html) class. The default pattern starts a token with an opening curly bracket `{` and ends with a closing curly bracket `}`.

## Examples

* Import properties from the named file using the JVM default file encoding.
 ```properties
import={file=/path/standard.properties}
 ```
* Import properties from the named file using the UTF-8 encoding.
 ```properties
import={file=/usr/lpp/ca1y/examples/emailServer.properties:encoding=UTF-8}
 ```
* Import properties from a CICS DOCTEMPLATE resource that is cached in memory upon first use.
 ```properties
import={doctemplate=MyTemplate}
 ```
* Import properties from the named file using UTF-8 encoding and do not log or allow these properties to be copied using tokens.
 ```properties
import.private={file=/usr/lpp/ca1y/examples/emailServer.properties:encoding=UTF-8}
 ```
* An event is sent to an HTTP server defined in a URIMAP. If there is a failure, the SupportPac links to program ERRLOG to log the HTTPCONTENT container to DB2.
 ```properties
http.content=Hello from CICS. {responsecontainer=HTTPCONTENT}
http.urimap=HTTPSERV
onfailure=ERRLOG
 ```
* If you need to replace tokens in JSON data, you will need to change this pattern as JSON uses curly brackets to denote arrays.
 ```properties
token.regex=\\$\\{(.+?)\\}
 ```