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
 * ValueEditorTableCellEditor.java
 * Created: prior to 2002
 * By: ?
 */

package org.openquark.gems.client.valueentry;

import java.awt.Component;
import java.util.EventObject;
import java.util.List;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;

import org.openquark.cal.valuenode.ValueNode;



/**
 * The default TableCellEditor for ValueEditors that use tables.
 * It will be a ValueEntryPanel of the correct type for the cell.
 */
public class ValueEditorTableCellEditor extends ValueEntryPanel implements TableCellEditor {

    private static final long serialVersionUID = 3959043138610802855L;

    /** The list of listeners. */
    private final List<CellEditorListener> listenerList;

    // If the cell editor is being currently used, the value is the column currently being edited.
    // If the cell editor is not currently used, the value is the last column edited.
    // If the cell editor was never used, then the value defaults to 0 (done in constructor).
    private int editColumn;
    
    /**
     * Constructor for a ValueEditorTableCellEditor.
     * @param parentTableValueEditor the parent table value editor
     * @param valueEditorHierarchyManager
     */
    public ValueEditorTableCellEditor(TableValueEditor parentTableValueEditor, ValueEditorHierarchyManager valueEditorHierarchyManager) {

        super(valueEditorHierarchyManager);
        setParentValueEditor(parentTableValueEditor);

        // use a synchronized list.
        listenerList = new Vector<CellEditorListener>();

        editColumn = 0;
    }
    
    /**
     * Return the parent table value editor.
     * @return the parent table value editor
     */
    private TableValueEditor getParentTableValueEditor() {
        return (TableValueEditor) getParentValueEditor();
    }
    
    /**
     * If the cell editor is being currently used, the value is the column currently being edited.
     * If the cell editor is not currently used, the value is the last column edited.
     * If the cell editor was never used, then the value defaults to 0 (done in constructor).   
     * Creation date: (01/03/01 9:12:55 AM)
     * @return int
     */
    public int getEditColumn() {
        return editColumn;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void editorActivated() {
        super.editorActivated();
        getParentTableValueEditor().handleCellActivated();
        updateLaunchEditorButton();
    }
    
    /**
     * Enable or disable the launch editor button according to the current state of things.
     */
    private void updateLaunchEditorButton() {

        if (isEditable()) {
            // Always enable editor button
            getLaunchEditorButton().setEnabled(true);

        } else {
            
            // The value node may not be set if the editor is being initialized..
            //  TODO: remove this line (initialize with a value node)
            if (getValueNode() == null) {
                return;
            }
            
            getLaunchEditorButton().setEnabled(true);
        }
    }

    /*
     * Methods implementing TableCellEditor    ************************************************************
     */

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

        // Note that the JTable parent automatically registers itself as a listener on this cell editor.
        //   When it receives editingStopped() events, it calls on the model to setValueAt() the cell that is currently being edited.
        //   This conflicts with the value editor's current mechanism of commiting values, which uses close().
        //
        // For now, this section is commented out to avoid this.  This means that setValueAt() is never called (for now).
        //   However, this introduces a problem in the API that any other listeners will never be notified.
        //   In the future it might be nicer if we could allow the JTable to setValueAt() in the normal way, have it fire tableCellUpdated()
        //   events, have those in turn fire valueCommitted() events, which then in turn results in a call to commitChildChanges().
        // The nice feature of this change would be that it should allow us to remove the hack at the beginning of 
        //   getTableCellEditorComponent().
        //
//        ChangeEvent ce = new ChangeEvent(this);
//        for (int i = 0, listenerCount = listenerList.size(); i < listenerCount; i++) {
//            ((CellEditorListener) listenerList.get(i)).editingStopped(ce);
//        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    public void cancelCellEditing() {

        // This is a hack!
        // JTable for JDK 1.4 has a "CellEditorRemover" that is supposed to cancel cell editing when focus
        // is shifted out of the table.  Since we put all child editors on the TableTop rather than the table
        // we have a problem with this.  For JDK 1.4.0 there is a bug in the CellEditorRemover so that it only
        // actually cancels the first time it trys to and never again.  This will probably be fixed for the next
        // release.  My hack is to check here and return without doing anything if the component with focus
        // belongs to a child editor.  This should normally only cause problems with things if the table's
        // "Remove" button is pressed because it uses this function.  However, when a child editor is opened (and
        // has focus) the Remove button is disabled.  As a result, the Remove button can never be pressed
        // unless there is no child editor so my little hack does not come into play.
        // Bug id: 4503845, 4709394 - fixable in jdk1.4.1.
        //
        // Do not cancel editing if there is a child editor open and it (or one of its components) has focus.
        ValueEditor childEditor = valueEditorHierarchyManager.getChildEditor(this);
        if (childEditor != null && childEditor.hasOverallFocus()) {
            return;
        }

        // Cancel editing...
        cancelValue();

        // See note for stopCellEditing()
//        ChangeEvent ce = new ChangeEvent(this);
//        for (int i = 0, listenerCount = listenerList.size(); i < listenerCount; i++) {
//            ((CellEditorListener) listenerList.get(i)).editingCanceled(ce);
//        }
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
        
        editColumn = column;
        
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

        // Prevent the user from clicking on the rightmost portion of a ValueEditorTableCellRenderer, 
        //   and suddenly launching the editor.
        getLaunchEditorButton().setEnabled(false);
        SwingUtilities.invokeLater(new Thread() {
            @Override
            public void run() {
                updateLaunchEditorButton();
                valueEditorHierarchyManager.activateCurrentEditor();
            }
        });

        return this;
    }
}
