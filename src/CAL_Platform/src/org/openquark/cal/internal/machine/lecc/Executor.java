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
 * Executor.java
 * Created: Jan 27, 2003 at 2:16:12 PM
 * By: RCypher
 */
package org.openquark.cal.internal.machine.lecc;

import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.openquark.cal.compiler.DataConstructor;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.compiler.io.EntryPoint;
import org.openquark.cal.internal.machine.DynamicRuntimeEnvironment;
import org.openquark.cal.internal.machine.EntryPointImpl;
import org.openquark.cal.internal.runtime.lecc.LECCMachineConfiguration;
import org.openquark.cal.internal.runtime.lecc.RTApplication;
import org.openquark.cal.internal.runtime.lecc.RTData;
import org.openquark.cal.internal.runtime.lecc.RTExecutionContext;
import org.openquark.cal.internal.runtime.lecc.RTFullApp;
import org.openquark.cal.internal.runtime.lecc.RTOApp2;
import org.openquark.cal.internal.runtime.lecc.RTOApp3;
import org.openquark.cal.internal.runtime.lecc.RTPartialApp;
import org.openquark.cal.internal.runtime.lecc.RTValue;
import org.openquark.cal.machine.CALExecutor;
import org.openquark.cal.machine.MachineFunction;
import org.openquark.cal.machine.Program;
import org.openquark.cal.machine.StatsGenerator;
import org.openquark.cal.machine.StatsGenerator.StatsObject;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.runtime.ExecutionContext;
import org.openquark.cal.runtime.ResourceAccess;


/**
 * This is the Executor for the LECC 'machine'
 * <p>
 * Creation: Jan 27, 2003
 * @author Raymond Cypher
 */

public class Executor implements CALExecutor {

    /** This is the program object representing the universe of functions that the executor can run. */
    private final Program program;

    /** Provides access to the resources of the current environment (e.g. from the workspace, or from Eclipse). */
    private final ResourceAccess resourceAccess;

    /** A Set of registered statistics generators. */
    private final Set<StatsGenerator> statsGenerators = new HashSet<StatsGenerator> ();

    /** Preallocated exception for VM errors, since in this case, we may not be able to allocate a new object when the exception occurs. */
    CALExecutorException.InternalException.VMException vmException = CALExecutorException.InternalException.VMException.makeInitial();

    private final RTExecutionContext executionContext;


    /**
     * Constructor for Executor.
     * @param program
     * @param resourceAccess
     *            the ResourceAccess instance to provide access to the resources of the current
     *            environment (e.g. from the workspace, or from Eclipse).
     * @param context
     */
    public Executor(Program program, ResourceAccess resourceAccess, ExecutionContext context) {
        super();
        if (program == null) {
            throw new NullPointerException ("lecc.Executor: program can't be null.");
        }
        if (resourceAccess == null) {
            throw new NullPointerException ("lecc.Executor: resourceAccess can't be null.");
        }
        if (context == null) {
            throw new NullPointerException ("lecc.Executor: context can't be null.");
        }

        this.program = program;
        this.resourceAccess = resourceAccess;
        this.executionContext = (RTExecutionContext)context;
    }

    /**
     * Run the function specified by the EntryPoint using the given Java arguments.
     * @param entryPoint
     * @param arguments (null is accepted and used as the 0-length Object array).
     * @return the result of the execution
     * @throws CALExecutorException
     */
    public Object exec(EntryPoint entryPoint, Object[] arguments) throws CALExecutorException {
        // Since we are starting execution we want to reset to initial state.
        reset();

        if (entryPoint == null) {
            throw new CALExecutorException.InternalException ("null EntryPoint.");
        }

        QualifiedName entryPointSCName = ((EntryPointImpl)entryPoint).getFunctionName();
        CALExecutorException error = null;
        Object result = null;
        RTValue startPoint = null;

        try {
            instrument (new ExecTimeInfo(true,
                                         executionContext.getNReductions(),
                                         executionContext.getNMethodCalls(),
                                         executionContext.getNDataTypeInstances()));

            try {
                LECCModule startModule =
                    (LECCModule) program.getModule(entryPointSCName.getModuleName());
                MachineFunction mf = startModule.getFunction(entryPointSCName);

                startPoint = makeStartPointInstance(mf);
            } catch (Exception e) {
                throw new CALExecutorException.InternalException ("Unable to create instance of " + entryPointSCName, e);
            }

            if (startPoint == null) {
                throw new CALExecutorException.InternalException ("Unable to create start point instance for: " + entryPointSCName);
            }

            if (arguments != null) {
                for (int i = 0; i < arguments.length; ++i) {
                    startPoint = startPoint.apply(new RTData.CAL_Opaque(arguments[i]));
                }
            }
            startingInitialExecution();
            result = startPoint.evaluate(executionContext).getOpaqueValue();

        } catch (RTValue.InternalException e) {
            error = new CALExecutorException.InternalException (e.getErrorInfo(), "Error in evaluation: ", e);
        } catch (CALExecutorException e) {
            error = e;
        } catch (LinkageError e) {

            // LinkageErrors can happen during classloading.
            // For example, an internal error during bytecode generation.
            error = new CALExecutorException.InternalException ("Fatal Executor error.", e);

        } catch (Error e) {
            // Assume that something really bad has happened: reset the state of the
            // machine and force a finalization/gc.

            program.resetCachedResults(executionContext);
            reset ();
            for (int i = 0; i < 10; ++i) {
                //System.out.println ("trying to free memory " + i);
                System.runFinalization();
                System.gc();
            }

            vmException.initCause(e);
            error = (vmException);
        } catch (Exception e) {
            error = new CALExecutorException.InternalException ("Fatal Executor error.", e);
        }

        startPoint = null;
        instrument (new ExecTimeInfo (false,
                                      executionContext.getNReductions(),
                                      executionContext.getNMethodCalls(),
                                      executionContext.getNDataTypeInstances()));

        if (LECCMachineConfiguration.generateCallCounts()) {
            instrument (new CallCountInfo (executionContext.getCallCounts(), "Call counts:"));
            instrument (new CallCountInfo (executionContext.getDcConstructorCounts(), "DataConstructor instance counts:"));
            instrument (new CallCountInfo (executionContext.getDcFunctionCounts(), "DataConstructor functional form counts:"));
        }

        finishedInitialExecution ();

        if (error != null) {
            throw error;
        }

        reset ();

        // Clear the RuntimeEnvironment field of the execution context.  This prevents
        // an out-of date instance of Program from being held.
        executionContext.clearRuntimeEnvironment();

        return result;
    }

    /**
     * Create an instance of the generated which is the starting
     * point for evaluating the entry point supercombinator.
     * This can involve resolving to an aliased function or 
     * literal value.
     * 
     * @param entryPoint
     * @return - the start point instance
     * @throws InvocationTargetException 
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws NoSuchFieldException
     */
    private RTValue makeStartPointInstance(MachineFunction entryPoint)
            throws InvocationTargetException, IllegalAccessException,
            ClassNotFoundException, NoSuchMethodException, NoSuchFieldException {
        
        
        final QualifiedName idName = CAL_Prelude.Functions.id;
        
        // An instance of Prelude.id
        RTValue idFunction = null;
    
        if (!entryPoint.getQualifiedName().equals(idName)) {
            
            LECCModule startModule = 
                (LECCModule) program.getModule(idName.getModuleName());
            
            MachineFunction mf = startModule.getFunction(idName);
            idFunction = getInstanceOfGeneratedClass(mf);
        }
        
        RTValue startPoint = getInstanceOfGeneratedClass(entryPoint);
        
        // At this point we want to apply the identity function (Prelude.id) to
        // the entry point. It has been
        // found that this extra level of indirection allows the Sun client JVM
        // to more efficiently garbage-collect
        // discarded graph nodes for some types of functions.
        if (idFunction != null) {
            //System.out.println("applying id");
            startPoint = idFunction.apply(startPoint);
        }
     
        return startPoint;
    }
    
    /**
     * Create an instance of the generated class corresponding to the
     * entry point supercombinator.
     * @param machineFunction - The MachineFunction representing the CAL function for
     *             which an instance of the generated class is desired.
     * @return - the start point instance
     * @throws InvocationTargetException 
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws NoSuchFieldException
     */
    /*
     * @implementation
     * If you modify this method, you should also modify the related method
     * StandaloneJarBuilder.getInstanceOfGeneratedClassJavaExpression 
     */
    private RTValue getInstanceOfGeneratedClass(MachineFunction machineFunction)
        throws InvocationTargetException, IllegalAccessException,
        ClassNotFoundException, NoSuchMethodException, NoSuchFieldException {

        // System.out.println ("****************** start -
        // makeStartPointInstance " + entryPointSCName + " => thread = " +
        // Thread.currentThread().getName());
        // long stime = System.currentTimeMillis();      
        
        RTValue startPoint = null;

        if (machineFunction == null) {
            throw new NullPointerException("MachineFunction argument cannot be null.");
        }
        
        // The specified target may be an alias of another function, or it may
        // be defined as a literal value
        // (either directly or from following an alias chain). In either case
        // there is no source/class
        // generated. So we need to either return the literal value or follow
        // the alias to a
        // function for which there is a generated source/class file.
        if (machineFunction.getLiteralValue() != null) {
            return new RTData.CAL_Opaque(machineFunction.getLiteralValue());
        }
        
        QualifiedName entryPointSCName = machineFunction.getQualifiedName();
        
        if (machineFunction.getAliasOf() != null) {
            entryPointSCName = machineFunction.getAliasOf();
        }
        
        LECCModule startModule = (LECCModule) program.getModule(
                entryPointSCName.getModuleName());

        machineFunction = startModule.getFunction(entryPointSCName);
        
        CALClassLoader loader = ((LECCModule) program.getModule(
                entryPointSCName.getModuleName())).getClassLoader();
        if (loader == null) {
            return null;
        }

        String className = "Unknown";

        // Munge the qualified name into a class name.
        // Get local name and capitalized local name
        if (machineFunction.isDataConstructor()) {
            
            // this is a DataConstructor
            className = CALToJavaNames.createFullClassNameFromDC(
                    entryPointSCName, startModule);
            DataConstructor dc = startModule.getModuleTypeInfo().getDataConstructor(
                    entryPointSCName.getUnqualifiedName());

            if (LECCMachineConfiguration.TREAT_ENUMS_AS_INTS && TypeExpr.isEnumType(dc.getTypeConstructor())) {
                // an enum data cons treated as an int - we should just return the boxed ordinal
                return RTData.CAL_Int.make(dc.getOrdinal());
                
            } else {
                if (className.endsWith("$TagDC")) {
                    if (entryPointSCName.equals(CAL_Prelude.DataConstructors.True)) {
                        return RTData.CAL_Boolean.TRUE;
                    } else if (entryPointSCName
                            .equals(CAL_Prelude.DataConstructors.False)) {
                        return RTData.CAL_Boolean.FALSE;
                    } else {
                        className = className.substring(0, className.length() - 6);
                        startPoint = loader.getTagDCStartPointInstance(className,
                            dc.getOrdinal());
                    }
                } else {
                    startPoint = loader.getStartPointInstance(className, machineFunction, executionContext);
                }
            }
        } else {
            className = CALToJavaNames
                    .createFullClassNameFromSC(entryPointSCName, startModule);
            startPoint = loader.getStartPointInstance(className, machineFunction, executionContext);
        }

        // System.out.println ("class loading time: " +
        // (System.currentTimeMillis() - stime) + " => thread = " +
        // Thread.currentThread().getName());
        // System.out.println ("****************** end -
        // makeStartPointInstance"+ " => thread = " +
        // Thread.currentThread().getName());

        return startPoint;
    }    
    
    /**
     * Reset the Executor.
     */
    void reset() {
        vmException = CALExecutorException.InternalException.VMException.makeInitial();
        executionContext.reset(new DynamicRuntimeEnvironment(program, resourceAccess));
    }

    /**
     * Register a stats generator.
     * @param gen
     */
    public void addStatsGenerator (StatsGenerator gen) {
        statsGenerators.add (gen);
    }

    /**
     * Remove a stats generator.
     * @param gen
     */
    public void removeStatsGenerator (StatsGenerator gen) {
        statsGenerators.remove (gen);
    }

    /**
     * Ask the runtime to quit.
     * Note that you can only ask the runtime to quit; you can't un-quit - we may want to have something like this later
     * if we want to implement "restart."
     * Creation date: (3/21/02 1:39:40 PM)
     */
    public void requestQuit() {
        executionContext.requestQuit();
    }

    /**
     * {@inheritDoc}
     */
    public void requestSuspend() {
        executionContext.requestSuspend();
    }

    /**
     * Set the regular expressions that the tracing code uses to filter the traces.
     *
     * @param patterns The list of regular expressions to use when filtering traces.
     */

    public void setTraceFilters(List<Pattern> patterns) {
        executionContext.setTraceFilters(patterns);
    }

    /**
     * This is used for diagnostics and instrumentation.
     * @param object Object
     */
    void instrument (StatsGenerator.ProfileObj object) {
        //System.out.println ("Instrument -> " + object.toString ());
        for (final StatsGenerator gen : statsGenerators) {
            gen.i_instrument(object);
        }
    }

    /**
     * A helper function for derived classes to override.
     * Allows derived classes to do any special signaling/handling
     * when the initial evaluation is starting.
     */
    void startingInitialExecution () {
        if (LECCMachineConfiguration.generateAppCounts()) {
            RTFullApp.resetNInstances();
            RTApplication.resetNInstances();
            RTValue.resetNApplicationCalls();
            RTOApp2.resetNInstances();
            RTOApp3.resetNInstances();
            RTPartialApp.resetNInstances();
        }
    }

    /**
     * A helper function for derived classes to override.
     * Allows derived classes to do any special signaling/handling
     * when the initial evaluation is complete.
     */
    void finishedInitialExecution () {
        if (LECCMachineConfiguration.generateAppCounts()) {
            System.out.println (RTValue.getNApplicationCalls() + " calls to apply().");
            System.out.println ("RTFullApp -> " + RTFullApp.getNInstances());
            System.out.println ("RTApplication -> " + RTApplication.getNInstances());
            System.out.println ("RTOApp2 -> " + RTOApp2.getNInstances());
            System.out.println ("RTOApp3 -> " + RTOApp3.getNInstances());
            System.out.println ("RTPartialApp -> " + RTPartialApp.getNInstances());
        }
    }

    /** {@inheritDoc} */
    public RTExecutionContext getContext () {
        return executionContext;
    }

    /**
     * Class used to tell the statistics generator about execution time info.
     *  @author rcypher
     */
    static class ExecTimeInfo implements StatsGenerator.ProfileObj {
        /** Does this object indicate the start or end of an event. */
        private boolean start;

        /** The number of reductions in this event. */
        private long reductions;

        /** The number of method calls in this event. */
        private long methods;

        /** The number of data instances in this event. */
        private long dataInstances;

        public ExecTimeInfo (boolean start, long reductions, long methods, long dataInstances) {
            this.start = start;
            this.reductions = reductions;
            this.dataInstances = dataInstances;
            this.methods = methods;
        }

        /**
         * Update the given StatsObject with the information in this ProfileObject.
         * @param stats
         * @return - the updated StatsObject, null if no update occurs
         */
        public StatsObject updateStats (StatsObject stats) {
            if (stats == null) {
                stats = new ExecTimeStats ();
            }

            // If the StatsObject is not compatible return null.
            if (!(stats instanceof ExecTimeStats)) {
                return null;
            }

            ExecTimeStats mStats = (ExecTimeStats)stats;

            if (start) {
                // If we are starting execution we update the state in the StatsObject
                mStats.executionStart = System.currentTimeMillis();
                mStats.execStarted = true;
            } else {
                // If we are ending execution we want to update the StatsObject
                // being sure to handle a StatsObject that hasn't been started.
                if (!mStats.execStarted) {
                    if (mStats.execTimes.size() == 0) {
                        return mStats;
                    }
                    mStats.execTimes.remove(mStats.execTimes.size() - 1);
                }

                mStats.execTimes.add (new Long (System.currentTimeMillis() - mStats.executionStart));
                mStats.execStarted = false;
            }

            // Update the counts if necessary.
            if (reductions != -1) {
                mStats.reductionsExec = reductions;
            }
            if (methods != -1) {
                mStats.methodsExec = methods;
            }
            if (dataInstances != -1) {
                mStats.dataInstancesExec = dataInstances;
            }
            return stats;
        }

        /**
         * Class used to track execution time statistics.
         * Instances of this class will be held by the StatsGenerator
         *  @author rcypher
         */
        protected static class ExecTimeStats extends StatsGenerator.PerformanceStatsObject {

            long executionTime = -1;

            /** Time in ms at which the execution started. */
            long executionStart = 0;

            // vectors to hold individual contributions
            final List<Long> execTimes = new ArrayList<Long> ();

            // Keep track of the state of execution.
            boolean execStarted = false;

            /** Count of reductions. */
            long reductionsExec;

            /** Count of method calls. */
            long methodsExec;

            /** Count of data instances. */
            long dataInstancesExec;

            /**
             * Generate a short message describing the information in this StatsObject.
             * @return - a string describing the information.
             */
            public String generateShortMessage() {
                DecimalFormat numFormat = new DecimalFormat("###,###.###");

                StringBuilder sb = new StringBuilder();
                if (LECCMachineConfiguration.generateStatistics()) {
                    sb.append (numFormat.format(reductionsExec) + " reductions in " + numFormat.format(getRunTime()) + "ms");
                    if (getRunTime() > 0) {
                        sb.append (", reductions/s = " + numFormat.format(((reductionsExec*1000)/getRunTime())));
                    }
                } else {
                    sb.append ("Run Time:        " +  numFormat.format(getRunTime()) + " ms\n");
                }
                return sb.toString();
            }

            /**
             * Generate a long message describing the information in this StatsObject.
             * @return - a string describing the information.
             */
            public String generateLongMessage() {
                DecimalFormat numFormat = new DecimalFormat("###,###.###");

                StringBuilder sb = new StringBuilder ();

                sb.append ("Run Time:        " +  numFormat.format(getRunTime()) + " ms\n");
                if (LECCMachineConfiguration.generateStatistics()) {
                    if (reductionsExec > 0 && getRunTime() > 0) {
                        sb.append ("  Reductions: " + numFormat.format(reductionsExec) + " (" + numFormat.format(((reductionsExec*1000)/getRunTime())) + "/s)");
                        sb.append("\n");
                    }
                    if (methodsExec > 0 && getRunTime() > 0) {
                        sb.append ("  Methods Calls:  " + numFormat.format(methodsExec) + " (" + numFormat.format(((methodsExec*1000)/getRunTime())) + "/s)");
                        sb.append ("\n");
                    }
                    if (dataInstancesExec > 0 && getRunTime() > 0) {
                        sb.append ("  Data Instances: " + numFormat.format(dataInstancesExec) + " (" + numFormat.format(((dataInstancesExec*1000)/getRunTime())) + "/s)");
                        sb.append ("\n");
                    }
                }
                return sb.toString();
            }

            /**
             * Summarize a list of StatsObject of the same actual type as this.
             * @param v - A List of StatsObject
             * @return a summarized StatsObject
             * {@inheritDoc}
             */
            public StatsObject summarize (List<StatsObject> v) {
                // Create a SummarizedExecTimes object
                // containing the summarized info for the list
                // of StatsObject

                // If there are more than 3 runs we will discard the first run
                final int nRuns = v.size();
                final int nstart = (nRuns >= 3) ? 1 : 0;
                final int nend = nRuns;
                final int ncount = nend - nstart;

                long allExecTimes[] = new long [nRuns];

                {
                    int i = 0;
                    for (final StatsObject statsObject : v) {
                        ExecTimeStats stats = (ExecTimeStats) statsObject;
                        allExecTimes [i] = stats.getRunTime();
                        i++;
                    }
                }

                long firstExecTime = allExecTimes [0];

                SummarizedExecTimes ptr = new SummarizedExecTimes ();
                ptr.allExecTimes = new long[allExecTimes.length];
                System.arraycopy(allExecTimes, 0, ptr.allExecTimes, 0, allExecTimes.length);

                long avgExecTime = 0;

                for (int i = nstart; i < nend; ++i) {
                    avgExecTime += allExecTimes [i];
                }

                avgExecTime /= (ncount);

                // calculate standard deviation.
                double stDev = 0.0;
                long avg = 0;
                for (int i = nstart; i < nend; ++i) {
                    avg += (allExecTimes [i]);
                }

                avg /= ncount;

                long summ = 0;
                for (int i = nstart; i < nend; ++i) {
                    long diff = allExecTimes [i] - avg;
                    summ += (diff * diff);
                }

                double stdDevPercent;
                if (ncount > 1) {
                    stDev = (double)summ /(double)(ncount-1);
                    stDev = Math.sqrt(stDev);

                    stDev = ((int)(stDev * 100.0)) / 100.0;
                    stdDevPercent = (stDev / (avgExecTime)) * 100.0;
                    stdDevPercent = ((int)(stdDevPercent * 100.0)) / 100.0;
                } else {
                    stDev = stdDevPercent = 0.0;
                }

                //calculate the standard error:
                double standardError = stDev / Math.sqrt(ncount);

                ptr.frRunTime = firstExecTime;
                ptr.avgRunTime = avgExecTime;
                ptr.stdDev = stDev;
                ptr.stdDevPercent = stdDevPercent;
                ptr.standardError = standardError;
                ptr.methodsExec = methodsExec;
                ptr.reductionsExec = reductionsExec;
                ptr.dataInstancesExec = dataInstancesExec;

                return ptr;
            }

            /**
             * Return the execution time (i.e. time spent running before reaching initial WHNF).
             * @return long
             */
            public long getRunTime () {

                if (executionTime == -1) {
                    executionTime = 0;
                    for (final Long l : execTimes) {
                        executionTime += l.longValue();
                    }
                }

                return executionTime;
            }

            @Override
            public long getAverageRunTime() {
                return getRunTime();
            }
            @Override
            public long getMedianRunTime() {
                return getRunTime();
            }
            @Override
            public double getStdDeviation() {
                return 0.0;
            }
            @Override
            public int getNRuns() {
                return 1;
            }
            @Override
            public long[] getRunTimes() {
                return new long[]{getRunTime()};
            }

        }

        /**
         *  Class used to summarize execution time statistics for a series of runs.
         *  @author rcypher
         */
        protected static class SummarizedExecTimes extends StatsGenerator.PerformanceStatsObject {
            /** Run time for the first run. */
            long frRunTime;

            /** Average run time. */
            long avgRunTime;

            /** Standard deviation of the run times. */
            double stdDev;

            /** Standard deviation as a percent of the average run time. */
            double stdDevPercent;

            /** Standard error of the mean. */
            double standardError;

            /** Times for all runs. */
            long allExecTimes[];

            /** The number of reductions for a single run. */
            long reductionsExec;

            /** The number of method calls for a single run. */
            long methodsExec;

            /** The number of data constructor instances for a single run. */
            long dataInstancesExec;

            /**
             * Generate a short message describing the information in this StatsObject.
             * @return - a string describing the information.
             */
            public String generateShortMessage() {
                StringBuilder sb = new StringBuilder ();
                DecimalFormat numFormat = new DecimalFormat("###,###.###");

                if (LECCMachineConfiguration.generateStatistics()) {
                    sb.append ("Summary:  time = " + numFormat.format(avgRunTime) + ", reductions/s = " + numFormat.format(((reductionsExec*1000)/avgRunTime)));
                } else {
                    sb.append ("Average time = " + numFormat.format(avgRunTime) + "ms.");
                }
                return sb.toString();
            }

            /**
             * Generate a long message describing the information in this StatsObject.
             * @return - a string describing the information.
             */
            public String generateLongMessage() {
                StringBuilder sb = new StringBuilder ();
                DecimalFormat numFormat = new DecimalFormat("###,###.###");

                if (allExecTimes.length >= 3) {
                    sb.append("Individual runs: (first run discarded for statistics due to potential class loading and code generation)\n");

                    for (int i = 0; i < allExecTimes.length; ++i) {
                        sb.append ("run " + i + ":\t" + numFormat.format(allExecTimes[i]));
                        if (i == 0) {
                            sb.append(" (discarded)\n");
                        } else {
                            sb.append('\n');
                        }
                    }
                } else {

                    sb.append("Individual runs: \n");
                    for (int i = 0; i < allExecTimes.length; ++i) {
                        sb.append ("run " + i + ":\t" + numFormat.format(allExecTimes[i]) + "\n");
                    }
                }

                sb.append("First run time = " + numFormat.format(frRunTime) + "ms\n");
                sb.append("Average time   = " + numFormat.format(avgRunTime) + "ms\n");
                sb.append("Minimum time   = " + numFormat.format(getMinTime()) + "ms\n");
                sb.append("Standard deviation of runs = " + numFormat.format(stdDev) + "ms or " + numFormat.format(stdDevPercent) + "% of average\n");
                sb.append("Standard error of mean     = " + numFormat.format(standardError) + "ms or " + numFormat.format(standardError/avgRunTime*100) + "% of average\n");

                if (LECCMachineConfiguration.generateStatistics()) {
                    if (reductionsExec > 0 && getRunTime() > 0) {
                        sb.append ("  Reductions:     " + numFormat.format(reductionsExec) + " (" + numFormat.format(((reductionsExec*1000)/getRunTime())) + "/s)");
                        sb.append("\n");
                    }
                    if (methodsExec > 0 && getRunTime() > 0) {
                        sb.append ("  Methods Calls:  " + numFormat.format(methodsExec) + " (" + numFormat.format(((methodsExec*1000)/getRunTime())) + "/s)");
                        sb.append ("\n");
                    }
                    if (dataInstancesExec > 0 && getRunTime() > 0) {
                        sb.append ("  Data Instances: " + numFormat.format(dataInstancesExec) + " (" + numFormat.format(((dataInstancesExec*1000)/getRunTime())) + "/s)");
                        sb.append ("\n");
                    }

                }

                return sb.toString();
            }

            /**
             * Return a summarization of the List of StatsObject.
             * @param runs
             * @return the summarized StatsObject
             * {@inheritDoc}
             */
            public StatsObject summarize (List<StatsObject> runs) {
                // This is a summarized object so no further summarization can be done.
                if (runs == null) {
                    return null;
                }
                return this;
            }

            /**
             * @return the average run time, since this is a summarization of multiple runs
             */
            public long getRunTime() {
                return avgRunTime;
            }

            /**
             * @return the average run time
             */
            @Override
            public long getAverageRunTime() {
                return avgRunTime;
            }

            /**
             * @return the median run time
             */
            @Override
            public long getMedianRunTime() {
                if (allExecTimes.length == 1) {
                    return allExecTimes[0];
                }

                long times[] = new long[allExecTimes.length];
                System.arraycopy(allExecTimes, 0, times, 0, times.length);
                Arrays.sort(times);
                if ((times.length % 2) == 0) {
                    return (times[times.length/2] + times[(times.length/2)-1]) / 2;
                }

                return times[(times.length/2)];
            }

            /**
             * @return the standard deviation
             */
            @Override
            public double getStdDeviation() {
                return stdDev;
            }

            /**
             * @return the standard error of the mean
             */
            public double getStandardError() {
                return standardError;
            }

            /**
             * @return the number of runs used in calculating the average and standard deviation.
             */
            @Override
            public int getNRuns() {
                return allExecTimes.length >= 3 ? allExecTimes.length - 1 : allExecTimes.length;
            }

            /**
             * Return the times for all runs.  Note that there may be more than are indicated by
             * the value returned by getNRuns().  This is because we discard the highest/lowest values,
             * if there are more than three runs, when calculating average run time and standard
             * deviation.
             * @return the times for all runs.
             */
            @Override
            public long[] getRunTimes() {
                long times[] = new long[allExecTimes.length];
                System.arraycopy(allExecTimes, 0, times, 0, times.length);
                return times;
            }

        }

    }

    /**
     * A profile object that collects information about the frequency of
     * invocation for different CAL entities. This object is used to communicate
     * the information to the StatsGenerator
     *
     * @author rcypher
     */
    static class CallCountInfo implements StatsGenerator.ProfileObj {
        /** Map of QualifiedName -> CallCount.  Associates a CallCount object with a named entity. */
        private final Map<QualifiedName, CallCount> counts;

        /** Describes the type of counts being recorded.  E.g. supercombinator call counts,
         * data constructor call counts, etc.
         */
        private final String type;

        CallCountInfo (Map<QualifiedName, Integer> counts, String type) {
            
            final Map<QualifiedName, CallCount> convertedCounts = new HashMap<QualifiedName, CallCount>();
            for (Map.Entry<QualifiedName, Integer> entry : counts.entrySet()) {
                QualifiedName name = entry.getKey();
                convertedCounts.put(name, new CallCount(name, entry.getValue()));
            }
                        
            this.counts = convertedCounts;
            this.type = type;
        }
        /**
         * Update the supplied statistics object.
         * If the StatsObject is of incorrect type return null.
         * If the StatsObject is null create a new one.
         * @param stats
         * @return StatsObject
         */
        public StatsObject updateStats (StatsObject stats) {
            if (stats == null) {
                stats = new CallCountStats (type);
            }

            if (!(stats instanceof CallCountStats)) {
                return null;
            }

            CallCountStats mStats = (CallCountStats)stats;

            if (!mStats.type.equals(type)) {
                return null;
            }

            Map<QualifiedName, CallCount> allCounts = mStats.counts;

            for (final Map.Entry<QualifiedName, CallCount> entry : allCounts.entrySet()) {
                QualifiedName key = entry.getKey();
                CallCount pc = counts.get(key);
                CallCount tc = entry.getValue();
                if (tc == null) {
                    tc = new CallCount(pc.getName(), 0);
                    allCounts.put(key, tc);
                }
                tc.incrementBy(pc.getCount());
            }
            return mStats;
        }

        /**
         * A class used to accumulate call count information in a StatsGenerator.
         * Instances of this class will be held by the StatsGenerator to accumulate
         * information and ultimately to generate a message describing the accumulated info.
         *
         * @author rcypher
         */
        protected static class CallCountStats implements StatsGenerator.StatsObject {

            /** Associates a named entity to a CallCount instance. */
            Map<QualifiedName, CallCount> counts = new HashMap<QualifiedName, CallCount>();

            /** A description of the type of counts being recorded.  e.g. supercombinator, dataconstructor, etc. */
            String type;

            public CallCountStats (String type) {
                this.type = type;
            }

            /**
             * Generate a short message describing these statistics.
             * @return String
             */
            public String generateShortMessage() {
                // There is only one version of the description of these statistics so
                // we simply pass on the call.
                return generateLongMessage();
            }

            /**
             * Generate a long message describing these statistics.
             * @return String
             */
            public String generateLongMessage() {
                // Sort the counts by frequency.
                List<CallCount> sortedCounts = new ArrayList<CallCount> ();
                for(final Map.Entry<QualifiedName, CallCount> entry : counts.entrySet()) {
                    sortedCounts.add (entry.getValue());
                }
                Collections.sort (sortedCounts);

                // Build up a message displaying the
                // counts sorted by frequency and accumulate a
                // total counts value to append at the end.
                StringBuilder sb = new StringBuilder();
                sb.append(type + "\n");

                long totalCounts = 0;
                for (final CallCount count : sortedCounts) {
                    sb.append (count.getCount() + "\t\t-> " + count.getName() + "\n");
                    totalCounts += count.getCount();
                }
                sb.append ("    total counts = " + totalCounts + "\n\n");
                if (totalCounts == 0) {
                    return "";
                }
                return sb.toString();
            }

            /**
             * Summarize the list of stats objects.
             * The objects in the list will be of the same type
             * as the executing instance.
             * This method is called when a set of StatsObjects has
             * been accumulated across several runs.
             * @param runs
             * @return StatsObject.
             * {@inheritDoc}
             */
            public StatsObject summarize(List<StatsObject> runs) {
                if (runs == null) {
                    return null;
                }
                return this;
            }

        }
    }

    /**
     * A class for holding a count of the number of times a CAL
     * function is invoked.
     * Implements comparable by comparing the count.
     * @author rcypher
     */
    static final class CallCount implements Comparable<CallCount> {
        /** Name of the CAL function. */
        private final QualifiedName name;

        /** Number of times the function has been invoked. */
        private int count;

        CallCount (QualifiedName name, int initialCount) {
            this.name = name;
            this.count = initialCount;
        }

        public void increment () {
            count++;
        }

        public void incrementBy(int i) {
            count += i;
        }

        public QualifiedName getName() {
            return name;
        }

        public int getCount() {
            return count;
        }

        /**
         * Compare two CallCount object based on
         * the count value.
         * @param cc - the object to compare to.
         * @return int
         */
        public int compareTo (CallCount cc) {
            int nameCompare = name.compareTo(cc.name);
            if (nameCompare != 0) {
                return nameCompare;
            }

            if (cc.count > count) {
                return 1;
            } else if (cc.count == count) {
                return 0;
            } else {
                return -1;
            }
        }

        @Override
        public boolean equals (Object o) {
            if (o == null || !(o instanceof CallCount)) {
                return false;
            }

            CallCount other = (CallCount)o;
            return count == other.count && name.equals(other.name);
        }

        @Override
        public int hashCode () {
            return name.hashCode() + count;
        }

    }
}
