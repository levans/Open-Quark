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
 * AdjunctSource.java
 * Creation date: Jan 26, 2005.
 * By: Joseph Wong
 */
package org.openquark.cal.compiler;

import java.io.Reader;
import java.io.StringReader;

import org.openquark.cal.compiler.SourceModel.TopLevelSourceElement;

/**
 * Abstracts the source of a CAL adjunct. This abstraction allows the CAL
 * compiler and typechecker to accept adjuncts in different representations
 * (e.g. text and source model), and to process them accordingly.
 * 
 * @author Joseph Wong
 */
public abstract class AdjunctSource {

    /**
     * Private constructor for AdjunctSource. This abstract class is not meant
     * to be subclassed outside of this package.
     */
    private AdjunctSource() {
    }
    
    /**
     * Concatenates two adjuncts to form a combined adjunct containing all the
     * elements from both.
     * 
     * @param other
     *            the other piece of adjunct to be concatenated with this
     *            adjunct
     * @return the concatenation of this adjunct and the specified adjunct
     */
    abstract AdjunctSource concat(AdjunctSource other);
    
    /**
     * Converts the adjunct into its source text representation.
     * 
     * @return the source text representation of this adjunct
     */
    @Override
    public abstract String toString(); 
            
    /**
     * Encapsulates the definition of an adjunct in source model form.
     * 
     * @author Joseph Wong
     */
    public static class FromSourceModel extends AdjunctSource {
        /**
         * The definition of the adjunct, as a list of
         * <code>TopLevelSourceElement</code>s.
         */
        private final SourceModel.TopLevelSourceElement[] elements;

        /**
         * Constructs an adjunct from a single source element.
         * 
         * @param element
         *            the sole source element of the adjunct
         */
        public FromSourceModel(SourceModel.TopLevelSourceElement element) {
            SourceModel.verifyArg(element, "element");
            
            this.elements = new SourceModel.TopLevelSourceElement[] { element };
        }

        /**
         * Constructs an adjunct from an array of source elements.
         * 
         * @param elements
         *            the source elements of the adjunct
         */
        public FromSourceModel(SourceModel.TopLevelSourceElement[] elements) {
            SourceModel.verifyArrayArg(elements, "elements");
            
            this.elements = elements;
        }

        /**
         * Concatenates two adjuncts to form a combined adjunct containing all
         * the elements from both.
         * 
         * @param other
         *            the other piece of adjunct to be concatenated with this
         *            adjunct
         * @return the concatenation of this adjunct and the specified adjunct
         */
        @Override
        AdjunctSource concat(AdjunctSource other) {
            SourceModel.verifyArg(other, "other");
            
            if (other instanceof FromSourceModel) {
                
                // create a new source-model-based adjunct with the source model
                // elements from both this adjunct and the other adjunct
                
                FromSourceModel smOther = (FromSourceModel)other;
                int newLength = elements.length + smOther.elements.length;
                
                SourceModel.TopLevelSourceElement[] newElements = new SourceModel.TopLevelSourceElement[newLength];
                
                System.arraycopy(elements, 0, newElements, 0, elements.length);
                System.arraycopy(smOther.elements, 0, newElements, elements.length, smOther.elements.length);
                
                return new FromSourceModel(newElements);
                
            } else if (other instanceof FromText) {
                
                // since the other adjunct is text-based, create a new
                // text-based adjunct with the combined source text
                
                FromText textOther = (FromText)other;
                
                StringBuilder sb = new StringBuilder();
                
                for (final TopLevelSourceElement element : elements) {
                    sb.append(element.toSourceText()).append('\n');
                }
                
                sb.append(textOther.source);
                
                return new FromText(sb.toString());
                
            } else {
                throw new UnsupportedOperationException(
                    "AdjunctSource.FromSourceModel.concat - argument type " + other.getClass() + "not supported.");
            }
        }

        /**
         * Converts the adjunct into its source text representation.
         * 
         * @return the source text representation of this adjunct
         */
        @Override
        public String toString() {
            
            StringBuilder sb = new StringBuilder();
            for (final TopLevelSourceElement element : elements) {
                element.toSourceText(sb);
                sb.append('\n');
            }
            return sb.toString();
        }

        /**
         * Converts the adjunct into the corresponding parse tree.
         * 
         * @return the parse tree node for the adjunct
         */
        ParseTreeNode toParseTreeNode() {

            ParseTreeNode outerDefnListNode = new ParseTreeNode(
                CALTreeParserTokenTypes.OUTER_DEFN_LIST, "OUTER_DEFN_LIST");

            int numChildren = elements.length;
            ParseTreeNode[] outerDefnNodes = new ParseTreeNode[numChildren];

            for (int i = 0; i < numChildren; i++) {
                outerDefnNodes[i] = elements[i].toParseTreeNode();
            }
            outerDefnListNode.addChildren(outerDefnNodes);

            return outerDefnListNode;
        }
        
        /**
         * @return the number of elements in this adjunct
         */
        public int getNElements() {
            return elements.length;
        }

        /**
         * @param i
         *            the index for the desired element
         * @return the <code>i</code>th element of this adjunct
         */
        public SourceModel.TopLevelSourceElement getElement(int i) {
            return elements[i];
        }
    }

    /**
     * Encapsulates the definition of an adjunct contained in text.
     * 
     * @author Joseph Wong
     */
    public static class FromText extends AdjunctSource {
        /**
         * The source text of the adjunct.
         */
        private final String source;

        /**
         * Constructs an adjunct from its CAL source text.
         * 
         * @param source
         *            the source of the adjunct
         */
        public FromText(String source) {
            SourceModel.verifyArg(source, "source");
            
            this.source = source;
        }
        
        /**
         * Concatenates two adjuncts to form a combined adjunct containing all
         * the elements from both.
         * 
         * @param other
         *            the other piece of adjunct to be concatenated with this
         *            adjunct
         * @return the concatenation of this adjunct and the specified adjunct
         */
        @Override
        AdjunctSource concat(AdjunctSource other) {
            SourceModel.verifyArg(other, "other");
            
            if (other instanceof FromSourceModel) {
                
                FromSourceModel smOther = (FromSourceModel)other;
                
                StringBuilder sb = new StringBuilder(source).append('\n');
                
                // since the other adjunct is in source model form, convert it to
                // its CAL text representation for the combined text-based adjunct
                for (final TopLevelSourceElement element : smOther.elements) {
                    sb.append(element.toSourceText()).append('\n');
                }
                
                return new FromText(sb.toString());
                
            } else if (other instanceof FromText) {
                
                FromText textOther = (FromText)other;
                
                StringBuilder sb = new StringBuilder(source).append('\n');
                
                sb.append(textOther.source);
                
                return new FromText(sb.toString());
                
            } else {
                throw new UnsupportedOperationException(
                    "AdjunctSource.FromSourceModel.concat - argument type " + other.getClass() + "not supported.");
            }            
        }

        /**
         * Converts the adjunct into its source text representation.
         * 
         * @return the source text representation of this adjunct
         */
        @Override
        public String toString() {
            return source;
        }

        /**
         * Obtains a new Reader for the source text.
         * 
         * @return the reader
         */
        Reader getReader() {
            return new StringReader(source);
        }
    }
}