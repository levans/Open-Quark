/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_StringProperties_internal.java)
 * was generated from CAL module: Cal.Utilities.StringProperties.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Utilities.StringProperties module from Java code.
 *  
 * Creation date: Fri Mar 16 13:11:58 PST 2007
 * --!>
 *  
 */

package org.openquark.cal.internal.module.Cal.Utilities;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * This module defines the types <code>Cal.Utilities.StringProperties.StringProperties</code>
 * and <code>Cal.Utilities.StringProperties.StringResourceBundle</code> which are useful for working with string resource files.
 * @author Joseph Wong
 */
public final class CAL_StringProperties_internal {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Utilities.StringProperties");

	/**
	 * This inner class (DataConstructors) contains constants
	 * and methods related to binding to CAL DataConstructors in the Cal.Utilities.StringProperties module.
	 */
	public static final class DataConstructors {
		/*
		 * DataConstructors for the Cal.Utilities.StringProperties.StringResourceBundle data type.
		 */

		/**
		 * Represents a <code>Cal.Utilities.StringProperties.StringProperties</code> fetched as a user resource,
		 * with identification information for the resource: module name, name, file extension,
		 * and the requested and actual locales of the resource.
		 * @param moduleName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the name of the module associated with the user resource.
		 * @param name (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the name of the resource, not including any file extensions. Cannot contain the character '_'.
		 * @param extension (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the file extension for the user resource.
		 * @param requestedLocale (CAL type: <code>Cal.Utilities.Locale.Locale</code>)
		 *          the locale for which the resource was to be fetched.
		 * @param actualLocale (CAL type: <code>Cal.Utilities.Locale.Locale</code>)
		 *          the actual locale for the resource fetched.
		 * @param strings (CAL type: <code>Cal.Utilities.StringProperties.StringProperties</code>)
		 *          the <code>Cal.Utilities.StringProperties.StringProperties</code>.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr StringResourceBundle(SourceModel.Expr moduleName, SourceModel.Expr name, SourceModel.Expr extension, SourceModel.Expr requestedLocale, SourceModel.Expr actualLocale, SourceModel.Expr strings) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.StringResourceBundle), moduleName, name, extension, requestedLocale, actualLocale, strings});
		}

		/**
		 * @see #StringResourceBundle(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param moduleName
		 * @param name
		 * @param extension
		 * @param requestedLocale
		 * @param actualLocale
		 * @param strings
		 * @return org.openquark.cal.compiler.SourceModel.Expr
		 */
		public static final SourceModel.Expr StringResourceBundle(java.lang.String moduleName, java.lang.String name, java.lang.String extension, SourceModel.Expr requestedLocale, SourceModel.Expr actualLocale, SourceModel.Expr strings) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.StringResourceBundle), SourceModel.Expr.makeStringValue(moduleName), SourceModel.Expr.makeStringValue(name), SourceModel.Expr.makeStringValue(extension), requestedLocale, actualLocale, strings});
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.StringProperties.StringResourceBundle.
		 * @see #StringResourceBundle(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName StringResourceBundle = 
			QualifiedName.make(
				CAL_StringProperties_internal.MODULE_NAME, 
				"StringResourceBundle");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.StringProperties.StringResourceBundle.
		 * @see #StringResourceBundle(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int StringResourceBundle_ordinal = 0;

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Utilities.StringProperties module.
	 */
	public static final class Functions {
		/**
		 * Searches for the property with the specified key in the <code>Cal.Utilities.StringProperties.StringProperties</code> instance.
		 * If the key is not found in there, its defaults, and its defaults' defaults, recursively, are then checked.
		 * If the property is not found, the Java <code>null</code> value is returned.
		 * @param stringProperties (CAL type: <code>Cal.Utilities.StringProperties.StringProperties</code>)
		 *          the <code>Cal.Utilities.StringProperties.StringProperties</code> instance.
		 * @param key (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the property key.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the value of the property, or the Java <code>null</code> value if there is no property with that key.
		 */
		public static final SourceModel.Expr getProperty(SourceModel.Expr stringProperties, SourceModel.Expr key) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getProperty), stringProperties, key});
		}

		/**
		 * @see #getProperty(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param stringProperties
		 * @param key
		 * @return the SourceModel.Expr representing an application of getProperty
		 */
		public static final SourceModel.Expr getProperty(SourceModel.Expr stringProperties, java.lang.String key) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getProperty), stringProperties, SourceModel.Expr.makeStringValue(key)});
		}

		/**
		 * Name binding for function: getProperty.
		 * @see #getProperty(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getProperty = 
			QualifiedName.make(
				CAL_StringProperties_internal.MODULE_NAME, 
				"getProperty");

		/**
		 * Returns a list of the keys defined in the given <code>Cal.Utilities.StringProperties.StringProperties</code> instance (and
		 * its chain of defaults instances).
		 * @param stringProperties (CAL type: <code>Cal.Utilities.StringProperties.StringProperties</code>)
		 *          the <code>Cal.Utilities.StringProperties.StringProperties</code> instance.
		 * @return (CAL type: <code>Cal.Core.Prelude.JList</code>) 
		 *          a list of all the keys in the instance, including the keys in the default property list.
		 */
		public static final SourceModel.Expr getPropertyNames(SourceModel.Expr stringProperties) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getPropertyNames), stringProperties});
		}

		/**
		 * Name binding for function: getPropertyNames.
		 * @see #getPropertyNames(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getPropertyNames = 
			QualifiedName.make(
				CAL_StringProperties_internal.MODULE_NAME, 
				"getPropertyNames");

		/**
		 * Searches for the property with the specified key in the <code>Cal.Utilities.StringProperties.StringProperties</code> instance.
		 * If the key is not found in there, its defaults, and its defaults' defaults, recursively, are then checked.
		 * If the proprety is not found, the default value argument is returned.
		 * @param stringProperties (CAL type: <code>Cal.Utilities.StringProperties.StringProperties</code>)
		 *          the <code>Cal.Utilities.StringProperties.StringProperties</code> instance.
		 * @param key (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the property key.
		 * @param defaultValue (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the default value to use.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the value of the property, or <code>defaultValue</code> if there is no property with that key.
		 */
		public static final SourceModel.Expr getPropertyWithDefault(SourceModel.Expr stringProperties, SourceModel.Expr key, SourceModel.Expr defaultValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getPropertyWithDefault), stringProperties, key, defaultValue});
		}

		/**
		 * @see #getPropertyWithDefault(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param stringProperties
		 * @param key
		 * @param defaultValue
		 * @return the SourceModel.Expr representing an application of getPropertyWithDefault
		 */
		public static final SourceModel.Expr getPropertyWithDefault(SourceModel.Expr stringProperties, java.lang.String key, java.lang.String defaultValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getPropertyWithDefault), stringProperties, SourceModel.Expr.makeStringValue(key), SourceModel.Expr.makeStringValue(defaultValue)});
		}

		/**
		 * Name binding for function: getPropertyWithDefault.
		 * @see #getPropertyWithDefault(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getPropertyWithDefault = 
			QualifiedName.make(
				CAL_StringProperties_internal.MODULE_NAME, 
				"getPropertyWithDefault");

		/**
		 * 
		 * @param properties (CAL type: <code>Cal.Utilities.StringProperties.StringProperties</code>)
		 *          the <code>Cal.Utilities.StringProperties.StringProperties</code> to test.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> iff the argument is null.
		 */
		public static final SourceModel.Expr isNullProperties(SourceModel.Expr properties) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isNullProperties), properties});
		}

		/**
		 * Name binding for function: isNullProperties.
		 * @see #isNullProperties(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isNullProperties = 
			QualifiedName.make(
				CAL_StringProperties_internal.MODULE_NAME, 
				"isNullProperties");

		/**
		 * 
		 * @param string (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the string to test.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> iff the argument is null.
		 */
		public static final SourceModel.Expr isNullString(SourceModel.Expr string) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isNullString), string});
		}

		/**
		 * @see #isNullString(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param string
		 * @return the SourceModel.Expr representing an application of isNullString
		 */
		public static final SourceModel.Expr isNullString(java.lang.String string) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isNullString), SourceModel.Expr.makeStringValue(string)});
		}

		/**
		 * Name binding for function: isNullString.
		 * @see #isNullString(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isNullString = 
			QualifiedName.make(
				CAL_StringProperties_internal.MODULE_NAME, 
				"isNullString");

		/**
		 * The null value, which can be used validly as the <em>defaults</em> of a <code>Cal.Utilities.StringProperties.StringProperties</code>.
		 * @return (CAL type: <code>Cal.Utilities.StringProperties.StringProperties</code>) 
		 */
		public static final SourceModel.Expr nullProperties() {
			return SourceModel.Expr.Var.make(Functions.nullProperties);
		}

		/**
		 * Name binding for function: nullProperties.
		 * @see #nullProperties()
		 */
		public static final QualifiedName nullProperties = 
			QualifiedName.make(
				CAL_StringProperties_internal.MODULE_NAME, 
				"nullProperties");

		/**
		 * Helper binding method for function: showStringProperties. 
		 * @param stringProperties
		 * @return the SourceModule.expr representing an application of showStringProperties
		 */
		public static final SourceModel.Expr showStringProperties(SourceModel.Expr stringProperties) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showStringProperties), stringProperties});
		}

		/**
		 * Name binding for function: showStringProperties.
		 * @see #showStringProperties(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showStringProperties = 
			QualifiedName.make(
				CAL_StringProperties_internal.MODULE_NAME, 
				"showStringProperties");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = -1050045734;

}
