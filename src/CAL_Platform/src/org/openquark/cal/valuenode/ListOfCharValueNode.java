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
 * ListOfCharValueNode.java
 * Creation date: (04/06/01 1:58:32 PM)
 * By: Michael Cheng
 */
package org.openquark.cal.valuenode;

import java.util.ArrayList;
import java.util.List;

import org.openquark.cal.compiler.DataConstructor;
import org.openquark.cal.compiler.SourceModel;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.compiler.io.InputPolicy;
import org.openquark.cal.compiler.io.OutputPolicy;


/**
 * A specialized ValueNode used to handle values of the Prelude.[Char] type.
 * Warning: it is not used for Prelude.String. Note the contrast with the ListValueNode, which
 * handles lists of any parametric type. This class provides a slightly more convenient
 * external representation of the Prelude.[Char] type than the ListValueNode.
 * 
 * Creation date: (04/06/01 1:58:32 PM)
 * @author Michael Cheng
 */
public class ListOfCharValueNode extends AlgebraicValueNode {

    /**
     * A custom ValueNodeProvider for the ListOfCharValueNode.
     * @author Frank Worsley
     */
    public static class ListOfCharValueNodeProvider extends ValueNodeProvider<ListOfCharValueNode> {
        
        public ListOfCharValueNodeProvider(ValueNodeBuilderHelper builderHelper) {
            super(builderHelper);
        }

        /**
         * @see org.openquark.cal.valuenode.ValueNodeProvider#getValueNodeClass()
         */
        @Override
        public Class<ListOfCharValueNode> getValueNodeClass() {
            return ListOfCharValueNode.class;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public ListOfCharValueNode getNodeInstance(Object value, DataConstructor dataConstructor, TypeExpr typeExpr) {
            
            // Check for handleability.          
            if (!typeExpr.sameType(getValueNodeBuilderHelper().getPreludeTypeConstants().getCharListType())) {
                return null;
            }
            
            return new ListOfCharValueNode((String)value, typeExpr);
        }        
    }
    
    private final StringBuilder sb;
    
    /**
     * The char used to replace the carriage return '\n' when
     * displaying text.
     */
    public static final char CHAR_RETURN_REPLACE = (char) 0xB6;

    /**
     * ListOfCharValueNode constructor.
     * This ListOfCharValueNode's value will be set to stringValue.
     * Note: If stringValue is null, then a default value of the empty String "" will be given.
     * Note: typeExprParam really must be of data type [Char], aka String.
     * 
     * @param stringValue 
     * @param typeExprParam 
     */
    public ListOfCharValueNode(String stringValue, TypeExpr typeExprParam) {
        super(typeExprParam);
        sb = (stringValue == null) ? new StringBuilder() : new StringBuilder(stringValue);
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
     * @param newTypeExpr the new type of the copied node.
     * @return ValueNode
     */
    @Override
    public ListOfCharValueNode copyValueNode(TypeExpr newTypeExpr) {

        checkCopyType(newTypeExpr);
        return new ListOfCharValueNode(getStringValue(), newTypeExpr);
    }

    /**
     * Returns the source model representation of the expression represented by
     * this ValueNode.
     * 
     * @return SourceModel.Expr
     */
    @Override
    public SourceModel.Expr getCALSourceModel() {

        // Can't call String.stringToCharacters if the current module doesn't import the String module.
//        return SourceModel.Expr.makeGemCall(
//                new QualifiedName("String", "stringToCharacters"),
//                SourceModel.Expr.Literal.StringLit.make(getStringValue()));

        String stringValue = getStringValue();
        int strLen = stringValue.length();

        // Create a list of char literals.
        SourceModel.Expr[] charElements = new SourceModel.Expr[strLen];
        for (int i = 0; i < strLen; i++) {
            charElements[i] = SourceModel.Expr.makeCharValue(stringValue.charAt(i));
        }

        return SourceModel.Expr.List.make(charElements);
    }
    
    /**
     * Returns the String value.
     * @return String
     */
    public String getStringValue() {

        return sb.toString();
    }
    
    @Override
    public Object getValue() {
        return getStringValue();
    }      
    
    /**
     * Returns the display text representation of the expression represented by this ValueNode.
     * @return String
     */
    @Override
    public String getTextValue() {

        // Will need to get rid of the carriage returns (since they will not be nicely displayed).
        return getStringValue().replace('\n', CHAR_RETURN_REPLACE);
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
    public Object[] getInputJavaValues () {
        char[] chars = sb.toString().toCharArray();
        ArrayList<Character> l = new ArrayList<Character> ();
        for (int i = 0; i < chars.length; ++i) {
            l.add (Character.valueOf(chars[i]));
        }
        return new Object[]{l};
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
    /**
     * Set a value which is the result of the marshaller described by
     * 'getOutputPolicy()'.
     * @param value - the java value
     */
    @Override
    public void setOutputJavaValue(Object value) {
        if (!(value instanceof List)) {
            throw new IllegalArgumentException("Error in ListOfCharValueNode.setOutputJavaValue: output must be an instance of List, not an instance of: " + value.getClass().getName());
        }
        for (final Object o : (List<?>)value) {
            sb.append(o);
        }
    }
    
}
