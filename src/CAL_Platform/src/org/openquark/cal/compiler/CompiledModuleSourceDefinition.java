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
 * ModuleCompiledSourceDefinition.java
 * Creation date: Aug 31, 2005
 * By: Raymond Cypher
 */
package org.openquark.cal.compiler;

import org.openquark.cal.services.CALFeatureName;
import org.openquark.cal.services.ResourceIdentifier;
import org.openquark.cal.services.WorkspaceResource;


/**
 * A ModuleSourceDefinition is a module resource which wraps a previously compiled module definition and its name.
 * 
 * @author rcypher
 */
public abstract class CompiledModuleSourceDefinition extends WorkspaceResource {

    public static final CompiledModuleSourceDefinition[] EMPTY_ARRAY = new CompiledModuleSourceDefinition[0];
    public static final String RESOURCE_TYPE = "CompiledDefinition";

    /** The name of the module. */
    private final ModuleName moduleName;
    
    /**
     * Constructor for a ModuleSourceDefinition.
     * @param moduleName the name of the module.
     */
    public CompiledModuleSourceDefinition(ModuleName moduleName) {
        super(ResourceIdentifier.make(RESOURCE_TYPE, CALFeatureName.getModuleFeatureName(moduleName)));
        if (moduleName == null) {
            throw new NullPointerException("Module name must not be null.");
        }

        this.moduleName = moduleName;
    }

    /**
     * Get the name of the module
     * @return the name of the module, or null if unknown by the source
     */
    public final ModuleName getModuleName() {
        return moduleName;
    }
}
