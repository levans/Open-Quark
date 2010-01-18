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
 * GemEntityValueNode.java
 * Creation date: Nov 7, 2003
 * By: Frank Worsley
 */
package org.openquark.cal.valuenode;

import org.openquark.cal.compiler.DataConstructor;
import org.openquark.cal.compiler.SourceModel;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.compiler.io.InputPolicy;
import org.openquark.cal.compiler.io.OutputPolicy;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.services.GemEntity;


/**
 * A value node that represents a function value corresponding to a named CAL function or
 * data constructor or class method.
 * @author Frank Worsley
 */
public class GemEntityValueNode extends ValueNode {

    /**
     * A custom ValueNodeProvider for the GemEntityValueNode.
     * @author Frank Worsley
     */
    public static class GemEntityValueNodeProvider extends ValueNodeProvider<GemEntityValueNode> {
        
        public GemEntityValueNodeProvider(ValueNodeBuilderHelper builderHelper) {
            super(builderHelper);
        }

        /**
         * @see org.openquark.cal.valuenode.ValueNodeProvider#getValueNodeClass()
         */
        @Override
        public Class<GemEntityValueNode> getValueNodeClass() {
            return GemEntityValueNode.class;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public GemEntityValueNode getNodeInstance(Object value, DataConstructor dataConstructor, TypeExpr typeExpr) {
            
            // Check for handleability.
            if (!typeExpr.isFunctionType()) {
                return null;
            }
            
            return new GemEntityValueNode((GemEntity) value, typeExpr);
        }
    }
    
    /** The entity that represents the function value of this value node. */
    private final GemEntity gemEntity;
    
    /** The message string for the internal error message if no function is specified. */
    public static final String INT_ERROR_MSG = "Unspecified function.";
    
    /**
     * Constructor for a new GemEntityValueNode.
     * @param gemEntity the entity for the gem that represents the function (this can be null)
     * @param typeExpr the type expression for the value node (must be of type function).
     */
    public GemEntityValueNode(GemEntity gemEntity, TypeExpr typeExpr) {
        super(typeExpr);
        
        // A function type can be parametric if you load a value gem that contained a
        // function value that was not specified. This can happen if you load a saved
        // gem design that contained such a gem. See the getResults() method in the ValueRunner.
        if (typeExpr.rootTypeConsApp() != null) {
            checkTypeConstructorName(typeExpr, CAL_Prelude.TypeConstructors.Function);
        }
        
        this.gemEntity = gemEntity;
    }
    
    /**
     * @see org.openquark.cal.valuenode.ValueNode#copyValueNode(org.openquark.cal.compiler.TypeExpr)
     */
    @Override
    public GemEntityValueNode copyValueNode(TypeExpr newTypeExpr) {
        checkCopyType(newTypeExpr);
        return new GemEntityValueNode(gemEntity, newTypeExpr);
    }

    /**
     * @return the source model representation of the expression represented by this ValueNode.
     * @see org.openquark.cal.valuenode.ValueNode#getCALSourceModel()
     */
    @Override
    public SourceModel.Expr getCALSourceModel() {
        return gemEntity != null
                ? SourceModel.Expr.makeGemCall(gemEntity.getName())
                : SourceModel.Expr.makeErrorCall(INT_ERROR_MSG);
    }
    
    /**
     * @see org.openquark.cal.valuenode.ValueNode#containsParametricValue()
     */
    @Override
    public boolean containsParametricValue() {
        return gemEntity == null || getTypeExpr().isPolymorphic();
    }

    /**
     * @see org.openquark.cal.valuenode.ValueNode#getValue()
     */
    @Override
    public Object getValue() {
        return gemEntity;
    }

    /**
     * @see org.openquark.cal.valuenode.ValueNode#getTextValue()
     */
    @Override
    public String getTextValue() {
        return gemEntity != null ? gemEntity.getName().getQualifiedName() : "?";
    }

    /**
     * @see org.openquark.cal.valuenode.ValueNode#transmuteValueNode(org.openquark.cal.valuenode.ValueNodeBuilderHelper, org.openquark.cal.valuenode.ValueNodeTransformer, org.openquark.cal.compiler.TypeExpr)
     */
    @Override
    public ValueNode transmuteValueNode(ValueNodeBuilderHelper valueNodeBuilderHelper, ValueNodeTransformer valueNodeTransformer, TypeExpr newTypeExpr) {

        if (newTypeExpr.sameType(getTypeExpr())) {
            return copyValueNode(newTypeExpr);
        }
        
        Class<? extends ValueNode> handlerClass = valueNodeBuilderHelper.getValueNodeClass(newTypeExpr);
        
        if (getClass().equals(handlerClass)) {
            
            if (gemEntity != null && TypeExpr.canPatternMatch(newTypeExpr, gemEntity.getTypeExpr(), valueNodeBuilderHelper.getPerspective().getWorkingModuleTypeInfo())) {
                return valueNodeBuilderHelper.buildValueNode(gemEntity, null, newTypeExpr);
            } else {
                return valueNodeBuilderHelper.buildValueNode(null, null, newTypeExpr);
            }
            
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
        SourceModel.Expr marshaller = (gemEntity == null)
                ? SourceModel.Expr.makeErrorCall(INT_ERROR_MSG)
                : SourceModel.Expr.makeGemCall(gemEntity.getName());
                
        return InputPolicy.makeWithTypeAndMarshaler(getTypeExpr().toSourceModel().getTypeExprDefn(), marshaller, 0);
    }
    
    /**
     * Return an array of objects which are the values needed by the marshaller
     * described by 'getInputPolicy()'.
     * @return - an array of Java objects corresponding to the value represented by a value node instance.
     */
    @Override
    public Object[] getInputJavaValues() {
        return new Object[0];
    }

    /**
     * Return an output policy which describes how to marshall a value represented
     * by a value node from CAL to Java.
     * @return - the output policy associated with the ValueNode instance.
     */
    @Override
    public OutputPolicy getOutputPolicy() {
        throw new UnsupportedOperationException();
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
