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
 * ExecutionContext.java
 * Created: Feb 2, 2005
 * By: Bo Ilic
 */
package org.openquark.cal.runtime;


/**
 * Provides an environment in which the execution of a CAL function occurs.
 * One of the most important tasks of an execution context is to provide a key to the
 * set of constant applicative form (CAF) values that are in use for the given execution.
 * <p>
 * In general, a single ExecutionContext object can be used by only 1 thread at a time.
 * It is OK however for a thread to run a program using an execution context, and then when that run is
 * completed for another thread to use the same execution context to run another program. This provides
 * the ability to share CAF values between multiple executions, even if the further executions are on
 * another thread.
 * <p>
 * Similarly the state of the execution context cannot be altered by one thread (e.g. changing a debugging
 * flag, etc.) while another thread is using the execution context to run a program.
 * <p> 
 * The execution context is also a holder for debugging status flags that are dependent on an execution.
 * <p>
 * The execution context holds onto an immutable set of properties which is specified by client code
 * on construction of the execution context, and can be accessed from within CAL. Some well-known properties
 * are defined by the platform (e.g. the current locale).
 * <p>
 * The execution context can also be used to interrupt an executing CAL program without affecting other
 * concurrently executing CAL programs.
 *   
 * @author Bo Ilic
 */
public abstract class ExecutionContext {
           
    /**
     * Runs the cleanup hooks that have been registered with this instance, in the order in which they
     * were originally registered.
     * 
     * Calling this method has the effect of clearing the list of cleanup hooks afterwards (so that they don't
     * get run again unless explicitly re-registered).
     * 
     * This method does not reset cached CAFs.
     */
    public abstract void cleanup();      
}
