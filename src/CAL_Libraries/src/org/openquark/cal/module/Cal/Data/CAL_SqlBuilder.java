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
 * Creation date: Fri Jan 22 15:13:36 PST 2010
 * --!>
 *  
 */

package org.openquark.cal.module.Cal.Data;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * Individual <code>Cal.Data.Sql.SqlBuilder</code> implementations have been moved into the Cal.Data.SqlBuilder package.
 * This module now is primarily for compatibility with the earlier versions of the code, and provides the <code>Cal.Data.SqlBuilder.bestSqlBuilderForDatabase</code> function 
 * which attempts to select the best <code>Cal.Data.Sql.SqlBuilder</code> based on the name of the database type.
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
		 * Helper binding method for function: db2SqlBuilder. 
		 * @return the SourceModule.expr representing an application of db2SqlBuilder
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
		 * Helper binding method for function: defaultSqlBuilder. 
		 * @return the SourceModule.expr representing an application of defaultSqlBuilder
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
		 * Helper binding method for function: derbySqlBuilder. 
		 * @return the SourceModule.expr representing an application of derbySqlBuilder
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
		 * Helper binding method for function: msAccessSqlBuilder. 
		 * @return the SourceModule.expr representing an application of msAccessSqlBuilder
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
		 * Helper binding method for function: msSqlServerSqlBuilder. 
		 * @return the SourceModule.expr representing an application of msSqlServerSqlBuilder
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
		 * Helper binding method for function: ncrTeradataSqlBuilder. 
		 * @return the SourceModule.expr representing an application of ncrTeradataSqlBuilder
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
		 * Helper binding method for function: odbcAccessSqlBuilder. 
		 * @return the SourceModule.expr representing an application of odbcAccessSqlBuilder
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
		 * Helper binding method for function: odbcSqlBuilder. 
		 * @return the SourceModule.expr representing an application of odbcSqlBuilder
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
		 * Helper binding method for function: oracleSqlBuilder. 
		 * @return the SourceModule.expr representing an application of oracleSqlBuilder
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
	public static final int javaDocHash = 472277723;

}
