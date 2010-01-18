/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_List.java)
 * was generated from CAL module: Cal.Collections.List.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Collections.List module from Java code.
 *  
 * Creation date: Tue Oct 16 16:54:52 PDT 2007
 * --!>
 *  
 */

package org.openquark.cal.module.Cal.Collections;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * Defines many useful functions for the <code>Cal.Core.Prelude.List</code> type. Note that the List type itself is defined
 * in the <code>Cal.Core.Prelude</code> module due to the fact that it is supported via built-in notation.
 * @author Bo Ilic
 */
public final class CAL_List {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Collections.List");

	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Collections.List module.
	 */
	public static final class TypeConstructors {
		/**
		 * A CAL foreign type corresponding to the Java type <code>java.util.Enumeration</code>.
		 * Primarily of use for supporting the functions <code>Cal.Collections.List.jEnumerationToJIterator</code> and <code>Cal.Collections.List.jEnumerationToJIterator</code> which
		 * convert between the 2 main interfaces representing iterators in Java.
		 */
		public static final QualifiedName JEnumeration = 
			QualifiedName.make(CAL_List.MODULE_NAME, "JEnumeration");

		/**
		 * A CAL foreign type corresponding to the Java type <code>java.util.Iterator</code>.
		 * Primarily of use for supporting the functions <code>Cal.Collections.List.toJIterator</code> and <code>Cal.Collections.List.toJIteratorWith</code>.
		 */
		public static final QualifiedName JIterator = 
			QualifiedName.make(CAL_List.MODULE_NAME, "JIterator");

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Collections.List module.
	 */
	public static final class Functions {
		/**
		 * <code>accumulateLeft</code> is similar to <code>Cal.Collections.List.foldLeft</code>, except that it returns the list of partial folds, instead of the completely
		 * folded result. <code>Cal.Collections.List.mapAccumulateLeft</code> is a generalization. <code>accumulateLeft</code> is similar to the Crystal Reports concept of 
		 * a running total.
		 * <p>
		 * If <code>accumulateFunction</code> is strict in both of its arguments it is usually better to use <code>Cal.Collections.List.accumulateLeftStrict</code>
		 * instead of <code>accumulateLeft</code> since using <code>accumulateLeft</code> will cause significantly more memory to be used than using
		 * <code>Cal.Collections.List.accumulateLeftStrict</code>. 
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.List.foldLeft, Cal.Collections.List.mapAccumulateLeft, Cal.Collections.List.accumulateLeft1, Cal.Collections.List.accumulateLeftStrict
		 * </dl>
		 * 
		 * @param accumulateFunction (CAL type: <code>a -> b -> a</code>)
		 *          the function to be used in accumulating the list.
		 * @param initialValue (CAL type: <code>a</code>)
		 *          the initial value for the accumulating process.
		 * @param list (CAL type: <code>[b]</code>)
		 *          the list to be accumulated.
		 * @return (CAL type: <code>[a]</code>) 
		 *          a list of successive accumulated values from the left.
		 */
		public static final SourceModel.Expr accumulateLeft(SourceModel.Expr accumulateFunction, SourceModel.Expr initialValue, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.accumulateLeft), accumulateFunction, initialValue, list});
		}

		/**
		 * Name binding for function: accumulateLeft.
		 * @see #accumulateLeft(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName accumulateLeft = 
			QualifiedName.make(CAL_List.MODULE_NAME, "accumulateLeft");

		/**
		 * <code>accumulateLeft1</code> is similar to <code>Cal.Collections.List.accumulateLeft</code>, except that it 
		 * uses the first element of the list as the initial value of the accumulation.
		 * <p>
		 * If <code>accumulateFunction</code> is strict in both of its arguments it is usually better to use <code>Cal.Collections.List.accumulateLeft1Strict</code>
		 * instead of <code>accumulateLeft1</code> since using <code>accumulateLeft1</code> will cause significantly more memory to be used than using
		 * <code>Cal.Collections.List.accumulateLeft1Strict</code>.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.List.accumulateLeft1Strict, Cal.Collections.List.foldLeft1, Cal.Collections.List.accumulateLeft
		 * </dl>
		 * 
		 * @param accumulateFunction (CAL type: <code>a -> a -> a</code>)
		 *          the function to be used in accumulating the list.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list to be accumulated.
		 * @return (CAL type: <code>[a]</code>) 
		 *          a list of successive accumulated values from the left.
		 */
		public static final SourceModel.Expr accumulateLeft1(SourceModel.Expr accumulateFunction, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.accumulateLeft1), accumulateFunction, list});
		}

		/**
		 * Name binding for function: accumulateLeft1.
		 * @see #accumulateLeft1(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName accumulateLeft1 = 
			QualifiedName.make(CAL_List.MODULE_NAME, "accumulateLeft1");

		/**
		 * <code>accumulateLeft1Strict</code> is similar to <code>Cal.Collections.List.accumulateLeftStrict</code>, except that it uses the first element of the list as
		 * the initial value in the accumulating process.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.List.foldLeft1, Cal.Collections.List.accumulateLeft1
		 * </dl>
		 * 
		 * @param accumulateFunction (CAL type: <code>a -> a -> a</code>)
		 *          the function to be used in accumulating the list.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list to be accumulated.
		 * @return (CAL type: <code>[a]</code>) 
		 *          a list of successive accumulated values from the left.
		 */
		public static final SourceModel.Expr accumulateLeft1Strict(SourceModel.Expr accumulateFunction, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.accumulateLeft1Strict), accumulateFunction, list});
		}

		/**
		 * Name binding for function: accumulateLeft1Strict.
		 * @see #accumulateLeft1Strict(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName accumulateLeft1Strict = 
			QualifiedName.make(CAL_List.MODULE_NAME, "accumulateLeft1Strict");

		/**
		 * This is the strict version of <code>Cal.Collections.List.accumulateLeft</code>. It is used for efficiency reasons in certain situations. 
		 * If <code>accumulateFunction</code> is strict in both of its arguments it is usually better to use <code>Cal.Collections.List.accumulateLeftStrict</code>
		 * instead of <code>accumulateLeft</code> since using <code>accumulateLeft</code> will cause significantly more memory to be used than using
		 * <code>Cal.Collections.List.accumulateLeftStrict</code>.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.List.foldLeftStrict, Cal.Collections.List.accumulateLeft
		 * </dl>
		 * 
		 * @param accumulateFunction (CAL type: <code>a -> b -> a</code>)
		 *          the function to be used in accumulating the list.
		 * @param initialValue (CAL type: <code>a</code>)
		 *          the initial value for the accumulating process.
		 * @param list (CAL type: <code>[b]</code>)
		 *          the list to be accumulated.
		 * @return (CAL type: <code>[a]</code>) 
		 *          a list of successive accumulated values from the left.
		 */
		public static final SourceModel.Expr accumulateLeftStrict(SourceModel.Expr accumulateFunction, SourceModel.Expr initialValue, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.accumulateLeftStrict), accumulateFunction, initialValue, list});
		}

		/**
		 * Name binding for function: accumulateLeftStrict.
		 * @see #accumulateLeftStrict(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName accumulateLeftStrict = 
			QualifiedName.make(CAL_List.MODULE_NAME, "accumulateLeftStrict");

		/**
		 * <code>accumulateRight</code> is similar to <code>Cal.Collections.List.accumulateLeft</code>, except that the accumulation process
		 * proceeds from right to left. Unlike foldRight and foldLeft, the result of accumulateRight and accumulateLeft
		 * generally differ, and the choice between them is not one of efficency.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.List.foldRight, Cal.Collections.List.mapAccumulateRight, Cal.Collections.List.accumulateLeft
		 * </dl>
		 * 
		 * @param accumulateFunction (CAL type: <code>a -> b -> b</code>)
		 *          the function to be used in accumulating the list.
		 * @param initialValue (CAL type: <code>b</code>)
		 *          the initial value for the accumulating process.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list to be scanned over.
		 * @return (CAL type: <code>[b]</code>) 
		 *          a list of successive accumulated values from the right.
		 */
		public static final SourceModel.Expr accumulateRight(SourceModel.Expr accumulateFunction, SourceModel.Expr initialValue, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.accumulateRight), accumulateFunction, initialValue, list});
		}

		/**
		 * Name binding for function: accumulateRight.
		 * @see #accumulateRight(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName accumulateRight = 
			QualifiedName.make(CAL_List.MODULE_NAME, "accumulateRight");

		/**
		 * <code>accumulateRight1</code> is similar to <code>Cal.Collections.List.accumulateRight</code>, except that it 
		 * uses the first element of the list as the initial value of the accumulation.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.List.foldRight1, Cal.Collections.List.accumulateRight
		 * </dl>
		 * 
		 * @param accumulateFunction (CAL type: <code>a -> a -> a</code>)
		 *          the function to be used in scanning the list.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list to be scanned over.
		 * @return (CAL type: <code>[a]</code>) 
		 *          a list of successive reduced values from the right.
		 */
		public static final SourceModel.Expr accumulateRight1(SourceModel.Expr accumulateFunction, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.accumulateRight1), accumulateFunction, list});
		}

		/**
		 * Name binding for function: accumulateRight1.
		 * @see #accumulateRight1(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName accumulateRight1 = 
			QualifiedName.make(CAL_List.MODULE_NAME, "accumulateRight1");

		/**
		 * <code>all predicate list</code> returns <code>Cal.Core.Prelude.True</code> if the predicate function evaluates to <code>Cal.Core.Prelude.True</code> on all the
		 * elements of the list (and the list is finite).
		 * @param predicate (CAL type: <code>a -> Cal.Core.Prelude.Boolean</code>)
		 *          a predicate to be applied to the elements of the list.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list whose elements are to be tested.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the predicate function evaluates to <code>Cal.Core.Prelude.True</code> on all the elements of the list (and the
		 * list is finite).
		 */
		public static final SourceModel.Expr all(SourceModel.Expr predicate, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.all), predicate, list});
		}

		/**
		 * Name binding for function: all.
		 * @see #all(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName all = 
			QualifiedName.make(CAL_List.MODULE_NAME, "all");

		/**
		 * Alternate items from the 2 lists.
		 * If one lists is longer than the other, then any remaining items will appear at the end of the list.
		 * @param list1 (CAL type: <code>[a]</code>)
		 *          one of the lists to be merged
		 * @param list2 (CAL type: <code>[a]</code>)
		 *          the other list to be merged
		 * @return (CAL type: <code>[a]</code>) 
		 *          a list consisting of alternating items from the input lists
		 */
		public static final SourceModel.Expr alternate(SourceModel.Expr list1, SourceModel.Expr list2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.alternate), list1, list2});
		}

		/**
		 * Name binding for function: alternate.
		 * @see #alternate(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName alternate = 
			QualifiedName.make(CAL_List.MODULE_NAME, "alternate");

		/**
		 * <code>andList list</code> returns <code>Cal.Core.Prelude.True</code> if every element of the list is <code>Cal.Core.Prelude.True</code> (and the list is finite).
		 * @param list (CAL type: <code>[Cal.Core.Prelude.Boolean]</code>)
		 *          the list whose elements are to be tested.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if every element of the list is <code>Cal.Core.Prelude.True</code> (and the list is finite).
		 */
		public static final SourceModel.Expr andList(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.andList), list});
		}

		/**
		 * Name binding for function: andList.
		 * @see #andList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName andList = 
			QualifiedName.make(CAL_List.MODULE_NAME, "andList");

		/**
		 * <code>any predicate list</code> returns <code>Cal.Core.Prelude.True</code> if the predicate function evaluates to <code>Cal.Core.Prelude.True</code> on at least
		 * one element of the list.
		 * @param predicate (CAL type: <code>a -> Cal.Core.Prelude.Boolean</code>)
		 *          a predicate to be applied to the elements of the list.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list whose elements are to be tested.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the predicate function evaluates to <code>Cal.Core.Prelude.True</code> on at least one element of the list.
		 */
		public static final SourceModel.Expr any(SourceModel.Expr predicate, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.any), predicate, list});
		}

		/**
		 * Name binding for function: any.
		 * @see #any(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName any = 
			QualifiedName.make(CAL_List.MODULE_NAME, "any");

		/**
		 * <code>break predicate list</code> breaks up list into a pair of lists. The start of the second list is the first element of
		 * <code>list</code> on which predicate is <code>Cal.Core.Prelude.True</code>.
		 * @param predicate (CAL type: <code>a -> Cal.Core.Prelude.Boolean</code>)
		 *          a predicate to be applied to the elements of the list.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list to be broken up into a pair of lists.
		 * @return (CAL type: <code>([a], [a])</code>) 
		 *          a pair of lists, where the first list is the longest prefix of the list for which predicate
		 * is <code>Cal.Core.Prelude.False</code> for each element, and the second list contains the remaining elements of the original list.
		 */
		public static final SourceModel.Expr break_(SourceModel.Expr predicate, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.break_), predicate, list});
		}

		/**
		 * Name binding for function: break.
		 * @see #break_(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName break_ = 
			QualifiedName.make(CAL_List.MODULE_NAME, "break");

		/**
		 * <code>breakAfter predicate list</code> breaks up list into a pair of lists. The last item of the first list is the first element
		 * of list on which the predicate function is <code>Cal.Core.Prelude.True</code>.
		 * @param predicate (CAL type: <code>a -> Cal.Core.Prelude.Boolean</code>)
		 *          a predicate to be applied to the elements of the list.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list to be split.
		 * @return (CAL type: <code>([a], [a])</code>) 
		 *          a pair of lists. The last item of the first list is the first element of list
		 * on which the predicate function is <code>Cal.Core.Prelude.True</code>. The second list contains the remaining
		 * elements of the original list.
		 */
		public static final SourceModel.Expr breakAfter(SourceModel.Expr predicate, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.breakAfter), predicate, list});
		}

		/**
		 * Name binding for function: breakAfter.
		 * @see #breakAfter(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName breakAfter = 
			QualifiedName.make(CAL_List.MODULE_NAME, "breakAfter");

		/**
		 * <code>breakAll predicate list</code> returns a list of lists, formed by splitting the original list before
		 * each element for which the predicate returns True.
		 * <p>
		 * e.g.
		 * <code>breakAll (\x -&gt; x==1) [2::Int,1,1,2,3,1,2] == [[2],[1], [1,2,3],[1,2]]</code>
		 * 
		 * @param predicate (CAL type: <code>a -> Cal.Core.Prelude.Boolean</code>)
		 *          a predicate to be applied to the elements of the list.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list to be broken up into sublists.
		 * @return (CAL type: <code>[[a]]</code>) 
		 *          a list of nonempty sublists of the original list, which <code>Cal.Core.Prelude.concat</code> to the original list, 
		 * and where each sublist has a maximal tail of elements not satisfying the predicate.
		 */
		public static final SourceModel.Expr breakAll(SourceModel.Expr predicate, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.breakAll), predicate, list});
		}

		/**
		 * Name binding for function: breakAll.
		 * @see #breakAll(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName breakAll = 
			QualifiedName.make(CAL_List.MODULE_NAME, "breakAll");

		/**
		 * Chops up a list into equals sublists of length <code>chopLength</code>.
		 * @param chopLength (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the length of the resulting sublists.
		 * @param listToChop (CAL type: <code>[a]</code>)
		 *          the list to be chopped.
		 * @return (CAL type: <code>[[a]]</code>) 
		 *          a list of the sublists.
		 */
		public static final SourceModel.Expr chop(SourceModel.Expr chopLength, SourceModel.Expr listToChop) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.chop), chopLength, listToChop});
		}

		/**
		 * @see #chop(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param chopLength
		 * @param listToChop
		 * @return the SourceModel.Expr representing an application of chop
		 */
		public static final SourceModel.Expr chop(int chopLength, SourceModel.Expr listToChop) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.chop), SourceModel.Expr.makeIntValue(chopLength), listToChop});
		}

		/**
		 * Name binding for function: chop.
		 * @see #chop(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName chop = 
			QualifiedName.make(CAL_List.MODULE_NAME, "chop");

		/**
		 * Returns a list of all the combinations of the items in the specified lists.
		 * If the firstIsFastestChanging flag is True, then this function iterates first over the earlier value lists.
		 * If firstIsFastestChanging is False, then the iteration happens first over the lists at lists at the end.
		 * For example, for the input lists <code>[[A, B, C], [1, 2, 3, 4]]</code> then the combinations where firstIsFastestChanging is True would be:
		 * <code>[[A, 1], [B, 1], [C, 1], [A, 2], [B, 2], [C, 2], [A, 3], [B, 3], [C, 3], [A, 4], [B, 4], [C, 4]]</code>
		 * When firstIsFastestChanging is False, the combinations would be:
		 * <code>[[A, 1], [A, 2], [A, 3], [A, 4], [B, 1], [B, 2], [B, 3], [B, 4], [C, 1], [C, 2], [C, 3], [C, 4]]</code>
		 * (Of course the values would all need to be of the same type.)
		 * If an empty list is specified for valuesLists, then [[]] (a single empty list) will be returned.
		 * @param firstIsFastestChanging (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 *          a flag indicating whether iteration happens first over the earlier or later lists
		 * @param valuesLists (CAL type: <code>[[a]]</code>)
		 *          a list of the lists of values
		 * @return (CAL type: <code>[[a]]</code>) 
		 *          a list of all the combinations of the items in the specified lists
		 */
		public static final SourceModel.Expr combinations(SourceModel.Expr firstIsFastestChanging, SourceModel.Expr valuesLists) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.combinations), firstIsFastestChanging, valuesLists});
		}

		/**
		 * @see #combinations(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param firstIsFastestChanging
		 * @param valuesLists
		 * @return the SourceModel.Expr representing an application of combinations
		 */
		public static final SourceModel.Expr combinations(boolean firstIsFastestChanging, SourceModel.Expr valuesLists) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.combinations), SourceModel.Expr.makeBooleanValue(firstIsFastestChanging), valuesLists});
		}

		/**
		 * Name binding for function: combinations.
		 * @see #combinations(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName combinations = 
			QualifiedName.make(CAL_List.MODULE_NAME, "combinations");

		/**
		 * Compares two lists lexicographically using a comparator function. 
		 * The empty list is considered less that any non-empty list.
		 * <p>
		 * Note that <code>Cal.Collections.List.compareBy Cal.Core.Prelude.compare == (compare :: Ord a =&gt; [a] -&gt; [a] -&gt; Ordering)</code>.
		 * Thus <code>Cal.Collections.List.compareBy</code> can be considered as a generalization of the class method <code>Cal.Core.Prelude.compare</code>
		 * when comparing 2 lists having possibly distinct types.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Prelude.compare
		 * </dl>
		 * 
		 * @param comparator (CAL type: <code>a -> b -> Cal.Core.Prelude.Ordering</code>)
		 * @param list1 (CAL type: <code>[a]</code>)
		 * @param list2 (CAL type: <code>[b]</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Ordering</code>) 
		 */
		public static final SourceModel.Expr compareBy(SourceModel.Expr comparator, SourceModel.Expr list1, SourceModel.Expr list2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.compareBy), comparator, list1, list2});
		}

		/**
		 * Name binding for function: compareBy.
		 * @see #compareBy(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName compareBy = 
			QualifiedName.make(CAL_List.MODULE_NAME, "compareBy");

		/**
		 * <code>composeAll [f1, f2, ... , fn] value</code> is the same as <code>f1 (f2 ( ... (fn value)))...)</code> which is the
		 * same as <code>f1 # f2 # ... # fn $ value</code>
		 * <p>
		 * For a fixed set of functions, it is more efficient to directly write the composition than to use composeAll.
		 * For example, rather than writing <code>composeAll [sin, cos, sqrt] 9.0</code> it is more efficient to write 
		 * <code>sin (cos (sqrt 9.0))</code>. 
		 * 
		 * @param functionList (CAL type: <code>[a -> a]</code>)
		 *          list of functions to compose
		 * @param value (CAL type: <code>a</code>)
		 *          the value to apply the compostion to.
		 * @return (CAL type: <code>a</code>) 
		 */
		public static final SourceModel.Expr composeAll(SourceModel.Expr functionList, SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.composeAll), functionList, value});
		}

		/**
		 * Name binding for function: composeAll.
		 * @see #composeAll(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName composeAll = 
			QualifiedName.make(CAL_List.MODULE_NAME, "composeAll");

		/**
		 * <code>concatMap mapFunction list</code> applies <code>mapFunction</code> to each element of list and then concatenates the
		 * resulting list. The result type of the <code>mapFunction</code> (b below) is <code>Cal.Core.Prelude.Appendable</code> to allow for the concatenation.
		 * @param mapFunction (CAL type: <code>Cal.Core.Prelude.Appendable b => a -> b</code>)
		 *          a function to be applied to the elements in the list.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list whose elements are to be mapped.
		 * @return (CAL type: <code>Cal.Core.Prelude.Appendable b => b</code>) 
		 *          the concatenation of the values obtained from mapping <code>mapFunction</code> to the elements in the list.
		 */
		public static final SourceModel.Expr concatMap(SourceModel.Expr mapFunction, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.concatMap), mapFunction, list});
		}

		/**
		 * Name binding for function: concatMap.
		 * @see #concatMap(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName concatMap = 
			QualifiedName.make(CAL_List.MODULE_NAME, "concatMap");

		/**
		 * <code>cycle list</code> is the infinite repetition of the list. If <code>list</code> is already an infinite list, then cycle list is
		 * just the original list.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list to be repeated.
		 * @return (CAL type: <code>[a]</code>) 
		 *          the infinite repetition of the list.
		 */
		public static final SourceModel.Expr cycle(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.cycle), list});
		}

		/**
		 * Name binding for function: cycle.
		 * @see #cycle(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName cycle = 
			QualifiedName.make(CAL_List.MODULE_NAME, "cycle");

		/**
		 * <code>delete x list</code> returns the list with the first element equivalent to <code>x</code> removed.
		 * <p>
		 * e.g. <code>delete 1 [3, 1, 4, 1, 5, 9] = [3, 4, 1, 5, 9]</code>
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.List.deleteBy
		 * </dl>
		 * 
		 * @param x (CAL type: <code>Cal.Core.Prelude.Eq a => a</code>)
		 *          the value of the element to be removed.
		 * @param list (CAL type: <code>Cal.Core.Prelude.Eq a => [a]</code>)
		 *          the list from which an element is to be removed.
		 * @return (CAL type: <code>Cal.Core.Prelude.Eq a => [a]</code>) 
		 *          the list with the first element equivalent to x removed.
		 */
		public static final SourceModel.Expr delete(SourceModel.Expr x, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.delete), x, list});
		}

		/**
		 * Name binding for function: delete.
		 * @see #delete(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName delete = 
			QualifiedName.make(CAL_List.MODULE_NAME, "delete");

		/**
		 * Removes the list item at the specified index.
		 * @param index (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the index of the item to be removed.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list from which an item is to be removed.
		 * @return (CAL type: <code>[a]</code>) 
		 *          the list with the item at the specified index removed.
		 */
		public static final SourceModel.Expr deleteAt(SourceModel.Expr index, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.deleteAt), index, list});
		}

		/**
		 * @see #deleteAt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param index
		 * @param list
		 * @return the SourceModel.Expr representing an application of deleteAt
		 */
		public static final SourceModel.Expr deleteAt(int index, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.deleteAt), SourceModel.Expr.makeIntValue(index), list});
		}

		/**
		 * Name binding for function: deleteAt.
		 * @see #deleteAt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName deleteAt = 
			QualifiedName.make(CAL_List.MODULE_NAME, "deleteAt");

		/**
		 * <code>deleteBy eq x list</code> returns the list with the first element equivalent to <code>x</code> (under <code>eq</code>) removed.
		 * <p>
		 * e.g. <code>deleteBy equals 1 [3, 1, 4, 1, 5, 9] = [3, 4, 1, 5, 9]</code>
		 * 
		 * @param eq (CAL type: <code>a -> b -> Cal.Core.Prelude.Boolean</code>)
		 *          the equality comparison function to use in comparing the list elements.
		 * @param x (CAL type: <code>a</code>)
		 *          the value of the element to be removed.
		 * @param list (CAL type: <code>[b]</code>)
		 *          the list from which an element is to be removed.
		 * @return (CAL type: <code>[b]</code>) 
		 *          the list with the first element equivalent to x (under <code>eq</code>) removed.
		 */
		public static final SourceModel.Expr deleteBy(SourceModel.Expr eq, SourceModel.Expr x, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.deleteBy), eq, x, list});
		}

		/**
		 * Name binding for function: deleteBy.
		 * @see #deleteBy(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName deleteBy = 
			QualifiedName.make(CAL_List.MODULE_NAME, "deleteBy");

		/**
		 * This function takes two lists and returns the first list with
		 * the first occurrence of each element of the second list removed.
		 * <p>
		 * Each element of the second list removes at most one element of the first list, and it removes the leftmost element.
		 * <p>
		 * e.g. 
		 * <ul>
		 *  <li>
		 *   <code>deleteFirsts [3, 1, 4, 1, 5] [1] = [3, 4, 1, 5]</code>
		 *  </li>
		 *  <li>
		 *   <code>deleteFirsts [3, 1, 4, 1, 5] [5, 4] = [3, 1, 1]</code>
		 *  </li>
		 *  <li>
		 *   <code>deleteFirsts [3] [1, 7] = [3]</code>
		 *  </li>
		 * </ul>
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.List.deleteFirstsBy
		 * </dl>
		 * 
		 * @param list1 (CAL type: <code>Cal.Core.Prelude.Eq a => [a]</code>)
		 *          the first list.
		 * @param list2 (CAL type: <code>Cal.Core.Prelude.Eq a => [a]</code>)
		 *          the second list.
		 * @return (CAL type: <code>Cal.Core.Prelude.Eq a => [a]</code>) 
		 *          the list <code>list1</code> with elements that are equivalent to elements in <code>list2</code> removed.
		 */
		public static final SourceModel.Expr deleteFirsts(SourceModel.Expr list1, SourceModel.Expr list2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.deleteFirsts), list1, list2});
		}

		/**
		 * Name binding for function: deleteFirsts.
		 * @see #deleteFirsts(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName deleteFirsts = 
			QualifiedName.make(CAL_List.MODULE_NAME, "deleteFirsts");

		/**
		 * This function takes a predicate and two lists and returns the first list with
		 * the first occurrence of each element of the second list removed.
		 * <p>
		 * Each element of the second list removes at most one element of the first list, and it removes the leftmost element.
		 * <p>
		 * e.g. 
		 * <ul>
		 *  <li>
		 *   <code>deleteFirstsBy Cal.Core.Prelude.equals [3, 1, 4, 1, 5] [1] = [3, 4, 1, 5]</code>
		 *  </li>
		 *  <li>
		 *   <code>deleteFirstsBy Cal.Core.Prelude.equals [3, 1, 4, 1, 5] [5, 4] = [3, 1, 1]</code>
		 *  </li>
		 *  <li>
		 *   <code>deleteFirstsBy Cal.Core.Prelude.equals [3] [1, 7] = [3]</code>
		 *  </li>
		 * </ul>
		 * 
		 * @param eq (CAL type: <code>a -> b -> Cal.Core.Prelude.Boolean</code>)
		 *          the equality comparison function to use in comparing the list elements.
		 * @param list1 (CAL type: <code>[b]</code>)
		 *          the first list.
		 * @param list2 (CAL type: <code>[a]</code>)
		 *          the second list.
		 * @return (CAL type: <code>[b]</code>) 
		 *          the list <code>list1</code> with elements that are equivalent to elements in <code>list2</code> (via <code>eq</code>) removed.
		 */
		public static final SourceModel.Expr deleteFirstsBy(SourceModel.Expr eq, SourceModel.Expr list1, SourceModel.Expr list2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.deleteFirstsBy), eq, list1, list2});
		}

		/**
		 * Name binding for function: deleteFirstsBy.
		 * @see #deleteFirstsBy(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName deleteFirstsBy = 
			QualifiedName.make(CAL_List.MODULE_NAME, "deleteFirstsBy");

		/**
		 * <code>drop nElements list</code> drops the first <code>nElements</code> elements of the list and returns the remaining elements
		 * @param nElements (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the number of elements to drop.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list from which elements are to be dropped.
		 * @return (CAL type: <code>[a]</code>) 
		 *          a list of the remaining elements.
		 */
		public static final SourceModel.Expr drop(SourceModel.Expr nElements, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.drop), nElements, list});
		}

		/**
		 * @see #drop(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param nElements
		 * @param list
		 * @return the SourceModel.Expr representing an application of drop
		 */
		public static final SourceModel.Expr drop(int nElements, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.drop), SourceModel.Expr.makeIntValue(nElements), list});
		}

		/**
		 * Name binding for function: drop.
		 * @see #drop(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName drop = 
			QualifiedName.make(CAL_List.MODULE_NAME, "drop");

		/**
		 * <code>dropWhile dropWhileTrueFunction list</code> drops the longest prefix of the list for which <code>dropWhileTrueFunction</code>
		 * is <code>Cal.Core.Prelude.True</code> for each element.
		 * <p>
		 * e.g. <code>dropWhile Cal.Core.Prelude.isEven [6, 2, 1, 2] = [1, 2]</code>
		 * 
		 * @param dropWhileTrueFunction (CAL type: <code>a -> Cal.Core.Prelude.Boolean</code>)
		 *          a predicate to be applied to the elements of the list.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list from which elements are to be taken.
		 * @return (CAL type: <code>[a]</code>) 
		 *          the remainder of the list after having dropped the longest prefix in
		 * which <code>dropWhileTrueFunction</code> is <code>Cal.Core.Prelude.True</code> for each element.
		 */
		public static final SourceModel.Expr dropWhile(SourceModel.Expr dropWhileTrueFunction, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.dropWhile), dropWhileTrueFunction, list});
		}

		/**
		 * Name binding for function: dropWhile.
		 * @see #dropWhile(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName dropWhile = 
			QualifiedName.make(CAL_List.MODULE_NAME, "dropWhile");

		/**
		 * Returns the index of the first element in the given list which is equal to
		 * the specified value, or <code>Cal.Core.Prelude.Nothing</code> if there is no such element.
		 * @param x (CAL type: <code>Cal.Core.Prelude.Eq a => a</code>)
		 *          the value to be found.
		 * @param list (CAL type: <code>Cal.Core.Prelude.Eq a => [a]</code>)
		 *          the list to be searched.
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Core.Prelude.Int</code>) 
		 *          the index of the first element in the given list which is equal to
		 * the specified value, or <code>Cal.Core.Prelude.Nothing</code> if there is no such element.
		 */
		public static final SourceModel.Expr elemIndex(SourceModel.Expr x, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.elemIndex), x, list});
		}

		/**
		 * Name binding for function: elemIndex.
		 * @see #elemIndex(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName elemIndex = 
			QualifiedName.make(CAL_List.MODULE_NAME, "elemIndex");

		/**
		 * Returns the indices of all elements of the specified list equal to the
		 * specified value, in ascending order.
		 * @param x (CAL type: <code>Cal.Core.Prelude.Eq a => a</code>)
		 *          the value to be found.
		 * @param list (CAL type: <code>Cal.Core.Prelude.Eq a => [a]</code>)
		 *          the list to be searched.
		 * @return (CAL type: <code>[Cal.Core.Prelude.Int]</code>) 
		 *          the indices of all elements of the specified list equal to the
		 * specified value, in ascending order.
		 */
		public static final SourceModel.Expr elemIndices(SourceModel.Expr x, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.elemIndices), x, list});
		}

		/**
		 * Name binding for function: elemIndices.
		 * @see #elemIndices(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName elemIndices = 
			QualifiedName.make(CAL_List.MODULE_NAME, "elemIndices");

		/**
		 * Returns whether <code>listToTest</code> ends with the specified suffix.
		 * @param suffix (CAL type: <code>Cal.Core.Prelude.Eq a => [a]</code>)
		 * @param listToTest (CAL type: <code>Cal.Core.Prelude.Eq a => [a]</code>)
		 *          the list to be tested for ending with <code>suffix</code>.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if <code>listToTest</code> ends with the list <code>suffix</code>.
		 */
		public static final SourceModel.Expr endsWith(SourceModel.Expr suffix, SourceModel.Expr listToTest) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.endsWith), suffix, listToTest});
		}

		/**
		 * Name binding for function: endsWith.
		 * @see #endsWith(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName endsWith = 
			QualifiedName.make(CAL_List.MODULE_NAME, "endsWith");

		/**
		 * <code>filter keepIfTrueFunction list</code> applies the predicate function to each element of the list, and returns
		 * the list of elements for which the predicate evaluates to <code>Cal.Core.Prelude.True</code>.
		 * @param keepIfTrueFunction (CAL type: <code>a -> Cal.Core.Prelude.Boolean</code>)
		 *          a predicate which returns <code>Cal.Core.Prelude.True</code> for items that should be kept, and <code>Cal.Core.Prelude.False</code> for
		 * items that should be dropped.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list.
		 * @return (CAL type: <code>[a]</code>) 
		 *          the list of those elements that satisfy the given predicate.
		 */
		public static final SourceModel.Expr filter(SourceModel.Expr keepIfTrueFunction, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.filter), keepIfTrueFunction, list});
		}

		/**
		 * Name binding for function: filter.
		 * @see #filter(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName filter = 
			QualifiedName.make(CAL_List.MODULE_NAME, "filter");

		/**
		 * <code>filterIndexed keepIfTrueFunction list</code> applies the predicate function <code>keepIfTrueFunction</code> to each element
		 * of the list, and returns the list of elements for which the predicate evaluates to <code>Cal.Core.Prelude.True</code>. <code>keepIfTrueFunction</code>
		 * is a function that is passed both the element value, and its zero-based index in the list.
		 * @param keepIfTrueFunction (CAL type: <code>a -> Cal.Core.Prelude.Int -> Cal.Core.Prelude.Boolean</code>)
		 *          a predicate which returns <code>Cal.Core.Prelude.True</code> for items that should be kept, and <code>Cal.Core.Prelude.False</code> for items that should
		 * be dropped. It is passed both the element value, and its zero-based index in the list.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list.
		 * @return (CAL type: <code>[a]</code>) 
		 *          the list of those elements that satisfy the given predicate.
		 */
		public static final SourceModel.Expr filterIndexed(SourceModel.Expr keepIfTrueFunction, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.filterIndexed), keepIfTrueFunction, list});
		}

		/**
		 * Name binding for function: filterIndexed.
		 * @see #filterIndexed(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName filterIndexed = 
			QualifiedName.make(CAL_List.MODULE_NAME, "filterIndexed");

		/**
		 * <code>filterJust listOfMaybes</code> filters <code>listOfMaybes</code>, removing the <code>Cal.Core.Prelude.Nothing</code> values, and taking the <code>Cal.Core.Prelude.Just</code>
		 * off the <em>Just</em> values. For example,
		 * <code>filterJust [Cal.Core.Prelude.Nothing, Cal.Core.Prelude.Just "Fred", Cal.Core.Prelude.Nothing, Cal.Core.Prelude.Just "Bob"] == ["Fred", "Bob"].</code>
		 * @param listOfMaybes (CAL type: <code>[Cal.Core.Prelude.Maybe a]</code>)
		 *          the list of Maybe values to filter.
		 * @return (CAL type: <code>[a]</code>) 
		 *          a list containing the <em>Just</em> values in the input list, with the <code>Cal.Core.Prelude.Just</code> data constructor taken off.
		 */
		public static final SourceModel.Expr filterJust(SourceModel.Expr listOfMaybes) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.filterJust), listOfMaybes});
		}

		/**
		 * Name binding for function: filterJust.
		 * @see #filterJust(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName filterJust = 
			QualifiedName.make(CAL_List.MODULE_NAME, "filterJust");

		/**
		 * <code>find predicate list</code> returns the first value of list for which the predicate function is <code>Cal.Core.Prelude.True</code>,
		 * if there is one, or <code>Cal.Core.Prelude.Nothing</code> otherwise.
		 * <p>
		 * e.g. <code>find Cal.Core.Prelude.isEven [1, 1, 4, 1, 2, 1, 1, 6] == Just 4</code>
		 * 
		 * @param predicate (CAL type: <code>a -> Cal.Core.Prelude.Boolean</code>)
		 *          a predicate to be applied to the elements of the list.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list to be searched.
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe a</code>) 
		 *          the first value of list for which the predicate function is <code>Cal.Core.Prelude.True</code>, if there is one, or
		 * <code>Cal.Core.Prelude.Nothing</code> otherwise.
		 */
		public static final SourceModel.Expr find(SourceModel.Expr predicate, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.find), predicate, list});
		}

		/**
		 * Name binding for function: find.
		 * @see #find(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName find = 
			QualifiedName.make(CAL_List.MODULE_NAME, "find");

		/**
		 * <code>findIndex predicate list</code> returns the first index of list for which the predicate function is <code>Cal.Core.Prelude.True</code>,
		 * if there is one, or <code>Cal.Core.Prelude.Nothing</code> otherwise. The index is 0-based.
		 * <p>
		 * e.g. <code>findIndex Cal.Core.Prelude.isEven [1, 1, 4, 1, 2, 1, 1, 6] == Cal.Core.Prelude.Just 2</code>
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.List.findIndices
		 * </dl>
		 * 
		 * @param predicate (CAL type: <code>a -> Cal.Core.Prelude.Boolean</code>)
		 *          a predicate to be applied to the elements of the list.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list to be searched.
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Core.Prelude.Int</code>) 
		 *          the first index of list for which the predicate function is <code>Cal.Core.Prelude.True</code>, if there is one, or <code>Cal.Core.Prelude.Nothing</code> otherwise.
		 */
		public static final SourceModel.Expr findIndex(SourceModel.Expr predicate, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.findIndex), predicate, list});
		}

		/**
		 * Name binding for function: findIndex.
		 * @see #findIndex(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName findIndex = 
			QualifiedName.make(CAL_List.MODULE_NAME, "findIndex");

		/**
		 * Returns the indices of all elements satisfying the specified predicate, in ascending order. The index values are 0-based.
		 * <p>
		 * e.g. <code>findIndices Cal.Core.Prelude.isEven [1, 1, 4, 1, 2, 1, 1, 6] == [2, 4, 7]</code>
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.List.findIndex
		 * </dl>
		 * 
		 * @param predicate (CAL type: <code>a -> Cal.Core.Prelude.Boolean</code>)
		 *          a predicate to be applied to the elements of the list.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list to be searched.
		 * @return (CAL type: <code>[Cal.Core.Prelude.Int]</code>) 
		 *          the list of all indices of list for which the predicate function evaluates to <code>Cal.Core.Prelude.True</code>.
		 */
		public static final SourceModel.Expr findIndices(SourceModel.Expr predicate, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.findIndices), predicate, list});
		}

		/**
		 * Name binding for function: findIndices.
		 * @see #findIndices(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName findIndices = 
			QualifiedName.make(CAL_List.MODULE_NAME, "findIndices");

		/**
		 * <code>foldLeft foldFunction initialValue list</code> collapses or 'folds' the list down to a single result, starting
		 * from the left of the list. It uses <code>initialValue</code> as the initial value for the folding process, and
		 * <code>foldFunction</code> as the collapsing function.
		 * <p>
		 * If <code>foldFunction</code> is strict in both of its arguments it is usually better to use <code>Cal.Collections.List.foldLeftStrict</code>
		 * instead of <code>foldLeft</code> since using <code>foldLeft</code> will cause significantly more memory to be used than using
		 * <code>Cal.Collections.List.foldLeftStrict</code>. If <code>foldFunction</code> is not strict in both of its arguments, it usually results in better
		 * lazy behavior to use <code>Cal.Collections.List.foldRight</code> rather than <code>foldLeft</code>. 
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.List.foldLeftStrict, Cal.Collections.List.foldRight, Cal.Collections.List.foldLeft1, Cal.Collections.List.foldLeft1Strict, Cal.Collections.List.foldRight1
		 * </dl>
		 * 
		 * @param foldFunction (CAL type: <code>a -> b -> a</code>)
		 *          the function to be used in folding the list.
		 * @param initialValue (CAL type: <code>a</code>)
		 *          the initial value for the folding process.
		 * @param list (CAL type: <code>[b]</code>)
		 *          the list to be folded over.
		 * @return (CAL type: <code>a</code>) 
		 *          the single result obtained from folding <code>foldFunction</code> over the list.
		 */
		public static final SourceModel.Expr foldLeft(SourceModel.Expr foldFunction, SourceModel.Expr initialValue, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.foldLeft), foldFunction, initialValue, list});
		}

		/**
		 * Name binding for function: foldLeft.
		 * @see #foldLeft(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName foldLeft = 
			QualifiedName.make(CAL_List.MODULE_NAME, "foldLeft");

		/**
		 * <code>foldLeft1</code> is similar to <code>Cal.Collections.List.foldLeft</code>, except that it uses the first element of the list as the initial value
		 * in the folding process. Hence it gives an error if list is the empty list.
		 * <p>
		 * If <code>foldFunction</code> is strict in both of its arguments it is usually better to use <code>Cal.Collections.List.foldLeft1Strict</code>
		 * instead of <code>foldLeft1</code> since using <code>foldLeft1</code> will cause significantly more memory to be used than using
		 * <code>Cal.Collections.List.foldLeft1Strict</code>. If <code>foldFunction</code> is not strict in both of its arguments, it usually results in better
		 * lazy behavior to use <code>Cal.Collections.List.foldRight1</code> rather than <code>foldLeft1</code>. 
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.List.foldLeft, Cal.Collections.List.foldLeftStrict, Cal.Collections.List.foldRight, Cal.Collections.List.foldLeft1Strict, Cal.Collections.List.foldRight1
		 * </dl>
		 * 
		 * @param foldFunction (CAL type: <code>a -> a -> a</code>)
		 *          the function to be used in folding the list.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list to be folded over.
		 * @return (CAL type: <code>a</code>) 
		 *          the single result obtained from folding <code>foldFunction</code> over the list.
		 */
		public static final SourceModel.Expr foldLeft1(SourceModel.Expr foldFunction, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.foldLeft1), foldFunction, list});
		}

		/**
		 * Name binding for function: foldLeft1.
		 * @see #foldLeft1(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName foldLeft1 = 
			QualifiedName.make(CAL_List.MODULE_NAME, "foldLeft1");

		/**
		 * <code>foldLeft1Strict</code> is similar to <code>Cal.Collections.List.foldLeftStrict</code>, except that it uses the first element of the list as
		 * the initial value in the folding process. Hence it gives an error if list is the empty list.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.List.foldLeft, Cal.Collections.List.foldLeftStrict, Cal.Collections.List.foldRight, Cal.Collections.List.foldLeft1, Cal.Collections.List.foldRight1
		 * </dl>
		 * 
		 * @param foldFunction (CAL type: <code>a -> a -> a</code>)
		 *          the function to be used in folding the list.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list to be folded over.
		 * @return (CAL type: <code>a</code>) 
		 *          the single result obtained from folding <code>foldFunction</code> over the list.
		 */
		public static final SourceModel.Expr foldLeft1Strict(SourceModel.Expr foldFunction, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.foldLeft1Strict), foldFunction, list});
		}

		/**
		 * Name binding for function: foldLeft1Strict.
		 * @see #foldLeft1Strict(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName foldLeft1Strict = 
			QualifiedName.make(CAL_List.MODULE_NAME, "foldLeft1Strict");

		/**
		 * This is the strict version of <code>Cal.Collections.List.foldLeft</code>. It is used for efficiency reasons in certain situations.
		 * For example, it can be used to define the length, sum and product functions so that they are constant
		 * space functions, whereas the versions defined with foldLeft would not be constant space.
		 * <p>
		 * If <code>foldFunction</code> is strict in both of its arguments it is usually better to use <code>foldLeftStrict</code>
		 * instead of <code>Cal.Collections.List.foldLeft</code> since using <code>Cal.Collections.List.foldLeft</code> will cause significantly more memory to be used than using
		 * <code>foldLeftStrict</code>. If <code>foldFunction</code> is not strict in both of its arguments, it usually results in better
		 * lazy behavior to use <code>Cal.Collections.List.foldRight</code> rather than <code>Cal.Collections.List.foldLeft</code>. 
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.List.foldLeft, Cal.Collections.List.foldRight, Cal.Collections.List.foldLeft1, Cal.Collections.List.foldLeft1Strict, Cal.Collections.List.foldRight1
		 * </dl>
		 * 
		 * @param foldFunction (CAL type: <code>a -> b -> a</code>)
		 *          the function to be used in folding the list.
		 * @param initialValue (CAL type: <code>a</code>)
		 *          the initial value for the folding process.
		 * @param list (CAL type: <code>[b]</code>)
		 *          the list to be folded over.
		 * @return (CAL type: <code>a</code>) 
		 *          the single result obtained from folding <code>foldFunction</code> over the list.
		 */
		public static final SourceModel.Expr foldLeftStrict(SourceModel.Expr foldFunction, SourceModel.Expr initialValue, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.foldLeftStrict), foldFunction, initialValue, list});
		}

		/**
		 * Name binding for function: foldLeftStrict.
		 * @see #foldLeftStrict(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName foldLeftStrict = 
			QualifiedName.make(CAL_List.MODULE_NAME, "foldLeftStrict");

		/**
		 * Similar to <code>Cal.Collections.List.foldLeft</code>, except that the folding process on the list is started with its rightmost element.
		 * Often the result of applying <code>Cal.Collections.List.foldLeft</code> or <code>foldRight</code> is the same, and the choice between them is a matter of
		 * efficiency. Which is better depends on the nature of the folding function. As a general rule, if the folding
		 * function is strict in both arguments, <code>Cal.Collections.List.foldLeftStrict</code> is a good choice. Otherwise <code>foldRight</code> is often best.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.List.foldLeft, Cal.Collections.List.foldLeftStrict, Cal.Collections.List.foldLeft1, Cal.Collections.List.foldLeft1Strict, Cal.Collections.List.foldRight1
		 * </dl>
		 * 
		 * @param foldFunction (CAL type: <code>a -> b -> b</code>)
		 *          the function to be used in folding the list.
		 * @param initialValue (CAL type: <code>b</code>)
		 *          the initial value for the folding process.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list to be folded over.
		 * @return (CAL type: <code>b</code>) 
		 *          the single result obtained from folding <code>foldFunction</code> over the list.
		 */
		public static final SourceModel.Expr foldRight(SourceModel.Expr foldFunction, SourceModel.Expr initialValue, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.foldRight), foldFunction, initialValue, list});
		}

		/**
		 * Name binding for function: foldRight.
		 * @see #foldRight(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName foldRight = 
			QualifiedName.make(CAL_List.MODULE_NAME, "foldRight");

		/**
		 * Similar to <code>Cal.Collections.List.foldLeft1</code>, except that the folding process on the list is started with its rightmost element.
		 * Often the result of applying <code>Cal.Collections.List.foldLeft1</code> or <code>foldRight1</code> is the same, and the choice between them is a matter of
		 * efficiency. Which is better depends on the nature of the folding function. As a general rule, if the folding
		 * function is strict in both arguments, <code>Cal.Collections.List.foldLeft1Strict</code> is a good choice. Otherwise <code>foldRight1</code> is often best.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.List.foldLeft, Cal.Collections.List.foldLeftStrict, Cal.Collections.List.foldRight, Cal.Collections.List.foldLeft1, Cal.Collections.List.foldLeft1Strict
		 * </dl>
		 * 
		 * @param foldFunction (CAL type: <code>a -> a -> a</code>)
		 *          the function to be used in folding the list.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list to be folded over.
		 * @return (CAL type: <code>a</code>) 
		 *          the single result obtained from folding <code>foldFunction</code> over the list.
		 */
		public static final SourceModel.Expr foldRight1(SourceModel.Expr foldFunction, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.foldRight1), foldFunction, list});
		}

		/**
		 * Name binding for function: foldRight1.
		 * @see #foldRight1(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName foldRight1 = 
			QualifiedName.make(CAL_List.MODULE_NAME, "foldRight1");

		/**
		 * Converts a Java collection to a CAL list.
		 * @param collection (CAL type: <code>Cal.Core.Prelude.JCollection</code>)
		 *          the Java collection.
		 * @return (CAL type: <code>Cal.Core.Prelude.Inputable a => [a]</code>) 
		 *          the corresponding CAL list.
		 */
		public static final SourceModel.Expr fromJCollection(SourceModel.Expr collection) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromJCollection), collection});
		}

		/**
		 * Name binding for function: fromJCollection.
		 * @see #fromJCollection(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fromJCollection = 
			QualifiedName.make(CAL_List.MODULE_NAME, "fromJCollection");

		/**
		 * Converts a Java collection to a CAL list using the element mapping function <code>f</code> of type <code>Cal.Core.Prelude.JObject -&gt; a</code> 
		 * to convert elements of the Java collection (in iterator order).
		 * @param javaCollection (CAL type: <code>Cal.Core.Prelude.JCollection</code>)
		 *          the Java collection.
		 * @param elementMappingFunction (CAL type: <code>Cal.Core.Prelude.JObject -> a</code>)
		 *          the mapping function converting elements of the Java collection to CAL values.
		 * @return (CAL type: <code>[a]</code>) 
		 *          the corresponding CAL list.
		 */
		public static final SourceModel.Expr fromJCollectionWith(SourceModel.Expr javaCollection, SourceModel.Expr elementMappingFunction) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromJCollectionWith), javaCollection, elementMappingFunction});
		}

		/**
		 * Name binding for function: fromJCollectionWith.
		 * @see #fromJCollectionWith(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fromJCollectionWith = 
			QualifiedName.make(CAL_List.MODULE_NAME, "fromJCollectionWith");

		/**
		 * Converts a Java iterator to a CAL list.
		 * <p>
		 * Note this function is mainly intended to be used when interacting programmatically with Java client code.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.List.fromJIteratorWith, Cal.Collections.List.toJIterator
		 * </dl>
		 * 
		 * @param iterator (CAL type: <code>Cal.Collections.List.JIterator</code>)
		 *          the Java iterator
		 * @return (CAL type: <code>Cal.Core.Prelude.Inputable a => [a]</code>) 
		 *          the corresponding CAL list.
		 */
		public static final SourceModel.Expr fromJIterator(SourceModel.Expr iterator) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromJIterator), iterator});
		}

		/**
		 * Name binding for function: fromJIterator.
		 * @see #fromJIterator(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fromJIterator = 
			QualifiedName.make(CAL_List.MODULE_NAME, "fromJIterator");

		/**
		 * Converts a Java iterator to a CAL list using the element mapping function <code>f</code> of 
		 * type <code>Cal.Core.Prelude.JObject -&gt; a</code> to convert iteration elements.
		 * <p>
		 * Note this function is mainly intended to be used when interacting programmatically with Java client code.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.List.fromJIterator, Cal.Collections.List.toJIteratorWith
		 * </dl>
		 * 
		 * @param iterator (CAL type: <code>Cal.Collections.List.JIterator</code>)
		 *          the Java iterator
		 * @param elementMappingFunction (CAL type: <code>Cal.Core.Prelude.JObject -> a</code>)
		 *          the mapping function converting iteration elements to CAL values.
		 * @return (CAL type: <code>[a]</code>) 
		 *          the corresponding CAL list.
		 */
		public static final SourceModel.Expr fromJIteratorWith(SourceModel.Expr iterator, SourceModel.Expr elementMappingFunction) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromJIteratorWith), iterator, elementMappingFunction});
		}

		/**
		 * Name binding for function: fromJIteratorWith.
		 * @see #fromJIteratorWith(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fromJIteratorWith = 
			QualifiedName.make(CAL_List.MODULE_NAME, "fromJIteratorWith");

		/**
		 * Splits the specified list into a list of lists of equal, adjacent elements.
		 * Note that the list is not sorted prior to grouping.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.List.groupBy
		 * </dl>
		 * 
		 * @param list (CAL type: <code>Cal.Core.Prelude.Eq a => [a]</code>)
		 *          the list to be split.
		 * @return (CAL type: <code>Cal.Core.Prelude.Eq a => [[a]]</code>) 
		 *          a list of lists of equal, adjacent elements.
		 */
		public static final SourceModel.Expr group(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.group), list});
		}

		/**
		 * Name binding for function: group.
		 * @see #group(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName group = 
			QualifiedName.make(CAL_List.MODULE_NAME, "group");

		/**
		 * Splits the specified list into a list of lists of equal, adjacent elements.
		 * Note that the list is not sorted prior to grouping.
		 * @param equalityFunction (CAL type: <code>a -> a -> Cal.Core.Prelude.Boolean</code>)
		 *          the equality comparison function to use in comparing the list elements.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list to be split.
		 * @return (CAL type: <code>[[a]]</code>) 
		 *          a list of lists of equal, adjacent elements.
		 */
		public static final SourceModel.Expr groupBy(SourceModel.Expr equalityFunction, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.groupBy), equalityFunction, list});
		}

		/**
		 * Name binding for function: groupBy.
		 * @see #groupBy(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName groupBy = 
			QualifiedName.make(CAL_List.MODULE_NAME, "groupBy");

		/**
		 * Returns the first element of the specified list. Terminates in an error on an empty list.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list whose first element is to be returned.
		 * @return (CAL type: <code>a</code>) 
		 *          the first element of the list.
		 */
		public static final SourceModel.Expr head(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.head), list});
		}

		/**
		 * Name binding for function: head.
		 * @see #head(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName head = 
			QualifiedName.make(CAL_List.MODULE_NAME, "head");

		/**
		 * Returns the position of the first occurrence of one list within another. This
		 * position is a 0-based index of the elements in <code>searchList</code>. If <code>findList</code> is not
		 * found in <code>searchList</code>, this function returns a call to <code>Cal.Core.Prelude.error</code>. The start
		 * argument sets the starting position for the search.
		 * @param equalityComparisonFunction (CAL type: <code>a -> b -> Cal.Core.Prelude.Boolean</code>)
		 *          the equality comparison function to use.
		 * @param fromIndex (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          0-based index of position to start searching from
		 * @param searchList (CAL type: <code>[b]</code>)
		 *          the list to be searched.
		 * @param findList (CAL type: <code>[a]</code>)
		 *          the list being sought within <code>searchList</code>.
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Core.Prelude.Int</code>) 
		 *          <code>Cal.Core.Prelude.Just</code> the 0-based index of the first occurrence of <code>findList</code> within <code>searchList</code>
		 * if any exist, or <code>Cal.Core.Prelude.Nothing</code> if <code>searchList</code> does not contain <code>findList</code>.
		 */
		public static final SourceModel.Expr inListBy(SourceModel.Expr equalityComparisonFunction, SourceModel.Expr fromIndex, SourceModel.Expr searchList, SourceModel.Expr findList) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inListBy), equalityComparisonFunction, fromIndex, searchList, findList});
		}

		/**
		 * @see #inListBy(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param equalityComparisonFunction
		 * @param fromIndex
		 * @param searchList
		 * @param findList
		 * @return the SourceModel.Expr representing an application of inListBy
		 */
		public static final SourceModel.Expr inListBy(SourceModel.Expr equalityComparisonFunction, int fromIndex, SourceModel.Expr searchList, SourceModel.Expr findList) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inListBy), equalityComparisonFunction, SourceModel.Expr.makeIntValue(fromIndex), searchList, findList});
		}

		/**
		 * Name binding for function: inListBy.
		 * @see #inListBy(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName inListBy = 
			QualifiedName.make(CAL_List.MODULE_NAME, "inListBy");

		/**
		 * Returns a list of all the elements of the list except the last one. Terminates in an error on an empty list.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list whose elements except the last one are to be returned.
		 * @return (CAL type: <code>[a]</code>) 
		 *          a list of all the elements of the list except the last one.
		 */
		public static final SourceModel.Expr init(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.init), list});
		}

		/**
		 * Name binding for function: init.
		 * @see #init(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName init = 
			QualifiedName.make(CAL_List.MODULE_NAME, "init");

		/**
		 * Returns the list of all initial segments of the specified list, shortest first.
		 * <p>
		 * e.g. <code>inits [3, 1, 4] = [[], [3], [3, 1], [3, 1, 4]]</code>
		 * 
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list whose initial segments are to be returned.
		 * @return (CAL type: <code>[[a]]</code>) 
		 *          the list of all initial segments of the argument, shortest first.
		 */
		public static final SourceModel.Expr inits(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inits), list});
		}

		/**
		 * Name binding for function: inits.
		 * @see #inits(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName inits = 
			QualifiedName.make(CAL_List.MODULE_NAME, "inits");

		/**
		 * Converts a Java list to a CAL list.
		 * @param list (CAL type: <code>Cal.Core.Prelude.JList</code>)
		 *          the Java list.
		 * @return (CAL type: <code>Cal.Core.Prelude.Inputable a => [a]</code>) 
		 *          the corresponding CAL list.
		 */
		public static final SourceModel.Expr inputList(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inputList), list});
		}

		/**
		 * Name binding for function: inputList.
		 * @see #inputList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName inputList = 
			QualifiedName.make(CAL_List.MODULE_NAME, "inputList");

		/**
		 * Converts a Java list to a CAL list using the element mapping function <code>f</code> of type <code>Cal.Core.Prelude.JObject -&gt; a</code> 
		 * to convert elements of the Java list.
		 * @param javaList (CAL type: <code>Cal.Core.Prelude.JList</code>)
		 *          the Java list.
		 * @param elementMappingFunction (CAL type: <code>Cal.Core.Prelude.JObject -> a</code>)
		 *          the mapping function converting elements of the Java list to CAL values.
		 * @return (CAL type: <code>[a]</code>) 
		 *          the corresponding CAL list.
		 */
		public static final SourceModel.Expr inputListWith(SourceModel.Expr javaList, SourceModel.Expr elementMappingFunction) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inputListWith), javaList, elementMappingFunction});
		}

		/**
		 * Name binding for function: inputListWith.
		 * @see #inputListWith(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName inputListWith = 
			QualifiedName.make(CAL_List.MODULE_NAME, "inputListWith");

		/**
		 * This function takes an element and a list and inserts the element into the
		 * list at the last position where it is still less than or equal to the next
		 * element. All order comparisons are done using the <code>Cal.Core.Prelude.compare</code> class
		 * method.
		 * <p>
		 * If the list is sorted before the call, the result will also be sorted.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.List.insertBy
		 * </dl>
		 * 
		 * @param value (CAL type: <code>Cal.Core.Prelude.Ord a => a</code>)
		 *          the value to be inserted into the list.
		 * @param list (CAL type: <code>Cal.Core.Prelude.Ord a => [a]</code>)
		 *          the list.
		 * @return (CAL type: <code>Cal.Core.Prelude.Ord a => [a]</code>) 
		 *          a copy of the input list with the specified value inserted.
		 */
		public static final SourceModel.Expr insert(SourceModel.Expr value, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.insert), value, list});
		}

		/**
		 * Name binding for function: insert.
		 * @see #insert(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName insert = 
			QualifiedName.make(CAL_List.MODULE_NAME, "insert");

		/**
		 * Inserts a value into a list at the specified index.
		 * @param index (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the zero-based index.
		 * @param value (CAL type: <code>a</code>)
		 *          the value to be inserted.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list to which the value is to be inserted.
		 * @return (CAL type: <code>[a]</code>) 
		 *          the list with the value inserted at the specified index.
		 */
		public static final SourceModel.Expr insertAt(SourceModel.Expr index, SourceModel.Expr value, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.insertAt), index, value, list});
		}

		/**
		 * @see #insertAt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param index
		 * @param value
		 * @param list
		 * @return the SourceModel.Expr representing an application of insertAt
		 */
		public static final SourceModel.Expr insertAt(int index, SourceModel.Expr value, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.insertAt), SourceModel.Expr.makeIntValue(index), value, list});
		}

		/**
		 * Name binding for function: insertAt.
		 * @see #insertAt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName insertAt = 
			QualifiedName.make(CAL_List.MODULE_NAME, "insertAt");

		/**
		 * This function takes an element and a list and inserts the element into the
		 * list at the last position where it is still less than or equal to the next
		 * element. All order comparisons are done using the supplied comparison
		 * function.
		 * <p>
		 * If the list is sorted before the call, the result will also be sorted.
		 * 
		 * @param comparisonFunction (CAL type: <code>a -> a -> Cal.Core.Prelude.Ordering</code>)
		 *          the comparison function.
		 * @param value (CAL type: <code>a</code>)
		 *          the value to be inserted into the list.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list.
		 * @return (CAL type: <code>[a]</code>) 
		 *          a copy of the input list with the specified value inserted.
		 */
		public static final SourceModel.Expr insertBy(SourceModel.Expr comparisonFunction, SourceModel.Expr value, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.insertBy), comparisonFunction, value, list});
		}

		/**
		 * Name binding for function: insertBy.
		 * @see #insertBy(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName insertBy = 
			QualifiedName.make(CAL_List.MODULE_NAME, "insertBy");

		/**
		 * Inserts values into a list at the specified index.
		 * @param index (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the zero-based index.
		 * @param valuesToInsert (CAL type: <code>[a]</code>)
		 *          the values to be inserted.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list to which the values are to be inserted.
		 * @return (CAL type: <code>[a]</code>) 
		 *          the list with the values inserted at the specified index.
		 */
		public static final SourceModel.Expr insertListAt(SourceModel.Expr index, SourceModel.Expr valuesToInsert, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.insertListAt), index, valuesToInsert, list});
		}

		/**
		 * @see #insertListAt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param index
		 * @param valuesToInsert
		 * @param list
		 * @return the SourceModel.Expr representing an application of insertListAt
		 */
		public static final SourceModel.Expr insertListAt(int index, SourceModel.Expr valuesToInsert, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.insertListAt), SourceModel.Expr.makeIntValue(index), valuesToInsert, list});
		}

		/**
		 * Name binding for function: insertListAt.
		 * @see #insertListAt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName insertListAt = 
			QualifiedName.make(CAL_List.MODULE_NAME, "insertListAt");

		/**
		 * Takes the intersection of two lists.
		 * <p>
		 * e.g. <code>intersect [3, 1, 4, 1] [1, 5, 9, 3] = [3, 1, 1]</code>
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.List.intersectBy
		 * </dl>
		 * 
		 * @param list1 (CAL type: <code>Cal.Core.Prelude.Eq a => [a]</code>)
		 *          the first list.
		 * @param list2 (CAL type: <code>Cal.Core.Prelude.Eq a => [a]</code>)
		 *          the second list.
		 * @return (CAL type: <code>Cal.Core.Prelude.Eq a => [a]</code>) 
		 *          the sublist of element of <code>list1</code> that are also in <code>list2</code>.
		 */
		public static final SourceModel.Expr intersect(SourceModel.Expr list1, SourceModel.Expr list2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intersect), list1, list2});
		}

		/**
		 * Name binding for function: intersect.
		 * @see #intersect(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intersect = 
			QualifiedName.make(CAL_List.MODULE_NAME, "intersect");

		/**
		 * Takes the intersection of two lists.
		 * <p>
		 * e.g. <code>intersectBy Cal.Core.Prelude.equals [3, 1, 4, 1] [1, 5, 9, 3] = [3, 1, 1]</code>
		 * 
		 * @param eq (CAL type: <code>a -> b -> Cal.Core.Prelude.Boolean</code>)
		 *          the equality comparison function to use in comparing the list elements.
		 * @param list1 (CAL type: <code>[a]</code>)
		 *          the first list.
		 * @param list2 (CAL type: <code>[b]</code>)
		 *          the second list.
		 * @return (CAL type: <code>[a]</code>) 
		 *          the sublist of element of <code>list1</code> that are also in <code>list2</code> according to <code>eq</code>.
		 */
		public static final SourceModel.Expr intersectBy(SourceModel.Expr eq, SourceModel.Expr list1, SourceModel.Expr list2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intersectBy), eq, list1, list2});
		}

		/**
		 * Name binding for function: intersectBy.
		 * @see #intersectBy(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intersectBy = 
			QualifiedName.make(CAL_List.MODULE_NAME, "intersectBy");

		/**
		 * This function takes an element and a list and "intersperses" that element
		 * between the elements of the list.
		 * <p>
		 * e.g. <code>intersperse 0 [1, 2, 3] = [1, 0, 2, 0, 3]</code>
		 * 
		 * @param separator (CAL type: <code>a</code>)
		 *          the element to be interspersed between the elements of the list.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list whose elements are to be interspersed by the separator.
		 * @return (CAL type: <code>[a]</code>) 
		 *          the interspersed list.
		 */
		public static final SourceModel.Expr intersperse(SourceModel.Expr separator, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intersperse), separator, list});
		}

		/**
		 * Name binding for function: intersperse.
		 * @see #intersperse(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intersperse = 
			QualifiedName.make(CAL_List.MODULE_NAME, "intersperse");

		/**
		 * <code>isElem elementValue list</code> returns <code>Cal.Core.Prelude.True</code> if <code>elementValue</code> is an element of the list.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.List.isElemBy
		 * </dl>
		 * 
		 * @param elementValue (CAL type: <code>Cal.Core.Prelude.Eq a => a</code>)
		 *          the value to be tested for membership in the list.
		 * @param list (CAL type: <code>Cal.Core.Prelude.Eq a => [a]</code>)
		 *          the list to be checked.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if <code>elementValue</code> is an element of the list.
		 */
		public static final SourceModel.Expr isElem(SourceModel.Expr elementValue, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isElem), elementValue, list});
		}

		/**
		 * Name binding for function: isElem.
		 * @see #isElem(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isElem = 
			QualifiedName.make(CAL_List.MODULE_NAME, "isElem");

		/**
		 * Returns whether a value is an element of a list, according to the specified equality comparison function.
		 * @param eq (CAL type: <code>a -> b -> Cal.Core.Prelude.Boolean</code>)
		 *          the equality comparison function to use in comparing the list elements.
		 * @param x (CAL type: <code>a</code>)
		 *          the value to be tested for membership in the list.
		 * @param list (CAL type: <code>[b]</code>)
		 *          the list to be checked.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if <code>x</code> is an element of the list according to <code>eq</code>; <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr isElemBy(SourceModel.Expr eq, SourceModel.Expr x, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isElemBy), eq, x, list});
		}

		/**
		 * Name binding for function: isElemBy.
		 * @see #isElemBy(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isElemBy = 
			QualifiedName.make(CAL_List.MODULE_NAME, "isElemBy");

		/**
		 * <code>isNotElem elementValue list</code> returns <code>Cal.Core.Prelude.True</code> if <code>elementValue</code> is not an element of the list.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.List.isElem
		 * </dl>
		 * 
		 * @param elementValue (CAL type: <code>Cal.Core.Prelude.Eq a => a</code>)
		 *          the value to be tested for membership in the list.
		 * @param list (CAL type: <code>Cal.Core.Prelude.Eq a => [a]</code>)
		 *          the list to be checked.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if <code>elementValue</code> is not an element of the list.
		 */
		public static final SourceModel.Expr isNotElem(SourceModel.Expr elementValue, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isNotElem), elementValue, list});
		}

		/**
		 * Name binding for function: isNotElem.
		 * @see #isNotElem(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isNotElem = 
			QualifiedName.make(CAL_List.MODULE_NAME, "isNotElem");

		/**
		 * Returns whether a list has exactly 1 element.
		 * <p>
		 * Because <code>Cal.Collections.List.length</code> is O(n) where n is the length of the list, it can be more efficient to
		 * call <code>Cal.Core.Prelude.isEmpty</code>, <code>Cal.Collections.List.isSingletonList</code> or <code>Cal.Collections.List.lengthAtLeast</code> (as appropriate) if all that
		 * is needed is a bound on the length of the list. 
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Prelude.isEmpty, Cal.Collections.List.lengthAtLeast, Cal.Collections.List.length
		 * </dl>
		 * 
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the list has exactly 1 element; <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr isSingletonList(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isSingletonList), list});
		}

		/**
		 * Name binding for function: isSingletonList.
		 * @see #isSingletonList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isSingletonList = 
			QualifiedName.make(CAL_List.MODULE_NAME, "isSingletonList");

		/**
		 * <code>iterate f x</code> returns the infinite list <code>[x, f x, f(f x), f(f(f x)), ...]</code>.
		 * @param iterationFunction (CAL type: <code>a -> a</code>)
		 *          the iteration function.
		 * @param initialValue (CAL type: <code>a</code>)
		 *          the initial value.
		 * @return (CAL type: <code>[a]</code>) 
		 *          the infinite list <code>[initialValue, iterationFunction initialValue, iterationFunction(iterationFunction initialValue), ...]</code>
		 */
		public static final SourceModel.Expr iterate(SourceModel.Expr iterationFunction, SourceModel.Expr initialValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.iterate), iterationFunction, initialValue});
		}

		/**
		 * Name binding for function: iterate.
		 * @see #iterate(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName iterate = 
			QualifiedName.make(CAL_List.MODULE_NAME, "iterate");

		/**
		 * Converts a Java enumeration to a Java iterator in a lazy fashion.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.List.fromJIterator, Cal.Collections.List.fromJIteratorWith
		 * </dl>
		 * 
		 * @param enumeration (CAL type: <code>Cal.Collections.List.JEnumeration</code>)
		 * @return (CAL type: <code>Cal.Collections.List.JIterator</code>) 
		 *          the corresponding iterator
		 */
		public static final SourceModel.Expr jEnumerationToJIterator(SourceModel.Expr enumeration) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jEnumerationToJIterator), enumeration});
		}

		/**
		 * Name binding for function: jEnumerationToJIterator.
		 * @see #jEnumerationToJIterator(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jEnumerationToJIterator = 
			QualifiedName.make(CAL_List.MODULE_NAME, "jEnumerationToJIterator");

		/**
		 * Converts a Java iterator to a Java enumeration in a lazy fashion.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.List.toJIterator, Cal.Collections.List.toJIteratorWith
		 * </dl>
		 * 
		 * @param iterator (CAL type: <code>Cal.Collections.List.JIterator</code>)
		 * @return (CAL type: <code>Cal.Collections.List.JEnumeration</code>) 
		 *          the corresponding enumeration
		 */
		public static final SourceModel.Expr jIteratorToJEnumeration(SourceModel.Expr iterator) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jIteratorToJEnumeration), iterator});
		}

		/**
		 * Name binding for function: jIteratorToJEnumeration.
		 * @see #jIteratorToJEnumeration(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jIteratorToJEnumeration = 
			QualifiedName.make(CAL_List.MODULE_NAME, "jIteratorToJEnumeration");

		/**
		 * This function intersperses a separator between each pair of elements in a list
		 * and then concatenates the list elements together.
		 * <p>
		 * e.g. <code>join ", " ["a", "b", "c"] = "a, b, c"</code>
		 * 
		 * @param separator (CAL type: <code>Cal.Core.Prelude.Appendable a => a</code>)
		 *          the element to be interspersed between the elements of the list.
		 * @param elements (CAL type: <code>Cal.Core.Prelude.Appendable a => [a]</code>)
		 *          the list whose elements are to be interspersed by the separator.
		 * @return (CAL type: <code>Cal.Core.Prelude.Appendable a => a</code>) 
		 *          the concatenation of the interspersed list.
		 */
		public static final SourceModel.Expr join(SourceModel.Expr separator, SourceModel.Expr elements) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.join), separator, elements});
		}

		/**
		 * Name binding for function: join.
		 * @see #join(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName join = 
			QualifiedName.make(CAL_List.MODULE_NAME, "join");

		/**
		 * Returns the last element of the specified list. This function is O(n) where n is the length of the list.
		 * Terminates in an error on an empty list.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list whose last element is to be returned.
		 * @return (CAL type: <code>a</code>) 
		 *          the first element of the list.
		 */
		public static final SourceModel.Expr last(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.last), list});
		}

		/**
		 * Name binding for function: last.
		 * @see #last(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName last = 
			QualifiedName.make(CAL_List.MODULE_NAME, "last");

		/**
		 * Returns a <code>Cal.Core.Prelude.List</code> with only the <code>Cal.Core.Prelude.Left</code> values of the <code>Cal.Core.Prelude.List</code> of 
		 * <code>Cal.Core.Prelude.Either</code>s passed in.
		 * @param list (CAL type: <code>[Cal.Core.Prelude.Either a b]</code>)
		 *          the <code>Cal.Core.Prelude.List</code> of eithers
		 * @return (CAL type: <code>[a]</code>) 
		 *          the <code>Cal.Core.Prelude.List</code> obtained by taking the <code>Cal.Core.Prelude.Left</code> value of each 
		 * of the <code>Cal.Core.Prelude.Either</code>s in the <code>Cal.Core.Prelude.List</code>
		 */
		public static final SourceModel.Expr leftValues(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.leftValues), list});
		}

		/**
		 * Name binding for function: leftValues.
		 * @see #leftValues(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName leftValues = 
			QualifiedName.make(CAL_List.MODULE_NAME, "leftValues");

		/**
		 * Returns the length of the specified list. This function is O(n) in time, where n is the length of the list.
		 * <p>
		 * Because <code>List.length</code> is O(n) where n is the length of the list, it can be more efficient to
		 * call <code>Cal.Core.Prelude.isEmpty</code>, <code>Cal.Collections.List.isSingletonList</code> or <code>Cal.Collections.List.lengthAtLeast</code> (as appropriate) if all that
		 * is needed is a bound on the length of the list.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Prelude.isEmpty, Cal.Collections.List.isSingletonList, Cal.Collections.List.lengthAtLeast
		 * </dl>
		 * 
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list whose length is to be returned.
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the length of the list.
		 */
		public static final SourceModel.Expr length(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.length), list});
		}

		/**
		 * Name binding for function: length.
		 * @see #length(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName length = 
			QualifiedName.make(CAL_List.MODULE_NAME, "length");

		/**
		 * <code>lengthAtLeast minLength list</code> returns <code>Cal.Core.Prelude.True</code> if the length of the list is greater than or equal
		 * to <code>minLength</code>.
		 * <p>
		 * Because <code>Cal.Collections.List.length</code> is O(n) where n is the length of the list, it can be more efficient to
		 * call <code>Cal.Core.Prelude.isEmpty</code>, <code>Cal.Collections.List.isSingletonList</code> or <code>Cal.Collections.List.lengthAtLeast</code> (as appropriate) if all that
		 * is needed is a bound on the length of the list.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Prelude.isEmpty, Cal.Collections.List.isSingletonList, Cal.Collections.List.length
		 * </dl>
		 * 
		 * @param minLength (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the minimum length the list must have for the function to return True.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the length of the list is greater than or equal to <code>minLength</code>.
		 */
		public static final SourceModel.Expr lengthAtLeast(SourceModel.Expr minLength, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lengthAtLeast), minLength, list});
		}

		/**
		 * @see #lengthAtLeast(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param minLength
		 * @param list
		 * @return the SourceModel.Expr representing an application of lengthAtLeast
		 */
		public static final SourceModel.Expr lengthAtLeast(int minLength, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lengthAtLeast), SourceModel.Expr.makeIntValue(minLength), list});
		}

		/**
		 * Name binding for function: lengthAtLeast.
		 * @see #lengthAtLeast(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName lengthAtLeast = 
			QualifiedName.make(CAL_List.MODULE_NAME, "lengthAtLeast");

		/**
		 * Constructs an empty list.
		 * @return (CAL type: <code>[a]</code>) 
		 *          an empty list.
		 */
		public static final SourceModel.Expr list0() {
			return SourceModel.Expr.Var.make(Functions.list0);
		}

		/**
		 * Name binding for function: list0.
		 * @see #list0()
		 */
		public static final QualifiedName list0 = 
			QualifiedName.make(CAL_List.MODULE_NAME, "list0");

		/**
		 * Constructs a list of one item.
		 * @param item (CAL type: <code>a</code>)
		 *          the item.
		 * @return (CAL type: <code>[a]</code>) 
		 *          the list containing the specified item.
		 */
		public static final SourceModel.Expr list1(SourceModel.Expr item) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.list1), item});
		}

		/**
		 * Name binding for function: list1.
		 * @see #list1(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName list1 = 
			QualifiedName.make(CAL_List.MODULE_NAME, "list1");

		/**
		 * Constructs a list of two items.
		 * @param item1 (CAL type: <code>a</code>)
		 *          the first item.
		 * @param item2 (CAL type: <code>a</code>)
		 *          the second item.
		 * @return (CAL type: <code>[a]</code>) 
		 *          the list containing all the specified items.
		 */
		public static final SourceModel.Expr list2(SourceModel.Expr item1, SourceModel.Expr item2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.list2), item1, item2});
		}

		/**
		 * Name binding for function: list2.
		 * @see #list2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName list2 = 
			QualifiedName.make(CAL_List.MODULE_NAME, "list2");

		/**
		 * Constructs a list of three items.
		 * @param item1 (CAL type: <code>a</code>)
		 *          the first item.
		 * @param item2 (CAL type: <code>a</code>)
		 *          the second item.
		 * @param item3 (CAL type: <code>a</code>)
		 *          the third item.
		 * @return (CAL type: <code>[a]</code>) 
		 *          the list containing all the specified items.
		 */
		public static final SourceModel.Expr list3(SourceModel.Expr item1, SourceModel.Expr item2, SourceModel.Expr item3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.list3), item1, item2, item3});
		}

		/**
		 * Name binding for function: list3.
		 * @see #list3(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName list3 = 
			QualifiedName.make(CAL_List.MODULE_NAME, "list3");

		/**
		 * Constructs a list of four items.
		 * @param item1 (CAL type: <code>a</code>)
		 *          the first item.
		 * @param item2 (CAL type: <code>a</code>)
		 *          the second item.
		 * @param item3 (CAL type: <code>a</code>)
		 *          the third item.
		 * @param item4 (CAL type: <code>a</code>)
		 *          the fourth item.
		 * @return (CAL type: <code>[a]</code>) 
		 *          the list containing all the specified items.
		 */
		public static final SourceModel.Expr list4(SourceModel.Expr item1, SourceModel.Expr item2, SourceModel.Expr item3, SourceModel.Expr item4) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.list4), item1, item2, item3, item4});
		}

		/**
		 * Name binding for function: list4.
		 * @see #list4(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName list4 = 
			QualifiedName.make(CAL_List.MODULE_NAME, "list4");

		/**
		 * Constructs a list of five items.
		 * @param item1 (CAL type: <code>a</code>)
		 *          the first item.
		 * @param item2 (CAL type: <code>a</code>)
		 *          the second item.
		 * @param item3 (CAL type: <code>a</code>)
		 *          the third item.
		 * @param item4 (CAL type: <code>a</code>)
		 *          the fourth item.
		 * @param item5 (CAL type: <code>a</code>)
		 *          the fifth item.
		 * @return (CAL type: <code>[a]</code>) 
		 *          the list containing all the specified items.
		 */
		public static final SourceModel.Expr list5(SourceModel.Expr item1, SourceModel.Expr item2, SourceModel.Expr item3, SourceModel.Expr item4, SourceModel.Expr item5) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.list5), item1, item2, item3, item4, item5});
		}

		/**
		 * Name binding for function: list5.
		 * @see #list5(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName list5 = 
			QualifiedName.make(CAL_List.MODULE_NAME, "list5");

		/**
		 * Constructs a list of six items.
		 * @param item1 (CAL type: <code>a</code>)
		 *          the first item.
		 * @param item2 (CAL type: <code>a</code>)
		 *          the second item.
		 * @param item3 (CAL type: <code>a</code>)
		 *          the third item.
		 * @param item4 (CAL type: <code>a</code>)
		 *          the fourth item.
		 * @param item5 (CAL type: <code>a</code>)
		 *          the fifth item.
		 * @param item6 (CAL type: <code>a</code>)
		 *          the sixth item.
		 * @return (CAL type: <code>[a]</code>) 
		 *          the list containing all the specified items.
		 */
		public static final SourceModel.Expr list6(SourceModel.Expr item1, SourceModel.Expr item2, SourceModel.Expr item3, SourceModel.Expr item4, SourceModel.Expr item5, SourceModel.Expr item6) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.list6), item1, item2, item3, item4, item5, item6});
		}

		/**
		 * Name binding for function: list6.
		 * @see #list6(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName list6 = 
			QualifiedName.make(CAL_List.MODULE_NAME, "list6");

		/**
		 * Constructs a list of seven items.
		 * @param item1 (CAL type: <code>a</code>)
		 *          the first item.
		 * @param item2 (CAL type: <code>a</code>)
		 *          the second item.
		 * @param item3 (CAL type: <code>a</code>)
		 *          the third item.
		 * @param item4 (CAL type: <code>a</code>)
		 *          the fourth item.
		 * @param item5 (CAL type: <code>a</code>)
		 *          the fifth item.
		 * @param item6 (CAL type: <code>a</code>)
		 *          the sixth item.
		 * @param item7 (CAL type: <code>a</code>)
		 *          the seventh item.
		 * @return (CAL type: <code>[a]</code>) 
		 *          the list containing all the specified items.
		 */
		public static final SourceModel.Expr list7(SourceModel.Expr item1, SourceModel.Expr item2, SourceModel.Expr item3, SourceModel.Expr item4, SourceModel.Expr item5, SourceModel.Expr item6, SourceModel.Expr item7) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.list7), item1, item2, item3, item4, item5, item6, item7});
		}

		/**
		 * Name binding for function: list7.
		 * @see #list7(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName list7 = 
			QualifiedName.make(CAL_List.MODULE_NAME, "list7");

		/**
		 * <code>lookup key map</code> returns the value corresponding to key in the map (association list).
		 * @param key (CAL type: <code>Cal.Core.Prelude.Eq a => a</code>)
		 *          the key for which the map is to be searched.
		 * @param map (CAL type: <code>Cal.Core.Prelude.Eq a => [(a, b)]</code>)
		 *          the map (association list) to be searched.
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe b</code>) 
		 *          the value corresponding to key in the map.
		 */
		public static final SourceModel.Expr lookup(SourceModel.Expr key, SourceModel.Expr map) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lookup), key, map});
		}

		/**
		 * Name binding for function: lookup.
		 * @see #lookup(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName lookup = 
			QualifiedName.make(CAL_List.MODULE_NAME, "lookup");

		/**
		 * Similar to <code>Cal.Collections.List.lookup</code> except it has a default specified which will be returned when the key can not be found in the
		 * map.  This avoids construction of the <code>Cal.Core.Prelude.Maybe</code> object returned from <code>Cal.Collections.List.lookup</code>.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.List.lookup
		 * </dl>
		 * 
		 * @param key (CAL type: <code>Cal.Core.Prelude.Eq a => a</code>)
		 *          the key to look up.
		 * @param keyValuePairs (CAL type: <code>Cal.Core.Prelude.Eq a => [(a, b)]</code>)
		 *          the association list of key-value pairs.
		 * @param defaultValue (CAL type: <code>b</code>)
		 *          the default value to return when the key cannot be found.
		 * @return (CAL type: <code>b</code>) 
		 */
		public static final SourceModel.Expr lookupWithDefault(SourceModel.Expr key, SourceModel.Expr keyValuePairs, SourceModel.Expr defaultValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lookupWithDefault), key, keyValuePairs, defaultValue});
		}

		/**
		 * Name binding for function: lookupWithDefault.
		 * @see #lookupWithDefault(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName lookupWithDefault = 
			QualifiedName.make(CAL_List.MODULE_NAME, "lookupWithDefault");

		/**
		 * <code>map mapFunction list</code> applies the function <code>mapFunction</code> to each element of the list and returns the
		 * resulting list.
		 * @param mapFunction (CAL type: <code>a -> b</code>)
		 *          a function to be applied to each element of the list.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list.
		 * @return (CAL type: <code>[b]</code>) 
		 *          the list obtained by applying <code>mapFunction</code> to each element of the list.
		 */
		public static final SourceModel.Expr map(SourceModel.Expr mapFunction, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.map), mapFunction, list});
		}

		/**
		 * Name binding for function: map.
		 * @see #map(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName map = 
			QualifiedName.make(CAL_List.MODULE_NAME, "map");

		/**
		 * This function behaves like a combination of <code>Cal.Collections.List.map</code> and <code>Cal.Collections.List.foldLeft</code>. It applies a
		 * function to each element of a list, passing an accumulating parameter from
		 * left to right, and returning a final value of this accumulator together with
		 * the new list.
		 * <p>
		 * Intuitively, this function encapsulates the logic of a running total computation.
		 * <p>
		 * In general, the accumulation proceeds one step at a time: f s{i} x{i} = (s{i+1}, y{i})
		 * and the function returns (s{n+1}, [y0, y1, ..., yn]). In other words, the final value of the
		 * accumulation s{n+1} as well as the partial result accumulations [y0, ..., yn].
		 * <p>
		 * There is a nice discussion in Peyton Jones and Lester, Implementing Functional Languages pg 56.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.List.mapAccumulateRight, Cal.Collections.List.map, Cal.Collections.List.foldLeft, Cal.Collections.List.accumulateLeft
		 * </dl>
		 * 
		 * @param accumulatorFunction (CAL type: <code>a -> b -> (a, c)</code>)
		 *          the processing function.
		 * @param initialAccumulator (CAL type: <code>a</code>)
		 *          the initial value of the accumulator.
		 * @param list (CAL type: <code>[b]</code>)
		 *          the list of values over which the accumulation is performed.
		 * @return (CAL type: <code>(a, [c])</code>) 
		 */
		public static final SourceModel.Expr mapAccumulateLeft(SourceModel.Expr accumulatorFunction, SourceModel.Expr initialAccumulator, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.mapAccumulateLeft), accumulatorFunction, initialAccumulator, list});
		}

		/**
		 * Name binding for function: mapAccumulateLeft.
		 * @see #mapAccumulateLeft(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName mapAccumulateLeft = 
			QualifiedName.make(CAL_List.MODULE_NAME, "mapAccumulateLeft");

		/**
		 * This is the strict version of <code>Cal.Collections.List.mapAccumulateLeft</code>. It is used for efficiency reasons in certain situations.
		 * If <code>accumulatorFunction</code> is strict in both of its arguments it is usually better to use <code>mapAccumulateLeftStrict</code>
		 * instead of <code>Cal.Collections.List.mapAccumulateLeft</code> since using <code>Cal.Collections.List.mapAccumulateLeft</code> will cause significantly more memory to be used than using
		 * <code>foldLeftStrict</code>.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.List.mapAccumulateLeft, Cal.Collections.List.accumulateLeftStrict, Cal.Collections.List.foldLeftStrict
		 * </dl>
		 * 
		 * @param accumulatorFunction (CAL type: <code>a -> b -> (a, c)</code>)
		 *          the processing function.
		 * @param initialAccumulator (CAL type: <code>a</code>)
		 *          the initial value of the accumulator.
		 * @param list (CAL type: <code>[b]</code>)
		 *          the list of values over which the accumulation is performed.
		 * @return (CAL type: <code>(a, [c])</code>) 
		 */
		public static final SourceModel.Expr mapAccumulateLeftStrict(SourceModel.Expr accumulatorFunction, SourceModel.Expr initialAccumulator, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.mapAccumulateLeftStrict), accumulatorFunction, initialAccumulator, list});
		}

		/**
		 * Name binding for function: mapAccumulateLeftStrict.
		 * @see #mapAccumulateLeftStrict(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName mapAccumulateLeftStrict = 
			QualifiedName.make(CAL_List.MODULE_NAME, "mapAccumulateLeftStrict");

		/**
		 * This function behaves like a combination of <code>Cal.Collections.List.map</code> and <code>Cal.Collections.List.foldRight</code>. It applies a
		 * function to each element of a list, passing an accumulating parameter from
		 * right to left, and returning a final value of this accumulator together with
		 * the new list.
		 * <p>
		 * This is analogous to mapAccumulateLeft except that the accumulation starts at the
		 * right of the list and works towards the first element.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.List.mapAccumulateLeft, Cal.Collections.List.map, Cal.Collections.List.foldRight, Cal.Collections.List.accumulateRight
		 * </dl>
		 * 
		 * @param accumulatorFunction (CAL type: <code>a -> b -> (a, c)</code>)
		 *          the processing function.
		 * @param initialAccumulator (CAL type: <code>a</code>)
		 *          the initial value of the accumulator.
		 * @param list (CAL type: <code>[b]</code>)
		 *          the list of values over which the accumulation is performed.
		 * @return (CAL type: <code>(a, [c])</code>) 
		 */
		public static final SourceModel.Expr mapAccumulateRight(SourceModel.Expr accumulatorFunction, SourceModel.Expr initialAccumulator, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.mapAccumulateRight), accumulatorFunction, initialAccumulator, list});
		}

		/**
		 * Name binding for function: mapAccumulateRight.
		 * @see #mapAccumulateRight(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName mapAccumulateRight = 
			QualifiedName.make(CAL_List.MODULE_NAME, "mapAccumulateRight");

		/**
		 * Applies the function <code>mapFunction</code> to each <code>#1</code> field in the list or records and returns
		 * the resulting list.
		 * @param mapFunction (CAL type: <code>a -> b</code>)
		 *          a function to be applied to the <code>#1</code> field for each record in the list
		 * @param list (CAL type: <code>r\#1 => [{r | #1 :: a}]</code>)
		 *          the list of records
		 * @return (CAL type: <code>r\#1 => [{r | #1 :: b}]</code>) 
		 *          the list obtained by applying mapFunction to the <code>#1</code> field for each record in the list
		 */
		public static final SourceModel.Expr mapField1(SourceModel.Expr mapFunction, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.mapField1), mapFunction, list});
		}

		/**
		 * Name binding for function: mapField1.
		 * @see #mapField1(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName mapField1 = 
			QualifiedName.make(CAL_List.MODULE_NAME, "mapField1");

		/**
		 * Applies the function <code>mapFunction</code> to each <code>#2</code> field in the list or records and returns
		 * the resulting list.
		 * @param mapFunction (CAL type: <code>a -> b</code>)
		 *          a function to be applied to the <code>#2</code> field for each record in the list
		 * @param list (CAL type: <code>r\#2 => [{r | #2 :: a}]</code>)
		 *          the list of records
		 * @return (CAL type: <code>r\#2 => [{r | #2 :: b}]</code>) 
		 *          the list obtained by applying mapFunction to the <code>#2</code> field for each record in the list
		 */
		public static final SourceModel.Expr mapField2(SourceModel.Expr mapFunction, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.mapField2), mapFunction, list});
		}

		/**
		 * Name binding for function: mapField2.
		 * @see #mapField2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName mapField2 = 
			QualifiedName.make(CAL_List.MODULE_NAME, "mapField2");

		/**
		 * <code>mapIndexed mapFunction list</code> applies the function <code>mapFunction</code> to each element of the list, where
		 * <code>mapFunction</code> is passed both the element value, and its zero-based index in the list.
		 * @param mapFunction (CAL type: <code>a -> Cal.Core.Prelude.Int -> b</code>)
		 *          a function to be applied to each element of the list, being passed both the element value, and its
		 * zero-based index in the list.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list.
		 * @return (CAL type: <code>[b]</code>) 
		 *          the list obtained by applying <code>mapFunction</code> to each element of the list.
		 */
		public static final SourceModel.Expr mapIndexed(SourceModel.Expr mapFunction, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.mapIndexed), mapFunction, list});
		}

		/**
		 * Name binding for function: mapIndexed.
		 * @see #mapIndexed(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName mapIndexed = 
			QualifiedName.make(CAL_List.MODULE_NAME, "mapIndexed");

		/**
		 * <code>mapJust mapFunction list</code> applies <code>mapFunction</code> to each element of list, discarding the <code>Cal.Core.Prelude.Nothing</code>
		 * values, and keeping the <em>Just</em> values after removing the <code>Cal.Core.Prelude.Just</code> constructor.
		 * @param mapFunction (CAL type: <code>a -> Cal.Core.Prelude.Maybe b</code>)
		 *          the mapping function.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list whose elements are to be mapped.
		 * @return (CAL type: <code>[b]</code>) 
		 *          the list of mapped values.
		 */
		public static final SourceModel.Expr mapJust(SourceModel.Expr mapFunction, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.mapJust), mapFunction, list});
		}

		/**
		 * Name binding for function: mapJust.
		 * @see #mapJust(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName mapJust = 
			QualifiedName.make(CAL_List.MODULE_NAME, "mapJust");

		/**
		 * Returns the maximum value in the specified list.
		 * Terminates in an error on an empty list.
		 * @param list (CAL type: <code>Cal.Core.Prelude.Ord a => [a]</code>)
		 *          the list whose maximum value is to be returned.
		 * @return (CAL type: <code>Cal.Core.Prelude.Ord a => a</code>) 
		 *          the maximum value in the list.
		 */
		public static final SourceModel.Expr maximum(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.maximum), list});
		}

		/**
		 * Name binding for function: maximum.
		 * @see #maximum(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName maximum = 
			QualifiedName.make(CAL_List.MODULE_NAME, "maximum");

		/**
		 * This function takes a comparison function and a list and returns the greatest
		 * element of the list by the comparison function. The list must be finite and
		 * non-empty.
		 * @param comparisonFunction (CAL type: <code>a -> a -> Cal.Core.Prelude.Ordering</code>)
		 *          the comparison function.
		 * @param list (CAL type: <code>[a]</code>)
		 *          a finite and non-empty list.
		 * @return (CAL type: <code>a</code>) 
		 *          the greatest element of the list by the comparison function.
		 */
		public static final SourceModel.Expr maximumBy(SourceModel.Expr comparisonFunction, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.maximumBy), comparisonFunction, list});
		}

		/**
		 * Name binding for function: maximumBy.
		 * @see #maximumBy(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName maximumBy = 
			QualifiedName.make(CAL_List.MODULE_NAME, "maximumBy");

		/**
		 * Assuming that <code>list1</code> and <code>list2</code> are sorted, <code>merge list1 list2</code> will merge all
		 * the elements of the two lists into a single sorted list. If an element of <code>list1</code> is equal
		 * to an element of <code>list2</code>, then the element of <code>list1</code> will be before the element of <code>list2</code>
		 * in the merged result.
		 * @param list1 (CAL type: <code>Cal.Core.Prelude.Ord a => [a]</code>)
		 *          assumed to be sorted
		 * @param list2 (CAL type: <code>Cal.Core.Prelude.Ord a => [a]</code>)
		 *          assumed to be sorted
		 * @return (CAL type: <code>Cal.Core.Prelude.Ord a => [a]</code>) 
		 *          the result of merging <code>list1</code> and <code>list2</code>. Will be a sorted list.
		 */
		public static final SourceModel.Expr merge(SourceModel.Expr list1, SourceModel.Expr list2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.merge), list1, list2});
		}

		/**
		 * Name binding for function: merge.
		 * @see #merge(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName merge = 
			QualifiedName.make(CAL_List.MODULE_NAME, "merge");

		/**
		 * Assuming that <code>list1</code> and <code>list2</code> are sorted according to <code>comparator</code>,
		 * <code>mergeBy comparator list1 list2</code> will merge all the elements of the two lists into
		 * a single list sorted according to <code>comparator</code>. If an element of <code>list1</code> is equal
		 * to an element of <code>list2</code> under the comparator, then the element of <code>list1</code> will
		 * be before the element of <code>list2</code> in the merged result.
		 * @param comparator (CAL type: <code>a -> a -> Cal.Core.Prelude.Ordering</code>)
		 *          comparison function to define the order of elements.
		 * @param list1 (CAL type: <code>[a]</code>)
		 *          assumed to be sorted according to <code>comparator</code>
		 * @param list2 (CAL type: <code>[a]</code>)
		 *          assumed to be sorted according to <code>comparator</code>
		 * @return (CAL type: <code>[a]</code>) 
		 *          the result of merging <code>list1</code> and <code>list2</code>. Will be sorted according to <code>comparator</code>.
		 */
		public static final SourceModel.Expr mergeBy(SourceModel.Expr comparator, SourceModel.Expr list1, SourceModel.Expr list2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.mergeBy), comparator, list1, list2});
		}

		/**
		 * Name binding for function: mergeBy.
		 * @see #mergeBy(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName mergeBy = 
			QualifiedName.make(CAL_List.MODULE_NAME, "mergeBy");

		/**
		 * Returns the minimum value in the specified list.
		 * Terminates in an error on an empty list.
		 * @param list (CAL type: <code>Cal.Core.Prelude.Ord a => [a]</code>)
		 *          the list whose minimum value is to be returned.
		 * @return (CAL type: <code>Cal.Core.Prelude.Ord a => a</code>) 
		 *          the minimum value in the list.
		 */
		public static final SourceModel.Expr minimum(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.minimum), list});
		}

		/**
		 * Name binding for function: minimum.
		 * @see #minimum(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName minimum = 
			QualifiedName.make(CAL_List.MODULE_NAME, "minimum");

		/**
		 * This function takes a comparison function and a list and returns the least
		 * element of the list by the comparison function. The list must be finite and
		 * non-empty.
		 * @param comparisonFunction (CAL type: <code>a -> a -> Cal.Core.Prelude.Ordering</code>)
		 *          the comparison function.
		 * @param list (CAL type: <code>[a]</code>)
		 *          a finite and non-empty list.
		 * @return (CAL type: <code>a</code>) 
		 *          the least element of the list by the comparison function.
		 */
		public static final SourceModel.Expr minimumBy(SourceModel.Expr comparisonFunction, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.minimumBy), comparisonFunction, list});
		}

		/**
		 * Name binding for function: minimumBy.
		 * @see #minimumBy(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName minimumBy = 
			QualifiedName.make(CAL_List.MODULE_NAME, "minimumBy");

		/**
		 * <code>orList list</code> returns <code>Cal.Core.Prelude.True</code> if at least one element of the list is <code>Cal.Core.Prelude.True</code>.
		 * @param list (CAL type: <code>[Cal.Core.Prelude.Boolean]</code>)
		 *          the list whose elements are to be tested.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if at least one element of the list is <code>Cal.Core.Prelude.True</code>.
		 */
		public static final SourceModel.Expr orList(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.orList), list});
		}

		/**
		 * Name binding for function: orList.
		 * @see #orList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName orList = 
			QualifiedName.make(CAL_List.MODULE_NAME, "orList");

		/**
		 * Converts a CAL list to a Java list.
		 * @param list (CAL type: <code>Cal.Core.Prelude.Outputable a => [a]</code>)
		 *          the CAL list.
		 * @return (CAL type: <code>Cal.Core.Prelude.JList</code>) 
		 *          the corresponding Java list.
		 */
		public static final SourceModel.Expr outputList(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputList), list});
		}

		/**
		 * Name binding for function: outputList.
		 * @see #outputList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName outputList = 
			QualifiedName.make(CAL_List.MODULE_NAME, "outputList");

		/**
		 * Converts a CAL list to a Java list using the element mapping function <code>f</code> of type <code>a -&gt; Cal.Core.Prelude.JObject</code> 
		 * to convert elements of the CAL list.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the CAL list.
		 * @param f (CAL type: <code>a -> Cal.Core.Prelude.JObject</code>)
		 *          the mapping function converting elements of the list to <code>Cal.Core.Prelude.JObject</code> values.
		 * @return (CAL type: <code>Cal.Core.Prelude.JList</code>) 
		 *          the corresponding JList.
		 */
		public static final SourceModel.Expr outputListWith(SourceModel.Expr list, SourceModel.Expr f) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputListWith), list, f});
		}

		/**
		 * Name binding for function: outputListWith.
		 * @see #outputListWith(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName outputListWith = 
			QualifiedName.make(CAL_List.MODULE_NAME, "outputListWith");

		/**
		 * This function takes a predicate and a list and returns the pair of lists of
		 * elements which respectively do and do not satisfy the predicate.
		 * <p>
		 * e.g. <code>partition Cal.Core.Prelude.isEven [3, 1, 4, 1, 5, 9, 2, 6] = ([4, 2, 6], [3, 1, 1, 5, 9])</code>
		 * 
		 * @param predicate (CAL type: <code>a -> Cal.Core.Prelude.Boolean</code>)
		 *          a predicate to be applied to the elements of the list.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list to be partitioned.
		 * @return (CAL type: <code>([a], [a])</code>) 
		 *          a pair of lists of elements which respectively do and do not satisfy the predicate.
		 */
		public static final SourceModel.Expr partition(SourceModel.Expr predicate, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.partition), predicate, list});
		}

		/**
		 * Name binding for function: partition.
		 * @see #partition(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName partition = 
			QualifiedName.make(CAL_List.MODULE_NAME, "partition");

		/**
		 * Periodizes a list at the specified frequency, returning a list of lists, one for each modulo of the frequency.
		 * @param frequency (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the frequency.
		 * @param listToPeriodize (CAL type: <code>[a]</code>)
		 *          the list to be periodized.
		 * @return (CAL type: <code>[[a]]</code>) 
		 *          a list of lists, one for each modulo of the frequency.
		 */
		public static final SourceModel.Expr periodize(SourceModel.Expr frequency, SourceModel.Expr listToPeriodize) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.periodize), frequency, listToPeriodize});
		}

		/**
		 * @see #periodize(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param frequency
		 * @param listToPeriodize
		 * @return the SourceModel.Expr representing an application of periodize
		 */
		public static final SourceModel.Expr periodize(int frequency, SourceModel.Expr listToPeriodize) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.periodize), SourceModel.Expr.makeIntValue(frequency), listToPeriodize});
		}

		/**
		 * Name binding for function: periodize.
		 * @see #periodize(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName periodize = 
			QualifiedName.make(CAL_List.MODULE_NAME, "periodize");

		/**
		 * <code>product list</code> return the product of all the elements of <code>list</code>, multiplying from left to right. product
		 * applied to an empty list is 1.
		 * @param list (CAL type: <code>Cal.Core.Prelude.Num a => [a]</code>)
		 *          the list whose elements are to be multiplied.
		 * @return (CAL type: <code>Cal.Core.Prelude.Num a => a</code>) 
		 *          the product of all the elements of list, multiplying from left to right, or 1 if the list is empty.
		 */
		public static final SourceModel.Expr product(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.product), list});
		}

		/**
		 * Name binding for function: product.
		 * @see #product(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName product = 
			QualifiedName.make(CAL_List.MODULE_NAME, "product");

		/**
		 * <code>removeDuplicates list</code> returns the sublist of list with duplicates removed. Ordering is preserved.
		 * <p>
		 * e.g. <code>removeDuplicates [3, 3, 1, 1, 3, 4, 1] = [3, 1, 4]</code>
		 * 
		 * @param list (CAL type: <code>Cal.Core.Prelude.Eq a => [a]</code>)
		 *          the list to remove duplicates from.
		 * @return (CAL type: <code>Cal.Core.Prelude.Eq a => [a]</code>) 
		 *          the sublist of list with duplicates removed.
		 */
		public static final SourceModel.Expr removeDuplicates(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.removeDuplicates), list});
		}

		/**
		 * Name binding for function: removeDuplicates.
		 * @see #removeDuplicates(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName removeDuplicates = 
			QualifiedName.make(CAL_List.MODULE_NAME, "removeDuplicates");

		/**
		 * <code>removeDuplicatesBy eq list</code> returns the sublist of list with duplicates (as determined by <code>eq</code>) removed.
		 * Ordering is preserved.
		 * <p>
		 * e.g. <code>removeDuplicatesBy Cal.Core.Prelude.equals [3, 3, 1, 1, 3, 4, 1] = [3, 1, 4]</code>
		 * 
		 * @param eq (CAL type: <code>a -> a -> Cal.Core.Prelude.Boolean</code>)
		 *          the equality comparison function to use in comparing the list elements.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list to be rid of duplicates.
		 * @return (CAL type: <code>[a]</code>) 
		 *          the sublist of list with duplicates (as determined by <code>eq</code>) removed.
		 */
		public static final SourceModel.Expr removeDuplicatesBy(SourceModel.Expr eq, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.removeDuplicatesBy), eq, list});
		}

		/**
		 * Name binding for function: removeDuplicatesBy.
		 * @see #removeDuplicatesBy(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName removeDuplicatesBy = 
			QualifiedName.make(CAL_List.MODULE_NAME, "removeDuplicatesBy");

		/**
		 * <code>repeat valueToRepeat</code> returns the infinite list <code>[valueToRepeat, valueToRepeat, valueToRepeat, ...]</code>.
		 * @param valueToRepeat (CAL type: <code>a</code>)
		 *          the value to be repeated.
		 * @return (CAL type: <code>[a]</code>) 
		 *          the infinite list <code>[valueToRepeat, valueToRepeat, valueToRepeat, ...]</code>
		 */
		public static final SourceModel.Expr repeat(SourceModel.Expr valueToRepeat) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.repeat), valueToRepeat});
		}

		/**
		 * Name binding for function: repeat.
		 * @see #repeat(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName repeat = 
			QualifiedName.make(CAL_List.MODULE_NAME, "repeat");

		/**
		 * Applies the predicate to each element and returns the list 
		 * where every element for which the predicate evaluated to <code>Cal.Core.Prelude.True</code> is replaced by <code>replaceWithValue</code>.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list whose elements are to be checked and replaced.
		 * @param predicate (CAL type: <code>a -> Cal.Core.Prelude.Boolean</code>)
		 *          a predicate to be applied to the elements of the list.
		 * @param replaceWithValue (CAL type: <code>a</code>)
		 *          the value to replace existing elements in the list.
		 * @return (CAL type: <code>[a]</code>) 
		 *          a copy of the list where every element for which the predicate evaluated to <code>Cal.Core.Prelude.True</code> is replaced by
		 * <code>replaceWithValue</code>.
		 */
		public static final SourceModel.Expr replaceAll(SourceModel.Expr list, SourceModel.Expr predicate, SourceModel.Expr replaceWithValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.replaceAll), list, predicate, replaceWithValue});
		}

		/**
		 * Name binding for function: replaceAll.
		 * @see #replaceAll(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName replaceAll = 
			QualifiedName.make(CAL_List.MODULE_NAME, "replaceAll");

		/**
		 * Returns the list with the value at a specified offset replaced by a specified value.
		 * Specifying a negative offset, or an offset larger than the last element of the list 
		 * causes a runtime error to be signalled.
		 * @param index (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          The index of the element to replace
		 * @param value (CAL type: <code>a</code>)
		 *          The value to replace the element with
		 * @param list (CAL type: <code>[a]</code>)
		 *          The list to replace an element of
		 * @return (CAL type: <code>[a]</code>) 
		 *          a copy of <code>list</code> with value at <code>index</code> instead of the original value.
		 */
		public static final SourceModel.Expr replaceAt(SourceModel.Expr index, SourceModel.Expr value, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.replaceAt), index, value, list});
		}

		/**
		 * @see #replaceAt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param index
		 * @param value
		 * @param list
		 * @return the SourceModel.Expr representing an application of replaceAt
		 */
		public static final SourceModel.Expr replaceAt(int index, SourceModel.Expr value, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.replaceAt), SourceModel.Expr.makeIntValue(index), value, list});
		}

		/**
		 * Name binding for function: replaceAt.
		 * @see #replaceAt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName replaceAt = 
			QualifiedName.make(CAL_List.MODULE_NAME, "replaceAt");

		/**
		 * <code>replicate nCopies valueToReplicate</code> is a list of length <code>nCopies</code>, with every element equal to
		 * <code>valueToReplicate</code>.
		 * @param nCopies (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the number of copies.
		 * @param valueToReplicate (CAL type: <code>a</code>)
		 *          the value to be replicated.
		 * @return (CAL type: <code>[a]</code>) 
		 *          a list of length <code>nCopies</code>, with every element equal to <code>valueToReplicate</code>.
		 */
		public static final SourceModel.Expr replicate(SourceModel.Expr nCopies, SourceModel.Expr valueToReplicate) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.replicate), nCopies, valueToReplicate});
		}

		/**
		 * @see #replicate(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param nCopies
		 * @param valueToReplicate
		 * @return the SourceModel.Expr representing an application of replicate
		 */
		public static final SourceModel.Expr replicate(int nCopies, SourceModel.Expr valueToReplicate) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.replicate), SourceModel.Expr.makeIntValue(nCopies), valueToReplicate});
		}

		/**
		 * Name binding for function: replicate.
		 * @see #replicate(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName replicate = 
			QualifiedName.make(CAL_List.MODULE_NAME, "replicate");

		/**
		 * Replicates a list for a specified number of times.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list.
		 * @param nCopies (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the number of copies to make.
		 * @return (CAL type: <code>[a]</code>) 
		 *          a list of <code>nCopies</code> of the list concatenated together.
		 */
		public static final SourceModel.Expr replicateList(SourceModel.Expr list, SourceModel.Expr nCopies) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.replicateList), list, nCopies});
		}

		/**
		 * @see #replicateList(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param list
		 * @param nCopies
		 * @return the SourceModel.Expr representing an application of replicateList
		 */
		public static final SourceModel.Expr replicateList(SourceModel.Expr list, int nCopies) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.replicateList), list, SourceModel.Expr.makeIntValue(nCopies)});
		}

		/**
		 * Name binding for function: replicateList.
		 * @see #replicateList(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName replicateList = 
			QualifiedName.make(CAL_List.MODULE_NAME, "replicateList");

		/**
		 * Reverses the elements of a list. reverse is O(n) time where n is the length of the list.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list to be reversed.
		 * @return (CAL type: <code>[a]</code>) 
		 *          a list containing the elements in the specified list, in reverse order.
		 */
		public static final SourceModel.Expr reverse(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.reverse), list});
		}

		/**
		 * Name binding for function: reverse.
		 * @see #reverse(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName reverse = 
			QualifiedName.make(CAL_List.MODULE_NAME, "reverse");

		/**
		 * Returns a <code>Cal.Core.Prelude.List</code> with only the <code>Cal.Core.Prelude.Right</code> values of the <code>Cal.Core.Prelude.List</code> of 
		 * <code>Cal.Core.Prelude.Either</code>s passed in.
		 * @param list (CAL type: <code>[Cal.Core.Prelude.Either a b]</code>)
		 *          the <code>Cal.Core.Prelude.List</code> of <code>Cal.Core.Prelude.Either</code>s
		 * @return (CAL type: <code>[b]</code>) 
		 *          the <code>Cal.Core.Prelude.List</code> obtained by taking the <code>Cal.Core.Prelude.Right</code> value of each of the 
		 * <code>Cal.Core.Prelude.Either</code>s in the <code>Cal.Core.Prelude.List</code>
		 */
		public static final SourceModel.Expr rightValues(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.rightValues), list});
		}

		/**
		 * Name binding for function: rightValues.
		 * @see #rightValues(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName rightValues = 
			QualifiedName.make(CAL_List.MODULE_NAME, "rightValues");

		/**
		 * Samples a list at the specified frequency. For example, if the frequency is 3, it will take every
		 * third element from the list (starting with the first element).
		 * @param frequency (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the sampling frequency.
		 * @param listToSample (CAL type: <code>[a]</code>)
		 *          the list to be sampled.
		 * @return (CAL type: <code>[a]</code>) 
		 *          a list of the samples.
		 */
		public static final SourceModel.Expr sample(SourceModel.Expr frequency, SourceModel.Expr listToSample) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.sample), frequency, listToSample});
		}

		/**
		 * @see #sample(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param frequency
		 * @param listToSample
		 * @return the SourceModel.Expr representing an application of sample
		 */
		public static final SourceModel.Expr sample(int frequency, SourceModel.Expr listToSample) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.sample), SourceModel.Expr.makeIntValue(frequency), listToSample});
		}

		/**
		 * Name binding for function: sample.
		 * @see #sample(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName sample = 
			QualifiedName.make(CAL_List.MODULE_NAME, "sample");

		/**
		 * <code>seqList</code> simply returns its input list. However, if this returned list is subsequently traversed by other clients,
		 * each element of the list is evaluated to weak-head normal form prior to visiting the next element.
		 * <p>
		 * <code>seqList</code> is a useful technique for controlling the space requirements of recursively defined lists in that it ensures
		 * that earlier list elements will be evaluated prior to proceeding to the next element, even if that is not required by lazy
		 * evaluation order. This can be useful if you are using a function like <code>Cal.Collections.List.subscript</code> which traverses without
		 * evaluating the elements of a list to get the particular subscripted element.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Prelude.seq, Cal.Collections.List.strictList
		 * </dl>
		 * 
		 * @param list (CAL type: <code>[a]</code>)
		 * @return (CAL type: <code>[a]</code>) 
		 *          the input list, but with each element guaranteed to be evaluated to weak-head-normal form if it is visited.
		 */
		public static final SourceModel.Expr seqList(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.seqList), list});
		}

		/**
		 * Name binding for function: seqList.
		 * @see #seqList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName seqList = 
			QualifiedName.make(CAL_List.MODULE_NAME, "seqList");

		/**
		 * Sorts the specified list. All order comparisons are done using the
		 * <code>Cal.Core.Prelude.compare</code> class method.
		 * <p>
		 * This function implements a stable sort in that items which evaluate to <code>Cal.Core.Prelude.EQ</code>
		 * under the <code>comparisonFunction</code> preserve their original ordering in the original
		 * list.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.List.sortBy
		 * </dl>
		 * 
		 * @param list (CAL type: <code>Cal.Core.Prelude.Ord a => [a]</code>)
		 *          the list to be sorted.
		 * @return (CAL type: <code>Cal.Core.Prelude.Ord a => [a]</code>) 
		 *          the sorted list.
		 */
		public static final SourceModel.Expr sort(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.sort), list});
		}

		/**
		 * Name binding for function: sort.
		 * @see #sort(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName sort = 
			QualifiedName.make(CAL_List.MODULE_NAME, "sort");

		/**
		 * Sorts the specified list according to the specified comparison function. All
		 * order comparisons are done using the supplied comparison function.
		 * <p>
		 * This function implements a stable sort in that items which evaluate to <code>Cal.Core.Prelude.EQ</code>
		 * under the comparisonFunction preserve their original ordering in the original
		 * list.
		 * 
		 * @param comparisonFunction (CAL type: <code>a -> a -> Cal.Core.Prelude.Ordering</code>)
		 *          the comparison function to use in determining the
		 * order of the elements.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list to be sorted.
		 * @return (CAL type: <code>[a]</code>) 
		 *          the sorted list.
		 */
		public static final SourceModel.Expr sortBy(SourceModel.Expr comparisonFunction, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.sortBy), comparisonFunction, list});
		}

		/**
		 * Name binding for function: sortBy.
		 * @see #sortBy(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName sortBy = 
			QualifiedName.make(CAL_List.MODULE_NAME, "sortBy");

		/**
		 * <code>sortByExternal</code> is an alternative to <code>Cal.Collections.List.sortBy</code>.
		 * It is a stable sort (does not reorder equivalent elements).
		 * <p>
		 * Its functionality is the same (in most cases) to <code>Cal.Collections.List.sortBy</code> but it does its sorting by delegating to Java's sorting mechanism. 
		 * It can be significantly faster when making full use of the resulting sorted list. However, it can be slower when taking only the
		 * first few elements of the resulting list, when the superior laziness of <code>Cal.Collections.List.sort</code> comes into play.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.List.sortBy
		 * </dl>
		 * 
		 * @param cmp (CAL type: <code>a -> a -> Cal.Core.Prelude.Ordering</code>)
		 *          the ordeirng comparison function to be used in the sort.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list to be sorted.
		 * @return (CAL type: <code>[a]</code>) 
		 *          the list, sorted.
		 */
		public static final SourceModel.Expr sortByExternal(SourceModel.Expr cmp, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.sortByExternal), cmp, list});
		}

		/**
		 * Name binding for function: sortByExternal.
		 * @see #sortByExternal(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName sortByExternal = 
			QualifiedName.make(CAL_List.MODULE_NAME, "sortByExternal");

		/**
		 * <code>sortExternal</code> is an alternative to <code>Cal.Collections.List.sort</code>. It is a stable sort (does not reorder equivalent elements).
		 * <p>
		 * Its functionality is the same (in most cases) to <code>Cal.Collections.List.sort</code> but it does its sorting by delegating to Java's sorting mechanism. 
		 * It can be significantly faster when making full use of the resulting sorted list. However, it can be slower when taking only the
		 * first few elements of the resulting list, when the superior laziness of <code>Cal.Collections.List.sort</code> comes into play.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.List.sort
		 * </dl>
		 * 
		 * @param list (CAL type: <code>Cal.Core.Prelude.Ord a => [a]</code>)
		 *          the list to be sorted.
		 * @return (CAL type: <code>Cal.Core.Prelude.Ord a => [a]</code>) 
		 *          the list, sorted.
		 */
		public static final SourceModel.Expr sortExternal(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.sortExternal), list});
		}

		/**
		 * Name binding for function: sortExternal.
		 * @see #sortExternal(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName sortExternal = 
			QualifiedName.make(CAL_List.MODULE_NAME, "sortExternal");

		/**
		 * <code>span predicate list</code> breaks up <code>list</code> into a pair of lists. The start of the second list is
		 * the first element of list on which predicate is False.
		 * <p>
		 * e.g. 
		 * <ul>
		 *  <li>
		 *   <code>span Cal.Core.Prelude.isEven [1, 2] = ([], [1, 2])</code>
		 *  </li>
		 *  <li>
		 *   <code>span Cal.Core.Prelude.isEven [4, 2, 1, 6] = ([4, 2], [1, 6])</code>
		 *  </li>
		 * </ul>
		 * 
		 * @param predicate (CAL type: <code>a -> Cal.Core.Prelude.Boolean</code>)
		 *          a predicate to be applied to the elements of the list.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list to be broken up into a pair of lists.
		 * @return (CAL type: <code>([a], [a])</code>) 
		 *          a pair of lists, where the first list is the longest prefix of the list for which predicate
		 * is <code>Cal.Core.Prelude.True</code> for each element, and the second list contains the remaining elements of the original list.
		 */
		public static final SourceModel.Expr span(SourceModel.Expr predicate, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.span), predicate, list});
		}

		/**
		 * Name binding for function: span.
		 * @see #span(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName span = 
			QualifiedName.make(CAL_List.MODULE_NAME, "span");

		/**
		 * <code>spanInclusive predicate list</code> breaks up list into a pair of lists. The last item in the first list is the first element
		 * of list on which predicate is <code>Cal.Core.Prelude.False</code>.
		 * <p>
		 * e.g.
		 * <ul>
		 *  <li>
		 *   <code>spanInclusive Cal.Core.Prelude.isEven [1, 2] = ([1], [2])</code>
		 *  </li>
		 *  <li>
		 *   <code>spanInclusive Cal.Core.Prelude.isEven [4, 2, 1, 6] = ([4, 2, 1], [6])</code>
		 *  </li>
		 * </ul>
		 * 
		 * @param predicate (CAL type: <code>a -> Cal.Core.Prelude.Boolean</code>)
		 *          a predicate to be applied to the elements of the list.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list to be split.
		 * @return (CAL type: <code>([a], [a])</code>) 
		 *          a pair of lists. The last item in the first list is the first element of list
		 * on which predicate is <code>Cal.Core.Prelude.False</code>. The second list contains the remaining
		 * elements of the original list.
		 */
		public static final SourceModel.Expr spanInclusive(SourceModel.Expr predicate, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.spanInclusive), predicate, list});
		}

		/**
		 * Name binding for function: spanInclusive.
		 * @see #spanInclusive(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName spanInclusive = 
			QualifiedName.make(CAL_List.MODULE_NAME, "spanInclusive");

		/**
		 * <code>split nElemsToSplit list</code> returns a pair of lists. The first consists of the first <code>nElemsToSplit</code>
		 * elements of list, the second consists of the remaining elements.
		 * @param nElemsToSplit (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the number of elements to be returned in the first list.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list to be split.
		 * @return (CAL type: <code>([a], [a])</code>) 
		 *          a pair of lists. The first consists of the first <code>nElemsToSplit</code> elements of list,
		 * the second consists of the remaining elements.
		 */
		public static final SourceModel.Expr splitAt(SourceModel.Expr nElemsToSplit, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.splitAt), nElemsToSplit, list});
		}

		/**
		 * @see #splitAt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param nElemsToSplit
		 * @param list
		 * @return the SourceModel.Expr representing an application of splitAt
		 */
		public static final SourceModel.Expr splitAt(int nElemsToSplit, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.splitAt), SourceModel.Expr.makeIntValue(nElemsToSplit), list});
		}

		/**
		 * Name binding for function: splitAt.
		 * @see #splitAt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName splitAt = 
			QualifiedName.make(CAL_List.MODULE_NAME, "splitAt");

		/**
		 * Splits a <code>Cal.Core.Prelude.List</code> of <code>Cal.Core.Prelude.Either</code>s into a pair of <code>Cal.Core.Prelude.List</code>s with the 
		 * first item being the <code>Cal.Core.Prelude.Left</code> values and the second the <code>Cal.Core.Prelude.Right</code> values.
		 * @param list (CAL type: <code>[Cal.Core.Prelude.Either a b]</code>)
		 *          the list of eithers
		 * @return (CAL type: <code>([a], [b])</code>) 
		 *          a pair of <code>Cal.Core.Prelude.List</code>s, the first <code>Cal.Core.Prelude.List</code> contains the <code>Cal.Core.Prelude.Left</code> values and the second is 
		 * the <code>Cal.Core.Prelude.List</code> of <code>Cal.Core.Prelude.Right</code> values.
		 */
		public static final SourceModel.Expr splitEither(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.splitEither), list});
		}

		/**
		 * Name binding for function: splitEither.
		 * @see #splitEither(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName splitEither = 
			QualifiedName.make(CAL_List.MODULE_NAME, "splitEither");

		/**
		 * Returns whether <code>listToTest</code> starts with the specified prefix.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.List.startsWithBy
		 * </dl>
		 * 
		 * @param prefix (CAL type: <code>Cal.Core.Prelude.Eq a => [a]</code>)
		 * @param listToTest (CAL type: <code>Cal.Core.Prelude.Eq a => [a]</code>)
		 *          the list to be tested for starting with <code>prefix</code>.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if <code>listToTest</code> starts with the list <code>prefix</code>.
		 */
		public static final SourceModel.Expr startsWith(SourceModel.Expr prefix, SourceModel.Expr listToTest) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.startsWith), prefix, listToTest});
		}

		/**
		 * Name binding for function: startsWith.
		 * @see #startsWith(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName startsWith = 
			QualifiedName.make(CAL_List.MODULE_NAME, "startsWith");

		/**
		 * Returns whether <code>listToTest</code> starts with the specified prefix using <code>eq</code> as the notion of equality between 
		 * elements of the prefix and elements of the listToTest.
		 * @param eq (CAL type: <code>a -> b -> Cal.Core.Prelude.Boolean</code>)
		 *          the equality comparison function to use in comparing the list elements for equality.
		 * @param prefix (CAL type: <code>[a]</code>)
		 * @param listToTest (CAL type: <code>[b]</code>)
		 *          the list to be tested for starting with <code>prefix</code>.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if <code>listToTest</code> starts with the list <code>prefix</code> using <code>eq</code> for comparing list
		 * elements for equality.
		 */
		public static final SourceModel.Expr startsWithBy(SourceModel.Expr eq, SourceModel.Expr prefix, SourceModel.Expr listToTest) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.startsWithBy), eq, prefix, listToTest});
		}

		/**
		 * Name binding for function: startsWithBy.
		 * @see #startsWithBy(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName startsWithBy = 
			QualifiedName.make(CAL_List.MODULE_NAME, "startsWithBy");

		/**
		 * Evaluates each element of the list (to weak-head normal form) prior to returning the list itself.
		 * <code>strictList list</code> is equivalent to <code>List.foldRight Prelude.seq list list</code>, however, it
		 * will be faster.
		 * <p>
		 * The difference between <code>Cal.Collections.List.seqList</code> and <code>strictList</code> is that <code>Cal.Collections.List.seqList</code> ensures that the
		 * elements of the list get evaluated in order as the list is traversed. However, <code>strictList</code> does more, it actually
		 * traverses the list prior to returning.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Prelude.seq, Cal.Collections.List.seqList
		 * </dl>
		 * 
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list to make strict
		 * @return (CAL type: <code>[a]</code>) 
		 *          the input list, however each element has been evaluated, in element order, prior to this function returning.
		 */
		public static final SourceModel.Expr strictList(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.strictList), list});
		}

		/**
		 * Name binding for function: strictList.
		 * @see #strictList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName strictList = 
			QualifiedName.make(CAL_List.MODULE_NAME, "strictList");

		/**
		 * Returns a portion of the argument list as a new list.
		 * <p>
		 * If <code>fromIndex &lt; 0</code>, <code>fromIndex &gt; toIndex</code>, or <code>toIndex &gt; length list</code>, the function
		 * terminates in an error.
		 * <p>
		 * If you want the list minus some number of leading elements (ie, if <code>toIndex == length list</code>,
		 * then you should use <code>Cal.Collections.List.drop</code> instead, since <code>Cal.Collections.List.subList</code> copies elements to
		 * create a new list, whereas <code>Cal.Collections.List.drop</code> does not.
		 * 
		 * @param list (CAL type: <code>[a]</code>)
		 *          to extract a sub-list from
		 * @param fromIndex (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          inclusive 0-based index.
		 * @param toIndex (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          exclusive 0-based index
		 * @return (CAL type: <code>[a]</code>) 
		 *          copy of the list from <code>fromIndex</code> (inclusive) to <code>toIndex</code> (exclusive).
		 */
		public static final SourceModel.Expr subList(SourceModel.Expr list, SourceModel.Expr fromIndex, SourceModel.Expr toIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.subList), list, fromIndex, toIndex});
		}

		/**
		 * @see #subList(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param list
		 * @param fromIndex
		 * @param toIndex
		 * @return the SourceModel.Expr representing an application of subList
		 */
		public static final SourceModel.Expr subList(SourceModel.Expr list, int fromIndex, int toIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.subList), list, SourceModel.Expr.makeIntValue(fromIndex), SourceModel.Expr.makeIntValue(toIndex)});
		}

		/**
		 * Name binding for function: subList.
		 * @see #subList(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName subList = 
			QualifiedName.make(CAL_List.MODULE_NAME, "subList");

		/**
		 * <code>subscript list index</code> returns the nth element of the list where n = <code>index</code>. The indices start at 0. If
		 * the index is negative or is greater than or equal to the length of the list, an error results.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list to be accessed.
		 * @param index (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the index of the list element to be returned.
		 * @return (CAL type: <code>a</code>) 
		 *          the element in the list at the position indicated by <code>index</code>.
		 */
		public static final SourceModel.Expr subscript(SourceModel.Expr list, SourceModel.Expr index) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.subscript), list, index});
		}

		/**
		 * @see #subscript(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param list
		 * @param index
		 * @return the SourceModel.Expr representing an application of subscript
		 */
		public static final SourceModel.Expr subscript(SourceModel.Expr list, int index) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.subscript), list, SourceModel.Expr.makeIntValue(index)});
		}

		/**
		 * Name binding for function: subscript.
		 * @see #subscript(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName subscript = 
			QualifiedName.make(CAL_List.MODULE_NAME, "subscript");

		/**
		 * <code>sum list</code> returns the sum of all the elements of <code>list</code>, adding from left to right. sum applied to an
		 * empty list is 0.
		 * @param list (CAL type: <code>Cal.Core.Prelude.Num a => [a]</code>)
		 *          the list whose elements are to be summed.
		 * @return (CAL type: <code>Cal.Core.Prelude.Num a => a</code>) 
		 *          the sum of all the elements of the list, adding from left to right, or 0 if the list is empty.
		 */
		public static final SourceModel.Expr sum(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.sum), list});
		}

		/**
		 * Name binding for function: sum.
		 * @see #sum(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName sum = 
			QualifiedName.make(CAL_List.MODULE_NAME, "sum");

		/**
		 * Returns a list of the elements after the first element of the list. Terminates in an error on an empty list.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list whose tail is to be returned.
		 * @return (CAL type: <code>[a]</code>) 
		 *          a list of the elements after the first element of the list.
		 */
		public static final SourceModel.Expr tail(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.tail), list});
		}

		/**
		 * Name binding for function: tail.
		 * @see #tail(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName tail = 
			QualifiedName.make(CAL_List.MODULE_NAME, "tail");

		/**
		 * Returns the list of all final segments of the specified list, longest first.
		 * <p>
		 * e.g. <code>tails [3, 1, 4] = [[3, 1, 4], [1, 4], [4], []]</code>
		 * 
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list whose final segments are to be returned.
		 * @return (CAL type: <code>[[a]]</code>) 
		 *          the list of all final segments of the argument, longest first.
		 */
		public static final SourceModel.Expr tails(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.tails), list});
		}

		/**
		 * Name binding for function: tails.
		 * @see #tails(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName tails = 
			QualifiedName.make(CAL_List.MODULE_NAME, "tails");

		/**
		 * <code>take nElements list</code> returns a list consisting of the first <code>nElements</code> elements of <code>list</code>.
		 * If the list has fewer than <code>nElements</code> elements, it just returns the list.
		 * @param nElements (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the number of elements to take.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list from which elements are to be taken.
		 * @return (CAL type: <code>[a]</code>) 
		 *          a list of the requested elements from the list.
		 */
		public static final SourceModel.Expr take(SourceModel.Expr nElements, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.take), nElements, list});
		}

		/**
		 * @see #take(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param nElements
		 * @param list
		 * @return the SourceModel.Expr representing an application of take
		 */
		public static final SourceModel.Expr take(int nElements, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.take), SourceModel.Expr.makeIntValue(nElements), list});
		}

		/**
		 * Name binding for function: take.
		 * @see #take(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName take = 
			QualifiedName.make(CAL_List.MODULE_NAME, "take");

		/**
		 * <code>takeWhile takeWhileTrueFunction list</code> returns the longest prefix of the list for which <code>takeWhileTrueFunction</code>
		 * is <code>Cal.Core.Prelude.True</code> for each element.
		 * <p>
		 * e.g. <code>takeWhile Cal.Core.Prelude.isEven [6, 2, 1, 2] = [6, 2]</code>
		 * 
		 * @param takeWhileTrueFunction (CAL type: <code>a -> Cal.Core.Prelude.Boolean</code>)
		 *          a predicate to be applied to the elements of the list.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list from which elements are to be taken.
		 * @return (CAL type: <code>[a]</code>) 
		 *          the longest prefix of the list for which <code>takeWhileTrueFunction</code> is True 
		 * for each element.
		 */
		public static final SourceModel.Expr takeWhile(SourceModel.Expr takeWhileTrueFunction, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.takeWhile), takeWhileTrueFunction, list});
		}

		/**
		 * Name binding for function: takeWhile.
		 * @see #takeWhile(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName takeWhile = 
			QualifiedName.make(CAL_List.MODULE_NAME, "takeWhile");

		/**
		 * Helper binding method for function: testModule. 
		 * @return the SourceModule.expr representing an application of testModule
		 */
		public static final SourceModel.Expr testModule() {
			return SourceModel.Expr.Var.make(Functions.testModule);
		}

		/**
		 * Name binding for function: testModule.
		 * @see #testModule()
		 */
		public static final QualifiedName testModule = 
			QualifiedName.make(CAL_List.MODULE_NAME, "testModule");

		/**
		 * Provides a view of a CAL list as a Java iterator. The CAL list can then be lazily traversed in Java
		 * client code using the method of java.util.Iterator.
		 * <p>
		 * The <code>Cal.Core.Prelude.output</code> class method on the <code>Cal.Core.Prelude.List</code> type and the <code>Cal.Collections.List.outputList</code> function output
		 * a java.util.List rather than an iterator. In particular, they will not terminate for infinite lists. 
		 * This function provides a mechanism whereby the Java client can control how much of the CAL list to explore.
		 * <p>
		 * Note this function is mainly intended to be used when interacting programmatically with Java client code.
		 * Within CAL itself, there is not much point in converting to a <code>Cal.Collections.List.JIterator</code>, which is a mutable foreign
		 * value type.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.List.toJIteratorWith, Cal.Collections.List.fromJIterator
		 * </dl>
		 * 
		 * @param list (CAL type: <code>Cal.Core.Prelude.Outputable a => [a]</code>)
		 *          the CAL list
		 * @return (CAL type: <code>Cal.Collections.List.JIterator</code>) 
		 *          a Java iterator on the CAL list, where the elements of the CAL List are converted to Java objects
		 * using the <code>Cal.Core.Prelude.output</code> function.
		 */
		public static final SourceModel.Expr toJIterator(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toJIterator), list});
		}

		/**
		 * Name binding for function: toJIterator.
		 * @see #toJIterator(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName toJIterator = 
			QualifiedName.make(CAL_List.MODULE_NAME, "toJIterator");

		/**
		 * Provides a view of a CAL list as a Java iterator. The CAL list can then be lazily traversed in Java
		 * client code using the method of java.util.Iterator.
		 * <p>
		 * The <code>Cal.Collections.List.outputListWith</code> function outputs a java.util.List rather than an iterator. In particular, it 
		 * will not terminate for infinite lists. This function provides a mechanism whereby the Java client can control
		 * how much of the CAL list to explore.
		 * <p>
		 * Note this function is mainly intended to be used when interacting programmatically with Java client code.
		 * Within CAL itself, there is not much point in converting to a <code>Cal.Collections.List.JIterator</code>, which is a mutable foreign
		 * value type.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.List.toJIterator, Cal.Collections.List.fromJIteratorWith
		 * </dl>
		 * 
		 * @param list (CAL type: <code>[a]</code>)
		 *          the CAL list
		 * @param f (CAL type: <code>a -> Cal.Core.Prelude.JObject</code>)
		 *          the mapping function converting elements of the list to <code>Cal.Core.Prelude.JObject</code> values, which are then returned
		 * by java.util.Iterator.next().
		 * @return (CAL type: <code>Cal.Collections.List.JIterator</code>) 
		 *          a Java iterator on the CAL list, where the elements of the CAL List are converted to Java objects
		 * using the <code>f</code> function.
		 */
		public static final SourceModel.Expr toJIteratorWith(SourceModel.Expr list, SourceModel.Expr f) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toJIteratorWith), list, f});
		}

		/**
		 * Name binding for function: toJIteratorWith.
		 * @see #toJIteratorWith(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName toJIteratorWith = 
			QualifiedName.make(CAL_List.MODULE_NAME, "toJIteratorWith");

		/**
		 * Transposes the rows and columns of the specified list of lists.
		 * <p>
		 * e.g. <code>transpose [[1,2,3],[4,5,6]] = [[1,4],[2,5],[3,6]]</code>
		 * <p>
		 * This function is like the matrix transpose from linear algebra, however it also works when
		 * the element lists are not all of equal lengths.
		 * 
		 * @param listOfLists (CAL type: <code>[[a]]</code>)
		 *          the list of lists to be transposed.
		 * @return (CAL type: <code>[[a]]</code>) 
		 *          the transposition of the argument.
		 */
		public static final SourceModel.Expr transpose(SourceModel.Expr listOfLists) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transpose), listOfLists});
		}

		/**
		 * Name binding for function: transpose.
		 * @see #transpose(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transpose = 
			QualifiedName.make(CAL_List.MODULE_NAME, "transpose");

		/**
		 * The <code>unfoldRight</code> function is a "dual" to <code>Cal.Collections.List.foldRight</code>: while <code>foldRight</code> reduces a
		 * list to a summary value, <code>Cal.Collections.List.unfoldRight</code> builds a list from a seed value. The
		 * function takes the element and returns <code>Cal.Core.Prelude.Nothing</code> if it is done producing the
		 * list or returns <code>Cal.Core.Prelude.Just (a,b)</code>, in which case, <code>a</code> is prepended to the list and <code>b</code>
		 * is used as the next element in a recursive call. For example,
		 * <p>
		 * <code>Cal.Collections.List.iterate f initialValue == unfoldRight (\x -&gt; Cal.Core.Prelude.Just (x, f x)) initialValue</code>
		 * <p>
		 * In some cases, <code>unfoldRight</code> can undo a <code>Cal.Collections.List.foldRight</code> operation:
		 * <p>
		 * <code>unfoldRight g (Cal.Collections.List.foldRight f z xs) == xs</code>
		 * <p>
		 * if the following holds:
		 * <ul>
		 *  <li>
		 *   <code>g (f x y) == Cal.Core.Prelude.Just (x,y)</code>
		 *  </li>
		 *  <li>
		 *   <code>g z == Cal.Core.Prelude.Nothing</code>
		 *  </li>
		 * </ul>
		 * 
		 * @param f (CAL type: <code>b -> Cal.Core.Prelude.Maybe (a, b)</code>)
		 * @param b (CAL type: <code>b</code>)
		 * @return (CAL type: <code>[a]</code>) 
		 */
		public static final SourceModel.Expr unfoldRight(SourceModel.Expr f, SourceModel.Expr b) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.unfoldRight), f, b});
		}

		/**
		 * Name binding for function: unfoldRight.
		 * @see #unfoldRight(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName unfoldRight = 
			QualifiedName.make(CAL_List.MODULE_NAME, "unfoldRight");

		/**
		 * <code>union list1 list2</code> returns <code>list1</code> concatenated with the nonduplicate elements of <code>list2</code>
		 * that do not occur in <code>list1</code>.
		 * <p>
		 * e.g. <code>union [3, 1, 5, 1] [9, 1, 6, 9] = [3, 1, 5, 1, 9, 6]</code>
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.List.unionBy
		 * </dl>
		 * 
		 * @param list1 (CAL type: <code>Cal.Core.Prelude.Eq a => [a]</code>)
		 *          the first list.
		 * @param list2 (CAL type: <code>Cal.Core.Prelude.Eq a => [a]</code>)
		 *          the second list.
		 * @return (CAL type: <code>Cal.Core.Prelude.Eq a => [a]</code>) 
		 *          <code>list1</code> concatenated with the nonduplicate elements of <code>list2</code> that do not occur in <code>list1</code>.
		 */
		public static final SourceModel.Expr union(SourceModel.Expr list1, SourceModel.Expr list2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.union), list1, list2});
		}

		/**
		 * Name binding for function: union.
		 * @see #union(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName union = 
			QualifiedName.make(CAL_List.MODULE_NAME, "union");

		/**
		 * <code>unionBy eq list1 list2</code> returns <code>list1</code> concatenated with the nonduplicate elements of <code>list2</code>
		 * (under <code>eq</code>) that do not occur in <code>list1</code>.
		 * <p>
		 * e.g. <code>unionBy Cal.Core.Prelude.equals [3, 1, 5, 1] [9, 1, 6, 9] = [3, 1, 5, 1, 9, 6]</code>
		 * 
		 * @param eq (CAL type: <code>a -> a -> Cal.Core.Prelude.Boolean</code>)
		 *          the equality comparison function to use in comparing the list elements.
		 * @param list1 (CAL type: <code>[a]</code>)
		 *          the first list.
		 * @param list2 (CAL type: <code>[a]</code>)
		 *          the second list.
		 * @return (CAL type: <code>[a]</code>) 
		 *          <code>list1</code> concatenated with the nonduplicate elements of <code>list2</code> (under <code>eq</code>) that do not
		 * occur in <code>list1</code>.
		 */
		public static final SourceModel.Expr unionBy(SourceModel.Expr eq, SourceModel.Expr list1, SourceModel.Expr list2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.unionBy), eq, list1, list2});
		}

		/**
		 * Name binding for function: unionBy.
		 * @see #unionBy(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName unionBy = 
			QualifiedName.make(CAL_List.MODULE_NAME, "unionBy");

		/**
		 * Transforms a list of pairs into a pair of lists.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.List.zip
		 * </dl>
		 * 
		 * @param list (CAL type: <code>[(a, b)]</code>)
		 *          the list to be unzipped.
		 * @return (CAL type: <code>([a], [b])</code>) 
		 *          a pair of lists.
		 */
		public static final SourceModel.Expr unzip(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.unzip), list});
		}

		/**
		 * Name binding for function: unzip.
		 * @see #unzip(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName unzip = 
			QualifiedName.make(CAL_List.MODULE_NAME, "unzip");

		/**
		 * Transforms a list of triples into a triple of lists.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.List.zip3
		 * </dl>
		 * 
		 * @param list (CAL type: <code>[(a, b, c)]</code>)
		 *          the list to be unzipped.
		 * @return (CAL type: <code>([a], [b], [c])</code>) 
		 *          a triple of lists.
		 */
		public static final SourceModel.Expr unzip3(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.unzip3), list});
		}

		/**
		 * Name binding for function: unzip3.
		 * @see #unzip3(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName unzip3 = 
			QualifiedName.make(CAL_List.MODULE_NAME, "unzip3");

		/**
		 * Transforms a list of 4-tuples into a 4-tuple of lists.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.List.zip4
		 * </dl>
		 * 
		 * @param list (CAL type: <code>[(a, b, c, d)]</code>)
		 *          the list to be unzipped.
		 * @return (CAL type: <code>([a], [b], [c], [d])</code>) 
		 *          a 4-tuple of lists.
		 */
		public static final SourceModel.Expr unzip4(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.unzip4), list});
		}

		/**
		 * Name binding for function: unzip4.
		 * @see #unzip4(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName unzip4 = 
			QualifiedName.make(CAL_List.MODULE_NAME, "unzip4");

		/**
		 * Transforms a list of 5-tuples into a 5-tuple of lists.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.List.zip5
		 * </dl>
		 * 
		 * @param list (CAL type: <code>[(a, b, c, d, e)]</code>)
		 *          the list to be unzipped.
		 * @return (CAL type: <code>([a], [b], [c], [d], [e])</code>) 
		 *          a 5-tuple of lists.
		 */
		public static final SourceModel.Expr unzip5(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.unzip5), list});
		}

		/**
		 * Name binding for function: unzip5.
		 * @see #unzip5(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName unzip5 = 
			QualifiedName.make(CAL_List.MODULE_NAME, "unzip5");

		/**
		 * Transforms a list of 6-tuples into a 6-tuple of lists.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.List.zip6
		 * </dl>
		 * 
		 * @param list (CAL type: <code>[(a, b, c, d, e, f)]</code>)
		 *          the list to be unzipped.
		 * @return (CAL type: <code>([a], [b], [c], [d], [e], [f])</code>) 
		 *          a 6-tuple of lists.
		 */
		public static final SourceModel.Expr unzip6(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.unzip6), list});
		}

		/**
		 * Name binding for function: unzip6.
		 * @see #unzip6(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName unzip6 = 
			QualifiedName.make(CAL_List.MODULE_NAME, "unzip6");

		/**
		 * Transforms a list of 7-tuples into a 7-tuple of lists.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.List.zip7
		 * </dl>
		 * 
		 * @param list (CAL type: <code>[(a, b, c, d, e, f, g)]</code>)
		 *          the list to be unzipped.
		 * @return (CAL type: <code>([a], [b], [c], [d], [e], [f], [g])</code>) 
		 *          a 7-tuple of lists.
		 */
		public static final SourceModel.Expr unzip7(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.unzip7), list});
		}

		/**
		 * Name binding for function: unzip7.
		 * @see #unzip7(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName unzip7 = 
			QualifiedName.make(CAL_List.MODULE_NAME, "unzip7");

		/**
		 * Converts two lists into a list of corresponding pairs.
		 * If one input list is short, excess elements of the longer list are discarded.
		 * <p>
		 * e.g. <code>zip [6, 3] [10, 20, 30] = [(6,10), (3, 20)]</code>
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.List.zipWith
		 * </dl>
		 * 
		 * @param list1 (CAL type: <code>[a]</code>)
		 *          the first list to be zipped.
		 * @param list2 (CAL type: <code>[b]</code>)
		 *          the second list to be zipped.
		 * @return (CAL type: <code>[(a, b)]</code>) 
		 *          a list of corresponding pairs.
		 */
		public static final SourceModel.Expr zip(SourceModel.Expr list1, SourceModel.Expr list2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.zip), list1, list2});
		}

		/**
		 * Name binding for function: zip.
		 * @see #zip(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName zip = 
			QualifiedName.make(CAL_List.MODULE_NAME, "zip");

		/**
		 * Converts three lists into a list of corresponding triples.
		 * The returned list is as long as the shortest of the input lists.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.List.zipWith3
		 * </dl>
		 * 
		 * @param list1 (CAL type: <code>[a]</code>)
		 *          the first list to be zipped.
		 * @param list2 (CAL type: <code>[b]</code>)
		 *          the second list to be zipped.
		 * @param list3 (CAL type: <code>[c]</code>)
		 *          the third list to be zipped.
		 * @return (CAL type: <code>[(a, b, c)]</code>) 
		 *          a list of corresponding triples.
		 */
		public static final SourceModel.Expr zip3(SourceModel.Expr list1, SourceModel.Expr list2, SourceModel.Expr list3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.zip3), list1, list2, list3});
		}

		/**
		 * Name binding for function: zip3.
		 * @see #zip3(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName zip3 = 
			QualifiedName.make(CAL_List.MODULE_NAME, "zip3");

		/**
		 * Converts four lists into a list of corresponding 4-tuples.
		 * The returned list is as long as the shortest of the input lists.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.List.zipWith4
		 * </dl>
		 * 
		 * @param list1 (CAL type: <code>[a]</code>)
		 *          the first list to be zipped.
		 * @param list2 (CAL type: <code>[b]</code>)
		 *          the second list to be zipped.
		 * @param list3 (CAL type: <code>[c]</code>)
		 *          the third list to be zipped.
		 * @param list4 (CAL type: <code>[d]</code>)
		 *          the fourth list to be zipped.
		 * @return (CAL type: <code>[(a, b, c, d)]</code>) 
		 *          a list of corresponding 4-tuples.
		 */
		public static final SourceModel.Expr zip4(SourceModel.Expr list1, SourceModel.Expr list2, SourceModel.Expr list3, SourceModel.Expr list4) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.zip4), list1, list2, list3, list4});
		}

		/**
		 * Name binding for function: zip4.
		 * @see #zip4(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName zip4 = 
			QualifiedName.make(CAL_List.MODULE_NAME, "zip4");

		/**
		 * Converts five lists into a list of corresponding 5-tuples.
		 * The returned list is as long as the shortest of the input lists.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.List.zipWith5
		 * </dl>
		 * 
		 * @param list1 (CAL type: <code>[a]</code>)
		 *          the first list to be zipped.
		 * @param list2 (CAL type: <code>[b]</code>)
		 *          the second list to be zipped.
		 * @param list3 (CAL type: <code>[c]</code>)
		 *          the third list to be zipped.
		 * @param list4 (CAL type: <code>[d]</code>)
		 *          the fourth list to be zipped.
		 * @param list5 (CAL type: <code>[e]</code>)
		 *          the fifth list to be zipped.
		 * @return (CAL type: <code>[(a, b, c, d, e)]</code>) 
		 *          a list of corresponding 5-tuples.
		 */
		public static final SourceModel.Expr zip5(SourceModel.Expr list1, SourceModel.Expr list2, SourceModel.Expr list3, SourceModel.Expr list4, SourceModel.Expr list5) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.zip5), list1, list2, list3, list4, list5});
		}

		/**
		 * Name binding for function: zip5.
		 * @see #zip5(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName zip5 = 
			QualifiedName.make(CAL_List.MODULE_NAME, "zip5");

		/**
		 * Converts six lists into a list of corresponding 6-tuples.
		 * The returned list is as long as the shortest of the input lists.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.List.zipWith6
		 * </dl>
		 * 
		 * @param list1 (CAL type: <code>[a]</code>)
		 *          the first list to be zipped.
		 * @param list2 (CAL type: <code>[b]</code>)
		 *          the second list to be zipped.
		 * @param list3 (CAL type: <code>[c]</code>)
		 *          the third list to be zipped.
		 * @param list4 (CAL type: <code>[d]</code>)
		 *          the fourth list to be zipped.
		 * @param list5 (CAL type: <code>[e]</code>)
		 *          the fifth list to be zipped.
		 * @param list6 (CAL type: <code>[f]</code>)
		 *          the sixth list to be zipped.
		 * @return (CAL type: <code>[(a, b, c, d, e, f)]</code>) 
		 *          a list of corresponding 6-tuples.
		 */
		public static final SourceModel.Expr zip6(SourceModel.Expr list1, SourceModel.Expr list2, SourceModel.Expr list3, SourceModel.Expr list4, SourceModel.Expr list5, SourceModel.Expr list6) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.zip6), list1, list2, list3, list4, list5, list6});
		}

		/**
		 * Name binding for function: zip6.
		 * @see #zip6(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName zip6 = 
			QualifiedName.make(CAL_List.MODULE_NAME, "zip6");

		/**
		 * Converts seven lists into a list of corresponding 7-tuples.
		 * The returned list is as long as the shortest of the input lists.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.List.zipWith7
		 * </dl>
		 * 
		 * @param list1 (CAL type: <code>[a]</code>)
		 *          the first list to be zipped.
		 * @param list2 (CAL type: <code>[b]</code>)
		 *          the second list to be zipped.
		 * @param list3 (CAL type: <code>[c]</code>)
		 *          the third list to be zipped.
		 * @param list4 (CAL type: <code>[d]</code>)
		 *          the fourth list to be zipped.
		 * @param list5 (CAL type: <code>[e]</code>)
		 *          the fifth list to be zipped.
		 * @param list6 (CAL type: <code>[f]</code>)
		 *          the sixth list to be zipped.
		 * @param list7 (CAL type: <code>[g]</code>)
		 *          the seventh list to be zipped.
		 * @return (CAL type: <code>[(a, b, c, d, e, f, g)]</code>) 
		 *          a list of corresponding 7-tuples.
		 */
		public static final SourceModel.Expr zip7(SourceModel.Expr list1, SourceModel.Expr list2, SourceModel.Expr list3, SourceModel.Expr list4, SourceModel.Expr list5, SourceModel.Expr list6, SourceModel.Expr list7) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.zip7), list1, list2, list3, list4, list5, list6, list7});
		}

		/**
		 * Name binding for function: zip7.
		 * @see #zip7(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName zip7 = 
			QualifiedName.make(CAL_List.MODULE_NAME, "zip7");

		/**
		 * Returns a list where each element is the result of applying a function to the
		 * corresponding elements of two lists.
		 * <p>
		 * <code>zipWith</code> generalises <code>Cal.Collections.List.zip</code> by zipping with the function given as the first
		 * argument, instead of a tupling function. For example, <code>zipWith Cal.Core.Prelude.add</code> applied
		 * to two lists produces the list of corresponding sums.
		 * <p>
		 * Example:
		 * 
		 * <pre> zipWith add [1.0, 2.0, 3.0] [4.0, 5.0, 6.0]
		 *     == [5.0, 7.0, 9.0]
		 * </pre>
		 * 
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.List.zip
		 * </dl>
		 * 
		 * @param zipFunction (CAL type: <code>a -> b -> c</code>)
		 *          the zipping function.
		 * @param list1 (CAL type: <code>[a]</code>)
		 *          the first list to be zipped.
		 * @param list2 (CAL type: <code>[b]</code>)
		 *          the second list to be zipped.
		 * @return (CAL type: <code>[c]</code>) 
		 *          a list of the element-wise combinations of the input lists.
		 */
		public static final SourceModel.Expr zipWith(SourceModel.Expr zipFunction, SourceModel.Expr list1, SourceModel.Expr list2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.zipWith), zipFunction, list1, list2});
		}

		/**
		 * Name binding for function: zipWith.
		 * @see #zipWith(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName zipWith = 
			QualifiedName.make(CAL_List.MODULE_NAME, "zipWith");

		/**
		 * Returns a list where each element is the result of applying a function to the
		 * corresponding elements of three lists.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.List.zip3
		 * </dl>
		 * 
		 * @param zipFunction (CAL type: <code>a -> b -> c -> d</code>)
		 *          the zipping function.
		 * @param list1 (CAL type: <code>[a]</code>)
		 *          the first list to be zipped.
		 * @param list2 (CAL type: <code>[b]</code>)
		 *          the second list to be zipped.
		 * @param list3 (CAL type: <code>[c]</code>)
		 *          the third list to be zipped.
		 * @return (CAL type: <code>[d]</code>) 
		 *          a list of the element-wise combinations of the input lists.
		 */
		public static final SourceModel.Expr zipWith3(SourceModel.Expr zipFunction, SourceModel.Expr list1, SourceModel.Expr list2, SourceModel.Expr list3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.zipWith3), zipFunction, list1, list2, list3});
		}

		/**
		 * Name binding for function: zipWith3.
		 * @see #zipWith3(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName zipWith3 = 
			QualifiedName.make(CAL_List.MODULE_NAME, "zipWith3");

		/**
		 * Returns a list where each element is the result of applying a function to the
		 * corresponding elements of four lists.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.List.zip4
		 * </dl>
		 * 
		 * @param zipFunction (CAL type: <code>a -> b -> c -> d -> e</code>)
		 *          the zipping function.
		 * @param list1 (CAL type: <code>[a]</code>)
		 *          the first list to be zipped.
		 * @param list2 (CAL type: <code>[b]</code>)
		 *          the second list to be zipped.
		 * @param list3 (CAL type: <code>[c]</code>)
		 *          the third list to be zipped.
		 * @param list4 (CAL type: <code>[d]</code>)
		 *          the fourth list to be zipped.
		 * @return (CAL type: <code>[e]</code>) 
		 *          a list of the element-wise combinations of the input lists.
		 */
		public static final SourceModel.Expr zipWith4(SourceModel.Expr zipFunction, SourceModel.Expr list1, SourceModel.Expr list2, SourceModel.Expr list3, SourceModel.Expr list4) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.zipWith4), zipFunction, list1, list2, list3, list4});
		}

		/**
		 * Name binding for function: zipWith4.
		 * @see #zipWith4(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName zipWith4 = 
			QualifiedName.make(CAL_List.MODULE_NAME, "zipWith4");

		/**
		 * Returns a list where each element is the result of applying a function to the
		 * corresponding elements of five lists.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.List.zip5
		 * </dl>
		 * 
		 * @param zipFunction (CAL type: <code>a -> b -> c -> d -> e -> f</code>)
		 *          the zipping function.
		 * @param list1 (CAL type: <code>[a]</code>)
		 *          the first list to be zipped.
		 * @param list2 (CAL type: <code>[b]</code>)
		 *          the second list to be zipped.
		 * @param list3 (CAL type: <code>[c]</code>)
		 *          the third list to be zipped.
		 * @param list4 (CAL type: <code>[d]</code>)
		 *          the fourth list to be zipped.
		 * @param list5 (CAL type: <code>[e]</code>)
		 *          the fifth list to be zipped.
		 * @return (CAL type: <code>[f]</code>) 
		 *          a list of the element-wise combinations of the input lists.
		 */
		public static final SourceModel.Expr zipWith5(SourceModel.Expr zipFunction, SourceModel.Expr list1, SourceModel.Expr list2, SourceModel.Expr list3, SourceModel.Expr list4, SourceModel.Expr list5) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.zipWith5), zipFunction, list1, list2, list3, list4, list5});
		}

		/**
		 * Name binding for function: zipWith5.
		 * @see #zipWith5(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName zipWith5 = 
			QualifiedName.make(CAL_List.MODULE_NAME, "zipWith5");

		/**
		 * Returns a list where each element is the result of applying a function to the
		 * corresponding elements of six lists.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.List.zip6
		 * </dl>
		 * 
		 * @param zipFunction (CAL type: <code>a -> b -> c -> d -> e -> f -> g</code>)
		 *          the zipping function.
		 * @param list1 (CAL type: <code>[a]</code>)
		 *          the first list to be zipped.
		 * @param list2 (CAL type: <code>[b]</code>)
		 *          the second list to be zipped.
		 * @param list3 (CAL type: <code>[c]</code>)
		 *          the third list to be zipped.
		 * @param list4 (CAL type: <code>[d]</code>)
		 *          the fourth list to be zipped.
		 * @param list5 (CAL type: <code>[e]</code>)
		 *          the fifth list to be zipped.
		 * @param list6 (CAL type: <code>[f]</code>)
		 *          the sixth list to be zipped.
		 * @return (CAL type: <code>[g]</code>) 
		 *          a list of the element-wise combinations of the input lists.
		 */
		public static final SourceModel.Expr zipWith6(SourceModel.Expr zipFunction, SourceModel.Expr list1, SourceModel.Expr list2, SourceModel.Expr list3, SourceModel.Expr list4, SourceModel.Expr list5, SourceModel.Expr list6) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.zipWith6), zipFunction, list1, list2, list3, list4, list5, list6});
		}

		/**
		 * Name binding for function: zipWith6.
		 * @see #zipWith6(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName zipWith6 = 
			QualifiedName.make(CAL_List.MODULE_NAME, "zipWith6");

		/**
		 * Returns a list where each element is the result of applying a function to the
		 * corresponding elements of seven lists.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.List.zip7
		 * </dl>
		 * 
		 * @param zipFunction (CAL type: <code>a -> b -> c -> d -> e -> f -> g -> h</code>)
		 *          the zipping function.
		 * @param list1 (CAL type: <code>[a]</code>)
		 *          the first list to be zipped.
		 * @param list2 (CAL type: <code>[b]</code>)
		 *          the second list to be zipped.
		 * @param list3 (CAL type: <code>[c]</code>)
		 *          the third list to be zipped.
		 * @param list4 (CAL type: <code>[d]</code>)
		 *          the fourth list to be zipped.
		 * @param list5 (CAL type: <code>[e]</code>)
		 *          the fifth list to be zipped.
		 * @param list6 (CAL type: <code>[f]</code>)
		 *          the sixth list to be zipped.
		 * @param list7 (CAL type: <code>[g]</code>)
		 *          the seventh list to be zipped.
		 * @return (CAL type: <code>[h]</code>) 
		 *          a list of the element-wise combinations of the input lists.
		 */
		public static final SourceModel.Expr zipWith7(SourceModel.Expr zipFunction, SourceModel.Expr list1, SourceModel.Expr list2, SourceModel.Expr list3, SourceModel.Expr list4, SourceModel.Expr list5, SourceModel.Expr list6, SourceModel.Expr list7) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.zipWith7), zipFunction, list1, list2, list3, list4, list5, list6, list7});
		}

		/**
		 * Name binding for function: zipWith7.
		 * @see #zipWith7(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName zipWith7 = 
			QualifiedName.make(CAL_List.MODULE_NAME, "zipWith7");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = -970097402;

}
