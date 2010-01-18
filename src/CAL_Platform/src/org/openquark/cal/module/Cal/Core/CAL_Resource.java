/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_Resource.java)
 * was generated from CAL module: Cal.Core.Resource.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Core.Resource module from Java code.
 *  
 * Creation date: Fri Mar 16 13:11:57 PST 2007
 * --!>
 *  
 */

package org.openquark.cal.module.Cal.Core;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * Provides access to user resources in the CAL environment.
 * @author Joseph Wong
 */
public final class CAL_Resource {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Core.Resource");

	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Core.Resource module.
	 */
	public static final class TypeConstructors {
		/**
		 * A foreign type representing the Java type <code>java.io.InputStream</code>.
		 */
		public static final QualifiedName InputStream = 
			QualifiedName.make(CAL_Resource.MODULE_NAME, "InputStream");

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Core.Resource module.
	 */
	public static final class Functions {
		/**
		 * Returns a binary user resource as a byte array.
		 * @param moduleName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the name of the module associated with the user resource.
		 * @param name (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the name of the resource, not including any file extensions. Cannot contain the character '_'.
		 * @param extension (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the file extension for the user resource.
		 * @param locale (CAL type: <code>Cal.Utilities.Locale.Locale</code>)
		 *          the locale for which the resource is to be fetched.
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe (Cal.Collections.Array.Array Cal.Core.Prelude.Byte)</code>) 
		 *          <code>Cal.Core.Prelude.Just byteArray</code>, if the resource could be read, or <code>Cal.Core.Prelude.Nothing</code> if the resource (or its fallbacks)
		 * cannot be read.
		 */
		public static final SourceModel.Expr getBinaryResource(SourceModel.Expr moduleName, SourceModel.Expr name, SourceModel.Expr extension, SourceModel.Expr locale) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getBinaryResource), moduleName, name, extension, locale});
		}

		/**
		 * @see #getBinaryResource(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param moduleName
		 * @param name
		 * @param extension
		 * @param locale
		 * @return the SourceModel.Expr representing an application of getBinaryResource
		 */
		public static final SourceModel.Expr getBinaryResource(java.lang.String moduleName, java.lang.String name, java.lang.String extension, SourceModel.Expr locale) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getBinaryResource), SourceModel.Expr.makeStringValue(moduleName), SourceModel.Expr.makeStringValue(name), SourceModel.Expr.makeStringValue(extension), locale});
		}

		/**
		 * Name binding for function: getBinaryResource.
		 * @see #getBinaryResource(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getBinaryResource = 
			QualifiedName.make(CAL_Resource.MODULE_NAME, "getBinaryResource");

		/**
		 * Returns a binary user resource as an <code>Cal.Core.Resource.InputStream</code>.
		 * @param moduleName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the name of the module associated with the user resource.
		 * @param name (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the name of the resource, not including any file extensions. Cannot contain the character '_'.
		 * @param extension (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the file extension for the user resource.
		 * @param locale (CAL type: <code>Cal.Utilities.Locale.Locale</code>)
		 *          the locale for which the resource is to be fetched.
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Core.Resource.InputStream</code>) 
		 *          <code>Cal.Core.Prelude.Just inputStream</code>, if the resource could be read, or <code>Cal.Core.Prelude.Nothing</code> if the resource (or its fallbacks)
		 * cannot be read.
		 */
		public static final SourceModel.Expr getResourceInputStream(SourceModel.Expr moduleName, SourceModel.Expr name, SourceModel.Expr extension, SourceModel.Expr locale) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getResourceInputStream), moduleName, name, extension, locale});
		}

		/**
		 * @see #getResourceInputStream(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param moduleName
		 * @param name
		 * @param extension
		 * @param locale
		 * @return the SourceModel.Expr representing an application of getResourceInputStream
		 */
		public static final SourceModel.Expr getResourceInputStream(java.lang.String moduleName, java.lang.String name, java.lang.String extension, SourceModel.Expr locale) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getResourceInputStream), SourceModel.Expr.makeStringValue(moduleName), SourceModel.Expr.makeStringValue(name), SourceModel.Expr.makeStringValue(extension), locale});
		}

		/**
		 * Name binding for function: getResourceInputStream.
		 * @see #getResourceInputStream(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getResourceInputStream = 
			QualifiedName.make(
				CAL_Resource.MODULE_NAME, 
				"getResourceInputStream");

		/**
		 * Returns an ordered list of <code>(Locale, InputStream)</code> pairs which represents a user resource and its associated fallbacks.
		 * The head of the list corresponds to the most-locale-specific resource available (with the actual locale of that particular
		 * resource made available in the pair), and the remaining entries correspond to available fallbacks.
		 * <p>
		 * The streams returned in the list should all be non-null.
		 * 
		 * @param moduleName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the name of the module associated with the user resource.
		 * @param name (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the name of the resource, not including any file extensions. Cannot contain the character '_'.
		 * @param extension (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the file extension for the user resource.
		 * @param locale (CAL type: <code>Cal.Utilities.Locale.Locale</code>)
		 *          the locale for which the resource is to be fetched.
		 * @return (CAL type: <code>[(Cal.Utilities.Locale.Locale, Cal.Core.Resource.InputStream)]</code>) 
		 *          an ordered list of <code>(Locale, InputStream)</code> pairs which represents a user resource and its associated fallbacks.
		 */
		public static final SourceModel.Expr getResourceInputStreamFallbackList(SourceModel.Expr moduleName, SourceModel.Expr name, SourceModel.Expr extension, SourceModel.Expr locale) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getResourceInputStreamFallbackList), moduleName, name, extension, locale});
		}

		/**
		 * @see #getResourceInputStreamFallbackList(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param moduleName
		 * @param name
		 * @param extension
		 * @param locale
		 * @return the SourceModel.Expr representing an application of getResourceInputStreamFallbackList
		 */
		public static final SourceModel.Expr getResourceInputStreamFallbackList(java.lang.String moduleName, java.lang.String name, java.lang.String extension, SourceModel.Expr locale) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getResourceInputStreamFallbackList), SourceModel.Expr.makeStringValue(moduleName), SourceModel.Expr.makeStringValue(name), SourceModel.Expr.makeStringValue(extension), locale});
		}

		/**
		 * Name binding for function: getResourceInputStreamFallbackList.
		 * @see #getResourceInputStreamFallbackList(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getResourceInputStreamFallbackList = 
			QualifiedName.make(
				CAL_Resource.MODULE_NAME, 
				"getResourceInputStreamFallbackList");

		/**
		 * Returns a text user resource as a string, using the ISO-8859-1 (Latin 1) character set.
		 * @param moduleName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the name of the module associated with the user resource.
		 * @param name (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the name of the resource, not including any file extensions. Cannot contain the character '_'.
		 * @param extension (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the file extension for the user resource.
		 * @param locale (CAL type: <code>Cal.Utilities.Locale.Locale</code>)
		 *          the locale for which the resource is to be fetched.
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Core.Prelude.String</code>) 
		 *          <code>Cal.Core.Prelude.Just text</code>, if the resource could be read, or <code>Cal.Core.Prelude.Nothing</code> if the resource (or its fallbacks)
		 * cannot be read.
		 */
		public static final SourceModel.Expr getTextResource(SourceModel.Expr moduleName, SourceModel.Expr name, SourceModel.Expr extension, SourceModel.Expr locale) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getTextResource), moduleName, name, extension, locale});
		}

		/**
		 * @see #getTextResource(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param moduleName
		 * @param name
		 * @param extension
		 * @param locale
		 * @return the SourceModel.Expr representing an application of getTextResource
		 */
		public static final SourceModel.Expr getTextResource(java.lang.String moduleName, java.lang.String name, java.lang.String extension, SourceModel.Expr locale) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getTextResource), SourceModel.Expr.makeStringValue(moduleName), SourceModel.Expr.makeStringValue(name), SourceModel.Expr.makeStringValue(extension), locale});
		}

		/**
		 * Name binding for function: getTextResource.
		 * @see #getTextResource(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getTextResource = 
			QualifiedName.make(CAL_Resource.MODULE_NAME, "getTextResource");

		/**
		 * Returns a text user resource as a string, using the specified character set.
		 * @param moduleName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the name of the module associated with the user resource.
		 * @param name (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the name of the resource, not including any file extensions. Cannot contain the character '_'.
		 * @param extension (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the file extension for the user resource.
		 * @param locale (CAL type: <code>Cal.Utilities.Locale.Locale</code>)
		 *          the locale for which the resource is to be fetched.
		 * @param charset (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Core.Prelude.String</code>) 
		 *          <code>Cal.Core.Prelude.Just text</code>, if the resource could be read, or <code>Cal.Core.Prelude.Nothing</code> if the resource (or its fallbacks)
		 * cannot be read.
		 */
		public static final SourceModel.Expr getTextResourceInCharset(SourceModel.Expr moduleName, SourceModel.Expr name, SourceModel.Expr extension, SourceModel.Expr locale, SourceModel.Expr charset) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getTextResourceInCharset), moduleName, name, extension, locale, charset});
		}

		/**
		 * @see #getTextResourceInCharset(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param moduleName
		 * @param name
		 * @param extension
		 * @param locale
		 * @param charset
		 * @return the SourceModel.Expr representing an application of getTextResourceInCharset
		 */
		public static final SourceModel.Expr getTextResourceInCharset(java.lang.String moduleName, java.lang.String name, java.lang.String extension, SourceModel.Expr locale, java.lang.String charset) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getTextResourceInCharset), SourceModel.Expr.makeStringValue(moduleName), SourceModel.Expr.makeStringValue(name), SourceModel.Expr.makeStringValue(extension), locale, SourceModel.Expr.makeStringValue(charset)});
		}

		/**
		 * Name binding for function: getTextResourceInCharset.
		 * @see #getTextResourceInCharset(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getTextResourceInCharset = 
			QualifiedName.make(
				CAL_Resource.MODULE_NAME, 
				"getTextResourceInCharset");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = -1827820154;

}
