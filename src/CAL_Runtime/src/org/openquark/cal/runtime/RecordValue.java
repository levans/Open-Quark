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
 * RecordValue.java
 * Created: Jan 8, 2007
 * By: Bo Ilic
 */

package org.openquark.cal.runtime;

import java.util.SortedMap;

import org.openquark.cal.compiler.FieldName;


/**
 * Represents a CAL record value marshaled to Java in such a way that the field-name information
 * is available along with the field values.
 * 
 * @author Bo Ilic
 */
public interface RecordValue extends SortedMap<FieldName, Object> {
    
    /**     
     * @return the number of fields in the record (both textual and ordinal).
     */
    public int getNFields();
    
    /**     
     * @param fieldName
     * @return the value (possibly null) of the record at the corresponding fieldName. Returns null if the record
     *      does not have a field with the given name.
     */
    public Object getField(FieldName fieldName);
    
    /**    
     * @param fieldName
     * @return true if this record actually has a field of the given name
     */
    public boolean hasField(FieldName fieldName); 
    
    /**     
     * @param ordinalFieldName the ordinal field name as an int. For example, for field #3, this would be 3.
     * @return the value (possibly null) of the record at the corresponding ordinalFieldName. Returns null if the record
     *     does not have a field with the given name.
     */
    public Object getOrdinalField(int ordinalFieldName);
    
    /**     
     * @param ordinalFieldName the ordinal field name as an int. For example, for field #3, this would be 3.
     * @return true if this record actually has an ordinal field of the given name
     */
    public boolean hasOrdinalField(int ordinalFieldName);
    
    /**     
     * @param textualFieldName a textual field-name (i.e. cannot be an ordinal field name such as #2).
     * @return the value (possibly null) of the record at the corresponding textualFieldName (could be null). Returns null if the record
     *     does not have a field with the given name.
     */    
    public Object getTextualField(String textualFieldName);
    
    /**     
     * @param textualFieldName a textual field-name (i.e. cannot be an ordinal field name such as #2).
     * @return true if this record actually has a textual field of the given name.
     */
    public boolean hasTextualField(String textualFieldName);        
}
