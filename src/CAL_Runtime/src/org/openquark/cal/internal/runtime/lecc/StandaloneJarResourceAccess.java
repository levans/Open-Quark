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
 * StandaloneJarResourceAccess.java
 * Created: Jun 5, 2007
 * By: Joseph Wong
 */

package org.openquark.cal.internal.runtime.lecc;

import java.io.InputStream;
import java.util.Locale;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.runtime.ResourceAccess;
import org.openquark.cal.services.LocaleUtilities;

/**
 * An implementation of the {@link ResourceAccess} interface for use in a standalone jar. The user resources
 * are packaged up into the jar, using the same directory structure as in the StandardVault or in a Car/Car-jar.
 * 
 * Note that StandaloneJarBuilder is the builder that creates standalone jars.
 *
 * @author Joseph Wong
 */
public final class StandaloneJarResourceAccess implements ResourceAccess {

    /** The disambiguating prefix in front of a locale specifier in a file name. */
    private static final String USER_RESOURCE_LOCALE_PREFIX = "_";

    /** The base folder for this kind of resource. */
    private static final String baseUserResourceFolder = "CAL_Resources";
    
    /**
     * The class loader that loaded the standalone JAR, which can then be used to access resources
     * in the JAR. (needed because the class loader which loaded this class may be an <i>ancestor</i>
     * of the one which loaded the standalone JAR (e.g. the bootstrap class loader), and thus may
     * not have access to resources necessary for the standalone JAR to run).
     * 
     * Cannot be null.
     */
    private final ClassLoader standaloneJarClassLoader;

    /**
     * Constructs an instance of this class.
     * @param standaloneJarClassLoader the class loader that loaded the standalone JAR. Cannot be null.
     */
    public StandaloneJarResourceAccess(final ClassLoader standaloneJarClassLoader) {
        if (standaloneJarClassLoader == null) {
            throw new NullPointerException("standaloneJarClassLoader cannot be null");
        }
        this.standaloneJarClassLoader = standaloneJarClassLoader;
    }
    
    /**
     * {@inheritDoc}
     */
    public InputStream getUserResource(final String moduleNameAsString, final String name, final String extension, final Locale locale) {
        
        final ModuleName moduleName = ModuleName.make(moduleNameAsString);
        
        // ClassLoader.getResourceAsStream() expects a path without the leading slash.
        final String pathString = getResourcePath(moduleName, name, extension, locale);
        
        return standaloneJarClassLoader.getResourceAsStream(pathString);
    }

    /**
     * An inlined implementation of {@code UserResourcePathMapper.getResourcePath}.
     * 
     * Returns the base path to a resource representing a given feature.
     * 
     * @param moduleName the name of the module associated with the user resource.
     * @param name the name of the resource, not including any file extensions. Cannot contain the character '_'.
     * @param extension the file extension for the user resource.
     * @param locale the locale for which the resource is to be fetched.
     * @return the corresponding resource path without a leading slash.
     */
    private String getResourcePath(final ModuleName moduleName, final String name, final String extension, final Locale locale) {
        if (name.indexOf(USER_RESOURCE_LOCALE_PREFIX) != -1) {
            throw new IllegalArgumentException("The locale identifier prefix '" + USER_RESOURCE_LOCALE_PREFIX + "' cannot appear in the name of a user resource.");
        }
        
        String fileName = name;
        
        if (!LocaleUtilities.isInvariantLocale(locale)) {
            fileName += USER_RESOURCE_LOCALE_PREFIX + LocaleUtilities.localeToCanonicalString(locale);
        }
        
        if (extension.length() > 0) {
            fileName += "." + extension;
        }
        return getModuleResourcePath(moduleName) + '/' + fileName;
    }

    /**
     * An inlined implementation of {@code UserResourcePathMapper.getModuleResourcePath}.
     * 
     * Returns the base path to the file or folder which contains the resources for the given module.
     * 
     * @param moduleName the name of the module.
     * @return the path (without leading slash) under which the resources corresponding to the module should be located.
     */
    private String getModuleResourcePath(final ModuleName moduleName) {
        String[] components = moduleName.toSourceText().split("\\.");
        int nComponents = components.length;
        
        StringBuilder result = new StringBuilder(baseUserResourceFolder);
        for (int i = 0; i < nComponents; i++) {
            result.append('/').append(getFileSystemName(components[i]));
        }
        return result.toString();
    }

    /**
     * An inlined implementation of {@code FileSystemResourceHelper.getFileSystemName}.
     * 
     * Converts a desired filename into an a name for use in the file system by
     * preceding all capital letters with hyphens. This is needed because not
     * all filesystems are case-sensitive (ie: Windows), but CAL is case sensitive.
     * 
     * @param name the name to convert.
     * @return the valid file name.
     */
    private String getFileSystemName(final String name) {
        StringBuilder fileName = new StringBuilder();
        
        int length = name.length();
        for (int i = 0; i < length; i++) {
        
            char c = name.charAt(i);
            
            if (Character.isUpperCase(c)) {
                fileName.append("-" + c);
            } else {
                fileName.append(c);
            }
        }
        
        return fileName.toString();
    }
}
