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
 * RecordValueNode.java
 * Created: July 5, 2004
 * By: Iulian Radu
 */

package org.openquark.cal.valuenode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.openquark.cal.compiler.DataConstructor;
import org.openquark.cal.compiler.FieldName;
import org.openquark.cal.compiler.RecordType;
import org.openquark.cal.compiler.SourceModel;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.compiler.io.InputPolicy;
import org.openquark.cal.compiler.io.OutputPolicy;
import org.openquark.util.UnsafeCast;


/**
 * Specialized AlgebraicValueNode for handling record values.
 * 
 * This object holds a list of value nodes whose elements correspond to values of the record fields,
 * along with a list of field names. 
 * 
 * @author Iulian Radu
 */
public class RecordValueNode extends AbstractRecordValueNode {

    /**
     * A custom ValueNodeProvider for the RecordValueNode.
     * @author Iulian Radu
     */
    public static class RecordValueNodeProvider extends ValueNodeProvider<RecordValueNode> {
        
        public RecordValueNodeProvider(ValueNodeBuilderHelper builderHelper) {
            super(builderHelper);
        }

        /**
         * @see org.openquark.cal.valuenode.ValueNodeProvider#getValueNodeClass()
         */
        @Override
        public Class<RecordValueNode> getValueNodeClass() {
            return RecordValueNode.class;
        }
        
        /**
         * @see org.openquark.cal.valuenode.ValueNodeProvider#isSpecialLiteralizedValueNode()
         */
        @Override
        public boolean isSpecialLiteralizedValueNode() {
            return false;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public RecordValueNode getNodeInstance(Object value, DataConstructor dataConstructor, TypeExpr typeExpr) {

            RecordType recordType = typeExpr.rootRecordType();
            
            if (recordType == null) {
                // This expression is not a record type, so this provides does not handle it
                return null;
            }
            
            List<FieldName> fieldNames = recordType.getHasFieldNames();
            
            // value is either a List of ValueNode or null
            List<ValueNode> listValue = UnsafeCast.asTypeOf(value, Collections.<ValueNode>emptyList());

            if (listValue == null) {
                // Create default value nodes for the fields
                
                int arity = fieldNames.size();
                listValue = new ArrayList<ValueNode>(arity);
                for (int i = 0 ; i < arity; i++) {
                
                    TypeExpr argType = recordType.getHasFieldType(fieldNames.get(i));
                    ValueNode valueNode = getValueNodeBuilderHelper().getValueNodeForTypeExpr(argType);
                    if (valueNode == null) {
                        // Cannot create a node represented, thus cannot create this node
                        return null;
                    }
                    listValue.add(valueNode);
                }
            }
            
            return new RecordValueNode(recordType, listValue, fieldNames);
        }
    }
    
    /** Names of the record fields, sorted via the FieldName ordering. */
    private final List<FieldName> fieldNames;
    
    /** Value nodes of the record fields. fieldNodes[i] is the node for field fieldNames[i] */
    private final List<ValueNode> fieldNodes;
    
    /**
     * Constructor
     * 
     * @param recordType type expression of the record
     * @param fieldNodes list of record field values 
     * @param fieldNames list of record field names corresponding to values, sorted in the FieldName ordering.
     */
    RecordValueNode(TypeExpr recordType, List<ValueNode> fieldNodes, List<FieldName> fieldNames) {
        super(recordType);
        if ((fieldNodes == null) || (fieldNames == null)) {
            throw new NullPointerException();
        }
        if (recordType.rootRecordType() == null) {
            throw new IllegalArgumentException();
        }
        
        this.fieldNodes = fieldNodes;
        this.fieldNames = fieldNames;
    }
    
    /**
     * @see org.openquark.cal.valuenode.ValueNode#getValue()
     */
    @Override
    public Object getValue() {
        return new ArrayList<ValueNode>(fieldNodes);
    }
    
    /**
     * Retrieve the value node representing the nth field in the record.
     * Note: Fields are indexed alphabetically 
     *  
     * @param n number of field
     * @return ValueNode
     */
    @Override
    public ValueNode getValueAt(int n) {
        return fieldNodes.get(n);
    }
    
    /**
     * @return the number of fields that the record is asserted to have. 
     */
    @Override
    public int getNFieldNames() {
        return fieldNames.size();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public FieldName getFieldName(int i) {
        return fieldNames.get(i);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<FieldName> getFieldNames() {
        return fieldNames;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public TypeExpr getFieldTypeExpr(FieldName fieldName) {
        return fieldNodes.get(fieldNames.indexOf(fieldName)).getTypeExpr();
    }
        
    /**
     * {@inheritDoc}
     */
    @Override
    public void setValueNodeAt(int i, ValueNode valueNode) {
        fieldNodes.set(i, valueNode);
    } 

    /**
     * Makes a copy of this ValueNode, but with another TypeExpr instance (of the same type).
     * This is a deep copy, with respect to value nodes and the associated type expression.
     * Note: if the new TypeExpr is a different type from the present TypeExpr, an error is thrown.
     * @param newTypeExpr the new type of the copied node.
     * @return ValueNode
     */
    @Override
    public RecordValueNode copyValueNode(TypeExpr newTypeExpr) {
    
        checkCopyType(newTypeExpr);

        // Copy field nodes

        int fieldCount = fieldNames.size();
        List<ValueNode> newFieldNodes = new ArrayList<ValueNode>(fieldCount);
        for (int i = 0; i < fieldCount; i++) {
            ValueNode fieldVN = fieldNodes.get(i);
            newFieldNodes.add(fieldVN.copyValueNode());
        }
        
        RecordValueNode newNode = new RecordValueNode(newTypeExpr, newFieldNodes, fieldNames);
        return newNode;
    }
    
    /**
     * Returns the source model representation of the expression represented by
     * this ValueNode. This creates a record constructor from the contained
     * fields.
     * 
     * Ex: for a record with fields "age" and "height" with values 1.0 and 2.0,
     *     this produces "{age = 1.0, height = 2.0}"
     * 
     * @return SourceModel.Expr
     */
    @Override
    public SourceModel.Expr getCALSourceModel() {

        int numFields = fieldNodes.size();
        SourceModel.Expr.Record.FieldModification[] fieldValuePairs = new SourceModel.Expr.Record.FieldModification[numFields];

        for (int i = 0; i < numFields; i++) {
            fieldValuePairs[i] =
                SourceModel.Expr.Record.FieldModification.Extension.make(
                    SourceModel.Name.Field.make(fieldNames.get(i)),
                    fieldNodes.get(i).getCALSourceModel());
        }

        return SourceModel.Expr.Record.make(null, fieldValuePairs);
    }
    
    /**
     * Returns the text representation of the expression represented by this ValueNode.
     * This produces a similar output to getCALValue, except getting text values for each field node.
     * @return String
     */
    @Override
    public String getTextValue() {

        StringBuilder calValue = new StringBuilder("{");
        for (int i = 0, n = fieldNodes.size(); i < n; i++) {
            if (i > 0) {
                calValue.append(", ");
            }
            calValue.append(fieldNames.get(i).getCalSourceForm()).append( " = ").append(fieldNodes.get(i).getTextValue());
        }
        calValue.append("}");
        return calValue.toString();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsParametricValue() {
        return getTypeExpr().isPolymorphic();
    }
    
    /**
     * Return an input policy which describes how to marshall a value represented
     * by a value node from Java to CAL.
     * @return - the input policy associated with ValueNode instance.
     */
    @Override
    public InputPolicy getInputPolicy () {
        
        // Instead of using Prelude.input as the input policy, we create a custom one that
        // simply applies the record constructor on the fields, of the form:
        // (\arg_0 ... arg_N -> { <field 0 name> :: (<input policy for field 0> <args>) , ... , <field k name> :: (<input policy for field k> <args>) })
        
        int nFields = fieldNodes.size();
        SourceModel.Expr.Record.FieldModification[] fieldValuePairs = new SourceModel.Expr.Record.FieldModification[nFields];

        int argCount = 0;
        List<SourceModel.Parameter> paramsForMarshaler = new ArrayList<SourceModel.Parameter>();
        
        for (int i = 0; i < nFields; i++) {
            
            InputPolicy fieldInputPolicy = fieldNodes.get(i).getInputPolicy();
            
            int nFieldInputPolicyArgs = fieldInputPolicy.getNArguments();
                       
            SourceModel.Expr[] fieldInputPolicyArgs = new SourceModel.Expr[nFieldInputPolicyArgs + 1];
            fieldInputPolicyArgs[0] = fieldInputPolicy.getMarshaler();

            //this loop is from 1 as the first element is always the input policy itself
            for (int j = 1; j <= nFieldInputPolicyArgs; j++) {
                String fieldArg = "arg_" + (argCount++);
                
                paramsForMarshaler.add(SourceModel.Parameter.make(fieldArg, false));
                fieldInputPolicyArgs[j] = SourceModel.Expr.Var.makeUnqualified(fieldArg);               
            }
            
            final SourceModel.Expr fieldValue;
            if (fieldInputPolicyArgs.length >= 2) {
                fieldValue = SourceModel.Expr.Application.make(fieldInputPolicyArgs);
            } else {
                fieldValue = fieldInputPolicyArgs[0];
            }
        

            fieldValuePairs[i] = SourceModel.Expr.Record.FieldModification.Extension.make(SourceModel.Name.Field.make(fieldNames.get(i)), fieldValue);
        }

        SourceModel.Expr marshaler;
        if (paramsForMarshaler.isEmpty()) {
            marshaler = SourceModel.Expr.Record.make(null, fieldValuePairs);
            
        } else {
            marshaler = SourceModel.Expr.Lambda.make(
                paramsForMarshaler.toArray(new SourceModel.Parameter[paramsForMarshaler.size()]),
                SourceModel.Expr.Record.make(null, fieldValuePairs));
        }
                
        return InputPolicy.makeWithTypeAndMarshaler(
            getNonParametricType().toSourceModel().getTypeExprDefn(), 
            marshaler, paramsForMarshaler.size());
    }
    
    /**
     * Return an array of objects which are the values needed by the marshaler
     * described by 'getInputPolicy()'.
     * 
     * @return - an array of Java objects corresponding to the value represented by a value node instance.
     */
    @Override
    public Object[] getInputJavaValues() {
        
        // We simply gather up all the arguments required for the components in order.
        // These are the arguments expected by the custom input policy build by getInputPolicy()
        
        List<Object> argumentValues = new ArrayList<Object>();
        for (final ValueNode vn : fieldNodes) {
            Object[] vals = vn.getInputJavaValues(); 
            if (vals != null) {
                argumentValues.addAll(Arrays.asList(vals));
            }
        }
        
        return argumentValues.toArray();
        
    }
    
    /**
     * Return an output policy which describes how to marshall a value represented
     * by a value node from CAL to Java.
     * @return - the output policy associated with the ValueNode instance.
     */
    @Override
    public OutputPolicy getOutputPolicy() {
        return OutputPolicy.DEFAULT_OUTPUT_POLICY;
    }
    
    /**
     * Set a value which is the result of the marshaller described by 'getOutputPolicy()'.
     * 
     * The output value for a record is a java.util.List whose elements correspond to input
     * values of the record fields (ordered alphabetically). 
     * 
     * @param value - the java value
     */
    @Override
    public void setOutputJavaValue(Object value) {
        if (!(value instanceof List)) {
            throw new IllegalArgumentException("Error in RangeValueNode.setOutputJavaValue: output must be an instance of List, not an instance of: " + value.getClass().getName());
        }
        
        List<?> fieldNodesList = (List<?>)value;
        for (int i = 0, n = fieldNodesList.size(); i < n; i++) {
            fieldNodes.get(i).setOutputJavaValue(fieldNodesList.get(i));
        }
    }
    
    /**
     * Transmutes the record value node. This creates a new value node which results from
     * converting the current node's type expression to the specified type expression.
     * 
     * If transmuting to a record type expression, this method creates value nodes for record fields
     * which are not already contained in this node, and transmutes fields common to both records
     * 
     * Ex: Transmuting the record (r\name,r\width)=>{r|name:String,width::Double} where width = 11.0, name="Ana" 
     *     to the new type (r\height, r\name, r\width)=>{r|height::Double, name::String, width::Long} produces a 
     *     record value node having the new type specified, a new value node height, a copied node name = "Ana", 
     *     and transmuted value node width = 11.  
     *  
     * @see org.openquark.cal.valuenode.ValueNode#transmuteValueNode(org.openquark.cal.valuenode.ValueNodeBuilderHelper, org.openquark.cal.valuenode.ValueNodeTransformer, org.openquark.cal.compiler.TypeExpr)
     */
    @Override
    public ValueNode transmuteValueNode(ValueNodeBuilderHelper valueNodeBuilderHelper, ValueNodeTransformer valueNodeTransformer, TypeExpr newTypeExpr) {
        Class<? extends ValueNode> handlerClass = valueNodeBuilderHelper.getValueNodeClass(newTypeExpr);

        if ((getClass().equals(handlerClass))) {
            
            RecordType recordType = newTypeExpr.rootRecordType();
            List<FieldName> newFieldNames = recordType.getHasFieldNames();
            List<ValueNode> newFieldNodes = new ArrayList<ValueNode>(newFieldNames.size());
            
            // Transmute all fields which have equal names, and create those which do not
            for (final FieldName newFieldName : newFieldNames) {
                ValueNode newFieldNode;
                
                TypeExpr newFieldType = recordType.getHasFieldType(newFieldName);
                int nameIndex = fieldNames.indexOf(newFieldName);
                if (nameIndex != -1) {
                    
                    // The record field in the new type is contained in our record
                    ValueNode oldFieldNode = this.fieldNodes.get(nameIndex);
                        
                    // Transmute field to copy/change type
                    newFieldNode = oldFieldNode.transmuteValueNode(
                        valueNodeBuilderHelper, 
                        valueNodeTransformer, 
                        newFieldType);
                    
                } else {
                    
                    // Field not contained in our record; ask node builder to create a new node
                    newFieldNode = valueNodeBuilderHelper.getValueNodeForTypeExpr(newFieldType);
                }
                
                newFieldNodes.add(newFieldNode);
            }
                
            // Now build the record with the new type expression and value nodes
            ValueNode vn = valueNodeBuilderHelper.buildValueNode(newFieldNodes, null, newTypeExpr);
            return vn;
            
        } else {
            return valueNodeTransformer.transform(valueNodeBuilderHelper, this, newTypeExpr);
        }
    }
    
}