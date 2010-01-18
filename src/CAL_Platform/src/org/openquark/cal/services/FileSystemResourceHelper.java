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
 * FileSystemResourceHelper.java
 * Creation date: Jul 23, 2004.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;

import javax.swing.filechooser.FileFilter;

import org.openquark.util.ClassInfo;
import org.openquark.util.FileSystemHelper;
import org.openquark.util.Pair;
import org.openquark.util.ui.ExtensionFileFilter;



/**
 * A static helper class which contains various utility methods used in the manipulation of module resources
 * with respect to the file system.
 * @author Edward Lam
 */
public class FileSystemResourceHelper {

    /*
     * Not intended to be instantiated.
     */
    private FileSystemResourceHelper() {
    }
    
    /**
     * Converts a desired filename into an a name for use in the file system by
     * preceding all capital letters with hyphens. This is needed because not
     * all filesystems are case-sensitive (ie: Windows), but CAL is case sensitive.
     * @param name the name to convert
     * @return the valid metadata file name
     */
    public static String getFileSystemName(String name) {
        
        StringBuilder fileName = new StringBuilder();
        
        int length = name.length();
        for (int i = 0; i < length; i++) {

            char c = name.charAt(i);
            
            if (Character.isUpperCase(c)) {
                fileName.append("-" + c);
            } else {
                fileName.append(c);
            }
        }
        
        return fileName.toString();
    }

    /**
     * Converts the file system name to the actual name used.
     * @param fileName the name used by the file system.
     * @return the case-sensitive name.
     */
    public static String fromFileSystemName(String fileName) {
        StringBuilder name = new StringBuilder();
        
        boolean nextCharUpper = false;  // Keeps track of whether the next character should be upper case.

        int length = fileName.length();
        for (int i = 0; i < length; i++) {

            char c = fileName.charAt(i);
            if (c == '-') {
                nextCharUpper = true;

            } else {
                if (nextCharUpper) {
                    name.append(Character.toUpperCase(c));
                } else {
                    name.append(Character.toLowerCase(c));
                }
                
                nextCharUpper = false;
            }
        }
        
        return name.toString();
    }

    /**
     * Get the default filechooser file filter for workspace files.
     * @return FileFilter a file filter for workspace files.
     */
    public static FileFilter getDefaultFileFilter() {
        return new ExtensionFileFilter("cws", "Workspace files");
    }

    /**
     * Delete a file or directory tree starting at some root.
     * @param rootFile the file or directory to delete.  If this is a directory, subdirectories will also be deleted.
     * @param status the tracking status object.
     * @return whether the deletion was successful.
     */
    public static boolean delTree(File rootFile, Status status) {

        // If the root file is a directory, empty it..
        delSubtrees(rootFile, status);
        
        // Attempt to delete the file or directory.
        boolean rootFileDeleted = rootFile.delete();
        if (!rootFileDeleted) {
            String fileTypeString = rootFile.isFile() ? "file" : "directory";
            status.add(new Status(Status.Severity.ERROR, "Could not delete " + fileTypeString + ": " + rootFile, null));
        }

        return rootFileDeleted;
    }
    
    /**
     * If a given file is a directory, empty it (delete its contents).
     * @param rootFile the root file or directory.
     * @param status the tracking status object.
     */
    public static void delSubtrees(File rootFile, Status status) {
        // If the root is a directory, get the files in the directory.
        File[] rootDirFiles = rootFile.listFiles();
        
        if (rootDirFiles != null) {
            
            // Iterate over the files in the directory.
            for (final File dirFile : rootDirFiles) {
                
                // Delete its contents.
                delTree(dirFile, status);
            }
        }
    }

    /**
     * Get the file names of resources which reside in a folder.
     * This will be sorted.
     * 
     * @param folder the folder to search.
     * @return the file names of the resources.
     */
    public static List<String> getFileNamesInFolder(ResourcePath.Folder folder) {

        List<URL> resourceURLList = getResourceURLList(folder);
        
        // The list to be returned.
        List<String> folderResourceNameList = new ArrayList<String>();
        
        // Iterate over the resources with the given path.
        for (final URL folderURL : resourceURLList) {
            try {
                // Get the file for the given resource.  This should be a directory..
                URI folderURI = WorkspaceLoader.urlToUri(folderURL);
                
                // Check for a non-hierarchical (opaque) URI.
                // This is the case if the URI exists within a .jar file, and will cause the File constructor to throw an exception.
                // We don't handle this, as this is the *FileSystem* resource helper.
                if (folderURI.isOpaque()) {
                    CALWorkspace.SERVICES_LOGGER.log(Level.WARNING,
                                                     "Cannot get resources for a folder with a non-hierarchical URI.  URI: " + folderURI);
                    continue;
                }
                
                File folderFile = new File(folderURI);

                // Add the matching files from the directory.
                File[] directoryFiles = folderFile.listFiles();
                if (directoryFiles != null) {
                    for (final File element : directoryFiles) {
                        folderResourceNameList.add(element.getName());
                    }
                }

                
            } catch (RuntimeException e) {
                // Thrown by File constructor if the uri is not acceptable.
                CALWorkspace.SERVICES_LOGGER.log(Level.WARNING, "Exception getting resources for " + folderURL, e);
            }
        }
        
        Collections.sort(folderResourceNameList);
        return folderResourceNameList;
    }

    /**
     * Get the file names of resources which reside in the subtree rooted at the folder.
     * The returned list is not sorted.
     * 
     * @param folder the folder to search.
     * @param addSubfolderNames whether the returned list should contain entries for subfolders as well as files.
     * @return the pairs of (file folder, file name) of the resources.
     */
    public static List<Pair<ResourcePath.Folder, String>> getFileFolderNamePairsInSubtreeRootedAtFolder(ResourcePath.Folder folder, boolean addSubfolderNames) {

        List<URL> resourceURLList = getResourceURLList(folder);
        
        // The list to be returned.
        List<Pair<ResourcePath.Folder, String>> folderResourceNameList = new ArrayList<Pair<ResourcePath.Folder, String>>();
        
        // Iterate over the resources with the given path.
        for (final URL folderURL : resourceURLList) {
            
            try {
                // Get the file for the given resource.  This should be a directory..
                URI folderURI = WorkspaceLoader.urlToUri(folderURL);
                
                // Check for a non-hierarchical (opaque) URI.
                // This is the case if the URI exists within a .jar file, and will cause the File constructor to throw an exception.
                // We don't handle this, as this is the *FileSystem* resource helper.
                if (folderURI.isOpaque()) {
                    CALWorkspace.SERVICES_LOGGER.log(Level.WARNING,
                                                     "Cannot get resources for a folder with a non-hierarchical URI.  URI: " + folderURI);
                    continue;
                }
                
                File folderFile = new File(folderURI);

                // Add the matching files from the directory.
                getFileFolderNamePairsInDirectoryTree(folderFile, folder, folderResourceNameList, addSubfolderNames);
                
            } catch (RuntimeException e) {
                // Thrown by File constructor if the uri is not acceptable.
                CALWorkspace.SERVICES_LOGGER.log(Level.WARNING, "Exception getting resources for " + folderURL, e);
            }
        }
        
        return folderResourceNameList;
    }

    /**
     * Returns a list of URLs corresponding to the given folder resource path.
     * @param folder a folder resource path.
     * @return a list of URLs corresponding to the given folder resource path.
     */
    private static List<URL> getResourceURLList(ResourcePath.Folder folder) {
        // Get the path string.
        // Annoyingly, the argument to ClassLoader.getResources() must not start with a slash, unless loading from a jar.
        // However, ClassLoader.getResource() must start with a slash.
        String pathStringMinusSlash = folder.getPathStringMinusSlash();
        
        // Create a list of resources with and without leading slash.
        List<URL> resourceURLList = new ArrayList<URL>();
        try {
            for (Enumeration<URL> en = ClassInfo.getResources(pathStringMinusSlash + "/"); en.hasMoreElements(); ) {
                resourceURLList.add(en.nextElement());
            }
        } catch (IOException e) {
            // Thrown by ClassLoader.getResources();
            CALWorkspace.SERVICES_LOGGER.log(Level.WARNING, "Exception getting resources for " + pathStringMinusSlash, e);
        }
    
        try {
            for (Enumeration<URL> en = ClassInfo.getResources(folder.getPathString() + "/"); en.hasMoreElements(); ) {
                resourceURLList.add(en.nextElement());
            }
        } catch (IOException e) {
            // Thrown by ClassLoader.getResources();
            CALWorkspace.SERVICES_LOGGER.log(Level.WARNING, "Exception getting resources for " + folder.getPathString(), e);
        }
        return resourceURLList;
    }

    /**
     * Get the names representing the resources in a directory.  This will be sorted.
     * 
     * @param directory the directory to search.
     * @param fileExtension the file extension used to represent resources of the desired type.
     *   If null, any resource will match.
     * @param folder the resource folder corresponding to the directory searched.
     * @param pathMapper the path mapper to use to map paths back to resource names
     * @return the names representing the resources in the given directory.
     *   Never null, but may be empty if the given file is not a directory.
     */
    public static List<ResourceName> getDirectoryResourceNames(File directory, String fileExtension, ResourcePath.Folder folder, ResourcePathMapper pathMapper) {
        return getFilteredDirectoryResourceNames(directory, fileExtension, folder, pathMapper, null);
    }
    
    /**
     * Get a filtered list of the names representing the resources in a directory.  This will be sorted.
     * 
     * @param directory the directory to search.
     * @param fileExtension the file extension used to represent resources of the desired type.
     *   If null, any resource will match.
     * @param folder the resource folder corresponding to the directory searched.
     * @param pathMapper the path mapper to use to map paths back to resource names
     * @param filter the filter to use for determining which resource names to keep in the returned list.
     *   Null for no filter.
     * @return the names representing the resources in the given directory.
     *   Never null, but may be empty if the given file is not a directory.
     */
    public static List<ResourceName> getFilteredDirectoryResourceNames(File directory, final String fileExtension, ResourcePath.Folder folder, ResourcePathMapper pathMapper, ResourceName.Filter filter) {

        java.io.FileFilter fileFilter = new java.io.FileFilter() {

            public boolean accept(File pathname) {
                if (fileExtension == null) {
                    return true;
                }
                return pathname.getName().endsWith("." + fileExtension);
            }
        };
        File[] matchingFiles = fileExtension == null ? directory.listFiles() : directory.listFiles(fileFilter);
        if (matchingFiles == null) {
            return Collections.emptyList();
        }
        
        List<ResourceName> directoryResourceNames = new ArrayList<ResourceName>();
        for (final File matchingFile : matchingFiles) {
            String fileName = matchingFile.getName();
            
            // If the filter is not null, check the path to make sure it's acceptable first
            ResourceName directoryResourceName = pathMapper.getResourceNameFromFolderAndFileName(folder, fileName);
            
            // It passes if the path mapper returns a non-null resource name (e.g. it has the right file extension).
            if (directoryResourceName != null) {
                // If the filter is not null, check the path to make sure it's acceptable first
                if (filter == null || filter.accept(directoryResourceName)) {
                    directoryResourceNames.add(directoryResourceName);
                }
            }
        }
        
        Collections.sort(directoryResourceNames);
        return directoryResourceNames;
    }
    
    /**
     * Get the names representing the resources in the subtree rooted at the directory.  This will be sorted.
     * 
     * @param directory the directory to search.
     * @param fileExtension the file extension used to represent resources of the desired type.
     *   If null, any resource will match.
     * @param folder the resource folder corresponding to the directory searched.
     * @param pathMapper the path mapper to use to map paths back to resource names
     * @return the names representing the resources in the given directory.
     *   Never null, but may be empty if the given file is not a directory.
     */
    public static List<ResourceName> getResourceNamesInSubtreeRootedAtDirectory(File directory, final String fileExtension, ResourcePath.Folder folder, ResourcePathMapper pathMapper) {
        
        List<Pair<ResourcePath.Folder, String>> matchingFiles = new ArrayList<Pair<ResourcePath.Folder, String>>();
        getFileFolderNamePairsInDirectoryTree(directory, folder, matchingFiles, false);
        
        List<ResourceName> directoryResourceNames = new ArrayList<ResourceName>();
        for (int i = 0, n = matchingFiles.size(); i < n; i++) {
            Pair<ResourcePath.Folder, String> pair = matchingFiles.get(i);
            ResourcePath.Folder fileFolder = pair.fst();
            String fileName = pair.snd();
            
            // If the file extension is not null, check the path to make sure it's acceptable first
            if (fileExtension != null) {
                if (!fileName.endsWith("." + fileExtension)) {
                    continue;
                }
            }
            
            ResourceName directoryResourceName = pathMapper.getResourceNameFromFolderAndFileName(fileFolder, fileName);
            
            // It passes if the path mapper returns a non-null resource name (e.g. it has the right file extension).
            if (directoryResourceName != null) {
                directoryResourceNames.add(directoryResourceName);
            }
        }
        
        Collections.sort(directoryResourceNames);
        return directoryResourceNames;
    }
    
    /**
     * Get the names representing the resources in this folder.
     * 
     * @param folder the folder to search.
     * @return the names representing the resources in the given directory.
     *   Never null, but may be empty if the given file is not a directory.
     */
    public static List<ResourceName> getFolderResourceNames(ResourcePath.Folder folder, ResourcePathMapper pathMapper) {
        return getFilteredFolderResourceNames(folder, pathMapper, null);
    }
    
    /**
     * Get a filtered list of the names representing the resources in this folder.
     * 
     * @param folder the folder to search.
     * @param filter the filter to use for determining which resource names to keep in the returned list.
     * @return the names representing the resources in the given directory.
     *   Never null, but may be empty if the given file is not a directory.
     */
    public static List<ResourceName> getFilteredFolderResourceNames(ResourcePath.Folder folder, ResourcePathMapper pathMapper, ResourceName.Filter filter) {
        
        Set<ResourceName> filteredSet = new TreeSet<ResourceName>();        // sort alphabetically..
        for (final String fileName : getFileNamesInFolder(folder)) {
            ResourceName resourceName = pathMapper.getResourceNameFromFolderAndFileName(folder, fileName);
            
            // It passes if the path mapper returns a non-null resource name (e.g. it has the right file extension).
            if (resourceName != null) {
                // If the filter is not null, check the path to make sure it's acceptable first
                if (filter == null || filter.accept(resourceName)) {
                    filteredSet.add(resourceName);
                }
            }
        }
        
        return new ArrayList<ResourceName>(filteredSet);
    }
    
    /**
     * Get the names representing the resources in the subtree rooted at this folder.
     * 
     * @param folder the folder to search.
     * @return the names representing the resources in the given directory.
     *   Never null, but may be empty if the given file is not a directory.
     */
    public static List<ResourceName> getResourceNamesInSubtreeRootedAtFolder(ResourcePath.Folder folder, ResourcePathMapper pathMapper) {
        
        Set<ResourceName> filteredSet = new TreeSet<ResourceName>();        // sort alphabetically..
        for (final Pair<ResourcePath.Folder, String> pair :  getFileFolderNamePairsInSubtreeRootedAtFolder(folder, false)) {
            ResourcePath.Folder fileFolder = pair.fst();
            String fileName = pair.snd();
            
            ResourceName resourceName = pathMapper.getResourceNameFromFolderAndFileName(fileFolder, fileName);
            
            // It passes if the path mapper returns a non-null resource name (e.g. it has the right file extension).
            if (resourceName != null) {
                filteredSet.add(resourceName);
            }
        }
        
        return new ArrayList<ResourceName>(filteredSet);
    }

    /**
     * Get the subdirectories of a given directory which contain any files with a given extension.
     * @param parentDirectory the folder whose subfolders will be searched.  The names of these subfolders
     *   are eligible to be returned.
     * @param fileExtension the file extension to search for.  If null, any file will match.
     * @param convertFromFileSystemName whether the folder names are encoded with getFileSystemName(), and should be decoded.
     * @return the names of folders for which files with the given extension exist.
     */
    public static Set<String> getDirectoryNamesWithFileExtension(File parentDirectory, String fileExtension, boolean convertFromFileSystemName) {
        Set<String> subdirectoryNameSet = new HashSet<String>();
        
        List<File> subDirectories = Arrays.asList(parentDirectory.listFiles());

      subFolderName:
        
        for (final File subDirectory : subDirectories) {
            String subFolderName = subDirectory.getName();
            String convertedName = convertFromFileSystemName ? fromFileSystemName(subFolderName) : subFolderName;

            File subdirectory = new File(parentDirectory, subFolderName);
            Set<String> filesInSubdirectoryTree = new HashSet<String>();
            getFilesInDirectoryTree(subdirectory, filesInSubdirectoryTree);
            
            for (String fileName : filesInSubdirectoryTree) {
                if (convertFromFileSystemName) {
                    fileName = fromFileSystemName(fileName);
                }
                if (fileExtension == null || fileName.endsWith(fileExtension)) {
                    subdirectoryNameSet.add(convertedName);
                    continue subFolderName;
                }
            }
        }
        
        return subdirectoryNameSet;
    }
    
    /**
     * Returns a set of folder resource paths of all non-empty subdirectories in the given folder.
     * @param parentFolder the folder resource path of the root of the subtree to process.
     * @param parentDirectory the file-system directory corresponding to the folder.
     * @param pathMapper the path mapper to use for mapping paths.
     * @return a set of folder resource paths of all non-empty subdirectories in the given folder.
     */
    public static Set<ResourcePath.Folder> getResourcePathsOfAllNonEmptySubdirectories(ResourcePath.Folder parentFolder, File parentDirectory, ResourcePathMapper pathMapper) {
        
        Set<ResourcePath.Folder> result = new HashSet<ResourcePath.Folder>();
        
        List<Pair<ResourcePath.Folder, String>> subfolderParentNamePairs = new ArrayList<Pair<ResourcePath.Folder, String>>();
        
        getFileFolderNamePairsInDirectoryTree(parentDirectory, parentFolder, subfolderParentNamePairs, true);
        
        for (final Pair<ResourcePath.Folder, String> pair :  subfolderParentNamePairs) {
            ResourcePath.Folder subfolderParent = pair.fst();
            String subfolderName = pair.snd();
            
            ResourcePath.Folder subfolder = subfolderParent.extendFolder(subfolderName);
            
            if (!getFolderResourceNames(subfolder, pathMapper).isEmpty()) {
                result.add(subfolder);
            }
        }
        
        return result;
    }
    
    /**
     * Adds to the given Set a string containing the full path name for each
     * descendent of the given directory
     * 
     * @param directory The root directory which contains the files to be added
     * @param fileSet The set to which all files will be added
     */
    public static void getFilesInDirectoryTree(File directory, Set<String> fileSet) {
        
        File[] fileList = directory.listFiles();
        for (final File element : fileList) {
            if (element.isDirectory()) {
                getFilesInDirectoryTree(element, fileSet);

            } else if (element.isFile()) {
                fileSet.add(element.getAbsolutePath());
            }
        }
    }
    
    /**
     * Adds to the given Set a string containing a pair (file folder, file name) for each
     * descendent of the given directory
     * 
     * @param directory The root directory which contains the files to be added
     * @param fileCollection The collection to which all files will be added
     * @param addSubfolderNames whether the returned list should contain entries for subfolders as well as files.
     */
    public static void getFileFolderNamePairsInDirectoryTree(File directory, ResourcePath.Folder folder, Collection<Pair<ResourcePath.Folder, String>> fileCollection, boolean addSubfolderNames) {
        
        File[] fileList = directory.listFiles();
        for (final File element : fileList) {
            if (element.isDirectory()) {
                getFileFolderNamePairsInDirectoryTree(element, folder.extendFolder(element.getName()), fileCollection, addSubfolderNames);
                
                if (addSubfolderNames) {
                    fileCollection.add(new Pair<ResourcePath.Folder, String>(folder, element.getName()));
                }

            } else if (element.isFile()) {
                fileCollection.add(new Pair<ResourcePath.Folder, String>(folder, element.getName()));
            }
        }
    }

    /**
     * Strip the file extension from a file name.
     * @param fileName the name of the file in question.
     * @param fileExtension the file extension to strip from the name.  If null, the filename will be returned.
     * @param fromFileSystemName whether to additionally convert the name from an encoded file system name.
     * @return fileName minus the file extension, or null if fileName does not have the given file extension.
     */
    public static String stripFileExtension(String fileName, String fileExtension, boolean fromFileSystemName) {
        
        if (fileExtension == null) {
            return fromFileSystemName ? FileSystemResourceHelper.fromFileSystemName(fileName) : fileName;
        }
        
        // Prefix the file extension with the period.
        fileExtension = "." + fileExtension;

        // Remove the extension, if any.
        if (fileName.endsWith(fileExtension)) {
            String truncatedFileName =  fileName.substring(0, fileName.length() - fileExtension.length());
            return fromFileSystemName ? FileSystemResourceHelper.fromFileSystemName(truncatedFileName) : truncatedFileName;
        }
        
        return null;
    }

    /**
     * Get the file which corresponds to the given path
     * @param resourcePath the resource path.
     * @param rootDirectory the root directory for resources using resource paths.
     * @param createDirectoryIfAbsent whether to create the directory to the file if it does not exist.
     * @return the file which corresponds to that path.
     */
    public static File getResourceFile(ResourcePath resourcePath, File rootDirectory, boolean createDirectoryIfAbsent) {
        File file = new File(rootDirectory, resourcePath.getPathString());

        // Create the file if that's what we need to do.
        if (createDirectoryIfAbsent && !file.exists()) {
            File fileFolder;
            if (resourcePath instanceof ResourcePath.Folder) {
                fileFolder = file;
                
            } else if (resourcePath instanceof ResourcePath.FilePath) {
                fileFolder = file.getParentFile();

            } else {
                throw new IllegalArgumentException("Unknown ResourcePath type: " + resourcePath.getClass().getName());
            }
            
            if (!FileSystemHelper.ensureDirectoryExists(fileFolder)) {
                CALWorkspace.SERVICES_LOGGER.log(Level.WARNING, "Could not create directory: " + fileFolder);
            }
            
        }
        return file;
    }
    
    /**
     * Copies all the data from the input stream to the output stream.
     */
    public static void transferData(InputStream inputStream, OutputStream outputStream) throws IOException {
        // Note: FileInputStream returns 0 bytes on read() if the path name is too long.
        //   Here we avoid this by converting to a channel.
        
        byte [] bufferArray = new byte [4096];
        ByteBuffer buffer = ByteBuffer.wrap(bufferArray);
        ReadableByteChannel channel = Channels.newChannel(inputStream);
        
        int nRead = 0;
        while ((nRead = channel.read(buffer)) != -1) {
            outputStream.write(bufferArray, 0, nRead);
            buffer.rewind(); // rewind the buffer so that the next read starts at position 0 again
        }
    }

}

