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
 * ColourValueNode.java
 * Creation date: (05/06/01 10:14:21 AM)
 * By: Michael Cheng
 */
package org.openquark.cal.valuenode;

import java.awt.Color;

import org.openquark.cal.compiler.DataConstructor;
import org.openquark.cal.compiler.SourceModel;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.compiler.io.InputPolicy;
import org.openquark.cal.compiler.io.OutputPolicy;
import org.openquark.cal.module.Cal.Graphics.CAL_Color;


/**
 * A specialized AlgebraicValueNode used to handle values of the Color.Colour type.
 * data public Colour = private MakeColour Int Int Int; // Red Green Blue
 *
 * Creation date: (05/06/01 10:14:21 AM)
 * @author Michael Cheng
 */
public class ColourValueNode extends AlgebraicValueNode {
    
    /**
     * A custom ValueNodeProvider for the ColourValueNode.
     * @author Frank Worsley
     */
    public static class ColourValueNodeProvider extends ValueNodeProvider<ColourValueNode> {

        public ColourValueNodeProvider(ValueNodeBuilderHelper builderHelper) {
            super(builderHelper);
        }
        
        /**
         * @see org.openquark.cal.valuenode.ValueNodeProvider#getValueNodeClass()
         */
        @Override
        public Class<ColourValueNode> getValueNodeClass() {
            return ColourValueNode.class;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public ColourValueNode getNodeInstance(Object value, DataConstructor dataConstructor, TypeExpr typeExpr) {
            
            // Check for handleability.
            if (!typeExpr.isNonParametricType(CAL_Color.TypeConstructors.Color)) {
                return null;
            }
            
            return new ColourValueNode((Color) value, typeExpr);
        }
    }    
    
    private int red = 0;
    private int green = 0;
    private int blue = 0;
    private int alpha = 255;

    private Color colourValue = null;
    
    /**
     * ColourValueNode constructor.
     * @param colourValue the  value of the value node, if null Black (R:0,G:0,B:0) will be used.
     * @param typeExprParam the type expression of the value node. Must be of Colour type. 
     */
    public ColourValueNode(Color colourValue, TypeExpr typeExprParam) {
        
        super(typeExprParam);
        checkTypeConstructorName(typeExprParam, CAL_Color.TypeConstructors.Color);
        
        if (colourValue != null) {
            red = colourValue.getRed();
            green = colourValue.getGreen();
            blue = colourValue.getBlue();
            alpha = colourValue.getAlpha();
        }       
    }
    
    /**    
     * @return boolean
     */
    @Override
    public boolean containsParametricValue() {
        return false;
    }    

    /**
     * Makes a copy of this ValueNode, but with another TypeExpr instance (of the same type).
     * This is a deep copy, with respect to value nodes and the associated type expression.
     * Note: if the new TypeExpr is a different type from the present TypeExpr, an error is thrown.
     * Creation date: (11/07/01 2:11:35 PM)
     * @param newTypeExpr TypeExpr the new type of the copied node.
     * @return ValueNode
     */
    @Override
    public ColourValueNode copyValueNode(TypeExpr newTypeExpr) {

        checkCopyType(newTypeExpr);
        return new ColourValueNode(getColourValue(), newTypeExpr);
    }

    /**
     * Returns the source model representation of the expression represented by
     * this ValueNode.
     * 
     * @return SourceModel.Expr
     */
    @Override
    public SourceModel.Expr getCALSourceModel() {

        return CAL_Color.Functions.makeTranslucentColor(red, green, blue, alpha);
    }

    /**
     * Returns colour value. Creation date: (13/06/01 2:21:53 PM)
     * 
     * @return Color
     */
    public Color getColourValue() {
        if (colourValue != null) {
            return colourValue;
        }
        return new Color(red, green, blue, alpha);
    }
    
    @Override
    public Object getValue() {
        return getColourValue();
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
     * Return an array of objects which are the values needed by the marshaller
     * described by 'getInputPolicy()'.
     * @return - an array of Java objects corresponding to the value represented by a value node instance.
     */
    @Override
    public Object[] getInputJavaValues() {
        return new Object[]{getColourValue()};
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
     * @param value - the java value
     */
    @Override
    public void setOutputJavaValue(Object value) {
        if (!(value instanceof Color)) {
            throw new IllegalArgumentException("Error in ColourValueNode.setOutputJavaValue: output must be an instance of Color, not an instance of: " + value.getClass().getName());
        }
        colourValue = (Color)value;
        red = colourValue.getRed();
        blue = colourValue.getBlue();
        green = colourValue.getGreen();
        alpha = colourValue.getAlpha();
    }
    
    /**
     * Returns the display text representation of the expression represented by this ValueNode.
     * @return String
     */
    @Override
    public String getTextValue() {     
        return "R:" + red + " G:" + green + " B:" + blue + " A:" + alpha;
    }     
}
