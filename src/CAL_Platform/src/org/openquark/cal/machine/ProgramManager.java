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
 * ProgramManager.java
 * Created: Feb 19, 2003 at 12:15:13 PM
 * By: Raymond Cypher
 */
package org.openquark.cal.machine;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.openquark.cal.compiler.EntryPointGenerator;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.ProgramModifier;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.TypeCheckerImpl;
import org.openquark.cal.machine.Program.ProgramException;
import org.openquark.cal.runtime.ExecutionContext;
import org.openquark.cal.runtime.ExecutionContextProperties;
import org.openquark.cal.runtime.MachineConfiguration;
import org.openquark.cal.runtime.MachineType;
import org.openquark.cal.runtime.ResourceAccess;


/**
 * This is the ProgramManager class.
 *
 * This is a factory class used to create a machine specific 
 * ProgramModifier, EntryPointGenerator or TypeCheckerImpl.
 * <p>
 * Created: Feb 19, 2003 at 12:15:13 PM
 * @author Raymond Cypher
 * @author Joseph Wong
 */
public abstract class ProgramManager {
    
   
    
    /**
     * The copy of the machine specific program. The scope of this field is
     * protected so as to allow subclasses to access it. It is never meant to be
     * made available to external clients.
     */
    protected final Program theProgram;
    
    /**
     * Provides access to the resources of the current environment (e.g. from the workspace, or from Eclipse).
     */
    protected final ResourceAccess resourceAccess;
    
    /**
     * Protected constructor for this abstract ProgramManager class.
     * 
     * @param program
     *            the Program object to be encapsulated by this ProgramManager
     *            instance.
     * @param resourceAccess
     *            the ResourceAccess instance to provide access to the resources of the current
     *            environment (e.g. from the workspace, or from Eclipse).
     */
    protected ProgramManager(Program program, ResourceAccess resourceAccess) {
        if (program == null) {
            throw new IllegalArgumentException("ProgramManager: the program must not be null");
        }
        if (resourceAccess == null) {
            throw new IllegalArgumentException("ProgramManager: the resourceAccess must not be null");
        }
        
        theProgram = program;
        this.resourceAccess = resourceAccess;
    }
    
    /**
     * Factory method for a workspace manager.
     * The default machine type will be used, unless otherwise specified by the system property.
     * @param resourceAccess
     *            the ResourceAccess instance to provide access to the resources of the current
     *            environment (e.g. from the workspace, or from Eclipse).
     *  @return the ProgramManager for this workspace.            
     */
    public static ProgramManager getProgramManager(ProgramResourceRepository.Provider resourceRepositoryProvider, ResourceAccess resourceAccess) {
        
        // the machine type system property is fetched on a per-call basis rather than during
        // initialization, allowing a determined client (e.g. ICE) to set the property
        // at an opportune time before this method is called.
        
        String machineTypeProperty = System.getProperty(MachineConfiguration.MACHINE_TYPE_PROP);

        // Lecc is the default machine type.
        boolean useGMachine = machineTypeProperty != null && machineTypeProperty.equals(MachineType.G.toString());
        MachineType machineType = useGMachine ? MachineType.G : MachineType.LECC;
        
        return getProgramManager(machineType, resourceRepositoryProvider, resourceAccess);
    }
    
    /**
     * Factory method for a workspace manager.
     * Note that specification of the machine type is intended as "internal" public.
     * Clients will always use the default machine type, unless they are intended to have "special" 
     * knowledge that other machine types are available.
     * 
     * @param machineType the machine type.
     * @param resourceAccess
     *            the ResourceAccess instance to provide access to the resources of the current
     *            environment (e.g. from the workspace, or from Eclipse).
     */
    public static ProgramManager getProgramManager(MachineType machineType, ProgramResourceRepository.Provider resourceRepositoryProvider, ResourceAccess resourceAccess) {
        
        if (machineType == MachineType.G) {
            return org.openquark.cal.internal.machine.g.ProgramManager.makeInstance(resourceAccess);
        
        } else if (machineType == MachineType.LECC) {
            return org.openquark.cal.internal.machine.lecc.ProgramManager.makeInstance(resourceRepositoryProvider.getRepository(machineType), resourceAccess);
            
        } else {
            throw new IllegalArgumentException("Unknown machine type: " + machineType.toString());
        }
    }
    
    /**
     * Returns the machine type associated with this ProgramManager.
     * 
     * @return the machine type.
     */
    public abstract MachineType getMachineType();

    /**
     * Factory method for creating a machine-specific ProgramModifier.
     * 
     * @return a new ProgramModifier instance.
     */
    public abstract ProgramModifier makeProgramModifier();
    
    /**
     * Factory method for creating a machine-specific EntryPointGenerator.
     * 
     * @return a new EntryPointGenerator instance.
     */
    public abstract EntryPointGenerator makeEntryPointGenerator();
    
    /**
     * Factory method for creating a machine-specific TypeCheckerImpl.
     * 
     * @return a new TypeCheckerImpl instance.
     */
    public abstract TypeCheckerImpl makeTypeCheckerImpl();
    
    /**
     * Factory method for creating a machine-specific execution context.
     * @param properties the ExecutionContextProperties instance encapsulating an immutable map of key-value pairs which is exposed
     *                   as system properties from within CAL.
     * 
     * @return a new CALExecutor.Context instance.
     */
    public abstract ExecutionContext makeExecutionContext(ExecutionContextProperties properties);
    
    /**
     * Factory method for creating a machine-specific executor.
     * 
     * @param context
     *            the execution context to be used by the executor.
     * @return a new CALExecutor instance.
     */
    public abstract CALExecutor makeExecutor(ExecutionContext context);
    
    /**
     * Discards any cached (memoized) results in this program.
     * 
     * @param context
     *            the context with which the cached results are associated.
     */
    public void resetCachedResults(ExecutionContext context) {
        theProgram.resetCachedResults(context);
    }
    
    /**
     * Discards any cached (memoized) results in the named module and any
     * dependent modules.
     * 
     * @param moduleName
     *            the name of the module whose cached results, and whose
     *            dependents' cached results, are to be discarded.
     * @param context
     *            the context with which the cached results are associated.
     */
    public void resetCachedResults(ModuleName moduleName, ExecutionContext context) {
        theProgram.resetCachedResults(moduleName, context);
    }
    
    /**
     * Discards the machine state, including any cached (memoized) results in this program.
     * 
     * @param context
     *            the context with which the machine state is associated.
     */
    public void resetMachineState(ExecutionContext context) {
        theProgram.resetMachineState(context);
    }
    
    /**
     * Discards the machine state, including any cached (memoized) results in the named module and any
     * dependent modules.
     * 
     * @param moduleName
     *            the name of the module whose machine state, and whose
     *            dependents' machine state, are to be discarded.
     * @param context
     *            the context with which the machine state associated.
     */
    public void resetMachineState(ModuleName moduleName, ExecutionContext context) {
        theProgram.resetMachineState(moduleName, context);
    }
    
    /**
     * Returns true iff the specified module is in the Program instance
     * encapsulated by this ProgramManager.
     * 
     * @param moduleName
     *            the name of the module to check.
     * @return true iff the specified module is in the program.
     */
    public boolean containsModule(ModuleName moduleName) {
        return theProgram.getModule(moduleName) != null;
    }
    
    /**
     * Returns the names of the modules contained by the Program instance
     * encapsulated by this ProgramManager.
     * 
     * @return an array of module names for the modules in the program.
     */
    public ModuleName[] getModuleNames() {
        return theProgram.getModuleNames();
    }
    
    /**
     * Return a list of Module object for modules contained the Program instance
     * encapsulated by this ProgramManager.
     * 
     * @return a list of Module objects for the modules in this program.
     */
    public List<Module> getModules() {
        return theProgram.getModules();
    }

    /**
     * Gets the module with a given moduleName.
     * @return the module with a given module name
     * @param name  the module name
     */
    public Module getModule(ModuleName name) {
        return theProgram.getModule(name);
    }
    
    /**
     * Generates a list of all modules that depend on the named module either
     * directly or indirectly.
     * 
     * @param dependeeModulename
     *            the name of the dependee module.
     * @param returnEmptySetIfNonexistent
     *            if true, this method returns an empty list if the module is
     *            not in the program. Otherwise it returns null in such a case.
     * @return A list of names of all dependent modules.
     */
    public Set<ModuleName> getSetOfDependentModuleNames(ModuleName dependeeModulename, boolean returnEmptySetIfNonexistent) {
        Set<ModuleName> set = theProgram.getDependentModules(dependeeModulename);
        if (set == null) {
            return returnEmptySetIfNonexistent ? Collections.<ModuleName>emptySet() : null;
        } else {
            return set;
        }
    }
    
    /**
     * Returns the type info for the specified module.
     * 
     * @param moduleName
     *            the name of the module whose type info is requested.
     * @return the type info for the specified module, or null if the module is
     *         not in the program.
     */
    public ModuleTypeInfo getModuleTypeInfo(ModuleName moduleName) {
        Module module = theProgram.getModule(moduleName);
        if (module != null) {
            return module.getModuleTypeInfo();
        } else {
            return null;
        }
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
        Module module = theProgram.getModule(moduleName);
        if (module != null) {
            return module.getNFunctions();
        } else {
            return 0;
        }        
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
        return theProgram.getCodeLabel(functionName);
    }
           
    /**
     * @param useOptimizer True iff the CAL based optimizer should be used during compilation.
     */
    public void useOptimizer(boolean useOptimizer){
        theProgram.useOptimizer(useOptimizer);
    }

    /**
     * @return the repository for program resources, or null if none.
     */
    public abstract ProgramResourceRepository getProgramResourceRepository();

    /**
     * @return the machine statistics associated with this program.
     */
    public abstract MachineStatistics getMachineStatistics();

    /**
     * @param moduleName
     * @return the machine statistics associated with the given module.
     */
    public abstract MachineStatistics getMachineStatisticsForModule(ModuleName moduleName);
}
