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
 * NValLong.java
 * Created: Jun 14, 2004 at 5:32:56 PM
 * By: Iulian Radu
 */
package org.openquark.cal.internal.machine.g;


/** 
 * This is the NValLong class
 *
 * <p>
 * Creation: Jun 14, 2004 at 5:32:55 PM
 * @author Iulian Radu
 */
final class NValLong extends NVal {

    private final long longValue;  
    
    NValLong (long d) {
        this.longValue = d;
    }
       
    @Override
    public Object getValue() { 
        return Long.valueOf(longValue);                        
    }
    
    protected long getLongValue() {
        return longValue;
    }
    
    @Override
    public String toString (int indent) {
        StringBuilder sp = new StringBuilder ();
        for (int i = 0; i < indent; ++i) {
            sp.append (" ");
        }
        
        return sp.toString() + idString(0) + " <" + longValue + ">";
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final String debug_getNodeStartText() {
        return Long.toString(longValue);
    }     

    /**
     * {@inheritDoc}
     */
    @Override
    public final DataType getDataType() {
        return DataType.LONG;
    }        
}