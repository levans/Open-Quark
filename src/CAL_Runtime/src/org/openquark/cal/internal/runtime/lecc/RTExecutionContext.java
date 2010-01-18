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
 * RTExecutionContext.java
 * Created: Dec 7, 2004
 * By: RCypher
 */

package org.openquark.cal.internal.runtime.lecc;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.internal.runtime.ExecutionContextImpl;
import org.openquark.cal.internal.runtime.RuntimeEnvironment;
import org.openquark.cal.runtime.ExecutionContextProperties;


/**
 * This is the class the lecc machine uses to track execution statistics.
 * It is also used to associate state with a thread of execution.
 * Currently this state consists of cached CAF values.
 * @author RCypher
 */
public final class RTExecutionContext extends ExecutionContextImpl {

    private static final int ACTION_CONTINUE = 0;
    private static final int ACTION_QUIT = 1;


    // Some member/methods used for collecting runtime statistics.
    //Since the execution context can be accessed by multiple threads while executing in concurrency enabled mode, 
    //these must be synchronized.
    private final AtomicInteger nReductions = new AtomicInteger(-1);
    private final AtomicInteger nMethodCalls = new AtomicInteger(-1);
    private final AtomicInteger nDataTypeInstances = new AtomicInteger(-1);          
    //use ConcurretMap/AtomicInteger to get synchronization + efficient creation of count objects
    private final ConcurrentMap<QualifiedName, AtomicInteger> callCounts = new ConcurrentHashMap<QualifiedName, AtomicInteger>(); 
    private final ConcurrentMap<QualifiedName, AtomicInteger> dcConstructorCounts = new ConcurrentHashMap<QualifiedName, AtomicInteger>();
    private final ConcurrentMap<QualifiedName, AtomicInteger> dcFunctionCounts = new ConcurrentHashMap<QualifiedName, AtomicInteger>();

    /**
     * Used by the client to tell the executor to stop prematurely.
     * Must be marked as volatile (or have access to it synchronized) even though read/writes of
     * ints are guaranteed to be atomic. See Effective Java item 48 pg 191.
     * We limit direct checking of quit (see the code below) since it can be quite expensive to
     * access a volatile field.
     */
    private volatile int continueAction = ACTION_CONTINUE;

    /**
     * Internal flag for tracking the number of times that a client tries to check the quit flag.
     * We use this to limit actually accessing the volatile boolean, since this can be quite expensive.
     */
    private int checkQuitCount = 0;

    /** Interval at which the actual volatile boolean quit flag is accessed. */
    private static final int CHECK_QUIT_INTERVAL = 20;


    /**
     * Constructs an instance of this class with the specified properties.
     * @param properties the properties to be associated with the execution context.
     * @param runtimeEnvironment
     */
    public RTExecutionContext(ExecutionContextProperties properties, RuntimeEnvironment runtimeEnvironment) {
        super(properties, runtimeEnvironment);
    }    

    /**
     * Request that the runtime terminate evaluation.
     */
    public void requestQuit () {
        continueAction = ACTION_QUIT;
    }

    /**
     * Request that the runtime suspend evaluation.
     */
    public void requestSuspend() {
        setStepAllThreads(true);
        setStepping(true);
    }

    /**
     * Was the runtime asked to quit? Note that if SourceGenerationConfiguration.NON_INTERRUPTIBLE_RUNTIME
     * is true then even if a quit was requested, the runtime will ignore this request.
     * Creation date: (3/21/02 2:18:40 PM)
     * @return boolean
     */
    public boolean isQuitRequested() {
        if (checkQuitCount++ > CHECK_QUIT_INTERVAL) {
            checkQuitCount = 0;
            if (continueAction == ACTION_CONTINUE) {
                return false;
            } else if (continueAction == ACTION_QUIT) {
                return true;
            }
        }
        return false;
    }

    /*
     * Some methods used to track runtime statistics.
     */
    public final void incrementNReductions(){
        nReductions.incrementAndGet();        
    }
    public final void incrementNMethodCalls(){
        nMethodCalls.incrementAndGet();
    }
    public final void incrementNDataTypeInstances(){
        nDataTypeInstances.incrementAndGet();
    }

    public final void scCalled (String moduleName, String unqualifiedName) {
        incrementCounts(moduleName, unqualifiedName, callCounts);         
    }
    public final void dcConstructorCalled (String moduleName, String unqualifiedName) {
        incrementCounts(moduleName, unqualifiedName, dcConstructorCounts);      
    }
    public final void dcFunctionCalled (String moduleName, String unqualifiedName) {
        incrementCounts(moduleName, unqualifiedName, dcFunctionCounts);        
    }
    private static void incrementCounts(
            final String moduleName,
            final String unqualifiedName,
            final ConcurrentMap<QualifiedName, AtomicInteger> countsMap) {
        
        final QualifiedName qualifiedName = QualifiedName.make(ModuleName.make(moduleName), unqualifiedName);
        AtomicInteger callCount = countsMap.get(qualifiedName);
        if (callCount != null) {
            callCount.incrementAndGet();
        } else {
            callCount = countsMap.putIfAbsent(qualifiedName, new AtomicInteger(1));
            if (callCount != null) {
                callCount.incrementAndGet();
            }
        }      
    }
    
    /**
     * @return Returns the nReductions.
     */
    public final int getNReductions() {
        return nReductions.get();
    }
    /**
     * @return Returns the nMethodCalls.
     */
    public final int getNMethodCalls() {
        return nMethodCalls.get();
    }
    /**
     * @return Returns the nDataTypeInstances.
     */
    public final int getNDataTypeInstances() {
        return nDataTypeInstances.get();
    }
    /**
     * @return Returns the callCounts. This is a copy and can be freely modified.
     */
    public final Map<QualifiedName, Integer> getCallCounts() {
        return copyMap(callCounts);        
    }
    /**
     * @return Returns the dcConstructorCounts. This is a copy and can be freely modified.
     */
    public final Map<QualifiedName, Integer> getDcConstructorCounts() {
        return copyMap(dcConstructorCounts);        
    }
    /**
     * @return Returns the dcFunctionCounts. This is a copy and can be freely modified.
     */
    public final Map<QualifiedName, Integer> getDcFunctionCounts() {
        return copyMap(dcFunctionCounts);
    }
    
    private static Map<QualifiedName, Integer> copyMap(ConcurrentMap<QualifiedName, AtomicInteger> map) {
        Map<QualifiedName, Integer> result = new HashMap<QualifiedName, Integer>();
        for (Map.Entry<QualifiedName, AtomicInteger> entry : map.entrySet()) {
            result.put(entry.getKey(), entry.getValue().intValue());
        }
        
        return result;
    }

    /**
     * Reset the fields of the execution context.
     * @param newRuntimeEnvironment  
     */
    public void reset (RuntimeEnvironment newRuntimeEnvironment) {
        if (newRuntimeEnvironment == null) {
            throw new NullPointerException("Invalid RuntimeEnvironment reference in RTExecutionContext.");
        }       

        nReductions.set(-1);
        nMethodCalls.set(-1);
        nDataTypeInstances.set(-1);
        callCounts.clear();
        dcConstructorCounts.clear();
        dcFunctionCounts.clear();
        continueAction = ACTION_CONTINUE;
        setRuntimeEnvironment(newRuntimeEnvironment);
    }

    /**
     * Null out the super.runtimeEnvironment field.
     * This prevents the execution context from holding on to
     * an out-of-date instance of RuntimeEnvironment.
     */
    public void clearRuntimeEnvironment() {
        setRuntimeEnvironment(null);
    }
}

