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
    formal-docbook.xsl
    Creation date: Dec 15, 2006.
    By: Neil Corkum
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" version="1.0">
    
  <xsl:import href="common-docbook.xsl"/>

  <!-- Prepend "Chapter xx: " to chapter headings. -->
  <xsl:param name="chapter.autolabel" select="1"></xsl:param>

  <!-- Prepend #. to section titles. -->
  <xsl:param name="section.autolabel" select="1"></xsl:param>

  <!-- Only label sections to this depth -->
  <xsl:param name="section.autolabel.max.depth" select="5"></xsl:param>

  <!-- Override fo/autotoc.xsl to add "Chapter", "Appendix" in front of number in TOC. -->
  <xsl:template name="toc.line">
    <xsl:variable name="id">
      <xsl:call-template name="object.id"/>
    </xsl:variable>
  
    <xsl:variable name="label">
      <xsl:apply-templates select="." mode="label.markup"/>
    </xsl:variable>
  
    <fo:block xsl:use-attribute-sets="toc.line.properties"
              end-indent="{$toc.indent.width}pt"
              last-line-end-indent="-{$toc.indent.width}pt">
      <fo:inline keep-with-next.within-line="always">
        <fo:basic-link internal-destination="{$id}">
<!-- Start change. -->
          <xsl:choose>
            <xsl:when test="local-name(.) = 'part'">
              <xsl:call-template name="gentext">
                <xsl:with-param name="key" select="'Part'"/>
              </xsl:call-template>
              <xsl:text> </xsl:text>
            </xsl:when>
            <xsl:when test="local-name(.) = 'chapter'">
              <xsl:call-template name="gentext">
                <xsl:with-param name="key" select="'Chapter'"/>
              </xsl:call-template>
              <xsl:text> </xsl:text>
            </xsl:when>
            <xsl:when test="local-name(.) = 'appendix'">
              <xsl:call-template name="gentext">
                <xsl:with-param name="key" select="'Appendix'"/>
              </xsl:call-template>
              <xsl:text> </xsl:text>
            </xsl:when>
          </xsl:choose>
<!-- End change. -->
          <xsl:if test="$label != ''">
            <xsl:copy-of select="$label"/>
            <xsl:value-of select="$autotoc.label.separator"/>
          </xsl:if>
          <xsl:apply-templates select="." mode="titleabbrev.markup"/>
        </fo:basic-link>
      </fo:inline>
      <fo:inline keep-together.within-line="always">
        <xsl:text> </xsl:text>
        <fo:leader leader-pattern="dots"
                   leader-pattern-width="3pt"
                   leader-alignment="reference-area"
                   keep-with-next.within-line="always"/>
        <xsl:text> </xsl:text> 
        <fo:basic-link internal-destination="{$id}">
          <fo:page-number-citation ref-id="{$id}"/>
        </fo:basic-link>
      </fo:inline>
    </fo:block>
  </xsl:template>


</xsl:stylesheet>
