/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_FiscalBusinessCalendar.java)
 * was generated from CAL module: Cal.Data.FiscalBusinessCalendar.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Data.FiscalBusinessCalendar module from Java code.
 *  
 * Creation date: Tue Aug 28 15:58:43 PDT 2007
 * --!>
 *  
 */

package org.openquark.cal.module.Cal.Data;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * This module contains an implementation of a <code>Cal.Data.BusinessCalendar.BusinessCalendar</code> based on a simple fiscal calendar which can be shifted
 * some number of months from the Gregorian calendar.
 * @author Richard Webster
 */
public final class CAL_FiscalBusinessCalendar {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Data.FiscalBusinessCalendar");

	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Data.FiscalBusinessCalendar module.
	 */
	public static final class Functions {
		/**
		 * Construct a fiscal business calendar that is offset from the default Gregorian 
		 * calendar by a fixed number of months.
		 * For example, if the fiscal calendar is shifted by +2 months, then the fiscal 2007 year would begin on Nov 1, 2006.
		 * If shifted by -2 months, then the fiscal 2007 year would begin on March 1, 2007.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Data.GregorianBusinessCalendar.defaultBusinessCalendar
		 * </dl>
		 * 
		 * @param nMonthsOffset (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the number of months which the fiscal calendar is offset from the Gregorian calendar
		 * @return (CAL type: <code>Cal.Data.BusinessCalendar.BusinessCalendar</code>) 
		 *          a fiscal business calendar shifted by N months from the Gregorian calendar
		 */
		public static final SourceModel.Expr fiscalBusinessCalendar(SourceModel.Expr nMonthsOffset) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fiscalBusinessCalendar), nMonthsOffset});
		}

		/**
		 * @see #fiscalBusinessCalendar(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param nMonthsOffset
		 * @return the SourceModel.Expr representing an application of fiscalBusinessCalendar
		 */
		public static final SourceModel.Expr fiscalBusinessCalendar(int nMonthsOffset) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fiscalBusinessCalendar), SourceModel.Expr.makeIntValue(nMonthsOffset)});
		}

		/**
		 * Name binding for function: fiscalBusinessCalendar.
		 * @see #fiscalBusinessCalendar(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fiscalBusinessCalendar = 
			QualifiedName.make(
				CAL_FiscalBusinessCalendar.MODULE_NAME, 
				"fiscalBusinessCalendar");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = 1188187974;

}
