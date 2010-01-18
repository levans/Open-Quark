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
 * GemDesignManager.java
 * Creation date: Jul 23, 2004.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.util.xml.XMLPersistenceHelper;
import org.openquark.util.xml.XMLPersistenceHelper.DocumentConstructionException;
import org.w3c.dom.Document;



/**
 * This class implements the manager responsible for saving and loading
 * GemDesign objects to a GemDesignStore using XML files.
 * 
 * @author Edward Lam
 */
public class GemDesignManager extends ModuleResourceManager {

    /** The store for gem designs for this manager. */
    private final GemDesignStore gemDesignStore;
    
    /** Cache of design-existence data */
    private final GemDesignExistenceCache gemDesignExistenceCache = new GemDesignExistenceCache();
    
    /**
     * Constructor for a GemDesignManager.
     * @param gemDesignStore the store for designs managed by this manager.
     */
    GemDesignManager(GemDesignStore gemDesignStore) {
        this.gemDesignStore = gemDesignStore;
    }
    
    /**
     * @param functionName the name of a function.
     * @return a CALFeatureName for the given function.
     */
    private static CALFeatureName getFeatureName(QualifiedName functionName) {
        return CALFeatureName.getFunctionFeatureName(functionName);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getResourceType() {
        return WorkspaceResource.GEM_DESIGN_RESOURCE_TYPE;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ResourceStore getResourceStore() {
        return gemDesignStore;
    }

    /**
     * @param gemEntity the entity to check if a design exists
     * @return true if there is a design with the given name
     */
    boolean hasGemDesign(GemEntity gemEntity) {
        
        synchronized (gemDesignExistenceCache) {
            
            CALFeatureName featureName = getFeatureName(gemEntity.getName());
            ResourceName resourceName = new ResourceName(featureName);
            
            // Check the cache first
            GemDesignExistenceCacheEntry entry = gemDesignExistenceCache.get(featureName);
            if (entry != null) {
                return entry.hasDesign();
            }
            
            // If the cache has no hits, then calculate a result and add an entry
            boolean result = gemDesignStore.hasFeature(resourceName);
            gemDesignExistenceCache.put(featureName, new GemDesignExistenceCacheEntry(result));
            return result;
        }
    }

    /**
     * Loads the known gem design with the given name.
     * @param designName the name of the design to load
     * @param loadStatus a Status object for storing the status of the load operation
     */
    GemDesign getGemDesign(QualifiedName designName, Status loadStatus) {
        InputStream inputStream = gemDesignStore.getInputStream(new ResourceName(getFeatureName(designName)));
        if (inputStream == null) {
            return null;
        }
        
        return GemDesignManager.loadGemDesign(designName, inputStream, loadStatus);
    }

    /**
     * Save the design view of the gem.
     * @param gemDesign the design to save
     * @param saveStatus the tracking status object
     * @return whether the design was successfully saved.
     */
    boolean saveGemDesign(GemDesign gemDesign, Status saveStatus) {
        
        // Create an output stream for the metadata.
        OutputStream gemDesignOutputStream = gemDesignStore.getOutputStream(new ResourceName(getFeatureName(gemDesign.getDesignName())), saveStatus);
        if (gemDesignOutputStream == null) {
            return false;
        }
        
        // Save the design
        saveGemDesign(gemDesign, gemDesignOutputStream);
        
        // Make sure we close any open stream.
        try {
            gemDesignOutputStream.flush();
            gemDesignOutputStream.close();
        } catch (IOException e) {
        }
        
        return true;
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public void removeModuleResources(ModuleName moduleName, Status removeStatus) {
        synchronized (gemDesignExistenceCache) {
            super.removeModuleResources(moduleName, removeStatus);
            gemDesignExistenceCache.removeModule(moduleName);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAllResources(Status removeStatus) {
        synchronized (gemDesignExistenceCache) {
            super.removeAllResources(removeStatus);
            gemDesignExistenceCache.clear();
        }
    }
    
    /**
     * Loads a gem design from the given file.
     * @param gemName the name of the gem described by the design.
     * @param inputStream the stream from which to read the design.
     * @param loadStatus a Status object for storing the status of the load operation
     */
    static GemDesign loadGemDesign(QualifiedName gemName, InputStream inputStream, Status loadStatus) {
    
        InputStream bufferedInputStream = new BufferedInputStream(inputStream);
        try {
    
            Document designDocument = XMLPersistenceHelper.documentFromXML(bufferedInputStream);
    
            return new GemDesign(gemName, designDocument);
            
        } catch (DocumentConstructionException ex) {
            loadStatus.add(new Status(Status.Severity.ERROR, "The design file XML is invalid.", ex));
            
        } catch (Exception ex) {
            loadStatus.add(new Status(Status.Severity.ERROR, "Exception while loading design.", ex));
        }
        
        return null;
    }

    /**
     * Save the design view of the gem.
     * @param gemDesign the design to save.
     * @param outputStream the stream to which to write the persisted form of the design.
     */
    static void saveGemDesign(GemDesign gemDesign, OutputStream outputStream) {
    
        OutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
    
        // Write out the design view
        XMLPersistenceHelper.documentToXML(gemDesign.getDesignDocument(), bufferedOutputStream, false);
    }

}

/**
 * This class encapsulates an entry in the GemDesignExistenceCache.
 * 
 * Creation date: (May 9, 2005)
 * @author Jawright
 */
class GemDesignExistenceCacheEntry {
    
    /** Whether the gem represented by this entry has a corresponding gem design */
    private final boolean hasDesign;
    
    /** Constructor for GemDesignCacheEntry
     * @param hasDesign Whether the gem represented by this entry has a design
     */
    GemDesignExistenceCacheEntry(boolean hasDesign) {
        this.hasDesign = hasDesign;
    }

    /**
     * Return true if this gem has a design, false otherwise
     * @return boolean
     */
    boolean hasDesign() {
        return hasDesign;
    }
}

/**
 * This class implements a cache of the knowledge of whether or not a given gem
 * has an associated design.
 * 
 * Creation date: (May 9, 2005)
 * @author Jawright
 */
class GemDesignExistenceCache {
    
    /** Cache of whether a gem has a design */
    private final Map<CALFeatureName, GemDesignExistenceCacheEntry> existenceCache = new HashMap<CALFeatureName, GemDesignExistenceCacheEntry>();
    
    /**
     * Return the cache entry (if any) that corresponds to the specified gem
     * @param featureName
     * @return GemDesignExistenceCacheEntry
     */
    GemDesignExistenceCacheEntry get(CALFeatureName featureName) {
        return existenceCache.get(featureName);
    }
    
    /**
     * Add a new entry to the existence cache
     * @param featureName Name of the gem that this entry corresponds to
     * @param entry Entry to add to the cache
     */
    void put(CALFeatureName featureName, GemDesignExistenceCacheEntry entry) {
        existenceCache.put(featureName, entry);
    }
    
    /**
     * Remove all of the cache entries that are associated with the specified module
     * @param moduleName
     */
    void removeModule(ModuleName moduleName) {
        for (Iterator<Map.Entry<CALFeatureName, GemDesignExistenceCacheEntry>> it = existenceCache.entrySet().iterator(); it.hasNext();) {
            Map.Entry<CALFeatureName, GemDesignExistenceCacheEntry> mapEntry = it.next();
            CALFeatureName key = mapEntry.getKey();
            if (key.hasModuleName() && key.toModuleName().equals(moduleName)) {
                it.remove();
            }
        }
    }
    
    /**
     * Remove all entries from the cache
     */
    void clear() {
        existenceCache.clear();
    }
}