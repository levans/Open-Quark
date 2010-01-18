/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_Record_internal.java)
 * was generated from CAL module: Cal.Core.Record.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Core.Record module from Java code.
 *  
 * Creation date: Mon Oct 15 17:40:49 PDT 2007
 * --!>
 *  
 */

package org.openquark.cal.internal.module.Cal.Core;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * Defines many useful functions for working with CAL record types. Since
 * tuples are records, these functions are also useful for working with tuples.
 * @author Bo Ilic
 */
public final class CAL_Record_internal {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Core.Record");

	/**
	 * This inner class (DataConstructors) contains constants
	 * and methods related to binding to CAL DataConstructors in the Cal.Core.Record module.
	 */
	public static final class DataConstructors {
		/*
		 * DataConstructors for the Cal.Core.Record.Dictionary data type.
		 */

		/**
		 * Binding for DataConstructor: Cal.Core.Record.Dictionary.
		 * @param dictionary
		 * @param method
		 * @return the SourceModule.Expr representing an application of Cal.Core.Record.Dictionary
		 */
		public static final SourceModel.Expr Dictionary(SourceModel.Expr dictionary, SourceModel.Expr method) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.Dictionary), dictionary, method});
		}

		/**
		 * @see #Dictionary(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param dictionary
		 * @param method
		 * @return org.openquark.cal.compiler.SourceModel.Expr
		 */
		public static final SourceModel.Expr Dictionary(SourceModel.Expr dictionary, int method) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.Dictionary), dictionary, SourceModel.Expr.makeIntValue(method)});
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Record.Dictionary.
		 * @see #Dictionary(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName Dictionary = 
			QualifiedName.make(CAL_Record_internal.MODULE_NAME, "Dictionary");

		/**
		 * Ordinal of DataConstructor Cal.Core.Record.Dictionary.
		 * @see #Dictionary(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int Dictionary_ordinal = 0;

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Core.Record module.
	 */
	public static final class Functions {
		/**
		 * primitive function supporting record to list
		 * it expects a record dictionary, the index of the method to use in the dictonary
		 * and a tuple containing arguments for invoking the instance function
		 * @param dict (CAL type: <code>Cal.Core.Prelude.CalValue</code>)
		 *          internal record dictionary
		 * @param index (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the index of the instance function to use from the record dictionary
		 * @param args (CAL type: <code>{r}</code>)
		 *          a tuple containing values used to invoke the instance functions
		 * @return (CAL type: <code>Cal.Core.Prelude.JCollection</code>) 
		 *          a JList of unevaluated applications of the instance functions.
		 */
		public static final SourceModel.Expr buildListPrimitive(SourceModel.Expr dict, SourceModel.Expr index, SourceModel.Expr args) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.buildListPrimitive), dict, index, args});
		}

		/**
		 * @see #buildListPrimitive(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param dict
		 * @param index
		 * @param args
		 * @return the SourceModel.Expr representing an application of buildListPrimitive
		 */
		public static final SourceModel.Expr buildListPrimitive(SourceModel.Expr dict, int index, SourceModel.Expr args) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.buildListPrimitive), dict, SourceModel.Expr.makeIntValue(index), args});
		}

		/**
		 * Name binding for function: buildListPrimitive.
		 * @see #buildListPrimitive(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName buildListPrimitive = 
			QualifiedName.make(
				CAL_Record_internal.MODULE_NAME, 
				"buildListPrimitive");

		/**
		 * primitive function supporting list to record 
		 * it expects a record dictionary, the index of the method to use in the dictonary
		 * and a tuple containing arguments for invoking the instance function
		 * @param dict (CAL type: <code>Cal.Core.Prelude.CalValue</code>)
		 *          internal record dictionary
		 * @param index (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the index of the instance function to use from the record dictionary
		 * @param args (CAL type: <code>{a}</code>)
		 *          a tuple containing values used to invoke the instance functions
		 * @return (CAL type: <code>{b}</code>) 
		 *          a record
		 */
		public static final SourceModel.Expr buildRecordPrimitive(SourceModel.Expr dict, SourceModel.Expr index, SourceModel.Expr args) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.buildRecordPrimitive), dict, index, args});
		}

		/**
		 * @see #buildRecordPrimitive(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param dict
		 * @param index
		 * @param args
		 * @return the SourceModel.Expr representing an application of buildRecordPrimitive
		 */
		public static final SourceModel.Expr buildRecordPrimitive(SourceModel.Expr dict, int index, SourceModel.Expr args) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.buildRecordPrimitive), dict, SourceModel.Expr.makeIntValue(index), args});
		}

		/**
		 * Name binding for function: buildRecordPrimitive.
		 * @see #buildRecordPrimitive(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName buildRecordPrimitive = 
			QualifiedName.make(
				CAL_Record_internal.MODULE_NAME, 
				"buildRecordPrimitive");

		/**
		 * <code>fieldNamesPrimitive</code> takes a record, and returns a <code>Cal.Core.Prelude.JList</code> that is actually a 
		 * <code>java.util.List</code> of <code>java.lang.String</code> objects representing the field names of the record.
		 * @param arg_1 (CAL type: <code>{r}</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.JList</code>) 
		 */
		public static final SourceModel.Expr fieldNamesPrimitive(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fieldNamesPrimitive), arg_1});
		}

		/**
		 * Name binding for function: fieldNamesPrimitive.
		 * @see #fieldNamesPrimitive(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fieldNamesPrimitive = 
			QualifiedName.make(
				CAL_Record_internal.MODULE_NAME, 
				"fieldNamesPrimitive");

		/**
		 * Primitive function for determining if a record has a field of the given name.
		 * this could be implemented as a non-primitive function based on <code>Cal.Core.Record.fieldNames</code>, but is done in
		 * this way for efficiency reasons.
		 * @param recordValue (CAL type: <code>{r}</code>)
		 *          the record to be checked for the presense of a field of the given name.
		 * @param fieldName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the field name to be checked.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the named field is present in <code>recordValue</code>; <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr hasFieldPrimitive(SourceModel.Expr recordValue, SourceModel.Expr fieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.hasFieldPrimitive), recordValue, fieldName});
		}

		/**
		 * @see #hasFieldPrimitive(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param recordValue
		 * @param fieldName
		 * @return the SourceModel.Expr representing an application of hasFieldPrimitive
		 */
		public static final SourceModel.Expr hasFieldPrimitive(SourceModel.Expr recordValue, java.lang.String fieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.hasFieldPrimitive), recordValue, SourceModel.Expr.makeStringValue(fieldName)});
		}

		/**
		 * Name binding for function: hasFieldPrimitive.
		 * @see #hasFieldPrimitive(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName hasFieldPrimitive = 
			QualifiedName.make(
				CAL_Record_internal.MODULE_NAME, 
				"hasFieldPrimitive");

		/**
		 * Helper binding method for function: recordToJRecordValuePrimitive. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of recordToJRecordValuePrimitive
		 */
		public static final SourceModel.Expr recordToJRecordValuePrimitive(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.recordToJRecordValuePrimitive), arg_1});
		}

		/**
		 * Name binding for function: recordToJRecordValuePrimitive.
		 * @see #recordToJRecordValuePrimitive(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName recordToJRecordValuePrimitive = 
			QualifiedName.make(
				CAL_Record_internal.MODULE_NAME, 
				"recordToJRecordValuePrimitive");

		/**
		 * 
		 * @param record (CAL type: <code>{r}</code>)
		 * @return (CAL type: <code>{r}</code>) 
		 *          record itself, except that each field of the record has been evaluated to weak-head normal form
		 * in field-name order.
		 */
		public static final SourceModel.Expr strictRecordPrimitive(SourceModel.Expr record) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.strictRecordPrimitive), record});
		}

		/**
		 * Name binding for function: strictRecordPrimitive.
		 * @see #strictRecordPrimitive(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName strictRecordPrimitive = 
			QualifiedName.make(
				CAL_Record_internal.MODULE_NAME, 
				"strictRecordPrimitive");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = -1981415755;

}
