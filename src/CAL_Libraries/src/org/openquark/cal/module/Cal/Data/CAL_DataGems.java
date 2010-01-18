/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_DataGems.java)
 * was generated from CAL module: Cal.Data.DataGems.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Data.DataGems module from Java code.
 *  
 * Creation date: Thu Aug 23 12:30:29 PDT 2007
 * --!>
 *  
 */

package org.openquark.cal.module.Cal.Data;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * DataGems provide a way to access database metadata, as well as query and modify database tables.
 * <p>
 * The JDBCConnection type allows access to a database through JDBC.
 * <p>
 * The ResultSet type is a foreign type which corresponds to a generic Java QueryResult interface.
 * There is an implementation of this interface for JDBC resultsets, but other implementations are also possible.
 * The Resultset functions in this module will work with any QueryResult implementation.
 * <p>
 * Due to stateful nature of a resultset (JDBC or otherwise), access to the resultset values must be done
 * through high-level functions (such as dataFromResultSet) which take care of managing the resultset position and extracting values in 
 * the correct order.
 * <p>
 * In the future, when we have generics/records in the typesystem, we'll be able to be much smarter about 
 * the relationship between the extractors, result column types and combiners.  Arbitrary recombined Tuple-ns
 * will be possible by applying a set of extractors to a record.  For now, there is a disconnect between the 
 * record and the extractors.  You have to know 'implicitly' what the column types will be in a result set
 * and what their column positions are, then use the right extractors with column names/numbers to get at 
 * the data values in a result set.  JDBC allows a little leeway in that it will actually perform value
 * transformations between in the actual column type and the requested type (the extractor).  This works in
 * most expected situations (which is nice), but it doesn't really absolve you from knowing what the data is!
 */
public final class CAL_DataGems {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Data.DataGems");

	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Data.DataGems module.
	 */
	public static final class TypeConstructors {
		/**
		 * A JDBC database connection.
		 * This can be used to access the database metadata and to query or update the database tables.
		 */
		public static final QualifiedName JDBCConnection = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "JDBCConnection");

		/**
		 * A statement that can be used to execute select or update queries with parameters.
		 */
		public static final QualifiedName JDBCPreparedStatement = 
			QualifiedName.make(
				CAL_DataGems.MODULE_NAME, 
				"JDBCPreparedStatement");

		/**
		 * A JDBC resultset.
		 * This can be exposed at a <code>Cal.Data.DataGems.ResultSet</code> by calling <code>Cal.Data.DataGems.resultSetFromJDBC</code>.
		 */
		public static final QualifiedName JDBCResultSet = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "JDBCResultSet");

		/**
		 * A type for representing a row in the resultset.
		 */
		public static final QualifiedName ResultRow = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "ResultRow");

		/**
		 * The results of executing a database select query.
		 * This can be used to access the resultset metadata and to retrieve the data values from the resultset.
		 */
		public static final QualifiedName ResultSet = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "ResultSet");

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Data.DataGems module.
	 */
	public static final class Functions {
		/**
		 * Adds a batch of operations to the database connection.
		 * @param connection (CAL type: <code>Cal.Data.DataGems.JDBCConnection</code>)
		 *          a database connection
		 * @param sql (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the SQL for the batch operations
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the batch was added successfully
		 */
		public static final SourceModel.Expr addBatch(SourceModel.Expr connection, SourceModel.Expr sql) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.addBatch), connection, sql});
		}

		/**
		 * @see #addBatch(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param connection
		 * @param sql
		 * @return the SourceModel.Expr representing an application of addBatch
		 */
		public static final SourceModel.Expr addBatch(SourceModel.Expr connection, java.lang.String sql) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.addBatch), connection, SourceModel.Expr.makeStringValue(sql)});
		}

		/**
		 * Name binding for function: addBatch.
		 * @see #addBatch(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName addBatch = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "addBatch");

		/**
		 * Binds a <code>Cal.Core.Prelude.Boolean</code> value to the given prepared statement
		 * @param stmt (CAL type: <code>Cal.Data.DataGems.JDBCPreparedStatement</code>)
		 *          Binds the value to this statement.
		 * @param parameterIndex (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          Binds the value at this index.
		 * @param value (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Core.Prelude.Boolean</code>)
		 *          Binds this value and <code>Cal.Core.Prelude.Nothing</code> means null.
		 * @return (CAL type: <code>Cal.Data.DataGems.JDBCPreparedStatement</code>) 
		 *          The modified statement.
		 */
		public static final SourceModel.Expr bindBooleanToPreparedStatement(SourceModel.Expr stmt, SourceModel.Expr parameterIndex, SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.bindBooleanToPreparedStatement), stmt, parameterIndex, value});
		}

		/**
		 * @see #bindBooleanToPreparedStatement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param stmt
		 * @param parameterIndex
		 * @param value
		 * @return the SourceModel.Expr representing an application of bindBooleanToPreparedStatement
		 */
		public static final SourceModel.Expr bindBooleanToPreparedStatement(SourceModel.Expr stmt, int parameterIndex, SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.bindBooleanToPreparedStatement), stmt, SourceModel.Expr.makeIntValue(parameterIndex), value});
		}

		/**
		 * Name binding for function: bindBooleanToPreparedStatement.
		 * @see #bindBooleanToPreparedStatement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName bindBooleanToPreparedStatement = 
			QualifiedName.make(
				CAL_DataGems.MODULE_NAME, 
				"bindBooleanToPreparedStatement");

		/**
		 * Binds a <code>Cal.Core.Prelude.Double</code> value to the given prepared statement
		 * @param stmt (CAL type: <code>Cal.Data.DataGems.JDBCPreparedStatement</code>)
		 *          Binds the value to this statement.
		 * @param parameterIndex (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          Binds the value at this index.
		 * @param value (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Core.Prelude.Double</code>)
		 *          Binds this value and <code>Cal.Core.Prelude.Nothing</code> means null.
		 * @return (CAL type: <code>Cal.Data.DataGems.JDBCPreparedStatement</code>) 
		 *          The modified statement.
		 */
		public static final SourceModel.Expr bindDoubleToPreparedStatement(SourceModel.Expr stmt, SourceModel.Expr parameterIndex, SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.bindDoubleToPreparedStatement), stmt, parameterIndex, value});
		}

		/**
		 * @see #bindDoubleToPreparedStatement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param stmt
		 * @param parameterIndex
		 * @param value
		 * @return the SourceModel.Expr representing an application of bindDoubleToPreparedStatement
		 */
		public static final SourceModel.Expr bindDoubleToPreparedStatement(SourceModel.Expr stmt, int parameterIndex, SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.bindDoubleToPreparedStatement), stmt, SourceModel.Expr.makeIntValue(parameterIndex), value});
		}

		/**
		 * Name binding for function: bindDoubleToPreparedStatement.
		 * @see #bindDoubleToPreparedStatement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName bindDoubleToPreparedStatement = 
			QualifiedName.make(
				CAL_DataGems.MODULE_NAME, 
				"bindDoubleToPreparedStatement");

		/**
		 * Binds an <code>Cal.Core.Prelude.Int</code> value to the given prepared statement
		 * @param stmt (CAL type: <code>Cal.Data.DataGems.JDBCPreparedStatement</code>)
		 *          Binds the value to this statement.
		 * @param parameterIndex (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          Binds the value at this index.
		 * @param value (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Core.Prelude.Int</code>)
		 *          Binds this value and <code>Cal.Core.Prelude.Nothing</code> means null.
		 * @return (CAL type: <code>Cal.Data.DataGems.JDBCPreparedStatement</code>) 
		 *          The modified statement.
		 */
		public static final SourceModel.Expr bindIntToPreparedStatement(SourceModel.Expr stmt, SourceModel.Expr parameterIndex, SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.bindIntToPreparedStatement), stmt, parameterIndex, value});
		}

		/**
		 * @see #bindIntToPreparedStatement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param stmt
		 * @param parameterIndex
		 * @param value
		 * @return the SourceModel.Expr representing an application of bindIntToPreparedStatement
		 */
		public static final SourceModel.Expr bindIntToPreparedStatement(SourceModel.Expr stmt, int parameterIndex, SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.bindIntToPreparedStatement), stmt, SourceModel.Expr.makeIntValue(parameterIndex), value});
		}

		/**
		 * Name binding for function: bindIntToPreparedStatement.
		 * @see #bindIntToPreparedStatement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName bindIntToPreparedStatement = 
			QualifiedName.make(
				CAL_DataGems.MODULE_NAME, 
				"bindIntToPreparedStatement");

		/**
		 * Binds a null value to the given prepared statement
		 * @param stmt (CAL type: <code>Cal.Data.DataGems.JDBCPreparedStatement</code>)
		 *          Binds the value to this statement.
		 * @param parameterIndex (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          Binds the value at this index.
		 * @param valueType (CAL type: <code>Cal.Utilities.ValueType.ValueType</code>)
		 *          Binds a null value of this type to the statement.
		 * @return (CAL type: <code>Cal.Data.DataGems.JDBCPreparedStatement</code>) 
		 *          The modified statement.
		 */
		public static final SourceModel.Expr bindNullToPreparedStatement(SourceModel.Expr stmt, SourceModel.Expr parameterIndex, SourceModel.Expr valueType) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.bindNullToPreparedStatement), stmt, parameterIndex, valueType});
		}

		/**
		 * @see #bindNullToPreparedStatement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param stmt
		 * @param parameterIndex
		 * @param valueType
		 * @return the SourceModel.Expr representing an application of bindNullToPreparedStatement
		 */
		public static final SourceModel.Expr bindNullToPreparedStatement(SourceModel.Expr stmt, int parameterIndex, SourceModel.Expr valueType) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.bindNullToPreparedStatement), stmt, SourceModel.Expr.makeIntValue(parameterIndex), valueType});
		}

		/**
		 * Name binding for function: bindNullToPreparedStatement.
		 * @see #bindNullToPreparedStatement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName bindNullToPreparedStatement = 
			QualifiedName.make(
				CAL_DataGems.MODULE_NAME, 
				"bindNullToPreparedStatement");

		/**
		 * Binds a <code>Cal.Core.Prelude.String</code> value to the given prepared statement
		 * @param stmt (CAL type: <code>Cal.Data.DataGems.JDBCPreparedStatement</code>)
		 *          Binds the value to this statement.
		 * @param parameterIndex (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          Binds the value at this index.
		 * @param value (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Core.Prelude.String</code>)
		 *          Binds this value and <code>Cal.Core.Prelude.Nothing</code> means null.
		 * @return (CAL type: <code>Cal.Data.DataGems.JDBCPreparedStatement</code>) 
		 *          The modified statement.
		 */
		public static final SourceModel.Expr bindStringToPreparedStatement(SourceModel.Expr stmt, SourceModel.Expr parameterIndex, SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.bindStringToPreparedStatement), stmt, parameterIndex, value});
		}

		/**
		 * @see #bindStringToPreparedStatement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param stmt
		 * @param parameterIndex
		 * @param value
		 * @return the SourceModel.Expr representing an application of bindStringToPreparedStatement
		 */
		public static final SourceModel.Expr bindStringToPreparedStatement(SourceModel.Expr stmt, int parameterIndex, SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.bindStringToPreparedStatement), stmt, SourceModel.Expr.makeIntValue(parameterIndex), value});
		}

		/**
		 * Name binding for function: bindStringToPreparedStatement.
		 * @see #bindStringToPreparedStatement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName bindStringToPreparedStatement = 
			QualifiedName.make(
				CAL_DataGems.MODULE_NAME, 
				"bindStringToPreparedStatement");

		/**
		 * Binds a <code>Cal.Utilities.Time.Time</code> value to the given prepared statement
		 * @param stmt (CAL type: <code>Cal.Data.DataGems.JDBCPreparedStatement</code>)
		 *          Binds the value to this statement.
		 * @param parameterIndex (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          Binds the value at this index.
		 * @param value (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Utilities.Time.Time</code>)
		 *          Binds this value and <code>Cal.Core.Prelude.Nothing</code> means null.
		 * @return (CAL type: <code>Cal.Data.DataGems.JDBCPreparedStatement</code>) 
		 *          The modified statement.
		 */
		public static final SourceModel.Expr bindTimeToPreparedStatement(SourceModel.Expr stmt, SourceModel.Expr parameterIndex, SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.bindTimeToPreparedStatement), stmt, parameterIndex, value});
		}

		/**
		 * @see #bindTimeToPreparedStatement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param stmt
		 * @param parameterIndex
		 * @param value
		 * @return the SourceModel.Expr representing an application of bindTimeToPreparedStatement
		 */
		public static final SourceModel.Expr bindTimeToPreparedStatement(SourceModel.Expr stmt, int parameterIndex, SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.bindTimeToPreparedStatement), stmt, SourceModel.Expr.makeIntValue(parameterIndex), value});
		}

		/**
		 * Name binding for function: bindTimeToPreparedStatement.
		 * @see #bindTimeToPreparedStatement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName bindTimeToPreparedStatement = 
			QualifiedName.make(
				CAL_DataGems.MODULE_NAME, 
				"bindTimeToPreparedStatement");

		/**
		 * Closes a prepared statement.
		 * @param preparedStmt (CAL type: <code>Cal.Data.DataGems.JDBCPreparedStatement</code>)
		 *          a prepared statement
		 * @return (CAL type: <code>()</code>) 
		 */
		public static final SourceModel.Expr closePreparedStatement(SourceModel.Expr preparedStmt) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.closePreparedStatement), preparedStmt});
		}

		/**
		 * Name binding for function: closePreparedStatement.
		 * @see #closePreparedStatement(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName closePreparedStatement = 
			QualifiedName.make(
				CAL_DataGems.MODULE_NAME, 
				"closePreparedStatement");

		/**
		 * Returns the number of columns in a <code>Cal.Data.DataGems.ResultSet</code>.
		 * @param resultSet (CAL type: <code>Cal.Data.DataGems.ResultSet</code>)
		 *          a <code>Cal.Data.DataGems.ResultSet</code>
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the number of columns in the <code>Cal.Data.DataGems.ResultSet</code>
		 */
		public static final SourceModel.Expr columnCount(SourceModel.Expr resultSet) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.columnCount), resultSet});
		}

		/**
		 * Name binding for function: columnCount.
		 * @see #columnCount(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName columnCount = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "columnCount");

		/**
		 * Returns the label for the <code>Cal.Data.DataGems.ResultSet</code> column at the specified ordinal.
		 * @param resultSet (CAL type: <code>Cal.Data.DataGems.ResultSet</code>)
		 *          a <code>Cal.Data.DataGems.ResultSet</code>
		 * @param columnOrdinal (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the 1-based column ordinal
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the label for the specified <code>Cal.Data.DataGems.ResultSet</code> column
		 */
		public static final SourceModel.Expr columnLabel(SourceModel.Expr resultSet, SourceModel.Expr columnOrdinal) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.columnLabel), resultSet, columnOrdinal});
		}

		/**
		 * @see #columnLabel(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param resultSet
		 * @param columnOrdinal
		 * @return the SourceModel.Expr representing an application of columnLabel
		 */
		public static final SourceModel.Expr columnLabel(SourceModel.Expr resultSet, int columnOrdinal) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.columnLabel), resultSet, SourceModel.Expr.makeIntValue(columnOrdinal)});
		}

		/**
		 * Name binding for function: columnLabel.
		 * @see #columnLabel(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName columnLabel = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "columnLabel");

		/**
		 * Returns the name for the <code>Cal.Data.DataGems.ResultSet</code> column at the specified ordinal.
		 * @param resultSet (CAL type: <code>Cal.Data.DataGems.ResultSet</code>)
		 *          a <code>Cal.Data.DataGems.ResultSet</code>
		 * @param columnOrdinal (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the 1-based column ordinal
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the name for the specified <code>Cal.Data.DataGems.ResultSet</code> column
		 */
		public static final SourceModel.Expr columnName(SourceModel.Expr resultSet, SourceModel.Expr columnOrdinal) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.columnName), resultSet, columnOrdinal});
		}

		/**
		 * @see #columnName(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param resultSet
		 * @param columnOrdinal
		 * @return the SourceModel.Expr representing an application of columnName
		 */
		public static final SourceModel.Expr columnName(SourceModel.Expr resultSet, int columnOrdinal) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.columnName), resultSet, SourceModel.Expr.makeIntValue(columnOrdinal)});
		}

		/**
		 * Name binding for function: columnName.
		 * @see #columnName(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName columnName = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "columnName");

		/**
		 * Returns the names of the <code>Cal.Data.DataGems.ResultSet</code> columns.
		 * @param resultSet (CAL type: <code>Cal.Data.DataGems.ResultSet</code>)
		 *          a <code>Cal.Data.DataGems.ResultSet</code>
		 * @return (CAL type: <code>[Cal.Core.Prelude.String]</code>) 
		 *          the names of the <code>Cal.Data.DataGems.ResultSet</code> columns
		 */
		public static final SourceModel.Expr columnNames(SourceModel.Expr resultSet) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.columnNames), resultSet});
		}

		/**
		 * Name binding for function: columnNames.
		 * @see #columnNames(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName columnNames = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "columnNames");

		/**
		 * Returns the SQL data type for the specified column in the <code>Cal.Data.DataGems.ResultSet</code>.
		 * @param resultSet (CAL type: <code>Cal.Data.DataGems.ResultSet</code>)
		 *          a <code>Cal.Data.DataGems.ResultSet</code>
		 * @param columnOrdinal (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the 1-based column ordinal
		 * @return (CAL type: <code>Cal.Data.SqlType.SqlType</code>) 
		 *          the <code>Cal.Data.SqlType.SqlType</code> for the specified <code>Cal.Data.DataGems.ResultSet</code> column
		 */
		public static final SourceModel.Expr columnSqlType(SourceModel.Expr resultSet, SourceModel.Expr columnOrdinal) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.columnSqlType), resultSet, columnOrdinal});
		}

		/**
		 * @see #columnSqlType(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param resultSet
		 * @param columnOrdinal
		 * @return the SourceModel.Expr representing an application of columnSqlType
		 */
		public static final SourceModel.Expr columnSqlType(SourceModel.Expr resultSet, int columnOrdinal) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.columnSqlType), resultSet, SourceModel.Expr.makeIntValue(columnOrdinal)});
		}

		/**
		 * Name binding for function: columnSqlType.
		 * @see #columnSqlType(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName columnSqlType = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "columnSqlType");

		/**
		 * Returns the closest value type for the specified column in the <code>Cal.Data.DataGems.ResultSet</code>.
		 * @param resultSet (CAL type: <code>Cal.Data.DataGems.ResultSet</code>)
		 *          a <code>Cal.Data.DataGems.ResultSet</code>
		 * @param columnOrdinal (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the 1-based column ordinal
		 * @return (CAL type: <code>Cal.Utilities.ValueType.ValueType</code>) 
		 *          the <code>Cal.Utilities.ValueType.ValueType</code> for the specified <code>Cal.Data.DataGems.ResultSet</code> column
		 */
		public static final SourceModel.Expr columnType(SourceModel.Expr resultSet, SourceModel.Expr columnOrdinal) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.columnType), resultSet, columnOrdinal});
		}

		/**
		 * @see #columnType(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param resultSet
		 * @param columnOrdinal
		 * @return the SourceModel.Expr representing an application of columnType
		 */
		public static final SourceModel.Expr columnType(SourceModel.Expr resultSet, int columnOrdinal) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.columnType), resultSet, SourceModel.Expr.makeIntValue(columnOrdinal)});
		}

		/**
		 * Name binding for function: columnType.
		 * @see #columnType(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName columnType = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "columnType");

		/**
		 * Commit the changes made through the database connection.
		 * @param connection (CAL type: <code>Cal.Data.DataGems.JDBCConnection</code>)
		 *          a database connection
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the changes were committed successfully
		 */
		public static final SourceModel.Expr commit(SourceModel.Expr connection) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.commit), connection});
		}

		/**
		 * Name binding for function: commit.
		 * @see #commit(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName commit = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "commit");

		/**
		 * Retrieves the name of the database product to which this connection is connected.
		 * @param connection (CAL type: <code>Cal.Data.DataGems.JDBCConnection</code>)
		 *          a database connection
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the name of the database product for the connection
		 */
		public static final SourceModel.Expr connectionDatabaseProductName(SourceModel.Expr connection) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.connectionDatabaseProductName), connection});
		}

		/**
		 * Name binding for function: connectionDatabaseProductName.
		 * @see #connectionDatabaseProductName(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName connectionDatabaseProductName = 
			QualifiedName.make(
				CAL_DataGems.MODULE_NAME, 
				"connectionDatabaseProductName");

		/**
		 * Creates a prepared statement from a SQL string.
		 * @param connection (CAL type: <code>Cal.Data.DataGems.JDBCConnection</code>)
		 *          a database connection
		 * @param sql (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the SQL for the prepared statement
		 * @return (CAL type: <code>Cal.Data.DataGems.JDBCPreparedStatement</code>) 
		 *          the prepared statement for the specified SQL
		 */
		public static final SourceModel.Expr createPreparedStatement(SourceModel.Expr connection, SourceModel.Expr sql) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.createPreparedStatement), connection, sql});
		}

		/**
		 * @see #createPreparedStatement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param connection
		 * @param sql
		 * @return the SourceModel.Expr representing an application of createPreparedStatement
		 */
		public static final SourceModel.Expr createPreparedStatement(SourceModel.Expr connection, java.lang.String sql) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.createPreparedStatement), connection, SourceModel.Expr.makeStringValue(sql)});
		}

		/**
		 * Name binding for function: createPreparedStatement.
		 * @see #createPreparedStatement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName createPreparedStatement = 
			QualifiedName.make(
				CAL_DataGems.MODULE_NAME, 
				"createPreparedStatement");

		/**
		 * Fetches the data from a <code>Cal.Data.DataGems.ResultSet</code> using the specified extractor function for each row.
		 * @param resultSet (CAL type: <code>Cal.Data.DataGems.ResultSet</code>)
		 *          a <code>Cal.Data.DataGems.ResultSet</code>
		 * @param extractFn (CAL type: <code>Cal.Data.DataGems.ResultRow -> a</code>)
		 *          a function to extract a value from each <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>[a]</code>) 
		 *          the list of values extracted from the <code>Cal.Data.DataGems.ResultSet</code> rows
		 */
		public static final SourceModel.Expr dataFromResultSet(SourceModel.Expr resultSet, SourceModel.Expr extractFn) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.dataFromResultSet), resultSet, extractFn});
		}

		/**
		 * Name binding for function: dataFromResultSet.
		 * @see #dataFromResultSet(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName dataFromResultSet = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "dataFromResultSet");

		/**
		 * Fetches the data from a <code>Cal.Data.DataGems.ResultSet</code> using the specified extractor function for each row.
		 * If the closeAfterReading option is <code>Cal.Core.Prelude.True</code>, then the <code>Cal.Data.DataGems.ResultSet</code> will be closed after reading the data.
		 * @param closeAfterReading (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 *          if <code>Cal.Core.Prelude.True</code>, then the <code>Cal.Data.DataGems.ResultSet</code> will be closed after reading the data
		 * @param resultSet (CAL type: <code>Cal.Data.DataGems.ResultSet</code>)
		 *          a <code>Cal.Data.DataGems.ResultSet</code>
		 * @param extractFn (CAL type: <code>Cal.Data.DataGems.ResultRow -> a</code>)
		 *          a function to extract a value from each <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>[a]</code>) 
		 *          the list of values extracted from the <code>Cal.Data.DataGems.ResultSet</code> rows
		 */
		public static final SourceModel.Expr dataFromResultSet2(SourceModel.Expr closeAfterReading, SourceModel.Expr resultSet, SourceModel.Expr extractFn) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.dataFromResultSet2), closeAfterReading, resultSet, extractFn});
		}

		/**
		 * @see #dataFromResultSet2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param closeAfterReading
		 * @param resultSet
		 * @param extractFn
		 * @return the SourceModel.Expr representing an application of dataFromResultSet2
		 */
		public static final SourceModel.Expr dataFromResultSet2(boolean closeAfterReading, SourceModel.Expr resultSet, SourceModel.Expr extractFn) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.dataFromResultSet2), SourceModel.Expr.makeBooleanValue(closeAfterReading), resultSet, extractFn});
		}

		/**
		 * Name binding for function: dataFromResultSet2.
		 * @see #dataFromResultSet2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName dataFromResultSet2 = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "dataFromResultSet2");

		/**
		 * Fetches the data from a <code>Cal.Data.DataGems.ResultSet</code> as a <code>Cal.Collections.Map.Map</code>.
		 * A map entry will be added for each row. The specified extractor functions will be used for the keys and values.
		 * @param resultSet (CAL type: <code>Cal.Data.DataGems.ResultSet</code>)
		 *          a <code>Cal.Data.DataGems.ResultSet</code>
		 * @param keyExtractorFn (CAL type: <code>Cal.Core.Prelude.Ord k => Cal.Data.DataGems.ResultRow -> k</code>)
		 *          a function to extract a key value from each <code>Cal.Data.DataGems.ResultRow</code>
		 * @param valueExtractFn (CAL type: <code>Cal.Data.DataGems.ResultRow -> a</code>)
		 *          a function to extract a map value from each <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>Cal.Core.Prelude.Ord k => Cal.Collections.Map.Map k a</code>) 
		 *          a <code>Cal.Collections.Map.Map</code> containing keys and values obtained by applying the functions to each row in the <code>Cal.Data.DataGems.ResultSet</code>
		 */
		public static final SourceModel.Expr dataMapFromResultSet(SourceModel.Expr resultSet, SourceModel.Expr keyExtractorFn, SourceModel.Expr valueExtractFn) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.dataMapFromResultSet), resultSet, keyExtractorFn, valueExtractFn});
		}

		/**
		 * Name binding for function: dataMapFromResultSet.
		 * @see #dataMapFromResultSet(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName dataMapFromResultSet = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "dataMapFromResultSet");

		/**
		 * Fetches all the data from a <code>Cal.Data.DataGems.ResultSet</code> as <code>Cal.Core.Dynamic.Dynamic</code> values.
		 * @param resultSet (CAL type: <code>Cal.Data.DataGems.ResultSet</code>)
		 *          a <code>Cal.Data.DataGems.ResultSet</code>
		 * @return (CAL type: <code>[[Cal.Core.Dynamic.Dynamic]]</code>) 
		 *          the data from the <code>Cal.Data.DataGems.ResultSet</code> as a list of rows of <code>Cal.Core.Dynamic.Dynamic</code> values
		 */
		public static final SourceModel.Expr dynamicDataFromResultSet(SourceModel.Expr resultSet) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.dynamicDataFromResultSet), resultSet});
		}

		/**
		 * Name binding for function: dynamicDataFromResultSet.
		 * @see #dynamicDataFromResultSet(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName dynamicDataFromResultSet = 
			QualifiedName.make(
				CAL_DataGems.MODULE_NAME, 
				"dynamicDataFromResultSet");

		/**
		 * Executes a batch of operations on the database connection.
		 * @param connection (CAL type: <code>Cal.Data.DataGems.JDBCConnection</code>)
		 *          a database connection
		 * @return (CAL type: <code>Cal.Collections.Array.Array Cal.Core.Prelude.Int</code>) 
		 *          an array containing the numbers of rows updated by each command in the batch
		 */
		public static final SourceModel.Expr executeBatch(SourceModel.Expr connection) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.executeBatch), connection});
		}

		/**
		 * Name binding for function: executeBatch.
		 * @see #executeBatch(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName executeBatch = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "executeBatch");

		/**
		 * Executes a SQL statement which performs an update operation.
		 * @param connection (CAL type: <code>Cal.Data.DataGems.JDBCConnection</code>)
		 *          a database connection
		 * @param updateSQL (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          a SQL statement for the update operation
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the number of rows affected by the update operation
		 */
		public static final SourceModel.Expr executeUpdate(SourceModel.Expr connection, SourceModel.Expr updateSQL) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.executeUpdate), connection, updateSQL});
		}

		/**
		 * @see #executeUpdate(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param connection
		 * @param updateSQL
		 * @return the SourceModel.Expr representing an application of executeUpdate
		 */
		public static final SourceModel.Expr executeUpdate(SourceModel.Expr connection, java.lang.String updateSQL) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.executeUpdate), connection, SourceModel.Expr.makeStringValue(updateSQL)});
		}

		/**
		 * Name binding for function: executeUpdate.
		 * @see #executeUpdate(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName executeUpdate = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "executeUpdate");

		/**
		 * Retrieves an array of values (using the same extractor) from each row in the <code>Cal.Data.DataGems.ResultRow</code>.
		 * @param extractFn (CAL type: <code>Cal.Core.Prelude.Typeable a => Cal.Core.Prelude.Int -> Cal.Data.DataGems.ResultRow -> a</code>)
		 *          a function to extract a value from a column of the <code>Cal.Data.DataGems.ResultRow</code>
		 * @param resultRow (CAL type: <code>Cal.Data.DataGems.ResultRow</code>)
		 *          a <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>Cal.Core.Prelude.Typeable a => Cal.Collections.Array.Array a</code>) 
		 *          an array of the specified values from the current <code>Cal.Data.DataGems.ResultRow</code>
		 */
		public static final SourceModel.Expr extractArray(SourceModel.Expr extractFn, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractArray), extractFn, resultRow});
		}

		/**
		 * Name binding for function: extractArray.
		 * @see #extractArray(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extractArray = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "extractArray");

		/**
		 * Extracts a <code>Cal.Core.Prelude.Boolean</code> value from the specified column in the <code>Cal.Data.DataGems.ResultRow</code>.
		 * @param columnN (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the (1-based) ordinal of the column for which the value will be fetched
		 * @param resultRow (CAL type: <code>Cal.Data.DataGems.ResultRow</code>)
		 *          a <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          the <code>Cal.Core.Prelude.Boolean</code> value from the specified column in the current <code>Cal.Data.DataGems.ResultRow</code>
		 */
		public static final SourceModel.Expr extractBoolean(SourceModel.Expr columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractBoolean), columnN, resultRow});
		}

		/**
		 * @see #extractBoolean(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param columnN
		 * @param resultRow
		 * @return the SourceModel.Expr representing an application of extractBoolean
		 */
		public static final SourceModel.Expr extractBoolean(int columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractBoolean), SourceModel.Expr.makeIntValue(columnN), resultRow});
		}

		/**
		 * Name binding for function: extractBoolean.
		 * @see #extractBoolean(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extractBoolean = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "extractBoolean");

		/**
		 * Extracts a <code>Cal.Core.Prelude.Boolean</code> value from the specified column in the <code>Cal.Data.DataGems.ResultRow</code>.
		 * @param columnName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the name of the column for which the value will be fetched
		 * @param resultRow (CAL type: <code>Cal.Data.DataGems.ResultRow</code>)
		 *          a <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          the <code>Cal.Core.Prelude.Boolean</code> value from the specified column in the current <code>Cal.Data.DataGems.ResultRow</code>
		 */
		public static final SourceModel.Expr extractBooleanByName(SourceModel.Expr columnName, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractBooleanByName), columnName, resultRow});
		}

		/**
		 * @see #extractBooleanByName(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param columnName
		 * @param resultRow
		 * @return the SourceModel.Expr representing an application of extractBooleanByName
		 */
		public static final SourceModel.Expr extractBooleanByName(java.lang.String columnName, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractBooleanByName), SourceModel.Expr.makeStringValue(columnName), resultRow});
		}

		/**
		 * Name binding for function: extractBooleanByName.
		 * @see #extractBooleanByName(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extractBooleanByName = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "extractBooleanByName");

		/**
		 * Extracts a <code>Cal.Core.Prelude.Boolean</code> value from the specified column in the <code>Cal.Data.DataGems.ResultRow</code>.
		 * The defaultValue will be returned if the fetched value is null.
		 * @param defaultValue (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 *          the value to be returned if the fetched value is null
		 * @param columnN (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the (1-based) ordinal of the column for which the value will be fetched
		 * @param resultRow (CAL type: <code>Cal.Data.DataGems.ResultRow</code>)
		 *          a <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          the <code>Cal.Core.Prelude.Boolean</code> value from the specified column in the current <code>Cal.Data.DataGems.ResultRow</code>
		 */
		public static final SourceModel.Expr extractBooleanWithDefault(SourceModel.Expr defaultValue, SourceModel.Expr columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractBooleanWithDefault), defaultValue, columnN, resultRow});
		}

		/**
		 * @see #extractBooleanWithDefault(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param defaultValue
		 * @param columnN
		 * @param resultRow
		 * @return the SourceModel.Expr representing an application of extractBooleanWithDefault
		 */
		public static final SourceModel.Expr extractBooleanWithDefault(boolean defaultValue, int columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractBooleanWithDefault), SourceModel.Expr.makeBooleanValue(defaultValue), SourceModel.Expr.makeIntValue(columnN), resultRow});
		}

		/**
		 * Name binding for function: extractBooleanWithDefault.
		 * @see #extractBooleanWithDefault(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extractBooleanWithDefault = 
			QualifiedName.make(
				CAL_DataGems.MODULE_NAME, 
				"extractBooleanWithDefault");

		/**
		 * Extracts a byte array value from the specified column in the <code>Cal.Data.DataGems.ResultRow</code>.
		 * @param columnN (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the (1-based) ordinal of the column for which the value will be fetched
		 * @param resultRow (CAL type: <code>Cal.Data.DataGems.ResultRow</code>)
		 *          a <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>Cal.Collections.Array.Array Cal.Core.Prelude.Byte</code>) 
		 *          the byte array value from the specified column in the current <code>Cal.Data.DataGems.ResultRow</code>
		 */
		public static final SourceModel.Expr extractBytes(SourceModel.Expr columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractBytes), columnN, resultRow});
		}

		/**
		 * @see #extractBytes(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param columnN
		 * @param resultRow
		 * @return the SourceModel.Expr representing an application of extractBytes
		 */
		public static final SourceModel.Expr extractBytes(int columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractBytes), SourceModel.Expr.makeIntValue(columnN), resultRow});
		}

		/**
		 * Name binding for function: extractBytes.
		 * @see #extractBytes(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extractBytes = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "extractBytes");

		/**
		 * Extracts a byte array value from the specified column in the <code>Cal.Data.DataGems.ResultRow</code>.
		 * @param columnName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the name of the column for which the value will be fetched
		 * @param resultRow (CAL type: <code>Cal.Data.DataGems.ResultRow</code>)
		 *          a <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>Cal.Collections.Array.Array Cal.Core.Prelude.Byte</code>) 
		 *          the byte array value from the specified column in the current <code>Cal.Data.DataGems.ResultRow</code>
		 */
		public static final SourceModel.Expr extractBytesByName(SourceModel.Expr columnName, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractBytesByName), columnName, resultRow});
		}

		/**
		 * @see #extractBytesByName(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param columnName
		 * @param resultRow
		 * @return the SourceModel.Expr representing an application of extractBytesByName
		 */
		public static final SourceModel.Expr extractBytesByName(java.lang.String columnName, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractBytesByName), SourceModel.Expr.makeStringValue(columnName), resultRow});
		}

		/**
		 * Name binding for function: extractBytesByName.
		 * @see #extractBytesByName(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extractBytesByName = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "extractBytesByName");

		/**
		 * Extracts a byte array value from the specified column in the <code>Cal.Data.DataGems.ResultRow</code>.
		 * The defaultValue will be returned if the fetched value is null.
		 * @param defaultValue (CAL type: <code>Cal.Collections.Array.Array Cal.Core.Prelude.Byte</code>)
		 *          the value to be returned if the fetched value is null
		 * @param columnN (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the (1-based) ordinal of the column for which the value will be fetched
		 * @param resultRow (CAL type: <code>Cal.Data.DataGems.ResultRow</code>)
		 *          a <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>Cal.Collections.Array.Array Cal.Core.Prelude.Byte</code>) 
		 *          the byte array value from the specified column in the current <code>Cal.Data.DataGems.ResultRow</code>
		 */
		public static final SourceModel.Expr extractBytesWithDefault(SourceModel.Expr defaultValue, SourceModel.Expr columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractBytesWithDefault), defaultValue, columnN, resultRow});
		}

		/**
		 * @see #extractBytesWithDefault(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param defaultValue
		 * @param columnN
		 * @param resultRow
		 * @return the SourceModel.Expr representing an application of extractBytesWithDefault
		 */
		public static final SourceModel.Expr extractBytesWithDefault(SourceModel.Expr defaultValue, int columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractBytesWithDefault), defaultValue, SourceModel.Expr.makeIntValue(columnN), resultRow});
		}

		/**
		 * Name binding for function: extractBytesWithDefault.
		 * @see #extractBytesWithDefault(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extractBytesWithDefault = 
			QualifiedName.make(
				CAL_DataGems.MODULE_NAME, 
				"extractBytesWithDefault");

		/**
		 * Extracts a <code>Cal.Utilities.RelativeTime.JDate</code> value from the specified column in the <code>Cal.Data.DataGems.ResultRow</code>.
		 * @param columnN (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the (1-based) ordinal of the column for which the value will be fetched
		 * @param resultRow (CAL type: <code>Cal.Data.DataGems.ResultRow</code>)
		 *          a <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>Cal.Utilities.RelativeTime.JDate</code>) 
		 *          the <code>Cal.Utilities.RelativeTime.JDate</code> value from the specified column in the current <code>Cal.Data.DataGems.ResultRow</code>
		 */
		public static final SourceModel.Expr extractDate(SourceModel.Expr columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractDate), columnN, resultRow});
		}

		/**
		 * @see #extractDate(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param columnN
		 * @param resultRow
		 * @return the SourceModel.Expr representing an application of extractDate
		 */
		public static final SourceModel.Expr extractDate(int columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractDate), SourceModel.Expr.makeIntValue(columnN), resultRow});
		}

		/**
		 * Name binding for function: extractDate.
		 * @see #extractDate(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extractDate = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "extractDate");

		/**
		 * Extracts a <code>Cal.Utilities.RelativeTime.JDate</code> value from the specified column in the <code>Cal.Data.DataGems.ResultRow</code>.
		 * @param columnName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the name of the column for which the value will be fetched
		 * @param resultRow (CAL type: <code>Cal.Data.DataGems.ResultRow</code>)
		 *          a <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>Cal.Utilities.RelativeTime.JDate</code>) 
		 *          the <code>Cal.Utilities.RelativeTime.JDate</code> value from the specified column in the current <code>Cal.Data.DataGems.ResultRow</code>
		 */
		public static final SourceModel.Expr extractDateByName(SourceModel.Expr columnName, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractDateByName), columnName, resultRow});
		}

		/**
		 * @see #extractDateByName(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param columnName
		 * @param resultRow
		 * @return the SourceModel.Expr representing an application of extractDateByName
		 */
		public static final SourceModel.Expr extractDateByName(java.lang.String columnName, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractDateByName), SourceModel.Expr.makeStringValue(columnName), resultRow});
		}

		/**
		 * Name binding for function: extractDateByName.
		 * @see #extractDateByName(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extractDateByName = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "extractDateByName");

		/**
		 * Extracts a <code>Cal.Utilities.RelativeTime.JDate</code> value from the specified column in the <code>Cal.Data.DataGems.ResultRow</code>.
		 * The defaultValue will be returned if the fetched value is null.
		 * @param defaultValue (CAL type: <code>Cal.Utilities.RelativeTime.JDate</code>)
		 *          the value to be returned if the fetched value is null
		 * @param columnN (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the (1-based) ordinal of the column for which the value will be fetched
		 * @param resultRow (CAL type: <code>Cal.Data.DataGems.ResultRow</code>)
		 *          a <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>Cal.Utilities.RelativeTime.JDate</code>) 
		 *          the <code>Cal.Utilities.RelativeTime.JDate</code> value from the specified column in the current <code>Cal.Data.DataGems.ResultRow</code>
		 */
		public static final SourceModel.Expr extractDateWithDefault(SourceModel.Expr defaultValue, SourceModel.Expr columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractDateWithDefault), defaultValue, columnN, resultRow});
		}

		/**
		 * @see #extractDateWithDefault(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param defaultValue
		 * @param columnN
		 * @param resultRow
		 * @return the SourceModel.Expr representing an application of extractDateWithDefault
		 */
		public static final SourceModel.Expr extractDateWithDefault(SourceModel.Expr defaultValue, int columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractDateWithDefault), defaultValue, SourceModel.Expr.makeIntValue(columnN), resultRow});
		}

		/**
		 * Name binding for function: extractDateWithDefault.
		 * @see #extractDateWithDefault(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extractDateWithDefault = 
			QualifiedName.make(
				CAL_DataGems.MODULE_NAME, 
				"extractDateWithDefault");

		/**
		 * Extracts a <code>Cal.Core.Prelude.Decimal</code> value from the specified column in the <code>Cal.Data.DataGems.ResultRow</code>.
		 * @param columnN (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the (1-based) ordinal of the column for which the value will be fetched
		 * @param resultRow (CAL type: <code>Cal.Data.DataGems.ResultRow</code>)
		 *          a <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>Cal.Core.Prelude.Decimal</code>) 
		 *          the <code>Cal.Core.Prelude.Decimal</code> value from the specified column in the current <code>Cal.Data.DataGems.ResultRow</code>
		 */
		public static final SourceModel.Expr extractDecimal(SourceModel.Expr columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractDecimal), columnN, resultRow});
		}

		/**
		 * @see #extractDecimal(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param columnN
		 * @param resultRow
		 * @return the SourceModel.Expr representing an application of extractDecimal
		 */
		public static final SourceModel.Expr extractDecimal(int columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractDecimal), SourceModel.Expr.makeIntValue(columnN), resultRow});
		}

		/**
		 * Name binding for function: extractDecimal.
		 * @see #extractDecimal(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extractDecimal = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "extractDecimal");

		/**
		 * Extracts a <code>Cal.Core.Prelude.Decimal</code> value from the specified column in the <code>Cal.Data.DataGems.ResultRow</code>.
		 * @param columnName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the name of the column for which the value will be fetched
		 * @param resultRow (CAL type: <code>Cal.Data.DataGems.ResultRow</code>)
		 *          a <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>Cal.Core.Prelude.Decimal</code>) 
		 *          the <code>Cal.Core.Prelude.Decimal</code> value from the specified column in the current <code>Cal.Data.DataGems.ResultRow</code>
		 */
		public static final SourceModel.Expr extractDecimalByName(SourceModel.Expr columnName, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractDecimalByName), columnName, resultRow});
		}

		/**
		 * @see #extractDecimalByName(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param columnName
		 * @param resultRow
		 * @return the SourceModel.Expr representing an application of extractDecimalByName
		 */
		public static final SourceModel.Expr extractDecimalByName(java.lang.String columnName, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractDecimalByName), SourceModel.Expr.makeStringValue(columnName), resultRow});
		}

		/**
		 * Name binding for function: extractDecimalByName.
		 * @see #extractDecimalByName(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extractDecimalByName = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "extractDecimalByName");

		/**
		 * Extracts a <code>Cal.Core.Prelude.Decimal</code> value from the specified column in the <code>Cal.Data.DataGems.ResultRow</code>.
		 * The defaultValue will be returned if the fetched value is null.
		 * @param defaultValue (CAL type: <code>Cal.Core.Prelude.Decimal</code>)
		 *          the value to be returned if the fetched value is null
		 * @param columnN (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the (1-based) ordinal of the column for which the value will be fetched
		 * @param resultRow (CAL type: <code>Cal.Data.DataGems.ResultRow</code>)
		 *          a <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>Cal.Core.Prelude.Decimal</code>) 
		 *          the <code>Cal.Core.Prelude.Decimal</code> value from the specified column in the current <code>Cal.Data.DataGems.ResultRow</code>
		 */
		public static final SourceModel.Expr extractDecimalWithDefault(SourceModel.Expr defaultValue, SourceModel.Expr columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractDecimalWithDefault), defaultValue, columnN, resultRow});
		}

		/**
		 * @see #extractDecimalWithDefault(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param defaultValue
		 * @param columnN
		 * @param resultRow
		 * @return the SourceModel.Expr representing an application of extractDecimalWithDefault
		 */
		public static final SourceModel.Expr extractDecimalWithDefault(SourceModel.Expr defaultValue, int columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractDecimalWithDefault), defaultValue, SourceModel.Expr.makeIntValue(columnN), resultRow});
		}

		/**
		 * Name binding for function: extractDecimalWithDefault.
		 * @see #extractDecimalWithDefault(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extractDecimalWithDefault = 
			QualifiedName.make(
				CAL_DataGems.MODULE_NAME, 
				"extractDecimalWithDefault");

		/**
		 * Extracts a <code>Cal.Core.Prelude.Double</code> value from the specified column in the <code>Cal.Data.DataGems.ResultRow</code>.
		 * @param columnN (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the (1-based) ordinal of the column for which the value will be fetched
		 * @param resultRow (CAL type: <code>Cal.Data.DataGems.ResultRow</code>)
		 *          a <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 *          the <code>Cal.Core.Prelude.Double</code> value from the specified column in the current <code>Cal.Data.DataGems.ResultRow</code>
		 */
		public static final SourceModel.Expr extractDouble(SourceModel.Expr columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractDouble), columnN, resultRow});
		}

		/**
		 * @see #extractDouble(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param columnN
		 * @param resultRow
		 * @return the SourceModel.Expr representing an application of extractDouble
		 */
		public static final SourceModel.Expr extractDouble(int columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractDouble), SourceModel.Expr.makeIntValue(columnN), resultRow});
		}

		/**
		 * Name binding for function: extractDouble.
		 * @see #extractDouble(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extractDouble = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "extractDouble");

		/**
		 * Extracts a <code>Cal.Core.Prelude.Double</code> value from the specified column in the <code>Cal.Data.DataGems.ResultRow</code>.
		 * @param columnName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the name of the column for which the value will be fetched
		 * @param resultRow (CAL type: <code>Cal.Data.DataGems.ResultRow</code>)
		 *          a <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 *          the <code>Cal.Core.Prelude.Double</code> value from the specified column in the current <code>Cal.Data.DataGems.ResultRow</code>
		 */
		public static final SourceModel.Expr extractDoubleByName(SourceModel.Expr columnName, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractDoubleByName), columnName, resultRow});
		}

		/**
		 * @see #extractDoubleByName(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param columnName
		 * @param resultRow
		 * @return the SourceModel.Expr representing an application of extractDoubleByName
		 */
		public static final SourceModel.Expr extractDoubleByName(java.lang.String columnName, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractDoubleByName), SourceModel.Expr.makeStringValue(columnName), resultRow});
		}

		/**
		 * Name binding for function: extractDoubleByName.
		 * @see #extractDoubleByName(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extractDoubleByName = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "extractDoubleByName");

		/**
		 * Extracts a <code>Cal.Core.Prelude.Double</code> value from the specified column in the <code>Cal.Data.DataGems.ResultRow</code>.
		 * The defaultValue will be returned if the fetched value is null.
		 * @param defaultValue (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 *          the value to be returned if the fetched value is null
		 * @param columnN (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the (1-based) ordinal of the column for which the value will be fetched
		 * @param resultRow (CAL type: <code>Cal.Data.DataGems.ResultRow</code>)
		 *          a <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 *          the <code>Cal.Core.Prelude.Double</code> value from the specified column in the current <code>Cal.Data.DataGems.ResultRow</code>
		 */
		public static final SourceModel.Expr extractDoubleWithDefault(SourceModel.Expr defaultValue, SourceModel.Expr columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractDoubleWithDefault), defaultValue, columnN, resultRow});
		}

		/**
		 * @see #extractDoubleWithDefault(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param defaultValue
		 * @param columnN
		 * @param resultRow
		 * @return the SourceModel.Expr representing an application of extractDoubleWithDefault
		 */
		public static final SourceModel.Expr extractDoubleWithDefault(double defaultValue, int columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractDoubleWithDefault), SourceModel.Expr.makeDoubleValue(defaultValue), SourceModel.Expr.makeIntValue(columnN), resultRow});
		}

		/**
		 * Name binding for function: extractDoubleWithDefault.
		 * @see #extractDoubleWithDefault(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extractDoubleWithDefault = 
			QualifiedName.make(
				CAL_DataGems.MODULE_NAME, 
				"extractDoubleWithDefault");

		/**
		 * Extracts a column value as a <code>Cal.Core.Dynamic.Dynamic</code> value.
		 * For a null value, the value <code>()</code> will be returned wrapped as a <code>Cal.Core.Dynamic.Dynamic</code>.
		 * @param valueType (CAL type: <code>Cal.Utilities.ValueType.ValueType</code>)
		 *          the type that will be used to fetch the column value
		 * @param columnN (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the (1-based) ordinal of the column for which the value should be fetched
		 * @param resultRow (CAL type: <code>Cal.Data.DataGems.ResultRow</code>)
		 *          a <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>Cal.Core.Dynamic.Dynamic</code>) 
		 *          a <code>Cal.Core.Dynamic.Dynamic</code> value with the column value from the current row in the <code>Cal.Data.DataGems.ResultRow</code>
		 */
		public static final SourceModel.Expr extractDynamic(SourceModel.Expr valueType, SourceModel.Expr columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractDynamic), valueType, columnN, resultRow});
		}

		/**
		 * @see #extractDynamic(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param valueType
		 * @param columnN
		 * @param resultRow
		 * @return the SourceModel.Expr representing an application of extractDynamic
		 */
		public static final SourceModel.Expr extractDynamic(SourceModel.Expr valueType, int columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractDynamic), valueType, SourceModel.Expr.makeIntValue(columnN), resultRow});
		}

		/**
		 * Name binding for function: extractDynamic.
		 * @see #extractDynamic(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extractDynamic = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "extractDynamic");

		/**
		 * Retrieves a list of <code>Cal.Core.Dynamic.Dynamic</code> values from a row in the <code>Cal.Data.DataGems.ResultRow</code>.
		 * Type value types for the columns will be determined from the <code>Cal.Data.DataGems.ResultRow</code> metadata.
		 * @param resultRow (CAL type: <code>Cal.Data.DataGems.ResultRow</code>)
		 *          a <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>[Cal.Core.Dynamic.Dynamic]</code>) 
		 *          the data from the current <code>Cal.Data.DataGems.ResultRow</code> as a list of <code>Cal.Core.Dynamic.Dynamic</code> values
		 */
		public static final SourceModel.Expr extractDynamics(SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractDynamics), resultRow});
		}

		/**
		 * Name binding for function: extractDynamics.
		 * @see #extractDynamics(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extractDynamics = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "extractDynamics");

		/**
		 * Retrieves a list of <code>Cal.Core.Dynamic.Dynamic</code> values (of the specified types) from a row in the <code>Cal.Data.DataGems.ResultRow</code>.
		 * @param colTypes (CAL type: <code>[Cal.Utilities.ValueType.ValueType]</code>)
		 *          the types in which each column value will be extracted
		 * @param resultRow (CAL type: <code>Cal.Data.DataGems.ResultRow</code>)
		 *          a <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>[Cal.Core.Dynamic.Dynamic]</code>) 
		 *          the data from the current <code>Cal.Data.DataGems.ResultRow</code> as a list of <code>Cal.Core.Dynamic.Dynamic</code> values
		 */
		public static final SourceModel.Expr extractDynamicsByType(SourceModel.Expr colTypes, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractDynamicsByType), colTypes, resultRow});
		}

		/**
		 * Name binding for function: extractDynamicsByType.
		 * @see #extractDynamicsByType(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extractDynamicsByType = 
			QualifiedName.make(
				CAL_DataGems.MODULE_NAME, 
				"extractDynamicsByType");

		/**
		 * Extracts an <code>Cal.Core.Prelude.Int</code> value from the specified column in the <code>Cal.Data.DataGems.ResultRow</code>.
		 * @param columnN (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the (1-based) ordinal of the column for which the value will be fetched
		 * @param resultRow (CAL type: <code>Cal.Data.DataGems.ResultRow</code>)
		 *          a <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the <code>Cal.Core.Prelude.Int</code> value from the specified column in the current <code>Cal.Data.DataGems.ResultRow</code>
		 */
		public static final SourceModel.Expr extractInt(SourceModel.Expr columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractInt), columnN, resultRow});
		}

		/**
		 * @see #extractInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param columnN
		 * @param resultRow
		 * @return the SourceModel.Expr representing an application of extractInt
		 */
		public static final SourceModel.Expr extractInt(int columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractInt), SourceModel.Expr.makeIntValue(columnN), resultRow});
		}

		/**
		 * Name binding for function: extractInt.
		 * @see #extractInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extractInt = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "extractInt");

		/**
		 * Extracts an <code>Cal.Core.Prelude.Int</code> value from the specified column in the <code>Cal.Data.DataGems.ResultRow</code>.
		 * @param columnName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the name of the column for which the value will be fetched
		 * @param resultRow (CAL type: <code>Cal.Data.DataGems.ResultRow</code>)
		 *          a <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the <code>Cal.Core.Prelude.Int</code> value from the specified column in the current <code>Cal.Data.DataGems.ResultRow</code>
		 */
		public static final SourceModel.Expr extractIntByName(SourceModel.Expr columnName, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractIntByName), columnName, resultRow});
		}

		/**
		 * @see #extractIntByName(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param columnName
		 * @param resultRow
		 * @return the SourceModel.Expr representing an application of extractIntByName
		 */
		public static final SourceModel.Expr extractIntByName(java.lang.String columnName, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractIntByName), SourceModel.Expr.makeStringValue(columnName), resultRow});
		}

		/**
		 * Name binding for function: extractIntByName.
		 * @see #extractIntByName(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extractIntByName = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "extractIntByName");

		/**
		 * Extracts an <code>Cal.Core.Prelude.Int</code> value from the specified column in the <code>Cal.Data.DataGems.ResultRow</code>.
		 * The defaultValue will be returned if the fetched value is null.
		 * @param defaultValue (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the value to be returned if the fetched value is null
		 * @param columnN (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the (1-based) ordinal of the column for which the value will be fetched
		 * @param resultRow (CAL type: <code>Cal.Data.DataGems.ResultRow</code>)
		 *          a <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the <code>Cal.Core.Prelude.Int</code> value from the specified column in the current <code>Cal.Data.DataGems.ResultRow</code>
		 */
		public static final SourceModel.Expr extractIntWithDefault(SourceModel.Expr defaultValue, SourceModel.Expr columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractIntWithDefault), defaultValue, columnN, resultRow});
		}

		/**
		 * @see #extractIntWithDefault(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param defaultValue
		 * @param columnN
		 * @param resultRow
		 * @return the SourceModel.Expr representing an application of extractIntWithDefault
		 */
		public static final SourceModel.Expr extractIntWithDefault(int defaultValue, int columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractIntWithDefault), SourceModel.Expr.makeIntValue(defaultValue), SourceModel.Expr.makeIntValue(columnN), resultRow});
		}

		/**
		 * Name binding for function: extractIntWithDefault.
		 * @see #extractIntWithDefault(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extractIntWithDefault = 
			QualifiedName.make(
				CAL_DataGems.MODULE_NAME, 
				"extractIntWithDefault");

		/**
		 * Retrieves a list of values (using the same extractor) from each row in the <code>Cal.Data.DataGems.ResultRow</code>.
		 * @param extractFn (CAL type: <code>Cal.Core.Prelude.Int -> Cal.Data.DataGems.ResultRow -> a</code>)
		 *          a function to extract a value from a column of the <code>Cal.Data.DataGems.ResultRow</code>
		 * @param resultRow (CAL type: <code>Cal.Data.DataGems.ResultRow</code>)
		 *          a <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>[a]</code>) 
		 *          a list of the specified values from the current <code>Cal.Data.DataGems.ResultRow</code>
		 */
		public static final SourceModel.Expr extractList(SourceModel.Expr extractFn, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractList), extractFn, resultRow});
		}

		/**
		 * Name binding for function: extractList.
		 * @see #extractList(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extractList = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "extractList");

		/**
		 * Extracts an <code>Cal.Core.Prelude.Long</code> value from the specified column in the <code>Cal.Data.DataGems.ResultRow</code>.
		 * @param columnN (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the (1-based) ordinal of the column for which the value will be fetched
		 * @param resultRow (CAL type: <code>Cal.Data.DataGems.ResultRow</code>)
		 *          a <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>Cal.Core.Prelude.Long</code>) 
		 *          the <code>Cal.Core.Prelude.Long</code> value from the specified column in the current <code>Cal.Data.DataGems.ResultRow</code>
		 */
		public static final SourceModel.Expr extractLong(SourceModel.Expr columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractLong), columnN, resultRow});
		}

		/**
		 * @see #extractLong(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param columnN
		 * @param resultRow
		 * @return the SourceModel.Expr representing an application of extractLong
		 */
		public static final SourceModel.Expr extractLong(int columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractLong), SourceModel.Expr.makeIntValue(columnN), resultRow});
		}

		/**
		 * Name binding for function: extractLong.
		 * @see #extractLong(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extractLong = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "extractLong");

		/**
		 * Extracts an <code>Cal.Core.Prelude.Long</code> value from the specified column in the <code>Cal.Data.DataGems.ResultRow</code>.
		 * @param columnName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the name of the column for which the value will be fetched
		 * @param resultRow (CAL type: <code>Cal.Data.DataGems.ResultRow</code>)
		 *          a <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>Cal.Core.Prelude.Long</code>) 
		 *          the <code>Cal.Core.Prelude.Long</code> value from the specified column in the current <code>Cal.Data.DataGems.ResultRow</code>
		 */
		public static final SourceModel.Expr extractLongByName(SourceModel.Expr columnName, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractLongByName), columnName, resultRow});
		}

		/**
		 * @see #extractLongByName(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param columnName
		 * @param resultRow
		 * @return the SourceModel.Expr representing an application of extractLongByName
		 */
		public static final SourceModel.Expr extractLongByName(java.lang.String columnName, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractLongByName), SourceModel.Expr.makeStringValue(columnName), resultRow});
		}

		/**
		 * Name binding for function: extractLongByName.
		 * @see #extractLongByName(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extractLongByName = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "extractLongByName");

		/**
		 * Extracts an <code>Cal.Core.Prelude.Long</code> value from the specified column in the <code>Cal.Data.DataGems.ResultRow</code>.
		 * The defaultValue will be returned if the fetched value is null.
		 * @param defaultValue (CAL type: <code>Cal.Core.Prelude.Long</code>)
		 *          the value to be returned if the fetched value is null
		 * @param columnN (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the (1-based) ordinal of the column for which the value will be fetched
		 * @param resultRow (CAL type: <code>Cal.Data.DataGems.ResultRow</code>)
		 *          a <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>Cal.Core.Prelude.Long</code>) 
		 *          the <code>Cal.Core.Prelude.Long</code> value from the specified column in the current <code>Cal.Data.DataGems.ResultRow</code>
		 */
		public static final SourceModel.Expr extractLongWithDefault(SourceModel.Expr defaultValue, SourceModel.Expr columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractLongWithDefault), defaultValue, columnN, resultRow});
		}

		/**
		 * @see #extractLongWithDefault(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param defaultValue
		 * @param columnN
		 * @param resultRow
		 * @return the SourceModel.Expr representing an application of extractLongWithDefault
		 */
		public static final SourceModel.Expr extractLongWithDefault(long defaultValue, int columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractLongWithDefault), SourceModel.Expr.makeLongValue(defaultValue), SourceModel.Expr.makeIntValue(columnN), resultRow});
		}

		/**
		 * Name binding for function: extractLongWithDefault.
		 * @see #extractLongWithDefault(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extractLongWithDefault = 
			QualifiedName.make(
				CAL_DataGems.MODULE_NAME, 
				"extractLongWithDefault");

		/**
		 * Extracts a <code>Cal.Core.Prelude.Maybe</code> <code>Cal.Core.Prelude.Boolean</code> value from the specified column in the <code>Cal.Data.DataGems.ResultRow</code>.
		 * Nothing will be returned if the fetched value is null.
		 * @param columnN (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the (1-based) ordinal of the column for which the value will be fetched
		 * @param resultRow (CAL type: <code>Cal.Data.DataGems.ResultRow</code>)
		 *          a <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Core.Prelude.Boolean</code>) 
		 *          the <code>Cal.Core.Prelude.Maybe</code> <code>Cal.Core.Prelude.Boolean</code> value from the specified column in the current <code>Cal.Data.DataGems.ResultRow</code>
		 */
		public static final SourceModel.Expr extractMaybeBoolean(SourceModel.Expr columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractMaybeBoolean), columnN, resultRow});
		}

		/**
		 * @see #extractMaybeBoolean(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param columnN
		 * @param resultRow
		 * @return the SourceModel.Expr representing an application of extractMaybeBoolean
		 */
		public static final SourceModel.Expr extractMaybeBoolean(int columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractMaybeBoolean), SourceModel.Expr.makeIntValue(columnN), resultRow});
		}

		/**
		 * Name binding for function: extractMaybeBoolean.
		 * @see #extractMaybeBoolean(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extractMaybeBoolean = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "extractMaybeBoolean");

		/**
		 * Extracts a byte array value from the specified column in the <code>Cal.Data.DataGems.ResultRow</code>.
		 * Nothing will be returned if the fetched value is null.
		 * @param columnN (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the (1-based) ordinal of the column for which the value will be fetched
		 * @param resultRow (CAL type: <code>Cal.Data.DataGems.ResultRow</code>)
		 *          a <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe (Cal.Collections.Array.Array Cal.Core.Prelude.Byte)</code>) 
		 *          the byte array value from the specified column in the current <code>Cal.Data.DataGems.ResultRow</code>
		 */
		public static final SourceModel.Expr extractMaybeBytes(SourceModel.Expr columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractMaybeBytes), columnN, resultRow});
		}

		/**
		 * @see #extractMaybeBytes(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param columnN
		 * @param resultRow
		 * @return the SourceModel.Expr representing an application of extractMaybeBytes
		 */
		public static final SourceModel.Expr extractMaybeBytes(int columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractMaybeBytes), SourceModel.Expr.makeIntValue(columnN), resultRow});
		}

		/**
		 * Name binding for function: extractMaybeBytes.
		 * @see #extractMaybeBytes(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extractMaybeBytes = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "extractMaybeBytes");

		/**
		 * Extracts a <code>Cal.Core.Prelude.Maybe</code> <code>Cal.Utilities.RelativeTime.JDate</code> value from the specified column in the <code>Cal.Data.DataGems.ResultRow</code>.
		 * Nothing will be returned if the fetched value is null.
		 * @param columnN (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the (1-based) ordinal of the column for which the value will be fetched
		 * @param resultRow (CAL type: <code>Cal.Data.DataGems.ResultRow</code>)
		 *          a <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Utilities.RelativeTime.JDate</code>) 
		 *          the <code>Cal.Core.Prelude.Maybe</code> <code>Cal.Utilities.RelativeTime.JDate</code> value from the specified column in the current <code>Cal.Data.DataGems.ResultRow</code>
		 */
		public static final SourceModel.Expr extractMaybeDate(SourceModel.Expr columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractMaybeDate), columnN, resultRow});
		}

		/**
		 * @see #extractMaybeDate(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param columnN
		 * @param resultRow
		 * @return the SourceModel.Expr representing an application of extractMaybeDate
		 */
		public static final SourceModel.Expr extractMaybeDate(int columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractMaybeDate), SourceModel.Expr.makeIntValue(columnN), resultRow});
		}

		/**
		 * Name binding for function: extractMaybeDate.
		 * @see #extractMaybeDate(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extractMaybeDate = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "extractMaybeDate");

		/**
		 * Extracts a <code>Cal.Core.Prelude.Maybe</code> <code>Cal.Core.Prelude.Decimal</code> value from the specified column in the <code>Cal.Data.DataGems.ResultRow</code>.
		 * Nothing will be returned if the fetched value is null.
		 * @param columnN (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the (1-based) ordinal of the column for which the value will be fetched
		 * @param resultRow (CAL type: <code>Cal.Data.DataGems.ResultRow</code>)
		 *          a <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Core.Prelude.Decimal</code>) 
		 *          the <code>Cal.Core.Prelude.Maybe</code> <code>Cal.Core.Prelude.Decimal</code> value from the specified column in the current <code>Cal.Data.DataGems.ResultRow</code>
		 */
		public static final SourceModel.Expr extractMaybeDecimal(SourceModel.Expr columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractMaybeDecimal), columnN, resultRow});
		}

		/**
		 * @see #extractMaybeDecimal(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param columnN
		 * @param resultRow
		 * @return the SourceModel.Expr representing an application of extractMaybeDecimal
		 */
		public static final SourceModel.Expr extractMaybeDecimal(int columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractMaybeDecimal), SourceModel.Expr.makeIntValue(columnN), resultRow});
		}

		/**
		 * Name binding for function: extractMaybeDecimal.
		 * @see #extractMaybeDecimal(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extractMaybeDecimal = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "extractMaybeDecimal");

		/**
		 * Extracts a <code>Cal.Core.Prelude.Maybe</code> <code>Cal.Core.Prelude.Double</code> value from the specified column in the <code>Cal.Data.DataGems.ResultRow</code>.
		 * Nothing will be returned if the fetched value is null.
		 * @param columnN (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the (1-based) ordinal of the column for which the value will be fetched
		 * @param resultRow (CAL type: <code>Cal.Data.DataGems.ResultRow</code>)
		 *          a <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Core.Prelude.Double</code>) 
		 *          the <code>Cal.Core.Prelude.Maybe</code> <code>Cal.Core.Prelude.Double</code> value from the specified column in the current <code>Cal.Data.DataGems.ResultRow</code>
		 */
		public static final SourceModel.Expr extractMaybeDouble(SourceModel.Expr columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractMaybeDouble), columnN, resultRow});
		}

		/**
		 * @see #extractMaybeDouble(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param columnN
		 * @param resultRow
		 * @return the SourceModel.Expr representing an application of extractMaybeDouble
		 */
		public static final SourceModel.Expr extractMaybeDouble(int columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractMaybeDouble), SourceModel.Expr.makeIntValue(columnN), resultRow});
		}

		/**
		 * Name binding for function: extractMaybeDouble.
		 * @see #extractMaybeDouble(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extractMaybeDouble = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "extractMaybeDouble");

		/**
		 * Extracts an <code>Cal.Core.Prelude.Maybe</code> <code>Cal.Core.Prelude.Int</code> value from the specified column in the <code>Cal.Data.DataGems.ResultRow</code>.
		 * Nothing will be returned if the fetched value is null.
		 * @param columnN (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the (1-based) ordinal of the column for which the value will be fetched
		 * @param resultRow (CAL type: <code>Cal.Data.DataGems.ResultRow</code>)
		 *          a <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Core.Prelude.Int</code>) 
		 *          the <code>Cal.Core.Prelude.Maybe</code> <code>Cal.Core.Prelude.Int</code> value from the specified column in the current <code>Cal.Data.DataGems.ResultRow</code>
		 */
		public static final SourceModel.Expr extractMaybeInt(SourceModel.Expr columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractMaybeInt), columnN, resultRow});
		}

		/**
		 * @see #extractMaybeInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param columnN
		 * @param resultRow
		 * @return the SourceModel.Expr representing an application of extractMaybeInt
		 */
		public static final SourceModel.Expr extractMaybeInt(int columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractMaybeInt), SourceModel.Expr.makeIntValue(columnN), resultRow});
		}

		/**
		 * Name binding for function: extractMaybeInt.
		 * @see #extractMaybeInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extractMaybeInt = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "extractMaybeInt");

		/**
		 * Extracts an <code>Cal.Core.Prelude.Maybe</code> <code>Cal.Core.Prelude.Long</code> value from the specified column in the <code>Cal.Data.DataGems.ResultRow</code>.
		 * Nothing will be returned if the fetched value is null.
		 * @param columnN (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the (1-based) ordinal of the column for which the value will be fetched
		 * @param resultRow (CAL type: <code>Cal.Data.DataGems.ResultRow</code>)
		 *          a <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Core.Prelude.Long</code>) 
		 *          the <code>Cal.Core.Prelude.Maybe</code> <code>Cal.Core.Prelude.Long</code> value from the specified column in the current <code>Cal.Data.DataGems.ResultRow</code>
		 */
		public static final SourceModel.Expr extractMaybeLong(SourceModel.Expr columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractMaybeLong), columnN, resultRow});
		}

		/**
		 * @see #extractMaybeLong(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param columnN
		 * @param resultRow
		 * @return the SourceModel.Expr representing an application of extractMaybeLong
		 */
		public static final SourceModel.Expr extractMaybeLong(int columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractMaybeLong), SourceModel.Expr.makeIntValue(columnN), resultRow});
		}

		/**
		 * Name binding for function: extractMaybeLong.
		 * @see #extractMaybeLong(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extractMaybeLong = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "extractMaybeLong");

		/**
		 * Extracts an Object value from the specified column in the <code>Cal.Data.DataGems.ResultRow</code>.
		 * Nothing will be returned if the fetched value is null.
		 * @param columnN (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the (1-based) ordinal of the column for which the value will be fetched
		 * @param resultRow (CAL type: <code>Cal.Data.DataGems.ResultRow</code>)
		 *          a <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Core.Prelude.JObject</code>) 
		 *          the Object value from the specified column in the current <code>Cal.Data.DataGems.ResultRow</code>
		 */
		public static final SourceModel.Expr extractMaybeObject(SourceModel.Expr columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractMaybeObject), columnN, resultRow});
		}

		/**
		 * @see #extractMaybeObject(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param columnN
		 * @param resultRow
		 * @return the SourceModel.Expr representing an application of extractMaybeObject
		 */
		public static final SourceModel.Expr extractMaybeObject(int columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractMaybeObject), SourceModel.Expr.makeIntValue(columnN), resultRow});
		}

		/**
		 * Name binding for function: extractMaybeObject.
		 * @see #extractMaybeObject(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extractMaybeObject = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "extractMaybeObject");

		/**
		 * Extracts a <code>Cal.Core.Prelude.Maybe</code> <code>Cal.Utilities.RelativeTime.RelativeDate</code> value from the specified column in the <code>Cal.Data.DataGems.ResultRow</code>.
		 * Nothing will be returned if the fetched value is null.
		 * @param columnN (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the (1-based) ordinal of the column for which the value will be fetched
		 * @param resultRow (CAL type: <code>Cal.Data.DataGems.ResultRow</code>)
		 *          a <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Utilities.RelativeTime.RelativeDate</code>) 
		 *          the <code>Cal.Core.Prelude.Maybe</code> <code>Cal.Utilities.RelativeTime.RelativeDate</code> value from the specified column in the current <code>Cal.Data.DataGems.ResultRow</code>
		 */
		public static final SourceModel.Expr extractMaybeRelativeDate(SourceModel.Expr columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractMaybeRelativeDate), columnN, resultRow});
		}

		/**
		 * @see #extractMaybeRelativeDate(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param columnN
		 * @param resultRow
		 * @return the SourceModel.Expr representing an application of extractMaybeRelativeDate
		 */
		public static final SourceModel.Expr extractMaybeRelativeDate(int columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractMaybeRelativeDate), SourceModel.Expr.makeIntValue(columnN), resultRow});
		}

		/**
		 * Name binding for function: extractMaybeRelativeDate.
		 * @see #extractMaybeRelativeDate(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extractMaybeRelativeDate = 
			QualifiedName.make(
				CAL_DataGems.MODULE_NAME, 
				"extractMaybeRelativeDate");

		/**
		 * Extracts a <code>Cal.Core.Prelude.Maybe</code> <code>Cal.Core.Prelude.String</code> value from the specified column in the <code>Cal.Data.DataGems.ResultRow</code>.
		 * Nothing will be returned if the fetched value is null.
		 * @param columnN (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the (1-based) ordinal of the column for which the value will be fetched
		 * @param resultRow (CAL type: <code>Cal.Data.DataGems.ResultRow</code>)
		 *          a <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Core.Prelude.String</code>) 
		 *          the <code>Cal.Core.Prelude.Maybe</code> <code>Cal.Core.Prelude.String</code> value from the specified column in the current <code>Cal.Data.DataGems.ResultRow</code>
		 */
		public static final SourceModel.Expr extractMaybeString(SourceModel.Expr columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractMaybeString), columnN, resultRow});
		}

		/**
		 * @see #extractMaybeString(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param columnN
		 * @param resultRow
		 * @return the SourceModel.Expr representing an application of extractMaybeString
		 */
		public static final SourceModel.Expr extractMaybeString(int columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractMaybeString), SourceModel.Expr.makeIntValue(columnN), resultRow});
		}

		/**
		 * Name binding for function: extractMaybeString.
		 * @see #extractMaybeString(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extractMaybeString = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "extractMaybeString");

		/**
		 * Extracts a <code>Cal.Core.Prelude.Maybe</code> <code>Cal.Utilities.Time.Time</code> value from the specified column in the <code>Cal.Data.DataGems.ResultRow</code>.
		 * Nothing will be returned if the fetched value is null.
		 * @param columnN (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the (1-based) ordinal of the column for which the value will be fetched
		 * @param resultRow (CAL type: <code>Cal.Data.DataGems.ResultRow</code>)
		 *          a <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Utilities.Time.Time</code>) 
		 *          the <code>Cal.Core.Prelude.Maybe</code> <code>Cal.Utilities.Time.Time</code> value from the specified column in the current <code>Cal.Data.DataGems.ResultRow</code>
		 */
		public static final SourceModel.Expr extractMaybeTime(SourceModel.Expr columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractMaybeTime), columnN, resultRow});
		}

		/**
		 * @see #extractMaybeTime(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param columnN
		 * @param resultRow
		 * @return the SourceModel.Expr representing an application of extractMaybeTime
		 */
		public static final SourceModel.Expr extractMaybeTime(int columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractMaybeTime), SourceModel.Expr.makeIntValue(columnN), resultRow});
		}

		/**
		 * Name binding for function: extractMaybeTime.
		 * @see #extractMaybeTime(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extractMaybeTime = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "extractMaybeTime");

		/**
		 * Extracts an Object value from the specified column in the <code>Cal.Data.DataGems.ResultRow</code>.
		 * @param columnN (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the (1-based) ordinal of the column for which the value will be fetched
		 * @param resultRow (CAL type: <code>Cal.Data.DataGems.ResultRow</code>)
		 *          a <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>Cal.Core.Prelude.JObject</code>) 
		 *          the Object value from the specified column in the current <code>Cal.Data.DataGems.ResultRow</code>
		 */
		public static final SourceModel.Expr extractObject(SourceModel.Expr columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractObject), columnN, resultRow});
		}

		/**
		 * @see #extractObject(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param columnN
		 * @param resultRow
		 * @return the SourceModel.Expr representing an application of extractObject
		 */
		public static final SourceModel.Expr extractObject(int columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractObject), SourceModel.Expr.makeIntValue(columnN), resultRow});
		}

		/**
		 * Name binding for function: extractObject.
		 * @see #extractObject(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extractObject = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "extractObject");

		/**
		 * Extracts an Object value from the specified column in the <code>Cal.Data.DataGems.ResultRow</code>.
		 * @param columnName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the name of the column for which the value will be fetched
		 * @param resultRow (CAL type: <code>Cal.Data.DataGems.ResultRow</code>)
		 *          a <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>Cal.Core.Prelude.JObject</code>) 
		 *          the Object value from the specified column in the current <code>Cal.Data.DataGems.ResultRow</code>
		 */
		public static final SourceModel.Expr extractObjectByName(SourceModel.Expr columnName, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractObjectByName), columnName, resultRow});
		}

		/**
		 * @see #extractObjectByName(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param columnName
		 * @param resultRow
		 * @return the SourceModel.Expr representing an application of extractObjectByName
		 */
		public static final SourceModel.Expr extractObjectByName(java.lang.String columnName, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractObjectByName), SourceModel.Expr.makeStringValue(columnName), resultRow});
		}

		/**
		 * Name binding for function: extractObjectByName.
		 * @see #extractObjectByName(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extractObjectByName = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "extractObjectByName");

		/**
		 * Extracts an Object value from the specified column in the <code>Cal.Data.DataGems.ResultRow</code>.
		 * The defaultValue will be returned if the fetched value is null.
		 * @param defaultValue (CAL type: <code>Cal.Core.Prelude.JObject</code>)
		 *          the value to be returned if the fetched value is null
		 * @param columnN (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the (1-based) ordinal of the column for which the value will be fetched
		 * @param resultRow (CAL type: <code>Cal.Data.DataGems.ResultRow</code>)
		 *          a <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>Cal.Core.Prelude.JObject</code>) 
		 *          the Object value from the specified column in the current <code>Cal.Data.DataGems.ResultRow</code>
		 */
		public static final SourceModel.Expr extractObjectWithDefault(SourceModel.Expr defaultValue, SourceModel.Expr columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractObjectWithDefault), defaultValue, columnN, resultRow});
		}

		/**
		 * @see #extractObjectWithDefault(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param defaultValue
		 * @param columnN
		 * @param resultRow
		 * @return the SourceModel.Expr representing an application of extractObjectWithDefault
		 */
		public static final SourceModel.Expr extractObjectWithDefault(SourceModel.Expr defaultValue, int columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractObjectWithDefault), defaultValue, SourceModel.Expr.makeIntValue(columnN), resultRow});
		}

		/**
		 * Name binding for function: extractObjectWithDefault.
		 * @see #extractObjectWithDefault(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extractObjectWithDefault = 
			QualifiedName.make(
				CAL_DataGems.MODULE_NAME, 
				"extractObjectWithDefault");

		/**
		 * Retrieves an array of some values (using the same extractor) from each row in the <code>Cal.Data.DataGems.ResultRow</code>.
		 * @param colOrdinals (CAL type: <code>Cal.Collections.Array.Array Cal.Core.Prelude.Int</code>)
		 *          an array of the (1-based) column ordinals for which values will be fetched
		 * @param extractFn (CAL type: <code>Cal.Core.Prelude.Typeable a => Cal.Core.Prelude.Int -> Cal.Data.DataGems.ResultRow -> a</code>)
		 *          a function to extract a value from a column of the <code>Cal.Data.DataGems.ResultRow</code>
		 * @param resultRow (CAL type: <code>Cal.Data.DataGems.ResultRow</code>)
		 *          a <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>Cal.Core.Prelude.Typeable a => Cal.Collections.Array.Array a</code>) 
		 *          an array of the specified values from the current <code>Cal.Data.DataGems.ResultRow</code>
		 */
		public static final SourceModel.Expr extractPartialArray(SourceModel.Expr colOrdinals, SourceModel.Expr extractFn, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractPartialArray), colOrdinals, extractFn, resultRow});
		}

		/**
		 * Name binding for function: extractPartialArray.
		 * @see #extractPartialArray(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extractPartialArray = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "extractPartialArray");

		/**
		 * Retrieves a list of some values (using the same extractor) from each row in the <code>Cal.Data.DataGems.ResultRow</code>.
		 * @param colOrdinals (CAL type: <code>[Cal.Core.Prelude.Int]</code>)
		 *          a list of the (1-based) column ordinals for which values will be fetched
		 * @param extractFn (CAL type: <code>Cal.Core.Prelude.Int -> Cal.Data.DataGems.ResultRow -> a</code>)
		 *          a function to extract a value from a column of the <code>Cal.Data.DataGems.ResultRow</code>
		 * @param resultRow (CAL type: <code>Cal.Data.DataGems.ResultRow</code>)
		 *          a <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>[a]</code>) 
		 *          a list of the specified values from the current <code>Cal.Data.DataGems.ResultRow</code>
		 */
		public static final SourceModel.Expr extractPartialList(SourceModel.Expr colOrdinals, SourceModel.Expr extractFn, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractPartialList), colOrdinals, extractFn, resultRow});
		}

		/**
		 * Name binding for function: extractPartialList.
		 * @see #extractPartialList(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extractPartialList = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "extractPartialList");

		/**
		 * Extracts a <code>Cal.Utilities.RelativeTime.RelativeDate</code> value from the specified column in the <code>Cal.Data.DataGems.ResultRow</code>.
		 * @param columnN (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the (1-based) ordinal of the column for which the value will be fetched
		 * @param resultRow (CAL type: <code>Cal.Data.DataGems.ResultRow</code>)
		 *          a <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>Cal.Utilities.RelativeTime.RelativeDate</code>) 
		 *          the <code>Cal.Utilities.RelativeTime.RelativeDate</code> value from the specified column in the current <code>Cal.Data.DataGems.ResultRow</code>
		 */
		public static final SourceModel.Expr extractRelativeDate(SourceModel.Expr columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractRelativeDate), columnN, resultRow});
		}

		/**
		 * @see #extractRelativeDate(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param columnN
		 * @param resultRow
		 * @return the SourceModel.Expr representing an application of extractRelativeDate
		 */
		public static final SourceModel.Expr extractRelativeDate(int columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractRelativeDate), SourceModel.Expr.makeIntValue(columnN), resultRow});
		}

		/**
		 * Name binding for function: extractRelativeDate.
		 * @see #extractRelativeDate(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extractRelativeDate = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "extractRelativeDate");

		/**
		 * Extracts a <code>Cal.Utilities.RelativeTime.RelativeDate</code> value from the specified column in the <code>Cal.Data.DataGems.ResultRow</code>.
		 * @param columnName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the name of the column for which the value will be fetched
		 * @param resultRow (CAL type: <code>Cal.Data.DataGems.ResultRow</code>)
		 *          a <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>Cal.Utilities.RelativeTime.RelativeDate</code>) 
		 *          the <code>Cal.Utilities.RelativeTime.RelativeDate</code> value from the specified column in the current <code>Cal.Data.DataGems.ResultRow</code>
		 */
		public static final SourceModel.Expr extractRelativeDateByName(SourceModel.Expr columnName, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractRelativeDateByName), columnName, resultRow});
		}

		/**
		 * @see #extractRelativeDateByName(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param columnName
		 * @param resultRow
		 * @return the SourceModel.Expr representing an application of extractRelativeDateByName
		 */
		public static final SourceModel.Expr extractRelativeDateByName(java.lang.String columnName, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractRelativeDateByName), SourceModel.Expr.makeStringValue(columnName), resultRow});
		}

		/**
		 * Name binding for function: extractRelativeDateByName.
		 * @see #extractRelativeDateByName(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extractRelativeDateByName = 
			QualifiedName.make(
				CAL_DataGems.MODULE_NAME, 
				"extractRelativeDateByName");

		/**
		 * Extracts a <code>Cal.Core.Prelude.String</code> value from the specified column in the <code>Cal.Data.DataGems.ResultRow</code>.
		 * @param columnN (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the (1-based) ordinal of the column for which the value will be fetched
		 * @param resultRow (CAL type: <code>Cal.Data.DataGems.ResultRow</code>)
		 *          a <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the <code>Cal.Core.Prelude.String</code> value from the specified column in the current <code>Cal.Data.DataGems.ResultRow</code>
		 */
		public static final SourceModel.Expr extractString(SourceModel.Expr columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractString), columnN, resultRow});
		}

		/**
		 * @see #extractString(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param columnN
		 * @param resultRow
		 * @return the SourceModel.Expr representing an application of extractString
		 */
		public static final SourceModel.Expr extractString(int columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractString), SourceModel.Expr.makeIntValue(columnN), resultRow});
		}

		/**
		 * Name binding for function: extractString.
		 * @see #extractString(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extractString = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "extractString");

		/**
		 * Extracts a <code>Cal.Core.Prelude.String</code> value from the specified column in the <code>Cal.Data.DataGems.ResultRow</code>.
		 * @param columnName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the name of the column for which the value will be fetched
		 * @param resultRow (CAL type: <code>Cal.Data.DataGems.ResultRow</code>)
		 *          a <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the <code>Cal.Core.Prelude.String</code> value from the specified column in the current <code>Cal.Data.DataGems.ResultRow</code>
		 */
		public static final SourceModel.Expr extractStringByName(SourceModel.Expr columnName, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractStringByName), columnName, resultRow});
		}

		/**
		 * @see #extractStringByName(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param columnName
		 * @param resultRow
		 * @return the SourceModel.Expr representing an application of extractStringByName
		 */
		public static final SourceModel.Expr extractStringByName(java.lang.String columnName, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractStringByName), SourceModel.Expr.makeStringValue(columnName), resultRow});
		}

		/**
		 * Name binding for function: extractStringByName.
		 * @see #extractStringByName(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extractStringByName = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "extractStringByName");

		/**
		 * Extracts a <code>Cal.Core.Prelude.String</code> value from the specified column in the <code>Cal.Data.DataGems.ResultRow</code>.
		 * The defaultValue will be returned if the fetched value is null.
		 * @param defaultValue (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the value to be returned if the fetched value is null
		 * @param columnN (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the (1-based) ordinal of the column for which the value will be fetched
		 * @param resultRow (CAL type: <code>Cal.Data.DataGems.ResultRow</code>)
		 *          a <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the <code>Cal.Core.Prelude.String</code> value from the specified column in the current <code>Cal.Data.DataGems.ResultRow</code>
		 */
		public static final SourceModel.Expr extractStringWithDefault(SourceModel.Expr defaultValue, SourceModel.Expr columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractStringWithDefault), defaultValue, columnN, resultRow});
		}

		/**
		 * @see #extractStringWithDefault(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param defaultValue
		 * @param columnN
		 * @param resultRow
		 * @return the SourceModel.Expr representing an application of extractStringWithDefault
		 */
		public static final SourceModel.Expr extractStringWithDefault(java.lang.String defaultValue, int columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractStringWithDefault), SourceModel.Expr.makeStringValue(defaultValue), SourceModel.Expr.makeIntValue(columnN), resultRow});
		}

		/**
		 * Name binding for function: extractStringWithDefault.
		 * @see #extractStringWithDefault(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extractStringWithDefault = 
			QualifiedName.make(
				CAL_DataGems.MODULE_NAME, 
				"extractStringWithDefault");

		/**
		 * Extracts a <code>Cal.Utilities.Time.Time</code> value from the specified column in the <code>Cal.Data.DataGems.ResultRow</code>.
		 * @param columnN (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the (1-based) ordinal of the column for which the value will be fetched
		 * @param resultRow (CAL type: <code>Cal.Data.DataGems.ResultRow</code>)
		 *          a <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>Cal.Utilities.Time.Time</code>) 
		 *          the <code>Cal.Utilities.Time.Time</code> value from the specified column in the current <code>Cal.Data.DataGems.ResultRow</code>
		 */
		public static final SourceModel.Expr extractTime(SourceModel.Expr columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractTime), columnN, resultRow});
		}

		/**
		 * @see #extractTime(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param columnN
		 * @param resultRow
		 * @return the SourceModel.Expr representing an application of extractTime
		 */
		public static final SourceModel.Expr extractTime(int columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractTime), SourceModel.Expr.makeIntValue(columnN), resultRow});
		}

		/**
		 * Name binding for function: extractTime.
		 * @see #extractTime(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extractTime = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "extractTime");

		/**
		 * Extracts a <code>Cal.Utilities.Time.Time</code> value from the specified column in the <code>Cal.Data.DataGems.ResultRow</code>.
		 * @param columnName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the name of the column for which the value will be fetched
		 * @param resultRow (CAL type: <code>Cal.Data.DataGems.ResultRow</code>)
		 *          a <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>Cal.Utilities.Time.Time</code>) 
		 *          the <code>Cal.Utilities.Time.Time</code> value from the specified column in the current <code>Cal.Data.DataGems.ResultRow</code>
		 */
		public static final SourceModel.Expr extractTimeByName(SourceModel.Expr columnName, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractTimeByName), columnName, resultRow});
		}

		/**
		 * @see #extractTimeByName(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param columnName
		 * @param resultRow
		 * @return the SourceModel.Expr representing an application of extractTimeByName
		 */
		public static final SourceModel.Expr extractTimeByName(java.lang.String columnName, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractTimeByName), SourceModel.Expr.makeStringValue(columnName), resultRow});
		}

		/**
		 * Name binding for function: extractTimeByName.
		 * @see #extractTimeByName(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extractTimeByName = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "extractTimeByName");

		/**
		 * Extracts a <code>Cal.Utilities.Time.Time</code> value from the specified column in the <code>Cal.Data.DataGems.ResultRow</code>.
		 * The defaultValue will be returned if the fetched value is null.
		 * @param defaultValue (CAL type: <code>Cal.Utilities.Time.Time</code>)
		 *          the value to be returned if the fetched value is null
		 * @param columnN (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the (1-based) ordinal of the column for which the value will be fetched
		 * @param resultRow (CAL type: <code>Cal.Data.DataGems.ResultRow</code>)
		 *          a <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>Cal.Utilities.Time.Time</code>) 
		 *          the <code>Cal.Utilities.Time.Time</code> value from the specified column in the current <code>Cal.Data.DataGems.ResultRow</code>
		 */
		public static final SourceModel.Expr extractTimeWithDefault(SourceModel.Expr defaultValue, SourceModel.Expr columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractTimeWithDefault), defaultValue, columnN, resultRow});
		}

		/**
		 * @see #extractTimeWithDefault(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param defaultValue
		 * @param columnN
		 * @param resultRow
		 * @return the SourceModel.Expr representing an application of extractTimeWithDefault
		 */
		public static final SourceModel.Expr extractTimeWithDefault(SourceModel.Expr defaultValue, int columnN, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractTimeWithDefault), defaultValue, SourceModel.Expr.makeIntValue(columnN), resultRow});
		}

		/**
		 * Name binding for function: extractTimeWithDefault.
		 * @see #extractTimeWithDefault(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extractTimeWithDefault = 
			QualifiedName.make(
				CAL_DataGems.MODULE_NAME, 
				"extractTimeWithDefault");

		/**
		 * Retrieves 2 values from each row in the <code>Cal.Data.DataGems.ResultRow</code>.
		 * @param extractFn1 (CAL type: <code>Cal.Data.DataGems.ResultRow -> a</code>)
		 *          a function to extract the 1st tuple value from the <code>Cal.Data.DataGems.ResultRow</code>
		 * @param extractFn2 (CAL type: <code>Cal.Data.DataGems.ResultRow -> b</code>)
		 *          a function to extract the 2nd tuple value from the <code>Cal.Data.DataGems.ResultRow</code>
		 * @param resultRow (CAL type: <code>Cal.Data.DataGems.ResultRow</code>)
		 *          a <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>(a, b)</code>) 
		 *          a tuple of values from the current <code>Cal.Data.DataGems.ResultRow</code>
		 */
		public static final SourceModel.Expr extractTuple2(SourceModel.Expr extractFn1, SourceModel.Expr extractFn2, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractTuple2), extractFn1, extractFn2, resultRow});
		}

		/**
		 * Name binding for function: extractTuple2.
		 * @see #extractTuple2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extractTuple2 = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "extractTuple2");

		/**
		 * Retrieves 3 values from each row in the <code>Cal.Data.DataGems.ResultRow</code>.
		 * @param extractFn1 (CAL type: <code>Cal.Data.DataGems.ResultRow -> a</code>)
		 *          a function to extract the 1st tuple value from the <code>Cal.Data.DataGems.ResultRow</code>
		 * @param extractFn2 (CAL type: <code>Cal.Data.DataGems.ResultRow -> b</code>)
		 *          a function to extract the 2nd tuple value from the <code>Cal.Data.DataGems.ResultRow</code>
		 * @param extractFn3 (CAL type: <code>Cal.Data.DataGems.ResultRow -> c</code>)
		 *          a function to extract the 3rd tuple value from the <code>Cal.Data.DataGems.ResultRow</code>
		 * @param resultRow (CAL type: <code>Cal.Data.DataGems.ResultRow</code>)
		 *          a <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>(a, b, c)</code>) 
		 *          a tuple of values from the current <code>Cal.Data.DataGems.ResultRow</code>
		 */
		public static final SourceModel.Expr extractTuple3(SourceModel.Expr extractFn1, SourceModel.Expr extractFn2, SourceModel.Expr extractFn3, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractTuple3), extractFn1, extractFn2, extractFn3, resultRow});
		}

		/**
		 * Name binding for function: extractTuple3.
		 * @see #extractTuple3(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extractTuple3 = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "extractTuple3");

		/**
		 * Retrieves 4 values from each row in the <code>Cal.Data.DataGems.ResultRow</code>.
		 * @param extractFn1 (CAL type: <code>Cal.Data.DataGems.ResultRow -> a</code>)
		 *          a function to extract the 1st tuple value from the <code>Cal.Data.DataGems.ResultRow</code>
		 * @param extractFn2 (CAL type: <code>Cal.Data.DataGems.ResultRow -> b</code>)
		 *          a function to extract the 2nd tuple value from the <code>Cal.Data.DataGems.ResultRow</code>
		 * @param extractFn3 (CAL type: <code>Cal.Data.DataGems.ResultRow -> c</code>)
		 *          a function to extract the 3rd tuple value from the <code>Cal.Data.DataGems.ResultRow</code>
		 * @param extractFn4 (CAL type: <code>Cal.Data.DataGems.ResultRow -> d</code>)
		 *          a function to extract the 4th tuple value from the <code>Cal.Data.DataGems.ResultRow</code>
		 * @param resultRow (CAL type: <code>Cal.Data.DataGems.ResultRow</code>)
		 *          a <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>(a, b, c, d)</code>) 
		 *          a tuple of values from the current <code>Cal.Data.DataGems.ResultRow</code>
		 */
		public static final SourceModel.Expr extractTuple4(SourceModel.Expr extractFn1, SourceModel.Expr extractFn2, SourceModel.Expr extractFn3, SourceModel.Expr extractFn4, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractTuple4), extractFn1, extractFn2, extractFn3, extractFn4, resultRow});
		}

		/**
		 * Name binding for function: extractTuple4.
		 * @see #extractTuple4(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extractTuple4 = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "extractTuple4");

		/**
		 * Retrieves 5 values from each row in the <code>Cal.Data.DataGems.ResultRow</code>.
		 * @param extractFn1 (CAL type: <code>Cal.Data.DataGems.ResultRow -> a</code>)
		 *          a function to extract the 1st tuple value from the <code>Cal.Data.DataGems.ResultRow</code>
		 * @param extractFn2 (CAL type: <code>Cal.Data.DataGems.ResultRow -> b</code>)
		 *          a function to extract the 2nd tuple value from the <code>Cal.Data.DataGems.ResultRow</code>
		 * @param extractFn3 (CAL type: <code>Cal.Data.DataGems.ResultRow -> c</code>)
		 *          a function to extract the 3rd tuple value from the <code>Cal.Data.DataGems.ResultRow</code>
		 * @param extractFn4 (CAL type: <code>Cal.Data.DataGems.ResultRow -> d</code>)
		 *          a function to extract the 4th tuple value from the <code>Cal.Data.DataGems.ResultRow</code>
		 * @param extractFn5 (CAL type: <code>Cal.Data.DataGems.ResultRow -> e</code>)
		 *          a function to extract the 5th tuple value from the <code>Cal.Data.DataGems.ResultRow</code>
		 * @param resultRow (CAL type: <code>Cal.Data.DataGems.ResultRow</code>)
		 *          a <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>(a, b, c, d, e)</code>) 
		 *          a tuple of values from the current <code>Cal.Data.DataGems.ResultRow</code>
		 */
		public static final SourceModel.Expr extractTuple5(SourceModel.Expr extractFn1, SourceModel.Expr extractFn2, SourceModel.Expr extractFn3, SourceModel.Expr extractFn4, SourceModel.Expr extractFn5, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractTuple5), extractFn1, extractFn2, extractFn3, extractFn4, extractFn5, resultRow});
		}

		/**
		 * Name binding for function: extractTuple5.
		 * @see #extractTuple5(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extractTuple5 = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "extractTuple5");

		/**
		 * Retrieves 6 values from each row in the <code>Cal.Data.DataGems.ResultRow</code>.
		 * @param extractFn1 (CAL type: <code>Cal.Data.DataGems.ResultRow -> a</code>)
		 *          a function to extract the 1st tuple value from the <code>Cal.Data.DataGems.ResultRow</code>
		 * @param extractFn2 (CAL type: <code>Cal.Data.DataGems.ResultRow -> b</code>)
		 *          a function to extract the 2nd tuple value from the <code>Cal.Data.DataGems.ResultRow</code>
		 * @param extractFn3 (CAL type: <code>Cal.Data.DataGems.ResultRow -> c</code>)
		 *          a function to extract the 3rd tuple value from the <code>Cal.Data.DataGems.ResultRow</code>
		 * @param extractFn4 (CAL type: <code>Cal.Data.DataGems.ResultRow -> d</code>)
		 *          a function to extract the 4th tuple value from the <code>Cal.Data.DataGems.ResultRow</code>
		 * @param extractFn5 (CAL type: <code>Cal.Data.DataGems.ResultRow -> e</code>)
		 *          a function to extract the 5th tuple value from the <code>Cal.Data.DataGems.ResultRow</code>
		 * @param extractFn6 (CAL type: <code>Cal.Data.DataGems.ResultRow -> f</code>)
		 *          a function to extract the 6th tuple value from the <code>Cal.Data.DataGems.ResultRow</code>
		 * @param resultRow (CAL type: <code>Cal.Data.DataGems.ResultRow</code>)
		 *          a <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>(a, b, c, d, e, f)</code>) 
		 *          a tuple of values from the current <code>Cal.Data.DataGems.ResultRow</code>
		 */
		public static final SourceModel.Expr extractTuple6(SourceModel.Expr extractFn1, SourceModel.Expr extractFn2, SourceModel.Expr extractFn3, SourceModel.Expr extractFn4, SourceModel.Expr extractFn5, SourceModel.Expr extractFn6, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractTuple6), extractFn1, extractFn2, extractFn3, extractFn4, extractFn5, extractFn6, resultRow});
		}

		/**
		 * Name binding for function: extractTuple6.
		 * @see #extractTuple6(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extractTuple6 = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "extractTuple6");

		/**
		 * Retrieves 7 values from each row in the <code>Cal.Data.DataGems.ResultRow</code>.
		 * @param extractFn1 (CAL type: <code>Cal.Data.DataGems.ResultRow -> a</code>)
		 *          a function to extract the 1st tuple value from the <code>Cal.Data.DataGems.ResultRow</code>
		 * @param extractFn2 (CAL type: <code>Cal.Data.DataGems.ResultRow -> b</code>)
		 *          a function to extract the 2nd tuple value from the <code>Cal.Data.DataGems.ResultRow</code>
		 * @param extractFn3 (CAL type: <code>Cal.Data.DataGems.ResultRow -> c</code>)
		 *          a function to extract the 3rd tuple value from the <code>Cal.Data.DataGems.ResultRow</code>
		 * @param extractFn4 (CAL type: <code>Cal.Data.DataGems.ResultRow -> d</code>)
		 *          a function to extract the 4th tuple value from the <code>Cal.Data.DataGems.ResultRow</code>
		 * @param extractFn5 (CAL type: <code>Cal.Data.DataGems.ResultRow -> e</code>)
		 *          a function to extract the 5th tuple value from the <code>Cal.Data.DataGems.ResultRow</code>
		 * @param extractFn6 (CAL type: <code>Cal.Data.DataGems.ResultRow -> f</code>)
		 *          a function to extract the 6th tuple value from the <code>Cal.Data.DataGems.ResultRow</code>
		 * @param extractFn7 (CAL type: <code>Cal.Data.DataGems.ResultRow -> g</code>)
		 *          a function to extract the 7th tuple value from the <code>Cal.Data.DataGems.ResultRow</code>
		 * @param resultRow (CAL type: <code>Cal.Data.DataGems.ResultRow</code>)
		 *          a <code>Cal.Data.DataGems.ResultRow</code>
		 * @return (CAL type: <code>(a, b, c, d, e, f, g)</code>) 
		 *          a tuple of values from the current <code>Cal.Data.DataGems.ResultRow</code>
		 */
		public static final SourceModel.Expr extractTuple7(SourceModel.Expr extractFn1, SourceModel.Expr extractFn2, SourceModel.Expr extractFn3, SourceModel.Expr extractFn4, SourceModel.Expr extractFn5, SourceModel.Expr extractFn6, SourceModel.Expr extractFn7, SourceModel.Expr resultRow) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extractTuple7), extractFn1, extractFn2, extractFn3, extractFn4, extractFn5, extractFn6, extractFn7, resultRow});
		}

		/**
		 * Name binding for function: extractTuple7.
		 * @see #extractTuple7(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extractTuple7 = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "extractTuple7");

		/**
		 * Folds a function over the rows of the <code>Cal.Data.DataGems.ResultSet</code>.
		 * @param foldFn (CAL type: <code>Cal.Data.DataGems.ResultRow -> a -> a</code>)
		 *          a folding function to be applied for each <code>Cal.Data.DataGems.ResultRow</code>
		 * @param initValue (CAL type: <code>a</code>)
		 *          the starting value for the folding
		 * @param resultSet (CAL type: <code>Cal.Data.DataGems.ResultSet</code>)
		 *          a <code>Cal.Data.DataGems.ResultSet</code>
		 * @return (CAL type: <code>a</code>) 
		 *          the result of applying the folding function over the <code>Cal.Data.DataGems.ResultSet</code> rows
		 */
		public static final SourceModel.Expr foldStrictOverResultSet(SourceModel.Expr foldFn, SourceModel.Expr initValue, SourceModel.Expr resultSet) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.foldStrictOverResultSet), foldFn, initValue, resultSet});
		}

		/**
		 * Name binding for function: foldStrictOverResultSet.
		 * @see #foldStrictOverResultSet(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName foldStrictOverResultSet = 
			QualifiedName.make(
				CAL_DataGems.MODULE_NAME, 
				"foldStrictOverResultSet");

		/**
		 * Returns the (1-based) ordinal of the specified column.
		 * An error is thrown if the column cannot be found in the <code>Cal.Data.DataGems.ResultSet</code>.
		 * @param resultSet (CAL type: <code>Cal.Data.DataGems.ResultSet</code>)
		 *          a <code>Cal.Data.DataGems.ResultSet</code>
		 * @param columnName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the name of a column
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the index of the <code>Cal.Data.DataGems.ResultSet</code> column with the specified name
		 */
		public static final SourceModel.Expr getColumnIndex(SourceModel.Expr resultSet, SourceModel.Expr columnName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getColumnIndex), resultSet, columnName});
		}

		/**
		 * @see #getColumnIndex(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param resultSet
		 * @param columnName
		 * @return the SourceModel.Expr representing an application of getColumnIndex
		 */
		public static final SourceModel.Expr getColumnIndex(SourceModel.Expr resultSet, java.lang.String columnName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getColumnIndex), resultSet, SourceModel.Expr.makeStringValue(columnName)});
		}

		/**
		 * Name binding for function: getColumnIndex.
		 * @see #getColumnIndex(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getColumnIndex = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "getColumnIndex");

		/**
		 * Closes a JDBC connection.
		 * This should be used with caution as it will modify the JDBC connection provided.
		 * @param connection (CAL type: <code>Cal.Data.DataGems.JDBCConnection</code>)
		 *          the connection to be closed
		 * @return (CAL type: <code>()</code>) 
		 */
		public static final SourceModel.Expr jdbcCloseConnection(SourceModel.Expr connection) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jdbcCloseConnection), connection});
		}

		/**
		 * Name binding for function: jdbcCloseConnection.
		 * @see #jdbcCloseConnection(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jdbcCloseConnection = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "jdbcCloseConnection");

		/**
		 * Close a <code>Cal.Data.DataGems.ResultSet</code>.
		 * @param resultSet (CAL type: <code>Cal.Data.DataGems.ResultSet</code>)
		 *          a <code>Cal.Data.DataGems.ResultSet</code>
		 * @return (CAL type: <code>()</code>) 
		 */
		public static final SourceModel.Expr jdbcCloseResultSet(SourceModel.Expr resultSet) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jdbcCloseResultSet), resultSet});
		}

		/**
		 * Name binding for function: jdbcCloseResultSet.
		 * @see #jdbcCloseResultSet(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jdbcCloseResultSet = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "jdbcCloseResultSet");

		/**
		 * Connect to a JDBC database.
		 * Use this if you don't know the driver is already loaded.
		 * @param url (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the JDBC connection URL
		 * @param userName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the user ID for the database
		 * @param password (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the password for the database
		 * @return (CAL type: <code>Cal.Data.DataGems.JDBCConnection</code>) 
		 *          a connection to the database
		 */
		public static final SourceModel.Expr jdbcConnection(SourceModel.Expr url, SourceModel.Expr userName, SourceModel.Expr password) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jdbcConnection), url, userName, password});
		}

		/**
		 * @see #jdbcConnection(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param url
		 * @param userName
		 * @param password
		 * @return the SourceModel.Expr representing an application of jdbcConnection
		 */
		public static final SourceModel.Expr jdbcConnection(java.lang.String url, java.lang.String userName, java.lang.String password) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jdbcConnection), SourceModel.Expr.makeStringValue(url), SourceModel.Expr.makeStringValue(userName), SourceModel.Expr.makeStringValue(password)});
		}

		/**
		 * Name binding for function: jdbcConnection.
		 * @see #jdbcConnection(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jdbcConnection = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "jdbcConnection");

		/**
		 * Use this if you don't know the driver is already loaded.
		 * @param driverClass (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the full class name of the JDBC driver
		 * @param url (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the JDBC connection URL
		 * @param userName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the user ID for the database
		 * @param password (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the password for the database
		 * @return (CAL type: <code>Cal.Data.DataGems.JDBCConnection</code>) 
		 *          a connection to the database
		 */
		public static final SourceModel.Expr jdbcConnectionWithDriverCheck(SourceModel.Expr driverClass, SourceModel.Expr url, SourceModel.Expr userName, SourceModel.Expr password) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jdbcConnectionWithDriverCheck), driverClass, url, userName, password});
		}

		/**
		 * @see #jdbcConnectionWithDriverCheck(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param driverClass
		 * @param url
		 * @param userName
		 * @param password
		 * @return the SourceModel.Expr representing an application of jdbcConnectionWithDriverCheck
		 */
		public static final SourceModel.Expr jdbcConnectionWithDriverCheck(java.lang.String driverClass, java.lang.String url, java.lang.String userName, java.lang.String password) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jdbcConnectionWithDriverCheck), SourceModel.Expr.makeStringValue(driverClass), SourceModel.Expr.makeStringValue(url), SourceModel.Expr.makeStringValue(userName), SourceModel.Expr.makeStringValue(password)});
		}

		/**
		 * Name binding for function: jdbcConnectionWithDriverCheck.
		 * @see #jdbcConnectionWithDriverCheck(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jdbcConnectionWithDriverCheck = 
			QualifiedName.make(
				CAL_DataGems.MODULE_NAME, 
				"jdbcConnectionWithDriverCheck");

		/**
		 * Load a JDBC driver.
		 * @param driverClass (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the full class name of the JDBC driver
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the driver loaded successfully, <code>Cal.Core.Prelude.False</code> otherwise
		 */
		public static final SourceModel.Expr jdbcDriverLoad(SourceModel.Expr driverClass) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jdbcDriverLoad), driverClass});
		}

		/**
		 * @see #jdbcDriverLoad(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param driverClass
		 * @return the SourceModel.Expr representing an application of jdbcDriverLoad
		 */
		public static final SourceModel.Expr jdbcDriverLoad(java.lang.String driverClass) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jdbcDriverLoad), SourceModel.Expr.makeStringValue(driverClass)});
		}

		/**
		 * Name binding for function: jdbcDriverLoad.
		 * @see #jdbcDriverLoad(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jdbcDriverLoad = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "jdbcDriverLoad");

		/**
		 * Gets the auto-commit flag for a JDBC connection.
		 * @param connection (CAL type: <code>Cal.Data.DataGems.JDBCConnection</code>)
		 *          a JDBC connection
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          the auto-commit flag for the connection
		 */
		public static final SourceModel.Expr jdbcGetAutoCommit(SourceModel.Expr connection) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jdbcGetAutoCommit), connection});
		}

		/**
		 * Name binding for function: jdbcGetAutoCommit.
		 * @see #jdbcGetAutoCommit(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jdbcGetAutoCommit = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "jdbcGetAutoCommit");

		/**
		 * Retrieves a <code>Cal.Data.DataGems.ResultSet</code> of table column information from a connection.
		 * @param connection (CAL type: <code>Cal.Data.DataGems.JDBCConnection</code>)
		 *          a database connection
		 * @param tableName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the name of the table for which column info will be retrieved
		 * @return (CAL type: <code>Cal.Data.DataGems.ResultSet</code>) 
		 *          a <code>Cal.Data.DataGems.ResultSet</code> of column information for the specified database table
		 */
		public static final SourceModel.Expr jdbcGetColumnsInfo(SourceModel.Expr connection, SourceModel.Expr tableName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jdbcGetColumnsInfo), connection, tableName});
		}

		/**
		 * @see #jdbcGetColumnsInfo(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param connection
		 * @param tableName
		 * @return the SourceModel.Expr representing an application of jdbcGetColumnsInfo
		 */
		public static final SourceModel.Expr jdbcGetColumnsInfo(SourceModel.Expr connection, java.lang.String tableName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jdbcGetColumnsInfo), connection, SourceModel.Expr.makeStringValue(tableName)});
		}

		/**
		 * Name binding for function: jdbcGetColumnsInfo.
		 * @see #jdbcGetColumnsInfo(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jdbcGetColumnsInfo = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "jdbcGetColumnsInfo");

		/**
		 * Retrieves a list of table names from the connection.
		 * System tables will be excluded from the results.
		 * @param connection (CAL type: <code>Cal.Data.DataGems.JDBCConnection</code>)
		 *          a database connection
		 * @return (CAL type: <code>[Cal.Core.Prelude.String]</code>) 
		 *          a list of table names for the connection
		 */
		public static final SourceModel.Expr jdbcGetConnectionTableNames(SourceModel.Expr connection) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jdbcGetConnectionTableNames), connection});
		}

		/**
		 * Name binding for function: jdbcGetConnectionTableNames.
		 * @see #jdbcGetConnectionTableNames(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jdbcGetConnectionTableNames = 
			QualifiedName.make(
				CAL_DataGems.MODULE_NAME, 
				"jdbcGetConnectionTableNames");

		/**
		 * Retrieves a list of table names from the connection.
		 * System tables will be excluded from the results.
		 * @param connection (CAL type: <code>Cal.Data.DataGems.JDBCConnection</code>)
		 *          a database connection
		 * @param catalogName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the catalog name for the tables of interest, or an empty string to not restrict the table catalog
		 * @param schemaPattern (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the schema name pattern for the tables of interest, or an empty string to not restrict the table schema
		 * @param tablePattern (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the table name pattern for the tables of interest, or an empty string to not restrict by table name
		 * @return (CAL type: <code>[Cal.Core.Prelude.String]</code>) 
		 *          a list of table names for the connection
		 */
		public static final SourceModel.Expr jdbcGetConnectionTableNamesWithFilters(SourceModel.Expr connection, SourceModel.Expr catalogName, SourceModel.Expr schemaPattern, SourceModel.Expr tablePattern) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jdbcGetConnectionTableNamesWithFilters), connection, catalogName, schemaPattern, tablePattern});
		}

		/**
		 * @see #jdbcGetConnectionTableNamesWithFilters(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param connection
		 * @param catalogName
		 * @param schemaPattern
		 * @param tablePattern
		 * @return the SourceModel.Expr representing an application of jdbcGetConnectionTableNamesWithFilters
		 */
		public static final SourceModel.Expr jdbcGetConnectionTableNamesWithFilters(SourceModel.Expr connection, java.lang.String catalogName, java.lang.String schemaPattern, java.lang.String tablePattern) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jdbcGetConnectionTableNamesWithFilters), connection, SourceModel.Expr.makeStringValue(catalogName), SourceModel.Expr.makeStringValue(schemaPattern), SourceModel.Expr.makeStringValue(tablePattern)});
		}

		/**
		 * Name binding for function: jdbcGetConnectionTableNamesWithFilters.
		 * @see #jdbcGetConnectionTableNamesWithFilters(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jdbcGetConnectionTableNamesWithFilters = 
			QualifiedName.make(
				CAL_DataGems.MODULE_NAME, 
				"jdbcGetConnectionTableNamesWithFilters");

		/**
		 * Retrieves information about the fields in a database table, including:  the field names, 
		 * their value types, and whether the type is a 'long' one.
		 * @param connection (CAL type: <code>Cal.Data.DataGems.JDBCConnection</code>)
		 *          a database connection
		 * @param tableName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the name of a table in the database
		 * @return (CAL type: <code>[{columnName :: Cal.Core.Prelude.String, columnSize :: Cal.Core.Prelude.Int, isLongType :: Cal.Core.Prelude.Boolean, valueType :: Cal.Utilities.ValueType.ValueType}]</code>) 
		 *          a list of records with field information for the specified table
		 */
		public static final SourceModel.Expr jdbcGetTableFieldInfo(SourceModel.Expr connection, SourceModel.Expr tableName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jdbcGetTableFieldInfo), connection, tableName});
		}

		/**
		 * @see #jdbcGetTableFieldInfo(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param connection
		 * @param tableName
		 * @return the SourceModel.Expr representing an application of jdbcGetTableFieldInfo
		 */
		public static final SourceModel.Expr jdbcGetTableFieldInfo(SourceModel.Expr connection, java.lang.String tableName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jdbcGetTableFieldInfo), connection, SourceModel.Expr.makeStringValue(tableName)});
		}

		/**
		 * Name binding for function: jdbcGetTableFieldInfo.
		 * @see #jdbcGetTableFieldInfo(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jdbcGetTableFieldInfo = 
			QualifiedName.make(
				CAL_DataGems.MODULE_NAME, 
				"jdbcGetTableFieldInfo");

		/**
		 * Retrieves a list of field names for the specified table in the connection.
		 * @param connection (CAL type: <code>Cal.Data.DataGems.JDBCConnection</code>)
		 *          a database connection
		 * @param tableName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the name of a table in the database
		 * @return (CAL type: <code>[Cal.Core.Prelude.String]</code>) 
		 *          the list of field names for the database table
		 */
		public static final SourceModel.Expr jdbcGetTableFieldNames(SourceModel.Expr connection, SourceModel.Expr tableName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jdbcGetTableFieldNames), connection, tableName});
		}

		/**
		 * @see #jdbcGetTableFieldNames(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param connection
		 * @param tableName
		 * @return the SourceModel.Expr representing an application of jdbcGetTableFieldNames
		 */
		public static final SourceModel.Expr jdbcGetTableFieldNames(SourceModel.Expr connection, java.lang.String tableName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jdbcGetTableFieldNames), connection, SourceModel.Expr.makeStringValue(tableName)});
		}

		/**
		 * Name binding for function: jdbcGetTableFieldNames.
		 * @see #jdbcGetTableFieldNames(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jdbcGetTableFieldNames = 
			QualifiedName.make(
				CAL_DataGems.MODULE_NAME, 
				"jdbcGetTableFieldNames");

		/**
		 * Retrieves a <code>Cal.Data.DataGems.ResultSet</code> of foreign key info for a table.
		 * @param connection (CAL type: <code>Cal.Data.DataGems.JDBCConnection</code>)
		 *          a database connection
		 * @param tableName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the name of the table for which foreign key info will be retrieved
		 * @return (CAL type: <code>Cal.Data.DataGems.ResultSet</code>) 
		 *          a <code>Cal.Data.DataGems.ResultSet</code> of foreign key information for the specified database table
		 */
		public static final SourceModel.Expr jdbcGetTableForeignKeyInfo(SourceModel.Expr connection, SourceModel.Expr tableName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jdbcGetTableForeignKeyInfo), connection, tableName});
		}

		/**
		 * @see #jdbcGetTableForeignKeyInfo(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param connection
		 * @param tableName
		 * @return the SourceModel.Expr representing an application of jdbcGetTableForeignKeyInfo
		 */
		public static final SourceModel.Expr jdbcGetTableForeignKeyInfo(SourceModel.Expr connection, java.lang.String tableName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jdbcGetTableForeignKeyInfo), connection, SourceModel.Expr.makeStringValue(tableName)});
		}

		/**
		 * Name binding for function: jdbcGetTableForeignKeyInfo.
		 * @see #jdbcGetTableForeignKeyInfo(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jdbcGetTableForeignKeyInfo = 
			QualifiedName.make(
				CAL_DataGems.MODULE_NAME, 
				"jdbcGetTableForeignKeyInfo");

		/**
		 * Retrieves a <code>Cal.Data.DataGems.ResultSet</code> of index info for a table.
		 * @param connection (CAL type: <code>Cal.Data.DataGems.JDBCConnection</code>)
		 *          a database connection
		 * @param tableName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the name of the table for which index info will be retrieved
		 * @param uniqueOnly (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 *          if True, then only info about unique indexes will be returned;
		 * if False, then all index info for the table will be returned
		 * @return (CAL type: <code>Cal.Data.DataGems.ResultSet</code>) 
		 *          a <code>Cal.Data.DataGems.ResultSet</code> of index information for the specified database table
		 */
		public static final SourceModel.Expr jdbcGetTableIndexInfo(SourceModel.Expr connection, SourceModel.Expr tableName, SourceModel.Expr uniqueOnly) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jdbcGetTableIndexInfo), connection, tableName, uniqueOnly});
		}

		/**
		 * @see #jdbcGetTableIndexInfo(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param connection
		 * @param tableName
		 * @param uniqueOnly
		 * @return the SourceModel.Expr representing an application of jdbcGetTableIndexInfo
		 */
		public static final SourceModel.Expr jdbcGetTableIndexInfo(SourceModel.Expr connection, java.lang.String tableName, boolean uniqueOnly) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jdbcGetTableIndexInfo), connection, SourceModel.Expr.makeStringValue(tableName), SourceModel.Expr.makeBooleanValue(uniqueOnly)});
		}

		/**
		 * Name binding for function: jdbcGetTableIndexInfo.
		 * @see #jdbcGetTableIndexInfo(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jdbcGetTableIndexInfo = 
			QualifiedName.make(
				CAL_DataGems.MODULE_NAME, 
				"jdbcGetTableIndexInfo");

		/**
		 * Retrieves a <code>Cal.Data.DataGems.ResultSet</code> of primary key columns for a table.
		 * @param connection (CAL type: <code>Cal.Data.DataGems.JDBCConnection</code>)
		 *          a database connection
		 * @param tableName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the name of the table for which primary key info will be retrieved
		 * @return (CAL type: <code>Cal.Data.DataGems.ResultSet</code>) 
		 *          a <code>Cal.Data.DataGems.ResultSet</code> of primary key information for the specified database table
		 */
		public static final SourceModel.Expr jdbcGetTablePrimaryKeyInfo(SourceModel.Expr connection, SourceModel.Expr tableName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jdbcGetTablePrimaryKeyInfo), connection, tableName});
		}

		/**
		 * @see #jdbcGetTablePrimaryKeyInfo(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param connection
		 * @param tableName
		 * @return the SourceModel.Expr representing an application of jdbcGetTablePrimaryKeyInfo
		 */
		public static final SourceModel.Expr jdbcGetTablePrimaryKeyInfo(SourceModel.Expr connection, java.lang.String tableName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jdbcGetTablePrimaryKeyInfo), connection, SourceModel.Expr.makeStringValue(tableName)});
		}

		/**
		 * Name binding for function: jdbcGetTablePrimaryKeyInfo.
		 * @see #jdbcGetTablePrimaryKeyInfo(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jdbcGetTablePrimaryKeyInfo = 
			QualifiedName.make(
				CAL_DataGems.MODULE_NAME, 
				"jdbcGetTablePrimaryKeyInfo");

		/**
		 * Retrieves a <code>Cal.Data.DataGems.ResultSet</code> of table information from a connection.
		 * @param connection (CAL type: <code>Cal.Data.DataGems.JDBCConnection</code>)
		 *          a database connection
		 * @return (CAL type: <code>Cal.Data.DataGems.ResultSet</code>) 
		 *          a <code>Cal.Data.DataGems.ResultSet</code> of table information from the database connection
		 */
		public static final SourceModel.Expr jdbcGetTablesInfo(SourceModel.Expr connection) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jdbcGetTablesInfo), connection});
		}

		/**
		 * Name binding for function: jdbcGetTablesInfo.
		 * @see #jdbcGetTablesInfo(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jdbcGetTablesInfo = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "jdbcGetTablesInfo");

		/**
		 * Retrieves a <code>Cal.Data.DataGems.ResultSet</code> of table information from a connection.
		 * @param connection (CAL type: <code>Cal.Data.DataGems.JDBCConnection</code>)
		 *          a database connection
		 * @param catalogName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the catalog name for the tables of interest, or an empty string to not restrict the table catalog
		 * @param schemaPattern (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the schema name pattern for the tables of interest, or an empty string to not restrict the table schema
		 * @param tablePattern (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the table name pattern for the tables of interest, or an empty string to not restrict by table name
		 * @return (CAL type: <code>Cal.Data.DataGems.ResultSet</code>) 
		 *          a <code>Cal.Data.DataGems.ResultSet</code> of table information from the database connection
		 */
		public static final SourceModel.Expr jdbcGetTablesInfoWithFilters(SourceModel.Expr connection, SourceModel.Expr catalogName, SourceModel.Expr schemaPattern, SourceModel.Expr tablePattern) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jdbcGetTablesInfoWithFilters), connection, catalogName, schemaPattern, tablePattern});
		}

		/**
		 * @see #jdbcGetTablesInfoWithFilters(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param connection
		 * @param catalogName
		 * @param schemaPattern
		 * @param tablePattern
		 * @return the SourceModel.Expr representing an application of jdbcGetTablesInfoWithFilters
		 */
		public static final SourceModel.Expr jdbcGetTablesInfoWithFilters(SourceModel.Expr connection, java.lang.String catalogName, java.lang.String schemaPattern, java.lang.String tablePattern) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jdbcGetTablesInfoWithFilters), connection, SourceModel.Expr.makeStringValue(catalogName), SourceModel.Expr.makeStringValue(schemaPattern), SourceModel.Expr.makeStringValue(tablePattern)});
		}

		/**
		 * Name binding for function: jdbcGetTablesInfoWithFilters.
		 * @see #jdbcGetTablesInfoWithFilters(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jdbcGetTablesInfoWithFilters = 
			QualifiedName.make(
				CAL_DataGems.MODULE_NAME, 
				"jdbcGetTablesInfoWithFilters");

		/**
		 * Execute a SQL SELECT statement against the specified connection to produce a <code>Cal.Data.DataGems.ResultSet</code>.
		 * @param connection (CAL type: <code>Cal.Data.DataGems.JDBCConnection</code>)
		 *          a connection to the database against which the query will be performed
		 * @param sql (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the SQL SELECT statement to be executed
		 * @return (CAL type: <code>Cal.Data.DataGems.ResultSet</code>) 
		 *          the results of the database query
		 */
		public static final SourceModel.Expr jdbcQueryToResultSet(SourceModel.Expr connection, SourceModel.Expr sql) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jdbcQueryToResultSet), connection, sql});
		}

		/**
		 * @see #jdbcQueryToResultSet(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param connection
		 * @param sql
		 * @return the SourceModel.Expr representing an application of jdbcQueryToResultSet
		 */
		public static final SourceModel.Expr jdbcQueryToResultSet(SourceModel.Expr connection, java.lang.String sql) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jdbcQueryToResultSet), connection, SourceModel.Expr.makeStringValue(sql)});
		}

		/**
		 * Name binding for function: jdbcQueryToResultSet.
		 * @see #jdbcQueryToResultSet(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jdbcQueryToResultSet = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "jdbcQueryToResultSet");

		/**
		 * Set auto-commit flag for a JDBC connection.
		 * This should be used with caution as it will modify the JDBC connection provided.
		 * @param connection (CAL type: <code>Cal.Data.DataGems.JDBCConnection</code>)
		 *          a JDBC connection
		 * @param autoCommitFlag (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 *          the new setting for the auto-commit flag for the connection
		 * @return (CAL type: <code>Cal.Data.DataGems.JDBCConnection</code>) 
		 *          the original JDBC connection with the new setting
		 */
		public static final SourceModel.Expr jdbcSetAutoCommit(SourceModel.Expr connection, SourceModel.Expr autoCommitFlag) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jdbcSetAutoCommit), connection, autoCommitFlag});
		}

		/**
		 * @see #jdbcSetAutoCommit(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param connection
		 * @param autoCommitFlag
		 * @return the SourceModel.Expr representing an application of jdbcSetAutoCommit
		 */
		public static final SourceModel.Expr jdbcSetAutoCommit(SourceModel.Expr connection, boolean autoCommitFlag) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jdbcSetAutoCommit), connection, SourceModel.Expr.makeBooleanValue(autoCommitFlag)});
		}

		/**
		 * Name binding for function: jdbcSetAutoCommit.
		 * @see #jdbcSetAutoCommit(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jdbcSetAutoCommit = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "jdbcSetAutoCommit");

		/**
		 * Adds a batch with a set of parameters to the prepared statement.
		 * @param preparedStmt (CAL type: <code>Cal.Data.DataGems.JDBCPreparedStatement</code>)
		 *          a prepared statement
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the batch was added successfully
		 */
		public static final SourceModel.Expr preparedStatementAddBatch(SourceModel.Expr preparedStmt) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.preparedStatementAddBatch), preparedStmt});
		}

		/**
		 * Name binding for function: preparedStatementAddBatch.
		 * @see #preparedStatementAddBatch(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName preparedStatementAddBatch = 
			QualifiedName.make(
				CAL_DataGems.MODULE_NAME, 
				"preparedStatementAddBatch");

		/**
		 * Executes the given batched prepared statement.
		 * @param stmt (CAL type: <code>Cal.Data.DataGems.JDBCPreparedStatement</code>)
		 *          Invokes executeBatch on this statement.
		 * // TODO * &#64;return Array Int
		 * @return (CAL type: <code>Cal.Collections.Array.Array Cal.Core.Prelude.Int</code>) 
		 */
		public static final SourceModel.Expr preparedStatementExecuteBatch(SourceModel.Expr stmt) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.preparedStatementExecuteBatch), stmt});
		}

		/**
		 * Name binding for function: preparedStatementExecuteBatch.
		 * @see #preparedStatementExecuteBatch(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName preparedStatementExecuteBatch = 
			QualifiedName.make(
				CAL_DataGems.MODULE_NAME, 
				"preparedStatementExecuteBatch");

		/**
		 * Executes a prepared statement which performs an update operation.
		 * @param preparedStmt (CAL type: <code>Cal.Data.DataGems.JDBCPreparedStatement</code>)
		 *          a prepared statement
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the number of rows affected by the update operation
		 */
		public static final SourceModel.Expr preparedStatementExecuteUpdate(SourceModel.Expr preparedStmt) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.preparedStatementExecuteUpdate), preparedStmt});
		}

		/**
		 * Name binding for function: preparedStatementExecuteUpdate.
		 * @see #preparedStatementExecuteUpdate(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName preparedStatementExecuteUpdate = 
			QualifiedName.make(
				CAL_DataGems.MODULE_NAME, 
				"preparedStatementExecuteUpdate");

		/**
		 * Retrieves the SQL from a prepared statement.
		 * @param preparedStmt (CAL type: <code>Cal.Data.DataGems.JDBCPreparedStatement</code>)
		 *          a prepared statement
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the SQL for the prepared statement
		 */
		public static final SourceModel.Expr preparedStatementGetSqlStatement(SourceModel.Expr preparedStmt) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.preparedStatementGetSqlStatement), preparedStmt});
		}

		/**
		 * Name binding for function: preparedStatementGetSqlStatement.
		 * @see #preparedStatementGetSqlStatement(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName preparedStatementGetSqlStatement = 
			QualifiedName.make(
				CAL_DataGems.MODULE_NAME, 
				"preparedStatementGetSqlStatement");

		/**
		 * Retrieves the time taken to execute a prepared statement.
		 * @param preparedStmt (CAL type: <code>Cal.Data.DataGems.JDBCPreparedStatement</code>)
		 *          a prepared statement
		 * @return (CAL type: <code>Cal.Core.Prelude.Long</code>) 
		 *          the time taken to execute the prepared statement
		 */
		public static final SourceModel.Expr preparedStatementGetTotalExecTime(SourceModel.Expr preparedStmt) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.preparedStatementGetTotalExecTime), preparedStmt});
		}

		/**
		 * Name binding for function: preparedStatementGetTotalExecTime.
		 * @see #preparedStatementGetTotalExecTime(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName preparedStatementGetTotalExecTime = 
			QualifiedName.make(
				CAL_DataGems.MODULE_NAME, 
				"preparedStatementGetTotalExecTime");

		/**
		 * Exposes a JDBC resultset as a <code>Cal.Data.DataGems.ResultSet</code>.
		 * @param jdbcResultSet (CAL type: <code>Cal.Data.DataGems.JDBCResultSet</code>)
		 *          a JDBC resultset
		 * @return (CAL type: <code>Cal.Data.DataGems.ResultSet</code>) 
		 *          a <code>Cal.Data.DataGems.ResultSet</code> based on the JDBC resultset
		 */
		public static final SourceModel.Expr resultSetFromJDBC(SourceModel.Expr jdbcResultSet) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.resultSetFromJDBC), jdbcResultSet});
		}

		/**
		 * Name binding for function: resultSetFromJDBC.
		 * @see #resultSetFromJDBC(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName resultSetFromJDBC = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "resultSetFromJDBC");

		/**
		 * Returns the names and value types of the columns in a <code>Cal.Data.DataGems.ResultSet</code>.
		 * @param resultSet (CAL type: <code>Cal.Data.DataGems.ResultSet</code>)
		 *          a <code>Cal.Data.DataGems.ResultSet</code>
		 * @return (CAL type: <code>[(Cal.Core.Prelude.String, Cal.Utilities.ValueType.ValueType)]</code>) 
		 *          the names and <code>Cal.Utilities.ValueType.ValueType</code>s of the <code>Cal.Data.DataGems.ResultSet</code> columns
		 */
		public static final SourceModel.Expr resultSetInfo(SourceModel.Expr resultSet) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.resultSetInfo), resultSet});
		}

		/**
		 * Name binding for function: resultSetInfo.
		 * @see #resultSetInfo(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName resultSetInfo = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "resultSetInfo");

		/**
		 * Returns a textual table containing the <code>Cal.Data.DataGems.ResultSet</code> values.
		 * Only up to <code>maxDisplayRows</code> will be displayed (unless zero is specified, in which case all rows will be included).
		 * @param maxDisplayRows (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the maximum number of rows to be displayed, or zero to include all rows
		 * @param resultSet (CAL type: <code>Cal.Data.DataGems.ResultSet</code>)
		 *          a <code>Cal.Data.DataGems.ResultSet</code>
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          a textual table containing the <code>Cal.Data.DataGems.ResultSet</code> data
		 */
		public static final SourceModel.Expr resultSetText(SourceModel.Expr maxDisplayRows, SourceModel.Expr resultSet) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.resultSetText), maxDisplayRows, resultSet});
		}

		/**
		 * @see #resultSetText(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param maxDisplayRows
		 * @param resultSet
		 * @return the SourceModel.Expr representing an application of resultSetText
		 */
		public static final SourceModel.Expr resultSetText(int maxDisplayRows, SourceModel.Expr resultSet) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.resultSetText), SourceModel.Expr.makeIntValue(maxDisplayRows), resultSet});
		}

		/**
		 * Name binding for function: resultSetText.
		 * @see #resultSetText(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName resultSetText = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "resultSetText");

		/**
		 * Rollback the changes made through the database connection.
		 * @param connection (CAL type: <code>Cal.Data.DataGems.JDBCConnection</code>)
		 *          a database connection
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the changes were rolled back successfully
		 */
		public static final SourceModel.Expr rollback(SourceModel.Expr connection) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.rollback), connection});
		}

		/**
		 * Name binding for function: rollback.
		 * @see #rollback(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName rollback = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "rollback");

		/**
		 * Returns the number of rows in the <code>Cal.Data.DataGems.ResultSet</code>.
		 * @param resultSet (CAL type: <code>Cal.Data.DataGems.ResultSet</code>)
		 *          a <code>Cal.Data.DataGems.ResultSet</code>
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the number of rows in the <code>Cal.Data.DataGems.ResultSet</code>
		 */
		public static final SourceModel.Expr rowCount(SourceModel.Expr resultSet) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.rowCount), resultSet});
		}

		/**
		 * Name binding for function: rowCount.
		 * @see #rowCount(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName rowCount = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "rowCount");

		/**
		 * Returns the description for the specified table.
		 * @param includeConstraintInfo (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 *          if <code>Cal.Core.Prelude.True</code>, then the table constraint info (primary/foreign keys and uniqueness) will be fetched;
		 * if <code>Cal.Core.Prelude.False</code>, then the constraint info will not be fetched
		 * @param connection (CAL type: <code>Cal.Data.DataGems.JDBCConnection</code>)
		 *          a database connection
		 * @param tableRef (CAL type: <code>Cal.Data.DatabaseMetadata.TableReference</code>)
		 *          a reference to a database table
		 * @return (CAL type: <code>Cal.Data.DatabaseMetadata.TableDescription</code>) 
		 *          a description of the database table (field info, primary/foreign key info, index info)
		 */
		public static final SourceModel.Expr tableDescription(SourceModel.Expr includeConstraintInfo, SourceModel.Expr connection, SourceModel.Expr tableRef) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.tableDescription), includeConstraintInfo, connection, tableRef});
		}

		/**
		 * @see #tableDescription(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param includeConstraintInfo
		 * @param connection
		 * @param tableRef
		 * @return the SourceModel.Expr representing an application of tableDescription
		 */
		public static final SourceModel.Expr tableDescription(boolean includeConstraintInfo, SourceModel.Expr connection, SourceModel.Expr tableRef) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.tableDescription), SourceModel.Expr.makeBooleanValue(includeConstraintInfo), connection, tableRef});
		}

		/**
		 * Name binding for function: tableDescription.
		 * @see #tableDescription(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName tableDescription = 
			QualifiedName.make(CAL_DataGems.MODULE_NAME, "tableDescription");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = -1070891296;

}
