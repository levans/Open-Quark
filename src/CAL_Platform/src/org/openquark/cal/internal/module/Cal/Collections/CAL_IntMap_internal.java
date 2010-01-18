/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_IntMap_internal.java)
 * was generated from CAL module: Cal.Collections.IntMap.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Collections.IntMap module from Java code.
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
 * An efficient implementation of maps from <code>Cal.Core.Prelude.Int</code> to values.
 * <p>
 * The implementation is based on <em>big-endian patricia trees</em>. This data structure 
 * performs especially well on binary operations like <code>Cal.Collections.IntMap.union</code> and <code>Cal.Collections.IntMap.intersection</code>.
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
 * with a maximum of W -- the number of bits in an <code>Cal.Core.Prelude.Int</code>.
 * <p>
 * This module is an adaptation of functionality from Daan Leijen's DData collections library for Haskell.
 * The library was obtained from <a href='http://www.cs.uu.nl/~daan/ddata.html'>http://www.cs.uu.nl/~daan/ddata.html</a>.
 * See the file <code>ThirdPartyComponents/ThirdPartyComponents.txt</code> for the DData license.
 * 
 * 
 * <dl><dt><b>See Also:</b>
 * <dd><b>Modules:</b> Cal.Collections.Map, Cal.Collections.LongMap
 * </dl>
 * 
 * @author Bo Ilic
 */
public final class CAL_IntMap_internal {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Collections.IntMap");

	/**
	 * This inner class (DataConstructors) contains constants
	 * and methods related to binding to CAL DataConstructors in the Cal.Collections.IntMap module.
	 */
	public static final class DataConstructors {
		/*
		 * DataConstructors for the Cal.Collections.IntMap.IntMap data type.
		 */

		/**
		 * Binding for DataConstructor: Cal.Collections.IntMap.Nil.
		 * @return the SourceModule.Expr representing an application of Cal.Collections.IntMap.Nil
		 */
		public static final SourceModel.Expr Nil() {
			return SourceModel.Expr.DataCons.make(DataConstructors.Nil);
		}

		/**
		 * Name binding for DataConstructor: Cal.Collections.IntMap.Nil.
		 * @see #Nil()
		 */
		public static final QualifiedName Nil = 
			QualifiedName.make(CAL_IntMap_internal.MODULE_NAME, "Nil");

		/**
		 * Ordinal of DataConstructor Cal.Collections.IntMap.Nil.
		 * @see #Nil()
		 */
		public static final int Nil_ordinal = 0;

		/**
		 * Binding for DataConstructor: Cal.Collections.IntMap.Tip.
		 * @param key
		 * @param value
		 * @return the SourceModule.Expr representing an application of Cal.Collections.IntMap.Tip
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
		public static final SourceModel.Expr Tip(int key, SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.Tip), SourceModel.Expr.makeIntValue(key), value});
		}

		/**
		 * Name binding for DataConstructor: Cal.Collections.IntMap.Tip.
		 * @see #Tip(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName Tip = 
			QualifiedName.make(CAL_IntMap_internal.MODULE_NAME, "Tip");

		/**
		 * Ordinal of DataConstructor Cal.Collections.IntMap.Tip.
		 * @see #Tip(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int Tip_ordinal = 1;

		/**
		 * Binding for DataConstructor: Cal.Collections.IntMap.Bin.
		 * @param prefix
		 * @param mask
		 * @param leftMap
		 * @param rightMap
		 * @return the SourceModule.Expr representing an application of Cal.Collections.IntMap.Bin
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
		public static final SourceModel.Expr Bin(int prefix, int mask, SourceModel.Expr leftMap, SourceModel.Expr rightMap) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.Bin), SourceModel.Expr.makeIntValue(prefix), SourceModel.Expr.makeIntValue(mask), leftMap, rightMap});
		}

		/**
		 * Name binding for DataConstructor: Cal.Collections.IntMap.Bin.
		 * @see #Bin(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName Bin = 
			QualifiedName.make(CAL_IntMap_internal.MODULE_NAME, "Bin");

		/**
		 * Ordinal of DataConstructor Cal.Collections.IntMap.Bin.
		 * @see #Bin(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int Bin_ordinal = 2;

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Collections.IntMap module.
	 */
	public static final class Functions {
		/**
		 * Helper binding method for function: arbitraryIntMap. 
		 * @return the SourceModule.expr representing an application of arbitraryIntMap
		 */
		public static final SourceModel.Expr arbitraryIntMap() {
			return SourceModel.Expr.Var.make(Functions.arbitraryIntMap);
		}

		/**
		 * Name binding for function: arbitraryIntMap.
		 * @see #arbitraryIntMap()
		 */
		public static final QualifiedName arbitraryIntMap = 
			QualifiedName.make(
				CAL_IntMap_internal.MODULE_NAME, 
				"arbitraryIntMap");

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
		public static final SourceModel.Expr bin(int p, int m, SourceModel.Expr l, SourceModel.Expr r) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.bin), SourceModel.Expr.makeIntValue(p), SourceModel.Expr.makeIntValue(m), l, r});
		}

		/**
		 * Name binding for function: bin.
		 * @see #bin(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName bin = 
			QualifiedName.make(CAL_IntMap_internal.MODULE_NAME, "bin");

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
		public static final SourceModel.Expr branchMask(int p1, int p2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.branchMask), SourceModel.Expr.makeIntValue(p1), SourceModel.Expr.makeIntValue(p2)});
		}

		/**
		 * Name binding for function: branchMask.
		 * @see #branchMask(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName branchMask = 
			QualifiedName.make(CAL_IntMap_internal.MODULE_NAME, "branchMask");

		/**
		 * Helper binding method for function: coarbitraryIntMap. 
		 * @param map
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of coarbitraryIntMap
		 */
		public static final SourceModel.Expr coarbitraryIntMap(SourceModel.Expr map, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.coarbitraryIntMap), map, arg_2});
		}

		/**
		 * Name binding for function: coarbitraryIntMap.
		 * @see #coarbitraryIntMap(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName coarbitraryIntMap = 
			QualifiedName.make(
				CAL_IntMap_internal.MODULE_NAME, 
				"coarbitraryIntMap");

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
			QualifiedName.make(CAL_IntMap_internal.MODULE_NAME, "foldR");

		/**
		 * Helper binding method for function: inputIntMap. 
		 * @param list
		 * @return the SourceModule.expr representing an application of inputIntMap
		 */
		public static final SourceModel.Expr inputIntMap(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inputIntMap), list});
		}

		/**
		 * Name binding for function: inputIntMap.
		 * @see #inputIntMap(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName inputIntMap = 
			QualifiedName.make(CAL_IntMap_internal.MODULE_NAME, "inputIntMap");

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
		public static final SourceModel.Expr join(int p1, SourceModel.Expr t1, int p2, SourceModel.Expr t2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.join), SourceModel.Expr.makeIntValue(p1), t1, SourceModel.Expr.makeIntValue(p2), t2});
		}

		/**
		 * Name binding for function: join.
		 * @see #join(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName join = 
			QualifiedName.make(CAL_IntMap_internal.MODULE_NAME, "join");

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
			QualifiedName.make(CAL_IntMap_internal.MODULE_NAME, "mapAccumL");

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
			QualifiedName.make(CAL_IntMap_internal.MODULE_NAME, "mapAccumR");

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
		public static final SourceModel.Expr mask(int i, int m) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.mask), SourceModel.Expr.makeIntValue(i), SourceModel.Expr.makeIntValue(m)});
		}

		/**
		 * Name binding for function: mask.
		 * @see #mask(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName mask = 
			QualifiedName.make(CAL_IntMap_internal.MODULE_NAME, "mask");

		/**
		 * Helper binding method for function: match. 
		 * @param i
		 * @param p
		 * @param m
		 * @return the SourceModule.expr representing an application of match
		 */
		public static final SourceModel.Expr match(SourceModel.Expr i, SourceModel.Expr p, SourceModel.Expr m) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.match), i, p, m});
		}

		/**
		 * @see #match(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param i
		 * @param p
		 * @param m
		 * @return the SourceModel.Expr representing an application of match
		 */
		public static final SourceModel.Expr match(int i, int p, int m) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.match), SourceModel.Expr.makeIntValue(i), SourceModel.Expr.makeIntValue(p), SourceModel.Expr.makeIntValue(m)});
		}

		/**
		 * Name binding for function: match.
		 * @see #match(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName match = 
			QualifiedName.make(CAL_IntMap_internal.MODULE_NAME, "match");

		/**
		 * Helper binding method for function: nomatch. 
		 * @param i
		 * @param p
		 * @param m
		 * @return the SourceModule.expr representing an application of nomatch
		 */
		public static final SourceModel.Expr nomatch(SourceModel.Expr i, SourceModel.Expr p, SourceModel.Expr m) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.nomatch), i, p, m});
		}

		/**
		 * @see #nomatch(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param i
		 * @param p
		 * @param m
		 * @return the SourceModel.Expr representing an application of nomatch
		 */
		public static final SourceModel.Expr nomatch(int i, int p, int m) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.nomatch), SourceModel.Expr.makeIntValue(i), SourceModel.Expr.makeIntValue(p), SourceModel.Expr.makeIntValue(m)});
		}

		/**
		 * Name binding for function: nomatch.
		 * @see #nomatch(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName nomatch = 
			QualifiedName.make(CAL_IntMap_internal.MODULE_NAME, "nomatch");

		/**
		 * Helper binding method for function: outputIntMap. 
		 * @param m
		 * @return the SourceModule.expr representing an application of outputIntMap
		 */
		public static final SourceModel.Expr outputIntMap(SourceModel.Expr m) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputIntMap), m});
		}

		/**
		 * Name binding for function: outputIntMap.
		 * @see #outputIntMap(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName outputIntMap = 
			QualifiedName.make(CAL_IntMap_internal.MODULE_NAME, "outputIntMap");

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
		public static final SourceModel.Expr shorter(int m1, int m2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shorter), SourceModel.Expr.makeIntValue(m1), SourceModel.Expr.makeIntValue(m2)});
		}

		/**
		 * Name binding for function: shorter.
		 * @see #shorter(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shorter = 
			QualifiedName.make(CAL_IntMap_internal.MODULE_NAME, "shorter");

		/**
		 * Helper binding method for function: showIntMap. 
		 * @param m
		 * @return the SourceModule.expr representing an application of showIntMap
		 */
		public static final SourceModel.Expr showIntMap(SourceModel.Expr m) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showIntMap), m});
		}

		/**
		 * Name binding for function: showIntMap.
		 * @see #showIntMap(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showIntMap = 
			QualifiedName.make(CAL_IntMap_internal.MODULE_NAME, "showIntMap");

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
			QualifiedName.make(CAL_IntMap_internal.MODULE_NAME, "submapCmp");

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
		public static final SourceModel.Expr zero(int i, int m) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.zero), SourceModel.Expr.makeIntValue(i), SourceModel.Expr.makeIntValue(m)});
		}

		/**
		 * Name binding for function: zero.
		 * @see #zero(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName zero = 
			QualifiedName.make(CAL_IntMap_internal.MODULE_NAME, "zero");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = 1693505942;

}
