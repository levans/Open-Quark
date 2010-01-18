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
 * RTRecordValue.java
 * Created: Apr 5, 2004
 * By: Bo Ilic
 */

package org.openquark.cal.internal.runtime.lecc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.openquark.cal.compiler.FieldName;
import org.openquark.cal.internal.runtime.RecordType;
import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.runtime.CalValue;


/**
 * Represents a record literal value in the runtime. 
 * In CAL source, this corresponds to a record such as: {field1 = "abc", field2 = 2.0}.
 * <p>
 * 
 * Records involving record-polymorphic record extension, such as:
 * {r | field1 = "abc", field2 = 2.0} are not represented directly using RTRecordValue objects.
 * Rather, r is first evaluated to a RTRecordValue, and then the extension is made. (In a lazy 
 * context, this is done through reducing RTRecordExtension, in a strict context, this is done
 * directly).
 * <p>
 * 
 * Records internally are implemented by 6 different subclasses:
 * -records whose ordinal fields are of tuple form i.e. #1, #2, ..., #n consecutive
 *  are stored as arrays with O(1) access to the ordinal fields, and no storage for ordinal field names.
 * -even if a record's ordinal part is not of tuple form, the field names are stored as unboxed ints.
 * -storage for records is space efficient.
 * 
 * @author Bo Ilic
 */
public abstract class RTRecordValue extends RTValue implements RecordType {
        
    static public final RTRecordValue EMPTY_RECORD = new EmptyRecord();
    
    /**
     * A helper class to represent the data necessary to form the ordinal part of
     * a record's field set. These are returned by certain intermediate functions.   
     * @author Bo Ilic   
     */
    private static abstract class OrdinalData {
        
        static final OrdinalData EMPTY = new Empty();
                                        
        private static final class Empty extends OrdinalData { 
                  
            private Empty() {
                // Make constructor private to prevent multiple instances.
            }
            
            @Override
            boolean isTuple (){
                return false;
            }
            @Override
            boolean hasOrdinalFields() {
                return false;
            }
            
            @Override
            int[] getOrdinalNames() {
                throw new UnsupportedOperationException();
            }
            @Override
            int getNOrdinalNames() {
                throw new UnsupportedOperationException();
            }
            @Override
            int getNthOrdinalName(int n) {
                throw new UnsupportedOperationException();
            }
            @Override
            RTValue[] getOrdinalValues() {
                throw new UnsupportedOperationException();
            }
        }
        
        private static final class Tuple extends OrdinalData {            
            private final RTValue[] ordinalValues;
            
            Tuple(RTValue[] ordinalValues) {   
                assert RTRecordValue.verifyTupleData(ordinalValues) :
                    "Invalid tuple data in OrdinalData.Tuple constructor.";
                this.ordinalValues = ordinalValues;
            } 
            
            @Override
            boolean isTuple (){
                return true;
            }
            @Override
            boolean hasOrdinalFields() {
                return true;
            }
            @Override
            int[] getOrdinalNames() {
                throw new UnsupportedOperationException();
            }
            @Override
            int getNOrdinalNames() {
                return ordinalValues.length;
            }
            @Override
            int getNthOrdinalName(int n) {
                if(n < ordinalValues.length) {
                    return n + 1;
                
                } else {
                    throw new ArrayIndexOutOfBoundsException();
                }
            }
            @Override
            RTValue[] getOrdinalValues() {
                return ordinalValues;
            }            
        }
        
        private static final class Ordinal extends OrdinalData {
            
            private final int[] ordinalNames;
            private final RTValue[] ordinalValues;
            
            Ordinal(int[] ordinalNames, RTValue[] ordinalValues) {  
            
                assert RTRecordValue.verifyOrdinalData(ordinalNames, ordinalValues) :
                    "Invalid ordinal data in OrdinalData.Ordinal constructor.";
                
                this.ordinalNames = ordinalNames;            
                this.ordinalValues = ordinalValues;                      
            }
            
            @Override
            boolean isTuple (){
                return false;
            }
            @Override
            boolean hasOrdinalFields() {
                return true;
            }
            @Override
            int getNOrdinalNames() {
                return ordinalNames.length;
            }
            @Override
            int getNthOrdinalName(int n) {
                return ordinalNames[n];
            }
            @Override
            int[] getOrdinalNames() {
                return ordinalNames;
            }
            @Override
            RTValue[] getOrdinalValues() {
                return ordinalValues;
            }            
        }               
        
        abstract boolean isTuple();        
        abstract boolean hasOrdinalFields();
        abstract int[] getOrdinalNames();
        abstract int getNOrdinalNames();
        abstract int getNthOrdinalName(int n);
        abstract RTValue[] getOrdinalValues();
    }
    
    /**
     * A helper class to represent the pair (textualNames, textualValues) returned from certain
     * intermediate functions.
     * @author Bo Ilic   
     */    
    private static final class TextualData {
       
        private final String[] textualNames;
        private final RTValue[] textualValues;
        
        private static final TextualData EMPTY = new TextualData();
        
        private TextualData() {
            textualNames = null;
            textualValues = null;
        }
        
        TextualData(String[] textualNames, RTValue[] textualValues) {
            
            assert RTRecordValue.verifyTextualData(textualNames, textualValues) :
                "Invalid textual data in TextualData constructor.";
            
            this.textualNames = textualNames;
            this.textualValues = textualValues;
        }
        
        boolean hasTextualFields() {
            return this != EMPTY;
        }
               
        String[] getTextualNames() {
            return textualNames;
        }
        RTValue[] getTextualValues() {
            return textualValues;
        }
    }    
    
    /**
     * Represents the empty record value {}.
     * 
     * @author Bo Ilic    
     */
    private static final class EmptyRecord extends RTRecordValue {
        
        private EmptyRecord() { 
            // Make constructor private to prevent multiple instances.
        }
        
        /** {@inheritDoc} */
        @Override
        public RTRecordValue makeFromValues(RTValue[] ordinalValues, RTValue[] textualValues) {
            assert (ordinalValues == null && textualValues == null) :
                "Invalid argument in EmptyRecord.makeFromValues";
            
            return EMPTY_RECORD;
        }
        
        /** {@inheritDoc} */
        @Override
        public RTValue getTextualFieldValue(String textualFieldName) {
            throw new UnsupportedOperationException();            
        }

        /** {@inheritDoc} */
        @Override
        public RTValue getOrdinalFieldValue(int ordinal) {           
            throw new UnsupportedOperationException();
        }
       
        /** {@inheritDoc} */
        @Override
        public int getNFields() {
            return 0;
        }

        /** {@inheritDoc} */
        @Override
        public RTValue getNthValue(int n) {                       
            throw new IndexOutOfBoundsException();           
        }
        
        /** {@inheritDoc} */
        @Override
        public final CalValue debug_getChild(int childN) {
            throw new IndexOutOfBoundsException(); 
        }
        
        /** {@inheritDoc} */
        @Override
        public boolean hasOrdinalField(int ordinal) {            
            return false;                                
        }    
                              
        /** {@inheritDoc} */
        @Override
        public boolean hasTextualField(String textualFieldName) {
            return false;    
        }
        
        /** {@inheritDoc} */
        @Override
        public int indexOfField(String fieldName) {
            return -1;
        }
        
        /** {@inheritDoc} */
        @Override
        public List<String> fieldNames() {
            return Collections.<String>emptyList();              
        }
        
        /** {@inheritDoc} */
        @Override
        public List<RTValue> fieldValues() {
            return Collections.<RTValue>emptyList();              
        }        
        
        /** {@inheritDoc} */
        @Override
        public FieldName getNthFieldName(int n) {
            throw new IndexOutOfBoundsException();
        }
        
        /** {@inheritDoc} */
        @Override
        OrdinalData getOrdinalData() {
            return OrdinalData.EMPTY;
        }
        /** {@inheritDoc} */
        @Override
        TextualData getTextualData() {
            return TextualData.EMPTY;
        }
    
        /** {@inheritDoc} */
        @Override
        OrdinalData retractTupleFields(int tupleSize) {
            throw new UnsupportedOperationException();           
        }
        /** {@inheritDoc} */
        @Override
        OrdinalData retractOrdinalFields(int[] retractedOrdinalNames) {
            throw new UnsupportedOperationException();
        }
        /** {@inheritDoc} */
        @Override
        TextualData retractTextualFields(String[] retractedTextualNames) {
            throw new UnsupportedOperationException();
        }
      
        /** {@inheritDoc} */
        @Override
        OrdinalData mergeTupleFields(RTValue[] extensionOrdinalValues) {
            return new OrdinalData.Tuple(extensionOrdinalValues);            
        }  
        /** {@inheritDoc} */
        @Override
        OrdinalData mergeOrdinalFields(int[] extensionOrdinalNames, RTValue[] extensionOrdinalValues) {   
            return new OrdinalData.Ordinal(extensionOrdinalNames, extensionOrdinalValues);           
        }
        /** {@inheritDoc} */
        @Override
        TextualData mergeTextualFields(String[] extensionTextualNames, RTValue[] extensionTextualValues) {                                      
            return new TextualData(extensionTextualNames, extensionTextualValues);                                  
        }
             
        /** {@inheritDoc} */
        @Override
        public int getNOrdinalFields() {           
            return 0;
        }
        /** {@inheritDoc} */
        @Override
        public int getNthOrdinalFieldName(int n) {
            throw new IndexOutOfBoundsException();
        }        
        /** {@inheritDoc} */
        @Override
        public RTValue getNthOrdinalValue(int n) {
            throw new IndexOutOfBoundsException();
        }
        /** {@inheritDoc} */
        @Override
        public int getNTextualFields() {          
            return 0;
        } 
        /** {@inheritDoc} */
        @Override
        public String getNthTextualFieldName(int n) {
            throw new IndexOutOfBoundsException();
        }         
        /** {@inheritDoc} */
        @Override
        public RTValue getNthTextualValue(int n) {
            throw new IndexOutOfBoundsException();
        }
                 
        /** {@inheritDoc} */
        @Override
        public boolean hasTupleOrdinalPart() {
            return false;
        }
        
        /** {@inheritDoc} */
        @Override
        public RTRecordValue insertOrdinalField(int fieldOrdinal, RTValue fieldValue) {
            if(fieldOrdinal == 1) {
                return RTRecordValue.makeTupleRecord(new RTValue[] {fieldValue});
            } else {
                return RTRecordValue.makeOrdinalRecord(new int[] {fieldOrdinal}, new RTValue[] {fieldValue});
            }
        }
        
        /** {@inheritDoc} */
        @Override
        public RTRecordValue insertTextualField(String fieldName, RTValue fieldValue) {
            return RTRecordValue.makeTextualRecord(new String[] {fieldName}, new RTValue[] {fieldValue});
        }
        
        /** {@inheritDoc} */
        @Override
        public RTRecordValue appendRecord(RTRecordValue otherRecord) {
            return otherRecord;
        }
        
        /** {@inheritDoc} */
        @Override
        public boolean sameFields(RecordType otherRecord) {
            return (otherRecord instanceof EmptyRecord);
        }
        
        /** {@inheritDoc} */
        @Override
        public RTRecordValue updateOrdinalField(int ordinal, RTValue fieldValue) {
            throw new UnsupportedOperationException("Attempt to update the empty record at field #" + ordinal + ".");
        }

        /** {@inheritDoc} */
        @Override
        public RTRecordValue updateMixedOrdinalField(int ordinal, RTValue fieldValue) {
            throw new UnsupportedOperationException("Attempt to update the empty record at field #" + ordinal + ".");
        }
        
        /** {@inheritDoc} */
        @Override
        public RTRecordValue updateTextualField(String textualFieldName, RTValue fieldValue) {
            throw new UnsupportedOperationException("Attempt to update the empty record at field " + textualFieldName + ".");
        } 
        
        /** {@inheritDoc} */
        @Override
        public RTRecordValue mutateOrdinalField(int ordinal, RTValue fieldValue) {
            throw new UnsupportedOperationException("Attempt to mutate the empty record at field #" + ordinal + ".");
        }
        
        /** {@inheritDoc} */
        @Override
        public RTRecordValue mutateTextualField(String textualFieldName, RTValue fieldValue) {
            throw new UnsupportedOperationException("Attempt to mutate the empty record at field " + textualFieldName + ".");
        }              
        
        /**
         * @see org.openquark.cal.internal.runtime.lecc.RTValue#apply(org.openquark.cal.internal.runtime.lecc.RTValue)
         * {@inheritDoc}
         */
        @Override
        public RTValue apply(RTValue argument) {
                          
            //The meaning of application to a record value is pointwise application i.e.
            //{field1 = f1, field2 = f2, ..., fieldk = fk} x == {field1 = f1 x, field2 = f2 x, ... fieldk = fk x}         
            
            //In normal circumstances, record values cannot be applied to values because the resulting types are
            //not typeable in CAL source. However, this situation does arise in the case of hidden dictionary switching.
            //
            //For example:
            //
            //{field1 = dictOrdInt, field2 = dictOrdString} 0 
            //is changed to
            //{field1 = dictOrdInt 0, field2 = dictOrdString 0}
            //(which latter reduces to {field1 = dictEqInt, field2 = dictEqString})
            //
            //in other words, the Int value being applied is applied pointwise to each field value.
                              
            return this;                 
        }

    }
    
    /**
     * Represents tuple-records i.e. 
     * -all the fields in the record are ordinal fields
     * -the ordinal fields are consecutive i.e. #1, #2, ..., #n.
     * -not the empty record
     * For example, {#1 = "abc", #2 = 100.0, #3 = True}
     * 
     * @author Bo Ilic    
     */
    private static final class TupleRecord extends RTRecordValue {
        
        /**
         * the ith element of ordinalValues is the field value corresponding
         * to the ordinal field name #i. Will have positive length.
         */   
        private final RTValue[] ordinalValues; 
        
        private TupleRecord(RTValue[] ordinalValues) {   
            
            assert RTRecordValue.verifyTupleData(ordinalValues) :
                "Invalid tuple data in TupleRecord constructor.";
            
            this.ordinalValues = ordinalValues;
        }
        
        @Override
        public RTRecordValue makeFromValues(RTValue[] newOrdinalValues, RTValue[] textualValues) {
            assert (textualValues == null) :
                "Illegal argument in TupleRecord.makeFromValues";
            
            return new TupleRecord(newOrdinalValues);
        }        
                        
        @Override
        public RTValue getTextualFieldValue(String textualFieldName) {
            throw new UnsupportedOperationException();            
        }

        @Override
        public RTValue getOrdinalFieldValue(int ordinal) {
            int index = ordinal - 1;
            // Update value to remove indirection chains.
            RTValue ordinalValue = ordinalValues[index];
            if (ordinalValue instanceof RTResultFunction) {
                return (ordinalValues[index] = ordinalValue.getValue());
            }
            
            return ordinalValue;
        }
       
        @Override
        public int getNFields() {
            return ordinalValues.length;
        }

        @Override
        public RTValue getNthValue(int n) {                       
            // Update value to remove indirection chains.
            RTValue ordinalValue = ordinalValues[n];
            if (ordinalValue instanceof RTResultFunction) {
                return (ordinalValues[n] = ordinalValue.getValue());
            }
            
            return ordinalValue;           
        }
        
        /** {@inheritDoc} */
        @Override
        public final CalValue debug_getChild(int childN) {
            return ordinalValues[childN]; 
        }        
        
        @Override
        public boolean hasOrdinalField(int ordinal) {            
            return ordinal <= ordinalValues.length;                                
        }    
                              
        @Override
        public boolean hasTextualField(String textualFieldName) {
            return false;    
        }
        
        @Override
        public int indexOfField(String fieldName) {
            
            // Only ordinal field names make sense here
            if(!FieldName.Ordinal.isValidCalSourceForm(fieldName)) {
                return -1;
            }
            
            int ordinal = fieldOrdinal(fieldName);
            if(ordinal > getNFields()) {
                return -1;
            }
            
            return ordinal - 1;
        }
        
        @Override
        public List<String> fieldNames() {
            
            int nFields = getNFields();
            String[] fieldNames = new String[nFields];            
           
            for (int i = 0; i < nFields; ++i) {
                fieldNames[i] = "#" + (i + 1);
            }                                          
                    
            return Arrays.asList(fieldNames);
        }
        
        @Override
        public List<RTValue> fieldValues() {
            // Update values to remove indirection chains.
            updateOrdinalValues();
            
            return Collections.unmodifiableList(Arrays.asList(ordinalValues));
        }        
        
        /** {@inheritDoc}*/
        @Override
        public FieldName getNthFieldName(int n) {
            return FieldName.makeOrdinalField(getNthOrdinalFieldName(n));              
        }        
        
        @Override
        OrdinalData getOrdinalData() {
            // Update values to remove indirection chains.
            updateOrdinalValues();
            
            return new OrdinalData.Tuple(ordinalValues);
        }
        @Override
        TextualData getTextualData() {
            return TextualData.EMPTY;
        }
    
        @Override
        OrdinalData retractTupleFields(int retractedTupleSize) {            
            // Update values to remove indirection chains.
            updateOrdinalValues();
            
            return RTRecordValue.retractFromTupleUsingTuple(ordinalValues, retractedTupleSize);            
        }
        @Override
        OrdinalData retractOrdinalFields(int[] retractedOrdinalNames) {
            // Update values to remove indirection chains.
            updateOrdinalValues();
            
            return RTRecordValue.retractFromTupleUsingOrdinal(ordinalValues, retractedOrdinalNames);
        }
        @Override
        TextualData retractTextualFields(String[] retractedTextualNames) {
            throw new UnsupportedOperationException();
        }
      
        @Override
        OrdinalData mergeTupleFields(RTValue[] extensionOrdinalValues) {
            //this is unsupported since 2 tuple (of size > 0) must have overlapping fields
            throw new UnsupportedOperationException();
        }          
        @Override
        OrdinalData mergeOrdinalFields(int[] extensionOrdinalNames, RTValue[] extensionOrdinalValues) {
            // Update values to remove indirection chains.
            updateOrdinalValues();
            
            return RTRecordValue.mergeTupleWithOrdinal(ordinalValues, extensionOrdinalNames, extensionOrdinalValues);     
        }                
        @Override
        TextualData mergeTextualFields(String[] extensionTextualNames, RTValue[] extensionTextualValues) {                           
            return new TextualData(extensionTextualNames, extensionTextualValues);       
        }
        
        /** {@inheritDoc} */
        @Override
        public int getNOrdinalFields() {           
            return ordinalValues.length;
        }
        /** {@inheritDoc} */
        @Override
        public int getNthOrdinalFieldName(int n) {
            if (n < 0 || n >= ordinalValues.length) {
                throw new IndexOutOfBoundsException();
            }
            
            return n + 1;
        } 
        /** {@inheritDoc} */
        @Override
        public RTValue getNthOrdinalValue(int n) {
            // Update value to remove indirection chains.
            RTValue ordinalValue = ordinalValues[n];
            if (ordinalValue instanceof RTResultFunction) {
                return(ordinalValues[n] = ordinalValue.getValue());
            }
            
            return ordinalValue;
        }
        
        /** {@inheritDoc} */
        @Override
        public int getNTextualFields() {          
            return 0;
        }
         /** {@inheritDoc} */
        @Override
        public String getNthTextualFieldName(int n) {
            throw new IndexOutOfBoundsException();
        }
        /** {@inheritDoc} */
        @Override
        public RTValue getNthTextualValue(int n) {
            throw new IndexOutOfBoundsException();
        }
        
        /** {@inheritDoc} */
        @Override
        public boolean hasTupleOrdinalPart() {
            return true;
        }
        
        /** {@inheritDoc} */
        @Override
        public RTRecordValue appendRecord(RTRecordValue otherRecord) {
            OrdinalData ordinalData = overlappingMergeOrdinalData(getOrdinalData(), otherRecord.getOrdinalData());
            return makeRecordFromFieldData(ordinalData, otherRecord.getTextualData());
        }
        
        /**     
         * @return boolean. True if the record has the fields #1, ..., #n, with no gaps, n >= 2
         *   and there are no other fields.
         */
        @Override
        public boolean isTuple2OrMoreRecord() {
            return getNOrdinalFields() > 1;
        }        
        
        /** {@inheritDoc} */
        @Override
        public boolean sameFields(RecordType otherRecordType) {
            if(!(otherRecordType instanceof TupleRecord)) {
                return false;
            }
            
            return otherRecordType.getNFields() == getNFields();
        }
        
        /** {@inheritDoc} */
        @Override
        public RTRecordValue updateOrdinalField(int ordinal, RTValue fieldValue) {
            RTValue[] newOrdinalValues = ordinalValues.clone();
            newOrdinalValues[ordinal - 1] = fieldValue;
            return new TupleRecord(newOrdinalValues);           
        }
        
        /** {@inheritDoc} */
        @Override
        public RTRecordValue updateMixedOrdinalField(int ordinal, RTValue fieldValue) {
            // This method should only be called when an we are updating ordinal fields
            // followed by mutating textual fields.  Since an textual record doesn't
            // contain ordinal values this is an error.
            throw new UnsupportedOperationException("Attempt to do mixed update on tuple record.");
        }
        
        /** {@inheritDoc} */
        @Override
        public RTRecordValue updateTextualField(String textualFieldName, RTValue fieldValue) {
            throw new UnsupportedOperationException("Attempt to update the tuple record at field " + textualFieldName + ".");
        }   
        
        /** {@inheritDoc} */
        @Override
        public RTRecordValue mutateOrdinalField(int ordinal, RTValue fieldValue) {           
            ordinalValues[ordinal - 1] = fieldValue;
            return this;             
        }
        
        /** {@inheritDoc} */
        @Override
        public RTRecordValue mutateTextualField(String textualFieldName, RTValue fieldValue) {
            throw new UnsupportedOperationException("Attempt to mutate the tuple record at field " + textualFieldName + ".");
        }           

        /**
         * @see org.openquark.cal.internal.runtime.lecc.RTValue#apply(org.openquark.cal.internal.runtime.lecc.RTValue)
         */
        @Override
        public RTValue apply(RTValue argument) {
                          
            //The meaning of application to a record value is pointwise application i.e.
            //{field1 = f1, field2 = f2, ..., fieldk = fk} x == {field1 = f1 x, field2 = f2 x, ... fieldk = fk x}         
            
            //In normal circumstances, record values cannot be applied to values because the resulting types are
            //not typeable in CAL source. However, this situation does arise in the case of hidden dictionary switching.
            //
            //For example:
            //
            //{field1 = dictOrdInt, field2 = dictOrdString} 0 
            //is changed to
            //{field1 = dictOrdInt 0, field2 = dictOrdString 0}
            //(which latter reduces to {field1 = dictEqInt, field2 = dictEqString})
            //
            //in other words, the Int value being applied is applied pointwise to each field value.
                              
            int nOrdinalFields = getNOrdinalFields();
            RTValue[] newOrdinalValues = new RTValue[nOrdinalFields];
            for (int i = 0; i < nOrdinalFields; ++i) {
                newOrdinalValues[i] = getNthOrdinalValue(i).apply(argument);
            }                       
            
            return new TupleRecord(newOrdinalValues);                  
        }  
        
        /**     
         * {@inheritDoc}
         */
        @Override
        public final String debug_getNodeStartText() {
            if (ordinalValues.length == 1) {
                return "{";
            }
      
            return "(";
        }
        /**    
         * {@inheritDoc}
         */
        @Override
        public final String debug_getNodeEndText() {    
            if (ordinalValues.length == 1) {
                return "}";
            }            
            
            return ")";
        }
        /**  
         * {@inheritDoc}
         */
        @Override
        public final String debug_getChildPrefixText(int childN) {  
            
            int nOrdinalFields = ordinalValues.length;
            
            if (nOrdinalFields == 1) {
                return super.debug_getChildPrefixText(childN);
            }
            
            if (childN >= 0 && childN < nOrdinalFields) {
                if (childN == 0) {
                    return "";
                }
                return ", ";
            }
            
            throw new IndexOutOfBoundsException();                           
        }             
        
        /**
         * Update the ordinal values to remove any indirection chains.
         */
        private final void updateOrdinalValues () {
            for (int i = 0, n = ordinalValues.length; i < n; ++i) {
                RTValue ordinalValue = ordinalValues[i];
                if (ordinalValue instanceof RTResultFunction) {
                    ordinalValues[i] = ordinalValue.getValue();
                }
            }
        }
   
    }
    
    /**
     * Represents ordinal records i.e. 
     * -all the fields in the record are ordinal fields
     * -not a TupleRecord or the EmptyRecord.
     *     
     * For example,
     * {#1 = "abc", #3 = True} is an OrdinalRecord
     * {#2 = 100.0, #3 = True} is an OrdinalRecord
     * {#1 = "abc", #2 = 100.0, #3 = True} is a TupleRecord and not an OrdinalRecord.
     *
     * @author Bo Ilic    
     */
    private static final class OrdinalRecord extends RTRecordValue {
        
        /**
         * the ordinal field names (as int values) in ascending ordinal order. Will have positive length 
         * and not be [1, 2, 3, ..., ordinalValues.length]
         */
        private final int[] ordinalNames;
        
        /** 
         * the ith element of ordinalValues is the field value corresponding to the ordinal field name
         * held at the ith element of ordinalNames.
         */   
        private final RTValue[] ordinalValues; 
        
        private OrdinalRecord(int[] ordinalNames, RTValue[] ordinalValues) {
            
            assert RTRecordValue.verifyOrdinalData(ordinalNames, ordinalValues) :
                "Invalid ordinal data in OrdinalRecord constructor.";
            
            this.ordinalNames = ordinalNames;
            this.ordinalValues = ordinalValues;
        }
        
        @Override
        public RTRecordValue makeFromValues(RTValue[] newOrdinalValues, RTValue[] textualValues) {
            assert (textualValues == null) :
                "Illegal argument in OrdinalRecord.makeFromValues.";
            
            return new OrdinalRecord(this.ordinalNames, newOrdinalValues);
        }          
        
        @Override
        public RTValue getTextualFieldValue(String textualFieldName) {
            throw new UnsupportedOperationException();            
        }

        @Override
        public RTValue getOrdinalFieldValue(int ordinal) {         
            int index = Arrays.binarySearch(ordinalNames, ordinal);
            
            // Update value to remove indirection chain.
            RTValue ordinalValue = ordinalValues[index];
            if (ordinalValue instanceof RTResultFunction) {
                return (ordinalValues[index] = ordinalValue.getValue());
            }
            
            return ordinalValue;
        }
       
        @Override
        public int getNFields() {
            return ordinalNames.length;
        }

        @Override
        public RTValue getNthValue(int n) {                       
            // Update value to remove indirection chains.
            RTValue ordinalValue = ordinalValues[n];
            if (ordinalValue instanceof RTResultFunction) {
                return (ordinalValues[n] = ordinalValue.getValue());
            }
            
            return ordinalValue;           
        }
        
        /**
         * {@inheritDoc}        
         */
        @Override
        public final CalValue debug_getChild(int childN) {
            return ordinalValues[childN]; 
        }         
        
        @Override
        public boolean hasOrdinalField(int ordinal) {
                       
            //if the ordinal is bigger than all the ordinals in ordinalNames, then we can dispense with binary search
            if (ordinal > ordinalNames[ordinalNames.length - 1]) {
                return false;
            }
            
            return Arrays.binarySearch(ordinalNames, ordinal) >= 0;      
        }    
                              
        @Override
        public boolean hasTextualField(String textualFieldName) {
            return false;    
        }
        
        @Override
        public int indexOfField(String fieldName) {
            
            // Only ordinal field names make sense here
            if(!FieldName.Ordinal.isValidCalSourceForm(fieldName)) {
                return -1;
            }
            
            int ordinal = fieldOrdinal(fieldName);
            int index = Arrays.binarySearch(ordinalNames, ordinal);
            if(index < 0) {
                return -1;
            }
            
            return index;
        }
        
        @Override
        public List<String> fieldNames() {
            
            int nFields = getNFields();
            String[] fieldNames = new String[nFields]; 
            
            for (int i = 0; i < nFields; ++i) {
                fieldNames[i] = "#" + ordinalNames[i];
            }            
                                           
            return Arrays.asList(fieldNames);
        }
        
        @Override
        public List<RTValue> fieldValues() {
            // Update values to remove indirection chains.
            updateOrdinalValues();
            
            return Collections.unmodifiableList(Arrays.asList(ordinalValues));
        }
        
        /** {@inheritDoc}*/
        @Override
        public FieldName getNthFieldName(int n) {                        
            return FieldName.makeOrdinalField(ordinalNames[n]);             
        }          
        
        @Override
        OrdinalData getOrdinalData() {
            // Update values to remove indirection chains.
            updateOrdinalValues();
            
            return new OrdinalData.Ordinal(ordinalNames, ordinalValues);
        }
        @Override
        TextualData getTextualData() {
            return TextualData.EMPTY;
        }
    
        @Override
        OrdinalData retractTupleFields(int retractedTupleSize) {
            // Update values to remove indirection chains.
            updateOrdinalValues();
            
            return RTRecordValue.retractFromOrdinalUsingTuple(ordinalNames, ordinalValues, retractedTupleSize);
        }
        @Override
        OrdinalData retractOrdinalFields(int[] retractedOrdinalNames) {
            // Update values to remove indirection chains.
            updateOrdinalValues();
            
            return RTRecordValue.retractFromOrdinalUsingOrdinal(ordinalNames, ordinalValues, retractedOrdinalNames); 
        }
        @Override
        TextualData retractTextualFields(String[] retractedTextualNames) {
            throw new UnsupportedOperationException();
        }
      
        @Override
        OrdinalData mergeTupleFields(RTValue[] extensionOrdinalValues) {
            // Update values to remove indirection chains.
            updateOrdinalValues();
            
            return RTRecordValue.mergeOrdinalWithTuple(ordinalNames, ordinalValues, extensionOrdinalValues);
        }                  
        @Override
        OrdinalData mergeOrdinalFields(int[] extensionOrdinalNames, RTValue[] extensionOrdinalValues) {   
            // Update values to remove indirection chains.
            updateOrdinalValues();
            
            return RTRecordValue.mergeOrdinalWithOrdinal(ordinalNames, ordinalValues, extensionOrdinalNames, extensionOrdinalValues);     
        }                  
        @Override
        TextualData mergeTextualFields(String[] extensionTextualNames, RTValue[] extensionTextualValues) {                          
            return new TextualData(extensionTextualNames, extensionTextualValues);      
        }
        
        /** {@inheritDoc} */
        @Override
        public int getNOrdinalFields() {           
            return ordinalNames.length;
        }
        /** {@inheritDoc} */
        @Override
        public int getNthOrdinalFieldName(int n) {
            return ordinalNames[n];           
        }
        /** {@inheritDoc} */
        @Override
        public RTValue getNthOrdinalValue(int n) {
            // Update value to remove indirection chains.
            RTValue ordinalValue = ordinalValues[n];
            if (ordinalValue instanceof RTResultFunction) {
                return (ordinalValues[n] = ordinalValue.getValue());
            }
            
            return ordinalValue;
        }
        
        /** {@inheritDoc} */
        @Override
        public int getNTextualFields() {          
            return 0;
        } 
        /** {@inheritDoc}*/
        @Override
        public String getNthTextualFieldName(int n) {
            throw new IndexOutOfBoundsException();
        }
        /** {@inheritDoc} */
        @Override
        public RTValue getNthTextualValue(int n) {
            throw new IndexOutOfBoundsException();
        }
        
        /** {@inheritDoc} */
        @Override
        public boolean hasTupleOrdinalPart() {
            return false;
        }
        
        /** {@inheritDoc} */
        @Override
        public RTRecordValue appendRecord(RTRecordValue otherRecord) {
            OrdinalData ordinalData = overlappingMergeOrdinalData(getOrdinalData(), otherRecord.getOrdinalData());
            return makeRecordFromFieldData(ordinalData, otherRecord.getTextualData());
        }

        /** {@inheritDoc} */
        @Override
        public boolean sameFields(RecordType otherRecordType) {
            if(!(otherRecordType instanceof OrdinalRecord)) {
                return false;
            }

            OrdinalRecord otherOrdinalRecord = (OrdinalRecord)otherRecordType;
            
            return Arrays.equals(otherOrdinalRecord.ordinalNames, ordinalNames);         
        }
        
        /** {@inheritDoc} */
        @Override
        public RTRecordValue updateOrdinalField(int ordinal, RTValue fieldValue) {
            int index = Arrays.binarySearch(ordinalNames, ordinal);
            RTValue[] newOrdinalValues = ordinalValues.clone();
            newOrdinalValues[index] = fieldValue;            
            return new OrdinalRecord(ordinalNames, newOrdinalValues);           
        }
        
        /** {@inheritDoc} */
        @Override
        public RTRecordValue updateMixedOrdinalField(int ordinal, RTValue fieldValue) {
            // This method should only be called when an we are updating ordinal fields
            // followed by mutating textual fields.  Since an ordinal record doesn't
            // contain textual values this is an error.
            throw new UnsupportedOperationException("Attempt to do mixed update on ordinal record.");
        }
        
        /** {@inheritDoc} */
        @Override
        public RTRecordValue updateTextualField(String textualFieldName, RTValue fieldValue) {              
            throw new UnsupportedOperationException("Attempt to update the ordinal record at field " + textualFieldName + ".");        
        } 
        
        /** {@inheritDoc} */
        @Override
        public RTRecordValue mutateOrdinalField(int ordinal, RTValue fieldValue) {
            int index = Arrays.binarySearch(ordinalNames, ordinal);           
            ordinalValues[index] = fieldValue;            
            return this;         
        }
        
        /** {@inheritDoc} */
        @Override
        public RTRecordValue mutateTextualField(String textualFieldName, RTValue fieldValue) {              
            throw new UnsupportedOperationException("Attempt to mutate the ordinal record at field " + textualFieldName + ".");        
        }         

        /**
         * @see org.openquark.cal.internal.runtime.lecc.RTValue#apply(org.openquark.cal.internal.runtime.lecc.RTValue)
         */
        @Override
        public RTValue apply(RTValue argument) {
                          
            //The meaning of application to a record value is pointwise application i.e.
            //{field1 = f1, field2 = f2, ..., fieldk = fk} x == {field1 = f1 x, field2 = f2 x, ... fieldk = fk x}         
            
            //In normal circumstances, record values cannot be applied to values because the resulting types are
            //not typeable in CAL source. However, this situation does arise in the case of hidden dictionary switching.
            //
            //For example:
            //
            //{field1 = dictOrdInt, field2 = dictOrdString} 0 
            //is changed to
            //{field1 = dictOrdInt 0, field2 = dictOrdString 0}
            //(which latter reduces to {field1 = dictEqInt, field2 = dictEqString})
            //
            //in other words, the Int value being applied is applied pointwise to each field value.
                              
            int nOrdinalFields = getNOrdinalFields();
            RTValue[] newOrdinalValues = new RTValue[nOrdinalFields];
            for (int i = 0; i < nOrdinalFields; ++i) {
                newOrdinalValues[i] = getNthOrdinalValue(i).apply(argument);
            }            
             
            return new OrdinalRecord(this.ordinalNames, newOrdinalValues);                 
        }           
        
        /**
         * Update the ordinal values to remove any indirection chains.
         */
        private final void updateOrdinalValues () {
            for (int i = 0, n = ordinalValues.length; i < n; ++i) {
                RTValue ordinalValue = ordinalValues[i];
                if (ordinalValue instanceof RTResultFunction) {
                    ordinalValues[i] = ordinalValue.getValue();
                }
            }
        }

    }
    
    /**
     * Represents textual records i.e. 
     * -all the fields in the record are textual fields
     * -not the EmptyRecord (i.e. there is at least 1 textual field).
     *     
     * For example,
     * {name = "abc", flag = True} is a TextualRecord     
     *
     * @author Bo Ilic    
     */    
    private static final class TextualRecord extends RTRecordValue {
        
        /** the textual field names of the record, in ascending alphabetical order. Will have positive length. */
        private final String[] textualNames;   
        
        /** 
         * the ith element of textualValues is the field value corresponding to the textual field name
         * held at the ith element of ordinalNames.
         */   
        private final RTValue[] textualValues;
        
        private TextualRecord(String[] textualNames, RTValue[] textualValues) {
            
            assert RTRecordValue.verifyTextualData(textualNames, textualValues) :
                "Invalid textual data in TextualRecord constructor.";
            
            this.textualNames = textualNames;
            this.textualValues = textualValues;
        }
        
        @Override
        public RTRecordValue makeFromValues(RTValue[] ordinalValues, RTValue[] newTextualValues) {
            assert (ordinalValues == null) :
                "Illegal argument in TextualRecord.makeFromValues.";
            
            return new TextualRecord(this.textualNames, newTextualValues);
        }         
        
        @Override
        public RTValue getTextualFieldValue(String textualFieldName) {
            int index = Arrays.binarySearch(textualNames, textualFieldName);
            
            // Update value to remove indirection chains.
            RTValue textualValue  = textualValues[index];
            if (textualValue instanceof RTResultFunction) {
                return (textualValues[index] = textualValue.getValue());
            }
            
            return textualValue;
        }

        @Override
        public RTValue getOrdinalFieldValue(int ordinal) { 
            throw new UnsupportedOperationException();            
        }
       
        @Override
        public int getNFields() {
            return textualNames.length;
        }

        @Override
        public RTValue getNthValue(int n) {
            // Update value to remove indirection chains.
            RTValue textualValue = textualValues[n];
            if (textualValue instanceof RTResultFunction) {
                return (textualValues[n] = textualValue.getValue());
            }
            
            return textualValue;           
        }
        
        /** {@inheritDoc} */
        @Override
        public final CalValue debug_getChild(int childN) {
            return textualValues[childN]; 
        }        
        
        @Override
        public boolean hasOrdinalField(int ordinal) {              
            return false;
        }
                              
        @Override
        public boolean hasTextualField(String textualFieldName) {
            return Arrays.binarySearch(textualNames, textualFieldName) >= 0;      
        }
        
        @Override
        public int indexOfField(String fieldName) {
            int index = Arrays.binarySearch(textualNames, fieldName); 
            if(index < 0) {
                return -1;
            }
            
            return index;
        }
        
        @Override
        public List<String> fieldNames() {           
            return Collections.unmodifiableList(Arrays.asList(textualNames));
        }
        
        @Override
        public List<RTValue> fieldValues() {
            // Update values to remove indirection chains.
            updateTextualValues();
            
            return Collections.unmodifiableList(Arrays.asList(textualValues));
        }        
        
        /** {@inheritDoc}*/
        @Override
        public FieldName getNthFieldName(int n) {            
            return FieldName.make(textualNames[n]);                    
        }           
        
        @Override
        OrdinalData getOrdinalData() {
            return OrdinalData.EMPTY;
        }
        @Override
        TextualData getTextualData() {
            // Update values to remove indirecton chains.
            updateTextualValues();
            
            return new TextualData(textualNames, textualValues);
        }
    
        @Override
        OrdinalData retractTupleFields(int retractedTupleSize) {
            throw new UnsupportedOperationException();
        }
        @Override
        OrdinalData retractOrdinalFields(int[] retractedOrdinalNames) {
            throw new UnsupportedOperationException();
        }
        @Override
        TextualData retractTextualFields(String[] retractedTextualNames) {
            // Update values to remove indirecton chains.
            updateTextualValues();
            
            return RTRecordValue.retractFromTextualUsingTextual(textualNames, textualValues, retractedTextualNames);
        }
      
        @Override
        OrdinalData mergeTupleFields(RTValue[] extensionOrdinalValues) {
            return new OrdinalData.Tuple(extensionOrdinalValues);
        }                  
        @Override
        OrdinalData mergeOrdinalFields(int[] extensionOrdinalNames, RTValue[] extensionOrdinalValues) {   
            return new OrdinalData.Ordinal(extensionOrdinalNames, extensionOrdinalValues);           
        }                  
        @Override
        TextualData mergeTextualFields(String[] extensionTextualNames, RTValue[] extensionTextualValues) {
            // Update values to remove indirecton chains.
            updateTextualValues();
            
            return RTRecordValue.mergeTextualWithTextual(textualNames, textualValues, extensionTextualNames, extensionTextualValues);                
        }
        
        /** {@inheritDoc}*/
        @Override
        public int getNOrdinalFields() {           
            return 0;
        }
        /** {@inheritDoc}*/
        @Override
        public int getNthOrdinalFieldName(int n) {
            throw new IndexOutOfBoundsException();
        }
        /** {@inheritDoc}*/
        @Override
        public RTValue getNthOrdinalValue(int n) {
            throw new IndexOutOfBoundsException();
        }
        
        /** {@inheritDoc}*/
        @Override
        public int getNTextualFields() {          
            return textualNames.length;
        }
        /** {@inheritDoc}*/
        @Override
        public String getNthTextualFieldName(int n) {
            return textualNames[n];
        }
        /** {@inheritDoc}*/
        @Override
        public RTValue getNthTextualValue(int n) {
            // Update value to remove indirection chains.
            RTValue textualValue = textualValues[n];
            if (textualValue instanceof RTResultFunction) {
                return (textualValues[n] = textualValue.getValue());
            }
            
            return textualValue;
        }           
        
        /** {@inheritDoc} */
        @Override
        public boolean hasTupleOrdinalPart() {
            return false;
        } 

        /** {@inheritDoc} */
        @Override
        public RTRecordValue appendRecord(RTRecordValue otherRecord) {
            TextualData textualData = overlappingMergeTextualData(getTextualData(), otherRecord.getTextualData());
            return makeRecordFromFieldData(otherRecord.getOrdinalData(), textualData);
        }
        
        /** {@inheritDoc} */
        @Override
        public boolean sameFields(RecordType otherRecordType) {
            if(!(otherRecordType instanceof TextualRecord)) {
                return false;
            }
            
            TextualRecord otherTextualRecord = (TextualRecord)otherRecordType;

            return Arrays.equals(otherTextualRecord.textualNames, textualNames);            
        }
        
        /** {@inheritDoc} */
        @Override
        public RTRecordValue updateOrdinalField(int ordinal, RTValue fieldValue) {
           throw new UnsupportedOperationException("Attempt to update the textual record at field #" + ordinal + ".");           
        }
        
        /** {@inheritDoc} */
        @Override
        public RTRecordValue updateMixedOrdinalField(int ordinal, RTValue fieldValue) {
            // This method should only be called when an we are updating ordinal fields
            // followed by mutating textual fields.  Since an textual record doesn't
            // contain ordinal values this is an error.
            throw new UnsupportedOperationException("Attempt to do mixed update on textual record.");
        }
        
        /** {@inheritDoc} */
        @Override
        public RTRecordValue updateTextualField(String textualFieldName, RTValue fieldValue) {            
            int index = Arrays.binarySearch(textualNames, textualFieldName);
            RTValue[] newTextualValues = textualValues.clone();
            newTextualValues[index] = fieldValue;            
            return new TextualRecord(textualNames, newTextualValues);           
        }
        
        /** {@inheritDoc} */
        @Override
        public RTRecordValue mutateOrdinalField(int ordinal, RTValue fieldValue) {
           throw new UnsupportedOperationException("Attempt to mutate the textual record at field #" + ordinal + ".");           
        }
        
        /** {@inheritDoc} */
        @Override
        public RTRecordValue mutateTextualField(String textualFieldName, RTValue fieldValue) {            
            int index = Arrays.binarySearch(textualNames, textualFieldName);           
            textualValues[index] = fieldValue;            
            return this;           
        }        

        /**
         * @see org.openquark.cal.internal.runtime.lecc.RTValue#apply(org.openquark.cal.internal.runtime.lecc.RTValue)
         */
        @Override
        public RTValue apply(RTValue argument) {
                          
            //The meaning of application to a record value is pointwise application i.e.
            //{field1 = f1, field2 = f2, ..., fieldk = fk} x == {field1 = f1 x, field2 = f2 x, ... fieldk = fk x}         
            
            //In normal circumstances, record values cannot be applied to values because the resulting types are
            //not typeable in CAL source. However, this situation does arise in the case of hidden dictionary switching.
            //
            //For example:
            //
            //{field1 = dictOrdInt, field2 = dictOrdString} 0 
            //is changed to
            //{field1 = dictOrdInt 0, field2 = dictOrdString 0}
            //(which latter reduces to {field1 = dictEqInt, field2 = dictEqString})
            //
            //in other words, the Int value being applied is applied pointwise to each field value.                                         
            
            int nTextualFields = getNTextualFields();
            RTValue[] newTextualValues = new RTValue[nTextualFields];
            for (int i = 0; i < nTextualFields; ++i) {
                newTextualValues[i] = getNthTextualValue(i).apply(argument);
            }
            
            return new TextualRecord(this.textualNames, newTextualValues);                  
        }        
        
        /**
         * Update the textual values to remove any indirection chains.
         */
        private final void updateTextualValues () {
            for (int i = 0, n = textualValues.length; i < n; ++i) {
                RTValue textualValue = textualValues[i];
                if (textualValue instanceof RTResultFunction) {
                    textualValues[i] = textualValue.getValue();
                }
            }
        }
 
    }
    
    /**
     * Represents records having both ordinal and textual fields, and such that the ordinal part is a tuple i.e. 
     * -there is at least 1 ordinal field, and at least 1 textual field
     * -the ordinal fields are consecutive i.e. #1, #2, ..., #n.
  
     * For example,
     * {#1 = 2.0, #2 = "FooBar", name = "abc", flag = True} is a TupleMixedRecord     
     *
     * @author Bo Ilic    
     */        
    private static final class TupleMixedRecord extends RTRecordValue {
        
        /**
         * the ith element of ordinalValues is the field value corresponding to
         * the ordinal field name #i. Will have positive length.
         */   
        private final RTValue[] ordinalValues;
        
        /**
         * the textual field names of the record, in ascending alphabetical
         * order. Will have positive length.
         */
        private final String[] textualNames;   
        
        /**
         * the ith element of textualValues is the field value corresponding to
         * the textual field name held at the ith element of ordinalNames.
         */   
        private final RTValue[] textualValues;
        
        private TupleMixedRecord(RTValue[] ordinalValues, String[] textualNames, RTValue[] textualValues) {
            
            assert RTRecordValue.verifyTupleData(ordinalValues) :
                "Invalid tuple data in TupleMixedRecord constructor.";
            assert RTRecordValue.verifyTextualData(textualNames, textualValues) :
                "Invalid textual data in TupleMixedRecord constructor.";
            
            this.ordinalValues = ordinalValues;
            this.textualNames = textualNames;
            this.textualValues = textualValues;
        }
        
        @Override
        public RTRecordValue makeFromValues(RTValue[] newOrdinalValues, RTValue[] newTextualValues) {           
            return new TupleMixedRecord(newOrdinalValues, this.textualNames, newTextualValues);
        }          
        
        @Override
        public RTValue getTextualFieldValue(String textualFieldName) {
            int index = Arrays.binarySearch(textualNames, textualFieldName);
            // Update the value to remove indirection chains.
            RTValue textualValue = textualValues[index];
            if (textualValue instanceof RTResultFunction) {
                return (textualValues[index] = textualValue.getValue());
            }
            return textualValue;
        }

        @Override
        public RTValue getOrdinalFieldValue(int ordinal) {
            int index = ordinal - 1;
            // Update value to remove indirection chains.
            RTValue ordinalValue = ordinalValues[index];
            if (ordinalValue instanceof RTResultFunction) {
                return (ordinalValues[index] = ordinalValue.getValue());
            }
            
            return ordinalValue;
        }
       
        @Override
        public int getNFields() {
            return ordinalValues.length + textualNames.length;
        }

        @Override
        public RTValue getNthValue(int n) {
            if (n < ordinalValues.length) {
                // Update value to remove indirection chains.
                RTValue ordinalValue = ordinalValues[n];
                if (ordinalValue instanceof RTResultFunction) {
                    return (ordinalValues[n] = ordinalValue.getValue());
                }
                
                return ordinalValue;
            }

            n = n - ordinalValues.length;
            // Update value to remove indirection chains.
            RTValue textualValue = textualValues[n];
            if (textualValue instanceof RTResultFunction) {
                return (textualValues[n] = textualValue.getValue());
            }
            
            return textualValue;
        } 
        
        /** {@inheritDoc} */
        @Override
        public final CalValue debug_getChild(int childN) {
            if (childN < ordinalValues.length) {
                return ordinalValues[childN];
            }

            return textualValues[childN - ordinalValues.length];
        }        
        
        @Override
        public boolean hasOrdinalField(int ordinal) {              
            return ordinal <= ordinalValues.length;
        }
                              
        @Override
        public boolean hasTextualField(String textualFieldName) {
            return Arrays.binarySearch(textualNames, textualFieldName) >= 0;      
        }
        
        @Override
        public int indexOfField(String fieldName) {
            
            if(FieldName.Ordinal.isValidCalSourceForm(fieldName)) {
                int ordinal = fieldOrdinal(fieldName);
                if(ordinal > getNOrdinalFields()) {
                    return -1;
                }
                
                return ordinal - 1;
            
            } else {
                int index = Arrays.binarySearch(textualNames, fieldName);
                if(index < 0) {
                    return -1;
                }
                
                return getNOrdinalFields() + index;
            }
        }
        
        @Override
        public final List<String> fieldNames() {
            
            int nFields = getNFields();
            String[] fieldNames = new String[nFields]; 
            int nOrdinalFields = ordinalValues.length;            
           
            for (int i = 0; i < nOrdinalFields; ++i) {
                fieldNames[i] = "#" + (i + 1);
            }                   
            
            //copy the textual field names
            System.arraycopy(textualNames, 0, fieldNames, nOrdinalFields, textualNames.length);
                    
            return Arrays.asList(fieldNames);
        } 
        
        @Override
        public final List<RTValue> fieldValues() {
            
            // Update fields to remove indirection chains.
            updateTextualValues();
            updateOrdinalValues();
            
            int nFields = getNFields();
            RTValue[] fieldValues = new RTValue[nFields];
            int nOrdinalFields = ordinalValues.length;
            
            //copy the ordinal field values
            System.arraycopy(ordinalValues, 0, fieldValues, 0, nOrdinalFields);
            
            //copy the textual field values
            System.arraycopy(textualValues, 0, fieldValues, nOrdinalFields, textualValues.length);
                                         
            return Arrays.asList(fieldValues);
        }
        
        /** {@inheritDoc}*/
        @Override
        public final FieldName getNthFieldName(int n) {
            
            int nOrdinalFields = ordinalValues.length;
            
            if (n < nOrdinalFields) {
                return FieldName.makeOrdinalField(n + 1);
            }
            
            return FieldName.make(textualNames[n - nOrdinalFields]);           
        }                
        
        @Override
        final OrdinalData getOrdinalData() {
            // Update values to remove indirection chains.
            updateOrdinalValues();
            
            return new OrdinalData.Tuple(ordinalValues);
        }
        @Override
        final TextualData getTextualData() {
            // Update values to remove indirection chains.
            updateTextualValues();
            
            return new TextualData(textualNames, textualValues);
        }
    
        @Override
        final OrdinalData retractTupleFields(int retractedTupleSize) {
            // Update values to remove indirection chains.
            updateOrdinalValues();
            
            return RTRecordValue.retractFromTupleUsingTuple(ordinalValues, retractedTupleSize);
        }
        @Override
        final OrdinalData retractOrdinalFields(int[] retractedOrdinalNames) {
            // Update values to remove indirection chains.
            updateOrdinalValues();
            
            return RTRecordValue.retractFromTupleUsingOrdinal(ordinalValues, retractedOrdinalNames);
        }
        @Override
        final TextualData retractTextualFields(String[] retractedTextualNames) {
            // Update values to remove indirection chains.
            updateTextualValues();
            
            return RTRecordValue.retractFromTextualUsingTextual(textualNames, textualValues, retractedTextualNames);
        }
      
        @Override
        final OrdinalData mergeTupleFields(RTValue[] extensionOrdinalValues) {
            throw new UnsupportedOperationException();
        }                  
        @Override
        final OrdinalData mergeOrdinalFields(int[] extensionOrdinalNames, RTValue[] extensionOrdinalValues) {
            // Update values to remove indirection chains.
            updateOrdinalValues();
            
            return RTRecordValue.mergeTupleWithOrdinal(ordinalValues, extensionOrdinalNames, extensionOrdinalValues);     
        }                 
        @Override
        final TextualData mergeTextualFields(String[] extensionTextualNames, RTValue[] extensionTextualValues) {
            // Update values to remove indirection chains.
            updateTextualValues();
            
            return RTRecordValue.mergeTextualWithTextual(textualNames, textualValues, extensionTextualNames, extensionTextualValues);        
        } 
        
        /** {@inheritDoc}*/
        @Override
        public int getNOrdinalFields() {           
            return ordinalValues.length;
        }
        /** {@inheritDoc}*/
        @Override
        public int getNthOrdinalFieldName(int n) {   
            if (n < 0 || n >= ordinalValues.length) {
                throw new IndexOutOfBoundsException();
            }
            return (n + 1);
        }
        /** {@inheritDoc}*/
        @Override
        public RTValue getNthOrdinalValue(int n) {
            // Update value to remove indirection chains.
            RTValue ordinalValue = ordinalValues[n];
            if (ordinalValue instanceof RTResultFunction) {
                return (ordinalValues[n] = ordinalValue.getValue());
            }
            
            return ordinalValue;
        }
        
        /** {@inheritDoc}*/
        @Override
        public int getNTextualFields() {          
            return textualNames.length;
        } 
        /** {@inheritDoc}*/
        @Override
        public String getNthTextualFieldName(int n) {          
            return textualNames[n];
        }         
        /** {@inheritDoc}*/
        @Override
        public RTValue getNthTextualValue(int n) {
            // Update value to remove indirection chains.
            RTValue textualValue = textualValues[n];
            if (textualValue instanceof RTResultFunction) {
                return (textualValues[n] = textualValue.getValue());
            }
            
            return textualValue;
        }
        
        /** {@inheritDoc} */
        @Override
        public boolean hasTupleOrdinalPart() {
            return true;
        }
        
        /** {@inheritDoc} */
        @Override
        public RTRecordValue appendRecord(RTRecordValue otherRecord) {
            OrdinalData ordinalData = overlappingMergeOrdinalData(getOrdinalData(), otherRecord.getOrdinalData());
            TextualData textualData = overlappingMergeTextualData(getTextualData(), otherRecord.getTextualData());
            return makeRecordFromFieldData(ordinalData, textualData);
        }
        
        /** {@inheritDoc} */
        @Override
        public boolean sameFields(RecordType otherRecordType) {
            
            if(!(otherRecordType instanceof TupleMixedRecord)) {
                return false;
            }
            
            TupleMixedRecord otherTupleMixedRecord = (TupleMixedRecord)otherRecordType;
            
            if(otherTupleMixedRecord.ordinalValues.length != ordinalValues.length) {
                return false;
            }
            
            return Arrays.equals(otherTupleMixedRecord.textualNames, textualNames);         
        }
        
        /** {@inheritDoc} */
        @Override
        public RTRecordValue updateOrdinalField(int ordinal, RTValue fieldValue) {
            RTValue[] newOrdinalValues = ordinalValues.clone();
            newOrdinalValues[ordinal - 1] = fieldValue;
            
            return new TupleMixedRecord(newOrdinalValues, textualNames, textualValues);           
        }
        
        /** {@inheritDoc} */
        @Override
        public RTRecordValue updateMixedOrdinalField(int ordinal, RTValue fieldValue) {
            RTValue[] newOrdinalValues = ordinalValues.clone();
            newOrdinalValues[ordinal - 1] = fieldValue;
            
            // We want to make a new array to hold textual values in the new record.  This 
            // allows the new records textual values to be mutated without affecting this
            // record.
            RTValue[] newTextualValues = new RTValue[textualValues.length];
            System.arraycopy(textualValues, 0, newTextualValues, 0, newTextualValues.length);
            
            return new TupleMixedRecord(newOrdinalValues, textualNames, newTextualValues);           
        }
                
        /** {@inheritDoc} */
        @Override
        public RTRecordValue updateTextualField(String textualFieldName, RTValue fieldValue) {            
            int index = Arrays.binarySearch(textualNames, textualFieldName);
            RTValue[] newTextualValues = textualValues.clone();
            newTextualValues[index] = fieldValue;

            return new TupleMixedRecord(ordinalValues, textualNames, newTextualValues);           
        } 
        
        /** {@inheritDoc} */
        @Override
        public RTRecordValue mutateOrdinalField(int ordinal, RTValue fieldValue) {            
            ordinalValues[ordinal - 1] = fieldValue;
            return this;           
        }
        
        /** {@inheritDoc} */
        @Override
        public RTRecordValue mutateTextualField(String textualFieldName, RTValue fieldValue) {            
            int index = Arrays.binarySearch(textualNames, textualFieldName);           
            textualValues[index] = fieldValue;     
            return this;           
        }         

        /**
         * @see org.openquark.cal.internal.runtime.lecc.RTValue#apply(org.openquark.cal.internal.runtime.lecc.RTValue)
         */
        @Override
        public RTValue apply(RTValue argument) {
                          
            //The meaning of application to a record value is pointwise application i.e.
            //{field1 = f1, field2 = f2, ..., fieldk = fk} x == {field1 = f1 x, field2 = f2 x, ... fieldk = fk x}         
            
            //In normal circumstances, record values cannot be applied to values because the resulting types are
            //not typeable in CAL source. However, this situation does arise in the case of hidden dictionary switching.
            //
            //For example:
            //
            //{field1 = dictOrdInt, field2 = dictOrdString} 0 
            //is changed to
            //{field1 = dictOrdInt 0, field2 = dictOrdString 0}
            //(which latter reduces to {field1 = dictEqInt, field2 = dictEqString})
            //
            //in other words, the Int value being applied is applied pointwise to each field value.
                              
            int nOrdinalFields = getNOrdinalFields();
            RTValue[] newOrdinalValues = new RTValue[nOrdinalFields];
            for (int i = 0; i < nOrdinalFields; ++i) {
                newOrdinalValues[i] = getNthOrdinalValue(i).apply(argument);
            }            
            
            int nTextualFields = getNTextualFields();
            RTValue[] newTextualValues = new RTValue[nTextualFields];
            for (int i = 0; i < nTextualFields; ++i) {
                newTextualValues[i] = getNthTextualValue(i).apply(argument);
            }
            
            return new TupleMixedRecord(newOrdinalValues, this.textualNames, newTextualValues);
                  
        }        
        
        /**
         * Update the ordinal values to remove any indirection chains.
         */
        private final void updateOrdinalValues () {
            for (int i = 0, n = ordinalValues.length; i < n; ++i) {
                RTValue ordinalValue = ordinalValues[i];
                if (ordinalValue instanceof RTResultFunction) {
                    ordinalValues[i] = ordinalValue.getValue();
                }
            }
        }
        
        /**
         * Update the textual values to remove any indirection chains.
         */
        private final void updateTextualValues () {
            for (int i = 0, n = textualValues.length; i < n; ++i) {
                RTValue textualValue = textualValues[i];
                if (textualValue instanceof RTResultFunction) {
                    textualValues[i] = textualValue.getValue();
                }
            }
        }
        
    }
    
    /**
     * Represents records having both ordinal and textual fields, and such that the ordinal part is not a tuple i.e. 
     * -there is at least 1 ordinal field, and at least 1 textual field
     * -the ordinal fields are not consecutive i.e. #1, #2, ..., #n.
  
     * For example,
     * {#1 = 2.0, #3 = "FooBar", name = "abc", flag = True} is a MixedRecord     
     *
     * @author Bo Ilic    
     */          
    private static final class MixedRecord extends RTRecordValue {

        /**
         * the ordinal field names (as int values) in ascending ordinal order.
         * Will have positive length and not be [1, 2, 3, ...,
         * ordinalValues.length]
         */
        private final int[] ordinalNames;

        /**
         * the ith element of ordinalValues is the field value corresponding to
         * the ordinal field name held at the ith element of ordinalNames.
         */
        private final RTValue[] ordinalValues;

        /**
         * the textual field names of the record, in ascending alphabetical
         * order. Will have positive length.
         */
        private final String[] textualNames;

        /**
         * the ith element of textualValues is the field value corresponding to
         * the textual field name held at the ith element of ordinalNames.
         */
        private final RTValue[] textualValues;

        private MixedRecord(int[] ordinalNames, RTValue[] ordinalValues, String[] textualNames, RTValue[] textualValues) {
            
            assert RTRecordValue.verifyOrdinalData(ordinalNames, ordinalValues) :
                "Invalid ordinal data in MixedRecord constructor.";
            assert RTRecordValue.verifyTextualData(textualNames, textualValues) :
                "Invalid textual data in MixedRecord constructor.";
            
            this.ordinalNames = ordinalNames;
            this.ordinalValues = ordinalValues;
            this.textualNames = textualNames;
            this.textualValues = textualValues;
        }
        
        @Override
        public RTRecordValue makeFromValues(RTValue[] newOrdinalValues, RTValue[] newTextualValues) {           
            return new MixedRecord(this.ordinalNames, newOrdinalValues, this.textualNames, newTextualValues);
        }        

        @Override
        public RTValue getTextualFieldValue(String textualFieldName) {
            int index = Arrays.binarySearch(textualNames, textualFieldName);
            // Update the value to remove any indirection chains.
            RTValue textualValue = textualValues[index];
            if (textualValue instanceof RTResultFunction) {
                return (textualValues[index] = textualValue.getValue());
            }
            
            return textualValue;
        }

        @Override
        public RTValue getOrdinalFieldValue(int ordinal) {     
            int index = Arrays.binarySearch(ordinalNames, ordinal);
            // Update the value to remove any indirection chains.
            RTValue ordinalValue = ordinalValues[index];
            if (ordinalValue instanceof RTResultFunction) {
                return (ordinalValues[index] = ordinalValue.getValue());
            }
            
            return ordinalValue;
        }
       
        @Override
        public int getNFields() {
            return ordinalNames.length + textualNames.length;
        }

        /**
         * Update the ordinal values to remove any indirection chains.
         */
        private final void updateOrdinalValues () {
            for (int i = 0, n = ordinalValues.length; i < n; ++i) {
                RTValue ordinalValue = ordinalValues[i];
                if (ordinalValue instanceof RTResultFunction) {
                    ordinalValues[i] = ordinalValue.getValue();
                }
            }
        }
        
        /**
         * Update the textual values to remove any indirection chains.
         */
        private final void updateTextualValues () {
            for (int i = 0, n = textualValues.length; i < n; ++i) {
                RTValue textualValue = textualValues[i];
                if (textualValue instanceof RTResultFunction) {
                    textualValues[i] = textualValue.getValue();
                }
            }
        }
        
        @Override
        public RTValue getNthValue(int n) {
            if (n < ordinalValues.length) {
                // Update the value to remove indirection chains.
                RTValue ordinalValue = ordinalValues[n];
                if (ordinalValue instanceof RTResultFunction) {
                    return (ordinalValues[n] = ordinalValue.getValue());
                }
                return ordinalValue;
            }

            n = n - ordinalValues.length;
            // Update the value to remove indirection chains.
            RTValue textualValue = textualValues[n];
            if (textualValue instanceof RTResultFunction) {
                return (textualValues[n] = textualValue.getValue());
            }
            return textualValue;
        }
        
        /** {@inheritDoc} */
        @Override
        public final CalValue debug_getChild(int childN) {
            if (childN < ordinalValues.length) {
                return ordinalValues[childN];
            }

            return textualValues[childN - ordinalValues.length];
        }         
        
        @Override
        public boolean hasOrdinalField(int ordinal) {
                        
            //if the ordinal is bigger than all the ordinals in ordinalNames, then we can dispense with binary search
            if (ordinal > ordinalNames[ordinalNames.length - 1]) {
                return false;
            }
            
            return Arrays.binarySearch(ordinalNames, ordinal) >= 0;      
        }        
               
        @Override
        public boolean hasTextualField(String textualFieldName) {
            return Arrays.binarySearch(textualNames, textualFieldName) >= 0;      
        }
        
        @Override
        public int indexOfField(String fieldName) {
            
            
            if(FieldName.Ordinal.isValidCalSourceForm(fieldName)) {
                int ordinal = fieldOrdinal(fieldName);

                int index = Arrays.binarySearch(ordinalNames, ordinal);
                if(index < 0) {
                    return -1;
                }
                
                return index;
            
            } else {
                int index = Arrays.binarySearch(textualNames, fieldName);
                if(index < 0) {
                    return -1;
                }
                
                return getNOrdinalFields() + index;
            }
        }
        
        @Override
        public List<String> fieldNames() {
            
            int nFields = getNFields();
            String[] fieldNames = new String[nFields]; 
            int nOrdinalFields = ordinalNames.length;
                        
            for (int i = 0; i < nOrdinalFields; ++i) {
                fieldNames[i] = "#" + ordinalNames[i];
            }            
            
            //copy the textual field names
            System.arraycopy(textualNames, 0, fieldNames, nOrdinalFields, textualNames.length);
                    
            return Arrays.asList(fieldNames);
        }
        
        @Override
        public List<RTValue> fieldValues() {
            
            int nFields = getNFields();
            RTValue[] fieldValues = new RTValue[nFields];
            int nOrdinalFields = ordinalValues.length;
            int nTextualFields = textualValues.length;

            // Update the ordinal values to remove any indirection chains.
            updateOrdinalValues();

            // Update the textual values to remove any indirection chains.
            updateTextualValues();
            
            //copy the ordinal field values
            System.arraycopy(ordinalValues, 0, fieldValues, 0, nOrdinalFields);
            
            //copy the textual field values
            System.arraycopy(textualValues, 0, fieldValues, nOrdinalFields, nTextualFields);
                                         
            return Arrays.asList(fieldValues);
        }        
        
        /** {@inheritDoc}*/
        @Override
        public FieldName getNthFieldName(int n) {
                                    
            int nOrdinalFields = ordinalNames.length;
            
            if (n < nOrdinalFields) {
                return FieldName.makeOrdinalField(ordinalNames[n]);
            }
            
            return FieldName.make(textualNames[n - nOrdinalFields]);           
        }        
        
        @Override
        OrdinalData getOrdinalData() {
            // Update the ordinal values to remove any indirection chains.
            updateOrdinalValues();
            
            return new OrdinalData.Ordinal(ordinalNames, ordinalValues);
        }
        @Override
        TextualData getTextualData() {
            // Update the textual values to remove any indirection chains.
            updateTextualValues();
            
            return new TextualData(textualNames, textualValues);
        }
    
        @Override
        OrdinalData retractTupleFields(int retractedTupleSize) {
            // Update values to remove indirection chains.
            updateOrdinalValues();
            
            return RTRecordValue.retractFromOrdinalUsingTuple(ordinalNames, ordinalValues, retractedTupleSize);
        }
        @Override
        OrdinalData retractOrdinalFields(int[] retractedOrdinalNames) {
            // Update values to remove indirection chains.
            updateOrdinalValues();
            
            return RTRecordValue.retractFromOrdinalUsingOrdinal(ordinalNames, ordinalValues, retractedOrdinalNames);
        }
        @Override
        TextualData retractTextualFields(String[] retractedTextualNames) {
            // Update values to remove indirection chains.
            updateTextualValues();
            
            return RTRecordValue.retractFromTextualUsingTextual(textualNames, textualValues, retractedTextualNames);
        }
      
        @Override
        OrdinalData mergeTupleFields(RTValue[] extensionOrdinalValues) {
            // Update values to remove indirection chains.
            updateOrdinalValues();
            
            return RTRecordValue.mergeOrdinalWithTuple(ordinalNames, ordinalValues, extensionOrdinalValues);  
        }        
        @Override
        OrdinalData mergeOrdinalFields(int[] extensionOrdinalNames, RTValue[] extensionOrdinalValues) { 
            // Update values to remove indirection chains.
            updateOrdinalValues();
            
            return RTRecordValue.mergeOrdinalWithOrdinal(ordinalNames, ordinalValues, extensionOrdinalNames, extensionOrdinalValues);     
        }                     
        @Override
        TextualData mergeTextualFields(String[] extensionTextualNames, RTValue[] extensionTextualValues) {
            // Update values to remove indirection chains.
            updateTextualValues();
            
            return RTRecordValue.mergeTextualWithTextual(textualNames, textualValues, extensionTextualNames, extensionTextualValues);        
        }
        
        /** {@inheritDoc}*/
        @Override
        public int getNOrdinalFields() {           
            return ordinalValues.length;
        }
        /** {@inheritDoc}*/
        @Override
        public int getNthOrdinalFieldName(int n) {
            return ordinalNames[n];
        }
        /** {@inheritDoc}*/
        @Override
        public RTValue getNthOrdinalValue(int n) {
            // Update the value to remove any indirection chains.
            RTValue ordinalValue = ordinalValues[n];
            if (ordinalValue instanceof RTResultFunction) {
                return (ordinalValues[n] = ordinalValue.getValue());
            }
            return ordinalValue;
        }
        
        /** {@inheritDoc}*/
        @Override
        public int getNTextualFields() {          
            return textualNames.length;
        } 
        /** {@inheritDoc}*/
        @Override
        public String getNthTextualFieldName(int n) {          
            return textualNames[n];
        }         
        /** {@inheritDoc}*/
        @Override
        public RTValue getNthTextualValue(int n) {
            // Update the value to remove any indirection chains.
            RTValue textualValue = textualValues[n];
            if (textualValue instanceof RTResultFunction) {
                return (textualValues[n] = textualValue.getValue());
            }
            return textualValue;
        }
        
        /** {@inheritDoc} */
        @Override
        public boolean hasTupleOrdinalPart() {
            return false;
        }
        
        /** {@inheritDoc} */
        @Override
        public RTRecordValue appendRecord(RTRecordValue otherRecord) {
            OrdinalData ordinalData = overlappingMergeOrdinalData(getOrdinalData(), otherRecord.getOrdinalData());
            TextualData textualData = overlappingMergeTextualData(getTextualData(), otherRecord.getTextualData());
            return makeRecordFromFieldData(ordinalData, textualData);
        }
        
        /** {@inheritDoc} */
        @Override
        public boolean sameFields(RecordType otherRecordType) {
            
            if(!(otherRecordType instanceof MixedRecord)) {
                return false;
            }
            
            MixedRecord otherMixedRecord = (MixedRecord)otherRecordType;
            
            return Arrays.equals(otherMixedRecord.ordinalNames, ordinalNames)
                && Arrays.equals(otherMixedRecord.textualNames, textualNames);
        }
        
        /** {@inheritDoc} */
        @Override
        public RTRecordValue updateOrdinalField(int ordinal, RTValue fieldValue) {
            int index = Arrays.binarySearch(ordinalNames, ordinal);
            RTValue[] newOrdinalValues = ordinalValues.clone();
            newOrdinalValues[index] = fieldValue;

            return new MixedRecord(ordinalNames, newOrdinalValues, textualNames, textualValues);           
        }
        
        /** {@inheritDoc} */
        @Override
        public RTRecordValue updateTextualField(String textualFieldName, RTValue fieldValue) {            
            int index = Arrays.binarySearch(textualNames, textualFieldName);
            RTValue[] newTextualValues = textualValues.clone();
            newTextualValues[index] = fieldValue;            

            return new MixedRecord(ordinalNames, ordinalValues, textualNames, newTextualValues);           
        }   
        
        /** {@inheritDoc} */
        @Override
        public RTRecordValue updateMixedOrdinalField(int ordinal, RTValue fieldValue) {
            int index = Arrays.binarySearch(ordinalNames, ordinal);
            RTValue[] newOrdinalValues = ordinalValues.clone();
            newOrdinalValues[index] = fieldValue;

            // We want to make a new array to hold textual values in the new record.  This 
            // allows the new records textual values to be mutated without affecting this
            // record.
            RTValue[] newTextualValues = new RTValue[textualValues.length];
            System.arraycopy(textualValues, 0, newTextualValues, 0, newTextualValues.length);
            
            return new MixedRecord(ordinalNames, newOrdinalValues, textualNames, newTextualValues);           
            
        }
        
        /** {@inheritDoc} */
        @Override
        public RTRecordValue mutateOrdinalField(int ordinal, RTValue fieldValue) {
            int index = Arrays.binarySearch(ordinalNames, ordinal);         
            ordinalValues[index] = fieldValue;            
            return this;           
        }
        
        /** {@inheritDoc} */
        @Override
        public RTRecordValue mutateTextualField(String textualFieldName, RTValue fieldValue) {            
            int index = Arrays.binarySearch(textualNames, textualFieldName);            
            textualValues[index] = fieldValue;            
            return this;           
        }           

        /**
         * @see org.openquark.cal.internal.runtime.lecc.RTValue#apply(org.openquark.cal.internal.runtime.lecc.RTValue)
         */
        @Override
        public RTValue apply(RTValue argument) {
                          
            //The meaning of application to a record value is pointwise application i.e.
            //{field1 = f1, field2 = f2, ..., fieldk = fk} x == {field1 = f1 x, field2 = f2 x, ... fieldk = fk x}         
            
            //In normal circumstances, record values cannot be applied to values because the resulting types are
            //not typeable in CAL source. However, this situation does arise in the case of hidden dictionary switching.
            //
            //For example:
            //
            //{field1 = dictOrdInt, field2 = dictOrdString} 0 
            //is changed to
            //{field1 = dictOrdInt 0, field2 = dictOrdString 0}
            //(which latter reduces to {field1 = dictEqInt, field2 = dictEqString})
            //
            //in other words, the Int value being applied is applied pointwise to each field value.
                              
            int nOrdinalFields = getNOrdinalFields();
            RTValue[] newOrdinalValues = new RTValue[nOrdinalFields];
            for (int i = 0; i < nOrdinalFields; ++i) {
                newOrdinalValues[i] = getNthOrdinalValue(i).apply(argument);
            }            
            
            int nTextualFields = getNTextualFields();
            RTValue[] newTextualValues = new RTValue[nTextualFields];
            for (int i = 0; i < nTextualFields; ++i) {
                newTextualValues[i] = getNthTextualValue(i).apply(argument);
            }
            
            return new MixedRecord(this.ordinalNames, newOrdinalValues, this.textualNames, newTextualValues);                 
        }
    }
                  
    public static RTRecordValue makeTupleRecord(RTValue[] ordinalValues) {
        return new TupleRecord(ordinalValues);    
    }
        
    public static RTRecordValue makeOrdinalRecord(int[] ordinalNames, RTValue[] ordinalValues) {
        return new OrdinalRecord(ordinalNames, ordinalValues);
    }
    
    public static RTRecordValue makeTextualRecord(String[] textualNames, RTValue[] textualValues) {
        return new TextualRecord(textualNames, textualValues);
    }
    
    public static RTRecordValue makeTupleMixedRecord(RTValue[] ordinalValues, String[] textualNames, RTValue[] textualValues) {
        return new TupleMixedRecord(ordinalValues, textualNames, textualValues);
    }    
    
    public static RTRecordValue makeMixedRecord(int[] ordinalNames, RTValue[] ordinalValues, String[] textualNames, RTValue[] textualValues) {
        return new MixedRecord(ordinalNames, ordinalValues, textualNames, textualValues);
    }
    

    /**
     * Makes a new RTRecordValue having the same field names as this record value, but with the values obtained
     * from ordinalValues and textualValues, which are assumed to be properly ordered by FieldName ordering.
     * @param ordinalValues should be null if there are no ordinal values
     * @param textualValues should be null if there are no textual values
     * @return RTRecordValue
     */
    abstract public RTRecordValue makeFromValues(RTValue[] ordinalValues, RTValue[] textualValues);
        
   
          
    abstract public RTValue getTextualFieldValue(String textualFieldName);    
    abstract public RTValue getOrdinalFieldValue(int ordinal);
    
    /**
     * @return the number of fields that are in this record. This includes textual as well as ordinal fields.
     */
    abstract public int getNFields(); 
    /**
     * Note that the fields of the record are ordered in field-name order i.e.
     * ordinal fields first, in numeric order, followed by textual fields in alphabetical order.     
     * @param n
     * @return the field name of the nth field in the record as a String e.g. "#1" or "orderDate".
     */
    abstract public FieldName getNthFieldName(int n);     
    /** 
     * Returns the value of the field at index n, but also has the side effect of updating the record
     * at index n to remove the top-most indirection chain. This is similar to field accessors for data
     * constructors.
     * @param n zero-based index into the fields of the record
     * @return the RTValue corresponding to the field at index n.
     */
    abstract public RTValue getNthValue(int n);
    
    /**     
     * @return true if the record has consecutive ordinal fields #1, #2, ... #n, where n >= 1. The record
     *    may or may not have textual fields.
     */
    abstract public boolean hasTupleOrdinalPart();
    
    public final boolean hasOrdinalFields() {
        return getNOrdinalFields() > 0;
    }
    /**     
     * @return the number of ordinal fields in this record
     */
    abstract public int getNOrdinalFields();
    /**     
     * @param n zero-based index into the ordinal fields
     * @return the ordinal field name of the nth ordinal field
     */
    abstract public int getNthOrdinalFieldName(int n);
    /**     
     * @param n zero-based index into the ordinal fields
     * @return the value corresponding to the ordinal field at index n.
     */
    abstract public RTValue getNthOrdinalValue(int n);
    
    /**     
     * @return true if this record has 1 or more textual fields
     */
    public final boolean hasTextualFields() {
        return getNTextualFields() > 0;
    }
    abstract public int getNTextualFields();
    abstract public String getNthTextualFieldName(int n);
    abstract public RTValue getNthTextualValue(int n); 
    
    /**     
     * @return boolean. True if the record has the fields #1, #2, ..., #n, with no gaps, n >= 2
     *   and there are no other fields.
     */
    public boolean isTuple2OrMoreRecord() {
        return false;
    }    
    
    /**     
     * Used to implement the primitive Prelude.hasField function.
     * @param calSourceFieldName  fieldName as a String, e.g. "#2", "#454", "orderDate" etc.
     * @return boolean whether this record has a mapping for the given ordinal.
     */    
    public final boolean hasField(String calSourceFieldName) {
        
        //todoBI we should be able to implement hasField more efficiently in the
        //case that the fieldName is given in literal form
        //e.g. hasField r "#2" should be inlined directly at compile-time to
        //hasOrdinalField r 2.        
        
        FieldName fieldName = FieldName.make(calSourceFieldName);
        if (fieldName == null) {
            return false;
        }
        
        if (fieldName instanceof FieldName.Textual) {
            return hasTextualField(calSourceFieldName);
        }
        
        return hasOrdinalField(((FieldName.Ordinal)fieldName).getOrdinal());               
    }
    
    /**     
     * Used to implement the primitive Prelude.hasField function.
     * @param ordinal must be a valid ordinal i.e. >= 1.
     * @return boolean whether this record has a mapping for the given ordinal.
     */
    abstract public boolean hasOrdinalField(int ordinal); 
    
    /**     
     * Used to implement the primitive Prelude.hasField function.
     * @param textualFieldName
     * @return boolean whether this record has a mapping for the given textualFieldName.
     */
    abstract public boolean hasTextualField(String textualFieldName);
    
    /**
     * Used to implement the primitive Prelude.recordFieldIndex function.
     * @param fieldName String name of field
     * @return int 0-based index of the specified field if this record has the specified field, or -1 otherwise
     */
    abstract public int indexOfField(String fieldName);
    
    /**     
     * Used to implement the primitive Prelude.fieldNamesPrimitive function.       
     *      
     * @return List (of Strings) the sorted list of fieldNames. The sort order is as specified by
     *   the FieldName.CalSourceFormComparator comparator i.e. ordinal field names before textual field names.
     */
    abstract public List<String> fieldNames();
    
    /**
     * Used to implement the primitive Prelude.fieldValuesPrimitive function.
     * 
     * @return List (of RTValue) the list of field values, sorted in field-name order.
     */
    abstract public List<RTValue> fieldValues();
            
    public final RTRecordValue makeTupleRecordRetraction(int retractedTupleSize) {
        
        assert (retractedTupleSize != 0) :
            "Illegal argument in TupleRecord.makeTupleRecordRetraction.";
        
        return RTRecordValue.makeRecordFromFieldData(
            retractTupleFields(retractedTupleSize),
            getTextualData());        
    }
    
    public final RTRecordValue makeOrdinalRecordRetraction(int[] retractedOrdinalNames) {
        
        int nRetractedOrdinals = retractedOrdinalNames.length;
        assert (retractedOrdinalNames[nRetractedOrdinals - 1] != nRetractedOrdinals) :
            "Illegal argument in TupleRecord.makeOrdinalRecordRetraction.";
        
        return RTRecordValue.makeRecordFromFieldData(
            retractOrdinalFields(retractedOrdinalNames),
            getTextualData());            
    }
    
    public final RTRecordValue makeTextualRecordRetraction(String[] retractedTextualNames) {
        
        assert (retractedTextualNames.length != 0) :
            "Illegal argument in TupleRecord.makeTextualRecordRetraction.";
        
        return RTRecordValue.makeRecordFromFieldData(
            getOrdinalData(),
            retractTextualFields(retractedTextualNames));
    } 
    
    public final RTRecordValue makeTupleMixedRecordRetraction(int retractedTupleSize, String[] retractedTextualNames) {
        
        assert (retractedTupleSize != 0 &&
            retractedTextualNames.length != 0) :
            "Illegal argument in TupleRecord.makeTupleMixedRecordRetraction.";
        
        return RTRecordValue.makeRecordFromFieldData(
            retractTupleFields(retractedTupleSize),
            retractTextualFields(retractedTextualNames));        
        
    }
    
    public final RTRecordValue makeMixedRecordRetraction(int[] retractedOrdinalNames, String[] retractedTextualNames) {
       
        int nRetractedOrdinals = retractedOrdinalNames.length;
        assert (retractedOrdinalNames[nRetractedOrdinals - 1] != nRetractedOrdinals &&
            retractedTextualNames.length != 0) :
            "Illegal argument in TupleRecord.makeMixedRecordRetraction.";
        
        return RTRecordValue.makeRecordFromFieldData(
            retractOrdinalFields(retractedOrdinalNames),
            retractTextualFields(retractedTextualNames));
    } 
    
    
    
    public final RTRecordValue makeTupleRecordExtension(RTValue[] extensionOrdinalValues) {
                
        assert RTRecordValue.verifyTupleData(extensionOrdinalValues) :
            "Invalid tuple data in RTRecordValue.makeTupleRecordExtension().";
               
        return RTRecordValue.makeRecordFromFieldData(
            mergeTupleFields(extensionOrdinalValues),
            getTextualData());
    }
        
    public final RTRecordValue makeOrdinalRecordExtension(int[] extensionOrdinalNames, RTValue[] extensionOrdinalValues) {
        
        assert RTRecordValue.verifyOrdinalData(extensionOrdinalNames, extensionOrdinalValues) :
            "Invalid ordinal data in RTRecordValue.makeOrdinalRecordExtension().";
        
        return RTRecordValue.makeRecordFromFieldData(
            mergeOrdinalFields(extensionOrdinalNames, extensionOrdinalValues),
            getTextualData());           
    }
    
    public final RTRecordValue makeTextualRecordExtension(String[] extensionTextualNames, RTValue[] extensionTextualValues) {
        
        assert RTRecordValue.verifyTextualData(extensionTextualNames, extensionTextualValues) :
            "Invalid textual data in RTRecordValue.makeTextualRecordExtension()."; 
        
        return RTRecordValue.makeRecordFromFieldData(
            getOrdinalData(),
            mergeTextualFields(extensionTextualNames, extensionTextualValues));       
    }
    
    public final RTRecordValue makeTupleMixedRecordExtension(RTValue[] extensionOrdinalValues,
            String[] extensionTextualNames, RTValue[] extensionTextualValues) {
        
        assert RTRecordValue.verifyTupleData(extensionOrdinalValues) :
            "Invalid tuple data in RTRecordValue.makeTupleMixedRecordExtension().";
        assert RTRecordValue.verifyTextualData(extensionTextualNames, extensionTextualValues) :
            "Invalid textual data in RTRecordValue.makeTupleMixedRecordExtension().";
        
        return RTRecordValue.makeRecordFromFieldData(
            mergeTupleFields(extensionOrdinalValues),
            mergeTextualFields(extensionTextualNames, extensionTextualValues));                 
    }
    
    public final RTRecordValue makeMixedRecordExtension(int[] extensionOrdinalNames, RTValue[] extensionOrdinalValues,
            String[] extensionTextualNames, RTValue[] extensionTextualValues) {
        
        assert RTRecordValue.verifyOrdinalData(extensionOrdinalNames, extensionOrdinalValues) : "Invalid ordinal data in RTRecordValue.makeMixedRecordExtension().";
        assert RTRecordValue.verifyTextualData(extensionTextualNames, extensionTextualValues) : "Invalid textual data in RTRecordValue.makeMixedRecordExtension().";
        
        return RTRecordValue.makeRecordFromFieldData(
            mergeOrdinalFields(extensionOrdinalNames, extensionOrdinalValues),
            mergeTextualFields(extensionTextualNames, extensionTextualValues));  
    } 
    
    /**
     * Non-destructive update of this RTRecordValue at a specified ordinal field name.
     * Note that an exception will be thrown if the record does not have a field with the
     * given ordinal field name.   
     * @param ordinal
     * @param fieldValue
     * @return a copy of this RTRecordValue with the ordinal field updated to hold 'fieldValue'. 
     */
    public abstract RTRecordValue updateOrdinalField(int ordinal, RTValue fieldValue);
    
    /**
     * Non-destructive update of this RTRecordValue at a specified ordinal field name.
     * Note that an exception will be thrown if the record does not have a field with the
     * given ordinal field name.   
     * This function differs from updateOrdinalField in that it allows for subseqent 
     * mutation of any textual fields. 
     *  
     * @param ordinal
     * @param fieldValue
     * @return a copy of this RTRecordValue with the ordinal field updated to hold 'fieldValue'.
     */
    public abstract RTRecordValue updateMixedOrdinalField(int ordinal, RTValue fieldValue);
    
    /**
     * Non-destructive update of this RTRecordValue at a specified textual field name.
     * Note that an exception will be thrown if the record does not have a field with the
     * given textual field name.   
     * @param textualFieldName
     * @param fieldValue
     * @return a copy of this RTRecordValue with the textual field updated to hold 'fieldValue'. 
     */    
    public abstract RTRecordValue updateTextualField(String textualFieldName, RTValue fieldValue);
    
    /**
     * Destructive update of this RTRecordValue at a specified ordinal field name.
     * Note that an exception will be thrown if the record does not have a field with the
     * given ordinal field name.   
     * @param ordinal
     * @param fieldValue
     * @return this RTRecordValue with the ordinal field updated to hold 'fieldValue' as a side-effect. 
     */
    public abstract RTRecordValue mutateOrdinalField(int ordinal, RTValue fieldValue);
    
    /**
     * Destructive update of this RTRecordValue at a specified textual field name.
     * Note that an exception will be thrown if the record does not have a field with the
     * given textual field name.   
     * @param textualFieldName
     * @param fieldValue
     * @return this RTRecordValue with the textual field updated to hold 'fieldValue' as a side-effect. 
     */    
    public abstract RTRecordValue mutateTextualField(String textualFieldName, RTValue fieldValue);    
    
    
    
    /**
     * Return a new record containing this record's fields plus the specified ordinal field.
     * If this record already has the given ordinal field name, then it is replaced.
     * @param ordinal the ordinal field name of the field to insert
     * @param fieldValue Value to set the field to
     * @return The new record with the new field value
     */
    public RTRecordValue insertOrdinalField(int ordinal, RTValue fieldValue) {

        if(ordinal == 1) {
            if(hasOrdinalField(ordinal)) {
                return updateOrdinalField(ordinal, fieldValue);                
            } else {
                return RTRecordValue.makeRecordFromFieldData(mergeTupleFields(new RTValue[] {fieldValue}), getTextualData());
            }

        } else {
            if(hasOrdinalField(ordinal)) {
                return updateOrdinalField(ordinal, fieldValue);                
            } else {
                return RTRecordValue.makeRecordFromFieldData(mergeOrdinalFields(new int[] {ordinal}, new RTValue[] {fieldValue}), getTextualData());
            }
        }
    }
    
    /**
     * Return a new record containing this record's fields plus the specified textual field.
     * If this record already has a field named 'textualFieldName', then it is replaced.
     * @param textualFieldName name of the textual field to insert
     * @param fieldValue Value to set the field to
     * @return The new record with the new field value
     */
    public RTRecordValue insertTextualField(String textualFieldName, RTValue fieldValue) {

        if (hasTextualField(textualFieldName)) {
            return updateTextualField(textualFieldName, fieldValue);
        } 
        
        return RTRecordValue.makeRecordFromFieldData(getOrdinalData(), mergeTextualFields(new String[] {textualFieldName}, new RTValue[] {fieldValue}));
    }
    
    /**
     * Return a new record containing this record's fields plus those of another record.
     * Any fields that both records contain will be set to the value in this record rather
     * than those of the other record (i.e., this record "wins").
     * @param otherRecord The other record to copy fields from
     * @return The new record containing the fields of both source records
     */
    abstract public RTRecordValue appendRecord(RTRecordValue otherRecord);
   
    /** {@inheritDoc} */
    public final RecordType appendRecordType(RecordType otherRecordType) {
        return appendRecord((RTRecordValue)otherRecordType);
    }
    
    /** {@inheritDoc} */
    public final RecordType insertRecordTypeField(String fieldName, Object type) {
        if(FieldName.Ordinal.isValidCalSourceForm(fieldName)) {
            return insertOrdinalField(fieldOrdinal(fieldName), (RTValue)type);
        
        } else {
            return insertTextualField(fieldName, (RTValue)type);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public CalValue getNthFieldValue(int n) {
        return getNthValue(n);
    }
   
    /**
     * {@inheritDoc}
     */
    abstract public boolean sameFields(RecordType otherType);
    
    /**
     * Merge two OrdinalData objects, returning a new OrdinalData object containing the fields of both.
     * The two objects may contain overlapping field names; in such cases, values are takes from the
     * primaryOrdinalData object. 
     * @param primaryOrdinalData OrdinalData
     * @param otherOrdinalData OrdinalData
     * @return A new, merged OrdinalData object
     */
    private static OrdinalData overlappingMergeOrdinalData(OrdinalData primaryOrdinalData, OrdinalData otherOrdinalData) {
        if(!otherOrdinalData.hasOrdinalFields()) {
            return primaryOrdinalData;
        }
        
        if(!primaryOrdinalData.hasOrdinalFields()) {
            return otherOrdinalData;
        }
        
        final int nPrimaryFields = primaryOrdinalData.getNOrdinalNames();
        final int nOtherFields = otherOrdinalData.getNOrdinalNames();
        int nExtensionFields = nOtherFields;
        
        // Step through once to determine how many fields will be extended (skipping all fields that already
        // exist in the primary field data)
        int primaryIdx = 0;
        int otherIdx = 0;
        while (primaryIdx < nPrimaryFields && otherIdx < nOtherFields) {
            
            int primaryName = primaryOrdinalData.getNthOrdinalName(primaryIdx); 
            int otherName = otherOrdinalData.getNthOrdinalName(otherIdx);
            
            if(primaryName < otherName) {
                primaryIdx++;
            
            } else if(otherName < primaryName) {
                otherIdx++;
            
            } else {
                nExtensionFields--;
                primaryIdx++;
                otherIdx++;
            }
        }

        // No non-conflicting names to extend with, so return unextended data
        if(nExtensionFields == 0) {
            return primaryOrdinalData; 
        }
        
        // No conflicts, so extend with all the provided data
        if(nExtensionFields == nOtherFields) {
            if(otherOrdinalData.isTuple()) {
                return mergeOrdinalWithTuple(primaryOrdinalData.getOrdinalNames(), primaryOrdinalData.getOrdinalValues(), otherOrdinalData.getOrdinalValues());
            
            } else if(primaryOrdinalData.isTuple()) {
                return mergeTupleWithOrdinal(primaryOrdinalData.getOrdinalValues(), otherOrdinalData.getOrdinalNames(), otherOrdinalData.getOrdinalValues());
            
            } else {
                return mergeOrdinalWithOrdinal(primaryOrdinalData.getOrdinalNames(), primaryOrdinalData.getOrdinalValues(), otherOrdinalData.getOrdinalNames(), otherOrdinalData.getOrdinalValues());
            }
        }

        // Step through again setting up the data to extend with
        RTValue[] otherValues = otherOrdinalData.getOrdinalValues();
        int[] extensionNames = new int[nExtensionFields];
        RTValue[] extensionValues = new RTValue[nExtensionFields];
        
        int extensionIdx = 0;
        primaryIdx = 0;
        for (otherIdx = 0; otherIdx < nOtherFields; otherIdx++) {
            
            // Scan forward in the primary-names list until the next potential conflict
            while(primaryIdx < nPrimaryFields && 
                  primaryOrdinalData.getNthOrdinalName(primaryIdx) < otherOrdinalData.getNthOrdinalName(otherIdx)) {
                primaryIdx++;
            }
            
            // Copy non-conflicting names and values
            if(primaryIdx >= nPrimaryFields || 
               otherOrdinalData.getNthOrdinalName(otherIdx) != primaryOrdinalData.getNthOrdinalName(primaryIdx)) {
                
                extensionNames[extensionIdx] = otherOrdinalData.getNthOrdinalName(otherIdx);
                extensionValues[extensionIdx] = otherValues[otherIdx];
                extensionIdx++;
            }
        }
        
        if (primaryOrdinalData.isTuple()) {
            return mergeTupleWithOrdinal(primaryOrdinalData.getOrdinalValues(), extensionNames, extensionValues);
        } else if(extensionNames[extensionNames.length - 1] == extensionNames.length) {
            return mergeOrdinalWithTuple(primaryOrdinalData.getOrdinalNames(), primaryOrdinalData.getOrdinalValues(), extensionValues);
        } else {
            return mergeOrdinalWithOrdinal(primaryOrdinalData.getOrdinalNames(), primaryOrdinalData.getOrdinalValues(), extensionNames, extensionValues);
        }
    }
    
    /**
     * Merge two TextualData objects, returning a new TextualData object containing the fields of both.
     * The two objects may contain overlapping field names; in such cases, values are takes from the
     * primaryTextualData object. 
     * @param primaryTextualData TextualData
     * @param otherTextualData TextualData
     * @return A new, merged TextualData object
     */
    private static TextualData overlappingMergeTextualData(TextualData primaryTextualData, TextualData otherTextualData) {

        if(!otherTextualData.hasTextualFields()) {
            return primaryTextualData;
        }
        
        if(!primaryTextualData.hasTextualFields()) {
            return otherTextualData;
        }
        
        int nExtensionFields = otherTextualData.getTextualNames().length;
        
        String[] primaryNames = primaryTextualData.getTextualNames();
        String[] otherNames = otherTextualData.getTextualNames();
        
        // Step through once to determine how many fields will be extended (skipping all fields that already
        // exist in the primary field data)
        int primaryIdx = 0;
        int otherIdx = 0;
        while(primaryIdx < primaryNames.length && otherIdx < otherNames.length) {
            
            int comparison = primaryNames[primaryIdx].compareTo(otherNames[otherIdx]);
            
            if(comparison < 0) {
                primaryIdx++;
            
            } else if(comparison > 0) {
                otherIdx++;
            
            } else {
                nExtensionFields--;
                primaryIdx++;
                otherIdx++;
            }
        }

        // No non-conflicting names to extend with, so return unextended data
        if(nExtensionFields == 0) {
            return primaryTextualData; 
        }
        
        // No conflicts, so extend with all the provided data
        if(nExtensionFields == otherNames.length) {
            return mergeTextualWithTextual(primaryNames, primaryTextualData.getTextualValues(), otherNames, otherTextualData.getTextualValues());
        }

        // Step through again setting up the data to extend with
        RTValue[] otherValues = otherTextualData.getTextualValues();
        String[] extensionNames = new String[nExtensionFields];
        RTValue[] extensionValues = new RTValue[nExtensionFields];
        
        int extensionIdx = 0;
        primaryIdx = 0;
        for(otherIdx = 0; otherIdx < otherNames.length; otherIdx++) {
            
            // Scan forward in the primary-names list until the next potential conflict
            while(primaryIdx < primaryNames.length) {
                
                if(primaryNames[primaryIdx].compareTo(otherNames[otherIdx]) >= 0) {
                    break;
                }
                
                primaryIdx++;
            }
            
            // Copy non-conflicting names and values
            if(primaryIdx >= primaryNames.length || !otherNames[otherIdx].equals(primaryNames[primaryIdx])) {
                extensionNames[extensionIdx] = otherNames[otherIdx];
                extensionValues[extensionIdx] = otherValues[otherIdx];
                extensionIdx++;
            }
        }

        return mergeTextualWithTextual(primaryNames, primaryTextualData.getTextualValues(), extensionNames, extensionValues);
    }
    
    private static RTRecordValue makeRecordFromFieldData(OrdinalData ordinalData, TextualData textualData) {         
        
        if (ordinalData.hasOrdinalFields()) {
            
            if (textualData.hasTextualFields()) {
                
                if (ordinalData.isTuple()) {                    
                    return new TupleMixedRecord(ordinalData.getOrdinalValues(), textualData.textualNames, textualData.textualValues);                
                } 
                
                return new MixedRecord(ordinalData.getOrdinalNames(), ordinalData.getOrdinalValues(), textualData.textualNames, textualData.textualValues);               
            }
            
            if (ordinalData.isTuple()) {
                return new TupleRecord(ordinalData.getOrdinalValues());
            }
            
            return new OrdinalRecord(ordinalData.getOrdinalNames(), ordinalData.getOrdinalValues());            
        } 
        
        if (textualData.hasTextualFields()) {
            return new TextualRecord(textualData.textualNames, textualData.textualValues);
        }
            
        return EMPTY_RECORD;       
    }    

    /**
     * @param fieldName String an ordinal field name
     * @return int The ordinal of the field name as an int
     */
    private static int fieldOrdinal(String fieldName) {
        return Integer.parseInt(fieldName.substring(1));
    }
    
    abstract OrdinalData getOrdinalData();
    abstract TextualData getTextualData();
    
    abstract OrdinalData retractTupleFields(int retractedTupleSize);
    abstract OrdinalData retractOrdinalFields(int[] retractedOrdinalNames);
    abstract TextualData retractTextualFields(String[] retractedTextualNames);
      
    abstract OrdinalData mergeTupleFields(RTValue[] extensionOrdinalValues);
    abstract OrdinalData mergeOrdinalFields(int[] extensionOrdinalNames, RTValue[] extensionOrdinalValues);  
    abstract TextualData mergeTextualFields(String[] extensionTextualNames, RTValue[] extensionTextualValues);
    
    private static OrdinalData retractFromOrdinalUsingTuple(int[] ordinalNames, RTValue[] ordinalValues, int retractedTupleSize) {
                     
        //ordinalNames must be of the form
        //[1, 2, 3, ..., retractedTupleSize, m1, m2, ..., mk]       
        //so we just lop off the first retractedTupleSize elements from ordinalNames and ordinalValues to get the retraction.
        
        int nNewOrdinalFields = ordinalNames.length - retractedTupleSize;
        int[] newOrdinalNames = new int[nNewOrdinalFields];
        RTValue[] newOrdinalValues = new RTValue[nNewOrdinalFields];
        
        System.arraycopy(ordinalNames, retractedTupleSize, newOrdinalNames, 0, nNewOrdinalFields);
        System.arraycopy(ordinalValues, retractedTupleSize, newOrdinalValues, 0, nNewOrdinalFields);
        
        return new OrdinalData.Ordinal(newOrdinalNames, newOrdinalValues);
    }
    
    private static OrdinalData retractFromOrdinalUsingOrdinal(int[] ordinalNames, RTValue[] ordinalValues, int[] retractedOrdinalNames) {
        
        int nRetractedFields = retractedOrdinalNames.length;
        int nNewOrdinalFields = ordinalNames.length - retractedOrdinalNames.length;
        if (nNewOrdinalFields == 0) {
            //retraction results in an empty ordinal part
            return OrdinalData.EMPTY;
        }
        
        //if ordinalName = [1, 2, 3, 4, 7, 8] and retractedOrdinalNames = [7, 8] then the result is a tuple.
        //we can efficiently check this beforehand and avoid allocating newOrdinalNames. 
        if (ordinalNames[nNewOrdinalFields - 1] == nNewOrdinalFields && retractedOrdinalNames[0] > nNewOrdinalFields) {
            
            RTValue[] newOrdinalValues = new RTValue[nNewOrdinalFields];            
            System.arraycopy(ordinalValues, 0, newOrdinalValues, 0, nNewOrdinalFields);
            
            return new OrdinalData.Tuple(newOrdinalValues);             
        }
        
        int[] newOrdinalNames = new int[nNewOrdinalFields];
        RTValue[] newOrdinalValues = new RTValue[nNewOrdinalFields];
        
        int ordinalIndex = 0;
        int retractedOrdinalIndex = 0;
        for (int i = 0; i < nNewOrdinalFields; ++i) {
            
            while(retractedOrdinalIndex < nRetractedFields &&
                  ordinalNames[ordinalIndex] == retractedOrdinalNames[retractedOrdinalIndex]) {
                ++ordinalIndex;
                ++retractedOrdinalIndex;
            }
            
            newOrdinalNames[i] = ordinalNames[ordinalIndex];
            newOrdinalValues[i] = ordinalValues[ordinalIndex];
            
            ++ordinalIndex;            
        }            
        
        return new OrdinalData.Ordinal(newOrdinalNames, newOrdinalValues);        
    }
    
    private static OrdinalData retractFromTupleUsingOrdinal(RTValue[] ordinalValues, int[] retractedOrdinalNames) {
        
        int nRetractedFields = retractedOrdinalNames.length;
        int nNewOrdinalFields = ordinalValues.length - nRetractedFields;
        //nNewOrdinalFields will be > 0 because retractedOrdinalNames do not form a tuple
        
        //if ordinalName = [1, 2, 3, 4, 5, 6, 7, 8] and retractedOrdinalNames = [7, 8] then the result is a tuple.
        //we can efficiently check this beforehand and avoid allocating newOrdinalNames.        
        if (retractedOrdinalNames[0] == nNewOrdinalFields + 1) {
                       
            RTValue[] newOrdinalValues = new RTValue[nNewOrdinalFields];            
            System.arraycopy(ordinalValues, 0, newOrdinalValues, 0, nNewOrdinalFields);
            
            return new OrdinalData.Tuple(newOrdinalValues);                     
        }
        
        int[] newOrdinalNames = new int[nNewOrdinalFields];
        RTValue[] newOrdinalValues = new RTValue[nNewOrdinalFields];
        
        int ordinalIndex = 0;
        int retractedOrdinalIndex = 0;
        for (int i = 0; i < nNewOrdinalFields; ++i) {
            
            while(retractedOrdinalIndex < nRetractedFields &&
                  ordinalIndex + 1 == retractedOrdinalNames[retractedOrdinalIndex]) {
                ++ordinalIndex;
                ++retractedOrdinalIndex;
            }
            
            newOrdinalNames[i] = ordinalIndex + 1;
            newOrdinalValues[i] = ordinalValues[ordinalIndex];
            
            ++ordinalIndex;            
        }
               
        return new OrdinalData.Ordinal(newOrdinalNames, newOrdinalValues);
    }
    
    private static OrdinalData retractFromTupleUsingTuple(RTValue[] ordinalValues, int retractedTupleSize) {
        
        //ordinalNames must be [1, 2, 3, ..., ordinalValue.length] and 0 < retractedTupleSize < ordinalValues.length        
        int nNewOrdinalFields = ordinalValues.length - retractedTupleSize;
        if(nNewOrdinalFields == 0) {
            return OrdinalData.EMPTY; 
        }
        
        int[] newOrdinalNames = new int[nNewOrdinalFields];
        RTValue[] newOrdinalValues = new RTValue[nNewOrdinalFields];
        
        for (int i = 0; i < nNewOrdinalFields; ++i) {
            newOrdinalNames[i] = retractedTupleSize + i + 1;
        }
        System.arraycopy(ordinalValues, retractedTupleSize, newOrdinalValues, 0, nNewOrdinalFields);
        
        return new OrdinalData.Ordinal(newOrdinalNames, newOrdinalValues);
    }
    
    private static TextualData retractFromTextualUsingTextual(String[] textualNames, RTValue[] textualValues, String[] retractedTextualNames) {
               
        int nRetractedFields = retractedTextualNames.length;
        int nNewTextualFields = textualNames.length - nRetractedFields;
        if (nNewTextualFields == 0) {
            //retraction results in an empty textual part
            return TextualData.EMPTY;
        }
               
        String[] newTextualNames = new String[nNewTextualFields];
        RTValue[] newTextualValues = new RTValue[nNewTextualFields];
        
        int textualIndex = 0;
        int retractedTextualIndex = 0;
        for (int i = 0; i < nNewTextualFields; ++i) {
            
            while(retractedTextualIndex < nRetractedFields &&
                  textualNames[textualIndex].equals(retractedTextualNames[retractedTextualIndex])) {
                ++textualIndex;
                ++retractedTextualIndex;
            }
            
            newTextualNames[i] = textualNames[textualIndex];
            newTextualValues[i] = textualValues[textualIndex];
            
            ++textualIndex;            
        }            
        
        return new TextualData(newTextualNames, newTextualValues);         
    }
    
    private static OrdinalData mergeOrdinalWithTuple(int[] ordinalNames, RTValue[] ordinalValues, RTValue[] extensionOrdinalValues) {
        return RTRecordValue.mergeTupleWithOrdinal(extensionOrdinalValues, ordinalNames, ordinalValues);
    }
        
    private static OrdinalData mergeTupleWithOrdinal(RTValue[] ordinalValues, int[] extensionOrdinalNames, RTValue[] extensionOrdinalValues) {
       
        assert (ordinalValues.length > 0 && extensionOrdinalValues.length > 0);        
         
        final int nOrdinalFields = ordinalValues.length;
        final int nExtensionFields = extensionOrdinalValues.length;
        //ordinalNames are [1, 2, 3, ..., nOrdinalFields]
        //so extensionOrdinalNames must all be > nOrdinalFields        
        //in particular,
        //newOrdinalNames = ordinalNames ++ extensionOrdinalNames
        //newOrdinalValues = ordinalValues ++ extensionOrdinalValues
        
        final int nNewOrdinalFields = nOrdinalFields + nExtensionFields;
        RTValue[] newOrdinalValues = new RTValue[nNewOrdinalFields];
        System.arraycopy(ordinalValues, 0, newOrdinalValues, 0, nOrdinalFields);
        System.arraycopy(extensionOrdinalValues, 0, newOrdinalValues, nOrdinalFields, nExtensionFields);
        
        if (extensionOrdinalNames[nExtensionFields - 1] == nNewOrdinalFields) {
            //in this case,
            //extensionOrdinalNames = [1, 2, 3, ..., nNewOrdinalFields]
            //i.e. the extension has a tuple ordinal part.
            return new OrdinalData.Tuple(newOrdinalValues);          
        }
        
        
        int[] newOrdinalNames = new int[nNewOrdinalFields];
        for (int i = 0; i < nOrdinalFields; ++i) {
            newOrdinalNames[i] = i + 1;
        }
        System.arraycopy(extensionOrdinalNames, 0, newOrdinalNames, nOrdinalFields, nExtensionFields);
              
        return new OrdinalData.Ordinal(newOrdinalNames, newOrdinalValues);                     
    }
    
    private static OrdinalData mergeOrdinalWithOrdinal(int[] ordinalNames, RTValue[] ordinalValues, int[] extensionOrdinalNames, RTValue[] extensionOrdinalValues) {
              
        final int nOrdinalFields = ordinalNames.length;             
        final int nExtensionFields = extensionOrdinalNames.length;
        assert (nOrdinalFields > 0 && nExtensionFields > 0);  
                
        final int nNewOrdinalFields = nOrdinalFields + nExtensionFields;

        
        //handle the situation where after the extension, we have a tuple.
        //For example if ordinalNames = [1, 3, 5] and extensionOrdinalNames = [2, 4]. Then after the extension it is
        //[1, 2, 3, 4, 5]
        if (Math.max(ordinalNames[nOrdinalFields -1], extensionOrdinalNames[nExtensionFields - 1]) == nNewOrdinalFields) {
                                   
            RTValue[] newOrdinalValues = new RTValue[nNewOrdinalFields];
            int thisIndex = 0;
            int extensionIndex = 0; 
            
            for (int i = 0; i < nNewOrdinalFields; ++i) {

                if (thisIndex < nOrdinalFields) {

                    if (extensionIndex < nExtensionFields) {

                        if (ordinalNames[thisIndex] < extensionOrdinalNames[extensionIndex]) {

                            newOrdinalValues[i] = ordinalValues[thisIndex];
                            ++thisIndex;

                        } else {

                            newOrdinalValues[i] = extensionOrdinalValues[extensionIndex];
                            ++extensionIndex;
                        }
                    } else {
                        //remaining items are from (ordinalNames, ordinalValues) starting at thisIndex
                        final int nRemaining = nNewOrdinalFields - i;                       
                        System.arraycopy(ordinalValues, thisIndex, newOrdinalValues, i, nRemaining);
                        break;
                    }

                } else {
                    //remaining items are from (extensionOrdinalNames, extensionOrdinalValues) starting at extensionIndex
                    final int nRemaining = nNewOrdinalFields - i;               
                    System.arraycopy(extensionOrdinalValues, extensionIndex, newOrdinalValues, i, nRemaining);
                    break;
                }
            }
            
            return new OrdinalData.Tuple(newOrdinalValues);                                         
        }
                            
        final int[] newOrdinalNames = new int[nNewOrdinalFields];
        final RTValue[] newOrdinalValues = new RTValue[nNewOrdinalFields];
        int thisIndex = 0;
        int extensionIndex = 0; 

        for (int i = 0; i < nNewOrdinalFields; ++i) {
            
            if (thisIndex < nOrdinalFields) {
                
                if (extensionIndex < nExtensionFields) {        
                                             
                    int thisName = ordinalNames[thisIndex];
                    int extensionName = extensionOrdinalNames[extensionIndex];
                    
                    if (thisName < extensionName) {
             
                        newOrdinalNames[i] = thisName;
                        newOrdinalValues[i] = ordinalValues[thisIndex];
                        ++thisIndex;
                      
                    } else {
                        
                        newOrdinalNames[i] = extensionName;
                        newOrdinalValues[i] = extensionOrdinalValues[extensionIndex];
                        ++extensionIndex;
                    }
                   
                } else {                    
                    //remaining items are from (ordinalNames, ordinalValues) starting at thisIndex
                    final int nRemaining = nNewOrdinalFields - i;
                    System.arraycopy(ordinalNames, thisIndex, newOrdinalNames, i, nRemaining);
                    System.arraycopy(ordinalValues, thisIndex, newOrdinalValues, i, nRemaining);
                    break;
                }
                
            } else {
                //remaining items are from (extensionOrdinalNames, extensionOrdinalValues) starting at extensionIndex
                final int nRemaining = nNewOrdinalFields - i;
                System.arraycopy(extensionOrdinalNames, extensionIndex, newOrdinalNames, i, nRemaining);
                System.arraycopy(extensionOrdinalValues, extensionIndex, newOrdinalValues, i, nRemaining);
                break; 
            }        
        }
        
        return new OrdinalData.Ordinal(newOrdinalNames, newOrdinalValues);         
    }           

    private static TextualData mergeTextualWithTextual(String[] textualNames, RTValue[] textualValues, String[] extensionTextualNames, RTValue[] extensionTextualValues) {
        
        final int nTextualFields = textualNames.length;
        final int nExtensionFields = extensionTextualNames.length;
        
        assert (nTextualFields > 0 && nExtensionFields > 0);
                
        final int nNewTextualFields = nTextualFields + nExtensionFields;
        String[] newTextualNames = new String[nNewTextualFields];
        RTValue[] newTextualValues = new RTValue[nNewTextualFields];
        int thisIndex = 0;
        int extensionIndex = 0; 
        
        for (int i = 0; i < nNewTextualFields; ++i) {
            
            if (thisIndex < nTextualFields) {
                
                if (extensionIndex < nExtensionFields) {
                                                      
                    String thisName = textualNames[thisIndex];
                    String extensionName = extensionTextualNames[extensionIndex];
                    
                    if (thisName.compareTo(extensionName) < 0) {
             
                        newTextualNames[i] = thisName;
                        newTextualValues[i] = textualValues[thisIndex];
                        ++thisIndex;
                      
                    } else {
                        
                        newTextualNames[i] = extensionName;
                        newTextualValues[i] = extensionTextualValues[extensionIndex];
                        ++extensionIndex;
                    } 
                } else {                    
                    //remaining items are from (textualNames, textualValues) starting at thisIndex
                    final int nRemaining = nNewTextualFields - i;
                    System.arraycopy(textualNames, thisIndex, newTextualNames, i, nRemaining);
                    System.arraycopy(textualValues, thisIndex, newTextualValues, i, nRemaining);
                    break;
                }
            } else {
                //remaining items are from (extensionTextualNames, extensionTextualValues) starting at extensionIndex
                final int nRemaining = nNewTextualFields - i;
                System.arraycopy(extensionTextualNames, extensionIndex, newTextualNames, i, nRemaining);
                System.arraycopy(extensionTextualValues, extensionIndex, newTextualValues, i, nRemaining);
                break; 
            }
        }
        
        return new TextualData(newTextualNames, newTextualValues);        
    }
    
    /**
     * A helper function that does an inexpensive consistency check for the (ordinalNames, ordinalValues) pair
     * to ensure that we are instantiating the right RTRecordValue derived class. This is important for efficiency
     * reasons since using the wrong class can in certain cases only be detected by a slowdown in runtime performance
     * which might be hard to notice without systematic benchmarking. 
     * 
     * Terminates in a runtime exception unless:
     * -ordinalNames and ordinalValues are both non-null and have the same length which is > 0.
     * -ordinalNames is not of the form [1, 2, 3, ..., ordinalNames.length - 1] i.e. should be using
     *  a Tuple class where ordinalNames is elided. This is done via an inexpensive check on the last element value.  
     * -ordinalNames and ordinalValues are both non-null
     * @param ordinalNames names of ordinal fields, in ascending ordinal order.
     * @param ordinalValues
     * @return true if the ordinal data is valid, false otherwise.
     */
    static boolean verifyOrdinalData (int[] ordinalNames, RTValue[] ordinalValues) {
        
        final int nOrdinalFields = ordinalNames.length;
        
        if (nOrdinalFields == 0 ||
            nOrdinalFields != ordinalValues.length ||
            ordinalNames[nOrdinalFields - 1] == nOrdinalFields) {  
            
            return false;
        }    
        return true;
    }
    
    /**
     * A helper function that does an inexpensive consistency check for the ordinalValues data for a tuple
     * to ensure that we are instantiating the right RTRecordValue derived class. This is important for efficiency
     * reasons since using the wrong class can in certain cases only be detected by a slowdown in runtime performance
     * which might be hard to notice without systematic benchmarking. 
     * 
     * Terminates in a runtime exception unless:
     * -ordinalValues is non-null and has length > 0.    
     * @param ordinalValues
     * @return true if the tuple data is valid, false otherwise.
     */    
    static boolean  verifyTupleData (RTValue[] ordinalValues) {
        
        if (ordinalValues.length == 0) {
            return false;
        }              
        return true;
    }    
    
    /**
     * A helper function that does an inexpensive consistency check for the (textualNames, textualValues) pair
     * to ensure that we are instantiating the right RTRecordValue derived class.
     * 
     * @param textualNames
     * @param textualValues
     * @return true if the textual data is valid, false otherwise
     */
    static boolean verifyTextualData (String[] textualNames, RTValue[] textualValues) {
        
        final int nTextualFields = textualNames.length;
        
        if (nTextualFields == 0 ||
            nTextualFields != textualValues.length) {
            
            return false;
        }        
        return true;
    }         

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.RTValue#reduce(org.openquark.cal.internal.runtime.lecc.RTExecutionContext)
     */
    @Override
    protected final RTValue reduce(RTExecutionContext ec) {
        return this;
    }
    
    /**
     * {@inheritDoc} 
     * Return an application of deepSeq to subParts.
     * @param deepSeq
     * @param rhs
     * @return the graph representing the result of the deepSeq application. 
     * @throws CALExecutorException      
     */
    @Override
    public final RTValue buildDeepSeq(RTSupercombinator deepSeq, RTValue rhs) throws CALExecutorException {
        RTValue result = this;
        for (int i = getNFields() - 1; i >= 0; --i) {
            result = deepSeq.apply(getNthValue(i)).apply(rhs);
            rhs = result;
        }
        
        return result;
    }
    
    /**        
     * {@inheritDoc}
     */
    @Override
    public final int debug_getNChildren() {         
        return getNFields();
    }
    /** 
     * We can't just call getNthValue() since getNthValue has the side effect of
     * attempting to shorten indirection chains and the debug_ methods cannot
     * have any side effects.     
     * {@inheritDoc}
     */
    @Override
    public abstract CalValue debug_getChild(int childN);     
   
    /**     
     * {@inheritDoc}
     */
    @Override
    public String debug_getNodeStartText() {
        return "{";
    }
    /**    
     * {@inheritDoc}
     */
    @Override
    public String debug_getNodeEndText() {       
        return "}";
    }
    /**  
     * {@inheritDoc}
     */
    @Override
    public String debug_getChildPrefixText(int childN) {  
        
        StringBuilder sb = new StringBuilder();
        if (childN > 0) {
            sb.append(", ");
        }
        sb.append(getNthFieldName(childN));
        sb.append(" = ");
        
        return sb.toString();          
    }

    /**     
     * @param ordinalValues must have length > 0.
     * @return RTRecordValue a copy of this RTRecordValue with the fields with ordinal field names #1, #2, ...,#(ordinalValues.length)
     *    updated by their corresponding values in the ordinalValues array.
     */
    public final RTRecordValue makeTupleRecordUpdate(RTValue[] ordinalValues) {
      
        RTRecordValue updatedRecord = updateOrdinalField(1, ordinalValues[0]);
        for (int i = 1, nValues = ordinalValues.length; i < nValues; ++i) {
            updatedRecord.mutateOrdinalField(i + 1, ordinalValues[i]);
        }
        
        return updatedRecord;        
    }

    /**    
     * @param ordinalNames must have length > 0 and equal to the length of ordinalValues.
     * @param ordinalValues
     * @return RTRecordValue a copy of this RTRecordValue with the the ordinal fields with the given ordinalNames updated
     *    with their corresponding ordinalValues
     */
    public final RTRecordValue makeOrdinalRecordUpdate(int[] ordinalNames, RTValue[] ordinalValues) {
        
        RTRecordValue updatedRecord = updateOrdinalField(ordinalNames[0], ordinalValues[0]);
        for (int i = 1, nValues = ordinalValues.length; i < nValues; ++i) {
            updatedRecord.mutateOrdinalField(ordinalNames[i], ordinalValues[i]);
        }
        
        return updatedRecord;
    }

    /**     
     * @param textualNames must have length > 0 and equal to the length of textualValues.
     * @param textualValues
     * @return a copy of this RTRecordValue with the the textual fields with the given textualNames updated
     *    with their corresponding textualValues
     */
    public final RTRecordValue makeTextualRecordUpdate(String[] textualNames, RTValue[] textualValues) {
        
        RTRecordValue updatedRecord = updateTextualField(textualNames[0], textualValues[0]);
        for (int i = 1, nValues = textualValues.length; i < nValues; ++i) {
            updatedRecord.mutateTextualField(textualNames[i], textualValues[i]);
        }
        
        return updatedRecord;
    }

    /**     
     * @param ordinalValues must have length > 0.
     * @param textualNames must have length > 0 and equal to the length of textualValues.
     * @param textualValues
     * @return a copy of this RTRecordValue with the the textual fields with the given textualNames updated
     *    with their corresponding textualValues and the fields with ordinal field names #1, #2, ...,#(ordinalValues.length)
     *    updated by their corresponding values in the ordinalValues array.
     */
    public final RTRecordValue makeTupleMixedRecordUpdate(RTValue[] ordinalValues, String[] textualNames, RTValue[] textualValues) {
        
        RTRecordValue updatedRecord = updateMixedOrdinalField(1, ordinalValues[0]);
        for (int i = 1, nValues = ordinalValues.length; i < nValues; ++i) {
            updatedRecord.mutateOrdinalField(i + 1, ordinalValues[i]);
        }
        
        for (int i = 0, nValues = textualValues.length; i < nValues; ++i) {
            updatedRecord.mutateTextualField(textualNames[i], textualValues[i]);
        }
        
        return updatedRecord;  
    }

    /**    
     * @param ordinalNames must have length > 0 and equal to the length of ordinalValues.
     * @param ordinalValues
     * @param textualNames must have length > 0 and equal to the length of textualValues.
     * @param textualValues
     * @return a copy of this RTRecordValue with the the ordinal fields with the given ordinalNames updated
     *    with their corresponding ordinalValues and the textual fields with the given textualNames updated
     *    with their corresponding textualValues. 
     */
    public final RTRecordValue makeMixedRecordUpdate(int[] ordinalNames, RTValue[] ordinalValues, String[] textualNames, RTValue[] textualValues) {
        
        RTRecordValue updatedRecord = updateMixedOrdinalField(ordinalNames[0], ordinalValues[0]);
        for (int i = 1, nValues = ordinalValues.length; i < nValues; ++i) {
            updatedRecord.mutateOrdinalField(ordinalNames[i], ordinalValues[i]);        
        }
        
        for (int i = 0, nValues = textualValues.length; i < nValues; ++i) {
            updatedRecord.mutateTextualField(textualNames[i], textualValues[i]);
        }
        
        return updatedRecord;               
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final DataType getDataType() {
        return DataType.OTHER;
    }
}

