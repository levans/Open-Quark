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
    javahelp-docbook.xsl
    Creation date: Dec 15, 2006.
    By: Neil Corkum
-->
<!DOCTYPE stylesheet [
<!ENTITY ntilde  "&#241;" ><!-- small n, tilde -->
<!ENTITY lsquo   "&#8216;"> <!-- left single quotation mark, U+2018 ISOnum -->
<!ENTITY rsquo   "&#8217;"> <!-- right single quotation mark, U+2019 ISOnum -->
<!ENTITY ldquo   "&#8220;"> <!-- left double quotation mark, U+201C ISOnum -->
<!ENTITY rdquo   "&#8221;"> <!-- right double quotation mark, U+201D ISOnum -->
<!ENTITY rarr    "&#8594;"> <!-- rightwards arrow, U+2192 ISOnum -->
<!ENTITY hellip  "&#8230;"> <!-- horizontal ellipsis = three dot leader, U+2026 ISOpub -->

]>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" version="1.0">
    
  <!-- This (invalid) uri should be rewritten at transform time using a uri resolver. -->
  <!-- To use the latest stylesheets available from the web: change "current2" to "current" -->
  <xsl:import href="http://docbook.sourceforge.net/release/xsl/current2/javahelp/profile-javahelp.xsl"/>

  <!-- Import common html generation customizations. -->
  <xsl:import href="../docbook-common-html.xsl"/>

  <xsl:param name="html.stylesheet" select="'javahelp.css'"></xsl:param>

  <!-- 
       In JRE 1.4.2, JavaHelp uses javax.swing.text.html.HTMLEditorKit as its html browser.
       This supports HTML 3.2 + some extensions
         see: http://java.sun.com/j2se/1.4.2/docs/api/javax/swing/text/html/HTMLEditorKit.html

       Rules in this section are to make the HTML work properly in Javahelp with JRE 1.4.2 
         If Javahelp or a new JRE adds support for HTML 4.0, these may be removable.
  -->

  <xsl:output method="html" 
            version="xxx"
            encoding="ISO-8859-1"
            indent="no"
            />

  <!-- Replace characters that javahelp can't handle. -->
  
  <!-- Default menu separator is a right-arrow symbol.
       Translated to the symbol &rarr; by the xsl transformer.
       This isn't available in html 3.2? -->
  <xsl:param name="menuchoice.menu.separator"> -&gt; </xsl:param>
  
  <xsl:template match="text()" name="javahelp-hack">
    <xsl:variable name="apos">'</xsl:variable>
    
    <!-- left and right single-quotation marks with apostrophes. -->
    <xsl:variable name="single-quote-escaped-text" 
                  select='translate(., "&lsquo;&rsquo;", "&apos;&apos;")'/>

    <!-- left and right double-quotation marks with standard quotes. -->
    <xsl:variable name="quote-escaped-text" 
                  select="translate($single-quote-escaped-text, '&ldquo;&rdquo;', '&quot;&quot;')" />

    <xsl:call-template name="replace-hellip">
      <xsl:with-param name="string" select="$quote-escaped-text" />
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="replace-hellip">
     <xsl:param name="string" select="." />
     <xsl:choose>
        <xsl:when test="contains($string, '&hellip;')">
           <xsl:value-of select="substring-before($string, '&hellip;')" />
           <xsl:text>...</xsl:text>
           <xsl:call-template name="replace-hellip">
              <xsl:with-param name="string" select="substring-after($string, '&hellip;')" />
           </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
           <xsl:value-of select="$string" />
        </xsl:otherwise>
     </xsl:choose>
  </xsl:template>
  <!-- end of templates hopefully removable if HTML support is updated -->
  
  <!-- Override template from javahelp/profile-javahelp.xsl (also present in javahelp/javahelp.xsl)-->
  <!-- change initial element in helpset to Welcome page -->
  <xsl:template name="helpset.content">
    <xsl:variable name="title">
      <xsl:apply-templates select="." mode="title.markup"></xsl:apply-templates>
    </xsl:variable>
  
    <helpset version="1.0">
      <title>
        <xsl:value-of select="$title"></xsl:value-of>
      </title>
  
      <!-- maps -->
      <maps>
        <homeID>Welcome</homeID>      <!-- Change: was "top" -->
        <mapref location="jhelpmap.jhm"></mapref>
      </maps>
  
      <!-- views -->
      <view>
        <name>TOC</name>
        <label>Table Of Contents</label>
        <type>javax.help.TOCView</type>
        <data>jhelptoc.xml</data>
      </view>
  
      <view>
        <name>Index</name>
        <label>Index</label>
        <type>javax.help.IndexView</type>
        <data>jhelpidx.xml</data>
      </view>
  
      <view>
        <name>Search</name>
        <label>Search</label>
        <type>javax.help.SearchView</type>
        <data engine="com.sun.java.help.search.DefaultSearchEngine">JavaHelpSearch</data>
      </view>
    </helpset>
  </xsl:template>

</xsl:stylesheet>
