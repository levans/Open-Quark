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
 * UserResourceFeatureName.java
 * Creation date: Jun 2, 2006.
 * By: Joseph Wong
 */
package org.openquark.cal.services;

import org.openquark.cal.compiler.ModuleName;

/**
 * A UserResourceFeatureName instance encapsulates the name of a user resource.
 * A user resource is a resource whose exact format is determined by the user code,
 * and not managed by the platform.
 * <p>
 * In particular, a UserResourceFeatureName is composed of the following parts:
 * <ul>
 * <li> a module name
 * <li> a resource name
 * <li> a file extension... since the file format is determined by the user, the
 *      extension needs to be specified separately (because the locale information needs to go
 *      after the name and before the extension when the feature name is encoded as a file name).
 * </ul>
 *
 * @author Joseph Wong
 */
public final class UserResourceFeatureName extends FeatureName {

    /** The feature type for a user resource feature name. */
    public static final FeatureType TYPE = new FeatureType("UserResource");
    
    /** The name of the module associated with the user resource. */
    private final ModuleName moduleName;
    
    /**
     * The file extension for the user resource, <b>not</b> including the '.', e.g.
     * "jpg", "wav", "properties".
     */
    private final String extension;
    
    /**
     * Constructs a UserResourceFeatureName.
     * @param moduleName the name of the module associated with the user resource.
     * @param name the name of the resource, not including any file extensions. Cannot contain the character '_'.
     * @param extension the file extension for the user resource.
     */
    public UserResourceFeatureName(ModuleName moduleName, String name, String extension) {
        super(TYPE, name);
        if (moduleName == null || extension == null) {
            throw new NullPointerException();
        }
        if (name.indexOf(UserResourcePathMapper.USER_RESOURCE_LOCALE_PREFIX) != -1) {
            throw new IllegalArgumentException("The locale identifier prefix '" + UserResourcePathMapper.USER_RESOURCE_LOCALE_PREFIX + "' cannot appear in the name of a user resource.");
        }
        this.moduleName = moduleName;
        this.extension = extension;
    }
    
    /**
     * @return the name of the module associated with the user resource.
     */
    public ModuleName getModuleName() {
        return moduleName;
    }

    /**
     * @return the file extension for the user resource.
     */
    public String getExtension() {
        return extension;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (o.getClass().equals(getClass())) {
            UserResourceFeatureName other = (UserResourceFeatureName)o;
            return super.equals(o)
                && other.moduleName.equals(moduleName)
                && other.extension.equals(extension);
        }
        
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return (super.hashCode() * 17 + moduleName.hashCode()) * 17 + extension.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "UserResource: (module=" + moduleName + ",name=" + getName() + ",extension=" + extension + ")";
    }
}
