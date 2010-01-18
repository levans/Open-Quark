/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_Range.java)
 * was generated from CAL module: Cal.Utilities.Range.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Utilities.Range module from Java code.
 *  
 * Creation date: Fri Mar 09 12:29:15 PST 2007
 * --!>
 *  
 */

package org.openquark.cal.module.Cal.Utilities;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * Defines the <code>Cal.Utilities.Range.Range</code> type as well as core support functions associated with ranges. 
 * The range type is similar to ranges in Crystal Reports, but the element type can be any orderable type.
 * @author Iulian Radu
 */
public final class CAL_Range {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Utilities.Range");

	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Utilities.Range module.
	 */
	public static final class TypeConstructors {
		/** Name binding for TypeConsApp: JRange. */
		public static final QualifiedName JRange = 
			QualifiedName.make(CAL_Range.MODULE_NAME, "JRange");

		/**
		 * Type Invariant: left endpoint &lt;= right endpoint
		 */
		public static final QualifiedName Range = 
			QualifiedName.make(CAL_Range.MODULE_NAME, "Range");

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Utilities.Range module.
	 */
	public static final class Functions {
		/**
		 * Indicates whether the first range succeeds the second.
		 * @param r1 (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Utilities.Range.Range a</code>)
		 * @param r2 (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Utilities.Range.Range a</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr afterRange(SourceModel.Expr r1, SourceModel.Expr r2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.afterRange), r1, r2});
		}

		/**
		 * Name binding for function: afterRange.
		 * @see #afterRange(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName afterRange = 
			QualifiedName.make(CAL_Range.MODULE_NAME, "afterRange");

		/**
		 * Indicates whether the first range preceeds the second.
		 * @param r1 (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Utilities.Range.Range a</code>)
		 * @param r2 (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Utilities.Range.Range a</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr beforeRange(SourceModel.Expr r1, SourceModel.Expr r2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.beforeRange), r1, r2});
		}

		/**
		 * Name binding for function: beforeRange.
		 * @see #beforeRange(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName beforeRange = 
			QualifiedName.make(CAL_Range.MODULE_NAME, "beforeRange");

		/**
		 * Complements the specified list of ranges.
		 * The ranges returned will be sorted by increasing start time.
		 * <p>
		 * Complexity: time O(n), space O(n)
		 * 
		 * @param rl (CAL type: <code>Cal.Core.Prelude.Ord a => [Cal.Utilities.Range.Range a]</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Ord a => [Cal.Utilities.Range.Range a]</code>) 
		 */
		public static final SourceModel.Expr complementRanges(SourceModel.Expr rl) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.complementRanges), rl});
		}

		/**
		 * Name binding for function: complementRanges.
		 * @see #complementRanges(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName complementRanges = 
			QualifiedName.make(CAL_Range.MODULE_NAME, "complementRanges");

		/**
		 * Consolidate the specified ranges
		 * The returned ranges will be ordered by increasing start time
		 * <p>
		 * Complexity: time O(n), space O(n)
		 * 
		 * @param rl (CAL type: <code>Cal.Core.Prelude.Ord a => [Cal.Utilities.Range.Range a]</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Ord a => [Cal.Utilities.Range.Range a]</code>) 
		 */
		public static final SourceModel.Expr consolidateRanges(SourceModel.Expr rl) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.consolidateRanges), rl});
		}

		/**
		 * Name binding for function: consolidateRanges.
		 * @see #consolidateRanges(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName consolidateRanges = 
			QualifiedName.make(CAL_Range.MODULE_NAME, "consolidateRanges");

		/**
		 * Returns the difference ranges resulting from of subtracting the second range list from the first.
		 * <p>
		 * Assumed: The ranges are non overlapping and sorted by increasing start time.
		 * <p>
		 * Complexity: time O(n+m), space O(n+m)
		 * 
		 * @param rl1 (CAL type: <code>Cal.Core.Prelude.Ord a => [Cal.Utilities.Range.Range a]</code>)
		 * @param rl2 (CAL type: <code>Cal.Core.Prelude.Ord a => [Cal.Utilities.Range.Range a]</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Ord a => [Cal.Utilities.Range.Range a]</code>) 
		 */
		public static final SourceModel.Expr differenceRanges(SourceModel.Expr rl1, SourceModel.Expr rl2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.differenceRanges), rl1, rl2});
		}

		/**
		 * Name binding for function: differenceRanges.
		 * @see #differenceRanges(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName differenceRanges = 
			QualifiedName.make(CAL_Range.MODULE_NAME, "differenceRanges");

		/**
		 * Returns whether the range has a left endpoint.
		 * @param r (CAL type: <code>Cal.Utilities.Range.Range a</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr hasLeftEndpoint(SourceModel.Expr r) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.hasLeftEndpoint), r});
		}

		/**
		 * Name binding for function: hasLeftEndpoint.
		 * @see #hasLeftEndpoint(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName hasLeftEndpoint = 
			QualifiedName.make(CAL_Range.MODULE_NAME, "hasLeftEndpoint");

		/**
		 * Returns whether the range has a right endpoint.
		 * @param r (CAL type: <code>Cal.Utilities.Range.Range a</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr hasRightEndpoint(SourceModel.Expr r) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.hasRightEndpoint), r});
		}

		/**
		 * Name binding for function: hasRightEndpoint.
		 * @see #hasRightEndpoint(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName hasRightEndpoint = 
			QualifiedName.make(CAL_Range.MODULE_NAME, "hasRightEndpoint");

		/**
		 * Returns whether the specified value falls within the range.
		 * <p>
		 * Complexity: time O(n), space O(n)
		 * 
		 * @param x (CAL type: <code>Cal.Core.Prelude.Ord a => a</code>)
		 * @param r (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Utilities.Range.Range a</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr inRange(SourceModel.Expr x, SourceModel.Expr r) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inRange), x, r});
		}

		/**
		 * Name binding for function: inRange.
		 * @see #inRange(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName inRange = 
			QualifiedName.make(CAL_Range.MODULE_NAME, "inRange");

		/**
		 * Returns whether the specified value is contained in the list of ranges.
		 * <p>
		 * Complexity: time O(n), space O(n)
		 * 
		 * @param x (CAL type: <code>Cal.Core.Prelude.Ord a => a</code>)
		 * @param rl (CAL type: <code>Cal.Core.Prelude.Ord a => [Cal.Utilities.Range.Range a]</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr inRangeList(SourceModel.Expr x, SourceModel.Expr rl) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inRangeList), x, rl});
		}

		/**
		 * Name binding for function: inRangeList.
		 * @see #inRangeList(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName inRangeList = 
			QualifiedName.make(CAL_Range.MODULE_NAME, "inRangeList");

		/**
		 * Returns whether the range includes its left endpoint.
		 * @param r (CAL type: <code>Cal.Utilities.Range.Range a</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr includesLeftEndpoint(SourceModel.Expr r) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.includesLeftEndpoint), r});
		}

		/**
		 * Name binding for function: includesLeftEndpoint.
		 * @see #includesLeftEndpoint(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName includesLeftEndpoint = 
			QualifiedName.make(CAL_Range.MODULE_NAME, "includesLeftEndpoint");

		/**
		 * Returns whether the range includes a right endpoint.
		 * @param r (CAL type: <code>Cal.Utilities.Range.Range a</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr includesRightEndpoint(SourceModel.Expr r) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.includesRightEndpoint), r});
		}

		/**
		 * Name binding for function: includesRightEndpoint.
		 * @see #includesRightEndpoint(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName includesRightEndpoint = 
			QualifiedName.make(CAL_Range.MODULE_NAME, "includesRightEndpoint");

		/**
		 * Converts a <code>Cal.Utilities.Range.JRange</code> value into a <code>Cal.Utilities.Range.Range</code> value.
		 * @param range (CAL type: <code>Cal.Utilities.Range.JRange</code>)
		 *          the <code>JRange</code> value to be converted.
		 * @return (CAL type: <code>Cal.Core.Prelude.Inputable a => Cal.Utilities.Range.Range a</code>) 
		 *          the corresponding <code>Range</code> value.
		 */
		public static final SourceModel.Expr inputRange(SourceModel.Expr range) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inputRange), range});
		}

		/**
		 * Name binding for function: inputRange.
		 * @see #inputRange(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName inputRange = 
			QualifiedName.make(CAL_Range.MODULE_NAME, "inputRange");

		/**
		 * Converts a <code>Cal.Utilities.Range.JRange</code> value into a <code>Cal.Utilities.Range.Range</code> value, with an endpoint transformation function.
		 * @param range (CAL type: <code>Cal.Utilities.Range.JRange</code>)
		 *          the <code>JRange</code> value to be converted.
		 * @param arg1Function (CAL type: <code>Cal.Core.Prelude.JObject -> a</code>)
		 *          the function to be applied to the endpoints of the range.
		 * @return (CAL type: <code>Cal.Utilities.Range.Range a</code>) 
		 *          the corresponding <code>Range</code> value.
		 */
		public static final SourceModel.Expr inputRangeWith(SourceModel.Expr range, SourceModel.Expr arg1Function) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inputRangeWith), range, arg1Function});
		}

		/**
		 * Name binding for function: inputRangeWith.
		 * @see #inputRangeWith(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName inputRangeWith = 
			QualifiedName.make(CAL_Range.MODULE_NAME, "inputRangeWith");

		/**
		 * Intersects the ranges in the specified list.
		 * @param rl (CAL type: <code>Cal.Core.Prelude.Ord a => [Cal.Utilities.Range.Range a]</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Core.Prelude.Maybe (Cal.Utilities.Range.Range a)</code>) 
		 */
		public static final SourceModel.Expr intersectRanges(SourceModel.Expr rl) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intersectRanges), rl});
		}

		/**
		 * Name binding for function: intersectRanges.
		 * @see #intersectRanges(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intersectRanges = 
			QualifiedName.make(CAL_Range.MODULE_NAME, "intersectRanges");

		/**
		 * Intersects two ranges.
		 * <p>
		 * Returns <code>Cal.Core.Prelude.Nothing</code> if the ranges do not overlap, or <code>Cal.Core.Prelude.Just ir</code> where <code>ir</code> is the intersection range
		 * <p>
		 * Complexity: time O(1), space O(1)
		 * 
		 * @param r1 (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Utilities.Range.Range a</code>)
		 * @param r2 (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Utilities.Range.Range a</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Core.Prelude.Maybe (Cal.Utilities.Range.Range a)</code>) 
		 */
		public static final SourceModel.Expr intersectTwoRanges(SourceModel.Expr r1, SourceModel.Expr r2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intersectTwoRanges), r1, r2});
		}

		/**
		 * Name binding for function: intersectTwoRanges.
		 * @see #intersectTwoRanges(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intersectTwoRanges = 
			QualifiedName.make(CAL_Range.MODULE_NAME, "intersectTwoRanges");

		/**
		 * Returns whether the range is algebraically of the sort that it can never
		 * contain a data value e.g. <code>Cal.Utilities.Range.BetweenExcludingEndpoints 2 2</code>.
		 * @param r (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Utilities.Range.Range a</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr isRedundantRange(SourceModel.Expr r) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isRedundantRange), r});
		}

		/**
		 * Name binding for function: isRedundantRange.
		 * @see #isRedundantRange(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isRedundantRange = 
			QualifiedName.make(CAL_Range.MODULE_NAME, "isRedundantRange");

		/**
		 * Returns the left endpoint of the range.
		 * @param r (CAL type: <code>Cal.Utilities.Range.Range a</code>)
		 * @return (CAL type: <code>a</code>) 
		 */
		public static final SourceModel.Expr leftEndpoint(SourceModel.Expr r) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.leftEndpoint), r});
		}

		/**
		 * Name binding for function: leftEndpoint.
		 * @see #leftEndpoint(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName leftEndpoint = 
			QualifiedName.make(CAL_Range.MODULE_NAME, "leftEndpoint");

		/**
		 * Constructs a range between two endpoings, not including either.
		 * @param x (CAL type: <code>Cal.Core.Prelude.Ord a => a</code>)
		 * @param y (CAL type: <code>Cal.Core.Prelude.Ord a => a</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Utilities.Range.Range a</code>) 
		 */
		public static final SourceModel.Expr makeBetweenExcludingEndpointsRange(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeBetweenExcludingEndpointsRange), x, y});
		}

		/**
		 * Name binding for function: makeBetweenExcludingEndpointsRange.
		 * @see #makeBetweenExcludingEndpointsRange(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeBetweenExcludingEndpointsRange = 
			QualifiedName.make(
				CAL_Range.MODULE_NAME, 
				"makeBetweenExcludingEndpointsRange");

		/**
		 * Constructs a range between two endpoints, including both.
		 * @param x (CAL type: <code>Cal.Core.Prelude.Ord a => a</code>)
		 * @param y (CAL type: <code>Cal.Core.Prelude.Ord a => a</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Utilities.Range.Range a</code>) 
		 */
		public static final SourceModel.Expr makeBetweenIncludingEndpointsRange(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeBetweenIncludingEndpointsRange), x, y});
		}

		/**
		 * Name binding for function: makeBetweenIncludingEndpointsRange.
		 * @see #makeBetweenIncludingEndpointsRange(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeBetweenIncludingEndpointsRange = 
			QualifiedName.make(
				CAL_Range.MODULE_NAME, 
				"makeBetweenIncludingEndpointsRange");

		/**
		 * Constructs a range between two endpoints, including the left.
		 * @param x (CAL type: <code>Cal.Core.Prelude.Ord a => a</code>)
		 * @param y (CAL type: <code>Cal.Core.Prelude.Ord a => a</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Utilities.Range.Range a</code>) 
		 */
		public static final SourceModel.Expr makeBetweenIncludingLeftEndpointRange(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeBetweenIncludingLeftEndpointRange), x, y});
		}

		/**
		 * Name binding for function: makeBetweenIncludingLeftEndpointRange.
		 * @see #makeBetweenIncludingLeftEndpointRange(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeBetweenIncludingLeftEndpointRange = 
			QualifiedName.make(
				CAL_Range.MODULE_NAME, 
				"makeBetweenIncludingLeftEndpointRange");

		/**
		 * Constructs a range between two endpoings, including the right.
		 * @param x (CAL type: <code>Cal.Core.Prelude.Ord a => a</code>)
		 * @param y (CAL type: <code>Cal.Core.Prelude.Ord a => a</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Utilities.Range.Range a</code>) 
		 */
		public static final SourceModel.Expr makeBetweenIncludingRightEndpointRange(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeBetweenIncludingRightEndpointRange), x, y});
		}

		/**
		 * Name binding for function: makeBetweenIncludingRightEndpointRange.
		 * @see #makeBetweenIncludingRightEndpointRange(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeBetweenIncludingRightEndpointRange = 
			QualifiedName.make(
				CAL_Range.MODULE_NAME, 
				"makeBetweenIncludingRightEndpointRange");

		/**
		 * Constructs a range including the entire space.
		 * @return (CAL type: <code>Cal.Utilities.Range.Range a</code>) 
		 */
		public static final SourceModel.Expr makeEntireRange() {
			return SourceModel.Expr.Var.make(Functions.makeEntireRange);
		}

		/**
		 * Name binding for function: makeEntireRange.
		 * @see #makeEntireRange()
		 */
		public static final QualifiedName makeEntireRange = 
			QualifiedName.make(CAL_Range.MODULE_NAME, "makeEntireRange");

		/**
		 * Constructs a range strictly greater than <code>x</code>.
		 * @param x (CAL type: <code>a</code>)
		 * @return (CAL type: <code>Cal.Utilities.Range.Range a</code>) 
		 */
		public static final SourceModel.Expr makeIsGreaterThanEqualsRange(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeIsGreaterThanEqualsRange), x});
		}

		/**
		 * Name binding for function: makeIsGreaterThanEqualsRange.
		 * @see #makeIsGreaterThanEqualsRange(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeIsGreaterThanEqualsRange = 
			QualifiedName.make(
				CAL_Range.MODULE_NAME, 
				"makeIsGreaterThanEqualsRange");

		/**
		 * Constructs a range strictly less than <code>x</code>
		 * @param x (CAL type: <code>a</code>)
		 * @return (CAL type: <code>Cal.Utilities.Range.Range a</code>) 
		 */
		public static final SourceModel.Expr makeIsGreaterThanRange(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeIsGreaterThanRange), x});
		}

		/**
		 * Name binding for function: makeIsGreaterThanRange.
		 * @see #makeIsGreaterThanRange(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeIsGreaterThanRange = 
			QualifiedName.make(CAL_Range.MODULE_NAME, "makeIsGreaterThanRange");

		/**
		 * Constructs a range less than or equals to <code>x</code>.
		 * @param x (CAL type: <code>a</code>)
		 * @return (CAL type: <code>Cal.Utilities.Range.Range a</code>) 
		 */
		public static final SourceModel.Expr makeIsLessThanEqualsRange(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeIsLessThanEqualsRange), x});
		}

		/**
		 * Name binding for function: makeIsLessThanEqualsRange.
		 * @see #makeIsLessThanEqualsRange(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeIsLessThanEqualsRange = 
			QualifiedName.make(
				CAL_Range.MODULE_NAME, 
				"makeIsLessThanEqualsRange");

		/**
		 * Constructs a range strictly less than <code>x</code>.
		 * @param x (CAL type: <code>a</code>)
		 * @return (CAL type: <code>Cal.Utilities.Range.Range a</code>) 
		 */
		public static final SourceModel.Expr makeIsLessThanRange(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeIsLessThanRange), x});
		}

		/**
		 * Name binding for function: makeIsLessThanRange.
		 * @see #makeIsLessThanRange(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeIsLessThanRange = 
			QualifiedName.make(CAL_Range.MODULE_NAME, "makeIsLessThanRange");

		/**
		 * Constructs a <code>Cal.Utilities.Range.Range</code> by describing its endpoints as <code>Cal.Core.Prelude.Maybe</code> pairs.
		 * Each pair contains the value of the endpoint and a <code>Cal.Core.Prelude.Boolean</code> to signify whether
		 * the endpoint is inclusive.
		 * @param leftEndpoint (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Core.Prelude.Maybe (Cal.Core.Prelude.Boolean, a)</code>)
		 *          <code>Cal.Core.Prelude.Maybe</code> a pair describing the left enpoint of the range.  If <code>Cal.Core.Prelude.Nothing</code>, the endpoint is infinite.
		 * @param rightEndpoint (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Core.Prelude.Maybe (Cal.Core.Prelude.Boolean, a)</code>)
		 *          <code>Cal.Core.Prelude.Maybe</code> a pair describing the right endpoint of the range.  If <code>Cal.Core.Prelude.Nothing</code>, the endpoint is infinite.
		 * @return (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Utilities.Range.Range a</code>) 
		 *          A <code>Cal.Utilities.Range.Range</code> described by the specified endpoints.
		 */
		public static final SourceModel.Expr makeRangeFromEndpoints(SourceModel.Expr leftEndpoint, SourceModel.Expr rightEndpoint) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeRangeFromEndpoints), leftEndpoint, rightEndpoint});
		}

		/**
		 * Name binding for function: makeRangeFromEndpoints.
		 * @see #makeRangeFromEndpoints(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeRangeFromEndpoints = 
			QualifiedName.make(CAL_Range.MODULE_NAME, "makeRangeFromEndpoints");

		/**
		 * Applies the specified function to the endpoints of the range.
		 * @param f (CAL type: <code>Cal.Core.Prelude.Ord b => a -> b</code>)
		 * @param r (CAL type: <code>Cal.Utilities.Range.Range a</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Ord b => Cal.Utilities.Range.Range b</code>) 
		 */
		public static final SourceModel.Expr mapRange(SourceModel.Expr f, SourceModel.Expr r) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.mapRange), f, r});
		}

		/**
		 * Name binding for function: mapRange.
		 * @see #mapRange(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName mapRange = 
			QualifiedName.make(CAL_Range.MODULE_NAME, "mapRange");

		/**
		 * Applies the specified function to the endpoints of each ranges in the list.
		 * @param f (CAL type: <code>Cal.Core.Prelude.Ord b => a -> b</code>)
		 * @param rl (CAL type: <code>[Cal.Utilities.Range.Range a]</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Ord b => [Cal.Utilities.Range.Range b]</code>) 
		 */
		public static final SourceModel.Expr mapRanges(SourceModel.Expr f, SourceModel.Expr rl) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.mapRanges), f, rl});
		}

		/**
		 * Name binding for function: mapRanges.
		 * @see #mapRanges(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName mapRanges = 
			QualifiedName.make(CAL_Range.MODULE_NAME, "mapRanges");

		/**
		 * Converts a <code>Cal.Utilities.Range.Range</code> value into a <code>Cal.Utilities.Range.JRange</code> value.
		 * @param range (CAL type: <code>Cal.Core.Prelude.Outputable a => Cal.Utilities.Range.Range a</code>)
		 *          the <code>Range</code> value to be converted.
		 * @return (CAL type: <code>Cal.Utilities.Range.JRange</code>) 
		 *          the corresponding <code>JRange</code> value.
		 */
		public static final SourceModel.Expr outputRange(SourceModel.Expr range) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputRange), range});
		}

		/**
		 * Name binding for function: outputRange.
		 * @see #outputRange(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName outputRange = 
			QualifiedName.make(CAL_Range.MODULE_NAME, "outputRange");

		/**
		 * Converts a <code>Cal.Utilities.Range.Range</code> value into a <code>Cal.Utilities.Range.JRange</code> value, with an endpoint transformation function.
		 * @param range (CAL type: <code>Cal.Utilities.Range.Range a</code>)
		 *          the <code>Range</code> value to be converted.
		 * @param arg1Function (CAL type: <code>a -> Cal.Core.Prelude.JObject</code>)
		 *          the function to be applied to the endpoints of the range.
		 * @return (CAL type: <code>Cal.Utilities.Range.JRange</code>) 
		 *          the corresponding <code>JRange</code> value.
		 */
		public static final SourceModel.Expr outputRangeWith(SourceModel.Expr range, SourceModel.Expr arg1Function) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputRangeWith), range, arg1Function});
		}

		/**
		 * Name binding for function: outputRangeWith.
		 * @see #outputRangeWith(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName outputRangeWith = 
			QualifiedName.make(CAL_Range.MODULE_NAME, "outputRangeWith");

		/**
		 * Returns the right endpoint of the range.
		 * @param r (CAL type: <code>Cal.Utilities.Range.Range a</code>)
		 * @return (CAL type: <code>a</code>) 
		 */
		public static final SourceModel.Expr rightEndpoint(SourceModel.Expr r) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.rightEndpoint), r});
		}

		/**
		 * Name binding for function: rightEndpoint.
		 * @see #rightEndpoint(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName rightEndpoint = 
			QualifiedName.make(CAL_Range.MODULE_NAME, "rightEndpoint");

		/**
		 * Strips all redundant ranges from the specified list.
		 * <p>
		 * Complexity: time O(n), space O(n)
		 * 
		 * @param rl (CAL type: <code>Cal.Core.Prelude.Eq a => [Cal.Utilities.Range.Range a]</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Eq a => [Cal.Utilities.Range.Range a]</code>) 
		 */
		public static final SourceModel.Expr stripRedundantRanges(SourceModel.Expr rl) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.stripRedundantRanges), rl});
		}

		/**
		 * Name binding for function: stripRedundantRanges.
		 * @see #stripRedundantRanges(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName stripRedundantRanges = 
			QualifiedName.make(CAL_Range.MODULE_NAME, "stripRedundantRanges");

		/**
		 * Computes the symmetric difference between the two range lists.
		 * <p>
		 * Assumed: the lists are non-overlaping and sorted by start time (consolidate if this is not so)
		 * <p>
		 * Complexity: time O(n+m) space O(n+m)
		 * 
		 * @param rl1 (CAL type: <code>Cal.Core.Prelude.Ord a => [Cal.Utilities.Range.Range a]</code>)
		 * @param rl2 (CAL type: <code>Cal.Core.Prelude.Ord a => [Cal.Utilities.Range.Range a]</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Ord a => [Cal.Utilities.Range.Range a]</code>) 
		 */
		public static final SourceModel.Expr symmetricDifferenceRanges(SourceModel.Expr rl1, SourceModel.Expr rl2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.symmetricDifferenceRanges), rl1, rl2});
		}

		/**
		 * Name binding for function: symmetricDifferenceRanges.
		 * @see #symmetricDifferenceRanges(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName symmetricDifferenceRanges = 
			QualifiedName.make(
				CAL_Range.MODULE_NAME, 
				"symmetricDifferenceRanges");

		/**
		 * Helper binding method for function: testRangeModule. 
		 * @return the SourceModule.expr representing an application of testRangeModule
		 */
		public static final SourceModel.Expr testRangeModule() {
			return SourceModel.Expr.Var.make(Functions.testRangeModule);
		}

		/**
		 * Name binding for function: testRangeModule.
		 * @see #testRangeModule()
		 */
		public static final QualifiedName testRangeModule = 
			QualifiedName.make(CAL_Range.MODULE_NAME, "testRangeModule");

		/**
		 * Unifies the two lists of ranges into a single list
		 * The returned ranges will be ordered by increasing start time
		 * <p>
		 * Complexity: time O(n+m), space O(n+m)
		 * 
		 * @param rl1 (CAL type: <code>Cal.Core.Prelude.Ord a => [Cal.Utilities.Range.Range a]</code>)
		 * @param rl2 (CAL type: <code>Cal.Core.Prelude.Ord a => [Cal.Utilities.Range.Range a]</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Ord a => [Cal.Utilities.Range.Range a]</code>) 
		 */
		public static final SourceModel.Expr unionRanges(SourceModel.Expr rl1, SourceModel.Expr rl2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.unionRanges), rl1, rl2});
		}

		/**
		 * Name binding for function: unionRanges.
		 * @see #unionRanges(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName unionRanges = 
			QualifiedName.make(CAL_Range.MODULE_NAME, "unionRanges");

		/**
		 * Unifies two ranges.
		 * <p>
		 * Returns <code>[unifiedRange]</code> if ranges overlap, or <code>[rangeA, rangeB]</code> in start order
		 * <p>
		 * Complexity: time O(1), space O(1)
		 * 
		 * @param r1 (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Utilities.Range.Range a</code>)
		 * @param r2 (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Utilities.Range.Range a</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Ord a => [Cal.Utilities.Range.Range a]</code>) 
		 */
		public static final SourceModel.Expr unionTwoRanges(SourceModel.Expr r1, SourceModel.Expr r2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.unionTwoRanges), r1, r2});
		}

		/**
		 * Name binding for function: unionTwoRanges.
		 * @see #unionTwoRanges(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName unionTwoRanges = 
			QualifiedName.make(CAL_Range.MODULE_NAME, "unionTwoRanges");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = -831709652;

}
