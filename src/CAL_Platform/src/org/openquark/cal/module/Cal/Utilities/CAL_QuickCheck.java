/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_QuickCheck.java)
 * was generated from CAL module: Cal.Utilities.QuickCheck.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Utilities.QuickCheck module from Java code.
 *  
 * Creation date: Fri Aug 17 18:11:06 PDT 2007
 * --!>
 *  
 */

package org.openquark.cal.module.Cal.Utilities;

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
public final class CAL_QuickCheck {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Utilities.QuickCheck");

	/**
	 * This inner class (TypeClasses) contains constants
	 * and methods related to binding to CAL TypeClasses in the Cal.Utilities.QuickCheck module.
	 */
	public static final class TypeClasses {
		/**
		 * The <code>Arbitrary</code> type class is used to represent types for which 
		 * pseudo random instances can be created. All the input parameters of test functions
		 * must be instances of <code>Arbitrary</code>.
		 */
		public static final QualifiedName Arbitrary = 
			QualifiedName.make(CAL_QuickCheck.MODULE_NAME, "Arbitrary");

		/**
		 * The <code>Testable</code> is an abstract type class used to represent types that can be tested
		 */
		public static final QualifiedName Testable = 
			QualifiedName.make(CAL_QuickCheck.MODULE_NAME, "Testable");

	}
	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Utilities.QuickCheck module.
	 */
	public static final class TypeConstructors {
		/**
		 * configuration used for controlling how tests are run
		 */
		public static final QualifiedName Config = 
			QualifiedName.make(CAL_QuickCheck.MODULE_NAME, "Config");

		/**
		 * Gen is an abstract type that is used for creating 
		 * instances of <code>Cal.Utilities.QuickCheck.Arbitrary</code> types for testing. 
		 * The generation is controlled by the <code>Cal.Utilities.QuickCheck.GenParams</code>. 
		 * A generator can create different instances of a type by varying the Random source 
		 * in the GenParams.
		 */
		public static final QualifiedName Gen = 
			QualifiedName.make(CAL_QuickCheck.MODULE_NAME, "Gen");

		/**
		 * The GenParams is an abstract type used for controlling the generation of <code>Cal.Utilities.QuickCheck.Arbitrary</code> instances.
		 * GenParams contain a size parameter for controlling the size of generated instances. For example this is used to constrain
		 * the size of Arbitrary Lists and Strings. GenParams also contains a random source. This is used as the basis
		 * for creating all arbitrary instances. Varying the random source will vary the instances created by a generator.
		 */
		public static final QualifiedName GenParams = 
			QualifiedName.make(CAL_QuickCheck.MODULE_NAME, "GenParams");

		/**
		 * An abstract type for representing a program Property. A property is esentially a testable function with
		 * wrappers such as <code>Cal.Utilities.QuickCheck.implies</code> and <code>Cal.Utilities.QuickCheck.classify</code>.
		 */
		public static final QualifiedName Property = 
			QualifiedName.make(CAL_QuickCheck.MODULE_NAME, "Property");

		/**
		 * A type for representing a test result. 
		 * Contains the outcome of the test, the arguments used to conduct the test,
		 * and any labels (see <code>Cal.Utilities.QuickCheck.classify</code>) associated with the test arguments.
		 */
		public static final QualifiedName Result = 
			QualifiedName.make(CAL_QuickCheck.MODULE_NAME, "Result");

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Utilities.QuickCheck module.
	 */
	public static final class Functions {
		/**
		 * 
		 * @return (CAL type: <code>Cal.Utilities.QuickCheck.Arbitrary a => Cal.Utilities.QuickCheck.Gen a</code>) 
		 *          a generator for the type
		 */
		public static final SourceModel.Expr arbitrary() {
			return SourceModel.Expr.Var.make(Functions.arbitrary);
		}

		/**
		 * Name binding for function: arbitrary.
		 * @see #arbitrary()
		 */
		public static final QualifiedName arbitrary = 
			QualifiedName.make(CAL_QuickCheck.MODULE_NAME, "arbitrary");

		/**
		 * Verifies a property using supplied configuration.
		 * @param config (CAL type: <code>Cal.Utilities.QuickCheck.Config</code>)
		 *          the configuration used for testing
		 * @param testItem (CAL type: <code>Cal.Utilities.QuickCheck.Testable t => t</code>)
		 *          the property to test
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          true if configured number of tests can be generated and pass, false if a test fails or the required number of tests cannot be generated.
		 */
		public static final SourceModel.Expr check(SourceModel.Expr config, SourceModel.Expr testItem) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.check), config, testItem});
		}

		/**
		 * Name binding for function: check.
		 * @see #check(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName check = 
			QualifiedName.make(CAL_QuickCheck.MODULE_NAME, "check");

		/**
		 * creates a generator that will return items from the input generators
		 * @param listOfGenerators (CAL type: <code>[Cal.Utilities.QuickCheck.Gen a]</code>)
		 * @return (CAL type: <code>Cal.Utilities.QuickCheck.Gen a</code>) 
		 *          <code>Cal.Utilities.QuickCheck.Gen</code> a generator which will create an item using one of the input generators
		 */
		public static final SourceModel.Expr chooseOneOf(SourceModel.Expr listOfGenerators) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.chooseOneOf), listOfGenerators});
		}

		/**
		 * Name binding for function: chooseOneOf.
		 * @see #chooseOneOf(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName chooseOneOf = 
			QualifiedName.make(CAL_QuickCheck.MODULE_NAME, "chooseOneOf");

		/**
		 * creates a generator that will return items from the input generators with the
		 * specified frequencies.
		 * @param freqList (CAL type: <code>Cal.Utilities.QuickCheck.Arbitrary a => [(Cal.Core.Prelude.Int, Cal.Utilities.QuickCheck.Gen a)]</code>)
		 *          a list of pairs of (frequency, gen)
		 * @return (CAL type: <code>Cal.Utilities.QuickCheck.Arbitrary a => Cal.Utilities.QuickCheck.Gen a</code>) 
		 *          <code>Cal.Utilities.QuickCheck.Gen</code> a generator of items with specified frequency
		 */
		public static final SourceModel.Expr chooseOneOfWithFreq(SourceModel.Expr freqList) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.chooseOneOfWithFreq), freqList});
		}

		/**
		 * Name binding for function: chooseOneOfWithFreq.
		 * @see #chooseOneOfWithFreq(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName chooseOneOfWithFreq = 
			QualifiedName.make(
				CAL_QuickCheck.MODULE_NAME, 
				"chooseOneOfWithFreq");

		/**
		 * creates a property which classifys the results for a test
		 * whenever the condition is met. This may be used for collecting 
		 * information about the
		 * nature of the data used for testing. For example:
		 * <p>
		 * 
		 * <pre>prop_reverse_classify :: [Int] -&gt; Property;
		 * prop_reverse_classify list = 
		 *    classify ((length list) &lt;= 1) "trivial" $
		 *    classify ((length list) &gt;1) "nontrivial" 
		 *    (list==reverse (reverse list));</pre>
		 * 
		 * <p>
		 * This will classify all test cases for which the list is of length 0 or 1 as trivial, and all others as nontrivial. 
		 * 
		 * @param condition (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 *          the condition for classifying
		 * @param name (CAL type: <code>Cal.Core.Debug.Show b => b</code>)
		 *          the label to use
		 * @param testItem (CAL type: <code>Cal.Utilities.QuickCheck.Testable a => a</code>)
		 *          the item to test
		 * @return (CAL type: <code>Cal.Utilities.QuickCheck.Property</code>) 
		 *          a testable property
		 */
		public static final SourceModel.Expr classify(SourceModel.Expr condition, SourceModel.Expr name, SourceModel.Expr testItem) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.classify), condition, name, testItem});
		}

		/**
		 * @see #classify(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param condition
		 * @param name
		 * @param testItem
		 * @return the SourceModel.Expr representing an application of classify
		 */
		public static final SourceModel.Expr classify(boolean condition, SourceModel.Expr name, SourceModel.Expr testItem) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.classify), SourceModel.Expr.makeBooleanValue(condition), name, testItem});
		}

		/**
		 * Name binding for function: classify.
		 * @see #classify(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName classify = 
			QualifiedName.make(CAL_QuickCheck.MODULE_NAME, "classify");

		/**
		 * Modifies a generator based on an instance of this type class.
		 * This method is required for generating functions with a domain of this type class
		 * @param arg_1 (CAL type: <code>Cal.Utilities.QuickCheck.Arbitrary a => a</code>)
		 * @param arg_2 (CAL type: <code>Cal.Utilities.QuickCheck.Gen b</code>)
		 * @return (CAL type: <code>Cal.Utilities.QuickCheck.Gen b</code>) 
		 *          modified generator based on an instance of this type class
		 */
		public static final SourceModel.Expr coarbitrary(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.coarbitrary), arg_1, arg_2});
		}

		/**
		 * Name binding for function: coarbitrary.
		 * @see #coarbitrary(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName coarbitrary = 
			QualifiedName.make(CAL_QuickCheck.MODULE_NAME, "coarbitrary");

		/**
		 * the default configuration used to run tests
		 * @return (CAL type: <code>Cal.Utilities.QuickCheck.Config</code>) 
		 */
		public static final SourceModel.Expr defaultConfig() {
			return SourceModel.Expr.Var.make(Functions.defaultConfig);
		}

		/**
		 * Name binding for function: defaultConfig.
		 * @see #defaultConfig()
		 */
		public static final QualifiedName defaultConfig = 
			QualifiedName.make(CAL_QuickCheck.MODULE_NAME, "defaultConfig");

		/**
		 * This is the default sizing function for testing -
		 * as test are performed the sizing gradually approaches a limit.
		 * @param testIteration (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the test iternation number for which to compute the sizing
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the sizing for the iteration
		 */
		public static final SourceModel.Expr defaultResizing(SourceModel.Expr testIteration) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.defaultResizing), testIteration});
		}

		/**
		 * @see #defaultResizing(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param testIteration
		 * @return the SourceModel.Expr representing an application of defaultResizing
		 */
		public static final SourceModel.Expr defaultResizing(int testIteration) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.defaultResizing), SourceModel.Expr.makeIntValue(testIteration)});
		}

		/**
		 * Name binding for function: defaultResizing.
		 * @see #defaultResizing(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName defaultResizing = 
			QualifiedName.make(CAL_QuickCheck.MODULE_NAME, "defaultResizing");

		/**
		 * Allows a function to be tested using a specific generator. This allows the user
		 * to supply their own custom generator. This may be necessary when a property
		 * has implies conditions that are hard to satisfy with random data. For example consider 
		 * the function orderedInsert, which inserts a number into an ordered list preserving the ordered property of the list.
		 * This could be tested using an <code>Cal.Utilities.QuickCheck.implies</code> expression to ensure that the test list
		 * is ordered, but many randomly generated lists will not satisfy this condition, and so the 
		 * testing may prove problematic. This situation can be improved by implementing a custom
		 * List generator that is guaranteed to create ordered lists. 
		 * In this case the forAll function is used to 
		 * specify the custom generator.
		 * @param gen (CAL type: <code>Cal.Core.Debug.Show a => Cal.Utilities.QuickCheck.Gen a</code>)
		 *          the custom generator to use in testing.
		 * @param func (CAL type: <code>(Cal.Core.Debug.Show a, Cal.Utilities.QuickCheck.Testable b) => a -> b</code>)
		 *          the function to be tested.
		 * @return (CAL type: <code>Cal.Utilities.QuickCheck.Property</code>) 
		 *          the property for testing.
		 */
		public static final SourceModel.Expr forAll(SourceModel.Expr gen, SourceModel.Expr func) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.forAll), gen, func});
		}

		/**
		 * Name binding for function: forAll.
		 * @see #forAll(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName forAll = 
			QualifiedName.make(CAL_QuickCheck.MODULE_NAME, "forAll");

		/**
		 * Converts a list of results to a string in which the parameters used
		 * in each test are shown on a seperate line. This function is most useful
		 * for checking the test parameters are as expected.
		 * e.g. formatResults (check defaultConfig test_property)
		 * @param results (CAL type: <code>[Cal.Utilities.QuickCheck.Result]</code>)
		 *          the list of results to format into a string
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          a string with each set of test parameters on a seperate line.
		 */
		public static final SourceModel.Expr formatResults(SourceModel.Expr results) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.formatResults), results});
		}

		/**
		 * Name binding for function: formatResults.
		 * @see #formatResults(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName formatResults = 
			QualifiedName.make(CAL_QuickCheck.MODULE_NAME, "formatResults");

		/**
		 * Creates an item using a generator
		 * @param genParams (CAL type: <code>Cal.Utilities.QuickCheck.GenParams</code>)
		 *          the generator params
		 * @param gen (CAL type: <code>Cal.Utilities.QuickCheck.Gen a</code>)
		 *          the generator
		 * @return (CAL type: <code>a</code>) 
		 *          generated item
		 */
		public static final SourceModel.Expr generate(SourceModel.Expr genParams, SourceModel.Expr gen) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.generate), genParams, gen});
		}

		/**
		 * Name binding for function: generate.
		 * @see #generate(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName generate = 
			QualifiedName.make(CAL_QuickCheck.MODULE_NAME, "generate");

		/**
		 * Creates a string showing n items created using a generator.
		 * This is intended to aid people developing <code>Cal.Utilities.QuickCheck.Arbitrary</code> instances.
		 * @param numItems (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the number of items to generate
		 * @param gp (CAL type: <code>Cal.Utilities.QuickCheck.GenParams</code>)
		 *          the generator params
		 * @param gen (CAL type: <code>Cal.Core.Debug.Show a => Cal.Utilities.QuickCheck.Gen a</code>)
		 *          the generator
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          string with one item per line
		 */
		public static final SourceModel.Expr generateNInstances(SourceModel.Expr numItems, SourceModel.Expr gp, SourceModel.Expr gen) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.generateNInstances), numItems, gp, gen});
		}

		/**
		 * @see #generateNInstances(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param numItems
		 * @param gp
		 * @param gen
		 * @return the SourceModel.Expr representing an application of generateNInstances
		 */
		public static final SourceModel.Expr generateNInstances(int numItems, SourceModel.Expr gp, SourceModel.Expr gen) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.generateNInstances), SourceModel.Expr.makeIntValue(numItems), gp, gen});
		}

		/**
		 * Name binding for function: generateNInstances.
		 * @see #generateNInstances(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName generateNInstances = 
			QualifiedName.make(CAL_QuickCheck.MODULE_NAME, "generateNInstances");

		/**
		 * Used to extract a pseudo random bounded integer from <code>Cal.Utilities.QuickCheck.GenParams</code>
		 * @param genParams (CAL type: <code>Cal.Utilities.QuickCheck.GenParams</code>)
		 * @param low (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the lower bound on the int
		 * @param highExclusive (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the exclusive upper bound on the integer
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          pseudo random bounded int
		 */
		public static final SourceModel.Expr getBoundedInt(SourceModel.Expr genParams, SourceModel.Expr low, SourceModel.Expr highExclusive) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getBoundedInt), genParams, low, highExclusive});
		}

		/**
		 * @see #getBoundedInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param genParams
		 * @param low
		 * @param highExclusive
		 * @return the SourceModel.Expr representing an application of getBoundedInt
		 */
		public static final SourceModel.Expr getBoundedInt(SourceModel.Expr genParams, int low, int highExclusive) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getBoundedInt), genParams, SourceModel.Expr.makeIntValue(low), SourceModel.Expr.makeIntValue(highExclusive)});
		}

		/**
		 * Name binding for function: getBoundedInt.
		 * @see #getBoundedInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getBoundedInt = 
			QualifiedName.make(CAL_QuickCheck.MODULE_NAME, "getBoundedInt");

		/**
		 * Used to extract the nth pseudo random bounded integer from <code>Cal.Utilities.QuickCheck.GenParams</code>
		 * @param nth (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          gets the nth bounded integer from genParams
		 * @param genParams (CAL type: <code>Cal.Utilities.QuickCheck.GenParams</code>)
		 * @param low (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the lower bound on the int
		 * @param highExclusive (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the exclusive upper bound on the integer
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          pseudo random bounded int
		 */
		public static final SourceModel.Expr getNthBoundedInt(SourceModel.Expr nth, SourceModel.Expr genParams, SourceModel.Expr low, SourceModel.Expr highExclusive) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getNthBoundedInt), nth, genParams, low, highExclusive});
		}

		/**
		 * @see #getNthBoundedInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param nth
		 * @param genParams
		 * @param low
		 * @param highExclusive
		 * @return the SourceModel.Expr representing an application of getNthBoundedInt
		 */
		public static final SourceModel.Expr getNthBoundedInt(int nth, SourceModel.Expr genParams, int low, int highExclusive) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getNthBoundedInt), SourceModel.Expr.makeIntValue(nth), genParams, SourceModel.Expr.makeIntValue(low), SourceModel.Expr.makeIntValue(highExclusive)});
		}

		/**
		 * Name binding for function: getNthBoundedInt.
		 * @see #getNthBoundedInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getNthBoundedInt = 
			QualifiedName.make(CAL_QuickCheck.MODULE_NAME, "getNthBoundedInt");

		/**
		 * creates a Property with a precondition.
		 * The testItem is tested only if the precondition is true. For example:
		 * <p>
		 * 
		 * <pre>prop_Insert :: Int -&gt; [Int] -&gt; Property;
		 * prop_Insert x list = (ordered list) `implies` (ordered (orderedInsert x list));</pre>
		 * 
		 * <p>
		 * This property will only test the orderedInsert function when 
		 * the precondition (ordered list) is True.  If a precondition is rarely 
		 * satisfied by random data the Property will be problematic to test. In this case
		 * a custom generator that has a greater chance of satisfying the
		 * precondition should be used, see <code>Cal.Utilities.QuickCheck.forAll</code>.
		 * 
		 * @param precondition (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 *          the condition that must be met before testItem will be tested
		 * @param testItem (CAL type: <code>Cal.Utilities.QuickCheck.Testable a => a</code>)
		 *          the item to test
		 * @return (CAL type: <code>Cal.Utilities.QuickCheck.Property</code>) 
		 *          Property - the testItem guarded by the precondition
		 */
		public static final SourceModel.Expr implies(SourceModel.Expr precondition, SourceModel.Expr testItem) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.implies), precondition, testItem});
		}

		/**
		 * @see #implies(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param precondition
		 * @param testItem
		 * @return the SourceModel.Expr representing an application of implies
		 */
		public static final SourceModel.Expr implies(boolean precondition, SourceModel.Expr testItem) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.implies), SourceModel.Expr.makeBooleanValue(precondition), testItem});
		}

		/**
		 * Name binding for function: implies.
		 * @see #implies(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName implies = 
			QualifiedName.make(CAL_QuickCheck.MODULE_NAME, "implies");

		/**
		 * Creates a generator that returns bounded integers
		 * @param low (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the lower bound on the int
		 * @param highExclusive (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the exclusive upper bound on the integer
		 * @return (CAL type: <code>Cal.Utilities.QuickCheck.Gen Cal.Core.Prelude.Int</code>) 
		 *          generator of pseudo random bounded int
		 */
		public static final SourceModel.Expr makeBoundedIntGen(SourceModel.Expr low, SourceModel.Expr highExclusive) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeBoundedIntGen), low, highExclusive});
		}

		/**
		 * @see #makeBoundedIntGen(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param low
		 * @param highExclusive
		 * @return the SourceModel.Expr representing an application of makeBoundedIntGen
		 */
		public static final SourceModel.Expr makeBoundedIntGen(int low, int highExclusive) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeBoundedIntGen), SourceModel.Expr.makeIntValue(low), SourceModel.Expr.makeIntValue(highExclusive)});
		}

		/**
		 * Name binding for function: makeBoundedIntGen.
		 * @see #makeBoundedIntGen(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeBoundedIntGen = 
			QualifiedName.make(CAL_QuickCheck.MODULE_NAME, "makeBoundedIntGen");

		/**
		 * Creates a generator that will return an item from the input list.
		 * @param listOfItems (CAL type: <code>[a]</code>)
		 * @return (CAL type: <code>Cal.Utilities.QuickCheck.Gen a</code>) 
		 *          <code>Cal.Utilities.QuickCheck.Gen</code> a generator of items contained in the input list
		 */
		public static final SourceModel.Expr makeChooseOneOfGen(SourceModel.Expr listOfItems) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeChooseOneOfGen), listOfItems});
		}

		/**
		 * Name binding for function: makeChooseOneOfGen.
		 * @see #makeChooseOneOfGen(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeChooseOneOfGen = 
			QualifiedName.make(CAL_QuickCheck.MODULE_NAME, "makeChooseOneOfGen");

		/**
		 * make a test configuration
		 * @param numTests (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the number of tests to run
		 * @param maxTries (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the maximum number of times data will be generated to run tests
		 * if the test has an implies constraint test data may have to be discarded
		 * because it does not meet the constraints. 10 times num tests
		 * is a reasonable value.
		 * @param resizing (CAL type: <code>Cal.Core.Prelude.Int -> Cal.Core.Prelude.Int</code>)
		 *          The function used to resize the generator parameters after each test
		 * @param genParams (CAL type: <code>Cal.Utilities.QuickCheck.GenParams</code>)
		 *          the generator parameters.
		 * @return (CAL type: <code>Cal.Utilities.QuickCheck.Config</code>) 
		 *          the new test configuration
		 */
		public static final SourceModel.Expr makeConfig(SourceModel.Expr numTests, SourceModel.Expr maxTries, SourceModel.Expr resizing, SourceModel.Expr genParams) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeConfig), numTests, maxTries, resizing, genParams});
		}

		/**
		 * @see #makeConfig(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param numTests
		 * @param maxTries
		 * @param resizing
		 * @param genParams
		 * @return the SourceModel.Expr representing an application of makeConfig
		 */
		public static final SourceModel.Expr makeConfig(int numTests, int maxTries, SourceModel.Expr resizing, SourceModel.Expr genParams) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeConfig), SourceModel.Expr.makeIntValue(numTests), SourceModel.Expr.makeIntValue(maxTries), resizing, genParams});
		}

		/**
		 * Name binding for function: makeConfig.
		 * @see #makeConfig(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeConfig = 
			QualifiedName.make(CAL_QuickCheck.MODULE_NAME, "makeConfig");

		/**
		 * make a configuration which will run the specified number of tests
		 * @param numTests (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the number of tests to run.
		 * @return (CAL type: <code>Cal.Utilities.QuickCheck.Config</code>) 
		 *          the test configuration.
		 */
		public static final SourceModel.Expr makeConfigWithMaxTests(SourceModel.Expr numTests) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeConfigWithMaxTests), numTests});
		}

		/**
		 * @see #makeConfigWithMaxTests(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param numTests
		 * @return the SourceModel.Expr representing an application of makeConfigWithMaxTests
		 */
		public static final SourceModel.Expr makeConfigWithMaxTests(int numTests) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeConfigWithMaxTests), SourceModel.Expr.makeIntValue(numTests)});
		}

		/**
		 * Name binding for function: makeConfigWithMaxTests.
		 * @see #makeConfigWithMaxTests(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeConfigWithMaxTests = 
			QualifiedName.make(
				CAL_QuickCheck.MODULE_NAME, 
				"makeConfigWithMaxTests");

		/**
		 * Creates a constant generator that will always return the specified item
		 * @param item (CAL type: <code>a</code>)
		 * @return (CAL type: <code>Cal.Utilities.QuickCheck.Gen a</code>) 
		 *          <code>Cal.Utilities.QuickCheck.Gen</code> a constant generator
		 */
		public static final SourceModel.Expr makeConstGen(SourceModel.Expr item) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeConstGen), item});
		}

		/**
		 * Name binding for function: makeConstGen.
		 * @see #makeConstGen(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeConstGen = 
			QualifiedName.make(CAL_QuickCheck.MODULE_NAME, "makeConstGen");

		/**
		 * A default set of generator parameters
		 * @return (CAL type: <code>Cal.Utilities.QuickCheck.GenParams</code>) 
		 *          default genParams
		 */
		public static final SourceModel.Expr makeDefaultGenParams() {
			return SourceModel.Expr.Var.make(Functions.makeDefaultGenParams);
		}

		/**
		 * Name binding for function: makeDefaultGenParams.
		 * @see #makeDefaultGenParams()
		 */
		public static final QualifiedName makeDefaultGenParams = 
			QualifiedName.make(
				CAL_QuickCheck.MODULE_NAME, 
				"makeDefaultGenParams");

		/**
		 * Constructs a generator from a generator function
		 * @param genFunction (CAL type: <code>Cal.Utilities.QuickCheck.GenParams -> a</code>)
		 *          the generator function
		 * @return (CAL type: <code>Cal.Utilities.QuickCheck.Gen a</code>) 
		 */
		public static final SourceModel.Expr makeGen(SourceModel.Expr genFunction) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeGen), genFunction});
		}

		/**
		 * Name binding for function: makeGen.
		 * @see #makeGen(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeGen = 
			QualifiedName.make(CAL_QuickCheck.MODULE_NAME, "makeGen");

		/**
		 * creates a list generator
		 * @param lengthGen (CAL type: <code>Cal.Utilities.QuickCheck.Gen Cal.Core.Prelude.Int</code>)
		 *          generator used to determine the length of the lists
		 * @param itemGen (CAL type: <code>Cal.Utilities.QuickCheck.Gen a</code>)
		 *          generator used for the items in generated lists
		 * @return (CAL type: <code>Cal.Utilities.QuickCheck.Gen [a]</code>) 
		 *          generator of lists of items
		 */
		public static final SourceModel.Expr makeListGen(SourceModel.Expr lengthGen, SourceModel.Expr itemGen) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeListGen), lengthGen, itemGen});
		}

		/**
		 * Name binding for function: makeListGen.
		 * @see #makeListGen(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeListGen = 
			QualifiedName.make(CAL_QuickCheck.MODULE_NAME, "makeListGen");

		/**
		 * Creates a new generator by applying a mapping to the output of an existing generator
		 * @param func (CAL type: <code>a -> b</code>)
		 *          the mapping function to apply
		 * @param gen (CAL type: <code>Cal.Utilities.QuickCheck.Gen a</code>)
		 *          the base generator
		 * @return (CAL type: <code>Cal.Utilities.QuickCheck.Gen b</code>) 
		 *          the new generator
		 */
		public static final SourceModel.Expr mapGen(SourceModel.Expr func, SourceModel.Expr gen) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.mapGen), func, gen});
		}

		/**
		 * Name binding for function: mapGen.
		 * @see #mapGen(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName mapGen = 
			QualifiedName.make(CAL_QuickCheck.MODULE_NAME, "mapGen");

		/**
		 * Used to split a genParams into different independent streams that can be used
		 * for creating independent arbitrary instances.
		 * @param nth (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the nth independent genParams
		 * @param genParams (CAL type: <code>Cal.Utilities.QuickCheck.GenParams</code>)
		 * @return (CAL type: <code>Cal.Utilities.QuickCheck.GenParams</code>) 
		 *          <code>Cal.Utilities.QuickCheck.GenParams</code> an independent genParams that will create different independent objects
		 */
		public static final SourceModel.Expr nthGenParams(SourceModel.Expr nth, SourceModel.Expr genParams) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.nthGenParams), nth, genParams});
		}

		/**
		 * @see #nthGenParams(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param nth
		 * @param genParams
		 * @return the SourceModel.Expr representing an application of nthGenParams
		 */
		public static final SourceModel.Expr nthGenParams(int nth, SourceModel.Expr genParams) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.nthGenParams), SourceModel.Expr.makeIntValue(nth), genParams});
		}

		/**
		 * Name binding for function: nthGenParams.
		 * @see #nthGenParams(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName nthGenParams = 
			QualifiedName.make(CAL_QuickCheck.MODULE_NAME, "nthGenParams");

		/**
		 * Verifies a property using default configuration.
		 * @param testItem (CAL type: <code>Cal.Utilities.QuickCheck.Testable t => t</code>)
		 *          the item to test
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          true iff all tests pass
		 */
		public static final SourceModel.Expr quickCheck(SourceModel.Expr testItem) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.quickCheck), testItem});
		}

		/**
		 * Name binding for function: quickCheck.
		 * @see #quickCheck(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName quickCheck = 
			QualifiedName.make(CAL_QuickCheck.MODULE_NAME, "quickCheck");

		/**
		 * Create a new GenParams with updated sizing parameter
		 * @param newSize (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the new size
		 * @param genParams (CAL type: <code>Cal.Utilities.QuickCheck.GenParams</code>)
		 * @return (CAL type: <code>Cal.Utilities.QuickCheck.GenParams</code>) 
		 *          the new genParams
		 */
		public static final SourceModel.Expr resize(SourceModel.Expr newSize, SourceModel.Expr genParams) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.resize), newSize, genParams});
		}

		/**
		 * @see #resize(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param newSize
		 * @param genParams
		 * @return the SourceModel.Expr representing an application of resize
		 */
		public static final SourceModel.Expr resize(int newSize, SourceModel.Expr genParams) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.resize), SourceModel.Expr.makeIntValue(newSize), genParams});
		}

		/**
		 * Name binding for function: resize.
		 * @see #resize(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName resize = 
			QualifiedName.make(CAL_QuickCheck.MODULE_NAME, "resize");

		/**
		 * This tests a property using the default configuration and returns
		 * a string suitable for display that described the parameters
		 * used for each test and the result.
		 * @param testItem (CAL type: <code>Cal.Utilities.QuickCheck.Testable t => t</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr showResults(SourceModel.Expr testItem) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showResults), testItem});
		}

		/**
		 * Name binding for function: showResults.
		 * @see #showResults(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showResults = 
			QualifiedName.make(CAL_QuickCheck.MODULE_NAME, "showResults");

		/**
		 * Returns the sizing parameter associated with genParams
		 * This should be used to control the size of the generated test data, e.g. it is used to limit the maximum size of 
		 * arbitrary lists.
		 * @param genParams (CAL type: <code>Cal.Utilities.QuickCheck.GenParams</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the size parameter
		 */
		public static final SourceModel.Expr size(SourceModel.Expr genParams) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.size), genParams});
		}

		/**
		 * Name binding for function: size.
		 * @see #size(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName size = 
			QualifiedName.make(CAL_QuickCheck.MODULE_NAME, "size");

		/**
		 * Summarizes results. This summarizes test data showing the percentage matching
		 * each of the classification conditions.
		 * @param results (CAL type: <code>[Cal.Utilities.QuickCheck.Result]</code>)
		 *          the list of results to summarize
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          summary - a string describing the results
		 */
		public static final SourceModel.Expr summarize(SourceModel.Expr results) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.summarize), results});
		}

		/**
		 * Name binding for function: summarize.
		 * @see #summarize(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName summarize = 
			QualifiedName.make(CAL_QuickCheck.MODULE_NAME, "summarize");

		/**
		 * Runs tests according to config and returns a list of results, or the 
		 * first failing result. This function may be used in conjunction with
		 * <code>Cal.Utilities.QuickCheck.formatResults</code> or <code>Cal.Utilities.QuickCheck.summarize</code>.
		 * @param config (CAL type: <code>Cal.Utilities.QuickCheck.Config</code>)
		 *          the confiuration to use for testing
		 * @param testItem (CAL type: <code>Cal.Utilities.QuickCheck.Testable t => t</code>)
		 *          the property to test
		 * @return (CAL type: <code>[Cal.Utilities.QuickCheck.Result]</code>) 
		 *          list of results, or first failing result.
		 */
		public static final SourceModel.Expr testResults(SourceModel.Expr config, SourceModel.Expr testItem) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.testResults), config, testItem});
		}

		/**
		 * Name binding for function: testResults.
		 * @see #testResults(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName testResults = 
			QualifiedName.make(CAL_QuickCheck.MODULE_NAME, "testResults");

		/**
		 * Tests a property and returns the list of results, or the first failing test.
		 * @param testItem (CAL type: <code>Cal.Utilities.QuickCheck.Testable t => t</code>)
		 * @return (CAL type: <code>[Cal.Utilities.QuickCheck.Result]</code>) 
		 */
		public static final SourceModel.Expr verboseCheck(SourceModel.Expr testItem) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.verboseCheck), testItem});
		}

		/**
		 * Name binding for function: verboseCheck.
		 * @see #verboseCheck(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName verboseCheck = 
			QualifiedName.make(CAL_QuickCheck.MODULE_NAME, "verboseCheck");

		/**
		 * Zips two generators with a combining function to create a new generator
		 * @param func (CAL type: <code>a -> b -> c</code>)
		 *          the combining function
		 * @param gen1 (CAL type: <code>Cal.Utilities.QuickCheck.Gen a</code>)
		 * @param gen2 (CAL type: <code>Cal.Utilities.QuickCheck.Gen b</code>)
		 * @return (CAL type: <code>Cal.Utilities.QuickCheck.Gen c</code>) 
		 *          the combined generator
		 */
		public static final SourceModel.Expr zipWithGen(SourceModel.Expr func, SourceModel.Expr gen1, SourceModel.Expr gen2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.zipWithGen), func, gen1, gen2});
		}

		/**
		 * Name binding for function: zipWithGen.
		 * @see #zipWithGen(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName zipWithGen = 
			QualifiedName.make(CAL_QuickCheck.MODULE_NAME, "zipWithGen");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = 174689127;

}
