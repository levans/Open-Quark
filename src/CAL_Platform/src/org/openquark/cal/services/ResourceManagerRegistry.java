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
 * ResourceManagerRegistry.java
 * Creation date: Oct 8, 2004.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.openquark.cal.compiler.ModuleSourceDefinition;
import org.openquark.cal.metadata.MetadataManager;



/**
 * A ResourceManagerRegistry manages the WorkspaceResourceManagers for the workspace.
 * @author Edward Lam
 */
public class ResourceManagerRegistry {
    
    /** Map from resource type to the manager for resources of that type.  Sorted by resource type.  */
    private final Map<String, ResourceManager> resourceTypeToManagerMap = new LinkedHashMap<String, ResourceManager>();
    
    /**
     * Constructor for a ResourceManagerRegistry.
     */
    public ResourceManagerRegistry() {
    }

    /**
     * @return the resource types supported by managers in the registry.
     */
    public Set<String> getResourceTypes() {
        return new LinkedHashSet<String>(resourceTypeToManagerMap.keySet());
    }
    
    /**
     * @return the resource managers in the registry.
     */
    public Set<ResourceManager> getResourceManagers() {
        return new LinkedHashSet<ResourceManager>(resourceTypeToManagerMap.values());
    }
    
    /**
     * Get the resource manager which handles ModuleResources of a given type.
     * @param resourceType the String which identifies the type of resource.
     * @return the resource manager which handles resource of that type, or null if there isn't any.
     */
    public ResourceManager getResourceManager(String resourceType) {
        return resourceTypeToManagerMap.get(resourceType);
    }

    /**
     * @return the registered source manager, if any.
     */
    public CALSourceManager getSourceManager() {
        return (CALSourceManager)getResourceManager(ModuleSourceDefinition.RESOURCE_TYPE);
    }
    
    /**
     * @return the registered metadataManager, if any.
     */
    public MetadataManager getMetadataManager() {
        return (MetadataManager)getResourceManager(WorkspaceResource.METADATA_RESOURCE_TYPE);
    }
    
    /**
     * @return the registered gem design manager, if any.
     */
    public GemDesignManager getDesignManager() {
        return (GemDesignManager)getResourceManager(WorkspaceResource.GEM_DESIGN_RESOURCE_TYPE);
    }

    /**
     * @return the registered workspace manager, if any.
     */
    public WorkspaceDeclarationManager getWorkspaceDeclarationManager() {
        return (WorkspaceDeclarationManager)getResourceManager(WorkspaceResource.WORKSPACE_DECLARATION_RESOURCE_TYPE);
    }
    
    /**
     * @return the registered Car manager, if any.
     */
    public CarManager getCarManager() {
        return (CarManager)getResourceManager(WorkspaceResource.CAR_RESOURCE_TYPE);
    }

    /**
     * @return the registered user resource manager, if any.
     */
    public UserResourceManager getUserResourceManager() {
        return (UserResourceManager)getResourceManager(WorkspaceResource.USER_RESOURCE_TYPE);
    }

    /**
     * Register a resource manager with this registry.
     * @param resourceManager the resource manager to register with the registry.
     */
    public void registerResourceManager(ResourceManager resourceManager) {
        String resourceType = resourceManager.getResourceType();
        if (resourceTypeToManagerMap.containsKey(resourceType)) {
            CALWorkspace.SERVICES_LOGGER.log(Level.WARNING, "A resource manager is already registered for type '" + resourceType + "'");
        }
        resourceTypeToManagerMap.put(resourceType, resourceManager);
    }

}
