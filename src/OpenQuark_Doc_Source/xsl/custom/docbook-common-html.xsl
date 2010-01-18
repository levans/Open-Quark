<?xml version="1.0" encoding="UTF-8"?>
<!--
Copyright (c) 2007 BUSINESS OBJECTS SOFTWARE LIMITED
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.
 
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
 
    * Neither the name of Business Objects nor the names of its contributors
      may be used to endorse or promote products derived from this software
      without specific prior written permission.
 
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.
-->
<!--
    docbook-common-html.xsl
    Creation date: Feb 26, 2007.
    By: Edward Lam
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" version="1.0">
    
  <xsl:import href="docbook-common.xsl"/>

  <!-- 
    chunking options 
    -->
  <xsl:param name="chunk.first.sections" select="1"></xsl:param>
  <xsl:param name="chunk.section.depth" select="3"></xsl:param>
  <xsl:param name="use.id.as.filename" select="'1'"></xsl:param>
  
  <!-- Fast chunking - supported by Xalan and Saxon (but not MSXSL..). -->
  <xsl:param name="chunk.fast" select="1"></xsl:param>
  
  <xsl:param name="html.extra.head.links" select="1"></xsl:param>
  
  <!-- Even with this, output html still fails validation. -->
  <xsl:param name="make.valid.html" select="1"></xsl:param>
  
  <!-- cal syntax highlighting (elem: <phrase role=".."> )-->
  <!-- The role attribute is copied as the html class attribute. -->
  <!-- 
     eg. in docbook xml:    <phrase role="somerole">
         in CSS stylesheet: .somerole {font-variant: small-caps;}
         
         <xsl:template match="phrase[@role = 'somerole']">
           <fo:inline font-variant="small-caps">
             <xsl:apply-templates/>
           </fo:inline>
         </xsl:template>
    -->
  <xsl:param name="phrase.propagates.style" select="1"></xsl:param>

  <!-- formalpara: insert line break between title and body.
       Overrides template in html\block.xsl -->
  <xsl:template match="formalpara/para">
    <br/>
    <xsl:apply-templates/>
  </xsl:template>

</xsl:stylesheet>
