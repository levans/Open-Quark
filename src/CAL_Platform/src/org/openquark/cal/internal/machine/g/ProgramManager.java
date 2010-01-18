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
package org.openquark.cal.internal.machine.g;

import org.openquark.cal.compiler.EntryPointGenerator;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.ProgramModifier;
import org.openquark.cal.compiler.TypeCheckerImpl;
import org.openquark.cal.machine.CALExecutor;
import org.openquark.cal.machine.MachineStatistics;
import org.openquark.cal.machine.ProgramResourceRepository;
import org.openquark.cal.runtime.ExecutionContext;
import org.openquark.cal.runtime.ExecutionContextProperties;
import org.openquark.cal.runtime.MachineType;
import org.openquark.cal.runtime.ResourceAccess;


/**
 * This is the ProgramManager class.
 *
 * This is a factory class used to create a g-machine-specific 
 * ProgramModifier, EntryPointGenerator or TypeCheckerImpl.
 * <p>
 * Created: Feb 19, 2003 at 12:15:13 PM
 * @author Raymond Cypher
 * @author Joseph Wong
 */
public class ProgramManager extends org.openquark.cal.machine.ProgramManager {
    
    /**
     * Factory method for creating a g-machine-specific ProgramManager.
     * @param resourceAccess
     *            the ResourceAccess instance to provide access to the resources of the current
     *            environment (e.g. from the workspace, or from Eclipse).
     * @return a new program manager.
     */
    public static ProgramManager makeInstance(ResourceAccess resourceAccess) {
        return new ProgramManager(resourceAccess);
    }
    
    /**
     * Private constructor for a g-machine-specific ProgramManager.
     * @param resourceAccess
     *            the ResourceAccess instance to provide access to the resources of the current
     *            environment (e.g. from the workspace, or from Eclipse).
     */
    private ProgramManager(ResourceAccess resourceAccess) {
        super(new GProgram(), resourceAccess);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public MachineType getMachineType() {
        return MachineType.G;
    }

    /**
     * Factory method for creating a g-machine-specific ProgramModifier.
     */
    @Override
    public ProgramModifier makeProgramModifier() {
        return org.openquark.cal.internal.machine.g.ProgramModifier.getProgramModifier(theProgram);
    }
    
    /**
     * Factory method for creating a g-machine-specific EntryPointGenerator.
     */
    @Override
    public EntryPointGenerator makeEntryPointGenerator() {
        return org.openquark.cal.internal.machine.g.EntryPointGenerator.getEntryPointGenerator(theProgram);
    }
    
    /**
     * Factory method for creating a g-machine-specific TypeCheckerImpl.
     */
    @Override
    public TypeCheckerImpl makeTypeCheckerImpl() {
        return org.openquark.cal.internal.machine.g.TypeCheckerImpl.getTypeCheckerImpl(theProgram);
    }
    
    /**
     * Factory method for creating a g-machine-specific execution context.
     */
    @Override
    public ExecutionContext makeExecutionContext(ExecutionContextProperties properties) {
        return new org.openquark.cal.internal.machine.g.Executor.GExecutionContext(properties);
    }

    /**
     * Factory method for creating a g-machine-specific executor.
     */
    @Override
    public CALExecutor makeExecutor(ExecutionContext context) {
        return new org.openquark.cal.internal.machine.g.Executor(theProgram, resourceAccess, context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProgramResourceRepository getProgramResourceRepository() {
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public MachineStatistics getMachineStatistics() {
        return new GMachineStatistics();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public MachineStatistics getMachineStatisticsForModule(ModuleName moduleName) {
        return new GMachineStatistics();
    }
    
    /** An immutable value type for representing G-machine statistics. */
    private static final class GMachineStatistics implements MachineStatistics {
        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "No statistics collected for the G-machine.";
        }
    }
}
