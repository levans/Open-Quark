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
 * CarPathStoreHelper.java
 * Creation date: Jan 10, 2006.
 * By: Joseph Wong
 */
package org.openquark.cal.services;

import java.io.InputStream;
import java.util.Iterator;

/**
 * This is a helper class which contains methods to determine which features are
 * present in a CAL Archive (Car) path store.
 * 
 * @author Joseph Wong
 */
final class CarPathStoreHelper {
    
    /*
     * Not intended to be instantiated.
     */
    private CarPathStoreHelper() {
    }

    /**
     * Get an iterator over the resources in the given store.
     * 
     * @param carStore the store containing Cars.
     * @return an iterator over the resources in the given store.
     * Note that the InputStream in the WorkspaceResource returned by the iterator is allowed to be null.
     */
    public static Iterator<WorkspaceResource> getResourceIterator(final CarStore carStore) {
        // Create the iterator.  This just shadows an iterator over the list of feature names.
        Iterator<WorkspaceResource> resourceIterator = new Iterator<WorkspaceResource>() {
            
            private final Iterator<ResourceName> carNameIterator = carStore.getCarNames().iterator();
            
            public boolean hasNext() {
                return carNameIterator.hasNext();
            }
            
            public WorkspaceResource next() {
                final ResourceName carResourceName = carNameIterator.next();
                final ResourceIdentifier carIdentifier = new ResourceIdentifier(carStore.getResourceType(), carResourceName);
                
                return new WorkspaceResource(carIdentifier) {
                    
                    @Override
                    public InputStream getInputStream(Status status) {
                        return carStore.getInputStream(carResourceName);
                    }
                    @Override
                    public long getTimeStamp() {
                        return carStore.getTimeStamp(carResourceName);
                    }
                    @Override
                    public String getDebugInfo() {
                        return carStore.getDebugInfo(carResourceName);
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
