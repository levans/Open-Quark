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
 * GemDesignPathMapper.java
 * Creation date: Nov 23, 2004.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.services.ResourcePath.FilePath;
import org.openquark.cal.services.ResourcePath.Folder;


/**
 * A ResourcePathMapper for GemDesign resources.
 * @author Edward Lam
 */
public class GemDesignPathMapper implements ResourcePathMapper.Module {

    /** The file extension for metadata files. */
    private static final String DESIGN_FILE_EXTENSION = "xml";

    /** The default base folder for saved gem designs. */
    private static final String DESIGN_BASE_FOLDER = "Designs";

    /** The base folder for this resource. */
    private static final ResourcePath.Folder baseDesignFolder = new ResourcePath.Folder(new String[] {DESIGN_BASE_FOLDER});
    
    /** Singleton instance. */
    public static final GemDesignPathMapper INSTANCE = new GemDesignPathMapper();
    
    
    /**
     * Constructor for a GemDesignPathMapper.
     */
    private GemDesignPathMapper() {
    }
    
    /**
     * {@inheritDoc}
     */
    public Folder getBaseResourceFolder() {
        return baseDesignFolder;
    }

    /**
     * {@inheritDoc}
     */
    public ResourcePath getModuleResourcePath(ModuleName moduleName) {
        String metadataFolderName = FileSystemResourceHelper.getFileSystemName(moduleName.toSourceText());
        return baseDesignFolder.extendFolder(metadataFolderName);
    }

    /**
     * {@inheritDoc}
     */
    public ModuleName getModuleNameFromResourcePath(ResourcePath moduleResourcePath) {
        return ModuleName.make(FileSystemResourceHelper.fromFileSystemName(moduleResourcePath.getName()));
    }

    /**
     * {@inheritDoc}
     */
    public FilePath getResourcePath(ResourceName resourceName) {
        QualifiedName designName = checkDesignName(resourceName);
        String fileName = FileSystemResourceHelper.getFileSystemName(designName.getQualifiedName() + "." + DESIGN_FILE_EXTENSION);
        String moduleNameString = FileSystemResourceHelper.getFileSystemName(designName.getModuleName().toSourceText());
        return baseDesignFolder.extendFolder(moduleNameString).extendFile(fileName);
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
     * Constructs a resource name corresponding to the feature of a gem design at the given file name.
     * @param fileName the name of the gem design file.
     * @return the corresponding resource name.
     */
    private ResourceName getResourceNameFromFileName(String fileName) {
        String unencodedFileName = FileSystemResourceHelper.stripFileExtension(fileName, DESIGN_FILE_EXTENSION, true);
        if (unencodedFileName != null) {
            QualifiedName qualifiedName = QualifiedName.makeFromCompoundName(unencodedFileName);
            return new ResourceName(CALFeatureName.getFunctionFeatureName(qualifiedName));
        } else {
            return null;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public String getFileExtension() {
        return DESIGN_FILE_EXTENSION;
    }

    /**
     * Check that a feature name represents a design.
     * @param resourceName the name of the design resource.
     * @return the design name if the feature represents a module.
     * @throws IllegalArgumentException if the ResourceIdentifier does not represent a design.
     */
    protected QualifiedName checkDesignName(ResourceName resourceName) {
        FeatureName featureName = resourceName.getFeatureName();
        if (featureName.getType() != CALFeatureName.FUNCTION) {
            throw new IllegalArgumentException("The feature name must represent a function, not the type " + featureName.getType());
        }
        return ((CALFeatureName)featureName).toQualifiedName();
    }
}