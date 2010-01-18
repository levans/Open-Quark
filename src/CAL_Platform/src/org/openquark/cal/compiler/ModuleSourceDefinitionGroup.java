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
 * ModuleSourceDefinitionGroup.java
 * Creation date: Mar 7, 2003.
 * By: Edward Lam
 */
package org.openquark.cal.compiler;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * A ModuleSourceDefinitionGroup wraps a set of module source definitions.
 * @author Edward Lam
 */
public class ModuleSourceDefinitionGroup {

    /** The module sources provided by this provider.  */
    private final ModuleSourceDefinition[] moduleSources;
    
    /** (ModuleName->ModuleSourceDefinition) Map from module name to the ModuleSourceDefinition for the module with that name. */
    private final Map<ModuleName, ModuleSourceDefinition> nameToSourceMap = new HashMap<ModuleName, ModuleSourceDefinition>();
    
    /**
     * Constructor for a ModuleSourceDefinitionGroup.
     * @param sources the sources of which the group is comprised.
     */
    public ModuleSourceDefinitionGroup(ModuleSourceDefinition[] sources) {
        this.moduleSources = sources.clone();
        
        for (final ModuleSourceDefinition sourceDefinition : sources) {
            if (sourceDefinition == null) {
                CALCompiler.COMPILER_LOGGER.log(Level.WARNING, "Null module resource provided.");
                continue;
            }
            ModuleSourceDefinition previousDefinition = nameToSourceMap.put(sourceDefinition.getModuleName(), sourceDefinition);
            
            // Check that multiple module sources don't have the same module name.
            if (previousDefinition != null) {
                CALCompiler.COMPILER_LOGGER.log(Level.WARNING, "Multiple modules sources map to module " + sourceDefinition.getModuleName());
                
                // Replace with the definition which comes first.
                nameToSourceMap.put(sourceDefinition.getModuleName(), previousDefinition);
            }
        }
    }
    
    /**
     * Get the nth module source in this provider.
     * @param n
     * @return ModuleSourceDefinition
     */
    public ModuleSourceDefinition getModuleSource(int n) {
        return moduleSources[n];
    }
    
    /**
     * Get the number of modules provided by this provider.
     * @return int
     */
    public int getNModules() {
        return moduleSources.length;
    }
    
    /**
     * Get the modules provided by this provider.
     * @return the modules provided by this provider.
     */
    public ModuleSourceDefinition[] getModuleSources() {
        return moduleSources.clone();
    }
    
    /**
     * Get the source definition for a module by name
     * @param moduleName the name of the module for which to get a ModuleSourceDefinition.
     * @return the corresponding ModuleSourceDefinition, or null if the module is not known to this provider.
     */
    public ModuleSourceDefinition getModuleSource(ModuleName moduleName) {
        return nameToSourceMap.get(moduleName);
    }
}

