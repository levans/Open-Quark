/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_Debug.java)
 * was generated from CAL module: Cal.Core.Debug.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Core.Debug module from Java code.
 *  
 * Creation date: Wed Oct 03 15:53:07 PDT 2007
 * --!>
 *  
 */

package org.openquark.cal.module.Cal.Core;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * Defines some useful functions and type classes for use when debugging CAL programs.
 * <p>
 * In general, the functions in this module should not be relied upon in production code. They are
 * intended for debugging purposes only, and their precise semantics and operation are subject to change.
 * <p>
 * <code>Cal.Core.Debug.Show</code> instances are provided for the <code>Cal.Core.Prelude</code> types.  Show instances for non-Prelude
 * types should be added to their home modules, not to this one.
 * 
 * @author Bo Ilic
 */
public final class CAL_Debug {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Core.Debug");

	/**
	 * This inner class (TypeClasses) contains constants
	 * and methods related to binding to CAL TypeClasses in the Cal.Core.Debug module.
	 */
	public static final class TypeClasses {
		/**
		 * The type class <code>Show</code> is intended for debug purposes only. In particular, the output string format
		 * may change- do not write code that depends on the particular output format of the <code>Cal.Core.Debug.show</code> method.
		 * <p>
		 * The <code>Show</code> type class can be used in <em>deriving</em> clauses. For a type with n arguments, <code>T a1 ... an</code>, this will
		 * automatically create an instance definition <code>instance (Show a1, Show a2, ..., Show an) =&gt; Show (T a1 ... an) where ...</code> using a
		 * canonical boilerplate definition for the instance methods. Deriving an instance of Show can expose details of the implementation of the
		 * the type to clients. Clients are not supposed to write production code that relys on the implementation of a Show instance,
		 * but this is still something worth being aware of.
		 * <p>
		 * When T is an algebraic type, the derived <code>Cal.Core.Debug.show</code> class method will display the fully qualified name of the data constructor,
		 * followed by calling show on each of the field values of the data constructor, in the order in which they are declared in the
		 * definition of the data constructor. If the data constructor has arguments, then the whole result string will be parenthesized.
		 * <p>
		 * When T is a foreign type whose underlying implementation type is a Java object type, the derived <code>Cal.Core.Prelude.equals</code> class method is
		 * implemented by calling the Java method java.lang.String.valueOf(Object) on the underlying Java object.
		 * <p>
		 * When T is a foreign type whose underlying type is a Java primitive type (char, boolean, byte, short, int, long, float, or double),
		 * the derived <code>Cal.Core.Prelude.equals</code> class method is implemented by calling the appropriate toString method in the standard Java wrapper classes
		 * such as java.lang.Int.toString(int).
		 * <p>
		 * As with all instances of Show, the behavior of the derived instances is subject to change.
		 */
		public static final QualifiedName Show = 
			QualifiedName.make(CAL_Debug.MODULE_NAME, "Show");

	}
	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Core.Debug module.
	 */
	public static final class TypeConstructors {
		/**
		 * Enumeration indicating the supported machine types (lecc or g). The purpose of this is for writing
		 * unit tests that depend on machine type. Do not use in production code.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Debug.machineType
		 * </dl>
		 */
		public static final QualifiedName MachineType = 
			QualifiedName.make(CAL_Debug.MODULE_NAME, "MachineType");

	}
	/**
	 * This inner class (DataConstructors) contains constants
	 * and methods related to binding to CAL DataConstructors in the Cal.Core.Debug module.
	 */
	public static final class DataConstructors {
		/*
		 * DataConstructors for the Cal.Core.Debug.MachineType data type.
		 */

		/**
		 * The Lazily Evaluating CAL Compiler (formerly Luke's Experimental CAL Compiler) machine type.
		 * Compiles CAL source to Java byte codes which are then run directly as a jvm program.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr MachineType_Lecc() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.MachineType_Lecc);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Debug.MachineType_Lecc.
		 * @see #MachineType_Lecc()
		 */
		public static final QualifiedName MachineType_Lecc = 
			QualifiedName.make(CAL_Debug.MODULE_NAME, "MachineType_Lecc");

		/**
		 * Ordinal of DataConstructor Cal.Core.Debug.MachineType_Lecc.
		 * @see #MachineType_Lecc()
		 */
		public static final int MachineType_Lecc_ordinal = 0;

		/**
		 * The g machine type. Compiles CAL source to in memory graph manipulation instruction sequences which are then
		 * run in an interpreter.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr MachineType_G() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.MachineType_G);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Debug.MachineType_G.
		 * @see #MachineType_G()
		 */
		public static final QualifiedName MachineType_G = 
			QualifiedName.make(CAL_Debug.MODULE_NAME, "MachineType_G");

		/**
		 * Ordinal of DataConstructor Cal.Core.Debug.MachineType_G.
		 * @see #MachineType_G()
		 */
		public static final int MachineType_G_ordinal = 1;

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Core.Debug module.
	 */
	public static final class Functions {
		/**
		 * Evaluates the given expressions (<code>f1</code> and <code>f2</code>) to WHNF and returns a record containing
		 * the time to evalute each expression and the difference expressed as an absolute and a percentage of the
		 * first time.
		 * @param f1 (CAL type: <code>a</code>)
		 * @param f2 (CAL type: <code>b</code>)
		 * @return (CAL type: <code>{percentDiff :: Cal.Core.Prelude.Double, time1 :: Cal.Core.Prelude.Long, time2 :: Cal.Core.Prelude.Long, timeDiff :: Cal.Core.Prelude.Long}</code>) 
		 */
		public static final SourceModel.Expr compareEvalTimes(SourceModel.Expr f1, SourceModel.Expr f2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.compareEvalTimes), f1, f2});
		}

		/**
		 * Name binding for function: compareEvalTimes.
		 * @see #compareEvalTimes(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName compareEvalTimes = 
			QualifiedName.make(CAL_Debug.MODULE_NAME, "compareEvalTimes");

		/**
		 * Returns the number of distinct indirection nodes in the internal graph representing the value that created <code>Cal.Core.Debug.InternalValueStats</code>.
		 * The distinct indirection node count provides a measure of how much uncompacted or wasted space the <code>Cal.Core.Prelude.CalValue</code> is taking.
		 * Indirection nodes which are shared and appear multiple times within the graph are only counted once.
		 * <p>
		 * This number is for debugging purposes only and should not be relied upon in production code. It is subject
		 * to change and its precise value will in general depend on the particular machine used.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Debug.internalValueStats
		 * </dl>
		 * 
		 * @param valueStats (CAL type: <code>Cal.Core.Debug.InternalValueStats</code>)
		 *          statistics about a particular CAL value
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the number of distinct indirection nodes in the graph of nodes of the value that created <code>Cal.Core.Debug.InternalValueStats</code>.
		 */
		public static final SourceModel.Expr distinctIndirectionNodeCount(SourceModel.Expr valueStats) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.distinctIndirectionNodeCount), valueStats});
		}

		/**
		 * Name binding for function: distinctIndirectionNodeCount.
		 * @see #distinctIndirectionNodeCount(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName distinctIndirectionNodeCount = 
			QualifiedName.make(
				CAL_Debug.MODULE_NAME, 
				"distinctIndirectionNodeCount");

		/**
		 * Returns the number of distinct nodes in the internal graph representing the value that created <code>Cal.Core.Debug.InternalValueStats</code>. 
		 * The distinct node count provides a measure of how much space the value is taking. Nodes which
		 * are shared and appear multiple times within the graph are only counted once.
		 * <p>
		 * This number is for debugging purposes only and should not be relied upon in production code. It is subject
		 * to change and its precise value will in general depend on the particular machine used.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Debug.internalValueStats
		 * </dl>
		 * 
		 * @param valueStats (CAL type: <code>Cal.Core.Debug.InternalValueStats</code>)
		 *          statistics about a particular CAL value
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the number of distinct nodes in the graph of nodes of the value that created <code>Cal.Core.Debug.InternalValueStats</code>.
		 */
		public static final SourceModel.Expr distinctNodeCount(SourceModel.Expr valueStats) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.distinctNodeCount), valueStats});
		}

		/**
		 * Name binding for function: distinctNodeCount.
		 * @see #distinctNodeCount(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName distinctNodeCount = 
			QualifiedName.make(CAL_Debug.MODULE_NAME, "distinctNodeCount");

		/**
		 * Evaluates the given expression <code>f</code> to WHNF and returns the time taken to evaluate it.
		 * @param f (CAL type: <code>a</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Long</code>) 
		 */
		public static final SourceModel.Expr evalTime(SourceModel.Expr f) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.evalTime), f});
		}

		/**
		 * Name binding for function: evalTime.
		 * @see #evalTime(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName evalTime = 
			QualifiedName.make(CAL_Debug.MODULE_NAME, "evalTime");

		/**
		 * 
		 * @return (CAL type: <code>Cal.Core.Prelude.Long</code>) 
		 *          retrieve the current system time in milliseconds.
		 */
		public static final SourceModel.Expr getSystemTime() {
			return SourceModel.Expr.Var.make(Functions.getSystemTime);
		}

		/**
		 * Name binding for function: getSystemTime.
		 * @see #getSystemTime()
		 */
		public static final QualifiedName getSystemTime = 
			QualifiedName.make(CAL_Debug.MODULE_NAME, "getSystemTime");

		/**
		 * Returns a list of the function names for which tracing is enabled. 
		 * These are the function for which tracing has been enabled by a call
		 * to <code>Cal.Core.Debug.setTracingEnabledFor</code>.
		 * Tracing for the functions in this list will occur regardless of the value
		 * returned by <code>Cal.Core.Debug.isTracingEnabled</code>.
		 * <p>
		 * Note that the particular build configuration of CAL must be capable of tracing before tracing will
		 * actually occur. Currently builds are capable of tracing if the system property 
		 * "org.openquark.cal.machine.debug_capable" is set.
		 * This is typically done by including the line "-Dorg.openquark.cal.machine.debug_capable"
		 * in the VM arguments when invoking Java on a program that makes use of the CAL platform. 
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Debug.setTracingEnabled, Cal.Core.Debug.setTracingEnabledFor
		 * </dl>
		 * 
		 * @param ignored (CAL type: <code>()</code>)
		 *          an ignored argument. <code>Cal.Core.Debug.getTraceEnabledFunctions</code> is not a pure function since the value it returns 
		 * depends on the state of the current execution context. The ignored argument is needed in order for this function not
		 * to be treated as a constant (CAF) always returning the same value.
		 * @return (CAL type: <code>[Cal.Core.Prelude.String]</code>) 
		 *          a <code>Cal.Collections.List</code> of <code>Cal.Core.Prelude.String</code> showing which functions have tracing enabled.
		 */
		public static final SourceModel.Expr getTraceEnabledFunctions(SourceModel.Expr ignored) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getTraceEnabledFunctions), ignored});
		}

		/**
		 * Name binding for function: getTraceEnabledFunctions.
		 * @see #getTraceEnabledFunctions(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getTraceEnabledFunctions = 
			QualifiedName.make(
				CAL_Debug.MODULE_NAME, 
				"getTraceEnabledFunctions");

		/**
		 * Calculates various statistics concerning the internal machine representation of the argument value. Currently these
		 * are the distinct node count, the distinct indirection node count and the number of shared nodes.
		 * The value is not evaluated or modified in any way by the computation.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Debug.distinctNodeCount, Cal.Core.Debug.distinctIndirectionNodeCount, Cal.Core.Debug.sharedNodeCount, Cal.Core.Debug.showInternal, Cal.Core.Debug.showInternalGraph
		 * </dl>
		 * 
		 * @param value (CAL type: <code>a</code>)
		 *          CAL value for which to compute statistics.
		 * @return (CAL type: <code>Cal.Core.Debug.InternalValueStats</code>) 
		 *          the gathered internal value statistics for the argument value.
		 */
		public static final SourceModel.Expr internalValueStats(SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.internalValueStats), value});
		}

		/**
		 * Name binding for function: internalValueStats.
		 * @see #internalValueStats(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName internalValueStats = 
			QualifiedName.make(CAL_Debug.MODULE_NAME, "internalValueStats");

		/**
		 * Returns true if tracing is currently enabled.
		 * <p>
		 * Note that the particular build configuration of CAL must be capable of tracing before tracing will
		 * actually occur, even if <code>Cal.Core.Debug.isTracingEnabled</code> returns <code>Cal.Core.Prelude.True</code>. Currently builds are capable 
		 * of tracing if the system property "org.openquark.cal.machine.debug_capable" is set.
		 * This is typically done by including the line "-Dorg.openquark.cal.machine.debug_capable"
		 * in the VM arguments when invoking Java on a program that makes use of the CAL platform. 
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Debug.setTracingEnabled, Cal.Core.Debug.traceShowsThreadName, Cal.Core.Debug.traceShowsFunctionArgs
		 * </dl>
		 * 
		 * @param ignored (CAL type: <code>()</code>)
		 *          an ignored argument. <code>Cal.Core.Debug.isTracingEnabled</code> is not a pure function since the value it returns 
		 * depends on the state of the current execution context. The ignored argument is needed in order for this function not
		 * to be treated as a constant (CAF) always returning the same value.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          true if tracing is enabled and false otherwise.
		 */
		public static final SourceModel.Expr isTracingEnabled(SourceModel.Expr ignored) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isTracingEnabled), ignored});
		}

		/**
		 * Name binding for function: isTracingEnabled.
		 * @see #isTracingEnabled(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isTracingEnabled = 
			QualifiedName.make(CAL_Debug.MODULE_NAME, "isTracingEnabled");

		/**
		 * The currently executing machine type. This function can be used to write unit tests that are only valid for
		 * a particular machine.
		 * @return (CAL type: <code>Cal.Core.Debug.MachineType</code>) 
		 *          the currently executing machine type.
		 */
		public static final SourceModel.Expr machineType() {
			return SourceModel.Expr.Var.make(Functions.machineType);
		}

		/**
		 * Name binding for function: machineType.
		 * @see #machineType()
		 */
		public static final QualifiedName machineType = 
			QualifiedName.make(CAL_Debug.MODULE_NAME, "machineType");

		/**
		 * Returns <code>Cal.Core.Prelude.True</code> if the evaluate times of the two expressions (<code>f1</code> and <code>f2</code>) are withing the 
		 * given percent margin.  ex. <code>sameEvalTimes expr1 expr2 10.0</code> returns true if the evaluation
		 * times of <code>expr1</code> and <code>expr2</code> are within 10 percent of each other.
		 * @param f1 (CAL type: <code>a</code>)
		 * @param f2 (CAL type: <code>b</code>)
		 * @param margin (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr sameEvalTimes(SourceModel.Expr f1, SourceModel.Expr f2, SourceModel.Expr margin) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.sameEvalTimes), f1, f2, margin});
		}

		/**
		 * @see #sameEvalTimes(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param f1
		 * @param f2
		 * @param margin
		 * @return the SourceModel.Expr representing an application of sameEvalTimes
		 */
		public static final SourceModel.Expr sameEvalTimes(SourceModel.Expr f1, SourceModel.Expr f2, double margin) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.sameEvalTimes), f1, f2, SourceModel.Expr.makeDoubleValue(margin)});
		}

		/**
		 * Name binding for function: sameEvalTimes.
		 * @see #sameEvalTimes(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName sameEvalTimes = 
			QualifiedName.make(CAL_Debug.MODULE_NAME, "sameEvalTimes");

		/**
		 * Performs the same as <code>Cal.Core.Debug.sameEvalTimes</code> but traces the result of <code>Cal.Core.Debug.compareEvalTimes</code>.
		 * @param f1 (CAL type: <code>a</code>)
		 * @param f2 (CAL type: <code>b</code>)
		 * @param margin (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr sameEvalTimesTrace(SourceModel.Expr f1, SourceModel.Expr f2, SourceModel.Expr margin) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.sameEvalTimesTrace), f1, f2, margin});
		}

		/**
		 * @see #sameEvalTimesTrace(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param f1
		 * @param f2
		 * @param margin
		 * @return the SourceModel.Expr representing an application of sameEvalTimesTrace
		 */
		public static final SourceModel.Expr sameEvalTimesTrace(SourceModel.Expr f1, SourceModel.Expr f2, double margin) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.sameEvalTimesTrace), f1, f2, SourceModel.Expr.makeDoubleValue(margin)});
		}

		/**
		 * Name binding for function: sameEvalTimesTrace.
		 * @see #sameEvalTimesTrace(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName sameEvalTimesTrace = 
			QualifiedName.make(CAL_Debug.MODULE_NAME, "sameEvalTimesTrace");

		/**
		 * Enables function tracing to also display the values of the function arguments as part of the trace.
		 * Note that no evaluation is done on the arguments in order to show their traced value.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Debug.traceShowsFunctionArgs, Cal.Core.Debug.setTracingEnabled, Cal.Core.Debug.setTraceShowsThreadName
		 * </dl>
		 * 
		 * @param traceShowsFunctionArgs (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 * @return (CAL type: <code>()</code>) 
		 */
		public static final SourceModel.Expr setTraceShowsFunctionArgs(SourceModel.Expr traceShowsFunctionArgs) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.setTraceShowsFunctionArgs), traceShowsFunctionArgs});
		}

		/**
		 * @see #setTraceShowsFunctionArgs(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param traceShowsFunctionArgs
		 * @return the SourceModel.Expr representing an application of setTraceShowsFunctionArgs
		 */
		public static final SourceModel.Expr setTraceShowsFunctionArgs(boolean traceShowsFunctionArgs) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.setTraceShowsFunctionArgs), SourceModel.Expr.makeBooleanValue(traceShowsFunctionArgs)});
		}

		/**
		 * Name binding for function: setTraceShowsFunctionArgs.
		 * @see #setTraceShowsFunctionArgs(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName setTraceShowsFunctionArgs = 
			QualifiedName.make(
				CAL_Debug.MODULE_NAME, 
				"setTraceShowsFunctionArgs");

		/**
		 * Enables function tracing to also display the name of the thread that is executing the particular function call 
		 * as part of the trace.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Debug.traceShowsThreadName, Cal.Core.Debug.setTraceShowsFunctionArgs, Cal.Core.Debug.setTracingEnabled
		 * </dl>
		 * 
		 * @param traceShowsThreadName (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 * @return (CAL type: <code>()</code>) 
		 */
		public static final SourceModel.Expr setTraceShowsThreadName(SourceModel.Expr traceShowsThreadName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.setTraceShowsThreadName), traceShowsThreadName});
		}

		/**
		 * @see #setTraceShowsThreadName(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param traceShowsThreadName
		 * @return the SourceModel.Expr representing an application of setTraceShowsThreadName
		 */
		public static final SourceModel.Expr setTraceShowsThreadName(boolean traceShowsThreadName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.setTraceShowsThreadName), SourceModel.Expr.makeBooleanValue(traceShowsThreadName)});
		}

		/**
		 * Name binding for function: setTraceShowsThreadName.
		 * @see #setTraceShowsThreadName(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName setTraceShowsThreadName = 
			QualifiedName.make(CAL_Debug.MODULE_NAME, "setTraceShowsThreadName");

		/**
		 * Enables tracing to occur for subsequent function calls.
		 * <p>
		 * Note that the particular build configuration of CAL must be capable of tracing before tracing will
		 * actually occur, even if <code>Cal.Core.Debug.isTracingEnabled</code> returns <code>Cal.Core.Prelude.True</code>. Currently builds are capable 
		 * of tracing if the system property "org.openquark.cal.machine.debug_capable" is set.
		 * This is typically done by including the line "-Dorg.openquark.cal.machine.debug_capable"
		 * in the VM arguments when invoking Java on a program that makes use of the CAL platform. 
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Debug.isTracingEnabled, Cal.Core.Debug.setTraceShowsThreadName, Cal.Core.Debug.setTraceShowsFunctionArgs
		 * </dl>
		 * 
		 * @param tracingEnabled (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 *          true to enable tracing of subsequent calls, false to disable tracing for subsequent calls.
		 * @return (CAL type: <code>()</code>) 
		 *          will always return ().
		 */
		public static final SourceModel.Expr setTracingEnabled(SourceModel.Expr tracingEnabled) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.setTracingEnabled), tracingEnabled});
		}

		/**
		 * @see #setTracingEnabled(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param tracingEnabled
		 * @return the SourceModel.Expr representing an application of setTracingEnabled
		 */
		public static final SourceModel.Expr setTracingEnabled(boolean tracingEnabled) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.setTracingEnabled), SourceModel.Expr.makeBooleanValue(tracingEnabled)});
		}

		/**
		 * Name binding for function: setTracingEnabled.
		 * @see #setTracingEnabled(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName setTracingEnabled = 
			QualifiedName.make(CAL_Debug.MODULE_NAME, "setTracingEnabled");

		/**
		 * Enables/disables tracing to occur for subsequent calls to the named function.
		 * This setting supersedes the tracing status set by <code>Cal.Core.Debug.setTracingEnabled</code>.
		 * <p>
		 * Note that the particular build configuration of CAL must be capable of tracing before tracing will
		 * actually occur, even if <code>Cal.Core.Debug.isTracingEnabled</code> returns <code>Cal.Core.Prelude.True</code>. Currently builds are capable 
		 * of tracing if the system property "org.openquark.cal.machine.debug_capable" is set.
		 * This is typically done by including the line "-Dorg.openquark.cal.machine.debug_capable"
		 * in the VM arguments when invoking Java on a program that makes use of the CAL platform. 
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Debug.isTracingEnabled, Cal.Core.Debug.setTraceShowsThreadName, Cal.Core.Debug.setTraceShowsFunctionArgs
		 * </dl>
		 * 
		 * @param functionName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the name of the function for which tracing will be enabled/disabled.
		 * @param tracingEnabled (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 *          true to enable tracing of subsequent calls, false to disable tracing for subsequent calls.
		 * @return (CAL type: <code>()</code>) 
		 *          will always return ().
		 */
		public static final SourceModel.Expr setTracingEnabledFor(SourceModel.Expr functionName, SourceModel.Expr tracingEnabled) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.setTracingEnabledFor), functionName, tracingEnabled});
		}

		/**
		 * @see #setTracingEnabledFor(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param functionName
		 * @param tracingEnabled
		 * @return the SourceModel.Expr representing an application of setTracingEnabledFor
		 */
		public static final SourceModel.Expr setTracingEnabledFor(java.lang.String functionName, boolean tracingEnabled) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.setTracingEnabledFor), SourceModel.Expr.makeStringValue(functionName), SourceModel.Expr.makeBooleanValue(tracingEnabled)});
		}

		/**
		 * Name binding for function: setTracingEnabledFor.
		 * @see #setTracingEnabledFor(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName setTracingEnabledFor = 
			QualifiedName.make(CAL_Debug.MODULE_NAME, "setTracingEnabledFor");

		/**
		 * Returns the number of shared nodes in the internal graph representing the value that created <code>Cal.Core.Debug.InternalValueStats</code>.
		 * The shared node count provides a measure of how graph-like (rather than tree-like) the <code>Cal.Core.Prelude.CalValue</code> is.
		 * <p>
		 * This number is for debugging purposes only and should not be relied upon in production code. It is subject
		 * to change and its precise value will in general depend on the particular machine used.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Debug.internalValueStats
		 * </dl>
		 * 
		 * @param valueStats (CAL type: <code>Cal.Core.Debug.InternalValueStats</code>)
		 *          statistics about a particular CAL value
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the number of shared nodes in the graph of nodes of the value that created <code>Cal.Core.Debug.InternalValueStats</code>.
		 */
		public static final SourceModel.Expr sharedNodeCount(SourceModel.Expr valueStats) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.sharedNodeCount), valueStats});
		}

		/**
		 * Name binding for function: sharedNodeCount.
		 * @see #sharedNodeCount(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName sharedNodeCount = 
			QualifiedName.make(CAL_Debug.MODULE_NAME, "sharedNodeCount");

		/**
		 * Constructs a string representation of the argument, which is a value of the instance type.
		 * The output is intended to be used for debugging purposes only- do not write code that depends on the
		 * particular output format.
		 * @param value (CAL type: <code>Cal.Core.Debug.Show a => a</code>)
		 *          the value whose string representation is to be returned.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          a string representation of the argument.
		 */
		public static final SourceModel.Expr show(SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.show), value});
		}

		/**
		 * Name binding for function: show.
		 * @see #show(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName show = 
			QualifiedName.make(CAL_Debug.MODULE_NAME, "show");

		/**
		 * Displays the internal representation of a value. This representation is subject to change,
		 * and should not be relied upon in production code. It is intended to assist in debugging, and for the 
		 * purposes of understanding the lazy evaluation behavior of CAL.
		 * <p>
		 * It is important to note that <code>Cal.Core.Debug.showInternal</code> does no evaluation of the value argument, and so does
		 * not change the order of reduction in any way. This is unlike <code>Cal.Core.Debug.show</code>, or most other means of
		 * displaying a value of a CAL computation. This can be a great advantage, however, the drawback is that
		 * <code>Cal.Core.Debug.showInternal</code> is displaying an internal form and thus can be less readable, and less customizable
		 * than using other means.
		 * <p>
		 * <code>Cal.Core.Debug.showInternal</code> will show some of the sharing of graph nodes in the graph structure of the value.
		 * If a node having one or more children is shared, its first appearance will be marked 
		 * e.g. &lt;&#64;nodeNumber = nodeText&gt;, and subsequent appearances will just display as &lt;&#64;nodeNumber&gt;.
		 * Nodes having zero children that are shared (such as function nodes or simple value nodes) are not
		 * shown as being shared. This makes the output easier to read while showing the most important sharing.
		 * To see more of the graph structure, use <code>Cal.Core.Debug.showInternalGraph</code>. 
		 * <p>
		 * <code>Cal.Core.Debug.showInternal</code> can fail to produce a representation of the <code>Cal.Core.Prelude.CalValue</code> if an exception in
		 * Object.toString() on one of the foreign objects held onto by the value graph occurs. In these cases, the returned
		 * value will indicate that a problem occurred and attempt to display a partial output.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Debug.showInternalGraph, Cal.Core.Debug.internalValueStats
		 * </dl>
		 * 
		 * @param value (CAL type: <code>a</code>)
		 *          CAL value for which to display the internal representation
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the internal representation of the value
		 */
		public static final SourceModel.Expr showInternal(SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showInternal), value});
		}

		/**
		 * Name binding for function: showInternal.
		 * @see #showInternal(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showInternal = 
			QualifiedName.make(CAL_Debug.MODULE_NAME, "showInternal");

		/**
		 * Displays the internal representation of a value, including information about shared nodes and indirection nodes.
		 * This representation is subject to change, and should not be relied upon in production code. It is intended to assist
		 * in debugging, and for the purposes of understanding the lazy evaluation behavior of CAL.
		 * <p>
		 * It is important to note that <code>Cal.Core.Debug.showInternalGraph</code> does no evaluation of the value argument, and so does
		 * not change the order of reduction in any way. This is unlike <code>Cal.Core.Debug.show</code>, or most other means of
		 * displaying a value of a CAL computation. This can be a great advantage, however, the drawback is that
		 * <code>Cal.Core.Debug.showInternalGraph</code> is displaying an internal form and thus can be less readable, and less customizable
		 * than using other means.
		 * <p>
		 * <code>Cal.Core.Debug.showInternalGraph</code> attempts to show more of the graph structure of the value. If a node is shared, 
		 * its first appearance will be marked e.g. &lt;&#64;nodeNumber = nodeText&gt;, and subsequent appearances will just
		 * display as &lt;&#64;nodeNumber&gt;. Indirections are shown using an asterix (*). 
		 * To see less of the graph structure, and potentially a more readable output, use <code>Cal.Core.Debug.showInternal</code>.
		 * <p>
		 * <code>Cal.Core.Debug.showInternalGraph</code> can fail to produce a representation of the <code>Cal.Core.Prelude.CalValue</code> if an exception in
		 * Object.toString() on one of the foreign objects held onto by the value graph occurs. In these cases, the returned
		 * value will indicate that a problem occurred and attempt to display a partial output.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Debug.showInternal, Cal.Core.Debug.internalValueStats
		 * </dl>
		 * 
		 * @param value (CAL type: <code>a</code>)
		 *          CAL value for which to display the internal representation
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the internal representation of the value
		 */
		public static final SourceModel.Expr showInternalGraph(SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showInternalGraph), value});
		}

		/**
		 * Name binding for function: showInternalGraph.
		 * @see #showInternalGraph(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showInternalGraph = 
			QualifiedName.make(CAL_Debug.MODULE_NAME, "showInternalGraph");

		/**
		 * <code>sleep</code> for a specified number of milliseconds and then return.
		 * This function can be useful in writing benchmarks to provide a dramatic slowdown.
		 * @param millis (CAL type: <code>Cal.Core.Prelude.Long</code>)
		 *          milliseconds to sleep for.
		 * @param valueToReturn (CAL type: <code>a</code>)
		 *          value to return after sleeping
		 * @return (CAL type: <code>a</code>) 
		 *          returns <code>valueToReturn</code> after sleeping for <code>millis</code> milliseconds.
		 */
		public static final SourceModel.Expr sleep(SourceModel.Expr millis, SourceModel.Expr valueToReturn) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.sleep), millis, valueToReturn});
		}

		/**
		 * @see #sleep(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param millis
		 * @param valueToReturn
		 * @return the SourceModel.Expr representing an application of sleep
		 */
		public static final SourceModel.Expr sleep(long millis, SourceModel.Expr valueToReturn) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.sleep), SourceModel.Expr.makeLongValue(millis), valueToReturn});
		}

		/**
		 * Name binding for function: sleep.
		 * @see #sleep(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName sleep = 
			QualifiedName.make(CAL_Debug.MODULE_NAME, "sleep");

		/**
		 * Prints its first argument to the standard error stream, and then returns its second argument.
		 * @param message (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the message to trace to the standard error stream
		 * @param value (CAL type: <code>a</code>)
		 *          the value to return
		 * @return (CAL type: <code>a</code>) 
		 *          the value
		 */
		public static final SourceModel.Expr trace(SourceModel.Expr message, SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.trace), message, value});
		}

		/**
		 * @see #trace(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param message
		 * @param value
		 * @return the SourceModel.Expr representing an application of trace
		 */
		public static final SourceModel.Expr trace(java.lang.String message, SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.trace), SourceModel.Expr.makeStringValue(message), value});
		}

		/**
		 * Name binding for function: trace.
		 * @see #trace(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName trace = 
			QualifiedName.make(CAL_Debug.MODULE_NAME, "trace");

		/**
		 * Augments a list of values such that a trace message will be displayed when members at a specified interval are accessed.
		 * This could be useful for tracking progress of a lengthy operation over a long list.
		 * @param message (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          a message to be included with each trace output, followed by the list index value
		 * @param interval (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the interval between list members for which trace messages will be displayed
		 * @param lst (CAL type: <code>[a]</code>)
		 *          the list to be augmented
		 * @return (CAL type: <code>[a]</code>) 
		 *          the list augmented with tracing information for every nth element
		 */
		public static final SourceModel.Expr traceListMemberAccess(SourceModel.Expr message, SourceModel.Expr interval, SourceModel.Expr lst) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.traceListMemberAccess), message, interval, lst});
		}

		/**
		 * @see #traceListMemberAccess(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param message
		 * @param interval
		 * @param lst
		 * @return the SourceModel.Expr representing an application of traceListMemberAccess
		 */
		public static final SourceModel.Expr traceListMemberAccess(java.lang.String message, int interval, SourceModel.Expr lst) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.traceListMemberAccess), SourceModel.Expr.makeStringValue(message), SourceModel.Expr.makeIntValue(interval), lst});
		}

		/**
		 * Name binding for function: traceListMemberAccess.
		 * @see #traceListMemberAccess(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName traceListMemberAccess = 
			QualifiedName.make(CAL_Debug.MODULE_NAME, "traceListMemberAccess");

		/**
		 * Prints its first argument to the standard output stream, and then returns its second argument.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Debug.trace
		 * </dl>
		 * 
		 * @param message (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the message to trace to the standard output stream
		 * @param value (CAL type: <code>a</code>)
		 *          the value to return
		 * @return (CAL type: <code>a</code>) 
		 *          the value
		 */
		public static final SourceModel.Expr traceOnStdOut(SourceModel.Expr message, SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.traceOnStdOut), message, value});
		}

		/**
		 * @see #traceOnStdOut(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param message
		 * @param value
		 * @return the SourceModel.Expr representing an application of traceOnStdOut
		 */
		public static final SourceModel.Expr traceOnStdOut(java.lang.String message, SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.traceOnStdOut), SourceModel.Expr.makeStringValue(message), value});
		}

		/**
		 * Name binding for function: traceOnStdOut.
		 * @see #traceOnStdOut(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName traceOnStdOut = 
			QualifiedName.make(CAL_Debug.MODULE_NAME, "traceOnStdOut");

		/**
		 * Traces the specified showable value and returns its original value.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Debug.trace
		 * </dl>
		 * 
		 * @param message (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          a message to be included with each trace output, followed by the Show form of the value
		 * @param val (CAL type: <code>Cal.Core.Debug.Show a => a</code>)
		 *          the value to be traced and returned
		 * @return (CAL type: <code>Cal.Core.Debug.Show a => a</code>) 
		 *          the original value after tracing it
		 */
		public static final SourceModel.Expr traceShowable(SourceModel.Expr message, SourceModel.Expr val) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.traceShowable), message, val});
		}

		/**
		 * @see #traceShowable(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param message
		 * @param val
		 * @return the SourceModel.Expr representing an application of traceShowable
		 */
		public static final SourceModel.Expr traceShowable(java.lang.String message, SourceModel.Expr val) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.traceShowable), SourceModel.Expr.makeStringValue(message), val});
		}

		/**
		 * Name binding for function: traceShowable.
		 * @see #traceShowable(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName traceShowable = 
			QualifiedName.make(CAL_Debug.MODULE_NAME, "traceShowable");

		/**
		 * Returns true if function tracing will also display the values of the function arguments as part of the trace.
		 * Note that no evaluation is done on the arguments in order to show their traced value.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Debug.setTraceShowsFunctionArgs, Cal.Core.Debug.isTracingEnabled, Cal.Core.Debug.traceShowsThreadName
		 * </dl>
		 * 
		 * @param ignored (CAL type: <code>()</code>)
		 *          an ignored argument. <code>Cal.Core.Debug.traceShowsFunctionArgs</code> is not a pure function since the value it returns 
		 * depends on the state of the current execution context. The ignored argument is needed in order for this function not
		 * to be treated as a constant (CAF) always returning the same value.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          true if tracing will show the values of the arguments passe
		 */
		public static final SourceModel.Expr traceShowsFunctionArgs(SourceModel.Expr ignored) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.traceShowsFunctionArgs), ignored});
		}

		/**
		 * Name binding for function: traceShowsFunctionArgs.
		 * @see #traceShowsFunctionArgs(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName traceShowsFunctionArgs = 
			QualifiedName.make(CAL_Debug.MODULE_NAME, "traceShowsFunctionArgs");

		/**
		 * Returns true if function tracing will also display the name of the thread that is executing the particular function
		 * call as part of the trace.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Debug.setTraceShowsThreadName, Cal.Core.Debug.isTracingEnabled, Cal.Core.Debug.traceShowsFunctionArgs
		 * </dl>
		 * 
		 * @param ignored (CAL type: <code>()</code>)
		 *          an ignored argument. <code>Cal.Core.Debug.traceShowsThreadName</code> is not a pure function since the value it returns 
		 * depends on the state of the current execution context. The ignored argument is needed in order for this function not
		 * to be treated as a constant (CAF) always returning the same value.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          true if tracing will show the name of the executing thread.
		 */
		public static final SourceModel.Expr traceShowsThreadName(SourceModel.Expr ignored) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.traceShowsThreadName), ignored});
		}

		/**
		 * Name binding for function: traceShowsThreadName.
		 * @see #traceShowsThreadName(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName traceShowsThreadName = 
			QualifiedName.make(CAL_Debug.MODULE_NAME, "traceShowsThreadName");

		/**
		 * Modifies the specified transformation function to trace the input value and the result value when evaluated.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Debug.traceTransformationDifferences, Cal.Core.Debug.trace, Cal.Core.Debug.traceShowable
		 * </dl>
		 * 
		 * @param message (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          a message to be included with each trace output, followed by the input and result values
		 * @param transformFn (CAL type: <code>(Cal.Core.Debug.Show a, Cal.Core.Debug.Show b) => a -> b</code>)
		 *          the function to be augmented to include tracing information
		 * @param arg_3 (CAL type: <code>Cal.Core.Debug.Show a => a</code>)
		 * @return (CAL type: <code>Cal.Core.Debug.Show b => b</code>) 
		 *          a modified version of the transformation function which includes tracing of the input and result values
		 */
		public static final SourceModel.Expr traceTransformation(SourceModel.Expr message, SourceModel.Expr transformFn, SourceModel.Expr arg_3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.traceTransformation), message, transformFn, arg_3});
		}

		/**
		 * @see #traceTransformation(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param message
		 * @param transformFn
		 * @param arg_3
		 * @return the SourceModel.Expr representing an application of traceTransformation
		 */
		public static final SourceModel.Expr traceTransformation(java.lang.String message, SourceModel.Expr transformFn, SourceModel.Expr arg_3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.traceTransformation), SourceModel.Expr.makeStringValue(message), transformFn, arg_3});
		}

		/**
		 * Name binding for function: traceTransformation.
		 * @see #traceTransformation(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName traceTransformation = 
			QualifiedName.make(CAL_Debug.MODULE_NAME, "traceTransformation");

		/**
		 * Modifies the specified transformation function to trace the input value and the result value when evaluated.
		 * The trace output will only be done if the transformed value is not equal to the input value.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Debug.traceTransformation, Cal.Core.Debug.trace, Cal.Core.Debug.traceShowable
		 * </dl>
		 * 
		 * @param message (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          a message to be included with each trace output, followed by the input and result values
		 * @param transformFn (CAL type: <code>(Cal.Core.Debug.Show a, Cal.Core.Prelude.Eq a) => a -> a</code>)
		 *          the function to be augmented to include tracing information
		 * @param arg_3 (CAL type: <code>(Cal.Core.Debug.Show a, Cal.Core.Prelude.Eq a) => a</code>)
		 * @return (CAL type: <code>(Cal.Core.Debug.Show a, Cal.Core.Prelude.Eq a) => a</code>) 
		 *          a modified version of the transformation function which includes tracing of the input and result values
		 */
		public static final SourceModel.Expr traceTransformationDifferences(SourceModel.Expr message, SourceModel.Expr transformFn, SourceModel.Expr arg_3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.traceTransformationDifferences), message, transformFn, arg_3});
		}

		/**
		 * @see #traceTransformationDifferences(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param message
		 * @param transformFn
		 * @param arg_3
		 * @return the SourceModel.Expr representing an application of traceTransformationDifferences
		 */
		public static final SourceModel.Expr traceTransformationDifferences(java.lang.String message, SourceModel.Expr transformFn, SourceModel.Expr arg_3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.traceTransformationDifferences), SourceModel.Expr.makeStringValue(message), transformFn, arg_3});
		}

		/**
		 * Name binding for function: traceTransformationDifferences.
		 * @see #traceTransformationDifferences(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName traceTransformationDifferences = 
			QualifiedName.make(
				CAL_Debug.MODULE_NAME, 
				"traceTransformationDifferences");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = -702463554;

}
