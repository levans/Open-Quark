/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_Set_internal.java)
 * was generated from CAL module: Cal.Collections.Set.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Collections.Set module from Java code.
 *  
 * Creation date: Fri Mar 16 13:11:58 PST 2007
 * --!>
 *  
 */

package org.openquark.cal.internal.module.Cal.Collections;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * An efficient implementation of sets of values.
 * <p>
 * The implementation of Set is based on <em>size balanced</em> binary trees (or trees of <em>bounded balance</em>) as described by:
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
 * Note that the implementation is <em>left-biased</em> -- the elements of a first argument
 * are always preferred to the second, for example in <code>Cal.Collections.Set.union</code> or <code>Cal.Collections.Set.insert</code>.
 * Of course, left-biasing can only be observed when equality an equivalence relation
 * instead of structural equality.
 * <p>
 * This module is an adaptation of functionality from Daan Leijen's DData collections library for Haskell.
 * The library was obtained from <a href='http://www.cs.uu.nl/~daan/ddata.html'>http://www.cs.uu.nl/~daan/ddata.html</a>.
 * See the file <code>ThirdPartyComponents/ThirdPartyComponents.txt</code> for the DData license.
 * 
 * 
 * <dl><dt><b>See Also:</b>
 * <dd><b>Modules:</b> Cal.Collections.Map, Cal.Collections.IntMap, Cal.Collections.LongMap
 * </dl>
 * 
 * @author Bo Ilic
 */
public final class CAL_Set_internal {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Collections.Set");

	/**
	 * This inner class (DataConstructors) contains constants
	 * and methods related to binding to CAL DataConstructors in the Cal.Collections.Set module.
	 */
	public static final class DataConstructors {
		/*
		 * DataConstructors for the Cal.Collections.Set.Set data type.
		 */

		/**
		 * Binding for DataConstructor: Cal.Collections.Set.Tip.
		 * @return the SourceModule.Expr representing an application of Cal.Collections.Set.Tip
		 */
		public static final SourceModel.Expr Tip() {
			return SourceModel.Expr.DataCons.make(DataConstructors.Tip);
		}

		/**
		 * Name binding for DataConstructor: Cal.Collections.Set.Tip.
		 * @see #Tip()
		 */
		public static final QualifiedName Tip = 
			QualifiedName.make(CAL_Set_internal.MODULE_NAME, "Tip");

		/**
		 * Ordinal of DataConstructor Cal.Collections.Set.Tip.
		 * @see #Tip()
		 */
		public static final int Tip_ordinal = 0;

		/**
		 * Binding for DataConstructor: Cal.Collections.Set.Bin.
		 * @param size
		 * @param value
		 * @param leftSet
		 * @param rightSet
		 * @return the SourceModule.Expr representing an application of Cal.Collections.Set.Bin
		 */
		public static final SourceModel.Expr Bin(SourceModel.Expr size, SourceModel.Expr value, SourceModel.Expr leftSet, SourceModel.Expr rightSet) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.Bin), size, value, leftSet, rightSet});
		}

		/**
		 * @see #Bin(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param size
		 * @param value
		 * @param leftSet
		 * @param rightSet
		 * @return org.openquark.cal.compiler.SourceModel.Expr
		 */
		public static final SourceModel.Expr Bin(int size, SourceModel.Expr value, SourceModel.Expr leftSet, SourceModel.Expr rightSet) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.Bin), SourceModel.Expr.makeIntValue(size), value, leftSet, rightSet});
		}

		/**
		 * Name binding for DataConstructor: Cal.Collections.Set.Bin.
		 * @see #Bin(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName Bin = 
			QualifiedName.make(CAL_Set_internal.MODULE_NAME, "Bin");

		/**
		 * Ordinal of DataConstructor Cal.Collections.Set.Bin.
		 * @see #Bin(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int Bin_ordinal = 1;

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Collections.Set module.
	 */
	public static final class Functions {
		/**
		 * Helper binding method for function: arbitrarySet. 
		 * @return the SourceModule.expr representing an application of arbitrarySet
		 */
		public static final SourceModel.Expr arbitrarySet() {
			return SourceModel.Expr.Var.make(Functions.arbitrarySet);
		}

		/**
		 * Name binding for function: arbitrarySet.
		 * @see #arbitrarySet()
		 */
		public static final QualifiedName arbitrarySet = 
			QualifiedName.make(CAL_Set_internal.MODULE_NAME, "arbitrarySet");

		/**
		 * Helper binding method for function: balance. 
		 * @param x
		 * @param l
		 * @param r
		 * @return the SourceModule.expr representing an application of balance
		 */
		public static final SourceModel.Expr balance(SourceModel.Expr x, SourceModel.Expr l, SourceModel.Expr r) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.balance), x, l, r});
		}

		/**
		 * Name binding for function: balance.
		 * @see #balance(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName balance = 
			QualifiedName.make(CAL_Set_internal.MODULE_NAME, "balance");

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
			QualifiedName.make(CAL_Set_internal.MODULE_NAME, "balanced");

		/**
		 * Helper binding method for function: bin. 
		 * @param x
		 * @param l
		 * @param r
		 * @return the SourceModule.expr representing an application of bin
		 */
		public static final SourceModel.Expr bin(SourceModel.Expr x, SourceModel.Expr l, SourceModel.Expr r) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.bin), x, l, r});
		}

		/**
		 * Name binding for function: bin.
		 * @see #bin(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName bin = 
			QualifiedName.make(CAL_Set_internal.MODULE_NAME, "bin");

		/**
		 * Helper binding method for function: coarbitrarySet. 
		 * @param set
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of coarbitrarySet
		 */
		public static final SourceModel.Expr coarbitrarySet(SourceModel.Expr set, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.coarbitrarySet), set, arg_2});
		}

		/**
		 * Name binding for function: coarbitrarySet.
		 * @see #coarbitrarySet(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName coarbitrarySet = 
			QualifiedName.make(CAL_Set_internal.MODULE_NAME, "coarbitrarySet");

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
			QualifiedName.make(CAL_Set_internal.MODULE_NAME, "delta");

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
			QualifiedName.make(CAL_Set_internal.MODULE_NAME, "directBuild");

		/**
		 * Helper binding method for function: doubleL. 
		 * @param x1
		 * @param t1
		 * @param t5
		 * @return the SourceModule.expr representing an application of doubleL
		 */
		public static final SourceModel.Expr doubleL(SourceModel.Expr x1, SourceModel.Expr t1, SourceModel.Expr t5) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleL), x1, t1, t5});
		}

		/**
		 * Name binding for function: doubleL.
		 * @see #doubleL(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleL = 
			QualifiedName.make(CAL_Set_internal.MODULE_NAME, "doubleL");

		/**
		 * Helper binding method for function: doubleR. 
		 * @param x1
		 * @param t5
		 * @param t4
		 * @return the SourceModule.expr representing an application of doubleR
		 */
		public static final SourceModel.Expr doubleR(SourceModel.Expr x1, SourceModel.Expr t5, SourceModel.Expr t4) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleR), x1, t5, t4});
		}

		/**
		 * Name binding for function: doubleR.
		 * @see #doubleR(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleR = 
			QualifiedName.make(CAL_Set_internal.MODULE_NAME, "doubleR");

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
			QualifiedName.make(CAL_Set_internal.MODULE_NAME, "emptySpecialized");

		/**
		 * Helper binding method for function: equalsSet. 
		 * @param s1
		 * @param s2
		 * @return the SourceModule.expr representing an application of equalsSet
		 */
		public static final SourceModel.Expr equalsSet(SourceModel.Expr s1, SourceModel.Expr s2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.equalsSet), s1, s2});
		}

		/**
		 * Name binding for function: equalsSet.
		 * @see #equalsSet(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName equalsSet = 
			QualifiedName.make(CAL_Set_internal.MODULE_NAME, "equalsSet");

		/**
		 * Helper binding method for function: filterGt. 
		 * @param cmp
		 * @param t
		 * @return the SourceModule.expr representing an application of filterGt
		 */
		public static final SourceModel.Expr filterGt(SourceModel.Expr cmp, SourceModel.Expr t) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.filterGt), cmp, t});
		}

		/**
		 * Name binding for function: filterGt.
		 * @see #filterGt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName filterGt = 
			QualifiedName.make(CAL_Set_internal.MODULE_NAME, "filterGt");

		/**
		 * Helper binding method for function: filterLt. 
		 * @param cmp
		 * @param t
		 * @return the SourceModule.expr representing an application of filterLt
		 */
		public static final SourceModel.Expr filterLt(SourceModel.Expr cmp, SourceModel.Expr t) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.filterLt), cmp, t});
		}

		/**
		 * Name binding for function: filterLt.
		 * @see #filterLt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName filterLt = 
			QualifiedName.make(CAL_Set_internal.MODULE_NAME, "filterLt");

		/**
		 * Helper binding method for function: foldR. 
		 * @param f
		 * @param z
		 * @param s
		 * @return the SourceModule.expr representing an application of foldR
		 */
		public static final SourceModel.Expr foldR(SourceModel.Expr f, SourceModel.Expr z, SourceModel.Expr s) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.foldR), f, z, s});
		}

		/**
		 * Name binding for function: foldR.
		 * @see #foldR(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName foldR = 
			QualifiedName.make(CAL_Set_internal.MODULE_NAME, "foldR");

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
			QualifiedName.make(CAL_Set_internal.MODULE_NAME, "glue");

		/**
		 * Helper binding method for function: hedgeDiff. 
		 * @param cmplo
		 * @param cmphi
		 * @param t1
		 * @param t2
		 * @return the SourceModule.expr representing an application of hedgeDiff
		 */
		public static final SourceModel.Expr hedgeDiff(SourceModel.Expr cmplo, SourceModel.Expr cmphi, SourceModel.Expr t1, SourceModel.Expr t2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.hedgeDiff), cmplo, cmphi, t1, t2});
		}

		/**
		 * Name binding for function: hedgeDiff.
		 * @see #hedgeDiff(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName hedgeDiff = 
			QualifiedName.make(CAL_Set_internal.MODULE_NAME, "hedgeDiff");

		/**
		 * Helper binding method for function: hedgeUnion. 
		 * @param cmplo
		 * @param cmphi
		 * @param t1
		 * @param t2
		 * @return the SourceModule.expr representing an application of hedgeUnion
		 */
		public static final SourceModel.Expr hedgeUnion(SourceModel.Expr cmplo, SourceModel.Expr cmphi, SourceModel.Expr t1, SourceModel.Expr t2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.hedgeUnion), cmplo, cmphi, t1, t2});
		}

		/**
		 * Name binding for function: hedgeUnion.
		 * @see #hedgeUnion(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName hedgeUnion = 
			QualifiedName.make(CAL_Set_internal.MODULE_NAME, "hedgeUnion");

		/**
		 * Helper binding method for function: inputSet. 
		 * @param list
		 * @return the SourceModule.expr representing an application of inputSet
		 */
		public static final SourceModel.Expr inputSet(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inputSet), list});
		}

		/**
		 * Name binding for function: inputSet.
		 * @see #inputSet(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName inputSet = 
			QualifiedName.make(CAL_Set_internal.MODULE_NAME, "inputSet");

		/**
		 * Helper binding method for function: insertMax. 
		 * @param x
		 * @param t
		 * @return the SourceModule.expr representing an application of insertMax
		 */
		public static final SourceModel.Expr insertMax(SourceModel.Expr x, SourceModel.Expr t) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.insertMax), x, t});
		}

		/**
		 * Name binding for function: insertMax.
		 * @see #insertMax(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName insertMax = 
			QualifiedName.make(CAL_Set_internal.MODULE_NAME, "insertMax");

		/**
		 * Helper binding method for function: insertMin. 
		 * @param x
		 * @param t
		 * @return the SourceModule.expr representing an application of insertMin
		 */
		public static final SourceModel.Expr insertMin(SourceModel.Expr x, SourceModel.Expr t) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.insertMin), x, t});
		}

		/**
		 * Name binding for function: insertMin.
		 * @see #insertMin(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName insertMin = 
			QualifiedName.make(CAL_Set_internal.MODULE_NAME, "insertMin");

		/**
		 * Helper binding method for function: intersect. 
		 * @param t1
		 * @param t2
		 * @return the SourceModule.expr representing an application of intersect
		 */
		public static final SourceModel.Expr intersect(SourceModel.Expr t1, SourceModel.Expr t2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intersect), t1, t2});
		}

		/**
		 * Name binding for function: intersect.
		 * @see #intersect(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intersect = 
			QualifiedName.make(CAL_Set_internal.MODULE_NAME, "intersect");

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
				CAL_Set_internal.MODULE_NAME, 
				"isEmptySpecialized");

		/**
		 * Helper binding method for function: isSubsetOfHelper. 
		 * @param t1
		 * @param t2
		 * @return the SourceModule.expr representing an application of isSubsetOfHelper
		 */
		public static final SourceModel.Expr isSubsetOfHelper(SourceModel.Expr t1, SourceModel.Expr t2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isSubsetOfHelper), t1, t2});
		}

		/**
		 * Name binding for function: isSubsetOfHelper.
		 * @see #isSubsetOfHelper(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isSubsetOfHelper = 
			QualifiedName.make(CAL_Set_internal.MODULE_NAME, "isSubsetOfHelper");

		/**
		 * Helper binding method for function: join. 
		 * @param x
		 * @param l
		 * @param r
		 * @return the SourceModule.expr representing an application of join
		 */
		public static final SourceModel.Expr join(SourceModel.Expr x, SourceModel.Expr l, SourceModel.Expr r) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.join), x, l, r});
		}

		/**
		 * Name binding for function: join.
		 * @see #join(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName join = 
			QualifiedName.make(CAL_Set_internal.MODULE_NAME, "join");

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
			QualifiedName.make(CAL_Set_internal.MODULE_NAME, "merge");

		/**
		 * Helper binding method for function: notEqualsSet. 
		 * @param s1
		 * @param s2
		 * @return the SourceModule.expr representing an application of notEqualsSet
		 */
		public static final SourceModel.Expr notEqualsSet(SourceModel.Expr s1, SourceModel.Expr s2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.notEqualsSet), s1, s2});
		}

		/**
		 * Name binding for function: notEqualsSet.
		 * @see #notEqualsSet(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName notEqualsSet = 
			QualifiedName.make(CAL_Set_internal.MODULE_NAME, "notEqualsSet");

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
			QualifiedName.make(CAL_Set_internal.MODULE_NAME, "ordered");

		/**
		 * Helper binding method for function: outputSet. 
		 * @param set
		 * @return the SourceModule.expr representing an application of outputSet
		 */
		public static final SourceModel.Expr outputSet(SourceModel.Expr set) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputSet), set});
		}

		/**
		 * Name binding for function: outputSet.
		 * @see #outputSet(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName outputSet = 
			QualifiedName.make(CAL_Set_internal.MODULE_NAME, "outputSet");

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
			QualifiedName.make(CAL_Set_internal.MODULE_NAME, "ratio");

		/**
		 * Helper binding method for function: rotateL. 
		 * @param x
		 * @param l
		 * @param r
		 * @return the SourceModule.expr representing an application of rotateL
		 */
		public static final SourceModel.Expr rotateL(SourceModel.Expr x, SourceModel.Expr l, SourceModel.Expr r) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.rotateL), x, l, r});
		}

		/**
		 * Name binding for function: rotateL.
		 * @see #rotateL(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName rotateL = 
			QualifiedName.make(CAL_Set_internal.MODULE_NAME, "rotateL");

		/**
		 * Helper binding method for function: rotateR. 
		 * @param x
		 * @param l
		 * @param r
		 * @return the SourceModule.expr representing an application of rotateR
		 */
		public static final SourceModel.Expr rotateR(SourceModel.Expr x, SourceModel.Expr l, SourceModel.Expr r) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.rotateR), x, l, r});
		}

		/**
		 * Name binding for function: rotateR.
		 * @see #rotateR(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName rotateR = 
			QualifiedName.make(CAL_Set_internal.MODULE_NAME, "rotateR");

		/**
		 * Helper binding method for function: showSet. 
		 * @param set
		 * @return the SourceModule.expr representing an application of showSet
		 */
		public static final SourceModel.Expr showSet(SourceModel.Expr set) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showSet), set});
		}

		/**
		 * Name binding for function: showSet.
		 * @see #showSet(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showSet = 
			QualifiedName.make(CAL_Set_internal.MODULE_NAME, "showSet");

		/**
		 * Helper binding method for function: singleL. 
		 * @param x1
		 * @param t1
		 * @param t4
		 * @return the SourceModule.expr representing an application of singleL
		 */
		public static final SourceModel.Expr singleL(SourceModel.Expr x1, SourceModel.Expr t1, SourceModel.Expr t4) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.singleL), x1, t1, t4});
		}

		/**
		 * Name binding for function: singleL.
		 * @see #singleL(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName singleL = 
			QualifiedName.make(CAL_Set_internal.MODULE_NAME, "singleL");

		/**
		 * Helper binding method for function: singleR. 
		 * @param x1
		 * @param t4
		 * @param t3
		 * @return the SourceModule.expr representing an application of singleR
		 */
		public static final SourceModel.Expr singleR(SourceModel.Expr x1, SourceModel.Expr t4, SourceModel.Expr t3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.singleR), x1, t4, t3});
		}

		/**
		 * Name binding for function: singleR.
		 * @see #singleR(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName singleR = 
			QualifiedName.make(CAL_Set_internal.MODULE_NAME, "singleR");

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
			QualifiedName.make(CAL_Set_internal.MODULE_NAME, "trim");

		/**
		 * Helper binding method for function: trimMemberLo. 
		 * @param lo
		 * @param cmphi
		 * @param t
		 * @return the SourceModule.expr representing an application of trimMemberLo
		 */
		public static final SourceModel.Expr trimMemberLo(SourceModel.Expr lo, SourceModel.Expr cmphi, SourceModel.Expr t) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.trimMemberLo), lo, cmphi, t});
		}

		/**
		 * Name binding for function: trimMemberLo.
		 * @see #trimMemberLo(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName trimMemberLo = 
			QualifiedName.make(CAL_Set_internal.MODULE_NAME, "trimMemberLo");

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
			QualifiedName.make(CAL_Set_internal.MODULE_NAME, "valid");

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
			QualifiedName.make(CAL_Set_internal.MODULE_NAME, "validsize");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = -1462997342;

}
