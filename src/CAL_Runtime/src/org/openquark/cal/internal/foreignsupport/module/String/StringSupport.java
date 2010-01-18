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
 * StringSupport.java
 * Created: Nov 7, 2005
 * By: Bo Ilic
 */

package org.openquark.cal.internal.foreignsupport.module.String;

import java.util.Arrays;

/**
 * A collection of helper foreign functions for support of the String module.
 * All methods and fields in this class are for internal use only.
 * @author Bo Ilic
 */
public final class StringSupport {
    
    /** StringSupport is non-instantiable */
    private StringSupport() {}
    
    /**
     * Note that the corresponding overload of String.indexOf types 'ch' as int rather
     * than char. However, the actual logic is as if the int is truncated to a char, so
     * we prefer to give a more naturally type-safe version.
     */
    public static int indexOf(char ch, String stringToSearch) {
        return stringToSearch.indexOf(ch);
    }
    
    /**
     * Note that the corresponding overload of String.indexOf types 'ch' as int rather
     * than char. However, the actual logic is as if the int is truncated to a char, so
     * we prefer to give a more naturally type-safe version.
     */
    public static int indexOf(char ch, int fromIndex, String stringToSearch) {
        return stringToSearch.indexOf(ch, fromIndex);
    }
      
    public static int indexOf(String str, String stringToSearch) {
        return stringToSearch.indexOf(str);
    }
       
    public static int indexOf(String str, int fromIndex, String stringToSearch) {
        return stringToSearch.indexOf(str, fromIndex);
    }
    
    /**
     * Note that the corresponding overload of String.indexOf types 'ch' as int rather
     * than char. However, the actual logic is as if the int is truncated to a char, so
     * we prefer to give a more naturally type-safe version.
     */
    public static int lastIndexOf(char ch, String stringToSearch) {
        return stringToSearch.lastIndexOf(ch);
    }
    
    /**
     * Note that the corresponding overload of String.indexOf types 'ch' as int rather
     * than char. However, the actual logic is as if the int is truncated to a char, so
     * we prefer to give a more naturally type-safe version.
     */
    public static int lastIndexOf(char ch, int fromIndex, String stringToSearch) {
        return stringToSearch.lastIndexOf(ch, fromIndex);
    }
      
    public static int lastIndexOf(String str, String stringToSearch) {
        return stringToSearch.lastIndexOf(str);
    }
       
    public static int lastIndexOf(String str, int fromIndex, String stringToSearch) {
        return stringToSearch.lastIndexOf(str, fromIndex);
    }
    
    public static boolean startsWith(String prefix, String stringToTest) {
        return stringToTest.startsWith(prefix);
    }
    
    public static boolean startsWith(String prefix, int offsetIndex, String stringToTest) {
        return stringToTest.startsWith(prefix, offsetIndex);
    }
    
    public static boolean endsWith(String suffix, String stringToTest) {
        return stringToTest.endsWith(suffix);
    } 
    
    
    /**
     * Returns a string created from repeating the character a specified number of times.
     * @param n number of times to repeat
     * @param c character to repeat    
     * @return string 
     */
    public static String replicate (int n, char c) {
        char[] charArray = new char[n];
        Arrays.fill(charArray, c);
        
        return new String(charArray);
    }
          
}
