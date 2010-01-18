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
 * BasicNullaryEnvironment.java
 * Creation date: Jan 9, 2006.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.logging.Level;

import org.openquark.util.ClassInfo;
import org.openquark.util.FileSystemHelper;
import org.openquark.util.Pair;



/**
 * The nullary environment for the basic cal workspace.
 * @author Edward Lam
 */
class BasicNullaryEnvironment extends NullaryEnvironment {
    
    /**
     * Constructor for a BasicNullaryEnvironment.
     */
    BasicNullaryEnvironment() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ResourceName> getFolderResourceNames(ResourcePath.Folder folder, ResourcePathMapper mapper) {
        return FileSystemResourceHelper.getFolderResourceNames(folder, mapper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ResourceName> getResourceNamesInSubtreeRootedAtFolder(ResourcePath.Folder folder, ResourcePathMapper mapper) {
        return FileSystemResourceHelper.getResourceNamesInSubtreeRootedAtFolder(folder, mapper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ResourceName> getFilteredFolderResourceNames(ResourcePath.Folder folder, ResourcePathMapper pathMapper, ResourceName.Filter filter) {
        return FileSystemResourceHelper.getFilteredFolderResourceNames(folder, pathMapper, filter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getFile(ResourcePath resourcePath, boolean createDirectoryIfAbsent) {
        if (resourcePath instanceof ResourcePath.Folder) {
            return getFile((ResourcePath.Folder)resourcePath, createDirectoryIfAbsent);
            
        } else if (resourcePath instanceof ResourcePath.FilePath) {
            return getFile((ResourcePath.FilePath)resourcePath, createDirectoryIfAbsent);

        } else {
            throw new IllegalArgumentException("Unknown ResourcePath type: " + resourcePath.getClass().getName());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getFolderNamesWithFileExtension(ResourcePath.Folder parentFolder, ResourcePathMapper pathMapper, boolean convertFromFileSystemName) {
        Set<String> subFolderNameSet = new HashSet<String>();
        
        List<String> subFolderNames = FileSystemResourceHelper.getFileNamesInFolder(parentFolder);
        
        String fileExtensionOfPathMapper = CALSourcePathMapper.CAL_FILE_EXTENSION;

      subFolderName:
        
        for (final String subFolderName : subFolderNames) {
            String moduleName = convertFromFileSystemName ? FileSystemResourceHelper.fromFileSystemName(subFolderName) : subFolderName;
            ResourcePath.Folder subFolder = parentFolder.extendFolder(subFolderName);
            
            // Check whether the module folder has a file with the given extension.
            if (!getFolderResourceNames(subFolder, pathMapper).isEmpty()) {
                subFolderNameSet.add(moduleName);
                continue;
            }
            
            // Check whether any of the files in the folder tree have a file with the given extension.
            List<String> subFolderResourceNames = FileSystemResourceHelper.getFileNamesInFolder(subFolder);
            for (final String string : subFolderResourceNames) {
                ResourcePath.Folder subFolderResourceFolder = subFolder.extendFolder(string);
                File subFolderResourceDirectory = getFile(subFolderResourceFolder, false);
                
                if (subFolderResourceDirectory != null && subFolderResourceDirectory.isDirectory()) {
                    Set<String> filesInModuleTree = new HashSet<String>();
                    FileSystemResourceHelper.getFilesInDirectoryTree(subFolderResourceDirectory, filesInModuleTree);
                    
                    for (String fileName : filesInModuleTree) {                        
                        if (convertFromFileSystemName) {
                            fileName = FileSystemResourceHelper.fromFileSystemName(fileName);
                        }
                        if (fileExtensionOfPathMapper == null || fileName.endsWith(fileExtensionOfPathMapper)) {
                            subFolderNameSet.add(moduleName);
                            continue subFolderName;
                        }
                    }
                }
            }
        }
        
        return subFolderNameSet;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Set<ResourcePath.Folder> getResourcePathsOfAllNonEmptySubfolders(ResourcePath.Folder parentFolder, ResourcePathMapper pathMapper) {
        
        Set<ResourcePath.Folder> result = new HashSet<ResourcePath.Folder>();
        
        List<Pair<ResourcePath.Folder, String>> subfolderParentNamePairs =
            FileSystemResourceHelper.getFileFolderNamePairsInSubtreeRootedAtFolder(parentFolder, true);
        
        for (final Pair<ResourcePath.Folder, String> pair : subfolderParentNamePairs) {
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
     * @param folder the folder for which to return a file.
     * @param createDirectoryIfAbsent whether to create the directory to the file if it does not exist.
     * @return the file corresponding to this resource path.  If createDirectoryIfAbsent is false, the file does not exist,
     * and the directory for this file does not exist, null will be returned.
     */
    private static File getFile(ResourcePath.Folder folder, boolean createDirectoryIfAbsent) {
        URL resourceURL = getURL(folder);
        
        // Check whether the resource does not currently exist.
        if (resourceURL != null) {
            URI resourceURI = WorkspaceLoader.urlToUri(resourceURL);
            if (!resourceURI.isOpaque()) {
                // not an opaque URI means it should refer to a regular file
                return new File(resourceURI);

            } else {
                // the opaque URI most likely refers to a jar, so skip it and try to find another one that refers to a regular file
                Enumeration<URL> resourceURLs = getURLs(folder);
                while (resourceURLs.hasMoreElements()) {
                    URL url = resourceURLs.nextElement();
                    URI uri = WorkspaceLoader.urlToUri(url);

                    if (uri.isOpaque()) {
                        continue; // skip if the URI is opaque
                    }

                    try {
                        return new File(uri);
                    } catch (IllegalArgumentException e) {
                        continue; // this URI does not represent a physical file, so skip it
                    }
                }
            }
        }

        if (!createDirectoryIfAbsent) {
            return null;
        }

        // Create the directory..
        return ensurePathExists(folder);
    }

    /**
     * @param filePath the FilePath for which to return a file.
     * @param createDirectoryIfAbsent whether to create the directory to the file if it does not exist.
     * @return the file corresponding to this resource path.  If createDirectoryIfAbsent is false, the file does not exist,
     * and the directory for this file does not exist, null will be returned.
     */
    private static File getFile(ResourcePath.FilePath filePath, boolean createDirectoryIfAbsent) {
        
        // If the file exists, return it.
        URL resourceURL = getURL(filePath);
        if (resourceURL != null) {
            URI resourceURI = WorkspaceLoader.urlToUri(resourceURL);
            
            if (!resourceURI.isOpaque()) {
                // not an opaque URI means it should refer to a regular file
                return new File(resourceURI);
                
            } else {
                // the opaque URI most likely refers to a jar, so skip it and try to find another one that refers to a regular file
                Enumeration<URL> resourceURLs = getURLs(filePath);
                while (resourceURLs.hasMoreElements()) {
                    URL url = resourceURLs.nextElement();
                    URI uri = WorkspaceLoader.urlToUri(url);
                    
                    if (uri.isOpaque()) {
                        continue; // skip if the URI is opaque
                    }
                    
                    try {
                        return new File(uri);
                    } catch (IllegalArgumentException e) {
                        continue; // this URI does not represent a physical file, so skip it
                    }
                }
            }
        }
    
        // If here, the file does not exist.  Determine the appropriate folder.
        File folderFile = getFile(filePath.getFolder(), createDirectoryIfAbsent);
        if (folderFile == null) {
            return null;
        }
    
        // Return a file object in the folder.
        String[] pathElements = filePath.getPathElements();
        String fileName = pathElements[pathElements.length - 1];
        
        return new File(folderFile, fileName);
    }
    
    /**
     * Ensure that the directory corresponding to a folder exists on the file system.
     * @param folder the folder in question.
     * @return the corresponding directory, or null if a corresponding directory does not exist and could not be created.
     *   If this is a file, the path to the directory will be created (if non-existent) but the file will not be created.
     */
    private static File ensurePathExists(ResourcePath.Folder folder) {
        
        String[] pathElements = folder.getPathElements();
        
        // Find the maximal sub path which already exists.
        for (int i = pathElements.length - 1; i >= -1; i--) {
            
            int nSubPathElements = i + 1;
            
            // Construct /pathElem1/pathElem2/.../pathElemi/
            String[] subPathElements = new String[nSubPathElements];
            System.arraycopy(pathElements, 0, subPathElements, 0, nSubPathElements);
            
            ResourcePath.Folder subPath = new ResourcePath.Folder(subPathElements);
            
            URL subPathURL = getURL(subPath);
            if (subPathURL != null) {
                // If here, subPathURL corresponds to the maximal sub path which already exists.
    
                URI subPathURI = WorkspaceLoader.urlToUri(subPathURL);
                if (subPathURI == null) {
                    CALWorkspace.SERVICES_LOGGER.log(Level.SEVERE, "Error getting directory from path: " + subPath);
                    return null;
                }
                
                // Get the file which corresponds to the given subpath.
                File subPathDir = new File(subPathURI);
                
                StringBuilder remainingPathStringBuilder = new StringBuilder("/");
                for (int j = nSubPathElements; j < pathElements.length; j++) {
                    remainingPathStringBuilder.append(pathElements[j] + "/");
                }
                
                File wholePathDir = new File(subPathDir, remainingPathStringBuilder.toString());
                boolean directoryExists = FileSystemHelper.ensureDirectoryExists(wholePathDir);
    
                if (!directoryExists) {
                    // For some reason, the directory doesn't exist, and couldn't be created.
                    break;
                }
    
                return wholePathDir;
            }
        }
        
        // If here, we couldn't even get the resource for "/".
        return null;
    }

    /**
     * Get the resource corresponding to this resource path, treating the resource path as a resource locator
     *   relative to the system class path.
     * @return the corresponding url, if the resource path exists in the system class path,
     *   or null if the path does not correspond to a locatable resource.
     */
    private static URL getURL(ResourcePath resourcePath) {
        // Determine if the file exists.
        // The leading slash is required if the resource is in a .jar.
        // The leading slash is prohibited if the resource is a file on the classpath in the file system.
        
        String pathString = resourcePath.getPathStringMinusSlash();
        URL url = ClassInfo.getResource(pathString);
        if (url != null) {
            return url;
        }
        
        pathString = resourcePath.getPathString();
        return ClassInfo.getResource(pathString);
    }

    /**
     * Get all the resources corresponding to this resource path, treating the resource path as a resource locator
     *   relative to the system class path.
     * @return an Enumeration of the corresponding urls, if the resource path exists in the system class path,
     *   or an empty enumeration if the path does not correspond to a locatable resource.
     */
    private static Enumeration<URL> getURLs(ResourcePath resourcePath) {
        // Determine if the file exists.
        // The leading slash is required if the resource is in a .jar.
        // The leading slash is prohibited if the resource is a file on the classpath in the file system.
        
        String pathString = resourcePath.getPathStringMinusSlash();
        try {
            Enumeration<URL> urls = ClassInfo.getResources(pathString);
            if (urls.hasMoreElements()) {
                return urls;
            }
        } catch (IOException e) {
            // we fallback to the second attempt below
        }
        
        pathString = resourcePath.getPathString();
        try {
            return ClassInfo.getResources(pathString);
        } catch (IOException e) {
            // return an empty enumeration
            return new Enumeration<URL>() {
                public boolean hasMoreElements() { return false; }
                public URL nextElement() { throw new NoSuchElementException("Empty enumeration"); }
            };
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<File> getCarJarFilesInEnvironment() {
        
        // The jar files in the environment are those on the classpath. We require
        // that the jar files be well-formed, with a manifest file in the appropriate location.
        
        // We look for jars by finding all the locatable manifest files, and then deconstructing the
        // resource URIs to obtain the underlying jar files.
        
        Enumeration<URL> en;
        try {
            en = ClassInfo.getResources(CarBuilder.CAR_MARKER_NAME);
        } catch (IOException e) {
            en = null;
        }
        
        Set<File> carJarFilesOnClasspath = new HashSet<File>();
        
        if (en != null) {
            while (en.hasMoreElements()) {
                URL url = en.nextElement();
                URI uri = WorkspaceLoader.urlToUri(url);
                
                if (uri.isOpaque() && uri.getScheme().equals("jar")) {
                    String schemeSpecificPart = uri.getRawSchemeSpecificPart();
                    String jarFilePart = schemeSpecificPart.substring(0, schemeSpecificPart.indexOf("!/"));
                    
                    // The resolvedURL may be of the form "jar:file:c:\Program Files\foo",
                    // where it is missing a leading slash after the "file:" and where the space
                    // is not escaped. Luckily the URI constructor handles the space character, but
                    // the File constructor does not like the opaque url (opaque because of the missing slash).
                    
                    if (jarFilePart.startsWith("file:") && !jarFilePart.startsWith("file:/")) {
                        jarFilePart = "file:/" + jarFilePart.substring(5);
                    }
                    
                    if (jarFilePart.startsWith("file:")) {
                        try {
                            File jarFile = new File(new URI(jarFilePart));
                            
                            if (jarFile.getName().endsWith(CarBuilder.DOT_CAR_DOT_JAR)) {
                                carJarFilesOnClasspath.add(jarFile);
                            }
                            
                        } catch (URISyntaxException e) {
                            // skip this bad entry and continue
                        }
                    } else {
                        try {
                            // For some JRE/Java Web Start versions (e.g. Java 1.6), the URL may be
                            // of the form jar:http://...!/... , so we will need to use the JarURLConnection
                            // to download to a temporary file
                            
                            JarURLConnection juc = (JarURLConnection)url.openConnection();
                            File jarFile = new File(juc.getJarFile().getName());
                            
                            // the cached jar file may not have maintained the original file extension, so just add it
                            carJarFilesOnClasspath.add(jarFile);
                            
                        } catch (IOException e) {
                            // skip this bad entry and continue
                        }
                    }
                }
            }
        }
        
        return carJarFilesOnClasspath;
    }
}

