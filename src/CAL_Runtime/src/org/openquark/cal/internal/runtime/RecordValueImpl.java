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
 * Created: Jan 11, 2007
 * By: Bo Ilic
 */

package org.openquark.cal.internal.runtime;

import java.util.TreeMap;

import org.openquark.cal.compiler.FieldName;
import org.openquark.cal.runtime.RecordValue;


/**
 * Represents a CAL record value marshaled to Java in such a way that the field-name information
 * is available along with the field values.
 *  
 * @author Bo Ilic
 */
public final class RecordValueImpl extends TreeMap<FieldName, Object> implements RecordValue {
  
    private static final long serialVersionUID = 2550692640227386560L;
    
    private RecordValueImpl() {
        super();
    }
    
    public static RecordValueImpl make() {
        return new RecordValueImpl();
    }

    /** {@inheritDoc}*/
    public Object getField(FieldName fieldName) {        
        return get(fieldName);
    }
    
    /** {@inheritDoc}*/
    public boolean hasField(FieldName fieldName) {
        return containsKey(fieldName);
    }

    /** {@inheritDoc}*/
    public int getNFields() {       
        return size();
    }

    /** {@inheritDoc}*/
    public Object getOrdinalField(int ordinalFieldName) {       
        return get(FieldName.makeOrdinalField(ordinalFieldName));               
    }
    
    /** {@inheritDoc}*/
    public boolean hasOrdinalField(int ordinalFieldName) {
        return containsKey(FieldName.makeOrdinalField(ordinalFieldName));
    }

    /** {@inheritDoc}*/
    public Object getTextualField(String textualFieldName) { 
        FieldName fieldName = FieldName.make(textualFieldName);
        if (fieldName instanceof FieldName.Ordinal) {
            throw new ClassCastException();
        }
        return get(fieldName);
    }
    
    /** {@inheritDoc}*/
    public boolean hasTextualField(String textualFieldName) {
        FieldName fieldName = FieldName.make(textualFieldName);
        if (fieldName instanceof FieldName.Ordinal) {
            throw new ClassCastException();
        }        
        return containsKey(fieldName);
    }
}
