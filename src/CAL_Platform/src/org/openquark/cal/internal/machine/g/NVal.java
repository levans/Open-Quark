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
 * NVal.java
 * Created: Oct 23, 2002 at 5:51:52 PM
 * By: Raymond Cypher
 */
package org.openquark.cal.internal.machine.g;

import java.util.Collection;

import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.runtime.CalValue;


/** 
 * This is the NVal class/interface
 *
 * Base class for different value node types.
 * Creation: Oct 23, 2002 at 5:51:52 PM
 * @author Raymond Cypher
 */
abstract class NVal extends Node {
    /**
     * Return the value this object wraps.
     */
    @Override
    public abstract Object getValue ();
    
    /*
     * Functions to perform state transitions.
     */
     
    /**
     * Do the Eval state transition.
     */
    @Override
    protected void i_eval (Executor e) throws CALExecutorException {
        // Do nothing, a value is already in WHNF.
    }
     
    @Override
    protected void i_unwind (Executor e) throws CALExecutorException {
        e.popDumpItem();
    } 
    
    /**
     * Print
     */
    @Override
    protected void i_print (Executor e) throws CALExecutorException {
        e.printVal = e.printVal + ": " + getValue().toString();
        e.stack.pop ();
    }
    
    /**
     * Do a split state transition.
     */
    @Override
    protected void i_split(Executor e, int n) throws CALExecutorException {
        //value objects are like 0-arity data constructor with respect to
        //case expression unpacking. For example, we can think of 
        //data Int = 0 | 1 | -1 | 2 | -2 | 3 ...
        e.stack.pop();       
    }
    
    @Override
    public void addChildren (Collection<Node> c) {
    }
    
    /**
     * Build an application of deepSeq
     */
    @Override
    protected Node buildDeepSeqInternal(Node rhs) {
        return rhs;
    }
    
    /**     
     * {@inheritDoc}
     */
    @Override
    public final int debug_getNChildren() {
        //value nodes have no children
        return 0;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public final CalValue debug_getChild(int childN) {
        throw new IndexOutOfBoundsException();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public abstract String debug_getNodeStartText();
    /**
     * {@inheritDoc}
     */
    @Override
    public final String debug_getNodeEndText() {
        return "";
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public final String debug_getChildPrefixText(int childN) {
        throw new IndexOutOfBoundsException();
    }
    
}
