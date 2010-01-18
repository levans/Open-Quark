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
 * WorkspaceResource.java
 * Creation date: Aug 4, 2004.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.io.InputStream;


/**
 * A persistent object which exists within a workspace, is locatable, and contains attributes and content.
 * @author Edward Lam
 */
public abstract class WorkspaceResource {
    
    /** The string identifying the resource type for workspace declarations. */
    public static final String WORKSPACE_DECLARATION_RESOURCE_TYPE = "WorkspaceDeclaration";
    
    /** The string identifying the resource type for metadata resources. */
    public static final String METADATA_RESOURCE_TYPE = "Metadata";

    /** The string identifying the resource type for gem designs. */
    public static final String GEM_DESIGN_RESOURCE_TYPE = "GemDesign";
    
    /** The string identifying the resource type for Cars. */
    public static final String CAR_RESOURCE_TYPE = "Car";
    
    /** The string identifying the resource type for user resources. */
    public static final String USER_RESOURCE_TYPE = "UserResource";
    
    
    /** The identifier naming this resource. */
    private final ResourceIdentifier identifier;

    /**
     * Constructor for a WorkspaceResource.
     * @param identifier the identifier naming this resource.
     */
    public WorkspaceResource(ResourceIdentifier identifier) {
        this.identifier = identifier;
    }
    
    /**
     * @return the identifier naming this resource.
     */
    public final ResourceIdentifier getIdentifier() {
        return identifier;
    }
    
    /**
     * Get the type of the resource.
     * @return a string which identifies the type of the resource.
     */
    public final String getResourceType() {
        return identifier.getResourceType();
    }
    
    /**
     * @return debugging information about the resource, e.g. the actual location of the resource.
     */
    public abstract String getDebugInfo();

    /**
     * Get the resource in the form of a newly-instantiated input stream.
     * The client should take care to call close() on the stream when done.
     * @param status the tracking status object.
     * @return the Stream, or null if the stream could not be instantiated (eg. for a file, if the
     *   file no longer exists).
     */
    public abstract InputStream getInputStream(Status status);

    /**
     * Get the timestamp for the given resource.  ie. the time when it was last modified.
     * @return - long
     *      A long value representing the time the source was last modified, measured in milliseconds
     *      since the epoch (00:00:00 GMT, January 1, 1970), or 0L if the information is not available.
     */
    public abstract long getTimeStamp ();
    
    /**
     * Warning- this class should only be used by the CAL services implementation. It is not part of the
     * external API of the CAL platform.
     * <p>
     * A simple wrapper around a resource identifier and its sync time.
     * @author Edward Lam
     */
    public static class SyncTime {
        
        private final ResourceIdentifier identifier;
        private final long syncTime;
    
        /**
         * Constructor for a ResourceRevision.
         */
        public SyncTime(ResourceIdentifier identifier, long syncTime) {
            this.identifier = identifier;
            this.syncTime = syncTime;
        }
        /**
         * @return the resource type.
         */
        public String getResourceType() {
            return identifier.getResourceType();
        }
        /**
         * @return the resource identifier.
         */
        public ResourceIdentifier getIdentifier() {
            return identifier;
        }
        /**
         * @return the syncTime.
         */
        public long getSyncTime() {
            return syncTime;
        }
    }
}
