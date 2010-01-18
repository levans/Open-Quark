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
 * JavaGenerator.java
 * Creation date: Sep 11, 2003.
 * By: Edward Lam
 */
package org.openquark.cal.internal.machine.lecc;

import java.util.HashSet;
import java.util.Set;

import org.openquark.cal.compiler.CompilerMessageLogger;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.TypeConstructor;
import org.openquark.cal.internal.machine.CodeGenerationException;
import org.openquark.cal.machine.StatusListener;

 
/**
 * A JavaGenerator generates Java constructs such as source or bytecodes.
 * @author Edward Lam
 */
abstract class JavaGenerator {

    /** A List of StatusListener */
    private Set<StatusListener> statusListeners = new HashSet<StatusListener>();

    /** The object used to collect statistics about the generated code.  May be null. */
    private CodeGenerationStats codeGenerationStats = null;
    
    /**
     * Generate the functional form of a given machine function.
     * @param functionGroupInfo
     * @param forceWrite
     * @param logger the logger to use for logging error messages.
     * @throws CodeGenerationException 
     */                    
    abstract void createFunction(LECCModule.FunctionGroupInfo functionGroupInfo, boolean forceWrite, CompilerMessageLogger logger) throws CodeGenerationException;

    /**
     * Generate a type constructor definition, along with appropriate subclasses for its data constructors.
     * @param typeConstructor the type constructor
     * @param forceWrite
     * @param logger the logger to use for logging error messages.
     * @throws CodeGenerationException 
     */                    
    abstract void createTypeDefinition(TypeConstructor typeConstructor, boolean forceWrite, CompilerMessageLogger logger) throws CodeGenerationException;
        
    /**
     * Wrap up.
     * eg. at this point the source generator might compile all the files that have been generated..
     * @throws CodeGenerationException
     */
    abstract void wrap() throws CodeGenerationException;
    
    /** Set the list of status listeners.
     * @param statusListeners - a List of StatusListener  
     */
    void setStatusListeners (Set<StatusListener> statusListeners) {
        this.statusListeners = new HashSet<StatusListener>(statusListeners);
    }
    
    void informStatusListeners (StatusListener.Status.Module moduleStatus, ModuleName moduleName) {
        for (final StatusListener statusListener : statusListeners) {
            statusListener.setModuleStatus(moduleStatus, moduleName);
        }
    }
    
    void informStatusListeners (StatusListener.Status.Entity entityStatus, String entityName) {
        for (final StatusListener statusListener : statusListeners) {
            statusListener.setEntityStatus(entityStatus, entityName);
        }
    }
    
    /**
     * @return Returns the generatedClassFiles.
     */
    Set<String> getGeneratedClassFiles() {
        return new HashSet<String>();
    }
    
    /**
     * @return the CodeGenerationStats from this JavaGenerator
     */
    CodeGenerationStats getCodeGenerationStats () {
        return codeGenerationStats;
    }
    
    /**
     * Set the object used to collect code generation info.
     * @param codeGenerationStats - the object for collecting code generation info.  May be null.
     */
    void setCodeGenerationStatsCollector (CodeGenerationStats codeGenerationStats) {
        this.codeGenerationStats = codeGenerationStats;
    }
}
