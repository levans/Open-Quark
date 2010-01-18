/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_MessageFormat.java)
 * was generated from CAL module: Cal.Utilities.MessageFormat.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Utilities.MessageFormat module from Java code.
 *  
 * Creation date: Fri Mar 16 13:11:56 PST 2007
 * --!>
 *  
 */

package org.openquark.cal.module.Cal.Utilities;

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
public final class CAL_MessageFormat {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Utilities.MessageFormat");

	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Utilities.MessageFormat module.
	 */
	public static final class Functions {
		/**
		 * Returns a string formatted according to the message pattern found in the given bundle using the specified key.
		 * The arguments are provided in a record (which can be a tuple), and is treated as ordered by the record's field names
		 * (i.e. ordinal fields first, in ascending order, then the textual fields in lexicographical order).
		 * @param bundle (CAL type: <code>Cal.Utilities.StringProperties.StringResourceBundle</code>)
		 *          the string resource bundle.
		 * @param key (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the key to be looked up in the bundle.
		 * @param args (CAL type: <code>Cal.Core.Prelude.Outputable r => {r}</code>)
		 *          the message arguments, ordered by the record's field names.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          a string formatted according to the message pattern found in the given bundle using the specified key,
		 * or a placeholder string if the key cannot be found.
		 */
		public static final SourceModel.Expr format(SourceModel.Expr bundle, SourceModel.Expr key, SourceModel.Expr args) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.format), bundle, key, args});
		}

		/**
		 * @see #format(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param bundle
		 * @param key
		 * @param args
		 * @return the SourceModel.Expr representing an application of format
		 */
		public static final SourceModel.Expr format(SourceModel.Expr bundle, java.lang.String key, SourceModel.Expr args) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.format), bundle, SourceModel.Expr.makeStringValue(key), args});
		}

		/**
		 * Name binding for function: format.
		 * @see #format(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName format = 
			QualifiedName.make(CAL_MessageFormat.MODULE_NAME, "format");

		/**
		 * Returns a string formatted according to the message pattern found in the given bundle using the specified key.
		 * @param bundle (CAL type: <code>Cal.Utilities.StringProperties.StringResourceBundle</code>)
		 *          the string resource bundle.
		 * @param key (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the key to be looked up in the bundle.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          a string formatted according to the message pattern found in the given bundle using the specified key,
		 * or a placeholder string if the key cannot be found.
		 */
		public static final SourceModel.Expr format0(SourceModel.Expr bundle, SourceModel.Expr key) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.format0), bundle, key});
		}

		/**
		 * @see #format0(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param bundle
		 * @param key
		 * @return the SourceModel.Expr representing an application of format0
		 */
		public static final SourceModel.Expr format0(SourceModel.Expr bundle, java.lang.String key) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.format0), bundle, SourceModel.Expr.makeStringValue(key)});
		}

		/**
		 * Name binding for function: format0.
		 * @see #format0(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName format0 = 
			QualifiedName.make(CAL_MessageFormat.MODULE_NAME, "format0");

		/**
		 * Returns a string formatted according to the message pattern found in the given bundle using the specified key.
		 * @param bundle (CAL type: <code>Cal.Utilities.StringProperties.StringResourceBundle</code>)
		 *          the string resource bundle.
		 * @param key (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the key to be looked up in the bundle.
		 * @param arg1 (CAL type: <code>Cal.Core.Prelude.Outputable a => a</code>)
		 *          the first message argument.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          a string formatted according to the message pattern found in the given bundle using the specified key,
		 * or a placeholder string if the key cannot be found.
		 */
		public static final SourceModel.Expr format1(SourceModel.Expr bundle, SourceModel.Expr key, SourceModel.Expr arg1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.format1), bundle, key, arg1});
		}

		/**
		 * @see #format1(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param bundle
		 * @param key
		 * @param arg1
		 * @return the SourceModel.Expr representing an application of format1
		 */
		public static final SourceModel.Expr format1(SourceModel.Expr bundle, java.lang.String key, SourceModel.Expr arg1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.format1), bundle, SourceModel.Expr.makeStringValue(key), arg1});
		}

		/**
		 * Name binding for function: format1.
		 * @see #format1(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName format1 = 
			QualifiedName.make(CAL_MessageFormat.MODULE_NAME, "format1");

		/**
		 * Returns a string formatted according to the message pattern found in the given bundle using the specified key.
		 * @param bundle (CAL type: <code>Cal.Utilities.StringProperties.StringResourceBundle</code>)
		 *          the string resource bundle.
		 * @param key (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the key to be looked up in the bundle.
		 * @param arg1 (CAL type: <code>Cal.Core.Prelude.Outputable a => a</code>)
		 *          the first message argument.
		 * @param arg2 (CAL type: <code>Cal.Core.Prelude.Outputable b => b</code>)
		 *          the second message argument.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          a string formatted according to the message pattern found in the given bundle using the specified key,
		 * or a placeholder string if the key cannot be found.
		 */
		public static final SourceModel.Expr format2(SourceModel.Expr bundle, SourceModel.Expr key, SourceModel.Expr arg1, SourceModel.Expr arg2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.format2), bundle, key, arg1, arg2});
		}

		/**
		 * @see #format2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param bundle
		 * @param key
		 * @param arg1
		 * @param arg2
		 * @return the SourceModel.Expr representing an application of format2
		 */
		public static final SourceModel.Expr format2(SourceModel.Expr bundle, java.lang.String key, SourceModel.Expr arg1, SourceModel.Expr arg2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.format2), bundle, SourceModel.Expr.makeStringValue(key), arg1, arg2});
		}

		/**
		 * Name binding for function: format2.
		 * @see #format2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName format2 = 
			QualifiedName.make(CAL_MessageFormat.MODULE_NAME, "format2");

		/**
		 * Returns a string formatted according to the message pattern found in the given bundle using the specified key.
		 * @param bundle (CAL type: <code>Cal.Utilities.StringProperties.StringResourceBundle</code>)
		 *          the string resource bundle.
		 * @param key (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the key to be looked up in the bundle.
		 * @param arg1 (CAL type: <code>Cal.Core.Prelude.Outputable a => a</code>)
		 *          the first message argument.
		 * @param arg2 (CAL type: <code>Cal.Core.Prelude.Outputable b => b</code>)
		 *          the second message argument.
		 * @param arg3 (CAL type: <code>Cal.Core.Prelude.Outputable c => c</code>)
		 *          the third message argument.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          a string formatted according to the message pattern found in the given bundle using the specified key,
		 * or a placeholder string if the key cannot be found.
		 */
		public static final SourceModel.Expr format3(SourceModel.Expr bundle, SourceModel.Expr key, SourceModel.Expr arg1, SourceModel.Expr arg2, SourceModel.Expr arg3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.format3), bundle, key, arg1, arg2, arg3});
		}

		/**
		 * @see #format3(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param bundle
		 * @param key
		 * @param arg1
		 * @param arg2
		 * @param arg3
		 * @return the SourceModel.Expr representing an application of format3
		 */
		public static final SourceModel.Expr format3(SourceModel.Expr bundle, java.lang.String key, SourceModel.Expr arg1, SourceModel.Expr arg2, SourceModel.Expr arg3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.format3), bundle, SourceModel.Expr.makeStringValue(key), arg1, arg2, arg3});
		}

		/**
		 * Name binding for function: format3.
		 * @see #format3(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName format3 = 
			QualifiedName.make(CAL_MessageFormat.MODULE_NAME, "format3");

		/**
		 * Returns a string formatted according to the message pattern found in the given bundle using the specified key.
		 * @param bundle (CAL type: <code>Cal.Utilities.StringProperties.StringResourceBundle</code>)
		 *          the string resource bundle.
		 * @param key (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the key to be looked up in the bundle.
		 * @param arg1 (CAL type: <code>Cal.Core.Prelude.Outputable a => a</code>)
		 *          the first message argument.
		 * @param arg2 (CAL type: <code>Cal.Core.Prelude.Outputable b => b</code>)
		 *          the second message argument.
		 * @param arg3 (CAL type: <code>Cal.Core.Prelude.Outputable c => c</code>)
		 *          the third message argument.
		 * @param arg4 (CAL type: <code>Cal.Core.Prelude.Outputable d => d</code>)
		 *          the fourth message argument.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          a string formatted according to the message pattern found in the given bundle using the specified key,
		 * or a placeholder string if the key cannot be found.
		 */
		public static final SourceModel.Expr format4(SourceModel.Expr bundle, SourceModel.Expr key, SourceModel.Expr arg1, SourceModel.Expr arg2, SourceModel.Expr arg3, SourceModel.Expr arg4) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.format4), bundle, key, arg1, arg2, arg3, arg4});
		}

		/**
		 * @see #format4(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param bundle
		 * @param key
		 * @param arg1
		 * @param arg2
		 * @param arg3
		 * @param arg4
		 * @return the SourceModel.Expr representing an application of format4
		 */
		public static final SourceModel.Expr format4(SourceModel.Expr bundle, java.lang.String key, SourceModel.Expr arg1, SourceModel.Expr arg2, SourceModel.Expr arg3, SourceModel.Expr arg4) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.format4), bundle, SourceModel.Expr.makeStringValue(key), arg1, arg2, arg3, arg4});
		}

		/**
		 * Name binding for function: format4.
		 * @see #format4(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName format4 = 
			QualifiedName.make(CAL_MessageFormat.MODULE_NAME, "format4");

		/**
		 * Returns a string formatted according to the message pattern found in the given bundle using the specified key.
		 * @param bundle (CAL type: <code>Cal.Utilities.StringProperties.StringResourceBundle</code>)
		 *          the string resource bundle.
		 * @param key (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the key to be looked up in the bundle.
		 * @param arg1 (CAL type: <code>Cal.Core.Prelude.Outputable a => a</code>)
		 *          the first message argument.
		 * @param arg2 (CAL type: <code>Cal.Core.Prelude.Outputable b => b</code>)
		 *          the second message argument.
		 * @param arg3 (CAL type: <code>Cal.Core.Prelude.Outputable c => c</code>)
		 *          the third message argument.
		 * @param arg4 (CAL type: <code>Cal.Core.Prelude.Outputable d => d</code>)
		 *          the fourth message argument.
		 * @param arg5 (CAL type: <code>Cal.Core.Prelude.Outputable e => e</code>)
		 *          the fifth message argument.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          a string formatted according to the message pattern found in the given bundle using the specified key,
		 * or a placeholder string if the key cannot be found.
		 */
		public static final SourceModel.Expr format5(SourceModel.Expr bundle, SourceModel.Expr key, SourceModel.Expr arg1, SourceModel.Expr arg2, SourceModel.Expr arg3, SourceModel.Expr arg4, SourceModel.Expr arg5) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.format5), bundle, key, arg1, arg2, arg3, arg4, arg5});
		}

		/**
		 * @see #format5(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param bundle
		 * @param key
		 * @param arg1
		 * @param arg2
		 * @param arg3
		 * @param arg4
		 * @param arg5
		 * @return the SourceModel.Expr representing an application of format5
		 */
		public static final SourceModel.Expr format5(SourceModel.Expr bundle, java.lang.String key, SourceModel.Expr arg1, SourceModel.Expr arg2, SourceModel.Expr arg3, SourceModel.Expr arg4, SourceModel.Expr arg5) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.format5), bundle, SourceModel.Expr.makeStringValue(key), arg1, arg2, arg3, arg4, arg5});
		}

		/**
		 * Name binding for function: format5.
		 * @see #format5(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName format5 = 
			QualifiedName.make(CAL_MessageFormat.MODULE_NAME, "format5");

		/**
		 * Returns a string formatted according to the message pattern found in the given bundle using the specified key.
		 * @param bundle (CAL type: <code>Cal.Utilities.StringProperties.StringResourceBundle</code>)
		 *          the string resource bundle.
		 * @param key (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the key to be looked up in the bundle.
		 * @param arg1 (CAL type: <code>Cal.Core.Prelude.Outputable a => a</code>)
		 *          the first message argument.
		 * @param arg2 (CAL type: <code>Cal.Core.Prelude.Outputable b => b</code>)
		 *          the second message argument.
		 * @param arg3 (CAL type: <code>Cal.Core.Prelude.Outputable c => c</code>)
		 *          the third message argument.
		 * @param arg4 (CAL type: <code>Cal.Core.Prelude.Outputable d => d</code>)
		 *          the fourth message argument.
		 * @param arg5 (CAL type: <code>Cal.Core.Prelude.Outputable e => e</code>)
		 *          the fifth message argument.
		 * @param arg6 (CAL type: <code>Cal.Core.Prelude.Outputable f => f</code>)
		 *          the sixth message argument.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          a string formatted according to the message pattern found in the given bundle using the specified key,
		 * or a placeholder string if the key cannot be found.
		 */
		public static final SourceModel.Expr format6(SourceModel.Expr bundle, SourceModel.Expr key, SourceModel.Expr arg1, SourceModel.Expr arg2, SourceModel.Expr arg3, SourceModel.Expr arg4, SourceModel.Expr arg5, SourceModel.Expr arg6) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.format6), bundle, key, arg1, arg2, arg3, arg4, arg5, arg6});
		}

		/**
		 * @see #format6(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param bundle
		 * @param key
		 * @param arg1
		 * @param arg2
		 * @param arg3
		 * @param arg4
		 * @param arg5
		 * @param arg6
		 * @return the SourceModel.Expr representing an application of format6
		 */
		public static final SourceModel.Expr format6(SourceModel.Expr bundle, java.lang.String key, SourceModel.Expr arg1, SourceModel.Expr arg2, SourceModel.Expr arg3, SourceModel.Expr arg4, SourceModel.Expr arg5, SourceModel.Expr arg6) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.format6), bundle, SourceModel.Expr.makeStringValue(key), arg1, arg2, arg3, arg4, arg5, arg6});
		}

		/**
		 * Name binding for function: format6.
		 * @see #format6(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName format6 = 
			QualifiedName.make(CAL_MessageFormat.MODULE_NAME, "format6");

		/**
		 * Returns a string formatted according to the message pattern found in the given bundle using the specified key.
		 * @param bundle (CAL type: <code>Cal.Utilities.StringProperties.StringResourceBundle</code>)
		 *          the string resource bundle.
		 * @param key (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the key to be looked up in the bundle.
		 * @param arg1 (CAL type: <code>Cal.Core.Prelude.Outputable a => a</code>)
		 *          the first message argument.
		 * @param arg2 (CAL type: <code>Cal.Core.Prelude.Outputable b => b</code>)
		 *          the second message argument.
		 * @param arg3 (CAL type: <code>Cal.Core.Prelude.Outputable c => c</code>)
		 *          the third message argument.
		 * @param arg4 (CAL type: <code>Cal.Core.Prelude.Outputable d => d</code>)
		 *          the fourth message argument.
		 * @param arg5 (CAL type: <code>Cal.Core.Prelude.Outputable e => e</code>)
		 *          the fifth message argument.
		 * @param arg6 (CAL type: <code>Cal.Core.Prelude.Outputable f => f</code>)
		 *          the sixth message argument.
		 * @param arg7 (CAL type: <code>Cal.Core.Prelude.Outputable g => g</code>)
		 *          the seventh message argument.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          a string formatted according to the message pattern found in the given bundle using the specified key,
		 * or a placeholder string if the key cannot be found.
		 */
		public static final SourceModel.Expr format7(SourceModel.Expr bundle, SourceModel.Expr key, SourceModel.Expr arg1, SourceModel.Expr arg2, SourceModel.Expr arg3, SourceModel.Expr arg4, SourceModel.Expr arg5, SourceModel.Expr arg6, SourceModel.Expr arg7) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.format7), bundle, key, arg1, arg2, arg3, arg4, arg5, arg6, arg7});
		}

		/**
		 * @see #format7(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param bundle
		 * @param key
		 * @param arg1
		 * @param arg2
		 * @param arg3
		 * @param arg4
		 * @param arg5
		 * @param arg6
		 * @param arg7
		 * @return the SourceModel.Expr representing an application of format7
		 */
		public static final SourceModel.Expr format7(SourceModel.Expr bundle, java.lang.String key, SourceModel.Expr arg1, SourceModel.Expr arg2, SourceModel.Expr arg3, SourceModel.Expr arg4, SourceModel.Expr arg5, SourceModel.Expr arg6, SourceModel.Expr arg7) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.format7), bundle, SourceModel.Expr.makeStringValue(key), arg1, arg2, arg3, arg4, arg5, arg6, arg7});
		}

		/**
		 * Name binding for function: format7.
		 * @see #format7(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName format7 = 
			QualifiedName.make(CAL_MessageFormat.MODULE_NAME, "format7");

		/**
		 * Returns the given number formatted as a currency value.
		 * @param locale (CAL type: <code>Cal.Utilities.Locale.Locale</code>)
		 *          the locale to use for formatting.
		 * @param number (CAL type: <code>(Cal.Core.Prelude.Num a, Cal.Core.Prelude.Outputable a) => a</code>)
		 *          the number to format.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the given number formatted as a currency value.
		 */
		public static final SourceModel.Expr formatCurrency(SourceModel.Expr locale, SourceModel.Expr number) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.formatCurrency), locale, number});
		}

		/**
		 * Name binding for function: formatCurrency.
		 * @see #formatCurrency(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName formatCurrency = 
			QualifiedName.make(CAL_MessageFormat.MODULE_NAME, "formatCurrency");

		/**
		 * Returns a string formatted according to the message pattern found in the given bundle using the specified key.
		 * @param bundle (CAL type: <code>Cal.Utilities.StringProperties.StringResourceBundle</code>)
		 *          the string resource bundle.
		 * @param key (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the key to be looked up in the bundle.
		 * @param args (CAL type: <code>[Cal.Core.Prelude.JObject]</code>)
		 *          the message arguments.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          a string formatted according to the message pattern found in the given bundle using the specified key,
		 * or a placeholder string if the key cannot be found.
		 */
		public static final SourceModel.Expr formatForArgList(SourceModel.Expr bundle, SourceModel.Expr key, SourceModel.Expr args) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.formatForArgList), bundle, key, args});
		}

		/**
		 * @see #formatForArgList(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param bundle
		 * @param key
		 * @param args
		 * @return the SourceModel.Expr representing an application of formatForArgList
		 */
		public static final SourceModel.Expr formatForArgList(SourceModel.Expr bundle, java.lang.String key, SourceModel.Expr args) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.formatForArgList), bundle, SourceModel.Expr.makeStringValue(key), args});
		}

		/**
		 * Name binding for function: formatForArgList.
		 * @see #formatForArgList(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName formatForArgList = 
			QualifiedName.make(
				CAL_MessageFormat.MODULE_NAME, 
				"formatForArgList");

		/**
		 * Returns a formatted string for the given number.
		 * @param locale (CAL type: <code>Cal.Utilities.Locale.Locale</code>)
		 *          the locale to use for formatting.
		 * @param number (CAL type: <code>(Cal.Core.Prelude.Num a, Cal.Core.Prelude.Outputable a) => a</code>)
		 *          the number to format.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          a formatted string for the given number.
		 */
		public static final SourceModel.Expr formatNumber(SourceModel.Expr locale, SourceModel.Expr number) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.formatNumber), locale, number});
		}

		/**
		 * Name binding for function: formatNumber.
		 * @see #formatNumber(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName formatNumber = 
			QualifiedName.make(CAL_MessageFormat.MODULE_NAME, "formatNumber");

		/**
		 * Returns the given number formatted by the specified pattern.
		 * @param decimalPattern (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the decimal pattern to use for formatting the number.
		 * @param locale (CAL type: <code>Cal.Utilities.Locale.Locale</code>)
		 *          the locale to use for formatting.
		 * @param number (CAL type: <code>(Cal.Core.Prelude.Num a, Cal.Core.Prelude.Outputable a) => a</code>)
		 *          the number to format.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the given number formatted by the specified pattern.
		 */
		public static final SourceModel.Expr formatNumberWithPattern(SourceModel.Expr decimalPattern, SourceModel.Expr locale, SourceModel.Expr number) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.formatNumberWithPattern), decimalPattern, locale, number});
		}

		/**
		 * @see #formatNumberWithPattern(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param decimalPattern
		 * @param locale
		 * @param number
		 * @return the SourceModel.Expr representing an application of formatNumberWithPattern
		 */
		public static final SourceModel.Expr formatNumberWithPattern(java.lang.String decimalPattern, SourceModel.Expr locale, SourceModel.Expr number) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.formatNumberWithPattern), SourceModel.Expr.makeStringValue(decimalPattern), locale, number});
		}

		/**
		 * Name binding for function: formatNumberWithPattern.
		 * @see #formatNumberWithPattern(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName formatNumberWithPattern = 
			QualifiedName.make(
				CAL_MessageFormat.MODULE_NAME, 
				"formatNumberWithPattern");

		/**
		 * Returns the given number formatted as a percent value.
		 * @param locale (CAL type: <code>Cal.Utilities.Locale.Locale</code>)
		 *          the locale to use for formatting.
		 * @param number (CAL type: <code>(Cal.Core.Prelude.Num a, Cal.Core.Prelude.Outputable a) => a</code>)
		 *          the number to format.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the given number formatted as a percent value.
		 */
		public static final SourceModel.Expr formatPercent(SourceModel.Expr locale, SourceModel.Expr number) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.formatPercent), locale, number});
		}

		/**
		 * Name binding for function: formatPercent.
		 * @see #formatPercent(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName formatPercent = 
			QualifiedName.make(CAL_MessageFormat.MODULE_NAME, "formatPercent");

		/**
		 * Returns a string formatted according to the specified message pattern.
		 * The arguments are provided in a record (which can be a tuple), and is treated as ordered by the record's field names
		 * (i.e. ordinal fields first, in ascending order, then the textual fields in lexicographical order).
		 * @param pattern (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the message pattern.
		 * @param locale (CAL type: <code>Cal.Utilities.Locale.Locale</code>)
		 *          the locale to use for formatting.
		 * @param args (CAL type: <code>Cal.Core.Prelude.Outputable r => {r}</code>)
		 *          the message arguments, ordered by the record's field names.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          a string formatted according to the message pattern.
		 */
		public static final SourceModel.Expr formatWithPattern(SourceModel.Expr pattern, SourceModel.Expr locale, SourceModel.Expr args) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.formatWithPattern), pattern, locale, args});
		}

		/**
		 * @see #formatWithPattern(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param pattern
		 * @param locale
		 * @param args
		 * @return the SourceModel.Expr representing an application of formatWithPattern
		 */
		public static final SourceModel.Expr formatWithPattern(java.lang.String pattern, SourceModel.Expr locale, SourceModel.Expr args) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.formatWithPattern), SourceModel.Expr.makeStringValue(pattern), locale, args});
		}

		/**
		 * Name binding for function: formatWithPattern.
		 * @see #formatWithPattern(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName formatWithPattern = 
			QualifiedName.make(
				CAL_MessageFormat.MODULE_NAME, 
				"formatWithPattern");

		/**
		 * Returns a string formatted according to the specified message pattern.
		 * @param pattern (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the message pattern.
		 * @param locale (CAL type: <code>Cal.Utilities.Locale.Locale</code>)
		 *          the locale to use for formatting.
		 * @param arg1 (CAL type: <code>Cal.Core.Prelude.Outputable a => a</code>)
		 *          the first message argument.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          a string formatted according to the message pattern.
		 */
		public static final SourceModel.Expr formatWithPattern1(SourceModel.Expr pattern, SourceModel.Expr locale, SourceModel.Expr arg1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.formatWithPattern1), pattern, locale, arg1});
		}

		/**
		 * @see #formatWithPattern1(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param pattern
		 * @param locale
		 * @param arg1
		 * @return the SourceModel.Expr representing an application of formatWithPattern1
		 */
		public static final SourceModel.Expr formatWithPattern1(java.lang.String pattern, SourceModel.Expr locale, SourceModel.Expr arg1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.formatWithPattern1), SourceModel.Expr.makeStringValue(pattern), locale, arg1});
		}

		/**
		 * Name binding for function: formatWithPattern1.
		 * @see #formatWithPattern1(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName formatWithPattern1 = 
			QualifiedName.make(
				CAL_MessageFormat.MODULE_NAME, 
				"formatWithPattern1");

		/**
		 * Returns a string formatted according to the specified message pattern.
		 * @param pattern (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the message pattern.
		 * @param locale (CAL type: <code>Cal.Utilities.Locale.Locale</code>)
		 *          the locale to use for formatting.
		 * @param arg1 (CAL type: <code>Cal.Core.Prelude.Outputable a => a</code>)
		 *          the first message argument.
		 * @param arg2 (CAL type: <code>Cal.Core.Prelude.Outputable b => b</code>)
		 *          the second message argument.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          a string formatted according to the message pattern.
		 */
		public static final SourceModel.Expr formatWithPattern2(SourceModel.Expr pattern, SourceModel.Expr locale, SourceModel.Expr arg1, SourceModel.Expr arg2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.formatWithPattern2), pattern, locale, arg1, arg2});
		}

		/**
		 * @see #formatWithPattern2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param pattern
		 * @param locale
		 * @param arg1
		 * @param arg2
		 * @return the SourceModel.Expr representing an application of formatWithPattern2
		 */
		public static final SourceModel.Expr formatWithPattern2(java.lang.String pattern, SourceModel.Expr locale, SourceModel.Expr arg1, SourceModel.Expr arg2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.formatWithPattern2), SourceModel.Expr.makeStringValue(pattern), locale, arg1, arg2});
		}

		/**
		 * Name binding for function: formatWithPattern2.
		 * @see #formatWithPattern2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName formatWithPattern2 = 
			QualifiedName.make(
				CAL_MessageFormat.MODULE_NAME, 
				"formatWithPattern2");

		/**
		 * Returns a string formatted according to the specified message pattern.
		 * @param pattern (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the message pattern.
		 * @param locale (CAL type: <code>Cal.Utilities.Locale.Locale</code>)
		 *          the locale to use for formatting.
		 * @param arg1 (CAL type: <code>Cal.Core.Prelude.Outputable a => a</code>)
		 *          the first message argument.
		 * @param arg2 (CAL type: <code>Cal.Core.Prelude.Outputable b => b</code>)
		 *          the second message argument.
		 * @param arg3 (CAL type: <code>Cal.Core.Prelude.Outputable c => c</code>)
		 *          the third message argument.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          a string formatted according to the message pattern.
		 */
		public static final SourceModel.Expr formatWithPattern3(SourceModel.Expr pattern, SourceModel.Expr locale, SourceModel.Expr arg1, SourceModel.Expr arg2, SourceModel.Expr arg3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.formatWithPattern3), pattern, locale, arg1, arg2, arg3});
		}

		/**
		 * @see #formatWithPattern3(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param pattern
		 * @param locale
		 * @param arg1
		 * @param arg2
		 * @param arg3
		 * @return the SourceModel.Expr representing an application of formatWithPattern3
		 */
		public static final SourceModel.Expr formatWithPattern3(java.lang.String pattern, SourceModel.Expr locale, SourceModel.Expr arg1, SourceModel.Expr arg2, SourceModel.Expr arg3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.formatWithPattern3), SourceModel.Expr.makeStringValue(pattern), locale, arg1, arg2, arg3});
		}

		/**
		 * Name binding for function: formatWithPattern3.
		 * @see #formatWithPattern3(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName formatWithPattern3 = 
			QualifiedName.make(
				CAL_MessageFormat.MODULE_NAME, 
				"formatWithPattern3");

		/**
		 * Returns a string formatted according to the specified message pattern.
		 * @param pattern (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the message pattern.
		 * @param locale (CAL type: <code>Cal.Utilities.Locale.Locale</code>)
		 *          the locale to use for formatting.
		 * @param arg1 (CAL type: <code>Cal.Core.Prelude.Outputable a => a</code>)
		 *          the first message argument.
		 * @param arg2 (CAL type: <code>Cal.Core.Prelude.Outputable b => b</code>)
		 *          the second message argument.
		 * @param arg3 (CAL type: <code>Cal.Core.Prelude.Outputable c => c</code>)
		 *          the third message argument.
		 * @param arg4 (CAL type: <code>Cal.Core.Prelude.Outputable d => d</code>)
		 *          the fourth message argument.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          a string formatted according to the message pattern.
		 */
		public static final SourceModel.Expr formatWithPattern4(SourceModel.Expr pattern, SourceModel.Expr locale, SourceModel.Expr arg1, SourceModel.Expr arg2, SourceModel.Expr arg3, SourceModel.Expr arg4) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.formatWithPattern4), pattern, locale, arg1, arg2, arg3, arg4});
		}

		/**
		 * @see #formatWithPattern4(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param pattern
		 * @param locale
		 * @param arg1
		 * @param arg2
		 * @param arg3
		 * @param arg4
		 * @return the SourceModel.Expr representing an application of formatWithPattern4
		 */
		public static final SourceModel.Expr formatWithPattern4(java.lang.String pattern, SourceModel.Expr locale, SourceModel.Expr arg1, SourceModel.Expr arg2, SourceModel.Expr arg3, SourceModel.Expr arg4) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.formatWithPattern4), SourceModel.Expr.makeStringValue(pattern), locale, arg1, arg2, arg3, arg4});
		}

		/**
		 * Name binding for function: formatWithPattern4.
		 * @see #formatWithPattern4(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName formatWithPattern4 = 
			QualifiedName.make(
				CAL_MessageFormat.MODULE_NAME, 
				"formatWithPattern4");

		/**
		 * Returns a string formatted according to the specified message pattern.
		 * @param pattern (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the message pattern.
		 * @param locale (CAL type: <code>Cal.Utilities.Locale.Locale</code>)
		 *          the locale to use for formatting.
		 * @param arg1 (CAL type: <code>Cal.Core.Prelude.Outputable a => a</code>)
		 *          the first message argument.
		 * @param arg2 (CAL type: <code>Cal.Core.Prelude.Outputable b => b</code>)
		 *          the second message argument.
		 * @param arg3 (CAL type: <code>Cal.Core.Prelude.Outputable c => c</code>)
		 *          the third message argument.
		 * @param arg4 (CAL type: <code>Cal.Core.Prelude.Outputable d => d</code>)
		 *          the fourth message argument.
		 * @param arg5 (CAL type: <code>Cal.Core.Prelude.Outputable e => e</code>)
		 *          the fifth message argument.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          a string formatted according to the message pattern.
		 */
		public static final SourceModel.Expr formatWithPattern5(SourceModel.Expr pattern, SourceModel.Expr locale, SourceModel.Expr arg1, SourceModel.Expr arg2, SourceModel.Expr arg3, SourceModel.Expr arg4, SourceModel.Expr arg5) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.formatWithPattern5), pattern, locale, arg1, arg2, arg3, arg4, arg5});
		}

		/**
		 * @see #formatWithPattern5(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param pattern
		 * @param locale
		 * @param arg1
		 * @param arg2
		 * @param arg3
		 * @param arg4
		 * @param arg5
		 * @return the SourceModel.Expr representing an application of formatWithPattern5
		 */
		public static final SourceModel.Expr formatWithPattern5(java.lang.String pattern, SourceModel.Expr locale, SourceModel.Expr arg1, SourceModel.Expr arg2, SourceModel.Expr arg3, SourceModel.Expr arg4, SourceModel.Expr arg5) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.formatWithPattern5), SourceModel.Expr.makeStringValue(pattern), locale, arg1, arg2, arg3, arg4, arg5});
		}

		/**
		 * Name binding for function: formatWithPattern5.
		 * @see #formatWithPattern5(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName formatWithPattern5 = 
			QualifiedName.make(
				CAL_MessageFormat.MODULE_NAME, 
				"formatWithPattern5");

		/**
		 * Returns a string formatted according to the specified message pattern.
		 * @param pattern (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the message pattern.
		 * @param locale (CAL type: <code>Cal.Utilities.Locale.Locale</code>)
		 *          the locale to use for formatting.
		 * @param arg1 (CAL type: <code>Cal.Core.Prelude.Outputable a => a</code>)
		 *          the first message argument.
		 * @param arg2 (CAL type: <code>Cal.Core.Prelude.Outputable b => b</code>)
		 *          the second message argument.
		 * @param arg3 (CAL type: <code>Cal.Core.Prelude.Outputable c => c</code>)
		 *          the third message argument.
		 * @param arg4 (CAL type: <code>Cal.Core.Prelude.Outputable d => d</code>)
		 *          the fourth message argument.
		 * @param arg5 (CAL type: <code>Cal.Core.Prelude.Outputable e => e</code>)
		 *          the fifth message argument.
		 * @param arg6 (CAL type: <code>Cal.Core.Prelude.Outputable f => f</code>)
		 *          the sixth message argument.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          a string formatted according to the message pattern.
		 */
		public static final SourceModel.Expr formatWithPattern6(SourceModel.Expr pattern, SourceModel.Expr locale, SourceModel.Expr arg1, SourceModel.Expr arg2, SourceModel.Expr arg3, SourceModel.Expr arg4, SourceModel.Expr arg5, SourceModel.Expr arg6) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.formatWithPattern6), pattern, locale, arg1, arg2, arg3, arg4, arg5, arg6});
		}

		/**
		 * @see #formatWithPattern6(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param pattern
		 * @param locale
		 * @param arg1
		 * @param arg2
		 * @param arg3
		 * @param arg4
		 * @param arg5
		 * @param arg6
		 * @return the SourceModel.Expr representing an application of formatWithPattern6
		 */
		public static final SourceModel.Expr formatWithPattern6(java.lang.String pattern, SourceModel.Expr locale, SourceModel.Expr arg1, SourceModel.Expr arg2, SourceModel.Expr arg3, SourceModel.Expr arg4, SourceModel.Expr arg5, SourceModel.Expr arg6) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.formatWithPattern6), SourceModel.Expr.makeStringValue(pattern), locale, arg1, arg2, arg3, arg4, arg5, arg6});
		}

		/**
		 * Name binding for function: formatWithPattern6.
		 * @see #formatWithPattern6(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName formatWithPattern6 = 
			QualifiedName.make(
				CAL_MessageFormat.MODULE_NAME, 
				"formatWithPattern6");

		/**
		 * Returns a string formatted according to the specified message pattern.
		 * @param pattern (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the message pattern.
		 * @param locale (CAL type: <code>Cal.Utilities.Locale.Locale</code>)
		 *          the locale to use for formatting.
		 * @param arg1 (CAL type: <code>Cal.Core.Prelude.Outputable a => a</code>)
		 *          the first message argument.
		 * @param arg2 (CAL type: <code>Cal.Core.Prelude.Outputable b => b</code>)
		 *          the second message argument.
		 * @param arg3 (CAL type: <code>Cal.Core.Prelude.Outputable c => c</code>)
		 *          the third message argument.
		 * @param arg4 (CAL type: <code>Cal.Core.Prelude.Outputable d => d</code>)
		 *          the fourth message argument.
		 * @param arg5 (CAL type: <code>Cal.Core.Prelude.Outputable e => e</code>)
		 *          the fifth message argument.
		 * @param arg6 (CAL type: <code>Cal.Core.Prelude.Outputable f => f</code>)
		 *          the sixth message argument.
		 * @param arg7 (CAL type: <code>Cal.Core.Prelude.Outputable g => g</code>)
		 *          the seventh message argument.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          a string formatted according to the message pattern.
		 */
		public static final SourceModel.Expr formatWithPattern7(SourceModel.Expr pattern, SourceModel.Expr locale, SourceModel.Expr arg1, SourceModel.Expr arg2, SourceModel.Expr arg3, SourceModel.Expr arg4, SourceModel.Expr arg5, SourceModel.Expr arg6, SourceModel.Expr arg7) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.formatWithPattern7), pattern, locale, arg1, arg2, arg3, arg4, arg5, arg6, arg7});
		}

		/**
		 * @see #formatWithPattern7(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param pattern
		 * @param locale
		 * @param arg1
		 * @param arg2
		 * @param arg3
		 * @param arg4
		 * @param arg5
		 * @param arg6
		 * @param arg7
		 * @return the SourceModel.Expr representing an application of formatWithPattern7
		 */
		public static final SourceModel.Expr formatWithPattern7(java.lang.String pattern, SourceModel.Expr locale, SourceModel.Expr arg1, SourceModel.Expr arg2, SourceModel.Expr arg3, SourceModel.Expr arg4, SourceModel.Expr arg5, SourceModel.Expr arg6, SourceModel.Expr arg7) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.formatWithPattern7), SourceModel.Expr.makeStringValue(pattern), locale, arg1, arg2, arg3, arg4, arg5, arg6, arg7});
		}

		/**
		 * Name binding for function: formatWithPattern7.
		 * @see #formatWithPattern7(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName formatWithPattern7 = 
			QualifiedName.make(
				CAL_MessageFormat.MODULE_NAME, 
				"formatWithPattern7");

		/**
		 * Returns a string formatted according to the specified message pattern.
		 * @param pattern (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the message pattern.
		 * @param locale (CAL type: <code>Cal.Utilities.Locale.Locale</code>)
		 *          the locale to use for formatting.
		 * @param args (CAL type: <code>[Cal.Core.Prelude.JObject]</code>)
		 *          the message arguments.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          a string formatted according to the message pattern.
		 */
		public static final SourceModel.Expr formatWithPatternForArgList(SourceModel.Expr pattern, SourceModel.Expr locale, SourceModel.Expr args) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.formatWithPatternForArgList), pattern, locale, args});
		}

		/**
		 * @see #formatWithPatternForArgList(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param pattern
		 * @param locale
		 * @param args
		 * @return the SourceModel.Expr representing an application of formatWithPatternForArgList
		 */
		public static final SourceModel.Expr formatWithPatternForArgList(java.lang.String pattern, SourceModel.Expr locale, SourceModel.Expr args) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.formatWithPatternForArgList), SourceModel.Expr.makeStringValue(pattern), locale, args});
		}

		/**
		 * Name binding for function: formatWithPatternForArgList.
		 * @see #formatWithPatternForArgList(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName formatWithPatternForArgList = 
			QualifiedName.make(
				CAL_MessageFormat.MODULE_NAME, 
				"formatWithPatternForArgList");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = 916040373;

}
