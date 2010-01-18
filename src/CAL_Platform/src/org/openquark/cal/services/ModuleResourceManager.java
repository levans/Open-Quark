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
 * ModuleResourceManager.java
 * Creation date: Dec 10, 2004.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.util.Iterator;

import org.openquark.cal.compiler.ModuleName;



/**
 * A resource manager for module-related resources.
 * @author Edward Lam
 */
public abstract class ModuleResourceManager extends ResourceManager {
    /**
     * Get the resources associated with a given module.
     * @param moduleName the name of a module.
     * @return an iterator over the resources in the manager for the given module.
     */
    public Iterator<WorkspaceResource> getResourceIterator(ModuleName moduleName) {
        return ((ResourceStore.Module)getResourceStore()).getResourceIterator(moduleName);
    }
    
    /**
     * Remove resources for the given module from the manager.
     * @param moduleName the name of a module.
     * @param removeStatus the tracking status object.
     */
    public void removeModuleResources(ModuleName moduleName, Status removeStatus) {
        ((ResourceStore.Module)getResourceStore()).removeModuleResources(moduleName, removeStatus);
    }


}
