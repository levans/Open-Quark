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
 * Vault.java
 * Creation date: Jul 20, 2004.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import org.openquark.cal.compiler.ModuleName;



/**
 * The Vault is a storage context (a.k.a. repository) for Gems.
 * @author Edward Lam
 */
public interface Vault {

    // getRevisionHistory(String moduleName);
    // labelModules(String label, Module[] modules);
    // importModules(String label);
    // lock/unlock
    
    /**
     * A VaultProvider is used to retrieve vaults of a given type.
     * @author Edward Lam
     */
    public interface VaultProvider {
        /**
         * @return a String which describes the type of vault provided by this provider.
         * eg. "StandardVault" for the standard vault.
         */
        public String getVaultDescriptor();
        
        /**
         * Get a vault of this type.
         * 
         * @param locationString a string which describes the vault location/instance desired.
         *   This can be null in the case of singleton vaults.
         * @param authenticationManager the authentication manager.
         *   This can be null if obtaining the vault does not require authentication.
         * @return a vault of the type as provided by this provider, and as specified by the location string.
         *   Null if a vault with these characteristics could not be constructed.
         */
        public Vault getVault(String locationString, VaultAuthenticationManager authenticationManager);
    }

    /**
     * Get the VaultProvider for this vault.
     * @return the VaultProvider for this vault.
     */
    public VaultProvider getVaultProvider();
    
    /**
     * Get the location string for this vault.
     *   The format of this string is dependent on the vault type.
     * 
     * @return a String specifying this vault.
     *   This string can be used by this vault's VaultProvider to indicate a specific instance of a vault.
     *   This can be null in the case of singleton vaults (eg. the StandardVault).
     */
    public String getLocationString();
    
    ////
    /// Modules
    //
    
    /**
     * Store a given module in the vault.
     * @param moduleName the name of the module to store.
     * @param workspace the workspace containing the module.
     * @param putStatus the tracking status object.
     * @return the revision number of the stored module, or 0 if the module could not be stored.
     */
    public int putStoredModule(ModuleName moduleName, CALWorkspace workspace, Status putStatus);
    
    /**
     * Import the resources for a module from the vault.
     * @param moduleName the name of the module to import.
     * @param revisionNumber the revision number of the module to import, or -1 to import the latest revision.
     * @param status the tracking status object.
     * @return the corresponding stored module resources from the vault, or null if none.
     */
    public StoredVaultElement.Module getStoredModule(ModuleName moduleName, int revisionNumber, Status status);

    /**
     * Get the modules available in this vault.
     * @param status the tracking status object.
     * @return the names of the available modules.
     */
    public ModuleName[] getAvailableModules(Status status);
    
    /**
     * @param moduleName the name of a module.
     * @return the RevisionHistory for that module, or null if that module does not exist in this vault.
     */
    public RevisionHistory getModuleRevisionHistory(ModuleName moduleName);
    
    ////
    /// Workspace declarations
    //
    
    /**
     * Store a given workspace declaration in the vault.
     * @param workspaceDeclarationName the name of the workspace declaration to store.
     * @param workspaceDeclaration the workspace declaration.
     * @param putStatus the tracking status object.
     * @return the revision number of the stored workspace declaration.
     */
    public int putWorkspaceDeclaration(String workspaceDeclarationName, WorkspaceDeclaration workspaceDeclaration, Status putStatus);
    
    /**
     * Import the workspace declaration from the vault.
     * @param workspaceDeclarationName the name of the workspace declaration.
     * @param revisionNumber the revision number of the workspace declaration to import, or -1 to import the latest revision.
     * @param status the tracking status object.
     * @return the corresponding stored workspace declaration, or null if none.
     */
    public StoredVaultElement.WorkspaceDeclaration getWorkspaceDeclaration(String workspaceDeclarationName, int revisionNumber, Status status);
    
    /**
     * Get the workspace declarations available in the vault.
     * @param status the tracking status object.
     * @return the names of the available workspace declarations.
     */
    public String[] getAvailableWorkspaceDeclarations(Status status);
    
    /**
     * @param workspaceDeclarationName the name of a workspace declaration.
     * @return the RevisionHistory for that workspace declaration, or null if that workspace declaration does not exist in this vault.
     */
    public RevisionHistory getWorkspaceDeclarationRevisionHistory(String workspaceDeclarationName);
    
    /**
     * @param workspaceDeclarationName the name of the workspace declaration.
     * @param revisionNumber the revision number of the workspace declaration to import, or -1 to import the latest revision.
     * @return debugging information about the workspace declaration, e.g. the actual location of the resource. Can be null if the resource does not exist.
     */
    public String getWorkspaceDeclarationDebugInfo(String workspaceDeclarationName, int revisionNumber);
    
    ////
    /// Cars
    //
    
    /**
     * Store a given Car in the vault.
     * @param car the Car to be stored.
     * @param putStatus the tracking status object.
     * @return the revision number of the stored Car.
     */
    public int putCar(Car car, Status putStatus);
    
    /**
     * Import a Car from the vault.
     * @param carName the name of the Car to import.
     * @param revisionNumber the revision number of the Car to import, or -1 to import the latest revision.
     * @param status the tracking status object.
     * @return the corresponding stored Car, or null if none.
     */
    public StoredVaultElement.Car getCar(String carName, int revisionNumber, Status status);
    
    /**
     * Get the Cars available in the vault.
     * @param status the tracking status object.
     * @return the names of the available Cars.
     */
    public String[] getAvailableCars(Status status);
    
    /**
     * @param carName the name of a Car.
     * @return the RevisionHistory for that Car, or null if that Car does not exist in this vault.
     */
    public RevisionHistory getCarRevisionHistory(String carName);
    
    /**
     * Retrieves a Car as a workspace resource.
     * @param carName the name of the Car to retrieve.
     * @param revisionNumber the revision number of the Car to retrieve, or -1 to retrieve the latest revision.
     * @param status the tracking statuc object.
     * @return the Car as a workspace resource, or null if none.
     */
    public Car getCarAsResource(String carName, int revisionNumber, Status status);
}
