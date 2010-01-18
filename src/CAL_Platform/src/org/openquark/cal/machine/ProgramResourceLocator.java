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
 * ProgramResourceLocator.java
 * Creation date: Nov 30, 2005.
 * By: Edward Lam
 */
package org.openquark.cal.machine;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.services.ResourcePath;


/**
 * Warning- this class should only be used by the CAL runtime implementation. It is not part of the
 * external API of the CAL platform.
 * <P>
 * A locator for program resources within a ProgramResourceRepository.
 * 
 * Wraps the following info associated with the program resource:
 * 1) the name of the module
 * 2) the resource path 
 * 
 * @author Edward Lam
 */
public abstract class ProgramResourceLocator {
    
    // TODOEL: Move this.
    /** The prefix for module package names. */
    public static final String MODULE_PREFIX = "cal_";

    /** The name of the module. */
    private final ModuleName moduleName;
    
    /** The resource path. */
    private final ResourcePath resourcePath;
    
    /**
     * A ProgramResourceLocator which represents a File.
     * @author Edward Lam
     */
    public static class File extends ProgramResourceLocator {

        /**
         * Constructor for a File.
         * @param moduleName
         * @param resourcePath
         */
        public File(ModuleName moduleName, ResourcePath.FilePath resourcePath) {
            super(moduleName, resourcePath);
        }
    }
    
    /**
     * A ProgramResourceLocator which represents a Folder.
     * @author Edward Lam
     */
    public static class Folder extends ProgramResourceLocator {

        /**
         * Constructor for a Folder.
         * @param moduleName
         * @param resourcePath
         */
        public Folder(ModuleName moduleName, ResourcePath.Folder resourcePath) {
            super(moduleName, resourcePath);
        }
        
        /**
         * Get a locator file which represents this locator, with the path extended by one element.
         * @param newPathElement a new path element.
         * @return a new locator, which represents this locator with the path extended by the given path element.
         */
        public ProgramResourceLocator.File extendFile(String newPathElement) {
            return new ProgramResourceLocator.File(getModuleName(), (getResourceFolder().extendFile(newPathElement)));
        }
        
        /**
         * Get a locator folder which represents this locator, with the path extended by one element.
         * @param newPathElement a new path element.
         * @return a new resource path, which represents this locator with the path extended by the given path element.
         * If the new path element is empty, this folder is returned.
         */
        public ProgramResourceLocator.Folder extendFolder(String newPathElement) {
            return new ProgramResourceLocator.Folder(getModuleName(), (getResourceFolder().extendFolder(newPathElement)));
        }
        
        /**
         * Get a locator folder which represents this locator, with the path extended by a number of elements.
         * @param newPathElements the new path elements.
         * @return a new locator, which represents this locator with the path extended by the given path elements.
         */
        public ProgramResourceLocator.Folder extendFolders(String[] newPathElements) {
            return new ProgramResourceLocator.Folder(getModuleName(), (getResourceFolder().extendFolders(newPathElements)));
        }
        
        /**
         * @return the resource path, as a folder.
         */
        public ResourcePath.Folder getResourceFolder() {
            return (ResourcePath.Folder)getResourcePath();
        }

    }

    /**
     * Private constructor for a ProgramResourceLocator.
     * Instances are created via subclass constructors.
     * 
     * @param moduleName
     * @param resourcePath
     */
    private ProgramResourceLocator(ModuleName moduleName, ResourcePath resourcePath) {
        this.moduleName = moduleName;
        this.resourcePath = resourcePath;
    }
    

    /**
     * @return the module name.
     */
    public ModuleName getModuleName() {
        return this.moduleName;
    }

    /**
     * @return the associated resource path.
     */
    public ResourcePath getResourcePath() {
        return this.resourcePath;
    }
    
    /**
     * @return the last path element, or null if there aren't any.
     */
    public String getName() {
        return resourcePath.getName();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Name: " + moduleName + ".  " + resourcePath.toString();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ProgramResourceLocator) {
            ProgramResourceLocator otherLocator = (ProgramResourceLocator)obj;
            return otherLocator.moduleName.equals(moduleName) && otherLocator.resourcePath.equals(resourcePath);
        }
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return moduleName.hashCode() * 17 + resourcePath.hashCode();
    }

    /**
     * Reverse the transformation applied by createPackageNameSegmentFromModuleName().
     * @param packageNameSegment a valid java package name segment, created from createPackageNameSegmentFromModuleName().
     * @return the name of the module for that package, or null if the name does not correspond to a name of a module package.
     */
    public static ModuleName createModuleNameFromPackageNameSegment(String packageNameSegment) {
        if (packageNameSegment.startsWith(ProgramResourceLocator.MODULE_PREFIX)) {
            // '_' in the package name segment is first mapped to '@' ('@' is an invalid character for a module name) 
            // "__" in the package name segment gets mapped to '_'
            // finally, '@' gets mapped to '$'
            
            // ModuleName.maybeMake is used so that null is returned if the name does not correspond to a name of a module package.
            return ModuleName.maybeMake(packageNameSegment.substring(ProgramResourceLocator.MODULE_PREFIX.length()).replaceAll("__", "@").replace('_', '.').replace('@', '_'));
        }
        return null;
    }


}
