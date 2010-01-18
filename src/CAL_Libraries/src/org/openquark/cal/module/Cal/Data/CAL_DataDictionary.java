/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_DataDictionary.java)
 * was generated from CAL module: Cal.Data.DataDictionary.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Data.DataDictionary module from Java code.
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
 * This module contains the <code>Cal.Data.DataDictionary.DataDictionary</code> type class, which
 * exposes a simplified view of a database and can be queried more simply than
 * constructing SQL (directly or using the <code>Cal.Data.Sql</code>
 * module types). The <code>Cal.Data.SqlDataDictionary</code> module
 * provides one implementation of a DataDictionary, however others are possible
 * as well.
 * @author Richard Webster
 */
public final class CAL_DataDictionary {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Data.DataDictionary");

	/**
	 * This inner class (TypeClasses) contains constants
	 * and methods related to binding to CAL TypeClasses in the Cal.Data.DataDictionary module.
	 */
	public static final class TypeClasses {
		/**
		 * A data dictionary exposes database fields and allows queries built from these database fields to be executed.
		 * The data dictionary hides complex details about the underlying database tables, SQL expressions, joins, etc... from the user.
		 */
		public static final QualifiedName DataDictionary = 
			QualifiedName.make(CAL_DataDictionary.MODULE_NAME, "DataDictionary");

	}
	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Data.DataDictionary module.
	 */
	public static final class TypeConstructors {
		/**
		 * A type representing a folder structure containing database fields.
		 * Folders are just named containers for other database items.
		 * Field items hold a database field and may also have child items.
		 */
		public static final QualifiedName DatabaseItem = 
			QualifiedName.make(CAL_DataDictionary.MODULE_NAME, "DatabaseItem");

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Data.DataDictionary module.
	 */
	public static final class Functions {
		/**
		 * Adds database fields to the data dictionary using the field info specified.
		 * The list of tuples is a list of definitions for the new fields.  The arguments are:
		 * <ul>
		 *  <li>
		 *   <code>Cal.Utilities.ValueType.ValueType</code> - The value type of the new field
		 *  </li>
		 *  <li>
		 *   <code>Cal.Data.DictionaryQuery.FieldType</code> - The field type of the new field
		 *  </li>
		 *  <li>
		 *   <code>Cal.Core.Prelude.String</code>    - The SQL select clause.  This must be valid SQL for the target server.
		 *  </li>
		 *  <li>
		 *   <code>Cal.Core.Prelude.String</code>    - The SQL where clause.  This must be valid SQL for the target server.
		 *  </li>
		 *  <li>
		 *   <code>Cal.Data.DictionaryQuery.AggregationType</code> - The agg type of the new field.
		 *  </li>
		 * </ul>
		 * <p>
		 * The return type contains the updated data dictionary and the list of database fields that
		 * were added.
		 * 
		 * @param arg_1 (CAL type: <code>Cal.Data.DataDictionary.DataDictionary a => a</code>)
		 * @param arg_2 (CAL type: <code>[(Cal.Utilities.ValueType.ValueType, Cal.Data.DictionaryQuery.FieldType, Cal.Core.Prelude.String, Cal.Core.Prelude.String, Cal.Data.DictionaryQuery.AggregationType)]</code>)
		 * @return (CAL type: <code>Cal.Data.DataDictionary.DataDictionary a => (a, [Cal.Data.DictionaryQuery.DatabaseField])</code>) 
		 */
		public static final SourceModel.Expr addDatabaseFields(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.addDatabaseFields), arg_1, arg_2});
		}

		/**
		 * Name binding for function: addDatabaseFields.
		 * @see #addDatabaseFields(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName addDatabaseFields = 
			QualifiedName.make(
				CAL_DataDictionary.MODULE_NAME, 
				"addDatabaseFields");

		/**
		 * Determines if all the fields specified are compatible.  That is, can a single query
		 * contain all of the specified fields and run successfully.  This is generally an issue
		 * with table joining or SQL limitations that prevent the fields from being part of a
		 * single query.
		 * @param arg_1 (CAL type: <code>Cal.Data.DataDictionary.DataDictionary a => a</code>)
		 * @param arg_2 (CAL type: <code>[Cal.Data.DictionaryQuery.DatabaseField]</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr areFieldsCompatible(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.areFieldsCompatible), arg_1, arg_2});
		}

		/**
		 * Name binding for function: areFieldsCompatible.
		 * @see #areFieldsCompatible(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName areFieldsCompatible = 
			QualifiedName.make(
				CAL_DataDictionary.MODULE_NAME, 
				"areFieldsCompatible");

		/**
		 * Returns a binary query field for the specified table.
		 * The name will be field will be looked up by unique name or display name.
		 * @param dictionary (CAL type: <code>Cal.Data.DataDictionary.DataDictionary a => a</code>)
		 * @param fieldName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.TypedExpr (Cal.Collections.Array.Array Cal.Core.Prelude.Byte)</code>) 
		 */
		public static final SourceModel.Expr binaryFieldExpr(SourceModel.Expr dictionary, SourceModel.Expr fieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.binaryFieldExpr), dictionary, fieldName});
		}

		/**
		 * @see #binaryFieldExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param dictionary
		 * @param fieldName
		 * @return the SourceModel.Expr representing an application of binaryFieldExpr
		 */
		public static final SourceModel.Expr binaryFieldExpr(SourceModel.Expr dictionary, java.lang.String fieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.binaryFieldExpr), dictionary, SourceModel.Expr.makeStringValue(fieldName)});
		}

		/**
		 * Name binding for function: binaryFieldExpr.
		 * @see #binaryFieldExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName binaryFieldExpr = 
			QualifiedName.make(
				CAL_DataDictionary.MODULE_NAME, 
				"binaryFieldExpr");

		/**
		 * Returns a Boolean query field for the specified table.
		 * The name will be field will be looked up by unique name or display name.
		 * @param dictionary (CAL type: <code>Cal.Data.DataDictionary.DataDictionary a => a</code>)
		 * @param fieldName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.TypedExpr Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr booleanFieldExpr(SourceModel.Expr dictionary, SourceModel.Expr fieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanFieldExpr), dictionary, fieldName});
		}

		/**
		 * @see #booleanFieldExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param dictionary
		 * @param fieldName
		 * @return the SourceModel.Expr representing an application of booleanFieldExpr
		 */
		public static final SourceModel.Expr booleanFieldExpr(SourceModel.Expr dictionary, java.lang.String fieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanFieldExpr), dictionary, SourceModel.Expr.makeStringValue(fieldName)});
		}

		/**
		 * Name binding for function: booleanFieldExpr.
		 * @see #booleanFieldExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName booleanFieldExpr = 
			QualifiedName.make(
				CAL_DataDictionary.MODULE_NAME, 
				"booleanFieldExpr");

		/**
		 * Returns up to N distinct values for the specified field in the dictionary.
		 * @param dictionary (CAL type: <code>Cal.Data.DataDictionary.DataDictionary a => a</code>)
		 * @param nMaxResults (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @param browseField (CAL type: <code>Cal.Data.DictionaryQuery.DatabaseField</code>)
		 * @return (CAL type: <code>[Cal.Core.Prelude.String]</code>) 
		 */
		public static final SourceModel.Expr browseDatabaseField(SourceModel.Expr dictionary, SourceModel.Expr nMaxResults, SourceModel.Expr browseField) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.browseDatabaseField), dictionary, nMaxResults, browseField});
		}

		/**
		 * @see #browseDatabaseField(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param dictionary
		 * @param nMaxResults
		 * @param browseField
		 * @return the SourceModel.Expr representing an application of browseDatabaseField
		 */
		public static final SourceModel.Expr browseDatabaseField(SourceModel.Expr dictionary, int nMaxResults, SourceModel.Expr browseField) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.browseDatabaseField), dictionary, SourceModel.Expr.makeIntValue(nMaxResults), browseField});
		}

		/**
		 * Name binding for function: browseDatabaseField.
		 * @see #browseDatabaseField(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName browseDatabaseField = 
			QualifiedName.make(
				CAL_DataDictionary.MODULE_NAME, 
				"browseDatabaseField");

		/**
		 * Returns the child items of a database item.
		 * @param databaseItem (CAL type: <code>Cal.Data.DataDictionary.DatabaseItem</code>)
		 * @return (CAL type: <code>[Cal.Data.DataDictionary.DatabaseItem]</code>) 
		 */
		public static final SourceModel.Expr childDatabaseItems(SourceModel.Expr databaseItem) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.childDatabaseItems), databaseItem});
		}

		/**
		 * Name binding for function: childDatabaseItems.
		 * @see #childDatabaseItems(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName childDatabaseItems = 
			QualifiedName.make(
				CAL_DataDictionary.MODULE_NAME, 
				"childDatabaseItems");

		/**
		 * All the database fields in the tree.
		 * @param arg_1 (CAL type: <code>Cal.Data.DataDictionary.DataDictionary a => a</code>)
		 * @return (CAL type: <code>[Cal.Data.DictionaryQuery.DatabaseField]</code>) 
		 */
		public static final SourceModel.Expr databaseFields(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.databaseFields), arg_1});
		}

		/**
		 * Name binding for function: databaseFields.
		 * @see #databaseFields(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName databaseFields = 
			QualifiedName.make(CAL_DataDictionary.MODULE_NAME, "databaseFields");

		/**
		 * Top level items in the tree.
		 * @param arg_1 (CAL type: <code>Cal.Data.DataDictionary.DataDictionary a => a</code>)
		 * @return (CAL type: <code>[Cal.Data.DataDictionary.DatabaseItem]</code>) 
		 */
		public static final SourceModel.Expr databaseItems(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.databaseItems), arg_1});
		}

		/**
		 * Name binding for function: databaseItems.
		 * @see #databaseItems(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName databaseItems = 
			QualifiedName.make(CAL_DataDictionary.MODULE_NAME, "databaseItems");

		/**
		 * Returns a double query field for the specified table.
		 * The name will be field will be looked up by unique name or display name.
		 * @param dictionary (CAL type: <code>Cal.Data.DataDictionary.DataDictionary a => a</code>)
		 * @param fieldName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.TypedExpr Cal.Core.Prelude.Double</code>) 
		 */
		public static final SourceModel.Expr doubleFieldExpr(SourceModel.Expr dictionary, SourceModel.Expr fieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleFieldExpr), dictionary, fieldName});
		}

		/**
		 * @see #doubleFieldExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param dictionary
		 * @param fieldName
		 * @return the SourceModel.Expr representing an application of doubleFieldExpr
		 */
		public static final SourceModel.Expr doubleFieldExpr(SourceModel.Expr dictionary, java.lang.String fieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleFieldExpr), dictionary, SourceModel.Expr.makeStringValue(fieldName)});
		}

		/**
		 * Name binding for function: doubleFieldExpr.
		 * @see #doubleFieldExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleFieldExpr = 
			QualifiedName.make(
				CAL_DataDictionary.MODULE_NAME, 
				"doubleFieldExpr");

		/**
		 * Execute the specified database query to get a resultset.
		 * @param arg_1 (CAL type: <code>Cal.Data.DataDictionary.DataDictionary a => a</code>)
		 * @param arg_2 (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @return (CAL type: <code>Cal.Data.DataGems.ResultSet</code>) 
		 */
		public static final SourceModel.Expr executeQuery(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.executeQuery), arg_1, arg_2});
		}

		/**
		 * Name binding for function: executeQuery.
		 * @see #executeQuery(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName executeQuery = 
			QualifiedName.make(CAL_DataDictionary.MODULE_NAME, "executeQuery");

		/**
		 * Top level items in the tree.
		 * Only fields passing the filter function will be included,
		 * and empty folders will not excluded.
		 * @param filterFn (CAL type: <code>Cal.Data.DictionaryQuery.DatabaseField -> Cal.Core.Prelude.Boolean</code>)
		 * @param dictionary (CAL type: <code>Cal.Data.DataDictionary.DataDictionary a => a</code>)
		 * @return (CAL type: <code>[Cal.Data.DataDictionary.DatabaseItem]</code>) 
		 */
		public static final SourceModel.Expr filteredDatabaseItems(SourceModel.Expr filterFn, SourceModel.Expr dictionary) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.filteredDatabaseItems), filterFn, dictionary});
		}

		/**
		 * Name binding for function: filteredDatabaseItems.
		 * @see #filteredDatabaseItems(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName filteredDatabaseItems = 
			QualifiedName.make(
				CAL_DataDictionary.MODULE_NAME, 
				"filteredDatabaseItems");

		/**
		 * Looks up one of the new database fields that was added to the data dictionary using the
		 * AddDatabaseFields function.
		 * The tuple defines the field to lookup.  The tuple arguments are:
		 * <ul>
		 *  <li>
		 *   <code>Cal.Utilities.ValueType.ValueType</code> - The value type of the field
		 *  </li>
		 *  <li>
		 *   <code>Cal.Data.DictionaryQuery.FieldType</code> - The field type of the field
		 *  </li>
		 *  <li>
		 *   <code>Cal.Core.Prelude.String</code>    - The SQL select clause.
		 *  </li>
		 *  <li>
		 *   <code>Cal.Core.Prelude.String</code>    - The SQL where clause.
		 *  </li>
		 *  <li>
		 *   <code>Cal.Data.DictionaryQuery.AggregationType</code> - The agg type of the field.
		 *  </li>
		 * </ul>
		 * <p>
		 * The return type is a Maybe which is either the DatabaseField found for the field definition
		 * or nothing if no field could be found.
		 * 
		 * @param arg_1 (CAL type: <code>Cal.Data.DataDictionary.DataDictionary a => a</code>)
		 * @param arg_2 (CAL type: <code>(Cal.Utilities.ValueType.ValueType, Cal.Data.DictionaryQuery.FieldType, Cal.Core.Prelude.String, Cal.Core.Prelude.String, Cal.Data.DictionaryQuery.AggregationType)</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Data.DictionaryQuery.DatabaseField</code>) 
		 */
		public static final SourceModel.Expr getDatabaseField(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getDatabaseField), arg_1, arg_2});
		}

		/**
		 * Name binding for function: getDatabaseField.
		 * @see #getDatabaseField(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getDatabaseField = 
			QualifiedName.make(
				CAL_DataDictionary.MODULE_NAME, 
				"getDatabaseField");

		/**
		 * Returns the name of the database item.
		 * @param databaseItem (CAL type: <code>Cal.Data.DataDictionary.DatabaseItem</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr getDatabaseItemName(SourceModel.Expr databaseItem) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getDatabaseItemName), databaseItem});
		}

		/**
		 * Name binding for function: getDatabaseItemName.
		 * @see #getDatabaseItemName(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getDatabaseItemName = 
			QualifiedName.make(
				CAL_DataDictionary.MODULE_NAME, 
				"getDatabaseItemName");

		/**
		 * Returns an integer query field for the specified table.
		 * The name will be field will be looked up by unique name or display name.
		 * @param dictionary (CAL type: <code>Cal.Data.DataDictionary.DataDictionary a => a</code>)
		 * @param fieldName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.TypedExpr Cal.Core.Prelude.Int</code>) 
		 */
		public static final SourceModel.Expr intFieldExpr(SourceModel.Expr dictionary, SourceModel.Expr fieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intFieldExpr), dictionary, fieldName});
		}

		/**
		 * @see #intFieldExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param dictionary
		 * @param fieldName
		 * @return the SourceModel.Expr representing an application of intFieldExpr
		 */
		public static final SourceModel.Expr intFieldExpr(SourceModel.Expr dictionary, java.lang.String fieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intFieldExpr), dictionary, SourceModel.Expr.makeStringValue(fieldName)});
		}

		/**
		 * Name binding for function: intFieldExpr.
		 * @see #intFieldExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intFieldExpr = 
			QualifiedName.make(CAL_DataDictionary.MODULE_NAME, "intFieldExpr");

		/**
		 * Returns whether the database item is a field.
		 * @param databaseItem (CAL type: <code>Cal.Data.DataDictionary.DatabaseItem</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr isDatabaseFieldItem(SourceModel.Expr databaseItem) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isDatabaseFieldItem), databaseItem});
		}

		/**
		 * Name binding for function: isDatabaseFieldItem.
		 * @see #isDatabaseFieldItem(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isDatabaseFieldItem = 
			QualifiedName.make(
				CAL_DataDictionary.MODULE_NAME, 
				"isDatabaseFieldItem");

		/**
		 * Returns whether the database item is a folder.
		 * @param databaseItem (CAL type: <code>Cal.Data.DataDictionary.DatabaseItem</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr isDatabaseFolderItem(SourceModel.Expr databaseItem) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isDatabaseFolderItem), databaseItem});
		}

		/**
		 * Name binding for function: isDatabaseFolderItem.
		 * @see #isDatabaseFolderItem(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isDatabaseFolderItem = 
			QualifiedName.make(
				CAL_DataDictionary.MODULE_NAME, 
				"isDatabaseFolderItem");

		/**
		 * All named join sets (contexts) in the dictionary.
		 * @param arg_1 (CAL type: <code>Cal.Data.DataDictionary.DataDictionary a => a</code>)
		 * @return (CAL type: <code>[Cal.Data.DictionaryQuery.JoinSet]</code>) 
		 */
		public static final SourceModel.Expr joinSets(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.joinSets), arg_1});
		}

		/**
		 * Name binding for function: joinSets.
		 * @see #joinSets(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName joinSets = 
			QualifiedName.make(CAL_DataDictionary.MODULE_NAME, "joinSets");

		/**
		 * Finds the database field with the specified unique identifier.
		 * @param dictionary (CAL type: <code>Cal.Data.DataDictionary.DataDictionary a => a</code>)
		 * @param fieldID (CAL type: <code>Cal.Utilities.UniqueIdentifier.UniqueIdentifier Cal.Data.DictionaryQuery.DatabaseField</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Data.DictionaryQuery.DatabaseField</code>) 
		 */
		public static final SourceModel.Expr lookupFieldByID(SourceModel.Expr dictionary, SourceModel.Expr fieldID) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lookupFieldByID), dictionary, fieldID});
		}

		/**
		 * Name binding for function: lookupFieldByID.
		 * @see #lookupFieldByID(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName lookupFieldByID = 
			QualifiedName.make(
				CAL_DataDictionary.MODULE_NAME, 
				"lookupFieldByID");

		/**
		 * Finds the database field with the specified unique name or display name.
		 * First, an attempt is made to find a field with the specified unique name.
		 * If this fails, then a search is made for a field with the specifield display name.
		 * An error is thrown if the field cannot be found.
		 * @param dictionary (CAL type: <code>Cal.Data.DataDictionary.DataDictionary a => a</code>)
		 * @param fieldName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.DatabaseField</code>) 
		 */
		public static final SourceModel.Expr lookupFieldByIdOrName(SourceModel.Expr dictionary, SourceModel.Expr fieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lookupFieldByIdOrName), dictionary, fieldName});
		}

		/**
		 * @see #lookupFieldByIdOrName(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param dictionary
		 * @param fieldName
		 * @return the SourceModel.Expr representing an application of lookupFieldByIdOrName
		 */
		public static final SourceModel.Expr lookupFieldByIdOrName(SourceModel.Expr dictionary, java.lang.String fieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lookupFieldByIdOrName), dictionary, SourceModel.Expr.makeStringValue(fieldName)});
		}

		/**
		 * Name binding for function: lookupFieldByIdOrName.
		 * @see #lookupFieldByIdOrName(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName lookupFieldByIdOrName = 
			QualifiedName.make(
				CAL_DataDictionary.MODULE_NAME, 
				"lookupFieldByIdOrName");

		/**
		 * Finds the database field with the specified display name (if unique).
		 * If multiple fields have the display name, then <code>Cal.Core.Prelude.Nothing</code> will be returned.
		 * @param dictionary (CAL type: <code>Cal.Data.DataDictionary.DataDictionary a => a</code>)
		 * @param fieldName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Data.DictionaryQuery.DatabaseField</code>) 
		 */
		public static final SourceModel.Expr lookupFieldByName(SourceModel.Expr dictionary, SourceModel.Expr fieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lookupFieldByName), dictionary, fieldName});
		}

		/**
		 * @see #lookupFieldByName(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param dictionary
		 * @param fieldName
		 * @return the SourceModel.Expr representing an application of lookupFieldByName
		 */
		public static final SourceModel.Expr lookupFieldByName(SourceModel.Expr dictionary, java.lang.String fieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lookupFieldByName), dictionary, SourceModel.Expr.makeStringValue(fieldName)});
		}

		/**
		 * Name binding for function: lookupFieldByName.
		 * @see #lookupFieldByName(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName lookupFieldByName = 
			QualifiedName.make(
				CAL_DataDictionary.MODULE_NAME, 
				"lookupFieldByName");

		/**
		 * Construct a database field item with the specified field and child items.
		 * @param field (CAL type: <code>Cal.Data.DictionaryQuery.DatabaseField</code>)
		 * @param children (CAL type: <code>[Cal.Data.DataDictionary.DatabaseItem]</code>)
		 * @return (CAL type: <code>Cal.Data.DataDictionary.DatabaseItem</code>) 
		 */
		public static final SourceModel.Expr makeDatabaseFieldItem(SourceModel.Expr field, SourceModel.Expr children) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeDatabaseFieldItem), field, children});
		}

		/**
		 * Name binding for function: makeDatabaseFieldItem.
		 * @see #makeDatabaseFieldItem(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeDatabaseFieldItem = 
			QualifiedName.make(
				CAL_DataDictionary.MODULE_NAME, 
				"makeDatabaseFieldItem");

		/**
		 * Construct a database folder item with the specified name and contents.
		 * @param name (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @param children (CAL type: <code>[Cal.Data.DataDictionary.DatabaseItem]</code>)
		 * @return (CAL type: <code>Cal.Data.DataDictionary.DatabaseItem</code>) 
		 */
		public static final SourceModel.Expr makeDatabaseFolderItem(SourceModel.Expr name, SourceModel.Expr children) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeDatabaseFolderItem), name, children});
		}

		/**
		 * @see #makeDatabaseFolderItem(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param name
		 * @param children
		 * @return the SourceModel.Expr representing an application of makeDatabaseFolderItem
		 */
		public static final SourceModel.Expr makeDatabaseFolderItem(java.lang.String name, SourceModel.Expr children) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeDatabaseFolderItem), SourceModel.Expr.makeStringValue(name), children});
		}

		/**
		 * Name binding for function: makeDatabaseFolderItem.
		 * @see #makeDatabaseFolderItem(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeDatabaseFolderItem = 
			QualifiedName.make(
				CAL_DataDictionary.MODULE_NAME, 
				"makeDatabaseFolderItem");

		/**
		 * Build the SQL query text for the specified query.
		 * @param arg_1 (CAL type: <code>Cal.Data.DataDictionary.DataDictionary a => a</code>)
		 * @param arg_2 (CAL type: <code>Cal.Data.DictionaryQuery.Query</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr queryText(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.queryText), arg_1, arg_2});
		}

		/**
		 * Name binding for function: queryText.
		 * @see #queryText(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName queryText = 
			QualifiedName.make(CAL_DataDictionary.MODULE_NAME, "queryText");

		/**
		 * Replaces the children of a database item.
		 * @param databaseItem (CAL type: <code>Cal.Data.DataDictionary.DatabaseItem</code>)
		 * @param newChildItems (CAL type: <code>[Cal.Data.DataDictionary.DatabaseItem]</code>)
		 * @return (CAL type: <code>Cal.Data.DataDictionary.DatabaseItem</code>) 
		 */
		public static final SourceModel.Expr setDatabaseChildItems(SourceModel.Expr databaseItem, SourceModel.Expr newChildItems) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.setDatabaseChildItems), databaseItem, newChildItems});
		}

		/**
		 * Name binding for function: setDatabaseChildItems.
		 * @see #setDatabaseChildItems(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName setDatabaseChildItems = 
			QualifiedName.make(
				CAL_DataDictionary.MODULE_NAME, 
				"setDatabaseChildItems");

		/**
		 * Returns a string query field for the specified table.
		 * The name will be field will be looked up by unique name or display name.
		 * @param dictionary (CAL type: <code>Cal.Data.DataDictionary.DataDictionary a => a</code>)
		 * @param fieldName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.TypedExpr Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr stringFieldExpr(SourceModel.Expr dictionary, SourceModel.Expr fieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.stringFieldExpr), dictionary, fieldName});
		}

		/**
		 * @see #stringFieldExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param dictionary
		 * @param fieldName
		 * @return the SourceModel.Expr representing an application of stringFieldExpr
		 */
		public static final SourceModel.Expr stringFieldExpr(SourceModel.Expr dictionary, java.lang.String fieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.stringFieldExpr), dictionary, SourceModel.Expr.makeStringValue(fieldName)});
		}

		/**
		 * Name binding for function: stringFieldExpr.
		 * @see #stringFieldExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName stringFieldExpr = 
			QualifiedName.make(
				CAL_DataDictionary.MODULE_NAME, 
				"stringFieldExpr");

		/**
		 * Returns a time query field for the specified table.
		 * The name will be field will be looked up by unique name or display name.
		 * @param dictionary (CAL type: <code>Cal.Data.DataDictionary.DataDictionary a => a</code>)
		 * @param fieldName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.TypedExpr Cal.Utilities.Time.Time</code>) 
		 */
		public static final SourceModel.Expr timeFieldExpr(SourceModel.Expr dictionary, SourceModel.Expr fieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.timeFieldExpr), dictionary, fieldName});
		}

		/**
		 * @see #timeFieldExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param dictionary
		 * @param fieldName
		 * @return the SourceModel.Expr representing an application of timeFieldExpr
		 */
		public static final SourceModel.Expr timeFieldExpr(SourceModel.Expr dictionary, java.lang.String fieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.timeFieldExpr), dictionary, SourceModel.Expr.makeStringValue(fieldName)});
		}

		/**
		 * Name binding for function: timeFieldExpr.
		 * @see #timeFieldExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName timeFieldExpr = 
			QualifiedName.make(CAL_DataDictionary.MODULE_NAME, "timeFieldExpr");

		/**
		 * Returns an untyped query field for the specified table.
		 * The name will be field will be looked up by unique name or display name.
		 * @param dictionary (CAL type: <code>Cal.Data.DataDictionary.DataDictionary a => a</code>)
		 * @param fieldName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Data.DictionaryQuery.Expr</code>) 
		 */
		public static final SourceModel.Expr untypedFieldExpr(SourceModel.Expr dictionary, SourceModel.Expr fieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.untypedFieldExpr), dictionary, fieldName});
		}

		/**
		 * @see #untypedFieldExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param dictionary
		 * @param fieldName
		 * @return the SourceModel.Expr representing an application of untypedFieldExpr
		 */
		public static final SourceModel.Expr untypedFieldExpr(SourceModel.Expr dictionary, java.lang.String fieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.untypedFieldExpr), dictionary, SourceModel.Expr.makeStringValue(fieldName)});
		}

		/**
		 * Name binding for function: untypedFieldExpr.
		 * @see #untypedFieldExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName untypedFieldExpr = 
			QualifiedName.make(
				CAL_DataDictionary.MODULE_NAME, 
				"untypedFieldExpr");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = -811812986;

}
