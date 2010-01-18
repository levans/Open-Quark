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
 * RecordType.java
 * Creation date: (Oct 26, 2005)
 * By: James Wright
 */
package org.openquark.cal.internal.runtime;

import org.openquark.cal.compiler.FieldName;
import org.openquark.cal.runtime.CalValue;


/**
 * Interface implemented by machine-dependent record dictionary implementations.
 * WARNING: This interface is for internal use only!
 *  
 * @author James Wright
 */
public interface RecordType {

    /**
     * @return The number of fields that this record has 
     */
    public int getNFields();
    
    /**
     * @param n The 0-based index of the field to retrieve the value from
     * @return CalValue containing the value of the nth field.  For a record
     *          dictionary, this will return a CAL function that accepts a single
     *          ignored argument, and that returns the TypeRep for the corresponding field.
     */
    public CalValue getNthFieldValue(int n);
    
    /**
     * @param n The 0-based index of the field to retrieve the name of
     * @return FieldName name of the nth field.
     */
    public FieldName getNthFieldName(int n);
    
    /**     
     * @return boolean. True if the record has the fields #1, ..., #n, with no gaps, n >= 2
     *   and there are no other fields.
     */
    public boolean isTuple2OrMoreRecord();
    
    /**
     * @param otherRecordType RecordType of another record to check
     * @return true if the other record has precisely the same field names as this one. 
     */
    public boolean sameFields(RecordType otherRecordType);
    
    /**
     * @param otherRecordType RecordType of another record to include fields from
     * @return RecordType containing the union of the fields of this recordType and otherRecordType.
     *          When both RecordTypes contain a field, the type from this RecordType will be used.
     */
    public RecordType appendRecordType(RecordType otherRecordType);
    
    /**
     * @param fieldName String representation of a field to add or update to this RecordType
     * @param fieldType This should be a native CAL value representing a function that accepts one 
     *                   returns one dummy argument and returns a TypeRep 
     * @return A new RecordType that contains the new/updated field
     */
    public RecordType insertRecordTypeField(String fieldName, Object fieldType);
}
