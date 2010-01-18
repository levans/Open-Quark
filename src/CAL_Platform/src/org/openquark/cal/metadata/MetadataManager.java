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
 * MetadataManager.java
 * Creation date: Jul 9, 2003
 * By: Frank Worsley
 */
package org.openquark.cal.metadata;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openquark.cal.compiler.ClassInstance;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.ScopedEntity;
import org.openquark.cal.module.Cal.Collections.CAL_List;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.services.CALFeatureName;
import org.openquark.cal.services.LocaleUtilities;
import org.openquark.cal.services.LocalizedResourceName;
import org.openquark.cal.services.MetaModule;
import org.openquark.cal.services.ModuleResourceManager;
import org.openquark.cal.services.ResourceName;
import org.openquark.cal.services.ResourceStore;
import org.openquark.cal.services.Status;
import org.openquark.cal.services.WorkspaceResource;
import org.openquark.cal.services.FeatureName.FeatureType;


/**
 * This class implements the metadata manager responsible for saving and 
 * loading CALFeatureMetadata objects to a MetadataStore using XML files.
 * 
 * @author Frank Worsley
 */
public class MetadataManager extends ModuleResourceManager {

    /** The namespace for log messages from this package. */
    public static final String METADATA_LOGGER_NAMESPACE = "org.openquark.cal.metadata";
    
    /** An instance of a Logger for log messages from this package. */
    static final Logger METADATA_LOGGER = Logger.getLogger(METADATA_LOGGER_NAMESPACE);
    
    /** The metadata cache for already loaded metadata. */
    private final MetadataCache metadataCache = new MetadataCache();
    
    /** 
     * The lock for access to the metadata cache.
     * Using a zero-length byte[] instead of a new Object() is a small optimization.
     */
    private final byte[] cacheLock = new byte[0];
    
    /** The store for metadata for this manager. */
    private final MetadataStore metadataStore;
    
    /**
     * Constructor for a metadata manager.
     * @param metadataStore the store for metadata managed by this manager.
     */
    public MetadataManager(MetadataStore metadataStore) {
        this.metadataStore = metadataStore;
    }
    
    /**
     * @param entity the entity to check for
     * @param locale the locale to check.
     * @return true if metadata exists for the given entity, false otherwise
     */
    public boolean hasMetadata(ScopedEntity entity, Locale locale) {
        return hasMetadata(CALFeatureName.getScopedEntityFeatureName(entity), locale);
    }
    
    /**
     * @param module the module to check for
     * @param locale the locale to check.
     * @return true if metadata exists for the given module, false otherwise
     */    
    public boolean hasMetadata(MetaModule module, Locale locale) {
        return hasMetadata(CALFeatureName.getModuleFeatureName(module), locale);
    }
    
    /**
     * @param instance the instance to check for
     * @param locale the locale to check.
     * @return true if metadata exists for the given instance, false otherwise
     */
    public boolean hasMetadata(ClassInstance instance, Locale locale) {
        return hasMetadata(CALFeatureName.getClassInstanceFeatureName(instance), locale);
    }
    
    /**
     * @param featureName the feature to check
     * @param locale the locale to check.
     * @return whether metadata exists for the given feature.
     */
    public boolean hasMetadata(CALFeatureName featureName, Locale locale) {
        return metadataStore.hasFeature(new LocalizedResourceName(featureName, locale));
    }
    
    /**
     * @param entity the entity to get metadata for
     * @param locale the locale to check.
     * @return the metadata for the entity. If the entity has no metadata, then default metadata is returned.
     */
    public ScopedEntityMetadata getMetadata(ScopedEntity entity, Locale locale) {
        return (ScopedEntityMetadata) getMetadata(CALFeatureName.getScopedEntityFeatureName(entity), locale);
    }
    
    /**
     * @param module the Module to get metadata for
     * @param locale the locale to check.
     * @return the metadata for the module. If the module has no metadata, then default metadata is returned. 
     */
    public ModuleMetadata getMetadata(MetaModule module, Locale locale) {
        return (ModuleMetadata) getMetadata(CALFeatureName.getModuleFeatureName(module), locale);
    }
    
    /**
     * @param instance the ClassInstance to get metadata for
     * @param locale the locale to check.
     * @return the metadata for the class instance. If the instance has no metadata, then default metadata is returned.
     */
    public ClassInstanceMetadata getMetadata(ClassInstance instance, Locale locale) {
        return (ClassInstanceMetadata) getMetadata(CALFeatureName.getClassInstanceFeatureName(instance), locale);
    }
    
    /**
     * Fetches the metadata for the given feature name and the given locale.
     * 
     * @param featureName the name of the feature to get metadata for
     * @param locale the locale to check.
     * @return the metadata for the feature. If the feature has no metadata, then default
     * metadata is returned.
     */
    public CALFeatureMetadata getMetadata(CALFeatureName featureName, Locale locale) {
        
        LocalizedResourceName resourceName = new LocalizedResourceName(featureName, locale);
        InputStream metadataInputStream;
        Status loadStatus = null;
        CALFeatureMetadata metadata;
        
        // Keep track of the originally requested locale
        Locale originalLocale = locale;
        
        synchronized (cacheLock) {
            
            // Check if we already have this cached
            MetadataCacheEntry cacheEntry = metadataCache.get(resourceName);
            if (cacheEntry != null) {
                return cacheEntry.getMetadata();
            }
            
            // Get the input stream for the metadata, and use this to load.
            metadataInputStream = metadataStore.getInputStream(resourceName);
            
            // go through the fallback mechanism if necessary
            
            // variant-specific locale not found, fallback to country-specific locale
            if (metadataInputStream == null && locale.getVariant().length() > 0) {
                locale = new Locale(locale.getLanguage(), locale.getCountry());
                resourceName = new LocalizedResourceName(featureName, locale);
                metadataInputStream = metadataStore.getInputStream(resourceName);
            }
            
            // country-specific locale not found, fallback to language-specific locale
            if (metadataInputStream == null && locale.getCountry().length() > 0) {
                locale = new Locale(locale.getLanguage());
                resourceName = new LocalizedResourceName(featureName, locale);
                metadataInputStream = metadataStore.getInputStream(resourceName);
            }
            
            // language-specific locale not found, fallback to neutral locale
            if (metadataInputStream == null) {
                locale = LocaleUtilities.INVARIANT_LOCALE;
                resourceName = new LocalizedResourceName(featureName, locale);
                metadataInputStream = metadataStore.getInputStream(resourceName);
            }
            
            if (metadataInputStream == null) {
                metadata = getEmptyMetadata(featureName, originalLocale);

            } else {
                loadStatus = new Status("Load status");
                // note that we pass in the original locale unaffected
                // by the fallback mechanism - this is done on purpose: once the
                // fallback mechanism finds the proper resource to load, its content
                // is treated as though it is the metadata for the originally requested locale.
                metadata = MetadataPersistenceHelper.loadMetadata(featureName, originalLocale, metadataInputStream, loadStatus);
            }
            
            // Add to the cache.
            cacheEntry = new MetadataCacheEntry(metadata);
            metadataCache.put(cacheEntry);
        }
        
        if (loadStatus != null && loadStatus.getSeverity().compareTo(Status.Severity.WARNING) >= 0) {
            METADATA_LOGGER.log(Level.WARNING, "Problems were encountered loading metadata for " + featureName);
            METADATA_LOGGER.log(Level.WARNING, loadStatus.getDebugMessage());
        }
        
        // Make sure we close any open stream.
        if (metadataInputStream != null) {
            try {
                metadataInputStream.close();
            } catch (IOException ioe) {
            }
        }
        
        return metadata;
    }
    
    /**
     * Returns a List of ResourceNames for the metadata resources associated with the given feature name, across all locales.
     * @param featureName the name of the feature whose metadata resources across all locales are to be enumerated.
     * @return a List of ResourceNames, one for each localized metadata resource associated with the given feature name.
     */
    public List<ResourceName> getMetadataResourceNamesForAllLocales(CALFeatureName featureName) {
        return metadataStore.getMetadataResourceNamesForAllLocales(featureName);
    }

    /**
     * Saves the given metadata object to permanent storage.
     * @param metadata the metadata object to save
     * @param saveStatus the tracking status object
     * @return true if metadata was saved, false otherwise
     */
    public boolean saveMetadata(CALFeatureMetadata metadata, Status saveStatus) {
        return saveMetadata(metadata, metadata.getFeatureName(), metadata.getLocale(), saveStatus);
    }
    
    /**
     * Save the given metadata object to permanent storage using the given name. 
     * @param metadata
     * @param metadataName
     * @param locale the locale of the metadata.
     * @param saveStatus
     * @return true if metadata was saved, false otherwise
     */
    boolean saveMetadata(CALFeatureMetadata metadata, CALFeatureName metadataName, Locale locale, Status saveStatus) {
        
        LocalizedResourceName resourceName = new LocalizedResourceName(metadataName, locale);
        
        // Create an output stream for the metadata.
        OutputStream metadataOutputStream = metadataStore.getOutputStream(resourceName, saveStatus);
        if (metadataOutputStream == null) {
            return false;
        }
        
        synchronized (cacheLock) {
            // Save the metadata
            MetadataPersistenceHelper.saveMetadata(metadata, metadataOutputStream);
            
            // Save the metadata in the cache.
            MetadataCacheEntry cacheEntry = new MetadataCacheEntry(metadata);
            metadataCache.put(cacheEntry);
        }
        
        try {
            // Make sure we flush and close any open stream.
            metadataOutputStream.flush();
            metadataOutputStream.close();
        } catch (IOException e) {
            METADATA_LOGGER.log(Level.WARNING, "A problem was encountered while saving metadata.", e);
            // return false; ??
        }

        return true;
    }

    /**
     * @param entity the entity for which to get a metadata object
     * @param locale the locale associated with the empty metadata.
     * @return the correct instance of a CALFeatureMetadata object without any fields initialized
     */
    public static ScopedEntityMetadata getEmptyMetadata(ScopedEntity entity, Locale locale) {
        return (ScopedEntityMetadata)getEmptyMetadata(CALFeatureName.getScopedEntityFeatureName(entity), locale);
    }
    
    /**
     * @param module the module for which to get a metadata object
     * @param locale the locale associated with the empty metadata.
     * @return the correct instance of a CALFeatureMetadata object without any fields initialized
     */
    public static ModuleMetadata getEmptyMetadata(MetaModule module, Locale locale) {
        return (ModuleMetadata) getEmptyMetadata(CALFeatureName.getModuleFeatureName(module), locale);
    }
    
    /**
     * @param instance the class instanceo for which to get a metadata object
     * @param locale the locale associated with the empty metadata.
     * @return the correct instance of a CALFeatureMetadata object without any fields initialized
     */
    public static ClassInstanceMetadata getEmptyMetadata(ClassInstance instance, Locale locale) {
        return (ClassInstanceMetadata) getEmptyMetadata(CALFeatureName.getClassInstanceFeatureName(instance), locale);
    }
    
    /**
     * @param featureName the name of the feature to get empty metadata for
     * @param locale the locale associated with the empty metadata.
     * @return the correct instance of CALFeatureMetadata without any fields initialized
     */
    public static CALFeatureMetadata getEmptyMetadata(CALFeatureName featureName, Locale locale) {
        
        FeatureType type = featureName.getType();
        
        if (type == CALFeatureName.FUNCTION) {
            return new FunctionMetadata(featureName, locale);
            
        } else if (type == CALFeatureName.CLASS_METHOD) {
            return new ClassMethodMetadata(featureName, locale);
            
        } else if (type == CALFeatureName.DATA_CONSTRUCTOR) {
            return new DataConstructorMetadata(featureName, locale);
            
        } else if (type == CALFeatureName.TYPE_CLASS) {
            return new TypeClassMetadata(featureName, locale);
            
        } else if (type == CALFeatureName.TYPE_CONSTRUCTOR) {
            return new TypeConstructorMetadata(featureName, locale);
            
        } else if (type == CALFeatureName.MODULE) {
            return new ModuleMetadata(featureName, locale);
        
        } else if (type == CALFeatureName.CLASS_INSTANCE) {
            return new ClassInstanceMetadata(featureName, locale);
            
        } else if (type == CALFeatureName.INSTANCE_METHOD) {
            return new InstanceMethodMetadata(featureName, locale);
        }
        
        throw new IllegalArgumentException("feature type not supported: " + type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean saveResource(ResourceName resourceName, InputStream sourceInputStream, Status saveStatus) {
        synchronized (cacheLock) {
            boolean result = super.saveResource(resourceName, sourceInputStream, saveStatus);
            metadataCache.removeResource(resourceName);
            return result;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void removeResource(ResourceName resourceName, Status removeStatus) {
        synchronized (cacheLock) {
            super.removeResource(resourceName, removeStatus);
            metadataCache.removeResource(resourceName);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void removeModuleResources(ModuleName moduleName, Status removeStatus) {
        synchronized (cacheLock) {
            super.removeModuleResources(moduleName, removeStatus);
            metadataCache.removeModule(moduleName);
        }
    }

    /**
     * Remove all metadata from the manager.
     * @param removeStatus the tracking status object.
     */
    @Override
    public void removeAllResources(Status removeStatus) {
        synchronized (cacheLock) {
            super.removeAllResources(removeStatus);
            metadataCache.clear();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResourceType() {
        return WorkspaceResource.METADATA_RESOURCE_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResourceStore getResourceStore() {
        return metadataStore;
    }
}

/**
 * This class implements a cache for already loaded metadata. It stores the
 * metadata in a HashMap using the metadata file as the key.
 * There is a maximum size for the cache and the least recently used data
 * is removed from the cache if it grows past its maximum size.
 * 
 * @author Frank Worsley
 */
class MetadataCache {
        
    /** The maximum number of items to keep in the cache. */
    private static final int MAXIMUM_CACHE_SIZE = 500;
        
    /** 
     * The Map that stores the actual cache entries. 
     * Modified so that the number of entries does not exceed MAXIMUM_CACHE_SIZE.
     **/
    private final Map<LocalizedResourceName, MetadataCacheEntry> dataCache = 
        new LinkedHashMap<LocalizedResourceName, MetadataCacheEntry>(MAXIMUM_CACHE_SIZE / 4, 0.75f, true) {

        private static final long serialVersionUID = 2316021820792831141L;

        @Override
        protected boolean removeEldestEntry(Entry<LocalizedResourceName, MetadataCacheEntry> eldest) {
            return size() > MAXIMUM_CACHE_SIZE;
        }
    };
        
    /**
     * Adds a new entry to the cache.
     * @param cacheEntry the entry to add
     */
    void put(MetadataCacheEntry cacheEntry) {
        dataCache.put (cacheEntry.getKey(), cacheEntry);
    }
        
    /**
     * @param resourceName the resource name for which to retrieve a cache entry
     * @return the MetadataCacheEntry associated with the given File, or null
     * if there is no such cache entry.
     */
    MetadataCacheEntry get(LocalizedResourceName resourceName) {
        return dataCache.get(resourceName);
    }
    
    /**
     * Remove the named cache entry.
     * @param resourceName the name of the metadata resource to be removed.
     */
    public void removeResource(ResourceName resourceName) {
        dataCache.remove(resourceName);
    }

    /**
     * Remove cache entries from the given module.
     * @param moduleName the name of the module.
     */
    void removeModule(ModuleName moduleName) {
        // For now, there is nothing for it but to iterate over all the entries in the cache.
        for (Iterator<Map.Entry<LocalizedResourceName, MetadataCacheEntry>> it = dataCache.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<LocalizedResourceName, MetadataCacheEntry> mapEntry = it.next();

            LocalizedResourceName cacheEntryKey = mapEntry.getKey();
            CALFeatureName cacheEntryFeatureName = (CALFeatureName)cacheEntryKey.getFeatureName();
            if (cacheEntryFeatureName.hasModuleName() && cacheEntryFeatureName.toModuleName().equals(moduleName)) {
                it.remove();
            }
        }
    }
    
    /**
     * Clear the metadata cache.
     */
    public void clear() {
        dataCache.clear();
    }
}
    
/**
 * This class represents an entry in the metadata cache. 
 * It encapsulates the GemMetadata and ArgumentMetadata associated with a cache entry.
 * @author Frank Worsley
 */
class MetadataCacheEntry {

    /** The metadata associated with this cache entry. */
    private final CALFeatureMetadata metadata;
        
    /**
     * Constructor for a new MetadataCacheEntry.
     * @param metadata the metadata associated with this cache entry
     */
    MetadataCacheEntry (CALFeatureMetadata metadata) {
        this.metadata = metadata.copy();
    }

    /**
     * @return the name which acts as the key for this cache entry.
     */
    LocalizedResourceName getKey() {
        if (metadata != null) {
            return metadata.getResourceName();
        }
        
        return null;
    }
    
    /**
     * @return a copy of the cached metadata object
     */
    CALFeatureMetadata getMetadata() {
        if (metadata != null) {
            return metadata.copy();
        }
        
        return null;
    }
}

/**
 * This class tests concurrent access to the metadata cache.
 * To exercise this test, first set the size of the metadata cache to 1 or 2.
 * @author Edward Lam
 */
class ConcurrentMetadataAccessTest {
    
    /**
     * This thread runs in a loop, and in each iteration attempts to fetch metadata for a single feature
     *   which should be known to have metadata.  Concurrent access failure will result in a failure to 
     *   find the metadata, a message being logged, or some other program failure.
     * @author Edward Lam
     */
    private static class MetadataGetThread extends Thread {
        
        private static final int nGets = 50000000;
        
        private final CALFeatureName featureNameToGet;
        private final MetadataManager metadataManager;
        
        /**
         * Constructor for a MetadataManager.MetadataGetThread.
         * @param metadataManager the metadata manager from which to get the metadata.
         * @param featureNameToGet the name of the feature for which to retrieve metadata from the manager.
         */
        public MetadataGetThread(MetadataManager metadataManager, CALFeatureName featureNameToGet) {
            this.metadataManager = metadataManager;
            this.featureNameToGet = featureNameToGet;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            Random random = new Random();
            
            System.err.println("Looking for: " + featureNameToGet);
            for (int i = 0; i < nGets; i++) {
                
                CALFeatureMetadata metadata = metadataManager.getMetadata(featureNameToGet, null);
                if (metadata == null) {
                    System.err.println("Metadata not found for: " + featureNameToGet);
                }
                
                // Sleep a random amount of time from 0 - 4 ms.
                try {
                    sleep(random.nextInt(5));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            System.err.println("Finished looking for: " + featureNameToGet);
        }
    }
    
    /**
     * Test target.  Creates six threads, attempting concurrent access metadata for three functions.
     * @param args
     */
    public static void main(String[] args) {
        MetadataManager metadataManager = new MetadataManager(new MetadataNullaryStore());
        MetadataGetThread thread1 = 
            new MetadataGetThread(metadataManager, CALFeatureName.getFunctionFeatureName(CAL_Prelude.Functions.abs));
        MetadataGetThread thread2 = 
            new MetadataGetThread(metadataManager, CALFeatureName.getFunctionFeatureName(CAL_Prelude.Functions.signum));
        MetadataGetThread thread3 = 
            new MetadataGetThread(metadataManager, CALFeatureName.getFunctionFeatureName(CAL_List.Functions.any));
        MetadataGetThread thread4 = 
            new MetadataGetThread(metadataManager, CALFeatureName.getFunctionFeatureName(CAL_Prelude.Functions.abs));
        MetadataGetThread thread5 = 
            new MetadataGetThread(metadataManager, CALFeatureName.getFunctionFeatureName(CAL_Prelude.Functions.signum));
        MetadataGetThread thread6 = 
            new MetadataGetThread(metadataManager, CALFeatureName.getFunctionFeatureName(CAL_List.Functions.any));
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        thread5.start();
        thread6.start();
        
        try {
            thread1.join();
            thread2.join();
            thread3.join();
            thread4.join();
            thread5.join();
            thread6.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}