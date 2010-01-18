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
 * RTDataConsSelection.java
 * Creation date: Sep 8, 2005
 * By: Raymond Cypher
 */
package org.openquark.cal.internal.runtime.lecc;

import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.runtime.CalValue;
import org.openquark.cal.runtime.ErrorInfo;

/**
 * This class represents the selection of field from a data constructor in a lazy context.
 * For example, like (head [Just 'a']).Just.value;
 * 
 * @author RCypher
 */
public abstract class RTDataConsFieldSelection extends RTResultFunction {

    /**
     * when evaluated, this is an expression that is a data constructor applied to its arguments. However, it
     * will not be evaluated in general e.g. head [Just 'a'] in the above example.
     */
    private RTValue dataConsExpr;
    private int fieldOrdinal;
    private ErrorInfo errorInfo;
    private int dcOrdinal;
    
    protected RTDataConsFieldSelection (
            RTValue dataConsExpr,
            int dcOrdinal,
            int fieldOrdinal, 
            ErrorInfo errorInfo) {
        assert (dataConsExpr != null && fieldOrdinal >= 0 && errorInfo != null) : "Invalid argument in RTDataConsFieldSelection constructor";
        
        this.dataConsExpr = dataConsExpr;
        this.fieldOrdinal = fieldOrdinal;
        this.errorInfo = errorInfo;
        this.dcOrdinal = dcOrdinal;
    }
    
    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.RTResultFunction#clearMembers()
     */
    @Override
    public void clearMembers() {
        dataConsExpr = null;
        errorInfo = null;
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.RTValue#reduce(org.openquark.cal.internal.runtime.lecc.RTExecutionContext)
     */
    @Override
    protected RTValue reduce(RTExecutionContext $ec) throws CALExecutorException {
        if (dataConsExpr != null) {
            setResult (((RTCons)dataConsExpr.evaluate($ec)).getFieldByIndex (dcOrdinal, fieldOrdinal, errorInfo));
            clearMembers();
        }
        return result;
    }

    /**
     * Retrieve the name of a field for the given ordinal.
     * This method will be overridden by generated classes 
     * associated with a specific DC. 
     * @param ordinal
     * @return the field name.
     */
    protected abstract String getFieldNameByOrdinal(int ordinal);
    
    /**
     * Retrieve the name of the data constructor associated with this instance
     * of a field selection.
     * @return the name of the DC.
     */
    protected abstract String getDCName ();
    
    /**     
     * {@inheritDoc}
     */
    @Override
    public final int debug_getNChildren() {            
        if (result != null) {
            return super.debug_getNChildren();
        }
        return 1;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public final CalValue debug_getChild(int childN) {
        if (result != null) {
            return super.debug_getChild(childN);
        }
        
        if (childN == 0) {
            return dataConsExpr;
        }        
            
        throw new IndexOutOfBoundsException();        
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public final String debug_getNodeStartText() {
        if (result != null) {
            return super.debug_getNodeStartText();
        }        
        
        return "";
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final String debug_getNodeEndText() {
        if (result != null) {
            return super.debug_getNodeEndText();
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append(".");
        sb.append(getDCName());
        sb.append(".");
        sb.append(getFieldNameByOrdinal(fieldOrdinal));
        
        return sb.toString();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final String debug_getChildPrefixText(int childN) {
        if (result != null) {
            return super.debug_getChildPrefixText(childN);
        }
        
        if (childN == 0) {
            return "";
        }
                
        throw new IndexOutOfBoundsException();                      
    }    
     
}
