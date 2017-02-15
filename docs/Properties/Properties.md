The SupportPac acts upon information passed to it as properties.
Place the properties in one of the following locations:
* If the SupportPac will be started as a CICS event custom adapter, place the properties in the Data passed to the Custom Adapter field in the adapter tab in the CICS Explorer event adapter editor or event binding editor.
* If the SupportPac will be started as a result of a CICS policy being triggered, place the properties in the Data passed to the Custom Adapter field in the adapter tab in the CICS Explorer event adapter editor.
* If the SupportPac will be started by a LINK CHANNEL() or START CHANNEL() command, place the properties in a character container named CA1Y. Ensure the container is created with the CCSID of your program by specifying FROMCCSID or FROMCODEPAGE parameters on the PUT CONTAINER command.
* If the SupportPac will be started by a LINK COMMAREA() or START FROM() command, place the properties in the commarea. The content is required to be in the CICS region local CCSID in which CA1Y executes.
Properties are specified using the format name=value and are case sensitive. Each property should end with a new line or line feed character.
To split a value over several lines, use the \ continuation character at the end of the line. To insert a new line character in a property value use \n. Blank lines, and lines where the first non-blank character is # or ! are used for comments and are ignored. Further formatting rules and an alternative XML notation are described in the Java Properties class load method at java.util.Properties load. 
If a property is specified more than once, the last instance will take effect.
It is advisable to avoid property names that clash with the names of tokens. 
Property values can contain tokens that are replaced as described in Tokens. Properties are processed in alphanumeric name order to resolve the tokens.
Additional properties can be imported from an external source such as a file that can be maintained independent to the event adapter configuration or program.
A combination of properties could be used to, for example, send an email and write to a temporary data queue. However, when using CICS events you may prefer to define these in separate event adapter configurations to maintain their independence in failure scenarios. Also from CICS TS V5.1 you can emit one captured event to multiple adapters using adapter sets.
