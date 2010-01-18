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
 * FileIO.java
 * Creation date: Jun 30, 2005.
 * By: Joseph Wong
 */

package org.openquark.cal.foreignsupport.module.File;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.openquark.util.IOStreams;


/**
 * This class contains foreign support methods for the File module, providing methods
 * for reading and writing text and binary files.
 * 
 * @author Bo Ilic
 * @author Joseph Wong
 */          
public final class FileIO {
    
    /**
     * Read the specified file and return the contents in a String.
     * @param fileName the name of the file to be read.
     */
    public static IOResult/*String*/ readFile(File fileName) {                                   
        
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            StringBuilder resultString = new StringBuilder();
            
            try {
                int c;
                while ((c = in.read()) != -1) {
                    resultString.append((char)c);
                }
            } finally {
                in.close();
            }
            return IOResult.makeResult(resultString.toString());
            
        } catch (IOException e) {
            return IOResult.makeError(e, fileName);
        }
    }
    
    /**
     * Write the specified contents into the file specified by the file name.
     * @param fileName the name of the file to be (over)written.
     * @param contents the contents to be written to the file.
     */
    public static IOResult/*void*/ writeFile(File fileName, String contents) {
        
        try {
            FileWriter writer = new FileWriter(fileName);
            try {
                writer.write(contents);
            } finally {
                writer.close();
            }
            return IOResult.makeVoidResult();
            
        } catch (IOException e) {
            return IOResult.makeError(e, fileName);
        }
    }
    
    /**
     * Append the specified contents to the file specified by the file name.
     * @param fileName the name of the file to be appended.
     * @param contents the contents to be written to the file.
     */
    public static IOResult/*void*/ appendFile(File fileName, String contents) {
        
        try {
            FileWriter writer = new FileWriter(fileName, true);
            try {
                writer.write(contents);
            } finally {
                writer.close();
            }
            return IOResult.makeVoidResult();
            
        } catch (IOException e) {
            return IOResult.makeError(e, fileName);
        }
    }
    
    /**
     * Read the specified file and return the contents in a byte array.
     * @param fileName the name of the file to be read.
     */
    public static IOResult/*byte[]*/ readFileBinary(File fileName) {
        try {
            return IOResult.makeResult(readFileBinaryIntoByteArray(fileName));
        } catch (IOException e) {
            return IOResult.makeError(e, fileName);
        }
    }
    
    /**
     * Read the specified file and return the contents in a byte array.
     * @param fileName the name of the file to be read.
     */
    public static byte[] readFileBinaryIntoByteArray(File fileName) throws java.io.IOException {
        InputStream in = new FileInputStream(fileName);
        long fileLength = fileName.length();
        return readFileBinary(in, (int)fileLength);
    }
    
    /**
     * Read the file specified by its file name or URL, and return the contents in a byte array.
     * @param fileNameOrUrl the url or name of the file to be read.
     */
    public static IOResult/*byte[]*/ readBinaryContentsFromFileOrUrl(String fileNameOrUrl) {
        // First check whether a valid filename is specified.
        File file = new File(fileNameOrUrl);
        
        if (file.exists()) {
            return readFileBinary(file);
            
        } else {
            // If the string isn't a valid path name, then check whether it is a URL.
            try {
                // Check whether the fileNameOrUrl is a valid URL.
                URL url = new URL(fileNameOrUrl);
                return IOResult.makeResult(readBinaryContentsFromUrlIntoByteArray(url));
                
            } catch (MalformedURLException e) {
                return IOResult.makeError(new FileNotFoundException(fileNameOrUrl + " (The system cannot find the path specified)"), null);
            } catch (IOException e) {
                return IOResult.makeError(e, null);
            }
        }
    }
    
    /**
     * Read the specified file and return the contents in a byte array.
     * @param fileNameOrUrl the url or name of the file to be read.
     */
    public static byte[] readBinaryContentsFromFileOrUrlIntoByteArray(String fileNameOrUrl) throws java.io.IOException {
        // First check whether a valid filename is specified.
        File file = new File(fileNameOrUrl);
        
        if (file.exists()) {
            return readFileBinaryIntoByteArray(file);
            
        } else {
            // If the string isn't a valid path name, then check whether it is a URL.
            try {
                // Check whether the fileNameOrUrl is a valid URL.
                URL url = new URL(fileNameOrUrl);
                return readBinaryContentsFromUrlIntoByteArray(url);
                
            } catch (MalformedURLException e) {
                throw new FileNotFoundException(fileNameOrUrl + " (The system cannot find the path specified)");
            }
        }
    }
    
    /**
     * Read the specified file and return the contents in a byte array.
     * @param fileURL the url of the file to be read.
     */
    public static byte[] readBinaryContentsFromUrlIntoByteArray(URL fileURL) throws java.io.IOException {
        InputStream inputStream = fileURL.openStream();
        return readFileBinary(inputStream, 0);
    }
    
    /**
     * Read the specified input stream and return the contents in a byte array.
     * 
     * @param inputStream
     *            the input stream to be read.
     * @param dataLength
     *            the number of bytes to read. If the size of the data is known,
     *            then it can be specified using this argument. If the data size
     *            is not known, then zero can be passed for this value.
     */
    public static byte[] readFileBinary(InputStream inputStream, int dataLength) throws java.io.IOException {
        // Buffer the input.
        inputStream = new BufferedInputStream(inputStream);
        
        ByteArrayOutputStream outputStream;
        if (dataLength > 0) {
            outputStream = new ByteArrayOutputStream(dataLength);
        } else {
            outputStream = new ByteArrayOutputStream();
        }
        
        try {
            IOStreams.transferData(inputStream, outputStream);
        } finally {
            inputStream.close();
        }
        
        return outputStream.toByteArray();
    }
    
    /**
     * Write the specified binary contents into the file specified by the file name.
     * @param fileName the name of the file to be (over)written.
     * @param contents the binary contents to be written to the file.
     */
    public static IOResult/*void*/ writeFileBinary(File fileName, byte[] contents) {
        
        try {
            FileOutputStream fos = new FileOutputStream(fileName);
            try {
                fos.write(contents);
            } finally {
                fos.close();
            }
            return IOResult.makeVoidResult();
            
        } catch (IOException e) {
            return IOResult.makeError(e, fileName);
        }
    }
    
    /**
     * Append the specified binary contents to the file specified by the file name.
     * @param fileName the name of the file to be appended.
     * @param contents the binary contents to be written to the file.
     */
    public static IOResult/*void*/ appendFileBinary(File fileName, byte[] contents) {
        
        try {
            FileOutputStream fos = new FileOutputStream(fileName, true);
            try {
                fos.write(contents);
            } finally {
                fos.close();
            }
            return IOResult.makeVoidResult();
            
        } catch (IOException e) {
            return IOResult.makeError(e, fileName);
        }
    }
}