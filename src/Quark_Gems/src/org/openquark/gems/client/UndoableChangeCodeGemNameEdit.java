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
 * UndoableChangeCodeGemNameEdit.java
 * Creation date:Oct 6th 2002
 * By: Ken Wong
 */
package org.openquark.gems.client;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoableEdit;

/**
 * An UndoableEdit used to undo a change in the name of a code gem.
 * Creation date: Oct 6th 2002
 * @author Ken Wong
 */
public class UndoableChangeCodeGemNameEdit extends AbstractUndoableEdit {

    private static final long serialVersionUID = 1171581849089480554L;

    /** The tabletop for the gem. */    
    private final TableTop tableTop;

    /** The code gem in question. */
    private final CodeGem codeGem;
    
    /** The old name of the code gem. */
    private final String oldName;

    /** The new name of the code gem. */
    private final String newName;

    /**
     * Constructor for an UndoableChangeCodeGemNameEdit.
     * @param tableTop the tabletop to which this edit belongs
     * @param codeGem the gem in question
     * @param oldName the name of the gem before the event
     */
    UndoableChangeCodeGemNameEdit(TableTop tableTop, CodeGem codeGem, String oldName) {
        this.tableTop = tableTop;
        this.codeGem = codeGem;
        this.oldName = oldName;
        this.newName = codeGem.getUnqualifiedName();
    }
    
    /**
     * A reasonable name for this edit.
     * @return String
     */
    @Override
    public String getPresentationName() {
        return GemCutter.getResourceString("UndoText_ChangeCodeGemName");
    }

    /**
     * Redo the edit.
     */
    @Override
    public void redo() {
        super.redo();
        tableTop.renameCodeGem(codeGem, newName);
    }

    /**
     * Undo the edit.
     */
    @Override
    public void undo() {
        super.undo();
        tableTop.renameCodeGem(codeGem, oldName);    
    }

    /**
     * Replace anEdit if possible.
     * @param anEdit
     * @return boolean whether the edit was absorbed.
     */
    @Override
    public boolean replaceEdit(UndoableEdit anEdit) {

        // Replace if the other edit is equivalent to this one
        if (!(anEdit instanceof UndoableChangeCodeGemNameEdit)) {
            return false;
        }
        
        UndoableChangeCodeGemNameEdit otherEdit = (UndoableChangeCodeGemNameEdit)anEdit;

        return (otherEdit.tableTop == this.tableTop &&
                otherEdit.codeGem == this.codeGem &&
                otherEdit.oldName.equals(this.oldName) &&
                otherEdit.newName.equals(this.newName));
        
    }
}

