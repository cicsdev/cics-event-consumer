# Properties to send an HTTP request using Java HTTP
Use these properties to send a request to an HTTP server using the Java JAX-RS framework.

Property | Usage
--- | ---
java.http.content&nbsp;=&nbsp;_value_ | **Required**. Specify the content to send to the HTTP server. It is important that the MIME is set appropriately, otherwise the HTTP server will fail to understand the content. The default MIME text/plain will be used if not specified using the mime token.
java.http.filter._name_&nbsp;=&nbsp;_value_ | Set a property that will be made available to the HTTP filter class on the parameter javax.ws.rs.client.ClientRequestContext using the getProperty() method. <br> For example: `java.http.filter.access-token=abcd` <br> can then used in the filter class: <br> `accessToken=clientRequestContext.getProperty(“java.http.filter.access-token)`
java.http.filterclass&nbsp;=&nbsp;_classname_ | Set the class to be registered as a [JAX-RS filter](http://cxf.apache.org/docs/jax-rs-filters.html) using the  javax.ws.rs.client.WebTarget.register() method. This class is required to implement the interface [jaxrs.ws.rs.client.ClientRequestFilter](https://docs.oracle.com/javaee/7/api/javax/ws/rs/client/ClientRequestFilter.html) and/or [jaxrs.ws.rs.client.ClientResponseFilter](https://docs.oracle.com/javaee/7/api/javax/ws/rs/client/ClientResponseFilter.html). This class can be used to perform custom pre- or post-processing of HTTP requests, for example to add security tokens that need to be retrieved from a secure store. The class should be made available on the classloader, for example using a Liberty global [shared library](http://www.ibm.com/support/knowledgecenter/SS7K4U_liberty/com.ibm.websphere.wlp.zseries.doc/ae/cwlp_sharedlibrary.html) and packaged as a .jar at the location `${shared.config.dir}/lib/global`
java.http.header.name&nbsp;=&nbsp;_value_ | Set an HTTP header name with value.
java.http.method&nbsp;=&nbsp;_method_ | Set the HTTP request method as described in [RFC2616](http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html). The default is PUT.
java.http.retry&nbsp;=&nbsp;_number_ | Set the number of times the request should be retried. The default is 0, meaning do not retry.
java.http.retrydelay&nbsp;=&nbsp;_milliseconds_ | Set the time in milliseconds to wait after each HTTP failure before retrying. The default is 500ms.
java.http.uri&nbsp;=&nbsp;_uri_ | **Required**. Set the scheme, hostname, and optionally user name, password, port, path and query string to be used to connect to the HTTP server. The URI format is described by [RFC3986](http://tools.ietf.org/html/rfc3986).

## Examples
* Send the text 'Hello' in the HTTP user body.
 ```properties
http.content=Hello
 ```
* Send a JSON document in the HTTP user body and set the MIME type to application/json.
 ```properties
http.content={json}
 ```
* Send a Common Base Event Rest document in the HTTP user body and set the MIME type to text/xml.
 ```properties
http.content={commonbaseeventrest}
 ```
* Use an HTTP filter class to monitor or change HTTP requests or responses.
 ```properties
java.http.filterclass=com.mycompany.filters.MyFilter
 ```
* Set the HTTP header access-token with the value 1234.
 ```properties
java.http.header.access-token=1234
 ```
* Use POST to create a new resource.
 ```properties
http.method=POST
 ```
* Use PUT to add content to an existing resource.
 ```properties
http.method=PUT
 ```
* Retry the request once.
 ```properties
http.retry=1
 ```
* Retry the request immediately.
 ```properties
http.retrydelay=0
 ```
* Send the request to the HTTP server at a URI endpoint.
 ```properties
http.uri=http://myserver.example.com/resource
 ```
* Send the request to the HTTP server at a URI endpoint with the basic authentication userid and password.
 ```properties
http.uri=http://username:password@server.example.com:8080/path?name=fred
 ```
