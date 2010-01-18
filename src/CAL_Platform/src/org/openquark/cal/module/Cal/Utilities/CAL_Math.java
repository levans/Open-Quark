/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_Math.java)
 * was generated from CAL module: Cal.Utilities.Math.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Utilities.Math module from Java code.
 *  
 * Creation date: Wed Jul 25 10:55:05 PDT 2007
 * --!>
 *  
 */

package org.openquark.cal.module.Cal.Utilities;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * Defines many useful math functions. For example, trigonometric, logarithmic and exponential functions.
 * @author Bo Ilic
 */
public final class CAL_Math {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Utilities.Math");

	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Utilities.Math module.
	 */
	public static final class Functions {
		/**
		 * Returns the arc cosine of an angle, in the range of 0.0 through <em>pi</em>.
		 * <p>
		 * Special cases:
		 * <ul>
		 *  <li>
		 *   if the argument is <code>Cal.Core.Prelude.notANumber</code> or its absolute value is greater than 1, then the result is <code>Cal.Core.Prelude.notANumber</code>.
		 *  </li>
		 * </ul>
		 * 
		 * @param x (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 *          the value whose arc cosine is to be returned.
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 *          the arc cosine of the argument.
		 */
		public static final SourceModel.Expr acos(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.acos), x});
		}

		/**
		 * @see #acos(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @return the SourceModel.Expr representing an application of acos
		 */
		public static final SourceModel.Expr acos(double x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.acos), SourceModel.Expr.makeDoubleValue(x)});
		}

		/**
		 * Name binding for function: acos.
		 * @see #acos(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName acos = 
			QualifiedName.make(CAL_Math.MODULE_NAME, "acos");

		/**
		 * Returns the inverse hyperbolic cosine of the specified <code>Cal.Core.Prelude.Double</code> value.
		 * @param x (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 *          the number whose inverse hyperbolic cosine is to be returned.
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 *          the inverse hyperbolic cosine of the argument.
		 */
		public static final SourceModel.Expr acosh(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.acosh), x});
		}

		/**
		 * @see #acosh(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @return the SourceModel.Expr representing an application of acosh
		 */
		public static final SourceModel.Expr acosh(double x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.acosh), SourceModel.Expr.makeDoubleValue(x)});
		}

		/**
		 * Name binding for function: acosh.
		 * @see #acosh(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName acosh = 
			QualifiedName.make(CAL_Math.MODULE_NAME, "acosh");

		/**
		 * Returns the arc sine of an angle, in the range of <em>-pi/2</em> through <em>pi/2</em>.
		 * <p>
		 * Special cases:
		 * <ul>
		 *  <li>
		 *   if the argument is <code>Cal.Core.Prelude.notANumber</code> or its absolute value is greater than 1, then the result is <code>Cal.Core.Prelude.notANumber</code>.
		 *  </li>
		 *  <li>
		 *   if the argument is zero, then the result is a zero with the same sign as the argument.
		 *  </li>
		 * </ul>
		 * 
		 * @param x (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 *          the value whose arc sine is to be returned.
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 *          the arc sine of the argument.
		 */
		public static final SourceModel.Expr asin(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.asin), x});
		}

		/**
		 * @see #asin(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @return the SourceModel.Expr representing an application of asin
		 */
		public static final SourceModel.Expr asin(double x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.asin), SourceModel.Expr.makeDoubleValue(x)});
		}

		/**
		 * Name binding for function: asin.
		 * @see #asin(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName asin = 
			QualifiedName.make(CAL_Math.MODULE_NAME, "asin");

		/**
		 * Returns the inverse hyperbolic sine of the specified <code>Cal.Core.Prelude.Double</code> value.
		 * @param x (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 *          the number whose inverse hyperbolic sine is to be returned.
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 *          the inverse hyperbolic sine of the argument.
		 */
		public static final SourceModel.Expr asinh(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.asinh), x});
		}

		/**
		 * @see #asinh(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @return the SourceModel.Expr representing an application of asinh
		 */
		public static final SourceModel.Expr asinh(double x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.asinh), SourceModel.Expr.makeDoubleValue(x)});
		}

		/**
		 * Name binding for function: asinh.
		 * @see #asinh(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName asinh = 
			QualifiedName.make(CAL_Math.MODULE_NAME, "asinh");

		/**
		 * Returns the arc tangent of an angle, in the range of <em>-pi/2</em> through <em>pi/2</em>.
		 * <p>
		 * Special cases:
		 * <ul>
		 *  <li>
		 *   if the argument is <code>Cal.Core.Prelude.notANumber</code>, then the result is <code>Cal.Core.Prelude.notANumber</code>.
		 *  </li>
		 *  <li>
		 *   if the argument is zero, then the result is a zero with the same sign as the argument.
		 *  </li>
		 * </ul>
		 * 
		 * @param x (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 *          the value whose arc tangent is to be returned.
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 *          the arc tangent of the argument.
		 */
		public static final SourceModel.Expr atan(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.atan), x});
		}

		/**
		 * @see #atan(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @return the SourceModel.Expr representing an application of atan
		 */
		public static final SourceModel.Expr atan(double x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.atan), SourceModel.Expr.makeDoubleValue(x)});
		}

		/**
		 * Name binding for function: atan.
		 * @see #atan(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName atan = 
			QualifiedName.make(CAL_Math.MODULE_NAME, "atan");

		/**
		 * Converts rectangular coordinates (x, y) to polar (r, theta). This function computes the
		 * phase theta by computing an arc tangent of y/x in the range of <em>-pi</em> to <em>pi</em>.
		 * @param y (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 *          the ordinate coordinate
		 * @param x (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 *          the abscissa coordinate
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 *          the theta component of the point (r, theta) in polar coordinates
		 * that corresponds to the point (x, y) in Cartesian coordinates.
		 */
		public static final SourceModel.Expr atan2(SourceModel.Expr y, SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.atan2), y, x});
		}

		/**
		 * @see #atan2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param y
		 * @param x
		 * @return the SourceModel.Expr representing an application of atan2
		 */
		public static final SourceModel.Expr atan2(double y, double x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.atan2), SourceModel.Expr.makeDoubleValue(y), SourceModel.Expr.makeDoubleValue(x)});
		}

		/**
		 * Name binding for function: atan2.
		 * @see #atan2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName atan2 = 
			QualifiedName.make(CAL_Math.MODULE_NAME, "atan2");

		/**
		 * Returns the inverse hyperbolic tangent of the specified <code>Cal.Core.Prelude.Double</code> value.
		 * @param x (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 *          the number whose inverse hyperbolic tangent is to be returned.
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 *          the inverse hyperbolic tangent of the argument.
		 */
		public static final SourceModel.Expr atanh(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.atanh), x});
		}

		/**
		 * @see #atanh(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @return the SourceModel.Expr representing an application of atanh
		 */
		public static final SourceModel.Expr atanh(double x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.atanh), SourceModel.Expr.makeDoubleValue(x)});
		}

		/**
		 * Name binding for function: atanh.
		 * @see #atanh(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName atanh = 
			QualifiedName.make(CAL_Math.MODULE_NAME, "atanh");

		/**
		 * Returns the smallest integral value that is greater than or equal to the argument.
		 * <p>
		 * Special cases:
		 * <ul>
		 *  <li>
		 *   if the argument is <code>Cal.Core.Prelude.notANumber</code>, <code>Cal.Core.Prelude.positiveInfinity</code>, <code>Cal.Core.Prelude.negativeInfinity</code>,
		 *   positive zero or negative zero, then the result is the same as the argument.
		 *  </li>
		 *  <li>
		 *   if the argument value is less than zero but greater than -1.0, then the result is negative zero.
		 *  </li>
		 * </ul>
		 * 
		 * @param x (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 *          the number whose ceiling is to be returned.
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 *          the smallest integral <code>Double</code> value greater than or equal to <code>x</code>.
		 */
		public static final SourceModel.Expr ceiling(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.ceiling), x});
		}

		/**
		 * @see #ceiling(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @return the SourceModel.Expr representing an application of ceiling
		 */
		public static final SourceModel.Expr ceiling(double x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.ceiling), SourceModel.Expr.makeDoubleValue(x)});
		}

		/**
		 * Name binding for function: ceiling.
		 * @see #ceiling(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName ceiling = 
			QualifiedName.make(CAL_Math.MODULE_NAME, "ceiling");

		/**
		 * Returns the trigonometric cosine of an angle.
		 * <p>
		 * Special cases:
		 * <ul>
		 *  <li>
		 *   if the argument is <code>Cal.Core.Prelude.notANumber</code>, <code>Cal.Core.Prelude.positiveInfinity</code> or <code>Cal.Core.Prelude.negativeInfinity</code>,
		 *   then the result is <code>Cal.Core.Prelude.notANumber</code>.
		 *  </li>
		 * </ul>
		 * 
		 * @param x (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 *          the angle, in radians.
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 *          the cosine of the argument.
		 */
		public static final SourceModel.Expr cos(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.cos), x});
		}

		/**
		 * @see #cos(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @return the SourceModel.Expr representing an application of cos
		 */
		public static final SourceModel.Expr cos(double x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.cos), SourceModel.Expr.makeDoubleValue(x)});
		}

		/**
		 * Name binding for function: cos.
		 * @see #cos(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName cos = 
			QualifiedName.make(CAL_Math.MODULE_NAME, "cos");

		/**
		 * Returns the hyperbolic cosine of the specified <code>Cal.Core.Prelude.Double</code> value. The hyperbolic cosine of x is defined to be
		 * (e<sup>x</sup> + e<sup>-x</sup>)/2 where <em>e</em> is Euler's number.
		 * @param x (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 *          the number whose hyperbolic cosine is to be returned.
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 *          the hyperbolic cosine of the argument.
		 */
		public static final SourceModel.Expr cosh(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.cosh), x});
		}

		/**
		 * @see #cosh(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @return the SourceModel.Expr representing an application of cosh
		 */
		public static final SourceModel.Expr cosh(double x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.cosh), SourceModel.Expr.makeDoubleValue(x)});
		}

		/**
		 * Name binding for function: cosh.
		 * @see #cosh(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName cosh = 
			QualifiedName.make(CAL_Math.MODULE_NAME, "cosh");

		/**
		 * Converts an angle measure in degrees to an angle measure in radians.
		 * @param angleInDegrees (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 *          angle measure in degrees.
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 *          angle measure in radians.
		 */
		public static final SourceModel.Expr degreesToRadians(SourceModel.Expr angleInDegrees) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.degreesToRadians), angleInDegrees});
		}

		/**
		 * @see #degreesToRadians(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param angleInDegrees
		 * @return the SourceModel.Expr representing an application of degreesToRadians
		 */
		public static final SourceModel.Expr degreesToRadians(double angleInDegrees) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.degreesToRadians), SourceModel.Expr.makeDoubleValue(angleInDegrees)});
		}

		/**
		 * Name binding for function: degreesToRadians.
		 * @see #degreesToRadians(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName degreesToRadians = 
			QualifiedName.make(CAL_Math.MODULE_NAME, "degreesToRadians");

		/**
		 * The <code>Cal.Core.Prelude.Double</code> value that is closer than any other to <em>e</em>, the base of the natural
		 * logarithm.
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 */
		public static final SourceModel.Expr e() {
			return SourceModel.Expr.Var.make(Functions.e);
		}

		/**
		 * Name binding for function: e.
		 * @see #e()
		 */
		public static final QualifiedName e = 
			QualifiedName.make(CAL_Math.MODULE_NAME, "e");

		/**
		 * Returns Euler's number <em>e</em> raised to the power of the specified <code>Cal.Core.Prelude.Double</code> value.
		 * <p>
		 * Special cases:
		 * <ul>
		 *  <li>
		 *   if the argument is <code>Cal.Core.Prelude.notANumber</code>, the result is <code>Cal.Core.Prelude.notANumber</code>.
		 *  </li>
		 *  <li>
		 *   if the argument is <code>Cal.Core.Prelude.positiveInfinity</code>, then the result is <code>Cal.Core.Prelude.positiveInfinity</code>.
		 *  </li>
		 *  <li>
		 *   if the argument is <code>Cal.Core.Prelude.negativeInfinity</code>, then the result is positive zero.
		 *  </li>
		 * </ul>
		 * 
		 * @param x (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 *          the exponent to raise <em>e</em> to.
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 *          the value <em>e</em><sup><code>x</code></sup>.
		 */
		public static final SourceModel.Expr exp(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.exp), x});
		}

		/**
		 * @see #exp(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @return the SourceModel.Expr representing an application of exp
		 */
		public static final SourceModel.Expr exp(double x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.exp), SourceModel.Expr.makeDoubleValue(x)});
		}

		/**
		 * Name binding for function: exp.
		 * @see #exp(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName exp = 
			QualifiedName.make(CAL_Math.MODULE_NAME, "exp");

		/**
		 * Returns the largest integral value that is less than or equal to the argument.
		 * <p>
		 * Special cases:
		 * <ul>
		 *  <li>
		 *   if the argument is <code>Cal.Core.Prelude.notANumber</code>, positive zero or negative zero, then the result is the same
		 *   as the argument.
		 *  </li>
		 * </ul>
		 * 
		 * @param x (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 *          the number whose floor is to be returned.
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 *          the largest integral <code>Double</code> value less than or equal to <code>x</code>.
		 */
		public static final SourceModel.Expr floor(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floor), x});
		}

		/**
		 * @see #floor(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @return the SourceModel.Expr representing an application of floor
		 */
		public static final SourceModel.Expr floor(double x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floor), SourceModel.Expr.makeDoubleValue(x)});
		}

		/**
		 * Name binding for function: floor.
		 * @see #floor(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName floor = 
			QualifiedName.make(CAL_Math.MODULE_NAME, "floor");

		/**
		 * Computes the fractional part of a <code>Cal.Core.Prelude.Double</code> value.
		 * <p>
		 * Note that, for finite values of <code>x</code>:
		 * 
		 * <pre>   (Cal.Utilities.Math.truncate x) + (fractionalPart x) == x</pre>
		 * 
		 * 
		 * @param value (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 *          the value whose fractional part is to be returned.
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 *          the fractional part of <code>value</code>.
		 */
		public static final SourceModel.Expr fractionalPart(SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fractionalPart), value});
		}

		/**
		 * @see #fractionalPart(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value
		 * @return the SourceModel.Expr representing an application of fractionalPart
		 */
		public static final SourceModel.Expr fractionalPart(double value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fractionalPart), SourceModel.Expr.makeDoubleValue(value)});
		}

		/**
		 * Name binding for function: fractionalPart.
		 * @see #fractionalPart(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fractionalPart = 
			QualifiedName.make(CAL_Math.MODULE_NAME, "fractionalPart");

		/**
		 * Computes the remainder from a rounding division as prescribed by the IEEE 754 standard.
		 * <p>
		 * <code>ieeeRemainder x y</code> returns a value equal to <code>x - y*(Cal.Utilities.Math.round (x/y))</code>.
		 * <p>
		 * Special cases:
		 * <ul>
		 *  <li>
		 *   if either argument is <code>Cal.Core.Prelude.notANumber</code>, or the first argument is infinite, or
		 *   the second argument is positive zero or negative zero, then the result is
		 *   <code>Cal.Core.Prelude.notANumber</code>.
		 *  </li>
		 *  <li>
		 *   if the first argument is finite and the second argument is infinite, then the
		 *   result is the same as the first argument.
		 *  </li>
		 * </ul>
		 * 
		 * @param x (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 *          the dividend.
		 * @param y (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 *          the divisor.
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 *          the remainder when <code>x</code> is divided by <code>y</code> by a rounding division.
		 */
		public static final SourceModel.Expr ieeeRemainder(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.ieeeRemainder), x, y});
		}

		/**
		 * @see #ieeeRemainder(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @param y
		 * @return the SourceModel.Expr representing an application of ieeeRemainder
		 */
		public static final SourceModel.Expr ieeeRemainder(double x, double y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.ieeeRemainder), SourceModel.Expr.makeDoubleValue(x), SourceModel.Expr.makeDoubleValue(y)});
		}

		/**
		 * Name binding for function: ieeeRemainder.
		 * @see #ieeeRemainder(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName ieeeRemainder = 
			QualifiedName.make(CAL_Math.MODULE_NAME, "ieeeRemainder");

		/**
		 * Returns the natural logarithm (base <em>e</em>) of the specified <code>Cal.Core.Prelude.Double</code> value.
		 * <p>
		 * Special cases:
		 * <ul>
		 *  <li>
		 *   if the argument is <code>Cal.Core.Prelude.notANumber</code> or less than zero, then the result is <code>Cal.Core.Prelude.notANumber</code>.
		 *  </li>
		 *  <li>
		 *   if the argument is <code>Cal.Core.Prelude.positiveInfinity</code>, then the result is <code>Cal.Core.Prelude.positiveInfinity</code>.
		 *  </li>
		 *  <li>
		 *   if the argument is positive zero or negative zero, then the result is <code>Cal.Core.Prelude.negativeInfinity</code>.
		 *  </li>
		 * </ul>
		 * 
		 * @param x (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 *          the value whose natural logarithm is to be returned.
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 *          the value <strong>ln</strong> <code>x</code>, the natural logarithm of <code>x</code>.
		 */
		public static final SourceModel.Expr log(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.log), x});
		}

		/**
		 * @see #log(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @return the SourceModel.Expr representing an application of log
		 */
		public static final SourceModel.Expr log(double x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.log), SourceModel.Expr.makeDoubleValue(x)});
		}

		/**
		 * Name binding for function: log.
		 * @see #log(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName log = 
			QualifiedName.make(CAL_Math.MODULE_NAME, "log");

		/**
		 * Returns the base 10 logarithm of the specified <code>Cal.Core.Prelude.Double</code> value.
		 * @param x (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 *          the value whose base 10 logarithm is to be returned.
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 *          the base 10 logarithm of <code>x</code>.
		 */
		public static final SourceModel.Expr log10(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.log10), x});
		}

		/**
		 * @see #log10(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @return the SourceModel.Expr representing an application of log10
		 */
		public static final SourceModel.Expr log10(double x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.log10), SourceModel.Expr.makeDoubleValue(x)});
		}

		/**
		 * Name binding for function: log10.
		 * @see #log10(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName log10 = 
			QualifiedName.make(CAL_Math.MODULE_NAME, "log10");

		/**
		 * Returns the logarithm of the first argument in the base of the second argument.
		 * @param x (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 *          the value whose logarithm is to be returned.
		 * @param base (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 *          the base of the logarithm.
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 *          (<strong>ln</strong> <code>x</code>) / (<strong>ln</strong> <code>base</code>).
		 */
		public static final SourceModel.Expr logBase(SourceModel.Expr x, SourceModel.Expr base) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.logBase), x, base});
		}

		/**
		 * @see #logBase(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @param base
		 * @return the SourceModel.Expr representing an application of logBase
		 */
		public static final SourceModel.Expr logBase(double x, double base) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.logBase), SourceModel.Expr.makeDoubleValue(x), SourceModel.Expr.makeDoubleValue(base)});
		}

		/**
		 * Name binding for function: logBase.
		 * @see #logBase(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName logBase = 
			QualifiedName.make(CAL_Math.MODULE_NAME, "logBase");

		/**
		 * The <code>Cal.Core.Prelude.Double</code> value that is closer than any other to <em>pi</em>, the mathematical constant
		 * representing the ratio of a circle's circumference to its diameter.
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 */
		public static final SourceModel.Expr pi() {
			return SourceModel.Expr.Var.make(Functions.pi);
		}

		/**
		 * Name binding for function: pi.
		 * @see #pi()
		 */
		public static final QualifiedName pi = 
			QualifiedName.make(CAL_Math.MODULE_NAME, "pi");

		/**
		 * Returns the result of raising the first argument to the power specified by the second argument.
		 * @param base (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 *          the <code>Double</code> value whose power is to be taken.
		 * @param exponent (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 *          an <code>Double</code> value specifying the exponent in the exponentiation.
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 *          <code>base</code><sup><code>exponent</code></sup>.
		 */
		public static final SourceModel.Expr power(SourceModel.Expr base, SourceModel.Expr exponent) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.power), base, exponent});
		}

		/**
		 * @see #power(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param base
		 * @param exponent
		 * @return the SourceModel.Expr representing an application of power
		 */
		public static final SourceModel.Expr power(double base, double exponent) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.power), SourceModel.Expr.makeDoubleValue(base), SourceModel.Expr.makeDoubleValue(exponent)});
		}

		/**
		 * Name binding for function: power.
		 * @see #power(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName power = 
			QualifiedName.make(CAL_Math.MODULE_NAME, "power");

		/**
		 * Converts an angle measure in radians to an angle measure in degrees.
		 * @param angleInRadians (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 *          angle measure in radians.
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 *          angle measure in radians.
		 */
		public static final SourceModel.Expr radiansToDegrees(SourceModel.Expr angleInRadians) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.radiansToDegrees), angleInRadians});
		}

		/**
		 * @see #radiansToDegrees(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param angleInRadians
		 * @return the SourceModel.Expr representing an application of radiansToDegrees
		 */
		public static final SourceModel.Expr radiansToDegrees(double angleInRadians) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.radiansToDegrees), SourceModel.Expr.makeDoubleValue(angleInRadians)});
		}

		/**
		 * Name binding for function: radiansToDegrees.
		 * @see #radiansToDegrees(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName radiansToDegrees = 
			QualifiedName.make(CAL_Math.MODULE_NAME, "radiansToDegrees");

		/**
		 * Returns the integral value that is closest in value to the argument. If there is a tie between
		 * two values that are equally close to the argument, then the returned result is the <em>even</em> value.
		 * <p>
		 * Special cases:
		 * <ul>
		 *  <li>
		 *   if the argument is <code>Cal.Core.Prelude.notANumber</code>, positive zero or negative zero, then the result is the same
		 *   as the argument.
		 *  </li>
		 * </ul>
		 * 
		 * @param x (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 *          the number to be rounded.
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 *          the integral <code>Double</code> value closest to <code>x</code>.
		 */
		public static final SourceModel.Expr round(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.round), x});
		}

		/**
		 * @see #round(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @return the SourceModel.Expr representing an application of round
		 */
		public static final SourceModel.Expr round(double x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.round), SourceModel.Expr.makeDoubleValue(x)});
		}

		/**
		 * Name binding for function: round.
		 * @see #round(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName round = 
			QualifiedName.make(CAL_Math.MODULE_NAME, "round");

		/**
		 * Returns the <code>Cal.Core.Prelude.Long</code> value that is closest in value to the argument, calculated as <em>floor(x + 0.5)</em>.
		 * <p>
		 * Special cases:
		 * <ul>
		 *  <li>
		 *   if the argument is <code>Cal.Core.Prelude.notANumber</code>, the result is 0.
		 *  </li>
		 *  <li>
		 *   if the argument is <code>Cal.Core.Prelude.negativeInfinity</code> or has a value less than or equal to
		 *   <code>Cal.Core.Prelude.minBound :: Cal.Core.Prelude.Long</code>, the result is
		 *   <code>Cal.Core.Prelude.minBound :: Cal.Core.Prelude.Long</code>.
		 *  </li>
		 *  <li>
		 *   if the argument is <code>Cal.Core.Prelude.positiveInfinity</code> or has a value greater than or equal to
		 *   <code>Cal.Core.Prelude.maxBound :: Cal.Core.Prelude.Long</code>, the result is
		 *   <code>Cal.Core.Prelude.maxBound :: Cal.Core.Prelude.Long</code>.
		 *  </li>
		 * </ul>
		 * 
		 * @param x (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 *          the number to be rounded.
		 * @return (CAL type: <code>Cal.Core.Prelude.Long</code>) 
		 *          the <code>Long</code> value closest to <code>x</code>.
		 */
		public static final SourceModel.Expr roundDoubleToLong(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.roundDoubleToLong), x});
		}

		/**
		 * @see #roundDoubleToLong(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @return the SourceModel.Expr representing an application of roundDoubleToLong
		 */
		public static final SourceModel.Expr roundDoubleToLong(double x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.roundDoubleToLong), SourceModel.Expr.makeDoubleValue(x)});
		}

		/**
		 * Name binding for function: roundDoubleToLong.
		 * @see #roundDoubleToLong(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName roundDoubleToLong = 
			QualifiedName.make(CAL_Math.MODULE_NAME, "roundDoubleToLong");

		/**
		 * Returns the <code>Cal.Core.Prelude.Int</code> value that is closest in value to the argument, calculated as <em>floor(x + 0.5)</em>.
		 * <p>
		 * Special cases:
		 * <ul>
		 *  <li>
		 *   if the argument is <code>Cal.Core.Prelude.notANumber</code>, the result is 0.
		 *  </li>
		 *  <li>
		 *   if the argument is <code>Cal.Core.Prelude.negativeInfinity</code> or has a value less than or equal to
		 *   <code>Cal.Core.Prelude.minBound :: Cal.Core.Prelude.Int</code>, the result is
		 *   <code>Cal.Core.Prelude.minBound :: Cal.Core.Prelude.Int</code>.
		 *  </li>
		 *  <li>
		 *   if the argument is <code>Cal.Core.Prelude.positiveInfinity</code> or has a value greater than or equal to
		 *   <code>Cal.Core.Prelude.maxBound :: Cal.Core.Prelude.Int</code>, the result is
		 *   <code>Cal.Core.Prelude.maxBound :: Cal.Core.Prelude.Int</code>.
		 *  </li>
		 * </ul>
		 * 
		 * @param x (CAL type: <code>Cal.Core.Prelude.Float</code>)
		 *          the number to be rounded.
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the <code>Long</code> value closest to <code>x</code>.
		 */
		public static final SourceModel.Expr roundFloatToInt(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.roundFloatToInt), x});
		}

		/**
		 * @see #roundFloatToInt(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @return the SourceModel.Expr representing an application of roundFloatToInt
		 */
		public static final SourceModel.Expr roundFloatToInt(float x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.roundFloatToInt), SourceModel.Expr.makeFloatValue(x)});
		}

		/**
		 * Name binding for function: roundFloatToInt.
		 * @see #roundFloatToInt(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName roundFloatToInt = 
			QualifiedName.make(CAL_Math.MODULE_NAME, "roundFloatToInt");

		/**
		 * Rounds a number to N decimal places.
		 * @param val (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 *          the value to be rounded.
		 * @param nDecimalPlaces (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the number of decimal places to keep.
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 *          <code>val</code>, rounded to N decimal places.
		 */
		public static final SourceModel.Expr roundToNPlaces(SourceModel.Expr val, SourceModel.Expr nDecimalPlaces) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.roundToNPlaces), val, nDecimalPlaces});
		}

		/**
		 * @see #roundToNPlaces(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param val
		 * @param nDecimalPlaces
		 * @return the SourceModel.Expr representing an application of roundToNPlaces
		 */
		public static final SourceModel.Expr roundToNPlaces(double val, int nDecimalPlaces) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.roundToNPlaces), SourceModel.Expr.makeDoubleValue(val), SourceModel.Expr.makeIntValue(nDecimalPlaces)});
		}

		/**
		 * Name binding for function: roundToNPlaces.
		 * @see #roundToNPlaces(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName roundToNPlaces = 
			QualifiedName.make(CAL_Math.MODULE_NAME, "roundToNPlaces");

		/**
		 * Returns the trigonometric sine of an angle.
		 * <p>
		 * Special cases:
		 * <ul>
		 *  <li>
		 *   if the argument is <code>Cal.Core.Prelude.notANumber</code>, <code>Cal.Core.Prelude.positiveInfinity</code> or <code>Cal.Core.Prelude.negativeInfinity</code>,
		 *   then the result is <code>Cal.Core.Prelude.notANumber</code>.
		 *  </li>
		 *  <li>
		 *   if the argument is zero, then the result is a zero with the same sign as the argument.
		 *  </li>
		 * </ul>
		 * 
		 * @param x (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 *          the angle, in radians.
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 *          the sine of the argument.
		 */
		public static final SourceModel.Expr sin(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.sin), x});
		}

		/**
		 * @see #sin(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @return the SourceModel.Expr representing an application of sin
		 */
		public static final SourceModel.Expr sin(double x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.sin), SourceModel.Expr.makeDoubleValue(x)});
		}

		/**
		 * Name binding for function: sin.
		 * @see #sin(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName sin = 
			QualifiedName.make(CAL_Math.MODULE_NAME, "sin");

		/**
		 * Returns the hyperbolic sine of the specified <code>Cal.Core.Prelude.Double</code> value. The hyperbolic sine of x is defined to be
		 * (e<sup>x</sup> - e<sup>-x</sup>)/2 where <em>e</em> is Euler's number.
		 * @param x (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 *          the number whose hyperbolic sine is to be returned.
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 *          the hyperbolic sine of the argument.
		 */
		public static final SourceModel.Expr sinh(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.sinh), x});
		}

		/**
		 * @see #sinh(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @return the SourceModel.Expr representing an application of sinh
		 */
		public static final SourceModel.Expr sinh(double x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.sinh), SourceModel.Expr.makeDoubleValue(x)});
		}

		/**
		 * Name binding for function: sinh.
		 * @see #sinh(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName sinh = 
			QualifiedName.make(CAL_Math.MODULE_NAME, "sinh");

		/**
		 * Returns the positive square root of the specified <code>Cal.Core.Prelude.Double</code> value.
		 * <p>
		 * Special cases:
		 * <ul>
		 *  <li>
		 *   if the argument is <code>Cal.Core.Prelude.notANumber</code> or less than zero, then the result is <code>Cal.Core.Prelude.notANumber</code>.
		 *  </li>
		 *  <li>
		 *   if the argument is <code>Cal.Core.Prelude.positiveInfinity</code>, then the result is <code>Cal.Core.Prelude.positiveInfinity</code>.
		 *  </li>
		 *  <li>
		 *   if the argument is positive zero or negative zero, then the result is the same as the argument.
		 *  </li>
		 * </ul>
		 * 
		 * @param x (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 *          the value whose positive square root is to be returned.
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 *          the positive square root of <code>x</code>.
		 */
		public static final SourceModel.Expr sqrt(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.sqrt), x});
		}

		/**
		 * @see #sqrt(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @return the SourceModel.Expr representing an application of sqrt
		 */
		public static final SourceModel.Expr sqrt(double x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.sqrt), SourceModel.Expr.makeDoubleValue(x)});
		}

		/**
		 * Name binding for function: sqrt.
		 * @see #sqrt(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName sqrt = 
			QualifiedName.make(CAL_Math.MODULE_NAME, "sqrt");

		/**
		 * Returns the trigonometric tangent of an angle.
		 * <p>
		 * Special cases:
		 * <ul>
		 *  <li>
		 *   if the argument is <code>Cal.Core.Prelude.notANumber</code>, <code>Cal.Core.Prelude.positiveInfinity</code> or <code>Cal.Core.Prelude.negativeInfinity</code>,
		 *   then the result is <code>Cal.Core.Prelude.notANumber</code>.
		 *  </li>
		 *  <li>
		 *   if the argument is zero, then the result is a zero with the same sign as the argument.
		 *  </li>
		 * </ul>
		 * 
		 * @param x (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 *          the angle, in radians.
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 *          the tangent of the argument.
		 */
		public static final SourceModel.Expr tan(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.tan), x});
		}

		/**
		 * @see #tan(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @return the SourceModel.Expr representing an application of tan
		 */
		public static final SourceModel.Expr tan(double x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.tan), SourceModel.Expr.makeDoubleValue(x)});
		}

		/**
		 * Name binding for function: tan.
		 * @see #tan(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName tan = 
			QualifiedName.make(CAL_Math.MODULE_NAME, "tan");

		/**
		 * Returns the hyperbolic tangent of the specified <code>Cal.Core.Prelude.Double</code> value. The hyperbolic tangent of x is defined to be
		 * (e<sup>x</sup> - e<sup>-x</sup>)/(e<sup>x</sup> + e<sup>-x</sup>), in other words, sinh(x)/cosh(x).
		 * Note that the absolute value of the exact tanh is always less than 1.
		 * @param x (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 *          the number whose hyperbolic tangent is to be returned.
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 *          the hyperbolic tangent of the argument.
		 */
		public static final SourceModel.Expr tanh(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.tanh), x});
		}

		/**
		 * @see #tanh(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @return the SourceModel.Expr representing an application of tanh
		 */
		public static final SourceModel.Expr tanh(double x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.tanh), SourceModel.Expr.makeDoubleValue(x)});
		}

		/**
		 * Name binding for function: tanh.
		 * @see #tanh(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName tanh = 
			QualifiedName.make(CAL_Math.MODULE_NAME, "tanh");

		/**
		 * Truncates the <code>Cal.Core.Prelude.Double</code> value towards zero, dropping the fractional part.
		 * <p>
		 * Note that, for finite values of <code>x</code>:
		 * 
		 * <pre>   (truncate x) + (Cal.Utilities.Math.fractionalPart x) == x</pre>
		 * 
		 * 
		 * @param value (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 *          the value whose fractional part is to be dropped.
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 *          <code>value</code> truncated towards zero.
		 */
		public static final SourceModel.Expr truncate(SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.truncate), value});
		}

		/**
		 * @see #truncate(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value
		 * @return the SourceModel.Expr representing an application of truncate
		 */
		public static final SourceModel.Expr truncate(double value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.truncate), SourceModel.Expr.makeDoubleValue(value)});
		}

		/**
		 * Name binding for function: truncate.
		 * @see #truncate(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName truncate = 
			QualifiedName.make(CAL_Math.MODULE_NAME, "truncate");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = -1636319060;

}
