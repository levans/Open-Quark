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
package org.openquark.cal.internal.machine.g;

import org.openquark.cal.compiler.CompiledModuleSourceDefinition;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.Packager;
import org.openquark.cal.machine.Program;
import org.openquark.cal.machine.StatusListener;


/**
 * This is the ProgramModifier class.
 *
 * <p>
 * Created: Feb 20, 2003 at 3:45:18 PM
 * @author Raymond Cypher
 */
public class ProgramModifier
    extends org.openquark.cal.compiler.ProgramModifier {

    /**
     * Constructor for ProgramModifier.
     * @param program
     */
    private ProgramModifier(Program program) {
        super(program);
    }
    
    /**
     * Factory method for a ProgramModifier.
     * 
     * @param program the existing program
     * @return a ProgramModifier instance based on the given workspace.
     */
    public static ProgramModifier getProgramModifier(Program program) {
        return new ProgramModifier(program);
    }
    
    @Override
    protected Packager makePackager(Program program) {
        return new GPackager(program, makeCodeGenerator());
    }
    
    @Override
    protected org.openquark.cal.machine.CodeGenerator makeCodeGenerator() {
        CodeGenerator cg = new CodeGenerator(false);
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
        // Currently the g-machine doesn't support loading compiled modules.
        return null;
    }    
}