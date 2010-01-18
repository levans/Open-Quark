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
 * AbstractResourcePathStore.java
 * Creation date: Nov 23, 2004.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.openquark.cal.services.ResourcePath.FilePath;



/**
 * This class provides a basic implementation of some of the methods in the ResourcePathStore interfaces.
 * @author Edward Lam
 */
public abstract class AbstractResourcePathStore implements ResourcePathStore {

    /** The resource type for the associated resource. */
    private final String resourceType;
    
    /** The path mapper for this store. */
    private final ResourcePathMapper pathMapper;
    
    /**
     * Constructor for an AbstractResourcePathStore.
     * @param resourceType the resource type for the associated resource.
     * @param pathMapper the path mapper for this store.
     */
    public AbstractResourcePathStore(String resourceType, ResourcePathMapper pathMapper) {
        this.resourceType = resourceType;
        this.pathMapper = pathMapper;
    }
    
    /**
     * {@inheritDoc}
     */
    public FilePath getResourcePath(ResourceName resourceName) {
        return pathMapper.getResourcePath(resourceName);
    }

    /**
     * @return the path mapper
     */
    public ResourcePathMapper getPathMapper() {
        return pathMapper;
    }

    /**
     * @return the type of the managed resource.
     */
    public String getResourceType() {
        return resourceType;
    }

    /**
     * @return the file extension associated with the managed resource.
     */
    public String getFileExtension() {
        return CALSourcePathMapper.CAL_FILE_EXTENSION;
    }

    /**
     * Transfer a resource from this store to another store.
     * @param oldResourceName the name of the resource in this store to be moved to the destination store.
     * @param newResourceName the name of the moved resource in the destination store.
     * @param newResourceStore the destination store.
     * @param status the status tracking object.
     * @return true if the transfer succeeds, false otherwise.
     */
    protected boolean transferResourceToAnotherStore(ResourceName oldResourceName, ResourceName newResourceName, ResourceStore newResourceStore, Status status) {
        if (isRemovable(oldResourceName)) {
            InputStream inStream = getInputStream(oldResourceName);
            OutputStream outStream = newResourceStore.getOutputStream(newResourceName, status);
            
            if (outStream != null) {
                
                try {
                    FileSystemResourceHelper.transferData(inStream, outStream);
                } catch (IOException e) {
                    String message = "Could not rename resource: (Type: " + getResourceType() + ". Name: " + oldResourceName + ")";
                    status.add(new Status(Status.Severity.INFO, message));
                    return false;
                }
                
                this.removeResource(oldResourceName, status);
                return true;
                
            } else {
                String message = "The renamed resource: (Type: " + getResourceType() + ". Name: " + newResourceName + ") is not writable.";
                status.add(new Status(Status.Severity.INFO, message));
                return false;
            }
        } else {
            String message = "The existing resource: (Type: " + getResourceType() + ". Name: " + oldResourceName + ") is not removable.";
            status.add(new Status(Status.Severity.INFO, message));
            return false;
        }
    }
}
