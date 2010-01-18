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
 * MetadataPathStoreHelper.java
 * Creation date: Nov 23, 2004.
 * By: Edward Lam
 */
package org.openquark.cal.metadata;

import java.util.ArrayList;
import java.util.List;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.services.AbstractResourcePathStore;
import org.openquark.cal.services.CALFeatureName;
import org.openquark.cal.services.ResourceName;
import org.openquark.cal.services.ResourcePath;
import org.openquark.cal.services.FeatureName.FeatureType;
import org.openquark.cal.services.ResourcePath.Folder;



/**
 * This is a helper class which contains methods to determine which features are present in a metadata path store.
 * @author Edward Lam
 */
class MetadataPathStoreHelper {
    
    /** The associated metadata path store */
    private final AbstractResourcePathStore abstractMetadataPathStore;

    /**
     * Constructor for a MetadataPathStoreHelper.
     * @param abstractMetadataPathStore the associated metadata path store.
     */
    public MetadataPathStoreHelper(AbstractResourcePathStore abstractMetadataPathStore) {
        this.abstractMetadataPathStore = abstractMetadataPathStore;
    }
    
    /**
     * Get the names of resources associated with the module.
     * @param moduleName the name of a module.
     * @return (List of ResourceName) the names of resources associated with the module.
     */
    public List<ResourceName> getModuleResourceNameList(ModuleName moduleName) {
        List<ResourceName> metadataResourceNameList = new ArrayList<ResourceName>();
        
        // Module
        fillModuleResourceNameListByFeatureType(moduleName, metadataResourceNameList, CALFeatureName.MODULE);
        
        // Functions
        fillModuleResourceNameListByFeatureType(moduleName, metadataResourceNameList, CALFeatureName.FUNCTION);
        
        // Class methods
        fillModuleResourceNameListByFeatureType(moduleName, metadataResourceNameList, CALFeatureName.CLASS_METHOD);
        
        // Data constructors
        fillModuleResourceNameListByFeatureType(moduleName, metadataResourceNameList, CALFeatureName.DATA_CONSTRUCTOR);
        
        // Type classes
        fillModuleResourceNameListByFeatureType(moduleName, metadataResourceNameList, CALFeatureName.TYPE_CLASS);
        
        // Type constructors
        fillModuleResourceNameListByFeatureType(moduleName, metadataResourceNameList, CALFeatureName.TYPE_CONSTRUCTOR);
        
        // Class instances
        fillModuleResourceNameListByFeatureType(moduleName, metadataResourceNameList, CALFeatureName.CLASS_INSTANCE);
        
        // Instance methods
        fillModuleResourceNameListByFeatureType(moduleName, metadataResourceNameList, CALFeatureName.INSTANCE_METHOD);
        
        return metadataResourceNameList;
    }

    /**
     * Fills the given list with the names of resources in the given module and of the given feature type.
     * @param moduleName the module of the resources.
     * @param metadataResourceNameList the list to be filled.
     * @param featureType the feature type of the resources whose names are to be returned.
     */
    private void fillModuleResourceNameListByFeatureType(ModuleName moduleName, List<ResourceName> metadataResourceNameList, FeatureType featureType) {
        ResourcePath.Folder modulePath = getFeatureFolder(moduleName, featureType);
        for (final ResourceName resourceName : abstractMetadataPathStore.getFolderResourceNames(modulePath)) {
            metadataResourceNameList.add(resourceName);
        }
    }
    
    /**
     * @param moduleName the name of a CAL module.
     * @param featureType the type of a CAL feature.
     * @return the folder in which the feature's metadata should be stored. 
     */
    private Folder getFeatureFolder(ModuleName moduleName, FeatureType featureType) {
        return ((MetadataPathMapper)abstractMetadataPathStore.getPathMapper()).getFeatureFolder(moduleName, featureType);
    }
    
    /**
     * Constructs a filter for keeping only the resource names whose feature names match the given feature name.
     * @param featureName the desired feature name.
     * @return a ResourceName.Filter representing the desired filter.
     */
    public ResourceName.Filter makeFeatureNameFilter(final CALFeatureName featureName) {
        return new ResourceName.Filter() {
            public boolean accept(ResourceName resourceName) {
                return resourceName.getFeatureName().equals(featureName);
            }};
    }
}
