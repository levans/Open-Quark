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
 * CALRunner.java
 * Creation date: (5/24/01 8:55:36 AM)
 * By: Michael Cheng
 */
package org.openquark.cal.valuenode;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openquark.cal.machine.CALExecutor;
import org.openquark.cal.runtime.ExecutionContext;
import org.openquark.cal.services.WorkspaceManager;


/**
 * CALRunner is the abstract super class which is used to subclass other runners that
 * coordinate the actual work of running CAL code.
 * Creation date: (5/24/01 8:55:36 AM)
 * @author Michael Cheng
 */  
public abstract class CALRunner implements Runnable {      

    /** The namespace for log messages from the value nodes. */
    public static final String VALUENODE_LOGGER_NAMESPACE = "org.openquark.cal.valuenode";
    
    /** An instance of a Logger for value node messages. */
    static final Logger VALUENODE_LOGGER = Logger.getLogger(VALUENODE_LOGGER_NAMESPACE);
    
    static {
        VALUENODE_LOGGER.setLevel(Level.FINEST);
    }
    
    /** Default name of the sc to run */
    public static final String TEST_SC_NAME = "cdInternal_runTarget";
    
    protected CALExecutor runtime;
    private final WorkspaceManager workspaceManager;
    
    protected boolean programmaticErrorFlagged2;

    /**
     * Used to create a value node for the output.
     */
    protected ValueNodeBuilderHelper valueNodeBuilderHelper;
    
    /**
     * CALRunner Constructor.
     * @param workspaceManager the workspace manager for this runner.
     */
    public CALRunner(WorkspaceManager workspaceManager) {
        super();

        if (workspaceManager == null) {
            throw new NullPointerException("Argument 'workspaceManager' cannot be null.");
        }
        this.workspaceManager = workspaceManager;
    }

    /**
     * Return the workspace manager
     * @return WorkspaceManager the workspace manager
     */
    protected WorkspaceManager getWorkspaceManager() {
        return workspaceManager;
    }

    /**
     * Sets the object used to help build the output.  This will be passed onto the executor.
     * @param newValueNodeBuilderHelper 
     */
    public void setValueNodeBuilderHelper(ValueNodeBuilderHelper newValueNodeBuilderHelper) {
        valueNodeBuilderHelper = newValueNodeBuilderHelper;
    }

    /**
     * Runs the program.
     */
    public abstract void run();

    /**
     * Static function that can be used by other classes to create an executor of
     * the type appropriate to this session.
     * @param executionContext the execution context, or null to use a new one.
     */
    public final CALExecutor createExecutor (WorkspaceManager wkspcMgr, ExecutionContext executionContext) {
        
        return wkspcMgr.makeExecutor(executionContext);
    }
    /** 
     * Returns true if an  error has been encountered running the program.
     */
    public abstract boolean isErrorFlagged();

}
