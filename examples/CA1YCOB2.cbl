       Process cics('cobol3,sp')                                        
       Process arith(extend) trunc(bin) list map xref rent              
      ***************************************************************** 
      * Licensed Materials - Property of IBM                            
      *                                                                 
      * cics-event-consumer      
      *                                                                 
      * (c) Copyright IBM Corp. 2012 - 2024 All Rights Reserved            
      *                                                                 
      *  US Government Users Restricted Rights - Use, duplication,      
      *  or disclosure restricted by GSA ADP Schedule Contract with     
      *  IBM Corporation.                                              
      ***************************************************************** 
      *                                                                 
      * This example program illustrates how to send an email using     
      * the EXEC CICS LINK interface to program CA1Y.                   
      *                                                                 
      * A container named "CA1Y" is created from name=value pairs       
      * that specify the email headers, body, an attachment, and mail   
      * server properties.                                              
      *                                                                 
      * The attachment and mail server properties are examples provided 
      * with the SupportPac.                                            
      *                                                                 
      ***************************************************************** 
       Identification Division.                                         
       Program-id. CA1YCOB2.                                            
       Environment division.                                            
       Data division.                                                   
      ***************************************************************** 
       Working-storage section.                                         
       01 CONFIG.                                                       
           02 CONFIG-CHANNEL-NAME   PIC X(16) VALUE 'MyChannel       '. 
           02 CONFIG-CONTAINER-NAME PIC X(16) VALUE 'CA1Y            '. 
           02 CONFIG-DATA-LENGTH    PIC 9(8) COMP VALUE 0.              
           02 CONFIG-DATA           PIC X(2048) VALUE SPACES.           
       01 CR                        PIC X(1)  VALUE X'25'.              
      ***************************************************************** 
       Linkage section.                                                 
       Procedure division.                                              
       Main-program section.                                            
      * --------------------------------------------------------------  
      * Create a container with the email headers, body, attachment,    
      * and import for the email server properties.                     
      * --------------------------------------------------------------  
           STRING                                                       
               'mail.to="Joe Bloggs" <joe.bloggs@example.com>' CR       
               'mail.subject=Email from {REGION_APPLID}' CR             
               'mail.content=This email was sent '                      
               'on {datetime=EEE, d MMM yyyy HH:mm:ss Z} '              
               'from transaction id {TASK_TRANID}, '                    
               'user id {TASK_USERID}, '                                
               'program {TASK_PROGRAM}, '                               
               'task number {TASK_NUMBER}, '                            
               'CICS SYSID {REGION_SYSID}, '                            
               'CICS APPLID {REGION_APPLID}.' CR                        
               'attachment={file=/u/ca1y/examples/'               
               'picture.png:binary}' CR                                 
               'import.private={file=/u/ca1y/examples/'           
               'emailServer.properties:encoding=UTF-8}' CR              
               X'00'                                                    
               DELIMITED BY SIZE INTO CONFIG-DATA.                      
                                                                        
           INSPECT CONFIG-DATA TALLYING CONFIG-DATA-LENGTH              
               FOR CHARACTERS BEFORE INITIAL X'00'.                     
                                                                        
           EXEC CICS PUT CONTAINER(CONFIG-CONTAINER-NAME)               
               CHANNEL(CONFIG-CHANNEL-NAME)                             
               FROM(CONFIG-DATA) FLENGTH(CONFIG-DATA-LENGTH) CHAR       
           END-EXEC.                                                    
                                                                        
           EXEC CICS LINK PROGRAM('CA1Y')                               
               CHANNEL(CONFIG-CHANNEL-NAME)                             
           END-EXEC.                                                    
                                                                        
           EXEC CICS RETURN END-EXEC.                                   
      * --------------------------------------------------------------- 
       Main-program-exit.                                               
           exit.                                                        
