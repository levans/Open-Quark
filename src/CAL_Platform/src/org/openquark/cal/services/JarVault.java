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
 * JarVault.java
 * Creation date: Nov 8, 2004.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.io.IOException;
import java.util.jar.JarFile;

import org.openquark.cal.metadata.MetadataJarStore;



/**
 * A JarVault is a read-only vault backing a jar containing the resources for a single module.
 * The location string is the name of the jar file to open for reading.
 * @author Edward Lam
 */
public class JarVault extends StoreBasedVault {

    /** The vault provider for this class. */
    private static final VaultProvider vaultProvider = new VaultProvider() {

        public String getVaultDescriptor() {
            return "JarVault";
        }

        public Vault getVault(String locationString, VaultAuthenticationManager authenticationManager) {
            if (locationString == null) {
                return null;
            }
            
            try {
                JarFile jarFile = new JarFile(locationString);
                return new JarVault(jarFile);
            } catch (IOException e) {
            }
            
            return null;
        }
    };

    /** The manager for the jar file backing this vault. */
    private final JarFileManager jarFileManager;
    
    /**
     * Constructor for a JarVault.
     * @param jarFile
     */
    public JarVault(JarFile jarFile) {
        this(new JarFileManager(jarFile));
    }
    
    /**
     * Constructs a JarVault with the given jar file manager.
     * @param jarFileManager the jar file manager that encapsulates the underlying jar file.
     */
    public JarVault(JarFileManager jarFileManager) {
        super(new CALSourceJarStore(jarFileManager), new MetadataJarStore(jarFileManager),
            new GemDesignJarStore(jarFileManager), new WorkspaceDeclarationJarStore(jarFileManager),
            new EmptyResourceStore.Car(WorkspaceResource.CAR_RESOURCE_TYPE), // Cars are not supported in a JarVault
            new UserResourceJarStore(jarFileManager));
        this.jarFileManager = jarFileManager;
    }
    
    /**
     * Get the VaultProvider for this class.
     * @return the VaultProvider for this class.
     */
    public static VaultProvider getVaultClassProvider() {
        return vaultProvider;
    }

    /**
     * {@inheritDoc}
     */
    public VaultProvider getVaultProvider() {
        return vaultProvider;
    }

    /**
     * {@inheritDoc}
     */
    public String getLocationString() {
        return jarFileManager.getJarFile().getName();
    }
    
}
