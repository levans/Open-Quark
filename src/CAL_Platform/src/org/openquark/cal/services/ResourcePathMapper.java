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
 * ResourcePathMapper.java
 * Creation date: Nov 23, 2004.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import org.openquark.cal.compiler.ModuleName;


/**
 * A path mapper is responsible for generating paths to resources for path-based resource stores.
 * @author Edward Lam
 */
public interface ResourcePathMapper {
    
    /**
     * @return the resource folder root.  This will be a subfolder of the base folder, or null if there is no such folder.
     */
    ResourcePath.Folder getBaseResourceFolder();
    
    /**
     * Get the base path to a resource representing a given feature.
     * 
     * @param resourceName the name for the resource.
     * @return the path under which the resource corresponding to the feature should be located,
     *   or null if the feature can not correspond to a resource.
     */
    ResourcePath.FilePath getResourcePath(ResourceName resourceName);
    
    /**
     * Get the name of a resource corresponding to the given base path.
     * 
     * @param path the path under which the resource is located.
     * @return the corresponding ResourceName, or null if there is no mapping from the path to a ResourceName.
     */
    ResourceName getResourceNameFromPath(ResourcePath.FilePath path);
    
    /**
     * Get the name of a resource corresponding to the given base path.
     * 
     * @param folder the folder under which the resource is located.
     * @param fileName the file name of the resource inside the folder.
     * @return the corresponding ResourceName, or null if there is no mapping from the path to a ResourceName.
     */
    ResourceName getResourceNameFromFolderAndFileName(ResourcePath.Folder folder, String fileName);
    
    /**
     * @return the file extension associated with files of the associated resource type.
     * Can be null if the resources do not have a fixed file extension.
     */
    String getFileExtension();

    
    /**
     * A path mapper for module resources.
     * @author Edward Lam
     */
    public interface Module extends ResourcePathMapper {
        /**
         * Get the base path to the file or folder which contains the resources for the given module.
         * 
         * @param moduleName the name of a module.
         * @return the path under which the resources corresponding to the module should be located,
         *   or null if the module can not correspond to a resource path.
         */
        ResourcePath getModuleResourcePath(ModuleName moduleName);
        
        /**
         * Returns the module name represented by the given resource path.
         * @param moduleResourcePath a resource path representing a module.
         * @return the module name, or null if the path does not correspond to a module.
         */
        ModuleName getModuleNameFromResourcePath(ResourcePath moduleResourcePath);
        
    }
}