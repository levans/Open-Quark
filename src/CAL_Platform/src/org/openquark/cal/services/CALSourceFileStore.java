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
 * CALSourceFileStore.java
 * Creation date: Jul 26, 2004.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.ModuleSourceDefinition;



/**
 * A source store using files and folders in the file system.
 * @author Edward Lam
 */
class CALSourceFileStore extends ResourceFileStore.Module implements CALSourceStore {

    /**
     * Constructor for a CAL source file store.
     * @param rootDirectory the directory under which the resource folder resides.
     */
    public CALSourceFileStore(File rootDirectory) {
        super(rootDirectory, ModuleSourceDefinition.RESOURCE_TYPE, CALSourcePathMapper.INSTANCE);
    }

    /**
     * {@inheritDoc}
     */
    public Set<ModuleName> getModuleNames() {
        return ModuleResourceStoreHelper.convertListOfResourceNamesToSetOfModuleNames(
            FileSystemResourceHelper.getResourceNamesInSubtreeRootedAtDirectory(getRootDirectory(), getFileExtension(), CALSourcePathMapper.INSTANCE.getBaseResourceFolder(), getPathMapper()));
    }
    
    /**
     * {@inheritDoc}
     */
    public List<ResourceName> getModuleResourceNameList(ModuleName moduleName) {
        ResourceName resourceName = new ResourceName(CALFeatureName.getModuleFeatureName(moduleName));
        if (hasFeature(resourceName)) {
            return Collections.singletonList(resourceName);
        }
        return Collections.emptyList();
    }
}
