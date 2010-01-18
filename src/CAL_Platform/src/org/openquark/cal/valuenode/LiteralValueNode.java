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
 * LiteralValueNode.java
 * Creation date: (04/06/01 8:23:22 AM)
 * By: Michael Cheng
 */
package org.openquark.cal.valuenode;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.openquark.cal.compiler.DataConstructor;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.compiler.io.InputPolicy;
import org.openquark.cal.compiler.io.OutputPolicy;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;


/**
 * LiteralValueNode is a specialized ValueNode used to represent values having a built-in
 * opaque type in CAL as a Java object. Currently these are the types:
 * Boolean, Char, Byte, Short, Int, Long, Float, Double, and String.
 * 
 * Creation date: (04/06/01 8:23:22 AM)
 * @author Michael Cheng
 */
public final class LiteralValueNode extends OpaqueValueNode {

    private static final BigDecimal BIG_DECIMAL_ZERO = new BigDecimal(0);
    
    /**
     * A custom ValueNodeProvider for the LiteralValueNode.
     * @author Frank Worsley
     */
    public static class LiteralValueNodeProvider extends ValueNodeProvider<LiteralValueNode> {

        public LiteralValueNodeProvider(ValueNodeBuilderHelper builderHelper) {
            super(builderHelper);
        }

        /**
         * @see org.openquark.cal.valuenode.ValueNodeProvider#getValueNodeClass()
         */
        @Override
        public Class<LiteralValueNode> getValueNodeClass() {
            return LiteralValueNode.class;
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
        public LiteralValueNode getNodeInstance(Object value, DataConstructor dataConstructor, TypeExpr typeExpr) {
            
            // Check for handleability.
            if (!isLiteralType(typeExpr)) {
                return null;
            }
            
            return new LiteralValueNode(value, dataConstructor, typeExpr);
        }
        
        /**
         * @param typeExpr the type expression to check
         * @return whether the type expression is a literal type
         */
        private static boolean isLiteralType(TypeExpr typeExpr) {
                        
            return typeExpr.isNonParametricType(CAL_Prelude.TypeConstructors.Boolean) ||
                   typeExpr.isNonParametricType(CAL_Prelude.TypeConstructors.Char) ||
                   typeExpr.isNonParametricType(CAL_Prelude.TypeConstructors.Byte) ||
                   typeExpr.isNonParametricType(CAL_Prelude.TypeConstructors.Short) ||
                   typeExpr.isNonParametricType(CAL_Prelude.TypeConstructors.Int) ||
                   typeExpr.isNonParametricType(CAL_Prelude.TypeConstructors.Integer) ||
                   typeExpr.isNonParametricType(CAL_Prelude.TypeConstructors.Decimal) ||
                   typeExpr.isNonParametricType(CAL_Prelude.TypeConstructors.Long) ||
                   typeExpr.isNonParametricType(CAL_Prelude.TypeConstructors.Float) ||
                   typeExpr.isNonParametricType(CAL_Prelude.TypeConstructors.Double) ||
                   typeExpr.isNonParametricType(CAL_Prelude.TypeConstructors.String);
        }
    }    
    
    /**
     * Constructs a new LiteralValueNode.
     * @param literalValue the literal value of the value node
     * @param dataConstructor a data constructor for a literal value (currently only TRUE or FALSE is valid)
     * @param typeExpr the TypeExpr of the data value represented by this LiteralValueNode
     */
    private LiteralValueNode(Object literalValue, DataConstructor dataConstructor, TypeExpr typeExpr) {
        super(getDefaultLiteralValue(literalValue, dataConstructor, typeExpr), typeExpr);
        
        if (!LiteralValueNodeProvider.isLiteralType(typeExpr)) {
            throw new IllegalArgumentException("the given type expression is not a literal type: " + typeExpr);
        }
    }
    
    /**
     * Constructs a new LiteralValueNode.
     * @param literalValue the literal value of the value node
     * @param typeExpr the TypeExpr of the data value represented by this LiteralValueNode
     */
    public LiteralValueNode(Object literalValue, TypeExpr typeExpr) {
        this(literalValue, null, typeExpr);
    }
    
    /**
     * Get the default literal value for the given args.
     *   This is a helper for the LiteralValueNode constructor, which must other constructor invocations on the first line.
     * @param literalValue the literal value, if any.
     * @param dataConstructor the data constructor.
     * @param typeExpr the type expr of the literal
     * @return Object the default literal value.
     */
    private static Object getDefaultLiteralValue(Object literalValue, DataConstructor dataConstructor, TypeExpr typeExpr) {
        if (literalValue != null) {
            return literalValue;
        }
        
        if (dataConstructor == null) {
            return LiteralValueNode.getDefaultValue(typeExpr);
        }
        
        // must be a Boolean data constructor
        QualifiedName dcName = dataConstructor.getName();
        if (dcName.equals(CAL_Prelude.DataConstructors.True)) {
            return Boolean.TRUE;
        } else if (dcName.equals(CAL_Prelude.DataConstructors.False)) {
            return Boolean.FALSE;
        } 
        
        throw new IllegalArgumentException("Can't get literal value for given args.");
    }
    
    /**
     * Get the default literal value for a given type expr.
     * @param typeExpr
     * @return Object
     */
    private static Object getDefaultValue(TypeExpr typeExpr) {

        if (typeExpr.isNonParametricType(CAL_Prelude.TypeConstructors.Boolean)) {

            return Boolean.FALSE;

        } else if (typeExpr.isNonParametricType(CAL_Prelude.TypeConstructors.Char)) {

            return Character.valueOf(' ');

        } else if (typeExpr.isNonParametricType(CAL_Prelude.TypeConstructors.Byte)) {

            return Byte.valueOf((byte)0);

        } else if (typeExpr.isNonParametricType(CAL_Prelude.TypeConstructors.Short)) {

            return Short.valueOf((short)0);

        } else if (typeExpr.isNonParametricType(CAL_Prelude.TypeConstructors.Int)) {

            return Integer.valueOf(0);

        } else if (typeExpr.isNonParametricType(CAL_Prelude.TypeConstructors.Long)) {

            return Long.valueOf(0);

        } else if (typeExpr.isNonParametricType(CAL_Prelude.TypeConstructors.Float)) {

            return Float.valueOf(0);

        } else if (typeExpr.isNonParametricType(CAL_Prelude.TypeConstructors.Double)) {

            return Double.valueOf(0);

        } else if (typeExpr.isNonParametricType(CAL_Prelude.TypeConstructors.String)) {

            return "";

        } else if (typeExpr.isNonParametricType(CAL_Prelude.TypeConstructors.Integer)) {
            
            return BigInteger.ZERO;
            
        } else if (typeExpr.isNonParametricType(CAL_Prelude.TypeConstructors.Decimal)) {
            
            return BIG_DECIMAL_ZERO;
            
        } else {

            throw new IllegalArgumentException("Literal values must be of type Boolean, Char, Byte, Short, Int, Integer, Decimal, Long, Float, Double or String.");
        }
    }
    
    /**
     * Makes a copy of this ValueNode, but with another TypeExpr instance (of the same type).
     * This is a deep copy, with respect to value nodes and the associated type expression.
     * Note: if the new TypeExpr is a different type from the present TypeExpr, an error is thrown.
     * Creation date: (06/07/01 8:57:11 AM)
     * @param newTypeExpr the new type of the copied node.
     * @return ValueNode
     */
    @Override
    public LiteralValueNode copyValueNode(TypeExpr newTypeExpr) {

        checkCopyType(newTypeExpr);
        return new LiteralValueNode(getValue(), newTypeExpr);
    }

    /**
     * Returns the Boolean value.
     * Note: Only call this method if the type is Boolean.
     * Creation date: (13/06/01 3:37:42 PM)
     * @return Boolean
     */
    public Boolean getBooleanValue() {

        return (Boolean) getValue();
    }
    
    /**
     * Returns the source model representation of the expression represented by
     * this ValueNode.
     * 
     * @return SourceModel.Expr
     */
    @Override
    public SourceModel.Expr getCALSourceModel() {

        TypeExpr typeExpr = getTypeExpr();

        if (typeExpr.isNonParametricType(CAL_Prelude.TypeConstructors.Boolean)) {
                        
            return SourceModel.Expr.makeBooleanValue(getBooleanValue().booleanValue());
           
        } else if (typeExpr.isNonParametricType(CAL_Prelude.TypeConstructors.Char)) {
            
            return SourceModel.Expr.Literal.Char.make(getValue().toString().charAt(0));           
            
        } else if (typeExpr.isNonParametricType(CAL_Prelude.TypeConstructors.Byte)) {
            
            return SourceModel.Expr.makeByteValue(((Byte)getValue()).byteValue());
            
        } else if (typeExpr.isNonParametricType(CAL_Prelude.TypeConstructors.Short)) {
            
            return SourceModel.Expr.makeShortValue(((Short)getValue()).shortValue());
                                        
        } else if (typeExpr.isNonParametricType(CAL_Prelude.TypeConstructors.Int)) {

            // TEMP: For now, we need to do this to ensure that the value is being interpreted as an Int and not Num.
            return SourceModel.Expr.makeIntValue(((Integer)getValue()).intValue());
            
        } else if (typeExpr.isNonParametricType(CAL_Prelude.TypeConstructors.Long)) {
            
            // TEMP: For now, we need to do this to ensure that the value is being interpreted as an Long and not Num.            
            return SourceModel.Expr.makeLongValue(((Long)getValue()).longValue());
            
        } else if (typeExpr.isNonParametricType(CAL_Prelude.TypeConstructors.Float)) {
            
            return SourceModel.Expr.makeFloatValue(((Float)getValue()).floatValue());
                               
        } else if (typeExpr.isNonParametricType(CAL_Prelude.TypeConstructors.Double)) {
       
            return SourceModel.Expr.makeDoubleValue(getDoubleValue().doubleValue());
               
        } else if (typeExpr.isNonParametricType(CAL_Prelude.TypeConstructors.String)) {
            
            return SourceModel.Expr.makeStringValue(getValue().toString());  
                      
        } else if (typeExpr.isNonParametricType(CAL_Prelude.TypeConstructors.Integer)) {
            
            return SourceModel.Expr.makeIntegerValue((BigInteger)getValue());
            
        } else if (typeExpr.isNonParametricType(CAL_Prelude.TypeConstructors.Decimal)) {
            return CAL_Prelude.Functions.stringToDecimal(getValue().toString());
            
        } else {
            
            throw new IllegalStateException("invalid type for a literal value");
        }
    }

    /**
     * Returns the Character value.
     * Note: Only call this method if the type is Character.
     * Creation date: (13/06/01 3:33:47 PM)
     * @return Character
     */
    public Character getCharacterValue() {

        return (Character) getValue();
    }
    
    /**
     * Returns the Integer value.
     * Note: Only call this method if the type is Integer.
     * Creation date: (13/06/01 3:24:36 PM)
     * @return Integer
     */
    public Integer getIntegerValue() {

        return (Integer) getValue();
    }
    
    /**
     * Returns the Double value.
     * Note: Only call this method if the type is Double.
     * Creation date: (13/06/01 3:31:05 PM)
     * @return Double
     */
    public Double getDoubleValue() {

        return (Double) getValue();
    }
    
    /**
     * Returns the String value.
     * Note: Only call this method if the type is Prelude.String.
     * @return String
     */
    public String getStringValue() {
        
        return (String) getValue();
    }
    
    /**
     * Returns the display text representation of the expression represented by this ValueNode.
     * @return String
     */
    @Override
    public String getTextValue() {

        String textValue = null;
        
        TypeExpr typeExpr = getTypeExpr();
       
        if (typeExpr.isNonParametricType(CAL_Prelude.TypeConstructors.Boolean)) {
            textValue = getBooleanValue().booleanValue() ? "True" : "False";
                       
        } else if (typeExpr.isNonParametricType(CAL_Prelude.TypeConstructors.String)) {
            // Will need to get rid of the carriage returns (since they will not be nicely displayed).
            textValue = getStringValue().replace('\n', ListOfCharValueNode.CHAR_RETURN_REPLACE);
              
        } else {          

            textValue = getValue().toString();
        }

        return textValue;
    }    
    
    /**
     * Return an input policy which describes how to marshall a value represented
     * by a value node from Java to CAL.
     * @return - the input policy associated with ValueNode instance.
     */
    @Override
    public InputPolicy getInputPolicy () {
        return InputPolicy.makeTypedDefaultInputPolicy(getTypeExpr().toSourceModel().getTypeExprDefn());
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
    
}
