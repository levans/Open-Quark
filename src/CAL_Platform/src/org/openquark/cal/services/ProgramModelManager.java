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
 * ProgramModelManager.java
 * Creation date: Nov 7, 2005.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openquark.cal.compiler.Compiler;
import org.openquark.cal.compiler.CompilerMessage;
import org.openquark.cal.compiler.CompilerMessageLogger;
import org.openquark.cal.compiler.EntryPointGenerator;
import org.openquark.cal.compiler.ForeignContextProvider;
import org.openquark.cal.compiler.MessageKind;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.ModuleSourceDefinition;
import org.openquark.cal.compiler.ModuleSourceDefinitionGroup;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.ProgramModifier;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.TypeChecker;
import org.openquark.cal.machine.CALExecutor;
import org.openquark.cal.machine.MachineFunction;
import org.openquark.cal.machine.MachineStatistics;
import org.openquark.cal.machine.Module;
import org.openquark.cal.machine.ProgramManager;
import org.openquark.cal.machine.StatusListener;
import org.openquark.cal.machine.Program.ProgramException;
import org.openquark.cal.runtime.ExecutionContext;
import org.openquark.cal.runtime.ExecutionContextProperties;
import org.openquark.cal.runtime.MachineType;



/**
 * This class presents a higher-level abstraction to the program manager.
 * It can manage queries and changes to the program without having to know about a program manager.
 * @author Edward Lam
 */
public class ProgramModelManager {
    
    /** The program manager. */
    private final ProgramManager programManager;
    
    /** The set of StatusListeners that want to listen to program generation changes in the program model. */
    private final Set<StatusListener> statusListeners;

    /**
     * Constructor for a program model manager. This is intended to be called
     * by the unit testing infrastructure to specify a custom ProgramManager.
     * 
     * @param programManager the ProgramManager for this ProgramModelManager.
     */
    public ProgramModelManager(ProgramManager programManager) {
        
        if (programManager == null) {
            throw new NullPointerException("Argument 'programManager' must not be null.");
        }
        
        this.programManager = programManager;
        this.statusListeners = Collections.synchronizedSet(new HashSet<StatusListener>());
    }
    
    /**
     * Makes a new disposable copy of the program modifier.
     * @param options - the values for compiler options
     * @return a copy of the program modifier, either a new one, or the private copy held by this workspace manager.
     */
    protected ProgramModifier makeProgramModifier(CompilationOptions options) {
        
        ProgramModifier newProgramModifier = programManager.makeProgramModifier();
        
        for (final StatusListener statusListener : statusListeners) {
            newProgramModifier.addStatusListener(statusListener);
        }
        
        if (options != null) {
            newProgramModifier.setForceCodeRegen(options.isForceCodeRegeneration());
            newProgramModifier.setForImmediateUse(options.isForImmediateUse());
            newProgramModifier.setIgnoreCompiledModuleInfo(options.isIgnoreCompiledModuleInfo());
            newProgramModifier.setForeignContextProvider(options.getCustomForeignContextProvider());
        }
        
        return newProgramModifier;
    }

    /**
     * Makes a new disposable copy of the entry point generator.
     * @param options - the values for compiler options
     * @return a new entry point generator.
     */
    private EntryPointGenerator makeEntryPointGenerator(CompilationOptions options) {
        
        EntryPointGenerator newEntryPointGenerator = programManager.makeEntryPointGenerator();
        
        for (final StatusListener statusListener : statusListeners) {
            newEntryPointGenerator.addStatusListener(statusListener);
        }
        
        if (options != null) {
            newEntryPointGenerator.setForceCodeRegen(options.isForceCodeRegeneration());
            newEntryPointGenerator.setForImmediateUse(options.isForImmediateUse());
            
            // TODOEL: Should we provide some kind of warning if inapplicable options are set?
            // eg. Can't override the foreign context for a module.
            //     ignoreCompiledModuleInfo doesn't apply.
        }
        
        return newEntryPointGenerator;
    }

    /**
     * Get the Compiler for this manager.
     * @return Compiler
     */
    public Compiler getCompiler() {
        return getCompiler(new CompilationOptions());
    }
    
    /**
     * Get the Compiler for this manager.
     * 
     * @param compilationOptions the compilation options for the compiler.
     * @return Compiler
     */
    public Compiler getCompiler(CompilationOptions compilationOptions) {
        Assert.isNotNullArgument(compilationOptions, "compilationOptions");
        return makeEntryPointGenerator(compilationOptions);
    }

    /**
     * @return a type checker associated with this manager.
     */
    public TypeChecker getTypeChecker() {
        // Make a new disposable copy of the type checker.
        return programManager.makeTypeCheckerImpl();
    }
    
    /**
     * Factory method for creating a machine-specific execution context.
     * @param properties the ExecutionContextProperties instance encapsulating an immutable map of key-value pairs which is exposed
     *                   as system properties from within CAL.
     * 
     * @return a new CALExecutor.Context instance.
     */
    public ExecutionContext makeExecutionContext(ExecutionContextProperties properties) {
        return programManager.makeExecutionContext(properties);
    }
    
    /**
     * Factory method for creating a machine-specific execution context with default values for
     * the execution context's properties.
     * 
     * @return a new CALExecutor.Context instance.
     */
    public ExecutionContext makeExecutionContextWithDefaultProperties() {
        return programManager.makeExecutionContext(makeDefaultExectionContextProperties());
    }

    /**
     * Factory method for creating a machine-specific executor.
     * 
     * @param context
     *            the execution context to be used by the executor.
     * @return a new CALExecutor instance.
     */
    public CALExecutor makeExecutor(ExecutionContext context) {
        return programManager.makeExecutor(context);
    }
    
    /**
     * Factory method for creating a machine-specific executor with a new
     * execution context.
     * @param properties the ExecutionContextProperties instance encapsulating an immutable map of key-value pairs which is exposed
     *                   as system properties from within CAL.
     * 
     * @return a new CALExecutor instance with a new execution context.
     */
    public CALExecutor makeExecutorWithNewContext(ExecutionContextProperties properties) {
        return programManager.makeExecutor(programManager.makeExecutionContext(properties));
    }
    
    /**
     * Factory method for creating a machine-specific executor with a new
     * execution context and default values for the execution context's properties.
     * 
     * @return a new CALExecutor instance with a new execution context.
     */
    public CALExecutor makeExecutorWithNewContextAndDefaultProperties() {
        return programManager.makeExecutor(programManager.makeExecutionContext(makeDefaultExectionContextProperties()));
    }
    
    /**
     * @return a ExecutionContextProperties instance encapsulating the default values for the execution context's properties.
     */
    private ExecutionContextProperties makeDefaultExectionContextProperties() {
        ExecutionContextProperties.Builder propertiesBuilder = new ExecutionContextProperties.Builder();
        ExecutionContextProperties defaultProperties = propertiesBuilder.toProperties();
        return defaultProperties;
    }
    
    /**
     * Compile the modules in this manager.
     * @param logger the logger used to log error messages.
     * @param dirtyModulesOnly if true, only dirty (uncompiled or changed) modules will be compiled.
     * @param statusListener a listener, possibly null, for the status of the compilation process
     */
    public void compile(ModuleSourceDefinitionGroup definitionGroup, CompilerMessageLogger logger, boolean dirtyModulesOnly, StatusListener statusListener) {
        compile(definitionGroup, logger, dirtyModulesOnly, statusListener, new CompilationOptions());
    }

    /**
     * Compile the modules in this manager.
     * @param logger the logger used to log error messages.
     * @param dirtyModulesOnly if true, only dirty (uncompiled or changed) modules will be compiled.
     * @param statusListener a listener, possibly null, for the status of the compilation process
     * @param options - values for compilation options
     */
    public void compile(ModuleSourceDefinitionGroup definitionGroup, CompilerMessageLogger logger,
            boolean dirtyModulesOnly, StatusListener statusListener, CompilationOptions options) {
        
        if (definitionGroup == null) {
            logger.logMessage(new CompilerMessage(new MessageKind.Fatal.CouldNotFindWorkspaceDefinition()));
            return;
        }
        
        ProgramModifier newProgramModifier = makeProgramModifier(options);
        
        if (statusListener != null) {
            newProgramModifier.addStatusListener(statusListener);
        }
        
        try {
            newProgramModifier.compile(definitionGroup, logger, dirtyModulesOnly);
            
        } finally {
            if (statusListener != null) {
                newProgramModifier.removeStatusListener(statusListener);
            }
        }
    }
    
    /**
     * @return the program manager.
     */
    protected final ProgramManager getProgramManager() {
        // TODOEL: remove this method!(?)
        return programManager;
    }
    
    /**
     * Compile a module to the current program.
     *   Dependent modules will be removed from the program.
     * @param sourceDef the ModuleSourceDefinition to compile.
     * @param logger the logger to use during the compile.
     * @return the highest error condition experienced
     */
    public CompilerMessage.Severity makeModule (ModuleSourceDefinition sourceDef, CompilerMessageLogger logger) {
        return makeModule(sourceDef, logger, new CompilationOptions());
    }

    /**
     * Compile a module to the current program.
     *   Dependent modules will be removed from the program.
     * @param sourceDef the ModuleSourceDefinition to compile.
     * @param logger the logger to use during the compile.
     * @param options - values for the compilation options
     * @return the highest error condition experienced
     */
    public CompilerMessage.Severity makeModule (ModuleSourceDefinition sourceDef, CompilerMessageLogger logger, CompilationOptions options) {
        
        // We want to always force code regeneration when compiling a module (vs. compiling the entire workspace).
        // This is partly to workaround the issue where compiling multiple SourceModelModuleSource
        // objects with the same timestamp back-to-back would erroneously supress the code
        // generation for the second and subsequent compilation attempts.
        CompilationOptions modifiedOptions = new CompilationOptions(options);
        modifiedOptions.setForceCodeRegeneration(true);
        
        ProgramModifier pm = makeProgramModifier(modifiedOptions);
        return pm.compile (sourceDef, logger);
    }

    /**
     * Compile module(s) to the current program.
     *   Dependent modules will be removed if not included.
     * @param moduleNames a list of the names of the changed modules.
     * @param definitionGroup the definition group from which the module definitions may be retrieved.
     * @param logger the logger to use during the compile.
     * @return the highest error condition experienced
     */
    public CompilerMessage.Severity makeModules (ModuleName[] moduleNames, ModuleSourceDefinitionGroup definitionGroup, CompilerMessageLogger logger) {
        return makeModules (moduleNames, definitionGroup, logger, new CompilationOptions());
    }
    
    /**
     * Compile module(s) to the current program.
     *   Dependent modules will be removed if not included.
     * @param moduleNames a list of the names of the changed modules.
     * @param definitionGroup the definition group from which the module definitions may be retrieved.
     * @param logger the logger to use during the compile.
     * @param options - the values for compilation options
     * @return the highest error condition experienced
     */
    public CompilerMessage.Severity makeModules (ModuleName[] moduleNames, ModuleSourceDefinitionGroup definitionGroup, CompilerMessageLogger logger, CompilationOptions options) {
        
        // We want to always force code regeneration when compiling a given set of modules (vs. compiling the entire workspace).
        // This is partly to workaround the issue where compiling multiple SourceModelModuleSource
        // objects with the same timestamp back-to-back would erroneously supress the code
        // generation for the second and subsequent compilation attempts.
        CompilationOptions modifiedOptions = new CompilationOptions(options);
        modifiedOptions.setForceCodeRegeneration(true);
        
        ProgramModifier pm = makeProgramModifier(modifiedOptions);
        return pm.compile (moduleNames, definitionGroup, logger);
    }

    /**
     * @param listener StatusListener
     */
    public void addStatusListener(StatusListener listener) {
        statusListeners.add(listener);
    }

    /**
     * @param listener StatusListener
     */
    public void removeStatusListener(StatusListener listener) {
        statusListeners.remove(listener);
    }

    /**
     * Discarded any cached (memoized) results in this program.
     * @param context the context with which the cached results are associated.
     */
    public void resetCachedResults(ExecutionContext context) {
        programManager.resetCachedResults (context);
    }
    
    /**
     * Discard any cached (memoized) results in the named module and any dependent modules.
     * @param moduleName
     * @param context the context with which the cached results are associated.
     */
    public void resetCachedResults(ModuleName moduleName, ExecutionContext context) {
        programManager.resetCachedResults (moduleName, context);
    }

    /**
     * Discarded the machine state, including any cached (memoized) results in this program.
     * @param context the context with which the machine state is associated.
     */
    public void resetMachineState(ExecutionContext context) {
        programManager.resetMachineState(context);
    }
    
    /**
     * Discard the machine state, including any cached (memoized) results in the named module and any dependent modules.
     * @param moduleName
     * @param context the context with which the machine state is associated.
     */
    public void resetMachineState(ModuleName moduleName, ExecutionContext context) {
        programManager.resetMachineState(moduleName, context);
    }

    /**
     * Remove a module from the program.
     * Its dependents will also be removed.
     * 
     * @param moduleName the name of the module to remove.
     * @return whether removing the module was successful.
     */
    public final boolean removeModule (ModuleName moduleName) {
        makeProgramModifier(new CompilationOptions()).removeModule(moduleName, true);
        return true;
    }
    
    /**
     * 
     * @param targetModule
     * @return the names of the dependent modules.
     */
    public Set<ModuleName> getDependentModuleNames (ModuleName targetModule) {
        return programManager.getSetOfDependentModuleNames(targetModule, true);
    }
    
    /**
     * 
     * @param moduleName
     * @return type info for the named module.  Null if module doesn't exist.
     */
    public ModuleTypeInfo getModuleTypeInfo (ModuleName moduleName) {
        return programManager.getModuleTypeInfo(moduleName);
    }
    
    /**
     * Gets the module with a given moduleName.
     * @return the module with a given module name
     * @param name  the module name
     */
    public Module getModule(ModuleName name) {
        return programManager.getModule(name);
    }

    /**
     * Return a list of Module object for modules contained the ProgramManager instance
     * encapsulated by this ProgramModleManager.
     * 
     * @return a list of Module objects for the modules in this program.
     */
    public List<Module> getModules() {
        return programManager.getModules();
    }
    
    /**
     * Returns true iff the specified module is in the Program instance
     * encapsulated by this WorkspaceManager.
     * 
     * @param moduleName
     *            the name of the module to check.
     * @return true iff the specified module is in the program.
     */
    public boolean hasModuleInProgram(ModuleName moduleName) {
        // this is purely a query on the program, so we do not check whether the module
        // is in the workspace
        return programManager.containsModule(moduleName);
    }
    
    /**
     * Returns the names of the modules contained by the Program instance
     * encapsulated by this WorkspaceManager.
     * 
     * @return an array of module names for the modules in the program.
     */
    public ModuleName[] getModuleNamesInProgram() {
        // this is purely a query on the program, and differs in semantics from
        // the method getModuleNames(), which returns the names of the modules in
        // the workspace rather than the program
        return programManager.getModuleNames();
    }
    
    /**
     * Returns the number of functions in the specified module.
     * 
     * @param moduleName
     *            the name of the module.
     * @return the number of functions in the specified module, or 0 if the
     *         module is not in the program.
     */
    public int getNFunctionsInModule(ModuleName moduleName) {
        // this is purely a query on the program, so we do not check whether the module
        // is in the workspace
        return programManager.getNFunctionsInModule(moduleName);
    }
    
    /**
     * Retrieves the specified machine function.
     * 
     * @param functionName
     *            the qualified name of the function.
     * @return the specified machine function, or null if it does not exist.
     * @throws ProgramException
     *             if the program does not contain the module for the function.
     */
    public MachineFunction getMachineFunction(QualifiedName functionName) throws ProgramException {
        return programManager.getMachineFunction(functionName);
    }
       
    
    /**
     * Returns the machine type associated with this WorkspaceManager.
     * 
     * @return the machine type.
     */
    public MachineType getMachineType() {
        return programManager.getMachineType();
    }

    /**
     * @return the machine statistics associated with this program.
     */
    public MachineStatistics getMachineStatistics() {
        return programManager.getMachineStatistics();
    }
    
    /**
     * @param moduleName
     * @return the machine statistics associated with the given module.
     */
    public MachineStatistics getMachineStatisticsForModule(ModuleName moduleName) {
        return programManager.getMachineStatisticsForModule(moduleName);
    }
    
    /**
     * A class to encapsulate the various compilation options
     * when compiling a module or adjunct.
     * @author rcypher
     */
    public static class CompilationOptions {
        /** Set to true if all existing generated code should be discarded and regenerated. */
        private boolean forceCodeRegeneration;
        
        /** Set to true if any compiled module files (i.e. type info etc.) should be ignored.
         * This is used in situations where we want to force compilation of all modules
         * without necessarily forcing re-generation of machine specific code.
         * This is useful in situation where the user has manually deleted/modified
         * the generated machine-specific code. */
        private boolean ignoreCompiledModuleInfo;
        
        /** Indicates to the compiler/code generator that the entities being compiled will be used immediately. */
        private boolean forImmediateUse;
        
        /** Any custom foreign context provider specified by the client. */
        private ForeignContextProvider customForeignContextProvider;
        
        public CompilationOptions () {
        }
        
        /**
         * Copy-constructor for CompilationOptions.
         * 
         * This copy-constructor is meant to facilitate the task of making a
         * copy of an existing CompilationOptions instance so that the
         * compilation options can be modified without affecting the original
         * copy.
         * 
         * @param other
         *            the CompilationOptions instance to be copied.
         */
        public CompilationOptions(CompilationOptions other) {
            this.forceCodeRegeneration = other.forceCodeRegeneration;
            this.ignoreCompiledModuleInfo = other.ignoreCompiledModuleInfo;
            this.forImmediateUse = other.forImmediateUse;
            this.customForeignContextProvider = other.customForeignContextProvider;
        }
        
        /**
         * @return Returns the forceCodeRegeneration.
         */
        public boolean isForceCodeRegeneration() {
            return forceCodeRegeneration;
        }
        /**
         * @param forceCodeRegeneration The forceCodeRegeneration to set.
         */
        public void setForceCodeRegeneration(boolean forceCodeRegeneration) {
            this.forceCodeRegeneration = forceCodeRegeneration;
        }
        /**
         * @return Returns the forImmediateUse.
         */
        public boolean isForImmediateUse() {
            return forImmediateUse;
        }
        /**
         * @param forImmediateUse The forImmediateUse to set.
         */
        public void setForImmediateUse(boolean forImmediateUse) {
            this.forImmediateUse = forImmediateUse;
        }

        /**
         * @return Returns the ignoreCompiledModuleInfo.
         */
        public boolean isIgnoreCompiledModuleInfo() {
            return ignoreCompiledModuleInfo;
        }
        
        /**
         * @param ignoreCompiledModuleInfo The ignoreCompiledModuleInfo to set.
         */
        public void setIgnoreCompiledModuleInfo(boolean ignoreCompiledModuleInfo) {
            this.ignoreCompiledModuleInfo = ignoreCompiledModuleInfo;
        }

        
        /**
         * @return Any custom foreign context provider specified by the client.  Null if not specified.
         */
        public ForeignContextProvider getCustomForeignContextProvider() {
            return customForeignContextProvider;
        }
        
        /**
         * @param customForeignContextProvider Set the custom foreign context provider to use.  Null to use the default.
         * Note that in most cases it is sensible to set this as null.
         * This is provided as a way for the Eclipse dev tools to provide context not visible to the compiler.
         */
        public void setCustomForeignContextProvider(ForeignContextProvider customForeignContextProvider) {
            this.customForeignContextProvider = customForeignContextProvider;
        }
    }
}
