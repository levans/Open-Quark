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
 * StandardVault.java
 * Creation date: Jul 20, 2004.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import org.openquark.cal.metadata.MetadataNullaryStore;
import org.openquark.cal.metadata.MetadataStore;


/**
 * The default gem repository.
 * @author Edward Lam
 */
public class StandardVault extends StoreBasedVault {
    
    /** The vault provider for this class. */
    private static final StandardVaultProvider standardVaultProvider = new StandardVaultProvider();
    
    /** The singleton instance of the standard vault. */
    private static final StandardVault INSTANCE;
    static {
        CALSourceStore sourceStore = new CALSourceNullaryStore();
        MetadataStore metadataStore = new MetadataNullaryStore();
        GemDesignStore gemDesignStore = new GemDesignNullaryStore();
        WorkspaceDeclarationStore workspaceDeclarationStore = new WorkspaceDeclarationNullaryStore();
        CarStore carStore = new CarNullaryStore();
        UserResourceStore userResourceStore = new UserResourceNullaryStore();

        INSTANCE = new StandardVault(sourceStore, metadataStore, gemDesignStore, workspaceDeclarationStore, carStore, userResourceStore);
    }

    /**
     * The provider for StandardVaults.
     * @author Edward Lam
     */
    private static class StandardVaultProvider implements VaultProvider {
        /**
         * {@inheritDoc}
         */
        public String getVaultDescriptor() {
            return "StandardVault";
        }
        
        /**
         * {@inheritDoc}
         */
        public Vault getVault(String locationString, VaultAuthenticationManager authenticationManager) {
            return getInstance();
        }
    }
    
    /*
     * This class is a singleton - not instantiable by other classes.
     */
    private StandardVault(CALSourceStore sourceStore, MetadataStore metadataStore,
                          GemDesignStore gemDesignStore, WorkspaceDeclarationStore workspaceDeclarationStore,
                          CarStore carStore, UserResourceStore userResourceStore) {
        super(sourceStore, metadataStore, gemDesignStore, workspaceDeclarationStore, carStore, userResourceStore);
    }
    
    /**
     * @return the singleton instance of the standard vault.
     */
    public static StandardVault getInstance() {
        return INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    public VaultProvider getVaultProvider() {
        return getVaultClassProvider();
    }

    /**
     * Get the VaultProvider for this class.
     * @return the VaultProvider for this class.
     */
    public static VaultProvider getVaultClassProvider() {
        return standardVaultProvider;
    }

    /**
     * {@inheritDoc}
     * Returns null.
     */
    public String getLocationString() {
        return null;
    }
}
