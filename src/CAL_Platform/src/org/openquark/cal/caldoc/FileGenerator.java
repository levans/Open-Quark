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
 * FileGenerator.java
 * Creation date: Oct 7, 2005.
 * By: Joseph Wong
 */
package org.openquark.cal.caldoc;

import java.nio.charset.Charset;

/**
 * This interface represents a rudimentary file generator for creating text files.
 *
 * @author Joseph Wong
 */
public interface FileGenerator {
    
    /** The UTF-8 Charset. */
    public static final Charset UTF_8_CHARSET = Charset.forName("UTF-8");

    /**
     * Generates a text file using the specified Charset.
     * @param fileName the name of the file to be generated.
     * @param content the content of the file to be generated.
     * @param charset the Charset to use for encoding.
     */
    public void generateTextFile(String fileName, String content, Charset charset);
    
    /**
     * Generates a text file in a particular subdirectory of the base directory using the specified Charset.
     * The subdirectory will be created if it does not exist.
     * 
     * @param subdirectory the subdirectory name.
     * @param fileName the name of the file to be generated.
     * @param content the content of the file to be generated.
     * @param charset the Charset to use for encoding.
     */
    public void generateTextFile(String subdirectory, String fileName, String content, Charset charset);
}
