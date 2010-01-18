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
 * ParametricValueNode.java
 * Creation date: (03/07/01 9:57:00 AM)
 * By: Michael Cheng
 */
package org.openquark.cal.valuenode;

import org.openquark.cal.compiler.DataConstructor;
import org.openquark.cal.compiler.SourceModel;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.compiler.io.InputPolicy;
import org.openquark.cal.compiler.io.OutputPolicy;


/**
 * A specialized ValueNode used to handle values having types such as 'a' and 'Num a => a' but not a specialization of it e.g. 'Array a'.
 * 
 * Creation date: (03/07/01 9:57:00 AM)
 * @author Michael Cheng
 */
public class ParametricValueNode extends ValueNode {

    /**
     * A custom ValueNodeProvider for the ParametricValueNode.
     * @author Frank Worsley
     */
    public static class ParametricValueNodeProvider extends ValueNodeProvider<ParametricValueNode> {
        
        public ParametricValueNodeProvider(ValueNodeBuilderHelper builderHelper) {
            super(builderHelper);
        }
        
        /**
         * @see org.openquark.cal.valuenode.ValueNodeProvider#getValueNodeClass()
         */
        @Override
        public Class<ParametricValueNode> getValueNodeClass() {
            return ParametricValueNode.class;
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
        public ParametricValueNode getNodeInstance(Object value, DataConstructor dataConstructor, TypeExpr typeExpr) {
            
            // a value can be represented as a parametric value node if its type is parametric, e.g. 'a' or 'Num a => a'
            // in other words, the type expression is just a type variable which may or may not be constrained.
            boolean isParametric = (typeExpr.rootTypeVar() != null);
            
            // Check for handleability.
            if (!isParametric) {
                return null;
            }
            
            return new ParametricValueNode(typeExpr);
        }
    }    
    
    /** The string that as the parameter for the error function used to represent this ParametricValueNode */
    public static final String INT_ERROR_MSG = "Unspecified value.";
    
    /**
     * Constructor for a new ParametricValueNode.
     * @param typeExpr the type expression of the value node. Must be a parametric type.
     */
    public ParametricValueNode(TypeExpr typeExpr) {
        super(typeExpr);
        
        // Just checking for a TypeVar is not enough. The ValueRunner may
        // instantiate a new ParametricValueNode whose type is [a] or (a, a, a).
        // This happens if a gem design with parametric value gems is loaded.
        // See the getResults() method in the ValueRunner.
        if (!typeExpr.isPolymorphic()) {
            throw new IllegalArgumentException("given type expression does not contain a parametric type: " + typeExpr);
        }
    }

    /**
     * Makes a copy of this ValueNode, but with another TypeExpr instance (of the same type).
     * This is a deep copy, with respect to value nodes and the associated type expression.
     * Note: if the new TypeExpr is a different type from the present TypeExpr, an error is thrown.
     * Creation date: (06/07/01 8:53:39 AM)
     * @param newTypeExpr the new type of the copied node.
     * @return ValueNode
     */
    @Override
    public ParametricValueNode copyValueNode(TypeExpr newTypeExpr) {
        checkCopyType(newTypeExpr);
        return new ParametricValueNode(newTypeExpr);
    }

    /**
     * Note: The source model for the parametric node is the error function. So it is actually runnable
     * @return SourceModel.Expr
     */
    @Override
    public SourceModel.Expr getCALSourceModel() {
        return SourceModel.Expr.makeErrorCall(INT_ERROR_MSG);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsParametricValue() {
        return true;
    }
    
    /**
     * Returns the display text representation of the expression represented by this ValueNode.
     * @return String
     */
    @Override
    public String getTextValue() {

        return "?";
    }
    
    @Override
    public Object getValue() {
        return this;
    }      
    
    /**
     * @see ValueNode#transmuteValueNode(ValueNodeBuilderHelper, ValueNodeTransformer, TypeExpr)
     */
    @Override
    public ValueNode transmuteValueNode(ValueNodeBuilderHelper valueNodeBuilderHelper, ValueNodeTransformer valueNodeTransformer, TypeExpr newTypeExpr) {
        // Eg: For ColourValueNode, or ListOfCharValueNode, or...
        // Also for foreign value nodes
        Class<? extends ValueNode> handlerClass = valueNodeBuilderHelper.getValueNodeClass(newTypeExpr);

        // If we don't need to change type expressions... Then we just basically copy the value
        if ((getClass().equals(handlerClass))) {
            return valueNodeBuilderHelper.buildValueNode(getValue(), null, newTypeExpr);
        } else {
            return valueNodeTransformer.transform(valueNodeBuilderHelper, this, newTypeExpr);
        }
    }

    /**
     * Return an input policy which describes how to marshall a value represented
     * by a value node from Java to CAL.
     * @return - the input policy associated with ValueNode instance.
     */
    @Override
    public InputPolicy getInputPolicy () {
        SourceModel.Expr marshaller = SourceModel.Expr.makeErrorCall(INT_ERROR_MSG);
        return InputPolicy.makeWithTypeAndMarshaler(getTypeExpr().toSourceModel().getTypeExprDefn(), marshaller, 0);
    }
    
    /**
     * Return an array of objects which are the values needed by the marshaller
     * described by 'getInputPolicy()'.
     * @return - an array of Java objects corresponding to the value represented by a value node instance.
     */
    @Override
    public Object[] getInputJavaValues() {
        return new Object[]{};
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
        throw new UnsupportedOperationException();
    }
    
}
