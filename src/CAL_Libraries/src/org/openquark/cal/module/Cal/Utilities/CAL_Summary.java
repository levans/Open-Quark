/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_Summary.java)
 * was generated from CAL module: Cal.Utilities.Summary.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Utilities.Summary module from Java code.
 *  
 * Creation date: Fri May 11 17:18:20 PDT 2007
 * --!>
 *  
 */

package org.openquark.cal.module.Cal.Utilities;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * This is the CAL Summary module.  The Summary module provides mathematical functions
 * for summarizing sets of data (eg, <code>Cal.Utilities.Summary.average</code>, <code>Cal.Utilities.Summary.populationStandardDeviation</code>, etc.).
 * @author Bo Ilic
 * @author James Wright
 */
public final class CAL_Summary {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Utilities.Summary");

	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Utilities.Summary module.
	 */
	public static final class TypeConstructors {
		/** Name binding for TypeConsApp: Quartile. */
		public static final QualifiedName Quartile = 
			QualifiedName.make(CAL_Summary.MODULE_NAME, "Quartile");

	}
	/**
	 * This inner class (DataConstructors) contains constants
	 * and methods related to binding to CAL DataConstructors in the Cal.Utilities.Summary module.
	 */
	public static final class DataConstructors {
		/*
		 * DataConstructors for the Cal.Utilities.Summary.Quartile data type.
		 */

		/**
		 * Binding for DataConstructor: Cal.Utilities.Summary.Minimum_0thPercentile.
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.Summary.Minimum_0thPercentile
		 */
		public static final SourceModel.Expr Minimum_0thPercentile() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.Minimum_0thPercentile);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.Summary.Minimum_0thPercentile.
		 * @see #Minimum_0thPercentile()
		 */
		public static final QualifiedName Minimum_0thPercentile = 
			QualifiedName.make(CAL_Summary.MODULE_NAME, "Minimum_0thPercentile");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.Summary.Minimum_0thPercentile.
		 * @see #Minimum_0thPercentile()
		 */
		public static final int Minimum_0thPercentile_ordinal = 0;

		/**
		 * Binding for DataConstructor: Cal.Utilities.Summary.FirstQuartile_25thPercentile.
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.Summary.FirstQuartile_25thPercentile
		 */
		public static final SourceModel.Expr FirstQuartile_25thPercentile() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.FirstQuartile_25thPercentile);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.Summary.FirstQuartile_25thPercentile.
		 * @see #FirstQuartile_25thPercentile()
		 */
		public static final QualifiedName FirstQuartile_25thPercentile = 
			QualifiedName.make(
				CAL_Summary.MODULE_NAME, 
				"FirstQuartile_25thPercentile");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.Summary.FirstQuartile_25thPercentile.
		 * @see #FirstQuartile_25thPercentile()
		 */
		public static final int FirstQuartile_25thPercentile_ordinal = 1;

		/**
		 * Binding for DataConstructor: Cal.Utilities.Summary.Median_50thPercentile.
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.Summary.Median_50thPercentile
		 */
		public static final SourceModel.Expr Median_50thPercentile() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.Median_50thPercentile);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.Summary.Median_50thPercentile.
		 * @see #Median_50thPercentile()
		 */
		public static final QualifiedName Median_50thPercentile = 
			QualifiedName.make(CAL_Summary.MODULE_NAME, "Median_50thPercentile");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.Summary.Median_50thPercentile.
		 * @see #Median_50thPercentile()
		 */
		public static final int Median_50thPercentile_ordinal = 2;

		/**
		 * Binding for DataConstructor: Cal.Utilities.Summary.ThirdQuartile_75thPercentile.
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.Summary.ThirdQuartile_75thPercentile
		 */
		public static final SourceModel.Expr ThirdQuartile_75thPercentile() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.ThirdQuartile_75thPercentile);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.Summary.ThirdQuartile_75thPercentile.
		 * @see #ThirdQuartile_75thPercentile()
		 */
		public static final QualifiedName ThirdQuartile_75thPercentile = 
			QualifiedName.make(
				CAL_Summary.MODULE_NAME, 
				"ThirdQuartile_75thPercentile");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.Summary.ThirdQuartile_75thPercentile.
		 * @see #ThirdQuartile_75thPercentile()
		 */
		public static final int ThirdQuartile_75thPercentile_ordinal = 3;

		/**
		 * Binding for DataConstructor: Cal.Utilities.Summary.Maximum_100thPercentile.
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.Summary.Maximum_100thPercentile
		 */
		public static final SourceModel.Expr Maximum_100thPercentile() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.Maximum_100thPercentile);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.Summary.Maximum_100thPercentile.
		 * @see #Maximum_100thPercentile()
		 */
		public static final QualifiedName Maximum_100thPercentile = 
			QualifiedName.make(
				CAL_Summary.MODULE_NAME, 
				"Maximum_100thPercentile");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.Summary.Maximum_100thPercentile.
		 * @see #Maximum_100thPercentile()
		 */
		public static final int Maximum_100thPercentile_ordinal = 4;

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Utilities.Summary module.
	 */
	public static final class Functions {
		/**
		 * Computes the average value of a list by first converting all elements of the list to <code>Cal.Core.Prelude.Double</code>
		 * values and then averaging them. It return <code>Cal.Core.Prelude.notANumber</code> for an empty list.
		 * 
		 * <pre>
		 *  average [x1, x2, ..., xn] == ((Cal.Core.Prelude.toDouble x1) + (Cal.Core.Prelude.toDouble x2)... + (Cal.Core.Prelude.toDouble xn)) / (Cal.Core.Prelude.toDouble n)</pre>
		 * 
		 * Runtime performance is O(n).  Only one pass over the data is required.
		 * @param values (CAL type: <code>Cal.Core.Prelude.Num a => [a]</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 */
		public static final SourceModel.Expr average(SourceModel.Expr values) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.average), values});
		}

		/**
		 * Name binding for function: average.
		 * @see #average(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName average = 
			QualifiedName.make(CAL_Summary.MODULE_NAME, "average");

		/**
		 * A custom version of <code>Cal.Utilities.Summary.average</code> that ignores NaN values.  The NaN values in the list will not contribute
		 * to either the summation or the count and will have no effect on the result.  If the list is empty, or
		 * contains only NaN values, then NaN is returned.
		 * <p>
		 * Runtime performance is O(n).  Only one pass over the data is required.
		 * 
		 * @param values (CAL type: <code>[Cal.Core.Prelude.Double]</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 */
		public static final SourceModel.Expr averageIgnoreNaN(SourceModel.Expr values) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.averageIgnoreNaN), values});
		}

		/**
		 * Name binding for function: averageIgnoreNaN.
		 * @see #averageIgnoreNaN(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName averageIgnoreNaN = 
			QualifiedName.make(CAL_Summary.MODULE_NAME, "averageIgnoreNaN");

		/**
		 * Returns the correlation coefficient between lists <code>list1</code> and <code>list2</code>.  The return value ranges 
		 * from -1.0 (for perfect negative correlation) to 1.0 (for perfect positive correlation), 
		 * with perfectly uncorrelated lists returning 0.0.  The lists should be of equal length.
		 * If one list is longer than the other, then the extra values will be ignored.
		 * Each list must contain at least 2 elements; Empty or singleton lists will return <code>Cal.Core.Prelude.notANumber</code>.
		 * Values from both lists are converted to Doubles before calculations begin.
		 * <p>
		 * Runtime performance is O(n).  Only one pass over the data is required.
		 * 
		 * @param list1 (CAL type: <code>Cal.Core.Prelude.Num a => [a]</code>)
		 * @param list2 (CAL type: <code>Cal.Core.Prelude.Num b => [b]</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 */
		public static final SourceModel.Expr correlation(SourceModel.Expr list1, SourceModel.Expr list2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.correlation), list1, list2});
		}

		/**
		 * Name binding for function: correlation.
		 * @see #correlation(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName correlation = 
			QualifiedName.make(CAL_Summary.MODULE_NAME, "correlation");

		/**
		 * Counts the number of non-NaN values in a list.  Any child whose value is NaN is ignored and will have
		 * no effect on the result.  If the list is empty, or contains only NaN values, then 0.0 is returned.
		 * <p>
		 * Runtime performance is O(n).  Only one pass over the data is required.
		 * 
		 * @param values (CAL type: <code>[Cal.Core.Prelude.Double]</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 */
		public static final SourceModel.Expr countIgnoreNaN(SourceModel.Expr values) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.countIgnoreNaN), values});
		}

		/**
		 * Name binding for function: countIgnoreNaN.
		 * @see #countIgnoreNaN(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName countIgnoreNaN = 
			QualifiedName.make(CAL_Summary.MODULE_NAME, "countIgnoreNaN");

		/**
		 * Returns the covariance between lists <code>list1</code> and <code>list2</code>.  The return value is not bounded in
		 * either the positive or negative directions.  The lists should be of equal length.  If
		 * one list is longer than the other, then the extra values will be ignored.
		 * Each list must contain at least 2 elements.  Empty or singleton lists will return
		 * <code>Cal.Core.Prelude.notANumber</code>.
		 * <p>
		 * Runtime performance is O(n).  Only one pass over the data is required.
		 * 
		 * @param list1 (CAL type: <code>Cal.Core.Prelude.Num a => [a]</code>)
		 * @param list2 (CAL type: <code>Cal.Core.Prelude.Num b => [b]</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 */
		public static final SourceModel.Expr covariance(SourceModel.Expr list1, SourceModel.Expr list2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.covariance), list1, list2});
		}

		/**
		 * Name binding for function: covariance.
		 * @see #covariance(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName covariance = 
			QualifiedName.make(CAL_Summary.MODULE_NAME, "covariance");

		/**
		 * Returns the number of distinct values that occur in values.
		 * <p>
		 * Runtime performce is O(n(lg n))
		 * 
		 * @param values (CAL type: <code>Cal.Core.Prelude.Ord a => [a]</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 */
		public static final SourceModel.Expr distinctCount(SourceModel.Expr values) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.distinctCount), values});
		}

		/**
		 * Name binding for function: distinctCount.
		 * @see #distinctCount(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName distinctCount = 
			QualifiedName.make(CAL_Summary.MODULE_NAME, "distinctCount");

		/**
		 * A custom version of <code>Cal.Collections.List.maximum</code> that ignores NaN values.  The NaN values in the list will not contribute
		 * to either the summation or the maximum and will have no effect on the result.  If the list is empty, or
		 * contains only NaN values, then NaN is returned.
		 * <p>
		 * Runtime performance is O(n).  Only one pass over the data is required.
		 * 
		 * @param values (CAL type: <code>[Cal.Core.Prelude.Double]</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 */
		public static final SourceModel.Expr maximumIgnoreNaN(SourceModel.Expr values) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.maximumIgnoreNaN), values});
		}

		/**
		 * Name binding for function: maximumIgnoreNaN.
		 * @see #maximumIgnoreNaN(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName maximumIgnoreNaN = 
			QualifiedName.make(CAL_Summary.MODULE_NAME, "maximumIgnoreNaN");

		/**
		 * Returns the median of values, using interpolation when the number of values
		 * is even.  eg, <code>median [1,2,3,4,5,6]</code> is 3.5.
		 * @param values (CAL type: <code>Cal.Core.Prelude.Num a => [a]</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 */
		public static final SourceModel.Expr median(SourceModel.Expr values) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.median), values});
		}

		/**
		 * Name binding for function: median.
		 * @see #median(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName median = 
			QualifiedName.make(CAL_Summary.MODULE_NAME, "median");

		/**
		 * A custom version of <code>Cal.Collections.List.minimum</code> that ignores NaN values.  The NaN values in the list will not contribute
		 * to either the summation or the minimum and will have no effect on the result.  If the list is empty, or
		 * contains only NaN values, then NaN is returned.
		 * <p>
		 * Runtime performance is O(n).  Only one pass over the data is required.
		 * 
		 * @param values (CAL type: <code>[Cal.Core.Prelude.Double]</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 */
		public static final SourceModel.Expr minimumIgnoreNaN(SourceModel.Expr values) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.minimumIgnoreNaN), values});
		}

		/**
		 * Name binding for function: minimumIgnoreNaN.
		 * @see #minimumIgnoreNaN(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName minimumIgnoreNaN = 
			QualifiedName.make(CAL_Summary.MODULE_NAME, "minimumIgnoreNaN");

		/**
		 * Mode returns <code>Cal.Core.Prelude.Just</code> (the most-frequently-occurring element) for a list of values,
		 * or Nothing for the empty list.  <code>mode xs</code> and <code>Cal.Utilities.Summary.nthMostFrequent xs 1</code> are semantically
		 * equivalent, but mode is more efficient.
		 * <p>
		 * Runtime performance is O(n(lg n)).
		 * 
		 * @param values (CAL type: <code>Cal.Core.Prelude.Ord a => [a]</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Core.Prelude.Maybe a</code>) 
		 */
		public static final SourceModel.Expr mode(SourceModel.Expr values) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.mode), values});
		}

		/**
		 * Name binding for function: mode.
		 * @see #mode(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName mode = 
			QualifiedName.make(CAL_Summary.MODULE_NAME, "mode");

		/**
		 * Calculates the <code>n</code>th moment about the mean of values
		 * <p>
		 * Runtime performace is O(n).  Requires three passes over the data.
		 * 
		 * @param values (CAL type: <code>Cal.Core.Prelude.Num a => [a]</code>)
		 * @param n (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 */
		public static final SourceModel.Expr nthMoment(SourceModel.Expr values, SourceModel.Expr n) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.nthMoment), values, n});
		}

		/**
		 * @see #nthMoment(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param values
		 * @param n
		 * @return the SourceModel.Expr representing an application of nthMoment
		 */
		public static final SourceModel.Expr nthMoment(SourceModel.Expr values, int n) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.nthMoment), values, SourceModel.Expr.makeIntValue(n)});
		}

		/**
		 * Name binding for function: nthMoment.
		 * @see #nthMoment(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName nthMoment = 
			QualifiedName.make(CAL_Summary.MODULE_NAME, "nthMoment");

		/**
		 * Returns <code>Cal.Core.Prelude.Just x</code>, where <code>x</code> is the value that occurs <code>number</code>-th most frequently
		 * in values.  Eg, <code>nthMostFrequent [1,2,3,3,3,9,9] 2</code> returns <code>Cal.Core.Prelude.Just 9</code>, since 9 is
		 * the 2nd most frequently occurring number in the given list.
		 * <p>
		 * If two numbers have the same frequency, then we return them smallest-number-first.
		 * Ex; <code>nthMostFrequent [2,2,1,1] 1 == Cal.Core.Prelude.Just 1</code> and <code>nthMostFrequent [2,2,1,1] 2 == Cal.Core.Prelude.Just 2</code>.
		 * Returns <code>Cal.Core.Prelude.Nothing</code> if number is &lt;= 0 or &gt;= the number of distinct values in the list.
		 * <p>
		 * Runtime performance is O(n(lg n))
		 * 
		 * @param values (CAL type: <code>Cal.Core.Prelude.Ord a => [a]</code>)
		 * @param number (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Core.Prelude.Maybe a</code>) 
		 */
		public static final SourceModel.Expr nthMostFrequent(SourceModel.Expr values, SourceModel.Expr number) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.nthMostFrequent), values, number});
		}

		/**
		 * @see #nthMostFrequent(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param values
		 * @param number
		 * @return the SourceModel.Expr representing an application of nthMostFrequent
		 */
		public static final SourceModel.Expr nthMostFrequent(SourceModel.Expr values, int number) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.nthMostFrequent), values, SourceModel.Expr.makeIntValue(number)});
		}

		/**
		 * Name binding for function: nthMostFrequent.
		 * @see #nthMostFrequent(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName nthMostFrequent = 
			QualifiedName.make(CAL_Summary.MODULE_NAME, "nthMostFrequent");

		/**
		 * Returns the percentile for values using interpolation.
		 * Eg, the 0.6 (60%) percentile for a 5-element list of values
		 * may differ from the 0.7 (70%) percentile for the same list.
		 * <p>
		 * Average-case runtime performance is O(n). (See <code>Cal.Utilities.Summary.selectNthRankedElement</code>)
		 * 
		 * @param values (CAL type: <code>Cal.Core.Prelude.Num a => [a]</code>)
		 * @param rank (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 */
		public static final SourceModel.Expr percentile(SourceModel.Expr values, SourceModel.Expr rank) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.percentile), values, rank});
		}

		/**
		 * @see #percentile(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param values
		 * @param rank
		 * @return the SourceModel.Expr representing an application of percentile
		 */
		public static final SourceModel.Expr percentile(SourceModel.Expr values, double rank) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.percentile), values, SourceModel.Expr.makeDoubleValue(rank)});
		}

		/**
		 * Name binding for function: percentile.
		 * @see #percentile(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName percentile = 
			QualifiedName.make(CAL_Summary.MODULE_NAME, "percentile");

		/**
		 * Finds the population standard deviation of a list of numbers by first
		 * converting each element to a <code>Cal.Core.Prelude.Double</code> and then finding the standard deviation
		 * of the result.  Returns 0.0 for an empty list.
		 * @param values (CAL type: <code>Cal.Core.Prelude.Num a => [a]</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 */
		public static final SourceModel.Expr populationStandardDeviation(SourceModel.Expr values) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.populationStandardDeviation), values});
		}

		/**
		 * Name binding for function: populationStandardDeviation.
		 * @see #populationStandardDeviation(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName populationStandardDeviation = 
			QualifiedName.make(
				CAL_Summary.MODULE_NAME, 
				"populationStandardDeviation");

		/**
		 * Calculates the population variance of a list of numbers by first converting all of the
		 * elements to <code>Cal.Core.Prelude.Double</code>s and then finding the sample variance of the resulting set of values.
		 * Returns 0.0 for an empty list.
		 * <p>
		 * Runtime performance is O(n).  Only one pass over the data is required.
		 * 
		 * @param values (CAL type: <code>Cal.Core.Prelude.Num a => [a]</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 */
		public static final SourceModel.Expr populationVariance(SourceModel.Expr values) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.populationVariance), values});
		}

		/**
		 * Name binding for function: populationVariance.
		 * @see #populationVariance(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName populationVariance = 
			QualifiedName.make(CAL_Summary.MODULE_NAME, "populationVariance");

		/**
		 * Helper binding method for function: quartile. 
		 * @param list
		 * @param q
		 * @return the SourceModule.expr representing an application of quartile
		 */
		public static final SourceModel.Expr quartile(SourceModel.Expr list, SourceModel.Expr q) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.quartile), list, q});
		}

		/**
		 * Name binding for function: quartile.
		 * @see #quartile(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName quartile = 
			QualifiedName.make(CAL_Summary.MODULE_NAME, "quartile");

		/**
		 * Helper binding method for function: sampleStandardDeviation. 
		 * @param values
		 * @return the SourceModule.expr representing an application of sampleStandardDeviation
		 */
		public static final SourceModel.Expr sampleStandardDeviation(SourceModel.Expr values) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.sampleStandardDeviation), values});
		}

		/**
		 * Name binding for function: sampleStandardDeviation.
		 * @see #sampleStandardDeviation(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName sampleStandardDeviation = 
			QualifiedName.make(
				CAL_Summary.MODULE_NAME, 
				"sampleStandardDeviation");

		/**
		 * Calculates the sample variance of a list of numbers by first converting all of the
		 * elements to <code>Cal.Core.Prelude.Double</code>s and then finding the sample variance of the resulting set of values.
		 * Returns 0.0 for lists of 0 or 1 elements.
		 * <p>
		 * Runtime performance is O(n).  Only one pass over the data is required.
		 * 
		 * @param values (CAL type: <code>Cal.Core.Prelude.Num a => [a]</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 */
		public static final SourceModel.Expr sampleVariance(SourceModel.Expr values) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.sampleVariance), values});
		}

		/**
		 * Name binding for function: sampleVariance.
		 * @see #sampleVariance(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName sampleVariance = 
			QualifiedName.make(CAL_Summary.MODULE_NAME, "sampleVariance");

		/**
		 * Returns the element that would be ranked <code>n</code> if values were sorted.
		 * <code>n</code> must be between 1 and <code>Cal.Collections.List.length values</code>, inclusive.
		 * <p>
		 * <code>selectNthRankedElement xs 1</code> is equivalent to <code>Cal.Collections.List.minimum xs</code> and
		 * <code>selectNthRankedElement xs (Cal.Collections.List.length xs)</code> is equivalent to <code>Cal.Collections.List.maximum xs</code>, 
		 * although minimum and maximum are more efficient.
		 * <p>
		 * Average-case runtime performance is O(n), worst-case (extremely unlikely) is O(n^2).
		 * 
		 * @param values (CAL type: <code>Cal.Core.Prelude.Ord a => [a]</code>)
		 * @param n (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Core.Prelude.Maybe a</code>) 
		 */
		public static final SourceModel.Expr selectNthRankedElement(SourceModel.Expr values, SourceModel.Expr n) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.selectNthRankedElement), values, n});
		}

		/**
		 * @see #selectNthRankedElement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param values
		 * @param n
		 * @return the SourceModel.Expr representing an application of selectNthRankedElement
		 */
		public static final SourceModel.Expr selectNthRankedElement(SourceModel.Expr values, int n) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.selectNthRankedElement), values, SourceModel.Expr.makeIntValue(n)});
		}

		/**
		 * Name binding for function: selectNthRankedElement.
		 * @see #selectNthRankedElement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName selectNthRankedElement = 
			QualifiedName.make(
				CAL_Summary.MODULE_NAME, 
				"selectNthRankedElement");

		/**
		 * A custom version of <code>Cal.Collections.List.sum</code> that ignores NaN values.  The NaN values in the list will not contribute
		 * to the summation and will have no effect on the result.  If the list is empty, or contains only NaN
		 * values, then NaN is returned.
		 * <p>
		 * Runtime performance is O(n).  Only one pass over the data is required.
		 * 
		 * @param values (CAL type: <code>[Cal.Core.Prelude.Double]</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 */
		public static final SourceModel.Expr sumIgnoreNaN(SourceModel.Expr values) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.sumIgnoreNaN), values});
		}

		/**
		 * Name binding for function: sumIgnoreNaN.
		 * @see #sumIgnoreNaN(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName sumIgnoreNaN = 
			QualifiedName.make(CAL_Summary.MODULE_NAME, "sumIgnoreNaN");

		/**
		 * Helper binding method for function: testSummaryModule. 
		 * @return the SourceModule.expr representing an application of testSummaryModule
		 */
		public static final SourceModel.Expr testSummaryModule() {
			return SourceModel.Expr.Var.make(Functions.testSummaryModule);
		}

		/**
		 * Name binding for function: testSummaryModule.
		 * @see #testSummaryModule()
		 */
		public static final QualifiedName testSummaryModule = 
			QualifiedName.make(CAL_Summary.MODULE_NAME, "testSummaryModule");

		/**
		 * Returns the weighted average of values weighted by weights.
		 * The ith value is weighted by the ith weight.
		 * Values and weights are both converted to <code>Cal.Core.Prelude.Double</code>s before calculations begin.
		 * <p>
		 * If there are more weights than values, the extra weights are ignored.
		 * If there are fewer weights than values, then the weights are cycled.  e.g.,
		 * <code>weightedAverage [6,10,11,8] [1,2]</code> is equivalent to <code>weightedAverage [6,10,11,8] [1,2,1,2]</code>
		 * Returns <code>Cal.Core.Prelude.notANumber</code> for empty lists.
		 * <p>
		 * Runtime performance is O(n).  Only one pass over the data is required.
		 * 
		 * @param values (CAL type: <code>Cal.Core.Prelude.Num a => [a]</code>)
		 * @param weights (CAL type: <code>Cal.Core.Prelude.Num b => [b]</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 */
		public static final SourceModel.Expr weightedAverage(SourceModel.Expr values, SourceModel.Expr weights) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.weightedAverage), values, weights});
		}

		/**
		 * Name binding for function: weightedAverage.
		 * @see #weightedAverage(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName weightedAverage = 
			QualifiedName.make(CAL_Summary.MODULE_NAME, "weightedAverage");

		/**
		 * A custom version of <code>Cal.Utilities.Summary.weightedAverage</code> that ignores NaN values.  Any child whose value or weight is NaN
		 * is ignored and will have no effect on the result.  If the list is empty, or contains only NaN values, then
		 * NaN is returned.  If no weights are provided, NaN is returned.
		 * <p>
		 * Runtime performance is O(n).  Only one pass over the data is required.
		 * 
		 * @param values (CAL type: <code>[Cal.Core.Prelude.Double]</code>)
		 * @param weights (CAL type: <code>[Cal.Core.Prelude.Double]</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 */
		public static final SourceModel.Expr weightedAverageIgnoreNaN(SourceModel.Expr values, SourceModel.Expr weights) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.weightedAverageIgnoreNaN), values, weights});
		}

		/**
		 * Name binding for function: weightedAverageIgnoreNaN.
		 * @see #weightedAverageIgnoreNaN(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName weightedAverageIgnoreNaN = 
			QualifiedName.make(
				CAL_Summary.MODULE_NAME, 
				"weightedAverageIgnoreNaN");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = -1498800844;

}
