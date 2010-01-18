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
 * ResourcePathStore.java
 * Creation date: Nov 23, 2004.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.util.List;


/**
 * A ModuleResourceStore which uses ResourcePaths to find resources.
 * @author Edward Lam
 */
public interface ResourcePathStore extends ResourceStore {

    /**
     * Get the path to the resource representing a given feature in this store.
     * @param resourceName the name of the resource.
     * @return the path under which the resource corresponding to the feature will be stored, or null
     *   if the file can not correspond to a resource in the store.
     */
    public ResourcePath.FilePath getResourcePath(ResourceName resourceName);

    /**
     * @param folder a folder
     * @return the names representing the resources in the given folder.
     */
    public List<ResourceName> getFolderResourceNames(ResourcePath.Folder folder);
    
    /**
     * @param folder a folder
     * @param filter the filter used for identifying the resource names to keep in the returned list.
     * @return the names representing the resources in the given folder.
     */
    public List<ResourceName> getFilteredFolderResourceNames(ResourcePath.Folder folder, ResourceName.Filter filter);
}
