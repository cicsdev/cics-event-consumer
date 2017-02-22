# Logging
The application uses the Java logging framework [java.util.logging](https://docs.oracle.com/javase/8/docs/technotes/guides/logging/overview.html#a1.0) to write INFO and FINE messages. These can be directed to the JVM server standard error (STDERR) file as follows: 

1. Disable the JVMSERVER resource.
* Create a logging.properties file on zFS to specify the logging parameters. The file should be saved in the JVM file encoding specified by the `-Dfile.encoding` parameter in the JVM server profile, typically ISO-8859-1. For example create /usr/lpp/ca1y/examples/logging.properties containing the following:
 ```properties
# Logging levels
java.util.logging.ConsoleHandler.level=ALL
com.ibm.cics.ca1y.level=ALL

# Logging handlers
com.ibm.cics.ca1y.handlers=java.util.logging.ConsoleHandler
com.ibm.cics.ca1y.useParentHandlers=false
java.util.logging.ConsoleHandler.formatter=java.util.logging.SimpleFormatter
java.util.logging.SimpleFormatter.format=%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$s %2$s - %5$s %6$s%n
 ```
* Add the following to the Liberty JVM server profile:
 ```properties
-Djava.util.logging.config.file=/usr/lpp/ca1y/examples/logging.properties
 ```
* Enable the JVMSERVER resource.

