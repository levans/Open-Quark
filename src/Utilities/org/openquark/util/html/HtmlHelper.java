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
 * HtmlHelper.java
 * Created: 8-Jan-2004
 * By: Richard Webster
 */
package org.openquark.util.html;

/**
 * Utility methods for HTML.
 * @author Richard Webster
 */
public class HtmlHelper {

    /**
     * Replaces the chars '>', '<', and '&' with the sequences '&gt;', '&lt;', and '&amp;'.
     * @param str  the source string
     * @return the encoded string
     */
    public static String htmlEncode(String str) {
        StringBuilder result = null;
        for (int i = 0, max = str.length(), delta = 0; i < max; i++) {
            char c = str.charAt(i);
            String replacement = null;
            if (c == '&') {
                replacement = "&amp;";
            }
            else if (c == '<') {
                replacement = "&lt;";
            }
            else if (c == '>') {
                replacement = "&gt;";
            }

            if (replacement != null) {
                if (result == null) {
                    result = new StringBuilder(str);
                }
                result.replace(i + delta, i + delta + 1, replacement);
                delta += (replacement.length() - 1);
            }
        }
        if (result == null) {
            return str;
        }
        return result.toString();
    }

//    /**
//     * Replaces the the sequences '&gt;', '&lt;', and '&amp;' with the chars '>', '<', and '&'.
//     * @param str  the source string
//     * @return the decoded string
//     */
//    public static String htmlDecode(String str) {
//        // TODO: ...
//        
//        return str;
//    }

    /**
     * Encodes the string passed in so that it can be used safely in a string in JavaScript code.
     * @param str
     * @return the original with the quotes and whitespaces encoded
     */
    public static String encodeJavaScriptString(String str) {
        
        str = str.replaceAll("\\\\", "\\\\\\\\");
        str = str.replaceAll("\\r", "\\\\r");
        str = str.replaceAll("\\n", "\\\\n");
        str = str.replaceAll("\\t", "\\\\t");
        str = str.replaceAll("\'", "\\\\'");
        str = str.replaceAll("\\\"", "\\\\\"");
        
        return str;
    }    
}
