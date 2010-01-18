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
 * ResourceFileStore.java
 * Creation date: Nov 22, 2004.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.services.ResourcePath.FilePath;
import org.openquark.util.FileSystemHelper;



/**
 * Warning- this class should only be used by the CAL services implementation. It is not part of the
 * external API of the CAL platform.
 * <p>
 * A resource store using files and folders in the file system.
 * @author Edward Lam
 */
public abstract class ResourceFileStore extends AbstractResourcePathStore implements ResourcePathStore {

    /** The directory under which the resource folder resides. */
    private final File rootDirectory;

    /**
     * Constructor for a ResourceFileStore.
     * @param rootDirectory the directory under which the resource folder resides.
     * @param resourceType the resource type for the associated resource.
     * @param pathMapper the path mapper for this store.
     */
    public ResourceFileStore(File rootDirectory, String resourceType, ResourcePathMapper pathMapper) {
        super(resourceType, pathMapper);
        this.rootDirectory = rootDirectory;
    }
    
    /**
     * @return the directory under which the resource folder resides.
     */
    public File getRootDirectory() {
        return rootDirectory;
    }

    /**
     * Get the file which contains the resource for the given feature.
     * @param resourceName the name of the resource.
     * @param createDirectoryIfAbsent whether to create the directory to the file if it does not exist.
     * @return the file which corresponds to that feature.  Note: this file may not exist.
     */
    private File getFeatureFile(ResourceName resourceName, boolean createDirectoryIfAbsent) {
        FilePath resourceFilePath = getResourcePath(resourceName);
        return getResourceFile(resourceFilePath, createDirectoryIfAbsent);
    }

    /**
     * Get the file which corresponds to the given path
     * @param resourcePath the resource path.
     * @param createDirectoryIfAbsent whether to create the directory to the file if it does not exist.
     * @return the file which corresponds to that path.
     */
    private File getResourceFile(ResourcePath resourcePath, boolean createDirectoryIfAbsent) {
        return FileSystemResourceHelper.getResourceFile(resourcePath, rootDirectory, createDirectoryIfAbsent);
    }
    
    /**
     * {@inheritDoc}
     */
    public List<ResourceName> getFolderResourceNames(ResourcePath.Folder folder) {
        File resourceFile = getResourceFile(folder, false);
        return FileSystemResourceHelper.getDirectoryResourceNames(resourceFile, getFileExtension(), folder, getPathMapper());
    }
    
    /**
     * {@inheritDoc}
     */
    public List<ResourceName> getFilteredFolderResourceNames(ResourcePath.Folder folder, ResourceName.Filter filter) {
        File resourceFile = getResourceFile(folder, false);
        return FileSystemResourceHelper.getFilteredDirectoryResourceNames(resourceFile, getFileExtension(), folder, getPathMapper(), filter);
    }
    
    /**
     * {@inheritDoc}
     */
    public void removeResource(ResourceName resourceName, Status removeStatus) {

        // Get the file to delete.
        ResourcePath.FilePath filePathToRemove = getPathMapper().getResourcePath(resourceName);
        File fileToRemove = FileSystemResourceHelper.getResourceFile(filePathToRemove, getRootDirectory(), false);
        
        // Attempt to delete the file.
        if (!fileToRemove.delete()) {
            String message = "Could not remove resource: (Type: " + getResourceType() + ".  Name: " + resourceName + ")";
            removeStatus.add(new Status(Status.Severity.WARNING, message));
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void removeAllResources(Status removeStatus) {
        // Delete subdirectories of the base resource folder, but leave the folder itself.
        File baseResourceDirectory = FileSystemResourceHelper.getResourceFile(getPathMapper().getBaseResourceFolder(), rootDirectory, false);
        if (baseResourceDirectory.exists()) {
            FileSystemResourceHelper.delSubtrees(baseResourceDirectory, removeStatus);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean renameResource(ResourceName oldResourceName, ResourceName newResourceName, ResourceStore newResourceStore, Status renameStatus) {

        ResourcePath.FilePath oldFilePath = getPathMapper().getResourcePath(oldResourceName);
        ResourcePath.FilePath newFilePath = getPathMapper().getResourcePath(newResourceName);
        
        File oldFile = FileSystemResourceHelper.getResourceFile(oldFilePath, getRootDirectory(), false);
        
        if (FileSystemHelper.fileExists(oldFile)) {
            
            if (oldFile == null) {
                String message = "Could not find resource to rename: (Type: " + getResourceType() + ". Name: " + oldResourceName + ")";
                renameStatus.add(new Status(Status.Severity.INFO, message));
                return false;
            
            } else {
                
                if (newResourceStore == this) {
                    // if the resource store for the renamed resource is also this store, then we can simply do a file rename
                    File newFile = FileSystemResourceHelper.getResourceFile(newFilePath, getRootDirectory(), true);
    
                    // Attempt to rename the file.
                    if (!oldFile.renameTo(newFile)) {
                        String message = "Could not perform resource renaming: (Type: " + getResourceType() + ". Name: " + oldResourceName + " -> " + newResourceName + ")";
                        renameStatus.add(new Status(Status.Severity.ERROR, message));
                        return false;
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
        File featureFile = getFeatureFile(resourceName, false);
        return featureFile != null && featureFile.isFile();
    }
    
    /**
     * {@inheritDoc}
     */
    public InputStream getInputStream(ResourceName resourceName) {
        File sourceFile = getFeatureFile(resourceName, false);
        if (sourceFile == null || !sourceFile.isFile()) {
            return null;
        }
        
        InputStream is;
        try {
            is = new BufferedInputStream(new FileInputStream(sourceFile));
        } catch (FileNotFoundException e) {
            CALWorkspace.SERVICES_LOGGER.log(Level.WARNING, "Could not create input stream for file: " + sourceFile, e);
            return null;
        }
        
        return is;
    }

    /**
     * {@inheritDoc}
     */
    public OutputStream getOutputStream(ResourceName resourceName, Status saveStatus) {
        File resourceFile = getFeatureFile(resourceName, true);

        OutputStream os;
        try {
            // Write out the resource file
            os = new BufferedOutputStream(new FileOutputStream(resourceFile));
            
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
        File sourceFile = getFeatureFile(resourceName, false);
        if (sourceFile == null || !sourceFile.isFile()) {
            return null;
        }
        
        return "from file: " + sourceFile.getAbsolutePath();
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
        return resourceFile != null && resourceFile.canWrite();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isRemovable(ResourceName resourceName) {
        return isWriteable(resourceName);
    }
    
    /**
     * A ResourceFileStore for module resources.
     * @author Edward Lam
     */
    public static abstract class Module extends ResourceFileStore implements ResourceStore.Module {
        
        /**
         * Constructor for a ResourceFileStore.Module.
         * @param rootDirectory
         * @param resourceType
         * @param pathMapper
         */
        public Module(File rootDirectory, String resourceType, ResourcePathMapper.Module pathMapper) {
            super(rootDirectory, resourceType, pathMapper);
        }
        
        /**
         * {@inheritDoc}
         */
        public void removeModuleResources(ModuleName moduleName, Status removeStatus) {
            // Get the file corresponding to the resource folder for that module.
            ResourcePath moduleResourcePath = ((ResourcePathMapper.Module)getPathMapper()).getModuleResourcePath(moduleName);
            File resourceFile = FileSystemResourceHelper.getResourceFile(moduleResourcePath, getRootDirectory(), false);

            if (resourceFile.isFile()) {
                boolean resourceFileDeleted = resourceFile.delete();
                if (!resourceFileDeleted) {
                    removeStatus.add(new Status(Status.Severity.ERROR, "Could not delete file: " + resourceFile, null));
                }
            } else if (resourceFile.isDirectory()) {
                // Delete the entire subtree.
                FileSystemResourceHelper.delTree(resourceFile, removeStatus);
            }
        }
        
        /**
         * Removes just the files (but not the directories) associated with the resources of the given module.
         * @param moduleName the name of a module.
         * @param removeStatus the tracking status object.
         */
        public void removeFilesOfResourcesInModule(ModuleName moduleName, Status removeStatus) {
            List<ResourceName> featureNameList = getModuleResourceNameList(moduleName);

            for (final ResourceName resourceName : featureNameList) {
                ResourcePath resourcePath = ((ResourcePathMapper.Module)getPathMapper()).getResourcePath(resourceName);
                File resourceFile = FileSystemResourceHelper.getResourceFile(resourcePath, getRootDirectory(), false);
                
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
    }

}
