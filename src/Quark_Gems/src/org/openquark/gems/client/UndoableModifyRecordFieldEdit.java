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
 * UndoableModifyRecordFieldEdit.java
 * Creation date: Sept. 25th, 2007.
 * By: Jennifer Chen
 */
package org.openquark.gems.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoableEdit;

import org.openquark.cal.compiler.FieldName;
import org.openquark.gems.client.Gem.PartInput;
import org.openquark.util.Pair;

/**
 * Undoable edit for modifying record fields to a RecordCreationGem, 
 * includes adding a new field, deleting and renaming an existing field
 * @author Jennifer Chen
 */
public class UndoableModifyRecordFieldEdit extends AbstractUndoableEdit {

    private static final long serialVersionUID = -4091724130085043703L;

    /** The RecordCreation gem modified. */
    private final RecordCreationGem recordCreationGem;

    /** The old field names and inputs before the action */
    private final Map<String, PartInput> oldMap;

    /** The updated field names and inputs after the action */
    private final Map<String, PartInput> newMap;

    /** The fields before the modification */
    private final List<String> oldModFields;

    /** The fields after the modification */
    private final List<String> newModFields;

    // Use single field for now since not handling multiple actions
    private final String oldModField;

    private final String newModField;

    /** Specifies if the action is adding, deleting or renaming a field */
    private final Action action;

    private enum Action {
        ADD, DELETE, RENAME
    };

    /**
     * Constructor
     * @param gem the RecordCreationGem being modified
     * @param oldMap map from field name to input prior to the modification.
     */
    UndoableModifyRecordFieldEdit(RecordCreationGem gem, Map<String, PartInput> oldMap) {
        this.recordCreationGem = gem;
        this.oldMap = oldMap;
        this.newMap = gem.getFieldNameToInputMap();

        // Determine which action was taken
        action = getAction();

        // Get the fields modified
        Pair<List<String>, List<String>> fieldsModified = getFieldModified();
        oldModFields = fieldsModified.fst();
        newModFields = fieldsModified.snd();

        // Use single field for now since not handling multiple actions
        if (!oldModFields.isEmpty()) {
            oldModField = oldModFields.get(0);
        } else {
            oldModField = null;
        }
        if (!newModFields.isEmpty()) {
            newModField = newModFields.get(0);
        } else {
            newModField = null;
        }
    }

    /**
     * Determines the action of of this edit. Note: can't handle multiple actions
     * @return ADD, DELETE or RENAME
     */
    private Action getAction() {
        int diff = newMap.size() - oldMap.size();
        if (diff > 0) {
            return Action.ADD;
        } else if (diff == 0) {
            return Action.RENAME;
        } else {
            return Action.DELETE;
        }
    }

    /**
     * Determined the fields being modified
     * @return a set of fields modified and a set of new fields
     */
    private Pair<List<String>, List<String>> getFieldModified() {

        Set<String> oldMapSet = oldMap.keySet();
        Set<String> newMapSet = newMap.keySet();

        // The set of fields only in the old map, ie either the deleted field or the renamed field
        List<String> inOldMapOnly = new ArrayList<String>(oldMapSet);
        inOldMapOnly.removeAll(newMapSet);

        // The set of fields only in the new map, ie either the added field or the renamed field
        List<String> inNewMapOnly = new ArrayList<String>(newMapSet);
        inNewMapOnly.removeAll(oldMapSet);

        return new Pair<List<String>, List<String>>(inOldMapOnly, inNewMapOnly);

    }

    /**
     * A reasonable name for this edit.
     */
    @Override
    public String getPresentationName() {
        String actionName = null;
        String fieldName = null;
        switch (action) {
        case ADD:
            actionName = "UndoText_AddRecordField";
            fieldName = newModField;
            break;
        case RENAME:
            actionName = "UndoText_RenameRecordField";
            fieldName = oldModField;
            break;
        case DELETE:
            actionName = "UndoText_DeleteRecordField";
            fieldName = oldModField;
            break;
        }
        return GemCutter.getResourceString(actionName) + " " + fieldName;
    }

    /**
     * Redo the edit.
     */
    @Override
    public void redo() {
        super.redo();
        switch (action) {
        case ADD:
            recordCreationGem.addNewFields(1);
            break;

        case RENAME:
            int fieldIndex = recordCreationGem.getFieldIndex(FieldName.make(oldModField));
            recordCreationGem.renameRecordField(fieldIndex, newModField);
            break;

        case DELETE:
            recordCreationGem.deleteField(oldModField);
            break;
        }
    }

    /**
     * Undo the edit.
     */
    @Override
    public void undo() {
        super.undo();

        switch (action) {
        case ADD:
            recordCreationGem.deleteField(newModField);
            recordCreationGem.setNextPossibleFieldName(recordCreationGem.getNextPossibleFieldName() - 1);
            break;

        case RENAME:
            int fieldIndex = recordCreationGem.getFieldIndex(FieldName.make(newModField));
            recordCreationGem.renameRecordField(fieldIndex, oldModField);
            break;

        case DELETE:
            recordCreationGem.setFieldNames(new ArrayList<String>(oldMap.keySet()));
            recordCreationGem.updateInputs(oldMap);
            break;
        }
    }

    /**
     * Replace anEdit if possible.
     * @return boolean whether the edit was absorbed.
     */
    @Override
    public boolean replaceEdit(UndoableEdit anEdit) {

        // Replace if the other edit is equivalent to this one
        if (!(anEdit instanceof UndoableModifyRecordFieldEdit)) {
            return false;
        }

        UndoableModifyRecordFieldEdit otherEdit = (UndoableModifyRecordFieldEdit)anEdit;

        return (otherEdit.recordCreationGem.equals(this.recordCreationGem) && 
                otherEdit.oldMap.equals(this.oldMap) && 
                otherEdit.newMap.equals(this.newMap) && 
                otherEdit.action == this.action);
    }

}
