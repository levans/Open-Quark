/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_Parallel.java)
 * was generated from CAL module: Cal.Experimental.Concurrent.Parallel.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Experimental.Concurrent.Parallel module from Java code.
 *  
 * Creation date: Tue Oct 23 09:52:35 PDT 2007
 * --!>
 *  
 */

package org.openquark.cal.module.Cal.Experimental.Concurrent;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * Various functions for parallel evaluation in CAL. CAL must be started with
 * the system propery org.openquark.cal.runtime.lecc.concurrent_runtime for the
 * functions in this module to truly behave in a parallel fashion. Otherwise
 * they result in a sequential approximations of the parallel functionality.
 * @author Bo Ilic
 */
public final class CAL_Parallel {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Experimental.Concurrent.Parallel");

	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Experimental.Concurrent.Parallel module.
	 */
	public static final class TypeConstructors {
		/**
		 * An Executor is used to run tasks, typically on worker threads.
		 * Different executors can be used to get different threading behaviour (such as thread pooling).
		 */
		public static final QualifiedName Executor = 
			QualifiedName.make(CAL_Parallel.MODULE_NAME, "Executor");

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Experimental.Concurrent.Parallel module.
	 */
	public static final class Functions {
		/**
		 * An executor with a pool of N threads for executing tasks.
		 * @param nThreads (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the number of threads in the pool
		 * @return (CAL type: <code>Cal.Experimental.Concurrent.Parallel.Executor</code>) 
		 */
		public static final SourceModel.Expr fixedThreadPoolExecutor(SourceModel.Expr nThreads) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fixedThreadPoolExecutor), nThreads});
		}

		/**
		 * @see #fixedThreadPoolExecutor(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param nThreads
		 * @return the SourceModel.Expr representing an application of fixedThreadPoolExecutor
		 */
		public static final SourceModel.Expr fixedThreadPoolExecutor(int nThreads) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fixedThreadPoolExecutor), SourceModel.Expr.makeIntValue(nThreads)});
		}

		/**
		 * Name binding for function: fixedThreadPoolExecutor.
		 * @see #fixedThreadPoolExecutor(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fixedThreadPoolExecutor = 
			QualifiedName.make(
				CAL_Parallel.MODULE_NAME, 
				"fixedThreadPoolExecutor");

		/**
		 * If CAL is started with the system property
		 * org.openquark.cal.runtime.lecc.concurrent_runtime then this function provides
		 * a way of spawning a new thread to perform a computation in parallel. It is
		 * often used in conjunction with <code>Prelude.seq</code> to precisely specify how
		 * a problem is sequenced and parallelized. In particular, if <code>par x y</code>
		 * is evaluated to weak-head normal form, then a thread is spawned to evaluate
		 * <code>x</code> to WHNF, and in parallel, <code>y</code> is also evaluated to WHNF
		 * with the function then returning <code>y</code>. Note this function may return
		 * before <code>x</code> has actually finished evaluating to WHNF.
		 * <p>
		 * If CAL is not started with the above system property then this function just
		 * returns <code>y</code> without touching <code>x</code>.
		 * 
		 * @param x (CAL type: <code>a</code>)
		 * @param y (CAL type: <code>b</code>)
		 * @return (CAL type: <code>b</code>) 
		 *          <code>y</code>, but a thread is spawned to evaluate <code>x</code> to WHNF in
		 * parallel.
		 */
		public static final SourceModel.Expr par(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.par), x, y});
		}

		/**
		 * Name binding for function: par.
		 * @see #par(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName par = 
			QualifiedName.make(CAL_Parallel.MODULE_NAME, "par");

		/**
		 * If CAL is started with the system property
		 * org.openquark.cal.runtime.lecc.concurrent_runtime then this function provides
		 * a way of performing a computation in parallel using the specified executor. 
		 * It is often used in conjunction with <code>Prelude.seq</code> to precisely specify how
		 * a problem is sequenced and parallelized. In particular, if <code>par x y</code>
		 * is evaluated to weak-head normal form, then a task is spawned to evaluate
		 * <code>x</code> to WHNF, and in parallel, <code>y</code> is also evaluated to WHNF
		 * with the function then returning <code>y</code>. Note this function may return
		 * before <code>x</code> has actually finished evaluating to WHNF.
		 * <p>
		 * If CAL is not started with the above system property then this function just
		 * returns <code>y</code> without touching <code>x</code>.
		 * 
		 * @param executor (CAL type: <code>Cal.Experimental.Concurrent.Parallel.Executor</code>)
		 *          the executor to be used to perform the parallel task
		 * @param x (CAL type: <code>a</code>)
		 *          a value to be evaluated to WHNF in parallel
		 * @param y (CAL type: <code>b</code>)
		 *          a value to be evaluated normally
		 * @return (CAL type: <code>b</code>) 
		 *          <code>y</code>, but a task is spawned to evaluate <code>x</code> to 
		 * WHNF in parallel.
		 */
		public static final SourceModel.Expr parallelExecute(SourceModel.Expr executor, SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.parallelExecute), executor, x, y});
		}

		/**
		 * Name binding for function: parallelExecute.
		 * @see #parallelExecute(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName parallelExecute = 
			QualifiedName.make(CAL_Parallel.MODULE_NAME, "parallelExecute");

		/**
		 * If CAL is started with the system property
		 * org.openquark.cal.runtime.lecc.concurrent_runtime then this function provides
		 * a parallel version of <code>Cal.Collections.List.map</code>. This function will not terminate for
		 * infinite lists, unlike <code>Cal.Collections.List.map</code>. A separate evaluation thread is
		 * created for each element of <code>list</code> to begin evaluating the application
		 * of <code>mapFunction</code> to each element to weak-head normal form (WHNF).
		 * <p>
		 * If CAL is not started with the above system property then this function
		 * behaves similar to <code>Cal.Collections.List.map</code> except that the elements of the list are
		 * first traversed (without being evaluated), so it will still hang for an
		 * infinite list.
		 * <p>
		 * If you want to ensure that the components of the list are actually evaluated
		 * to WHNF prior to the result being returned, just compose with <code>Cal.Collections.List.strictList</code>.
		 * 
		 * @param mapFunction (CAL type: <code>a -> b</code>)
		 * @param list (CAL type: <code>[a]</code>)
		 * @return (CAL type: <code>[b]</code>) 
		 *          applies <code>mapFunction</code> to each element of <code>list</code>, and
		 * evaluates these applications, in parallel, to WHNF. The function may return
		 * prior to all elements being evaluated to WHNF.
		 */
		public static final SourceModel.Expr parallelMap(SourceModel.Expr mapFunction, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.parallelMap), mapFunction, list});
		}

		/**
		 * Name binding for function: parallelMap.
		 * @see #parallelMap(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName parallelMap = 
			QualifiedName.make(CAL_Parallel.MODULE_NAME, "parallelMap");

		/**
		 * If CAL is started with the system property
		 * org.openquark.cal.runtime.lecc.concurrent_runtime then this function returns
		 * the original pair, except that a thread is started for each component to
		 * evaluate it to weak-head normal form (WHNF). These 2 threads may not be
		 * finished doing so before this function returns, but if a component is needed
		 * in WHNF prior to this by client code, then graph reduction will block until
		 * the component has finished evaluation to WHNF.
		 * <p>
		 * If CAL is not started with the above system property then this function
		 * behaves like <code>Cal.Core.Prelude.id</code> on pairs.
		 * <p>
		 * If you want to ensure that the components of the pair have finished being
		 * evaluated to WHNF prior to the pair being returned, just compose with <code>Cal.Core.Record.strictRecord</code>.
		 * 
		 * @param pair (CAL type: <code>(a, b)</code>)
		 * @return (CAL type: <code>(a, b)</code>) 
		 *          pair, where the first and second components are in the process of
		 * being evaluated to WHNF.
		 */
		public static final SourceModel.Expr parallelTuple2(SourceModel.Expr pair) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.parallelTuple2), pair});
		}

		/**
		 * Name binding for function: parallelTuple2.
		 * @see #parallelTuple2(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName parallelTuple2 = 
			QualifiedName.make(CAL_Parallel.MODULE_NAME, "parallelTuple2");

		/**
		 * The synchronous executor will run tasks directly on the calling thread when submitted.
		 * @return (CAL type: <code>Cal.Experimental.Concurrent.Parallel.Executor</code>) 
		 */
		public static final SourceModel.Expr synchronousExecutor() {
			return SourceModel.Expr.Var.make(Functions.synchronousExecutor);
		}

		/**
		 * Name binding for function: synchronousExecutor.
		 * @see #synchronousExecutor()
		 */
		public static final QualifiedName synchronousExecutor = 
			QualifiedName.make(CAL_Parallel.MODULE_NAME, "synchronousExecutor");

		/**
		 * The thread-per-task executor will spawn a new thread for each execution.
		 * @return (CAL type: <code>Cal.Experimental.Concurrent.Parallel.Executor</code>) 
		 */
		public static final SourceModel.Expr threadPerTaskExecutor() {
			return SourceModel.Expr.Var.make(Functions.threadPerTaskExecutor);
		}

		/**
		 * Name binding for function: threadPerTaskExecutor.
		 * @see #threadPerTaskExecutor()
		 */
		public static final QualifiedName threadPerTaskExecutor = 
			QualifiedName.make(
				CAL_Parallel.MODULE_NAME, 
				"threadPerTaskExecutor");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = -1499113264;

}
