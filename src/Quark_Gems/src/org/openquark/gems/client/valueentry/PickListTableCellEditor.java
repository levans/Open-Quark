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
 * PickListTableCellEditor.java
 * Created: Feb. 3 / 2004
 * By: David Mosimann
 */
package org.openquark.gems.client.valueentry;

import java.awt.Component;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;

import org.openquark.cal.valuenode.ListValueNode;
import org.openquark.cal.valuenode.ValueNode;


/**
 * A pick list editor that is customized to be used as a table cell editor.  This implementation extends
 * from the pick list value editor which uses a combo box to display the default values.
 */
public class PickListTableCellEditor extends PickListValueEditor implements TableCellEditor {

    private static final long serialVersionUID = 3882665477485020298L;

    /** The list of listeners. */
    private final List<CellEditorListener> listenerList = new ArrayList<CellEditorListener>();

    /**
     * Constructor for a PickListTableCellEditor.
     * @param parentTableValueEditor the parent table value editor
     * @param valueEditorHierarchyManager
     */
    public PickListTableCellEditor(TableValueEditor parentTableValueEditor,
                                   ValueEditorHierarchyManager valueEditorHierarchyManager,
                                   ListValueNode defaultValues,
                                   boolean defaultValuesOnly) {
        super(valueEditorHierarchyManager, null, defaultValues, defaultValuesOnly);
        setParentValueEditor(parentTableValueEditor);
    }

    /**
     * {@inheritDoc}
     */
    public Object getCellEditorValue() {
        return getValueNode();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isCellEditable(EventObject anEvent) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean shouldSelectCell(EventObject anEvent) {
        return true;
    }

    /**
     * {@inheritDoc}
     * This is a commit.
     */
    public boolean stopCellEditing() {
        commitValue();
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public void cancelCellEditing() {
        // Cancel editing...
        cancelValue();
    }

    /**
     * {@inheritDoc}
     */
    public void addCellEditorListener(CellEditorListener l) {
        listenerList.add(l);
    }

    /**
     * {@inheritDoc}
     */
    public void removeCellEditorListener(CellEditorListener l) {
        listenerList.remove(l);
    }

    /**
     * {@inheritDoc}
     * Value must be of type ValueNode.
     * The use of a ValueEditorTableModel should ensure this.
     */
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        
        // HACK: a component may attempt to set this cell editor to edit a new cell before the old value was committed.
        //  (eg. click from the child editor for one value in a list onto another value in the list)
        if (valueEditorHierarchyManager.getChildEditor(this) != null) {
            commitValue();
        }
        
        setEditorIsClosing(false);
        setOwnerValueNode((ValueNode)value);
        setInitialValue();

        // Add this editor to the hierarchy if it's not in the hierarchy already.
        //  (If we are clicking on this editor from a child, we do not want to add this editor).
        //  On close (eg. on stopCellEditing()), it will be removed from the hierarchy, and this will be called again.
        //  TODO: stuff like this should be handled by the manager itself.
        if (!valueEditorHierarchyManager.existsInHierarchy(this)) {
            valueEditorHierarchyManager.addEditorToHierarchy(this, getParentValueEditor());
        }

        return this;
    }

    /**
     * Creates a new combo box and returns it.  Subclasses can override this to change the combo box that is
     * returned.
     * @param values The initial values for the combo box.
     * @return a new JComboBox
     */
    @Override
    protected JComboBox createComboBox(Object[] values) {
        return new JComboBox(values);
    }
}

///**
// * A pick list editor that is customized to be used as a table cell editor.  This implementation extends
// * from the ValueEntryPanel class and works similar to the enumerated value type editor.
// */
//public class PickListTableCellEditor extends ValueEditorTableCellEditor {
//    /** The list of default values that will be presented to the user. */
//    private final ListValueNode defaultValues;
//
//    /**
//     * Constructor for a PickListTableCellEditor.
//     * @param parentTableValueEditor the parent table value editor
//     * @param valueEditorHierarchyManager
//     */
//    public PickListTableCellEditor(TableValueEditor parentTableValueEditor,
//                                   ValueEditorHierarchyManager valueEditorHierarchyManager,
//                                   ListValueNode defaultValues,
//                                   boolean defaultValuesOnly) {
//        super(parentTableValueEditor, valueEditorHierarchyManager);
//        this.defaultValues = defaultValues;
//    }
//
//    /**
//     * Returns a value editor appropriate for this value node, or null if none is appropriate.
//     */
//    protected ValueEditor getValueEditor() {
//        // The normal case is an edited type of "a" and a default values list of "[a]" in which case we
//        // popup an enumerated value editor with the list of possible values.
//        ValueEditor editor = new EnumeratedValueEditorBase(valueEditorHierarchyManager) {
//            /**
//             * @see org.openquark.gems.client.valueentry.EnumeratedValueEditorBase#getValueList()
//             */
//            protected List getValueList() {
//                return (List)defaultValues.getValue();
//            }
//        };
//
//        editor.setOwnerValueNode(getOwnerValueNode());
//        return editor;
//    }
//}
