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
 * ResourceRevision.java
 * Creation date: Dec 9, 2004.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openquark.cal.compiler.ModuleName;



/**
 * Warning- this class should only be used by the CAL services implementation. It is not part of the
 * external API of the CAL platform.
 * <p>
 * A simple wrapper around a module resource, its feature name, and its revision.
 * 
 * This class exists because vaults may keep revisions of module resources, independently of revisions for modules themselves.
 * eg. in the standard vault, there is only ever one revision of a module and its resources, but the actual resource revision number
 * changes as the resources are modified.  The revisions associated with the most up-to-date resources may be different for the
 * different resources.
 * 
 * @author Edward Lam
 */
public class ResourceRevision {
    
    /** The resource identifier. */
    private final ResourceIdentifier resourceIdentifier;
    
    /** The revision number. */
    private final long revisionNumber;
    
    /**
     * Constructor for a ResourceRevision.
     * @param resourceIdentifier
     * @param revision
     */
    public ResourceRevision(ResourceIdentifier resourceIdentifier, long revision) {
        this.resourceIdentifier = resourceIdentifier;
        this.revisionNumber = revision;
    }
    
    /**
     * @return the resource type.
     */
    public String getResourceType() {
        return resourceIdentifier.getResourceType();
    }
    
    /**
     * @return the resource identifier.
     */
    public ResourceIdentifier getIdentifier() {
        return resourceIdentifier;
    }
    
    /**
     * @return the revision.
     */
    public long getRevisionNumber() {
        return revisionNumber;
    }
    
    
    /**
     * The revision info for a group of resources.
     * @author Edward Lam
     */
    public static class Info {
        
        /**
         * The map which tracks resource revisions.
         * Map from resource type to a map for that type. The map for the type maps ResourceName to ResourceRevision for that type.
         */
        private final Map<String, Map<ResourceName, ResourceRevision>> resourceRevisionMap = new HashMap<String, Map<ResourceName,ResourceRevision>>();
        
        /**
         * Constructor for a Info.
         * @param revisionList
         */
        public Info(List<ResourceRevision> revisionList) {
            
            for (final ResourceRevision resourceRevision : revisionList) {
                updateResourceRevision(resourceRevision);
            }
        }
        
        /**
         * Constructor for a Info.
         */
        public Info() {
            this(Collections.<ResourceRevision>emptyList());
        }
    
        /**
         * @return the resource types in this object.
         */
        public String[] getResourceTypes() {
            Set<String> resourceTypeSet = resourceRevisionMap.keySet();
            String[] returnVal = new String[resourceTypeSet.size()];
            return resourceTypeSet.toArray(returnVal);
        }
    
        /**
         * Get the revision for a given resource.
         * @param resourceIdentifier the identifier of the resource.
         * @return the revision number of that resource, or 0L if that resource is not in this object.
         */
        public long getResourceRevision(ResourceIdentifier resourceIdentifier) {
            Map<ResourceName,ResourceRevision> resourceTypeMap = resourceRevisionMap.get(resourceIdentifier.getResourceType());
            if (resourceTypeMap == null) {
                return 0L;
            }
            
            ResourceRevision resourceRevision = resourceTypeMap.get(resourceIdentifier.getResourceName());
            if (resourceRevision == null) {
                return 0L;
            }
            
            return resourceRevision.getRevisionNumber();
        }
        
        /**
         * Get the resource revisions for a given resource type.
         * @param resourceType the type for which the resource revisions should be returned.
         * @return the resource revisions for that type.  Empty if there aren't any.
         */
        public Set<ResourceRevision> getResourceRevisions(String resourceType) {
            Map<ResourceName,ResourceRevision> resourceTypeMap = resourceRevisionMap.get(resourceType);
            if (resourceTypeMap == null) {
                return Collections.emptySet();
            }
            return new HashSet<ResourceRevision>(resourceTypeMap.values());
        }
    
        /**
         * Update some revision info in this object.
         * @param resourceRevision an updated revision.
         */
        void updateResourceRevision(ResourceRevision resourceRevision) {
            String resourceType = resourceRevision.getResourceType();
            ResourceName resourceName = resourceRevision.getIdentifier().getResourceName();
            
            Map<ResourceName, ResourceRevision> resourceTypeMap = resourceRevisionMap.get(resourceType);
            if (resourceTypeMap == null) {
                resourceTypeMap = new HashMap<ResourceName, ResourceRevision>();
                resourceRevisionMap.put(resourceType, resourceTypeMap);
            }
            
            resourceTypeMap.put(resourceName, resourceRevision);
        }
        
        /**
         * Update some revision info in this object.
         * @param resourceIdentifier the identifier of the resource.
         * @param resourceRevisionNum the updated revision number.
         */
        void updateResourceRevision(ResourceIdentifier resourceIdentifier, long resourceRevisionNum) {
            updateResourceRevision(new ResourceRevision(resourceIdentifier, resourceRevisionNum));
        }
        
        /**
         * Update some revision info in this object.
         * @param updatedInfo some updated revision info.
         */
        void updateResourceRevisionInfo(Info updatedInfo) {
            for (final String updatedFeatureType : updatedInfo.resourceRevisionMap.keySet()) {
                Map<ResourceName,ResourceRevision> updatedFeatureNameToSyncTimeMap = updatedInfo.resourceRevisionMap.get(updatedFeatureType);
    
                for (final ResourceRevision updatedRevision : updatedFeatureNameToSyncTimeMap.values()) {
                    updateResourceRevision(updatedRevision);
                }
            }
        }
        
        /**
         * @param resourceIdentifier the identifier of the resource whose revision info should be removed.
         * @return true if there was a resource revision to remove.
         */
        public boolean removeResourceRevision(ResourceIdentifier resourceIdentifier) {
            Map<ResourceName,ResourceRevision> resourceTypeMap = resourceRevisionMap.get(resourceIdentifier.getResourceType());
            if (resourceTypeMap != null) {
                return resourceTypeMap.remove(resourceIdentifier.getResourceName()) != null;
            }
            return false;
        }
        
        /**
         * Helper class to remove info associated with a module.
         * @param moduleName the name of the module whose info to remove
         */
        public void removeModule(ModuleName moduleName) {
            
            // Iterate over the maps for the resource types.
            for (final Map<ResourceName,ResourceRevision> resourceNameToRevisionMap : resourceRevisionMap.values()) {
                // Iterate over the mappings in the map for that resource type.
                for (Iterator<ResourceName> it = resourceNameToRevisionMap.keySet().iterator(); it.hasNext(); ) {
                    ResourceName resourceName = it.next();
                    FeatureName featureName = resourceName.getFeatureName();
                    if (!(featureName instanceof CALFeatureName)) {
                        continue;
                    }
                    CALFeatureName calFeatureName = (CALFeatureName)featureName;
    
                    // Check that the feature name has a module name which is the same as the one we're looking for.
                    if (calFeatureName.hasModuleName() && calFeatureName.toModuleName().equals(moduleName)) {
                        it.remove();
                    }
                }
            }
        }
    
        /**
         * Clear all revision info from this object.
         */
        void clear() {
            resourceRevisionMap.clear();
        }
    }
    
}