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
 * Program.java
 * Creation date: (March 17 2000 10:34:54 AM)
 * By: LWE
 */
package org.openquark.cal.machine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.runtime.ExecutionContext;


/**
 * Warning- this class should only be used by the CAL runtime implementation. It is not part of the
 * external API of the CAL platform.
 * <P>
 * A Program is a collection of one or more modules which have been loaded to 
 * define an executable program.
 * 
 * @author LWE, Raymond Cypher
 */
public abstract class Program {
   
    private final Map<ModuleName, Module>  moduleNameToModuleMap;

    /** Mapping from module name to timestamp of last successful compilation */
    private Map<ModuleName, Long> compiledModuleTimestamps = new HashMap<ModuleName, Long>();

    /** Whether or not to use the CAL optimizer when compiling the program */    
    private boolean useOptimizer = true;
    
    //---------------------------------------------------------------------------------
    // NOTE: if additional data structures are added to the Program, make sure that
    // that they are cleaned up appropriately when a module is removed from the Program
    //---------------------------------------------------------------------------------
    
    //private Object machineSpecificInfo;
            
    /**
     * Default constructor
     */
    public Program () {
        moduleNameToModuleMap = new HashMap<ModuleName, Module>();
    }

    /**
     * Add a module to this program.
     * Normally called by a Loader.
     * Derived Program classes create a type-specific module object and then call addModule (Module)
     * @param moduleName
     * @param foreignClassLoader the classloader to use to resolve foreign classes for the module.
     * @return the newly created Module
     */
    public abstract Module addModule(ModuleName moduleName, ClassLoader foreignClassLoader);
    
    /**
     * Add the given module to this program.
     * @param module
     */
    synchronized public void addModule (Module module) {
        moduleNameToModuleMap.put(module.getName(), module);
    }

    /**
     * @param name - the QualifiedName of the function to retrieve information about
     * @return - the MachineFunction object associated with the named function, may be null
     * @throws ProgramException
     */
    synchronized public final MachineFunction getCodeLabel (QualifiedName name) throws ProgramException {
        // Get the module object matching the module name
        Module thisModule = moduleNameToModuleMap.get(name.getModuleName());
        if (thisModule == null) {
            // No module
            throw new Program.ProgramException("Unable to resolve module in label: " + name);
        }
        
        MachineFunction cl = thisModule.getFunction(name.getUnqualifiedName());
       
        return cl; 
    }

    /**
     * Return an array of module names for the modules in this program.
     * @return an array of module names for the modules in this program.
     */
    synchronized public final ModuleName[] getModuleNames() {
        return moduleNameToModuleMap.keySet().toArray(new ModuleName[0]);
    }

    /**
     * Return a <b>copy</b> of the modules in this program..
     * 
     * @return a copy of the modules in this program in a new Collection object.
     */
    synchronized public final List<Module> getModules () {
        /*
         * THREAD-SAFETY ISSUE:
         * this needs to be a copy, and not an unmodifiable view based on the underlying
         * collection (e.g. via Collections.unmodifiableCollection()), because iterating through
         * such a list is incompatible with concurrent modification (it would result in a
         * java.util.ConcurrentModificationException). Such a scenario will occur
         * with compilations taking place simultaneously on multiple threads.
         */
        return new ArrayList<Module>(moduleNameToModuleMap.values());
    }

    /**
     * Sets whether or not the CAL based optimizer will be use during compilation.
     * 
     * @param useOptimizer True if the CAL based optimized should be used during compilation.
     */
    
    public void useOptimizer(boolean useOptimizer){
        this.useOptimizer = useOptimizer;        
    }
    
    /**
     * Use this to determine if the CAL based optimizer should be run during compilation.
     *  
     * @return True iff the CAL based optimizer should be run during compilation.
     */
    
    public boolean useOptimizer(){
        return useOptimizer;
    }
    

    /**
     * Gets the module with a given moduleName.
     * @return the module with a given module name
     * @param moduleName the module name
     */
    synchronized public final Module getModule(ModuleName moduleName) {

        // Get the module object with the given the module name
        return moduleNameToModuleMap.get(moduleName);
    }

    /**
     * Remove a named module from this program.
     * @param moduleName the module to remove
     * @param includeDependents
     * @return the Set of modules which were removed
     */
    synchronized public final Set<Module> removeModule(final ModuleName moduleName, final boolean includeDependents) {
        
        Set<Module> removedModules = new HashSet<Module>();
                
        {
            Module module = moduleNameToModuleMap.remove(moduleName);        
            compiledModuleTimestamps.remove(moduleName);        
            if (module != null) {
                removedModules.add(module);
            }
        }
                       
        if (includeDependents) {
            
            Set<ModuleName> dependents = getDependentModules(moduleName);            
            for (final ModuleName dependentName : dependents) {
               
                Module dependentModule = moduleNameToModuleMap.remove(dependentName);
                compiledModuleTimestamps.remove(dependentName);
                if (dependentModule != null) {
                    removedModules.add(dependentModule);
                }
            }
        }
        
        return removedModules;
    }

    /**
     * Generate a set of all modules that depend on the named module
     * either directly or indirectly.
     * @param dependeeModuleName The name of the dependee module.
     * @return Set (of String) A set of names of all dependent modules.
     */
    synchronized public final Set<ModuleName> getDependentModules(ModuleName dependeeModuleName) {
        Set<ModuleName> v = new HashSet<ModuleName>();
        v.add (dependeeModuleName);
        return getDependentModules(v);
    }
    
    /**
     * Generate a list of all modules that depend on the named modules
     * either directly or indirectly.
     * @param changedModules  A Set of names (ModuleName) of modules.
     * @return A Set of names (ModuleName) of all dependent modules.
     */
    synchronized public final Set<ModuleName> getDependentModules(Set<ModuleName> changedModules) {
        if (changedModules.isEmpty()) {
            return Collections.<ModuleName>emptySet();
        }
        
        Set<ModuleName> v = new HashSet<ModuleName>();
        for (final ModuleName moduleName : changedModules) {
           
            for (final Module m : moduleNameToModuleMap.values()) {
                
                if (m.dependsOn(moduleName) && !v.contains(m.getName()) && !changedModules.contains (m.getName())) {
                    v.add (m.getName());
                }
            }
        }
        
        if (v.isEmpty ()) {
            return v;
        }
        
        Set<ModuleName> l = getDependentModules(v);
        for (final ModuleName s : l) {            
            if (!v.contains (s)) {
                v.add (s);
            }
        }
        
        return v;
    }
    
    /**
     * Put an entry into the mapping from module source to the timestamp of last
     * successful compilation.
     * 
     * @param moduleName
     *            the module name
     * @param timestamp
     *            the timestamp of the last successful compilation of the module. Cannot be null.
     */
    synchronized public final void putCompilationTimestamp(ModuleName moduleName, Long timestamp) {
        if (timestamp == null) {
            throw new IllegalArgumentException("timestamp cannot be null");
        }
        compiledModuleTimestamps.put(moduleName, timestamp);
    }
    
    /**
     * Remove the last successful compilation timestamp associated with a module
     * source.
     * 
     * @param moduleName
     *            the module name
     */
    synchronized public final void removeCompilationTimestamp(ModuleName moduleName) {
        compiledModuleTimestamps.remove(moduleName);
    }
    
    /**
     * Retrieve the last successful compilation timestamp associated with a
     * module source.
     * 
     * @param moduleName
     *            the module name
     * @return the timestamp of the last successful compilation of the module,
     *         or null if the module has not been compiled previously.
     */
    synchronized public final Long getCompilationTimestamp(ModuleName moduleName) {
        return compiledModuleTimestamps.get(moduleName);
    }

    // Abstract methods to be overridden in machine specific derived classes.
    /**
     * Discarded any cached (memoized) results in this program.
     * @param context the context with which the cached results are associated.
     */
    public abstract void resetCachedResults(ExecutionContext context);
    
    /**
     * Discard any cached (memoized) results in the named module and any dependent modules.
     * @param moduleName
     * @param context the context with which the cached results are associated.
     */
    public abstract void resetCachedResults(ModuleName moduleName, ExecutionContext context);
    
    /**
     * Discarded the machine state, including any cached (memoized) results in this program.
     * @param context the context with which the machine state associated.
     */
    public abstract void resetMachineState(ExecutionContext context);
    
    /**
     * Discard the machine state, including any cached (memoized) results in the named module and any dependent modules.
     * @param moduleName
     * @param context the context with which the machine state associated.
     */
    public abstract void resetMachineState(ModuleName moduleName, ExecutionContext context);
    
    /**
     * Program exception
     */
    public static final class ProgramException extends Exception {
       
        private static final long serialVersionUID = 3533290083808452134L;

        /**
         * Construct from message
         */
        ProgramException(String message) {
            super(message);
        }
    }
}
