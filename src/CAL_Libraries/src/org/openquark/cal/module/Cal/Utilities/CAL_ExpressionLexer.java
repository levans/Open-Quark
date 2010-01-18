/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_ExpressionLexer.java)
 * was generated from CAL module: Cal.Utilities.ExpressionLexer.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Utilities.ExpressionLexer module from Java code.
 *  
 * Creation date: Tue Aug 28 15:58:39 PDT 2007
 * --!>
 *  
 */

package org.openquark.cal.module.Cal.Utilities;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * This module implements a lexer for general expressions, based on the module <code>Cal.Utilities.Parser</code>.
 * <p>
 * The function <code>Cal.Utilities.ExpressionLexer.tokenStream</code> returns a parser that accepts a list of <code>Cal.Core.Prelude.Char</code> and produces a list of <code>Cal.Utilities.ExpressionLexer.Token</code>.
 * <p>
 * For example (from the module <code>Cal.Data.SqlParser</code>):
 * <p>
 * 
 * <pre> identifierNonAlphaChars = ['_', '#', '$'];
 *  
 *  sqlKeywords = [
 *      "SELECT",
 *      "FROM",
 *      "WHERE",
 *      // etc... 
 *      ];
 *      
 *  sqlFunctionNames = [
 *      "ABS",
 *      "ACOS",
 *      "ATAN",
 *      // etc...
 *      ];
 *      
 *  specialCharSequences = [
 *     "(+)",
 *     "*=*",
 *     "*=",
 *     "=*",
 *     // etc...
 *     ];
 *     
 *  identifierQuoteChars = [('[', ']'),
 *                         ('"', '"'),
 *                         ('`', '`')];
 * 
 *  lexer = Cal.Utilities.ExpressionLexer.tokenStream identifierNonAlphaChars sqlKeywords sqlFunctionNames specialCharSequences identifierQuoteChars;</pre>
 * 
 * <p>
 * The result of a successful call to this lexer can be used as input to a higher-level parser that accepts <code>Cal.Utilities.ExpressionLexer.Token</code>s as input.
 * <p>
 * The module also provides a number of functions that can be used to recognize different kinds of tokens. 
 * For example:
 * <ul>
 *  <li>
 *   <code>Cal.Utilities.ExpressionLexer.keywordToken</code> - recognizes a keyword in the list supplied to <code>Cal.Utilities.ExpressionLexer.tokenStream</code>
 *  </li>
 *  <li>
 *   <code>Cal.Utilities.ExpressionLexer.functionToken</code> - recognizes a funnction in the list supplied to <code>Cal.Utilities.ExpressionLexer.tokenStream</code>
 *  </li>
 *  <li>
 *   <code>Cal.Utilities.ExpressionLexer.specialCharToken</code> - recognizes a special character
 *  </li>
 *  <li>
 *   <code>Cal.Utilities.ExpressionLexer.specialCharSequenceToken</code> - recognizes a special character sequence
 *  </li>
 *  <li>
 *   <code>Cal.Utilities.ExpressionLexer.nameToken</code> - recognizes an identifier
 *  </li>
 *  <li>
 *   <code>Cal.Utilities.ExpressionLexer.stringToken</code> - recognizes a string literal
 *  </li>
 *  <li>
 *   <code>Cal.Utilities.ExpressionLexer.numberToken</code> - recognizes a numeric literal
 *  </li>
 *  <li>
 *   <code>Cal.Utilities.ExpressionLexer.ampersandToken</code> - recognizes an ampersand - there are several similar functions for special characters
 *  </li>
 * </ul>
 * <p>
 * For example:
 * <p>
 * 
 * <pre> // a table name is one or more identifiers separated by dots
 *  table_name_parts = sepBy1 nameToken dotToken;
 * </pre>
 * 
 * 
 * @author Luke Evans
 * @author Greg McClement
 */
public final class CAL_ExpressionLexer {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Utilities.ExpressionLexer");

	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Utilities.ExpressionLexer module.
	 */
	public static final class TypeConstructors {
		/**
		 * A <code>Token</code> is a typed lexeme represented a 'part of speech' in the source language
		 * Each <code>Token</code> holds its <code>Cal.Utilities.ExpressionLexer.TokenImage</code>
		 */
		public static final QualifiedName Token = 
			QualifiedName.make(CAL_ExpressionLexer.MODULE_NAME, "Token");

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Utilities.ExpressionLexer module.
	 */
	public static final class Functions {
		/**
		 * Helper binding method for function: ampersandToken. 
		 * @return the SourceModule.expr representing an application of ampersandToken
		 */
		public static final SourceModel.Expr ampersandToken() {
			return SourceModel.Expr.Var.make(Functions.ampersandToken);
		}

		/**
		 * Name binding for function: ampersandToken.
		 * @see #ampersandToken()
		 */
		public static final QualifiedName ampersandToken = 
			QualifiedName.make(
				CAL_ExpressionLexer.MODULE_NAME, 
				"ampersandToken");

		/**
		 * Helper binding method for function: asteriskToken. 
		 * @return the SourceModule.expr representing an application of asteriskToken
		 */
		public static final SourceModel.Expr asteriskToken() {
			return SourceModel.Expr.Var.make(Functions.asteriskToken);
		}

		/**
		 * Name binding for function: asteriskToken.
		 * @see #asteriskToken()
		 */
		public static final QualifiedName asteriskToken = 
			QualifiedName.make(CAL_ExpressionLexer.MODULE_NAME, "asteriskToken");

		/**
		 * Helper binding method for function: booleanToken. 
		 * @return the SourceModule.expr representing an application of booleanToken
		 */
		public static final SourceModel.Expr booleanToken() {
			return SourceModel.Expr.Var.make(Functions.booleanToken);
		}

		/**
		 * Name binding for function: booleanToken.
		 * @see #booleanToken()
		 */
		public static final QualifiedName booleanToken = 
			QualifiedName.make(CAL_ExpressionLexer.MODULE_NAME, "booleanToken");

		/**
		 * Gets the boolean value from a boolean token.
		 * @param token (CAL type: <code>Cal.Utilities.ExpressionLexer.Token</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr booleanTokenValue(SourceModel.Expr token) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanTokenValue), token});
		}

		/**
		 * Name binding for function: booleanTokenValue.
		 * @see #booleanTokenValue(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName booleanTokenValue = 
			QualifiedName.make(
				CAL_ExpressionLexer.MODULE_NAME, 
				"booleanTokenValue");

		/**
		 * Helper binding method for function: caretToken. 
		 * @return the SourceModule.expr representing an application of caretToken
		 */
		public static final SourceModel.Expr caretToken() {
			return SourceModel.Expr.Var.make(Functions.caretToken);
		}

		/**
		 * Name binding for function: caretToken.
		 * @see #caretToken()
		 */
		public static final QualifiedName caretToken = 
			QualifiedName.make(CAL_ExpressionLexer.MODULE_NAME, "caretToken");

		/**
		 * Helper binding method for function: closeBraceToken. 
		 * @return the SourceModule.expr representing an application of closeBraceToken
		 */
		public static final SourceModel.Expr closeBraceToken() {
			return SourceModel.Expr.Var.make(Functions.closeBraceToken);
		}

		/**
		 * Name binding for function: closeBraceToken.
		 * @see #closeBraceToken()
		 */
		public static final QualifiedName closeBraceToken = 
			QualifiedName.make(
				CAL_ExpressionLexer.MODULE_NAME, 
				"closeBraceToken");

		/**
		 * Helper binding method for function: closeParenToken. 
		 * @return the SourceModule.expr representing an application of closeParenToken
		 */
		public static final SourceModel.Expr closeParenToken() {
			return SourceModel.Expr.Var.make(Functions.closeParenToken);
		}

		/**
		 * Name binding for function: closeParenToken.
		 * @see #closeParenToken()
		 */
		public static final QualifiedName closeParenToken = 
			QualifiedName.make(
				CAL_ExpressionLexer.MODULE_NAME, 
				"closeParenToken");

		/**
		 * Helper binding method for function: colonToken. 
		 * @return the SourceModule.expr representing an application of colonToken
		 */
		public static final SourceModel.Expr colonToken() {
			return SourceModel.Expr.Var.make(Functions.colonToken);
		}

		/**
		 * Name binding for function: colonToken.
		 * @see #colonToken()
		 */
		public static final QualifiedName colonToken = 
			QualifiedName.make(CAL_ExpressionLexer.MODULE_NAME, "colonToken");

		/**
		 * Helper binding method for function: commaToken. 
		 * @return the SourceModule.expr representing an application of commaToken
		 */
		public static final SourceModel.Expr commaToken() {
			return SourceModel.Expr.Var.make(Functions.commaToken);
		}

		/**
		 * Name binding for function: commaToken.
		 * @see #commaToken()
		 */
		public static final QualifiedName commaToken = 
			QualifiedName.make(CAL_ExpressionLexer.MODULE_NAME, "commaToken");

		/**
		 * Helper binding method for function: dotToken. 
		 * @return the SourceModule.expr representing an application of dotToken
		 */
		public static final SourceModel.Expr dotToken() {
			return SourceModel.Expr.Var.make(Functions.dotToken);
		}

		/**
		 * Name binding for function: dotToken.
		 * @see #dotToken()
		 */
		public static final QualifiedName dotToken = 
			QualifiedName.make(CAL_ExpressionLexer.MODULE_NAME, "dotToken");

		/**
		 * Helper binding method for function: eqToken. 
		 * @return the SourceModule.expr representing an application of eqToken
		 */
		public static final SourceModel.Expr eqToken() {
			return SourceModel.Expr.Var.make(Functions.eqToken);
		}

		/**
		 * Name binding for function: eqToken.
		 * @see #eqToken()
		 */
		public static final QualifiedName eqToken = 
			QualifiedName.make(CAL_ExpressionLexer.MODULE_NAME, "eqToken");

		/**
		 * Helper binding method for function: exclmnToken. 
		 * @return the SourceModule.expr representing an application of exclmnToken
		 */
		public static final SourceModel.Expr exclmnToken() {
			return SourceModel.Expr.Var.make(Functions.exclmnToken);
		}

		/**
		 * Name binding for function: exclmnToken.
		 * @see #exclmnToken()
		 */
		public static final QualifiedName exclmnToken = 
			QualifiedName.make(CAL_ExpressionLexer.MODULE_NAME, "exclmnToken");

		/**
		 * Helper binding method for function: functionToken. 
		 * @return the SourceModule.expr representing an application of functionToken
		 */
		public static final SourceModel.Expr functionToken() {
			return SourceModel.Expr.Var.make(Functions.functionToken);
		}

		/**
		 * Name binding for function: functionToken.
		 * @see #functionToken()
		 */
		public static final QualifiedName functionToken = 
			QualifiedName.make(CAL_ExpressionLexer.MODULE_NAME, "functionToken");

		/**
		 * Gets the name of the function from a function token.
		 * @param token (CAL type: <code>Cal.Utilities.ExpressionLexer.Token</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr functionTokenValue(SourceModel.Expr token) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.functionTokenValue), token});
		}

		/**
		 * Name binding for function: functionTokenValue.
		 * @see #functionTokenValue(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName functionTokenValue = 
			QualifiedName.make(
				CAL_ExpressionLexer.MODULE_NAME, 
				"functionTokenValue");

		/**
		 * Gets the image from a <code>Cal.Utilities.ExpressionLexer.TokenImage</code>
		 * @param tokenImage (CAL type: <code>Cal.Utilities.ExpressionLexer.TokenImage</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr getImage(SourceModel.Expr tokenImage) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getImage), tokenImage});
		}

		/**
		 * Name binding for function: getImage.
		 * @see #getImage(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getImage = 
			QualifiedName.make(CAL_ExpressionLexer.MODULE_NAME, "getImage");

		/**
		 * Gets the Col# from a <code>Cal.Utilities.ExpressionLexer.Position</code>
		 * @param pos (CAL type: <code>Cal.Utilities.ExpressionLexer.Position</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 */
		public static final SourceModel.Expr getPosCol(SourceModel.Expr pos) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getPosCol), pos});
		}

		/**
		 * Name binding for function: getPosCol.
		 * @see #getPosCol(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getPosCol = 
			QualifiedName.make(CAL_ExpressionLexer.MODULE_NAME, "getPosCol");

		/**
		 * Gets the Line# from a <code>Cal.Utilities.ExpressionLexer.Position</code>
		 * @param pos (CAL type: <code>Cal.Utilities.ExpressionLexer.Position</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 */
		public static final SourceModel.Expr getPosLine(SourceModel.Expr pos) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getPosLine), pos});
		}

		/**
		 * Name binding for function: getPosLine.
		 * @see #getPosLine(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getPosLine = 
			QualifiedName.make(CAL_ExpressionLexer.MODULE_NAME, "getPosLine");

		/**
		 * Gets the <code>Cal.Utilities.ExpressionLexer.Position</code> from a <code>Cal.Utilities.ExpressionLexer.TokenImage</code>
		 * @param tokenImage (CAL type: <code>Cal.Utilities.ExpressionLexer.TokenImage</code>)
		 * @return (CAL type: <code>Cal.Utilities.ExpressionLexer.Position</code>) 
		 */
		public static final SourceModel.Expr getPosition(SourceModel.Expr tokenImage) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getPosition), tokenImage});
		}

		/**
		 * Name binding for function: getPosition.
		 * @see #getPosition(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getPosition = 
			QualifiedName.make(CAL_ExpressionLexer.MODULE_NAME, "getPosition");

		/**
		 * Gets a <code>Cal.Utilities.ExpressionLexer.TokenImage</code> from a <code>Cal.Utilities.ExpressionLexer.Token</code>
		 * @param token (CAL type: <code>Cal.Utilities.ExpressionLexer.Token</code>)
		 * @return (CAL type: <code>Cal.Utilities.ExpressionLexer.TokenImage</code>) 
		 */
		public static final SourceModel.Expr getTokenImage(SourceModel.Expr token) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getTokenImage), token});
		}

		/**
		 * Name binding for function: getTokenImage.
		 * @see #getTokenImage(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getTokenImage = 
			QualifiedName.make(CAL_ExpressionLexer.MODULE_NAME, "getTokenImage");

		/**
		 * Helper binding method for function: gtToken. 
		 * @return the SourceModule.expr representing an application of gtToken
		 */
		public static final SourceModel.Expr gtToken() {
			return SourceModel.Expr.Var.make(Functions.gtToken);
		}

		/**
		 * Name binding for function: gtToken.
		 * @see #gtToken()
		 */
		public static final QualifiedName gtToken = 
			QualifiedName.make(CAL_ExpressionLexer.MODULE_NAME, "gtToken");

		/**
		 * Helper binding method for function: hashToken. 
		 * @return the SourceModule.expr representing an application of hashToken
		 */
		public static final SourceModel.Expr hashToken() {
			return SourceModel.Expr.Var.make(Functions.hashToken);
		}

		/**
		 * Name binding for function: hashToken.
		 * @see #hashToken()
		 */
		public static final QualifiedName hashToken = 
			QualifiedName.make(CAL_ExpressionLexer.MODULE_NAME, "hashToken");

		/**
		 * Helper binding method for function: intTokenValue. 
		 * @param token
		 * @return the SourceModule.expr representing an application of intTokenValue
		 */
		public static final SourceModel.Expr intTokenValue(SourceModel.Expr token) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intTokenValue), token});
		}

		/**
		 * Name binding for function: intTokenValue.
		 * @see #intTokenValue(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intTokenValue = 
			QualifiedName.make(CAL_ExpressionLexer.MODULE_NAME, "intTokenValue");

		/**
		 * Helper binding method for function: integerToken. 
		 * @return the SourceModule.expr representing an application of integerToken
		 */
		public static final SourceModel.Expr integerToken() {
			return SourceModel.Expr.Var.make(Functions.integerToken);
		}

		/**
		 * Name binding for function: integerToken.
		 * @see #integerToken()
		 */
		public static final QualifiedName integerToken = 
			QualifiedName.make(CAL_ExpressionLexer.MODULE_NAME, "integerToken");

		/**
		 * Helper binding method for function: keywordToken. 
		 * @param keyword
		 * @return the SourceModule.expr representing an application of keywordToken
		 */
		public static final SourceModel.Expr keywordToken(SourceModel.Expr keyword) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.keywordToken), keyword});
		}

		/**
		 * @see #keywordToken(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param keyword
		 * @return the SourceModel.Expr representing an application of keywordToken
		 */
		public static final SourceModel.Expr keywordToken(java.lang.String keyword) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.keywordToken), SourceModel.Expr.makeStringValue(keyword)});
		}

		/**
		 * Name binding for function: keywordToken.
		 * @see #keywordToken(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName keywordToken = 
			QualifiedName.make(CAL_ExpressionLexer.MODULE_NAME, "keywordToken");

		/**
		 * Helper binding method for function: ltToken. 
		 * @return the SourceModule.expr representing an application of ltToken
		 */
		public static final SourceModel.Expr ltToken() {
			return SourceModel.Expr.Var.make(Functions.ltToken);
		}

		/**
		 * Name binding for function: ltToken.
		 * @see #ltToken()
		 */
		public static final QualifiedName ltToken = 
			QualifiedName.make(CAL_ExpressionLexer.MODULE_NAME, "ltToken");

		/**
		 * Helper binding method for function: minusToken. 
		 * @return the SourceModule.expr representing an application of minusToken
		 */
		public static final SourceModel.Expr minusToken() {
			return SourceModel.Expr.Var.make(Functions.minusToken);
		}

		/**
		 * Name binding for function: minusToken.
		 * @see #minusToken()
		 */
		public static final QualifiedName minusToken = 
			QualifiedName.make(CAL_ExpressionLexer.MODULE_NAME, "minusToken");

		/**
		 * Helper binding method for function: nameToken. 
		 * @return the SourceModule.expr representing an application of nameToken
		 */
		public static final SourceModel.Expr nameToken() {
			return SourceModel.Expr.Var.make(Functions.nameToken);
		}

		/**
		 * Name binding for function: nameToken.
		 * @see #nameToken()
		 */
		public static final QualifiedName nameToken = 
			QualifiedName.make(CAL_ExpressionLexer.MODULE_NAME, "nameToken");

		/**
		 * Gets the name value from a name token.
		 * @param token (CAL type: <code>Cal.Utilities.ExpressionLexer.Token</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr nameTokenValue(SourceModel.Expr token) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.nameTokenValue), token});
		}

		/**
		 * Name binding for function: nameTokenValue.
		 * @see #nameTokenValue(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName nameTokenValue = 
			QualifiedName.make(
				CAL_ExpressionLexer.MODULE_NAME, 
				"nameTokenValue");

		/**
		 * Helper binding method for function: numberToken. 
		 * @return the SourceModule.expr representing an application of numberToken
		 */
		public static final SourceModel.Expr numberToken() {
			return SourceModel.Expr.Var.make(Functions.numberToken);
		}

		/**
		 * Name binding for function: numberToken.
		 * @see #numberToken()
		 */
		public static final QualifiedName numberToken = 
			QualifiedName.make(CAL_ExpressionLexer.MODULE_NAME, "numberToken");

		/**
		 * Gets the number value from a number token.
		 * @param token (CAL type: <code>Cal.Utilities.ExpressionLexer.Token</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 */
		public static final SourceModel.Expr numericTokenValue(SourceModel.Expr token) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.numericTokenValue), token});
		}

		/**
		 * Name binding for function: numericTokenValue.
		 * @see #numericTokenValue(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName numericTokenValue = 
			QualifiedName.make(
				CAL_ExpressionLexer.MODULE_NAME, 
				"numericTokenValue");

		/**
		 * Helper binding method for function: openBraceToken. 
		 * @return the SourceModule.expr representing an application of openBraceToken
		 */
		public static final SourceModel.Expr openBraceToken() {
			return SourceModel.Expr.Var.make(Functions.openBraceToken);
		}

		/**
		 * Name binding for function: openBraceToken.
		 * @see #openBraceToken()
		 */
		public static final QualifiedName openBraceToken = 
			QualifiedName.make(
				CAL_ExpressionLexer.MODULE_NAME, 
				"openBraceToken");

		/**
		 * Helper binding method for function: openParenToken. 
		 * @return the SourceModule.expr representing an application of openParenToken
		 */
		public static final SourceModel.Expr openParenToken() {
			return SourceModel.Expr.Var.make(Functions.openParenToken);
		}

		/**
		 * Name binding for function: openParenToken.
		 * @see #openParenToken()
		 */
		public static final QualifiedName openParenToken = 
			QualifiedName.make(
				CAL_ExpressionLexer.MODULE_NAME, 
				"openParenToken");

		/**
		 * Helper binding method for function: parseErrorText. 
		 * @param text
		 * @param err
		 * @return the SourceModule.expr representing an application of parseErrorText
		 */
		public static final SourceModel.Expr parseErrorText(SourceModel.Expr text, SourceModel.Expr err) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.parseErrorText), text, err});
		}

		/**
		 * @see #parseErrorText(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param text
		 * @param err
		 * @return the SourceModel.Expr representing an application of parseErrorText
		 */
		public static final SourceModel.Expr parseErrorText(java.lang.String text, SourceModel.Expr err) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.parseErrorText), SourceModel.Expr.makeStringValue(text), err});
		}

		/**
		 * Name binding for function: parseErrorText.
		 * @see #parseErrorText(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName parseErrorText = 
			QualifiedName.make(
				CAL_ExpressionLexer.MODULE_NAME, 
				"parseErrorText");

		/**
		 * Helper binding method for function: percentToken. 
		 * @return the SourceModule.expr representing an application of percentToken
		 */
		public static final SourceModel.Expr percentToken() {
			return SourceModel.Expr.Var.make(Functions.percentToken);
		}

		/**
		 * Name binding for function: percentToken.
		 * @see #percentToken()
		 */
		public static final QualifiedName percentToken = 
			QualifiedName.make(CAL_ExpressionLexer.MODULE_NAME, "percentToken");

		/**
		 * Helper binding method for function: plusToken. 
		 * @return the SourceModule.expr representing an application of plusToken
		 */
		public static final SourceModel.Expr plusToken() {
			return SourceModel.Expr.Var.make(Functions.plusToken);
		}

		/**
		 * Name binding for function: plusToken.
		 * @see #plusToken()
		 */
		public static final QualifiedName plusToken = 
			QualifiedName.make(CAL_ExpressionLexer.MODULE_NAME, "plusToken");

		/**
		 * Helper binding method for function: showToken. 
		 * @param token
		 * @return the SourceModule.expr representing an application of showToken
		 */
		public static final SourceModel.Expr showToken(SourceModel.Expr token) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showToken), token});
		}

		/**
		 * Name binding for function: showToken.
		 * @see #showToken(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showToken = 
			QualifiedName.make(CAL_ExpressionLexer.MODULE_NAME, "showToken");

		/**
		 * Helper binding method for function: slashToken. 
		 * @return the SourceModule.expr representing an application of slashToken
		 */
		public static final SourceModel.Expr slashToken() {
			return SourceModel.Expr.Var.make(Functions.slashToken);
		}

		/**
		 * Name binding for function: slashToken.
		 * @see #slashToken()
		 */
		public static final QualifiedName slashToken = 
			QualifiedName.make(CAL_ExpressionLexer.MODULE_NAME, "slashToken");

		/**
		 * Helper binding method for function: specialCharSequenceToken. 
		 * @param specialChars
		 * @return the SourceModule.expr representing an application of specialCharSequenceToken
		 */
		public static final SourceModel.Expr specialCharSequenceToken(SourceModel.Expr specialChars) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.specialCharSequenceToken), specialChars});
		}

		/**
		 * @see #specialCharSequenceToken(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param specialChars
		 * @return the SourceModel.Expr representing an application of specialCharSequenceToken
		 */
		public static final SourceModel.Expr specialCharSequenceToken(java.lang.String specialChars) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.specialCharSequenceToken), SourceModel.Expr.makeStringValue(specialChars)});
		}

		/**
		 * Name binding for function: specialCharSequenceToken.
		 * @see #specialCharSequenceToken(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName specialCharSequenceToken = 
			QualifiedName.make(
				CAL_ExpressionLexer.MODULE_NAME, 
				"specialCharSequenceToken");

		/**
		 * Helper binding method for function: specialCharToken. 
		 * @param specialChar
		 * @return the SourceModule.expr representing an application of specialCharToken
		 */
		public static final SourceModel.Expr specialCharToken(SourceModel.Expr specialChar) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.specialCharToken), specialChar});
		}

		/**
		 * @see #specialCharToken(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param specialChar
		 * @return the SourceModel.Expr representing an application of specialCharToken
		 */
		public static final SourceModel.Expr specialCharToken(char specialChar) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.specialCharToken), SourceModel.Expr.makeCharValue(specialChar)});
		}

		/**
		 * Name binding for function: specialCharToken.
		 * @see #specialCharToken(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName specialCharToken = 
			QualifiedName.make(
				CAL_ExpressionLexer.MODULE_NAME, 
				"specialCharToken");

		/**
		 * Helper binding method for function: specificNameToken. 
		 * @param nameExpected
		 * @return the SourceModule.expr representing an application of specificNameToken
		 */
		public static final SourceModel.Expr specificNameToken(SourceModel.Expr nameExpected) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.specificNameToken), nameExpected});
		}

		/**
		 * @see #specificNameToken(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param nameExpected
		 * @return the SourceModel.Expr representing an application of specificNameToken
		 */
		public static final SourceModel.Expr specificNameToken(java.lang.String nameExpected) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.specificNameToken), SourceModel.Expr.makeStringValue(nameExpected)});
		}

		/**
		 * Name binding for function: specificNameToken.
		 * @see #specificNameToken(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName specificNameToken = 
			QualifiedName.make(
				CAL_ExpressionLexer.MODULE_NAME, 
				"specificNameToken");

		/**
		 * Helper binding method for function: stringToken. 
		 * @return the SourceModule.expr representing an application of stringToken
		 */
		public static final SourceModel.Expr stringToken() {
			return SourceModel.Expr.Var.make(Functions.stringToken);
		}

		/**
		 * Name binding for function: stringToken.
		 * @see #stringToken()
		 */
		public static final QualifiedName stringToken = 
			QualifiedName.make(CAL_ExpressionLexer.MODULE_NAME, "stringToken");

		/**
		 * Gets the string value from a string token.
		 * @param token (CAL type: <code>Cal.Utilities.ExpressionLexer.Token</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr stringTokenValue(SourceModel.Expr token) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.stringTokenValue), token});
		}

		/**
		 * Name binding for function: stringTokenValue.
		 * @see #stringTokenValue(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName stringTokenValue = 
			QualifiedName.make(
				CAL_ExpressionLexer.MODULE_NAME, 
				"stringTokenValue");

		/**
		 * Helper binding method for function: tildeToken. 
		 * @return the SourceModule.expr representing an application of tildeToken
		 */
		public static final SourceModel.Expr tildeToken() {
			return SourceModel.Expr.Var.make(Functions.tildeToken);
		}

		/**
		 * Name binding for function: tildeToken.
		 * @see #tildeToken()
		 */
		public static final QualifiedName tildeToken = 
			QualifiedName.make(CAL_ExpressionLexer.MODULE_NAME, "tildeToken");

		/**
		 * String of lexemes with whitespace/comments between
		 * This is the top level rule for the expression lexer
		 * @param identifierNonAlphaChars (CAL type: <code>[Cal.Core.Prelude.Char]</code>)
		 *          Characters other than letters and numbers to allow in identifiers (other than the first character)
		 * @param keywords (CAL type: <code>[Cal.Core.Prelude.String]</code>)
		 *          Keyword sequences (separated by whitespace) - each individual keyword must be a valid identifier
		 * @param functionNames (CAL type: <code>[Cal.Core.Prelude.String]</code>)
		 *          Function names - each must be a valid identifier
		 * @param specialCharSequences (CAL type: <code>[Cal.Core.Prelude.String]</code>)
		 *          Special character sequences, not separated by whitespace
		 * @param identifierQuoteChars (CAL type: <code>[(Cal.Core.Prelude.Char, Cal.Core.Prelude.Char)]</code>)
		 *          Pairs of matching opening and closing quote characters - e.g. ("[", "]")
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser Cal.Core.Prelude.Char a [Cal.Utilities.ExpressionLexer.Token]</code>) 
		 */
		public static final SourceModel.Expr tokenStream(SourceModel.Expr identifierNonAlphaChars, SourceModel.Expr keywords, SourceModel.Expr functionNames, SourceModel.Expr specialCharSequences, SourceModel.Expr identifierQuoteChars) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.tokenStream), identifierNonAlphaChars, keywords, functionNames, specialCharSequences, identifierQuoteChars});
		}

		/**
		 * Name binding for function: tokenStream.
		 * @see #tokenStream(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName tokenStream = 
			QualifiedName.make(CAL_ExpressionLexer.MODULE_NAME, "tokenStream");

		/**
		 * String of lexemes with whitespace/comments between
		 * This is the top level rule for the expression lexer
		 * @param indentifier_rule (CAL type: <code>Cal.Utilities.Parser.GenParser Cal.Core.Prelude.Char st [Cal.Core.Prelude.Char]</code>)
		 *          Parser to use in recognizing identifiers or "names"
		 * @param keywords (CAL type: <code>[Cal.Core.Prelude.String]</code>)
		 *          Keyword sequences (separated by whitespace) - each individual keyword must be a valid identifier
		 * @param functionNames (CAL type: <code>[Cal.Core.Prelude.String]</code>)
		 *          Function names - each must be a valid identifier
		 * @param specialCharSequences (CAL type: <code>[Cal.Core.Prelude.String]</code>)
		 *          Special character sequences, not separated by whitespace
		 * @param identifierQuoteChars (CAL type: <code>[(Cal.Core.Prelude.Char, Cal.Core.Prelude.Char)]</code>)
		 *          Pairs of matching opening and closing quote characters - e.g. ("[", "]")
		 * @param commentDelimiters (CAL type: <code>[(Cal.Core.Prelude.String, Cal.Core.Prelude.String)]</code>)
		 *          Pairs of matching opening and closing comment delimiters characters - e.g. ("//", "\n")
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser Cal.Core.Prelude.Char st [Cal.Utilities.ExpressionLexer.Token]</code>) 
		 */
		public static final SourceModel.Expr tokenStream2(SourceModel.Expr indentifier_rule, SourceModel.Expr keywords, SourceModel.Expr functionNames, SourceModel.Expr specialCharSequences, SourceModel.Expr identifierQuoteChars, SourceModel.Expr commentDelimiters) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.tokenStream2), indentifier_rule, keywords, functionNames, specialCharSequences, identifierQuoteChars, commentDelimiters});
		}

		/**
		 * Name binding for function: tokenStream2.
		 * @see #tokenStream2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName tokenStream2 = 
			QualifiedName.make(CAL_ExpressionLexer.MODULE_NAME, "tokenStream2");

		/**
		 * Helper binding method for function: vertBarToken. 
		 * @return the SourceModule.expr representing an application of vertBarToken
		 */
		public static final SourceModel.Expr vertBarToken() {
			return SourceModel.Expr.Var.make(Functions.vertBarToken);
		}

		/**
		 * Name binding for function: vertBarToken.
		 * @see #vertBarToken()
		 */
		public static final QualifiedName vertBarToken = 
			QualifiedName.make(CAL_ExpressionLexer.MODULE_NAME, "vertBarToken");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = 431426541;

}
