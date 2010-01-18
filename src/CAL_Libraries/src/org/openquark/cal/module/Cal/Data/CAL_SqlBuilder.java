/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_SqlBuilder.java)
 * was generated from CAL module: Cal.Data.SqlBuilder.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Data.SqlBuilder module from Java code.
 *  
 * Creation date: Fri Oct 19 11:45:12 PDT 2007
 * --!>
 *  
 */

package org.openquark.cal.module.Cal.Data;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * This module contains implementations of <code>Cal.Data.Sql.SqlBuilder</code> for several kinds of RDBMS. 
 * These are generally variations on the <code>Cal.Data.SqlBuilder.defaultSqlBuilder</code>.
 * <p>
 * There is a function <code>Cal.Data.SqlBuilder.bestSqlBuilderForDatabase</code> that attempts to choose a <code>Cal.Data.Sql.SqlBuilder</code> based on an identifying string.
 * 
 * @author Richard Webster
 */
public final class CAL_SqlBuilder {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Data.SqlBuilder");

	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Data.SqlBuilder module.
	 */
	public static final class Functions {
		/**
		 * Returns the best <code>Cal.Data.Sql.SqlBuilder</code> for the type of database type name specified.
		 * If no match can be found, then the default <code>Cal.Data.Sql.SqlBuilder</code> will be returned.
		 * @param databaseTypeName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Data.Sql.SqlBuilder</code>) 
		 */
		public static final SourceModel.Expr bestSqlBuilderForDatabase(SourceModel.Expr databaseTypeName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.bestSqlBuilderForDatabase), databaseTypeName});
		}

		/**
		 * @see #bestSqlBuilderForDatabase(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param databaseTypeName
		 * @return the SourceModel.Expr representing an application of bestSqlBuilderForDatabase
		 */
		public static final SourceModel.Expr bestSqlBuilderForDatabase(java.lang.String databaseTypeName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.bestSqlBuilderForDatabase), SourceModel.Expr.makeStringValue(databaseTypeName)});
		}

		/**
		 * Name binding for function: bestSqlBuilderForDatabase.
		 * @see #bestSqlBuilderForDatabase(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName bestSqlBuilderForDatabase = 
			QualifiedName.make(
				CAL_SqlBuilder.MODULE_NAME, 
				"bestSqlBuilderForDatabase");

		/**
		 * The DB2 SQL builder generates queries which will work with IBM DB2 databases.
		 * Quoting of identifiers is different than the default.
		 * The double-quote character is used for quoting.
		 * @return (CAL type: <code>Cal.Data.Sql.SqlBuilder</code>) 
		 */
		public static final SourceModel.Expr db2SqlBuilder() {
			return SourceModel.Expr.Var.make(Functions.db2SqlBuilder);
		}

		/**
		 * Name binding for function: db2SqlBuilder.
		 * @see #db2SqlBuilder()
		 */
		public static final QualifiedName db2SqlBuilder = 
			QualifiedName.make(CAL_SqlBuilder.MODULE_NAME, "db2SqlBuilder");

		/**
		 * Creates a default primitive value builder.
		 * @return (CAL type: <code>Cal.Data.Sql.PrimitiveValueBuilder</code>) 
		 */
		public static final SourceModel.Expr defaultPrimitiveValueBuilder() {
			return 
				SourceModel.Expr.Var.make(
					Functions.defaultPrimitiveValueBuilder);
		}

		/**
		 * Name binding for function: defaultPrimitiveValueBuilder.
		 * @see #defaultPrimitiveValueBuilder()
		 */
		public static final QualifiedName defaultPrimitiveValueBuilder = 
			QualifiedName.make(
				CAL_SqlBuilder.MODULE_NAME, 
				"defaultPrimitiveValueBuilder");

		/**
		 * Creates a default implementation of a <code>Cal.Data.Sql.SqlBuilder</code>.
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
			QualifiedName.make(CAL_SqlBuilder.MODULE_NAME, "defaultSqlBuilder");

		/**
		 * The SQL builder for Derby differs from the default SQL builder
		 * in the identifier quoting characters - double quotes instead of square brackets
		 * @return (CAL type: <code>Cal.Data.Sql.SqlBuilder</code>) 
		 */
		public static final SourceModel.Expr derbySqlBuilder() {
			return SourceModel.Expr.Var.make(Functions.derbySqlBuilder);
		}

		/**
		 * Name binding for function: derbySqlBuilder.
		 * @see #derbySqlBuilder()
		 */
		public static final QualifiedName derbySqlBuilder = 
			QualifiedName.make(CAL_SqlBuilder.MODULE_NAME, "derbySqlBuilder");

		/**
		 * The Microsoft Jet SQL builder generates queries which will work with the MS Jet database engine 
		 * (which supports MS Access, Excel, Text, and others).
		 * Special syntax is used for a few SQL functions.
		 * @return (CAL type: <code>Cal.Data.Sql.SqlBuilder</code>) 
		 */
		public static final SourceModel.Expr msAccessSqlBuilder() {
			return SourceModel.Expr.Var.make(Functions.msAccessSqlBuilder);
		}

		/**
		 * Name binding for function: msAccessSqlBuilder.
		 * @see #msAccessSqlBuilder()
		 */
		public static final QualifiedName msAccessSqlBuilder = 
			QualifiedName.make(CAL_SqlBuilder.MODULE_NAME, "msAccessSqlBuilder");

		/**
		 * Helper binding method for function: msJetSqlBuilder. 
		 * @return the SourceModule.expr representing an application of msJetSqlBuilder
		 */
		public static final SourceModel.Expr msJetSqlBuilder() {
			return SourceModel.Expr.Var.make(Functions.msJetSqlBuilder);
		}

		/**
		 * Name binding for function: msJetSqlBuilder.
		 * @see #msJetSqlBuilder()
		 */
		public static final QualifiedName msJetSqlBuilder = 
			QualifiedName.make(CAL_SqlBuilder.MODULE_NAME, "msJetSqlBuilder");

		/**
		 * The Microsoft SQL Server SQL builder generates queries which will work with SQL Server databases.
		 * Special syntax is used for some SQL functions.
		 * @return (CAL type: <code>Cal.Data.Sql.SqlBuilder</code>) 
		 */
		public static final SourceModel.Expr msSqlServerSqlBuilder() {
			return SourceModel.Expr.Var.make(Functions.msSqlServerSqlBuilder);
		}

		/**
		 * Name binding for function: msSqlServerSqlBuilder.
		 * @see #msSqlServerSqlBuilder()
		 */
		public static final QualifiedName msSqlServerSqlBuilder = 
			QualifiedName.make(
				CAL_SqlBuilder.MODULE_NAME, 
				"msSqlServerSqlBuilder");

		/**
		 * Helper binding method for function: ncrTeradataPrimitiveValueBuilder. 
		 * @return the SourceModule.expr representing an application of ncrTeradataPrimitiveValueBuilder
		 */
		public static final SourceModel.Expr ncrTeradataPrimitiveValueBuilder() {
			return 
				SourceModel.Expr.Var.make(
					Functions.ncrTeradataPrimitiveValueBuilder);
		}

		/**
		 * Name binding for function: ncrTeradataPrimitiveValueBuilder.
		 * @see #ncrTeradataPrimitiveValueBuilder()
		 */
		public static final QualifiedName ncrTeradataPrimitiveValueBuilder = 
			QualifiedName.make(
				CAL_SqlBuilder.MODULE_NAME, 
				"ncrTeradataPrimitiveValueBuilder");

		/**
		 * The SQL builder generates queries which will work with Teradata databases.
		 * Quoting of identifiers is different than the default.
		 * The double-quote character is used for quoting and this is also needed for table names with lower case letters.
		 * @return (CAL type: <code>Cal.Data.Sql.SqlBuilder</code>) 
		 */
		public static final SourceModel.Expr ncrTeradataSqlBuilder() {
			return SourceModel.Expr.Var.make(Functions.ncrTeradataSqlBuilder);
		}

		/**
		 * Name binding for function: ncrTeradataSqlBuilder.
		 * @see #ncrTeradataSqlBuilder()
		 */
		public static final QualifiedName ncrTeradataSqlBuilder = 
			QualifiedName.make(
				CAL_SqlBuilder.MODULE_NAME, 
				"ncrTeradataSqlBuilder");

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
				CAL_SqlBuilder.MODULE_NAME, 
				"odbcAccessSqlBuilder");

		/**
		 * The ODBC SQL builder generates queries which will work with typical ODBC datasources.
		 * More specialized versions might be needed for certain ODBC drivers and/or database servers.
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
			QualifiedName.make(CAL_SqlBuilder.MODULE_NAME, "odbcSqlBuilder");

		/**
		 * The Oracle SQL builder generates queries which will work with Oracle databases.
		 * Quoting of identifiers is different than the default.
		 * The double-quote character is used for quoting and this is also needed for table names with lower case letters.
		 * @return (CAL type: <code>Cal.Data.Sql.SqlBuilder</code>) 
		 */
		public static final SourceModel.Expr oracleSqlBuilder() {
			return SourceModel.Expr.Var.make(Functions.oracleSqlBuilder);
		}

		/**
		 * Name binding for function: oracleSqlBuilder.
		 * @see #oracleSqlBuilder()
		 */
		public static final QualifiedName oracleSqlBuilder = 
			QualifiedName.make(CAL_SqlBuilder.MODULE_NAME, "oracleSqlBuilder");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = 414428870;

}
