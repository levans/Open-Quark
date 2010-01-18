/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_Memoize_internal.java)
 * was generated from CAL module: Cal.Core.Memoize.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Core.Memoize module from Java code.
 *  
 * Creation date: Fri Mar 16 13:11:57 PST 2007
 * --!>
 *  
 */

package org.openquark.cal.internal.module.Cal.Core;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * Implementation of memoization: based on the paper 'Disposable Memo Functions' by Cook and Launchbury.
 * A memo-function remembers the arguments to which it has been applied, together with the result.  
 * If applied to a repeated argument, memo functions return the cached answer rather than recomputing
 * it from scratch.  Memoization improves the time complexity of algorithms with repeated 
 * computations, but at the cost of higher memory use.
 * <p>
 * The key idea in this module is to provide a function: <code>memo :: (Ord a) =&gt; (a -&gt; b) -&gt; (a -&gt; b); </code>
 * When applied to a function <code>Cal.Core.Memoize.memo</code> returns an equivalent memoized function.  When  all
 * references to this new function have been dropped the cached values will be discarded.
 * 
 * @author Raymond Cypher
 */
public final class CAL_Memoize_internal {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Core.Memoize");

	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Core.Memoize module.
	 */
	public static final class Functions {
		/**
		 * Access the cache using the argument value.
		 * If a result is already cached return it.
		 * If no result is cached create a new result, add it to the cache, and return it.
		 * @param functionCachePair (CAL type: <code>Cal.Core.Prelude.Ord a => (a -> b, Cal.Core.Memoize.MemoCache)</code>)
		 *          a tuple containing the memoized function and the associated cache.
		 * @param argument (CAL type: <code>Cal.Core.Prelude.Ord a => a</code>)
		 *          the argument to the memoized function.
		 * @return (CAL type: <code>b</code>) 
		 *          a cached result of applying the memoized function to the argument
		 */
		public static final SourceModel.Expr access(SourceModel.Expr functionCachePair, SourceModel.Expr argument) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.access), functionCachePair, argument});
		}

		/**
		 * Name binding for function: access.
		 * @see #access(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName access = 
			QualifiedName.make(CAL_Memoize_internal.MODULE_NAME, "access");

		/**
		 * Retrieve the current result map from the <code>Cal.Core.Memoize.MemoCache</code>.
		 * @param memoCache (CAL type: <code>Cal.Core.Memoize.MemoCache</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.CalValue</code>) 
		 */
		public static final SourceModel.Expr memoCache_getMap(SourceModel.Expr memoCache) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.memoCache_getMap), memoCache});
		}

		/**
		 * Name binding for function: memoCache_getMap.
		 * @see #memoCache_getMap(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName memoCache_getMap = 
			QualifiedName.make(
				CAL_Memoize_internal.MODULE_NAME, 
				"memoCache_getMap");

		/**
		 * Set the current result map into the <code>Cal.Core.Memoize.MemoCache</code>.
		 * @param memoCache (CAL type: <code>Cal.Core.Memoize.MemoCache</code>)
		 * @param map (CAL type: <code>Cal.Core.Prelude.CalValue</code>)
		 * @return (CAL type: <code>()</code>) 
		 */
		public static final SourceModel.Expr memoCache_setMap(SourceModel.Expr memoCache, SourceModel.Expr map) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.memoCache_setMap), memoCache, map});
		}

		/**
		 * Name binding for function: memoCache_setMap.
		 * @see #memoCache_setMap(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName memoCache_setMap = 
			QualifiedName.make(
				CAL_Memoize_internal.MODULE_NAME, 
				"memoCache_setMap");

		/**
		 * Constructor for the foreign type <code>Cal.Core.Memoize.MemoCache</code>.  It takes an <code>Cal.Core.Prelude.CalValue</code> which will be an instance of
		 * <code>Cal.Collections.Map.Map</code>.
		 * @param map (CAL type: <code>Cal.Core.Prelude.CalValue</code>)
		 * @return (CAL type: <code>Cal.Core.Memoize.MemoCache</code>) 
		 */
		public static final SourceModel.Expr newMemoCache(SourceModel.Expr map) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.newMemoCache), map});
		}

		/**
		 * Name binding for function: newMemoCache.
		 * @see #newMemoCache(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName newMemoCache = 
			QualifiedName.make(CAL_Memoize_internal.MODULE_NAME, "newMemoCache");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = -1935662654;

}
