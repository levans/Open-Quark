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
 * NDCFieldSelection.java
 * Creation date: Sep 9, 2005
 * By: Raymond Cypher
 */
package org.openquark.cal.internal.machine.g;

import java.util.Collection;

import org.openquark.cal.compiler.DataConstructor;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.runtime.CalValue;
import org.openquark.cal.runtime.ErrorInfo;


/**
 * 
 * @author RCypher
 */
public class NDataConsFieldSelection extends NPrimitiveFunc {
    /** The record to apply the selection to. */
    private Node dc;
    
    /** The field to select. */
    private final int fieldIndex;
    

    private final DataConstructor dataConstructor;
    
    private final ErrorInfo errorInfo;
    
    /**
     * Create an application of record selection.
     * @param dc
     * @param dataConstructor
     * @param fieldIndex
     * @param errorInfo
     */
    NDataConsFieldSelection (Node dc, DataConstructor dataConstructor, int fieldIndex, ErrorInfo errorInfo) {
        this.dc = dc;
        this.dataConstructor = dataConstructor;
        this.fieldIndex = fieldIndex;
        this.errorInfo = errorInfo;
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
        
        return is + "<DCFieldSelection " + dataConstructor.toString() + " " + fieldIndex + " (" + errorInfo.toString() + ")>";
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.g.Node#addChildren(java.util.Collection)
     */
    @Override
    protected void addChildren(Collection<Node> c) {
        if (!c.contains (dc)) {
            c.add (dc);
            dc.addChildren (c);
        }
    }

    /**
     * @return the arity of the function implemented.
     */
    @Override
    protected int getArity() {
        return 0;
    }
    
    /**
     * @return the qualified name of the function being implemented.
     */
    @Override
    protected QualifiedName getName() {
        return QualifiedName.make(CAL_Prelude.MODULE_NAME, "dcfieldselection");
        
    }

    /**
     * Perform the function logic.
     * @param arguments - the arguments to the function as an array of Node.
     * @param executor
     * @return the result of the evaluation
     * @throws CALExecutorException
     */
    @Override
    public Node doEvaluation (Node[] arguments, Executor executor)  throws CALExecutorException {
        dc = executor.internalEvaluate(dc);
        Node field = dc.i_selectDCField(dataConstructor, fieldIndex, errorInfo);
        return field;
    }
    
    /**     
     * {@inheritDoc}
     */
    @Override
    public final int debug_getNChildren() {  
        if (hasIndirection()) {
            return super.debug_getNChildren();           
        }        
     
        return 1;
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
            return dc;
        }
        
        throw new IndexOutOfBoundsException();        
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public final String debug_getNodeStartText() {
        if (hasIndirection()) {
            return super.debug_getNodeStartText();           
        }        
        
        return "";
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public final String debug_getNodeEndText() {
        if (hasIndirection()) {
            return super.debug_getNodeEndText();           
        } 
        
        StringBuilder sb = new StringBuilder();
        String fieldName = dataConstructor.getNthFieldName(fieldIndex).getCalSourceForm();          
        sb.append('.').append(dataConstructor.getName().getQualifiedName()).append('.').append(fieldName);                
        
        return sb.toString();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public final String debug_getChildPrefixText(int childN) {
        if (hasIndirection()) {
            return super.debug_getChildPrefixText(childN);           
        }
        
        if (childN == 0) {
            return "";
        }
        
        throw new IndexOutOfBoundsException();            
    }    
    
}
