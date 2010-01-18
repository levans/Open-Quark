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
 * GProgram.java
 * Created: Nov 24, 2003  5:43:35 PM
 * By: RCypher
 */
package org.openquark.cal.internal.machine.g;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.openquark.cal.compiler.CompilerMessageLogger;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.internal.serialization.RecordInputStream;
import org.openquark.cal.internal.serialization.RecordOutputStream;
import org.openquark.cal.machine.MachineFunction;
import org.openquark.cal.machine.Module;
import org.openquark.cal.machine.Program;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.runtime.ExecutionContext;


/**
 *  This is the Program derivative specific to the g-machine.
 *
 *  @author rcypher
 */
class GProgram extends Program {

    /**
     * {@inheritDoc}
     */
    @Override
    synchronized public void resetCachedResults(ExecutionContext context) {
        resetCachedResults(CAL_Prelude.MODULE_NAME, context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    synchronized public void resetCachedResults(ModuleName moduleName, ExecutionContext context) {
        resetCachedResults (moduleName, context, new HashSet<ModuleName>());
        // run the cleanup hooks when the CAFs are released.
        context.cleanup();
    }
    
    /**
     * Reset the cached results in the named module and any dependent modules.
     * @param moduleName
     * @param context - the execution context of the results to be discarded. 
     * @param resetModules - a set of module names representing the modules that have already been reset.
     */
    synchronized private void resetCachedResults(ModuleName moduleName, ExecutionContext context, Set<ModuleName> resetModules) {
        if (resetModules.contains (moduleName)) {
            return;
        }

        Module m = getModule(moduleName);
        if (m == null) {
            return;
        }

        resetModules.add(moduleName);
                
        for (final MachineFunction machineFunction : m.getFunctions()) {
            GMachineFunction gmf = (GMachineFunction)machineFunction;
            gmf.resetNGlobal(context);
        }
              
        for (final Module m2 : getModules()) {            
            if (m2.dependsOn(moduleName)) {
                resetCachedResults(m2.getName(), context, resetModules);
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    synchronized public void resetMachineState(ExecutionContext context) {
        resetCachedResults(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    synchronized public void resetMachineState(ModuleName moduleName, ExecutionContext context) {
        resetCachedResults(moduleName, context);
    }
    
    /**
    * Add a module to this program.
    * Normally called by a Loader.
    * Derived Program classes create a type
    * specific module object and then call addModule (Module)
    * Creation date: (3/17/00 4:11:18 PM)
    * @param moduleName
    * @param foreignClassLoader the classloader to use to resolve foreign classes for the module.
    * @return the newly created Module
    */
    @Override
    synchronized public Module addModule(ModuleName moduleName, ClassLoader foreignClassLoader) {
        Module module = new GModule(moduleName, foreignClassLoader);
        addModule (module);
        return module;
    }
    
    /**
     * The version of Module specific to the g machine.
     * GModule
     * @author RCypher
     * Created: May 14, 2004
     */
    static class GModule extends Module {
        
        GModule (ModuleName moduleName, ClassLoader foreignClassLoader) {
            super (moduleName, foreignClassLoader);
        }
        
        /**
         * This method is used to delegate loading of MachineFunciton instances
         * to concrete extensions of Module.  The sub-classes of module contain
         * the information about what class to actually load.
         * @param s
         * @param mti
         * @param msgLogger
         * @return an instance of MachineFunction
         * @throws IOException 
         */
        @Override
        protected MachineFunction loadMachineFunction(RecordInputStream s, ModuleTypeInfo mti, CompilerMessageLogger msgLogger) throws IOException {
            throw new IOException ("Serialization is not supported for the g-machine.");
        }
               
        /**
         * Write this instance of GModule out to the RecordOutputStream
         * @param s
         * @throws IOException
         */
        @Override
        protected void writeActual (RecordOutputStream s) throws IOException {
            throw new IOException ("Serialization is not supported for the g-machine.");
        }

        /** {@inheritDoc} */
        @Override
        protected void writeMachineSpecificSerializationInfo(RecordOutputStream s) throws IOException {
            throw new IOException ("Serialization is not supported for the g-machine.");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean mustLoadExpressions() {
            return false;
        }
    }
}
