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
 * VaultRegistry.java
 * Creation date: Jul 29, 2004.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.util.HashSet;
import java.util.Set;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.services.Vault.VaultProvider;



/**
 * A VaultRegistry maintains a registry of known VaultProviders, thus providing a way to instantiate a vault
 *   given its descriptor and location string.
 * @author Edward Lam
 */
public class VaultRegistry {

    /** The VaultProviders known to this registry. */
    private final Set<VaultProvider> vaultProviders = new HashSet<VaultProvider>();
    
    /** The vault authentication manager associated with this registry. */
    private final VaultAuthenticationManager vaultAuthenticationManager = new VaultAuthenticationManager();
    
    /**
     * Constructor for a VaultRegistry.
     */
    public VaultRegistry() {
        // Register some standard vault providers.
        registerVaultProvider(StandardVault.getVaultClassProvider());
        registerVaultProvider(SimpleCALFileVault.getVaultClassProvider());
        registerVaultProvider(JarVault.getVaultClassProvider());
        registerVaultProvider(NonExistentVault.getVaultClassProvider());
        registerVaultProvider(SimpleCarFileVault.getVaultClassProvider());
    }
    
    /**
     * Register a Vault Provider with this registry.
     * @param newProvider the provider to register.
     */
    public void registerVaultProvider(VaultProvider newProvider) {
        vaultProviders.add(newProvider);
    }
    
    /**
     * Register a vault authenticator to use to authenticate access to vaults using this registry.
     * @param vaultAuthenticator the authenticator to register.
     */
    public void registerVaultAuthenticator(VaultAuthenticator vaultAuthenticator) {
        vaultAuthenticationManager.registerAuthenticator(vaultAuthenticator);
    }
    
    /**
     * Get a Vault by descriptor and location.
     * @param vaultDescriptor a descriptor string which indicates the type of vault.
     * @param locationString a provider-specific location.
     * @return the corresponding vault, or null if the corresponding vault could not be found.
     */
    public Vault getVault(String vaultDescriptor, String locationString) {
        // Iterate through the vault providers, checking for the one with the given descriptor.
        for (final VaultProvider vaultProvider : vaultProviders) {
            if (vaultProvider.getVaultDescriptor().equals(vaultDescriptor)) {
                // Return the vault provided by the provider.
                return vaultProvider.getVault(locationString, vaultAuthenticationManager);
            }
        }
        
        // Couldn't find a vault with the given descriptor.
        return null;
    }
    
    /**
     * Get a stored module given a vault descriptor and the arguments to the provider it describes.
     * @param vaultModuleInfo the vault info for the stored module element.
     * @param status the tracking status object.
     * @return the corresponding stored module, or null if a corresponding stored module could not be constructed.
     */
    public StoredVaultElement.Module getStoredModule(VaultElementInfo vaultModuleInfo, Status status) {
        
        Vault vault = getVault(vaultModuleInfo.getVaultDescriptor(), vaultModuleInfo.getLocationString(), status);
        if (vault == null) {
            return null;
        }
        
        String elementName = vaultModuleInfo.getElementName();
        int revisionNum = vaultModuleInfo.getRevision();
        
        final ModuleName moduleName = ModuleName.maybeMake(elementName);
        if (moduleName == null) {
            status.add(new Status(Status.Severity.ERROR, "The name " + elementName + " is not a valid module name", null));
            return null;
        }
        
        StoredVaultElement.Module storedElement = vault.getStoredModule(moduleName, revisionNum, status);
        
        if (storedElement == null) {
            logProblemRetrievingStoredElement("module", elementName, revisionNum, status);
        }
        return storedElement;
    }
    
    /**
     * Get a stored workspace declaration given a vault descriptor and the arguments to the provider it describes.
     * @param vaultDeclarationInfo the vault info for the stored workspace declaration element.
     * @param status the tracking status object.
     * @return the corresponding stored workspace declaration, or null if a corresponding stored workspace declaration could not be constructed.
     */
    public StoredVaultElement.WorkspaceDeclaration getStoredWorkspaceDeclaration(VaultElementInfo vaultDeclarationInfo, Status status) {
        
        Vault vault = getVault(vaultDeclarationInfo.getVaultDescriptor(), vaultDeclarationInfo.getLocationString(), status);
        if (vault == null) {
            return null;
        }
        
        String elementName = vaultDeclarationInfo.getElementName();
        int revisionNum = vaultDeclarationInfo.getRevision();
        
        StoredVaultElement.WorkspaceDeclaration storedElement = vault.getWorkspaceDeclaration(elementName, revisionNum, status);
        
        if (storedElement == null) {
            logProblemRetrievingStoredElement("workspace declaration", elementName, revisionNum, status);
        }
        return storedElement;
    }
    
    /**
     * Get a stored Car given a vault descriptor and the arguments to the provider it describes.
     * @param vaultCarInfo the vault info for the stored Car element.
     * @param status the tracking status object.
     * @return the corresponding stored Car, or null if a corresponding stored Car could not be constructed.
     */
    public StoredVaultElement.Car getStoredCar(VaultElementInfo vaultCarInfo, Status status) {
        
        Vault vault = getVault(vaultCarInfo.getVaultDescriptor(), vaultCarInfo.getLocationString(), status);
        if (vault == null) {
            return null;
        }
        
        String elementName = vaultCarInfo.getElementName();
        int revisionNum = vaultCarInfo.getRevision();
        
        StoredVaultElement.Car storedElement = vault.getCar(elementName, revisionNum, status);
        
        if (storedElement == null) {
            logProblemRetrievingStoredElement("Car", elementName, revisionNum, status);
        }
        return storedElement;
    }
    
    /**
     * Get a Vault by descriptor and location, and the vault cannot be fetched, log the problem to the given status object.
     * @param vaultDescriptor a descriptor string which indicates the type of vault.
     * @param locationString a provider-specific location.
     * @param status the tracking static object.
     * @return the corresponding vault, or null if the corresponding vault could not be found.
     */
    private Vault getVault(String vaultDescriptor, String locationString, Status status) {
        
        Vault vault = getVault(vaultDescriptor, locationString);
        if (vault == null) {
            String errorString = "VaultRegistry: could not instantiate a vault with descriptor " +
                                 vaultDescriptor + " and location " + locationString;
            status.add(new Status(Status.Severity.ERROR, errorString));
        }
        
        return vault;
    }
    
    /**
     * Logs a problem with retrieving a stored element to the given status object.
     * @param elementType the type of the element.
     * @param elementName the name of the element.
     * @param revisionNum the revision number of the element.
     * @param status the tracking status object, to which the problem is to be logged.
     */
    private void logProblemRetrievingStoredElement(String elementType, String elementName, int revisionNum, Status status) {
        String errorString = "VaultRegistry: Could not retrieve " + elementType + " \"" + elementName + "\", revision " + revisionNum + " from the given vault.";
        status.add(new Status(Status.Severity.ERROR, errorString));
    }
}
