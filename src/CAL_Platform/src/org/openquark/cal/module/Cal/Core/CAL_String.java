/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_String.java)
 * was generated from CAL module: Cal.Core.String.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Core.String module from Java code.
 *  
 * Creation date: Fri Mar 16 13:11:57 PST 2007
 * --!>
 *  
 */

package org.openquark.cal.module.Cal.Core;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * Defines many useful functions for the <code>Cal.Core.Prelude.String</code> type. Note that the <code>String</code> type itself is defined
 * in the <code>Cal.Core.Prelude</code> module due to the fact that it is supported via built-in notation for <code>String</code> literals.
 * @author Bo Ilic
 */
public final class CAL_String {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Core.String");

	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Core.String module.
	 */
	public static final class Functions {
		/**
		 * Compares two strings lexographically, ignoring case differences.
		 * @param stringValue1 (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the first string to compare.
		 * @param stringValue2 (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the second string to compare.
		 * @return (CAL type: <code>Cal.Core.Prelude.Ordering</code>) 
		 *          <code>Cal.Core.Prelude.LT</code>, <code>Cal.Core.Prelude.EQ</code>, or <code>Cal.Core.Prelude.GT</code> if <code>stringValue1</code> is respectively less than, equal to, or greater
		 * than <code>stringValue2</code>, ignoring case.
		 */
		public static final SourceModel.Expr compareIgnoreCase(SourceModel.Expr stringValue1, SourceModel.Expr stringValue2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.compareIgnoreCase), stringValue1, stringValue2});
		}

		/**
		 * @see #compareIgnoreCase(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param stringValue1
		 * @param stringValue2
		 * @return the SourceModel.Expr representing an application of compareIgnoreCase
		 */
		public static final SourceModel.Expr compareIgnoreCase(java.lang.String stringValue1, java.lang.String stringValue2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.compareIgnoreCase), SourceModel.Expr.makeStringValue(stringValue1), SourceModel.Expr.makeStringValue(stringValue2)});
		}

		/**
		 * Name binding for function: compareIgnoreCase.
		 * @see #compareIgnoreCase(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName compareIgnoreCase = 
			QualifiedName.make(CAL_String.MODULE_NAME, "compareIgnoreCase");

		/**
		 * <code>dropWhile dropWhileTrueFunction string</code> drops the longest prefix of the string for which <code>dropWhileTrueFunction</code>
		 * is <code>Cal.Core.Prelude.True</code> for each element.
		 * <p>
		 * e.g. <code>dropWhile Cal.Core.Char.isUpperCase "ABCdefGHI" == "defGHI"</code>
		 * 
		 * @param dropWhileTrueFunction (CAL type: <code>Cal.Core.Prelude.Char -> Cal.Core.Prelude.Boolean</code>)
		 *          a predicate to be applied to the elements of the string.
		 * @param string (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the string from which elements are to be taken.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the remainder of the string after having dropped the longest prefix in
		 * which <code>dropWhileTrueFunction</code> is <code>Cal.Core.Prelude.True</code> for each element.
		 */
		public static final SourceModel.Expr dropWhile(SourceModel.Expr dropWhileTrueFunction, SourceModel.Expr string) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.dropWhile), dropWhileTrueFunction, string});
		}

		/**
		 * @see #dropWhile(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param dropWhileTrueFunction
		 * @param string
		 * @return the SourceModel.Expr representing an application of dropWhile
		 */
		public static final SourceModel.Expr dropWhile(SourceModel.Expr dropWhileTrueFunction, java.lang.String string) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.dropWhile), dropWhileTrueFunction, SourceModel.Expr.makeStringValue(string)});
		}

		/**
		 * Name binding for function: dropWhile.
		 * @see #dropWhile(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName dropWhile = 
			QualifiedName.make(CAL_String.MODULE_NAME, "dropWhile");

		/**
		 * Returns whether <code>stringToTest</code> ends with the specified suffix.
		 * @param suffix (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @param stringToTest (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the string to be tested for ending with <code>suffix</code>.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if <code>stringToTest</code> ends with the string <code>suffix</code>.
		 */
		public static final SourceModel.Expr endsWith(SourceModel.Expr suffix, SourceModel.Expr stringToTest) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.endsWith), suffix, stringToTest});
		}

		/**
		 * @see #endsWith(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param suffix
		 * @param stringToTest
		 * @return the SourceModel.Expr representing an application of endsWith
		 */
		public static final SourceModel.Expr endsWith(java.lang.String suffix, java.lang.String stringToTest) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.endsWith), SourceModel.Expr.makeStringValue(suffix), SourceModel.Expr.makeStringValue(stringToTest)});
		}

		/**
		 * Name binding for function: endsWith.
		 * @see #endsWith(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName endsWith = 
			QualifiedName.make(CAL_String.MODULE_NAME, "endsWith");

		/**
		 * Returns whether the strings are lexographically equal, ignoring case differences.
		 * This function returns <code>Cal.Core.Prelude.True</code> if the two strings <code>x</code> and <code>y</code> are
		 * of the same length, and each corresponding pair of characters in the two strings satisfy
		 * the following:
		 * <p>
		 * Two characters <code>a</code> and <code>b</code> are considered the same, ignoring case if at least one of the following is true:
		 * <ul>
		 *  <li>
		 *   <code>a == b</code>
		 *  </li>
		 *  <li>
		 *   <code>Cal.Core.String.toLowerCase a == toLowerCase b</code>
		 *  </li>
		 *  <li>
		 *   <code>Cal.Core.String.toUpperCase a == toUpperCase b</code>
		 *  </li>
		 * </ul>
		 * 
		 * @param x (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the first string to compare.
		 * @param y (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the second string to compare.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the argument strings are equal, ignoring case; <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr equalsIgnoreCase(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.equalsIgnoreCase), x, y});
		}

		/**
		 * @see #equalsIgnoreCase(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @param y
		 * @return the SourceModel.Expr representing an application of equalsIgnoreCase
		 */
		public static final SourceModel.Expr equalsIgnoreCase(java.lang.String x, java.lang.String y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.equalsIgnoreCase), SourceModel.Expr.makeStringValue(x), SourceModel.Expr.makeStringValue(y)});
		}

		/**
		 * Name binding for function: equalsIgnoreCase.
		 * @see #equalsIgnoreCase(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName equalsIgnoreCase = 
			QualifiedName.make(CAL_String.MODULE_NAME, "equalsIgnoreCase");

		/**
		 * <code>filter keepIfTrueFunction string</code> applies the predicate function to each character of the string, and returns
		 * the string of characters for which the predicate evaluates to <code>Cal.Core.Prelude.True</code>.
		 * @param keepIfTrueFunction (CAL type: <code>Cal.Core.Prelude.Char -> Cal.Core.Prelude.Boolean</code>)
		 *          a predicate which returns <code>Cal.Core.Prelude.True</code> for characters that should be kept, and <code>Cal.Core.Prelude.False</code> for characters that
		 * should be dropped.
		 * @param string (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the string.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the string of those characters that satisfy the given predicate.
		 */
		public static final SourceModel.Expr filter(SourceModel.Expr keepIfTrueFunction, SourceModel.Expr string) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.filter), keepIfTrueFunction, string});
		}

		/**
		 * @see #filter(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param keepIfTrueFunction
		 * @param string
		 * @return the SourceModel.Expr representing an application of filter
		 */
		public static final SourceModel.Expr filter(SourceModel.Expr keepIfTrueFunction, java.lang.String string) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.filter), keepIfTrueFunction, SourceModel.Expr.makeStringValue(string)});
		}

		/**
		 * Name binding for function: filter.
		 * @see #filter(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName filter = 
			QualifiedName.make(CAL_String.MODULE_NAME, "filter");

		/**
		 * <code>find predicate string</code> returns the first value of string for which the predicate function is <code>Cal.Core.Prelude.True</code>, if there is
		 * one, or <code>Cal.Core.Prelude.Nothing</code> otherwise.
		 * <p>
		 * e.g. <code>find Cal.Core.Char.isWhitespace "This is a sentence." == Cal.Core.Prelude.Just ' '</code>
		 * 
		 * @param predicate (CAL type: <code>Cal.Core.Prelude.Char -> Cal.Core.Prelude.Boolean</code>)
		 *          a predicate to be applied to the elements of the string.
		 * @param string (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the string to be searched.
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Core.Prelude.Char</code>) 
		 *          the first value of string for which the predicate function is <code>Cal.Core.Prelude.True</code>, if there is one, or <code>Cal.Core.Prelude.Nothing</code>
		 * otherwise.
		 */
		public static final SourceModel.Expr find(SourceModel.Expr predicate, SourceModel.Expr string) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.find), predicate, string});
		}

		/**
		 * @see #find(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param predicate
		 * @param string
		 * @return the SourceModel.Expr representing an application of find
		 */
		public static final SourceModel.Expr find(SourceModel.Expr predicate, java.lang.String string) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.find), predicate, SourceModel.Expr.makeStringValue(string)});
		}

		/**
		 * Name binding for function: find.
		 * @see #find(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName find = 
			QualifiedName.make(CAL_String.MODULE_NAME, "find");

		/**
		 * <code>findIndex predicate string</code> returns the first index of string for which the predicate function is <code>Cal.Core.Prelude.True</code>, if
		 * there is one, or <code>Cal.Core.Prelude.Nothing</code> otherwise. The index is 0-based.
		 * <p>
		 * e.g. <code>findIndex Cal.Core.Char.isUpperCase "abcDEFghi" == Cal.Core.Prelude.Just 3</code>
		 * 
		 * @param predicate (CAL type: <code>Cal.Core.Prelude.Char -> Cal.Core.Prelude.Boolean</code>)
		 *          a predicate to be applied to the elements of the string.
		 * @param string (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the string to be searched.
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Core.Prelude.Int</code>) 
		 *          the first index of string for which the predicate function is <code>Cal.Core.Prelude.True</code>, if there is one, or <code>Cal.Core.Prelude.Nothing</code>
		 * otherwise.
		 */
		public static final SourceModel.Expr findIndex(SourceModel.Expr predicate, SourceModel.Expr string) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.findIndex), predicate, string});
		}

		/**
		 * @see #findIndex(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param predicate
		 * @param string
		 * @return the SourceModel.Expr representing an application of findIndex
		 */
		public static final SourceModel.Expr findIndex(SourceModel.Expr predicate, java.lang.String string) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.findIndex), predicate, SourceModel.Expr.makeStringValue(string)});
		}

		/**
		 * Name binding for function: findIndex.
		 * @see #findIndex(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName findIndex = 
			QualifiedName.make(CAL_String.MODULE_NAME, "findIndex");

		/**
		 * This is the strict version of <code>foldLeft</code>. It is used for efficiency reasons in certain situations.
		 * @param foldFunction (CAL type: <code>a -> Cal.Core.Prelude.Char -> a</code>)
		 *          the function to be used in folding the string.
		 * @param initialValue (CAL type: <code>a</code>)
		 *          the initial value for the folding process.
		 * @param string (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the string to be folded over.
		 * @return (CAL type: <code>a</code>) 
		 *          the single result obtained from folding <code>foldFunction</code> over the string.
		 */
		public static final SourceModel.Expr foldLeftStrict(SourceModel.Expr foldFunction, SourceModel.Expr initialValue, SourceModel.Expr string) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.foldLeftStrict), foldFunction, initialValue, string});
		}

		/**
		 * @see #foldLeftStrict(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param foldFunction
		 * @param initialValue
		 * @param string
		 * @return the SourceModel.Expr representing an application of foldLeftStrict
		 */
		public static final SourceModel.Expr foldLeftStrict(SourceModel.Expr foldFunction, SourceModel.Expr initialValue, java.lang.String string) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.foldLeftStrict), foldFunction, initialValue, SourceModel.Expr.makeStringValue(string)});
		}

		/**
		 * Name binding for function: foldLeftStrict.
		 * @see #foldLeftStrict(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName foldLeftStrict = 
			QualifiedName.make(CAL_String.MODULE_NAME, "foldLeftStrict");

		/**
		 * Converts a single character to a string of length 1 holding that character.
		 * @param char_ (CAL type: <code>Cal.Core.Prelude.Char</code>)
		 *          the character to represent as a <code>String</code>.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          a string of length 1 holding the character.
		 */
		public static final SourceModel.Expr fromChar(SourceModel.Expr char_) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromChar), char_});
		}

		/**
		 * @see #fromChar(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param char_
		 * @return the SourceModel.Expr representing an application of fromChar
		 */
		public static final SourceModel.Expr fromChar(char char_) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromChar), SourceModel.Expr.makeCharValue(char_)});
		}

		/**
		 * Name binding for function: fromChar.
		 * @see #fromChar(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fromChar = 
			QualifiedName.make(CAL_String.MODULE_NAME, "fromChar");

		/**
		 * Converts the list of characters to a string.
		 * @param listOfChars (CAL type: <code>[Cal.Core.Prelude.Char]</code>)
		 *          the list of characters.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          a string constructed from the list of characters.
		 */
		public static final SourceModel.Expr fromList(SourceModel.Expr listOfChars) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromList), listOfChars});
		}

		/**
		 * Name binding for function: fromList.
		 * @see #fromList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fromList = 
			QualifiedName.make(CAL_String.MODULE_NAME, "fromList");

		/**
		 * Finds the index of the first occurrence of <code>charToFind</code> in <code>stringToSearch</code> or -1 if the character does not occur.
		 * @param charToFind (CAL type: <code>Cal.Core.Prelude.Char</code>)
		 * @param stringToSearch (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the index of the first occurrence of <code>charToFind</code> in <code>stringToSearch</code> or -1 if the character does not occur.
		 */
		public static final SourceModel.Expr indexOf(SourceModel.Expr charToFind, SourceModel.Expr stringToSearch) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.indexOf), charToFind, stringToSearch});
		}

		/**
		 * @see #indexOf(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param charToFind
		 * @param stringToSearch
		 * @return the SourceModel.Expr representing an application of indexOf
		 */
		public static final SourceModel.Expr indexOf(char charToFind, java.lang.String stringToSearch) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.indexOf), SourceModel.Expr.makeCharValue(charToFind), SourceModel.Expr.makeStringValue(stringToSearch)});
		}

		/**
		 * Name binding for function: indexOf.
		 * @see #indexOf(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName indexOf = 
			QualifiedName.make(CAL_String.MODULE_NAME, "indexOf");

		/**
		 * Finds the index of the first occurrence at index greater than or equal to <code>fromIndex</code> of <code>charToFind</code> in
		 * <code>stringToSearch</code> or -1 if the character does not occur from <code>fromIndex</code> onwards.
		 * <p>
		 * There are no invalid values of <code>fromIndex</code>. <code>fromIndex &lt; 0</code> is taken as starting the search from the start of
		 * <code>stringToSearch</code> while <code>fromIndex &gt;= length stringToSearch</code> always returns -1. 
		 * 
		 * @param charToFind (CAL type: <code>Cal.Core.Prelude.Char</code>)
		 * @param fromIndex (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          index to start searching from.
		 * @param stringToSearch (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the index of the first occurrence at index greater than or equal to <code>fromIndex</code> of <code>charToFind</code> in
		 * <code>stringToSearch</code> or -1 if the character does not occur from <code>fromIndex</code> onwards.
		 */
		public static final SourceModel.Expr indexOfFrom(SourceModel.Expr charToFind, SourceModel.Expr fromIndex, SourceModel.Expr stringToSearch) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.indexOfFrom), charToFind, fromIndex, stringToSearch});
		}

		/**
		 * @see #indexOfFrom(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param charToFind
		 * @param fromIndex
		 * @param stringToSearch
		 * @return the SourceModel.Expr representing an application of indexOfFrom
		 */
		public static final SourceModel.Expr indexOfFrom(char charToFind, int fromIndex, java.lang.String stringToSearch) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.indexOfFrom), SourceModel.Expr.makeCharValue(charToFind), SourceModel.Expr.makeIntValue(fromIndex), SourceModel.Expr.makeStringValue(stringToSearch)});
		}

		/**
		 * Name binding for function: indexOfFrom.
		 * @see #indexOfFrom(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName indexOfFrom = 
			QualifiedName.make(CAL_String.MODULE_NAME, "indexOfFrom");

		/**
		 * Finds the index of the first occurrence of <code>stringToFind</code> in <code>stringToSearch</code> or -1 if the <code>stringToFind</code>
		 * does not occur.
		 * @param stringToFind (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @param stringToSearch (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the index of the first occurrence of <code>stringToFind</code> in <code>stringToSearch</code> or -1 if the character does
		 * not occur.
		 */
		public static final SourceModel.Expr indexOfString(SourceModel.Expr stringToFind, SourceModel.Expr stringToSearch) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.indexOfString), stringToFind, stringToSearch});
		}

		/**
		 * @see #indexOfString(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param stringToFind
		 * @param stringToSearch
		 * @return the SourceModel.Expr representing an application of indexOfString
		 */
		public static final SourceModel.Expr indexOfString(java.lang.String stringToFind, java.lang.String stringToSearch) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.indexOfString), SourceModel.Expr.makeStringValue(stringToFind), SourceModel.Expr.makeStringValue(stringToSearch)});
		}

		/**
		 * Name binding for function: indexOfString.
		 * @see #indexOfString(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName indexOfString = 
			QualifiedName.make(CAL_String.MODULE_NAME, "indexOfString");

		/**
		 * Finds the index of the first occurrence at index greater than or equal to <code>fromIndex</code> of <code>stringToFind</code> in
		 * <code>stringToSearch</code> or -1 if <code>stringToFind</code> does not occur from <code>fromIndex</code> onwards.
		 * <p>
		 * There are no invalid values of <code>fromIndex</code>. <code>fromIndex &lt; 0</code> is taken as starting the search from the start of
		 * <code>stringToSearch</code> while <code>fromIndex &gt;= length stringToSearch</code> always returns -1. 
		 * 
		 * @param stringToFind (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @param fromIndex (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          index to start searching from.
		 * @param stringToSearch (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the index of the first occurrence at index greater than or equal to <code>fromIndex</code> of <code>stringToFind</code> in
		 * <code>stringToSearch</code> or -1 if <code>stringToFind</code> does not occur from <code>fromIndex</code> onwards.
		 */
		public static final SourceModel.Expr indexOfStringFrom(SourceModel.Expr stringToFind, SourceModel.Expr fromIndex, SourceModel.Expr stringToSearch) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.indexOfStringFrom), stringToFind, fromIndex, stringToSearch});
		}

		/**
		 * @see #indexOfStringFrom(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param stringToFind
		 * @param fromIndex
		 * @param stringToSearch
		 * @return the SourceModel.Expr representing an application of indexOfStringFrom
		 */
		public static final SourceModel.Expr indexOfStringFrom(java.lang.String stringToFind, int fromIndex, java.lang.String stringToSearch) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.indexOfStringFrom), SourceModel.Expr.makeStringValue(stringToFind), SourceModel.Expr.makeIntValue(fromIndex), SourceModel.Expr.makeStringValue(stringToSearch)});
		}

		/**
		 * Name binding for function: indexOfStringFrom.
		 * @see #indexOfStringFrom(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName indexOfStringFrom = 
			QualifiedName.make(CAL_String.MODULE_NAME, "indexOfStringFrom");

		/**
		 * Finds the index of the last occurrence of <code>charToFind</code> in <code>stringToSearch</code> or -1 if the character does not occur.
		 * @param charToFind (CAL type: <code>Cal.Core.Prelude.Char</code>)
		 * @param stringToSearch (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the index of the last occurrence of <code>charToFind</code> in <code>stringToSearch</code> or -1 if the character does not occur.
		 */
		public static final SourceModel.Expr lastIndexOf(SourceModel.Expr charToFind, SourceModel.Expr stringToSearch) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lastIndexOf), charToFind, stringToSearch});
		}

		/**
		 * @see #lastIndexOf(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param charToFind
		 * @param stringToSearch
		 * @return the SourceModel.Expr representing an application of lastIndexOf
		 */
		public static final SourceModel.Expr lastIndexOf(char charToFind, java.lang.String stringToSearch) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lastIndexOf), SourceModel.Expr.makeCharValue(charToFind), SourceModel.Expr.makeStringValue(stringToSearch)});
		}

		/**
		 * Name binding for function: lastIndexOf.
		 * @see #lastIndexOf(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName lastIndexOf = 
			QualifiedName.make(CAL_String.MODULE_NAME, "lastIndexOf");

		/**
		 * Finds the index of the last occurrence at index less than or equal to <code>fromIndex</code> of <code>charToFind</code> in
		 * <code>stringToSearch</code> or -1 if the character does not occur from <code>fromIndex</code> and earlier.
		 * <p>
		 * There are no invalid values of <code>fromIndex</code>. <code>fromIndex &lt; 0</code> always returns -1, and
		 * <code>fromIndex &gt;= length stringToSearch</code> is taken as starting the search from the last index of <code>stringToSearch</code>.
		 * 
		 * @param charToFind (CAL type: <code>Cal.Core.Prelude.Char</code>)
		 * @param fromIndex (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          index to start searching from.
		 * @param stringToSearch (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the index of the last occurrence at index less than or equal to <code>fromIndex</code> of <code>charToFind</code> in
		 * <code>stringToSearch</code> or -1 if the character does not occur from <code>fromIndex</code> and earlier.
		 */
		public static final SourceModel.Expr lastIndexOfFrom(SourceModel.Expr charToFind, SourceModel.Expr fromIndex, SourceModel.Expr stringToSearch) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lastIndexOfFrom), charToFind, fromIndex, stringToSearch});
		}

		/**
		 * @see #lastIndexOfFrom(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param charToFind
		 * @param fromIndex
		 * @param stringToSearch
		 * @return the SourceModel.Expr representing an application of lastIndexOfFrom
		 */
		public static final SourceModel.Expr lastIndexOfFrom(char charToFind, int fromIndex, java.lang.String stringToSearch) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lastIndexOfFrom), SourceModel.Expr.makeCharValue(charToFind), SourceModel.Expr.makeIntValue(fromIndex), SourceModel.Expr.makeStringValue(stringToSearch)});
		}

		/**
		 * Name binding for function: lastIndexOfFrom.
		 * @see #lastIndexOfFrom(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName lastIndexOfFrom = 
			QualifiedName.make(CAL_String.MODULE_NAME, "lastIndexOfFrom");

		/**
		 * Finds the index of the last occurrence of <code>stringToFind</code> in <code>stringToSearch</code> or -1 if the <code>stringToFind</code>
		 * does not occur.
		 * @param stringToFind (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @param stringToSearch (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the index of the last occurrence of <code>stringToFind</code> in <code>stringToSearch</code> or -1 if the character does not occur.
		 */
		public static final SourceModel.Expr lastIndexOfString(SourceModel.Expr stringToFind, SourceModel.Expr stringToSearch) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lastIndexOfString), stringToFind, stringToSearch});
		}

		/**
		 * @see #lastIndexOfString(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param stringToFind
		 * @param stringToSearch
		 * @return the SourceModel.Expr representing an application of lastIndexOfString
		 */
		public static final SourceModel.Expr lastIndexOfString(java.lang.String stringToFind, java.lang.String stringToSearch) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lastIndexOfString), SourceModel.Expr.makeStringValue(stringToFind), SourceModel.Expr.makeStringValue(stringToSearch)});
		}

		/**
		 * Name binding for function: lastIndexOfString.
		 * @see #lastIndexOfString(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName lastIndexOfString = 
			QualifiedName.make(CAL_String.MODULE_NAME, "lastIndexOfString");

		/**
		 * Finds the index of the last occurrence at index less than or equal to <code>fromIndex</code> of <code>stringToFind</code> in
		 * <code>stringToSearch</code> or -1 if <code>stringToFind</code> does not occur from <code>fromIndex</code> and earlier.
		 * <p>
		 * There are no invalid values of <code>fromIndex</code>. <code>fromIndex &lt; 0</code> always returns -1, and
		 * <code>fromIndex &gt;= length stringToSearch</code> is taken as starting the search from the last index of <code>stringToSearch</code>.
		 * 
		 * @param stringToFind (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @param fromIndex (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          index to start searching from.
		 * @param stringToSearch (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the index of the last occurrence at index less than or equal to <code>fromIndex</code> of <code>stringToFind</code> in
		 * <code>stringToSearch</code> or -1 if <code>stringToFind</code> does not occur from <code>fromIndex</code> and earlier.
		 */
		public static final SourceModel.Expr lastIndexOfStringFrom(SourceModel.Expr stringToFind, SourceModel.Expr fromIndex, SourceModel.Expr stringToSearch) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lastIndexOfStringFrom), stringToFind, fromIndex, stringToSearch});
		}

		/**
		 * @see #lastIndexOfStringFrom(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param stringToFind
		 * @param fromIndex
		 * @param stringToSearch
		 * @return the SourceModel.Expr representing an application of lastIndexOfStringFrom
		 */
		public static final SourceModel.Expr lastIndexOfStringFrom(java.lang.String stringToFind, int fromIndex, java.lang.String stringToSearch) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lastIndexOfStringFrom), SourceModel.Expr.makeStringValue(stringToFind), SourceModel.Expr.makeIntValue(fromIndex), SourceModel.Expr.makeStringValue(stringToSearch)});
		}

		/**
		 * Name binding for function: lastIndexOfStringFrom.
		 * @see #lastIndexOfStringFrom(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName lastIndexOfStringFrom = 
			QualifiedName.make(CAL_String.MODULE_NAME, "lastIndexOfStringFrom");

		/**
		 * Returns the length of the string. The length is equal to the number of characters contained in the string.
		 * @param string (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the string whose length is to be returned.
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the length of the string.
		 */
		public static final SourceModel.Expr length(SourceModel.Expr string) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.length), string});
		}

		/**
		 * @see #length(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param string
		 * @return the SourceModel.Expr representing an application of length
		 */
		public static final SourceModel.Expr length(java.lang.String string) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.length), SourceModel.Expr.makeStringValue(string)});
		}

		/**
		 * Name binding for function: length.
		 * @see #length(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName length = 
			QualifiedName.make(CAL_String.MODULE_NAME, "length");

		/**
		 * Breaks up a string into a list of strings, one for each line. Line breaks are determined by the <code>'\n'</code> character.
		 * <p>
		 * e.g. <code>lines "this is\na test of\nthe lines function" = ["this is", "a test of", "the lines function"]</code>
		 * 
		 * @param string (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the string to be split.
		 * @return (CAL type: <code>[Cal.Core.Prelude.String]</code>) 
		 *          a list of strings, one for each line in the specified string.
		 */
		public static final SourceModel.Expr lines(SourceModel.Expr string) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lines), string});
		}

		/**
		 * @see #lines(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param string
		 * @return the SourceModel.Expr representing an application of lines
		 */
		public static final SourceModel.Expr lines(java.lang.String string) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lines), SourceModel.Expr.makeStringValue(string)});
		}

		/**
		 * Name binding for function: lines.
		 * @see #lines(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName lines = 
			QualifiedName.make(CAL_String.MODULE_NAME, "lines");

		/**
		 * <code>map mapFunction string</code> applies the function <code>mapFunction</code> to each character of <code>string</code> and returns the
		 * resulting string.
		 * @param mapFunction (CAL type: <code>Cal.Core.Prelude.Char -> Cal.Core.Prelude.Char</code>)
		 *          a function to be applied to each element of the argument string.
		 * @param string (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the string to which the <code>mapFunction</code> is applied element-wise.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the string obtained by applying <code>mapFunction</code> to each element of string.
		 */
		public static final SourceModel.Expr map(SourceModel.Expr mapFunction, SourceModel.Expr string) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.map), mapFunction, string});
		}

		/**
		 * @see #map(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param mapFunction
		 * @param string
		 * @return the SourceModel.Expr representing an application of map
		 */
		public static final SourceModel.Expr map(SourceModel.Expr mapFunction, java.lang.String string) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.map), mapFunction, SourceModel.Expr.makeStringValue(string)});
		}

		/**
		 * Name binding for function: map.
		 * @see #map(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName map = 
			QualifiedName.make(CAL_String.MODULE_NAME, "map");

		/**
		 * Breaks up the specified string where any of the separators occur.
		 * The substring pieces are returned (not including the separator chars);
		 * the separators are matched in the order they are specified, e.g.:
		 * <code>"what is this"</code> split by <code>[" is", "i"]</code> gives <code>["what", " th", "s"]</code>
		 * @param separators (CAL type: <code>[Cal.Core.Prelude.String]</code>)
		 *          the list of separators.
		 * @param stringValue (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the string to be split.
		 * @return (CAL type: <code>[Cal.Core.Prelude.String]</code>) 
		 *          a list of the substring pieces.
		 */
		public static final SourceModel.Expr multiSplitString(SourceModel.Expr separators, SourceModel.Expr stringValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.multiSplitString), separators, stringValue});
		}

		/**
		 * @see #multiSplitString(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param separators
		 * @param stringValue
		 * @return the SourceModel.Expr representing an application of multiSplitString
		 */
		public static final SourceModel.Expr multiSplitString(SourceModel.Expr separators, java.lang.String stringValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.multiSplitString), separators, SourceModel.Expr.makeStringValue(stringValue)});
		}

		/**
		 * Name binding for function: multiSplitString.
		 * @see #multiSplitString(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName multiSplitString = 
			QualifiedName.make(CAL_String.MODULE_NAME, "multiSplitString");

		/**
		 * Replaces any sequence of whitespace characters with a single space. Here the definition of whitespace follows that
		 * of the <code>Cal.Core.Char.isWhitespace</code> function.
		 * @param inputString (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the input string.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          a copy of the input string where any sequence of whitespace characters is replaced with a single space.
		 */
		public static final SourceModel.Expr normalizeWhitespace(SourceModel.Expr inputString) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.normalizeWhitespace), inputString});
		}

		/**
		 * @see #normalizeWhitespace(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param inputString
		 * @return the SourceModel.Expr representing an application of normalizeWhitespace
		 */
		public static final SourceModel.Expr normalizeWhitespace(java.lang.String inputString) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.normalizeWhitespace), SourceModel.Expr.makeStringValue(inputString)});
		}

		/**
		 * Name binding for function: normalizeWhitespace.
		 * @see #normalizeWhitespace(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName normalizeWhitespace = 
			QualifiedName.make(CAL_String.MODULE_NAME, "normalizeWhitespace");

		/**
		 * Replaces each substring of the specified string that matches the given regular expression with the given replacement.
		 * @param originalString (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the original string.
		 * @param regex (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the regular expression to which the original string is to be matched.
		 * @param replacementString (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the replacement string.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the resulting string.
		 */
		public static final SourceModel.Expr replaceAllString(SourceModel.Expr originalString, SourceModel.Expr regex, SourceModel.Expr replacementString) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.replaceAllString), originalString, regex, replacementString});
		}

		/**
		 * @see #replaceAllString(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param originalString
		 * @param regex
		 * @param replacementString
		 * @return the SourceModel.Expr representing an application of replaceAllString
		 */
		public static final SourceModel.Expr replaceAllString(java.lang.String originalString, java.lang.String regex, java.lang.String replacementString) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.replaceAllString), SourceModel.Expr.makeStringValue(originalString), SourceModel.Expr.makeStringValue(regex), SourceModel.Expr.makeStringValue(replacementString)});
		}

		/**
		 * Name binding for function: replaceAllString.
		 * @see #replaceAllString(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName replaceAllString = 
			QualifiedName.make(CAL_String.MODULE_NAME, "replaceAllString");

		/**
		 * <code>replicate nCopies charToReplicate</code> is a string of length <code>nCopies</code>, with every character equal to
		 * <code>charToReplicate</code>.
		 * @param nCopies (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the number of copies.
		 * @param charToReplicate (CAL type: <code>Cal.Core.Prelude.Char</code>)
		 *          the char to be replicated.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          a string of length <code>nCopies</code>, with every character equal to <code>charToReplicate</code>.
		 */
		public static final SourceModel.Expr replicate(SourceModel.Expr nCopies, SourceModel.Expr charToReplicate) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.replicate), nCopies, charToReplicate});
		}

		/**
		 * @see #replicate(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param nCopies
		 * @param charToReplicate
		 * @return the SourceModel.Expr representing an application of replicate
		 */
		public static final SourceModel.Expr replicate(int nCopies, char charToReplicate) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.replicate), SourceModel.Expr.makeIntValue(nCopies), SourceModel.Expr.makeCharValue(charToReplicate)});
		}

		/**
		 * Name binding for function: replicate.
		 * @see #replicate(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName replicate = 
			QualifiedName.make(CAL_String.MODULE_NAME, "replicate");

		/**
		 * Reverses the sequence of characters of a string. 
		 * <code>reverse</code> is O(n) time where n is the length of the string.
		 * @param string (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          to be reversed.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          a string containing the characters in the specified argument string, in reverse order.
		 */
		public static final SourceModel.Expr reverse(SourceModel.Expr string) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.reverse), string});
		}

		/**
		 * @see #reverse(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param string
		 * @return the SourceModel.Expr representing an application of reverse
		 */
		public static final SourceModel.Expr reverse(java.lang.String string) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.reverse), SourceModel.Expr.makeStringValue(string)});
		}

		/**
		 * Name binding for function: reverse.
		 * @see #reverse(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName reverse = 
			QualifiedName.make(CAL_String.MODULE_NAME, "reverse");

		/**
		 * Constructs a string of a specified number of spaces.
		 * @param n (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the number of spaces.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          a string of n spaces.
		 */
		public static final SourceModel.Expr space(SourceModel.Expr n) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.space), n});
		}

		/**
		 * @see #space(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param n
		 * @return the SourceModel.Expr representing an application of space
		 */
		public static final SourceModel.Expr space(int n) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.space), SourceModel.Expr.makeIntValue(n)});
		}

		/**
		 * Name binding for function: space.
		 * @see #space(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName space = 
			QualifiedName.make(CAL_String.MODULE_NAME, "space");

		/**
		 * Breaks up the specified string where the separator text occurs.
		 * The substring pieces are returned (not including the separator characters).
		 * @param separator (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the separator.
		 * @param stringValue (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the string to be split.
		 * @return (CAL type: <code>[Cal.Core.Prelude.String]</code>) 
		 *          a list of the substring pieces.
		 */
		public static final SourceModel.Expr splitString(SourceModel.Expr separator, SourceModel.Expr stringValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.splitString), separator, stringValue});
		}

		/**
		 * @see #splitString(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param separator
		 * @param stringValue
		 * @return the SourceModel.Expr representing an application of splitString
		 */
		public static final SourceModel.Expr splitString(java.lang.String separator, java.lang.String stringValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.splitString), SourceModel.Expr.makeStringValue(separator), SourceModel.Expr.makeStringValue(stringValue)});
		}

		/**
		 * Name binding for function: splitString.
		 * @see #splitString(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName splitString = 
			QualifiedName.make(CAL_String.MODULE_NAME, "splitString");

		/**
		 * Returns whether <code>stringToTest</code> starts with the specified prefix.
		 * @param prefix (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @param stringToTest (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the string to be tested for starting with <code>prefix</code>.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if <code>stringToTest</code> starts with the string <code>prefix</code>.
		 */
		public static final SourceModel.Expr startsWith(SourceModel.Expr prefix, SourceModel.Expr stringToTest) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.startsWith), prefix, stringToTest});
		}

		/**
		 * @see #startsWith(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param prefix
		 * @param stringToTest
		 * @return the SourceModel.Expr representing an application of startsWith
		 */
		public static final SourceModel.Expr startsWith(java.lang.String prefix, java.lang.String stringToTest) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.startsWith), SourceModel.Expr.makeStringValue(prefix), SourceModel.Expr.makeStringValue(stringToTest)});
		}

		/**
		 * Name binding for function: startsWith.
		 * @see #startsWith(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName startsWith = 
			QualifiedName.make(CAL_String.MODULE_NAME, "startsWith");

		/**
		 * Returns whether <code>stringToTest</code> starts with the specified prefix beginning at the specified offset index of
		 * <code>stringToTest</code>. The result is <code>Cal.Core.Prelude.False</code> if <code>offsetIndex</code> is not a valid index into <code>stringToTest</code>.
		 * @param prefix (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @param offsetIndex (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          index into <code>stringToTest</code> from which to start testing.
		 * @param stringToTest (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the string to be tested for starting with <code>prefix</code>.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if <code>stringToTest</code> starts with the string <code>prefix</code> beginning at the specified offset index.
		 */
		public static final SourceModel.Expr startsWithFrom(SourceModel.Expr prefix, SourceModel.Expr offsetIndex, SourceModel.Expr stringToTest) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.startsWithFrom), prefix, offsetIndex, stringToTest});
		}

		/**
		 * @see #startsWithFrom(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param prefix
		 * @param offsetIndex
		 * @param stringToTest
		 * @return the SourceModel.Expr representing an application of startsWithFrom
		 */
		public static final SourceModel.Expr startsWithFrom(java.lang.String prefix, int offsetIndex, java.lang.String stringToTest) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.startsWithFrom), SourceModel.Expr.makeStringValue(prefix), SourceModel.Expr.makeIntValue(offsetIndex), SourceModel.Expr.makeStringValue(stringToTest)});
		}

		/**
		 * Name binding for function: startsWithFrom.
		 * @see #startsWithFrom(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName startsWithFrom = 
			QualifiedName.make(CAL_String.MODULE_NAME, "startsWithFrom");

		/**
		 * Retrieves the character at the specified (0-based) position in the string
		 * @param string (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the string.
		 * @param position (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the position of the character to be retrieved.
		 * @return (CAL type: <code>Cal.Core.Prelude.Char</code>) 
		 *          the character at the specified position of the specified string.
		 */
		public static final SourceModel.Expr subscript(SourceModel.Expr string, SourceModel.Expr position) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.subscript), string, position});
		}

		/**
		 * @see #subscript(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param string
		 * @param position
		 * @return the SourceModel.Expr representing an application of subscript
		 */
		public static final SourceModel.Expr subscript(java.lang.String string, int position) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.subscript), SourceModel.Expr.makeStringValue(string), SourceModel.Expr.makeIntValue(position)});
		}

		/**
		 * Name binding for function: subscript.
		 * @see #subscript(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName subscript = 
			QualifiedName.make(CAL_String.MODULE_NAME, "subscript");

		/**
		 * Returns a new string that is a substring of the specified string. 
		 * The substring begins at the specified <code>beginIndex</code> and extends to the character at index <code>endIndex - 1</code>. 
		 * Thus the length of the substring is <code>endIndex-beginIndex</code>.
		 * @param originalString (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the string whose substring is to be returned.
		 * @param beginIndex (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the beginning index, inclusive.
		 * @param endIndex (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the ending index, exclusive.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the specified substring.
		 */
		public static final SourceModel.Expr substring(SourceModel.Expr originalString, SourceModel.Expr beginIndex, SourceModel.Expr endIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.substring), originalString, beginIndex, endIndex});
		}

		/**
		 * @see #substring(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param originalString
		 * @param beginIndex
		 * @param endIndex
		 * @return the SourceModel.Expr representing an application of substring
		 */
		public static final SourceModel.Expr substring(java.lang.String originalString, int beginIndex, int endIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.substring), SourceModel.Expr.makeStringValue(originalString), SourceModel.Expr.makeIntValue(beginIndex), SourceModel.Expr.makeIntValue(endIndex)});
		}

		/**
		 * Name binding for function: substring.
		 * @see #substring(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName substring = 
			QualifiedName.make(CAL_String.MODULE_NAME, "substring");

		/**
		 * <code>takeWhile takeWhileTrueFunction string</code> returns the longest prefix of the string for which <code>takeWhileTrueFunction</code>
		 * is <code>Cal.Core.Prelude.True</code> for each element.
		 * <p>
		 * e.g. <code>takeWhile Cal.Core.Char.isUpperCase "ABCdef" == "ABC"</code>
		 * 
		 * @param takeWhileTrueFunction (CAL type: <code>Cal.Core.Prelude.Char -> Cal.Core.Prelude.Boolean</code>)
		 *          a predicate to be applied to the elements of the string.
		 * @param string (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the string from which elements are to be taken.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the longest prefix of the string for which <code>takeWhileTrueFunction</code> is <code>Cal.Core.Prelude.True</code> 
		 * for each element.
		 */
		public static final SourceModel.Expr takeWhile(SourceModel.Expr takeWhileTrueFunction, SourceModel.Expr string) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.takeWhile), takeWhileTrueFunction, string});
		}

		/**
		 * @see #takeWhile(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param takeWhileTrueFunction
		 * @param string
		 * @return the SourceModel.Expr representing an application of takeWhile
		 */
		public static final SourceModel.Expr takeWhile(SourceModel.Expr takeWhileTrueFunction, java.lang.String string) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.takeWhile), takeWhileTrueFunction, SourceModel.Expr.makeStringValue(string)});
		}

		/**
		 * Name binding for function: takeWhile.
		 * @see #takeWhile(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName takeWhile = 
			QualifiedName.make(CAL_String.MODULE_NAME, "takeWhile");

		/**
		 * 
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the testing predicates in this module all run successfully.
		 */
		public static final SourceModel.Expr testModule() {
			return SourceModel.Expr.Var.make(Functions.testModule);
		}

		/**
		 * Name binding for function: testModule.
		 * @see #testModule()
		 */
		public static final QualifiedName testModule = 
			QualifiedName.make(CAL_String.MODULE_NAME, "testModule");

		/**
		 * Converts the string to a list of characters.
		 * @param stringValue (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the string to be converted.
		 * @return (CAL type: <code>[Cal.Core.Prelude.Char]</code>) 
		 *          a list of characters in the string.
		 */
		public static final SourceModel.Expr toList(SourceModel.Expr stringValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toList), stringValue});
		}

		/**
		 * @see #toList(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param stringValue
		 * @return the SourceModel.Expr representing an application of toList
		 */
		public static final SourceModel.Expr toList(java.lang.String stringValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toList), SourceModel.Expr.makeStringValue(stringValue)});
		}

		/**
		 * Name binding for function: toList.
		 * @see #toList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName toList = 
			QualifiedName.make(CAL_String.MODULE_NAME, "toList");

		/**
		 * Converts all of the characters in the specified string to lower case using
		 * the rules of the default locale <code>Cal.Utilities.Locale.defaultLocale</code>.
		 * <p>
		 * Case mapping is based on the Unicode Standard. Note that the returned string
		 * may not have the same length as the original string.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.Locale.defaultLocale
		 * </dl>
		 * 
		 * @param stringValue (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the string to be converted.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the string converted to lowercase.
		 */
		public static final SourceModel.Expr toLowerCase(SourceModel.Expr stringValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toLowerCase), stringValue});
		}

		/**
		 * @see #toLowerCase(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param stringValue
		 * @return the SourceModel.Expr representing an application of toLowerCase
		 */
		public static final SourceModel.Expr toLowerCase(java.lang.String stringValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toLowerCase), SourceModel.Expr.makeStringValue(stringValue)});
		}

		/**
		 * Name binding for function: toLowerCase.
		 * @see #toLowerCase(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName toLowerCase = 
			QualifiedName.make(CAL_String.MODULE_NAME, "toLowerCase");

		/**
		 * Converts all of the characters in the specified string to lower case using
		 * the rules of the specified locale.
		 * <p>
		 * Case mapping is based on the Unicode Standard. Note that the returned string
		 * may not have the same length as the original string.
		 * 
		 * @param stringValue (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the string to be converted.
		 * @param locale (CAL type: <code>Cal.Utilities.Locale.Locale</code>)
		 *          the locale whose rules are to be used.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the string converted to lowercase.
		 */
		public static final SourceModel.Expr toLowerCaseInLocale(SourceModel.Expr stringValue, SourceModel.Expr locale) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toLowerCaseInLocale), stringValue, locale});
		}

		/**
		 * @see #toLowerCaseInLocale(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param stringValue
		 * @param locale
		 * @return the SourceModel.Expr representing an application of toLowerCaseInLocale
		 */
		public static final SourceModel.Expr toLowerCaseInLocale(java.lang.String stringValue, SourceModel.Expr locale) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toLowerCaseInLocale), SourceModel.Expr.makeStringValue(stringValue), locale});
		}

		/**
		 * Name binding for function: toLowerCaseInLocale.
		 * @see #toLowerCaseInLocale(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName toLowerCaseInLocale = 
			QualifiedName.make(CAL_String.MODULE_NAME, "toLowerCaseInLocale");

		/**
		 * Converts all of the characters in the specified string to upper case using
		 * the rules of the default locale <code>Cal.Utilities.Locale.defaultLocale</code>.
		 * <p>
		 * Case mapping is based on the Unicode Standard. Note that the returned string
		 * may not have the same length as the original string.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.Locale.defaultLocale
		 * </dl>
		 * 
		 * @param stringValue (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the string to be converted.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the string converted to uppercase.
		 */
		public static final SourceModel.Expr toUpperCase(SourceModel.Expr stringValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toUpperCase), stringValue});
		}

		/**
		 * @see #toUpperCase(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param stringValue
		 * @return the SourceModel.Expr representing an application of toUpperCase
		 */
		public static final SourceModel.Expr toUpperCase(java.lang.String stringValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toUpperCase), SourceModel.Expr.makeStringValue(stringValue)});
		}

		/**
		 * Name binding for function: toUpperCase.
		 * @see #toUpperCase(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName toUpperCase = 
			QualifiedName.make(CAL_String.MODULE_NAME, "toUpperCase");

		/**
		 * Converts all of the characters in the specified string to upper case using
		 * the rules of the specified locale.
		 * <p>
		 * Case mapping is based on the Unicode Standard. Note that the returned string
		 * may not have the same length as the original string.
		 * 
		 * @param stringValue (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the string to be converted.
		 * @param locale (CAL type: <code>Cal.Utilities.Locale.Locale</code>)
		 *          the locale whose rules are to be used.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the string converted to uppercase.
		 */
		public static final SourceModel.Expr toUpperCaseInLocale(SourceModel.Expr stringValue, SourceModel.Expr locale) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toUpperCaseInLocale), stringValue, locale});
		}

		/**
		 * @see #toUpperCaseInLocale(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param stringValue
		 * @param locale
		 * @return the SourceModel.Expr representing an application of toUpperCaseInLocale
		 */
		public static final SourceModel.Expr toUpperCaseInLocale(java.lang.String stringValue, SourceModel.Expr locale) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toUpperCaseInLocale), SourceModel.Expr.makeStringValue(stringValue), locale});
		}

		/**
		 * Name binding for function: toUpperCaseInLocale.
		 * @see #toUpperCaseInLocale(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName toUpperCaseInLocale = 
			QualifiedName.make(CAL_String.MODULE_NAME, "toUpperCaseInLocale");

		/**
		 * Trims leading and trailing whitespace from the string.
		 * <p>
		 * For this function, a whitespace is defined to be any character in the range <code>'\u0000'</code>-<code>'\u0020'</code>. 
		 * 
		 * @param stringToTrim (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the string to be trimmed.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          a copy of <code>stringToTrim</code> with leading and trailing whitespace removed.
		 */
		public static final SourceModel.Expr trim(SourceModel.Expr stringToTrim) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.trim), stringToTrim});
		}

		/**
		 * @see #trim(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param stringToTrim
		 * @return the SourceModel.Expr representing an application of trim
		 */
		public static final SourceModel.Expr trim(java.lang.String stringToTrim) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.trim), SourceModel.Expr.makeStringValue(stringToTrim)});
		}

		/**
		 * Name binding for function: trim.
		 * @see #trim(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName trim = 
			QualifiedName.make(CAL_String.MODULE_NAME, "trim");

		/**
		 * Removes leading whitespace from a string. Here the definition of whitespace follows that
		 * of the <code>Cal.Core.Char.isWhitespace</code> function.
		 * @param string (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the string.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the string with leading whitespace removed.
		 */
		public static final SourceModel.Expr trimLeft(SourceModel.Expr string) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.trimLeft), string});
		}

		/**
		 * @see #trimLeft(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param string
		 * @return the SourceModel.Expr representing an application of trimLeft
		 */
		public static final SourceModel.Expr trimLeft(java.lang.String string) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.trimLeft), SourceModel.Expr.makeStringValue(string)});
		}

		/**
		 * Name binding for function: trimLeft.
		 * @see #trimLeft(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName trimLeft = 
			QualifiedName.make(CAL_String.MODULE_NAME, "trimLeft");

		/**
		 * Removes trailing whitespace from a string. Here the definition of whitespace follows that
		 * of the <code>Cal.Core.Char.isWhitespace</code> function.
		 * @param string (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the string.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the string with trailing whitespace removed.
		 */
		public static final SourceModel.Expr trimRight(SourceModel.Expr string) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.trimRight), string});
		}

		/**
		 * @see #trimRight(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param string
		 * @return the SourceModel.Expr representing an application of trimRight
		 */
		public static final SourceModel.Expr trimRight(java.lang.String string) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.trimRight), SourceModel.Expr.makeStringValue(string)});
		}

		/**
		 * Name binding for function: trimRight.
		 * @see #trimRight(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName trimRight = 
			QualifiedName.make(CAL_String.MODULE_NAME, "trimRight");

		/**
		 * Converts a list of strings into a single string, where the original strings are now separated by newlines (<code>'\n'</code>).
		 * @param listOfLines (CAL type: <code>[Cal.Core.Prelude.String]</code>)
		 *          a list of strings to be concatenated into a single string, separated by newlines (<code>'\n'</code>).
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          a concatenation of the strings, separated by newlines (<code>'\n'</code>).
		 */
		public static final SourceModel.Expr unlines(SourceModel.Expr listOfLines) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.unlines), listOfLines});
		}

		/**
		 * Name binding for function: unlines.
		 * @see #unlines(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName unlines = 
			QualifiedName.make(CAL_String.MODULE_NAME, "unlines");

		/**
		 * Converts a list of words into a single string, where the original words are separated by spaces (<code>' '</code>).
		 * @param listOfWords (CAL type: <code>[Cal.Core.Prelude.String]</code>)
		 *          a list of strings to be concatenated into a single string, separated by spaces (<code>' '</code>).
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          a concatenation of the strings, separated by spaces.
		 */
		public static final SourceModel.Expr unwords(SourceModel.Expr listOfWords) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.unwords), listOfWords});
		}

		/**
		 * Name binding for function: unwords.
		 * @see #unwords(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName unwords = 
			QualifiedName.make(CAL_String.MODULE_NAME, "unwords");

		/**
		 * Breaks up a string into a list of strings, one for each word. Whitespace is eliminated. Here whitespace
		 * is defined to be one of the following characters: <code>' '</code>, <code>'\t'</code>, <code>'\n'</code>.
		 * @param string (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the string to be split.
		 * @return (CAL type: <code>[Cal.Core.Prelude.String]</code>) 
		 *          a list of strings, one for each word in the specified string.
		 */
		public static final SourceModel.Expr words(SourceModel.Expr string) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.words), string});
		}

		/**
		 * @see #words(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param string
		 * @return the SourceModel.Expr representing an application of words
		 */
		public static final SourceModel.Expr words(java.lang.String string) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.words), SourceModel.Expr.makeStringValue(string)});
		}

		/**
		 * Name binding for function: words.
		 * @see #words(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName words = 
			QualifiedName.make(CAL_String.MODULE_NAME, "words");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = 263509704;

}
