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
 * ValueNode.java
 * Creation date: (04/06/01 8:19:48 AM)
 * By: Michael Cheng
 */
package org.openquark.cal.valuenode;

import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.compiler.io.InputPolicy;
import org.openquark.cal.compiler.io.OutputPolicy;


/**
 * The underlying tree data structure used to represent values.
 * ValueNodes are immutable:
 * @author Michael Cheng
 */
public abstract class ValueNode {

    /** The TypeExpr of the data value which this ValueNode represents. */
    private final TypeExpr typeExpr;

    /**
     * ValueNode constructor.
     * @param typeExprParam indicates the TypeExpr of the data value which this ValueNode represents.
     */
    public ValueNode(TypeExpr typeExprParam) {
        super();
        this.typeExpr = typeExprParam;
    }

    /**
     * Helper method for copyValueNode().  Checks to see if the new typeExpr is ok for the value node.
     * Throws an exception if not.
     */
    protected final void checkCopyType(TypeExpr newTypeExpr) {
        // If the type expr is null, we're not valid anyway.  (Maybe we got it from the ValueRunner).
        if (typeExpr == null) {
            return;
        }

        // check that the types are equivalent
        if (!typeExpr.sameType(newTypeExpr)) {
            throw new IllegalStateException("Programming error: attempting to copy a value node replacing type " + typeExpr + " with type " + newTypeExpr);
        }
    }
    
    /**
     * Helper method for ValueNode constructors to check if a type expression has a given type constructor
     * as its root.
     * @param typeExpr the type expression to check
     * @param typeConsName the type constructor name to check for
     * @throws IllegalArgumentException if the pruned type expression is not a type constructor with the given name
     */
    protected static final void checkTypeConstructorName(TypeExpr typeExpr, QualifiedName typeConsName) {
        
        if (!typeExpr.hasRootTypeConstructor(typeConsName)) {       
            throw new IllegalArgumentException("type expression is not type constructor " + typeConsName + ": " + typeExpr);
        }
    }
    
    /**
     * Makes a copy of this ValueNode (the copy will have the same value, TypeExpr, etc.)
     * Note: The returned copy will be the same type as this ValueNode.
     * (Eg: A ColourValueNode's copyValueNode method will return a ColourValueNode.)
     * @return ValueNode
     */
    public final ValueNode copyValueNode() {

        ValueNode vn = copyValueNode(typeExpr);
        return vn;
    }

    /**
     * Makes a copy of this ValueNode, but with another TypeExpr instance (of the same type).
     * This is a deep copy, with respect to value nodes and the associated type expression.
     * Note: if the new TypeExpr is a different type from the present TypeExpr, an error is thrown.
     * @param newTypeExpr the new type of the copied node.
     * @return ValueNode
     */
    public abstract ValueNode copyValueNode(TypeExpr newTypeExpr);

    /**
     * Returns the CAL text representation of the expression represented by this ValueNode.
     */
    public final String getCALValue() {
        return getCALSourceModel().toSourceText();
    }

    /**
     * @return the source model representation of the expression represented by this ValueNode.
     */
    public abstract SourceModel.Expr getCALSourceModel();    
    /**
     * Returns whether this node contains a parametric value node
     * @return boolean
     */
    public abstract boolean containsParametricValue();
    
    /**  
     * @return Object the most convenient Java representation of the value held onto by this ValueNode.
     * The contract of this method is that the returned Object is immutable, since ValueNodes are
     * intended to be immutable value classes. For example, if this method were to return a List
     * or other Collection object, the caller should assume that the Collection is not modifiable.
     */
    abstract public Object getValue();    

    /**
     * Return an array of objects which are the values needed by the marshaller
     * described by 'getInputPolicy()'.
     * @return - an array of Java objects corresponding to the value represented by a value node instance.
     */
    abstract public Object[] getInputJavaValues();
    
    /**
     * Return an input policy which describes how to marshall a value represented
     * by a value node from Java to CAL.
     * @return - the input policy associated with ValueNode instance.
     */
    abstract public InputPolicy getInputPolicy();
    
    /**
     * Return an output policy which describes how to marshall a value represented
     * by a value node from CAL to Java.
     * @return - the output policy associated with the ValueNode instance.
     */
    abstract public OutputPolicy getOutputPolicy();
    
    /**
     * Set a value which is the result of the marshaller described by
     * 'getOutputPolicy()'.
     * @param value - the java value
     */
    abstract public void setOutputJavaValue(Object value);
    
    /** 
     * Returns the display text representation of the expression represented by this ValueNode.
     * @return String
     */
    public abstract String getTextValue();
    
    @Override
    public String toString() {
        return getTextValue();
    }

    /**
     * Returns the TypeExpr of the data value represented by this ValueNode.
     * @return TypeExpr
     */
    public TypeExpr getTypeExpr() {
        return typeExpr;
    }

    /**
     * Returns whether the object in the argument is the same as this valueNode in terms of their type and CAL value 
     * @param object
     * @return boolean
     */
    public boolean sameValue(Object object) {
        if (object instanceof ValueNode) {
            ValueNode valueNode = (ValueNode) object;
            return (getTypeExpr().sameType(valueNode.getTypeExpr()) && getCALValue().equals(valueNode.getCALValue()));
        }
        return false;
    }
    
    /**
     * Returns a new ValueNode according to type TypeExpr, and modeled after valueNode (which means that the returned ValueNode
     * will have as much common data as possible with valueNode)..
     * 
     * This will return a new ValueNode with newTypeExpr as its assigned type expression. It does NOT provide a copy
     * of the newTypeExpr. So be careful not to share this newTypeExpr amongst multiple nodes!
     * 
     * However, to copy the value of valueNode over to the returned ValueNode, the newTypeExpr that you pass in must be compatible with 
     * the type expression of the value node that you are copying from. (Which means you should create a new TypeExpr and call 
     * TransmuteTypeExpr on it to have it correspond), Otherwise, the valueNode that you create will really have nothing in common with 
     * the valueNode. 
     * @param valueNodeBuilderHelper
     * @param valueNodeTransformer
     * @param newTypeExpr
     * @return ValueNode a new ValueNode with newTypeExpr as its TypeExpr, and having as much common data with this ValueNode as possible.
     */ 
    public abstract ValueNode transmuteValueNode(ValueNodeBuilderHelper valueNodeBuilderHelper, ValueNodeTransformer valueNodeTransformer, TypeExpr newTypeExpr);

}
