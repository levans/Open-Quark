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
 * CSSBuilder.java
 * Creation date: Oct 13, 2005.
 * By: Joseph Wong
 */
package org.openquark.cal.caldoc;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.text.html.CSS;
import javax.swing.text.html.HTML;

/**
 * This class implements a rudimentary builder for constructing Cascading Style Sheets (CSS).
 * It is based on a string builder, and does not build up a DOM for the entire document. Rule sets
 * however are represented by a simple structured intermediate representation before being appended
 * into the buffer.
 *
 * @author Joseph Wong
 */
final class CSSBuilder {
    
    /** The string builder holding the CSS being built. */
    private final StringBuilder buffer = new StringBuilder();
    
    /**
     * Represents the notion of a <i>selector</i> in CSS.
     *
     * @author Joseph Wong
     */
    final static class Selector {
        
        /** The CSS representation of the selector. */
        private final String cssRep;
        
        /**
         * Private constructor.
         * Factory methods should be used instead for constructing instances.
         * 
         * @param cssRep the CSS representation of the selector.
         */
        private Selector(String cssRep) {
            this.cssRep = cssRep;
        }
        
        /**
         * Creates a generic selector based on the CSS representation.
         * @param cssRep the CSS representation of the selector.
         * @return the corresponding selector.
         */
        static Selector make(String cssRep) {
            return new Selector(cssRep);
        }
        
        /**
         * Creates a type selector based on an HTML tag.
         * @param tag the HTML tag.
         * @return the corresponding type selector.
         */
        static Selector makeType(HTML.Tag tag) {
            return new Selector(tag.toString());
        }
        
        /**
         * Creates a type selector based on an ID.
         * @param id the ID.
         * @return the corresponding ID selector.
         */
        static Selector makeID(String id) {
            return new Selector("#" + id);
        }
        
        /**
         * Creates a class selector based on an HTML style class.
         * @param styleClass the HTML style class.
         * @return the corresponding class selector.
         */
        static Selector makeClass(StyleClass styleClass) {
            return new Selector(styleClass.toCSS());
        }
        
        /**
         * Creates a class selector based on a selector and an HTML style class.
         * @param selector the base selector.
         * @param styleClass the HTML style class.
         * @return the corresponding class selector.
         */
        static Selector makeClass(Selector selector, StyleClass styleClass) {
            return new Selector(selector.cssRep + styleClass.toCSS());
        }
        
        /**
         * Creates a pseudo-class selector.
         * @param selector the base selector.
         * @param pseudoClass the name of the pseudo-class.
         * @return the corresponding pseudo-class selector.
         */
        static Selector makePseudoClass(Selector selector, String pseudoClass) {
            return new Selector(selector.cssRep + ":" + pseudoClass);
        }
        
        /**
         * Creates a descendant selector with two selectors.
         * @param outer the outer selector.
         * @param inner the inner selector.
         * @return the corresponding descendent selector.
         */
        static Selector makeDescendant(Selector outer, Selector inner) {
            return new Selector(outer.cssRep + " " + inner.cssRep);
        }
        
        /**
         * Creates a descendant selector with three selectors.
         * @param outer the outer selector.
         * @param middle the middle selector.
         * @param inner the inner selector.
         * @return the corresponding descendent selector.
         */
        static Selector makeDescendant(Selector outer, Selector middle, Selector inner) {
            return new Selector(outer.cssRep + " " + middle.cssRep + " " + inner.cssRep);
        }
        
        /** @return the CSS representation of the selector. */
        String toCSS() {
            return cssRep;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return cssRep;
        }
    }

    /**
     * Represents the notion of a <i>rule set</i> in CSS.
     *
     * @author Joseph Wong
     */
    final static class RuleSet {
        
        /** The number of spaces to use for indentation. */
        private static final String INDENT_SPACES = "   ";
        
        /** The list of selectors associated with this rule set. */
        private final List<Selector> selectors = new ArrayList<Selector>();
        
        /** The insertion-ordered map of attributes to values for this rule set. */
        private final LinkedHashMap<String, String> attrValues = new LinkedHashMap<String, String>();
        
        /**
         * Constructs a rule set based on one selector.
         * @param selector the selector.
         */
        RuleSet(Selector selector) {
            if (selector == null) {
                throw new NullPointerException();
            }
            
            selectors.add(selector);
        }
        
        /**
         * Constructs a rule set based on two selectors.
         * @param selector1 the first selector.
         * @param selector2 the second selector.
         */
        RuleSet(Selector selector1, Selector selector2) {
            if (selector1 == null || selector2 == null) {
                throw new NullPointerException();
            }
            
            selectors.add(selector1);
            selectors.add(selector2);
        }
        
        /**
         * Constructs a rule set based on three selectors.
         * @param selector1 the first selector.
         * @param selector2 the second selector.
         * @param selector3 the third selector.
         */
        RuleSet(Selector selector1, Selector selector2, Selector selector3) {
            if (selector1 == null || selector2 == null || selector3 == null) {
                throw new NullPointerException();
            }
            
            selectors.add(selector1);
            selectors.add(selector2);
            selectors.add(selector3);
        }
        
        /**
         * Constructs a rule set based on a non-empty array of selectors.
         * @param selectorArray a non-empty array of selectors.
         */
        RuleSet(Selector[] selectorArray) {
            if (selectorArray == null) {
                throw new NullPointerException();
            }
            
            if (selectorArray.length == 0) {
                throw new IllegalArgumentException();
            }
            
            for (final Selector selector : selectorArray) {
                if (selector == null) {
                    throw new NullPointerException();
                }
                
                selectors.add(selector);
            }
        }
        
        /**
         * Adds an attribute-value pair to this rule set.
         * @param attribute the attribute.
         * @param value the associated value.
         */
        RuleSet addAttribute(String attribute, String value) {
            if (attribute == null || value == null) {
                throw new NullPointerException();
            }
            
            attrValues.put(attribute, value);
            
            return this;
        }
        
        /**
         * Adds an attribute-value pair to this rule set.
         * @param attribute the attribute.
         * @param value the associated value.
         */
        RuleSet addAttribute(CSS.Attribute attribute, String value) {
            if (attribute == null || value == null) {
                throw new NullPointerException();
            }
            
            addAttribute(attribute.toString(), value);
            
            return this;
        }
        
        /** @return the string representation of this rule set. */
        String toCSS() {
            StringBuilder buffer = new StringBuilder();
            
            for (int i = 0, n = selectors.size(); i < n; i++) {
                if (i > 0) {
                    buffer.append(", ");
                }
                buffer.append(selectors.get(i).toCSS());
            }
            
            buffer.append(" {\n");
            for (final Map.Entry<String, String> entry : attrValues.entrySet()) {
                buffer.append(INDENT_SPACES).append(entry.getKey()).append(": ").append(entry.getValue()).append(";\n");
            }
            buffer.append("}\n");
            
            return buffer.toString();
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return toCSS();
        }
    }

    /** @return the string representation of the CSS. */
    String toCSS() {
        return buffer.toString();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return toCSS();
    }
    
    /**
     * Appends a rule set to the end of the buffer.
     * @param ruleSet the rule set.
     * @return this CSSBuilder instance.
     */
    CSSBuilder addRuleSet(RuleSet ruleSet) {
        buffer.append(ruleSet.toCSS()).append('\n');
        return this;
    }
}
