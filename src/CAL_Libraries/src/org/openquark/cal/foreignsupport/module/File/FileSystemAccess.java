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
 * FileSystemAccess.java
 * Creation date: Jun 30, 2005.
 * By: Joseph Wong
 */

package org.openquark.cal.foreignsupport.module.File;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.openquark.util.FileSystemHelper;
import org.openquark.util.WildcardPatternMatcher;
import org.openquark.util.time.Time;


/**
 * This class contains foreign support methods for the File module, providing access
 * to the file system: manipulating and querying directories.
 * 
 * @author Richard Webster
 * @author Joseph Wong
 */          
public final class FileSystemAccess {
    
    /**
     * Returns a list of abstract pathnames denoting the files in the directory
     * denoted by the specified abstract pathname, filtered by the specified
     * file name pattern.
     * 
     * If the specified abstract pathname does not denote a directory, then this
     * method returns an empty list. Otherwise a list of File objects is
     * returned, one for each file in the directory, and if the filesOnly
     * argument is false, one for each subdirectory in the directory as well.
     * Pathnames denoting the directory itself and the directory's parent
     * directory are not included in the result. If the specified pathname is
     * absolute then each resulting pathname is absolute; if the specified
     * pathname is relative then each resulting pathname will be relative to the
     * same directory.
     * 
     * There is no guarantee that the name strings in the resulting array will
     * appear in any specific order; they are not, in particular, guaranteed to
     * appear in alphabetical order.
     * 
     * The file name pattern admits the use of the wildcard characters * and ?.
     * The wildcard * stands for 0 or more characters, and the wildcard ?
     * stands for a single character.
     * 
     * @param directory
     *            the directory whose contents are to be listed.
     * @param fileNamePattern
     *            the file name pattern used to filter the file names to be
     *            returned
     * @param filesOnly
     *            whether the returned list should contain only files. If false,
     *            the returned list will contain files as well as
     *            subdirectories.
     * @return A list of abstract pathnames denoting the files and directories
     *         in the directory denoted by the specified abstract pathname. The
     *         list will be empty if the directory is empty, or if the abstract
     *         pathname does not denote a directory, or if an I/O error occurs.
     */
    public static List<String> getFilteredDirectoryContents(File directory, String fileNamePattern, final boolean filesOnly) {
        
        final Pattern pattern = WildcardPatternMatcher.getPattern(fileNamePattern);
        
        File[] matchingFiles = directory.listFiles(new FileFilter () {
            public boolean accept(File pathname) {
                // Reject child directories if filesOnly is true.
                if (filesOnly && !pathname.isFile()) {
                    return false;
                }
                return pattern == null || pattern.matcher(pathname.getName()).matches();
            }
        });
        
        List<String> fileNameList = new ArrayList<String>();
        if (matchingFiles != null) {
            for (int fileN = 0, nFiles = matchingFiles.length; fileN < nFiles; ++fileN) {
                File matchingFile = matchingFiles[fileN];
                fileNameList.add(matchingFile.getAbsolutePath());
            }
        }
        return fileNameList;
    }
    
    /**
     * Returns a list of abstract pathnames denoting the files in the directory
     * denoted by the specified abstract pathname.
     * 
     * If the specified abstract pathname does not denote a directory, then this
     * method returns an empty list. Otherwise a list of File objects is
     * returned, one for each file or directory in the directory. Pathnames
     * denoting the directory itself and the directory's parent directory are
     * not included in the result. If the specified pathname is absolute then
     * each resulting pathname is absolute; if the specified pathname is
     * relative then each resulting pathname will be relative to the same
     * directory.
     * 
     * There is no guarantee that the name strings in the resulting array will
     * appear in any specific order; they are not, in particular, guaranteed to
     * appear in alphabetical order.
     * 
     * @return A list of abstract pathnames denoting the files and directories
     *         in the directory denoted by the specified abstract pathname. The
     *         list will be empty if the directory is empty, or if the abstract
     *         pathname does not denote a directory, or if an I/O error occurs.
     */
    public static List<File> getDirectoryContents(File dirName) {
        File[] files = dirName.listFiles();
        if (files == null) {
            return Collections.emptyList();
        } else {
            return Arrays.asList(files);
        }
    }    
    
    /**
     * Returns the last modification time of the specified file or directory.
     * @param fileName the name of the file.
     * @return the last modification time of the specified file or directory.
     */
    public static Time getModificationTime(File fileName) {
        return Time.fromDate(new Date(fileName.lastModified()));
    }
    
    /**
     * Delete an entire directory tree.
     * @param dirName the root of the tree to be deleted.
     * @return true if all deletions were successful; false otherwise
     */
    public static boolean deleteDirectoryTree(File dirName) {
        return FileSystemHelper.delTree(dirName);
    }
}