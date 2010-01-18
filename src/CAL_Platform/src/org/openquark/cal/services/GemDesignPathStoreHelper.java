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
 * GemDesignPathStoreHelper.java
 * Creation date: Nov 23, 2004.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.util.ArrayList;
import java.util.List;

import org.openquark.cal.compiler.ModuleName;



/**
 * This is a helper class which contains methods to determine which features are present in a gem design path store.
 * @author Edward Lam
 */
class GemDesignPathStoreHelper {
    
    /**
     * Constructor for a GemDesignPathStoreHelper.
     */
    private GemDesignPathStoreHelper() {
    }
    
    /**
     * Get the names of resources associated with the module.
     * @param moduleName the name of a module.
     * @return the names of features associated with the module.
     */
    public static List<ResourceName> getModuleResourceNameList(AbstractResourcePathStore pathStore, ModuleName moduleName) {
        
        List<ResourceName> moduleFeatureNameList = new ArrayList<ResourceName>();
        
        // Get the design folder for the module, and analyze the names of the files in that folder..
        ResourcePath.Folder designModuleFolder =
                (ResourcePath.Folder)((ResourcePathMapper.Module)pathStore.getPathMapper()).getModuleResourcePath(moduleName);
        
        for (final ResourceName resourceName : pathStore.getFolderResourceNames(designModuleFolder)) {
            if (resourceName != null) {
                moduleFeatureNameList.add(resourceName);
            }
        }

        return moduleFeatureNameList;
    }
}
