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
 * NConstrGeneral.java
 * Created: Sep 23, 2002 at 3:07:31 PM
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
 * This is the type class/interface
 *
 * <p>
 * Creation: Sep 23, 2002 at 3:07:31 PM
 * @author Raymond Cypher
 */
final class NConstrGeneral extends NConstr {
    private Node[] nodes;
    
    NConstrGeneral (DataConstructor tag, int ord, Node [] nodes) {
        super (tag, ord);
        this.nodes = nodes;
    }
     
    @Override
    public String toString (int indent) {
        StringBuilder sp = new StringBuilder ();
        for (int i = 0; i < indent; ++i) {
            sp.append (" ");
        }
        
        StringBuilder sb = new StringBuilder ();
        sb.append (sp.toString());
        sb.append ("#" + getOrdinalValue () + ": NConstr <" + getTag() + ",\n");
        for (int i = 0; i < nodes.length; ++i) {
            Node node = nodes [i];
            //sb.append (node.toString (indent + 4));
            sb.append (node.idString(indent + 15));
            sb.append ("\n");
        }
        
        sb.append (sp.toString() + ">");
        
        return sb.toString ();
    }
    
    @Override
    protected void addChildren (Collection<Node> c) {
        if (nodes == null) {
            return;
        }
        
        for (int i = 0; i < nodes.length; ++i) {
            Node n = nodes [i];
            if (!c.contains (n)) {
                c.add (n);
                n.addChildren (c);
            }
        }
    }
    @Override
    protected int getArity () {
        if (nodes == null) {
            return 0;
        }
        return nodes.length;
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
        
        stack.pop ();
        if (n == 0) {
            return;
        }
        for (int i = 0; i < nodes.length; ++i) {
            Node v = nodes [i];
            if (v instanceof NInd) {
                v = (nodes [i] = v.getLeafNode());
            }
            stack.push (v);
        }
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
        
        if (fieldIndex < nodes.length) {
            Node n = nodes [fieldIndex];
            if (n instanceof NInd) {
                n = (nodes [fieldIndex] = n.getLeafNode());
            }
            return(n);
        
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
        for (int i = nodes.length-1; i>=0; --i) {
            Node n = nodes[i];
            if (n instanceof NInd) {
                n = (nodes[i] = n.getLeafNode());
            }
            rhs = NDeepSeq.instance.apply(n).apply(rhs);
        }
        return rhs;
    }
    
    /**     
     * {@inheritDoc}
     */
    @Override
    public final int debug_getNChildren() {        
        return nodes.length;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public final CalValue debug_getChild(int childN) {
        return nodes[childN];       
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
        if (childN >= 0 || childN < nodes.length) {
            return " ";
        }
        throw new IndexOutOfBoundsException();
    }    
}

