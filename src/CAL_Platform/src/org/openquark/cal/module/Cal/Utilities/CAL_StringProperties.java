/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_StringProperties.java)
 * was generated from CAL module: Cal.Utilities.StringProperties.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Utilities.StringProperties module from Java code.
 *  
 * Creation date: Fri Mar 16 13:11:57 PST 2007
 * --!>
 *  
 */

package org.openquark.cal.module.Cal.Utilities;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * This module defines the types <code>Cal.Utilities.StringProperties.StringProperties</code>
 * and <code>Cal.Utilities.StringProperties.StringResourceBundle</code> which are useful for working with string resource files.
 * @author Joseph Wong
 */
public final class CAL_StringProperties {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Utilities.StringProperties");

	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Utilities.StringProperties module.
	 */
	public static final class TypeConstructors {
		/**
		 * Represents a set of string-valued properties that can be loaded from a persisted format.
		 */
		public static final QualifiedName StringProperties = 
			QualifiedName.make(
				CAL_StringProperties.MODULE_NAME, 
				"StringProperties");

		/**
		 * Represents a <code>Cal.Utilities.StringProperties.StringProperties</code> fetched as a user resource.
		 */
		public static final QualifiedName StringResourceBundle = 
			QualifiedName.make(
				CAL_StringProperties.MODULE_NAME, 
				"StringResourceBundle");

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Utilities.StringProperties module.
	 */
	public static final class Functions {
		/**
		 * Returns the actual locale for the resource fetched.
		 * @param bundle (CAL type: <code>Cal.Utilities.StringProperties.StringResourceBundle</code>)
		 *          the string resource bundle.
		 * @return (CAL type: <code>Cal.Utilities.Locale.Locale</code>) 
		 *          the actual locale for the resource fetched.
		 */
		public static final SourceModel.Expr bundleActualLocale(SourceModel.Expr bundle) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.bundleActualLocale), bundle});
		}

		/**
		 * Name binding for function: bundleActualLocale.
		 * @see #bundleActualLocale(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName bundleActualLocale = 
			QualifiedName.make(
				CAL_StringProperties.MODULE_NAME, 
				"bundleActualLocale");

		/**
		 * Returns the file extension of the user resource associated with the bundle.
		 * @param bundle (CAL type: <code>Cal.Utilities.StringProperties.StringResourceBundle</code>)
		 *          the string resource bundle.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the file extension of the user resource associated with the bundle.
		 */
		public static final SourceModel.Expr bundleExtension(SourceModel.Expr bundle) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.bundleExtension), bundle});
		}

		/**
		 * Name binding for function: bundleExtension.
		 * @see #bundleExtension(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName bundleExtension = 
			QualifiedName.make(
				CAL_StringProperties.MODULE_NAME, 
				"bundleExtension");

		/**
		 * Returns the name of the module associated with the bundle.
		 * @param bundle (CAL type: <code>Cal.Utilities.StringProperties.StringResourceBundle</code>)
		 *          the string resource bundle.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the name of the module associated with the bundle.
		 */
		public static final SourceModel.Expr bundleModuleName(SourceModel.Expr bundle) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.bundleModuleName), bundle});
		}

		/**
		 * Name binding for function: bundleModuleName.
		 * @see #bundleModuleName(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName bundleModuleName = 
			QualifiedName.make(
				CAL_StringProperties.MODULE_NAME, 
				"bundleModuleName");

		/**
		 * Returns the name of the user resource associated with the bundle.
		 * @param bundle (CAL type: <code>Cal.Utilities.StringProperties.StringResourceBundle</code>)
		 *          the string resource bundle.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the name of the user resource associated with the bundle.
		 */
		public static final SourceModel.Expr bundleName(SourceModel.Expr bundle) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.bundleName), bundle});
		}

		/**
		 * Name binding for function: bundleName.
		 * @see #bundleName(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName bundleName = 
			QualifiedName.make(CAL_StringProperties.MODULE_NAME, "bundleName");

		/**
		 * Returns the locale for which the resource was to be fetched.
		 * @param bundle (CAL type: <code>Cal.Utilities.StringProperties.StringResourceBundle</code>)
		 *          the string resource bundle.
		 * @return (CAL type: <code>Cal.Utilities.Locale.Locale</code>) 
		 *          the locale for which the resource was to be fetched.
		 */
		public static final SourceModel.Expr bundleRequestedLocale(SourceModel.Expr bundle) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.bundleRequestedLocale), bundle});
		}

		/**
		 * Name binding for function: bundleRequestedLocale.
		 * @see #bundleRequestedLocale(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName bundleRequestedLocale = 
			QualifiedName.make(
				CAL_StringProperties.MODULE_NAME, 
				"bundleRequestedLocale");

		/**
		 * Returns the <code>Cal.Utilities.StringProperties.StringProperties</code> associated with the bundle.
		 * @param bundle (CAL type: <code>Cal.Utilities.StringProperties.StringResourceBundle</code>)
		 *          the string resource bundle.
		 * @return (CAL type: <code>Cal.Utilities.StringProperties.StringProperties</code>) 
		 *          the <code>Cal.Utilities.StringProperties.StringProperties</code> associated with the bundle.
		 */
		public static final SourceModel.Expr bundleStrings(SourceModel.Expr bundle) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.bundleStrings), bundle});
		}

		/**
		 * Name binding for function: bundleStrings.
		 * @see #bundleStrings(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName bundleStrings = 
			QualifiedName.make(
				CAL_StringProperties.MODULE_NAME, 
				"bundleStrings");

		/**
		 * An empty <code>Cal.Utilities.StringProperties.StringProperties</code>.
		 * @return (CAL type: <code>Cal.Utilities.StringProperties.StringProperties</code>) 
		 */
		public static final SourceModel.Expr emptyStringProperties() {
			return SourceModel.Expr.Var.make(Functions.emptyStringProperties);
		}

		/**
		 * Name binding for function: emptyStringProperties.
		 * @see #emptyStringProperties()
		 */
		public static final QualifiedName emptyStringProperties = 
			QualifiedName.make(
				CAL_StringProperties.MODULE_NAME, 
				"emptyStringProperties");

		/**
		 * Returns a list of all the keys defined in the given <code>Cal.Utilities.StringProperties.StringProperties</code> (and
		 * its chain of defaults instances).
		 * @param stringProperties (CAL type: <code>Cal.Utilities.StringProperties.StringProperties</code>)
		 *          the <code>Cal.Utilities.StringProperties.StringProperties</code> instance.
		 * @return (CAL type: <code>[Cal.Core.Prelude.String]</code>) 
		 *          a list of all the keys in the instance, including the keys in the default property list.
		 */
		public static final SourceModel.Expr keys(SourceModel.Expr stringProperties) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.keys), stringProperties});
		}

		/**
		 * Name binding for function: keys.
		 * @see #keys(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName keys = 
			QualifiedName.make(CAL_StringProperties.MODULE_NAME, "keys");

		/**
		 * Looks up the named property in the given <code>Cal.Utilities.StringProperties.StringProperties</code>.
		 * @param key (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the name of the property.
		 * @param stringProperties (CAL type: <code>Cal.Utilities.StringProperties.StringProperties</code>)
		 *          the <code>Cal.Utilities.StringProperties.StringProperties</code> instance.
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Core.Prelude.String</code>) 
		 *          <code>Cal.Core.Prelude.Just</code> the value of the property, or <code>Cal.Core.Prelude.Nothing</code> if there is no property with that key.
		 */
		public static final SourceModel.Expr lookup(SourceModel.Expr key, SourceModel.Expr stringProperties) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lookup), key, stringProperties});
		}

		/**
		 * @see #lookup(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param key
		 * @param stringProperties
		 * @return the SourceModel.Expr representing an application of lookup
		 */
		public static final SourceModel.Expr lookup(java.lang.String key, SourceModel.Expr stringProperties) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lookup), SourceModel.Expr.makeStringValue(key), stringProperties});
		}

		/**
		 * Name binding for function: lookup.
		 * @see #lookup(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName lookup = 
			QualifiedName.make(CAL_StringProperties.MODULE_NAME, "lookup");

		/**
		 * Looks up the named property in the given <code>Cal.Utilities.StringProperties.StringProperties</code>, with the provided default.
		 * @param key (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the name of the property.
		 * @param stringProperties (CAL type: <code>Cal.Utilities.StringProperties.StringProperties</code>)
		 *          the <code>Cal.Utilities.StringProperties.StringProperties</code> instance.
		 * @param defaultValue (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the default value to use.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the value of the property, or <code>defaultValue</code> if there is no property with that key.
		 */
		public static final SourceModel.Expr lookupWithDefault(SourceModel.Expr key, SourceModel.Expr stringProperties, SourceModel.Expr defaultValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lookupWithDefault), key, stringProperties, defaultValue});
		}

		/**
		 * @see #lookupWithDefault(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param key
		 * @param stringProperties
		 * @param defaultValue
		 * @return the SourceModel.Expr representing an application of lookupWithDefault
		 */
		public static final SourceModel.Expr lookupWithDefault(java.lang.String key, SourceModel.Expr stringProperties, java.lang.String defaultValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lookupWithDefault), SourceModel.Expr.makeStringValue(key), stringProperties, SourceModel.Expr.makeStringValue(defaultValue)});
		}

		/**
		 * Name binding for function: lookupWithDefault.
		 * @see #lookupWithDefault(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName lookupWithDefault = 
			QualifiedName.make(
				CAL_StringProperties.MODULE_NAME, 
				"lookupWithDefault");

		/**
		 * Constructs a <code>Cal.Utilities.StringProperties.StringProperties</code> by reading in the persisted format from an input stream.
		 * @param inputStream (CAL type: <code>Cal.Core.Resource.InputStream</code>)
		 *          the stream to be read.
		 * @return (CAL type: <code>Cal.Utilities.StringProperties.StringProperties</code>) 
		 *          a <code>Cal.Utilities.StringProperties.StringProperties</code> with the key-value pairs read in from the stream.
		 */
		public static final SourceModel.Expr makeStringProperties(SourceModel.Expr inputStream) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeStringProperties), inputStream});
		}

		/**
		 * Name binding for function: makeStringProperties.
		 * @see #makeStringProperties(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeStringProperties = 
			QualifiedName.make(
				CAL_StringProperties.MODULE_NAME, 
				"makeStringProperties");

		/**
		 * Constructs a <code>Cal.Utilities.StringProperties.StringProperties</code> by reading in the persisted format from an input stream, and
		 * with the given defaults.
		 * @param inputStream (CAL type: <code>Cal.Core.Resource.InputStream</code>)
		 *          the stream to be read.
		 * @param defaults (CAL type: <code>Cal.Utilities.StringProperties.StringProperties</code>)
		 *          the defaults to fall back on.
		 * @return (CAL type: <code>Cal.Utilities.StringProperties.StringProperties</code>) 
		 *          a <code>Cal.Utilities.StringProperties.StringProperties</code> with the key-value pairs read in from the stream.
		 */
		public static final SourceModel.Expr makeStringPropertiesWithDefaults(SourceModel.Expr inputStream, SourceModel.Expr defaults) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeStringPropertiesWithDefaults), inputStream, defaults});
		}

		/**
		 * Name binding for function: makeStringPropertiesWithDefaults.
		 * @see #makeStringPropertiesWithDefaults(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeStringPropertiesWithDefaults = 
			QualifiedName.make(
				CAL_StringProperties.MODULE_NAME, 
				"makeStringPropertiesWithDefaults");

		/**
		 * Constructs a string resource bundle by fetching the user resource with the given module name and resource name,
		 * in the current locale. The file extension defaults to "properties".
		 * @param moduleName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the name of the module associated with the user resource.
		 * @param name (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the name of the resource, not including any file extensions. Cannot contain the character '_'.
		 * @return (CAL type: <code>Cal.Utilities.StringProperties.StringResourceBundle</code>) 
		 */
		public static final SourceModel.Expr makeStringResourceBundle(SourceModel.Expr moduleName, SourceModel.Expr name) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeStringResourceBundle), moduleName, name});
		}

		/**
		 * @see #makeStringResourceBundle(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param moduleName
		 * @param name
		 * @return the SourceModel.Expr representing an application of makeStringResourceBundle
		 */
		public static final SourceModel.Expr makeStringResourceBundle(java.lang.String moduleName, java.lang.String name) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeStringResourceBundle), SourceModel.Expr.makeStringValue(moduleName), SourceModel.Expr.makeStringValue(name)});
		}

		/**
		 * Name binding for function: makeStringResourceBundle.
		 * @see #makeStringResourceBundle(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeStringResourceBundle = 
			QualifiedName.make(
				CAL_StringProperties.MODULE_NAME, 
				"makeStringResourceBundle");

		/**
		 * Constructs a string resource bundle by fetching the user resource with the given module name and resource name, and in the
		 * specific locale. The file extension defaults to "properties".
		 * @param moduleName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the name of the module associated with the user resource.
		 * @param name (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the name of the resource, not including any file extensions. Cannot contain the character '_'.
		 * @param locale (CAL type: <code>Cal.Utilities.Locale.Locale</code>)
		 *          the locale for which the resource is to be fetched.
		 * @return (CAL type: <code>Cal.Utilities.StringProperties.StringResourceBundle</code>) 
		 */
		public static final SourceModel.Expr makeStringResourceBundleInLocale(SourceModel.Expr moduleName, SourceModel.Expr name, SourceModel.Expr locale) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeStringResourceBundleInLocale), moduleName, name, locale});
		}

		/**
		 * @see #makeStringResourceBundleInLocale(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param moduleName
		 * @param name
		 * @param locale
		 * @return the SourceModel.Expr representing an application of makeStringResourceBundleInLocale
		 */
		public static final SourceModel.Expr makeStringResourceBundleInLocale(java.lang.String moduleName, java.lang.String name, SourceModel.Expr locale) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeStringResourceBundleInLocale), SourceModel.Expr.makeStringValue(moduleName), SourceModel.Expr.makeStringValue(name), locale});
		}

		/**
		 * Name binding for function: makeStringResourceBundleInLocale.
		 * @see #makeStringResourceBundleInLocale(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeStringResourceBundleInLocale = 
			QualifiedName.make(
				CAL_StringProperties.MODULE_NAME, 
				"makeStringResourceBundleInLocale");

		/**
		 * Constructs a string resource bundle by fetching the user resource with the given module name and resource name and extension,
		 * and in the specific locale.
		 * @param moduleName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the name of the module associated with the user resource.
		 * @param name (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the name of the resource, not including any file extensions. Cannot contain the character '_'.
		 * @param extension (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the file extension for the user resource.
		 * @param locale (CAL type: <code>Cal.Utilities.Locale.Locale</code>)
		 *          the locale for which the resource is to be fetched.
		 * @return (CAL type: <code>Cal.Utilities.StringProperties.StringResourceBundle</code>) 
		 */
		public static final SourceModel.Expr makeStringResourceBundleWithExtensionInLocale(SourceModel.Expr moduleName, SourceModel.Expr name, SourceModel.Expr extension, SourceModel.Expr locale) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeStringResourceBundleWithExtensionInLocale), moduleName, name, extension, locale});
		}

		/**
		 * @see #makeStringResourceBundleWithExtensionInLocale(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param moduleName
		 * @param name
		 * @param extension
		 * @param locale
		 * @return the SourceModel.Expr representing an application of makeStringResourceBundleWithExtensionInLocale
		 */
		public static final SourceModel.Expr makeStringResourceBundleWithExtensionInLocale(java.lang.String moduleName, java.lang.String name, java.lang.String extension, SourceModel.Expr locale) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeStringResourceBundleWithExtensionInLocale), SourceModel.Expr.makeStringValue(moduleName), SourceModel.Expr.makeStringValue(name), SourceModel.Expr.makeStringValue(extension), locale});
		}

		/**
		 * Name binding for function: makeStringResourceBundleWithExtensionInLocale.
		 * @see #makeStringResourceBundleWithExtensionInLocale(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeStringResourceBundleWithExtensionInLocale = 
			QualifiedName.make(
				CAL_StringProperties.MODULE_NAME, 
				"makeStringResourceBundleWithExtensionInLocale");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = 242845432;

}
