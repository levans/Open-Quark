/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_TimeZone_internal.java)
 * was generated from CAL module: Cal.Utilities.TimeZone.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Utilities.TimeZone module from Java code.
 *  
 * Creation date: Thu Oct 18 19:18:19 PDT 2007
 * --!>
 *  
 */

package org.openquark.cal.internal.module.Cal.Utilities;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * Defines the <code>Cal.Utilities.TimeZone.TimeZone</code> type and its affiliated operations.
 * @author Rick Cameron
 * @author Joseph Wong
 */
public final class CAL_TimeZone_internal {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Utilities.TimeZone");

	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Utilities.TimeZone module.
	 */
	public static final class TypeConstructors {
		/**
		 * Java's own time zone class.
		 */
		public static final QualifiedName JavaUtilTimeZone = 
			QualifiedName.make(
				CAL_TimeZone_internal.MODULE_NAME, 
				"JavaUtilTimeZone");

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Utilities.TimeZone module.
	 */
	public static final class Functions {
		/**
		 * Converts a Java time zone to an immutable time zone.
		 * @param javaUtilTimeZone (CAL type: <code>Cal.Utilities.TimeZone.JavaUtilTimeZone</code>)
		 *          the time zone.
		 * @return (CAL type: <code>Cal.Utilities.TimeZone.TimeZone</code>) 
		 *          the corresponding immutable time zone.
		 */
		public static final SourceModel.Expr fromJavaUtilTimeZone(SourceModel.Expr javaUtilTimeZone) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromJavaUtilTimeZone), javaUtilTimeZone});
		}

		/**
		 * Name binding for function: fromJavaUtilTimeZone.
		 * @see #fromJavaUtilTimeZone(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fromJavaUtilTimeZone = 
			QualifiedName.make(
				CAL_TimeZone_internal.MODULE_NAME, 
				"fromJavaUtilTimeZone");

		/**
		 * Returns the Java time zone's ID.
		 * @param javaUtilTimeZone (CAL type: <code>Cal.Utilities.TimeZone.JavaUtilTimeZone</code>)
		 *          the time zone.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the time zone ID.
		 */
		public static final SourceModel.Expr jGetJavaUtilTimeZoneID(SourceModel.Expr javaUtilTimeZone) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jGetJavaUtilTimeZoneID), javaUtilTimeZone});
		}

		/**
		 * Name binding for function: jGetJavaUtilTimeZoneID.
		 * @see #jGetJavaUtilTimeZoneID(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jGetJavaUtilTimeZoneID = 
			QualifiedName.make(
				CAL_TimeZone_internal.MODULE_NAME, 
				"jGetJavaUtilTimeZoneID");

		/**
		 * Returns the time zone defined in the given set of properties of an execution context.
		 * @param executionContextProperties (CAL type: <code>Cal.Core.System.ExecutionContextProperties</code>)
		 *          the set of system properties.
		 * @return (CAL type: <code>Cal.Utilities.TimeZone.JavaUtilTimeZone</code>) 
		 *          the time zone defined in the system properties.
		 */
		public static final SourceModel.Expr jGetTimeZone(SourceModel.Expr executionContextProperties) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jGetTimeZone), executionContextProperties});
		}

		/**
		 * Name binding for function: jGetTimeZone.
		 * @see #jGetTimeZone(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jGetTimeZone = 
			QualifiedName.make(
				CAL_TimeZone_internal.MODULE_NAME, 
				"jGetTimeZone");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = -1151284829;

}
