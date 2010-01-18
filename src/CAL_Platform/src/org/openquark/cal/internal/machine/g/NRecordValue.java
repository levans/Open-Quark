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
 * NRecordValue.java
 * Created: Apr 12, 2004
 * By: RCypher
 */

package org.openquark.cal.internal.machine.g;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openquark.cal.compiler.FieldName;
import org.openquark.cal.internal.machine.g.functions.NDeepSeq;
import org.openquark.cal.internal.runtime.RecordType;
import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.runtime.CalValue;


/**
 * NRecordValue
 * This class is the graph node representing a record.
 * 
 * Note that NRecordValue is used in 2 ways:
 * a. as a concrete class to represent a record value (corresponding to RTRecordValue in lecc)
 * b. as a base for NRecordExtension
 * The lecc implementation separates out these 2 cases, and thus differs slightly from the g machine
 * implementation.
 * 
 * @author RCypher
 * Created: Apr 12, 2004
 */
public class NRecordValue extends NInd implements RecordType {

    /** String -> Node map. */
    final Map<String, Node> fieldToValueMap;
    
    /** 
     * used to order field names in CAL source form (as Strings) so that ordinal field names occur before textual field names and
     * ordinals are ordered numerically.
     */
    private static final Comparator<String> calSourceFormComparator = new FieldName.CalSourceFormComparator();
    
    /**
     * Create an empty record.
     *
     */
    NRecordValue () {
        fieldToValueMap = new HashMap<String, Node>();
    }

    /**
     * Create a new record with a pre-set size.
     * @param nFields number of fields in the record
     */    
    public NRecordValue (int nFields) {
        //Note: the HashMap documentation has some advice for setting the initialCapacity. 
        //Since we know the final size of the map, we can completely avoid rehashing.
        //the default load factor of HashMap is 0.75. If the initial capacity is greater than the maximum number 
        //of entries divided by the load factor, no rehash operations will ever occur. 
        int initialCapacity = (int)(nFields / 0.75 + 1);        
        fieldToValueMap = new HashMap<String, Node>(initialCapacity);
    }
    
    /**
     * Create a new record that is a copy of an existing record.
     * @param baseRecord
     */
    NRecordValue (NRecordValue baseRecord) {
        fieldToValueMap = new HashMap<String, Node> (baseRecord.fieldToValueMap);
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
        
        return fieldToValueMap.size();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public CalValue debug_getChild(int childN) {
        if (hasIndirection()) {
            return super.debug_getChild(childN);           
        }
        
        return getNthFieldValue(childN);                  
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String debug_getNodeStartText() {
        if (hasIndirection()) {
            return super.debug_getNodeStartText();           
        }
        
        if (isTuple2OrMoreRecord()) {
            return "(";
        }
                
        return "{";
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String debug_getNodeEndText() {
        if (hasIndirection()) {
            return super.debug_getNodeEndText();           
        } 
        
        if (isTuple2OrMoreRecord()) {
            return ")";
        }
                
        return "}";
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String debug_getChildPrefixText(int childN) {
        if (hasIndirection()) {
            return super.debug_getChildPrefixText(childN);           
        }
                        
        if (isTuple2OrMoreRecord()) {           
            if (childN < 0 || childN >= fieldToValueMap.size()) {
                throw new IndexOutOfBoundsException();
            }
            
            if (childN == 0) {
                return "";
            }
            
            return ", ";            
        }        
        
        String fieldName = getNthFieldName(childN).getCalSourceForm();
        
        StringBuilder sb = new StringBuilder();
        
        if (childN > 0) {
            sb.append(", ");
        }
              
        sb.append(fieldName);
        sb.append(" = ");
        return sb.toString();
    }        
    
    
    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.g.Node#addChildren(java.util.Collection)
     */
    @Override
    protected void addChildren(Collection<Node> c) {
        Iterator<String> it = fieldToValueMap.keySet().iterator();
        while (it.hasNext ()) {
            Node n = fieldToValueMap.get(it.next());
            if (!c.contains(n)) {
                c.add (n);
                n.addChildren(c);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.g.Node#i_eval(org.openquark.cal.internal.runtime.g.Executor)
     */
    @Override
    protected void i_eval(Executor e) throws CALExecutorException {
        // Do nothing, a record value is already in WHNF.
    }

    /**
     * Do Unwind state transition.
     */
    @Override
    protected void i_unwind (Executor e) throws CALExecutorException {
        e.popDumpItem();
    }

    /**
     * Add a field value to this record.
     * @param fieldName
     * @param n
     */
    public void putValue (String fieldName, Node n) {
        fieldToValueMap.put (fieldName, n.getLeafNode());
    }
    
    /**
     * Remove a field value from this record.
     * @param fieldName
     */
    void removeValue (String fieldName) {
        fieldToValueMap.remove(fieldName);
    }
    
    /**
     * Retrieve a field value from this record.
     * @param fieldName
     * @return - the graph node representing a field value.
     */
    public Node getValue (String fieldName) {
        Node n = fieldToValueMap.get(fieldName);
        if (n instanceof NInd) {
            n = n.getLeafNode();
            fieldToValueMap.put(fieldName, n);
        }
        return n;
    }
    
    /**
     * Returns the value of the value of the field with the specified index
     * @param fieldIndex int Index of field to retrieve the value for
     * @return Node the value of the specified field
     */
    public Node getNthValue (int fieldIndex) {
        String fieldName = getNthFieldName(fieldIndex).getCalSourceForm();
        Node n = fieldToValueMap.get(fieldName);
        if (n instanceof NInd) {
            n = n.getLeafNode();
            fieldToValueMap.put(fieldName, n);
        }
        
        return n;
    }
    
    /**
     * {@inheritDoc}
     */
    public CalValue getNthFieldValue(int fieldIndex) {
        return getNthValue(fieldIndex);
    }
    
    /**
     * {@inheritDoc}
     */
    public int getNFields() {
        return fieldToValueMap.size();
    }
    
    /**
     * {@inheritDoc}
     */
    public FieldName getNthFieldName(int fieldIndex) {
        return FieldName.make(fieldNames().get(fieldIndex));
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isTuple2OrMoreRecord() {
        
        if(getNFields() < 2) {
            return false;
        }
        
        List<String> fieldNames = fieldNames();
        int prevField = 0;
        for (final String fieldName : fieldNames) {
            
            if(!isOrdinalFieldName(fieldName)) {
                return false;
            }
            
            FieldName.Ordinal fieldOrdinal = (FieldName.Ordinal)FieldName.make(fieldName);
            if(fieldOrdinal.getOrdinal() != (prevField + 1)) {
                return false;
            }
            
            prevField = fieldOrdinal.getOrdinal();
        }
        
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean sameFields(RecordType otherRecordType) {
        
        if(!(otherRecordType instanceof NRecordValue)) {
            throw new IllegalArgumentException("otherRecordType must be an NRecordValue");
        }
        NRecordValue otherRecord = (NRecordValue)otherRecordType;
        
        if(otherRecordType.getNFields() != getNFields()) {
            return false;
        }
        
        List<String> ownFieldNames = fieldNames();
        List<String> otherFieldNames = otherRecord.fieldNames();

        Iterator<String> ownIt = ownFieldNames.iterator();
        Iterator<String> otherIt = otherFieldNames.iterator();
        while(ownIt.hasNext() && otherIt.hasNext()) {
            String ownName = ownIt.next();
            String otherName = otherIt.next();
            
            if(!ownName.equals(otherName)) {
                return false;
            }
        }

        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    public RecordType appendRecordType(RecordType otherRecordType) {
        
        if(!(otherRecordType instanceof NRecordValue)) {
            throw new IllegalArgumentException("otherRecordType must be an NRecordValue");
        }
        NRecordValue otherRecord = (NRecordValue)otherRecordType;
        
        return appendRecord(otherRecord);
    }
    
    /**
     * {@inheritDoc}
     */
    public RecordType insertRecordTypeField(String fieldName, Object fieldValue) {
        return insertRecordField(fieldName, (Node)fieldValue);
    }

    /**
     * @param fieldName Name of field to insert
     * @param fieldValue Value of field to insert
     * @return A new NRecordValue containing all the fields of this value, plus the 
     *          the field whose name and value are provided.  If this record already
     *          contains a field of the specified name, the new record will contain the
     *          specified field value, not the current field value.
     */
    public NRecordValue insertRecordField(String fieldName, Node fieldValue) {
        
        NRecordValue newRecordValue = new NRecordValue(this);
        newRecordValue.putValue(fieldName, fieldValue);
        return newRecordValue;
    }
    
    /**
     * @param fieldName The name of a field
     * @return True if the field is an ordinal field name, false otherwise
     */
    private static boolean isOrdinalFieldName(String fieldName) {
        return FieldName.Ordinal.isValidCalSourceForm(fieldName);
    }
    
    /**     
     * Used to implement the primitive Prelude.hasField function.
     * @param fieldName
     * @return boolean whether this record has a mapping for the given fieldName.
     */
    public boolean hasField(String fieldName) {
        return fieldToValueMap.containsKey(fieldName);
    }     
    
    /**     
     * Used to implement the primitive Prelude.fieldNamesPrimitive function.
     * Note this function makes a copy of the underlying keyset for safety reasons.     
     *      
     * @return List (of Strings) the sorted list of fieldnames. The sort order is as specified by
     *   the FieldName.CalSourceFormComparator comparator i.e. ordinal field names before textual field names.
     */
    public List<String> fieldNames() {
        List<String> fieldNames = new ArrayList<String>(fieldToValueMap.keySet());
        Collections.sort(fieldNames, NRecordValue.calSourceFormComparator);
        return fieldNames;
    }
    
    /**
     * Used to implement the primitive Prelude.recordFieldIndex function.
     * @param fieldName Name of the field to check
     * @return -1 if the record does not have the specified field, or the 0-based index
     *          of the field otherwise.
     */
    public int indexOfField(String fieldName) {
        return fieldNames().indexOf(fieldName);
    }
    
    /**     
     * Used to implement the primitive Prelude.fieldValuesPrimitive function.     
     *      
     * @return List (of Node) the list of field values, in the order determined by the field-names.
     *   The sort order on field names is as specified by
     *   the FieldName.CalSourceFormComparator comparator i.e. ordinal field names before textual field names.
     */
    public List<Node> fieldValues() {
        
        List<String> fieldNames = fieldNames();
        List<Node> fieldValues = new ArrayList<Node>(fieldNames.size());
        for (final String key : fieldNames) {
            Node n = fieldToValueMap.get(key);
            if (n instanceof NInd) {
                n = n.getLeafNode();
                fieldToValueMap.put(key, n);
            }
            fieldValues.add(n);
        }  
        
        return fieldValues;
    }    
    
    /**
     * @param baseRecord Node representing the base record for the extension
     * @return Node a new Node representing the extension of the specified base record.
     */
    public static NRecordValue makeExtension(Node baseRecord) {
        return new NRecordExtension(baseRecord);
    }
    
    /**
     * Returns a new record containing the fields of both this record and of otherRecord.
     * If the records have any overlapping field names, the fields will have the value of
     * the field in this record, not otherRecord.
     * @param otherRecord The record to append to this record
     * @return NRecordValue a new record containing the fields of both this record and of otherRecord
     */
    public NRecordValue appendRecord(NRecordValue otherRecord) {
        NRecordValue appended = new NRecordValue(fieldToValueMap.size() + otherRecord.fieldToValueMap.size());
        for (final String fieldName : fieldToValueMap.keySet()) {
            appended.putValue(fieldName, getValue(fieldName));
        }
        
        for (final String fieldName : otherRecord.fieldToValueMap.keySet()) {
            if(hasField(fieldName)) {
                continue;
            }
            appended.putValue(fieldName, otherRecord.getValue(fieldName));
        }
        
        return appended;
    }
    
    /**
     * Application of a record value is a special case which represents pointwise application of
     * the argument to each of the fields in the record.
     * @param n
     * @return the application of this node to the argument.
     */
    @Override
    public Node apply (Node n) {
        NRecordValue appliedRecord = new NRecordValue (this.fieldToValueMap.size());
        for (final String fieldName : fieldToValueMap.keySet()) {
            Node fieldValue = getValue(fieldName);
            appliedRecord.putValue(fieldName, fieldValue.apply(n));            
        }
        
        
        return appliedRecord;
    }
    
    /**
     * Build an application of deepSeq
     */
    @Override
    protected Node buildDeepSeqInternal(Node rhs) {
        List<String> fieldNames = fieldNames();
        for (int i = fieldNames.size()-1; i >= 0; --i) {
            Node fieldVal = getValue(fieldNames.get(i));
            rhs = NDeepSeq.instance.apply(fieldVal).apply(rhs);
        }
        return rhs;
    }

    @Override
    public Object getValue() {
        return this;
    }

}
