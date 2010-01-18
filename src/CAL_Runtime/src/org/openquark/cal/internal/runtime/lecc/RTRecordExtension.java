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
 * RTRecordExtension.java
 * Created: Apr 8, 2004
 * By: Bo Ilic
 */
package org.openquark.cal.internal.runtime.lecc;

import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.runtime.CalValue;

/**
 * Models record extension in a lazy context.
 * For example:
 * {r | field1 = "abc", field2 = 20.0}
 * where we don't want to force the evaluation of r (to a record).
 * 
 * For example, if
 * projection x y = y;
 * then we don't want to call error on:
 * testProjection4 = projection {Prelude.error "this should not be evaluated" | field1 = 10.0} "very good result";
 * 
 * Conceptually one can think of RTRecordExtension as implementing the fully saturated reduction:
 * recordExtension recordExpr extensionFields = {recordExpr | extensionFields} 
 *
 * @author Bo Ilic
 */
public abstract class RTRecordExtension extends RTResultFunction {
    
    /** a value that is guaranteed by the compiler to evaluate to a RTRecordValue */
    private RTValue baseRecordExpr;    
    
    /**
     * Represents extension by a tuple-record field set i.e. 
     * -all the fields in the record are ordinal fields
     * -the ordinal fields are consecutive i.e. #1, #2, ..., #n.
     * -not the empty record
     * For example, {r | #1 = "abc", #2 = 100.0, #3 = True}
     * 
     * @author Bo Ilic    
     */
    private static final class TupleExtension extends RTRecordExtension {
        
        /**
         * the ith element of ordinalValues is the field value corresponding
         * to the ordinal field name #i. Will have positive length.
         */   
        private RTValue[] ordinalValues; 
        
        private TupleExtension(RTValue baseRecord, RTValue[] ordinalValues) {   
            
            super(baseRecord);
            
            assert RTRecordValue.verifyTupleData(ordinalValues) :
                "Invalid tuple data in TupleExtension constructor.";
            
            this.ordinalValues = ordinalValues;
        }
        
        /* (non-Javadoc)
         * @see org.openquark.cal.internal.runtime.lecc.RTValue#reduce(org.openquark.cal.internal.runtime.lecc.RTExecutionContext)
         */
        @Override
        protected final RTValue reduce(RTExecutionContext ec) throws CALExecutorException {
            
            // Update and return result
            if (super.baseRecordExpr != null) {
                               
                setResult(((RTRecordValue)super.baseRecordExpr.evaluate(ec)).makeTupleRecordExtension(ordinalValues));                               
                clearMembers();
                if (result == null) {
                    throw new NullPointerException ("Invalid reduction state in record extension.  This is probably caused by a circular record definition.");
                }
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
     * Represents extension by an ordinal record field set i.e. 
     * -all the fields in the record are ordinal fields
     * -not a TupleRecord or the EmptyRecord.
     *     
     * For example,
     * {r | #1 = "abc", #3 = True} is an OrdinalExtension
     * {r | #2 = 100.0, #3 = True} is an OrdinalExtension
     * {r| #1 = "abc", #2 = 100.0, #3 = True} is a TupleExtension and not an OrdinalExtension.
     *
     * @author Bo Ilic    
     */
    private static final class OrdinalExtension extends RTRecordExtension {
        
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
        
        private OrdinalExtension(RTValue baseRecord, int[] ordinalNames, RTValue[] ordinalValues) {
            
            super(baseRecord);
            
            assert RTRecordValue.verifyOrdinalData(ordinalNames, ordinalValues) : "Invalid ordinal data in OrdinalExtension().";
            
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
                               
                setResult(((RTRecordValue)super.baseRecordExpr.evaluate(ec)).makeOrdinalRecordExtension(ordinalNames, ordinalValues));                               
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
     * Represents extension by a textual records field set i.e. 
     * -all the fields in the record are textual fields
     * -not the EmptyExtension (i.e. there is at least 1 textual field).
     *     
     * For example,
     * {r | name = "abc", flag = True} is a TextualExtension     
     *
     * @author Bo Ilic    
     */    
    private static final class TextualExtension extends RTRecordExtension {
        
        /** the textual field names of the record, in ascending alphabetical order. Will have positive length. */
        private String[] textualNames;   
        
        /** 
         * the ith element of textualValues is the field value corresponding to the textual field name
         * held at the ith element of ordinalNames.
         */   
        private RTValue[] textualValues;
        
        private TextualExtension(RTValue baseRecord, String[] textualNames, RTValue[] textualValues) {
            
            super(baseRecord);
            
            assert RTRecordValue.verifyTextualData(textualNames, textualValues) :
                "Invalid textual data in TextualExtension constructor.";
            
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
                               
                setResult(((RTRecordValue)super.baseRecordExpr.evaluate(ec)).makeTextualRecordExtension(textualNames, textualValues));                               
                clearMembers();
                if (result == null) {
                    throw new NullPointerException ("Invalid reduction state in record extension.  This is probably caused by a circular record definition.");
                }
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
     * Represents extension by a record field set having both ordinal and textual fields, and such that the ordinal part is a tuple i.e. 
     * -there is at least 1 ordinal field, and at least 1 textual field
     * -the ordinal fields are consecutive i.e. #1, #2, ..., #n.
  
     * For example,
     * {r| #1 = 2.0, #2 = "FooBar", name = "abc", flag = True} is a TupleMixedExtension     
     *
     * @author Bo Ilic    
     */        
    private static final class TupleMixedExtension extends RTRecordExtension {
        
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
        
        private TupleMixedExtension(RTValue baseRecord, RTValue[] ordinalValues, String[] textualNames, RTValue[] textualValues) {
            
            super(baseRecord);
            
            assert RTRecordValue.verifyTupleData(ordinalValues) :
                "Invalid tuple data in TupleMixedExtension constructor.";
            assert RTRecordValue.verifyTextualData(textualNames, textualValues) :
                "Invalid textual data in TupleMixedExtension constructor.";
            
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
                               
                setResult(((RTRecordValue)super.baseRecordExpr.evaluate(ec)).makeTupleMixedRecordExtension(ordinalValues, textualNames, textualValues));                               
                clearMembers();
                if (result == null) {
                    throw new NullPointerException ("Invalid reduction state in record extension.  This is probably caused by a circular record definition.");
                }
                
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
     * Represents extension by a record field set having both ordinal and textual fields, and such that the ordinal part is not a tuple i.e. 
     * -there is at least 1 ordinal field, and at least 1 textual field
     * -the ordinal fields are not consecutive i.e. #1, #2, ..., #n.
  
     * For example,
     * {r | #1 = 2.0, #3 = "FooBar", name = "abc", flag = True} is a MixedExtension     
     *
     * @author Bo Ilic    
     */          
    private static final class MixedExtension extends RTRecordExtension {

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

        private MixedExtension(RTValue baseRecord, int[] ordinalNames, RTValue[] ordinalValues, String[] textualNames, RTValue[] textualValues) {
            
            super(baseRecord);
            
            assert RTRecordValue.verifyOrdinalData(ordinalNames, ordinalValues) : "Invalid ordinal data in MixedExtension constructor.";
            assert RTRecordValue.verifyTextualData(textualNames, textualValues) : "Invalid textual data in MixedExtension constructor.";
            
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
                               
                setResult(((RTRecordValue)super.baseRecordExpr.evaluate(ec)).makeMixedRecordExtension(ordinalNames, ordinalValues, textualNames, textualValues));                               
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
       
    
    private RTRecordExtension(RTValue baseRecordExpr) {
        assert (baseRecordExpr != null);
        
        this.baseRecordExpr = baseRecordExpr;       
    }
           
    public static RTRecordExtension makeTupleRecordExtension(RTValue baseRecordExpr, RTValue[] ordinalValues) {
        return new TupleExtension(baseRecordExpr, ordinalValues);   
    }
        
    public static RTRecordExtension makeOrdinalRecordExtension(RTValue baseRecordExpr, int[] ordinalNames, RTValue[] ordinalValues) {
        return new OrdinalExtension(baseRecordExpr, ordinalNames, ordinalValues); 
    }
    
    public static RTRecordExtension makeTextualRecordExtension(RTValue baseRecordExpr, String[] textualNames, RTValue[] textualValues) {
        return new TextualExtension(baseRecordExpr, textualNames, textualValues);
    }
    
    public static RTRecordExtension makeTupleMixedRecordExtension(RTValue baseRecordExpr, RTValue[] ordinalValues, String[] textualNames, RTValue[] textualValues) {
        return new TupleMixedExtension(baseRecordExpr, ordinalValues, textualNames, textualValues);
    }    
    
    public static RTRecordExtension makeMixedRecordExtension(RTValue baseRecordExpr, int[] ordinalNames, RTValue[] ordinalValues, String[] textualNames, RTValue[] textualValues) {
        return new MixedExtension(baseRecordExpr, ordinalNames, ordinalValues, textualNames, textualValues);
    }

    /**
     * @return the number of fields that we are extending by. This includes textual as well as ordinal fields.
     */
    abstract public int getNFields();  
    
    /**
     * Note that the fields of the record extension are ordered in field-name order i.e.
     * ordinal fields first, in numeric order, followed by textual fields in alphabetical order.     
     * @param n
     * @return the field name of the nth field in the record extension as a String e.g. "#1" or "orderDate".
     */
    abstract public String getNthFieldName(int n); 
    
    /**
     * The value of the nth record extension field, with the fields of the extension ordered in field-name order.
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
        sb.append(" = ");
        
        return sb.toString();
    }
}
