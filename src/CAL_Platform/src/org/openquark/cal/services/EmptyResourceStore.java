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
 * EmptyResourceStore.java
 * Creation date: Dec 10, 2004.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.util.EmptyIterator;


/**
 * A ResourceStore which contains no resources.
 * @author Edward Lam
 */
abstract class EmptyResourceStore implements ResourceStore {
    
    /** The resource type for this store. */
    private final String resourceType;
    
    /**
     * Constructor for a EmptyResourceStore.
     * @param resourceType
     */
    EmptyResourceStore(String resourceType) {
        this.resourceType = resourceType;
    }
    
    /**
     * {@inheritDoc}
     */
    public InputStream getInputStream(ResourceName resourceName) {
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    public OutputStream getOutputStream(ResourceName resourceName, Status status) {
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getDebugInfo(ResourceName resourceName) {
        return "from EmptyResourceStore";
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean hasFeature(ResourceName resourceName) {
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    public Iterator<WorkspaceResource> getResourceIterator() {
        return EmptyIterator.emptyIterator();
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean renameResource(ResourceName oldResourceName, ResourceName newResourceName, ResourceStore newResourceStore, Status removeStatus) {
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    public void removeResource(ResourceName resourceName, Status removeStatus) {
    }
    
    /**
     * {@inheritDoc}
     */
    public void removeAllResources(Status removeStatus) {
    }
    
    /**
     * {@inheritDoc}
     */
    public long getTimeStamp(ResourceName resourceName) {
        return 0;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isWriteable() {
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isWriteable(ResourceName resourceName) {
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isRemovable(ResourceName resourceName) {
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getResourceType() {
        return resourceType;
    }
    
    /**
     * An EmptyResourceStore which would hold module resources.
     * @author Edward Lam
     */
    public static class Module extends EmptyResourceStore implements ResourceStore.Module {
        
        /**
         * Constructor for a EmptyResourceStore.Module.
         * @param resourceType
         */
        public Module(String resourceType) {
            super(resourceType);
        }
        
        /**
         * {@inheritDoc}
         */
        public Set<ModuleName> getModuleNames() {
            return Collections.emptySet();
        }
        
        /**
         * {@inheritDoc}
         */
        public Iterator<WorkspaceResource> getResourceIterator(ModuleName moduleName) {
            return EmptyIterator.emptyIterator();
        }
        
        /**
         * {@inheritDoc}
         */
        public void removeModuleResources(ModuleName moduleName, Status removeStatus) {
        }
        
        /**
         * {@inheritDoc}
         */
        public List<ResourceName> getModuleResourceNameList(ModuleName moduleName) {
            return Collections.emptyList();
        }
    }
    
    /**
     * An empty resource store which would hold workspace declarations.
     * @author Edward Lam
     */
    public static class Workspace extends EmptyResourceStore implements WorkspaceDeclarationStore {
        
        /**
         * Constructor for a EmptyResourceStore.Workspace.
         * @param resourceType
         */
        Workspace(String resourceType) {
            super(resourceType);
        }

        /**
         * {@inheritDoc}
         */
        public Set<ResourceName> getWorkspaceNames() {
            return Collections.emptySet();
        }
    }
    
    /**
     * An empty resource store which would hold CAL Archives (Cars).
     *
     * @author Joseph Wong
     */
    public static class Car extends EmptyResourceStore implements CarStore {

        /**
         * Constructor for a EmptyResourceStore.Car.
         * @param resourceType
         */
        Car(String resourceType) {
            super(resourceType);
        }

        /**
         * {@inheritDoc}
         */
        public Set<ResourceName> getCarNames() {
            return Collections.emptySet();
        }

        /**
         * {@inheritDoc}
         */
        public org.openquark.cal.services.Car getCar(String carName) {
            return null;
        }
    }
}