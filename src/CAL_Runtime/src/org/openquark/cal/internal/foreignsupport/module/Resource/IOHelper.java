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
 * IOHelper.java
 * Creation date: Jun 5, 2006.
 * By: Joseph Wong
 */
package org.openquark.cal.internal.foreignsupport.module.Resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.openquark.util.IOStreams;


/**
 * Contains helper methods for the Resouce module, which allows CAL code to access user resources.
 * 
 * @author Joseph Wong
 */
public final class IOHelper {

    /**
     * Private constructor.
     */
    private IOHelper() {
    }
        

    /**
     * Reads the specified InputStream into a new byte array, and close the stream afterwards.
     * If any IOExceptions are thrown during the reading process, a null byte array will be returned.
     * 
     * @param is the InputStream to be read.
     * @return an array of the bytes read, or null if there was a problem reading the stream.
     */
    public static byte[] readIntoByteArray(InputStream is) {
        if (is == null) {
            return null;
        }
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            try {
                IOStreams.transferData(is, outputStream);
            } finally {
                is.close();
            }
            
            return outputStream.toByteArray();
            
        } catch (IOException e) {
            return null;
        }
    }  

    /**
     * Reads the specified InputStream into a string using the given character set.
     * If any IOExceptions are thrown during the reading process, a null byte array will be returned.
     * 
     * @param charsetName the character set to be used for translating bytes into characters.
     * @param is the InputStream to be read.
     * 
     * @return a string for the text read from the stream, or null if there was a problem reading the stream.
     */
    public static String readIntoString(String charsetName, InputStream is) {
        if (is == null) {
            return null;
        }
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            try {
                IOStreams.transferData(is, outputStream);
            } finally {
                is.close();
            }
            
            return new String(outputStream.toByteArray(), charsetName);
            
        } catch (IOException e) {
            return null;
        }
    }
}
