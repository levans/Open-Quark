/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_Set.java)
 * was generated from CAL module: Cal.Collections.Set.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Collections.Set module from Java code.
 *  
 * Creation date: Fri Mar 16 13:11:57 PST 2007
 * --!>
 *  
 */

package org.openquark.cal.module.Cal.Collections;

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
public final class CAL_Set {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Collections.Set");

	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Collections.Set module.
	 */
	public static final class TypeConstructors {
		/**
		 * A set of values.
		 */
		public static final QualifiedName Set = 
			QualifiedName.make(CAL_Set.MODULE_NAME, "Set");

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Collections.Set module.
	 */
	public static final class Functions {
		/**
		 * Deletes an element from a set.
		 * <p>
		 * Complexity: O(log n)
		 * 
		 * @param x (CAL type: <code>Cal.Core.Prelude.Ord a => a</code>)
		 *          the element.
		 * @param t (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Collections.Set.Set a</code>)
		 *          the set.
		 * @return (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Collections.Set.Set a</code>) 
		 *          the set, with the specified element deleted if present.
		 */
		public static final SourceModel.Expr delete(SourceModel.Expr x, SourceModel.Expr t) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.delete), x, t});
		}

		/**
		 * Name binding for function: delete.
		 * @see #delete(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName delete = 
			QualifiedName.make(CAL_Set.MODULE_NAME, "delete");

		/**
		 * Helper binding method for function: deleteFindMax. 
		 * @param t
		 * @return the SourceModule.expr representing an application of deleteFindMax
		 */
		public static final SourceModel.Expr deleteFindMax(SourceModel.Expr t) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.deleteFindMax), t});
		}

		/**
		 * Name binding for function: deleteFindMax.
		 * @see #deleteFindMax(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName deleteFindMax = 
			QualifiedName.make(CAL_Set.MODULE_NAME, "deleteFindMax");

		/**
		 * Deletes and finds the minimal element.
		 * <p>
		 * Complexity: O(log n)
		 * 
		 * @param t (CAL type: <code>Cal.Collections.Set.Set a</code>)
		 *          the set.
		 * @return (CAL type: <code>(a, Cal.Collections.Set.Set a)</code>) 
		 *          a pair containing the minimal element and the set with the minimal element removed.
		 */
		public static final SourceModel.Expr deleteFindMin(SourceModel.Expr t) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.deleteFindMin), t});
		}

		/**
		 * Name binding for function: deleteFindMin.
		 * @see #deleteFindMin(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName deleteFindMin = 
			QualifiedName.make(CAL_Set.MODULE_NAME, "deleteFindMin");

		/**
		 * Deletes the maximal element of the set.
		 * <p>
		 * Complexity: O(log n)
		 * 
		 * @param t (CAL type: <code>Cal.Collections.Set.Set a</code>)
		 *          the set.
		 * @return (CAL type: <code>Cal.Collections.Set.Set a</code>) 
		 *          the set, with its maximal element deleted.
		 */
		public static final SourceModel.Expr deleteMax(SourceModel.Expr t) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.deleteMax), t});
		}

		/**
		 * Name binding for function: deleteMax.
		 * @see #deleteMax(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName deleteMax = 
			QualifiedName.make(CAL_Set.MODULE_NAME, "deleteMax");

		/**
		 * Deletes the minimal element of the set.
		 * <p>
		 * Complexity: O(log n)
		 * 
		 * @param t (CAL type: <code>Cal.Collections.Set.Set a</code>)
		 *          the set.
		 * @return (CAL type: <code>Cal.Collections.Set.Set a</code>) 
		 *          the set, with its minimal element deleted.
		 */
		public static final SourceModel.Expr deleteMin(SourceModel.Expr t) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.deleteMin), t});
		}

		/**
		 * Name binding for function: deleteMin.
		 * @see #deleteMin(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName deleteMin = 
			QualifiedName.make(CAL_Set.MODULE_NAME, "deleteMin");

		/**
		 * Returns the difference of two sets.
		 * <p>
		 * Complexity: O(n+m)
		 * 
		 * @param t1 (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Collections.Set.Set a</code>)
		 *          the first set.
		 * @param t2 (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Collections.Set.Set a</code>)
		 *          the second set.
		 * @return (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Collections.Set.Set a</code>) 
		 *          the difference of the two sets.
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
			QualifiedName.make(CAL_Set.MODULE_NAME, "difference");

		/**
		 * Returns all elements of a set.
		 * <p>
		 * Complexity: O(n)
		 * 
		 * @param s (CAL type: <code>Cal.Collections.Set.Set a</code>)
		 *          the set.
		 * @return (CAL type: <code>[a]</code>) 
		 *          a list of all elements of the set.
		 */
		public static final SourceModel.Expr elems(SourceModel.Expr s) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.elems), s});
		}

		/**
		 * Name binding for function: elems.
		 * @see #elems(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName elems = 
			QualifiedName.make(CAL_Set.MODULE_NAME, "elems");

		/**
		 * The empty set.
		 * <p>
		 * Complexity: O(1)
		 * 
		 * @return (CAL type: <code>Cal.Collections.Set.Set a</code>) 
		 *          an empty set.
		 */
		public static final SourceModel.Expr empty() {
			return SourceModel.Expr.Var.make(Functions.empty);
		}

		/**
		 * Name binding for function: empty.
		 * @see #empty()
		 */
		public static final QualifiedName empty = 
			QualifiedName.make(CAL_Set.MODULE_NAME, "empty");

		/**
		 * Filters all elements that satisfy the predicate.
		 * <p>
		 * Complexity: O(n)
		 * 
		 * @param p (CAL type: <code>a -> Cal.Core.Prelude.Boolean</code>)
		 *          the predicate for testing the elements.
		 * @param s (CAL type: <code>Cal.Collections.Set.Set a</code>)
		 *          the set.
		 * @return (CAL type: <code>Cal.Collections.Set.Set a</code>) 
		 *          the set containing only those elements that satisfy the predicate.
		 */
		public static final SourceModel.Expr filter(SourceModel.Expr p, SourceModel.Expr s) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.filter), p, s});
		}

		/**
		 * Name binding for function: filter.
		 * @see #filter(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName filter = 
			QualifiedName.make(CAL_Set.MODULE_NAME, "filter");

		/**
		 * Finds the maximal element of the set.
		 * <p>
		 * Complexity: O(log n)
		 * 
		 * @param t (CAL type: <code>Cal.Collections.Set.Set a</code>)
		 *          the set.
		 * @return (CAL type: <code>a</code>) 
		 *          the maximal element of the set.
		 */
		public static final SourceModel.Expr findMax(SourceModel.Expr t) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.findMax), t});
		}

		/**
		 * Name binding for function: findMax.
		 * @see #findMax(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName findMax = 
			QualifiedName.make(CAL_Set.MODULE_NAME, "findMax");

		/**
		 * Finds the minimal element of the set.
		 * <p>
		 * Complexity: O(log n)
		 * 
		 * @param t (CAL type: <code>Cal.Collections.Set.Set a</code>)
		 *          the set.
		 * @return (CAL type: <code>a</code>) 
		 *          the minimal element of the set.
		 */
		public static final SourceModel.Expr findMin(SourceModel.Expr t) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.findMin), t});
		}

		/**
		 * Name binding for function: findMin.
		 * @see #findMin(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName findMin = 
			QualifiedName.make(CAL_Set.MODULE_NAME, "findMin");

		/**
		 * Folds over the elements of a set.
		 * <p>
		 * Complexity: O(n)
		 * 
		 * @param f (CAL type: <code>a -> b -> b</code>)
		 *          the function to be folded over the elements in the set.
		 * @param z (CAL type: <code>b</code>)
		 * @param s (CAL type: <code>Cal.Collections.Set.Set a</code>)
		 *          the set.
		 * @return (CAL type: <code>b</code>) 
		 *          the summary value obtained from the fold.
		 */
		public static final SourceModel.Expr fold(SourceModel.Expr f, SourceModel.Expr z, SourceModel.Expr s) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fold), f, z, s});
		}

		/**
		 * Name binding for function: fold.
		 * @see #fold(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fold = 
			QualifiedName.make(CAL_Set.MODULE_NAME, "fold");

		/**
		 * Creates a set from an array of elements.
		 * @param array (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Collections.Array.Array a</code>)
		 *          - the array of elements
		 * @return (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Collections.Set.Set a</code>) 
		 *          a set containing the elements.
		 */
		public static final SourceModel.Expr fromArray(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromArray), array});
		}

		/**
		 * Name binding for function: fromArray.
		 * @see #fromArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fromArray = 
			QualifiedName.make(CAL_Set.MODULE_NAME, "fromArray");

		/**
		 * Creates a set from an array of elements, with
		 * each element being transformed by the provided 
		 * function.
		 * @param f (CAL type: <code>Cal.Core.Prelude.Ord b => a -> b</code>)
		 *          - the transformation function to apply to each array element.
		 * @param array (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          - the array of elements.
		 * @return (CAL type: <code>Cal.Core.Prelude.Ord b => Cal.Collections.Set.Set b</code>) 
		 *          a Set containing the transformed elements.
		 */
		public static final SourceModel.Expr fromArrayWith(SourceModel.Expr f, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromArrayWith), f, array});
		}

		/**
		 * Name binding for function: fromArrayWith.
		 * @see #fromArrayWith(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fromArrayWith = 
			QualifiedName.make(CAL_Set.MODULE_NAME, "fromArrayWith");

		/**
		 * Builds a set from an ascending list in linear time. The precondition (input
		 * list is ascending) is not checked.
		 * <p>
		 * Complexity: O(n)
		 * 
		 * @param xs (CAL type: <code>Cal.Core.Prelude.Eq a => [a]</code>)
		 *          an ascending list of elements.
		 * @return (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Collections.Set.Set a</code>) 
		 *          a set containing the elements.
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
			QualifiedName.make(CAL_Set.MODULE_NAME, "fromAscList");

		/**
		 * Builds a set from an ascending list of distinct elements in linear time.
		 * The precondition (input list is strictly ascending) is not checked.
		 * <p>
		 * Complexity: O(n)
		 * 
		 * @param xs (CAL type: <code>[a]</code>)
		 *          an ascending list of distinct elements.
		 * @return (CAL type: <code>Cal.Collections.Set.Set a</code>) 
		 *          a set containing the elements.
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
			QualifiedName.make(CAL_Set.MODULE_NAME, "fromDistinctAscList");

		/**
		 * Creates a set from a list of elements.
		 * <p>
		 * Complexity: O(n*log n)
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.Set.fromAscList
		 * </dl>
		 * 
		 * @param xs (CAL type: <code>Cal.Core.Prelude.Ord a => [a]</code>)
		 *          the list of elements.
		 * @return (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Collections.Set.Set a</code>) 
		 *          a set containing the elements.
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
			QualifiedName.make(CAL_Set.MODULE_NAME, "fromList");

		/**
		 * Creates a set from a list of elements with each
		 * element in the list being transformed by the provided function.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.Set.fromList
		 * </dl>
		 * 
		 * @param func (CAL type: <code>Cal.Core.Prelude.Ord b => a -> b</code>)
		 *          - the transformation function to apply to list elements.
		 * @param xs (CAL type: <code>[a]</code>)
		 *          - the list of elements.
		 * @return (CAL type: <code>Cal.Core.Prelude.Ord b => Cal.Collections.Set.Set b</code>) 
		 *          a set containing the transformed elements
		 */
		public static final SourceModel.Expr fromListWith(SourceModel.Expr func, SourceModel.Expr xs) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromListWith), func, xs});
		}

		/**
		 * Name binding for function: fromListWith.
		 * @see #fromListWith(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fromListWith = 
			QualifiedName.make(CAL_Set.MODULE_NAME, "fromListWith");

		/**
		 * Inserts an element in a set.
		 * <p>
		 * Complexity: O(log n)
		 * 
		 * @param x (CAL type: <code>Cal.Core.Prelude.Ord a => a</code>)
		 *          the element.
		 * @param t (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Collections.Set.Set a</code>)
		 *          the set into which the element is to be inserted.
		 * @return (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Collections.Set.Set a</code>) 
		 *          the set with the element inserted.
		 */
		public static final SourceModel.Expr insert(SourceModel.Expr x, SourceModel.Expr t) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.insert), x, t});
		}

		/**
		 * Name binding for function: insert.
		 * @see #insert(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName insert = 
			QualifiedName.make(CAL_Set.MODULE_NAME, "insert");

		/**
		 * Returns the intersection of two sets.
		 * <p>
		 * Complexity: O(n+m)
		 * 
		 * @param t1 (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Collections.Set.Set a</code>)
		 *          the first set.
		 * @param t2 (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Collections.Set.Set a</code>)
		 *          the second set.
		 * @return (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Collections.Set.Set a</code>) 
		 *          the intersection of the two sets.
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
			QualifiedName.make(CAL_Set.MODULE_NAME, "intersection");

		/**
		 * Returns the intersection of a list of sets.
		 * @param sets (CAL type: <code>Cal.Core.Prelude.Ord a => [Cal.Collections.Set.Set a]</code>)
		 *          a list of sets
		 * @return (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Collections.Set.Set a</code>) 
		 *          the intersection of the sets in the list
		 */
		public static final SourceModel.Expr intersections(SourceModel.Expr sets) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intersections), sets});
		}

		/**
		 * Name binding for function: intersections.
		 * @see #intersections(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intersections = 
			QualifiedName.make(CAL_Set.MODULE_NAME, "intersections");

		/**
		 * Returns whether the set is empty.
		 * <p>
		 * Complexity: O(1).
		 * 
		 * @param t (CAL type: <code>Cal.Collections.Set.Set a</code>)
		 *          the set.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the set is empty; <code>Cal.Core.Prelude.False</code> otherwise.
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
			QualifiedName.make(CAL_Set.MODULE_NAME, "isEmpty");

		/**
		 * Returns whether the first set is a proper subset of the second set (ie. a subset but not equal).
		 * <p>
		 * Complexity: O(n+m)
		 * 
		 * @param s1 (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Collections.Set.Set a</code>)
		 *          the first set.
		 * @param s2 (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Collections.Set.Set a</code>)
		 *          the second set.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the first set is a proper subset of the second set; <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr isProperSubsetOf(SourceModel.Expr s1, SourceModel.Expr s2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isProperSubsetOf), s1, s2});
		}

		/**
		 * Name binding for function: isProperSubsetOf.
		 * @see #isProperSubsetOf(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isProperSubsetOf = 
			QualifiedName.make(CAL_Set.MODULE_NAME, "isProperSubsetOf");

		/**
		 * Returns whether the first set is a subset of the second set.
		 * <p>
		 * Complexity: O(n+m)
		 * 
		 * @param t1 (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Collections.Set.Set a</code>)
		 *          the first set.
		 * @param t2 (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Collections.Set.Set a</code>)
		 *          the second set.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the first set is a subset of the second set; <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr isSubsetOf(SourceModel.Expr t1, SourceModel.Expr t2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isSubsetOf), t1, t2});
		}

		/**
		 * Name binding for function: isSubsetOf.
		 * @see #isSubsetOf(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isSubsetOf = 
			QualifiedName.make(CAL_Set.MODULE_NAME, "isSubsetOf");

		/**
		 * <code>map f s</code> is the set obtained by applying <code>f</code> to each element of <code>s</code>. It is possible
		 * that the size of the result may be smaller if, for some <code>(x,y)</code>, <code>x != y</code> but
		 * <code>(f x) == (f y)</code>.
		 * <p>
		 * Complexity: O(n*log n)
		 * 
		 * @param f (CAL type: <code>(Cal.Core.Prelude.Ord a, Cal.Core.Prelude.Ord b) => a -> b</code>)
		 *          the function to be mapped over the elements in the set.
		 * @param set (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Collections.Set.Set a</code>)
		 *          the set.
		 * @return (CAL type: <code>Cal.Core.Prelude.Ord b => Cal.Collections.Set.Set b</code>) 
		 *          the set containing the mapped elements.
		 */
		public static final SourceModel.Expr map(SourceModel.Expr f, SourceModel.Expr set) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.map), f, set});
		}

		/**
		 * Name binding for function: map.
		 * @see #map(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName map = 
			QualifiedName.make(CAL_Set.MODULE_NAME, "map");

		/**
		 * <code>mapJust mapFunction set</code> applies <code>mapFunction</code> to each element of set, discarding the <code>Cal.Core.Prelude.Nothing</code>
		 * values, and keeping the <em>Just</em> values after removing the <code>Cal.Core.Prelude.Just</code> constructor.
		 * @param mapFunction (CAL type: <code>Cal.Core.Prelude.Ord b => a -> Cal.Core.Prelude.Maybe b</code>)
		 *          the mapping function.
		 * @param set (CAL type: <code>Cal.Collections.Set.Set a</code>)
		 *          the Set whose elements are to be mapped.
		 * @return (CAL type: <code>Cal.Core.Prelude.Ord b => Cal.Collections.Set.Set b</code>) 
		 *          the Set of mapped values.
		 */
		public static final SourceModel.Expr mapJust(SourceModel.Expr mapFunction, SourceModel.Expr set) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.mapJust), mapFunction, set});
		}

		/**
		 * Name binding for function: mapJust.
		 * @see #mapJust(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName mapJust = 
			QualifiedName.make(CAL_Set.MODULE_NAME, "mapJust");

		/**
		 * Returns whether the element is in the set.
		 * <p>
		 * Complexity: O(log n)
		 * 
		 * @param x (CAL type: <code>Cal.Core.Prelude.Ord a => a</code>)
		 *          the element.
		 * @param t (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Collections.Set.Set a</code>)
		 *          the set.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the element is in the set; <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr member(SourceModel.Expr x, SourceModel.Expr t) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.member), x, t});
		}

		/**
		 * Name binding for function: member.
		 * @see #member(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName member = 
			QualifiedName.make(CAL_Set.MODULE_NAME, "member");

		/**
		 * Partitions the set into two sets, one with all elements that satisfy the predicate
		 * and one with all elements that don't satisfy the predicate.
		 * <p>
		 * Complexity: O(n)
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.Set.split
		 * </dl>
		 * 
		 * @param p (CAL type: <code>a -> Cal.Core.Prelude.Boolean</code>)
		 *          the predicate for testing the elements.
		 * @param s (CAL type: <code>Cal.Collections.Set.Set a</code>)
		 *          the set.
		 * @return (CAL type: <code>(Cal.Collections.Set.Set a, Cal.Collections.Set.Set a)</code>) 
		 *          a pair of sets. The first set contains all elements that satisfy the
		 * predicate, the second all elements that fail the predicate.
		 */
		public static final SourceModel.Expr partition(SourceModel.Expr p, SourceModel.Expr s) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.partition), p, s});
		}

		/**
		 * Name binding for function: partition.
		 * @see #partition(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName partition = 
			QualifiedName.make(CAL_Set.MODULE_NAME, "partition");

		/**
		 * Creates a singleton set.
		 * <p>
		 * Complexity: O(1)
		 * 
		 * @param x (CAL type: <code>a</code>)
		 *          the value.
		 * @return (CAL type: <code>Cal.Collections.Set.Set a</code>) 
		 *          a set with the specified value as its single element.
		 */
		public static final SourceModel.Expr single(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.single), x});
		}

		/**
		 * Name binding for function: single.
		 * @see #single(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName single = 
			QualifiedName.make(CAL_Set.MODULE_NAME, "single");

		/**
		 * Returns the number of elements in the set.
		 * <p>
		 * Complexity: O(1).
		 * 
		 * @param t (CAL type: <code>Cal.Collections.Set.Set a</code>)
		 *          the set.
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the number of elements in the set.
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
			QualifiedName.make(CAL_Set.MODULE_NAME, "size");

		/**
		 * <code>split x set</code> returns a pair <code>(set1,set2)</code> where all elements in <code>set1</code> are smaller
		 * than <code>x</code> and all elements in <code>set2</code> are larger than <code>x</code>. The value <code>x</code> itself is not to be found
		 * in either in <code>set1</code> or <code>set2</code>.
		 * <p>
		 * Complexity: O(log n)
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.Set.splitMember
		 * </dl>
		 * 
		 * @param x (CAL type: <code>Cal.Core.Prelude.Ord a => a</code>)
		 *          the pivot on which the set is to be split.
		 * @param t (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Collections.Set.Set a</code>)
		 *          the set.
		 * @return (CAL type: <code>Cal.Core.Prelude.Ord a => (Cal.Collections.Set.Set a, Cal.Collections.Set.Set a)</code>) 
		 *          a pair <code>(set1,set2)</code> where the elements in <code>set1</code> are smaller than <code>x</code> and the
		 * elements in <code>set2</code> larger than <code>x</code>.
		 */
		public static final SourceModel.Expr split(SourceModel.Expr x, SourceModel.Expr t) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.split), x, t});
		}

		/**
		 * Name binding for function: split.
		 * @see #split(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName split = 
			QualifiedName.make(CAL_Set.MODULE_NAME, "split");

		/**
		 * Performs a split but also returns whether the pivot element was found in the
		 * original set.
		 * <p>
		 * Complexity: O(log n)
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.Set.split
		 * </dl>
		 * 
		 * @param x (CAL type: <code>Cal.Core.Prelude.Ord a => a</code>)
		 *          the pivot on which the set is to be split.
		 * @param t (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Collections.Set.Set a</code>)
		 *          the set.
		 * @return (CAL type: <code>Cal.Core.Prelude.Ord a => (Cal.Core.Prelude.Boolean, Cal.Collections.Set.Set a, Cal.Collections.Set.Set a)</code>) 
		 *          a triple <code>(b,set1,set2)</code> where the elements in <code>set1</code> are smaller than <code>x</code> and the
		 * elements in <code>set2</code> larger than <code>x</code>, and where <code>b</code> is <code>Cal.Core.Prelude.True</code> iff the pivot <code>x</code> is
		 * found in the set <code>t</code>.
		 */
		public static final SourceModel.Expr splitMember(SourceModel.Expr x, SourceModel.Expr t) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.splitMember), x, t});
		}

		/**
		 * Name binding for function: splitMember.
		 * @see #splitMember(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName splitMember = 
			QualifiedName.make(CAL_Set.MODULE_NAME, "splitMember");

		/**
		 * Converts a Set to an Array of elements.
		 * @param set (CAL type: <code>Cal.Core.Prelude.Typeable a => Cal.Collections.Set.Set a</code>)
		 *          - the Set of elements.
		 * @return (CAL type: <code>Cal.Core.Prelude.Typeable a => Cal.Collections.Array.Array a</code>) 
		 *          an Array containing the elements in the set.
		 */
		public static final SourceModel.Expr toArray(SourceModel.Expr set) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toArray), set});
		}

		/**
		 * Name binding for function: toArray.
		 * @see #toArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName toArray = 
			QualifiedName.make(CAL_Set.MODULE_NAME, "toArray");

		/**
		 * Converts a Set to an Array of transformed elements.
		 * @param f (CAL type: <code>Cal.Core.Prelude.Typeable b => a -> b</code>)
		 *          - the function used to transform the Set elements.
		 * @param set (CAL type: <code>Cal.Collections.Set.Set a</code>)
		 *          - the Set of elements.
		 * @return (CAL type: <code>Cal.Core.Prelude.Typeable b => Cal.Collections.Array.Array b</code>) 
		 *          an Array containing the transformed elements of the set.
		 */
		public static final SourceModel.Expr toArrayWith(SourceModel.Expr f, SourceModel.Expr set) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toArrayWith), f, set});
		}

		/**
		 * Name binding for function: toArrayWith.
		 * @see #toArrayWith(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName toArrayWith = 
			QualifiedName.make(CAL_Set.MODULE_NAME, "toArrayWith");

		/**
		 * Converts the set to an ascending list of elements.
		 * <p>
		 * Complexity: O(n)
		 * 
		 * @param t (CAL type: <code>Cal.Collections.Set.Set a</code>)
		 *          the set.
		 * @return (CAL type: <code>[a]</code>) 
		 *          a list of elements in the set in ascending order.
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
			QualifiedName.make(CAL_Set.MODULE_NAME, "toAscList");

		/**
		 * Converts the set to an ascending list of elements.
		 * <p>
		 * Complexity: O(n)
		 * 
		 * @param s (CAL type: <code>Cal.Collections.Set.Set a</code>)
		 *          the set.
		 * @return (CAL type: <code>[a]</code>) 
		 *          a list of elements in the set in ascending order.
		 */
		public static final SourceModel.Expr toList(SourceModel.Expr s) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toList), s});
		}

		/**
		 * Name binding for function: toList.
		 * @see #toList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName toList = 
			QualifiedName.make(CAL_Set.MODULE_NAME, "toList");

		/**
		 * Converts the set to a list of transformed
		 * elements.
		 * @param f (CAL type: <code>a -> b</code>)
		 *          - the function used to transform the Set elements.
		 * @param set (CAL type: <code>Cal.Collections.Set.Set a</code>)
		 *          - the Set of elements.
		 * @return (CAL type: <code>[b]</code>) 
		 *          a List containing the transformed elements of the Set.
		 */
		public static final SourceModel.Expr toListWith(SourceModel.Expr f, SourceModel.Expr set) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toListWith), f, set});
		}

		/**
		 * Name binding for function: toListWith.
		 * @see #toListWith(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName toListWith = 
			QualifiedName.make(CAL_Set.MODULE_NAME, "toListWith");

		/**
		 * Returns the union of two sets. Uses the efficient <em>hedge-union</em> algorithm.
		 * @param t1 (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Collections.Set.Set a</code>)
		 *          the first set.
		 * @param t2 (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Collections.Set.Set a</code>)
		 *          the second set.
		 * @return (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Collections.Set.Set a</code>) 
		 *          the union of <code>t1</code> and <code>t2</code>.
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
			QualifiedName.make(CAL_Set.MODULE_NAME, "union");

		/**
		 * Returns the union of a list of sets.
		 * @param ts (CAL type: <code>Cal.Core.Prelude.Ord a => [Cal.Collections.Set.Set a]</code>)
		 *          a list of sets.
		 * @return (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Collections.Set.Set a</code>) 
		 *          the union of the sets in the list.
		 */
		public static final SourceModel.Expr unions(SourceModel.Expr ts) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.unions), ts});
		}

		/**
		 * Name binding for function: unions.
		 * @see #unions(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName unions = 
			QualifiedName.make(CAL_Set.MODULE_NAME, "unions");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = 583733088;

}
