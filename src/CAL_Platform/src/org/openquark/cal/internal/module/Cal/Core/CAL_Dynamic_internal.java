/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_Dynamic_internal.java)
 * was generated from CAL module: Cal.Core.Dynamic.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Core.Dynamic module from Java code.
 *  
 * Creation date: Fri Mar 16 13:11:57 PST 2007
 * --!>
 *  
 */

package org.openquark.cal.internal.module.Cal.Core;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * Defines the <code>Cal.Core.Dynamic.Dynamic</code> type along with a variety of functions
 * for working with it. Based on the Dynamics module in Hugs and GHC, and the paper 
 * "Scrap your boilerplate: a practical design for generic programming".
 * @author Bo Ilic
 * @author James Wright
 */
public final class CAL_Dynamic_internal {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Core.Dynamic");

	/**
	 * This inner class (DataConstructors) contains constants
	 * and methods related to binding to CAL DataConstructors in the Cal.Core.Dynamic module.
	 */
	public static final class DataConstructors {
		/*
		 * DataConstructors for the Cal.Core.Dynamic.Dynamic data type.
		 */

		/**
		 * 
		 * @param type (CAL type: <code>Cal.Core.Prelude.TypeRep</code>)
		 *          type representation of the value
		 * @param untypedValue (CAL type: <code>Cal.Core.Prelude.CalValue</code>)
		 *          placeholder for the untyped value
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr Dynamic(SourceModel.Expr type, SourceModel.Expr untypedValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.Dynamic), type, untypedValue});
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Dynamic.Dynamic.
		 * @see #Dynamic(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName Dynamic = 
			QualifiedName.make(CAL_Dynamic_internal.MODULE_NAME, "Dynamic");

		/**
		 * Ordinal of DataConstructor Cal.Core.Dynamic.Dynamic.
		 * @see #Dynamic(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int Dynamic_ordinal = 0;

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Core.Dynamic module.
	 */
	public static final class Functions {
		/**
		 * Helper binding method for function: appendDynamicRecordExamples. 
		 * @return the SourceModule.expr representing an application of appendDynamicRecordExamples
		 */
		public static final SourceModel.Expr appendDynamicRecordExamples() {
			return 
				SourceModel.Expr.Var.make(Functions.appendDynamicRecordExamples);
		}

		/**
		 * Name binding for function: appendDynamicRecordExamples.
		 * @see #appendDynamicRecordExamples()
		 */
		public static final QualifiedName appendDynamicRecordExamples = 
			QualifiedName.make(
				CAL_Dynamic_internal.MODULE_NAME, 
				"appendDynamicRecordExamples");

		/**
		 * Helper binding method for function: appendRecordExamples. 
		 * @return the SourceModule.expr representing an application of appendRecordExamples
		 */
		public static final SourceModel.Expr appendRecordExamples() {
			return SourceModel.Expr.Var.make(Functions.appendRecordExamples);
		}

		/**
		 * Name binding for function: appendRecordExamples.
		 * @see #appendRecordExamples()
		 */
		public static final QualifiedName appendRecordExamples = 
			QualifiedName.make(
				CAL_Dynamic_internal.MODULE_NAME, 
				"appendRecordExamples");

		/**
		 * Primitive function for appending two records together, yielding a single
		 * record whose fields are a union of the field names of the two records.
		 * Returns an <code>Cal.Core.Prelude.CalValue</code> representing a record containing the union
		 * of the fields of the two records.  If field names overlap, then the field
		 * value of the first record will be chosen for each field.
		 * @param arg_1 (CAL type: <code>{r}</code>)
		 * @param arg_2 (CAL type: <code>{s}</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.CalValue</code>) 
		 */
		public static final SourceModel.Expr appendRecordPrimitive(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.appendRecordPrimitive), arg_1, arg_2});
		}

		/**
		 * Name binding for function: appendRecordPrimitive.
		 * @see #appendRecordPrimitive(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName appendRecordPrimitive = 
			QualifiedName.make(
				CAL_Dynamic_internal.MODULE_NAME, 
				"appendRecordPrimitive");

		/**
		 * Helper for <code>Cal.Core.Dynamic.appendRecord</code> and <code>Cal.Core.Dynamic.appendDynamicRecord</code>.  Accepts two <code>Cal.Core.Prelude.TypeRep</code>s 
		 * representing record types and returns the <code>Cal.Core.Prelude.TypeRep</code> representing the type of the 
		 * record that contains the union of the fields of the two records.  A runtime error
		 * is signalled if either <code>typeRep1</code> or <code>typeRep2</code> do not represent record types.
		 * @param typeRep1 (CAL type: <code>Cal.Core.Prelude.TypeRep</code>)
		 *          First <code>TypeRep</code> to add to the combined <code>TypeRep</code>
		 * @param typeRep2 (CAL type: <code>Cal.Core.Prelude.TypeRep</code>)
		 *          Second <code>TypeRep</code> to add to the combined <code>TypeRep</code>
		 * @return (CAL type: <code>Cal.Core.Prelude.TypeRep</code>) 
		 *          <code>TypeRep</code> of a record containing the union of the fields of <code>typeRep1</code> and <code>typeRep2</code>
		 * (favouring the fields of <code>typeRep1</code> in case of overlap).
		 */
		public static final SourceModel.Expr appendRecordTypeRep(SourceModel.Expr typeRep1, SourceModel.Expr typeRep2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.appendRecordTypeRep), typeRep1, typeRep2});
		}

		/**
		 * Name binding for function: appendRecordTypeRep.
		 * @see #appendRecordTypeRep(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName appendRecordTypeRep = 
			QualifiedName.make(
				CAL_Dynamic_internal.MODULE_NAME, 
				"appendRecordTypeRep");

		/**
		 * Helper binding method for function: concatDynamicRecord. 
		 * @param recordList
		 * @return the SourceModule.expr representing an application of concatDynamicRecord
		 */
		public static final SourceModel.Expr concatDynamicRecord(SourceModel.Expr recordList) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.concatDynamicRecord), recordList});
		}

		/**
		 * Name binding for function: concatDynamicRecord.
		 * @see #concatDynamicRecord(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName concatDynamicRecord = 
			QualifiedName.make(
				CAL_Dynamic_internal.MODULE_NAME, 
				"concatDynamicRecord");

		/**
		 * Helper binding method for function: dynamicAppExamples. 
		 * @return the SourceModule.expr representing an application of dynamicAppExamples
		 */
		public static final SourceModel.Expr dynamicAppExamples() {
			return SourceModel.Expr.Var.make(Functions.dynamicAppExamples);
		}

		/**
		 * Name binding for function: dynamicAppExamples.
		 * @see #dynamicAppExamples()
		 */
		public static final QualifiedName dynamicAppExamples = 
			QualifiedName.make(
				CAL_Dynamic_internal.MODULE_NAME, 
				"dynamicAppExamples");

		/**
		 * Helper binding method for function: dynamicApplyExamples. 
		 * @return the SourceModule.expr representing an application of dynamicApplyExamples
		 */
		public static final SourceModel.Expr dynamicApplyExamples() {
			return SourceModel.Expr.Var.make(Functions.dynamicApplyExamples);
		}

		/**
		 * Name binding for function: dynamicApplyExamples.
		 * @see #dynamicApplyExamples()
		 */
		public static final QualifiedName dynamicApplyExamples = 
			QualifiedName.make(
				CAL_Dynamic_internal.MODULE_NAME, 
				"dynamicApplyExamples");

		/**
		 * Helper binding method for function: dynamicRecordFieldNamesExamples. 
		 * @return the SourceModule.expr representing an application of dynamicRecordFieldNamesExamples
		 */
		public static final SourceModel.Expr dynamicRecordFieldNamesExamples() {
			return 
				SourceModel.Expr.Var.make(
					Functions.dynamicRecordFieldNamesExamples);
		}

		/**
		 * Name binding for function: dynamicRecordFieldNamesExamples.
		 * @see #dynamicRecordFieldNamesExamples()
		 */
		public static final QualifiedName dynamicRecordFieldNamesExamples = 
			QualifiedName.make(
				CAL_Dynamic_internal.MODULE_NAME, 
				"dynamicRecordFieldNamesExamples");

		/**
		 * Helper binding method for function: dynamicRecordFieldValueExamples. 
		 * @return the SourceModule.expr representing an application of dynamicRecordFieldValueExamples
		 */
		public static final SourceModel.Expr dynamicRecordFieldValueExamples() {
			return 
				SourceModel.Expr.Var.make(
					Functions.dynamicRecordFieldValueExamples);
		}

		/**
		 * Name binding for function: dynamicRecordFieldValueExamples.
		 * @see #dynamicRecordFieldValueExamples()
		 */
		public static final QualifiedName dynamicRecordFieldValueExamples = 
			QualifiedName.make(
				CAL_Dynamic_internal.MODULE_NAME, 
				"dynamicRecordFieldValueExamples");

		/**
		 * Helper binding method for function: dynamicRecordFieldValuesExamples. 
		 * @return the SourceModule.expr representing an application of dynamicRecordFieldValuesExamples
		 */
		public static final SourceModel.Expr dynamicRecordFieldValuesExamples() {
			return 
				SourceModel.Expr.Var.make(
					Functions.dynamicRecordFieldValuesExamples);
		}

		/**
		 * Name binding for function: dynamicRecordFieldValuesExamples.
		 * @see #dynamicRecordFieldValuesExamples()
		 */
		public static final QualifiedName dynamicRecordFieldValuesExamples = 
			QualifiedName.make(
				CAL_Dynamic_internal.MODULE_NAME, 
				"dynamicRecordFieldValuesExamples");

		/**
		 * Helper binding method for function: emptyDynamicRecord. 
		 * @return the SourceModule.expr representing an application of emptyDynamicRecord
		 */
		public static final SourceModel.Expr emptyDynamicRecord() {
			return SourceModel.Expr.Var.make(Functions.emptyDynamicRecord);
		}

		/**
		 * Name binding for function: emptyDynamicRecord.
		 * @see #emptyDynamicRecord()
		 */
		public static final QualifiedName emptyDynamicRecord = 
			QualifiedName.make(
				CAL_Dynamic_internal.MODULE_NAME, 
				"emptyDynamicRecord");

		/**
		 * Converts an ordinal field name to a corresponding <code>Cal.Core.Prelude.Int</code> value.
		 * Eg, <code>fieldOrdinal "#3" == 3.</code>
		 * A runtime error will be signalled if the field name is not a valid ordinal
		 * field name.
		 * @param fieldName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          An ordinal field name to convert to an integer.
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the <code>Cal.Core.Prelude.Int</code> value represented by fieldName
		 */
		public static final SourceModel.Expr fieldOrdinal(SourceModel.Expr fieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fieldOrdinal), fieldName});
		}

		/**
		 * @see #fieldOrdinal(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param fieldName
		 * @return the SourceModel.Expr representing an application of fieldOrdinal
		 */
		public static final SourceModel.Expr fieldOrdinal(java.lang.String fieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fieldOrdinal), SourceModel.Expr.makeStringValue(fieldName)});
		}

		/**
		 * Name binding for function: fieldOrdinal.
		 * @see #fieldOrdinal(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fieldOrdinal = 
			QualifiedName.make(CAL_Dynamic_internal.MODULE_NAME, "fieldOrdinal");

		/**
		 * Helper binding method for function: fieldOrdinalExamples. 
		 * @return the SourceModule.expr representing an application of fieldOrdinalExamples
		 */
		public static final SourceModel.Expr fieldOrdinalExamples() {
			return SourceModel.Expr.Var.make(Functions.fieldOrdinalExamples);
		}

		/**
		 * Name binding for function: fieldOrdinalExamples.
		 * @see #fieldOrdinalExamples()
		 */
		public static final QualifiedName fieldOrdinalExamples = 
			QualifiedName.make(
				CAL_Dynamic_internal.MODULE_NAME, 
				"fieldOrdinalExamples");

		/**
		 * <code>fieldValues</code> returns the list of field values of a record, ordered by field-name order.
		 * Ordinal field names, such as <code>"#1"</code>, <code>"#2"</code> etc, are first, in ordinal order.
		 * Textual field names such as <code>"orderDate"</code>, <code>"shipDate"</code> are next, in alphabetical order.
		 * @param recordValue (CAL type: <code>{r}</code>)
		 *          the record whose field values are to be returned.
		 * @return (CAL type: <code>[Cal.Core.Prelude.CalValue]</code>) 
		 *          the field values of the record, in field-name order.
		 */
		public static final SourceModel.Expr fieldValues(SourceModel.Expr recordValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fieldValues), recordValue});
		}

		/**
		 * Name binding for function: fieldValues.
		 * @see #fieldValues(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fieldValues = 
			QualifiedName.make(CAL_Dynamic_internal.MODULE_NAME, "fieldValues");

		/**
		 * <code>fieldNamesPrimitive</code> takes a record, and returns a <code>Cal.Core.Prelude.JList</code> that is actually a 
		 * <code>java.util.List</code> of <code>Cal.Core.Prelude.CalValue</code> objects representing the field values of the record
		 * in field-name order.
		 * @param arg_1 (CAL type: <code>{r}</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.JList</code>) 
		 */
		public static final SourceModel.Expr fieldValuesPrimitive(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fieldValuesPrimitive), arg_1});
		}

		/**
		 * Name binding for function: fieldValuesPrimitive.
		 * @see #fieldValuesPrimitive(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fieldValuesPrimitive = 
			QualifiedName.make(
				CAL_Dynamic_internal.MODULE_NAME, 
				"fieldValuesPrimitive");

		/**
		 * Helper binding method for function: fromDynamicListExamples. 
		 * @return the SourceModule.expr representing an application of fromDynamicListExamples
		 */
		public static final SourceModel.Expr fromDynamicListExamples() {
			return SourceModel.Expr.Var.make(Functions.fromDynamicListExamples);
		}

		/**
		 * Name binding for function: fromDynamicListExamples.
		 * @see #fromDynamicListExamples()
		 */
		public static final QualifiedName fromDynamicListExamples = 
			QualifiedName.make(
				CAL_Dynamic_internal.MODULE_NAME, 
				"fromDynamicListExamples");

		/**
		 * Helper binding method for function: insertDynamicRecordDynamicFieldExamples. 
		 * @return the SourceModule.expr representing an application of insertDynamicRecordDynamicFieldExamples
		 */
		public static final SourceModel.Expr insertDynamicRecordDynamicFieldExamples() {
			return 
				SourceModel.Expr.Var.make(
					Functions.insertDynamicRecordDynamicFieldExamples);
		}

		/**
		 * Name binding for function: insertDynamicRecordDynamicFieldExamples.
		 * @see #insertDynamicRecordDynamicFieldExamples()
		 */
		public static final QualifiedName insertDynamicRecordDynamicFieldExamples = 
			QualifiedName.make(
				CAL_Dynamic_internal.MODULE_NAME, 
				"insertDynamicRecordDynamicFieldExamples");

		/**
		 * Helper binding method for function: insertDynamicRecordFieldExamples. 
		 * @return the SourceModule.expr representing an application of insertDynamicRecordFieldExamples
		 */
		public static final SourceModel.Expr insertDynamicRecordFieldExamples() {
			return 
				SourceModel.Expr.Var.make(
					Functions.insertDynamicRecordFieldExamples);
		}

		/**
		 * Name binding for function: insertDynamicRecordFieldExamples.
		 * @see #insertDynamicRecordFieldExamples()
		 */
		public static final QualifiedName insertDynamicRecordFieldExamples = 
			QualifiedName.make(
				CAL_Dynamic_internal.MODULE_NAME, 
				"insertDynamicRecordFieldExamples");

		/**
		 * Primitive function for adding an ordinally-named field to a record.
		 * Accepts a record value, a field name, and a value, and returns an <code>Cal.Core.Prelude.CalValue</code>
		 * that represents a record extension of the record with the specified
		 * field.
		 * @param arg_1 (CAL type: <code>{r}</code>)
		 * @param arg_2 (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @param arg_3 (CAL type: <code>a</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.CalValue</code>) 
		 */
		public static final SourceModel.Expr insertOrdinalRecordFieldPrimitive(SourceModel.Expr arg_1, SourceModel.Expr arg_2, SourceModel.Expr arg_3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.insertOrdinalRecordFieldPrimitive), arg_1, arg_2, arg_3});
		}

		/**
		 * @see #insertOrdinalRecordFieldPrimitive(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @param arg_3
		 * @return the SourceModel.Expr representing an application of insertOrdinalRecordFieldPrimitive
		 */
		public static final SourceModel.Expr insertOrdinalRecordFieldPrimitive(SourceModel.Expr arg_1, int arg_2, SourceModel.Expr arg_3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.insertOrdinalRecordFieldPrimitive), arg_1, SourceModel.Expr.makeIntValue(arg_2), arg_3});
		}

		/**
		 * Name binding for function: insertOrdinalRecordFieldPrimitive.
		 * @see #insertOrdinalRecordFieldPrimitive(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName insertOrdinalRecordFieldPrimitive = 
			QualifiedName.make(
				CAL_Dynamic_internal.MODULE_NAME, 
				"insertOrdinalRecordFieldPrimitive");

		/**
		 * Helper binding method for function: insertRecordFieldExamples. 
		 * @return the SourceModule.expr representing an application of insertRecordFieldExamples
		 */
		public static final SourceModel.Expr insertRecordFieldExamples() {
			return 
				SourceModel.Expr.Var.make(Functions.insertRecordFieldExamples);
		}

		/**
		 * Name binding for function: insertRecordFieldExamples.
		 * @see #insertRecordFieldExamples()
		 */
		public static final QualifiedName insertRecordFieldExamples = 
			QualifiedName.make(
				CAL_Dynamic_internal.MODULE_NAME, 
				"insertRecordFieldExamples");

		/**
		 * Helper for <code>Cal.Core.Dynamic.insertRecordField</code> and <code>Cal.Core.Dynamic.insertDynamicRecordField</code>.  Accepts a record, a
		 * <code>Cal.Core.Prelude.TypeRep</code> for the record, a field name for the new field, and a value to set
		 * the new field to, and the TypeRep of the field.
		 * If the field name is already present in the record, a new record will be
		 * returned that contains the new value in place of the old value.  If <code>recordType</code> does 
		 * not represent a record type, a runtime error will be signalled.
		 * @param recordValue (CAL type: <code>{r}</code>)
		 *          Record to use as a base record
		 * @param recordType (CAL type: <code>Cal.Core.Prelude.TypeRep</code>)
		 *          <code>TypeRep</code> of recordValue
		 * @param fieldName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          Name of the new field
		 * @param fieldValue (CAL type: <code>a</code>)
		 *          Value to set the new field to
		 * @param fieldType (CAL type: <code>Cal.Core.Prelude.TypeRep</code>)
		 *          TypeRep of fieldValue
		 * @return (CAL type: <code>Cal.Core.Dynamic.Dynamic</code>) 
		 *          <code>Dynamic</code> containing a new record extended by a field called fieldName having
		 * the value fieldValue.
		 */
		public static final SourceModel.Expr insertRecordFieldHelper(SourceModel.Expr recordValue, SourceModel.Expr recordType, SourceModel.Expr fieldName, SourceModel.Expr fieldValue, SourceModel.Expr fieldType) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.insertRecordFieldHelper), recordValue, recordType, fieldName, fieldValue, fieldType});
		}

		/**
		 * @see #insertRecordFieldHelper(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param recordValue
		 * @param recordType
		 * @param fieldName
		 * @param fieldValue
		 * @param fieldType
		 * @return the SourceModel.Expr representing an application of insertRecordFieldHelper
		 */
		public static final SourceModel.Expr insertRecordFieldHelper(SourceModel.Expr recordValue, SourceModel.Expr recordType, java.lang.String fieldName, SourceModel.Expr fieldValue, SourceModel.Expr fieldType) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.insertRecordFieldHelper), recordValue, recordType, SourceModel.Expr.makeStringValue(fieldName), fieldValue, fieldType});
		}

		/**
		 * Name binding for function: insertRecordFieldHelper.
		 * @see #insertRecordFieldHelper(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName insertRecordFieldHelper = 
			QualifiedName.make(
				CAL_Dynamic_internal.MODULE_NAME, 
				"insertRecordFieldHelper");

		/**
		 * Primitive function for adding a textually-named field to a record.
		 * Accepts a record value, a field name, and a value, and returns an <code>Cal.Core.Prelude.CalValue</code>
		 * that represents a record extension of the record with the specified
		 * field.
		 * @param arg_1 (CAL type: <code>{r}</code>)
		 * @param arg_2 (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @param arg_3 (CAL type: <code>a</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.CalValue</code>) 
		 */
		public static final SourceModel.Expr insertTextualRecordFieldPrimitive(SourceModel.Expr arg_1, SourceModel.Expr arg_2, SourceModel.Expr arg_3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.insertTextualRecordFieldPrimitive), arg_1, arg_2, arg_3});
		}

		/**
		 * @see #insertTextualRecordFieldPrimitive(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @param arg_3
		 * @return the SourceModel.Expr representing an application of insertTextualRecordFieldPrimitive
		 */
		public static final SourceModel.Expr insertTextualRecordFieldPrimitive(SourceModel.Expr arg_1, java.lang.String arg_2, SourceModel.Expr arg_3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.insertTextualRecordFieldPrimitive), arg_1, SourceModel.Expr.makeStringValue(arg_2), arg_3});
		}

		/**
		 * Name binding for function: insertTextualRecordFieldPrimitive.
		 * @see #insertTextualRecordFieldPrimitive(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName insertTextualRecordFieldPrimitive = 
			QualifiedName.make(
				CAL_Dynamic_internal.MODULE_NAME, 
				"insertTextualRecordFieldPrimitive");

		/**
		 * Helper binding method for function: isEmptyDynamicRecord. 
		 * @param dynamicRecord
		 * @return the SourceModule.expr representing an application of isEmptyDynamicRecord
		 */
		public static final SourceModel.Expr isEmptyDynamicRecord(SourceModel.Expr dynamicRecord) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isEmptyDynamicRecord), dynamicRecord});
		}

		/**
		 * Name binding for function: isEmptyDynamicRecord.
		 * @see #isEmptyDynamicRecord(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isEmptyDynamicRecord = 
			QualifiedName.make(
				CAL_Dynamic_internal.MODULE_NAME, 
				"isEmptyDynamicRecord");

		/**
		 * 
		 * @param ordinalFieldName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the provided string represents an ordinal field name, or <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr isOrdinalFieldName(SourceModel.Expr ordinalFieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isOrdinalFieldName), ordinalFieldName});
		}

		/**
		 * @see #isOrdinalFieldName(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param ordinalFieldName
		 * @return the SourceModel.Expr representing an application of isOrdinalFieldName
		 */
		public static final SourceModel.Expr isOrdinalFieldName(java.lang.String ordinalFieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isOrdinalFieldName), SourceModel.Expr.makeStringValue(ordinalFieldName)});
		}

		/**
		 * Name binding for function: isOrdinalFieldName.
		 * @see #isOrdinalFieldName(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isOrdinalFieldName = 
			QualifiedName.make(
				CAL_Dynamic_internal.MODULE_NAME, 
				"isOrdinalFieldName");

		/**
		 * Helper binding method for function: isOrdinalFieldNameExamples. 
		 * @return the SourceModule.expr representing an application of isOrdinalFieldNameExamples
		 */
		public static final SourceModel.Expr isOrdinalFieldNameExamples() {
			return 
				SourceModel.Expr.Var.make(Functions.isOrdinalFieldNameExamples);
		}

		/**
		 * Name binding for function: isOrdinalFieldNameExamples.
		 * @see #isOrdinalFieldNameExamples()
		 */
		public static final QualifiedName isOrdinalFieldNameExamples = 
			QualifiedName.make(
				CAL_Dynamic_internal.MODULE_NAME, 
				"isOrdinalFieldNameExamples");

		/**
		 * WARNING- this function must not be public.
		 * <p>
		 * A helper function to extract the index of a field name from a record.
		 * This is useful for implementing certain primitives in the Prelude so that we do not need
		 * to repeatedly look up and validate field names.
		 * 
		 * @param recordValue (CAL type: <code>{r}</code>)
		 *          the record to be checked for the index of a field of the given name.
		 * @param fieldName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the field name to find the index of e.g. <code>"#2"</code>, <code>"orderDate"</code>.
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          zero-based index of the field in the record, or -1 if the field is not in the record.
		 */
		public static final SourceModel.Expr recordFieldIndex(SourceModel.Expr recordValue, SourceModel.Expr fieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.recordFieldIndex), recordValue, fieldName});
		}

		/**
		 * @see #recordFieldIndex(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param recordValue
		 * @param fieldName
		 * @return the SourceModel.Expr representing an application of recordFieldIndex
		 */
		public static final SourceModel.Expr recordFieldIndex(SourceModel.Expr recordValue, java.lang.String fieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.recordFieldIndex), recordValue, SourceModel.Expr.makeStringValue(fieldName)});
		}

		/**
		 * Name binding for function: recordFieldIndex.
		 * @see #recordFieldIndex(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName recordFieldIndex = 
			QualifiedName.make(
				CAL_Dynamic_internal.MODULE_NAME, 
				"recordFieldIndex");

		/**
		 * WARNING- this function must not be public.
		 * <p>
		 * Primitive function for extracting a value from a record at a given index.
		 * Note that the function terminates in an error if the record does not in fact have given field index.  
		 * 
		 * @param recordValue (CAL type: <code>Cal.Core.Prelude.Typeable r => {r}</code>)
		 *          the record from which to extract a type.
		 * @param fieldIndex (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the field index for a given field name in a record (as returned by <code>Cal.Core.Dynamic.recordFieldIndex</code> for the record).
		 * @return (CAL type: <code>Cal.Core.Prelude.CalValue</code>) 
		 *          an un-typed representation of the type of the field at the given index. Will be of type
		 * <code>Cal.Core.Prelude.TypeRep</code>.
		 */
		public static final SourceModel.Expr recordFieldTypePrimitive(SourceModel.Expr recordValue, SourceModel.Expr fieldIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.recordFieldTypePrimitive), recordValue, fieldIndex});
		}

		/**
		 * @see #recordFieldTypePrimitive(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param recordValue
		 * @param fieldIndex
		 * @return the SourceModel.Expr representing an application of recordFieldTypePrimitive
		 */
		public static final SourceModel.Expr recordFieldTypePrimitive(SourceModel.Expr recordValue, int fieldIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.recordFieldTypePrimitive), recordValue, SourceModel.Expr.makeIntValue(fieldIndex)});
		}

		/**
		 * Name binding for function: recordFieldTypePrimitive.
		 * @see #recordFieldTypePrimitive(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName recordFieldTypePrimitive = 
			QualifiedName.make(
				CAL_Dynamic_internal.MODULE_NAME, 
				"recordFieldTypePrimitive");

		/**
		 * Helper binding method for function: recordFieldValueExamples. 
		 * @return the SourceModule.expr representing an application of recordFieldValueExamples
		 */
		public static final SourceModel.Expr recordFieldValueExamples() {
			return SourceModel.Expr.Var.make(Functions.recordFieldValueExamples);
		}

		/**
		 * Name binding for function: recordFieldValueExamples.
		 * @see #recordFieldValueExamples()
		 */
		public static final QualifiedName recordFieldValueExamples = 
			QualifiedName.make(
				CAL_Dynamic_internal.MODULE_NAME, 
				"recordFieldValueExamples");

		/**
		 * WARNING- this function must not be public.
		 * <p>
		 * Primitive function for extracting a value from a record at a given index.
		 * Note that the function terminates in an error if the record does not in fact have given field index.  
		 * 
		 * @param recordValue (CAL type: <code>{r}</code>)
		 *          the record from which to extract a field.
		 * @param fieldIndex (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the field index for a given field name in a record (as returned by <code>Cal.Core.Dynamic.recordFieldIndex</code>
		 * for the record).
		 * @return (CAL type: <code>Cal.Core.Prelude.CalValue</code>) 
		 *          an un-typed representation of the value of the supplied field.
		 */
		public static final SourceModel.Expr recordFieldValuePrimitive(SourceModel.Expr recordValue, SourceModel.Expr fieldIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.recordFieldValuePrimitive), recordValue, fieldIndex});
		}

		/**
		 * @see #recordFieldValuePrimitive(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param recordValue
		 * @param fieldIndex
		 * @return the SourceModel.Expr representing an application of recordFieldValuePrimitive
		 */
		public static final SourceModel.Expr recordFieldValuePrimitive(SourceModel.Expr recordValue, int fieldIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.recordFieldValuePrimitive), recordValue, SourceModel.Expr.makeIntValue(fieldIndex)});
		}

		/**
		 * Name binding for function: recordFieldValuePrimitive.
		 * @see #recordFieldValuePrimitive(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName recordFieldValuePrimitive = 
			QualifiedName.make(
				CAL_Dynamic_internal.MODULE_NAME, 
				"recordFieldValuePrimitive");

		/**
		 * 
		 * @param recordType (CAL type: <code>Cal.Core.Prelude.RecordType</code>)
		 * @param arg_2 (CAL type: <code>Cal.Core.Prelude.RecordType</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.RecordType</code>) 
		 *          A new <code>Cal.Core.Prelude.RecordType</code> containing the union of the fields of the two argument <code>Cal.Core.Prelude.RecordType</code>s.
		 * If both <code>Cal.Core.Prelude.RecordType</code> arguments contain the same field, the first <code>Cal.Core.Prelude.RecordType</code> argument
		 * is given precedence.  (This is the same rule as for <code>Cal.Core.Dynamic.appendRecord</code>)
		 */
		public static final SourceModel.Expr recordType_appendRecordType(SourceModel.Expr recordType, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.recordType_appendRecordType), recordType, arg_2});
		}

		/**
		 * Name binding for function: recordType_appendRecordType.
		 * @see #recordType_appendRecordType(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName recordType_appendRecordType = 
			QualifiedName.make(
				CAL_Dynamic_internal.MODULE_NAME, 
				"recordType_appendRecordType");

		/**
		 * 
		 * @param recordType (CAL type: <code>Cal.Core.Prelude.RecordType</code>)
		 * @param arg_2 (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @param arg_3 (CAL type: <code>Cal.Core.Prelude.JObject</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.RecordType</code>) 
		 *          A new <code>Cal.Core.Prelude.RecordType</code> containing all the fields of the <code>Cal.Core.Prelude.RecordType</code> argument, plus a new
		 * field whose name and value are specified by the <code>Cal.Core.Prelude.String</code> and <code>Cal.Core.Prelude.JObject</code> arguments
		 * respectively.
		 * If the <code>Cal.Core.Prelude.RecordType</code> argument already contains the field specified by the string argument, the new
		 * <code>Cal.Core.Prelude.RecordType</code> will contain the value specified by the <code>Cal.Core.Prelude.JObject</code> argument rather than the value from
		 * the <code>Cal.Core.Prelude.RecordType</code> argument.
		 */
		public static final SourceModel.Expr recordType_insertRecordTypeField(SourceModel.Expr recordType, SourceModel.Expr arg_2, SourceModel.Expr arg_3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.recordType_insertRecordTypeField), recordType, arg_2, arg_3});
		}

		/**
		 * @see #recordType_insertRecordTypeField(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param recordType
		 * @param arg_2
		 * @param arg_3
		 * @return the SourceModel.Expr representing an application of recordType_insertRecordTypeField
		 */
		public static final SourceModel.Expr recordType_insertRecordTypeField(SourceModel.Expr recordType, java.lang.String arg_2, SourceModel.Expr arg_3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.recordType_insertRecordTypeField), recordType, SourceModel.Expr.makeStringValue(arg_2), arg_3});
		}

		/**
		 * Name binding for function: recordType_insertRecordTypeField.
		 * @see #recordType_insertRecordTypeField(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName recordType_insertRecordTypeField = 
			QualifiedName.make(
				CAL_Dynamic_internal.MODULE_NAME, 
				"recordType_insertRecordTypeField");

		/**
		 * Helper binding method for function: showDynamic. 
		 * @param dynamicValue
		 * @return the SourceModule.expr representing an application of showDynamic
		 */
		public static final SourceModel.Expr showDynamic(SourceModel.Expr dynamicValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showDynamic), dynamicValue});
		}

		/**
		 * Name binding for function: showDynamic.
		 * @see #showDynamic(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showDynamic = 
			QualifiedName.make(CAL_Dynamic_internal.MODULE_NAME, "showDynamic");

		/**
		 * Helper binding method for function: showExamples. 
		 * @return the SourceModule.expr representing an application of showExamples
		 */
		public static final SourceModel.Expr showExamples() {
			return SourceModel.Expr.Var.make(Functions.showExamples);
		}

		/**
		 * Name binding for function: showExamples.
		 * @see #showExamples()
		 */
		public static final QualifiedName showExamples = 
			QualifiedName.make(CAL_Dynamic_internal.MODULE_NAME, "showExamples");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = 532387249;

}
