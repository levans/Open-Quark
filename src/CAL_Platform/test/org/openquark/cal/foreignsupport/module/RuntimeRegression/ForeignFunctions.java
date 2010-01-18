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
 * ForeignFunctions.javaRTKernel.java
 * Created: Nov 8, 2005
 * By: Raymond Cypher
 */

package org.openquark.cal.foreignsupport.module.RuntimeRegression;

import java.io.IOException;

/**
 * ForeignFunctions
 * This is a class which holds static and non-static methods used in testing foreign functions.
 * @author rcypher
 */
public class ForeignFunctions {

    public static String foreignCAF () throws IOException {
        if (true) {
            return new String();
        } else {
            throw new IOException();
        }
    }
    
    public static String getString (boolean b) throws IOException {
        if (b) {
            return new String();
        } else {
            throw new IOException();
        }
    }
    
    public static Integer objectToInteger (Object o) {
        return (Integer)o;
    }
    public static boolean integerToBoolean (Integer i) {
        return i.intValue() != 0;
    }
    
    public static void voidForeign (double d1, double d2) {
        if(d1 + d2 < d1) {System.out.println("d2 < 0");}
    }
    
    public static double throwUndeclaredException (boolean b) {
        if (b) { return 0.0;} 
        throw new NullPointerException ("Null pointer exception in ForeignFunctions.throwUndeclaredException.");
    }
}
