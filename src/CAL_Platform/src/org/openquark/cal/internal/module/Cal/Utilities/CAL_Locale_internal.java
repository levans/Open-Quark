/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_Locale_internal.java)
 * was generated from CAL module: Cal.Utilities.Locale.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Utilities.Locale module from Java code.
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
 * This module defines the <code>Cal.Utilities.Locale.Locale</code> type, and provides functions for
 * working with locale values, accessing locale properties of the system, and performing
 * locale-sensitive string comparisons through the use of <code>Cal.Utilities.Locale.Collator</code> and <code>Cal.Utilities.Locale.CollationKey</code>.
 * @author Joseph Wong
 */
public final class CAL_Locale_internal {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Utilities.Locale");

	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Utilities.Locale module.
	 */
	public static final class TypeConstructors {
		/**
		 * A foreign type for Java's <code>Object[]</code> array tupe.
		 */
		public static final QualifiedName JObjectArray = 
			QualifiedName.make(CAL_Locale_internal.MODULE_NAME, "JObjectArray");

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Utilities.Locale module.
	 */
	public static final class Functions {
		/**
		 * Converts <code>Cal.Utilities.Locale.CollatorDecompositionMode</code> values to Java collator decomposition mode constants.
		 * @param mode (CAL type: <code>Cal.Utilities.Locale.CollatorDecompositionMode</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 */
		public static final SourceModel.Expr collatorDecompositionModeToInt(SourceModel.Expr mode) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.collatorDecompositionModeToInt), mode});
		}

		/**
		 * Name binding for function: collatorDecompositionModeToInt.
		 * @see #collatorDecompositionModeToInt(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName collatorDecompositionModeToInt = 
			QualifiedName.make(
				CAL_Locale_internal.MODULE_NAME, 
				"collatorDecompositionModeToInt");

		/**
		 * Converts <code>Cal.Utilities.Locale.CollatorStrength</code> values to Java collator strength constants.
		 * @param strength (CAL type: <code>Cal.Utilities.Locale.CollatorStrength</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 */
		public static final SourceModel.Expr collatorStrengthToInt(SourceModel.Expr strength) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.collatorStrengthToInt), strength});
		}

		/**
		 * Name binding for function: collatorStrengthToInt.
		 * @see #collatorStrengthToInt(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName collatorStrengthToInt = 
			QualifiedName.make(
				CAL_Locale_internal.MODULE_NAME, 
				"collatorStrengthToInt");

		/**
		 * Converts Java collator decomposition mode constants to <code>Cal.Utilities.Locale.CollatorDecompositionMode</code> values.
		 * @param value (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @return (CAL type: <code>Cal.Utilities.Locale.CollatorDecompositionMode</code>) 
		 */
		public static final SourceModel.Expr intToCollatorDecompositionMode(SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intToCollatorDecompositionMode), value});
		}

		/**
		 * @see #intToCollatorDecompositionMode(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value
		 * @return the SourceModel.Expr representing an application of intToCollatorDecompositionMode
		 */
		public static final SourceModel.Expr intToCollatorDecompositionMode(int value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intToCollatorDecompositionMode), SourceModel.Expr.makeIntValue(value)});
		}

		/**
		 * Name binding for function: intToCollatorDecompositionMode.
		 * @see #intToCollatorDecompositionMode(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intToCollatorDecompositionMode = 
			QualifiedName.make(
				CAL_Locale_internal.MODULE_NAME, 
				"intToCollatorDecompositionMode");

		/**
		 * Converts Java collator strength constants to <code>Cal.Utilities.Locale.CollatorStrength</code> values.
		 * @param value (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @return (CAL type: <code>Cal.Utilities.Locale.CollatorStrength</code>) 
		 */
		public static final SourceModel.Expr intToCollatorStrength(SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intToCollatorStrength), value});
		}

		/**
		 * @see #intToCollatorStrength(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value
		 * @return the SourceModel.Expr representing an application of intToCollatorStrength
		 */
		public static final SourceModel.Expr intToCollatorStrength(int value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intToCollatorStrength), SourceModel.Expr.makeIntValue(value)});
		}

		/**
		 * Name binding for function: intToCollatorStrength.
		 * @see #intToCollatorStrength(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intToCollatorStrength = 
			QualifiedName.make(
				CAL_Locale_internal.MODULE_NAME, 
				"intToCollatorStrength");

		/**
		 * Converts a <code>Cal.Utilities.Locale.JObjectArray</code> to a <code>Cal.Core.Prelude.JList</code>.
		 * @param objectArray (CAL type: <code>Cal.Utilities.Locale.JObjectArray</code>)
		 *          the array of Java objects.
		 * @return (CAL type: <code>Cal.Core.Prelude.JList</code>) 
		 *          the corresponding list.
		 */
		public static final SourceModel.Expr jArrayToList(SourceModel.Expr objectArray) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jArrayToList), objectArray});
		}

		/**
		 * Name binding for function: jArrayToList.
		 * @see #jArrayToList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jArrayToList = 
			QualifiedName.make(CAL_Locale_internal.MODULE_NAME, "jArrayToList");

		/**
		 * Java constant for canonical decomposition.
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 */
		public static final SourceModel.Expr jCanonicalDecomposition() {
			return SourceModel.Expr.Var.make(Functions.jCanonicalDecomposition);
		}

		/**
		 * Name binding for function: jCanonicalDecomposition.
		 * @see #jCanonicalDecomposition()
		 */
		public static final QualifiedName jCanonicalDecomposition = 
			QualifiedName.make(
				CAL_Locale_internal.MODULE_NAME, 
				"jCanonicalDecomposition");

		/**
		 * Returns an array of all installed locales for the <code>java.text.Collator</code> class.
		 * @return (CAL type: <code>Cal.Utilities.Locale.JObjectArray</code>) 
		 *          an array of locales.
		 */
		public static final SourceModel.Expr jCollatorGetAvailableLocales() {
			return 
				SourceModel.Expr.Var.make(
					Functions.jCollatorGetAvailableLocales);
		}

		/**
		 * Name binding for function: jCollatorGetAvailableLocales.
		 * @see #jCollatorGetAvailableLocales()
		 */
		public static final QualifiedName jCollatorGetAvailableLocales = 
			QualifiedName.make(
				CAL_Locale_internal.MODULE_NAME, 
				"jCollatorGetAvailableLocales");

		/**
		 * Java constant for identical collator strength.
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 */
		public static final SourceModel.Expr jCollatorStrengthIdentical() {
			return 
				SourceModel.Expr.Var.make(Functions.jCollatorStrengthIdentical);
		}

		/**
		 * Name binding for function: jCollatorStrengthIdentical.
		 * @see #jCollatorStrengthIdentical()
		 */
		public static final QualifiedName jCollatorStrengthIdentical = 
			QualifiedName.make(
				CAL_Locale_internal.MODULE_NAME, 
				"jCollatorStrengthIdentical");

		/**
		 * Java constant for primary collator strength.
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 */
		public static final SourceModel.Expr jCollatorStrengthPrimary() {
			return SourceModel.Expr.Var.make(Functions.jCollatorStrengthPrimary);
		}

		/**
		 * Name binding for function: jCollatorStrengthPrimary.
		 * @see #jCollatorStrengthPrimary()
		 */
		public static final QualifiedName jCollatorStrengthPrimary = 
			QualifiedName.make(
				CAL_Locale_internal.MODULE_NAME, 
				"jCollatorStrengthPrimary");

		/**
		 * Java constant for secondary collator strength.
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 */
		public static final SourceModel.Expr jCollatorStrengthSecondary() {
			return 
				SourceModel.Expr.Var.make(Functions.jCollatorStrengthSecondary);
		}

		/**
		 * Name binding for function: jCollatorStrengthSecondary.
		 * @see #jCollatorStrengthSecondary()
		 */
		public static final QualifiedName jCollatorStrengthSecondary = 
			QualifiedName.make(
				CAL_Locale_internal.MODULE_NAME, 
				"jCollatorStrengthSecondary");

		/**
		 * Java constant for tertiary collator strength.
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 */
		public static final SourceModel.Expr jCollatorStrengthTertiary() {
			return 
				SourceModel.Expr.Var.make(Functions.jCollatorStrengthTertiary);
		}

		/**
		 * Name binding for function: jCollatorStrengthTertiary.
		 * @see #jCollatorStrengthTertiary()
		 */
		public static final QualifiedName jCollatorStrengthTertiary = 
			QualifiedName.make(
				CAL_Locale_internal.MODULE_NAME, 
				"jCollatorStrengthTertiary");

		/**
		 * Compares the relative order of two strings based on the given collator's collation rules.
		 * @param collator (CAL type: <code>Cal.Utilities.Locale.Collator</code>)
		 *          the collator to use.
		 * @param x (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the first string to be compared.
		 * @param y (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the second string to be compared.
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          an integer &lt;0, =0, or &gt;0 depending on whether <code>x</code> is less than, equal to, or greater than <code>y</code> according
		 * to the collator.
		 */
		public static final SourceModel.Expr jCompareByCollator(SourceModel.Expr collator, SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jCompareByCollator), collator, x, y});
		}

		/**
		 * @see #jCompareByCollator(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param collator
		 * @param x
		 * @param y
		 * @return the SourceModel.Expr representing an application of jCompareByCollator
		 */
		public static final SourceModel.Expr jCompareByCollator(SourceModel.Expr collator, java.lang.String x, java.lang.String y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jCompareByCollator), collator, SourceModel.Expr.makeStringValue(x), SourceModel.Expr.makeStringValue(y)});
		}

		/**
		 * Name binding for function: jCompareByCollator.
		 * @see #jCompareByCollator(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jCompareByCollator = 
			QualifiedName.make(
				CAL_Locale_internal.MODULE_NAME, 
				"jCompareByCollator");

		/**
		 * Java constant for full decomposition.
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 */
		public static final SourceModel.Expr jFullDecomposition() {
			return SourceModel.Expr.Var.make(Functions.jFullDecomposition);
		}

		/**
		 * Name binding for function: jFullDecomposition.
		 * @see #jFullDecomposition()
		 */
		public static final QualifiedName jFullDecomposition = 
			QualifiedName.make(
				CAL_Locale_internal.MODULE_NAME, 
				"jFullDecomposition");

		/**
		 * Gets the decomposition mode of a collator as a Java constant.
		 * @param collator (CAL type: <code>Cal.Utilities.Locale.Collator</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 */
		public static final SourceModel.Expr jGetDecomposition(SourceModel.Expr collator) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jGetDecomposition), collator});
		}

		/**
		 * Name binding for function: jGetDecomposition.
		 * @see #jGetDecomposition(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jGetDecomposition = 
			QualifiedName.make(
				CAL_Locale_internal.MODULE_NAME, 
				"jGetDecomposition");

		/**
		 * Returns the locale defined in the given set of properties of an execution context.
		 * @param executionContextProperties (CAL type: <code>Cal.Core.System.ExecutionContextProperties</code>)
		 *          the set of system properties.
		 * @return (CAL type: <code>Cal.Utilities.Locale.Locale</code>) 
		 *          the locale defined in the system properties.
		 */
		public static final SourceModel.Expr jGetLocale(SourceModel.Expr executionContextProperties) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jGetLocale), executionContextProperties});
		}

		/**
		 * Name binding for function: jGetLocale.
		 * @see #jGetLocale(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jGetLocale = 
			QualifiedName.make(CAL_Locale_internal.MODULE_NAME, "jGetLocale");

		/**
		 * Gets the strength of a collator as a Java constant.
		 * @param collator (CAL type: <code>Cal.Utilities.Locale.Collator</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 */
		public static final SourceModel.Expr jGetStrength(SourceModel.Expr collator) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jGetStrength), collator});
		}

		/**
		 * Name binding for function: jGetStrength.
		 * @see #jGetStrength(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jGetStrength = 
			QualifiedName.make(CAL_Locale_internal.MODULE_NAME, "jGetStrength");

		/**
		 * Returns an array of all installed locales for the <code>java.util.Locale</code> class.
		 * @return (CAL type: <code>Cal.Utilities.Locale.JObjectArray</code>) 
		 *          an array of locales.
		 */
		public static final SourceModel.Expr jLocaleGetAvailableLocales() {
			return 
				SourceModel.Expr.Var.make(Functions.jLocaleGetAvailableLocales);
		}

		/**
		 * Name binding for function: jLocaleGetAvailableLocales.
		 * @see #jLocaleGetAvailableLocales()
		 */
		public static final QualifiedName jLocaleGetAvailableLocales = 
			QualifiedName.make(
				CAL_Locale_internal.MODULE_NAME, 
				"jLocaleGetAvailableLocales");

		/**
		 * Java constant for no decomposition.
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 */
		public static final SourceModel.Expr jNoDecomposition() {
			return SourceModel.Expr.Var.make(Functions.jNoDecomposition);
		}

		/**
		 * Name binding for function: jNoDecomposition.
		 * @see #jNoDecomposition()
		 */
		public static final QualifiedName jNoDecomposition = 
			QualifiedName.make(
				CAL_Locale_internal.MODULE_NAME, 
				"jNoDecomposition");

		/**
		 * Sets the decomposition mode of a collator. Not a pure function.
		 * @param collator (CAL type: <code>Cal.Utilities.Locale.Collator</code>)
		 * @param arg_2 (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @return (CAL type: <code>()</code>) 
		 */
		public static final SourceModel.Expr jSetDecomposition(SourceModel.Expr collator, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jSetDecomposition), collator, arg_2});
		}

		/**
		 * @see #jSetDecomposition(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param collator
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of jSetDecomposition
		 */
		public static final SourceModel.Expr jSetDecomposition(SourceModel.Expr collator, int arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jSetDecomposition), collator, SourceModel.Expr.makeIntValue(arg_2)});
		}

		/**
		 * Name binding for function: jSetDecomposition.
		 * @see #jSetDecomposition(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jSetDecomposition = 
			QualifiedName.make(
				CAL_Locale_internal.MODULE_NAME, 
				"jSetDecomposition");

		/**
		 * Sets the strength of a collator. Not a pure function.
		 * @param collator (CAL type: <code>Cal.Utilities.Locale.Collator</code>)
		 * @param arg_2 (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @return (CAL type: <code>()</code>) 
		 */
		public static final SourceModel.Expr jSetStrength(SourceModel.Expr collator, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jSetStrength), collator, arg_2});
		}

		/**
		 * @see #jSetStrength(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param collator
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of jSetStrength
		 */
		public static final SourceModel.Expr jSetStrength(SourceModel.Expr collator, int arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jSetStrength), collator, SourceModel.Expr.makeIntValue(arg_2)});
		}

		/**
		 * Name binding for function: jSetStrength.
		 * @see #jSetStrength(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jSetStrength = 
			QualifiedName.make(CAL_Locale_internal.MODULE_NAME, "jSetStrength");

		/**
		 * Returns a string representation of the locale for the purpose of debugging.
		 * For the <code>Cal.Utilities.Locale.invariantLocale</code>, the string "(invariantLocale)" is returned. For other locales,
		 * the returned string is the canonical form returned by <code>Cal.Utilities.Locale.canonicalString</code>.
		 * @param locale (CAL type: <code>Cal.Utilities.Locale.Locale</code>)
		 *          the locale whose string representation is to be returned.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          a string representation of the locale.
		 */
		public static final SourceModel.Expr showLocale(SourceModel.Expr locale) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showLocale), locale});
		}

		/**
		 * Name binding for function: showLocale.
		 * @see #showLocale(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showLocale = 
			QualifiedName.make(CAL_Locale_internal.MODULE_NAME, "showLocale");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = 689281659;

}
