/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_XmlCharacterClasses.java)
 * was generated from CAL module: Cal.Experimental.Utilities.XmlCharacterClasses.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Experimental.Utilities.XmlCharacterClasses module from Java code.
 *  
 * Creation date: Tue Oct 16 15:42:49 PDT 2007
 * --!>
 *  
 */

package org.openquark.cal.module.Cal.Experimental.Utilities;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * This module provides optimized functions for testing whether characters match the
 * productions in Appendix B, Character Classes, of the XML 1.0 specification.
 * @author Malcolm Sharpe
 */
public final class CAL_XmlCharacterClasses {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Experimental.Utilities.XmlCharacterClasses");

	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Experimental.Utilities.XmlCharacterClasses module.
	 */
	public static final class Functions {
		/**
		 * Return true if and only if the given character matches the BaseChar production.
		 * @param arg_1 (CAL type: <code>Cal.Core.Prelude.Char</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr isBaseChar(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isBaseChar), arg_1});
		}

		/**
		 * @see #isBaseChar(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of isBaseChar
		 */
		public static final SourceModel.Expr isBaseChar(char arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isBaseChar), SourceModel.Expr.makeCharValue(arg_1)});
		}

		/**
		 * Name binding for function: isBaseChar.
		 * @see #isBaseChar(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isBaseChar = 
			QualifiedName.make(
				CAL_XmlCharacterClasses.MODULE_NAME, 
				"isBaseChar");

		/**
		 * Return true if and only if the given character matches the BaseChar production.
		 * @param arg_1 (CAL type: <code>Cal.Core.Prelude.Char</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr isCombiningChar(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isCombiningChar), arg_1});
		}

		/**
		 * @see #isCombiningChar(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of isCombiningChar
		 */
		public static final SourceModel.Expr isCombiningChar(char arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isCombiningChar), SourceModel.Expr.makeCharValue(arg_1)});
		}

		/**
		 * Name binding for function: isCombiningChar.
		 * @see #isCombiningChar(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isCombiningChar = 
			QualifiedName.make(
				CAL_XmlCharacterClasses.MODULE_NAME, 
				"isCombiningChar");

		/**
		 * Return true if and only if the given character matches the BaseChar production.
		 * @param arg_1 (CAL type: <code>Cal.Core.Prelude.Char</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr isDigit(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isDigit), arg_1});
		}

		/**
		 * @see #isDigit(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of isDigit
		 */
		public static final SourceModel.Expr isDigit(char arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isDigit), SourceModel.Expr.makeCharValue(arg_1)});
		}

		/**
		 * Name binding for function: isDigit.
		 * @see #isDigit(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isDigit = 
			QualifiedName.make(CAL_XmlCharacterClasses.MODULE_NAME, "isDigit");

		/**
		 * Return true if and only if the given character matches the BaseChar production.
		 * @param arg_1 (CAL type: <code>Cal.Core.Prelude.Char</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr isExtender(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isExtender), arg_1});
		}

		/**
		 * @see #isExtender(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of isExtender
		 */
		public static final SourceModel.Expr isExtender(char arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isExtender), SourceModel.Expr.makeCharValue(arg_1)});
		}

		/**
		 * Name binding for function: isExtender.
		 * @see #isExtender(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isExtender = 
			QualifiedName.make(
				CAL_XmlCharacterClasses.MODULE_NAME, 
				"isExtender");

		/**
		 * Return true if and only if the given character matches the BaseChar production.
		 * @param arg_1 (CAL type: <code>Cal.Core.Prelude.Char</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr isIdeographic(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isIdeographic), arg_1});
		}

		/**
		 * @see #isIdeographic(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of isIdeographic
		 */
		public static final SourceModel.Expr isIdeographic(char arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isIdeographic), SourceModel.Expr.makeCharValue(arg_1)});
		}

		/**
		 * Name binding for function: isIdeographic.
		 * @see #isIdeographic(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isIdeographic = 
			QualifiedName.make(
				CAL_XmlCharacterClasses.MODULE_NAME, 
				"isIdeographic");

		/**
		 * Return true if and only if the given character matches the Letter production.
		 * @param c (CAL type: <code>Cal.Core.Prelude.Char</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr isLetter(SourceModel.Expr c) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isLetter), c});
		}

		/**
		 * @see #isLetter(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param c
		 * @return the SourceModel.Expr representing an application of isLetter
		 */
		public static final SourceModel.Expr isLetter(char c) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isLetter), SourceModel.Expr.makeCharValue(c)});
		}

		/**
		 * Name binding for function: isLetter.
		 * @see #isLetter(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isLetter = 
			QualifiedName.make(CAL_XmlCharacterClasses.MODULE_NAME, "isLetter");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = -704631070;

}
