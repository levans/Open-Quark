/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_Decimal_internal.java)
 * was generated from CAL module: Cal.Utilities.Decimal.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Utilities.Decimal module from Java code.
 *  
 * Creation date: Fri Mar 16 13:11:57 PST 2007
 * --!>
 *  
 */

package org.openquark.cal.internal.module.Cal.Utilities;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * Defines many useful functions for the <code>Cal.Core.Prelude.Decimal</code> type. Note that the <code>Cal.Core.Prelude.Decimal</code>
 * type itself is defined in the <code>Cal.Core.Prelude</code> module to make it available to Prelude functions.
 * @author James Wright
 */
public final class CAL_Decimal_internal {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Utilities.Decimal");

	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Utilities.Decimal module.
	 */
	public static final class Functions {
		/**
		 * Helper binding method for function: divideWithRoundingExamples. 
		 * @return the SourceModule.expr representing an application of divideWithRoundingExamples
		 */
		public static final SourceModel.Expr divideWithRoundingExamples() {
			return 
				SourceModel.Expr.Var.make(Functions.divideWithRoundingExamples);
		}

		/**
		 * Name binding for function: divideWithRoundingExamples.
		 * @see #divideWithRoundingExamples()
		 */
		public static final QualifiedName divideWithRoundingExamples = 
			QualifiedName.make(
				CAL_Decimal_internal.MODULE_NAME, 
				"divideWithRoundingExamples");

		/**
		 * Helper binding method for function: divideWithScaleAndRoundingExamples. 
		 * @return the SourceModule.expr representing an application of divideWithScaleAndRoundingExamples
		 */
		public static final SourceModel.Expr divideWithScaleAndRoundingExamples() {
			return 
				SourceModel.Expr.Var.make(
					Functions.divideWithScaleAndRoundingExamples);
		}

		/**
		 * Name binding for function: divideWithScaleAndRoundingExamples.
		 * @see #divideWithScaleAndRoundingExamples()
		 */
		public static final QualifiedName divideWithScaleAndRoundingExamples = 
			QualifiedName.make(
				CAL_Decimal_internal.MODULE_NAME, 
				"divideWithScaleAndRoundingExamples");

		/**
		 * Helper binding method for function: inputRoundingMode. 
		 * @param objectValue
		 * @return the SourceModule.expr representing an application of inputRoundingMode
		 */
		public static final SourceModel.Expr inputRoundingMode(SourceModel.Expr objectValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inputRoundingMode), objectValue});
		}

		/**
		 * Name binding for function: inputRoundingMode.
		 * @see #inputRoundingMode(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName inputRoundingMode = 
			QualifiedName.make(
				CAL_Decimal_internal.MODULE_NAME, 
				"inputRoundingMode");

		/**
		 * Helper binding method for function: jDivideWithRounding. 
		 * @param decimal
		 * @param arg_2
		 * @param arg_3
		 * @return the SourceModule.expr representing an application of jDivideWithRounding
		 */
		public static final SourceModel.Expr jDivideWithRounding(SourceModel.Expr decimal, SourceModel.Expr arg_2, SourceModel.Expr arg_3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jDivideWithRounding), decimal, arg_2, arg_3});
		}

		/**
		 * @see #jDivideWithRounding(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param decimal
		 * @param arg_2
		 * @param arg_3
		 * @return the SourceModel.Expr representing an application of jDivideWithRounding
		 */
		public static final SourceModel.Expr jDivideWithRounding(SourceModel.Expr decimal, SourceModel.Expr arg_2, int arg_3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jDivideWithRounding), decimal, arg_2, SourceModel.Expr.makeIntValue(arg_3)});
		}

		/**
		 * Name binding for function: jDivideWithRounding.
		 * @see #jDivideWithRounding(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jDivideWithRounding = 
			QualifiedName.make(
				CAL_Decimal_internal.MODULE_NAME, 
				"jDivideWithRounding");

		/**
		 * Helper binding method for function: jDivideWithScaleAndRounding. 
		 * @param decimal
		 * @param arg_2
		 * @param arg_3
		 * @param arg_4
		 * @return the SourceModule.expr representing an application of jDivideWithScaleAndRounding
		 */
		public static final SourceModel.Expr jDivideWithScaleAndRounding(SourceModel.Expr decimal, SourceModel.Expr arg_2, SourceModel.Expr arg_3, SourceModel.Expr arg_4) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jDivideWithScaleAndRounding), decimal, arg_2, arg_3, arg_4});
		}

		/**
		 * @see #jDivideWithScaleAndRounding(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param decimal
		 * @param arg_2
		 * @param arg_3
		 * @param arg_4
		 * @return the SourceModel.Expr representing an application of jDivideWithScaleAndRounding
		 */
		public static final SourceModel.Expr jDivideWithScaleAndRounding(SourceModel.Expr decimal, SourceModel.Expr arg_2, int arg_3, int arg_4) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jDivideWithScaleAndRounding), decimal, arg_2, SourceModel.Expr.makeIntValue(arg_3), SourceModel.Expr.makeIntValue(arg_4)});
		}

		/**
		 * Name binding for function: jDivideWithScaleAndRounding.
		 * @see #jDivideWithScaleAndRounding(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jDivideWithScaleAndRounding = 
			QualifiedName.make(
				CAL_Decimal_internal.MODULE_NAME, 
				"jDivideWithScaleAndRounding");

		/**
		 * Helper binding method for function: jSetScaleWithRounding. 
		 * @param decimal
		 * @param arg_2
		 * @param arg_3
		 * @return the SourceModule.expr representing an application of jSetScaleWithRounding
		 */
		public static final SourceModel.Expr jSetScaleWithRounding(SourceModel.Expr decimal, SourceModel.Expr arg_2, SourceModel.Expr arg_3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jSetScaleWithRounding), decimal, arg_2, arg_3});
		}

		/**
		 * @see #jSetScaleWithRounding(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param decimal
		 * @param arg_2
		 * @param arg_3
		 * @return the SourceModel.Expr representing an application of jSetScaleWithRounding
		 */
		public static final SourceModel.Expr jSetScaleWithRounding(SourceModel.Expr decimal, int arg_2, int arg_3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jSetScaleWithRounding), decimal, SourceModel.Expr.makeIntValue(arg_2), SourceModel.Expr.makeIntValue(arg_3)});
		}

		/**
		 * Name binding for function: jSetScaleWithRounding.
		 * @see #jSetScaleWithRounding(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jSetScaleWithRounding = 
			QualifiedName.make(
				CAL_Decimal_internal.MODULE_NAME, 
				"jSetScaleWithRounding");

		/**
		 * Helper binding method for function: outputRoundingMode. 
		 * @param roundingMode
		 * @return the SourceModule.expr representing an application of outputRoundingMode
		 */
		public static final SourceModel.Expr outputRoundingMode(SourceModel.Expr roundingMode) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputRoundingMode), roundingMode});
		}

		/**
		 * Name binding for function: outputRoundingMode.
		 * @see #outputRoundingMode(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName outputRoundingMode = 
			QualifiedName.make(
				CAL_Decimal_internal.MODULE_NAME, 
				"outputRoundingMode");

		/**
		 * Helper binding method for function: roundingModeExamples. 
		 * @return the SourceModule.expr representing an application of roundingModeExamples
		 */
		public static final SourceModel.Expr roundingModeExamples() {
			return SourceModel.Expr.Var.make(Functions.roundingModeExamples);
		}

		/**
		 * Name binding for function: roundingModeExamples.
		 * @see #roundingModeExamples()
		 */
		public static final QualifiedName roundingModeExamples = 
			QualifiedName.make(
				CAL_Decimal_internal.MODULE_NAME, 
				"roundingModeExamples");

		/**
		 * Helper binding method for function: scaleExamples. 
		 * @return the SourceModule.expr representing an application of scaleExamples
		 */
		public static final SourceModel.Expr scaleExamples() {
			return SourceModel.Expr.Var.make(Functions.scaleExamples);
		}

		/**
		 * Name binding for function: scaleExamples.
		 * @see #scaleExamples()
		 */
		public static final QualifiedName scaleExamples = 
			QualifiedName.make(
				CAL_Decimal_internal.MODULE_NAME, 
				"scaleExamples");

		/**
		 * Helper binding method for function: setScaleWithRoundingExamples. 
		 * @return the SourceModule.expr representing an application of setScaleWithRoundingExamples
		 */
		public static final SourceModel.Expr setScaleWithRoundingExamples() {
			return 
				SourceModel.Expr.Var.make(
					Functions.setScaleWithRoundingExamples);
		}

		/**
		 * Name binding for function: setScaleWithRoundingExamples.
		 * @see #setScaleWithRoundingExamples()
		 */
		public static final QualifiedName setScaleWithRoundingExamples = 
			QualifiedName.make(
				CAL_Decimal_internal.MODULE_NAME, 
				"setScaleWithRoundingExamples");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = -1390883646;

}
