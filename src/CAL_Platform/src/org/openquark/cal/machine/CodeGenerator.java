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
 * CodeGenerator.java
 * Created: March 14, 2000
 * By: Raymond Cypher
 */
package org.openquark.cal.machine;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.openquark.cal.compiler.CompilerMessage;
import org.openquark.cal.compiler.CompilerMessageLogger;
import org.openquark.cal.compiler.ModuleName;


/**
 * Warning- this class should only be used by the CAL runtime implementation. It is not part of the
 * external API of the CAL platform.
 * <P>
 * This abstract class describes what CodeGenerator classes have to do.
 * It also provides the CodeGenerator factory method.
 * Creation date: (3/14/00 7:27:31 PM)
 * @author LWE, Raymond Cypher
 */
public abstract class CodeGenerator {

    /** A Set of status listeners. **/
    private final Set<StatusListener> statusListeners = new HashSet<StatusListener>();
    
    /** True if this CodeGenerator is generating code for an adjunct. */
    private final boolean isForAdjunct;
    
    protected CodeGenerator (boolean isForAdjunct) {
        this.isForAdjunct = isForAdjunct;
    }
    
    /**
     * Generate machine-appropriate code for all the supercombinators in the program.
     * @param module
     * @param logger
     * @return CompilerMessage.Severity
     */    
    abstract public CompilerMessage.Severity generateSCCode (Module module, CompilerMessageLogger logger);
    
    /**
     * Add a status listener to receive status messages.
     * @param listener
     */            
    public void addStatusListener (StatusListener listener) {
        if (!statusListeners.contains (listener)) {
            statusListeners.add (listener);
        }
    }      

    /**
     * Get the Set of status listeners.
     * @return an immutable List of StatusListener
     */
    protected Set<StatusListener> getStatusListeners () {
        return Collections.unmodifiableSet(statusListeners);
    }
    
    /**
     * Pass the status of a module to all status listeners.
     * @param moduleStatus
     * @param moduleName
     */
    protected void informStatusListeners (StatusListener.Status.Module moduleStatus, ModuleName moduleName) {
        for (final StatusListener statusListener : statusListeners) {
            statusListener.setModuleStatus(moduleStatus, moduleName);
        }
    }
    
    /**
     * Pass the status of an entity to all status listeners.
     * @param entityStatus
     * @param entityName
     */
    protected void informStatusListeners (StatusListener.Status.Entity entityStatus, String entityName) {
        for (final StatusListener statusListener : statusListeners) {
            statusListener.setEntityStatus(entityStatus, entityName);
        }
    }

    
    /**
     * Call this method when no further calls to generateSCCode will be made for this CodeGenerator.
     * This allows for finalization and cleanup of resources.
     * @param logger the logger to use for logging error messages.
     */
    public void finishedGeneratingCode(CompilerMessageLogger logger) {
    }
    
    public boolean isForAdjunct() {
        return isForAdjunct;
    }
}
