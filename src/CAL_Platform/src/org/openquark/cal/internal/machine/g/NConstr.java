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
 * NConstr.java
 * Created: Sep 23, 2002 at 3:02:36 PM
 * By: Raymond Cypher
 */
package org.openquark.cal.internal.machine.g;

import java.util.Collection;

import org.openquark.cal.compiler.DataConstructor;
import org.openquark.cal.runtime.CALExecutorException;


/** 
 * This is the NConstr class
 *
 * Represents the base class for all data constructors in the runtime.
 * Note: certain things that behave like 0-arity data constructors with
 * respect to the runtime (e.g. Int and Boolean values, which are used 
 * as conditions in a case expression), are not represented by this 
 * hierarchy but rather derive from NVal
 * 
 * Creation: Sep 23, 2002 at 3:02:36 PM
 * @author Raymond Cypher
 */

abstract class NConstr extends Node {
    private final DataConstructor tag;
    private final int ord;

    NConstr (DataConstructor tag, int ord) {
        assert (tag != null);
        this.tag = tag;
        this.ord = ord;
    }      
    
    @Override
    abstract protected void addChildren (Collection<Node> c);
    
    abstract protected int getArity ();
      
    protected DataConstructor getTag () {
        return tag;
    }
    
    @Override
    public int getOrdinalValue(){
        return ord;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final DataType getDataType() {
        return DataType.OTHER;
    }
   
    /**
     * Do the Eval state transition.
     */
    @Override
    protected void i_eval (Executor e) throws CALExecutorException {
        // Do nothing, a constructor is already in WHNF.
    }
             
    /**
     * Do Unwind state transition.
     */
    @Override
    protected void i_unwind (Executor e) throws CALExecutorException {
        e.popDumpItem();
    }
    
    /**
     * Do a split state transition.
     */
    @Override
    abstract protected void i_split (Executor e, int n) throws CALExecutorException;
     
}

