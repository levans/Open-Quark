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
 * ListValueNode.java
 * Created: June 6, 2001
 * By: Michael Cheng
 */

package org.openquark.cal.valuenode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.openquark.cal.compiler.DataConstructor;
import org.openquark.cal.compiler.SourceModel;
import org.openquark.cal.compiler.TypeConsApp;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.compiler.io.InputPolicy;
import org.openquark.cal.compiler.io.OutputPolicy;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.util.UnsafeCast;


/**
 * A specialized CompositeValueNode used to handle values of the Prelude.List parametric type.
 * Note: It makes navigating lists easier by getting rid of the 'unnecessary chain of Prelude.Cons'.
 * Creation date: (06/06/01 2:51:30 PM)
 * @author Michael Cheng
 */
public class ListValueNode extends ValueNode {

    /**
     * A custom ValueNodeProvider for the ListValueNode.
     * @author Frank Worsley
     */
    public static class ListValueNodeProvider extends ValueNodeProvider<ListValueNode> {
        
        public ListValueNodeProvider(ValueNodeBuilderHelper builderHelper) {
            super(builderHelper);
        }

        /**
         * @see org.openquark.cal.valuenode.ValueNodeProvider#getValueNodeClass()
         */
        @Override
        public Class<ListValueNode> getValueNodeClass() {
            return ListValueNode.class;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public ListValueNode getNodeInstance(Object value, DataConstructor dataConstructor, TypeExpr typeExpr) {
            
            // Check for handleability.
            if (!typeExpr.isListType()) {
                return null;
            }
            
            // value is a List of ValueNode
            List<ValueNode> listValue = UnsafeCast.asTypeOf(value, Collections.<ValueNode>emptyList());

            // Check for availability of list elements.
            ValueNode listElementVN;
            if (value != null && !listValue.isEmpty()) {
                // Get the prototypical value from the list value.
                listElementVN = listValue.get(0);

            } else {
                // Get the prototypical value from the builder helper.
                listElementVN = getValueNodeBuilderHelper().buildValueNode(null, null, ((TypeConsApp)typeExpr).getArg(0));
            }
            
            if (listElementVN == null) {
                // Not available.
                return null;
            }
            
            return new ListValueNode(listValue, typeExpr, listElementVN);
        }        
    }
    
    /** The list value. */
    private List<ValueNode> listValue;
    
    /** List of Java objects corresponding to the list element type. */    
    private List<?> outputListValue;
    
    /** A value node appropriate for the type of list element.  Can be null if the list is empty.  Not modified. */
    private final ValueNode prototypicalListElementVN;
    
    /**
     * ListValueNode constructor.
     * @param valueParam the list value.  If null, a default empty list will be represented.
     * @param typeExprParam
     * @param builderHelper
     */
    public ListValueNode(List<ValueNode> valueParam, TypeExpr typeExprParam, ValueNodeBuilderHelper builderHelper) {
        this(valueParam, typeExprParam, builderHelper.buildValueNode(null, null, ((TypeConsApp)typeExprParam).getArg(0)));
    }
    
    /**
     * ListValueNode constructor.
     * @param valueParam The list value.  If null, a default empty list will be represented.
     * @param typeExprParam
     * @param prototypicalListElementVN a prototypical value for the component list element value nodes.
     *   Must be non-null.
     */
    public ListValueNode(List<ValueNode> valueParam, TypeExpr typeExprParam, ValueNode prototypicalListElementVN) {

        super(typeExprParam);
        
        if (!typeExprParam.isListType()) {
            throw new IllegalArgumentException("given type expression is not a list type: " + typeExprParam);
        }
        
        if (valueParam == null) {
            listValue = new ArrayList<ValueNode>();
        } else {
            listValue = valueParam;
        }
        
        if (prototypicalListElementVN == null) {
            throw new IllegalArgumentException("Cannot generate prototype child value.");
        }

        this.prototypicalListElementVN = prototypicalListElementVN;
    }

    /**
     * Makes a copy of this ValueNode, but with another TypeExpr instance (of the same type).
     * This is a deep copy, with respect to value nodes and the associated type expression.
     * Note: if the new TypeExpr is a different type from the present TypeExpr, an error is thrown.
     * @param newTypeExpr the new type of the copied node.
     * @return ValueNode
     */
    @Override
    public ListValueNode copyValueNode(TypeExpr newTypeExpr) {

        checkCopyType(newTypeExpr);
 
        List<ValueNode> copyList = null;
        if (listValue != null) {
            copyList = new ArrayList<ValueNode>(listValue.size());
            TypeExpr componentTypeExpr = ListValueNode.getElementTypeExpr(newTypeExpr);
    
            for (int i = 0, listCount = listValue.size(); i < listCount; i++) {
    
                ValueNode elementVN = listValue.get(i);
                copyList.add(elementVN.copyValueNode(componentTypeExpr));
            }
        }
        
        ListValueNode lvn = new ListValueNode(copyList, newTypeExpr, prototypicalListElementVN);
        lvn.outputListValue = outputListValue;
        return lvn;
    }

    /**
     * Returns the source model representation of the expression represented by
     * this ValueNode.
     *  
     * @return SourceModel.Expr
     */
    @Override
    public SourceModel.Expr getCALSourceModel() {
        
        // This method optimizes the output for list value nodes containing explicitly
        // type-qualified elements. For example, the list of elements
        //         
        // [1 :: Prelude.Int, 2 :: Prelude.Int, 3 :: Prelude.Int]
        //         
        // will produce a source model corresponding to
        //         
        // [1 :: Prelude.Int, 2, 3]
        //         
        // where the type qualification appears only for the first element. However,
        // this implementation does not handle more advanced cases, where the type
        // qualification occurs not on the list elements themselves, but on the
        // subexpressions therein. For example, we do not handle cases like
        //        
        // [(1 :: Prelude.Int, "one"), (2 :: Prelude.Int, "two"), (3 :: Prelude.Int, "three")]
        //        
        // even though the type qualifications on the elements after the first one are
        // unnecessary. These cases are not currently handled because they require
        // special care. In particular, it is *not* correct to simply strip all
        // subexpressions of type qualifications. For example:
        //
        // [(1 :: Prelude.Int == 2 :: Prelude.Int), (3 :: Prelude.Int == 2 :: Prelude.Int)]
        //
        // is not semantically equivalent to [(1 :: Prelude.Int == 2 :: Prelude.Int), (3 == 2)]
        //
        // because 3 == 2 cannot be evaluated, since both 3 and 2 are Nums, and == does
        // not resolve to any particular instance method.

        List<SourceModel.Expr> elements = new ArrayList<SourceModel.Expr>(listValue.size());
        
        String elemTypeSourceText = null;

        for (final ValueNode listElem : listValue) {
            
            SourceModel.Expr elemExpr = listElem.getCALSourceModel();
            
            if (elemExpr instanceof SourceModel.Expr.ExprTypeSignature) {
                SourceModel.Expr.ExprTypeSignature ets = (SourceModel.Expr.ExprTypeSignature)elemExpr;
                
                if (elemTypeSourceText == null) {
                    // there has not been list elements with associated type
                    // signatures up to this point, so this element's expression
                    // is added to the source model with its type signature
                    
                    elemTypeSourceText = ets.getTypeSignature().toSourceText();
                    elements.add(elemExpr);
                    
                } else if (elemTypeSourceText.equals(ets.getTypeSignature().toSourceText())) {
                    // this element's type signature is equal to one that's
                    // already seen, so add the element's expression to the
                    // source model without its type signature
                    
                    elements.add(ets.getExpr());
                } else {
                    // since the type signatures do not match, add the expression with its
                    // type signature
                    
                    elements.add(elemExpr);
                }
            } else {
                // there is no type signature associated with this element, so simply
                // add its expression to the source model
                elements.add(elemExpr);
            }
        }

        return SourceModel.Expr.List.make(elements);
    }
    
    /**
     * @see ValueNode#containsParametricValue()
     */
    @Override
    public boolean containsParametricValue() {
        if (listValue == null) {
            return false;
        }
        
        for (int i = 1, elementCount = getNElements(); i < elementCount; i++) {

            ValueNode elementVN = getValueAt(i);
            if (elementVN.containsParametricValue()) {
                return true;
            }
        }
        return listValue.size() == 0;
    }
    
    /**
     * Analyzes the analyzeTypeExpr, and returns the TypeExpr of the list parameter.
     * e.g. for [Int] it returns Int.    
     * @return TypeExpr
     * @param analyzeTypeExpr 
     */
    private static TypeExpr getElementTypeExpr(TypeExpr analyzeTypeExpr) {

        // It doesn't matter the componentNumber (Elements of a list are homogeneous).
        TypeConsApp typeConsApp = analyzeTypeExpr.rootTypeConsApp();
                
        return typeConsApp.getArg(0);
    }
    
    /**
     * @see org.openquark.cal.valuenode.ValueNode#getValue()
     */
    @Override
    public List<ValueNode> getValue() {
        return Collections.unmodifiableList(listValue);
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
        for (final ValueNode vn : listValue) {
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
    public InputPolicy getInputPolicy () {
        
        // Instead of using Prelude.input as the input policy, we create a custom one that
        // simply applies the list constructor on the elements, of the form:
        // (\arg_0 ... arg_N -> [ (<input policy for element 0> <args>) , ... , (<input policy for element k> <args>) ])
        
        int listSize = listValue.size();
        SourceModel.Expr[] elements = new SourceModel.Expr[listSize];

        int argCount = 0;
        List<SourceModel.Parameter> paramsForMarshaler = new ArrayList<SourceModel.Parameter>();
        
        for (int i = 0; i < listSize; i++) {
            
            InputPolicy elementInputPolicy = listValue.get(i).getInputPolicy();
            
            int nElementInputPolicyArgs = elementInputPolicy.getNArguments();
            
            SourceModel.Expr[] elementInputPolicyArgs = new SourceModel.Expr[nElementInputPolicyArgs + 1];
            elementInputPolicyArgs[0] = elementInputPolicy.getMarshaler();

            //this loop is from 1 as the first element is always the input policy itself
            for (int j = 1; j <= nElementInputPolicyArgs; j++) {
                String elementArg = "arg_" + (argCount++);                
                paramsForMarshaler.add(SourceModel.Parameter.make(elementArg, false));
                elementInputPolicyArgs[j] = SourceModel.Expr.Var.makeUnqualified(elementArg);
            }

            if (elementInputPolicyArgs.length >= 2) {
                elements[i] = SourceModel.Expr.Application.make(elementInputPolicyArgs);
            } else {
                elements[i] = elementInputPolicyArgs[0];
            }
        }

        SourceModel.Expr marshaler;
        if (paramsForMarshaler.isEmpty()) {
            marshaler = SourceModel.Expr.List.make(elements);
            
        } else {
            marshaler = SourceModel.Expr.Lambda.make(
                paramsForMarshaler.toArray(new SourceModel.Parameter[paramsForMarshaler.size()]),
                SourceModel.Expr.List.make(elements));
        }
        
        return InputPolicy.makeWithTypeAndMarshaler(getTypeExpr().toSourceModel().getTypeExprDefn(), marshaler, paramsForMarshaler.size());
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
        if (!(value instanceof List<?>)) {
            throw new IllegalArgumentException("Error in ListValueNode.setOutputJavaValue: output must be an instance of List, not an instance of: " + value.getClass().getName());
        }
        
        // Some external components are expecting that this will always be a list of value nodes,
        // so convert the value from java objects to value nodes, and leave outputListValue null
        listValue = new ArrayList<ValueNode>();
        for (final Object childValue : ((List<?>)value)) {
            ValueNode vn = prototypicalListElementVN.copyValueNode();
            vn.setOutputJavaValue(childValue);
            listValue.add(vn);
        }
        outputListValue = null;
    }
    
    /**
     * Returns the display text representation of the expression represented by this ValueNode.
     * @return String
     */
    @Override
    public String getTextValue() {
        if (outputListValue != null) {
            // todo: temp code for list of Char
            //todo: it would be faster to use sameType but we don't have access to PreludeTypeConstants here.
            if (getTypeExpr().toString().equals("[" + CAL_Prelude.TypeConstructors.Char.getQualifiedName() + "]")) {
                StringBuilder sbList = new StringBuilder();

                for (final Character character : UnsafeCast.<Character>castList(outputListValue)) {
                    sbList.append(character.charValue());
                }

                return sbList.toString();
            }

            return outputListValue.toString();            
        }
        
        // todo: temp code for list of Char
        if (getTypeExpr().toString().equals("[" + CAL_Prelude.TypeConstructors.Char.getQualifiedName() + "]")) {
            StringBuilder sbList = new StringBuilder();

            for (int i = 0, elementCount = listValue.size(); i < elementCount; i++) {
                ValueNode elementVN = listValue.get(i);
                sbList.append(elementVN.getTextValue());
            }

            return sbList.toString();
        }

     
        StringBuilder sbList = new StringBuilder("[");

        if (listValue.size() > 0) {

            ValueNode firstVN = listValue.get(0);

            sbList.append(firstVN.getTextValue());

            for (int i = 1, elementCount = listValue.size(); i < elementCount; i++) {

                ValueNode elementVN = listValue.get(i);

                sbList.append(", ").append(elementVN.getTextValue());
            }
        }

        sbList.append("]");

        return sbList.toString();
    }
           
    public int getNElements() {
        if (outputListValue != null) {
            return outputListValue.size();
        }
        return listValue.size();
    }
       
    public void add(ValueNode valueNode) {
        listValue.add(valueNode);
    }
    
    public ValueNode getValueAt(int i) {
        return listValue.get(i);
    } 
         
    public void setValueNodeAt(int i, ValueNode valueNode) {
        listValue.set(i, valueNode);
    } 
    
    public void removeValueNodeAt(int i) {
        listValue.remove(i);
    }      

    /**
     * Replaces the current list of value nodes with the specified collection.
     */
    public void setValueList(Collection<ValueNode> newValues) {
        listValue.clear();
        listValue.addAll(newValues);
    }

    /**
     * @see ValueNode#transmuteValueNode(ValueNodeBuilderHelper, ValueNodeTransformer, TypeExpr)
     */
    @Override
    public ValueNode transmuteValueNode(ValueNodeBuilderHelper valueNodeBuilderHelper, ValueNodeTransformer valueNodeTransformer, TypeExpr newTypeExpr) {
        Class<? extends ValueNode> handlerClass = valueNodeBuilderHelper.getValueNodeClass(newTypeExpr);
        if ((getClass().equals(handlerClass))) {
            
            List<ValueNode> newComponentList = new ArrayList<ValueNode>(getNElements());
            
            // Go thru the element ValueNodes and transmute them.
            for (int i = 0, elementCount = getNElements(); i < elementCount; i++) {
            
                ValueNode elementVN = getValueAt(i);
                TypeExpr elementTypeExpr = ListValueNode.getElementTypeExpr(newTypeExpr);
                newComponentList.add(elementVN.transmuteValueNode(valueNodeBuilderHelper, valueNodeTransformer, elementTypeExpr));
            }
            
            ValueNode newValueNode = valueNodeBuilderHelper.buildValueNode(newComponentList, null, newTypeExpr);
            
            return newValueNode;
    
        } else {
            return valueNodeTransformer.transform(valueNodeBuilderHelper, this, newTypeExpr);
        }
    }
}
