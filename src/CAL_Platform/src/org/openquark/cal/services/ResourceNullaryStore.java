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
 * ResourceNullaryStore.java
 * Creation date: Nov 23, 2004.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.ModuleSourceDefinition;
import org.openquark.cal.services.ResourcePath.FilePath;



/**
 * Warning- this class should only be used by the CAL services implementation. It is not part of the
 * external API of the CAL platform.
 * <p>
 * A resource store for the nullary workspace, using files and folders in the file system.
 * @author Edward Lam
 */
public abstract class ResourceNullaryStore extends AbstractResourcePathStore {
    
    /**
     * Constructor for a ResourceNullaryStore.
     * @param resourceType the resource type for the associated resource.
     * @param pathMapper the path mapper for this store.
     */
    public ResourceNullaryStore(String resourceType, ResourcePathMapper pathMapper) {
        super(resourceType, pathMapper);
    }
    
    /**
     * @return the nullary environment.
     */
    private static NullaryEnvironment getNullaryEnvironment() {
        return NullaryEnvironment.getNullaryEnvironment();
    }
    
    /**
     * Get the file which contains the resource for the given feature.
     * @param resourceName the identifier of the resource.
     * @param createDirectoryIfAbsent whether to create the directory to the file if it does not exist.
     * @return the file which corresponds to that module.
     */
    File getFeatureFile(ResourceName resourceName, boolean createDirectoryIfAbsent) {
        FilePath resourceFilePath = getResourcePath(resourceName);
        return getNullaryEnvironment().getFile(resourceFilePath, createDirectoryIfAbsent);
    }

    /**
     * {@inheritDoc}
     */
    public List<ResourceName> getFolderResourceNames(ResourcePath.Folder folder) {
        return getNullaryEnvironment().getFolderResourceNames(folder, getPathMapper());
    }
    
    /**
     * {@inheritDoc}
     */
    public List<ResourceName> getFilteredFolderResourceNames(ResourcePath.Folder folder, ResourceName.Filter filter) {
        return getNullaryEnvironment().getFilteredFolderResourceNames(folder, getPathMapper(), filter);
    }
    
    /**
     * {@inheritDoc}
     */
    public void removeResource(ResourceName resourceName, Status removeStatus) {
        
        NullaryEnvironment nullaryEnvironment = getNullaryEnvironment();
        
        // Get the path to the file to remove.
        ResourcePath.FilePath filePathToRemove = getPathMapper().getResourcePath(resourceName);
        
        if (nullaryEnvironment.exists(filePathToRemove)) {
            File fileToRemove = nullaryEnvironment.getFile(filePathToRemove, false);

            if (fileToRemove == null) {
                String message = "Could not find resource to remove: (Type: " + getResourceType() + ".  Name: " + resourceName + ")";
                removeStatus.add(new Status(Status.Severity.WARNING, message));
                
            } else {
                // Attempt to delete the file.
                if (!fileToRemove.delete()) {
                    String message = "Could not remove resource: (Type: " + getResourceType() + ".  Name: " + resourceName + ")";
                    removeStatus.add(new Status(Status.Severity.WARNING, message));
                }
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void removeAllResources(Status removeStatus) {
        
        NullaryEnvironment nullaryEnvironment = getNullaryEnvironment();
        
        // Delete subdirectories of the base resource folder, but leave the folder itself.
        ResourcePath.Folder baseResourceFolder = getPathMapper().getBaseResourceFolder();
        if (nullaryEnvironment.exists(baseResourceFolder)) {
            nullaryEnvironment.delSubtrees(baseResourceFolder, removeStatus);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean renameResource(ResourceName oldResourceName, ResourceName newResourceName, ResourceStore newResourceStore, Status renameStatus) {
        
        NullaryEnvironment nullaryEnvironment = getNullaryEnvironment();

        // Get the file path to rename.
        ResourcePath.FilePath oldFilePath = getPathMapper().getResourcePath(oldResourceName);
        
        if (nullaryEnvironment.exists(oldFilePath)) {
            File oldFile = nullaryEnvironment.getFile(oldFilePath, false);
            
            if (oldFile == null) {
                String message = "Could not find resource to rename: (Type: " + getResourceType() + ". Name: " + oldResourceName + ")";
                renameStatus.add(new Status(Status.Severity.INFO, message));
                return false;
            } else {
                
                if (newResourceStore == this) {
                    // if the resource store for the renamed resource is also this store, then we can simply do a file rename
                    ResourcePath.FilePath newFilePath = getPathMapper().getResourcePath(newResourceName);
                    File newFile = nullaryEnvironment.getFile(oldFilePath, newFilePath, true);
                    
                    // Attempt to rename the file.
                    if (!oldFile.renameTo(newFile)) {
                        // If a simple rename didn't work (e.g. renaming a module A to B.C causes the source file to move from CAL/A.cal to CAL/B/C.cal - a different directory)
                        // then we do a import/delete combo
                        return transferResourceToAnotherStore(oldResourceName, newResourceName, newResourceStore, renameStatus);
                    }
                    
                    return true;
                    
                } else {
                    // the old and new resource stores are different, so we'll attempt to do a import/delete combo
                    return transferResourceToAnotherStore(oldResourceName, newResourceName, newResourceStore, renameStatus);
                }
            }
        } else {
            String message = "Could not find resource to rename: (Type: " + getResourceType() + ". Name: " + oldResourceName + ")";
            renameStatus.add(new Status(Status.Severity.INFO, message));
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasFeature(ResourceName resourceName) {
        FilePath filePath = getResourcePath(resourceName);
        return getNullaryEnvironment().exists(filePath);
    }
    
    /**
     * {@inheritDoc}
     */
    public InputStream getInputStream(ResourceName resourceName) {
        FilePath filePath = getResourcePath(resourceName);
        return getNullaryEnvironment().getInputStream(filePath, new Status("Input stream status."));
    }

    /**
     * {@inheritDoc}
     */
    public OutputStream getOutputStream(ResourceName resourceName, Status saveStatus) {
        File resourceFile = getFeatureFile(resourceName, true);
        return getOutputStream(resourceFile, saveStatus);
    }
    
    /**
     * Helper method to create an output stream for a file corresponding to a ResourceName.
     * @param resourceFile the file for the resource.
     * @param saveStatus the tracking status object
     * @return an output stream for the given file, or null if the output stream could not be created.
     */
    static final OutputStream getOutputStream(File resourceFile, Status saveStatus) {

        OutputStream os;
        try {
            // Write out the resource file
            os = new FileOutputStream(resourceFile);
            
        } catch (FileNotFoundException e) {
            saveStatus.add(new Status(Status.Severity.WARNING, "Failed to save resource.", e));
            return null;
        }

        return os;
    }
    

    /**
     * {@inheritDoc}
     */
    public String getDebugInfo(ResourceName resourceName) {
        FilePath filePath = getResourcePath(resourceName);
        return getNullaryEnvironment().getDebugInfo(filePath);
    }

    /**
     * {@inheritDoc}
     */
    public long getTimeStamp(ResourceName resourceName) {
        File resourceFile = getFeatureFile(resourceName, false);

        if (resourceFile != null && resourceFile.exists()) {
            return resourceFile.lastModified();
        }
        return 0;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isWriteable() {
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isWriteable(ResourceName resourceName) {
        File resourceFile = getFeatureFile(resourceName, false);
        return resourceFile != null && resourceFile.isFile() && resourceFile.canWrite();
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isRemovable(ResourceName resourceName) {
        return isWriteable(resourceName);
    }
    
    
    /**
     * A ResourceNullaryStore for module resources.
     * @author Edward Lam
     */
    public static abstract class Module extends ResourceNullaryStore implements ResourceStore.Module {
        
        /**
         * Constructor for a ResourceNullaryStore.Module.
         * @param resourceType
         * @param pathMapper
         */
        public Module(String resourceType, ResourcePathMapper.Module pathMapper) {
            super(resourceType, pathMapper);
        }
        
        /**
         * {@inheritDoc}
         */
        public void removeModuleResources(ModuleName moduleName, Status removeStatus) {
            // Note: we're assuming somewhat that all the resources of a given type for the given module live in the same directory.
            deleteModuleResourceFiles(moduleName, removeStatus);
            
            NullaryEnvironment nullaryEnvironment = getNullaryEnvironment();
            
            // Now remove the base folder, if the module is represented by a folder.
            ResourcePath moduleResourcePath = ((ResourcePathMapper.Module)getPathMapper()).getModuleResourcePath(moduleName);
            if (nullaryEnvironment.exists(moduleResourcePath)) {
                nullaryEnvironment.delTree(moduleResourcePath, removeStatus);
            }
        }
        
        /**
         * Removes just the files (but not the directories) associated with the resources of the given module.
         * @param moduleName the name of a module.
         * @param removeStatus the tracking status object.
         */
        public void removeFilesOfResourcesInModule(ModuleName moduleName, Status removeStatus) {
            // Note: we're assuming somewhat that all the resources of a given type for the given module live in the same directory.
            deleteModuleResourceFiles(moduleName, removeStatus);
            
            // We do not remove the base folder, because there may be other modules living in subfolders.
        }

        /**
         * Removes just the files (but not the directories) associated with the resources of the given module.
         * @param moduleName the name of a module.
         * @param removeStatus the tracking status object.
         */
        private void deleteModuleResourceFiles(ModuleName moduleName, Status removeStatus) {
            List<ResourceName> featureNameList = getModuleResourceNameList(moduleName);

            for (final ResourceName resourceName : featureNameList) {
                File resourceFile = getFeatureFile(resourceName, false);
                if (resourceFile == null || !resourceFile.isFile()) {
                    String message = "Could not determine location for " + resourceName;
                    removeStatus.add(new Status(Status.Severity.ERROR, message, null));
                    
                } else {
                    if (!resourceFile.delete()) {
                        String message = "Could not remove resource for " + resourceName;
                        removeStatus.add(new Status(Status.Severity.ERROR, message, null));
                    }
                }
            }
        }
        
        /**
         * {@inheritDoc}
         */
        public final Iterator<WorkspaceResource> getResourceIterator() {
            return ModuleResourceStoreHelper.getResourceIterator(this);
        }
        
        /**
         * {@inheritDoc}
         */
        public final Iterator<WorkspaceResource> getResourceIterator(ModuleName moduleName) {
            return ModuleResourceStoreHelper.getResourceIterator(this, moduleName);
        }
        
        /**
         * {@inheritDoc}
         */
        public OutputStream getOutputStream(ResourceName resourceName, Status saveStatus) {

            File resourceFile = null;  // should be non-null by the time we create the output stream.
            
            if (getResourceType() != ModuleSourceDefinition.RESOURCE_TYPE) {   
                // A module store which is not a cal source store.
                // Attempt to co-locate the module resources with the CAL source file.
                FilePath resourceFilePath = getResourcePath(resourceName);

                // (But don't change the location if the file already exists).
                if (!getNullaryEnvironment().exists(resourceFilePath)) {
                    
                    // Extract the module name (if any) from the resource name.
                    FeatureName featureName = resourceName.getFeatureName();
                    if (featureName instanceof CALFeatureName) {
                        
                        CALFeatureName calFeatureName = (CALFeatureName)featureName;

                        if (calFeatureName.hasModuleName()) {
                            ModuleName moduleName = calFeatureName.toModuleName();

                            // Get the base file path from the path mapper and use this to get the file with the right base folder.
                            ResourceName moduleResourceName = new ResourceName(CALFeatureName.getModuleFeatureName(moduleName));
                            FilePath baseFilePath = CALSourcePathMapper.INSTANCE.getResourcePath(moduleResourceName);
                            resourceFile = getNullaryEnvironment().getFile(baseFilePath, resourceFilePath, true);
                        }
                    }
                }
            }
            
            if (resourceFile == null) {
                resourceFile = getFeatureFile(resourceName, true);
            }
            
            return getOutputStream(resourceFile, saveStatus);
        }
    }
}
