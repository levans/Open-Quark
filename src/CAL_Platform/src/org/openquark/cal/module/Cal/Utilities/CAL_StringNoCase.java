/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_StringNoCase.java)
 * was generated from CAL module: Cal.Utilities.StringNoCase.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Utilities.StringNoCase module from Java code.
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
 * Defines <code>Cal.Utilities.StringNoCase.StringNoCase</code>, a case-insensitive string type and a variety of useful operations
 * and class instances for it.
 * <p>
 * <code>Cal.Utilities.StringNoCase.StringNoCase</code> is mainly intended to allow the use of <code>Cal.Core.Prelude.String</code>s in case-insensitive ways for keys in maps etc.
 * In general, very few functions are available for <code>Cal.Utilities.StringNoCase.StringNoCase</code> since we can efficiently just convert to a <code>Cal.Core.Prelude.String</code> and use
 * the <code>Cal.Core.Prelude.String</code> functions. However, a few additional functions would be useful here.
 * 
 * @author Bo Ilic
 */
public final class CAL_StringNoCase {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Utilities.StringNoCase");

	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Utilities.StringNoCase module.
	 */
	public static final class TypeConstructors {
		/**
		 * A case-insensitive string type. Note that although comparison and equality operators on <code>Cal.Utilities.StringNoCase.StringNoCase</code> are case-insensitive,
		 * <code>Cal.Utilities.StringNoCase.StringNoCase</code> values preserve the case of the underlying <code>Cal.Core.Prelude.String</code> whenever possible e.g. the concatenation of 2 case-insensitive
		 * strings will have as its underlying string value the concatentation of the 2 underlying strings.
		 */
		public static final QualifiedName StringNoCase = 
			QualifiedName.make(CAL_StringNoCase.MODULE_NAME, "StringNoCase");

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Utilities.StringNoCase module.
	 */
	public static final class Functions {
		/**
		 * Converts a <code>Cal.Core.Prelude.String</code> value to a <code>Cal.Utilities.StringNoCase.StringNoCase</code> value.
		 * @param stringValue (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the <code>String</code> value to be converted.
		 * @return (CAL type: <code>Cal.Utilities.StringNoCase.StringNoCase</code>) 
		 *          the corresponding <code>StringNoCase</code> value.
		 */
		public static final SourceModel.Expr fromString(SourceModel.Expr stringValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromString), stringValue});
		}

		/**
		 * @see #fromString(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param stringValue
		 * @return the SourceModel.Expr representing an application of fromString
		 */
		public static final SourceModel.Expr fromString(java.lang.String stringValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromString), SourceModel.Expr.makeStringValue(stringValue)});
		}

		/**
		 * Name binding for function: fromString.
		 * @see #fromString(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fromString = 
			QualifiedName.make(CAL_StringNoCase.MODULE_NAME, "fromString");

		/**
		 * Converts a <code>Cal.Utilities.StringNoCase.StringNoCase</code> value to a <code>Cal.Core.Prelude.String</code> value.
		 * @param stringNoCaseValue (CAL type: <code>Cal.Utilities.StringNoCase.StringNoCase</code>)
		 *          the <code>StringNoCase</code> value to be converted.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the corresponding <code>String</code> value.
		 */
		public static final SourceModel.Expr toString(SourceModel.Expr stringNoCaseValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toString), stringNoCaseValue});
		}

		/**
		 * @see #toString(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param stringNoCaseValue
		 * @return the SourceModel.Expr representing an application of toString
		 */
		public static final SourceModel.Expr toString(java.lang.String stringNoCaseValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toString), SourceModel.Expr.makeStringValue(stringNoCaseValue)});
		}

		/**
		 * Name binding for function: toString.
		 * @see #toString(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName toString = 
			QualifiedName.make(CAL_StringNoCase.MODULE_NAME, "toString");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = -1593005542;

}
