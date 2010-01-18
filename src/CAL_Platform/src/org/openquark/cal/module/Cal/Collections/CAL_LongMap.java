/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_LongMap.java)
 * was generated from CAL module: Cal.Collections.LongMap.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Collections.LongMap module from Java code.
 *  
 * Creation date: Fri Mar 16 13:11:56 PST 2007
 * --!>
 *  
 */

package org.openquark.cal.module.Cal.Collections;

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
public final class CAL_LongMap {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Collections.LongMap");

	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Collections.LongMap module.
	 */
	public static final class TypeConstructors {
		/**
		 * A map from keys (of type <code>Cal.Core.Prelude.Long</code>) to values (of type <code>a</code>).
		 */
		public static final QualifiedName LongMap = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "LongMap");

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Collections.LongMap module.
	 */
	public static final class Functions {
		/**
		 * Adjusts a value at a specific key. When the key is not a member of the map,
		 * the original map is returned.
		 * <p>
		 * Complexity: O(min(n,W))
		 * 
		 * @param f (CAL type: <code>a -> a</code>)
		 *          the function used to map the old value associated with the key to the new value.
		 * @param k (CAL type: <code>Cal.Core.Prelude.Long</code>)
		 *          the key.
		 * @param m (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>)
		 *          the map.
		 * @return (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>) 
		 *          the map, with the value at the specified key adjusted if present.
		 */
		public static final SourceModel.Expr adjust(SourceModel.Expr f, SourceModel.Expr k, SourceModel.Expr m) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.adjust), f, k, m});
		}

		/**
		 * @see #adjust(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param f
		 * @param k
		 * @param m
		 * @return the SourceModel.Expr representing an application of adjust
		 */
		public static final SourceModel.Expr adjust(SourceModel.Expr f, long k, SourceModel.Expr m) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.adjust), f, SourceModel.Expr.makeLongValue(k), m});
		}

		/**
		 * Name binding for function: adjust.
		 * @see #adjust(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName adjust = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "adjust");

		/**
		 * Adjusts a value at a specific key. When the key is not a member of the map,
		 * the original map is returned.
		 * <p>
		 * Complexity: O(min(n,W))
		 * 
		 * @param f (CAL type: <code>Cal.Core.Prelude.Long -> a -> a</code>)
		 *          the function which, when given the old key-value pair, returns the new value to be associated with the key.
		 * @param k (CAL type: <code>Cal.Core.Prelude.Long</code>)
		 *          the key.
		 * @param m (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>)
		 *          the map.
		 * @return (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>) 
		 *          the map, with the value at the specified key adjusted if present.
		 */
		public static final SourceModel.Expr adjustWithKey(SourceModel.Expr f, SourceModel.Expr k, SourceModel.Expr m) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.adjustWithKey), f, k, m});
		}

		/**
		 * @see #adjustWithKey(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param f
		 * @param k
		 * @param m
		 * @return the SourceModel.Expr representing an application of adjustWithKey
		 */
		public static final SourceModel.Expr adjustWithKey(SourceModel.Expr f, long k, SourceModel.Expr m) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.adjustWithKey), f, SourceModel.Expr.makeLongValue(k), m});
		}

		/**
		 * Name binding for function: adjustWithKey.
		 * @see #adjustWithKey(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName adjustWithKey = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "adjustWithKey");

		/**
		 * Returns an association list of all key-value pairs in the map.
		 * <p>
		 * Complexity: O(n)
		 * 
		 * @param m (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>)
		 *          the map.
		 * @return (CAL type: <code>[(Cal.Core.Prelude.Long, a)]</code>) 
		 *          an association list of all key-value pairs in the map.
		 */
		public static final SourceModel.Expr assocs(SourceModel.Expr m) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.assocs), m});
		}

		/**
		 * Name binding for function: assocs.
		 * @see #assocs(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName assocs = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "assocs");

		/**
		 * Deletes a key and its value from the map. When the key is not a member of the
		 * map, the original map is returned.
		 * <p>
		 * Complexity: O(log n)
		 * 
		 * @param k (CAL type: <code>Cal.Core.Prelude.Long</code>)
		 *          the key.
		 * @param t (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>)
		 *          the map.
		 * @return (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>) 
		 *          the map, with the specified key and its corresponding value deleted if present.
		 */
		public static final SourceModel.Expr delete(SourceModel.Expr k, SourceModel.Expr t) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.delete), k, t});
		}

		/**
		 * @see #delete(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param k
		 * @param t
		 * @return the SourceModel.Expr representing an application of delete
		 */
		public static final SourceModel.Expr delete(long k, SourceModel.Expr t) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.delete), SourceModel.Expr.makeLongValue(k), t});
		}

		/**
		 * Name binding for function: delete.
		 * @see #delete(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName delete = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "delete");

		/**
		 * Returns the difference of two maps (based on keys).
		 * <p>
		 * Complexity: O(n+m)
		 * 
		 * @param t1 (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>)
		 *          the first map.
		 * @param t2 (CAL type: <code>Cal.Collections.LongMap.LongMap b</code>)
		 *          the second map.
		 * @return (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>) 
		 *          the difference of the two maps.
		 */
		public static final SourceModel.Expr difference(SourceModel.Expr t1, SourceModel.Expr t2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.difference), t1, t2});
		}

		/**
		 * Name binding for function: difference.
		 * @see #difference(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName difference = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "difference");

		/**
		 * Returns the difference of two maps, with a combining function.
		 * <p>
		 * Complexity: O(n+m)
		 * 
		 * @param f (CAL type: <code>a -> b -> Cal.Core.Prelude.Maybe a</code>)
		 *          the combining function.
		 * @param m1 (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>)
		 *          the first map.
		 * @param m2 (CAL type: <code>Cal.Collections.LongMap.LongMap b</code>)
		 *          the second map.
		 * @return (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>) 
		 *          the difference of the two maps.
		 */
		public static final SourceModel.Expr differenceWith(SourceModel.Expr f, SourceModel.Expr m1, SourceModel.Expr m2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.differenceWith), f, m1, m2});
		}

		/**
		 * Name binding for function: differenceWith.
		 * @see #differenceWith(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName differenceWith = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "differenceWith");

		/**
		 * Returns the difference of two maps, with a combining function. When two equal keys are
		 * encountered, the combining function is applied to the key and both values.
		 * If it returns <code>Cal.Core.Prelude.Nothing</code>, the element is discarded (proper set difference). If
		 * it returns <code>Cal.Core.Prelude.Just y</code>, the element is updated with a new value <code>y</code>.
		 * <p>
		 * Complexity: O(n+m)
		 * 
		 * @param f (CAL type: <code>Cal.Core.Prelude.Long -> a -> b -> Cal.Core.Prelude.Maybe a</code>)
		 *          the combining function.
		 * @param t1 (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>)
		 *          the first map.
		 * @param t2 (CAL type: <code>Cal.Collections.LongMap.LongMap b</code>)
		 *          the second map.
		 * @return (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>) 
		 *          the difference of the two maps.
		 */
		public static final SourceModel.Expr differenceWithKey(SourceModel.Expr f, SourceModel.Expr t1, SourceModel.Expr t2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.differenceWithKey), f, t1, t2});
		}

		/**
		 * Name binding for function: differenceWithKey.
		 * @see #differenceWithKey(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName differenceWithKey = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "differenceWithKey");

		/**
		 * Returns all elements of the map.
		 * <p>
		 * Complexity: O(n)
		 * 
		 * @param m (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>)
		 *          the map.
		 * @return (CAL type: <code>[a]</code>) 
		 *          a list of all elements of the map.
		 */
		public static final SourceModel.Expr elems(SourceModel.Expr m) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.elems), m});
		}

		/**
		 * Name binding for function: elems.
		 * @see #elems(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName elems = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "elems");

		/**
		 * The empty map.
		 * <p>
		 * Complexity: O(1)
		 * 
		 * @return (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>) 
		 *          an empty map.
		 */
		public static final SourceModel.Expr empty() {
			return SourceModel.Expr.Var.make(Functions.empty);
		}

		/**
		 * Name binding for function: empty.
		 * @see #empty()
		 */
		public static final QualifiedName empty = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "empty");

		/**
		 * Filters all values that satisfy the predicate.
		 * <p>
		 * Complexity: O(n)
		 * 
		 * @param p (CAL type: <code>a -> Cal.Core.Prelude.Boolean</code>)
		 *          the predicate for testing the values.
		 * @param m (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>)
		 *          the map.
		 * @return (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>) 
		 *          the map containing only those key-value pairs whose values satisfy the predicate.
		 */
		public static final SourceModel.Expr filter(SourceModel.Expr p, SourceModel.Expr m) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.filter), p, m});
		}

		/**
		 * Name binding for function: filter.
		 * @see #filter(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName filter = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "filter");

		/**
		 * Filters all keys/values that satisfy the predicate.
		 * <p>
		 * Complexity: O(n)
		 * 
		 * @param pred (CAL type: <code>Cal.Core.Prelude.Long -> a -> Cal.Core.Prelude.Boolean</code>)
		 *          the predicate for testing the keys and values.
		 * @param t (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>)
		 *          the map.
		 * @return (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>) 
		 *          the map containing only those key-value pairs that satisfy the predicate.
		 */
		public static final SourceModel.Expr filterWithKey(SourceModel.Expr pred, SourceModel.Expr t) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.filterWithKey), pred, t});
		}

		/**
		 * Name binding for function: filterWithKey.
		 * @see #filterWithKey(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName filterWithKey = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "filterWithKey");

		/**
		 * Finds the value of a key. Calls <code>Cal.Core.Prelude.error</code> when the element cannot be found.
		 * <p>
		 * Complexity: O(min(n,W))
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.LongMap.lookup, Cal.Collections.LongMap.lookupWithDefault
		 * </dl>
		 * 
		 * @param k (CAL type: <code>Cal.Core.Prelude.Long</code>)
		 *          the key.
		 * @param m (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>)
		 *          the map.
		 * @return (CAL type: <code>a</code>) 
		 *          the value of the key in the map, or a call to <code>Cal.Core.Prelude.error</code> if it cannot be found.
		 */
		public static final SourceModel.Expr find(SourceModel.Expr k, SourceModel.Expr m) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.find), k, m});
		}

		/**
		 * @see #find(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param k
		 * @param m
		 * @return the SourceModel.Expr representing an application of find
		 */
		public static final SourceModel.Expr find(long k, SourceModel.Expr m) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.find), SourceModel.Expr.makeLongValue(k), m});
		}

		/**
		 * Name binding for function: find.
		 * @see #find(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName find = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "find");

		/**
		 * Folds over the values in the map in an unspecified order.
		 * <p>
		 * Complexity: O(n)
		 * 
		 * @param f (CAL type: <code>a -> b -> b</code>)
		 *          the function to be folded over the values in the map.
		 * @param z (CAL type: <code>b</code>)
		 * @param t (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>)
		 *          the map.
		 * @return (CAL type: <code>b</code>) 
		 *          the summary value obtained from the fold.
		 */
		public static final SourceModel.Expr fold(SourceModel.Expr f, SourceModel.Expr z, SourceModel.Expr t) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fold), f, z, t});
		}

		/**
		 * Name binding for function: fold.
		 * @see #fold(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fold = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "fold");

		/**
		 * Folds over the keys and values in the map in an unspecified order.
		 * <p>
		 * Complexity: O(n)
		 * 
		 * @param f (CAL type: <code>Cal.Core.Prelude.Long -> a -> b -> b</code>)
		 *          the function to be folded over the keys and values in the map.
		 * @param z (CAL type: <code>b</code>)
		 * @param t (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>)
		 *          the map.
		 * @return (CAL type: <code>b</code>) 
		 *          the summary value obtained from the fold.
		 */
		public static final SourceModel.Expr foldWithKey(SourceModel.Expr f, SourceModel.Expr z, SourceModel.Expr t) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.foldWithKey), f, z, t});
		}

		/**
		 * Name binding for function: foldWithKey.
		 * @see #foldWithKey(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName foldWithKey = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "foldWithKey");

		/**
		 * Builds a map from an ascending list in linear time. The precondition (input
		 * list is ascending) is not checked.
		 * <p>
		 * Complexity: O(n*min(n,W))
		 * 
		 * @param xs (CAL type: <code>[(Cal.Core.Prelude.Long, a)]</code>)
		 *          an ascending list of key-value pairs.
		 * @return (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>) 
		 *          a map containing the key-value entries.
		 */
		public static final SourceModel.Expr fromAscList(SourceModel.Expr xs) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromAscList), xs});
		}

		/**
		 * Name binding for function: fromAscList.
		 * @see #fromAscList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fromAscList = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "fromAscList");

		/**
		 * Builds a map from an ascending list in linear time with a combining function
		 * for equal keys. The precondition (input list is ascending) is not checked.
		 * <p>
		 * Complexity: O(n*min(n,W))
		 * 
		 * @param f (CAL type: <code>a -> a -> a</code>)
		 *          the combining function.
		 * @param xs (CAL type: <code>[(Cal.Core.Prelude.Long, a)]</code>)
		 *          an ascending list of key-value pairs.
		 * @return (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>) 
		 *          a map containing the key-value entries.
		 */
		public static final SourceModel.Expr fromAscListWith(SourceModel.Expr f, SourceModel.Expr xs) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromAscListWith), f, xs});
		}

		/**
		 * Name binding for function: fromAscListWith.
		 * @see #fromAscListWith(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fromAscListWith = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "fromAscListWith");

		/**
		 * Builds a map from an ascending list in linear time with a combining function
		 * for equal keys. The precondition (input list is ascending) is not checked.
		 * <p>
		 * Complexity: O(n*min(n,W))
		 * 
		 * @param f (CAL type: <code>Cal.Core.Prelude.Long -> a -> a -> a</code>)
		 *          the combining function.
		 * @param xs (CAL type: <code>[(Cal.Core.Prelude.Long, a)]</code>)
		 *          an ascending list of key-value pairs.
		 * @return (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>) 
		 *          a map containing the key-value entries.
		 */
		public static final SourceModel.Expr fromAscListWithKey(SourceModel.Expr f, SourceModel.Expr xs) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromAscListWithKey), f, xs});
		}

		/**
		 * Name binding for function: fromAscListWithKey.
		 * @see #fromAscListWithKey(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fromAscListWithKey = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "fromAscListWithKey");

		/**
		 * Builds a map from an ascending list of distinct elements in linear time.
		 * The precondition is not checked.
		 * <p>
		 * Complexity: O(n*min(n,W))
		 * 
		 * @param xs (CAL type: <code>[(Cal.Core.Prelude.Long, a)]</code>)
		 *          an ascending list of distinct key-value pairs.
		 * @return (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>) 
		 *          a map containing the key-value entries.
		 */
		public static final SourceModel.Expr fromDistinctAscList(SourceModel.Expr xs) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromDistinctAscList), xs});
		}

		/**
		 * Name binding for function: fromDistinctAscList.
		 * @see #fromDistinctAscList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fromDistinctAscList = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "fromDistinctAscList");

		/**
		 * Builds a map from a list of key-value pairs.
		 * <p>
		 * Complexity: O(n*min(n,W))
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.LongMap.fromAscList
		 * </dl>
		 * 
		 * @param xs (CAL type: <code>[(Cal.Core.Prelude.Long, a)]</code>)
		 *          the list of key-value pairs.
		 * @return (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>) 
		 *          a map containing the key-value entries.
		 */
		public static final SourceModel.Expr fromList(SourceModel.Expr xs) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromList), xs});
		}

		/**
		 * Name binding for function: fromList.
		 * @see #fromList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fromList = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "fromList");

		/**
		 * Builds a map from a list of key-value pairs with a combining function.
		 * <p>
		 * Complexity: O(n*min(n,W))
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.LongMap.fromAscListWith
		 * </dl>
		 * 
		 * @param f (CAL type: <code>a -> a -> a</code>)
		 *          the combining function.
		 * @param xs (CAL type: <code>[(Cal.Core.Prelude.Long, a)]</code>)
		 *          the list of key-value pairs.
		 * @return (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>) 
		 *          a map containing the key-value entries.
		 */
		public static final SourceModel.Expr fromListWith(SourceModel.Expr f, SourceModel.Expr xs) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromListWith), f, xs});
		}

		/**
		 * Name binding for function: fromListWith.
		 * @see #fromListWith(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fromListWith = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "fromListWith");

		/**
		 * Builds a map from a list of key-value pairs with a combining function.
		 * <p>
		 * Complexity: O(n*min(n,W))
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.LongMap.fromAscListWithKey
		 * </dl>
		 * 
		 * @param f (CAL type: <code>Cal.Core.Prelude.Long -> a -> a -> a</code>)
		 *          the combining function.
		 * @param xs (CAL type: <code>[(Cal.Core.Prelude.Long, a)]</code>)
		 *          the list of key-value pairs.
		 * @return (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>) 
		 *          a map containing the key-value entries.
		 */
		public static final SourceModel.Expr fromListWithKey(SourceModel.Expr f, SourceModel.Expr xs) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromListWithKey), f, xs});
		}

		/**
		 * Name binding for function: fromListWithKey.
		 * @see #fromListWithKey(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fromListWithKey = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "fromListWithKey");

		/**
		 * Inserts a new key/value pair in the map. When the key is already an element of
		 * the set, it's value is replaced by the new value, ie. <code>insert</code> is
		 * left-biased.
		 * <p>
		 * Complexity: O(min(n,W))
		 * 
		 * @param k (CAL type: <code>Cal.Core.Prelude.Long</code>)
		 *          the key.
		 * @param x (CAL type: <code>a</code>)
		 *          the value.
		 * @param t (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>)
		 *          the map into which the key-value pair is to be inserted.
		 * @return (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>) 
		 *          the map with the key-value pair inserted.
		 */
		public static final SourceModel.Expr insert(SourceModel.Expr k, SourceModel.Expr x, SourceModel.Expr t) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.insert), k, x, t});
		}

		/**
		 * @see #insert(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param k
		 * @param x
		 * @param t
		 * @return the SourceModel.Expr representing an application of insert
		 */
		public static final SourceModel.Expr insert(long k, SourceModel.Expr x, SourceModel.Expr t) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.insert), SourceModel.Expr.makeLongValue(k), x, t});
		}

		/**
		 * Name binding for function: insert.
		 * @see #insert(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName insert = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "insert");

		/**
		 * <code>insertLookupWithKey f k x map</code> is a pair where the first element is equal to
		 * <code>Cal.Collections.LongMap.lookup k map</code> and the second element equal to <code>Cal.Collections.LongMap.insertWithKey f k x map</code>.
		 * <p>
		 * Complexity: O(min(n,W))
		 * 
		 * @param f (CAL type: <code>Cal.Core.Prelude.Long -> a -> a -> a</code>)
		 *          the combining function.
		 * @param k (CAL type: <code>Cal.Core.Prelude.Long</code>)
		 *          the key.
		 * @param x (CAL type: <code>a</code>)
		 *          the value.
		 * @param t (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>)
		 *          the map into which the key-value pair is to be inserted.
		 * @return (CAL type: <code>(Cal.Core.Prelude.Maybe a, Cal.Collections.LongMap.LongMap a)</code>) 
		 *          the map with the key-value pair inserted.
		 */
		public static final SourceModel.Expr insertLookupWithKey(SourceModel.Expr f, SourceModel.Expr k, SourceModel.Expr x, SourceModel.Expr t) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.insertLookupWithKey), f, k, x, t});
		}

		/**
		 * @see #insertLookupWithKey(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param f
		 * @param k
		 * @param x
		 * @param t
		 * @return the SourceModel.Expr representing an application of insertLookupWithKey
		 */
		public static final SourceModel.Expr insertLookupWithKey(SourceModel.Expr f, long k, SourceModel.Expr x, SourceModel.Expr t) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.insertLookupWithKey), f, SourceModel.Expr.makeLongValue(k), x, t});
		}

		/**
		 * Name binding for function: insertLookupWithKey.
		 * @see #insertLookupWithKey(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName insertLookupWithKey = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "insertLookupWithKey");

		/**
		 * Inserts a new key and value in the map with a combining function.
		 * <p>
		 * Complexity: O(min(n,W))
		 * 
		 * @param f (CAL type: <code>a -> a -> a</code>)
		 *          the combining function.
		 * @param k (CAL type: <code>Cal.Core.Prelude.Long</code>)
		 *          the key.
		 * @param x (CAL type: <code>a</code>)
		 *          the value.
		 * @param t (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>)
		 *          the map into which the key-value pair is to be inserted.
		 * @return (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>) 
		 *          the map with the key-value pair inserted.
		 */
		public static final SourceModel.Expr insertWith(SourceModel.Expr f, SourceModel.Expr k, SourceModel.Expr x, SourceModel.Expr t) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.insertWith), f, k, x, t});
		}

		/**
		 * @see #insertWith(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param f
		 * @param k
		 * @param x
		 * @param t
		 * @return the SourceModel.Expr representing an application of insertWith
		 */
		public static final SourceModel.Expr insertWith(SourceModel.Expr f, long k, SourceModel.Expr x, SourceModel.Expr t) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.insertWith), f, SourceModel.Expr.makeLongValue(k), x, t});
		}

		/**
		 * Name binding for function: insertWith.
		 * @see #insertWith(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName insertWith = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "insertWith");

		/**
		 * Inserts a new key and value in the map with a combining function.
		 * <p>
		 * Complexity: O(min(n,W))
		 * 
		 * @param f (CAL type: <code>Cal.Core.Prelude.Long -> a -> a -> a</code>)
		 *          the combining function.
		 * @param k (CAL type: <code>Cal.Core.Prelude.Long</code>)
		 *          the key.
		 * @param x (CAL type: <code>a</code>)
		 *          the value.
		 * @param t (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>)
		 *          the map into which the key-value pair is to be inserted.
		 * @return (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>) 
		 *          the map with the key-value pair inserted.
		 */
		public static final SourceModel.Expr insertWithKey(SourceModel.Expr f, SourceModel.Expr k, SourceModel.Expr x, SourceModel.Expr t) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.insertWithKey), f, k, x, t});
		}

		/**
		 * @see #insertWithKey(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param f
		 * @param k
		 * @param x
		 * @param t
		 * @return the SourceModel.Expr representing an application of insertWithKey
		 */
		public static final SourceModel.Expr insertWithKey(SourceModel.Expr f, long k, SourceModel.Expr x, SourceModel.Expr t) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.insertWithKey), f, SourceModel.Expr.makeLongValue(k), x, t});
		}

		/**
		 * Name binding for function: insertWithKey.
		 * @see #insertWithKey(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName insertWithKey = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "insertWithKey");

		/**
		 * Returns the (left-biased) intersection of two maps (based on keys). The values in the first map are returned.
		 * <p>
		 * Complexity: O(n+m)
		 * 
		 * @param t1 (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>)
		 *          the first map.
		 * @param t2 (CAL type: <code>Cal.Collections.LongMap.LongMap b</code>)
		 *          the second map.
		 * @return (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>) 
		 *          the intersection of the two maps.
		 */
		public static final SourceModel.Expr intersection(SourceModel.Expr t1, SourceModel.Expr t2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intersection), t1, t2});
		}

		/**
		 * Name binding for function: intersection.
		 * @see #intersection(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intersection = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "intersection");

		/**
		 * Returns the intersection of two maps, with a combining function.
		 * <p>
		 * Complexity: O(n+m)
		 * 
		 * @param f (CAL type: <code>a -> b -> c</code>)
		 *          the combining function.
		 * @param m1 (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>)
		 *          the first map.
		 * @param m2 (CAL type: <code>Cal.Collections.LongMap.LongMap b</code>)
		 *          the second map.
		 * @return (CAL type: <code>Cal.Collections.LongMap.LongMap c</code>) 
		 *          the intersection of the two maps.
		 */
		public static final SourceModel.Expr intersectionWith(SourceModel.Expr f, SourceModel.Expr m1, SourceModel.Expr m2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intersectionWith), f, m1, m2});
		}

		/**
		 * Name binding for function: intersectionWith.
		 * @see #intersectionWith(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intersectionWith = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "intersectionWith");

		/**
		 * Returns the intersection of two maps, with a combining function.
		 * <p>
		 * Complexity: O(n+m)
		 * 
		 * @param f (CAL type: <code>Cal.Core.Prelude.Long -> a -> b -> c</code>)
		 *          the combining function.
		 * @param t1 (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>)
		 *          the first map.
		 * @param t2 (CAL type: <code>Cal.Collections.LongMap.LongMap b</code>)
		 *          the second map.
		 * @return (CAL type: <code>Cal.Collections.LongMap.LongMap c</code>) 
		 *          the intersection of the two maps.
		 */
		public static final SourceModel.Expr intersectionWithKey(SourceModel.Expr f, SourceModel.Expr t1, SourceModel.Expr t2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intersectionWithKey), f, t1, t2});
		}

		/**
		 * Name binding for function: intersectionWithKey.
		 * @see #intersectionWithKey(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intersectionWithKey = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "intersectionWithKey");

		/**
		 * Returns whether the map is empty.
		 * <p>
		 * Complexity: O(1).
		 * 
		 * @param t (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>)
		 *          the map.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the map is empty; <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr isEmpty(SourceModel.Expr t) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isEmpty), t});
		}

		/**
		 * Name binding for function: isEmpty.
		 * @see #isEmpty(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isEmpty = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "isEmpty");

		/**
		 * Returns whether the first map is a proper submap of the second map (ie. a submap but not equal).
		 * <p>
		 * Complexity: O(n+m)
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.LongMap.isProperSubmapBy
		 * </dl>
		 * 
		 * @param m1 (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Collections.LongMap.LongMap a</code>)
		 *          the first map.
		 * @param m2 (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Collections.LongMap.LongMap a</code>)
		 *          the second map.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the first map is a proper submap of the second map; <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr isProperSubmap(SourceModel.Expr m1, SourceModel.Expr m2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isProperSubmap), m1, m2});
		}

		/**
		 * Name binding for function: isProperSubmap.
		 * @see #isProperSubmap(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isProperSubmap = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "isProperSubmap");

		/**
		 * Returns whether the first map is a proper submap of the second map (ie. a submap but not equal).
		 * <p>
		 * Complexity: O(n+m)
		 * <p>
		 * The expression <code>isProperSubmapBy f m1 m2</code> returns <code>Cal.Core.Prelude.True</code> when <code>m1</code> and <code>m2</code>
		 * are not equal, all keys in <code>m1</code> are in <code>m2</code>, and when <code>f</code> returns <code>Cal.Core.Prelude.True</code> when applied
		 * to their respective values.
		 * <p>
		 * For example, the following expressions are all <code>Cal.Core.Prelude.True</code>:
		 * 
		 * <pre> isProperSubmapBy Cal.Core.Prelude.equals         (Cal.Collections.LongMap.fromList [(1,1)]) (Cal.Collections.LongMap.fromList [(1,1),(2,2)])
		 *  isProperSubmapBy Cal.Core.Prelude.lessThanEquals (Cal.Collections.LongMap.fromList [(1,1)]) (Cal.Collections.LongMap.fromList [(1,1),(2,2)])</pre>
		 * 
		 * <p>
		 * But the following are all <code>Cal.Core.Prelude.False</code>:
		 * 
		 * <pre> isProperSubmapBy Cal.Core.Prelude.equals   (Cal.Collections.LongMap.fromList [(1,1),(2,2)]) (Cal.Collections.LongMap.fromList [(1,1),(2,2)])
		 *  isProperSubmapBy Cal.Core.Prelude.equals   (Cal.Collections.LongMap.fromList [(1,1),(2,2)]) (Cal.Collections.LongMap.fromList [(1,1)])
		 *  isProperSubmapBy Cal.Core.Prelude.lessThan (Cal.Collections.LongMap.fromList [(1,1)])       (Cal.Collections.LongMap.fromList [(1,1),(2,2)])</pre>
		 * 
		 * 
		 * @param pred (CAL type: <code>a -> b -> Cal.Core.Prelude.Boolean</code>)
		 *          a predicate for testing the equality of map values.
		 * @param t1 (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>)
		 *          the first map.
		 * @param t2 (CAL type: <code>Cal.Collections.LongMap.LongMap b</code>)
		 *          the second map.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the first map is a submap of the second map; <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr isProperSubmapBy(SourceModel.Expr pred, SourceModel.Expr t1, SourceModel.Expr t2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isProperSubmapBy), pred, t1, t2});
		}

		/**
		 * Name binding for function: isProperSubmapBy.
		 * @see #isProperSubmapBy(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isProperSubmapBy = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "isProperSubmapBy");

		/**
		 * Returns whether the first map is a submap of the second map.
		 * <p>
		 * Complexity: O(n+m)
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.LongMap.isSubmapBy
		 * </dl>
		 * 
		 * @param m1 (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Collections.LongMap.LongMap a</code>)
		 *          the first map.
		 * @param m2 (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Collections.LongMap.LongMap a</code>)
		 *          the second map.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the first map is a submap of the second map; <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr isSubmap(SourceModel.Expr m1, SourceModel.Expr m2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isSubmap), m1, m2});
		}

		/**
		 * Name binding for function: isSubmap.
		 * @see #isSubmap(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isSubmap = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "isSubmap");

		/**
		 * Returns whether the first map is a submap of the second map.
		 * <p>
		 * Complexity: O(n+m)
		 * <p>
		 * The expression <code>isSubmapBy f t1 t2</code> returns <code>Cal.Core.Prelude.True</code> if all keys in <code>t1</code> are in
		 * tree <code>t2</code>, and when <code>f</code> returns <code>Cal.Core.Prelude.True</code> when applied to their respective values.
		 * <p>
		 * For example, the following expressions are all <code>Cal.Core.Prelude.True</code>:
		 * 
		 * <pre> isSubmapBy Cal.Core.Prelude.equals         (Cal.Collections.LongMap.fromList [(1,1)]) (Cal.Collections.LongMap.fromList [(1,1),(2,2)])
		 *  isSubmapBy Cal.Core.Prelude.lessThanEquals (Cal.Collections.LongMap.fromList [(1,1)]) (Cal.Collections.LongMap.fromList [(1,1),(2,2)])
		 *  isSubmapBy Cal.Core.Prelude.equals         (Cal.Collections.LongMap.fromList [(1,1),(2,2)]) (Cal.Collections.LongMap.fromList [(1,1),(2,2)])</pre>
		 * 
		 * <p>
		 * But the following are all <code>Cal.Core.Prelude.False</code>:
		 * 
		 * <pre> isSubmapBy Cal.Core.Prelude.equals   (Cal.Collections.LongMap.fromList [(1,2)]) (Cal.Collections.LongMap.fromList [(1,1),(2,2)])
		 *  isSubmapBy Cal.Core.Prelude.lessThan (Cal.Collections.LongMap.fromList [(1,1)]) (Cal.Collections.LongMap.fromList [(1,1),(2,2)])
		 *  isSubmapBy Cal.Core.Prelude.equals   (Cal.Collections.LongMap.fromList [(1,1),(2,2)]) (Cal.Collections.LongMap.fromList [(1,1)])</pre>
		 * 
		 * 
		 * @param pred (CAL type: <code>a -> b -> Cal.Core.Prelude.Boolean</code>)
		 *          a predicate for testing the equality of map values.
		 * @param t1 (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>)
		 *          the first map.
		 * @param t2 (CAL type: <code>Cal.Collections.LongMap.LongMap b</code>)
		 *          the second map.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the first map is a submap of the second map; <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr isSubmapBy(SourceModel.Expr pred, SourceModel.Expr t1, SourceModel.Expr t2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isSubmapBy), pred, t1, t2});
		}

		/**
		 * Name binding for function: isSubmapBy.
		 * @see #isSubmapBy(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isSubmapBy = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "isSubmapBy");

		/**
		 * Returns a set of all keys of the map.
		 * <p>
		 * Complexity: O(n)
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Type Constructors:</b> Cal.Collections.Set.Set
		 * </dl>
		 * 
		 * @param m (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>)
		 *          the map.
		 * @return (CAL type: <code>Cal.Collections.Set.Set Cal.Core.Prelude.Long</code>) 
		 *          a Set of all keys of the map.
		 */
		public static final SourceModel.Expr keySet(SourceModel.Expr m) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.keySet), m});
		}

		/**
		 * Name binding for function: keySet.
		 * @see #keySet(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName keySet = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "keySet");

		/**
		 * Returns all keys of the map.
		 * <p>
		 * Complexity: O(n)
		 * 
		 * @param m (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>)
		 *          the map.
		 * @return (CAL type: <code>[Cal.Core.Prelude.Long]</code>) 
		 *          a list of all keys of the map.
		 */
		public static final SourceModel.Expr keys(SourceModel.Expr m) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.keys), m});
		}

		/**
		 * Name binding for function: keys.
		 * @see #keys(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName keys = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "keys");

		/**
		 * Looks up the value at a key in the map.
		 * <p>
		 * Complexity: O(min(n,W))
		 * 
		 * @param k (CAL type: <code>Cal.Core.Prelude.Long</code>)
		 *          the key.
		 * @param t (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>)
		 *          the map.
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe a</code>) 
		 *          <code>Cal.Core.Prelude.Just value</code> if the key is found in the map and associated with the
		 * value, or <code>Cal.Core.Prelude.Nothing</code> if the key is not found.
		 */
		public static final SourceModel.Expr lookup(SourceModel.Expr k, SourceModel.Expr t) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lookup), k, t});
		}

		/**
		 * @see #lookup(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param k
		 * @param t
		 * @return the SourceModel.Expr representing an application of lookup
		 */
		public static final SourceModel.Expr lookup(long k, SourceModel.Expr t) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lookup), SourceModel.Expr.makeLongValue(k), t});
		}

		/**
		 * Name binding for function: lookup.
		 * @see #lookup(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName lookup = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "lookup");

		/**
		 * <code>lookupWithDefault key map defaultValue</code> returns the value at the given key or <code>defaultValue</code>
		 * when the key is not in the map.
		 * <p>
		 * Complexity: O(min(n,W))
		 * 
		 * @param key (CAL type: <code>Cal.Core.Prelude.Long</code>)
		 *          the key.
		 * @param map (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>)
		 *          the map.
		 * @param defaultValue (CAL type: <code>a</code>)
		 *          the default value to be returned if the key is not in the map.
		 * @return (CAL type: <code>a</code>) 
		 *          the value at the given key or <code>defaultValue</code> when the key is not in the map.
		 */
		public static final SourceModel.Expr lookupWithDefault(SourceModel.Expr key, SourceModel.Expr map, SourceModel.Expr defaultValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lookupWithDefault), key, map, defaultValue});
		}

		/**
		 * @see #lookupWithDefault(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param key
		 * @param map
		 * @param defaultValue
		 * @return the SourceModel.Expr representing an application of lookupWithDefault
		 */
		public static final SourceModel.Expr lookupWithDefault(long key, SourceModel.Expr map, SourceModel.Expr defaultValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lookupWithDefault), SourceModel.Expr.makeLongValue(key), map, defaultValue});
		}

		/**
		 * Name binding for function: lookupWithDefault.
		 * @see #lookupWithDefault(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName lookupWithDefault = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "lookupWithDefault");

		/**
		 * Maps a function over all values in the map.
		 * <p>
		 * Complexity: O(n)
		 * 
		 * @param f (CAL type: <code>a -> b</code>)
		 *          the function to be mapped over the values in the map.
		 * @param m (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>)
		 *          the map.
		 * @return (CAL type: <code>Cal.Collections.LongMap.LongMap b</code>) 
		 *          the map containing the mapped values.
		 */
		public static final SourceModel.Expr map(SourceModel.Expr f, SourceModel.Expr m) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.map), f, m});
		}

		/**
		 * Name binding for function: map.
		 * @see #map(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName map = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "map");

		/**
		 * Threads an accumulating argument through the map in an unspecified order.
		 * <p>
		 * Complexity: O(n)
		 * 
		 * @param f (CAL type: <code>a -> b -> (a, c)</code>)
		 *          the function to be mapped over the values in the map.
		 * @param a (CAL type: <code>a</code>)
		 *          the accumulator to be thread through the map.
		 * @param m (CAL type: <code>Cal.Collections.LongMap.LongMap b</code>)
		 *          the map.
		 * @return (CAL type: <code>(a, Cal.Collections.LongMap.LongMap c)</code>) 
		 *          the map containing the mapped values.
		 */
		public static final SourceModel.Expr mapAccum(SourceModel.Expr f, SourceModel.Expr a, SourceModel.Expr m) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.mapAccum), f, a, m});
		}

		/**
		 * Name binding for function: mapAccum.
		 * @see #mapAccum(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName mapAccum = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "mapAccum");

		/**
		 * Threads an accumulating argument through the map in an unspecified order.
		 * <p>
		 * Complexity: O(n)
		 * 
		 * @param f (CAL type: <code>a -> Cal.Core.Prelude.Long -> b -> (a, c)</code>)
		 *          the function to be mapped over the keys and values in the map.
		 * @param a (CAL type: <code>a</code>)
		 *          the accumulator to be thread through the map.
		 * @param t (CAL type: <code>Cal.Collections.LongMap.LongMap b</code>)
		 *          the map.
		 * @return (CAL type: <code>(a, Cal.Collections.LongMap.LongMap c)</code>) 
		 *          the map containing the mapped values.
		 */
		public static final SourceModel.Expr mapAccumWithKey(SourceModel.Expr f, SourceModel.Expr a, SourceModel.Expr t) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.mapAccumWithKey), f, a, t});
		}

		/**
		 * Name binding for function: mapAccumWithKey.
		 * @see #mapAccumWithKey(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName mapAccumWithKey = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "mapAccumWithKey");

		/**
		 * <code>mapKeys f s</code> is the map obtained by applying <code>f</code> to each key of <code>s</code>.
		 * <p>
		 * Complexity: O(n*min(n,W))
		 * <p>
		 * The size of the result may be smaller if <code>f</code> maps two or more distinct keys to
		 * the same new key. In this case the value at the smallest of these keys is
		 * retained.
		 * 
		 * @param f (CAL type: <code>Cal.Core.Prelude.Long -> Cal.Core.Prelude.Long</code>)
		 *          the function to be mapped over the keys in the map.
		 * @param s (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>)
		 *          the map.
		 * @return (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>) 
		 *          the map containing the mapped keys.
		 */
		public static final SourceModel.Expr mapKeys(SourceModel.Expr f, SourceModel.Expr s) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.mapKeys), f, s});
		}

		/**
		 * Name binding for function: mapKeys.
		 * @see #mapKeys(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName mapKeys = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "mapKeys");

		/**
		 * <code>mapKeysWith c f s</code> is the map obtained by applying <code>f</code> to each key of <code>s</code>.
		 * <p>
		 * Complexity: O(n*min(n,W))
		 * <p>
		 * The size of the result may be smaller if <code>f</code> maps two or more distinct keys to
		 * the same new key. In this case the associated values will be combined using
		 * <code>c</code>.
		 * 
		 * @param c (CAL type: <code>a -> a -> a</code>)
		 *          the combining function.
		 * @param f (CAL type: <code>Cal.Core.Prelude.Long -> Cal.Core.Prelude.Long</code>)
		 *          the function to be mapped over the keys in the map.
		 * @param m (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>)
		 *          the map.
		 * @return (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>) 
		 *          the map containing the mapped keys.
		 */
		public static final SourceModel.Expr mapKeysWith(SourceModel.Expr c, SourceModel.Expr f, SourceModel.Expr m) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.mapKeysWith), c, f, m});
		}

		/**
		 * Name binding for function: mapKeysWith.
		 * @see #mapKeysWith(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName mapKeysWith = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "mapKeysWith");

		/**
		 * Maps a function over all values in the map.
		 * <p>
		 * Complexity: O(n)
		 * 
		 * @param f (CAL type: <code>Cal.Core.Prelude.Long -> a -> b</code>)
		 *          the function to be mapped over the keys and values in the map.
		 * @param t (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>)
		 *          the map.
		 * @return (CAL type: <code>Cal.Collections.LongMap.LongMap b</code>) 
		 *          the map containing the mapped values.
		 */
		public static final SourceModel.Expr mapWithKey(SourceModel.Expr f, SourceModel.Expr t) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.mapWithKey), f, t});
		}

		/**
		 * Name binding for function: mapWithKey.
		 * @see #mapWithKey(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName mapWithKey = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "mapWithKey");

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
		public static final SourceModel.Expr match(long i, long p, long m) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.match), SourceModel.Expr.makeLongValue(i), SourceModel.Expr.makeLongValue(p), SourceModel.Expr.makeLongValue(m)});
		}

		/**
		 * Name binding for function: match.
		 * @see #match(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName match = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "match");

		/**
		 * Returns whether the key is a member of the map.
		 * <p>
		 * Complexity: O(min(n,W))
		 * 
		 * @param k (CAL type: <code>Cal.Core.Prelude.Long</code>)
		 *          the key.
		 * @param m (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>)
		 *          the map.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the key is a member of the map; <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr member(SourceModel.Expr k, SourceModel.Expr m) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.member), k, m});
		}

		/**
		 * @see #member(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param k
		 * @param m
		 * @return the SourceModel.Expr representing an application of member
		 */
		public static final SourceModel.Expr member(long k, SourceModel.Expr m) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.member), SourceModel.Expr.makeLongValue(k), m});
		}

		/**
		 * Name binding for function: member.
		 * @see #member(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName member = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "member");

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
		public static final SourceModel.Expr nomatch(long i, long p, long m) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.nomatch), SourceModel.Expr.makeLongValue(i), SourceModel.Expr.makeLongValue(p), SourceModel.Expr.makeLongValue(m)});
		}

		/**
		 * Name binding for function: nomatch.
		 * @see #nomatch(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName nomatch = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "nomatch");

		/**
		 * Partitions the map according to a predicate. The first map contains all
		 * elements that satisfy the predicate, the second all elements that fail the
		 * predicate.
		 * <p>
		 * Complexity: O(n)
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.LongMap.split
		 * </dl>
		 * 
		 * @param p (CAL type: <code>a -> Cal.Core.Prelude.Boolean</code>)
		 *          the predicate for testing the values.
		 * @param m (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>)
		 *          the map.
		 * @return (CAL type: <code>(Cal.Collections.LongMap.LongMap a, Cal.Collections.LongMap.LongMap a)</code>) 
		 *          a pair of maps. The first map contains all elements that satisfy the
		 * predicate, the second all elements that fail the predicate.
		 */
		public static final SourceModel.Expr partition(SourceModel.Expr p, SourceModel.Expr m) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.partition), p, m});
		}

		/**
		 * Name binding for function: partition.
		 * @see #partition(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName partition = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "partition");

		/**
		 * Partitions the map according to a predicate. The first map contains all
		 * elements that satisfy the predicate, the second all elements that fail the
		 * predicate.
		 * <p>
		 * Complexity: O(n)
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.LongMap.split
		 * </dl>
		 * 
		 * @param pred (CAL type: <code>Cal.Core.Prelude.Long -> a -> Cal.Core.Prelude.Boolean</code>)
		 *          the predicate for testing the keys and values.
		 * @param t (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>)
		 *          the map.
		 * @return (CAL type: <code>(Cal.Collections.LongMap.LongMap a, Cal.Collections.LongMap.LongMap a)</code>) 
		 *          a pair of maps. The first map contains all elements that satisfy the
		 * predicate, the second all elements that fail the predicate.
		 */
		public static final SourceModel.Expr partitionWithKey(SourceModel.Expr pred, SourceModel.Expr t) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.partitionWithKey), pred, t});
		}

		/**
		 * Name binding for function: partitionWithKey.
		 * @see #partitionWithKey(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName partitionWithKey = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "partitionWithKey");

		/**
		 * Creates a map with a single element.
		 * <p>
		 * Complexity: O(1)
		 * 
		 * @param k (CAL type: <code>Cal.Core.Prelude.Long</code>)
		 *          the key.
		 * @param x (CAL type: <code>a</code>)
		 *          the value.
		 * @return (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>) 
		 *          a map with the specified key-value pair as its single element.
		 */
		public static final SourceModel.Expr single(SourceModel.Expr k, SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.single), k, x});
		}

		/**
		 * @see #single(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param k
		 * @param x
		 * @return the SourceModel.Expr representing an application of single
		 */
		public static final SourceModel.Expr single(long k, SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.single), SourceModel.Expr.makeLongValue(k), x});
		}

		/**
		 * Name binding for function: single.
		 * @see #single(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName single = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "single");

		/**
		 * Returns the number of elements in the map.
		 * <p>
		 * Complexity: O(n).
		 * 
		 * @param t (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>)
		 *          the map.
		 * @return (CAL type: <code>Cal.Core.Prelude.Long</code>) 
		 *          the number of elements in the map.
		 */
		public static final SourceModel.Expr size(SourceModel.Expr t) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.size), t});
		}

		/**
		 * Name binding for function: size.
		 * @see #size(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName size = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "size");

		/**
		 * <code>split k map</code> returns a pair <code>(map1,map2)</code> where the keys in <code>map1</code> are smaller
		 * than <code>k</code> and the keys in <code>map2</code> larger than <code>k</code>. Any key equal to <code>k</code> is found in
		 * neither <code>map1</code> nor <code>map2</code>.
		 * <p>
		 * Complexity: O(log n)
		 * 
		 * @param k (CAL type: <code>Cal.Core.Prelude.Long</code>)
		 *          the key.
		 * @param t (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>)
		 *          the map.
		 * @return (CAL type: <code>(Cal.Collections.LongMap.LongMap a, Cal.Collections.LongMap.LongMap a)</code>) 
		 *          a pair <code>(map1,map2)</code> where the keys in <code>map1</code> are smaller than <code>k</code> and the
		 * keys in <code>map2</code> larger than <code>k</code>.
		 */
		public static final SourceModel.Expr split(SourceModel.Expr k, SourceModel.Expr t) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.split), k, t});
		}

		/**
		 * @see #split(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param k
		 * @param t
		 * @return the SourceModel.Expr representing an application of split
		 */
		public static final SourceModel.Expr split(long k, SourceModel.Expr t) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.split), SourceModel.Expr.makeLongValue(k), t});
		}

		/**
		 * Name binding for function: split.
		 * @see #split(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName split = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "split");

		/**
		 * <code>splitLookup key map</code> splits a map just like <code>Cal.Collections.LongMap.split</code> but also returns <code>Cal.Collections.LongMap.lookup key map</code>.
		 * <p>
		 * Complexity: O(log n)
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.LongMap.split, Cal.Collections.LongMap.lookup
		 * </dl>
		 * 
		 * @param key (CAL type: <code>Cal.Core.Prelude.Long</code>)
		 * @param map (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>)
		 * @return (CAL type: <code>(Cal.Core.Prelude.Maybe a, Cal.Collections.LongMap.LongMap a, Cal.Collections.LongMap.LongMap a)</code>) 
		 *          a triple <code>(lookupResult, map1, map2)</code> where the keys in <code>map1</code> are smaller than <code>key</code> and the
		 * keys in <code>map2</code> larger than <code>key</code>, and where <code>lookupResult</code> is the result of looking
		 * up the key in the map.
		 */
		public static final SourceModel.Expr splitLookup(SourceModel.Expr key, SourceModel.Expr map) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.splitLookup), key, map});
		}

		/**
		 * @see #splitLookup(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param key
		 * @param map
		 * @return the SourceModel.Expr representing an application of splitLookup
		 */
		public static final SourceModel.Expr splitLookup(long key, SourceModel.Expr map) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.splitLookup), SourceModel.Expr.makeLongValue(key), map});
		}

		/**
		 * Name binding for function: splitLookup.
		 * @see #splitLookup(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName splitLookup = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "splitLookup");

		/**
		 * Converts to a list of key-value pairs with the keys in ascending order.
		 * <p>
		 * Complexity: O(n)
		 * 
		 * @param t (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>)
		 *          the map.
		 * @return (CAL type: <code>[(Cal.Core.Prelude.Long, a)]</code>) 
		 *          a list of key-value pairs with the keys in ascending order.
		 */
		public static final SourceModel.Expr toAscList(SourceModel.Expr t) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toAscList), t});
		}

		/**
		 * Name binding for function: toAscList.
		 * @see #toAscList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName toAscList = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "toAscList");

		/**
		 * Converts to a list of key-value pairs with the keys.
		 * <p>
		 * Complexity: O(n)
		 * 
		 * @param t (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>)
		 *          the map.
		 * @return (CAL type: <code>[(Cal.Core.Prelude.Long, a)]</code>) 
		 *          a list of key-value pairs with the keys in ascending order.
		 */
		public static final SourceModel.Expr toList(SourceModel.Expr t) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toList), t});
		}

		/**
		 * Name binding for function: toList.
		 * @see #toList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName toList = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "toList");

		/**
		 * <code>union t1 t2</code> takes the left-biased union of <code>t1</code> and <code>t2</code>. It prefers <code>t1</code> when
		 * duplicate keys are encountered, i.e. <code>(union == Cal.Collections.LongMap.unionWith Cal.Core.Prelude.const)</code>.
		 * <p>
		 * Complexity: O(n+m)
		 * 
		 * @param t1 (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>)
		 *          the first map.
		 * @param t2 (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>)
		 *          the second map.
		 * @return (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>) 
		 *          the left-biased union of <code>t1</code> and <code>t2</code>.
		 */
		public static final SourceModel.Expr union(SourceModel.Expr t1, SourceModel.Expr t2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.union), t1, t2});
		}

		/**
		 * Name binding for function: union.
		 * @see #union(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName union = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "union");

		/**
		 * Returns the left-biased union of two maps, with a combining function.
		 * <p>
		 * Complexity: O(n+m)
		 * 
		 * @param f (CAL type: <code>a -> a -> a</code>)
		 *          the combining function.
		 * @param m1 (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>)
		 *          the first map.
		 * @param m2 (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>)
		 *          the second map.
		 * @return (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>) 
		 *          the left-biased union of two maps.
		 */
		public static final SourceModel.Expr unionWith(SourceModel.Expr f, SourceModel.Expr m1, SourceModel.Expr m2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.unionWith), f, m1, m2});
		}

		/**
		 * Name binding for function: unionWith.
		 * @see #unionWith(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName unionWith = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "unionWith");

		/**
		 * Returns the left-biased union of two maps, with a combining function.
		 * <p>
		 * Complexity: O(n+m)
		 * 
		 * @param f (CAL type: <code>Cal.Core.Prelude.Long -> a -> a -> a</code>)
		 *          the combining function.
		 * @param t1 (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>)
		 *          the first map.
		 * @param t2 (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>)
		 *          the second map.
		 * @return (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>) 
		 *          the left-biased union of two maps.
		 */
		public static final SourceModel.Expr unionWithKey(SourceModel.Expr f, SourceModel.Expr t1, SourceModel.Expr t2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.unionWithKey), f, t1, t2});
		}

		/**
		 * Name binding for function: unionWithKey.
		 * @see #unionWithKey(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName unionWithKey = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "unionWithKey");

		/**
		 * Returns the union of a list of maps.
		 * @param xs (CAL type: <code>[Cal.Collections.LongMap.LongMap a]</code>)
		 *          a list of maps.
		 * @return (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>) 
		 *          the union of the maps in the list.
		 */
		public static final SourceModel.Expr unions(SourceModel.Expr xs) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.unions), xs});
		}

		/**
		 * Name binding for function: unions.
		 * @see #unions(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName unions = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "unions");

		/**
		 * Returns the union of a list of maps, with a combining operation.
		 * @param f (CAL type: <code>a -> a -> a</code>)
		 *          the combining function.
		 * @param xs (CAL type: <code>[Cal.Collections.LongMap.LongMap a]</code>)
		 *          a list of maps.
		 * @return (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>) 
		 *          the union of the maps in the list.
		 */
		public static final SourceModel.Expr unionsWith(SourceModel.Expr f, SourceModel.Expr xs) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.unionsWith), f, xs});
		}

		/**
		 * Name binding for function: unionsWith.
		 * @see #unionsWith(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName unionsWith = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "unionsWith");

		/**
		 * Returns the union of a list of maps, with a combining operation.
		 * @param f (CAL type: <code>Cal.Core.Prelude.Long -> a -> a -> a</code>)
		 *          the combining function.
		 * @param xs (CAL type: <code>[Cal.Collections.LongMap.LongMap a]</code>)
		 *          a list of maps.
		 * @return (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>) 
		 *          the union of the maps in the list.
		 */
		public static final SourceModel.Expr unionsWithKey(SourceModel.Expr f, SourceModel.Expr xs) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.unionsWithKey), f, xs});
		}

		/**
		 * Name binding for function: unionsWithKey.
		 * @see #unionsWithKey(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName unionsWithKey = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "unionsWithKey");

		/**
		 * <code>update f k map</code> updates the value <code>x</code> at <code>k</code> (if it is in the map). If <code>f x</code> is
		 * <code>Cal.Core.Prelude.Nothing</code>, the element is deleted. If it is <code>Cal.Core.Prelude.Just y</code>, the key <code>k</code> is bound to the
		 * new value <code>y</code>.
		 * <p>
		 * Complexity: O(min(n,W))
		 * 
		 * @param f (CAL type: <code>a -> Cal.Core.Prelude.Maybe a</code>)
		 *          the function used to map the old value associated with the key to the new value.
		 * @param k (CAL type: <code>Cal.Core.Prelude.Long</code>)
		 *          the key.
		 * @param m (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>)
		 *          the map.
		 * @return (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>) 
		 *          the map, with the value at the specified key adjusted if present.
		 */
		public static final SourceModel.Expr update(SourceModel.Expr f, SourceModel.Expr k, SourceModel.Expr m) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.update), f, k, m});
		}

		/**
		 * @see #update(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param f
		 * @param k
		 * @param m
		 * @return the SourceModel.Expr representing an application of update
		 */
		public static final SourceModel.Expr update(SourceModel.Expr f, long k, SourceModel.Expr m) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.update), f, SourceModel.Expr.makeLongValue(k), m});
		}

		/**
		 * Name binding for function: update.
		 * @see #update(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName update = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "update");

		/**
		 * Simultaneously looks up and updates the map at a specific key.
		 * <p>
		 * Complexity: O(min(n,W))
		 * 
		 * @param f (CAL type: <code>Cal.Core.Prelude.Long -> a -> Cal.Core.Prelude.Maybe a</code>)
		 *          the function which, when given the old key-value pair, returns the new
		 * value to be associated with the key.
		 * @param k (CAL type: <code>Cal.Core.Prelude.Long</code>)
		 *          the key.
		 * @param t (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>)
		 *          the map.
		 * @return (CAL type: <code>(Cal.Core.Prelude.Maybe a, Cal.Collections.LongMap.LongMap a)</code>) 
		 *          a pair. The first element contains either <code>Cal.Core.Prelude.Just value</code> if the key was
		 * originally associated with value, or <code>Cal.Core.Prelude.Nothing</code> otherwise. The second
		 * element contains the map, with the value at the specified key
		 * adjusted if present.
		 */
		public static final SourceModel.Expr updateLookupWithKey(SourceModel.Expr f, SourceModel.Expr k, SourceModel.Expr t) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.updateLookupWithKey), f, k, t});
		}

		/**
		 * @see #updateLookupWithKey(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param f
		 * @param k
		 * @param t
		 * @return the SourceModel.Expr representing an application of updateLookupWithKey
		 */
		public static final SourceModel.Expr updateLookupWithKey(SourceModel.Expr f, long k, SourceModel.Expr t) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.updateLookupWithKey), f, SourceModel.Expr.makeLongValue(k), t});
		}

		/**
		 * Name binding for function: updateLookupWithKey.
		 * @see #updateLookupWithKey(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName updateLookupWithKey = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "updateLookupWithKey");

		/**
		 * <code>updateWithKey f k map</code> updates the value <code>x</code> at <code>k</code> (if it is in the map). If
		 * <code>f k x</code> is <code>Cal.Core.Prelude.Nothing</code>, the element is deleted. If it is <code>Cal.Core.Prelude.Just y</code>, the key <code>k</code> is
		 * bound to the new value <code>y</code>.
		 * <p>
		 * Complexity: O(min(n,W))
		 * 
		 * @param f (CAL type: <code>Cal.Core.Prelude.Long -> a -> Cal.Core.Prelude.Maybe a</code>)
		 *          the function which, when given the old key-value pair, returns the new value to be associated with the key.
		 * @param k (CAL type: <code>Cal.Core.Prelude.Long</code>)
		 *          the key.
		 * @param t (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>)
		 *          the map.
		 * @return (CAL type: <code>Cal.Collections.LongMap.LongMap a</code>) 
		 *          the map, with the value at the specified key adjusted if present.
		 */
		public static final SourceModel.Expr updateWithKey(SourceModel.Expr f, SourceModel.Expr k, SourceModel.Expr t) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.updateWithKey), f, k, t});
		}

		/**
		 * @see #updateWithKey(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param f
		 * @param k
		 * @param t
		 * @return the SourceModel.Expr representing an application of updateWithKey
		 */
		public static final SourceModel.Expr updateWithKey(SourceModel.Expr f, long k, SourceModel.Expr t) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.updateWithKey), f, SourceModel.Expr.makeLongValue(k), t});
		}

		/**
		 * Name binding for function: updateWithKey.
		 * @see #updateWithKey(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName updateWithKey = 
			QualifiedName.make(CAL_LongMap.MODULE_NAME, "updateWithKey");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = -1104623940;

}
