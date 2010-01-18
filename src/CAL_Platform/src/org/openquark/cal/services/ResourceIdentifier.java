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
 * ResourceIdentifier.java
 * Creation date: Sep 9, 2005.
 * By: Joseph Wong
 */
package org.openquark.cal.services;

import java.util.Locale;

/**
 * A ResourceIdentifier identifies a resource.
 * 
 * This class is *not* meant to be subclassed in cases where a resouce cannot
 * simply be identified by just the resource type and feature name. Instead,
 * ResourceName should be subclassed to provide the extra identification fields.
 * One example is with CAL metadata which are locale-specific, and hence the
 * full identifier for a metadata resource needs to be (type, feature name,
 * locale), meaning that the resource name should be a (feature name, locale) pair.
 * 
 * @see ResourceName
 * 
 * @author Joseph Wong
 */
public final class ResourceIdentifier {

    /** The string identifying the type of this resource. */
    private final String resourceType;
    
    /** The remainder of the identifier, having taken into account the type. */
    private final ResourceName resourceName;
    
    /**
     * Constructs a ResourceIdentifier.
     * @param resourceType the type of the resource.
     * @param resourceName the remainder of the identifier, having taken into account the type.
     */
    public ResourceIdentifier(String resourceType, ResourceName resourceName) {
        if (resourceType == null || resourceName == null) {
            throw new NullPointerException();
        }
        
        this.resourceType = resourceType;
        this.resourceName = resourceName;
    }
    
    /**
     * Static helper method for constructing a resource identifier representing a general resource.
     * @param resourceType the type of the resource.
     * @param featureName the feature name.
     * @return a ResourceIdentifier representing the resource.
     */
    public static ResourceIdentifier make(String resourceType, FeatureName featureName) {
        return new ResourceIdentifier(resourceType, new ResourceName(featureName));
    }
    
    /**
     * Static helper method for constructing a resource identifier representing a localized resource.
     * @param resourceType the type of the resource.
     * @param featureName the feature name.
     * @param locale the locale associated with the resource.
     * @return a ResourceIdentifier representing the localized resource.
     */
    public static ResourceIdentifier makeLocalized(String resourceType, FeatureName featureName, Locale locale) {
        return new ResourceIdentifier(resourceType, new LocalizedResourceName(featureName, locale));
    }
    
    /**
     * @return the type of the resource.
     */
    public String getResourceType() {
        return resourceType;
    }
    
    /**
     * @return the remainder of the identifier, having taken into account the type.
     */
    public ResourceName getResourceName() {
        return resourceName;
    }
    
    /**
     * @return the name of the feature represented by this resource.
     */
    public FeatureName getFeatureName() {
        return resourceName.getFeatureName();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "ResourceIdentifier: (ResourceType=" + resourceType + ", " + resourceName + ")";
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ResourceIdentifier) {
            ResourceIdentifier otherIdentifier = (ResourceIdentifier)obj;
            return otherIdentifier.resourceType.equals(resourceType) && otherIdentifier.resourceName.equals(resourceName);
        }
        
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return 37 * resourceName.hashCode() + 17 * resourceType.hashCode();
    }
}
