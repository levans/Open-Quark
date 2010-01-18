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
 * Created: Feb 23, 2007
 * By: Robin Salkeld
 * --!>
 */
package org.openquark.cal.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.openquark.cal.compiler.CompilerMessage;
import org.openquark.cal.compiler.CompilerMessageLogger;
import org.openquark.cal.compiler.MessageLogger;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.io.EntryPoint;
import org.openquark.cal.compiler.io.EntryPointSpec;
import org.openquark.cal.compiler.io.InputPolicy;
import org.openquark.cal.compiler.io.OutputPolicy;
import org.openquark.cal.machine.CALExecutor;
import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.runtime.ExecutionContext;
import org.openquark.util.General;


/**
 * This is a helper class that manages a cache of {@link EntryPoint}s for a
 * particular workspace. It provides runFunction() methods that accept an
 * {@link EntryPointSpec} along with a set of arguments and execute the
 * application function of the specified function to those arguments, using the
 * {@link InputPolicy InputPolicies} and {@link OutputPolicy OutputPolicies}
 * specified. This is implemented by dynamically obtaining and recreating
 * {@code EntryPoints} as they are needed and holding them in a cache for future
 * function calls to use. This will often be more efficient than
 * BasicCalServices.runFunction() since repeated calls to the same functions
 * with different arguments is a common usage pattern.
 * 
 * @author Robin Salkeld, Magnus Byne
 */
final class EntryPointCache {
    
    /** The program model manager this class caches entry points for. */
    final ProgramModelManager programModelManager;
        
    /** this is the size of the module cache - it limits the number of entry
     * points that will be cached for any one module.
     */
    private final int numEntryPointsToCachePerModule;
    
    /**
     * This is used to map from ModuleName to a module caches.
     * A LRU cache is used, so that we only cache entry points in a fixed
     * number of modules. Once this number is exceeded, all the entry points for
     * the first module referenced are removed.
     */
    final private LRUCache<ModuleName, ModuleCache> cache;
    
    /**
     * this lock is used to protect the cache
     */
    final private ReadWriteLock cacheLock = new ReentrantReadWriteLock();

    /**
     * this is used for the caching  - 
     * the least recently added items are removed from the cache
     * when the max size is exceeded
     *
     * @author Magnus Byne
     */
    private static class LRUCache<K,V> extends java.util.LinkedHashMap<K,V> {
        private static final long serialVersionUID = -2300095914974540766L;

        /** this is the maximum number of items the map will hold*/
        protected int cacheSize;
        
        /**
         * Create the cache
         * @param cacheSize the number of items to store
         */
        LRUCache(int cacheSize) {
            this.cacheSize = cacheSize;
        }
        
        @Override
        protected boolean removeEldestEntry(Map.Entry<K,V> eldest) { return size() >= cacheSize; }
    }
    
 
    /**
     * This class is used to cache entry points for a single module
     * @author mbyne
     */
    private final class ModuleCache {
        /** the cache of entrypointspec -> entrypoint*/
        final private LRUCache<EntryPointSpec, EntryPoint> cache;
        
        /** the name of the module that entry points are cached for*/
        final private ModuleName moduleName;
        
        /**lock used to gate access to the cache*/
        final private ReadWriteLock cacheLock = new ReentrantReadWriteLock();
        
        ModuleCache(ModuleName name, int size) {
            moduleName = name;
            cache = new LRUCache<EntryPointSpec, EntryPoint>(size);
        }
        
        /**
         * runs a function. If the entryPointSpec is not already in the cache it is
         * first added.
         * @param entryPointSpec describes the function to run
         * @param arguments the arguments for running the function
         * @param executionContext the execution context to use
         * @return the result of running the function
         * @throws CALExecutorException
         * @throws GemCompilationException
         */
        Object runFunction(EntryPointSpec entryPointSpec, Object[] arguments, ExecutionContext executionContext) throws CALExecutorException, GemCompilationException {            
            cacheLock.readLock().lock();
            try {
                EntryPoint ep = cache.get(entryPointSpec);
                if (ep == null) {
                    addEntryPoints(Collections.singletonList(entryPointSpec), true);
                    ep = cache.get(entryPointSpec);
                }       
                CALExecutor executor = programModelManager.makeExecutor(executionContext);

                return executor.exec(ep, arguments);

            } finally {
                cacheLock.readLock().unlock();
            }
        }
        
        /** 
         * Add a list of entrypointspecs to the module cache
         * assumes that a read lock is already held on the cache
         * @param entryPointSpecs
         */
        void addEntryPoints(Collection<EntryPointSpec> entryPointSpecs) throws GemCompilationException {
            addEntryPoints(entryPointSpecs, false);
        }
        
        /** 
         * Add a list of entrypointspecs to the module cache
         * assumes that a read lock is already held on the cache
         * @param entryPointSpecs
         * @param haveRead - this param indicates if we already have a read lock on the cache
         */
        private void addEntryPoints(Collection<EntryPointSpec> entryPointSpecs, boolean haveRead) throws GemCompilationException {
            //upgrade to a write lock
            if (haveRead) {
                cacheLock.readLock().unlock();
            }
            cacheLock.writeLock().lock();
            try {

                HashSet<EntryPointSpec> required = new HashSet<EntryPointSpec>();

                required.addAll(cache.keySet());
                required.addAll(entryPointSpecs);

                CompilerMessageLogger compilerLogger = new MessageLogger();
                       
                List<EntryPointSpec> requiredList = new ArrayList<EntryPointSpec>(required);
                //compile the entry points
                List<EntryPoint> entryPoints 
                = programModelManager.getCompiler().getEntryPoints(requiredList, moduleName, compilerLogger);

                if (compilerLogger.getMaxSeverity().compareTo(CompilerMessage.Severity.ERROR) >= 0) {
                    StringBuffer sb = new StringBuffer();
                    sb.append("Failed to generate entry points for module '");
                    sb.append(moduleName);
                    sb.append(General.SYSTEM_EOL);
                    for(CompilerMessage msg : compilerLogger.getCompilerMessages()) {
                        sb.append(msg);
                        sb.append(General.SYSTEM_EOL);
                    }

                    //all of the cached entry points are invalid now.
                    cache.clear();
                    throw new GemCompilationException(sb.toString());
                }

                //update the cache with the new entry points
                // entry point spec
                for (int i = 0, n = entryPoints.size(); i < n; i++) {
                    EntryPointSpec entryPointSpec = requiredList.get(i);
                    EntryPoint entryPoint = entryPoints.get(i);
                    if (entryPoint != null) {
                        cache.put(entryPointSpec, entryPoint);
                    } else {
                        cache.remove(entryPointSpec);
                    }
                }
            } finally {
                if (haveRead) {
                    cacheLock.readLock().lock();
                }
                cacheLock.writeLock().unlock();
            }
        }
    }
    
    Object runFunction(EntryPointSpec entryPointSpec, Object[] arguments, ExecutionContext executionContext) throws CALExecutorException, GemCompilationException {
        cacheLock.readLock().lock();
        try {
            ModuleName moduleName = entryPointSpec.getFunctionalAgentName().getModuleName();
            ModuleCache moduleCache = getModuleCache(moduleName);
            
            return moduleCache.runFunction(entryPointSpec, arguments, executionContext);
        } finally {
            cacheLock.readLock().unlock(); 
        }
    }
   
    /**
     * gets the module cache for the specified module. It assumes there is already
     * a read lock on the module cache. If there is no entry for the module
     * it is created.
     * @param moduleName
     * @return the module cache
     */
    private ModuleCache getModuleCache(ModuleName moduleName)
    {
        ModuleCache moduleCache = cache.get(moduleName);

        if (moduleCache != null) {
            return moduleCache;
        }
        
        cacheLock.readLock().unlock();
        cacheLock.writeLock().lock();
        try {
            //make sure it hasn't been inserted by someone while the lock was being upgraded
            moduleCache = cache.get(moduleName);
            if (moduleCache != null) {
                return moduleCache;
            }
            
            moduleCache = new ModuleCache(moduleName, numEntryPointsToCachePerModule);
            cache.put(moduleName, moduleCache);
            
            return moduleCache;
        } finally {
            cacheLock.readLock().lock();
            cacheLock.writeLock().unlock();
        }
    }
    /**
     * Pre-caches entry points - can be used to cut down initial time taken to run functions.
     * @param entryPointSpecs
     * @throws GemCompilationException
     */
    void cacheEntryPoints(Collection<EntryPointSpec> entryPointSpecs) throws GemCompilationException {

        //split the entry points into sets of each module  
        HashMap<ModuleName, HashSet<EntryPointSpec> > specs = new HashMap<ModuleName, HashSet<EntryPointSpec> >();
        
        for(EntryPointSpec spec : entryPointSpecs) {
            ModuleName module = spec.getFunctionalAgentName().getModuleName();
            Set<EntryPointSpec> moduleSet = specs.get(module);
            
            if (moduleSet == null) {
                HashSet<EntryPointSpec> set = new HashSet<EntryPointSpec>();
                set.add(spec);
                specs.put(module, set);
            } else {
                moduleSet.add(spec);
            }
        }

        //update each of the module caches
        cacheLock.readLock().lock();
        try {
            for(ModuleName moduleName : specs.keySet()) {
                ModuleCache moduleCache = getModuleCache(moduleName);
                moduleCache.addEntryPoints(specs.get(moduleName));
            }
        } finally {
            cacheLock.readLock().unlock(); 
        }       
        
    }
    
 
    /**
     * EntryPointCache constructor.
     * @param programModelManager
     */
    EntryPointCache(ProgramModelManager programModelManager, int numEntryPointsToCachePerModule, int numModulesToCache) {
        this.programModelManager = programModelManager;
        this.numEntryPointsToCachePerModule = numEntryPointsToCachePerModule;
        cache = new LRUCache<ModuleName, ModuleCache>(numModulesToCache);
    }
    
    
    /** lock the module cache */
    void lockCache() {
        cacheLock.writeLock().lock();
    }

    /** unlock the module cache */
    void unlockCache() {
        cacheLock.writeLock().unlock();
    }
    
    /**
     * remove everything from the cache 
     * must do this whenever any modules are recompiled, as they could affect any other module.
     * Must have acquired the module lock first.
     */
    void flush() {
        cache.clear();
    }
}
