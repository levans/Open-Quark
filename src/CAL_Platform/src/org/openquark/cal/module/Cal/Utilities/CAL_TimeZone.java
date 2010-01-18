/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_TimeZone.java)
 * was generated from CAL module: Cal.Utilities.TimeZone.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Utilities.TimeZone module from Java code.
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
 * Defines the <code>Cal.Utilities.TimeZone.TimeZone</code> type and its affiliated operations.
 * @author Rick Cameron
 * @author Joseph Wong
 */
public final class CAL_TimeZone {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Utilities.TimeZone");

	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Utilities.TimeZone module.
	 */
	public static final class TypeConstructors {
		/**
		 * Represents a time zone.
		 */
		public static final QualifiedName TimeZone = 
			QualifiedName.make(CAL_TimeZone.MODULE_NAME, "TimeZone");

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Utilities.TimeZone module.
	 */
	public static final class Functions {
		/**
		 * Returns the time zone associated with the current execution context. This is a constant for
		 * a particular execution context.
		 * @return (CAL type: <code>Cal.Utilities.TimeZone.TimeZone</code>) 
		 *          the time zone associated with the current execution context.
		 */
		public static final SourceModel.Expr currentTimeZone() {
			return SourceModel.Expr.Var.make(Functions.currentTimeZone);
		}

		/**
		 * Name binding for function: currentTimeZone.
		 * @see #currentTimeZone()
		 */
		public static final QualifiedName currentTimeZone = 
			QualifiedName.make(CAL_TimeZone.MODULE_NAME, "currentTimeZone");

		/**
		 * Returns the long display name of the time zone in the given locale.
		 * @param timeZone (CAL type: <code>Cal.Utilities.TimeZone.TimeZone</code>)
		 *          the time zone.
		 * @param locale (CAL type: <code>Cal.Utilities.Locale.Locale</code>)
		 *          the locale in which the name should be localized.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the long display name.
		 */
		public static final SourceModel.Expr longDisplayName(SourceModel.Expr timeZone, SourceModel.Expr locale) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longDisplayName), timeZone, locale});
		}

		/**
		 * Name binding for function: longDisplayName.
		 * @see #longDisplayName(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName longDisplayName = 
			QualifiedName.make(CAL_TimeZone.MODULE_NAME, "longDisplayName");

		/**
		 * Constructs a time zone from a time zone ID.
		 * <p>
		 * Time zone IDs can be of the form:
		 * 
		 * <pre> GMT[+|-]hh[[:]mm]
		 * </pre>
		 * 
		 * or can be one of the well known time zones such as "America/Los_Angeles".
		 * 
		 * @param id (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the time zone ID.
		 * @return (CAL type: <code>Cal.Utilities.TimeZone.TimeZone</code>) 
		 *          the corresponding time zone.
		 */
		public static final SourceModel.Expr makeTimeZone(SourceModel.Expr id) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeTimeZone), id});
		}

		/**
		 * @see #makeTimeZone(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param id
		 * @return the SourceModel.Expr representing an application of makeTimeZone
		 */
		public static final SourceModel.Expr makeTimeZone(java.lang.String id) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeTimeZone), SourceModel.Expr.makeStringValue(id)});
		}

		/**
		 * Name binding for function: makeTimeZone.
		 * @see #makeTimeZone(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeTimeZone = 
			QualifiedName.make(CAL_TimeZone.MODULE_NAME, "makeTimeZone");

		/**
		 * Returns the short display name of the time zone in the given locale.
		 * @param timeZone (CAL type: <code>Cal.Utilities.TimeZone.TimeZone</code>)
		 *          the time zone.
		 * @param locale (CAL type: <code>Cal.Utilities.Locale.Locale</code>)
		 *          the locale in which the name should be localized.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the short display name.
		 */
		public static final SourceModel.Expr shortDisplayName(SourceModel.Expr timeZone, SourceModel.Expr locale) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortDisplayName), timeZone, locale});
		}

		/**
		 * Name binding for function: shortDisplayName.
		 * @see #shortDisplayName(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shortDisplayName = 
			QualifiedName.make(CAL_TimeZone.MODULE_NAME, "shortDisplayName");

		/**
		 * Returns the time zone ID of the given time zone.
		 * @param timeZone (CAL type: <code>Cal.Utilities.TimeZone.TimeZone</code>)
		 *          the time zone.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the time zone ID.
		 */
		public static final SourceModel.Expr timeZoneID(SourceModel.Expr timeZone) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.timeZoneID), timeZone});
		}

		/**
		 * Name binding for function: timeZoneID.
		 * @see #timeZoneID(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName timeZoneID = 
			QualifiedName.make(CAL_TimeZone.MODULE_NAME, "timeZoneID");

		/**
		 * The UTC time zone.
		 * @return (CAL type: <code>Cal.Utilities.TimeZone.TimeZone</code>) 
		 */
		public static final SourceModel.Expr utcTimeZone() {
			return SourceModel.Expr.Var.make(Functions.utcTimeZone);
		}

		/**
		 * Name binding for function: utcTimeZone.
		 * @see #utcTimeZone()
		 */
		public static final QualifiedName utcTimeZone = 
			QualifiedName.make(CAL_TimeZone.MODULE_NAME, "utcTimeZone");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = 1023591186;

}
