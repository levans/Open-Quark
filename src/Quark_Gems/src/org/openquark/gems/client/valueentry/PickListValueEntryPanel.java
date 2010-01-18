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
 * PickListValueEntryPanel.java
 * Creation date: Feb. 2 / 2004
 * By: David Mosimann
 */
package org.openquark.gems.client.valueentry;

import java.util.List;

import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.valuenode.ListValueNode;
import org.openquark.cal.valuenode.ValueNode;



/**
 * Extends the value entry panel to provide a pick list of default values rather than the type specific
 * value editor when the expansion "..." button is clicked on.  This class also deals with the value node
 * being a list in which case the "..." button needs to popup a list value editor where the table cell
 * editor is a pick list editor. 
 */
public class PickListValueEntryPanel extends ValueEntryPanel {

    private static final long serialVersionUID = -855428642518610423L;

    /** The list of default values that will be presented to the user. */
    private final ListValueNode defaultValues;

    /** Whether the user should be restricted to one of the default values or they can enter any value */
    private final boolean defaultValuesOnly;
    
    /**
     * @param valueEditorHierarchyManager
     * @param dataVN
     */
    public PickListValueEntryPanel(ValueEditorHierarchyManager valueEditorHierarchyManager,
                                   ValueNode dataVN,
                                   ListValueNode defaultValues,
                                   boolean defaultValuesOnly) {
        super(valueEditorHierarchyManager, dataVN);
        this.defaultValues = defaultValues;
        this.defaultValuesOnly = defaultValuesOnly;
    }

    /**
     * Returns a value editor appropriate for this value node, or null if none is appropriate.
     */
    @Override
    protected ValueEditor getValueEditor() {
        // If the type being edited is "[a]" then the default values is normally "[a]".  In this case
        // we want to popup a list value editor where each element can be one from the list.
        ValueEditor editor;
        ModuleTypeInfo typeInfo = valueEditorHierarchyManager.getValueEditorManager().getPerspective().getWorkingModuleTypeInfo();
        TypeExpr dataType = getValueNode().getTypeExpr(); 
        if (dataType.isListType() && TypeExpr.canUnifyType(dataType, defaultValues.getTypeExpr(), typeInfo)) {
            editor = new ListTupleValueEditor(valueEditorHierarchyManager, null) {
                private static final long serialVersionUID = -6933035868423482449L;

                /**
                 * Override the default table cell editor with one that understands pick lists  
                 */
                @Override
                protected ValueEditor createTableCellEditor() {
                    return new PickListTableCellEditor(this,
                                                       valueEditorHierarchyManager,
                                                       defaultValues,
                                                       defaultValuesOnly);
                }
            };
        } else {
            // The normal case is an edited type of "a" and a default values list of "[a]" in which case we
            // popup an enumerated value editor with the list of possible values.
            editor = new EnumeratedValueEditorBase(valueEditorHierarchyManager) {
                private static final long serialVersionUID = -1137393564179799312L;

                /**
                 * @see org.openquark.gems.client.valueentry.EnumeratedValueEditorBase#getValueList()
                 */
                @Override
                protected List<ValueNode> getValueList() {
                    return defaultValues.getValue();
                }
            };
        }

        editor.setOwnerValueNode(getOwnerValueNode());
        return editor;
    }
}
