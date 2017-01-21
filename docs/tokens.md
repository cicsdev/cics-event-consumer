# Tokens
Tokens are used to dynamically create and merge content from a variety of sources.

Each property passed to the SupportPac has a value, and the value can contain tokens that are replaced with content as described in the tables below. For example the property `mail.content={file=/usr/file.txt}` will have the value replaced with the contents of the zFS file /usr/file.txt.

The SupportPac identifies a token by searching the property value for a regular expression. By default the regular expression requires a token is prefixed with a { character and suffixed with a } character. You can change the regular expression using the token.regex property.

Tokens and their parameters are case sensitive, and the parameters are required to be in the order specified. Some parameters are optional. 

A token cannot be placed within another token. 

If the token is invalid, for example it specifies a file name that does not exist, it will be removed and may result in messages being written to the Java stderr.

Property | Usage
--- | ---
_propertyName_ | Token is replaced with the contents of the property. Properties considered private, i.e. loaded using the import.private property, will not be replaced.
_cicsContainerName_ | If the container is a character (CHAR) type, the token is replaced with the contents of the CICS container. <br><br> If the container is a binary (BIT) type, the contents are attached to the property, or appended if one already exists. This will also set the attachment name to the container name if it is not already set using the token name.
_jvmPropertyName_ | The token is replaced with the value of the specified JVM system property. You can set a JVM system property in the CICS JVM profile using the convention -Dproperty=value.
commonbaseeventrest | Token is replaced with an XML document referred to as the [common base event REST format](http://www.ibm.com/support/knowledgecenter/SSGMCP_5.3.0/com.ibm.cics.ts.eventprocessing.doc/reference/dfhep_event_processing_cberformat.html). In addition to header elements, all event business information items are automatically included. Additional properties can be added using the emit token. The property MIME is set to text/xml.
commonbaseevent | Token is replaced with an XML representation referred to as the [common base event format](http://www.ibm.com/support/knowledgecenter/SSGMCP_5.3.0/com.ibm.cics.ts.eventprocessing.doc/reference/dfhep_event_processing_cbeformat.html). In addition to header elements, all event business information items are automatically included. Additional properties can be added using the emit token. The property MIME is set to text/xml.
datetime=_format_ | Token is replaced with the date and time using the Java [SimpleDateFormat](http://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html) format as detailed under heading Date and Time Patterns. If CA1Y is started as a custom event adapter, the date and time is taken from when the event was captured. Otherwise the current date and time as known by the JVM server is used. <br><br> If the format is not specified `{datetime=}` the token is replaced with the default Java date and time format. For Java 7 this is: dow mon dd hh:mm:ss zzz yyyy
doctemplate=_name_ <br> :addPropertiesAsSymbols <br> :binary | Token is replaced with the contents of the CICS DOCTEMPLATE resource named by name. The DOCTEMPLATE type should be EBCDIC. Note for improved performance CICS will cache DOCTEMPLATE resources once first used. The cached copy can be discarded by disabling then re-enabling the resource. <br><br> The optional `:addPropertiesAsSymbols` indicates all properties are added as symbols to the document. CICS will replace symbols in the template with the values of the properties. Symbols used in document templates must follow the naming rules outlined in topic [Using symbols in document templates](https://www.ibm.com/support/knowledgecenter/SSGMCP_5.3.0/com.ibm.cics.ts.applicationprogramming.doc/topics/dfhp3_doc_symbol_indoc.html?lang=en). <br><br> The optional `:binary` indicates the contents are treated as binary and the DOCTEMPLATE type should be binary.
emit | Include the value of this property when processing tokens commonbaseeventrest, commonbaseevent, and json.
file=_filename_ <br> :encoding=_codepage_ <br> :binary | Token is replaced with the contents of the fully qualified file on zFS, where the contents are loaded using the default [Java character set encoding](http://docs.oracle.com/javase/7/docs/technotes/guides/intl/encoding.doc.html). Note the contents are not cached. If caching is required use a DOCTEMPLATE resource. <br><br> The optional `:encoding` indicates the contents are read using the specified [Java character set encoding](http://docs.oracle.com/javase/7/docs/technotes/guides/intl/encoding.doc.html). <br><br> The optional `:binary` indicates the contents are read as binary.
file=//'_datasetName_' <br> :encoding=_codepage_ <br> :binary | Token is replaced with the contents of the fully qualified dataset. If the dataset is a PDS, a member must be specified. See the JZOS [Zfile](http://www.ibm.com/developerworks/java/zos/javadoc/jzos/com/ibm/jzos/ZFile.html) constructor for more examples. Note the contents are not cached. If caching is required use a CICS DOCTEMPLATE resource. <br><br> The optional `:encoding` indicates the contents are read using the specified [Java character set encoding](http://docs.oracle.com/javase/7/docs/technotes/guides/intl/encoding.doc.html). <br><br> The optional `:binary` indicates  the contents are read as binary.
file=//DD:_ddName_ <br> :encoding=_codepage_ <br> :binary | Token is replaced with the contents of the dataset specified by the DD card in the CICS JCL. If the dataset is a PDS a member can be specified. Note the contents are not cached. If caching is required use a DOCTEMPLATE resource. <br><br> The optional `:encoding` indicates the contents are read using the specified [Java character set encoding](http://docs.oracle.com/javase/7/docs/technotes/guides/intl/encoding.doc.html). <br><br> The optional `:binary` indicates  the contents are read as binary.
ftp=_fileName_<br>:server=_hostName_ <br> :username=_userName_ <br> :userpassword=_password_ <br> :transfer=ascii/binary <br> :mode=localactive/localpassive <br> :epsv=false/true <br> :protocol=_protocol_ <br> :trustmgr=_trustManager_ <br> :datatimeout=_seconds_ <br> :proxyserver=_hostName_ <br> :proxyusername=_proxyUserName_ <br> :proxypassword=_password_ | Token is replaced with the contents of the specified file retrieved from the FTP server. The Apache Creative Commons commons-net FTPClient framework is used to interact with the FTP server and documents these parameters. <br><br> fileName and hostName are required and all other parameters are optional. <br><br> fileName can include a relative or absolute path as understood by the FTP server. <br><br> hostName can include ':' followed by a port number to override the default of 21. <br><br> The default for `:username` is anonymous. <br><br> The default for `:transfer` is ascii. <br><br> The default for `:mode` is localactive. <br><br> The default for `:epsv` (extended passive mode) is false.
hex=_property_ | Token is replaced with a hexadecimal string representation of the contents of the specified property.
htmltable <br> :properties=_pattern1_ <br> :containers=_pattern2_ <br> :summary | Token is replaced by an HTML document containing one or two tables. The first table has a row detailing each property whos name matches the Java regular expression pattern1. The second table has a row detailing each container in the default channel whos name matches the Java regular expression pattern2. Java regular expressions are described in the Java [Pattern](http://docs.oracle.com/javase/6/docs/api/java/util/regex/Pattern.html) class. The summary option limits the length of property values to 32 bytes. Note: Properties loaded from import.private will have their values excluded.
json <br> :properties=_pattern1_ | Token is replaced with a JSON representation of the CICS event and the MIME for the property is set to application/json. The event business information items that match pattern1 are included. Additional properties can be added using the emit token. Note: JSON makes use of the { and } characters as start and end tags for the document and arrays. Add the token noinnertokens to prevent the JSON being parsed for tokens. Alternatively use the tokens.regex property to change that characters used to mark the start and end of tokens. <br><br> The optional `:properties` indicates only properties with a name that follows the specified pattern are included.
link=_cicsProgram_ | Link to the specified CICS program with the current channel.
mime=_mediatype_ <br> :to=_mime_ <br> :xslt=_property_ | Set the MIME media type of the property as defined by [IANA media types](http://www.iana.org/assignments/media-types). When sending an email, all properties with a MIME are attached to the email. Using token file=filename<br>:binary automatically sets the MIME based on the file name extension. <br><br> The optional `:to` indicates the MIME type the property should be converted to. <br><br> The optional `:xslt` indicates when converting the property the specified XSLT should be used for the conversion.
name=_name_ | Set the name of the attachment. If the token file is used in the property, the default name is the file name and extension.
name=_property_ | Set the name of the attachment to the contents of the named property.
noinnertokens | Tokens for this property will be processed, however content inserted as a result of resolving tokens will not be searched for tokens. Use this to avoid searching within the content from files, doctemplates, or JSON documents for tokens.
putcontainer=_container_ | Copies the content from the current resolved property into a CICS container. If the property contains binary data the container will be type BIT, otherwise type CHAR.
responsecontainer=_container_ | Copy the contents of the property to the named CICS container once all properties have been processed. The container will be type BIT for binary content, otherwise will be type CHAR.
systemsymbol=_pattern_ | Token is replaced with the resolved MVS system symbol pattern specified. The pattern takes the form &SYMBOL. or &SYMBOL(n:m). for a substring as described by [substituteSystemSymbols](http://www.ibm.com/support/knowledgecenter/SSYKE2_8.0.0/com.ibm.java.zsecurity.api.80.doc/com.ibm.jzos/com/ibm/jzos/ZUtil.html?lang=en-us#substituteSystemSymbols(java.lang.String, boolean)). Topic [What are System Symbols?](http://www.ibm.com/support/knowledgecenter/SSLTBW_2.1.0/com.ibm.zos.v2r1.ieab600/symwhat.htm?lang=en) in the z/OS MVS JCL Reference describes how to set and display system symbols.
nomoretokens | Token processing is stopped for the remainder of the property. Use this when you know the property does not require token replacement.
texttable <br> :properties=_pattern1_ <br> :containers=_pattern2_ | Token is replaced by a hexadecimal dump of the properties and containers whos name match the Java regular expression pattern. Java regular expressions are details in the Java [Pattern](http://docs.oracle.com/javase/6/docs/api/java/util/regex/Pattern.html) class. Note: Properties loaded from import.private will have their values excluded.
transform=_property_  <br> :xmltransform=_resource_ | Token is replaced with the results from passing the specified property and XMLTRANSFORM resource to the CICS command TRANSFORM DATATOXML. For information on creating the required XMLTRANSFORM resource see [Mapping and transforming application data and XML](http://www.ibm.com/support/knowledgecenter/SSGMCP_5.3.0/com.ibm.cics.ts.applicationprogramming.doc/datamapping/datamappings.html).
trim | The leading and trailing spaces are removed from the property.
zip=_zipName_ <br> :include=_propertyList_ | Compress the contents of the property into a zip named by zipName. If zipName is not specified, the property name and extension .zip is used. <br><br> The optional `:include` indicates the list of properties separated commas to be included.

## Additional tokens available with event processing

The following tokens are only available when the SupportPac is started as an event adapter.

Property | Usage
--- | ---
eventInformation | The named event business information item, as defined using the CICS Explorer event binding editor on the Specifications tab.
EPCX_VERSION <br> EPCX_SCHEMA_VERSION <br> EPCX_SCHEMA_RELEASE <br> EPCX_EVENT_BINDING <br> EPCX_CS_NAME <br> EPCX_EBUSERTAG <br> EPCX_BUSINESSEVENT <br> EPCX_NETQUAL <br> EPCX_APPLID <br> EPCX_TRANID <br> EPCX_USERID <br> EPCX_ABSTIME <br> EPCX_EVENT_TYPE <br> EPCX_PROGRAM <br> EPCX_RESP <br> EPCX_UOWID | The event binding or task information. See topic [EPCX - Event Processing Context Container](http://www.ibm.com/support/knowledgecenter/SSGMCP_5.3.0/com.ibm.cics.ts.doc/dfhs4/DFHEPCXK.html) in the CICS Information Center for their definitions.
EPAP_VERSION <br> EPAP_ADAPTER_NAME <br> EPAP_RECOVER | Contents of the specified field from EP adapter configuration. Refer to topic [EPAP - Event Processing Adaptparm Container](http://www.ibm.com/support/knowledgecenter/SSGMCP_5.3.0/com.ibm.cics.ts.doc/dfhs4/DFHEPAPK.html) in the CICS Information Center for their definitions.

## Additional tokens available with LINK or START commands
The following tokens are only available when the SupportPac is started by the LINK or START CHANNEL commands.

Property | Usage
--- | ---
TASK_TRANID | The current task transaction ID.
TASK_USERID | The current task user ID.
TASK_PROGRAM | The current program name.
TASK_NUMBER | The current task number.
REGION_SYSID | The CICS region system ID.
REGION_APPLID | The CICS region application ID.

## Examples
* Replace the token with the contents of MyContainer.
 ```properties
{MyContainer}
 ```
* The JVM property myProperty is set in the JVM profile, and the {myProperty} token is replaced with “Greetings”. In the JVM profile add `-DmyProperty=Greetings` then the value of myProperty can be used as a token:
 ```properties
 {myProperty}
  ```
* The CICS set JVM property com.ibm.cics.jvmserver.configroot is set to JVM server configuration root directory. This property was new at CICS TS V5.2.
 ```properties
 {com.ibm.cics.jvmserver.configroot}
 ```
* Include this property when creating a table
 ```properties
MyProperty={emit}Hello
java.http.content={json}
 ```
* Replace the token with the contents of the specified zFS file using an encoding.
 ```properties
{file=/path/name.ext:encoding=US-ASCII}
 ```
 ```properties
{file=/path/name.ext:encoding=Cp1047}
 ```
 ```properties
{file=/path/name.ext:encoding=UTF-8}
 ```
* Replace the token with the contents of the specified MVS file.
 ```properties
 {file=//'HLQ.MYSEQ'}
  ```
  ```properties
 {file=//'HLQ.MYPDS(SAMPMEM)'}
  ```
  ```properties
 {file=//'HLQ.MYSEQ':encoding=Cp1047}
  ```
   ```properties
 {file=//'HLQ.MYSEQ':binary}
  ```
   ```properties
 {file=//DD:MYCARD(SAMPMEM)}
  ```
   ```properties
 {file=//DD:MYCARD(SAMPMEM):encoding=Cp1047}
  ```
  ```properties
 {file=//DD:MYCARD(SAMPMEM):binary}
  ```
* Replace the token with the contents of the file from FTP:
 ```properties
 {ftp=/path/file1.txt:server=my.host.com}
  ```
 ```properties
{ftp=/path/file1.txt:server=my.host.com:username=johndoe:password=secret}
 ```
* Include all properties and containers.
 ```properties
 {htmltable}
  ```
* Include only properties.
 ```properties
 {htmltable:properties=.*}
  ```
* Include only properties whos name does not start with “mail”.
 ```properties
{htmltable:properties=(?!mail.*).*
 ```
* Include only containers named MyContainer1 and MyContainer2.
 ```properties
{htmltable:containers=MyContainer1|MyContainer2}
 ```
* Set the property MIME type.
 ```properties
{mime=text/plain}
 ```
 ```properties
{mime=text/html}
 ```
 ```properties
{mime=text/xml}
 ```
 ```properties
{mime=image/jpeg}
 ```
 ```properties
{mime=application/octet-stream}
 ```
* Convert the property.
 ```properties
{mime=text/xsl:to=application/pdf}
 ```
 ```properties
{mime=text/xml:to=application/pdf:xslt=MyStyleSheet}
 ```
* Set the name of the attachment to 'My photo.jpg'
 ```
{name=My photo.jpg}
 ```
* Set the name of the attachment to 'Invoice' followed by todays date.
    ```properties
MyFileName=Invoice – {datetime=yyyy-MM-dd}.pdf
MyAttachment={name=MyFileName}
    ```
* Include all properties and containers.
 ```properties
{texttable}
 ```
* Include only properties.
 ```properties
{texttable:properties=.*}
 ```
* Include only properties whos name does not start with “mail”.
 ```properties
 {texttable:properties=(?!mail.*).*
  ```
* Include only containers named MyContainer1 and MyContainer2.
 ```properties
{texttable:containers=MyContainer1|MyContainer2}
 ```
* Attach a ZIP file.
 ```properties
{zip=MyZip.zip}
 ```
 ```properties
{zip=MyZip.zip:include=property1}
 ```
 ```properties
{zip=MyZip.zip:include=property1,property2}
 ```
* Replace the token with a system symbol.
 ```properties
{systemsymbol=&SYSNAME.}
 ```
 ```properties
{systemsymbol=&SYSPLEX.}
 ```
 ```properties
{systemsymbol=&SYSCLONE.}
 ```
* Transform the CustomerData property using the XML transform CustomerXML.
 ```properties
{transform=CustomerData:xmltransform=CustomerXML}
 ```
* Replace the token with the date and time.
 ```properties
{datetime=yyyy.MM.dd 'at' HH:mm:ss z}
 ```