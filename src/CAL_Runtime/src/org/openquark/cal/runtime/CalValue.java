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
 * CalValue.java
 * Created: April 2, 2004 
 * By: Raymond Cypher
 */
package org.openquark.cal.runtime;




/**
 * This is the base class used by CAL for values. 
 * 
 * @author RCypher
 */
public abstract class CalValue {    
    
    /**
     * An enumeration for the underlying value type of an internal value.
     *
     * @author Joseph Wong
     */
    public static enum DataType {
        
        CHAR("char"),
        BOOLEAN("boolean"),
        BYTE("byte"),
        SHORT("short"),
        INT("int"),
        LONG("long"),
        FLOAT("float"),
        DOUBLE("double"),
        OBJECT("object"),
        OTHER("other");
        
        /** A display name for the type. */
        private final String name;
        
        /**
         * Constructs an {@link CalValue.DataType}.
         * @param name a display name for the type.
         */
        private DataType(String name) {
            this.name = name;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return name;
        }
    }
    
    /**     
     * Warning: for debugging use only. Implementations must not modify this CalValue in any way.
     * debug_getNChildren and debug_getChild provide an abstract way for traversing the value graph.
     * 
     * See the implementation of DebugSupport.javaStackShowInternal to see the contract that implementers
     * of this method must adhere to.
     * 
     * @return the number of children of this node.
     */
    public abstract int debug_getNChildren();   
    /**     
     * Warning: for debugging use only. Implementations must not modify this CalValue in any way.
     * debug_getNChildren and debug_getChild provide an abstract way for traversing the value graph.
     * 
     * See the implementation of DebugSupport.javaStackShowInternal to see the contract that implementers
     * of this method must adhere to.
     * 
     * @param childN the zero-based child number
     * @return the child at the specified childN index. It is possible for this to be null
     *    when attempting to view an CalValue that is in the proces of being reduced. 
     */    
    public abstract CalValue debug_getChild(int childN);
    /**
     * Warning: for debugging use only. Implementations must not modify this CalValue in any way.
     * 
     * See the implementation of DebugSupport.javaStackShowInternal to see the contract that implementers
     * of this method must adhere to.
     * 
     * @return the start string for this node. Cannot be null.
     */
    public abstract String debug_getNodeStartText();
    /**
     * Warning: for debugging use only. Implementations must not modify this CalValue in any way.
     * 
     * See the implementation of DebugSupport.javaStackShowInternal to see the contract that implementers
     * of this method must adhere to.
     * 
     * @return the end string for this node. Cannot be null.
     */
    public abstract String debug_getNodeEndText();
    /**
     * Warning: for debugging use only. Implementations must not modify this CalValue in any way.
     * 
     * See the implementation of DebugSupport.javaStackShowInternal to see the contract that implementers
     * of this method must adhere to.
     *  
     * @param childN
     * @return string immediately prior to any text for the child at index childN.  Cannot be null.
     */
    public abstract String debug_getChildPrefixText(int childN);
    
    /** 
     * Warning: for debugging use only. Implementations must not modify this CalValue in any way.
     *     
     * Returns true if this node is only an indirection to another node and thus behaves in all respects like that
     * other node, masking its own identity. If Java supported memory overwrites, then instead of having indirection
     * nodes we could directly update memory to change the type of a node. 
     * 
     * This method is mainly intended to support cross-machine debugging.
     * 
     * @return true if this node is an indirection node.
     */
    public abstract boolean debug_isIndirectionNode();
    
    /**
     * Returns the type of the underlying value associated with this CalValue.
     * 
     * For subclasses that do not represent primitive data types, the value
     * {@link CalValue.DataType#OTHER} should be returned.
     * 
     * @return the type of the underlying value associated with this CalValue.
     */
    public abstract DataType getDataType();
    
    /**
     * @return a string representation of this CalValue for debugging purposes. This representation
     * is subject to change and should not be relied upon in production code. Does not do any 
     * evaluation or modify the CalValue in any way.
     */
    @Override
    public final String toString() {        
        return DebugSupport.showInternalGraph(this);               
    } 
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(Object other) {
        //we need CalValue.equals() and hashCode to always be based on references so that the DebugSupport classes
        //correctly deal with sharing.
        //This is why CalValue.equals and hashCode are marked as final, delegating to Object's implementation.
        return super.equals(other);
    }
    
    /**
     * {@inheritDoc}
     */    
    @Override
    public final int hashCode() {
        //we need CalValue.equals() and hashCode to always be based on references so that the DebugSupport classes
        //correctly deal with sharing.
        //This is why CalValue.equals and hashCode are marked as final, delegating to Object's implementation.
        return super.hashCode();
    }
    
    /**    
     * @return if this CalValue has type DataType.Object, and the underlying Object is in
     *    fact a CalValue, then return that CalValue, otherwise just return this value.   
     */
    public abstract CalValue internalUnwrapOpaque();    
    
    
    /**
     * @return the MachineType in which this CalValue was created.
     */
    public abstract MachineType debug_getMachineType();    
}


