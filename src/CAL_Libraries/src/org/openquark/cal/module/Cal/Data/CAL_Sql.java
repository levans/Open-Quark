/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_Sql.java)
 * was generated from CAL module: Cal.Data.Sql.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Data.Sql module from Java code.
 *  
 * Creation date: Fri Jan 22 15:13:44 PST 2010
 * --!>
 *  
 */

package org.openquark.cal.module.Cal.Data;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * This module provides a combinator library for creating abstract SQL queries.
 * A query is represented by the type <code>Cal.Data.Sql.Query</code>.
 * <p>
 * A new, empty query can be created by calling <code>Cal.Data.Sql.newQuery</code>.
 * <p>
 * 
 * <pre> qry0 = newQuery;
 * </pre>
 * 
 * <p>
 * To project a column or a SQL expression, use the <code>Cal.Data.Sql.project</code> function. 
 * There are several variations on this function, such as <code>Cal.Data.Sql.projectColumn</code> and <code>Cal.Data.Sql.projectWithAliases</code>.
 * <p>
 * 
 * <pre> qry1 = project qry0 [toUntypedExpr countryField, toUntypedExpr orderDateField]; 
 * </pre>
 * 
 * <p>
 * To add a restriction to the query, use the <code>Cal.Data.Sql.restrict</code> function.
 * <p>
 * 
 * <pre> qry2 = restrict qry1 (eqExpr countryField (stringConstant "Canada"));
 * </pre>
 * 
 * <p>
 * To add sorting to the query, use the <code>Cal.Data.Sql.order</code> function.
 * <p>
 * 
 * <pre> qry3 = order qry2 orderDateField True;
 * </pre>
 * 
 * <p>
 * To add a join to the query, use the <code>Cal.Data.Sql.join</code> function. 
 * <p>
 * 
 * <pre> joinInfo = makeJoinInfo (intField custTable "Customer ID") (intField ordersTable "Customer ID") InnerJoin;
 *  qry4 = join qry3 joinInfo;
 * </pre>
 * 
 * <p>
 * An abstract <code>Cal.Data.Sql.Query</code> can be converted to a concrete SQL query with the <code>Cal.Data.Sql.queryText</code> function. 
 * This function requires a <code>Cal.Data.Sql.SqlBuilder</code> for a specific RDBMS as found in the module <code>Cal.Data.SqlBuilder</code>.
 * <p>
 * See the module <code>Cal.Test.Data.Sql_Tests</code> for examples of query construction.
 * <p>
 * There is some support for other types of SQL statements, such as ones to create/delete tables and inserting rows.
 * See the <code>Cal.Data.Sql.Statement</code> type.
 * 
 * @author Richard Webster
 */
public final class CAL_Sql {
	public static final ModuleName MODULE_NAME = ModuleName.make("Cal.Data.Sql");

	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Data.Sql module.
	 */
	public static final class TypeConstructors {
		/**
		 * Database expression functions and operators.
		 */
		public static final QualifiedName DbFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "DbFunction");

		/**
		 * A database expression.
		 */
		public static final QualifiedName Expr = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "Expr");

		/**
		 * Information about a join between 2 tables.
		 */
		public static final QualifiedName JoinInfo = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "JoinInfo");

		/**
		 * A binary tree structure for representing joins.
		 */
		public static final QualifiedName JoinNode = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "JoinNode");

		/**
		 * A named set of joins.
		 */
		public static final QualifiedName JoinSet = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "JoinSet");

		/**
		 * The types of joins that can be performed between two tables.
		 */
		public static final QualifiedName JoinType = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "JoinType");

		/**
		 * A database parameter that can be used in place of a value in a SQL statement.
		 * The parameter values can be bound in when executing the SQL statement.
		 * This is often used to execute the same statement multiple times with different values.
		 */
		public static final QualifiedName Parameter = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "Parameter");

		/**
		 * A database SELECT query.
		 */
		public static final QualifiedName Query = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "Query");

		/**
		 * Options for the query.
		 */
		public static final QualifiedName QueryOption = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "QueryOption");

		/**
		 * Associates a table resource with its alias for the query.
		 */
		public static final QualifiedName QueryTable = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "QueryTable");

		/**
		 * The SQL Builder holds the various functions needed to construct the SQL query
		 * text from a <code>Cal.Data.Sql.Query</code>.
		 * Different instances of this should be created for the different SQL
		 * 'flavours'.
		 */
		public static final QualifiedName SqlBuilder = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "SqlBuilder");

		/**
		 * This is a helper object which is passed through the SQL builder code to keep
		 * track of intermediate state.
		 */
		public static final QualifiedName SqlBuilderState = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "SqlBuilderState");

		/**
		 * SQL statements to modify tables and/or values.
		 */
		public static final QualifiedName Statement = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "Statement");

		/**
		 * Possible time interval values for working with date/time values.
		 */
		public static final QualifiedName TimeInterval = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "TimeInterval");

		/**
		 * <code>TypedExpr</code> wraps an untyped <code>Cal.Data.Sql.Expr</code> and adds information about the expression
		 * data type.
		 */
		public static final QualifiedName TypedExpr = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "TypedExpr");

	}
	/**
	 * This inner class (DataConstructors) contains constants
	 * and methods related to binding to CAL DataConstructors in the Cal.Data.Sql module.
	 */
	public static final class DataConstructors {
		/*
		 * DataConstructors for the Cal.Data.Sql.DbFunction data type.
		 */

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.OpNot.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.OpNot
		 */
		public static final SourceModel.Expr OpNot() {
			return SourceModel.Expr.DataCons.make(DataConstructors.OpNot);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.OpNot.
		 * @see #OpNot()
		 */
		public static final QualifiedName OpNot = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "OpNot");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.OpNot.
		 * @see #OpNot()
		 */
		public static final int OpNot_ordinal = 0;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.OpBitNot.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.OpBitNot
		 */
		public static final SourceModel.Expr OpBitNot() {
			return SourceModel.Expr.DataCons.make(DataConstructors.OpBitNot);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.OpBitNot.
		 * @see #OpBitNot()
		 */
		public static final QualifiedName OpBitNot = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "OpBitNot");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.OpBitNot.
		 * @see #OpBitNot()
		 */
		public static final int OpBitNot_ordinal = 1;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.OpNegate.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.OpNegate
		 */
		public static final SourceModel.Expr OpNegate() {
			return SourceModel.Expr.DataCons.make(DataConstructors.OpNegate);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.OpNegate.
		 * @see #OpNegate()
		 */
		public static final QualifiedName OpNegate = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "OpNegate");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.OpNegate.
		 * @see #OpNegate()
		 */
		public static final int OpNegate_ordinal = 2;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.OpIsNull.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.OpIsNull
		 */
		public static final SourceModel.Expr OpIsNull() {
			return SourceModel.Expr.DataCons.make(DataConstructors.OpIsNull);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.OpIsNull.
		 * @see #OpIsNull()
		 */
		public static final QualifiedName OpIsNull = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "OpIsNull");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.OpIsNull.
		 * @see #OpIsNull()
		 */
		public static final int OpIsNull_ordinal = 3;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.OpIsNotNull.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.OpIsNotNull
		 */
		public static final SourceModel.Expr OpIsNotNull() {
			return SourceModel.Expr.DataCons.make(DataConstructors.OpIsNotNull);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.OpIsNotNull.
		 * @see #OpIsNotNull()
		 */
		public static final QualifiedName OpIsNotNull = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "OpIsNotNull");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.OpIsNotNull.
		 * @see #OpIsNotNull()
		 */
		public static final int OpIsNotNull_ordinal = 4;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.OpExists.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.OpExists
		 */
		public static final SourceModel.Expr OpExists() {
			return SourceModel.Expr.DataCons.make(DataConstructors.OpExists);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.OpExists.
		 * @see #OpExists()
		 */
		public static final QualifiedName OpExists = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "OpExists");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.OpExists.
		 * @see #OpExists()
		 */
		public static final int OpExists_ordinal = 5;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.OpEq.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.OpEq
		 */
		public static final SourceModel.Expr OpEq() {
			return SourceModel.Expr.DataCons.make(DataConstructors.OpEq);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.OpEq.
		 * @see #OpEq()
		 */
		public static final QualifiedName OpEq = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "OpEq");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.OpEq.
		 * @see #OpEq()
		 */
		public static final int OpEq_ordinal = 6;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.OpLt.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.OpLt
		 */
		public static final SourceModel.Expr OpLt() {
			return SourceModel.Expr.DataCons.make(DataConstructors.OpLt);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.OpLt.
		 * @see #OpLt()
		 */
		public static final QualifiedName OpLt = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "OpLt");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.OpLt.
		 * @see #OpLt()
		 */
		public static final int OpLt_ordinal = 7;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.OpLtEq.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.OpLtEq
		 */
		public static final SourceModel.Expr OpLtEq() {
			return SourceModel.Expr.DataCons.make(DataConstructors.OpLtEq);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.OpLtEq.
		 * @see #OpLtEq()
		 */
		public static final QualifiedName OpLtEq = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "OpLtEq");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.OpLtEq.
		 * @see #OpLtEq()
		 */
		public static final int OpLtEq_ordinal = 8;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.OpGt.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.OpGt
		 */
		public static final SourceModel.Expr OpGt() {
			return SourceModel.Expr.DataCons.make(DataConstructors.OpGt);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.OpGt.
		 * @see #OpGt()
		 */
		public static final QualifiedName OpGt = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "OpGt");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.OpGt.
		 * @see #OpGt()
		 */
		public static final int OpGt_ordinal = 9;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.OpGtEq.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.OpGtEq
		 */
		public static final SourceModel.Expr OpGtEq() {
			return SourceModel.Expr.DataCons.make(DataConstructors.OpGtEq);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.OpGtEq.
		 * @see #OpGtEq()
		 */
		public static final QualifiedName OpGtEq = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "OpGtEq");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.OpGtEq.
		 * @see #OpGtEq()
		 */
		public static final int OpGtEq_ordinal = 10;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.OpNotEq.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.OpNotEq
		 */
		public static final SourceModel.Expr OpNotEq() {
			return SourceModel.Expr.DataCons.make(DataConstructors.OpNotEq);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.OpNotEq.
		 * @see #OpNotEq()
		 */
		public static final QualifiedName OpNotEq = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "OpNotEq");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.OpNotEq.
		 * @see #OpNotEq()
		 */
		public static final int OpNotEq_ordinal = 11;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.OpAnd.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.OpAnd
		 */
		public static final SourceModel.Expr OpAnd() {
			return SourceModel.Expr.DataCons.make(DataConstructors.OpAnd);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.OpAnd.
		 * @see #OpAnd()
		 */
		public static final QualifiedName OpAnd = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "OpAnd");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.OpAnd.
		 * @see #OpAnd()
		 */
		public static final int OpAnd_ordinal = 12;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.OpOr.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.OpOr
		 */
		public static final SourceModel.Expr OpOr() {
			return SourceModel.Expr.DataCons.make(DataConstructors.OpOr);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.OpOr.
		 * @see #OpOr()
		 */
		public static final QualifiedName OpOr = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "OpOr");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.OpOr.
		 * @see #OpOr()
		 */
		public static final int OpOr_ordinal = 13;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.OpLike.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.OpLike
		 */
		public static final SourceModel.Expr OpLike() {
			return SourceModel.Expr.DataCons.make(DataConstructors.OpLike);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.OpLike.
		 * @see #OpLike()
		 */
		public static final QualifiedName OpLike = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "OpLike");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.OpLike.
		 * @see #OpLike()
		 */
		public static final int OpLike_ordinal = 14;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.OpIn.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.OpIn
		 */
		public static final SourceModel.Expr OpIn() {
			return SourceModel.Expr.DataCons.make(DataConstructors.OpIn);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.OpIn.
		 * @see #OpIn()
		 */
		public static final QualifiedName OpIn = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "OpIn");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.OpIn.
		 * @see #OpIn()
		 */
		public static final int OpIn_ordinal = 15;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.OpCat.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.OpCat
		 */
		public static final SourceModel.Expr OpCat() {
			return SourceModel.Expr.DataCons.make(DataConstructors.OpCat);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.OpCat.
		 * @see #OpCat()
		 */
		public static final QualifiedName OpCat = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "OpCat");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.OpCat.
		 * @see #OpCat()
		 */
		public static final int OpCat_ordinal = 16;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.OpPlus.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.OpPlus
		 */
		public static final SourceModel.Expr OpPlus() {
			return SourceModel.Expr.DataCons.make(DataConstructors.OpPlus);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.OpPlus.
		 * @see #OpPlus()
		 */
		public static final QualifiedName OpPlus = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "OpPlus");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.OpPlus.
		 * @see #OpPlus()
		 */
		public static final int OpPlus_ordinal = 17;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.OpMinus.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.OpMinus
		 */
		public static final SourceModel.Expr OpMinus() {
			return SourceModel.Expr.DataCons.make(DataConstructors.OpMinus);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.OpMinus.
		 * @see #OpMinus()
		 */
		public static final QualifiedName OpMinus = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "OpMinus");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.OpMinus.
		 * @see #OpMinus()
		 */
		public static final int OpMinus_ordinal = 18;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.OpMul.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.OpMul
		 */
		public static final SourceModel.Expr OpMul() {
			return SourceModel.Expr.DataCons.make(DataConstructors.OpMul);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.OpMul.
		 * @see #OpMul()
		 */
		public static final QualifiedName OpMul = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "OpMul");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.OpMul.
		 * @see #OpMul()
		 */
		public static final int OpMul_ordinal = 19;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.OpDiv.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.OpDiv
		 */
		public static final SourceModel.Expr OpDiv() {
			return SourceModel.Expr.DataCons.make(DataConstructors.OpDiv);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.OpDiv.
		 * @see #OpDiv()
		 */
		public static final QualifiedName OpDiv = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "OpDiv");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.OpDiv.
		 * @see #OpDiv()
		 */
		public static final int OpDiv_ordinal = 20;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.OpMod.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.OpMod
		 */
		public static final SourceModel.Expr OpMod() {
			return SourceModel.Expr.DataCons.make(DataConstructors.OpMod);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.OpMod.
		 * @see #OpMod()
		 */
		public static final QualifiedName OpMod = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "OpMod");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.OpMod.
		 * @see #OpMod()
		 */
		public static final int OpMod_ordinal = 21;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.OpBitAnd.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.OpBitAnd
		 */
		public static final SourceModel.Expr OpBitAnd() {
			return SourceModel.Expr.DataCons.make(DataConstructors.OpBitAnd);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.OpBitAnd.
		 * @see #OpBitAnd()
		 */
		public static final QualifiedName OpBitAnd = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "OpBitAnd");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.OpBitAnd.
		 * @see #OpBitAnd()
		 */
		public static final int OpBitAnd_ordinal = 22;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.OpBitOr.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.OpBitOr
		 */
		public static final SourceModel.Expr OpBitOr() {
			return SourceModel.Expr.DataCons.make(DataConstructors.OpBitOr);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.OpBitOr.
		 * @see #OpBitOr()
		 */
		public static final QualifiedName OpBitOr = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "OpBitOr");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.OpBitOr.
		 * @see #OpBitOr()
		 */
		public static final int OpBitOr_ordinal = 23;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.OpBitXor.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.OpBitXor
		 */
		public static final SourceModel.Expr OpBitXor() {
			return SourceModel.Expr.DataCons.make(DataConstructors.OpBitXor);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.OpBitXor.
		 * @see #OpBitXor()
		 */
		public static final QualifiedName OpBitXor = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "OpBitXor");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.OpBitXor.
		 * @see #OpBitXor()
		 */
		public static final int OpBitXor_ordinal = 24;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.OpBetween.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.OpBetween
		 */
		public static final SourceModel.Expr OpBetween() {
			return SourceModel.Expr.DataCons.make(DataConstructors.OpBetween);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.OpBetween.
		 * @see #OpBetween()
		 */
		public static final QualifiedName OpBetween = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "OpBetween");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.OpBetween.
		 * @see #OpBetween()
		 */
		public static final int OpBetween_ordinal = 25;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.OpCase_Simple.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.OpCase_Simple
		 */
		public static final SourceModel.Expr OpCase_Simple() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.OpCase_Simple);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.OpCase_Simple.
		 * @see #OpCase_Simple()
		 */
		public static final QualifiedName OpCase_Simple = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "OpCase_Simple");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.OpCase_Simple.
		 * @see #OpCase_Simple()
		 */
		public static final int OpCase_Simple_ordinal = 26;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.OpCase_Searched.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.OpCase_Searched
		 */
		public static final SourceModel.Expr OpCase_Searched() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.OpCase_Searched);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.OpCase_Searched.
		 * @see #OpCase_Searched()
		 */
		public static final QualifiedName OpCase_Searched = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "OpCase_Searched");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.OpCase_Searched.
		 * @see #OpCase_Searched()
		 */
		public static final int OpCase_Searched_ordinal = 27;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.ConvertToStringFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.ConvertToStringFunction
		 */
		public static final SourceModel.Expr ConvertToStringFunction() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.ConvertToStringFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.ConvertToStringFunction.
		 * @see #ConvertToStringFunction()
		 */
		public static final QualifiedName ConvertToStringFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "ConvertToStringFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.ConvertToStringFunction.
		 * @see #ConvertToStringFunction()
		 */
		public static final int ConvertToStringFunction_ordinal = 28;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.ConvertToIntFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.ConvertToIntFunction
		 */
		public static final SourceModel.Expr ConvertToIntFunction() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.ConvertToIntFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.ConvertToIntFunction.
		 * @see #ConvertToIntFunction()
		 */
		public static final QualifiedName ConvertToIntFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "ConvertToIntFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.ConvertToIntFunction.
		 * @see #ConvertToIntFunction()
		 */
		public static final int ConvertToIntFunction_ordinal = 29;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.ConvertToDoubleFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.ConvertToDoubleFunction
		 */
		public static final SourceModel.Expr ConvertToDoubleFunction() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.ConvertToDoubleFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.ConvertToDoubleFunction.
		 * @see #ConvertToDoubleFunction()
		 */
		public static final QualifiedName ConvertToDoubleFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "ConvertToDoubleFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.ConvertToDoubleFunction.
		 * @see #ConvertToDoubleFunction()
		 */
		public static final int ConvertToDoubleFunction_ordinal = 30;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.ConvertToTimeFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.ConvertToTimeFunction
		 */
		public static final SourceModel.Expr ConvertToTimeFunction() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.ConvertToTimeFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.ConvertToTimeFunction.
		 * @see #ConvertToTimeFunction()
		 */
		public static final QualifiedName ConvertToTimeFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "ConvertToTimeFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.ConvertToTimeFunction.
		 * @see #ConvertToTimeFunction()
		 */
		public static final int ConvertToTimeFunction_ordinal = 31;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.AbsFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.AbsFunction
		 */
		public static final SourceModel.Expr AbsFunction() {
			return SourceModel.Expr.DataCons.make(DataConstructors.AbsFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.AbsFunction.
		 * @see #AbsFunction()
		 */
		public static final QualifiedName AbsFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "AbsFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.AbsFunction.
		 * @see #AbsFunction()
		 */
		public static final int AbsFunction_ordinal = 32;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.AcosFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.AcosFunction
		 */
		public static final SourceModel.Expr AcosFunction() {
			return SourceModel.Expr.DataCons.make(DataConstructors.AcosFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.AcosFunction.
		 * @see #AcosFunction()
		 */
		public static final QualifiedName AcosFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "AcosFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.AcosFunction.
		 * @see #AcosFunction()
		 */
		public static final int AcosFunction_ordinal = 33;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.AsinFucntion.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.AsinFucntion
		 */
		public static final SourceModel.Expr AsinFucntion() {
			return SourceModel.Expr.DataCons.make(DataConstructors.AsinFucntion);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.AsinFucntion.
		 * @see #AsinFucntion()
		 */
		public static final QualifiedName AsinFucntion = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "AsinFucntion");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.AsinFucntion.
		 * @see #AsinFucntion()
		 */
		public static final int AsinFucntion_ordinal = 34;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.AtanFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.AtanFunction
		 */
		public static final SourceModel.Expr AtanFunction() {
			return SourceModel.Expr.DataCons.make(DataConstructors.AtanFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.AtanFunction.
		 * @see #AtanFunction()
		 */
		public static final QualifiedName AtanFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "AtanFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.AtanFunction.
		 * @see #AtanFunction()
		 */
		public static final int AtanFunction_ordinal = 35;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.Atan2Function.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.Atan2Function
		 */
		public static final SourceModel.Expr Atan2Function() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.Atan2Function);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.Atan2Function.
		 * @see #Atan2Function()
		 */
		public static final QualifiedName Atan2Function = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "Atan2Function");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.Atan2Function.
		 * @see #Atan2Function()
		 */
		public static final int Atan2Function_ordinal = 36;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.CeilingFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.CeilingFunction
		 */
		public static final SourceModel.Expr CeilingFunction() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.CeilingFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.CeilingFunction.
		 * @see #CeilingFunction()
		 */
		public static final QualifiedName CeilingFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "CeilingFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.CeilingFunction.
		 * @see #CeilingFunction()
		 */
		public static final int CeilingFunction_ordinal = 37;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.CosFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.CosFunction
		 */
		public static final SourceModel.Expr CosFunction() {
			return SourceModel.Expr.DataCons.make(DataConstructors.CosFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.CosFunction.
		 * @see #CosFunction()
		 */
		public static final QualifiedName CosFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "CosFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.CosFunction.
		 * @see #CosFunction()
		 */
		public static final int CosFunction_ordinal = 38;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.CotFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.CotFunction
		 */
		public static final SourceModel.Expr CotFunction() {
			return SourceModel.Expr.DataCons.make(DataConstructors.CotFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.CotFunction.
		 * @see #CotFunction()
		 */
		public static final QualifiedName CotFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "CotFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.CotFunction.
		 * @see #CotFunction()
		 */
		public static final int CotFunction_ordinal = 39;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.DegreesFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.DegreesFunction
		 */
		public static final SourceModel.Expr DegreesFunction() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.DegreesFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.DegreesFunction.
		 * @see #DegreesFunction()
		 */
		public static final QualifiedName DegreesFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "DegreesFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.DegreesFunction.
		 * @see #DegreesFunction()
		 */
		public static final int DegreesFunction_ordinal = 40;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.ExpFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.ExpFunction
		 */
		public static final SourceModel.Expr ExpFunction() {
			return SourceModel.Expr.DataCons.make(DataConstructors.ExpFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.ExpFunction.
		 * @see #ExpFunction()
		 */
		public static final QualifiedName ExpFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "ExpFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.ExpFunction.
		 * @see #ExpFunction()
		 */
		public static final int ExpFunction_ordinal = 41;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.FloorFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.FloorFunction
		 */
		public static final SourceModel.Expr FloorFunction() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.FloorFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.FloorFunction.
		 * @see #FloorFunction()
		 */
		public static final QualifiedName FloorFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "FloorFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.FloorFunction.
		 * @see #FloorFunction()
		 */
		public static final int FloorFunction_ordinal = 42;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.LogFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.LogFunction
		 */
		public static final SourceModel.Expr LogFunction() {
			return SourceModel.Expr.DataCons.make(DataConstructors.LogFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.LogFunction.
		 * @see #LogFunction()
		 */
		public static final QualifiedName LogFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "LogFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.LogFunction.
		 * @see #LogFunction()
		 */
		public static final int LogFunction_ordinal = 43;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.Log10Function.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.Log10Function
		 */
		public static final SourceModel.Expr Log10Function() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.Log10Function);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.Log10Function.
		 * @see #Log10Function()
		 */
		public static final QualifiedName Log10Function = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "Log10Function");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.Log10Function.
		 * @see #Log10Function()
		 */
		public static final int Log10Function_ordinal = 44;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.ModFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.ModFunction
		 */
		public static final SourceModel.Expr ModFunction() {
			return SourceModel.Expr.DataCons.make(DataConstructors.ModFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.ModFunction.
		 * @see #ModFunction()
		 */
		public static final QualifiedName ModFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "ModFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.ModFunction.
		 * @see #ModFunction()
		 */
		public static final int ModFunction_ordinal = 45;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.PiFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.PiFunction
		 */
		public static final SourceModel.Expr PiFunction() {
			return SourceModel.Expr.DataCons.make(DataConstructors.PiFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.PiFunction.
		 * @see #PiFunction()
		 */
		public static final QualifiedName PiFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "PiFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.PiFunction.
		 * @see #PiFunction()
		 */
		public static final int PiFunction_ordinal = 46;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.PowerFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.PowerFunction
		 */
		public static final SourceModel.Expr PowerFunction() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.PowerFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.PowerFunction.
		 * @see #PowerFunction()
		 */
		public static final QualifiedName PowerFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "PowerFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.PowerFunction.
		 * @see #PowerFunction()
		 */
		public static final int PowerFunction_ordinal = 47;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.RadiansFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.RadiansFunction
		 */
		public static final SourceModel.Expr RadiansFunction() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.RadiansFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.RadiansFunction.
		 * @see #RadiansFunction()
		 */
		public static final QualifiedName RadiansFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "RadiansFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.RadiansFunction.
		 * @see #RadiansFunction()
		 */
		public static final int RadiansFunction_ordinal = 48;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.RandFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.RandFunction
		 */
		public static final SourceModel.Expr RandFunction() {
			return SourceModel.Expr.DataCons.make(DataConstructors.RandFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.RandFunction.
		 * @see #RandFunction()
		 */
		public static final QualifiedName RandFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "RandFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.RandFunction.
		 * @see #RandFunction()
		 */
		public static final int RandFunction_ordinal = 49;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.RoundFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.RoundFunction
		 */
		public static final SourceModel.Expr RoundFunction() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.RoundFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.RoundFunction.
		 * @see #RoundFunction()
		 */
		public static final QualifiedName RoundFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "RoundFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.RoundFunction.
		 * @see #RoundFunction()
		 */
		public static final int RoundFunction_ordinal = 50;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.SignFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.SignFunction
		 */
		public static final SourceModel.Expr SignFunction() {
			return SourceModel.Expr.DataCons.make(DataConstructors.SignFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.SignFunction.
		 * @see #SignFunction()
		 */
		public static final QualifiedName SignFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "SignFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.SignFunction.
		 * @see #SignFunction()
		 */
		public static final int SignFunction_ordinal = 51;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.SinFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.SinFunction
		 */
		public static final SourceModel.Expr SinFunction() {
			return SourceModel.Expr.DataCons.make(DataConstructors.SinFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.SinFunction.
		 * @see #SinFunction()
		 */
		public static final QualifiedName SinFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "SinFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.SinFunction.
		 * @see #SinFunction()
		 */
		public static final int SinFunction_ordinal = 52;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.SqrtFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.SqrtFunction
		 */
		public static final SourceModel.Expr SqrtFunction() {
			return SourceModel.Expr.DataCons.make(DataConstructors.SqrtFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.SqrtFunction.
		 * @see #SqrtFunction()
		 */
		public static final QualifiedName SqrtFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "SqrtFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.SqrtFunction.
		 * @see #SqrtFunction()
		 */
		public static final int SqrtFunction_ordinal = 53;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.TanFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.TanFunction
		 */
		public static final SourceModel.Expr TanFunction() {
			return SourceModel.Expr.DataCons.make(DataConstructors.TanFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.TanFunction.
		 * @see #TanFunction()
		 */
		public static final QualifiedName TanFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "TanFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.TanFunction.
		 * @see #TanFunction()
		 */
		public static final int TanFunction_ordinal = 54;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.TruncateFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.TruncateFunction
		 */
		public static final SourceModel.Expr TruncateFunction() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.TruncateFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.TruncateFunction.
		 * @see #TruncateFunction()
		 */
		public static final QualifiedName TruncateFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "TruncateFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.TruncateFunction.
		 * @see #TruncateFunction()
		 */
		public static final int TruncateFunction_ordinal = 55;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.AsciiFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.AsciiFunction
		 */
		public static final SourceModel.Expr AsciiFunction() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.AsciiFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.AsciiFunction.
		 * @see #AsciiFunction()
		 */
		public static final QualifiedName AsciiFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "AsciiFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.AsciiFunction.
		 * @see #AsciiFunction()
		 */
		public static final int AsciiFunction_ordinal = 56;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.CharFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.CharFunction
		 */
		public static final SourceModel.Expr CharFunction() {
			return SourceModel.Expr.DataCons.make(DataConstructors.CharFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.CharFunction.
		 * @see #CharFunction()
		 */
		public static final QualifiedName CharFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "CharFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.CharFunction.
		 * @see #CharFunction()
		 */
		public static final int CharFunction_ordinal = 57;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.DifferenceFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.DifferenceFunction
		 */
		public static final SourceModel.Expr DifferenceFunction() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.DifferenceFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.DifferenceFunction.
		 * @see #DifferenceFunction()
		 */
		public static final QualifiedName DifferenceFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "DifferenceFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.DifferenceFunction.
		 * @see #DifferenceFunction()
		 */
		public static final int DifferenceFunction_ordinal = 58;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.InsertFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.InsertFunction
		 */
		public static final SourceModel.Expr InsertFunction() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.InsertFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.InsertFunction.
		 * @see #InsertFunction()
		 */
		public static final QualifiedName InsertFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "InsertFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.InsertFunction.
		 * @see #InsertFunction()
		 */
		public static final int InsertFunction_ordinal = 59;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.LcaseFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.LcaseFunction
		 */
		public static final SourceModel.Expr LcaseFunction() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.LcaseFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.LcaseFunction.
		 * @see #LcaseFunction()
		 */
		public static final QualifiedName LcaseFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "LcaseFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.LcaseFunction.
		 * @see #LcaseFunction()
		 */
		public static final int LcaseFunction_ordinal = 60;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.LeftFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.LeftFunction
		 */
		public static final SourceModel.Expr LeftFunction() {
			return SourceModel.Expr.DataCons.make(DataConstructors.LeftFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.LeftFunction.
		 * @see #LeftFunction()
		 */
		public static final QualifiedName LeftFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "LeftFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.LeftFunction.
		 * @see #LeftFunction()
		 */
		public static final int LeftFunction_ordinal = 61;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.LengthFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.LengthFunction
		 */
		public static final SourceModel.Expr LengthFunction() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.LengthFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.LengthFunction.
		 * @see #LengthFunction()
		 */
		public static final QualifiedName LengthFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "LengthFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.LengthFunction.
		 * @see #LengthFunction()
		 */
		public static final int LengthFunction_ordinal = 62;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.LocateFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.LocateFunction
		 */
		public static final SourceModel.Expr LocateFunction() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.LocateFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.LocateFunction.
		 * @see #LocateFunction()
		 */
		public static final QualifiedName LocateFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "LocateFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.LocateFunction.
		 * @see #LocateFunction()
		 */
		public static final int LocateFunction_ordinal = 63;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.LtrimFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.LtrimFunction
		 */
		public static final SourceModel.Expr LtrimFunction() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.LtrimFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.LtrimFunction.
		 * @see #LtrimFunction()
		 */
		public static final QualifiedName LtrimFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "LtrimFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.LtrimFunction.
		 * @see #LtrimFunction()
		 */
		public static final int LtrimFunction_ordinal = 64;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.RepeatFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.RepeatFunction
		 */
		public static final SourceModel.Expr RepeatFunction() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.RepeatFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.RepeatFunction.
		 * @see #RepeatFunction()
		 */
		public static final QualifiedName RepeatFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "RepeatFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.RepeatFunction.
		 * @see #RepeatFunction()
		 */
		public static final int RepeatFunction_ordinal = 65;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.ReplaceFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.ReplaceFunction
		 */
		public static final SourceModel.Expr ReplaceFunction() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.ReplaceFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.ReplaceFunction.
		 * @see #ReplaceFunction()
		 */
		public static final QualifiedName ReplaceFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "ReplaceFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.ReplaceFunction.
		 * @see #ReplaceFunction()
		 */
		public static final int ReplaceFunction_ordinal = 66;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.RightFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.RightFunction
		 */
		public static final SourceModel.Expr RightFunction() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.RightFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.RightFunction.
		 * @see #RightFunction()
		 */
		public static final QualifiedName RightFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "RightFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.RightFunction.
		 * @see #RightFunction()
		 */
		public static final int RightFunction_ordinal = 67;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.RtrimFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.RtrimFunction
		 */
		public static final SourceModel.Expr RtrimFunction() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.RtrimFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.RtrimFunction.
		 * @see #RtrimFunction()
		 */
		public static final QualifiedName RtrimFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "RtrimFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.RtrimFunction.
		 * @see #RtrimFunction()
		 */
		public static final int RtrimFunction_ordinal = 68;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.SoundexFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.SoundexFunction
		 */
		public static final SourceModel.Expr SoundexFunction() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.SoundexFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.SoundexFunction.
		 * @see #SoundexFunction()
		 */
		public static final QualifiedName SoundexFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "SoundexFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.SoundexFunction.
		 * @see #SoundexFunction()
		 */
		public static final int SoundexFunction_ordinal = 69;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.SpaceFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.SpaceFunction
		 */
		public static final SourceModel.Expr SpaceFunction() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.SpaceFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.SpaceFunction.
		 * @see #SpaceFunction()
		 */
		public static final QualifiedName SpaceFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "SpaceFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.SpaceFunction.
		 * @see #SpaceFunction()
		 */
		public static final int SpaceFunction_ordinal = 70;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.SubstringFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.SubstringFunction
		 */
		public static final SourceModel.Expr SubstringFunction() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.SubstringFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.SubstringFunction.
		 * @see #SubstringFunction()
		 */
		public static final QualifiedName SubstringFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "SubstringFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.SubstringFunction.
		 * @see #SubstringFunction()
		 */
		public static final int SubstringFunction_ordinal = 71;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.UcaseFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.UcaseFunction
		 */
		public static final SourceModel.Expr UcaseFunction() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.UcaseFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.UcaseFunction.
		 * @see #UcaseFunction()
		 */
		public static final QualifiedName UcaseFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "UcaseFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.UcaseFunction.
		 * @see #UcaseFunction()
		 */
		public static final int UcaseFunction_ordinal = 72;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.DatabaseFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.DatabaseFunction
		 */
		public static final SourceModel.Expr DatabaseFunction() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.DatabaseFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.DatabaseFunction.
		 * @see #DatabaseFunction()
		 */
		public static final QualifiedName DatabaseFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "DatabaseFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.DatabaseFunction.
		 * @see #DatabaseFunction()
		 */
		public static final int DatabaseFunction_ordinal = 73;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.UserFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.UserFunction
		 */
		public static final SourceModel.Expr UserFunction() {
			return SourceModel.Expr.DataCons.make(DataConstructors.UserFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.UserFunction.
		 * @see #UserFunction()
		 */
		public static final QualifiedName UserFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "UserFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.UserFunction.
		 * @see #UserFunction()
		 */
		public static final int UserFunction_ordinal = 74;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.IfNullFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.IfNullFunction
		 */
		public static final SourceModel.Expr IfNullFunction() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.IfNullFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.IfNullFunction.
		 * @see #IfNullFunction()
		 */
		public static final QualifiedName IfNullFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "IfNullFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.IfNullFunction.
		 * @see #IfNullFunction()
		 */
		public static final int IfNullFunction_ordinal = 75;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.NullIfFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.NullIfFunction
		 */
		public static final SourceModel.Expr NullIfFunction() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.NullIfFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.NullIfFunction.
		 * @see #NullIfFunction()
		 */
		public static final QualifiedName NullIfFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "NullIfFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.NullIfFunction.
		 * @see #NullIfFunction()
		 */
		public static final int NullIfFunction_ordinal = 76;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.DayNameFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.DayNameFunction
		 */
		public static final SourceModel.Expr DayNameFunction() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.DayNameFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.DayNameFunction.
		 * @see #DayNameFunction()
		 */
		public static final QualifiedName DayNameFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "DayNameFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.DayNameFunction.
		 * @see #DayNameFunction()
		 */
		public static final int DayNameFunction_ordinal = 77;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.DayOfWeekFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.DayOfWeekFunction
		 */
		public static final SourceModel.Expr DayOfWeekFunction() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.DayOfWeekFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.DayOfWeekFunction.
		 * @see #DayOfWeekFunction()
		 */
		public static final QualifiedName DayOfWeekFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "DayOfWeekFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.DayOfWeekFunction.
		 * @see #DayOfWeekFunction()
		 */
		public static final int DayOfWeekFunction_ordinal = 78;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.DayOfMonthFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.DayOfMonthFunction
		 */
		public static final SourceModel.Expr DayOfMonthFunction() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.DayOfMonthFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.DayOfMonthFunction.
		 * @see #DayOfMonthFunction()
		 */
		public static final QualifiedName DayOfMonthFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "DayOfMonthFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.DayOfMonthFunction.
		 * @see #DayOfMonthFunction()
		 */
		public static final int DayOfMonthFunction_ordinal = 79;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.DayOfYearFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.DayOfYearFunction
		 */
		public static final SourceModel.Expr DayOfYearFunction() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.DayOfYearFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.DayOfYearFunction.
		 * @see #DayOfYearFunction()
		 */
		public static final QualifiedName DayOfYearFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "DayOfYearFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.DayOfYearFunction.
		 * @see #DayOfYearFunction()
		 */
		public static final int DayOfYearFunction_ordinal = 80;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.HourFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.HourFunction
		 */
		public static final SourceModel.Expr HourFunction() {
			return SourceModel.Expr.DataCons.make(DataConstructors.HourFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.HourFunction.
		 * @see #HourFunction()
		 */
		public static final QualifiedName HourFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "HourFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.HourFunction.
		 * @see #HourFunction()
		 */
		public static final int HourFunction_ordinal = 81;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.MinuteFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.MinuteFunction
		 */
		public static final SourceModel.Expr MinuteFunction() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.MinuteFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.MinuteFunction.
		 * @see #MinuteFunction()
		 */
		public static final QualifiedName MinuteFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "MinuteFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.MinuteFunction.
		 * @see #MinuteFunction()
		 */
		public static final int MinuteFunction_ordinal = 82;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.MonthFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.MonthFunction
		 */
		public static final SourceModel.Expr MonthFunction() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.MonthFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.MonthFunction.
		 * @see #MonthFunction()
		 */
		public static final QualifiedName MonthFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "MonthFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.MonthFunction.
		 * @see #MonthFunction()
		 */
		public static final int MonthFunction_ordinal = 83;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.MonthNameFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.MonthNameFunction
		 */
		public static final SourceModel.Expr MonthNameFunction() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.MonthNameFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.MonthNameFunction.
		 * @see #MonthNameFunction()
		 */
		public static final QualifiedName MonthNameFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "MonthNameFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.MonthNameFunction.
		 * @see #MonthNameFunction()
		 */
		public static final int MonthNameFunction_ordinal = 84;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.NowFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.NowFunction
		 */
		public static final SourceModel.Expr NowFunction() {
			return SourceModel.Expr.DataCons.make(DataConstructors.NowFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.NowFunction.
		 * @see #NowFunction()
		 */
		public static final QualifiedName NowFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "NowFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.NowFunction.
		 * @see #NowFunction()
		 */
		public static final int NowFunction_ordinal = 85;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.QuarterFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.QuarterFunction
		 */
		public static final SourceModel.Expr QuarterFunction() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.QuarterFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.QuarterFunction.
		 * @see #QuarterFunction()
		 */
		public static final QualifiedName QuarterFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "QuarterFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.QuarterFunction.
		 * @see #QuarterFunction()
		 */
		public static final int QuarterFunction_ordinal = 86;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.SecondFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.SecondFunction
		 */
		public static final SourceModel.Expr SecondFunction() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.SecondFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.SecondFunction.
		 * @see #SecondFunction()
		 */
		public static final QualifiedName SecondFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "SecondFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.SecondFunction.
		 * @see #SecondFunction()
		 */
		public static final int SecondFunction_ordinal = 87;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.WeekFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.WeekFunction
		 */
		public static final SourceModel.Expr WeekFunction() {
			return SourceModel.Expr.DataCons.make(DataConstructors.WeekFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.WeekFunction.
		 * @see #WeekFunction()
		 */
		public static final QualifiedName WeekFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "WeekFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.WeekFunction.
		 * @see #WeekFunction()
		 */
		public static final int WeekFunction_ordinal = 88;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.YearFunction.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.YearFunction
		 */
		public static final SourceModel.Expr YearFunction() {
			return SourceModel.Expr.DataCons.make(DataConstructors.YearFunction);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.YearFunction.
		 * @see #YearFunction()
		 */
		public static final QualifiedName YearFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "YearFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.YearFunction.
		 * @see #YearFunction()
		 */
		public static final int YearFunction_ordinal = 89;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.DateTimeAddFunction.
		 * @param timeInterval
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.DateTimeAddFunction
		 */
		public static final SourceModel.Expr DateTimeAddFunction(SourceModel.Expr timeInterval) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.DateTimeAddFunction), timeInterval});
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.DateTimeAddFunction.
		 * @see #DateTimeAddFunction(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName DateTimeAddFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "DateTimeAddFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.DateTimeAddFunction.
		 * @see #DateTimeAddFunction(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int DateTimeAddFunction_ordinal = 90;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.DateTimeDiffFunction.
		 * @param timeInterval
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.DateTimeDiffFunction
		 */
		public static final SourceModel.Expr DateTimeDiffFunction(SourceModel.Expr timeInterval) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.DateTimeDiffFunction), timeInterval});
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.DateTimeDiffFunction.
		 * @see #DateTimeDiffFunction(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName DateTimeDiffFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "DateTimeDiffFunction");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.DateTimeDiffFunction.
		 * @see #DateTimeDiffFunction(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int DateTimeDiffFunction_ordinal = 91;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.AggrCount.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.AggrCount
		 */
		public static final SourceModel.Expr AggrCount() {
			return SourceModel.Expr.DataCons.make(DataConstructors.AggrCount);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.AggrCount.
		 * @see #AggrCount()
		 */
		public static final QualifiedName AggrCount = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "AggrCount");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.AggrCount.
		 * @see #AggrCount()
		 */
		public static final int AggrCount_ordinal = 92;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.AggrSum.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.AggrSum
		 */
		public static final SourceModel.Expr AggrSum() {
			return SourceModel.Expr.DataCons.make(DataConstructors.AggrSum);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.AggrSum.
		 * @see #AggrSum()
		 */
		public static final QualifiedName AggrSum = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "AggrSum");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.AggrSum.
		 * @see #AggrSum()
		 */
		public static final int AggrSum_ordinal = 93;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.AggrAvg.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.AggrAvg
		 */
		public static final SourceModel.Expr AggrAvg() {
			return SourceModel.Expr.DataCons.make(DataConstructors.AggrAvg);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.AggrAvg.
		 * @see #AggrAvg()
		 */
		public static final QualifiedName AggrAvg = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "AggrAvg");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.AggrAvg.
		 * @see #AggrAvg()
		 */
		public static final int AggrAvg_ordinal = 94;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.AggrMin.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.AggrMin
		 */
		public static final SourceModel.Expr AggrMin() {
			return SourceModel.Expr.DataCons.make(DataConstructors.AggrMin);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.AggrMin.
		 * @see #AggrMin()
		 */
		public static final QualifiedName AggrMin = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "AggrMin");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.AggrMin.
		 * @see #AggrMin()
		 */
		public static final int AggrMin_ordinal = 95;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.AggrMax.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.AggrMax
		 */
		public static final SourceModel.Expr AggrMax() {
			return SourceModel.Expr.DataCons.make(DataConstructors.AggrMax);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.AggrMax.
		 * @see #AggrMax()
		 */
		public static final QualifiedName AggrMax = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "AggrMax");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.AggrMax.
		 * @see #AggrMax()
		 */
		public static final int AggrMax_ordinal = 96;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.AggrDistinctCount.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.AggrDistinctCount
		 */
		public static final SourceModel.Expr AggrDistinctCount() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.AggrDistinctCount);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.AggrDistinctCount.
		 * @see #AggrDistinctCount()
		 */
		public static final QualifiedName AggrDistinctCount = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "AggrDistinctCount");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.AggrDistinctCount.
		 * @see #AggrDistinctCount()
		 */
		public static final int AggrDistinctCount_ordinal = 97;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.AggrDistinctSum.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.AggrDistinctSum
		 */
		public static final SourceModel.Expr AggrDistinctSum() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.AggrDistinctSum);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.AggrDistinctSum.
		 * @see #AggrDistinctSum()
		 */
		public static final QualifiedName AggrDistinctSum = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "AggrDistinctSum");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.AggrDistinctSum.
		 * @see #AggrDistinctSum()
		 */
		public static final int AggrDistinctSum_ordinal = 98;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.AggrDistinctAvg.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.AggrDistinctAvg
		 */
		public static final SourceModel.Expr AggrDistinctAvg() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.AggrDistinctAvg);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.AggrDistinctAvg.
		 * @see #AggrDistinctAvg()
		 */
		public static final QualifiedName AggrDistinctAvg = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "AggrDistinctAvg");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.AggrDistinctAvg.
		 * @see #AggrDistinctAvg()
		 */
		public static final int AggrDistinctAvg_ordinal = 99;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.AggrCountAll.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.AggrCountAll
		 */
		public static final SourceModel.Expr AggrCountAll() {
			return SourceModel.Expr.DataCons.make(DataConstructors.AggrCountAll);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.AggrCountAll.
		 * @see #AggrCountAll()
		 */
		public static final QualifiedName AggrCountAll = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "AggrCountAll");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.AggrCountAll.
		 * @see #AggrCountAll()
		 */
		public static final int AggrCountAll_ordinal = 100;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.AggrStdDev.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.AggrStdDev
		 */
		public static final SourceModel.Expr AggrStdDev() {
			return SourceModel.Expr.DataCons.make(DataConstructors.AggrStdDev);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.AggrStdDev.
		 * @see #AggrStdDev()
		 */
		public static final QualifiedName AggrStdDev = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "AggrStdDev");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.AggrStdDev.
		 * @see #AggrStdDev()
		 */
		public static final int AggrStdDev_ordinal = 101;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.AggrStdDevP.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.AggrStdDevP
		 */
		public static final SourceModel.Expr AggrStdDevP() {
			return SourceModel.Expr.DataCons.make(DataConstructors.AggrStdDevP);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.AggrStdDevP.
		 * @see #AggrStdDevP()
		 */
		public static final QualifiedName AggrStdDevP = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "AggrStdDevP");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.AggrStdDevP.
		 * @see #AggrStdDevP()
		 */
		public static final int AggrStdDevP_ordinal = 102;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.AggrVar.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.AggrVar
		 */
		public static final SourceModel.Expr AggrVar() {
			return SourceModel.Expr.DataCons.make(DataConstructors.AggrVar);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.AggrVar.
		 * @see #AggrVar()
		 */
		public static final QualifiedName AggrVar = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "AggrVar");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.AggrVar.
		 * @see #AggrVar()
		 */
		public static final int AggrVar_ordinal = 103;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.AggrVarP.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.AggrVarP
		 */
		public static final SourceModel.Expr AggrVarP() {
			return SourceModel.Expr.DataCons.make(DataConstructors.AggrVarP);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.AggrVarP.
		 * @see #AggrVarP()
		 */
		public static final QualifiedName AggrVarP = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "AggrVarP");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.AggrVarP.
		 * @see #AggrVarP()
		 */
		public static final int AggrVarP_ordinal = 104;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.AggrOther.
		 * @param other
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.AggrOther
		 */
		public static final SourceModel.Expr AggrOther(SourceModel.Expr other) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.AggrOther), other});
		}

		/**
		 * @see #AggrOther(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param other
		 * @return org.openquark.cal.compiler.SourceModel.Expr
		 */
		public static final SourceModel.Expr AggrOther(java.lang.String other) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.AggrOther), SourceModel.Expr.makeStringValue(other)});
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.AggrOther.
		 * @see #AggrOther(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName AggrOther = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "AggrOther");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.AggrOther.
		 * @see #AggrOther(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int AggrOther_ordinal = 105;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.FunctionOther.
		 * @param funcName
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.FunctionOther
		 */
		public static final SourceModel.Expr FunctionOther(SourceModel.Expr funcName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.FunctionOther), funcName});
		}

		/**
		 * @see #FunctionOther(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param funcName
		 * @return org.openquark.cal.compiler.SourceModel.Expr
		 */
		public static final SourceModel.Expr FunctionOther(java.lang.String funcName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.FunctionOther), SourceModel.Expr.makeStringValue(funcName)});
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.FunctionOther.
		 * @see #FunctionOther(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName FunctionOther = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "FunctionOther");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.FunctionOther.
		 * @see #FunctionOther(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int FunctionOther_ordinal = 106;

		/*
		 * DataConstructors for the Cal.Data.Sql.JoinNode data type.
		 */

		/**
		 * A node representing a single query table.
		 * @param table (CAL type: <code>Cal.Data.Sql.QueryTable</code>)
		 *          a query table
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr JoinTable(SourceModel.Expr table) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.JoinTable), table});
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.JoinTable.
		 * @see #JoinTable(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName JoinTable = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "JoinTable");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.JoinTable.
		 * @see #JoinTable(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int JoinTable_ordinal = 0;

		/**
		 * A node specifying a join between tables in two join trees.
		 * @param leftNode (CAL type: <code>Cal.Data.Sql.JoinNode</code>)
		 *          one of the join trees to be joined
		 * @param rightNode (CAL type: <code>Cal.Data.Sql.JoinNode</code>)
		 *          the other join tree to be joined
		 * @param linkingExpr (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Boolean</code>)
		 *          a Boolean expression joining tables in the join trees
		 * @param joinType (CAL type: <code>Cal.Data.Sql.JoinType</code>)
		 *          the type of join to be performed
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr JoinSubtree(SourceModel.Expr leftNode, SourceModel.Expr rightNode, SourceModel.Expr linkingExpr, SourceModel.Expr joinType) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.JoinSubtree), leftNode, rightNode, linkingExpr, joinType});
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.JoinSubtree.
		 * @see #JoinSubtree(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName JoinSubtree = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "JoinSubtree");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.JoinSubtree.
		 * @see #JoinSubtree(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int JoinSubtree_ordinal = 1;

		/*
		 * DataConstructors for the Cal.Data.Sql.JoinType data type.
		 */

		/**
		 * The inner join option indicates that rows should be returned where 
		 * the join condition between the table is satisfied.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr InnerJoin() {
			return SourceModel.Expr.DataCons.make(DataConstructors.InnerJoin);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.InnerJoin.
		 * @see #InnerJoin()
		 */
		public static final QualifiedName InnerJoin = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "InnerJoin");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.InnerJoin.
		 * @see #InnerJoin()
		 */
		public static final int InnerJoin_ordinal = 0;

		/**
		 * The left outer join option indicates that all rows from the left table should 
		 * be included along with values from the right table where the join condition is satisfied.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr LeftOuterJoin() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.LeftOuterJoin);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.LeftOuterJoin.
		 * @see #LeftOuterJoin()
		 */
		public static final QualifiedName LeftOuterJoin = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "LeftOuterJoin");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.LeftOuterJoin.
		 * @see #LeftOuterJoin()
		 */
		public static final int LeftOuterJoin_ordinal = 1;

		/**
		 * The right outer join option indicates that all rows from the right table should 
		 * be included along with values from the left table where the join condition is satisfied.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr RightOuterJoin() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.RightOuterJoin);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.RightOuterJoin.
		 * @see #RightOuterJoin()
		 */
		public static final QualifiedName RightOuterJoin = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "RightOuterJoin");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.RightOuterJoin.
		 * @see #RightOuterJoin()
		 */
		public static final int RightOuterJoin_ordinal = 2;

		/**
		 * The full outer join option indicates that all rows satisfying the join condition should be returned
		 * as well as any unmatched rows from both the left and right tables.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr FullOuterJoin() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.FullOuterJoin);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.FullOuterJoin.
		 * @see #FullOuterJoin()
		 */
		public static final QualifiedName FullOuterJoin = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "FullOuterJoin");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.FullOuterJoin.
		 * @see #FullOuterJoin()
		 */
		public static final int FullOuterJoin_ordinal = 3;

		/*
		 * DataConstructors for the Cal.Data.Sql.QueryOption data type.
		 */

		/**
		 * This option indicates that only distinct rows should be returned for the query.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr Distinct() {
			return SourceModel.Expr.DataCons.make(DataConstructors.Distinct);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.Distinct.
		 * @see #Distinct()
		 */
		public static final QualifiedName Distinct = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "Distinct");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.Distinct.
		 * @see #Distinct()
		 */
		public static final int Distinct_ordinal = 0;

		/**
		 * This option indicates that the first N rows should be returned.
		 * @param n (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the number of rows to be returned
		 * @param percent (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 *          if True then the number with be interpretted as a percentage of the full set of rows,
		 * if False then the number will be interpretted as a number of rows
		 * @param withTies (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 *          if True then any rows that are considered equivalent order-wise to the Nth row will also be included in the results;
		 * if False then only the first N rows will be returned, even if there are ties
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr TopN(SourceModel.Expr n, SourceModel.Expr percent, SourceModel.Expr withTies) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.TopN), n, percent, withTies});
		}

		/**
		 * @see #TopN(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param n
		 * @param percent
		 * @param withTies
		 * @return org.openquark.cal.compiler.SourceModel.Expr
		 */
		public static final SourceModel.Expr TopN(int n, boolean percent, boolean withTies) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.TopN), SourceModel.Expr.makeIntValue(n), SourceModel.Expr.makeBooleanValue(percent), SourceModel.Expr.makeBooleanValue(withTies)});
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.TopN.
		 * @see #TopN(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName TopN = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "TopN");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.TopN.
		 * @see #TopN(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int TopN_ordinal = 1;

		/*
		 * DataConstructors for the Cal.Data.Sql.TimeInterval data type.
		 */

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.YearInterval.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.YearInterval
		 */
		public static final SourceModel.Expr YearInterval() {
			return SourceModel.Expr.DataCons.make(DataConstructors.YearInterval);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.YearInterval.
		 * @see #YearInterval()
		 */
		public static final QualifiedName YearInterval = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "YearInterval");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.YearInterval.
		 * @see #YearInterval()
		 */
		public static final int YearInterval_ordinal = 0;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.QuarterInterval.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.QuarterInterval
		 */
		public static final SourceModel.Expr QuarterInterval() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.QuarterInterval);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.QuarterInterval.
		 * @see #QuarterInterval()
		 */
		public static final QualifiedName QuarterInterval = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "QuarterInterval");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.QuarterInterval.
		 * @see #QuarterInterval()
		 */
		public static final int QuarterInterval_ordinal = 1;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.MonthInterval.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.MonthInterval
		 */
		public static final SourceModel.Expr MonthInterval() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.MonthInterval);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.MonthInterval.
		 * @see #MonthInterval()
		 */
		public static final QualifiedName MonthInterval = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "MonthInterval");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.MonthInterval.
		 * @see #MonthInterval()
		 */
		public static final int MonthInterval_ordinal = 2;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.DayInterval.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.DayInterval
		 */
		public static final SourceModel.Expr DayInterval() {
			return SourceModel.Expr.DataCons.make(DataConstructors.DayInterval);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.DayInterval.
		 * @see #DayInterval()
		 */
		public static final QualifiedName DayInterval = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "DayInterval");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.DayInterval.
		 * @see #DayInterval()
		 */
		public static final int DayInterval_ordinal = 3;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.HourInterval.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.HourInterval
		 */
		public static final SourceModel.Expr HourInterval() {
			return SourceModel.Expr.DataCons.make(DataConstructors.HourInterval);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.HourInterval.
		 * @see #HourInterval()
		 */
		public static final QualifiedName HourInterval = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "HourInterval");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.HourInterval.
		 * @see #HourInterval()
		 */
		public static final int HourInterval_ordinal = 4;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.MinuteInterval.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.MinuteInterval
		 */
		public static final SourceModel.Expr MinuteInterval() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.MinuteInterval);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.MinuteInterval.
		 * @see #MinuteInterval()
		 */
		public static final QualifiedName MinuteInterval = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "MinuteInterval");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.MinuteInterval.
		 * @see #MinuteInterval()
		 */
		public static final int MinuteInterval_ordinal = 5;

		/**
		 * Binding for DataConstructor: Cal.Data.Sql.SecondInterval.
		 * @return the SourceModule.Expr representing an application of Cal.Data.Sql.SecondInterval
		 */
		public static final SourceModel.Expr SecondInterval() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.SecondInterval);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.Sql.SecondInterval.
		 * @see #SecondInterval()
		 */
		public static final QualifiedName SecondInterval = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "SecondInterval");

		/**
		 * Ordinal of DataConstructor Cal.Data.Sql.SecondInterval.
		 * @see #SecondInterval()
		 */
		public static final int SecondInterval_ordinal = 6;

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Data.Sql module.
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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "absExpr");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "acosExpr");

		/**
		 * Constructs a database expression to add two numeric operand expressions.
		 * @param expr1 (CAL type: <code>Cal.Core.Prelude.Num a => Cal.Data.Sql.TypedExpr a</code>)
		 *          a numeric database expression for the first operand
		 * @param expr2 (CAL type: <code>Cal.Core.Prelude.Num a => Cal.Data.Sql.TypedExpr a</code>)
		 *          a numeric database expression for the second operand
		 * @return (CAL type: <code>Cal.Core.Prelude.Num a => Cal.Data.Sql.TypedExpr a</code>) 
		 *          a numeric database expression to add the two numeric operand expressions
		 */
		public static final SourceModel.Expr addExpr(SourceModel.Expr expr1, SourceModel.Expr expr2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.addExpr), expr1, expr2});
		}

		/**
		 * Name binding for function: addExpr.
		 * @see #addExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName addExpr = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "addExpr");

		/**
		 * Adds the specified join nodes to the query.
		 * @param query (CAL type: <code>Cal.Data.Sql.Query</code>)
		 * @param newJoins (CAL type: <code>[Cal.Data.Sql.JoinNode]</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.Query</code>) 
		 */
		public static final SourceModel.Expr addJoins(SourceModel.Expr query, SourceModel.Expr newJoins) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.addJoins), query, newJoins});
		}

		/**
		 * Name binding for function: addJoins.
		 * @see #addJoins(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName addJoins = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "addJoins");

		/**
		 * Adds an option to the query.
		 * @param query (CAL type: <code>Cal.Data.Sql.Query</code>)
		 * @param newOption (CAL type: <code>Cal.Data.Sql.QueryOption</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.Query</code>) 
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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "addOption");

		/**
		 * Adds parentheses around some expression text.
		 * @param sqlBuilder (CAL type: <code>Cal.Data.Sql.SqlBuilder</code>)
		 * @param exprText (CAL type: <code>Cal.Utilities.PrettyPrinter.Document</code>)
		 * @return (CAL type: <code>Cal.Utilities.PrettyPrinter.Document</code>) 
		 */
		public static final SourceModel.Expr addParens(SourceModel.Expr sqlBuilder, SourceModel.Expr exprText) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.addParens), sqlBuilder, exprText});
		}

		/**
		 * Name binding for function: addParens.
		 * @see #addParens(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName addParens = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "addParens");

		/**
		 * Forces the specified table to be included in the query, even if no fields
		 * from it are used in the rest of the query.
		 * @param query (CAL type: <code>Cal.Data.Sql.Query</code>)
		 * @param newUnjoinedTable (CAL type: <code>Cal.Data.Sql.QueryTable</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.Query</code>) 
		 */
		public static final SourceModel.Expr addUnjoinedTable(SourceModel.Expr query, SourceModel.Expr newUnjoinedTable) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.addUnjoinedTable), query, newUnjoinedTable});
		}

		/**
		 * Name binding for function: addUnjoinedTable.
		 * @see #addUnjoinedTable(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName addUnjoinedTable = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "addUnjoinedTable");

		/**
		 * This is an alternate version of <code>Cal.Data.Sql.intersectionQuery</code>.
		 * Instead of creating 2 subqueries and linking them, this function creates a
		 * subquery for the second query
		 * and links it directly to the first query (although special handling is needed
		 * if the first query is uses <code>Cal.Data.Sql.TopN</code>).
		 * This should give the same results as <code>intersectionQuery</code>.
		 * <p>
		 * TODO: check which option is most efficient and get rid of the other one.
		 * 
		 * @param query1 (CAL type: <code>Cal.Data.Sql.Query</code>)
		 * @param query2 (CAL type: <code>Cal.Data.Sql.Query</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.Query</code>) 
		 */
		public static final SourceModel.Expr alternateIntersectionQuery(SourceModel.Expr query1, SourceModel.Expr query2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.alternateIntersectionQuery), query1, query2});
		}

		/**
		 * Name binding for function: alternateIntersectionQuery.
		 * @see #alternateIntersectionQuery(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName alternateIntersectionQuery = 
			QualifiedName.make(
				CAL_Sql.MODULE_NAME, 
				"alternateIntersectionQuery");

		/**
		 * Constructs a database expression to test whether both of the operand expressions are True.
		 * @param b1 (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Boolean</code>)
		 *          a database expression for the first operand
		 * @param b2 (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Boolean</code>)
		 *          a database expression for the second operand
		 * @return (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Boolean</code>) 
		 *          a Boolean database expression to test whether both of the operand expressions are True
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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "andExpr");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "asciiExpr");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "asinExpr");

		/**
		 * The <code>assignMissingColumnAliases</code> is a transformation function to assign the missing aliases for all columns 
		 * of the passed in query
		 * <p>
		 * Use it in conjunction with <code>modifyQueries</code> function which can help to perfrom the transaformation 
		 * for all subqueries
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Data.Sql.modifyQueries
		 * </dl>
		 * 
		 * @param query (CAL type: <code>Cal.Data.Sql.Query</code>)
		 *          to transform
		 * @return (CAL type: <code>Cal.Data.Sql.Query</code>) 
		 *          transformed query
		 */
		public static final SourceModel.Expr assignMissingColumnAliases(SourceModel.Expr query) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.assignMissingColumnAliases), query});
		}

		/**
		 * Name binding for function: assignMissingColumnAliases.
		 * @see #assignMissingColumnAliases(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName assignMissingColumnAliases = 
			QualifiedName.make(
				CAL_Sql.MODULE_NAME, 
				"assignMissingColumnAliases");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "atan2Expr");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "atanExpr");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "avgExpr");

		/**
		 * Constructs a database expression which test whether the value of one operand expression is between 
		 * the value of two other operand expressions.
		 * @param leftExpr (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Data.Sql.TypedExpr a</code>)
		 *          a database expression to be tested
		 * @param lowerExpr (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Data.Sql.TypedExpr a</code>)
		 *          a database expression for the lower bound of the value range
		 * @param upperExpr (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Data.Sql.TypedExpr a</code>)
		 *          a database expression for the upper bound of the value range
		 * @return (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Boolean</code>) 
		 *          a Boolean database expression to test whether the value of one operand expression is between 
		 * the value of two other operand expressions
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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "betweenExpr");

		/**
		 * Helper binding method for function: binaryField. 
		 * @param table
		 * @param fieldName
		 * @return the SourceModule.expr representing an application of binaryField
		 */
		public static final SourceModel.Expr binaryField(SourceModel.Expr table, SourceModel.Expr fieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.binaryField), table, fieldName});
		}

		/**
		 * @see #binaryField(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param table
		 * @param fieldName
		 * @return the SourceModel.Expr representing an application of binaryField
		 */
		public static final SourceModel.Expr binaryField(SourceModel.Expr table, java.lang.String fieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.binaryField), table, SourceModel.Expr.makeStringValue(fieldName)});
		}

		/**
		 * Name binding for function: binaryField.
		 * @see #binaryField(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName binaryField = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "binaryField");

		/**
		 * Constructs a database expression to perform a bitwise 'and' of two numeric operand expressions.
		 * @param expr1 (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Int</code>)
		 *          an integer database expression for the first operand
		 * @param expr2 (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Int</code>)
		 *          an integer database expression for the second operand
		 * @return (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Int</code>) 
		 *          an integer database expression to perform a bitwise 'and' of two numeric operand expressions
		 */
		public static final SourceModel.Expr bitwiseAndExpr(SourceModel.Expr expr1, SourceModel.Expr expr2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.bitwiseAndExpr), expr1, expr2});
		}

		/**
		 * Name binding for function: bitwiseAndExpr.
		 * @see #bitwiseAndExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName bitwiseAndExpr = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "bitwiseAndExpr");

		/**
		 * Constructs a database expression for the application of the bitwise 'not' operator to an integer argument.
		 * @param intExpr (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Int</code>)
		 *          an integer database expression
		 * @return (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Int</code>) 
		 *          an integer database expression for the application of the bitwise 'not' operator to the argument expression
		 */
		public static final SourceModel.Expr bitwiseNotExpr(SourceModel.Expr intExpr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.bitwiseNotExpr), intExpr});
		}

		/**
		 * Name binding for function: bitwiseNotExpr.
		 * @see #bitwiseNotExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName bitwiseNotExpr = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "bitwiseNotExpr");

		/**
		 * Constructs a database expression to perform a bitwise 'or' of two numeric operand expressions.
		 * @param expr1 (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Int</code>)
		 *          an integer database expression for the first operand
		 * @param expr2 (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Int</code>)
		 *          an integer database expression for the second operand
		 * @return (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Int</code>) 
		 *          an integer database expression to perform a bitwise 'or' of two numeric operand expressions
		 */
		public static final SourceModel.Expr bitwiseOrExpr(SourceModel.Expr expr1, SourceModel.Expr expr2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.bitwiseOrExpr), expr1, expr2});
		}

		/**
		 * Name binding for function: bitwiseOrExpr.
		 * @see #bitwiseOrExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName bitwiseOrExpr = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "bitwiseOrExpr");

		/**
		 * Constructs a database expression to perform a bitwise 'xor' of two numeric operand expressions.
		 * @param expr1 (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Int</code>)
		 *          an integer database expression for the first operand
		 * @param expr2 (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Int</code>)
		 *          an integer database expression for the second operand
		 * @return (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Int</code>) 
		 *          an integer database expression to perform a bitwise 'xor' of two numeric operand expressions
		 */
		public static final SourceModel.Expr bitwiseXorExpr(SourceModel.Expr expr1, SourceModel.Expr expr2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.bitwiseXorExpr), expr1, expr2});
		}

		/**
		 * Name binding for function: bitwiseXorExpr.
		 * @see #bitwiseXorExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName bitwiseXorExpr = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "bitwiseXorExpr");

		/**
		 * Construct a database expression for a Boolean value.
		 * @param boolValue (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 *          the Boolean value
		 * @return (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Boolean</code>) 
		 *          the database expression for the Boolean value
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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "booleanConstant");

		/**
		 * Helper binding method for function: booleanField. 
		 * @param table
		 * @param fieldName
		 * @return the SourceModule.expr representing an application of booleanField
		 */
		public static final SourceModel.Expr booleanField(SourceModel.Expr table, SourceModel.Expr fieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanField), table, fieldName});
		}

		/**
		 * @see #booleanField(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param table
		 * @param fieldName
		 * @return the SourceModel.Expr representing an application of booleanField
		 */
		public static final SourceModel.Expr booleanField(SourceModel.Expr table, java.lang.String fieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanField), table, SourceModel.Expr.makeStringValue(fieldName)});
		}

		/**
		 * Name binding for function: booleanField.
		 * @see #booleanField(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName booleanField = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "booleanField");

		/**
		 * Constructs an Boolean expression for a database parameter.
		 * If Nothing is specified for the name, then the parameter will be an unnamed one.
		 * @param maybeParamName (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Core.Prelude.String</code>)
		 *          an optional name for the parameter
		 * @return (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Boolean</code>) 
		 *          a Boolean database expression for a database parameter
		 */
		public static final SourceModel.Expr booleanParameter(SourceModel.Expr maybeParamName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanParameter), maybeParamName});
		}

		/**
		 * Name binding for function: booleanParameter.
		 * @see #booleanParameter(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName booleanParameter = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "booleanParameter");

		/**
		 * Constructs a single join tree based on the list of join info items.
		 * This will throw an error if a single set of joins cannot be produced from the
		 * join info items
		 * (which would likely mean that there are tables or groups of tables which are
		 * not linked in some way).
		 * @param joinInfo (CAL type: <code>[Cal.Data.Sql.JoinInfo]</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.JoinNode</code>) 
		 */
		public static final SourceModel.Expr buildJoinTree(SourceModel.Expr joinInfo) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.buildJoinTree), joinInfo});
		}

		/**
		 * Name binding for function: buildJoinTree.
		 * @see #buildJoinTree(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName buildJoinTree = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "buildJoinTree");

		/**
		 * Builds a table name and optional table alias.
		 * @param sqlBuilder (CAL type: <code>Cal.Data.Sql.SqlBuilder</code>)
		 * @param builderState (CAL type: <code>Cal.Data.Sql.SqlBuilderState</code>)
		 * @param queryTable (CAL type: <code>Cal.Data.Sql.QueryTable</code>)
		 * @return (CAL type: <code>Cal.Utilities.PrettyPrinter.Document</code>) 
		 */
		public static final SourceModel.Expr buildTableAndAliasText(SourceModel.Expr sqlBuilder, SourceModel.Expr builderState, SourceModel.Expr queryTable) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.buildTableAndAliasText), sqlBuilder, builderState, queryTable});
		}

		/**
		 * Name binding for function: buildTableAndAliasText.
		 * @see #buildTableAndAliasText(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName buildTableAndAliasText = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "buildTableAndAliasText");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "ceilingExpr");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "charExpr");

		/**
		 * Constructs a database expression to concatenate two string operand expressions.
		 * @param expr1 (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.String</code>)
		 *          a string database expression for the first operand
		 * @param expr2 (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.String</code>)
		 *          a string database expression for the second operand
		 * @return (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.String</code>) 
		 *          a string database expression to concatenate the string operand expressions
		 */
		public static final SourceModel.Expr concatExpr(SourceModel.Expr expr1, SourceModel.Expr expr2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.concatExpr), expr1, expr2});
		}

		/**
		 * Name binding for function: concatExpr.
		 * @see #concatExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName concatExpr = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "concatExpr");

		/**
		 * The function recurses the Expr Boolean and makes sure that every item involved in operation against time constant 
		 * is casted to datetime format explictly.
		 * <p>
		 * Use it to make expression to be evaluated to explicit casts.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Data.Sql.convertToTimeExpr
		 * </dl>
		 * 
		 * @param expr (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Boolean</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Boolean</code>) 
		 *          transformed expr
		 */
		public static final SourceModel.Expr convertDateQueryFieldsToTimeExprs(SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.convertDateQueryFieldsToTimeExprs), expr});
		}

		/**
		 * Name binding for function: convertDateQueryFieldsToTimeExprs.
		 * @see #convertDateQueryFieldsToTimeExprs(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName convertDateQueryFieldsToTimeExprs = 
			QualifiedName.make(
				CAL_Sql.MODULE_NAME, 
				"convertDateQueryFieldsToTimeExprs");

		/**
		 * The <code>convertQueryColumnsToCartesianJoins</code> is a transformation function pushing the sub-queries 
		 * nested as select items down to the from clause to perform cartesian join between datasources.
		 * <p>
		 * It is useful on platforms like Teradata that do not support nested subqueries in select items. 
		 * The conversion will be applied to the passed in query and all its subqueries. 
		 * After all missign aliases have been assigned.
		 * <p>
		 * It expects all aliases to be assigned therefore make sure <code>assignMissingColumnAliases</code> is 
		 * applied before this transform.
		 * <p>
		 * Use it in conjunction with <code>modifyQueries</code> function which can help to perfrom 
		 * the transaformation for all subqueries
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Data.Sql.modifyQueries, Cal.Data.Sql.assignMissingColumnAliases
		 * </dl>
		 * 
		 * @param query (CAL type: <code>Cal.Data.Sql.Query</code>)
		 *          to transform
		 * @return (CAL type: <code>Cal.Data.Sql.Query</code>) 
		 *          transformed query
		 */
		public static final SourceModel.Expr convertQueryColumnsToCartesianJoins(SourceModel.Expr query) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.convertQueryColumnsToCartesianJoins), query});
		}

		/**
		 * Name binding for function: convertQueryColumnsToCartesianJoins.
		 * @see #convertQueryColumnsToCartesianJoins(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName convertQueryColumnsToCartesianJoins = 
			QualifiedName.make(
				CAL_Sql.MODULE_NAME, 
				"convertQueryColumnsToCartesianJoins");

		/**
		 * Converts a value to a double value.
		 * @param arg_1 (CAL type: <code>Cal.Data.Sql.TypedExpr a</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Double</code>) 
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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "convertToDoubleExpr");

		/**
		 * Converts a value to a int value.
		 * @param arg_1 (CAL type: <code>Cal.Data.Sql.TypedExpr a</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Int</code>) 
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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "convertToIntExpr");

		/**
		 * Converts a value to a string value.
		 * @param arg_1 (CAL type: <code>Cal.Data.Sql.TypedExpr a</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.String</code>) 
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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "convertToStringExpr");

		/**
		 * It applies ConvertToTime function to the passed in expression. Use it when you want the end result to be evaluated to the explicit cast call.
		 * @param arg_1 (CAL type: <code>Cal.Data.Sql.TypedExpr a</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Utilities.Time.Time</code>) 
		 *          function expr
		 */
		public static final SourceModel.Expr convertToTimeExpr(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.convertToTimeExpr), arg_1});
		}

		/**
		 * Name binding for function: convertToTimeExpr.
		 * @see #convertToTimeExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName convertToTimeExpr = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "convertToTimeExpr");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "cosExpr");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "cotExpr");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "countAllExpr");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "countExpr");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "databaseExpr");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "dateTimeAddExpr");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "dateTimeDiffExpr");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "dayNameExpr");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "dayOfMonthExpr");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "dayOfWeekExpr");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "dayOfYearExpr");

		/**
		 * Returns the default name of a database function.
		 * @param func (CAL type: <code>Cal.Data.Sql.DbFunction</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr defaultFunctionName(SourceModel.Expr func) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.defaultFunctionName), func});
		}

		/**
		 * Name binding for function: defaultFunctionName.
		 * @see #defaultFunctionName(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName defaultFunctionName = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "defaultFunctionName");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "degreesExpr");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "differenceExpr");

		/**
		 * Produces a query which returns the difference of the two specified queries.
		 * The final result columns will be based on the first query.
		 * The difference will be performed on the corresponding columns in each query.
		 * It is ok for the queries to have different numbers of columns.
		 * @param query1 (CAL type: <code>Cal.Data.Sql.Query</code>)
		 * @param query2 (CAL type: <code>Cal.Data.Sql.Query</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.Query</code>) 
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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "differenceQuery");

		/**
		 * Produces a query which returns the difference of the two specified queries.
		 * The final result columns will be based on the first query.
		 * The difference will be performed on the specified expressions.
		 * @param query1 (CAL type: <code>Cal.Data.Sql.Query</code>)
		 * @param query2 (CAL type: <code>Cal.Data.Sql.Query</code>)
		 * @param compareExprs (CAL type: <code>[(Cal.Data.Sql.Expr, Cal.Data.Sql.Expr)]</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.Query</code>) 
		 */
		public static final SourceModel.Expr differenceQuery2(SourceModel.Expr query1, SourceModel.Expr query2, SourceModel.Expr compareExprs) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.differenceQuery2), query1, query2, compareExprs});
		}

		/**
		 * Name binding for function: differenceQuery2.
		 * @see #differenceQuery2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName differenceQuery2 = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "differenceQuery2");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "distinctAvgExpr");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "distinctCountExpr");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "distinctSumExpr");

		/**
		 * Constructs a database expression to divide two numeric operand expressions.
		 * @param expr1 (CAL type: <code>Cal.Core.Prelude.Num a => Cal.Data.Sql.TypedExpr a</code>)
		 *          a numeric database expression for the first operand
		 * @param expr2 (CAL type: <code>Cal.Core.Prelude.Num a => Cal.Data.Sql.TypedExpr a</code>)
		 *          a numeric database expression for the second operand
		 * @return (CAL type: <code>Cal.Core.Prelude.Num a => Cal.Data.Sql.TypedExpr a</code>) 
		 *          a numeric database expression to divide the two numeric operand expressions
		 */
		public static final SourceModel.Expr divideExpr(SourceModel.Expr expr1, SourceModel.Expr expr2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.divideExpr), expr1, expr2});
		}

		/**
		 * Name binding for function: divideExpr.
		 * @see #divideExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName divideExpr = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "divideExpr");

		/**
		 * Helper binding method for function: doubleField. 
		 * @param table
		 * @param fieldName
		 * @return the SourceModule.expr representing an application of doubleField
		 */
		public static final SourceModel.Expr doubleField(SourceModel.Expr table, SourceModel.Expr fieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleField), table, fieldName});
		}

		/**
		 * @see #doubleField(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param table
		 * @param fieldName
		 * @return the SourceModel.Expr representing an application of doubleField
		 */
		public static final SourceModel.Expr doubleField(SourceModel.Expr table, java.lang.String fieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleField), table, SourceModel.Expr.makeStringValue(fieldName)});
		}

		/**
		 * Name binding for function: doubleField.
		 * @see #doubleField(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleField = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "doubleField");

		/**
		 * Constructs an double expression for a database parameter.
		 * If Nothing is specified for the name, then the parameter will be an unnamed one.
		 * @param maybeParamName (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Core.Prelude.String</code>)
		 *          an optional name for the parameter
		 * @return (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Double</code>) 
		 *          a double database expression for a database parameter
		 */
		public static final SourceModel.Expr doubleParameter(SourceModel.Expr maybeParamName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleParameter), maybeParamName});
		}

		/**
		 * Name binding for function: doubleParameter.
		 * @see #doubleParameter(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleParameter = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "doubleParameter");

		/**
		 * Constructs a database expression which test two operand expressions for equality.
		 * @param expr1 (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Data.Sql.TypedExpr a</code>)
		 *          a database expression for the first operand
		 * @param expr2 (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Data.Sql.TypedExpr a</code>)
		 *          a database expression for the second operand
		 * @return (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Boolean</code>) 
		 *          a Boolean database expression to test the operand expressions for equality
		 */
		public static final SourceModel.Expr eqExpr(SourceModel.Expr expr1, SourceModel.Expr expr2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.eqExpr), expr1, expr2});
		}

		/**
		 * Name binding for function: eqExpr.
		 * @see #eqExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName eqExpr = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "eqExpr");

		/**
		 * Constructs a database expression to test whether the argument subquery expression returns any rows.
		 * @param subqueryExpr (CAL type: <code>Cal.Data.Sql.TypedExpr [a]</code>)
		 *          a subquery expressoin
		 * @return (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Boolean</code>) 
		 *          a Boolean database expression to test whether the argument subquery expression returns any rows
		 */
		public static final SourceModel.Expr existsExpr(SourceModel.Expr subqueryExpr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.existsExpr), subqueryExpr});
		}

		/**
		 * Name binding for function: existsExpr.
		 * @see #existsExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName existsExpr = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "existsExpr");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "expExpr");

		/**
		 * Helper binding method for function: exprOperatorPrecedence. 
		 * @param expr
		 * @return the SourceModule.expr representing an application of exprOperatorPrecedence
		 */
		public static final SourceModel.Expr exprOperatorPrecedence(SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.exprOperatorPrecedence), expr});
		}

		/**
		 * Name binding for function: exprOperatorPrecedence.
		 * @see #exprOperatorPrecedence(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName exprOperatorPrecedence = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "exprOperatorPrecedence");

		/**
		 * Returns whether the expression uses aggregate functions.
		 * <p>
		 * TODO: is there anything else to look for?
		 * 
		 * @param expr (CAL type: <code>Cal.Data.Sql.Expr</code>)
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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "exprUsesAggregation");

		/**
		 * Use it to check if the expression's part uses binary function.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Data.Sql.isBinaryFunction
		 * </dl>
		 * 
		 * @param expr (CAL type: <code>Cal.Data.Sql.Expr</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          True when expression uses binary function
		 */
		public static final SourceModel.Expr exprUsesBinaryFunction(SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.exprUsesBinaryFunction), expr});
		}

		/**
		 * Name binding for function: exprUsesBinaryFunction.
		 * @see #exprUsesBinaryFunction(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName exprUsesBinaryFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "exprUsesBinaryFunction");

		/**
		 * Builds the query text for a single expression.
		 * Note that this should only be called to generate text for top-level
		 * expressions.
		 * For expressions embedded in a SQL query or other expressions, call
		 * <code>Cal.Data.Sql.expressionText2</code> instead and provide the appropriate <code>builderState</code>.
		 * @param builder (CAL type: <code>Cal.Data.Sql.SqlBuilder</code>)
		 * @param expr (CAL type: <code>Cal.Data.Sql.Expr</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr expressionText(SourceModel.Expr builder, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expressionText), builder, expr});
		}

		/**
		 * Name binding for function: expressionText.
		 * @see #expressionText(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expressionText = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "expressionText");

		/**
		 * Builds the query text for a single expression.
		 * @param builder (CAL type: <code>Cal.Data.Sql.SqlBuilder</code>)
		 * @param builderState (CAL type: <code>Cal.Data.Sql.SqlBuilderState</code>)
		 * @param expr (CAL type: <code>Cal.Data.Sql.Expr</code>)
		 * @return (CAL type: <code>Cal.Utilities.PrettyPrinter.Document</code>) 
		 */
		public static final SourceModel.Expr expressionText2(SourceModel.Expr builder, SourceModel.Expr builderState, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expressionText2), builder, builderState, expr});
		}

		/**
		 * Name binding for function: expressionText2.
		 * @see #expressionText2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expressionText2 = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "expressionText2");

		/**
		 * A database expression for the Boolean value False.
		 * @return (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Boolean</code>) 
		 *          the database expression for the Boolean value False
		 */
		public static final SourceModel.Expr falseConstant() {
			return SourceModel.Expr.Var.make(Functions.falseConstant);
		}

		/**
		 * Name binding for function: falseConstant.
		 * @see #falseConstant()
		 */
		public static final QualifiedName falseConstant = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "falseConstant");

		/**
		 * Generates the text for a table field description.
		 * @param builder (CAL type: <code>Cal.Data.Sql.SqlBuilder</code>)
		 * @param fieldDesc (CAL type: <code>Cal.Data.DatabaseMetadata.FieldDescription</code>)
		 * @return (CAL type: <code>Cal.Utilities.PrettyPrinter.Document</code>) 
		 */
		public static final SourceModel.Expr fieldDescriptionText(SourceModel.Expr builder, SourceModel.Expr fieldDesc) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fieldDescriptionText), builder, fieldDesc});
		}

		/**
		 * Name binding for function: fieldDescriptionText.
		 * @see #fieldDescriptionText(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fieldDescriptionText = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "fieldDescriptionText");

		/**
		 * Generates the text for a field data type.
		 * @param builder (CAL type: <code>Cal.Data.Sql.SqlBuilder</code>)
		 * @param dataType (CAL type: <code>Cal.Data.SqlType.SqlType</code>)
		 * @return (CAL type: <code>Cal.Utilities.PrettyPrinter.Document</code>) 
		 */
		public static final SourceModel.Expr fieldTypeText(SourceModel.Expr builder, SourceModel.Expr dataType) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fieldTypeText), builder, dataType});
		}

		/**
		 * Name binding for function: fieldTypeText.
		 * @see #fieldTypeText(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fieldTypeText = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "fieldTypeText");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "floorExpr");

		/**
		 * Returns the name for a SQL function.
		 * @param sqlBuilder (CAL type: <code>Cal.Data.Sql.SqlBuilder</code>)
		 * @param func (CAL type: <code>Cal.Data.Sql.DbFunction</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr functionName(SourceModel.Expr sqlBuilder, SourceModel.Expr func) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.functionName), sqlBuilder, func});
		}

		/**
		 * Name binding for function: functionName.
		 * @see #functionName(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName functionName = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "functionName");

		/**
		 * Returns the arguments from a function expression.
		 * An exception will be thrown if the expression is not a function expression.
		 * @param expr (CAL type: <code>Cal.Data.Sql.Expr</code>)
		 *          a database expression
		 * @return (CAL type: <code>[Cal.Data.Sql.Expr]</code>) 
		 *          the argument expressions from the function expression
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
				CAL_Sql.MODULE_NAME, 
				"getArgumentsFromFunctionExpr");

		/**
		 * Returns the best set of joins for the fields in the specified query.
		 * An error will be thrown if an appropriate join set cannot be found.
		 * If multiple join sets can be used for the query tables, then an error will be returned unless each 
		 * of the compatible join sets uses the same joins to connect these tables (ignoring any joins unrelated to the 
		 * tables in the query).
		 * If a single table is required, then an arbitrary join set (containing this table) will be returned.
		 * If no tables are needed, then <code>Cal.Core.Prelude.Nothing</code> is returned.
		 * @param joinSets (CAL type: <code>[Cal.Data.Sql.JoinSet]</code>)
		 * @param sqlQuery (CAL type: <code>Cal.Data.Sql.Query</code>)
		 * @param anchorFieldExpressions (CAL type: <code>[Cal.Data.Sql.Expr]</code>)
		 * @param preferredJoinSetID (CAL type: <code>Cal.Core.Prelude.Maybe (Cal.Utilities.UniqueIdentifier.UniqueIdentifier Cal.Data.Sql.JoinSet)</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Data.Sql.JoinSet</code>) 
		 */
		public static final SourceModel.Expr getBestJoinSetForQueryFields(SourceModel.Expr joinSets, SourceModel.Expr sqlQuery, SourceModel.Expr anchorFieldExpressions, SourceModel.Expr preferredJoinSetID) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getBestJoinSetForQueryFields), joinSets, sqlQuery, anchorFieldExpressions, preferredJoinSetID});
		}

		/**
		 * Name binding for function: getBestJoinSetForQueryFields.
		 * @see #getBestJoinSetForQueryFields(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getBestJoinSetForQueryFields = 
			QualifiedName.make(
				CAL_Sql.MODULE_NAME, 
				"getBestJoinSetForQueryFields");

		/**
		 * Returns the boolean value from a constant expression.
		 * An exception will be thrown if the expression is not a constant Boolean expression.
		 * @param expr (CAL type: <code>Cal.Data.Sql.Expr</code>)
		 *          a database expression
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          the Boolean value from the constant database expression
		 */
		public static final SourceModel.Expr getBooleanValueFromConstExpr(SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getBooleanValueFromConstExpr), expr});
		}

		/**
		 * Name binding for function: getBooleanValueFromConstExpr.
		 * @see #getBooleanValueFromConstExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getBooleanValueFromConstExpr = 
			QualifiedName.make(
				CAL_Sql.MODULE_NAME, 
				"getBooleanValueFromConstExpr");

		/**
		 * Returns a list of columns that belong to the given table and that are
		 * referenced from within the given query. Only references from joins
		 * and QueryField expressions in the list of projected columns are returned.
		 * <p>
		 * NOTE: This method was written to support the UBO PoC, so it is not meant
		 * to be generally useful.  See module AggregationInfo.
		 * 
		 * @param query (CAL type: <code>Cal.Data.Sql.Query</code>)
		 *          The query.
		 * @param targetTable (CAL type: <code>Cal.Data.Sql.QueryTable</code>)
		 *          The table we're searching for references to.
		 * @return (CAL type: <code>[Cal.Data.Sql.Expr]</code>) 
		 *          A list of expressions that represent the columns belonging to
		 * targetTable.
		 */
		public static final SourceModel.Expr getColumnsForTable(SourceModel.Expr query, SourceModel.Expr targetTable) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getColumnsForTable), query, targetTable});
		}

		/**
		 * Name binding for function: getColumnsForTable.
		 * @see #getColumnsForTable(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getColumnsForTable = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "getColumnsForTable");

		/**
		 * Returns all the tables used in the specified expression.
		 * @param includeSubqueryTables (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 * @param expr (CAL type: <code>Cal.Data.Sql.Expr</code>)
		 * @return (CAL type: <code>[Cal.Data.Sql.QueryTable]</code>) 
		 */
		public static final SourceModel.Expr getExpressionTables(SourceModel.Expr includeSubqueryTables, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getExpressionTables), includeSubqueryTables, expr});
		}

		/**
		 * @see #getExpressionTables(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param includeSubqueryTables
		 * @param expr
		 * @return the SourceModel.Expr representing an application of getExpressionTables
		 */
		public static final SourceModel.Expr getExpressionTables(boolean includeSubqueryTables, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getExpressionTables), SourceModel.Expr.makeBooleanValue(includeSubqueryTables), expr});
		}

		/**
		 * Name binding for function: getExpressionTables.
		 * @see #getExpressionTables(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getExpressionTables = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "getExpressionTables");

		/**
		 * Returns the query table from a query field expression.
		 * An exception will be thrown if the expression is not a query field expression.
		 * @param expr (CAL type: <code>Cal.Data.Sql.Expr</code>)
		 *          a database expression
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the field name from the query field expression
		 */
		public static final SourceModel.Expr getFieldNameFromQueryFieldExpr(SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getFieldNameFromQueryFieldExpr), expr});
		}

		/**
		 * Name binding for function: getFieldNameFromQueryFieldExpr.
		 * @see #getFieldNameFromQueryFieldExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getFieldNameFromQueryFieldExpr = 
			QualifiedName.make(
				CAL_Sql.MODULE_NAME, 
				"getFieldNameFromQueryFieldExpr");

		/**
		 * Returns the function type from a function expression.
		 * An exception will be thrown if the expression is not a function expression.
		 * @param expr (CAL type: <code>Cal.Data.Sql.Expr</code>)
		 *          a database expression
		 * @return (CAL type: <code>Cal.Data.Sql.DbFunction</code>) 
		 *          the function identifier from the function expression
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
				CAL_Sql.MODULE_NAME, 
				"getFunctionFromFunctionExpr");

		/**
		 * 
		 * @param query (CAL type: <code>Cal.Data.Sql.Query</code>)
		 *          The query for which we want the groups.
		 * @return (CAL type: <code>[Cal.Data.Sql.Expr]</code>) 
		 *          The list of groups for the given query, or if the query is a union
		 * then the groups for the first query in the union.
		 */
		public static final SourceModel.Expr getGroups(SourceModel.Expr query) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getGroups), query});
		}

		/**
		 * Name binding for function: getGroups.
		 * @see #getGroups(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getGroups = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "getGroups");

		/**
		 * Returns all the leaf tables from the specified join tree.
		 * @param rootJoinNode (CAL type: <code>Cal.Data.Sql.JoinNode</code>)
		 * @return (CAL type: <code>[Cal.Data.Sql.QueryTable]</code>) 
		 */
		public static final SourceModel.Expr getJoinTreeTables(SourceModel.Expr rootJoinNode) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getJoinTreeTables), rootJoinNode});
		}

		/**
		 * Name binding for function: getJoinTreeTables.
		 * @see #getJoinTreeTables(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getJoinTreeTables = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "getJoinTreeTables");

		/**
		 * Returns the value expressions from a list expression.
		 * An exception will be thrown if the expression is not a list expression.
		 * @param expr (CAL type: <code>Cal.Data.Sql.Expr</code>)
		 *          a database expression
		 * @return (CAL type: <code>[Cal.Data.Sql.Expr]</code>) 
		 *          the list of value expressions form a list expression
		 */
		public static final SourceModel.Expr getListFromListExpr(SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getListFromListExpr), expr});
		}

		/**
		 * Name binding for function: getListFromListExpr.
		 * @see #getListFromListExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getListFromListExpr = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "getListFromListExpr");

		/**
		 * Returns the number value from a constant expression.
		 * An exception will be thrown if the expression is not a numeric constant expression.
		 * @param expr (CAL type: <code>Cal.Data.Sql.Expr</code>)
		 *          a database expression
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 *          the numeric value from the constant database expression
		 */
		public static final SourceModel.Expr getNumberValueFromConstExpr(SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getNumberValueFromConstExpr), expr});
		}

		/**
		 * Name binding for function: getNumberValueFromConstExpr.
		 * @see #getNumberValueFromConstExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getNumberValueFromConstExpr = 
			QualifiedName.make(
				CAL_Sql.MODULE_NAME, 
				"getNumberValueFromConstExpr");

		/**
		 * Returns the name of a named parameter.
		 * An error will be thrown if the parameter is unnamed.
		 * @param param (CAL type: <code>Cal.Data.Sql.Parameter</code>)
		 *          a database parameter
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the name of the named database parameter
		 */
		public static final SourceModel.Expr getParameterName(SourceModel.Expr param) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getParameterName), param});
		}

		/**
		 * Name binding for function: getParameterName.
		 * @see #getParameterName(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getParameterName = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "getParameterName");

		/**
		 * Returns the projected columns for the query.
		 * For a <code>Cal.Data.Sql.Union</code> query, this will return only the projected columns for the first
		 * query.
		 * <p>
		 * TODO: rename this to projectedColumns...
		 * 
		 * @param query (CAL type: <code>Cal.Data.Sql.Query</code>)
		 * @return (CAL type: <code>[Cal.Data.Sql.Expr]</code>) 
		 */
		public static final SourceModel.Expr getProjectedColumns(SourceModel.Expr query) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getProjectedColumns), query});
		}

		/**
		 * Name binding for function: getProjectedColumns.
		 * @see #getProjectedColumns(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getProjectedColumns = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "getProjectedColumns");

		/**
		 * Returns the table alias for a query table.
		 * This is a composition of the base alias and the table ID to give a distinct alias for the table.
		 * @param queryTable (CAL type: <code>Cal.Data.Sql.QueryTable</code>)
		 *          a query table
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the table alias for the query table
		 */
		public static final SourceModel.Expr getQueryTableAlias(SourceModel.Expr queryTable) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getQueryTableAlias), queryTable});
		}

		/**
		 * Name binding for function: getQueryTableAlias.
		 * @see #getQueryTableAlias(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getQueryTableAlias = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "getQueryTableAlias");

		/**
		 * Returns the field name from a query field expression.
		 * An exception will be thrown if the expression is not a query field expression.
		 * @param expr (CAL type: <code>Cal.Data.Sql.Expr</code>)
		 *          a database expression
		 * @return (CAL type: <code>Cal.Data.Sql.QueryTable</code>) 
		 *          the query table from the query field expression
		 */
		public static final SourceModel.Expr getQueryTableFromQueryFieldExpr(SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getQueryTableFromQueryFieldExpr), expr});
		}

		/**
		 * Name binding for function: getQueryTableFromQueryFieldExpr.
		 * @see #getQueryTableFromQueryFieldExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getQueryTableFromQueryFieldExpr = 
			QualifiedName.make(
				CAL_Sql.MODULE_NAME, 
				"getQueryTableFromQueryFieldExpr");

		/**
		 * Returns the name of the table (without quotes).
		 * An empty string is returned for the name of a subquery table.
		 * @param queryTable (CAL type: <code>Cal.Data.Sql.QueryTable</code>)
		 *          a query table
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the name of the base database table, 
		 * or an empty string if the query table is based on a subquery
		 */
		public static final SourceModel.Expr getQueryTableName(SourceModel.Expr queryTable) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getQueryTableName), queryTable});
		}

		/**
		 * Name binding for function: getQueryTableName.
		 * @see #getQueryTableName(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getQueryTableName = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "getQueryTableName");

		/**
		 * Returns a list of the tables used in the query.
		 * This will only include fields used in subqueries if the option is specified.
		 * @param includeSubqueryTables (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 * @param query (CAL type: <code>Cal.Data.Sql.Query</code>)
		 * @return (CAL type: <code>[Cal.Data.Sql.QueryTable]</code>) 
		 */
		public static final SourceModel.Expr getQueryTables(SourceModel.Expr includeSubqueryTables, SourceModel.Expr query) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getQueryTables), includeSubqueryTables, query});
		}

		/**
		 * @see #getQueryTables(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param includeSubqueryTables
		 * @param query
		 * @return the SourceModel.Expr representing an application of getQueryTables
		 */
		public static final SourceModel.Expr getQueryTables(boolean includeSubqueryTables, SourceModel.Expr query) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getQueryTables), SourceModel.Expr.makeBooleanValue(includeSubqueryTables), query});
		}

		/**
		 * Name binding for function: getQueryTables.
		 * @see #getQueryTables(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getQueryTables = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "getQueryTables");

		/**
		 * Returns the string value from a constant expression.
		 * An exception will be thrown if the expression is not a string constant expression.
		 * @param expr (CAL type: <code>Cal.Data.Sql.Expr</code>)
		 *          a database expression
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the string value from the constant database expression
		 */
		public static final SourceModel.Expr getStringValueFromConstExpr(SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getStringValueFromConstExpr), expr});
		}

		/**
		 * Name binding for function: getStringValueFromConstExpr.
		 * @see #getStringValueFromConstExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getStringValueFromConstExpr = 
			QualifiedName.make(
				CAL_Sql.MODULE_NAME, 
				"getStringValueFromConstExpr");

		/**
		 * The <code>isSubQueryExpr</code> retrieves the <code>Query</code> type from untyped expression.
		 * <p>
		 * Throws error when argument is not of SubQueryType
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Data.Sql.isSubQueryExpr
		 * <dd><b>Data Constructors:</b> Cal.Data.Sql.SubQueryExpr
		 * </dl>
		 * 
		 * @param expr (CAL type: <code>Cal.Data.Sql.Expr</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.Query</code>) 
		 *          Query part of the SubQueryExpr
		 */
		public static final SourceModel.Expr getSubQueryFromExpr(SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getSubQueryFromExpr), expr});
		}

		/**
		 * Name binding for function: getSubQueryFromExpr.
		 * @see #getSubQueryFromExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getSubQueryFromExpr = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "getSubQueryFromExpr");

		/**
		 * Returns the time value from a constant expression.
		 * An exception will be thrown if the expression is not a time constant expression.
		 * @param expr (CAL type: <code>Cal.Data.Sql.Expr</code>)
		 *          a database expression
		 * @return (CAL type: <code>Cal.Utilities.Time.Time</code>) 
		 *          the time value from the constant database expression
		 */
		public static final SourceModel.Expr getTimeValueFromConstExpr(SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getTimeValueFromConstExpr), expr});
		}

		/**
		 * Name binding for function: getTimeValueFromConstExpr.
		 * @see #getTimeValueFromConstExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getTimeValueFromConstExpr = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "getTimeValueFromConstExpr");

		/**
		 * Adds grouping to the query.
		 * @param query (CAL type: <code>Cal.Data.Sql.Query</code>)
		 * @param newGroup (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Data.Sql.TypedExpr a</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.Query</code>) 
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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "group");

		/**
		 * Adds grouping on the specified fields.
		 * @param query (CAL type: <code>Cal.Data.Sql.Query</code>)
		 * @param groupFields (CAL type: <code>[Cal.Data.Sql.Expr]</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.Query</code>) 
		 */
		public static final SourceModel.Expr group2(SourceModel.Expr query, SourceModel.Expr groupFields) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.group2), query, groupFields});
		}

		/**
		 * Name binding for function: group2.
		 * @see #group2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName group2 = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "group2");

		/**
		 * Constructs a database expression to test whether the first operand is greater than or equal to the second.
		 * @param expr1 (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Data.Sql.TypedExpr a</code>)
		 *          a database expression for the first operand
		 * @param expr2 (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Data.Sql.TypedExpr a</code>)
		 *          a database expression for the second operand
		 * @return (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Boolean</code>) 
		 *          a Boolean database expression to test whether the first operand is greater than or equal to the second
		 */
		public static final SourceModel.Expr gtEqExpr(SourceModel.Expr expr1, SourceModel.Expr expr2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.gtEqExpr), expr1, expr2});
		}

		/**
		 * Name binding for function: gtEqExpr.
		 * @see #gtEqExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName gtEqExpr = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "gtEqExpr");

		/**
		 * Constructs a database expression to test whether the first operand is greater than the second.
		 * @param expr1 (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Data.Sql.TypedExpr a</code>)
		 *          a database expression for the first operand
		 * @param expr2 (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Data.Sql.TypedExpr a</code>)
		 *          a database expression for the second operand
		 * @return (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Boolean</code>) 
		 *          a Boolean database expression to test whether the first operand is greater than the second
		 */
		public static final SourceModel.Expr gtExpr(SourceModel.Expr expr1, SourceModel.Expr expr2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.gtExpr), expr1, expr2});
		}

		/**
		 * Name binding for function: gtExpr.
		 * @see #gtExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName gtExpr = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "gtExpr");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "hourExpr");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "ifNullExpr");

		/**
		 * Constructs a database expression to test whether the value of the first operand is equal to the value
		 * returned by one of the list operand expressions.
		 * @param leftExpr (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Data.Sql.TypedExpr a</code>)
		 *          a database expression for the first operand
		 * @param listValueExprs (CAL type: <code>Cal.Core.Prelude.Eq a => [Cal.Data.Sql.TypedExpr a]</code>)
		 *          a database expression for the values against the first operand will be tested
		 * @return (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Boolean</code>) 
		 *          a Boolean database expression to test whether the value of the first operand is one of 
		 * the values returned by the list operand expression
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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "inExpr");

		/**
		 * Constructs a database expression to test whether the value of the first operand is one of the values 
		 * returned by the list operand expression.
		 * @param leftExpr (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Data.Sql.TypedExpr a</code>)
		 *          a database expression for the first operand
		 * @param listValuesExpr (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Data.Sql.TypedExpr [a]</code>)
		 *          a database expression for the values against the first operand will be tested
		 * @return (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Boolean</code>) 
		 *          a Boolean database expression to test whether the value of the first operand is one of 
		 * the values returned by the list operand expression
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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "inExpr2");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "insertExpr");

		/**
		 * Helper binding method for function: intField. 
		 * @param table
		 * @param fieldName
		 * @return the SourceModule.expr representing an application of intField
		 */
		public static final SourceModel.Expr intField(SourceModel.Expr table, SourceModel.Expr fieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intField), table, fieldName});
		}

		/**
		 * @see #intField(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param table
		 * @param fieldName
		 * @return the SourceModel.Expr representing an application of intField
		 */
		public static final SourceModel.Expr intField(SourceModel.Expr table, java.lang.String fieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intField), table, SourceModel.Expr.makeStringValue(fieldName)});
		}

		/**
		 * Name binding for function: intField.
		 * @see #intField(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intField = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "intField");

		/**
		 * Converts an integer value to a double value.
		 * The assumption here is that the database will do an implicit conversion
		 * between these 2 types, so no function will be applied in the generated SQL.
		 * A function could be used here if necessary.
		 * @param expr (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Int</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Double</code>) 
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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "intToDoubleExpr");

		/**
		 * Constructs an integer expression for a database parameter.
		 * If Nothing is specified for the name, then the parameter will be an unnamed one.
		 * @param maybeParamName (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Core.Prelude.String</code>)
		 *          an optional name for the parameter
		 * @return (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Int</code>) 
		 *          a integer database expression for a database parameter
		 */
		public static final SourceModel.Expr integerParameter(SourceModel.Expr maybeParamName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.integerParameter), maybeParamName});
		}

		/**
		 * Name binding for function: integerParameter.
		 * @see #integerParameter(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName integerParameter = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "integerParameter");

		/**
		 * Produces a query which returns the intersection of the two specified queries.
		 * The final result columns will be based on the first query.
		 * The intersection will be performed on the corresponding columns in each
		 * query.
		 * It is ok for the queries to have different numbers of columns.
		 * @param query1 (CAL type: <code>Cal.Data.Sql.Query</code>)
		 * @param query2 (CAL type: <code>Cal.Data.Sql.Query</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.Query</code>) 
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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "intersectionQuery");

		/**
		 * Produces a query which returns the intersection of the two specified queries.
		 * The final result columns will be based on the first query.
		 * The intersection will be performed on the specified expressions.
		 * @param query1 (CAL type: <code>Cal.Data.Sql.Query</code>)
		 * @param query2 (CAL type: <code>Cal.Data.Sql.Query</code>)
		 * @param compareExprs (CAL type: <code>[(Cal.Data.Sql.Expr, Cal.Data.Sql.Expr)]</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.Query</code>) 
		 */
		public static final SourceModel.Expr intersectionQuery2(SourceModel.Expr query1, SourceModel.Expr query2, SourceModel.Expr compareExprs) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intersectionQuery2), query1, query2, compareExprs});
		}

		/**
		 * Name binding for function: intersectionQuery2.
		 * @see #intersectionQuery2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intersectionQuery2 = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "intersectionQuery2");

		/**
		 * Indicates whether a specified function is an aggregation function or not.
		 * @param fn (CAL type: <code>Cal.Data.Sql.DbFunction</code>)
		 *          a database function identifier
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          True if the specified function is an aggregation function
		 */
		public static final SourceModel.Expr isAggregationFunction(SourceModel.Expr fn) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isAggregationFunction), fn});
		}

		/**
		 * Name binding for function: isAggregationFunction.
		 * @see #isAggregationFunction(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isAggregationFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "isAggregationFunction");

		/**
		 * 
		 * @param dbFun (CAL type: <code>Cal.Data.Sql.DbFunction</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          True when a given function is a function of two parameters
		 */
		public static final SourceModel.Expr isBinaryFunction(SourceModel.Expr dbFun) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isBinaryFunction), dbFun});
		}

		/**
		 * Name binding for function: isBinaryFunction.
		 * @see #isBinaryFunction(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isBinaryFunction = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "isBinaryFunction");

		/**
		 * Returns <code>Cal.Core.Prelude.True</code> if the expression represents a constant containing a boolean value.
		 * @param expr (CAL type: <code>Cal.Data.Sql.Expr</code>)
		 *          a database expression
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the expression represents a Boolean constant; <code>Cal.Core.Prelude.False</code> otherwise
		 */
		public static final SourceModel.Expr isBooleanConstExpr(SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isBooleanConstExpr), expr});
		}

		/**
		 * Name binding for function: isBooleanConstExpr.
		 * @see #isBooleanConstExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isBooleanConstExpr = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "isBooleanConstExpr");

		/**
		 * Returns <code>Cal.Core.Prelude.True</code> if the expression represents a function (or operator)
		 * application.
		 * @param expr (CAL type: <code>Cal.Data.Sql.Expr</code>)
		 *          a database expression
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the expression represents a database function expression; <code>Cal.Core.Prelude.False</code> otherwise
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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "isFunctionExpr");

		/**
		 * Returns <code>Cal.Core.Prelude.True</code> if the expression represents a list.
		 * @param expr (CAL type: <code>Cal.Data.Sql.Expr</code>)
		 *          a database expression
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the expression represents a list; <code>Cal.Core.Prelude.False</code> otherwise
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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "isListExpr");

		/**
		 * Constructs a database expression to test whether an argument expression is non-null.
		 * @param expr (CAL type: <code>Cal.Data.Sql.TypedExpr a</code>)
		 *          a database expression
		 * @return (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Boolean</code>) 
		 *          a Boolean database expression to test test whether an argument expression is non-null
		 */
		public static final SourceModel.Expr isNotNullExpr(SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isNotNullExpr), expr});
		}

		/**
		 * Name binding for function: isNotNullExpr.
		 * @see #isNotNullExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isNotNullExpr = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "isNotNullExpr");

		/**
		 * Constructs a database expression to test whether an argument expression is null.
		 * @param expr (CAL type: <code>Cal.Data.Sql.TypedExpr a</code>)
		 *          a database expression
		 * @return (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Boolean</code>) 
		 *          a Boolean database expression to test test whether an argument expression is null
		 */
		public static final SourceModel.Expr isNullExpr(SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isNullExpr), expr});
		}

		/**
		 * Name binding for function: isNullExpr.
		 * @see #isNullExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isNullExpr = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "isNullExpr");

		/**
		 * Tests whether an untyped expression has the constant value of null.
		 * This does not check whether an expression evaluates to null -- it simply
		 * checks whether it has the constant value of null.
		 * @param expr (CAL type: <code>Cal.Data.Sql.Expr</code>)
		 *          a database expression
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          True if the expression has the constant value of null
		 */
		public static final SourceModel.Expr isNullValue(SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isNullValue), expr});
		}

		/**
		 * Name binding for function: isNullValue.
		 * @see #isNullValue(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isNullValue = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "isNullValue");

		/**
		 * Returns <code>Cal.Core.Prelude.True</code> if the expression represents a constant containing a number value.
		 * @param expr (CAL type: <code>Cal.Data.Sql.Expr</code>)
		 *          a database expression
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the expression represents a numeric literal; <code>Cal.Core.Prelude.False</code> otherwise
		 */
		public static final SourceModel.Expr isNumberConstExpr(SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isNumberConstExpr), expr});
		}

		/**
		 * Name binding for function: isNumberConstExpr.
		 * @see #isNumberConstExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isNumberConstExpr = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "isNumberConstExpr");

		/**
		 * Returns whether the function is really an operator (in-fix).
		 * @param func (CAL type: <code>Cal.Data.Sql.DbFunction</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr isOperator(SourceModel.Expr func) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isOperator), func});
		}

		/**
		 * Name binding for function: isOperator.
		 * @see #isOperator(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isOperator = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "isOperator");

		/**
		 * Returns whether the expression represents an operator applied to one or more
		 * arguments.
		 * @param expr (CAL type: <code>Cal.Data.Sql.Expr</code>)
		 *          a database expression
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the expression represents an operator applied to one or
		 * more arguments; <code>Cal.Core.Prelude.False</code> otherwise
		 */
		public static final SourceModel.Expr isOperatorExpr(SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isOperatorExpr), expr});
		}

		/**
		 * Name binding for function: isOperatorExpr.
		 * @see #isOperatorExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isOperatorExpr = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "isOperatorExpr");

		/**
		 * Returns <code>Cal.Core.Prelude.True</code> if the expression represents a field in a query table.
		 * @param expr (CAL type: <code>Cal.Data.Sql.Expr</code>)
		 *          a database expression
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the expression represents a field in a query table; <code>Cal.Core.Prelude.False</code> otherwise
		 */
		public static final SourceModel.Expr isQueryFieldExpr(SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isQueryFieldExpr), expr});
		}

		/**
		 * Name binding for function: isQueryFieldExpr.
		 * @see #isQueryFieldExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isQueryFieldExpr = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "isQueryFieldExpr");

		/**
		 * The <code>isSubQueryExpr</code> tests passed in query if it going to retrieve single column and value in the result set.
		 * @param query (CAL type: <code>Cal.Data.Sql.Query</code>)
		 *          to test
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          True when expected resultset is using single column with applied aggregate function and the query has no groupings.
		 */
		public static final SourceModel.Expr isSingletonResultQuery(SourceModel.Expr query) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isSingletonResultQuery), query});
		}

		/**
		 * Name binding for function: isSingletonResultQuery.
		 * @see #isSingletonResultQuery(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isSingletonResultQuery = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "isSingletonResultQuery");

		/**
		 * Returns <code>Cal.Core.Prelude.True</code> if the expression represents a constant containing a string value.
		 * @param expr (CAL type: <code>Cal.Data.Sql.Expr</code>)
		 *          a database expression
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the expression represents a string literal; <code>Cal.Core.Prelude.False</code> otherwise
		 */
		public static final SourceModel.Expr isStringConstExpr(SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isStringConstExpr), expr});
		}

		/**
		 * Name binding for function: isStringConstExpr.
		 * @see #isStringConstExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isStringConstExpr = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "isStringConstExpr");

		/**
		 * The <code>isSubQueryExpr</code> tests expression if it is of SubQueryExpr type.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Data Constructors:</b> Cal.Data.Sql.SubQueryExpr
		 * </dl>
		 * 
		 * @param expr (CAL type: <code>Cal.Data.Sql.Expr</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          True when Expr is a <code>SubQueryExpr</code>
		 */
		public static final SourceModel.Expr isSubQueryExpr(SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isSubQueryExpr), expr});
		}

		/**
		 * Name binding for function: isSubQueryExpr.
		 * @see #isSubQueryExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isSubQueryExpr = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "isSubQueryExpr");

		/**
		 * Returns whether the specified table is a subquery table.
		 * @param queryTable (CAL type: <code>Cal.Data.Sql.QueryTable</code>)
		 *          a query table
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          True if the table is a subquery (or opaque subquery) table;
		 * False if the table is a base database table
		 */
		public static final SourceModel.Expr isSubqueryTable(SourceModel.Expr queryTable) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isSubqueryTable), queryTable});
		}

		/**
		 * Name binding for function: isSubqueryTable.
		 * @see #isSubqueryTable(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isSubqueryTable = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "isSubqueryTable");

		/**
		 * Returns <code>Cal.Core.Prelude.True</code> if the expression represents a constant containing a time value.
		 * @param expr (CAL type: <code>Cal.Data.Sql.Expr</code>)
		 *          a database expression
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the expression represents a time constant; <code>Cal.Core.Prelude.False</code> otherwise
		 */
		public static final SourceModel.Expr isTimeConstExpr(SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isTimeConstExpr), expr});
		}

		/**
		 * Name binding for function: isTimeConstExpr.
		 * @see #isTimeConstExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isTimeConstExpr = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "isTimeConstExpr");

		/**
		 * Returns whether the query option specifies TopN criteria.
		 * @param option (CAL type: <code>Cal.Data.Sql.QueryOption</code>)
		 *          a query option
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          True if the query option specifies TopN criteria
		 */
		public static final SourceModel.Expr isTopNOption(SourceModel.Expr option) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isTopNOption), option});
		}

		/**
		 * Name binding for function: isTopNOption.
		 * @see #isTopNOption(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isTopNOption = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "isTopNOption");

		/**
		 * Returns whether the parameter is a named one.
		 * @param param (CAL type: <code>Cal.Data.Sql.Parameter</code>)
		 *          a database parameter
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          True if the parameter is named one; False if the parameter is unnamed
		 */
		public static final SourceModel.Expr isUnnamedParameter(SourceModel.Expr param) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isUnnamedParameter), param});
		}

		/**
		 * Name binding for function: isUnnamedParameter.
		 * @see #isUnnamedParameter(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isUnnamedParameter = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "isUnnamedParameter");

		/**
		 * Adds a join between 2 tables.
		 * @param query (CAL type: <code>Cal.Data.Sql.Query</code>)
		 * @param newJoinInfo (CAL type: <code>Cal.Data.Sql.JoinInfo</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.Query</code>) 
		 */
		public static final SourceModel.Expr join(SourceModel.Expr query, SourceModel.Expr newJoinInfo) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.join), query, newJoinInfo});
		}

		/**
		 * Name binding for function: join.
		 * @see #join(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName join = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "join");

		/**
		 * Adds the specified joins to the query.
		 * @param query (CAL type: <code>Cal.Data.Sql.Query</code>)
		 * @param newJoins (CAL type: <code>[Cal.Data.Sql.JoinInfo]</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.Query</code>) 
		 */
		public static final SourceModel.Expr join2(SourceModel.Expr query, SourceModel.Expr newJoins) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.join2), query, newJoins});
		}

		/**
		 * Name binding for function: join2.
		 * @see #join2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName join2 = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "join2");

		/**
		 * Returns the join info from the join set.
		 * @param joinSet (CAL type: <code>Cal.Data.Sql.JoinSet</code>)
		 *          a join set
		 * @return (CAL type: <code>Cal.Data.Sql.JoinNode</code>) 
		 *          the root join node of the set
		 */
		public static final SourceModel.Expr joinSetJoinTree(SourceModel.Expr joinSet) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.joinSetJoinTree), joinSet});
		}

		/**
		 * Name binding for function: joinSetJoinTree.
		 * @see #joinSetJoinTree(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName joinSetJoinTree = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "joinSetJoinTree");

		/**
		 * Returns the join information from the query.
		 * For a <code>Cal.Data.Sql.Union</code> query, the joins from the first query will be returned.
		 * @param query (CAL type: <code>Cal.Data.Sql.Query</code>)
		 * @return (CAL type: <code>[Cal.Data.Sql.JoinNode]</code>) 
		 */
		public static final SourceModel.Expr joins(SourceModel.Expr query) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.joins), query});
		}

		/**
		 * Name binding for function: joins.
		 * @see #joins(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName joins = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "joins");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "lcaseExpr");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "leftExpr");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "lengthExpr");

		/**
		 * Constructs a database expression to test whether the first string operand expression matches 
		 * the pattern of the second operand expression.
		 * @param strExpr (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.String</code>)
		 *          a string database expression for the first operand
		 * @param patternExpr (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.String</code>)
		 *          a string database expression for the pattern against which the first operand will be tested
		 * @return (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Boolean</code>) 
		 *          a Boolean database expression to test whether first string operand expression matches 
		 * the pattern of the second operand expression
		 */
		public static final SourceModel.Expr likeExpr(SourceModel.Expr strExpr, SourceModel.Expr patternExpr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.likeExpr), strExpr, patternExpr});
		}

		/**
		 * Name binding for function: likeExpr.
		 * @see #likeExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName likeExpr = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "likeExpr");

		/**
		 * Linearizes the structure of the given statement by extracting all
		 * statements nested in any compound statement.  This function will be
		 * invoked recursively on all compound statements.  If the given statement
		 * is not a compound statement, then it will be returned as is.
		 * @param statement (CAL type: <code>Cal.Data.Sql.Statement</code>)
		 * @return (CAL type: <code>[Cal.Data.Sql.Statement]</code>) 
		 *          A linearized/flattened list of statement.
		 */
		public static final SourceModel.Expr linearizeStatement(SourceModel.Expr statement) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.linearizeStatement), statement});
		}

		/**
		 * Name binding for function: linearizeStatement.
		 * @see #linearizeStatement(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName linearizeStatement = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "linearizeStatement");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "locate2Expr");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "locateExpr");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "log10Expr");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "logExpr");

		/**
		 * Constructs a database expression to test whether the first operand is less than or equal to the second.
		 * @param expr1 (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Data.Sql.TypedExpr a</code>)
		 *          a database expression for the first operand
		 * @param expr2 (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Data.Sql.TypedExpr a</code>)
		 *          a database expression for the second operand
		 * @return (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Boolean</code>) 
		 *          a Boolean database expression to test whether the first operand is less than or equal to the second
		 */
		public static final SourceModel.Expr ltEqExpr(SourceModel.Expr expr1, SourceModel.Expr expr2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.ltEqExpr), expr1, expr2});
		}

		/**
		 * Name binding for function: ltEqExpr.
		 * @see #ltEqExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName ltEqExpr = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "ltEqExpr");

		/**
		 * Constructs a database expression to test whether the first operand is less than the second.
		 * @param expr1 (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Data.Sql.TypedExpr a</code>)
		 *          a database expression for the first operand
		 * @param expr2 (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Data.Sql.TypedExpr a</code>)
		 *          a database expression for the second operand
		 * @return (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Boolean</code>) 
		 *          a Boolean database expression to test whether the first operand is less than the second
		 */
		public static final SourceModel.Expr ltExpr(SourceModel.Expr expr1, SourceModel.Expr expr2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.ltExpr), expr1, expr2});
		}

		/**
		 * Name binding for function: ltExpr.
		 * @see #ltExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName ltExpr = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "ltExpr");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "ltrimExpr");

		/**
		 * Helper binding method for function: makeCommitStatement. 
		 * @return the SourceModule.expr representing an application of makeCommitStatement
		 */
		public static final SourceModel.Expr makeCommitStatement() {
			return SourceModel.Expr.Var.make(Functions.makeCommitStatement);
		}

		/**
		 * Name binding for function: makeCommitStatement.
		 * @see #makeCommitStatement()
		 */
		public static final QualifiedName makeCommitStatement = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "makeCommitStatement");

		/**
		 * Creates a compound statement from a list of statements.  This function
		 * will attempt to flatten any compound statements contained in the argument
		 * list.
		 * @param statements (CAL type: <code>[Cal.Data.Sql.Statement]</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.Statement</code>) 
		 */
		public static final SourceModel.Expr makeCompoundStatement(SourceModel.Expr statements) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeCompoundStatement), statements});
		}

		/**
		 * Name binding for function: makeCompoundStatement.
		 * @see #makeCompoundStatement(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeCompoundStatement = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "makeCompoundStatement");

		/**
		 * Helper binding method for function: makeCreateDatabaseStatement. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of makeCreateDatabaseStatement
		 */
		public static final SourceModel.Expr makeCreateDatabaseStatement(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeCreateDatabaseStatement), arg_1, arg_2});
		}

		/**
		 * @see #makeCreateDatabaseStatement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of makeCreateDatabaseStatement
		 */
		public static final SourceModel.Expr makeCreateDatabaseStatement(SourceModel.Expr arg_1, boolean arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeCreateDatabaseStatement), arg_1, SourceModel.Expr.makeBooleanValue(arg_2)});
		}

		/**
		 * Name binding for function: makeCreateDatabaseStatement.
		 * @see #makeCreateDatabaseStatement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeCreateDatabaseStatement = 
			QualifiedName.make(
				CAL_Sql.MODULE_NAME, 
				"makeCreateDatabaseStatement");

		/**
		 * Helper binding method for function: makeCreateTableStatement. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of makeCreateTableStatement
		 */
		public static final SourceModel.Expr makeCreateTableStatement(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeCreateTableStatement), arg_1, arg_2});
		}

		/**
		 * @see #makeCreateTableStatement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of makeCreateTableStatement
		 */
		public static final SourceModel.Expr makeCreateTableStatement(SourceModel.Expr arg_1, boolean arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeCreateTableStatement), arg_1, SourceModel.Expr.makeBooleanValue(arg_2)});
		}

		/**
		 * Name binding for function: makeCreateTableStatement.
		 * @see #makeCreateTableStatement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeCreateTableStatement = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "makeCreateTableStatement");

		/**
		 * Helper binding method for function: makeCreateViewStatement. 
		 * @param arg_1
		 * @param arg_2
		 * @param arg_3
		 * @return the SourceModule.expr representing an application of makeCreateViewStatement
		 */
		public static final SourceModel.Expr makeCreateViewStatement(SourceModel.Expr arg_1, SourceModel.Expr arg_2, SourceModel.Expr arg_3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeCreateViewStatement), arg_1, arg_2, arg_3});
		}

		/**
		 * @see #makeCreateViewStatement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @param arg_3
		 * @return the SourceModel.Expr representing an application of makeCreateViewStatement
		 */
		public static final SourceModel.Expr makeCreateViewStatement(SourceModel.Expr arg_1, SourceModel.Expr arg_2, boolean arg_3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeCreateViewStatement), arg_1, arg_2, SourceModel.Expr.makeBooleanValue(arg_3)});
		}

		/**
		 * Name binding for function: makeCreateViewStatement.
		 * @see #makeCreateViewStatement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeCreateViewStatement = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "makeCreateViewStatement");

		/**
		 * Helper binding method for function: makeDeleteRowsStatement. 
		 * @param tableRef
		 * @param condition
		 * @return the SourceModule.expr representing an application of makeDeleteRowsStatement
		 */
		public static final SourceModel.Expr makeDeleteRowsStatement(SourceModel.Expr tableRef, SourceModel.Expr condition) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeDeleteRowsStatement), tableRef, condition});
		}

		/**
		 * Name binding for function: makeDeleteRowsStatement.
		 * @see #makeDeleteRowsStatement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeDeleteRowsStatement = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "makeDeleteRowsStatement");

		/**
		 * Helper binding method for function: makeDropDatabaseStatement. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of makeDropDatabaseStatement
		 */
		public static final SourceModel.Expr makeDropDatabaseStatement(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeDropDatabaseStatement), arg_1, arg_2});
		}

		/**
		 * @see #makeDropDatabaseStatement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of makeDropDatabaseStatement
		 */
		public static final SourceModel.Expr makeDropDatabaseStatement(SourceModel.Expr arg_1, boolean arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeDropDatabaseStatement), arg_1, SourceModel.Expr.makeBooleanValue(arg_2)});
		}

		/**
		 * Name binding for function: makeDropDatabaseStatement.
		 * @see #makeDropDatabaseStatement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeDropDatabaseStatement = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "makeDropDatabaseStatement");

		/**
		 * Helper binding method for function: makeDropTableStatement. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of makeDropTableStatement
		 */
		public static final SourceModel.Expr makeDropTableStatement(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeDropTableStatement), arg_1, arg_2});
		}

		/**
		 * @see #makeDropTableStatement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of makeDropTableStatement
		 */
		public static final SourceModel.Expr makeDropTableStatement(SourceModel.Expr arg_1, boolean arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeDropTableStatement), arg_1, SourceModel.Expr.makeBooleanValue(arg_2)});
		}

		/**
		 * Name binding for function: makeDropTableStatement.
		 * @see #makeDropTableStatement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeDropTableStatement = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "makeDropTableStatement");

		/**
		 * Helper binding method for function: makeDropViewStatement. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of makeDropViewStatement
		 */
		public static final SourceModel.Expr makeDropViewStatement(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeDropViewStatement), arg_1, arg_2});
		}

		/**
		 * @see #makeDropViewStatement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of makeDropViewStatement
		 */
		public static final SourceModel.Expr makeDropViewStatement(SourceModel.Expr arg_1, boolean arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeDropViewStatement), arg_1, SourceModel.Expr.makeBooleanValue(arg_2)});
		}

		/**
		 * Name binding for function: makeDropViewStatement.
		 * @see #makeDropViewStatement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeDropViewStatement = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "makeDropViewStatement");

		/**
		 * Helper binding method for function: makeInsertQueryValuesByColumnStatement. 
		 * @param tableRef
		 * @param destColumns
		 * @param insertQuery
		 * @return the SourceModule.expr representing an application of makeInsertQueryValuesByColumnStatement
		 */
		public static final SourceModel.Expr makeInsertQueryValuesByColumnStatement(SourceModel.Expr tableRef, SourceModel.Expr destColumns, SourceModel.Expr insertQuery) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeInsertQueryValuesByColumnStatement), tableRef, destColumns, insertQuery});
		}

		/**
		 * Name binding for function: makeInsertQueryValuesByColumnStatement.
		 * @see #makeInsertQueryValuesByColumnStatement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeInsertQueryValuesByColumnStatement = 
			QualifiedName.make(
				CAL_Sql.MODULE_NAME, 
				"makeInsertQueryValuesByColumnStatement");

		/**
		 * Helper binding method for function: makeInsertQueryValuesStatement. 
		 * @param tableRef
		 * @param insertQuery
		 * @return the SourceModule.expr representing an application of makeInsertQueryValuesStatement
		 */
		public static final SourceModel.Expr makeInsertQueryValuesStatement(SourceModel.Expr tableRef, SourceModel.Expr insertQuery) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeInsertQueryValuesStatement), tableRef, insertQuery});
		}

		/**
		 * Name binding for function: makeInsertQueryValuesStatement.
		 * @see #makeInsertQueryValuesStatement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeInsertQueryValuesStatement = 
			QualifiedName.make(
				CAL_Sql.MODULE_NAME, 
				"makeInsertQueryValuesStatement");

		/**
		 * Helper binding method for function: makeInsertValuesByColumnStatement. 
		 * @param tableRef
		 * @param destColumns
		 * @param rowValues
		 * @return the SourceModule.expr representing an application of makeInsertValuesByColumnStatement
		 */
		public static final SourceModel.Expr makeInsertValuesByColumnStatement(SourceModel.Expr tableRef, SourceModel.Expr destColumns, SourceModel.Expr rowValues) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeInsertValuesByColumnStatement), tableRef, destColumns, rowValues});
		}

		/**
		 * Name binding for function: makeInsertValuesByColumnStatement.
		 * @see #makeInsertValuesByColumnStatement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeInsertValuesByColumnStatement = 
			QualifiedName.make(
				CAL_Sql.MODULE_NAME, 
				"makeInsertValuesByColumnStatement");

		/**
		 * Helper binding method for function: makeInsertValuesStatement. 
		 * @param tableRef
		 * @param rowValues
		 * @return the SourceModule.expr representing an application of makeInsertValuesStatement
		 */
		public static final SourceModel.Expr makeInsertValuesStatement(SourceModel.Expr tableRef, SourceModel.Expr rowValues) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeInsertValuesStatement), tableRef, rowValues});
		}

		/**
		 * Name binding for function: makeInsertValuesStatement.
		 * @see #makeInsertValuesStatement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeInsertValuesStatement = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "makeInsertValuesStatement");

		/**
		 * Creates a join info using the query field info specified.
		 * The expressions provided must both be <code>Cal.Data.Sql.QueryField</code>s.
		 * The link expressions will be created to compare each pair of fields using
		 * <code>'='</code>.
		 * If other link comparison are required, the <code>Cal.Data.Sql.JoinInfo</code> can be constructed with
		 * these explicitly.
		 * @param leftField (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Data.Sql.TypedExpr a</code>)
		 *          an expression for a field in the left table to be joined
		 * @param rightField (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Data.Sql.TypedExpr a</code>)
		 *          an expression for a field in the right table to be joined
		 * @param joinType (CAL type: <code>Cal.Data.Sql.JoinType</code>)
		 *          the type of join being performed (inner, left outer, right outer, or full outer)
		 * @return (CAL type: <code>Cal.Data.Sql.JoinInfo</code>) 
		 *          join info between the specified table fields
		 */
		public static final SourceModel.Expr makeJoinInfo(SourceModel.Expr leftField, SourceModel.Expr rightField, SourceModel.Expr joinType) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeJoinInfo), leftField, rightField, joinType});
		}

		/**
		 * Name binding for function: makeJoinInfo.
		 * @see #makeJoinInfo(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeJoinInfo = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "makeJoinInfo");

		/**
		 * Creates a join info between 2 tables using the specified linking expression.
		 * @param leftTable (CAL type: <code>Cal.Data.Sql.QueryTable</code>)
		 *          one of the query tables to be joined
		 * @param rightTable (CAL type: <code>Cal.Data.Sql.QueryTable</code>)
		 *          the other query table to be joined
		 * @param linkingExpr (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Boolean</code>)
		 *          a Boolean expression joining fields from the tables
		 * @param joinType (CAL type: <code>Cal.Data.Sql.JoinType</code>)
		 *          the type of join being performed (inner, left outer, right outer, or full outer)
		 * @return (CAL type: <code>Cal.Data.Sql.JoinInfo</code>) 
		 */
		public static final SourceModel.Expr makeJoinInfo2(SourceModel.Expr leftTable, SourceModel.Expr rightTable, SourceModel.Expr linkingExpr, SourceModel.Expr joinType) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeJoinInfo2), leftTable, rightTable, linkingExpr, joinType});
		}

		/**
		 * Name binding for function: makeJoinInfo2.
		 * @see #makeJoinInfo2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeJoinInfo2 = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "makeJoinInfo2");

		/**
		 * Construct a join set from a list of JoinInfo values.
		 * @param name (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the name of the join set
		 * @param joins (CAL type: <code>[Cal.Data.Sql.JoinInfo]</code>)
		 *          the joins to be part of the set
		 * @return (CAL type: <code>Cal.Data.Sql.JoinSet</code>) 
		 *          a join set with the specified name
		 */
		public static final SourceModel.Expr makeJoinSet(SourceModel.Expr name, SourceModel.Expr joins) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeJoinSet), name, joins});
		}

		/**
		 * @see #makeJoinSet(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param name
		 * @param joins
		 * @return the SourceModel.Expr representing an application of makeJoinSet
		 */
		public static final SourceModel.Expr makeJoinSet(java.lang.String name, SourceModel.Expr joins) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeJoinSet), SourceModel.Expr.makeStringValue(name), joins});
		}

		/**
		 * Name binding for function: makeJoinSet.
		 * @see #makeJoinSet(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeJoinSet = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "makeJoinSet");

		/**
		 * Construct a join set from a JoinNode tree
		 * @param name (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the name of the join set
		 * @param joinTree (CAL type: <code>Cal.Data.Sql.JoinNode</code>)
		 *          the root join of the join set
		 * @return (CAL type: <code>Cal.Data.Sql.JoinSet</code>) 
		 *          a join set with the specified name
		 */
		public static final SourceModel.Expr makeJoinSet2(SourceModel.Expr name, SourceModel.Expr joinTree) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeJoinSet2), name, joinTree});
		}

		/**
		 * @see #makeJoinSet2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param name
		 * @param joinTree
		 * @return the SourceModel.Expr representing an application of makeJoinSet2
		 */
		public static final SourceModel.Expr makeJoinSet2(java.lang.String name, SourceModel.Expr joinTree) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeJoinSet2), SourceModel.Expr.makeStringValue(name), joinTree});
		}

		/**
		 * Name binding for function: makeJoinSet2.
		 * @see #makeJoinSet2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeJoinSet2 = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "makeJoinSet2");

		/**
		 * Constructs a subquery table using the specified query text and alias.
		 * The query text must be valid when used as a subquery.
		 * In most cases, this means that it cannot contain an ORDER BY clause.
		 * @param subqueryText (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          a SELECT statement text to be treated as a query table
		 * @param tableAlias (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the alias to use for the table in the query;
		 * If an empty alias is specified, then a default one will be generated.
		 * @return (CAL type: <code>Cal.Data.Sql.QueryTable</code>) 
		 *          a query table for the nested SELECT query with the given alias
		 */
		public static final SourceModel.Expr makeOpaqueSubQueryTable(SourceModel.Expr subqueryText, SourceModel.Expr tableAlias) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeOpaqueSubQueryTable), subqueryText, tableAlias});
		}

		/**
		 * @see #makeOpaqueSubQueryTable(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param subqueryText
		 * @param tableAlias
		 * @return the SourceModel.Expr representing an application of makeOpaqueSubQueryTable
		 */
		public static final SourceModel.Expr makeOpaqueSubQueryTable(java.lang.String subqueryText, java.lang.String tableAlias) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeOpaqueSubQueryTable), SourceModel.Expr.makeStringValue(subqueryText), SourceModel.Expr.makeStringValue(tableAlias)});
		}

		/**
		 * Name binding for function: makeOpaqueSubQueryTable.
		 * @see #makeOpaqueSubQueryTable(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeOpaqueSubQueryTable = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "makeOpaqueSubQueryTable");

		/**
		 * Constructs a query table using the specified table name.
		 * The table alias will be based on the table name.
		 * @param tableName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the name of the table to be used in the query
		 * @return (CAL type: <code>Cal.Data.Sql.QueryTable</code>) 
		 *          a query table for the specified table using the table name as the alias
		 */
		public static final SourceModel.Expr makeQueryTable(SourceModel.Expr tableName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeQueryTable), tableName});
		}

		/**
		 * @see #makeQueryTable(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param tableName
		 * @return the SourceModel.Expr representing an application of makeQueryTable
		 */
		public static final SourceModel.Expr makeQueryTable(java.lang.String tableName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeQueryTable), SourceModel.Expr.makeStringValue(tableName)});
		}

		/**
		 * Name binding for function: makeQueryTable.
		 * @see #makeQueryTable(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeQueryTable = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "makeQueryTable");

		/**
		 * Constructs a query table using the specified table name and base alias name.
		 * If no table alias is specified, the table name will be used as the alias.
		 * @param tableName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the name of the table to be used in the query
		 * @param tableAlias (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the alias to use for the table in the query;
		 * An empty alias name indicates that the table name should be used as the alias
		 * @return (CAL type: <code>Cal.Data.Sql.QueryTable</code>) 
		 *          a query table for the specified table and alias
		 */
		public static final SourceModel.Expr makeQueryTableWithAlias(SourceModel.Expr tableName, SourceModel.Expr tableAlias) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeQueryTableWithAlias), tableName, tableAlias});
		}

		/**
		 * @see #makeQueryTableWithAlias(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param tableName
		 * @param tableAlias
		 * @return the SourceModel.Expr representing an application of makeQueryTableWithAlias
		 */
		public static final SourceModel.Expr makeQueryTableWithAlias(java.lang.String tableName, java.lang.String tableAlias) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeQueryTableWithAlias), SourceModel.Expr.makeStringValue(tableName), SourceModel.Expr.makeStringValue(tableAlias)});
		}

		/**
		 * Name binding for function: makeQueryTableWithAlias.
		 * @see #makeQueryTableWithAlias(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeQueryTableWithAlias = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "makeQueryTableWithAlias");

		/**
		 * Returns a 'safe' version of the specified name.
		 * @param sqlBuilder (CAL type: <code>Cal.Data.Sql.SqlBuilder</code>)
		 * @param name (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr makeSafeSqlName(SourceModel.Expr sqlBuilder, SourceModel.Expr name) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeSafeSqlName), sqlBuilder, name});
		}

		/**
		 * @see #makeSafeSqlName(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param sqlBuilder
		 * @param name
		 * @return the SourceModel.Expr representing an application of makeSafeSqlName
		 */
		public static final SourceModel.Expr makeSafeSqlName(SourceModel.Expr sqlBuilder, java.lang.String name) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeSafeSqlName), sqlBuilder, SourceModel.Expr.makeStringValue(name)});
		}

		/**
		 * Name binding for function: makeSafeSqlName.
		 * @see #makeSafeSqlName(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeSafeSqlName = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "makeSafeSqlName");

		/**
		 * Returns 'safe' versions of the specified names.
		 * The names will all be distinct.
		 * @param sqlBuilder (CAL type: <code>Cal.Data.Sql.SqlBuilder</code>)
		 * @param namesInUse (CAL type: <code>[Cal.Core.Prelude.String]</code>)
		 * @param names (CAL type: <code>[Cal.Core.Prelude.String]</code>)
		 * @return (CAL type: <code>[Cal.Core.Prelude.String]</code>) 
		 */
		public static final SourceModel.Expr makeSafeSqlNames(SourceModel.Expr sqlBuilder, SourceModel.Expr namesInUse, SourceModel.Expr names) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeSafeSqlNames), sqlBuilder, namesInUse, names});
		}

		/**
		 * Name binding for function: makeSafeSqlNames.
		 * @see #makeSafeSqlNames(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeSafeSqlNames = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "makeSafeSqlNames");

		/**
		 * Constructs a SqlBuilder using the functions provided in a record.
		 * The advantage of this approach is that a SqlBuilder can easily override selected functions from the record of another SqlBuilder.
		 * @param sqlBuilderFns (CAL type: <code>{addParens :: Cal.Utilities.PrettyPrinter.Document -> Cal.Utilities.PrettyPrinter.Document, booleanToSql :: Cal.Core.Prelude.Boolean -> Cal.Utilities.PrettyPrinter.Document, buildCommitStatement :: Cal.Data.Sql.SqlBuilder -> Cal.Utilities.PrettyPrinter.Document, buildCreateDatabaseStatement :: Cal.Data.Sql.SqlBuilder -> Cal.Data.DatabaseMetadata.DatabaseReference -> Cal.Core.Prelude.Boolean -> Cal.Utilities.PrettyPrinter.Document, buildCreateTableStatement :: Cal.Data.Sql.SqlBuilder -> Cal.Data.DatabaseMetadata.TableDescription -> Cal.Core.Prelude.Boolean -> Cal.Utilities.PrettyPrinter.Document, buildCreateViewStatement :: Cal.Data.Sql.SqlBuilder -> Cal.Data.DatabaseMetadata.TableReference -> Cal.Data.Sql.Query -> Cal.Core.Prelude.Boolean -> Cal.Utilities.PrettyPrinter.Document, buildDeleteRowsStatement :: Cal.Data.Sql.SqlBuilder -> Cal.Data.Sql.SqlBuilderState -> Cal.Data.DatabaseMetadata.TableReference -> Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Boolean -> Cal.Utilities.PrettyPrinter.Document, buildDropDatabaseStatement :: Cal.Data.Sql.SqlBuilder -> Cal.Data.DatabaseMetadata.DatabaseReference -> Cal.Core.Prelude.Boolean -> Cal.Utilities.PrettyPrinter.Document, buildDropTableStatement :: Cal.Data.Sql.SqlBuilder -> Cal.Data.DatabaseMetadata.TableReference -> Cal.Core.Prelude.Boolean -> Cal.Utilities.PrettyPrinter.Document, buildDropViewStatement :: Cal.Data.Sql.SqlBuilder -> Cal.Data.DatabaseMetadata.TableReference -> Cal.Core.Prelude.Boolean -> Cal.Utilities.PrettyPrinter.Document, buildFieldDescription :: Cal.Data.Sql.SqlBuilder -> Cal.Data.DatabaseMetadata.FieldDescription -> Cal.Utilities.PrettyPrinter.Document, buildFieldType :: Cal.Data.SqlType.SqlType -> Cal.Utilities.PrettyPrinter.Document, buildFromClause :: Cal.Data.Sql.SqlBuilder -> Cal.Data.Sql.SqlBuilderState -> [Cal.Data.Sql.JoinNode] -> Cal.Utilities.PrettyPrinter.Document, buildGroupByClause :: Cal.Data.Sql.SqlBuilder -> Cal.Data.Sql.SqlBuilderState -> [Cal.Data.Sql.Expr] -> Cal.Utilities.PrettyPrinter.Document, buildHavingClause :: Cal.Data.Sql.SqlBuilder -> Cal.Data.Sql.SqlBuilderState -> Cal.Core.Prelude.Maybe Cal.Data.Sql.Expr -> Cal.Utilities.PrettyPrinter.Document, buildInsertQueryValuesStatement :: Cal.Data.Sql.SqlBuilder -> Cal.Data.DatabaseMetadata.TableReference -> Cal.Core.Prelude.Maybe [Cal.Core.Prelude.String] -> Cal.Data.Sql.Query -> Cal.Utilities.PrettyPrinter.Document, buildInsertValuesStatement :: Cal.Data.Sql.SqlBuilder -> Cal.Data.DatabaseMetadata.TableReference -> Cal.Core.Prelude.Maybe [Cal.Core.Prelude.String] -> [Cal.Data.Sql.Expr] -> Cal.Utilities.PrettyPrinter.Document, buildOrderByClause :: Cal.Data.Sql.SqlBuilder -> Cal.Data.Sql.SqlBuilderState -> [(Cal.Data.Sql.Expr, Cal.Core.Prelude.Boolean)] -> Cal.Utilities.PrettyPrinter.Document, buildSelectClause :: Cal.Data.Sql.SqlBuilder -> Cal.Data.Sql.SqlBuilderState -> [Cal.Data.Sql.QueryOption] -> [(Cal.Data.Sql.Expr, Cal.Core.Prelude.String)] -> Cal.Utilities.PrettyPrinter.Document, buildTableAndAliasText :: Cal.Data.Sql.SqlBuilder -> Cal.Data.Sql.SqlBuilderState -> Cal.Data.Sql.QueryTable -> Cal.Utilities.PrettyPrinter.Document, buildUpdateValuesStatement :: Cal.Data.Sql.SqlBuilder -> Cal.Data.Sql.SqlBuilderState -> Cal.Data.DatabaseMetadata.TableReference -> [Cal.Core.Prelude.String] -> [Cal.Data.Sql.Expr] -> Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Boolean -> Cal.Utilities.PrettyPrinter.Document, buildWhereClause :: Cal.Data.Sql.SqlBuilder -> Cal.Data.Sql.SqlBuilderState -> Cal.Core.Prelude.Maybe Cal.Data.Sql.Expr -> [Cal.Data.Sql.JoinNode] -> Cal.Utilities.PrettyPrinter.Document, constructQuery :: Cal.Data.Sql.SqlBuilder -> Cal.Data.Sql.SqlBuilderState -> Cal.Utilities.PrettyPrinter.Document -> Cal.Utilities.PrettyPrinter.Document -> Cal.Utilities.PrettyPrinter.Document -> Cal.Utilities.PrettyPrinter.Document -> Cal.Utilities.PrettyPrinter.Document -> Cal.Utilities.PrettyPrinter.Document -> Cal.Utilities.PrettyPrinter.Document, constructUnionQuery :: Cal.Data.Sql.SqlBuilder -> Cal.Data.Sql.SqlBuilderState -> Cal.Utilities.PrettyPrinter.Document -> Cal.Utilities.PrettyPrinter.Document -> Cal.Core.Prelude.Boolean -> Cal.Utilities.PrettyPrinter.Document, doubleToSql :: Cal.Core.Prelude.Double -> Cal.Utilities.PrettyPrinter.Document, functionName :: Cal.Data.Sql.DbFunction -> Cal.Core.Prelude.String, functionToSql :: Cal.Data.Sql.SqlBuilder -> Cal.Data.Sql.SqlBuilderState -> Cal.Data.Sql.DbFunction -> [Cal.Data.Sql.Expr] -> Cal.Utilities.PrettyPrinter.Document, intToSql :: Cal.Core.Prelude.Int -> Cal.Utilities.PrettyPrinter.Document, listToSql :: Cal.Data.Sql.SqlBuilder -> Cal.Data.Sql.SqlBuilderState -> [Cal.Data.Sql.Expr] -> Cal.Utilities.PrettyPrinter.Document, makeSafeName :: Cal.Core.Prelude.String -> Cal.Core.Prelude.String, nullToSql :: Cal.Utilities.PrettyPrinter.Document, operatorText :: Cal.Data.Sql.DbFunction -> Cal.Core.Prelude.String, parameterToSql :: Cal.Data.Sql.SqlBuilder -> Cal.Data.Sql.Parameter -> Cal.Utilities.PrettyPrinter.Document, prepareQuery :: Cal.Data.Sql.SqlBuilderState -> Cal.Data.Sql.Query -> Cal.Data.Sql.Query, quoteIdentifier :: Cal.Core.Prelude.String -> Cal.Utilities.PrettyPrinter.Document, stringToSql :: Cal.Core.Prelude.String -> Cal.Utilities.PrettyPrinter.Document, timeIntervalToSql :: Cal.Data.Sql.TimeInterval -> Cal.Utilities.PrettyPrinter.Document, timeToSql :: Cal.Utilities.Time.Time -> Cal.Utilities.TimeZone.TimeZone -> Cal.Utilities.PrettyPrinter.Document}</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.SqlBuilder</code>) 
		 */
		public static final SourceModel.Expr makeSqlBuilder(SourceModel.Expr sqlBuilderFns) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeSqlBuilder), sqlBuilderFns});
		}

		/**
		 * Name binding for function: makeSqlBuilder.
		 * @see #makeSqlBuilder(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeSqlBuilder = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "makeSqlBuilder");

		/**
		 * Constructs a subquery table using the specified query and alias.
		 * This function will remove any ordering from the subquery (unless it is a <code>Cal.Data.Sql.TopN</code>
		 * query).
		 * @param subquery (CAL type: <code>Cal.Data.Sql.Query</code>)
		 *          a database query to be treated as a query table
		 * @param tableAlias (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the alias to use for the table in the query;
		 * If an empty alias is specified, then a default one will be generated.
		 * @return (CAL type: <code>Cal.Data.Sql.QueryTable</code>) 
		 *          a query table for the nested SELECT query with the given alias
		 */
		public static final SourceModel.Expr makeSubQueryTable(SourceModel.Expr subquery, SourceModel.Expr tableAlias) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeSubQueryTable), subquery, tableAlias});
		}

		/**
		 * @see #makeSubQueryTable(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param subquery
		 * @param tableAlias
		 * @return the SourceModel.Expr representing an application of makeSubQueryTable
		 */
		public static final SourceModel.Expr makeSubQueryTable(SourceModel.Expr subquery, java.lang.String tableAlias) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeSubQueryTable), subquery, SourceModel.Expr.makeStringValue(tableAlias)});
		}

		/**
		 * Name binding for function: makeSubQueryTable.
		 * @see #makeSubQueryTable(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeSubQueryTable = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "makeSubQueryTable");

		/**
		 * Returns an updated version of the name list where each name is unique.
		 * The name comparisons are not case sensitive.
		 * The names will be made unique by appending an integer to the end of the name.
		 * <p>
		 * TODO: perhaps this should take into account the maximum length for various
		 * names for the database...
		 * 
		 * @param namesAlreadyInUse (CAL type: <code>[Cal.Core.Prelude.String]</code>)
		 * @param names (CAL type: <code>[Cal.Core.Prelude.String]</code>)
		 * @return (CAL type: <code>[Cal.Core.Prelude.String]</code>) 
		 */
		public static final SourceModel.Expr makeUniqueNames(SourceModel.Expr namesAlreadyInUse, SourceModel.Expr names) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeUniqueNames), namesAlreadyInUse, names});
		}

		/**
		 * Name binding for function: makeUniqueNames.
		 * @see #makeUniqueNames(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeUniqueNames = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "makeUniqueNames");

		/**
		 * Helper binding method for function: makeUpdateValuesStatement. 
		 * @param tableRef
		 * @param destColumns
		 * @param newValues
		 * @param condition
		 * @return the SourceModule.expr representing an application of makeUpdateValuesStatement
		 */
		public static final SourceModel.Expr makeUpdateValuesStatement(SourceModel.Expr tableRef, SourceModel.Expr destColumns, SourceModel.Expr newValues, SourceModel.Expr condition) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeUpdateValuesStatement), tableRef, destColumns, newValues, condition});
		}

		/**
		 * Name binding for function: makeUpdateValuesStatement.
		 * @see #makeUpdateValuesStatement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeUpdateValuesStatement = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "makeUpdateValuesStatement");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "maxExpr");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "minExpr");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "minuteExpr");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "modExpr");

		/**
		 * Applies the specified query modification function to the query and any
		 * subqueries that it contains.
		 * @param updateQueryFn (CAL type: <code>Cal.Data.Sql.Query -> Cal.Data.Sql.Query</code>)
		 * @param query (CAL type: <code>Cal.Data.Sql.Query</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.Query</code>) 
		 */
		public static final SourceModel.Expr modifyQueries(SourceModel.Expr updateQueryFn, SourceModel.Expr query) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.modifyQueries), updateQueryFn, query});
		}

		/**
		 * Name binding for function: modifyQueries.
		 * @see #modifyQueries(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName modifyQueries = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "modifyQueries");

		/**
		 * Applies the specified table modification function to all tables in the expression.
		 * @param updateTableFn (CAL type: <code>Cal.Data.Sql.QueryTable -> Cal.Data.Sql.QueryTable</code>)
		 * @param expr (CAL type: <code>Cal.Data.Sql.Expr</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.Expr</code>) 
		 */
		public static final SourceModel.Expr modifyTablesInExpr(SourceModel.Expr updateTableFn, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.modifyTablesInExpr), updateTableFn, expr});
		}

		/**
		 * Name binding for function: modifyTablesInExpr.
		 * @see #modifyTablesInExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName modifyTablesInExpr = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "modifyTablesInExpr");

		/**
		 * Applies the specified table modification function to all tables in the join node.
		 * @param updateTableFn (CAL type: <code>Cal.Data.Sql.QueryTable -> Cal.Data.Sql.QueryTable</code>)
		 * @param join (CAL type: <code>Cal.Data.Sql.JoinNode</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.JoinNode</code>) 
		 */
		public static final SourceModel.Expr modifyTablesInJoinNode(SourceModel.Expr updateTableFn, SourceModel.Expr join) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.modifyTablesInJoinNode), updateTableFn, join});
		}

		/**
		 * Name binding for function: modifyTablesInJoinNode.
		 * @see #modifyTablesInJoinNode(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName modifyTablesInJoinNode = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "modifyTablesInJoinNode");

		/**
		 * Applies the specified table modification function to all tables in the join set.
		 * @param updateTableFn (CAL type: <code>Cal.Data.Sql.QueryTable -> Cal.Data.Sql.QueryTable</code>)
		 * @param joinSet (CAL type: <code>Cal.Data.Sql.JoinSet</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.JoinSet</code>) 
		 */
		public static final SourceModel.Expr modifyTablesInJoinSet(SourceModel.Expr updateTableFn, SourceModel.Expr joinSet) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.modifyTablesInJoinSet), updateTableFn, joinSet});
		}

		/**
		 * Name binding for function: modifyTablesInJoinSet.
		 * @see #modifyTablesInJoinSet(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName modifyTablesInJoinSet = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "modifyTablesInJoinSet");

		/**
		 * Applies the specified table modification function to all tables in the query.
		 * @param updateTableFn (CAL type: <code>Cal.Data.Sql.QueryTable -> Cal.Data.Sql.QueryTable</code>)
		 * @param query (CAL type: <code>Cal.Data.Sql.Query</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.Query</code>) 
		 */
		public static final SourceModel.Expr modifyTablesInQuery(SourceModel.Expr updateTableFn, SourceModel.Expr query) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.modifyTablesInQuery), updateTableFn, query});
		}

		/**
		 * Name binding for function: modifyTablesInQuery.
		 * @see #modifyTablesInQuery(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName modifyTablesInQuery = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "modifyTablesInQuery");

		/**
		 * Applies the specified table modification function to all tables in the typed expression.
		 * @param updateTableFn (CAL type: <code>Cal.Data.Sql.QueryTable -> Cal.Data.Sql.QueryTable</code>)
		 * @param typedExpr (CAL type: <code>Cal.Data.Sql.TypedExpr a</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.TypedExpr a</code>) 
		 */
		public static final SourceModel.Expr modifyTablesInTypedExpr(SourceModel.Expr updateTableFn, SourceModel.Expr typedExpr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.modifyTablesInTypedExpr), updateTableFn, typedExpr});
		}

		/**
		 * Name binding for function: modifyTablesInTypedExpr.
		 * @see #modifyTablesInTypedExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName modifyTablesInTypedExpr = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "modifyTablesInTypedExpr");

		/**
		 * Constructs a database expression to return the modulus of two numeric operand expressions.
		 * @param expr1 (CAL type: <code>Cal.Core.Prelude.Num a => Cal.Data.Sql.TypedExpr a</code>)
		 *          a numeric database expression for the first operand
		 * @param expr2 (CAL type: <code>Cal.Core.Prelude.Num a => Cal.Data.Sql.TypedExpr a</code>)
		 *          a numeric database expression for the second operand
		 * @return (CAL type: <code>Cal.Core.Prelude.Num a => Cal.Data.Sql.TypedExpr a</code>) 
		 *          a numeric database expression to return the modulus of the two numeric operand expressions
		 */
		public static final SourceModel.Expr modulusExpr(SourceModel.Expr expr1, SourceModel.Expr expr2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.modulusExpr), expr1, expr2});
		}

		/**
		 * Name binding for function: modulusExpr.
		 * @see #modulusExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName modulusExpr = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "modulusExpr");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "monthExpr");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "monthNameExpr");

		/**
		 * Constructs a database expression to multiply two numeric operand expressions.
		 * @param expr1 (CAL type: <code>Cal.Core.Prelude.Num a => Cal.Data.Sql.TypedExpr a</code>)
		 *          a numeric database expression for the first operand
		 * @param expr2 (CAL type: <code>Cal.Core.Prelude.Num a => Cal.Data.Sql.TypedExpr a</code>)
		 *          a numeric database expression for the second operand
		 * @return (CAL type: <code>Cal.Core.Prelude.Num a => Cal.Data.Sql.TypedExpr a</code>) 
		 *          a numeric database expression to multiply the two numeric operand expressions
		 */
		public static final SourceModel.Expr multiplyExpr(SourceModel.Expr expr1, SourceModel.Expr expr2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.multiplyExpr), expr1, expr2});
		}

		/**
		 * Name binding for function: multiplyExpr.
		 * @see #multiplyExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName multiplyExpr = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "multiplyExpr");

		/**
		 * Constructs a database expression for the negation of the numeric argument expression.
		 * @param numExpr (CAL type: <code>Cal.Core.Prelude.Num a => Cal.Data.Sql.TypedExpr a</code>)
		 *          a numeric database expression
		 * @return (CAL type: <code>Cal.Core.Prelude.Num a => Cal.Data.Sql.TypedExpr a</code>) 
		 *          a numeric database expression for the negation of the argument expression
		 */
		public static final SourceModel.Expr negateExpr(SourceModel.Expr numExpr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.negateExpr), numExpr});
		}

		/**
		 * Name binding for function: negateExpr.
		 * @see #negateExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName negateExpr = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "negateExpr");

		/**
		 * Creates a new, empty query.
		 * @return (CAL type: <code>Cal.Data.Sql.Query</code>) 
		 */
		public static final SourceModel.Expr newQuery() {
			return SourceModel.Expr.Var.make(Functions.newQuery);
		}

		/**
		 * Name binding for function: newQuery.
		 * @see #newQuery()
		 */
		public static final QualifiedName newQuery = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "newQuery");

		/**
		 * Constructs a database expression which tests two operand expressions for inequality.
		 * @param expr1 (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Data.Sql.TypedExpr a</code>)
		 *          a database expression for the first operand
		 * @param expr2 (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Data.Sql.TypedExpr a</code>)
		 *          a database expression for the second operand
		 * @return (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Boolean</code>) 
		 *          a Boolean database expression to test the operand expressions for inequality
		 */
		public static final SourceModel.Expr notEqExpr(SourceModel.Expr expr1, SourceModel.Expr expr2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.notEqExpr), expr1, expr2});
		}

		/**
		 * Name binding for function: notEqExpr.
		 * @see #notEqExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName notEqExpr = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "notEqExpr");

		/**
		 * Constructs a database expression for the application of the 'not' operator to a Boolean argument.
		 * @param boolExpr (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Boolean</code>)
		 *          a Boolean database expression
		 * @return (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Boolean</code>) 
		 *          a Boolean database expression for the application of the 'not' operator to the argument expression
		 */
		public static final SourceModel.Expr notExpr(SourceModel.Expr boolExpr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.notExpr), boolExpr});
		}

		/**
		 * Name binding for function: notExpr.
		 * @see #notExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName notExpr = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "notExpr");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "nowExpr");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "nullIfExpr");

		/**
		 * A database expression for a null value of some data type.
		 * @return (CAL type: <code>Cal.Data.Sql.TypedExpr a</code>) 
		 *          a database expression for a null value
		 */
		public static final SourceModel.Expr nullValue() {
			return SourceModel.Expr.Var.make(Functions.nullValue);
		}

		/**
		 * Name binding for function: nullValue.
		 * @see #nullValue()
		 */
		public static final QualifiedName nullValue = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "nullValue");

		/**
		 * Construct a database expression for a numeric literal.
		 * @param numValue (CAL type: <code>Cal.Core.Prelude.Num a => a</code>)
		 *          the numeric literal value
		 * @return (CAL type: <code>Cal.Core.Prelude.Num a => Cal.Data.Sql.TypedExpr a</code>) 
		 *          the database expression for the numeric literal
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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "numericConstant");

		/**
		 * Returns the precedence of the specified operator.
		 * An operation with a higher precendence value will be done first.
		 * @param func (CAL type: <code>Cal.Data.Sql.DbFunction</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 */
		public static final SourceModel.Expr operatorPrecedence(SourceModel.Expr func) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.operatorPrecedence), func});
		}

		/**
		 * Name binding for function: operatorPrecedence.
		 * @see #operatorPrecedence(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName operatorPrecedence = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "operatorPrecedence");

		/**
		 * Returns the text for a SQL operator.
		 * @param sqlBuilder (CAL type: <code>Cal.Data.Sql.SqlBuilder</code>)
		 * @param operator (CAL type: <code>Cal.Data.Sql.DbFunction</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr operatorText(SourceModel.Expr sqlBuilder, SourceModel.Expr operator) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.operatorText), sqlBuilder, operator});
		}

		/**
		 * Name binding for function: operatorText.
		 * @see #operatorText(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName operatorText = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "operatorText");

		/**
		 * Constructs a database expression to test whether either of the operand expressions are True.
		 * @param b1 (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Boolean</code>)
		 *          a database expression for the first operand
		 * @param b2 (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Boolean</code>)
		 *          a database expression for the second operand
		 * @return (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Boolean</code>) 
		 *          a Boolean database expression to test whether either of the operand expressions are True
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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "orExpr");

		/**
		 * Adds sorting on the specified expression.
		 * @param query (CAL type: <code>Cal.Data.Sql.Query</code>)
		 * @param sortExpr (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Data.Sql.TypedExpr a</code>)
		 * @param sortAscending (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.Query</code>) 
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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "order");

		/**
		 * Adds sorting on the specified fields.
		 * @param query (CAL type: <code>Cal.Data.Sql.Query</code>)
		 * @param newOrderings (CAL type: <code>[(Cal.Data.Sql.Expr, Cal.Core.Prelude.Boolean)]</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.Query</code>) 
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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "order2");

		/**
		 * Returns the ordering info for the query.
		 * @param query (CAL type: <code>Cal.Data.Sql.Query</code>)
		 * @return (CAL type: <code>[(Cal.Data.Sql.Expr, Cal.Core.Prelude.Boolean)]</code>) 
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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "orderingExpressions");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "piExpr");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "powerExpr");

		/**
		 * Adds the specified expressions as result columns in the query.
		 * @param query (CAL type: <code>Cal.Data.Sql.Query</code>)
		 * @param newColumns (CAL type: <code>[Cal.Data.Sql.Expr]</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.Query</code>) 
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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "project");

		/**
		 * Adds the specified expression as a result column in the query.
		 * @param query (CAL type: <code>Cal.Data.Sql.Query</code>)
		 * @param newColumn (CAL type: <code>Cal.Data.Sql.TypedExpr a</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.Query</code>) 
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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "projectColumn");

		/**
		 * Adds the specified expression as a result column in the query.
		 * @param query (CAL type: <code>Cal.Data.Sql.Query</code>)
		 * @param newColumn (CAL type: <code>Cal.Data.Sql.TypedExpr a</code>)
		 * @param columnAlias (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.Query</code>) 
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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "projectColumnWithAlias");

		/**
		 * Projects the specified expressions from the query, and group and order (ASC) on the expressions.
		 * @param query (CAL type: <code>Cal.Data.Sql.Query</code>)
		 * @param exprs (CAL type: <code>[Cal.Data.Sql.Expr]</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.Query</code>) 
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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "projectGroupAndOrder");

		/**
		 * Adds the specified expressions as result columns with the corresponding
		 * aliases in the query.
		 * <p>
		 * TODO: don't add the same field multiple times...
		 * 
		 * @param query (CAL type: <code>Cal.Data.Sql.Query</code>)
		 * @param newColumns (CAL type: <code>[(Cal.Data.Sql.Expr, Cal.Core.Prelude.String)]</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.Query</code>) 
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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "projectWithAliases");

		/**
		 * Returns the aliases for the query's projected columns.
		 * @param query (CAL type: <code>Cal.Data.Sql.Query</code>)
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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "projectedColumnAliases");

		/**
		 * Returns the projected columns for the query.
		 * For a <code>Cal.Data.Sql.Union</code> query, this will return only the projected columns for the first
		 * query.
		 * @param query (CAL type: <code>Cal.Data.Sql.Query</code>)
		 * @return (CAL type: <code>[(Cal.Data.Sql.Expr, Cal.Core.Prelude.String)]</code>) 
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
				CAL_Sql.MODULE_NAME, 
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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "quarterExpr");

		/**
		 * Returns the query options.
		 * For <code>Cal.Data.Sql.Union</code> queries, this will return the options for the first query.
		 * @param query (CAL type: <code>Cal.Data.Sql.Query</code>)
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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "queryOptions");

		/**
		 * Generates the SQL text for the specified database query.
		 * @param builder (CAL type: <code>Cal.Data.Sql.SqlBuilder</code>)
		 * @param forDisplay (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 * @param query (CAL type: <code>Cal.Data.Sql.Query</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr queryText(SourceModel.Expr builder, SourceModel.Expr forDisplay, SourceModel.Expr query) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.queryText), builder, forDisplay, query});
		}

		/**
		 * @see #queryText(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param builder
		 * @param forDisplay
		 * @param query
		 * @return the SourceModel.Expr representing an application of queryText
		 */
		public static final SourceModel.Expr queryText(SourceModel.Expr builder, boolean forDisplay, SourceModel.Expr query) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.queryText), builder, SourceModel.Expr.makeBooleanValue(forDisplay), query});
		}

		/**
		 * Name binding for function: queryText.
		 * @see #queryText(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName queryText = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "queryText");

		/**
		 * Generates the SQL text document for the specified database query.
		 * @param builder (CAL type: <code>Cal.Data.Sql.SqlBuilder</code>)
		 * @param query (CAL type: <code>Cal.Data.Sql.Query</code>)
		 * @return (CAL type: <code>Cal.Utilities.PrettyPrinter.Document</code>) 
		 */
		public static final SourceModel.Expr queryTextDocument(SourceModel.Expr builder, SourceModel.Expr query) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.queryTextDocument), builder, query});
		}

		/**
		 * Name binding for function: queryTextDocument.
		 * @see #queryTextDocument(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName queryTextDocument = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "queryTextDocument");

		/**
		 * Adds quotes to a name (such as a table).
		 * @param sqlBuilder (CAL type: <code>Cal.Data.Sql.SqlBuilder</code>)
		 * @param indentifierText (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Utilities.PrettyPrinter.Document</code>) 
		 */
		public static final SourceModel.Expr quoteIdentifier(SourceModel.Expr sqlBuilder, SourceModel.Expr indentifierText) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.quoteIdentifier), sqlBuilder, indentifierText});
		}

		/**
		 * @see #quoteIdentifier(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param sqlBuilder
		 * @param indentifierText
		 * @return the SourceModel.Expr representing an application of quoteIdentifier
		 */
		public static final SourceModel.Expr quoteIdentifier(SourceModel.Expr sqlBuilder, java.lang.String indentifierText) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.quoteIdentifier), sqlBuilder, SourceModel.Expr.makeStringValue(indentifierText)});
		}

		/**
		 * Name binding for function: quoteIdentifier.
		 * @see #quoteIdentifier(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName quoteIdentifier = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "quoteIdentifier");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "radiansExpr");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "randExpr");

		/**
		 * Convert a range of values for a field or expression into the equivalent restriction expression.
		 * @param makeConstantFn (CAL type: <code>Cal.Core.Prelude.Ord a => a -> Cal.Data.Sql.TypedExpr a</code>)
		 *          a function to produce a database constant expression from a value
		 * @param field (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Data.Sql.TypedExpr a</code>)
		 *          the database field or expression to be restricted
		 * @param range (CAL type: <code>Cal.Core.Prelude.Ord a => Cal.Utilities.Range.Range a</code>)
		 *          the range of values to which the field will be restricted
		 * @return (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Boolean</code>) 
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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "rangeToRestrictionExpr");

		/**
		 * Removes the option of the same type as the specified one (even if the exact
		 * values don't match).
		 * @param query (CAL type: <code>Cal.Data.Sql.Query</code>)
		 * @param optionTypeToRemove (CAL type: <code>Cal.Data.Sql.QueryOption</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.Query</code>) 
		 */
		public static final SourceModel.Expr removeOption(SourceModel.Expr query, SourceModel.Expr optionTypeToRemove) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.removeOption), query, optionTypeToRemove});
		}

		/**
		 * Name binding for function: removeOption.
		 * @see #removeOption(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName removeOption = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "removeOption");

		/**
		 * Removes all ordering from the query.
		 * @param query (CAL type: <code>Cal.Data.Sql.Query</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.Query</code>) 
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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "removeOrdering");

		/**
		 * Removes the specified expression from the projected columns list.
		 * For <code>Cal.Data.Sql.Union</code> queries, the specified expression will be removed from the both
		 * queries along with the
		 * corresponding columns in the other query.
		 * @param query (CAL type: <code>Cal.Data.Sql.Query</code>)
		 * @param exprToRemove (CAL type: <code>Cal.Data.Sql.Expr</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.Query</code>) 
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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "removeProjectedColumn");

		/**
		 * Removes all projected columns from the query.
		 * @param query (CAL type: <code>Cal.Data.Sql.Query</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.Query</code>) 
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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "removeProjectedColumns");

		/**
		 * Removed any joins which are including any tables which are otherwise unused
		 * in the query.
		 * An unused table will not be removed if is being used to join other tables
		 * that are used in the query.
		 * @param query (CAL type: <code>Cal.Data.Sql.Query</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.Query</code>) 
		 */
		public static final SourceModel.Expr removeUnusedTables(SourceModel.Expr query) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.removeUnusedTables), query});
		}

		/**
		 * Name binding for function: removeUnusedTables.
		 * @see #removeUnusedTables(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName removeUnusedTables = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "removeUnusedTables");

		/**
		 * Removed any joins which are including any tables which are otherwise unused
		 * in the query.
		 * An unused table will not be removed if is being used to join other tables
		 * that are used in the query.
		 * Tables used by the anchorFields expressions will not be removed either.
		 * @param anchorFields (CAL type: <code>[Cal.Data.Sql.Expr]</code>)
		 * @param query (CAL type: <code>Cal.Data.Sql.Query</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.Query</code>) 
		 */
		public static final SourceModel.Expr removeUnusedTables2(SourceModel.Expr anchorFields, SourceModel.Expr query) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.removeUnusedTables2), anchorFields, query});
		}

		/**
		 * Name binding for function: removeUnusedTables2.
		 * @see #removeUnusedTables2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName removeUnusedTables2 = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "removeUnusedTables2");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "repeatExpr");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "replaceExpr");

		/**
		 * Adds a restriction on the rows returned by the query.
		 * @param query (CAL type: <code>Cal.Data.Sql.Query</code>)
		 * @param newRestriction (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Boolean</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.Query</code>) 
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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "restrict");

		/**
		 * Adds the specified restrictions on the rows returned by the query.
		 * @param query (CAL type: <code>Cal.Data.Sql.Query</code>)
		 * @param newRestrictions (CAL type: <code>[Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Boolean]</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.Query</code>) 
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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "restrict2");

		/**
		 * Returns the restriction expressions (including group restrictions) for the
		 * query.
		 * For a <code>Cal.Data.Sql.Union</code> query, this only returns the restrictions for the first query.
		 * @param query (CAL type: <code>Cal.Data.Sql.Query</code>)
		 * @return (CAL type: <code>[Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Boolean]</code>) 
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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "restrictionExpressions");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "rightExpr");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "roundExpr");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "rtrimExpr");

		/**
		 * Construct a database expression for a searched case statement.
		 * Each WHEN clause will contain a Boolean expression and a result value.
		 * The result will be the result value for the first WHEN clause with a condition that returns True.
		 * If none of the WHEN clause expressions return True, then the ELSE value will be returned.
		 * @param whenConditionAndResults (CAL type: <code>[(Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Boolean, Cal.Data.Sql.TypedExpr b)]</code>)
		 *          a list of pairs of Boolean condition expressions and result values
		 * @param elseValue (CAL type: <code>Cal.Core.Prelude.Maybe (Cal.Data.Sql.TypedExpr b)</code>)
		 *          a database expression for the value to be returned if none of the WHEN clauses evaluate to True
		 * @return (CAL type: <code>Cal.Data.Sql.TypedExpr b</code>) 
		 *          a database expression for a searched case statement
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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "searchedCaseExpr");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "secondExpr");

		/**
		 * Sets the aliases for the first N projected columns.
		 * Any other existing aliases will be left untouched.
		 * @param query (CAL type: <code>Cal.Data.Sql.Query</code>)
		 * @param newAliases (CAL type: <code>[Cal.Core.Prelude.String]</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.Query</code>) 
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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "setColumnAliases");

		/**
		 * Sets the join information for the query as a list of join node trees.
		 * This will replace any existing join info.
		 * @param query (CAL type: <code>Cal.Data.Sql.Query</code>)
		 * @param newJoins (CAL type: <code>[Cal.Data.Sql.JoinNode]</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.Query</code>) 
		 */
		public static final SourceModel.Expr setJoins(SourceModel.Expr query, SourceModel.Expr newJoins) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.setJoins), query, newJoins});
		}

		/**
		 * Name binding for function: setJoins.
		 * @see #setJoins(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName setJoins = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "setJoins");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "signExpr");

		/**
		 * Constructs a database expression for a simple case statement.
		 * When this expression is evaluated, the result value of the matching WHEN clause will be returned.
		 * If none of the WHEN values match the case value, then the ELSE value will be returned.
		 * @param caseExpr (CAL type: <code>Cal.Core.Prelude.Eq a => Cal.Data.Sql.TypedExpr a</code>)
		 *          a database expression for the value to be tested
		 * @param whenValueAndResults (CAL type: <code>Cal.Core.Prelude.Eq a => [(Cal.Data.Sql.TypedExpr a, Cal.Data.Sql.TypedExpr b)]</code>)
		 *          a list of pairs of comparison values and the corresponding result values
		 * @param elseValue (CAL type: <code>Cal.Core.Prelude.Maybe (Cal.Data.Sql.TypedExpr b)</code>)
		 *          a database expression for the value to be returned if none of the WHEN clauses match
		 * @return (CAL type: <code>Cal.Data.Sql.TypedExpr b</code>) 
		 *          a database expression for a simple case statement
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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "simpleCaseExpr");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "sinExpr");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "soundexExpr");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "spaceExpr");

		/**
		 * Breaks a compound statement into multiple compound statements with a smaller
		 * statement count.  This function assumes that it is not possible to create
		 * nested compound statements.
		 * @param stmt (CAL type: <code>Cal.Data.Sql.Statement</code>)
		 * @param maxCount (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @return (CAL type: <code>[Cal.Data.Sql.Statement]</code>) 
		 */
		public static final SourceModel.Expr splitCompoundStatement(SourceModel.Expr stmt, SourceModel.Expr maxCount) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.splitCompoundStatement), stmt, maxCount});
		}

		/**
		 * @see #splitCompoundStatement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param stmt
		 * @param maxCount
		 * @return the SourceModel.Expr representing an application of splitCompoundStatement
		 */
		public static final SourceModel.Expr splitCompoundStatement(SourceModel.Expr stmt, int maxCount) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.splitCompoundStatement), stmt, SourceModel.Expr.makeIntValue(maxCount)});
		}

		/**
		 * Name binding for function: splitCompoundStatement.
		 * @see #splitCompoundStatement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName splitCompoundStatement = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "splitCompoundStatement");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "sqrtExpr");

		/**
		 * Generates the SQL text for a statement.
		 * @param builder (CAL type: <code>Cal.Data.Sql.SqlBuilder</code>)
		 * @param forDisplay (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 * @param statement (CAL type: <code>Cal.Data.Sql.Statement</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr statementText(SourceModel.Expr builder, SourceModel.Expr forDisplay, SourceModel.Expr statement) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.statementText), builder, forDisplay, statement});
		}

		/**
		 * @see #statementText(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param builder
		 * @param forDisplay
		 * @param statement
		 * @return the SourceModel.Expr representing an application of statementText
		 */
		public static final SourceModel.Expr statementText(SourceModel.Expr builder, boolean forDisplay, SourceModel.Expr statement) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.statementText), builder, SourceModel.Expr.makeBooleanValue(forDisplay), statement});
		}

		/**
		 * Name binding for function: statementText.
		 * @see #statementText(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName statementText = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "statementText");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "stdDevExpr");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "stdDevPExpr");

		/**
		 * Construct a database expression for a string literal.
		 * @param strValue (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the string literal value
		 * @return (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.String</code>) 
		 *          the database expression for the string literal
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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "stringConstant");

		/**
		 * Helper binding method for function: stringField. 
		 * @param table
		 * @param fieldName
		 * @return the SourceModule.expr representing an application of stringField
		 */
		public static final SourceModel.Expr stringField(SourceModel.Expr table, SourceModel.Expr fieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.stringField), table, fieldName});
		}

		/**
		 * @see #stringField(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param table
		 * @param fieldName
		 * @return the SourceModel.Expr representing an application of stringField
		 */
		public static final SourceModel.Expr stringField(SourceModel.Expr table, java.lang.String fieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.stringField), table, SourceModel.Expr.makeStringValue(fieldName)});
		}

		/**
		 * Name binding for function: stringField.
		 * @see #stringField(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName stringField = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "stringField");

		/**
		 * Constructs an string expression for a database parameter.
		 * If Nothing is specified for the name, then the parameter will be an unnamed one.
		 * @param maybeParamName (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Core.Prelude.String</code>)
		 *          an optional name for the parameter
		 * @return (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.String</code>) 
		 *          a string database expression for a database parameter
		 */
		public static final SourceModel.Expr stringParameter(SourceModel.Expr maybeParamName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.stringParameter), maybeParamName});
		}

		/**
		 * Name binding for function: stringParameter.
		 * @see #stringParameter(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName stringParameter = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "stringParameter");

		/**
		 * Creates a database expression based on a subquery.
		 * Any ordering will be removed from the subquery (unless it uses <code>Cal.Data.Sql.TopN</code>).
		 * The subquery must have a single projected column.
		 * @param subquery (CAL type: <code>Cal.Data.Sql.Query</code>)
		 *          the subquery to be treated as a database expression
		 * @return (CAL type: <code>Cal.Data.Sql.Expr</code>) 
		 *          a database expression based on the subquery
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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "subQueryExpr");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "substringExpr");

		/**
		 * Constructs a database expression to subtract two numeric operand expressions.
		 * @param expr1 (CAL type: <code>Cal.Core.Prelude.Num a => Cal.Data.Sql.TypedExpr a</code>)
		 *          a numeric database expression for the first operand
		 * @param expr2 (CAL type: <code>Cal.Core.Prelude.Num a => Cal.Data.Sql.TypedExpr a</code>)
		 *          a numeric database expression for the second operand
		 * @return (CAL type: <code>Cal.Core.Prelude.Num a => Cal.Data.Sql.TypedExpr a</code>) 
		 *          a numeric database expression to subtract the two numeric operand expressions
		 */
		public static final SourceModel.Expr subtractExpr(SourceModel.Expr expr1, SourceModel.Expr expr2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.subtractExpr), expr1, expr2});
		}

		/**
		 * Name binding for function: subtractExpr.
		 * @see #subtractExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName subtractExpr = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "subtractExpr");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "sumExpr");

		/**
		 * Builds the query text for a table name (including quotes where needed).
		 * @param builder (CAL type: <code>Cal.Data.Sql.SqlBuilder</code>)
		 * @param builderState (CAL type: <code>Cal.Data.Sql.SqlBuilderState</code>)
		 * @param queryTable (CAL type: <code>Cal.Data.Sql.QueryTable</code>)
		 * @return (CAL type: <code>Cal.Utilities.PrettyPrinter.Document</code>) 
		 */
		public static final SourceModel.Expr tableNameText(SourceModel.Expr builder, SourceModel.Expr builderState, SourceModel.Expr queryTable) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.tableNameText), builder, builderState, queryTable});
		}

		/**
		 * Name binding for function: tableNameText.
		 * @see #tableNameText(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName tableNameText = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "tableNameText");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "tanExpr");

		/**
		 * Construct a database expression for a time literal.
		 * @param timeValue (CAL type: <code>Cal.Utilities.Time.Time</code>)
		 *          the time literal value
		 * @return (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Utilities.Time.Time</code>) 
		 *          the database expression for the time literal
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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "timeConstant");

		/**
		 * Helper binding method for function: timeField. 
		 * @param table
		 * @param fieldName
		 * @return the SourceModule.expr representing an application of timeField
		 */
		public static final SourceModel.Expr timeField(SourceModel.Expr table, SourceModel.Expr fieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.timeField), table, fieldName});
		}

		/**
		 * @see #timeField(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param table
		 * @param fieldName
		 * @return the SourceModel.Expr representing an application of timeField
		 */
		public static final SourceModel.Expr timeField(SourceModel.Expr table, java.lang.String fieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.timeField), table, SourceModel.Expr.makeStringValue(fieldName)});
		}

		/**
		 * Name binding for function: timeField.
		 * @see #timeField(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName timeField = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "timeField");

		/**
		 * Builds the SQL text for a time interval constant.
		 * @param sqlBuilder (CAL type: <code>Cal.Data.Sql.SqlBuilder</code>)
		 * @param timeInterval (CAL type: <code>Cal.Data.Sql.TimeInterval</code>)
		 * @return (CAL type: <code>Cal.Utilities.PrettyPrinter.Document</code>) 
		 */
		public static final SourceModel.Expr timeIntervalToSql(SourceModel.Expr sqlBuilder, SourceModel.Expr timeInterval) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.timeIntervalToSql), sqlBuilder, timeInterval});
		}

		/**
		 * Name binding for function: timeIntervalToSql.
		 * @see #timeIntervalToSql(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName timeIntervalToSql = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "timeIntervalToSql");

		/**
		 * Constructs an time expression for a database parameter.
		 * If Nothing is specified for the name, then the parameter will be an unnamed one.
		 * @param maybeParamName (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Core.Prelude.String</code>)
		 *          an optional name for the parameter
		 * @return (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Utilities.Time.Time</code>) 
		 *          a time database expression for a database parameter
		 */
		public static final SourceModel.Expr timeParameter(SourceModel.Expr maybeParamName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.timeParameter), maybeParamName});
		}

		/**
		 * Name binding for function: timeParameter.
		 * @see #timeParameter(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName timeParameter = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "timeParameter");

		/**
		 * Returns a typed expression from the untyped expression.
		 * @param untypedExpr (CAL type: <code>Cal.Data.Sql.Expr</code>)
		 *          an untyped database expression
		 * @return (CAL type: <code>Cal.Data.Sql.TypedExpr a</code>) 
		 *          the typed database expression
		 */
		public static final SourceModel.Expr toTypedExpr(SourceModel.Expr untypedExpr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toTypedExpr), untypedExpr});
		}

		/**
		 * Name binding for function: toTypedExpr.
		 * @see #toTypedExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName toTypedExpr = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "toTypedExpr");

		/**
		 * Returns the untyped expression from a typed expression.
		 * @param typedExpr (CAL type: <code>Cal.Data.Sql.TypedExpr a</code>)
		 *          a typed database expression
		 * @return (CAL type: <code>Cal.Data.Sql.Expr</code>) 
		 *          the untyped database expression
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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "toUntypedExpr");

		/**
		 * The <code>transformQueryRestrictionExpr</code> takes a conversion function and executes it against 
		 * the restriction expression of a query.
		 * @param conversionFunction (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Boolean -> Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Boolean</code>)
		 * @param query (CAL type: <code>Cal.Data.Sql.Query</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.Query</code>) 
		 *          transformed query
		 */
		public static final SourceModel.Expr transformQueryRestrictionExpr(SourceModel.Expr conversionFunction, SourceModel.Expr query) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformQueryRestrictionExpr), conversionFunction, query});
		}

		/**
		 * Name binding for function: transformQueryRestrictionExpr.
		 * @see #transformQueryRestrictionExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformQueryRestrictionExpr = 
			QualifiedName.make(
				CAL_Sql.MODULE_NAME, 
				"transformQueryRestrictionExpr");

		/**
		 * A database expression for the Boolean value True.
		 * @return (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Boolean</code>) 
		 *          the database expression for the Boolean value True
		 */
		public static final SourceModel.Expr trueConstant() {
			return SourceModel.Expr.Var.make(Functions.trueConstant);
		}

		/**
		 * Name binding for function: trueConstant.
		 * @see #trueConstant()
		 */
		public static final QualifiedName trueConstant = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "trueConstant");

		/**
		 * TODO: is this the correct signature?
		 * @param arg_1 (CAL type: <code>Cal.Core.Prelude.Num a => Cal.Data.Sql.TypedExpr a</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Int</code>) 
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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "truncateExpr");

		/**
		 * Returns the type of the values represented by this typed expression.
		 * @param expr (CAL type: <code>Cal.Core.Prelude.Typeable a => Cal.Data.Sql.TypedExpr a</code>)
		 *          a database expression
		 * @return (CAL type: <code>Cal.Core.Prelude.TypeRep</code>) 
		 *          the type of the database expression
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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "typeOfExpr");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "ucaseExpr");

		/**
		 * Combines the 2 queries into a Union query.
		 * @param query1 (CAL type: <code>Cal.Data.Sql.Query</code>)
		 * @param query2 (CAL type: <code>Cal.Data.Sql.Query</code>)
		 * @param unionAll (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.Query</code>) 
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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "unionQuery");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "untypedAggregationExpr");

		/**
		 * Constructs an untyped database expression for the application of a binary operator.
		 * No checking will be done for the types of the arguments.
		 * Where possible the appropriate type-safe function should be called to construct the 
		 * function expression.
		 * @param func (CAL type: <code>Cal.Data.Sql.DbFunction</code>)
		 *          an identifier for a binary operator
		 * @param leftArgument (CAL type: <code>Cal.Data.Sql.Expr</code>)
		 *          a database expression for the left argument to which the operator will be applied
		 * @param rightArgument (CAL type: <code>Cal.Data.Sql.Expr</code>)
		 *          a database expression for the right argument to which the operator will be applied
		 * @return (CAL type: <code>Cal.Data.Sql.Expr</code>) 
		 *          an untyped database expression for the application of the binary operator
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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "untypedBinaryExpr");

		/**
		 * Returns a query field for the specified table.
		 * @param table (CAL type: <code>Cal.Data.Sql.QueryTable</code>)
		 * @param fieldName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.Expr</code>) 
		 */
		public static final SourceModel.Expr untypedField(SourceModel.Expr table, SourceModel.Expr fieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.untypedField), table, fieldName});
		}

		/**
		 * @see #untypedField(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param table
		 * @param fieldName
		 * @return the SourceModel.Expr representing an application of untypedField
		 */
		public static final SourceModel.Expr untypedField(SourceModel.Expr table, java.lang.String fieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.untypedField), table, SourceModel.Expr.makeStringValue(fieldName)});
		}

		/**
		 * Name binding for function: untypedField.
		 * @see #untypedField(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName untypedField = 
			QualifiedName.make(CAL_Sql.MODULE_NAME, "untypedField");

		/**
		 * Constructs an untyped database expression for a call to a database function.
		 * No checking will be done for the number or types of the arguments.
		 * Where possible the appropriate type-safe function should be called to construct the 
		 * function expression.
		 * @param func (CAL type: <code>Cal.Data.Sql.DbFunction</code>)
		 *          an identifier for a database function
		 * @param arguments (CAL type: <code>[Cal.Data.Sql.Expr]</code>)
		 *          a list of database expressions for the function arguments
		 * @return (CAL type: <code>Cal.Data.Sql.Expr</code>) 
		 *          an untyped database expression for a call to the database function
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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "untypedFunctionExpr");

		/**
		 * Constructs an untyped database expression for a list of values.
		 * @param listValues (CAL type: <code>[Cal.Data.Sql.Expr]</code>)
		 *          a list of database expressions for the list value
		 * @return (CAL type: <code>Cal.Data.Sql.Expr</code>) 
		 *          an untyped database expression for the list of values
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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "untypedListExpr");

		/**
		 * Constructs an untyped database expression for the application of a unary operator.
		 * No checking will be done for the type of the argument.
		 * Where possible the appropriate type-safe function should be called to construct the 
		 * function expression.
		 * @param func (CAL type: <code>Cal.Data.Sql.DbFunction</code>)
		 *          an identifier for a unary operator
		 * @param argument (CAL type: <code>Cal.Data.Sql.Expr</code>)
		 *          a database expression to which the unary operator will be applied
		 * @return (CAL type: <code>Cal.Data.Sql.Expr</code>) 
		 *          an untyped database expression for the application of the unary operator
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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "untypedUnaryExpr");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "userExpr");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "varianceExpr");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "variancePExpr");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "weekExpr");

		/**
		 * Wraps the query in an outer query which projects the same columns as the
		 * original.
		 * The ordering from the original query is also preserved.
		 * @param query (CAL type: <code>Cal.Data.Sql.Query</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.Query</code>) 
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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "wrapQuery");

		/**
		 * Wraps the query in an outer query which projects the same columns as the
		 * original.
		 * The ordering from the original query is also preserved.
		 * The wrapped query will be returned along with wrapped versions of the
		 * specified additional expressions.
		 * @param query (CAL type: <code>Cal.Data.Sql.Query</code>)
		 * @param additionalExprs (CAL type: <code>[Cal.Data.Sql.Expr]</code>)
		 * @return (CAL type: <code>(Cal.Data.Sql.Query, [Cal.Data.Sql.Expr])</code>) 
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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "wrapQuery2");

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
			QualifiedName.make(CAL_Sql.MODULE_NAME, "yearExpr");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = 1231102455;

}
