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
 * RangeValueNode.java
 * Creation date: (05/10/04 10:14:21 AM)
 * By: Iulian Radu
 */
package org.openquark.cal.valuenode;

import java.util.ArrayList;
import java.util.List;

import org.openquark.cal.compiler.DataConstructor;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;
import org.openquark.cal.compiler.TypeConsApp;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.compiler.TypeVar;
import org.openquark.cal.compiler.io.InputPolicy;
import org.openquark.cal.compiler.io.OutputPolicy;
import org.openquark.cal.foreignsupport.module.Range.RangeValue;
import org.openquark.cal.module.Cal.Utilities.CAL_Range;


/**
 * A specialized AlgebraicValueNode used to handle values of the Range.Range type.
 *
 * Creation date: May 10, 2004
 * @author Iulian Radu
 */
public class RangeValueNode extends AlgebraicValueNode {
    
    /**
     * Class describing the form of a range. 
     * This indicates whether the range has a left and/or right bound, 
     * whether the range includes its endpoints, and the name
     * of the supercombinator constructing such a range.
     * 
     * @author Iulian Radu
     */
    public static class Form {
        
        // Names of the supercombinators constructing individual range forms (contained in module: Range)
        static final private String RANGE_CONSTRUCTOR_WHOLE          = CAL_Range.Functions.makeEntireRange.getUnqualifiedName();
        static final private String RANGE_CONSTRUCTOR_LESS           = CAL_Range.Functions.makeIsLessThanRange.getUnqualifiedName();
        static final private String RANGE_CONSTRUCTOR_LESS_EQUALS    = CAL_Range.Functions.makeIsLessThanEqualsRange.getUnqualifiedName();
        static final private String RANGE_CONSTRUCTOR_GREATER        = CAL_Range.Functions.makeIsGreaterThanRange.getUnqualifiedName();
        static final private String RANGE_CONSTRUCTOR_GREATER_EQUALS = CAL_Range.Functions.makeIsGreaterThanEqualsRange.getUnqualifiedName();
        static final private String RANGE_CONSTRUCTOR_INCLUDE_BOTH   = CAL_Range.Functions.makeBetweenIncludingEndpointsRange.getUnqualifiedName();
        static final private String RANGE_CONSTRUCTOR_INCLUDE_RIGHT  = CAL_Range.Functions.makeBetweenIncludingRightEndpointRange.getUnqualifiedName();
        static final private String RANGE_CONSTRUCTOR_INCLUDE_LEFT   = CAL_Range.Functions.makeBetweenIncludingLeftEndpointRange.getUnqualifiedName();
        static final private String RANGE_CONSTRUCTOR_INCLUDE_NONE   = CAL_Range.Functions.makeBetweenExcludingEndpointsRange.getUnqualifiedName();
        
        /** Whether the range has a left endpoint */
        private final boolean hasLeft;
        
        /** Whether the range has a right endpoint */
        private final boolean hasRight;
        
        /** Whether the range includes its left endpoint */
        private final boolean includesLeft;
        
        /** Whether the range includes its right endpoint */
        private final boolean includesRight;
        
        /** Constructor **/
        public Form(boolean leftBounded, boolean rightBounded, boolean includesLeft, boolean includesRight) {
            this.hasLeft = leftBounded;
            this.hasRight = rightBounded;
            this.includesLeft = includesLeft;
            this.includesRight = includesRight;
        }
        
        /**
         * @return the name of the supercombinator constructing a range with this form
         */
        public String getConstructorSCName() {
            if (!hasLeft && !hasRight) {
                // Does not have any bounds
                return RANGE_CONSTRUCTOR_WHOLE;
                
            } else if (!hasLeft) {
                // Does not have left bound, so has right
                
                if (includesRight) {
                    return RANGE_CONSTRUCTOR_LESS_EQUALS;
                } else {
                    return RANGE_CONSTRUCTOR_LESS;
                }
            } else if (!hasRight) {
                // Does not have right bound, so has left
                
                if (includesLeft) {
                    return RANGE_CONSTRUCTOR_GREATER_EQUALS;
                } else {
                    return RANGE_CONSTRUCTOR_GREATER;
                }
            } else {
                // Both endpoints exist
                
                if (includesLeft) {
                    if (includesRight) {
                        return RANGE_CONSTRUCTOR_INCLUDE_BOTH;
                    } else {
                        return RANGE_CONSTRUCTOR_INCLUDE_LEFT;
                    }
                    
                } else {
                    if (includesRight) {
                        return RANGE_CONSTRUCTOR_INCLUDE_RIGHT;
                    } else {
                        return RANGE_CONSTRUCTOR_INCLUDE_NONE;
                    }
                }
            }
        }
        
        /**
         * @return whether the range has a left bound
         */
        public boolean hasLeftBound() {
            return hasLeft;
        }
        
        /**
         * @return whether the range has a right bound
         */
        public boolean hasRightBound() {
            return hasRight;
        }
        
        /**
         * @return whether the range includes its left bound
         */
        public boolean includesLeftBound() {
            return includesLeft;
        }
        
        /**
         * @return whether the range includes its right bound
         */
        public boolean includesRightBound() {
            return includesRight;
        }
        
        /**
         * @return a copy of this range form
         */
        public Form makeCopy() {
            return new Form(hasLeft, hasRight, includesLeft, includesRight);
        }
    }
    
    /**
     * Placeholder class for a range node form and left/right endpoint nodes. 
     * This is used to pass information about an existing range node, when 
     * transmuting such a node to a different range type expression via 
     * transmuteValueNode()
     * 
     * @author Iulian Radu
     */
    private static class SimpleRangeData {
        public Form form;
        public ValueNode leftNode;
        public ValueNode rightNode;
        
        public SimpleRangeData(Form form, ValueNode leftNode, ValueNode rightNode) {
            this.leftNode = leftNode;
            this.rightNode = rightNode;
            this.form = form;
        }
        
        public Form getForm() {
            return form;
        }
        
        public ValueNode getLeftNode() {
            return leftNode;
        }
        
        public ValueNode getRightNode() {
            return rightNode;
        }
    }
    
    /**
     * A custom ValueNodeProvider for the RangeValueNode.
     * 
     * @author Iulian Radu
     */
    public static class RangeValueNodeProvider extends ValueNodeProvider<RangeValueNode> {

        /** The default form of a range includes both endpoints */
        private static final Form RANGE_DEFAULT_FORM = new Form(true, true, true, true);
        
        /** Qualified name of the supercombinator used for default construction of ranges */
        public static final QualifiedName RANGE_CONSTRUCTION_SC_NAME = QualifiedName.make(CAL_Range.MODULE_NAME, RANGE_DEFAULT_FORM.getConstructorSCName());
        
        /**
         * Constructs a new RangeValueNodeProvider.
         * @param builderHelper the value node builder helper to use for building value nodes.
         */
        public RangeValueNodeProvider(ValueNodeBuilderHelper builderHelper) {
            super(builderHelper);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public Class<RangeValueNode> getValueNodeClass() {
            return RangeValueNode.class;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public RangeValueNode getNodeInstance(Object value, DataConstructor dataConstructor, TypeExpr typeExpr) {
            
            // Check for handleability.
            if (typeExpr.hasRootTypeConstructor(CAL_Range.TypeConstructors.Range)) {
                
                // Ensure Orderability.
                
                // The value node is restricted to handle Ordinal instance types for 
                // the endpoints. The following code enforces this, by ensuring the
                // type constructor argument (ie: "Double" in "Range Double") is valid
                // for use as argument type for the range construction supercombinators.
                //
                // Since these require ordinal type arguments, "Range Boolean" will be
                // available as a value node, whereas "Range BinaryOp" will not.
                
                // Get the more specialized type required by the construction sc (this will be "Ord a => a").
                TypeExpr scArgType = getValueNodeBuilderHelper().getPerspective().getWorkingModuleTypeInfo().getVisibleFunction(RANGE_CONSTRUCTION_SC_NAME).getTypeExpr().getTypePieces()[0];
                
                // Get the argument type of the value node type expression (this will be "Boolean" for "Range Boolean")
                TypeExpr endpointType = ((TypeConsApp)typeExpr).getArg(0);
                
                if (!(endpointType instanceof TypeVar)) {
                    // If the type is specialized, check if the type is valid by unifying with
                    // the construction sc argument type. 
                    if (!TypeExpr.canUnifyType(scArgType, endpointType, getValueNodeBuilderHelper().getPerspective().getWorkingModuleTypeInfo())) {
                        return null;
                    }
                } else {
                    // The type was not specialized (ie: the range type is "Range a"), so the type
                    // of the endpoints will be simply what is required by the construction sc.
                    endpointType = scArgType;
                }
                
                
                // Construct range from a specified value
                
                if (value != null) {
                    // A value was specified; it contains a form and left/right value nodes.
                    
                    SimpleRangeData data = (SimpleRangeData)value;
                    return new RangeValueNode(typeExpr, data.getForm(), data.getLeftNode(), data.getRightNode());
                }
                
                // Construct the default value nodes for the endpoints
                
                ValueNode leftVN = getValueNodeBuilderHelper().getValueNodeForTypeExpr(endpointType);
                ValueNode rightVN = getValueNodeBuilderHelper().getValueNodeForTypeExpr(endpointType);
                
                // Construct the default range form
                
                Form form;
                if (dataConstructor != null) {
                    // This should not occur
                    throw new IllegalArgumentException("Data constructor supplied, not expected.");
                } else {
                    // Otherwise, the default form includes both endpoints
                    form = RANGE_DEFAULT_FORM;
                }
                
                // Construct the range value node
                return new RangeValueNode(typeExpr, form, leftVN, rightVN);
            
                
            } else {
                // Not a range data type; the provider does not handle this
                return null;
            }
        }
    }
    
    /** The range form */
    private Form form;
    
    /** The node representing the left endpoint */
    private final ValueNode leftNode;
    
    /** The node representing the right endpoint */
    private final ValueNode rightNode;
    
    
    /**
     * Range ValueNode constructor.
     * @param typeExprParam the type expression of the value node. Must be of Range type.
     * @param rangeForm describes the form of this range
     * @param start value node corresponding to the start endpoint (null if none)
     * @param end value node corresponding to the end endpoint (null if none) 
     */
    public RangeValueNode(TypeExpr typeExprParam, Form rangeForm, ValueNode start, ValueNode end) {
        
        super(typeExprParam);
        checkTypeConstructorName(typeExprParam, CAL_Range.TypeConstructors.Range);
        
        if (rangeForm == null) {
            throw new IllegalArgumentException();
        }
        
        this.form = rangeForm;
        this.leftNode = start;
        this.rightNode = end;
    }
    
    /**    
     * @return whether the range type is parametric (eg: "Range a" is parametric)
     */
    @Override
    public boolean containsParametricValue() {
        return getTypeExpr().isPolymorphic();
    }    

    /**
     * Makes a copy of this ValueNode, but with another TypeExpr instance (of the same type).
     * This is a deep copy, with respect to value nodes and the associated type expression.
     * Note: if the new TypeExpr is a different type from the present TypeExpr, an error is thrown.
     * @param newTypeExpr TypeExpr the new type of the copied node.
     * @return ValueNode
     */
    @Override
    public RangeValueNode copyValueNode(TypeExpr newTypeExpr) {

        checkCopyType(newTypeExpr);
        RangeValueNode other = new RangeValueNode(newTypeExpr, form, leftNode.copyValueNode(), rightNode.copyValueNode());
        return other;
    }

    /**
     * @return the source model representation of the expression represented by this ValueNode.
     */
    @Override
    public SourceModel.Expr getCALSourceModel() {

        if (form == null) {
            throw new IllegalStateException("Range form does not exist");
        }
        
        List<SourceModel.Expr> arguments = new ArrayList<SourceModel.Expr>();
        
        if (form.hasLeftBound()) {
            arguments.add(leftNode.getCALSourceModel());
        }
        if (form.hasRightBound()) {
            arguments.add(rightNode.getCALSourceModel());
        }

        return SourceModel.Expr.makeGemCall(
            QualifiedName.make(CAL_Range.MODULE_NAME, form.getConstructorSCName()),            
            arguments.toArray(new SourceModel.Expr[0]));
    }
    
    /**
     * @return the display text representation of the expression represented by this ValueNode.
     */
    @Override
    public String getTextValue() {
        return getFormattedTextValue(leftNode.getTextValue(), rightNode.getTextValue());
    }
    
    /**
     * Returns a text representation of the current range form and endpoints.
     * Ex: If the range form represents the constructor "IsLessThan" and the right
     *     endpoint is 2.0, then the text representation will be "X < 2.0"
     * 
     * @param startTextValue text representation of the left endpoint
     * @param endTextValue text representation of the right endpoint
     * @return text representation of the range
     */
    private String getFormattedTextValue(String startTextValue, String endTextValue) {
        StringBuilder sb = new StringBuilder();
        
        if (!form.hasLeftBound() && !form.hasRightBound()) {
            return "Entire range";
        }
        
        if (form.hasLeftBound()) {
            sb.append(startTextValue + " <" + (form.includesLeftBound()? "=":"") + " ");
        }
        sb.append("X");
        if (form.hasRightBound()) {
            sb.append(" <" + (form.includesRightBound()? "=":"") + " " + endTextValue);            
        }
        
        return sb.toString();
    }
    
    /**
     * @see org.openquark.cal.valuenode.ValueNode#getValue()
     */
    @Override
    public Object getValue() {
        return new SimpleRangeData(form, leftNode.copyValueNode(), rightNode.copyValueNode());
    }
    
    /**
     * @return the value node representing the left endpoint
     */
    public ValueNode getLeftEndpoint() {
        return leftNode;
    }
    
    /**
     * @return the value node representing the right endpoint
     */
    public ValueNode getRightEndpoint() {
        return rightNode;
    }
    
    /**
     * @return whether the range includes the left endpoint
     */
    public boolean getIncludesLeftEndpoint() {
        return form.includesLeftBound();
    }
    
    /**
     * @return whether the range includes the left endpoint
     */
    public boolean getIncludesRightEndpoint() {
        return form.includesRightBound();
    }
    
    /**
     * @return the form of this range node
     */
    public Form getForm() {
        return form;
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
        // Give a RangeValue object representing this range 
        Object[] lvals = leftNode.getInputJavaValues();
        Object[] rvals = rightNode.getInputJavaValues();
        return new Object[]{ getRangeValue(
                (lvals != null && lvals.length > 0)? lvals[0] : null, 
                (rvals != null && rvals.length > 0)? rvals[0] : null)};
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
        if (!(value instanceof RangeValue)) {
            throw new IllegalArgumentException("Error in RangeValueNode.setOutputJavaValue: output must be an instance of RangeValue, not an instance of: " + value.getClass().getName());
        }
        
        // Take info from range value and put into ours
        RangeValue rangeValue = (RangeValue)value;
        form = new Form(rangeValue.hasLeftBound(), rangeValue.hasRightBound(), rangeValue.includesLeftBound(), rangeValue.includesRightBound());
        if (form.hasLeftBound()) {
            leftNode.setOutputJavaValue(rangeValue.getLeftEndpoint());
        }
        if (form.hasRightBound()) {
            rightNode.setOutputJavaValue(rangeValue.getRightEndpoint());
        }
    }
      
    /**
     * {@inheritDoc}
     */
    @Override
    public ValueNode transmuteValueNode(ValueNodeBuilderHelper valueNodeBuilderHelper, ValueNodeTransformer valueNodeTransformer, TypeExpr newTypeExpr) {
        Class<?> handlerClass = valueNodeBuilderHelper.getValueNodeClass(newTypeExpr);

        if ((getClass().equals(handlerClass))) {
            
            // Transmute the endpoint nodes to the new types
            TypeExpr childTypeExpr = ((TypeConsApp)newTypeExpr).getArg(0);
            ValueNode leftNode;
            ValueNode rightNode;
            if (!childTypeExpr.sameType(this.leftNode.getTypeExpr())) {
                leftNode = this.leftNode.transmuteValueNode(valueNodeBuilderHelper, valueNodeTransformer, childTypeExpr);
                rightNode = this.rightNode.transmuteValueNode(valueNodeBuilderHelper, valueNodeTransformer, childTypeExpr);
            } else {
                leftNode = this.leftNode.copyValueNode();
                rightNode = this.rightNode.copyValueNode();
            }
            
            // Then recreate the range value node from the new type, maintaining the endpoints and form
            ValueNode vn = valueNodeBuilderHelper.buildValueNode(new SimpleRangeData(form, leftNode, rightNode), null, newTypeExpr);
            return vn;
        } else {
            return valueNodeTransformer.transform(valueNodeBuilderHelper, this, newTypeExpr);
        }
    }
    
    /** 
     * Get the range value representation of this value node.
     * 
     * @param left input representation of the left endpoint node
     * @param right input representation of the right endpoint node
     * @return RangeValue
     */
    private RangeValue getRangeValue(Object left, Object right) {
        if (!form.hasLeft && !form.hasRight) {
            // Does not have any bounds
            return RangeValue.constructEntireRange();
            
        } else if (!form.hasLeft) {
            // Does not have left bound, so has right
            
            if (form.includesRight) {
                return RangeValue.constructIsLessThanEquals(right);
            } else {
                return RangeValue.constructIsLessThan(right);
            }
        } else if (!form.hasRight) {
            // Does not have right bound, so has left
            
            if (form.includesLeft) {
                return RangeValue.constructIsGreaterThanEquals(left);
            } else {
                return RangeValue.constructIsGreaterThan(left);
            }
        } else {
            // Both endpoints exist
            
            if (form.includesLeft) {
                if (form.includesRight) {
                    return RangeValue.constructBetweenIncludingEndpoints(left, right);
                } else {
                    return RangeValue.constructBetweenIncludingLeftEndpoint(left, right);
                }
                
            } else {
                if (form.includesRight) {
                    return RangeValue.constructBetweenIncludingRightEndpoint(left, right);
                } else {
                    return RangeValue.constructBetweenExcludingEndpoints(left, right);
                }
            }
        }
    }
}