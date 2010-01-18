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
 * Node.java
 * Created: Sep 23, 2002 at 2:42:30 PM
 * By: Raymond Cypher
 */
package org.openquark.cal.internal.machine.g;

import java.util.Collection;

import org.openquark.cal.compiler.DataConstructor;
import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.runtime.CalValue;
import org.openquark.cal.runtime.ErrorInfo;
import org.openquark.cal.runtime.MachineType;


/** 
 * This is the Node class/interface
 *
 * The is the abstract base for the nodes used to construct the program graph.
 * Creation: Aug 12, 2002 at 12:44:15 PM
 * @author rcypher
 */
public abstract class Node extends CalValue {

//  static int nodeOrdCount = 0;
//  int nodeOrd;

    Node() {
//      nodeOrd = nodeOrdCount++;
    }

    protected abstract String toString(int n);
    
    /**
     * {@inheritDoc}
     */
    @Override
    public abstract int debug_getNChildren();   
    /**
     * {@inheritDoc}
     */ 
    @Override
    public abstract CalValue debug_getChild(int childN);
    /**
     * {@inheritDoc}
     */
    @Override
    public abstract String debug_getNodeStartText();
    /**
     * {@inheritDoc}
     */
    @Override
    public abstract String debug_getNodeEndText();
    /**
     * {@inheritDoc}
     */
    @Override
    public abstract String debug_getChildPrefixText(int childN);
    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean debug_isIndirectionNode() {        
        return hasIndirection();
    }    
    
    /**
     * Generate a string of the form '#<ordinal value>: <type name>
     */
    protected String idString (int indent) {
        StringBuilder sp = new StringBuilder();
        for (int i = 0; i < indent; ++i) {
            sp.append(" ");
        }

        Class<? extends Node> cl = getClass ();
        String name = cl.getName ();
        name = name.substring (name.lastIndexOf ('.')+1, name.length());
        return sp.toString() + name;
//        return sp.toString() + "#" + nodeOrd + ", " + name;
    }

    protected abstract void addChildren(Collection<Node> c);

    protected void setIndirectionNode(Node n) {
        throw new UnsupportedOperationException("Setting indirection in bad node type. " + this.toString());
    }
    
    protected void setParent (Node n) {
    }

    public int getOrdinalValue () {
        throw new UnsupportedOperationException("Can't call getTagValue for class: " + getClass().getName());
    }    
    public Object getValue() {
        throw new UnsupportedOperationException("Can only call getValue for values or data constructors.");
    }

    protected boolean hasIndirection() {
        return false;
    }
    Node getIndirectionNode () {
        return null;
    }
    
    /*
     * Functions to make the state changes.
     */

    /**
     * Do Unwind state transition.
     */
    protected void i_unwind(Executor e) throws CALExecutorException {
        throw new UnsupportedOperationException("Cannot unwind on node of type. " + this.toString());
    }

    /**
     * Print
     */
    protected void i_print(Executor e) throws CALExecutorException {
        throw new CALExecutorException.InternalException("i_print: bad node type on stack.", null);
    }

    /**
     * Do a split state transition.
     */
    protected void i_split(Executor e, int n) throws CALExecutorException {
        //split state transitions are used for unpacking the arguments of
        //a data constructor (or data constructor like object such as an
        //integer value used in a case expression). Thus they are errors
        //unless there is a NVal or NConstr object on the stack     
        throw new CALExecutorException.InternalException("i_split: bad node type on stack. " + this.toString (), null);
    }

    /**
     * Gets the field for the indicated data constructor value.
     * @param dataConstructor the data constructor expected to represent the value.
     * @param fieldIndex the index of the field to retrieve.
     * @param errorInfo The error information that identifies the source position of the selection. May be null.
     * @return The selected field  
     * @throws CALExecutorException 
     */
    protected Node i_selectDCField(DataConstructor dataConstructor, int fieldIndex, ErrorInfo errorInfo) throws CALExecutorException {
        //select DC field is used for unpacking an argument of
        //a data constructor (or data constructor like object such as an
        //integer value used in a case expression). Thus they are errors
        //unless there is a NVal or NConstr object on the stack     
        throw new CALExecutorException.InternalException("i_selectDCField: bad node type on stack. " + this.toString (), null);
    }
    
    protected void i_dispatch (Executor e, int k) throws CALExecutorException {
        // The general case.
        // <f:n1:n2:...:nk:r:S, G[f=AP m1 m2], Dispatch k:[], D>
        // =>
        // <f:v1:v2:...:v(k-1):r:S, G[v1 = AP f n1              ], Unwind:[], D>
        //                           [vi = AP v(i-1), ni (1<i<k)]
        //                           [r = AP v(k-1) nk          ]
        
        int si = e.stack.size () - 2;
        
        for (int i=1; i < k; ++i) {
            e.stack.set (si, new NAp (e.stack.get (si+1), e.stack.get(si--)));
        }
        
        NAp na = new NAp (e.stack.get (si + 1), e.stack.get (si));
        e.stack.remove (si--);
        e.stack.get (si).setIndirectionNode (na);
        e.stack.set (si, na);

        e.stack.peek ().i_unwind (e);
    }

    /**
     * Do the Eval state transition.
     */
    protected abstract void i_eval(Executor e) throws CALExecutorException;

    /**
     * Build an application of deepSeq
     */
    protected abstract Node buildDeepSeqInternal(Node rhs) ;

    public final Node buildDeepSeq (Node rhs) {
        return buildDeepSeqInternal (rhs);
    }
    
    public Node getLeafNode () {
        return this;
    }
    
    /**
     * Apply this node to the given argument.
     * @param n
     * @return the application of this node to the argument.
     */
    public Node apply (Node n) {
        return new NAp (this, n);
    }
    
    /** {@inheritDoc} */
    @Override
    public final Node internalUnwrapOpaque() {
        if (this instanceof NValObject) {                       
            Object opaqueValue = ((NValObject)this).getValue();
            if (opaqueValue instanceof Node) {
                return (Node)opaqueValue;
            }            
        } 

        return this;          
    }    
    
    @Override
    public final MachineType debug_getMachineType() {
        return MachineType.G;
    }       
}
