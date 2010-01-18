/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_Resource_internal.java)
 * was generated from CAL module: Cal.Core.Resource.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Core.Resource module from Java code.
 *  
 * Creation date: Fri Mar 16 13:11:58 PST 2007
 * --!>
 *  
 */

package org.openquark.cal.internal.module.Cal.Core;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * Provides access to user resources in the CAL environment.
 * @author Joseph Wong
 */
public final class CAL_Resource_internal {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Core.Resource");

	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Core.Resource module.
	 */
	public static final class TypeConstructors {
		/**
		 * A foreign type for Java's <code>byte[]</code> array tupe.
		 */
		public static final QualifiedName JByteArray = 
			QualifiedName.make(CAL_Resource_internal.MODULE_NAME, "JByteArray");

		/**
		 * A foreign type representing the interface through which user resources can be accessed.
		 */
		public static final QualifiedName JResourceAccess = 
			QualifiedName.make(
				CAL_Resource_internal.MODULE_NAME, 
				"JResourceAccess");

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Core.Resource module.
	 */
	public static final class Functions {
		/**
		 * Returns the interface for resource access from an execution context.
		 * @param executionContext (CAL type: <code>Cal.Core.Prelude.ExecutionContext</code>)
		 *          the execution context.
		 * @return (CAL type: <code>Cal.Core.Resource.JResourceAccess</code>) 
		 *          the interface for resource access.
		 */
		public static final SourceModel.Expr getResourceAccess(SourceModel.Expr executionContext) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getResourceAccess), executionContext});
		}

		/**
		 * Name binding for function: getResourceAccess.
		 * @see #getResourceAccess(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getResourceAccess = 
			QualifiedName.make(
				CAL_Resource_internal.MODULE_NAME, 
				"getResourceAccess");

		/**
		 * Returns an <code>Cal.Core.Resource.InputStream</code> for the named user resource in the specified locale. Note that this
		 * method does not implement any locale-fallback mechanism - it is up to the caller to do so.
		 * @param resourceAccess (CAL type: <code>Cal.Core.Resource.JResourceAccess</code>)
		 *          the interface for resource access.
		 * @param moduleName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the name of the module associated with the user resource.
		 * @param name (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the name of the resource, not including any file extensions. Cannot contain the character '_'.
		 * @param extension (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the file extension for the user resource.
		 * @param locale (CAL type: <code>Cal.Utilities.Locale.Locale</code>)
		 *          the locale for which the resource is to be fetched.
		 * @return (CAL type: <code>Cal.Core.Resource.InputStream</code>) 
		 *          an InputStream for the user resource, or null if the resource cannot be found.
		 */
		public static final SourceModel.Expr getUserResource(SourceModel.Expr resourceAccess, SourceModel.Expr moduleName, SourceModel.Expr name, SourceModel.Expr extension, SourceModel.Expr locale) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getUserResource), resourceAccess, moduleName, name, extension, locale});
		}

		/**
		 * @see #getUserResource(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param resourceAccess
		 * @param moduleName
		 * @param name
		 * @param extension
		 * @param locale
		 * @return the SourceModel.Expr representing an application of getUserResource
		 */
		public static final SourceModel.Expr getUserResource(SourceModel.Expr resourceAccess, java.lang.String moduleName, java.lang.String name, java.lang.String extension, SourceModel.Expr locale) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getUserResource), resourceAccess, SourceModel.Expr.makeStringValue(moduleName), SourceModel.Expr.makeStringValue(name), SourceModel.Expr.makeStringValue(extension), locale});
		}

		/**
		 * Name binding for function: getUserResource.
		 * @see #getUserResource(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getUserResource = 
			QualifiedName.make(
				CAL_Resource_internal.MODULE_NAME, 
				"getUserResource");

		/**
		 * 
		 * @param array (CAL type: <code>Cal.Core.Resource.JByteArray</code>)
		 *          the byte array to test.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> iff the argument is null.
		 */
		public static final SourceModel.Expr isNullByteArray(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isNullByteArray), array});
		}

		/**
		 * Name binding for function: isNullByteArray.
		 * @see #isNullByteArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isNullByteArray = 
			QualifiedName.make(
				CAL_Resource_internal.MODULE_NAME, 
				"isNullByteArray");

		/**
		 * 
		 * @param is (CAL type: <code>Cal.Core.Resource.InputStream</code>)
		 *          the <code>Cal.Core.Resource.InputStream</code> to test.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> iff the argument is null.
		 */
		public static final SourceModel.Expr isNullInputStream(SourceModel.Expr is) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isNullInputStream), is});
		}

		/**
		 * Name binding for function: isNullInputStream.
		 * @see #isNullInputStream(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isNullInputStream = 
			QualifiedName.make(
				CAL_Resource_internal.MODULE_NAME, 
				"isNullInputStream");

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
				CAL_Resource_internal.MODULE_NAME, 
				"isNullString");

		/**
		 * Reads the specified <code>Cal.Core.Resource.InputStream</code> into a new byte array, and close the stream afterwards.
		 * If any IOExceptions are thrown during the reading process, a null byte array will be returned.
		 * @param inputStream (CAL type: <code>Cal.Core.Resource.InputStream</code>)
		 *          the <code>InputStream</code> to be read.
		 * @return (CAL type: <code>Cal.Core.Resource.JByteArray</code>) 
		 *          an array of the bytes read, or null if there was a problem reading the stream.
		 */
		public static final SourceModel.Expr readIntoByteArray(SourceModel.Expr inputStream) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.readIntoByteArray), inputStream});
		}

		/**
		 * Name binding for function: readIntoByteArray.
		 * @see #readIntoByteArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName readIntoByteArray = 
			QualifiedName.make(
				CAL_Resource_internal.MODULE_NAME, 
				"readIntoByteArray");

		/**
		 * Reads the specified <code>Cal.Core.Resource.InputStream</code> into a string using the given character set.
		 * If any IOExceptions are thrown during the reading process, a null byte array will be returned.
		 * @param charsetName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the character set to be used for translating bytes into characters.
		 * @param is (CAL type: <code>Cal.Core.Resource.InputStream</code>)
		 *          the <code>InputStream</code> to be read.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          a string for the text read from the stream, or null if there was a problem reading the stream.
		 */
		public static final SourceModel.Expr readIntoString(SourceModel.Expr charsetName, SourceModel.Expr is) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.readIntoString), charsetName, is});
		}

		/**
		 * @see #readIntoString(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param charsetName
		 * @param is
		 * @return the SourceModel.Expr representing an application of readIntoString
		 */
		public static final SourceModel.Expr readIntoString(java.lang.String charsetName, SourceModel.Expr is) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.readIntoString), SourceModel.Expr.makeStringValue(charsetName), is});
		}

		/**
		 * Name binding for function: readIntoString.
		 * @see #readIntoString(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName readIntoString = 
			QualifiedName.make(
				CAL_Resource_internal.MODULE_NAME, 
				"readIntoString");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = 1954887263;

}
