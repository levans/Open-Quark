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
 * TextEncodingUtilities.java
 * Creation date: Dec 12, 2006.
 * By: Joseph Wong
 */
package org.openquark.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * Warning- this class should only be used by the CAL compiler implementation. It is not part of the
 * external API of the CAL platform.
 * <P>
 * This helper class provides methods for converting byte streams to character streams using the
 * standard encoding for CAL files, which is UTF-8.
 *
 * @author Joseph Wong
 */
public final class TextEncodingUtilities {

    /** The UTF-8 Charset. */
    public static final Charset UTF_8_CHARSET = Charset.forName("UTF-8");
    
    /** This class is not meant to be instantiated. */
    private TextEncodingUtilities() {}
    
    /**
     * Constructs a new Reader based on the UTF-8 encoding.
     * @param inputStream the underlying input stream.
     * @return a corresponding Reader.
     */
    public static Reader makeUTF8Reader(InputStream inputStream) {
        return new InputStreamReader(inputStream, UTF_8_CHARSET);
    }
    
    /**
     * Converts the string to bytes using the UTF-8 encoding.
     * @param string the string to be converted into bytes.
     * @return the corresponding UTF-8-encoded bytes.
     */
    public static byte[] getUTF8Bytes(String string) {
        try {
            return string.getBytes(UTF_8_CHARSET.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
