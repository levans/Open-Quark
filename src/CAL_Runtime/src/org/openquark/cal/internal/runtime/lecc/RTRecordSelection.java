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
 * RTRecordSelection.java
 * Created: Apr 7, 2004
 * By: Bo Ilic
 */
package org.openquark.cal.internal.runtime.lecc;

import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.runtime.CalValue;


/**
 * Represents record selection in a lazy context.
 * 
 * For example, if
 * projection x y = y;
 * then
 * projection (error "this should not be evaluated").field1 10.0
 * should not result in the error function being called and just return 10.0. 
 * 
 * Conceptually one can think of RTRecordSelection as implementing the fully saturated reduction:
 * recordSelection recordExpr fieldName = recordExpr.fieldName; 
 * 
 * @author Bo Ilic
 */
public abstract class RTRecordSelection extends RTResultFunction {
   
    /** an expression which when evaluated is guaranteed by the compiler to be a record value */
    private RTValue recordExpr;    
    
    public final static class Ordinal extends RTRecordSelection {

        private int ordinal;

        public Ordinal(RTValue recordExpr, int ordinal) {
            super(recordExpr);
            this.ordinal = ordinal;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.openquark.cal.internal.runtime.lecc.RTValue#unwind()
         */
        @Override
        protected final RTValue reduce(RTExecutionContext ec) throws CALExecutorException {

            // Update and return result
            if (super.recordExpr != null) {
                setResult(((RTRecordValue) super.recordExpr.evaluate(ec)).getOrdinalFieldValue(ordinal));
                clearMembers();
                if (result == null) {
                    throw new NullPointerException ("Invalid reduction state in record selection.  This is probably caused by a circular record definition.");
                }
            }
            return result;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.openquark.cal.internal.runtime.lecc.RTResultFunction#clearMembers()
         */
        @Override
        public void clearMembers() {
            super.recordExpr = null;
        } 
        
        /**
         * {@inheritDoc}
         */
        @Override
        String getFieldName() {
            return "#" + ordinal;
        }
    }
    
    public final static class Textual extends RTRecordSelection {

        private String textualFieldName;

        public Textual(RTValue recordExpr, String textualFieldName) {
            super(recordExpr);
            this.textualFieldName = textualFieldName;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.openquark.cal.internal.runtime.lecc.RTValue#unwind()
         */
        @Override
        protected final RTValue reduce(RTExecutionContext ec) throws CALExecutorException {

            // Update and return result
            if (super.recordExpr != null) {
                setResult(((RTRecordValue) super.recordExpr.evaluate(ec)).getTextualFieldValue(textualFieldName));
                clearMembers();
                if (result == null) {
                    throw new NullPointerException ("Invalid reduction state in record selection.  This is probably caused by a circular record definition.");
                }
            }
            return result;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.openquark.cal.internal.runtime.lecc.RTResultFunction#clearMembers()
         */
        @Override
        public void clearMembers() {
            super.recordExpr = null;
            textualFieldName = null;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        String getFieldName() {
            return textualFieldName;
        }        
    }
    
    private RTRecordSelection(RTValue recordExpr) {
        this.recordExpr = recordExpr;        
    }
    
    abstract String getFieldName();
    
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
        
        switch (childN) {
        case 0:
            return recordExpr;
        default:
            throw new IndexOutOfBoundsException();
        }
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
        
        return "." + getFieldName();       
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
