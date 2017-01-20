# Property to submit a batch job

Use this property to submit content as a Job Control Language \(JCL\) batch job to the MVS internal reader facility using the JZOS [MvsJobSubmitter](http://www.ibm.com/developerworks/java/zos/javadoc/jzos/SDK7/com/ibm/jzos/MvsJobSubmitter.html#write(java.lang.String)\) class.

Use the escape sequence \n to insert a new line, and the backslash character \ to continue onto the next line. The maximum length of each line in the batch job is 80 characters once all tokens have been resolve.

The SupportPac does not wait for the batch job to start executing. Use MVS Job Entry Subsystem \(JES\) facilities to check progress, successful execution, and output of the batch job.

If the SupportPac is started by a LINK command, the submitted job ID is returned in the container named mvsjob.jobid.

| Property | Usage |
| --- | --- |
| mvsjob.content = _value_ | **Required.** The contents of the property are submitted to MVS as a batch job. |

## Examples

* A modify command is issued against the CICS region to set a file enabled. The EPCX\_APPLID and File\_name tokens are replaced with the captured items from file system event.

  ```
  mvsjob.content=//MODIFY JOB CLASS=M,MSGCLASS=H \n\
  //IEFBR EXEC PGM=IEFBR14 \n\
  // F {EPCX_APPLID},'CEMT SET FILE({File_name}) ENABLE'
  ```

* The content of the batch job is retrieved from a dataset member, with the property token _command_ replaced with the contents of the property of the same name.

  ```
  command=CEMT SET FILE({File_name}) ENABLE
  mvsjob.content={file=//'MYJOBS.JCL(SKELETON)'}
  ```

 The contents of MVS file MYJOBS.JCL\(SKELETON\):

  ```
  //MODIFY JOB CLASS=M,MSGCLASS=H 
  //IEFBR EXEC PGM=IEFBR14 
  // F {EPCX_APPLID},'{command}'
  ```



