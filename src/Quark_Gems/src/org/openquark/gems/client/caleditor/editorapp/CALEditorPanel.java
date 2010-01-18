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
 * CALEditorPanel.java
 * Creation date: (5/4/01 12:36:47 PM)
 * By: Luke Evans
 */
package org.openquark.gems.client.caleditor.editorapp;

import org.openquark.gems.client.caleditor.CALEditor;
import org.openquark.gems.client.caleditor.CALSyntaxStyleListener;

/**
 * The Editor Panel itself.
 * Creation date: (5/4/01 12:36:47 PM)
 * @author Luke Evans
 */
public class CALEditorPanel extends CALEditor implements CALSyntaxStyleListener, javax.swing.event.DocumentListener {
    
    private static final long serialVersionUID = 2910611455926901591L;
    private CALIDE calIDE = null;
    
    /**
     * CALEditorPanel constructor comment.
     */
    public CALEditorPanel() {
        super();

        // Set this as the document listener and style listener
        getDocument().addDocumentListener(this);
        // Make us a style listener (there shouldn't be any others as we own the editor pane!
        try {
            addCALSyntaxStyleListener(this);
        } catch (java.util.TooManyListenersException e) {
        }
    }
    
    /**
     * Gives notification that an attribute or set of attributes changed.
     *
     * @param e the document event
     */
    public void changedUpdate(javax.swing.event.DocumentEvent e) {
    }
    
    /**
     * Indicate that the document has changed.
     * Creation date: (5/7/01 7:38:54 AM)
     */
    private void docChanged() {
        // Call the CALIDE docChanged() method
        calIDE.docChanged();
    }
    
    /**
     * Lookup the font for a given scanCode.
     * Creation date: (5/4/01 1:21:58 PM)
     * @return java.awt.Font the font to apply
     * @param scanCode int the scan code (token type)
     * @param image String the token image
     * @param suggestedFont java.awt.Font the suggested font (default or user set) or null
     */
    public java.awt.Font fontLookup(int scanCode, java.lang.String image, java.awt.Font suggestedFont) {
        return suggestedFont;
    }
    
    /**
     * Lookup the foreground colour for a given scanCode.
     * Creation date: (5/4/01 1:21:58 PM)
     * @return Style the font to apply
     * @param scanCode int the scan code (token type)
     * @param image String the token image
     * @param suggestedColour java.awt.Color the suggested colour (default or user set) or null
     */
    public java.awt.Color foreColourLookup(int scanCode, java.lang.String image, java.awt.Color suggestedColour) {
        return suggestedColour;
    }
    
    /**
     * Get the editor content.
     * Creation date: (5/4/01 5:11:24 PM)
     * @return java.lang.String the content (CAL source hopefully!)
     */
    public String getContent() {
        return getText();
    }
    
    /**
     * Gives notification that there was an insert into the document.  The 
     * range given by the DocumentEvent bounds the freshly inserted region.
     *
     * @param e the document event
     */
    public void insertUpdate(javax.swing.event.DocumentEvent e) {
        docChanged();
    }
    
    /**
     * Gives notification that a portion of the document has been 
     * removed.  The range is given in terms of what the view last
     * saw (that is, before updating sticky positions).
     *
     * @param e the document event
     */
    public void removeUpdate(javax.swing.event.DocumentEvent e) {
        docChanged();
    }
    
    /**
     * Set the CALIDE class.
     * Creation date: (5/7/01 7:36:57 AM)
     * @param calIDE org.openquark.gems.client.caleditor.editorapp.CALIDE
     */
    public void setCALIDE(CALIDE calIDE) {
        this.calIDE = calIDE;
    }
    
    /**
     * Set the editor content.
     * Creation date: (5/4/01 5:11:24 PM)
     * @param content java.lang.String the content (CAL source hopefully!)
     */
    public void setContent(String content) {
        setText(content);
    }
    
    /**
     * Lookup the style for a given scanCode.
     * Creation date: (5/4/01 1:21:58 PM)
     * @return javax.swing.text.Style the style to apply
     * @param scanCode int the scan code (token type)
     * @param image String the token image 
     * @param suggestedStyle java.awt.Color the suggested style (default or user set) or null
     */
    public javax.swing.text.Style styleLookup(int scanCode, String image, javax.swing.text.Style suggestedStyle) {
        return suggestedStyle;
    }
}
