/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_QuickCheck_internal.java)
 * was generated from CAL module: Cal.Utilities.QuickCheck.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Utilities.QuickCheck module from Java code.
 *  
 * Creation date: Fri Aug 17 18:11:14 PDT 2007
 * --!>
 *  
 */

package org.openquark.cal.internal.module.Cal.Utilities;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * QuickCheck provides a simple mechanism for testing programs by generating arbitrary test data. 
 * It is closely based on QuickCheck for Haskell, 
 * see: <a href='http://www.cs.chalmers.se/~rjmh/QuickCheck/'>http://www.cs.chalmers.se/~rjmh/QuickCheck/</a>
 * <p>
 * The basic idea is to help simplify and improve testing by 
 * automatically creating arbitrary test data. Properties of 
 * programs can be described by functions. QuickCheck can be used to validate
 * these properties by automatically generating lots of arbitrary data to fill the functions'
 * parameters. If a property is not true, QuickCheck can report the parameters
 * that falsified the property.
 * <p>
 * For example a simple property of reverse can be expressed as: 
 * <p>
 * 
 * <pre>prop_reverse :: [Int] -&gt; Boolean;
 * prop_reverse list = list == reverse (reverse list);</pre>
 * 
 * <p>
 * This property can be checked using the following:
 * <p>
 * <code>quickCheck prop_reverse</code>
 * <p>
 * The <code>Cal.Utilities.QuickCheck.runTests</code> function will automatically create 100 pseudo random lists of integers in order to 
 * test the property and collect the results.
 * <p>
 * It is possible to specify preconditions on the testing data using <code>Cal.Utilities.QuickCheck.implies</code>.  
 * For example to test the transitivity of less than:
 * 
 * <pre>prop_lessThan :: Int -&gt; Int -&gt; Int -&gt; Property;
 * prop_lessThan x y z = ((x &lt; y) &amp;&amp; (y &lt; z)) `implies` (x &lt; z);</pre>
 *  
 * <p>
 * When this property is tested around three quarters of the test data will be discarded. In cases where
 * it is rare that random data will satisfy the preconditions, a custom generator  <code>Cal.Utilities.QuickCheck.Gen</code> should be implemented
 * and specified using <code>Cal.Utilities.QuickCheck.forAll</code>.
 * <p>
 * It is possible to classify the
 * generated data to gather statistics about the tests, see <code>Cal.Utilities.QuickCheck.classify</code> and 
 * <code>Cal.Utilities.QuickCheck.summarize</code> for more information.
 * 
 * @author Magnus Byne
 */
public final class CAL_QuickCheck_internal {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Utilities.QuickCheck");

	/**
	 * This inner class (DataConstructors) contains constants
	 * and methods related to binding to CAL DataConstructors in the Cal.Utilities.QuickCheck module.
	 */
	public static final class DataConstructors {
		/*
		 * DataConstructors for the Cal.Utilities.QuickCheck.Config data type.
		 */

		/**
		 * Binding for DataConstructor: Cal.Utilities.QuickCheck.Config.
		 * @param numTests
		 * @param maxTests
		 * @param resizing
		 * @param genParams
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.QuickCheck.Config
		 */
		public static final SourceModel.Expr Config(SourceModel.Expr numTests, SourceModel.Expr maxTests, SourceModel.Expr resizing, SourceModel.Expr genParams) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.Config), numTests, maxTests, resizing, genParams});
		}

		/**
		 * @see #Config(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param numTests
		 * @param maxTests
		 * @param resizing
		 * @param genParams
		 * @return org.openquark.cal.compiler.SourceModel.Expr
		 */
		public static final SourceModel.Expr Config(int numTests, int maxTests, SourceModel.Expr resizing, SourceModel.Expr genParams) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.Config), SourceModel.Expr.makeIntValue(numTests), SourceModel.Expr.makeIntValue(maxTests), resizing, genParams});
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.QuickCheck.Config.
		 * @see #Config(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName Config = 
			QualifiedName.make(CAL_QuickCheck_internal.MODULE_NAME, "Config");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.QuickCheck.Config.
		 * @see #Config(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int Config_ordinal = 0;

		/*
		 * DataConstructors for the Cal.Utilities.QuickCheck.Gen data type.
		 */

		/**
		 * Binding for DataConstructor: Cal.Utilities.QuickCheck.Gen.
		 * @param f
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.QuickCheck.Gen
		 */
		public static final SourceModel.Expr Gen(SourceModel.Expr f) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.Gen), f});
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.QuickCheck.Gen.
		 * @see #Gen(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName Gen = 
			QualifiedName.make(CAL_QuickCheck_internal.MODULE_NAME, "Gen");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.QuickCheck.Gen.
		 * @see #Gen(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int Gen_ordinal = 0;

		/*
		 * DataConstructors for the Cal.Utilities.QuickCheck.GenParams data type.
		 */

		/**
		 * A data constructor for controlling generation of Arbitrary types.
		 * @param size (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          controls the size of generated instances, e.g. String length
		 * @param longs (CAL type: <code>[Cal.Core.Prelude.Long]</code>)
		 *          list of the longs used as the random source for generating instances
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr GenParams(SourceModel.Expr size, SourceModel.Expr longs) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.GenParams), size, longs});
		}

		/**
		 * @see #GenParams(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param size
		 * @param longs
		 * @return org.openquark.cal.compiler.SourceModel.Expr
		 */
		public static final SourceModel.Expr GenParams(int size, SourceModel.Expr longs) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.GenParams), SourceModel.Expr.makeIntValue(size), longs});
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.QuickCheck.GenParams.
		 * @see #GenParams(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName GenParams = 
			QualifiedName.make(CAL_QuickCheck_internal.MODULE_NAME, "GenParams");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.QuickCheck.GenParams.
		 * @see #GenParams(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int GenParams_ordinal = 0;

		/*
		 * DataConstructors for the Cal.Utilities.QuickCheck.Property data type.
		 */

		/**
		 * Binding for DataConstructor: Cal.Utilities.QuickCheck.Property.
		 * @param gen
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.QuickCheck.Property
		 */
		public static final SourceModel.Expr Property(SourceModel.Expr gen) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.Property), gen});
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.QuickCheck.Property.
		 * @see #Property(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName Property = 
			QualifiedName.make(CAL_QuickCheck_internal.MODULE_NAME, "Property");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.QuickCheck.Property.
		 * @see #Property(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int Property_ordinal = 0;

		/*
		 * DataConstructors for the Cal.Utilities.QuickCheck.Result data type.
		 */

		/**
		 * A data constructor that represents the result of a single test.
		 * @param ok (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Core.Prelude.Boolean</code>)
		 *          the outcome of the test - Nothing if the test could not be run.
		 * @param labels (CAL type: <code>[Cal.Core.Prelude.String]</code>)
		 *          list of the labels that are associated with the test.
		 * @param args (CAL type: <code>[Cal.Core.Prelude.String]</code>)
		 *          list of the arguments that were used to conduct the test.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr Result(SourceModel.Expr ok, SourceModel.Expr labels, SourceModel.Expr args) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.Result), ok, labels, args});
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.QuickCheck.Result.
		 * @see #Result(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName Result = 
			QualifiedName.make(CAL_QuickCheck_internal.MODULE_NAME, "Result");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.QuickCheck.Result.
		 * @see #Result(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int Result_ordinal = 0;

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Utilities.QuickCheck module.
	 */
	public static final class Functions {
		/**
		 * Adds an argument into a result
		 * @param arg (CAL type: <code>Cal.Core.Debug.Show a => a</code>)
		 *          the argument to add to the result
		 * @param result (CAL type: <code>Cal.Utilities.QuickCheck.Result</code>)
		 *          the result to add to the argument
		 * @return (CAL type: <code>Cal.Utilities.QuickCheck.Result</code>) 
		 *          the result with the argument a
		 */
		public static final SourceModel.Expr addArg(SourceModel.Expr arg, SourceModel.Expr result) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.addArg), arg, result});
		}

		/**
		 * Name binding for function: addArg.
		 * @see #addArg(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName addArg = 
			QualifiedName.make(CAL_QuickCheck_internal.MODULE_NAME, "addArg");

		/**
		 * Checks a list of results and returns true iff test outcomes are true
		 * and that there is the expected number of test results
		 * @param num (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the expected number of tests
		 * @param res (CAL type: <code>[Cal.Utilities.QuickCheck.Result]</code>)
		 *          the list of results
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          true iff there the expected number of positive tests
		 */
		public static final SourceModel.Expr allTestsPass(SourceModel.Expr num, SourceModel.Expr res) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.allTestsPass), num, res});
		}

		/**
		 * @see #allTestsPass(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param num
		 * @param res
		 * @return the SourceModel.Expr representing an application of allTestsPass
		 */
		public static final SourceModel.Expr allTestsPass(int num, SourceModel.Expr res) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.allTestsPass), SourceModel.Expr.makeIntValue(num), res});
		}

		/**
		 * Name binding for function: allTestsPass.
		 * @see #allTestsPass(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName allTestsPass = 
			QualifiedName.make(
				CAL_QuickCheck_internal.MODULE_NAME, 
				"allTestsPass");

		/**
		 * adds another generator to the head of a list generator
		 * so that if the input generator returns list of length n
		 * the output generator will create list of length n+1
		 * @param gen (CAL type: <code>Cal.Utilities.QuickCheck.Gen a</code>)
		 *          input generator of type a
		 * @param listGen (CAL type: <code>Cal.Utilities.QuickCheck.Gen [a]</code>)
		 *          input generator of type [a] where length n
		 * @return (CAL type: <code>Cal.Utilities.QuickCheck.Gen [a]</code>) 
		 *          generator of list of length n+1
		 */
		public static final SourceModel.Expr appendGenToListGen(SourceModel.Expr gen, SourceModel.Expr listGen) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.appendGenToListGen), gen, listGen});
		}

		/**
		 * Name binding for function: appendGenToListGen.
		 * @see #appendGenToListGen(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName appendGenToListGen = 
			QualifiedName.make(
				CAL_QuickCheck_internal.MODULE_NAME, 
				"appendGenToListGen");

		/**
		 * Helper binding method for function: arbitraryBoolean. 
		 * @return the SourceModule.expr representing an application of arbitraryBoolean
		 */
		public static final SourceModel.Expr arbitraryBoolean() {
			return SourceModel.Expr.Var.make(Functions.arbitraryBoolean);
		}

		/**
		 * Name binding for function: arbitraryBoolean.
		 * @see #arbitraryBoolean()
		 */
		public static final QualifiedName arbitraryBoolean = 
			QualifiedName.make(
				CAL_QuickCheck_internal.MODULE_NAME, 
				"arbitraryBoolean");

		/**
		 * Helper binding method for function: arbitraryByte. 
		 * @return the SourceModule.expr representing an application of arbitraryByte
		 */
		public static final SourceModel.Expr arbitraryByte() {
			return SourceModel.Expr.Var.make(Functions.arbitraryByte);
		}

		/**
		 * Name binding for function: arbitraryByte.
		 * @see #arbitraryByte()
		 */
		public static final QualifiedName arbitraryByte = 
			QualifiedName.make(
				CAL_QuickCheck_internal.MODULE_NAME, 
				"arbitraryByte");

		/**
		 * Helper binding method for function: arbitraryChar. 
		 * @return the SourceModule.expr representing an application of arbitraryChar
		 */
		public static final SourceModel.Expr arbitraryChar() {
			return SourceModel.Expr.Var.make(Functions.arbitraryChar);
		}

		/**
		 * Name binding for function: arbitraryChar.
		 * @see #arbitraryChar()
		 */
		public static final QualifiedName arbitraryChar = 
			QualifiedName.make(
				CAL_QuickCheck_internal.MODULE_NAME, 
				"arbitraryChar");

		/**
		 * Helper binding method for function: arbitraryDecimal. 
		 * @return the SourceModule.expr representing an application of arbitraryDecimal
		 */
		public static final SourceModel.Expr arbitraryDecimal() {
			return SourceModel.Expr.Var.make(Functions.arbitraryDecimal);
		}

		/**
		 * Name binding for function: arbitraryDecimal.
		 * @see #arbitraryDecimal()
		 */
		public static final QualifiedName arbitraryDecimal = 
			QualifiedName.make(
				CAL_QuickCheck_internal.MODULE_NAME, 
				"arbitraryDecimal");

		/**
		 * Helper binding method for function: arbitraryDouble. 
		 * @return the SourceModule.expr representing an application of arbitraryDouble
		 */
		public static final SourceModel.Expr arbitraryDouble() {
			return SourceModel.Expr.Var.make(Functions.arbitraryDouble);
		}

		/**
		 * Name binding for function: arbitraryDouble.
		 * @see #arbitraryDouble()
		 */
		public static final QualifiedName arbitraryDouble = 
			QualifiedName.make(
				CAL_QuickCheck_internal.MODULE_NAME, 
				"arbitraryDouble");

		/**
		 * Helper binding method for function: arbitraryEither. 
		 * @return the SourceModule.expr representing an application of arbitraryEither
		 */
		public static final SourceModel.Expr arbitraryEither() {
			return SourceModel.Expr.Var.make(Functions.arbitraryEither);
		}

		/**
		 * Name binding for function: arbitraryEither.
		 * @see #arbitraryEither()
		 */
		public static final QualifiedName arbitraryEither = 
			QualifiedName.make(
				CAL_QuickCheck_internal.MODULE_NAME, 
				"arbitraryEither");

		/**
		 * Helper binding method for function: arbitraryExamples. 
		 * @return the SourceModule.expr representing an application of arbitraryExamples
		 */
		public static final SourceModel.Expr arbitraryExamples() {
			return SourceModel.Expr.Var.make(Functions.arbitraryExamples);
		}

		/**
		 * Name binding for function: arbitraryExamples.
		 * @see #arbitraryExamples()
		 */
		public static final QualifiedName arbitraryExamples = 
			QualifiedName.make(
				CAL_QuickCheck_internal.MODULE_NAME, 
				"arbitraryExamples");

		/**
		 * Helper binding method for function: arbitraryFloat. 
		 * @return the SourceModule.expr representing an application of arbitraryFloat
		 */
		public static final SourceModel.Expr arbitraryFloat() {
			return SourceModel.Expr.Var.make(Functions.arbitraryFloat);
		}

		/**
		 * Name binding for function: arbitraryFloat.
		 * @see #arbitraryFloat()
		 */
		public static final QualifiedName arbitraryFloat = 
			QualifiedName.make(
				CAL_QuickCheck_internal.MODULE_NAME, 
				"arbitraryFloat");

		/**
		 * Helper binding method for function: arbitraryFunction. 
		 * @return the SourceModule.expr representing an application of arbitraryFunction
		 */
		public static final SourceModel.Expr arbitraryFunction() {
			return SourceModel.Expr.Var.make(Functions.arbitraryFunction);
		}

		/**
		 * Name binding for function: arbitraryFunction.
		 * @see #arbitraryFunction()
		 */
		public static final QualifiedName arbitraryFunction = 
			QualifiedName.make(
				CAL_QuickCheck_internal.MODULE_NAME, 
				"arbitraryFunction");

		/**
		 * Helper binding method for function: arbitraryInt. 
		 * @return the SourceModule.expr representing an application of arbitraryInt
		 */
		public static final SourceModel.Expr arbitraryInt() {
			return SourceModel.Expr.Var.make(Functions.arbitraryInt);
		}

		/**
		 * Name binding for function: arbitraryInt.
		 * @see #arbitraryInt()
		 */
		public static final QualifiedName arbitraryInt = 
			QualifiedName.make(
				CAL_QuickCheck_internal.MODULE_NAME, 
				"arbitraryInt");

		/**
		 * Helper binding method for function: arbitraryList. 
		 * @return the SourceModule.expr representing an application of arbitraryList
		 */
		public static final SourceModel.Expr arbitraryList() {
			return SourceModel.Expr.Var.make(Functions.arbitraryList);
		}

		/**
		 * Name binding for function: arbitraryList.
		 * @see #arbitraryList()
		 */
		public static final QualifiedName arbitraryList = 
			QualifiedName.make(
				CAL_QuickCheck_internal.MODULE_NAME, 
				"arbitraryList");

		/**
		 * Helper binding method for function: arbitraryLong. 
		 * @return the SourceModule.expr representing an application of arbitraryLong
		 */
		public static final SourceModel.Expr arbitraryLong() {
			return SourceModel.Expr.Var.make(Functions.arbitraryLong);
		}

		/**
		 * Name binding for function: arbitraryLong.
		 * @see #arbitraryLong()
		 */
		public static final QualifiedName arbitraryLong = 
			QualifiedName.make(
				CAL_QuickCheck_internal.MODULE_NAME, 
				"arbitraryLong");

		/**
		 * Helper binding method for function: arbitraryMaybe. 
		 * @return the SourceModule.expr representing an application of arbitraryMaybe
		 */
		public static final SourceModel.Expr arbitraryMaybe() {
			return SourceModel.Expr.Var.make(Functions.arbitraryMaybe);
		}

		/**
		 * Name binding for function: arbitraryMaybe.
		 * @see #arbitraryMaybe()
		 */
		public static final QualifiedName arbitraryMaybe = 
			QualifiedName.make(
				CAL_QuickCheck_internal.MODULE_NAME, 
				"arbitraryMaybe");

		/**
		 * Helper binding method for function: arbitraryRecord. 
		 * @return the SourceModule.expr representing an application of arbitraryRecord
		 */
		public static final SourceModel.Expr arbitraryRecord() {
			return SourceModel.Expr.Var.make(Functions.arbitraryRecord);
		}

		/**
		 * Name binding for function: arbitraryRecord.
		 * @see #arbitraryRecord()
		 */
		public static final QualifiedName arbitraryRecord = 
			QualifiedName.make(
				CAL_QuickCheck_internal.MODULE_NAME, 
				"arbitraryRecord");

		/**
		 * Helper binding method for function: arbitraryRecordPrimitive. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of arbitraryRecordPrimitive
		 */
		public static final SourceModel.Expr arbitraryRecordPrimitive(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.arbitraryRecordPrimitive), arg_1, arg_2});
		}

		/**
		 * Name binding for function: arbitraryRecordPrimitive.
		 * @see #arbitraryRecordPrimitive(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName arbitraryRecordPrimitive = 
			QualifiedName.make(
				CAL_QuickCheck_internal.MODULE_NAME, 
				"arbitraryRecordPrimitive");

		/**
		 * Helper binding method for function: arbitraryShort. 
		 * @return the SourceModule.expr representing an application of arbitraryShort
		 */
		public static final SourceModel.Expr arbitraryShort() {
			return SourceModel.Expr.Var.make(Functions.arbitraryShort);
		}

		/**
		 * Name binding for function: arbitraryShort.
		 * @see #arbitraryShort()
		 */
		public static final QualifiedName arbitraryShort = 
			QualifiedName.make(
				CAL_QuickCheck_internal.MODULE_NAME, 
				"arbitraryShort");

		/**
		 * Helper binding method for function: arbitraryString. 
		 * @return the SourceModule.expr representing an application of arbitraryString
		 */
		public static final SourceModel.Expr arbitraryString() {
			return SourceModel.Expr.Var.make(Functions.arbitraryString);
		}

		/**
		 * Name binding for function: arbitraryString.
		 * @see #arbitraryString()
		 */
		public static final QualifiedName arbitraryString = 
			QualifiedName.make(
				CAL_QuickCheck_internal.MODULE_NAME, 
				"arbitraryString");

		/**
		 * Helper binding method for function: arbitraryUnit. 
		 * @return the SourceModule.expr representing an application of arbitraryUnit
		 */
		public static final SourceModel.Expr arbitraryUnit() {
			return SourceModel.Expr.Var.make(Functions.arbitraryUnit);
		}

		/**
		 * Name binding for function: arbitraryUnit.
		 * @see #arbitraryUnit()
		 */
		public static final QualifiedName arbitraryUnit = 
			QualifiedName.make(
				CAL_QuickCheck_internal.MODULE_NAME, 
				"arbitraryUnit");

		/**
		 * Converts an <code>Cal.Core.Prelude.Int</code> value to a <code>Cal.Core.Prelude.Char</code> value.
		 * @param intValue (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the value to be converted.
		 * @return (CAL type: <code>Cal.Core.Prelude.Char</code>) 
		 *          the corresponding <code>Char</code> value.
		 */
		public static final SourceModel.Expr charFromInt(SourceModel.Expr intValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charFromInt), intValue});
		}

		/**
		 * @see #charFromInt(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param intValue
		 * @return the SourceModel.Expr representing an application of charFromInt
		 */
		public static final SourceModel.Expr charFromInt(int intValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charFromInt), SourceModel.Expr.makeIntValue(intValue)});
		}

		/**
		 * Name binding for function: charFromInt.
		 * @see #charFromInt(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName charFromInt = 
			QualifiedName.make(
				CAL_QuickCheck_internal.MODULE_NAME, 
				"charFromInt");

		/**
		 * Converts a <code>Cal.Core.Prelude.Char</code> value to an <code>Cal.Core.Prelude.Int</code> value.
		 * @param charValue (CAL type: <code>Cal.Core.Prelude.Char</code>)
		 *          the value to be converted.
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the corresponding <code>Int</code> value.
		 */
		public static final SourceModel.Expr charToInt(SourceModel.Expr charValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charToInt), charValue});
		}

		/**
		 * @see #charToInt(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param charValue
		 * @return the SourceModel.Expr representing an application of charToInt
		 */
		public static final SourceModel.Expr charToInt(char charValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charToInt), SourceModel.Expr.makeCharValue(charValue)});
		}

		/**
		 * Name binding for function: charToInt.
		 * @see #charToInt(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName charToInt = 
			QualifiedName.make(CAL_QuickCheck_internal.MODULE_NAME, "charToInt");

		/**
		 * Helper binding method for function: coarbitraryBoolean. 
		 * @param b
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of coarbitraryBoolean
		 */
		public static final SourceModel.Expr coarbitraryBoolean(SourceModel.Expr b, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.coarbitraryBoolean), b, arg_2});
		}

		/**
		 * @see #coarbitraryBoolean(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param b
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of coarbitraryBoolean
		 */
		public static final SourceModel.Expr coarbitraryBoolean(boolean b, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.coarbitraryBoolean), SourceModel.Expr.makeBooleanValue(b), arg_2});
		}

		/**
		 * Name binding for function: coarbitraryBoolean.
		 * @see #coarbitraryBoolean(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName coarbitraryBoolean = 
			QualifiedName.make(
				CAL_QuickCheck_internal.MODULE_NAME, 
				"coarbitraryBoolean");

		/**
		 * Helper binding method for function: coarbitraryByte. 
		 * @param s
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of coarbitraryByte
		 */
		public static final SourceModel.Expr coarbitraryByte(SourceModel.Expr s, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.coarbitraryByte), s, arg_2});
		}

		/**
		 * @see #coarbitraryByte(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param s
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of coarbitraryByte
		 */
		public static final SourceModel.Expr coarbitraryByte(byte s, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.coarbitraryByte), SourceModel.Expr.makeByteValue(s), arg_2});
		}

		/**
		 * Name binding for function: coarbitraryByte.
		 * @see #coarbitraryByte(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName coarbitraryByte = 
			QualifiedName.make(
				CAL_QuickCheck_internal.MODULE_NAME, 
				"coarbitraryByte");

		/**
		 * Helper binding method for function: coarbitraryChar. 
		 * @param c
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of coarbitraryChar
		 */
		public static final SourceModel.Expr coarbitraryChar(SourceModel.Expr c, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.coarbitraryChar), c, arg_2});
		}

		/**
		 * @see #coarbitraryChar(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param c
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of coarbitraryChar
		 */
		public static final SourceModel.Expr coarbitraryChar(char c, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.coarbitraryChar), SourceModel.Expr.makeCharValue(c), arg_2});
		}

		/**
		 * Name binding for function: coarbitraryChar.
		 * @see #coarbitraryChar(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName coarbitraryChar = 
			QualifiedName.make(
				CAL_QuickCheck_internal.MODULE_NAME, 
				"coarbitraryChar");

		/**
		 * Helper binding method for function: coarbitraryDecimal. 
		 * @param value
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of coarbitraryDecimal
		 */
		public static final SourceModel.Expr coarbitraryDecimal(SourceModel.Expr value, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.coarbitraryDecimal), value, arg_2});
		}

		/**
		 * Name binding for function: coarbitraryDecimal.
		 * @see #coarbitraryDecimal(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName coarbitraryDecimal = 
			QualifiedName.make(
				CAL_QuickCheck_internal.MODULE_NAME, 
				"coarbitraryDecimal");

		/**
		 * Helper binding method for function: coarbitraryDouble. 
		 * @param value
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of coarbitraryDouble
		 */
		public static final SourceModel.Expr coarbitraryDouble(SourceModel.Expr value, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.coarbitraryDouble), value, arg_2});
		}

		/**
		 * @see #coarbitraryDouble(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of coarbitraryDouble
		 */
		public static final SourceModel.Expr coarbitraryDouble(double value, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.coarbitraryDouble), SourceModel.Expr.makeDoubleValue(value), arg_2});
		}

		/**
		 * Name binding for function: coarbitraryDouble.
		 * @see #coarbitraryDouble(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName coarbitraryDouble = 
			QualifiedName.make(
				CAL_QuickCheck_internal.MODULE_NAME, 
				"coarbitraryDouble");

		/**
		 * Helper binding method for function: coarbitraryEither. 
		 * @param m
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of coarbitraryEither
		 */
		public static final SourceModel.Expr coarbitraryEither(SourceModel.Expr m, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.coarbitraryEither), m, arg_2});
		}

		/**
		 * Name binding for function: coarbitraryEither.
		 * @see #coarbitraryEither(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName coarbitraryEither = 
			QualifiedName.make(
				CAL_QuickCheck_internal.MODULE_NAME, 
				"coarbitraryEither");

		/**
		 * Helper binding method for function: coarbitraryFloat. 
		 * @param value
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of coarbitraryFloat
		 */
		public static final SourceModel.Expr coarbitraryFloat(SourceModel.Expr value, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.coarbitraryFloat), value, arg_2});
		}

		/**
		 * @see #coarbitraryFloat(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of coarbitraryFloat
		 */
		public static final SourceModel.Expr coarbitraryFloat(float value, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.coarbitraryFloat), SourceModel.Expr.makeFloatValue(value), arg_2});
		}

		/**
		 * Name binding for function: coarbitraryFloat.
		 * @see #coarbitraryFloat(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName coarbitraryFloat = 
			QualifiedName.make(
				CAL_QuickCheck_internal.MODULE_NAME, 
				"coarbitraryFloat");

		/**
		 * Helper binding method for function: coarbitraryFunction. 
		 * @param f
		 * @param gen
		 * @return the SourceModule.expr representing an application of coarbitraryFunction
		 */
		public static final SourceModel.Expr coarbitraryFunction(SourceModel.Expr f, SourceModel.Expr gen) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.coarbitraryFunction), f, gen});
		}

		/**
		 * Name binding for function: coarbitraryFunction.
		 * @see #coarbitraryFunction(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName coarbitraryFunction = 
			QualifiedName.make(
				CAL_QuickCheck_internal.MODULE_NAME, 
				"coarbitraryFunction");

		/**
		 * Helper binding method for function: coarbitraryInt. 
		 * @param n
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of coarbitraryInt
		 */
		public static final SourceModel.Expr coarbitraryInt(SourceModel.Expr n, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.coarbitraryInt), n, arg_2});
		}

		/**
		 * @see #coarbitraryInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param n
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of coarbitraryInt
		 */
		public static final SourceModel.Expr coarbitraryInt(int n, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.coarbitraryInt), SourceModel.Expr.makeIntValue(n), arg_2});
		}

		/**
		 * Name binding for function: coarbitraryInt.
		 * @see #coarbitraryInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName coarbitraryInt = 
			QualifiedName.make(
				CAL_QuickCheck_internal.MODULE_NAME, 
				"coarbitraryInt");

		/**
		 * Helper binding method for function: coarbitraryList. 
		 * @param b
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of coarbitraryList
		 */
		public static final SourceModel.Expr coarbitraryList(SourceModel.Expr b, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.coarbitraryList), b, arg_2});
		}

		/**
		 * Name binding for function: coarbitraryList.
		 * @see #coarbitraryList(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName coarbitraryList = 
			QualifiedName.make(
				CAL_QuickCheck_internal.MODULE_NAME, 
				"coarbitraryList");

		/**
		 * Helper binding method for function: coarbitraryLong. 
		 * @param n
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of coarbitraryLong
		 */
		public static final SourceModel.Expr coarbitraryLong(SourceModel.Expr n, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.coarbitraryLong), n, arg_2});
		}

		/**
		 * @see #coarbitraryLong(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param n
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of coarbitraryLong
		 */
		public static final SourceModel.Expr coarbitraryLong(long n, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.coarbitraryLong), SourceModel.Expr.makeLongValue(n), arg_2});
		}

		/**
		 * Name binding for function: coarbitraryLong.
		 * @see #coarbitraryLong(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName coarbitraryLong = 
			QualifiedName.make(
				CAL_QuickCheck_internal.MODULE_NAME, 
				"coarbitraryLong");

		/**
		 * Helper binding method for function: coarbitraryMaybe. 
		 * @param m
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of coarbitraryMaybe
		 */
		public static final SourceModel.Expr coarbitraryMaybe(SourceModel.Expr m, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.coarbitraryMaybe), m, arg_2});
		}

		/**
		 * Name binding for function: coarbitraryMaybe.
		 * @see #coarbitraryMaybe(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName coarbitraryMaybe = 
			QualifiedName.make(
				CAL_QuickCheck_internal.MODULE_NAME, 
				"coarbitraryMaybe");

		/**
		 * Helper binding method for function: coarbitraryRecord. 
		 * @param record
		 * @param gen
		 * @return the SourceModule.expr representing an application of coarbitraryRecord
		 */
		public static final SourceModel.Expr coarbitraryRecord(SourceModel.Expr record, SourceModel.Expr gen) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.coarbitraryRecord), record, gen});
		}

		/**
		 * Name binding for function: coarbitraryRecord.
		 * @see #coarbitraryRecord(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName coarbitraryRecord = 
			QualifiedName.make(
				CAL_QuickCheck_internal.MODULE_NAME, 
				"coarbitraryRecord");

		/**
		 * Helper binding method for function: coarbitraryRecordPrimitive. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of coarbitraryRecordPrimitive
		 */
		public static final SourceModel.Expr coarbitraryRecordPrimitive(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.coarbitraryRecordPrimitive), arg_1, arg_2});
		}

		/**
		 * Name binding for function: coarbitraryRecordPrimitive.
		 * @see #coarbitraryRecordPrimitive(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName coarbitraryRecordPrimitive = 
			QualifiedName.make(
				CAL_QuickCheck_internal.MODULE_NAME, 
				"coarbitraryRecordPrimitive");

		/**
		 * Helper binding method for function: coarbitraryShort. 
		 * @param s
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of coarbitraryShort
		 */
		public static final SourceModel.Expr coarbitraryShort(SourceModel.Expr s, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.coarbitraryShort), s, arg_2});
		}

		/**
		 * @see #coarbitraryShort(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param s
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of coarbitraryShort
		 */
		public static final SourceModel.Expr coarbitraryShort(short s, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.coarbitraryShort), SourceModel.Expr.makeShortValue(s), arg_2});
		}

		/**
		 * Name binding for function: coarbitraryShort.
		 * @see #coarbitraryShort(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName coarbitraryShort = 
			QualifiedName.make(
				CAL_QuickCheck_internal.MODULE_NAME, 
				"coarbitraryShort");

		/**
		 * Helper binding method for function: coarbitraryString. 
		 * @param string
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of coarbitraryString
		 */
		public static final SourceModel.Expr coarbitraryString(SourceModel.Expr string, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.coarbitraryString), string, arg_2});
		}

		/**
		 * @see #coarbitraryString(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param string
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of coarbitraryString
		 */
		public static final SourceModel.Expr coarbitraryString(java.lang.String string, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.coarbitraryString), SourceModel.Expr.makeStringValue(string), arg_2});
		}

		/**
		 * Name binding for function: coarbitraryString.
		 * @see #coarbitraryString(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName coarbitraryString = 
			QualifiedName.make(
				CAL_QuickCheck_internal.MODULE_NAME, 
				"coarbitraryString");

		/**
		 * Helper binding method for function: coarbitraryUnit. 
		 * @param u
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of coarbitraryUnit
		 */
		public static final SourceModel.Expr coarbitraryUnit(SourceModel.Expr u, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.coarbitraryUnit), u, arg_2});
		}

		/**
		 * Name binding for function: coarbitraryUnit.
		 * @see #coarbitraryUnit(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName coarbitraryUnit = 
			QualifiedName.make(
				CAL_QuickCheck_internal.MODULE_NAME, 
				"coarbitraryUnit");

		/**
		 * Converts a <code>Cal.Core.Prelude.Double</code> value to the equivalent <code>Cal.Core.Prelude.Decimal</code> value.
		 * @param arg_1 (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Decimal</code>) 
		 */
		public static final SourceModel.Expr decimalFromDouble(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.decimalFromDouble), arg_1});
		}

		/**
		 * @see #decimalFromDouble(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of decimalFromDouble
		 */
		public static final SourceModel.Expr decimalFromDouble(double arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.decimalFromDouble), SourceModel.Expr.makeDoubleValue(arg_1)});
		}

		/**
		 * Name binding for function: decimalFromDouble.
		 * @see #decimalFromDouble(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName decimalFromDouble = 
			QualifiedName.make(
				CAL_QuickCheck_internal.MODULE_NAME, 
				"decimalFromDouble");

		/**
		 * Helper binding method for function: defaultNumTests. 
		 * @return the SourceModule.expr representing an application of defaultNumTests
		 */
		public static final SourceModel.Expr defaultNumTests() {
			return SourceModel.Expr.Var.make(Functions.defaultNumTests);
		}

		/**
		 * Name binding for function: defaultNumTests.
		 * @see #defaultNumTests()
		 */
		public static final QualifiedName defaultNumTests = 
			QualifiedName.make(
				CAL_QuickCheck_internal.MODULE_NAME, 
				"defaultNumTests");

		/**
		 * Extracts the generating function from a generator
		 * @param gen (CAL type: <code>Cal.Utilities.QuickCheck.Gen a</code>)
		 *          the generator
		 * @param arg_2 (CAL type: <code>Cal.Utilities.QuickCheck.GenParams</code>)
		 * @return (CAL type: <code>a</code>) 
		 *          the generator function
		 */
		public static final SourceModel.Expr genFunc(SourceModel.Expr gen, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.genFunc), gen, arg_2});
		}

		/**
		 * Name binding for function: genFunc.
		 * @see #genFunc(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName genFunc = 
			QualifiedName.make(CAL_QuickCheck_internal.MODULE_NAME, "genFunc");

		/**
		 * This creates a random instance of the type class
		 * this method is only used internally in the arbitrary primitive functions
		 * @param arg_1 (CAL type: <code>Cal.Utilities.QuickCheck.GenParams</code>)
		 * @return (CAL type: <code>Cal.Utilities.QuickCheck.Arbitrary a => a</code>) 
		 *          an arbitrary instance of the type class based on the genParams
		 */
		public static final SourceModel.Expr generateInstance(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.generateInstance), arg_1});
		}

		/**
		 * Name binding for function: generateInstance.
		 * @see #generateInstance(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName generateInstance = 
			QualifiedName.make(
				CAL_QuickCheck_internal.MODULE_NAME, 
				"generateInstance");

		/**
		 * Helper binding method for function: generateInstanceDefault. 
		 * @param genParams
		 * @return the SourceModule.expr representing an application of generateInstanceDefault
		 */
		public static final SourceModel.Expr generateInstanceDefault(SourceModel.Expr genParams) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.generateInstanceDefault), genParams});
		}

		/**
		 * Name binding for function: generateInstanceDefault.
		 * @see #generateInstanceDefault(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName generateInstanceDefault = 
			QualifiedName.make(
				CAL_QuickCheck_internal.MODULE_NAME, 
				"generateInstanceDefault");

		/**
		 * Used to extract a pseudo random bounded double from <code>Cal.Utilities.QuickCheck.GenParams</code>
		 * @param genParams (CAL type: <code>Cal.Utilities.QuickCheck.GenParams</code>)
		 * @param low (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 *          the lower bound on the int
		 * @param highExclusive (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 *          the exclusive upper bound on the integer
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 *          pseudo random bounded int
		 */
		public static final SourceModel.Expr getBoundedDouble(SourceModel.Expr genParams, SourceModel.Expr low, SourceModel.Expr highExclusive) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getBoundedDouble), genParams, low, highExclusive});
		}

		/**
		 * @see #getBoundedDouble(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param genParams
		 * @param low
		 * @param highExclusive
		 * @return the SourceModel.Expr representing an application of getBoundedDouble
		 */
		public static final SourceModel.Expr getBoundedDouble(SourceModel.Expr genParams, double low, double highExclusive) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getBoundedDouble), genParams, SourceModel.Expr.makeDoubleValue(low), SourceModel.Expr.makeDoubleValue(highExclusive)});
		}

		/**
		 * Name binding for function: getBoundedDouble.
		 * @see #getBoundedDouble(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getBoundedDouble = 
			QualifiedName.make(
				CAL_QuickCheck_internal.MODULE_NAME, 
				"getBoundedDouble");

		/**
		 * Helper binding method for function: getLabels. 
		 * @param r
		 * @return the SourceModule.expr representing an application of getLabels
		 */
		public static final SourceModel.Expr getLabels(SourceModel.Expr r) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getLabels), r});
		}

		/**
		 * Name binding for function: getLabels.
		 * @see #getLabels(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getLabels = 
			QualifiedName.make(CAL_QuickCheck_internal.MODULE_NAME, "getLabels");

		/**
		 * Helper binding method for function: labelResult. 
		 * @param name
		 * @param res
		 * @return the SourceModule.expr representing an application of labelResult
		 */
		public static final SourceModel.Expr labelResult(SourceModel.Expr name, SourceModel.Expr res) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.labelResult), name, res});
		}

		/**
		 * @see #labelResult(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param name
		 * @param res
		 * @return the SourceModel.Expr representing an application of labelResult
		 */
		public static final SourceModel.Expr labelResult(java.lang.String name, SourceModel.Expr res) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.labelResult), SourceModel.Expr.makeStringValue(name), res});
		}

		/**
		 * Name binding for function: labelResult.
		 * @see #labelResult(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName labelResult = 
			QualifiedName.make(
				CAL_QuickCheck_internal.MODULE_NAME, 
				"labelResult");

		/**
		 * returns a set of gen params which will create randoms independent of rightGenParams
		 * @param genParams (CAL type: <code>Cal.Utilities.QuickCheck.GenParams</code>)
		 * @return (CAL type: <code>Cal.Utilities.QuickCheck.GenParams</code>) 
		 *          <code>Cal.Utilities.QuickCheck.GenParams</code> an independent genParams that will create different independent objects
		 */
		public static final SourceModel.Expr leftGenParams(SourceModel.Expr genParams) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.leftGenParams), genParams});
		}

		/**
		 * Name binding for function: leftGenParams.
		 * @see #leftGenParams(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName leftGenParams = 
			QualifiedName.make(
				CAL_QuickCheck_internal.MODULE_NAME, 
				"leftGenParams");

		/**
		 * Helper binding method for function: nothing. 
		 * @return the SourceModule.expr representing an application of nothing
		 */
		public static final SourceModel.Expr nothing() {
			return SourceModel.Expr.Var.make(Functions.nothing);
		}

		/**
		 * Name binding for function: nothing.
		 * @see #nothing()
		 */
		public static final QualifiedName nothing = 
			QualifiedName.make(CAL_QuickCheck_internal.MODULE_NAME, "nothing");

		/**
		 * Given a function f that creates a generator of b, returns
		 * a new generator that generates functions a-&gt;b
		 * @param func (CAL type: <code>a -> Cal.Utilities.QuickCheck.Gen b</code>)
		 *          function that creates a generator
		 * @return (CAL type: <code>Cal.Utilities.QuickCheck.Gen (a -> b)</code>) 
		 *          a generator that creates functions
		 */
		public static final SourceModel.Expr promote(SourceModel.Expr func) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.promote), func});
		}

		/**
		 * Name binding for function: promote.
		 * @see #promote(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName promote = 
			QualifiedName.make(CAL_QuickCheck_internal.MODULE_NAME, "promote");

		/**
		 * returns a set of gen params which will create randoms independent of leftGenParams
		 * @param genParams (CAL type: <code>Cal.Utilities.QuickCheck.GenParams</code>)
		 * @return (CAL type: <code>Cal.Utilities.QuickCheck.GenParams</code>) 
		 *          <code>Cal.Utilities.QuickCheck.GenParams</code> an independent genParams that will create different independent objects
		 */
		public static final SourceModel.Expr rightGenParams(SourceModel.Expr genParams) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.rightGenParams), genParams});
		}

		/**
		 * Name binding for function: rightGenParams.
		 * @see #rightGenParams(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName rightGenParams = 
			QualifiedName.make(
				CAL_QuickCheck_internal.MODULE_NAME, 
				"rightGenParams");

		/**
		 * Helper binding method for function: roundDoubleToLong. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of roundDoubleToLong
		 */
		public static final SourceModel.Expr roundDoubleToLong(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.roundDoubleToLong), arg_1});
		}

		/**
		 * @see #roundDoubleToLong(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of roundDoubleToLong
		 */
		public static final SourceModel.Expr roundDoubleToLong(double arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.roundDoubleToLong), SourceModel.Expr.makeDoubleValue(arg_1)});
		}

		/**
		 * Name binding for function: roundDoubleToLong.
		 * @see #roundDoubleToLong(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName roundDoubleToLong = 
			QualifiedName.make(
				CAL_QuickCheck_internal.MODULE_NAME, 
				"roundDoubleToLong");

		/**
		 * Helper binding method for function: roundFloatToInt. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of roundFloatToInt
		 */
		public static final SourceModel.Expr roundFloatToInt(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.roundFloatToInt), arg_1});
		}

		/**
		 * @see #roundFloatToInt(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of roundFloatToInt
		 */
		public static final SourceModel.Expr roundFloatToInt(float arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.roundFloatToInt), SourceModel.Expr.makeFloatValue(arg_1)});
		}

		/**
		 * Name binding for function: roundFloatToInt.
		 * @see #roundFloatToInt(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName roundFloatToInt = 
			QualifiedName.make(
				CAL_QuickCheck_internal.MODULE_NAME, 
				"roundFloatToInt");

		/**
		 * Tests testItem the specified number of times and return a list of results. If a test fails no more tests are conducted. 
		 * If the maxTestAttempts is exceeded an error is raised.
		 * @param config (CAL type: <code>Cal.Utilities.QuickCheck.Config</code>)
		 *          the test configuration
		 * @param testItem (CAL type: <code>Cal.Utilities.QuickCheck.Testable t => t</code>)
		 *          the item to test
		 * @return (CAL type: <code>[Cal.Utilities.QuickCheck.Result]</code>) 
		 *          details the outcome of the each test
		 */
		public static final SourceModel.Expr runTests(SourceModel.Expr config, SourceModel.Expr testItem) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.runTests), config, testItem});
		}

		/**
		 * Name binding for function: runTests.
		 * @see #runTests(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName runTests = 
			QualifiedName.make(CAL_QuickCheck_internal.MODULE_NAME, "runTests");

		/**
		 * Helper binding method for function: test. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of test
		 */
		public static final SourceModel.Expr test(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.test), arg_1, arg_2});
		}

		/**
		 * Name binding for function: test.
		 * @see #test(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName test = 
			QualifiedName.make(CAL_QuickCheck_internal.MODULE_NAME, "test");

		/**
		 * Helper binding method for function: testBoolean. 
		 * @param value
		 * @param genParams
		 * @return the SourceModule.expr representing an application of testBoolean
		 */
		public static final SourceModel.Expr testBoolean(SourceModel.Expr value, SourceModel.Expr genParams) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.testBoolean), value, genParams});
		}

		/**
		 * @see #testBoolean(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value
		 * @param genParams
		 * @return the SourceModel.Expr representing an application of testBoolean
		 */
		public static final SourceModel.Expr testBoolean(boolean value, SourceModel.Expr genParams) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.testBoolean), SourceModel.Expr.makeBooleanValue(value), genParams});
		}

		/**
		 * Name binding for function: testBoolean.
		 * @see #testBoolean(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName testBoolean = 
			QualifiedName.make(
				CAL_QuickCheck_internal.MODULE_NAME, 
				"testBoolean");

		/**
		 * Tests a function using the default domain generator
		 * @param func (CAL type: <code>(Cal.Core.Debug.Show a, Cal.Utilities.QuickCheck.Arbitrary a, Cal.Utilities.QuickCheck.Testable b) => a -> b</code>)
		 * @param genParams (CAL type: <code>Cal.Utilities.QuickCheck.GenParams</code>)
		 * @return (CAL type: <code>Cal.Utilities.QuickCheck.Result</code>) 
		 */
		public static final SourceModel.Expr testFunction(SourceModel.Expr func, SourceModel.Expr genParams) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.testFunction), func, genParams});
		}

		/**
		 * Name binding for function: testFunction.
		 * @see #testFunction(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName testFunction = 
			QualifiedName.make(
				CAL_QuickCheck_internal.MODULE_NAME, 
				"testFunction");

		/**
		 * Tests a function using a supplied domain generator
		 * @param gen (CAL type: <code>Cal.Core.Debug.Show a => Cal.Utilities.QuickCheck.Gen a</code>)
		 * @param func (CAL type: <code>(Cal.Core.Debug.Show a, Cal.Utilities.QuickCheck.Testable b) => a -> b</code>)
		 * @param genParams (CAL type: <code>Cal.Utilities.QuickCheck.GenParams</code>)
		 * @return (CAL type: <code>Cal.Utilities.QuickCheck.Result</code>) 
		 */
		public static final SourceModel.Expr testFunctionGen(SourceModel.Expr gen, SourceModel.Expr func, SourceModel.Expr genParams) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.testFunctionGen), gen, func, genParams});
		}

		/**
		 * Name binding for function: testFunctionGen.
		 * @see #testFunctionGen(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName testFunctionGen = 
			QualifiedName.make(
				CAL_QuickCheck_internal.MODULE_NAME, 
				"testFunctionGen");

		/**
		 * Helper binding method for function: testProperty. 
		 * @param property
		 * @param genParams
		 * @return the SourceModule.expr representing an application of testProperty
		 */
		public static final SourceModel.Expr testProperty(SourceModel.Expr property, SourceModel.Expr genParams) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.testProperty), property, genParams});
		}

		/**
		 * Name binding for function: testProperty.
		 * @see #testProperty(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName testProperty = 
			QualifiedName.make(
				CAL_QuickCheck_internal.MODULE_NAME, 
				"testProperty");

		/**
		 * Helper binding method for function: testResult. 
		 * @param res
		 * @param genParams
		 * @return the SourceModule.expr representing an application of testResult
		 */
		public static final SourceModel.Expr testResult(SourceModel.Expr res, SourceModel.Expr genParams) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.testResult), res, genParams});
		}

		/**
		 * Name binding for function: testResult.
		 * @see #testResult(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName testResult = 
			QualifiedName.make(
				CAL_QuickCheck_internal.MODULE_NAME, 
				"testResult");

		/**
		 * Creates an alternative generator from an input generator by applying a mapping to the genParams
		 * @param v (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          controls the variant
		 * @param gen (CAL type: <code>Cal.Utilities.QuickCheck.Gen a</code>)
		 *          the input generator to vary
		 * @return (CAL type: <code>Cal.Utilities.QuickCheck.Gen a</code>) 
		 *          the altered generator
		 */
		public static final SourceModel.Expr variant(SourceModel.Expr v, SourceModel.Expr gen) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.variant), v, gen});
		}

		/**
		 * @see #variant(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param v
		 * @param gen
		 * @return the SourceModel.Expr representing an application of variant
		 */
		public static final SourceModel.Expr variant(int v, SourceModel.Expr gen) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.variant), SourceModel.Expr.makeIntValue(v), gen});
		}

		/**
		 * Name binding for function: variant.
		 * @see #variant(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName variant = 
			QualifiedName.make(CAL_QuickCheck_internal.MODULE_NAME, "variant");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = 2100920939;

}
