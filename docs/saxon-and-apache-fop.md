# Properties to convert documents using Saxson or Apache FOP
Use these properties to specify the Java class to use for XSLT transformations, and to specify additional configuration for the Apache FOP.

Property | Usage
--- | ---
fop.config.file&nbsp;=&nbsp;_file_ | Apache FOP configuration file as defined at http://xmlgraphics.apache.org/fop/1.1/configuration.html
javax.xml.transform.TransformerFactory&nbsp;=&nbsp;_class_ | Specify the Java class name to use to transform XSLT.

## Examples
* Use a configuration file to configure Apache FOP.
 ```properties
fop.config.file=/path/config.xml
 ```
* Use the Apache FOP transformer for XSLT processing.
 ```properties
 javax.xml.transform.TransformerFactory=org.apache.xalan.processor.TransformerFactoryImpl`
  ```
* Use the Saxon transformer for XSLT processing.
 ```properties
 javax.xml.transform.TransformerFactory=net.sf.saxon.TransformerFactoryImpl
  ```