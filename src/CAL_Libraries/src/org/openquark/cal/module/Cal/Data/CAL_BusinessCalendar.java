/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_BusinessCalendar.java)
 * was generated from CAL module: Cal.Data.BusinessCalendar.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Data.BusinessCalendar module from Java code.
 *  
 * Creation date: Tue Aug 28 15:58:42 PDT 2007
 * --!>
 *  
 */

package org.openquark.cal.module.Cal.Data;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * This module contains the <code>Cal.Data.BusinessCalendar.BusinessCalendar</code> type
 * which provides information about valid period values, display names, etc...
 * for period types (Year, Quarter, Month, etc...) in the calendar. There are
 * also functions to build database expressions for extracting period values
 * (relative to the calendar) from a database date/time field.
 * <p>
 * Actual implementations of BusinessCalendars can be found in <code>Cal.Data.GregorianBusinessCalendar</code> and <code>Cal.Data.FiscalBusinessCalendar</code>. Other implementations are possible as
 * well.
 * 
 * @author Richard Webster
 */
public final class CAL_BusinessCalendar {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Data.BusinessCalendar");

	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Data.BusinessCalendar module.
	 */
	public static final class TypeConstructors {
		/**
		 * A business calendar provides information about valid period values, display names, etc... 
		 * for period types (Year, Quarter, Month, etc...) in the calendar.
		 * The business calendar also contains logic to build database expressions for extracting
		 * period values (relative to the calendar) from a database date/time field.
		 */
		public static final QualifiedName BusinessCalendar = 
			QualifiedName.make(
				CAL_BusinessCalendar.MODULE_NAME, 
				"BusinessCalendar");

		/**
		 * A <code>Cal.Data.BusinessCalendar.Period</code> is defined by one or more nested
		 * <code>Cal.Data.BusinessCalendar.PeriodType</code>-value pairs.
		 * The pairs are assumed to be in nesting order, with the outermost
		 * <code>Cal.Data.BusinessCalendar.PeriodType</code> at the beginning of the list.
		 * <p>
		 * TODO: most of the operations seem to access the end of this list so it might
		 * be better to store it in the opposite order.
		 */
		public static final QualifiedName Period = 
			QualifiedName.make(CAL_BusinessCalendar.MODULE_NAME, "Period");

		/**
		 * An enumeration of the various types of time periods recognized by business
		 * calendars.
		 */
		public static final QualifiedName PeriodType = 
			QualifiedName.make(CAL_BusinessCalendar.MODULE_NAME, "PeriodType");

	}
	/**
	 * This inner class (DataConstructors) contains constants
	 * and methods related to binding to CAL DataConstructors in the Cal.Data.BusinessCalendar module.
	 */
	public static final class DataConstructors {
		/*
		 * DataConstructors for the Cal.Data.BusinessCalendar.PeriodType data type.
		 */

		/**
		 * Binding for DataConstructor: Cal.Data.BusinessCalendar.NoPeriod.
		 * @return the SourceModule.Expr representing an application of Cal.Data.BusinessCalendar.NoPeriod
		 */
		public static final SourceModel.Expr NoPeriod() {
			return SourceModel.Expr.DataCons.make(DataConstructors.NoPeriod);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.BusinessCalendar.NoPeriod.
		 * @see #NoPeriod()
		 */
		public static final QualifiedName NoPeriod = 
			QualifiedName.make(CAL_BusinessCalendar.MODULE_NAME, "NoPeriod");

		/**
		 * Ordinal of DataConstructor Cal.Data.BusinessCalendar.NoPeriod.
		 * @see #NoPeriod()
		 */
		public static final int NoPeriod_ordinal = 0;

		/**
		 * Binding for DataConstructor: Cal.Data.BusinessCalendar.YearPeriod.
		 * @return the SourceModule.Expr representing an application of Cal.Data.BusinessCalendar.YearPeriod
		 */
		public static final SourceModel.Expr YearPeriod() {
			return SourceModel.Expr.DataCons.make(DataConstructors.YearPeriod);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.BusinessCalendar.YearPeriod.
		 * @see #YearPeriod()
		 */
		public static final QualifiedName YearPeriod = 
			QualifiedName.make(CAL_BusinessCalendar.MODULE_NAME, "YearPeriod");

		/**
		 * Ordinal of DataConstructor Cal.Data.BusinessCalendar.YearPeriod.
		 * @see #YearPeriod()
		 */
		public static final int YearPeriod_ordinal = 1;

		/**
		 * Binding for DataConstructor: Cal.Data.BusinessCalendar.QuarterPeriod.
		 * @return the SourceModule.Expr representing an application of Cal.Data.BusinessCalendar.QuarterPeriod
		 */
		public static final SourceModel.Expr QuarterPeriod() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.QuarterPeriod);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.BusinessCalendar.QuarterPeriod.
		 * @see #QuarterPeriod()
		 */
		public static final QualifiedName QuarterPeriod = 
			QualifiedName.make(
				CAL_BusinessCalendar.MODULE_NAME, 
				"QuarterPeriod");

		/**
		 * Ordinal of DataConstructor Cal.Data.BusinessCalendar.QuarterPeriod.
		 * @see #QuarterPeriod()
		 */
		public static final int QuarterPeriod_ordinal = 2;

		/**
		 * Binding for DataConstructor: Cal.Data.BusinessCalendar.MonthPeriod.
		 * @return the SourceModule.Expr representing an application of Cal.Data.BusinessCalendar.MonthPeriod
		 */
		public static final SourceModel.Expr MonthPeriod() {
			return SourceModel.Expr.DataCons.make(DataConstructors.MonthPeriod);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.BusinessCalendar.MonthPeriod.
		 * @see #MonthPeriod()
		 */
		public static final QualifiedName MonthPeriod = 
			QualifiedName.make(CAL_BusinessCalendar.MODULE_NAME, "MonthPeriod");

		/**
		 * Ordinal of DataConstructor Cal.Data.BusinessCalendar.MonthPeriod.
		 * @see #MonthPeriod()
		 */
		public static final int MonthPeriod_ordinal = 3;

		/**
		 * Binding for DataConstructor: Cal.Data.BusinessCalendar.WeekOfYearPeriod.
		 * @return the SourceModule.Expr representing an application of Cal.Data.BusinessCalendar.WeekOfYearPeriod
		 */
		public static final SourceModel.Expr WeekOfYearPeriod() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.WeekOfYearPeriod);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.BusinessCalendar.WeekOfYearPeriod.
		 * @see #WeekOfYearPeriod()
		 */
		public static final QualifiedName WeekOfYearPeriod = 
			QualifiedName.make(
				CAL_BusinessCalendar.MODULE_NAME, 
				"WeekOfYearPeriod");

		/**
		 * Ordinal of DataConstructor Cal.Data.BusinessCalendar.WeekOfYearPeriod.
		 * @see #WeekOfYearPeriod()
		 */
		public static final int WeekOfYearPeriod_ordinal = 4;

		/**
		 * Binding for DataConstructor: Cal.Data.BusinessCalendar.DayOfYearPeriod.
		 * @return the SourceModule.Expr representing an application of Cal.Data.BusinessCalendar.DayOfYearPeriod
		 */
		public static final SourceModel.Expr DayOfYearPeriod() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.DayOfYearPeriod);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.BusinessCalendar.DayOfYearPeriod.
		 * @see #DayOfYearPeriod()
		 */
		public static final QualifiedName DayOfYearPeriod = 
			QualifiedName.make(
				CAL_BusinessCalendar.MODULE_NAME, 
				"DayOfYearPeriod");

		/**
		 * Ordinal of DataConstructor Cal.Data.BusinessCalendar.DayOfYearPeriod.
		 * @see #DayOfYearPeriod()
		 */
		public static final int DayOfYearPeriod_ordinal = 5;

		/**
		 * Binding for DataConstructor: Cal.Data.BusinessCalendar.DayOfMonthPeriod.
		 * @return the SourceModule.Expr representing an application of Cal.Data.BusinessCalendar.DayOfMonthPeriod
		 */
		public static final SourceModel.Expr DayOfMonthPeriod() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.DayOfMonthPeriod);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.BusinessCalendar.DayOfMonthPeriod.
		 * @see #DayOfMonthPeriod()
		 */
		public static final QualifiedName DayOfMonthPeriod = 
			QualifiedName.make(
				CAL_BusinessCalendar.MODULE_NAME, 
				"DayOfMonthPeriod");

		/**
		 * Ordinal of DataConstructor Cal.Data.BusinessCalendar.DayOfMonthPeriod.
		 * @see #DayOfMonthPeriod()
		 */
		public static final int DayOfMonthPeriod_ordinal = 6;

		/**
		 * Binding for DataConstructor: Cal.Data.BusinessCalendar.DayOfWeekPeriod.
		 * @return the SourceModule.Expr representing an application of Cal.Data.BusinessCalendar.DayOfWeekPeriod
		 */
		public static final SourceModel.Expr DayOfWeekPeriod() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.DayOfWeekPeriod);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.BusinessCalendar.DayOfWeekPeriod.
		 * @see #DayOfWeekPeriod()
		 */
		public static final QualifiedName DayOfWeekPeriod = 
			QualifiedName.make(
				CAL_BusinessCalendar.MODULE_NAME, 
				"DayOfWeekPeriod");

		/**
		 * Ordinal of DataConstructor Cal.Data.BusinessCalendar.DayOfWeekPeriod.
		 * @see #DayOfWeekPeriod()
		 */
		public static final int DayOfWeekPeriod_ordinal = 7;

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Data.BusinessCalendar module.
	 */
	public static final class Functions {
		/**
		 * Returns whether the specified child <code>Cal.Data.BusinessCalendar.PeriodType</code> can
		 * roll up into the parent <code>Cal.Data.BusinessCalendar.PeriodType</code>.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Data.BusinessCalendar.getValidParentTimePeriods
		 * </dl>
		 * 
		 * @param childPeriod (CAL type: <code>Cal.Data.BusinessCalendar.PeriodType</code>)
		 * @param parentPeriod (CAL type: <code>Cal.Data.BusinessCalendar.PeriodType</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr canRollUpTimePeriod(SourceModel.Expr childPeriod, SourceModel.Expr parentPeriod) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.canRollUpTimePeriod), childPeriod, parentPeriod});
		}

		/**
		 * Name binding for function: canRollUpTimePeriod.
		 * @see #canRollUpTimePeriod(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName canRollUpTimePeriod = 
			QualifiedName.make(
				CAL_BusinessCalendar.MODULE_NAME, 
				"canRollUpTimePeriod");

		/**
		 * Returns the lowest level <code>Cal.Data.BusinessCalendar.PeriodType</code> in the
		 * <code>Cal.Data.BusinessCalendar.Period</code> and the corresponding value.
		 * <code>Cal.Data.BusinessCalendar.NoPeriod</code> and an error are returned if there are no
		 * values in the <code>Cal.Data.BusinessCalendar.Period</code>.
		 * @param period (CAL type: <code>Cal.Data.BusinessCalendar.Period</code>)
		 * @return (CAL type: <code>(Cal.Data.BusinessCalendar.PeriodType, Cal.Core.Prelude.Int)</code>) 
		 */
		public static final SourceModel.Expr getBasePeriod(SourceModel.Expr period) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getBasePeriod), period});
		}

		/**
		 * Name binding for function: getBasePeriod.
		 * @see #getBasePeriod(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getBasePeriod = 
			QualifiedName.make(
				CAL_BusinessCalendar.MODULE_NAME, 
				"getBasePeriod");

		/**
		 * Returns the lowest level <code>Cal.Data.BusinessCalendar.PeriodType</code> in the
		 * <code>Cal.Data.BusinessCalendar.Period</code>.
		 * <code>Cal.Data.BusinessCalendar.NoPeriod</code> is returned if there are no values in the
		 * <code>Cal.Data.BusinessCalendar.Period</code>.
		 * @param arg_1 (CAL type: <code>Cal.Data.BusinessCalendar.Period</code>)
		 * @return (CAL type: <code>Cal.Data.BusinessCalendar.PeriodType</code>) 
		 */
		public static final SourceModel.Expr getBasePeriodType(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getBasePeriodType), arg_1});
		}

		/**
		 * Name binding for function: getBasePeriodType.
		 * @see #getBasePeriodType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getBasePeriodType = 
			QualifiedName.make(
				CAL_BusinessCalendar.MODULE_NAME, 
				"getBasePeriodType");

		/**
		 * Returns the value for the lowest level <code>Cal.Data.BusinessCalendar.PeriodType</code>
		 * in the <code>Cal.Data.BusinessCalendar.Period</code>.
		 * Throws an error if there are no values in the <code>Cal.Data.BusinessCalendar.Period</code>.
		 * @param arg_1 (CAL type: <code>Cal.Data.BusinessCalendar.Period</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 */
		public static final SourceModel.Expr getBasePeriodValue(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getBasePeriodValue), arg_1});
		}

		/**
		 * Name binding for function: getBasePeriodValue.
		 * @see #getBasePeriodValue(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getBasePeriodValue = 
			QualifiedName.make(
				CAL_BusinessCalendar.MODULE_NAME, 
				"getBasePeriodValue");

		/**
		 * Returns the list of valid values (and the corresponding string representations)
		 * for the specified <code>Cal.Data.BusinessCalendar.PeriodType</code> in the given
		 * <code>Cal.Data.BusinessCalendar.Period</code>.  Optionally, endpoints may be specified to
		 * limit the range.
		 * <p>
		 * For example, for the <code>Cal.Data.BusinessCalendar.DayOfWeekPeriod</code> period type,
		 * this might return <code>[(2, "Monday"), (4, "Wednesday"), (6, "Friday")]</code>.
		 * <p>
		 * For example, for the <code>Cal.Data.BusinessCalendar.QuarterPeriod</code> period type, 
		 * this might return <code>[(1, "Q1 98"), (2, "Q2 98"), (3, "Q3 98"), (4, "Q4 98")]</code>.
		 * 
		 * @param businessCalendar (CAL type: <code>Cal.Data.BusinessCalendar.BusinessCalendar</code>)
		 * @param parentPeriod (CAL type: <code>Cal.Data.BusinessCalendar.Period</code>)
		 * @param periodType (CAL type: <code>Cal.Data.BusinessCalendar.PeriodType</code>)
		 * @param maybeMinValue (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Core.Prelude.Int</code>)
		 * @param maybeMaxValue (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Core.Prelude.Int</code>)
		 * @return (CAL type: <code>[(Cal.Core.Prelude.Int, Cal.Core.Prelude.String)]</code>) 
		 */
		public static final SourceModel.Expr getChildPeriodValues(SourceModel.Expr businessCalendar, SourceModel.Expr parentPeriod, SourceModel.Expr periodType, SourceModel.Expr maybeMinValue, SourceModel.Expr maybeMaxValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getChildPeriodValues), businessCalendar, parentPeriod, periodType, maybeMinValue, maybeMaxValue});
		}

		/**
		 * Name binding for function: getChildPeriodValues.
		 * @see #getChildPeriodValues(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getChildPeriodValues = 
			QualifiedName.make(
				CAL_BusinessCalendar.MODULE_NAME, 
				"getChildPeriodValues");

		/**
		 * Queries a given <code>Cal.Data.BusinessCalendar.BusinessCalendar</code> to convert a time
		 * value into a <code>Cal.Data.BusinessCalendar.Period</code> with the specified
		 * <code>Cal.Data.BusinessCalendar.PeriodType</code>s.
		 * @param businessCalendar (CAL type: <code>Cal.Data.BusinessCalendar.BusinessCalendar</code>)
		 * @param time (CAL type: <code>Cal.Utilities.Time.Time</code>)
		 * @param periodTypes (CAL type: <code>[Cal.Data.BusinessCalendar.PeriodType]</code>)
		 * @return (CAL type: <code>Cal.Data.BusinessCalendar.Period</code>) 
		 */
		public static final SourceModel.Expr getPeriodFromTime(SourceModel.Expr businessCalendar, SourceModel.Expr time, SourceModel.Expr periodTypes) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getPeriodFromTime), businessCalendar, time, periodTypes});
		}

		/**
		 * Name binding for function: getPeriodFromTime.
		 * @see #getPeriodFromTime(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getPeriodFromTime = 
			QualifiedName.make(
				CAL_BusinessCalendar.MODULE_NAME, 
				"getPeriodFromTime");

		/**
		 * Queries a given <code>Cal.Data.BusinessCalendar.BusinessCalendar</code> to determine the
		 * time range corresponding to the given <code>Cal.Data.BusinessCalendar.Period</code>.
		 * This is only possible if the <code>Cal.Data.BusinessCalendar.YearPeriod</code> period is
		 * included in the period.
		 * If the info cannot be determined for this period, then the range returned
		 * will not be bounded on either end.
		 * @param businessCalendar (CAL type: <code>Cal.Data.BusinessCalendar.BusinessCalendar</code>)
		 * @param period (CAL type: <code>Cal.Data.BusinessCalendar.Period</code>)
		 * @return (CAL type: <code>Cal.Utilities.Range.Range Cal.Utilities.Time.Time</code>) 
		 */
		public static final SourceModel.Expr getPeriodTimeRange(SourceModel.Expr businessCalendar, SourceModel.Expr period) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getPeriodTimeRange), businessCalendar, period});
		}

		/**
		 * Name binding for function: getPeriodTimeRange.
		 * @see #getPeriodTimeRange(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getPeriodTimeRange = 
			QualifiedName.make(
				CAL_BusinessCalendar.MODULE_NAME, 
				"getPeriodTimeRange");

		/**
		 * Returns a descriptive name for the <code>Cal.Data.BusinessCalendar.PeriodType</code>.
		 * @param periodType (CAL type: <code>Cal.Data.BusinessCalendar.PeriodType</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr getPeriodTypeName(SourceModel.Expr periodType) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getPeriodTypeName), periodType});
		}

		/**
		 * Name binding for function: getPeriodTypeName.
		 * @see #getPeriodTypeName(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getPeriodTypeName = 
			QualifiedName.make(
				CAL_BusinessCalendar.MODULE_NAME, 
				"getPeriodTypeName");

		/**
		 * Returns the value of the specified <code>Cal.Data.BusinessCalendar.PeriodType</code> in
		 * the <code>Cal.Data.BusinessCalendar.Period</code>.
		 * If the <code>Cal.Data.BusinessCalendar.Period</code> does not include this
		 * <code>Cal.Data.BusinessCalendar.PeriodType</code>, then <code>Cal.Core.Prelude.Nothing</code>
		 * will be returned.
		 * <p>
		 * TODO: should this function attempt to calculate the value if it is not
		 * available? (e.g. Quarter if Year and Month are present.)
		 * 
		 * @param period (CAL type: <code>Cal.Data.BusinessCalendar.Period</code>)
		 * @param periodType (CAL type: <code>Cal.Data.BusinessCalendar.PeriodType</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Core.Prelude.Int</code>) 
		 */
		public static final SourceModel.Expr getPeriodValue(SourceModel.Expr period, SourceModel.Expr periodType) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getPeriodValue), period, periodType});
		}

		/**
		 * Name binding for function: getPeriodValue.
		 * @see #getPeriodValue(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getPeriodValue = 
			QualifiedName.make(
				CAL_BusinessCalendar.MODULE_NAME, 
				"getPeriodValue");

		/**
		 * Queries a given <code>Cal.Data.BusinessCalendar.BusinessCalendar</code> to determine the
		 * value of a given <code>Cal.Data.BusinessCalendar.PeriodType</code> for the specified time.
		 * @param businessCalendar (CAL type: <code>Cal.Data.BusinessCalendar.BusinessCalendar</code>)
		 * @param time (CAL type: <code>Cal.Utilities.Time.Time</code>)
		 * @param periodType (CAL type: <code>Cal.Data.BusinessCalendar.PeriodType</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 */
		public static final SourceModel.Expr getPeriodValueFromTime(SourceModel.Expr businessCalendar, SourceModel.Expr time, SourceModel.Expr periodType) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getPeriodValueFromTime), businessCalendar, time, periodType});
		}

		/**
		 * Name binding for function: getPeriodValueFromTime.
		 * @see #getPeriodValueFromTime(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getPeriodValueFromTime = 
			QualifiedName.make(
				CAL_BusinessCalendar.MODULE_NAME, 
				"getPeriodValueFromTime");

		/**
		 * Queries a given <code>Cal.Data.BusinessCalendar.BusinessCalendar</code> to determine
		 * what range of values are possible for a specified <code>Cal.Data.BusinessCalendar.PeriodType</code>
		 * within a specified <code>Cal.Data.BusinessCalendar.Period</code>.
		 * @param businessCalendar (CAL type: <code>Cal.Data.BusinessCalendar.BusinessCalendar</code>)
		 * @param parentPeriod (CAL type: <code>Cal.Data.BusinessCalendar.Period</code>)
		 * @param periodType (CAL type: <code>Cal.Data.BusinessCalendar.PeriodType</code>)
		 * @return (CAL type: <code>(Cal.Core.Prelude.Int, Cal.Core.Prelude.Int)</code>) 
		 */
		public static final SourceModel.Expr getPeriodValueRange(SourceModel.Expr businessCalendar, SourceModel.Expr parentPeriod, SourceModel.Expr periodType) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getPeriodValueRange), businessCalendar, parentPeriod, periodType});
		}

		/**
		 * Name binding for function: getPeriodValueRange.
		 * @see #getPeriodValueRange(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getPeriodValueRange = 
			QualifiedName.make(
				CAL_BusinessCalendar.MODULE_NAME, 
				"getPeriodValueRange");

		/**
		 * Returns a list <code>Cal.Data.BusinessCalendar.PeriodType</code>-value pairs for the
		 * <code>Cal.Data.BusinessCalendar.Period</code>.
		 * The pairs are in nesting order, with the outermost
		 * <code>Cal.Data.BusinessCalendar.PeriodType</code> at the beginning of the list.
		 * @param period (CAL type: <code>Cal.Data.BusinessCalendar.Period</code>)
		 * @return (CAL type: <code>[(Cal.Data.BusinessCalendar.PeriodType, Cal.Core.Prelude.Int)]</code>) 
		 */
		public static final SourceModel.Expr getPeriodValues(SourceModel.Expr period) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getPeriodValues), period});
		}

		/**
		 * Name binding for function: getPeriodValues.
		 * @see #getPeriodValues(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getPeriodValues = 
			QualifiedName.make(
				CAL_BusinessCalendar.MODULE_NAME, 
				"getPeriodValues");

		/**
		 * Queries a given <code>Cal.Data.BusinessCalendar.BusinessCalendar</code> to determine the
		 * database expression to extract the value of the <code>Cal.Data.BusinessCalendar.PeriodType</code>
		 * from the specified time expression.
		 * @param businessCalendar (CAL type: <code>Cal.Data.BusinessCalendar.BusinessCalendar</code>)
		 * @param baseTimeExpr (CAL type: <code>Cal.Data.DictionaryQuery.Expr</code>)
		 * @param periodType (CAL type: <code>Cal.Data.BusinessCalendar.PeriodType</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.Expr</code>) 
		 */
		public static final SourceModel.Expr getTimePartExpr(SourceModel.Expr businessCalendar, SourceModel.Expr baseTimeExpr, SourceModel.Expr periodType) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getTimePartExpr), businessCalendar, baseTimeExpr, periodType});
		}

		/**
		 * Name binding for function: getTimePartExpr.
		 * @see #getTimePartExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getTimePartExpr = 
			QualifiedName.make(
				CAL_BusinessCalendar.MODULE_NAME, 
				"getTimePartExpr");

		/**
		 * Queries a given <code>Cal.Data.BusinessCalendar.BusinessCalendar</code> to determine the
		 * range of possible values for the base <code>Cal.Data.BusinessCalendar.PeriodType</code>
		 * of a <code>Cal.Data.BusinessCalendar.Period</code>.
		 * @param businessCalendar (CAL type: <code>Cal.Data.BusinessCalendar.BusinessCalendar</code>)
		 * @param period (CAL type: <code>Cal.Data.BusinessCalendar.Period</code>)
		 * @return (CAL type: <code>(Cal.Core.Prelude.Int, Cal.Core.Prelude.Int)</code>) 
		 */
		public static final SourceModel.Expr getValidBasePeriodValues(SourceModel.Expr businessCalendar, SourceModel.Expr period) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getValidBasePeriodValues), businessCalendar, period});
		}

		/**
		 * Name binding for function: getValidBasePeriodValues.
		 * @see #getValidBasePeriodValues(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getValidBasePeriodValues = 
			QualifiedName.make(
				CAL_BusinessCalendar.MODULE_NAME, 
				"getValidBasePeriodValues");

		/**
		 * Returns a list of the valid child <code>Cal.Data.BusinessCalendar.PeriodType</code> values
		 * which can roll up into the specified parent <code>Cal.Data.BusinessCalendar.PeriodType</code>.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Data.BusinessCalendar.getValidParentTimePeriods
		 * </dl>
		 * 
		 * @param parentPeriod (CAL type: <code>Cal.Data.BusinessCalendar.PeriodType</code>)
		 * @return (CAL type: <code>[Cal.Data.BusinessCalendar.PeriodType]</code>) 
		 */
		public static final SourceModel.Expr getValidChildTimePeriods(SourceModel.Expr parentPeriod) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getValidChildTimePeriods), parentPeriod});
		}

		/**
		 * Name binding for function: getValidChildTimePeriods.
		 * @see #getValidChildTimePeriods(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getValidChildTimePeriods = 
			QualifiedName.make(
				CAL_BusinessCalendar.MODULE_NAME, 
				"getValidChildTimePeriods");

		/**
		 * Returns a list of the valid parent <code>Cal.Data.BusinessCalendar.PeriodType</code> values
		 * into which a child <code>Cal.Data.BusinessCalendar.PeriodType</code> can roll up.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Data.BusinessCalendar.getValidChildTimePeriods
		 * </dl>
		 * 
		 * @param childPeriod (CAL type: <code>Cal.Data.BusinessCalendar.PeriodType</code>)
		 * @return (CAL type: <code>[Cal.Data.BusinessCalendar.PeriodType]</code>) 
		 */
		public static final SourceModel.Expr getValidParentTimePeriods(SourceModel.Expr childPeriod) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getValidParentTimePeriods), childPeriod});
		}

		/**
		 * Name binding for function: getValidParentTimePeriods.
		 * @see #getValidParentTimePeriods(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getValidParentTimePeriods = 
			QualifiedName.make(
				CAL_BusinessCalendar.MODULE_NAME, 
				"getValidParentTimePeriods");

		/**
		 * Retrieves the <code>Cal.Data.BusinessCalendar.PeriodType</code> corresponding to a given ordinal.
		 * @param intVal (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @return (CAL type: <code>Cal.Data.BusinessCalendar.PeriodType</code>) 
		 *          The corresponding <code>Cal.Data.BusinessCalendar.PeriodType</code> if one exists, or
		 * <code>Cal.Data.BusinessCalendar.NoPeriod</code> otherwise.
		 */
		public static final SourceModel.Expr intToPeriodType(SourceModel.Expr intVal) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intToPeriodType), intVal});
		}

		/**
		 * @see #intToPeriodType(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param intVal
		 * @return the SourceModel.Expr representing an application of intToPeriodType
		 */
		public static final SourceModel.Expr intToPeriodType(int intVal) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intToPeriodType), SourceModel.Expr.makeIntValue(intVal)});
		}

		/**
		 * Name binding for function: intToPeriodType.
		 * @see #intToPeriodType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intToPeriodType = 
			QualifiedName.make(
				CAL_BusinessCalendar.MODULE_NAME, 
				"intToPeriodType");

		/**
		 * Construct a new <code>Cal.Data.BusinessCalendar.BusinessCalendar</code> with the specified behavior.
		 * @param periodValueRangeFn (CAL type: <code>Cal.Data.BusinessCalendar.Period -> Cal.Data.BusinessCalendar.PeriodType -> (Cal.Core.Prelude.Int, Cal.Core.Prelude.Int)</code>)
		 *          a function to get the range of valid values for a specified period, given the parent period
		 * @param periodDisplayNameFn (CAL type: <code>Cal.Data.BusinessCalendar.Period -> Cal.Data.BusinessCalendar.PeriodType -> Cal.Core.Prelude.Int -> Cal.Core.Prelude.String</code>)
		 *          a function to get the display name for a period value
		 * @param periodTimeRangeFn (CAL type: <code>Cal.Data.BusinessCalendar.Period -> Cal.Utilities.Range.Range Cal.Utilities.Time.Time</code>)
		 *          a function to get the start and end time for for a specified period
		 * @param periodValueFromTimeFn (CAL type: <code>Cal.Utilities.Time.Time -> Cal.Data.BusinessCalendar.PeriodType -> Cal.Core.Prelude.Int</code>)
		 *          a function to get the value of the specified period for a time value
		 * @param timePartExprFn (CAL type: <code>Cal.Data.DictionaryQuery.Expr -> Cal.Data.BusinessCalendar.PeriodType -> Cal.Data.DictionaryQuery.Expr</code>)
		 *          a function to build a database expression to extract the appropriate time part from a time value
		 * @return (CAL type: <code>Cal.Data.BusinessCalendar.BusinessCalendar</code>) 
		 */
		public static final SourceModel.Expr makeBusinessCalendar(SourceModel.Expr periodValueRangeFn, SourceModel.Expr periodDisplayNameFn, SourceModel.Expr periodTimeRangeFn, SourceModel.Expr periodValueFromTimeFn, SourceModel.Expr timePartExprFn) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeBusinessCalendar), periodValueRangeFn, periodDisplayNameFn, periodTimeRangeFn, periodValueFromTimeFn, timePartExprFn});
		}

		/**
		 * Name binding for function: makeBusinessCalendar.
		 * @see #makeBusinessCalendar(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeBusinessCalendar = 
			QualifiedName.make(
				CAL_BusinessCalendar.MODULE_NAME, 
				"makeBusinessCalendar");

		/**
		 * Constructs a <code>Cal.Data.BusinessCalendar.Period</code> from a containing
		 * <code>Cal.Data.BusinessCalendar.Period</code> value.
		 * The <code>Cal.Data.BusinessCalendar.PeriodType</code> is assumed to be able to nest
		 * within the innermost <code>Cal.Data.BusinessCalendar.PeriodType</code> of the parent.
		 * <p>
		 * TODO: validate period nesting (i.e. order)?
		 * 
		 * @param parentPeriod (CAL type: <code>Cal.Data.BusinessCalendar.Period</code>)
		 * @param childPeriodType (CAL type: <code>Cal.Data.BusinessCalendar.PeriodType</code>)
		 * @param childPeriodValue (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @return (CAL type: <code>Cal.Data.BusinessCalendar.Period</code>) 
		 */
		public static final SourceModel.Expr makeChildPeriod(SourceModel.Expr parentPeriod, SourceModel.Expr childPeriodType, SourceModel.Expr childPeriodValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeChildPeriod), parentPeriod, childPeriodType, childPeriodValue});
		}

		/**
		 * @see #makeChildPeriod(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param parentPeriod
		 * @param childPeriodType
		 * @param childPeriodValue
		 * @return the SourceModel.Expr representing an application of makeChildPeriod
		 */
		public static final SourceModel.Expr makeChildPeriod(SourceModel.Expr parentPeriod, SourceModel.Expr childPeriodType, int childPeriodValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeChildPeriod), parentPeriod, childPeriodType, SourceModel.Expr.makeIntValue(childPeriodValue)});
		}

		/**
		 * Name binding for function: makeChildPeriod.
		 * @see #makeChildPeriod(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeChildPeriod = 
			QualifiedName.make(
				CAL_BusinessCalendar.MODULE_NAME, 
				"makeChildPeriod");

		/**
		 * Constructs a <code>Cal.Data.BusinessCalendar.Period</code> from its
		 * <code>Cal.Data.BusinessCalendar.Period</code> values.
		 * The pairs are assumed to be in nesting order, with the outermost
		 * <code>Cal.Data.BusinessCalendar.PeriodType</code> at the beginning of the list.
		 * <p>
		 * TODO: validate period nesting (i.e. order)?
		 * 
		 * @param periodValues (CAL type: <code>[(Cal.Data.BusinessCalendar.PeriodType, Cal.Core.Prelude.Int)]</code>)
		 * @return (CAL type: <code>Cal.Data.BusinessCalendar.Period</code>) 
		 */
		public static final SourceModel.Expr makePeriod(SourceModel.Expr periodValues) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makePeriod), periodValues});
		}

		/**
		 * Name binding for function: makePeriod.
		 * @see #makePeriod(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makePeriod = 
			QualifiedName.make(CAL_BusinessCalendar.MODULE_NAME, "makePeriod");

		/**
		 * Queries a given <code>Cal.Data.BusinessCalendar.BusinessCalendar</code> to determine the
		 * display string of a given <code>Cal.Data.BusinessCalendar.PeriodType</code> and value
		 * with a specified <code>Cal.Data.BusinessCalendar.Period</code> as context.
		 * <p>
		 * For example, depending on the <code>Cal.Data.BusinessCalendar.BusinessCalendar</code>,
		 * for the <code>Cal.Data.BusinessCalendar.DayOfWeekPeriod</code> period type, this might return
		 * <code>"Monday"</code>.  Similarly, for the <code>Cal.Data.BusinessCalendar.QuarterPeriod</code>
		 * period type, this might return <code>"Q1 98"</code>.
		 * 
		 * @param businessCalendar (CAL type: <code>Cal.Data.BusinessCalendar.BusinessCalendar</code>)
		 * @param parentPeriod (CAL type: <code>Cal.Data.BusinessCalendar.Period</code>)
		 * @param periodType (CAL type: <code>Cal.Data.BusinessCalendar.PeriodType</code>)
		 * @param periodValue (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr periodDisplayName(SourceModel.Expr businessCalendar, SourceModel.Expr parentPeriod, SourceModel.Expr periodType, SourceModel.Expr periodValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.periodDisplayName), businessCalendar, parentPeriod, periodType, periodValue});
		}

		/**
		 * @see #periodDisplayName(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param businessCalendar
		 * @param parentPeriod
		 * @param periodType
		 * @param periodValue
		 * @return the SourceModel.Expr representing an application of periodDisplayName
		 */
		public static final SourceModel.Expr periodDisplayName(SourceModel.Expr businessCalendar, SourceModel.Expr parentPeriod, SourceModel.Expr periodType, int periodValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.periodDisplayName), businessCalendar, parentPeriod, periodType, SourceModel.Expr.makeIntValue(periodValue)});
		}

		/**
		 * Name binding for function: periodDisplayName.
		 * @see #periodDisplayName(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName periodDisplayName = 
			QualifiedName.make(
				CAL_BusinessCalendar.MODULE_NAME, 
				"periodDisplayName");

		/**
		 * Retrieves the ordinal corresponding to a given <code>Cal.Data.BusinessCalendar.PeriodType</code>.
		 * @param periodType (CAL type: <code>Cal.Data.BusinessCalendar.PeriodType</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          The corresponding ordinal.
		 */
		public static final SourceModel.Expr periodTypeToInt(SourceModel.Expr periodType) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.periodTypeToInt), periodType});
		}

		/**
		 * Name binding for function: periodTypeToInt.
		 * @see #periodTypeToInt(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName periodTypeToInt = 
			QualifiedName.make(
				CAL_BusinessCalendar.MODULE_NAME, 
				"periodTypeToInt");

		/**
		 * Sets the value for the lowest level <code>Cal.Data.BusinessCalendar.PeriodType</code>
		 * in the <code>Cal.Data.BusinessCalendar.Period</code>.
		 * Throws an error if there are no values in the <code>Cal.Data.BusinessCalendar.Period</code>.
		 * @param period (CAL type: <code>Cal.Data.BusinessCalendar.Period</code>)
		 * @param newBasePeriodValue (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @return (CAL type: <code>Cal.Data.BusinessCalendar.Period</code>) 
		 */
		public static final SourceModel.Expr setBasePeriodValue(SourceModel.Expr period, SourceModel.Expr newBasePeriodValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.setBasePeriodValue), period, newBasePeriodValue});
		}

		/**
		 * @see #setBasePeriodValue(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param period
		 * @param newBasePeriodValue
		 * @return the SourceModel.Expr representing an application of setBasePeriodValue
		 */
		public static final SourceModel.Expr setBasePeriodValue(SourceModel.Expr period, int newBasePeriodValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.setBasePeriodValue), period, SourceModel.Expr.makeIntValue(newBasePeriodValue)});
		}

		/**
		 * Name binding for function: setBasePeriodValue.
		 * @see #setBasePeriodValue(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName setBasePeriodValue = 
			QualifiedName.make(
				CAL_BusinessCalendar.MODULE_NAME, 
				"setBasePeriodValue");

		/**
		 * Adds an offset to the specified period type of the period.
		 * <p>
		 * If this would cause the root period type in the period to go out of bounds, 
		 * then an error will be thrown.  For example, for the period <code>[Month, 10]</code>
		 * adding 5 to the Month period type would result in a month value of 15, which
		 * is not possible.
		 * <p>
		 * If the root period type is Year, then this error should not occur.
		 * <p>
		 * If not shifting the base (lowest) period type in the period, then it is
		 * possible that the lower-level values could become out of range.  In this case,
		 * they will be set to the nearest in-range value.  For example, adding 1 Year
		 * to <code>[(Year 2004, Month 2(Feb), Day 29)]</code> would give
		 * <code>[(Year 2005, Month 2(Feb), Day 28)]</code>.
		 * <p>
		 * Currently, this function will only work with period types explicitly included
		 * in the period.
		 * <p>
		 * TODO: it would probably be nice to handle ones which can be determined from
		 * lower level ones (quarters from months, etc...)...
		 * <p>
		 * TODO: does this handle the case where the child values need to change
		 * depending on the parent values?  For example, for Year/Month/DayOfYear,
		 * shifting the month value should update the DayOfYear value as well.
		 * 
		 * @param businessCalendar (CAL type: <code>Cal.Data.BusinessCalendar.BusinessCalendar</code>)
		 * @param origPeriod (CAL type: <code>Cal.Data.BusinessCalendar.Period</code>)
		 * @param periodType (CAL type: <code>Cal.Data.BusinessCalendar.PeriodType</code>)
		 * @param shiftAmount (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @return (CAL type: <code>Cal.Data.BusinessCalendar.Period</code>) 
		 */
		public static final SourceModel.Expr shiftByNPeriods(SourceModel.Expr businessCalendar, SourceModel.Expr origPeriod, SourceModel.Expr periodType, SourceModel.Expr shiftAmount) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shiftByNPeriods), businessCalendar, origPeriod, periodType, shiftAmount});
		}

		/**
		 * @see #shiftByNPeriods(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param businessCalendar
		 * @param origPeriod
		 * @param periodType
		 * @param shiftAmount
		 * @return the SourceModel.Expr representing an application of shiftByNPeriods
		 */
		public static final SourceModel.Expr shiftByNPeriods(SourceModel.Expr businessCalendar, SourceModel.Expr origPeriod, SourceModel.Expr periodType, int shiftAmount) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shiftByNPeriods), businessCalendar, origPeriod, periodType, SourceModel.Expr.makeIntValue(shiftAmount)});
		}

		/**
		 * Name binding for function: shiftByNPeriods.
		 * @see #shiftByNPeriods(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shiftByNPeriods = 
			QualifiedName.make(
				CAL_BusinessCalendar.MODULE_NAME, 
				"shiftByNPeriods");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = 2050478502;

}
