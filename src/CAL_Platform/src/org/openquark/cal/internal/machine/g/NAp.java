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
 * NAp.java
 * Created: Sep 23, 2002 at 2:49:09 PM
 * By: Raymond Cypher
 */
package org.openquark.cal.internal.machine.g;

import java.util.Collection;

import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.runtime.CalValue;


/** 
 * This is the NAp class
 *
 * This class is used to represent an application node in 
 * the program graph.
 * Creation: Sep 23, 2002 at 2:49:09 PM
 * @author Raymond Cypher
 */

public final class NAp extends NInd {
    
    private Node n1;
    private Node n2;
    
    public NAp (Node n1, Node n2) {
        super ();
        
        if (n1 == null || n2 == null) {
            throw new IllegalArgumentException ("The arguments n1 and n2 to the NAp constructor cannot be null.");
        }
        
        this.n1 = n1;
        this.n2 = n2;
        
    }
    
    /**
     * @return if the application is (@ n1 n2) then this is n1.
     */
    Node getN1() {
        return n1;
    }
    
    /**
     * @return if the application is (@ n1 n2) then this is n2.
     */
    Node getN2() {
        return n2;
    }
        
    @Override
    public String toString (int indent) {
        if (getIndirectionNode() != null) {
            return indString (indent);
        } else {
            StringBuilder sp = new StringBuilder ();
            for (int i = 0; i < indent; ++i) {
                sp.append (" ");
            }
            
            StringBuilder sb = new StringBuilder ();
            sb.append (sp.toString ());
            sb.append (idString(0) + " <\n");
            sb.append (n1.idString (indent + 12) + ", \n");
            sb.append (n2.idString (indent + 12) + "\n" + sp.toString() + ">");
            return sb.toString ();
        }
    }
    
    /**     
     * {@inheritDoc}
     */
    @Override
    public final int debug_getNChildren() {  
        if (hasIndirection()) {
            return super.debug_getNChildren();           
        }        
     
        return 2;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public final CalValue debug_getChild(int childN) {
        if (hasIndirection()) {
            return super.debug_getChild(childN);           
        }
        
        switch (childN) {
        case 0:
            return n1;
        case 1:
            return n2;
        default:                   
            throw new IndexOutOfBoundsException();
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public final String debug_getNodeStartText() {
        if (hasIndirection()) {
            return super.debug_getNodeStartText();           
        }        
        
        return "(";
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public final String debug_getNodeEndText() {
        if (hasIndirection()) {
            return super.debug_getNodeEndText();           
        } 
        
        return ")";
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public final String debug_getChildPrefixText(int childN) {
        if (hasIndirection()) {
            return super.debug_getChildPrefixText(childN);           
        }
        
        switch (childN) {
        case 0:
            return "";
        case 1:
            return " ";
        default:
            throw new IndexOutOfBoundsException();             
        }       
    }        
    
    @Override
    protected void addChildren (Collection<Node> c) {
        if (getIndirectionNode() != null) {
            if (!c.contains (getIndirectionNode())) {
                c.add (getIndirectionNode());
                getIndirectionNode().addChildren (c);
            }
            return;
        }
        
        if (!c.contains (n1)) {
            c.add (n1);
            n1.addChildren (c);
        }
        if (!c.contains (n2)) {
            c.add (n2);
            n2.addChildren (c);
        }
    }
    
    @Override
    protected void setIndirectionNode (Node n) {
        super.setIndirectionNode (n);
        n1 = null;
        n2 = null;
    }
    
    /*
     * Functions to perform state transitions.
     */
    @Override
    protected void i_unwind (Executor e) throws CALExecutorException {
        if (getLeafNode() != this) {
            e.stack.pop ();
            e.stack.push (getLeafNode());
            getLeafNode().i_unwind (e);
            return;
        }
        
        e.stack.push (n1);
        n1.i_unwind (e);
    }

    /**
     * Do the Eval state transition.
     */
    @Override
    protected void i_eval(Executor e) throws CALExecutorException {
        if (getLeafNode() != this) {
            e.stack.pop();
            e.stack.push (getLeafNode());
            getLeafNode().i_eval(e);
            return;
        }

        e.pushDumpItem();
        i_unwind(e);
    }

    void clear () {
        n1 = null;
        n2 = null;
    }
}   
