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
 * DataConstructorValueNode.java
 * Created: June 4, 2000
 * By: Michael Cheng
 */

package org.openquark.cal.valuenode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openquark.cal.compiler.DataConstructor;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;
import org.openquark.cal.compiler.TypeConsApp;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.compiler.io.InputPolicy;
import org.openquark.cal.compiler.io.OutputPolicy;
import org.openquark.cal.foreignsupport.module.Prelude.AlgebraicValue;
import org.openquark.cal.foreignsupport.module.Prelude.EitherValue;
import org.openquark.cal.foreignsupport.module.Prelude.MaybeValue;
import org.openquark.cal.foreignsupport.module.Prelude.OrderingValue;
import org.openquark.cal.foreignsupport.module.Prelude.UnitValue;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.services.MetaModule;
import org.openquark.cal.services.Perspective;
import org.openquark.util.UnsafeCast;


/**
 * DataConstructorValueNode is a specialized ValueNode used to represent DataConstructor values.  
 * It holds a DataConstructor object, and a list containing ValueNodes which represent the 
 * component values of the DataConstructor object.
 * 
 * DataConstructorValueNode is special among the derived classes of AlgebraicValueNode. This is
 * because it can be used to represent any algebraic CAL value. However, the representation of the
 * value may not be as efficient or easy-to-work-with for Java clients for the cases in which a 
 * special purpose AlgebraicValueNode, optimized for values of a particular type, is available.
 * 
 * @author Michael Cheng
 */
public class DataConstructorValueNode extends AlgebraicValueNode {

    /**
     * A custom ValueNodeProvider for the DataConstructorValueNode.
     * @author Frank Worsley
     */
    public static class DataConstructorValueNodeProvider extends ValueNodeProvider<DataConstructorValueNode> {
        
        /** The working module for which the typeExprToNodeMap has been calculated. */
        private MetaModule workingModule = null;
        
        /** 
         * Map from typeExpr.toString() -> value node provided for that type.
         * Contains type expr for which value nodes have already been determined, or null if 
         * 1) we are currently trying to build a value node for that type. 
         * 2) a value node for that type cannot be built. 
         **/
        private final Map<String, DataConstructorValueNode> typeExprToNodeMap = new HashMap<String, DataConstructorValueNode>();
        
        public DataConstructorValueNodeProvider(ValueNodeBuilderHelper builderHelper) {
            super(builderHelper);
        }

        /**
         * @see org.openquark.cal.valuenode.ValueNodeProvider#getValueNodeClass()
         */
        @Override
        public Class<DataConstructorValueNode> getValueNodeClass() {
            return DataConstructorValueNode.class;
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
         * 
         * This method builds a new DataConstructorValueNode for a type expression. 
         * If no data constructor is provided, it smartly picks a default data constructor to use, 
         * so that there is no endless chain of recursive data constructors.
         * If no non-recursive data constructor can be found this method returns null.
         * 
         * This method also returns null if typeExpr is present in the typeExprToNodeMap but has a null
         * value node mapped to it. This case would occur if we are trying to find a data constructor and encounter
         * one that requires an argument for which we have not yet found a constructor itself. This would indicate
         * a cyclic constructor and therefore this method returns null.
         * 
         * @param value the value for the value node
         * @param dataConstructor the data constructor for the value node
         * @param typeExpr the type expression for the value node
         * @return a new ValueNode or null if the type constructor has no non-recursive data constructors.
         */
        @Override
        public DataConstructorValueNode getNodeInstance(Object value, DataConstructor dataConstructor, TypeExpr typeExpr) {
            
            // Check for handleability.
            TypeConsApp typeConsApp = typeExpr.rootTypeConsApp();
            if (typeConsApp == null || typeConsApp.isFunctionType()) {
                return null;
            }
            
            // Check visibility of the type.. (should we do this?)
            Perspective perspective = getValueNodeBuilderHelper().getPerspective();
            if (perspective.getTypeConstructor(typeConsApp.getName()) == null) {
                return null;
            }
            
            if (dataConstructor == null) {
                // No data constructor provided - we have to pick one, and create a default value.
                
                // If the perspective's working module has changed, the typeExprToNodeMap is no longer valid.
                if (perspective.getWorkingModule() != workingModule) {
                    workingModule = perspective.getWorkingModule();
                    typeExprToNodeMap.clear();
                }
                
                // TODOEL: ~HACK - we use the string-ified value as a key because we want it equals() in the map.
                String typeExprString = typeExpr.toString();
            
                // First, check if we've previously created a value node for this expression.
                if (typeExprToNodeMap.containsKey(typeExprString)) {

                    // We've already found a value node for this type, so just return it.
                    // If we're currently in the process of finding a value node for it, then this
                    // will end up returning null. That's ok since it indicates to the caller that
                    // there is a cycle.
                    DataConstructorValueNode valueNode = typeExprToNodeMap.get(typeExprString);
                    return valueNode != null ? valueNode.copyValueNode(typeExpr) : null;
                
                } else {
                    // Add a signal that we are building a value node for this type.
                    typeExprToNodeMap.put(typeExprString, null);
                }
                
                // Iterate over the visible data constructors.
                DataConstructor[] dataConsArray = perspective.getDataConstructorsForType(typeConsApp.getName());
                
                if (dataConsArray != null) {

                    for (final DataConstructor element : dataConsArray) {

                        // See if a value node can be built for this data constructor (call back with non-null data constructor).
                        DataConstructorValueNode dataConsValueNode = getNodeInstance(null, element, typeExpr);
                        
                        if (dataConsValueNode != null) {
                            typeExprToNodeMap.put(typeExprString, dataConsValueNode);
                            return dataConsValueNode;
                        }
                    }
                }
            
                return null;

            } else {
                
                if (value == null) {

                    // Try and get default child nodes for the value. 
                    // If there are no valid visible data constructors value will still be null.  This should only happen 
                    // when a data constructor value is output and the child nodes are then initialized during unwinding.
                    
                    int arity = dataConstructor.getArity();
                    
                    if (arity == 0) {
                        // No arguments, so we don't need any child value nodes.
                        value = Collections.EMPTY_LIST;
                        
                    } else {
                        // Construct child value nodes for the arguments.
                        ValueNode[] childValueNodes = new ValueNode[arity];
                    
                        for (int i = 0 ; i < arity; i++) {
                        
                            TypeExpr argType = TypeExpr.getComponentTypeExpr(typeExpr, i, dataConstructor);
    
                            childValueNodes[i] = getValueNodeBuilderHelper().getValueNodeForTypeExpr(argType);  
                        
                            if (childValueNodes[i] == null) {
                            
                                // Looks like this argument type has already been encountered and we have
                                // not been able to find a value node for it. That means this data constructor is
                                // cyclic and cannot be used.
                                return null;
                            }
                        }
                    
                        value = Arrays.asList(childValueNodes);
                    }
                }
                
                List<ValueNode> listValue = UnsafeCast.asTypeOf(value, Collections.<ValueNode>emptyList());
                return new DataConstructorValueNode(listValue, dataConstructor, typeExpr);
            }
        }
    }
    
    
    /** The data constructor this value node is for. */
    private final DataConstructor dataConstructor;

    /**
     * The List containing the ValueNodes representing the value components of this DataConstructorValueNode.
     * There is a value component for each argument of the data constructor.
     */
    private final List<ValueNode> childrenList;
    
    private Object outputValue;
    
    /**
     * Constructs a new DataConstructorValueNode representing a DataConstructor.
     * @param childrenList the list of child value nodes for the given data constructor
     * @param dataConstructorValue the data constructor this value node represents 
     * @param typeExprParam the TypeExpr of the data value represented by this DataConstructorValueNode. 
     */
    public DataConstructorValueNode(List<ValueNode> childrenList, DataConstructor dataConstructorValue, TypeExpr typeExprParam) {
    
        super(typeExprParam);
        
        if (dataConstructorValue == null) {
            throw new NullPointerException();
        }
        
        this.dataConstructor = dataConstructorValue;
        this.childrenList = childrenList != null ? new ArrayList<ValueNode>(childrenList) : new ArrayList<ValueNode>();
    }
    
    /**
     * @see org.openquark.cal.valuenode.ValueNode#getValue()
     */
    @Override
    public Object getValue() {
        return this;
    }      
    
    /**
     * Makes a copy of this ValueNode, but with another TypeExpr instance (of the same type).
     * This is a deep copy, with respect to value nodes and the associated type expression.
     * Note: if the new TypeExpr is a different type from the present TypeExpr, an error is thrown.
     * @param newTypeExpr the new type of the copied node.
     * @return ValueNode
     */
    @Override
    public DataConstructorValueNode copyValueNode(TypeExpr newTypeExpr) {
    
        checkCopyType(newTypeExpr);
    
        // eg. data Foo a = Bar (Baz a) a;
        // newTypeExpr will be of type "Foo a", but data constructor will be Bar, and children will be [Baz a, a].
        // dataConstructor type will be a -> Foo a.
        
        int childrenCount = childrenList.size();
        List<ValueNode> newChildrenList = new ArrayList<ValueNode>(childrenCount);

        for (int i = 0; i < childrenCount; i++) {

            ValueNode childVN = childrenList.get(i);
            TypeExpr elementTypeExpr = TypeExpr.getComponentTypeExpr(newTypeExpr, i, dataConstructor);
            newChildrenList.add(childVN.copyValueNode(elementTypeExpr));
        }

        DataConstructorValueNode dvn =  new DataConstructorValueNode(newChildrenList, getDataConstructor(), newTypeExpr);
        dvn.setOutputJavaValue(outputValue);
        return dvn;
    }

    /**
     * Returns the source model representation of the expression represented by
     * this ValueNode.
     * 
     * @return SourceModel.Expr
     */
    @Override
    public SourceModel.Expr getCALSourceModel() {

        int childrenCount = childrenList.size();
        SourceModel.Expr[] params = new SourceModel.Expr[childrenCount];

        for (int i = 0; i < childrenCount; i++) {
            params[i] = childrenList.get(i).getCALSourceModel();
        }

        return SourceModel.Expr.makeGemCall(dataConstructor.getName(), params);
    }
    
    /**
     * Returns whether valueNode contains a parametric value
     * @see ValueNode#containsParametricValue()  
     */
    @Override
    public boolean containsParametricValue() {
        if (outputValue != null) {
            return false;
        }
        
        for (int i = 0, childrenCount = childrenList.size(); i < childrenCount; i++) {
            if (childrenList.get(i).containsParametricValue()) {
                return true;
            }
        }
        return false;       
    }
    
    /**
     * Returns a list of the children ValueNodes representing the arguments values of the
     * data constructor this DataConstructorValueNode represents. Note that the returned
     * list is not modifiable.
     * @return List
     */
    public List<ValueNode> getChildrenList() {
        return Collections.unmodifiableList(childrenList);
    }

    /**
     * Sets the children of this data constructor node to the nodes in the given list.
     * @param newChildrenList the new list of children. Must be non-null and same size as current list.
     */    
    public void setChildrenList(List<ValueNode> newChildrenList) {
        
        if (newChildrenList == null) {
            throw new IllegalArgumentException("new children list cannot be null");
        }
        
        if (newChildrenList.size() != childrenList.size()) {
            throw new IllegalArgumentException("new children list cannot be different size than current list");
        }
        
        childrenList.clear();
        childrenList.addAll(newChildrenList);
    }
    
    /**
     * Returns the DataConstructor.
     * @return DataConstructor
     */
    public final DataConstructor getDataConstructor() {
        return dataConstructor;
    }
    
    /**
     * Returns the display text representation of the expression represented by this ValueNode.
     * @return String
     */
    @Override
    public String getTextValue() {
        if (outputValue != null) {
            return outputValue.toString();
        }
        
        String dcName = dataConstructor.getName().getUnqualifiedName();
        // If there is no children, then simply return the DataConstructor name (and no need for brackets either).
        if (childrenList.size() == 0) {

            return dcName;
        }

        StringBuilder sb = new StringBuilder("(");
        sb.append(dcName);

        for (int i = 0, childrenCount = childrenList.size(); i < childrenCount; i++) {

            sb.append(" ");
            sb.append(childrenList.get(i).getTextValue());
        }

        sb.append(')');

        return sb.toString();               
    }    
    
    /**
     * @see ValueNode#transmuteValueNode(ValueNodeBuilderHelper, ValueNodeTransformer, TypeExpr)
     */
    @Override
    public ValueNode transmuteValueNode(ValueNodeBuilderHelper valueNodeBuilderHelper, ValueNodeTransformer valueNodeTransformer, TypeExpr newTypeExpr) {

        QualifiedName typeConsName = getTypeExpr().rootTypeConsApp().getName();
        TypeConsApp newTypeConsApp = newTypeExpr.rootTypeConsApp();

        // If we don't need to change type constructors, then we just basically copy the value.
        if (newTypeConsApp != null && typeConsName.equals(newTypeConsApp.getName())) {

            List<ValueNode> componentList = getChildrenList();
            List<ValueNode> newComponentList = new ArrayList<ValueNode>(componentList.size());
    
            // Go thru the component ValueNodes and transmute them.
            for (int i = 0, componentCount = componentList.size(); i < componentCount; i++) {

                ValueNode componentVN = componentList.get(i);
                TypeExpr componentTypeExpr = TypeExpr.getComponentTypeExpr(newTypeExpr, i, dataConstructor);
                newComponentList.add(componentVN.transmuteValueNode(valueNodeBuilderHelper, valueNodeTransformer, componentTypeExpr));
            }

            ValueNode newValueNode = valueNodeBuilderHelper.buildValueNode(newComponentList, dataConstructor, newTypeExpr);
            return newValueNode;

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
        
        // We simply gather up all the arguments required for the children in order.
        // These are the arguments expected by the custom input policy build by getInputPolicy()
        
        List<Object> argumentValues = new ArrayList<Object>();
        for (final ValueNode child : childrenList) {
            Object[] vals = child.getInputJavaValues(); 
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
        // simply applies the data constructor on the children, of the form:
        // (\arg_0 ... arg_N -> M.DC (<input policy for child 0> <args>) ... (<input policy for child k> <args>))
        //
        // For example, if this value node's type is Maybe Int, and the DC is Just, the input policy will be:
        // (\arg_0 -> Prelude.Just (Prelude.input arg_0))
        //
        // If this value node's type is (Foo.Tuple2 Int Int) (with one DC Tuple2, with 2 fields #1 and #2), the input policy will be:
        // (\arg_0 arg_1 -> Foo.Tuple2 (Prelude.input arg_0) (Prelude.input arg_1))
        //
        // If this value node's type is Maybe (Maybe Unit), and the DC in both layers is just, then input policy will be:
        // (Prelude.Just (Prelude.Just Prelude.Unit))
        //
        // Note that in the last example there are no arguments.
        
        
        int childrenCount = childrenList.size();
        SourceModel.Expr[] childrenValues = new SourceModel.Expr[childrenCount];

        int argCount = 0;
        List<SourceModel.Parameter> paramsForMarshaler = new ArrayList<SourceModel.Parameter>();
        
        for (int i = 0; i < childrenCount; i++) {
            
            InputPolicy childInputPolicy = childrenList.get(i).getInputPolicy();
            
            int nChildInputPolicyArgs = childInputPolicy.getNArguments();
            
            SourceModel.Expr[] childInputPolicyParts = new SourceModel.Expr[nChildInputPolicyArgs + 1];
            childInputPolicyParts[0] = childInputPolicy.getMarshaler();
            
            //this loop is from 1 as the first element is always the input policy itself
            for (int j = 1; j <= nChildInputPolicyArgs; j++) {
                String childArg = "arg_" + (argCount++);
                paramsForMarshaler.add(SourceModel.Parameter.make(childArg, false));    
                childInputPolicyParts[j] = SourceModel.Expr.Var.makeUnqualified(childArg);
            }
            
            if (childInputPolicyParts.length >= 2) {
                childrenValues[i] = SourceModel.Expr.Application.make(childInputPolicyParts);
            } else {
                childrenValues[i] = childInputPolicyParts[0];
            }
        }

        SourceModel.Expr marshaler;
        if (paramsForMarshaler.isEmpty()) {
            marshaler = SourceModel.Expr.makeGemCall(dataConstructor.getName(), childrenValues);
            
        } else {
            marshaler = SourceModel.Expr.Lambda.make(
                paramsForMarshaler.toArray(new SourceModel.Parameter[paramsForMarshaler.size()]),
                SourceModel.Expr.makeGemCall(dataConstructor.getName(), childrenValues));
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
        
        TypeExpr typeExpr = getTypeExpr();
        
        // We only restrict the value that can be set if it is one of the small number of types we know about.
        if (typeExpr.hasRootTypeConstructor(CAL_Prelude.TypeConstructors.Maybe) ||
            typeExpr.hasRootTypeConstructor(CAL_Prelude.TypeConstructors.Either) ||
            typeExpr.hasRootTypeConstructor(CAL_Prelude.TypeConstructors.Ordering) ||
            typeExpr.hasRootTypeConstructor(CAL_Prelude.TypeConstructors.Unit)) {
            
            if (value instanceof AlgebraicValue ||
                value instanceof MaybeValue ||
                value instanceof EitherValue ||
                value instanceof UnitValue ||
                value instanceof OrderingValue) {
                
                outputValue = value;
            } else {
                outputValue = null;
            }
            
        } else {
            outputValue = value;
        }
    }    
}
