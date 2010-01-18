/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_Decimal.java)
 * was generated from CAL module: Cal.Utilities.Decimal.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Utilities.Decimal module from Java code.
 *  
 * Creation date: Fri Mar 16 13:11:56 PST 2007
 * --!>
 *  
 */

package org.openquark.cal.module.Cal.Utilities;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * Defines many useful functions for the <code>Cal.Core.Prelude.Decimal</code> type. Note that the <code>Cal.Core.Prelude.Decimal</code>
 * type itself is defined in the <code>Cal.Core.Prelude</code> module to make it available to Prelude functions.
 * @author James Wright
 */
public final class CAL_Decimal {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Utilities.Decimal");

	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Utilities.Decimal module.
	 */
	public static final class TypeConstructors {
		/**
		 * An enumeration representing the different rounding modes.  The standard
		 * arithmetic divide operator uses <code>Cal.Utilities.Decimal.ROUND_HALF_UP</code>.
		 */
		public static final QualifiedName RoundingMode = 
			QualifiedName.make(CAL_Decimal.MODULE_NAME, "RoundingMode");

	}
	/**
	 * This inner class (DataConstructors) contains constants
	 * and methods related to binding to CAL DataConstructors in the Cal.Utilities.Decimal module.
	 */
	public static final class DataConstructors {
		/*
		 * DataConstructors for the Cal.Utilities.Decimal.RoundingMode data type.
		 */

		/**
		 * Always round up (away from zero)
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr ROUND_UP() {
			return SourceModel.Expr.DataCons.make(DataConstructors.ROUND_UP);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.Decimal.ROUND_UP.
		 * @see #ROUND_UP()
		 */
		public static final QualifiedName ROUND_UP = 
			QualifiedName.make(CAL_Decimal.MODULE_NAME, "ROUND_UP");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.Decimal.ROUND_UP.
		 * @see #ROUND_UP()
		 */
		public static final int ROUND_UP_ordinal = 0;

		/**
		 * Always round down (toward zero)
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr ROUND_DOWN() {
			return SourceModel.Expr.DataCons.make(DataConstructors.ROUND_DOWN);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.Decimal.ROUND_DOWN.
		 * @see #ROUND_DOWN()
		 */
		public static final QualifiedName ROUND_DOWN = 
			QualifiedName.make(CAL_Decimal.MODULE_NAME, "ROUND_DOWN");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.Decimal.ROUND_DOWN.
		 * @see #ROUND_DOWN()
		 */
		public static final int ROUND_DOWN_ordinal = 1;

		/**
		 * Round towards positive infinity
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr ROUND_CEILING() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.ROUND_CEILING);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.Decimal.ROUND_CEILING.
		 * @see #ROUND_CEILING()
		 */
		public static final QualifiedName ROUND_CEILING = 
			QualifiedName.make(CAL_Decimal.MODULE_NAME, "ROUND_CEILING");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.Decimal.ROUND_CEILING.
		 * @see #ROUND_CEILING()
		 */
		public static final int ROUND_CEILING_ordinal = 2;

		/**
		 * Round toward negative infinity
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr ROUND_FLOOR() {
			return SourceModel.Expr.DataCons.make(DataConstructors.ROUND_FLOOR);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.Decimal.ROUND_FLOOR.
		 * @see #ROUND_FLOOR()
		 */
		public static final QualifiedName ROUND_FLOOR = 
			QualifiedName.make(CAL_Decimal.MODULE_NAME, "ROUND_FLOOR");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.Decimal.ROUND_FLOOR.
		 * @see #ROUND_FLOOR()
		 */
		public static final int ROUND_FLOOR_ordinal = 3;

		/**
		 * Round towards "nearest neighbor" unless both neighbors are equidistant, 
		 * in which case round up.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr ROUND_HALF_UP() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.ROUND_HALF_UP);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.Decimal.ROUND_HALF_UP.
		 * @see #ROUND_HALF_UP()
		 */
		public static final QualifiedName ROUND_HALF_UP = 
			QualifiedName.make(CAL_Decimal.MODULE_NAME, "ROUND_HALF_UP");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.Decimal.ROUND_HALF_UP.
		 * @see #ROUND_HALF_UP()
		 */
		public static final int ROUND_HALF_UP_ordinal = 4;

		/**
		 * Round towards "nearest neighbor" unless both neighbors are equidistant, 
		 * in which case round down.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr ROUND_HALF_DOWN() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.ROUND_HALF_DOWN);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.Decimal.ROUND_HALF_DOWN.
		 * @see #ROUND_HALF_DOWN()
		 */
		public static final QualifiedName ROUND_HALF_DOWN = 
			QualifiedName.make(CAL_Decimal.MODULE_NAME, "ROUND_HALF_DOWN");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.Decimal.ROUND_HALF_DOWN.
		 * @see #ROUND_HALF_DOWN()
		 */
		public static final int ROUND_HALF_DOWN_ordinal = 5;

		/**
		 * Round towards the "nearest neighbor" unless both neighbors are equidistant, 
		 * in which case, round towards the even neighbor.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr ROUND_HALF_EVEN() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.ROUND_HALF_EVEN);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.Decimal.ROUND_HALF_EVEN.
		 * @see #ROUND_HALF_EVEN()
		 */
		public static final QualifiedName ROUND_HALF_EVEN = 
			QualifiedName.make(CAL_Decimal.MODULE_NAME, "ROUND_HALF_EVEN");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.Decimal.ROUND_HALF_EVEN.
		 * @see #ROUND_HALF_EVEN()
		 */
		public static final int ROUND_HALF_EVEN_ordinal = 6;

		/**
		 * Indicate that rounding is unnecessary.  An error will be signalled if this
		 * rounding mode is signalled for an operation in which rounding is necessary.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr ROUND_UNNECESSARY() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.ROUND_UNNECESSARY);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.Decimal.ROUND_UNNECESSARY.
		 * @see #ROUND_UNNECESSARY()
		 */
		public static final QualifiedName ROUND_UNNECESSARY = 
			QualifiedName.make(CAL_Decimal.MODULE_NAME, "ROUND_UNNECESSARY");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.Decimal.ROUND_UNNECESSARY.
		 * @see #ROUND_UNNECESSARY()
		 */
		public static final int ROUND_UNNECESSARY_ordinal = 7;

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Utilities.Decimal module.
	 */
	public static final class Functions {
		/**
		 * A version of divide that allows the user to specify the rounding mode to
		 * use if rounding is required.
		 * @param roundingMode (CAL type: <code>Cal.Utilities.Decimal.RoundingMode</code>)
		 *          <code>RoundingMode</code> specifying how to do any required rounding.
		 * An error will be signalled if <code>Cal.Utilities.Decimal.ROUND_UNNECESSARY</code> is specified 
		 * for an operation that requires rounding.
		 * @param decimalValue1 (CAL type: <code>Cal.Core.Prelude.Decimal</code>)
		 *          <code>Decimal</code> value to divide
		 * @param decimalValue2 (CAL type: <code>Cal.Core.Prelude.Decimal</code>)
		 *          <code>Decimal</code> value to divide by
		 * @return (CAL type: <code>Cal.Core.Prelude.Decimal</code>) 
		 *          the result of dividing <code>decimalValue1</code> by <code>decimalValue2</code>.  Any rounding 
		 * required to fit the result into the result's scale will by done in 
		 * the specified rounding mode.
		 */
		public static final SourceModel.Expr divideWithRounding(SourceModel.Expr roundingMode, SourceModel.Expr decimalValue1, SourceModel.Expr decimalValue2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.divideWithRounding), roundingMode, decimalValue1, decimalValue2});
		}

		/**
		 * Name binding for function: divideWithRounding.
		 * @see #divideWithRounding(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName divideWithRounding = 
			QualifiedName.make(CAL_Decimal.MODULE_NAME, "divideWithRounding");

		/**
		 * A version of divide that allows the user to specify the scale of the result
		 * and the rounding mode to use if rounding is necessary.
		 * @param scale (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          Scale of the result
		 * @param roundingMode (CAL type: <code>Cal.Utilities.Decimal.RoundingMode</code>)
		 *          <code>RoundingMode</code> specifying how to do any required rounding.
		 * An error will be signalled if <code>Cal.Utilities.Decimal.ROUND_UNNECESSARY</code> is specified 
		 * for an operation that requires rounding.
		 * @param decimalValue1 (CAL type: <code>Cal.Core.Prelude.Decimal</code>)
		 *          <code>Decimal</code> value to divide
		 * @param decimalValue2 (CAL type: <code>Cal.Core.Prelude.Decimal</code>)
		 *          <code>Decimal</code> value to divide by
		 * @return (CAL type: <code>Cal.Core.Prelude.Decimal</code>) 
		 *          the result of dividing <code>decimalValue1</code> by <code>decimalValue2</code>.  This result will
		 * have the specified scale, and any rounding required to fit the result into
		 * the result's scale will by done in the specified rounding mode.
		 */
		public static final SourceModel.Expr divideWithScaleAndRounding(SourceModel.Expr scale, SourceModel.Expr roundingMode, SourceModel.Expr decimalValue1, SourceModel.Expr decimalValue2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.divideWithScaleAndRounding), scale, roundingMode, decimalValue1, decimalValue2});
		}

		/**
		 * @see #divideWithScaleAndRounding(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param scale
		 * @param roundingMode
		 * @param decimalValue1
		 * @param decimalValue2
		 * @return the SourceModel.Expr representing an application of divideWithScaleAndRounding
		 */
		public static final SourceModel.Expr divideWithScaleAndRounding(int scale, SourceModel.Expr roundingMode, SourceModel.Expr decimalValue1, SourceModel.Expr decimalValue2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.divideWithScaleAndRounding), SourceModel.Expr.makeIntValue(scale), roundingMode, decimalValue1, decimalValue2});
		}

		/**
		 * Name binding for function: divideWithScaleAndRounding.
		 * @see #divideWithScaleAndRounding(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName divideWithScaleAndRounding = 
			QualifiedName.make(
				CAL_Decimal.MODULE_NAME, 
				"divideWithScaleAndRounding");

		/**
		 * Converts a <code>Cal.Core.Prelude.Double</code> value to the equivalent <code>Cal.Core.Prelude.Decimal</code> value.
		 * An error is signalled if <code>doubleValue</code> is <code>Cal.Core.Prelude.notANumber</code>, <code>Cal.Core.Prelude.positiveInfinity</code>,
		 * or <code>Cal.Core.Prelude.negativeInfinity</code>.
		 * @param doubleValue (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 *          <code>Double</code> value to convert to a <code>Decimal</code>.  This should
		 * not be <code>Cal.Core.Prelude.notANumber</code>, <code>Cal.Core.Prelude.positiveInfinity</code>, or <code>Cal.Core.Prelude.negativeInfinity</code>.
		 * @return (CAL type: <code>Cal.Core.Prelude.Decimal</code>) 
		 *          the equivalent <code>Decimal</code> value to <code>doubleToDouble</code>
		 */
		public static final SourceModel.Expr fromDouble(SourceModel.Expr doubleValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromDouble), doubleValue});
		}

		/**
		 * @see #fromDouble(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param doubleValue
		 * @return the SourceModel.Expr representing an application of fromDouble
		 */
		public static final SourceModel.Expr fromDouble(double doubleValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromDouble), SourceModel.Expr.makeDoubleValue(doubleValue)});
		}

		/**
		 * Name binding for function: fromDouble.
		 * @see #fromDouble(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fromDouble = 
			QualifiedName.make(CAL_Decimal.MODULE_NAME, "fromDouble");

		/**
		 * Parses a string (eg. <code>"34.33"</code>, <code>"1.0e50000"</code>) into a <code>Cal.Core.Prelude.Decimal</code>.
		 * An error (specifically, a NumberFormatException) will be signalled for invalid strings.
		 * @param stringValue (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          string to parse into a <code>Decimal</code>
		 * @return (CAL type: <code>Cal.Core.Prelude.Decimal</code>) 
		 *          <code>Decimal</code> value represented by <code>stringValue</code>
		 */
		public static final SourceModel.Expr fromString(SourceModel.Expr stringValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromString), stringValue});
		}

		/**
		 * @see #fromString(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param stringValue
		 * @return the SourceModel.Expr representing an application of fromString
		 */
		public static final SourceModel.Expr fromString(java.lang.String stringValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromString), SourceModel.Expr.makeStringValue(stringValue)});
		}

		/**
		 * Name binding for function: fromString.
		 * @see #fromString(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fromString = 
			QualifiedName.make(CAL_Decimal.MODULE_NAME, "fromString");

		/**
		 * 
		 * @param decimalValue (CAL type: <code>Cal.Core.Prelude.Decimal</code>)
		 *          <code>Decimal</code> value to return the scale for
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the scale (ie, number of digits after the decimal point) of the
		 * specified decimal value.
		 */
		public static final SourceModel.Expr scale(SourceModel.Expr decimalValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.scale), decimalValue});
		}

		/**
		 * Name binding for function: scale.
		 * @see #scale(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName scale = 
			QualifiedName.make(CAL_Decimal.MODULE_NAME, "scale");

		/**
		 * Returns a new <code>Cal.Core.Prelude.Decimal</code> value with the specified scale.
		 * <p>
		 * A <code>Decimal</code> value consists of an arbitrary-precision Integer value plus a
		 * 32-bit unsigned "scale", which represents the number of digits to the
		 * right of the decimal place.  So, for example:
		 * <ul>
		 *  <li>
		 *   -3.00 has a scale of 2
		 *  </li>
		 *  <li>
		 *   123.11 has a scale of 2
		 *  </li>
		 *  <li>
		 *   -1.0609 has a scale of 4
		 *  </li>
		 *  <li>
		 *   876134 has a scale of 0
		 *  </li>
		 * </ul>
		 * 
		 * @param decimalValue (CAL type: <code>Cal.Core.Prelude.Decimal</code>)
		 *          Value to duplicate with a new scale
		 * @param newScale (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          Scale for the new <code>Decimal</code> value
		 * @param roundingMode (CAL type: <code>Cal.Utilities.Decimal.RoundingMode</code>)
		 *          <code>RoundingMode</code> specifying how to do any required rounding.
		 * An error will be signalled if <code>Cal.Utilities.Decimal.ROUND_UNNECESSARY</code> is specified 
		 * for an operation that requires rounding.
		 * @return (CAL type: <code>Cal.Core.Prelude.Decimal</code>) 
		 *          A <code>Decimal</code> value that represents <code>decimalValue</code> with the specified scale.
		 * The value will be rounded according to the specified mode if the new
		 * scale is smaller than the old one.
		 */
		public static final SourceModel.Expr setScaleWithRounding(SourceModel.Expr decimalValue, SourceModel.Expr newScale, SourceModel.Expr roundingMode) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.setScaleWithRounding), decimalValue, newScale, roundingMode});
		}

		/**
		 * @see #setScaleWithRounding(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param decimalValue
		 * @param newScale
		 * @param roundingMode
		 * @return the SourceModel.Expr representing an application of setScaleWithRounding
		 */
		public static final SourceModel.Expr setScaleWithRounding(SourceModel.Expr decimalValue, int newScale, SourceModel.Expr roundingMode) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.setScaleWithRounding), decimalValue, SourceModel.Expr.makeIntValue(newScale), roundingMode});
		}

		/**
		 * Name binding for function: setScaleWithRounding.
		 * @see #setScaleWithRounding(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName setScaleWithRounding = 
			QualifiedName.make(CAL_Decimal.MODULE_NAME, "setScaleWithRounding");

		/**
		 * Helper binding method for function: testModule. 
		 * @return the SourceModule.expr representing an application of testModule
		 */
		public static final SourceModel.Expr testModule() {
			return SourceModel.Expr.Var.make(Functions.testModule);
		}

		/**
		 * Name binding for function: testModule.
		 * @see #testModule()
		 */
		public static final QualifiedName testModule = 
			QualifiedName.make(CAL_Decimal.MODULE_NAME, "testModule");

		/**
		 * Converts a <code>Cal.Core.Prelude.Decimal</code> value to a string representation.
		 * @param decimalValue (CAL type: <code>Cal.Core.Prelude.Decimal</code>)
		 *          <code>Decimal</code> value to convert
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the string representation of a <code>Decimal</code> value
		 */
		public static final SourceModel.Expr toString(SourceModel.Expr decimalValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toString), decimalValue});
		}

		/**
		 * Name binding for function: toString.
		 * @see #toString(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName toString = 
			QualifiedName.make(CAL_Decimal.MODULE_NAME, "toString");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = -1863325781;

}
