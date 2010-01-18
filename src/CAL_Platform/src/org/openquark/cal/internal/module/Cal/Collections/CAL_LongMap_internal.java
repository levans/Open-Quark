/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_LongMap_internal.java)
 * was generated from CAL module: Cal.Collections.LongMap.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Collections.LongMap module from Java code.
 *  
 * Creation date: Fri Mar 16 13:11:57 PST 2007
 * --!>
 *  
 */

package org.openquark.cal.internal.module.Cal.Collections;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * An efficient implementation of maps from <code>Cal.Core.Prelude.Long</code> to values.
 * <p>
 * The implementation is based on <em>big-endian patricia trees</em>. This data structure 
 * performs especially well on binary operations like <code>Cal.Collections.LongMap.union</code> and <code>Cal.Collections.LongMap.intersection</code>.
 * <ul>
 *  <li>
 *   Chris Okasaki and Andy Gill,  "Fast Mergeable Integer Maps",
 *   Workshop on ML, September 1998, pages 77--86, <a href='http://www.cse.ogi.edu/~andy/pub/finite.htm'>http://www.cse.ogi.edu/~andy/pub/finite.htm</a>
 *  </li>
 *  <li>
 *   D.R. Morrison, "PATRICIA -- Practical Algorithm To Retrieve Information
 *   Coded In Alphanumeric", Journal of the ACM, 15(4), October 1968, pages 514--534.
 *   
 *  </li>
 * </ul>
 * <p>
 * Many operations have a worst-case complexity of O(min(n,W)). This means that the
 * operation can become linear in the number of elements 
 * with a maximum of W -- the number of bits in an <code>Cal.Core.Prelude.Long</code>.
 * <p>
 * This module is an adaptation of functionality from Daan Leijen's DData collections library for Haskell.
 * The library was obtained from <a href='http://www.cs.uu.nl/~daan/ddata.html'>http://www.cs.uu.nl/~daan/ddata.html</a>.
 * See the file <code>ThirdPartyComponents/ThirdPartyComponents.txt</code> for the DData license.
 * 
 * 
 * <dl><dt><b>See Also:</b>
 * <dd><b>Modules:</b> Cal.Collections.Map, Cal.Collections.IntMap
 * </dl>
 * 
 * @author Bo Ilic
 */
public final class CAL_LongMap_internal {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Collections.LongMap");

	/**
	 * This inner class (DataConstructors) contains constants
	 * and methods related to binding to CAL DataConstructors in the Cal.Collections.LongMap module.
	 */
	public static final class DataConstructors {
		/*
		 * DataConstructors for the Cal.Collections.LongMap.LongMap data type.
		 */

		/**
		 * Binding for DataConstructor: Cal.Collections.LongMap.Nil.
		 * @return the SourceModule.Expr representing an application of Cal.Collections.LongMap.Nil
		 */
		public static final SourceModel.Expr Nil() {
			return SourceModel.Expr.DataCons.make(DataConstructors.Nil);
		}

		/**
		 * Name binding for DataConstructor: Cal.Collections.LongMap.Nil.
		 * @see #Nil()
		 */
		public static final QualifiedName Nil = 
			QualifiedName.make(CAL_LongMap_internal.MODULE_NAME, "Nil");

		/**
		 * Ordinal of DataConstructor Cal.Collections.LongMap.Nil.
		 * @see #Nil()
		 */
		public static final int Nil_ordinal = 0;

		/**
		 * Binding for DataConstructor: Cal.Collections.LongMap.Tip.
		 * @param key
		 * @param value
		 * @return the SourceModule.Expr representing an application of Cal.Collections.LongMap.Tip
		 */
		public static final SourceModel.Expr Tip(SourceModel.Expr key, SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.Tip), key, value});
		}

		/**
		 * @see #Tip(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param key
		 * @param value
		 * @return org.openquark.cal.compiler.SourceModel.Expr
		 */
		public static final SourceModel.Expr Tip(long key, SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.Tip), SourceModel.Expr.makeLongValue(key), value});
		}

		/**
		 * Name binding for DataConstructor: Cal.Collections.LongMap.Tip.
		 * @see #Tip(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName Tip = 
			QualifiedName.make(CAL_LongMap_internal.MODULE_NAME, "Tip");

		/**
		 * Ordinal of DataConstructor Cal.Collections.LongMap.Tip.
		 * @see #Tip(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int Tip_ordinal = 1;

		/**
		 * Binding for DataConstructor: Cal.Collections.LongMap.Bin.
		 * @param prefix
		 * @param mask
		 * @param leftMap
		 * @param rightMap
		 * @return the SourceModule.Expr representing an application of Cal.Collections.LongMap.Bin
		 */
		public static final SourceModel.Expr Bin(SourceModel.Expr prefix, SourceModel.Expr mask, SourceModel.Expr leftMap, SourceModel.Expr rightMap) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.Bin), prefix, mask, leftMap, rightMap});
		}

		/**
		 * @see #Bin(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param prefix
		 * @param mask
		 * @param leftMap
		 * @param rightMap
		 * @return org.openquark.cal.compiler.SourceModel.Expr
		 */
		public static final SourceModel.Expr Bin(long prefix, long mask, SourceModel.Expr leftMap, SourceModel.Expr rightMap) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.Bin), SourceModel.Expr.makeLongValue(prefix), SourceModel.Expr.makeLongValue(mask), leftMap, rightMap});
		}

		/**
		 * Name binding for DataConstructor: Cal.Collections.LongMap.Bin.
		 * @see #Bin(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName Bin = 
			QualifiedName.make(CAL_LongMap_internal.MODULE_NAME, "Bin");

		/**
		 * Ordinal of DataConstructor Cal.Collections.LongMap.Bin.
		 * @see #Bin(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int Bin_ordinal = 2;

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Collections.LongMap module.
	 */
	public static final class Functions {
		/**
		 * Helper binding method for function: arbitraryLongMap. 
		 * @return the SourceModule.expr representing an application of arbitraryLongMap
		 */
		public static final SourceModel.Expr arbitraryLongMap() {
			return SourceModel.Expr.Var.make(Functions.arbitraryLongMap);
		}

		/**
		 * Name binding for function: arbitraryLongMap.
		 * @see #arbitraryLongMap()
		 */
		public static final QualifiedName arbitraryLongMap = 
			QualifiedName.make(
				CAL_LongMap_internal.MODULE_NAME, 
				"arbitraryLongMap");

		/**
		 * Helper binding method for function: bin. 
		 * @param p
		 * @param m
		 * @param l
		 * @param r
		 * @return the SourceModule.expr representing an application of bin
		 */
		public static final SourceModel.Expr bin(SourceModel.Expr p, SourceModel.Expr m, SourceModel.Expr l, SourceModel.Expr r) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.bin), p, m, l, r});
		}

		/**
		 * @see #bin(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param p
		 * @param m
		 * @param l
		 * @param r
		 * @return the SourceModel.Expr representing an application of bin
		 */
		public static final SourceModel.Expr bin(long p, long m, SourceModel.Expr l, SourceModel.Expr r) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.bin), SourceModel.Expr.makeLongValue(p), SourceModel.Expr.makeLongValue(m), l, r});
		}

		/**
		 * Name binding for function: bin.
		 * @see #bin(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName bin = 
			QualifiedName.make(CAL_LongMap_internal.MODULE_NAME, "bin");

		/**
		 * Helper binding method for function: branchMask. 
		 * @param p1
		 * @param p2
		 * @return the SourceModule.expr representing an application of branchMask
		 */
		public static final SourceModel.Expr branchMask(SourceModel.Expr p1, SourceModel.Expr p2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.branchMask), p1, p2});
		}

		/**
		 * @see #branchMask(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param p1
		 * @param p2
		 * @return the SourceModel.Expr representing an application of branchMask
		 */
		public static final SourceModel.Expr branchMask(long p1, long p2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.branchMask), SourceModel.Expr.makeLongValue(p1), SourceModel.Expr.makeLongValue(p2)});
		}

		/**
		 * Name binding for function: branchMask.
		 * @see #branchMask(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName branchMask = 
			QualifiedName.make(CAL_LongMap_internal.MODULE_NAME, "branchMask");

		/**
		 * Helper binding method for function: coarbitraryLongMap. 
		 * @param map
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of coarbitraryLongMap
		 */
		public static final SourceModel.Expr coarbitraryLongMap(SourceModel.Expr map, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.coarbitraryLongMap), map, arg_2});
		}

		/**
		 * Name binding for function: coarbitraryLongMap.
		 * @see #coarbitraryLongMap(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName coarbitraryLongMap = 
			QualifiedName.make(
				CAL_LongMap_internal.MODULE_NAME, 
				"coarbitraryLongMap");

		/**
		 * Helper binding method for function: foldR. 
		 * @param f
		 * @param z
		 * @param t
		 * @return the SourceModule.expr representing an application of foldR
		 */
		public static final SourceModel.Expr foldR(SourceModel.Expr f, SourceModel.Expr z, SourceModel.Expr t) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.foldR), f, z, t});
		}

		/**
		 * Name binding for function: foldR.
		 * @see #foldR(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName foldR = 
			QualifiedName.make(CAL_LongMap_internal.MODULE_NAME, "foldR");

		/**
		 * Helper binding method for function: inputLongMap. 
		 * @param list
		 * @return the SourceModule.expr representing an application of inputLongMap
		 */
		public static final SourceModel.Expr inputLongMap(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inputLongMap), list});
		}

		/**
		 * Name binding for function: inputLongMap.
		 * @see #inputLongMap(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName inputLongMap = 
			QualifiedName.make(CAL_LongMap_internal.MODULE_NAME, "inputLongMap");

		/**
		 * Helper binding method for function: join. 
		 * @param p1
		 * @param t1
		 * @param p2
		 * @param t2
		 * @return the SourceModule.expr representing an application of join
		 */
		public static final SourceModel.Expr join(SourceModel.Expr p1, SourceModel.Expr t1, SourceModel.Expr p2, SourceModel.Expr t2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.join), p1, t1, p2, t2});
		}

		/**
		 * @see #join(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param p1
		 * @param t1
		 * @param p2
		 * @param t2
		 * @return the SourceModel.Expr representing an application of join
		 */
		public static final SourceModel.Expr join(long p1, SourceModel.Expr t1, long p2, SourceModel.Expr t2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.join), SourceModel.Expr.makeLongValue(p1), t1, SourceModel.Expr.makeLongValue(p2), t2});
		}

		/**
		 * Name binding for function: join.
		 * @see #join(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName join = 
			QualifiedName.make(CAL_LongMap_internal.MODULE_NAME, "join");

		/**
		 * Helper binding method for function: mapAccumL. 
		 * @param f
		 * @param a
		 * @param t
		 * @return the SourceModule.expr representing an application of mapAccumL
		 */
		public static final SourceModel.Expr mapAccumL(SourceModel.Expr f, SourceModel.Expr a, SourceModel.Expr t) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.mapAccumL), f, a, t});
		}

		/**
		 * Name binding for function: mapAccumL.
		 * @see #mapAccumL(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName mapAccumL = 
			QualifiedName.make(CAL_LongMap_internal.MODULE_NAME, "mapAccumL");

		/**
		 * Helper binding method for function: mapAccumR. 
		 * @param f
		 * @param a
		 * @param t
		 * @return the SourceModule.expr representing an application of mapAccumR
		 */
		public static final SourceModel.Expr mapAccumR(SourceModel.Expr f, SourceModel.Expr a, SourceModel.Expr t) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.mapAccumR), f, a, t});
		}

		/**
		 * Name binding for function: mapAccumR.
		 * @see #mapAccumR(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName mapAccumR = 
			QualifiedName.make(CAL_LongMap_internal.MODULE_NAME, "mapAccumR");

		/**
		 * Helper binding method for function: mask. 
		 * @param i
		 * @param m
		 * @return the SourceModule.expr representing an application of mask
		 */
		public static final SourceModel.Expr mask(SourceModel.Expr i, SourceModel.Expr m) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.mask), i, m});
		}

		/**
		 * @see #mask(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param i
		 * @param m
		 * @return the SourceModel.Expr representing an application of mask
		 */
		public static final SourceModel.Expr mask(long i, long m) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.mask), SourceModel.Expr.makeLongValue(i), SourceModel.Expr.makeLongValue(m)});
		}

		/**
		 * Name binding for function: mask.
		 * @see #mask(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName mask = 
			QualifiedName.make(CAL_LongMap_internal.MODULE_NAME, "mask");

		/**
		 * Helper binding method for function: outputLongMap. 
		 * @param m
		 * @return the SourceModule.expr representing an application of outputLongMap
		 */
		public static final SourceModel.Expr outputLongMap(SourceModel.Expr m) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputLongMap), m});
		}

		/**
		 * Name binding for function: outputLongMap.
		 * @see #outputLongMap(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName outputLongMap = 
			QualifiedName.make(
				CAL_LongMap_internal.MODULE_NAME, 
				"outputLongMap");

		/**
		 * Helper binding method for function: shorter. 
		 * @param m1
		 * @param m2
		 * @return the SourceModule.expr representing an application of shorter
		 */
		public static final SourceModel.Expr shorter(SourceModel.Expr m1, SourceModel.Expr m2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shorter), m1, m2});
		}

		/**
		 * @see #shorter(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param m1
		 * @param m2
		 * @return the SourceModel.Expr representing an application of shorter
		 */
		public static final SourceModel.Expr shorter(long m1, long m2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shorter), SourceModel.Expr.makeLongValue(m1), SourceModel.Expr.makeLongValue(m2)});
		}

		/**
		 * Name binding for function: shorter.
		 * @see #shorter(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shorter = 
			QualifiedName.make(CAL_LongMap_internal.MODULE_NAME, "shorter");

		/**
		 * Helper binding method for function: showLongMap. 
		 * @param m
		 * @return the SourceModule.expr representing an application of showLongMap
		 */
		public static final SourceModel.Expr showLongMap(SourceModel.Expr m) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showLongMap), m});
		}

		/**
		 * Name binding for function: showLongMap.
		 * @see #showLongMap(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showLongMap = 
			QualifiedName.make(CAL_LongMap_internal.MODULE_NAME, "showLongMap");

		/**
		 * Helper binding method for function: submapCmp. 
		 * @param pred
		 * @param t1
		 * @param t2
		 * @return the SourceModule.expr representing an application of submapCmp
		 */
		public static final SourceModel.Expr submapCmp(SourceModel.Expr pred, SourceModel.Expr t1, SourceModel.Expr t2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.submapCmp), pred, t1, t2});
		}

		/**
		 * Name binding for function: submapCmp.
		 * @see #submapCmp(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName submapCmp = 
			QualifiedName.make(CAL_LongMap_internal.MODULE_NAME, "submapCmp");

		/**
		 * Helper binding method for function: zero. 
		 * @param i
		 * @param m
		 * @return the SourceModule.expr representing an application of zero
		 */
		public static final SourceModel.Expr zero(SourceModel.Expr i, SourceModel.Expr m) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.zero), i, m});
		}

		/**
		 * @see #zero(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param i
		 * @param m
		 * @return the SourceModel.Expr representing an application of zero
		 */
		public static final SourceModel.Expr zero(long i, long m) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.zero), SourceModel.Expr.makeLongValue(i), SourceModel.Expr.makeLongValue(m)});
		}

		/**
		 * Name binding for function: zero.
		 * @see #zero(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName zero = 
			QualifiedName.make(CAL_LongMap_internal.MODULE_NAME, "zero");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = -582225905;

}
