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
 * Compiler.java
 * Created: Mar 7, 2003
 * By: Raymond Cypher
 */

package org.openquark.cal.compiler;

import java.util.List;

import org.openquark.cal.compiler.io.EntryPoint;
import org.openquark.cal.compiler.io.EntryPointSpec;
import org.openquark.cal.machine.StatusListener;


/**
 * This is the Compiler interface.
 *
 * Created: Mar 7, 2003
 * @author rcypher
 */
public interface Compiler {

    /**
     * Compile the specified adjunct and generate a corresponding entry point. 
     * @param adjunctSource - The source for the adjunct.  
     * @param entryPointSpec - Information about the target: name, i/o policies, etc.
     * @param targetModuleName - The name of the module to run in.
     * @param logger
     * @return The opaque identifier that the runtime uses to execute the target with proper marshaling.
     */
    public EntryPoint getEntryPoint (AdjunctSource adjunctSource, EntryPointSpec entryPointSpec, ModuleName targetModuleName, CompilerMessageLogger logger);
    
    /**
     * Generate an entry point for an existing function.
     * @param entryPointSpec - Information about the target: name, i/o policies, etc.
     * @param targetModuleName - the name of the module to run in
     * @param logger
     * @return The opaque identifier that the runtime uses to execute the target with proper marshaling.
     */
    public EntryPoint getEntryPoint (EntryPointSpec entryPointSpec, ModuleName targetModuleName, CompilerMessageLogger logger);

    /**
     * Compile the specified adjunct and generate a set of entry points.
     * @param adjunctSource - the source for the adjunct. 
     * @param entryPointSpecs - List of EntryPointSpec.  
     * @param targetModuleName - The name of the module to run in.
     * @param logger
     * @return List of EntryPoint
     */
    public List<EntryPoint> getEntryPoints (AdjunctSource adjunctSource, List<EntryPointSpec> entryPointSpecs, ModuleName targetModuleName, CompilerMessageLogger logger);
    
    /**
     * Generate an entry point for the named functions.
     * @param entryPointSpecs - list of EntryPointSpec
     * @param currentModule - name of the current module
     * @param logger
     * @return a list EntryPoint
     */
    public List<EntryPoint> getEntryPoints (List<EntryPointSpec> entryPointSpecs, ModuleName currentModule, CompilerMessageLogger logger);
    
    /**
     * Set the flag which indicates that code regeneration
     * should be forced.
     * @param b
     */
    public void setForceCodeRegen (boolean b);
    
    /**
     * Get the state of the flag that indicates whether
     * code regeneration should be forced.
     * @return boolean
     */
    public boolean getForceCodeRegen ();

    /**
     * Set the flag that indicates that things being compiled
     * will be used immediately.
     * @param b
     */
    public void setForImmediateUse (boolean b);
    
    /**
     * Get the state of the flag which indicates that
     * things being compiled will be used immediately.
     * @return boolean
     */
    public boolean isForImmediateUse ();
    
    /**
     * Add a status listener to the program generation process.
     * @param listener StatusListener
     */
    public void addStatusListener (StatusListener listener);
    
    /**
     * Remove a listener.
     * @param listener StatusListener
     */
    public void removeStatusListener (StatusListener listener);

}
