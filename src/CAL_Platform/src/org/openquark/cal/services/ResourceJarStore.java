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
 * ResourceJarStore.java
 * Creation date: Nov 23, 2004.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.openquark.cal.compiler.ModuleName;



/**
 * Warning- this class should only be used by the CAL services implementation. It is not part of the
 * external API of the CAL platform.
 * <p>
 * A read-only resource store based on compressed resources in a jar file.
 * @author Edward Lam
 */
public abstract class ResourceJarStore extends AbstractResourcePathStore {

    /** The manager for the jar file on which this store is based. */
    private final JarFileManager jarFileManager;
    
    /**
     * Constructor for a ResourceNullaryStore.
     * @param jarFileManager the manager for the jar file on which this store is based.
     * @param resourceType the resource type for the associated resource.
     * @param pathMapper the path mapper for this store.
     */
    public ResourceJarStore(JarFileManager jarFileManager, String resourceType, ResourcePathMapper pathMapper) {
        super(resourceType, pathMapper);
        this.jarFileManager = jarFileManager;
    }

    /**
     * @param resourceName the name of the resource.
     * @return the JarEntry for the feature, or null if the feature could not be found
     */
    private JarEntry getJarEntry(ResourceName resourceName) {
        ResourcePath.FilePath resourceFile = getResourcePath(resourceName);
        return jarFileManager.getJarFile().getJarEntry(resourceFile.getPathStringMinusSlash());
    }

    /**
     * {@inheritDoc}
     */
    public List<ResourceName> getFolderResourceNames(ResourcePath.Folder folder) {
        return ModulePackager.getFolderResourceNames(jarFileManager, folder, getPathMapper());
    }
    
    /**
     * {@inheritDoc}
     */
    public List<ResourceName> getFilteredFolderResourceNames(ResourcePath.Folder folder, ResourceName.Filter filter) {
        return ModulePackager.getFilteredFolderResourceNames(jarFileManager, folder, getPathMapper(), filter);
    }

    /**
     * {@inheritDoc}
     */
    public void removeResource(ResourceName resourceName, Status removeStatus) {
        removeStatus.add(new Status(Status.Severity.ERROR, "Jar Store not modifiable.", null));
    }
    
    /**
     * {@inheritDoc}
     */
    public void removeAllResources(Status removeStatus) {
        removeStatus.add(new Status(Status.Severity.ERROR, "Jar Store not modifiable.", null));
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean renameResource(ResourceName oldResourceName, ResourceName newResourceName, ResourceStore newResourceStore, Status renameStatus) {
        renameStatus.add(new Status(Status.Severity.ERROR, "Jar Store not modifiable.", null));
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasFeature(ResourceName resourceName) {
        return getJarEntry(resourceName) != null;
    }
    
    /**
     * {@inheritDoc}
     */
    public InputStream getInputStream(ResourceName resourceName) {
        JarEntry jarEntry = getJarEntry(resourceName);
        if (jarEntry != null) {
            try {
                return jarFileManager.getJarFile().getInputStream(jarEntry);
                
            } catch (IOException e) {
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public OutputStream getOutputStream(ResourceName resourceName, Status saveStatus) {
        saveStatus.add(new Status(Status.Severity.ERROR, "Jar Store not modifiable.", null));
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getDebugInfo(ResourceName resourceName) {
        JarEntry jarEntry = getJarEntry(resourceName);
        if (jarEntry != null) {
            String jarFileName = jarFileManager.getJarFile().getName();
            String type;
            if (jarFileName.endsWith(CarBuilder.DOT_CAR_DOT_JAR)) {
                type = "Car-jar";
            } else if (jarFileName.endsWith("." + CarPathMapper.INSTANCE.getFileExtension())) {
                type = "Car";
            } else {
                type = "jar";
            }
            return "from " + type + ": " + jarFileName + ", entry: " + jarEntry;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public long getTimeStamp(ResourceName resourceName) {
        JarEntry jarEntry = getJarEntry(resourceName);
        if (jarEntry == null) {
            return 0L;
        }
        long entryTime = jarEntry.getTime();
        return (entryTime == -1) ? 0 : entryTime;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isWriteable() {
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isWriteable(ResourceName resourceName) {
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isRemovable(ResourceName resourceName) {
        return false;
    }
    
    /**
     * @return the jar file on which this store is based.
     */
    public JarFile getJarFile() {
        return jarFileManager.getJarFile();
    }
    
    /**
     * @return the jar file manager associated with this store.
     */
    public JarFileManager getJarFileManager() {
        return jarFileManager;
    }
    
    
    /**
     * A ResourceJarStore for module resources.
     * @author Edward Lam
     */
    public static abstract class Module extends ResourceJarStore implements ResourceStore.Module {
        
        /**
         * Constructor for a ResourceJarStore.Module.
         * @param jarFileManager
         * @param resourceType
         * @param pathMapper
         */
        public Module(JarFileManager jarFileManager, String resourceType, ResourcePathMapper pathMapper) {
            super(jarFileManager, resourceType, pathMapper);
        }
        
        /**
         * {@inheritDoc}
         */
        public void removeModuleResources(ModuleName moduleName, Status removeStatus) {
            removeStatus.add(new Status(Status.Severity.ERROR, "Jar Store not modifiable.", null));
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
