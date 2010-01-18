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
 * NInd.java
 * Created: Sep 23, 2002 at 2:59:58 PM
 * By: Raymond Cypher
 */
package org.openquark.cal.internal.machine.g;

import java.util.Collection;

import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.runtime.CalValue;


/** 
 * This is the NInd class
 *
 * Used to represent an indirection node in the
 * program graph.
 * Creation: Sep 23, 2002 at 2:59:57 PM
 * @author Raymond Cypher
 */

class NInd extends Node {
    
    private Node indirectionNode;
    private Node parent;
    
    NInd () {
        super ();
        //the Node.indirection has the default of null upon construction.
        //In fact, the NInd node type is a placeholder where we know we
        //need a Node, but don't know what kind yet.
    }   
  
    @Override
    public String toString (int indent) {
        StringBuilder sp = new StringBuilder ();
        for (int i = 0; i < indent; ++i) {
            sp.append (" ");
        }
        
        if (indirectionNode == null) {
            return sp.toString() + ": NInd <null>";
//            return sp.toString() + "#" + nodeOrd + ": NInd <null>";
        }
        return indString (indent);
    }
    
    /**     
     * {@inheritDoc}
     */
    @Override
    public int debug_getNChildren() {            
        if (indirectionNode == null) {
            throw new IllegalStateException("should call the overidden version if indirectionNode == null");
        }
        return 1;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public CalValue debug_getChild(int childN) {
        if (indirectionNode == null) {
            throw new IllegalStateException("should call the overidden version if indirectionNode == null");
        }
        
        if (childN == 0) {
            return indirectionNode;
        } 
        
        throw new IndexOutOfBoundsException();        
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String debug_getNodeStartText() {
        if (indirectionNode == null) {
            throw new IllegalStateException("should call the overidden version if indirectionNode == null");
        }        
        
        return "";
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String debug_getNodeEndText() {
        if (indirectionNode == null) {
            throw new IllegalStateException("should call the overidden version if indirectionNode == null");
        }
        
        return "";
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String debug_getChildPrefixText(int childN) {
        if (indirectionNode == null) {
            throw new IllegalStateException("should call the overidden version if indirectionNode == null");
        }
        
        if (childN == 0) {
            return "";
        }
        throw new IndexOutOfBoundsException();
    }    
    
    @Override
    Node getIndirectionNode() {
        return indirectionNode;
    }
    
    @Override
    protected void setIndirectionNode(Node n) {
        n = n.getLeafNode();
        if (n != this) {
            if (this.parent == null) {
                (indirectionNode = n).setParent(this);
            } else {
                this.parent.setIndirectionNode(indirectionNode = n);
            }
        }
    }
    @Override
    protected void setParent(Node n) {
        this.parent = n;
    }
    protected void clearIndirectionNode() {
        indirectionNode = null;
    }
    
    @Override
    public Node getLeafNode () {
        if (indirectionNode == null) {
            return this;
        } 
        
        Node temp = indirectionNode;
        while (temp.hasIndirection()) {
            temp = temp.getIndirectionNode();
        }
        
        return (indirectionNode = temp);
    }
    
    @Override
    protected void addChildren(Collection<Node> c) {
        if (indirectionNode != null) {
            if (!c.contains(indirectionNode)) {
                c.add(indirectionNode);
                indirectionNode.addChildren(c);
            }
        }
    }

    protected String indString(int indent) {
        StringBuilder sp = new StringBuilder();
        for (int i = 0; i < indent; ++i) {
            sp.append(" ");
        }
        return sp.toString()
//            + "#"
//            + nodeOrd
            + ": NInd <\n"
            + indirectionNode.idString(indent + 12)
            + "\n"
            + sp.toString()
            + "           >";
    }

    /**
     * Generate a string of the form '#<ordinal value>: <type name>
     */
    @Override
    protected String idString (int indent) {
        if (indirectionNode == null) {
            return super.idString (indent);
        } else {
            return indirectionNode.idString (indent);
        }
    }
    
    /*
     * Functions to perform state transisions.
     */
    /**
     * Do the Eval state transition.
     */
    @Override
    protected void i_eval (Executor e) throws CALExecutorException {
        
        if (getIndirectionNode() != null) {
            e.stack.pop ();
            e.stack.push (getIndirectionNode().getLeafNode());
            getIndirectionNode().getLeafNode().i_eval(e);
            return;
        }

        throw new CALExecutorException.InternalException ("Null indirection in NInd node.", null);        
    }
    
    @Override
    protected void i_unwind (Executor e) throws CALExecutorException {
        
        Executor.GStack stack = e.stack;
        
        stack.pop ();
        stack.push (indirectionNode.getLeafNode());
        indirectionNode.getLeafNode().i_unwind (e);
    }
    
    @Override
    protected boolean hasIndirection () {
        return indirectionNode != null;
    }
    
    /**
     * Build an application of deepSeq
     */
    @Override
    protected Node buildDeepSeqInternal(Node rhs) {
        throw new UnsupportedOperationException("buildDeepSeq() should only be called on a WHNF.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataType getDataType() {
        if (indirectionNode != null) {
            return indirectionNode.getDataType();
        } else {
            return DataType.OTHER;
        }
    }        
}

