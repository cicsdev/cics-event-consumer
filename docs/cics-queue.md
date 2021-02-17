# Properties to write a message to a CICS queue
Use these properties to write content to a CICS temporary storage (TS) or temporary data (TD) queue.

Property | Usage
--- | ---
queue&nbsp;=&nbsp;_queueName_ | **Required**. The queue name. Follow the queue naming rules defined by CICS. For TS queues see [QNAME(name)](https://www.ibm.com/support/knowledgecenter/SSGMCP_5.6.0/reference-applications/commands-api/dfhp4_writeqts.html). For TD queues see [QUEUE(name)](https://www.ibm.com/support/knowledgecenter/SSGMCP_5.6.0/reference-applications/commands-api/dfhp4_writeqtd.html).
queue.content&nbsp;=&nbsp;_value_ | **Required**. Text content to write to the queue.
queue.content.encoding&nbsp;=&nbsp;_value_ | Content is written to the queue using the specified encoding. If not specified the CICS region local CCSID will be used.
queue.content.length.chunk&nbsp;=&nbsp;_maxLength_ | The maximum length of content to write in a single queue record. If the content is beyond this length, multiple queue records will be written. In this situation it is possible other CICS tasks may write records to the same queue and become interspersed with records written by this task. If this property is not specified, the content will be written as a single record and truncation may occur.
queue.content.length.max&nbsp;=&nbsp;_maxLength_ | The maximum length of content to write to the queue. Content over this length is truncated.
queue.sysid&nbsp;=&nbsp;_sysID_ | The remote system identifier (SYSID) where the queue resides. If this property is not specified, the queue is assumed to be local to the CICS region that is executing the SupportPac.
queue.ts.storage&nbsp;=&nbsp;_type_ | Specify the queue is to be created in main or auxiliary storage if it does not already exist. If not specified, main is assumed.
queue.type&nbsp;=&nbsp;_type_ | Specify the queue type. If not specified, temporary storage is assumed.

## Examples
* Use the queue name MyQueue.
  ```properties
 queue=MyQueue
  ```
* Write the contents 'Hello from CICS'.
 ```properties
 queue.content=Hello from CICS.
  ```
* Write the text content read from the named dataset member.
 ```properties
queue.content={file=//DD:MYDATAS(MEMBER)}
 ```
* Write the binary content read from the named zFS file to the queue.
 ```properties
queue.content={file=/path/picture.png:binary}
 ```
* Write to a temporary storage queue.
 ```properties
queue.type=ts
 ```
* Write to a temporary data queue.
 ```properties
queue.type=td
 ```
* Write the queue content using a specific encoding.
 ```properties
queue.content.encoding=Cp1047
 ```
* Write the content to the queue in chunks of 32763 bytes each.
 ```properties
queue.content.length.chunk=32763
 ```
* Write the content to the queue up to a maximum of 32763 bytes.
 ```properties
queue.content.length.max=32763
 ```
* Write to the queue using the system idenifier (SYSID) SYS1.
 ```properties
queue.sysid=SYS1
 ```
* Write to the queue using main storage.
 ```properties
queue.ts.storage=main
 ```
* Write to the queue using auxiliary storage.
 ```properties
queue.ts.storage=auxiliary
 ```
