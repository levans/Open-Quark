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
 * CALNameEditor.java
 * Created: Feb 12, 2004
 * By: David Mosimann
 */
package org.openquark.gems.client.explorer;

import java.awt.Color;
import java.awt.Component;
import java.awt.font.FontRenderContext;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

import org.openquark.gems.client.CodeGem;
import org.openquark.gems.client.CollectorGem;
import org.openquark.gems.client.EditableIdentifierNameField;
import org.openquark.gems.client.Gem;
import org.openquark.gems.client.ToolTipHelpers;
import org.openquark.gems.client.utilities.ExtendedUndoManager;


/**
 * A tree cell editor component for editing gem names.
 * @author Frank Worsley
 */
public abstract class CALNameEditor extends EditableIdentifierNameField.VariableName
                                    implements ExplorerGemNameEditor {
    
    /** Max character length of the name. */
    private static final int MAX_LENGTH = 25;
    
    /** The gem this editor is for. */
    private final Gem gem;
    
    /** The name of the gem before it was edited. */
    private final String oldName;
    
    /** The undo manager for this text field. */
    private final ExtendedUndoManager undoManager;
    
    public CALNameEditor (Gem gem) {
        if (gem == null) {
            throw new NullPointerException();
        }
        
        this.gem = gem;
        this.oldName = (gem instanceof CodeGem) ? ((CodeGem)gem).getUnqualifiedName() : ((CollectorGem)gem).getUnqualifiedName();

        // Setup the text to be displayed.
        setInitialText(oldName);
        setText(getInitialText());
        setEditable(true);
        setMaxLength(MAX_LENGTH);

        // Hopefully, the initial text is a valid name.
        if (!isValidName(getInitialText())) {
            throw new IllegalArgumentException("Programming Error: attempting to initialize the name of a variable with invalid name: " + getInitialText());
        }

        // Setup the border.
        Border outerBorder = BorderFactory.createLineBorder(Color. BLACK);
        Border innerBorder = BorderFactory.createEmptyBorder(1, 5, 1, 10);
        setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));

        // Set up the undo manager.
        this.undoManager = new ExtendedUndoManager();
        getDocument().addUndoableEditListener(undoManager);

        // Select all of the current text
        selectAll();
    }
    
    /**
     * Returns the UI component for the gem editor
     * @return a non-null Component to be shown as the UI component of the editor
     */
    public Component getComponent() {
        return this;
    }

    /**
     * @return the gem being edited by this editor
     */
    public Gem getGem() {
        return gem;
    }
    
    /**
     * Stops the current name editing.  The current value should be validated and committed at this
     * time.  If the current value is invalid it should be discarded and the original value should
     * be preserved.
     */
    public void stopEditing() {
        boolean valid = isValidName(getText());
        if (valid) {
            textCommittedValid();
        } else {
            textCommittedInvalid();
        }
    }

    /**
     * Stops the current name editing.  The current value should be discarded and no new value should
     * be committed.
     */
    public void cancelEditing() {
        textChangeInvalid();
    }

    /**
     * Take appropriate action if the result of the text change is invalid.
     */
    @Override
    public void textChangeInvalid(){

        setForeground(Color.lightGray);
        updateGemName(getText());

        // set a tooltip saying that the text is invalid
        String text = ExplorerMessages.getString("ToolTip_InvalidVariableName");
        String[] lines = ToolTipHelpers.splitTextIntoLines(text, 300, getFont(), getFontRenderContext());
        text = "<html>" + lines [0];
        for (int i = 1; i < lines.length; i++) {
            text += "<br>" + lines[i];
        }
        setToolTipText(text + "</html>");
    
    }

    /**
     * Take appropriate action if the result of the text change is valid.
     */
    @Override
    public void textChangeValid(){
    
        setForeground(Color.black);
        updateGemName(getText());

        // clear any tooltip saying that the text is invalid
        setToolTipText(null);
    }

    /**
     * Take appropriate action if the text committed is valid.
     */
    @Override
    public void textCommittedInvalid(){
        // the text is already reverted.  Update the gem name to reflect this.
        String revertedText = getText();
        updateGemName(revertedText);
    
    }

    /**
     * Take appropriate action if the text committed is valid.
     */
    @Override
    public void textCommittedValid(){
        // Update the gem name.
        String newName = getText();
        if  (!newName.equals(oldName)) {
            renameGem(newName, oldName, true);
        }
    }
    
    /**
     * Update the name of the let gem represented by this text field.
     * @param newName String the new name for the let gem
     */
    private void updateGemName(String newName){
        renameGem(newName, oldName, false);
    }
    
    /**
     * Returns whether a name is a valid name for this field
     * @param name String the name to check for validity 
     */
    @Override
    protected boolean isValidName(String name) {
        return super.isValidName(name);
    }

    /**
     * Renames the specified gem to the new name.  If the new name and old name match then nothing
     * needs to be done.  If the commit flag is true than an undoable edit should be performed, otherwise
     * only the UI should be updated to reflect changes while the user is typing.
     * @param newName
     * @param oldName
     * @param commit
     */
    abstract protected void renameGem(String newName, String oldName, boolean commit);
    
    /**
     * @return a font render context used to render text for the editor
     */
    abstract protected FontRenderContext getFontRenderContext();
}
