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
 * CALSourcePathMapper.java
 * Creation date: Nov 23, 2004.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.services.ResourcePath.FilePath;
import org.openquark.cal.services.ResourcePath.Folder;



/**
 * A ResourcePathMapper for CAL source file resources.
 * @author Edward Lam
 */
public class CALSourcePathMapper implements ResourcePathMapper.Module {

    /** The file extension for cal source files.*/
    public static final String CAL_FILE_EXTENSION = "cal";

    /** The folder for CAL source scripts. */
    public static final String SCRIPTS_BASE_FOLDER = "CAL";

    /** The base folder for this resource. */
    private static final ResourcePath.Folder baseScriptsFolder = new ResourcePath.Folder(new String[] {SCRIPTS_BASE_FOLDER});

    /** The path elements of the base folder for this resource. */
    private static final String[] BASE_SCRIPTS_FOLDER_PATH_ELEMENTS = baseScriptsFolder.getPathElements();

    /** Singleton instance. */
    public static final CALSourcePathMapper INSTANCE = new CALSourcePathMapper();
    

    /**
     * Constructor for a CALSourcePathMapper.
     */
    private CALSourcePathMapper() {
    }
    
    /**
     * {@inheritDoc}
     */
    public Folder getBaseResourceFolder() {
        return baseScriptsFolder;
    }

    /**
     * {@inheritDoc}
     */
    public ResourcePath getModuleResourcePath(ModuleName moduleName) {
        String[] components = moduleName.toSourceText().split("\\.");
        
        int nMinusOne = components.length - 1;
        
        ResourcePath.Folder moduleNameQualifierFolder = baseScriptsFolder;
        for (int i = 0; i < nMinusOne; i++) {
            moduleNameQualifierFolder = moduleNameQualifierFolder.extendFolder(components[i]);
        }
        
        String fileName = components[nMinusOne] + "." + CAL_FILE_EXTENSION;
        return moduleNameQualifierFolder.extendFile(fileName);
    }
    
    /**
     * {@inheritDoc}
     */
    public ModuleName getModuleNameFromResourcePath(ResourcePath moduleResourcePath) {
        if (moduleResourcePath instanceof FilePath) {
            return ModuleName.make(getResourceNameFromPath((FilePath)moduleResourcePath).getFeatureName().getName());
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public FilePath getResourcePath(ResourceName resourceName) {
        ModuleName moduleName = checkModuleName(resourceName);
        return (ResourcePath.FilePath)getModuleResourcePath(moduleName);
    }
    
    /**
     * {@inheritDoc}
     */
    public ResourceName getResourceNameFromPath(FilePath path) {
        return getResourceNameFromFolderAndFileName(path.getFolder(), path.getFileName());
    }
    
    /**
     * {@inheritDoc}
     */
    public ResourceName getResourceNameFromFolderAndFileName(Folder folder, String fileName) {
        String unqualifiedModuleName = FileSystemResourceHelper.stripFileExtension(fileName, CAL_FILE_EXTENSION, false);
        if (unqualifiedModuleName != null) {
            
            String[] folderPathElements = folder.getPathElements();
            
            // first, we need to make sure that the prefix of the folder matches that of baseScriptsFolder
            int moduleNameStartPos = 0;
            for (int i = 0; i < BASE_SCRIPTS_FOLDER_PATH_ELEMENTS.length; i++) {
                if (!folderPathElements[i].equals(BASE_SCRIPTS_FOLDER_PATH_ELEMENTS[i])) {
                    return null;
                }
                moduleNameStartPos++;
            }
            
            // the remainder of the folder then becomes the module name qualifier
            StringBuilder moduleNameBuilder = new StringBuilder();
            for (int j = moduleNameStartPos; j < folderPathElements.length; j++) {
                moduleNameBuilder.append(folderPathElements[j]).append('.');
            }
            
            moduleNameBuilder.append(unqualifiedModuleName);
            
            return new ResourceName(CALFeatureName.getModuleFeatureName(ModuleName.make(moduleNameBuilder.toString())));
        } else {
            return null;
        }
    }
    
    /**
     * Given a module name, returns a corresponding path which is relative to the base resource folder.
     * @param moduleName a module name.
     * @return the corresponding path which is relative to the base resource folder.
     */
    public String getPathForModuleRelativeToBaseResourceFolder(ModuleName moduleName) {
        return moduleName.toSourceText().replace('.', '/') + "." + CAL_FILE_EXTENSION;
    }
    
    /**
     * Given a path which is relative to the base resource folder, returns the corresponding module name.
     * @param pathRelativeToBaseResourceFolder a path which is relative to the base resource folder.
     * @return the corresponding module name.
     */
    public ModuleName getModuleNameFromPathRelativeToBaseResourceFolder(String pathRelativeToBaseResourceFolder) {
        String modulePath = FileSystemResourceHelper.stripFileExtension(pathRelativeToBaseResourceFolder, CAL_FILE_EXTENSION, false);
        if (modulePath != null) {
            return ModuleName.make(modulePath.replace('/', '.'));
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getFileExtension() {
        return CAL_FILE_EXTENSION;
    }

    /**
     * Check that a feature name represents a module.
     * @param resourceName the name of the module resource.
     * @return the module name if the feature represents a module.
     * @throws IllegalArgumentException if the ResourceIdentifier does not represent a module.
     */
    private ModuleName checkModuleName(ResourceName resourceName) {
        FeatureName featureName = resourceName.getFeatureName();
        if (featureName.getType() != CALFeatureName.MODULE) {
            throw new IllegalArgumentException("The feature name must represent a module, not the type " + featureName.getType());
        }
        return ((CALFeatureName)featureName).toModuleName();
    }

}