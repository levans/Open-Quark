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
 * ValueEditorTableModel.java
 * Created: prior to 2002
 * By: Michael Cheng
 */

package org.openquark.gems.client.valueentry;
import javax.swing.table.AbstractTableModel;

import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.valuenode.ValueNode;


/**
 * The TableModel for the ValueEditors that use JTables.
 * The values that are set/get for this TableModel will be of type ValueNode.
 * Note also that ValueNode contain the values that are to be displayed on screen.
 */
public abstract class ValueEditorTableModel extends AbstractTableModel {
   
    protected final ValueEditorManager valueEditorManager;

    /**
     * Constructor for the TableModel    
     * @param valueEditorManager
     */
    public ValueEditorTableModel(ValueEditorManager valueEditorManager) {
        this.valueEditorManager = valueEditorManager;     
    }
    
    @Override
    public Class<ValueNode> getColumnClass(int c) {
        // Note: The actual values for each cell are ValueNodes, but the real/representative values
        // is an Object (Integer, Double, Boolean, etc) in the ValueNode.
        return ValueNode.class;
    }
    
    public abstract int getColumnCount();
    
    @Override
    public abstract String getColumnName(int col);
        
    public abstract TypeExpr getElementType(int col);
        
    public abstract int getNElements();
    
    @Override
    public boolean isCellEditable(int row, int col) {
        return true;
    }

    /**
     * {@inheritDoc}
     * Does nothing! Component values should be committed by TableValueEditor.commitChildChanges().
     * TODOEL: do tableModel.fireTableCellUpdated(row, col) events?
     */
    @Override
    public void setValueAt(Object value, int row, int col) {
    }
}