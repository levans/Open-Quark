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
 * FileSystemHelper.java
 * Creation date: Nov 18, 2004.
 * By: Edward Lam
 */
package org.openquark.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;


/**
 * This class contains static helper methods to aid in interactions with the file system.
 * @author Edward Lam
 * @author Joseph Wong
 */
public final class FileSystemHelper {

    /*
     * Not intended to be instantiated.
     */
    private FileSystemHelper() {
    }
    
    /**
     * Determine if a file exists.
     * Workaround for java.io.File lack of support for path names > 260 chars in Windows.
     * 
     * Note: Creation of the following will correctly throw a FileNotFoundException:
     * - FileInputStream 
     * - FileReader
     * - RandomAccessFile 
     * 
     * if the file truly does not exist.
     * 
     * @param file a file object.  May be null, in which case false is returned.
     * @return whether the file exists.
     */
    public static boolean fileExists(File file) {
        /*
         * TODOEL: Only works for files.  Doesn't work with folders.  
         * For folders: File.canRead() or create a URL and try to read it?
         */
        if (file == null) {
            return false;
        }
        
        // We can just use the file.exists() method if the path name isn't too long.
        if (file.getAbsolutePath().length() < 255) {
            return file.exists();
        }

        // As far as I can tell, there are two ways to tell if a file with a too-long pathname exists.
        // 1) Create a FileInputStream, and see if it causes an exception.
        // 2) Create a RandomAccessFile, and see if it causes an exception.
        // Testing shows that, at the moment, 2) is faster.
        RandomAccessFile raFile = null;
        try {
            raFile = new RandomAccessFile(file, "r");
            return true;

        } catch (FileNotFoundException e) {

        } finally {
            if (raFile != null) {
                try {
                    raFile.close();
                } catch (IOException ioe) {
                }
            }
        }

        return false;
    }
    
    /**
     * Return the extension portion of the file's name .
     */
    public static String getExtension(File f) {
        if (f != null) {
            String filename = f.getName();
            int i = filename.lastIndexOf('.');
            if (i > 0 && i < filename.length() - 1) {
                return filename.substring(i + 1).toLowerCase();
            }
        }
        return null;
    }

    /**
     * Delete an entire directory tree.
     * @param f - the root of the tree.
     * @return - true if all deletions were successful, false otherwise.
     */
    public static boolean delTree (File f) {
        if (f.isDirectory()) {
            File[] files = f.listFiles();
            for (int i = 0; i < files.length; ++i) {
                if (!delTree(files[i])) {
                    return false;
                }
            }
        }
        
        return f.delete();
    }
    
    /**
     * Attempts to create the specified directory (and all its ancestors) if it does
     * not already exists. This method is intended as a workaround for
     * {@link File#mkdirs()}, which has a known race condition where it would unnecessary
     * fail in scenarios where:
     * <ul>
     * <li>mkdirs is called on the path /a/b
     * <li>the directory /a does not exist
     * <li>mkdirs attempts to create /a, but in the mean time some other thread (or process) creates it first
     * <li>mkdirs realizes that it failed to create /a (since it now exists), and decides to give up creating /a/b
     * </ul>
     * 
     * This bug is filed as Bug 4742723 in Sun's bug database:
     * <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4742723">
     * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4742723
     * </a>
     * 
     * @param f the directory
     * @return true if the directory exists or has been created successfully.
     */
    public static boolean ensureDirectoryExists(File f) {
        if (f.isDirectory()) {
            return true;
        }
        
        if (f.mkdir()) {
            return true;
        }

        // We obtain the canonical path so that we can obtain a proper
        // path for the parent (which we'll try to ensure exists)
        File canonicalFile;
        try {
            canonicalFile = f.getCanonicalFile();
            
        } catch (IOException e) {
            // getCanonicalFile() had some problems, but we really cannot say
            // that the directory does not exist (because it may have been created
            // by another process or thread), so the return value should be another
            // check of isDirectory()
            return f.isDirectory();
        }
        
        // The directory could not be made as is, so try
        // to create its parent (recursively) and then
        // try again
        File parent = canonicalFile.getParentFile();
        
        if (parent != null) {
            ensureDirectoryExists(parent);
        }
        
        // we tried our best creating the parent, so regardless
        // or whether that succeeded or not, we try creating
        // the requested directory again
        if (canonicalFile.mkdir()) {
            return true;
        } else {
            // the final mkdir() attempt "failed", but that could
            // mean that the directory really exists, so the
            // return value should be another check of isDirectory()
            return f.isDirectory();
        }
    }
    
    
    /**
     * Returns a subfolder in the user's "Application Data" folder with the
     * given name. The folder will be created if it does not already exists. If
     * the clean flag is set and the folder already exists it will be deleted
     * first and then recreated.
     * 
     * @param folderName The name of the folder to create.
     * @param clean True means the folder will be empty. False means existing data will be preserved. 
     * @return The directory File.
     * @throws BusinessObjectsException
     */
    public static File getApplicationDataFolder(String folderName, boolean clean) throws BusinessObjectsException {
        
        // Get the system user.home property.
        String userHomePath = System.getProperty("user.home");
        if (userHomePath == null) {
            throw new BusinessObjectsException(
                    "Could not find java.home system property.");
        }
        
        // Create the file object for the application's data folder.
        File userHome = new File(userHomePath);
        File appFolder = new File(userHome, "Application Data/"+folderName);
        
        // Clean the folder if requested.
        if (appFolder.exists() && clean) {
            delTree(appFolder);
        }
        
        // Ensure the the folder exists.
        if (!FileSystemHelper.ensureDirectoryExists(appFolder)) {
            throw new BusinessObjectsException("Could not create folder "+appFolder);
        }
        
        return appFolder;
    }

}
