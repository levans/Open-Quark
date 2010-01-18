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
 * LECCMachineConfiguration.java
 * Created: Sep 10, 2003 3:02:02 PM
 * By: RCypher
 */
package org.openquark.cal.internal.runtime.lecc;

import org.openquark.cal.runtime.MachineConfiguration;


/**
 * This class holds configuration information for code generation,
 * as well as some information accessed by the runtime related to how
 * the code was generated.
 *
 * Creation: Sep 10, 2003
 * @author RCypher
 */
public final class LECCMachineConfiguration {

    /**
     * The name of the root package for generated classes.
     */
    public static final String ROOT_PACKAGE = "org.openquark";
    public static final String GEN_BYTECODE_PROP = "org.openquark.cal.machine.lecc.source";
    public static final String STATIC_BYTECODE_RUNTIME_PROP = "org.openquark.cal.machine.lecc.static_runtime";
    public static final String GEN_STATISTICS_PROP = "org.openquark.cal.machine.lecc.runtime_statistics";
    public static final String GEN_CALLCOUNTS_PROP = "org.openquark.cal.machine.lecc.runtime_call_counts";
    public static final String GEN_APPCOUNTS_PROP = "org.openquark.cal.machine.lecc.runtime_gen_app_counts";
    public static final String NON_INTERRUPTIBLE_RUNTIME_PROP = "org.openquark.cal.machine.lecc.non_interruptible";
    public static final String CONCURRENT_RUNTIME_PROP = "org.openquark.cal.machine.lecc.concurrent_runtime";
    public static final String USE_LAZY_FOREIGN_ENTITY_LOADING_PROP = "org.openquark.cal.machine.lecc.strict_foreign_entity_loading";
    public static final String CODE_GENERATION_STATS_PROP = "org.openquark.cal.machine.lecc.code_generation_stats";
    public static final String DEBUG_INFO_PROP = "org.openquark.cal.machine.lecc.debug_info";
    public static final String OUTPUT_DIR_PROP = "org.openquark.cal.machine.lecc.output_directory";
    public static final String BYTECODE_SPACE_OPTIMIZATION_PROP = "org.openquark.cal.machine.lecc.bytecode_space_optimization";
    public static final String SOURCE_CODE_SPACE_OPTIMIZATION_PROP = "org.openquark.cal.machine.lecc.source_code_space_optimization";

    /** Version of code generation schemas.
     *  Changing this value will force all existing
     *  generated sources to be re-generated.
     */
    public static final int CODEGEN_VERSION = 1612;

    /** Whether to directly generate bytecode, or go to source first. */
    private static final boolean GEN_BYTECODE = System.getProperty(GEN_BYTECODE_PROP) == null;

    /**
     * Flag affecting lecc runtime if GEN_BYTECODE is true.
     *   If true, bytecode will be generated when asked for by the classloader.
     *   If false, it will be generated statically to disk, and loaded from there by the classloader.
     */
    private static final boolean STATIC_BYTECODE_RUNTIME = System.getProperty(STATIC_BYTECODE_RUNTIME_PROP) != null;

    /** Turns on generation of reduction, method call, and data instance counting.
     *  NOTE: Changing this flag's value will result in forcing code regeneration in a static runtime.
     */
    private static final boolean GEN_STATISTICS = System.getProperty (GEN_STATISTICS_PROP) != null;

    /** Turns on generation of call counting.
     *  NOTE: Changing this flag's value will result in forcing code regeneration in a static runtime.
     */
    private static final boolean GEN_CALLCOUNTS = System.getProperty (GEN_CALLCOUNTS_PROP) != null;

    /** Turns on generation of application node counting.
     *  Counts number of RTApplication nodes and number of RTFullApp nodes.
     */
    private static final boolean GEN_APPCOUNTS = System.getProperty (GEN_APPCOUNTS_PROP) != null;

    /**
     * If this is set to true, the code generated will be capable of function tracing.
     * One can then dynamically turn on and off function tracing via calls to Debug.setTracingEnabled.
     * Also the code generated will be capable of halting on breakpoints.
     *
     * Note that when generating debug code, we set GEN_DIRECT_PRIMOP_CALLS to false so that
     * calls to primitive operators and foreign functions show up in the traces as well.
     * Also we set PASS_EXEC_CONTEXT_TO_DATA_CONSTRUCTORS to true so that data constructors
     * can trace/halt.
     */
    private static final boolean GEN_DEBUG_CAPABLE = System.getProperty(MachineConfiguration.MACHINE_DEBUG_CAPABLE_PROP) != null;

    /**
     * If true, the system does not check the quit flag in the execution context to check for external interruptions of execution.
     * This is mainly set for benchmarking comparison in situations where we are comparing against another non-interruptible program
     * and don't want to occur the extra (small) overhead of checking this flag.
     */
    private static final boolean NON_INTERRUPTIBLE_RUNTIME = !GEN_DEBUG_CAPABLE && System.getProperty(NON_INTERRUPTIBLE_RUNTIME_PROP) != null;

    /**
     * If true, the lecc run-time supports concurrent reduction of CAL programs on a single execution context.
     * Note that the lecc runtime is thread-safe even when this flag is false in the sense that concurrent evaluation CAL entry points
     * on separate execution contexts that do not share graph is thread-safe (assuming that referenced foreign entities that are
     * shared are thread-safe).
     * At the moment, this support is limited to being able to concurrently evaluate program graphs that have no shared references
     * other than evaluated CAFs.
     */
    private static final boolean CONCURRENT_RUNTIME = System.getProperty(CONCURRENT_RUNTIME_PROP) != null;

    /**
     * If this is true the generated code will pass the execution context to the constructors of the
     * data constructor classes.
     */
    private static final boolean PASS_EXEC_CONTEXT_TO_DATA_CONSTRUCTORS = GEN_CALLCOUNTS || GEN_STATISTICS || GEN_DEBUG_CAPABLE;

    /**
     * If true, calls to primitive operators will be made directly whenever possible. Primitive operators
     * are those primitive functions defined in the PrimOp class, as well as foreign function calls. Direct
     * calls to them are encoded as primitive jvm operations (for example, for int addition), or as direct
     * calls to a foreign function.
     *
     * If false, calls to primitive operators are made via their corresponding fUnboxed method of their
     * RTValue derived class.
     *
     * In general, calling primitive functions directly is good for performance. However, we may want to call
     * indirectly so that calls to such functions can be traced, and so that more precise error information
     * can be given in the case of an exception occurring while invoking the primitive function.
     */
    private static final boolean GEN_DIRECT_PRIMOP_CALLS = !GEN_DEBUG_CAPABLE;

    /**
     * If true, then foreign entities corresponding to foreign types and foreign functions will be loaded lazily.
     * Otherwise, they are resolved and loaded eagerly during compilation/deserialization.
     */
    private static final boolean USE_LAZY_FOREIGN_ENTITY_LOADING = System.getProperty(USE_LAZY_FOREIGN_ENTITY_LOADING_PROP) == null;

    /**
     * If true, the bytecode compiler rewrites its output to insert null assignments, which optimizes for
     * runtime memory usage.
     * Otherwise, no rewriting is done.
     */
    private static final boolean BYTECODE_SPACE_OPTIMIZATION = System.getProperty(BYTECODE_SPACE_OPTIMIZATION_PROP) != null;

    /**
     * If true, the java generator uses calls to RTValue.lastRef to perform null assignments of local variables
     * at the point of last reference, which optimizes for runtime memory usage.
     * Otherwise, local variables are not set to null .
     */
    private static final boolean SOURCE_CODE_SPACE_OPTIMIZATION = System.getProperty(SOURCE_CODE_SPACE_OPTIMIZATION_PROP) != null;

    /**
     * The different lazy body functions for the supercombinators (f, fLn) simply refer to the strict body fSn.
     */
    public static final boolean CONSOLIDATE_FUNCTION_BODIES = true;

    /**
     * Generate and call function bodies which directly return an unboxed value.
     */
    public static final boolean DIRECT_UNBOXED_RETURNS = true;

    /**
     * This flag turns on the optimization of fully saturated lazy applications by
     * using a strict application node where possible.
     */
    public static final boolean OPTIMIZE_LAZY_APP_NODES = true;

    /**
     * This flag turns on optimized representation of dictionary function applications.
     * Currently the dictionary functions optimized are those for the type classes Eq, Ord,
     * and Num.
     * i.e.: equals, notEquals, lessThan, lessThanEquals, greaterThan, greaterThanEquals,
     * fromInteger, toDouble, negate, add, subtract, multiply, divide.
     */
    public static final boolean OPTIMIZE_TYPECLASS_DICTS = true;

    /**
     * This flag indicates that operations specified in the 'optimizedLazyOps' map
     * should be compiled strictly even in a lazy context if all the arguments
     * are known to be already evaluated.
     */
    public static final boolean OPTIMIZE_LAZY_SUPERCOMBINATORS = true;

    /**
     * This flag allows elimination of switches on data types that only have one
     * data constructor.
     */
    public static final boolean OPTIMIZE_SINGLE_DC_CASES = true;

    /**
     * This flag optimizes cases on two DC data types by generating an if-then-else
     */
    static final boolean OPTIMIZE_TWO_DC_CASES = false;

    /**
     * This flag turns off the use of strictness annotations on data constructor and function arguments.
     */
    public static final boolean IGNORE_STRICTNESS_ANNOTATIONS = false;

    /**
     * Turns on optimization of application chains where the LHS cannot be determined at compile time.
     */
    public static final boolean OPTIMIZE_GENERAL_APP_CHAINS = true;

    /**
     * Treat the data constructors in an 'enumeration' data type as ints.
     */
    public static final boolean TREAT_ENUMS_AS_INTS = true;

    /**
     * The number of arguments for which a specialized application node
     * exists.
     * The runtime will contain specialized application nodes for fully and
     * partially saturated application for functions of up to and including
     * this arity.
     * Also when generating classes we will generate special purpose application
     * nodes for handling strict/unboxed values for functions with up to and
     * including this arity.
     */
    public static final int OPTIMIZED_APP_CHAIN_LENGTH = 15;


    /**
     * Flag indicating that generated code should contain additional
     * functionality to test correctness of let variable code generation.
     * This flag is only for internal debugging use and should be used
     * with caution.  If code executes successfully with this flag
     * set to true it is guaranteed that let variables are not being
     * executed multiple times for a single reduction of a function.
     * However if an error is reported with this flag turned on it
     * doesn't necessarily mean that let variable evaluation is
     * incorrect.  The error should be taken as an indication to double-
     * check the corresponding generated code to determine if a problem
     * truly exists as turning this flag on can incorrectly report a double
     * evaluation of a letvar in situations where reduction of a function
     * application will cause reduction of a previously created application
     * of the same function.
     */
    public static final boolean SANITY_CHECK_LET_VARS = false;

    private LECCMachineConfiguration() {}

    /**
     * @return true if the current configuration directly generates bytecode.
     */
    public static boolean generateBytecode () { return GEN_BYTECODE;}

    /**
     * @return true if generation of statistics is turned on.
     */
    public static boolean generateStatistics() {return GEN_STATISTICS;}

    /**
     * @return true if the generation of call counts is turned on.
     */
    public static boolean generateCallCounts() {return GEN_CALLCOUNTS;}

    /**
     * @return true if the generation of application counts is turned on.
     */
    public static boolean generateAppCounts(){return GEN_APPCOUNTS;}

    /**
     * @return true if debug mode is turned on.
     */
    public static boolean generateDebugCode() {return GEN_DEBUG_CAPABLE;}

    /**
     * @return true if the runtime is in non-interruptible mode.
     */
    public static final boolean nonInterruptibleRuntime() {return NON_INTERRUPTIBLE_RUNTIME;}

    /**
     * @return true if the execution context should be passed to data constructors.
     */
    public static final boolean passExecContextToDataConstructors() {return PASS_EXEC_CONTEXT_TO_DATA_CONSTRUCTORS;}

    /**
     * @return true if direct calls to primitive operators can be in-lined.
     */
    public static final boolean generateDirectPrimOpCalls() {return GEN_DIRECT_PRIMOP_CALLS;}

    /**
     * @return whether config flags specify that a lecc runtime would run in the mode where the runtime is static.
     * ie. bytecode is retrieved from disk.
     */
    public static final boolean isLeccRuntimeStatic() {
        return !GEN_BYTECODE || STATIC_BYTECODE_RUNTIME;
    }

    /**
     * @return whether foreign entities corresponding to foreign types and foreign functions will be loaded lazily.
     */
    public static boolean useLazyForeignEntityLoading() {
        return USE_LAZY_FOREIGN_ENTITY_LOADING;
    }

    /**
     * @return whether the bytecode compiler should rewrite its output to optimize for runtime memory usage.
     */
    public static boolean bytecodeSpaceOptimization() {
        return BYTECODE_SPACE_OPTIMIZATION;
    }

    /**
     * @return whether the java generator should use RTValue.lastRef() to null out local variables
     * at the point of final reference.
     */
    public static boolean sourcecodeSpaceOptimization() {
        return SOURCE_CODE_SPACE_OPTIMIZATION;
    }

    /**
     * If true, the lecc run-time supports concurrent reduction of CAL programs on a single execution context.
     * Note that the lecc runtime is thread-safe even when this flag is false in the sense that concurrent evaluation CAL entry points
     * on separate execution contexts that do not share graph is thread-safe (assuming that referenced foreign entities that are
     * shared are thread-safe).
     *
     * <p>
     * When this flag is true, the lecc run-time supports concurrent threads doing CAL evaluation on a single execution context.
     * This is accomplished in 3 ways:
     * <ol>
     *   <li> access to CAFs is synchronized (this holds true even without this flag on)
     *   <li> The rootNode, held by the execution context, is held in thread local storage
     *   <li> RTValue.evaluate and reduce methods are synchronized in RTResultFunction subclasses.
     * </ol>
     */
    public static boolean concurrentRuntime() {
        return CONCURRENT_RUNTIME;
    }
}
