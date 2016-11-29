# cics-event-consumer
Consumes events produced by the CICS TS event processing support

## CAY logging
CA1Y uses java.util.logging facilities to trace the processing of consumed events. Logging can be enabled and the output
directed to the JVM standard error (STDERR) by doing the folowing:

1. Create a logging.properties file on zFS to specify the logging parameters.
For example create /usr/lpp/ca1y/examples/logging.properties with the following content:

# Logging levels
java.util.logging.ConsoleHandler.level=ALL
com.ibm.cics.ca1y.level=ALL

# Logging handlers
com.ibm.cics.ca1y.handlers=java.util.logging.ConsoleHandler
com.ibm.cics.ca1y.useParentHandlers=false
java.util.logging.ConsoleHandler.formatter=java.util.logging.SimpleFormatter
java.util.logging.SimpleFormatter.format=%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$s %2$s - %5$s %6$s%n

2. Add the following line to the JVM server profile:
-Djava.util.logging.config.file=/usr/lpp/ca1y/examples/logging.properties
