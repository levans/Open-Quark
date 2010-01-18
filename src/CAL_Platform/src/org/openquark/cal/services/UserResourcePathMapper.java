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
 * UserResourcePathMapper.java
 * Creation date: Jun 2, 2006.
 * By: Joseph Wong
 */
package org.openquark.cal.services;

import java.util.Locale;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.services.ResourcePath.FilePath;
import org.openquark.cal.services.ResourcePath.Folder;


/**
 * A path mapper for user resources.
 * A user resource is a resource whose exact format is determined by the user code,
 * and not managed by the platform.
 * <p>
 * In particular, a feature name for a user resource is composed of the following parts:
 * <ul>
 * <li>a module name
 * <li>a resource name
 * <li>a resource extension... since the file format is determined by the user, the
 *    extension needs to be specified separately (because the locale information needs to go
 *    after the name and before the extension when the feature name is encoded as a file name).
 * </ul>
 * 
 * This path mapper takes all these components into account when constructing the file path.
 *
 * @author Joseph Wong
 */
public class UserResourcePathMapper implements ResourcePathMapper.Module {

    /** There is no fixed file extension for user resource files, so null is specified. */
    private static final String USER_RESOURCE_FILE_EXTENSION = null;
    
    /** The name of the user resource base folder. */
    private static final String USER_RESOURCE_BASE_FOLDER_NAME = "CAL_Resources";
    
    /** The disambiguating prefix in front of a locale specifier in a file name. */
    static final String USER_RESOURCE_LOCALE_PREFIX = "_";

    /** The base folder for this kind of resource. */
    private static final ResourcePath.Folder baseUserResourceFolder = new ResourcePath.Folder(new String[] {USER_RESOURCE_BASE_FOLDER_NAME});

    /** The path elements of the base folder for this kind of resource. */
    private static final String[] BASE_USER_RESOURCES_FOLDER_PATH_ELEMENTS = baseUserResourceFolder.getPathElements();
    
    /** Singleton instance. */
    public static final UserResourcePathMapper INSTANCE = new UserResourcePathMapper();

    /**
     * Private constructor.
     */
    private UserResourcePathMapper() {
    }

    /**
     * {@inheritDoc}
     */
    public Folder getBaseResourceFolder() {
        return baseUserResourceFolder;
    }

    /**
     * {@inheritDoc}
     */
    public FilePath getResourcePath(ResourceName resourceName) {
        FeatureName featureName = resourceName.getFeatureName();
        
        if (featureName.getType() != UserResourceFeatureName.TYPE) {
            return null;
        }
        
        UserResourceFeatureName userResourceFeatureName = (UserResourceFeatureName)featureName;

        String name = userResourceFeatureName.getName();
        
        if (name.indexOf(USER_RESOURCE_LOCALE_PREFIX) != -1) {
            throw new IllegalArgumentException("The locale identifier prefix '" + USER_RESOURCE_LOCALE_PREFIX + "' cannot appear in the name of a user resource.");
        }
        
        String extension = userResourceFeatureName.getExtension();
        Locale locale = LocalizedResourceName.localeOf(resourceName);
        
        String fileName = name;
        
        if (!LocaleUtilities.isInvariantLocale(locale)) {
            fileName += USER_RESOURCE_LOCALE_PREFIX + LocaleUtilities.localeToCanonicalString(locale);
        }
        
        if (extension.length() > 0) {
            fileName += "." + extension;
        }
        
        return ((ResourcePath.Folder)getModuleResourcePath(userResourceFeatureName.getModuleName())).extendFile(fileName);
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
        
        ModuleName moduleName = getModuleNameFromResourcePath(folder);
        if (moduleName == null) {
            return null;
        }
        
        int lastPeriodPos = fileName.lastIndexOf('.');
        
        String extension = fileName.substring(lastPeriodPos + 1);
        String firstPart = fileName.substring(0, lastPeriodPos);
        
        int localePrefixPos = firstPart.indexOf(USER_RESOURCE_LOCALE_PREFIX);
        
        String name;
        Locale locale;
        if (localePrefixPos != -1) {
            name = firstPart.substring(0, localePrefixPos);
            locale = LocaleUtilities.localeFromCanonicalString(firstPart.substring(localePrefixPos + USER_RESOURCE_LOCALE_PREFIX.length()));
        } else {
            name = firstPart;
            locale = LocaleUtilities.INVARIANT_LOCALE;
        }
        
        return new LocalizedResourceName(new UserResourceFeatureName(moduleName, name, extension), locale);
    }

    /**
     * {@inheritDoc}
     */
    public ModuleName getModuleNameFromResourcePath(ResourcePath moduleResourcePath) {
        if (moduleResourcePath instanceof Folder) {
            return getModuleNameFromResourcePath((Folder)moduleResourcePath);
        } else {
            return null;
        }
    }

    /**
     * Returns the module name represented by the given resource folder.
     * @param folder the folder representing a module.
     * @return the corresponding module name, or null if the path does not correspond to a module.
     */
    private ModuleName getModuleNameFromResourcePath(Folder folder) {
        String moduleName;
        
        String[] pathElementsOfFolder = folder.getPathElements();
        
        if (pathElementsOfFolder.length < 1) {
            moduleName = null;
        }
        
        // first, we need to make sure that the prefix of the folder matches that of baseScriptsFolder
        int moduleNameStartPos = 0;
        for (int i = 0; i < BASE_USER_RESOURCES_FOLDER_PATH_ELEMENTS.length; i++) {
            if (!pathElementsOfFolder[i].equals(BASE_USER_RESOURCES_FOLDER_PATH_ELEMENTS[i])) {
                moduleName = null;
            }
            moduleNameStartPos++;
        }
        
        // the remainder of the folder then becomes the module name qualifier
        StringBuilder moduleNameBuilder = new StringBuilder();
        for (int j = moduleNameStartPos; j < pathElementsOfFolder.length; j++) {
            if (j > moduleNameStartPos) {
                moduleNameBuilder.append('.');
            }
            moduleNameBuilder.append(FileSystemResourceHelper.fromFileSystemName(pathElementsOfFolder[j]));
        }
        
        moduleName = moduleNameBuilder.toString();
        return ModuleName.make(moduleName);
    }
    
    /**
     * {@inheritDoc}
     */
    public String getFileExtension() {
        return USER_RESOURCE_FILE_EXTENSION;
    }
    
    /**
     * {@inheritDoc}
     */
    public ResourcePath getModuleResourcePath(ModuleName moduleName) {
        String[] components = moduleName.toSourceText().split("\\.");
        int nComponents = components.length;
        
        ResourcePath.Folder result = baseUserResourceFolder;
        for (int i = 0; i < nComponents; i++) {
            result = result.extendFolder(FileSystemResourceHelper.getFileSystemName(components[i]));
        }
        return result;
    }
}
