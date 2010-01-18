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
 * ForeignValueNode.java
 * Created: 4-Apr-2003
 * By: David Mosimann
 */
package org.openquark.cal.valuenode;

import org.openquark.cal.compiler.DataConstructor;
import org.openquark.cal.compiler.SourceModel;
import org.openquark.cal.compiler.SourceModelUtilities;
import org.openquark.cal.compiler.TypeConsApp;
import org.openquark.cal.compiler.TypeConstructor;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.compiler.io.InputPolicy;
import org.openquark.cal.compiler.io.OutputPolicy;


/**
 * A value node designed to hold onto foreign values.  The foreign values are held onto by an Object reference
 * which can be cast to the appropriate Java object by users of this class.  
 */
public final class ForeignValueNode extends OpaqueValueNode {
    
    /**
     * A custom ValueNodeProvider for the ForeignValueNode.
     * @author Frank Worsley
     */
    public static class ForeignValueNodeProvider extends ValueNodeProvider<ForeignValueNode> {
        
        public ForeignValueNodeProvider(ValueNodeBuilderHelper builderHelper) {
            super(builderHelper);
        }

        /**
         * @see org.openquark.cal.valuenode.ValueNodeProvider#getValueNodeClass()
         */
        @Override
        public Class<ForeignValueNode> getValueNodeClass() {
            return ForeignValueNode.class;
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
        public ForeignValueNode getNodeInstance(Object value, DataConstructor dataConstructor, TypeExpr typeExpr) {
            
            // Check for handleability.
            TypeConsApp typeConsApp = typeExpr.rootTypeConsApp();
            TypeConstructor typeCons = typeConsApp != null ? getPerspective().getTypeConstructor(typeConsApp.getName())
                                                                    : null;
            
            if (typeCons == null || typeCons.getForeignTypeInfo() == null) {
                return null;
            }
            
            return new ForeignValueNode(value, typeExpr);
        }
    }
    
    /**
     * ForeignValueNode constructor.
     * @param foreignValue the value of this value node.
     * @param typeExprParam the type expression of this value node. Must be a foreign type.
     */
    public ForeignValueNode(Object foreignValue, TypeExpr typeExprParam) {
        super(foreignValue, typeExprParam);
        
        TypeConsApp typeConsApp = typeExprParam.rootTypeConsApp();
        if (typeConsApp == null || !typeConsApp.usesForeignType()) {
            throw new IllegalArgumentException("type expression is not a foreign type: " + typeExprParam);
        }
    }

    /**
     * Returns the source model representation of the expression represented by
     * this ValueNode.
     * 
     * @return SourceModel.Expr
     * @throws UnsupportedOperationException
     *             foreign values don't have CAL textual representations as
     *             literals
     */
    @Override
    public SourceModel.Expr getCALSourceModel() {
        
        SourceModel.Expr expr =
            SourceModelUtilities.TextParsing.parseExprIntoSourceModel(String.valueOf(getValue()));
        
        if (expr == null) {
            // The string representation of the foreign value is not parsable as CAL
            // text, so for the purpose of allowing comparisons of such values by
            // ValueNode.sameValue(), we return the string representation of
            // the foreign value wrapped in a Prelude.error call. Because
            // the implementation of ValueNode.sameValue() implements equality comparison
            // by comparing the source text of the two value nodes in question,
            // by returning the specifically crafted call to error, we preserve
            // the intended semantics of the method for foreign value nodes.
            
            expr = SourceModel.Expr.makeErrorCall("Foreign value: '" + String.valueOf(getValue()) + "'");
        }
        return expr;
        
        //todoBI this should really be an unsupported operation, but it crashes the GemCutter otherwise when
        //running a gem with foreign arguments.
        //throw new UnsupportedOperationException("foreign values don't have CAL textual representations as literals");
    }
    
    /**
     * Makes a copy of this ValueNode, but with another TypeExpr instance (of the same type).
     * The foreign object is shared so both value nodes will refer to the same foreign object
     * @param newTypeExpr the new type of the copied node.
     * @return ValueNode
     */
    @Override
    public ForeignValueNode copyValueNode(TypeExpr newTypeExpr) {
        checkCopyType(newTypeExpr);
        return new ForeignValueNode(getValue(), newTypeExpr);
    }

    /**
     * Returns the display text representation of the expression.  Since we can't say much about a foreign
     * type we simply return String.valueOf() on the value represented by this ValueNode.
     * @return String
     */
    @Override
    public String getTextValue() {
        return String.valueOf(getValue());
    }

    /**
     * Return an array of objects which are the values needed by the marshaler
     * described by 'getInputPolicy()'.
     * @return - an array of Java objects corresponding to the value represented by a value node instance.
     */
    @Override
    public Object[] getInputJavaValues() {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Return an input policy which describes how to marshal a value represented
     * by a value node from Java to CAL.
     * @return - the input policy associated with ValueNode instance.
     */
    @Override
    public InputPolicy getInputPolicy() {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Return an output policy which describes how to marshal a value represented
     * by a value node from CAL to Java.
     * @return - the output policy associated with the ValueNode instance.
     */
    @Override
    public OutputPolicy getOutputPolicy() {
        return OutputPolicy.DEFAULT_OUTPUT_POLICY;
    }

}
