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
 * General.java
 * Creation date: Nov 6, 2002.
 * By: Luke Evans
 */
package org.openquark.util;

import java.awt.Color;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URLDecoder;
import java.util.regex.Pattern;



/**
 * General utilities (static access).
 * 
 * Please try to add your static helper functions to a better place than this!
 * 
 * @author LEvans
 */
public final class General {
    
    /** not intended to be instantiated */
    private General () {}
    
    /**
     * Determine a text value from a system property, or falling back to a
     * supplied default
     * @param property the name of the System property which might hold the
     * value
     * @param defaultValue the value if the system property is not found (set)
     * @return the value
     */
    public static String getDefaultOrSystemProperty(String property, String defaultValue) {
        String propValue;
        return (propValue = System.getProperty(property)) == null ? defaultValue : propValue; 
    }   
    
    /**
     * Create a valid identifier from the given root string
     * @param root the basic string from which to derive the identifier
     * @param worryAboutFirstCharacter whether to concern ourselves with the
     * first character rule (can't be a digit)
     * @param maxLength 
     * @return the identifier
     */
    public static String validJavaIdentifier(String root, boolean worryAboutFirstCharacter, int maxLength) {
        StringBuilder sb = new StringBuilder(root.length());
        
        char sourceChar, targetChar;
        boolean wordBreak = false;
        for (int i = 0; i < root.length(); i++) {
            sourceChar = root.charAt(i);
            if (i == 0 && worryAboutFirstCharacter) {
                if (Character.isJavaIdentifierStart(sourceChar)) {
                    targetChar = Character.toLowerCase(sourceChar);           
                } else {
                    targetChar = '_';
                }
            } else {            
                if (Character.isJavaIdentifierPart(sourceChar)) {
                    // Do work break
                    if (wordBreak){
                        if (Character.isUpperCase(sourceChar) || Character.isDigit(sourceChar)) {
                            // Emit underscore, then character
                            sb.append("_");
                            targetChar = sourceChar;
                        } else {
                            // Emit upper case letter
                            targetChar = Character.toUpperCase(sourceChar);
                        }
                        wordBreak = false;
                        
                    } else {
                        targetChar = Character.toLowerCase(sourceChar);
                    } 
                    
                } else {
                    // Use 'word break' logic on next character, but don't emit anything
                    wordBreak = true;
                    continue;
                }
            
            }
            // Emit character 
            sb.append(targetChar);
            
            // Stop if we have reached a maximum length
            if (maxLength > 0 && sb.length() >= maxLength){
                break;
            }
        }
        
        return sb.toString();
        
    }

    /**
     * Returns a transparent version of the specified colour.
     * @param colour  the colour to base the tranparent colour on.
     * @param alpha   the value of the alpha channel (0 to 255).
     * @return a transparent version of the specified colour
     */
    public static Color makeTransparentColour(Color colour, int alpha) {
        if (colour == null) {
            return null;
        }
        return new Color(colour.getRed(), colour.getGreen(), colour.getBlue(), alpha);
    }
    
    /**
     * Decodes an URL encoded string (MIME: application/x-www-form-urlencoded) 
     * which was previously encoded with a specific encodingType. 
     * 
     * This method is a wrapper around URLDecoder.decode(), returning null if the
     * encoding type is not supported.
     * 
     * @param stringURL string to decode
     * @param encodingType encoding type
     * @return decoded string
     */
    public static String decodeURL(String stringURL, String encodingType) {
        try {
            return URLDecoder.decode(stringURL, encodingType);
            
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException();
        } 
    }
    
    /**
     * Decodes an URL string encoded with encoding type UTF-8.
     * @param stringURL the string to decode
     * @return the decoded string.
     */
    public static String decodeUrlUtf8(String stringURL) {
        return decodeURL(stringURL, "UTF-8");
    }

    /**
     * @param intArray 
     * @return a comma-separated string representation of an int array. 
     */
    public static String intArrayToString(int[] intArray) {
        StringBuilder sb = new StringBuilder("[");
        if (intArray != null) {
            final int nItems = intArray.length;

            for (int i = 0; i < nItems; ++i) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(intArray[i]);
            }
        }
        sb.append(']');
        
        return sb.toString();
    }
    
    /**
     * Sets all the public members of the object passed in to null.
     * Useful in the unit testing to make sure we don't leak.
     * @param toClear 
     * @throws IllegalAccessException if there's a non public member that doesn't start with dontClear_
     */
    public static void clearMembers(Object toClear) throws IllegalAccessException {
        Field[] fields = toClear.getClass().getDeclaredFields();
        
        for (int i=0; i< fields.length; ++i) {
            Field field = fields[i];
            
            Class<?> type = field.getType();
            if (!type.isPrimitive()) {
                int modifiers = field.getModifiers();
                if (!Modifier.isStatic(modifiers)) {
                    if (!Modifier.isFinal(modifiers)) {
                        if (!Modifier.isPublic(modifiers)) {
                            String name = field.getName();
                            if (name.startsWith("dontClear_")) {
                                break;
                            }
                        }
                        
                        // try to clear the member
                        field.set(toClear, null);
                    }
                }
            }
        }
    }

    /** This is the platform EOL character sequence*/
    static public final String SYSTEM_EOL = System.getProperty("line.separator");

    /**
     * Converts the newline/formfeed chars in a string to the 
     * standard platform line separator.
     * @param input
     * @return the string with the platform line end encoding
     */
    public static String toPlatformLineSeparators(String input) {
        return input.replaceAll("(\r\n)|\n|\r", SYSTEM_EOL);
    }
   
    
    /**
     * Removes any occurrences of the specified token (and the associated values) the connect string.
     * The token name is not case sensitive.
     * This code will not properly handle connect strings where the token values contain embedded semi-colons.
     * @param connectString  the connect string to be updated
     * @param tokenName      the name of the token (not case sensitive) to remove
     * @return               the connect string with the specified token removed.
     */
    public static String removeConnectStringToken(String connectString, String tokenName) {
        // Look for something like:  <tokenName> = <value> ;
        // The value part will take everything up to and including the next semi-colon (or the end of the string).
        // Allow for spaces before the token name and on either side of the '='.
        // Handle the case where the token appears at the beginning specially (since we don't need to add a new semi-colon).
        final String startReplaceExpr = "\\A\\s*" + tokenName + "\\s*\\=\\s*[^\\;]*\\;?";
        final String nonStartReplaceExpr = "\\;\\s*" + tokenName + "\\s*\\=\\s*[^\\;]*\\;?";

        String result1 = Pattern.compile(startReplaceExpr, Pattern.CASE_INSENSITIVE).matcher(connectString).replaceAll("");
        String result2 = Pattern.compile(nonStartReplaceExpr, Pattern.CASE_INSENSITIVE).matcher(result1).replaceAll(";");

        return result2;
    }
}
