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
 * IOStreams.java
 * Created: Nov 23, 2005
 * By: Richard Webster
 */
package org.openquark.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Utility methods for working with InputStreams and OutputStreams.
 */
public final class IOStreams {

    /** The size of the byte buffer to use when reading input streams. */
    private final static int BUFFER_SIZE = 4096;
    
    // This class should not be instantiated.
    private IOStreams() {}

    /**
     * Copies all the data from the input stream to the output stream.
     */        
    public static void transferData(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte [] bufferArray = new byte [BUFFER_SIZE];
        ByteBuffer buffer = ByteBuffer.wrap(bufferArray);
        ReadableByteChannel channel = Channels.newChannel(inputStream);
        
        int nRead = 0;
        while ((nRead = channel.read(buffer)) != -1) {
            outputStream.write(bufferArray, 0, nRead);
            buffer.rewind(); // rewind the buffer so that the next read starts at position 0 again
        }
    }
    
    /**
     * Unzips the contents of the given zip file to the given folder.
     * 
     * NOTE: this code was adapted from 
     * http://java.sun.com/developer/technicalArticles/Programming/compression/
     * 
     * @param zipInputStream The input stream for the zip file being unzipped.
     * @param destDir The root folder where the contents of the zip file will be written.
     * @throws BusinessObjectsException
     */
    public static void unzip(ZipInputStream zipInputStream, File destDir)
            throws BusinessObjectsException {

        try {

            BufferedOutputStream dest = null;
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    int count;
                    byte data[] = new byte[BUFFER_SIZE];
                    File outputFile = new File(destDir, entry.getName());
                    FileSystemHelper.ensureDirectoryExists(outputFile.getParentFile());
                    FileOutputStream fos = new FileOutputStream(outputFile);
                    dest = new BufferedOutputStream(fos, BUFFER_SIZE);
                    while ((count = zipInputStream.read(data, 0, BUFFER_SIZE)) != -1) {
                        dest.write(data, 0, count);
                    }
                    dest.flush();
                    dest.close();
                }
            }
            zipInputStream.close();

        } catch (FileNotFoundException e) {
            throw new BusinessObjectsException(e);
        } catch (IOException e) {
            throw new BusinessObjectsException(e);
        }
    }

    /**
     * Unzips the zip file at the given URL to the given destination folder.
     */
    public static void unzip(URL url, File destDir) throws BusinessObjectsException {
        try {
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(url.openStream()));
            unzip(zis, destDir);
        } catch (IOException e) {
            throw new BusinessObjectsException(e);
        }
    }
    
    /**
     * Reads the given input stream into a String.
     */
    public static String readStringUsingDefaultSystemEncoding(InputStream inputStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        transferData(inputStream, baos);
        return baos.toString();
    }
    
    /**
     * Reads the given input stream into a String.
     */
    public static void writeStringUsingDefaultSystemEncoding(String string, OutputStream outputStream) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(string.getBytes());
        transferData(bais, outputStream);
    }

}
