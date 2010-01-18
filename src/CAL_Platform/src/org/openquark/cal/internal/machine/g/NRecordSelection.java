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
 * NRecordSelection.java
 * Created: Apr 12, 2004
 * By: RCypher
 */

package org.openquark.cal.internal.machine.g;

import java.util.Collection;

import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.runtime.CalValue;


/**
 * NRecordSelection
 * Graph node representing the application of selection to a record.
 * @author RCypher
 * Created: Apr 12, 2004
 */
class NRecordSelection extends NInd {
    /** The record to apply the selection to. */
    private final Node record;
    
    /** The field to select. */
    private final String fieldName;
    
    /** The code of the virtual selection function. */
    private Code code;
         
    /**
     * Create an application of record selection.
     * @param fieldName
     * @param record
     */
    NRecordSelection (String fieldName, Node record) {
        this.record = record;
        this.fieldName = fieldName;
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
    
    /**     
     * {@inheritDoc}
     */
    @Override
    public int debug_getNChildren() {  
        if (hasIndirection()) {
            return super.debug_getNChildren();           
        }        
        
        return 1;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public CalValue debug_getChild(int childN) {
        if (hasIndirection()) {
            return super.debug_getChild(childN);           
        }
        
        if (childN == 0) {
            return record;
        }
        
        throw new IndexOutOfBoundsException();        
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String debug_getNodeStartText() {
        if (hasIndirection()) {
            return super.debug_getNodeStartText();           
        }        
        
        return "";
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String debug_getNodeEndText() {
        if (hasIndirection()) {
            return super.debug_getNodeEndText();           
        } 
        
        return "." + fieldName;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String debug_getChildPrefixText(int childN) {
        if (hasIndirection()) {
            return super.debug_getChildPrefixText(childN);           
        }
        
        if (childN == 0) {
            return "";
        }
        
        throw new IndexOutOfBoundsException();            
    }    
    
    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.g.Node#addChildren(java.util.Collection)
     */
    @Override
    protected void addChildren(Collection<Node> c) {
        if (!c.contains (record)) {
            c.add (record);
            record.addChildren (c);
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
     * Generate the g-machine code that corresponds to the 
     * actions represented by this node.
     * @return the code.
     */
    private Code getCode () {
        // In essence this node can be considered the application 
        // of a 1 arity function that selects a specific field
        // from a record supplied as an argument.
        if (code == null) {
            Instruction i[] = new Instruction [4];
            i[0] = Instruction.I_Eval;
            i[1] = new Instruction.I_RecordSelection(fieldName);
            i[2] = new Instruction.I_Update(0);
            i[3] = Instruction.I_Unwind;
            code = new Code(i);
        }
        
        return code;
    }
    
    /**
     * Do Unwind state transition.
     */
    @Override
    protected void i_unwind (Executor e) throws CALExecutorException {
        // Simply push the single argument of this application
        // and update the current instruction pointer.
        e.stack.push (record);
        e.setIP(getCode());
    }    

}
