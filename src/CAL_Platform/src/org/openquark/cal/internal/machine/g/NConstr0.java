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
 * By: Bo Ilic
 */
package org.openquark.cal.internal.machine.g;

import java.util.Collection;

import org.openquark.cal.compiler.DataConstructor;
import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.runtime.CalValue;
import org.openquark.cal.runtime.ErrorInfo;


/** 
 * This is the NConstr0 class
 *
 * Represents a data constructor node of arity zero in the program graph.
 * Note: this doesn't include 0 arity constructors such as "5" or "True" which
 * are handled as NVal derived classes. Rather, it includes 0 arity constructors
 * such as Nothing and Spring.
 * 
 * Creation: Nov 20, 2002.
 * @author Bo Ilic
 */

final class NConstr0 extends NConstr {
 
    NConstr0 (DataConstructor tag, int ord) {
        super (tag, ord);       
    }
    
    NConstr0 (DataConstructor dataConstructor) {
        super (dataConstructor, dataConstructor.getOrdinal());       
    }
         
    @Override
    public String toString (int indent) {
        StringBuilder sp = new StringBuilder ();
        for (int i = 0; i < indent; ++i) {
            sp.append (" ");
        }
        
        StringBuilder sb = new StringBuilder ();
        sb.append (sp.toString());
        sb.append (idString(0) + " <" + getTag()  + ">,\n");
        return sb.toString ();
    }
    
    @Override
    protected void addChildren (Collection<Node> c) {
    }
        
    @Override
    protected int getArity () {
        return 0;
    }
           
    /**
     * Print
     */
    @Override
    protected void i_print (Executor e) throws CALExecutorException {
        e.stack.pop ();
        e.printVal = e.printVal + ": " + getTag();
    }
    
    /**
     * Do a split state transition.
     */
    @Override
    protected void i_split (Executor e, int n) throws CALExecutorException {
        e.stack.pop ();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Node i_selectDCField(DataConstructor dataConstructor, int fieldIndex, ErrorInfo errorInfo) throws CALExecutorException {
        if (dataConstructor != getTag()) {
            String message = "Wrong data constructor value selected.  Expecting: " + dataConstructor.getName() + 
                             ", found: " + getTag().getName() + ".";
            throw new CALExecutorException.ExternalException.PatternMatchFailure (errorInfo, message, null);
        }
        return null;
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
    public final String debug_getNodeStartText() {
        return getTag().getName().getQualifiedName();
    }
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