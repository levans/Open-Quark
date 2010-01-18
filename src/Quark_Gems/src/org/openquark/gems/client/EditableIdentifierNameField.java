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
 * EditableIdentifierNameField.java
 * Creation date: (10/29/01 4:04:00 PM)
 * By: Edward Lam
 */
package org.openquark.gems.client;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import org.openquark.cal.compiler.LanguageInfo;


/**
 * An editable text field that accepts valid CAL identifiers
 * Creation date: (10/29/01 4:04:00 PM)
 * @author Edward Lam
 */
public abstract class EditableIdentifierNameField extends JTextField implements java.awt.event.ActionListener{

    /** The name this field starts out with*/
    private String initialText;

    /**
     * Default constructor for this field
     * Creation date: (10/29/01 6:49:22 PM)
     */
    EditableIdentifierNameField() {
        super();

        // Set a thin border to prevent text from disappearing under it.
        setBorder(javax.swing.BorderFactory.createEmptyBorder(1,1,1,1));
                        
        // notify of any commit actions on the part of the user
        addActionListener(this);
    }
    
    /**
     * Sets the initial text
     * @param initialText
     */
    protected void setInitialText(String initialText) {
        this.initialText = initialText;
    }
    
    /**
     * @return the initial text
     */
    protected String getInitialText() {
        return initialText;
    }
        
    /**
     * Sets maximum length restriction of this document (currently 25 characters)
     * if not called there is no max restriction
     * @param limit
     */
    protected void setMaxLength (int limit) { 
        ((LetterNumberUnderscoreDocument)getDocument()).setMaxLength(limit);
    }
    
    /**
     * Invoked when an action occurs.
     * Creation date: (10/30/01 12:15:22 PM)
     * @param e java.awt.event.ActionEvent
     */
    public void actionPerformed(java.awt.event.ActionEvent e){
        // just commit the text
        commitText();
    }
    
    /**
     * Cancel text entry (press "ESC" ..)
     * Creation date: (10/31/01 3:31:22 PM)
     */
    protected void cancelEntry(){
        // What to do, what to do..
        textCommittedInvalid();
    }
    
    /**
     * Commit the text currently in the text field
     * Creation date: (10/31/01 2:32:22 PM)
     */
    protected void commitText(){
        String newText = getText();

        // the action we take depends on whether the new name is valid or invalid
        if (isValidName(newText) && (!newText.equals(initialText) )) {
            // The new name checks out.  Take appropriate action
            textCommittedValid();
        } else {
            // Invalid.  No good.  Bad.
            cancelEntry();
        }
    }

    /**
     * Creates the default implementation of the model to be used at construction if one isn't explicitly given.
     * Overriden to return a LetterNumberUnderscoreDocument.
     * Creation date: (10/29/01 6:50:22 PM)
     * @return Document the default model implementation. 
     */
    @Override
    protected Document createDefaultModel() {
        return new LetterNumberUnderscoreDocument();
    }

    /**
     * Returns whether a name is a valid name for this field
     * Creation date: (10/30/01 12:23:22 PM)
     * @param letName String the name to check for validity 
     */
    protected abstract boolean isValidName(String letName);

    /**
     * Notify that the text of this text field has changed.  Called upon insertUpdate() and remove() completion.
     * Eg. If the current result is not valid, maybe do something about it (like warn the user somehow..)
     * Creation date: (10/30/01 11:43:22 AM)
     */
    protected void textChanged(){
        String updatedText = getText();

        if (isValidName(updatedText)) {
            textChangeValid();
        } else {
            textChangeInvalid();
        }
    }
    
    /**
     * Take appropriate action if the result of the text change is invalid.
     * Creation date: (10/30/01 2:54:22 AM)
     */
    protected abstract void textChangeInvalid();
    
    /**
     * Take appropriate action if the result of the text change is valid.
     * Creation date: (10/30/01 2:54:22 AM)
     */
    protected abstract void textChangeValid();
    
    /**
     * Take appropriate action if the text committed is invalid.
     * Creation date: (10/30/01 2:54:22 AM)
     */
    protected abstract void textCommittedInvalid();
    
    /**
     * Take appropriate action if the text committed is valid.
     * Creation date: (10/30/01 3:28:22 AM)
     */
    protected abstract void textCommittedValid();

    /**
     * An editable text field that accepts valid CAL constructor names
     * Creation date: (10/29/01 4:04:00 PM)
     * @author Edward Lam
     */
    public abstract static class ConstructorName extends EditableIdentifierNameField{
        /**
         * Returns whether a name is a valid name for this field
         * Creation date: (11/01/01 2:44:22 PM)
         * @param name String the name to check for validity 
         */
        @Override
        protected boolean isValidName(String name){
            return LanguageInfo.isValidDataConstructorName(name);            
        }        
    }
    
    /**
     * An editable text field that accepts valid CAL variable names
     * Creation date: (10/29/01 4:04:00 PM)
     * @author Edward Lam
     */
    public abstract static class VariableName extends EditableIdentifierNameField{
        /**
         * Returns whether a name is a valid name for this field
         * Creation date: (10/30/01 12:23:22 PM)
         * @param name String the name to check for validity 
         */
        @Override
        protected boolean isValidName(String name){
            return LanguageInfo.isValidFunctionName(name);
        }        
    }
    
    /**
     * The Document is a container for text that serves as the model for swing text components.
     * A warning signal is shown if the present text is not a valid identifier name.
     * Creation date: (10/29/01 6:53:22 PM)
     * @author Edward Lam
     */
    private abstract class RestrictedTextDocument extends PlainDocument {
        
        /** whether the maximum length is in effect */
        private boolean maxLengthInEffect = false;
        
        private int maxLength = 0;
        
        /**
         * Inserts a string of content. 
         * Creation date: (10/29/01 6:55:22 PM)
         * @param offs int the offset into the document to insert the content >= 0. 
         * All positions that track change at or after the given location will move.
         * @param str String the string to insert
         * @param a AttributeSet the attributes to associate with the inserted content. 
         * This may be null if there are no attributes.
         */
        @Override
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            char[] source = str.toCharArray();
            char[] result = new char[source.length];
            int j = 0;
        
            // only letters, digits and underscores are allowed in identifiers    
            for (int i = 0; i < result.length; i++) {
                if (characterValid(source[i])) {
                    result[j++] = source[i];
                }
            }
            
            if (!maxLengthInEffect || (getLength() + result.length) <= maxLength ) {
                super.insertString(offs, new String(result, 0, j), a);
            }
        
            // perform bookkeeping and other duties
            textChanged();
        }
        
        /**
         * Checks whether a given character is allowed in the document
         * @param c character to check for validity
         * @return true if character is valid, false otherwise
         */
        public abstract boolean characterValid(char c);
        
        /**
         * Removes a portion of the content of the document.
         * Creation date: (10/29/01 6:58:22 PM)
         * @param offs int the offset from the begining >= 0
         * @param len int the number of characters to remove >= 0
         */
        @Override
        public void remove(int offs, int len) throws BadLocationException {
            super.remove(offs, len);
        
            // perform bookkeeping and other duties
            textChanged();
        }
        
        /**
         * enables or disables the maximum length restriction of this document (currently 25 characters)
         * @param limit
         */
        void setMaxLength (int limit) { 
            maxLengthInEffect = true;
            this.maxLength = limit;
        }
    }
    
    /**
     * Document class that accepts letters, numbers and the underscore character as
     * valid characters.
     * @author Neil Corkum
     */
    protected class LetterNumberUnderscoreDocument extends RestrictedTextDocument {
        private static final long serialVersionUID = -814558192378393767L;

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean characterValid(char c) {
            return (Character.isLetterOrDigit(c) || c == '_');
        }
    }
    
    /**
     * Document class that accepts letters, numbers, the underscore character and the 
     * pound (#) character as valid characters.
     * @author Neil Corkum
     */
    protected class LetterNumberUnderscorePoundDocument extends RestrictedTextDocument {
        private static final long serialVersionUID = -1094709578204461360L;

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean characterValid(char c) {
            return (Character.isLetterOrDigit(c) || c == '_' || c == '#');
        }
    }
}
