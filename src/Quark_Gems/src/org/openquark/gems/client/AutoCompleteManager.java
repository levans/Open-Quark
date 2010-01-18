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
 * AutoCompleteManager.java
 * Creation date: Dec 10th 2002
 * By: Ken Wong
 */
package org.openquark.gems.client;

import java.awt.Point;

import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.JTextComponent;

import org.openquark.cal.services.Perspective;
import org.openquark.gems.client.IntellicutListModelAdapter.IntellicutListEntry;

/**
 * This class handles the instance(s) of AutoCompletePopupMenu that are used for the GemCutter
 * (Currently used in the CodeGemEditor)
 * @author KCWong
 * Creation Date: Dec 3rd 2002
 */
public class AutoCompleteManager {
    
    /**
     * Interface implemented for editors using auto-completion.
     * @author Iulian Radu
     */
    public interface AutoCompleteEditor {
        
        /**
         * Completion was selected and can be inserted into the editor component.
         * 
         * @param backtrackLength number of characters to backtack from the current caret position
         * @param insertString auto-complete string to insert (qualified name if entity is ambiguous, unqualified otherwise)
         */
        public void insertAutoCompleteString(int backtrackLength, String insertString);
        
        /**
         * @return text component where auto-completion popup appears 
         */
        public JTextComponent getEditorComponent();
    }
    
    /** The shared instance of the AutoCompletePopupMenu used for the CodeGemEditor */
    private final AutoCompletePopupMenu autoCompletePopup;

    /** The perspective to get gems from. */
    private final Perspective perspective;
    
    /** the code editor */
    private final AutoCompleteEditor editor;

    /**
     * Default Constructor for the AutoCompleteManager
     * @param editor
     * @param perspective
     */
    public AutoCompleteManager (AutoCompleteEditor editor, Perspective perspective) {
        this.editor = editor;
        this.perspective = perspective;
        
        // create the one shared instance of the AutoCompletePopupMenu for the CodeGemEditors
        autoCompletePopup = new AutoCompletePopupMenu(this);
    }

    /**
     * Completes the steps necessary to properly close the CodeGemPanel autocomplete popup
     */
    void closeAutoCompletePopup() {
        // when the panel is closed, then we want to complete the proper user actions
        IntellicutListEntry selected = autoCompletePopup.getSelected();
        JTextComponent calEditor = editor.getEditorComponent();
        
        // Close before doing anything else, so that the ui doesn't try to update the popup appearance for any string insertion.
        autoCompletePopup.setVisible(false);           
        
        // if the user commited, then 'selected' is not null
        
        if (selected != null) {
            String insertion = selected.getSourceText();
            int userInputLength = autoCompletePopup.getUserInputQualifiedNameLength();
            
            // removes the user's portion of the to-be completed work to fix case errors
            // eg. User types "Hou", then hit Ctrl-Space. Then we replace "Hou" with "hour"
            editor.insertAutoCompleteString(userInputLength, insertion);

        }
        calEditor.requestFocus();
    }
    
    /**
     * Show the CodeEditorQuickCutPanel
     */
    public void showCodeEditorPopupMenu() throws AutoCompletePopupMenu.AutoCompleteException {
        
        try {
            JTextComponent editorPane = editor.getEditorComponent();
            Caret caret = editorPane.getCaret();
            final int caretPos = Math.min(caret.getMark(), caret.getDot());            
            Point location = editorPane.getUI().modelToView(editorPane, caretPos).getLocation();
            int height = editorPane.getFontMetrics(editorPane.getFont()).getHeight();
            location = new Point (location.x, location.y + height);
            try {
                autoCompletePopup.start(perspective, editor.getEditorComponent(), location);        
            } catch (AutoCompletePopupMenu.AutoCompleteException e) {
                closeAutoCompletePopup();
                throw e;
            }
        } catch (BadLocationException e) {
            // positioning auto-complete popup in an invalid position. 
            throw new IllegalStateException("bad location on auto-complete insert");
        }
    }
    
    /**
     * Returns the current perspective 
     * @return Perspective
     */
    Perspective getPerspective() { 
        return perspective;
    }
}
