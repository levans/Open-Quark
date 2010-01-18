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
package org.openquark.cal.internal.machine.lecc;

import org.openquark.cal.compiler.EntryPointGenerator;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.ProgramModifier;
import org.openquark.cal.compiler.TypeCheckerImpl;
import org.openquark.cal.internal.runtime.lecc.RTExecutionContext;
import org.openquark.cal.machine.CALExecutor;
import org.openquark.cal.machine.MachineStatistics;
import org.openquark.cal.machine.Program;
import org.openquark.cal.machine.ProgramResourceRepository;
import org.openquark.cal.runtime.ExecutionContext;
import org.openquark.cal.runtime.ExecutionContextProperties;
import org.openquark.cal.runtime.MachineType;
import org.openquark.cal.runtime.ResourceAccess;


/**
 * This is the ProgramManager class.
 *
 * This is a factory class used to create a lecc-machine-specific 
 * ProgramModifier, EntryPointGenerator or TypeCheckerImpl.
 * <p>
 * Created: Feb 19, 2003 at 12:15:13 PM
 * @author Raymond Cypher
 * @author Joseph Wong
 */
public class ProgramManager extends org.openquark.cal.machine.ProgramManager {

    /**
     * The repository for program resources.
     */
    private final ProgramResourceRepository resourceRepository;
    
    /**
     * Factory method for creating a lecc-machine-specific ProgramManager.
     * @param resourceRepository
     * @param resourceAccess
     *            the ResourceAccess instance to provide access to the resources of the current
     *            environment (e.g. from the workspace, or from Eclipse).
     * @return a new program manager.
     */
    public static ProgramManager makeInstance(ProgramResourceRepository resourceRepository, ResourceAccess resourceAccess) {
        if (resourceRepository == null) {
            throw new NullPointerException("Parameter 'resourceRepository' must not be null.");
        }
        
        return new ProgramManager(new LECCProgram(resourceRepository), resourceRepository, resourceAccess);
    }
    
    /**
     * Private constructor for a lecc-machine-specific ProgramManager.
     * @param program
     * @param resourceRepository
     * @param resourceAccess
     *            the ResourceAccess instance to provide access to the resources of the current
     *            environment (e.g. from the workspace, or from Eclipse).
     */
    private ProgramManager(Program program, ProgramResourceRepository resourceRepository, ResourceAccess resourceAccess) {
        super(program, resourceAccess);
        this.resourceRepository = resourceRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MachineType getMachineType() {
        return MachineType.LECC;
    }
    
    /**
     * Factory method for creating a lecc-machine-specific ProgramModifier.
     * @return an instance of ProgramModifier
     */
    @Override
    public ProgramModifier makeProgramModifier() {
        return org.openquark.cal.internal.machine.lecc.ProgramModifier.getProgramModifier(theProgram, resourceRepository);
    }
    
    /**
     * Factory method for creating a lecc-machine-specific EntryPointGenerator.
     * @return an instance of EntryPointGenerator
     */
    @Override
    public EntryPointGenerator makeEntryPointGenerator() {
        return org.openquark.cal.internal.machine.lecc.EntryPointGenerator.getEntryPointGenerator(theProgram, resourceRepository);
    }
    
    /**
     * Factory method for creating a lecc-machine-specific TypeCheckerImpl.
     * @return an instance of TypeCheckerImpl
     */
    @Override
    public TypeCheckerImpl makeTypeCheckerImpl() {
        return org.openquark.cal.internal.machine.lecc.TypeCheckerImpl.getTypeCheckerImpl(theProgram);
    }

    /**
     * Factory method for creating a lecc-machine-specific execution context.
     * @param properties
     * @return an instance of ExecutionContext
     */
    @Override
    public ExecutionContext makeExecutionContext(ExecutionContextProperties properties) {
        return new RTExecutionContext(properties, null);
    }

    /**
     * Factory method for creating a lecc-machine-specific executor.
     * @param context
     * @return an instance of CALExecutor
     */
    @Override
    public CALExecutor makeExecutor(ExecutionContext context) {
        return new org.openquark.cal.internal.machine.lecc.Executor(theProgram, resourceAccess, context);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ProgramResourceRepository getProgramResourceRepository() {
        return resourceRepository;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public MachineStatistics getMachineStatistics() {
        return ((LECCProgram)theProgram).getMachineStatistics();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public MachineStatistics getMachineStatisticsForModule(ModuleName moduleName) {
        return ((LECCProgram)theProgram).getMachineStatisticsForModule(moduleName);
    }
}
