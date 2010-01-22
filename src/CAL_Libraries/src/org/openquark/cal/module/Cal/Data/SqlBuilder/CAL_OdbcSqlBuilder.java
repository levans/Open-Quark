/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_OdbcSqlBuilder.java)
 * was generated from CAL module: Cal.Data.SqlBuilder.OdbcSqlBuilder.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Data.SqlBuilder.OdbcSqlBuilder module from Java code.
 *  
 * Creation date: Fri Jan 22 15:14:05 PST 2010
 * --!>
 *  
 */

package org.openquark.cal.module.Cal.Data.SqlBuilder;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * The ODBC SQL builder generates queries which will work with typical ODBC datasources.
 * More specialized versions might be needed for certain ODBC drivers and/or database servers.
 */
public final class CAL_OdbcSqlBuilder {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Data.SqlBuilder.OdbcSqlBuilder");

	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Data.SqlBuilder.OdbcSqlBuilder module.
	 */
	public static final class Functions {
		/**
		 * For ODBC-Access, there are some problems using the square brackets as quotes inside of the 
		 * ODBC function escape syntax, so use back-quotes instead.
		 * @return (CAL type: <code>Cal.Data.Sql.SqlBuilder</code>) 
		 */
		public static final SourceModel.Expr odbcAccessSqlBuilder() {
			return SourceModel.Expr.Var.make(Functions.odbcAccessSqlBuilder);
		}

		/**
		 * Name binding for function: odbcAccessSqlBuilder.
		 * @see #odbcAccessSqlBuilder()
		 */
		public static final QualifiedName odbcAccessSqlBuilder = 
			QualifiedName.make(
				CAL_OdbcSqlBuilder.MODULE_NAME, 
				"odbcAccessSqlBuilder");

		/**
		 * Helper binding method for function: odbcAccessSqlBuilderFunctions. 
		 * @return the SourceModule.expr representing an application of odbcAccessSqlBuilderFunctions
		 */
		public static final SourceModel.Expr odbcAccessSqlBuilderFunctions() {
			return 
				SourceModel.Expr.Var.make(
					Functions.odbcAccessSqlBuilderFunctions);
		}

		/**
		 * Name binding for function: odbcAccessSqlBuilderFunctions.
		 * @see #odbcAccessSqlBuilderFunctions()
		 */
		public static final QualifiedName odbcAccessSqlBuilderFunctions = 
			QualifiedName.make(
				CAL_OdbcSqlBuilder.MODULE_NAME, 
				"odbcAccessSqlBuilderFunctions");

		/**
		 * The ODBC SQL builder generates queries which will work with typical ODBC datasources.
		 * More specialized versions might be needed for certain ODBC drivers and/or database servers.
		 * <p>
		 * The SQL builder for ODBC differs from the default SQL builder in that:
		 * 1. 
		 * 
		 * @return (CAL type: <code>Cal.Data.Sql.SqlBuilder</code>) 
		 */
		public static final SourceModel.Expr odbcSqlBuilder() {
			return SourceModel.Expr.Var.make(Functions.odbcSqlBuilder);
		}

		/**
		 * Name binding for function: odbcSqlBuilder.
		 * @see #odbcSqlBuilder()
		 */
		public static final QualifiedName odbcSqlBuilder = 
			QualifiedName.make(CAL_OdbcSqlBuilder.MODULE_NAME, "odbcSqlBuilder");

		/**
		 * The SqlBuilder functions for ODBC.
		 * @return (CAL type: <code>{addParens :: Cal.Utilities.PrettyPrinter.Document -> Cal.Utilities.PrettyPrinter.Document, booleanToSql :: Cal.Core.Prelude.Boolean -> Cal.Utilities.PrettyPrinter.Document, buildCommitStatement :: Cal.Data.Sql.SqlBuilder -> Cal.Utilities.PrettyPrinter.Document, buildCreateDatabaseStatement :: Cal.Data.Sql.SqlBuilder -> Cal.Data.DatabaseMetadata.DatabaseReference -> Cal.Core.Prelude.Boolean -> Cal.Utilities.PrettyPrinter.Document, buildCreateTableStatement :: Cal.Data.Sql.SqlBuilder -> Cal.Data.DatabaseMetadata.TableDescription -> Cal.Core.Prelude.Boolean -> Cal.Utilities.PrettyPrinter.Document, buildCreateViewStatement :: Cal.Data.Sql.SqlBuilder -> Cal.Data.DatabaseMetadata.TableReference -> Cal.Data.Sql.Query -> Cal.Core.Prelude.Boolean -> Cal.Utilities.PrettyPrinter.Document, buildDeleteRowsStatement :: Cal.Data.Sql.SqlBuilder -> Cal.Data.Sql.SqlBuilderState -> Cal.Data.DatabaseMetadata.TableReference -> Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Boolean -> Cal.Utilities.PrettyPrinter.Document, buildDropDatabaseStatement :: Cal.Data.Sql.SqlBuilder -> Cal.Data.DatabaseMetadata.DatabaseReference -> Cal.Core.Prelude.Boolean -> Cal.Utilities.PrettyPrinter.Document, buildDropTableStatement :: Cal.Data.Sql.SqlBuilder -> Cal.Data.DatabaseMetadata.TableReference -> Cal.Core.Prelude.Boolean -> Cal.Utilities.PrettyPrinter.Document, buildDropViewStatement :: Cal.Data.Sql.SqlBuilder -> Cal.Data.DatabaseMetadata.TableReference -> Cal.Core.Prelude.Boolean -> Cal.Utilities.PrettyPrinter.Document, buildFieldDescription :: Cal.Data.Sql.SqlBuilder -> Cal.Data.DatabaseMetadata.FieldDescription -> Cal.Utilities.PrettyPrinter.Document, buildFieldType :: Cal.Data.SqlType.SqlType -> Cal.Utilities.PrettyPrinter.Document, buildFromClause :: Cal.Data.Sql.SqlBuilder -> Cal.Data.Sql.SqlBuilderState -> [Cal.Data.Sql.JoinNode] -> Cal.Utilities.PrettyPrinter.Document, buildGroupByClause :: Cal.Data.Sql.SqlBuilder -> Cal.Data.Sql.SqlBuilderState -> [Cal.Data.Sql.Expr] -> Cal.Utilities.PrettyPrinter.Document, buildHavingClause :: Cal.Data.Sql.SqlBuilder -> Cal.Data.Sql.SqlBuilderState -> Cal.Core.Prelude.Maybe Cal.Data.Sql.Expr -> Cal.Utilities.PrettyPrinter.Document, buildInsertQueryValuesStatement :: Cal.Data.Sql.SqlBuilder -> Cal.Data.DatabaseMetadata.TableReference -> Cal.Core.Prelude.Maybe [Cal.Core.Prelude.String] -> Cal.Data.Sql.Query -> Cal.Utilities.PrettyPrinter.Document, buildInsertValuesStatement :: Cal.Data.Sql.SqlBuilder -> Cal.Data.DatabaseMetadata.TableReference -> Cal.Core.Prelude.Maybe [Cal.Core.Prelude.String] -> [Cal.Data.Sql.Expr] -> Cal.Utilities.PrettyPrinter.Document, buildOrderByClause :: Cal.Data.Sql.SqlBuilder -> Cal.Data.Sql.SqlBuilderState -> [(Cal.Data.Sql.Expr, Cal.Core.Prelude.Boolean)] -> Cal.Utilities.PrettyPrinter.Document, buildSelectClause :: Cal.Data.Sql.SqlBuilder -> Cal.Data.Sql.SqlBuilderState -> [Cal.Data.Sql.QueryOption] -> [(Cal.Data.Sql.Expr, Cal.Core.Prelude.String)] -> Cal.Utilities.PrettyPrinter.Document, buildTableAndAliasText :: Cal.Data.Sql.SqlBuilder -> Cal.Data.Sql.SqlBuilderState -> Cal.Data.Sql.QueryTable -> Cal.Utilities.PrettyPrinter.Document, buildUpdateValuesStatement :: Cal.Data.Sql.SqlBuilder -> Cal.Data.Sql.SqlBuilderState -> Cal.Data.DatabaseMetadata.TableReference -> [Cal.Core.Prelude.String] -> [Cal.Data.Sql.Expr] -> Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Boolean -> Cal.Utilities.PrettyPrinter.Document, buildWhereClause :: Cal.Data.Sql.SqlBuilder -> Cal.Data.Sql.SqlBuilderState -> Cal.Core.Prelude.Maybe Cal.Data.Sql.Expr -> [Cal.Data.Sql.JoinNode] -> Cal.Utilities.PrettyPrinter.Document, constructQuery :: Cal.Data.Sql.SqlBuilder -> Cal.Data.Sql.SqlBuilderState -> Cal.Utilities.PrettyPrinter.Document -> Cal.Utilities.PrettyPrinter.Document -> Cal.Utilities.PrettyPrinter.Document -> Cal.Utilities.PrettyPrinter.Document -> Cal.Utilities.PrettyPrinter.Document -> Cal.Utilities.PrettyPrinter.Document -> Cal.Utilities.PrettyPrinter.Document, constructUnionQuery :: Cal.Data.Sql.SqlBuilder -> Cal.Data.Sql.SqlBuilderState -> Cal.Utilities.PrettyPrinter.Document -> Cal.Utilities.PrettyPrinter.Document -> Cal.Core.Prelude.Boolean -> Cal.Utilities.PrettyPrinter.Document, doubleToSql :: Cal.Core.Prelude.Double -> Cal.Utilities.PrettyPrinter.Document, functionName :: Cal.Data.Sql.DbFunction -> Cal.Core.Prelude.String, functionToSql :: Cal.Data.Sql.SqlBuilder -> Cal.Data.Sql.SqlBuilderState -> Cal.Data.Sql.DbFunction -> [Cal.Data.Sql.Expr] -> Cal.Utilities.PrettyPrinter.Document, intToSql :: Cal.Core.Prelude.Int -> Cal.Utilities.PrettyPrinter.Document, listToSql :: Cal.Data.Sql.SqlBuilder -> Cal.Data.Sql.SqlBuilderState -> [Cal.Data.Sql.Expr] -> Cal.Utilities.PrettyPrinter.Document, makeSafeName :: Cal.Core.Prelude.String -> Cal.Core.Prelude.String, nullToSql :: Cal.Utilities.PrettyPrinter.Document, operatorText :: Cal.Data.Sql.DbFunction -> Cal.Core.Prelude.String, parameterToSql :: Cal.Data.Sql.SqlBuilder -> Cal.Data.Sql.Parameter -> Cal.Utilities.PrettyPrinter.Document, prepareQuery :: Cal.Data.Sql.SqlBuilderState -> Cal.Data.Sql.Query -> Cal.Data.Sql.Query, quoteIdentifier :: Cal.Core.Prelude.String -> Cal.Utilities.PrettyPrinter.Document, stringToSql :: Cal.Core.Prelude.String -> Cal.Utilities.PrettyPrinter.Document, timeIntervalToSql :: Cal.Data.Sql.TimeInterval -> Cal.Utilities.PrettyPrinter.Document, timeToSql :: Cal.Utilities.Time.Time -> Cal.Utilities.TimeZone.TimeZone -> Cal.Utilities.PrettyPrinter.Document}</code>) 
		 */
		public static final SourceModel.Expr odbcSqlBuilderFunctions() {
			return SourceModel.Expr.Var.make(Functions.odbcSqlBuilderFunctions);
		}

		/**
		 * Name binding for function: odbcSqlBuilderFunctions.
		 * @see #odbcSqlBuilderFunctions()
		 */
		public static final QualifiedName odbcSqlBuilderFunctions = 
			QualifiedName.make(
				CAL_OdbcSqlBuilder.MODULE_NAME, 
				"odbcSqlBuilderFunctions");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = -196865573;

}
