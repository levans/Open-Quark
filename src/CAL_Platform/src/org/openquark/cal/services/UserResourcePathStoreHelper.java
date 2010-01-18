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
 * UserResourcePathStoreHelper.java
 * Creation date: Jun 2, 2006.
 * By: Joseph Wong
 */
package org.openquark.cal.services;

import java.util.ArrayList;
import java.util.List;

import org.openquark.cal.compiler.ModuleName;


/**
 * This is a helper class which contains methods to determine which features are
 * present in a user resource path store.
 *
 * @author Joseph Wong
 */
class UserResourcePathStoreHelper {

    /**
     * Private constructor.
     */
    private UserResourcePathStoreHelper() {
    }

    /**
     * Get the names of resources associated with the module.
     * @param pathStore the path store.
     * @param moduleName the name of a module.
     * @return the names of resources associated with the module.
     */
    public static List<ResourceName> getModuleResourceNameList(AbstractResourcePathStore pathStore, ModuleName moduleName) {
        
        List<ResourceName> result = new ArrayList<ResourceName>();
        
        ResourcePath.Folder moduleFolder =
            (ResourcePath.Folder)((ResourcePathMapper.Module)pathStore.getPathMapper()).getModuleResourcePath(moduleName);

        for (final ResourceName resourceName : pathStore.getFolderResourceNames(moduleFolder)) {
            if (resourceName != null) {
                result.add(resourceName);
            }
        }
        
        return result;
    }
}
