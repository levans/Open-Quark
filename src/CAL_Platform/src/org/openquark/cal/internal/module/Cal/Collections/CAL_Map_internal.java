/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_Map_internal.java)
 * was generated from CAL module: Cal.Collections.Map.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Collections.Map module from Java code.
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
 * An efficient implementation of maps from keys to values.
 * <p>
 * The implementation of Map is based on <em>size balanced</em> binary trees (or trees of <em>bounded balance</em>) as described by:
 * <ul>
 *  <li>
 *   Stephen Adams, "Efficient sets: a balancing act", Journal of Functional
 *   Programming 3(4):553-562, October 1993, <a href='http://www.swiss.ai.mit.edu/~adams/BB'>http://www.swiss.ai.mit.edu/~adams/BB</a>.
 *  </li>
 *  <li>
 *   J. Nievergelt and E.M. Reingold, "Binary search trees of bounded balance",
 *   SIAM journal of computing 2(1), March 1973.
 *   
 *  </li>
 * </ul>
 * <p>
 * This module is an adaptation of functionality from Daan Leijen's DData collections library for Haskell.
 * The library was obtained from <a href='http://www.cs.uu.nl/~daan/ddata.html'>http://www.cs.uu.nl/~daan/ddata.html</a>.
 * See the file <code>ThirdPartyComponents/ThirdPartyComponents.txt</code> for the DData license.
 * 
 * 
 * <dl><dt><b>See Also:</b>
 * <dd><b>Modules:</b> Cal.Collections.IntMap, Cal.Collections.LongMap
 * </dl>
 * 
 * @author Bo Ilic
 */
public final class CAL_Map_internal {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Collections.Map");

	/**
	 * This inner class (DataConstructors) contains constants
	 * and methods related to binding to CAL DataConstructors in the Cal.Collections.Map module.
	 */
	public static final class DataConstructors {
		/*
		 * DataConstructors for the Cal.Collections.Map.Map data type.
		 */

		/**
		 * Binding for DataConstructor: Cal.Collections.Map.Tip.
		 * @return the SourceModule.Expr representing an application of Cal.Collections.Map.Tip
		 */
		public static final SourceModel.Expr Tip() {
			return SourceModel.Expr.DataCons.make(DataConstructors.Tip);
		}

		/**
		 * Name binding for DataConstructor: Cal.Collections.Map.Tip.
		 * @see #Tip()
		 */
		public static final QualifiedName Tip = 
			QualifiedName.make(CAL_Map_internal.MODULE_NAME, "Tip");

		/**
		 * Ordinal of DataConstructor Cal.Collections.Map.Tip.
		 * @see #Tip()
		 */
		public static final int Tip_ordinal = 0;

		/**
		 * Binding for DataConstructor: Cal.Collections.Map.Bin.
		 * @param size
		 * @param key
		 * @param value
		 * @param leftMap
		 * @param rightMap
		 * @return the SourceModule.Expr representing an application of Cal.Collections.Map.Bin
		 */
		public static final SourceModel.Expr Bin(SourceModel.Expr size, SourceModel.Expr key, SourceModel.Expr value, SourceModel.Expr leftMap, SourceModel.Expr rightMap) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.Bin), size, key, value, leftMap, rightMap});
		}

		/**
		 * @see #Bin(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param size
		 * @param key
		 * @param value
		 * @param leftMap
		 * @param rightMap
		 * @return org.openquark.cal.compiler.SourceModel.Expr
		 */
		public static final SourceModel.Expr Bin(int size, SourceModel.Expr key, SourceModel.Expr value, SourceModel.Expr leftMap, SourceModel.Expr rightMap) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.Bin), SourceModel.Expr.makeIntValue(size), key, value, leftMap, rightMap});
		}

		/**
		 * Name binding for DataConstructor: Cal.Collections.Map.Bin.
		 * @see #Bin(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName Bin = 
			QualifiedName.make(CAL_Map_internal.MODULE_NAME, "Bin");

		/**
		 * Ordinal of DataConstructor Cal.Collections.Map.Bin.
		 * @see #Bin(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int Bin_ordinal = 1;

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Collections.Map module.
	 */
	public static final class Functions {
		/**
		 * Helper binding method for function: balance. 
		 * @param k
		 * @param x
		 * @param l
		 * @param r
		 * @return the SourceModule.expr representing an application of balance
		 */
		public static final SourceModel.Expr balance(SourceModel.Expr k, SourceModel.Expr x, SourceModel.Expr l, SourceModel.Expr r) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.balance), k, x, l, r});
		}

		/**
		 * Name binding for function: balance.
		 * @see #balance(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName balance = 
			QualifiedName.make(CAL_Map_internal.MODULE_NAME, "balance");

		/**
		 * Helper binding method for function: balanced. 
		 * @param t
		 * @return the SourceModule.expr representing an application of balanced
		 */
		public static final SourceModel.Expr balanced(SourceModel.Expr t) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.balanced), t});
		}

		/**
		 * Name binding for function: balanced.
		 * @see #balanced(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName balanced = 
			QualifiedName.make(CAL_Map_internal.MODULE_NAME, "balanced");

		/**
		 * Helper binding method for function: bin. 
		 * @param k
		 * @param x
		 * @param l
		 * @param r
		 * @return the SourceModule.expr representing an application of bin
		 */
		public static final SourceModel.Expr bin(SourceModel.Expr k, SourceModel.Expr x, SourceModel.Expr l, SourceModel.Expr r) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.bin), k, x, l, r});
		}

		/**
		 * Name binding for function: bin.
		 * @see #bin(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName bin = 
			QualifiedName.make(CAL_Map_internal.MODULE_NAME, "bin");

		/**
		 * Helper binding method for function: deepEquals. 
		 * @param t1
		 * @param t2
		 * @return the SourceModule.expr representing an application of deepEquals
		 */
		public static final SourceModel.Expr deepEquals(SourceModel.Expr t1, SourceModel.Expr t2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.deepEquals), t1, t2});
		}

		/**
		 * Name binding for function: deepEquals.
		 * @see #deepEquals(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName deepEquals = 
			QualifiedName.make(CAL_Map_internal.MODULE_NAME, "deepEquals");

		/**
		 * Helper binding method for function: delta. 
		 * @return the SourceModule.expr representing an application of delta
		 */
		public static final SourceModel.Expr delta() {
			return SourceModel.Expr.Var.make(Functions.delta);
		}

		/**
		 * Name binding for function: delta.
		 * @see #delta()
		 */
		public static final QualifiedName delta = 
			QualifiedName.make(CAL_Map_internal.MODULE_NAME, "delta");

		/**
		 * Helper binding method for function: directBuild. 
		 * @param n
		 * @param list
		 * @return the SourceModule.expr representing an application of directBuild
		 */
		public static final SourceModel.Expr directBuild(SourceModel.Expr n, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.directBuild), n, list});
		}

		/**
		 * @see #directBuild(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param n
		 * @param list
		 * @return the SourceModel.Expr representing an application of directBuild
		 */
		public static final SourceModel.Expr directBuild(int n, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.directBuild), SourceModel.Expr.makeIntValue(n), list});
		}

		/**
		 * Name binding for function: directBuild.
		 * @see #directBuild(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName directBuild = 
			QualifiedName.make(CAL_Map_internal.MODULE_NAME, "directBuild");

		/**
		 * Helper binding method for function: doubleL. 
		 * @param k1
		 * @param x1
		 * @param t1
		 * @param t5
		 * @return the SourceModule.expr representing an application of doubleL
		 */
		public static final SourceModel.Expr doubleL(SourceModel.Expr k1, SourceModel.Expr x1, SourceModel.Expr t1, SourceModel.Expr t5) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleL), k1, x1, t1, t5});
		}

		/**
		 * Name binding for function: doubleL.
		 * @see #doubleL(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleL = 
			QualifiedName.make(CAL_Map_internal.MODULE_NAME, "doubleL");

		/**
		 * Helper binding method for function: doubleR. 
		 * @param k1
		 * @param x1
		 * @param t5
		 * @param t4
		 * @return the SourceModule.expr representing an application of doubleR
		 */
		public static final SourceModel.Expr doubleR(SourceModel.Expr k1, SourceModel.Expr x1, SourceModel.Expr t5, SourceModel.Expr t4) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleR), k1, x1, t5, t4});
		}

		/**
		 * Name binding for function: doubleR.
		 * @see #doubleR(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleR = 
			QualifiedName.make(CAL_Map_internal.MODULE_NAME, "doubleR");

		/**
		 * Helper binding method for function: emptySpecialized. 
		 * @return the SourceModule.expr representing an application of emptySpecialized
		 */
		public static final SourceModel.Expr emptySpecialized() {
			return SourceModel.Expr.Var.make(Functions.emptySpecialized);
		}

		/**
		 * Name binding for function: emptySpecialized.
		 * @see #emptySpecialized()
		 */
		public static final QualifiedName emptySpecialized = 
			QualifiedName.make(CAL_Map_internal.MODULE_NAME, "emptySpecialized");

		/**
		 * Helper binding method for function: equalsMap. 
		 * @param m1
		 * @param m2
		 * @return the SourceModule.expr representing an application of equalsMap
		 */
		public static final SourceModel.Expr equalsMap(SourceModel.Expr m1, SourceModel.Expr m2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.equalsMap), m1, m2});
		}

		/**
		 * Name binding for function: equalsMap.
		 * @see #equalsMap(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName equalsMap = 
			QualifiedName.make(CAL_Map_internal.MODULE_NAME, "equalsMap");

		/**
		 * Helper binding method for function: filterGt. 
		 * @param cmp
		 * @param m
		 * @return the SourceModule.expr representing an application of filterGt
		 */
		public static final SourceModel.Expr filterGt(SourceModel.Expr cmp, SourceModel.Expr m) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.filterGt), cmp, m});
		}

		/**
		 * Name binding for function: filterGt.
		 * @see #filterGt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName filterGt = 
			QualifiedName.make(CAL_Map_internal.MODULE_NAME, "filterGt");

		/**
		 * Helper binding method for function: filterLt. 
		 * @param cmp
		 * @param m
		 * @return the SourceModule.expr representing an application of filterLt
		 */
		public static final SourceModel.Expr filterLt(SourceModel.Expr cmp, SourceModel.Expr m) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.filterLt), cmp, m});
		}

		/**
		 * Name binding for function: filterLt.
		 * @see #filterLt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName filterLt = 
			QualifiedName.make(CAL_Map_internal.MODULE_NAME, "filterLt");

		/**
		 * Helper binding method for function: foldI. 
		 * @param f
		 * @param z
		 * @param m
		 * @return the SourceModule.expr representing an application of foldI
		 */
		public static final SourceModel.Expr foldI(SourceModel.Expr f, SourceModel.Expr z, SourceModel.Expr m) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.foldI), f, z, m});
		}

		/**
		 * Name binding for function: foldI.
		 * @see #foldI(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName foldI = 
			QualifiedName.make(CAL_Map_internal.MODULE_NAME, "foldI");

		/**
		 * Helper binding method for function: foldL. 
		 * @param f
		 * @param z
		 * @param m
		 * @return the SourceModule.expr representing an application of foldL
		 */
		public static final SourceModel.Expr foldL(SourceModel.Expr f, SourceModel.Expr z, SourceModel.Expr m) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.foldL), f, z, m});
		}

		/**
		 * Name binding for function: foldL.
		 * @see #foldL(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName foldL = 
			QualifiedName.make(CAL_Map_internal.MODULE_NAME, "foldL");

		/**
		 * Helper binding method for function: foldR. 
		 * @param f
		 * @param z
		 * @param m
		 * @return the SourceModule.expr representing an application of foldR
		 */
		public static final SourceModel.Expr foldR(SourceModel.Expr f, SourceModel.Expr z, SourceModel.Expr m) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.foldR), f, z, m});
		}

		/**
		 * Name binding for function: foldR.
		 * @see #foldR(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName foldR = 
			QualifiedName.make(CAL_Map_internal.MODULE_NAME, "foldR");

		/**
		 * Helper binding method for function: glue. 
		 * @param l
		 * @param r
		 * @return the SourceModule.expr representing an application of glue
		 */
		public static final SourceModel.Expr glue(SourceModel.Expr l, SourceModel.Expr r) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.glue), l, r});
		}

		/**
		 * Name binding for function: glue.
		 * @see #glue(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName glue = 
			QualifiedName.make(CAL_Map_internal.MODULE_NAME, "glue");

		/**
		 * Helper binding method for function: hedgeDiff. 
		 * @param cmplo
		 * @param cmphi
		 * @param m
		 * @param t
		 * @return the SourceModule.expr representing an application of hedgeDiff
		 */
		public static final SourceModel.Expr hedgeDiff(SourceModel.Expr cmplo, SourceModel.Expr cmphi, SourceModel.Expr m, SourceModel.Expr t) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.hedgeDiff), cmplo, cmphi, m, t});
		}

		/**
		 * Name binding for function: hedgeDiff.
		 * @see #hedgeDiff(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName hedgeDiff = 
			QualifiedName.make(CAL_Map_internal.MODULE_NAME, "hedgeDiff");

		/**
		 * Helper binding method for function: hedgeDiffWithKey. 
		 * @param f
		 * @param cmplo
		 * @param cmphi
		 * @param map1
		 * @param map2
		 * @return the SourceModule.expr representing an application of hedgeDiffWithKey
		 */
		public static final SourceModel.Expr hedgeDiffWithKey(SourceModel.Expr f, SourceModel.Expr cmplo, SourceModel.Expr cmphi, SourceModel.Expr map1, SourceModel.Expr map2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.hedgeDiffWithKey), f, cmplo, cmphi, map1, map2});
		}

		/**
		 * Name binding for function: hedgeDiffWithKey.
		 * @see #hedgeDiffWithKey(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName hedgeDiffWithKey = 
			QualifiedName.make(CAL_Map_internal.MODULE_NAME, "hedgeDiffWithKey");

		/**
		 * Helper binding method for function: hedgeUnionL. 
		 * @param cmplo
		 * @param cmphi
		 * @param t1
		 * @param t2
		 * @return the SourceModule.expr representing an application of hedgeUnionL
		 */
		public static final SourceModel.Expr hedgeUnionL(SourceModel.Expr cmplo, SourceModel.Expr cmphi, SourceModel.Expr t1, SourceModel.Expr t2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.hedgeUnionL), cmplo, cmphi, t1, t2});
		}

		/**
		 * Name binding for function: hedgeUnionL.
		 * @see #hedgeUnionL(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName hedgeUnionL = 
			QualifiedName.make(CAL_Map_internal.MODULE_NAME, "hedgeUnionL");

		/**
		 * Helper binding method for function: hedgeUnionR. 
		 * @param cmplo
		 * @param cmphi
		 * @param t1
		 * @param t2
		 * @return the SourceModule.expr representing an application of hedgeUnionR
		 */
		public static final SourceModel.Expr hedgeUnionR(SourceModel.Expr cmplo, SourceModel.Expr cmphi, SourceModel.Expr t1, SourceModel.Expr t2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.hedgeUnionR), cmplo, cmphi, t1, t2});
		}

		/**
		 * Name binding for function: hedgeUnionR.
		 * @see #hedgeUnionR(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName hedgeUnionR = 
			QualifiedName.make(CAL_Map_internal.MODULE_NAME, "hedgeUnionR");

		/**
		 * Helper binding method for function: hedgeUnionWithKey. 
		 * @param f
		 * @param cmplo
		 * @param cmphi
		 * @param t1
		 * @param t2
		 * @return the SourceModule.expr representing an application of hedgeUnionWithKey
		 */
		public static final SourceModel.Expr hedgeUnionWithKey(SourceModel.Expr f, SourceModel.Expr cmplo, SourceModel.Expr cmphi, SourceModel.Expr t1, SourceModel.Expr t2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.hedgeUnionWithKey), f, cmplo, cmphi, t1, t2});
		}

		/**
		 * Name binding for function: hedgeUnionWithKey.
		 * @see #hedgeUnionWithKey(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName hedgeUnionWithKey = 
			QualifiedName.make(
				CAL_Map_internal.MODULE_NAME, 
				"hedgeUnionWithKey");

		/**
		 * Helper binding method for function: inputMap. 
		 * @param list
		 * @return the SourceModule.expr representing an application of inputMap
		 */
		public static final SourceModel.Expr inputMap(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inputMap), list});
		}

		/**
		 * Name binding for function: inputMap.
		 * @see #inputMap(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName inputMap = 
			QualifiedName.make(CAL_Map_internal.MODULE_NAME, "inputMap");

		/**
		 * Helper binding method for function: insertMax. 
		 * @param kx
		 * @param x
		 * @param t
		 * @return the SourceModule.expr representing an application of insertMax
		 */
		public static final SourceModel.Expr insertMax(SourceModel.Expr kx, SourceModel.Expr x, SourceModel.Expr t) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.insertMax), kx, x, t});
		}

		/**
		 * Name binding for function: insertMax.
		 * @see #insertMax(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName insertMax = 
			QualifiedName.make(CAL_Map_internal.MODULE_NAME, "insertMax");

		/**
		 * Helper binding method for function: insertMin. 
		 * @param kx
		 * @param x
		 * @param t
		 * @return the SourceModule.expr representing an application of insertMin
		 */
		public static final SourceModel.Expr insertMin(SourceModel.Expr kx, SourceModel.Expr x, SourceModel.Expr t) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.insertMin), kx, x, t});
		}

		/**
		 * Name binding for function: insertMin.
		 * @see #insertMin(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName insertMin = 
			QualifiedName.make(CAL_Map_internal.MODULE_NAME, "insertMin");

		/**
		 * Helper binding method for function: intersectWithKey. 
		 * @param f
		 * @param t1
		 * @param t2
		 * @return the SourceModule.expr representing an application of intersectWithKey
		 */
		public static final SourceModel.Expr intersectWithKey(SourceModel.Expr f, SourceModel.Expr t1, SourceModel.Expr t2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intersectWithKey), f, t1, t2});
		}

		/**
		 * Name binding for function: intersectWithKey.
		 * @see #intersectWithKey(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intersectWithKey = 
			QualifiedName.make(CAL_Map_internal.MODULE_NAME, "intersectWithKey");

		/**
		 * Helper binding method for function: isEmptySpecialized. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of isEmptySpecialized
		 */
		public static final SourceModel.Expr isEmptySpecialized(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isEmptySpecialized), arg_1});
		}

		/**
		 * Name binding for function: isEmptySpecialized.
		 * @see #isEmptySpecialized(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isEmptySpecialized = 
			QualifiedName.make(
				CAL_Map_internal.MODULE_NAME, 
				"isEmptySpecialized");

		/**
		 * Helper binding method for function: isSubmapHelper. 
		 * @param f
		 * @param t1
		 * @param t2
		 * @return the SourceModule.expr representing an application of isSubmapHelper
		 */
		public static final SourceModel.Expr isSubmapHelper(SourceModel.Expr f, SourceModel.Expr t1, SourceModel.Expr t2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isSubmapHelper), f, t1, t2});
		}

		/**
		 * Name binding for function: isSubmapHelper.
		 * @see #isSubmapHelper(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isSubmapHelper = 
			QualifiedName.make(CAL_Map_internal.MODULE_NAME, "isSubmapHelper");

		/**
		 * Helper binding method for function: join. 
		 * @param kx
		 * @param x
		 * @param l
		 * @param r
		 * @return the SourceModule.expr representing an application of join
		 */
		public static final SourceModel.Expr join(SourceModel.Expr kx, SourceModel.Expr x, SourceModel.Expr l, SourceModel.Expr r) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.join), kx, x, l, r});
		}

		/**
		 * Name binding for function: join.
		 * @see #join(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName join = 
			QualifiedName.make(CAL_Map_internal.MODULE_NAME, "join");

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
			QualifiedName.make(CAL_Map_internal.MODULE_NAME, "mapAccumL");

		/**
		 * Helper binding method for function: merge. 
		 * @param l
		 * @param r
		 * @return the SourceModule.expr representing an application of merge
		 */
		public static final SourceModel.Expr merge(SourceModel.Expr l, SourceModel.Expr r) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.merge), l, r});
		}

		/**
		 * Name binding for function: merge.
		 * @see #merge(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName merge = 
			QualifiedName.make(CAL_Map_internal.MODULE_NAME, "merge");

		/**
		 * Helper binding method for function: notEqualsMap. 
		 * @param m1
		 * @param m2
		 * @return the SourceModule.expr representing an application of notEqualsMap
		 */
		public static final SourceModel.Expr notEqualsMap(SourceModel.Expr m1, SourceModel.Expr m2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.notEqualsMap), m1, m2});
		}

		/**
		 * Name binding for function: notEqualsMap.
		 * @see #notEqualsMap(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName notEqualsMap = 
			QualifiedName.make(CAL_Map_internal.MODULE_NAME, "notEqualsMap");

		/**
		 * Helper binding method for function: ordered. 
		 * @param t
		 * @return the SourceModule.expr representing an application of ordered
		 */
		public static final SourceModel.Expr ordered(SourceModel.Expr t) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.ordered), t});
		}

		/**
		 * Name binding for function: ordered.
		 * @see #ordered(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName ordered = 
			QualifiedName.make(CAL_Map_internal.MODULE_NAME, "ordered");

		/**
		 * Helper binding method for function: outputMap. 
		 * @param m
		 * @return the SourceModule.expr representing an application of outputMap
		 */
		public static final SourceModel.Expr outputMap(SourceModel.Expr m) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputMap), m});
		}

		/**
		 * Name binding for function: outputMap.
		 * @see #outputMap(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName outputMap = 
			QualifiedName.make(CAL_Map_internal.MODULE_NAME, "outputMap");

		/**
		 * Helper binding method for function: ratio. 
		 * @return the SourceModule.expr representing an application of ratio
		 */
		public static final SourceModel.Expr ratio() {
			return SourceModel.Expr.Var.make(Functions.ratio);
		}

		/**
		 * Name binding for function: ratio.
		 * @see #ratio()
		 */
		public static final QualifiedName ratio = 
			QualifiedName.make(CAL_Map_internal.MODULE_NAME, "ratio");

		/**
		 * Helper binding method for function: rotateL. 
		 * @param k
		 * @param x
		 * @param l
		 * @param r
		 * @return the SourceModule.expr representing an application of rotateL
		 */
		public static final SourceModel.Expr rotateL(SourceModel.Expr k, SourceModel.Expr x, SourceModel.Expr l, SourceModel.Expr r) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.rotateL), k, x, l, r});
		}

		/**
		 * Name binding for function: rotateL.
		 * @see #rotateL(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName rotateL = 
			QualifiedName.make(CAL_Map_internal.MODULE_NAME, "rotateL");

		/**
		 * Helper binding method for function: rotateR. 
		 * @param k
		 * @param x
		 * @param l
		 * @param r
		 * @return the SourceModule.expr representing an application of rotateR
		 */
		public static final SourceModel.Expr rotateR(SourceModel.Expr k, SourceModel.Expr x, SourceModel.Expr l, SourceModel.Expr r) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.rotateR), k, x, l, r});
		}

		/**
		 * Name binding for function: rotateR.
		 * @see #rotateR(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName rotateR = 
			QualifiedName.make(CAL_Map_internal.MODULE_NAME, "rotateR");

		/**
		 * Helper binding method for function: showMap. 
		 * @param m
		 * @return the SourceModule.expr representing an application of showMap
		 */
		public static final SourceModel.Expr showMap(SourceModel.Expr m) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showMap), m});
		}

		/**
		 * Name binding for function: showMap.
		 * @see #showMap(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showMap = 
			QualifiedName.make(CAL_Map_internal.MODULE_NAME, "showMap");

		/**
		 * Helper binding method for function: singleL. 
		 * @param k1
		 * @param x1
		 * @param t1
		 * @param t4
		 * @return the SourceModule.expr representing an application of singleL
		 */
		public static final SourceModel.Expr singleL(SourceModel.Expr k1, SourceModel.Expr x1, SourceModel.Expr t1, SourceModel.Expr t4) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.singleL), k1, x1, t1, t4});
		}

		/**
		 * Name binding for function: singleL.
		 * @see #singleL(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName singleL = 
			QualifiedName.make(CAL_Map_internal.MODULE_NAME, "singleL");

		/**
		 * Helper binding method for function: singleR. 
		 * @param k1
		 * @param x1
		 * @param t4
		 * @param t3
		 * @return the SourceModule.expr representing an application of singleR
		 */
		public static final SourceModel.Expr singleR(SourceModel.Expr k1, SourceModel.Expr x1, SourceModel.Expr t4, SourceModel.Expr t3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.singleR), k1, x1, t4, t3});
		}

		/**
		 * Name binding for function: singleR.
		 * @see #singleR(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName singleR = 
			QualifiedName.make(CAL_Map_internal.MODULE_NAME, "singleR");

		/**
		 * Helper binding method for function: trim. 
		 * @param cmplo
		 * @param cmphi
		 * @param t
		 * @return the SourceModule.expr representing an application of trim
		 */
		public static final SourceModel.Expr trim(SourceModel.Expr cmplo, SourceModel.Expr cmphi, SourceModel.Expr t) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.trim), cmplo, cmphi, t});
		}

		/**
		 * Name binding for function: trim.
		 * @see #trim(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName trim = 
			QualifiedName.make(CAL_Map_internal.MODULE_NAME, "trim");

		/**
		 * Helper binding method for function: trimLookupLo. 
		 * @param lo
		 * @param cmphi
		 * @param t
		 * @return the SourceModule.expr representing an application of trimLookupLo
		 */
		public static final SourceModel.Expr trimLookupLo(SourceModel.Expr lo, SourceModel.Expr cmphi, SourceModel.Expr t) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.trimLookupLo), lo, cmphi, t});
		}

		/**
		 * Name binding for function: trimLookupLo.
		 * @see #trimLookupLo(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName trimLookupLo = 
			QualifiedName.make(CAL_Map_internal.MODULE_NAME, "trimLookupLo");

		/**
		 * Helper binding method for function: valid. 
		 * @param t
		 * @return the SourceModule.expr representing an application of valid
		 */
		public static final SourceModel.Expr valid(SourceModel.Expr t) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.valid), t});
		}

		/**
		 * Name binding for function: valid.
		 * @see #valid(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName valid = 
			QualifiedName.make(CAL_Map_internal.MODULE_NAME, "valid");

		/**
		 * Helper binding method for function: validsize. 
		 * @param t
		 * @return the SourceModule.expr representing an application of validsize
		 */
		public static final SourceModel.Expr validsize(SourceModel.Expr t) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.validsize), t});
		}

		/**
		 * Name binding for function: validsize.
		 * @see #validsize(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName validsize = 
			QualifiedName.make(CAL_Map_internal.MODULE_NAME, "validsize");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = -325626556;

}
