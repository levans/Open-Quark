/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_Locale.java)
 * was generated from CAL module: Cal.Utilities.Locale.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Utilities.Locale module from Java code.
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
 * This module defines the <code>Cal.Utilities.Locale.Locale</code> type, and provides functions for
 * working with locale values, accessing locale properties of the system, and performing
 * locale-sensitive string comparisons through the use of <code>Cal.Utilities.Locale.Collator</code> and <code>Cal.Utilities.Locale.CollationKey</code>.
 * @author Joseph Wong
 */
public final class CAL_Locale {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Utilities.Locale");

	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Utilities.Locale module.
	 */
	public static final class TypeConstructors {
		/**
		 * Represents a string under the rules of a specific <code>Cal.Utilities.Locale.Collator</code>.
		 * <p>
		 * In particular, the following identity holds:
		 * 
		 * <pre> Cal.Utilities.Locale.compareByCollator x y == Cal.Core.Prelude.compare (Cal.Utilities.Locale.makeCollationKey x) (makeCollationKey y)</pre>
		 * 
		 * When strings must be compared multiple times (e.g. when sorting), it is more efficient to
		 * first convert the strings to <code>CollationKey</code>s.
		 */
		public static final QualifiedName CollationKey = 
			QualifiedName.make(CAL_Locale.MODULE_NAME, "CollationKey");

		/**
		 * A collator's function is to support locale-sensitive string comparisons. Using a collator one can
		 * implement searching and sorting of text in a locale-sensitive manner.
		 */
		public static final QualifiedName Collator = 
			QualifiedName.make(CAL_Locale.MODULE_NAME, "Collator");

		/**
		 * Represents the <em>decomposition mode</em> of a <code>Cal.Utilities.Locale.Collator</code>. This property of a collator determines how Unicode composed
		 * characters are handled. The different decomposition modes allow the user to make a speed/completeness tradeoff decision with regards
		 * to collation behavior.
		 */
		public static final QualifiedName CollatorDecompositionMode = 
			QualifiedName.make(
				CAL_Locale.MODULE_NAME, 
				"CollatorDecompositionMode");

		/**
		 * Represents the <em>strength</em> of a <code>Cal.Utilities.Locale.Collator</code>. This property of a collator determines the level of
		 * difference considered significant in comparisons.
		 */
		public static final QualifiedName CollatorStrength = 
			QualifiedName.make(CAL_Locale.MODULE_NAME, "CollatorStrength");

		/**
		 * A <code>Cal.Utilities.Locale.Locale</code> can represent:
		 * <ul>
		 *  <li>
		 *   a language,
		 *  </li>
		 *  <li>
		 *   a language in a particular country/region, or
		 *  </li>
		 *  <li>
		 *   a language in a particular country/region, in a particular variant.
		 *  </li>
		 * </ul>
		 * <p>
		 * The language is to be specified using an ISO 639 code:
		 * <a href='http://www.loc.gov/standards/iso639-2/langcodes.html'>http://www.loc.gov/standards/iso639-2/langcodes.html</a>
		 * <p>
		 * The country/region is to be specified using an ISO 3166 code:
		 * <a href='http://www.iso.org/iso/en/prods-services/iso3166ma/02iso-3166-code-lists/list-en1.html'>http://www.iso.org/iso/en/prods-services/iso3166ma/02iso-3166-code-lists/list-en1.html</a>
		 * <p>
		 * The variant is an OS/vendor specific code that can be any string.
		 * <p>
		 * This is a CAL foreign type corresponding to the Java type <code>java.util.Locale</code>.
		 */
		public static final QualifiedName Locale = 
			QualifiedName.make(CAL_Locale.MODULE_NAME, "Locale");

	}
	/**
	 * This inner class (DataConstructors) contains constants
	 * and methods related to binding to CAL DataConstructors in the Cal.Utilities.Locale module.
	 */
	public static final class DataConstructors {
		/*
		 * DataConstructors for the Cal.Utilities.Locale.CollatorDecompositionMode data type.
		 */

		/**
		 * In this mode, accented characters will not be decomposed for collation. This is the default decomposition
		 * mode for a collator, and provides the fastest collation. However, a collator in this mode will only produce
		 * correct results for texts containing no accents.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr NoDecomposition() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.NoDecomposition);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.Locale.NoDecomposition.
		 * @see #NoDecomposition()
		 */
		public static final QualifiedName NoDecomposition = 
			QualifiedName.make(CAL_Locale.MODULE_NAME, "NoDecomposition");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.Locale.NoDecomposition.
		 * @see #NoDecomposition()
		 */
		public static final int NoDecomposition_ordinal = 0;

		/**
		 * In this mode, characters that are canonical variants according to the Unicode standard will be decomposed for collation.
		 * Accented characters are collated properly in this mode.
		 * <p>
		 * This mode corresponds to Normalization Form D as described in Unicode Technical Report #15:
		 * <a href='http://www.unicode.org/unicode/reports/tr15/'>http://www.unicode.org/unicode/reports/tr15/</a>
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr CanonicalDecomposition() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.CanonicalDecomposition);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.Locale.CanonicalDecomposition.
		 * @see #CanonicalDecomposition()
		 */
		public static final QualifiedName CanonicalDecomposition = 
			QualifiedName.make(CAL_Locale.MODULE_NAME, "CanonicalDecomposition");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.Locale.CanonicalDecomposition.
		 * @see #CanonicalDecomposition()
		 */
		public static final int CanonicalDecomposition_ordinal = 1;

		/**
		 * In this mode, both Unicode canonical variants and Unicode compatibility variants will be decomposed for collation.
		 * <p>
		 * This is the most complete and the slowest decomposition mode.
		 * <p>
		 * This mode corresponds to Normalization Form KD as described in Unicode Technical Report #15:
		 * <a href='http://www.unicode.org/unicode/reports/tr15/'>http://www.unicode.org/unicode/reports/tr15/</a>
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr FullDecomposition() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.FullDecomposition);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.Locale.FullDecomposition.
		 * @see #FullDecomposition()
		 */
		public static final QualifiedName FullDecomposition = 
			QualifiedName.make(CAL_Locale.MODULE_NAME, "FullDecomposition");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.Locale.FullDecomposition.
		 * @see #FullDecomposition()
		 */
		public static final int FullDecomposition_ordinal = 2;

		/*
		 * DataConstructors for the Cal.Utilities.Locale.CollatorStrength data type.
		 */

		/**
		 * The weakest collator strength. For example, "e" and "f" are considered primary differences
		 * but not "e" and "\u00ea" (e with acute accent).
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr CollatorStrengthPrimary() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.CollatorStrengthPrimary);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.Locale.CollatorStrengthPrimary.
		 * @see #CollatorStrengthPrimary()
		 */
		public static final QualifiedName CollatorStrengthPrimary = 
			QualifiedName.make(
				CAL_Locale.MODULE_NAME, 
				"CollatorStrengthPrimary");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.Locale.CollatorStrengthPrimary.
		 * @see #CollatorStrengthPrimary()
		 */
		public static final int CollatorStrengthPrimary_ordinal = 0;

		/**
		 * A collator strength that commonly discerns different accented forms, but does not discern case differences.
		 * For example, "e" and "\u00ea" (e with acute accent) are considered secondary differences but not "e" and "E".
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr CollatorStrengthSecondary() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.CollatorStrengthSecondary);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.Locale.CollatorStrengthSecondary.
		 * @see #CollatorStrengthSecondary()
		 */
		public static final QualifiedName CollatorStrengthSecondary = 
			QualifiedName.make(
				CAL_Locale.MODULE_NAME, 
				"CollatorStrengthSecondary");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.Locale.CollatorStrengthSecondary.
		 * @see #CollatorStrengthSecondary()
		 */
		public static final int CollatorStrengthSecondary_ordinal = 1;

		/**
		 * A collator strength that commonly discerns case differences. For example, "e" and "E" are considered tertiary differences,
		 * but not "\u0001" and "\u0002" (both are control characters).
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr CollatorStrengthTertiary() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.CollatorStrengthTertiary);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.Locale.CollatorStrengthTertiary.
		 * @see #CollatorStrengthTertiary()
		 */
		public static final QualifiedName CollatorStrengthTertiary = 
			QualifiedName.make(
				CAL_Locale.MODULE_NAME, 
				"CollatorStrengthTertiary");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.Locale.CollatorStrengthTertiary.
		 * @see #CollatorStrengthTertiary()
		 */
		public static final int CollatorStrengthTertiary_ordinal = 2;

		/**
		 * The strongest collator strength. Two strings must be identical for the collator to deem them the same.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr CollatorStrengthIdentical() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.CollatorStrengthIdentical);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.Locale.CollatorStrengthIdentical.
		 * @see #CollatorStrengthIdentical()
		 */
		public static final QualifiedName CollatorStrengthIdentical = 
			QualifiedName.make(
				CAL_Locale.MODULE_NAME, 
				"CollatorStrengthIdentical");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.Locale.CollatorStrengthIdentical.
		 * @see #CollatorStrengthIdentical()
		 */
		public static final int CollatorStrengthIdentical_ordinal = 3;

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Utilities.Locale module.
	 */
	public static final class Functions {
		/**
		 * Returns a list of all locales that are supported by the various collator functions.
		 * @return (CAL type: <code>[Cal.Utilities.Locale.Locale]</code>) 
		 *          a list of locales.
		 */
		public static final SourceModel.Expr availableCollatorLocales() {
			return SourceModel.Expr.Var.make(Functions.availableCollatorLocales);
		}

		/**
		 * Name binding for function: availableCollatorLocales.
		 * @see #availableCollatorLocales()
		 */
		public static final QualifiedName availableCollatorLocales = 
			QualifiedName.make(
				CAL_Locale.MODULE_NAME, 
				"availableCollatorLocales");

		/**
		 * Returns a list of all locales that are supported by the various display name functions.
		 * @return (CAL type: <code>[Cal.Utilities.Locale.Locale]</code>) 
		 *          a list of all supported display locales.
		 */
		public static final SourceModel.Expr availableDisplayLocales() {
			return SourceModel.Expr.Var.make(Functions.availableDisplayLocales);
		}

		/**
		 * Name binding for function: availableDisplayLocales.
		 * @see #availableDisplayLocales()
		 */
		public static final QualifiedName availableDisplayLocales = 
			QualifiedName.make(
				CAL_Locale.MODULE_NAME, 
				"availableDisplayLocales");

		/**
		 * The locale "en_CA".
		 * @return (CAL type: <code>Cal.Utilities.Locale.Locale</code>) 
		 */
		public static final SourceModel.Expr canadaEnglishLocale() {
			return SourceModel.Expr.Var.make(Functions.canadaEnglishLocale);
		}

		/**
		 * Name binding for function: canadaEnglishLocale.
		 * @see #canadaEnglishLocale()
		 */
		public static final QualifiedName canadaEnglishLocale = 
			QualifiedName.make(CAL_Locale.MODULE_NAME, "canadaEnglishLocale");

		/**
		 * The locale "fr_CA".
		 * @return (CAL type: <code>Cal.Utilities.Locale.Locale</code>) 
		 */
		public static final SourceModel.Expr canadaFrenchLocale() {
			return SourceModel.Expr.Var.make(Functions.canadaFrenchLocale);
		}

		/**
		 * Name binding for function: canadaFrenchLocale.
		 * @see #canadaFrenchLocale()
		 */
		public static final QualifiedName canadaFrenchLocale = 
			QualifiedName.make(CAL_Locale.MODULE_NAME, "canadaFrenchLocale");

		/**
		 * Returns the canonical string representation of a given locale: &lt;language&gt;[_&lt;country/region&gt;[_&lt;variant&gt;]]
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.Locale.fromCanonicalString
		 * </dl>
		 * 
		 * @param locale (CAL type: <code>Cal.Utilities.Locale.Locale</code>)
		 *          the locale.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the canonical string representation of the locale.
		 */
		public static final SourceModel.Expr canonicalString(SourceModel.Expr locale) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.canonicalString), locale});
		}

		/**
		 * Name binding for function: canonicalString.
		 * @see #canonicalString(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName canonicalString = 
			QualifiedName.make(CAL_Locale.MODULE_NAME, "canonicalString");

		/**
		 * The locale "zh_CN".
		 * @return (CAL type: <code>Cal.Utilities.Locale.Locale</code>) 
		 */
		public static final SourceModel.Expr chinaChineseLocale() {
			return SourceModel.Expr.Var.make(Functions.chinaChineseLocale);
		}

		/**
		 * Name binding for function: chinaChineseLocale.
		 * @see #chinaChineseLocale()
		 */
		public static final QualifiedName chinaChineseLocale = 
			QualifiedName.make(CAL_Locale.MODULE_NAME, "chinaChineseLocale");

		/**
		 * The locale "zh".
		 * @return (CAL type: <code>Cal.Utilities.Locale.Locale</code>) 
		 */
		public static final SourceModel.Expr chineseLocale() {
			return SourceModel.Expr.Var.make(Functions.chineseLocale);
		}

		/**
		 * Name binding for function: chineseLocale.
		 * @see #chineseLocale()
		 */
		public static final QualifiedName chineseLocale = 
			QualifiedName.make(CAL_Locale.MODULE_NAME, "chineseLocale");

		/**
		 * Returns the source string represented by a collation key.
		 * @param collationKey (CAL type: <code>Cal.Utilities.Locale.CollationKey</code>)
		 *          the collation key whose source string is to be returned.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the corresponding source string.
		 */
		public static final SourceModel.Expr collationKeySourceString(SourceModel.Expr collationKey) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.collationKeySourceString), collationKey});
		}

		/**
		 * Name binding for function: collationKeySourceString.
		 * @see #collationKeySourceString(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName collationKeySourceString = 
			QualifiedName.make(
				CAL_Locale.MODULE_NAME, 
				"collationKeySourceString");

		/**
		 * Returns the decomposition mode of the given collator.
		 * @param collator (CAL type: <code>Cal.Utilities.Locale.Collator</code>)
		 *          the collator to query.
		 * @return (CAL type: <code>Cal.Utilities.Locale.CollatorDecompositionMode</code>) 
		 *          the decomposition mode of the collator.
		 */
		public static final SourceModel.Expr collatorDecompositionMode(SourceModel.Expr collator) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.collatorDecompositionMode), collator});
		}

		/**
		 * Name binding for function: collatorDecompositionMode.
		 * @see #collatorDecompositionMode(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName collatorDecompositionMode = 
			QualifiedName.make(
				CAL_Locale.MODULE_NAME, 
				"collatorDecompositionMode");

		/**
		 * Returns the strength of the given collator.
		 * @param collator (CAL type: <code>Cal.Utilities.Locale.Collator</code>)
		 *          the collator to query.
		 * @return (CAL type: <code>Cal.Utilities.Locale.CollatorStrength</code>) 
		 *          the strength of the collator.
		 */
		public static final SourceModel.Expr collatorStrength(SourceModel.Expr collator) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.collatorStrength), collator});
		}

		/**
		 * Name binding for function: collatorStrength.
		 * @see #collatorStrength(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName collatorStrength = 
			QualifiedName.make(CAL_Locale.MODULE_NAME, "collatorStrength");

		/**
		 * Compares the relative order of two strings based on the given collator's collation rules.
		 * @param collator (CAL type: <code>Cal.Utilities.Locale.Collator</code>)
		 *          the collator to use.
		 * @param x (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the first string to be compared.
		 * @param y (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the second string to be compared.
		 * @return (CAL type: <code>Cal.Core.Prelude.Ordering</code>) 
		 *          <code>Cal.Core.Prelude.LT</code>, <code>Cal.Core.Prelude.EQ</code>, <code>Cal.Core.Prelude.GT</code> depending on whether <code>x</code> is less than, equal to, or greater than <code>y</code>
		 * according to the collator.
		 */
		public static final SourceModel.Expr compareByCollator(SourceModel.Expr collator, SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.compareByCollator), collator, x, y});
		}

		/**
		 * @see #compareByCollator(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param collator
		 * @param x
		 * @param y
		 * @return the SourceModel.Expr representing an application of compareByCollator
		 */
		public static final SourceModel.Expr compareByCollator(SourceModel.Expr collator, java.lang.String x, java.lang.String y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.compareByCollator), collator, SourceModel.Expr.makeStringValue(x), SourceModel.Expr.makeStringValue(y)});
		}

		/**
		 * Name binding for function: compareByCollator.
		 * @see #compareByCollator(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName compareByCollator = 
			QualifiedName.make(CAL_Locale.MODULE_NAME, "compareByCollator");

		/**
		 * Returns the country/region code for the locale, which can be an empty string if no country/region
		 * is specified by the locale.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.Locale.iso3Country, Cal.Utilities.Locale.displayCountryInLocale, Cal.Utilities.Locale.displayCountryInCurrentLocale
		 * </dl>
		 * 
		 * @param locale (CAL type: <code>Cal.Utilities.Locale.Locale</code>)
		 *          the locale to query.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the country/region code for the locale, which can be an empty string.
		 */
		public static final SourceModel.Expr country(SourceModel.Expr locale) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.country), locale});
		}

		/**
		 * Name binding for function: country.
		 * @see #country(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName country = 
			QualifiedName.make(CAL_Locale.MODULE_NAME, "country");

		/**
		 * Returns the locale associated with the current execution context. This is a constant for
		 * a particular execution context.
		 * @return (CAL type: <code>Cal.Utilities.Locale.Locale</code>) 
		 *          the locale associated with the current execution context.
		 */
		public static final SourceModel.Expr currentLocale() {
			return SourceModel.Expr.Var.make(Functions.currentLocale);
		}

		/**
		 * Name binding for function: currentLocale.
		 * @see #currentLocale()
		 */
		public static final QualifiedName currentLocale = 
			QualifiedName.make(CAL_Locale.MODULE_NAME, "currentLocale");

		/**
		 * A collator for the current locale.
		 * @return (CAL type: <code>Cal.Utilities.Locale.Collator</code>) 
		 *          a collator for the current locale.
		 */
		public static final SourceModel.Expr currentLocaleCollator() {
			return SourceModel.Expr.Var.make(Functions.currentLocaleCollator);
		}

		/**
		 * Name binding for function: currentLocaleCollator.
		 * @see #currentLocaleCollator()
		 */
		public static final QualifiedName currentLocaleCollator = 
			QualifiedName.make(CAL_Locale.MODULE_NAME, "currentLocaleCollator");

		/**
		 * Returns the default locale of the underlying virtual machine. This may be different from the
		 * <code>Cal.Utilities.Locale.currentLocale</code>.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.Locale.currentLocale
		 * </dl>
		 * 
		 * @return (CAL type: <code>Cal.Utilities.Locale.Locale</code>) 
		 *          the default locale.
		 */
		public static final SourceModel.Expr defaultLocale() {
			return SourceModel.Expr.Var.make(Functions.defaultLocale);
		}

		/**
		 * Name binding for function: defaultLocale.
		 * @see #defaultLocale()
		 */
		public static final QualifiedName defaultLocale = 
			QualifiedName.make(CAL_Locale.MODULE_NAME, "defaultLocale");

		/**
		 * Returns a display name for the locale's country/region, localized in the current locale.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.Locale.country, Cal.Utilities.Locale.iso3Country, Cal.Utilities.Locale.displayCountryInLocale
		 * </dl>
		 * 
		 * @param locale (CAL type: <code>Cal.Utilities.Locale.Locale</code>)
		 *          the locale whose country/region's display name is to be returned.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the display name of <code>locale</code>'s country/region localized in <code>currentLocale</code>.
		 */
		public static final SourceModel.Expr displayCountryInCurrentLocale(SourceModel.Expr locale) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.displayCountryInCurrentLocale), locale});
		}

		/**
		 * Name binding for function: displayCountryInCurrentLocale.
		 * @see #displayCountryInCurrentLocale(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName displayCountryInCurrentLocale = 
			QualifiedName.make(
				CAL_Locale.MODULE_NAME, 
				"displayCountryInCurrentLocale");

		/**
		 * Returns a display name for the locale's country/region, localized in the given display locale.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.Locale.country, Cal.Utilities.Locale.iso3Country, Cal.Utilities.Locale.displayCountryInCurrentLocale
		 * </dl>
		 * 
		 * @param locale (CAL type: <code>Cal.Utilities.Locale.Locale</code>)
		 *          the locale whose country/region's display name is to be returned.
		 * @param displayLocale (CAL type: <code>Cal.Utilities.Locale.Locale</code>)
		 *          the locale in which the name should be localized.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the display name of <code>locale</code>'s country/region localized in <code>displayLocale</code>.
		 */
		public static final SourceModel.Expr displayCountryInLocale(SourceModel.Expr locale, SourceModel.Expr displayLocale) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.displayCountryInLocale), locale, displayLocale});
		}

		/**
		 * Name binding for function: displayCountryInLocale.
		 * @see #displayCountryInLocale(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName displayCountryInLocale = 
			QualifiedName.make(CAL_Locale.MODULE_NAME, "displayCountryInLocale");

		/**
		 * Returns a display name for the locale's language, localized in the current locale.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.Locale.language, Cal.Utilities.Locale.iso3Language, Cal.Utilities.Locale.displayLanguageInLocale
		 * </dl>
		 * 
		 * @param locale (CAL type: <code>Cal.Utilities.Locale.Locale</code>)
		 *          the locale whose language's display name is to be returned.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the display name of <code>locale</code>'s language localized in <code>Cal.Utilities.Locale.currentLocale</code>.
		 */
		public static final SourceModel.Expr displayLanguageInCurrentLocale(SourceModel.Expr locale) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.displayLanguageInCurrentLocale), locale});
		}

		/**
		 * Name binding for function: displayLanguageInCurrentLocale.
		 * @see #displayLanguageInCurrentLocale(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName displayLanguageInCurrentLocale = 
			QualifiedName.make(
				CAL_Locale.MODULE_NAME, 
				"displayLanguageInCurrentLocale");

		/**
		 * Returns a display name for the locale's language, localized in the given display locale.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.Locale.language, Cal.Utilities.Locale.iso3Language, Cal.Utilities.Locale.displayLanguageInCurrentLocale
		 * </dl>
		 * 
		 * @param locale (CAL type: <code>Cal.Utilities.Locale.Locale</code>)
		 *          the locale whose language's display name is to be returned.
		 * @param displayLocale (CAL type: <code>Cal.Utilities.Locale.Locale</code>)
		 *          the locale in which the name should be localized.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the display name of <code>locale</code>'s language localized in <code>displayLocale</code>.
		 */
		public static final SourceModel.Expr displayLanguageInLocale(SourceModel.Expr locale, SourceModel.Expr displayLocale) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.displayLanguageInLocale), locale, displayLocale});
		}

		/**
		 * Name binding for function: displayLanguageInLocale.
		 * @see #displayLanguageInLocale(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName displayLanguageInLocale = 
			QualifiedName.make(
				CAL_Locale.MODULE_NAME, 
				"displayLanguageInLocale");

		/**
		 * Returns a display name for the locale, localized in the current locale.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.Locale.displayNameInLocale, Cal.Utilities.Locale.displayLanguageInCurrentLocale, Cal.Utilities.Locale.displayCountryInCurrentLocale, Cal.Utilities.Locale.displayVariantInCurrentLocale
		 * </dl>
		 * 
		 * @param locale (CAL type: <code>Cal.Utilities.Locale.Locale</code>)
		 *          the locale whose display name is to be returned.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the display name of <code>locale</code> localized in <code>Cal.Utilities.Locale.currentLocale</code>.
		 */
		public static final SourceModel.Expr displayNameInCurrentLocale(SourceModel.Expr locale) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.displayNameInCurrentLocale), locale});
		}

		/**
		 * Name binding for function: displayNameInCurrentLocale.
		 * @see #displayNameInCurrentLocale(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName displayNameInCurrentLocale = 
			QualifiedName.make(
				CAL_Locale.MODULE_NAME, 
				"displayNameInCurrentLocale");

		/**
		 * Returns a display name for the locale, localized in the given display locale.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.Locale.displayLanguageInLocale, Cal.Utilities.Locale.displayCountryInLocale, Cal.Utilities.Locale.displayVariantInLocale
		 * </dl>
		 * 
		 * @param locale (CAL type: <code>Cal.Utilities.Locale.Locale</code>)
		 *          the locale whose display name is to be returned.
		 * @param displayLocale (CAL type: <code>Cal.Utilities.Locale.Locale</code>)
		 *          the locale in which the name should be localized.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the display name of <code>locale</code> localized in <code>displayLocale</code>.
		 */
		public static final SourceModel.Expr displayNameInLocale(SourceModel.Expr locale, SourceModel.Expr displayLocale) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.displayNameInLocale), locale, displayLocale});
		}

		/**
		 * Name binding for function: displayNameInLocale.
		 * @see #displayNameInLocale(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName displayNameInLocale = 
			QualifiedName.make(CAL_Locale.MODULE_NAME, "displayNameInLocale");

		/**
		 * Returns a display name for the locale's variant, localized in the current locale.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.Locale.variant, Cal.Utilities.Locale.displayVariantInLocale
		 * </dl>
		 * 
		 * @param locale (CAL type: <code>Cal.Utilities.Locale.Locale</code>)
		 *          the locale whose variant's display name is to be returned.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the display name of <code>locale</code>'s variant localized in <code>currentLocale</code>.
		 */
		public static final SourceModel.Expr displayVariantInCurrentLocale(SourceModel.Expr locale) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.displayVariantInCurrentLocale), locale});
		}

		/**
		 * Name binding for function: displayVariantInCurrentLocale.
		 * @see #displayVariantInCurrentLocale(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName displayVariantInCurrentLocale = 
			QualifiedName.make(
				CAL_Locale.MODULE_NAME, 
				"displayVariantInCurrentLocale");

		/**
		 * Returns a display name for the locale's variant, localized in the given display locale.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.Locale.variant, Cal.Utilities.Locale.displayVariantInCurrentLocale
		 * </dl>
		 * 
		 * @param locale (CAL type: <code>Cal.Utilities.Locale.Locale</code>)
		 *          the locale whose variant's display name is to be returned.
		 * @param displayLocale (CAL type: <code>Cal.Utilities.Locale.Locale</code>)
		 *          the locale in which the name should be localized.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the display name of <code>locale</code>'s variant localized in <code>displayLocale</code>.
		 */
		public static final SourceModel.Expr displayVariantInLocale(SourceModel.Expr locale, SourceModel.Expr displayLocale) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.displayVariantInLocale), locale, displayLocale});
		}

		/**
		 * Name binding for function: displayVariantInLocale.
		 * @see #displayVariantInLocale(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName displayVariantInLocale = 
			QualifiedName.make(CAL_Locale.MODULE_NAME, "displayVariantInLocale");

		/**
		 * The locale "en".
		 * @return (CAL type: <code>Cal.Utilities.Locale.Locale</code>) 
		 */
		public static final SourceModel.Expr englishLocale() {
			return SourceModel.Expr.Var.make(Functions.englishLocale);
		}

		/**
		 * Name binding for function: englishLocale.
		 * @see #englishLocale()
		 */
		public static final QualifiedName englishLocale = 
			QualifiedName.make(CAL_Locale.MODULE_NAME, "englishLocale");

		/**
		 * Compares the equality of two strings based on the given collator's collation rules.
		 * @param collator (CAL type: <code>Cal.Utilities.Locale.Collator</code>)
		 *          the collator to use.
		 * @param x (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the first string to be compared.
		 * @param y (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the second string to be compared.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the argument strings are equal, according to the collator; <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr equalsByCollator(SourceModel.Expr collator, SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.equalsByCollator), collator, x, y});
		}

		/**
		 * @see #equalsByCollator(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param collator
		 * @param x
		 * @param y
		 * @return the SourceModel.Expr representing an application of equalsByCollator
		 */
		public static final SourceModel.Expr equalsByCollator(SourceModel.Expr collator, java.lang.String x, java.lang.String y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.equalsByCollator), collator, SourceModel.Expr.makeStringValue(x), SourceModel.Expr.makeStringValue(y)});
		}

		/**
		 * Name binding for function: equalsByCollator.
		 * @see #equalsByCollator(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName equalsByCollator = 
			QualifiedName.make(CAL_Locale.MODULE_NAME, "equalsByCollator");

		/**
		 * Returns the <em>fallback</em> locale of the given locale.
		 * <p>
		 * The CAL locale fallback mechanism operates by removing the finest level of detail
		 * from a locale:
		 * <ul>
		 *  <li>
		 *   &lt;language&gt;_&lt;country/region&gt;_&lt;variant&gt; -&gt; &lt;language&gt;_&lt;country/region&gt;
		 *  </li>
		 *  <li>
		 *   &lt;language&gt;_&lt;country/region&gt; -&gt; &lt;language&gt;
		 *  </li>
		 *  <li>
		 *   &lt;language&gt; -&gt; <code>Cal.Utilities.Locale.invariantLocale</code>
		 *  </li>
		 *  <li>
		 *   invariantLocale -&gt; invariantLocale
		 *  </li>
		 * </ul>
		 * 
		 * @param locale (CAL type: <code>Cal.Utilities.Locale.Locale</code>)
		 *          the locale whose fallback locale is to be returned.
		 * @return (CAL type: <code>Cal.Utilities.Locale.Locale</code>) 
		 *          the corresponding fallback locale.
		 */
		public static final SourceModel.Expr fallback(SourceModel.Expr locale) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fallback), locale});
		}

		/**
		 * Name binding for function: fallback.
		 * @see #fallback(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fallback = 
			QualifiedName.make(CAL_Locale.MODULE_NAME, "fallback");

		/**
		 * The locale "fr_FR".
		 * @return (CAL type: <code>Cal.Utilities.Locale.Locale</code>) 
		 */
		public static final SourceModel.Expr franceFrenchLocale() {
			return SourceModel.Expr.Var.make(Functions.franceFrenchLocale);
		}

		/**
		 * Name binding for function: franceFrenchLocale.
		 * @see #franceFrenchLocale()
		 */
		public static final QualifiedName franceFrenchLocale = 
			QualifiedName.make(CAL_Locale.MODULE_NAME, "franceFrenchLocale");

		/**
		 * The locale "fr".
		 * @return (CAL type: <code>Cal.Utilities.Locale.Locale</code>) 
		 */
		public static final SourceModel.Expr frenchLocale() {
			return SourceModel.Expr.Var.make(Functions.frenchLocale);
		}

		/**
		 * Name binding for function: frenchLocale.
		 * @see #frenchLocale()
		 */
		public static final QualifiedName frenchLocale = 
			QualifiedName.make(CAL_Locale.MODULE_NAME, "frenchLocale");

		/**
		 * Constructs a locale based on its canonical string representation: &lt;language&gt;[_&lt;country/region&gt;[_&lt;variant&gt;]]
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.Locale.canonicalString
		 * </dl>
		 * 
		 * @param canonicalString (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the locale's canonical string representation.
		 * @return (CAL type: <code>Cal.Utilities.Locale.Locale</code>) 
		 *          the corresponding locale.
		 */
		public static final SourceModel.Expr fromCanonicalString(SourceModel.Expr canonicalString) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromCanonicalString), canonicalString});
		}

		/**
		 * @see #fromCanonicalString(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param canonicalString
		 * @return the SourceModel.Expr representing an application of fromCanonicalString
		 */
		public static final SourceModel.Expr fromCanonicalString(java.lang.String canonicalString) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromCanonicalString), SourceModel.Expr.makeStringValue(canonicalString)});
		}

		/**
		 * Name binding for function: fromCanonicalString.
		 * @see #fromCanonicalString(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fromCanonicalString = 
			QualifiedName.make(CAL_Locale.MODULE_NAME, "fromCanonicalString");

		/**
		 * The locale "de".
		 * @return (CAL type: <code>Cal.Utilities.Locale.Locale</code>) 
		 */
		public static final SourceModel.Expr germanLocale() {
			return SourceModel.Expr.Var.make(Functions.germanLocale);
		}

		/**
		 * Name binding for function: germanLocale.
		 * @see #germanLocale()
		 */
		public static final QualifiedName germanLocale = 
			QualifiedName.make(CAL_Locale.MODULE_NAME, "germanLocale");

		/**
		 * The locale "de_DE".
		 * @return (CAL type: <code>Cal.Utilities.Locale.Locale</code>) 
		 */
		public static final SourceModel.Expr germanyGermanLocale() {
			return SourceModel.Expr.Var.make(Functions.germanyGermanLocale);
		}

		/**
		 * Name binding for function: germanyGermanLocale.
		 * @see #germanyGermanLocale()
		 */
		public static final QualifiedName germanyGermanLocale = 
			QualifiedName.make(CAL_Locale.MODULE_NAME, "germanyGermanLocale");

		/**
		 * The locale constant for the <em>invariant locale</em>, which is treated by the CAL platform
		 * as the final fallback locale for the lookup of localized resources.
		 * <p>
		 * The invariant locale has the canonical string representation of the empty string (""), and
		 * is not associated with any language or country.
		 * 
		 * @return (CAL type: <code>Cal.Utilities.Locale.Locale</code>) 
		 */
		public static final SourceModel.Expr invariantLocale() {
			return SourceModel.Expr.Var.make(Functions.invariantLocale);
		}

		/**
		 * Name binding for function: invariantLocale.
		 * @see #invariantLocale()
		 */
		public static final QualifiedName invariantLocale = 
			QualifiedName.make(CAL_Locale.MODULE_NAME, "invariantLocale");

		/**
		 * Returns a three-letter abbreviation for the locale's country/region. If the locale does not specify a country/region,
		 * an empty string is returned. Otherwise, the returned string is an uppercase ISO 3166 alpha-3 country/region code.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.Locale.country, Cal.Utilities.Locale.displayCountryInLocale, Cal.Utilities.Locale.displayCountryInCurrentLocale
		 * </dl>
		 * 
		 * @param locale (CAL type: <code>Cal.Utilities.Locale.Locale</code>)
		 *          the locale to query.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the country/region code for the locale, which can be an empty string.
		 */
		public static final SourceModel.Expr iso3Country(SourceModel.Expr locale) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.iso3Country), locale});
		}

		/**
		 * Name binding for function: iso3Country.
		 * @see #iso3Country(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName iso3Country = 
			QualifiedName.make(CAL_Locale.MODULE_NAME, "iso3Country");

		/**
		 * Returns a three-letter abbreviation for the locale's language. If the locale does not specify a language
		 * (e.g. the <code>Cal.Utilities.Locale.invariantLocale</code>), an empty string is returned. Otherwise, the returned string is a lowercase
		 * ISO 639-2/T language code.
		 * <p>
		 * ISO 639 standard: <a href='http://www.loc.gov/standards/iso639-2/langcodes.html'>http://www.loc.gov/standards/iso639-2/langcodes.html</a>
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.Locale.language, Cal.Utilities.Locale.displayLanguageInLocale, Cal.Utilities.Locale.displayLanguageInCurrentLocale
		 * </dl>
		 * 
		 * @param locale (CAL type: <code>Cal.Utilities.Locale.Locale</code>)
		 *          the locale to query.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the language code for the locale, which can be an empty string.
		 */
		public static final SourceModel.Expr iso3Language(SourceModel.Expr locale) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.iso3Language), locale});
		}

		/**
		 * Name binding for function: iso3Language.
		 * @see #iso3Language(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName iso3Language = 
			QualifiedName.make(CAL_Locale.MODULE_NAME, "iso3Language");

		/**
		 * The locale "it".
		 * @return (CAL type: <code>Cal.Utilities.Locale.Locale</code>) 
		 */
		public static final SourceModel.Expr italianLocale() {
			return SourceModel.Expr.Var.make(Functions.italianLocale);
		}

		/**
		 * Name binding for function: italianLocale.
		 * @see #italianLocale()
		 */
		public static final QualifiedName italianLocale = 
			QualifiedName.make(CAL_Locale.MODULE_NAME, "italianLocale");

		/**
		 * The locale "it_IT".
		 * @return (CAL type: <code>Cal.Utilities.Locale.Locale</code>) 
		 */
		public static final SourceModel.Expr italyItalianLocale() {
			return SourceModel.Expr.Var.make(Functions.italyItalianLocale);
		}

		/**
		 * Name binding for function: italyItalianLocale.
		 * @see #italyItalianLocale()
		 */
		public static final QualifiedName italyItalianLocale = 
			QualifiedName.make(CAL_Locale.MODULE_NAME, "italyItalianLocale");

		/**
		 * The locale "ja_JP".
		 * @return (CAL type: <code>Cal.Utilities.Locale.Locale</code>) 
		 */
		public static final SourceModel.Expr japanJapaneseLocale() {
			return SourceModel.Expr.Var.make(Functions.japanJapaneseLocale);
		}

		/**
		 * Name binding for function: japanJapaneseLocale.
		 * @see #japanJapaneseLocale()
		 */
		public static final QualifiedName japanJapaneseLocale = 
			QualifiedName.make(CAL_Locale.MODULE_NAME, "japanJapaneseLocale");

		/**
		 * The locale "ja".
		 * @return (CAL type: <code>Cal.Utilities.Locale.Locale</code>) 
		 */
		public static final SourceModel.Expr japaneseLocale() {
			return SourceModel.Expr.Var.make(Functions.japaneseLocale);
		}

		/**
		 * Name binding for function: japaneseLocale.
		 * @see #japaneseLocale()
		 */
		public static final QualifiedName japaneseLocale = 
			QualifiedName.make(CAL_Locale.MODULE_NAME, "japaneseLocale");

		/**
		 * The locale "ko_KR".
		 * @return (CAL type: <code>Cal.Utilities.Locale.Locale</code>) 
		 */
		public static final SourceModel.Expr koreaKoreanLocale() {
			return SourceModel.Expr.Var.make(Functions.koreaKoreanLocale);
		}

		/**
		 * Name binding for function: koreaKoreanLocale.
		 * @see #koreaKoreanLocale()
		 */
		public static final QualifiedName koreaKoreanLocale = 
			QualifiedName.make(CAL_Locale.MODULE_NAME, "koreaKoreanLocale");

		/**
		 * The locale "ko".
		 * @return (CAL type: <code>Cal.Utilities.Locale.Locale</code>) 
		 */
		public static final SourceModel.Expr koreanLocale() {
			return SourceModel.Expr.Var.make(Functions.koreanLocale);
		}

		/**
		 * Name binding for function: koreanLocale.
		 * @see #koreanLocale()
		 */
		public static final QualifiedName koreanLocale = 
			QualifiedName.make(CAL_Locale.MODULE_NAME, "koreanLocale");

		/**
		 * Returns the language code for the locale, which can be an empty string if no language
		 * is specified by the locale (for example in the case of the <code>Cal.Utilities.Locale.invariantLocale</code>).
		 * <p>
		 * Note that ISO 639 is not a stable standard, and for compatibility reasons the language code
		 * returned by this function may correspond to an <em>older</em> code for the language. Therefore,
		 * to check the language of a locale, do not use a simply string comparison:
		 * 
		 * <pre> // incorrect way of comparing locale languages
		 *  language locale == "he"
		 * </pre>
		 * 
		 * but rather do this:
		 * 
		 * <pre> // the correct way of comparing locale languages
		 *  language locale == language (Cal.Utilities.Locale.makeLocaleFromLanguage "he")</pre>
		 * 
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.Locale.iso3Language, Cal.Utilities.Locale.displayLanguageInLocale, Cal.Utilities.Locale.displayLanguageInCurrentLocale
		 * </dl>
		 * 
		 * @param locale (CAL type: <code>Cal.Utilities.Locale.Locale</code>)
		 *          the locale to query.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the language code for the locale, which can be an empty string.
		 */
		public static final SourceModel.Expr language(SourceModel.Expr locale) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.language), locale});
		}

		/**
		 * Name binding for function: language.
		 * @see #language(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName language = 
			QualifiedName.make(CAL_Locale.MODULE_NAME, "language");

		/**
		 * Constructs a collation key based on a given collator and a given string.
		 * @param collator (CAL type: <code>Cal.Utilities.Locale.Collator</code>)
		 *          the collator whose rules are to be used.
		 * @param source (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the string to be represented by a collation key.
		 * @return (CAL type: <code>Cal.Utilities.Locale.CollationKey</code>) 
		 *          the corresponding collation key.
		 */
		public static final SourceModel.Expr makeCollationKey(SourceModel.Expr collator, SourceModel.Expr source) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeCollationKey), collator, source});
		}

		/**
		 * @see #makeCollationKey(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param collator
		 * @param source
		 * @return the SourceModel.Expr representing an application of makeCollationKey
		 */
		public static final SourceModel.Expr makeCollationKey(SourceModel.Expr collator, java.lang.String source) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeCollationKey), collator, SourceModel.Expr.makeStringValue(source)});
		}

		/**
		 * Name binding for function: makeCollationKey.
		 * @see #makeCollationKey(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeCollationKey = 
			QualifiedName.make(CAL_Locale.MODULE_NAME, "makeCollationKey");

		/**
		 * Constructs a collator for the given locale.
		 * @param locale (CAL type: <code>Cal.Utilities.Locale.Locale</code>)
		 *          the desired locale.
		 * @return (CAL type: <code>Cal.Utilities.Locale.Collator</code>) 
		 *          a collator for the given locale.
		 */
		public static final SourceModel.Expr makeCollator(SourceModel.Expr locale) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeCollator), locale});
		}

		/**
		 * Name binding for function: makeCollator.
		 * @see #makeCollator(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeCollator = 
			QualifiedName.make(CAL_Locale.MODULE_NAME, "makeCollator");

		/**
		 * Constructs a collator for the given locale and with the given collator decomposition mode.
		 * @param locale (CAL type: <code>Cal.Utilities.Locale.Locale</code>)
		 *          the desired locale.
		 * @param mode (CAL type: <code>Cal.Utilities.Locale.CollatorDecompositionMode</code>)
		 *          the desired decomposition mode.
		 * @return (CAL type: <code>Cal.Utilities.Locale.Collator</code>) 
		 *          a collator for the given locale and decomposition mode.
		 */
		public static final SourceModel.Expr makeCollatorWithDecompositionMode(SourceModel.Expr locale, SourceModel.Expr mode) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeCollatorWithDecompositionMode), locale, mode});
		}

		/**
		 * Name binding for function: makeCollatorWithDecompositionMode.
		 * @see #makeCollatorWithDecompositionMode(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeCollatorWithDecompositionMode = 
			QualifiedName.make(
				CAL_Locale.MODULE_NAME, 
				"makeCollatorWithDecompositionMode");

		/**
		 * Constructs a collator for the given locale and with the given collator strength.
		 * @param locale (CAL type: <code>Cal.Utilities.Locale.Locale</code>)
		 *          the desired locale.
		 * @param strength (CAL type: <code>Cal.Utilities.Locale.CollatorStrength</code>)
		 *          the desired strength.
		 * @return (CAL type: <code>Cal.Utilities.Locale.Collator</code>) 
		 *          a collator for the given locale and strength.
		 */
		public static final SourceModel.Expr makeCollatorWithStrength(SourceModel.Expr locale, SourceModel.Expr strength) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeCollatorWithStrength), locale, strength});
		}

		/**
		 * Name binding for function: makeCollatorWithStrength.
		 * @see #makeCollatorWithStrength(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeCollatorWithStrength = 
			QualifiedName.make(
				CAL_Locale.MODULE_NAME, 
				"makeCollatorWithStrength");

		/**
		 * Constructs a collator for the given locale and with the given collator strength and decomposition mode.
		 * @param locale (CAL type: <code>Cal.Utilities.Locale.Locale</code>)
		 *          the desired locale.
		 * @param strength (CAL type: <code>Cal.Utilities.Locale.CollatorStrength</code>)
		 *          the desired strength.
		 * @param mode (CAL type: <code>Cal.Utilities.Locale.CollatorDecompositionMode</code>)
		 *          the desired decomposition mode.
		 * @return (CAL type: <code>Cal.Utilities.Locale.Collator</code>) 
		 *          a collator for the given locale, strength, and decomposition mode.
		 */
		public static final SourceModel.Expr makeCollatorWithStrengthAndDecompositionMode(SourceModel.Expr locale, SourceModel.Expr strength, SourceModel.Expr mode) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeCollatorWithStrengthAndDecompositionMode), locale, strength, mode});
		}

		/**
		 * Name binding for function: makeCollatorWithStrengthAndDecompositionMode.
		 * @see #makeCollatorWithStrengthAndDecompositionMode(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeCollatorWithStrengthAndDecompositionMode = 
			QualifiedName.make(
				CAL_Locale.MODULE_NAME, 
				"makeCollatorWithStrengthAndDecompositionMode");

		/**
		 * Constructs a <code>Cal.Utilities.Locale.Locale</code> from a two-letter ISO 639 language code.
		 * <p>
		 * Note that ISO 639 is not a stable standard, and for compatibility reasons the language code
		 * returned by the <code>Cal.Utilities.Locale.language</code> function may correspond to an <em>older</em> code for the language.
		 * <p>
		 * ISO 639 standard: <a href='http://www.loc.gov/standards/iso639-2/langcodes.html'>http://www.loc.gov/standards/iso639-2/langcodes.html</a>
		 * 
		 * @param language (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          a two-letter ISO 639 language code, in lowercase.
		 * @return (CAL type: <code>Cal.Utilities.Locale.Locale</code>) 
		 *          a locale representing the language.
		 */
		public static final SourceModel.Expr makeLocaleFromLanguage(SourceModel.Expr language) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeLocaleFromLanguage), language});
		}

		/**
		 * @see #makeLocaleFromLanguage(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param language
		 * @return the SourceModel.Expr representing an application of makeLocaleFromLanguage
		 */
		public static final SourceModel.Expr makeLocaleFromLanguage(java.lang.String language) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeLocaleFromLanguage), SourceModel.Expr.makeStringValue(language)});
		}

		/**
		 * Name binding for function: makeLocaleFromLanguage.
		 * @see #makeLocaleFromLanguage(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeLocaleFromLanguage = 
			QualifiedName.make(CAL_Locale.MODULE_NAME, "makeLocaleFromLanguage");

		/**
		 * Constructs a <code>Cal.Utilities.Locale.Locale</code> from a two-letter ISO 639 language code and a two-letter
		 * ISO 3166 country/region code.
		 * <p>
		 * Note that the ISO standards are not stable, and for compatibility reasons the language code
		 * returned by the <code>Cal.Utilities.Locale.language</code> function may correspond to an <em>older</em> code for the language.
		 * <p>
		 * ISO 639 standard: <a href='http://www.loc.gov/standards/iso639-2/langcodes.html'>http://www.loc.gov/standards/iso639-2/langcodes.html</a>
		 * <p>
		 * ISO 3166 standard: <a href='http://www.iso.org/iso/en/prods-services/iso3166ma/02iso-3166-code-lists/list-en1.html'>http://www.iso.org/iso/en/prods-services/iso3166ma/02iso-3166-code-lists/list-en1.html</a>
		 * 
		 * @param language (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          a two-letter ISO 639 language code, in lowercase.
		 * @param country (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          a two-letter ISO 3166 country/region code, in uppercase.
		 * @return (CAL type: <code>Cal.Utilities.Locale.Locale</code>) 
		 *          a locale representing the language and country/region.
		 */
		public static final SourceModel.Expr makeLocaleFromLanguageCountry(SourceModel.Expr language, SourceModel.Expr country) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeLocaleFromLanguageCountry), language, country});
		}

		/**
		 * @see #makeLocaleFromLanguageCountry(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param language
		 * @param country
		 * @return the SourceModel.Expr representing an application of makeLocaleFromLanguageCountry
		 */
		public static final SourceModel.Expr makeLocaleFromLanguageCountry(java.lang.String language, java.lang.String country) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeLocaleFromLanguageCountry), SourceModel.Expr.makeStringValue(language), SourceModel.Expr.makeStringValue(country)});
		}

		/**
		 * Name binding for function: makeLocaleFromLanguageCountry.
		 * @see #makeLocaleFromLanguageCountry(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeLocaleFromLanguageCountry = 
			QualifiedName.make(
				CAL_Locale.MODULE_NAME, 
				"makeLocaleFromLanguageCountry");

		/**
		 * Constructs a <code>Cal.Utilities.Locale.Locale</code> from a two-letter ISO 639 language code, a two-letter
		 * ISO 3166 country/region code, and a variant.
		 * <p>
		 * Note that the ISO standards are not stable, and for compatibility reasons the language code
		 * returned by the <code>Cal.Utilities.Locale.language</code> function may correspond to an <em>older</em> code for the language.
		 * <p>
		 * ISO 639 standard: <a href='http://www.loc.gov/standards/iso639-2/langcodes.html'>http://www.loc.gov/standards/iso639-2/langcodes.html</a>
		 * <p>
		 * ISO 3166 standard: <a href='http://www.iso.org/iso/en/prods-services/iso3166ma/02iso-3166-code-lists/list-en1.html'>http://www.iso.org/iso/en/prods-services/iso3166ma/02iso-3166-code-lists/list-en1.html</a>
		 * 
		 * @param language (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          a two-letter ISO 639 language code, in lowercase.
		 * @param country (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          a two-letter ISO 3166 country/region code, in uppercase.
		 * @param variant (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          an OS/vendor specific code, which can be any string.
		 * @return (CAL type: <code>Cal.Utilities.Locale.Locale</code>) 
		 *          a locale representing the language, country/region and variant.
		 */
		public static final SourceModel.Expr makeLocaleFromLanguageCountryVariant(SourceModel.Expr language, SourceModel.Expr country, SourceModel.Expr variant) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeLocaleFromLanguageCountryVariant), language, country, variant});
		}

		/**
		 * @see #makeLocaleFromLanguageCountryVariant(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param language
		 * @param country
		 * @param variant
		 * @return the SourceModel.Expr representing an application of makeLocaleFromLanguageCountryVariant
		 */
		public static final SourceModel.Expr makeLocaleFromLanguageCountryVariant(java.lang.String language, java.lang.String country, java.lang.String variant) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeLocaleFromLanguageCountryVariant), SourceModel.Expr.makeStringValue(language), SourceModel.Expr.makeStringValue(country), SourceModel.Expr.makeStringValue(variant)});
		}

		/**
		 * Name binding for function: makeLocaleFromLanguageCountryVariant.
		 * @see #makeLocaleFromLanguageCountryVariant(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeLocaleFromLanguageCountryVariant = 
			QualifiedName.make(
				CAL_Locale.MODULE_NAME, 
				"makeLocaleFromLanguageCountryVariant");

		/**
		 * The locale "zh_CN".
		 * @return (CAL type: <code>Cal.Utilities.Locale.Locale</code>) 
		 */
		public static final SourceModel.Expr simplifiedChineseLocale() {
			return SourceModel.Expr.Var.make(Functions.simplifiedChineseLocale);
		}

		/**
		 * Name binding for function: simplifiedChineseLocale.
		 * @see #simplifiedChineseLocale()
		 */
		public static final QualifiedName simplifiedChineseLocale = 
			QualifiedName.make(
				CAL_Locale.MODULE_NAME, 
				"simplifiedChineseLocale");

		/**
		 * The locale "zh_TW".
		 * @return (CAL type: <code>Cal.Utilities.Locale.Locale</code>) 
		 */
		public static final SourceModel.Expr taiwanChineseLocale() {
			return SourceModel.Expr.Var.make(Functions.taiwanChineseLocale);
		}

		/**
		 * Name binding for function: taiwanChineseLocale.
		 * @see #taiwanChineseLocale()
		 */
		public static final QualifiedName taiwanChineseLocale = 
			QualifiedName.make(CAL_Locale.MODULE_NAME, "taiwanChineseLocale");

		/**
		 * The locale "zh_TW".
		 * @return (CAL type: <code>Cal.Utilities.Locale.Locale</code>) 
		 */
		public static final SourceModel.Expr traditionalChineseLocale() {
			return SourceModel.Expr.Var.make(Functions.traditionalChineseLocale);
		}

		/**
		 * Name binding for function: traditionalChineseLocale.
		 * @see #traditionalChineseLocale()
		 */
		public static final QualifiedName traditionalChineseLocale = 
			QualifiedName.make(
				CAL_Locale.MODULE_NAME, 
				"traditionalChineseLocale");

		/**
		 * The locale "en_GB".
		 * @return (CAL type: <code>Cal.Utilities.Locale.Locale</code>) 
		 */
		public static final SourceModel.Expr ukEnglishLocale() {
			return SourceModel.Expr.Var.make(Functions.ukEnglishLocale);
		}

		/**
		 * Name binding for function: ukEnglishLocale.
		 * @see #ukEnglishLocale()
		 */
		public static final QualifiedName ukEnglishLocale = 
			QualifiedName.make(CAL_Locale.MODULE_NAME, "ukEnglishLocale");

		/**
		 * The locale "en_US".
		 * @return (CAL type: <code>Cal.Utilities.Locale.Locale</code>) 
		 */
		public static final SourceModel.Expr usEnglishLocale() {
			return SourceModel.Expr.Var.make(Functions.usEnglishLocale);
		}

		/**
		 * Name binding for function: usEnglishLocale.
		 * @see #usEnglishLocale()
		 */
		public static final QualifiedName usEnglishLocale = 
			QualifiedName.make(CAL_Locale.MODULE_NAME, "usEnglishLocale");

		/**
		 * Returns the variant for the locale, which can be an empty string if no variant is specified
		 * by the locale.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.Locale.displayVariantInLocale, Cal.Utilities.Locale.displayVariantInCurrentLocale
		 * </dl>
		 * 
		 * @param locale (CAL type: <code>Cal.Utilities.Locale.Locale</code>)
		 *          the locale to query.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the variant for the locale, which can be an empty string.
		 */
		public static final SourceModel.Expr variant(SourceModel.Expr locale) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.variant), locale});
		}

		/**
		 * Name binding for function: variant.
		 * @see #variant(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName variant = 
			QualifiedName.make(CAL_Locale.MODULE_NAME, "variant");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = -279582786;

}
