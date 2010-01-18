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
 * WorkspaceDeclarationPathStoreHelper.java
 * Creation date: Dec 13, 2004.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.io.InputStream;
import java.util.Iterator;


/**
 * This is a helper class which contains methods to determine which features are present in a workspace declaration path store.
 * @author Edward Lam
 */
class WorkspaceDeclarationPathStoreHelper {
    
    /*
     * Not intended to be instantiated.
     */
    private WorkspaceDeclarationPathStoreHelper() {
    }

    /**
     * Get an iterator over the resources in the given store.
     * 
     * @param workspaceDeclarationStore the store containing workspace declarations.
     * @return an iterator over the resources in this file store.
     * Note that the InputStream in the WorkspaceResource returned by the iterator is allowed to be null.
     */
    public static Iterator<WorkspaceResource> getResourceIterator(final WorkspaceDeclarationStore workspaceDeclarationStore) {
        // Create the iterator.  This just shadows an iterator over the list of feature names.
        Iterator<WorkspaceResource> resourceIterator = new Iterator<WorkspaceResource>() {
            
            private final Iterator<ResourceName> workspaceNameIterator = workspaceDeclarationStore.getWorkspaceNames().iterator();
            
            public boolean hasNext() {
                return workspaceNameIterator.hasNext();
            }
            
            public WorkspaceResource next() {
                final ResourceName workspaceResourceName = workspaceNameIterator.next();
                final ResourceIdentifier workspaceIdentifier = new ResourceIdentifier(workspaceDeclarationStore.getResourceType(), workspaceResourceName);
                
                return new WorkspaceResource(workspaceIdentifier) {
                    
                    @Override
                    public InputStream getInputStream(Status status) {
                        return workspaceDeclarationStore.getInputStream(workspaceResourceName);
                    }
                    @Override
                    public long getTimeStamp() {
                        return workspaceDeclarationStore.getTimeStamp(workspaceResourceName);
                    }
                    @Override
                    public String getDebugInfo() {
                        return workspaceDeclarationStore.getDebugInfo(workspaceResourceName);
                    }
                };
            }
            
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
        
        return resourceIterator;
    }
}
