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
      * the SupportPac CICS event adapter interface.                    
      *                                                                 
      * Two containers are created with information that could be       
      * included in the email, and the event is signaled.               
      *                                                                 
      * The example EmailCA1YCOB1.evbind event binding lets CICS know   
      * to capture the OrderPlaced event and information from these     
      * containers, and to call the CA1Y custom event adapter with      
      * appropriate configuration.                                      
      *                                                                 
      * The CA1Y custom event adapter sets up the email content         
      * and mail server properties and sends the email.                 
      *                                                                 
      ***************************************************************** 
       Identification Division.                                         
       Program-id. CA1YCOB1.                                            
       Environment division.                                            
       Data division.                                                   
      ***************************************************************** 
       Working-storage section.                                         
       01 EVENT      PIC X(32) VALUE 'OrderPlaced                     '.
       01 CHANNEL-INFO.                                                 
           02 EVENT-CHANNEL          PIC X(16) VALUE 'MyChannel       '.
           02 CONTAINER-CUSTOMER     PIC X(16) VALUE 'Customer        '.
           02 CONTAINER-ORDER-PLACED PIC X(16) VALUE 'Order           '.
       01 CUSTOMER.                                                     
           02 CUST-NAME         PIC X(20) VALUE 'Joe Adventurous     '. 
           02 CUST-ADDR1        PIC X(20) VALUE 'Rockclimbing Avenue '. 
           02 CUST-EMAIL        PIC X(20) VALUE 'user@example.com    '. 
       01 ORDER-PLACED.                                                 
           02 ORDER-NUMBER      PIC 9(08) VALUE 12345678.               
           02 ITEM-QUANTITY     PIC 9(03) VALUE 1.                      
           02 ITEM-DESCRIPTION  PIC X(20) VALUE 'Rope                '. 
      ***************************************************************** 
       Linkage section.                                                 
       Procedure division.                                              
       Main-program section.                                            
      * --------------------------------------------------------------  
      * Create the container for customer information.                  
      * --------------------------------------------------------------                                                                          
           EXEC CICS PUT CONTAINER(CONTAINER-CUSTOMER)                  
               CHANNEL(EVENT-CHANNEL)                                   
               FROM(CUSTOMER) CHAR                                      
           END-EXEC.                                                    
      * --------------------------------------------------------------  
      * Create the container for order information.                     
      * --------------------------------------------------------------  
           EXEC CICS PUT CONTAINER(CONTAINER-ORDER-PLACED)              
               CHANNEL(EVENT-CHANNEL)                                   
               FROM(ORDER-PLACED) CHAR                                  
           END-EXEC.                                                    
      * --------------------------------------------------------------  
      * Signal the event has occurred.                                  
      * --------------------------------------------------------------  
           EXEC CICS SIGNAL EVENT(EVENT)                                
               FROMCHANNEL(EVENT-CHANNEL)                               
           END-EXEC.                                                    
                                                                        
           EXEC CICS RETURN END-EXEC.                                   
      * --------------------------------------------------------------- 
       Main-program-exit.                                               
           exit.                                                        