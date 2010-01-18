/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_GregorianBusinessCalendar.java)
 * was generated from CAL module: Cal.Data.GregorianBusinessCalendar.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Data.GregorianBusinessCalendar module from Java code.
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
 * This module contains an implementation of a <code>Cal.Data.BusinessCalendar.BusinessCalendar</code> based on the Gregorian calendar. Here, the periods have
 * their usual Gregorian values and names.
 * @author Richard Webster
 */
public final class CAL_GregorianBusinessCalendar {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Data.GregorianBusinessCalendar");

	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Data.GregorianBusinessCalendar module.
	 */
	public static final class Functions {
		/**
		 * An instance of BusinessCalendar based on the Gregorian calendar.
		 * Uses American conventions when displaying dates.
		 * @return (CAL type: <code>Cal.Data.BusinessCalendar.BusinessCalendar</code>) 
		 */
		public static final SourceModel.Expr defaultBusinessCalendar() {
			return SourceModel.Expr.Var.make(Functions.defaultBusinessCalendar);
		}

		/**
		 * Name binding for function: defaultBusinessCalendar.
		 * @see #defaultBusinessCalendar()
		 */
		public static final QualifiedName defaultBusinessCalendar = 
			QualifiedName.make(
				CAL_GregorianBusinessCalendar.MODULE_NAME, 
				"defaultBusinessCalendar");

		/**
		 * An instance of BusinessCalendar based on the Gregorian calendar.
		 * Uses a more generic, easily parseable display format:
		 * <ul>
		 *  <li>
		 *   Periods are always listed from longest to shortest
		 *  </li>
		 *  <li>
		 *   Format is purely numerical except for separators and period type indicators
		 *   (e.g. 'Q' for quarters).
		 *  </li>
		 * </ul>
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Data.GregorianBusinessCalendar.gregorian_periodDisplayName_localizable
		 * </dl>
		 * 
		 * @return (CAL type: <code>Cal.Data.BusinessCalendar.BusinessCalendar</code>) 
		 */
		public static final SourceModel.Expr localizableDisplayNameBusinessCalendar() {
			return 
				SourceModel.Expr.Var.make(
					Functions.localizableDisplayNameBusinessCalendar);
		}

		/**
		 * Name binding for function: localizableDisplayNameBusinessCalendar.
		 * @see #localizableDisplayNameBusinessCalendar()
		 */
		public static final QualifiedName localizableDisplayNameBusinessCalendar = 
			QualifiedName.make(
				CAL_GregorianBusinessCalendar.MODULE_NAME, 
				"localizableDisplayNameBusinessCalendar");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = -1259805380;

}
