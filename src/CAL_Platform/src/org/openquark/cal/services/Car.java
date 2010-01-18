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
 * Car.java
 * Creation date: Jan 10, 2006.
 * By: Joseph Wong
 */
package org.openquark.cal.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.openquark.cal.machine.ProgramResourceLocator;
import org.openquark.cal.metadata.MetadataJarStore;
import org.openquark.cal.metadata.MetadataManager;
import org.openquark.util.Pair;


/**
 * A Car represents a CAL Archive file (a Car file) as a workspace resource.
 * <p>
 * A Car is like a jar in that it is based on the zip file format. The contents
 * of a Car file are CAL resources: workspace declarations, module-based
 * resources like CAL source files, metadata, gem designs, and also
 * program-oriented files like cmi and lc files.
 * <p>
 * Car files are intended to be used in scenarios where developers would like to
 * run the Quark platform but do not need to modify the resources contained in
 * the Car files in question.
 * <p>
 * A module is either completely defined in a Car, or lives completely outside a
 * Car. What this means is that the resources for a module cannot be scattered
 * across a Car boundary. Therefore, we now maintain a mapping from a module
 * name to the corresponding location where resources for the module are to be
 * found - either on disk or from a particular Car file. (See
 * {@link VirtualResourceManager}).
 * 
 * @author Joseph Wong
 */
/*
 * Performance note:
 * 
 * The use of Car files should not have any negative impact on performance. In
 * measurements using EverythingICE in Eclipse (in run mode), the use of Cars
 * has resulted in a performance gain of up to 15% in the time it takes to load
 * the workspace. This is due to the fact that there all the resources are in
 * one location in a Car, versus having to find the resources scattered across
 * the multiple directories that make up the StandardVault. A similar
 * performance gain can be achieved by having all the resources colocated in one
 * directory (as in the case of the packaged Quark builds).
 */
public final class Car extends WorkspaceResource {

    /** The file which contains the Car. */
    private final File file;
    
    /** The singleton JarFileManager for this Car. It starts out as null and is lazily initialized on first use. */
    private JarFileManager jarFileManager;
    
    /** The folder within a Car that contains Car workspace spec files. */
    static final ResourcePath.Folder CAR_WORKSPACE_SPEC_FOLDER = new ResourcePath.Folder(new String[] {"META-INF", "Car Workspace Spec"});
    
    /**
     * Instances of Car are meant to be created by an instance of this Factory class. This factory
     * caches Cars that it creates, so that for the same name and File, the factory returns the same Car object
     * in its getCar() method.
     *
     * @author Joseph Wong
     */
    public final static class Factory {
        
        /**
         * A Map mapping a (car name, car file path) pair to the corresponding cached Car instance.
         */
        private final Map<Pair<String, String>, Car> carCache = new WeakHashMap<Pair<String, String>, Car>();

        /**
         * Factory method for getting a Car instance for the given name and File.
         * @param carName the name of the Car.
         * @param carFile the file which contains the Car.
         * @return the corresponding Car instance.
         */
        public synchronized Car getCar(String carName, File carFile) {
            
            Pair<String, String> carID = new Pair<String, String>(carName, carFile.getPath());
            
            Car car = carCache.get(carID);
            
            if (car == null) {
                car = new Car(carName, carFile);
                carCache.put(carID, car);
            }
            
            return car;
        }
    }
    
    /**
     * The Car.Accessor provides read access to a Car in its role as a container for other resources.
     *
     * @author Joseph Wong
     */
    public static final class Accessor {
        
        /**
         * The jar file manager that encapsulates the underlying Car file.
         */
        private final JarFileManager jarFileManager;
        
        /**
         * The registry of the resource managers which provide access to resources stored <i>within</i> the Car.
         */
        private final ResourceManagerRegistry resourceManagerRegistry;
        
        /**
         * Private constructor for this class. Instances should be created via {@link Car#getAccessor(Status)}.
         * @param jarFileManager the jar file manager that encapsulates the underlying Car file.
         */
        private Accessor(JarFileManager jarFileManager) {
            if (jarFileManager == null) {
                throw new NullPointerException("jarFileManager cannot be null.");
            }
            
            this.jarFileManager = jarFileManager;
            resourceManagerRegistry = new ResourceManagerRegistry();
            resourceManagerRegistry.registerResourceManager(new CALSourceManager(new CALSourceJarStore(jarFileManager)));
            resourceManagerRegistry.registerResourceManager(new MetadataManager(new MetadataJarStore(jarFileManager)));
            resourceManagerRegistry.registerResourceManager(new GemDesignManager(new GemDesignJarStore(jarFileManager)));
            resourceManagerRegistry.registerResourceManager(new WorkspaceDeclarationManager(new WorkspaceDeclarationJarStore(jarFileManager)));
            resourceManagerRegistry.registerResourceManager(new UserResourceManager(new UserResourceJarStore(jarFileManager)));
        }
        
        /**
         * @return the name of the Car file.
         */
        public String getCarFileName() {
            return getJarFile().getName();
        }
        
        /**
         * @return the underlying JarFile representing the Car file.
         */
        public JarFile getJarFile() {
            return jarFileManager.getJarFile();
        }
        
        /**
         * @return the jar file manager encapsulating the Car file.
         */
        public JarFileManager getJarFileManager() {
            return jarFileManager;
        }
        
        /**
         * Registers a resource manager for accessing resources within the Car.
         * 
         * Package-scoped... expected to be called only by {@link VirtualResourceManager}
         * 
         * @param resourceManager
         */
        void registerResourceManager(ResourceManager resourceManager) {
            resourceManagerRegistry.registerResourceManager(resourceManager);
        }
        
        /**
         * @return the registered source manager, or null if there is none.
         */
        public CALSourceManager getSourceManager() {
            return resourceManagerRegistry.getSourceManager();
        }
        
        /**
         * @return the registered metadata manager, or null if there is none.
         */
        public MetadataManager getMetadataManager() {
            return resourceManagerRegistry.getMetadataManager();
        }
        
        /**
         * @return the registered gem design manager, or null if there is none.
         */
        public GemDesignManager getDesignManager() {
            return resourceManagerRegistry.getDesignManager();
        }
        
        /**
         * @return the registered workspace declaration manager, or null if there is none.
         */
        public WorkspaceDeclarationManager getWorkspaceDeclarationManager() {
            return resourceManagerRegistry.getWorkspaceDeclarationManager();
        }
        
        /**
         * @return the registered user resource manager, or null if there is none.
         */
        public UserResourceManager getUserResourceManager() {
            return resourceManagerRegistry.getUserResourceManager();
        }
        
        /**
         * Returns the resource manager which handles resources stored in the Car of a given type.
         * @param resourceType the String which identifies the type of resource.
         * @return the resource manager which handles resource of that type, or null if there isn't any.
         */
        public ResourceManager getResourceManager(String resourceType) {
            return resourceManagerRegistry.getResourceManager(resourceType);
        }
        
        /**
         * @return the resource managers in the registry.
         */
        public Set<ResourceManager> getResourceManagers() {
            return resourceManagerRegistry.getResourceManagers();
        }
        
        /**
         * Returns an InputStream for the named Car workspace spec file in the Car.
         * @param specName the name of the spec file.
         * @param status the tracking status object.
         * @return the InputStream for the spec file, or null if the file cannot be found in the Car.
         */
        public InputStream getWorkspaceSpec(String specName, Status status) {
            String relativePath = CAR_WORKSPACE_SPEC_FOLDER.extendFile(specName).getPathStringMinusSlash();
            JarFile jarFile = getJarFile();
            
            try {
                ZipEntry entry = jarFile.getEntry(relativePath);
                
                if (entry == null) {
                    String errorString = "Error reading workspace spec from Car: The file " + relativePath + " cannot be found in the Car.";
                    status.add(new Status(Status.Severity.ERROR, errorString));
                    return null;
                    
                } else {
                    return jarFile.getInputStream(entry);
                }
                
            } catch (IOException e) {
                String errorString = "Error reading workspace spec from Car.";
                status.add(new Status(Status.Severity.ERROR, errorString));
            }
            
            return null;
        }
        
        /**
         * Returns the locators of the files inside the given folder.
         * @param folder the locator of the folder.
         * @param resourcePath the resource path that corresponds to the folder locator.
         * @return an array of ProgramResourceLocators for the files inside the folder.
         */
        ProgramResourceLocator[] getFolderMembers(ProgramResourceLocator.Folder folder, ResourcePath resourcePath) {
            String relativePath = resourcePath.getPathStringMinusSlash();
            
            Set<String> fileNames = jarFileManager.getFileNamesInFolder(relativePath);
            
            if (fileNames == null) {
                return new ProgramResourceLocator[0];
                
            } else {
                List<ProgramResourceLocator> locatorList = new ArrayList<ProgramResourceLocator>();
                
                for (final String trailingPath : fileNames) {
                    locatorList.add(folder.extendFile(trailingPath));
                }
                
                return locatorList.toArray(new ProgramResourceLocator[locatorList.size()]);
            }
        }
        
        /**
         * Fetches the named resource from inside the Car.
         * @param resourcePath the path to the resource within the Car.
         * @return an InputStream for the resource, or null if the resource cannot be found in the Car.
         * @throws IOException
         */
        InputStream getResource(ResourcePath resourcePath) throws IOException {
            String relativePath = resourcePath.getPathStringMinusSlash();
            JarFile jarFile = getJarFile();
            ZipEntry entry = jarFile.getEntry(relativePath);
            if (entry == null) {
                throw new IOException("The resource " + relativePath + " cannot be found in the Car " + getCarFileName());
            }
            return jarFile.getInputStream(entry);
        }
        
        /**
         * Returns whether the named resource exists in the Car.
         * @param resourcePath the path to the resource within the Car.
         * @return true if the resource is found in the Car, false otherwise.
         */
        boolean doesResourceExist(ResourcePath resourcePath) {
            String relativePath = resourcePath.getPathStringMinusSlash();
            ZipEntry entry = getJarFile().getEntry(relativePath);
            return entry != null;
        }
        
        /**
         * Returns the last modified time of the named resource in the Car.
         * @param resourcePath the path to the resource within the Car.
         * @return the last modified time of the resource, or 0 if the resource cannot be found in the Car.
         */
        long getResourceLastModifiedTime(ResourcePath resourcePath) {
            String relativePath = resourcePath.getPathStringMinusSlash();
            ZipEntry entry = getJarFile().getEntry(relativePath);
            if (entry == null) {
                // according to the contract of java.io.File.lastModified(), the value 0 is returned if the file does not exist
                return 0;
            }
            return entry.getTime();
        }
        
        /**
         * Returns the size of the named resource in the Car.
         * @param resourcePath the path to the resource within the Car.
         * @return the size of resource, or 0 if the resource cannot be found in the Car.
         */
        long getResourceSize(ResourcePath resourcePath) {
            String relativePath = resourcePath.getPathStringMinusSlash();
            ZipEntry entry = getJarFile().getEntry(relativePath);
            if (entry == null) {
                // according to the contract of java.io.File.length(), the value 0 is returned if the file does not exist
                return 0;
            }
            return entry.getSize();
        }
        
        /**
         * @param resourcePath the path to the resource within the Car.
         * @return debugging information about the resource, e.g. the actual location of the resource. Can be null if the resource does not exist.
         */
        String getDebugInfo(ResourcePath resourcePath) {
            String relativePath = resourcePath.getPathStringMinusSlash();
            JarFile jarFile = getJarFile();
            ZipEntry entry = jarFile.getEntry(relativePath);
            if (entry == null) {
                return "The resource " + relativePath + " cannot be found in the Car " + getCarFileName();
            }
            return "from Car: " + getCarFileName() + ", entry: " + entry;
        }
    }

    /**
     * Private constructor. The {@link Car.Factory} should be used to create instances of this class.
     * 
     * @param carName the name of the Car.
     * @param carFile the file which contains the Car.
     */
    private Car(String carName, File carFile) {
        super(ResourceIdentifier.make(WorkspaceResource.CAR_RESOURCE_TYPE, CarFeatureName.getCarFeatureName(carName)));
        
        if (carFile == null) {
            throw new NullPointerException("File must not be null.");
        }
        
        this.file = carFile;
    }
    
    /**
     * @return the file which contains the Car.
     */
    public File getFile() {
        return file;
    }
    
    /**
     * @return the name of the Car.
     */
    public String getName() {
        return getIdentifier().getFeatureName().getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getInputStream(Status status) {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException fnfe) {
            String errorString = "Error loading Car.  File: \"" + file + "\" could not be found.";
            status.add(new Status(Status.Severity.ERROR, errorString));
        }
        
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getTimeStamp() {
        return file.lastModified();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getDebugInfo() {
        return "from file: " + file.getAbsolutePath();
    }

    /**
     * Returns an accessor for reading the resources within the Car.
     * 
     * @param status the tracking status object.
     * @return a Car.Accessor for accessing the Car, or null if there is a problem with reading the Car.
     */
    public synchronized Accessor getAccessor(Status status) {
        try {
            if (jarFileManager == null) {
                jarFileManager = new JarFileManager(new JarFile(file));
            }
            return new Accessor(jarFileManager);
        } catch (IOException e) {
            String errorString = "Error reading Car.";
            status.add(new Status(Status.Severity.ERROR, errorString));
        }
        
        return null;
    }
}
