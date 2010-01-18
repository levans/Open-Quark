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
 * ExcludeTestModulesFilter.java
 * Creation date: Oct 14, 2005.
 * By: Joseph Wong
 */
package org.openquark.cal.filter;

import java.io.File;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.services.CALFeatureName;
import org.openquark.cal.services.CALWorkspace;
import org.openquark.cal.services.NullaryEnvironment;
import org.openquark.cal.services.ResourceName;
import org.openquark.cal.services.ResourcePath;
import org.openquark.cal.services.ResourcePathStore;
import org.openquark.cal.services.ResourceStore;


/**
 * (HACK)
 * A module filter specifying that modules that are "test modules" are to be excluded.
 * A testing module is a module with an ancestor directory named 'test'.
 *
 * @author James Wright
 * @author Joseph Wong
 */
public final class ExcludeTestModulesFilter implements ModuleFilter {
    
    /** The CAL workspace associated with the client of this filter. */
    private final CALWorkspace workspace;

    /**
     * Constructs an instance of this filter.
     * @param workspace the associated CAL workspace.
     */
    public ExcludeTestModulesFilter(final CALWorkspace workspace) {
        if (workspace == null) {
            throw new NullPointerException();
        }
        
        this.workspace = workspace;
    }

    /**
     * {@inheritDoc}
     */
    public boolean acceptModule(final ModuleName moduleName) {
        return !isTestingModule(moduleName);
    }

    /**
     * Check the specified module to see whether it is a testing module.
     * A testing module is a module with an ancestor directory named 'test'.
     * @param moduleName the name of the module.
     * @return true if the specified module is a testing module; false otherwise.
     */
    private boolean isTestingModule(final ModuleName moduleName) {
        final ResourceStore sourceStore = workspace.getSourceManager(moduleName).getResourceStore();
        final CALFeatureName moduleFeatureName = CALFeatureName.getModuleFeatureName(moduleName);
        final ResourceName resourceName = new ResourceName(moduleFeatureName);
        
        // If the source store doesn't know about this module, then it is not a testing module
        // (because it wasn't loaded from disk)
        if (!sourceStore.hasFeature(resourceName)) {
            return false;
        }
        
        if (sourceStore instanceof ResourcePathStore) {
            final ResourcePathStore sourcePathStore = (ResourcePathStore)sourceStore;
            final ResourcePath.FilePath resourcePath = sourcePathStore.getResourcePath(resourceName);
            
            // Check for ancestors named test.
            // Note: this assumes that the file exists with respect to the nullary environment.
            
            File currentFile = NullaryEnvironment.getNullaryEnvironment().getFile(resourcePath, false);
            for ( ; currentFile != null; currentFile = currentFile.getParentFile()) {
                
                if (currentFile.getName().equals("test")) {
                    return true;
                }
            }
        }

        return false;
    }
}
