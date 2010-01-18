/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_StringNoCase_internal.java)
 * was generated from CAL module: Cal.Utilities.StringNoCase.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Utilities.StringNoCase module from Java code.
 *  
 * Creation date: Fri Mar 16 13:11:58 PST 2007
 * --!>
 *  
 */

package org.openquark.cal.internal.module.Cal.Utilities;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * Defines <code>Cal.Utilities.StringNoCase.StringNoCase</code>, a case-insensitive string type and a variety of useful operations
 * and class instances for it.
 * <p>
 * <code>Cal.Utilities.StringNoCase.StringNoCase</code> is mainly intended to allow the use of <code>Cal.Core.Prelude.String</code>s in case-insensitive ways for keys in maps etc.
 * In general, very few functions are available for <code>Cal.Utilities.StringNoCase.StringNoCase</code> since we can efficiently just convert to a <code>Cal.Core.Prelude.String</code> and use
 * the <code>Cal.Core.Prelude.String</code> functions. However, a few additional functions would be useful here.
 * 
 * @author Bo Ilic
 */
public final class CAL_StringNoCase_internal {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Utilities.StringNoCase");

	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Utilities.StringNoCase module.
	 */
	public static final class Functions {
		/**
		 * Helper binding method for function: appendStringNoCase. 
		 * @param string1
		 * @param string2
		 * @return the SourceModule.expr representing an application of appendStringNoCase
		 */
		public static final SourceModel.Expr appendStringNoCase(SourceModel.Expr string1, SourceModel.Expr string2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.appendStringNoCase), string1, string2});
		}

		/**
		 * @see #appendStringNoCase(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param string1
		 * @param string2
		 * @return the SourceModel.Expr representing an application of appendStringNoCase
		 */
		public static final SourceModel.Expr appendStringNoCase(java.lang.String string1, java.lang.String string2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.appendStringNoCase), SourceModel.Expr.makeStringValue(string1), SourceModel.Expr.makeStringValue(string2)});
		}

		/**
		 * Name binding for function: appendStringNoCase.
		 * @see #appendStringNoCase(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName appendStringNoCase = 
			QualifiedName.make(
				CAL_StringNoCase_internal.MODULE_NAME, 
				"appendStringNoCase");

		/**
		 * Helper binding method for function: compareStringNoCase. 
		 * @param string1
		 * @param string2
		 * @return the SourceModule.expr representing an application of compareStringNoCase
		 */
		public static final SourceModel.Expr compareStringNoCase(SourceModel.Expr string1, SourceModel.Expr string2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.compareStringNoCase), string1, string2});
		}

		/**
		 * @see #compareStringNoCase(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param string1
		 * @param string2
		 * @return the SourceModel.Expr representing an application of compareStringNoCase
		 */
		public static final SourceModel.Expr compareStringNoCase(java.lang.String string1, java.lang.String string2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.compareStringNoCase), SourceModel.Expr.makeStringValue(string1), SourceModel.Expr.makeStringValue(string2)});
		}

		/**
		 * Name binding for function: compareStringNoCase.
		 * @see #compareStringNoCase(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName compareStringNoCase = 
			QualifiedName.make(
				CAL_StringNoCase_internal.MODULE_NAME, 
				"compareStringNoCase");

		/**
		 * Helper binding method for function: concatStringNoCase. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of concatStringNoCase
		 */
		public static final SourceModel.Expr concatStringNoCase(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.concatStringNoCase), arg_1});
		}

		/**
		 * Name binding for function: concatStringNoCase.
		 * @see #concatStringNoCase(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName concatStringNoCase = 
			QualifiedName.make(
				CAL_StringNoCase_internal.MODULE_NAME, 
				"concatStringNoCase");

		/**
		 * Helper binding method for function: emptyStringNoCase. 
		 * @return the SourceModule.expr representing an application of emptyStringNoCase
		 */
		public static final SourceModel.Expr emptyStringNoCase() {
			return SourceModel.Expr.Var.make(Functions.emptyStringNoCase);
		}

		/**
		 * Name binding for function: emptyStringNoCase.
		 * @see #emptyStringNoCase()
		 */
		public static final QualifiedName emptyStringNoCase = 
			QualifiedName.make(
				CAL_StringNoCase_internal.MODULE_NAME, 
				"emptyStringNoCase");

		/**
		 * Helper binding method for function: equalsStringNoCase. 
		 * @param string1
		 * @param string2
		 * @return the SourceModule.expr representing an application of equalsStringNoCase
		 */
		public static final SourceModel.Expr equalsStringNoCase(SourceModel.Expr string1, SourceModel.Expr string2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.equalsStringNoCase), string1, string2});
		}

		/**
		 * @see #equalsStringNoCase(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param string1
		 * @param string2
		 * @return the SourceModel.Expr representing an application of equalsStringNoCase
		 */
		public static final SourceModel.Expr equalsStringNoCase(java.lang.String string1, java.lang.String string2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.equalsStringNoCase), SourceModel.Expr.makeStringValue(string1), SourceModel.Expr.makeStringValue(string2)});
		}

		/**
		 * Name binding for function: equalsStringNoCase.
		 * @see #equalsStringNoCase(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName equalsStringNoCase = 
			QualifiedName.make(
				CAL_StringNoCase_internal.MODULE_NAME, 
				"equalsStringNoCase");

		/**
		 * Helper binding method for function: greaterThanEqualsStringNoCase. 
		 * @param string1
		 * @param string2
		 * @return the SourceModule.expr representing an application of greaterThanEqualsStringNoCase
		 */
		public static final SourceModel.Expr greaterThanEqualsStringNoCase(SourceModel.Expr string1, SourceModel.Expr string2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.greaterThanEqualsStringNoCase), string1, string2});
		}

		/**
		 * @see #greaterThanEqualsStringNoCase(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param string1
		 * @param string2
		 * @return the SourceModel.Expr representing an application of greaterThanEqualsStringNoCase
		 */
		public static final SourceModel.Expr greaterThanEqualsStringNoCase(java.lang.String string1, java.lang.String string2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.greaterThanEqualsStringNoCase), SourceModel.Expr.makeStringValue(string1), SourceModel.Expr.makeStringValue(string2)});
		}

		/**
		 * Name binding for function: greaterThanEqualsStringNoCase.
		 * @see #greaterThanEqualsStringNoCase(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName greaterThanEqualsStringNoCase = 
			QualifiedName.make(
				CAL_StringNoCase_internal.MODULE_NAME, 
				"greaterThanEqualsStringNoCase");

		/**
		 * Helper binding method for function: greaterThanStringNoCase. 
		 * @param string1
		 * @param string2
		 * @return the SourceModule.expr representing an application of greaterThanStringNoCase
		 */
		public static final SourceModel.Expr greaterThanStringNoCase(SourceModel.Expr string1, SourceModel.Expr string2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.greaterThanStringNoCase), string1, string2});
		}

		/**
		 * @see #greaterThanStringNoCase(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param string1
		 * @param string2
		 * @return the SourceModel.Expr representing an application of greaterThanStringNoCase
		 */
		public static final SourceModel.Expr greaterThanStringNoCase(java.lang.String string1, java.lang.String string2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.greaterThanStringNoCase), SourceModel.Expr.makeStringValue(string1), SourceModel.Expr.makeStringValue(string2)});
		}

		/**
		 * Name binding for function: greaterThanStringNoCase.
		 * @see #greaterThanStringNoCase(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName greaterThanStringNoCase = 
			QualifiedName.make(
				CAL_StringNoCase_internal.MODULE_NAME, 
				"greaterThanStringNoCase");

		/**
		 * Helper binding method for function: isEmptyStringNoCase. 
		 * @param string
		 * @return the SourceModule.expr representing an application of isEmptyStringNoCase
		 */
		public static final SourceModel.Expr isEmptyStringNoCase(SourceModel.Expr string) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isEmptyStringNoCase), string});
		}

		/**
		 * @see #isEmptyStringNoCase(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param string
		 * @return the SourceModel.Expr representing an application of isEmptyStringNoCase
		 */
		public static final SourceModel.Expr isEmptyStringNoCase(java.lang.String string) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isEmptyStringNoCase), SourceModel.Expr.makeStringValue(string)});
		}

		/**
		 * Name binding for function: isEmptyStringNoCase.
		 * @see #isEmptyStringNoCase(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isEmptyStringNoCase = 
			QualifiedName.make(
				CAL_StringNoCase_internal.MODULE_NAME, 
				"isEmptyStringNoCase");

		/**
		 * 
		 * @param stringNoCase (CAL type: <code>Cal.Utilities.StringNoCase.StringNoCase</code>)
		 * @param arg_2 (CAL type: <code>Cal.Utilities.StringNoCase.StringNoCase</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          -1 for LT, 0 for EQ, 1 for GT
		 */
		public static final SourceModel.Expr jCompareStringNoCase(SourceModel.Expr stringNoCase, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jCompareStringNoCase), stringNoCase, arg_2});
		}

		/**
		 * @see #jCompareStringNoCase(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param stringNoCase
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of jCompareStringNoCase
		 */
		public static final SourceModel.Expr jCompareStringNoCase(java.lang.String stringNoCase, java.lang.String arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jCompareStringNoCase), SourceModel.Expr.makeStringValue(stringNoCase), SourceModel.Expr.makeStringValue(arg_2)});
		}

		/**
		 * Name binding for function: jCompareStringNoCase.
		 * @see #jCompareStringNoCase(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jCompareStringNoCase = 
			QualifiedName.make(
				CAL_StringNoCase_internal.MODULE_NAME, 
				"jCompareStringNoCase");

		/**
		 * Helper binding method for function: lessThanEqualsStringNoCase. 
		 * @param string1
		 * @param string2
		 * @return the SourceModule.expr representing an application of lessThanEqualsStringNoCase
		 */
		public static final SourceModel.Expr lessThanEqualsStringNoCase(SourceModel.Expr string1, SourceModel.Expr string2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lessThanEqualsStringNoCase), string1, string2});
		}

		/**
		 * @see #lessThanEqualsStringNoCase(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param string1
		 * @param string2
		 * @return the SourceModel.Expr representing an application of lessThanEqualsStringNoCase
		 */
		public static final SourceModel.Expr lessThanEqualsStringNoCase(java.lang.String string1, java.lang.String string2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lessThanEqualsStringNoCase), SourceModel.Expr.makeStringValue(string1), SourceModel.Expr.makeStringValue(string2)});
		}

		/**
		 * Name binding for function: lessThanEqualsStringNoCase.
		 * @see #lessThanEqualsStringNoCase(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName lessThanEqualsStringNoCase = 
			QualifiedName.make(
				CAL_StringNoCase_internal.MODULE_NAME, 
				"lessThanEqualsStringNoCase");

		/**
		 * Helper binding method for function: lessThanStringNoCase. 
		 * @param string1
		 * @param string2
		 * @return the SourceModule.expr representing an application of lessThanStringNoCase
		 */
		public static final SourceModel.Expr lessThanStringNoCase(SourceModel.Expr string1, SourceModel.Expr string2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lessThanStringNoCase), string1, string2});
		}

		/**
		 * @see #lessThanStringNoCase(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param string1
		 * @param string2
		 * @return the SourceModel.Expr representing an application of lessThanStringNoCase
		 */
		public static final SourceModel.Expr lessThanStringNoCase(java.lang.String string1, java.lang.String string2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lessThanStringNoCase), SourceModel.Expr.makeStringValue(string1), SourceModel.Expr.makeStringValue(string2)});
		}

		/**
		 * Name binding for function: lessThanStringNoCase.
		 * @see #lessThanStringNoCase(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName lessThanStringNoCase = 
			QualifiedName.make(
				CAL_StringNoCase_internal.MODULE_NAME, 
				"lessThanStringNoCase");

		/**
		 * Helper binding method for function: maxStringNoCase. 
		 * @param string1
		 * @param string2
		 * @return the SourceModule.expr representing an application of maxStringNoCase
		 */
		public static final SourceModel.Expr maxStringNoCase(SourceModel.Expr string1, SourceModel.Expr string2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.maxStringNoCase), string1, string2});
		}

		/**
		 * @see #maxStringNoCase(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param string1
		 * @param string2
		 * @return the SourceModel.Expr representing an application of maxStringNoCase
		 */
		public static final SourceModel.Expr maxStringNoCase(java.lang.String string1, java.lang.String string2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.maxStringNoCase), SourceModel.Expr.makeStringValue(string1), SourceModel.Expr.makeStringValue(string2)});
		}

		/**
		 * Name binding for function: maxStringNoCase.
		 * @see #maxStringNoCase(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName maxStringNoCase = 
			QualifiedName.make(
				CAL_StringNoCase_internal.MODULE_NAME, 
				"maxStringNoCase");

		/**
		 * Helper binding method for function: minStringNoCase. 
		 * @param string1
		 * @param string2
		 * @return the SourceModule.expr representing an application of minStringNoCase
		 */
		public static final SourceModel.Expr minStringNoCase(SourceModel.Expr string1, SourceModel.Expr string2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.minStringNoCase), string1, string2});
		}

		/**
		 * @see #minStringNoCase(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param string1
		 * @param string2
		 * @return the SourceModel.Expr representing an application of minStringNoCase
		 */
		public static final SourceModel.Expr minStringNoCase(java.lang.String string1, java.lang.String string2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.minStringNoCase), SourceModel.Expr.makeStringValue(string1), SourceModel.Expr.makeStringValue(string2)});
		}

		/**
		 * Name binding for function: minStringNoCase.
		 * @see #minStringNoCase(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName minStringNoCase = 
			QualifiedName.make(
				CAL_StringNoCase_internal.MODULE_NAME, 
				"minStringNoCase");

		/**
		 * Helper binding method for function: notEqualsStringNoCase. 
		 * @param string1
		 * @param string2
		 * @return the SourceModule.expr representing an application of notEqualsStringNoCase
		 */
		public static final SourceModel.Expr notEqualsStringNoCase(SourceModel.Expr string1, SourceModel.Expr string2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.notEqualsStringNoCase), string1, string2});
		}

		/**
		 * @see #notEqualsStringNoCase(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param string1
		 * @param string2
		 * @return the SourceModel.Expr representing an application of notEqualsStringNoCase
		 */
		public static final SourceModel.Expr notEqualsStringNoCase(java.lang.String string1, java.lang.String string2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.notEqualsStringNoCase), SourceModel.Expr.makeStringValue(string1), SourceModel.Expr.makeStringValue(string2)});
		}

		/**
		 * Name binding for function: notEqualsStringNoCase.
		 * @see #notEqualsStringNoCase(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName notEqualsStringNoCase = 
			QualifiedName.make(
				CAL_StringNoCase_internal.MODULE_NAME, 
				"notEqualsStringNoCase");

		/**
		 * Helper binding method for function: showStringNoCase. 
		 * @param x
		 * @return the SourceModule.expr representing an application of showStringNoCase
		 */
		public static final SourceModel.Expr showStringNoCase(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showStringNoCase), x});
		}

		/**
		 * @see #showStringNoCase(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @return the SourceModel.Expr representing an application of showStringNoCase
		 */
		public static final SourceModel.Expr showStringNoCase(java.lang.String x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showStringNoCase), SourceModel.Expr.makeStringValue(x)});
		}

		/**
		 * Name binding for function: showStringNoCase.
		 * @see #showStringNoCase(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showStringNoCase = 
			QualifiedName.make(
				CAL_StringNoCase_internal.MODULE_NAME, 
				"showStringNoCase");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = 1149935927;

}
