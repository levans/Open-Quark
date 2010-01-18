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
 * UserResourceJarStore.java
 * Creation date: Jun 2, 2006.
 * By: Joseph Wong
 */
package org.openquark.cal.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openquark.cal.compiler.ModuleName;


/**
 * Warning- this class should only be used by the CAL services implementation. It is not part of the
 * external API of the CAL platform.
 * <p>
 * A read-only user resource store based on compressed resources in a jar file.
 * A user resource is a resource whose exact format is determined by the user code,
 * and not managed by the platform.
 *
 * @author Joseph Wong
 */
class UserResourceJarStore extends ResourceJarStore.Module implements UserResourceStore {

    /**
     * Constructor for a UserResourceJarStore.
     * @param jarFileManager the manager for the jar file on which this store is based.
     */
    public UserResourceJarStore(JarFileManager jarFileManager) {
        super(jarFileManager, WorkspaceResource.USER_RESOURCE_TYPE, UserResourcePathMapper.INSTANCE);
    }

    /**
     * {@inheritDoc}
     */
    public Set<ModuleName> getModuleNames() {
        Set<ResourcePath.Folder> moduleResourcePaths =
            ModulePackager.getResourcePathsOfAllNonEmptySubfolders(getJarFileManager(), getPathMapper().getBaseResourceFolder());
        
        Set<ModuleName> moduleNames = new HashSet<ModuleName>();
        for (final ResourcePath.Folder moduleResourcePath : moduleResourcePaths) {
            moduleNames.add(((ResourcePathMapper.Module)getPathMapper()).getModuleNameFromResourcePath(moduleResourcePath));
        }
        
        return moduleNames;
    }
    
    /**
     * {@inheritDoc}
     */
    public List<ResourceName> getModuleResourceNameList(ModuleName moduleName) {
        return UserResourcePathStoreHelper.getModuleResourceNameList(this, moduleName);
    }
}
