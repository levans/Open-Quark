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
 * Assert.java
 * Creation date: Oct 8, 2002.
 * By: Edward Lam
 */
package org.openquark.cal.services;

/**
 * A static helper class which contains various utility methods which can be used
 * to check code preconditions at runtime.
 * 
 * These methods throw some sort of unchecked exception if the precondition is not met.
 * 
 * @author Edward Lam
 */
public final class Assert {
    /*
     * TODOEL: When this is ready, move it to a utility package..
     */

    /**
     * Private constructor -- this class is not intended to be instantiated.
     */
    private Assert() {
    }
    
    /**
     * Assert that an object is not null.
     * 
     * @param o the object to check
     * @throws NullPointerException if o is null.
     */
    public static void isNotNull(Object o) throws NullPointerException {
        Assert.isNotNull(o, null);
    }

    /**
     * Assert that an object is not null.
     * 
     * @param o the object to check.
     * @param exceptionText the text of the exception message if the object is null.
     * If null, no exception text is included.
     * @throws NullPointerException if o is null.
     */
    public static void isNotNull(Object o, String exceptionText) throws NullPointerException {
        if (o == null) {
            if (exceptionText == null) {
                throw new NullPointerException();
            } else {
                throw new NullPointerException(exceptionText);
            }
        }
    }

    
    /**
     * Assert that the value of an argument is not null.
     * @param argValue the value of the argument.
     * @throws NullPointerException if argValue is null.
     */
    public static void isNotNullArgument(Object argValue) throws NullPointerException {
        Assert.isNotNullArgument(argValue, null);
    }
    
    /**
     * Assert that the value of an argument is not null.
     * @param argValue the value of the argument.
     * @param argName the name of the argument.
     * If null or an empty string, no argument name is assumed.
     * 
     * @throws NullPointerException if argumentValue is null.
     */
    public static void isNotNullArgument(Object argValue, String argName) throws NullPointerException {
        if (argValue == null) {
            if (argName == null || argName.length() == 0) {
                throw new NullPointerException("Argument must not be null.");
            } else {
                throw new NullPointerException("Argument \"" + argName + "\" must not be null");
            }
        }
    }
}
