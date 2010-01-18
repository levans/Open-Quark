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
 * SourceModification.java
 * Creation date: (Feb 17, 2006)
 * By: James Wright
 */
package org.openquark.cal.compiler;

import java.util.Comparator;

/**
 * A SourceModification models a single modification to a single source file.
 * Includes a source position, old text, and new text.
 *
 * This class is a generalization of the RenameData class, which it replaces.
 * 
 * @author James Wright
 * @author Bo Ilic
 */
public abstract class SourceModification {

    /** Position to perform replacement at */
    private final SourcePosition sourcePosition;

    /**
     * The corresponding position of sourcePosition in the modified file
     */
    private SourcePosition newSourcePosition = null;
    
    /** Maximum size of the summary snippet to print in toString methods */ 
    private static final int MAX_SNIPPET_LENGTH = 32;
    
    /**
     * A SourceModification that replaces text at a specific location
     * with other text (not necessarily of the same length).  
     * 
     * @author James Wright
     */
    static final class ReplaceText extends SourceModification {
        
        /** Text to be replaced at sourcePosition */
        private final String oldText;
        
        /** Text to replace oldText with at sourcePosition */
        private final String newText;
        
        ReplaceText(String oldText, String newText, SourcePosition sourcePosition) {
            super(sourcePosition);
            if(oldText == null || newText == null) {
                throw new NullPointerException();
            }
            this.oldText = oldText;
            this.newText = newText;
        }
        
        /** {@inheritDoc} */
        @Override
        SourceModification getInverse(SourcePosition sourcePosition) {
            return new ReplaceText(getNewText(), getOldText(), sourcePosition);
        }
        
        /** {@inheritDoc} */
        @Override
        public String toString() {
            StringBuilder outputBuffer = new StringBuilder();
            String oldSnippet = getOldText().length() > MAX_SNIPPET_LENGTH ?
                    getOldText().substring(0, MAX_SNIPPET_LENGTH - 3) + "..." :
                    getOldText();
            String newSnippet = getNewText().length() > MAX_SNIPPET_LENGTH ?
                    getNewText().substring(0, MAX_SNIPPET_LENGTH - 3) + "..." :
                    getNewText();
            
            outputBuffer.append("replace '");
            outputBuffer.append(oldSnippet);
            outputBuffer.append("' with '");
            outputBuffer.append(newSnippet);
            outputBuffer.append("' at (");
            outputBuffer.append(getSourcePosition().getLine());
            outputBuffer.append(", ");
            outputBuffer.append(getSourcePosition().getColumn());
            outputBuffer.append(')');
            
            return outputBuffer.toString();
        }
    
        /** {@inheritDoc} */
        @Override
        String getOldText() {
            return oldText;
        }
        
        /** {@inheritDoc} */
        @Override
        String getNewText() {
            return newText;
        }
    }
    
    /**
     * A SourceModification that is used to keep track of
     * a cursor position.
     * 
     * @author Greg McClement
     */
    static final class CursorPosition extends SourceModification {
        CursorPosition(SourcePosition sourcePosition) {
            super(sourcePosition);
        }
        
        /** {@inheritDoc} */
        @Override
        SourceModification getInverse(SourcePosition sourcePosition) {
            return new CursorPosition(sourcePosition);
        }
        
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "";
        }
    
        /** {@inheritDoc} */
        @Override
        String getOldText() {
            return "";
        }
        
        /** {@inheritDoc} */
        @Override
        String getNewText() {
            return "";
        }
    }
    
    /**
     * A SourceModification that removes specific text at a specific location.
     * 
     * @author James Wright
     */
    static final class RemoveText extends SourceModification {
        
        /** Text to be removed at sourcePosition */
        private final String oldText;
        
        RemoveText(String oldText, SourcePosition sourcePosition) {
            super(sourcePosition);
            if(oldText == null) {
                throw new NullPointerException();
            }
            this.oldText = oldText;
        }
        
        /** {@inheritDoc} */
        @Override
        SourceModification getInverse(SourcePosition sourcePosition) {
            return new InsertText(getOldText(), sourcePosition);
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            StringBuilder outputBuffer = new StringBuilder();
            String removalSnippet = getOldText().length() > MAX_SNIPPET_LENGTH ?
                    getOldText().substring(0, MAX_SNIPPET_LENGTH - 3) + "..." :
                    getOldText();
            
            outputBuffer.append("remove '");
            outputBuffer.append(removalSnippet);
            outputBuffer.append("' at (");
            outputBuffer.append(getSourcePosition().getLine());
            outputBuffer.append(", ");
            outputBuffer.append(getSourcePosition().getColumn());
            outputBuffer.append(')');
            
            return outputBuffer.toString();
        }

        /** {@inheritDoc} */
        @Override
        String getOldText() {
            return oldText;
        }
        
        /** {@inheritDoc} */
        @Override
        String getNewText() {
            return "";
        }
    }
    
    /**
     * A SourceModification that inserts some text at a specific location.
     * 
     * @author James Wright
     */
    static final class InsertText extends SourceModification {
        
        /** Text to be inserted at sourcePosition */
        private final String newText;

        InsertText(String newText, SourcePosition sourcePosition) {
            super(sourcePosition);
            if(newText == null) {
                throw new NullPointerException();
            }
            this.newText = newText;
        }
        
        /** {@inheritDoc} */
        @Override
        SourceModification getInverse(SourcePosition sourcePosition) {
            return new RemoveText(getNewText(), sourcePosition);
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            StringBuilder outputBuffer = new StringBuilder();
            String importSnippet = getNewText().length() > MAX_SNIPPET_LENGTH ?
                    getNewText().substring(0, MAX_SNIPPET_LENGTH - 3) + "..." :
                    getNewText();
            
            outputBuffer.append("insert '");
            outputBuffer.append(importSnippet);
            outputBuffer.append("' at (");
            outputBuffer.append(getSourcePosition().getLine());
            outputBuffer.append(", ");
            outputBuffer.append(getSourcePosition().getColumn());
            outputBuffer.append(')');
            
            return outputBuffer.toString();
        }

        /** {@inheritDoc} */
        @Override
        String getOldText() {
            return "";
        }
        
        /** {@inheritDoc} */
        @Override
        String getNewText() {
            return newText;
        }
    }
    
    /** Constructor for use by child classes */
    private SourceModification(SourcePosition sourcePosition) {
        if(sourcePosition == null) {
            throw new NullPointerException();
        }
        this.sourcePosition = sourcePosition;
    }
    
    /**
     * @return Text that will be removed or replaced by this modification
     */
    abstract String getOldText();
    
    /**
     * @return Text that will be inserted by this modification
     */
    abstract String getNewText();
    
    /** @return SourcePosition in the old source text to perform the modification at */ 
    SourcePosition getSourcePosition() {
        return sourcePosition;
    }

    void setNewSourcePosition(SourcePosition newSourcePosition){
        this.newSourcePosition = newSourcePosition;
    }
    
    /**
     * @return the corresponding position of the original source position in the modified file. This
     * is used for example to update old cursor positions to new cursor positions.
     */
    SourcePosition getNewSourcePosition(){
        return newSourcePosition;
    }
    
    
    /**
     * Returns a new SourceModification that performs the opposite of this modification at the 
     * specified SourcePosition.
     * @param positionInModifiedText SourcePosition to perform the inverse modification at
     */
    abstract SourceModification getInverse(SourcePosition positionInModifiedText);    
    
    /**
     * Comparator object to order SourceModification objects by increasing 
     * source position of the modification.
     */
    public static final Comparator<SourceModification> compareByPosition = new CompareByPosition();
    private static class CompareByPosition implements Comparator<SourceModification> {
        /** {@inheritDoc} */
        public int compare(SourceModification o1, SourceModification o2) {
            if ((o1 == null) || (o2 == null)) {
                throw new NullPointerException();
            }
            final int compare = SourcePosition.compareByPosition.compare(
                o1.getSourcePosition(),
                o2.getSourcePosition());
            
            if (compare != 0){
            	return compare;
            }

            // if the position matches then cursor position should sort 
            // last so they pick up the prior modifications
            if (o1 instanceof CursorPosition){
            	if (!(o2 instanceof CursorPosition)){
            		return 1;
            	}
            }
            else{
            	if (o2 instanceof CursorPosition){
            		return -1;
            	}
            }
            
            return compare;
        }
    }
}
