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
 * StoredVaultElement.java
 * Creation date: Dec 9, 2004.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.io.InputStream;
import java.util.Iterator;

/**
 * A StoredVaultElement represents a persisted form for a workspace entities in a vault.
 * Workspace entities are constructed from these when imported from vaults.
 * @author Edward Lam
 */
public interface StoredVaultElement {

    /**
     * @return the name of the vault element.
     */
    public String getName();
    
    /**
     * @return The vault info for this vault element.
     */
    public VaultElementInfo getVaultInfo();

    /**
     * A StoredVaultElement.WorkspaceDeclaration represents a persisted form for a workspace declaration.
     * Workspace Declarations are constructed from these when imported from vaults.
     * @author Edward Lam
     */
    public interface WorkspaceDeclaration extends StoredVaultElement {
        
        /**
         * @return an input stream containing the actual workspace declaration.
         */
        public InputStream getInputStream();     // Should this have its own separate representation as a WorkspaceResource?
        
    }

    /**
     * A StoredVaultElement.Module represents a persisted base form for a module.
     * Modules in the workspace are constructed from these when imported from vaults.
     * @author Edward Lam
     */
    public interface Module extends StoredVaultElement {

        /**
         * Get revision info for resources in this StoredModule.
         * @return revision info for resources in this StoredModule.
         */
        public ResourceRevision.Info getResourceRevisionInfo();
        
        /**
         * @return An iterator over the resources in this StoredModule.
         */
        public Iterator<WorkspaceResource> getResourceIterator();
        
    }
    
    /**
     * A StoredVaultElement.Car represents a presisted form for a CAL Archive (Car).
     * Cars are constructed from these when imported from vaults.
     * 
     * @author Joseph Wong
     */
    public interface Car extends StoredVaultElement {
        
        /**
         * @return the CarVault that corresponds to this Car.
         */
        public CarVault getCarVault();
        
        /**
         * Retrieves an input stream for the specified Car workspace spec file.
         * @param specName the name of the spec file.
         * @param status the tracking status object.
         * @return the input stream for the spec file.
         */
        public InputStream getWorkspaceSpec(String specName, Status status);
    }
}
