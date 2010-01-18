<?xml version="1.0" encoding="UTF-8"?>
<!--
Copyright (c) 2006 BUSINESS OBJECTS SOFTWARE LIMITED
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
    informal-docbook.xsl
    Creation date: Dec 15, 2006.
    By: Neil Corkum
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" version="1.0">
    
  <xsl:import href="common-docbook.xsl"/>

  <!-- Don't prepend "Chapter xx: " to chapter headings. -->
  <xsl:param name="chapter.autolabel" select="0"></xsl:param>
  
  <!-- Prepend #. to section titles. -->
  <xsl:param name="section.autolabel" select="1"></xsl:param>
  
  <!-- Only label sections to this depth -->
  <xsl:param name="section.autolabel.max.depth" select="1"></xsl:param>

  <!-- Don't indent the body of an article. -->
  <xsl:param name="body.start.indent" select="0"></xsl:param>
  
  <!-- No blank page between titlepage and rest of document. -->
  <xsl:template name="book.titlepage.separator"/>
  
  <!-- No table of contents. -->
  <xsl:param name="generate.toc">
    book nop
    article nop
  </xsl:param>

  <!-- Format variable lists as blocks, indented a bit. -->
  <xsl:param name="variablelist.as.blocks" select="1"></xsl:param>
  <xsl:attribute-set name="list.block.spacing">
    <xsl:attribute name="margin-left">
      <xsl:choose>
        <xsl:when test="self::variablelist">0.25in</xsl:when>
        <xsl:otherwise>0pt</xsl:otherwise>
      </xsl:choose>
    </xsl:attribute>
  </xsl:attribute-set>

  <!-- 
    Override target in fo/titlepage.xsl
    Do not generate the line for editors.
  -->
  <xsl:template match="editor[1]" priority="2" mode="titlepage.mode">
  </xsl:template>
  
  <!-- If we find a section which has a legal notice, insert a page break before the notice. -->
  <xsl:template match="section">
  
  	<xsl:variable name="foundLegal">
      <xsl:value-of select=".//legalnotice"/>
    </xsl:variable>
  	
  	<!-- Actually insert a block which specifies a page break comes after. -->
  	<xsl:if test="string-length($foundLegal) &gt; 0">
      <fo:block break-after="page"/>
    </xsl:if>
    
    <!-- Now do the rest of the section stuff. -->
    <xsl:apply-imports/>
    
  </xsl:template>

  <!-- 
    Override target in common/labels.xsl
    Don't apply the numeric label to the legal notice.
    The legal notice will appear at the end of the document, so numbers won't be skipped..
  -->
  <xsl:template name="label.this.section">
    <xsl:param name="section" select="."/>
  
    <xsl:variable name="level">
      <xsl:call-template name="section.level"/>
    </xsl:variable>
  
    <!-- New variable. -->
  	<xsl:variable name="foundLegal">
      <xsl:value-of select=".//legalnotice"/>
    </xsl:variable>
    
    <xsl:choose>
      <xsl:when test="string-length($foundLegal) &gt; 0">0</xsl:when>  <!-- New when. -->
      
      <xsl:when test="$level &lt;= $section.autolabel.max.depth">      
        <xsl:value-of select="$section.autolabel"/>
      </xsl:when>
      <xsl:otherwise>0</xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <!-- Component titles (chapter, article, preface) - shrink font a bit. -->
  <xsl:attribute-set name="component.title.properties">
    <xsl:attribute name="font-size">
      <xsl:value-of select="$body.font.master * 1.8"/>
      <xsl:text>pt</xsl:text>
    </xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="section.title.level1.properties">
    <xsl:attribute name="font-size">
      <xsl:value-of select="$body.font.master * 1.4"/>
      <xsl:text>pt</xsl:text>
    </xsl:attribute>
  </xsl:attribute-set>
  
  <xsl:attribute-set name="section.title.level2.properties">
    <xsl:attribute name="font-size">
      <xsl:value-of select="$body.font.master * 1.3"/>
      <xsl:text>pt</xsl:text>
    </xsl:attribute>
  </xsl:attribute-set>
  
  <xsl:attribute-set name="section.title.level3.properties">
    <xsl:attribute name="font-size">
      <xsl:value-of select="$body.font.master * 1.2"/>
      <xsl:text>pt</xsl:text>
    </xsl:attribute>
  </xsl:attribute-set>
  
  <xsl:attribute-set name="section.title.level4.properties">
    <xsl:attribute name="font-size">
      <xsl:value-of select="$body.font.master * 1.1"/>
      <xsl:text>pt</xsl:text>
    </xsl:attribute>
  </xsl:attribute-set>
  
  <!-- No header rule. -->
  <xsl:param name="header.rule" select="0"/>

  <!-- No header text. -->
  <xsl:template name="header.content">
  </xsl:template>
  
</xsl:stylesheet>
