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
 * ListTupleTableModel.java
 * Created: Feb 28, 2001
 * By: Michael Cheng
 */

package org.openquark.gems.client.valueentry;

import java.util.List;

import org.openquark.cal.compiler.FieldName;
import org.openquark.cal.compiler.TypeConsApp;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.valuenode.ListValueNode;
import org.openquark.cal.valuenode.NTupleValueNode;
import org.openquark.cal.valuenode.RecordValueNode;
import org.openquark.cal.valuenode.ValueNode;


/**
 * The table model for handling lists, lists of tuples and lists of records
 * 
 * Note: Do not use this table model for types other than lists (which includes lists of tuples).
 * 
 * Creation date: (28/02/01 1:17:20 PM)
 * @author Michael Cheng
 */
class ListTupleTableModel extends ListTableModelBase {

    private static final long serialVersionUID = -5931490046618373666L;

    /** The ith value of elementTypeArrayParam gives the type for the ith column in the array.*/
    private final TypeExpr[] elementTypeExprArray;
    
    /** 
     * Whether the columns of this table are consolidated.
     * 
     * If true, this causes the model to have one column whose elements are the list element value nodes.
     * otherwise, for lists of tuples and records, the model columns represent individual fields of the record/tuple elements.
     */
    private boolean consolidatedColumns = false;
    
    /**
     * ListTupleTableModel constructor.
     * @param listValueNode
     * @param valueEditorHierarchyManager
     */
    public ListTupleTableModel(ListValueNode listValueNode, ValueEditorHierarchyManager valueEditorHierarchyManager, boolean consolidatedColumns) {

        super(listValueNode, valueEditorHierarchyManager.getValueEditorManager());
        
        if (listValueNode == null) {
            throw new NullPointerException ("argument listValueNode cannot be null.");
        }
        
        // Initialize elementTypeArray.
        int nElements = getNElements();

        elementTypeExprArray = new TypeExpr[nElements];
        for (int i = 0; i < nElements; i++) {
            elementTypeExprArray[i] = getElementType(i);
        }
        
        this.consolidatedColumns = consolidatedColumns;
    }
    
    @Override
    public int getColumnCount() {
        if (!consolidatedColumns) {
            
            if (elementTypeExprArray.length == 0 && isListRecord()) {
                // Special case: list of records with no fields has one column (with header)
                return 1;
                
            } else {
                return elementTypeExprArray.length;
            }
        } else {
            return 1;
        }
    }
    
    @Override
    public int getRowCount() {
        
        if (elementTypeExprArray.length == 0 && isListRecord()) {
            // Special case: if this is a list of records with no fields,
            return super.getRowCount();
            
        } else {
            return super.getRowCount();
        }
    }
    
    @Override
    public String getColumnName(int col) {
        
        TypeConsApp listTypeConsApp = ((TypeConsApp) getListValueNode().getTypeExpr());
        
        if (!consolidatedColumns && isListRecord()) {
            
            // Special case: if this is a list of records, use record field names as column names
            List<FieldName> fieldNames = listTypeConsApp.getArg(0).rootRecordType().getHasFieldNames();
            
            if (fieldNames.size() == 0) {
                return ValueEditorMessages.getString("VE_NoFieldsColumnName");
                
            } else {
                return (fieldNames.get(col)).getCalSourceForm();
            }
            
        } else {
            TypeExpr columnType = getElementType(col);
            
            if (columnType.rootRecordType() != null && columnType.rootRecordType().getNHasFields() > 0) {
                // Special case: Column names for record types with more than 0 fields are labeled "{Record}" for clarity
                return ValueEditorMessages.getString("VE_RecordColumnName");
            } else {
                return valueEditorManager.getTypeName(columnType);
            }
        }
    }
    
    /**
     * Return the TypeExpr for the ith column in the array (ie: the ith element in the elementTypeExprArray).
     * @param col column index of element to retrieve type for
     * @return TypeExpr 
     */
    @Override
    public TypeExpr getElementType (int col) {
        TypeConsApp listTypeConsApp = ((TypeConsApp) getListValueNode().getTypeExpr());
       
        if (!consolidatedColumns && isListRecord()) {
            
            //this handles the case of lists of tuples as well as lists of general records
            
            // Special case: in the case of a list of records, index the record fields alphabetically 
            List<FieldName> fieldNames = listTypeConsApp.getArg(0).rootRecordType().getHasFieldNames();
            
            if (fieldNames.isEmpty()) {
                // Special case: record has no fields. The type of this column is the record type
                return ((TypeConsApp) getListValueNode().getTypeExpr()).getArg(0).rootRecordType();
            }
            
            return listTypeConsApp.getArg(0).rootRecordType().getHasFieldType(fieldNames.get(col));
            
        } else {
            return listTypeConsApp.getArg(col);
        }       
    }
    
    @Override
    public int getNElements() {
        
        TypeConsApp listTypeConsApp = ((TypeConsApp) getListValueNode().getTypeExpr());
       
        if (!consolidatedColumns && isListRecord()) {
            // Special case: in the case of a list of record, index the record fields
            return listTypeConsApp.getArg(0).rootRecordType().getNHasFields();
            
        } else {
            return listTypeConsApp.getNArgs();
        }  
    }
    
    public Object getValueAt(int row, int col) {

        ValueNode rowValueNode = getListValueNode().getValueAt(row);
        
        if (!consolidatedColumns && isListRecord()) {
            
            if (rowValueNode instanceof RecordValueNode) {
                // A list of records
                if (elementTypeExprArray.length == 0) {                
                    return ValueEditorMessages.getString("VE_NoFieldsValue");
                } else {
                    return ((RecordValueNode)rowValueNode).getValueAt(col);
                }
            
            } else if (rowValueNode instanceof NTupleValueNode) {
                // A list of tuples.
                return ((NTupleValueNode)rowValueNode).getValueAt(col);

            } else {
                throw new IllegalStateException("Can't handle list of records where list elements are of type: " + rowValueNode.getClass());
            }

        } else {
            // Just a list.
            return rowValueNode;

        }
    }
    
    /**
     * Returns true if the data type in this model is a List of Records,
     * otherwise returns false.
     * Note that this will also be true if the type of the data is a List of Tuples.
     * @return boolean
     */
    public boolean isListRecord() {
        // Note: Always assume that typeExpr is a List.
        TypeConsApp listTypeConsApp = (TypeConsApp) getListValueNode().getTypeExpr();                 
        return listTypeConsApp.getArg(0).rootRecordType() != null;
    }
    
    /**
     * Sets the value at the indicated row and col.
     */
    @Override
    public void setValueAt(Object value, int row, int col) {

        // Need to check if it's just a list, or a list of records
        if (!consolidatedColumns && isListRecord()) {

            ValueNode rowValueNode = getListValueNode().getValueAt(row);
            
            if (rowValueNode instanceof RecordValueNode) {
                // A list of records.
                ((RecordValueNode)rowValueNode).setValueNodeAt(col, (ValueNode) value);
            
            } else if (rowValueNode instanceof NTupleValueNode) {
                // A list of tuples.
                ((NTupleValueNode)rowValueNode).setValueNodeAt(col, (ValueNode) value);
            
            } else {
                throw new IllegalStateException("Can't handle list of records where list elements are of type: " + rowValueNode.getClass());
            }

        } else {

            // Just a list.
            getListValueNode().setValueNodeAt(row, (ValueNode)value);

        }
        
        fireTableCellUpdated(row, col);
    }
    
    /**
     * @return whether the model is set to consolidate columns (for tuple and record types)
     */
    public boolean isConsolidatingColumns() {
        return consolidatedColumns;
    }
    
    /**
     * Set the model to consolidate columns for tuple and record types
     * @param consolidatedColumns new value for consolidation flag
     */
    public void setConsolidatingColumns(boolean consolidatedColumns) {

        this.consolidatedColumns = consolidatedColumns;
        fireTableStructureChanged();
    }
    
    /**
     * @see javax.swing.table.TableModel#isCellEditable(int, int)
     */
    @Override
    public boolean isCellEditable(int row, int column) {
        
       if (isListRecord() && elementTypeExprArray.length == 0 && !isConsolidatingColumns()) {
           // If it's modeling an empty record, don't allow editing of any columns
           return false;
           
       } else {
           return super.isCellEditable(row, column);
       }
    }
}
