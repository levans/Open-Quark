/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_Time.java)
 * was generated from CAL module: Cal.Utilities.Time.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Utilities.Time module from Java code.
 *  
 * Creation date: Tue Jul 31 15:10:28 PDT 2007
 * --!>
 *  
 */

package org.openquark.cal.module.Cal.Utilities;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * This module defines types and functions for working with absolute time values.
 * @author Rick Cameron
 */
public final class CAL_Time {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Utilities.Time");

	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Utilities.Time module.
	 */
	public static final class TypeConstructors {
		/**
		 * The type <code>Cal.Utilities.Time.Calendar</code> represents a kind of calendar - Gregorian, Japanese Emperor, Chinese, Buddhist, Hebrew or Islamic.
		 * It is much like an enumeration, but it is implemented as a Java class.
		 */
		public static final QualifiedName Calendar = 
			QualifiedName.make(CAL_Time.MODULE_NAME, "Calendar");

		/**
		 * <code>Cal.Utilities.Time.DayOfWeek</code> is an enumeration that represents the days of the week in the Gregorian calendar.
		 * It implements <code>Cal.Core.Prelude.Eq</code>, but other common type classes, such as <code>Cal.Core.Prelude.Ord</code> and <code>Cal.Core.Prelude.Enum</code>, are not implemented
		 * because <code>Cal.Utilities.Time.DayOfWeek</code> has a circular ordering
		 */
		public static final QualifiedName DayOfWeek = 
			QualifiedName.make(CAL_Time.MODULE_NAME, "DayOfWeek");

		/**
		 * A <code>Cal.Utilities.Time.Duration</code> represents the difference between two <code>Cal.Utilities.Time.Time</code> values. It may be positive or negative. 
		 * As with <code>Cal.Utilities.Time.Time</code>, its quantum is 1 tick.
		 */
		public static final QualifiedName Duration = 
			QualifiedName.make(CAL_Time.MODULE_NAME, "Duration");

		/**
		 * The type <code>Cal.Utilities.Time.Time</code> represents a point on the time dimension of our universe. The range of time that can be represented
		 * is approximately 29,000 years before and after the base time, 00:00:00 UTC 1 Jan 1970. The resolution of measurement
		 * is 100 ns - this is called 1 tick.
		 */
		public static final QualifiedName Time = 
			QualifiedName.make(CAL_Time.MODULE_NAME, "Time");

	}
	/**
	 * This inner class (DataConstructors) contains constants
	 * and methods related to binding to CAL DataConstructors in the Cal.Utilities.Time module.
	 */
	public static final class DataConstructors {
		/*
		 * DataConstructors for the Cal.Utilities.Time.DayOfWeek data type.
		 */

		/**
		 * Binding for DataConstructor: Cal.Utilities.Time.Monday.
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.Time.Monday
		 */
		public static final SourceModel.Expr Monday() {
			return SourceModel.Expr.DataCons.make(DataConstructors.Monday);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.Time.Monday.
		 * @see #Monday()
		 */
		public static final QualifiedName Monday = 
			QualifiedName.make(CAL_Time.MODULE_NAME, "Monday");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.Time.Monday.
		 * @see #Monday()
		 */
		public static final int Monday_ordinal = 0;

		/**
		 * Binding for DataConstructor: Cal.Utilities.Time.Tuesday.
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.Time.Tuesday
		 */
		public static final SourceModel.Expr Tuesday() {
			return SourceModel.Expr.DataCons.make(DataConstructors.Tuesday);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.Time.Tuesday.
		 * @see #Tuesday()
		 */
		public static final QualifiedName Tuesday = 
			QualifiedName.make(CAL_Time.MODULE_NAME, "Tuesday");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.Time.Tuesday.
		 * @see #Tuesday()
		 */
		public static final int Tuesday_ordinal = 1;

		/**
		 * Binding for DataConstructor: Cal.Utilities.Time.Wednesday.
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.Time.Wednesday
		 */
		public static final SourceModel.Expr Wednesday() {
			return SourceModel.Expr.DataCons.make(DataConstructors.Wednesday);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.Time.Wednesday.
		 * @see #Wednesday()
		 */
		public static final QualifiedName Wednesday = 
			QualifiedName.make(CAL_Time.MODULE_NAME, "Wednesday");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.Time.Wednesday.
		 * @see #Wednesday()
		 */
		public static final int Wednesday_ordinal = 2;

		/**
		 * Binding for DataConstructor: Cal.Utilities.Time.Thursday.
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.Time.Thursday
		 */
		public static final SourceModel.Expr Thursday() {
			return SourceModel.Expr.DataCons.make(DataConstructors.Thursday);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.Time.Thursday.
		 * @see #Thursday()
		 */
		public static final QualifiedName Thursday = 
			QualifiedName.make(CAL_Time.MODULE_NAME, "Thursday");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.Time.Thursday.
		 * @see #Thursday()
		 */
		public static final int Thursday_ordinal = 3;

		/**
		 * Binding for DataConstructor: Cal.Utilities.Time.Friday.
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.Time.Friday
		 */
		public static final SourceModel.Expr Friday() {
			return SourceModel.Expr.DataCons.make(DataConstructors.Friday);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.Time.Friday.
		 * @see #Friday()
		 */
		public static final QualifiedName Friday = 
			QualifiedName.make(CAL_Time.MODULE_NAME, "Friday");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.Time.Friday.
		 * @see #Friday()
		 */
		public static final int Friday_ordinal = 4;

		/**
		 * Binding for DataConstructor: Cal.Utilities.Time.Saturday.
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.Time.Saturday
		 */
		public static final SourceModel.Expr Saturday() {
			return SourceModel.Expr.DataCons.make(DataConstructors.Saturday);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.Time.Saturday.
		 * @see #Saturday()
		 */
		public static final QualifiedName Saturday = 
			QualifiedName.make(CAL_Time.MODULE_NAME, "Saturday");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.Time.Saturday.
		 * @see #Saturday()
		 */
		public static final int Saturday_ordinal = 5;

		/**
		 * Binding for DataConstructor: Cal.Utilities.Time.Sunday.
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.Time.Sunday
		 */
		public static final SourceModel.Expr Sunday() {
			return SourceModel.Expr.DataCons.make(DataConstructors.Sunday);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.Time.Sunday.
		 * @see #Sunday()
		 */
		public static final QualifiedName Sunday = 
			QualifiedName.make(CAL_Time.MODULE_NAME, "Sunday");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.Time.Sunday.
		 * @see #Sunday()
		 */
		public static final int Sunday_ordinal = 6;

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Utilities.Time module.
	 */
	public static final class Functions {
		/**
		 * 
		 * @param duration1 (CAL type: <code>Cal.Utilities.Time.Duration</code>)
		 * @param duration2 (CAL type: <code>Cal.Utilities.Time.Duration</code>)
		 * @return (CAL type: <code>Cal.Utilities.Time.Duration</code>) 
		 *          the sum of two <code>Cal.Utilities.Time.Duration</code>s.
		 */
		public static final SourceModel.Expr addDurationToDuration(SourceModel.Expr duration1, SourceModel.Expr duration2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.addDurationToDuration), duration1, duration2});
		}

		/**
		 * Name binding for function: addDurationToDuration.
		 * @see #addDurationToDuration(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName addDurationToDuration = 
			QualifiedName.make(CAL_Time.MODULE_NAME, "addDurationToDuration");

		/**
		 * 
		 * @param time (CAL type: <code>Cal.Utilities.Time.Time</code>)
		 * @param duration (CAL type: <code>Cal.Utilities.Time.Duration</code>)
		 * @return (CAL type: <code>Cal.Utilities.Time.Time</code>) 
		 *          the <code>Cal.Utilities.Time.Time</code> that results from adding a <code>Cal.Utilities.Time.Duration</code> to a <code>Cal.Utilities.Time.Time</code>.
		 */
		public static final SourceModel.Expr addDurationToTime(SourceModel.Expr time, SourceModel.Expr duration) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.addDurationToTime), time, duration});
		}

		/**
		 * Name binding for function: addDurationToTime.
		 * @see #addDurationToTime(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName addDurationToTime = 
			QualifiedName.make(CAL_Time.MODULE_NAME, "addDurationToTime");

		/**
		 * The Buddhist calendar is a lunisolar calendar, and is sometimes used in Thailand and other south-east Asian countries.
		 * @return (CAL type: <code>Cal.Utilities.Time.Calendar</code>) 
		 */
		public static final SourceModel.Expr buddhistCalendar() {
			return SourceModel.Expr.Var.make(Functions.buddhistCalendar);
		}

		/**
		 * Name binding for function: buddhistCalendar.
		 * @see #buddhistCalendar()
		 */
		public static final QualifiedName buddhistCalendar = 
			QualifiedName.make(CAL_Time.MODULE_NAME, "buddhistCalendar");

		/**
		 * The Chinese calendar.
		 * @return (CAL type: <code>Cal.Utilities.Time.Calendar</code>) 
		 */
		public static final SourceModel.Expr chineseCalendar() {
			return SourceModel.Expr.Var.make(Functions.chineseCalendar);
		}

		/**
		 * Name binding for function: chineseCalendar.
		 * @see #chineseCalendar()
		 */
		public static final QualifiedName chineseCalendar = 
			QualifiedName.make(CAL_Time.MODULE_NAME, "chineseCalendar");

		/**
		 * 
		 * @param serializedTimeValue (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Utilities.Time.Time</code>) 
		 *          a <code>Cal.Utilities.Time.Time</code> value based on the serialized value provided.
		 */
		public static final SourceModel.Expr deserializeTimeValue(SourceModel.Expr serializedTimeValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.deserializeTimeValue), serializedTimeValue});
		}

		/**
		 * @see #deserializeTimeValue(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param serializedTimeValue
		 * @return the SourceModel.Expr representing an application of deserializeTimeValue
		 */
		public static final SourceModel.Expr deserializeTimeValue(java.lang.String serializedTimeValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.deserializeTimeValue), SourceModel.Expr.makeStringValue(serializedTimeValue)});
		}

		/**
		 * Name binding for function: deserializeTimeValue.
		 * @see #deserializeTimeValue(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName deserializeTimeValue = 
			QualifiedName.make(CAL_Time.MODULE_NAME, "deserializeTimeValue");

		/**
		 * Converts a <code>Cal.Utilities.Time.Duration</code> to a number of days, as a <code>Cal.Core.Prelude.Double</code>.
		 * @param duration (CAL type: <code>Cal.Utilities.Time.Duration</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 */
		public static final SourceModel.Expr durationToDays(SourceModel.Expr duration) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.durationToDays), duration});
		}

		/**
		 * Name binding for function: durationToDays.
		 * @see #durationToDays(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName durationToDays = 
			QualifiedName.make(CAL_Time.MODULE_NAME, "durationToDays");

		/**
		 * Converts a <code>Cal.Utilities.Time.Duration</code> to a number of minutes, as a <code>Cal.Core.Prelude.Double</code>.
		 * @param duration (CAL type: <code>Cal.Utilities.Time.Duration</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 */
		public static final SourceModel.Expr durationToMinutes(SourceModel.Expr duration) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.durationToMinutes), duration});
		}

		/**
		 * Name binding for function: durationToMinutes.
		 * @see #durationToMinutes(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName durationToMinutes = 
			QualifiedName.make(CAL_Time.MODULE_NAME, "durationToMinutes");

		/**
		 * 
		 * @param duration (CAL type: <code>Cal.Utilities.Time.Duration</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 *          converts a <code>Cal.Utilities.Time.Duration</code> to a number of seconds, as a <code>Cal.Core.Prelude.Double</code>.
		 */
		public static final SourceModel.Expr durationToSeconds(SourceModel.Expr duration) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.durationToSeconds), duration});
		}

		/**
		 * Name binding for function: durationToSeconds.
		 * @see #durationToSeconds(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName durationToSeconds = 
			QualifiedName.make(CAL_Time.MODULE_NAME, "durationToSeconds");

		/**
		 * Converts a <code>Cal.Utilities.Time.Duration</code> to a number of ticks, as a <code>Cal.Core.Prelude.Long</code>.
		 * @param duration (CAL type: <code>Cal.Utilities.Time.Duration</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Long</code>) 
		 *          ticks
		 */
		public static final SourceModel.Expr durationToTicks(SourceModel.Expr duration) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.durationToTicks), duration});
		}

		/**
		 * Name binding for function: durationToTicks.
		 * @see #durationToTicks(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName durationToTicks = 
			QualifiedName.make(CAL_Time.MODULE_NAME, "durationToTicks");

		/**
		 * Formats a <code>Cal.Utilities.Time.Time</code> value as a string.
		 * @param time (CAL type: <code>Cal.Utilities.Time.Time</code>)
		 *          the Time value to be formatted
		 * @param timeZone (CAL type: <code>Cal.Utilities.TimeZone.TimeZone</code>)
		 *          the time zone to use
		 * @param calendar (CAL type: <code>Cal.Utilities.Time.Calendar</code>)
		 *          the calendar to use
		 * @param pattern (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the pattern string used to format the Time value. It follows the JDK conventions.
		 * @param locale (CAL type: <code>Cal.Utilities.Locale.Locale</code>)
		 *          the locale to use for text elements such as day of the week or month name
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr formatTime(SourceModel.Expr time, SourceModel.Expr timeZone, SourceModel.Expr calendar, SourceModel.Expr pattern, SourceModel.Expr locale) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.formatTime), time, timeZone, calendar, pattern, locale});
		}

		/**
		 * @see #formatTime(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param time
		 * @param timeZone
		 * @param calendar
		 * @param pattern
		 * @param locale
		 * @return the SourceModel.Expr representing an application of formatTime
		 */
		public static final SourceModel.Expr formatTime(SourceModel.Expr time, SourceModel.Expr timeZone, SourceModel.Expr calendar, java.lang.String pattern, SourceModel.Expr locale) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.formatTime), time, timeZone, calendar, SourceModel.Expr.makeStringValue(pattern), locale});
		}

		/**
		 * Name binding for function: formatTime.
		 * @see #formatTime(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName formatTime = 
			QualifiedName.make(CAL_Time.MODULE_NAME, "formatTime");

		/**
		 * Returns the <code>Cal.Utilities.Time.DayOfWeek</code> for a <code>Cal.Utilities.Time.Time</code> in a specified <code>Cal.Utilities.TimeZone.TimeZone</code>.
		 * @param time (CAL type: <code>Cal.Utilities.Time.Time</code>)
		 * @param timeZone (CAL type: <code>Cal.Utilities.TimeZone.TimeZone</code>)
		 * @return (CAL type: <code>Cal.Utilities.Time.DayOfWeek</code>) 
		 */
		public static final SourceModel.Expr getDayOfWeek(SourceModel.Expr time, SourceModel.Expr timeZone) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getDayOfWeek), time, timeZone});
		}

		/**
		 * Name binding for function: getDayOfWeek.
		 * @see #getDayOfWeek(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getDayOfWeek = 
			QualifiedName.make(CAL_Time.MODULE_NAME, "getDayOfWeek");

		/**
		 * The Gregorian calendar, introduced by Pope Gregory in 1582.
		 * @return (CAL type: <code>Cal.Utilities.Time.Calendar</code>) 
		 */
		public static final SourceModel.Expr gregorianCalendar() {
			return SourceModel.Expr.Var.make(Functions.gregorianCalendar);
		}

		/**
		 * Name binding for function: gregorianCalendar.
		 * @see #gregorianCalendar()
		 */
		public static final QualifiedName gregorianCalendar = 
			QualifiedName.make(CAL_Time.MODULE_NAME, "gregorianCalendar");

		/**
		 * The Hebrew calendar is a lunisolar calendar.
		 * @return (CAL type: <code>Cal.Utilities.Time.Calendar</code>) 
		 */
		public static final SourceModel.Expr hebrewCalendar() {
			return SourceModel.Expr.Var.make(Functions.hebrewCalendar);
		}

		/**
		 * Name binding for function: hebrewCalendar.
		 * @see #hebrewCalendar()
		 */
		public static final QualifiedName hebrewCalendar = 
			QualifiedName.make(CAL_Time.MODULE_NAME, "hebrewCalendar");

		/**
		 * The Islamic calendar is a lunar calendar. It is also called the Hijri calendar.
		 * @return (CAL type: <code>Cal.Utilities.Time.Calendar</code>) 
		 */
		public static final SourceModel.Expr islamicCalendar() {
			return SourceModel.Expr.Var.make(Functions.islamicCalendar);
		}

		/**
		 * Name binding for function: islamicCalendar.
		 * @see #islamicCalendar()
		 */
		public static final QualifiedName islamicCalendar = 
			QualifiedName.make(CAL_Time.MODULE_NAME, "islamicCalendar");

		/**
		 * The Japanese Emperor calendar. The months and days are equivalent to those in the Gregorian calendar,
		 * but the years are counted from the beginning of the reign of the emperor.
		 * @return (CAL type: <code>Cal.Utilities.Time.Calendar</code>) 
		 */
		public static final SourceModel.Expr japaneseCalendar() {
			return SourceModel.Expr.Var.make(Functions.japaneseCalendar);
		}

		/**
		 * Name binding for function: japaneseCalendar.
		 * @see #japaneseCalendar()
		 */
		public static final QualifiedName japaneseCalendar = 
			QualifiedName.make(CAL_Time.MODULE_NAME, "japaneseCalendar");

		/**
		 * 
		 * @param ticks (CAL type: <code>Cal.Core.Prelude.Long</code>)
		 * @return (CAL type: <code>Cal.Utilities.Time.Time</code>) 
		 *          a <code>Cal.Utilities.Time.Time</code> value from an absolute number of ticks.
		 */
		public static final SourceModel.Expr makeTimeFromTicks(SourceModel.Expr ticks) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeTimeFromTicks), ticks});
		}

		/**
		 * @see #makeTimeFromTicks(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param ticks
		 * @return the SourceModel.Expr representing an application of makeTimeFromTicks
		 */
		public static final SourceModel.Expr makeTimeFromTicks(long ticks) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeTimeFromTicks), SourceModel.Expr.makeLongValue(ticks)});
		}

		/**
		 * Name binding for function: makeTimeFromTicks.
		 * @see #makeTimeFromTicks(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeTimeFromTicks = 
			QualifiedName.make(CAL_Time.MODULE_NAME, "makeTimeFromTicks");

		/**
		 * 
		 * @param year (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @param month (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @param day (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @param hour (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @param min (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @param sec (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @param ticks (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @param timeZone (CAL type: <code>Cal.Utilities.TimeZone.TimeZone</code>)
		 * @return (CAL type: <code>Cal.Utilities.Time.Time</code>) 
		 *          a <code>Cal.Utilities.Time.Time</code> value from a year, month, day, hour, minute, second, ticks and time zone.
		 */
		public static final SourceModel.Expr makeTimeValue(SourceModel.Expr year, SourceModel.Expr month, SourceModel.Expr day, SourceModel.Expr hour, SourceModel.Expr min, SourceModel.Expr sec, SourceModel.Expr ticks, SourceModel.Expr timeZone) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeTimeValue), year, month, day, hour, min, sec, ticks, timeZone});
		}

		/**
		 * @see #makeTimeValue(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param year
		 * @param month
		 * @param day
		 * @param hour
		 * @param min
		 * @param sec
		 * @param ticks
		 * @param timeZone
		 * @return the SourceModel.Expr representing an application of makeTimeValue
		 */
		public static final SourceModel.Expr makeTimeValue(int year, int month, int day, int hour, int min, int sec, int ticks, SourceModel.Expr timeZone) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeTimeValue), SourceModel.Expr.makeIntValue(year), SourceModel.Expr.makeIntValue(month), SourceModel.Expr.makeIntValue(day), SourceModel.Expr.makeIntValue(hour), SourceModel.Expr.makeIntValue(min), SourceModel.Expr.makeIntValue(sec), SourceModel.Expr.makeIntValue(ticks), timeZone});
		}

		/**
		 * Name binding for function: makeTimeValue.
		 * @see #makeTimeValue(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeTimeValue = 
			QualifiedName.make(CAL_Time.MODULE_NAME, "makeTimeValue");

		/**
		 * Construct a <code>Cal.Utilities.Time.Time</code> value from year, month, day, hour, minute, second and tick in the UTC time zone.
		 * @param ymdhmst (CAL type: <code>(Cal.Core.Prelude.Int, Cal.Core.Prelude.Int, Cal.Core.Prelude.Int, Cal.Core.Prelude.Int, Cal.Core.Prelude.Int, Cal.Core.Prelude.Int, Cal.Core.Prelude.Int)</code>)
		 * @return (CAL type: <code>Cal.Utilities.Time.Time</code>) 
		 */
		public static final SourceModel.Expr makeUTCTimeFromTuple(SourceModel.Expr ymdhmst) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeUTCTimeFromTuple), ymdhmst});
		}

		/**
		 * Name binding for function: makeUTCTimeFromTuple.
		 * @see #makeUTCTimeFromTuple(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeUTCTimeFromTuple = 
			QualifiedName.make(CAL_Time.MODULE_NAME, "makeUTCTimeFromTuple");

		/**
		 * 
		 * @param year (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @param month (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @param day (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @param hour (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @param min (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @param sec (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @param ticks (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @return (CAL type: <code>Cal.Utilities.Time.Time</code>) 
		 *          a <code>Cal.Utilities.Time.Time</code> value from year, month, day, hour, minute, second and tick in the UTC time zone.
		 */
		public static final SourceModel.Expr makeUTCTimeValue(SourceModel.Expr year, SourceModel.Expr month, SourceModel.Expr day, SourceModel.Expr hour, SourceModel.Expr min, SourceModel.Expr sec, SourceModel.Expr ticks) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeUTCTimeValue), year, month, day, hour, min, sec, ticks});
		}

		/**
		 * @see #makeUTCTimeValue(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param year
		 * @param month
		 * @param day
		 * @param hour
		 * @param min
		 * @param sec
		 * @param ticks
		 * @return the SourceModel.Expr representing an application of makeUTCTimeValue
		 */
		public static final SourceModel.Expr makeUTCTimeValue(int year, int month, int day, int hour, int min, int sec, int ticks) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeUTCTimeValue), SourceModel.Expr.makeIntValue(year), SourceModel.Expr.makeIntValue(month), SourceModel.Expr.makeIntValue(day), SourceModel.Expr.makeIntValue(hour), SourceModel.Expr.makeIntValue(min), SourceModel.Expr.makeIntValue(sec), SourceModel.Expr.makeIntValue(ticks)});
		}

		/**
		 * Name binding for function: makeUTCTimeValue.
		 * @see #makeUTCTimeValue(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeUTCTimeValue = 
			QualifiedName.make(CAL_Time.MODULE_NAME, "makeUTCTimeValue");

		/**
		 * Converts a number of days to a <code>Cal.Utilities.Time.Duration</code>.
		 * @param nDays (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 * @return (CAL type: <code>Cal.Utilities.Time.Duration</code>) 
		 */
		public static final SourceModel.Expr nDaysDuration(SourceModel.Expr nDays) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.nDaysDuration), nDays});
		}

		/**
		 * @see #nDaysDuration(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param nDays
		 * @return the SourceModel.Expr representing an application of nDaysDuration
		 */
		public static final SourceModel.Expr nDaysDuration(double nDays) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.nDaysDuration), SourceModel.Expr.makeDoubleValue(nDays)});
		}

		/**
		 * Name binding for function: nDaysDuration.
		 * @see #nDaysDuration(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName nDaysDuration = 
			QualifiedName.make(CAL_Time.MODULE_NAME, "nDaysDuration");

		/**
		 * Returns the number of days since the most recent Monday.
		 * @param dow (CAL type: <code>Cal.Utilities.Time.DayOfWeek</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 */
		public static final SourceModel.Expr nDaysSinceMonday(SourceModel.Expr dow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.nDaysSinceMonday), dow});
		}

		/**
		 * Name binding for function: nDaysSinceMonday.
		 * @see #nDaysSinceMonday(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName nDaysSinceMonday = 
			QualifiedName.make(CAL_Time.MODULE_NAME, "nDaysSinceMonday");

		/**
		 * Converts an integer number of seconds to a <code>Cal.Utilities.Time.Duration</code>.
		 * @param nSeconds (CAL type: <code>Cal.Core.Prelude.Long</code>)
		 *          An integer number of seconds
		 * @return (CAL type: <code>Cal.Utilities.Time.Duration</code>) 
		 *          duration
		 */
		public static final SourceModel.Expr nSecondsAsLongToDuration(SourceModel.Expr nSeconds) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.nSecondsAsLongToDuration), nSeconds});
		}

		/**
		 * @see #nSecondsAsLongToDuration(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param nSeconds
		 * @return the SourceModel.Expr representing an application of nSecondsAsLongToDuration
		 */
		public static final SourceModel.Expr nSecondsAsLongToDuration(long nSeconds) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.nSecondsAsLongToDuration), SourceModel.Expr.makeLongValue(nSeconds)});
		}

		/**
		 * Name binding for function: nSecondsAsLongToDuration.
		 * @see #nSecondsAsLongToDuration(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName nSecondsAsLongToDuration = 
			QualifiedName.make(CAL_Time.MODULE_NAME, "nSecondsAsLongToDuration");

		/**
		 * Converts a number of seconds to a <code>Cal.Utilities.Time.Duration</code>.
		 * @param nSeconds (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 *          A number of seconds (which can have a fractional part)
		 * @return (CAL type: <code>Cal.Utilities.Time.Duration</code>) 
		 *          duration
		 */
		public static final SourceModel.Expr nSecondsDuration(SourceModel.Expr nSeconds) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.nSecondsDuration), nSeconds});
		}

		/**
		 * @see #nSecondsDuration(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param nSeconds
		 * @return the SourceModel.Expr representing an application of nSecondsDuration
		 */
		public static final SourceModel.Expr nSecondsDuration(double nSeconds) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.nSecondsDuration), SourceModel.Expr.makeDoubleValue(nSeconds)});
		}

		/**
		 * Name binding for function: nSecondsDuration.
		 * @see #nSecondsDuration(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName nSecondsDuration = 
			QualifiedName.make(CAL_Time.MODULE_NAME, "nSecondsDuration");

		/**
		 * Helper binding method for function: now. 
		 * @return the SourceModule.expr representing an application of now
		 */
		public static final SourceModel.Expr now() {
			return SourceModel.Expr.Var.make(Functions.now);
		}

		/**
		 * Name binding for function: now.
		 * @see #now()
		 */
		public static final QualifiedName now = 
			QualifiedName.make(CAL_Time.MODULE_NAME, "now");

		/**
		 * Parses a time value from a string using the specified pattern.
		 * @param timeString (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          a time string to be parsed
		 * @param timeZone (CAL type: <code>Cal.Utilities.TimeZone.TimeZone</code>)
		 *          the time zone to use
		 * @param calendar (CAL type: <code>Cal.Utilities.Time.Calendar</code>)
		 *          the calendar to use
		 * @param pattern (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the pattern string used to format the Time value. It follows the JDK conventions.
		 * @param locale (CAL type: <code>Cal.Utilities.Locale.Locale</code>)
		 *          the locale to use for text elements such as day of the week or month name
		 * @return (CAL type: <code>Cal.Utilities.Time.Time</code>) 
		 */
		public static final SourceModel.Expr parseTime(SourceModel.Expr timeString, SourceModel.Expr timeZone, SourceModel.Expr calendar, SourceModel.Expr pattern, SourceModel.Expr locale) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.parseTime), timeString, timeZone, calendar, pattern, locale});
		}

		/**
		 * @see #parseTime(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param timeString
		 * @param timeZone
		 * @param calendar
		 * @param pattern
		 * @param locale
		 * @return the SourceModel.Expr representing an application of parseTime
		 */
		public static final SourceModel.Expr parseTime(java.lang.String timeString, SourceModel.Expr timeZone, SourceModel.Expr calendar, java.lang.String pattern, SourceModel.Expr locale) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.parseTime), SourceModel.Expr.makeStringValue(timeString), timeZone, calendar, SourceModel.Expr.makeStringValue(pattern), locale});
		}

		/**
		 * Name binding for function: parseTime.
		 * @see #parseTime(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName parseTime = 
			QualifiedName.make(CAL_Time.MODULE_NAME, "parseTime");

		/**
		 * 
		 * @param time (CAL type: <code>Cal.Utilities.Time.Time</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          a string representing the serialized form of the <code>Cal.Utilities.Time.Time</code> value.
		 */
		public static final SourceModel.Expr serializeTimeValue(SourceModel.Expr time) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.serializeTimeValue), time});
		}

		/**
		 * Name binding for function: serializeTimeValue.
		 * @see #serializeTimeValue(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName serializeTimeValue = 
			QualifiedName.make(CAL_Time.MODULE_NAME, "serializeTimeValue");

		/**
		 * 
		 * @param duration1 (CAL type: <code>Cal.Utilities.Time.Duration</code>)
		 * @param duration2 (CAL type: <code>Cal.Utilities.Time.Duration</code>)
		 * @return (CAL type: <code>Cal.Utilities.Time.Duration</code>) 
		 *          he difference between two <code>Cal.Utilities.Time.Duration</code>s.
		 */
		public static final SourceModel.Expr subtractDurationFromDuration(SourceModel.Expr duration1, SourceModel.Expr duration2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.subtractDurationFromDuration), duration1, duration2});
		}

		/**
		 * Name binding for function: subtractDurationFromDuration.
		 * @see #subtractDurationFromDuration(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName subtractDurationFromDuration = 
			QualifiedName.make(
				CAL_Time.MODULE_NAME, 
				"subtractDurationFromDuration");

		/**
		 * 
		 * @param time1 (CAL type: <code>Cal.Utilities.Time.Time</code>)
		 * @param time2 (CAL type: <code>Cal.Utilities.Time.Time</code>)
		 * @return (CAL type: <code>Cal.Utilities.Time.Duration</code>) 
		 *          the <code>Cal.Utilities.Time.Duration</code> that represents the difference between two <code>Cal.Utilities.Time.Time</code>s.
		 */
		public static final SourceModel.Expr subtractTimeFromTime(SourceModel.Expr time1, SourceModel.Expr time2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.subtractTimeFromTime), time1, time2});
		}

		/**
		 * Name binding for function: subtractTimeFromTime.
		 * @see #subtractTimeFromTime(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName subtractTimeFromTime = 
			QualifiedName.make(CAL_Time.MODULE_NAME, "subtractTimeFromTime");

		/**
		 * Converts a number of ticks to a <code>Cal.Utilities.Time.Duration</code>.
		 * @param ticks (CAL type: <code>Cal.Core.Prelude.Long</code>)
		 * @return (CAL type: <code>Cal.Utilities.Time.Duration</code>) 
		 *          duration
		 */
		public static final SourceModel.Expr ticksToDuration(SourceModel.Expr ticks) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.ticksToDuration), ticks});
		}

		/**
		 * @see #ticksToDuration(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param ticks
		 * @return the SourceModel.Expr representing an application of ticksToDuration
		 */
		public static final SourceModel.Expr ticksToDuration(long ticks) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.ticksToDuration), SourceModel.Expr.makeLongValue(ticks)});
		}

		/**
		 * Name binding for function: ticksToDuration.
		 * @see #ticksToDuration(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName ticksToDuration = 
			QualifiedName.make(CAL_Time.MODULE_NAME, "ticksToDuration");

		/**
		 * Extract the components of a <code>Cal.Utilities.Time.Time</code>.
		 * @param timeZone (CAL type: <code>Cal.Utilities.TimeZone.TimeZone</code>)
		 * @param time (CAL type: <code>Cal.Utilities.Time.Time</code>)
		 * @return (CAL type: <code>{day :: Cal.Core.Prelude.Int, hours :: Cal.Core.Prelude.Int, minutes :: Cal.Core.Prelude.Int, month :: Cal.Core.Prelude.Int, seconds :: Cal.Core.Prelude.Int, ticks :: Cal.Core.Prelude.Int, year :: Cal.Core.Prelude.Int}</code>) 
		 */
		public static final SourceModel.Expr timeParts(SourceModel.Expr timeZone, SourceModel.Expr time) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.timeParts), timeZone, time});
		}

		/**
		 * Name binding for function: timeParts.
		 * @see #timeParts(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName timeParts = 
			QualifiedName.make(CAL_Time.MODULE_NAME, "timeParts");

		/**
		 * Returns a <code>Cal.Utilities.Time.Duration</code> of 0 ticks.
		 * @return (CAL type: <code>Cal.Utilities.Time.Duration</code>) 
		 */
		public static final SourceModel.Expr zeroDuration() {
			return SourceModel.Expr.Var.make(Functions.zeroDuration);
		}

		/**
		 * Name binding for function: zeroDuration.
		 * @see #zeroDuration()
		 */
		public static final QualifiedName zeroDuration = 
			QualifiedName.make(CAL_Time.MODULE_NAME, "zeroDuration");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = -227662948;

}
