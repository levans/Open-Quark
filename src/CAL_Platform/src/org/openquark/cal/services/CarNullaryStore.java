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
 * CarNullaryStore.java
 * Creation date: Jan 10, 2006.
 * By: Joseph Wong
 */
package org.openquark.cal.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.openquark.cal.services.ResourceName.Filter;
import org.openquark.cal.services.ResourcePath.Folder;


/**
 * A CAL Archive (Car) store for the nullary workspace, using files and folders in the file system.
 *
 * @author Joseph Wong
 */
/*
 * @implementation
 * 
 * This store (also the WorkspaceDeclarationNullaryStore) differs from other nullary stores in that it
 * encompasses more than just the resources within the regular nullary environment's organization and
 * directory structure.
 * 
 * In this case, Cars appearing as Car-jars on the classpath are treated as Cars in the StandardVault.
 * This is possible because Cars are currently packaged using the Jar file format. The .jar file extension
 * is stripped off of a Car-jar's file name to obtain the canonical name of the Car. For example,
 * <tt>cal.platform.car.jar</tt> is the Car-jar that defines the StandardVault Car <tt>cal.platform.car</tt>.
 * 
 * This extension in semantics is achieved by overriding all of the interface methods defined in ResourceNullaryStore,
 * so that the methods can all be rendered aware of the existence of Car-jars.
 * 
 * In terms of precedence, the Cars located in the nullary envrionment's regular directory structure take
 * precedence over the Car-jars on the classpath.
 */
class CarNullaryStore extends ResourceNullaryStore implements CarStore {
    
    /** The factory for making Cars. */
    private final Car.Factory carFactory;
    
    /** The names of Car-jars on the classpath. */
    private Map<String, File> carJarFiles;

    /**
     * Constructs a CarNullaryStore.
     */
    public CarNullaryStore() {
        super(WorkspaceResource.CAR_RESOURCE_TYPE, CarPathMapper.INSTANCE);
        this.carFactory = new Car.Factory();
    }

    /**
     * {@inheritDoc}
     */
    public Set<ResourceName> getCarNames() {
        return new HashSet<ResourceName>(getFolderResourceNames(getPathMapper().getBaseResourceFolder()));
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<WorkspaceResource> getResourceIterator() {
        return CarPathStoreHelper.getResourceIterator(this);
    }

    /**
     * {@inheritDoc}
     */
    public synchronized Car getCar(String carName) {
        ResourceName carResourceName = new ResourceName(CarFeatureName.getCarFeatureName(carName));
        final File carFile;
        
        // we use super.hasFeature here to determine whether the nullary environment has the Car,
        // since we override hasFeature in this class to include the Car-jars on the classpath as well.
        if (super.hasFeature(carResourceName)) {
            carFile = getFeatureFile(carResourceName, false);
        } else {
            carFile = getCarJar(carResourceName);
        }
        
        return carFactory.getCar(carName, carFile);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ResourceName> getFilteredFolderResourceNames(Folder folder, Filter filter) {
        // we combine the resources from the nullary environment and those from the classpath
        
        List<ResourceName> baseNullaryEnvResult = super.getFilteredFolderResourceNames(folder, filter);
        
        List<ResourceName> finalList = new ArrayList<ResourceName>();
        finalList.addAll(baseNullaryEnvResult);
        
        if (folder.equals(getPathMapper().getBaseResourceFolder())) {
            for (final String carName : getCarJarFiles().keySet()) {
                ResourceName carResourceName = new ResourceName(CarFeatureName.getCarFeatureName(carName));
                
                if (filter.accept(carResourceName)) {
                    finalList.add(carResourceName);
                }
            }
        }
        
        return finalList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ResourceName> getFolderResourceNames(Folder folder) {
        // we combine the resources from the nullary environment and those from the classpath
        
        List<ResourceName> baseNullaryEnvResult = super.getFolderResourceNames(folder);
        
        List<ResourceName> finalList = new ArrayList<ResourceName>();
        finalList.addAll(baseNullaryEnvResult);
        
        if (folder.equals(getPathMapper().getBaseResourceFolder())) {
            for (final String carName : getCarJarFiles().keySet()) {
                finalList.add(new ResourceName(CarFeatureName.getCarFeatureName(carName)));
            }
        }
        
        return finalList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getInputStream(ResourceName resourceName) {
        
        InputStream baseNullaryEnvResult = super.getInputStream(resourceName);
        
        if (baseNullaryEnvResult != null) {
            return baseNullaryEnvResult;
            
        } else {
            // the Car does not exist in the nullary environment, so check the Car-jars on the classpath
            File carJarFile = getCarJar(resourceName);
            if (carJarFile != null) {
                try {
                    return new FileInputStream(carJarFile);
                } catch (FileNotFoundException e) {
                    return null;
                }
            } else {
                return null;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OutputStream getOutputStream(ResourceName resourceName, Status saveStatus) {
        
        // we do not support opening output streams to Car-jars on the classpath
        return super.getOutputStream(resourceName, saveStatus);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDebugInfo(ResourceName resourceName) {
        
        String baseNullaryEnvResult = super.getDebugInfo(resourceName);
        
        if (baseNullaryEnvResult != null) {
            return baseNullaryEnvResult;
            
        } else {
            // the Car does not exist in the nullary environment, so check the Car-jars on the classpath
            File carJarFile = getCarJar(resourceName);
            if (carJarFile != null) {
                return "from Car-jar: " + carJarFile.getAbsolutePath();
            } else {
                return null;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getTimeStamp(ResourceName resourceName) {
        
        long baseNullaryEnvResult = super.getTimeStamp(resourceName);
        
        if (baseNullaryEnvResult != 0) {
            return baseNullaryEnvResult;
            
        } else {
            // the Car does not exist in the nullary environment, so check the Car-jars on the classpath
            File carJarFile = getCarJar(resourceName);
            if (carJarFile != null && carJarFile.exists()) {
                return carJarFile.lastModified();
            } else {
                return 0;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasFeature(ResourceName resourceName) {
        
        // if the Car does not exist in the nullary environment, then check the Car-jars on the classpath
        return super.hasFeature(resourceName) || getCarJar(resourceName) != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRemovable(ResourceName resourceName) {
        
        // we do not support removing Car-jars on the classpath
        return super.isRemovable(resourceName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWriteable() {
        
        // this store itself is writeable
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWriteable(ResourceName resourceName) {
        
        // we do not support writing to Car-jars on the classpath
        return super.isWriteable(resourceName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAllResources(Status removeStatus) {
        
        // we do not support removing Car-jars on the classpath
        super.removeAllResources(removeStatus);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeResource(ResourceName resourceName, Status removeStatus) {
        
        // we do not support removing Car-jars on the classpath
        super.removeResource(resourceName, removeStatus);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean renameResource(ResourceName oldResourceName, ResourceName newResourceName, ResourceStore newResourceStore, Status renameStatus) {
        
        // we do not support renaming Car-jars on the classpath
        return super.renameResource(oldResourceName, newResourceName, newResourceStore, renameStatus);
    }
    
    /**
     * Returns the file associated with a Car-jar on the classpath.
     * @param resourceName the resource name for the Car to fetch.
     * @return the file associated with a Car-jar on the classpath; or null if no such jar exists.
     */
    private File getCarJar(ResourceName resourceName) {
        String carName = resourceName.getFeatureName().getName();
        File carJarFile = getCarJarFiles().get(carName);
        return carJarFile;
    }

    /**
     * @return a Map mapping Car names to the Car-jar files on the classpath.
     */
    private synchronized Map<String, File> getCarJarFiles() {
        if (carJarFiles != null) {
            return carJarFiles;
        }
        
        carJarFiles = new HashMap<String, File>();

        Set<File> carJarFilesInEnvironment = NullaryEnvironment.getNullaryEnvironment().getCarJarFilesInEnvironment();
        
        for (final File jarFile : carJarFilesInEnvironment) {
            String jarName = jarFile.getName();

            String carName = null;

            if (jarFile.exists()) {
                try {
                    JarFile jar = new JarFile(jarFile);
                    try {
                        Manifest manifest = jar.getManifest();
                        if (manifest != null) {

                            Attributes mainAttributes = manifest.getMainAttributes();
                            if (mainAttributes != null) {
                                carName = mainAttributes.getValue(ModulePackager.CAR_NAME_MANIFEST_ATTRIBUTE);
                            }
                        }
                    } finally {
                        jar.close();
                    }
                } catch (IOException e) {
                    // the jar file cannot be opened now, so use the default mechanism below
                }
            }

            if (carName == null) {
                // default to chopping off the ".jar" off the end
                carName = jarName.substring(0, jarName.length() - 4);
            }

            // if the Car has already been added to the map, then it must come from an earlier
            // classpath entry, and so we honour the first entry rather than this one
            if (!carJarFiles.containsKey(carName)) {
                carJarFiles.put(carName, jarFile);
            }
        }
        
        return carJarFiles;
    }
}
