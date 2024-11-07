<?xml version="1.0" encoding="UTF-8" ?>
<!-- <copyright                                                  -->
<!-- 	Licensed Materials - Property of IBM                     -->
<!-- 	cics-event-consumer                                      -->
<!-- 	(c) Copyright IBM Corp. 2012 - 2024 All Rights Reserved  -->
<!-- 	US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp. -->
<!-- </copyright> -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
<xsl:output method="xml" indent="yes" />
<xsl:template match="/">
<fo:root>
<fo:layout-master-set>
<fo:simple-page-master master-name="A4-portrait" page-height="29.7cm" page-width="21.0cm" margin="2cm">
<fo:region-body />
</fo:simple-page-master>
</fo:layout-master-set>
<fo:page-sequence master-reference="A4-portrait">
<fo:flow flow-name="xsl-region-body">
<fo:block>
Hello world! This PDF was generated for
<xsl:value-of select="name" />
!
</fo:block>
</fo:flow>
</fo:page-sequence>
</fo:root>
</xsl:template>
</xsl:stylesheet> 
