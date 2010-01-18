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
 * Cast.java
 * Created: July 30, 2002
 * By: Bo Ilic
 */
package org.openquark.cal.internal.foreignsupport.module.Prelude;



/**
 * Holds onto some helpful casting functions. Note that these are not true Java casts since they are
 * converting from a Java reference type (Object) to a Java primitive type. Hence they cannot be
 * implemented directly using a foreign function declaration with a "cast" foreign resolver.
 * 
 * Creation date: (July 30, 2002) 
 * @author Bo Ilic   
 */
public final class Cast {

    private Cast() {
    }

    
    public static char objectToChar(Object object) {
        return ((Character) object).charValue();
    }
        
    public static boolean objectToBoolean(Object object) {
        return ((Boolean) object).booleanValue();
    }    
    
    public static byte objectToByte(Object object) {
        return ((Byte) object).byteValue();
    }
    
    public static short objectToShort(Object object) {
        return ((Short) object).shortValue();
    }
       
    public static int objectToInt(Object object) {
        return ((Integer) object).intValue();
    }    
   
    public static long objectToLong(Object object) {
        return ((Long) object).longValue();
    }
   
    public static float objectToFloat(Object object) {
        return ((Float) object).floatValue();
    }
   
    public static double objectToDouble(Object object) {
        return ((Double) object).doubleValue();
    }           
}
