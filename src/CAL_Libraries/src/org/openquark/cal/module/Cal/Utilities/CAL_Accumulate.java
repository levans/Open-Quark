/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_Accumulate.java)
 * was generated from CAL module: Cal.Utilities.Accumulate.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Utilities.Accumulate module from Java code.
 *  
 * Creation date: Tue Aug 28 15:58:37 PDT 2007
 * --!>
 *  
 */

package org.openquark.cal.module.Cal.Utilities;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * Defines accumulation functions, that can be used to compute multiple summaries over data, along with
 * finalizing the results. This is similar to the sort of summary operations done by a reporting application.
 * @author Raymond Cypher
 */
public final class CAL_Accumulate {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Utilities.Accumulate");

	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Utilities.Accumulate module.
	 */
	public static final class Functions {
		/**
		 * Do an accumulation operation across a data set.
		 * @param accumulator (CAL type: <code>a -> b -> a</code>)
		 *          function to apply at each data point
		 * @param finalizer (CAL type: <code>a -> c</code>)
		 *          function that is applied to the result of applying func across the record set.
		 * @param runningValue (CAL type: <code>a</code>)
		 *          current state of calculation
		 * @param converter (CAL type: <code>d -> b</code>)
		 *          function that transforms an element of the data set to the type of data point func is expecting.
		 * @param values (CAL type: <code>[d]</code>)
		 *          a list of values
		 * @return (CAL type: <code>c</code>) 
		 */
		public static final SourceModel.Expr accumulate(SourceModel.Expr accumulator, SourceModel.Expr finalizer, SourceModel.Expr runningValue, SourceModel.Expr converter, SourceModel.Expr values) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.accumulate), accumulator, finalizer, runningValue, converter, values});
		}

		/**
		 * Name binding for function: accumulate.
		 * @see #accumulate(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName accumulate = 
			QualifiedName.make(CAL_Accumulate.MODULE_NAME, "accumulate");

		/**
		 * <code>accumulate1</code> takes a record describing the accumulator and a set of values to operate on.
		 * <p>
		 * The record contains: <code>accumulator</code>, <code>finalizer</code>, <code>runningValue</code>, and <code>converter</code>
		 * The accumulator is applied to the running value and each data point.
		 * The finalizer is replied to the running value after the accumulator has been applied to all data points.
		 * The running value represents the current state of the accumulation.
		 * The converter is used to transform an individual value.  Often it is used to extract a field from a record/tuple.
		 * <p>
		 * <code>Cal.Utilities.Accumulate.accumulate2</code> takes a record containing two accumulator description records and so on for <code>Cal.Utilities.Accumulate.accumulate3</code>, etc.
		 * 
		 * @param accumulator (CAL type: <code>(a\accumulator, a\converter, a\finalizer, a\runningValue) => {a | accumulator :: b -> c -> b, converter :: d -> c, finalizer :: b -> e, runningValue :: b}</code>)
		 *          - the record containing the accumulator parts
		 * @param values (CAL type: <code>[d]</code>)
		 *          - a <code>Cal.Core.Prelude.List</code> of values
		 * @return (CAL type: <code>e</code>) 
		 */
		public static final SourceModel.Expr accumulate1(SourceModel.Expr accumulator, SourceModel.Expr values) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.accumulate1), accumulator, values});
		}

		/**
		 * Name binding for function: accumulate1.
		 * @see #accumulate1(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName accumulate1 = 
			QualifiedName.make(CAL_Accumulate.MODULE_NAME, "accumulate1");

		/**
		 * <code>accumulate2</code> is lazy in each of the accumulated values.  i.e. accessing one accumulated value doesn't cause the other value to
		 * be evaluated.  As a result there are two passes over the data to get each value.  Also the entire value record set ends up residing in
		 * memory.
		 * It takes a record containing two fields, #1 and #2, each of which are an accumulator description record as
		 * described in the comment for <code>Cal.Utilities.Accumulate.accumulate1</code>
		 * @param accumulators (CAL type: <code>(a\#1, a\#2, b\accumulator, b\converter, b\finalizer, b\runningValue, g\accumulator, g\converter, g\finalizer, g\runningValue) => {a | #1 :: {b | accumulator :: c -> d -> c, converter :: e -> d, finalizer :: c -> f, runningValue :: c}, #2 :: {g | accumulator :: h -> i -> h, converter :: e -> i, finalizer :: h -> j, runningValue :: h}}</code>)
		 *          - a record containing the records containing the accumulator parts
		 * @param values (CAL type: <code>[e]</code>)
		 *          - a <code>Cal.Core.Prelude.List</code> of values
		 * @return (CAL type: <code>(f, j)</code>) 
		 */
		public static final SourceModel.Expr accumulate2(SourceModel.Expr accumulators, SourceModel.Expr values) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.accumulate2), accumulators, values});
		}

		/**
		 * Name binding for function: accumulate2.
		 * @see #accumulate2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName accumulate2 = 
			QualifiedName.make(CAL_Accumulate.MODULE_NAME, "accumulate2");

		/**
		 * <code>accumulate2Strict</code> calculates all accumulated values in a single pass over the data.  
		 * This means that it runs in constant space
		 * and that it is not possible to access one value without both being calculated.
		 * <p>
		 * It takes a record containing two fields, #1 and #2, each of which are an accumulator description record as
		 * described in the comment for <code>Cal.Utilities.Accumulate.accumulate1</code>
		 * 
		 * @param accumulators (CAL type: <code>(a\#1, a\#2, b\accumulator, b\converter, b\finalizer, b\runningValue, g\accumulator, g\converter, g\finalizer, g\runningValue) => {a | #1 :: {b | accumulator :: c -> d -> c, converter :: e -> d, finalizer :: c -> f, runningValue :: c}, #2 :: {g | accumulator :: h -> i -> h, converter :: e -> i, finalizer :: h -> j, runningValue :: h}}</code>)
		 *          - a record containing the records containing the accumulator parts
		 * @param values (CAL type: <code>[e]</code>)
		 *          - a <code>Cal.Core.Prelude.List</code> of values
		 * @return (CAL type: <code>(f, j)</code>) 
		 */
		public static final SourceModel.Expr accumulate2Strict(SourceModel.Expr accumulators, SourceModel.Expr values) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.accumulate2Strict), accumulators, values});
		}

		/**
		 * Name binding for function: accumulate2Strict.
		 * @see #accumulate2Strict(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName accumulate2Strict = 
			QualifiedName.make(CAL_Accumulate.MODULE_NAME, "accumulate2Strict");

		/**
		 * <code>accumulate3</code> is lazy in each of the accumulated values.  i.e. accessing one accumulated value doesn't cause the other value to
		 * be evaluated.  As a result there are three passes over the data if all values are accessed.  
		 * Also the entire value record set ends up residing in memory.
		 * @param accumulators (CAL type: <code>(a\#1, a\#2, a\#3, b\accumulator, b\converter, b\finalizer, b\runningValue, g\accumulator, g\converter, g\finalizer, g\runningValue, k\accumulator, k\converter, k\finalizer, k\runningValue) => {a | #1 :: {b | accumulator :: c -> d -> c, converter :: e -> d, finalizer :: c -> f, runningValue :: c}, #2 :: {g | accumulator :: h -> i -> h, converter :: e -> i, finalizer :: h -> j, runningValue :: h}, #3 :: {k | accumulator :: l -> m -> l, converter :: e -> m, finalizer :: l -> n, runningValue :: l}}</code>)
		 *          - a record containing the records containing the accumulator parts
		 * @param values (CAL type: <code>[e]</code>)
		 *          - a <code>Cal.Core.Prelude.List</code> of values
		 * @return (CAL type: <code>(f, j, n)</code>) 
		 */
		public static final SourceModel.Expr accumulate3(SourceModel.Expr accumulators, SourceModel.Expr values) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.accumulate3), accumulators, values});
		}

		/**
		 * Name binding for function: accumulate3.
		 * @see #accumulate3(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName accumulate3 = 
			QualifiedName.make(CAL_Accumulate.MODULE_NAME, "accumulate3");

		/**
		 * <code>accumulate3Strict</code> calculates all accumulated values in a single pass over the data.  This means that it runs in constant space
		 * and that it is not possible to access one accumulated value without all three being calculated.
		 * @param accumulators (CAL type: <code>(a\#1, a\#2, a\#3, b\accumulator, b\converter, b\finalizer, b\runningValue, g\accumulator, g\converter, g\finalizer, g\runningValue, k\accumulator, k\converter, k\finalizer, k\runningValue) => {a | #1 :: {b | accumulator :: c -> d -> c, converter :: e -> d, finalizer :: c -> f, runningValue :: c}, #2 :: {g | accumulator :: h -> i -> h, converter :: e -> i, finalizer :: h -> j, runningValue :: h}, #3 :: {k | accumulator :: l -> m -> l, converter :: e -> m, finalizer :: l -> n, runningValue :: l}}</code>)
		 *          - a record containing the records containing the accumulator parts
		 * @param values (CAL type: <code>[e]</code>)
		 *          - a <code>Cal.Core.Prelude.List</code> of values
		 * @return (CAL type: <code>(f, j, n)</code>) 
		 */
		public static final SourceModel.Expr accumulate3Strict(SourceModel.Expr accumulators, SourceModel.Expr values) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.accumulate3Strict), accumulators, values});
		}

		/**
		 * Name binding for function: accumulate3Strict.
		 * @see #accumulate3Strict(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName accumulate3Strict = 
			QualifiedName.make(CAL_Accumulate.MODULE_NAME, "accumulate3Strict");

		/**
		 * <code>accumulate4</code> is lazy in each of the accumulated values.  i.e. accessing one accumulated value doesn't cause the other value to
		 * be evaluated.  As a result there are four passes over the data if all accumulated values are accessed.  
		 * Also the entire value record set ends up residing in memory.
		 * @param accumulators (CAL type: <code>(a\#1, a\#2, a\#3, a\#4, b\accumulator, b\converter, b\finalizer, b\runningValue, g\accumulator, g\converter, g\finalizer, g\runningValue, k\accumulator, k\converter, k\finalizer, k\runningValue, o\accumulator, o\converter, o\finalizer, o\runningValue) => {a | #1 :: {b | accumulator :: c -> d -> c, converter :: e -> d, finalizer :: c -> f, runningValue :: c}, #2 :: {g | accumulator :: h -> i -> h, converter :: e -> i, finalizer :: h -> j, runningValue :: h}, #3 :: {k | accumulator :: l -> m -> l, converter :: e -> m, finalizer :: l -> n, runningValue :: l}, #4 :: {o | accumulator :: p -> q -> p, converter :: e -> q, finalizer :: p -> r, runningValue :: p}}</code>)
		 *          - a record containing the records containing the accumulator parts
		 * @param values (CAL type: <code>[e]</code>)
		 *          - a <code>Cal.Core.Prelude.List</code> of values
		 * @return (CAL type: <code>(f, j, n, r)</code>) 
		 */
		public static final SourceModel.Expr accumulate4(SourceModel.Expr accumulators, SourceModel.Expr values) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.accumulate4), accumulators, values});
		}

		/**
		 * Name binding for function: accumulate4.
		 * @see #accumulate4(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName accumulate4 = 
			QualifiedName.make(CAL_Accumulate.MODULE_NAME, "accumulate4");

		/**
		 * <code>accumulate4Strict</code> calculates all accumulated values in a single pass over the data.  This means that it runs in constant space
		 * and that it is not possible to access one accumulated value without all four being calculated.
		 * @param accumulators (CAL type: <code>(a\#1, a\#2, a\#3, a\#4, b\accumulator, b\converter, b\finalizer, b\runningValue, g\accumulator, g\converter, g\finalizer, g\runningValue, k\accumulator, k\converter, k\finalizer, k\runningValue, o\accumulator, o\converter, o\finalizer, o\runningValue) => {a | #1 :: {b | accumulator :: c -> d -> c, converter :: e -> d, finalizer :: c -> f, runningValue :: c}, #2 :: {g | accumulator :: h -> i -> h, converter :: e -> i, finalizer :: h -> j, runningValue :: h}, #3 :: {k | accumulator :: l -> m -> l, converter :: e -> m, finalizer :: l -> n, runningValue :: l}, #4 :: {o | accumulator :: p -> q -> p, converter :: e -> q, finalizer :: p -> r, runningValue :: p}}</code>)
		 *          - a record containing the records containing the accumulator parts
		 * @param values (CAL type: <code>[e]</code>)
		 *          - a <code>Cal.Core.Prelude.List</code> of values
		 * @return (CAL type: <code>(f, j, n, r)</code>) 
		 */
		public static final SourceModel.Expr accumulate4Strict(SourceModel.Expr accumulators, SourceModel.Expr values) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.accumulate4Strict), accumulators, values});
		}

		/**
		 * Name binding for function: accumulate4Strict.
		 * @see #accumulate4Strict(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName accumulate4Strict = 
			QualifiedName.make(CAL_Accumulate.MODULE_NAME, "accumulate4Strict");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = 1556652736;

}
