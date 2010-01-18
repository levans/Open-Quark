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
 * TupleTableModel.java
 * Created: Feb 28, 2001
 * By: Michael Cheng
 */

package org.openquark.gems.client.valueentry;

import java.util.Map;

import org.openquark.cal.compiler.FieldName;
import org.openquark.cal.compiler.RecordType;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.valuenode.NTupleValueNode;


/**
 * The table model for handling tuples.
 * Creation date: (28/02/01 1:15:42 PM)
 * @author Michael Cheng
 */
class TupleTableModel extends ValueEditorTableModel {
    
    private static final long serialVersionUID = -1795081418272379243L;

    /** The ith value of elementTypeArrayParam gives the type for the ith column in the array.*/
    private final TypeExpr[] elementTypeExprArray;

    private final NTupleValueNode nTupleValueNode;

    /**
     * TupleTableModel constructor.
     * @param nTupleValueNodeParam 
     * @param valueEditorHierarchyManager
     */
    public TupleTableModel(NTupleValueNode nTupleValueNodeParam, ValueEditorHierarchyManager valueEditorHierarchyManager) {
        super(valueEditorHierarchyManager.getValueEditorManager());
        nTupleValueNode = nTupleValueNodeParam;
       
        // Initialize elementTypeArray.
        RecordType recordType = nTupleValueNodeParam.getTypeExpr().rootRecordType();
        Map<FieldName, TypeExpr>  hasFieldsMap = recordType.getHasFieldsMap();
        int nElements = hasFieldsMap.size();
        elementTypeExprArray = new TypeExpr[nElements];
        int i = 0;
        for (final TypeExpr elementTypeExpr : hasFieldsMap.values()) {
            elementTypeExprArray[i] = elementTypeExpr;
            ++i;
        }      
    }
    
    @Override
    public int getColumnCount() {
        return elementTypeExprArray.length;
    }
    
    @Override
    public String getColumnName(int col) {
        return valueEditorManager.getTypeName(elementTypeExprArray[col]);
    }
    
    /**
     * Returns the elementTypeArray.
     * The ith value of elementTypeArrayParam gives the TypeExpr for the ith column in the array.
     * @return TypeExpr[]
     */
    public TypeExpr[] getElementTypeExprArray() {
        return elementTypeExprArray;
    }    
      
    @Override
    public TypeExpr getElementType(int col) {
        return elementTypeExprArray[col];       
    }
    
    @Override
    public int getNElements() {
        return elementTypeExprArray.length;             
    }

    public int getRowCount() {
        // Tuples can be considered as one row.
        return 1;
    }

    public Object getValueAt(int row, int col) {
        return nTupleValueNode.getValueAt(col);
    }
}