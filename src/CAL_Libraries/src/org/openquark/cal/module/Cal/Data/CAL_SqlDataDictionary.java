/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_SqlDataDictionary.java)
 * was generated from CAL module: Cal.Data.SqlDataDictionary.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Data.SqlDataDictionary module from Java code.
 *  
 * Creation date: Tue Aug 28 15:58:43 PDT 2007
 * --!>
 *  
 */

package org.openquark.cal.module.Cal.Data;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * This module contains an implementation of a <code>Cal.Data.DataDictionary.DataDictionary</code> based on the <code>Cal.Data.Sql</code> and <code>Cal.Data.DataGems</code> modules.
 * <p>
 * Information about SQL expressions and join is specified when the data
 * dictionary is constructed, but users of the data dictionary work with a
 * simplified query model.
 * 
 * @author Richard Webster
 */
public final class CAL_SqlDataDictionary {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Data.SqlDataDictionary");

	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Data.SqlDataDictionary module.
	 */
	public static final class TypeConstructors {
		/**
		 * An implementation of the <code>Cal.Data.DataDictionary.DataDictionary</code> type class based on the 
		 * Sql builder and DataGems functionality.
		 */
		public static final QualifiedName SqlDataDictionary = 
			QualifiedName.make(
				CAL_SqlDataDictionary.MODULE_NAME, 
				"SqlDataDictionary");

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Data.SqlDataDictionary module.
	 */
	public static final class Functions {
		/**
		 * Constructs a SQL data dictionary with no folder information.
		 * @param name (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the name of the data dictionary (which will also be used for the unique identifier)
		 * @param connection (CAL type: <code>Cal.Data.DataGems.JDBCConnection</code>)
		 *          the JDBC database connection to be used when executing queries against this data dictionary
		 * @param sqlBuilder (CAL type: <code>Cal.Data.Sql.SqlBuilder</code>)
		 *          the SQL builder to be used to build SQL text when processing database queries
		 * @param baseRestriction (CAL type: <code>Cal.Core.Prelude.Maybe (Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Boolean)</code>)
		 *          this restriction (if specified) will be included in all queries against this dictionary
		 * @param joinSets (CAL type: <code>[Cal.Data.Sql.JoinSet]</code>)
		 *          sets of join information to be used when processing queries
		 * @param fieldInfo (CAL type: <code>[(Cal.Data.DictionaryQuery.DatabaseField, Cal.Data.Sql.Expr)]</code>)
		 *          the database fields to be exposed in the dictionary, along with the underlying SQL expressions to which they correspond
		 * @return (CAL type: <code>Cal.Data.SqlDataDictionary.SqlDataDictionary</code>) 
		 *          a data dictionary based on the specified connection, database expressions, and join information
		 */
		public static final SourceModel.Expr makeSqlDataDictionary(SourceModel.Expr name, SourceModel.Expr connection, SourceModel.Expr sqlBuilder, SourceModel.Expr baseRestriction, SourceModel.Expr joinSets, SourceModel.Expr fieldInfo) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeSqlDataDictionary), name, connection, sqlBuilder, baseRestriction, joinSets, fieldInfo});
		}

		/**
		 * @see #makeSqlDataDictionary(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param name
		 * @param connection
		 * @param sqlBuilder
		 * @param baseRestriction
		 * @param joinSets
		 * @param fieldInfo
		 * @return the SourceModel.Expr representing an application of makeSqlDataDictionary
		 */
		public static final SourceModel.Expr makeSqlDataDictionary(java.lang.String name, SourceModel.Expr connection, SourceModel.Expr sqlBuilder, SourceModel.Expr baseRestriction, SourceModel.Expr joinSets, SourceModel.Expr fieldInfo) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeSqlDataDictionary), SourceModel.Expr.makeStringValue(name), connection, sqlBuilder, baseRestriction, joinSets, fieldInfo});
		}

		/**
		 * Name binding for function: makeSqlDataDictionary.
		 * @see #makeSqlDataDictionary(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeSqlDataDictionary = 
			QualifiedName.make(
				CAL_SqlDataDictionary.MODULE_NAME, 
				"makeSqlDataDictionary");

		/**
		 * Constructs a SQL data dictionary with folder information.
		 * The fieldInfo for each database field may specify a list of ancestor dictinary item names.
		 * These can include the names of other database fields.
		 * Folders will be created as needed for any other names.
		 * @param name (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the name of the data dictionary (which will also be used for the unique identifier)
		 * @param connection (CAL type: <code>Cal.Data.DataGems.JDBCConnection</code>)
		 *          the JDBC database connection to be used when executing queries against this data dictionary
		 * @param sqlBuilder (CAL type: <code>Cal.Data.Sql.SqlBuilder</code>)
		 *          the SQL builder to be used to build SQL text when processing database queries
		 * @param baseRestriction (CAL type: <code>Cal.Core.Prelude.Maybe (Cal.Data.Sql.TypedExpr Cal.Core.Prelude.Boolean)</code>)
		 *          this restriction (if specified) will be included in all queries against this dictionary
		 * @param joinSets (CAL type: <code>[Cal.Data.Sql.JoinSet]</code>)
		 *          sets of join information to be used when processing queries
		 * @param fieldInfo (CAL type: <code>[(Cal.Data.DictionaryQuery.DatabaseField, [Cal.Core.Prelude.String], Cal.Data.Sql.Expr)]</code>)
		 *          the database fields to be exposed in the dictionary, along with the folder path and the underlying SQL expressions to which they correspond
		 * @return (CAL type: <code>Cal.Data.SqlDataDictionary.SqlDataDictionary</code>) 
		 *          a data dictionary based on the specified connection, database expressions, and join information
		 */
		public static final SourceModel.Expr makeSqlDataDictionary2(SourceModel.Expr name, SourceModel.Expr connection, SourceModel.Expr sqlBuilder, SourceModel.Expr baseRestriction, SourceModel.Expr joinSets, SourceModel.Expr fieldInfo) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeSqlDataDictionary2), name, connection, sqlBuilder, baseRestriction, joinSets, fieldInfo});
		}

		/**
		 * @see #makeSqlDataDictionary2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param name
		 * @param connection
		 * @param sqlBuilder
		 * @param baseRestriction
		 * @param joinSets
		 * @param fieldInfo
		 * @return the SourceModel.Expr representing an application of makeSqlDataDictionary2
		 */
		public static final SourceModel.Expr makeSqlDataDictionary2(java.lang.String name, SourceModel.Expr connection, SourceModel.Expr sqlBuilder, SourceModel.Expr baseRestriction, SourceModel.Expr joinSets, SourceModel.Expr fieldInfo) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeSqlDataDictionary2), SourceModel.Expr.makeStringValue(name), connection, sqlBuilder, baseRestriction, joinSets, fieldInfo});
		}

		/**
		 * Name binding for function: makeSqlDataDictionary2.
		 * @see #makeSqlDataDictionary2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeSqlDataDictionary2 = 
			QualifiedName.make(
				CAL_SqlDataDictionary.MODULE_NAME, 
				"makeSqlDataDictionary2");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = 577300449;

}
