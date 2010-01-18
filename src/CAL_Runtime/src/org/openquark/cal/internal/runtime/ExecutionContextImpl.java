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
 * ExecutionContextImpl.java
 * Creation date: Oct 23, 2006.
 * By: Edward Lam
 */
package org.openquark.cal.internal.runtime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.regex.Pattern;

import org.openquark.cal.foreignsupport.module.Prelude.UnitValue;
import org.openquark.cal.runtime.CalFunction;
import org.openquark.cal.runtime.CalValue;
import org.openquark.cal.runtime.Cleanable;
import org.openquark.cal.runtime.DebugSupport;
import org.openquark.cal.runtime.ExecutionContext;
import org.openquark.cal.runtime.ExecutionContextProperties;
import org.openquark.cal.runtime.ResourceAccess;


/**
 * See the superclass {@link ExecutionContext Javadoc} for a basic intro. 
 *
 * @author Bo Ilic, Raymond Cypher
 */
public abstract class ExecutionContextImpl extends ExecutionContext {
      
    //////////////////////////////////////////////////////////////////////
    //tracing related fields
    
    /** 
     * true if function tracing information should be printed to the console. 
     * If the the given machine configuration is not capable of tracing this flag has no effect.
     * Marked as volatile since the execution context may be shared by multiple threads in concurrent mode.
     */
    private volatile boolean tracingEnabled;
    
    /**
     * true if function tracing should also show the name of the thread that is evaluating the given function.
     * If the the given machine configuration is not capable of tracing this flag has no effect.
     * Marked as volatile since the execution context may be shared by multiple threads in concurrent mode.
     */
    private volatile boolean traceShowsThreadName = true;
    
    /**
     * true if function tracing should also show the arguments of the function being traced. This has the effect of
     * calling DebugSupport.showInternal on the arguments and does not modify the program evaluation state.
     * If the the given machine configuration is not capable of tracing this flag has no effect.
     * Marked as volatile since the execution context may be shared by multiple threads in concurrent mode.
     */    
    private volatile boolean traceShowsFunctionArgs = true;
    
    /**
     * When there is some text that is to be traced, and patterns is non-empty, then that text will
     * be traced only if it matches one of the patterns.
     * <P>
     * The algorithm is copy on write. The list in patterns is never to be modified. 
     * When the list is changed a new list must be saved in patterns.
     * This field must be marked as volatile (or have access to it synchronized) even though read/writes of
     * references are guaranteed to be atomic. See Effective Java item 48 pg 191.     
     * The only changer is the UI so there is only one thread doing writes. The list must be accessed by getting
     * the value of the list and saving it locally and then performing operations on the saved value. Item 48
     * allows this to function property. 
     */    
    private volatile List<Pattern> patterns = Collections.<Pattern>emptyList();
    
    /** 
     * The qualified names of functions for which tracing has been
     * specifically enabled. Since we have stated that an ExecutionContext cannot
     * be accessed by two threads simultaneously we don't need to synchronize the set.
     * <P>
     * The algorithm is copy on write. The list in tracedFunctions is never to be modified. 
     * When the list is changed a new list must be saved in tracedFunctions.
     * This field must be marked as volatile (or have access to it synchronized) even though read/writes of
     * references are guaranteed to be atomic. See Effective Java item 48 pg 191.     
     * The only changer is the UI so there is only one thread doing writes. The list must be accessed by getting
     * the value of the list and saving it locally and then performing operations on the saved value. Item 48
     * allows this to function property. 
     */ 
    private volatile Set<String> tracedFunctions = Collections.<String>emptySet();;
    
    
    //////////////////////////////////////////////////////////////////////
    //fundamental (non-debugging or tracing) fields   
    
    /** 
     * Provides access to information about entities in the workspace as needed to run the given CAL program.
     * In the case of non-standalone JARs, this is a wrapper on a Program and ResourceAccess objects. In the case of
     * Standalone JARs, it is something less- just sufficient to get the information needed to support the lookup or
     * reflection-like capabilities supported by Standalone JARs.
     */
    private RuntimeEnvironment runtimeEnvironment;
              
    /**
     * The ExecutionContextProperties instance encapsulating an immutable map of key-value pairs which is exposed
     * as system properties from within CAL.
     */
    private final ExecutionContextProperties properties;
    
    /**
     * A List of Cleanables representing the cleanup hooks that have been registered by CAL code to be run whenever
     * the execution context is cleaned up (explicitly via cleanup() or Program.resetCachedResults()).
     * 
     * This can be modified by calls to System.registerCleanable, and so access must be synchronized since
     * the execution context can be used by multiple threads in concurrent mode.
     */
    private final List<Cleanable> cleanables = new Vector<Cleanable>();
   
     
    /////////////////////////////////////////////////////////////////////
    //debuging fields      
    
    /** The debug controller. */
    private final DebugController debugController = new DebugController();
    
    /** Set of String: qualified names of functions and data constructors to break on. 
     * 
     * The algorithm is copy on write. The list in breakOnFunctions is never to be modified. 
     * When the list is changed a new list must be saved in breakOnFunctions.
     * This field must be marked as volatile (or have access to it synchronized) even though read/writes of
     * references are guaranteed to be atomic. See Effective Java item 48 pg 191.     
     * The only changer is the UI so there is only one thread doing writes. The list must be accessed by getting
     * the value of the list and saving it locally and then performing operations on the saved value. Item 48
     * allows this to function property. 
     */
    private volatile Set<String> breakOnFunctions = Collections.<String>emptySet();
              
    /** 
     * Flag indicating that Execution should proceed in a stepwise fashion. i.e. suspend on
     * each function or data constructor.
     */
    private volatile boolean stepping;

    /**
     * Indicates that all threads should be stepped. This is only checked when {@link #stepping} is true.
     */
    private volatile boolean stepAllThreads;
    
    /**
     * The set of threads that should be stepped. This is a copy-on-write set, and is thread-safe for all operations.
     * This set is only checked when {@link #stepping} is true and {@link #stepAllThreads} is false.
     */
    private final Set<Thread> threadsToBeStepped = new CopyOnWriteArraySet<Thread>();
    
           
    /**
     * Constructor for this abstract base class.
     * 
     * @param properties
     *            the ExecutionContextProperties instance encapsulating an immutable map of
     *            key-value pairs which is exposed as system properties from within CAL.
     */
    protected ExecutionContextImpl(ExecutionContextProperties properties) {
        this(properties, null);
    }
    
    /**
     * Constructor for this abstract base class.
     * 
     * @param properties
     *            the ExecutionContextProperties instance encapsulating an immutable map of
     *            key-value pairs which is exposed as system properties from within CAL.
     * @param runtimeEnvironment 
     */
    protected ExecutionContextImpl(ExecutionContextProperties properties, RuntimeEnvironment runtimeEnvironment) {
        
        if (properties == null) {
            throw new NullPointerException();
        }
        this.properties = properties;
        
        this.runtimeEnvironment = runtimeEnvironment;
        
    }            
    
    /** 
     * Check if debug processing is needed for the named function.
     * @param functionName
     * @return true if debug processing is required for the named function 
     * If the the given machine configuration is not capable of debugging this flag has no effect.
     */    
    public final boolean isDebugProcessingNeeded (String functionName) {
        
        final Set<String> tracedFunctions;//needed for thread safety, since this.tracedFunctions may change...  
        final Set<String> breakOnFunctions;
        return isTracingEnabled() || 
               ((tracedFunctions = this.tracedFunctions).size() > 0 && tracedFunctions.contains(functionName)) ||       
               stepping ||                
               ((breakOnFunctions = this.breakOnFunctions).size() > 0 && breakOnFunctions.contains(functionName));
    }
    
    public final void debugProcessing (String functionName, CalValue[] argValues) {
        // Do trace if necessary 
        final Set<String> tracedFunctions; //needed for thread safety, since this.tracedFunctions may change...     
        if (isTracingEnabled() || ((tracedFunctions = this.tracedFunctions).size() > 0 && tracedFunctions.contains(functionName))) {
            final StringBuilder $sb;
            
            if (traceShowsThreadName()) {
                $sb = new StringBuilder(Thread.currentThread().getName()).append("> ");
            } else {
                $sb = new StringBuilder();           
            } 
            
            $sb.append(functionName);
            
            if (traceShowsFunctionArgs()) {
                $sb.append(DebugSupport.showInternalForArgumentValues(
                    argValues));                 
            }
            
            trace($sb.toString());
        }
        
        // Do suspend if necessary
        final Set<String> breakOnFunctions;
        if (stepping) {
            if (stepAllThreads || threadsToBeStepped.contains(Thread.currentThread())) {
                suspendInternal(functionName, argValues);
            }
        } else if ((breakOnFunctions = this.breakOnFunctions).size() > 0 && breakOnFunctions.contains(functionName)) {
            suspendInternal(functionName, argValues);            
        }
    }

    /**
     * @return true if function tracing information should be printed to the console. 
     * If the the given machine configuration is not capable of tracing this flag has no effect.
     */    
    public final boolean isTracingEnabled() {
        return tracingEnabled;
    }    
    public final void setTracingEnabled(final boolean tracingEnabled) {
        this.tracingEnabled = tracingEnabled;
    }
    
    /**
     * @return true if function tracing should also show the name of the thread that is evaluating the given function.
     * If the the given machine configuration is not capable of tracing this flag has no effect.
     */    
    public final boolean traceShowsThreadName() {
        return traceShowsThreadName;
    }    
    public final void setTraceShowsThreadName(boolean traceShowsThreadName) {
        this.traceShowsThreadName = traceShowsThreadName;
    }
    
    /**
     * @return true if function tracing should also show the arguments of the function being traced. This has the effect of
     * calling DebugSupport.showInternal on the arguments and does not modify the program evaluation state.
     * If the the given machine configuration is not capable of tracing this flag has no effect.
     */       
    public final boolean traceShowsFunctionArgs() {
        return traceShowsFunctionArgs;
    }
    public final void setTraceShowsFunctionArgs(boolean traceShowsFunctionArgs) {
        this.traceShowsFunctionArgs = traceShowsFunctionArgs;
    }       
    
    /**
     * Set the list of regular expressions to use for filtering trace output. A copy of the
     * list is made. The regular expression are immutable so the are not copied but rather 
     * directly stored in the list.
     * 
     * @param patterns The list of regular expression to use for the filter.
     */
    public final void setTraceFilters(List<Pattern> patterns){
        if (patterns == null) {
            throw new NullPointerException("'patterns' cannot be null.");
        }
        this.patterns = new ArrayList<Pattern>(patterns);
    }    
    public final void clearTraceFields() {
        this.patterns = Collections.<Pattern>emptyList();
    }    
    /**
     * Retrieve an immutable version of the list of regular expressions used to filter
     * trace output.
     * @return List - the List of regular expressions used to filter trace output.
     */
    public final List<Pattern> getTraceFilters () {
        return Collections.unmodifiableList(patterns);
    }
    
    /**
     * This function is used to print trace messages to the console.
     * 
     * @param traceMessage The message to be written to the console.
     */
    
    public final void trace (String traceMessage){
        
        //must assign to a local variable to avoid needing to explicitly synchronize access to patterns.
        //this is because this.patterns may change between checking the size, and iteration.
        List<Pattern> patterns = this.patterns;
                       
        // maintain the original behaviour
        if (patterns.size() == 0){
            System.out.println(traceMessage);
            return;
        }

        // there are some patterns only print out traces that match 
        for(final Pattern pattern : patterns){
            if (pattern.matcher(traceMessage).matches()){
                System.out.println(traceMessage);
            }
        }        
    }

    /**
     * Set the RuntimeEnvironment instance in which the execution context is
     * operating.
     * @param runtimeEnvironment
     */
    protected final void setRuntimeEnvironment (RuntimeEnvironment runtimeEnvironment) {
        
        this.runtimeEnvironment = runtimeEnvironment;
    }
    
    /**    
     * @return the runtime environment.
     */
    protected final RuntimeEnvironment getRuntimeEnvironment () {
        return runtimeEnvironment;
    }
    
    /**
     * This function is referenced by Cal.Core.Resource.getResourceAccess.
     * @return the ResourceAccess instance to provide access to the resources of the current
     *         environment (e.g. from the workspace, or from Eclipse).
     */   
    public final ResourceAccess getResourceAccess() {
        return runtimeEnvironment.getResourceAccess();
    }
    
    /**
     * @return the ExecutionContextProperties instance encapsulating an immutable map of key-value pairs which is exposed
     *         as system properties from within CAL.
     */   
    public final ExecutionContextProperties getProperties() {
        return properties;
    }
    
    /**
     * Retrieves the Class object associated with a CAL foreign type.
     *      
     * @param qualifiedTypeConsName the fully-qualified type constructor name as a String e.g. "Cal.Core.Prelude.Maybe".
     * @param foreignName the name of the foreign class as returned by <code>Class.getName()</code>.
     * @return Class of the foreign type constructor, or null if not a foreign type.
     * @throws Exception if the class cannot be resolved.
     */   
    public final Class<?> getForeignClass(final String qualifiedTypeConsName, final String foreignName) throws Exception {        
        return runtimeEnvironment.getForeignClass(qualifiedTypeConsName, foreignName);
    }

    /**
     * Registers a cleanup hook to be run whenever the execution context is cleaned up
     * (explicitly via cleanup() or Program.resetCachedResults()).
     * 
     * @param cleanable the cleanup hook
     * @throws NullPointerException if the cleanup hook is null.
     */  
    public final void registerCleanable(Cleanable cleanable) {
        if (cleanable == null) {
            throw new NullPointerException();
        }
        cleanables.add(cleanable);
    }

    /**
     * Registers a cleanup hook to be run whenever the execution context is cleaned up
     * (explicitly via cleanup() or Program.resetCachedResults()).
     * The specified CAL function will be called on cleanup.
     * 
     * @param cleanupFunction  the CAL cleanup function
     * @throws NullPointerException if the cleanup hook is null.
     */
    public final void registerCleanable(final CalFunction /* ()->() */cleanupFunction) {
        if (cleanupFunction == null) {
            throw new NullPointerException();
        }
        registerCleanable(new Cleanable() {
            public void cleanup() {
                // Invoke the CAL cleanup function.
                // The argument value will be ingored anyway.
                cleanupFunction.evaluate(UnitValue.UNIT);
            }
        });
    }

    /**
     * Runs the cleanup hooks that have been registered with this instance, in the order in which they
     * were originally registered.
     * 
     * Calling this method has the effect of clearing the list of cleanup hooks afterwards (so that they don't
     * get run again unless explicitly re-registered).
     * 
     * This method does not reset cached CAFs.
     */
    @Override
    public final void cleanup() {
        
        synchronized (cleanables) {

            for (final Cleanable cleanable : cleanables) {
                cleanable.cleanup();
            }
            cleanables.clear();
        }
    }
              
    /**
     * @return an unmodifiable Set<String> containing the qualified
     * names of functions for which tracing has been specifically enabled.
     */
    public final Set<String> getTracedFunctions () {
        return Collections.unmodifiableSet(tracedFunctions);
    }    
    /**     
     * @param tracedFunctions qualified names of the functions for which tracing
     * is specifically enabled. Must not be null. The argument set is copied.
     */
    public final void setTracedFunctions(Set<String> tracedFunctions) {
        if (tracedFunctions == null) {
            throw new NullPointerException("argument 'tracedFunctions' cannot be null.");
        }

        this.tracedFunctions = new HashSet<String>(tracedFunctions);
    }
    public final void clearTracedFunctions() {
        this.tracedFunctions = Collections.<String>emptySet();
    }
    /**     
     * Add a new function to the set of functions for which 
     * tracing is specifically enabled.
     * Note: this will supersede the general tracing setting.
     * 
     * Warning: this method is inefficient for adding multiple functions since the underlying
     * list is copy-on-write.
     * 
     * @param tracedFunction - the qualified name of the function 
     *     for which tracing should be enabled. 
     */
    public final void addTracedFunction (String tracedFunction) { 
        if (tracedFunction == null) {
            throw new NullPointerException("tracedFunction cannot be null.");
        }
        
        Set<String> oldTracedFunctions = getTracedFunctions();
        if (!oldTracedFunctions.contains(tracedFunction)) {
            Set<String> newTracedFunctions = new HashSet<String>(oldTracedFunctions);
            newTracedFunctions.add(tracedFunction);
            setTracedFunctions(newTracedFunctions);
        }  
    }    
    /**
     * Remove a function from the set of functions for which 
     * tracing is specifically enabled.
     * 
     * Warning: this method is inefficient for adding multiple functions since the underlying
     * list is copy-on-write.
     * 
     * @param tracedFunction - the qualified name of the function
     *     to remove from the set of traced functions.
     */
    public final void removeTracedFunction (String tracedFunction) {
        if (tracedFunction == null) {
            throw new NullPointerException("tracedFunction cannot be null.");
        }
        
        Set<String> oldTracedFunctions = getTracedFunctions();
        if (oldTracedFunctions.contains(tracedFunction)) {
            Set<String> newTracedFunctions = new HashSet<String>(oldTracedFunctions);
            newTracedFunctions.remove(tracedFunction);
            setTracedFunctions(newTracedFunctions);
        }  
    }
    
    /**
     * @return an unmodifiable String Set of the qualified names of the functional agents on which breakpoints are set.
     */
    public final Set<String> getBreakpoints () {
        return Collections.unmodifiableSet(this.breakOnFunctions);
    }
    /**     
     * @param breakpoints qualified names of the functional agents on which breakpoints are set. Cannot be null.
     *    The argument set is copied.
     */
    public final void setBreakpoints(Set<String> breakpoints) {

        if (breakpoints == null) {
            throw new NullPointerException("Argument 'breakpoints' cannot be null.");
        }

        this.breakOnFunctions = new HashSet<String>(breakpoints);
    }    
    public final void clearBreakpoints() {
        this.breakOnFunctions = Collections.<String>emptySet();
    }
       
      
    
    /**
     * Build up a Suspension object which describes the state of the CAL function
     * from which suspension was initiated and then suspend the calling thread
     * until it is notified to continue.
     * @param functionName - the CAL function calling suspend.
     * @param argValues - the argument values for the CAL function calling suspend.
     */
    private void suspendInternal(String functionName, CalValue[] argValues) {
        String[][] argNamesAndTypes = runtimeEnvironment.getArgNamesAndTypes(functionName);  
        //todoBI the old code used to pass an empty CalValue array if anything bad happened in getArgNamesAndTypes. This
        //seems incorrect, since indeed we know how many args we actually have.
        suspendInternal(functionName, argNamesAndTypes[0], argNamesAndTypes[1], argValues);
    }

    /**
     * Build up a Suspension object which describes the state of the CAL function
     * from which suspension was initiated and then suspend the calling thread
     * until it is notified to continue.
     * @param functionName - the CAL function calling suspend.
     * @param argNames - the argument names for the CAL function calling suspend.
     * @param argTypes - the argument types for the the CAL function calling suspend.
     * @param argValues - the argument values for the CAL function calling suspend.
     */
    private void suspendInternal (String functionName, String[] argNames, String[] argTypes, CalValue[] argValues) {
        // We throw and catch a NullPointerException as a simple way of
        // getting a stack trace for this thread.
        try {
            throw new NullPointerException();
        } catch (NullPointerException e) {

            // We want to strip off the top of the stack trace the calls into the execution context.
            // We want to show the user the stack trace at the point the call to suspend originated.
            StackTraceElement[] stackTrace = e.getStackTrace();
            for (int i = 0; i < stackTrace.length; ++i) {
                StackTraceElement ste = stackTrace[i];
                if (ste.getClassName().indexOf("cal_") >= 0) {
                    if (i > 0) {
                        StackTraceElement temp[] = new StackTraceElement[stackTrace.length - i];
                        System.arraycopy(stackTrace, i, temp, 0, temp.length);
                    }
                    break;
                }
            }

            // Create the suspension description.
            final SuspensionState suspensionState = new SuspensionState(functionName, argNames, argTypes, argValues, stackTrace);

            // Suspend the current thread.
            debugController.suspendCurrentThread(suspensionState);
        }
    }
    
    
    /**
     * Allow all suspended CAL execution threads to resume.
     * This should be called by debugging clients. 
     */
    public void resumeAll() {
        debugController.resumeAll();
    }
    
    /**
     * Resumes the specified suspended thread. If the thread is not suspended then this is a no-op.
     * This should be called by debugging clients.
     * @param thread the suspended thread.
     */
    public void resumeThread(final Thread thread) {
        debugController.resumeThread(thread);
    }

    public final boolean isStepping() {
        return stepping;
    }

    public final void setStepping(boolean stepping) {
        if (!stepping) {
            // reset the state if not stepping
            stepAllThreads = false;
            threadsToBeStepped.clear();
        }
        this.stepping = stepping;
    }
    
    /**
     * Sets the flag that indicates that all threads should be stepped. 
     * This should be called by debugging clients.
     * @param value the new value.
     */
    public final void setStepAllThreads(boolean value) {
        this.stepAllThreads = value;
    }
    
    /**
     * Adds a thread to the set of threads that should be stepped.
     * This should be called by debugging clients.
     * @param thread a thread to be stepped.
     */
    public final void addThreadToBeStepped(final Thread thread) {
        threadsToBeStepped.add(thread);
    }
    
    /**
     * Removes a thread to the set of threads that should be stepped.
     * This should be called by debugging clients.
     * @param thread a thread not to be stepped.
     */
    public final void removeThreadToBeStepped(final Thread thread) {
        threadsToBeStepped.remove(thread);
    }
    
    /**
     * Adds a debug listener (a debugging client).
     * @param listener a debug listener.
     */
    public void addDebugListener(final DebugListener listener) {
        debugController.addDebugListener(listener);
    }
    
    /**
     * Removes a debug listener (a debugging client).
     * @param listener a debug listener.
     */
    public void removeDebugListener(final DebugListener listener) {
        debugController.removeDebugListener(listener);
    }
    
    /**
     * @return a map from suspended threads to their suspension states.
     */
    public Map<Thread, SuspensionState> getThreadSuspensions() {
        return debugController.getThreadSuspensions();
    }
    
    /**
     * @return whether there are any suspended threads.
     */
    public boolean hasSuspendedThreads() {
        return debugController.hasSuspendedThreads();
    }
    
    /**
     * Returns whether the given thread is suspended.
     * @param thread the thread to check.
     * @return true if the thread is suspended.
     */
    public boolean isThreadSuspended(final Thread thread) {
        return debugController.isThreadSuspended(thread);
    }
    
    /**
     * Represents a listener interested in debugging-related events.
     *
     * @author Joseph Wong
     */
    public static interface DebugListener {
        
        /**
         * Handles the event of a thread being suspended.
         * @param thread the thread being suspended.
         * @param suspensionState the suspension state of the thread.
         */
        void threadSuspended(Thread thread, SuspensionState suspensionState);
    }
 
    /**
     * A controller for managing the suspension and resuming of execution.
     *
     * @author Joseph Wong
     */
    private static final class DebugController {
        
        /** A map from suspended threads to their suspension states. */
        private final Map<Thread, SuspensionState> threadSuspensions = new IdentityHashMap<Thread, SuspensionState>();
        
        /**
         * The set of debug listeners. The use of a CopyOnWriteArraySet means that this collection
         * is thread-safe for traversal operations.
         */
        private final CopyOnWriteArraySet<DebugListener> listeners = new CopyOnWriteArraySet<DebugListener>();
        
        /**
         * Adds a debug listener (a debugging client).
         * @param listener a debug listener.
         */
        synchronized void addDebugListener(final DebugListener listener) {
            listeners.add(listener);
        }
        
        /**
         * Removes a debug listener (a debugging client).
         * @param listener a debug listener.
         */
        synchronized void removeDebugListener(final DebugListener listener) {
            listeners.remove(listener);
        }

        /**
         * Suspends the current thread.
         * @param suspensionState the associated suspension state.
         */
        void suspendCurrentThread(final SuspensionState suspensionState) {
            final Thread currentThread = Thread.currentThread();
            
            synchronized (this) {
                // put the thread in the suspension map first... this is important, since
                // during the upcall the client may decide to resume immediately,
                // and so there should be something in the map for the client to remove
                // and the absence of the entry would be a signal to the code below
                // to not even bother with the first wait()
                threadSuspensions.put(currentThread, suspensionState);
            }
            
            // make upcalls while not holding the lock... 
            for (final DebugListener listener : listeners) {
                listener.threadSuspended(currentThread, suspensionState);
            }
            
            // actually suspend the thread
            synchronized (this) {
                try {
                    // If the map still has the key, the client has not resumed the thread, so call wait().
                    // Note that it is possible for the loop body to not execute even once, if the client
                    // calls resumeThread() or resumeAll() during the upcall period.
                    while (threadSuspensions.containsKey(currentThread)) {
                        wait();
                    }

                } catch (final InterruptedException e) {
                    threadSuspensions.remove(currentThread);
                }
            }
        }
        
        /**
         * Resumes all suspended threads.
         */
        synchronized void resumeAll() {
            threadSuspensions.clear();
            notifyAll();
        }
        
        /**
         * Resumes the specified suspended thread. If the thread is not suspended then this is a no-op.
         * @param thread the suspended thread.
         */
        synchronized void resumeThread(final Thread thread) {
            threadSuspensions.remove(thread);
            notifyAll();
        }
        
        /**
         * @return a map from suspended threads to their suspension states.
         */
        synchronized Map<Thread, SuspensionState> getThreadSuspensions() {
            return new IdentityHashMap<Thread, SuspensionState>(threadSuspensions);
        }
        
        /**
         * @return whether there are any suspended threads.
         */
        synchronized boolean hasSuspendedThreads() {
            return !threadSuspensions.isEmpty();
        }
        
        /**
         * Returns whether the given thread is suspended.
         * @param thread the thread to check.
         * @return true if the thread is suspended.
         */
        synchronized boolean isThreadSuspended(final Thread thread) {
            return threadSuspensions.containsKey(thread);
        }
    }

    /**
     * Description of the state of a CAL program at the point that suspension was
     * initiated.
     * @author RCypher
     *
     */
    public static final class SuspensionState {
        /** The name of the CAL function which initiated suspension. */
        private final String functionName;
        
        /** The argument names for the CAL function which initiated suspension. */
        private final String[] argNames;
        
        /** The argument values for the CAL function at the point where suspension was initiated. */
        private final CalValue[] argValues;
        
        /** Descriptions of the types of the arguments to the CAL function. */
        private final String[] argTypes;
        
        /** The stack trace at the point suspension was initiated. */
        private final StackTraceElement[] stackTrace;
        
        private SuspensionState(String functionName, String[] argNames,
                String[] argTypes, CalValue[] argValues,
                StackTraceElement[] stackTrace) {
            this.functionName = functionName;
            this.argNames = argNames;
            this.argValues = argValues;
            this.argTypes = argTypes;
            this.stackTrace = stackTrace;
        }

        /**
         * @return The name of the CAL function which initiated suspension. 
         */
        public String getFunctionName() {
            return functionName;
        }

        /**
         * @return The argument names for the CAL function which initiated suspension.
         */
        public String[] getArgNames() {
            return argNames.clone();
        }

        /**
         * @return The argument values for the CAL function at the point where suspension was initiated.
         */
        public CalValue[] getArgValues() {
            return argValues.clone();
        }
        
        /**
         * @return Descriptions of the types of the arguments to the CAL function.
         */
        public String[] getArgTypes () {
            return argTypes.clone();
        }
        
        /**
         * @return The stack trace at the point suspension was initiated. 
         */
        public StackTraceElement[] getStackTrace() {
            return stackTrace.clone();
        }
    }
    
    
}
