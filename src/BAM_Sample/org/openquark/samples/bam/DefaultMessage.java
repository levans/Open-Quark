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
 * DefaultMessage.java
 * Created: 5-Mar-2004
 * By: Rick Cameron
 */

package org.openquark.samples.bam;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * This is the default message implementation. It uses a map to store
 * message key, value pairs.
 * 
 */
class DefaultMessage implements Message {
    
    private final String messageType;
    
    private Map<String, Object> valueMap = new HashMap<String, Object> ();
    
    DefaultMessage (String messageType){
        this.messageType = messageType;
    }

    /**
     * @see org.openquark.samples.bam.Message#getType()
     */
    public String getType () {
        return messageType;
    }

    /**
     * @see org.openquark.samples.bam.Message#getPropertyNames()
     */
    public Collection<String> getPropertyNames () {
        return valueMap.keySet();
    }

    /**
     * @see org.openquark.samples.bam.Message#getProperty(java.lang.String)
     */
    public Object getProperty (String propertyName) {
        return valueMap.get(propertyName);
    }

    void addValue (String propertyName, Object value) {
        valueMap.put(propertyName, value);
    }
    
    @Override
    public String toString() {
        String msg = messageType + " {";
        for( Iterator<String> iter = valueMap.keySet().iterator(); iter.hasNext(); ) {
            Object key=iter.next();
            msg += key + " = " + valueMap.get(key) + ", ";
        }
        msg += " }";
        
        return msg;
    }
}
