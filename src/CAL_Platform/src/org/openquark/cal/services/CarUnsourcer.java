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
 * CarUnsourcer.java
 * Creation date: Nov 15, 2006.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.logging.Logger;

import org.openquark.util.IOStreams;



/**
 * Helper class to create a car or car jar by stripping an existing such archive of its cal source.
 * 
 * This class is not meant to be instantiated or subclassed.
 * 
 * @author Edward Lam
 */
final class CarUnsourcer {

    /** The base folder for cal source. */
    private static final String baseCalSourceFolderPathString = CALSourcePathMapper.SCRIPTS_BASE_FOLDER;
    
    /** The file extension for cal source. */
    private static final String calSourceFileExtension = CALSourcePathMapper.CAL_FILE_EXTENSION;

    /**
     * Take the specified car or car jar and generate a new one without source in the specified output location.
     * 
     * @param carOrCarJarPath the path to the car or car jar.
     * @param outputFolderString the output folder.
     * @param logger the logger to which messages will be logged.
     */
    public static void unsourceCar(String carOrCarJarPath, String outputFolderString, Logger logger) throws FileNotFoundException, IOException {
        
        logger.info("Creating unsourced archive from: \"" + carOrCarJarPath + "\"");
        
        JarFile jarFile = new JarFile(carOrCarJarPath);
        try {
            if (jarFile.getEntry(CarBuilder.CAR_MARKER_NAME) == null) {
                throw new IOException("Not a car: " + carOrCarJarPath);
            }
            
            String baseName = (new File(carOrCarJarPath)).getName();
            File outputFile = new File(outputFolderString, baseName);
            FileOutputStream fos = new FileOutputStream(outputFile);

            boolean outfileOk = false;
            try {
                BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
                JarOutputStream jos = new JarOutputStream(bos);

                try {
                    for (Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements(); ) {
                        JarEntry injarEntry = entries.nextElement();
                        JarEntry outjarEntry = createOutjarEntry(injarEntry);
                        
                        jos.putNextEntry(outjarEntry);
                        
                        String injarEntryName = injarEntry.getName();
                        ResourcePath.FilePath filePathFromJarEntry = getFilePathFromJarEntry(injarEntryName);
                        String entryPathString = filePathFromJarEntry.getPathString();
                        
                        if (entryPathString.startsWith(baseCalSourceFolderPathString) && entryPathString.endsWith(calSourceFileExtension)) {
                            // It's the source file.
                            // Don't transfer the data.
                        } else {
                            try {
                                IOStreams.transferData(jarFile.getInputStream(injarEntry), jos);
                            } finally {
                                jos.closeEntry();
                            }
                        }
                    }

                    outfileOk = true;

                } finally {
                    jos.flush();
                    bos.flush();

                    jos.close();
                    bos.close();
                }


            } catch (IOException e) {
                outfileOk = false;
                throw e;
            
            } finally {
                fos.close();

                // delete the incomplete Car if the operation failed with an exception
                if (!outfileOk) {
                    outputFile.delete();
                } else {
                    logger.info("Wrote output file: " + outputFile);
                }
            }
        
        } finally {
            jarFile.close();
        }
    }
    
    /**
     * Create a new jar entry from an existing entry.
     * This entry will be suitable for use in a new jar file.
     * 
     * This is different from the constructor JarEntry(otherJarEntry), which will copy all fields.
     * Copying all fields is undesirable, since we want the new jar entry to not have some fields (such as size) set.
     * These fields cannot be unset once set -- for instance, calling jarEntry.setSize(-1) will cause a RuntimeException.
     * 
     * @param injarEntry the existing entry.
     * @return a new jar entry, based on the existing entry, suitable for use in a new jar file.
     */
    private static final JarEntry createOutjarEntry(JarEntry injarEntry) {
        // Just copy the name, the comment, and any extra data.
        JarEntry outjarEntry = new JarEntry(injarEntry.getName());
        outjarEntry.setComment(injarEntry.getComment());
        outjarEntry.setExtra(injarEntry.getExtra());

        return outjarEntry;
    }
    
    /**
     * Convert a jar entry name to a file path.
     * @param entryName the entry name.
     * @return a file path corresponding to the entry name.
     * Slashes in the entry name will be treated as delimiters separating segments of the entry.
     */
    private static final ResourcePath.FilePath getFilePathFromJarEntry(String entryName) {
        
        String[] strings = entryName.split("\\/");
        
        ResourcePath.Folder folder = ResourcePath.EMPTY_PATH;
        for (int i = 0; i < strings.length - 1; i++) {
            folder = folder.extendFolder(strings[i]);
        }
        return folder.extendFile(strings[strings.length - 1]);
    }

}
