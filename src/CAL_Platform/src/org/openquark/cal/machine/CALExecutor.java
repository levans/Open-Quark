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
 * CALExecutor.java
 * Creation date: (March 27/01 8:30:03 AM)
 * By: Luke Evans
 */
package org.openquark.cal.machine;

import java.util.List;
import java.util.regex.Pattern;

import org.openquark.cal.compiler.io.EntryPoint;
import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.runtime.ExecutionContext;


/**
 * The interface supported by all CAL Executors, regardless of operating machine.
 * Creation date: (3/27/01 8:30:03 AM)
 * @author Luke Evans
 */
public interface CALExecutor {
    
    /**
     * Execute the program from the specified entry point.
     * @param entryPoint
     * @param arguments (null is accepted and used as the 0-length Object array).
     * @return the value resulting from the execution
     * @throws CALExecutorException
     */
    public Object exec(EntryPoint entryPoint, Object[] arguments) throws CALExecutorException;

    /**
     * Register a stats generator.
     * @param gen
     */
    public void addStatsGenerator (StatsGenerator gen);

    /**
     * Remove a stats generator.
     * @param gen
     */
    public void removeStatsGenerator (StatsGenerator gen);

    /**
     * Ask the runtime to quit.
     * Note that you can only ask the runtime to quit; you can't un-quit - we may want to have something like this later
     * if we want to implement "restart."
     * Creation date: (3/21/02 1:39:40 PM)
     */
    public void requestQuit();

    /**
     * Ask the runtime to suspend.
     * If the running build is not debug capable this
     * will have no effect.
     * Debug capability is enabled via the system 
     * property: org.openquark.cal.runtime.debug_capable
     */
    public void requestSuspend();

    /**
     * @return the context of execution being used by this CALExecutor.
     */
    public ExecutionContext getContext ();

    /**
     * Set the regular expressions that the tracing code uses to filter the traces.
     * 
     * @param patterns The list of regular expressions to use when filtering traces.
     */

    public void setTraceFilters(List<Pattern> patterns);

}