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
 * VaultWorkspaceDeclarationProvider.java
 * Creation date: Mar 7, 2005.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.io.InputStream;


/**
 * A Workspace declaration provider which chooses the source of its declaration based on provided system properties and defaults.
 *   The workspace declaration will be referenced using a given system property, if set.
 *   Otherwise, if set, the declaration will be referenced using the system property for the workspace spec.
 *   Otherwise the declaration will have used the default file name.
 * 
 * @author Edward Lam
 */
public class VaultWorkspaceDeclarationProvider implements WorkspaceDeclaration.StreamProvider {

    /** The vault in which the workspace declaration exists. */
    private final Vault vault;
    
    /** The name of the workspace declaration. */
    private final String workspaceDeclarationName;
    
    /** The revision number of the workspace declaration. */
    private final int revisionNumber;

    /**
     * Constructor for a VaultWorkspaceDeclarationProvider.
     * @param vault the vault in which the workspace declaration exists.
     * @param workspaceDeclarationName the name of the workspace declaration.
     * @param revisionNumber the revision number of the workspace declaration.
     */
    public VaultWorkspaceDeclarationProvider(Vault vault, String workspaceDeclarationName, int revisionNumber) {
        this.vault = vault;
        this.workspaceDeclarationName = workspaceDeclarationName;
        this.revisionNumber = revisionNumber;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getName() {
        return workspaceDeclarationName;
    }

    /**
     * {@inheritDoc}
     */
    public String getLocation() {
        StringBuilder sb = new StringBuilder("[");
        sb.append(vault.getVaultProvider().getVaultDescriptor() + ", ");
        sb.append(workspaceDeclarationName + ", ");
        sb.append(vault.getLocationString() + ", ");
        sb.append(revisionNumber);
        sb.append("]");
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    public InputStream getInputStream(VaultRegistry vaultRegistry, Status status) {
        
        StoredVaultElement.WorkspaceDeclaration workspaceDeclarationToSync =
                vault.getWorkspaceDeclaration(workspaceDeclarationName, revisionNumber, status);
        
        if (workspaceDeclarationToSync != null) {
            return workspaceDeclarationToSync.getInputStream();
        }
        
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getDebugInfo(VaultRegistry vaultRegistry) {
        return vault.getWorkspaceDeclarationDebugInfo(workspaceDeclarationName, revisionNumber);
    }
}