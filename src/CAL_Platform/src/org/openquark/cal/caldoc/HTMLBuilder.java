/*
 * Copyright (c) 2007 BUSINESS OBJECTS SOFTWARE LIMITED
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *  
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *  
 *     * Neither the name of Business Objects nor the names of its contributors
 *       may be used to endorse or promote products derived from this software
 *       without specific prior written permission.
 *  
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */


/*
 * HTMLBuilder.java
 * Creation date: Sep 28, 2005.
 * By: Joseph Wong
 */
package org.openquark.cal.caldoc;

import javax.swing.text.html.HTML.Attribute;
import javax.swing.text.html.HTML.Tag;

import org.openquark.cal.util.ArrayStack;

/**
 * This class implements a rudimentary builder for constructing HTML.
 * It is based on a string builder, and does not build up a DOM for the entire document.
 *
 * @author Joseph Wong
 */
final class HTMLBuilder {
    
    /** The string builder holding the HTML being built.*/
    private final StringBuilder buffer = new StringBuilder();
    
    /** Keeps track of the current indent level. */
    private int indentLevel = 0;
    
    /** The number of spaces to use per indent level. */
    private static final int INDENT = 1;
    
    /** The stack of HTML tags representing the current context (from the parent element all the way up to the root element). */
    private final ArrayStack<Tag> tagStack = ArrayStack.make();
    
    /** Keeps track of the immediately enclosing tag. */
    private Tag currentTag = null;
    
    /** Keeps track of whether a newline was just emitted (potentially suffixed with some whitespace). */
    private boolean justEmittedNewline = false;
    
    /**
     * Represents a list of attributes for an HTML element.
     *
     * @author Joseph Wong
     */
    final static class AttributeList {
        
        /** The HTML representation of the attribute list. */
        private final String htmlRep;
        
        /**
         * Private constructor.
         * Factory methods should be used instead for constructing instances.
         * 
         * @param htmlRep the HTML representation of the attribute list.
         */
        private AttributeList(String htmlRep) {
            this.htmlRep = htmlRep;
        }
        
        /**
         * Creates an empty attribute list.
         * @return an empty attribute list.
         */
        static AttributeList make() {
            return new AttributeList("");
        }
        
        /**
         * Creates an attribute list of one attribute-value pair.
         * @param attribute the attribute.
         * @param value the value.
         * @return a new attribute list.
         */
        static AttributeList make(String attribute, String value) {
            return new AttributeList(attribute + "='" + value + "'");
        }
        
        /**
         * Creates an attribute list of one attribute-value pair.
         * @param attribute the attribute.
         * @param value the value.
         * @return a new attribute list.
         */
        static AttributeList make(Attribute attribute, String value) {
            return make(attribute.toString(), value);
        }
        
        /**
         * Creates an attribute list of two attribute-value pairs.
         * @param attribute1 the first attribute.
         * @param value1 the first value.
         * @param attribute2 the second attribute.
         * @param value2 the second value.
         * @return a new attribute list.
         */
        static AttributeList make(Attribute attribute1, String value1, Attribute attribute2, String value2) {
            return make(attribute1, value1).concat(make(attribute2, value2));
        }
        
        /**
         * Creates an attribute list of four attribute-value pairs.
         * @param attribute1 the first attribute.
         * @param value1 the first value.
         * @param attribute2 the second attribute.
         * @param value2 the second value.
         * @param attribute3 the third attribute.
         * @param value3 the third value.
         * @return a new attribute list.
         */
        static AttributeList make(Attribute attribute1, String value1, Attribute attribute2, String value2, Attribute attribute3, String value3) {
            return make(attribute1, value1, attribute2, value2).concat(make(attribute3, value3));
        }
        
        /**
         * Creates an attribute list of four attribute-value pairs.
         * @param attribute1 the first attribute.
         * @param value1 the first value.
         * @param attribute2 the second attribute.
         * @param value2 the second value.
         * @param attribute3 the third attribute.
         * @param value3 the third value.
         * @param attribute4 the fourth attribute.
         * @param value4 the fourth value.
         * @return a new attribute list.
         */
        static AttributeList make(Attribute attribute1, String value1, Attribute attribute2, String value2, Attribute attribute3, String value3, Attribute attribute4, String value4) {
            return make(attribute1, value1, attribute2, value2, attribute3, value3).concat(make(attribute4, value4));
        }
        
        /**
         * Creates a new attribute list out of the concatenation of this list with another list.
         * @param other the other list.
         * @return a new attribute list that is the concatenation of this list with the other list.
         */
        AttributeList concat(AttributeList other) {
            return new AttributeList(htmlRep + " " + other.htmlRep);
        }
        
        /**
         * @return the HTML representation of the attribute list.
         */
        String toHTML() {
            return htmlRep;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return htmlRep;
        }
    }

    /**
     * Returns whether the current context is to generate preformatted text.
     * (For determining whether whitespace could be adjusted/inserted by the builder.)
     * 
     * @return true if the current context is to generate preformatted text; false otherwise.
     */
    private boolean inPreformattedText() {
        if (currentTag == null) {
            return false;
        } else {
            return currentTag.isPreformatted();
        }
    }
    
    /**
     * Writes an open tag for an element to the string builder, without adjusting indentation or inserting newlines.
     * @param tag the tag.
     * @param attributes the element's attributes.
     */
    private void writeOpenTag(Tag tag, String attributes) {
        buffer.append('<').append(tag.toString());
        if (attributes != null && attributes.length() > 0) {
            buffer.append(' ').append(attributes);
        }
        buffer.append('>');
        
        tagStack.push(tag);
        currentTag = tag;
    }
    
    /**
     * Writes an open tag for a block element to the string builder.
     * @param tag the tag.
     * @return this HTMLBuilder instance.
     */
    private HTMLBuilder openBlockTag(Tag tag) {
        return openBlockTag(tag, null);
    }
    
    /**
     * Writes an open tag for a block element to the string builder.
     * @param tag the tag.
     * @param attributes the element's attributes.
     * @return this HTMLBuilder instance.
     */
    private HTMLBuilder openBlockTag(Tag tag, String attributes) {
        if (!justEmittedNewline) {
            newlineAndIndent();
        }
        writeOpenTag(tag, attributes);
        indentLevel += INDENT;
        if (!tag.isPreformatted()) {
            newlineAndIndent();
        }
        return this;
    }
    
    /**
     * Writes an open tag for an inline element to the string builder.
     * @param tag the tag.
     * @return this HTMLBuilder instance.
     */
    private HTMLBuilder openInlineTag(Tag tag) {
        return openInlineTag(tag, null);
    }
    
    /**
     * Writes an open tag for an inline element to the string builder.
     * @param tag the tag.
     * @param attributes the element's attributes.
     * @return this HTMLBuilder instance.
     */
    private HTMLBuilder openInlineTag(Tag tag, String attributes) {
        //buffer.append(' ');
        writeOpenTag(tag, attributes);
        return this;
    }
    
    /**
     * Writes an open tag for an element to the string builder.
     * @param tag the tag.
     * @return this HTMLBuilder instance.
     */
    HTMLBuilder openTag(Tag tag) {
        if (tag.isBlock() || tag.breaksFlow()) {
            return openBlockTag(tag);
        } else {
            return openInlineTag(tag);
        }
    }
    
    /**
     * Writes an open tag for an element to the string builder.
     * @param tag the tag.
     * @param attributes the element's attributes.
     * @return this HTMLBuilder instance.
     */
    HTMLBuilder openTag(Tag tag, String attributes) {
        if (tag.isBlock() || tag.breaksFlow()) {
            return openBlockTag(tag, attributes);
        } else {
            return openInlineTag(tag, attributes);
        }
    }
    
    /**
     * Writes an open tag for an element to the string builder.
     * @param tag the tag.
     * @param attributes the element's attributes.
     * @return this HTMLBuilder instance.
     */
    HTMLBuilder openTag(Tag tag, AttributeList attributes) {
        return openTag(tag, attributes.toHTML());
    }
    
    /**
     * Writes a close tag for an element to the string builder, without adjusting indentation or inserting newlines.
     * @param tag the tag.
     */
    private void writeCloseTag(Tag tag) {
        buffer.append("</").append(tag.toString()).append('>');
        
        tagStack.pop();
        if (tagStack.isEmpty()) {
            currentTag = null;
        } else {
            currentTag = tagStack.peek();
        }
    }
    
    /**
     * Writes a close tag for a block element to the string builder.
     * @param tag the tag.
     * @return this HTMLBuilder instance.
     */
    private HTMLBuilder closeBlockTag(Tag tag) {
        indentLevel -= INDENT;
        
        if (!tag.isPreformatted()) {
            if (justEmittedNewline) {
                int lenBeforePotentialIndent = buffer.length() - INDENT;
                if (lenBeforePotentialIndent >= 0 && buffer.substring(lenBeforePotentialIndent).equals(spaces(INDENT))) {
                    buffer.setLength(lenBeforePotentialIndent);
                }
            } else {
                newlineAndIndent();
            }
        }
        
        writeCloseTag(tag);
        newlineAndIndent();
        return this;
    }
    
    /**
     * Writes a close tag for an inline element to the string builder.
     * @param tag the tag.
     * @return this HTMLBuilder instance.
     */
    private HTMLBuilder closeInlineTag(Tag tag) {
        writeCloseTag(tag);
        //buffer.append(' ');
        return this;
    }
    
    /**
     * Writes a close tag for an element to the string builder.
     * @param tag the tag.
     * @return this HTMLBuilder instance.
     */
    HTMLBuilder closeTag(Tag tag) {
        if (tag.isBlock() || tag.breaksFlow()) {
            return closeBlockTag(tag);
        } else {
            return closeInlineTag(tag);
        }
    }
    
    /**
     * Writes a tag for an empty element to the string builder.
     * @param tag the tag.
     * @param attributes the element's attributes.
     * @return this HTMLBuilder instance.
     */
    HTMLBuilder emptyTag(Tag tag, String attributes) {
        if (tag.isBlock() || tag.breaksFlow()) {
            if (!justEmittedNewline) {
                newlineAndIndent();
            }
        }
        buffer.append('<').append(tag.toString());
        if (attributes != null && attributes.length() > 0) {
            buffer.append(' ').append(attributes);
        }
        buffer.append('>');
        return this;
    }
    
    /**
     * Writes a tag for an empty element to the string builder.
     * @param tag the tag.
     * @param attributes the element's attributes.
     * @return this HTMLBuilder instance.
     */
    HTMLBuilder emptyTag(Tag tag, AttributeList attributes) {
        return emptyTag(tag, attributes.toHTML());
    }
    
    /**
     * Writes a tag for an empty element to the string builder.
     * @param tag the tag.
     * @return this HTMLBuilder instance.
     */
    HTMLBuilder emptyTag(Tag tag) {
        return emptyTag(tag, (String)null);
    }
    
    /**
     * Writes the given text to the buffer, properly indented to the current indent level.
     * @param text the text to be written.
     * @return this HTMLBuilder instance.
     */
    private HTMLBuilder addIndentedText(String text) {
        addUnindentedText(text.replaceAll("\\n", "\n" + spaces(indentLevel)));
        return this;
    }
    
    /**
     * Adds a newline and a proper indent to the buffer.
     * @return this HTMLBuilder instance.
     */
    private HTMLBuilder newlineAndIndent() {
        newlineWithoutIndent();
        indent();
        return this;
    }
    
    /**
     * Writes the given text to the buffer, without adjusting for indentation.
     * @param text the text to be written.
     * @return this HTMLBuilder instance.
     */
    private HTMLBuilder addUnindentedText(String text) {
        buffer.append(text);
        if (text.length() > 0) {
            char lastChar = text.charAt(text.length() - 1);
            justEmittedNewline = (lastChar == '\n' || lastChar == '\r');
        }
        return this;
    }
    
    /**
     * Adds a newline but without adding an indent afterwards.
     * @return this HTMLBuilder instance.
     */
    private HTMLBuilder newlineWithoutIndent() {
        buffer.append('\n');
        justEmittedNewline = true;
        return this;
    }
    
    /**
     * Writes the given text to the buffer, which will be optionally indented
     * depending on whether the current context is to generate preformatted text (in which
     * whitespace is significant and cannot be adjusted) or not.
     * 
     * @param text the text to be written.
     * @return this HTMLBuilder instance.
     */
    HTMLBuilder addText(String text) {
        if (inPreformattedText()) {
            return addUnindentedText(text);
        } else {
            return addIndentedText(text);
        }
    }
    
    /**
     * Adds a newline to the buffer, which will be optionally followed by an indent
     * depending on whether the current context is to generate preformatted text (in which
     * whitespace is significant and cannot be adjusted) or not.
     * 
     * @return this HTMLBuilder instance.
     */
    HTMLBuilder newline() {
        if (inPreformattedText()) {
            return newlineWithoutIndent();
        } else {
            return newlineAndIndent();
        }
    }
    
    /**
     * Writes the given text wrapped by open and close tags to the buffer.
     * @param tag the tag.
     * @param text the text to be written.
     * @return this HTMLBuilder instance.
     */
    HTMLBuilder addTaggedText(Tag tag, String text) {
        if (tag.isBlock() && !justEmittedNewline) {
            newline();
        }
        openInlineTag(tag);
        addText(text);
        closeInlineTag(tag);
        if (tag.isBlock()) {
            newline();
        }
        return this;
    }
    
    /**
     * Writes the given text wrapped by open and close tags to the buffer.
     * @param tag the tag.
     * @param attributes the element's attributes.
     * @param text the text to be written.
     * @return this HTMLBuilder instance.
     */
    HTMLBuilder addTaggedText(Tag tag, String attributes, String text) {
        openInlineTag(tag, attributes);
        addText(text);
        closeInlineTag(tag);
        return this;
    }
    
    /**
     * Writes the given text wrapped by open and close tags to the buffer.
     * @param tag the tag.
     * @param attributes the element's attributes.
     * @param text the text to be written.
     * @return this HTMLBuilder instance.
     */
    HTMLBuilder addTaggedText(Tag tag, AttributeList attributes, String text) {
        return addTaggedText(tag, attributes.toHTML(), text);
    }
    
    /**
     * Adds an indent to the buffer.
     */
    private void indent() {
        for (int i = 0; i < indentLevel; i++) {
            buffer.append(' ');
        }
    }
    
    /**
     * Creates a string of spaces.
     * @param nSpaces the number of spaces in the generated string.
     * @return a string of spaces.
     */
    private String spaces(int nSpaces) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < nSpaces; i++) {
            buf.append(' ');
        }
        return buf.toString();
    }
    
    /**
     * @return the string representation of the HTML.
     */
    String toHTML() {
        return buffer.toString();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return toHTML();
    }
}