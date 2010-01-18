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
 * NValBoolean.java
 * Created: Oct 23, 2002 at 5:57:31 PM
 * By: Raymond Cypher
 */
package org.openquark.cal.internal.machine.g;



/** 
 * This is the type class/interface
 *
 * <p>
 * Creation: Oct 23, 2002 at 5:57:30 PM
 * @author Raymond Cypher
 */
public final class NValBoolean extends NVal {

    //public static final NValBoolean TRUE = new NValBoolean (true);
    //public static final NValBoolean FALSE = new NValBoolean (false);
    
    private final boolean booleanValue;

    public NValBoolean (boolean b) {
        this.booleanValue = b;
    }
        
    /**
     * Return an object representation of the value.
     */
    @Override
    public Object getValue() {
        if (booleanValue) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }
    
    @Override
    public int getOrdinalValue () {
        return booleanValue ? 1 : 0;
    }
    /**
     * Return boolean value.
     */
    public boolean getBooleanValue () {
        return booleanValue;
    }
        
    @Override
    public String toString (int indent) {
        StringBuilder sp = new StringBuilder ();
        for (int i = 0; i < indent; ++i) {
            sp.append (" ");
        }
        
        return sp.toString() + idString(0) + " <" + booleanValue + ">";
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final String debug_getNodeStartText() {
        return Boolean.toString(booleanValue);
    }   

    /**
     * {@inheritDoc}
     */
    @Override
    public final DataType getDataType() {
        return DataType.BOOLEAN;
    }        
}
