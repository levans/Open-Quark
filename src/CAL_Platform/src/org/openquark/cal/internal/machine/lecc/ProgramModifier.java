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
 * Created: Feb 20, 2003 at 3:45:20 PM
 * By: Raymond Cypher
 */
package org.openquark.cal.internal.machine.lecc;

import org.openquark.cal.compiler.CompiledModuleSourceDefinition;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.Packager;
import org.openquark.cal.internal.machine.RepositoryResourceCompiledModuleSourceDefinition;
import org.openquark.cal.machine.Module;
import org.openquark.cal.machine.Program;
import org.openquark.cal.machine.ProgramResourceLocator;
import org.openquark.cal.machine.ProgramResourceRepository;
import org.openquark.cal.machine.StatusListener;


/**
 * This is lecc machine extension of the ProgramModifier class.
 * It implements the methods to create machine specific instances
 * of the CodeGeneratore, Packager, and Program.
 * <p>
 * Created: Feb 20, 2003 at 3:45:18 PM
 * @author Raymond Cypher
 */
public class ProgramModifier
    extends org.openquark.cal.compiler.ProgramModifier {

    /**
     * The object which provides info about the location of program resources. 
     */
    private final ProgramResourceRepository resourceRepository;

    /**
     * Constructor for ProgramModifier.
     * @param program
     * @param resourceRepository The repository for program resources.
     */
    private ProgramModifier(LECCProgram program, ProgramResourceRepository resourceRepository) {
        super(program);
        
        if (resourceRepository == null) {
            throw new NullPointerException("Argument 'resourceRepository' cannot be null.");
        }
        this.resourceRepository = resourceRepository;
    }
    
    /**
     * Factory method for a ProgramModifier.
     * 
     * @param program the existing program
     * @param resourceRepository the resource finder for program resources
     * @return a ProgramModifier instance based on the given workspace.
     */
    public static ProgramModifier getProgramModifier(Program program, ProgramResourceRepository resourceRepository) {
        return new ProgramModifier((LECCProgram)program, resourceRepository);
    }
    
    /**
     * Create a machine specific instance of Packager.
     * @param program
     * @return - an instance of JavaPackager.
     */
    @Override
    protected Packager makePackager(Program program) {
        return new JavaPackager(program, makeCodeGenerator());
        
    }
    
    /**
     * Create a machine specific instance of CodeGenerator.
     * @return - an instance of the lecc code generator.
     */
    @Override
    protected org.openquark.cal.machine.CodeGenerator makeCodeGenerator() {
        CodeGenerator cg = new CodeGenerator(getForceCodeRegen(), isForImmediateUse(), false, resourceRepository);
        for (final StatusListener sl : getStatusListeners()) {           
            cg.addStatusListener(sl);        
        }
        return cg;
    }
    
    /**
     * This function is used to retrieve a compiled module file written out to the same location as the 
     * generated sources associated with a specific machine.
     * @param moduleName
     * @return a CompiledModuleSourceDefinition if the compiled source is avaialable, otherwise null.
     */
    @Override
    protected CompiledModuleSourceDefinition getCompiledSourceDefinition (ModuleName moduleName) {
        
        ProgramResourceLocator.Folder moduleFolder = CodeGenerator.getModuleResourceFolder(moduleName);
        ProgramResourceLocator.File compiledModuleFileLocator = moduleFolder.extendFile(moduleName + "." + Module.COMPILED_MODULE_SUFFIX);

        if (!resourceRepository.exists(compiledModuleFileLocator)) {
            return null;
        }
        
        return new RepositoryResourceCompiledModuleSourceDefinition (moduleName, compiledModuleFileLocator, resourceRepository);
    }
}
