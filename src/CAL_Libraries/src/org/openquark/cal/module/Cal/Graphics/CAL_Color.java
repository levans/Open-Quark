/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_Color.java)
 * was generated from CAL module: Cal.Graphics.Color.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Graphics.Color module from Java code.
 *  
 * Creation date: Tue Aug 28 15:58:39 PDT 2007
 * --!>
 *  
 */

package org.openquark.cal.module.Cal.Graphics;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * Defines the CAL <code>Cal.Graphics.Color.Color</code> type, as well as associated functions.
 * @author Bo Ilic
 */
public final class CAL_Color {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Graphics.Color");

	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Graphics.Color module.
	 */
	public static final class TypeConstructors {
		/**
		 * The CAL type for representing a color.
		 */
		public static final QualifiedName Color = 
			QualifiedName.make(CAL_Color.MODULE_NAME, "Color");

		/**
		 * A CAL foreign type corresponding to the Java java.awt.Color type.
		 */
		public static final QualifiedName JColor = 
			QualifiedName.make(CAL_Color.MODULE_NAME, "JColor");

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Graphics.Color module.
	 */
	public static final class Functions {
		/**
		 * The <code>Color</code> value for the color aqua.
		 * @return (CAL type: <code>Cal.Graphics.Color.Color</code>) 
		 */
		public static final SourceModel.Expr aqua() {
			return SourceModel.Expr.Var.make(Functions.aqua);
		}

		/**
		 * Name binding for function: aqua.
		 * @see #aqua()
		 */
		public static final QualifiedName aqua = 
			QualifiedName.make(CAL_Color.MODULE_NAME, "aqua");

		/**
		 * The <code>Color</code> value for the color black.
		 * @return (CAL type: <code>Cal.Graphics.Color.Color</code>) 
		 */
		public static final SourceModel.Expr black() {
			return SourceModel.Expr.Var.make(Functions.black);
		}

		/**
		 * Name binding for function: black.
		 * @see #black()
		 */
		public static final QualifiedName black = 
			QualifiedName.make(CAL_Color.MODULE_NAME, "black");

		/**
		 * The <code>Color</code> value for the color blue.
		 * @return (CAL type: <code>Cal.Graphics.Color.Color</code>) 
		 */
		public static final SourceModel.Expr blue() {
			return SourceModel.Expr.Var.make(Functions.blue);
		}

		/**
		 * Name binding for function: blue.
		 * @see #blue()
		 */
		public static final QualifiedName blue = 
			QualifiedName.make(CAL_Color.MODULE_NAME, "blue");

		/**
		 * Returns a triple of the constituent RGB values that make up the specified <code>Cal.Graphics.Color.Color</code> value.
		 * @param colour (CAL type: <code>Cal.Graphics.Color.Color</code>)
		 *          the <code>Color</code> value.
		 * @return (CAL type: <code>(Cal.Core.Prelude.Int, Cal.Core.Prelude.Int, Cal.Core.Prelude.Int)</code>) 
		 *          a triple of the constituent RGB values that make up the specified <code>Cal.Graphics.Color.Color</code> value.
		 */
		public static final SourceModel.Expr colorToRGB(SourceModel.Expr colour) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.colorToRGB), colour});
		}

		/**
		 * Name binding for function: colorToRGB.
		 * @see #colorToRGB(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName colorToRGB = 
			QualifiedName.make(CAL_Color.MODULE_NAME, "colorToRGB");

		/**
		 * Returns a 4-tuple of the constituent RGB and alpha values that make up the specified <code>Cal.Graphics.Color.Color</code> value.
		 * @param colour (CAL type: <code>Cal.Graphics.Color.Color</code>)
		 *          the <code>Color</code> value.
		 * @return (CAL type: <code>(Cal.Core.Prelude.Int, Cal.Core.Prelude.Int, Cal.Core.Prelude.Int, Cal.Core.Prelude.Int)</code>) 
		 *          a 4-tuple of the constituent RGB and alpha values that make up the specified <code>Cal.Graphics.Color.Color</code> value.
		 */
		public static final SourceModel.Expr colorToRGBA(SourceModel.Expr colour) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.colorToRGBA), colour});
		}

		/**
		 * Name binding for function: colorToRGBA.
		 * @see #colorToRGBA(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName colorToRGBA = 
			QualifiedName.make(CAL_Color.MODULE_NAME, "colorToRGBA");

		/**
		 * The <code>Color</code> value for the color fuchsia.
		 * @return (CAL type: <code>Cal.Graphics.Color.Color</code>) 
		 */
		public static final SourceModel.Expr fuchsia() {
			return SourceModel.Expr.Var.make(Functions.fuchsia);
		}

		/**
		 * Name binding for function: fuchsia.
		 * @see #fuchsia()
		 */
		public static final QualifiedName fuchsia = 
			QualifiedName.make(CAL_Color.MODULE_NAME, "fuchsia");

		/**
		 * The <code>Color</code> value for the color gray.
		 * @return (CAL type: <code>Cal.Graphics.Color.Color</code>) 
		 */
		public static final SourceModel.Expr gray() {
			return SourceModel.Expr.Var.make(Functions.gray);
		}

		/**
		 * Name binding for function: gray.
		 * @see #gray()
		 */
		public static final QualifiedName gray = 
			QualifiedName.make(CAL_Color.MODULE_NAME, "gray");

		/**
		 * The <code>Color</code> value for the color green.
		 * @return (CAL type: <code>Cal.Graphics.Color.Color</code>) 
		 */
		public static final SourceModel.Expr green() {
			return SourceModel.Expr.Var.make(Functions.green);
		}

		/**
		 * Name binding for function: green.
		 * @see #green()
		 */
		public static final QualifiedName green = 
			QualifiedName.make(CAL_Color.MODULE_NAME, "green");

		/**
		 * Converts a <code>Cal.Graphics.Color.JColor</code> value to a <code>Cal.Graphics.Color.Color</code> value.
		 * @param color (CAL type: <code>Cal.Graphics.Color.JColor</code>)
		 *          the <code>JColor</code> value to be converted.
		 * @return (CAL type: <code>Cal.Graphics.Color.Color</code>) 
		 *          the corresponding <code>Color</code> value.
		 */
		public static final SourceModel.Expr inputColor(SourceModel.Expr color) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inputColor), color});
		}

		/**
		 * Name binding for function: inputColor.
		 * @see #inputColor(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName inputColor = 
			QualifiedName.make(CAL_Color.MODULE_NAME, "inputColor");

		/**
		 * The <code>Color</code> value for the color lime.
		 * @return (CAL type: <code>Cal.Graphics.Color.Color</code>) 
		 */
		public static final SourceModel.Expr lime() {
			return SourceModel.Expr.Var.make(Functions.lime);
		}

		/**
		 * Name binding for function: lime.
		 * @see #lime()
		 */
		public static final QualifiedName lime = 
			QualifiedName.make(CAL_Color.MODULE_NAME, "lime");

		/**
		 * Constructs a <code>Cal.Graphics.Color.Color</code> value from its constituent RGB values in the range (0 - 255).
		 * @param red (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the red component in the range (0 - 255).
		 * @param green (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the green component in the range (0 - 255).
		 * @param blue (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the blue component in the range (0 - 255).
		 * @return (CAL type: <code>Cal.Graphics.Color.Color</code>) 
		 *          the resulting <code>Color</code> value.
		 */
		public static final SourceModel.Expr makeColor(SourceModel.Expr red, SourceModel.Expr green, SourceModel.Expr blue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeColor), red, green, blue});
		}

		/**
		 * @see #makeColor(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param red
		 * @param green
		 * @param blue
		 * @return the SourceModel.Expr representing an application of makeColor
		 */
		public static final SourceModel.Expr makeColor(int red, int green, int blue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeColor), SourceModel.Expr.makeIntValue(red), SourceModel.Expr.makeIntValue(green), SourceModel.Expr.makeIntValue(blue)});
		}

		/**
		 * Name binding for function: makeColor.
		 * @see #makeColor(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeColor = 
			QualifiedName.make(CAL_Color.MODULE_NAME, "makeColor");

		/**
		 * Constructs a <code>Cal.Graphics.Color.Color</code> value representing a translucent color from its constituent RGB and alpha values in the range (0 - 255).
		 * @param red (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the red component in the range (0 - 255).
		 * @param green (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the green component in the range (0 - 255).
		 * @param blue (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the blue component in the range (0 - 255).
		 * @param alpha (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the alpha component in the range (0 - 255).
		 * @return (CAL type: <code>Cal.Graphics.Color.Color</code>) 
		 *          the resulting <code>Color</code> value.
		 */
		public static final SourceModel.Expr makeTranslucentColor(SourceModel.Expr red, SourceModel.Expr green, SourceModel.Expr blue, SourceModel.Expr alpha) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeTranslucentColor), red, green, blue, alpha});
		}

		/**
		 * @see #makeTranslucentColor(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param red
		 * @param green
		 * @param blue
		 * @param alpha
		 * @return the SourceModel.Expr representing an application of makeTranslucentColor
		 */
		public static final SourceModel.Expr makeTranslucentColor(int red, int green, int blue, int alpha) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeTranslucentColor), SourceModel.Expr.makeIntValue(red), SourceModel.Expr.makeIntValue(green), SourceModel.Expr.makeIntValue(blue), SourceModel.Expr.makeIntValue(alpha)});
		}

		/**
		 * Name binding for function: makeTranslucentColor.
		 * @see #makeTranslucentColor(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeTranslucentColor = 
			QualifiedName.make(CAL_Color.MODULE_NAME, "makeTranslucentColor");

		/**
		 * The <code>Color</code> value for the color maroon.
		 * @return (CAL type: <code>Cal.Graphics.Color.Color</code>) 
		 */
		public static final SourceModel.Expr maroon() {
			return SourceModel.Expr.Var.make(Functions.maroon);
		}

		/**
		 * Name binding for function: maroon.
		 * @see #maroon()
		 */
		public static final QualifiedName maroon = 
			QualifiedName.make(CAL_Color.MODULE_NAME, "maroon");

		/**
		 * The <code>Color</code> value for the color navy.
		 * @return (CAL type: <code>Cal.Graphics.Color.Color</code>) 
		 */
		public static final SourceModel.Expr navy() {
			return SourceModel.Expr.Var.make(Functions.navy);
		}

		/**
		 * Name binding for function: navy.
		 * @see #navy()
		 */
		public static final QualifiedName navy = 
			QualifiedName.make(CAL_Color.MODULE_NAME, "navy");

		/**
		 * The <code>Color</code> value for the color olive.
		 * @return (CAL type: <code>Cal.Graphics.Color.Color</code>) 
		 */
		public static final SourceModel.Expr olive() {
			return SourceModel.Expr.Var.make(Functions.olive);
		}

		/**
		 * Name binding for function: olive.
		 * @see #olive()
		 */
		public static final QualifiedName olive = 
			QualifiedName.make(CAL_Color.MODULE_NAME, "olive");

		/**
		 * Converts a <code>Cal.Graphics.Color.Color</code> value to a <code>Cal.Graphics.Color.JColor</code> value.
		 * @param colour (CAL type: <code>Cal.Graphics.Color.Color</code>)
		 *          the <code>Color</code> value to be converted.
		 * @return (CAL type: <code>Cal.Graphics.Color.JColor</code>) 
		 *          the corresponding <code>JColor</code> value.
		 */
		public static final SourceModel.Expr outputColor(SourceModel.Expr colour) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputColor), colour});
		}

		/**
		 * Name binding for function: outputColor.
		 * @see #outputColor(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName outputColor = 
			QualifiedName.make(CAL_Color.MODULE_NAME, "outputColor");

		/**
		 * The <code>Color</code> value for the color purple.
		 * @return (CAL type: <code>Cal.Graphics.Color.Color</code>) 
		 */
		public static final SourceModel.Expr purple() {
			return SourceModel.Expr.Var.make(Functions.purple);
		}

		/**
		 * Name binding for function: purple.
		 * @see #purple()
		 */
		public static final QualifiedName purple = 
			QualifiedName.make(CAL_Color.MODULE_NAME, "purple");

		/**
		 * The <code>Color</code> value for the color red.
		 * @return (CAL type: <code>Cal.Graphics.Color.Color</code>) 
		 */
		public static final SourceModel.Expr red() {
			return SourceModel.Expr.Var.make(Functions.red);
		}

		/**
		 * Name binding for function: red.
		 * @see #red()
		 */
		public static final QualifiedName red = 
			QualifiedName.make(CAL_Color.MODULE_NAME, "red");

		/**
		 * Constructs a <code>Cal.Graphics.Color.Color</code> value from its constituent RGB values, as represented as a triple.
		 * @param rgb (CAL type: <code>(Cal.Core.Prelude.Int, Cal.Core.Prelude.Int, Cal.Core.Prelude.Int)</code>)
		 *          the (red, green, blue) components, each in the range (0 - 255).
		 * @return (CAL type: <code>Cal.Graphics.Color.Color</code>) 
		 *          the resulting <code>Color</code> value.
		 */
		public static final SourceModel.Expr rgbToColor(SourceModel.Expr rgb) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.rgbToColor), rgb});
		}

		/**
		 * Name binding for function: rgbToColor.
		 * @see #rgbToColor(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName rgbToColor = 
			QualifiedName.make(CAL_Color.MODULE_NAME, "rgbToColor");

		/**
		 * Constructs a <code>Cal.Graphics.Color.Color</code> value representing a translucent color from its constituent RGB and alpha values, as represented as a 4-tuple.
		 * @param rgba (CAL type: <code>(Cal.Core.Prelude.Int, Cal.Core.Prelude.Int, Cal.Core.Prelude.Int, Cal.Core.Prelude.Int)</code>)
		 *          the (red, green, blue, alpha) components, each in the range (0 - 255).
		 * @return (CAL type: <code>Cal.Graphics.Color.Color</code>) 
		 *          the resulting <code>Color</code> value.
		 */
		public static final SourceModel.Expr rgbaToColor(SourceModel.Expr rgba) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.rgbaToColor), rgba});
		}

		/**
		 * Name binding for function: rgbaToColor.
		 * @see #rgbaToColor(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName rgbaToColor = 
			QualifiedName.make(CAL_Color.MODULE_NAME, "rgbaToColor");

		/**
		 * Helper binding method for function: showColor. 
		 * @param color
		 * @return the SourceModule.expr representing an application of showColor
		 */
		public static final SourceModel.Expr showColor(SourceModel.Expr color) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showColor), color});
		}

		/**
		 * Name binding for function: showColor.
		 * @see #showColor(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showColor = 
			QualifiedName.make(CAL_Color.MODULE_NAME, "showColor");

		/**
		 * The <code>Color</code> value for the color silver.
		 * @return (CAL type: <code>Cal.Graphics.Color.Color</code>) 
		 */
		public static final SourceModel.Expr silver() {
			return SourceModel.Expr.Var.make(Functions.silver);
		}

		/**
		 * Name binding for function: silver.
		 * @see #silver()
		 */
		public static final QualifiedName silver = 
			QualifiedName.make(CAL_Color.MODULE_NAME, "silver");

		/**
		 * The <code>Color</code> value for the color teal.
		 * @return (CAL type: <code>Cal.Graphics.Color.Color</code>) 
		 */
		public static final SourceModel.Expr teal() {
			return SourceModel.Expr.Var.make(Functions.teal);
		}

		/**
		 * Name binding for function: teal.
		 * @see #teal()
		 */
		public static final QualifiedName teal = 
			QualifiedName.make(CAL_Color.MODULE_NAME, "teal");

		/**
		 * The <code>Color</code> value for the color white.
		 * @return (CAL type: <code>Cal.Graphics.Color.Color</code>) 
		 */
		public static final SourceModel.Expr white() {
			return SourceModel.Expr.Var.make(Functions.white);
		}

		/**
		 * Name binding for function: white.
		 * @see #white()
		 */
		public static final QualifiedName white = 
			QualifiedName.make(CAL_Color.MODULE_NAME, "white");

		/**
		 * The <code>Color</code> value for the color yellow.
		 * @return (CAL type: <code>Cal.Graphics.Color.Color</code>) 
		 */
		public static final SourceModel.Expr yellow() {
			return SourceModel.Expr.Var.make(Functions.yellow);
		}

		/**
		 * Name binding for function: yellow.
		 * @see #yellow()
		 */
		public static final QualifiedName yellow = 
			QualifiedName.make(CAL_Color.MODULE_NAME, "yellow");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = -229951300;

}
