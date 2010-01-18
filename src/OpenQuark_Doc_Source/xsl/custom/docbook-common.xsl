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
    docbook-common.xsl
    Creation date: Feb 23, 2007.
    By: Edward Lam
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" version="1.0">
    
  <xsl:param name="glossterm.auto.link" select="1"></xsl:param>
  <xsl:param name="firstterm.only.link" select="1"></xsl:param>

  <!-- Allows stylesheets to use Xalan/Saxon Java functions to calculate an image's intrinsic size. -->
  <xsl:param name="use.extensions" select="1"></xsl:param>
  <xsl:param name="graphicsize.extension" select="1"></xsl:param> 

<!--  <xsl:param name="fop.extensions" select="1"></xsl:param> --> <!-- fop 0.20.xx or earlier -->
  <xsl:param name="fop1.extensions" select="1"></xsl:param>   <!-- fop 0.9x.xx or later -->

  <!-- <xsl:param name="menuchoice.separator" select="'-&gt;'"></xsl:param>
  <xsl:param name="menuchoice.menu.separator" select="'-&gt;'"></xsl:param> -->

  <!-- Inline references to figures etc. display without title. 
       Note that this affects all cross-references.  Hack title back in for chapters, etc. below. -->
  <xsl:param name="xref.with.number.and.title" select="0"/>

  <!-- But display references to chapters, sections, etc. with a title. -->
  <xsl:param name="local.l10n.xml" select="document('')"/> 
  <l:i18n xmlns:l="http://docbook.sourceforge.net/xmlns/l10n/1.0"> 
    <l:l10n language="en"> 
      <l:context name="xref-number"> 
        <l:template name="chapter" text="Chapter&#160;%n, %t"/>
        <l:template name="part" text="Part&#160;%n, &#8220;%t&#8221;"/>
        <l:template name="sect1" text="Section&#160;%n, &#8220;%t&#8221;"/>
        <l:template name="sect2" text="Section&#160;%n, &#8220;%t&#8221;"/>
        <l:template name="sect3" text="Section&#160;%n, &#8220;%t&#8221;"/>
        <l:template name="sect4" text="Section&#160;%n, &#8220;%t&#8221;"/>
        <l:template name="sect5" text="Section&#160;%n, &#8220;%t&#8221;"/>
        <l:template name="section" text="Section&#160;%n, &#8220;%t&#8221;"/>
      </l:context>    
    </l:l10n>
  </l:i18n>

  <!-- Figure titles appear below the figure -->
  <xsl:param name="formal.title.placement">
    figure after
  </xsl:param>

  <!-- Always wrap, even if verbatim -->
  <!--
  <xsl:attribute-set name="monospace.verbatim.properties" use-attribute-sets="verbatim.properties monospace.properties">
    <xsl:attribute name="text-align">start</xsl:attribute>
    <xsl:attribute name="wrap-option">no-wrap</xsl:attribute>
  </xsl:attribute-set>
  -->

  <!-- In the 1.71.1 distro, the "modifier" tag only seems to be handled for synopses. -->
  <xsl:template match="modifier">
    <xsl:call-template name="inline.monoseq"></xsl:call-template>
  </xsl:template>

<!-- overrides of formatting specified in html/inline.xsl and fo/inline.xsl -->  
  <xsl:template match="guibutton">
    <xsl:call-template name="inline.boldseq"></xsl:call-template>
  </xsl:template>

  <xsl:template match="guiicon">
    <xsl:call-template name="inline.boldseq"></xsl:call-template>
  </xsl:template>

  <xsl:template match="guilabel">
    <xsl:call-template name="inline.boldseq"></xsl:call-template>
  </xsl:template>

  <xsl:template match="guimenu">
    <xsl:call-template name="inline.boldseq"></xsl:call-template>
  </xsl:template>

  <xsl:template match="guimenuitem">
    <xsl:call-template name="inline.boldseq"></xsl:call-template>
  </xsl:template>

  <xsl:template match="guisubmenu">
    <xsl:call-template name="inline.boldseq"></xsl:call-template>
  </xsl:template>

  <xsl:template match="package">
    <xsl:call-template name="inline.monoseq"></xsl:call-template>
  </xsl:template>
  
  <xsl:template match="type">
    <xsl:call-template name="inline.monoseq"></xsl:call-template>
  </xsl:template>

</xsl:stylesheet>
