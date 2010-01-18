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
 * UndoableChangeFieldSelectionEdit.java
 * Creation date: Dec 6, 2006.
 * By: Neil Corkum
 */
package org.openquark.gems.client;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoableEdit;

/**
 * An UndoableEdit used to change the field that an RecordFieldSelection gem is extracting
 * from a record.
 * @author Neil Corkum
 */
public class UndoableChangeFieldSelectionEdit extends AbstractUndoableEdit {
    private static final long serialVersionUID = 636836878728440925L;

    /** The tabletop for the gem. */    
    private final TableTop tableTop;

    /** The RecordFieldSelection gem in question. */
    private final RecordFieldSelectionGem recordFieldSelectionGem;
    
    /** The old name of the field extracted by the RecordFieldSelection gem. */
    private final String oldName;
    
    /** The old state of whether or not the field was fixed */
    private final boolean oldFixedState;

    /** The new name of the field extracted by the RecordFieldSelection gem. */
    private final String newName;
    
    /** The new state fixed state */
    private final boolean newFixedState;


    /**
     * Constructor for an UndoableChangeFieldSelectionEdit.
     * @param tableTop the tabletop to which to add.
     * @param recordFieldSelectionGem the collector gem in question
     * @param oldName the name of the gem before the event
     */
    UndoableChangeFieldSelectionEdit(TableTop tableTop, RecordFieldSelectionGem recordFieldSelectionGem, String oldName, boolean oldFixedState) {
        this.tableTop = tableTop;
        this.recordFieldSelectionGem = recordFieldSelectionGem;
        this.oldName = oldName;
        this.newName = recordFieldSelectionGem.getFieldNameString();
        this.oldFixedState = oldFixedState;
        this.newFixedState = recordFieldSelectionGem.isFieldFixed();
    }
    
    /**
     * A reasonable name for this edit.
     */
    @Override
    public String getPresentationName() {
        return GemCutter.getResourceString("UndoText_ChangeFieldSelected");
    }

    /**
     * Redo the edit.
     */
    @Override
    public void redo() {
        super.redo();
        recordFieldSelectionGem.setFieldFixed(newFixedState);
        recordFieldSelectionGem.setFieldName(newName);
    }

    /**
     * Undo the edit.
     */
    @Override
    public void undo() {
        super.undo();
        recordFieldSelectionGem.setFieldFixed(oldFixedState);
        recordFieldSelectionGem.setFieldName(oldName);
    }

    /**
     * Replace anEdit if possible.
     * @return boolean whether the edit was absorbed.
     */
    @Override
    public boolean replaceEdit(UndoableEdit anEdit){

        // Replace if the other edit is equivalent to this one
        if (!(anEdit instanceof UndoableChangeFieldSelectionEdit)) {
            return false;
        }
        
        UndoableChangeFieldSelectionEdit otherEdit = (UndoableChangeFieldSelectionEdit)anEdit;

        return (otherEdit.tableTop == this.tableTop &&
                otherEdit.recordFieldSelectionGem == this.recordFieldSelectionGem &&
                otherEdit.oldFixedState == this.oldFixedState &&
                ( otherEdit.oldName != null &&
                otherEdit.oldName.equals(this.oldName)) &&
                ( otherEdit.newName !=null &&
                otherEdit.newName.equals(this.newName)));
    }
}
