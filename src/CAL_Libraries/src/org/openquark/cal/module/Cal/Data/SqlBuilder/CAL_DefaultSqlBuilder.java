/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_DefaultSqlBuilder.java)
 * was generated from CAL module: Cal.Data.SqlBuilder.DefaultSqlBuilder.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Data.SqlBuilder.DefaultSqlBuilder module from Java code.
 *  
 * Creation date: Sat Jan 23 14:26:47 PST 2010
 * --!>
 *  
 */

package org.openquark.cal.module.Cal.Data.SqlBuilder;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * A default SqlBuilder implementation, which attempts to use common syntax from popular databases.
 * Other SqlBuilder implementations can be created by overriding the necessary functions in the defaultSqlBuilderFunctions record
 * and then passing this to Sql.makeSqlBuilder.
 */
public final class CAL_DefaultSqlBuilder {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Data.SqlBuilder.DefaultSqlBuilder");

	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Data.SqlBuilder.DefaultSqlBuilder module.
	 */
	public static final class Functions {
		/**
		 * Helper binding method for function: buildTableAndAliasTextHelper. 
		 * @param includeAsKeywordWithAlias
		 * @param builder
		 * @param builderState
		 * @param table
		 * @return the SourceModule.expr representing an application of buildTableAndAliasTextHelper
		 */
		public static final SourceModel.Expr buildTableAndAliasTextHelper(SourceModel.Expr includeAsKeywordWithAlias, SourceModel.Expr builder, SourceModel.Expr builderState, SourceModel.Expr table) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.buildTableAndAliasTextHelper), includeAsKeywordWithAlias, builder, builderState, table});
		}

		/**
		 * @see #buildTableAndAliasTextHelper(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param includeAsKeywordWithAlias
		 * @param builder
		 * @param builderState
		 * @param table
		 * @return the SourceModel.Expr representing an application of buildTableAndAliasTextHelper
		 */
		public static final SourceModel.Expr buildTableAndAliasTextHelper(boolean includeAsKeywordWithAlias, SourceModel.Expr builder, SourceModel.Expr builderState, SourceModel.Expr table) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.buildTableAndAliasTextHelper), SourceModel.Expr.makeBooleanValue(includeAsKeywordWithAlias), builder, builderState, table});
		}

		/**
		 * Name binding for function: buildTableAndAliasTextHelper.
		 * @see #buildTableAndAliasTextHelper(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName buildTableAndAliasTextHelper = 
			QualifiedName.make(
				CAL_DefaultSqlBuilder.MODULE_NAME, 
				"buildTableAndAliasTextHelper");

		/**
		 * A default SQL builder which attempts to construct SQL using elements common to most popular databases.
		 * @return (CAL type: <code>Cal.Data.Sql.SqlBuilder</code>) 
		 */
		public static final SourceModel.Expr defaultSqlBuilder() {
			return SourceModel.Expr.Var.make(Functions.defaultSqlBuilder);
		}

		/**
		 * Name binding for function: defaultSqlBuilder.
		 * @see #defaultSqlBuilder()
		 */
		public static final QualifiedName defaultSqlBuilder = 
			QualifiedName.make(
				CAL_DefaultSqlBuilder.MODULE_NAME, 
				"defaultSqlBuilder");

		/**
		 * Functions for the default SQL builder.
		 * Other SqlBuilders can be made by replacing the functions in this record with functions which provide database-specific functionality.
		 * The new record of functions can then be passed to sqlBuilderFromFunctions to construct the SqlBuilder.
		 * @return (CAL type: <code>{addParens :: Cal.Utilities.PrettyPrinter.Document -> Cal.Utilities.PrettyPrinter.Document, booleanToSql :: Cal.Core.Prelude.Boolean -> Cal.Utilities.PrettyPrinter.Document, buildCommitStatement :: Cal.Data.Sql.SqlBuilder -> Cal.Utilities.PrettyPrinter.Document, buildCreateDatabaseStatement :: Cal.Data.Sql.SqlBuilder -> Cal.Data.DatabaseMetadata.DatabaseReference -> Cal.Core.Prelude.Boolean -> Cal.Utilities.PrettyPrinter.Document, buildCreateTableStatement :: Cal.Data.Sql.SqlBuilder -> Cal.Data.DatabaseMetadata.TableDescription -> Cal.Core.Prelude.Boolean -> Cal.Utilities.PrettyPrinter.Document, buildCreateViewStatement :: Cal.Data.Sql.SqlBuilder -> Cal.Data.DatabaseMetadata.TableReference -> Cal.Data.Sql.Query -> Cal.Core.Prelude.Boolean -> Cal.Utilities.PrettyPrinter.Document, buildDeleteRowsStatement :: Cal.Data.Sql.SqlBuilder -> Cal.Data.Sql.SqlBuilderState -> Cal.Data.DatabaseMetadata.TableReference -> Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Boolean -> Cal.Utilities.PrettyPrinter.Document, buildDropDatabaseStatement :: Cal.Data.Sql.SqlBuilder -> Cal.Data.DatabaseMetadata.DatabaseReference -> Cal.Core.Prelude.Boolean -> Cal.Utilities.PrettyPrinter.Document, buildDropTableStatement :: Cal.Data.Sql.SqlBuilder -> Cal.Data.DatabaseMetadata.TableReference -> Cal.Core.Prelude.Boolean -> Cal.Utilities.PrettyPrinter.Document, buildDropViewStatement :: Cal.Data.Sql.SqlBuilder -> Cal.Data.DatabaseMetadata.TableReference -> Cal.Core.Prelude.Boolean -> Cal.Utilities.PrettyPrinter.Document, buildFieldDescription :: Cal.Data.Sql.SqlBuilder -> Cal.Data.DatabaseMetadata.FieldDescription -> Cal.Utilities.PrettyPrinter.Document, buildFieldType :: Cal.Data.SqlType.SqlType -> Cal.Utilities.PrettyPrinter.Document, buildFromClause :: Cal.Data.Sql.SqlBuilder -> Cal.Data.Sql.SqlBuilderState -> [Cal.Data.Sql.JoinNode] -> Cal.Utilities.PrettyPrinter.Document, buildGroupByClause :: Cal.Data.Sql.SqlBuilder -> Cal.Data.Sql.SqlBuilderState -> [Cal.Data.Sql.Expr] -> Cal.Utilities.PrettyPrinter.Document, buildHavingClause :: Cal.Data.Sql.SqlBuilder -> Cal.Data.Sql.SqlBuilderState -> Cal.Core.Prelude.Maybe Cal.Data.Sql.Expr -> Cal.Utilities.PrettyPrinter.Document, buildInsertQueryValuesStatement :: Cal.Data.Sql.SqlBuilder -> Cal.Data.DatabaseMetadata.TableReference -> Cal.Core.Prelude.Maybe [Cal.Core.Prelude.String] -> Cal.Data.Sql.Query -> Cal.Utilities.PrettyPrinter.Document, buildInsertValuesStatement :: Cal.Data.Sql.SqlBuilder -> Cal.Data.DatabaseMetadata.TableReference -> Cal.Core.Prelude.Maybe [Cal.Core.Prelude.String] -> [Cal.Data.Sql.Expr] -> Cal.Utilities.PrettyPrinter.Document, buildOrderByClause :: Cal.Data.Sql.SqlBuilder -> Cal.Data.Sql.SqlBuilderState -> [(Cal.Data.Sql.Expr, Cal.Core.Prelude.Boolean)] -> Cal.Utilities.PrettyPrinter.Document, buildSelectClause :: Cal.Data.Sql.SqlBuilder -> Cal.Data.Sql.SqlBuilderState -> [Cal.Data.Sql.QueryOption] -> [(Cal.Data.Sql.Expr, Cal.Core.Prelude.String)] -> Cal.Utilities.PrettyPrinter.Document, buildTableAndAliasText :: Cal.Data.Sql.SqlBuilder -> Cal.Data.Sql.SqlBuilderState -> Cal.Data.Sql.QueryTable -> Cal.Utilities.PrettyPrinter.Document, buildUpdateValuesStatement :: Cal.Data.Sql.SqlBuilder -> Cal.Data.Sql.SqlBuilderState -> Cal.Data.DatabaseMetadata.TableReference -> [Cal.Core.Prelude.String] -> [Cal.Data.Sql.Expr] -> Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Boolean -> Cal.Utilities.PrettyPrinter.Document, buildWhereClause :: Cal.Data.Sql.SqlBuilder -> Cal.Data.Sql.SqlBuilderState -> Cal.Core.Prelude.Maybe Cal.Data.Sql.Expr -> [Cal.Data.Sql.JoinNode] -> Cal.Utilities.PrettyPrinter.Document, constructQuery :: Cal.Data.Sql.SqlBuilder -> Cal.Data.Sql.SqlBuilderState -> Cal.Utilities.PrettyPrinter.Document -> Cal.Utilities.PrettyPrinter.Document -> Cal.Utilities.PrettyPrinter.Document -> Cal.Utilities.PrettyPrinter.Document -> Cal.Utilities.PrettyPrinter.Document -> Cal.Utilities.PrettyPrinter.Document -> Cal.Utilities.PrettyPrinter.Document, constructUnionQuery :: Cal.Data.Sql.SqlBuilder -> Cal.Data.Sql.SqlBuilderState -> Cal.Utilities.PrettyPrinter.Document -> Cal.Utilities.PrettyPrinter.Document -> Cal.Core.Prelude.Boolean -> Cal.Utilities.PrettyPrinter.Document, doubleToSql :: Cal.Core.Prelude.Double -> Cal.Utilities.PrettyPrinter.Document, functionName :: Cal.Data.Sql.DbFunction -> Cal.Core.Prelude.String, functionToSql :: Cal.Data.Sql.SqlBuilder -> Cal.Data.Sql.SqlBuilderState -> Cal.Data.Sql.DbFunction -> [Cal.Data.Sql.Expr] -> Cal.Utilities.PrettyPrinter.Document, intToSql :: Cal.Core.Prelude.Int -> Cal.Utilities.PrettyPrinter.Document, listToSql :: Cal.Data.Sql.SqlBuilder -> Cal.Data.Sql.SqlBuilderState -> [Cal.Data.Sql.Expr] -> Cal.Utilities.PrettyPrinter.Document, makeSafeName :: Cal.Core.Prelude.String -> Cal.Core.Prelude.String, nullToSql :: Cal.Utilities.PrettyPrinter.Document, operatorText :: Cal.Data.Sql.DbFunction -> Cal.Core.Prelude.String, parameterToSql :: Cal.Data.Sql.SqlBuilder -> Cal.Data.Sql.Parameter -> Cal.Utilities.PrettyPrinter.Document, prepareQuery :: Cal.Data.Sql.SqlBuilderState -> Cal.Data.Sql.Query -> Cal.Data.Sql.Query, quoteIdentifier :: Cal.Core.Prelude.String -> Cal.Utilities.PrettyPrinter.Document, stringToSql :: Cal.Core.Prelude.String -> Cal.Utilities.PrettyPrinter.Document, timeIntervalToSql :: Cal.Data.Sql.TimeInterval -> Cal.Utilities.PrettyPrinter.Document, timeToSql :: Cal.Utilities.Time.Time -> Cal.Utilities.TimeZone.TimeZone -> Cal.Utilities.PrettyPrinter.Document}</code>) 
		 */
		public static final SourceModel.Expr defaultSqlBuilderFunctions() {
			return 
				SourceModel.Expr.Var.make(Functions.defaultSqlBuilderFunctions);
		}

		/**
		 * Name binding for function: defaultSqlBuilderFunctions.
		 * @see #defaultSqlBuilderFunctions()
		 */
		public static final QualifiedName defaultSqlBuilderFunctions = 
			QualifiedName.make(
				CAL_DefaultSqlBuilder.MODULE_NAME, 
				"defaultSqlBuilderFunctions");

		/**
		 * Returns whether the specified identifier needs to be quoted.
		 * @param txt (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr default_quotesNeeded(SourceModel.Expr txt) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.default_quotesNeeded), txt});
		}

		/**
		 * @see #default_quotesNeeded(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param txt
		 * @return the SourceModel.Expr representing an application of default_quotesNeeded
		 */
		public static final SourceModel.Expr default_quotesNeeded(java.lang.String txt) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.default_quotesNeeded), SourceModel.Expr.makeStringValue(txt)});
		}

		/**
		 * Name binding for function: default_quotesNeeded.
		 * @see #default_quotesNeeded(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName default_quotesNeeded = 
			QualifiedName.make(
				CAL_DefaultSqlBuilder.MODULE_NAME, 
				"default_quotesNeeded");

		/**
		 * Limits the length of the projected column aliases in the query.
		 * @param maxColumnAliasLen (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @param query (CAL type: <code>Cal.Data.Sql.Query</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.Query</code>) 
		 */
		public static final SourceModel.Expr limitColumnAliasLength(SourceModel.Expr maxColumnAliasLen, SourceModel.Expr query) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.limitColumnAliasLength), maxColumnAliasLen, query});
		}

		/**
		 * @see #limitColumnAliasLength(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param maxColumnAliasLen
		 * @param query
		 * @return the SourceModel.Expr representing an application of limitColumnAliasLength
		 */
		public static final SourceModel.Expr limitColumnAliasLength(int maxColumnAliasLen, SourceModel.Expr query) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.limitColumnAliasLength), SourceModel.Expr.makeIntValue(maxColumnAliasLen), query});
		}

		/**
		 * Name binding for function: limitColumnAliasLength.
		 * @see #limitColumnAliasLength(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName limitColumnAliasLength = 
			QualifiedName.make(
				CAL_DefaultSqlBuilder.MODULE_NAME, 
				"limitColumnAliasLength");

		/**
		 * Returns a SQL-safe version of the specified name and trim the trailing
		 * characters if the length of the name exceeds the specified maximum length.
		 * @param safeNameFn (CAL type: <code>Cal.Core.Prelude.String -> Cal.Core.Prelude.String</code>)
		 * @param maxLength (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @param name (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr makeSafeNameWithLengthCheck(SourceModel.Expr safeNameFn, SourceModel.Expr maxLength, SourceModel.Expr name) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeSafeNameWithLengthCheck), safeNameFn, maxLength, name});
		}

		/**
		 * @see #makeSafeNameWithLengthCheck(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param safeNameFn
		 * @param maxLength
		 * @param name
		 * @return the SourceModel.Expr representing an application of makeSafeNameWithLengthCheck
		 */
		public static final SourceModel.Expr makeSafeNameWithLengthCheck(SourceModel.Expr safeNameFn, int maxLength, java.lang.String name) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeSafeNameWithLengthCheck), safeNameFn, SourceModel.Expr.makeIntValue(maxLength), SourceModel.Expr.makeStringValue(name)});
		}

		/**
		 * Name binding for function: makeSafeNameWithLengthCheck.
		 * @see #makeSafeNameWithLengthCheck(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeSafeNameWithLengthCheck = 
			QualifiedName.make(
				CAL_DefaultSqlBuilder.MODULE_NAME, 
				"makeSafeNameWithLengthCheck");

		/**
		 * Adds quotes, if needed to the specified identifier.
		 * For a qualified table name, this will quote each piece of the name separately.
		 * @param quotesNeededFn (CAL type: <code>Cal.Core.Prelude.String -> Cal.Core.Prelude.Boolean</code>)
		 * @param openQuote (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @param closeQuote (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @param txt (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Utilities.PrettyPrinter.Document</code>) 
		 */
		public static final SourceModel.Expr quoteIdentifierHelper(SourceModel.Expr quotesNeededFn, SourceModel.Expr openQuote, SourceModel.Expr closeQuote, SourceModel.Expr txt) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.quoteIdentifierHelper), quotesNeededFn, openQuote, closeQuote, txt});
		}

		/**
		 * @see #quoteIdentifierHelper(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param quotesNeededFn
		 * @param openQuote
		 * @param closeQuote
		 * @param txt
		 * @return the SourceModel.Expr representing an application of quoteIdentifierHelper
		 */
		public static final SourceModel.Expr quoteIdentifierHelper(SourceModel.Expr quotesNeededFn, java.lang.String openQuote, java.lang.String closeQuote, java.lang.String txt) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.quoteIdentifierHelper), quotesNeededFn, SourceModel.Expr.makeStringValue(openQuote), SourceModel.Expr.makeStringValue(closeQuote), SourceModel.Expr.makeStringValue(txt)});
		}

		/**
		 * Name binding for function: quoteIdentifierHelper.
		 * @see #quoteIdentifierHelper(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName quoteIdentifierHelper = 
			QualifiedName.make(
				CAL_DefaultSqlBuilder.MODULE_NAME, 
				"quoteIdentifierHelper");

		/**
		 * Returns whether the specified identifier needs to be quoted.
		 * @param isValidFirstSqlChar (CAL type: <code>Cal.Core.Prelude.Char -> Cal.Core.Prelude.Boolean</code>)
		 * @param isValidSqlChar (CAL type: <code>Cal.Core.Prelude.Char -> Cal.Core.Prelude.Boolean</code>)
		 * @param txt (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr quotesNeededHelper(SourceModel.Expr isValidFirstSqlChar, SourceModel.Expr isValidSqlChar, SourceModel.Expr txt) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.quotesNeededHelper), isValidFirstSqlChar, isValidSqlChar, txt});
		}

		/**
		 * @see #quotesNeededHelper(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param isValidFirstSqlChar
		 * @param isValidSqlChar
		 * @param txt
		 * @return the SourceModel.Expr representing an application of quotesNeededHelper
		 */
		public static final SourceModel.Expr quotesNeededHelper(SourceModel.Expr isValidFirstSqlChar, SourceModel.Expr isValidSqlChar, java.lang.String txt) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.quotesNeededHelper), isValidFirstSqlChar, isValidSqlChar, SourceModel.Expr.makeStringValue(txt)});
		}

		/**
		 * Name binding for function: quotesNeededHelper.
		 * @see #quotesNeededHelper(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName quotesNeededHelper = 
			QualifiedName.make(
				CAL_DefaultSqlBuilder.MODULE_NAME, 
				"quotesNeededHelper");

		/**
		 * Quote names which contain lower case letters as well.
		 * @param txt (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr quotesNeeded_allowUppercaseOnly(SourceModel.Expr txt) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.quotesNeeded_allowUppercaseOnly), txt});
		}

		/**
		 * @see #quotesNeeded_allowUppercaseOnly(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param txt
		 * @return the SourceModel.Expr representing an application of quotesNeeded_allowUppercaseOnly
		 */
		public static final SourceModel.Expr quotesNeeded_allowUppercaseOnly(java.lang.String txt) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.quotesNeeded_allowUppercaseOnly), SourceModel.Expr.makeStringValue(txt)});
		}

		/**
		 * Name binding for function: quotesNeeded_allowUppercaseOnly.
		 * @see #quotesNeeded_allowUppercaseOnly(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName quotesNeeded_allowUppercaseOnly = 
			QualifiedName.make(
				CAL_DefaultSqlBuilder.MODULE_NAME, 
				"quotesNeeded_allowUppercaseOnly");

		/**
		 * Convert TopN queries to restrict the number of rows using the ranking functions.
		 * @param query (CAL type: <code>Cal.Data.Sql.Query</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.Query</code>) 
		 */
		public static final SourceModel.Expr restructureTopNToRanking(SourceModel.Expr query) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.restructureTopNToRanking), query});
		}

		/**
		 * Name binding for function: restructureTopNToRanking.
		 * @see #restructureTopNToRanking(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName restructureTopNToRanking = 
			QualifiedName.make(
				CAL_DefaultSqlBuilder.MODULE_NAME, 
				"restructureTopNToRanking");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = 2087035338;

}
