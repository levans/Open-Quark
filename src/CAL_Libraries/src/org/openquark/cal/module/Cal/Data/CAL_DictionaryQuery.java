/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_DictionaryQuery.java)
 * was generated from CAL module: Cal.Data.DictionaryQuery.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Data.DictionaryQuery module from Java code.
 *  
 * Creation date: Tue Oct 09 17:29:10 PDT 2007
 * --!>
 *  
 */

package org.openquark.cal.module.Cal.Data;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * This module contains the model for constructing high-level database queries
 * against data dictionaries.
 * Much of this model resembles the <code>Cal.Data.Sql</code> module functionality; however
 * complex concepts like database tables and joins are omitted.
 * @author Richard Webster
 */
public final class CAL_DictionaryQuery {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Data.DictionaryQuery");

	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Data.DictionaryQuery module.
	 */
	public static final class TypeConstructors {
		/**
		 * The field aggregation type indicates how values of the field should be combined to get a single result value.
		 */
		public static final QualifiedName AggregationType = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"AggregationType");

		/**
		 * A database field exposed from a data dictionary.
		 */
		public static final QualifiedName DatabaseField = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "DatabaseField");

		/**
		 * A database expression which can be used in a dictionary query.
		 * Expressions can reference database fields, constant values, database functions applied to other expressions, etc....
		 */
		public static final QualifiedName Expr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "Expr");

		/**
		 * The field type inicates whether a database field represents a dimension, measure, detail, or filter.
		 * This is not the same as the value type (string, double, int, etc...) of a database field.
		 */
		public static final QualifiedName FieldType = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "FieldType");

		/** Name binding for TypeConsApp: JDatabaseField. */
		public static final QualifiedName JDatabaseField = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"JDatabaseField");

		/**
		 * A set of joins (or context).
		 * This type only identifies the join set.
		 * The dictionary will have its own private implementation of the actual joins.
		 */
		public static final QualifiedName JoinSet = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "JoinSet");

		/**
		 * A dictionary query uses database expressions to specify a desired set of result data.
		 * Database expressions are used to specify the projected columns as well as to order and filter the results.
		 */
		public static final QualifiedName Query = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "Query");

		/**
		 * <code>TypedExpr</code> wraps an untyped <code>Cal.Data.DictionaryQuery.Expr</code> and adds information about the expression data type.
		 */
		public static final QualifiedName TypedExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "TypedExpr");

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Data.DictionaryQuery module.
	 */
	public static final class Functions {
		/**
		 * Helper binding method for function: absExpr. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of absExpr
		 */
		public static final SourceModel.Expr absExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.absExpr), arg_1});
		}

		/**
		 * Name binding for function: absExpr.
		 * @see #absExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName absExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "absExpr");

		/**
		 * Returns the <code>Cal.Data.Sql.Expr</code> corresponding to this abstract expression.
		 * The specified field conversion function will be used to convert dictionary field references to
		 * the underlying Sql expression.
		 * @param fieldLookupFn (CAL type: <code>Cal.Data.DictionaryQuery.DatabaseField -> Cal.Data.Sql.Expr</code>)
		 * @param convertQueryFn (CAL type: <code>Cal.Data.DictionaryQuery.Query -> Cal.Data.Sql.Query</code>)
		 * @param abstractExpr (CAL type: <code>Cal.Data.DictionaryQuery.Expr</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.Expr</code>) 
		 */
		public static final SourceModel.Expr abstractExpressionToSqlExpression(SourceModel.Expr fieldLookupFn, SourceModel.Expr convertQueryFn, SourceModel.Expr abstractExpr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.abstractExpressionToSqlExpression), fieldLookupFn, convertQueryFn, abstractExpr});
		}

		/**
		 * Name binding for function: abstractExpressionToSqlExpression.
		 * @see #abstractExpressionToSqlExpression(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName abstractExpressionToSqlExpression = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"abstractExpressionToSqlExpression");

		/**
		 * Helper binding method for function: acosExpr. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of acosExpr
		 */
		public static final SourceModel.Expr acosExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.acosExpr), arg_1});
		}

		/**
		 * Name binding for function: acosExpr.
		 * @see #acosExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName acosExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "acosExpr");

		/**
		 * Adds an anchor expression to the query.
		 * This will force the underlying database tabled referenced by the anchor expression to be included in the query.
		 * However, the anchor expression will not be projected from the query.
		 * @param query (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @param anchorExpr (CAL type: <code>Cal.Data.DictionaryQuery.TypedExpr a</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>) 
		 */
		public static final SourceModel.Expr addAnchorExpression(SourceModel.Expr query, SourceModel.Expr anchorExpr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.addAnchorExpression), query, anchorExpr});
		}

		/**
		 * Name binding for function: addAnchorExpression.
		 * @see #addAnchorExpression(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName addAnchorExpression = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"addAnchorExpression");

		/**
		 * Adds anchor expressions (which force the tables used to be included in the query).
		 * @param query (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @param newAnchorExprs (CAL type: <code>[Cal.Data.DictionaryQuery.Expr]</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>) 
		 */
		public static final SourceModel.Expr addAnchorExpressions(SourceModel.Expr query, SourceModel.Expr newAnchorExprs) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.addAnchorExpressions), query, newAnchorExprs});
		}

		/**
		 * Name binding for function: addAnchorExpressions.
		 * @see #addAnchorExpressions(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName addAnchorExpressions = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"addAnchorExpressions");

		/**
		 * Helper binding method for function: addExpr. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of addExpr
		 */
		public static final SourceModel.Expr addExpr(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.addExpr), arg_1, arg_2});
		}

		/**
		 * Name binding for function: addExpr.
		 * @see #addExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName addExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "addExpr");

		/**
		 * Adds an option to the query.
		 * @param query (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @param newOption (CAL type: <code>Cal.Data.Sql.QueryOption</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>) 
		 */
		public static final SourceModel.Expr addOption(SourceModel.Expr query, SourceModel.Expr newOption) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.addOption), query, newOption});
		}

		/**
		 * Name binding for function: addOption.
		 * @see #addOption(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName addOption = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "addOption");

		/**
		 * Adds a filter to the query.
		 * The database field specified must be of the filter type.
		 * @param query (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @param newFilter (CAL type: <code>Cal.Data.DictionaryQuery.DatabaseField</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>) 
		 */
		public static final SourceModel.Expr addQueryFilter(SourceModel.Expr query, SourceModel.Expr newFilter) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.addQueryFilter), query, newFilter});
		}

		/**
		 * Name binding for function: addQueryFilter.
		 * @see #addQueryFilter(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName addQueryFilter = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"addQueryFilter");

		/**
		 * Returns a list of queries used by this query (and the query itself).
		 * The list will be ordered such that a query's child queries will follow it.
		 * The root query will be the first one in the list returned.
		 * @param query (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @return (CAL type: <code>[Cal.Data.DictionaryQuery.Query]</code>) 
		 */
		public static final SourceModel.Expr allComponentQueries(SourceModel.Expr query) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.allComponentQueries), query});
		}

		/**
		 * Name binding for function: allComponentQueries.
		 * @see #allComponentQueries(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName allComponentQueries = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"allComponentQueries");

		/**
		 * Returns the list of anchoring expressions (which force the tables used to be included in the query).
		 * For <code>Cal.Data.DictionaryQuery.Union</code> queries this returns the anchor expressions for the first query.
		 * @param query (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @return (CAL type: <code>[Cal.Data.DictionaryQuery.Expr]</code>) 
		 */
		public static final SourceModel.Expr anchoringExpressions(SourceModel.Expr query) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.anchoringExpressions), query});
		}

		/**
		 * Name binding for function: anchoringExpressions.
		 * @see #anchoringExpressions(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName anchoringExpressions = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"anchoringExpressions");

		/**
		 * Helper binding method for function: andExpr. 
		 * @param b1
		 * @param b2
		 * @return the SourceModule.expr representing an application of andExpr
		 */
		public static final SourceModel.Expr andExpr(SourceModel.Expr b1, SourceModel.Expr b2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.andExpr), b1, b2});
		}

		/**
		 * Name binding for function: andExpr.
		 * @see #andExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName andExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "andExpr");

		/**
		 * Helper binding method for function: asciiExpr. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of asciiExpr
		 */
		public static final SourceModel.Expr asciiExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.asciiExpr), arg_1});
		}

		/**
		 * Name binding for function: asciiExpr.
		 * @see #asciiExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName asciiExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "asciiExpr");

		/**
		 * Helper binding method for function: asinExpr. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of asinExpr
		 */
		public static final SourceModel.Expr asinExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.asinExpr), arg_1});
		}

		/**
		 * Name binding for function: asinExpr.
		 * @see #asinExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName asinExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "asinExpr");

		/**
		 * Helper binding method for function: atan2Expr. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of atan2Expr
		 */
		public static final SourceModel.Expr atan2Expr(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.atan2Expr), arg_1, arg_2});
		}

		/**
		 * Name binding for function: atan2Expr.
		 * @see #atan2Expr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName atan2Expr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "atan2Expr");

		/**
		 * Helper binding method for function: atanExpr. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of atanExpr
		 */
		public static final SourceModel.Expr atanExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.atanExpr), arg_1});
		}

		/**
		 * Name binding for function: atanExpr.
		 * @see #atanExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName atanExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "atanExpr");

		/**
		 * Helper binding method for function: avgAggregationType. 
		 * @return the SourceModule.expr representing an application of avgAggregationType
		 */
		public static final SourceModel.Expr avgAggregationType() {
			return SourceModel.Expr.Var.make(Functions.avgAggregationType);
		}

		/**
		 * Name binding for function: avgAggregationType.
		 * @see #avgAggregationType()
		 */
		public static final QualifiedName avgAggregationType = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"avgAggregationType");

		/**
		 * Helper binding method for function: avgExpr. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of avgExpr
		 */
		public static final SourceModel.Expr avgExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.avgExpr), arg_1});
		}

		/**
		 * Name binding for function: avgExpr.
		 * @see #avgExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName avgExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "avgExpr");

		/**
		 * Helper binding method for function: betweenExpr. 
		 * @param leftExpr
		 * @param lowerExpr
		 * @param upperExpr
		 * @return the SourceModule.expr representing an application of betweenExpr
		 */
		public static final SourceModel.Expr betweenExpr(SourceModel.Expr leftExpr, SourceModel.Expr lowerExpr, SourceModel.Expr upperExpr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.betweenExpr), leftExpr, lowerExpr, upperExpr});
		}

		/**
		 * Name binding for function: betweenExpr.
		 * @see #betweenExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName betweenExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "betweenExpr");

		/**
		 * Helper binding method for function: binaryDatabaseField. 
		 * @param fieldType
		 * @param name
		 * @return the SourceModule.expr representing an application of binaryDatabaseField
		 */
		public static final SourceModel.Expr binaryDatabaseField(SourceModel.Expr fieldType, SourceModel.Expr name) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.binaryDatabaseField), fieldType, name});
		}

		/**
		 * @see #binaryDatabaseField(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param fieldType
		 * @param name
		 * @return the SourceModel.Expr representing an application of binaryDatabaseField
		 */
		public static final SourceModel.Expr binaryDatabaseField(SourceModel.Expr fieldType, java.lang.String name) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.binaryDatabaseField), fieldType, SourceModel.Expr.makeStringValue(name)});
		}

		/**
		 * Name binding for function: binaryDatabaseField.
		 * @see #binaryDatabaseField(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName binaryDatabaseField = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"binaryDatabaseField");

		/**
		 * Helper binding method for function: binaryDatabaseField2. 
		 * @param fieldType
		 * @param name
		 * @param uniqueID
		 * @param isPreaggregated
		 * @return the SourceModule.expr representing an application of binaryDatabaseField2
		 */
		public static final SourceModel.Expr binaryDatabaseField2(SourceModel.Expr fieldType, SourceModel.Expr name, SourceModel.Expr uniqueID, SourceModel.Expr isPreaggregated) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.binaryDatabaseField2), fieldType, name, uniqueID, isPreaggregated});
		}

		/**
		 * @see #binaryDatabaseField2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param fieldType
		 * @param name
		 * @param uniqueID
		 * @param isPreaggregated
		 * @return the SourceModel.Expr representing an application of binaryDatabaseField2
		 */
		public static final SourceModel.Expr binaryDatabaseField2(SourceModel.Expr fieldType, java.lang.String name, SourceModel.Expr uniqueID, boolean isPreaggregated) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.binaryDatabaseField2), fieldType, SourceModel.Expr.makeStringValue(name), uniqueID, SourceModel.Expr.makeBooleanValue(isPreaggregated)});
		}

		/**
		 * Name binding for function: binaryDatabaseField2.
		 * @see #binaryDatabaseField2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName binaryDatabaseField2 = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"binaryDatabaseField2");

		/**
		 * Helper binding method for function: binaryField. 
		 * @param field
		 * @return the SourceModule.expr representing an application of binaryField
		 */
		public static final SourceModel.Expr binaryField(SourceModel.Expr field) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.binaryField), field});
		}

		/**
		 * Name binding for function: binaryField.
		 * @see #binaryField(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName binaryField = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "binaryField");

		/**
		 * Helper binding method for function: bitwiseAndExpr. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of bitwiseAndExpr
		 */
		public static final SourceModel.Expr bitwiseAndExpr(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.bitwiseAndExpr), arg_1, arg_2});
		}

		/**
		 * Name binding for function: bitwiseAndExpr.
		 * @see #bitwiseAndExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName bitwiseAndExpr = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"bitwiseAndExpr");

		/**
		 * Helper binding method for function: bitwiseNotExpr. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of bitwiseNotExpr
		 */
		public static final SourceModel.Expr bitwiseNotExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.bitwiseNotExpr), arg_1});
		}

		/**
		 * Name binding for function: bitwiseNotExpr.
		 * @see #bitwiseNotExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName bitwiseNotExpr = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"bitwiseNotExpr");

		/**
		 * Helper binding method for function: bitwiseOrExpr. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of bitwiseOrExpr
		 */
		public static final SourceModel.Expr bitwiseOrExpr(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.bitwiseOrExpr), arg_1, arg_2});
		}

		/**
		 * Name binding for function: bitwiseOrExpr.
		 * @see #bitwiseOrExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName bitwiseOrExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "bitwiseOrExpr");

		/**
		 * Helper binding method for function: bitwiseXorExpr. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of bitwiseXorExpr
		 */
		public static final SourceModel.Expr bitwiseXorExpr(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.bitwiseXorExpr), arg_1, arg_2});
		}

		/**
		 * Name binding for function: bitwiseXorExpr.
		 * @see #bitwiseXorExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName bitwiseXorExpr = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"bitwiseXorExpr");

		/**
		 * Helper binding method for function: booleanConstant. 
		 * @param boolValue
		 * @return the SourceModule.expr representing an application of booleanConstant
		 */
		public static final SourceModel.Expr booleanConstant(SourceModel.Expr boolValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanConstant), boolValue});
		}

		/**
		 * @see #booleanConstant(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param boolValue
		 * @return the SourceModel.Expr representing an application of booleanConstant
		 */
		public static final SourceModel.Expr booleanConstant(boolean boolValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanConstant), SourceModel.Expr.makeBooleanValue(boolValue)});
		}

		/**
		 * Name binding for function: booleanConstant.
		 * @see #booleanConstant(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName booleanConstant = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"booleanConstant");

		/**
		 * Helper binding method for function: booleanDatabaseField. 
		 * @param fieldType
		 * @param name
		 * @return the SourceModule.expr representing an application of booleanDatabaseField
		 */
		public static final SourceModel.Expr booleanDatabaseField(SourceModel.Expr fieldType, SourceModel.Expr name) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanDatabaseField), fieldType, name});
		}

		/**
		 * @see #booleanDatabaseField(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param fieldType
		 * @param name
		 * @return the SourceModel.Expr representing an application of booleanDatabaseField
		 */
		public static final SourceModel.Expr booleanDatabaseField(SourceModel.Expr fieldType, java.lang.String name) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanDatabaseField), fieldType, SourceModel.Expr.makeStringValue(name)});
		}

		/**
		 * Name binding for function: booleanDatabaseField.
		 * @see #booleanDatabaseField(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName booleanDatabaseField = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"booleanDatabaseField");

		/**
		 * Helper binding method for function: booleanDatabaseField2. 
		 * @param fieldType
		 * @param name
		 * @param uniqueID
		 * @param isPreaggregated
		 * @return the SourceModule.expr representing an application of booleanDatabaseField2
		 */
		public static final SourceModel.Expr booleanDatabaseField2(SourceModel.Expr fieldType, SourceModel.Expr name, SourceModel.Expr uniqueID, SourceModel.Expr isPreaggregated) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanDatabaseField2), fieldType, name, uniqueID, isPreaggregated});
		}

		/**
		 * @see #booleanDatabaseField2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param fieldType
		 * @param name
		 * @param uniqueID
		 * @param isPreaggregated
		 * @return the SourceModel.Expr representing an application of booleanDatabaseField2
		 */
		public static final SourceModel.Expr booleanDatabaseField2(SourceModel.Expr fieldType, java.lang.String name, SourceModel.Expr uniqueID, boolean isPreaggregated) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanDatabaseField2), fieldType, SourceModel.Expr.makeStringValue(name), uniqueID, SourceModel.Expr.makeBooleanValue(isPreaggregated)});
		}

		/**
		 * Name binding for function: booleanDatabaseField2.
		 * @see #booleanDatabaseField2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName booleanDatabaseField2 = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"booleanDatabaseField2");

		/**
		 * Helper binding method for function: booleanField. 
		 * @param field
		 * @return the SourceModule.expr representing an application of booleanField
		 */
		public static final SourceModel.Expr booleanField(SourceModel.Expr field) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanField), field});
		}

		/**
		 * Name binding for function: booleanField.
		 * @see #booleanField(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName booleanField = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "booleanField");

		/**
		 * Helper binding method for function: ceilingExpr. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of ceilingExpr
		 */
		public static final SourceModel.Expr ceilingExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.ceilingExpr), arg_1});
		}

		/**
		 * Name binding for function: ceilingExpr.
		 * @see #ceilingExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName ceilingExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "ceilingExpr");

		/**
		 * Helper binding method for function: charExpr. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of charExpr
		 */
		public static final SourceModel.Expr charExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charExpr), arg_1});
		}

		/**
		 * Name binding for function: charExpr.
		 * @see #charExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName charExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "charExpr");

		/**
		 * Returns the 2 component queries of a union, intersection, or difference query.
		 * @param query (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @return (CAL type: <code>(Cal.Data.DictionaryQuery.Query, Cal.Data.DictionaryQuery.Query)</code>) 
		 */
		public static final SourceModel.Expr componentQueries(SourceModel.Expr query) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.componentQueries), query});
		}

		/**
		 * Name binding for function: componentQueries.
		 * @see #componentQueries(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName componentQueries = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"componentQueries");

		/**
		 * Helper binding method for function: concatExpr. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of concatExpr
		 */
		public static final SourceModel.Expr concatExpr(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.concatExpr), arg_1, arg_2});
		}

		/**
		 * Name binding for function: concatExpr.
		 * @see #concatExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName concatExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "concatExpr");

		/**
		 * Converts a value to a double value.
		 * @param arg_1 (CAL type: <code>Cal.Data.DictionaryQuery.TypedExpr a</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.TypedExpr Cal.Core.Prelude.Double</code>) 
		 */
		public static final SourceModel.Expr convertToDoubleExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.convertToDoubleExpr), arg_1});
		}

		/**
		 * Name binding for function: convertToDoubleExpr.
		 * @see #convertToDoubleExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName convertToDoubleExpr = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"convertToDoubleExpr");

		/**
		 * Converts a value to a int value.
		 * @param arg_1 (CAL type: <code>Cal.Data.DictionaryQuery.TypedExpr a</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.TypedExpr Cal.Core.Prelude.Int</code>) 
		 */
		public static final SourceModel.Expr convertToIntExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.convertToIntExpr), arg_1});
		}

		/**
		 * Name binding for function: convertToIntExpr.
		 * @see #convertToIntExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName convertToIntExpr = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"convertToIntExpr");

		/**
		 * Converts a value to a string value.
		 * @param arg_1 (CAL type: <code>Cal.Data.DictionaryQuery.TypedExpr a</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.TypedExpr Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr convertToStringExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.convertToStringExpr), arg_1});
		}

		/**
		 * Name binding for function: convertToStringExpr.
		 * @see #convertToStringExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName convertToStringExpr = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"convertToStringExpr");

		/**
		 * Helper binding method for function: cosExpr. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of cosExpr
		 */
		public static final SourceModel.Expr cosExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.cosExpr), arg_1});
		}

		/**
		 * Name binding for function: cosExpr.
		 * @see #cosExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName cosExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "cosExpr");

		/**
		 * Helper binding method for function: cotExpr. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of cotExpr
		 */
		public static final SourceModel.Expr cotExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.cotExpr), arg_1});
		}

		/**
		 * Name binding for function: cotExpr.
		 * @see #cotExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName cotExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "cotExpr");

		/**
		 * Helper binding method for function: countAggregationType. 
		 * @return the SourceModule.expr representing an application of countAggregationType
		 */
		public static final SourceModel.Expr countAggregationType() {
			return SourceModel.Expr.Var.make(Functions.countAggregationType);
		}

		/**
		 * Name binding for function: countAggregationType.
		 * @see #countAggregationType()
		 */
		public static final QualifiedName countAggregationType = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"countAggregationType");

		/**
		 * Helper binding method for function: countAllExpr. 
		 * @return the SourceModule.expr representing an application of countAllExpr
		 */
		public static final SourceModel.Expr countAllExpr() {
			return SourceModel.Expr.Var.make(Functions.countAllExpr);
		}

		/**
		 * Name binding for function: countAllExpr.
		 * @see #countAllExpr()
		 */
		public static final QualifiedName countAllExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "countAllExpr");

		/**
		 * Helper binding method for function: countExpr. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of countExpr
		 */
		public static final SourceModel.Expr countExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.countExpr), arg_1});
		}

		/**
		 * Name binding for function: countExpr.
		 * @see #countExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName countExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "countExpr");

		/**
		 * Helper binding method for function: databaseExpr. 
		 * @return the SourceModule.expr representing an application of databaseExpr
		 */
		public static final SourceModel.Expr databaseExpr() {
			return SourceModel.Expr.Var.make(Functions.databaseExpr);
		}

		/**
		 * Name binding for function: databaseExpr.
		 * @see #databaseExpr()
		 */
		public static final QualifiedName databaseExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "databaseExpr");

		/**
		 * Helper binding method for function: dateTimeAddExpr. 
		 * @param timeInterval
		 * @param arg_2
		 * @param arg_3
		 * @return the SourceModule.expr representing an application of dateTimeAddExpr
		 */
		public static final SourceModel.Expr dateTimeAddExpr(SourceModel.Expr timeInterval, SourceModel.Expr arg_2, SourceModel.Expr arg_3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.dateTimeAddExpr), timeInterval, arg_2, arg_3});
		}

		/**
		 * Name binding for function: dateTimeAddExpr.
		 * @see #dateTimeAddExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName dateTimeAddExpr = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"dateTimeAddExpr");

		/**
		 * Helper binding method for function: dateTimeDiffExpr. 
		 * @param timeInterval
		 * @param arg_2
		 * @param arg_3
		 * @return the SourceModule.expr representing an application of dateTimeDiffExpr
		 */
		public static final SourceModel.Expr dateTimeDiffExpr(SourceModel.Expr timeInterval, SourceModel.Expr arg_2, SourceModel.Expr arg_3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.dateTimeDiffExpr), timeInterval, arg_2, arg_3});
		}

		/**
		 * Name binding for function: dateTimeDiffExpr.
		 * @see #dateTimeDiffExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName dateTimeDiffExpr = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"dateTimeDiffExpr");

		/**
		 * Helper binding method for function: dayNameExpr. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of dayNameExpr
		 */
		public static final SourceModel.Expr dayNameExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.dayNameExpr), arg_1});
		}

		/**
		 * Name binding for function: dayNameExpr.
		 * @see #dayNameExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName dayNameExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "dayNameExpr");

		/**
		 * Helper binding method for function: dayOfMonthExpr. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of dayOfMonthExpr
		 */
		public static final SourceModel.Expr dayOfMonthExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.dayOfMonthExpr), arg_1});
		}

		/**
		 * Name binding for function: dayOfMonthExpr.
		 * @see #dayOfMonthExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName dayOfMonthExpr = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"dayOfMonthExpr");

		/**
		 * Helper binding method for function: dayOfWeekExpr. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of dayOfWeekExpr
		 */
		public static final SourceModel.Expr dayOfWeekExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.dayOfWeekExpr), arg_1});
		}

		/**
		 * Name binding for function: dayOfWeekExpr.
		 * @see #dayOfWeekExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName dayOfWeekExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "dayOfWeekExpr");

		/**
		 * Helper binding method for function: dayOfYearExpr. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of dayOfYearExpr
		 */
		public static final SourceModel.Expr dayOfYearExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.dayOfYearExpr), arg_1});
		}

		/**
		 * Name binding for function: dayOfYearExpr.
		 * @see #dayOfYearExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName dayOfYearExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "dayOfYearExpr");

		/**
		 * Helper binding method for function: degreesExpr. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of degreesExpr
		 */
		public static final SourceModel.Expr degreesExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.degreesExpr), arg_1});
		}

		/**
		 * Name binding for function: degreesExpr.
		 * @see #degreesExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName degreesExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "degreesExpr");

		/**
		 * Helper binding method for function: detailFieldType. 
		 * @return the SourceModule.expr representing an application of detailFieldType
		 */
		public static final SourceModel.Expr detailFieldType() {
			return SourceModel.Expr.Var.make(Functions.detailFieldType);
		}

		/**
		 * Name binding for function: detailFieldType.
		 * @see #detailFieldType()
		 */
		public static final QualifiedName detailFieldType = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"detailFieldType");

		/**
		 * Helper binding method for function: differenceExpr. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of differenceExpr
		 */
		public static final SourceModel.Expr differenceExpr(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.differenceExpr), arg_1, arg_2});
		}

		/**
		 * Name binding for function: differenceExpr.
		 * @see #differenceExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName differenceExpr = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"differenceExpr");

		/**
		 * Returns an query that produces the difference of the specified queries.
		 * The difference will be done on the first N columns in the 2 queries.
		 * <p>
		 * TODO: this function isn't working correctly when one of the queries in a union query...
		 * 
		 * @param query1 (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @param query2 (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>) 
		 */
		public static final SourceModel.Expr differenceQuery(SourceModel.Expr query1, SourceModel.Expr query2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.differenceQuery), query1, query2});
		}

		/**
		 * Name binding for function: differenceQuery.
		 * @see #differenceQuery(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName differenceQuery = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"differenceQuery");

		/**
		 * Returns an query that produces the difference of the specified queries.
		 * The difference will be done on the first columns in each of the 2 queries.
		 * <p>
		 * TODO: get rid of this function and use the differenceQuery function once this is working properly...
		 * 
		 * @param query1 (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @param query2 (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>) 
		 */
		public static final SourceModel.Expr differenceQuerySimple(SourceModel.Expr query1, SourceModel.Expr query2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.differenceQuerySimple), query1, query2});
		}

		/**
		 * Name binding for function: differenceQuerySimple.
		 * @see #differenceQuerySimple(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName differenceQuerySimple = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"differenceQuerySimple");

		/**
		 * Helper binding method for function: dimensionFieldType. 
		 * @return the SourceModule.expr representing an application of dimensionFieldType
		 */
		public static final SourceModel.Expr dimensionFieldType() {
			return SourceModel.Expr.Var.make(Functions.dimensionFieldType);
		}

		/**
		 * Name binding for function: dimensionFieldType.
		 * @see #dimensionFieldType()
		 */
		public static final QualifiedName dimensionFieldType = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"dimensionFieldType");

		/**
		 * Helper binding method for function: distinctAvgExpr. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of distinctAvgExpr
		 */
		public static final SourceModel.Expr distinctAvgExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.distinctAvgExpr), arg_1});
		}

		/**
		 * Name binding for function: distinctAvgExpr.
		 * @see #distinctAvgExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName distinctAvgExpr = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"distinctAvgExpr");

		/**
		 * Helper binding method for function: distinctCountAggregationType. 
		 * @return the SourceModule.expr representing an application of distinctCountAggregationType
		 */
		public static final SourceModel.Expr distinctCountAggregationType() {
			return 
				SourceModel.Expr.Var.make(
					Functions.distinctCountAggregationType);
		}

		/**
		 * Name binding for function: distinctCountAggregationType.
		 * @see #distinctCountAggregationType()
		 */
		public static final QualifiedName distinctCountAggregationType = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"distinctCountAggregationType");

		/**
		 * Helper binding method for function: distinctCountExpr. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of distinctCountExpr
		 */
		public static final SourceModel.Expr distinctCountExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.distinctCountExpr), arg_1});
		}

		/**
		 * Name binding for function: distinctCountExpr.
		 * @see #distinctCountExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName distinctCountExpr = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"distinctCountExpr");

		/**
		 * Helper binding method for function: distinctSumExpr. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of distinctSumExpr
		 */
		public static final SourceModel.Expr distinctSumExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.distinctSumExpr), arg_1});
		}

		/**
		 * Name binding for function: distinctSumExpr.
		 * @see #distinctSumExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName distinctSumExpr = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"distinctSumExpr");

		/**
		 * Helper binding method for function: divideExpr. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of divideExpr
		 */
		public static final SourceModel.Expr divideExpr(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.divideExpr), arg_1, arg_2});
		}

		/**
		 * Name binding for function: divideExpr.
		 * @see #divideExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName divideExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "divideExpr");

		/**
		 * Helper binding method for function: doubleDatabaseField. 
		 * @param fieldType
		 * @param name
		 * @return the SourceModule.expr representing an application of doubleDatabaseField
		 */
		public static final SourceModel.Expr doubleDatabaseField(SourceModel.Expr fieldType, SourceModel.Expr name) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleDatabaseField), fieldType, name});
		}

		/**
		 * @see #doubleDatabaseField(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param fieldType
		 * @param name
		 * @return the SourceModel.Expr representing an application of doubleDatabaseField
		 */
		public static final SourceModel.Expr doubleDatabaseField(SourceModel.Expr fieldType, java.lang.String name) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleDatabaseField), fieldType, SourceModel.Expr.makeStringValue(name)});
		}

		/**
		 * Name binding for function: doubleDatabaseField.
		 * @see #doubleDatabaseField(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleDatabaseField = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"doubleDatabaseField");

		/**
		 * Helper binding method for function: doubleDatabaseField2. 
		 * @param fieldType
		 * @param name
		 * @param uniqueID
		 * @param isPreaggregated
		 * @return the SourceModule.expr representing an application of doubleDatabaseField2
		 */
		public static final SourceModel.Expr doubleDatabaseField2(SourceModel.Expr fieldType, SourceModel.Expr name, SourceModel.Expr uniqueID, SourceModel.Expr isPreaggregated) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleDatabaseField2), fieldType, name, uniqueID, isPreaggregated});
		}

		/**
		 * @see #doubleDatabaseField2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param fieldType
		 * @param name
		 * @param uniqueID
		 * @param isPreaggregated
		 * @return the SourceModel.Expr representing an application of doubleDatabaseField2
		 */
		public static final SourceModel.Expr doubleDatabaseField2(SourceModel.Expr fieldType, java.lang.String name, SourceModel.Expr uniqueID, boolean isPreaggregated) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleDatabaseField2), fieldType, SourceModel.Expr.makeStringValue(name), uniqueID, SourceModel.Expr.makeBooleanValue(isPreaggregated)});
		}

		/**
		 * Name binding for function: doubleDatabaseField2.
		 * @see #doubleDatabaseField2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleDatabaseField2 = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"doubleDatabaseField2");

		/**
		 * Helper binding method for function: doubleField. 
		 * @param field
		 * @return the SourceModule.expr representing an application of doubleField
		 */
		public static final SourceModel.Expr doubleField(SourceModel.Expr field) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleField), field});
		}

		/**
		 * Name binding for function: doubleField.
		 * @see #doubleField(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleField = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "doubleField");

		/**
		 * Helper binding method for function: eqExpr. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of eqExpr
		 */
		public static final SourceModel.Expr eqExpr(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.eqExpr), arg_1, arg_2});
		}

		/**
		 * Name binding for function: eqExpr.
		 * @see #eqExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName eqExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "eqExpr");

		/**
		 * Helper binding method for function: expExpr. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of expExpr
		 */
		public static final SourceModel.Expr expExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expExpr), arg_1});
		}

		/**
		 * Name binding for function: expExpr.
		 * @see #expExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "expExpr");

		/**
		 * Returns whether the expression uses aggregate functions.
		 * <p>
		 * TODO: is there anything else to look for?
		 * 
		 * @param expr (CAL type: <code>Cal.Data.DictionaryQuery.Expr</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr exprUsesAggregation(SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.exprUsesAggregation), expr});
		}

		/**
		 * Name binding for function: exprUsesAggregation.
		 * @see #exprUsesAggregation(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName exprUsesAggregation = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"exprUsesAggregation");

		/**
		 * Returns a list of the fields used in an expression.
		 * This will not include fields from subqueries.
		 * This will not include filter fields.
		 * <p>
		 * TODO: this should include certain fields from subqueries in some cases (fields which are references to this query from an inner query)...
		 * 
		 * @param expr (CAL type: <code>Cal.Data.DictionaryQuery.Expr</code>)
		 * @return (CAL type: <code>[Cal.Data.DictionaryQuery.DatabaseField]</code>) 
		 */
		public static final SourceModel.Expr expressionFields(SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expressionFields), expr});
		}

		/**
		 * Name binding for function: expressionFields.
		 * @see #expressionFields(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expressionFields = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"expressionFields");

		/**
		 * Helper binding method for function: falseConstant. 
		 * @return the SourceModule.expr representing an application of falseConstant
		 */
		public static final SourceModel.Expr falseConstant() {
			return SourceModel.Expr.Var.make(Functions.falseConstant);
		}

		/**
		 * Name binding for function: falseConstant.
		 * @see #falseConstant()
		 */
		public static final QualifiedName falseConstant = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "falseConstant");

		/**
		 * Returns the default aggregation type of the field (sum, min, max, etc...).
		 * @param field (CAL type: <code>Cal.Data.DictionaryQuery.DatabaseField</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.AggregationType</code>) 
		 */
		public static final SourceModel.Expr fieldDefaultAggregationType(SourceModel.Expr field) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fieldDefaultAggregationType), field});
		}

		/**
		 * Name binding for function: fieldDefaultAggregationType.
		 * @see #fieldDefaultAggregationType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fieldDefaultAggregationType = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"fieldDefaultAggregationType");

		/**
		 * Returns the field type of the field (which is different from the field value type).
		 * @param field (CAL type: <code>Cal.Data.DictionaryQuery.DatabaseField</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.FieldType</code>) 
		 */
		public static final SourceModel.Expr fieldType(SourceModel.Expr field) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fieldType), field});
		}

		/**
		 * Name binding for function: fieldType.
		 * @see #fieldType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fieldType = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "fieldType");

		/**
		 * Returns the value type of a database field.
		 * @param field (CAL type: <code>Cal.Data.DictionaryQuery.DatabaseField</code>)
		 * @return (CAL type: <code>Cal.Utilities.ValueType.ValueType</code>) 
		 */
		public static final SourceModel.Expr fieldValueType(SourceModel.Expr field) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fieldValueType), field});
		}

		/**
		 * Name binding for function: fieldValueType.
		 * @see #fieldValueType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fieldValueType = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"fieldValueType");

		/**
		 * Helper binding method for function: filterFieldType. 
		 * @return the SourceModule.expr representing an application of filterFieldType
		 */
		public static final SourceModel.Expr filterFieldType() {
			return SourceModel.Expr.Var.make(Functions.filterFieldType);
		}

		/**
		 * Name binding for function: filterFieldType.
		 * @see #filterFieldType()
		 */
		public static final QualifiedName filterFieldType = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"filterFieldType");

		/**
		 * Helper binding method for function: floorExpr. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of floorExpr
		 */
		public static final SourceModel.Expr floorExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floorExpr), arg_1});
		}

		/**
		 * Name binding for function: floorExpr.
		 * @see #floorExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName floorExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "floorExpr");

		/**
		 * Returns a list of expressions which are ANDed together, if any.
		 * @param expr (CAL type: <code>Cal.Data.DictionaryQuery.TypedExpr Cal.Core.Prelude.Boolean</code>)
		 * @return (CAL type: <code>[Cal.Data.DictionaryQuery.TypedExpr Cal.Core.Prelude.Boolean]</code>) 
		 */
		public static final SourceModel.Expr getAndedBooleanExprs(SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getAndedBooleanExprs), expr});
		}

		/**
		 * Name binding for function: getAndedBooleanExprs.
		 * @see #getAndedBooleanExprs(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getAndedBooleanExprs = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"getAndedBooleanExprs");

		/**
		 * Returns the arguments from a function expression.
		 * @param expr (CAL type: <code>Cal.Data.DictionaryQuery.Expr</code>)
		 * @return (CAL type: <code>[Cal.Data.DictionaryQuery.Expr]</code>) 
		 */
		public static final SourceModel.Expr getArgumentsFromFunctionExpr(SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getArgumentsFromFunctionExpr), expr});
		}

		/**
		 * Name binding for function: getArgumentsFromFunctionExpr.
		 * @see #getArgumentsFromFunctionExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getArgumentsFromFunctionExpr = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"getArgumentsFromFunctionExpr");

		/**
		 * Returns the list of expressions from a list expression.
		 * @param expr (CAL type: <code>Cal.Data.DictionaryQuery.Expr</code>)
		 * @return (CAL type: <code>[Cal.Data.DictionaryQuery.Expr]</code>) 
		 */
		public static final SourceModel.Expr getExpressionsFromList(SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getExpressionsFromList), expr});
		}

		/**
		 * Name binding for function: getExpressionsFromList.
		 * @see #getExpressionsFromList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getExpressionsFromList = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"getExpressionsFromList");

		/**
		 * Returns the field from a field expression.
		 * @param expr (CAL type: <code>Cal.Data.DictionaryQuery.Expr</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.DatabaseField</code>) 
		 */
		public static final SourceModel.Expr getFieldFromExpr(SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getFieldFromExpr), expr});
		}

		/**
		 * Name binding for function: getFieldFromExpr.
		 * @see #getFieldFromExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getFieldFromExpr = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"getFieldFromExpr");

		/**
		 * Returns the function type from a function expression.
		 * @param expr (CAL type: <code>Cal.Data.DictionaryQuery.Expr</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.DbFunction</code>) 
		 */
		public static final SourceModel.Expr getFunctionFromFunctionExpr(SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getFunctionFromFunctionExpr), expr});
		}

		/**
		 * Name binding for function: getFunctionFromFunctionExpr.
		 * @see #getFunctionFromFunctionExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getFunctionFromFunctionExpr = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"getFunctionFromFunctionExpr");

		/**
		 * Returns the string constant value for the expression.
		 * @param expr (CAL type: <code>Cal.Data.DictionaryQuery.Expr</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr getStringConstantValue(SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getStringConstantValue), expr});
		}

		/**
		 * Name binding for function: getStringConstantValue.
		 * @see #getStringConstantValue(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getStringConstantValue = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"getStringConstantValue");

		/**
		 * Adds grouping to the query.
		 * @param query (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @param newGroup (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Data.DictionaryQuery.TypedExpr a</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>) 
		 */
		public static final SourceModel.Expr group(SourceModel.Expr query, SourceModel.Expr newGroup) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.group), query, newGroup});
		}

		/**
		 * Name binding for function: group.
		 * @see #group(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName group = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "group");

		/**
		 * Adds grouping on the specified fields.
		 * @param query (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @param newGroupFields (CAL type: <code>[Cal.Data.DictionaryQuery.Expr]</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>) 
		 */
		public static final SourceModel.Expr group2(SourceModel.Expr query, SourceModel.Expr newGroupFields) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.group2), query, newGroupFields});
		}

		/**
		 * Name binding for function: group2.
		 * @see #group2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName group2 = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "group2");

		/**
		 * Returns the group restriction expression (if any).
		 * For <code>Cal.Data.DictionaryQuery.Union</code> queries, this only returns the restrictions for the first query.
		 * @param query (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe (Cal.Data.DictionaryQuery.TypedExpr Cal.Core.Prelude.Boolean)</code>) 
		 */
		public static final SourceModel.Expr groupRestrictionExpression(SourceModel.Expr query) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.groupRestrictionExpression), query});
		}

		/**
		 * Name binding for function: groupRestrictionExpression.
		 * @see #groupRestrictionExpression(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName groupRestrictionExpression = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"groupRestrictionExpression");

		/**
		 * Returns the grouping expressions for the query.
		 * For <code>Cal.Data.DictionaryQuery.Union</code> queries this returns the grouping expressions for the first query.
		 * @param query (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @return (CAL type: <code>[Cal.Data.DictionaryQuery.Expr]</code>) 
		 */
		public static final SourceModel.Expr groupingExpressions(SourceModel.Expr query) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.groupingExpressions), query});
		}

		/**
		 * Name binding for function: groupingExpressions.
		 * @see #groupingExpressions(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName groupingExpressions = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"groupingExpressions");

		/**
		 * Helper binding method for function: gtEqExpr. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of gtEqExpr
		 */
		public static final SourceModel.Expr gtEqExpr(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.gtEqExpr), arg_1, arg_2});
		}

		/**
		 * Name binding for function: gtEqExpr.
		 * @see #gtEqExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName gtEqExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "gtEqExpr");

		/**
		 * Helper binding method for function: gtExpr. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of gtExpr
		 */
		public static final SourceModel.Expr gtExpr(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.gtExpr), arg_1, arg_2});
		}

		/**
		 * Name binding for function: gtExpr.
		 * @see #gtExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName gtExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "gtExpr");

		/**
		 * Helper binding method for function: hourExpr. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of hourExpr
		 */
		public static final SourceModel.Expr hourExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.hourExpr), arg_1});
		}

		/**
		 * Name binding for function: hourExpr.
		 * @see #hourExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName hourExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "hourExpr");

		/**
		 * Helper binding method for function: ifNullExpr. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of ifNullExpr
		 */
		public static final SourceModel.Expr ifNullExpr(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.ifNullExpr), arg_1, arg_2});
		}

		/**
		 * Name binding for function: ifNullExpr.
		 * @see #ifNullExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName ifNullExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "ifNullExpr");

		/**
		 * Helper binding method for function: inExpr. 
		 * @param leftExpr
		 * @param listValueExprs
		 * @return the SourceModule.expr representing an application of inExpr
		 */
		public static final SourceModel.Expr inExpr(SourceModel.Expr leftExpr, SourceModel.Expr listValueExprs) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inExpr), leftExpr, listValueExprs});
		}

		/**
		 * Name binding for function: inExpr.
		 * @see #inExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName inExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "inExpr");

		/**
		 * Helper binding method for function: inExpr2. 
		 * @param leftExpr
		 * @param listValuesExpr
		 * @return the SourceModule.expr representing an application of inExpr2
		 */
		public static final SourceModel.Expr inExpr2(SourceModel.Expr leftExpr, SourceModel.Expr listValuesExpr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inExpr2), leftExpr, listValuesExpr});
		}

		/**
		 * Name binding for function: inExpr2.
		 * @see #inExpr2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName inExpr2 = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "inExpr2");

		/**
		 * Helper binding method for function: insertExpr. 
		 * @param stringExpr
		 * @param start
		 * @param length
		 * @param insertStr
		 * @return the SourceModule.expr representing an application of insertExpr
		 */
		public static final SourceModel.Expr insertExpr(SourceModel.Expr stringExpr, SourceModel.Expr start, SourceModel.Expr length, SourceModel.Expr insertStr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.insertExpr), stringExpr, start, length, insertStr});
		}

		/**
		 * Name binding for function: insertExpr.
		 * @see #insertExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName insertExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "insertExpr");

		/**
		 * Inserts ordering at the specified (zero-based) index in the ordering list.
		 * @param query (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @param pos (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @param sortExpr (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Data.DictionaryQuery.TypedExpr a</code>)
		 * @param sortAscending (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>) 
		 */
		public static final SourceModel.Expr insertOrdering(SourceModel.Expr query, SourceModel.Expr pos, SourceModel.Expr sortExpr, SourceModel.Expr sortAscending) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.insertOrdering), query, pos, sortExpr, sortAscending});
		}

		/**
		 * @see #insertOrdering(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param query
		 * @param pos
		 * @param sortExpr
		 * @param sortAscending
		 * @return the SourceModel.Expr representing an application of insertOrdering
		 */
		public static final SourceModel.Expr insertOrdering(SourceModel.Expr query, int pos, SourceModel.Expr sortExpr, boolean sortAscending) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.insertOrdering), query, SourceModel.Expr.makeIntValue(pos), sortExpr, SourceModel.Expr.makeBooleanValue(sortAscending)});
		}

		/**
		 * Name binding for function: insertOrdering.
		 * @see #insertOrdering(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName insertOrdering = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"insertOrdering");

		/**
		 * Inserts ordering info at the specified (zero-based) index in the ordering list.
		 * @param query (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @param pos (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @param newOrderings (CAL type: <code>[(Cal.Data.DictionaryQuery.Expr, Cal.Core.Prelude.Boolean)]</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>) 
		 */
		public static final SourceModel.Expr insertOrderings(SourceModel.Expr query, SourceModel.Expr pos, SourceModel.Expr newOrderings) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.insertOrderings), query, pos, newOrderings});
		}

		/**
		 * @see #insertOrderings(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param query
		 * @param pos
		 * @param newOrderings
		 * @return the SourceModel.Expr representing an application of insertOrderings
		 */
		public static final SourceModel.Expr insertOrderings(SourceModel.Expr query, int pos, SourceModel.Expr newOrderings) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.insertOrderings), query, SourceModel.Expr.makeIntValue(pos), newOrderings});
		}

		/**
		 * Name binding for function: insertOrderings.
		 * @see #insertOrderings(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName insertOrderings = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"insertOrderings");

		/**
		 * Helper binding method for function: intDatabaseField. 
		 * @param fieldType
		 * @param name
		 * @return the SourceModule.expr representing an application of intDatabaseField
		 */
		public static final SourceModel.Expr intDatabaseField(SourceModel.Expr fieldType, SourceModel.Expr name) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intDatabaseField), fieldType, name});
		}

		/**
		 * @see #intDatabaseField(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param fieldType
		 * @param name
		 * @return the SourceModel.Expr representing an application of intDatabaseField
		 */
		public static final SourceModel.Expr intDatabaseField(SourceModel.Expr fieldType, java.lang.String name) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intDatabaseField), fieldType, SourceModel.Expr.makeStringValue(name)});
		}

		/**
		 * Name binding for function: intDatabaseField.
		 * @see #intDatabaseField(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intDatabaseField = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"intDatabaseField");

		/**
		 * Helper binding method for function: intDatabaseField2. 
		 * @param fieldType
		 * @param name
		 * @param uniqueID
		 * @param isPreaggregated
		 * @return the SourceModule.expr representing an application of intDatabaseField2
		 */
		public static final SourceModel.Expr intDatabaseField2(SourceModel.Expr fieldType, SourceModel.Expr name, SourceModel.Expr uniqueID, SourceModel.Expr isPreaggregated) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intDatabaseField2), fieldType, name, uniqueID, isPreaggregated});
		}

		/**
		 * @see #intDatabaseField2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param fieldType
		 * @param name
		 * @param uniqueID
		 * @param isPreaggregated
		 * @return the SourceModel.Expr representing an application of intDatabaseField2
		 */
		public static final SourceModel.Expr intDatabaseField2(SourceModel.Expr fieldType, java.lang.String name, SourceModel.Expr uniqueID, boolean isPreaggregated) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intDatabaseField2), fieldType, SourceModel.Expr.makeStringValue(name), uniqueID, SourceModel.Expr.makeBooleanValue(isPreaggregated)});
		}

		/**
		 * Name binding for function: intDatabaseField2.
		 * @see #intDatabaseField2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intDatabaseField2 = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"intDatabaseField2");

		/**
		 * Helper binding method for function: intField. 
		 * @param field
		 * @return the SourceModule.expr representing an application of intField
		 */
		public static final SourceModel.Expr intField(SourceModel.Expr field) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intField), field});
		}

		/**
		 * Name binding for function: intField.
		 * @see #intField(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intField = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "intField");

		/**
		 * Converts an integer value to a double value.
		 * The assumption here is that the database will do an implicit conversion
		 * between these 2 types, so no function will be applied in the generated SQL.
		 * A function could be used here if necessary.
		 * @param expr (CAL type: <code>Cal.Data.DictionaryQuery.TypedExpr Cal.Core.Prelude.Int</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.TypedExpr Cal.Core.Prelude.Double</code>) 
		 */
		public static final SourceModel.Expr intToDoubleExpr(SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intToDoubleExpr), expr});
		}

		/**
		 * Name binding for function: intToDoubleExpr.
		 * @see #intToDoubleExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intToDoubleExpr = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"intToDoubleExpr");

		/**
		 * Returns an query that intersects the specified queries.
		 * The difference will be done on the first N columns in the 2 queries.
		 * @param query1 (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @param query2 (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>) 
		 */
		public static final SourceModel.Expr intersectionQuery(SourceModel.Expr query1, SourceModel.Expr query2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intersectionQuery), query1, query2});
		}

		/**
		 * Name binding for function: intersectionQuery.
		 * @see #intersectionQuery(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intersectionQuery = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"intersectionQuery");

		/**
		 * Returns an query that produces the intersection of the specified queries.
		 * The intersection will be done on the first columns in each of the 2 queries.
		 * <p>
		 * TODO: get rid of this function and use the intersectionQuery function once this is working properly...
		 * 
		 * @param query1 (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @param query2 (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>) 
		 */
		public static final SourceModel.Expr intersectionQuerySimple(SourceModel.Expr query1, SourceModel.Expr query2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intersectionQuerySimple), query1, query2});
		}

		/**
		 * Name binding for function: intersectionQuerySimple.
		 * @see #intersectionQuerySimple(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intersectionQuerySimple = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"intersectionQuerySimple");

		/**
		 * Returns whether the specified expressions is a constant value.
		 * @param expr (CAL type: <code>Cal.Data.DictionaryQuery.Expr</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr isConstantExpr(SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isConstantExpr), expr});
		}

		/**
		 * Name binding for function: isConstantExpr.
		 * @see #isConstantExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isConstantExpr = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"isConstantExpr");

		/**
		 * Returns whether the field is marked as a detail field.
		 * @param field (CAL type: <code>Cal.Data.DictionaryQuery.DatabaseField</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr isDetailField(SourceModel.Expr field) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isDetailField), field});
		}

		/**
		 * Name binding for function: isDetailField.
		 * @see #isDetailField(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isDetailField = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "isDetailField");

		/**
		 * Returns whether the field is marked as a dimension.
		 * @param field (CAL type: <code>Cal.Data.DictionaryQuery.DatabaseField</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr isDimensionField(SourceModel.Expr field) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isDimensionField), field});
		}

		/**
		 * Name binding for function: isDimensionField.
		 * @see #isDimensionField(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isDimensionField = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"isDimensionField");

		/**
		 * Returns <code>Cal.Core.Prelude.True</code> if the expression represents a field.
		 * @param expr (CAL type: <code>Cal.Data.DictionaryQuery.Expr</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr isFieldExpr(SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isFieldExpr), expr});
		}

		/**
		 * Name binding for function: isFieldExpr.
		 * @see #isFieldExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isFieldExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "isFieldExpr");

		/**
		 * Returns whether a measure field already includes aggregation.
		 * @param field (CAL type: <code>Cal.Data.DictionaryQuery.DatabaseField</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr isFieldPreaggregated(SourceModel.Expr field) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isFieldPreaggregated), field});
		}

		/**
		 * Name binding for function: isFieldPreaggregated.
		 * @see #isFieldPreaggregated(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isFieldPreaggregated = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"isFieldPreaggregated");

		/**
		 * Returns whether the field is marked as a filter.
		 * @param field (CAL type: <code>Cal.Data.DictionaryQuery.DatabaseField</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr isFilterField(SourceModel.Expr field) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isFilterField), field});
		}

		/**
		 * Name binding for function: isFilterField.
		 * @see #isFilterField(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isFilterField = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "isFilterField");

		/**
		 * Returns <code>Cal.Core.Prelude.True</code> if the expression represents a function (or operator) application.
		 * @param expr (CAL type: <code>Cal.Data.DictionaryQuery.Expr</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr isFunctionExpr(SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isFunctionExpr), expr});
		}

		/**
		 * Name binding for function: isFunctionExpr.
		 * @see #isFunctionExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isFunctionExpr = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"isFunctionExpr");

		/**
		 * Returns whether the specified expression is a list of expressions.
		 * @param expr (CAL type: <code>Cal.Data.DictionaryQuery.Expr</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr isListExpr(SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isListExpr), expr});
		}

		/**
		 * Name binding for function: isListExpr.
		 * @see #isListExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isListExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "isListExpr");

		/**
		 * Returns whether the field is marked as a measure.
		 * @param field (CAL type: <code>Cal.Data.DictionaryQuery.DatabaseField</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr isMeasureField(SourceModel.Expr field) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isMeasureField), field});
		}

		/**
		 * Name binding for function: isMeasureField.
		 * @see #isMeasureField(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isMeasureField = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"isMeasureField");

		/**
		 * Helper binding method for function: isNotNullExpr. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of isNotNullExpr
		 */
		public static final SourceModel.Expr isNotNullExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isNotNullExpr), arg_1});
		}

		/**
		 * Name binding for function: isNotNullExpr.
		 * @see #isNotNullExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isNotNullExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "isNotNullExpr");

		/**
		 * Helper binding method for function: isNullExpr. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of isNullExpr
		 */
		public static final SourceModel.Expr isNullExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isNullExpr), arg_1});
		}

		/**
		 * Name binding for function: isNullExpr.
		 * @see #isNullExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isNullExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "isNullExpr");

		/**
		 * Returns whether the field is numeric.
		 * @param field (CAL type: <code>Cal.Data.DictionaryQuery.DatabaseField</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr isNumericField(SourceModel.Expr field) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isNumericField), field});
		}

		/**
		 * Name binding for function: isNumericField.
		 * @see #isNumericField(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isNumericField = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"isNumericField");

		/**
		 * Returns whether the specified expression is a string constant value.
		 * @param expr (CAL type: <code>Cal.Data.DictionaryQuery.Expr</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr isStringConstantExpr(SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isStringConstantExpr), expr});
		}

		/**
		 * Name binding for function: isStringConstantExpr.
		 * @see #isStringConstantExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isStringConstantExpr = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"isStringConstantExpr");

		/**
		 * Returns whether a <code>Cal.Data.Sql.TopN</code> option is specified for the query.
		 * @param query (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr isTopNQuery(SourceModel.Expr query) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isTopNQuery), query});
		}

		/**
		 * Name binding for function: isTopNQuery.
		 * @see #isTopNQuery(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isTopNQuery = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "isTopNQuery");

		/**
		 * Returns whether this query is a UNION ALL query.
		 * @param query (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr isUnionAll(SourceModel.Expr query) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isUnionAll), query});
		}

		/**
		 * Name binding for function: isUnionAll.
		 * @see #isUnionAll(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isUnionAll = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "isUnionAll");

		/**
		 * Returns whether this query is a <code>Cal.Data.DictionaryQuery.Union</code> of 2 other queries.
		 * @param query (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr isUnionQuery(SourceModel.Expr query) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isUnionQuery), query});
		}

		/**
		 * Name binding for function: isUnionQuery.
		 * @see #isUnionQuery(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isUnionQuery = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "isUnionQuery");

		/**
		 * Joins 2 queries together on the specified expressions.
		 * Both queries will be wrapped as subqueries and joined together.
		 * The modified query will be returned along with expression for referencing the specified 
		 * expression from <code>query1</code> and <code>query2</code> in the joined query.
		 * The projected columns and ordering from the first query will be preserved in the joined query.
		 * @param joinType (CAL type: <code>Cal.Data.Sql.JoinType</code>)
		 * @param query1 (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @param query2 (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @param joinExprs (CAL type: <code>[(Cal.Data.DictionaryQuery.Expr, Cal.Data.DictionaryQuery.Expr)]</code>)
		 * @param query1Exprs (CAL type: <code>[Cal.Data.DictionaryQuery.Expr]</code>)
		 * @param query2Exprs (CAL type: <code>[Cal.Data.DictionaryQuery.Expr]</code>)
		 * @return (CAL type: <code>(Cal.Data.DictionaryQuery.Query, [Cal.Data.DictionaryQuery.Expr], [Cal.Data.DictionaryQuery.Expr])</code>) 
		 */
		public static final SourceModel.Expr joinQueries(SourceModel.Expr joinType, SourceModel.Expr query1, SourceModel.Expr query2, SourceModel.Expr joinExprs, SourceModel.Expr query1Exprs, SourceModel.Expr query2Exprs) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.joinQueries), joinType, query1, query2, joinExprs, query1Exprs, query2Exprs});
		}

		/**
		 * Name binding for function: joinQueries.
		 * @see #joinQueries(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName joinQueries = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "joinQueries");

		/**
		 * Helper binding method for function: lcaseExpr. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of lcaseExpr
		 */
		public static final SourceModel.Expr lcaseExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lcaseExpr), arg_1});
		}

		/**
		 * Name binding for function: lcaseExpr.
		 * @see #lcaseExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName lcaseExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "lcaseExpr");

		/**
		 * Helper binding method for function: leftExpr. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of leftExpr
		 */
		public static final SourceModel.Expr leftExpr(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.leftExpr), arg_1, arg_2});
		}

		/**
		 * Name binding for function: leftExpr.
		 * @see #leftExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName leftExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "leftExpr");

		/**
		 * Helper binding method for function: lengthExpr. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of lengthExpr
		 */
		public static final SourceModel.Expr lengthExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lengthExpr), arg_1});
		}

		/**
		 * Name binding for function: lengthExpr.
		 * @see #lengthExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName lengthExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "lengthExpr");

		/**
		 * Helper binding method for function: likeExpr. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of likeExpr
		 */
		public static final SourceModel.Expr likeExpr(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.likeExpr), arg_1, arg_2});
		}

		/**
		 * Name binding for function: likeExpr.
		 * @see #likeExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName likeExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "likeExpr");

		/**
		 * Helper binding method for function: locate2Expr. 
		 * @param searchExpr
		 * @param stringExpr
		 * @param start
		 * @return the SourceModule.expr representing an application of locate2Expr
		 */
		public static final SourceModel.Expr locate2Expr(SourceModel.Expr searchExpr, SourceModel.Expr stringExpr, SourceModel.Expr start) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.locate2Expr), searchExpr, stringExpr, start});
		}

		/**
		 * Name binding for function: locate2Expr.
		 * @see #locate2Expr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName locate2Expr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "locate2Expr");

		/**
		 * Helper binding method for function: locateExpr. 
		 * @param searchExpr
		 * @param stringExpr
		 * @return the SourceModule.expr representing an application of locateExpr
		 */
		public static final SourceModel.Expr locateExpr(SourceModel.Expr searchExpr, SourceModel.Expr stringExpr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.locateExpr), searchExpr, stringExpr});
		}

		/**
		 * Name binding for function: locateExpr.
		 * @see #locateExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName locateExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "locateExpr");

		/**
		 * Helper binding method for function: log10Expr. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of log10Expr
		 */
		public static final SourceModel.Expr log10Expr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.log10Expr), arg_1});
		}

		/**
		 * Name binding for function: log10Expr.
		 * @see #log10Expr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName log10Expr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "log10Expr");

		/**
		 * Helper binding method for function: logExpr. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of logExpr
		 */
		public static final SourceModel.Expr logExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.logExpr), arg_1});
		}

		/**
		 * Name binding for function: logExpr.
		 * @see #logExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName logExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "logExpr");

		/**
		 * Helper binding method for function: ltEqExpr. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of ltEqExpr
		 */
		public static final SourceModel.Expr ltEqExpr(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.ltEqExpr), arg_1, arg_2});
		}

		/**
		 * Name binding for function: ltEqExpr.
		 * @see #ltEqExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName ltEqExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "ltEqExpr");

		/**
		 * Helper binding method for function: ltExpr. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of ltExpr
		 */
		public static final SourceModel.Expr ltExpr(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.ltExpr), arg_1, arg_2});
		}

		/**
		 * Name binding for function: ltExpr.
		 * @see #ltExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName ltExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "ltExpr");

		/**
		 * Helper binding method for function: ltrimExpr. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of ltrimExpr
		 */
		public static final SourceModel.Expr ltrimExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.ltrimExpr), arg_1});
		}

		/**
		 * Name binding for function: ltrimExpr.
		 * @see #ltrimExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName ltrimExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "ltrimExpr");

		/**
		 * Construct a database field.
		 * @param arg_1 (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @param arg_2 (CAL type: <code>Cal.Utilities.UniqueIdentifier.UniqueIdentifier Cal.Data.DictionaryQuery.DatabaseField</code>)
		 * @param arg_3 (CAL type: <code>Cal.Utilities.ValueType.ValueType</code>)
		 * @param arg_4 (CAL type: <code>Cal.Data.DictionaryQuery.FieldType</code>)
		 * @param arg_5 (CAL type: <code>Cal.Data.DictionaryQuery.AggregationType</code>)
		 * @param arg_6 (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.DatabaseField</code>) 
		 */
		public static final SourceModel.Expr makeDatabaseField(SourceModel.Expr arg_1, SourceModel.Expr arg_2, SourceModel.Expr arg_3, SourceModel.Expr arg_4, SourceModel.Expr arg_5, SourceModel.Expr arg_6) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeDatabaseField), arg_1, arg_2, arg_3, arg_4, arg_5, arg_6});
		}

		/**
		 * @see #makeDatabaseField(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @param arg_3
		 * @param arg_4
		 * @param arg_5
		 * @param arg_6
		 * @return the SourceModel.Expr representing an application of makeDatabaseField
		 */
		public static final SourceModel.Expr makeDatabaseField(java.lang.String arg_1, SourceModel.Expr arg_2, SourceModel.Expr arg_3, SourceModel.Expr arg_4, SourceModel.Expr arg_5, boolean arg_6) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeDatabaseField), SourceModel.Expr.makeStringValue(arg_1), arg_2, arg_3, arg_4, arg_5, SourceModel.Expr.makeBooleanValue(arg_6)});
		}

		/**
		 * Name binding for function: makeDatabaseField.
		 * @see #makeDatabaseField(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeDatabaseField = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"makeDatabaseField");

		/**
		 * Constructs a GROUP BY query which groups all non-measure fields and aggregates all measures (if not preaggregated).
		 * The results will be ordered by the non-measure fields as well (in the order specified).
		 * @param exprs (CAL type: <code>[Cal.Data.DictionaryQuery.Expr]</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>) 
		 */
		public static final SourceModel.Expr makeGroupingQuery(SourceModel.Expr exprs) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeGroupingQuery), exprs});
		}

		/**
		 * Name binding for function: makeGroupingQuery.
		 * @see #makeGroupingQuery(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeGroupingQuery = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"makeGroupingQuery");

		/**
		 * Construct a new join set identfier.
		 * @param arg_1 (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @param arg_2 (CAL type: <code>Cal.Utilities.UniqueIdentifier.UniqueIdentifier Cal.Data.DictionaryQuery.JoinSet</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.JoinSet</code>) 
		 */
		public static final SourceModel.Expr makeJoinSet(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeJoinSet), arg_1, arg_2});
		}

		/**
		 * @see #makeJoinSet(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of makeJoinSet
		 */
		public static final SourceModel.Expr makeJoinSet(java.lang.String arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeJoinSet), SourceModel.Expr.makeStringValue(arg_1), arg_2});
		}

		/**
		 * Name binding for function: makeJoinSet.
		 * @see #makeJoinSet(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeJoinSet = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "makeJoinSet");

		/**
		 * This wraps the specified query as a subquery and returns expressions which can be accessed in an outer query.
		 * @param query (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @param queryExprs (CAL type: <code>[Cal.Data.DictionaryQuery.Expr]</code>)
		 * @return (CAL type: <code>[Cal.Data.DictionaryQuery.Expr]</code>) 
		 */
		public static final SourceModel.Expr makeSubqueryTable(SourceModel.Expr query, SourceModel.Expr queryExprs) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeSubqueryTable), query, queryExprs});
		}

		/**
		 * Name binding for function: makeSubqueryTable.
		 * @see #makeSubqueryTable(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeSubqueryTable = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"makeSubqueryTable");

		/**
		 * Helper binding method for function: maxAggregationType. 
		 * @return the SourceModule.expr representing an application of maxAggregationType
		 */
		public static final SourceModel.Expr maxAggregationType() {
			return SourceModel.Expr.Var.make(Functions.maxAggregationType);
		}

		/**
		 * Name binding for function: maxAggregationType.
		 * @see #maxAggregationType()
		 */
		public static final QualifiedName maxAggregationType = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"maxAggregationType");

		/**
		 * Helper binding method for function: maxExpr. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of maxExpr
		 */
		public static final SourceModel.Expr maxExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.maxExpr), arg_1});
		}

		/**
		 * Name binding for function: maxExpr.
		 * @see #maxExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName maxExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "maxExpr");

		/**
		 * Helper binding method for function: measureFieldType. 
		 * @return the SourceModule.expr representing an application of measureFieldType
		 */
		public static final SourceModel.Expr measureFieldType() {
			return SourceModel.Expr.Var.make(Functions.measureFieldType);
		}

		/**
		 * Name binding for function: measureFieldType.
		 * @see #measureFieldType()
		 */
		public static final QualifiedName measureFieldType = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"measureFieldType");

		/**
		 * Helper binding method for function: minAggregationType. 
		 * @return the SourceModule.expr representing an application of minAggregationType
		 */
		public static final SourceModel.Expr minAggregationType() {
			return SourceModel.Expr.Var.make(Functions.minAggregationType);
		}

		/**
		 * Name binding for function: minAggregationType.
		 * @see #minAggregationType()
		 */
		public static final QualifiedName minAggregationType = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"minAggregationType");

		/**
		 * Helper binding method for function: minExpr. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of minExpr
		 */
		public static final SourceModel.Expr minExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.minExpr), arg_1});
		}

		/**
		 * Name binding for function: minExpr.
		 * @see #minExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName minExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "minExpr");

		/**
		 * Helper binding method for function: minuteExpr. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of minuteExpr
		 */
		public static final SourceModel.Expr minuteExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.minuteExpr), arg_1});
		}

		/**
		 * Name binding for function: minuteExpr.
		 * @see #minuteExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName minuteExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "minuteExpr");

		/**
		 * Helper binding method for function: modExpr. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of modExpr
		 */
		public static final SourceModel.Expr modExpr(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.modExpr), arg_1, arg_2});
		}

		/**
		 * Name binding for function: modExpr.
		 * @see #modExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName modExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "modExpr");

		/**
		 * Helper binding method for function: modulusExpr. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of modulusExpr
		 */
		public static final SourceModel.Expr modulusExpr(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.modulusExpr), arg_1, arg_2});
		}

		/**
		 * Name binding for function: modulusExpr.
		 * @see #modulusExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName modulusExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "modulusExpr");

		/**
		 * Helper binding method for function: monthExpr. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of monthExpr
		 */
		public static final SourceModel.Expr monthExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.monthExpr), arg_1});
		}

		/**
		 * Name binding for function: monthExpr.
		 * @see #monthExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName monthExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "monthExpr");

		/**
		 * Helper binding method for function: monthNameExpr. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of monthNameExpr
		 */
		public static final SourceModel.Expr monthNameExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.monthNameExpr), arg_1});
		}

		/**
		 * Name binding for function: monthNameExpr.
		 * @see #monthNameExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName monthNameExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "monthNameExpr");

		/**
		 * Helper binding method for function: multiplyExpr. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of multiplyExpr
		 */
		public static final SourceModel.Expr multiplyExpr(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.multiplyExpr), arg_1, arg_2});
		}

		/**
		 * Name binding for function: multiplyExpr.
		 * @see #multiplyExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName multiplyExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "multiplyExpr");

		/**
		 * Helper binding method for function: negateExpr. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of negateExpr
		 */
		public static final SourceModel.Expr negateExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.negateExpr), arg_1});
		}

		/**
		 * Name binding for function: negateExpr.
		 * @see #negateExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName negateExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "negateExpr");

		/**
		 * Creates a new, empty query.
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>) 
		 */
		public static final SourceModel.Expr newQuery() {
			return SourceModel.Expr.Var.make(Functions.newQuery);
		}

		/**
		 * Name binding for function: newQuery.
		 * @see #newQuery()
		 */
		public static final QualifiedName newQuery = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "newQuery");

		/**
		 * Helper binding method for function: noAggregationType. 
		 * @return the SourceModule.expr representing an application of noAggregationType
		 */
		public static final SourceModel.Expr noAggregationType() {
			return SourceModel.Expr.Var.make(Functions.noAggregationType);
		}

		/**
		 * Name binding for function: noAggregationType.
		 * @see #noAggregationType()
		 */
		public static final QualifiedName noAggregationType = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"noAggregationType");

		/**
		 * Helper binding method for function: notEqExpr. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of notEqExpr
		 */
		public static final SourceModel.Expr notEqExpr(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.notEqExpr), arg_1, arg_2});
		}

		/**
		 * Name binding for function: notEqExpr.
		 * @see #notEqExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName notEqExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "notEqExpr");

		/**
		 * Helper binding method for function: notExpr. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of notExpr
		 */
		public static final SourceModel.Expr notExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.notExpr), arg_1});
		}

		/**
		 * Name binding for function: notExpr.
		 * @see #notExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName notExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "notExpr");

		/**
		 * Helper binding method for function: nowExpr. 
		 * @return the SourceModule.expr representing an application of nowExpr
		 */
		public static final SourceModel.Expr nowExpr() {
			return SourceModel.Expr.Var.make(Functions.nowExpr);
		}

		/**
		 * Name binding for function: nowExpr.
		 * @see #nowExpr()
		 */
		public static final QualifiedName nowExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "nowExpr");

		/**
		 * Helper binding method for function: nullIfExpr. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of nullIfExpr
		 */
		public static final SourceModel.Expr nullIfExpr(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.nullIfExpr), arg_1, arg_2});
		}

		/**
		 * Name binding for function: nullIfExpr.
		 * @see #nullIfExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName nullIfExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "nullIfExpr");

		/**
		 * Helper binding method for function: nullValue. 
		 * @return the SourceModule.expr representing an application of nullValue
		 */
		public static final SourceModel.Expr nullValue() {
			return SourceModel.Expr.Var.make(Functions.nullValue);
		}

		/**
		 * Name binding for function: nullValue.
		 * @see #nullValue()
		 */
		public static final QualifiedName nullValue = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "nullValue");

		/**
		 * Helper binding method for function: numericConstant. 
		 * @param numValue
		 * @return the SourceModule.expr representing an application of numericConstant
		 */
		public static final SourceModel.Expr numericConstant(SourceModel.Expr numValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.numericConstant), numValue});
		}

		/**
		 * Name binding for function: numericConstant.
		 * @see #numericConstant(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName numericConstant = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"numericConstant");

		/**
		 * Helper binding method for function: orExpr. 
		 * @param b1
		 * @param b2
		 * @return the SourceModule.expr representing an application of orExpr
		 */
		public static final SourceModel.Expr orExpr(SourceModel.Expr b1, SourceModel.Expr b2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.orExpr), b1, b2});
		}

		/**
		 * Name binding for function: orExpr.
		 * @see #orExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName orExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "orExpr");

		/**
		 * Adds sorting on the specified expression.
		 * @param query (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @param sortExpr (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Data.DictionaryQuery.TypedExpr a</code>)
		 * @param sortAscending (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>) 
		 */
		public static final SourceModel.Expr order(SourceModel.Expr query, SourceModel.Expr sortExpr, SourceModel.Expr sortAscending) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.order), query, sortExpr, sortAscending});
		}

		/**
		 * @see #order(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param query
		 * @param sortExpr
		 * @param sortAscending
		 * @return the SourceModel.Expr representing an application of order
		 */
		public static final SourceModel.Expr order(SourceModel.Expr query, SourceModel.Expr sortExpr, boolean sortAscending) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.order), query, sortExpr, SourceModel.Expr.makeBooleanValue(sortAscending)});
		}

		/**
		 * Name binding for function: order.
		 * @see #order(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName order = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "order");

		/**
		 * Adds sorting on the specified fields.
		 * @param query (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @param newOrderings (CAL type: <code>[(Cal.Data.DictionaryQuery.Expr, Cal.Core.Prelude.Boolean)]</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>) 
		 */
		public static final SourceModel.Expr order2(SourceModel.Expr query, SourceModel.Expr newOrderings) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.order2), query, newOrderings});
		}

		/**
		 * Name binding for function: order2.
		 * @see #order2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName order2 = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "order2");

		/**
		 * Returns the ordering info for the query.
		 * @param query (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @return (CAL type: <code>[(Cal.Data.DictionaryQuery.Expr, Cal.Core.Prelude.Boolean)]</code>) 
		 */
		public static final SourceModel.Expr orderingExpressions(SourceModel.Expr query) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.orderingExpressions), query});
		}

		/**
		 * Name binding for function: orderingExpressions.
		 * @see #orderingExpressions(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName orderingExpressions = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"orderingExpressions");

		/**
		 * Helper binding method for function: outputDatabaseField. 
		 * @param field
		 * @return the SourceModule.expr representing an application of outputDatabaseField
		 */
		public static final SourceModel.Expr outputDatabaseField(SourceModel.Expr field) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputDatabaseField), field});
		}

		/**
		 * Name binding for function: outputDatabaseField.
		 * @see #outputDatabaseField(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName outputDatabaseField = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"outputDatabaseField");

		/**
		 * Helper binding method for function: piExpr. 
		 * @return the SourceModule.expr representing an application of piExpr
		 */
		public static final SourceModel.Expr piExpr() {
			return SourceModel.Expr.Var.make(Functions.piExpr);
		}

		/**
		 * Name binding for function: piExpr.
		 * @see #piExpr()
		 */
		public static final QualifiedName piExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "piExpr");

		/**
		 * Helper binding method for function: powerExpr. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of powerExpr
		 */
		public static final SourceModel.Expr powerExpr(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.powerExpr), arg_1, arg_2});
		}

		/**
		 * Name binding for function: powerExpr.
		 * @see #powerExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName powerExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "powerExpr");

		/**
		 * Returns the ID of the preferred join set, if any.
		 * For a <code>Cal.Data.DictionaryQuery.Union</code> query, the join set of the first query will be returned.
		 * @param query (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe (Cal.Utilities.UniqueIdentifier.UniqueIdentifier Cal.Data.DictionaryQuery.JoinSet)</code>) 
		 */
		public static final SourceModel.Expr preferredJoinSetID(SourceModel.Expr query) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.preferredJoinSetID), query});
		}

		/**
		 * Name binding for function: preferredJoinSetID.
		 * @see #preferredJoinSetID(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName preferredJoinSetID = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"preferredJoinSetID");

		/**
		 * Adds the specified expressions as result columns in the query.
		 * @param query (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @param newColumns (CAL type: <code>[Cal.Data.DictionaryQuery.Expr]</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>) 
		 */
		public static final SourceModel.Expr project(SourceModel.Expr query, SourceModel.Expr newColumns) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.project), query, newColumns});
		}

		/**
		 * Name binding for function: project.
		 * @see #project(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName project = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "project");

		/**
		 * Adds the specified expression as a result column in the query.
		 * @param query (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @param newColumn (CAL type: <code>Cal.Data.DictionaryQuery.TypedExpr a</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>) 
		 */
		public static final SourceModel.Expr projectColumn(SourceModel.Expr query, SourceModel.Expr newColumn) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.projectColumn), query, newColumn});
		}

		/**
		 * Name binding for function: projectColumn.
		 * @see #projectColumn(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName projectColumn = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "projectColumn");

		/**
		 * Adds the specified expression as a result column in the query.
		 * @param query (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @param newColumn (CAL type: <code>Cal.Data.DictionaryQuery.TypedExpr a</code>)
		 * @param columnAlias (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>) 
		 */
		public static final SourceModel.Expr projectColumnWithAlias(SourceModel.Expr query, SourceModel.Expr newColumn, SourceModel.Expr columnAlias) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.projectColumnWithAlias), query, newColumn, columnAlias});
		}

		/**
		 * @see #projectColumnWithAlias(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param query
		 * @param newColumn
		 * @param columnAlias
		 * @return the SourceModel.Expr representing an application of projectColumnWithAlias
		 */
		public static final SourceModel.Expr projectColumnWithAlias(SourceModel.Expr query, SourceModel.Expr newColumn, java.lang.String columnAlias) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.projectColumnWithAlias), query, newColumn, SourceModel.Expr.makeStringValue(columnAlias)});
		}

		/**
		 * Name binding for function: projectColumnWithAlias.
		 * @see #projectColumnWithAlias(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName projectColumnWithAlias = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"projectColumnWithAlias");

		/**
		 * Projects the specified expressions from the query, and group and order (ASC) on the expressions.
		 * @param query (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @param exprs (CAL type: <code>[Cal.Data.DictionaryQuery.Expr]</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>) 
		 */
		public static final SourceModel.Expr projectGroupAndOrder(SourceModel.Expr query, SourceModel.Expr exprs) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.projectGroupAndOrder), query, exprs});
		}

		/**
		 * Name binding for function: projectGroupAndOrder.
		 * @see #projectGroupAndOrder(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName projectGroupAndOrder = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"projectGroupAndOrder");

		/**
		 * Adds the specified expressions as result columns with the corresponding aliases in the query.
		 * <p>
		 * TODO: don't add the same expression multiple times...
		 * 
		 * @param query (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @param newColumns (CAL type: <code>[(Cal.Data.DictionaryQuery.Expr, Cal.Core.Prelude.String)]</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>) 
		 */
		public static final SourceModel.Expr projectWithAliases(SourceModel.Expr query, SourceModel.Expr newColumns) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.projectWithAliases), query, newColumns});
		}

		/**
		 * Name binding for function: projectWithAliases.
		 * @see #projectWithAliases(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName projectWithAliases = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"projectWithAliases");

		/**
		 * Returns the aliases for the query's projected columns.
		 * @param query (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @return (CAL type: <code>[Cal.Core.Prelude.String]</code>) 
		 */
		public static final SourceModel.Expr projectedColumnAliases(SourceModel.Expr query) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.projectedColumnAliases), query});
		}

		/**
		 * Name binding for function: projectedColumnAliases.
		 * @see #projectedColumnAliases(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName projectedColumnAliases = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"projectedColumnAliases");

		/**
		 * Returns the projected columns for the query.
		 * For <code>Cal.Data.DictionaryQuery.Union</code> queries, this will return only the projected columns for the first query.
		 * @param query (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @return (CAL type: <code>[Cal.Data.DictionaryQuery.Expr]</code>) 
		 */
		public static final SourceModel.Expr projectedColumns(SourceModel.Expr query) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.projectedColumns), query});
		}

		/**
		 * Name binding for function: projectedColumns.
		 * @see #projectedColumns(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName projectedColumns = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"projectedColumns");

		/**
		 * Returns the projected columns for the query.
		 * For <code>Cal.Data.DictionaryQuery.Union</code> queries, this will return only the projected columns for the first query.
		 * @param query (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @return (CAL type: <code>[(Cal.Data.DictionaryQuery.Expr, Cal.Core.Prelude.String)]</code>) 
		 */
		public static final SourceModel.Expr projectedColumnsWithAliases(SourceModel.Expr query) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.projectedColumnsWithAliases), query});
		}

		/**
		 * Name binding for function: projectedColumnsWithAliases.
		 * @see #projectedColumnsWithAliases(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName projectedColumnsWithAliases = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"projectedColumnsWithAliases");

		/**
		 * Helper binding method for function: quarterExpr. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of quarterExpr
		 */
		public static final SourceModel.Expr quarterExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.quarterExpr), arg_1});
		}

		/**
		 * Name binding for function: quarterExpr.
		 * @see #quarterExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName quarterExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "quarterExpr");

		/**
		 * Returns a list of the fields used in a query.
		 * @param query (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @return (CAL type: <code>[Cal.Data.DictionaryQuery.DatabaseField]</code>) 
		 */
		public static final SourceModel.Expr queryFields(SourceModel.Expr query) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.queryFields), query});
		}

		/**
		 * Name binding for function: queryFields.
		 * @see #queryFields(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName queryFields = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "queryFields");

		/**
		 * Returns a list of the filters used in a query.
		 * For <code>Cal.Data.DictionaryQuery.Union</code> queries this returns the filters from the first query.
		 * @param query (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @return (CAL type: <code>[Cal.Data.DictionaryQuery.DatabaseField]</code>) 
		 */
		public static final SourceModel.Expr queryFilters(SourceModel.Expr query) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.queryFilters), query});
		}

		/**
		 * Name binding for function: queryFilters.
		 * @see #queryFilters(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName queryFilters = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "queryFilters");

		/**
		 * Returns the query options.
		 * For <code>Cal.Data.DictionaryQuery.Union</code> queries this will return the options for the first query.
		 * @param query (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @return (CAL type: <code>[Cal.Data.Sql.QueryOption]</code>) 
		 */
		public static final SourceModel.Expr queryOptions(SourceModel.Expr query) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.queryOptions), query});
		}

		/**
		 * Name binding for function: queryOptions.
		 * @see #queryOptions(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName queryOptions = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "queryOptions");

		/**
		 * Helper binding method for function: radiansExpr. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of radiansExpr
		 */
		public static final SourceModel.Expr radiansExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.radiansExpr), arg_1});
		}

		/**
		 * Name binding for function: radiansExpr.
		 * @see #radiansExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName radiansExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "radiansExpr");

		/**
		 * Helper binding method for function: randExpr. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of randExpr
		 */
		public static final SourceModel.Expr randExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.randExpr), arg_1});
		}

		/**
		 * Name binding for function: randExpr.
		 * @see #randExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName randExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "randExpr");

		/**
		 * Convert a range of values for a field into the equivalent restriction expression.
		 * @param makeConstantFn (CAL type: <code>Cal.Core.Prelude.Ord a => a -> Cal.Data.DictionaryQuery.TypedExpr a</code>)
		 *          a function to produce a database constant expression from a value
		 * @param field (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Data.DictionaryQuery.TypedExpr a</code>)
		 *          the database field to be restricted
		 * @param range (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Utilities.Range.Range a</code>)
		 *          the range of values to which the field will be restricted
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.TypedExpr Cal.Core.Prelude.Boolean</code>) 
		 *          a Boolean expression restricting the field to the specified range
		 */
		public static final SourceModel.Expr rangeToRestrictionExpr(SourceModel.Expr makeConstantFn, SourceModel.Expr field, SourceModel.Expr range) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.rangeToRestrictionExpr), makeConstantFn, field, range});
		}

		/**
		 * Name binding for function: rangeToRestrictionExpr.
		 * @see #rangeToRestrictionExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName rangeToRestrictionExpr = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"rangeToRestrictionExpr");

		/**
		 * Returns the record restriction expression (if any).
		 * This does not include any group restriction expressions.
		 * For <code>Cal.Data.DictionaryQuery.Union</code> queries, this only returns the restrictions for the first query.
		 * @param query (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe (Cal.Data.DictionaryQuery.TypedExpr Cal.Core.Prelude.Boolean)</code>) 
		 */
		public static final SourceModel.Expr recordRestrictionExpression(SourceModel.Expr query) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.recordRestrictionExpression), query});
		}

		/**
		 * Name binding for function: recordRestrictionExpression.
		 * @see #recordRestrictionExpression(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName recordRestrictionExpression = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"recordRestrictionExpression");

		/**
		 * Removes all group restrictions (not record restrictions) from the query.
		 * @param query (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>) 
		 */
		public static final SourceModel.Expr removeGroupRestrictions(SourceModel.Expr query) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.removeGroupRestrictions), query});
		}

		/**
		 * Name binding for function: removeGroupRestrictions.
		 * @see #removeGroupRestrictions(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName removeGroupRestrictions = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"removeGroupRestrictions");

		/**
		 * Removes all ordering from the query.
		 * @param query (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>) 
		 */
		public static final SourceModel.Expr removeOrdering(SourceModel.Expr query) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.removeOrdering), query});
		}

		/**
		 * Name binding for function: removeOrdering.
		 * @see #removeOrdering(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName removeOrdering = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"removeOrdering");

		/**
		 * Removes the specified expression from the projected columns list.
		 * For <code>Cal.Data.DictionaryQuery.Union</code> queries, the specified expression will be removed from the both queries along with the
		 * corresponding columns in the other query.
		 * @param query (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @param exprToRemove (CAL type: <code>Cal.Data.DictionaryQuery.Expr</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>) 
		 */
		public static final SourceModel.Expr removeProjectedColumn(SourceModel.Expr query, SourceModel.Expr exprToRemove) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.removeProjectedColumn), query, exprToRemove});
		}

		/**
		 * Name binding for function: removeProjectedColumn.
		 * @see #removeProjectedColumn(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName removeProjectedColumn = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"removeProjectedColumn");

		/**
		 * Removes all projected columns from the query.
		 * @param query (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>) 
		 */
		public static final SourceModel.Expr removeProjectedColumns(SourceModel.Expr query) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.removeProjectedColumns), query});
		}

		/**
		 * Name binding for function: removeProjectedColumns.
		 * @see #removeProjectedColumns(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName removeProjectedColumns = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"removeProjectedColumns");

		/**
		 * Removes all record restrictions (not group restrictions) from the query.
		 * @param query (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>) 
		 */
		public static final SourceModel.Expr removeRecordRestrictions(SourceModel.Expr query) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.removeRecordRestrictions), query});
		}

		/**
		 * Name binding for function: removeRecordRestrictions.
		 * @see #removeRecordRestrictions(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName removeRecordRestrictions = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"removeRecordRestrictions");

		/**
		 * Helper binding method for function: repeatExpr. 
		 * @param stringExpr
		 * @param count
		 * @return the SourceModule.expr representing an application of repeatExpr
		 */
		public static final SourceModel.Expr repeatExpr(SourceModel.Expr stringExpr, SourceModel.Expr count) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.repeatExpr), stringExpr, count});
		}

		/**
		 * Name binding for function: repeatExpr.
		 * @see #repeatExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName repeatExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "repeatExpr");

		/**
		 * Helper binding method for function: replaceExpr. 
		 * @param stringExpr
		 * @param searchStr
		 * @param replacementStr
		 * @return the SourceModule.expr representing an application of replaceExpr
		 */
		public static final SourceModel.Expr replaceExpr(SourceModel.Expr stringExpr, SourceModel.Expr searchStr, SourceModel.Expr replacementStr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.replaceExpr), stringExpr, searchStr, replacementStr});
		}

		/**
		 * Name binding for function: replaceExpr.
		 * @see #replaceExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName replaceExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "replaceExpr");

		/**
		 * Adds a restriction on the rows returned by the query.
		 * @param query (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @param newRestriction (CAL type: <code>Cal.Data.DictionaryQuery.TypedExpr Cal.Core.Prelude.Boolean</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>) 
		 */
		public static final SourceModel.Expr restrict(SourceModel.Expr query, SourceModel.Expr newRestriction) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.restrict), query, newRestriction});
		}

		/**
		 * Name binding for function: restrict.
		 * @see #restrict(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName restrict = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "restrict");

		/**
		 * Adds the specified restrictions on the rows returned by the query.
		 * @param query (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @param newRestrictions (CAL type: <code>[Cal.Data.DictionaryQuery.TypedExpr Cal.Core.Prelude.Boolean]</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>) 
		 */
		public static final SourceModel.Expr restrict2(SourceModel.Expr query, SourceModel.Expr newRestrictions) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.restrict2), query, newRestrictions});
		}

		/**
		 * Name binding for function: restrict2.
		 * @see #restrict2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName restrict2 = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "restrict2");

		/**
		 * Returns the restriction expressions (including group restrictions) for the query.
		 * For <code>Cal.Data.DictionaryQuery.Union</code> queries, this only returns the restrictions for the first query.
		 * @param query (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @return (CAL type: <code>[Cal.Data.DictionaryQuery.TypedExpr Cal.Core.Prelude.Boolean]</code>) 
		 */
		public static final SourceModel.Expr restrictionExpressions(SourceModel.Expr query) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.restrictionExpressions), query});
		}

		/**
		 * Name binding for function: restrictionExpressions.
		 * @see #restrictionExpressions(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName restrictionExpressions = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"restrictionExpressions");

		/**
		 * Helper binding method for function: rightExpr. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of rightExpr
		 */
		public static final SourceModel.Expr rightExpr(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.rightExpr), arg_1, arg_2});
		}

		/**
		 * Name binding for function: rightExpr.
		 * @see #rightExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName rightExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "rightExpr");

		/**
		 * Helper binding method for function: roundExpr. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of roundExpr
		 */
		public static final SourceModel.Expr roundExpr(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.roundExpr), arg_1, arg_2});
		}

		/**
		 * Name binding for function: roundExpr.
		 * @see #roundExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName roundExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "roundExpr");

		/**
		 * Helper binding method for function: rtrimExpr. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of rtrimExpr
		 */
		public static final SourceModel.Expr rtrimExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.rtrimExpr), arg_1});
		}

		/**
		 * Name binding for function: rtrimExpr.
		 * @see #rtrimExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName rtrimExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "rtrimExpr");

		/**
		 * Helper binding method for function: searchedCaseExpr. 
		 * @param whenConditionAndResults
		 * @param elseValue
		 * @return the SourceModule.expr representing an application of searchedCaseExpr
		 */
		public static final SourceModel.Expr searchedCaseExpr(SourceModel.Expr whenConditionAndResults, SourceModel.Expr elseValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.searchedCaseExpr), whenConditionAndResults, elseValue});
		}

		/**
		 * Name binding for function: searchedCaseExpr.
		 * @see #searchedCaseExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName searchedCaseExpr = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"searchedCaseExpr");

		/**
		 * Helper binding method for function: secondExpr. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of secondExpr
		 */
		public static final SourceModel.Expr secondExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.secondExpr), arg_1});
		}

		/**
		 * Name binding for function: secondExpr.
		 * @see #secondExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName secondExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "secondExpr");

		/**
		 * Sets the aliases for the first N projected columns.
		 * Any other existing aliases will be left untouched.
		 * @param query (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @param newAliases (CAL type: <code>[Cal.Core.Prelude.String]</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>) 
		 */
		public static final SourceModel.Expr setColumnAliases(SourceModel.Expr query, SourceModel.Expr newAliases) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.setColumnAliases), query, newAliases});
		}

		/**
		 * Name binding for function: setColumnAliases.
		 * @see #setColumnAliases(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName setColumnAliases = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"setColumnAliases");

		/**
		 * Sets whether a measure field includes aggregation.
		 * @param field (CAL type: <code>Cal.Data.DictionaryQuery.DatabaseField</code>)
		 * @param newIsPreaggregated (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.DatabaseField</code>) 
		 */
		public static final SourceModel.Expr setFieldPreaggregated(SourceModel.Expr field, SourceModel.Expr newIsPreaggregated) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.setFieldPreaggregated), field, newIsPreaggregated});
		}

		/**
		 * @see #setFieldPreaggregated(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param field
		 * @param newIsPreaggregated
		 * @return the SourceModel.Expr representing an application of setFieldPreaggregated
		 */
		public static final SourceModel.Expr setFieldPreaggregated(SourceModel.Expr field, boolean newIsPreaggregated) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.setFieldPreaggregated), field, SourceModel.Expr.makeBooleanValue(newIsPreaggregated)});
		}

		/**
		 * Name binding for function: setFieldPreaggregated.
		 * @see #setFieldPreaggregated(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName setFieldPreaggregated = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"setFieldPreaggregated");

		/**
		 * Sets the preferred join set.
		 * @param query (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @param newPreferredJoinSetID (CAL type: <code>Cal.Utilities.UniqueIdentifier.UniqueIdentifier Cal.Data.DictionaryQuery.JoinSet</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>) 
		 */
		public static final SourceModel.Expr setPreferredJoinSetID(SourceModel.Expr query, SourceModel.Expr newPreferredJoinSetID) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.setPreferredJoinSetID), query, newPreferredJoinSetID});
		}

		/**
		 * Name binding for function: setPreferredJoinSetID.
		 * @see #setPreferredJoinSetID(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName setPreferredJoinSetID = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"setPreferredJoinSetID");

		/**
		 * Sets the <code>Cal.Data.Sql.TopN</code> option on the query.
		 * <p>
		 * TODO: perhaps it should be possible to remove the TopN option by specifying zero for N...
		 * 
		 * @param query (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @param n (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @param percent (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 * @param withTies (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>) 
		 */
		public static final SourceModel.Expr setTopNOption(SourceModel.Expr query, SourceModel.Expr n, SourceModel.Expr percent, SourceModel.Expr withTies) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.setTopNOption), query, n, percent, withTies});
		}

		/**
		 * @see #setTopNOption(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param query
		 * @param n
		 * @param percent
		 * @param withTies
		 * @return the SourceModel.Expr representing an application of setTopNOption
		 */
		public static final SourceModel.Expr setTopNOption(SourceModel.Expr query, int n, boolean percent, boolean withTies) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.setTopNOption), query, SourceModel.Expr.makeIntValue(n), SourceModel.Expr.makeBooleanValue(percent), SourceModel.Expr.makeBooleanValue(withTies)});
		}

		/**
		 * Name binding for function: setTopNOption.
		 * @see #setTopNOption(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName setTopNOption = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "setTopNOption");

		/**
		 * Helper binding method for function: signExpr. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of signExpr
		 */
		public static final SourceModel.Expr signExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.signExpr), arg_1});
		}

		/**
		 * Name binding for function: signExpr.
		 * @see #signExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName signExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "signExpr");

		/**
		 * Helper binding method for function: simpleCaseExpr. 
		 * @param caseExpr
		 * @param whenValueAndResults
		 * @param elseValue
		 * @return the SourceModule.expr representing an application of simpleCaseExpr
		 */
		public static final SourceModel.Expr simpleCaseExpr(SourceModel.Expr caseExpr, SourceModel.Expr whenValueAndResults, SourceModel.Expr elseValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.simpleCaseExpr), caseExpr, whenValueAndResults, elseValue});
		}

		/**
		 * Name binding for function: simpleCaseExpr.
		 * @see #simpleCaseExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName simpleCaseExpr = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"simpleCaseExpr");

		/**
		 * Helper binding method for function: sinExpr. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of sinExpr
		 */
		public static final SourceModel.Expr sinExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.sinExpr), arg_1});
		}

		/**
		 * Name binding for function: sinExpr.
		 * @see #sinExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName sinExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "sinExpr");

		/**
		 * Helper binding method for function: soundexExpr. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of soundexExpr
		 */
		public static final SourceModel.Expr soundexExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.soundexExpr), arg_1});
		}

		/**
		 * Name binding for function: soundexExpr.
		 * @see #soundexExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName soundexExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "soundexExpr");

		/**
		 * Helper binding method for function: spaceExpr. 
		 * @param nSpaces
		 * @return the SourceModule.expr representing an application of spaceExpr
		 */
		public static final SourceModel.Expr spaceExpr(SourceModel.Expr nSpaces) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.spaceExpr), nSpaces});
		}

		/**
		 * Name binding for function: spaceExpr.
		 * @see #spaceExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName spaceExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "spaceExpr");

		/**
		 * Helper binding method for function: sqrtExpr. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of sqrtExpr
		 */
		public static final SourceModel.Expr sqrtExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.sqrtExpr), arg_1});
		}

		/**
		 * Name binding for function: sqrtExpr.
		 * @see #sqrtExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName sqrtExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "sqrtExpr");

		/**
		 * Helper binding method for function: stdDevExpr. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of stdDevExpr
		 */
		public static final SourceModel.Expr stdDevExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.stdDevExpr), arg_1});
		}

		/**
		 * Name binding for function: stdDevExpr.
		 * @see #stdDevExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName stdDevExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "stdDevExpr");

		/**
		 * Helper binding method for function: stdDevPExpr. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of stdDevPExpr
		 */
		public static final SourceModel.Expr stdDevPExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.stdDevPExpr), arg_1});
		}

		/**
		 * Name binding for function: stdDevPExpr.
		 * @see #stdDevPExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName stdDevPExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "stdDevPExpr");

		/**
		 * Helper binding method for function: stringConstant. 
		 * @param strValue
		 * @return the SourceModule.expr representing an application of stringConstant
		 */
		public static final SourceModel.Expr stringConstant(SourceModel.Expr strValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.stringConstant), strValue});
		}

		/**
		 * @see #stringConstant(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param strValue
		 * @return the SourceModel.Expr representing an application of stringConstant
		 */
		public static final SourceModel.Expr stringConstant(java.lang.String strValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.stringConstant), SourceModel.Expr.makeStringValue(strValue)});
		}

		/**
		 * Name binding for function: stringConstant.
		 * @see #stringConstant(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName stringConstant = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"stringConstant");

		/**
		 * Helper binding method for function: stringDatabaseField. 
		 * @param fieldType
		 * @param name
		 * @return the SourceModule.expr representing an application of stringDatabaseField
		 */
		public static final SourceModel.Expr stringDatabaseField(SourceModel.Expr fieldType, SourceModel.Expr name) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.stringDatabaseField), fieldType, name});
		}

		/**
		 * @see #stringDatabaseField(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param fieldType
		 * @param name
		 * @return the SourceModel.Expr representing an application of stringDatabaseField
		 */
		public static final SourceModel.Expr stringDatabaseField(SourceModel.Expr fieldType, java.lang.String name) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.stringDatabaseField), fieldType, SourceModel.Expr.makeStringValue(name)});
		}

		/**
		 * Name binding for function: stringDatabaseField.
		 * @see #stringDatabaseField(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName stringDatabaseField = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"stringDatabaseField");

		/**
		 * Helper binding method for function: stringDatabaseField2. 
		 * @param fieldType
		 * @param name
		 * @param uniqueID
		 * @param isPreaggregated
		 * @return the SourceModule.expr representing an application of stringDatabaseField2
		 */
		public static final SourceModel.Expr stringDatabaseField2(SourceModel.Expr fieldType, SourceModel.Expr name, SourceModel.Expr uniqueID, SourceModel.Expr isPreaggregated) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.stringDatabaseField2), fieldType, name, uniqueID, isPreaggregated});
		}

		/**
		 * @see #stringDatabaseField2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param fieldType
		 * @param name
		 * @param uniqueID
		 * @param isPreaggregated
		 * @return the SourceModel.Expr representing an application of stringDatabaseField2
		 */
		public static final SourceModel.Expr stringDatabaseField2(SourceModel.Expr fieldType, java.lang.String name, SourceModel.Expr uniqueID, boolean isPreaggregated) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.stringDatabaseField2), fieldType, SourceModel.Expr.makeStringValue(name), uniqueID, SourceModel.Expr.makeBooleanValue(isPreaggregated)});
		}

		/**
		 * Name binding for function: stringDatabaseField2.
		 * @see #stringDatabaseField2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName stringDatabaseField2 = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"stringDatabaseField2");

		/**
		 * TODO: perhaps these functions should check that the value type of the field is correct...
		 * @param field (CAL type: <code>Cal.Data.DictionaryQuery.DatabaseField</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.TypedExpr Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr stringField(SourceModel.Expr field) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.stringField), field});
		}

		/**
		 * Name binding for function: stringField.
		 * @see #stringField(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName stringField = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "stringField");

		/**
		 * Creates a field expression based on a subquery.
		 * Any ordering will be removed from the subquery (unless it uses <code>Cal.Data.Sql.TopN</code>).
		 * <p>
		 * TODO: is there a way to make this typed based on the (one and only) result column in the query?
		 * 
		 * @param subquery (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.Expr</code>) 
		 */
		public static final SourceModel.Expr subQueryExpr(SourceModel.Expr subquery) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.subQueryExpr), subquery});
		}

		/**
		 * Name binding for function: subQueryExpr.
		 * @see #subQueryExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName subQueryExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "subQueryExpr");

		/**
		 * Returns information about linked subqueries.
		 * @param query (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @return (CAL type: <code>[(Cal.Data.Sql.JoinType, [(Cal.Data.DictionaryQuery.Expr, Cal.Data.DictionaryQuery.Expr)])]</code>) 
		 */
		public static final SourceModel.Expr subqueryJoinInfoList(SourceModel.Expr query) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.subqueryJoinInfoList), query});
		}

		/**
		 * Name binding for function: subqueryJoinInfoList.
		 * @see #subqueryJoinInfoList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName subqueryJoinInfoList = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"subqueryJoinInfoList");

		/**
		 * Helper binding method for function: substringExpr. 
		 * @param stringExpr
		 * @param start
		 * @param length
		 * @return the SourceModule.expr representing an application of substringExpr
		 */
		public static final SourceModel.Expr substringExpr(SourceModel.Expr stringExpr, SourceModel.Expr start, SourceModel.Expr length) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.substringExpr), stringExpr, start, length});
		}

		/**
		 * Name binding for function: substringExpr.
		 * @see #substringExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName substringExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "substringExpr");

		/**
		 * Helper binding method for function: subtractExpr. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of subtractExpr
		 */
		public static final SourceModel.Expr subtractExpr(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.subtractExpr), arg_1, arg_2});
		}

		/**
		 * Name binding for function: subtractExpr.
		 * @see #subtractExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName subtractExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "subtractExpr");

		/**
		 * Helper binding method for function: sumAggregationType. 
		 * @return the SourceModule.expr representing an application of sumAggregationType
		 */
		public static final SourceModel.Expr sumAggregationType() {
			return SourceModel.Expr.Var.make(Functions.sumAggregationType);
		}

		/**
		 * Name binding for function: sumAggregationType.
		 * @see #sumAggregationType()
		 */
		public static final QualifiedName sumAggregationType = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"sumAggregationType");

		/**
		 * Helper binding method for function: sumExpr. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of sumExpr
		 */
		public static final SourceModel.Expr sumExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.sumExpr), arg_1});
		}

		/**
		 * Name binding for function: sumExpr.
		 * @see #sumExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName sumExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "sumExpr");

		/**
		 * Returns an expression which aggregates an expression from the specified query.
		 * The first projected expression of the query will be aggregated.
		 * @param query (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @param aggrOp (CAL type: <code>Cal.Data.Sql.DbFunction</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.Expr</code>) 
		 */
		public static final SourceModel.Expr summarizeQueryValues(SourceModel.Expr query, SourceModel.Expr aggrOp) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.summarizeQueryValues), query, aggrOp});
		}

		/**
		 * Name binding for function: summarizeQueryValues.
		 * @see #summarizeQueryValues(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName summarizeQueryValues = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"summarizeQueryValues");

		/**
		 * Helper binding method for function: tanExpr. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of tanExpr
		 */
		public static final SourceModel.Expr tanExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.tanExpr), arg_1});
		}

		/**
		 * Name binding for function: tanExpr.
		 * @see #tanExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName tanExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "tanExpr");

		/**
		 * Helper binding method for function: timeConstant. 
		 * @param timeValue
		 * @return the SourceModule.expr representing an application of timeConstant
		 */
		public static final SourceModel.Expr timeConstant(SourceModel.Expr timeValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.timeConstant), timeValue});
		}

		/**
		 * Name binding for function: timeConstant.
		 * @see #timeConstant(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName timeConstant = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "timeConstant");

		/**
		 * Helper binding method for function: timeDatabaseField. 
		 * @param fieldType
		 * @param name
		 * @return the SourceModule.expr representing an application of timeDatabaseField
		 */
		public static final SourceModel.Expr timeDatabaseField(SourceModel.Expr fieldType, SourceModel.Expr name) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.timeDatabaseField), fieldType, name});
		}

		/**
		 * @see #timeDatabaseField(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param fieldType
		 * @param name
		 * @return the SourceModel.Expr representing an application of timeDatabaseField
		 */
		public static final SourceModel.Expr timeDatabaseField(SourceModel.Expr fieldType, java.lang.String name) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.timeDatabaseField), fieldType, SourceModel.Expr.makeStringValue(name)});
		}

		/**
		 * Name binding for function: timeDatabaseField.
		 * @see #timeDatabaseField(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName timeDatabaseField = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"timeDatabaseField");

		/**
		 * Helper binding method for function: timeDatabaseField2. 
		 * @param fieldType
		 * @param name
		 * @param uniqueID
		 * @param isPreaggregated
		 * @return the SourceModule.expr representing an application of timeDatabaseField2
		 */
		public static final SourceModel.Expr timeDatabaseField2(SourceModel.Expr fieldType, SourceModel.Expr name, SourceModel.Expr uniqueID, SourceModel.Expr isPreaggregated) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.timeDatabaseField2), fieldType, name, uniqueID, isPreaggregated});
		}

		/**
		 * @see #timeDatabaseField2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param fieldType
		 * @param name
		 * @param uniqueID
		 * @param isPreaggregated
		 * @return the SourceModel.Expr representing an application of timeDatabaseField2
		 */
		public static final SourceModel.Expr timeDatabaseField2(SourceModel.Expr fieldType, java.lang.String name, SourceModel.Expr uniqueID, boolean isPreaggregated) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.timeDatabaseField2), fieldType, SourceModel.Expr.makeStringValue(name), uniqueID, SourceModel.Expr.makeBooleanValue(isPreaggregated)});
		}

		/**
		 * Name binding for function: timeDatabaseField2.
		 * @see #timeDatabaseField2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName timeDatabaseField2 = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"timeDatabaseField2");

		/**
		 * Helper binding method for function: timeField. 
		 * @param field
		 * @return the SourceModule.expr representing an application of timeField
		 */
		public static final SourceModel.Expr timeField(SourceModel.Expr field) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.timeField), field});
		}

		/**
		 * Name binding for function: timeField.
		 * @see #timeField(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName timeField = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "timeField");

		/**
		 * Returns a typed expression from the untyped expression.
		 * @param arg_1 (CAL type: <code>Cal.Data.DictionaryQuery.Expr</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.TypedExpr a</code>) 
		 */
		public static final SourceModel.Expr toTypedExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toTypedExpr), arg_1});
		}

		/**
		 * Name binding for function: toTypedExpr.
		 * @see #toTypedExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName toTypedExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "toTypedExpr");

		/**
		 * Returns the untyped expression from a typed expression.
		 * @param typedExpr (CAL type: <code>Cal.Data.DictionaryQuery.TypedExpr a</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.Expr</code>) 
		 */
		public static final SourceModel.Expr toUntypedExpr(SourceModel.Expr typedExpr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toUntypedExpr), typedExpr});
		}

		/**
		 * Name binding for function: toUntypedExpr.
		 * @see #toUntypedExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName toUntypedExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "toUntypedExpr");

		/**
		 * Helper binding method for function: trueConstant. 
		 * @return the SourceModule.expr representing an application of trueConstant
		 */
		public static final SourceModel.Expr trueConstant() {
			return SourceModel.Expr.Var.make(Functions.trueConstant);
		}

		/**
		 * Name binding for function: trueConstant.
		 * @see #trueConstant()
		 */
		public static final QualifiedName trueConstant = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "trueConstant");

		/**
		 * Helper binding method for function: truncateExpr. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of truncateExpr
		 */
		public static final SourceModel.Expr truncateExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.truncateExpr), arg_1});
		}

		/**
		 * Name binding for function: truncateExpr.
		 * @see #truncateExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName truncateExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "truncateExpr");

		/**
		 * Returns the type of the values represented by this typed expression.
		 * @param expr (CAL type: <code>Cal.Core.Prelude.Typeable a => Cal.Data.DictionaryQuery.TypedExpr a</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.TypeRep</code>) 
		 */
		public static final SourceModel.Expr typeOfExpr(SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.typeOfExpr), expr});
		}

		/**
		 * Name binding for function: typeOfExpr.
		 * @see #typeOfExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName typeOfExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "typeOfExpr");

		/**
		 * Helper binding method for function: ucaseExpr. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of ucaseExpr
		 */
		public static final SourceModel.Expr ucaseExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.ucaseExpr), arg_1});
		}

		/**
		 * Name binding for function: ucaseExpr.
		 * @see #ucaseExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName ucaseExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "ucaseExpr");

		/**
		 * Combines the 2 queries into a <code>Cal.Data.DictionaryQuery.Union</code> query.
		 * @param query1 (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @param query2 (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @param unionAll (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>) 
		 */
		public static final SourceModel.Expr unionQuery(SourceModel.Expr query1, SourceModel.Expr query2, SourceModel.Expr unionAll) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.unionQuery), query1, query2, unionAll});
		}

		/**
		 * @see #unionQuery(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param query1
		 * @param query2
		 * @param unionAll
		 * @return the SourceModel.Expr representing an application of unionQuery
		 */
		public static final SourceModel.Expr unionQuery(SourceModel.Expr query1, SourceModel.Expr query2, boolean unionAll) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.unionQuery), query1, query2, SourceModel.Expr.makeBooleanValue(unionAll)});
		}

		/**
		 * Name binding for function: unionQuery.
		 * @see #unionQuery(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName unionQuery = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "unionQuery");

		/**
		 * Helper binding method for function: untypedAggregationExpr. 
		 * @param aggrOp
		 * @param baseExpr
		 * @return the SourceModule.expr representing an application of untypedAggregationExpr
		 */
		public static final SourceModel.Expr untypedAggregationExpr(SourceModel.Expr aggrOp, SourceModel.Expr baseExpr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.untypedAggregationExpr), aggrOp, baseExpr});
		}

		/**
		 * Name binding for function: untypedAggregationExpr.
		 * @see #untypedAggregationExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName untypedAggregationExpr = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"untypedAggregationExpr");

		/**
		 * Helper binding method for function: untypedBinaryExpr. 
		 * @param func
		 * @param leftArgument
		 * @param rightArgument
		 * @return the SourceModule.expr representing an application of untypedBinaryExpr
		 */
		public static final SourceModel.Expr untypedBinaryExpr(SourceModel.Expr func, SourceModel.Expr leftArgument, SourceModel.Expr rightArgument) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.untypedBinaryExpr), func, leftArgument, rightArgument});
		}

		/**
		 * Name binding for function: untypedBinaryExpr.
		 * @see #untypedBinaryExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName untypedBinaryExpr = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"untypedBinaryExpr");

		/**
		 * Returns a query field for the specified table.
		 * @param field (CAL type: <code>Cal.Data.DictionaryQuery.DatabaseField</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.Expr</code>) 
		 */
		public static final SourceModel.Expr untypedField(SourceModel.Expr field) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.untypedField), field});
		}

		/**
		 * Name binding for function: untypedField.
		 * @see #untypedField(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName untypedField = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "untypedField");

		/**
		 * Helper binding method for function: untypedFunctionExpr. 
		 * @param func
		 * @param arguments
		 * @return the SourceModule.expr representing an application of untypedFunctionExpr
		 */
		public static final SourceModel.Expr untypedFunctionExpr(SourceModel.Expr func, SourceModel.Expr arguments) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.untypedFunctionExpr), func, arguments});
		}

		/**
		 * Name binding for function: untypedFunctionExpr.
		 * @see #untypedFunctionExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName untypedFunctionExpr = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"untypedFunctionExpr");

		/**
		 * Helper binding method for function: untypedListExpr. 
		 * @param listValues
		 * @return the SourceModule.expr representing an application of untypedListExpr
		 */
		public static final SourceModel.Expr untypedListExpr(SourceModel.Expr listValues) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.untypedListExpr), listValues});
		}

		/**
		 * Name binding for function: untypedListExpr.
		 * @see #untypedListExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName untypedListExpr = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"untypedListExpr");

		/**
		 * Helper binding method for function: untypedUnaryExpr. 
		 * @param func
		 * @param argument
		 * @return the SourceModule.expr representing an application of untypedUnaryExpr
		 */
		public static final SourceModel.Expr untypedUnaryExpr(SourceModel.Expr func, SourceModel.Expr argument) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.untypedUnaryExpr), func, argument});
		}

		/**
		 * Name binding for function: untypedUnaryExpr.
		 * @see #untypedUnaryExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName untypedUnaryExpr = 
			QualifiedName.make(
				CAL_DictionaryQuery.MODULE_NAME, 
				"untypedUnaryExpr");

		/**
		 * Helper binding method for function: userExpr. 
		 * @return the SourceModule.expr representing an application of userExpr
		 */
		public static final SourceModel.Expr userExpr() {
			return SourceModel.Expr.Var.make(Functions.userExpr);
		}

		/**
		 * Name binding for function: userExpr.
		 * @see #userExpr()
		 */
		public static final QualifiedName userExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "userExpr");

		/**
		 * Helper binding method for function: varianceExpr. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of varianceExpr
		 */
		public static final SourceModel.Expr varianceExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.varianceExpr), arg_1});
		}

		/**
		 * Name binding for function: varianceExpr.
		 * @see #varianceExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName varianceExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "varianceExpr");

		/**
		 * Helper binding method for function: variancePExpr. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of variancePExpr
		 */
		public static final SourceModel.Expr variancePExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.variancePExpr), arg_1});
		}

		/**
		 * Name binding for function: variancePExpr.
		 * @see #variancePExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName variancePExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "variancePExpr");

		/**
		 * Helper binding method for function: weekExpr. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of weekExpr
		 */
		public static final SourceModel.Expr weekExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.weekExpr), arg_1});
		}

		/**
		 * Name binding for function: weekExpr.
		 * @see #weekExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName weekExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "weekExpr");

		/**
		 * Wraps the query in an outer query which projects the same columns as the original.
		 * The ordering from the original query is also preserved.
		 * @param query (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>) 
		 */
		public static final SourceModel.Expr wrapQuery(SourceModel.Expr query) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.wrapQuery), query});
		}

		/**
		 * Name binding for function: wrapQuery.
		 * @see #wrapQuery(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName wrapQuery = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "wrapQuery");

		/**
		 * Wraps the query in an outer query which projects the same columns as the original.
		 * The ordering from the original query is also preserved.
		 * The wrapped query will be returned along with wrapped versions of the specified additional expressions.
		 * @param query (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @param additionalExprs (CAL type: <code>[Cal.Data.DictionaryQuery.Expr]</code>)
		 * @return (CAL type: <code>(Cal.Data.DictionaryQuery.Query, [Cal.Data.DictionaryQuery.Expr])</code>) 
		 */
		public static final SourceModel.Expr wrapQuery2(SourceModel.Expr query, SourceModel.Expr additionalExprs) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.wrapQuery2), query, additionalExprs});
		}

		/**
		 * Name binding for function: wrapQuery2.
		 * @see #wrapQuery2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName wrapQuery2 = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "wrapQuery2");

		/**
		 * Helper binding method for function: yearExpr. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of yearExpr
		 */
		public static final SourceModel.Expr yearExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.yearExpr), arg_1});
		}

		/**
		 * Name binding for function: yearExpr.
		 * @see #yearExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName yearExpr = 
			QualifiedName.make(CAL_DictionaryQuery.MODULE_NAME, "yearExpr");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = 1016928833;

}
