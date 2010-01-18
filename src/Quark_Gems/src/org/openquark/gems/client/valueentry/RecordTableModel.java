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
 * RecordTableModel.java
 * Created: Jul 06, 2004
 * By: Iulian Radu
 */

package org.openquark.gems.client.valueentry;

import java.util.List;

import org.openquark.cal.compiler.FieldName;
import org.openquark.cal.compiler.RecordType;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.valuenode.AbstractRecordValueNode;


/**
 * The table model for handling records.
 * This models the record fields as either rows or columns (depending on
 * layout orientation) 
 * 
 * @author Iulian Radu
 */
class RecordTableModel extends ValueEditorTableModel {
    
    private static final long serialVersionUID = -7164727694472130455L;

    /** Indicates the types of the record fields */
    private final TypeExpr[] fieldTypeExprArray;
    
    /** Indicates the name of each record field */
    private final FieldName[] fieldNameArray;

    /** The record value node modeled */
    private final AbstractRecordValueNode recordValueNode;
    
    /** 
     * Whether the table orientation is horizontal. 
     * If true, then fields correspond to columns; otherwise, fields correspond to rows.
     */ 
    private final boolean horizontalLayout;
    

    /**
     * RecordTableModel constructor.
     * @param recordValueNodeParam 
     * @param valueEditorHierarchyManager
     */
    public RecordTableModel(AbstractRecordValueNode recordValueNodeParam, ValueEditorHierarchyManager valueEditorHierarchyManager, boolean horizontalOrientation) {
        super(valueEditorHierarchyManager.getValueEditorManager());
        recordValueNode = recordValueNodeParam;
        horizontalLayout = horizontalOrientation;
        
        // Initialize Arrays.
        
        RecordType recordType = recordValueNode.getTypeExpr().rootRecordType();
        List<FieldName> recordFieldNames = recordType.getHasFieldNames();
        int nElements = recordFieldNames.size();

        fieldNameArray = new FieldName[nElements];
        for (int i = 0; i < nElements; i++) {
            fieldNameArray[i] = recordFieldNames.get(i);
        }
        fieldTypeExprArray = new TypeExpr[nElements];
        for (int i = 0; i < nElements; i++) {
            fieldTypeExprArray[i] = recordType.getHasFieldType(fieldNameArray[i]);
        }
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getColumnCount() {
        if (horizontalLayout) {
            return fieldTypeExprArray.length;
            
        } else {
            return 1;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getColumnName(int col) {
        if (horizontalLayout) {
            return fieldNameArray[col].getCalSourceForm();
            
        } else {
            return "RECORD";
        }
    }   
      
    /**
     * {@inheritDoc}
     */
    @Override
    public TypeExpr getElementType(int col) {
        if (horizontalLayout) {
            return fieldTypeExprArray[col];
            
        } else {
            // Note: This will probably never be reached, since it is used for column header
            // icons, and we do not display them.
            
            return recordValueNode.getTypeExpr();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getNElements() {
        // Note: This is used only for determining size and focus,
        // and it is meant to return the number of columns
        
        if (horizontalLayout) {
            return fieldTypeExprArray.length;
            
        } else {
            return 1;
        }       
    }

    /**
     * {@inheritDoc}
     */
    public int getRowCount() {
        if (horizontalLayout) {
            return 1;
            
        } else {
            return fieldTypeExprArray.length;
        }
    }
    
    /**
     * @return type expression representing the types of each table row
     */
    public TypeExpr[] getRowElementTypeExprArray() {
        if (horizontalLayout) {
            return new TypeExpr[]{recordValueNode.getTypeExpr()};
            
        } else {
            return fieldTypeExprArray;
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object getValueAt(int row, int col) {
        if (horizontalLayout) {
            return recordValueNode.getValueAt(col);
            
        } else {
            return recordValueNode.getValueAt(row);
        }
    }
}