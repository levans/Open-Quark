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
 * ProgramResourceManager.java
 * Creation date: December 8, 2005.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.machine.ProgramResourceLocator;
import org.openquark.cal.runtime.MachineType;
import org.openquark.cal.services.ResourcePath.Folder;


//
// todo(jowong) We may want to consider how to move forward with this class, which currently is not used much, except
// for helping out the deletion of program resources.
//
// There are 3 possibilities:
// - implement the resource support, so that program resources are indeed first class workspace resources, and so that
//   operations other than deletion (e.g. enumeration) would actually work with this manager.
//
// - unify this class with ProgramResourceRepository, since both classes are involved with managing resources of the program,
//   except that ProgramResourceRepository exposes a lower-level interface for the compiler, while this class deals
//   more with the workspace.
//
// - delete this class since it is not used by clients as a true resource manager, and move its functionality
//   outside of the ResourceManager hierarchy.
//

/**
 * The resource manager for cal program resources in the workspace.
 * @author Edward Lam
 */
public abstract class ProgramResourceManager extends ModuleResourceManager {

    /** The string representing the resource type. */
    private final String resourceType;
    
    /** The store managed by this manager. */
    private final ResourceStore.Module programResourceStore;
    
    /**
     * Implements a Car-based ProgramResourceManager, where all the (read-only) resources come from a Car.
     *
     * @author Joseph Wong
     */
    public static final class CarBased extends ProgramResourceManager {
        
        /**
         * Private constructor. Instances should be created via the {@link #make} method.
         * 
         * @param resourceType the string representing the resource type.
         * @param programResourceStore the Car-based store managed by this manager.
         */
        private CarBased(String resourceType, ResourceStore.Module programResourceStore) {
            super(resourceType, programResourceStore);
        }
        
        /**
         * Factory method for constructing an instance of this manager class.
         * @param machineType the program machine type.
         * @param carAccessor the accessor for the Car.
         * @return an instance of this class.
         */
        public static CarBased make(MachineType machineType, final Car.Accessor carAccessor) {
            
            String resourceType = machineType.toString().toUpperCase() + "_RUNTIME";
            
            if (carAccessor == null) {
                throw new NullPointerException();
            }
            
            // Create the resource store as a jar store.
            ResourceStore.Module programResourceStore = new ResourceJarStore.Module(carAccessor.getJarFileManager(), resourceType, new ProgramResourcePathMapper(machineType)) {

                public List<ResourceName> getModuleResourceNameList(ModuleName moduleName) {
                    return Collections.emptyList();
                }
                
                public Set<ModuleName> getModuleNames() {
                    Folder baseResourceFolder = getPathMapper().getBaseResourceFolder();
                    
                    Set<String> subDirectoryPaths = getJarFileManager().getAllDescendentFolderPathsInFolder(baseResourceFolder.getPathStringMinusSlash());
                    
                    List<String> subDirectoryNames = new ArrayList<String>();
                    
                    for (final String subDirectoryPath : subDirectoryPaths) {
                        
                        ResourcePath.Folder subFolder = new ResourcePath.Folder(subDirectoryPath.split("/"));
                        String subFolderName = subFolder.getName();
                        
                        // we only add to the list of subdirectory names if the folder is a direct child of the base resource folder
                        if (subFolder.equals(baseResourceFolder.extendFolder(subFolderName))) {
                            subDirectoryNames.add(subFolderName);
                        }
                    }
                    
                    Set<ModuleName> moduleNames = new HashSet<ModuleName>();
                    
                    for (int i = 0, n = subDirectoryNames.size(); i < n; i++) {
                        String subDirectoryName = subDirectoryNames.get(i);
                        ModuleName moduleName = ProgramResourceLocator.createModuleNameFromPackageNameSegment(subDirectoryName);
                        if (moduleName != null) {
                            moduleNames.add(moduleName);
                        }
                    }
                    return moduleNames;
                }
                
            };
            
            return new CarBased(resourceType, programResourceStore);
        }
    }
    
    /**
     * Implements a file-system-based ProgramResourceManager, where all the resources come from the file system.
     *
     * @author Joseph Wong
     */
    public static final class FileSystemBased extends ProgramResourceManager {
        
        /**
         * Private constructor. Instances should be created via the {@link #make} method.
         * 
         * @param resourceType the string representing the resource type.
         * @param programResourceStore the Car-based store managed by this manager.
         */
        private FileSystemBased(String resourceType, ResourceStore.Module programResourceStore) {
            super(resourceType, programResourceStore);
        }
        
        /**
         * Factory method for constructing an instance of this manager class.
         * @param machineType the program machine type.
         * @param sourceGenerationRoot The root directory for generated program resources.
         * @return an instance of this class.
         */
        public static FileSystemBased make(MachineType machineType, final File sourceGenerationRoot) {
            
            String resourceType = machineType.toString().toUpperCase() + "_RUNTIME";
            
            if (sourceGenerationRoot == null) {
                throw new NullPointerException();
            }
            
            // Create the resource store as a file store.
            ResourceStore.Module programResourceStore = new ResourceFileStore.Module(sourceGenerationRoot, resourceType, new ProgramResourcePathMapper(machineType)) {
                
                public List<ResourceName> getModuleResourceNameList(ModuleName moduleName) {
                    return Collections.emptyList();
                }
                
                public Set<ModuleName> getModuleNames() {
                    Set<ModuleName> moduleNames = new HashSet<ModuleName>();
                    
                    File[] subDirectories = sourceGenerationRoot.listFiles();
                    if (subDirectories != null) {
                        for (final File subDirectory : subDirectories) {
                            String subDirectoryName = subDirectory.getName();
                            ModuleName moduleName = ProgramResourceLocator.createModuleNameFromPackageNameSegment(subDirectoryName);
                            if (subDirectory.isDirectory() && moduleName != null) {
                                moduleNames.add(moduleName);
                            }
                        }
                    }
                    return moduleNames;
                }
            };
            
            return new FileSystemBased(resourceType, programResourceStore);
        }
    }
    
    /**
     * Private constructor for subclasses.
     * 
     * @param resourceType the string representing the resource type.
     * @param programResourceStore the Car-based store managed by this manager.
     */
    private ProgramResourceManager(String resourceType, ResourceStore.Module programResourceStore) {
        this.resourceType = resourceType;
        this.programResourceStore = programResourceStore;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getResourceType() {
        return resourceType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResourceStore getResourceStore() {
        return programResourceStore;
    }
}