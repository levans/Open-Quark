/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_Dynamic.java)
 * was generated from CAL module: Cal.Core.Dynamic.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Core.Dynamic module from Java code.
 *  
 * Creation date: Fri Mar 16 13:11:56 PST 2007
 * --!>
 *  
 */

package org.openquark.cal.module.Cal.Core;

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
public final class CAL_Dynamic {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Core.Dynamic");

	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Core.Dynamic module.
	 */
	public static final class TypeConstructors {
		/**
		 * The <code>Dynamic</code> type is a type used for holding values of a variety of CAL types.
		 * Dynamic values can be created using the toDynamic function and can be extracted 
		 * via the <code>Cal.Core.Dynamic.fromDynamicWithDefault</code> and <code>Cal.Core.Dynamic.fromDynamic</code> functions. The set of types that
		 * can be dynamically represented by the <code>Dynamic</code> type is extensible. Any type that
		 * is an instance of the <code>Cal.Core.Prelude.Typeable</code> type class can be used dynamically.
		 * <p>
		 * Note: using the <code>Dynamic</code> type is generally considered poor functional programming
		 * practice, and frequently there are ways to re-express a solution to avoid its use.
		 * However, <code>Dynamic</code> is actually type-safe in the sense that run-time errors cannot
		 * occur because of the use of the "wrong" type. The reason using <code>Dynamic</code> is frowned
		 * upon somewhat is that the type system is not able to help the user with the process
		 * of his or her construction of CAL code as much. There is also a (small) performance
		 * penalty of carrying type information at runtime.
		 */
		public static final QualifiedName Dynamic = 
			QualifiedName.make(CAL_Dynamic.MODULE_NAME, "Dynamic");

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Core.Dynamic module.
	 */
	public static final class Functions {
		/**
		 * Combine two records contained by <code>Cal.Core.Dynamic.Dynamic</code>s into a single record containing the fields of both.
		 * A runtime error is signalled if either <code>dynamicRecord1</code> or <code>dynamicRecord2</code> contain non-record values.
		 * @param dynamicRecord1 (CAL type: <code>Cal.Core.Dynamic.Dynamic</code>)
		 *          <code>Dynamic</code> containing a record
		 * @param dynamicRecord2 (CAL type: <code>Cal.Core.Dynamic.Dynamic</code>)
		 *          <code>Dynamic</code> containing a record
		 * @return (CAL type: <code>Cal.Core.Dynamic.Dynamic</code>) 
		 *          <code>Dynamic</code> containing a record that has all the fields of the records contained by
		 * dynamicRecord1 and dynamicRecord2.  The fields of <code>dynamicRecord1</code> are used for field
		 * names shared by both records.
		 */
		public static final SourceModel.Expr appendDynamicRecord(SourceModel.Expr dynamicRecord1, SourceModel.Expr dynamicRecord2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.appendDynamicRecord), dynamicRecord1, dynamicRecord2});
		}

		/**
		 * Name binding for function: appendDynamicRecord.
		 * @see #appendDynamicRecord(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName appendDynamicRecord = 
			QualifiedName.make(CAL_Dynamic.MODULE_NAME, "appendDynamicRecord");

		/**
		 * Combine two records into a single record containing the fields of both.
		 * @param record1 (CAL type: <code>Cal.Core.Prelude.Typeable r => {r}</code>)
		 *          First record to combine
		 * @param record2 (CAL type: <code>Cal.Core.Prelude.Typeable s => {s}</code>)
		 *          Second record to combine
		 * @return (CAL type: <code>Cal.Core.Dynamic.Dynamic</code>) 
		 *          <code>Dynamic</code> containing the record that has all the fields of <code>record1</code> and <code>record2</code>.
		 * The fields from <code>record1</code> will be used for field names that both records contain.
		 */
		public static final SourceModel.Expr appendRecord(SourceModel.Expr record1, SourceModel.Expr record2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.appendRecord), record1, record2});
		}

		/**
		 * Name binding for function: appendRecord.
		 * @see #appendRecord(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName appendRecord = 
			QualifiedName.make(CAL_Dynamic.MODULE_NAME, "appendRecord");

		/**
		 * Takes a function wrapped in a <code>Cal.Core.Dynamic.Dynamic</code> value and applies it to an argument
		 * wrapped in a <code>Cal.Core.Dynamic.Dynamic</code> value.  An error will be signalled if the types of the
		 * underlying values are not compatible.
		 * @param dynamicFunction (CAL type: <code>Cal.Core.Dynamic.Dynamic</code>)
		 *          <code>Dynamic</code> value containing the function to apply
		 * @param arg (CAL type: <code>Cal.Core.Dynamic.Dynamic</code>)
		 *          <code>Dynamic</code> value containing the value to apply <code>dynamicFunction</code> to
		 * @return (CAL type: <code>Cal.Core.Dynamic.Dynamic</code>) 
		 *          a <code>Dynamic</code> value wrapping the result of the application.
		 */
		public static final SourceModel.Expr dynamicApp(SourceModel.Expr dynamicFunction, SourceModel.Expr arg) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.dynamicApp), dynamicFunction, arg});
		}

		/**
		 * Name binding for function: dynamicApp.
		 * @see #dynamicApp(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName dynamicApp = 
			QualifiedName.make(CAL_Dynamic.MODULE_NAME, "dynamicApp");

		/**
		 * Takes a function wrapped in a <code>Cal.Core.Dynamic.Dynamic</code> value and attempts to apply it to an argument
		 * wrapped in a <code>Cal.Core.Dynamic.Dynamic</code> value.
		 * @param dynamicFunction (CAL type: <code>Cal.Core.Dynamic.Dynamic</code>)
		 *          <code>Dynamic</code> value containing the function to apply
		 * @param arg (CAL type: <code>Cal.Core.Dynamic.Dynamic</code>)
		 *          <code>Dynamic</code> value containing the value to apply dynamicFunction to
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Core.Dynamic.Dynamic</code>) 
		 *          <code>Cal.Core.Prelude.Maybe Cal.Core.Dynamic.Dynamic</code> which will be <code>Cal.Core.Prelude.Nothing</code> if the type of
		 * <code>dynamicFunction</code>'s wrapped value is not compatible with arg's wrapped value, or
		 * <code>Cal.Core.Prelude.Just dynamicResult</code>, where <code>dynamicResult</code> is a <code>Cal.Core.Dynamic.Dynamic</code>
		 * value wrapping the result of the application.
		 */
		public static final SourceModel.Expr dynamicApply(SourceModel.Expr dynamicFunction, SourceModel.Expr arg) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.dynamicApply), dynamicFunction, arg});
		}

		/**
		 * Name binding for function: dynamicApply.
		 * @see #dynamicApply(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName dynamicApply = 
			QualifiedName.make(CAL_Dynamic.MODULE_NAME, "dynamicApply");

		/**
		 * If <code>dynamicValue</code> is actually a record, then return <code>Cal.Core.Prelude.Just</code> the list of field names of the record.
		 * The values in the list are in field-name order of the orginal record.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Record.fieldNames
		 * </dl>
		 * 
		 * @param dynamicValue (CAL type: <code>Cal.Core.Dynamic.Dynamic</code>)
		 *          the <code>Dynamic</code> value.
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe [Cal.Core.Prelude.String]</code>) 
		 *          <code>Cal.Core.Prelude.Just</code> (the list of field names of the record), if <code>dynamicValue</code> is actually a record,
		 * or <code>Cal.Core.Prelude.Nothing</code> otherwise.
		 */
		public static final SourceModel.Expr dynamicRecordFieldNames(SourceModel.Expr dynamicValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.dynamicRecordFieldNames), dynamicValue});
		}

		/**
		 * Name binding for function: dynamicRecordFieldNames.
		 * @see #dynamicRecordFieldNames(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName dynamicRecordFieldNames = 
			QualifiedName.make(
				CAL_Dynamic.MODULE_NAME, 
				"dynamicRecordFieldNames");

		/**
		 * Accepts a <code>Cal.Core.Dynamic.Dynamic</code> value that contains a record and the name of a field,
		 * and returns a <code>Cal.Core.Dynamic.Dynamic</code> value that represents the value of the specified
		 * field of the record.  A runtime error is signalled if <code>dynamicRecord</code> does not
		 * contain a record, or if the record does not have the specified field.
		 * @param dynamicRecord (CAL type: <code>Cal.Core.Dynamic.Dynamic</code>)
		 *          value that contains a record
		 * @param fieldName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          Name of field to extract
		 * @return (CAL type: <code>Cal.Core.Dynamic.Dynamic</code>) 
		 *          <code>Dynamic</code> value of the value of the field specified by <code>fieldName</code> in the
		 * record contained by <code>dynamicRecord</code>.
		 */
		public static final SourceModel.Expr dynamicRecordFieldValue(SourceModel.Expr dynamicRecord, SourceModel.Expr fieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.dynamicRecordFieldValue), dynamicRecord, fieldName});
		}

		/**
		 * @see #dynamicRecordFieldValue(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param dynamicRecord
		 * @param fieldName
		 * @return the SourceModel.Expr representing an application of dynamicRecordFieldValue
		 */
		public static final SourceModel.Expr dynamicRecordFieldValue(SourceModel.Expr dynamicRecord, java.lang.String fieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.dynamicRecordFieldValue), dynamicRecord, SourceModel.Expr.makeStringValue(fieldName)});
		}

		/**
		 * Name binding for function: dynamicRecordFieldValue.
		 * @see #dynamicRecordFieldValue(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName dynamicRecordFieldValue = 
			QualifiedName.make(
				CAL_Dynamic.MODULE_NAME, 
				"dynamicRecordFieldValue");

		/**
		 * If <code>dynamicValue</code> is actually a record, then return <code>Cal.Core.Prelude.Just</code> the list of field values of the record, wrapped in
		 * <code>Cal.Core.Dynamic.Dynamic</code> values. Otherwise, return <code>Cal.Core.Prelude.Nothing</code>. The values in the list are in field-name order
		 * of the orginal record.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Dynamic.fieldValues
		 * </dl>
		 * 
		 * @param dynamicValue (CAL type: <code>Cal.Core.Dynamic.Dynamic</code>)
		 *          the <code>Dynamic</code> value.
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe [Cal.Core.Dynamic.Dynamic]</code>) 
		 *          <code>Cal.Core.Prelude.Just</code> (the list of field values of the record, wrapped in <code>Cal.Core.Dynamic.Dynamic</code> values), if
		 * <code>dynamicValue</code> is actually a record, or <code>Cal.Core.Prelude.Nothing</code> otherwise.
		 */
		public static final SourceModel.Expr dynamicRecordFieldValues(SourceModel.Expr dynamicValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.dynamicRecordFieldValues), dynamicValue});
		}

		/**
		 * Name binding for function: dynamicRecordFieldValues.
		 * @see #dynamicRecordFieldValues(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName dynamicRecordFieldValues = 
			QualifiedName.make(
				CAL_Dynamic.MODULE_NAME, 
				"dynamicRecordFieldValues");

		/**
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Dynamic.dynamicRecordFieldValue
		 * </dl>
		 * 
		 * @param dynamicValue (CAL type: <code>Cal.Core.Dynamic.Dynamic</code>)
		 *          the <code>Dynamic</code> value to test.
		 * @param fieldName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if <code>dynamicValue</code> is a dynamic record with the given field name and false otherwise.
		 */
		public static final SourceModel.Expr dynamicRecordHasField(SourceModel.Expr dynamicValue, SourceModel.Expr fieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.dynamicRecordHasField), dynamicValue, fieldName});
		}

		/**
		 * @see #dynamicRecordHasField(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param dynamicValue
		 * @param fieldName
		 * @return the SourceModel.Expr representing an application of dynamicRecordHasField
		 */
		public static final SourceModel.Expr dynamicRecordHasField(SourceModel.Expr dynamicValue, java.lang.String fieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.dynamicRecordHasField), dynamicValue, SourceModel.Expr.makeStringValue(fieldName)});
		}

		/**
		 * Name binding for function: dynamicRecordHasField.
		 * @see #dynamicRecordHasField(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName dynamicRecordHasField = 
			QualifiedName.make(CAL_Dynamic.MODULE_NAME, "dynamicRecordHasField");

		/**
		 * Returns the <code>Cal.Core.Prelude.TypeRep</code> value representing the type of the underlying value of a
		 * <code>Cal.Core.Dynamic.Dynamic</code> value.
		 * @param dynamicValue (CAL type: <code>Cal.Core.Dynamic.Dynamic</code>)
		 *          the <code>Dynamic</code> value.
		 * @return (CAL type: <code>Cal.Core.Prelude.TypeRep</code>) 
		 *          the <code>TypeRep</code> value representing the type of the underlying value of a <code>Cal.Core.Dynamic.Dynamic</code> value.
		 */
		public static final SourceModel.Expr dynamicUnderlyingType(SourceModel.Expr dynamicValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.dynamicUnderlyingType), dynamicValue});
		}

		/**
		 * Name binding for function: dynamicUnderlyingType.
		 * @see #dynamicUnderlyingType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName dynamicUnderlyingType = 
			QualifiedName.make(CAL_Dynamic.MODULE_NAME, "dynamicUnderlyingType");

		/**
		 * One of the ways to extract a value from a <code>Cal.Core.Dynamic.Dynamic</code> value.
		 * Extracts <code>Cal.Core.Prelude.Just underlyingValue</code> from the <code>Cal.Core.Dynamic.Dynamic</code> value if the type of the underlying
		 * value matches what is required of the context in which <code>fromDynamic</code> is used.
		 * Otherwise <code>Cal.Core.Prelude.Nothing</code> is returned.
		 * @param dynamicValue (CAL type: <code>Cal.Core.Dynamic.Dynamic</code>)
		 *          the <code>Dynamic</code> value whose underlying value is to be extracted.
		 * @return (CAL type: <code>Cal.Core.Prelude.Typeable a => Cal.Core.Prelude.Maybe a</code>) 
		 *          <code>Cal.Core.Prelude.Just v</code>, where <code>v</code> is the underlying value, if its type matches what is required of the context,
		 * or <code>Cal.Core.Prelude.Nothing</code> otherwise.
		 */
		public static final SourceModel.Expr fromDynamic(SourceModel.Expr dynamicValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromDynamic), dynamicValue});
		}

		/**
		 * Name binding for function: fromDynamic.
		 * @see #fromDynamic(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fromDynamic = 
			QualifiedName.make(CAL_Dynamic.MODULE_NAME, "fromDynamic");

		/**
		 * If <code>dynamicValue</code> is actually a list, then it return <code>Cal.Core.Prelude.Just</code> (the list, with elements wrapped in a Dynamic value)
		 * otherwise, the function returns <code>Cal.Core.Prelude.Nothing</code>.
		 * @param dynamicValue (CAL type: <code>Cal.Core.Dynamic.Dynamic</code>)
		 *          the <code>Dynamic</code> value.
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe [Cal.Core.Dynamic.Dynamic]</code>) 
		 *          <code>Cal.Core.Prelude.Just</code> (the list, with elements wrapped in a Dynamic value), if <code>dynamicValue</code> is actually a list,
		 * or <code>Cal.Core.Prelude.Nothing</code> otherwise.
		 */
		public static final SourceModel.Expr fromDynamicList(SourceModel.Expr dynamicValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromDynamicList), dynamicValue});
		}

		/**
		 * Name binding for function: fromDynamicList.
		 * @see #fromDynamicList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fromDynamicList = 
			QualifiedName.make(CAL_Dynamic.MODULE_NAME, "fromDynamicList");

		/**
		 * One of the ways to extract a value from a <code>Cal.Core.Dynamic.Dynamic</code> value.
		 * If the underlying type of the dynamic value is the same as that of
		 * the <code>defaultValue</code>, then the underlying value is returned. Otherwise
		 * <code>defaultValue</code> is returned.
		 * @param dynamicValue (CAL type: <code>Cal.Core.Dynamic.Dynamic</code>)
		 *          the <code>Dynamic</code> value whose underlying value is to be extracted.
		 * @param defaultValue (CAL type: <code>Cal.Core.Prelude.Typeable a => a</code>)
		 *          the default value to be returned if <code>dynamicValue</code>'s underlying ytpe
		 * is not the same as the type of this argument.
		 * @return (CAL type: <code>Cal.Core.Prelude.Typeable a => a</code>) 
		 *          the underlying value if its type matches that of <code>defaultValue</code>, or <code>defaultValue</code> otherwise.
		 */
		public static final SourceModel.Expr fromDynamicWithDefault(SourceModel.Expr dynamicValue, SourceModel.Expr defaultValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromDynamicWithDefault), dynamicValue, defaultValue});
		}

		/**
		 * Name binding for function: fromDynamicWithDefault.
		 * @see #fromDynamicWithDefault(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fromDynamicWithDefault = 
			QualifiedName.make(
				CAL_Dynamic.MODULE_NAME, 
				"fromDynamicWithDefault");

		/**
		 * Takes a <code>Cal.Core.Dynamic.Dynamic</code> value containing a record, a field name, and a dynamic value, and returns a
		 * <code>Cal.Core.Dynamic.Dynamic</code> value containing a new record that extends the old record with a field of the
		 * specified name having the specified value.  A runtime error is signalled if <code>dynamicRecordValue</code> does not
		 * contain a record.
		 * @param dynamicRecordValue (CAL type: <code>Cal.Core.Dynamic.Dynamic</code>)
		 *          <code>Dynamic</code> containing the record to extend
		 * @param fieldName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          The name of the field to add
		 * @param dynamicFieldValue (CAL type: <code>Cal.Core.Dynamic.Dynamic</code>)
		 *          Dynamic wrapper for the underlying field value to add
		 * @return (CAL type: <code>Cal.Core.Dynamic.Dynamic</code>) 
		 *          <code>Cal.Core.Dynamic.Dynamic</code> A <code>Cal.Core.Dynamic.Dynamic</code> containing an extention of
		 * <code>recordValue</code> that has the field named by <code>fieldName</code> with a value of <code>dynamicFieldValue</code>.
		 */
		public static final SourceModel.Expr insertDynamicRecordDynamicField(SourceModel.Expr dynamicRecordValue, SourceModel.Expr fieldName, SourceModel.Expr dynamicFieldValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.insertDynamicRecordDynamicField), dynamicRecordValue, fieldName, dynamicFieldValue});
		}

		/**
		 * @see #insertDynamicRecordDynamicField(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param dynamicRecordValue
		 * @param fieldName
		 * @param dynamicFieldValue
		 * @return the SourceModel.Expr representing an application of insertDynamicRecordDynamicField
		 */
		public static final SourceModel.Expr insertDynamicRecordDynamicField(SourceModel.Expr dynamicRecordValue, java.lang.String fieldName, SourceModel.Expr dynamicFieldValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.insertDynamicRecordDynamicField), dynamicRecordValue, SourceModel.Expr.makeStringValue(fieldName), dynamicFieldValue});
		}

		/**
		 * Name binding for function: insertDynamicRecordDynamicField.
		 * @see #insertDynamicRecordDynamicField(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName insertDynamicRecordDynamicField = 
			QualifiedName.make(
				CAL_Dynamic.MODULE_NAME, 
				"insertDynamicRecordDynamicField");

		/**
		 * Takes a <code>Cal.Core.Dynamic.Dynamic</code> value containing a record, a field name, and a value, and returns a
		 * <code>Cal.Core.Dynamic.Dynamic</code> value containing a new record that extends the old record with a field of the
		 * specified name having the specified value.  A runtime error is signalled if <code>dynamicRecordValue</code> does not
		 * contain a record.
		 * @param dynamicRecordValue (CAL type: <code>Cal.Core.Dynamic.Dynamic</code>)
		 *          <code>Dynamic</code> containing the record to extend
		 * @param fieldName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          The name of the field to add
		 * @param fieldValue (CAL type: <code>Cal.Core.Prelude.Typeable a => a</code>)
		 *          The value of the field to add
		 * @return (CAL type: <code>Cal.Core.Dynamic.Dynamic</code>) 
		 *          <code>Cal.Core.Dynamic.Dynamic</code> A <code>Cal.Core.Dynamic.Dynamic</code> containing an extention of
		 * <code>recordValue</code> that has the field named by <code>fieldName</code> with a value of <code>fieldValue</code>.
		 */
		public static final SourceModel.Expr insertDynamicRecordField(SourceModel.Expr dynamicRecordValue, SourceModel.Expr fieldName, SourceModel.Expr fieldValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.insertDynamicRecordField), dynamicRecordValue, fieldName, fieldValue});
		}

		/**
		 * @see #insertDynamicRecordField(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param dynamicRecordValue
		 * @param fieldName
		 * @param fieldValue
		 * @return the SourceModel.Expr representing an application of insertDynamicRecordField
		 */
		public static final SourceModel.Expr insertDynamicRecordField(SourceModel.Expr dynamicRecordValue, java.lang.String fieldName, SourceModel.Expr fieldValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.insertDynamicRecordField), dynamicRecordValue, SourceModel.Expr.makeStringValue(fieldName), fieldValue});
		}

		/**
		 * Name binding for function: insertDynamicRecordField.
		 * @see #insertDynamicRecordField(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName insertDynamicRecordField = 
			QualifiedName.make(
				CAL_Dynamic.MODULE_NAME, 
				"insertDynamicRecordField");

		/**
		 * Takes a record, a field name, and a value, and returns a <code>Cal.Core.Dynamic.Dynamic</code> value containing
		 * a new record that extends the old record with a field of the specified name having
		 * the specified value.
		 * @param recordValue (CAL type: <code>Cal.Core.Prelude.Typeable r => {r}</code>)
		 *          The record to extend
		 * @param fieldName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          The name of the field to add
		 * @param fieldValue (CAL type: <code>Cal.Core.Prelude.Typeable a => a</code>)
		 *          The value of the field to add
		 * @return (CAL type: <code>Cal.Core.Dynamic.Dynamic</code>) 
		 *          A <code>Cal.Core.Dynamic.Dynamic</code> containing an extention of <code>recordValue</code> that has the field named by
		 * <code>fieldName</code> with a value of <code>fieldValue</code>.
		 */
		public static final SourceModel.Expr insertRecordField(SourceModel.Expr recordValue, SourceModel.Expr fieldName, SourceModel.Expr fieldValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.insertRecordField), recordValue, fieldName, fieldValue});
		}

		/**
		 * @see #insertRecordField(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param recordValue
		 * @param fieldName
		 * @param fieldValue
		 * @return the SourceModel.Expr representing an application of insertRecordField
		 */
		public static final SourceModel.Expr insertRecordField(SourceModel.Expr recordValue, java.lang.String fieldName, SourceModel.Expr fieldValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.insertRecordField), recordValue, SourceModel.Expr.makeStringValue(fieldName), fieldValue});
		}

		/**
		 * Name binding for function: insertRecordField.
		 * @see #insertRecordField(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName insertRecordField = 
			QualifiedName.make(CAL_Dynamic.MODULE_NAME, "insertRecordField");

		/**
		 * Given a record value and a field name, returns a <code>Cal.Core.Dynamic.Dynamic</code> value representing the
		 * specified field in the record.  A runtime error is signalled if the record does
		 * not contain the specified field.
		 * @param recordValue (CAL type: <code>Cal.Core.Prelude.Typeable r => {r}</code>)
		 *          The record to extract a field from
		 * @param fieldName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          Name of the field to extract
		 * @return (CAL type: <code>Cal.Core.Dynamic.Dynamic</code>) 
		 *          <code>Dynamic</code> value containing the field's value
		 */
		public static final SourceModel.Expr recordFieldValue(SourceModel.Expr recordValue, SourceModel.Expr fieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.recordFieldValue), recordValue, fieldName});
		}

		/**
		 * @see #recordFieldValue(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param recordValue
		 * @param fieldName
		 * @return the SourceModel.Expr representing an application of recordFieldValue
		 */
		public static final SourceModel.Expr recordFieldValue(SourceModel.Expr recordValue, java.lang.String fieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.recordFieldValue), recordValue, SourceModel.Expr.makeStringValue(fieldName)});
		}

		/**
		 * Name binding for function: recordFieldValue.
		 * @see #recordFieldValue(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName recordFieldValue = 
			QualifiedName.make(CAL_Dynamic.MODULE_NAME, "recordFieldValue");

		/**
		 * 
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the testing predicates in this module all run successfully.
		 */
		public static final SourceModel.Expr testModule() {
			return SourceModel.Expr.Var.make(Functions.testModule);
		}

		/**
		 * Name binding for function: testModule.
		 * @see #testModule()
		 */
		public static final QualifiedName testModule = 
			QualifiedName.make(CAL_Dynamic.MODULE_NAME, "testModule");

		/**
		 * Creates a <code>Cal.Core.Dynamic.Dynamic</code> value.
		 * @param v (CAL type: <code>Cal.Core.Prelude.Typeable a => a</code>)
		 *          the value to be encapsulated in a <code>Cal.Core.Dynamic.Dynamic</code> value.
		 * @return (CAL type: <code>Cal.Core.Dynamic.Dynamic</code>) 
		 *          a <code>Cal.Core.Dynamic.Dynamic</code> value encapsulating <code>v</code>.
		 */
		public static final SourceModel.Expr toDynamic(SourceModel.Expr v) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toDynamic), v});
		}

		/**
		 * Name binding for function: toDynamic.
		 * @see #toDynamic(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName toDynamic = 
			QualifiedName.make(CAL_Dynamic.MODULE_NAME, "toDynamic");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = 1565997170;

}
