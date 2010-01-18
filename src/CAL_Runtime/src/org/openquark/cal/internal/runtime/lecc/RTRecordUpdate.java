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
 * RTRecordUpdate.java
 * Created: May 3, 2006
 * By: Bo Ilic
 */

package org.openquark.cal.internal.runtime.lecc;

import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.runtime.CalValue;

/**
 * Models record update e.g.
 * {r | f1 := 10.0, f2 := "True", f3 := expr1 ++ expr2}
 * in a lazy context.
 * 
 * @author Bo Ilic
 */
public abstract class RTRecordUpdate extends RTResultFunction {
    
    private RTValue baseRecordExpr;
      
    /**
     * Represents update by a tuple-record field set i.e. 
     * -all the fields in the record are ordinal fields
     * -the ordinal fields are consecutive i.e. #1, #2, ..., #n.
     * -not the empty record
     * For example, {r | #1 := "abc", #2 := 100.0, #3 := True}
     * 
     * @author Bo Ilic    
     */
    private static final class TupleUpdate extends RTRecordUpdate {
        
        /**
         * the ith element of ordinalValues is the field value corresponding
         * to the ordinal field name #i. Will have positive length.
         */   
        private RTValue[] ordinalValues; 
        
        private TupleUpdate(RTValue baseRecord, RTValue[] ordinalValues) {   
            
            super(baseRecord);
            
            assert RTRecordValue.verifyTupleData(ordinalValues) :
                "Invalid tuple data in TupleUpdate constructor.";
            
            this.ordinalValues = ordinalValues;
        }
        
        /* (non-Javadoc)
         * @see org.openquark.cal.internal.runtime.lecc.RTValue#reduce(org.openquark.cal.internal.runtime.lecc.RTExecutionContext)
         */
        @Override
        protected final RTValue reduce(RTExecutionContext ec) throws CALExecutorException {
            
            // Update and return result
            if (super.baseRecordExpr != null) {
                               
                setResult(((RTRecordValue)super.baseRecordExpr.evaluate(ec)).makeTupleRecordUpdate(ordinalValues));                               
                clearMembers();
            }
                    
            return result;
        }        
        
        /*
         *  (non-Javadoc)
         * @see org.openquark.cal.internal.runtime.lecc.RTResultFunction#clearMembers()
         */
        @Override
        public void clearMembers () {
            super.baseRecordExpr = null; 
            ordinalValues = null;
        }

        /** {@inheritDoc} */
        @Override
        public int getNFields() {           
            return ordinalValues.length;
        }

        /** {@inheritDoc} */
        @Override
        public String getNthFieldName(int n) {            
            return "#" + (n + 1);
        }

        /** {@inheritDoc} */
        @Override
        public RTValue getNthValue(int n) {            
            return ordinalValues[n];
        }          
    }
    
    /**
     * Represents update by an ordinal record field set i.e. 
     * -all the fields in the record are ordinal fields
     * -not a TupleRecord or the EmptyRecord.
     *     
     * For example,
     * {r | #1 = "abc", #3 = True} is an OrdinalUpdate
     * {r | #2 = 100.0, #3 = True} is an OrdinalUpdate
     * {r| #1 = "abc", #2 = 100.0, #3 = True} is a TupleUpdate and not an OrdinalUpdate.
     *
     * @author Bo Ilic    
     */
    private static final class OrdinalUpdate extends RTRecordUpdate {
        
        /**
         * the ordinal field names (as int values) in ascending ordinal order. Will have positive length 
         * and not be [1, 2, 3, ..., ordinalValues.length]
         */
        private int[] ordinalNames;
        
        /** 
         * the ith element of ordinalValues is the field value corresponding to the ordinal field name
         * held at the ith element of ordinalNames.
         */   
        private RTValue[] ordinalValues; 
        
        private OrdinalUpdate(RTValue baseRecord, int[] ordinalNames, RTValue[] ordinalValues) {
            
            super(baseRecord);
            
            assert RTRecordValue.verifyOrdinalData(ordinalNames, ordinalValues) : "Invalid ordinal data in OrdinalUpdate().";
            
            this.ordinalNames = ordinalNames;
            this.ordinalValues = ordinalValues;
        }
        
        /* (non-Javadoc)
         * @see org.openquark.cal.internal.runtime.lecc.RTValue#reduce(org.openquark.cal.internal.runtime.lecc.RTExecutionContext)
         */
        @Override
        protected final RTValue reduce(RTExecutionContext ec) throws CALExecutorException {
            
            // Update and return result
            if (super.baseRecordExpr != null) {
                               
                setResult(((RTRecordValue)super.baseRecordExpr.evaluate(ec)).makeOrdinalRecordUpdate(ordinalNames, ordinalValues));                               
                clearMembers();
            }
                    
            return result;
        }        
        
        /*
         *  (non-Javadoc)
         * @see org.openquark.cal.internal.runtime.lecc.RTResultFunction#clearMembers()
         */
        @Override
        public void clearMembers () {
            super.baseRecordExpr = null;
            ordinalNames = null;
            ordinalValues = null;            
        }

        /** {@inheritDoc} */
        @Override
        public int getNFields() {
            return ordinalNames.length;
        }

        /** {@inheritDoc} */
        @Override
        public String getNthFieldName(int n) {
            return "#" + ordinalNames[n];
        }

        /** {@inheritDoc} */
        @Override
        public RTValue getNthValue(int n) {
            return ordinalValues[n];
        }            
    }
    
    /**
     * Represents update by a textual records field set i.e. 
     * -all the fields in the record are textual fields
     * -not the EmptyUpdate (i.e. there is at least 1 textual field).
     *     
     * For example,
     * {r | name = "abc", flag = True} is a TextualUpdate     
     *
     * @author Bo Ilic    
     */    
    private static final class TextualUpdate extends RTRecordUpdate {
        
        /** the textual field names of the record, in ascending alphabetical order. Will have positive length. */
        private String[] textualNames;   
        
        /** 
         * the ith element of textualValues is the field value corresponding to the textual field name
         * held at the ith element of ordinalNames.
         */   
        private RTValue[] textualValues;
        
        private TextualUpdate(RTValue baseRecord, String[] textualNames, RTValue[] textualValues) {
            
            super(baseRecord);
            
            assert RTRecordValue.verifyTextualData(textualNames, textualValues) :
                "Invalid textual data in TextualUpdate constructor.";
            
            this.textualNames = textualNames;
            this.textualValues = textualValues;
        }
        
        /* (non-Javadoc)
         * @see org.openquark.cal.internal.runtime.lecc.RTValue#reduce(org.openquark.cal.internal.runtime.lecc.RTExecutionContext)
         */
        @Override
        protected final RTValue reduce(RTExecutionContext ec) throws CALExecutorException {
            
            // Update and return result
            if (super.baseRecordExpr != null) {
                               
                setResult(((RTRecordValue)super.baseRecordExpr.evaluate(ec)).makeTextualRecordUpdate(textualNames, textualValues));                               
                clearMembers();
            }
                    
            return result;
        }        
        
        /*
         *  (non-Javadoc)
         * @see org.openquark.cal.internal.runtime.lecc.RTResultFunction#clearMembers()
         */
        @Override
        public void clearMembers () {
            super.baseRecordExpr = null;          
            textualNames = null;
            textualValues = null;
        } 
        
        /** {@inheritDoc} */
        @Override
        public int getNFields() {
            return textualNames.length;
        }

        /** {@inheritDoc} */
        @Override
        public String getNthFieldName(int n) {
            return textualNames[n];
        }

        /** {@inheritDoc} */
        @Override
        public RTValue getNthValue(int n) {
            return textualValues[n];
        }        
    }
    
    /**
     * Represents update by a record field set having both ordinal and textual fields, and such that the ordinal part is a tuple i.e. 
     * -there is at least 1 ordinal field, and at least 1 textual field
     * -the ordinal fields are consecutive i.e. #1, #2, ..., #n.
  
     * For example,
     * {r| #1 = 2.0, #2 = "FooBar", name = "abc", flag = True} is a TupleMixedUpdate     
     *
     * @author Bo Ilic    
     */        
    private static final class TupleMixedUpdate extends RTRecordUpdate {
        
        /**
         * the ith element of ordinalValues is the field value corresponding to
         * the ordinal field name #i. Will have positive length.
         */   
        private RTValue[] ordinalValues;
        
        /**
         * the textual field names of the record, in ascending alphabetical
         * order. Will have positive length.
         */
        private String[] textualNames;   
        
        /**
         * the ith element of textualValues is the field value corresponding to
         * the textual field name held at the ith element of ordinalNames.
         */   
        private RTValue[] textualValues;
        
        private TupleMixedUpdate(RTValue baseRecord, RTValue[] ordinalValues, String[] textualNames, RTValue[] textualValues) {
            
            super(baseRecord);
            
            assert RTRecordValue.verifyTupleData(ordinalValues) :
                "Invalid tuple data in TupleMixedUpdate constructor.";
            assert RTRecordValue.verifyTextualData(textualNames, textualValues) :
                "Invalid textual data in TupleMixedUpdate constructor.";
            
            this.ordinalValues = ordinalValues;
            this.textualNames = textualNames;
            this.textualValues = textualValues;
        }
        
        /* (non-Javadoc)
         * @see org.openquark.cal.internal.runtime.lecc.RTValue#reduce(org.openquark.cal.internal.runtime.lecc.RTExecutionContext)
         */
        @Override
        protected final RTValue reduce(RTExecutionContext ec) throws CALExecutorException {
            
            // Update and return result
            if (super.baseRecordExpr != null) {
                               
                setResult(((RTRecordValue)super.baseRecordExpr.evaluate(ec)).makeTupleMixedRecordUpdate(ordinalValues, textualNames, textualValues));                               
                clearMembers();
            }
                    
            return result;
        }        
        
        /*
         *  (non-Javadoc)
         * @see org.openquark.cal.internal.runtime.lecc.RTResultFunction#clearMembers()
         */
        @Override
        public void clearMembers () {
            super.baseRecordExpr = null;           
            ordinalValues = null;
            textualNames = null;
            textualValues = null;
        }

        /** {@inheritDoc} */
        @Override
        public int getNFields() {
            return ordinalValues.length + textualNames.length;
        }

        /** {@inheritDoc} */
        @Override
        public String getNthFieldName(int n) {
            int nOrdinalFields = ordinalValues.length;
            
            if (n < nOrdinalFields) {
                return "#" + (n + 1);
            }
            
            return textualNames[n - nOrdinalFields];
        }

        /** {@inheritDoc} */
        @Override
        public RTValue getNthValue(int n) {
            if (n < ordinalValues.length) {
                return ordinalValues[n];
            }

            return textualValues[n - ordinalValues.length];
        }            
    }
    
    /**
     * Represents update by a record field set having both ordinal and textual fields, and such that the ordinal part is not a tuple i.e. 
     * -there is at least 1 ordinal field, and at least 1 textual field
     * -the ordinal fields are not consecutive i.e. #1, #2, ..., #n.
  
     * For example,
     * {r | #1 = 2.0, #3 = "FooBar", name = "abc", flag = True} is a MixedUpdate     
     *
     * @author Bo Ilic    
     */          
    private static final class MixedUpdate extends RTRecordUpdate {

        /**
         * the ordinal field names (as int values) in ascending ordinal order.
         * Will have positive length and not be [1, 2, 3, ...,
         * ordinalValues.length]
         */
        private int[] ordinalNames;

        /**
         * the ith element of ordinalValues is the field value corresponding to
         * the ordinal field name held at the ith element of ordinalNames.
         */
        private RTValue[] ordinalValues;

        /**
         * the textual field names of the record, in ascending alphabetical
         * order. Will have positive length.
         */
        private String[] textualNames;

        /**
         * the ith element of textualValues is the field value corresponding to
         * the textual field name held at the ith element of ordinalNames.
         */
        private RTValue[] textualValues;

        private MixedUpdate(RTValue baseRecord, int[] ordinalNames, RTValue[] ordinalValues, String[] textualNames, RTValue[] textualValues) {
            
            super(baseRecord);
            
            assert RTRecordValue.verifyOrdinalData(ordinalNames, ordinalValues) : "Invalid ordinal data in MixedUpdate constructor.";
            assert RTRecordValue.verifyTextualData(textualNames, textualValues) : "Invalid textual data in MixedUpdate constructor.";
            
            this.ordinalNames = ordinalNames;
            this.ordinalValues = ordinalValues;
            this.textualNames = textualNames;
            this.textualValues = textualValues;
        }
        
        /* (non-Javadoc)
         * @see org.openquark.cal.internal.runtime.lecc.RTValue#reduce(org.openquark.cal.internal.runtime.lecc.RTExecutionContext)
         */
        @Override
        protected final RTValue reduce(RTExecutionContext ec) throws CALExecutorException {
            
            // Update and return result
            if (super.baseRecordExpr != null) {
                               
                setResult(((RTRecordValue)super.baseRecordExpr.evaluate(ec)).makeMixedRecordUpdate(ordinalNames, ordinalValues, textualNames, textualValues));                               
                clearMembers();
            }
                    
            return result;
        }        
        
        /*
         *  (non-Javadoc)
         * @see org.openquark.cal.internal.runtime.lecc.RTResultFunction#clearMembers()
         */
        @Override
        public void clearMembers () {
            super.baseRecordExpr = null;
            ordinalNames = null;
            ordinalValues = null;
            textualNames = null;
            textualValues = null;
        }

        /** {@inheritDoc} */
        @Override
        public int getNFields() {
            return ordinalNames.length + textualNames.length;
        }

        /** {@inheritDoc} */
        @Override
        public String getNthFieldName(int n) {
            int nOrdinalFields = ordinalNames.length;
            
            if (n < nOrdinalFields) {
                return "#" + ordinalNames[n];
            }
            
            return textualNames[n - nOrdinalFields];             
        }

        /** {@inheritDoc} */
        @Override
        public RTValue getNthValue(int n) {
            if (n < ordinalValues.length) {
                return ordinalValues[n];
            }
            
            return textualValues[n - ordinalValues.length];
        }        
    }     
    
    private RTRecordUpdate(RTValue baseRecordExpr) {
        assert (baseRecordExpr != null);
        
        this.baseRecordExpr = baseRecordExpr;       
    }
           
    public static RTRecordUpdate makeTupleRecordUpdate(RTValue baseRecordExpr, RTValue[] ordinalValues) {
        return new TupleUpdate(baseRecordExpr, ordinalValues);   
    }
        
    public static RTRecordUpdate makeOrdinalRecordUpdate(RTValue baseRecordExpr, int[] ordinalNames, RTValue[] ordinalValues) {
        return new OrdinalUpdate(baseRecordExpr, ordinalNames, ordinalValues); 
    }
    
    public static RTRecordUpdate makeTextualRecordUpdate(RTValue baseRecordExpr, String[] textualNames, RTValue[] textualValues) {
        return new TextualUpdate(baseRecordExpr, textualNames, textualValues);
    }
    
    public static RTRecordUpdate makeTupleMixedRecordUpdate(RTValue baseRecordExpr, RTValue[] ordinalValues, String[] textualNames, RTValue[] textualValues) {
        return new TupleMixedUpdate(baseRecordExpr, ordinalValues, textualNames, textualValues);
    }    
    
    public static RTRecordUpdate makeMixedRecordUpdate(RTValue baseRecordExpr, int[] ordinalNames, RTValue[] ordinalValues, String[] textualNames, RTValue[] textualValues) {
        return new MixedUpdate(baseRecordExpr, ordinalNames, ordinalValues, textualNames, textualValues);
    }

    /**
     * @return the number of fields that we are updating by. This includes textual as well as ordinal fields.
     */
    abstract public int getNFields();  
    
    /**
     * Note that the fields of the record update are ordered in field-name order i.e.
     * ordinal fields first, in numeric order, followed by textual fields in alphabetical order.     
     * @param n
     * @return the field name of the nth field in the record update as a String e.g. "#1" or "orderDate".
     */
    abstract public String getNthFieldName(int n); 
    
    /**
     * The value of the nth record update field, with the fields of the update ordered in field-name order.
     * @param n
     * @return RTValue
     */
    abstract public RTValue getNthValue(int n);
    
    /**     
     * {@inheritDoc}
     */
    @Override
    public final int debug_getNChildren() {            
        if (result != null) {
            return super.debug_getNChildren();
        }
        return getNFields() + 1;
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
            return baseRecordExpr;
        }
        
        return getNthValue(childN - 1);       
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public final String debug_getNodeStartText() {
        if (result != null) {
            return super.debug_getNodeStartText();
        }        
        
        return "{";
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public final String debug_getNodeEndText() {
        if (result != null) {
            return super.debug_getNodeEndText();
        }
        
        return "}";       
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
            //prefix to the record being extended
            return "";
        }
        
        String fieldName = getNthFieldName(childN - 1);
        
        StringBuilder sb = new StringBuilder();
        if (childN == 1) {
            sb.append(" | ");
        } else {
            sb.append(", ");
        }
        sb.append(fieldName);
        sb.append(" := ");
        
        return sb.toString();
    } 
}
