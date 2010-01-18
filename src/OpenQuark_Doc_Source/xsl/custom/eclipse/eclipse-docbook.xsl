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
    eclipse-docbook.xsl
    Creation date: Feb 26, 2007.
    By: Edward Lam
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exsl="http://exslt.org/common"
                xmlns:cf="http://docbook.sourceforge.net/xmlns/chunkfast/1.0"
                xmlns:ng="http://docbook.org/docbook-ng"
                xmlns:db="http://docbook.org/ns/docbook"
                version="1.0"
                exclude-result-prefixes="exsl cf ng db"
                xmlns:fo="http://www.w3.org/1999/XSL/Format" 
                >
    
  <!-- This (invalid) uri should be rewritten at transform time using a uri resolver. -->
  <!-- To use the latest stylesheets available from the web: change "current2" to "current" -->
  <xsl:import href="http://docbook.sourceforge.net/release/xsl/current2/eclipse/eclipse.xsl"/>

  <xsl:import href="../docbook-common-html.xsl"/>

  <xsl:param name="eclipse.plugin.name" select="Eclipse_CAL_Development_User_Guide"/>
  <xsl:param name="eclipse.plugin.id" select="com.businessobjects.lang.cal.eclipse.doc"/>
  <xsl:param name="eclipse.plugin.provider" select="Business_Objects_Software_Limited"/>

  <xsl:param name="html.stylesheet" select="'../eclipsehelp.css'"/>
  
  <!-- Put all generated files in the "content" folder. -->
  <xsl:param name="base.dir" select="'content/'" />
  
  <!-- Don't blather about all the html files we're generating. -->
  <xsl:param name="chunk.quietly" select="1" />
  

<!--
  <xsl:param name="toc.section.depth">2</xsl:param>
  <xsl:param name="generate.section.toc.level" select="2"></xsl:param>
  -->

  <!--
    MULTI-HACK (!!):
      We need to declare the doctype in order to get IE not to render our html in quirks mode.
        (Note however that the output still fails html validation in several ways..)
        In IE quirks mode some line feeds are ignored.
    
      The following two parameters are supposed to set the doctype for output documents.
      However they don't work for the chunker under Xalan.
        Xalan uses redirect:write, which doesn't seem to have doctype configuration options for its output.
        So without anything else, the output html would still be missing a doctype declaration.
      
      Chunking with Xalan causes all output chunks to inherit the doctype from the base output.
      So we override the base xsl:output declaration and add the two doctype attributes there.
        This works as long as all output can use the same doctype.
      
      Note that there seems to be a bug in Xalan which causes two doctype declarations to be spit out with this.
      Thankfully both IE and Mozilla/Firefox seem to be able to handle this.
      
    HOWEVER:
      The eclipse stylesheet write two types of files: html and xml.
        HTML are ok (except for having the duplicate declarations).
        XML (toc.xml and plugin.xml) are not.  The doctype declaration must be manually removed from these files.
    -->
  <xsl:param name="chunker.output.doctype-public" select="'-//W3C//DTD HTML 4.01 Transitional//EN'"/>
  <xsl:param name="chunker.output.doctype-system" select="'http://www.w3.org/TR/html4/loose.dtd'"/>

  <xsl:output method="html"
            encoding="ISO-8859-1"
            indent="no"
            doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"
            doctype-system="http://www.w3.org/TR/html4/loose.dtd"
            />



  <!-- Copied directly from html/chunk-code.xsl.
       DocBook 5 wants this on the root element: xmlns="http://docbook.org/ns/docbook"
        But this makes the eclipse stylesheets barf.
       This code strips the namespace from documents with the namespace attribute as specified in DocBook 5,
        so that they can have applied to them the docbook stylesheets, which themselves have not yet been 
        converted to acknowledging the namespaces.
       Other docbook stylesheets have this rule, but the eclipse stylesheets seem to be missing it.
       
       Note: it's unclear whether it would be better to instead attempt to just:
         1) strip the namespace 
         2) apply-imports
    -->
  <xsl:template match="/">
    <xsl:choose>
      <!-- include extra test for Xalan quirk -->
      <xsl:when test="(function-available('exsl:node-set') or
                       contains(system-property('xsl:vendor'),
                         'Apache Software Foundation'))
                      and (*/self::ng:* or */self::db:*)">
        <!-- Hack! If someone hands us a DocBook V5.x or DocBook NG document,
             toss the namespace and continue. Someday we'll reverse this logic
             and add the namespace to documents that don't have one.
             But not before the whole stylesheet has been converted to use
             namespaces. i.e., don't hold your breath -->
        <xsl:message>Stripping namespace from DocBook 5 document.</xsl:message>
        <xsl:variable name="nons">
          <xsl:apply-templates mode="stripNS"/>
        </xsl:variable>
        <xsl:message>Processing stripped document.</xsl:message>
        <xsl:apply-templates select="exsl:node-set($nons)"/>
      </xsl:when>
      <!-- Can't process unless namespace removed -->
      <xsl:when test="*/self::ng:* or */self::db:*">
        <xsl:message terminate="yes">
          <xsl:text>Unable to strip the namespace from DB5 document,</xsl:text>
          <xsl:text> cannot proceed.</xsl:text>
        </xsl:message>
      </xsl:when>
      <xsl:otherwise>
        <xsl:choose>
          <xsl:when test="$rootid != ''">
            <xsl:choose>
              <xsl:when test="count(key('id',$rootid)) = 0">
                <xsl:message terminate="yes">
                  <xsl:text>ID '</xsl:text>
                  <xsl:value-of select="$rootid"/>
                  <xsl:text>' not found in document.</xsl:text>
                </xsl:message>
              </xsl:when>
              <xsl:otherwise>
                <xsl:if test="$collect.xref.targets = 'yes' or
                              $collect.xref.targets = 'only'">
                  <xsl:apply-templates select="key('id', $rootid)"
                                       mode="collect.targets"/>
                </xsl:if>
                <xsl:if test="$collect.xref.targets != 'only'">
                  <xsl:apply-templates select="key('id',$rootid)"
                                       mode="process.root"/>

                    <!-- Change 1: add two lines: -->
                    <xsl:call-template name="etoc"/>
                    <xsl:call-template name="plugin.xml"/>

                  <xsl:if test="$tex.math.in.alt != ''">
                    <xsl:apply-templates select="key('id',$rootid)"
                                         mode="collect.tex.math"/>
                  </xsl:if>
                  <xsl:if test="$generate.manifest != 0">
                    <xsl:call-template name="generate.manifest">
                      <xsl:with-param name="node" select="key('id',$rootid)"/>
                    </xsl:call-template>
                  </xsl:if>
                </xsl:if>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:when>
          <xsl:otherwise>
            <xsl:if test="$collect.xref.targets = 'yes' or
                          $collect.xref.targets = 'only'">
              <xsl:apply-templates select="/" mode="collect.targets"/>
            </xsl:if>
            <xsl:if test="$collect.xref.targets != 'only'">
              <xsl:apply-templates select="/" mode="process.root"/>

              <!-- Change 2: add two lines: -->
              <xsl:call-template name="etoc"/>
              <xsl:call-template name="plugin.xml"/>

              <xsl:if test="$tex.math.in.alt != ''">
                <xsl:apply-templates select="/" mode="collect.tex.math"/>
              </xsl:if>
              <xsl:if test="$generate.manifest != 0">
                <xsl:call-template name="generate.manifest">
                  <xsl:with-param name="node" select="/"/>
                </xsl:call-template>
              </xsl:if>
            </xsl:if>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
</xsl:stylesheet>
