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
 * NRecordExtension.java
 * Created: Apr 12, 2004
 * By: RCypher
 */

package org.openquark.cal.internal.machine.g;

import java.util.Collection;
import java.util.Iterator;

import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.runtime.CalValue;


/**
 * NRecordExtension
 * @author RCypher
 * Created: Apr 12, 2004
 */
class NRecordExtension extends NRecordValue {

    /** Graph node representing the record being extended. */
    private final Node baseRecord;
    
    /** Code representing the actions of this node. */
    private Code code;
    
    /**
     * Create a node representing the application of a virtual
     * extension function to a base record.
     * @param baseRecord
     */
    NRecordExtension (Node baseRecord) {
        super();
        if (baseRecord == null) {
            throw new NullPointerException();
        }
        this.baseRecord = baseRecord;
    }
    
    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.g.Node#toString(int)
     */
    @Override
    public String toString(int n) {
        StringBuilder is = new StringBuilder();
        for (int i = 0; i < n; ++i) {
            is.append(" ");
        }
        
        return is + idString(0);
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean isTuple2OrMoreRecord() {
        return false;
    }

    /**     
     * {@inheritDoc}
     */
    @Override
    public final int debug_getNChildren() {  
        if (hasIndirection()) {
            return super.debug_getNChildren();           
        }        
        
        //1 for the record extension
        return super.debug_getNChildren() + 1;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public final CalValue debug_getChild(int childN) {
        if (hasIndirection()) {
            return super.debug_getChild(childN);           
        }
        
        if (childN == 0) {
            return baseRecord;
        }
        
        return super.debug_getChild(childN - 1);                  
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public final String debug_getNodeStartText() {
        if (hasIndirection()) {
            return super.debug_getNodeStartText();           
        }
               
        return "{";
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public final String debug_getNodeEndText() {
        if (hasIndirection()) {
            return super.debug_getNodeEndText();           
        } 
                 
        return "}";
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public final String debug_getChildPrefixText(int childN) {
        if (hasIndirection()) {
            return super.debug_getChildPrefixText(childN);           
        }
        
        if (childN < 0 || childN > fieldToValueMap.size()) {
            throw new IndexOutOfBoundsException();
        }
        
        if (childN == 0) {
            return "";
        }
         
        String childPrefix = super.debug_getChildPrefixText(childN - 1);
        if (childN == 1) {
            childPrefix =  " | " + childPrefix;                    
        }
        return childPrefix;        
    }    
    
    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.g.Node#addChildren(java.util.Collection)
     */
    @Override
    protected void addChildren(Collection<Node> c) {
        super.addChildren (c);
        if (!c.contains(baseRecord)) {
            c.add(baseRecord);
            baseRecord.addChildren(c);
        }
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.g.Node#i_eval(org.openquark.cal.internal.runtime.g.Executor)
     */
    @Override
    protected void i_eval(Executor e) throws CALExecutorException {
        e.pushDumpItem();
        i_unwind (e);
    }
    
    /**
     * Generate the code for the virtual function represented
     * by this node.
     * @return the code.
     */
    private Code getCode () {
        if (code == null) {
            Instruction i[] = new Instruction [6 +(fieldToValueMap.size() * 2)];
            int inst = 0;
            // First evaluate the base record.
            i[inst++] = new Instruction.I_Push(0);
            i[inst++] = Instruction.I_Eval;
            
            // Create a copy of the base record.
            i[inst++] = new Instruction.I_ExtendRecord();
            
            // Add the additional fields to the base record.            
            Iterator<String> it = fieldToValueMap.keySet().iterator();
            int pos = 2;
            while (it.hasNext ()) {
                String fieldName = it.next();
                i[inst++] = new Instruction.I_Push(pos++);
                i[inst++] = new Instruction.I_PutRecordField(fieldName);
            }
            
            // Update with the result.
            i[inst++] = new Instruction.I_Update(fieldToValueMap.size() + 1);
            i[inst++] = new Instruction.I_Pop(fieldToValueMap.size() + 1);
            i[inst] = Instruction.I_Unwind;
            code = new Code(i);
        }
        return code;
    }
    
    /**
     * Do Unwind state transition.
     */
    @Override
    protected void i_unwind (Executor e) throws CALExecutorException {
        // Put the 'arguments' on the stack in the right order.
        Iterator<String> it = fieldToValueMap.keySet().iterator();
        Node[] nodes = new Node [fieldToValueMap.size()];
        int pos = 0;
        while (it.hasNext ()) {
            nodes[pos++] = getValue(it.next());
        }
        for (int i = nodes.length - 1; i >= 0; --i) {
            e.stack.push (nodes[i]);
        }
        e.stack.push (baseRecord);
        
        // Set the instruction pointer.
        e.setIP (getCode());        
    }    
}
