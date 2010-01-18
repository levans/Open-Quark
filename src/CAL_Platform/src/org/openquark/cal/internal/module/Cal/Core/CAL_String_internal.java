/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_String_internal.java)
 * was generated from CAL module: Cal.Core.String.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Core.String module from Java code.
 *  
 * Creation date: Tue Jul 31 18:06:12 PDT 2007
 * --!>
 *  
 */

package org.openquark.cal.internal.module.Cal.Core;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * Defines many useful functions for the <code>Cal.Core.Prelude.String</code> type. Note that the <code>String</code> type itself is defined
 * in the <code>Cal.Core.Prelude</code> module due to the fact that it is supported via built-in notation for <code>String</code> literals.
 * @author Bo Ilic
 */
public final class CAL_String_internal {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Core.String");

	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Core.String module.
	 */
	public static final class TypeConstructors {
		/** Name binding for TypeConsApp: JStringBuilder. */
		public static final QualifiedName JStringBuilder = 
			QualifiedName.make(
				CAL_String_internal.MODULE_NAME, 
				"JStringBuilder");

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Core.String module.
	 */
	public static final class Functions {
		/**
		 * Helper binding method for function: compareStringsExamples. 
		 * @return the SourceModule.expr representing an application of compareStringsExamples
		 */
		public static final SourceModel.Expr compareStringsExamples() {
			return SourceModel.Expr.Var.make(Functions.compareStringsExamples);
		}

		/**
		 * Name binding for function: compareStringsExamples.
		 * @see #compareStringsExamples()
		 */
		public static final QualifiedName compareStringsExamples = 
			QualifiedName.make(
				CAL_String_internal.MODULE_NAME, 
				"compareStringsExamples");

		/**
		 * Helper binding method for function: dropWhileExamples. 
		 * @return the SourceModule.expr representing an application of dropWhileExamples
		 */
		public static final SourceModel.Expr dropWhileExamples() {
			return SourceModel.Expr.Var.make(Functions.dropWhileExamples);
		}

		/**
		 * Name binding for function: dropWhileExamples.
		 * @see #dropWhileExamples()
		 */
		public static final QualifiedName dropWhileExamples = 
			QualifiedName.make(
				CAL_String_internal.MODULE_NAME, 
				"dropWhileExamples");

		/**
		 * Helper binding method for function: endsWithExamples. 
		 * @return the SourceModule.expr representing an application of endsWithExamples
		 */
		public static final SourceModel.Expr endsWithExamples() {
			return SourceModel.Expr.Var.make(Functions.endsWithExamples);
		}

		/**
		 * Name binding for function: endsWithExamples.
		 * @see #endsWithExamples()
		 */
		public static final QualifiedName endsWithExamples = 
			QualifiedName.make(
				CAL_String_internal.MODULE_NAME, 
				"endsWithExamples");

		/**
		 * Helper binding method for function: filterExamples. 
		 * @return the SourceModule.expr representing an application of filterExamples
		 */
		public static final SourceModel.Expr filterExamples() {
			return SourceModel.Expr.Var.make(Functions.filterExamples);
		}

		/**
		 * Name binding for function: filterExamples.
		 * @see #filterExamples()
		 */
		public static final QualifiedName filterExamples = 
			QualifiedName.make(
				CAL_String_internal.MODULE_NAME, 
				"filterExamples");

		/**
		 * Helper binding method for function: findExamples. 
		 * @return the SourceModule.expr representing an application of findExamples
		 */
		public static final SourceModel.Expr findExamples() {
			return SourceModel.Expr.Var.make(Functions.findExamples);
		}

		/**
		 * Name binding for function: findExamples.
		 * @see #findExamples()
		 */
		public static final QualifiedName findExamples = 
			QualifiedName.make(CAL_String_internal.MODULE_NAME, "findExamples");

		/**
		 * Helper binding method for function: findIndexExamples. 
		 * @return the SourceModule.expr representing an application of findIndexExamples
		 */
		public static final SourceModel.Expr findIndexExamples() {
			return SourceModel.Expr.Var.make(Functions.findIndexExamples);
		}

		/**
		 * Name binding for function: findIndexExamples.
		 * @see #findIndexExamples()
		 */
		public static final QualifiedName findIndexExamples = 
			QualifiedName.make(
				CAL_String_internal.MODULE_NAME, 
				"findIndexExamples");

		/**
		 * Helper binding method for function: foldLeftStrictExamples. 
		 * @return the SourceModule.expr representing an application of foldLeftStrictExamples
		 */
		public static final SourceModel.Expr foldLeftStrictExamples() {
			return SourceModel.Expr.Var.make(Functions.foldLeftStrictExamples);
		}

		/**
		 * Name binding for function: foldLeftStrictExamples.
		 * @see #foldLeftStrictExamples()
		 */
		public static final QualifiedName foldLeftStrictExamples = 
			QualifiedName.make(
				CAL_String_internal.MODULE_NAME, 
				"foldLeftStrictExamples");

		/**
		 * Helper binding method for function: jCompareStringIgnoreCase. 
		 * @param string
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of jCompareStringIgnoreCase
		 */
		public static final SourceModel.Expr jCompareStringIgnoreCase(SourceModel.Expr string, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jCompareStringIgnoreCase), string, arg_2});
		}

		/**
		 * @see #jCompareStringIgnoreCase(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param string
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of jCompareStringIgnoreCase
		 */
		public static final SourceModel.Expr jCompareStringIgnoreCase(java.lang.String string, java.lang.String arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jCompareStringIgnoreCase), SourceModel.Expr.makeStringValue(string), SourceModel.Expr.makeStringValue(arg_2)});
		}

		/**
		 * Name binding for function: jCompareStringIgnoreCase.
		 * @see #jCompareStringIgnoreCase(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jCompareStringIgnoreCase = 
			QualifiedName.make(
				CAL_String_internal.MODULE_NAME, 
				"jCompareStringIgnoreCase");

		/**
		 * Helper binding method for function: linesExamples. 
		 * @return the SourceModule.expr representing an application of linesExamples
		 */
		public static final SourceModel.Expr linesExamples() {
			return SourceModel.Expr.Var.make(Functions.linesExamples);
		}

		/**
		 * Name binding for function: linesExamples.
		 * @see #linesExamples()
		 */
		public static final QualifiedName linesExamples = 
			QualifiedName.make(CAL_String_internal.MODULE_NAME, "linesExamples");

		/**
		 * Helper binding method for function: mapExamples. 
		 * @return the SourceModule.expr representing an application of mapExamples
		 */
		public static final SourceModel.Expr mapExamples() {
			return SourceModel.Expr.Var.make(Functions.mapExamples);
		}

		/**
		 * Name binding for function: mapExamples.
		 * @see #mapExamples()
		 */
		public static final QualifiedName mapExamples = 
			QualifiedName.make(CAL_String_internal.MODULE_NAME, "mapExamples");

		/**
		 * Helper binding method for function: normalizeWhitespaceExamples. 
		 * @return the SourceModule.expr representing an application of normalizeWhitespaceExamples
		 */
		public static final SourceModel.Expr normalizeWhitespaceExamples() {
			return 
				SourceModel.Expr.Var.make(Functions.normalizeWhitespaceExamples);
		}

		/**
		 * Name binding for function: normalizeWhitespaceExamples.
		 * @see #normalizeWhitespaceExamples()
		 */
		public static final QualifiedName normalizeWhitespaceExamples = 
			QualifiedName.make(
				CAL_String_internal.MODULE_NAME, 
				"normalizeWhitespaceExamples");

		/**
		 * Helper binding method for function: replicateExamples. 
		 * @return the SourceModule.expr representing an application of replicateExamples
		 */
		public static final SourceModel.Expr replicateExamples() {
			return SourceModel.Expr.Var.make(Functions.replicateExamples);
		}

		/**
		 * Name binding for function: replicateExamples.
		 * @see #replicateExamples()
		 */
		public static final QualifiedName replicateExamples = 
			QualifiedName.make(
				CAL_String_internal.MODULE_NAME, 
				"replicateExamples");

		/**
		 * Helper binding method for function: reverseExamples. 
		 * @return the SourceModule.expr representing an application of reverseExamples
		 */
		public static final SourceModel.Expr reverseExamples() {
			return SourceModel.Expr.Var.make(Functions.reverseExamples);
		}

		/**
		 * Name binding for function: reverseExamples.
		 * @see #reverseExamples()
		 */
		public static final QualifiedName reverseExamples = 
			QualifiedName.make(
				CAL_String_internal.MODULE_NAME, 
				"reverseExamples");

		/**
		 * Helper binding method for function: startsWithExamples. 
		 * @return the SourceModule.expr representing an application of startsWithExamples
		 */
		public static final SourceModel.Expr startsWithExamples() {
			return SourceModel.Expr.Var.make(Functions.startsWithExamples);
		}

		/**
		 * Name binding for function: startsWithExamples.
		 * @see #startsWithExamples()
		 */
		public static final QualifiedName startsWithExamples = 
			QualifiedName.make(
				CAL_String_internal.MODULE_NAME, 
				"startsWithExamples");

		/**
		 * Helper binding method for function: stringBuilder_appendChar. 
		 * @param jStringBuilder
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of stringBuilder_appendChar
		 */
		public static final SourceModel.Expr stringBuilder_appendChar(SourceModel.Expr jStringBuilder, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.stringBuilder_appendChar), jStringBuilder, arg_2});
		}

		/**
		 * @see #stringBuilder_appendChar(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param jStringBuilder
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of stringBuilder_appendChar
		 */
		public static final SourceModel.Expr stringBuilder_appendChar(SourceModel.Expr jStringBuilder, char arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.stringBuilder_appendChar), jStringBuilder, SourceModel.Expr.makeCharValue(arg_2)});
		}

		/**
		 * Name binding for function: stringBuilder_appendChar.
		 * @see #stringBuilder_appendChar(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName stringBuilder_appendChar = 
			QualifiedName.make(
				CAL_String_internal.MODULE_NAME, 
				"stringBuilder_appendChar");

		/**
		 * Helper binding method for function: stringBuilder_charAt. 
		 * @param jStringBuilder
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of stringBuilder_charAt
		 */
		public static final SourceModel.Expr stringBuilder_charAt(SourceModel.Expr jStringBuilder, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.stringBuilder_charAt), jStringBuilder, arg_2});
		}

		/**
		 * @see #stringBuilder_charAt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param jStringBuilder
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of stringBuilder_charAt
		 */
		public static final SourceModel.Expr stringBuilder_charAt(SourceModel.Expr jStringBuilder, int arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.stringBuilder_charAt), jStringBuilder, SourceModel.Expr.makeIntValue(arg_2)});
		}

		/**
		 * Name binding for function: stringBuilder_charAt.
		 * @see #stringBuilder_charAt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName stringBuilder_charAt = 
			QualifiedName.make(
				CAL_String_internal.MODULE_NAME, 
				"stringBuilder_charAt");

		/**
		 * Helper binding method for function: stringBuilder_new0. 
		 * @return the SourceModule.expr representing an application of stringBuilder_new0
		 */
		public static final SourceModel.Expr stringBuilder_new0() {
			return SourceModel.Expr.Var.make(Functions.stringBuilder_new0);
		}

		/**
		 * Name binding for function: stringBuilder_new0.
		 * @see #stringBuilder_new0()
		 */
		public static final QualifiedName stringBuilder_new0 = 
			QualifiedName.make(
				CAL_String_internal.MODULE_NAME, 
				"stringBuilder_new0");

		/**
		 * Helper binding method for function: stringBuilder_new1. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of stringBuilder_new1
		 */
		public static final SourceModel.Expr stringBuilder_new1(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.stringBuilder_new1), arg_1});
		}

		/**
		 * @see #stringBuilder_new1(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of stringBuilder_new1
		 */
		public static final SourceModel.Expr stringBuilder_new1(int arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.stringBuilder_new1), SourceModel.Expr.makeIntValue(arg_1)});
		}

		/**
		 * Name binding for function: stringBuilder_new1.
		 * @see #stringBuilder_new1(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName stringBuilder_new1 = 
			QualifiedName.make(
				CAL_String_internal.MODULE_NAME, 
				"stringBuilder_new1");

		/**
		 * Helper binding method for function: stringBuilder_new2. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of stringBuilder_new2
		 */
		public static final SourceModel.Expr stringBuilder_new2(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.stringBuilder_new2), arg_1});
		}

		/**
		 * @see #stringBuilder_new2(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of stringBuilder_new2
		 */
		public static final SourceModel.Expr stringBuilder_new2(java.lang.String arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.stringBuilder_new2), SourceModel.Expr.makeStringValue(arg_1)});
		}

		/**
		 * Name binding for function: stringBuilder_new2.
		 * @see #stringBuilder_new2(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName stringBuilder_new2 = 
			QualifiedName.make(
				CAL_String_internal.MODULE_NAME, 
				"stringBuilder_new2");

		/**
		 * Helper binding method for function: stringBuilder_reverse. 
		 * @param jStringBuilder
		 * @return the SourceModule.expr representing an application of stringBuilder_reverse
		 */
		public static final SourceModel.Expr stringBuilder_reverse(SourceModel.Expr jStringBuilder) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.stringBuilder_reverse), jStringBuilder});
		}

		/**
		 * Name binding for function: stringBuilder_reverse.
		 * @see #stringBuilder_reverse(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName stringBuilder_reverse = 
			QualifiedName.make(
				CAL_String_internal.MODULE_NAME, 
				"stringBuilder_reverse");

		/**
		 * Helper binding method for function: stringBuilder_setCharAt. 
		 * @param jStringBuilder
		 * @param arg_2
		 * @param arg_3
		 * @return the SourceModule.expr representing an application of stringBuilder_setCharAt
		 */
		public static final SourceModel.Expr stringBuilder_setCharAt(SourceModel.Expr jStringBuilder, SourceModel.Expr arg_2, SourceModel.Expr arg_3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.stringBuilder_setCharAt), jStringBuilder, arg_2, arg_3});
		}

		/**
		 * @see #stringBuilder_setCharAt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param jStringBuilder
		 * @param arg_2
		 * @param arg_3
		 * @return the SourceModel.Expr representing an application of stringBuilder_setCharAt
		 */
		public static final SourceModel.Expr stringBuilder_setCharAt(SourceModel.Expr jStringBuilder, int arg_2, char arg_3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.stringBuilder_setCharAt), jStringBuilder, SourceModel.Expr.makeIntValue(arg_2), SourceModel.Expr.makeCharValue(arg_3)});
		}

		/**
		 * Name binding for function: stringBuilder_setCharAt.
		 * @see #stringBuilder_setCharAt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName stringBuilder_setCharAt = 
			QualifiedName.make(
				CAL_String_internal.MODULE_NAME, 
				"stringBuilder_setCharAt");

		/**
		 * Helper binding method for function: stringBuilder_toString. 
		 * @param jStringBuilder
		 * @return the SourceModule.expr representing an application of stringBuilder_toString
		 */
		public static final SourceModel.Expr stringBuilder_toString(SourceModel.Expr jStringBuilder) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.stringBuilder_toString), jStringBuilder});
		}

		/**
		 * Name binding for function: stringBuilder_toString.
		 * @see #stringBuilder_toString(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName stringBuilder_toString = 
			QualifiedName.make(
				CAL_String_internal.MODULE_NAME, 
				"stringBuilder_toString");

		/**
		 * Helper binding method for function: takeWhileExamples. 
		 * @return the SourceModule.expr representing an application of takeWhileExamples
		 */
		public static final SourceModel.Expr takeWhileExamples() {
			return SourceModel.Expr.Var.make(Functions.takeWhileExamples);
		}

		/**
		 * Name binding for function: takeWhileExamples.
		 * @see #takeWhileExamples()
		 */
		public static final QualifiedName takeWhileExamples = 
			QualifiedName.make(
				CAL_String_internal.MODULE_NAME, 
				"takeWhileExamples");

		/**
		 * Helper binding method for function: toLowerCaseExamples. 
		 * @return the SourceModule.expr representing an application of toLowerCaseExamples
		 */
		public static final SourceModel.Expr toLowerCaseExamples() {
			return SourceModel.Expr.Var.make(Functions.toLowerCaseExamples);
		}

		/**
		 * Name binding for function: toLowerCaseExamples.
		 * @see #toLowerCaseExamples()
		 */
		public static final QualifiedName toLowerCaseExamples = 
			QualifiedName.make(
				CAL_String_internal.MODULE_NAME, 
				"toLowerCaseExamples");

		/**
		 * Helper binding method for function: toUpperCaseExamples. 
		 * @return the SourceModule.expr representing an application of toUpperCaseExamples
		 */
		public static final SourceModel.Expr toUpperCaseExamples() {
			return SourceModel.Expr.Var.make(Functions.toUpperCaseExamples);
		}

		/**
		 * Name binding for function: toUpperCaseExamples.
		 * @see #toUpperCaseExamples()
		 */
		public static final QualifiedName toUpperCaseExamples = 
			QualifiedName.make(
				CAL_String_internal.MODULE_NAME, 
				"toUpperCaseExamples");

		/**
		 * Helper binding method for function: trimExamples. 
		 * @return the SourceModule.expr representing an application of trimExamples
		 */
		public static final SourceModel.Expr trimExamples() {
			return SourceModel.Expr.Var.make(Functions.trimExamples);
		}

		/**
		 * Name binding for function: trimExamples.
		 * @see #trimExamples()
		 */
		public static final QualifiedName trimExamples = 
			QualifiedName.make(CAL_String_internal.MODULE_NAME, "trimExamples");

		/**
		 * Helper binding method for function: unlinesExamples. 
		 * @return the SourceModule.expr representing an application of unlinesExamples
		 */
		public static final SourceModel.Expr unlinesExamples() {
			return SourceModel.Expr.Var.make(Functions.unlinesExamples);
		}

		/**
		 * Name binding for function: unlinesExamples.
		 * @see #unlinesExamples()
		 */
		public static final QualifiedName unlinesExamples = 
			QualifiedName.make(
				CAL_String_internal.MODULE_NAME, 
				"unlinesExamples");

		/**
		 * Helper binding method for function: unwordsExamples. 
		 * @return the SourceModule.expr representing an application of unwordsExamples
		 */
		public static final SourceModel.Expr unwordsExamples() {
			return SourceModel.Expr.Var.make(Functions.unwordsExamples);
		}

		/**
		 * Name binding for function: unwordsExamples.
		 * @see #unwordsExamples()
		 */
		public static final QualifiedName unwordsExamples = 
			QualifiedName.make(
				CAL_String_internal.MODULE_NAME, 
				"unwordsExamples");

		/**
		 * Helper binding method for function: wordsExamples. 
		 * @return the SourceModule.expr representing an application of wordsExamples
		 */
		public static final SourceModel.Expr wordsExamples() {
			return SourceModel.Expr.Var.make(Functions.wordsExamples);
		}

		/**
		 * Name binding for function: wordsExamples.
		 * @see #wordsExamples()
		 */
		public static final QualifiedName wordsExamples = 
			QualifiedName.make(CAL_String_internal.MODULE_NAME, "wordsExamples");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = -1632126270;

}
