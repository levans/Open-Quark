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
 * GPackager.java
 * Created: Feb 20, 2003 at 3:45:20 PM
 * By: Raymond Cypher
 */
package org.openquark.cal.internal.machine.g;

import org.openquark.cal.compiler.CoreFunction;
import org.openquark.cal.compiler.Packager;
import org.openquark.cal.machine.CodeGenerator;
import org.openquark.cal.machine.MachineFunction;
import org.openquark.cal.machine.Program;


/**
 * This is the GPackager class.
 * This class adds CodeLabels containing g-machine specific info into a program object.
 * <p>
 * Created: Feb 20, 2003 at 3:45:18 PM
 * @author Raymond Cypher
 */
class GPackager extends Packager {

    /**
     * Create a new GPackager.
     * @param program the program to package.
     * @param cg
     */
    public GPackager(Program program, CodeGenerator cg) {
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
        return new GMachineFunction (coreFunction);
    }
    
}
