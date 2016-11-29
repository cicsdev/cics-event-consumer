       Process cics('cobol3,sp')                                        
       Process arith(extend) trunc(bin) list map xref rent              
      ***************************************************************** 
      * Licensed Materials - Property of IBM                            
      *                                                                 
      * CICS SupportPac CA1Y - CICS ® TS support for sending emails     
      *                                                                 
      * © Copyright IBM Corporation 2012 All Rights Reserved.           
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
      * that uses tokens to load the email headers, body, an 
      * attachment, and mail server properties from CICS containers.                                              
      *                                                                 
      ***************************************************************** 
       Identification Division.                                         
       Program-id. CA1YCOB3.                                            
       Environment division.                                            
       Data division.                                                   
      ******************************************************************
       Working-storage section.                                         
       01 CONFIG.                                                       
           02 CONFIG-CHANNEL-NAME   PIC X(16)  VALUE 'CA1Y            '. 
           02 CONFIG-CONTAINER-NAME PIC X(16)  VALUE 'CA1Y            '. 
           02 TO-CONTAINER-NAME     PIC X(16)  VALUE 'TO              '. 
           02 SUBJECT-CONTAINER-NAME PIC X(16) VALUE 'SUBJECT         '.
           02 CONTENT-CONTAINER-NAME PIC X(16) VALUE 'CONTENT         '.
           02 ATTACH1-CONTAINER-NAME PIC X(16) VALUE 'ATTACH1         '.
       01 WORKAREA.                                                     
           02 WORKAREA-DATA-LENGTH  PIC 9(8) COMP VALUE 0.              
           02 WORKAREA-DATA         PIC X(1024) VALUE SPACES.           
       01 CR                        PIC X(1)   VALUE X'25'.              
      ******************************************************************
       Linkage section.                                                 
       Procedure division.                                              
       Main-program section.                                            
      * --------------------------------------------------------------  
      * Create container for mail configuration                         
      * --------------------------------------------------------------  
           STRING                                                       
               'import.private={file=/usr/lpp/ca1y/examples/'           
               'emailServer.properties:encoding=UTF-8}' CR              
               'mail.to={' TO-CONTAINER-NAME '}' CR                     
               'mail.subject={' SUBJECT-CONTAINER-NAME '}' CR           
               'mail.content={' CONTENT-CONTAINER-NAME '}' CR           
               'attachment={mime=application/octet-stream}'
               '{' ATTACH1-CONTAINER-NAME '}' CR
               X'00'                                                    
               DELIMITED BY SIZE INTO WORKAREA-DATA.                    
                                                                        
           MOVE 0 TO WORKAREA-DATA-LENGTH.                              
           INSPECT WORKAREA-DATA TALLYING WORKAREA-DATA-LENGTH          
               FOR CHARACTERS BEFORE INITIAL X'00'.                     
                                                                        
           EXEC CICS PUT CONTAINER(CONFIG-CONTAINER-NAME)               
               CHANNEL(CONFIG-CHANNEL-NAME)                             
               FROM(WORKAREA-DATA) FLENGTH(WORKAREA-DATA-LENGTH) CHAR   
           END-EXEC.                                                    
      * --------------------------------------------------------------  
      * Create container for mail recipient                             
      * --------------------------------------------------------------  
           STRING '"Joe Bloggs" <joe.bloggs@example.com>' X'00'         
               DELIMITED BY SIZE INTO WORKAREA-DATA.                    
                                                                        
           MOVE 0 TO WORKAREA-DATA-LENGTH.                              
           INSPECT WORKAREA-DATA TALLYING WORKAREA-DATA-LENGTH          
               FOR CHARACTERS BEFORE INITIAL X'00'.                     
                                                                        
           EXEC CICS PUT CONTAINER(TO-CONTAINER-NAME)                   
               CHANNEL(CONFIG-CHANNEL-NAME)                             
               FROM(WORKAREA-DATA) FLENGTH(WORKAREA-DATA-LENGTH) CHAR   
           END-EXEC.                                                    
      * --------------------------------------------------------------  
      * Create container for subject                                    
      * --------------------------------------------------------------  
           STRING 'Email from {REGION_APPLID}' X'00'                    
               DELIMITED BY SIZE INTO WORKAREA-DATA.                    
                                                                        
           MOVE 0 TO WORKAREA-DATA-LENGTH.                              
           INSPECT WORKAREA-DATA TALLYING WORKAREA-DATA-LENGTH          
               FOR CHARACTERS BEFORE INITIAL X'00'.                     
                                                                        
           EXEC CICS PUT CONTAINER(SUBJECT-CONTAINER-NAME)              
               CHANNEL(CONFIG-CHANNEL-NAME)                             
               FROM(WORKAREA-DATA) FLENGTH(WORKAREA-DATA-LENGTH) CHAR   
           END-EXEC.                                                    
      * --------------------------------------------------------------  
      * Create container for content                                    
      * --------------------------------------------------------------  
           STRING 'This email was sent '                                
               'on {datetime=EEE, d MMM yyyy HH:mm:ss Z} '              
               'from transaction id {TASK_TRANID}, '                    
               'user id {TASK_USERID}, '                                
               'program {TASK_PROGRAM}, '                               
               'task number {TASK_NUMBER}, '                            
               'CICS SYSID {REGION_SYSID}, '                            
               'CICS APPLID {REGION_APPLID}.'                           
               X'00'                                                    
               DELIMITED BY SIZE INTO WORKAREA-DATA.                    
                                                                        
           MOVE 0 TO WORKAREA-DATA-LENGTH.                              
           INSPECT WORKAREA-DATA TALLYING WORKAREA-DATA-LENGTH          
               FOR CHARACTERS BEFORE INITIAL X'00'.                     
                                                                        
           EXEC CICS PUT CONTAINER(CONTENT-CONTAINER-NAME)              
               CHANNEL(CONFIG-CHANNEL-NAME)                             
               FROM(WORKAREA-DATA) FLENGTH(WORKAREA-DATA-LENGTH) CHAR   
           END-EXEC.                                                    
      * --------------------------------------------------------------  
      * Create container for attachment                                 
      * --------------------------------------------------------------  
           STRING X'0102030405060708090A0B0C0D0E0F'                     
               X'00'                                                    
               DELIMITED BY SIZE INTO WORKAREA-DATA.                    
                                                                        
           MOVE 0 TO WORKAREA-DATA-LENGTH.                              
           INSPECT WORKAREA-DATA TALLYING WORKAREA-DATA-LENGTH          
               FOR CHARACTERS BEFORE INITIAL X'00'.                     
                                                                        
           EXEC CICS PUT CONTAINER(ATTACH1-CONTAINER-NAME)              
               CHANNEL(CONFIG-CHANNEL-NAME)                             
               FROM(WORKAREA-DATA) FLENGTH(WORKAREA-DATA-LENGTH) BIT    
           END-EXEC.                                                    
      * --------------------------------------------------------------  
      * Emit the mail message                                           
      * --------------------------------------------------------------  
           EXEC CICS LINK PROGRAM('CA1Y')                               
               CHANNEL(CONFIG-CHANNEL-NAME)                             
           END-EXEC.                                                    
      *                                                                 
           EXEC CICS RETURN END-EXEC.                                   
      *                                                                 
       Main-program-exit.                                               
           exit.                                                        