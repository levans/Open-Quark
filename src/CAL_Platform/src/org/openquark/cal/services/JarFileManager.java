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
 * JarFileManager.java
 * Creation date: Jan 25, 2006.
 * By: Joseph Wong
 */
package org.openquark.cal.services;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * This class implements a manager for a {@link JarFile}. In particular, it caches information about
 * the entries in the jar file for performance, and provides methods for querying the jar file about
 * particular subdirectories within the jar without enumerating through all entries in the jar.
 *
 * @author Joseph Wong
 */
public class JarFileManager {

    /**
     * The JarFile encapsulated by this manager.
     */
    private final JarFile jarFile;
    
    /**
     * A Map mapping a folder name to a Set of the names of the files contained directly within that folder.
     * 
     * It starts out as null, and is lazily built on first access.
     */
    private Map<String, Set<String>> folderNameToFileNames;
    
    /**
     * A Map mapping a folder name to a Set of the paths of all (direct and indirect) descendent folders within that folder.
     * 
     * It starts out as null, and is lazily built on first access.
     */
    private Map<String, Set<String>> folderNameToAllDescendentFolderPaths;
    
    /**
     * Constructs a JarFileManager, encapsulating the given JarFile.
     * @param jarFile the jar file to be managed by the manager.
     */
    public JarFileManager(JarFile jarFile) {
        if (jarFile == null) {
            throw new NullPointerException();
        }
        
        this.jarFile = jarFile;
    }
    
    /**
     * Ensures that the underlying jar file is closed.
     */
    @Override
    protected void finalize() throws IOException {
        jarFile.close();
    }
    
    /**
     * @return the JarFile encapsulated by this manager.
     */
    public JarFile getJarFile() {
        return jarFile;
    }
    
    /**
     * If the cache is not initialized, enumerate through the jar file and build up the cache.
     */
    private synchronized void ensureCacheInitialized() {
        if (folderNameToFileNames == null || folderNameToAllDescendentFolderPaths == null) {
            
            Map<String, Set<String>> fileNamesMap = new HashMap<String, Set<String>>();
            Map<String, Set<String>> directoryPathsMap = new HashMap<String, Set<String>>();
            
            // loop through all the entries in the jar, building up the (folder |-> {children files}) map
            // and the (folder |-> {all descendant folders}) map.
            for (Enumeration<JarEntry> en = jarFile.entries(); en.hasMoreElements(); ) {
                ZipEntry entry = en.nextElement();
                String entryName = entry.getName();
                
                if (!entry.isDirectory()) {
                    // build the file entries cache
                    int lastIndexOfSlash = entryName.lastIndexOf('/');
                    
                    String relativePath;
                    String fileName;
                    if (lastIndexOfSlash != -1) {
                        relativePath = entryName.substring(0, lastIndexOfSlash);
                        fileName = entryName.substring(lastIndexOfSlash + 1);
                    } else {
                        relativePath = ""; // the 'root' folder as a relative path is the empty string
                        fileName = entryName;
                    }
                    
                    Set<String> fileNames = fileNamesMap.get(relativePath);
                    
                    if (fileNames == null) {
                        fileNames = new HashSet<String>();
                        fileNamesMap.put(relativePath, fileNames);
                    }
                    
                    fileNames.add(fileName);
                    
                } else {
                    // build the directory entries cache
                    String directoryPath = entryName;
                    addDirectoryPathToCachingMap(directoryPathsMap, directoryPath);
                }
            }
            
            // a Car file that we write out ourselves may not have explicit subdirectory entries,
            // so we will need to loop through the parent directories of the file entries in the Car,
            // i.e. the keys in the fileNamesMap
            for (final String directoryPath : fileNamesMap.keySet()) {
                addDirectoryPathToCachingMap(directoryPathsMap, directoryPath);
            }
            
            // wrap all inner sets with unmodifiable wrappers
            Map<String, Set<String>> fileNamesMapWithUnmodifiableInnerSets = new HashMap<String, Set<String>>();
            
            for (final Map.Entry<String, Set<String>> mapEntry : fileNamesMap.entrySet()) {
                fileNamesMapWithUnmodifiableInnerSets.put(mapEntry.getKey(), Collections.unmodifiableSet(mapEntry.getValue()));
            }
            
            Map<String, Set<String>> directoryPathsMapWithUnmodifiableInnerSets = new HashMap<String, Set<String>>();
            
            for (final Map.Entry<String, Set<String>> mapEntry : directoryPathsMap.entrySet()) {
                directoryPathsMapWithUnmodifiableInnerSets.put(mapEntry.getKey(), Collections.unmodifiableSet(mapEntry.getValue()));
            }
            
            // lock the outer maps down with unmodifiable wrappers
            folderNameToFileNames = Collections.unmodifiableMap(fileNamesMapWithUnmodifiableInnerSets);
            
            folderNameToAllDescendentFolderPaths = Collections.unmodifiableMap(directoryPathsMapWithUnmodifiableInnerSets);
        }
    }

    /**
     * Processes a directory path and adds appropriate entries for it to the (folder |-&gt; {all descendant folders}) map.
     * @param directoryPathsMap the caching map.
     * @param directoryPath the directory path to be cached in the given map.
     */
    private void addDirectoryPathToCachingMap(Map<String, Set<String>> directoryPathsMap, String directoryPath) {
        
        Set<String> allPaths = new HashSet<String>();
        allPaths.add(directoryPath);
        
        String currentPath = directoryPath;
        
        // we loop through all the ancestor folders of this entry
        // and associate this entry with each of its ancestors
        do {
            /// let currentPath = parent of currentPath
            //
            int lastIndexOfSlash = currentPath.lastIndexOf('/');
            
            if (lastIndexOfSlash != -1) {
                currentPath = currentPath.substring(0, lastIndexOfSlash);
            } else {
                currentPath = ""; // the 'root' folder as a relative path is the empty string
            }
            
            Set<String> subfolderPathsOfCurrentPath = directoryPathsMap.get(currentPath);
            
            if (subfolderPathsOfCurrentPath == null) {
                subfolderPathsOfCurrentPath = new HashSet<String>();
                directoryPathsMap.put(currentPath, subfolderPathsOfCurrentPath);
            }
            
            subfolderPathsOfCurrentPath.addAll(allPaths);
            
            allPaths.add(currentPath);
            
        } while (currentPath.length() > 0);
    }
    
    /**
     * Returns a Set of the names for the files in the specified folder.
     * @param folderName the name of the folder.
     * @return a Set of the names for the files in the specified folder, or null if the jar file has
     *         no entries in the specified folder. The returned Set is not modifiable.
     */
    public Set<String> getFileNamesInFolder(String folderName) {
        ensureCacheInitialized();
        return folderNameToFileNames.get(folderName);
    }
    
    /**
     * Returns a Set of the paths for all (direct and indirect) descendent folders in the specified folder.
     * @param folderName the name of the folder.
     * @return a Set of the paths for all (direct and indirect) descendent folders in the specified folder,
     *         or null if the jar file has no subfolders in the specified folder. The returned Set is not modifiable.
     */
    public Set<String> getAllDescendentFolderPathsInFolder(String folderName) {
        ensureCacheInitialized();
        return folderNameToAllDescendentFolderPaths.get(folderName);
    }
}
