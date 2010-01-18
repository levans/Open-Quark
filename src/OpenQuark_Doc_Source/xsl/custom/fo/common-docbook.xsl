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
    common-docbook.xsl
    Creation date: Jul 12, 2007.
    By: Edward Lam
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" version="1.0">
    
  <!-- This (invalid) uri should be rewritten at transform time using a uri resolver. -->
  <!-- To use the latest stylesheets available from the web: change "current2" to "current" -->
  <xsl:import href="http://docbook.sourceforge.net/release/xsl/current2/fo/docbook.xsl"/>

  <xsl:import href="../docbook-common.xsl"/>

  <xsl:param name="body.font.master">11</xsl:param>

  <!-- Add list item indent.
       Otherwise the list item symbol is aligned with the parent's left margin. -->
  <xsl:attribute-set name="compact.list.item.spacing">
    <xsl:attribute name="margin-left">1em</xsl:attribute>
  </xsl:attribute-set>
  <xsl:attribute-set name="list.item.spacing">
    <xsl:attribute name="margin-left">1em</xsl:attribute>
  </xsl:attribute-set>

  <!-- By default, lists nested within other lists use compact spacing.
       Overridden from fo/lists.xsl
    -->
  <xsl:template match="itemizedlist/listitem">
    <xsl:variable name="id"><xsl:call-template name="object.id"/></xsl:variable>
  
    <xsl:variable name="item.contents">
      <fo:list-item-label end-indent="label-end()" xsl:use-attribute-sets="itemizedlist.label.properties">
        <fo:block>
          <xsl:call-template name="itemizedlist.label.markup">
            <xsl:with-param name="itemsymbol">
              <xsl:call-template name="list.itemsymbol">
                <xsl:with-param name="node" select="parent::itemizedlist"/>
              </xsl:call-template>
            </xsl:with-param>
          </xsl:call-template>
        </fo:block>
      </fo:list-item-label>
      <fo:list-item-body start-indent="body-start()">
        <xsl:choose>
          <!-- * work around broken passivetex list-item-body rendering -->
          <xsl:when test="$passivetex.extensions = '1'">
            <xsl:apply-templates/>
          </xsl:when>
          <xsl:otherwise>
            <fo:block>
              <xsl:apply-templates/>
            </fo:block>
          </xsl:otherwise>
        </xsl:choose>
      </fo:list-item-body>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="parent::*/@spacing = 'compact'">
        <fo:list-item id="{$id}" xsl:use-attribute-sets="compact.list.item.spacing">
          <xsl:copy-of select="$item.contents"/>
        </fo:list-item>
      </xsl:when>
<!-- Start change. -->
      <xsl:when test="parent::*/@spacing = 'normal'">
        <fo:list-item id="{$id}" xsl:use-attribute-sets="list.item.spacing">
          <xsl:copy-of select="$item.contents"/>
        </fo:list-item>
      </xsl:when>
      <xsl:when test="ancestor::listitem"> 
        <fo:list-item id="{$id}" xsl:use-attribute-sets="compact.list.item.spacing">
          <xsl:copy-of select="$item.contents"/>
        </fo:list-item>
      </xsl:when>
<!-- End change. -->
      <xsl:otherwise>
        <fo:list-item id="{$id}" xsl:use-attribute-sets="list.item.spacing">
          <xsl:copy-of select="$item.contents"/>
        </fo:list-item>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- From common/common.xsl.  Allow symbols other than disc. -->
  <xsl:template name="next.itemsymbol">
    <xsl:param name="itemsymbol" select="'default'"/>
    <xsl:choose>
      <!-- Change this list if you want to change the order of symbols -->
      <xsl:when test="$itemsymbol = 'disc'">circle</xsl:when>
      <xsl:when test="$itemsymbol = 'circle'">smallblacksquare</xsl:when>  <!-- The change: was "square" -->
      <xsl:otherwise>disc</xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- From fo/lists.xsl.  Allow symbols other than disc. -->
  <xsl:template name="itemizedlist.label.markup">
    <xsl:param name="itemsymbol" select="'disc'"/>

    <xsl:choose>
      <xsl:when test="$itemsymbol='none'"></xsl:when>
      <xsl:when test="$itemsymbol='disc'">&#x2022;</xsl:when>
      <xsl:when test="$itemsymbol='bullet'">&#x2022;</xsl:when>
      <xsl:when test="$itemsymbol='endash'">&#x2013;</xsl:when>
      <xsl:when test="$itemsymbol='emdash'">&#x2014;</xsl:when>
      <!-- Some of these may work in your XSL-FO processor and fonts -->
      <!--
      <xsl:when test="$itemsymbol='square'">&#x25A0;</xsl:when>
      <xsl:when test="$itemsymbol='box'">&#x25A0;</xsl:when>
      <xsl:when test="$itemsymbol='smallblacksquare'">&#x25AA;</xsl:when>
      <xsl:when test="$itemsymbol='circle'">&#x25CB;</xsl:when>
      <xsl:when test="$itemsymbol='opencircle'">&#x25CB;</xsl:when>
      <xsl:when test="$itemsymbol='whitesquare'">&#x25A1;</xsl:when>
      <xsl:when test="$itemsymbol='smallwhitesquare'">&#x25AB;</xsl:when>
      <xsl:when test="$itemsymbol='round'">&#x25CF;</xsl:when>
      <xsl:when test="$itemsymbol='blackcircle'">&#x25CF;</xsl:when>
      <xsl:when test="$itemsymbol='whitebullet'">&#x25E6;</xsl:when>
      <xsl:when test="$itemsymbol='triangle'">&#x2023;</xsl:when>
      <xsl:when test="$itemsymbol='point'">&#x203A;</xsl:when>
      <xsl:when test="$itemsymbol='hand'"><fo:inline 
                           font-family="Wingdings 2">A</fo:inline></xsl:when>
      -->

<!-- Start change. -->
      <!--
        For FOP, can use any of the so-called "Base-14 Fonts"
        (the PDF Specification mandates that these 14 fonts must be available 
         to every PDF reader): 
          Helvetica (normal, bold, italic, bold italic), 
          Times (normal, bold, italic, bold italic), 
          Courier (normal, bold, italic, bold italic), 
          Symbol and 
          ZapfDingbats.
        -->
      <xsl:when test="$itemsymbol='circle'">
        <!-- Tweaked letter "o"
        <fo:inline font-family="Courier" font-size="6" vertical-align="35%">o</fo:inline> -->

        <!-- Tweaked degree sign. -->
        <fo:inline font-family="Courier" font-size="9" vertical-align="0%">&#x00B0;</fo:inline>
      </xsl:when>

      <xsl:when test="$itemsymbol='smallblacksquare'">
        <fo:inline font-family="ZapfDingbats" font-size="3.5" vertical-align="55%">&#x25A0;</fo:inline>
      </xsl:when>
<!-- End change. -->

      <xsl:otherwise>&#x2022;</xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  
  <!-- Italicize cross-references. -->
  <xsl:attribute-set name="xref.properties">
    <xsl:attribute name="font-style">italic</xsl:attribute>
  </xsl:attribute-set>

  <!-- Italicize formulae. -->
    <xsl:template match="phrase[@role = 'formula']">
    <fo:inline font-style="italic">
      <xsl:apply-templates/>
    </fo:inline>
  </xsl:template>


  <!--
     CAL syntax coloring.
     (within programlisting elements)
     Keep this in sync with javahelp.css.
   -->
  <xsl:template match="phrase[@role = 'cal-keyword']">
    <fo:inline color="rgb(127,0,85)">
      <xsl:apply-templates/>
    </fo:inline>
  </xsl:template>
  <xsl:template match="phrase[@role = 'cal-operator']">
    <fo:inline font-weight="bold">
      <xsl:apply-templates/>
    </fo:inline>
  </xsl:template>
  <xsl:template match="phrase[@role = 'cal-string']">
    <fo:inline color="rgb(42,0,255)">
      <xsl:apply-templates/>
    </fo:inline>
  </xsl:template>
  <xsl:template match="phrase[@role = 'cal-caldoc']">
    <fo:inline color="rgb(63,95,191)">
      <xsl:apply-templates/>
    </fo:inline>
  </xsl:template>
  <xsl:template match="phrase[@role = 'cal-caldoctag']">
    <fo:inline color="rgb(127,127,159)">
      <xsl:apply-templates/>
    </fo:inline>
  </xsl:template>
  <xsl:template match="phrase[@role = 'cal-comment']">
    <fo:inline color="rgb(63,127,95)">
      <xsl:apply-templates/>
    </fo:inline>
  </xsl:template>
 

  <!-- Handle emphasis with role="bold-italic" -->
  <!-- Modified from template for inline.boldseq in fo/inline.xsl. -->
  <xsl:template match="emphasis[@role='bold-italic']">
    <xsl:param name="content">
      <xsl:apply-templates/>
    </xsl:param>
    <fo:inline font-weight="bold" font-style="italic">   <!-- The change: added the font-style attribute. -->
      <xsl:if test="@dir">
        <xsl:attribute name="direction">
          <xsl:choose>
            <xsl:when test="@dir = 'ltr' or @dir = 'lro'">ltr</xsl:when>
            <xsl:otherwise>rtl</xsl:otherwise>
          </xsl:choose>
        </xsl:attribute>
      </xsl:if>
      <xsl:copy-of select="$content"/>
    </fo:inline>
  </xsl:template>


  <!--
    Program listings.
    -->
  <xsl:attribute-set name="monospace.verbatim.properties" use-attribute-sets="verbatim.properties monospace.properties"/>

  <xsl:attribute-set name="monospace.properties">
    <!-- Reduce font size.-->
    <xsl:attribute name="font-size">85%</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="verbatim.properties">
    <!-- Enable word-wrap on program listings. -->
    <xsl:attribute name="wrap-option">wrap</xsl:attribute>

    <!-- Indicate the line break by inserting a slash character. -->
    <xsl:attribute name="hyphenation-character">\</xsl:attribute>
  </xsl:attribute-set>
 
  <!-- Based on inline.monoseq template from fo/inline.xsl -->
  <xsl:template name="inline.boldunderlineseq">
    <xsl:param name="content">
      <xsl:apply-templates></xsl:apply-templates>
    </xsl:param>
    <fo:inline font-weight="bold" text-decoration="underline">  <!-- the change.. -->
      <xsl:if test="@dir">
        <xsl:attribute name="direction">
          <xsl:choose>
            <xsl:when test="@dir = 'ltr' or @dir = 'lro'">ltr</xsl:when>
            <xsl:otherwise>rtl</xsl:otherwise>
          </xsl:choose>
        </xsl:attribute>
      </xsl:if>
      <xsl:copy-of select="$content"></xsl:copy-of>
    </fo:inline>
  </xsl:template> 


  <!--
    Formal para - modify so that the title is not contiguous with the following para element.
      It instead will appear on a separate line.
      
      Override template from fo/block.xsl
      
      TODO: it would be nice if we could force the title to appear with a little bit of text on the same page.
        Right now it is possible for the title to appear by itself
    -->
  <xsl:template match="formalpara">
    <fo:block>    <!-- the change: don't apply attributes to this block -->
      <xsl:if test="@id">
        <xsl:attribute name="id"><xsl:value-of select="@id"/></xsl:attribute>
      </xsl:if>
      <xsl:apply-templates/>
    </fo:block>
  </xsl:template>

  <xsl:template match="formalpara/title">
    <!-- Change 1: wrap in block with attribute set. -->
    <fo:block xsl:use-attribute-sets="normal.para.spacing">
      <xsl:apply-imports/>
      <!--
      <br/>
      -->
    </fo:block>
  </xsl:template>

  <xsl:template match="formalpara/para">
    <!-- Change: wrap in block with attribute set. -->
    <fo:block xsl:use-attribute-sets="normal.para.spacing">
      <xsl:apply-templates/>
    </fo:block>
  </xsl:template>


  <!-- Centre figure images. -->
  <xsl:attribute-set name="figure.properties" 
                     use-attribute-sets="formal.object.properties">
     <xsl:attribute name="text-align">center</xsl:attribute>
  </xsl:attribute-set>

  <!-- Centre titles for formal objects (figures, examples, ...). -->
  <xsl:attribute-set name="formal.title.properties" 
                     use-attribute-sets="formal.object.properties">
     <xsl:attribute name="text-align">center</xsl:attribute>
  </xsl:attribute-set>


  <!-- Formal titles (for figures, tables, etc.)
       - don't enlarge text as much, increase surrounding whitespace. -->
  <xsl:attribute-set name="formal.title.properties" use-attribute-sets="normal.para.spacing">
    <xsl:attribute name="font-weight">bold</xsl:attribute>
    <xsl:attribute name="font-size">
      <xsl:value-of select="$body.font.master * 1.1"></xsl:value-of>  <!-- Change 1.2 to 1.1 -->
      <xsl:text>pt</xsl:text>
    </xsl:attribute>
    <xsl:attribute name="hyphenate">false</xsl:attribute>
    <!-- Changes: add 1.0em to each .. -->
    <xsl:attribute name="space-after.minimum">1.4em</xsl:attribute>
    <xsl:attribute name="space-after.optimum">1.6em</xsl:attribute>
    <xsl:attribute name="space-after.maximum">1.8em</xsl:attribute>
  </xsl:attribute-set>


  <!-- 
    Adjust header cell sizes so there is enough space for the copyright text.
      Single-sided output: left centre right
      Double-sided output: inside centre outside
    -->
  <xsl:param name="header.column.widths">1 7 1</xsl:param>

  <!-- Adjust footer cell sizes so there is enough space for the copyright text. -->
  <xsl:param name="footer.column.widths">10 0 1</xsl:param>

  <!--
    Footer - add copyright notice on left, page number on right.
    Override template from fo/pagesetup.xsl
    -->
  <xsl:template name="footer.content">
    <xsl:param name="pageclass" select="''"/>
    <xsl:param name="sequence" select="''"/>
    <xsl:param name="position" select="''"/>
    <xsl:param name="gentext-key" select="''"/>
  
  <!--
    <fo:block>
      <xsl:value-of select="$pageclass"/>
      <xsl:text>, </xsl:text>
      <xsl:value-of select="$sequence"/>
      <xsl:text>, </xsl:text>
      <xsl:value-of select="$position"/>
      <xsl:text>, </xsl:text>
      <xsl:value-of select="$gentext-key"/>
    </fo:block>
  -->
  
    <fo:block>
      <!-- pageclass can be front, body, back -->
      <!-- sequence can be odd, even, first, blank -->
      <!-- position can be left, center, right -->
      <xsl:choose>
        <xsl:when test="$pageclass = 'titlepage'">
          <!-- nop; no footer on title pages -->
        </xsl:when>
  
        <xsl:when test="$double.sided != 0 and $sequence = 'even'
                        and $position='left'">
          <fo:page-number/>
        </xsl:when>
  
        <xsl:when test="$double.sided != 0 and ($sequence = 'odd' or $sequence = 'first')
                        and $position='right'">
          <fo:page-number/>
        </xsl:when>
<!--
        Changed from:
        <xsl:when test="$double.sided = 0 and $position='center'">
          <fo:page-number/>
        </xsl:when>
  -->
        <xsl:when test="$double.sided = 0">
          <xsl:choose>
            <xsl:when test="$position='left'">
              <fo:inline>
                <xsl:attribute name="font-size">90%</xsl:attribute>
                <xsl:value-of select="$footer.text" />
              </fo:inline>
            </xsl:when>
            <xsl:when test="$position='right'">
              <fo:page-number/>
            </xsl:when>
          </xsl:choose>
        </xsl:when>
        <!-- end change -->
  
        <xsl:when test="$sequence='blank'">
          <xsl:choose>
            <xsl:when test="$double.sided != 0 and $position = 'left'">
              <fo:page-number/>
            </xsl:when>
            <xsl:when test="$double.sided = 0 and $position = 'center'">
              <fo:page-number/>
            </xsl:when>
            <xsl:otherwise>
              <!-- nop -->
            </xsl:otherwise>
          </xsl:choose>
        </xsl:when>
  
        <xsl:otherwise>
          <!-- nop -->
        </xsl:otherwise>
      </xsl:choose>
    </fo:block>
  </xsl:template>

  <!-- Override template from fo/inline.xsl -->
  <xsl:template name="process.menuchoice">
    <xsl:param name="nodelist" select="guibutton|guiicon|guilabel|guimenu|guimenuitem|guisubmenu|interface"></xsl:param><!-- not(shortcut) -->
    <xsl:param name="count" select="1"></xsl:param>

    <xsl:variable name="mm.separator">
      <xsl:choose>

        <!-- The change - don't test for fop.extensions -->
        <xsl:when test="contains($menuchoice.menu.separator, '&#x2192;')">
          <fo:inline font-family="Symbol">
            <xsl:copy-of select="$menuchoice.menu.separator"></xsl:copy-of>
          </fo:inline>
        </xsl:when>
        <xsl:otherwise>
          <xsl:copy-of select="$menuchoice.menu.separator"></xsl:copy-of>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:choose>
      <xsl:when test="$count>count($nodelist)"></xsl:when>
      <xsl:when test="$count=1">
        <xsl:apply-templates select="$nodelist[$count=position()]"></xsl:apply-templates>
        <xsl:call-template name="process.menuchoice">
          <xsl:with-param name="nodelist" select="$nodelist"></xsl:with-param>
          <xsl:with-param name="count" select="$count+1"></xsl:with-param>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:variable name="node" select="$nodelist[$count=position()]"></xsl:variable>
        <xsl:choose>
          <xsl:when test="name($node)='guimenuitem'
                          or name($node)='guisubmenu'">
            <xsl:copy-of select="$mm.separator"></xsl:copy-of>
          </xsl:when>
          <xsl:otherwise>
            <xsl:copy-of select="$menuchoice.separator"></xsl:copy-of>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="$node"></xsl:apply-templates>
        <xsl:call-template name="process.menuchoice">
          <xsl:with-param name="nodelist" select="$nodelist"></xsl:with-param>
          <xsl:with-param name="count" select="$count+1"></xsl:with-param>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <!-- Override template from fo/inline.xsl -->
  <!-- make links to Glossary italic -->
  <xsl:template match="glossterm" name="glossterm">
    <xsl:param name="firstterm" select="0"></xsl:param>
  
    <xsl:choose>
      <xsl:when test="($firstterm.only.link = 0 or $firstterm = 1) and @linkend">
        <xsl:variable name="targets" select="key('id',@linkend)"></xsl:variable>
        <xsl:variable name="target" select="$targets[1]"></xsl:variable>
  
        <xsl:choose>
          <xsl:when test="$target">
            <fo:basic-link internal-destination="{@linkend}" xsl:use-attribute-sets="xref.properties">
              <xsl:call-template name="inline.italicseq"></xsl:call-template>
            </fo:basic-link>
          </xsl:when>
          <xsl:otherwise>
            <xsl:call-template name="inline.italicseq"></xsl:call-template>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
  
      <xsl:when test="not(@linkend)
                      and ($firstterm.only.link = 0 or $firstterm = 1)
                      and ($glossterm.auto.link != 0)
                      and $glossary.collection != ''">
        <xsl:variable name="term">
          <xsl:choose>
            <xsl:when test="@baseform"><xsl:value-of select="@baseform"></xsl:value-of></xsl:when>
            <xsl:otherwise><xsl:value-of select="."></xsl:value-of></xsl:otherwise>
          </xsl:choose>
        </xsl:variable>
        <xsl:variable name="cterm" select="(document($glossary.collection,.)//glossentry[glossterm=$term])[1]"></xsl:variable>
  
        <xsl:choose>
          <xsl:when test="not($cterm)">
            <xsl:message>
              <xsl:text>There's no entry for </xsl:text>
              <xsl:value-of select="$term"></xsl:value-of>
              <xsl:text> in </xsl:text>
              <xsl:value-of select="$glossary.collection"></xsl:value-of>
            </xsl:message>
            <xsl:call-template name="inline.italicseq"></xsl:call-template>
          </xsl:when>
          <xsl:otherwise>
            <xsl:variable name="id">
              <xsl:choose>
                <xsl:when test="$cterm/@id">
                  <xsl:value-of select="$cterm/@id"></xsl:value-of>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="generate-id($cterm)"></xsl:value-of>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:variable>
            <fo:basic-link internal-destination="{$id}" xsl:use-attribute-sets="xref.properties">
              <xsl:call-template name="inline.italicseq"></xsl:call-template>
            </fo:basic-link>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
  
      <xsl:when test="not(@linkend)
                      and ($firstterm.only.link = 0 or $firstterm = 1)
                      and $glossterm.auto.link != 0">
        <xsl:variable name="term">
          <xsl:choose>
            <xsl:when test="@baseform">
              <xsl:value-of select="@baseform"></xsl:value-of>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="."></xsl:value-of>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>
  
        <xsl:variable name="targets" select="//glossentry[glossterm=$term or glossterm/@baseform=$term]"></xsl:variable>
  
        <xsl:variable name="target" select="$targets[1]"></xsl:variable>
  
        <xsl:choose>
          <xsl:when test="count($targets)=0">
            <xsl:message>
              <xsl:text>Error: no glossentry for glossterm: </xsl:text>
              <xsl:value-of select="."></xsl:value-of>
              <xsl:text>.</xsl:text>
            </xsl:message>
            <xsl:call-template name="inline.italicseq"></xsl:call-template>
          </xsl:when>
          <xsl:otherwise>
            <xsl:variable name="termid">
              <xsl:call-template name="object.id">
                <xsl:with-param name="object" select="$target"></xsl:with-param>
              </xsl:call-template>
            </xsl:variable>
  
            <fo:basic-link internal-destination="{$termid}" xsl:use-attribute-sets="xref.properties">
              <xsl:call-template name="inline.italicseq"></xsl:call-template>    <!-- The change.. -->
            </fo:basic-link>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="inline.italicseq"></xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  
  <!-- Override template from fo/graphics.xsl 
       - fix graphics default size to 75%   
  -->
  <xsl:template name="process.image">
    <!-- When this template is called, the current node should be  -->
    <!-- a graphic, inlinegraphic, imagedata, or videodata. All    -->
    <!-- those elements have the same set of attributes, so we can -->
    <!-- handle them all in one place.                             -->
  
    <xsl:variable name="scalefit">
      <xsl:choose>
        <xsl:when test="$ignore.image.scaling != 0">0</xsl:when>
        <xsl:when test="@contentwidth">0</xsl:when>
        <xsl:when test="@contentdepth and 
                        @contentdepth != '100%'">0</xsl:when>
        <xsl:when test="@scale">0</xsl:when>
        <xsl:when test="@scalefit"><xsl:value-of select="@scalefit"></xsl:value-of></xsl:when>
        <xsl:when test="@width or @depth">1</xsl:when>
        <xsl:otherwise>0</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
  
    <xsl:variable name="scale">
      <xsl:choose>
        <xsl:when test="$ignore.image.scaling != 0">0</xsl:when>
        <xsl:when test="@contentwidth or @contentdepth">1.0</xsl:when>
        <xsl:when test="@scale">
          <xsl:value-of select="@scale div 100.0"></xsl:value-of>
        </xsl:when>
        <xsl:otherwise>1.0</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
  
    <xsl:variable name="filename">
      <xsl:choose>
        <xsl:when test="local-name(.) = 'graphic'
                        or local-name(.) = 'inlinegraphic'">
          <!-- handle legacy graphic and inlinegraphic by new template --> 
          <xsl:call-template name="mediaobject.filename">
            <xsl:with-param name="object" select="."></xsl:with-param>
          </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
          <!-- imagedata, videodata, audiodata -->
          <xsl:call-template name="mediaobject.filename">
            <xsl:with-param name="object" select=".."></xsl:with-param>
          </xsl:call-template>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
  
    <xsl:variable name="content-type">
      <xsl:if test="@format">
        <xsl:call-template name="graphic.format.content-type">
          <xsl:with-param name="format" select="@format"></xsl:with-param>
        </xsl:call-template>
      </xsl:if>
    </xsl:variable>
  
    <xsl:variable name="bgcolor">
      <xsl:call-template name="dbfo-attribute">
        <xsl:with-param name="pis" select="../processing-instruction('dbfo')"></xsl:with-param>
        <xsl:with-param name="attribute" select="'background-color'"></xsl:with-param>
      </xsl:call-template>
    </xsl:variable>
  
    <fo:external-graphic>
      <xsl:attribute name="src">
        <xsl:call-template name="fo-external-image">
          <xsl:with-param name="filename">
            <xsl:if test="$img.src.path != '' and
                          not(starts-with($filename, '/')) and
                          not(contains($filename, '://'))">
              <xsl:value-of select="$img.src.path"></xsl:value-of>
            </xsl:if>
            <xsl:value-of select="$filename"></xsl:value-of>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:attribute>
  
      <xsl:attribute name="width">
        <xsl:choose>
          <xsl:when test="$ignore.image.scaling != 0">auto</xsl:when>
          <xsl:when test="contains(@width,'%')">
            <xsl:value-of select="@width"></xsl:value-of>
          </xsl:when>
          <xsl:when test="@width and not(@width = '')">
            <xsl:call-template name="length-spec">
              <xsl:with-param name="length" select="@width"></xsl:with-param>
              <xsl:with-param name="default.units" select="'px'"></xsl:with-param>
            </xsl:call-template>
          </xsl:when>
          <xsl:when test="not(@depth) and $default.image.width != ''">
            <xsl:call-template name="length-spec">
              <xsl:with-param name="length" select="$default.image.width"></xsl:with-param>
              <xsl:with-param name="default.units" select="'px'"></xsl:with-param>
            </xsl:call-template>
          </xsl:when>
          <xsl:otherwise>auto</xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
  
      <xsl:attribute name="height">
        <xsl:choose>
          <xsl:when test="$ignore.image.scaling != 0">auto</xsl:when>
          <xsl:when test="contains(@depth,'%')">
            <xsl:value-of select="@depth"></xsl:value-of>
          </xsl:when>
          <xsl:when test="@depth">
            <xsl:call-template name="length-spec">
              <xsl:with-param name="length" select="@depth"></xsl:with-param>
              <xsl:with-param name="default.units" select="'px'"></xsl:with-param>
            </xsl:call-template>
          </xsl:when>
          <xsl:otherwise>auto</xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
  
      <xsl:attribute name="content-width">
        <xsl:choose>
          <xsl:when test="$ignore.image.scaling != 0">auto</xsl:when>
          <xsl:when test="contains(@contentwidth,'%')">
            <xsl:value-of select="@contentwidth"></xsl:value-of>
          </xsl:when>
          <xsl:when test="@contentwidth">
            <xsl:call-template name="length-spec">
              <xsl:with-param name="length" select="@contentwidth"></xsl:with-param>
              <xsl:with-param name="default.units" select="'px'"></xsl:with-param>
            </xsl:call-template>
          </xsl:when>
          <xsl:when test="number($scale) != 1.0">
            <xsl:value-of select="$scale * 100"></xsl:value-of>
            <xsl:text>%</xsl:text>
          </xsl:when>
          <xsl:when test="$scalefit = 1">scale-to-fit</xsl:when>
          <xsl:otherwise>75%</xsl:otherwise>    <!-- Change 1 -->
        </xsl:choose>
      </xsl:attribute>
  
      <xsl:attribute name="content-height">
        <xsl:choose>
          <xsl:when test="$ignore.image.scaling != 0">auto</xsl:when>
          <xsl:when test="contains(@contentdepth,'%')">
            <xsl:value-of select="@contentdepth"></xsl:value-of>
          </xsl:when>
          <xsl:when test="@contentdepth">
            <xsl:call-template name="length-spec">
              <xsl:with-param name="length" select="@contentdepth"></xsl:with-param>
              <xsl:with-param name="default.units" select="'px'"></xsl:with-param>
            </xsl:call-template>
          </xsl:when>
          <xsl:when test="number($scale) != 1.0">
            <xsl:value-of select="$scale * 100"></xsl:value-of>
            <xsl:text>%</xsl:text>
          </xsl:when>
          <xsl:when test="$scalefit = 1">scale-to-fit</xsl:when>
          <xsl:otherwise>75%</xsl:otherwise>    <!-- Change 2 -->
        </xsl:choose>
      </xsl:attribute>
  
      <xsl:if test="$content-type != ''">
        <xsl:attribute name="content-type">
          <xsl:value-of select="concat('content-type:',$content-type)"></xsl:value-of>
        </xsl:attribute>
      </xsl:if>
  
      <xsl:if test="$bgcolor != ''">
        <xsl:attribute name="background-color">
          <xsl:value-of select="$bgcolor"></xsl:value-of>
        </xsl:attribute>
      </xsl:if>
  
      <xsl:if test="@align">
        <xsl:attribute name="text-align">
          <xsl:value-of select="@align"></xsl:value-of>
        </xsl:attribute>
      </xsl:if>
  
      <xsl:if test="@valign">
        <xsl:attribute name="display-align">
          <xsl:choose>
            <xsl:when test="@valign = 'top'">before</xsl:when>
            <xsl:when test="@valign = 'middle'">center</xsl:when>
            <xsl:when test="@valign = 'bottom'">after</xsl:when>
            <xsl:otherwise>auto</xsl:otherwise>
          </xsl:choose>
        </xsl:attribute>
      </xsl:if>
    </fo:external-graphic>
  </xsl:template>

</xsl:stylesheet>
