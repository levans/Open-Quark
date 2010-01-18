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
 * CarPathMapper.java
 * Creation date: Jan 10, 2006.
 * By: Joseph Wong
 */
package org.openquark.cal.services;

import org.openquark.cal.services.ResourcePath.FilePath;
import org.openquark.cal.services.ResourcePath.Folder;

/**
 * A ResourcePathMapper for CAL Archives (Cars) as resources.
 *
 * @author Joseph Wong
 */
public class CarPathMapper implements ResourcePathMapper {

    /** The file extension for Cars. */
    private static final String CAR_FILE_EXTENSION = "car";

    /** The default base folder for saved Cars. */
    private static final String CAR_BASE_FOLDER = "Car";

    /** The base folder for this resource. */
    private static final ResourcePath.Folder baseResourceFolder = new ResourcePath.Folder(new String[] {CAR_BASE_FOLDER});
    
    /** Singleton instance. */
    public static final CarPathMapper INSTANCE = new CarPathMapper();
    
    /**
     * Private constructor for a CarPathMapper.
     */
    private CarPathMapper() {
    }

    /**
     * {@inheritDoc}
     */
    public Folder getBaseResourceFolder() {
        return baseResourceFolder;
    }

    /**
     * {@inheritDoc}
     */
    public FilePath getResourcePath(ResourceName resourceName) {
        String carName = checkCarName(resourceName);
        return getCarPath(carName);
    }

    /**
     * {@inheritDoc}
     */
    public ResourceName getResourceNameFromPath(FilePath path) {
        return getResourceNameFromFileName(path.getFileName());
    }

    /**
     * {@inheritDoc}
     */
    public ResourceName getResourceNameFromFolderAndFileName(Folder folder, String fileName) {
        return getResourceNameFromFileName(fileName);
    }

    /**
     * {@inheritDoc}
     */
    public String getFileExtension() {
        return CAR_FILE_EXTENSION;
    }
    
    /**
     * Get the base path to a Car with the given name.
     * 
     * @param carName the name of the Car.
     * @return the path under which the Car should be located.
     */
    public FilePath getCarPath(String carName) {
        return baseResourceFolder.extendFile(carName);
    }
    
    /**
     * Constructs a resource name corresponding to a Car at the given file name.
     * @param fileName the name of the Car file.
     * @return the corresponding resource name.
     */
    private ResourceName getResourceNameFromFileName(String fileName) {
        return new ResourceName(CarFeatureName.getCarFeatureName(fileName));
    }

    /**
     * Check that a feature name represents a Car.
     * @param resourceName the name of the Car resource.
     * @return the Car name if the feature represents a Car.
     * @throws IllegalArgumentException if the ResourceIdentifier does not represent a Car.
     */
    protected String checkCarName(ResourceName resourceName) {
        FeatureName featureName = resourceName.getFeatureName();
        if (featureName.getType() != CarFeatureName.TYPE) {
            throw new IllegalArgumentException("The feature name must represent a Car, not the type " + featureName.getType());
        }
        return featureName.getName();
    }
}
