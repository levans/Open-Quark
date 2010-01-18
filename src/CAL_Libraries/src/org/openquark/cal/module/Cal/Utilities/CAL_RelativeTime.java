/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_RelativeTime.java)
 * was generated from CAL module: Cal.Utilities.RelativeTime.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Utilities.RelativeTime module from Java code.
 *  
 * Creation date: Tue Aug 28 15:58:40 PDT 2007
 * --!>
 *  
 */

package org.openquark.cal.module.Cal.Utilities;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * This module contains the <code>Cal.Utilities.RelativeTime.RelativeDate</code>, <code>Cal.Utilities.RelativeTime.RelativeTime</code> and
 * <code>Cal.Utilities.RelativeTime.RelativeDateTime</code> types, together with the functions that operate on them.
 * <p>
 * These types represent dates, times and date-time values, without the notion of a specific time-zone. 
 * In other words, they are not a precise moment in time, such as defined by the <code>Cal.Utilities.Time.Time</code> type,
 * but rather a relative concept. For example, it is valid to speak of Sept 1, 2006 as being the first day of the
 * month of September, but depending on where in the world one is at a specific moment in time, the actual date
 * could be August 31, 2005 or September 2, 2005.
 * <p>
 * In addition, <code>Cal.Utilities.RelativeTime.RelativeDate</code> and <code>Cal.Utilities.RelativeTime.RelativeDateTime</code> are implicitly with respect
 * to the Gregorian calendar.
 * 
 * @author Bo Ilic
 */
public final class CAL_RelativeTime {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Utilities.RelativeTime");

	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Utilities.RelativeTime module.
	 */
	public static final class TypeConstructors {
		/**
		 * Used to specify the units of a time interval to be added
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.RelativeTime.dateAdd
		 * </dl>
		 */
		public static final QualifiedName DateAddType = 
			QualifiedName.make(CAL_RelativeTime.MODULE_NAME, "DateAddType");

		/**
		 * Type to specify the units for the result of performing a difference between 2 date-time values.
		 * <p>
		 * For example, it allows you to specify that you want the difference expressed in quarters, or weeks.
		 */
		public static final QualifiedName DateDiffType = 
			QualifiedName.make(CAL_RelativeTime.MODULE_NAME, "DateDiffType");

		/** Name binding for TypeConsApp: DatePartType. */
		public static final QualifiedName DatePartType = 
			QualifiedName.make(CAL_RelativeTime.MODULE_NAME, "DatePartType");

		/** Name binding for TypeConsApp: DayOfWeek. */
		public static final QualifiedName DayOfWeek = 
			QualifiedName.make(CAL_RelativeTime.MODULE_NAME, "DayOfWeek");

		/**
		 * Used to specify the definition of what the first week of the year should be considered to be.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.RelativeTime.datePart
		 * </dl>
		 */
		public static final QualifiedName FirstWeekOfYear = 
			QualifiedName.make(CAL_RelativeTime.MODULE_NAME, "FirstWeekOfYear");

		/** Name binding for TypeConsApp: JDate. */
		public static final QualifiedName JDate = 
			QualifiedName.make(CAL_RelativeTime.MODULE_NAME, "JDate");

		/**
		 * Type to represent a date value without the notion of a time-zone. The date values are implicitly
		 * with respect to the Gregorian calendar.
		 */
		public static final QualifiedName RelativeDate = 
			QualifiedName.make(CAL_RelativeTime.MODULE_NAME, "RelativeDate");

		/**
		 * Type to represent a date-time value without the notion of a time-zone. The date-time values are implicitly
		 * with respect to the Gregorian calendar.
		 */
		public static final QualifiedName RelativeDateTime = 
			QualifiedName.make(CAL_RelativeTime.MODULE_NAME, "RelativeDateTime");

		/**
		 * Type to represent a time value in a single 24 hour day. The actual day is unspecified so it is a relative
		 * notion of time.
		 */
		public static final QualifiedName RelativeTime = 
			QualifiedName.make(CAL_RelativeTime.MODULE_NAME, "RelativeTime");

	}
	/**
	 * This inner class (DataConstructors) contains constants
	 * and methods related to binding to CAL DataConstructors in the Cal.Utilities.RelativeTime module.
	 */
	public static final class DataConstructors {
		/*
		 * DataConstructors for the Cal.Utilities.RelativeTime.DateAddType data type.
		 */

		/**
		 * Binding for DataConstructor: Cal.Utilities.RelativeTime.YearAdd.
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.RelativeTime.YearAdd
		 */
		public static final SourceModel.Expr YearAdd() {
			return SourceModel.Expr.DataCons.make(DataConstructors.YearAdd);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.RelativeTime.YearAdd.
		 * @see #YearAdd()
		 */
		public static final QualifiedName YearAdd = 
			QualifiedName.make(CAL_RelativeTime.MODULE_NAME, "YearAdd");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.RelativeTime.YearAdd.
		 * @see #YearAdd()
		 */
		public static final int YearAdd_ordinal = 0;

		/**
		 * Binding for DataConstructor: Cal.Utilities.RelativeTime.QuarterAdd.
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.RelativeTime.QuarterAdd
		 */
		public static final SourceModel.Expr QuarterAdd() {
			return SourceModel.Expr.DataCons.make(DataConstructors.QuarterAdd);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.RelativeTime.QuarterAdd.
		 * @see #QuarterAdd()
		 */
		public static final QualifiedName QuarterAdd = 
			QualifiedName.make(CAL_RelativeTime.MODULE_NAME, "QuarterAdd");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.RelativeTime.QuarterAdd.
		 * @see #QuarterAdd()
		 */
		public static final int QuarterAdd_ordinal = 1;

		/**
		 * Binding for DataConstructor: Cal.Utilities.RelativeTime.MonthAdd.
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.RelativeTime.MonthAdd
		 */
		public static final SourceModel.Expr MonthAdd() {
			return SourceModel.Expr.DataCons.make(DataConstructors.MonthAdd);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.RelativeTime.MonthAdd.
		 * @see #MonthAdd()
		 */
		public static final QualifiedName MonthAdd = 
			QualifiedName.make(CAL_RelativeTime.MODULE_NAME, "MonthAdd");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.RelativeTime.MonthAdd.
		 * @see #MonthAdd()
		 */
		public static final int MonthAdd_ordinal = 2;

		/**
		 * Binding for DataConstructor: Cal.Utilities.RelativeTime.DayAdd.
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.RelativeTime.DayAdd
		 */
		public static final SourceModel.Expr DayAdd() {
			return SourceModel.Expr.DataCons.make(DataConstructors.DayAdd);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.RelativeTime.DayAdd.
		 * @see #DayAdd()
		 */
		public static final QualifiedName DayAdd = 
			QualifiedName.make(CAL_RelativeTime.MODULE_NAME, "DayAdd");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.RelativeTime.DayAdd.
		 * @see #DayAdd()
		 */
		public static final int DayAdd_ordinal = 3;

		/**
		 * Binding for DataConstructor: Cal.Utilities.RelativeTime.WeekAdd.
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.RelativeTime.WeekAdd
		 */
		public static final SourceModel.Expr WeekAdd() {
			return SourceModel.Expr.DataCons.make(DataConstructors.WeekAdd);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.RelativeTime.WeekAdd.
		 * @see #WeekAdd()
		 */
		public static final QualifiedName WeekAdd = 
			QualifiedName.make(CAL_RelativeTime.MODULE_NAME, "WeekAdd");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.RelativeTime.WeekAdd.
		 * @see #WeekAdd()
		 */
		public static final int WeekAdd_ordinal = 4;

		/**
		 * Binding for DataConstructor: Cal.Utilities.RelativeTime.HourAdd.
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.RelativeTime.HourAdd
		 */
		public static final SourceModel.Expr HourAdd() {
			return SourceModel.Expr.DataCons.make(DataConstructors.HourAdd);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.RelativeTime.HourAdd.
		 * @see #HourAdd()
		 */
		public static final QualifiedName HourAdd = 
			QualifiedName.make(CAL_RelativeTime.MODULE_NAME, "HourAdd");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.RelativeTime.HourAdd.
		 * @see #HourAdd()
		 */
		public static final int HourAdd_ordinal = 5;

		/**
		 * Binding for DataConstructor: Cal.Utilities.RelativeTime.MinuteAdd.
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.RelativeTime.MinuteAdd
		 */
		public static final SourceModel.Expr MinuteAdd() {
			return SourceModel.Expr.DataCons.make(DataConstructors.MinuteAdd);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.RelativeTime.MinuteAdd.
		 * @see #MinuteAdd()
		 */
		public static final QualifiedName MinuteAdd = 
			QualifiedName.make(CAL_RelativeTime.MODULE_NAME, "MinuteAdd");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.RelativeTime.MinuteAdd.
		 * @see #MinuteAdd()
		 */
		public static final int MinuteAdd_ordinal = 6;

		/**
		 * Binding for DataConstructor: Cal.Utilities.RelativeTime.SecondAdd.
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.RelativeTime.SecondAdd
		 */
		public static final SourceModel.Expr SecondAdd() {
			return SourceModel.Expr.DataCons.make(DataConstructors.SecondAdd);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.RelativeTime.SecondAdd.
		 * @see #SecondAdd()
		 */
		public static final QualifiedName SecondAdd = 
			QualifiedName.make(CAL_RelativeTime.MODULE_NAME, "SecondAdd");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.RelativeTime.SecondAdd.
		 * @see #SecondAdd()
		 */
		public static final int SecondAdd_ordinal = 7;

		/*
		 * DataConstructors for the Cal.Utilities.RelativeTime.DateDiffType data type.
		 */

		/**
		 * Binding for DataConstructor: Cal.Utilities.RelativeTime.YearDiff.
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.RelativeTime.YearDiff
		 */
		public static final SourceModel.Expr YearDiff() {
			return SourceModel.Expr.DataCons.make(DataConstructors.YearDiff);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.RelativeTime.YearDiff.
		 * @see #YearDiff()
		 */
		public static final QualifiedName YearDiff = 
			QualifiedName.make(CAL_RelativeTime.MODULE_NAME, "YearDiff");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.RelativeTime.YearDiff.
		 * @see #YearDiff()
		 */
		public static final int YearDiff_ordinal = 0;

		/**
		 * Binding for DataConstructor: Cal.Utilities.RelativeTime.QuarterDiff.
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.RelativeTime.QuarterDiff
		 */
		public static final SourceModel.Expr QuarterDiff() {
			return SourceModel.Expr.DataCons.make(DataConstructors.QuarterDiff);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.RelativeTime.QuarterDiff.
		 * @see #QuarterDiff()
		 */
		public static final QualifiedName QuarterDiff = 
			QualifiedName.make(CAL_RelativeTime.MODULE_NAME, "QuarterDiff");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.RelativeTime.QuarterDiff.
		 * @see #QuarterDiff()
		 */
		public static final int QuarterDiff_ordinal = 1;

		/**
		 * Binding for DataConstructor: Cal.Utilities.RelativeTime.MonthDiff.
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.RelativeTime.MonthDiff
		 */
		public static final SourceModel.Expr MonthDiff() {
			return SourceModel.Expr.DataCons.make(DataConstructors.MonthDiff);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.RelativeTime.MonthDiff.
		 * @see #MonthDiff()
		 */
		public static final QualifiedName MonthDiff = 
			QualifiedName.make(CAL_RelativeTime.MODULE_NAME, "MonthDiff");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.RelativeTime.MonthDiff.
		 * @see #MonthDiff()
		 */
		public static final int MonthDiff_ordinal = 2;

		/**
		 * Binding for DataConstructor: Cal.Utilities.RelativeTime.DayDiff.
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.RelativeTime.DayDiff
		 */
		public static final SourceModel.Expr DayDiff() {
			return SourceModel.Expr.DataCons.make(DataConstructors.DayDiff);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.RelativeTime.DayDiff.
		 * @see #DayDiff()
		 */
		public static final QualifiedName DayDiff = 
			QualifiedName.make(CAL_RelativeTime.MODULE_NAME, "DayDiff");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.RelativeTime.DayDiff.
		 * @see #DayDiff()
		 */
		public static final int DayDiff_ordinal = 3;

		/**
		 * Binding for DataConstructor: Cal.Utilities.RelativeTime.WeekDiff.
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.RelativeTime.WeekDiff
		 */
		public static final SourceModel.Expr WeekDiff() {
			return SourceModel.Expr.DataCons.make(DataConstructors.WeekDiff);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.RelativeTime.WeekDiff.
		 * @see #WeekDiff()
		 */
		public static final QualifiedName WeekDiff = 
			QualifiedName.make(CAL_RelativeTime.MODULE_NAME, "WeekDiff");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.RelativeTime.WeekDiff.
		 * @see #WeekDiff()
		 */
		public static final int WeekDiff_ordinal = 4;

		/**
		 * Binding for DataConstructor: Cal.Utilities.RelativeTime.FirstDayOfWeekDiff.
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.RelativeTime.FirstDayOfWeekDiff
		 */
		public static final SourceModel.Expr FirstDayOfWeekDiff() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.FirstDayOfWeekDiff);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.RelativeTime.FirstDayOfWeekDiff.
		 * @see #FirstDayOfWeekDiff()
		 */
		public static final QualifiedName FirstDayOfWeekDiff = 
			QualifiedName.make(
				CAL_RelativeTime.MODULE_NAME, 
				"FirstDayOfWeekDiff");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.RelativeTime.FirstDayOfWeekDiff.
		 * @see #FirstDayOfWeekDiff()
		 */
		public static final int FirstDayOfWeekDiff_ordinal = 5;

		/**
		 * Binding for DataConstructor: Cal.Utilities.RelativeTime.HourDiff.
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.RelativeTime.HourDiff
		 */
		public static final SourceModel.Expr HourDiff() {
			return SourceModel.Expr.DataCons.make(DataConstructors.HourDiff);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.RelativeTime.HourDiff.
		 * @see #HourDiff()
		 */
		public static final QualifiedName HourDiff = 
			QualifiedName.make(CAL_RelativeTime.MODULE_NAME, "HourDiff");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.RelativeTime.HourDiff.
		 * @see #HourDiff()
		 */
		public static final int HourDiff_ordinal = 6;

		/**
		 * Binding for DataConstructor: Cal.Utilities.RelativeTime.MinuteDiff.
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.RelativeTime.MinuteDiff
		 */
		public static final SourceModel.Expr MinuteDiff() {
			return SourceModel.Expr.DataCons.make(DataConstructors.MinuteDiff);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.RelativeTime.MinuteDiff.
		 * @see #MinuteDiff()
		 */
		public static final QualifiedName MinuteDiff = 
			QualifiedName.make(CAL_RelativeTime.MODULE_NAME, "MinuteDiff");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.RelativeTime.MinuteDiff.
		 * @see #MinuteDiff()
		 */
		public static final int MinuteDiff_ordinal = 7;

		/**
		 * Binding for DataConstructor: Cal.Utilities.RelativeTime.SecondDiff.
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.RelativeTime.SecondDiff
		 */
		public static final SourceModel.Expr SecondDiff() {
			return SourceModel.Expr.DataCons.make(DataConstructors.SecondDiff);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.RelativeTime.SecondDiff.
		 * @see #SecondDiff()
		 */
		public static final QualifiedName SecondDiff = 
			QualifiedName.make(CAL_RelativeTime.MODULE_NAME, "SecondDiff");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.RelativeTime.SecondDiff.
		 * @see #SecondDiff()
		 */
		public static final int SecondDiff_ordinal = 8;

		/*
		 * DataConstructors for the Cal.Utilities.RelativeTime.DatePartType data type.
		 */

		/**
		 * Binding for DataConstructor: Cal.Utilities.RelativeTime.YearPart.
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.RelativeTime.YearPart
		 */
		public static final SourceModel.Expr YearPart() {
			return SourceModel.Expr.DataCons.make(DataConstructors.YearPart);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.RelativeTime.YearPart.
		 * @see #YearPart()
		 */
		public static final QualifiedName YearPart = 
			QualifiedName.make(CAL_RelativeTime.MODULE_NAME, "YearPart");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.RelativeTime.YearPart.
		 * @see #YearPart()
		 */
		public static final int YearPart_ordinal = 0;

		/**
		 * Binding for DataConstructor: Cal.Utilities.RelativeTime.QuarterPart.
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.RelativeTime.QuarterPart
		 */
		public static final SourceModel.Expr QuarterPart() {
			return SourceModel.Expr.DataCons.make(DataConstructors.QuarterPart);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.RelativeTime.QuarterPart.
		 * @see #QuarterPart()
		 */
		public static final QualifiedName QuarterPart = 
			QualifiedName.make(CAL_RelativeTime.MODULE_NAME, "QuarterPart");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.RelativeTime.QuarterPart.
		 * @see #QuarterPart()
		 */
		public static final int QuarterPart_ordinal = 1;

		/**
		 * Binding for DataConstructor: Cal.Utilities.RelativeTime.MonthPart.
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.RelativeTime.MonthPart
		 */
		public static final SourceModel.Expr MonthPart() {
			return SourceModel.Expr.DataCons.make(DataConstructors.MonthPart);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.RelativeTime.MonthPart.
		 * @see #MonthPart()
		 */
		public static final QualifiedName MonthPart = 
			QualifiedName.make(CAL_RelativeTime.MODULE_NAME, "MonthPart");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.RelativeTime.MonthPart.
		 * @see #MonthPart()
		 */
		public static final int MonthPart_ordinal = 2;

		/**
		 * Binding for DataConstructor: Cal.Utilities.RelativeTime.DayOfYearPart.
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.RelativeTime.DayOfYearPart
		 */
		public static final SourceModel.Expr DayOfYearPart() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.DayOfYearPart);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.RelativeTime.DayOfYearPart.
		 * @see #DayOfYearPart()
		 */
		public static final QualifiedName DayOfYearPart = 
			QualifiedName.make(CAL_RelativeTime.MODULE_NAME, "DayOfYearPart");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.RelativeTime.DayOfYearPart.
		 * @see #DayOfYearPart()
		 */
		public static final int DayOfYearPart_ordinal = 3;

		/**
		 * Binding for DataConstructor: Cal.Utilities.RelativeTime.DayPart.
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.RelativeTime.DayPart
		 */
		public static final SourceModel.Expr DayPart() {
			return SourceModel.Expr.DataCons.make(DataConstructors.DayPart);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.RelativeTime.DayPart.
		 * @see #DayPart()
		 */
		public static final QualifiedName DayPart = 
			QualifiedName.make(CAL_RelativeTime.MODULE_NAME, "DayPart");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.RelativeTime.DayPart.
		 * @see #DayPart()
		 */
		public static final int DayPart_ordinal = 4;

		/**
		 * Binding for DataConstructor: Cal.Utilities.RelativeTime.DayOfWeekPart.
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.RelativeTime.DayOfWeekPart
		 */
		public static final SourceModel.Expr DayOfWeekPart() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.DayOfWeekPart);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.RelativeTime.DayOfWeekPart.
		 * @see #DayOfWeekPart()
		 */
		public static final QualifiedName DayOfWeekPart = 
			QualifiedName.make(CAL_RelativeTime.MODULE_NAME, "DayOfWeekPart");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.RelativeTime.DayOfWeekPart.
		 * @see #DayOfWeekPart()
		 */
		public static final int DayOfWeekPart_ordinal = 5;

		/**
		 * Binding for DataConstructor: Cal.Utilities.RelativeTime.WeekOfYearPart.
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.RelativeTime.WeekOfYearPart
		 */
		public static final SourceModel.Expr WeekOfYearPart() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.WeekOfYearPart);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.RelativeTime.WeekOfYearPart.
		 * @see #WeekOfYearPart()
		 */
		public static final QualifiedName WeekOfYearPart = 
			QualifiedName.make(CAL_RelativeTime.MODULE_NAME, "WeekOfYearPart");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.RelativeTime.WeekOfYearPart.
		 * @see #WeekOfYearPart()
		 */
		public static final int WeekOfYearPart_ordinal = 6;

		/**
		 * Binding for DataConstructor: Cal.Utilities.RelativeTime.HourPart.
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.RelativeTime.HourPart
		 */
		public static final SourceModel.Expr HourPart() {
			return SourceModel.Expr.DataCons.make(DataConstructors.HourPart);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.RelativeTime.HourPart.
		 * @see #HourPart()
		 */
		public static final QualifiedName HourPart = 
			QualifiedName.make(CAL_RelativeTime.MODULE_NAME, "HourPart");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.RelativeTime.HourPart.
		 * @see #HourPart()
		 */
		public static final int HourPart_ordinal = 7;

		/**
		 * Binding for DataConstructor: Cal.Utilities.RelativeTime.MinutePart.
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.RelativeTime.MinutePart
		 */
		public static final SourceModel.Expr MinutePart() {
			return SourceModel.Expr.DataCons.make(DataConstructors.MinutePart);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.RelativeTime.MinutePart.
		 * @see #MinutePart()
		 */
		public static final QualifiedName MinutePart = 
			QualifiedName.make(CAL_RelativeTime.MODULE_NAME, "MinutePart");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.RelativeTime.MinutePart.
		 * @see #MinutePart()
		 */
		public static final int MinutePart_ordinal = 8;

		/**
		 * Binding for DataConstructor: Cal.Utilities.RelativeTime.SecondPart.
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.RelativeTime.SecondPart
		 */
		public static final SourceModel.Expr SecondPart() {
			return SourceModel.Expr.DataCons.make(DataConstructors.SecondPart);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.RelativeTime.SecondPart.
		 * @see #SecondPart()
		 */
		public static final QualifiedName SecondPart = 
			QualifiedName.make(CAL_RelativeTime.MODULE_NAME, "SecondPart");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.RelativeTime.SecondPart.
		 * @see #SecondPart()
		 */
		public static final int SecondPart_ordinal = 9;

		/*
		 * DataConstructors for the Cal.Utilities.RelativeTime.DayOfWeek data type.
		 */

		/**
		 * Binding for DataConstructor: Cal.Utilities.RelativeTime.Sunday.
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.RelativeTime.Sunday
		 */
		public static final SourceModel.Expr Sunday() {
			return SourceModel.Expr.DataCons.make(DataConstructors.Sunday);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.RelativeTime.Sunday.
		 * @see #Sunday()
		 */
		public static final QualifiedName Sunday = 
			QualifiedName.make(CAL_RelativeTime.MODULE_NAME, "Sunday");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.RelativeTime.Sunday.
		 * @see #Sunday()
		 */
		public static final int Sunday_ordinal = 0;

		/**
		 * Binding for DataConstructor: Cal.Utilities.RelativeTime.Monday.
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.RelativeTime.Monday
		 */
		public static final SourceModel.Expr Monday() {
			return SourceModel.Expr.DataCons.make(DataConstructors.Monday);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.RelativeTime.Monday.
		 * @see #Monday()
		 */
		public static final QualifiedName Monday = 
			QualifiedName.make(CAL_RelativeTime.MODULE_NAME, "Monday");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.RelativeTime.Monday.
		 * @see #Monday()
		 */
		public static final int Monday_ordinal = 1;

		/**
		 * Binding for DataConstructor: Cal.Utilities.RelativeTime.Tuesday.
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.RelativeTime.Tuesday
		 */
		public static final SourceModel.Expr Tuesday() {
			return SourceModel.Expr.DataCons.make(DataConstructors.Tuesday);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.RelativeTime.Tuesday.
		 * @see #Tuesday()
		 */
		public static final QualifiedName Tuesday = 
			QualifiedName.make(CAL_RelativeTime.MODULE_NAME, "Tuesday");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.RelativeTime.Tuesday.
		 * @see #Tuesday()
		 */
		public static final int Tuesday_ordinal = 2;

		/**
		 * Binding for DataConstructor: Cal.Utilities.RelativeTime.Wednesday.
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.RelativeTime.Wednesday
		 */
		public static final SourceModel.Expr Wednesday() {
			return SourceModel.Expr.DataCons.make(DataConstructors.Wednesday);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.RelativeTime.Wednesday.
		 * @see #Wednesday()
		 */
		public static final QualifiedName Wednesday = 
			QualifiedName.make(CAL_RelativeTime.MODULE_NAME, "Wednesday");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.RelativeTime.Wednesday.
		 * @see #Wednesday()
		 */
		public static final int Wednesday_ordinal = 3;

		/**
		 * Binding for DataConstructor: Cal.Utilities.RelativeTime.Thursday.
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.RelativeTime.Thursday
		 */
		public static final SourceModel.Expr Thursday() {
			return SourceModel.Expr.DataCons.make(DataConstructors.Thursday);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.RelativeTime.Thursday.
		 * @see #Thursday()
		 */
		public static final QualifiedName Thursday = 
			QualifiedName.make(CAL_RelativeTime.MODULE_NAME, "Thursday");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.RelativeTime.Thursday.
		 * @see #Thursday()
		 */
		public static final int Thursday_ordinal = 4;

		/**
		 * Binding for DataConstructor: Cal.Utilities.RelativeTime.Friday.
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.RelativeTime.Friday
		 */
		public static final SourceModel.Expr Friday() {
			return SourceModel.Expr.DataCons.make(DataConstructors.Friday);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.RelativeTime.Friday.
		 * @see #Friday()
		 */
		public static final QualifiedName Friday = 
			QualifiedName.make(CAL_RelativeTime.MODULE_NAME, "Friday");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.RelativeTime.Friday.
		 * @see #Friday()
		 */
		public static final int Friday_ordinal = 5;

		/**
		 * Binding for DataConstructor: Cal.Utilities.RelativeTime.Saturday.
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.RelativeTime.Saturday
		 */
		public static final SourceModel.Expr Saturday() {
			return SourceModel.Expr.DataCons.make(DataConstructors.Saturday);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.RelativeTime.Saturday.
		 * @see #Saturday()
		 */
		public static final QualifiedName Saturday = 
			QualifiedName.make(CAL_RelativeTime.MODULE_NAME, "Saturday");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.RelativeTime.Saturday.
		 * @see #Saturday()
		 */
		public static final int Saturday_ordinal = 6;

		/*
		 * DataConstructors for the Cal.Utilities.RelativeTime.FirstWeekOfYear data type.
		 */

		/**
		 * Binding for DataConstructor: Cal.Utilities.RelativeTime.WeekInWhichJan1Occurs.
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.RelativeTime.WeekInWhichJan1Occurs
		 */
		public static final SourceModel.Expr WeekInWhichJan1Occurs() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.WeekInWhichJan1Occurs);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.RelativeTime.WeekInWhichJan1Occurs.
		 * @see #WeekInWhichJan1Occurs()
		 */
		public static final QualifiedName WeekInWhichJan1Occurs = 
			QualifiedName.make(
				CAL_RelativeTime.MODULE_NAME, 
				"WeekInWhichJan1Occurs");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.RelativeTime.WeekInWhichJan1Occurs.
		 * @see #WeekInWhichJan1Occurs()
		 */
		public static final int WeekInWhichJan1Occurs_ordinal = 0;

		/**
		 * Binding for DataConstructor: Cal.Utilities.RelativeTime.FirstWeekWithAtLeast4DaysInNewYear.
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.RelativeTime.FirstWeekWithAtLeast4DaysInNewYear
		 */
		public static final SourceModel.Expr FirstWeekWithAtLeast4DaysInNewYear() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.FirstWeekWithAtLeast4DaysInNewYear);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.RelativeTime.FirstWeekWithAtLeast4DaysInNewYear.
		 * @see #FirstWeekWithAtLeast4DaysInNewYear()
		 */
		public static final QualifiedName FirstWeekWithAtLeast4DaysInNewYear = 
			QualifiedName.make(
				CAL_RelativeTime.MODULE_NAME, 
				"FirstWeekWithAtLeast4DaysInNewYear");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.RelativeTime.FirstWeekWithAtLeast4DaysInNewYear.
		 * @see #FirstWeekWithAtLeast4DaysInNewYear()
		 */
		public static final int FirstWeekWithAtLeast4DaysInNewYear_ordinal = 1;

		/**
		 * Binding for DataConstructor: Cal.Utilities.RelativeTime.FirstWeekFullyInNewYear.
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.RelativeTime.FirstWeekFullyInNewYear
		 */
		public static final SourceModel.Expr FirstWeekFullyInNewYear() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.FirstWeekFullyInNewYear);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.RelativeTime.FirstWeekFullyInNewYear.
		 * @see #FirstWeekFullyInNewYear()
		 */
		public static final QualifiedName FirstWeekFullyInNewYear = 
			QualifiedName.make(
				CAL_RelativeTime.MODULE_NAME, 
				"FirstWeekFullyInNewYear");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.RelativeTime.FirstWeekFullyInNewYear.
		 * @see #FirstWeekFullyInNewYear()
		 */
		public static final int FirstWeekFullyInNewYear_ordinal = 2;

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Utilities.RelativeTime module.
	 */
	public static final class Functions {
		/**
		 * <code>Cal.Utilities.RelativeTime.dateAdd</code> is used to add intervals of time to a <code>Cal.Utilities.RelativeTime.RelativeDateTime</code> value. Its main feature
		 * is that the date-time returned will always be valid. For example, <code>Cal.Utilities.RelativeTime.dateAdd</code> takes into account such factors as
		 * the number of days in a month and leap years.
		 * @param intervalType (CAL type: <code>Cal.Utilities.RelativeTime.DateAddType</code>)
		 * @param nIntervals (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the number of intervals to be added. It can be positive (to get date-times in the future) or negative
		 * (to get date-times in the past).
		 * @param dateTime (CAL type: <code>Cal.Utilities.RelativeTime.RelativeDateTime</code>)
		 *          the date-time value to which the intervals are to be added
		 * @return (CAL type: <code>Cal.Utilities.RelativeTime.RelativeDateTime</code>) 
		 *          a date-time value to which a specified number of time intervals have been added.
		 */
		public static final SourceModel.Expr dateAdd(SourceModel.Expr intervalType, SourceModel.Expr nIntervals, SourceModel.Expr dateTime) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.dateAdd), intervalType, nIntervals, dateTime});
		}

		/**
		 * @see #dateAdd(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param intervalType
		 * @param nIntervals
		 * @param dateTime
		 * @return the SourceModel.Expr representing an application of dateAdd
		 */
		public static final SourceModel.Expr dateAdd(SourceModel.Expr intervalType, int nIntervals, SourceModel.Expr dateTime) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.dateAdd), intervalType, SourceModel.Expr.makeIntValue(nIntervals), dateTime});
		}

		/**
		 * Name binding for function: dateAdd.
		 * @see #dateAdd(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName dateAdd = 
			QualifiedName.make(CAL_RelativeTime.MODULE_NAME, "dateAdd");

		/**
		 * 
		 * @param dateDiffType (CAL type: <code>Cal.Utilities.RelativeTime.DateDiffType</code>)
		 * @param dateTime1 (CAL type: <code>Cal.Utilities.RelativeTime.RelativeDateTime</code>)
		 * @param dateTime2 (CAL type: <code>Cal.Utilities.RelativeTime.RelativeDateTime</code>)
		 * @param maybeFirstDayOfWeek (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Utilities.RelativeTime.DayOfWeek</code>)
		 *          if Nothing, then <code>Cal.Utilities.RelativeTime.Sunday</code> is assumed.
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 */
		public static final SourceModel.Expr dateDiff(SourceModel.Expr dateDiffType, SourceModel.Expr dateTime1, SourceModel.Expr dateTime2, SourceModel.Expr maybeFirstDayOfWeek) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.dateDiff), dateDiffType, dateTime1, dateTime2, maybeFirstDayOfWeek});
		}

		/**
		 * Name binding for function: dateDiff.
		 * @see #dateDiff(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName dateDiff = 
			QualifiedName.make(CAL_RelativeTime.MODULE_NAME, "dateDiff");

		/**
		 * Extracts a specified component of a date. For example, this function can be used to
		 * determine what week of the year a given date is.
		 * @param datePartType (CAL type: <code>Cal.Utilities.RelativeTime.DatePartType</code>)
		 * @param dateTime (CAL type: <code>Cal.Utilities.RelativeTime.RelativeDateTime</code>)
		 * @param maybeFirstDayOfWeek (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Utilities.RelativeTime.DayOfWeek</code>)
		 *          if <code>Cal.Core.Prelude.Nothing</code> then <code>Cal.Utilities.RelativeTime.Sunday</code> is used as a default.
		 * @param maybeFirstWeekOfYear (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Utilities.RelativeTime.FirstWeekOfYear</code>)
		 *          if <code>Cal.Core.Prelude.Nothing</code> then <code>Cal.Utilities.RelativeTime.WeekInWhichJan1Occurs</code> is used as a default.
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 */
		public static final SourceModel.Expr datePart(SourceModel.Expr datePartType, SourceModel.Expr dateTime, SourceModel.Expr maybeFirstDayOfWeek, SourceModel.Expr maybeFirstWeekOfYear) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.datePart), datePartType, dateTime, maybeFirstDayOfWeek, maybeFirstWeekOfYear});
		}

		/**
		 * Name binding for function: datePart.
		 * @see #datePart(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName datePart = 
			QualifiedName.make(CAL_RelativeTime.MODULE_NAME, "datePart");

		/**
		 * Helper binding method for function: dateToYearMonthDay. 
		 * @param date
		 * @return the SourceModule.expr representing an application of dateToYearMonthDay
		 */
		public static final SourceModel.Expr dateToYearMonthDay(SourceModel.Expr date) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.dateToYearMonthDay), date});
		}

		/**
		 * @see #dateToYearMonthDay(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param date
		 * @return the SourceModel.Expr representing an application of dateToYearMonthDay
		 */
		public static final SourceModel.Expr dateToYearMonthDay(int date) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.dateToYearMonthDay), SourceModel.Expr.makeIntValue(date)});
		}

		/**
		 * Name binding for function: dateToYearMonthDay.
		 * @see #dateToYearMonthDay(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName dateToYearMonthDay = 
			QualifiedName.make(
				CAL_RelativeTime.MODULE_NAME, 
				"dateToYearMonthDay");

		/**
		 * Helper binding method for function: day. 
		 * @param date
		 * @return the SourceModule.expr representing an application of day
		 */
		public static final SourceModel.Expr day(SourceModel.Expr date) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.day), date});
		}

		/**
		 * @see #day(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param date
		 * @return the SourceModel.Expr representing an application of day
		 */
		public static final SourceModel.Expr day(int date) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.day), SourceModel.Expr.makeIntValue(date)});
		}

		/**
		 * Name binding for function: day.
		 * @see #day(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName day = 
			QualifiedName.make(CAL_RelativeTime.MODULE_NAME, "day");

		/**
		 * 
		 * @param date (CAL type: <code>Cal.Utilities.RelativeTime.RelativeDate</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the day of week that a date occurs on with Sunday = 1, Monday = 2, ..., Saturday = 7.
		 */
		public static final SourceModel.Expr dayOfWeek(SourceModel.Expr date) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.dayOfWeek), date});
		}

		/**
		 * @see #dayOfWeek(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param date
		 * @return the SourceModel.Expr representing an application of dayOfWeek
		 */
		public static final SourceModel.Expr dayOfWeek(int date) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.dayOfWeek), SourceModel.Expr.makeIntValue(date)});
		}

		/**
		 * Name binding for function: dayOfWeek.
		 * @see #dayOfWeek(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName dayOfWeek = 
			QualifiedName.make(CAL_RelativeTime.MODULE_NAME, "dayOfWeek");

		/**
		 * Helper binding method for function: getDatePart. 
		 * @param dateTime
		 * @return the SourceModule.expr representing an application of getDatePart
		 */
		public static final SourceModel.Expr getDatePart(SourceModel.Expr dateTime) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getDatePart), dateTime});
		}

		/**
		 * Name binding for function: getDatePart.
		 * @see #getDatePart(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getDatePart = 
			QualifiedName.make(CAL_RelativeTime.MODULE_NAME, "getDatePart");

		/**
		 * Helper binding method for function: getTimePart. 
		 * @param dateTime
		 * @return the SourceModule.expr representing an application of getTimePart
		 */
		public static final SourceModel.Expr getTimePart(SourceModel.Expr dateTime) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getTimePart), dateTime});
		}

		/**
		 * Name binding for function: getTimePart.
		 * @see #getTimePart(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getTimePart = 
			QualifiedName.make(CAL_RelativeTime.MODULE_NAME, "getTimePart");

		/**
		 * Helper binding method for function: hour. 
		 * @param time
		 * @return the SourceModule.expr representing an application of hour
		 */
		public static final SourceModel.Expr hour(SourceModel.Expr time) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.hour), time});
		}

		/**
		 * @see #hour(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param time
		 * @return the SourceModel.Expr representing an application of hour
		 */
		public static final SourceModel.Expr hour(int time) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.hour), SourceModel.Expr.makeIntValue(time)});
		}

		/**
		 * Name binding for function: hour.
		 * @see #hour(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName hour = 
			QualifiedName.make(CAL_RelativeTime.MODULE_NAME, "hour");

		/**
		 * Helper binding method for function: jDateToRelativeDate. 
		 * @param date
		 * @return the SourceModule.expr representing an application of jDateToRelativeDate
		 */
		public static final SourceModel.Expr jDateToRelativeDate(SourceModel.Expr date) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jDateToRelativeDate), date});
		}

		/**
		 * Name binding for function: jDateToRelativeDate.
		 * @see #jDateToRelativeDate(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jDateToRelativeDate = 
			QualifiedName.make(
				CAL_RelativeTime.MODULE_NAME, 
				"jDateToRelativeDate");

		/**
		 * Helper binding method for function: jDateToRelativeDateTime. 
		 * @param jdate
		 * @return the SourceModule.expr representing an application of jDateToRelativeDateTime
		 */
		public static final SourceModel.Expr jDateToRelativeDateTime(SourceModel.Expr jdate) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jDateToRelativeDateTime), jdate});
		}

		/**
		 * Name binding for function: jDateToRelativeDateTime.
		 * @see #jDateToRelativeDateTime(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jDateToRelativeDateTime = 
			QualifiedName.make(
				CAL_RelativeTime.MODULE_NAME, 
				"jDateToRelativeDateTime");

		/**
		 * Helper binding method for function: makeRelativeDateTimeValue. 
		 * @param year
		 * @param month
		 * @param day
		 * @param hour
		 * @param min
		 * @param sec
		 * @return the SourceModule.expr representing an application of makeRelativeDateTimeValue
		 */
		public static final SourceModel.Expr makeRelativeDateTimeValue(SourceModel.Expr year, SourceModel.Expr month, SourceModel.Expr day, SourceModel.Expr hour, SourceModel.Expr min, SourceModel.Expr sec) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeRelativeDateTimeValue), year, month, day, hour, min, sec});
		}

		/**
		 * @see #makeRelativeDateTimeValue(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param year
		 * @param month
		 * @param day
		 * @param hour
		 * @param min
		 * @param sec
		 * @return the SourceModel.Expr representing an application of makeRelativeDateTimeValue
		 */
		public static final SourceModel.Expr makeRelativeDateTimeValue(int year, int month, int day, int hour, int min, int sec) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeRelativeDateTimeValue), SourceModel.Expr.makeIntValue(year), SourceModel.Expr.makeIntValue(month), SourceModel.Expr.makeIntValue(day), SourceModel.Expr.makeIntValue(hour), SourceModel.Expr.makeIntValue(min), SourceModel.Expr.makeIntValue(sec)});
		}

		/**
		 * Name binding for function: makeRelativeDateTimeValue.
		 * @see #makeRelativeDateTimeValue(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeRelativeDateTimeValue = 
			QualifiedName.make(
				CAL_RelativeTime.MODULE_NAME, 
				"makeRelativeDateTimeValue");

		/**
		 * Helper binding method for function: makeRelativeDateTimeValue2. 
		 * @param year
		 * @param month
		 * @param day
		 * @return the SourceModule.expr representing an application of makeRelativeDateTimeValue2
		 */
		public static final SourceModel.Expr makeRelativeDateTimeValue2(SourceModel.Expr year, SourceModel.Expr month, SourceModel.Expr day) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeRelativeDateTimeValue2), year, month, day});
		}

		/**
		 * @see #makeRelativeDateTimeValue2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param year
		 * @param month
		 * @param day
		 * @return the SourceModel.Expr representing an application of makeRelativeDateTimeValue2
		 */
		public static final SourceModel.Expr makeRelativeDateTimeValue2(int year, int month, int day) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeRelativeDateTimeValue2), SourceModel.Expr.makeIntValue(year), SourceModel.Expr.makeIntValue(month), SourceModel.Expr.makeIntValue(day)});
		}

		/**
		 * Name binding for function: makeRelativeDateTimeValue2.
		 * @see #makeRelativeDateTimeValue2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeRelativeDateTimeValue2 = 
			QualifiedName.make(
				CAL_RelativeTime.MODULE_NAME, 
				"makeRelativeDateTimeValue2");

		/**
		 * Helper binding method for function: makeRelativeDateValue. 
		 * @param year
		 * @param month
		 * @param day
		 * @return the SourceModule.expr representing an application of makeRelativeDateValue
		 */
		public static final SourceModel.Expr makeRelativeDateValue(SourceModel.Expr year, SourceModel.Expr month, SourceModel.Expr day) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeRelativeDateValue), year, month, day});
		}

		/**
		 * @see #makeRelativeDateValue(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param year
		 * @param month
		 * @param day
		 * @return the SourceModel.Expr representing an application of makeRelativeDateValue
		 */
		public static final SourceModel.Expr makeRelativeDateValue(int year, int month, int day) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeRelativeDateValue), SourceModel.Expr.makeIntValue(year), SourceModel.Expr.makeIntValue(month), SourceModel.Expr.makeIntValue(day)});
		}

		/**
		 * Name binding for function: makeRelativeDateValue.
		 * @see #makeRelativeDateValue(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeRelativeDateValue = 
			QualifiedName.make(
				CAL_RelativeTime.MODULE_NAME, 
				"makeRelativeDateValue");

		/**
		 * Helper binding method for function: makeRelativeTimeValue. 
		 * @param hour
		 * @param min
		 * @param sec
		 * @return the SourceModule.expr representing an application of makeRelativeTimeValue
		 */
		public static final SourceModel.Expr makeRelativeTimeValue(SourceModel.Expr hour, SourceModel.Expr min, SourceModel.Expr sec) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeRelativeTimeValue), hour, min, sec});
		}

		/**
		 * @see #makeRelativeTimeValue(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param hour
		 * @param min
		 * @param sec
		 * @return the SourceModel.Expr representing an application of makeRelativeTimeValue
		 */
		public static final SourceModel.Expr makeRelativeTimeValue(int hour, int min, int sec) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeRelativeTimeValue), SourceModel.Expr.makeIntValue(hour), SourceModel.Expr.makeIntValue(min), SourceModel.Expr.makeIntValue(sec)});
		}

		/**
		 * Name binding for function: makeRelativeTimeValue.
		 * @see #makeRelativeTimeValue(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeRelativeTimeValue = 
			QualifiedName.make(
				CAL_RelativeTime.MODULE_NAME, 
				"makeRelativeTimeValue");

		/**
		 * Helper binding method for function: minute. 
		 * @param time
		 * @return the SourceModule.expr representing an application of minute
		 */
		public static final SourceModel.Expr minute(SourceModel.Expr time) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.minute), time});
		}

		/**
		 * @see #minute(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param time
		 * @return the SourceModel.Expr representing an application of minute
		 */
		public static final SourceModel.Expr minute(int time) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.minute), SourceModel.Expr.makeIntValue(time)});
		}

		/**
		 * Name binding for function: minute.
		 * @see #minute(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName minute = 
			QualifiedName.make(CAL_RelativeTime.MODULE_NAME, "minute");

		/**
		 * Helper binding method for function: month. 
		 * @param date
		 * @return the SourceModule.expr representing an application of month
		 */
		public static final SourceModel.Expr month(SourceModel.Expr date) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.month), date});
		}

		/**
		 * @see #month(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param date
		 * @return the SourceModel.Expr representing an application of month
		 */
		public static final SourceModel.Expr month(int date) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.month), SourceModel.Expr.makeIntValue(date)});
		}

		/**
		 * Name binding for function: month.
		 * @see #month(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName month = 
			QualifiedName.make(CAL_RelativeTime.MODULE_NAME, "month");

		/**
		 * Helper binding method for function: relativeDateTimeToJDate. 
		 * @param dateTime
		 * @return the SourceModule.expr representing an application of relativeDateTimeToJDate
		 */
		public static final SourceModel.Expr relativeDateTimeToJDate(SourceModel.Expr dateTime) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.relativeDateTimeToJDate), dateTime});
		}

		/**
		 * Name binding for function: relativeDateTimeToJDate.
		 * @see #relativeDateTimeToJDate(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName relativeDateTimeToJDate = 
			QualifiedName.make(
				CAL_RelativeTime.MODULE_NAME, 
				"relativeDateTimeToJDate");

		/**
		 * Helper binding method for function: relativeDateToJDate. 
		 * @param date
		 * @return the SourceModule.expr representing an application of relativeDateToJDate
		 */
		public static final SourceModel.Expr relativeDateToJDate(SourceModel.Expr date) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.relativeDateToJDate), date});
		}

		/**
		 * @see #relativeDateToJDate(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param date
		 * @return the SourceModel.Expr representing an application of relativeDateToJDate
		 */
		public static final SourceModel.Expr relativeDateToJDate(int date) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.relativeDateToJDate), SourceModel.Expr.makeIntValue(date)});
		}

		/**
		 * Name binding for function: relativeDateToJDate.
		 * @see #relativeDateToJDate(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName relativeDateToJDate = 
			QualifiedName.make(
				CAL_RelativeTime.MODULE_NAME, 
				"relativeDateToJDate");

		/**
		 * Helper binding method for function: second. 
		 * @param time
		 * @return the SourceModule.expr representing an application of second
		 */
		public static final SourceModel.Expr second(SourceModel.Expr time) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.second), time});
		}

		/**
		 * @see #second(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param time
		 * @return the SourceModel.Expr representing an application of second
		 */
		public static final SourceModel.Expr second(int time) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.second), SourceModel.Expr.makeIntValue(time)});
		}

		/**
		 * Name binding for function: second.
		 * @see #second(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName second = 
			QualifiedName.make(CAL_RelativeTime.MODULE_NAME, "second");

		/**
		 * Predicate function for verifying basic functionality of the RelativeTime module.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr testModule() {
			return SourceModel.Expr.Var.make(Functions.testModule);
		}

		/**
		 * Name binding for function: testModule.
		 * @see #testModule()
		 */
		public static final QualifiedName testModule = 
			QualifiedName.make(CAL_RelativeTime.MODULE_NAME, "testModule");

		/**
		 * Helper binding method for function: timeToHourMinuteSecond. 
		 * @param time
		 * @return the SourceModule.expr representing an application of timeToHourMinuteSecond
		 */
		public static final SourceModel.Expr timeToHourMinuteSecond(SourceModel.Expr time) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.timeToHourMinuteSecond), time});
		}

		/**
		 * @see #timeToHourMinuteSecond(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param time
		 * @return the SourceModel.Expr representing an application of timeToHourMinuteSecond
		 */
		public static final SourceModel.Expr timeToHourMinuteSecond(int time) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.timeToHourMinuteSecond), SourceModel.Expr.makeIntValue(time)});
		}

		/**
		 * Name binding for function: timeToHourMinuteSecond.
		 * @see #timeToHourMinuteSecond(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName timeToHourMinuteSecond = 
			QualifiedName.make(
				CAL_RelativeTime.MODULE_NAME, 
				"timeToHourMinuteSecond");

		/**
		 * Helper binding method for function: toDateTime. 
		 * @param date
		 * @return the SourceModule.expr representing an application of toDateTime
		 */
		public static final SourceModel.Expr toDateTime(SourceModel.Expr date) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toDateTime), date});
		}

		/**
		 * @see #toDateTime(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param date
		 * @return the SourceModel.Expr representing an application of toDateTime
		 */
		public static final SourceModel.Expr toDateTime(int date) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toDateTime), SourceModel.Expr.makeIntValue(date)});
		}

		/**
		 * Name binding for function: toDateTime.
		 * @see #toDateTime(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName toDateTime = 
			QualifiedName.make(CAL_RelativeTime.MODULE_NAME, "toDateTime");

		/**
		 * Helper binding method for function: year. 
		 * @param date
		 * @return the SourceModule.expr representing an application of year
		 */
		public static final SourceModel.Expr year(SourceModel.Expr date) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.year), date});
		}

		/**
		 * @see #year(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param date
		 * @return the SourceModel.Expr representing an application of year
		 */
		public static final SourceModel.Expr year(int date) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.year), SourceModel.Expr.makeIntValue(date)});
		}

		/**
		 * Name binding for function: year.
		 * @see #year(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName year = 
			QualifiedName.make(CAL_RelativeTime.MODULE_NAME, "year");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = 738712575;

}
