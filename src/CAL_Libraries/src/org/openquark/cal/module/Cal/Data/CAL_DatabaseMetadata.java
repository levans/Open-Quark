/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_DatabaseMetadata.java)
 * was generated from CAL module: Cal.Data.DatabaseMetadata.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Data.DatabaseMetadata module from Java code.
 *  
 * Creation date: Wed Aug 08 13:58:30 PDT 2007
 * --!>
 *  
 */

package org.openquark.cal.module.Cal.Data;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * This module provides types that model the metadata of a relational database.
 * <p>
 * A <code>Cal.Data.DatabaseMetadata.TableDescription</code> holds information about the columns and constraints on a table.
 * <p>
 * A <code>Cal.Data.DatabaseMetadata.FieldDescription</code> holds the properties of a single column, such as name, data type and whether values can be null.
 * <p>
 * A <code>Cal.Data.DatabaseMetadata.TableConstraint</code> can specify the primary keys, foreign keys or unique-value columns of a table. 
 * 
 * @author Kevin Sit
 */
public final class CAL_DatabaseMetadata {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Data.DatabaseMetadata");

	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Data.DatabaseMetadata module.
	 */
	public static final class TypeConstructors {
		/**
		 * ConnectionSettings
		 */
		public static final QualifiedName ConnectionSettings = 
			QualifiedName.make(
				CAL_DatabaseMetadata.MODULE_NAME, 
				"ConnectionSettings");

		/**
		 * This type constructor allows the developer to uniquely identify a database
		 * on a server.
		 */
		public static final QualifiedName DatabaseReference = 
			QualifiedName.make(
				CAL_DatabaseMetadata.MODULE_NAME, 
				"DatabaseReference");

		/**
		 * Stores essential information about a field (i.e. a column) in a table.
		 */
		public static final QualifiedName FieldDescription = 
			QualifiedName.make(
				CAL_DatabaseMetadata.MODULE_NAME, 
				"FieldDescription");

		/**
		 * JConnectionSettings
		 */
		public static final QualifiedName JConnectionSettings = 
			QualifiedName.make(
				CAL_DatabaseMetadata.MODULE_NAME, 
				"JConnectionSettings");

		/**
		 * Different types of constraints that can be applied to a table
		 */
		public static final QualifiedName TableConstraint = 
			QualifiedName.make(
				CAL_DatabaseMetadata.MODULE_NAME, 
				"TableConstraint");

		/**
		 * Stores essential information about a table in the database.
		 */
		public static final QualifiedName TableDescription = 
			QualifiedName.make(
				CAL_DatabaseMetadata.MODULE_NAME, 
				"TableDescription");

		/**
		 * This type constructor allows the developer to uniquely identify a table
		 * in a database.
		 */
		public static final QualifiedName TableReference = 
			QualifiedName.make(
				CAL_DatabaseMetadata.MODULE_NAME, 
				"TableReference");

	}
	/**
	 * This inner class (DataConstructors) contains constants
	 * and methods related to binding to CAL DataConstructors in the Cal.Data.DatabaseMetadata module.
	 */
	public static final class DataConstructors {
		/*
		 * DataConstructors for the Cal.Data.DatabaseMetadata.ConnectionSettings data type.
		 */

		/**
		 * Binding for DataConstructor: Cal.Data.DatabaseMetadata.ConnectionSettings.
		 * @param server
		 * @param port
		 * @param database
		 * @param username
		 * @param password
		 * @return the SourceModule.Expr representing an application of Cal.Data.DatabaseMetadata.ConnectionSettings
		 */
		public static final SourceModel.Expr ConnectionSettings(SourceModel.Expr server, SourceModel.Expr port, SourceModel.Expr database, SourceModel.Expr username, SourceModel.Expr password) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.ConnectionSettings), server, port, database, username, password});
		}

		/**
		 * @see #ConnectionSettings(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param server
		 * @param port
		 * @param database
		 * @param username
		 * @param password
		 * @return org.openquark.cal.compiler.SourceModel.Expr
		 */
		public static final SourceModel.Expr ConnectionSettings(java.lang.String server, int port, java.lang.String database, java.lang.String username, java.lang.String password) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.ConnectionSettings), SourceModel.Expr.makeStringValue(server), SourceModel.Expr.makeIntValue(port), SourceModel.Expr.makeStringValue(database), SourceModel.Expr.makeStringValue(username), SourceModel.Expr.makeStringValue(password)});
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.DatabaseMetadata.ConnectionSettings.
		 * @see #ConnectionSettings(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName ConnectionSettings = 
			QualifiedName.make(
				CAL_DatabaseMetadata.MODULE_NAME, 
				"ConnectionSettings");

		/**
		 * Ordinal of DataConstructor Cal.Data.DatabaseMetadata.ConnectionSettings.
		 * @see #ConnectionSettings(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int ConnectionSettings_ordinal = 0;

		/*
		 * DataConstructors for the Cal.Data.DatabaseMetadata.TableConstraint data type.
		 */

		/**
		 * Binding for DataConstructor: Cal.Data.DatabaseMetadata.PrimaryKeyConstraint.
		 * @param primaryKeyFieldNames
		 * @return the SourceModule.Expr representing an application of Cal.Data.DatabaseMetadata.PrimaryKeyConstraint
		 */
		public static final SourceModel.Expr PrimaryKeyConstraint(SourceModel.Expr primaryKeyFieldNames) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.PrimaryKeyConstraint), primaryKeyFieldNames});
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.DatabaseMetadata.PrimaryKeyConstraint.
		 * @see #PrimaryKeyConstraint(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName PrimaryKeyConstraint = 
			QualifiedName.make(
				CAL_DatabaseMetadata.MODULE_NAME, 
				"PrimaryKeyConstraint");

		/**
		 * Ordinal of DataConstructor Cal.Data.DatabaseMetadata.PrimaryKeyConstraint.
		 * @see #PrimaryKeyConstraint(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int PrimaryKeyConstraint_ordinal = 0;

		/**
		 * Binding for DataConstructor: Cal.Data.DatabaseMetadata.UniqueConstraint.
		 * @param uniqueFieldNames
		 * @return the SourceModule.Expr representing an application of Cal.Data.DatabaseMetadata.UniqueConstraint
		 */
		public static final SourceModel.Expr UniqueConstraint(SourceModel.Expr uniqueFieldNames) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.UniqueConstraint), uniqueFieldNames});
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.DatabaseMetadata.UniqueConstraint.
		 * @see #UniqueConstraint(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName UniqueConstraint = 
			QualifiedName.make(
				CAL_DatabaseMetadata.MODULE_NAME, 
				"UniqueConstraint");

		/**
		 * Ordinal of DataConstructor Cal.Data.DatabaseMetadata.UniqueConstraint.
		 * @see #UniqueConstraint(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int UniqueConstraint_ordinal = 1;

		/**
		 * Binding for DataConstructor: Cal.Data.DatabaseMetadata.ForeignKeyConstraint.
		 * @param foreignTableRef
		 * @param referencedFields
		 * @return the SourceModule.Expr representing an application of Cal.Data.DatabaseMetadata.ForeignKeyConstraint
		 */
		public static final SourceModel.Expr ForeignKeyConstraint(SourceModel.Expr foreignTableRef, SourceModel.Expr referencedFields) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.ForeignKeyConstraint), foreignTableRef, referencedFields});
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.DatabaseMetadata.ForeignKeyConstraint.
		 * @see #ForeignKeyConstraint(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName ForeignKeyConstraint = 
			QualifiedName.make(
				CAL_DatabaseMetadata.MODULE_NAME, 
				"ForeignKeyConstraint");

		/**
		 * Ordinal of DataConstructor Cal.Data.DatabaseMetadata.ForeignKeyConstraint.
		 * @see #ForeignKeyConstraint(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int ForeignKeyConstraint_ordinal = 2;

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Data.DatabaseMetadata module.
	 */
	public static final class Functions {
		/**
		 * This function attempts to locate a child element under the given XML element
		 * which has the same name as the specified element name.  If such child element
		 * can be found, then return the child text stored under this element.  Otherwise,
		 * throw an error.
		 * @param xmlElement (CAL type: <code>Cal.Utilities.XmlBuilder.XmlNode</code>)
		 * @param childElemName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr elementGrandChildText(SourceModel.Expr xmlElement, SourceModel.Expr childElemName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.elementGrandChildText), xmlElement, childElemName});
		}

		/**
		 * @see #elementGrandChildText(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param xmlElement
		 * @param childElemName
		 * @return the SourceModel.Expr representing an application of elementGrandChildText
		 */
		public static final SourceModel.Expr elementGrandChildText(SourceModel.Expr xmlElement, java.lang.String childElemName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.elementGrandChildText), xmlElement, SourceModel.Expr.makeStringValue(childElemName)});
		}

		/**
		 * Name binding for function: elementGrandChildText.
		 * @see #elementGrandChildText(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName elementGrandChildText = 
			QualifiedName.make(
				CAL_DatabaseMetadata.MODULE_NAME, 
				"elementGrandChildText");

		/**
		 * Returns the database name portion from the reference
		 * @param ref (CAL type: <code>Cal.Data.DatabaseMetadata.DatabaseReference</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr getDatabaseNameFromReference(SourceModel.Expr ref) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getDatabaseNameFromReference), ref});
		}

		/**
		 * Name binding for function: getDatabaseNameFromReference.
		 * @see #getDatabaseNameFromReference(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getDatabaseNameFromReference = 
			QualifiedName.make(
				CAL_DatabaseMetadata.MODULE_NAME, 
				"getDatabaseNameFromReference");

		/**
		 * Returns the comment for a field description.
		 * @param fieldDescription (CAL type: <code>Cal.Data.DatabaseMetadata.FieldDescription</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr getFieldComment(SourceModel.Expr fieldDescription) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getFieldComment), fieldDescription});
		}

		/**
		 * Name binding for function: getFieldComment.
		 * @see #getFieldComment(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getFieldComment = 
			QualifiedName.make(
				CAL_DatabaseMetadata.MODULE_NAME, 
				"getFieldComment");

		/**
		 * Returns the SQL data type of the given field.
		 * @param fieldDescription (CAL type: <code>Cal.Data.DatabaseMetadata.FieldDescription</code>)
		 * @return (CAL type: <code>Cal.Data.SqlType.SqlType</code>) 
		 */
		public static final SourceModel.Expr getFieldDataType(SourceModel.Expr fieldDescription) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getFieldDataType), fieldDescription});
		}

		/**
		 * Name binding for function: getFieldDataType.
		 * @see #getFieldDataType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getFieldDataType = 
			QualifiedName.make(
				CAL_DatabaseMetadata.MODULE_NAME, 
				"getFieldDataType");

		/**
		 * Returns the name of the given field
		 * @param fd (CAL type: <code>Cal.Data.DatabaseMetadata.FieldDescription</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr getFieldName(SourceModel.Expr fd) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getFieldName), fd});
		}

		/**
		 * Name binding for function: getFieldName.
		 * @see #getFieldName(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getFieldName = 
			QualifiedName.make(CAL_DatabaseMetadata.MODULE_NAME, "getFieldName");

		/**
		 * Helper binding method for function: getPrimaryKeyFieldNames. 
		 * @param constraint
		 * @return the SourceModule.expr representing an application of getPrimaryKeyFieldNames
		 */
		public static final SourceModel.Expr getPrimaryKeyFieldNames(SourceModel.Expr constraint) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getPrimaryKeyFieldNames), constraint});
		}

		/**
		 * Name binding for function: getPrimaryKeyFieldNames.
		 * @see #getPrimaryKeyFieldNames(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getPrimaryKeyFieldNames = 
			QualifiedName.make(
				CAL_DatabaseMetadata.MODULE_NAME, 
				"getPrimaryKeyFieldNames");

		/**
		 * Returns the table constraints applied to the given table
		 * @param td (CAL type: <code>Cal.Data.DatabaseMetadata.TableDescription</code>)
		 * @return (CAL type: <code>[Cal.Data.DatabaseMetadata.TableConstraint]</code>) 
		 */
		public static final SourceModel.Expr getTableConstraints(SourceModel.Expr td) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getTableConstraints), td});
		}

		/**
		 * Name binding for function: getTableConstraints.
		 * @see #getTableConstraints(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getTableConstraints = 
			QualifiedName.make(
				CAL_DatabaseMetadata.MODULE_NAME, 
				"getTableConstraints");

		/**
		 * Returns the field contained in the given table
		 * @param td (CAL type: <code>Cal.Data.DatabaseMetadata.TableDescription</code>)
		 * @return (CAL type: <code>[Cal.Data.DatabaseMetadata.FieldDescription]</code>) 
		 */
		public static final SourceModel.Expr getTableFields(SourceModel.Expr td) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getTableFields), td});
		}

		/**
		 * Name binding for function: getTableFields.
		 * @see #getTableFields(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getTableFields = 
			QualifiedName.make(
				CAL_DatabaseMetadata.MODULE_NAME, 
				"getTableFields");

		/**
		 * A convenience function for returning the key fields in the given table
		 * @param td (CAL type: <code>Cal.Data.DatabaseMetadata.TableDescription</code>)
		 * @return (CAL type: <code>[Cal.Data.DatabaseMetadata.FieldDescription]</code>) 
		 */
		public static final SourceModel.Expr getTableKeyFields(SourceModel.Expr td) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getTableKeyFields), td});
		}

		/**
		 * Name binding for function: getTableKeyFields.
		 * @see #getTableKeyFields(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getTableKeyFields = 
			QualifiedName.make(
				CAL_DatabaseMetadata.MODULE_NAME, 
				"getTableKeyFields");

		/**
		 * A convenience function for getting the table name of the given table
		 * @param td (CAL type: <code>Cal.Data.DatabaseMetadata.TableDescription</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr getTableName(SourceModel.Expr td) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getTableName), td});
		}

		/**
		 * Name binding for function: getTableName.
		 * @see #getTableName(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getTableName = 
			QualifiedName.make(CAL_DatabaseMetadata.MODULE_NAME, "getTableName");

		/**
		 * Returns the table name portion from the reference
		 * @param ref (CAL type: <code>Cal.Data.DatabaseMetadata.TableReference</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr getTableNameFromReference(SourceModel.Expr ref) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getTableNameFromReference), ref});
		}

		/**
		 * Name binding for function: getTableNameFromReference.
		 * @see #getTableNameFromReference(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getTableNameFromReference = 
			QualifiedName.make(
				CAL_DatabaseMetadata.MODULE_NAME, 
				"getTableNameFromReference");

		/**
		 * Returns the <code>Cal.Data.DatabaseMetadata.TableReference</code> that can be used to reference this table
		 * @param td (CAL type: <code>Cal.Data.DatabaseMetadata.TableDescription</code>)
		 * @return (CAL type: <code>Cal.Data.DatabaseMetadata.TableReference</code>) 
		 */
		public static final SourceModel.Expr getTableReference(SourceModel.Expr td) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getTableReference), td});
		}

		/**
		 * Name binding for function: getTableReference.
		 * @see #getTableReference(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getTableReference = 
			QualifiedName.make(
				CAL_DatabaseMetadata.MODULE_NAME, 
				"getTableReference");

		/**
		 * Returns true if the given field is nullable
		 * @param fd (CAL type: <code>Cal.Data.DatabaseMetadata.FieldDescription</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr isFieldNullable(SourceModel.Expr fd) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isFieldNullable), fd});
		}

		/**
		 * Name binding for function: isFieldNullable.
		 * @see #isFieldNullable(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isFieldNullable = 
			QualifiedName.make(
				CAL_DatabaseMetadata.MODULE_NAME, 
				"isFieldNullable");

		/**
		 * Helper binding method for function: isForeignKeyConstraint. 
		 * @param constraint
		 * @return the SourceModule.expr representing an application of isForeignKeyConstraint
		 */
		public static final SourceModel.Expr isForeignKeyConstraint(SourceModel.Expr constraint) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isForeignKeyConstraint), constraint});
		}

		/**
		 * Name binding for function: isForeignKeyConstraint.
		 * @see #isForeignKeyConstraint(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isForeignKeyConstraint = 
			QualifiedName.make(
				CAL_DatabaseMetadata.MODULE_NAME, 
				"isForeignKeyConstraint");

		/**
		 * Helper binding method for function: isPrimaryKeyConstraint. 
		 * @param constraint
		 * @return the SourceModule.expr representing an application of isPrimaryKeyConstraint
		 */
		public static final SourceModel.Expr isPrimaryKeyConstraint(SourceModel.Expr constraint) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isPrimaryKeyConstraint), constraint});
		}

		/**
		 * Name binding for function: isPrimaryKeyConstraint.
		 * @see #isPrimaryKeyConstraint(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isPrimaryKeyConstraint = 
			QualifiedName.make(
				CAL_DatabaseMetadata.MODULE_NAME, 
				"isPrimaryKeyConstraint");

		/**
		 * Helper binding method for function: isUniqueConstraint. 
		 * @param constraint
		 * @return the SourceModule.expr representing an application of isUniqueConstraint
		 */
		public static final SourceModel.Expr isUniqueConstraint(SourceModel.Expr constraint) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isUniqueConstraint), constraint});
		}

		/**
		 * Name binding for function: isUniqueConstraint.
		 * @see #isUniqueConstraint(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isUniqueConstraint = 
			QualifiedName.make(
				CAL_DatabaseMetadata.MODULE_NAME, 
				"isUniqueConstraint");

		/**
		 * Helper binding method for function: makeConnectionSettings. 
		 * @param server
		 * @param port
		 * @param database
		 * @param username
		 * @param password
		 * @return the SourceModule.expr representing an application of makeConnectionSettings
		 */
		public static final SourceModel.Expr makeConnectionSettings(SourceModel.Expr server, SourceModel.Expr port, SourceModel.Expr database, SourceModel.Expr username, SourceModel.Expr password) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeConnectionSettings), server, port, database, username, password});
		}

		/**
		 * @see #makeConnectionSettings(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param server
		 * @param port
		 * @param database
		 * @param username
		 * @param password
		 * @return the SourceModel.Expr representing an application of makeConnectionSettings
		 */
		public static final SourceModel.Expr makeConnectionSettings(java.lang.String server, int port, java.lang.String database, java.lang.String username, java.lang.String password) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeConnectionSettings), SourceModel.Expr.makeStringValue(server), SourceModel.Expr.makeIntValue(port), SourceModel.Expr.makeStringValue(database), SourceModel.Expr.makeStringValue(username), SourceModel.Expr.makeStringValue(password)});
		}

		/**
		 * Name binding for function: makeConnectionSettings.
		 * @see #makeConnectionSettings(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeConnectionSettings = 
			QualifiedName.make(
				CAL_DatabaseMetadata.MODULE_NAME, 
				"makeConnectionSettings");

		/**
		 * Makes a new database reference
		 * @param databaseName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Data.DatabaseMetadata.DatabaseReference</code>) 
		 */
		public static final SourceModel.Expr makeDatabaseReference(SourceModel.Expr databaseName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeDatabaseReference), databaseName});
		}

		/**
		 * @see #makeDatabaseReference(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param databaseName
		 * @return the SourceModel.Expr representing an application of makeDatabaseReference
		 */
		public static final SourceModel.Expr makeDatabaseReference(java.lang.String databaseName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeDatabaseReference), SourceModel.Expr.makeStringValue(databaseName)});
		}

		/**
		 * Name binding for function: makeDatabaseReference.
		 * @see #makeDatabaseReference(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeDatabaseReference = 
			QualifiedName.make(
				CAL_DatabaseMetadata.MODULE_NAME, 
				"makeDatabaseReference");

		/**
		 * Constructor function
		 * @param fieldName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @param dataType (CAL type: <code>Cal.Data.SqlType.SqlType</code>)
		 * @param nullable (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 * @return (CAL type: <code>Cal.Data.DatabaseMetadata.FieldDescription</code>) 
		 */
		public static final SourceModel.Expr makeFieldDescription(SourceModel.Expr fieldName, SourceModel.Expr dataType, SourceModel.Expr nullable) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeFieldDescription), fieldName, dataType, nullable});
		}

		/**
		 * @see #makeFieldDescription(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param fieldName
		 * @param dataType
		 * @param nullable
		 * @return the SourceModel.Expr representing an application of makeFieldDescription
		 */
		public static final SourceModel.Expr makeFieldDescription(java.lang.String fieldName, SourceModel.Expr dataType, boolean nullable) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeFieldDescription), SourceModel.Expr.makeStringValue(fieldName), dataType, SourceModel.Expr.makeBooleanValue(nullable)});
		}

		/**
		 * Name binding for function: makeFieldDescription.
		 * @see #makeFieldDescription(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeFieldDescription = 
			QualifiedName.make(
				CAL_DatabaseMetadata.MODULE_NAME, 
				"makeFieldDescription");

		/**
		 * Constructor function
		 * @param fieldName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @param dataType (CAL type: <code>Cal.Data.SqlType.SqlType</code>)
		 * @param nullable (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 * @param comment (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Data.DatabaseMetadata.FieldDescription</code>) 
		 */
		public static final SourceModel.Expr makeFieldDescriptionWithComment(SourceModel.Expr fieldName, SourceModel.Expr dataType, SourceModel.Expr nullable, SourceModel.Expr comment) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeFieldDescriptionWithComment), fieldName, dataType, nullable, comment});
		}

		/**
		 * @see #makeFieldDescriptionWithComment(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param fieldName
		 * @param dataType
		 * @param nullable
		 * @param comment
		 * @return the SourceModel.Expr representing an application of makeFieldDescriptionWithComment
		 */
		public static final SourceModel.Expr makeFieldDescriptionWithComment(java.lang.String fieldName, SourceModel.Expr dataType, boolean nullable, java.lang.String comment) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeFieldDescriptionWithComment), SourceModel.Expr.makeStringValue(fieldName), dataType, SourceModel.Expr.makeBooleanValue(nullable), SourceModel.Expr.makeStringValue(comment)});
		}

		/**
		 * Name binding for function: makeFieldDescriptionWithComment.
		 * @see #makeFieldDescriptionWithComment(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeFieldDescriptionWithComment = 
			QualifiedName.make(
				CAL_DatabaseMetadata.MODULE_NAME, 
				"makeFieldDescriptionWithComment");

		/**
		 * Constructor function
		 * @param tableRef (CAL type: <code>Cal.Data.DatabaseMetadata.TableReference</code>)
		 * @param fields (CAL type: <code>[Cal.Data.DatabaseMetadata.FieldDescription]</code>)
		 * @param constraints (CAL type: <code>[Cal.Data.DatabaseMetadata.TableConstraint]</code>)
		 * @return (CAL type: <code>Cal.Data.DatabaseMetadata.TableDescription</code>) 
		 */
		public static final SourceModel.Expr makeTableDescription(SourceModel.Expr tableRef, SourceModel.Expr fields, SourceModel.Expr constraints) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeTableDescription), tableRef, fields, constraints});
		}

		/**
		 * Name binding for function: makeTableDescription.
		 * @see #makeTableDescription(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeTableDescription = 
			QualifiedName.make(
				CAL_DatabaseMetadata.MODULE_NAME, 
				"makeTableDescription");

		/**
		 * Makes a new table reference
		 * @param tableName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Data.DatabaseMetadata.TableReference</code>) 
		 */
		public static final SourceModel.Expr makeTableReference(SourceModel.Expr tableName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeTableReference), tableName});
		}

		/**
		 * @see #makeTableReference(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param tableName
		 * @return the SourceModel.Expr representing an application of makeTableReference
		 */
		public static final SourceModel.Expr makeTableReference(java.lang.String tableName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeTableReference), SourceModel.Expr.makeStringValue(tableName)});
		}

		/**
		 * Name binding for function: makeTableReference.
		 * @see #makeTableReference(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeTableReference = 
			QualifiedName.make(
				CAL_DatabaseMetadata.MODULE_NAME, 
				"makeTableReference");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = 317113984;

}
