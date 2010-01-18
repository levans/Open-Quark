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
 * ListTableModelBase.java
 * Created: ??
 * By: Richard Webster
 */
package org.openquark.gems.client.valueentry;

import org.openquark.cal.compiler.TypeConsApp;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.valuenode.ListValueNode;
import org.openquark.cal.valuenode.ValueNode;


/**
 * Common base class for list value editor models.
 * @author Richard Webster
 */
public abstract class ListTableModelBase extends AbstractListTableModel {

    private final ListValueNode listValueNode;

    /**
     * Constructor for ListTableModelBase.
     */
    public ListTableModelBase(ListValueNode listValueNode, ValueEditorManager valueEditorManager) {
        super(valueEditorManager);
        if (listValueNode == null) {
            throw new NullPointerException ("argument listValueNode cannot be null.");
        }

        this.listValueNode = listValueNode;
    }

    /**
     * Returns the value node for the list.
     */
    protected ListValueNode getListValueNode() {
        return listValueNode;
    }

    /**
     * Returns the number of rows to display in the list editor.
     */
    @Override
    public int getRowCount() {
        return listValueNode.getNElements();
    }

    /**
     * Adds an item to the list.
     */
    @Override
    public void addRow() {
        TypeExpr typeExpr = listValueNode.getTypeExpr();
       
        TypeExpr elementTypeExpr = ((TypeConsApp) typeExpr).getArg(0);
        ValueNode elementVN = valueEditorManager.getValueNodeBuilderHelper().getValueNodeForTypeExpr(elementTypeExpr);
        listValueNode.add(elementVN);
        
        // The following two lines were commented out in Ken's change.  Why?
        int indexNewRow = listValueNode.getNElements() - 1;
        fireTableRowsInserted(indexNewRow, indexNewRow);
    }

    /**
     * Moves the specified row one down.
     * Note: Make sure that there is another row under it.
     */
    @Override
    public void moveRowDown(int row) {

        ValueNode moveRow = listValueNode.getValueAt(row);
        ValueNode nextRow = listValueNode.getValueAt(row + 1);

        listValueNode.setValueNodeAt(row + 1, moveRow);
        listValueNode.setValueNodeAt(row, nextRow);

        fireTableRowsUpdated(row, row + 1);
    }
    
    /**
     * Moves the specified row one up.
     * Note: Make sure that there is another row above it.
     */
    @Override
    public void moveRowUp(int row) {

        ValueNode moveRow = listValueNode.getValueAt(row);
        ValueNode nextRow = listValueNode.getValueAt(row - 1);

        listValueNode.setValueNodeAt(row - 1, moveRow);
        listValueNode.setValueNodeAt(row, nextRow);

        fireTableRowsUpdated(row - 1, row);
    }

    /**
     * Removes the specified item from the list.
     */
    @Override
    public void removeRow(int row) {
        listValueNode.removeValueNodeAt(row);

        fireTableRowsDeleted(row, row);
    }
}

