# Properties to write an MVS console message
Use these properties to write an MVS console message using the write to operator (WTO) macro via the JZOS [MvsConsole](http://www.ibm.com/developerworks/java/zos/javadoc/jzos/SDK7/com/ibm/jzos/MvsConsole.html#wto(com.ibm.jzos.WtoMessage, java.lang.String)) class.

Property | Usage
--- | ---
mvswto.content&nbsp;=&nbsp;_value_ | **Required**. The contents of the property are submitted to the MVS console.
mvswto.descriptor&nbsp;=&nbsp;_code_ | An integer for the MVS descriptor code. A description and rules governing these codes are documented in the Descriptor codes topic in z/OS MVS System Messages.
mvswto.route&nbsp;=&nbsp;_code_ | Specify an integer for the MVS routing code. See the Routing codes topic in z/OS MVS System Messages for a description and rules governing these codes.

## Examples
* Issue an MVS WTO command with the message Hello.
 ```properties
 mvsjob.content=Hello
  ```
* Use the MVS descriptor code 16.
 ```properties
mvswto.descriptor=16
 ```
 As above, but using the constants that start DESC_ defined in class com.ibm.jzos.WtoConstants rather than an integer.
 ```properties
mvswto.descriptor=DESC_IMPORTANT_INFORMATION_MESSAGES
 ```
* Use the MVS routing code 32.
 ```properties
mvswto.route=32
 ```
 As above, but using a constant defined in class WtoConstants rather than an integer.
 ```properties
mvswto.route=ROUTCDE_PROGRAMMER_INFORMATION
 ```
