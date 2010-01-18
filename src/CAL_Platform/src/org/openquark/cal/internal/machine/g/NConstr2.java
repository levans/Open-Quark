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
 * NConstr2.java
 * Created: Sep 23, 2002 at 3:13:20 PM
 * By: Raymond Cypher
 */
package org.openquark.cal.internal.machine.g;

import java.util.Collection;

import org.openquark.cal.compiler.DataConstructor;
import org.openquark.cal.internal.machine.g.functions.NDeepSeq;
import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.runtime.CalValue;
import org.openquark.cal.runtime.ErrorInfo;


/** 
 * This is the NConstr2 class
 *
 * Represents a Constructor node of arity 2 in the
 * program graph.
 * Creation: Sep 23, 2002 at 3:13:20 PM
 * @author Raymond Cypher
 */
public class NConstr2 extends NConstr {
    private Node n0;
    private Node n1;
    
    NConstr2 (DataConstructor tag, int ord, Node n0, Node n1) {
        super (tag, ord);
        this.n0 = n0;
        this.n1 = n1;
//        if (n0 instanceof NValChar) {
//            System.out.println ("zz");
//        }
    }
    
    NConstr2 (DataConstructor dataConstructor, Node n0, Node n1) {
        super (dataConstructor, dataConstructor.getOrdinal());
        this.n0 = n0;
        this.n1 = n1;
    }    

    NConstr2 () {
        this (null, 0, null, null);
    }
    
    public Node getN0() {
        Node n = n0;
        if (n instanceof NInd) {
            return (n0 = n.getLeafNode());
        }
        return n;
    }
    
    public Node getN1() {
        Node n = n1;
        if (n instanceof NInd) {
            return (n1 = n.getLeafNode());
        }
        return n;
    }
         
    @Override
    public String toString (int indent) {
        StringBuilder sp = new StringBuilder ();
        for (int i = 0; i < indent; ++i) {
            sp.append (" ");
        }
        
        StringBuilder sb = new StringBuilder ();
        sb.append (sp.toString());
        sb.append (idString(0) + " <" + getTag()  + ",\n");

        sb.append (n0.idString (indent + 16));
        sb.append ("\n");
        sb.append (n1.idString (indent + 16));
        sb.append ("\n");
        
        sb.append (sp.toString() + ">");
        
        return sb.toString ();
    }
    
    @Override
    protected void addChildren (Collection<Node> c) {
        if (!c.contains (n0)) {
            c.add (n0);
            n0.addChildren (c);
        }
        if (!c.contains (n1)) {
            c.add (n1);
            n1.addChildren (c);
        }
    }
    @Override
    protected int getArity () {
        return 2;
    }
    
    /*
     * Functions to perform state transitions.
     */
     
    
    /**
     * Do a split state transition.
     */
    @Override
    protected void i_split (Executor e, int n) throws CALExecutorException {
        
        Executor.GStack stack = e.stack;
        
        // pops the dc off the stack, and pushes its args.
        stack.pop ();
        if (n == 0) {
            return;
        }
        stack.push (getN0());
        stack.push (getN1());
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
        
        if (fieldIndex == 0) {
            return(getN0());
        
        } else if (fieldIndex == 1) {
            return(getN1());
        
        } else {
            String message = "Invalid field index: " + fieldIndex + " for data constructor " + dataConstructor.getName();
            throw new CALExecutorException.InternalException (errorInfo, message, null);
        }
    }
    
    /**
     * Build an application of deepSeq
     */
    @Override
    protected Node buildDeepSeqInternal(Node rhs) {
        return NDeepSeq.instance.apply (getN0()).apply(NDeepSeq.instance.apply(getN1()).apply(rhs));
    }
    
    /**     
     * {@inheritDoc}
     */
    @Override
    public final int debug_getNChildren() {        
        return 2;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public final CalValue debug_getChild(int childN) {
        switch (childN) {
        case 0:
            return n0;
        case 1:
            return n1;
        default:
            throw new IndexOutOfBoundsException();
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public final String debug_getNodeStartText() {
        return "(" + getTag().getName().getQualifiedName();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public final String debug_getNodeEndText() {
        return ")";
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public final String debug_getChildPrefixText(int childN) {
        if (childN == 0 || childN == 1) {
            return " ";
        }
        throw new IndexOutOfBoundsException();
    }        
}



