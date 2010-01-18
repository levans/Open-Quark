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
 * ResourceManager.java
 * Creation date: Oct 7, 2004.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * A manager which handles saving and loading a resource with respect to a resource store.
 * @author Edward Lam
 */
public abstract class ResourceManager {
    
    /**
     * @return a string identifying the resource type managed by this manager.
     */
    public abstract String getResourceType();

    /**
     * @return the resource store backing this manager.
     */
    public abstract ResourceStore getResourceStore();

    
    /**
     * Get the timestamp associated with a given feature.
     * @param resourceName the name of the resource.
     * @return A long value representing the time the resource for the feature was last modified, measured in milliseconds
     * 0L if the resource for the given feature does not exist in the store.
     */
    public final long getTimeStamp(ResourceName resourceName) {
        return getResourceStore().getTimeStamp(resourceName);
    }
    
    /**
     * @param resourceName the resource name of the resource.
     * @return debugging information about the resource, e.g. the actual location of the resource. Can be null if the resource does not exist.
     */
    public String getDebugInfo(ResourceName resourceName) {
        return getResourceStore().getDebugInfo(resourceName);
    }

    /**
     * Import a resource from its stored form into management by this manager.
     * @param workspaceResource the resource to import.
     * @param importStatus the tracking status object
     * @return the sync time for the resource, or null if the import failed.
     */
    public final WorkspaceResource.SyncTime importResource(WorkspaceResource workspaceResource, Status importStatus) {
        String managedResourceType = getResourceType();
        
        if (!workspaceResource.getResourceType().equals(managedResourceType)) {
            throw new IllegalArgumentException("This resource manager does not accept resources of type " + workspaceResource.getResourceType() +
                    ".  Only resources of type " + managedResourceType + " are accepted.");
        }
        
        ResourceName resourceName = workspaceResource.getIdentifier().getResourceName();
        InputStream inputStream = workspaceResource.getInputStream(importStatus);
        
        if (inputStream == null) {
            importStatus.add(new Status(Status.Severity.ERROR, "Could not get input stream for: " + workspaceResource.getIdentifier(), null));
            return null;
        }
        
        // Import it.  Saving the resource also closes the input stream.
        boolean imported = saveResource(resourceName, inputStream, importStatus);
        if (!imported) {
            importStatus.add(new Status(Status.Severity.WARNING, "Could not import resource for: " + workspaceResource.getIdentifier(), null));
        }
        
        return new WorkspaceResource.SyncTime(new ResourceIdentifier(managedResourceType, resourceName), getTimeStamp(resourceName));
    }

    /**
     * Save the given resource.
     * @param resourceName the name of the resource to save.
     * @param sourceInputStream the input stream on the resource to save.  Assumed to be non-null.  This stream will be closed.
     * @param saveStatus the tracking status object.
     * @return whether the source was successfully saved.
     */
    protected boolean saveResource(ResourceName resourceName, InputStream sourceInputStream, Status saveStatus) {
        
        // Create an output stream for the source.
        OutputStream sourceOutputStream = getResourceStore().getOutputStream(resourceName, saveStatus);
        if (sourceOutputStream == null) {
            String errorString = "Could not create output stream.";
            saveStatus.add(new Status(Status.Severity.ERROR, errorString));
            return false;
        }

        try {
            // Transfer the data between the streams.
            FileSystemResourceHelper.transferData(sourceInputStream, sourceOutputStream);

        } catch (IOException ioe) {
            String message = "Couldn't save resource for feature " + resourceName + ", type " + getResourceType();
            saveStatus.add(new Status(Status.Severity.ERROR, message, ioe));
            return false;
        
        } finally {
            try {
                sourceInputStream.close();
            } catch (IOException e) {
            }
            try {
                sourceOutputStream.flush();
                sourceOutputStream.close();
            } catch (IOException e) {
            }
        }
        
        return true;
    }

    /**
     * Remove a given resource from the store.
     * @param resourceName the name of the resource to remove from the store.
     * @param removeStatus the tracking status object.
     */
    public void removeResource(ResourceName resourceName, Status removeStatus) {
        getResourceStore().removeResource(resourceName, removeStatus);
    }

    /**
     * Remove all resources from the store.
     * @param removeStatus the tracking status object.
     */
    public void removeAllResources(Status removeStatus) {
        getResourceStore().removeAllResources(removeStatus);
    }

}
