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
 * ResourcePath.java
 * Creation date: Sep 13, 2004.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.util.Arrays;
import java.util.List;

/**
 * A ResourcePath represents a componentized path relative to the classloader root.
 * 
 * This class is necessary because in our development environment, an abstract relative path such as "/CAL/"
 *   can refer to more than one absolute path on the file system.
 * 
 * @author Edward Lam
 */
public abstract class ResourcePath {
    public static final ResourcePath.Folder EMPTY_PATH = new ResourcePath.Folder(new String[0]);

    /** The path elements of which this path is comprised. */
    private final String[] pathElements;
    
    /**
     * A ResourcePath which represents a Folder.
     * @author Edward Lam
     */
    public static class Folder extends ResourcePath {
        
        /**
         * Constructor for a Folder.
         * @param pathElements the elements of which this path is comprised.
         */
        public Folder(String[] pathElements) {
            super(pathElements);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public ResourcePath.Folder getFolder() {
            return this;
        }
        
        /**
         * Get a resource path folder which represents this resource path, extended by one element.
         * @param newPathElement a new path element.
         * @return a new resource path, which represents this path extended by the given path element.
         */
        public ResourcePath.FilePath extendFile(String newPathElement) {
            return (ResourcePath.FilePath)extend(newPathElement, false);
        }
        
        /**
         * Get a resource path folder which represents this resource path, extended by one element.
         * @param newPathElement a new path element.
         * @return a new resource path, which represents this path extended by the given path element.
         * If the new path element is empty, this folder is returned.
         */
        public ResourcePath.Folder extendFolder(String newPathElement) {
            newPathElement = newPathElement.trim();
            if (newPathElement.length() == 0) {
                return this;
            }
            return (ResourcePath.Folder)extend(newPathElement, true);
        }
        
        /**
         * Get a resource path folder which represents this resource path, extended by a number of elements.
         * @param newPathElements the new path elements.
         * @return a new resource path, which represents this path extended by the given path elements.
         */
        public ResourcePath.Folder extendFolders(String[] newPathElements) {
            ResourcePath.Folder extendedFolder = this;
            for (final String element : newPathElements) {
                extendedFolder = extendedFolder.extendFolder(element);
            }
            return extendedFolder;
        }
        
        /**
         * Get a resource path which represents this resource path, extended by one element.
         * @param newPathElement a new path element.
         * @param folder if true, extend with a folder.  Otherwise extend with a file.
         * @return a new resource path, which represents this path extended by the given path element.
         */
        private ResourcePath extend(String newPathElement, boolean folder) {
            String[] pathElements = getPathElements();
            String[] newPathElements = new String[pathElements.length + 1];
            System.arraycopy(pathElements, 0, newPathElements, 0, pathElements.length);
            newPathElements[pathElements.length] = newPathElement;
            
            if (folder) {
                return new ResourcePath.Folder(newPathElements);
            } else {
                return new ResourcePath.FilePath(newPathElements);
            }
        }
        
        /**
         * Constructs a folder resource path from a path string without a leading slash.
         * <p>
         * Warning: This should not be exposed as part of the public API!
         * (path string without leading slashes is a package-private concept)
         * 
         * @param pathStringMinusSlash a path string without a leading slash.
         * @return the corresponding folder resource path.
         */
        static ResourcePath.Folder makeFromPathStringMinusSlash(String pathStringMinusSlash) {
            String[] elements = pathStringMinusSlash.split("/");
            return new ResourcePath.Folder(elements);
        }

    }
    
    /**
     * A ResourcePath which represents a resource/file.
     * @author Edward Lam
     */
    public static class FilePath extends ResourcePath {

        /**
         * Constructor for a FilePath.
         * @param pathElements the elements of which this path is comprised.
         */
        public FilePath(String[] pathElements) {
            super(pathElements);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public Folder getFolder() {
            String[] pathElements = getPathElements();
            int nFolderElements = pathElements.length - 1;
            
            List<String> pathElementList = Arrays.asList(pathElements);
            List<String> pathElementSubList = pathElementList.subList(0, nFolderElements);
            
            String[] folderElements = new String[nFolderElements];
            return new ResourcePath.Folder(pathElementSubList.toArray(folderElements));
        }
        
        /**
         * @return the file component of the path.
         */
        public String getFileName() {
            String[] pathElements = getPathElements();
            return pathElements[pathElements.length - 1];
        }
    }

    /**
     * Constructor for a ResourcePath.
     * @param pathElements the elements of which this path is comprised.
     */
    private ResourcePath(String[] pathElements) {
        this.pathElements = pathElements.clone();
    }
    
    /**
     * @return the path elements of which this path is comprised.
     */
    public String[] getPathElements() {
        return pathElements;
    }
    
    /**
     * @return the last path element, or null if there aren't any.
     */
    public String getName() {
        if (pathElements.length == 0) {
            return null;
        }
        return pathElements[pathElements.length - 1];
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ResourcePath: [");
        for (int i = 0; i < pathElements.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(pathElements[i]);
        }
        sb.append("]");
        return sb.toString();
    }
    
    /**
     * Get the path in string form.
     * @return the path in string form.
     * ie. /pathElem1/pathElem2/.../pathElemn
     */
    public String getPathString() {
        if (pathElements.length == 0) {
            return "/";
        }
        
        StringBuilder sb = new StringBuilder();
        for (final String element : pathElements) {
            sb.append("/" + element);
        }
        
        return sb.toString();
    }

    /**
     * Get the path in string form, without the leading slash
     * @return the path in string form.
     * ie. pathElem1/pathElem2/.../pathElemn
     */
    String getPathStringMinusSlash() {
        return getPathString().substring(1);
    }
    
    /**
     * @return the subpath of this path which corresponds to the path to the directory.
     */
    public abstract Folder getFolder();
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != getClass()) {
            return false;
        }
        return Arrays.equals(getPathElements(), ((ResourcePath)obj).getPathElements());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int hash = (this instanceof Folder) ? 17 : 37;
        for (final String element : pathElements) {
            hash *= 37;
            hash += (17 * element.hashCode());
        }
        return hash;
    }

}