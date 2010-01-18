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
 * AlgebraicValueNode.java
 * Created: April 10, 2003
 * By: Bo Ilic
 */
package org.openquark.cal.valuenode;

import org.openquark.cal.compiler.TypeExpr;

/**
 * AlgebraicValueNode is used as a base class for representing values that within CAL are of a
 * type given by a non-foreign data declaration. In other words they are values of a "algebraic" data type.
 * Such values are decomposable in CAL using case expressions and are not "opaque" from the point of view of
 * CAL. 
 * 
 * @author Bo Ilic
 */
public abstract class AlgebraicValueNode extends ValueNode {
    
    public AlgebraicValueNode (TypeExpr typeExpr) {
        super (typeExpr);
    }
    
    /**
     * @see ValueNode#transmuteValueNode(ValueNodeBuilderHelper, ValueNodeTransformer, TypeExpr)
     */
    @Override
    public ValueNode transmuteValueNode(ValueNodeBuilderHelper valueNodeBuilderHelper, ValueNodeTransformer valueNodeTransformer, TypeExpr newTypeExpr) {
        // Eg: For ColourValueNode, or ListOfCharValueNode, or...
        // Also for foreign value nodes
        Class<? extends ValueNode> handlerClass = valueNodeBuilderHelper.getValueNodeClass(newTypeExpr);
        if ((getClass().equals(handlerClass))) {
            return valueNodeBuilderHelper.buildValueNode(getValue(), null, newTypeExpr);
        } else {
            return valueNodeTransformer.transform(valueNodeBuilderHelper, this, newTypeExpr);
        }
    }
}
