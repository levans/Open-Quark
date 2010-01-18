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
 * Debug.java
 * Created: May 13, 2002
 * By: Bo Ilic
 */
package org.openquark.cal.internal.foreignsupport.module.Debug;

import org.openquark.cal.runtime.CalValue;
import org.openquark.cal.runtime.DebugSupport;

/**
 * Some helper functions for supporting debugging primitives in CAL.
 * Creation date: (Oct 22, 2002) 
 * @author Bo Ilic  
 */       
public final class Debug {
            
    private Debug() {}
    
    /**    
     * implements the instance function for Debug.Show Prelude.String.
     * @param s
     * @return The showable version of the String s
     */
    public static String showString(String s) {
        if (s == null) {
            return "null";
        }
        return new StringBuilder("\"").append(s).append('\"').toString();
    }
    
    /**    
     * implements the instance function for Debug.Show Prelude.Char.
     * @param c
     * @return the showable String for the char c.
     */
    public static String showChar(char c) {      
        return new StringBuilder("\'").append(c).append('\'').toString();
    }    
    
    /**
     * Method printToStandardOut
     * @param message the message to print on the standard out
     */
    public static void printToStandardOut (String message) {
        System.out.print (message);
    }
    
    /**
     * Method printToStandardError.
     * @param message the message to print on the standard error
     */
    public static void printToStandardError (String message) {
        System.err.print(message);            
    }      
           
    /**     
     * @param calValue
     * @return the result of calling DebugSupport.showInternal(calValue) except that all exceptions in calling the method
     *    are handled so this will always succeed.
     */    
    public final static String showInternal(CalValue calValue) {
        if (calValue == null) {
            throw new NullPointerException("calValue cannot be null.");
        }
        
        try {
            return DebugSupport.showInternal(calValue);
        } catch (Throwable exception) {
            return exception.getMessage();
        }        
    }
    
    /**     
     * @param calValue
     * @return the result of calling DebugSupport.showInternalGraph(calValue) except that all exceptions in calling the method
     *    are handled so this will always succeed.
     */    
    public final static String showInternalGraph(CalValue calValue) {
        if (calValue == null) {
            throw new NullPointerException("calValue cannot be null.");
        }
        
        try {
            return DebugSupport.showInternalGraph(calValue);
        } catch (Throwable exception) {
            return exception.getMessage();
        }        
    }                 
        
    
}