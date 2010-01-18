/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_Random_internal.java)
 * was generated from CAL module: Cal.Utilities.Random.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Utilities.Random module from Java code.
 *  
 * Creation date: Fri Mar 16 13:11:58 PST 2007
 * --!>
 *  
 */

package org.openquark.cal.internal.module.Cal.Utilities;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * Provides simple mechanisms for generating Lists of pseudo-random numbers of various common types.
 * @author Bo Ilic
 */
public final class CAL_Random_internal {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Utilities.Random");

	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Utilities.Random module.
	 */
	public static final class TypeConstructors {
		/**
		 * foreign class java.util.Random
		 */
		public static final QualifiedName JRandom = 
			QualifiedName.make(CAL_Random_internal.MODULE_NAME, "JRandom");

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Utilities.Random module.
	 */
	public static final class Functions {
		/**
		 * foreign constructor java.util.Random
		 * @param arg_1 (CAL type: <code>Cal.Core.Prelude.Long</code>)
		 * @return (CAL type: <code>Cal.Utilities.Random.JRandom</code>) 
		 */
		public static final SourceModel.Expr random_new_Long(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.random_new_Long), arg_1});
		}

		/**
		 * @see #random_new_Long(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of random_new_Long
		 */
		public static final SourceModel.Expr random_new_Long(long arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.random_new_Long), SourceModel.Expr.makeLongValue(arg_1)});
		}

		/**
		 * Name binding for function: random_new_Long.
		 * @see #random_new_Long(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName random_new_Long = 
			QualifiedName.make(
				CAL_Random_internal.MODULE_NAME, 
				"random_new_Long");

		/**
		 * foreign method java.util.Random.nextBoolean
		 * @param jRandom (CAL type: <code>Cal.Utilities.Random.JRandom</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr random_nextBoolean(SourceModel.Expr jRandom) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.random_nextBoolean), jRandom});
		}

		/**
		 * Name binding for function: random_nextBoolean.
		 * @see #random_nextBoolean(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName random_nextBoolean = 
			QualifiedName.make(
				CAL_Random_internal.MODULE_NAME, 
				"random_nextBoolean");

		/**
		 * foreign method java.util.Random.nextDouble
		 * @param jRandom (CAL type: <code>Cal.Utilities.Random.JRandom</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 */
		public static final SourceModel.Expr random_nextDouble(SourceModel.Expr jRandom) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.random_nextDouble), jRandom});
		}

		/**
		 * Name binding for function: random_nextDouble.
		 * @see #random_nextDouble(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName random_nextDouble = 
			QualifiedName.make(
				CAL_Random_internal.MODULE_NAME, 
				"random_nextDouble");

		/**
		 * foreign method java.util.Random.nextFloat
		 * @param jRandom (CAL type: <code>Cal.Utilities.Random.JRandom</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Float</code>) 
		 */
		public static final SourceModel.Expr random_nextFloat(SourceModel.Expr jRandom) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.random_nextFloat), jRandom});
		}

		/**
		 * Name binding for function: random_nextFloat.
		 * @see #random_nextFloat(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName random_nextFloat = 
			QualifiedName.make(
				CAL_Random_internal.MODULE_NAME, 
				"random_nextFloat");

		/**
		 * foreign method java.util.Random.nextGaussian
		 * @param jRandom (CAL type: <code>Cal.Utilities.Random.JRandom</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 */
		public static final SourceModel.Expr random_nextGaussian(SourceModel.Expr jRandom) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.random_nextGaussian), jRandom});
		}

		/**
		 * Name binding for function: random_nextGaussian.
		 * @see #random_nextGaussian(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName random_nextGaussian = 
			QualifiedName.make(
				CAL_Random_internal.MODULE_NAME, 
				"random_nextGaussian");

		/**
		 * foreign method java.util.Random.nextInt
		 * @param jRandom (CAL type: <code>Cal.Utilities.Random.JRandom</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 */
		public static final SourceModel.Expr random_nextInt(SourceModel.Expr jRandom) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.random_nextInt), jRandom});
		}

		/**
		 * Name binding for function: random_nextInt.
		 * @see #random_nextInt(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName random_nextInt = 
			QualifiedName.make(
				CAL_Random_internal.MODULE_NAME, 
				"random_nextInt");

		/**
		 * foreign method java.util.Random.nextInt
		 * @param jRandom (CAL type: <code>Cal.Utilities.Random.JRandom</code>)
		 * @param arg_2 (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 */
		public static final SourceModel.Expr random_nextInt_Int(SourceModel.Expr jRandom, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.random_nextInt_Int), jRandom, arg_2});
		}

		/**
		 * @see #random_nextInt_Int(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param jRandom
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of random_nextInt_Int
		 */
		public static final SourceModel.Expr random_nextInt_Int(SourceModel.Expr jRandom, int arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.random_nextInt_Int), jRandom, SourceModel.Expr.makeIntValue(arg_2)});
		}

		/**
		 * Name binding for function: random_nextInt_Int.
		 * @see #random_nextInt_Int(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName random_nextInt_Int = 
			QualifiedName.make(
				CAL_Random_internal.MODULE_NAME, 
				"random_nextInt_Int");

		/**
		 * foreign method java.util.Random.nextLong
		 * @param jRandom (CAL type: <code>Cal.Utilities.Random.JRandom</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Long</code>) 
		 */
		public static final SourceModel.Expr random_nextLong(SourceModel.Expr jRandom) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.random_nextLong), jRandom});
		}

		/**
		 * Name binding for function: random_nextLong.
		 * @see #random_nextLong(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName random_nextLong = 
			QualifiedName.make(
				CAL_Random_internal.MODULE_NAME, 
				"random_nextLong");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = 1014536574;

}
