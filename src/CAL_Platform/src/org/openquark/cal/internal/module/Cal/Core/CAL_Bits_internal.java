/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_Bits_internal.java)
 * was generated from CAL module: Cal.Core.Bits.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Core.Bits module from Java code.
 *  
 * Creation date: Fri Mar 16 13:11:57 PST 2007
 * --!>
 *  
 */

package org.openquark.cal.internal.module.Cal.Core;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * Defines the type class and operations for bitwise operations.
 * @author Peter Cardwell
 */
public final class CAL_Bits_internal {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Core.Bits");

	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Core.Bits module.
	 */
	public static final class Functions {
		/**
		 * Helper binding method for function: bitwiseAndInt. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of bitwiseAndInt
		 */
		public static final SourceModel.Expr bitwiseAndInt(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.bitwiseAndInt), arg_1, arg_2});
		}

		/**
		 * @see #bitwiseAndInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of bitwiseAndInt
		 */
		public static final SourceModel.Expr bitwiseAndInt(int arg_1, int arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.bitwiseAndInt), SourceModel.Expr.makeIntValue(arg_1), SourceModel.Expr.makeIntValue(arg_2)});
		}

		/**
		 * Name binding for function: bitwiseAndInt.
		 * @see #bitwiseAndInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName bitwiseAndInt = 
			QualifiedName.make(CAL_Bits_internal.MODULE_NAME, "bitwiseAndInt");

		/**
		 * Helper binding method for function: bitwiseAndLong. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of bitwiseAndLong
		 */
		public static final SourceModel.Expr bitwiseAndLong(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.bitwiseAndLong), arg_1, arg_2});
		}

		/**
		 * @see #bitwiseAndLong(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of bitwiseAndLong
		 */
		public static final SourceModel.Expr bitwiseAndLong(long arg_1, long arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.bitwiseAndLong), SourceModel.Expr.makeLongValue(arg_1), SourceModel.Expr.makeLongValue(arg_2)});
		}

		/**
		 * Name binding for function: bitwiseAndLong.
		 * @see #bitwiseAndLong(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName bitwiseAndLong = 
			QualifiedName.make(CAL_Bits_internal.MODULE_NAME, "bitwiseAndLong");

		/**
		 * Helper binding method for function: bitwiseOrInt. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of bitwiseOrInt
		 */
		public static final SourceModel.Expr bitwiseOrInt(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.bitwiseOrInt), arg_1, arg_2});
		}

		/**
		 * @see #bitwiseOrInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of bitwiseOrInt
		 */
		public static final SourceModel.Expr bitwiseOrInt(int arg_1, int arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.bitwiseOrInt), SourceModel.Expr.makeIntValue(arg_1), SourceModel.Expr.makeIntValue(arg_2)});
		}

		/**
		 * Name binding for function: bitwiseOrInt.
		 * @see #bitwiseOrInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName bitwiseOrInt = 
			QualifiedName.make(CAL_Bits_internal.MODULE_NAME, "bitwiseOrInt");

		/**
		 * Helper binding method for function: bitwiseOrLong. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of bitwiseOrLong
		 */
		public static final SourceModel.Expr bitwiseOrLong(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.bitwiseOrLong), arg_1, arg_2});
		}

		/**
		 * @see #bitwiseOrLong(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of bitwiseOrLong
		 */
		public static final SourceModel.Expr bitwiseOrLong(long arg_1, long arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.bitwiseOrLong), SourceModel.Expr.makeLongValue(arg_1), SourceModel.Expr.makeLongValue(arg_2)});
		}

		/**
		 * Name binding for function: bitwiseOrLong.
		 * @see #bitwiseOrLong(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName bitwiseOrLong = 
			QualifiedName.make(CAL_Bits_internal.MODULE_NAME, "bitwiseOrLong");

		/**
		 * Helper binding method for function: bitwiseXorInt. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of bitwiseXorInt
		 */
		public static final SourceModel.Expr bitwiseXorInt(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.bitwiseXorInt), arg_1, arg_2});
		}

		/**
		 * @see #bitwiseXorInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of bitwiseXorInt
		 */
		public static final SourceModel.Expr bitwiseXorInt(int arg_1, int arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.bitwiseXorInt), SourceModel.Expr.makeIntValue(arg_1), SourceModel.Expr.makeIntValue(arg_2)});
		}

		/**
		 * Name binding for function: bitwiseXorInt.
		 * @see #bitwiseXorInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName bitwiseXorInt = 
			QualifiedName.make(CAL_Bits_internal.MODULE_NAME, "bitwiseXorInt");

		/**
		 * Helper binding method for function: bitwiseXorLong. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of bitwiseXorLong
		 */
		public static final SourceModel.Expr bitwiseXorLong(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.bitwiseXorLong), arg_1, arg_2});
		}

		/**
		 * @see #bitwiseXorLong(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of bitwiseXorLong
		 */
		public static final SourceModel.Expr bitwiseXorLong(long arg_1, long arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.bitwiseXorLong), SourceModel.Expr.makeLongValue(arg_1), SourceModel.Expr.makeLongValue(arg_2)});
		}

		/**
		 * Name binding for function: bitwiseXorLong.
		 * @see #bitwiseXorLong(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName bitwiseXorLong = 
			QualifiedName.make(CAL_Bits_internal.MODULE_NAME, "bitwiseXorLong");

		/**
		 * Helper binding method for function: complementInt. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of complementInt
		 */
		public static final SourceModel.Expr complementInt(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.complementInt), arg_1});
		}

		/**
		 * @see #complementInt(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of complementInt
		 */
		public static final SourceModel.Expr complementInt(int arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.complementInt), SourceModel.Expr.makeIntValue(arg_1)});
		}

		/**
		 * Name binding for function: complementInt.
		 * @see #complementInt(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName complementInt = 
			QualifiedName.make(CAL_Bits_internal.MODULE_NAME, "complementInt");

		/**
		 * Helper binding method for function: complementLong. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of complementLong
		 */
		public static final SourceModel.Expr complementLong(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.complementLong), arg_1});
		}

		/**
		 * @see #complementLong(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of complementLong
		 */
		public static final SourceModel.Expr complementLong(long arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.complementLong), SourceModel.Expr.makeLongValue(arg_1)});
		}

		/**
		 * Name binding for function: complementLong.
		 * @see #complementLong(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName complementLong = 
			QualifiedName.make(CAL_Bits_internal.MODULE_NAME, "complementLong");

		/**
		 * Helper binding method for function: highestBitMaskInt. 
		 * @param x
		 * @return the SourceModule.expr representing an application of highestBitMaskInt
		 */
		public static final SourceModel.Expr highestBitMaskInt(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.highestBitMaskInt), x});
		}

		/**
		 * @see #highestBitMaskInt(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @return the SourceModel.Expr representing an application of highestBitMaskInt
		 */
		public static final SourceModel.Expr highestBitMaskInt(int x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.highestBitMaskInt), SourceModel.Expr.makeIntValue(x)});
		}

		/**
		 * Name binding for function: highestBitMaskInt.
		 * @see #highestBitMaskInt(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName highestBitMaskInt = 
			QualifiedName.make(
				CAL_Bits_internal.MODULE_NAME, 
				"highestBitMaskInt");

		/**
		 * Helper binding method for function: highestBitMaskLong. 
		 * @param x
		 * @return the SourceModule.expr representing an application of highestBitMaskLong
		 */
		public static final SourceModel.Expr highestBitMaskLong(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.highestBitMaskLong), x});
		}

		/**
		 * @see #highestBitMaskLong(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @return the SourceModel.Expr representing an application of highestBitMaskLong
		 */
		public static final SourceModel.Expr highestBitMaskLong(long x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.highestBitMaskLong), SourceModel.Expr.makeLongValue(x)});
		}

		/**
		 * Name binding for function: highestBitMaskLong.
		 * @see #highestBitMaskLong(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName highestBitMaskLong = 
			QualifiedName.make(
				CAL_Bits_internal.MODULE_NAME, 
				"highestBitMaskLong");

		/**
		 * Helper binding method for function: shiftLInt. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of shiftLInt
		 */
		public static final SourceModel.Expr shiftLInt(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shiftLInt), arg_1, arg_2});
		}

		/**
		 * @see #shiftLInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of shiftLInt
		 */
		public static final SourceModel.Expr shiftLInt(int arg_1, int arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shiftLInt), SourceModel.Expr.makeIntValue(arg_1), SourceModel.Expr.makeIntValue(arg_2)});
		}

		/**
		 * Name binding for function: shiftLInt.
		 * @see #shiftLInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shiftLInt = 
			QualifiedName.make(CAL_Bits_internal.MODULE_NAME, "shiftLInt");

		/**
		 * Helper binding method for function: shiftLLong. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of shiftLLong
		 */
		public static final SourceModel.Expr shiftLLong(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shiftLLong), arg_1, arg_2});
		}

		/**
		 * @see #shiftLLong(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of shiftLLong
		 */
		public static final SourceModel.Expr shiftLLong(long arg_1, long arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shiftLLong), SourceModel.Expr.makeLongValue(arg_1), SourceModel.Expr.makeLongValue(arg_2)});
		}

		/**
		 * Name binding for function: shiftLLong.
		 * @see #shiftLLong(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shiftLLong = 
			QualifiedName.make(CAL_Bits_internal.MODULE_NAME, "shiftLLong");

		/**
		 * Helper binding method for function: shiftRInt. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of shiftRInt
		 */
		public static final SourceModel.Expr shiftRInt(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shiftRInt), arg_1, arg_2});
		}

		/**
		 * @see #shiftRInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of shiftRInt
		 */
		public static final SourceModel.Expr shiftRInt(int arg_1, int arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shiftRInt), SourceModel.Expr.makeIntValue(arg_1), SourceModel.Expr.makeIntValue(arg_2)});
		}

		/**
		 * Name binding for function: shiftRInt.
		 * @see #shiftRInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shiftRInt = 
			QualifiedName.make(CAL_Bits_internal.MODULE_NAME, "shiftRInt");

		/**
		 * Helper binding method for function: shiftRLong. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of shiftRLong
		 */
		public static final SourceModel.Expr shiftRLong(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shiftRLong), arg_1, arg_2});
		}

		/**
		 * @see #shiftRLong(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of shiftRLong
		 */
		public static final SourceModel.Expr shiftRLong(long arg_1, long arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shiftRLong), SourceModel.Expr.makeLongValue(arg_1), SourceModel.Expr.makeLongValue(arg_2)});
		}

		/**
		 * Name binding for function: shiftRLong.
		 * @see #shiftRLong(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shiftRLong = 
			QualifiedName.make(CAL_Bits_internal.MODULE_NAME, "shiftRLong");

		/**
		 * Helper binding method for function: shiftRUnsignedInt. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of shiftRUnsignedInt
		 */
		public static final SourceModel.Expr shiftRUnsignedInt(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shiftRUnsignedInt), arg_1, arg_2});
		}

		/**
		 * @see #shiftRUnsignedInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of shiftRUnsignedInt
		 */
		public static final SourceModel.Expr shiftRUnsignedInt(int arg_1, int arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shiftRUnsignedInt), SourceModel.Expr.makeIntValue(arg_1), SourceModel.Expr.makeIntValue(arg_2)});
		}

		/**
		 * Name binding for function: shiftRUnsignedInt.
		 * @see #shiftRUnsignedInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shiftRUnsignedInt = 
			QualifiedName.make(
				CAL_Bits_internal.MODULE_NAME, 
				"shiftRUnsignedInt");

		/**
		 * Helper binding method for function: shiftRUnsignedLong. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of shiftRUnsignedLong
		 */
		public static final SourceModel.Expr shiftRUnsignedLong(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shiftRUnsignedLong), arg_1, arg_2});
		}

		/**
		 * @see #shiftRUnsignedLong(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of shiftRUnsignedLong
		 */
		public static final SourceModel.Expr shiftRUnsignedLong(long arg_1, long arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shiftRUnsignedLong), SourceModel.Expr.makeLongValue(arg_1), SourceModel.Expr.makeLongValue(arg_2)});
		}

		/**
		 * Name binding for function: shiftRUnsignedLong.
		 * @see #shiftRUnsignedLong(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shiftRUnsignedLong = 
			QualifiedName.make(
				CAL_Bits_internal.MODULE_NAME, 
				"shiftRUnsignedLong");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = -882812638;

}
