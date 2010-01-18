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
 * ValueNodeProvider.java
 * Creation date: Nov 4, 2003
 * By: Frank Worsley
 */
package org.openquark.cal.valuenode;

import org.openquark.cal.compiler.DataConstructor;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.services.Perspective;


/**
 * A value node provider is used to provide information about a particular type of value node
 * and get an instance of a value node.
 * 
 * @param <T> the ValueNode class provided by this provider.
 * 
 * @author Frank Worsley
 */
public abstract class ValueNodeProvider<T extends ValueNode> {

    /** The value node builder helper used by this provider. */
    private final ValueNodeBuilderHelper builderHelper;
    
    /**
     * Constructs a new ValueNodeProvider.
     * @param builderHelper the value node builder helper to use for building value nodes.
     */
    public ValueNodeProvider(ValueNodeBuilderHelper builderHelper) {
        
        if (builderHelper == null) {
            throw new NullPointerException();
        }
        
        this.builderHelper = builderHelper;
    }

    /**
     * @return the Class of the ValueNode provided by this provider.
     */
    public abstract Class<T> getValueNodeClass();

    /**
     * Initializes a new instance of the value node this provider is for using the given information.
     * To construct a default value for a given type, pass in null for the first two arguments.
     * 
     * @param value The value that will be held onto.  
     *   The object should be of a type appropriate for the value node suitable for the specified type expression
     * @param dataConstructor The CAL data constructor for the specified object.
     * @param typeExpr The type expression of the specified object.  
     * @return Returns a newly constructed value node initialized with the given value, or null if the given type
     *   is not handled by this provider.
     */
    public abstract T getNodeInstance(Object value, DataConstructor dataConstructor, TypeExpr typeExpr);

    /**
     * @param typeExpr the type expression to create a value node for
     * @return a new instance of the value node this provider is for, initialized with the given type expression
     */
    public final T getNodeInstance(TypeExpr typeExpr) {
        return getNodeInstance(null, null, typeExpr);
    }
    
    /**
     * Whether or not the value node this provider is for requires special literalization to be turned
     * on in the ValueNodeBuilderHelper. This is true for all value nodes except literal, foreign, explicitly
     * refined and data constructor value nodes. Therefore the default return value is true.
     * @return whether or not the value node this provider is for requires special literlization to be on
     */
    public boolean isSpecialLiteralizedValueNode() {
        return true;
    }
    
    /**
     * @return the value node builder helper used by this provider
     */
    protected ValueNodeBuilderHelper getValueNodeBuilderHelper() {
        return builderHelper;
    }

    /**
     * @return the perspective used by this provider
     */
    protected Perspective getPerspective() {
        return builderHelper.getPerspective();
    }
}
