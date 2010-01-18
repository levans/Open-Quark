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
 * StringEncoder_Test.java
 * Creation date: Mar 10, 2005.
 * By: Joseph Wong
 */
package org.openquark.cal.compiler;

import junit.framework.TestCase;

/**
 * A set of JUnit test cases that tests the proper operation of the methods in the StringEncoder
 * class, namely that of encoding characters and strings as CAL literals, and unencoding such
 * literals back into the characters and strings they represent.
 *
 * @author Joseph Wong
 */
public class StringEncoder_Test extends TestCase {

    /**
     * Constructor for StringEncoder_Test.
     * 
     * @param name
     *            the name of the test case
     */
    public StringEncoder_Test(String name) {
        super(name);
    }

    /**
     * Tests the method StringEncoder.unencodeString() for properly encoding a string
     * as a CAL string literal.
     */
    public void testEncodeString() {
        assertEquals("\"Hello World\"", StringEncoder.encodeString("Hello World"));
        assertEquals("\"\\\"Hello World\\\"\"", StringEncoder.encodeString("\"Hello World\""));
        assertEquals("\"aA,@ .\\n\\r\\t\\b\\f\\\"\\\'\\\\\\u0001\\u0fff\\u7fff\"", StringEncoder.encodeString("aA,@ .\n\r\t\b\f\"\'\\\u0001\u0fff\u7fff"));
    }

    /**
     * Tests the method StringEncoder.unencodeString() for properly encoding a character
     * as a CAL character literal.
     */
    public void testEncodeChar() {
        assertEquals("'a'", StringEncoder.encodeChar('a'));
        assertEquals("'A'", StringEncoder.encodeChar('A'));
        assertEquals("','", StringEncoder.encodeChar(','));
        assertEquals("'@'", StringEncoder.encodeChar('@'));
        assertEquals("' '", StringEncoder.encodeChar(' '));
        assertEquals("'.'", StringEncoder.encodeChar('.'));
        assertEquals("'\\n'", StringEncoder.encodeChar('\n'));
        assertEquals("'\\r'", StringEncoder.encodeChar('\r'));
        assertEquals("'\\t'", StringEncoder.encodeChar('\t'));
        assertEquals("'\\b'", StringEncoder.encodeChar('\b'));
        assertEquals("'\\f'", StringEncoder.encodeChar('\f'));
        assertEquals("'\\\"'", StringEncoder.encodeChar('\"'));
        assertEquals("'\\\''", StringEncoder.encodeChar('\''));
        assertEquals("'\\\\'", StringEncoder.encodeChar('\\'));
        
        for (char c = 128; c < '\u7fff'; c++) {
            if (c < '\u0010') {
                assertEquals("'\\u000" + Integer.toHexString(c) + "'", StringEncoder.encodeChar(c));                
            }
            else if (c < '\u0100') {
                assertEquals("'\\u00" + Integer.toHexString(c) + "'", StringEncoder.encodeChar(c));                
            }
            else if (c < '\u1000') {
                assertEquals("'\\u0" + Integer.toHexString(c) + "'", StringEncoder.encodeChar(c));                
            }
            else {
                assertEquals("'\\u" + Integer.toHexString(c) + "'", StringEncoder.encodeChar(c));
            }
        }
    }

    /**
     * Tests the method StringEncoder.unencodeString() for properly unencoding a character encoded
     * as a CAL character literal.
     */
    public void testUnencodeChar() {
        assertEquals('a', StringEncoder.unencodeChar("'a'"));
        assertEquals('A', StringEncoder.unencodeChar("'A'"));
        assertEquals(',', StringEncoder.unencodeChar("','"));
        assertEquals('@', StringEncoder.unencodeChar("'@'"));
        assertEquals(' ', StringEncoder.unencodeChar("' '"));
        assertEquals('.', StringEncoder.unencodeChar("'.'"));
        assertEquals('\n', StringEncoder.unencodeChar("'\\n'"));
        assertEquals('\r', StringEncoder.unencodeChar("'\\r'"));
        assertEquals('\t', StringEncoder.unencodeChar("'\\t'"));
        assertEquals('\b', StringEncoder.unencodeChar("'\\b'"));
        assertEquals('\f', StringEncoder.unencodeChar("'\\f'"));
        assertEquals('\"', StringEncoder.unencodeChar("'\\\"'"));
        assertEquals('\'', StringEncoder.unencodeChar("'\\\''"));
        assertEquals('\\', StringEncoder.unencodeChar("'\\\\'"));
        
        for (char c = 128; c < '\u7fff'; c++) {
            if (c < '\u0010') {
                assertEquals(c, StringEncoder.unencodeChar("'\\u000" + Integer.toHexString(c) + "'"));                
            }
            else if (c < '\u0100') {
                assertEquals(c, StringEncoder.unencodeChar("'\\u00" + Integer.toHexString(c) + "'"));                
            }
            else if (c < '\u1000') {
                assertEquals(c, StringEncoder.unencodeChar("'\\u0" + Integer.toHexString(c) + "'"));                
            }
            else {
                assertEquals(c, StringEncoder.unencodeChar("'\\u" + Integer.toHexString(c) + "'"));
            }
        }
    }

    /**
     * Tests the method StringEncoder.unencodeString() for properly unencoding a string encoded
     * as a CAL string literal.
     */
    public void testUnencodeString() {
        assertEquals("Hello World", StringEncoder.unencodeString("\"Hello World\""));
        assertEquals("\"Hello World\"", StringEncoder.unencodeString("\"\\\"Hello World\\\"\""));
        assertEquals("aA,@ .\n\r\t\b\f\"\'\\\u0001\u0fff\u7fff", StringEncoder.unencodeString("\"aA,@ .\\n\\r\\t\\b\\f\\\"\\\'\\\\\\u0001\\u0fff\\u7fff\""));
    }

}
