/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_MessageFormat_internal.java)
 * was generated from CAL module: Cal.Utilities.MessageFormat.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Utilities.MessageFormat module from Java code.
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
 * This module defines a set of functions for formatting strings with message patterns. It provides a means
 * for producing concatenated messages in a localizable way.
 * <p>
 * For the exact syntax of the message patterns, please refer to the Java documentation for the
 * <code>java.text.MessageFormat</code> class:
 * <a href='http://java.sun.com/j2se/1.4.2/docs/api/java/text/MessageFormat.html'>http://java.sun.com/j2se/1.4.2/docs/api/java/text/MessageFormat.html</a>
 * 
 * @author Joseph Wong
 */
public final class CAL_MessageFormat_internal {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Utilities.MessageFormat");

	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Utilities.MessageFormat module.
	 */
	public static final class TypeConstructors {
		/**
		 * A foreign type for Java's <code>java.text.MessageFormat</code> type.
		 */
		public static final QualifiedName JMessageFormat = 
			QualifiedName.make(
				CAL_MessageFormat_internal.MODULE_NAME, 
				"JMessageFormat");

		/**
		 * A foreign type for Java's <code>Object[]</code> array tupe.
		 */
		public static final QualifiedName JObjectArray = 
			QualifiedName.make(
				CAL_MessageFormat_internal.MODULE_NAME, 
				"JObjectArray");

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Utilities.MessageFormat module.
	 */
	public static final class Functions {
		/**
		 * Formats the specified object according to the specified message format to produce a string.
		 * @param messageFormat (CAL type: <code>Cal.Utilities.MessageFormat.JMessageFormat</code>)
		 *          the message format.
		 * @param object (CAL type: <code>Cal.Core.Prelude.JObject</code>)
		 *          the object.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          a string formatted according to the message format.
		 */
		public static final SourceModel.Expr jFormat(SourceModel.Expr messageFormat, SourceModel.Expr object) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jFormat), messageFormat, object});
		}

		/**
		 * Name binding for function: jFormat.
		 * @see #jFormat(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jFormat = 
			QualifiedName.make(
				CAL_MessageFormat_internal.MODULE_NAME, 
				"jFormat");

		/**
		 * Converts a <code>Cal.Core.Prelude.JList</code> to a <code>Cal.Utilities.MessageFormat.JObjectArray</code>.
		 * @param list (CAL type: <code>Cal.Core.Prelude.JList</code>)
		 *          the list.
		 * @return (CAL type: <code>Cal.Utilities.MessageFormat.JObjectArray</code>) 
		 *          the corresponding object array.
		 */
		public static final SourceModel.Expr jListToArray(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jListToArray), list});
		}

		/**
		 * Name binding for function: jListToArray.
		 * @see #jListToArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jListToArray = 
			QualifiedName.make(
				CAL_MessageFormat_internal.MODULE_NAME, 
				"jListToArray");

		/**
		 * Constructs an instance of <code>Cal.Utilities.MessageFormat.JMessageFormat</code>.
		 * @param pattern (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the message pattern.
		 * @param locale (CAL type: <code>Cal.Utilities.Locale.Locale</code>)
		 *          the locale to use for formatting.
		 * @return (CAL type: <code>Cal.Utilities.MessageFormat.JMessageFormat</code>) 
		 *          a new instance of <code>Cal.Utilities.MessageFormat.JMessageFormat</code>.
		 */
		public static final SourceModel.Expr jMakeMessageFormat(SourceModel.Expr pattern, SourceModel.Expr locale) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jMakeMessageFormat), pattern, locale});
		}

		/**
		 * @see #jMakeMessageFormat(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param pattern
		 * @param locale
		 * @return the SourceModel.Expr representing an application of jMakeMessageFormat
		 */
		public static final SourceModel.Expr jMakeMessageFormat(java.lang.String pattern, SourceModel.Expr locale) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jMakeMessageFormat), SourceModel.Expr.makeStringValue(pattern), locale});
		}

		/**
		 * Name binding for function: jMakeMessageFormat.
		 * @see #jMakeMessageFormat(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jMakeMessageFormat = 
			QualifiedName.make(
				CAL_MessageFormat_internal.MODULE_NAME, 
				"jMakeMessageFormat");

		/**
		 * Returns the placeholder string for a key that cannot be found.
		 * @param key (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the key that cannot be found.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          a placeholder string for the key.
		 */
		public static final SourceModel.Expr missingFormat(SourceModel.Expr key) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.missingFormat), key});
		}

		/**
		 * @see #missingFormat(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param key
		 * @return the SourceModel.Expr representing an application of missingFormat
		 */
		public static final SourceModel.Expr missingFormat(java.lang.String key) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.missingFormat), SourceModel.Expr.makeStringValue(key)});
		}

		/**
		 * Name binding for function: missingFormat.
		 * @see #missingFormat(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName missingFormat = 
			QualifiedName.make(
				CAL_MessageFormat_internal.MODULE_NAME, 
				"missingFormat");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = -1127773011;

}
