/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_Memoize.java)
 * was generated from CAL module: Cal.Core.Memoize.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Core.Memoize module from Java code.
 *  
 * Creation date: Fri Mar 16 13:11:56 PST 2007
 * --!>
 *  
 */

package org.openquark.cal.module.Cal.Core;

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
public final class CAL_Memoize {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Core.Memoize");

	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Core.Memoize module.
	 */
	public static final class TypeConstructors {
		/**
		 * This is the type of the actual cache object. 
		 * Essentially it is a foreign type which holds on to an <code>Cal.Core.Prelude.CalValue</code>.  The held
		 * <code>Cal.Core.Prelude.CalValue</code> will be the current Map of parameter value to result.
		 */
		public static final QualifiedName MemoCache = 
			QualifiedName.make(CAL_Memoize.MODULE_NAME, "MemoCache");

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Core.Memoize module.
	 */
	public static final class Functions {
		/**
		 * Creates a memoizing version of a function.
		 * This new function will cache/retrieve results in a <code>MemoCache</code>
		 * using the argument value as a key. 
		 * The type of the key must be an instance of <code>Cal.Core.Prelude.Ord</code>.
		 * <p>
		 * Note: memoization applies to functions of type <code>(a -&gt; b)</code> so applying
		 * this to a supercombinator of arity &gt; 1 will generate a cache that
		 * caches function instances.  Not a cache that caches results ignoring
		 * some of the function arguments.  See <code>Cal.Test.Core.Memoize_Tests.memoizedVersionOfFourParam</code> for the pattern to use
		 * when memoizing a multi argument supercombinator.
		 * 
		 * @param f (CAL type: <code>Cal.Core.Prelude.Ord a => a -> b</code>)
		 *          the function to be memoized.
		 * @param arg_2 (CAL type: <code>Cal.Core.Prelude.Ord a => a</code>)
		 * @return (CAL type: <code>b</code>) 
		 *          a memoizing version of <code>f</code>.
		 */
		public static final SourceModel.Expr memo(SourceModel.Expr f, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.memo), f, arg_2});
		}

		/**
		 * Name binding for function: memo.
		 * @see #memo(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName memo = 
			QualifiedName.make(CAL_Memoize.MODULE_NAME, "memo");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = 2077119856;

}
