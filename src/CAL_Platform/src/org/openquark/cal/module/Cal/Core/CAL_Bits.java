/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_Bits.java)
 * was generated from CAL module: Cal.Core.Bits.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Core.Bits module from Java code.
 *  
 * Creation date: Fri Mar 16 13:11:56 PST 2007
 * --!>
 *  
 */

package org.openquark.cal.module.Cal.Core;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * Defines the type class and operations for bitwise operations.
 * @author Peter Cardwell
 */
public final class CAL_Bits {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Core.Bits");

	/**
	 * This inner class (TypeClasses) contains constants
	 * and methods related to binding to CAL TypeClasses in the Cal.Core.Bits module.
	 */
	public static final class TypeClasses {
		/**
		 * A class for data types that support bitwise operations.
		 */
		public static final QualifiedName Bits = 
			QualifiedName.make(CAL_Bits.MODULE_NAME, "Bits");

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Core.Bits module.
	 */
	public static final class Functions {
		/**
		 * Combines its two arguments by performing a boolean AND operation on
		 * their individual bits. The results has a bit set only if the corresponding
		 * bit is set in both arguments.
		 * For example, <code>bitwiseAnd(00001010, 00000111) =&gt; 00000010</code>
		 * @param x (CAL type: <code>Cal.Core.Bits.Bits a => a</code>)
		 *          the first value to be bitwise ANDed.
		 * @param y (CAL type: <code>Cal.Core.Bits.Bits a => a</code>)
		 *          the second value to be bitwise ANDed.
		 * @return (CAL type: <code>Cal.Core.Bits.Bits a => a</code>) 
		 *          the bitwise AND of <code>x</code> and <code>y</code>.
		 */
		public static final SourceModel.Expr bitwiseAnd(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.bitwiseAnd), x, y});
		}

		/**
		 * Name binding for function: bitwiseAnd.
		 * @see #bitwiseAnd(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName bitwiseAnd = 
			QualifiedName.make(CAL_Bits.MODULE_NAME, "bitwiseAnd");

		/**
		 * Combines its two arguments by performing a boolean OR operation on their 
		 * individual bits. The result has a bit set if the corresponding bit is
		 * set in either or both of the arguments. It has a zero bit only where both
		 * corresponding argument bits are zero.
		 * For example, <code>bitwiseOr(00001010, 00000111) =&gt; 00001111</code>
		 * @param x (CAL type: <code>Cal.Core.Bits.Bits a => a</code>)
		 *          the first value to be bitwise ORed.
		 * @param y (CAL type: <code>Cal.Core.Bits.Bits a => a</code>)
		 *          the second value to be bitwise ORed.
		 * @return (CAL type: <code>Cal.Core.Bits.Bits a => a</code>) 
		 *          the bitwise OR of <code>x</code> and <code>y</code>.
		 */
		public static final SourceModel.Expr bitwiseOr(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.bitwiseOr), x, y});
		}

		/**
		 * Name binding for function: bitwiseOr.
		 * @see #bitwiseOr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName bitwiseOr = 
			QualifiedName.make(CAL_Bits.MODULE_NAME, "bitwiseOr");

		/**
		 * Combines its two arguments by performing a boolean XOR operation on their
		 * individual bits. The result has a bit set if the corresponding bits in the
		 * two arguments are different. If the corresponding argument bits are both ones
		 * or both zeros, the result bit is a zero. 
		 * For example, <code>bitwiseXor (00001010, 00000111) =&gt; 00001101</code>
		 * @param x (CAL type: <code>Cal.Core.Bits.Bits a => a</code>)
		 *          the first value to be bitwise XORed.
		 * @param y (CAL type: <code>Cal.Core.Bits.Bits a => a</code>)
		 *          the second value to be bitwise XORed.
		 * @return (CAL type: <code>Cal.Core.Bits.Bits a => a</code>) 
		 *          the bitwise XOR of <code>x</code> and <code>y</code>.
		 */
		public static final SourceModel.Expr bitwiseXor(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.bitwiseXor), x, y});
		}

		/**
		 * Name binding for function: bitwiseXor.
		 * @see #bitwiseXor(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName bitwiseXor = 
			QualifiedName.make(CAL_Bits.MODULE_NAME, "bitwiseXor");

		/**
		 * Inverts each bit of its input, converting ones to zeros and zeros to ones.
		 * For example, <code>complement(00001100) =&gt; 11110011</code>
		 * @param x (CAL type: <code>Cal.Core.Bits.Bits a => a</code>)
		 *          the value to be complemented bitwise.
		 * @return (CAL type: <code>Cal.Core.Bits.Bits a => a</code>) 
		 *          the bitwise complement of <code>x</code>.
		 */
		public static final SourceModel.Expr complement(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.complement), x});
		}

		/**
		 * Name binding for function: complement.
		 * @see #complement(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName complement = 
			QualifiedName.make(CAL_Bits.MODULE_NAME, "complement");

		/**
		 * Clears all but the highest 1-bit of the input.
		 * For example,
		 * <ul>
		 *  <li>
		 *   <code>highestBitMask (00101101) =&gt; 00100000</code>
		 *  </li>
		 *  <li>
		 *   <code>highestBitMask (11111111) =&gt; 10000000</code>
		 *  </li>
		 *  <li>
		 *   <code>highestBitMask (00000000) =&gt; 00000000</code>
		 *  </li>
		 * </ul>
		 * 
		 * @param x (CAL type: <code>Cal.Core.Bits.Bits a => a</code>)
		 *          the value whose bits except for its highest 1-bit is to be cleared.
		 * @return (CAL type: <code>Cal.Core.Bits.Bits a => a</code>) 
		 *          a value whose bits are all zero except for the bit corresponding to <code>x</code>'s highest 1-bit.
		 */
		public static final SourceModel.Expr highestBitMask(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.highestBitMask), x});
		}

		/**
		 * Name binding for function: highestBitMask.
		 * @see #highestBitMask(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName highestBitMask = 
			QualifiedName.make(CAL_Bits.MODULE_NAME, "highestBitMask");

		/**
		 * Shifts the bits of the first argument left by the number of places specified
		 * by the second argument. High-order bits of the first argument are lost, and
		 * zero bits are shifted in from the right.
		 * For example,
		 * <ul>
		 *  <li>
		 *   <code>shiftL(00001010, 1) =&gt; 00010100</code>
		 *  </li>
		 *  <li>
		 *   <code>shiftL(00000111, 3) =&gt; 00111000</code>
		 *  </li>
		 * </ul>
		 * 
		 * @param x (CAL type: <code>Cal.Core.Bits.Bits a => a</code>)
		 *          the value to be left-shifted.
		 * @param numPlaces (CAL type: <code>Cal.Core.Bits.Bits a => a</code>)
		 *          the number of places by which x is left-shifted.
		 * @return (CAL type: <code>Cal.Core.Bits.Bits a => a</code>) 
		 *          <code>x</code> left-shifted by <code>numPlaces</code> places.
		 */
		public static final SourceModel.Expr shiftL(SourceModel.Expr x, SourceModel.Expr numPlaces) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shiftL), x, numPlaces});
		}

		/**
		 * Name binding for function: shiftL.
		 * @see #shiftL(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shiftL = 
			QualifiedName.make(CAL_Bits.MODULE_NAME, "shiftL");

		/**
		 * Shifts the bits of the first argument to the right by the number of places
		 * specified by the second argument. The low-order bits of the first argument
		 * are shifted away and are lost. The high-order bits shifted in are the same
		 * as the original high-order bits of the first argument. In other words,
		 * if the first argument is positive, zeros are shifted into the high-order bits.
		 * If the first argument is negative, ones are shifted in instead.
		 * For example,
		 * <ul>
		 *  <li>
		 *   <code>shiftR(00001010, 1) =&gt; 00000101</code>
		 *  </li>
		 *  <li>
		 *   <code>shiftR(00011011, 3) =&gt; 00000011</code>
		 *  </li>
		 *  <li>
		 *   <code>shiftR(11001110, 2) =&gt; 11110011</code>
		 *  </li>
		 * </ul>
		 * <p>
		 *  
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Bits.shiftRUnsigned
		 * </dl>
		 * 
		 * @param x (CAL type: <code>Cal.Core.Bits.Bits a => a</code>)
		 *          the value to be right-shifted.
		 * @param numPlaces (CAL type: <code>Cal.Core.Bits.Bits a => a</code>)
		 *          the number of places by which x is right-shifted.
		 * @return (CAL type: <code>Cal.Core.Bits.Bits a => a</code>) 
		 *          <code>x</code> right-shifted by <code>numPlaces</code> places.
		 */
		public static final SourceModel.Expr shiftR(SourceModel.Expr x, SourceModel.Expr numPlaces) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shiftR), x, numPlaces});
		}

		/**
		 * Name binding for function: shiftR.
		 * @see #shiftR(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shiftR = 
			QualifiedName.make(CAL_Bits.MODULE_NAME, "shiftR");

		/**
		 * Works like <code>Cal.Core.Bits.shiftR</code>, but always shifts zeros into the high-order bits of
		 * the result, regardless of the sign of the first argument.
		 * For example, <code>shiftRUnsigned(11111111, 4) =&gt; 00001111</code>
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Bits.shiftR
		 * </dl>
		 * 
		 * @param x (CAL type: <code>Cal.Core.Bits.Bits a => a</code>)
		 *          the value to be right-shifted.
		 * @param numPlaces (CAL type: <code>Cal.Core.Bits.Bits a => a</code>)
		 *          the number of places by which x is right-shifted.
		 * @return (CAL type: <code>Cal.Core.Bits.Bits a => a</code>) 
		 *          <code>x</code> right-shifted by <code>numPlaces</code> places.
		 */
		public static final SourceModel.Expr shiftRUnsigned(SourceModel.Expr x, SourceModel.Expr numPlaces) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shiftRUnsigned), x, numPlaces});
		}

		/**
		 * Name binding for function: shiftRUnsigned.
		 * @see #shiftRUnsigned(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shiftRUnsigned = 
			QualifiedName.make(CAL_Bits.MODULE_NAME, "shiftRUnsigned");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = -560415868;

}
