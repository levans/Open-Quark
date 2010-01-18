/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_Char.java)
 * was generated from CAL module: Cal.Core.Char.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Core.Char module from Java code.
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
 * Defines many useful functions for the <code>Cal.Core.Prelude.Char</code> type. Note that the <code>Cal.Core.Prelude.Char</code>
 * type itself is defined in the <code>Cal.Core.Prelude</code> module due to the fact that it is supported via built-in notation
 * for <code>Cal.Core.Prelude.Char</code> literals.
 * @author Bo Ilic
 */
public final class CAL_Char {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Core.Char");

	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Core.Char module.
	 */
	public static final class TypeConstructors {
		/**
		 * Symbolic constants that represent the java.lang.Character Unicode directionality
		 * constants.
		 */
		public static final QualifiedName Directionality = 
			QualifiedName.make(CAL_Char.MODULE_NAME, "Directionality");

		/**
		 * The <code>Char.Type</code> enumeration provides symbolic constants that represent the
		 * Unicode category of a specified character.
		 */
		public static final QualifiedName Type = 
			QualifiedName.make(CAL_Char.MODULE_NAME, "Type");

	}
	/**
	 * This inner class (DataConstructors) contains constants
	 * and methods related to binding to CAL DataConstructors in the Cal.Core.Char module.
	 */
	public static final class DataConstructors {
		/*
		 * DataConstructors for the Cal.Core.Char.Directionality data type.
		 */

		/**
		 * Undefined bidirectional character type.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr DIRECTIONALITY_UNDEFINED() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.DIRECTIONALITY_UNDEFINED);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Char.DIRECTIONALITY_UNDEFINED.
		 * @see #DIRECTIONALITY_UNDEFINED()
		 */
		public static final QualifiedName DIRECTIONALITY_UNDEFINED = 
			QualifiedName.make(CAL_Char.MODULE_NAME, "DIRECTIONALITY_UNDEFINED");

		/**
		 * Ordinal of DataConstructor Cal.Core.Char.DIRECTIONALITY_UNDEFINED.
		 * @see #DIRECTIONALITY_UNDEFINED()
		 */
		public static final int DIRECTIONALITY_UNDEFINED_ordinal = 0;

		/**
		 * Strong bidirectional character type "L" in the Unicode specification.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr DIRECTIONALITY_LEFT_TO_RIGHT() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.DIRECTIONALITY_LEFT_TO_RIGHT);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Char.DIRECTIONALITY_LEFT_TO_RIGHT.
		 * @see #DIRECTIONALITY_LEFT_TO_RIGHT()
		 */
		public static final QualifiedName DIRECTIONALITY_LEFT_TO_RIGHT = 
			QualifiedName.make(
				CAL_Char.MODULE_NAME, 
				"DIRECTIONALITY_LEFT_TO_RIGHT");

		/**
		 * Ordinal of DataConstructor Cal.Core.Char.DIRECTIONALITY_LEFT_TO_RIGHT.
		 * @see #DIRECTIONALITY_LEFT_TO_RIGHT()
		 */
		public static final int DIRECTIONALITY_LEFT_TO_RIGHT_ordinal = 1;

		/**
		 * Strong bidirectional character type "R" in the Unicode specification.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr DIRECTIONALITY_RIGHT_TO_LEFT() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.DIRECTIONALITY_RIGHT_TO_LEFT);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Char.DIRECTIONALITY_RIGHT_TO_LEFT.
		 * @see #DIRECTIONALITY_RIGHT_TO_LEFT()
		 */
		public static final QualifiedName DIRECTIONALITY_RIGHT_TO_LEFT = 
			QualifiedName.make(
				CAL_Char.MODULE_NAME, 
				"DIRECTIONALITY_RIGHT_TO_LEFT");

		/**
		 * Ordinal of DataConstructor Cal.Core.Char.DIRECTIONALITY_RIGHT_TO_LEFT.
		 * @see #DIRECTIONALITY_RIGHT_TO_LEFT()
		 */
		public static final int DIRECTIONALITY_RIGHT_TO_LEFT_ordinal = 2;

		/**
		 * Strong bidirectional character type "AL" in the Unicode specification.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Char.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC.
		 * @see #DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC()
		 */
		public static final QualifiedName DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC = 
			QualifiedName.make(
				CAL_Char.MODULE_NAME, 
				"DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC");

		/**
		 * Ordinal of DataConstructor Cal.Core.Char.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC.
		 * @see #DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC()
		 */
		public static final int DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC_ordinal = 3;

		/**
		 * Weak bidirectional character type "EN" in the Unicode specification.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr DIRECTIONALITY_EUROPEAN_NUMBER() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.DIRECTIONALITY_EUROPEAN_NUMBER);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Char.DIRECTIONALITY_EUROPEAN_NUMBER.
		 * @see #DIRECTIONALITY_EUROPEAN_NUMBER()
		 */
		public static final QualifiedName DIRECTIONALITY_EUROPEAN_NUMBER = 
			QualifiedName.make(
				CAL_Char.MODULE_NAME, 
				"DIRECTIONALITY_EUROPEAN_NUMBER");

		/**
		 * Ordinal of DataConstructor Cal.Core.Char.DIRECTIONALITY_EUROPEAN_NUMBER.
		 * @see #DIRECTIONALITY_EUROPEAN_NUMBER()
		 */
		public static final int DIRECTIONALITY_EUROPEAN_NUMBER_ordinal = 4;

		/**
		 * Weak bidirectional character type "ES" in the Unicode specification.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr DIRECTIONALITY_EUROPEAN_NUMBER_SEPARATOR() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.DIRECTIONALITY_EUROPEAN_NUMBER_SEPARATOR);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Char.DIRECTIONALITY_EUROPEAN_NUMBER_SEPARATOR.
		 * @see #DIRECTIONALITY_EUROPEAN_NUMBER_SEPARATOR()
		 */
		public static final QualifiedName DIRECTIONALITY_EUROPEAN_NUMBER_SEPARATOR = 
			QualifiedName.make(
				CAL_Char.MODULE_NAME, 
				"DIRECTIONALITY_EUROPEAN_NUMBER_SEPARATOR");

		/**
		 * Ordinal of DataConstructor Cal.Core.Char.DIRECTIONALITY_EUROPEAN_NUMBER_SEPARATOR.
		 * @see #DIRECTIONALITY_EUROPEAN_NUMBER_SEPARATOR()
		 */
		public static final int DIRECTIONALITY_EUROPEAN_NUMBER_SEPARATOR_ordinal = 
			5;

		/**
		 * Weak bidirectional character type "ET" in the Unicode specification.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr DIRECTIONALITY_EUROPEAN_NUMBER_TERMINATOR() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.DIRECTIONALITY_EUROPEAN_NUMBER_TERMINATOR);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Char.DIRECTIONALITY_EUROPEAN_NUMBER_TERMINATOR.
		 * @see #DIRECTIONALITY_EUROPEAN_NUMBER_TERMINATOR()
		 */
		public static final QualifiedName DIRECTIONALITY_EUROPEAN_NUMBER_TERMINATOR = 
			QualifiedName.make(
				CAL_Char.MODULE_NAME, 
				"DIRECTIONALITY_EUROPEAN_NUMBER_TERMINATOR");

		/**
		 * Ordinal of DataConstructor Cal.Core.Char.DIRECTIONALITY_EUROPEAN_NUMBER_TERMINATOR.
		 * @see #DIRECTIONALITY_EUROPEAN_NUMBER_TERMINATOR()
		 */
		public static final int DIRECTIONALITY_EUROPEAN_NUMBER_TERMINATOR_ordinal = 
			6;

		/**
		 * Weak bidirectional character type "AN" in the Unicode specification.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr DIRECTIONALITY_ARABIC_NUMBER() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.DIRECTIONALITY_ARABIC_NUMBER);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Char.DIRECTIONALITY_ARABIC_NUMBER.
		 * @see #DIRECTIONALITY_ARABIC_NUMBER()
		 */
		public static final QualifiedName DIRECTIONALITY_ARABIC_NUMBER = 
			QualifiedName.make(
				CAL_Char.MODULE_NAME, 
				"DIRECTIONALITY_ARABIC_NUMBER");

		/**
		 * Ordinal of DataConstructor Cal.Core.Char.DIRECTIONALITY_ARABIC_NUMBER.
		 * @see #DIRECTIONALITY_ARABIC_NUMBER()
		 */
		public static final int DIRECTIONALITY_ARABIC_NUMBER_ordinal = 7;

		/**
		 * Weak bidirectional character type "CS" in the Unicode specification.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr DIRECTIONALITY_COMMON_NUMBER_SEPARATOR() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.DIRECTIONALITY_COMMON_NUMBER_SEPARATOR);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Char.DIRECTIONALITY_COMMON_NUMBER_SEPARATOR.
		 * @see #DIRECTIONALITY_COMMON_NUMBER_SEPARATOR()
		 */
		public static final QualifiedName DIRECTIONALITY_COMMON_NUMBER_SEPARATOR = 
			QualifiedName.make(
				CAL_Char.MODULE_NAME, 
				"DIRECTIONALITY_COMMON_NUMBER_SEPARATOR");

		/**
		 * Ordinal of DataConstructor Cal.Core.Char.DIRECTIONALITY_COMMON_NUMBER_SEPARATOR.
		 * @see #DIRECTIONALITY_COMMON_NUMBER_SEPARATOR()
		 */
		public static final int DIRECTIONALITY_COMMON_NUMBER_SEPARATOR_ordinal = 
			8;

		/**
		 * Weak bidirectional character type "NSM" in the Unicode specification.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr DIRECTIONALITY_NONSPACING_MARK() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.DIRECTIONALITY_NONSPACING_MARK);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Char.DIRECTIONALITY_NONSPACING_MARK.
		 * @see #DIRECTIONALITY_NONSPACING_MARK()
		 */
		public static final QualifiedName DIRECTIONALITY_NONSPACING_MARK = 
			QualifiedName.make(
				CAL_Char.MODULE_NAME, 
				"DIRECTIONALITY_NONSPACING_MARK");

		/**
		 * Ordinal of DataConstructor Cal.Core.Char.DIRECTIONALITY_NONSPACING_MARK.
		 * @see #DIRECTIONALITY_NONSPACING_MARK()
		 */
		public static final int DIRECTIONALITY_NONSPACING_MARK_ordinal = 9;

		/**
		 * Weak bidirectional character type "BN" in the Unicode specification.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr DIRECTIONALITY_BOUNDARY_NEUTRAL() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.DIRECTIONALITY_BOUNDARY_NEUTRAL);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Char.DIRECTIONALITY_BOUNDARY_NEUTRAL.
		 * @see #DIRECTIONALITY_BOUNDARY_NEUTRAL()
		 */
		public static final QualifiedName DIRECTIONALITY_BOUNDARY_NEUTRAL = 
			QualifiedName.make(
				CAL_Char.MODULE_NAME, 
				"DIRECTIONALITY_BOUNDARY_NEUTRAL");

		/**
		 * Ordinal of DataConstructor Cal.Core.Char.DIRECTIONALITY_BOUNDARY_NEUTRAL.
		 * @see #DIRECTIONALITY_BOUNDARY_NEUTRAL()
		 */
		public static final int DIRECTIONALITY_BOUNDARY_NEUTRAL_ordinal = 10;

		/**
		 * Neutral bidirectional character type "B" in the Unicode specification.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr DIRECTIONALITY_PARAGRAPH_SEPARATOR() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.DIRECTIONALITY_PARAGRAPH_SEPARATOR);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Char.DIRECTIONALITY_PARAGRAPH_SEPARATOR.
		 * @see #DIRECTIONALITY_PARAGRAPH_SEPARATOR()
		 */
		public static final QualifiedName DIRECTIONALITY_PARAGRAPH_SEPARATOR = 
			QualifiedName.make(
				CAL_Char.MODULE_NAME, 
				"DIRECTIONALITY_PARAGRAPH_SEPARATOR");

		/**
		 * Ordinal of DataConstructor Cal.Core.Char.DIRECTIONALITY_PARAGRAPH_SEPARATOR.
		 * @see #DIRECTIONALITY_PARAGRAPH_SEPARATOR()
		 */
		public static final int DIRECTIONALITY_PARAGRAPH_SEPARATOR_ordinal = 11;

		/**
		 * Neutral bidirectional character type "S" in the Unicode specification.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr DIRECTIONALITY_SEGMENT_SEPARATOR() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.DIRECTIONALITY_SEGMENT_SEPARATOR);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Char.DIRECTIONALITY_SEGMENT_SEPARATOR.
		 * @see #DIRECTIONALITY_SEGMENT_SEPARATOR()
		 */
		public static final QualifiedName DIRECTIONALITY_SEGMENT_SEPARATOR = 
			QualifiedName.make(
				CAL_Char.MODULE_NAME, 
				"DIRECTIONALITY_SEGMENT_SEPARATOR");

		/**
		 * Ordinal of DataConstructor Cal.Core.Char.DIRECTIONALITY_SEGMENT_SEPARATOR.
		 * @see #DIRECTIONALITY_SEGMENT_SEPARATOR()
		 */
		public static final int DIRECTIONALITY_SEGMENT_SEPARATOR_ordinal = 12;

		/**
		 * Neutral bidirectional character type "WS" in the Unicode specification.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr DIRECTIONALITY_WHITESPACE() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.DIRECTIONALITY_WHITESPACE);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Char.DIRECTIONALITY_WHITESPACE.
		 * @see #DIRECTIONALITY_WHITESPACE()
		 */
		public static final QualifiedName DIRECTIONALITY_WHITESPACE = 
			QualifiedName.make(
				CAL_Char.MODULE_NAME, 
				"DIRECTIONALITY_WHITESPACE");

		/**
		 * Ordinal of DataConstructor Cal.Core.Char.DIRECTIONALITY_WHITESPACE.
		 * @see #DIRECTIONALITY_WHITESPACE()
		 */
		public static final int DIRECTIONALITY_WHITESPACE_ordinal = 13;

		/**
		 * Neutral bidirectional character type "ON" in the Unicode specification.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr DIRECTIONALITY_OTHER_NEUTRALS() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.DIRECTIONALITY_OTHER_NEUTRALS);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Char.DIRECTIONALITY_OTHER_NEUTRALS.
		 * @see #DIRECTIONALITY_OTHER_NEUTRALS()
		 */
		public static final QualifiedName DIRECTIONALITY_OTHER_NEUTRALS = 
			QualifiedName.make(
				CAL_Char.MODULE_NAME, 
				"DIRECTIONALITY_OTHER_NEUTRALS");

		/**
		 * Ordinal of DataConstructor Cal.Core.Char.DIRECTIONALITY_OTHER_NEUTRALS.
		 * @see #DIRECTIONALITY_OTHER_NEUTRALS()
		 */
		public static final int DIRECTIONALITY_OTHER_NEUTRALS_ordinal = 14;

		/**
		 * Strong bidirectional character type "LRE" in the Unicode specification.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr DIRECTIONALITY_LEFT_TO_RIGHT_EMBEDDING() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.DIRECTIONALITY_LEFT_TO_RIGHT_EMBEDDING);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Char.DIRECTIONALITY_LEFT_TO_RIGHT_EMBEDDING.
		 * @see #DIRECTIONALITY_LEFT_TO_RIGHT_EMBEDDING()
		 */
		public static final QualifiedName DIRECTIONALITY_LEFT_TO_RIGHT_EMBEDDING = 
			QualifiedName.make(
				CAL_Char.MODULE_NAME, 
				"DIRECTIONALITY_LEFT_TO_RIGHT_EMBEDDING");

		/**
		 * Ordinal of DataConstructor Cal.Core.Char.DIRECTIONALITY_LEFT_TO_RIGHT_EMBEDDING.
		 * @see #DIRECTIONALITY_LEFT_TO_RIGHT_EMBEDDING()
		 */
		public static final int DIRECTIONALITY_LEFT_TO_RIGHT_EMBEDDING_ordinal = 
			15;

		/**
		 * Strong bidirectional character type "LRO" in the Unicode specification.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr DIRECTIONALITY_LEFT_TO_RIGHT_OVERRIDE() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.DIRECTIONALITY_LEFT_TO_RIGHT_OVERRIDE);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Char.DIRECTIONALITY_LEFT_TO_RIGHT_OVERRIDE.
		 * @see #DIRECTIONALITY_LEFT_TO_RIGHT_OVERRIDE()
		 */
		public static final QualifiedName DIRECTIONALITY_LEFT_TO_RIGHT_OVERRIDE = 
			QualifiedName.make(
				CAL_Char.MODULE_NAME, 
				"DIRECTIONALITY_LEFT_TO_RIGHT_OVERRIDE");

		/**
		 * Ordinal of DataConstructor Cal.Core.Char.DIRECTIONALITY_LEFT_TO_RIGHT_OVERRIDE.
		 * @see #DIRECTIONALITY_LEFT_TO_RIGHT_OVERRIDE()
		 */
		public static final int DIRECTIONALITY_LEFT_TO_RIGHT_OVERRIDE_ordinal = 
			16;

		/**
		 * Strong bidirectional character type "RLE" in the Unicode specification.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr DIRECTIONALITY_RIGHT_TO_LEFT_EMBEDDING() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.DIRECTIONALITY_RIGHT_TO_LEFT_EMBEDDING);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Char.DIRECTIONALITY_RIGHT_TO_LEFT_EMBEDDING.
		 * @see #DIRECTIONALITY_RIGHT_TO_LEFT_EMBEDDING()
		 */
		public static final QualifiedName DIRECTIONALITY_RIGHT_TO_LEFT_EMBEDDING = 
			QualifiedName.make(
				CAL_Char.MODULE_NAME, 
				"DIRECTIONALITY_RIGHT_TO_LEFT_EMBEDDING");

		/**
		 * Ordinal of DataConstructor Cal.Core.Char.DIRECTIONALITY_RIGHT_TO_LEFT_EMBEDDING.
		 * @see #DIRECTIONALITY_RIGHT_TO_LEFT_EMBEDDING()
		 */
		public static final int DIRECTIONALITY_RIGHT_TO_LEFT_EMBEDDING_ordinal = 
			17;

		/**
		 * Strong bidirectional character type "RLO" in the Unicode specification.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr DIRECTIONALITY_RIGHT_TO_LEFT_OVERRIDE() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.DIRECTIONALITY_RIGHT_TO_LEFT_OVERRIDE);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Char.DIRECTIONALITY_RIGHT_TO_LEFT_OVERRIDE.
		 * @see #DIRECTIONALITY_RIGHT_TO_LEFT_OVERRIDE()
		 */
		public static final QualifiedName DIRECTIONALITY_RIGHT_TO_LEFT_OVERRIDE = 
			QualifiedName.make(
				CAL_Char.MODULE_NAME, 
				"DIRECTIONALITY_RIGHT_TO_LEFT_OVERRIDE");

		/**
		 * Ordinal of DataConstructor Cal.Core.Char.DIRECTIONALITY_RIGHT_TO_LEFT_OVERRIDE.
		 * @see #DIRECTIONALITY_RIGHT_TO_LEFT_OVERRIDE()
		 */
		public static final int DIRECTIONALITY_RIGHT_TO_LEFT_OVERRIDE_ordinal = 
			18;

		/**
		 * Weak bidirectional character type "PDF" in the Unicode specification.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr DIRECTIONALITY_POP_DIRECTIONAL_FORMAT() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.DIRECTIONALITY_POP_DIRECTIONAL_FORMAT);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Char.DIRECTIONALITY_POP_DIRECTIONAL_FORMAT.
		 * @see #DIRECTIONALITY_POP_DIRECTIONAL_FORMAT()
		 */
		public static final QualifiedName DIRECTIONALITY_POP_DIRECTIONAL_FORMAT = 
			QualifiedName.make(
				CAL_Char.MODULE_NAME, 
				"DIRECTIONALITY_POP_DIRECTIONAL_FORMAT");

		/**
		 * Ordinal of DataConstructor Cal.Core.Char.DIRECTIONALITY_POP_DIRECTIONAL_FORMAT.
		 * @see #DIRECTIONALITY_POP_DIRECTIONAL_FORMAT()
		 */
		public static final int DIRECTIONALITY_POP_DIRECTIONAL_FORMAT_ordinal = 
			19;

		/*
		 * DataConstructors for the Cal.Core.Char.Type data type.
		 */

		/**
		 * General category "Cn" in the Unicode specification.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr TYPE_UNASSIGNED() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.TYPE_UNASSIGNED);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Char.TYPE_UNASSIGNED.
		 * @see #TYPE_UNASSIGNED()
		 */
		public static final QualifiedName TYPE_UNASSIGNED = 
			QualifiedName.make(CAL_Char.MODULE_NAME, "TYPE_UNASSIGNED");

		/**
		 * Ordinal of DataConstructor Cal.Core.Char.TYPE_UNASSIGNED.
		 * @see #TYPE_UNASSIGNED()
		 */
		public static final int TYPE_UNASSIGNED_ordinal = 0;

		/**
		 * General category "Lu" in the Unicode specification.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr TYPE_UPPERCASE_LETTER() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.TYPE_UPPERCASE_LETTER);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Char.TYPE_UPPERCASE_LETTER.
		 * @see #TYPE_UPPERCASE_LETTER()
		 */
		public static final QualifiedName TYPE_UPPERCASE_LETTER = 
			QualifiedName.make(CAL_Char.MODULE_NAME, "TYPE_UPPERCASE_LETTER");

		/**
		 * Ordinal of DataConstructor Cal.Core.Char.TYPE_UPPERCASE_LETTER.
		 * @see #TYPE_UPPERCASE_LETTER()
		 */
		public static final int TYPE_UPPERCASE_LETTER_ordinal = 1;

		/**
		 * General category "Ll" in the Unicode specification.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr TYPE_LOWERCASE_LETTER() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.TYPE_LOWERCASE_LETTER);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Char.TYPE_LOWERCASE_LETTER.
		 * @see #TYPE_LOWERCASE_LETTER()
		 */
		public static final QualifiedName TYPE_LOWERCASE_LETTER = 
			QualifiedName.make(CAL_Char.MODULE_NAME, "TYPE_LOWERCASE_LETTER");

		/**
		 * Ordinal of DataConstructor Cal.Core.Char.TYPE_LOWERCASE_LETTER.
		 * @see #TYPE_LOWERCASE_LETTER()
		 */
		public static final int TYPE_LOWERCASE_LETTER_ordinal = 2;

		/**
		 * General category "Lt" in the Unicode specification.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr TYPE_TITLECASE_LETTER() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.TYPE_TITLECASE_LETTER);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Char.TYPE_TITLECASE_LETTER.
		 * @see #TYPE_TITLECASE_LETTER()
		 */
		public static final QualifiedName TYPE_TITLECASE_LETTER = 
			QualifiedName.make(CAL_Char.MODULE_NAME, "TYPE_TITLECASE_LETTER");

		/**
		 * Ordinal of DataConstructor Cal.Core.Char.TYPE_TITLECASE_LETTER.
		 * @see #TYPE_TITLECASE_LETTER()
		 */
		public static final int TYPE_TITLECASE_LETTER_ordinal = 3;

		/**
		 * General category "Lm" in the Unicode specification.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr TYPE_MODIFIER_LETTER() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.TYPE_MODIFIER_LETTER);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Char.TYPE_MODIFIER_LETTER.
		 * @see #TYPE_MODIFIER_LETTER()
		 */
		public static final QualifiedName TYPE_MODIFIER_LETTER = 
			QualifiedName.make(CAL_Char.MODULE_NAME, "TYPE_MODIFIER_LETTER");

		/**
		 * Ordinal of DataConstructor Cal.Core.Char.TYPE_MODIFIER_LETTER.
		 * @see #TYPE_MODIFIER_LETTER()
		 */
		public static final int TYPE_MODIFIER_LETTER_ordinal = 4;

		/**
		 * General category "Lo" in the Unicode specification.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr TYPE_OTHER_LETTER() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.TYPE_OTHER_LETTER);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Char.TYPE_OTHER_LETTER.
		 * @see #TYPE_OTHER_LETTER()
		 */
		public static final QualifiedName TYPE_OTHER_LETTER = 
			QualifiedName.make(CAL_Char.MODULE_NAME, "TYPE_OTHER_LETTER");

		/**
		 * Ordinal of DataConstructor Cal.Core.Char.TYPE_OTHER_LETTER.
		 * @see #TYPE_OTHER_LETTER()
		 */
		public static final int TYPE_OTHER_LETTER_ordinal = 5;

		/**
		 * General category "Mn" in the Unicode specification.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr TYPE_NON_SPACING_MARK() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.TYPE_NON_SPACING_MARK);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Char.TYPE_NON_SPACING_MARK.
		 * @see #TYPE_NON_SPACING_MARK()
		 */
		public static final QualifiedName TYPE_NON_SPACING_MARK = 
			QualifiedName.make(CAL_Char.MODULE_NAME, "TYPE_NON_SPACING_MARK");

		/**
		 * Ordinal of DataConstructor Cal.Core.Char.TYPE_NON_SPACING_MARK.
		 * @see #TYPE_NON_SPACING_MARK()
		 */
		public static final int TYPE_NON_SPACING_MARK_ordinal = 6;

		/**
		 * General category "Me" in the Unicode specification.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr TYPE_ENCLOSING_MARK() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.TYPE_ENCLOSING_MARK);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Char.TYPE_ENCLOSING_MARK.
		 * @see #TYPE_ENCLOSING_MARK()
		 */
		public static final QualifiedName TYPE_ENCLOSING_MARK = 
			QualifiedName.make(CAL_Char.MODULE_NAME, "TYPE_ENCLOSING_MARK");

		/**
		 * Ordinal of DataConstructor Cal.Core.Char.TYPE_ENCLOSING_MARK.
		 * @see #TYPE_ENCLOSING_MARK()
		 */
		public static final int TYPE_ENCLOSING_MARK_ordinal = 7;

		/**
		 * General category "Mc" in the Unicode specification.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr TYPE_COMBINING_SPACING_MARK() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.TYPE_COMBINING_SPACING_MARK);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Char.TYPE_COMBINING_SPACING_MARK.
		 * @see #TYPE_COMBINING_SPACING_MARK()
		 */
		public static final QualifiedName TYPE_COMBINING_SPACING_MARK = 
			QualifiedName.make(
				CAL_Char.MODULE_NAME, 
				"TYPE_COMBINING_SPACING_MARK");

		/**
		 * Ordinal of DataConstructor Cal.Core.Char.TYPE_COMBINING_SPACING_MARK.
		 * @see #TYPE_COMBINING_SPACING_MARK()
		 */
		public static final int TYPE_COMBINING_SPACING_MARK_ordinal = 8;

		/**
		 * General category "Nd" in the Unicode specification.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr TYPE_DECIMAL_DIGIT_NUMBER() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.TYPE_DECIMAL_DIGIT_NUMBER);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Char.TYPE_DECIMAL_DIGIT_NUMBER.
		 * @see #TYPE_DECIMAL_DIGIT_NUMBER()
		 */
		public static final QualifiedName TYPE_DECIMAL_DIGIT_NUMBER = 
			QualifiedName.make(
				CAL_Char.MODULE_NAME, 
				"TYPE_DECIMAL_DIGIT_NUMBER");

		/**
		 * Ordinal of DataConstructor Cal.Core.Char.TYPE_DECIMAL_DIGIT_NUMBER.
		 * @see #TYPE_DECIMAL_DIGIT_NUMBER()
		 */
		public static final int TYPE_DECIMAL_DIGIT_NUMBER_ordinal = 9;

		/**
		 * General category "Nl" in the Unicode specification.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr TYPE_LETTER_NUMBER() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.TYPE_LETTER_NUMBER);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Char.TYPE_LETTER_NUMBER.
		 * @see #TYPE_LETTER_NUMBER()
		 */
		public static final QualifiedName TYPE_LETTER_NUMBER = 
			QualifiedName.make(CAL_Char.MODULE_NAME, "TYPE_LETTER_NUMBER");

		/**
		 * Ordinal of DataConstructor Cal.Core.Char.TYPE_LETTER_NUMBER.
		 * @see #TYPE_LETTER_NUMBER()
		 */
		public static final int TYPE_LETTER_NUMBER_ordinal = 10;

		/**
		 * General category "No" in the Unicode specification.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr TYPE_OTHER_NUMBER() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.TYPE_OTHER_NUMBER);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Char.TYPE_OTHER_NUMBER.
		 * @see #TYPE_OTHER_NUMBER()
		 */
		public static final QualifiedName TYPE_OTHER_NUMBER = 
			QualifiedName.make(CAL_Char.MODULE_NAME, "TYPE_OTHER_NUMBER");

		/**
		 * Ordinal of DataConstructor Cal.Core.Char.TYPE_OTHER_NUMBER.
		 * @see #TYPE_OTHER_NUMBER()
		 */
		public static final int TYPE_OTHER_NUMBER_ordinal = 11;

		/**
		 * General category "Zs" in the Unicode specification.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr TYPE_SPACE_SEPARATOR() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.TYPE_SPACE_SEPARATOR);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Char.TYPE_SPACE_SEPARATOR.
		 * @see #TYPE_SPACE_SEPARATOR()
		 */
		public static final QualifiedName TYPE_SPACE_SEPARATOR = 
			QualifiedName.make(CAL_Char.MODULE_NAME, "TYPE_SPACE_SEPARATOR");

		/**
		 * Ordinal of DataConstructor Cal.Core.Char.TYPE_SPACE_SEPARATOR.
		 * @see #TYPE_SPACE_SEPARATOR()
		 */
		public static final int TYPE_SPACE_SEPARATOR_ordinal = 12;

		/**
		 * General category "Zl" in the Unicode specification.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr TYPE_LINE_SEPARATOR() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.TYPE_LINE_SEPARATOR);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Char.TYPE_LINE_SEPARATOR.
		 * @see #TYPE_LINE_SEPARATOR()
		 */
		public static final QualifiedName TYPE_LINE_SEPARATOR = 
			QualifiedName.make(CAL_Char.MODULE_NAME, "TYPE_LINE_SEPARATOR");

		/**
		 * Ordinal of DataConstructor Cal.Core.Char.TYPE_LINE_SEPARATOR.
		 * @see #TYPE_LINE_SEPARATOR()
		 */
		public static final int TYPE_LINE_SEPARATOR_ordinal = 13;

		/**
		 * General category "Zp" in the Unicode specification.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr TYPE_PARAGRAPH_SEPARATOR() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.TYPE_PARAGRAPH_SEPARATOR);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Char.TYPE_PARAGRAPH_SEPARATOR.
		 * @see #TYPE_PARAGRAPH_SEPARATOR()
		 */
		public static final QualifiedName TYPE_PARAGRAPH_SEPARATOR = 
			QualifiedName.make(CAL_Char.MODULE_NAME, "TYPE_PARAGRAPH_SEPARATOR");

		/**
		 * Ordinal of DataConstructor Cal.Core.Char.TYPE_PARAGRAPH_SEPARATOR.
		 * @see #TYPE_PARAGRAPH_SEPARATOR()
		 */
		public static final int TYPE_PARAGRAPH_SEPARATOR_ordinal = 14;

		/**
		 * General category "Cc" in the Unicode specification.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr TYPE_CONTROL() {
			return SourceModel.Expr.DataCons.make(DataConstructors.TYPE_CONTROL);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Char.TYPE_CONTROL.
		 * @see #TYPE_CONTROL()
		 */
		public static final QualifiedName TYPE_CONTROL = 
			QualifiedName.make(CAL_Char.MODULE_NAME, "TYPE_CONTROL");

		/**
		 * Ordinal of DataConstructor Cal.Core.Char.TYPE_CONTROL.
		 * @see #TYPE_CONTROL()
		 */
		public static final int TYPE_CONTROL_ordinal = 15;

		/**
		 * General category "Cf" in the Unicode specification.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr TYPE_FORMAT() {
			return SourceModel.Expr.DataCons.make(DataConstructors.TYPE_FORMAT);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Char.TYPE_FORMAT.
		 * @see #TYPE_FORMAT()
		 */
		public static final QualifiedName TYPE_FORMAT = 
			QualifiedName.make(CAL_Char.MODULE_NAME, "TYPE_FORMAT");

		/**
		 * Ordinal of DataConstructor Cal.Core.Char.TYPE_FORMAT.
		 * @see #TYPE_FORMAT()
		 */
		public static final int TYPE_FORMAT_ordinal = 16;

		/**
		 * General category "Co" in the Unicode specification.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr TYPE_PRIVATE_USE() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.TYPE_PRIVATE_USE);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Char.TYPE_PRIVATE_USE.
		 * @see #TYPE_PRIVATE_USE()
		 */
		public static final QualifiedName TYPE_PRIVATE_USE = 
			QualifiedName.make(CAL_Char.MODULE_NAME, "TYPE_PRIVATE_USE");

		/**
		 * Ordinal of DataConstructor Cal.Core.Char.TYPE_PRIVATE_USE.
		 * @see #TYPE_PRIVATE_USE()
		 */
		public static final int TYPE_PRIVATE_USE_ordinal = 17;

		/**
		 * General category "Cs" in the Unicode specification.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr TYPE_SURROGATE() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.TYPE_SURROGATE);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Char.TYPE_SURROGATE.
		 * @see #TYPE_SURROGATE()
		 */
		public static final QualifiedName TYPE_SURROGATE = 
			QualifiedName.make(CAL_Char.MODULE_NAME, "TYPE_SURROGATE");

		/**
		 * Ordinal of DataConstructor Cal.Core.Char.TYPE_SURROGATE.
		 * @see #TYPE_SURROGATE()
		 */
		public static final int TYPE_SURROGATE_ordinal = 18;

		/**
		 * General category "Pd" in the Unicode specification.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr TYPE_DASH_PUNCTUATION() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.TYPE_DASH_PUNCTUATION);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Char.TYPE_DASH_PUNCTUATION.
		 * @see #TYPE_DASH_PUNCTUATION()
		 */
		public static final QualifiedName TYPE_DASH_PUNCTUATION = 
			QualifiedName.make(CAL_Char.MODULE_NAME, "TYPE_DASH_PUNCTUATION");

		/**
		 * Ordinal of DataConstructor Cal.Core.Char.TYPE_DASH_PUNCTUATION.
		 * @see #TYPE_DASH_PUNCTUATION()
		 */
		public static final int TYPE_DASH_PUNCTUATION_ordinal = 19;

		/**
		 * General category "Ps" in the Unicode specification.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr TYPE_START_PUNCTUATION() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.TYPE_START_PUNCTUATION);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Char.TYPE_START_PUNCTUATION.
		 * @see #TYPE_START_PUNCTUATION()
		 */
		public static final QualifiedName TYPE_START_PUNCTUATION = 
			QualifiedName.make(CAL_Char.MODULE_NAME, "TYPE_START_PUNCTUATION");

		/**
		 * Ordinal of DataConstructor Cal.Core.Char.TYPE_START_PUNCTUATION.
		 * @see #TYPE_START_PUNCTUATION()
		 */
		public static final int TYPE_START_PUNCTUATION_ordinal = 20;

		/**
		 * General category "Pe" in the Unicode specification.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr TYPE_END_PUNCTUATION() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.TYPE_END_PUNCTUATION);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Char.TYPE_END_PUNCTUATION.
		 * @see #TYPE_END_PUNCTUATION()
		 */
		public static final QualifiedName TYPE_END_PUNCTUATION = 
			QualifiedName.make(CAL_Char.MODULE_NAME, "TYPE_END_PUNCTUATION");

		/**
		 * Ordinal of DataConstructor Cal.Core.Char.TYPE_END_PUNCTUATION.
		 * @see #TYPE_END_PUNCTUATION()
		 */
		public static final int TYPE_END_PUNCTUATION_ordinal = 21;

		/**
		 * General category "Pc" in the Unicode specification.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr TYPE_CONNECTOR_PUNCTUATION() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.TYPE_CONNECTOR_PUNCTUATION);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Char.TYPE_CONNECTOR_PUNCTUATION.
		 * @see #TYPE_CONNECTOR_PUNCTUATION()
		 */
		public static final QualifiedName TYPE_CONNECTOR_PUNCTUATION = 
			QualifiedName.make(
				CAL_Char.MODULE_NAME, 
				"TYPE_CONNECTOR_PUNCTUATION");

		/**
		 * Ordinal of DataConstructor Cal.Core.Char.TYPE_CONNECTOR_PUNCTUATION.
		 * @see #TYPE_CONNECTOR_PUNCTUATION()
		 */
		public static final int TYPE_CONNECTOR_PUNCTUATION_ordinal = 22;

		/**
		 * General category "Po" in the Unicode specification.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr TYPE_OTHER_PUNCTUATION() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.TYPE_OTHER_PUNCTUATION);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Char.TYPE_OTHER_PUNCTUATION.
		 * @see #TYPE_OTHER_PUNCTUATION()
		 */
		public static final QualifiedName TYPE_OTHER_PUNCTUATION = 
			QualifiedName.make(CAL_Char.MODULE_NAME, "TYPE_OTHER_PUNCTUATION");

		/**
		 * Ordinal of DataConstructor Cal.Core.Char.TYPE_OTHER_PUNCTUATION.
		 * @see #TYPE_OTHER_PUNCTUATION()
		 */
		public static final int TYPE_OTHER_PUNCTUATION_ordinal = 23;

		/**
		 * General category "Sm" in the Unicode specification.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr TYPE_MATH_SYMBOL() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.TYPE_MATH_SYMBOL);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Char.TYPE_MATH_SYMBOL.
		 * @see #TYPE_MATH_SYMBOL()
		 */
		public static final QualifiedName TYPE_MATH_SYMBOL = 
			QualifiedName.make(CAL_Char.MODULE_NAME, "TYPE_MATH_SYMBOL");

		/**
		 * Ordinal of DataConstructor Cal.Core.Char.TYPE_MATH_SYMBOL.
		 * @see #TYPE_MATH_SYMBOL()
		 */
		public static final int TYPE_MATH_SYMBOL_ordinal = 24;

		/**
		 * General category "Sc" in the Unicode specification.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr TYPE_CURRENCY_SYMBOL() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.TYPE_CURRENCY_SYMBOL);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Char.TYPE_CURRENCY_SYMBOL.
		 * @see #TYPE_CURRENCY_SYMBOL()
		 */
		public static final QualifiedName TYPE_CURRENCY_SYMBOL = 
			QualifiedName.make(CAL_Char.MODULE_NAME, "TYPE_CURRENCY_SYMBOL");

		/**
		 * Ordinal of DataConstructor Cal.Core.Char.TYPE_CURRENCY_SYMBOL.
		 * @see #TYPE_CURRENCY_SYMBOL()
		 */
		public static final int TYPE_CURRENCY_SYMBOL_ordinal = 25;

		/**
		 * General category "Sk" in the Unicode specification.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr TYPE_MODIFIER_SYMBOL() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.TYPE_MODIFIER_SYMBOL);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Char.TYPE_MODIFIER_SYMBOL.
		 * @see #TYPE_MODIFIER_SYMBOL()
		 */
		public static final QualifiedName TYPE_MODIFIER_SYMBOL = 
			QualifiedName.make(CAL_Char.MODULE_NAME, "TYPE_MODIFIER_SYMBOL");

		/**
		 * Ordinal of DataConstructor Cal.Core.Char.TYPE_MODIFIER_SYMBOL.
		 * @see #TYPE_MODIFIER_SYMBOL()
		 */
		public static final int TYPE_MODIFIER_SYMBOL_ordinal = 26;

		/**
		 * General category "So" in the Unicode specification.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr TYPE_OTHER_SYMBOL() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.TYPE_OTHER_SYMBOL);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Char.TYPE_OTHER_SYMBOL.
		 * @see #TYPE_OTHER_SYMBOL()
		 */
		public static final QualifiedName TYPE_OTHER_SYMBOL = 
			QualifiedName.make(CAL_Char.MODULE_NAME, "TYPE_OTHER_SYMBOL");

		/**
		 * Ordinal of DataConstructor Cal.Core.Char.TYPE_OTHER_SYMBOL.
		 * @see #TYPE_OTHER_SYMBOL()
		 */
		public static final int TYPE_OTHER_SYMBOL_ordinal = 27;

		/**
		 * General category "Pi" in the Unicode specification.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr TYPE_INITIAL_QUOTE_PUNCTUATION() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.TYPE_INITIAL_QUOTE_PUNCTUATION);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Char.TYPE_INITIAL_QUOTE_PUNCTUATION.
		 * @see #TYPE_INITIAL_QUOTE_PUNCTUATION()
		 */
		public static final QualifiedName TYPE_INITIAL_QUOTE_PUNCTUATION = 
			QualifiedName.make(
				CAL_Char.MODULE_NAME, 
				"TYPE_INITIAL_QUOTE_PUNCTUATION");

		/**
		 * Ordinal of DataConstructor Cal.Core.Char.TYPE_INITIAL_QUOTE_PUNCTUATION.
		 * @see #TYPE_INITIAL_QUOTE_PUNCTUATION()
		 */
		public static final int TYPE_INITIAL_QUOTE_PUNCTUATION_ordinal = 28;

		/**
		 * General category "Pf" in the Unicode specification.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr TYPE_FINAL_QUOTE_PUNCTUATION() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.TYPE_FINAL_QUOTE_PUNCTUATION);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Char.TYPE_FINAL_QUOTE_PUNCTUATION.
		 * @see #TYPE_FINAL_QUOTE_PUNCTUATION()
		 */
		public static final QualifiedName TYPE_FINAL_QUOTE_PUNCTUATION = 
			QualifiedName.make(
				CAL_Char.MODULE_NAME, 
				"TYPE_FINAL_QUOTE_PUNCTUATION");

		/**
		 * Ordinal of DataConstructor Cal.Core.Char.TYPE_FINAL_QUOTE_PUNCTUATION.
		 * @see #TYPE_FINAL_QUOTE_PUNCTUATION()
		 */
		public static final int TYPE_FINAL_QUOTE_PUNCTUATION_ordinal = 29;

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Core.Char module.
	 */
	public static final class Functions {
		/**
		 * Converts a <code>Cal.Core.Prelude.Char</code> representing a hexadecimal digit to an <code>Cal.Core.Prelude.Int</code>.
		 * <p>
		 * e.g.
		 * <ul>
		 *  <li>
		 *   <code>digitToInt '8' = 8</code>
		 *  </li>
		 *  <li>
		 *   <code>digitToInt 'a' = 10</code>
		 *  </li>
		 * </ul>
		 * 
		 * @param c (CAL type: <code>Cal.Core.Prelude.Char</code>)
		 *          the character containing the hexadecimal digit to be converted.
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the <code>Int</code> value that is represented by the hexadecimal digit.
		 */
		public static final SourceModel.Expr digitToInt(SourceModel.Expr c) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.digitToInt), c});
		}

		/**
		 * @see #digitToInt(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param c
		 * @return the SourceModel.Expr representing an application of digitToInt
		 */
		public static final SourceModel.Expr digitToInt(char c) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.digitToInt), SourceModel.Expr.makeCharValue(c)});
		}

		/**
		 * Name binding for function: digitToInt.
		 * @see #digitToInt(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName digitToInt = 
			QualifiedName.make(CAL_Char.MODULE_NAME, "digitToInt");

		/**
		 * 
		 * @param charValue (CAL type: <code>Cal.Core.Prelude.Char</code>)
		 *          the <code>Cal.Core.Prelude.Char</code> to classify
		 * @return (CAL type: <code>Cal.Core.Char.Directionality</code>) 
		 *          a <code>Cal.Core.Char.Directionality</code> value that represents the directionality of charValue.
		 */
		public static final SourceModel.Expr directionality(SourceModel.Expr charValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.directionality), charValue});
		}

		/**
		 * @see #directionality(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param charValue
		 * @return the SourceModel.Expr representing an application of directionality
		 */
		public static final SourceModel.Expr directionality(char charValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.directionality), SourceModel.Expr.makeCharValue(charValue)});
		}

		/**
		 * Name binding for function: directionality.
		 * @see #directionality(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName directionality = 
			QualifiedName.make(CAL_Char.MODULE_NAME, "directionality");

		/**
		 * Converts an <code>Cal.Core.Prelude.Int</code> value to a <code>Cal.Core.Prelude.Char</code> value.
		 * @param intValue (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the value to be converted.
		 * @return (CAL type: <code>Cal.Core.Prelude.Char</code>) 
		 *          the corresponding <code>Char</code> value.
		 */
		public static final SourceModel.Expr fromInt(SourceModel.Expr intValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromInt), intValue});
		}

		/**
		 * @see #fromInt(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param intValue
		 * @return the SourceModel.Expr representing an application of fromInt
		 */
		public static final SourceModel.Expr fromInt(int intValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromInt), SourceModel.Expr.makeIntValue(intValue)});
		}

		/**
		 * Name binding for function: fromInt.
		 * @see #fromInt(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fromInt = 
			QualifiedName.make(CAL_Char.MODULE_NAME, "fromInt");

		/**
		 * Converts an <code>Cal.Core.Prelude.Int</code> to a <code>Cal.Core.Prelude.Char</code> representing that <code>Int</code> as a hexadecimal digit.
		 * <p>
		 * e.g.
		 * <ul>
		 *  <li>
		 *   <code>intToDigit 5 = '5'</code>
		 *  </li>
		 *  <li>
		 *   <code>intToDigit 10 = 'a'</code>
		 *  </li>
		 * </ul>
		 * 
		 * @param i (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the <code>Int</code> value to be converted.
		 * @return (CAL type: <code>Cal.Core.Prelude.Char</code>) 
		 *          the <code>Char</code> representing <code>i</code> as a hexadecimal digit.
		 */
		public static final SourceModel.Expr intToDigit(SourceModel.Expr i) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intToDigit), i});
		}

		/**
		 * @see #intToDigit(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param i
		 * @return the SourceModel.Expr representing an application of intToDigit
		 */
		public static final SourceModel.Expr intToDigit(int i) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intToDigit), SourceModel.Expr.makeIntValue(i)});
		}

		/**
		 * Name binding for function: intToDigit.
		 * @see #intToDigit(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intToDigit = 
			QualifiedName.make(CAL_Char.MODULE_NAME, "intToDigit");

		/**
		 * Determines if the specified character is a digit according to the Unicode Standard.
		 * <p>
		 * A character <code>c</code> is a digit if <code>Cal.Core.Char.type c == Cal.Core.Char.TYPE_DECIMAL_DIGIT_NUMBER</code>.
		 * <p>
		 * Note: This function cannot handle supplementary Unicode characters, whose code points are above U+FFFF.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Char.isLetter, Cal.Core.Char.isLetterOrDigit
		 * </dl>
		 * 
		 * @param charValue (CAL type: <code>Cal.Core.Prelude.Char</code>)
		 *          the character to check.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the character is a digit; <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr isDigit(SourceModel.Expr charValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isDigit), charValue});
		}

		/**
		 * @see #isDigit(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param charValue
		 * @return the SourceModel.Expr representing an application of isDigit
		 */
		public static final SourceModel.Expr isDigit(char charValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isDigit), SourceModel.Expr.makeCharValue(charValue)});
		}

		/**
		 * Name binding for function: isDigit.
		 * @see #isDigit(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isDigit = 
			QualifiedName.make(CAL_Char.MODULE_NAME, "isDigit");

		/**
		 * Determines if the specified character is a letter according to the Unicode Standard.
		 * <p>
		 * A character <code>c</code> is a letter if <code>Cal.Core.Char.type c</code> is equal to one of:
		 * <ul>
		 *  <li>
		 *   <code>Cal.Core.Char.TYPE_UPPERCASE_LETTER</code>
		 *  </li>
		 *  <li>
		 *   <code>Cal.Core.Char.TYPE_LOWERCASE_LETTER</code>
		 *  </li>
		 *  <li>
		 *   <code>Cal.Core.Char.TYPE_TITLECASE_LETTER</code>
		 *  </li>
		 *  <li>
		 *   <code>Cal.Core.Char.TYPE_MODIFIER_LETTER</code>
		 *  </li>
		 *  <li>
		 *   <code>Cal.Core.Char.TYPE_OTHER_LETTER</code>
		 *  </li>
		 * </ul>
		 * <p>
		 *  
		 * <p>
		 * Note: This function cannot handle supplementary Unicode characters, whose code points are above U+FFFF.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Char.isUpperCase, Cal.Core.Char.isLowerCase, Cal.Core.Char.isDigit, Cal.Core.Char.isLetterOrDigit
		 * </dl>
		 * 
		 * @param charValue (CAL type: <code>Cal.Core.Prelude.Char</code>)
		 *          the character to check.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the character is a letter; <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr isLetter(SourceModel.Expr charValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isLetter), charValue});
		}

		/**
		 * @see #isLetter(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param charValue
		 * @return the SourceModel.Expr representing an application of isLetter
		 */
		public static final SourceModel.Expr isLetter(char charValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isLetter), SourceModel.Expr.makeCharValue(charValue)});
		}

		/**
		 * Name binding for function: isLetter.
		 * @see #isLetter(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isLetter = 
			QualifiedName.make(CAL_Char.MODULE_NAME, "isLetter");

		/**
		 * Determines if the specified character is a letter or digit according to the Unicode Standard.
		 * <p>
		 * For any character <code>c</code>, <code>isLetterOrDigit c == Cal.Core.Char.isLetter c || Cal.Core.Char.isDigit c</code>.
		 * <p>
		 * Note: This function cannot handle supplementary Unicode characters, whose code points are above U+FFFF.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Char.isLetter, Cal.Core.Char.isDigit
		 * </dl>
		 * 
		 * @param charValue (CAL type: <code>Cal.Core.Prelude.Char</code>)
		 *          the character to check.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the character is a letter or digit; <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr isLetterOrDigit(SourceModel.Expr charValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isLetterOrDigit), charValue});
		}

		/**
		 * @see #isLetterOrDigit(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param charValue
		 * @return the SourceModel.Expr representing an application of isLetterOrDigit
		 */
		public static final SourceModel.Expr isLetterOrDigit(char charValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isLetterOrDigit), SourceModel.Expr.makeCharValue(charValue)});
		}

		/**
		 * Name binding for function: isLetterOrDigit.
		 * @see #isLetterOrDigit(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isLetterOrDigit = 
			QualifiedName.make(CAL_Char.MODULE_NAME, "isLetterOrDigit");

		/**
		 * Determines if the specified character is a lowercase character according to the Unicode Standard.
		 * <p>
		 * A character <code>c</code> is in lowercase if <code>Cal.Core.Char.type c == Cal.Core.Char.TYPE_LOWERCASE_LETTER</code>.
		 * <p>
		 * Note: This function cannot handle supplementary Unicode characters, whose code points are above U+FFFF.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Char.isUpperCase, Cal.Core.Char.toLowerCase
		 * </dl>
		 * 
		 * @param charValue (CAL type: <code>Cal.Core.Prelude.Char</code>)
		 *          the character to check.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the character is lowercase; <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr isLowerCase(SourceModel.Expr charValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isLowerCase), charValue});
		}

		/**
		 * @see #isLowerCase(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param charValue
		 * @return the SourceModel.Expr representing an application of isLowerCase
		 */
		public static final SourceModel.Expr isLowerCase(char charValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isLowerCase), SourceModel.Expr.makeCharValue(charValue)});
		}

		/**
		 * Name binding for function: isLowerCase.
		 * @see #isLowerCase(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isLowerCase = 
			QualifiedName.make(CAL_Char.MODULE_NAME, "isLowerCase");

		/**
		 * Determines if the specified character is a space character according to the Unicode Standard.
		 * <p>
		 * A character <code>c</code> is a space character if <code>Cal.Core.Char.type c</code> is equal to one of:
		 * <ul>
		 *  <li>
		 *   <code>Cal.Core.Char.TYPE_SPACE_SEPARATOR</code>
		 *  </li>
		 *  <li>
		 *   <code>Cal.Core.Char.TYPE_LINE_SEPARATOR</code>
		 *  </li>
		 *  <li>
		 *   <code>Cal.Core.Char.TYPE_PARAGRAPH_SEPARATOR</code>
		 *  </li>
		 * </ul>
		 * <p>
		 * Note: This function cannot handle supplementary Unicode characters, whose code points are above U+FFFF.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Char.isWhitespace
		 * </dl>
		 * 
		 * @param charValue (CAL type: <code>Cal.Core.Prelude.Char</code>)
		 *          the character to check.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the character is a space character; <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr isSpaceChar(SourceModel.Expr charValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isSpaceChar), charValue});
		}

		/**
		 * @see #isSpaceChar(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param charValue
		 * @return the SourceModel.Expr representing an application of isSpaceChar
		 */
		public static final SourceModel.Expr isSpaceChar(char charValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isSpaceChar), SourceModel.Expr.makeCharValue(charValue)});
		}

		/**
		 * Name binding for function: isSpaceChar.
		 * @see #isSpaceChar(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isSpaceChar = 
			QualifiedName.make(CAL_Char.MODULE_NAME, "isSpaceChar");

		/**
		 * Determines if the specified character is an uppercase character according to the Unicode Standard.
		 * <p>
		 * A character <code>c</code> is in uppercase if <code>Cal.Core.Char.type c == Cal.Core.Char.TYPE_UPPERCASE_LETTER</code>.
		 * <p>
		 * Note: This function cannot handle supplementary Unicode characters, whose code points are above U+FFFF.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Char.isLowerCase, Cal.Core.Char.toUpperCase
		 * </dl>
		 * 
		 * @param charValue (CAL type: <code>Cal.Core.Prelude.Char</code>)
		 *          the character to check.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the character is uppercase; <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr isUpperCase(SourceModel.Expr charValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isUpperCase), charValue});
		}

		/**
		 * @see #isUpperCase(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param charValue
		 * @return the SourceModel.Expr representing an application of isUpperCase
		 */
		public static final SourceModel.Expr isUpperCase(char charValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isUpperCase), SourceModel.Expr.makeCharValue(charValue)});
		}

		/**
		 * Name binding for function: isUpperCase.
		 * @see #isUpperCase(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isUpperCase = 
			QualifiedName.make(CAL_Char.MODULE_NAME, "isUpperCase");

		/**
		 * Determines if the specified character is white space according to Java. The characters named below
		 * are considered as white space:
		 * <ul>
		 *  <li>
		 *   A Unicode space character
		 *   (i.e. <code>Cal.Core.Char.type charValue</code> is one of <code>Cal.Core.Char.TYPE_SPACE_SEPARATOR</code>, <code>Cal.Core.Char.TYPE_LINE_SEPARATOR</code>,
		 *   or <code>Cal.Core.Char.TYPE_PARAGRAPH_SEPARATOR</code>)
		 *   that is not a non-breaking space (<code>'\u00A0'</code>, <code>'\u2007'</code>, <code>'\u202F'</code>).
		 *  </li>
		 *  <li>
		 *   <code>'\u0009'</code>, HORIZONTAL TABULATION.
		 *  </li>
		 *  <li>
		 *   <code>'\u000A'</code>, LINE FEED.
		 *  </li>
		 *  <li>
		 *   <code>'\u000B'</code>, VERTICAL TABULATION.
		 *  </li>
		 *  <li>
		 *   <code>'\u000C'</code>, FORM FEED.
		 *  </li>
		 *  <li>
		 *   <code>'\u000D'</code>, CARRIAGE RETURN.
		 *  </li>
		 *  <li>
		 *   <code>'\u001C'</code>, FILE SEPARATOR.
		 *  </li>
		 *  <li>
		 *   <code>'\u001D'</code>, GROUP SEPARATOR.
		 *  </li>
		 *  <li>
		 *   <code>'\u001E'</code>, RECORD SEPARATOR.
		 *  </li>
		 *  <li>
		 *   <code>'\u001F'</code>, UNIT SEPARATOR.
		 *  </li>
		 * </ul>
		 * <p>
		 * Note: This function cannot handle supplementary Unicode characters, whose code points are above U+FFFF.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Char.isSpaceChar
		 * </dl>
		 * 
		 * @param charValue (CAL type: <code>Cal.Core.Prelude.Char</code>)
		 *          the character to check.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the character is a Java whitespace character; <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr isWhitespace(SourceModel.Expr charValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isWhitespace), charValue});
		}

		/**
		 * @see #isWhitespace(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param charValue
		 * @return the SourceModel.Expr representing an application of isWhitespace
		 */
		public static final SourceModel.Expr isWhitespace(char charValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isWhitespace), SourceModel.Expr.makeCharValue(charValue)});
		}

		/**
		 * Name binding for function: isWhitespace.
		 * @see #isWhitespace(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isWhitespace = 
			QualifiedName.make(CAL_Char.MODULE_NAME, "isWhitespace");

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
			QualifiedName.make(CAL_Char.MODULE_NAME, "testModule");

		/**
		 * Converts a <code>Cal.Core.Prelude.Char</code> value to an <code>Cal.Core.Prelude.Int</code> value.
		 * @param charValue (CAL type: <code>Cal.Core.Prelude.Char</code>)
		 *          the value to be converted.
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the corresponding <code>Int</code> value.
		 */
		public static final SourceModel.Expr toInt(SourceModel.Expr charValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toInt), charValue});
		}

		/**
		 * @see #toInt(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param charValue
		 * @return the SourceModel.Expr representing an application of toInt
		 */
		public static final SourceModel.Expr toInt(char charValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toInt), SourceModel.Expr.makeCharValue(charValue)});
		}

		/**
		 * Name binding for function: toInt.
		 * @see #toInt(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName toInt = 
			QualifiedName.make(CAL_Char.MODULE_NAME, "toInt");

		/**
		 * Converts the character argument to lowercase using case mapping information
		 * from the Unicode Standard.
		 * <p>
		 * Note that <code>(Cal.Core.Char.isLowerCase (toLowerCase ch))</code> is not true for many characters (e.g. symbols, Han characters).
		 * <p>
		 * In general, <code>Cal.Core.String.toLowerCase</code> should be used to map characters to lowercase.
		 * String case mapping functions have several benefits over character case mapping
		 * functions. String case mapping functions can perform locale-sensitive mappings,
		 * context-sensitive mappings, and 1:M character mappings, whereas the character case
		 * mapping functions cannot.
		 * <p>
		 * Note: This function cannot handle supplementary Unicode characters, whose code points are above U+FFFF.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Char.isLowerCase, Cal.Core.String.toLowerCase
		 * </dl>
		 * 
		 * @param charValue (CAL type: <code>Cal.Core.Prelude.Char</code>)
		 *          the character to be converted.
		 * @return (CAL type: <code>Cal.Core.Prelude.Char</code>) 
		 *          the lowercase equivalent of the character, if any; otherwise, the character itself.
		 */
		public static final SourceModel.Expr toLowerCase(SourceModel.Expr charValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toLowerCase), charValue});
		}

		/**
		 * @see #toLowerCase(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param charValue
		 * @return the SourceModel.Expr representing an application of toLowerCase
		 */
		public static final SourceModel.Expr toLowerCase(char charValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toLowerCase), SourceModel.Expr.makeCharValue(charValue)});
		}

		/**
		 * Name binding for function: toLowerCase.
		 * @see #toLowerCase(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName toLowerCase = 
			QualifiedName.make(CAL_Char.MODULE_NAME, "toLowerCase");

		/**
		 * Converts the character argument to uppercase using case mapping information
		 * from the Unicode Standard.
		 * <p>
		 * Note that <code>(Cal.Core.Char.isUpperCase (toUpperCase ch))</code> is not true for many characters (e.g. symbols, Han characters).
		 * <p>
		 * In general, <code>Cal.Core.String.toUpperCase</code> should be used to map characters to uppercase.
		 * String case mapping functions have several benefits over character case mapping
		 * functions. String case mapping functions can perform locale-sensitive mappings,
		 * context-sensitive mappings, and 1:M character mappings, whereas the character case
		 * mapping functions cannot.
		 * <p>
		 * Note: This function cannot handle supplementary Unicode characters, whose code points are above U+FFFF.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Char.isUpperCase, Cal.Core.String.toUpperCase
		 * </dl>
		 * 
		 * @param charValue (CAL type: <code>Cal.Core.Prelude.Char</code>)
		 *          the character to be converted.
		 * @return (CAL type: <code>Cal.Core.Prelude.Char</code>) 
		 *          the uppercase equivalent of the character, if any; otherwise, the character itself.
		 */
		public static final SourceModel.Expr toUpperCase(SourceModel.Expr charValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toUpperCase), charValue});
		}

		/**
		 * @see #toUpperCase(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param charValue
		 * @return the SourceModel.Expr representing an application of toUpperCase
		 */
		public static final SourceModel.Expr toUpperCase(char charValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toUpperCase), SourceModel.Expr.makeCharValue(charValue)});
		}

		/**
		 * Name binding for function: toUpperCase.
		 * @see #toUpperCase(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName toUpperCase = 
			QualifiedName.make(CAL_Char.MODULE_NAME, "toUpperCase");

		/**
		 * 
		 * @param charValue (CAL type: <code>Cal.Core.Prelude.Char</code>)
		 *          <code>Cal.Core.Prelude.Char</code> to classify
		 * @return (CAL type: <code>Cal.Core.Char.Type</code>) 
		 *          a <code>Cal.Core.Char.Type</code> value representing the type of the provided <code>Cal.Core.Prelude.Char</code>
		 */
		public static final SourceModel.Expr type(SourceModel.Expr charValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.type), charValue});
		}

		/**
		 * @see #type(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param charValue
		 * @return the SourceModel.Expr representing an application of type
		 */
		public static final SourceModel.Expr type(char charValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.type), SourceModel.Expr.makeCharValue(charValue)});
		}

		/**
		 * Name binding for function: type.
		 * @see #type(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName type = 
			QualifiedName.make(CAL_Char.MODULE_NAME, "type");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = 1189593673;

}
