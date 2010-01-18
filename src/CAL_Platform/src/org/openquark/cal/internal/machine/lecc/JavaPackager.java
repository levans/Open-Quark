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
 * JavaPackager.java
 * Creation date: Jan 17, 2003.
 * By: Luke Evans
 */
package org.openquark.cal.internal.machine.lecc;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.openquark.cal.compiler.CompilerMessageLogger;
import org.openquark.cal.compiler.CoreFunction;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.Packager;
import org.openquark.cal.compiler.UnableToResolveForeignEntityException;
import org.openquark.cal.internal.machine.MachineFunctionImpl;
import org.openquark.cal.internal.serialization.ModuleSerializationTags;
import org.openquark.cal.internal.serialization.RecordInputStream;
import org.openquark.cal.internal.serialization.RecordOutputStream;
import org.openquark.cal.machine.CodeGenerator;
import org.openquark.cal.machine.MachineFunction;
import org.openquark.cal.machine.Program;


/**
 * The JavaPackager places LECCCodeLables containing an SCJavaDefn instance into
 * the current package in the program. 
 * @author LEvans
 */
public class JavaPackager extends Packager {

    /** Set of String used to track packaged functions. */
    Set<String> packagedFunctions = new HashSet<String>();
    
    /** 
     * Create a new JavaPackager.
     * @param program the program to package.
     * @param cg
     */
    JavaPackager (Program program, CodeGenerator cg) {
        super (program, cg);
        if (program == null || cg == null) {
            throw new IllegalArgumentException ("Unable to create packager because program or code generator is null.");
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public MachineFunction getMachineFunction(CoreFunction coreFunction) {
        return new LECCMachineFunction(coreFunction);
    }    
    
    /**
     * Wrap the current adjunct: do any cleanup processing on the entire packaged contents.
     * Override of base class method.
     * @param logger
     * @throws Packager.PackagerException
     */
    @Override
    public void wrapAdjunct (CompilerMessageLogger logger) throws Packager.PackagerException, UnableToResolveForeignEntityException {
        // Let the super class handle its post adjunct tasks.
        super.wrapAdjunct(logger);
        
        // At this point we need to discard any previously calculated
        // function group info cached in the module for the adjunct 
        // as it is no longer valid.
        LECCModule module = (LECCModule)getCurrentModule();
        for (final String name : packagedFunctions) {
            module.clearFunctionGroupInfo(name);
        }
        
        // Reset the packaged functions.
        packagedFunctions = new HashSet<String>();
    }

    
    
    /**
     * Wrap the current module: do any cleanup processing on the entire packaged contents.
     * Override of base class method.
     * @param logger
     * @throws Packager.PackagerException
     */
    @Override
    public void wrapModule (CompilerMessageLogger logger) throws Packager.PackagerException, UnableToResolveForeignEntityException {
        // Let the super class handle end of module cleanup.
        super.wrapModule(logger);

        
        // Reset the packaged functions.
        packagedFunctions = new HashSet<String>();
    }
    
    /**
     * Store this core function in the package into the current module.
     * Override of base class method.
     * @param coreFunction - the function to be stored
     * @throws PackagerException
     */
    @Override
    public void store(CoreFunction coreFunction) throws PackagerException {
        // Let the super class handle storing into the module.
        super.store(coreFunction);
        
        // Add the name to the packaged functions set
        packagedFunctions.add(coreFunction.getName().getUnqualifiedName());
    }
    
    /**
     * Create a new module object into which future 'stores' will place program objects.
     * Override of base class method.
     * @param name - name of the module
     * @param foreignClassLoader the classloader to use to resolve foreign classes for the module.
     * @throws PackagerException
     */
    @Override
    public void newModule(ModuleName name, ClassLoader foreignClassLoader) throws PackagerException {
        // Let the super class handle creating the new module.        
        super.newModule(name, foreignClassLoader);
        
        // Reset the packaged functions.
        packagedFunctions = new HashSet<String>();
    }        

    /**
     * Switches the current module to be the specified module. 
     * The module must have already been added to the package.
     * Override of base class method.
     * @param name the name of the module to switch to
     * @throws PackagerException
     */
    @Override
    public void switchModule(ModuleName name) throws PackagerException {
        // Let the super class handle switching to a different module.
        super.switchModule(name);
        
        // Reset the packaged functions.
        packagedFunctions = new HashSet<String>();
    }
    
    public static final class LECCMachineFunction extends MachineFunctionImpl {
        private static final int serialSchema = 0;
        
        /**
         * Construct a label from name and offset
         * @param coreFunction
         */
        public LECCMachineFunction(CoreFunction coreFunction) {
            super(coreFunction);
        }  
        
        /**
         * Default constructor used for serialization.
         */
        protected LECCMachineFunction () {
            
        }
        
       /** 
        * Write this LECCMachineFunction instance out to the RecordOutputStream. 
        * @param s
        * @throws IOException
        */
       @Override
    public void write (RecordOutputStream s) throws IOException {
           s.startRecord (ModuleSerializationTags.LECC_MACHINE_FUNCTION, serialSchema);
           // There are currently no fields in LECCMachineFunction so we simply
           // let the super class write itself out.
           super.write(s);
           s.endRecord ();
       }        
        /**
         * Read the content of this LECCMachineFunction from the 
         * RecordInputStream.
         * @param s
         * @param schema
         * @param mti
         * @param msgLogger
         * @throws IOException
         */
        void readContent (RecordInputStream s, int schema, ModuleTypeInfo mti, CompilerMessageLogger msgLogger) throws IOException {
            // Currently there are no fields in the LECCMachineFunction so we can
            // simply call 'read' on the superclass.
            super.read(s, mti, msgLogger);
        }
    }
}
