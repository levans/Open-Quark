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
 * NonExistantVault.java
 * Created: Apr 27, 2005
 * By: Peter Cardwell
 */

package org.openquark.cal.services;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.services.StoredVaultElement.Module;


/**
 * A placeholder vault to be used with entities that do not have a real vault associated with them.
 * @author Peter Cardwell
 */
class NonExistentVault implements Vault {
    
    /** The vault provider for this class. */
    private static final VaultProvider vaultProvider = new VaultProvider() {
        
        /**
         * {@inheritDoc}
         */
        public String getVaultDescriptor() {
            return "NonExistent";
        }

        /**
         * {@inheritDoc}
         */
        public Vault getVault(String locationString, VaultAuthenticationManager authenticationManager) {
            return new NonExistentVault();
        }
    };
    
    /** The revision history for non-existent resources. */
    static final class NonExistentRevisionHistory implements RevisionHistory {
        
        public int getRootRevision() {
            return -1;
        }

        public int getLatestRevision() {
            return -1;
        }

        public int[] getAvailableRevisions() {
            return new int[] {};
        }
    }
    
    private static NonExistentVault instance = new NonExistentVault();
    public static NonExistentVault getInstance() { return instance; }
        
    private NonExistentVault() {}

    /*
     * @see org.openquark.cal.services.Vault#getVaultProvider()
     */
    public VaultProvider getVaultProvider() {
       
        return null;
    }

    /*
     * @see org.openquark.cal.services.Vault#getLocationString()
     */
    public String getLocationString() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public int putStoredModule(ModuleName moduleName, CALWorkspace workspace,
            Status putStatus) {
        putStatus.add(new Status(Status.Severity.ERROR, "Can't put stored modules into the non-existent vault."));
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public Module getStoredModule(ModuleName moduleName, int revisionNumber,
            Status status) {
        status.add(new Status(Status.Severity.WARNING, "Can't get stored modules from the non-existent vault."));
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public ModuleName[] getAvailableModules(Status status) {
        return new ModuleName[0];
    }

    /**
     * {@inheritDoc}
     */
    public RevisionHistory getModuleRevisionHistory(ModuleName moduleName) {
        return new NonExistentRevisionHistory();
    }

    /*
     * @see org.openquark.cal.services.Vault#putWorkspaceDeclaration(java.lang.String, org.openquark.cal.services.WorkspaceDeclaration, org.openquark.cal.services.Status)
     */
    public int putWorkspaceDeclaration(String workspaceDeclarationName,
            WorkspaceDeclaration workspaceDeclaration, Status putStatus) {
        putStatus.add(new Status(Status.Severity.ERROR, "Can't put workspace declaration into the non-existent vault."));
        return -1;
    }

    /*
     * @see org.openquark.cal.services.Vault#getWorkspaceDeclaration(java.lang.String, int, org.openquark.cal.services.Status)
     */
    public org.openquark.cal.services.StoredVaultElement.WorkspaceDeclaration getWorkspaceDeclaration(
            String workspaceDeclarationName, int revisionNumber, Status status) {
        return null;
    }

    /*
     * @see org.openquark.cal.services.Vault#getAvailableWorkspaceDeclarations(org.openquark.cal.services.Status)
     */
    public String[] getAvailableWorkspaceDeclarations(Status status) {
        return new String[0];
    }

    /*
     * @see org.openquark.cal.services.Vault#getWorkspaceDeclarationRevisionHistory(java.lang.String)
     */
    public RevisionHistory getWorkspaceDeclarationRevisionHistory(
            String workspaceDeclarationName) {
        return new NonExistentRevisionHistory();
    }

    /**
     * {@inheritDoc}
     */
    public String getWorkspaceDeclarationDebugInfo(String workspaceDeclarationName, int revisionNumber) {
        return "from NonExistentVault";
    }

    /**
     * {@inheritDoc}
     */
    public int putCar(Car car, Status putStatus) {
        putStatus.add(new Status(Status.Severity.ERROR, "Can't put Cars into the non-existent vault."));
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public StoredVaultElement.Car getCar(String carName, int revisionNumber, Status status) {
        status.add(new Status(Status.Severity.WARNING, "Can't get Cars from the non-existent vault."));
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String[] getAvailableCars(Status status) {
        return new String[0];
    }

    /**
     * {@inheritDoc}
     */
    public RevisionHistory getCarRevisionHistory(String carName) {
        return new NonExistentRevisionHistory();
    }
    
    /**
     * {@inheritDoc}
     */
    public Car getCarAsResource(String carName, int revisionNumber, Status status) {
        status.add(new Status(Status.Severity.WARNING, "Can't get Cars from the non-existent vault."));
        return null;
    }
    
    /**
     * Get the VaultProvider for this class.
     * @return the VaultProvider for this class.
     */
    public static VaultProvider getVaultClassProvider() {
        return vaultProvider;
    }
}
