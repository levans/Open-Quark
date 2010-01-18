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
 * AbstractRecordValueNode.java
 * Creation date: Oct 4, 2006.
 * By: Edward Lam
 */
package org.openquark.cal.valuenode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openquark.cal.compiler.FieldName;
import org.openquark.cal.compiler.RecordType;
import org.openquark.cal.compiler.TypeExpr;



/**
 * Abstract AlgebraicValueNode for handling values whose type expr is a RecordType.
 * 
 * @author Edward Lam
 */
public abstract class AbstractRecordValueNode extends AlgebraicValueNode {
    
    /**
     * Constructor for an AbstractRecordValueNode.
     * @param typeExpr the TypeExpr of the data value which this ValueNode represents.
     */
    public AbstractRecordValueNode(TypeExpr typeExpr) {
        super(typeExpr);
    }

    /**
     * Retrieve the value node representing the nth field in the record.
     * Note: Fields are indexed alphabetically 
     *  
     * @param n number of field
     * @return ValueNode
     */
    public abstract ValueNode getValueAt(int n);
    
    /**
     * @return the number of fields that the record is asserted to have. 
     */
    public abstract int getNFieldNames();
    
    /**
     * Get the name of the ith field in the record
     * Note: Fields are indexed by the ordering on the FieldName class.
     * 
     * @param i index of field to retrieve
     * @return FieldName
     */
    public abstract FieldName getFieldName(int i);
    
    /**
     * @return of record field names
     */
    public abstract List<FieldName> getFieldNames();
    
    /**
     * Get the type expression of the ith field
     * @param fieldName
     * @return TypeExpr
     */
    public abstract TypeExpr getFieldTypeExpr(FieldName fieldName);
    
    /**
     * Set the value node of the ith field.
     * @param i the index for the field within the record.
     * @param valueNode the new value node for the ith field in the record.
     */
    public abstract void setValueNodeAt(int i, ValueNode valueNode);

    /**
     * Renames a field of this value node.
     * 
     * This function transmutes the record value node to a new type which contains the new field but
     * not the old. The value of this field is preserved in the process.
     * 
     * Ex: Rename field "age" to "height" in record {age=1.0, code="Ras"} produces record {code="Ras", height=1.0}
     * 
     * @param fieldName name of field to rename
     * @param newFieldName new name of field
     * @param builderHelper
     * @param transformer
     * @return RecordValueNode with the field renamed
     */
    public AbstractRecordValueNode renameField(FieldName fieldName, FieldName newFieldName, ValueNodeBuilderHelper builderHelper, ValueNodeTransformer transformer) {

        // Create the new record type.
        RecordType newRecordType = getRecordTypeForRenamedField(getTypeExpr(), fieldName, newFieldName);
        
        // Create new record value node by transmuting ourselves to the new record type
        
        AbstractRecordValueNode newRecordNode = (RecordValueNode)this.transmuteValueNode(builderHelper, transformer, newRecordType);
        
        // Now, the new record value node contains a brand new node for the new field. 
        // We will now place the old field value in this node.
        
        ValueNode oldFieldNode = getValueAt(getFieldNames().indexOf(fieldName));
        newRecordNode.setValueNodeAt(newRecordNode.getFieldNames().indexOf(newFieldName), oldFieldNode.copyValueNode());
        
        return newRecordNode;
    }
    

    /**
     * this returns the type of the record as a non parametric type.
     * It is used for the value node input policies, as there is no reason to input a parametric input
     * @return non parametric record type
     */
    protected final TypeExpr getNonParametricType() {
        return getNonParametricType( getTypeExpr());
    }

    /**
     * converts all records types - including nested records to non parametric version
     * @param typeExpr the type to convert
     * @return non parametric version of the type
     */
    private TypeExpr getNonParametricType(TypeExpr typeExpr) {
        if (typeExpr instanceof RecordType) {
            Map<FieldName, TypeExpr> fieldsMap = ((RecordType)typeExpr).getHasFieldsMap();
            Map<FieldName, TypeExpr> newFieldsMap = new HashMap<FieldName, TypeExpr>();
            for(final Map.Entry<FieldName, TypeExpr> entry : fieldsMap.entrySet()) {
                newFieldsMap.put(entry.getKey(), getNonParametricType(entry.getValue()));
            }
            
            return TypeExpr.makeNonPolymorphicRecordType(newFieldsMap);

        } else {
            return typeExpr;
        }
    }

    /**
     * Create a new record type from the current type, 
     * but having extra field newFieldName, and missing the field oldFieldName. 
     * @param typeExpr the record type on which the new type will be based.
     * @param oldFieldName the field name to be replaced.
     * @param newFieldName the field name with which to replace the old field name.
     * @return the new record type.
     */
    public static RecordType getRecordTypeForRenamedField(TypeExpr typeExpr, FieldName oldFieldName, FieldName newFieldName) {

        Map<FieldName, TypeExpr> fieldNamesToTypeMap = new HashMap<FieldName, TypeExpr>();
        List<FieldName> existingFields = typeExpr.rootRecordType().getHasFieldNames();
        for (final FieldName existingFieldName : existingFields) {
            TypeExpr existingFieldType = typeExpr.rootRecordType().getHasFieldType(existingFieldName);

            fieldNamesToTypeMap.put(existingFieldName, existingFieldType);
        }
        
        TypeExpr fieldTypeExpr = fieldNamesToTypeMap.remove(oldFieldName);
        fieldNamesToTypeMap.put(newFieldName, fieldTypeExpr);

        return TypeExpr.makeNonPolymorphicRecordType(fieldNamesToTypeMap);
    }

}
