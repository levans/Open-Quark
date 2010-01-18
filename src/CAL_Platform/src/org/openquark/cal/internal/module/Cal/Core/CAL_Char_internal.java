/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_Char_internal.java)
 * was generated from CAL module: Cal.Core.Char.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Core.Char module from Java code.
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
 * Defines many useful functions for the <code>Cal.Core.Prelude.Char</code> type. Note that the <code>Cal.Core.Prelude.Char</code>
 * type itself is defined in the <code>Cal.Core.Prelude</code> module due to the fact that it is supported via built-in notation
 * for <code>Cal.Core.Prelude.Char</code> literals.
 * @author Bo Ilic
 */
public final class CAL_Char_internal {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Core.Char");

	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Core.Char module.
	 */
	public static final class Functions {
		/**
		 * Helper binding method for function: byteToDirectionality. 
		 * @param byteValue
		 * @return the SourceModule.expr representing an application of byteToDirectionality
		 */
		public static final SourceModel.Expr byteToDirectionality(SourceModel.Expr byteValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteToDirectionality), byteValue});
		}

		/**
		 * @see #byteToDirectionality(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param byteValue
		 * @return the SourceModel.Expr representing an application of byteToDirectionality
		 */
		public static final SourceModel.Expr byteToDirectionality(byte byteValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteToDirectionality), SourceModel.Expr.makeByteValue(byteValue)});
		}

		/**
		 * Name binding for function: byteToDirectionality.
		 * @see #byteToDirectionality(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName byteToDirectionality = 
			QualifiedName.make(
				CAL_Char_internal.MODULE_NAME, 
				"byteToDirectionality");

		/**
		 * Helper binding method for function: directionalityExamples. 
		 * @return the SourceModule.expr representing an application of directionalityExamples
		 */
		public static final SourceModel.Expr directionalityExamples() {
			return SourceModel.Expr.Var.make(Functions.directionalityExamples);
		}

		/**
		 * Name binding for function: directionalityExamples.
		 * @see #directionalityExamples()
		 */
		public static final QualifiedName directionalityExamples = 
			QualifiedName.make(
				CAL_Char_internal.MODULE_NAME, 
				"directionalityExamples");

		/**
		 * Helper binding method for function: inputDirectionality. 
		 * @param objectValue
		 * @return the SourceModule.expr representing an application of inputDirectionality
		 */
		public static final SourceModel.Expr inputDirectionality(SourceModel.Expr objectValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inputDirectionality), objectValue});
		}

		/**
		 * Name binding for function: inputDirectionality.
		 * @see #inputDirectionality(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName inputDirectionality = 
			QualifiedName.make(
				CAL_Char_internal.MODULE_NAME, 
				"inputDirectionality");

		/**
		 * Helper binding method for function: inputType. 
		 * @param objectValue
		 * @return the SourceModule.expr representing an application of inputType
		 */
		public static final SourceModel.Expr inputType(SourceModel.Expr objectValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inputType), objectValue});
		}

		/**
		 * Name binding for function: inputType.
		 * @see #inputType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName inputType = 
			QualifiedName.make(CAL_Char_internal.MODULE_NAME, "inputType");

		/**
		 * Helper binding method for function: intToType. 
		 * @param intValue
		 * @return the SourceModule.expr representing an application of intToType
		 */
		public static final SourceModel.Expr intToType(SourceModel.Expr intValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intToType), intValue});
		}

		/**
		 * @see #intToType(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param intValue
		 * @return the SourceModel.Expr representing an application of intToType
		 */
		public static final SourceModel.Expr intToType(int intValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intToType), SourceModel.Expr.makeIntValue(intValue)});
		}

		/**
		 * Name binding for function: intToType.
		 * @see #intToType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intToType = 
			QualifiedName.make(CAL_Char_internal.MODULE_NAME, "intToType");

		/**
		 * Helper binding method for function: jDIRECTIONALITY_ARABIC_NUMBER. 
		 * @return the SourceModule.expr representing an application of jDIRECTIONALITY_ARABIC_NUMBER
		 */
		public static final SourceModel.Expr jDIRECTIONALITY_ARABIC_NUMBER() {
			return 
				SourceModel.Expr.Var.make(
					Functions.jDIRECTIONALITY_ARABIC_NUMBER);
		}

		/**
		 * Name binding for function: jDIRECTIONALITY_ARABIC_NUMBER.
		 * @see #jDIRECTIONALITY_ARABIC_NUMBER()
		 */
		public static final QualifiedName jDIRECTIONALITY_ARABIC_NUMBER = 
			QualifiedName.make(
				CAL_Char_internal.MODULE_NAME, 
				"jDIRECTIONALITY_ARABIC_NUMBER");

		/**
		 * Helper binding method for function: jDIRECTIONALITY_BOUNDARY_NEUTRAL. 
		 * @return the SourceModule.expr representing an application of jDIRECTIONALITY_BOUNDARY_NEUTRAL
		 */
		public static final SourceModel.Expr jDIRECTIONALITY_BOUNDARY_NEUTRAL() {
			return 
				SourceModel.Expr.Var.make(
					Functions.jDIRECTIONALITY_BOUNDARY_NEUTRAL);
		}

		/**
		 * Name binding for function: jDIRECTIONALITY_BOUNDARY_NEUTRAL.
		 * @see #jDIRECTIONALITY_BOUNDARY_NEUTRAL()
		 */
		public static final QualifiedName jDIRECTIONALITY_BOUNDARY_NEUTRAL = 
			QualifiedName.make(
				CAL_Char_internal.MODULE_NAME, 
				"jDIRECTIONALITY_BOUNDARY_NEUTRAL");

		/**
		 * Helper binding method for function: jDIRECTIONALITY_COMMON_NUMBER_SEPARATOR. 
		 * @return the SourceModule.expr representing an application of jDIRECTIONALITY_COMMON_NUMBER_SEPARATOR
		 */
		public static final SourceModel.Expr jDIRECTIONALITY_COMMON_NUMBER_SEPARATOR() {
			return 
				SourceModel.Expr.Var.make(
					Functions.jDIRECTIONALITY_COMMON_NUMBER_SEPARATOR);
		}

		/**
		 * Name binding for function: jDIRECTIONALITY_COMMON_NUMBER_SEPARATOR.
		 * @see #jDIRECTIONALITY_COMMON_NUMBER_SEPARATOR()
		 */
		public static final QualifiedName jDIRECTIONALITY_COMMON_NUMBER_SEPARATOR = 
			QualifiedName.make(
				CAL_Char_internal.MODULE_NAME, 
				"jDIRECTIONALITY_COMMON_NUMBER_SEPARATOR");

		/**
		 * Helper binding method for function: jDIRECTIONALITY_EUROPEAN_NUMBER. 
		 * @return the SourceModule.expr representing an application of jDIRECTIONALITY_EUROPEAN_NUMBER
		 */
		public static final SourceModel.Expr jDIRECTIONALITY_EUROPEAN_NUMBER() {
			return 
				SourceModel.Expr.Var.make(
					Functions.jDIRECTIONALITY_EUROPEAN_NUMBER);
		}

		/**
		 * Name binding for function: jDIRECTIONALITY_EUROPEAN_NUMBER.
		 * @see #jDIRECTIONALITY_EUROPEAN_NUMBER()
		 */
		public static final QualifiedName jDIRECTIONALITY_EUROPEAN_NUMBER = 
			QualifiedName.make(
				CAL_Char_internal.MODULE_NAME, 
				"jDIRECTIONALITY_EUROPEAN_NUMBER");

		/**
		 * Helper binding method for function: jDIRECTIONALITY_EUROPEAN_NUMBER_SEPARATOR. 
		 * @return the SourceModule.expr representing an application of jDIRECTIONALITY_EUROPEAN_NUMBER_SEPARATOR
		 */
		public static final SourceModel.Expr jDIRECTIONALITY_EUROPEAN_NUMBER_SEPARATOR() {
			return 
				SourceModel.Expr.Var.make(
					Functions.jDIRECTIONALITY_EUROPEAN_NUMBER_SEPARATOR);
		}

		/**
		 * Name binding for function: jDIRECTIONALITY_EUROPEAN_NUMBER_SEPARATOR.
		 * @see #jDIRECTIONALITY_EUROPEAN_NUMBER_SEPARATOR()
		 */
		public static final QualifiedName jDIRECTIONALITY_EUROPEAN_NUMBER_SEPARATOR = 
			QualifiedName.make(
				CAL_Char_internal.MODULE_NAME, 
				"jDIRECTIONALITY_EUROPEAN_NUMBER_SEPARATOR");

		/**
		 * Helper binding method for function: jDIRECTIONALITY_EUROPEAN_NUMBER_TERMINATOR. 
		 * @return the SourceModule.expr representing an application of jDIRECTIONALITY_EUROPEAN_NUMBER_TERMINATOR
		 */
		public static final SourceModel.Expr jDIRECTIONALITY_EUROPEAN_NUMBER_TERMINATOR() {
			return 
				SourceModel.Expr.Var.make(
					Functions.jDIRECTIONALITY_EUROPEAN_NUMBER_TERMINATOR);
		}

		/**
		 * Name binding for function: jDIRECTIONALITY_EUROPEAN_NUMBER_TERMINATOR.
		 * @see #jDIRECTIONALITY_EUROPEAN_NUMBER_TERMINATOR()
		 */
		public static final QualifiedName jDIRECTIONALITY_EUROPEAN_NUMBER_TERMINATOR = 
			QualifiedName.make(
				CAL_Char_internal.MODULE_NAME, 
				"jDIRECTIONALITY_EUROPEAN_NUMBER_TERMINATOR");

		/**
		 * Helper binding method for function: jDIRECTIONALITY_LEFT_TO_RIGHT. 
		 * @return the SourceModule.expr representing an application of jDIRECTIONALITY_LEFT_TO_RIGHT
		 */
		public static final SourceModel.Expr jDIRECTIONALITY_LEFT_TO_RIGHT() {
			return 
				SourceModel.Expr.Var.make(
					Functions.jDIRECTIONALITY_LEFT_TO_RIGHT);
		}

		/**
		 * Name binding for function: jDIRECTIONALITY_LEFT_TO_RIGHT.
		 * @see #jDIRECTIONALITY_LEFT_TO_RIGHT()
		 */
		public static final QualifiedName jDIRECTIONALITY_LEFT_TO_RIGHT = 
			QualifiedName.make(
				CAL_Char_internal.MODULE_NAME, 
				"jDIRECTIONALITY_LEFT_TO_RIGHT");

		/**
		 * Helper binding method for function: jDIRECTIONALITY_LEFT_TO_RIGHT_EMBEDDING. 
		 * @return the SourceModule.expr representing an application of jDIRECTIONALITY_LEFT_TO_RIGHT_EMBEDDING
		 */
		public static final SourceModel.Expr jDIRECTIONALITY_LEFT_TO_RIGHT_EMBEDDING() {
			return 
				SourceModel.Expr.Var.make(
					Functions.jDIRECTIONALITY_LEFT_TO_RIGHT_EMBEDDING);
		}

		/**
		 * Name binding for function: jDIRECTIONALITY_LEFT_TO_RIGHT_EMBEDDING.
		 * @see #jDIRECTIONALITY_LEFT_TO_RIGHT_EMBEDDING()
		 */
		public static final QualifiedName jDIRECTIONALITY_LEFT_TO_RIGHT_EMBEDDING = 
			QualifiedName.make(
				CAL_Char_internal.MODULE_NAME, 
				"jDIRECTIONALITY_LEFT_TO_RIGHT_EMBEDDING");

		/**
		 * Helper binding method for function: jDIRECTIONALITY_LEFT_TO_RIGHT_OVERRIDE. 
		 * @return the SourceModule.expr representing an application of jDIRECTIONALITY_LEFT_TO_RIGHT_OVERRIDE
		 */
		public static final SourceModel.Expr jDIRECTIONALITY_LEFT_TO_RIGHT_OVERRIDE() {
			return 
				SourceModel.Expr.Var.make(
					Functions.jDIRECTIONALITY_LEFT_TO_RIGHT_OVERRIDE);
		}

		/**
		 * Name binding for function: jDIRECTIONALITY_LEFT_TO_RIGHT_OVERRIDE.
		 * @see #jDIRECTIONALITY_LEFT_TO_RIGHT_OVERRIDE()
		 */
		public static final QualifiedName jDIRECTIONALITY_LEFT_TO_RIGHT_OVERRIDE = 
			QualifiedName.make(
				CAL_Char_internal.MODULE_NAME, 
				"jDIRECTIONALITY_LEFT_TO_RIGHT_OVERRIDE");

		/**
		 * Helper binding method for function: jDIRECTIONALITY_NONSPACING_MARK. 
		 * @return the SourceModule.expr representing an application of jDIRECTIONALITY_NONSPACING_MARK
		 */
		public static final SourceModel.Expr jDIRECTIONALITY_NONSPACING_MARK() {
			return 
				SourceModel.Expr.Var.make(
					Functions.jDIRECTIONALITY_NONSPACING_MARK);
		}

		/**
		 * Name binding for function: jDIRECTIONALITY_NONSPACING_MARK.
		 * @see #jDIRECTIONALITY_NONSPACING_MARK()
		 */
		public static final QualifiedName jDIRECTIONALITY_NONSPACING_MARK = 
			QualifiedName.make(
				CAL_Char_internal.MODULE_NAME, 
				"jDIRECTIONALITY_NONSPACING_MARK");

		/**
		 * Helper binding method for function: jDIRECTIONALITY_OTHER_NEUTRALS. 
		 * @return the SourceModule.expr representing an application of jDIRECTIONALITY_OTHER_NEUTRALS
		 */
		public static final SourceModel.Expr jDIRECTIONALITY_OTHER_NEUTRALS() {
			return 
				SourceModel.Expr.Var.make(
					Functions.jDIRECTIONALITY_OTHER_NEUTRALS);
		}

		/**
		 * Name binding for function: jDIRECTIONALITY_OTHER_NEUTRALS.
		 * @see #jDIRECTIONALITY_OTHER_NEUTRALS()
		 */
		public static final QualifiedName jDIRECTIONALITY_OTHER_NEUTRALS = 
			QualifiedName.make(
				CAL_Char_internal.MODULE_NAME, 
				"jDIRECTIONALITY_OTHER_NEUTRALS");

		/**
		 * Helper binding method for function: jDIRECTIONALITY_PARAGRAPH_SEPARATOR. 
		 * @return the SourceModule.expr representing an application of jDIRECTIONALITY_PARAGRAPH_SEPARATOR
		 */
		public static final SourceModel.Expr jDIRECTIONALITY_PARAGRAPH_SEPARATOR() {
			return 
				SourceModel.Expr.Var.make(
					Functions.jDIRECTIONALITY_PARAGRAPH_SEPARATOR);
		}

		/**
		 * Name binding for function: jDIRECTIONALITY_PARAGRAPH_SEPARATOR.
		 * @see #jDIRECTIONALITY_PARAGRAPH_SEPARATOR()
		 */
		public static final QualifiedName jDIRECTIONALITY_PARAGRAPH_SEPARATOR = 
			QualifiedName.make(
				CAL_Char_internal.MODULE_NAME, 
				"jDIRECTIONALITY_PARAGRAPH_SEPARATOR");

		/**
		 * Helper binding method for function: jDIRECTIONALITY_POP_DIRECTIONAL_FORMAT. 
		 * @return the SourceModule.expr representing an application of jDIRECTIONALITY_POP_DIRECTIONAL_FORMAT
		 */
		public static final SourceModel.Expr jDIRECTIONALITY_POP_DIRECTIONAL_FORMAT() {
			return 
				SourceModel.Expr.Var.make(
					Functions.jDIRECTIONALITY_POP_DIRECTIONAL_FORMAT);
		}

		/**
		 * Name binding for function: jDIRECTIONALITY_POP_DIRECTIONAL_FORMAT.
		 * @see #jDIRECTIONALITY_POP_DIRECTIONAL_FORMAT()
		 */
		public static final QualifiedName jDIRECTIONALITY_POP_DIRECTIONAL_FORMAT = 
			QualifiedName.make(
				CAL_Char_internal.MODULE_NAME, 
				"jDIRECTIONALITY_POP_DIRECTIONAL_FORMAT");

		/**
		 * Helper binding method for function: jDIRECTIONALITY_RIGHT_TO_LEFT. 
		 * @return the SourceModule.expr representing an application of jDIRECTIONALITY_RIGHT_TO_LEFT
		 */
		public static final SourceModel.Expr jDIRECTIONALITY_RIGHT_TO_LEFT() {
			return 
				SourceModel.Expr.Var.make(
					Functions.jDIRECTIONALITY_RIGHT_TO_LEFT);
		}

		/**
		 * Name binding for function: jDIRECTIONALITY_RIGHT_TO_LEFT.
		 * @see #jDIRECTIONALITY_RIGHT_TO_LEFT()
		 */
		public static final QualifiedName jDIRECTIONALITY_RIGHT_TO_LEFT = 
			QualifiedName.make(
				CAL_Char_internal.MODULE_NAME, 
				"jDIRECTIONALITY_RIGHT_TO_LEFT");

		/**
		 * Helper binding method for function: jDIRECTIONALITY_RIGHT_TO_LEFT_ARABIC. 
		 * @return the SourceModule.expr representing an application of jDIRECTIONALITY_RIGHT_TO_LEFT_ARABIC
		 */
		public static final SourceModel.Expr jDIRECTIONALITY_RIGHT_TO_LEFT_ARABIC() {
			return 
				SourceModel.Expr.Var.make(
					Functions.jDIRECTIONALITY_RIGHT_TO_LEFT_ARABIC);
		}

		/**
		 * Name binding for function: jDIRECTIONALITY_RIGHT_TO_LEFT_ARABIC.
		 * @see #jDIRECTIONALITY_RIGHT_TO_LEFT_ARABIC()
		 */
		public static final QualifiedName jDIRECTIONALITY_RIGHT_TO_LEFT_ARABIC = 
			QualifiedName.make(
				CAL_Char_internal.MODULE_NAME, 
				"jDIRECTIONALITY_RIGHT_TO_LEFT_ARABIC");

		/**
		 * Helper binding method for function: jDIRECTIONALITY_RIGHT_TO_LEFT_EMBEDDING. 
		 * @return the SourceModule.expr representing an application of jDIRECTIONALITY_RIGHT_TO_LEFT_EMBEDDING
		 */
		public static final SourceModel.Expr jDIRECTIONALITY_RIGHT_TO_LEFT_EMBEDDING() {
			return 
				SourceModel.Expr.Var.make(
					Functions.jDIRECTIONALITY_RIGHT_TO_LEFT_EMBEDDING);
		}

		/**
		 * Name binding for function: jDIRECTIONALITY_RIGHT_TO_LEFT_EMBEDDING.
		 * @see #jDIRECTIONALITY_RIGHT_TO_LEFT_EMBEDDING()
		 */
		public static final QualifiedName jDIRECTIONALITY_RIGHT_TO_LEFT_EMBEDDING = 
			QualifiedName.make(
				CAL_Char_internal.MODULE_NAME, 
				"jDIRECTIONALITY_RIGHT_TO_LEFT_EMBEDDING");

		/**
		 * Helper binding method for function: jDIRECTIONALITY_RIGHT_TO_LEFT_OVERRIDE. 
		 * @return the SourceModule.expr representing an application of jDIRECTIONALITY_RIGHT_TO_LEFT_OVERRIDE
		 */
		public static final SourceModel.Expr jDIRECTIONALITY_RIGHT_TO_LEFT_OVERRIDE() {
			return 
				SourceModel.Expr.Var.make(
					Functions.jDIRECTIONALITY_RIGHT_TO_LEFT_OVERRIDE);
		}

		/**
		 * Name binding for function: jDIRECTIONALITY_RIGHT_TO_LEFT_OVERRIDE.
		 * @see #jDIRECTIONALITY_RIGHT_TO_LEFT_OVERRIDE()
		 */
		public static final QualifiedName jDIRECTIONALITY_RIGHT_TO_LEFT_OVERRIDE = 
			QualifiedName.make(
				CAL_Char_internal.MODULE_NAME, 
				"jDIRECTIONALITY_RIGHT_TO_LEFT_OVERRIDE");

		/**
		 * Helper binding method for function: jDIRECTIONALITY_SEGMENT_SEPARATOR. 
		 * @return the SourceModule.expr representing an application of jDIRECTIONALITY_SEGMENT_SEPARATOR
		 */
		public static final SourceModel.Expr jDIRECTIONALITY_SEGMENT_SEPARATOR() {
			return 
				SourceModel.Expr.Var.make(
					Functions.jDIRECTIONALITY_SEGMENT_SEPARATOR);
		}

		/**
		 * Name binding for function: jDIRECTIONALITY_SEGMENT_SEPARATOR.
		 * @see #jDIRECTIONALITY_SEGMENT_SEPARATOR()
		 */
		public static final QualifiedName jDIRECTIONALITY_SEGMENT_SEPARATOR = 
			QualifiedName.make(
				CAL_Char_internal.MODULE_NAME, 
				"jDIRECTIONALITY_SEGMENT_SEPARATOR");

		/**
		 * Helper binding method for function: jDIRECTIONALITY_UNDEFINED. 
		 * @return the SourceModule.expr representing an application of jDIRECTIONALITY_UNDEFINED
		 */
		public static final SourceModel.Expr jDIRECTIONALITY_UNDEFINED() {
			return 
				SourceModel.Expr.Var.make(Functions.jDIRECTIONALITY_UNDEFINED);
		}

		/**
		 * Name binding for function: jDIRECTIONALITY_UNDEFINED.
		 * @see #jDIRECTIONALITY_UNDEFINED()
		 */
		public static final QualifiedName jDIRECTIONALITY_UNDEFINED = 
			QualifiedName.make(
				CAL_Char_internal.MODULE_NAME, 
				"jDIRECTIONALITY_UNDEFINED");

		/**
		 * Helper binding method for function: jDIRECTIONALITY_WHITESPACE. 
		 * @return the SourceModule.expr representing an application of jDIRECTIONALITY_WHITESPACE
		 */
		public static final SourceModel.Expr jDIRECTIONALITY_WHITESPACE() {
			return 
				SourceModel.Expr.Var.make(Functions.jDIRECTIONALITY_WHITESPACE);
		}

		/**
		 * Name binding for function: jDIRECTIONALITY_WHITESPACE.
		 * @see #jDIRECTIONALITY_WHITESPACE()
		 */
		public static final QualifiedName jDIRECTIONALITY_WHITESPACE = 
			QualifiedName.make(
				CAL_Char_internal.MODULE_NAME, 
				"jDIRECTIONALITY_WHITESPACE");

		/**
		 * 
		 * @param charValue (CAL type: <code>Cal.Core.Prelude.Char</code>)
		 *          the <code>Cal.Core.Prelude.Char</code> to classify
		 * @return (CAL type: <code>Cal.Core.Prelude.Byte</code>) 
		 *          a <code>Cal.Core.Prelude.Byte</code> value that can be translated to a <code>Cal.Core.Char.Directionality</code> value
		 * using <code>Cal.Core.Char.byteToDirectionality</code>.
		 */
		public static final SourceModel.Expr jDirectionality(SourceModel.Expr charValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jDirectionality), charValue});
		}

		/**
		 * @see #jDirectionality(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param charValue
		 * @return the SourceModel.Expr representing an application of jDirectionality
		 */
		public static final SourceModel.Expr jDirectionality(char charValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jDirectionality), SourceModel.Expr.makeCharValue(charValue)});
		}

		/**
		 * Name binding for function: jDirectionality.
		 * @see #jDirectionality(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jDirectionality = 
			QualifiedName.make(CAL_Char_internal.MODULE_NAME, "jDirectionality");

		/**
		 * Helper binding method for function: jTYPE_COMBINING_SPACING_MARK. 
		 * @return the SourceModule.expr representing an application of jTYPE_COMBINING_SPACING_MARK
		 */
		public static final SourceModel.Expr jTYPE_COMBINING_SPACING_MARK() {
			return 
				SourceModel.Expr.Var.make(
					Functions.jTYPE_COMBINING_SPACING_MARK);
		}

		/**
		 * Name binding for function: jTYPE_COMBINING_SPACING_MARK.
		 * @see #jTYPE_COMBINING_SPACING_MARK()
		 */
		public static final QualifiedName jTYPE_COMBINING_SPACING_MARK = 
			QualifiedName.make(
				CAL_Char_internal.MODULE_NAME, 
				"jTYPE_COMBINING_SPACING_MARK");

		/**
		 * Helper binding method for function: jTYPE_CONNECTOR_PUNCTUATION. 
		 * @return the SourceModule.expr representing an application of jTYPE_CONNECTOR_PUNCTUATION
		 */
		public static final SourceModel.Expr jTYPE_CONNECTOR_PUNCTUATION() {
			return 
				SourceModel.Expr.Var.make(Functions.jTYPE_CONNECTOR_PUNCTUATION);
		}

		/**
		 * Name binding for function: jTYPE_CONNECTOR_PUNCTUATION.
		 * @see #jTYPE_CONNECTOR_PUNCTUATION()
		 */
		public static final QualifiedName jTYPE_CONNECTOR_PUNCTUATION = 
			QualifiedName.make(
				CAL_Char_internal.MODULE_NAME, 
				"jTYPE_CONNECTOR_PUNCTUATION");

		/**
		 * Helper binding method for function: jTYPE_CONTROL. 
		 * @return the SourceModule.expr representing an application of jTYPE_CONTROL
		 */
		public static final SourceModel.Expr jTYPE_CONTROL() {
			return SourceModel.Expr.Var.make(Functions.jTYPE_CONTROL);
		}

		/**
		 * Name binding for function: jTYPE_CONTROL.
		 * @see #jTYPE_CONTROL()
		 */
		public static final QualifiedName jTYPE_CONTROL = 
			QualifiedName.make(CAL_Char_internal.MODULE_NAME, "jTYPE_CONTROL");

		/**
		 * Helper binding method for function: jTYPE_CURRENCY_SYMBOL. 
		 * @return the SourceModule.expr representing an application of jTYPE_CURRENCY_SYMBOL
		 */
		public static final SourceModel.Expr jTYPE_CURRENCY_SYMBOL() {
			return SourceModel.Expr.Var.make(Functions.jTYPE_CURRENCY_SYMBOL);
		}

		/**
		 * Name binding for function: jTYPE_CURRENCY_SYMBOL.
		 * @see #jTYPE_CURRENCY_SYMBOL()
		 */
		public static final QualifiedName jTYPE_CURRENCY_SYMBOL = 
			QualifiedName.make(
				CAL_Char_internal.MODULE_NAME, 
				"jTYPE_CURRENCY_SYMBOL");

		/**
		 * Helper binding method for function: jTYPE_DASH_PUNCTUATION. 
		 * @return the SourceModule.expr representing an application of jTYPE_DASH_PUNCTUATION
		 */
		public static final SourceModel.Expr jTYPE_DASH_PUNCTUATION() {
			return SourceModel.Expr.Var.make(Functions.jTYPE_DASH_PUNCTUATION);
		}

		/**
		 * Name binding for function: jTYPE_DASH_PUNCTUATION.
		 * @see #jTYPE_DASH_PUNCTUATION()
		 */
		public static final QualifiedName jTYPE_DASH_PUNCTUATION = 
			QualifiedName.make(
				CAL_Char_internal.MODULE_NAME, 
				"jTYPE_DASH_PUNCTUATION");

		/**
		 * Helper binding method for function: jTYPE_DECIMAL_DIGIT_NUMBER. 
		 * @return the SourceModule.expr representing an application of jTYPE_DECIMAL_DIGIT_NUMBER
		 */
		public static final SourceModel.Expr jTYPE_DECIMAL_DIGIT_NUMBER() {
			return 
				SourceModel.Expr.Var.make(Functions.jTYPE_DECIMAL_DIGIT_NUMBER);
		}

		/**
		 * Name binding for function: jTYPE_DECIMAL_DIGIT_NUMBER.
		 * @see #jTYPE_DECIMAL_DIGIT_NUMBER()
		 */
		public static final QualifiedName jTYPE_DECIMAL_DIGIT_NUMBER = 
			QualifiedName.make(
				CAL_Char_internal.MODULE_NAME, 
				"jTYPE_DECIMAL_DIGIT_NUMBER");

		/**
		 * Helper binding method for function: jTYPE_ENCLOSING_MARK. 
		 * @return the SourceModule.expr representing an application of jTYPE_ENCLOSING_MARK
		 */
		public static final SourceModel.Expr jTYPE_ENCLOSING_MARK() {
			return SourceModel.Expr.Var.make(Functions.jTYPE_ENCLOSING_MARK);
		}

		/**
		 * Name binding for function: jTYPE_ENCLOSING_MARK.
		 * @see #jTYPE_ENCLOSING_MARK()
		 */
		public static final QualifiedName jTYPE_ENCLOSING_MARK = 
			QualifiedName.make(
				CAL_Char_internal.MODULE_NAME, 
				"jTYPE_ENCLOSING_MARK");

		/**
		 * Helper binding method for function: jTYPE_END_PUNCTUATION. 
		 * @return the SourceModule.expr representing an application of jTYPE_END_PUNCTUATION
		 */
		public static final SourceModel.Expr jTYPE_END_PUNCTUATION() {
			return SourceModel.Expr.Var.make(Functions.jTYPE_END_PUNCTUATION);
		}

		/**
		 * Name binding for function: jTYPE_END_PUNCTUATION.
		 * @see #jTYPE_END_PUNCTUATION()
		 */
		public static final QualifiedName jTYPE_END_PUNCTUATION = 
			QualifiedName.make(
				CAL_Char_internal.MODULE_NAME, 
				"jTYPE_END_PUNCTUATION");

		/**
		 * Helper binding method for function: jTYPE_FINAL_QUOTE_PUNCTUATION. 
		 * @return the SourceModule.expr representing an application of jTYPE_FINAL_QUOTE_PUNCTUATION
		 */
		public static final SourceModel.Expr jTYPE_FINAL_QUOTE_PUNCTUATION() {
			return 
				SourceModel.Expr.Var.make(
					Functions.jTYPE_FINAL_QUOTE_PUNCTUATION);
		}

		/**
		 * Name binding for function: jTYPE_FINAL_QUOTE_PUNCTUATION.
		 * @see #jTYPE_FINAL_QUOTE_PUNCTUATION()
		 */
		public static final QualifiedName jTYPE_FINAL_QUOTE_PUNCTUATION = 
			QualifiedName.make(
				CAL_Char_internal.MODULE_NAME, 
				"jTYPE_FINAL_QUOTE_PUNCTUATION");

		/**
		 * Helper binding method for function: jTYPE_FORMAT. 
		 * @return the SourceModule.expr representing an application of jTYPE_FORMAT
		 */
		public static final SourceModel.Expr jTYPE_FORMAT() {
			return SourceModel.Expr.Var.make(Functions.jTYPE_FORMAT);
		}

		/**
		 * Name binding for function: jTYPE_FORMAT.
		 * @see #jTYPE_FORMAT()
		 */
		public static final QualifiedName jTYPE_FORMAT = 
			QualifiedName.make(CAL_Char_internal.MODULE_NAME, "jTYPE_FORMAT");

		/**
		 * Helper binding method for function: jTYPE_INITIAL_QUOTE_PUNCTUATION. 
		 * @return the SourceModule.expr representing an application of jTYPE_INITIAL_QUOTE_PUNCTUATION
		 */
		public static final SourceModel.Expr jTYPE_INITIAL_QUOTE_PUNCTUATION() {
			return 
				SourceModel.Expr.Var.make(
					Functions.jTYPE_INITIAL_QUOTE_PUNCTUATION);
		}

		/**
		 * Name binding for function: jTYPE_INITIAL_QUOTE_PUNCTUATION.
		 * @see #jTYPE_INITIAL_QUOTE_PUNCTUATION()
		 */
		public static final QualifiedName jTYPE_INITIAL_QUOTE_PUNCTUATION = 
			QualifiedName.make(
				CAL_Char_internal.MODULE_NAME, 
				"jTYPE_INITIAL_QUOTE_PUNCTUATION");

		/**
		 * Helper binding method for function: jTYPE_LETTER_NUMBER. 
		 * @return the SourceModule.expr representing an application of jTYPE_LETTER_NUMBER
		 */
		public static final SourceModel.Expr jTYPE_LETTER_NUMBER() {
			return SourceModel.Expr.Var.make(Functions.jTYPE_LETTER_NUMBER);
		}

		/**
		 * Name binding for function: jTYPE_LETTER_NUMBER.
		 * @see #jTYPE_LETTER_NUMBER()
		 */
		public static final QualifiedName jTYPE_LETTER_NUMBER = 
			QualifiedName.make(
				CAL_Char_internal.MODULE_NAME, 
				"jTYPE_LETTER_NUMBER");

		/**
		 * Helper binding method for function: jTYPE_LINE_SEPARATOR. 
		 * @return the SourceModule.expr representing an application of jTYPE_LINE_SEPARATOR
		 */
		public static final SourceModel.Expr jTYPE_LINE_SEPARATOR() {
			return SourceModel.Expr.Var.make(Functions.jTYPE_LINE_SEPARATOR);
		}

		/**
		 * Name binding for function: jTYPE_LINE_SEPARATOR.
		 * @see #jTYPE_LINE_SEPARATOR()
		 */
		public static final QualifiedName jTYPE_LINE_SEPARATOR = 
			QualifiedName.make(
				CAL_Char_internal.MODULE_NAME, 
				"jTYPE_LINE_SEPARATOR");

		/**
		 * Helper binding method for function: jTYPE_LOWERCASE_LETTER. 
		 * @return the SourceModule.expr representing an application of jTYPE_LOWERCASE_LETTER
		 */
		public static final SourceModel.Expr jTYPE_LOWERCASE_LETTER() {
			return SourceModel.Expr.Var.make(Functions.jTYPE_LOWERCASE_LETTER);
		}

		/**
		 * Name binding for function: jTYPE_LOWERCASE_LETTER.
		 * @see #jTYPE_LOWERCASE_LETTER()
		 */
		public static final QualifiedName jTYPE_LOWERCASE_LETTER = 
			QualifiedName.make(
				CAL_Char_internal.MODULE_NAME, 
				"jTYPE_LOWERCASE_LETTER");

		/**
		 * Helper binding method for function: jTYPE_MATH_SYMBOL. 
		 * @return the SourceModule.expr representing an application of jTYPE_MATH_SYMBOL
		 */
		public static final SourceModel.Expr jTYPE_MATH_SYMBOL() {
			return SourceModel.Expr.Var.make(Functions.jTYPE_MATH_SYMBOL);
		}

		/**
		 * Name binding for function: jTYPE_MATH_SYMBOL.
		 * @see #jTYPE_MATH_SYMBOL()
		 */
		public static final QualifiedName jTYPE_MATH_SYMBOL = 
			QualifiedName.make(
				CAL_Char_internal.MODULE_NAME, 
				"jTYPE_MATH_SYMBOL");

		/**
		 * Helper binding method for function: jTYPE_MODIFIER_LETTER. 
		 * @return the SourceModule.expr representing an application of jTYPE_MODIFIER_LETTER
		 */
		public static final SourceModel.Expr jTYPE_MODIFIER_LETTER() {
			return SourceModel.Expr.Var.make(Functions.jTYPE_MODIFIER_LETTER);
		}

		/**
		 * Name binding for function: jTYPE_MODIFIER_LETTER.
		 * @see #jTYPE_MODIFIER_LETTER()
		 */
		public static final QualifiedName jTYPE_MODIFIER_LETTER = 
			QualifiedName.make(
				CAL_Char_internal.MODULE_NAME, 
				"jTYPE_MODIFIER_LETTER");

		/**
		 * Helper binding method for function: jTYPE_MODIFIER_SYMBOL. 
		 * @return the SourceModule.expr representing an application of jTYPE_MODIFIER_SYMBOL
		 */
		public static final SourceModel.Expr jTYPE_MODIFIER_SYMBOL() {
			return SourceModel.Expr.Var.make(Functions.jTYPE_MODIFIER_SYMBOL);
		}

		/**
		 * Name binding for function: jTYPE_MODIFIER_SYMBOL.
		 * @see #jTYPE_MODIFIER_SYMBOL()
		 */
		public static final QualifiedName jTYPE_MODIFIER_SYMBOL = 
			QualifiedName.make(
				CAL_Char_internal.MODULE_NAME, 
				"jTYPE_MODIFIER_SYMBOL");

		/**
		 * Helper binding method for function: jTYPE_NON_SPACING_MARK. 
		 * @return the SourceModule.expr representing an application of jTYPE_NON_SPACING_MARK
		 */
		public static final SourceModel.Expr jTYPE_NON_SPACING_MARK() {
			return SourceModel.Expr.Var.make(Functions.jTYPE_NON_SPACING_MARK);
		}

		/**
		 * Name binding for function: jTYPE_NON_SPACING_MARK.
		 * @see #jTYPE_NON_SPACING_MARK()
		 */
		public static final QualifiedName jTYPE_NON_SPACING_MARK = 
			QualifiedName.make(
				CAL_Char_internal.MODULE_NAME, 
				"jTYPE_NON_SPACING_MARK");

		/**
		 * Helper binding method for function: jTYPE_OTHER_LETTER. 
		 * @return the SourceModule.expr representing an application of jTYPE_OTHER_LETTER
		 */
		public static final SourceModel.Expr jTYPE_OTHER_LETTER() {
			return SourceModel.Expr.Var.make(Functions.jTYPE_OTHER_LETTER);
		}

		/**
		 * Name binding for function: jTYPE_OTHER_LETTER.
		 * @see #jTYPE_OTHER_LETTER()
		 */
		public static final QualifiedName jTYPE_OTHER_LETTER = 
			QualifiedName.make(
				CAL_Char_internal.MODULE_NAME, 
				"jTYPE_OTHER_LETTER");

		/**
		 * Helper binding method for function: jTYPE_OTHER_NUMBER. 
		 * @return the SourceModule.expr representing an application of jTYPE_OTHER_NUMBER
		 */
		public static final SourceModel.Expr jTYPE_OTHER_NUMBER() {
			return SourceModel.Expr.Var.make(Functions.jTYPE_OTHER_NUMBER);
		}

		/**
		 * Name binding for function: jTYPE_OTHER_NUMBER.
		 * @see #jTYPE_OTHER_NUMBER()
		 */
		public static final QualifiedName jTYPE_OTHER_NUMBER = 
			QualifiedName.make(
				CAL_Char_internal.MODULE_NAME, 
				"jTYPE_OTHER_NUMBER");

		/**
		 * Helper binding method for function: jTYPE_OTHER_PUNCTUATION. 
		 * @return the SourceModule.expr representing an application of jTYPE_OTHER_PUNCTUATION
		 */
		public static final SourceModel.Expr jTYPE_OTHER_PUNCTUATION() {
			return SourceModel.Expr.Var.make(Functions.jTYPE_OTHER_PUNCTUATION);
		}

		/**
		 * Name binding for function: jTYPE_OTHER_PUNCTUATION.
		 * @see #jTYPE_OTHER_PUNCTUATION()
		 */
		public static final QualifiedName jTYPE_OTHER_PUNCTUATION = 
			QualifiedName.make(
				CAL_Char_internal.MODULE_NAME, 
				"jTYPE_OTHER_PUNCTUATION");

		/**
		 * Helper binding method for function: jTYPE_OTHER_SYMBOL. 
		 * @return the SourceModule.expr representing an application of jTYPE_OTHER_SYMBOL
		 */
		public static final SourceModel.Expr jTYPE_OTHER_SYMBOL() {
			return SourceModel.Expr.Var.make(Functions.jTYPE_OTHER_SYMBOL);
		}

		/**
		 * Name binding for function: jTYPE_OTHER_SYMBOL.
		 * @see #jTYPE_OTHER_SYMBOL()
		 */
		public static final QualifiedName jTYPE_OTHER_SYMBOL = 
			QualifiedName.make(
				CAL_Char_internal.MODULE_NAME, 
				"jTYPE_OTHER_SYMBOL");

		/**
		 * Helper binding method for function: jTYPE_PARAGRAPH_SEPARATOR. 
		 * @return the SourceModule.expr representing an application of jTYPE_PARAGRAPH_SEPARATOR
		 */
		public static final SourceModel.Expr jTYPE_PARAGRAPH_SEPARATOR() {
			return 
				SourceModel.Expr.Var.make(Functions.jTYPE_PARAGRAPH_SEPARATOR);
		}

		/**
		 * Name binding for function: jTYPE_PARAGRAPH_SEPARATOR.
		 * @see #jTYPE_PARAGRAPH_SEPARATOR()
		 */
		public static final QualifiedName jTYPE_PARAGRAPH_SEPARATOR = 
			QualifiedName.make(
				CAL_Char_internal.MODULE_NAME, 
				"jTYPE_PARAGRAPH_SEPARATOR");

		/**
		 * Helper binding method for function: jTYPE_PRIVATE_USE. 
		 * @return the SourceModule.expr representing an application of jTYPE_PRIVATE_USE
		 */
		public static final SourceModel.Expr jTYPE_PRIVATE_USE() {
			return SourceModel.Expr.Var.make(Functions.jTYPE_PRIVATE_USE);
		}

		/**
		 * Name binding for function: jTYPE_PRIVATE_USE.
		 * @see #jTYPE_PRIVATE_USE()
		 */
		public static final QualifiedName jTYPE_PRIVATE_USE = 
			QualifiedName.make(
				CAL_Char_internal.MODULE_NAME, 
				"jTYPE_PRIVATE_USE");

		/**
		 * Helper binding method for function: jTYPE_SPACE_SEPARATOR. 
		 * @return the SourceModule.expr representing an application of jTYPE_SPACE_SEPARATOR
		 */
		public static final SourceModel.Expr jTYPE_SPACE_SEPARATOR() {
			return SourceModel.Expr.Var.make(Functions.jTYPE_SPACE_SEPARATOR);
		}

		/**
		 * Name binding for function: jTYPE_SPACE_SEPARATOR.
		 * @see #jTYPE_SPACE_SEPARATOR()
		 */
		public static final QualifiedName jTYPE_SPACE_SEPARATOR = 
			QualifiedName.make(
				CAL_Char_internal.MODULE_NAME, 
				"jTYPE_SPACE_SEPARATOR");

		/**
		 * Helper binding method for function: jTYPE_START_PUNCTUATION. 
		 * @return the SourceModule.expr representing an application of jTYPE_START_PUNCTUATION
		 */
		public static final SourceModel.Expr jTYPE_START_PUNCTUATION() {
			return SourceModel.Expr.Var.make(Functions.jTYPE_START_PUNCTUATION);
		}

		/**
		 * Name binding for function: jTYPE_START_PUNCTUATION.
		 * @see #jTYPE_START_PUNCTUATION()
		 */
		public static final QualifiedName jTYPE_START_PUNCTUATION = 
			QualifiedName.make(
				CAL_Char_internal.MODULE_NAME, 
				"jTYPE_START_PUNCTUATION");

		/**
		 * Helper binding method for function: jTYPE_SURROGATE. 
		 * @return the SourceModule.expr representing an application of jTYPE_SURROGATE
		 */
		public static final SourceModel.Expr jTYPE_SURROGATE() {
			return SourceModel.Expr.Var.make(Functions.jTYPE_SURROGATE);
		}

		/**
		 * Name binding for function: jTYPE_SURROGATE.
		 * @see #jTYPE_SURROGATE()
		 */
		public static final QualifiedName jTYPE_SURROGATE = 
			QualifiedName.make(CAL_Char_internal.MODULE_NAME, "jTYPE_SURROGATE");

		/**
		 * Helper binding method for function: jTYPE_TITLECASE_LETTER. 
		 * @return the SourceModule.expr representing an application of jTYPE_TITLECASE_LETTER
		 */
		public static final SourceModel.Expr jTYPE_TITLECASE_LETTER() {
			return SourceModel.Expr.Var.make(Functions.jTYPE_TITLECASE_LETTER);
		}

		/**
		 * Name binding for function: jTYPE_TITLECASE_LETTER.
		 * @see #jTYPE_TITLECASE_LETTER()
		 */
		public static final QualifiedName jTYPE_TITLECASE_LETTER = 
			QualifiedName.make(
				CAL_Char_internal.MODULE_NAME, 
				"jTYPE_TITLECASE_LETTER");

		/**
		 * Helper binding method for function: jTYPE_UNASSIGNED. 
		 * @return the SourceModule.expr representing an application of jTYPE_UNASSIGNED
		 */
		public static final SourceModel.Expr jTYPE_UNASSIGNED() {
			return SourceModel.Expr.Var.make(Functions.jTYPE_UNASSIGNED);
		}

		/**
		 * Name binding for function: jTYPE_UNASSIGNED.
		 * @see #jTYPE_UNASSIGNED()
		 */
		public static final QualifiedName jTYPE_UNASSIGNED = 
			QualifiedName.make(
				CAL_Char_internal.MODULE_NAME, 
				"jTYPE_UNASSIGNED");

		/**
		 * Helper binding method for function: jTYPE_UPPERCASE_LETTER. 
		 * @return the SourceModule.expr representing an application of jTYPE_UPPERCASE_LETTER
		 */
		public static final SourceModel.Expr jTYPE_UPPERCASE_LETTER() {
			return SourceModel.Expr.Var.make(Functions.jTYPE_UPPERCASE_LETTER);
		}

		/**
		 * Name binding for function: jTYPE_UPPERCASE_LETTER.
		 * @see #jTYPE_UPPERCASE_LETTER()
		 */
		public static final QualifiedName jTYPE_UPPERCASE_LETTER = 
			QualifiedName.make(
				CAL_Char_internal.MODULE_NAME, 
				"jTYPE_UPPERCASE_LETTER");

		/**
		 * 
		 * @param charValue (CAL type: <code>Cal.Core.Prelude.Char</code>)
		 *          <code>Cal.Core.Prelude.Char</code> to classify
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          an Int value that can be translated to a Type value by <code>Cal.Core.Char.intToType</code>
		 */
		public static final SourceModel.Expr jType(SourceModel.Expr charValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jType), charValue});
		}

		/**
		 * @see #jType(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param charValue
		 * @return the SourceModel.Expr representing an application of jType
		 */
		public static final SourceModel.Expr jType(char charValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jType), SourceModel.Expr.makeCharValue(charValue)});
		}

		/**
		 * Name binding for function: jType.
		 * @see #jType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jType = 
			QualifiedName.make(CAL_Char_internal.MODULE_NAME, "jType");

		/**
		 * Helper binding method for function: outputDirectionality. 
		 * @param directionalityValue
		 * @return the SourceModule.expr representing an application of outputDirectionality
		 */
		public static final SourceModel.Expr outputDirectionality(SourceModel.Expr directionalityValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputDirectionality), directionalityValue});
		}

		/**
		 * Name binding for function: outputDirectionality.
		 * @see #outputDirectionality(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName outputDirectionality = 
			QualifiedName.make(
				CAL_Char_internal.MODULE_NAME, 
				"outputDirectionality");

		/**
		 * Helper binding method for function: outputType. 
		 * @param typeValue
		 * @return the SourceModule.expr representing an application of outputType
		 */
		public static final SourceModel.Expr outputType(SourceModel.Expr typeValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputType), typeValue});
		}

		/**
		 * Name binding for function: outputType.
		 * @see #outputType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName outputType = 
			QualifiedName.make(CAL_Char_internal.MODULE_NAME, "outputType");

		/**
		 * Helper binding method for function: typeExamples. 
		 * @return the SourceModule.expr representing an application of typeExamples
		 */
		public static final SourceModel.Expr typeExamples() {
			return SourceModel.Expr.Var.make(Functions.typeExamples);
		}

		/**
		 * Name binding for function: typeExamples.
		 * @see #typeExamples()
		 */
		public static final QualifiedName typeExamples = 
			QualifiedName.make(CAL_Char_internal.MODULE_NAME, "typeExamples");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = 205893345;

}
