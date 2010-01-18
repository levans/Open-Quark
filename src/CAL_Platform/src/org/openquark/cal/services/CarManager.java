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
 * CarManager.java
 * Creation date: Jan 10, 2006.
 * By: Joseph Wong
 */
package org.openquark.cal.services;

/**
 * A manager to manage loading CAL Archives (Cars) using a Car store.
 *
 * @author Joseph Wong
 */
public class CarManager extends ResourceManager {
    
    /** The store used by this manager to manage Cars. */
    private final CarStore carStore;
    
    /**
     * Constructs a CarManager, encapsulating the given store.
     * @param carStore the store for Cars managed by this manager.
     */
    public CarManager(CarStore carStore) {
        if (carStore == null) {
            throw new NullPointerException();
        }
        
        this.carStore = carStore;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResourceType() {
        return WorkspaceResource.CAR_RESOURCE_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResourceStore getResourceStore() {
        return carStore;
    }
}
