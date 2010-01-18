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
 * NullaryEnvironment.java
 * Creation date: Dec 21, 2005.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

import org.openquark.util.FileSystemHelper;



/**
 * A nullary environment encapsulates the details of finding resources relative to the current classpath.
 * Resources in this environment are accessed by means of ResourcePaths, which represent relative paths.
 * 
 * @author Edward Lam
 */
public abstract class NullaryEnvironment {
    
    /** Singleton concrete instance of the current nullary environment. */
    private static final NullaryEnvironment NULLARY_ENVIRONMENT =
        WorkspaceLoader.getWorkspaceProviderFactory().createCALWorkspaceProvider(null).getNullaryEnvironment();

    /**
     * @return the nullary environment for the current execution environment.
     */
    public static NullaryEnvironment getNullaryEnvironment() {
        return NULLARY_ENVIRONMENT;
    }
    
    /**
     * Get the names representing the resources in this folder.
     * 
     * @param folder the folder to search.
     * @return the names representing the resources in the given directory.
     *   Never null, but may be empty if the given file is not a directory.
     */
    public abstract List<ResourceName> getFolderResourceNames(ResourcePath.Folder folder, ResourcePathMapper mapper);
    
    /**
     * Get the names representing the resources in the subtree rooted at this folder.
     * 
     * @param folder the folder to search.
     * @return the names representing the resources in the given directory.
     *   Never null, but may be empty if the given file is not a directory.
     */
    public abstract List<ResourceName> getResourceNamesInSubtreeRootedAtFolder(ResourcePath.Folder folder, ResourcePathMapper mapper);
    
    /**
     * Get a filtered list of the names representing the resources in this folder.
     * 
     * @param folder the folder to search.
     * @param filter the filter to use for determining which resource names to keep in the returned list.
     * @return the names representing the resources in the given directory.
     *   Never null, but may be empty if the given file is not a directory.
     */
    public abstract List<ResourceName> getFilteredFolderResourceNames(ResourcePath.Folder folder, ResourcePathMapper pathMapper, ResourceName.Filter filter);

    /**
     * @param resourcePath the path for which to return a file.
     * @param createDirectoryIfAbsent whether to create the directory to the file if it does not exist.
     * @return the file corresponding to this resource path.  If createDirectoryIfAbsent is false, the file does not exist,
     * and the directory for this file does not exist, null will be returned.
     */
    public abstract File getFile(ResourcePath resourcePath, boolean createDirectoryIfAbsent);

    /**
     * Delete a file or folder tree starting at some root.
     * @param resourcePath the file or directory to delete.  If this is a directory, subdirectories will also be deleted.
     * @param status the tracking status object.
     * @return whether the deletion was successful.
     */
    public boolean delTree(ResourcePath resourcePath, Status status) {
        return FileSystemResourceHelper.delTree(getFile(resourcePath, false), status);
    }

    /**
     * Empty a folder (delete its contents).
     * @param rootFolder the root folder to delete.
     * @param status the tracking status object.
     */
    public void delSubtrees(ResourcePath.Folder rootFolder, Status status) {
        FileSystemResourceHelper.delSubtrees(getFile(rootFolder, false), status);
    }
    
    /**
     * This function calls getFile() using the baseFilePath argument, and then determines the base folder from
     * the return value and returns the file corresponding to newFilePath but contained within the subtree rooted
     * at this base folder.
     * 
     * This method can be used to rename a file while ensuring that it remains within the same base directory.
     * 
     * @param baseFilePath the FilePath from which to determine the base folder.
     * @param newFilePath the FilePath for which to return a file.
     * @param createDirectoryIfAbsent whether to create the directory to the file if it does not exist.
     * @return The file corresponding to this resource path, using the
     */
    public File getFile(ResourcePath.FilePath baseFilePath, ResourcePath.FilePath newFilePath, boolean createDirectoryIfAbsent) {
        
        File oldFile = getFile(baseFilePath, false);
        
        // Get the "root" directory for the old file.
        File baseFolder = oldFile.getAbsoluteFile();
        for (int i = 0, n = baseFilePath.getPathElements().length; i < n; i++) {
            baseFolder = baseFolder.getParentFile();
        }

        return FileSystemResourceHelper.getResourceFile(newFilePath, baseFolder, createDirectoryIfAbsent);
    }

    /**
     * Get the subfolders of a given folder which contain any files with a given extension.
     * @param parentFolder the folder whose subfolders will be searched.  The names of these subfolders
     *   are eligible to be returned.
     * @param pathMapper the path mapper whose file extension is the search criteria.
     * @param convertFromFileSystemName whether the folder names are encoded with getFileSystemName(), and should be decoded.
     * @return the names of folders for which files with the given extension exist.
     */
    public abstract Set<String> getFolderNamesWithFileExtension(ResourcePath.Folder parentFolder, ResourcePathMapper pathMapper, boolean convertFromFileSystemName);
    
    /**
     * Returns a set of folder resource paths of all non-empty subfolders in the given folder.
     * @param parentFolder the folder resource path of the root of the subtree to process.
     * @param pathMapper the path mapper to use for mapping paths.
     * @return a set of folder resource paths of all non-empty subfolders in the given folder.
     */
    public abstract Set<ResourcePath.Folder> getResourcePathsOfAllNonEmptySubfolders(ResourcePath.Folder parentFolder, ResourcePathMapper pathMapper);

    /**
     * @return whether the file or directory corresponding to this path can be located.
     */
    public boolean exists(ResourcePath resourcePath) {
        File file = getFile(resourcePath, false);
        
        if (resourcePath instanceof ResourcePath.FilePath) {
            // A file.
            return FileSystemHelper.fileExists(file);
        } else {
            // Must be a folder.
            return file != null && file.isDirectory();
        }
    }
    
    /**
     * @param filePath a file whose contents to retrieve.
     * @param status the tracking status object.
     * @return the contents of the file, or null if the file could not be found or read.
     */
    public InputStream getInputStream(ResourcePath.FilePath filePath, Status status) {
        File file = getFile(filePath, false);
        if (file == null) {
            String message = "Could not find a resource at " + filePath.getPathString();
            status.add(new Status(Status.Severity.INFO, message));
            return null;
        }
        
        try {
            return new FileInputStream(file);
        
        } catch (FileNotFoundException e) {
            String message = "Could not find a resource at " + filePath.getPathString();
            status.add(new Status(Status.Severity.INFO, message, e));
            return null;
        }
    }
    
    /**
     * @param filePath a file in the nullary environment.
     * @return debugging information about the resource, e.g. the actual location of the resource. Can be null if the resource does not exist.
     */
    public String getDebugInfo(ResourcePath.FilePath filePath) {
        File file = getFile(filePath, false);
        if (file == null) {
            return null;
        } else {
            return "from nullary file: " + file.getAbsolutePath();
        }
    }
    
    /**
     * @return a Set of File objects representing the Car-jar files in the environment (e.g. on the classpath).
     */
    public abstract Set<File> getCarJarFilesInEnvironment();
}
