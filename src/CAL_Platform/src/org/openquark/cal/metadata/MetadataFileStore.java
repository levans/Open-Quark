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
 * MetadataFileStore.java
 * Creation date: Jul 22, 2004.
 * By: Edward Lam
 */
package org.openquark.cal.metadata;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.services.CALFeatureName;
import org.openquark.cal.services.FileSystemResourceHelper;
import org.openquark.cal.services.ModuleResourceStoreHelper;
import org.openquark.cal.services.ResourceFileStore;
import org.openquark.cal.services.ResourceName;
import org.openquark.cal.services.WorkspaceResource;
import org.openquark.cal.services.ResourcePath.Folder;




/**
 * Warning- this class should only be used by the CAL services implementation. It is not part of the
 * external API of the CAL platform.
 * <p>
 * A metadata store using files and directories in the file system.
 * @author Edward Lam
 */
public class MetadataFileStore extends ResourceFileStore.Module implements MetadataStore {

    /** The associated path store helper.*/
    private final MetadataPathStoreHelper metadataStoreHelper;
    
    /**
     * Constructor for a metadata file store.
     * @param rootDirectory the directory under which the metadata folder resides.
     */
    public MetadataFileStore(File rootDirectory) {
        super(rootDirectory, WorkspaceResource.METADATA_RESOURCE_TYPE, MetadataPathMapper.INSTANCE);
        metadataStoreHelper = new MetadataPathStoreHelper(this);
    }

    /**
     * {@inheritDoc}
     */
    public Set<ModuleName> getModuleNames() {
        File baseResourceDirectory = FileSystemResourceHelper.getResourceFile(getPathMapper().getBaseResourceFolder(), getRootDirectory(), false);
        return ModuleResourceStoreHelper.convertCollectionOfModuleNameStringsToSetOfModuleNames(
            FileSystemResourceHelper.getDirectoryNamesWithFileExtension(baseResourceDirectory, getFileExtension(), true));
    }
    
    /**
     * {@inheritDoc}
     */
    public List<ResourceName> getModuleResourceNameList(ModuleName moduleName) {
        return metadataStoreHelper.getModuleResourceNameList(moduleName);
    }

    /**
     * {@inheritDoc}
     */
    public List<ResourceName> getMetadataResourceNamesForAllLocales(CALFeatureName featureName) {
        Folder folder = ((MetadataPathMapper)getPathMapper()).getFeatureFolder(featureName);
        return getFilteredFolderResourceNames(folder, metadataStoreHelper.makeFeatureNameFilter(featureName));
    }
}
