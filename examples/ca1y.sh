# -----------------------------------------------------------------------------------------------
# Start CICS SupportPac CA1Y to send an email.
#
# The script will produce messages to standard out and complete with return code 0 if successful.  
#
# Syntax:
#   ca1y.sh "property1=value1" ... "propertyN=valueN" < propertyPipe
#
# Example 1. Send an email using properties from a file
#   ca1y.sh < ./input.properties
#
# Example 2. Send an email using properties from a file and overriding them with properties on
# the command line.
# 	ca1y.sh "mail.to=user@example.com" "mail.subject=Email from system {systemsymbol=&SYSNAME.}
#   sysplex {systemsymbol=&SYSPLEX.}" "mail.content=Here is your email." < ./input.properties
#
# CA1Y will merge the properties obtained from the following methods, in lowest to highest order
# of precedence:
#   - from a pipe
#   - from the command line
#   - from script variable PROPERTIES
#
# Set JAVA_HOME_DEFAULT to the Java home directory, for example:
#   JAVA_HOME_DEFAULT="/java/java71_64/J7.1_64"
#
# Set CICS_HOME to the CICS home directory, for example:
#   CICS_HOME="/cics/cics690"
#
# Set PROPERTIES to the properties that override those provided on the command line, for example
# the email server settings:
#   PROPERTIES="$PROPERTIES import.private={file=/usr/lpp/ca1y/examples/emailServer.properties:encoding=UTF8}"
#
# Set JAVA_OPTIONS to required JVM options, for example to quick start the JVM 
#   JAVA_OPTIONS="$JAVA_OPTIONS -Xquickstart"
#   JAVA_OPTIONS="$JAVA_OPTIONS -Djava.util.logging.config.file=/usr/lpp/ca1y/examples/logging.properties"
#   JAVA_OPTIONS="$JAVA_OPTIONS -Xrunjdwp:transport=dt_socket,server=y,address=27508,suspend=y"
# -----------------------------------------------------------------------------------------------
JAVA_HOME_DEFAULT="/java/java71_64/J7.1_64"
CICS_HOME="/cics/cics690"
CA1Y_HOME="/usr/lpp/ca1y"
PROPERTIES="$PROPERTIES import.private={file="$CA1Y_HOME"/ca1y/examples/emailServer.properties:encoding=UTF8}"
JAVA_OPTIONS="$JAVA_OPTIONS -Xquickstart"

# Set Java environment if not already defined
if [ -n "${JAVA_HOME}" ]; then
else
	export JAVA_HOME="${JAVA_HOME_DEFAULT}"

	export PATH=/bin:"${JAVA_HOME}"/bin

	LIBPATH=/lib:/usr/lib:"${JAVA_HOME}"/bin
	LIBPATH="$LIBPATH":"${JAVA_HOME}"/lib/s390x
	LIBPATH="$LIBPATH":"${JAVA_HOME}"/lib/s390x/j9vm
	LIBPATH="$LIBPATH":"${JAVA_HOME}"/bin/classic
	export LIBPATH="$LIBPATH":

	CLASSPATH="${JAVA_HOME}"/lib:"${JAVA_HOME}"/lib/ext
fi

CLASSPATH="$CLASSPATH":"$CICS_HOME"/lib/com.ibm.cics.server.jar
CLASSPATH="$CLASSPATH":"$CICS_HOME"/lib/pipeline/mail.jar
CLASSPATH="$CLASSPATH":"$CA1Y_HOME"/*
export CLASSPATH="$CLASSPATH":

# Start CA1Y 
java $JAVA_OPTIONS com.ibm.cics.ca1y.Emit "$@" $PROPERTIES
exit $?
