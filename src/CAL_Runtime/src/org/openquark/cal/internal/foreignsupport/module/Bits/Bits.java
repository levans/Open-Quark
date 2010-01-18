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
 * Bits.java
 * Created: November 17, 2004 
 * By: Peter Cardwell 
 */
package org.openquark.cal.internal.foreignsupport.module.Bits;


/**
 * @author Peter Cardwell
 * The Bits class holds implementations of bitwise operations that are supported in java.
 */
public final class Bits {      
    
    /**
     * Returns an int where only the highest bit is set. It is found by first 
     * setting all bits in lower positions than the highest bit and then taking 
     * an exclusive or with the original value.
     * For example,
     *      highestBitMask (00101101) => 00100000
     *      highestBitMask (11111111) => 10000000
     *      highestBitMask (00000000) => 00000000
     */
    public static final int highestBitMask (int x) {
        x |= x>>>1;
        x |= x>>>2;
        x |= x>>>4;
        x |= x>>>8;
        x |= x>>>16;        
        return x ^ (x>>>1);
    }
    
    /**
     * Returns a long where only the highest bit is set. It is found by first 
     * setting all bits in lower positions than the highest bit and then taking 
     * an exclusive or with the original value.
     * For example,
     *      highestBitMask (00101101) => 00100000
     *      highestBitMask (11111111) => 10000000
     *      highestBitMask (00000000) => 00000000
     */
    public static final long highestBitMask (long x) {
        x |= x>>>1;
        x |= x>>>2;
        x |= x>>>4;
        x |= x>>>8;
        x |= x>>>16;
        x |= x>>>32;
        return x ^ (x>>>1);
    }
        
}

