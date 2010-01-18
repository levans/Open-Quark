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
 * StringEncoder.java
 * Creation date: (January 10, 2001 3:12:59 PM)
 * By: Bo Ilic
 */
package org.openquark.cal.compiler;

/**
 * A utility class for converting encoded strings and characters to the underlying string or character.
 * The encoding assumed is the encoding for string and character literals as specified in the Java Language
 * Specification. This is also the syntax used for string and character literals in CAL.
 * Creation date: (January 10, 2001 3:12:59 PM)
 * @author Bo Ilic
 */
public final class StringEncoder {
    
    /** ASCII characters have codes 0-127. */
    private final static int MAX_ASCII_CODE = 127;

    static private class CharIntPair {
        private final char c;
        private final int i;

        CharIntPair(char c, int i) {
            this.c = c;
            this.i = i;
        }

        public char getChar() {
            return c;
        }

        public int getInt() {
            return i;
        }
    }
    
    private StringEncoder() {}
    
    /**
     * Create a textual representation of a String value that can be parsed in CAL or Java source code as a String literal.
     * 
     * In particular, the special characters newline, carriage return, horizontal tab, backspace, form feed, double quote,
     * single quote, and backslash are output in their escaped format.
     * Non- ASCII characters i.e. having code >= 128 are output in hex escaped form e.g. "\u1234" etc.
     * ISO-control characters (0-31 and 127-159) within the ASCII range are also escaped. 
     * 
     * Does the opposite of unencodeString.
     * Eg: Turns 'a' + 'b' + '\n' into '"' + 'a' + 'b' + '\' + 'n' + '"'
     * Creation date: (06/04/01 8:33:00 AM)     
     * @param unencodedString String
     * @return includes the starting and ending double quote characters.
     */
    public static String encodeString(String unencodedString) {
        StringBuilder sbEncoded = new StringBuilder("\"");

        // Examine each char in the unencodedString, and if it's a special character, encode it and add it to sbEncoded.  
        // If it's just a plain ol' regular run o' the mill char, then just add it to sbEncoded.    
        for (int i = 0, unencodedStringLength = unencodedString.length(); i < unencodedStringLength; i++) {
            char c = unencodedString.charAt(i);
            
            sbEncoded.append(charToEncodedCharFragment(c));                      
        }

        // Need to add ending enclosing double quotes.    
        sbEncoded.append('\"');

        return sbEncoded.toString();
    }
    
    /**
     * Create a textual representation of a char value that can be parsed in CAL or Java source code as a char literal.
     * 
     * In particular, the special characters newline, carriage return, horizontal tab, backspace, form feed, double quote,
     * single quote, and backslash are output in their escaped format.
     * Non- ASCII characters i.e. having code >= 128 are output in escaped form e.g. "\u1234" etc. 
     * ISO-control characters (0-31 and 127-159) within the ASCII range are also escaped. 
     * 
     * @param c
     * @return - The textual representation for the escape sequence representing the character.
     *   eg. '\n', or '\uA123', or 'w'. Note that the start and end single quote are included. What is returned
     *   can directly be parsed as a char literal in CAL or Java source.
     */    
    public static String encodeChar(char c) {
        return new StringBuilder ("\'").append(charToEncodedCharFragment(c)).append('\'').toString();              
    } 
    
    private static String charToEncodedCharFragment(char c) {
               
        switch (c) {
            case '\n' : //newline
                return "\\n";                                    

            case '\r' : //carriage return
                return "\\r";                                    

            case '\t' : //horizontal tab
                return "\\t";                               

            case '\b' : //backspace
                return "\\b";                                 

            case '\f' : //form feed
                return "\\f";                                  

            case '\"' : //double quote
                return "\\\"";                                 

            case '\'' : //single quote
                return "\\\'";                                  

            case '\\' : //back slash
                return "\\\\";                                 
                
            default : 
            {                
                if (c > MAX_ASCII_CODE || Character.isISOControl(c)) {                    
                    String hs = Integer.toHexString(c);
                    switch (hs.length()) {                     
                        case 1:
                            return "\\u000" + hs;
                    
                        case 2:
                            return "\\u00" + hs;                          
                            
                        case 3:
                            return "\\u0" + hs;                           
                            
                        case 4:
                            return "\\u" + hs;                           
                         
                        default:
                            assert (false);
                            return null;                            
                    }                                       
                    
                } else {
                    return Character.toString(c);
                }
                
            }
        }     
    }
    
    /**
     * Is the character an octal digit?
     * Creation date: (1/10/01 3:39:26 PM)
     * @return boolean
     * @param c char
     */
    static private boolean isOctalDigit(char c) {
        return c >= '0' && c <= '7';
    }
    
    /**
     * Converts a char literal (as obtained from the lexer) from its quoted and escaped form
     * to its underlying string value.
     * e.g. "'\n'" is converted to '\n'.
     *
     * Creation date: (1/10/01 2:35:01 PM)
     * @return char
     * @param encodedChar String
     * @exception IllegalArgumentException The exception description.
     */
    public static char unencodeChar(String encodedChar) throws IllegalArgumentException {

        int encodedCharLength = encodedChar.length();
        if (encodedCharLength <= 2 || encodedChar.charAt(0) != '\'' || encodedChar.charAt(encodedCharLength - 1) != '\'') {
            throw new IllegalArgumentException();
        }

        char c = encodedChar.charAt(1);

        switch (c) {
            case '\'' :
                throw new IllegalArgumentException();

            case '\\' :
            {
                //escaped character
                CharIntPair charInt = unencodeEscape(encodedChar.substring(2));
                if (encodedCharLength > charInt.getInt() + 3) {
                    throw new IllegalArgumentException();
                }

                return charInt.getChar();
            }

            default :
                return c;
        }
    }
    
    /**
     * Attempts to parse a character from an escape string and returns the pair consisting of the
     * parsed character and the number of characters consumed from the escapeString.
     * For example, "naa" returns ('\n', 1) and "u1234abc" returns ('\u1234', 5).
     * Creation date: (1/10/01 1:53:28 PM)
     * @return CharIntPair the escape character, followed by the number of character consumed from escapeString
     * @param escapeString String 
     * @exception IllegalArgumentException thrown if a char can not be extracted
     */
    private static CharIntPair unencodeEscape(String escapeString) throws IllegalArgumentException {

        int escapeStringLength = escapeString.length();
        if (escapeStringLength == 0) {
            throw new IllegalArgumentException();
        }
        char c = escapeString.charAt(0);

        char resultChar;
        int nCharsConsumed = 1;

        switch (c) {
            case 'n' :
                resultChar = '\n';
                break;

            case 'r' :
                resultChar = '\r';
                break;

            case 't' :
                resultChar = '\t';
                break;

            case 'b' :
                resultChar = '\b';
                break;

            case 'f' :
                resultChar = '\f';
                break;

            case '"' :
                resultChar = '"';
                break;

            case '\'' :
                resultChar = '\'';
                break;

            case '\\' :
                resultChar = '\\';
                break;

            case 'u' :
            {
                //hex control character
                //('u')+ HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT 
                
                int firstHexDigitIndex = 1;
                int lastHexDigitIndexPlusOne = firstHexDigitIndex + 4;
                while (escapeString.charAt(firstHexDigitIndex) == 'u') {
                    firstHexDigitIndex++;
                    lastHexDigitIndexPlusOne++;

                    if (escapeStringLength < lastHexDigitIndexPlusOne) {
                        throw new IllegalArgumentException();
                    }
                }

                String hexString = escapeString.substring(firstHexDigitIndex, lastHexDigitIndexPlusOne);

                int controlChar = 0;
                try {
                    controlChar = Integer.parseInt(hexString, 16);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException();
                }

                resultChar = (char)controlChar;
                nCharsConsumed = lastHexDigitIndexPlusOne;
                break;
            }

            case '0' :
            case '1' :
            case '2' :
            case '3' :
            case '4' :
            case '5' :
            case '6' :
            case '7' :
            {
                //octal control character
                //(ZeroToThree OctalDigit OctalDigit) | (OctalDigit OctalDigit) | (OctalDigit)

                int octalLength = 1;

                if (escapeStringLength >= 2 && isOctalDigit(escapeString.charAt(1))) {

                    if (escapeStringLength >= 3 && (c >= '0' || c <= '3') && isOctalDigit(escapeString.charAt(2))) {
                        octalLength = 3;
                    } else {
                        octalLength = 2;
                    }
                }

                String octalString = escapeString.substring(0, octalLength);

                int controlChar = 0;
                try {
                    controlChar = Integer.parseInt(octalString, 8);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException();
                }

                resultChar = (char) controlChar;
                nCharsConsumed = octalLength;
                break;
            }

            default :
                throw new IllegalArgumentException();
        }

        return new CharIntPair(resultChar, nCharsConsumed);
    }
    
    /**
     * Converts a string literal (as obtained from the lexer) from its quoted and escaped form
     * to its underlying string value.
     * e.g. '"' + 'a' + '\\' + 'n' + 'b' + '"' is converted to 'a' + '\n' + 'b'
     * 
     * Creation date: (1/10/01 9:43:02 AM)
     * @return String
     * @param encodedString String
     * @exception IllegalArgumentException if the string is not a valid encoded string
     */
    public static String unencodeString(String encodedString) throws IllegalArgumentException {

        int encodedStringLength = encodedString.length();
        if (encodedStringLength < 2 || encodedString.charAt(0) != '"' || encodedString.charAt(encodedStringLength - 1) != '"') {
            throw new IllegalArgumentException();
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 1; i < encodedStringLength - 1; ++i) {

            char c = encodedString.charAt(i);

            switch (c) {
                case '"' :
                    throw new IllegalArgumentException();

                case '\\' :
                {
                    //escaped character
                    CharIntPair charInt = unencodeEscape(encodedString.substring(i + 1));
                    sb.append(charInt.getChar());
                    i += charInt.getInt();
                    break;
                }

                default :
                    sb.append(c);
                    break;
            }
        }

        return sb.toString();
    }
}