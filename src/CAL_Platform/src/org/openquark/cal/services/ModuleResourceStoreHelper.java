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
 * ModuleResourceStoreHelper.java
 * Creation date: Dec 13, 2004.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.util.EmptyIterator;
import org.openquark.util.IteratorChain;



/**
 * Warning- this class should only be used by the CAL services implementation. It is not part of the
 * external API of the CAL platform.
 * <p>
 * This is a static helper class to return resource iterators for module resource stores.
 * @author Edward Lam
 */
public final class ModuleResourceStoreHelper {
    
    /*
     * Not intended to be instantiated.
     */
    private ModuleResourceStoreHelper() {
    }
    
    /**
     * Get an iterator over the resources in a store.
     * 
     * @param moduleResourceStore the store for which to return the iterator.
     * 
     * @return an iterator over the resources in the store.
     * Note that the InputStream in the WorkspaceResource returned by the iterator is allowed to be null.
     */
    public static Iterator<WorkspaceResource> getResourceIterator(ResourceStore.Module moduleResourceStore) {
        List<Iterator<WorkspaceResource>> iteratorList = new ArrayList<Iterator<WorkspaceResource>>();
        for (final ModuleName moduleName : moduleResourceStore.getModuleNames()) {
            iteratorList.add(getResourceIterator(moduleResourceStore, moduleName));
        }
        
        return iteratorList.isEmpty() ? 
                EmptyIterator.<WorkspaceResource>emptyIterator() : 
                    new IteratorChain<WorkspaceResource>(iteratorList);
    }
    
    /**
     * Get an iterator over the resources in a store.
     * 
     * @param moduleResourceStore the store for which to return the iterator.
     * @param moduleName the name of the module for which resources will be retrieved.
     * 
     * @return an iterator over the resources in this file store.
     * Note that the InputStream in the WorkspaceResource returned by the iterator is allowed to be null.
     */
    public static Iterator<WorkspaceResource> getResourceIterator(final ResourceStore.Module moduleResourceStore, ModuleName moduleName) {
        
        final List<ResourceName> moduleResourceNameList = moduleResourceStore.getModuleResourceNameList(moduleName);
        
        // Create the iterator.  This just shadows an iterator over the list of feature names.
        Iterator<WorkspaceResource> resourceIterator = new Iterator<WorkspaceResource>() {
            
            private final Iterator<ResourceName> moduleResourceNameIterator = moduleResourceNameList.iterator();
            
            public boolean hasNext() {
                return moduleResourceNameIterator.hasNext();
            }
            
            public WorkspaceResource next() {
                final ResourceName resourceName = moduleResourceNameIterator.next();
                final ResourceIdentifier resourceIdentifier = new ResourceIdentifier(moduleResourceStore.getResourceType(), resourceName);
                
                return new WorkspaceResource(resourceIdentifier) {
                    
                    @Override
                    public InputStream getInputStream(Status status) {
                        return moduleResourceStore.getInputStream(resourceName);
                    }
                    @Override
                    public long getTimeStamp() {
                        return moduleResourceStore.getTimeStamp(resourceName);
                    }
                    @Override
                    public String getDebugInfo() {
                        return moduleResourceStore.getDebugInfo(resourceName);
                    }
                };
            }
            
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
        
        return resourceIterator;
    }

    /**
     * Converts a List of ResourceName objects (representing module resources) into a Set of ModuleNames.
     * @param listOfModuleResourceNames the ResourceName objects to be converted.
     * @return the corresponding Set of ModuleNames.
     */
    public static final Set<ModuleName> convertListOfResourceNamesToSetOfModuleNames(List<ResourceName> listOfModuleResourceNames) {
        Set<ModuleName> set = new HashSet<ModuleName>();
        for (int i = 0, n = listOfModuleResourceNames.size(); i < n; i++) {
            ResourceName moduleResourceName = listOfModuleResourceNames.get(i);
            set.add(((CALFeatureName)moduleResourceName.getFeatureName()).toModuleName());
        }
        return set;
    }
    
    /**
     * Converts a Collection of module name strings into a Set of ModuleNames.
     * @param moduleNameStrings the Collection of module name strings to be converted.
     * @return the corresponding Set of ModuleNames.
     */
    public static final Set<ModuleName> convertCollectionOfModuleNameStringsToSetOfModuleNames(Collection<String> moduleNameStrings) {
        Set<ModuleName> set = new HashSet<ModuleName>();
        for (final String moduleNameString : moduleNameStrings) {
            set.add(ModuleName.make(moduleNameString));
        }
        return set;
    }
}

