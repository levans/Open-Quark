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
 * WorkspaceDeclarationJarStore.java
 * Creation date: Dec 10, 2004.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * A read-only workspace declaration store based on compressed resources in a jar file.
 * @author Edward Lam
 */
class WorkspaceDeclarationJarStore extends ResourceJarStore implements WorkspaceDeclarationStore {

    /**
     * Constructor for a WorkspaceDeclarationJarStore.
     * @param jarFileManager the manager for the jar file on which this store is based.
     */
    public WorkspaceDeclarationJarStore(JarFileManager jarFileManager) {
        super(jarFileManager, WorkspaceResource.WORKSPACE_DECLARATION_RESOURCE_TYPE, WorkspaceDeclarationPathMapper.INSTANCE);
    }

    /**
     * {@inheritDoc}
     */
    public Set<ResourceName> getWorkspaceNames() {
        return new HashSet<ResourceName>(ModulePackager.getFolderResourceNames(getJarFileManager(), getPathMapper().getBaseResourceFolder(),
                           getPathMapper()));
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<WorkspaceResource> getResourceIterator() {
        return WorkspaceDeclarationPathStoreHelper.getResourceIterator(this);
    }
}
