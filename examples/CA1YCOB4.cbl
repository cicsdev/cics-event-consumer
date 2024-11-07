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
      * This example illustrates how to convert an XML document to a
      * PDF document using an XSLT stylesheet.
      *
      * The container named "CA1Y" is created with properties:
      * 1. MyXSLT that loads the XSLT document from zFS.
      * 2. MyPDF that puts the converted PDF document into container
      *    "PDF". The PDF documented is created by converting from
      *    MIME type text/xml to application/pdf using the XSLT in
      *    property “MyXSLT”.
      * 
      * The SupportPac program CA1Y is then called to create the PDF
      * document. The PDF document is then retrieved from the "PDF"
      * container.
      *****************************************************************
       Identification Division.
       Program-id. CA1YCOB4.
       Environment division.
       Data division.
      *****************************************************************
       Working-storage section.
       01 CONFIG.
           02 CONFIG-CHANNEL-NAME   PIC X(16) VALUE 'MyChannel       '.
           02 CONFIG-CONT-NAME      PIC X(16) VALUE 'CA1Y            '.
           02 CONFIG-DATA-LENGTH    PIC 9(8) COMP VALUE 0.
           02 CONFIG-DATA           PIC X(2048) VALUE SPACES.
       01 PDF-DOC.
           02 PDF-CONT-NAME         PIC X(16) VALUE 'PDF             '.
           02 PDF-DATA-LENGTH       PIC 9(8) COMP VALUE 0.
           02 PDF-DATA              PIC X(10240) VALUE SPACES.
       01 RESPONSE.
           02 RESPONSE-CONT-NAME    PIC X(16) VALUE 'CA1YRESPONSE    '.
           02 RESPONSE-DATA-LENGTH  PIC 9(8) COMP VALUE 0.
           02 RESPONSE-DATA         PIC X(16) VALUE SPACES.
       01 CR                        PIC X(1)  VALUE X'25'.
      *****************************************************************
       Linkage section.
       Procedure division.
       Main-program section.
      * --------------------------------------------------------------
      * Create a container with properties used by the SupportPac.
      * --------------------------------------------------------------
           STRING
            'javax.xml.transform.TransformerFactory='
            'org.apache.xalan.processor.TransformerFactoryImpl' CR
            'MyXSLT={file=/u/ca1y/examples/helloWorld.xslt'
            ':encoding=UTF-8}' CR
      *
            'MyPDF={responsecontainer=' PDF-CONT-NAME '}'
            '{mime=text/xml:to=application/pdf:xslt=MyXSLT}'
            '<?xml version="1.0" encoding="UTF-8" ?>'
            '<name>Joe Bloggs</name>' CR
      *
            X'00'
            DELIMITED BY SIZE INTO CONFIG-DATA.

           INSPECT CONFIG-DATA TALLYING CONFIG-DATA-LENGTH
               FOR CHARACTERS BEFORE INITIAL X'00'.

           EXEC CICS PUT CONTAINER(CONFIG-CONT-NAME)
               CHANNEL(CONFIG-CHANNEL-NAME)
               FROM(CONFIG-DATA) FLENGTH(CONFIG-DATA-LENGTH) CHAR
           END-EXEC.
      * --------------------------------------------------------------
      * Link to the SupportPac.
      * --------------------------------------------------------------
           EXEC CICS LINK PROGRAM('CA1Y')
               CHANNEL(CONFIG-CHANNEL-NAME)
           END-EXEC.
      * --------------------------------------------------------------
      * Get the SupportPac response.
      * --------------------------------------------------------------
           COMPUTE RESPONSE-DATA-LENGTH = LENGTH OF RESPONSE-DATA.
           EXEC CICS GET CONTAINER(RESPONSE-CONT-NAME)
               CHANNEL(CONFIG-CHANNEL-NAME)
               INTO(RESPONSE-DATA) FLENGTH(RESPONSE-DATA-LENGTH)
           END-EXEC.
      * --------------------------------------------------------------
      * Get the PDF.
      * --------------------------------------------------------------
           COMPUTE PDF-DATA-LENGTH = LENGTH OF PDF-DATA.
           EXEC CICS GET CONTAINER(PDF-CONT-NAME)
               CHANNEL(CONFIG-CHANNEL-NAME)
               INTO(PDF-DATA) FLENGTH(PDF-DATA-LENGTH)
           END-EXEC.

           EXEC CICS RETURN END-EXEC.
      * ---------------------------------------------------------------
       Main-program-exit.
           exit. 
