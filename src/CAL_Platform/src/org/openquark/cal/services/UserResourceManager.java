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
 * UserResourceManager.java
 * Creation date: Jun 2, 2006.
 * By: Joseph Wong
 */
package org.openquark.cal.services;

import java.io.InputStream;
import java.util.Locale;

import org.openquark.cal.compiler.ModuleName;


/**
 * A resource manager for managing the loading of user resources from a resource store.
 * A user resource is a resource whose exact format is determined by the user code,
 * and not managed by the platform.
 *
 * @author Joseph Wong
 */
public class UserResourceManager extends ModuleResourceManager {

    /**
     * The resource store for user resources.
     */
    private final UserResourceStore userResourceStore;

    /**
     * Constructs a UserResourceManager.
     * @param userResourceStore the resource store for user resources.
     */
    public UserResourceManager(UserResourceStore userResourceStore) {
        if (userResourceStore == null) {
            throw new NullPointerException();
        }
        this.userResourceStore = userResourceStore;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResourceType() {
        return WorkspaceResource.USER_RESOURCE_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResourceStore getResourceStore() {
        return userResourceStore;
    }
    
    /**
     * Returns an InputStream for the named user resource in the specified locale. Note that this
     * method does not implement any locale-fallback mechanism - it is up to the caller to do so.
     * 
     * @param moduleName the name of the module associated with the user resource.
     * @param name the name of the resource, not including any file extensions. Cannot contain the character '_'.
     * @param extension the file extension for the user resource.
     * @param locale the locale for which the resource is to be fetched.
     * @return an InputStream for the user resource, or null if the resource cannot be found.
     */
    public InputStream getUserResource(ModuleName moduleName, String name, String extension, Locale locale) {
        
        UserResourceFeatureName featureName = new UserResourceFeatureName(moduleName, name, extension);
        LocalizedResourceName resourceName = new LocalizedResourceName(featureName, locale);
        InputStream inputStream = userResourceStore.getInputStream(resourceName);

        return inputStream;
    }
}
