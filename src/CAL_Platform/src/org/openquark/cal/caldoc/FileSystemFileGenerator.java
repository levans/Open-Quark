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
 * FileSystemFileGenerator.java
 * Creation date: Oct 7, 2005.
 * By: Joseph Wong
 */
package org.openquark.cal.caldoc;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.logging.Logger;

import org.openquark.util.FileSystemHelper;


/**
 * This class implements a rudimentary file generator for creating text files on the file system.
 *
 * @author Joseph Wong
 */
public class FileSystemFileGenerator implements FileGenerator {
    
    /**
     * The size of the buffer for the BufferedOutputStream, used to improve performance on the FileOutputStream
     * through buffering the writes to the file system.
     */
    private static final int OUTPUT_STREAM_BUFFER_SIZE = 1024;

    /** The system's line separator. */
    private static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");
    
    /** The base directory for the generated files. */
    private final File baseDirectory;
    
    /** The logger to be used for logging status/error messages. */
    private final Logger logger;
    
    /**
     * Constructs a FileGenerator based in the given directory.
     * @param baseDirectory the base directory for the generated files.
     * @param logger the logger to be used for logging status/error messages.
     */
    public FileSystemFileGenerator(File baseDirectory, Logger logger) {
        AbstractDocumentationGenerator.verifyArg(baseDirectory, "baseDirectory");
        FileSystemHelper.ensureDirectoryExists(baseDirectory);
        this.baseDirectory = baseDirectory;
        
        AbstractDocumentationGenerator.verifyArg(logger, "logger");
        this.logger = logger;
    }
    
    /**
     * Generates a text file using the specified Charset.
     * @param fileName the name of the file to be generated.
     * @param content the content of the file to be generated.
     * @param charset the Charset to use for encoding.
     */
    public void generateTextFile(String fileName, String content, Charset charset) {
        File file = new File(baseDirectory, fileName);
        generateTextFile(file, content, charset);
    }
    
    /**
     * Generates a text file in a particular subdirectory of the base directory using the specified Charset.
     * The subdirectory will be created if it does not exist.
     * 
     * @param subdirectory the subdirectory name.
     * @param fileName the name of the file to be generated.
     * @param content the content of the file to be generated.
     * @param charset the Charset to use for encoding.
     */
    public void generateTextFile(String subdirectory, String fileName, String content, Charset charset) {
        File file;
        if (subdirectory != null) {
            File subdirFile = new File(baseDirectory, subdirectory);
            FileSystemHelper.ensureDirectoryExists(subdirFile);
            
            file = new File(subdirFile, fileName);
        } else {
            file = new File(baseDirectory, fileName);
        }
        generateTextFile(file, content, charset);
    }
    
    /**
     * Generates a text file using the specified Charset.
     * @param file the File object representing the file to be generated.
     * @param content the content of the file to be generated.
     * @param charset the Charset to use for encoding.
     */
    private void generateTextFile(File file, String content, Charset charset) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fos, OUTPUT_STREAM_BUFFER_SIZE);
            try {
                OutputStreamWriter writer = new OutputStreamWriter(bos, charset);
                
                writer.write(content.replaceAll("\n", LINE_SEPARATOR));
                writer.flush();
                writer.close();
                
                logger.finest(CALDocMessages.getString("STATUS.generatedFile", file.getAbsolutePath()));
                
            } finally {
                bos.flush();
                bos.close();
                fos.close();
            }
        } catch (FileNotFoundException e) {
            logger.severe(CALDocMessages.getString("STATUS.cannotWriteToFile", file.getAbsolutePath()));
        } catch (IOException e) {
            logger.severe(CALDocMessages.getString("STATUS.errorWritingFile", file.getAbsolutePath()));
        }
    }
}
