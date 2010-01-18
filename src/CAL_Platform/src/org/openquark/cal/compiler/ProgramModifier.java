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
 * ProgramModifier.java
 * Created: Feb 19, 2003 at 12:15:01 PM
 * By: Raymond Cypher
 */
package org.openquark.cal.compiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openquark.cal.compiler.CompilerMessage.Severity;
import org.openquark.cal.machine.CodeGenerator;
import org.openquark.cal.machine.Module;
import org.openquark.cal.machine.Program;
import org.openquark.cal.machine.StatusListener;


/**
 * This is the ProgramModifier class.
 *
 * This class is used to compile modules. 
 * <p>
 * Created: Feb 19, 2003 at 12:15:01 PM
 * @author Raymond Cypher
 */
public abstract class ProgramModifier {

    public static final String IGNORE_CMI_PROP = "org.openquark.cal.machine.lecc.internal.ignore_cmf";
    
    /** This is a temporary flag used for debugging loading of compiled module files.
     *  If set, existing .cmi files will be ignored. */
    private static final boolean IGNORE_COMPILED_MODULE_INFO = System.getProperty(IGNORE_CMI_PROP) != null;
    
    private final CALCompiler compiler;

    private final Program program;

    /** Collection of status listeners */
    private final Set<StatusListener> statusListeners = new HashSet<StatusListener>();
    
    /** The program change listeners. */
    private final Set<ProgramChangeListener> programChangeListeners = new HashSet<ProgramChangeListener>();   

    /** Flag used to indicate that all code should be regenerated. */
    private boolean forceCodeRegen;
    
    /** Flag to indicate that the code being compiled will be executing immediately.
     * this allows for optimizations in generating/loading.
     */
    private boolean forImmediateUse;
    
    /**
     * Flag to ignore compiled module info and re-construct from cal source.
     */
    private boolean ignoreCompiledModuleInfo;
    
    /** Any custom foreign context provider specified by the client. */
    private ForeignContextProvider foreignContextProvider;
    
    /**
     * Constructor for ProgramModifier.
     * @param program
     */
    protected ProgramModifier(Program program) {
        compiler = new CALCompiler();

        if (program == null) {
            throw new NullPointerException("Argument 'program' cannot be null.");
        }
        this.program = program;
        
        // makes sure that the CALCompiler has a copy of the packager with the program
        // from the very beginning
        compiler.setPackager(makePackager(program));
        
        // Add a listener to announce when a module is loaded.
        addStatusListener(new StatusListener.StatusListenerAdapter() {
            @Override
            public void setModuleStatus(StatusListener.Status.Module moduleStatus, ModuleName moduleName) {
                if (moduleStatus == StatusListener.SM_LOADED) {
                    fireModuleLoaded(ProgramModifier.this.program.getModule(moduleName));
                }
            }
        });
    }
    
    abstract protected Packager makePackager(Program program);
    
    abstract protected CodeGenerator makeCodeGenerator();

    /**
     * This function is used to retrieve a compiled module file written out to the same location as the 
     * generated sources associated with a specific machine.
     * @param moduleName
     * @return a CompiledModuleSourceDefinition if the compiled source is avaialable, otherwise null. 
     */
    abstract protected CompiledModuleSourceDefinition getCompiledSourceDefinition (ModuleName moduleName);
    
    
    /**
     * Compile a module to the current program.
     *   Dependent modules will be removed from the program.
     * @param sourceDef the ModuleSourceDefinition to compile.
     * @param logger the logger to use during the compile.
     * @return the highest error condition experienced
     */
    public final Severity compile(final ModuleSourceDefinition sourceDef, CompilerMessageLogger logger) {
        
        if (sourceDef == null) {
            throw new NullPointerException("Source definition must not be null.");
        }
        ModuleName sourceDefName = sourceDef.getModuleName();
        if (sourceDefName == null) {
            throw new NullPointerException("Source definition name must not be null.");
        }
        
        ModuleSourceDefinitionGroup sourceProvider = new ModuleSourceDefinitionGroup(new ModuleSourceDefinition[] {sourceDef});
        ModuleName[] moduleNames = new ModuleName[] {sourceDefName};
        
        // Defer to a more general compile().
        return compile(moduleNames, sourceProvider, logger);
    }

    /**
     * Compile all module(s) in the given source provider.
     * @param definitionGroup the definition group from which the module definitions may be retrieved.
     * @param logger the logger to use during the compile.
     * @param dirtyModulesOnly if True, compiles only those modules modified since last compilation to the current program.
     * If false, compiles all modules in the source provider to a new program.
     * @return the highest error condition experienced
     */
    public final CompilerMessage.Severity compile (ModuleSourceDefinitionGroup definitionGroup, CompilerMessageLogger logger, boolean dirtyModulesOnly) {

        Set<ModuleName> allModuleNames = new HashSet<ModuleName>();
        for (int i = 0, nModules = definitionGroup.getNModules(); i < nModules; i++) {
            ModuleSourceDefinition sourceDefinition = definitionGroup.getModuleSource(i);
            allModuleNames.add (sourceDefinition.getModuleName());
        }
        
        if (!dirtyModulesOnly) {
            return compileAllModules (allModuleNames, definitionGroup, logger);
        }
        
        
        List<ModuleName> dirtyModuleNames = new ArrayList<ModuleName>();
        for (final ModuleName moduleName : allModuleNames) {           
            
            ModuleSourceDefinition sourceDefinition = definitionGroup.getModuleSource(moduleName);

            Long timestamp = program.getCompilationTimestamp(moduleName);
            
            if (timestamp != null) {
                // Module was compiled before; check timestamp
                
                if (timestamp.compareTo(new Long(sourceDefinition.getTimeStamp())) < 0 ) {
                    // Source was modified since last compilation
                    dirtyModuleNames.add(moduleName);
                }
                
            } else {
                // Module was not compiled before; will be compiled
                dirtyModuleNames.add(moduleName);
            }
        }
        
        ModuleName[] dirtyModuleNamesArray = new ModuleName[dirtyModuleNames.size()];
        dirtyModuleNames.toArray(dirtyModuleNamesArray);
        
        return compile(dirtyModuleNamesArray, definitionGroup, logger);
    }

    /**
     * Compile all module sources from the specified source definition group.
     * @param moduleNames
     * @param definitionGroup the module source definitions to compile.
     * @param logger compiler message logger
     * @return highest severity of the logged messages
     */
    private final CompilerMessage.Severity compileAllModules (Set<ModuleName> moduleNames, ModuleSourceDefinitionGroup definitionGroup, CompilerMessageLogger logger) {

        // ensure the program has no modules in it.
        for (final ModuleName moduleName : moduleNames) {            
            removeModuleInternal(moduleName, false);
        }

        Map<ModuleName, ModuleSourceDefinition> allSourceDefinitions = new HashMap<ModuleName, ModuleSourceDefinition>();
        for (int i = 0, nModules = definitionGroup.getNModules(); i < nModules; i++) {
            ModuleSourceDefinition ms = definitionGroup.getModuleSource(i);
            if (ms != null) {
                allSourceDefinitions.put(ms.getModuleName(), ms);
            }
        }

        return compileSimple (moduleNames, allSourceDefinitions, logger);
    }
    
    /**
     * Compile a number of modules in the given source definition group.
     * @param moduleNames the names of the modules to compile..
     * @param definitionGroup the definition group from which the module definitions may be retrieved.
     * @param logger the logger to use during the compile.
     * @return the highest error condition experienced
     */
    public final CompilerMessage.Severity compile (ModuleName[] moduleNames, ModuleSourceDefinitionGroup definitionGroup, CompilerMessageLogger logger) {

        Set<ModuleName> moduleNamesSet = new HashSet<ModuleName>();
        for (int i = 0; i < moduleNames.length; ++i) {
            moduleNamesSet.add(moduleNames[i]);
        }
        Set<ModuleName> dependentModuleNames = program.getDependentModules(moduleNamesSet);
        
        Set<ModuleName> allChangedModules = new HashSet<ModuleName>();
        allChangedModules.addAll (moduleNamesSet);
        allChangedModules.addAll (dependentModuleNames);

        
        Map<ModuleName, ModuleSourceDefinition> sourceDefinitionMap = new HashMap<ModuleName, ModuleSourceDefinition>();
        for (final ModuleName moduleName : allChangedModules) {            
            if (getProgram().getModule(moduleName) != null) {
                removeModuleInternal (moduleName, false);
            }

            ModuleSourceDefinition newModuleSourceDefinition = definitionGroup.getModuleSource(moduleName);

            if (newModuleSourceDefinition != null) {
                sourceDefinitionMap.put (moduleName, newModuleSourceDefinition);
            }
        }
        
        return compileSimple(allChangedModules, sourceDefinitionMap, logger);
    }

    /**
     * Get all compiled module files (valid or invalid) that have previously been written as part of machine source generation.  
     * @param moduleNames - names of all modules being compiled/loaded
     * @param logger
     * @return a Map of module name to compiled module source definition.
     */
    private final Map<ModuleName, CompiledModuleSourceDefinition> getAllCompiledDefinitions (Set<ModuleName> moduleNames, CompilerMessageLogger logger) {
        Map<ModuleName, CompiledModuleSourceDefinition> compiledDefinitionMap = new HashMap<ModuleName, CompiledModuleSourceDefinition>();
        
        if (!isIgnoreCompiledModuleInfo() && !getForceCodeRegen() && !ProgramModifier.IGNORE_COMPILED_MODULE_INFO) {
            
            for (final ModuleName moduleName : moduleNames) {               
                CompiledModuleSourceDefinition cmsd = getCompiledSourceDefinition(moduleName);
                if (cmsd != null) {
                    compiledDefinitionMap.put(moduleName, cmsd);
                }
            }
            
            ModuleName[] moduleNamesFromProgram = getProgram().getModuleNames();
            for (final ModuleName moduleName : moduleNamesFromProgram) {
                CompiledModuleSourceDefinition cmsd = getCompiledSourceDefinition (moduleName);
                if (cmsd != null) {
                    compiledDefinitionMap.put (moduleName, cmsd);
                }
            }
        }
        
        return compiledDefinitionMap;
    }
    
    /**
     * Helper method for the other compile()'s
     *   When this method is called, the program should not contain any modules for which module sources are provided.
     * @param moduleNames
     * @param sourceDefinitionMap (module name to ModuleSourceDefinition) the module source definitions to compile
     * @param logger message logger for the compiler
     * @return the highest error condition experienced.
     */
    private final CompilerMessage.Severity compileSimple (Set<ModuleName> moduleNames, Map<ModuleName, ModuleSourceDefinition> sourceDefinitionMap, CompilerMessageLogger logger) {

        // Before we start compiling/loading we should check to see if there are compiled module files 
        // written out as part of machine source generation.
        Map<ModuleName, CompiledModuleSourceDefinition> compiledDefinitionMap = getAllCompiledDefinitions(moduleNames, logger);
        
        compiler.setCompilerMessageLogger(logger);
        
        Packager pk = makePackager(program);
        
        // Setup information for status messages 
        int nModules = moduleNames.size();
        double increment = (Math.round ((1.0 / nModules) * 1000.0))/10.0;
        pk.setModuleIncrement(increment);
        
        for (final StatusListener sl : statusListeners) {            
            pk.addStatusListener(sl);
        }

        // For the modules to be compiled, remove timestamps before compiling.
        // As each module is compiled the packager will update the timestamp to
        // the new value.
        for (final ModuleName moduleName : moduleNames) {           
            program.removeCompilationTimestamp(moduleName);
        }

        CompilerMessage.Severity retVal = compiler.compileModules(moduleNames, sourceDefinitionMap, compiledDefinitionMap, pk, foreignContextProvider);
        compiler.setCompilerMessageLogger(null);
        
        return retVal;
    }

    /**
     * @return the program associated with this compiler.
     */
    protected final Program getProgram() {
        return program;
    }

    /**
     * Remove a named module from this program.
     * @param moduleName the module to remove
     * @param includeDependents
     */
    public final void removeModule(ModuleName moduleName, boolean includeDependents) {
        removeModuleInternal (moduleName, includeDependents);
    }   
    
    /**
     * Remove a named module from this program.
     * @param moduleName
     * @param includeDependents
     */
    private final void removeModuleInternal (ModuleName moduleName, boolean includeDependents) {
        Set<Module> removedModules = getProgram().removeModule(moduleName, includeDependents);
        
        // Notify change listeners.        
        for (final Module module : removedModules) {
            fireModuleRemoved(module);
        }
    }

    /**
     * Add a status listener.
     * @param listener StatusListener
     */
    public final void addStatusListener(StatusListener listener) {
        if (!statusListeners.contains(listener)) {
            statusListeners.add(listener);
        }
    }

    /**
     * Remove a status listener.
     * @param listener StatusListener
     */
    public final void removeStatusListener(StatusListener listener) {
        statusListeners.remove(listener);
    }

    /**
     * Add a listener to be notified when the program content changes.
     * @param listener
     */
    public final void addChangeListener (ProgramChangeListener listener){
        if (!programChangeListeners.contains (listener)) {
            programChangeListeners.add (listener);
        }
    }
    
    /**
     * Remove a listener from the listener list.
     * @param listener
     */
    public final void removeChangeListener (ProgramChangeListener listener) {
        programChangeListeners.remove (listener);
    }   

    /**
     * Notify the program change listeners that a module has been loaded.
     * @param module the module which was loaded.
     */
    private final void fireModuleLoaded(Module module) {
        // notify listeners of new module
        for (final ProgramChangeListener pcl : programChangeListeners) {          
            pcl.moduleLoaded (module);
        }
    }
    
    /**
     * Notify the program change listeners that a module has been removed.
     * @param module the module which was removed.
     */
    private final void fireModuleRemoved(Module module) {
        // notify listeners of removed module
        for (final ProgramChangeListener pcl : programChangeListeners) {           
            pcl.moduleRemoved(module);
        }
    }
    
    /**
     * Set the flag which indicates that code regeneration should be forced.
     * 
     * @param b
     *            the new value for the flag.
     */
    public final void setForceCodeRegen (boolean b) {
        forceCodeRegen = b;
    }
    
    /**
     * Get the state of the flag that indicates whether code regeneration should
     * be forced.
     * 
     * @return the state of the flag.
     */
    public final boolean getForceCodeRegen () {
        return forceCodeRegen;
    }
    
    /**
     * Set the flag indicating whether the compiled code will
     * be immediately loaded/run.
     */
    public final void setForImmediateUse (boolean b) {
        forImmediateUse = b;
    }
    
    /**
     * Get the flag indicating the compiled code will be immediately loaded/run.
     * @return boolean
     */
    public boolean isForImmediateUse() {
        return forImmediateUse;
    }      

    /**
     * @return Returns the ignoreCompiledModuleInfo.
     */
    final boolean isIgnoreCompiledModuleInfo() {
        return ignoreCompiledModuleInfo;
    }
    
    /**
     * @param ignoreCompiledModuleInfo The ignoreCompiledModuleInfo to set.
     */
    public final void setIgnoreCompiledModuleInfo(boolean ignoreCompiledModuleInfo) {
        this.ignoreCompiledModuleInfo = ignoreCompiledModuleInfo;
    }

    /**
     * @param foreignContextProvider the foreign context provider to use, or null to use the default.
     * Note that in most cases it is sensible to set this as null.  
     * This is provided as a way for the Eclipse tooling to provide context not visible to the compiler.
     */
    public final void setForeignContextProvider(ForeignContextProvider foreignContextProvider) {
        this.foreignContextProvider = foreignContextProvider;
    }
    /**
     * @return the foreign context provider to use, or null to use the default.
     */
    final ForeignContextProvider getForeignContextProvider() {
        return foreignContextProvider;
    }
    
    /**
     * @return an iterator over the registered status listeners.
     */
    protected final Collection<StatusListener> getStatusListeners () {
        return statusListeners;
    }

    /**
     * Use the CAL based optimizer for compiling.
     * 
     */
    public final void useOptimizer(){
        compiler.useOptimizer();
    }
    
    /**
     * This is the ProgramChangeListener interface.
     * Interface to be implemented by classes that wish to be notified when the program is modified.
     * Listeners can be registered/removed through Program.addChangeListener() and Program.removeChangeListener().
     * Created: Mar 14, 2003
     * @author RCypher
     */
    public interface ProgramChangeListener {
        /**
         * Notify listeners that a module was loaded.  
         * This will be called when the module is complete.
         * @param moduleName the name of the module.
         */
        public void moduleLoaded (Module moduleName);
        
        /**
         * Notify listeners that a module was removed from the program.
         * @param moduleName the name of the module.
         */
        public void moduleRemoved (Module moduleName);
    }
}
