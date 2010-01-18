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
 * VaultStatus.java
 * Creation date: Dec 9, 2004.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.openquark.cal.compiler.ModuleName;


/**
 * An object to encapsulate the current vault-related status of modules in the workspace.
 * In the future, this should be obtained on a per-resource basis.
 * @author Edward Lam
 */
public class VaultStatus {

    // Possible states:
    // Modules: inaccessible vault info, workspace only, out of date.
    // Resources: modified, out of date, workspace only, vault only.

    /** map from module name to vault info.*/
    private final Map<ModuleName, VaultElementInfo> inaccessibleVaultInfoMap = new HashMap<ModuleName, VaultElementInfo>();
    
    private final Set<ModuleName> inaccessibleModuleSet = new HashSet<ModuleName>();

    private final Set<ModuleName> workspaceOnlyModuleSet = new HashSet<ModuleName>();
    private final Set<ModuleName> outOfDateModuleSet = new HashSet<ModuleName>();

    private final Set<ResourceIdentifier> modifiedResourceSet = new HashSet<ResourceIdentifier>();
    private final Set<ResourceIdentifier> outOfDateResourceSet = new HashSet<ResourceIdentifier>();
    
    private final Set<ResourceIdentifier> workspaceOnlyResourceNameSet = new HashSet<ResourceIdentifier>();
    private final Set<ResourceIdentifier> vaultOnlyResourceNameSet = new HashSet<ResourceIdentifier>();
    
    private final Set<ResourceIdentifier> unmanagedResourceSet = new HashSet<ResourceIdentifier>();
    
    /**
     * Constructor for a VaultStatus.
     */
    VaultStatus() {
    }
    
    /**
     * Dump a set into the status buffer.
     * @param targetBuilder the string builder to which to dump the status.
     * @param setToDump the set whose elements' toString() forms will be appended into the status buffer.
     */
    private static void dumpSet(StringBuilder targetBuilder, Set<?> setToDump) {
        if (!setToDump.isEmpty()) {
            // Convert to an array and sort.
            List<Object> listToDump = new ArrayList<Object>(setToDump);
            String[] stringsToDump = new String[listToDump.size()];
            
            int index = 0;
            for (final Object object : setToDump) {
                stringsToDump[index] = object.toString();
                index++;
            }
            Arrays.sort(stringsToDump);
            
            for (final String element : stringsToDump) {
                targetBuilder.append("  " + element + "\n");
            }

        } else {
            targetBuilder.append("  (none)\n");
        }
    }
    
    /**
     * @return the stringified form of the status.
     */
    public String getStatusString() {
        StringBuilder statusString = new StringBuilder();

        if (!inaccessibleVaultInfoMap.isEmpty()) {
            statusString.append("Modules whose vaults are not accessible:\n");
            
            Set<ModuleName> modulesWithInaccessibleVaultSortedByName = new TreeSet<ModuleName>(inaccessibleVaultInfoMap.keySet());
            
            for (final ModuleName moduleName : modulesWithInaccessibleVaultSortedByName) {
                VaultElementInfo vaultInfo = inaccessibleVaultInfoMap.get(moduleName);

                statusString.append(moduleName);
                statusString.append("  Descriptor: " + vaultInfo.getVaultDescriptor());
                statusString.append("  Location:   " + vaultInfo.getLocationString() + "\n");
            }
        }
        
        if (!inaccessibleModuleSet.isEmpty()) {
            statusString.append("Modules which are not accessible:\n");
            dumpSet(statusString, inaccessibleModuleSet);
        }

        statusString.append("Modules which exist in the workspace but not in the vault:\n");
        dumpSet(statusString, workspaceOnlyModuleSet);

        statusString.append("Modules which are out of date:\n");
        dumpSet(statusString, outOfDateModuleSet);
        
        statusString.append("Resources which are out of date:\n");
        dumpSet(statusString, outOfDateResourceSet);
        
        statusString.append("Resources which are modified:\n");
        dumpSet(statusString, modifiedResourceSet);

        statusString.append("Resources which exist in the workspace but not in the vault:\n");
        dumpSet(statusString, workspaceOnlyResourceNameSet);
        
        statusString.append("Resources which exist in the vault but not in the workspace:\n");
        dumpSet(statusString, vaultOnlyResourceNameSet);
        
        return statusString.toString();
    }

    /**
     * Add a vault info object as not corresponding to an accessible location.
     * @param moduleName the name of the module whose vault is inaccessible.
     * @param vaultInfo the module's vault info.
     */
    void addInaccessibleVaultInfo(ModuleName moduleName, VaultElementInfo vaultInfo) {
        inaccessibleVaultInfoMap.put(moduleName, vaultInfo);
    }
    /**
     * @return Map from module name to vault info for vaults which are inaccessible.
     */
    public Map<ModuleName, VaultElementInfo> getInaccessibleVaultInfoMap() {
        return new HashMap<ModuleName, VaultElementInfo>(inaccessibleVaultInfoMap);
    }
    
    
    /**
     * Set the vault status of a module as being inaccessible.
     * @param moduleName the name of the module.
     */
    void addInaccessibleModule(ModuleName moduleName) {
        inaccessibleModuleSet.add(moduleName);
    }
    
    /**
     * @return the names of modules which are no longer accessible.
     */
    public Set<ModuleName> getInaccessibleModuleSet() {
        return new HashSet<ModuleName>(inaccessibleModuleSet);
    }
    
    /**
     * @param moduleName the name of a module.
     * @return whether the module is inaccessible, according to the info known to this status object.
     */
    public boolean isModuleInaccessible(ModuleName moduleName) {
        return inaccessibleModuleSet.contains(moduleName);
    }
    
    
    /**
     * Set the vault status of module as existing in the workspace but not in any vault.
     * @param moduleName the name of the module.
     */
    void addWorkspaceOnlyModule(ModuleName moduleName) {
        workspaceOnlyModuleSet.add(moduleName);
    }
    /**
     * @return the names of modules which are not associated with a vault.
     */
    public Set<ModuleName> getWorkspaceOnlyModuleNameSet() {
        return new HashSet<ModuleName>(workspaceOnlyModuleSet);
    }

    /**
     * @param moduleName the name of a module.
     * @return whether the module is not associated with a vault, according to the info known to this status object.
     */
    public boolean isModuleWorkspaceOnly(ModuleName moduleName) {
        return workspaceOnlyModuleSet.contains(moduleName);
    }
    
    
    /**
     * Set the vault status of a module as out of date.
     * @param moduleName the name of the module which is out of date.
     */
    void addOutOfDateModule(ModuleName moduleName) {
        outOfDateModuleSet.add(moduleName);
    }
    /**
     * @return the names of out of date modules.
     */
    public Set<ModuleName> getOutOfDateModuleSet() {
        return new HashSet<ModuleName>(outOfDateModuleSet);
    }
    
    /**
     * @param moduleName the name of a module.
     * @return whether the module is out of date, according to the info known to this status object.
     */
    public boolean isModuleOutOfDate(ModuleName moduleName) {
        return outOfDateModuleSet.contains(moduleName);
    }
    
    
    /**
     * @param moduleName the name of a module.
     * @return whether the module is inaccessible, according to the info known to this status object.
     */
    public boolean isModuleModified(ModuleName moduleName) {
        return hasModuleIdentifier(moduleName, modifiedResourceSet) ||
               hasModuleIdentifier(moduleName, workspaceOnlyResourceNameSet);
    }
    
    /**
     * A helper method to find whether a set of identifiers contains a feature belonging to a given module.
     * @param moduleName the name of the module for which to search.
     * @param identifierSet the identifiers to search.
     * @return whether the given set of identifiers contains a feature belonging to the given module.
     */
    private boolean hasModuleIdentifier(ModuleName moduleName, Set<ResourceIdentifier> identifierSet) {
        
        // iterate through the identifier set, seeing if any of the identifiers come from the given module.
        for (final ResourceIdentifier resourceIdentifier : identifierSet) {
            FeatureName resourceName = resourceIdentifier.getFeatureName();
            
            if (resourceName instanceof CALFeatureName) {
                CALFeatureName calResourceName = (CALFeatureName)resourceName;
                
                if (calResourceName.hasModuleName() && calResourceName.toModuleName().equals(moduleName)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    

    /**
     * Set the vault status of a resource as modified.
     * @param resourceIdentifier an identifier for the resource which is modified.
     */
    void addModifiedResource(ResourceIdentifier resourceIdentifier) {
        modifiedResourceSet.add(resourceIdentifier);
    }
    /**
     * @return the identifiers for resources which are modified.
     */
    public Set<ResourceIdentifier> getModifiedResourceSet() {
        return new HashSet<ResourceIdentifier>(modifiedResourceSet);
    }

    /**
     * Set the vault status for a resource as out of date.
     * @param resourceIdentifier an identifier for the resource which is out of date.
     */
    public void addOutOfDateResource(ResourceIdentifier resourceIdentifier) {
        outOfDateResourceSet.add(resourceIdentifier);
    }
    /**
     * @return the identifiers for resources which are out of date.
     */
    public Set<ResourceIdentifier> getOutOfDateResourceSet() {
        return new HashSet<ResourceIdentifier>(outOfDateResourceSet);
    }

    /**
     * Set the vault status for a resource as existing in the vault but not in the workspace.
     * @param resourceIdentifier an identifier for the resource.
     */
    void addVaultOnlyResource(ResourceIdentifier resourceIdentifier) {
        vaultOnlyResourceNameSet.add(resourceIdentifier);
    }
    /**
     * @return the names of features which exist in the vault but not in the workspace.
     */
    public Set<ResourceIdentifier> getVaultOnlyResourceSet() {
        return new HashSet<ResourceIdentifier>(vaultOnlyResourceNameSet);
    }
    
    /**
     * Set the vault status of a resource as existing in the workspace but not in the vault.
     * @param resourceIdentifier an identifier for the resource.
     */
    void addWorkspaceOnlyResource(ResourceIdentifier resourceIdentifier) {
        workspaceOnlyResourceNameSet.add(resourceIdentifier);
    }
    /**
     * @return the names of resources which exist in the workspace but not in the vault.
     */
    public Set<ResourceIdentifier> getWorkspaceOnlyResourceSet() {
        return new HashSet<ResourceIdentifier>(workspaceOnlyResourceNameSet);
    }
    
    /**
     * Set the vault status of a feature's as unmanaged.
     *   This means that it is not associated with a registered resource manaager.
     * @param resourceIdentifier an identifier for the resource.
     */
    void addUnmanagedResource(ResourceIdentifier resourceIdentifier) {
        unmanagedResourceSet.add(resourceIdentifier);
    }
    
    /**
     * @return the names of resources which are unmanaged.
     *   This means that they are not associated with a registered resource manaager.
     */
    public Set<ResourceIdentifier> getUnmanagedResourceSet() {
        return new HashSet<ResourceIdentifier>(unmanagedResourceSet);
    }
}
