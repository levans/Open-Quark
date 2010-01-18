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
 * OpaqueValueNode.java
 * Created: 7-Apr-2003
 * By: David Mosimann
 */
package org.openquark.cal.valuenode;

import org.openquark.cal.compiler.TypeExpr;

/**
 * This is an abstract base class used to represent an opaque value.  The value could be some form of literal
 * value or a foreign type.  It is used to make a distinction in the class heirarchy between a literal value
 * and a foreign value. 
 * The key property of an opaque value is that its internal representation within CAL is exactly the same
 * as its external representation in Java. So subclasses of this class are just a lightweight wrapper
 * around the corresponding Java object, with some helper methods.
 */
public abstract class OpaqueValueNode extends ValueNode {
    
    /**
     * The value being held onto.
     */
    private Object value;

    /**
     * @param value
     * @param typeExprParam 
     */
    public OpaqueValueNode(Object value, TypeExpr typeExprParam) {
        super(typeExprParam);
        this.value = value;
    }

    /**
     * Returns the value.
     * @return Object
     */
    @Override
    public Object getValue() {
        return value;
    }

    /**
     * Return an array of objects which are the values needed by the marshaller
     * described by 'getInputPolicy()'.
     * @return - an array of Java objects corresponding to the value represented by a value node instance.
     */
    @Override
    public Object[] getInputJavaValues () {
        return new Object[]{value};
    }
    
    /**
     * Set a value which is the result of the marshaller described by
     * 'getOutputPolicy()'.
     * @param value - the java value
     */
    @Override
    public void setOutputJavaValue(Object value) {
        this.value = value;
    }
    
    
    /**
     * An opaque value shouldn't contain a parametric value.
     * @return boolean
     */
    @Override
    public final boolean containsParametricValue() {
        return false;
    }
    
    /**
     * @see ValueNode#transmuteValueNode(ValueNodeBuilderHelper, ValueNodeTransformer, TypeExpr)
     */
    @Override
    public ValueNode transmuteValueNode(ValueNodeBuilderHelper valueNodeBuilderHelper, ValueNodeTransformer valueNodeTransformer, TypeExpr newTypeExpr) {
        //  Eg: For ColourValueNode, or ListOfCharValueNode, or...
         // Also for foreign value nodes
        if (newTypeExpr.sameType(getTypeExpr())) {
            return copyValueNode(newTypeExpr);
        }
         
        return valueNodeTransformer.transform(valueNodeBuilderHelper, this, newTypeExpr);
    }
}
