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
 * ResourceStore.java
 * Creation date: Dec 10, 2004.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.openquark.cal.compiler.ModuleName;


/**
 * A ResourceStore represents a repository which holds the resources for a given resource type.
 * 
 * @author Edward Lam
 */
public interface ResourceStore {

    /**
     * @return the type of the managed resource.
     */
    public String getResourceType();
    
    /**
     * Get an iterator over the resources in this store.
     * @return an iterator over the resources in this file store.
     * Note that the InputStream in the WorkspaceResource returned by the iterator is allowed to be null.
     */
    public Iterator<WorkspaceResource> getResourceIterator();
    
    /**
     * Get an input stream which from which the given feature may be read.
     * @param resourceName the resource name of the resource.
     * @return an input stream for the given feature, or null if the input stream could not be created.
     */
    public InputStream getInputStream(ResourceName resourceName);

    /**
     * Get an output stream to which the indicated feature may be written.
     * @param resourceName the resource name of the resource.
     * @param status the tracking status object
     * @return an output stream for the given feature, or null if the output stream could not be created.
     */
    public OutputStream getOutputStream(ResourceName resourceName, Status status);

    /**
     * Get the time stamp associated with the resource for the given feature.
     * @param resourceName the resource name of the resource.
     * @return A long value representing the time the resource for the feature was last modified, measured in milliseconds
     * 0L if the resource for the given feature does not exist in the store.
     */
    public long getTimeStamp(ResourceName resourceName);

    /**
     * Determine whether the store contains a resource for the given feature.
     * @param resourceName the resource name of the resource to check.
     * @return true if the feature is associated with a resource in this store, false otherwise
     */
    public boolean hasFeature(ResourceName resourceName);
    
    /**
     * @return whether this store is writeable.
     */
    public boolean isWriteable();

    /**
     * @param resourceName the resource name of the resource.
     * @return whether the resource for a given feature exists and is writeable.
     */
    public boolean isWriteable(ResourceName resourceName);
    
    /**
     * @param resourceName the resource name of the resource.
     * @return whether the resource for a given feature exists and is removable.
     */
    public boolean isRemovable(ResourceName resourceName);
    
    /**
     * Remove a given resource from the store.
     * @param resourceName the resource name of the resource to remove from the store.
     * @param removeStatus the tracking status object.
     */
    public void removeResource(ResourceName resourceName, Status removeStatus);

    /**
     * Remove all resources from the store.
     * @param removeStatus the tracking status object.
     */
    public void removeAllResources(Status removeStatus);
    
    /**
     * Rename a given resource in the store (or across stores), if it exists. Note that this will only change the name associated with the resource;
     * it will not perform any changes to the actual resource itself. Clients are responsible for updating the contents
     * of the resources to reflect the new name.
     * 
     * @param oldResourceName
     * @param newResourceName
     * @param newResourceStore the new resource store to accept the renamed resource. It can be the same store as this one, or another one.
     * @param renameStatus the tracking status object.
     * @return True if the renaming occurred, false otherwise. The method will return false if the resource does not exist
     * in the resource store.
     */
    public boolean renameResource(ResourceName oldResourceName, ResourceName newResourceName, ResourceStore newResourceStore, Status renameStatus);
    
    /**
     * @param resourceName the resource name of the resource.
     * @return debugging information about the resource, e.g. the actual location of the resource. Can be null if the resource does not exist.
     */
    public String getDebugInfo(ResourceName resourceName);

    /**
     * A ResourceStore.Module represents a repository which holds the module resources for a given resource type.
     * 
     * @author Edward Lam
     */
    public interface Module extends ResourceStore {
        /**
         * @return the names of modules for which any resources exists, either for the module itself, or
         *   for features in that module.
         */
        public Set<ModuleName> getModuleNames();

        /**
         * Get an iterator over the resources in this store.
         * @param moduleName the name of the module for which resources will be retrieved.
         * @return an iterator over the resources in this file store.
         * Note that the InputStream in the WorkspaceResource returned by the iterator is allowed to be null.
         */
        public Iterator<WorkspaceResource> getResourceIterator(ModuleName moduleName);
        
        /**
         * Remove the resources for a module from the store.
         * @param moduleName the name of a module.
         * @param removeStatus the tracking status object.
         */
        public void removeModuleResources(ModuleName moduleName, Status removeStatus);

        /**
         * Get the names of resources associated with the module.
         * @param moduleName the name of a module.
         * @return the names of resources associated with the module.
         */
        List<ResourceName> getModuleResourceNameList(ModuleName moduleName);
    }
}