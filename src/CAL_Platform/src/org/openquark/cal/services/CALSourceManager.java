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
 * CALSourceManager.java
 * Creation date: Jul 26, 2004.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.io.InputStream;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.ModuleSourceDefinition;



/**
 * A manager to manage saving and loading CAL source using a source store.
 * @author Edward Lam
 */
public class CALSourceManager extends ModuleResourceManager {

    /** The store used by this manager to manage cal source. */
    private final CALSourceStore sourceStore;

    /**
     * @param sourceStore the store which contains the source managed by this manager.
     */
    public CALSourceManager(CALSourceStore sourceStore) {
        this.sourceStore = sourceStore;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResourceType() {
        return ModuleSourceDefinition.RESOURCE_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResourceStore getResourceStore() {
        return sourceStore;
    }
    
    /**
     * Loads the source for the module with the given name.
     * @param moduleName the name of the module to load
     * @param loadStatus a Status object for storing the status of the load operation
     */
    ModuleSourceDefinition getSource(ModuleName moduleName, Status loadStatus) {
        final CALFeatureName moduleFeatureName = getFeatureName(moduleName);
        final ResourceName moduleResourceName = new ResourceName(moduleFeatureName);
        
        ModuleSourceDefinition moduleSource = new ModuleSourceDefinition(moduleName) {
            @Override
            public InputStream getInputStream(Status status) {
                return sourceStore.getInputStream(moduleResourceName);
            }
            
            @Override
            public long getTimeStamp() {
                return sourceStore.getTimeStamp(moduleResourceName);
            }

            @Override
            public String getDebugInfo() {
                return sourceStore.getDebugInfo(moduleResourceName);
            }
        };

        return moduleSource;
    }

    /**
     * Save the source definition for a module to this manager.
     * @param moduleName the name of the module.
     * @param moduleDefinition the text definition of the module.
     * @param saveStatus the tracking status object.
     * @return whether the source was successfully saved.
     */
    public boolean saveSource(final ModuleName moduleName, final String moduleDefinition, Status saveStatus) {
        ModuleSourceDefinition sourceDefinition = new StringModuleSourceDefinition(moduleName, moduleDefinition);
        return importResource(sourceDefinition, saveStatus) != null;
    }
    
    /**
     * @param moduleName the name of a module.
     * @return whether the source definition for a given module is writeable.
     */
    public boolean isWriteable(ModuleName moduleName) {
        return sourceStore.isWriteable(new ResourceName(getFeatureName(moduleName)));
    }
    
    /**
     * @param moduleName the name of a module.
     * @return a CALFeatureName for the given module.
     */
    private CALFeatureName getFeatureName(ModuleName moduleName) {
        return CALFeatureName.getModuleFeatureName(moduleName);
    }
    
}
