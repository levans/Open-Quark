/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_Random.java)
 * was generated from CAL module: Cal.Utilities.Random.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Utilities.Random module from Java code.
 *  
 * Creation date: Fri Mar 16 13:11:57 PST 2007
 * --!>
 *  
 */

package org.openquark.cal.module.Cal.Utilities;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * Provides simple mechanisms for generating Lists of pseudo-random numbers of various common types.
 * @author Bo Ilic
 */
public final class CAL_Random {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Utilities.Random");

	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Utilities.Random module.
	 */
	public static final class Functions {
		/**
		 * A random system generated seed value that can be used as the initial seed for <code>Cal.Utilities.Random.randomInts</code> and 
		 * similar functions when repeatable random sequences are not desired. Note: this is not a pure function,
		 * and returns a different value each time it is evaluated. Currently, this just returns the system time
		 * in milliseconds, but this is subject to change.
		 * @return (CAL type: <code>Cal.Core.Prelude.Long</code>) 
		 *          a random system generated seed value.
		 */
		public static final SourceModel.Expr initialSeed() {
			return SourceModel.Expr.Var.make(Functions.initialSeed);
		}

		/**
		 * Name binding for function: initialSeed.
		 * @see #initialSeed()
		 */
		public static final QualifiedName initialSeed = 
			QualifiedName.make(CAL_Random.MODULE_NAME, "initialSeed");

		/**
		 * 
		 * @param seed (CAL type: <code>Cal.Core.Prelude.Long</code>)
		 *          the seed to use to start the list of random numbers
		 * @return (CAL type: <code>[Cal.Core.Prelude.Boolean]</code>) 
		 *          an infinite pseudo-random list of <code>Cal.Core.Prelude.Boolean</code> values uniformly distributed between <code>Cal.Core.Prelude.False</code> and <code>Cal.Core.Prelude.True</code>.
		 */
		public static final SourceModel.Expr randomBooleans(SourceModel.Expr seed) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.randomBooleans), seed});
		}

		/**
		 * @see #randomBooleans(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param seed
		 * @return the SourceModel.Expr representing an application of randomBooleans
		 */
		public static final SourceModel.Expr randomBooleans(long seed) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.randomBooleans), SourceModel.Expr.makeLongValue(seed)});
		}

		/**
		 * Name binding for function: randomBooleans.
		 * @see #randomBooleans(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName randomBooleans = 
			QualifiedName.make(CAL_Random.MODULE_NAME, "randomBooleans");

		/**
		 * 
		 * @param seed (CAL type: <code>Cal.Core.Prelude.Long</code>)
		 *          the seed to use to start the list of random numbers
		 * @param maxExclusive (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the random ints will be less than maxExclusive but greater than or equal to 0. Must be positive or an error will occur.
		 * @return (CAL type: <code>[Cal.Core.Prelude.Int]</code>) 
		 *          an infinite pseudo-random list of <code>Cal.Core.Prelude.Int</code> values uniformly distributed between 0 (inclusive) and maxExclusive (exclusive).
		 */
		public static final SourceModel.Expr randomBoundedInts(SourceModel.Expr seed, SourceModel.Expr maxExclusive) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.randomBoundedInts), seed, maxExclusive});
		}

		/**
		 * @see #randomBoundedInts(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param seed
		 * @param maxExclusive
		 * @return the SourceModel.Expr representing an application of randomBoundedInts
		 */
		public static final SourceModel.Expr randomBoundedInts(long seed, int maxExclusive) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.randomBoundedInts), SourceModel.Expr.makeLongValue(seed), SourceModel.Expr.makeIntValue(maxExclusive)});
		}

		/**
		 * Name binding for function: randomBoundedInts.
		 * @see #randomBoundedInts(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName randomBoundedInts = 
			QualifiedName.make(CAL_Random.MODULE_NAME, "randomBoundedInts");

		/**
		 * 
		 * @param seed (CAL type: <code>Cal.Core.Prelude.Long</code>)
		 *          the seed to use to start the list of random numbers
		 * @return (CAL type: <code>[Cal.Core.Prelude.Double]</code>) 
		 *          an infinite pseudo-random list of <code>Cal.Core.Prelude.Double</code> values uniformly distributed between 0.0 and 1.0.
		 */
		public static final SourceModel.Expr randomDoubles(SourceModel.Expr seed) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.randomDoubles), seed});
		}

		/**
		 * @see #randomDoubles(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param seed
		 * @return the SourceModel.Expr representing an application of randomDoubles
		 */
		public static final SourceModel.Expr randomDoubles(long seed) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.randomDoubles), SourceModel.Expr.makeLongValue(seed)});
		}

		/**
		 * Name binding for function: randomDoubles.
		 * @see #randomDoubles(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName randomDoubles = 
			QualifiedName.make(CAL_Random.MODULE_NAME, "randomDoubles");

		/**
		 * 
		 * @param seed (CAL type: <code>Cal.Core.Prelude.Long</code>)
		 *          the seed to use to start the list of random numbers
		 * @return (CAL type: <code>[Cal.Core.Prelude.Float]</code>) 
		 *          an infinite pseudo-random list of <code>Cal.Core.Prelude.Float</code> values uniformly distributed between 0.0 and 1.0.
		 */
		public static final SourceModel.Expr randomFloats(SourceModel.Expr seed) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.randomFloats), seed});
		}

		/**
		 * @see #randomFloats(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param seed
		 * @return the SourceModel.Expr representing an application of randomFloats
		 */
		public static final SourceModel.Expr randomFloats(long seed) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.randomFloats), SourceModel.Expr.makeLongValue(seed)});
		}

		/**
		 * Name binding for function: randomFloats.
		 * @see #randomFloats(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName randomFloats = 
			QualifiedName.make(CAL_Random.MODULE_NAME, "randomFloats");

		/**
		 * 
		 * @param seed (CAL type: <code>Cal.Core.Prelude.Long</code>)
		 *          the seed to use to start the list of random numbers
		 * @return (CAL type: <code>[Cal.Core.Prelude.Double]</code>) 
		 *          an infinite pseudo-random list of <code>Cal.Core.Prelude.Double</code> values that are distributed according to the Gaussian (or "normal") distribution
		 * with mean 0.0 and standard deviation 1.0.
		 */
		public static final SourceModel.Expr randomGaussians(SourceModel.Expr seed) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.randomGaussians), seed});
		}

		/**
		 * @see #randomGaussians(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param seed
		 * @return the SourceModel.Expr representing an application of randomGaussians
		 */
		public static final SourceModel.Expr randomGaussians(long seed) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.randomGaussians), SourceModel.Expr.makeLongValue(seed)});
		}

		/**
		 * Name binding for function: randomGaussians.
		 * @see #randomGaussians(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName randomGaussians = 
			QualifiedName.make(CAL_Random.MODULE_NAME, "randomGaussians");

		/**
		 * 
		 * @param seed (CAL type: <code>Cal.Core.Prelude.Long</code>)
		 *          the seed to use to start the list of random numbers
		 * @return (CAL type: <code>[Cal.Core.Prelude.Int]</code>) 
		 *          an infinite pseudo-random list of <code>Cal.Core.Prelude.Int</code> values uniformly distributed across all possible Ints.
		 */
		public static final SourceModel.Expr randomInts(SourceModel.Expr seed) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.randomInts), seed});
		}

		/**
		 * @see #randomInts(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param seed
		 * @return the SourceModel.Expr representing an application of randomInts
		 */
		public static final SourceModel.Expr randomInts(long seed) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.randomInts), SourceModel.Expr.makeLongValue(seed)});
		}

		/**
		 * Name binding for function: randomInts.
		 * @see #randomInts(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName randomInts = 
			QualifiedName.make(CAL_Random.MODULE_NAME, "randomInts");

		/**
		 * 
		 * @param seed (CAL type: <code>Cal.Core.Prelude.Long</code>)
		 *          the seed to use to start the list of random numbers
		 * @return (CAL type: <code>[Cal.Core.Prelude.Long]</code>) 
		 *          an infinite pseudo-random list of <code>Cal.Core.Prelude.Long</code> values uniformly distributed across all possible Longs.
		 */
		public static final SourceModel.Expr randomLongs(SourceModel.Expr seed) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.randomLongs), seed});
		}

		/**
		 * @see #randomLongs(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param seed
		 * @return the SourceModel.Expr representing an application of randomLongs
		 */
		public static final SourceModel.Expr randomLongs(long seed) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.randomLongs), SourceModel.Expr.makeLongValue(seed)});
		}

		/**
		 * Name binding for function: randomLongs.
		 * @see #randomLongs(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName randomLongs = 
			QualifiedName.make(CAL_Random.MODULE_NAME, "randomLongs");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = -343697826;

}
