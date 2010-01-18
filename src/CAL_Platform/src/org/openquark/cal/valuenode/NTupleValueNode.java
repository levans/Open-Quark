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
 * NTupleValueNode.java
 * Created: June 15, 2001
 * By: Michael Cheng
 */
 
package org.openquark.cal.valuenode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.openquark.cal.compiler.DataConstructor;
import org.openquark.cal.compiler.FieldName;
import org.openquark.cal.compiler.RecordType;
import org.openquark.cal.compiler.SourceModel;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.compiler.io.InputPolicy;
import org.openquark.cal.compiler.io.OutputPolicy;
import org.openquark.util.UnsafeCast;


/**
 * A specialized ValueNode used to handle tuple record values.
 * Creation date: (15/06/01 11:44:36 AM)
 * @author Michael Cheng
 */
public class NTupleValueNode extends AbstractRecordValueNode {

    /**
     * A custom ValueNodeProvider for the NTupleValueNode.
     * @author Frank Worsley
     */
    public static class NTupleValueNodeProvider extends ValueNodeProvider<NTupleValueNode> {
        
        public NTupleValueNodeProvider(ValueNodeBuilderHelper builderHelper) {
            super(builderHelper);
        }
        
        /**
         * @see org.openquark.cal.valuenode.ValueNodeProvider#getValueNodeClass()
         */
        @Override
        public Class<NTupleValueNode> getValueNodeClass() {
            return NTupleValueNode.class;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public NTupleValueNode getNodeInstance(Object value, DataConstructor dataConstructor, TypeExpr typeExpr) {
            
            // Check for handleability.
            if (!typeExpr.isTupleType()) {
                return null;
            }
            
            if (value == null) {

                List<ValueNode> tupleValue = new ArrayList<ValueNode>();
                RecordType recordType = typeExpr.rootRecordType();
                   
                Map<FieldName, TypeExpr> hasFieldsMap = recordType.getHasFieldsMap();
                for (final TypeExpr fieldType : hasFieldsMap.values()) {
                
                    ValueNode newVN = getValueNodeBuilderHelper().getValueNodeForTypeExpr(fieldType);
                    
                    // Return null if a child value node couldn't be built.
                    if (newVN == null) {
                        return null;
                    }
                    
                    tupleValue.add(newVN);
                }
                
                value = tupleValue;
            }
            
            // value is a List of ValueNode
            List<ValueNode> listValue = UnsafeCast.asTypeOf(value, Collections.<ValueNode>emptyList());

            return new NTupleValueNode(listValue, typeExpr, getValueNodeBuilderHelper());
        }
    }
    
    /** The list that holds on to the value nodes for the tuple pieces. */
    private List<ValueNode> tupleValue;
    
    /** The number of tuple pieces, ie: the size of the tuple. */
    private final int numPieces;
    
    /**
     * List that holds onto the Java marshalled values in the tuple. The 0th element of the list is the 1st component of the tuple,
     * the 1st element of the list holds onto the second component of the tuple, and so on.
     */
    private List<?> outputTupleValue;
    
    private final ValueNode[] tupleElementValueNodes;
    
    /**
     * NTupleValueNode constructor.
     * @param tupleValue the list of value nodes for the tuple parameters. Size of list must match size of tuple.
     * @param typeExprParam the type expression for the value node
     * @param builderHelper
     */
    private NTupleValueNode(List<ValueNode> tupleValue, TypeExpr typeExprParam, ValueNodeBuilderHelper builderHelper) {

        super(typeExprParam);
        
        if (!typeExprParam.isTupleType()) {
            throw new IllegalArgumentException("given type expression is not a tuple type: " + typeExprParam);
        }

        if (tupleValue == null) {
            throw new NullPointerException();
        }
        
        RecordType recordType = typeExprParam.rootRecordType();
        Map<FieldName, TypeExpr> hasFieldsMap = recordType.getHasFieldsMap();
        
        // Set the number of pieces in this tuple so that we know when we're completed
        this.numPieces = hasFieldsMap.size();
        
        if (tupleValue.size() != numPieces) {
            throw new IllegalArgumentException("invalid number of values in list: " + tupleValue.size() + " -- tuple size: " + numPieces);
        }
            
        this.tupleValue = new ArrayList<ValueNode>(tupleValue);
        
        tupleElementValueNodes = new ValueNode[numPieces];
        int i = 0;
        for (final TypeExpr fieldTypeExpr : hasFieldsMap.values()) {
            tupleElementValueNodes[i] = builderHelper.getValueNodeForTypeExpr(fieldTypeExpr);
            ++i;
        }
    }

    /**
     * NTupleValueNode constructor.
     * @param tupleValue the list of value nodes for the tuple parameters. Size of list must match size of tuple.
     * @param outputTupleValue  List that holds onto the Java marshalled values in the tuple. The 0th element of the list is the 1st component of the tuple
     * @param typeExprParam the type expression for the value node
     * @param tupleElementValueNodes
     */
    private NTupleValueNode(List<ValueNode> tupleValue, List<?> outputTupleValue, TypeExpr typeExprParam, ValueNode[] tupleElementValueNodes) {

        super(typeExprParam);
        
        int tupleDimension = typeExprParam.getTupleDimension();
        if (tupleDimension == -1) {
            throw new IllegalArgumentException("given type expression is not a tuple type: " + typeExprParam);
        }

        if (tupleValue == null && outputTupleValue == null) {
            throw new NullPointerException();
        }
        
        // Set the number of pieces in this tuple so that we know when we're completed
        this.numPieces = tupleDimension;
        
        if ((tupleValue != null && tupleValue.size() != numPieces) && (outputTupleValue != null && outputTupleValue.size() != numPieces)) {
            throw new IllegalArgumentException("invalid number of values in list: " + tupleValue.size() + " -- tuple size: " + numPieces);
        }
        
        if (tupleValue != null) { 
            this.tupleValue = new ArrayList<ValueNode>(tupleValue);
        }
        this.outputTupleValue = outputTupleValue;
        
        this.tupleElementValueNodes = tupleElementValueNodes;
    }
    
    /**
     * Makes a copy of this ValueNode, but with another TypeExpr instance (of the same type).
     * This is a deep copy, with respect to value nodes and the associated type expression.
     * Note: if the new TypeExpr is a different type from the present TypeExpr, an error is thrown.
     * Creation date: (06/07/01 8:49:28 AM)
     * @param newTypeExpr the new type of the copied node.
     * @return ValueNode
     */
    @Override
    public NTupleValueNode copyValueNode(TypeExpr newTypeExpr) {

        checkCopyType(newTypeExpr); 
       
        List<ValueNode> tupleCopy = null;
        
        if (tupleValue != null) {
            Map<FieldName, TypeExpr> hasFieldsMap = newTypeExpr.rootRecordType().getHasFieldsMap(); 
            int tupleSize = getTupleSize();
            tupleCopy = new ArrayList<ValueNode>(tupleSize);
            int i = 0;
            for (final TypeExpr componentTypeExpr : hasFieldsMap.values()) {
                ValueNode elementVN = tupleValue.get(i);
                tupleCopy.add(elementVN.copyValueNode(componentTypeExpr));
                ++i;
            }
        }                      
        
        NTupleValueNode ntv = new NTupleValueNode(tupleCopy, outputTupleValue, newTypeExpr, tupleElementValueNodes);
        return ntv;
    }

    /**
     * Returns the source model representation of the expression represented by
     * this ValueNode.
     * 
     * @return SourceModel.Expr
     */
    @Override
    public SourceModel.Expr getCALSourceModel() {

        int tupleDim = tupleValue.size();
        SourceModel.Expr[] components = new SourceModel.Expr[tupleDim];

        for (int i = 0; i < tupleDim; i++) {
            components[i] = tupleValue.get(i).getCALSourceModel();
        }

        return SourceModel.Expr.Tuple.make(components);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsParametricValue() {
        if (tupleValue != null) {
            for (int i = 0, tupleDim = tupleValue.size(); i < tupleDim; i++) {
    
                ValueNode elementVN = tupleValue.get(i);
                if (elementVN.containsParametricValue()) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    @Override
    public List<ValueNode> getValue() {
        return Collections.unmodifiableList(tupleValue);
    }      
    
    /**
     * Returns the display text representation of the expression
     * represented by this ValueNode.
     * Creation date: (03/07/01 11:03:28 AM)
     * @return String
     */
    @Override
    public String getTextValue() {
       
        if (outputTupleValue != null) {
            return outputTupleValue.toString();
        }
               
        StringBuilder sbTuple = new StringBuilder("(");      

        for (int i = 0, tupleDim = tupleValue.size(); i < tupleDim; i++) {

            if (i > 0) {
                sbTuple.append(", ");
            }

            ValueNode elementVN = tupleValue.get(i);
            sbTuple.append(elementVN.getTextValue());
        }

        sbTuple.append(")");

        return sbTuple.toString();
    }    
    
    /**
     * @return int the size of the tuple. eg. for a 4-tuple, returns 4.
     */
    public int getTupleSize() {
        return numPieces;
    }
    
    /**
     * @param index the index of the value within the tuple.
     * @return the value node for the ith item in the tuple.
     */
    @Override
    public ValueNode getValueAt(int index) {
        return tupleValue.get(index);
    }
    
    /**
     * @param i the index for the value within the tuple.
     * @param valueNode the new value node for the ith item in the tuple.
     */
    @Override
    public void setValueNodeAt(int i, ValueNode valueNode) {
        tupleValue.set(i, valueNode);
    }           
    
    /**
     * @see ValueNode#transmuteValueNode(ValueNodeBuilderHelper, ValueNodeTransformer, TypeExpr)
     */
    @Override
    public ValueNode transmuteValueNode(ValueNodeBuilderHelper valueNodeBuilderHelper, ValueNodeTransformer valueNodeTransformer, TypeExpr newTypeExpr) {
        
        Class<? extends ValueNode> handlerClass = valueNodeBuilderHelper.getValueNodeClass(newTypeExpr);

        // If we don't need to change type expressions... Then we just basically copy the value
        if (getClass().equals(handlerClass)) {
            
            int currentTupleSize = getTupleSize();
            RecordType newRecordType = newTypeExpr.rootRecordType();
            Map<FieldName, TypeExpr> newRecordHasFieldsMap = newRecordType.getHasFieldsMap();
            
            List<ValueNode> newComponentList = new ArrayList<ValueNode>();

            int i = 0;
            for (final TypeExpr componentTypeExpr : newRecordHasFieldsMap.values()) {
                // Copy as many current args as we have. Provide default value nodes for the others.
                
                if (i < currentTupleSize) {
                    ValueNode componentVN = getValueAt(i);
                    newComponentList.add(componentVN.transmuteValueNode(valueNodeBuilderHelper, valueNodeTransformer, componentTypeExpr));
                
                } else {
                    newComponentList.add(valueNodeBuilderHelper.getValueNodeForTypeExpr(componentTypeExpr));
                }
                
                ++i;
            }
            
            return valueNodeBuilderHelper.buildValueNode(newComponentList, null, newTypeExpr);

        } else {
            return valueNodeTransformer.transform(valueNodeBuilderHelper, this, newTypeExpr);
        }
    }

    /**
     * Return an array of objects which are the values needed by the marshaller
     * described by 'getInputPolicy()'.
     * @return - an array of Java objects corresponding to the value represented by a value node instance.
     */
    @Override
    public Object[] getInputJavaValues() {
        
        // We simply gather up all the arguments required for the components in order.
        // These are the arguments expected by the custom input policy build by getInputPolicy()
        
        List<Object> argumentValues = new ArrayList<Object>();
        for (final ValueNode vn : tupleValue) {
            Object[] vals = vn.getInputJavaValues(); 
            if (vals != null) {
                argumentValues.addAll(Arrays.asList(vals));
            }
        }
        
        return argumentValues.toArray();
    }
    
    /**
     * Return an input policy which describes how to marshall a value represented
     * by a value node from Java to CAL.
     * @return - the input policy associated with ValueNode instance.
     */
    @Override
    public InputPolicy getInputPolicy() {
        
        // Instead of using Prelude.input as the input policy, we create a custom one that
        // simply applies the tuple constructor on the components, of the form:
        // (\arg_0 ... arg_N -> ( (<input policy for component 0> <args>) , ... , (<input policy for component k> <args>) ))
        
        int tupleDim = tupleValue.size();
        SourceModel.Expr[] components = new SourceModel.Expr[tupleDim];

        int argCount = 0;
        List<SourceModel.Parameter> paramsForMarshaler = new ArrayList<SourceModel.Parameter>();
        
        for (int i = 0; i < tupleDim; i++) {
            
            InputPolicy componentInputPolicy = tupleValue.get(i).getInputPolicy();
            
            int nComponentInputPolicyArgs = componentInputPolicy.getNArguments();            
            SourceModel.Expr[] componentInputPolicyArgs = new SourceModel.Expr[nComponentInputPolicyArgs + 1];
            componentInputPolicyArgs[0] = componentInputPolicy.getMarshaler();

            //this loop is from 1 as the first element is always the input policy itself
            for (int j = 1; j <= nComponentInputPolicyArgs; j++) {                
                String componentArg = "arg_" + (argCount++);
                
                paramsForMarshaler.add(SourceModel.Parameter.make(componentArg, false));
                componentInputPolicyArgs[j] = SourceModel.Expr.Var.makeUnqualified(componentArg);               
            }
            
            if (componentInputPolicyArgs.length >= 2) {
                components[i] = SourceModel.Expr.Application.make(componentInputPolicyArgs);
            } else {
                components[i] = componentInputPolicyArgs[0];
            }
        }

        SourceModel.Expr marshaler;
        if (paramsForMarshaler.isEmpty()) {
            marshaler = SourceModel.Expr.Tuple.make(components);
            
        } else {
            marshaler = SourceModel.Expr.Lambda.make(
                paramsForMarshaler.toArray(new SourceModel.Parameter[paramsForMarshaler.size()]),
                SourceModel.Expr.Tuple.make(components));
        }
        
        return InputPolicy.makeWithTypeAndMarshaler(getNonParametricType().toSourceModel().getTypeExprDefn(), marshaler, paramsForMarshaler.size());
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
     * Set a value which is the result of the marshaller described by
     * 'getOutputPolicy()'.
     * @param value - the java value
     */
    @Override
    public void setOutputJavaValue(Object value) {
        if (!(value instanceof List)) {
            throw new IllegalArgumentException("Error in NTupleValueNode.setOutputJavaValue: output must be an instance of Tuple, not an instance of: " + value.getClass().getName());
        }
        outputTupleValue = (List<?>)value;
        tupleValue = new ArrayList<ValueNode>();
        for (int i = 0; i < numPieces; ++i) {
            ValueNode vn = tupleElementValueNodes[i].copyValueNode();
            vn.setOutputJavaValue(outputTupleValue.get(i));
            tupleValue.add(vn);
        }
        outputTupleValue = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldName getFieldName(int i) {
        return getFieldNames().get(i);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FieldName> getFieldNames() {
        return ((RecordType)getTypeExpr()).getHasFieldNames();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeExpr getFieldTypeExpr(FieldName fieldName) {
        return ((RecordType)getTypeExpr()).getHasFieldsMap().get(fieldName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNFieldNames() {
        return numPieces;
    }
    
}
