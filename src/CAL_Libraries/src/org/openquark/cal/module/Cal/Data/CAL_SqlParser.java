/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_SqlParser.java)
 * was generated from CAL module: Cal.Data.SqlParser.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Data.SqlParser module from Java code.
 *  
 * Creation date: Tue Aug 28 15:58:41 PDT 2007
 * --!>
 *  
 */

package org.openquark.cal.module.Cal.Data;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * This module implements a SQL parser.  Using the <code>Cal.Utilities.Parser</code> and <code>Cal.Utilities.ExpressionLexer</code> modules, 
 * a specific lexer and parser are described for a SQL syntax.  
 * The intent of the parser is to build SQL combinators (see <code>Cal.Data.Sql</code> module) as SQL syntax is parsed, the
 * result being an evaluable SQL expression (at least translatable to evaluable SQL via a <code>Cal.Data.Sql.SqlBuilder</code>).
 * @author Luke Evans
 * @author Greg McClement
 * @author Richard Webster
 */
public final class CAL_SqlParser {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Data.SqlParser");

	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Data.SqlParser module.
	 */
	public static final class Functions {
		/**
		 * Parses the specified SQL expression text.
		 * An error is thrown if the text could not be parsed without any remaining text.
		 * @param sqlExprText (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.Expr</code>) 
		 */
		public static final SourceModel.Expr parseExpression(SourceModel.Expr sqlExprText) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.parseExpression), sqlExprText});
		}

		/**
		 * @see #parseExpression(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param sqlExprText
		 * @return the SourceModel.Expr representing an application of parseExpression
		 */
		public static final SourceModel.Expr parseExpression(java.lang.String sqlExprText) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.parseExpression), SourceModel.Expr.makeStringValue(sqlExprText)});
		}

		/**
		 * Name binding for function: parseExpression.
		 * @see #parseExpression(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName parseExpression = 
			QualifiedName.make(CAL_SqlParser.MODULE_NAME, "parseExpression");

		/**
		 * Parses the SQL text as a SELECT statement.
		 * An error is thrown if the text could not be parsed without any remaining text.
		 * @param sql (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.Query</code>) 
		 */
		public static final SourceModel.Expr parseSelectStatement(SourceModel.Expr sql) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.parseSelectStatement), sql});
		}

		/**
		 * @see #parseSelectStatement(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param sql
		 * @return the SourceModel.Expr representing an application of parseSelectStatement
		 */
		public static final SourceModel.Expr parseSelectStatement(java.lang.String sql) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.parseSelectStatement), SourceModel.Expr.makeStringValue(sql)});
		}

		/**
		 * Name binding for function: parseSelectStatement.
		 * @see #parseSelectStatement(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName parseSelectStatement = 
			QualifiedName.make(
				CAL_SqlParser.MODULE_NAME, 
				"parseSelectStatement");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = -529707899;

}
