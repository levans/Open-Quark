/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_Array.java)
 * was generated from CAL module: Cal.Collections.Array.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Collections.Array module from Java code.
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
 * Defines the abstract data type <code>Cal.Collections.Array.Array</code> along with a variety of functions and instances for the type.
 * <p>
 * <code>Cal.Collections.Array.Array</code> is a polymorphic, immutable (i.e. purely functional) array type.
 * It is strict in its element values i.e. when an <code>Cal.Collections.Array.Array a</code> value is
 * created, all elements of the array are evaluated to weak-head normal form.
 * <p>
 * <code>Cal.Collections.Array.Array</code> offers constant time access to its elements. In addition,
 * <code>Cal.Collections.Array.Array</code> uses an unboxed representation of elements whenever possible. So for example, for
 * <code>Cal.Collections.Array.Array Cal.Core.Prelude.Int</code>, the underlying representation is the Java primitive array
 * <code>[int]</code>. Also, for an array of Java foreign objects, such as
 * <code>Cal.Collections.Array.Array Cal.Core.Prelude.String</code>, the underlying representation is a Java array
 * <code>[java.lang.String]</code>, rather than an array of CAL internal values holding onto java.lang.Strings
 * i.e. it is more space efficient.
 * 
 * @author Bo Ilic
 */
public final class CAL_Array {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Collections.Array");

	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Collections.Array module.
	 */
	public static final class TypeConstructors {
		/**
		 * <code>Array</code> is a polymorphic, immutable (i.e. purely functional) array type. It is strict in its element
		 * values i.e. when an <code>Array a</code> value is created, all elements of the array are evaluated to weak-head
		 * normal form.
		 * <p>
		 * <code>Array</code> offers constant time access to its elements. In addition, <code>Array</code> uses an unboxed representation
		 * of elements whenever possible. So for example, for <code>Array Cal.Core.Prelude.Int</code>, the underlying representation is the
		 * Java primitive array <code>[int]</code>.
		 */
		public static final QualifiedName Array = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "Array");

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Collections.Array module.
	 */
	public static final class Functions {
		/**
		 * <code>all predicate array</code> returns <code>Cal.Core.Prelude.True</code> if the predicate function evaluates to <code>Cal.Core.Prelude.True</code> on all the
		 * elements of the array (and the array is finite).
		 * @param predicate (CAL type: <code>a -> Cal.Core.Prelude.Boolean</code>)
		 *          a predicate to be applied to the elements of the array.
		 * @param array (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          the array whose elements are to be tested.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the predicate function evaluates to <code>Cal.Core.Prelude.True</code> on all the elements of the array (and the
		 * array is finite).
		 */
		public static final SourceModel.Expr all(SourceModel.Expr predicate, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.all), predicate, array});
		}

		/**
		 * Name binding for function: all.
		 * @see #all(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName all = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "all");

		/**
		 * Alternate items from the 2 arrays.
		 * If one arrays is longer than the other, then any remaining items will appear at the end of the array.
		 * @param array1 (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          one of the arrays to be merged
		 * @param array2 (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          the other array to be merged
		 * @return (CAL type: <code>Cal.Collections.Array.Array a</code>) 
		 *          an array consisting of alternating items from the input arrays
		 */
		public static final SourceModel.Expr alternate(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.alternate), array1, array2});
		}

		/**
		 * Name binding for function: alternate.
		 * @see #alternate(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName alternate = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "alternate");

		/**
		 * <code>andArray array</code> returns <code>Cal.Core.Prelude.True</code> if every element of the array is <code>Cal.Core.Prelude.True</code>.
		 * @param array (CAL type: <code>Cal.Collections.Array.Array Cal.Core.Prelude.Boolean</code>)
		 *          the array whose elements are to be tested.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if every element of the array is <code>Cal.Core.Prelude.True</code>.
		 */
		public static final SourceModel.Expr andArray(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.andArray), array});
		}

		/**
		 * Name binding for function: andArray.
		 * @see #andArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName andArray = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "andArray");

		/**
		 * <code>any predicate array</code> returns <code>Cal.Core.Prelude.True</code> if the predicate function evaluates to <code>Cal.Core.Prelude.True</code> on at least
		 * one element of the array.
		 * @param predicate (CAL type: <code>a -> Cal.Core.Prelude.Boolean</code>)
		 *          a predicate to be applied to the elements of the array.
		 * @param array (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          the array whose elements are to be tested.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the predicate function evaluates to <code>Cal.Core.Prelude.True</code> on at least one element of the array.
		 */
		public static final SourceModel.Expr any(SourceModel.Expr predicate, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.any), predicate, array});
		}

		/**
		 * Name binding for function: any.
		 * @see #any(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName any = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "any");

		/**
		 * Constructs an empty array.
		 * @return (CAL type: <code>Cal.Collections.Array.Array a</code>) 
		 *          an empty array.
		 */
		public static final SourceModel.Expr array0() {
			return SourceModel.Expr.Var.make(Functions.array0);
		}

		/**
		 * Name binding for function: array0.
		 * @see #array0()
		 */
		public static final QualifiedName array0 = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "array0");

		/**
		 * Constructs an array of one item.
		 * @param item (CAL type: <code>a</code>)
		 *          the item.
		 * @return (CAL type: <code>Cal.Collections.Array.Array a</code>) 
		 *          the array containing the specified item.
		 */
		public static final SourceModel.Expr array1(SourceModel.Expr item) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.array1), item});
		}

		/**
		 * Name binding for function: array1.
		 * @see #array1(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName array1 = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "array1");

		/**
		 * Constructs an array of two items.
		 * @param item1 (CAL type: <code>a</code>)
		 *          the first item.
		 * @param item2 (CAL type: <code>a</code>)
		 *          the second item.
		 * @return (CAL type: <code>Cal.Collections.Array.Array a</code>) 
		 *          the array containing all the specified items.
		 */
		public static final SourceModel.Expr array2(SourceModel.Expr item1, SourceModel.Expr item2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.array2), item1, item2});
		}

		/**
		 * Name binding for function: array2.
		 * @see #array2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName array2 = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "array2");

		/**
		 * Constructs an array of three items.
		 * @param item1 (CAL type: <code>a</code>)
		 *          the first item.
		 * @param item2 (CAL type: <code>a</code>)
		 *          the second item.
		 * @param item3 (CAL type: <code>a</code>)
		 *          the third item.
		 * @return (CAL type: <code>Cal.Collections.Array.Array a</code>) 
		 *          the array containing all the specified items.
		 */
		public static final SourceModel.Expr array3(SourceModel.Expr item1, SourceModel.Expr item2, SourceModel.Expr item3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.array3), item1, item2, item3});
		}

		/**
		 * Name binding for function: array3.
		 * @see #array3(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName array3 = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "array3");

		/**
		 * Constructs an array of four items.
		 * @param item1 (CAL type: <code>a</code>)
		 *          the first item.
		 * @param item2 (CAL type: <code>a</code>)
		 *          the second item.
		 * @param item3 (CAL type: <code>a</code>)
		 *          the third item.
		 * @param item4 (CAL type: <code>a</code>)
		 *          the fourth item.
		 * @return (CAL type: <code>Cal.Collections.Array.Array a</code>) 
		 *          the array containing all the specified items.
		 */
		public static final SourceModel.Expr array4(SourceModel.Expr item1, SourceModel.Expr item2, SourceModel.Expr item3, SourceModel.Expr item4) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.array4), item1, item2, item3, item4});
		}

		/**
		 * Name binding for function: array4.
		 * @see #array4(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName array4 = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "array4");

		/**
		 * Constructs an array of five items.
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
		 * @return (CAL type: <code>Cal.Collections.Array.Array a</code>) 
		 *          the array containing all the specified items.
		 */
		public static final SourceModel.Expr array5(SourceModel.Expr item1, SourceModel.Expr item2, SourceModel.Expr item3, SourceModel.Expr item4, SourceModel.Expr item5) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.array5), item1, item2, item3, item4, item5});
		}

		/**
		 * Name binding for function: array5.
		 * @see #array5(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName array5 = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "array5");

		/**
		 * Constructs an array of six items.
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
		 * @return (CAL type: <code>Cal.Collections.Array.Array a</code>) 
		 *          the array containing all the specified items.
		 */
		public static final SourceModel.Expr array6(SourceModel.Expr item1, SourceModel.Expr item2, SourceModel.Expr item3, SourceModel.Expr item4, SourceModel.Expr item5, SourceModel.Expr item6) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.array6), item1, item2, item3, item4, item5, item6});
		}

		/**
		 * Name binding for function: array6.
		 * @see #array6(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName array6 = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "array6");

		/**
		 * Constructs an array of seven items.
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
		 * @return (CAL type: <code>Cal.Collections.Array.Array a</code>) 
		 *          the array containing all the specified items.
		 */
		public static final SourceModel.Expr array7(SourceModel.Expr item1, SourceModel.Expr item2, SourceModel.Expr item3, SourceModel.Expr item4, SourceModel.Expr item5, SourceModel.Expr item6, SourceModel.Expr item7) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.array7), item1, item2, item3, item4, item5, item6, item7});
		}

		/**
		 * Name binding for function: array7.
		 * @see #array7(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName array7 = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "array7");

		/**
		 * Searches the array for the specified value using the binary search algorithm. The array must be
		 * sorted (via the ordering defined by <code>Cal.Core.Prelude.compare</code>) for the result to be well-defined. 
		 * If the array contains multiple elements that are equal to the specified value, there is no guarantee
		 * which specific one will be found.
		 * <p>
		 * If the array does not contain the specified value, a negative value will be returned. This negative value
		 * is equal to <code>(-insertionPoint - 1)</code> where <code>insertionPoint</code> is the point at which the value would need to
		 * be inserted into the array to maintain a sorted array.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.Array.binarySearchBy
		 * </dl>
		 * 
		 * @param array (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Collections.Array.Array a</code>)
		 *          sorted array to search
		 * @param value (CAL type: <code>Cal.Core.Prelude.Ord a => a</code>)
		 *          the value to search for
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the index of value in the array, or a negative value if value does not occur in the array.
		 */
		public static final SourceModel.Expr binarySearch(SourceModel.Expr array, SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.binarySearch), array, value});
		}

		/**
		 * Name binding for function: binarySearch.
		 * @see #binarySearch(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName binarySearch = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "binarySearch");

		/**
		 * Searches the array for the specified value using the binary search algorithm. The array must be
		 * sorted (via the ordering defined by the <code>comparisonFunction</code> argument) for the result to be well-defined. 
		 * If the array contains multiple elements that are equal to the specified value (under the notion of equality
		 * defined by <code>comparisonFunction</code>), there is no guarantee which specific one will be found.
		 * <p>
		 * If the array does not contain the specified value, a negative value will be returned. This negative value
		 * is equal to <code>(-insertionPoint - 1)</code> where <code>insertionPoint</code> is the point at which the value would need to
		 * be inserted into the array to maintain a sorted array.
		 * 
		 * @param comparisonFunction (CAL type: <code>a -> a -> Cal.Core.Prelude.Ordering</code>)
		 *          the comparison function to use for the search.
		 * @param array (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          the array to search. Assumed to be sorted according to the ordering established by comparisonFunction.
		 * @param value (CAL type: <code>a</code>)
		 *          the value to search for
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the index of value in the array, or a negative value if value does not occur in the array.
		 */
		public static final SourceModel.Expr binarySearchBy(SourceModel.Expr comparisonFunction, SourceModel.Expr array, SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.binarySearchBy), comparisonFunction, array, value});
		}

		/**
		 * Name binding for function: binarySearchBy.
		 * @see #binarySearchBy(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName binarySearchBy = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "binarySearchBy");

		/**
		 * <code>break predicate array</code> breaks up array into a pair of arrays. The start of the second array is the first element of
		 * <code>array</code> on which predicate is <code>Cal.Core.Prelude.True</code>.
		 * @param predicate (CAL type: <code>a -> Cal.Core.Prelude.Boolean</code>)
		 *          a predicate to be applied to the elements of the array.
		 * @param array (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          the array to be broken up into a pair of arrays.
		 * @return (CAL type: <code>(Cal.Collections.Array.Array a, Cal.Collections.Array.Array a)</code>) 
		 *          a pair of arrays, where the first array is the longest prefix of the array for which predicate
		 * is <code>Cal.Core.Prelude.False</code> for each element, and the second array contains the remaining elements of the original array.
		 */
		public static final SourceModel.Expr break_(SourceModel.Expr predicate, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.break_), predicate, array});
		}

		/**
		 * Name binding for function: break.
		 * @see #break_(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName break_ = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "break");

		/**
		 * <code>breakAfter predicate array</code> breaks up array into a pair of arrays. The last item of the first array is the first element
		 * of array on which the predicate function is <code>Cal.Core.Prelude.True</code>.
		 * @param predicate (CAL type: <code>a -> Cal.Core.Prelude.Boolean</code>)
		 *          a predicate to be applied to the elements of the array.
		 * @param array (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          the array to be split.
		 * @return (CAL type: <code>(Cal.Collections.Array.Array a, Cal.Collections.Array.Array a)</code>) 
		 *          a pair of arrays. The last item of the first array is the first element of array
		 * on which the predicate function is <code>Cal.Core.Prelude.True</code>. The second array contains the remaining
		 * elements of the original array.
		 */
		public static final SourceModel.Expr breakAfter(SourceModel.Expr predicate, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.breakAfter), predicate, array});
		}

		/**
		 * Name binding for function: breakAfter.
		 * @see #breakAfter(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName breakAfter = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "breakAfter");

		/**
		 * Chops up an Array into equals subarrays of length <code>chopLength</code>.
		 * @param chopLength (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the length of the resulting sublists.
		 * @param arrayToChop (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          the list to be chopped.
		 * @return (CAL type: <code>Cal.Collections.Array.Array (Cal.Collections.Array.Array a)</code>) 
		 *          an array of the subarrays.
		 */
		public static final SourceModel.Expr chop(SourceModel.Expr chopLength, SourceModel.Expr arrayToChop) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.chop), chopLength, arrayToChop});
		}

		/**
		 * @see #chop(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param chopLength
		 * @param arrayToChop
		 * @return the SourceModel.Expr representing an application of chop
		 */
		public static final SourceModel.Expr chop(int chopLength, SourceModel.Expr arrayToChop) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.chop), SourceModel.Expr.makeIntValue(chopLength), arrayToChop});
		}

		/**
		 * Name binding for function: chop.
		 * @see #chop(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName chop = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "chop");

		/**
		 * Returns a compressed version of a byte array.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.Array.decompressBytes
		 * </dl>
		 * 
		 * @param byteArray (CAL type: <code>Cal.Collections.Array.Array Cal.Core.Prelude.Byte</code>)
		 *          the byte array to be compressed
		 * @return (CAL type: <code>Cal.Collections.Array.Array Cal.Core.Prelude.Byte</code>) 
		 *          the compressed byte array
		 */
		public static final SourceModel.Expr compressBytes(SourceModel.Expr byteArray) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.compressBytes), byteArray});
		}

		/**
		 * Name binding for function: compressBytes.
		 * @see #compressBytes(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName compressBytes = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "compressBytes");

		/**
		 * An Array version of the regular <code>concat</code> function
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Prelude.concat
		 * <dd><b>Type Constructors:</b> Cal.Collections.Array.Array
		 * </dl>
		 * 
		 * @param array (CAL type: <code>Cal.Core.Prelude.Appendable a => Cal.Collections.Array.Array a</code>)
		 *          The Array we're collapsing to a single value
		 * @return (CAL type: <code>Cal.Core.Prelude.Appendable a => a</code>) 
		 */
		public static final SourceModel.Expr concat(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.concat), array});
		}

		/**
		 * Name binding for function: concat.
		 * @see #concat(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName concat = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "concat");

		/**
		 * Returns an uncompressed version of a compressed byte array.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.Array.compressBytes
		 * </dl>
		 * 
		 * @param byteArray (CAL type: <code>Cal.Collections.Array.Array Cal.Core.Prelude.Byte</code>)
		 *          the byte array to be decompressed
		 * @return (CAL type: <code>Cal.Collections.Array.Array Cal.Core.Prelude.Byte</code>) 
		 *          the decompressed byte array
		 */
		public static final SourceModel.Expr decompressBytes(SourceModel.Expr byteArray) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.decompressBytes), byteArray});
		}

		/**
		 * Name binding for function: decompressBytes.
		 * @see #decompressBytes(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName decompressBytes = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "decompressBytes");

		/**
		 * <code>delete x array</code> returns the array with the first element equivalent to <code>x</code> removed.
		 * <p>
		 * e.g. <code>delete 1 [3, 1, 4, 1, 5, 9] = [3, 4, 1, 5, 9]</code>
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.Array.deleteBy
		 * </dl>
		 * 
		 * @param element (CAL type: <code>Cal.Core.Prelude.Eq a => a</code>)
		 *          the value of the element to be removed.
		 * @param array (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Collections.Array.Array a</code>)
		 *          the array from which an element is to be removed.
		 * @return (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Collections.Array.Array a</code>) 
		 *          the array with the first element equivalent to x removed.
		 */
		public static final SourceModel.Expr delete(SourceModel.Expr element, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.delete), element, array});
		}

		/**
		 * Name binding for function: delete.
		 * @see #delete(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName delete = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "delete");

		/**
		 * Removes the array item at the specified index.
		 * Note that the argument array is not modified.
		 * Fails in a run-time error if index is not a valid index into the argument array.
		 * @param index (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the index of the item to be removed.
		 * @param array (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          the array from which an item is to be removed.
		 * @return (CAL type: <code>Cal.Collections.Array.Array a</code>) 
		 *          the array with the item at the specified index removed.
		 */
		public static final SourceModel.Expr deleteAt(SourceModel.Expr index, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.deleteAt), index, array});
		}

		/**
		 * @see #deleteAt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param index
		 * @param array
		 * @return the SourceModel.Expr representing an application of deleteAt
		 */
		public static final SourceModel.Expr deleteAt(int index, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.deleteAt), SourceModel.Expr.makeIntValue(index), array});
		}

		/**
		 * Name binding for function: deleteAt.
		 * @see #deleteAt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName deleteAt = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "deleteAt");

		/**
		 * <code>deleteBy eq element array</code> returns the array with the first element equivalent to <code>x</code> (under <code>eq</code>) removed.
		 * @param eq (CAL type: <code>a -> b -> Cal.Core.Prelude.Boolean</code>)
		 *          the equality comparison function to use in comparing the array elements.
		 * @param element (CAL type: <code>a</code>)
		 *          the value of the element to be removed.
		 * @param array (CAL type: <code>Cal.Collections.Array.Array b</code>)
		 *          the array from which an element is to be removed.
		 * @return (CAL type: <code>Cal.Collections.Array.Array b</code>) 
		 *          the array with the first element equivalent to x (under <code>eq</code>) removed.
		 */
		public static final SourceModel.Expr deleteBy(SourceModel.Expr eq, SourceModel.Expr element, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.deleteBy), eq, element, array});
		}

		/**
		 * Name binding for function: deleteBy.
		 * @see #deleteBy(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName deleteBy = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "deleteBy");

		/**
		 * Removes the array items at the specified range of indices.
		 * Note that the argument array is not modified.
		 * Fails in a run-time error if
		 * <ul>
		 *  <li>
		 *   <code>fromIndex &lt; 0</code>
		 *  </li>
		 *  <li>
		 *   <code>toIndex &gt; length array</code>
		 *  </li>
		 *  <li>
		 *   <code>fromIndex &gt; toIndex</code>
		 *  </li>
		 * </ul>
		 * 
		 * @param fromIndex (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the index of the first element to be removed.
		 * @param toIndex (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          one more than the index of the last element to be removed.
		 * @param array (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          the array from which the given values at the specified range of indices are to be removed.
		 * @return (CAL type: <code>Cal.Collections.Array.Array a</code>) 
		 *          the array with the items at the specified range of indices removed.
		 */
		public static final SourceModel.Expr deleteRange(SourceModel.Expr fromIndex, SourceModel.Expr toIndex, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.deleteRange), fromIndex, toIndex, array});
		}

		/**
		 * @see #deleteRange(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param fromIndex
		 * @param toIndex
		 * @param array
		 * @return the SourceModel.Expr representing an application of deleteRange
		 */
		public static final SourceModel.Expr deleteRange(int fromIndex, int toIndex, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.deleteRange), SourceModel.Expr.makeIntValue(fromIndex), SourceModel.Expr.makeIntValue(toIndex), array});
		}

		/**
		 * Name binding for function: deleteRange.
		 * @see #deleteRange(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName deleteRange = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "deleteRange");

		/**
		 * <code>drop nElements array</code> drops the first <code>nElements</code> elements of the array and returns the remaining elements
		 * @param nElementsToDrop (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the number of elements to drop.
		 * @param array (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          the array from which elements are to be dropped.
		 * @return (CAL type: <code>Cal.Collections.Array.Array a</code>) 
		 *          an array of the remaining elements.
		 */
		public static final SourceModel.Expr drop(SourceModel.Expr nElementsToDrop, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.drop), nElementsToDrop, array});
		}

		/**
		 * @see #drop(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param nElementsToDrop
		 * @param array
		 * @return the SourceModel.Expr representing an application of drop
		 */
		public static final SourceModel.Expr drop(int nElementsToDrop, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.drop), SourceModel.Expr.makeIntValue(nElementsToDrop), array});
		}

		/**
		 * Name binding for function: drop.
		 * @see #drop(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName drop = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "drop");

		/**
		 * <code>dropWhile dropWhileTrueFunction list</code> drops the longest prefix of the array for which <code>dropWhileTrueFunction</code>
		 * is <code>Cal.Core.Prelude.True</code> for each element.
		 * <p>
		 * e.g. <code>dropWhile Cal.Core.Prelude.isEven (Cal.Collections.Array.fromList [6, 2, 1, 2]) = (Cal.Collections.Array.fromList [1, 2])</code>
		 * 
		 * @param dropWhileTrueFunction (CAL type: <code>a -> Cal.Core.Prelude.Boolean</code>)
		 *          a predicate to be applied to the elements of the array.
		 * @param array (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          the array from which elements are to be taken.
		 * @return (CAL type: <code>Cal.Collections.Array.Array a</code>) 
		 *          the remainder of the array after having dropped the longest prefix in
		 * which <code>dropWhileTrueFunction</code> is <code>Cal.Core.Prelude.True</code> for each element.
		 */
		public static final SourceModel.Expr dropWhile(SourceModel.Expr dropWhileTrueFunction, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.dropWhile), dropWhileTrueFunction, array});
		}

		/**
		 * Name binding for function: dropWhile.
		 * @see #dropWhile(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName dropWhile = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "dropWhile");

		/**
		 * Returns the index of the first element in the given array which is equal to
		 * the specified value, or <code>Cal.Core.Prelude.Nothing</code> if there is no such element.
		 * @param value (CAL type: <code>Cal.Core.Prelude.Eq a => a</code>)
		 *          the value to be found.
		 * @param array (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Collections.Array.Array a</code>)
		 *          the array to be searched.
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Core.Prelude.Int</code>) 
		 *          the index of the first element in the given array which is equal to
		 * the specified value, or <code>Cal.Core.Prelude.Nothing</code> if there is no such element.
		 */
		public static final SourceModel.Expr elemIndex(SourceModel.Expr value, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.elemIndex), value, array});
		}

		/**
		 * Name binding for function: elemIndex.
		 * @see #elemIndex(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName elemIndex = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "elemIndex");

		/**
		 * Returns the indices of all elements of the specified array equal to the
		 * specified value, in ascending order.
		 * @param x (CAL type: <code>Cal.Core.Prelude.Eq a => a</code>)
		 *          the value to be found.
		 * @param array (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Collections.Array.Array a</code>)
		 *          the array to be searched.
		 * @return (CAL type: <code>Cal.Collections.Array.Array Cal.Core.Prelude.Int</code>) 
		 *          the indices of all elements of the specified array equal to the
		 * specified value, in ascending order.
		 */
		public static final SourceModel.Expr elemIndices(SourceModel.Expr x, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.elemIndices), x, array});
		}

		/**
		 * Name binding for function: elemIndices.
		 * @see #elemIndices(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName elemIndices = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "elemIndices");

		/**
		 * <code>filter keepIfTrueFunction array</code> applies the predicate function to each element of the array, and returns
		 * the array of elements for which the predicate evaluates to <code>Cal.Core.Prelude.True</code>.
		 * @param keepIfTrueFunction (CAL type: <code>a -> Cal.Core.Prelude.Boolean</code>)
		 *          a predicate which returns True for items that should be kept, and False for items that should be dropped.
		 * @param array (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          the array.
		 * @return (CAL type: <code>Cal.Collections.Array.Array a</code>) 
		 *          the array of those elements that satisfy the given predicate.
		 */
		public static final SourceModel.Expr filter(SourceModel.Expr keepIfTrueFunction, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.filter), keepIfTrueFunction, array});
		}

		/**
		 * Name binding for function: filter.
		 * @see #filter(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName filter = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "filter");

		/**
		 * <code>filterIndexed keepIfTrueFunction array</code> applies the predicate function <code>keepIfTrueFunction</code> to each
		 * element of the array, and returns the array of elements for which the predicate evaluates to <code>Cal.Core.Prelude.True</code>.
		 * <code>keepIfTrueFunction</code> is a function that is passed both the element value, and its zero-based index in the array.
		 * @param keepIfTrueFunction (CAL type: <code>a -> Cal.Core.Prelude.Int -> Cal.Core.Prelude.Boolean</code>)
		 *          a predicate which returns <code>Cal.Core.Prelude.True</code> for items that should be kept, and <code>Cal.Core.Prelude.False</code> for items that
		 * should be dropped. It is passed both the element value, and its zero-based index in the array.
		 * @param array (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          the array.
		 * @return (CAL type: <code>Cal.Collections.Array.Array a</code>) 
		 *          the array of those elements that satisfy the given predicate.
		 */
		public static final SourceModel.Expr filterIndexed(SourceModel.Expr keepIfTrueFunction, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.filterIndexed), keepIfTrueFunction, array});
		}

		/**
		 * Name binding for function: filterIndexed.
		 * @see #filterIndexed(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName filterIndexed = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "filterIndexed");

		/**
		 * <code>find predicate array</code> returns the first value of array for which the predicate function is <code>Cal.Core.Prelude.True</code>,
		 * if there is one, or <code>Cal.Core.Prelude.Nothing</code> otherwise.
		 * <p>
		 * e.g. <code>find Cal.Core.Prelude.isEven (Cal.Collections.Array.fromList [1, 1, 4, 1, 2, 1, 1, 6]) == Cal.Core.Prelude.Just 4</code>
		 * 
		 * @param predicate (CAL type: <code>a -> Cal.Core.Prelude.Boolean</code>)
		 *          a predicate to be applied to the elements of the array.
		 * @param array (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          the array to be searched.
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe a</code>) 
		 *          the first value of array for which the predicate function is <code>Cal.Core.Prelude.True</code>, if there is one, or <code>Cal.Core.Prelude.Nothing</code>
		 * otherwise.
		 */
		public static final SourceModel.Expr find(SourceModel.Expr predicate, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.find), predicate, array});
		}

		/**
		 * Name binding for function: find.
		 * @see #find(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName find = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "find");

		/**
		 * <code>findIndex predicate array</code> returns the first index of array for which the predicate function is <code>Cal.Core.Prelude.True</code>,
		 * if there is one, or <code>Cal.Core.Prelude.Nothing</code> otherwise. The index is 0-based.
		 * <p>
		 * e.g. <code>findIndex Cal.Core.Prelude.isEven (Cal.Collections.Array.fromList [1, 1, 4, 1, 2, 1, 1, 6]) == Cal.Core.Prelude.Just 2</code>
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.Array.findIndices
		 * </dl>
		 * 
		 * @param predicate (CAL type: <code>a -> Cal.Core.Prelude.Boolean</code>)
		 *          a predicate to be applied to the elements of the list.
		 * @param array (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          the array to be searched.
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Core.Prelude.Int</code>) 
		 *          the first index of array for which the predicate function is <code>Cal.Core.Prelude.True</code>, if there is one, or <code>Cal.Core.Prelude.Nothing</code> otherwise.
		 */
		public static final SourceModel.Expr findIndex(SourceModel.Expr predicate, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.findIndex), predicate, array});
		}

		/**
		 * Name binding for function: findIndex.
		 * @see #findIndex(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName findIndex = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "findIndex");

		/**
		 * Returns the indices of all elements satisfying the specified predicate, in ascending order. The index values are 0-based.
		 * <p>
		 * e.g. <code>findIndices Cal.Core.Prelude.isEven [1, 1, 4, 1, 2, 1, 1, 6] == [2, 4, 7]</code>
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.Array.findIndex
		 * </dl>
		 * 
		 * @param predicate (CAL type: <code>a -> Cal.Core.Prelude.Boolean</code>)
		 *          a predicate to be applied to the elements of the list.
		 * @param array (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          the array to be searched.
		 * @return (CAL type: <code>[Cal.Core.Prelude.Int]</code>) 
		 *          the list of all indices of array for which the predicate function evaluates to <code>Cal.Core.Prelude.True</code>.
		 */
		public static final SourceModel.Expr findIndices(SourceModel.Expr predicate, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.findIndices), predicate, array});
		}

		/**
		 * Name binding for function: findIndices.
		 * @see #findIndices(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName findIndices = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "findIndices");

		/**
		 * <code>foldLeft foldFunction initialValue list</code> collapses or 'folds' the array down to a single result, starting
		 * from the left of the array. It uses <code>initialValue</code> as the initial value for the folding process, and
		 * <code>foldFunction</code> as the collapsing function.
		 * <p>
		 * If <code>foldFunction</code> is strict in both of its arguments it is usually better to use <code>Cal.Collections.Array.foldLeftStrict</code>
		 * instead of <code>foldLeft</code> since using <code>foldLeft</code> will cause significantly more memory to be used than using
		 * <code>Cal.Collections.Array.foldLeftStrict</code>. If <code>foldFunction</code> is not strict in both of its arguments, it usually results in better
		 * lazy behavior to use <code>Cal.Collections.Array.foldRight</code> rather than <code>foldLeft</code>.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.Array.foldLeftStrict, Cal.Collections.Array.foldRight, Cal.Collections.Array.foldLeft1, Cal.Collections.Array.foldLeft1Strict, Cal.Collections.Array.foldRight1
		 * </dl>
		 * 
		 * @param foldFunction (CAL type: <code>a -> b -> a</code>)
		 *          the function to be used in folding the array.
		 * @param initialValue (CAL type: <code>a</code>)
		 *          the initial value for the folding process.
		 * @param array (CAL type: <code>Cal.Collections.Array.Array b</code>)
		 *          the array to be folded over.
		 * @return (CAL type: <code>a</code>) 
		 *          the single result obtained from folding <code>foldFunction</code> over the array.
		 */
		public static final SourceModel.Expr foldLeft(SourceModel.Expr foldFunction, SourceModel.Expr initialValue, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.foldLeft), foldFunction, initialValue, array});
		}

		/**
		 * Name binding for function: foldLeft.
		 * @see #foldLeft(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName foldLeft = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "foldLeft");

		/**
		 * <code>foldLeft1</code> is similar to <code>Cal.Collections.Array.foldLeft</code>, except that it uses the first element of the array as the initial value
		 * in the folding process. Hence it gives an error if array is the empty array.
		 * <p>
		 * If <code>foldFunction</code> is strict in both of its arguments it is usually better to use <code>Cal.Collections.Array.foldLeft1Strict</code>
		 * instead of <code>foldLeft1</code> since using <code>foldLeft1</code> will cause significantly more memory to be used than using
		 * <code>Cal.Collections.Array.foldLeft1Strict</code>. If <code>foldFunction</code> is not strict in both of its arguments, it usually results in better
		 * lazy behavior to use <code>Cal.Collections.Array.foldRight1</code> rather than <code>foldLeft1</code>. 
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.Array.foldLeft, Cal.Collections.Array.foldLeftStrict, Cal.Collections.Array.foldRight, Cal.Collections.Array.foldLeft1Strict, Cal.Collections.Array.foldRight1
		 * </dl>
		 * 
		 * @param foldFunction (CAL type: <code>a -> a -> a</code>)
		 *          the function to be used in folding the array.
		 * @param array (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          the array to be folded over.
		 * @return (CAL type: <code>a</code>) 
		 *          the single result obtained from folding <code>foldFunction</code> over the array.
		 */
		public static final SourceModel.Expr foldLeft1(SourceModel.Expr foldFunction, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.foldLeft1), foldFunction, array});
		}

		/**
		 * Name binding for function: foldLeft1.
		 * @see #foldLeft1(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName foldLeft1 = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "foldLeft1");

		/**
		 * <code>foldLeft1Strict</code> is similar to <code>Cal.Collections.Array.foldLeftStrict</code>, except that it uses the first element of the array as
		 * the initial value in the folding process. Hence it gives an error if array is the empty array.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.Array.foldLeft, Cal.Collections.Array.foldLeftStrict, Cal.Collections.Array.foldRight, Cal.Collections.Array.foldLeft1, Cal.Collections.Array.foldRight1
		 * </dl>
		 * 
		 * @param foldFunction (CAL type: <code>a -> a -> a</code>)
		 *          the function to be used in folding the array.
		 * @param array (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          the array to be folded over.
		 * @return (CAL type: <code>a</code>) 
		 *          the single result obtained from folding <code>foldFunction</code> over the array.
		 */
		public static final SourceModel.Expr foldLeft1Strict(SourceModel.Expr foldFunction, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.foldLeft1Strict), foldFunction, array});
		}

		/**
		 * Name binding for function: foldLeft1Strict.
		 * @see #foldLeft1Strict(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName foldLeft1Strict = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "foldLeft1Strict");

		/**
		 * This is the strict version of <code>Cal.Collections.Array.foldLeft</code>. It is used for efficiency reasons in certain situations.
		 * For example, it can be used to define the sum and product functions so that they are constant
		 * space functions, whereas the versions defined with <code>Cal.Collections.Array.foldLeft</code> would not be constant space.
		 * <p>
		 * If <code>foldFunction</code> is strict in both of its arguments it is usually better to use <code>foldLeftStrict</code>
		 * instead of <code>Cal.Collections.Array.foldLeft</code> since using <code>Cal.Collections.Array.foldLeft</code> will cause significantly more memory to be used than using
		 * <code>foldLeftStrict</code>. If <code>foldFunction</code> is not strict in both of its arguments, it usually results in better
		 * lazy behavior to use <code>Cal.Collections.Array.foldRight</code> rather than <code>Cal.Collections.Array.foldLeft</code>. 
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.Array.foldLeft, Cal.Collections.Array.foldRight, Cal.Collections.Array.foldLeft1, Cal.Collections.Array.foldLeft1Strict, Cal.Collections.Array.foldRight1
		 * </dl>
		 * 
		 * @param foldFunction (CAL type: <code>a -> b -> a</code>)
		 *          the function to be used in folding the array.
		 * @param initialValue (CAL type: <code>a</code>)
		 *          the initial value for the folding process.
		 * @param array (CAL type: <code>Cal.Collections.Array.Array b</code>)
		 *          the array to be folded over.
		 * @return (CAL type: <code>a</code>) 
		 *          the single result obtained from folding <code>foldFunction</code> over the array.
		 */
		public static final SourceModel.Expr foldLeftStrict(SourceModel.Expr foldFunction, SourceModel.Expr initialValue, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.foldLeftStrict), foldFunction, initialValue, array});
		}

		/**
		 * Name binding for function: foldLeftStrict.
		 * @see #foldLeftStrict(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName foldLeftStrict = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "foldLeftStrict");

		/**
		 * Similar to <code>Cal.Collections.Array.foldLeft</code>, except that the folding process on the array is started with its rightmost element.
		 * Often the result of applying <code>Cal.Collections.Array.foldLeft</code> or <code>foldRight</code> is the same, and the choice between them is a matter of
		 * efficiency. Which is better depends on the nature of the folding function. As a general rule, if the folding
		 * function is strict in both arguments, <code>Cal.Collections.Array.foldLeftStrict</code> is a good choice. Otherwise <code>foldRight</code> is often best.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.Array.foldLeft, Cal.Collections.Array.foldLeftStrict, Cal.Collections.Array.foldLeft1, Cal.Collections.Array.foldLeft1Strict, Cal.Collections.Array.foldRight1
		 * </dl>
		 * 
		 * @param foldFunction (CAL type: <code>a -> b -> b</code>)
		 *          the function to be used in folding the array.
		 * @param initialValue (CAL type: <code>b</code>)
		 *          the initial value for the folding process.
		 * @param array (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          the array to be folded over.
		 * @return (CAL type: <code>b</code>) 
		 *          the single result obtained from folding <code>foldFunction</code> over the array.
		 */
		public static final SourceModel.Expr foldRight(SourceModel.Expr foldFunction, SourceModel.Expr initialValue, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.foldRight), foldFunction, initialValue, array});
		}

		/**
		 * Name binding for function: foldRight.
		 * @see #foldRight(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName foldRight = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "foldRight");

		/**
		 * Similar to <code>Cal.Collections.Array.foldLeft1</code>, except that the folding process on the array is started with its rightmost element.
		 * Often the result of applying <code>Cal.Collections.Array.foldLeft1</code> or <code>foldRight1</code> is the same, and the choice between them is a matter of
		 * efficiency. Which is better depends on the nature of the folding function. As a general rule, if the folding
		 * function is strict in both arguments, <code>Cal.Collections.Array.foldLeft1Strict</code> is a good choice. Otherwise <code>foldRight1</code> is often best.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.Array.foldLeft, Cal.Collections.Array.foldLeftStrict, Cal.Collections.Array.foldRight, Cal.Collections.Array.foldLeft1, Cal.Collections.Array.foldLeft1Strict
		 * </dl>
		 * 
		 * @param foldFunction (CAL type: <code>a -> a -> a</code>)
		 *          the function to be used in folding the array.
		 * @param array (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          the array to be folded over.
		 * @return (CAL type: <code>a</code>) 
		 *          the single result obtained from folding <code>foldFunction</code> over the array.
		 */
		public static final SourceModel.Expr foldRight1(SourceModel.Expr foldFunction, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.foldRight1), foldFunction, array});
		}

		/**
		 * Name binding for function: foldRight1.
		 * @see #foldRight1(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName foldRight1 = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "foldRight1");

		/**
		 * Builds an array from a list. Note that because of how the <code>Cal.Collections.Array.Array</code> type is defined, every element of list
		 * will be evaluated to weak head normal form. In particular, this function will hang on an infinite source list!
		 * <p>
		 * Note that another main way of constructing arrays is using <code>Cal.Core.Prelude.input</code> (and the
		 * <code>Cal.Core.Prelude.Inputable Cal.Collections.Array.Array</code> instance). 
		 * Using <code>Cal.Core.Prelude.input</code> on a Java array will generally be more efficient that inputing a foreign object first as
		 * a CAL list and then calling <code>Cal.Collections.Array.fromList</code>.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.Array.fromListWith
		 * </dl>
		 * 
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list to convert to an array
		 * @return (CAL type: <code>Cal.Collections.Array.Array a</code>) 
		 *          the resulting array
		 */
		public static final SourceModel.Expr fromList(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromList), list});
		}

		/**
		 * Name binding for function: fromList.
		 * @see #fromList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fromList = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "fromList");

		/**
		 * Builds an array from a list with the help of an element conversion function <code>f</code>.
		 * Note that because of how the <code>Cal.Collections.Array.Array</code> type is defined, every element of list
		 * will be evaluated to weak head normal form. In particular, this function will hang on an infinite source list!
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.Array.fromList
		 * </dl>
		 * 
		 * @param f (CAL type: <code>a -> b</code>)
		 *          function to use to transform elements of the list to elements of the array
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list to convert to an array
		 * @return (CAL type: <code>Cal.Collections.Array.Array b</code>) 
		 *          the resulting array
		 */
		public static final SourceModel.Expr fromListWith(SourceModel.Expr f, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromListWith), f, list});
		}

		/**
		 * Name binding for function: fromListWith.
		 * @see #fromListWith(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fromListWith = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "fromListWith");

		/**
		 * Converts a <code>Cal.Core.Prelude.String</code> to an <code>Cal.Collections.Array.Array Cal.Core.Prelude.Char</code>.
		 * @param string (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Collections.Array.Array Cal.Core.Prelude.Char</code>) 
		 */
		public static final SourceModel.Expr fromString(SourceModel.Expr string) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromString), string});
		}

		/**
		 * @see #fromString(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param string
		 * @return the SourceModel.Expr representing an application of fromString
		 */
		public static final SourceModel.Expr fromString(java.lang.String string) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromString), SourceModel.Expr.makeStringValue(string)});
		}

		/**
		 * Name binding for function: fromString.
		 * @see #fromString(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fromString = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "fromString");

		/**
		 * Finds the index of the first occurrence of <code>valueToFind</code> in <code>array</code> or -1 if the value does not occur.
		 * @param valueToFind (CAL type: <code>Cal.Core.Prelude.Eq a => a</code>)
		 * @param array (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Collections.Array.Array a</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the index of the first occurrence of <code>valueToFind</code> in <code>array</code> or -1 if the value does not occur.
		 */
		public static final SourceModel.Expr indexOf(SourceModel.Expr valueToFind, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.indexOf), valueToFind, array});
		}

		/**
		 * Name binding for function: indexOf.
		 * @see #indexOf(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName indexOf = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "indexOf");

		/**
		 * Finds the index of the first occurrence at index greater than or equal to <code>fromIndex</code> of <code>valueToFind</code> in <code>array</code>
		 * or -1 if the value does not occur from <code>fromIndex</code> onwards.
		 * <p>
		 * There are no invalid values of <code>fromIndex</code>. <code>fromIndex &lt; 0</code> is taken as starting the search from the start of <code>array</code>
		 * while <code>fromIndex &gt;= length array</code> always returns -1. 
		 * 
		 * @param valueToFind (CAL type: <code>Cal.Core.Prelude.Eq a => a</code>)
		 * @param fromIndex (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          index to start searching from.
		 * @param array (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Collections.Array.Array a</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the index of the first occurrence at index greater than or equal to <code>fromIndex</code> of <code>valueToFind</code> in <code>array</code>
		 * or -1 if the value does not occur from <code>fromIndex</code> onwards.
		 */
		public static final SourceModel.Expr indexOfFrom(SourceModel.Expr valueToFind, SourceModel.Expr fromIndex, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.indexOfFrom), valueToFind, fromIndex, array});
		}

		/**
		 * @see #indexOfFrom(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param valueToFind
		 * @param fromIndex
		 * @param array
		 * @return the SourceModel.Expr representing an application of indexOfFrom
		 */
		public static final SourceModel.Expr indexOfFrom(SourceModel.Expr valueToFind, int fromIndex, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.indexOfFrom), valueToFind, SourceModel.Expr.makeIntValue(fromIndex), array});
		}

		/**
		 * Name binding for function: indexOfFrom.
		 * @see #indexOfFrom(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName indexOfFrom = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "indexOfFrom");

		/**
		 * Inputs an array from a Java array.
		 * <p>
		 * In the case that a is one of the <code>Cal.Core.Prelude</code> types <code>Cal.Core.Prelude.Boolean</code>, <code>Cal.Core.Prelude.Byte</code>, <code>Cal.Core.Prelude.Short</code>, <code>Cal.Core.Prelude.Int</code>,
		 * <code>Cal.Core.Prelude.Long</code>, <code>Cal.Core.Prelude.Float</code> or <code>Cal.Core.Prelude.Double</code>, the array must be a Java primitive array. For example,
		 * <code>Cal.Collections.Array.Array Cal.Core.Prelude.Int</code> will be input from a Java object of Java type <code>[int]</code>
		 * (and not <code>[java.util.Integer]</code>).
		 * <p>
		 * In the case where <code>a</code> is some other type, then the input format is a Java array whose elements
		 * are the Java objects required to input via <code>Cal.Core.Prelude.input</code> for the element values. 
		 * 
		 * @param arrayAsObject (CAL type: <code>Cal.Core.Prelude.JObject</code>)
		 * @return (CAL type: <code>(Cal.Core.Prelude.Inputable a, Cal.Core.Prelude.Typeable a) => Cal.Collections.Array.Array a</code>) 
		 */
		public static final SourceModel.Expr inputPrimitive(SourceModel.Expr arrayAsObject) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inputPrimitive), arrayAsObject});
		}

		/**
		 * Name binding for function: inputPrimitive.
		 * @see #inputPrimitive(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName inputPrimitive = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "inputPrimitive");

		/**
		 * This function takes an element and an array and inserts the element into the
		 * array at the last position where it is still less than or equal to the next
		 * element. All order comparisons are done using the <code>Cal.Core.Prelude.compare</code> class
		 * method.
		 * <p>
		 * If the array is sorted before the call, the result will also be sorted.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.Array.insertBy
		 * </dl>
		 * 
		 * @param value (CAL type: <code>Cal.Core.Prelude.Ord a => a</code>)
		 *          the value to be inserted into the array.
		 * @param array (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Collections.Array.Array a</code>)
		 *          the array.
		 * @return (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Collections.Array.Array a</code>) 
		 *          a copy of the input array with the specified value inserted.
		 */
		public static final SourceModel.Expr insert(SourceModel.Expr value, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.insert), value, array});
		}

		/**
		 * Name binding for function: insert.
		 * @see #insert(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName insert = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "insert");

		/**
		 * Inserts values into a array at the specified index.
		 * @param index (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the zero-based index.
		 * @param valuesToInsert (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          the values to be inserted.
		 * @param array (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          the array to which the values are to be inserted.
		 * @return (CAL type: <code>Cal.Collections.Array.Array a</code>) 
		 *          the array with the values inserted at the specified index.
		 */
		public static final SourceModel.Expr insertArrayAt(SourceModel.Expr index, SourceModel.Expr valuesToInsert, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.insertArrayAt), index, valuesToInsert, array});
		}

		/**
		 * @see #insertArrayAt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param index
		 * @param valuesToInsert
		 * @param array
		 * @return the SourceModel.Expr representing an application of insertArrayAt
		 */
		public static final SourceModel.Expr insertArrayAt(int index, SourceModel.Expr valuesToInsert, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.insertArrayAt), SourceModel.Expr.makeIntValue(index), valuesToInsert, array});
		}

		/**
		 * Name binding for function: insertArrayAt.
		 * @see #insertArrayAt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName insertArrayAt = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "insertArrayAt");

		/**
		 * Inserts a value into a array at the specified index.
		 * @param index (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the zero-based index.
		 * @param value (CAL type: <code>a</code>)
		 *          the value to be inserted.
		 * @param array (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          the array to which the value is to be inserted.
		 * @return (CAL type: <code>Cal.Collections.Array.Array a</code>) 
		 *          the array with the value inserted at the specified index.
		 */
		public static final SourceModel.Expr insertAt(SourceModel.Expr index, SourceModel.Expr value, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.insertAt), index, value, array});
		}

		/**
		 * @see #insertAt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param index
		 * @param value
		 * @param array
		 * @return the SourceModel.Expr representing an application of insertAt
		 */
		public static final SourceModel.Expr insertAt(int index, SourceModel.Expr value, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.insertAt), SourceModel.Expr.makeIntValue(index), value, array});
		}

		/**
		 * Name binding for function: insertAt.
		 * @see #insertAt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName insertAt = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "insertAt");

		/**
		 * This function takes an element and an array and inserts the element into the
		 * array at the last position where it is still less than or equal to the next
		 * element. All order comparisons are done using the supplied comparison
		 * function.
		 * <p>
		 * If the array is sorted before the call, the result will also be sorted.
		 * 
		 * @param comparisonFunction (CAL type: <code>a -> a -> Cal.Core.Prelude.Ordering</code>)
		 *          the comparison function.
		 * @param value (CAL type: <code>a</code>)
		 *          the value to be inserted into the array.
		 * @param array (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          the array.
		 * @return (CAL type: <code>Cal.Collections.Array.Array a</code>) 
		 *          a copy of the input array with the specified value inserted.
		 */
		public static final SourceModel.Expr insertBy(SourceModel.Expr comparisonFunction, SourceModel.Expr value, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.insertBy), comparisonFunction, value, array});
		}

		/**
		 * Name binding for function: insertBy.
		 * @see #insertBy(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName insertBy = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "insertBy");

		/**
		 * <code>isElem elementValue array</code> returns <code>Cal.Core.Prelude.True</code> if <code>elementValue</code> is an element of the array.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.Array.isElemBy
		 * </dl>
		 * 
		 * @param elementValue (CAL type: <code>Cal.Core.Prelude.Eq a => a</code>)
		 *          the value to be tested for membership in the array.
		 * @param array (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Collections.Array.Array a</code>)
		 *          the array to be checked.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if <code>elementValue</code> is an element of the array.
		 */
		public static final SourceModel.Expr isElem(SourceModel.Expr elementValue, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isElem), elementValue, array});
		}

		/**
		 * Name binding for function: isElem.
		 * @see #isElem(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isElem = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "isElem");

		/**
		 * Returns whether a value is an element of an array, according to the specified equality comparison function.
		 * @param eq (CAL type: <code>a -> b -> Cal.Core.Prelude.Boolean</code>)
		 *          the equality comparison function to use in comparing the array elements.
		 * @param x (CAL type: <code>a</code>)
		 *          the value to be tested for membership in the array.
		 * @param array (CAL type: <code>Cal.Collections.Array.Array b</code>)
		 *          the array to be checked.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if <code>x</code> is an element of the array according to <code>eq</code>; <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr isElemBy(SourceModel.Expr eq, SourceModel.Expr x, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isElemBy), eq, x, array});
		}

		/**
		 * Name binding for function: isElemBy.
		 * @see #isElemBy(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isElemBy = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "isElemBy");

		/**
		 * <code>isNotElem elementValue array</code> returns <code>Cal.Core.Prelude.True</code> if <code>elementValue</code> is not an element of the array.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.Array.isElem
		 * </dl>
		 * 
		 * @param elementValue (CAL type: <code>Cal.Core.Prelude.Eq a => a</code>)
		 *          the value to be tested for membership in the array.
		 * @param array (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Collections.Array.Array a</code>)
		 *          the array to be checked.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if <code>elementValue</code> is not an element of the array.
		 */
		public static final SourceModel.Expr isNotElem(SourceModel.Expr elementValue, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isNotElem), elementValue, array});
		}

		/**
		 * Name binding for function: isNotElem.
		 * @see #isNotElem(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isNotElem = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "isNotElem");

		/**
		 * Returns the last element of the specified array. This function is O(1).
		 * Terminates in an error on an empty array.
		 * @param array (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          the array whose last element is to be returned.
		 * @return (CAL type: <code>a</code>) 
		 *          the last element of the array.
		 */
		public static final SourceModel.Expr last(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.last), array});
		}

		/**
		 * Name binding for function: last.
		 * @see #last(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName last = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "last");

		/**
		 * Finds the index of the last occurrence of <code>valueToFind</code> in <code>array</code> or -1 if the value does not occur.
		 * @param valueToFind (CAL type: <code>Cal.Core.Prelude.Eq a => a</code>)
		 * @param array (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Collections.Array.Array a</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the index of the last occurrence of <code>valueToFind</code> in <code>array</code> or -1 if the value does not occur.
		 */
		public static final SourceModel.Expr lastIndexOf(SourceModel.Expr valueToFind, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lastIndexOf), valueToFind, array});
		}

		/**
		 * Name binding for function: lastIndexOf.
		 * @see #lastIndexOf(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName lastIndexOf = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "lastIndexOf");

		/**
		 * Finds the index of the last occurrence at index less than or equal to <code>fromIndex</code> of <code>valueToFind</code>
		 * in <code>array</code> or -1 if the value does not occur from <code>fromIndex</code> and earlier.
		 * <p>
		 * There are no invalid values of <code>fromIndex</code>. <code>fromIndex &lt; 0</code> always returns -1, and
		 * <code>fromIndex &gt;= length array</code> is taken as starting the search from the last index of <code>array</code>.
		 * 
		 * @param valueToFind (CAL type: <code>Cal.Core.Prelude.Eq a => a</code>)
		 * @param fromIndex (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          index to start searching from.
		 * @param array (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Collections.Array.Array a</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the index of the last occurrence at index less than or equal to <code>fromIndex</code> of <code>valueToFind</code>
		 * in <code>array</code> or -1 if the value does not occur from <code>fromIndex</code> and earlier.
		 */
		public static final SourceModel.Expr lastIndexOfFrom(SourceModel.Expr valueToFind, SourceModel.Expr fromIndex, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lastIndexOfFrom), valueToFind, fromIndex, array});
		}

		/**
		 * @see #lastIndexOfFrom(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param valueToFind
		 * @param fromIndex
		 * @param array
		 * @return the SourceModel.Expr representing an application of lastIndexOfFrom
		 */
		public static final SourceModel.Expr lastIndexOfFrom(SourceModel.Expr valueToFind, int fromIndex, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lastIndexOfFrom), valueToFind, SourceModel.Expr.makeIntValue(fromIndex), array});
		}

		/**
		 * Name binding for function: lastIndexOfFrom.
		 * @see #lastIndexOfFrom(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName lastIndexOfFrom = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "lastIndexOfFrom");

		/**
		 * Returns the length of the specified array. This function is O(1) in time (unlike length for lists which is O(n)
		 * where n is the length of the list).
		 * @param array (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          the array whose length is to be returned.
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the length of the array.
		 */
		public static final SourceModel.Expr length(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.length), array});
		}

		/**
		 * Name binding for function: length.
		 * @see #length(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName length = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "length");

		/**
		 * Creates an <code>Array</code> calling a function for each of the indices in the resultant array
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Type Constructors:</b> Cal.Collections.Array.Array
		 * </dl>
		 * 
		 * @param size (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          The number of items to include in the array
		 * @param itemFunction (CAL type: <code>Cal.Core.Prelude.Int -> a</code>)
		 *          A function applied for [0, 1, 2, ...  <code>size</code>-1].
		 * @return (CAL type: <code>Cal.Collections.Array.Array a</code>) 
		 *          an array of <code>a</code>.
		 */
		public static final SourceModel.Expr makeArrayFromIndices(SourceModel.Expr size, SourceModel.Expr itemFunction) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeArrayFromIndices), size, itemFunction});
		}

		/**
		 * @see #makeArrayFromIndices(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param size
		 * @param itemFunction
		 * @return the SourceModel.Expr representing an application of makeArrayFromIndices
		 */
		public static final SourceModel.Expr makeArrayFromIndices(int size, SourceModel.Expr itemFunction) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeArrayFromIndices), SourceModel.Expr.makeIntValue(size), itemFunction});
		}

		/**
		 * Name binding for function: makeArrayFromIndices.
		 * @see #makeArrayFromIndices(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeArrayFromIndices = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "makeArrayFromIndices");

		/**
		 * <code>map mapFunction array</code> applies the function <code>mapFunction</code> to each element of array and returns the
		 * resulting array.
		 * @param mapFunction (CAL type: <code>a -> b</code>)
		 *          a function to be applied to each element of the array.
		 * @param array (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          the array to which the mapFunction is applied element-wise.
		 * @return (CAL type: <code>Cal.Collections.Array.Array b</code>) 
		 *          the array obtained by applying <code>mapFunction</code> to each element of the array.
		 */
		public static final SourceModel.Expr map(SourceModel.Expr mapFunction, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.map), mapFunction, array});
		}

		/**
		 * Name binding for function: map.
		 * @see #map(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName map = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "map");

		/**
		 * <code>mapIndexed mapFunction array</code> applies the function <code>mapFunction</code> to each element of the array, where
		 * <code>mapFunction</code> is passed both the element value, and its zero-based index in the array.
		 * @param mapFunction (CAL type: <code>a -> Cal.Core.Prelude.Int -> b</code>)
		 *          a function to be applied to each element of the array, being passed both the element value, and its
		 * zero-based index in the array.
		 * @param array (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          the array.
		 * @return (CAL type: <code>Cal.Collections.Array.Array b</code>) 
		 *          the array obtained by applying <code>mapFunction</code> to each element of the array.
		 */
		public static final SourceModel.Expr mapIndexed(SourceModel.Expr mapFunction, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.mapIndexed), mapFunction, array});
		}

		/**
		 * Name binding for function: mapIndexed.
		 * @see #mapIndexed(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName mapIndexed = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "mapIndexed");

		/**
		 * Returns the maximum value in the specified array.
		 * Terminates in an error on an empty array.
		 * @param array (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Collections.Array.Array a</code>)
		 *          the array whose maximum value is to be returned.
		 * @return (CAL type: <code>Cal.Core.Prelude.Ord a => a</code>) 
		 *          the maximum value in the array.
		 */
		public static final SourceModel.Expr maximum(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.maximum), array});
		}

		/**
		 * Name binding for function: maximum.
		 * @see #maximum(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName maximum = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "maximum");

		/**
		 * This function takes a comparison function and a array and returns the greatest
		 * element of the array by the comparison function. The array must be non-empty.
		 * @param comparisonFunction (CAL type: <code>a -> a -> Cal.Core.Prelude.Ordering</code>)
		 *          the comparison function.
		 * @param array (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          a non-empty array.
		 * @return (CAL type: <code>a</code>) 
		 *          the greatest element of the array by the comparison function.
		 */
		public static final SourceModel.Expr maximumBy(SourceModel.Expr comparisonFunction, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.maximumBy), comparisonFunction, array});
		}

		/**
		 * Name binding for function: maximumBy.
		 * @see #maximumBy(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName maximumBy = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "maximumBy");

		/**
		 * Assuming that <code>array1</code> and <code>array2</code> are sorted, <code>merge array1 array2</code> will merge all
		 * the elements of the two arrays into a single sorted array. If an element of <code>array1</code> is equal
		 * to an element of <code>array2</code>, then the element of <code>array1</code> will be before the element of <code>array2</code>
		 * in the merged result.
		 * @param array1 (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Collections.Array.Array a</code>)
		 *          assumed to be sorted
		 * @param array2 (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Collections.Array.Array a</code>)
		 *          assumed to be sorted
		 * @return (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Collections.Array.Array a</code>) 
		 *          the result of merging <code>array1</code> and <code>array2</code>. Will be a sorted array.
		 */
		public static final SourceModel.Expr merge(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.merge), array1, array2});
		}

		/**
		 * Name binding for function: merge.
		 * @see #merge(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName merge = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "merge");

		/**
		 * Assuming that <code>array1</code> and <code>array2</code> are sorted according to <code>comparator</code>,
		 * <code>mergeBy comparator array1 array2</code> will merge all the elements of the two arrays into
		 * a single array sorted according to <code>comparator</code>. If an element of <code>array1</code> is equal
		 * to an element of <code>array2</code> under the comparator, then the element of <code>array1</code> will
		 * be before the element of <code>array2</code> in the merged result.
		 * @param comparator (CAL type: <code>a -> a -> Cal.Core.Prelude.Ordering</code>)
		 *          comparison function to define the order of elements.
		 * @param array1 (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          assumed to be sorted according to 'comparator'
		 * @param array2 (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          assumed to be sorted according to 'comparator'
		 * @return (CAL type: <code>Cal.Collections.Array.Array a</code>) 
		 *          the result of merging <code>array1</code> and <code>array2</code>. Will be sorted according to <code>comparator</code>.
		 */
		public static final SourceModel.Expr mergeBy(SourceModel.Expr comparator, SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.mergeBy), comparator, array1, array2});
		}

		/**
		 * Name binding for function: mergeBy.
		 * @see #mergeBy(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName mergeBy = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "mergeBy");

		/**
		 * Returns the minimum value in the specified array.
		 * Terminates in an error on an empty array.
		 * @param array (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Collections.Array.Array a</code>)
		 *          the array whose minimum value is to be returned.
		 * @return (CAL type: <code>Cal.Core.Prelude.Ord a => a</code>) 
		 *          the minimum value in the array.
		 */
		public static final SourceModel.Expr minimum(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.minimum), array});
		}

		/**
		 * Name binding for function: minimum.
		 * @see #minimum(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName minimum = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "minimum");

		/**
		 * This function takes a comparison function and a array and returns the least
		 * element of the array by the comparison function. The array must be non-empty.
		 * @param comparisonFunction (CAL type: <code>a -> a -> Cal.Core.Prelude.Ordering</code>)
		 *          the comparison function.
		 * @param array (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          a non-empty array.
		 * @return (CAL type: <code>a</code>) 
		 *          the least element of the array by the comparison function.
		 */
		public static final SourceModel.Expr minimumBy(SourceModel.Expr comparisonFunction, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.minimumBy), comparisonFunction, array});
		}

		/**
		 * Name binding for function: minimumBy.
		 * @see #minimumBy(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName minimumBy = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "minimumBy");

		/**
		 * <code>orArray array</code> returns <code>Cal.Core.Prelude.True</code> if at least one element of the array is <code>Cal.Core.Prelude.True</code>.
		 * @param array (CAL type: <code>Cal.Collections.Array.Array Cal.Core.Prelude.Boolean</code>)
		 *          the array whose elements are to be tested.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if at least one element of the array is <code>Cal.Core.Prelude.True</code>.
		 */
		public static final SourceModel.Expr orArray(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.orArray), array});
		}

		/**
		 * Name binding for function: orArray.
		 * @see #orArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName orArray = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "orArray");

		/**
		 * Outputs the given array to a Java array.
		 * <p>
		 * In the case that a is one of the <code>Cal.Core.Prelude</code> types <code>Cal.Core.Prelude.Boolean</code>, <code>Cal.Core.Prelude.Byte</code>, <code>Cal.Core.Prelude.Short</code>, <code>Cal.Core.Prelude.Int</code>,
		 * <code>Cal.Core.Prelude.Long</code>, <code>Cal.Core.Prelude.Float</code> or <code>Cal.Core.Prelude.Double</code>, the array will be a Java primitive array. For example,
		 * <code>Cal.Collections.Array.Array Cal.Core.Prelude.Int</code> will be output to a Java object of Java type <code>[int]</code>
		 * (and not <code>[java.util.Integer]</code>).
		 * <p>
		 * In the case where <code>a</code> is some other type, then the result will be a Java array whose elements are
		 * Java objects determined by applying <code>Cal.Core.Prelude.output</code> to each of the elements of the CAL array. 
		 * 
		 * @param array (CAL type: <code>(Cal.Core.Prelude.Outputable a, Cal.Core.Prelude.Typeable a) => Cal.Collections.Array.Array a</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.JObject</code>) 
		 */
		public static final SourceModel.Expr outputPrimitive(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputPrimitive), array});
		}

		/**
		 * Name binding for function: outputPrimitive.
		 * @see #outputPrimitive(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName outputPrimitive = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "outputPrimitive");

		/**
		 * Periodizes an array at the specified frequency, returning an array of arrays, one for each modulo of the frequency.
		 * @param frequency (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the frequency.
		 * @param array (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          the array to be periodized.
		 * @return (CAL type: <code>Cal.Collections.Array.Array (Cal.Collections.Array.Array a)</code>) 
		 *          an array of arrays, one for each modulo of the frequency.
		 */
		public static final SourceModel.Expr periodize(SourceModel.Expr frequency, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.periodize), frequency, array});
		}

		/**
		 * @see #periodize(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param frequency
		 * @param array
		 * @return the SourceModel.Expr representing an application of periodize
		 */
		public static final SourceModel.Expr periodize(int frequency, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.periodize), SourceModel.Expr.makeIntValue(frequency), array});
		}

		/**
		 * Name binding for function: periodize.
		 * @see #periodize(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName periodize = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "periodize");

		/**
		 * <code>product array</code> return the product of all the elements of <code>array</code>, multiplying from left to right.
		 * <code>product</code> applied to an empty array is 1.
		 * @param array (CAL type: <code>Cal.Core.Prelude.Num a => Cal.Collections.Array.Array a</code>)
		 *          the array whose elements are to be multiplied.
		 * @return (CAL type: <code>Cal.Core.Prelude.Num a => a</code>) 
		 *          the product of all the elements of array, multiplying from left to right, or 1 if the array is empty.
		 */
		public static final SourceModel.Expr product(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.product), array});
		}

		/**
		 * Name binding for function: product.
		 * @see #product(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName product = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "product");

		/**
		 * Applies the predicate to each element and returns the array 
		 * where every element for which the predicate evaluated to <code>Cal.Core.Prelude.True</code> is replaced by <code>replaceWithValue</code>.
		 * @param array (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          the array whose elements are to be checked and replaced.
		 * @param predicate (CAL type: <code>a -> Cal.Core.Prelude.Boolean</code>)
		 *          a predicate to be applied to the elements of the array.
		 * @param replaceWithValue (CAL type: <code>a</code>)
		 *          the value to replace existing elements in the array.
		 * @return (CAL type: <code>Cal.Collections.Array.Array a</code>) 
		 *          a copy of the array where every element for which the predicate evaluated to <code>Cal.Core.Prelude.True</code> is replaced by
		 * <code>replaceWithValue</code>.
		 */
		public static final SourceModel.Expr replaceAll(SourceModel.Expr array, SourceModel.Expr predicate, SourceModel.Expr replaceWithValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.replaceAll), array, predicate, replaceWithValue});
		}

		/**
		 * Name binding for function: replaceAll.
		 * @see #replaceAll(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName replaceAll = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "replaceAll");

		/**
		 * Returns the array with the value at a specified offset replaced by a specified value.
		 * Specifying a negative offset, or an offset larger than the last element of the array 
		 * causes a runtime error to be signalled.
		 * @param index (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          The index of the element to replace
		 * @param value (CAL type: <code>a</code>)
		 *          The value to replace the element with
		 * @param array (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          The array to replace an element of
		 * @return (CAL type: <code>Cal.Collections.Array.Array a</code>) 
		 *          a copy of <code>array</code> with <code>value</code> at <code>index</code> instead of the original value.
		 */
		public static final SourceModel.Expr replaceAt(SourceModel.Expr index, SourceModel.Expr value, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.replaceAt), index, value, array});
		}

		/**
		 * @see #replaceAt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param index
		 * @param value
		 * @param array
		 * @return the SourceModel.Expr representing an application of replaceAt
		 */
		public static final SourceModel.Expr replaceAt(int index, SourceModel.Expr value, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.replaceAt), SourceModel.Expr.makeIntValue(index), value, array});
		}

		/**
		 * Name binding for function: replaceAt.
		 * @see #replaceAt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName replaceAt = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "replaceAt");

		/**
		 * <code>replicate nCopies valueToReplicate</code> is an array of length <code>nCopies</code>, with every element equal to
		 * <code>valueToReplicate</code>.
		 * @param nCopies (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the number of copies.
		 * @param valueToReplicate (CAL type: <code>a</code>)
		 *          the value to be replicated.
		 * @return (CAL type: <code>Cal.Collections.Array.Array a</code>) 
		 *          an array of length <code>nCopies</code>, with every element equal to <code>valueToReplicate</code>.
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
			QualifiedName.make(CAL_Array.MODULE_NAME, "replicate");

		/**
		 * Replicates a array for a specified number of times.
		 * @param array (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          the array.
		 * @param nCopies (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the number of copies to make.
		 * @return (CAL type: <code>Cal.Collections.Array.Array a</code>) 
		 *          an array of <code>nCopies</code> of the array concatenated together.
		 */
		public static final SourceModel.Expr replicateArray(SourceModel.Expr array, SourceModel.Expr nCopies) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.replicateArray), array, nCopies});
		}

		/**
		 * @see #replicateArray(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param nCopies
		 * @return the SourceModel.Expr representing an application of replicateArray
		 */
		public static final SourceModel.Expr replicateArray(SourceModel.Expr array, int nCopies) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.replicateArray), array, SourceModel.Expr.makeIntValue(nCopies)});
		}

		/**
		 * Name binding for function: replicateArray.
		 * @see #replicateArray(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName replicateArray = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "replicateArray");

		/**
		 * Reverses the elements of an array. <code>reverse</code> is O(n) time where n is the length of the array.
		 * @param array (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          the array to be reversed.
		 * @return (CAL type: <code>Cal.Collections.Array.Array a</code>) 
		 *          an array containing the elements in the specified array, in reverse order.
		 */
		public static final SourceModel.Expr reverse(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.reverse), array});
		}

		/**
		 * Name binding for function: reverse.
		 * @see #reverse(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName reverse = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "reverse");

		/**
		 * Sorts the specified array. All order comparisons are done using the
		 * <code>Cal.Core.Prelude.compare</code> class method.
		 * <p>
		 * This function implements a stable sort in that items which evaluate to <code>Cal.Core.Prelude.EQ</code>
		 * under the <code>Cal.Core.Prelude.compare</code> class method preserve their original ordering in the original
		 * array.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.Array.sortBy
		 * </dl>
		 * 
		 * @param array (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Collections.Array.Array a</code>)
		 *          the array to be sorted.
		 * @return (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Collections.Array.Array a</code>) 
		 *          the sorted array.
		 */
		public static final SourceModel.Expr sort(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.sort), array});
		}

		/**
		 * Name binding for function: sort.
		 * @see #sort(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName sort = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "sort");

		/**
		 * Sorts the specified array according to the specified comparison function. All
		 * order comparisons are done using the supplied comparison function.
		 * <p>
		 * This function implements a stable sort in that items which evaluate to <code>Cal.Core.Prelude.EQ</code>
		 * under the <code>comparisonFunction</code> preserve their original ordering in the original
		 * array.
		 * 
		 * @param comparisonFunction (CAL type: <code>a -> a -> Cal.Core.Prelude.Ordering</code>)
		 *          the comparison function to use in determining the
		 * order of the elements.
		 * @param array (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          the array to be sorted.
		 * @return (CAL type: <code>Cal.Collections.Array.Array a</code>) 
		 *          the sorted array.
		 */
		public static final SourceModel.Expr sortBy(SourceModel.Expr comparisonFunction, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.sortBy), comparisonFunction, array});
		}

		/**
		 * Name binding for function: sortBy.
		 * @see #sortBy(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName sortBy = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "sortBy");

		/**
		 * <code>span predicate array</code> breaks up <code>array</code> into a pair of arrays. The start of the second array is
		 * the first element of array on which predicate is False.
		 * @param predicate (CAL type: <code>a -> Cal.Core.Prelude.Boolean</code>)
		 *          a predicate to be applied to the elements of the array.
		 * @param array (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          the array to be broken up into a pair of arrays.
		 * @return (CAL type: <code>(Cal.Collections.Array.Array a, Cal.Collections.Array.Array a)</code>) 
		 *          a pair of arrays, where the first array is the longest prefix of the array for which predicate
		 * is <code>Cal.Core.Prelude.True</code> for each element, and the second array contains the remaining elements of the original array.
		 */
		public static final SourceModel.Expr span(SourceModel.Expr predicate, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.span), predicate, array});
		}

		/**
		 * Name binding for function: span.
		 * @see #span(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName span = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "span");

		/**
		 * <code>spanInclusive predicate array</code> breaks up array into a pair of arrays. The last item in the first array is the first element
		 * of array on which predicate is <code>Cal.Core.Prelude.False</code>.
		 * @param predicate (CAL type: <code>a -> Cal.Core.Prelude.Boolean</code>)
		 *          a predicate to be applied to the elements of the array.
		 * @param array (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          the array to be split.
		 * @return (CAL type: <code>(Cal.Collections.Array.Array a, Cal.Collections.Array.Array a)</code>) 
		 *          a pair of arrays. The last item in the first array is the first element of array
		 * on which predicate is <code>Cal.Core.Prelude.False</code>. The second array contains the remaining
		 * elements of the original array.
		 */
		public static final SourceModel.Expr spanInclusive(SourceModel.Expr predicate, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.spanInclusive), predicate, array});
		}

		/**
		 * Name binding for function: spanInclusive.
		 * @see #spanInclusive(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName spanInclusive = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "spanInclusive");

		/**
		 * <code>splitAt nElemsToSplit list</code> returns a pair of arrays. The first consists of the first <code>nElemsToSplit</code>
		 * elements of array, the second consists of the remaining elements.
		 * @param nElementsToSplit (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the number of elements to be returned in the first array.
		 * @param array (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          the array to be split.
		 * @return (CAL type: <code>(Cal.Collections.Array.Array a, Cal.Collections.Array.Array a)</code>) 
		 *          a pair of arrays. The first consists of the first <code>nElementsToSplit</code> elements of array,
		 * the second consists of the remaining elements.
		 */
		public static final SourceModel.Expr splitAt(SourceModel.Expr nElementsToSplit, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.splitAt), nElementsToSplit, array});
		}

		/**
		 * @see #splitAt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param nElementsToSplit
		 * @param array
		 * @return the SourceModel.Expr representing an application of splitAt
		 */
		public static final SourceModel.Expr splitAt(int nElementsToSplit, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.splitAt), SourceModel.Expr.makeIntValue(nElementsToSplit), array});
		}

		/**
		 * Name binding for function: splitAt.
		 * @see #splitAt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName splitAt = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "splitAt");

		/**
		 * Returns whether <code>arrayToTest</code> starts with the specified prefix.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.Array.startsWithBy
		 * </dl>
		 * 
		 * @param prefix (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Collections.Array.Array a</code>)
		 * @param arrayToTest (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Collections.Array.Array a</code>)
		 *          the array to be tested for starting with <code>prefix</code>.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if <code>arrayToTest</code> starts with the array <code>prefix</code>.
		 */
		public static final SourceModel.Expr startsWith(SourceModel.Expr prefix, SourceModel.Expr arrayToTest) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.startsWith), prefix, arrayToTest});
		}

		/**
		 * Name binding for function: startsWith.
		 * @see #startsWith(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName startsWith = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "startsWith");

		/**
		 * Returns whether <code>arrayToTest</code> starts with the specified prefix using <code>eq</code> as the notion of equality between 
		 * elements of the prefix and elements of the arrayToTest.
		 * @param eq (CAL type: <code>a -> b -> Cal.Core.Prelude.Boolean</code>)
		 *          the equality comparison function to use in comparing the array elements for equality.
		 * @param prefix (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 * @param arrayToTest (CAL type: <code>Cal.Collections.Array.Array b</code>)
		 *          the array to be tested for starting with <code>prefix</code>.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if <code>arrayToTest</code> starts with the array <code>prefix</code> using <code>eq</code> for comparing array
		 * elements for equality.
		 */
		public static final SourceModel.Expr startsWithBy(SourceModel.Expr eq, SourceModel.Expr prefix, SourceModel.Expr arrayToTest) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.startsWithBy), eq, prefix, arrayToTest});
		}

		/**
		 * Name binding for function: startsWithBy.
		 * @see #startsWithBy(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName startsWithBy = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "startsWithBy");

		/**
		 * Extracts a portion of the argument array as a new array.
		 * <p>
		 * If <code>fromIndex &lt; 0</code>, <code>fromIndex &gt; toIndex</code>, or <code>toIndex &gt; length array</code>, the function
		 * terminates in an error. Use the <code>Cal.Collections.Array.length</code> function to pre-check to avoid this
		 * if necessary.
		 * 
		 * @param array (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          to extract a sub-array from
		 * @param fromIndex (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          inclusive 0-based index.
		 * @param toIndex (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          exclusive 0-based index
		 * @return (CAL type: <code>Cal.Collections.Array.Array a</code>) 
		 *          copy of the array from <code>fromIndex</code> (inclusive) to <code>toIndex</code> (exclusive).
		 */
		public static final SourceModel.Expr subArray(SourceModel.Expr array, SourceModel.Expr fromIndex, SourceModel.Expr toIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.subArray), array, fromIndex, toIndex});
		}

		/**
		 * @see #subArray(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param fromIndex
		 * @param toIndex
		 * @return the SourceModel.Expr representing an application of subArray
		 */
		public static final SourceModel.Expr subArray(SourceModel.Expr array, int fromIndex, int toIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.subArray), array, SourceModel.Expr.makeIntValue(fromIndex), SourceModel.Expr.makeIntValue(toIndex)});
		}

		/**
		 * Name binding for function: subArray.
		 * @see #subArray(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName subArray = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "subArray");

		/**
		 * <code>subscript array index</code> returns the nth element of the array where n = <code>index</code>. The indices start at 0.
		 * If the index is negative or is greater than or equal to the length of the array, an error results.
		 * <p>
		 * <code>subscript</code> is O(1) i.e. constant time unlike subscript for lists which is O(n).
		 * 
		 * @param array (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          the array to be accessed.
		 * @param index (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the index of the array element to be returned.
		 * @return (CAL type: <code>a</code>) 
		 *          the element in the array at the position indicated by <code>index</code>.
		 */
		public static final SourceModel.Expr subscript(SourceModel.Expr array, SourceModel.Expr index) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.subscript), array, index});
		}

		/**
		 * @see #subscript(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param index
		 * @return the SourceModel.Expr representing an application of subscript
		 */
		public static final SourceModel.Expr subscript(SourceModel.Expr array, int index) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.subscript), array, SourceModel.Expr.makeIntValue(index)});
		}

		/**
		 * Name binding for function: subscript.
		 * @see #subscript(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName subscript = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "subscript");

		/**
		 * <code>sum array</code> returns the sum of all the elements of <code>array</code>, adding from left to right. <code>sum</code> applied
		 * to an empty array is 0.
		 * @param array (CAL type: <code>Cal.Core.Prelude.Num a => Cal.Collections.Array.Array a</code>)
		 *          the array whose elements are to be summed.
		 * @return (CAL type: <code>Cal.Core.Prelude.Num a => a</code>) 
		 *          the sum of all the elements of the array, adding from left to right, or 0 if the array is empty.
		 */
		public static final SourceModel.Expr sum(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.sum), array});
		}

		/**
		 * Name binding for function: sum.
		 * @see #sum(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName sum = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "sum");

		/**
		 * <code>take nElements array</code> returns an array consisting of the first <code>nElements</code> elements of <code>array</code>.
		 * If the array has fewer than <code>nElements</code> elements, it just returns the array.
		 * @param nElementsToTake (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the number of elements to take.
		 * @param array (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          the array from which elements are to be taken.
		 * @return (CAL type: <code>Cal.Collections.Array.Array a</code>) 
		 *          a array of the requested elements from the array.
		 */
		public static final SourceModel.Expr take(SourceModel.Expr nElementsToTake, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.take), nElementsToTake, array});
		}

		/**
		 * @see #take(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param nElementsToTake
		 * @param array
		 * @return the SourceModel.Expr representing an application of take
		 */
		public static final SourceModel.Expr take(int nElementsToTake, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.take), SourceModel.Expr.makeIntValue(nElementsToTake), array});
		}

		/**
		 * Name binding for function: take.
		 * @see #take(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName take = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "take");

		/**
		 * <code>takeWhile takeWhileTrueFunction array</code> returns the longest prefix of the array for which <code>takeWhileTrueFunction</code>
		 * is <code>Cal.Core.Prelude.True</code> for each element.
		 * <p>
		 * e.g. <code>takeWhile Cal.Core.Prelude.isEven (Cal.Collections.Array.fromList [6, 2, 1, 2]) = (Cal.Collections.Array.fromList [6, 2])</code>
		 * 
		 * @param takeWhileTrueFunction (CAL type: <code>a -> Cal.Core.Prelude.Boolean</code>)
		 *          a predicate to be applied to the elements of the array.
		 * @param array (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          the array from which elements are to be taken.
		 * @return (CAL type: <code>Cal.Collections.Array.Array a</code>) 
		 *          the longest prefix of the array for which <code>takeWhileTrueFunction</code> is True 
		 * for each element.
		 */
		public static final SourceModel.Expr takeWhile(SourceModel.Expr takeWhileTrueFunction, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.takeWhile), takeWhileTrueFunction, array});
		}

		/**
		 * Name binding for function: takeWhile.
		 * @see #takeWhile(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName takeWhile = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "takeWhile");

		/**
		 * Unit test for the <code>Array</code> module.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the unit tests for the <code>Array</code> module pass.
		 */
		public static final SourceModel.Expr testModule() {
			return SourceModel.Expr.Var.make(Functions.testModule);
		}

		/**
		 * Name binding for function: testModule.
		 * @see #testModule()
		 */
		public static final QualifiedName testModule = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "testModule");

		/**
		 * Converts an array to a list.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.Array.toListWith
		 * </dl>
		 * 
		 * @param array (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          the array to convert to a list
		 * @return (CAL type: <code>[a]</code>) 
		 *          the resulting list
		 */
		public static final SourceModel.Expr toList(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toList), array});
		}

		/**
		 * Name binding for function: toList.
		 * @see #toList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName toList = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "toList");

		/**
		 * Converts an array to a list with the help of an element conversion function <code>f</code>
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.Array.toList
		 * </dl>
		 * 
		 * @param f (CAL type: <code>a -> b</code>)
		 *          function to use to transform elements of the array to elements of the list
		 * @param array (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          the array to convert to a list
		 * @return (CAL type: <code>[b]</code>) 
		 *          the resulting list
		 */
		public static final SourceModel.Expr toListWith(SourceModel.Expr f, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toListWith), f, array});
		}

		/**
		 * Name binding for function: toListWith.
		 * @see #toListWith(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName toListWith = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "toListWith");

		/**
		 * Converts an <code>Cal.Collections.Array.Array Cal.Core.Prelude.Char</code> to a <code>Cal.Core.Prelude.String</code>.
		 * @param array (CAL type: <code>Cal.Collections.Array.Array Cal.Core.Prelude.Char</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr toString(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toString), array});
		}

		/**
		 * Name binding for function: toString.
		 * @see #toString(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName toString = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "toString");

		/**
		 * Transforms an array of pairs into a pair of arrays.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.Array.zip
		 * </dl>
		 * 
		 * @param array (CAL type: <code>Cal.Collections.Array.Array (a, b)</code>)
		 *          the array to be unzipped.
		 * @return (CAL type: <code>(Cal.Collections.Array.Array a, Cal.Collections.Array.Array b)</code>) 
		 *          a pair of arrays.
		 */
		public static final SourceModel.Expr unzip(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.unzip), array});
		}

		/**
		 * Name binding for function: unzip.
		 * @see #unzip(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName unzip = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "unzip");

		/**
		 * Transforms an array of triples into a triple of arrays.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.Array.zip
		 * </dl>
		 * 
		 * @param array (CAL type: <code>Cal.Collections.Array.Array (a, b, c)</code>)
		 *          the array to be unzipped.
		 * @return (CAL type: <code>(Cal.Collections.Array.Array a, Cal.Collections.Array.Array b, Cal.Collections.Array.Array c)</code>) 
		 *          a triple of arrays.
		 */
		public static final SourceModel.Expr unzip3(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.unzip3), array});
		}

		/**
		 * Name binding for function: unzip3.
		 * @see #unzip3(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName unzip3 = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "unzip3");

		/**
		 * Transforms an array of 4-tuples into a 4-tuple of arrays.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.Array.zip
		 * </dl>
		 * 
		 * @param array (CAL type: <code>Cal.Collections.Array.Array (a, b, c, d)</code>)
		 *          the array to be unzipped.
		 * @return (CAL type: <code>(Cal.Collections.Array.Array a, Cal.Collections.Array.Array b, Cal.Collections.Array.Array c, Cal.Collections.Array.Array d)</code>) 
		 *          a 4-tuple of arrays.
		 */
		public static final SourceModel.Expr unzip4(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.unzip4), array});
		}

		/**
		 * Name binding for function: unzip4.
		 * @see #unzip4(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName unzip4 = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "unzip4");

		/**
		 * Updates an array at a specified index with a new element value. The returned array is a copy
		 * and the original array is unmodified (as is required by the fact that the <code>Cal.Collections.Array.Array</code> type is a
		 * pure-functional type).
		 * Terminates in an error if index is not a valid array index.
		 * @param index (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          zero-based index to update.
		 * @param value (CAL type: <code>a</code>)
		 *          to set into the array.
		 * @param array (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          to be updated.
		 * @return (CAL type: <code>Cal.Collections.Array.Array a</code>) 
		 *          updated array.
		 */
		public static final SourceModel.Expr updateAt(SourceModel.Expr index, SourceModel.Expr value, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.updateAt), index, value, array});
		}

		/**
		 * @see #updateAt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param index
		 * @param value
		 * @param array
		 * @return the SourceModel.Expr representing an application of updateAt
		 */
		public static final SourceModel.Expr updateAt(int index, SourceModel.Expr value, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.updateAt), SourceModel.Expr.makeIntValue(index), value, array});
		}

		/**
		 * Name binding for function: updateAt.
		 * @see #updateAt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName updateAt = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "updateAt");

		/**
		 * Converts two arrays into an array of corresponding pairs.
		 * If one input array is short, excess elements of the longer array are discarded.
		 * <p>
		 * e.g. <code>zip (Cal.Collections.Array.fromList [6, 3]) (Cal.Collections.Array.fromList ["orange", "apple", "pear"]) == (Cal.Collections.Array.fromList [(6, "orange"), (3, "apple")])</code> 
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.Array.zipWith
		 * </dl>
		 * 
		 * @param array1 (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          the first array to be zipped.
		 * @param array2 (CAL type: <code>Cal.Collections.Array.Array b</code>)
		 *          the second array to be zipped.
		 * @return (CAL type: <code>Cal.Collections.Array.Array (a, b)</code>) 
		 *          an array of corresponding pairs.
		 */
		public static final SourceModel.Expr zip(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.zip), array1, array2});
		}

		/**
		 * Name binding for function: zip.
		 * @see #zip(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName zip = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "zip");

		/**
		 * Converts three arrays into an array of corresponding triples.
		 * If one input array is short, excess elements of the longer arrays are discarded.
		 * <p>
		 * e.g. <code>zip3 (Cal.Collections.Array.fromList [6, 3]) (Cal.Collections.Array.fromList ["orange", "apple", "pear"]) (Cal.Collections.Array.fromList ['a', 'b']) == (Cal.Collections.Array.fromList [(6, "orange", 'a'), (3, "apple", 'b')])</code> 
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.Array.zip
		 * </dl>
		 * 
		 * @param array1 (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          the first array to be zipped.
		 * @param array2 (CAL type: <code>Cal.Collections.Array.Array b</code>)
		 *          the second array to be zipped.
		 * @param array3 (CAL type: <code>Cal.Collections.Array.Array c</code>)
		 *          the third array to be zipped.
		 * @return (CAL type: <code>Cal.Collections.Array.Array (a, b, c)</code>) 
		 *          an array of corresponding triples.
		 */
		public static final SourceModel.Expr zip3(SourceModel.Expr array1, SourceModel.Expr array2, SourceModel.Expr array3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.zip3), array1, array2, array3});
		}

		/**
		 * Name binding for function: zip3.
		 * @see #zip3(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName zip3 = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "zip3");

		/**
		 * Converts four arrays into an array of corresponding 4-tuples.
		 * If one input array is short, excess elements of the longer arrays are discarded.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.Array.zip
		 * </dl>
		 * 
		 * @param array1 (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          the first array to be zipped.
		 * @param array2 (CAL type: <code>Cal.Collections.Array.Array b</code>)
		 *          the second array to be zipped.
		 * @param array3 (CAL type: <code>Cal.Collections.Array.Array c</code>)
		 *          the third array to be zipped.
		 * @param array4 (CAL type: <code>Cal.Collections.Array.Array d</code>)
		 *          the fourth array to be zipped.
		 * @return (CAL type: <code>Cal.Collections.Array.Array (a, b, c, d)</code>) 
		 *          an array of corresponding 4-tuples.
		 */
		public static final SourceModel.Expr zip4(SourceModel.Expr array1, SourceModel.Expr array2, SourceModel.Expr array3, SourceModel.Expr array4) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.zip4), array1, array2, array3, array4});
		}

		/**
		 * Name binding for function: zip4.
		 * @see #zip4(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName zip4 = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "zip4");

		/**
		 * Returns an array where each element is the result of applying a function to the
		 * corresponding elements of two arrays.
		 * <p>
		 * <code>zipWith</code> generalises <code>Cal.Collections.Array.zip</code> by zipping with the function given as the first
		 * argument, instead of a tupling function. For example, <code>zipWith Cal.Core.Prelude.add</code> applied
		 * to two arrays produces the array of corresponding sums.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.Array.zip
		 * </dl>
		 * 
		 * @param zipFunction (CAL type: <code>a -> b -> c</code>)
		 *          the zipping function.
		 * @param array1 (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          the first array to be zipped.
		 * @param array2 (CAL type: <code>Cal.Collections.Array.Array b</code>)
		 *          the second array to be zipped.
		 * @return (CAL type: <code>Cal.Collections.Array.Array c</code>) 
		 *          an array of the element-wise combination of the input arrays.
		 */
		public static final SourceModel.Expr zipWith(SourceModel.Expr zipFunction, SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.zipWith), zipFunction, array1, array2});
		}

		/**
		 * Name binding for function: zipWith.
		 * @see #zipWith(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName zipWith = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "zipWith");

		/**
		 * Returns an array where each element is the result of applying a function to the
		 * corresponding elements of three arrays.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.Array.zip
		 * </dl>
		 * 
		 * @param zipFunction (CAL type: <code>a -> b -> c -> d</code>)
		 *          the zipping function.
		 * @param array1 (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          the first array to be zipped.
		 * @param array2 (CAL type: <code>Cal.Collections.Array.Array b</code>)
		 *          the second array to be zipped.
		 * @param array3 (CAL type: <code>Cal.Collections.Array.Array c</code>)
		 *          the third array to be zipped.
		 * @return (CAL type: <code>Cal.Collections.Array.Array d</code>) 
		 *          an array of the element-wise combination of the input arrays.
		 */
		public static final SourceModel.Expr zipWith3(SourceModel.Expr zipFunction, SourceModel.Expr array1, SourceModel.Expr array2, SourceModel.Expr array3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.zipWith3), zipFunction, array1, array2, array3});
		}

		/**
		 * Name binding for function: zipWith3.
		 * @see #zipWith3(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName zipWith3 = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "zipWith3");

		/**
		 * Returns an array where each element is the result of applying a function to the
		 * corresponding elements of four arrays.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.Array.zip
		 * </dl>
		 * 
		 * @param zipFunction (CAL type: <code>a -> b -> c -> d -> e</code>)
		 *          the zipping function.
		 * @param array1 (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          the first array to be zipped.
		 * @param array2 (CAL type: <code>Cal.Collections.Array.Array b</code>)
		 *          the second array to be zipped.
		 * @param array3 (CAL type: <code>Cal.Collections.Array.Array c</code>)
		 *          the third array to be zipped.
		 * @param array4 (CAL type: <code>Cal.Collections.Array.Array d</code>)
		 *          the fourth array to be zipped.
		 * @return (CAL type: <code>Cal.Collections.Array.Array e</code>) 
		 *          an array of the element-wise combination of the input arrays.
		 */
		public static final SourceModel.Expr zipWith4(SourceModel.Expr zipFunction, SourceModel.Expr array1, SourceModel.Expr array2, SourceModel.Expr array3, SourceModel.Expr array4) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.zipWith4), zipFunction, array1, array2, array3, array4});
		}

		/**
		 * Name binding for function: zipWith4.
		 * @see #zipWith4(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName zipWith4 = 
			QualifiedName.make(CAL_Array.MODULE_NAME, "zipWith4");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = 1360671228;

}
