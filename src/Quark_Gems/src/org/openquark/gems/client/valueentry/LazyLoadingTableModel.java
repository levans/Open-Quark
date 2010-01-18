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
 * LazyLoadingTableModel.java
 * Created: Nov 12, 2003
 * By: Kevin Sit
 */
package org.openquark.gems.client.valueentry;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import org.openquark.util.UnsafeCast;

/**
 * @author ksit
 */
public class LazyLoadingTableModel extends DefaultTableModel {

    private static final long serialVersionUID = 7452879275031531192L;

    /**
     * When this flag is set, all of the cells are editable.
     */    
    private boolean globallyEditable;

    public LazyLoadingTableModel() {
        super();
    }

    /**
     * The globally editable flag overrides this method: if the flag is set,
     * this method always return false. 
     * 
     * @see javax.swing.table.TableModel#isCellEditable(int, int)
     */
    @Override
    public boolean isCellEditable(int row, int column) {
        if (globallyEditable) {
            return super.isCellEditable(row, column);
        } else {
            return false;
        }
    }
    
    /**
     * Sets the globally editable flag for this model.  Once this flag affects
     * all cells in the model.
     * @param globallyEditable
     */
    public void setGloballyEditable(boolean globallyEditable) {
        this.globallyEditable = globallyEditable;
    }

    /**
     * Returns <code>true</code> if all the cells are editable.
     */
    public boolean isGloballyEditable() {
        return globallyEditable;
    }

    /**
     * Adds all rows to the data model.  Each row is represented by an array
     * of <code>Object</code>s.  Ideally each object array should contain the
     * same number of elements as the number of column(s).
     * @param rows
     */
    public void addAllRows(Object[][] rows) {
        insertAllRows(getRowCount(), 
                      UnsafeCast.<Vector<Vector<Object>>>unsafeCast(convertToVector(rows)));    // convertToVector() described as such in API.
    }
    
    /**
     * Internal helper method for inserting a list of rows to the data model
     * and fire one single "table rows inserted" event for the entire operation.
     * @param rowIndex
     * @param rows
     */    
    private void insertAllRows(int rowIndex, Vector<Vector<Object>> rows) {
        int rowCount = rows.size();
        for (int i = 0; i < rowCount; i++) {
            getDataVector().insertElementAt(rows.get(i), rowIndex + i); 
        }
        justifyRows(rowIndex, rowIndex + rowCount); 
        fireTableRowsInserted(rowIndex, rowIndex + rowCount-1);
    }

    /**
     * Method copied from the base class for adjusting the width of each row's
     * data vector to match the column count or create a new empty vector
     * for rows with <code>null</code> data vector.
     * @param from inclusive
     * @param to exclusive
     */
    protected void justifyRows(int from, int to) {
        // Sometimes the DefaultTableModel is subclassed 
        // instead of the AbstractTableModel by mistake. 
        // Set the number of rows for the case when getRowCount 
        // is overridden. 
        Vector<Vector<Object>> dataVector = getDataVector();
        dataVector.setSize(getRowCount());
        for (int i = from; i < to; i++) {
            if (dataVector.elementAt(i) == null) {
                dataVector.setElementAt(new Vector<Object>(), i);
            }
            dataVector.elementAt(i).setSize(getColumnCount());
        }
    }
    
    /**
     * Override to generify output type.
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public Vector<Vector<Object>> getDataVector() {
        return dataVector;
    }

}
