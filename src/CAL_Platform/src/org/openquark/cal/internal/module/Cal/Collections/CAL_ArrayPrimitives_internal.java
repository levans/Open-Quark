/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_ArrayPrimitives_internal.java)
 * was generated from CAL module: Cal.Collections.ArrayPrimitives.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Collections.ArrayPrimitives module from Java code.
 *  
 * Creation date: Wed Mar 28 13:07:55 PST 2007
 * --!>
 *  
 */

package org.openquark.cal.internal.module.Cal.Collections;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * This module is a helper module for the Array module, defining the necessary functions and
 * types to support the various Java primitive arrays. It does not export a public api- its
 * purpose is only to make the Array module itself more readable by encapsulating a self
 * contained unit.
 * @author Bo Ilic
 */
public final class CAL_ArrayPrimitives_internal {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Collections.ArrayPrimitives");

	/**
	 * This inner class (TypeClasses) contains constants
	 * and methods related to binding to CAL TypeClasses in the Cal.Collections.ArrayPrimitives module.
	 */
	public static final class TypeClasses {
		/**
		 * Contains a variety of useful methods for working with primitive arrays. This would be more useful if
		 * multi-parameter type classes were supported. For example, then array_subscript could be a method (which in can't be
		 * currently since the container type, such as <code>Cal.Collections.ArrayPrimitives.JCharArray</code>, is not directly related to the element type
		 * <code>Cal.Core.Prelude.Char</code>).
		 */
		public static final QualifiedName Array = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"Array");

	}
	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Collections.ArrayPrimitives module.
	 */
	public static final class TypeConstructors {
		/** Name binding for TypeConsApp: JBooleanArray. */
		public static final QualifiedName JBooleanArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"JBooleanArray");

		/** Name binding for TypeConsApp: JByteArray. */
		public static final QualifiedName JByteArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"JByteArray");

		/** Name binding for TypeConsApp: JCalValueArray. */
		public static final QualifiedName JCalValueArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"JCalValueArray");

		/** Name binding for TypeConsApp: JCharArray. */
		public static final QualifiedName JCharArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"JCharArray");

		/** Name binding for TypeConsApp: JDoubleArray. */
		public static final QualifiedName JDoubleArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"JDoubleArray");

		/** Name binding for TypeConsApp: JFloatArray. */
		public static final QualifiedName JFloatArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"JFloatArray");

		/** Name binding for TypeConsApp: JIntArray. */
		public static final QualifiedName JIntArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"JIntArray");

		/** Name binding for TypeConsApp: JLongArray. */
		public static final QualifiedName JLongArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"JLongArray");

		/** Name binding for TypeConsApp: JObjectArray. */
		public static final QualifiedName JObjectArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"JObjectArray");

		/** Name binding for TypeConsApp: JShortArray. */
		public static final QualifiedName JShortArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"JShortArray");

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Collections.ArrayPrimitives module.
	 */
	public static final class Functions {
		/**
		 * Helper binding method for function: array_arrayToList. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of array_arrayToList
		 */
		public static final SourceModel.Expr array_arrayToList(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.array_arrayToList), arg_1});
		}

		/**
		 * Name binding for function: array_arrayToList.
		 * @see #array_arrayToList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName array_arrayToList = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"array_arrayToList");

		/**
		 * Helper binding method for function: array_cloneReplacingNullArray. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of array_cloneReplacingNullArray
		 */
		public static final SourceModel.Expr array_cloneReplacingNullArray(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.array_cloneReplacingNullArray), arg_1});
		}

		/**
		 * Name binding for function: array_cloneReplacingNullArray.
		 * @see #array_cloneReplacingNullArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName array_cloneReplacingNullArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"array_cloneReplacingNullArray");

		/**
		 * Helper binding method for function: array_concatList. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of array_concatList
		 */
		public static final SourceModel.Expr array_concatList(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.array_concatList), arg_1});
		}

		/**
		 * Name binding for function: array_concatList.
		 * @see #array_concatList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName array_concatList = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"array_concatList");

		/**
		 * Helper binding method for function: array_fromCalValueArray. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of array_fromCalValueArray
		 */
		public static final SourceModel.Expr array_fromCalValueArray(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.array_fromCalValueArray), arg_1});
		}

		/**
		 * Name binding for function: array_fromCalValueArray.
		 * @see #array_fromCalValueArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName array_fromCalValueArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"array_fromCalValueArray");

		/**
		 * Helper binding method for function: array_length. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of array_length
		 */
		public static final SourceModel.Expr array_length(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.array_length), arg_1});
		}

		/**
		 * Name binding for function: array_length.
		 * @see #array_length(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName array_length = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"array_length");

		/**
		 * Helper binding method for function: array_listToArray. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of array_listToArray
		 */
		public static final SourceModel.Expr array_listToArray(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.array_listToArray), arg_1});
		}

		/**
		 * Name binding for function: array_listToArray.
		 * @see #array_listToArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName array_listToArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"array_listToArray");

		/**
		 * Helper binding method for function: array_makeDefault. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of array_makeDefault
		 */
		public static final SourceModel.Expr array_makeDefault(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.array_makeDefault), arg_1});
		}

		/**
		 * @see #array_makeDefault(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of array_makeDefault
		 */
		public static final SourceModel.Expr array_makeDefault(int arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.array_makeDefault), SourceModel.Expr.makeIntValue(arg_1)});
		}

		/**
		 * Name binding for function: array_makeDefault.
		 * @see #array_makeDefault(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName array_makeDefault = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"array_makeDefault");

		/**
		 * Helper binding method for function: array_removeRange. 
		 * @param arg_1
		 * @param arg_2
		 * @param arg_3
		 * @return the SourceModule.expr representing an application of array_removeRange
		 */
		public static final SourceModel.Expr array_removeRange(SourceModel.Expr arg_1, SourceModel.Expr arg_2, SourceModel.Expr arg_3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.array_removeRange), arg_1, arg_2, arg_3});
		}

		/**
		 * @see #array_removeRange(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @param arg_3
		 * @return the SourceModel.Expr representing an application of array_removeRange
		 */
		public static final SourceModel.Expr array_removeRange(SourceModel.Expr arg_1, int arg_2, int arg_3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.array_removeRange), arg_1, SourceModel.Expr.makeIntValue(arg_2), SourceModel.Expr.makeIntValue(arg_3)});
		}

		/**
		 * Name binding for function: array_removeRange.
		 * @see #array_removeRange(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName array_removeRange = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"array_removeRange");

		/**
		 * Helper binding method for function: array_reverse. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of array_reverse
		 */
		public static final SourceModel.Expr array_reverse(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.array_reverse), arg_1});
		}

		/**
		 * Name binding for function: array_reverse.
		 * @see #array_reverse(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName array_reverse = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"array_reverse");

		/**
		 * Helper binding method for function: array_subArray. 
		 * @param arg_1
		 * @param arg_2
		 * @param arg_3
		 * @return the SourceModule.expr representing an application of array_subArray
		 */
		public static final SourceModel.Expr array_subArray(SourceModel.Expr arg_1, SourceModel.Expr arg_2, SourceModel.Expr arg_3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.array_subArray), arg_1, arg_2, arg_3});
		}

		/**
		 * @see #array_subArray(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @param arg_3
		 * @return the SourceModel.Expr representing an application of array_subArray
		 */
		public static final SourceModel.Expr array_subArray(SourceModel.Expr arg_1, int arg_2, int arg_3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.array_subArray), arg_1, SourceModel.Expr.makeIntValue(arg_2), SourceModel.Expr.makeIntValue(arg_3)});
		}

		/**
		 * Name binding for function: array_subArray.
		 * @see #array_subArray(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName array_subArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"array_subArray");

		/**
		 * Helper binding method for function: array_toCalValueArray. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of array_toCalValueArray
		 */
		public static final SourceModel.Expr array_toCalValueArray(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.array_toCalValueArray), arg_1});
		}

		/**
		 * Name binding for function: array_toCalValueArray.
		 * @see #array_toCalValueArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName array_toCalValueArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"array_toCalValueArray");

		/**
		 * Helper binding method for function: booleanArray_append. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of booleanArray_append
		 */
		public static final SourceModel.Expr booleanArray_append(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_append), array1, array2});
		}

		/**
		 * Name binding for function: booleanArray_append.
		 * @see #booleanArray_append(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName booleanArray_append = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"booleanArray_append");

		/**
		 * Helper binding method for function: booleanArray_array1. 
		 * @param value
		 * @return the SourceModule.expr representing an application of booleanArray_array1
		 */
		public static final SourceModel.Expr booleanArray_array1(SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_array1), value});
		}

		/**
		 * @see #booleanArray_array1(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value
		 * @return the SourceModel.Expr representing an application of booleanArray_array1
		 */
		public static final SourceModel.Expr booleanArray_array1(boolean value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_array1), SourceModel.Expr.makeBooleanValue(value)});
		}

		/**
		 * Name binding for function: booleanArray_array1.
		 * @see #booleanArray_array1(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName booleanArray_array1 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"booleanArray_array1");

		/**
		 * Helper binding method for function: booleanArray_array2. 
		 * @param value1
		 * @param value2
		 * @return the SourceModule.expr representing an application of booleanArray_array2
		 */
		public static final SourceModel.Expr booleanArray_array2(SourceModel.Expr value1, SourceModel.Expr value2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_array2), value1, value2});
		}

		/**
		 * @see #booleanArray_array2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value1
		 * @param value2
		 * @return the SourceModel.Expr representing an application of booleanArray_array2
		 */
		public static final SourceModel.Expr booleanArray_array2(boolean value1, boolean value2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_array2), SourceModel.Expr.makeBooleanValue(value1), SourceModel.Expr.makeBooleanValue(value2)});
		}

		/**
		 * Name binding for function: booleanArray_array2.
		 * @see #booleanArray_array2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName booleanArray_array2 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"booleanArray_array2");

		/**
		 * Helper binding method for function: booleanArray_array3. 
		 * @param value1
		 * @param value2
		 * @param value3
		 * @return the SourceModule.expr representing an application of booleanArray_array3
		 */
		public static final SourceModel.Expr booleanArray_array3(SourceModel.Expr value1, SourceModel.Expr value2, SourceModel.Expr value3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_array3), value1, value2, value3});
		}

		/**
		 * @see #booleanArray_array3(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value1
		 * @param value2
		 * @param value3
		 * @return the SourceModel.Expr representing an application of booleanArray_array3
		 */
		public static final SourceModel.Expr booleanArray_array3(boolean value1, boolean value2, boolean value3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_array3), SourceModel.Expr.makeBooleanValue(value1), SourceModel.Expr.makeBooleanValue(value2), SourceModel.Expr.makeBooleanValue(value3)});
		}

		/**
		 * Name binding for function: booleanArray_array3.
		 * @see #booleanArray_array3(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName booleanArray_array3 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"booleanArray_array3");

		/**
		 * Helper binding method for function: booleanArray_array4. 
		 * @param value1
		 * @param value2
		 * @param value3
		 * @param value4
		 * @return the SourceModule.expr representing an application of booleanArray_array4
		 */
		public static final SourceModel.Expr booleanArray_array4(SourceModel.Expr value1, SourceModel.Expr value2, SourceModel.Expr value3, SourceModel.Expr value4) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_array4), value1, value2, value3, value4});
		}

		/**
		 * @see #booleanArray_array4(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value1
		 * @param value2
		 * @param value3
		 * @param value4
		 * @return the SourceModel.Expr representing an application of booleanArray_array4
		 */
		public static final SourceModel.Expr booleanArray_array4(boolean value1, boolean value2, boolean value3, boolean value4) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_array4), SourceModel.Expr.makeBooleanValue(value1), SourceModel.Expr.makeBooleanValue(value2), SourceModel.Expr.makeBooleanValue(value3), SourceModel.Expr.makeBooleanValue(value4)});
		}

		/**
		 * Name binding for function: booleanArray_array4.
		 * @see #booleanArray_array4(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName booleanArray_array4 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"booleanArray_array4");

		/**
		 * Helper binding method for function: booleanArray_array5. 
		 * @param value1
		 * @param value2
		 * @param value3
		 * @param value4
		 * @param value5
		 * @return the SourceModule.expr representing an application of booleanArray_array5
		 */
		public static final SourceModel.Expr booleanArray_array5(SourceModel.Expr value1, SourceModel.Expr value2, SourceModel.Expr value3, SourceModel.Expr value4, SourceModel.Expr value5) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_array5), value1, value2, value3, value4, value5});
		}

		/**
		 * @see #booleanArray_array5(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value1
		 * @param value2
		 * @param value3
		 * @param value4
		 * @param value5
		 * @return the SourceModel.Expr representing an application of booleanArray_array5
		 */
		public static final SourceModel.Expr booleanArray_array5(boolean value1, boolean value2, boolean value3, boolean value4, boolean value5) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_array5), SourceModel.Expr.makeBooleanValue(value1), SourceModel.Expr.makeBooleanValue(value2), SourceModel.Expr.makeBooleanValue(value3), SourceModel.Expr.makeBooleanValue(value4), SourceModel.Expr.makeBooleanValue(value5)});
		}

		/**
		 * Name binding for function: booleanArray_array5.
		 * @see #booleanArray_array5(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName booleanArray_array5 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"booleanArray_array5");

		/**
		 * Helper binding method for function: booleanArray_array6. 
		 * @param value1
		 * @param value2
		 * @param value3
		 * @param value4
		 * @param value5
		 * @param value6
		 * @return the SourceModule.expr representing an application of booleanArray_array6
		 */
		public static final SourceModel.Expr booleanArray_array6(SourceModel.Expr value1, SourceModel.Expr value2, SourceModel.Expr value3, SourceModel.Expr value4, SourceModel.Expr value5, SourceModel.Expr value6) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_array6), value1, value2, value3, value4, value5, value6});
		}

		/**
		 * @see #booleanArray_array6(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value1
		 * @param value2
		 * @param value3
		 * @param value4
		 * @param value5
		 * @param value6
		 * @return the SourceModel.Expr representing an application of booleanArray_array6
		 */
		public static final SourceModel.Expr booleanArray_array6(boolean value1, boolean value2, boolean value3, boolean value4, boolean value5, boolean value6) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_array6), SourceModel.Expr.makeBooleanValue(value1), SourceModel.Expr.makeBooleanValue(value2), SourceModel.Expr.makeBooleanValue(value3), SourceModel.Expr.makeBooleanValue(value4), SourceModel.Expr.makeBooleanValue(value5), SourceModel.Expr.makeBooleanValue(value6)});
		}

		/**
		 * Name binding for function: booleanArray_array6.
		 * @see #booleanArray_array6(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName booleanArray_array6 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"booleanArray_array6");

		/**
		 * Helper binding method for function: booleanArray_array7. 
		 * @param value1
		 * @param value2
		 * @param value3
		 * @param value4
		 * @param value5
		 * @param value6
		 * @param value7
		 * @return the SourceModule.expr representing an application of booleanArray_array7
		 */
		public static final SourceModel.Expr booleanArray_array7(SourceModel.Expr value1, SourceModel.Expr value2, SourceModel.Expr value3, SourceModel.Expr value4, SourceModel.Expr value5, SourceModel.Expr value6, SourceModel.Expr value7) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_array7), value1, value2, value3, value4, value5, value6, value7});
		}

		/**
		 * @see #booleanArray_array7(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value1
		 * @param value2
		 * @param value3
		 * @param value4
		 * @param value5
		 * @param value6
		 * @param value7
		 * @return the SourceModel.Expr representing an application of booleanArray_array7
		 */
		public static final SourceModel.Expr booleanArray_array7(boolean value1, boolean value2, boolean value3, boolean value4, boolean value5, boolean value6, boolean value7) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_array7), SourceModel.Expr.makeBooleanValue(value1), SourceModel.Expr.makeBooleanValue(value2), SourceModel.Expr.makeBooleanValue(value3), SourceModel.Expr.makeBooleanValue(value4), SourceModel.Expr.makeBooleanValue(value5), SourceModel.Expr.makeBooleanValue(value6), SourceModel.Expr.makeBooleanValue(value7)});
		}

		/**
		 * Name binding for function: booleanArray_array7.
		 * @see #booleanArray_array7(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName booleanArray_array7 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"booleanArray_array7");

		/**
		 * Helper binding method for function: booleanArray_arrayToList. 
		 * @param array
		 * @return the SourceModule.expr representing an application of booleanArray_arrayToList
		 */
		public static final SourceModel.Expr booleanArray_arrayToList(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_arrayToList), array});
		}

		/**
		 * Name binding for function: booleanArray_arrayToList.
		 * @see #booleanArray_arrayToList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName booleanArray_arrayToList = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"booleanArray_arrayToList");

		/**
		 * Helper binding method for function: booleanArray_cloneReplacingNullArray. 
		 * @param array
		 * @return the SourceModule.expr representing an application of booleanArray_cloneReplacingNullArray
		 */
		public static final SourceModel.Expr booleanArray_cloneReplacingNullArray(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_cloneReplacingNullArray), array});
		}

		/**
		 * Name binding for function: booleanArray_cloneReplacingNullArray.
		 * @see #booleanArray_cloneReplacingNullArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName booleanArray_cloneReplacingNullArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"booleanArray_cloneReplacingNullArray");

		/**
		 * Helper binding method for function: booleanArray_compare. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of booleanArray_compare
		 */
		public static final SourceModel.Expr booleanArray_compare(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_compare), array1, array2});
		}

		/**
		 * Name binding for function: booleanArray_compare.
		 * @see #booleanArray_compare(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName booleanArray_compare = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"booleanArray_compare");

		/**
		 * Helper binding method for function: booleanArray_compareHelper. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of booleanArray_compareHelper
		 */
		public static final SourceModel.Expr booleanArray_compareHelper(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_compareHelper), array1, array2});
		}

		/**
		 * Name binding for function: booleanArray_compareHelper.
		 * @see #booleanArray_compareHelper(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName booleanArray_compareHelper = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"booleanArray_compareHelper");

		/**
		 * Helper binding method for function: booleanArray_concat. 
		 * @param list
		 * @return the SourceModule.expr representing an application of booleanArray_concat
		 */
		public static final SourceModel.Expr booleanArray_concat(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_concat), list});
		}

		/**
		 * Name binding for function: booleanArray_concat.
		 * @see #booleanArray_concat(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName booleanArray_concat = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"booleanArray_concat");

		/**
		 * Helper binding method for function: booleanArray_concatList. 
		 * @param list
		 * @return the SourceModule.expr representing an application of booleanArray_concatList
		 */
		public static final SourceModel.Expr booleanArray_concatList(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_concatList), list});
		}

		/**
		 * Name binding for function: booleanArray_concatList.
		 * @see #booleanArray_concatList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName booleanArray_concatList = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"booleanArray_concatList");

		/**
		 * Helper binding method for function: booleanArray_empty. 
		 * @return the SourceModule.expr representing an application of booleanArray_empty
		 */
		public static final SourceModel.Expr booleanArray_empty() {
			return SourceModel.Expr.Var.make(Functions.booleanArray_empty);
		}

		/**
		 * Name binding for function: booleanArray_empty.
		 * @see #booleanArray_empty()
		 */
		public static final QualifiedName booleanArray_empty = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"booleanArray_empty");

		/**
		 * Helper binding method for function: booleanArray_equals. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of booleanArray_equals
		 */
		public static final SourceModel.Expr booleanArray_equals(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_equals), array1, array2});
		}

		/**
		 * Name binding for function: booleanArray_equals.
		 * @see #booleanArray_equals(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName booleanArray_equals = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"booleanArray_equals");

		/**
		 * Converts a Java array of CAL <code>Cal.Core.Prelude.Boolean</code>s to a Java array of primitive Java booleans.
		 * @param calValueArray (CAL type: <code>Cal.Collections.ArrayPrimitives.JCalValueArray</code>)
		 * @return (CAL type: <code>Cal.Collections.ArrayPrimitives.JBooleanArray</code>) 
		 */
		public static final SourceModel.Expr booleanArray_fromCalValueArray(SourceModel.Expr calValueArray) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_fromCalValueArray), calValueArray});
		}

		/**
		 * Name binding for function: booleanArray_fromCalValueArray.
		 * @see #booleanArray_fromCalValueArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName booleanArray_fromCalValueArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"booleanArray_fromCalValueArray");

		/**
		 * Helper binding method for function: booleanArray_greaterThan. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of booleanArray_greaterThan
		 */
		public static final SourceModel.Expr booleanArray_greaterThan(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_greaterThan), array1, array2});
		}

		/**
		 * Name binding for function: booleanArray_greaterThan.
		 * @see #booleanArray_greaterThan(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName booleanArray_greaterThan = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"booleanArray_greaterThan");

		/**
		 * Helper binding method for function: booleanArray_greaterThanEquals. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of booleanArray_greaterThanEquals
		 */
		public static final SourceModel.Expr booleanArray_greaterThanEquals(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_greaterThanEquals), array1, array2});
		}

		/**
		 * Name binding for function: booleanArray_greaterThanEquals.
		 * @see #booleanArray_greaterThanEquals(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName booleanArray_greaterThanEquals = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"booleanArray_greaterThanEquals");

		/**
		 * Helper binding method for function: booleanArray_indexOf. 
		 * @param array
		 * @param element
		 * @return the SourceModule.expr representing an application of booleanArray_indexOf
		 */
		public static final SourceModel.Expr booleanArray_indexOf(SourceModel.Expr array, SourceModel.Expr element) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_indexOf), array, element});
		}

		/**
		 * @see #booleanArray_indexOf(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param element
		 * @return the SourceModel.Expr representing an application of booleanArray_indexOf
		 */
		public static final SourceModel.Expr booleanArray_indexOf(SourceModel.Expr array, boolean element) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_indexOf), array, SourceModel.Expr.makeBooleanValue(element)});
		}

		/**
		 * Name binding for function: booleanArray_indexOf.
		 * @see #booleanArray_indexOf(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName booleanArray_indexOf = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"booleanArray_indexOf");

		/**
		 * Helper binding method for function: booleanArray_indexOf2. 
		 * @param array
		 * @param element
		 * @param fromIndex
		 * @return the SourceModule.expr representing an application of booleanArray_indexOf2
		 */
		public static final SourceModel.Expr booleanArray_indexOf2(SourceModel.Expr array, SourceModel.Expr element, SourceModel.Expr fromIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_indexOf2), array, element, fromIndex});
		}

		/**
		 * @see #booleanArray_indexOf2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param element
		 * @param fromIndex
		 * @return the SourceModel.Expr representing an application of booleanArray_indexOf2
		 */
		public static final SourceModel.Expr booleanArray_indexOf2(SourceModel.Expr array, boolean element, int fromIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_indexOf2), array, SourceModel.Expr.makeBooleanValue(element), SourceModel.Expr.makeIntValue(fromIndex)});
		}

		/**
		 * Name binding for function: booleanArray_indexOf2.
		 * @see #booleanArray_indexOf2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName booleanArray_indexOf2 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"booleanArray_indexOf2");

		/**
		 * Helper binding method for function: booleanArray_isEmpty. 
		 * @param array
		 * @return the SourceModule.expr representing an application of booleanArray_isEmpty
		 */
		public static final SourceModel.Expr booleanArray_isEmpty(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_isEmpty), array});
		}

		/**
		 * Name binding for function: booleanArray_isEmpty.
		 * @see #booleanArray_isEmpty(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName booleanArray_isEmpty = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"booleanArray_isEmpty");

		/**
		 * Helper binding method for function: booleanArray_lastIndexOf. 
		 * @param array
		 * @param element
		 * @return the SourceModule.expr representing an application of booleanArray_lastIndexOf
		 */
		public static final SourceModel.Expr booleanArray_lastIndexOf(SourceModel.Expr array, SourceModel.Expr element) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_lastIndexOf), array, element});
		}

		/**
		 * @see #booleanArray_lastIndexOf(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param element
		 * @return the SourceModel.Expr representing an application of booleanArray_lastIndexOf
		 */
		public static final SourceModel.Expr booleanArray_lastIndexOf(SourceModel.Expr array, boolean element) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_lastIndexOf), array, SourceModel.Expr.makeBooleanValue(element)});
		}

		/**
		 * Name binding for function: booleanArray_lastIndexOf.
		 * @see #booleanArray_lastIndexOf(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName booleanArray_lastIndexOf = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"booleanArray_lastIndexOf");

		/**
		 * Helper binding method for function: booleanArray_lastIndexOf2. 
		 * @param array
		 * @param element
		 * @param fromIndex
		 * @return the SourceModule.expr representing an application of booleanArray_lastIndexOf2
		 */
		public static final SourceModel.Expr booleanArray_lastIndexOf2(SourceModel.Expr array, SourceModel.Expr element, SourceModel.Expr fromIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_lastIndexOf2), array, element, fromIndex});
		}

		/**
		 * @see #booleanArray_lastIndexOf2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param element
		 * @param fromIndex
		 * @return the SourceModel.Expr representing an application of booleanArray_lastIndexOf2
		 */
		public static final SourceModel.Expr booleanArray_lastIndexOf2(SourceModel.Expr array, boolean element, int fromIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_lastIndexOf2), array, SourceModel.Expr.makeBooleanValue(element), SourceModel.Expr.makeIntValue(fromIndex)});
		}

		/**
		 * Name binding for function: booleanArray_lastIndexOf2.
		 * @see #booleanArray_lastIndexOf2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName booleanArray_lastIndexOf2 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"booleanArray_lastIndexOf2");

		/**
		 * Helper binding method for function: booleanArray_length. 
		 * @param array
		 * @return the SourceModule.expr representing an application of booleanArray_length
		 */
		public static final SourceModel.Expr booleanArray_length(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_length), array});
		}

		/**
		 * Name binding for function: booleanArray_length.
		 * @see #booleanArray_length(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName booleanArray_length = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"booleanArray_length");

		/**
		 * Helper binding method for function: booleanArray_lessThan. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of booleanArray_lessThan
		 */
		public static final SourceModel.Expr booleanArray_lessThan(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_lessThan), array1, array2});
		}

		/**
		 * Name binding for function: booleanArray_lessThan.
		 * @see #booleanArray_lessThan(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName booleanArray_lessThan = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"booleanArray_lessThan");

		/**
		 * Helper binding method for function: booleanArray_lessThanEquals. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of booleanArray_lessThanEquals
		 */
		public static final SourceModel.Expr booleanArray_lessThanEquals(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_lessThanEquals), array1, array2});
		}

		/**
		 * Name binding for function: booleanArray_lessThanEquals.
		 * @see #booleanArray_lessThanEquals(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName booleanArray_lessThanEquals = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"booleanArray_lessThanEquals");

		/**
		 * Helper binding method for function: booleanArray_listToArray. 
		 * @param list
		 * @return the SourceModule.expr representing an application of booleanArray_listToArray
		 */
		public static final SourceModel.Expr booleanArray_listToArray(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_listToArray), list});
		}

		/**
		 * Name binding for function: booleanArray_listToArray.
		 * @see #booleanArray_listToArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName booleanArray_listToArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"booleanArray_listToArray");

		/**
		 * Helper binding method for function: booleanArray_listToArrayWithFirstElement. 
		 * @param firstElement
		 * @param list
		 * @return the SourceModule.expr representing an application of booleanArray_listToArrayWithFirstElement
		 */
		public static final SourceModel.Expr booleanArray_listToArrayWithFirstElement(SourceModel.Expr firstElement, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_listToArrayWithFirstElement), firstElement, list});
		}

		/**
		 * @see #booleanArray_listToArrayWithFirstElement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param firstElement
		 * @param list
		 * @return the SourceModel.Expr representing an application of booleanArray_listToArrayWithFirstElement
		 */
		public static final SourceModel.Expr booleanArray_listToArrayWithFirstElement(boolean firstElement, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_listToArrayWithFirstElement), SourceModel.Expr.makeBooleanValue(firstElement), list});
		}

		/**
		 * Name binding for function: booleanArray_listToArrayWithFirstElement.
		 * @see #booleanArray_listToArrayWithFirstElement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName booleanArray_listToArrayWithFirstElement = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"booleanArray_listToArrayWithFirstElement");

		/**
		 * Helper binding method for function: booleanArray_makeDefault. 
		 * @param size
		 * @return the SourceModule.expr representing an application of booleanArray_makeDefault
		 */
		public static final SourceModel.Expr booleanArray_makeDefault(SourceModel.Expr size) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_makeDefault), size});
		}

		/**
		 * @see #booleanArray_makeDefault(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param size
		 * @return the SourceModel.Expr representing an application of booleanArray_makeDefault
		 */
		public static final SourceModel.Expr booleanArray_makeDefault(int size) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_makeDefault), SourceModel.Expr.makeIntValue(size)});
		}

		/**
		 * Name binding for function: booleanArray_makeDefault.
		 * @see #booleanArray_makeDefault(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName booleanArray_makeDefault = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"booleanArray_makeDefault");

		/**
		 * Helper binding method for function: booleanArray_max. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of booleanArray_max
		 */
		public static final SourceModel.Expr booleanArray_max(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_max), array1, array2});
		}

		/**
		 * Name binding for function: booleanArray_max.
		 * @see #booleanArray_max(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName booleanArray_max = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"booleanArray_max");

		/**
		 * Helper binding method for function: booleanArray_min. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of booleanArray_min
		 */
		public static final SourceModel.Expr booleanArray_min(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_min), array1, array2});
		}

		/**
		 * Name binding for function: booleanArray_min.
		 * @see #booleanArray_min(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName booleanArray_min = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"booleanArray_min");

		/**
		 * Helper binding method for function: booleanArray_notEquals. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of booleanArray_notEquals
		 */
		public static final SourceModel.Expr booleanArray_notEquals(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_notEquals), array1, array2});
		}

		/**
		 * Name binding for function: booleanArray_notEquals.
		 * @see #booleanArray_notEquals(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName booleanArray_notEquals = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"booleanArray_notEquals");

		/**
		 * Helper binding method for function: booleanArray_removeRange. 
		 * @param array
		 * @param fromIndex
		 * @param toIndex
		 * @return the SourceModule.expr representing an application of booleanArray_removeRange
		 */
		public static final SourceModel.Expr booleanArray_removeRange(SourceModel.Expr array, SourceModel.Expr fromIndex, SourceModel.Expr toIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_removeRange), array, fromIndex, toIndex});
		}

		/**
		 * @see #booleanArray_removeRange(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param fromIndex
		 * @param toIndex
		 * @return the SourceModel.Expr representing an application of booleanArray_removeRange
		 */
		public static final SourceModel.Expr booleanArray_removeRange(SourceModel.Expr array, int fromIndex, int toIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_removeRange), array, SourceModel.Expr.makeIntValue(fromIndex), SourceModel.Expr.makeIntValue(toIndex)});
		}

		/**
		 * Name binding for function: booleanArray_removeRange.
		 * @see #booleanArray_removeRange(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName booleanArray_removeRange = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"booleanArray_removeRange");

		/**
		 * Helper binding method for function: booleanArray_replace. 
		 * @param array
		 * @param oldElementValue
		 * @param newElementValue
		 * @return the SourceModule.expr representing an application of booleanArray_replace
		 */
		public static final SourceModel.Expr booleanArray_replace(SourceModel.Expr array, SourceModel.Expr oldElementValue, SourceModel.Expr newElementValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_replace), array, oldElementValue, newElementValue});
		}

		/**
		 * @see #booleanArray_replace(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param oldElementValue
		 * @param newElementValue
		 * @return the SourceModel.Expr representing an application of booleanArray_replace
		 */
		public static final SourceModel.Expr booleanArray_replace(SourceModel.Expr array, boolean oldElementValue, boolean newElementValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_replace), array, SourceModel.Expr.makeBooleanValue(oldElementValue), SourceModel.Expr.makeBooleanValue(newElementValue)});
		}

		/**
		 * Name binding for function: booleanArray_replace.
		 * @see #booleanArray_replace(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName booleanArray_replace = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"booleanArray_replace");

		/**
		 * Helper binding method for function: booleanArray_replicate. 
		 * @param nCopies
		 * @param valueToReplicate
		 * @return the SourceModule.expr representing an application of booleanArray_replicate
		 */
		public static final SourceModel.Expr booleanArray_replicate(SourceModel.Expr nCopies, SourceModel.Expr valueToReplicate) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_replicate), nCopies, valueToReplicate});
		}

		/**
		 * @see #booleanArray_replicate(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param nCopies
		 * @param valueToReplicate
		 * @return the SourceModel.Expr representing an application of booleanArray_replicate
		 */
		public static final SourceModel.Expr booleanArray_replicate(int nCopies, boolean valueToReplicate) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_replicate), SourceModel.Expr.makeIntValue(nCopies), SourceModel.Expr.makeBooleanValue(valueToReplicate)});
		}

		/**
		 * Name binding for function: booleanArray_replicate.
		 * @see #booleanArray_replicate(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName booleanArray_replicate = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"booleanArray_replicate");

		/**
		 * Helper binding method for function: booleanArray_reverse. 
		 * @param array
		 * @return the SourceModule.expr representing an application of booleanArray_reverse
		 */
		public static final SourceModel.Expr booleanArray_reverse(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_reverse), array});
		}

		/**
		 * Name binding for function: booleanArray_reverse.
		 * @see #booleanArray_reverse(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName booleanArray_reverse = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"booleanArray_reverse");

		/**
		 * Helper binding method for function: booleanArray_sort. 
		 * @param array
		 * @return the SourceModule.expr representing an application of booleanArray_sort
		 */
		public static final SourceModel.Expr booleanArray_sort(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_sort), array});
		}

		/**
		 * Name binding for function: booleanArray_sort.
		 * @see #booleanArray_sort(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName booleanArray_sort = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"booleanArray_sort");

		/**
		 * Helper binding method for function: booleanArray_subArray. 
		 * @param array
		 * @param fromIndex
		 * @param toIndex
		 * @return the SourceModule.expr representing an application of booleanArray_subArray
		 */
		public static final SourceModel.Expr booleanArray_subArray(SourceModel.Expr array, SourceModel.Expr fromIndex, SourceModel.Expr toIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_subArray), array, fromIndex, toIndex});
		}

		/**
		 * @see #booleanArray_subArray(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param fromIndex
		 * @param toIndex
		 * @return the SourceModel.Expr representing an application of booleanArray_subArray
		 */
		public static final SourceModel.Expr booleanArray_subArray(SourceModel.Expr array, int fromIndex, int toIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_subArray), array, SourceModel.Expr.makeIntValue(fromIndex), SourceModel.Expr.makeIntValue(toIndex)});
		}

		/**
		 * Name binding for function: booleanArray_subArray.
		 * @see #booleanArray_subArray(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName booleanArray_subArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"booleanArray_subArray");

		/**
		 * Helper binding method for function: booleanArray_subscript. 
		 * @param array
		 * @param index
		 * @return the SourceModule.expr representing an application of booleanArray_subscript
		 */
		public static final SourceModel.Expr booleanArray_subscript(SourceModel.Expr array, SourceModel.Expr index) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_subscript), array, index});
		}

		/**
		 * @see #booleanArray_subscript(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param index
		 * @return the SourceModel.Expr representing an application of booleanArray_subscript
		 */
		public static final SourceModel.Expr booleanArray_subscript(SourceModel.Expr array, int index) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_subscript), array, SourceModel.Expr.makeIntValue(index)});
		}

		/**
		 * Name binding for function: booleanArray_subscript.
		 * @see #booleanArray_subscript(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName booleanArray_subscript = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"booleanArray_subscript");

		/**
		 * Converts a Java array of primitive Java booleans to a Java array of CAL <code>Cal.Core.Prelude.Boolean</code>s.
		 * @param array (CAL type: <code>Cal.Collections.ArrayPrimitives.JBooleanArray</code>)
		 * @return (CAL type: <code>Cal.Collections.ArrayPrimitives.JCalValueArray</code>) 
		 */
		public static final SourceModel.Expr booleanArray_toCalValueArray(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_toCalValueArray), array});
		}

		/**
		 * Name binding for function: booleanArray_toCalValueArray.
		 * @see #booleanArray_toCalValueArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName booleanArray_toCalValueArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"booleanArray_toCalValueArray");

		/**
		 * Helper binding method for function: booleanArray_toShowString. 
		 * @param array
		 * @return the SourceModule.expr representing an application of booleanArray_toShowString
		 */
		public static final SourceModel.Expr booleanArray_toShowString(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_toShowString), array});
		}

		/**
		 * Name binding for function: booleanArray_toShowString.
		 * @see #booleanArray_toShowString(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName booleanArray_toShowString = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"booleanArray_toShowString");

		/**
		 * Helper binding method for function: booleanArray_update. 
		 * @param array
		 * @param index
		 * @param newValue
		 * @return the SourceModule.expr representing an application of booleanArray_update
		 */
		public static final SourceModel.Expr booleanArray_update(SourceModel.Expr array, SourceModel.Expr index, SourceModel.Expr newValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_update), array, index, newValue});
		}

		/**
		 * @see #booleanArray_update(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param index
		 * @param newValue
		 * @return the SourceModel.Expr representing an application of booleanArray_update
		 */
		public static final SourceModel.Expr booleanArray_update(SourceModel.Expr array, int index, boolean newValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.booleanArray_update), array, SourceModel.Expr.makeIntValue(index), SourceModel.Expr.makeBooleanValue(newValue)});
		}

		/**
		 * Name binding for function: booleanArray_update.
		 * @see #booleanArray_update(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName booleanArray_update = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"booleanArray_update");

		/**
		 * Helper binding method for function: byteArray_append. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of byteArray_append
		 */
		public static final SourceModel.Expr byteArray_append(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_append), array1, array2});
		}

		/**
		 * Name binding for function: byteArray_append.
		 * @see #byteArray_append(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName byteArray_append = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"byteArray_append");

		/**
		 * Helper binding method for function: byteArray_array1. 
		 * @param value
		 * @return the SourceModule.expr representing an application of byteArray_array1
		 */
		public static final SourceModel.Expr byteArray_array1(SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_array1), value});
		}

		/**
		 * @see #byteArray_array1(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value
		 * @return the SourceModel.Expr representing an application of byteArray_array1
		 */
		public static final SourceModel.Expr byteArray_array1(byte value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_array1), SourceModel.Expr.makeByteValue(value)});
		}

		/**
		 * Name binding for function: byteArray_array1.
		 * @see #byteArray_array1(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName byteArray_array1 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"byteArray_array1");

		/**
		 * Helper binding method for function: byteArray_array2. 
		 * @param value1
		 * @param value2
		 * @return the SourceModule.expr representing an application of byteArray_array2
		 */
		public static final SourceModel.Expr byteArray_array2(SourceModel.Expr value1, SourceModel.Expr value2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_array2), value1, value2});
		}

		/**
		 * @see #byteArray_array2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value1
		 * @param value2
		 * @return the SourceModel.Expr representing an application of byteArray_array2
		 */
		public static final SourceModel.Expr byteArray_array2(byte value1, byte value2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_array2), SourceModel.Expr.makeByteValue(value1), SourceModel.Expr.makeByteValue(value2)});
		}

		/**
		 * Name binding for function: byteArray_array2.
		 * @see #byteArray_array2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName byteArray_array2 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"byteArray_array2");

		/**
		 * Helper binding method for function: byteArray_array3. 
		 * @param value1
		 * @param value2
		 * @param value3
		 * @return the SourceModule.expr representing an application of byteArray_array3
		 */
		public static final SourceModel.Expr byteArray_array3(SourceModel.Expr value1, SourceModel.Expr value2, SourceModel.Expr value3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_array3), value1, value2, value3});
		}

		/**
		 * @see #byteArray_array3(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value1
		 * @param value2
		 * @param value3
		 * @return the SourceModel.Expr representing an application of byteArray_array3
		 */
		public static final SourceModel.Expr byteArray_array3(byte value1, byte value2, byte value3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_array3), SourceModel.Expr.makeByteValue(value1), SourceModel.Expr.makeByteValue(value2), SourceModel.Expr.makeByteValue(value3)});
		}

		/**
		 * Name binding for function: byteArray_array3.
		 * @see #byteArray_array3(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName byteArray_array3 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"byteArray_array3");

		/**
		 * Helper binding method for function: byteArray_array4. 
		 * @param value1
		 * @param value2
		 * @param value3
		 * @param value4
		 * @return the SourceModule.expr representing an application of byteArray_array4
		 */
		public static final SourceModel.Expr byteArray_array4(SourceModel.Expr value1, SourceModel.Expr value2, SourceModel.Expr value3, SourceModel.Expr value4) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_array4), value1, value2, value3, value4});
		}

		/**
		 * @see #byteArray_array4(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value1
		 * @param value2
		 * @param value3
		 * @param value4
		 * @return the SourceModel.Expr representing an application of byteArray_array4
		 */
		public static final SourceModel.Expr byteArray_array4(byte value1, byte value2, byte value3, byte value4) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_array4), SourceModel.Expr.makeByteValue(value1), SourceModel.Expr.makeByteValue(value2), SourceModel.Expr.makeByteValue(value3), SourceModel.Expr.makeByteValue(value4)});
		}

		/**
		 * Name binding for function: byteArray_array4.
		 * @see #byteArray_array4(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName byteArray_array4 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"byteArray_array4");

		/**
		 * Helper binding method for function: byteArray_array5. 
		 * @param value1
		 * @param value2
		 * @param value3
		 * @param value4
		 * @param value5
		 * @return the SourceModule.expr representing an application of byteArray_array5
		 */
		public static final SourceModel.Expr byteArray_array5(SourceModel.Expr value1, SourceModel.Expr value2, SourceModel.Expr value3, SourceModel.Expr value4, SourceModel.Expr value5) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_array5), value1, value2, value3, value4, value5});
		}

		/**
		 * @see #byteArray_array5(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value1
		 * @param value2
		 * @param value3
		 * @param value4
		 * @param value5
		 * @return the SourceModel.Expr representing an application of byteArray_array5
		 */
		public static final SourceModel.Expr byteArray_array5(byte value1, byte value2, byte value3, byte value4, byte value5) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_array5), SourceModel.Expr.makeByteValue(value1), SourceModel.Expr.makeByteValue(value2), SourceModel.Expr.makeByteValue(value3), SourceModel.Expr.makeByteValue(value4), SourceModel.Expr.makeByteValue(value5)});
		}

		/**
		 * Name binding for function: byteArray_array5.
		 * @see #byteArray_array5(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName byteArray_array5 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"byteArray_array5");

		/**
		 * Helper binding method for function: byteArray_array6. 
		 * @param value1
		 * @param value2
		 * @param value3
		 * @param value4
		 * @param value5
		 * @param value6
		 * @return the SourceModule.expr representing an application of byteArray_array6
		 */
		public static final SourceModel.Expr byteArray_array6(SourceModel.Expr value1, SourceModel.Expr value2, SourceModel.Expr value3, SourceModel.Expr value4, SourceModel.Expr value5, SourceModel.Expr value6) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_array6), value1, value2, value3, value4, value5, value6});
		}

		/**
		 * @see #byteArray_array6(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value1
		 * @param value2
		 * @param value3
		 * @param value4
		 * @param value5
		 * @param value6
		 * @return the SourceModel.Expr representing an application of byteArray_array6
		 */
		public static final SourceModel.Expr byteArray_array6(byte value1, byte value2, byte value3, byte value4, byte value5, byte value6) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_array6), SourceModel.Expr.makeByteValue(value1), SourceModel.Expr.makeByteValue(value2), SourceModel.Expr.makeByteValue(value3), SourceModel.Expr.makeByteValue(value4), SourceModel.Expr.makeByteValue(value5), SourceModel.Expr.makeByteValue(value6)});
		}

		/**
		 * Name binding for function: byteArray_array6.
		 * @see #byteArray_array6(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName byteArray_array6 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"byteArray_array6");

		/**
		 * Helper binding method for function: byteArray_array7. 
		 * @param value1
		 * @param value2
		 * @param value3
		 * @param value4
		 * @param value5
		 * @param value6
		 * @param value7
		 * @return the SourceModule.expr representing an application of byteArray_array7
		 */
		public static final SourceModel.Expr byteArray_array7(SourceModel.Expr value1, SourceModel.Expr value2, SourceModel.Expr value3, SourceModel.Expr value4, SourceModel.Expr value5, SourceModel.Expr value6, SourceModel.Expr value7) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_array7), value1, value2, value3, value4, value5, value6, value7});
		}

		/**
		 * @see #byteArray_array7(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value1
		 * @param value2
		 * @param value3
		 * @param value4
		 * @param value5
		 * @param value6
		 * @param value7
		 * @return the SourceModel.Expr representing an application of byteArray_array7
		 */
		public static final SourceModel.Expr byteArray_array7(byte value1, byte value2, byte value3, byte value4, byte value5, byte value6, byte value7) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_array7), SourceModel.Expr.makeByteValue(value1), SourceModel.Expr.makeByteValue(value2), SourceModel.Expr.makeByteValue(value3), SourceModel.Expr.makeByteValue(value4), SourceModel.Expr.makeByteValue(value5), SourceModel.Expr.makeByteValue(value6), SourceModel.Expr.makeByteValue(value7)});
		}

		/**
		 * Name binding for function: byteArray_array7.
		 * @see #byteArray_array7(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName byteArray_array7 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"byteArray_array7");

		/**
		 * Helper binding method for function: byteArray_arrayToList. 
		 * @param array
		 * @return the SourceModule.expr representing an application of byteArray_arrayToList
		 */
		public static final SourceModel.Expr byteArray_arrayToList(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_arrayToList), array});
		}

		/**
		 * Name binding for function: byteArray_arrayToList.
		 * @see #byteArray_arrayToList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName byteArray_arrayToList = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"byteArray_arrayToList");

		/**
		 * Helper binding method for function: byteArray_binarySearch. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of byteArray_binarySearch
		 */
		public static final SourceModel.Expr byteArray_binarySearch(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_binarySearch), arg_1, arg_2});
		}

		/**
		 * @see #byteArray_binarySearch(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of byteArray_binarySearch
		 */
		public static final SourceModel.Expr byteArray_binarySearch(SourceModel.Expr arg_1, byte arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_binarySearch), arg_1, SourceModel.Expr.makeByteValue(arg_2)});
		}

		/**
		 * Name binding for function: byteArray_binarySearch.
		 * @see #byteArray_binarySearch(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName byteArray_binarySearch = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"byteArray_binarySearch");

		/**
		 * Helper binding method for function: byteArray_cloneReplacingNullArray. 
		 * @param array
		 * @return the SourceModule.expr representing an application of byteArray_cloneReplacingNullArray
		 */
		public static final SourceModel.Expr byteArray_cloneReplacingNullArray(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_cloneReplacingNullArray), array});
		}

		/**
		 * Name binding for function: byteArray_cloneReplacingNullArray.
		 * @see #byteArray_cloneReplacingNullArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName byteArray_cloneReplacingNullArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"byteArray_cloneReplacingNullArray");

		/**
		 * Helper binding method for function: byteArray_compare. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of byteArray_compare
		 */
		public static final SourceModel.Expr byteArray_compare(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_compare), array1, array2});
		}

		/**
		 * Name binding for function: byteArray_compare.
		 * @see #byteArray_compare(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName byteArray_compare = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"byteArray_compare");

		/**
		 * Helper binding method for function: byteArray_compareHelper. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of byteArray_compareHelper
		 */
		public static final SourceModel.Expr byteArray_compareHelper(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_compareHelper), array1, array2});
		}

		/**
		 * Name binding for function: byteArray_compareHelper.
		 * @see #byteArray_compareHelper(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName byteArray_compareHelper = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"byteArray_compareHelper");

		/**
		 * Helper binding method for function: byteArray_compress. 
		 * @param byteArray
		 * @return the SourceModule.expr representing an application of byteArray_compress
		 */
		public static final SourceModel.Expr byteArray_compress(SourceModel.Expr byteArray) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_compress), byteArray});
		}

		/**
		 * Name binding for function: byteArray_compress.
		 * @see #byteArray_compress(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName byteArray_compress = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"byteArray_compress");

		/**
		 * Helper binding method for function: byteArray_concat. 
		 * @param list
		 * @return the SourceModule.expr representing an application of byteArray_concat
		 */
		public static final SourceModel.Expr byteArray_concat(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_concat), list});
		}

		/**
		 * Name binding for function: byteArray_concat.
		 * @see #byteArray_concat(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName byteArray_concat = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"byteArray_concat");

		/**
		 * Helper binding method for function: byteArray_concatList. 
		 * @param list
		 * @return the SourceModule.expr representing an application of byteArray_concatList
		 */
		public static final SourceModel.Expr byteArray_concatList(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_concatList), list});
		}

		/**
		 * Name binding for function: byteArray_concatList.
		 * @see #byteArray_concatList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName byteArray_concatList = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"byteArray_concatList");

		/**
		 * Helper binding method for function: byteArray_decompress. 
		 * @param byteArray
		 * @return the SourceModule.expr representing an application of byteArray_decompress
		 */
		public static final SourceModel.Expr byteArray_decompress(SourceModel.Expr byteArray) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_decompress), byteArray});
		}

		/**
		 * Name binding for function: byteArray_decompress.
		 * @see #byteArray_decompress(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName byteArray_decompress = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"byteArray_decompress");

		/**
		 * Helper binding method for function: byteArray_empty. 
		 * @return the SourceModule.expr representing an application of byteArray_empty
		 */
		public static final SourceModel.Expr byteArray_empty() {
			return SourceModel.Expr.Var.make(Functions.byteArray_empty);
		}

		/**
		 * Name binding for function: byteArray_empty.
		 * @see #byteArray_empty()
		 */
		public static final QualifiedName byteArray_empty = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"byteArray_empty");

		/**
		 * Helper binding method for function: byteArray_equals. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of byteArray_equals
		 */
		public static final SourceModel.Expr byteArray_equals(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_equals), array1, array2});
		}

		/**
		 * Name binding for function: byteArray_equals.
		 * @see #byteArray_equals(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName byteArray_equals = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"byteArray_equals");

		/**
		 * Converts a Java array of CAL <code>Cal.Core.Prelude.Byte</code>s to a Java array of primitive Java bytes.
		 * @param calValueArray (CAL type: <code>Cal.Collections.ArrayPrimitives.JCalValueArray</code>)
		 * @return (CAL type: <code>Cal.Collections.ArrayPrimitives.JByteArray</code>) 
		 */
		public static final SourceModel.Expr byteArray_fromCalValueArray(SourceModel.Expr calValueArray) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_fromCalValueArray), calValueArray});
		}

		/**
		 * Name binding for function: byteArray_fromCalValueArray.
		 * @see #byteArray_fromCalValueArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName byteArray_fromCalValueArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"byteArray_fromCalValueArray");

		/**
		 * Helper binding method for function: byteArray_greaterThan. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of byteArray_greaterThan
		 */
		public static final SourceModel.Expr byteArray_greaterThan(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_greaterThan), array1, array2});
		}

		/**
		 * Name binding for function: byteArray_greaterThan.
		 * @see #byteArray_greaterThan(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName byteArray_greaterThan = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"byteArray_greaterThan");

		/**
		 * Helper binding method for function: byteArray_greaterThanEquals. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of byteArray_greaterThanEquals
		 */
		public static final SourceModel.Expr byteArray_greaterThanEquals(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_greaterThanEquals), array1, array2});
		}

		/**
		 * Name binding for function: byteArray_greaterThanEquals.
		 * @see #byteArray_greaterThanEquals(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName byteArray_greaterThanEquals = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"byteArray_greaterThanEquals");

		/**
		 * Helper binding method for function: byteArray_indexOf. 
		 * @param array
		 * @param element
		 * @return the SourceModule.expr representing an application of byteArray_indexOf
		 */
		public static final SourceModel.Expr byteArray_indexOf(SourceModel.Expr array, SourceModel.Expr element) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_indexOf), array, element});
		}

		/**
		 * @see #byteArray_indexOf(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param element
		 * @return the SourceModel.Expr representing an application of byteArray_indexOf
		 */
		public static final SourceModel.Expr byteArray_indexOf(SourceModel.Expr array, byte element) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_indexOf), array, SourceModel.Expr.makeByteValue(element)});
		}

		/**
		 * Name binding for function: byteArray_indexOf.
		 * @see #byteArray_indexOf(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName byteArray_indexOf = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"byteArray_indexOf");

		/**
		 * Helper binding method for function: byteArray_indexOf2. 
		 * @param array
		 * @param element
		 * @param fromIndex
		 * @return the SourceModule.expr representing an application of byteArray_indexOf2
		 */
		public static final SourceModel.Expr byteArray_indexOf2(SourceModel.Expr array, SourceModel.Expr element, SourceModel.Expr fromIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_indexOf2), array, element, fromIndex});
		}

		/**
		 * @see #byteArray_indexOf2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param element
		 * @param fromIndex
		 * @return the SourceModel.Expr representing an application of byteArray_indexOf2
		 */
		public static final SourceModel.Expr byteArray_indexOf2(SourceModel.Expr array, byte element, int fromIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_indexOf2), array, SourceModel.Expr.makeByteValue(element), SourceModel.Expr.makeIntValue(fromIndex)});
		}

		/**
		 * Name binding for function: byteArray_indexOf2.
		 * @see #byteArray_indexOf2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName byteArray_indexOf2 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"byteArray_indexOf2");

		/**
		 * Helper binding method for function: byteArray_isEmpty. 
		 * @param array
		 * @return the SourceModule.expr representing an application of byteArray_isEmpty
		 */
		public static final SourceModel.Expr byteArray_isEmpty(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_isEmpty), array});
		}

		/**
		 * Name binding for function: byteArray_isEmpty.
		 * @see #byteArray_isEmpty(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName byteArray_isEmpty = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"byteArray_isEmpty");

		/**
		 * Helper binding method for function: byteArray_lastIndexOf. 
		 * @param array
		 * @param element
		 * @return the SourceModule.expr representing an application of byteArray_lastIndexOf
		 */
		public static final SourceModel.Expr byteArray_lastIndexOf(SourceModel.Expr array, SourceModel.Expr element) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_lastIndexOf), array, element});
		}

		/**
		 * @see #byteArray_lastIndexOf(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param element
		 * @return the SourceModel.Expr representing an application of byteArray_lastIndexOf
		 */
		public static final SourceModel.Expr byteArray_lastIndexOf(SourceModel.Expr array, byte element) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_lastIndexOf), array, SourceModel.Expr.makeByteValue(element)});
		}

		/**
		 * Name binding for function: byteArray_lastIndexOf.
		 * @see #byteArray_lastIndexOf(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName byteArray_lastIndexOf = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"byteArray_lastIndexOf");

		/**
		 * Helper binding method for function: byteArray_lastIndexOf2. 
		 * @param array
		 * @param element
		 * @param fromIndex
		 * @return the SourceModule.expr representing an application of byteArray_lastIndexOf2
		 */
		public static final SourceModel.Expr byteArray_lastIndexOf2(SourceModel.Expr array, SourceModel.Expr element, SourceModel.Expr fromIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_lastIndexOf2), array, element, fromIndex});
		}

		/**
		 * @see #byteArray_lastIndexOf2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param element
		 * @param fromIndex
		 * @return the SourceModel.Expr representing an application of byteArray_lastIndexOf2
		 */
		public static final SourceModel.Expr byteArray_lastIndexOf2(SourceModel.Expr array, byte element, int fromIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_lastIndexOf2), array, SourceModel.Expr.makeByteValue(element), SourceModel.Expr.makeIntValue(fromIndex)});
		}

		/**
		 * Name binding for function: byteArray_lastIndexOf2.
		 * @see #byteArray_lastIndexOf2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName byteArray_lastIndexOf2 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"byteArray_lastIndexOf2");

		/**
		 * Helper binding method for function: byteArray_length. 
		 * @param array
		 * @return the SourceModule.expr representing an application of byteArray_length
		 */
		public static final SourceModel.Expr byteArray_length(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_length), array});
		}

		/**
		 * Name binding for function: byteArray_length.
		 * @see #byteArray_length(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName byteArray_length = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"byteArray_length");

		/**
		 * Helper binding method for function: byteArray_lessThan. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of byteArray_lessThan
		 */
		public static final SourceModel.Expr byteArray_lessThan(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_lessThan), array1, array2});
		}

		/**
		 * Name binding for function: byteArray_lessThan.
		 * @see #byteArray_lessThan(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName byteArray_lessThan = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"byteArray_lessThan");

		/**
		 * Helper binding method for function: byteArray_lessThanEquals. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of byteArray_lessThanEquals
		 */
		public static final SourceModel.Expr byteArray_lessThanEquals(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_lessThanEquals), array1, array2});
		}

		/**
		 * Name binding for function: byteArray_lessThanEquals.
		 * @see #byteArray_lessThanEquals(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName byteArray_lessThanEquals = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"byteArray_lessThanEquals");

		/**
		 * Helper binding method for function: byteArray_listToArray. 
		 * @param list
		 * @return the SourceModule.expr representing an application of byteArray_listToArray
		 */
		public static final SourceModel.Expr byteArray_listToArray(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_listToArray), list});
		}

		/**
		 * Name binding for function: byteArray_listToArray.
		 * @see #byteArray_listToArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName byteArray_listToArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"byteArray_listToArray");

		/**
		 * Helper binding method for function: byteArray_listToArrayWithFirstElement. 
		 * @param firstElement
		 * @param list
		 * @return the SourceModule.expr representing an application of byteArray_listToArrayWithFirstElement
		 */
		public static final SourceModel.Expr byteArray_listToArrayWithFirstElement(SourceModel.Expr firstElement, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_listToArrayWithFirstElement), firstElement, list});
		}

		/**
		 * @see #byteArray_listToArrayWithFirstElement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param firstElement
		 * @param list
		 * @return the SourceModel.Expr representing an application of byteArray_listToArrayWithFirstElement
		 */
		public static final SourceModel.Expr byteArray_listToArrayWithFirstElement(byte firstElement, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_listToArrayWithFirstElement), SourceModel.Expr.makeByteValue(firstElement), list});
		}

		/**
		 * Name binding for function: byteArray_listToArrayWithFirstElement.
		 * @see #byteArray_listToArrayWithFirstElement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName byteArray_listToArrayWithFirstElement = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"byteArray_listToArrayWithFirstElement");

		/**
		 * Helper binding method for function: byteArray_makeDefault. 
		 * @param size
		 * @return the SourceModule.expr representing an application of byteArray_makeDefault
		 */
		public static final SourceModel.Expr byteArray_makeDefault(SourceModel.Expr size) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_makeDefault), size});
		}

		/**
		 * @see #byteArray_makeDefault(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param size
		 * @return the SourceModel.Expr representing an application of byteArray_makeDefault
		 */
		public static final SourceModel.Expr byteArray_makeDefault(int size) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_makeDefault), SourceModel.Expr.makeIntValue(size)});
		}

		/**
		 * Name binding for function: byteArray_makeDefault.
		 * @see #byteArray_makeDefault(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName byteArray_makeDefault = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"byteArray_makeDefault");

		/**
		 * Helper binding method for function: byteArray_max. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of byteArray_max
		 */
		public static final SourceModel.Expr byteArray_max(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_max), array1, array2});
		}

		/**
		 * Name binding for function: byteArray_max.
		 * @see #byteArray_max(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName byteArray_max = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"byteArray_max");

		/**
		 * Helper binding method for function: byteArray_min. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of byteArray_min
		 */
		public static final SourceModel.Expr byteArray_min(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_min), array1, array2});
		}

		/**
		 * Name binding for function: byteArray_min.
		 * @see #byteArray_min(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName byteArray_min = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"byteArray_min");

		/**
		 * Helper binding method for function: byteArray_notEquals. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of byteArray_notEquals
		 */
		public static final SourceModel.Expr byteArray_notEquals(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_notEquals), array1, array2});
		}

		/**
		 * Name binding for function: byteArray_notEquals.
		 * @see #byteArray_notEquals(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName byteArray_notEquals = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"byteArray_notEquals");

		/**
		 * Helper binding method for function: byteArray_removeRange. 
		 * @param array
		 * @param fromIndex
		 * @param toIndex
		 * @return the SourceModule.expr representing an application of byteArray_removeRange
		 */
		public static final SourceModel.Expr byteArray_removeRange(SourceModel.Expr array, SourceModel.Expr fromIndex, SourceModel.Expr toIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_removeRange), array, fromIndex, toIndex});
		}

		/**
		 * @see #byteArray_removeRange(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param fromIndex
		 * @param toIndex
		 * @return the SourceModel.Expr representing an application of byteArray_removeRange
		 */
		public static final SourceModel.Expr byteArray_removeRange(SourceModel.Expr array, int fromIndex, int toIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_removeRange), array, SourceModel.Expr.makeIntValue(fromIndex), SourceModel.Expr.makeIntValue(toIndex)});
		}

		/**
		 * Name binding for function: byteArray_removeRange.
		 * @see #byteArray_removeRange(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName byteArray_removeRange = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"byteArray_removeRange");

		/**
		 * Helper binding method for function: byteArray_replace. 
		 * @param array
		 * @param oldElementValue
		 * @param newElementValue
		 * @return the SourceModule.expr representing an application of byteArray_replace
		 */
		public static final SourceModel.Expr byteArray_replace(SourceModel.Expr array, SourceModel.Expr oldElementValue, SourceModel.Expr newElementValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_replace), array, oldElementValue, newElementValue});
		}

		/**
		 * @see #byteArray_replace(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param oldElementValue
		 * @param newElementValue
		 * @return the SourceModel.Expr representing an application of byteArray_replace
		 */
		public static final SourceModel.Expr byteArray_replace(SourceModel.Expr array, byte oldElementValue, byte newElementValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_replace), array, SourceModel.Expr.makeByteValue(oldElementValue), SourceModel.Expr.makeByteValue(newElementValue)});
		}

		/**
		 * Name binding for function: byteArray_replace.
		 * @see #byteArray_replace(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName byteArray_replace = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"byteArray_replace");

		/**
		 * Helper binding method for function: byteArray_replicate. 
		 * @param nCopies
		 * @param valueToReplicate
		 * @return the SourceModule.expr representing an application of byteArray_replicate
		 */
		public static final SourceModel.Expr byteArray_replicate(SourceModel.Expr nCopies, SourceModel.Expr valueToReplicate) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_replicate), nCopies, valueToReplicate});
		}

		/**
		 * @see #byteArray_replicate(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param nCopies
		 * @param valueToReplicate
		 * @return the SourceModel.Expr representing an application of byteArray_replicate
		 */
		public static final SourceModel.Expr byteArray_replicate(int nCopies, byte valueToReplicate) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_replicate), SourceModel.Expr.makeIntValue(nCopies), SourceModel.Expr.makeByteValue(valueToReplicate)});
		}

		/**
		 * Name binding for function: byteArray_replicate.
		 * @see #byteArray_replicate(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName byteArray_replicate = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"byteArray_replicate");

		/**
		 * Helper binding method for function: byteArray_reverse. 
		 * @param array
		 * @return the SourceModule.expr representing an application of byteArray_reverse
		 */
		public static final SourceModel.Expr byteArray_reverse(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_reverse), array});
		}

		/**
		 * Name binding for function: byteArray_reverse.
		 * @see #byteArray_reverse(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName byteArray_reverse = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"byteArray_reverse");

		/**
		 * Helper binding method for function: byteArray_sort. 
		 * @param array
		 * @return the SourceModule.expr representing an application of byteArray_sort
		 */
		public static final SourceModel.Expr byteArray_sort(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_sort), array});
		}

		/**
		 * Name binding for function: byteArray_sort.
		 * @see #byteArray_sort(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName byteArray_sort = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"byteArray_sort");

		/**
		 * Helper binding method for function: byteArray_subArray. 
		 * @param array
		 * @param fromIndex
		 * @param toIndex
		 * @return the SourceModule.expr representing an application of byteArray_subArray
		 */
		public static final SourceModel.Expr byteArray_subArray(SourceModel.Expr array, SourceModel.Expr fromIndex, SourceModel.Expr toIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_subArray), array, fromIndex, toIndex});
		}

		/**
		 * @see #byteArray_subArray(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param fromIndex
		 * @param toIndex
		 * @return the SourceModel.Expr representing an application of byteArray_subArray
		 */
		public static final SourceModel.Expr byteArray_subArray(SourceModel.Expr array, int fromIndex, int toIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_subArray), array, SourceModel.Expr.makeIntValue(fromIndex), SourceModel.Expr.makeIntValue(toIndex)});
		}

		/**
		 * Name binding for function: byteArray_subArray.
		 * @see #byteArray_subArray(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName byteArray_subArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"byteArray_subArray");

		/**
		 * Helper binding method for function: byteArray_subscript. 
		 * @param array
		 * @param index
		 * @return the SourceModule.expr representing an application of byteArray_subscript
		 */
		public static final SourceModel.Expr byteArray_subscript(SourceModel.Expr array, SourceModel.Expr index) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_subscript), array, index});
		}

		/**
		 * @see #byteArray_subscript(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param index
		 * @return the SourceModel.Expr representing an application of byteArray_subscript
		 */
		public static final SourceModel.Expr byteArray_subscript(SourceModel.Expr array, int index) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_subscript), array, SourceModel.Expr.makeIntValue(index)});
		}

		/**
		 * Name binding for function: byteArray_subscript.
		 * @see #byteArray_subscript(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName byteArray_subscript = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"byteArray_subscript");

		/**
		 * Converts a Java array of primitive Java bytes to a Java array of CAL <code>Cal.Core.Prelude.Byte</code>s.
		 * @param array (CAL type: <code>Cal.Collections.ArrayPrimitives.JByteArray</code>)
		 * @return (CAL type: <code>Cal.Collections.ArrayPrimitives.JCalValueArray</code>) 
		 */
		public static final SourceModel.Expr byteArray_toCalValueArray(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_toCalValueArray), array});
		}

		/**
		 * Name binding for function: byteArray_toCalValueArray.
		 * @see #byteArray_toCalValueArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName byteArray_toCalValueArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"byteArray_toCalValueArray");

		/**
		 * Helper binding method for function: byteArray_toShowString. 
		 * @param array
		 * @return the SourceModule.expr representing an application of byteArray_toShowString
		 */
		public static final SourceModel.Expr byteArray_toShowString(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_toShowString), array});
		}

		/**
		 * Name binding for function: byteArray_toShowString.
		 * @see #byteArray_toShowString(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName byteArray_toShowString = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"byteArray_toShowString");

		/**
		 * Helper binding method for function: byteArray_update. 
		 * @param array
		 * @param index
		 * @param newValue
		 * @return the SourceModule.expr representing an application of byteArray_update
		 */
		public static final SourceModel.Expr byteArray_update(SourceModel.Expr array, SourceModel.Expr index, SourceModel.Expr newValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_update), array, index, newValue});
		}

		/**
		 * @see #byteArray_update(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param index
		 * @param newValue
		 * @return the SourceModel.Expr representing an application of byteArray_update
		 */
		public static final SourceModel.Expr byteArray_update(SourceModel.Expr array, int index, byte newValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteArray_update), array, SourceModel.Expr.makeIntValue(index), SourceModel.Expr.makeByteValue(newValue)});
		}

		/**
		 * Name binding for function: byteArray_update.
		 * @see #byteArray_update(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName byteArray_update = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"byteArray_update");

		/**
		 * Helper binding method for function: calValueArray_append. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of calValueArray_append
		 */
		public static final SourceModel.Expr calValueArray_append(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.calValueArray_append), array1, array2});
		}

		/**
		 * Name binding for function: calValueArray_append.
		 * @see #calValueArray_append(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName calValueArray_append = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"calValueArray_append");

		/**
		 * Helper binding method for function: calValueArray_array1. 
		 * @param value
		 * @return the SourceModule.expr representing an application of calValueArray_array1
		 */
		public static final SourceModel.Expr calValueArray_array1(SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.calValueArray_array1), value});
		}

		/**
		 * Name binding for function: calValueArray_array1.
		 * @see #calValueArray_array1(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName calValueArray_array1 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"calValueArray_array1");

		/**
		 * Helper binding method for function: calValueArray_array2. 
		 * @param value1
		 * @param value2
		 * @return the SourceModule.expr representing an application of calValueArray_array2
		 */
		public static final SourceModel.Expr calValueArray_array2(SourceModel.Expr value1, SourceModel.Expr value2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.calValueArray_array2), value1, value2});
		}

		/**
		 * Name binding for function: calValueArray_array2.
		 * @see #calValueArray_array2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName calValueArray_array2 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"calValueArray_array2");

		/**
		 * Helper binding method for function: calValueArray_array3. 
		 * @param value1
		 * @param value2
		 * @param value3
		 * @return the SourceModule.expr representing an application of calValueArray_array3
		 */
		public static final SourceModel.Expr calValueArray_array3(SourceModel.Expr value1, SourceModel.Expr value2, SourceModel.Expr value3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.calValueArray_array3), value1, value2, value3});
		}

		/**
		 * Name binding for function: calValueArray_array3.
		 * @see #calValueArray_array3(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName calValueArray_array3 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"calValueArray_array3");

		/**
		 * Helper binding method for function: calValueArray_array4. 
		 * @param value1
		 * @param value2
		 * @param value3
		 * @param value4
		 * @return the SourceModule.expr representing an application of calValueArray_array4
		 */
		public static final SourceModel.Expr calValueArray_array4(SourceModel.Expr value1, SourceModel.Expr value2, SourceModel.Expr value3, SourceModel.Expr value4) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.calValueArray_array4), value1, value2, value3, value4});
		}

		/**
		 * Name binding for function: calValueArray_array4.
		 * @see #calValueArray_array4(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName calValueArray_array4 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"calValueArray_array4");

		/**
		 * Helper binding method for function: calValueArray_array5. 
		 * @param value1
		 * @param value2
		 * @param value3
		 * @param value4
		 * @param value5
		 * @return the SourceModule.expr representing an application of calValueArray_array5
		 */
		public static final SourceModel.Expr calValueArray_array5(SourceModel.Expr value1, SourceModel.Expr value2, SourceModel.Expr value3, SourceModel.Expr value4, SourceModel.Expr value5) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.calValueArray_array5), value1, value2, value3, value4, value5});
		}

		/**
		 * Name binding for function: calValueArray_array5.
		 * @see #calValueArray_array5(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName calValueArray_array5 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"calValueArray_array5");

		/**
		 * Helper binding method for function: calValueArray_array6. 
		 * @param value1
		 * @param value2
		 * @param value3
		 * @param value4
		 * @param value5
		 * @param value6
		 * @return the SourceModule.expr representing an application of calValueArray_array6
		 */
		public static final SourceModel.Expr calValueArray_array6(SourceModel.Expr value1, SourceModel.Expr value2, SourceModel.Expr value3, SourceModel.Expr value4, SourceModel.Expr value5, SourceModel.Expr value6) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.calValueArray_array6), value1, value2, value3, value4, value5, value6});
		}

		/**
		 * Name binding for function: calValueArray_array6.
		 * @see #calValueArray_array6(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName calValueArray_array6 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"calValueArray_array6");

		/**
		 * Helper binding method for function: calValueArray_array7. 
		 * @param value1
		 * @param value2
		 * @param value3
		 * @param value4
		 * @param value5
		 * @param value6
		 * @param value7
		 * @return the SourceModule.expr representing an application of calValueArray_array7
		 */
		public static final SourceModel.Expr calValueArray_array7(SourceModel.Expr value1, SourceModel.Expr value2, SourceModel.Expr value3, SourceModel.Expr value4, SourceModel.Expr value5, SourceModel.Expr value6, SourceModel.Expr value7) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.calValueArray_array7), value1, value2, value3, value4, value5, value6, value7});
		}

		/**
		 * Name binding for function: calValueArray_array7.
		 * @see #calValueArray_array7(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName calValueArray_array7 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"calValueArray_array7");

		/**
		 * Helper binding method for function: calValueArray_arrayToList. 
		 * @param array
		 * @return the SourceModule.expr representing an application of calValueArray_arrayToList
		 */
		public static final SourceModel.Expr calValueArray_arrayToList(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.calValueArray_arrayToList), array});
		}

		/**
		 * Name binding for function: calValueArray_arrayToList.
		 * @see #calValueArray_arrayToList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName calValueArray_arrayToList = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"calValueArray_arrayToList");

		/**
		 * Helper binding method for function: calValueArray_cloneReplacingNullArray. 
		 * @param array
		 * @return the SourceModule.expr representing an application of calValueArray_cloneReplacingNullArray
		 */
		public static final SourceModel.Expr calValueArray_cloneReplacingNullArray(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.calValueArray_cloneReplacingNullArray), array});
		}

		/**
		 * Name binding for function: calValueArray_cloneReplacingNullArray.
		 * @see #calValueArray_cloneReplacingNullArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName calValueArray_cloneReplacingNullArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"calValueArray_cloneReplacingNullArray");

		/**
		 * Helper binding method for function: calValueArray_concat. 
		 * @param list
		 * @return the SourceModule.expr representing an application of calValueArray_concat
		 */
		public static final SourceModel.Expr calValueArray_concat(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.calValueArray_concat), list});
		}

		/**
		 * Name binding for function: calValueArray_concat.
		 * @see #calValueArray_concat(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName calValueArray_concat = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"calValueArray_concat");

		/**
		 * Helper binding method for function: calValueArray_concatList. 
		 * @param list
		 * @return the SourceModule.expr representing an application of calValueArray_concatList
		 */
		public static final SourceModel.Expr calValueArray_concatList(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.calValueArray_concatList), list});
		}

		/**
		 * Name binding for function: calValueArray_concatList.
		 * @see #calValueArray_concatList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName calValueArray_concatList = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"calValueArray_concatList");

		/**
		 * Helper binding method for function: calValueArray_empty. 
		 * @return the SourceModule.expr representing an application of calValueArray_empty
		 */
		public static final SourceModel.Expr calValueArray_empty() {
			return SourceModel.Expr.Var.make(Functions.calValueArray_empty);
		}

		/**
		 * Name binding for function: calValueArray_empty.
		 * @see #calValueArray_empty()
		 */
		public static final QualifiedName calValueArray_empty = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"calValueArray_empty");

		/**
		 * Identity on the container Java array of CAL values.
		 * @param calValueArray (CAL type: <code>Cal.Collections.ArrayPrimitives.JCalValueArray</code>)
		 * @return (CAL type: <code>Cal.Collections.ArrayPrimitives.JCalValueArray</code>) 
		 */
		public static final SourceModel.Expr calValueArray_fromCalValueArray(SourceModel.Expr calValueArray) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.calValueArray_fromCalValueArray), calValueArray});
		}

		/**
		 * Name binding for function: calValueArray_fromCalValueArray.
		 * @see #calValueArray_fromCalValueArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName calValueArray_fromCalValueArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"calValueArray_fromCalValueArray");

		/**
		 * Helper binding method for function: calValueArray_isEmpty. 
		 * @param array
		 * @return the SourceModule.expr representing an application of calValueArray_isEmpty
		 */
		public static final SourceModel.Expr calValueArray_isEmpty(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.calValueArray_isEmpty), array});
		}

		/**
		 * Name binding for function: calValueArray_isEmpty.
		 * @see #calValueArray_isEmpty(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName calValueArray_isEmpty = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"calValueArray_isEmpty");

		/**
		 * Helper binding method for function: calValueArray_length. 
		 * @param array
		 * @return the SourceModule.expr representing an application of calValueArray_length
		 */
		public static final SourceModel.Expr calValueArray_length(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.calValueArray_length), array});
		}

		/**
		 * Name binding for function: calValueArray_length.
		 * @see #calValueArray_length(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName calValueArray_length = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"calValueArray_length");

		/**
		 * Helper binding method for function: calValueArray_listToArray. 
		 * @param list
		 * @return the SourceModule.expr representing an application of calValueArray_listToArray
		 */
		public static final SourceModel.Expr calValueArray_listToArray(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.calValueArray_listToArray), list});
		}

		/**
		 * Name binding for function: calValueArray_listToArray.
		 * @see #calValueArray_listToArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName calValueArray_listToArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"calValueArray_listToArray");

		/**
		 * Helper binding method for function: calValueArray_listToArrayWithFirstElement. 
		 * @param firstElement
		 * @param list
		 * @return the SourceModule.expr representing an application of calValueArray_listToArrayWithFirstElement
		 */
		public static final SourceModel.Expr calValueArray_listToArrayWithFirstElement(SourceModel.Expr firstElement, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.calValueArray_listToArrayWithFirstElement), firstElement, list});
		}

		/**
		 * Name binding for function: calValueArray_listToArrayWithFirstElement.
		 * @see #calValueArray_listToArrayWithFirstElement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName calValueArray_listToArrayWithFirstElement = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"calValueArray_listToArrayWithFirstElement");

		/**
		 * Helper binding method for function: calValueArray_makeDefault. 
		 * @param size
		 * @return the SourceModule.expr representing an application of calValueArray_makeDefault
		 */
		public static final SourceModel.Expr calValueArray_makeDefault(SourceModel.Expr size) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.calValueArray_makeDefault), size});
		}

		/**
		 * @see #calValueArray_makeDefault(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param size
		 * @return the SourceModel.Expr representing an application of calValueArray_makeDefault
		 */
		public static final SourceModel.Expr calValueArray_makeDefault(int size) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.calValueArray_makeDefault), SourceModel.Expr.makeIntValue(size)});
		}

		/**
		 * Name binding for function: calValueArray_makeDefault.
		 * @see #calValueArray_makeDefault(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName calValueArray_makeDefault = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"calValueArray_makeDefault");

		/**
		 * Helper binding method for function: calValueArray_removeRange. 
		 * @param array
		 * @param fromIndex
		 * @param toIndex
		 * @return the SourceModule.expr representing an application of calValueArray_removeRange
		 */
		public static final SourceModel.Expr calValueArray_removeRange(SourceModel.Expr array, SourceModel.Expr fromIndex, SourceModel.Expr toIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.calValueArray_removeRange), array, fromIndex, toIndex});
		}

		/**
		 * @see #calValueArray_removeRange(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param fromIndex
		 * @param toIndex
		 * @return the SourceModel.Expr representing an application of calValueArray_removeRange
		 */
		public static final SourceModel.Expr calValueArray_removeRange(SourceModel.Expr array, int fromIndex, int toIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.calValueArray_removeRange), array, SourceModel.Expr.makeIntValue(fromIndex), SourceModel.Expr.makeIntValue(toIndex)});
		}

		/**
		 * Name binding for function: calValueArray_removeRange.
		 * @see #calValueArray_removeRange(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName calValueArray_removeRange = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"calValueArray_removeRange");

		/**
		 * Helper binding method for function: calValueArray_replicate. 
		 * @param nCopies
		 * @param valueToReplicate
		 * @return the SourceModule.expr representing an application of calValueArray_replicate
		 */
		public static final SourceModel.Expr calValueArray_replicate(SourceModel.Expr nCopies, SourceModel.Expr valueToReplicate) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.calValueArray_replicate), nCopies, valueToReplicate});
		}

		/**
		 * @see #calValueArray_replicate(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param nCopies
		 * @param valueToReplicate
		 * @return the SourceModel.Expr representing an application of calValueArray_replicate
		 */
		public static final SourceModel.Expr calValueArray_replicate(int nCopies, SourceModel.Expr valueToReplicate) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.calValueArray_replicate), SourceModel.Expr.makeIntValue(nCopies), valueToReplicate});
		}

		/**
		 * Name binding for function: calValueArray_replicate.
		 * @see #calValueArray_replicate(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName calValueArray_replicate = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"calValueArray_replicate");

		/**
		 * Helper binding method for function: calValueArray_reverse. 
		 * @param array
		 * @return the SourceModule.expr representing an application of calValueArray_reverse
		 */
		public static final SourceModel.Expr calValueArray_reverse(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.calValueArray_reverse), array});
		}

		/**
		 * Name binding for function: calValueArray_reverse.
		 * @see #calValueArray_reverse(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName calValueArray_reverse = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"calValueArray_reverse");

		/**
		 * Helper binding method for function: calValueArray_subArray. 
		 * @param array
		 * @param fromIndex
		 * @param toIndex
		 * @return the SourceModule.expr representing an application of calValueArray_subArray
		 */
		public static final SourceModel.Expr calValueArray_subArray(SourceModel.Expr array, SourceModel.Expr fromIndex, SourceModel.Expr toIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.calValueArray_subArray), array, fromIndex, toIndex});
		}

		/**
		 * @see #calValueArray_subArray(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param fromIndex
		 * @param toIndex
		 * @return the SourceModel.Expr representing an application of calValueArray_subArray
		 */
		public static final SourceModel.Expr calValueArray_subArray(SourceModel.Expr array, int fromIndex, int toIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.calValueArray_subArray), array, SourceModel.Expr.makeIntValue(fromIndex), SourceModel.Expr.makeIntValue(toIndex)});
		}

		/**
		 * Name binding for function: calValueArray_subArray.
		 * @see #calValueArray_subArray(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName calValueArray_subArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"calValueArray_subArray");

		/**
		 * Helper binding method for function: calValueArray_subscript. 
		 * @param array
		 * @param index
		 * @return the SourceModule.expr representing an application of calValueArray_subscript
		 */
		public static final SourceModel.Expr calValueArray_subscript(SourceModel.Expr array, SourceModel.Expr index) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.calValueArray_subscript), array, index});
		}

		/**
		 * @see #calValueArray_subscript(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param index
		 * @return the SourceModel.Expr representing an application of calValueArray_subscript
		 */
		public static final SourceModel.Expr calValueArray_subscript(SourceModel.Expr array, int index) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.calValueArray_subscript), array, SourceModel.Expr.makeIntValue(index)});
		}

		/**
		 * Name binding for function: calValueArray_subscript.
		 * @see #calValueArray_subscript(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName calValueArray_subscript = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"calValueArray_subscript");

		/**
		 * copies the container Java array of CAL values.
		 * @param array (CAL type: <code>Cal.Collections.ArrayPrimitives.JCalValueArray</code>)
		 * @return (CAL type: <code>Cal.Collections.ArrayPrimitives.JCalValueArray</code>) 
		 */
		public static final SourceModel.Expr calValueArray_toCalValueArray(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.calValueArray_toCalValueArray), array});
		}

		/**
		 * Name binding for function: calValueArray_toCalValueArray.
		 * @see #calValueArray_toCalValueArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName calValueArray_toCalValueArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"calValueArray_toCalValueArray");

		/**
		 * Helper binding method for function: calValueArray_toShowString. 
		 * @param array
		 * @return the SourceModule.expr representing an application of calValueArray_toShowString
		 */
		public static final SourceModel.Expr calValueArray_toShowString(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.calValueArray_toShowString), array});
		}

		/**
		 * Name binding for function: calValueArray_toShowString.
		 * @see #calValueArray_toShowString(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName calValueArray_toShowString = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"calValueArray_toShowString");

		/**
		 * Helper binding method for function: calValueArray_update. 
		 * @param array
		 * @param index
		 * @param newValue
		 * @return the SourceModule.expr representing an application of calValueArray_update
		 */
		public static final SourceModel.Expr calValueArray_update(SourceModel.Expr array, SourceModel.Expr index, SourceModel.Expr newValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.calValueArray_update), array, index, newValue});
		}

		/**
		 * @see #calValueArray_update(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param index
		 * @param newValue
		 * @return the SourceModel.Expr representing an application of calValueArray_update
		 */
		public static final SourceModel.Expr calValueArray_update(SourceModel.Expr array, int index, SourceModel.Expr newValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.calValueArray_update), array, SourceModel.Expr.makeIntValue(index), newValue});
		}

		/**
		 * Name binding for function: calValueArray_update.
		 * @see #calValueArray_update(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName calValueArray_update = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"calValueArray_update");

		/**
		 * Helper binding method for function: charArray_append. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of charArray_append
		 */
		public static final SourceModel.Expr charArray_append(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_append), array1, array2});
		}

		/**
		 * Name binding for function: charArray_append.
		 * @see #charArray_append(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName charArray_append = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"charArray_append");

		/**
		 * Helper binding method for function: charArray_array1. 
		 * @param value
		 * @return the SourceModule.expr representing an application of charArray_array1
		 */
		public static final SourceModel.Expr charArray_array1(SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_array1), value});
		}

		/**
		 * @see #charArray_array1(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value
		 * @return the SourceModel.Expr representing an application of charArray_array1
		 */
		public static final SourceModel.Expr charArray_array1(char value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_array1), SourceModel.Expr.makeCharValue(value)});
		}

		/**
		 * Name binding for function: charArray_array1.
		 * @see #charArray_array1(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName charArray_array1 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"charArray_array1");

		/**
		 * Helper binding method for function: charArray_array2. 
		 * @param value1
		 * @param value2
		 * @return the SourceModule.expr representing an application of charArray_array2
		 */
		public static final SourceModel.Expr charArray_array2(SourceModel.Expr value1, SourceModel.Expr value2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_array2), value1, value2});
		}

		/**
		 * @see #charArray_array2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value1
		 * @param value2
		 * @return the SourceModel.Expr representing an application of charArray_array2
		 */
		public static final SourceModel.Expr charArray_array2(char value1, char value2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_array2), SourceModel.Expr.makeCharValue(value1), SourceModel.Expr.makeCharValue(value2)});
		}

		/**
		 * Name binding for function: charArray_array2.
		 * @see #charArray_array2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName charArray_array2 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"charArray_array2");

		/**
		 * Helper binding method for function: charArray_array3. 
		 * @param value1
		 * @param value2
		 * @param value3
		 * @return the SourceModule.expr representing an application of charArray_array3
		 */
		public static final SourceModel.Expr charArray_array3(SourceModel.Expr value1, SourceModel.Expr value2, SourceModel.Expr value3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_array3), value1, value2, value3});
		}

		/**
		 * @see #charArray_array3(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value1
		 * @param value2
		 * @param value3
		 * @return the SourceModel.Expr representing an application of charArray_array3
		 */
		public static final SourceModel.Expr charArray_array3(char value1, char value2, char value3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_array3), SourceModel.Expr.makeCharValue(value1), SourceModel.Expr.makeCharValue(value2), SourceModel.Expr.makeCharValue(value3)});
		}

		/**
		 * Name binding for function: charArray_array3.
		 * @see #charArray_array3(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName charArray_array3 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"charArray_array3");

		/**
		 * Helper binding method for function: charArray_array4. 
		 * @param value1
		 * @param value2
		 * @param value3
		 * @param value4
		 * @return the SourceModule.expr representing an application of charArray_array4
		 */
		public static final SourceModel.Expr charArray_array4(SourceModel.Expr value1, SourceModel.Expr value2, SourceModel.Expr value3, SourceModel.Expr value4) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_array4), value1, value2, value3, value4});
		}

		/**
		 * @see #charArray_array4(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value1
		 * @param value2
		 * @param value3
		 * @param value4
		 * @return the SourceModel.Expr representing an application of charArray_array4
		 */
		public static final SourceModel.Expr charArray_array4(char value1, char value2, char value3, char value4) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_array4), SourceModel.Expr.makeCharValue(value1), SourceModel.Expr.makeCharValue(value2), SourceModel.Expr.makeCharValue(value3), SourceModel.Expr.makeCharValue(value4)});
		}

		/**
		 * Name binding for function: charArray_array4.
		 * @see #charArray_array4(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName charArray_array4 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"charArray_array4");

		/**
		 * Helper binding method for function: charArray_array5. 
		 * @param value1
		 * @param value2
		 * @param value3
		 * @param value4
		 * @param value5
		 * @return the SourceModule.expr representing an application of charArray_array5
		 */
		public static final SourceModel.Expr charArray_array5(SourceModel.Expr value1, SourceModel.Expr value2, SourceModel.Expr value3, SourceModel.Expr value4, SourceModel.Expr value5) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_array5), value1, value2, value3, value4, value5});
		}

		/**
		 * @see #charArray_array5(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value1
		 * @param value2
		 * @param value3
		 * @param value4
		 * @param value5
		 * @return the SourceModel.Expr representing an application of charArray_array5
		 */
		public static final SourceModel.Expr charArray_array5(char value1, char value2, char value3, char value4, char value5) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_array5), SourceModel.Expr.makeCharValue(value1), SourceModel.Expr.makeCharValue(value2), SourceModel.Expr.makeCharValue(value3), SourceModel.Expr.makeCharValue(value4), SourceModel.Expr.makeCharValue(value5)});
		}

		/**
		 * Name binding for function: charArray_array5.
		 * @see #charArray_array5(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName charArray_array5 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"charArray_array5");

		/**
		 * Helper binding method for function: charArray_array6. 
		 * @param value1
		 * @param value2
		 * @param value3
		 * @param value4
		 * @param value5
		 * @param value6
		 * @return the SourceModule.expr representing an application of charArray_array6
		 */
		public static final SourceModel.Expr charArray_array6(SourceModel.Expr value1, SourceModel.Expr value2, SourceModel.Expr value3, SourceModel.Expr value4, SourceModel.Expr value5, SourceModel.Expr value6) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_array6), value1, value2, value3, value4, value5, value6});
		}

		/**
		 * @see #charArray_array6(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value1
		 * @param value2
		 * @param value3
		 * @param value4
		 * @param value5
		 * @param value6
		 * @return the SourceModel.Expr representing an application of charArray_array6
		 */
		public static final SourceModel.Expr charArray_array6(char value1, char value2, char value3, char value4, char value5, char value6) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_array6), SourceModel.Expr.makeCharValue(value1), SourceModel.Expr.makeCharValue(value2), SourceModel.Expr.makeCharValue(value3), SourceModel.Expr.makeCharValue(value4), SourceModel.Expr.makeCharValue(value5), SourceModel.Expr.makeCharValue(value6)});
		}

		/**
		 * Name binding for function: charArray_array6.
		 * @see #charArray_array6(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName charArray_array6 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"charArray_array6");

		/**
		 * Helper binding method for function: charArray_array7. 
		 * @param value1
		 * @param value2
		 * @param value3
		 * @param value4
		 * @param value5
		 * @param value6
		 * @param value7
		 * @return the SourceModule.expr representing an application of charArray_array7
		 */
		public static final SourceModel.Expr charArray_array7(SourceModel.Expr value1, SourceModel.Expr value2, SourceModel.Expr value3, SourceModel.Expr value4, SourceModel.Expr value5, SourceModel.Expr value6, SourceModel.Expr value7) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_array7), value1, value2, value3, value4, value5, value6, value7});
		}

		/**
		 * @see #charArray_array7(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value1
		 * @param value2
		 * @param value3
		 * @param value4
		 * @param value5
		 * @param value6
		 * @param value7
		 * @return the SourceModel.Expr representing an application of charArray_array7
		 */
		public static final SourceModel.Expr charArray_array7(char value1, char value2, char value3, char value4, char value5, char value6, char value7) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_array7), SourceModel.Expr.makeCharValue(value1), SourceModel.Expr.makeCharValue(value2), SourceModel.Expr.makeCharValue(value3), SourceModel.Expr.makeCharValue(value4), SourceModel.Expr.makeCharValue(value5), SourceModel.Expr.makeCharValue(value6), SourceModel.Expr.makeCharValue(value7)});
		}

		/**
		 * Name binding for function: charArray_array7.
		 * @see #charArray_array7(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName charArray_array7 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"charArray_array7");

		/**
		 * Helper binding method for function: charArray_arrayToList. 
		 * @param array
		 * @return the SourceModule.expr representing an application of charArray_arrayToList
		 */
		public static final SourceModel.Expr charArray_arrayToList(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_arrayToList), array});
		}

		/**
		 * Name binding for function: charArray_arrayToList.
		 * @see #charArray_arrayToList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName charArray_arrayToList = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"charArray_arrayToList");

		/**
		 * Helper binding method for function: charArray_binarySearch. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of charArray_binarySearch
		 */
		public static final SourceModel.Expr charArray_binarySearch(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_binarySearch), arg_1, arg_2});
		}

		/**
		 * @see #charArray_binarySearch(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of charArray_binarySearch
		 */
		public static final SourceModel.Expr charArray_binarySearch(SourceModel.Expr arg_1, char arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_binarySearch), arg_1, SourceModel.Expr.makeCharValue(arg_2)});
		}

		/**
		 * Name binding for function: charArray_binarySearch.
		 * @see #charArray_binarySearch(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName charArray_binarySearch = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"charArray_binarySearch");

		/**
		 * Helper binding method for function: charArray_cloneReplacingNullArray. 
		 * @param array
		 * @return the SourceModule.expr representing an application of charArray_cloneReplacingNullArray
		 */
		public static final SourceModel.Expr charArray_cloneReplacingNullArray(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_cloneReplacingNullArray), array});
		}

		/**
		 * Name binding for function: charArray_cloneReplacingNullArray.
		 * @see #charArray_cloneReplacingNullArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName charArray_cloneReplacingNullArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"charArray_cloneReplacingNullArray");

		/**
		 * Helper binding method for function: charArray_compare. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of charArray_compare
		 */
		public static final SourceModel.Expr charArray_compare(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_compare), array1, array2});
		}

		/**
		 * Name binding for function: charArray_compare.
		 * @see #charArray_compare(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName charArray_compare = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"charArray_compare");

		/**
		 * Helper binding method for function: charArray_compareHelper. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of charArray_compareHelper
		 */
		public static final SourceModel.Expr charArray_compareHelper(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_compareHelper), array1, array2});
		}

		/**
		 * Name binding for function: charArray_compareHelper.
		 * @see #charArray_compareHelper(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName charArray_compareHelper = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"charArray_compareHelper");

		/**
		 * Helper binding method for function: charArray_concat. 
		 * @param list
		 * @return the SourceModule.expr representing an application of charArray_concat
		 */
		public static final SourceModel.Expr charArray_concat(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_concat), list});
		}

		/**
		 * Name binding for function: charArray_concat.
		 * @see #charArray_concat(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName charArray_concat = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"charArray_concat");

		/**
		 * Helper binding method for function: charArray_concatList. 
		 * @param list
		 * @return the SourceModule.expr representing an application of charArray_concatList
		 */
		public static final SourceModel.Expr charArray_concatList(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_concatList), list});
		}

		/**
		 * Name binding for function: charArray_concatList.
		 * @see #charArray_concatList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName charArray_concatList = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"charArray_concatList");

		/**
		 * Helper binding method for function: charArray_empty. 
		 * @return the SourceModule.expr representing an application of charArray_empty
		 */
		public static final SourceModel.Expr charArray_empty() {
			return SourceModel.Expr.Var.make(Functions.charArray_empty);
		}

		/**
		 * Name binding for function: charArray_empty.
		 * @see #charArray_empty()
		 */
		public static final QualifiedName charArray_empty = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"charArray_empty");

		/**
		 * Helper binding method for function: charArray_equals. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of charArray_equals
		 */
		public static final SourceModel.Expr charArray_equals(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_equals), array1, array2});
		}

		/**
		 * Name binding for function: charArray_equals.
		 * @see #charArray_equals(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName charArray_equals = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"charArray_equals");

		/**
		 * Converts a Java array of CAL <code>Cal.Core.Prelude.Char</code>s to a Java array of primitive Java chars.
		 * @param calValueArray (CAL type: <code>Cal.Collections.ArrayPrimitives.JCalValueArray</code>)
		 * @return (CAL type: <code>Cal.Collections.ArrayPrimitives.JCharArray</code>) 
		 */
		public static final SourceModel.Expr charArray_fromCalValueArray(SourceModel.Expr calValueArray) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_fromCalValueArray), calValueArray});
		}

		/**
		 * Name binding for function: charArray_fromCalValueArray.
		 * @see #charArray_fromCalValueArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName charArray_fromCalValueArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"charArray_fromCalValueArray");

		/**
		 * Helper binding method for function: charArray_fromString. 
		 * @param string
		 * @return the SourceModule.expr representing an application of charArray_fromString
		 */
		public static final SourceModel.Expr charArray_fromString(SourceModel.Expr string) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_fromString), string});
		}

		/**
		 * @see #charArray_fromString(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param string
		 * @return the SourceModel.Expr representing an application of charArray_fromString
		 */
		public static final SourceModel.Expr charArray_fromString(java.lang.String string) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_fromString), SourceModel.Expr.makeStringValue(string)});
		}

		/**
		 * Name binding for function: charArray_fromString.
		 * @see #charArray_fromString(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName charArray_fromString = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"charArray_fromString");

		/**
		 * Helper binding method for function: charArray_greaterThan. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of charArray_greaterThan
		 */
		public static final SourceModel.Expr charArray_greaterThan(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_greaterThan), array1, array2});
		}

		/**
		 * Name binding for function: charArray_greaterThan.
		 * @see #charArray_greaterThan(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName charArray_greaterThan = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"charArray_greaterThan");

		/**
		 * Helper binding method for function: charArray_greaterThanEquals. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of charArray_greaterThanEquals
		 */
		public static final SourceModel.Expr charArray_greaterThanEquals(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_greaterThanEquals), array1, array2});
		}

		/**
		 * Name binding for function: charArray_greaterThanEquals.
		 * @see #charArray_greaterThanEquals(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName charArray_greaterThanEquals = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"charArray_greaterThanEquals");

		/**
		 * Helper binding method for function: charArray_indexOf. 
		 * @param array
		 * @param element
		 * @return the SourceModule.expr representing an application of charArray_indexOf
		 */
		public static final SourceModel.Expr charArray_indexOf(SourceModel.Expr array, SourceModel.Expr element) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_indexOf), array, element});
		}

		/**
		 * @see #charArray_indexOf(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param element
		 * @return the SourceModel.Expr representing an application of charArray_indexOf
		 */
		public static final SourceModel.Expr charArray_indexOf(SourceModel.Expr array, char element) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_indexOf), array, SourceModel.Expr.makeCharValue(element)});
		}

		/**
		 * Name binding for function: charArray_indexOf.
		 * @see #charArray_indexOf(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName charArray_indexOf = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"charArray_indexOf");

		/**
		 * Helper binding method for function: charArray_indexOf2. 
		 * @param array
		 * @param element
		 * @param fromIndex
		 * @return the SourceModule.expr representing an application of charArray_indexOf2
		 */
		public static final SourceModel.Expr charArray_indexOf2(SourceModel.Expr array, SourceModel.Expr element, SourceModel.Expr fromIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_indexOf2), array, element, fromIndex});
		}

		/**
		 * @see #charArray_indexOf2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param element
		 * @param fromIndex
		 * @return the SourceModel.Expr representing an application of charArray_indexOf2
		 */
		public static final SourceModel.Expr charArray_indexOf2(SourceModel.Expr array, char element, int fromIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_indexOf2), array, SourceModel.Expr.makeCharValue(element), SourceModel.Expr.makeIntValue(fromIndex)});
		}

		/**
		 * Name binding for function: charArray_indexOf2.
		 * @see #charArray_indexOf2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName charArray_indexOf2 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"charArray_indexOf2");

		/**
		 * Helper binding method for function: charArray_isEmpty. 
		 * @param array
		 * @return the SourceModule.expr representing an application of charArray_isEmpty
		 */
		public static final SourceModel.Expr charArray_isEmpty(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_isEmpty), array});
		}

		/**
		 * Name binding for function: charArray_isEmpty.
		 * @see #charArray_isEmpty(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName charArray_isEmpty = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"charArray_isEmpty");

		/**
		 * Helper binding method for function: charArray_lastIndexOf. 
		 * @param array
		 * @param element
		 * @return the SourceModule.expr representing an application of charArray_lastIndexOf
		 */
		public static final SourceModel.Expr charArray_lastIndexOf(SourceModel.Expr array, SourceModel.Expr element) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_lastIndexOf), array, element});
		}

		/**
		 * @see #charArray_lastIndexOf(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param element
		 * @return the SourceModel.Expr representing an application of charArray_lastIndexOf
		 */
		public static final SourceModel.Expr charArray_lastIndexOf(SourceModel.Expr array, char element) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_lastIndexOf), array, SourceModel.Expr.makeCharValue(element)});
		}

		/**
		 * Name binding for function: charArray_lastIndexOf.
		 * @see #charArray_lastIndexOf(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName charArray_lastIndexOf = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"charArray_lastIndexOf");

		/**
		 * Helper binding method for function: charArray_lastIndexOf2. 
		 * @param array
		 * @param element
		 * @param fromIndex
		 * @return the SourceModule.expr representing an application of charArray_lastIndexOf2
		 */
		public static final SourceModel.Expr charArray_lastIndexOf2(SourceModel.Expr array, SourceModel.Expr element, SourceModel.Expr fromIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_lastIndexOf2), array, element, fromIndex});
		}

		/**
		 * @see #charArray_lastIndexOf2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param element
		 * @param fromIndex
		 * @return the SourceModel.Expr representing an application of charArray_lastIndexOf2
		 */
		public static final SourceModel.Expr charArray_lastIndexOf2(SourceModel.Expr array, char element, int fromIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_lastIndexOf2), array, SourceModel.Expr.makeCharValue(element), SourceModel.Expr.makeIntValue(fromIndex)});
		}

		/**
		 * Name binding for function: charArray_lastIndexOf2.
		 * @see #charArray_lastIndexOf2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName charArray_lastIndexOf2 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"charArray_lastIndexOf2");

		/**
		 * Helper binding method for function: charArray_length. 
		 * @param array
		 * @return the SourceModule.expr representing an application of charArray_length
		 */
		public static final SourceModel.Expr charArray_length(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_length), array});
		}

		/**
		 * Name binding for function: charArray_length.
		 * @see #charArray_length(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName charArray_length = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"charArray_length");

		/**
		 * Helper binding method for function: charArray_lessThan. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of charArray_lessThan
		 */
		public static final SourceModel.Expr charArray_lessThan(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_lessThan), array1, array2});
		}

		/**
		 * Name binding for function: charArray_lessThan.
		 * @see #charArray_lessThan(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName charArray_lessThan = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"charArray_lessThan");

		/**
		 * Helper binding method for function: charArray_lessThanEquals. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of charArray_lessThanEquals
		 */
		public static final SourceModel.Expr charArray_lessThanEquals(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_lessThanEquals), array1, array2});
		}

		/**
		 * Name binding for function: charArray_lessThanEquals.
		 * @see #charArray_lessThanEquals(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName charArray_lessThanEquals = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"charArray_lessThanEquals");

		/**
		 * Helper binding method for function: charArray_listToArray. 
		 * @param list
		 * @return the SourceModule.expr representing an application of charArray_listToArray
		 */
		public static final SourceModel.Expr charArray_listToArray(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_listToArray), list});
		}

		/**
		 * Name binding for function: charArray_listToArray.
		 * @see #charArray_listToArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName charArray_listToArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"charArray_listToArray");

		/**
		 * Helper binding method for function: charArray_listToArrayWithFirstElement. 
		 * @param firstElement
		 * @param list
		 * @return the SourceModule.expr representing an application of charArray_listToArrayWithFirstElement
		 */
		public static final SourceModel.Expr charArray_listToArrayWithFirstElement(SourceModel.Expr firstElement, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_listToArrayWithFirstElement), firstElement, list});
		}

		/**
		 * @see #charArray_listToArrayWithFirstElement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param firstElement
		 * @param list
		 * @return the SourceModel.Expr representing an application of charArray_listToArrayWithFirstElement
		 */
		public static final SourceModel.Expr charArray_listToArrayWithFirstElement(char firstElement, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_listToArrayWithFirstElement), SourceModel.Expr.makeCharValue(firstElement), list});
		}

		/**
		 * Name binding for function: charArray_listToArrayWithFirstElement.
		 * @see #charArray_listToArrayWithFirstElement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName charArray_listToArrayWithFirstElement = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"charArray_listToArrayWithFirstElement");

		/**
		 * Helper binding method for function: charArray_makeDefault. 
		 * @param size
		 * @return the SourceModule.expr representing an application of charArray_makeDefault
		 */
		public static final SourceModel.Expr charArray_makeDefault(SourceModel.Expr size) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_makeDefault), size});
		}

		/**
		 * @see #charArray_makeDefault(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param size
		 * @return the SourceModel.Expr representing an application of charArray_makeDefault
		 */
		public static final SourceModel.Expr charArray_makeDefault(int size) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_makeDefault), SourceModel.Expr.makeIntValue(size)});
		}

		/**
		 * Name binding for function: charArray_makeDefault.
		 * @see #charArray_makeDefault(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName charArray_makeDefault = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"charArray_makeDefault");

		/**
		 * Helper binding method for function: charArray_max. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of charArray_max
		 */
		public static final SourceModel.Expr charArray_max(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_max), array1, array2});
		}

		/**
		 * Name binding for function: charArray_max.
		 * @see #charArray_max(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName charArray_max = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"charArray_max");

		/**
		 * Helper binding method for function: charArray_min. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of charArray_min
		 */
		public static final SourceModel.Expr charArray_min(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_min), array1, array2});
		}

		/**
		 * Name binding for function: charArray_min.
		 * @see #charArray_min(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName charArray_min = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"charArray_min");

		/**
		 * Helper binding method for function: charArray_notEquals. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of charArray_notEquals
		 */
		public static final SourceModel.Expr charArray_notEquals(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_notEquals), array1, array2});
		}

		/**
		 * Name binding for function: charArray_notEquals.
		 * @see #charArray_notEquals(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName charArray_notEquals = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"charArray_notEquals");

		/**
		 * Helper binding method for function: charArray_removeRange. 
		 * @param array
		 * @param fromIndex
		 * @param toIndex
		 * @return the SourceModule.expr representing an application of charArray_removeRange
		 */
		public static final SourceModel.Expr charArray_removeRange(SourceModel.Expr array, SourceModel.Expr fromIndex, SourceModel.Expr toIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_removeRange), array, fromIndex, toIndex});
		}

		/**
		 * @see #charArray_removeRange(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param fromIndex
		 * @param toIndex
		 * @return the SourceModel.Expr representing an application of charArray_removeRange
		 */
		public static final SourceModel.Expr charArray_removeRange(SourceModel.Expr array, int fromIndex, int toIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_removeRange), array, SourceModel.Expr.makeIntValue(fromIndex), SourceModel.Expr.makeIntValue(toIndex)});
		}

		/**
		 * Name binding for function: charArray_removeRange.
		 * @see #charArray_removeRange(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName charArray_removeRange = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"charArray_removeRange");

		/**
		 * Helper binding method for function: charArray_replace. 
		 * @param array
		 * @param oldElementValue
		 * @param newElementValue
		 * @return the SourceModule.expr representing an application of charArray_replace
		 */
		public static final SourceModel.Expr charArray_replace(SourceModel.Expr array, SourceModel.Expr oldElementValue, SourceModel.Expr newElementValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_replace), array, oldElementValue, newElementValue});
		}

		/**
		 * @see #charArray_replace(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param oldElementValue
		 * @param newElementValue
		 * @return the SourceModel.Expr representing an application of charArray_replace
		 */
		public static final SourceModel.Expr charArray_replace(SourceModel.Expr array, char oldElementValue, char newElementValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_replace), array, SourceModel.Expr.makeCharValue(oldElementValue), SourceModel.Expr.makeCharValue(newElementValue)});
		}

		/**
		 * Name binding for function: charArray_replace.
		 * @see #charArray_replace(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName charArray_replace = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"charArray_replace");

		/**
		 * Helper binding method for function: charArray_replicate. 
		 * @param nCopies
		 * @param valueToReplicate
		 * @return the SourceModule.expr representing an application of charArray_replicate
		 */
		public static final SourceModel.Expr charArray_replicate(SourceModel.Expr nCopies, SourceModel.Expr valueToReplicate) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_replicate), nCopies, valueToReplicate});
		}

		/**
		 * @see #charArray_replicate(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param nCopies
		 * @param valueToReplicate
		 * @return the SourceModel.Expr representing an application of charArray_replicate
		 */
		public static final SourceModel.Expr charArray_replicate(int nCopies, char valueToReplicate) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_replicate), SourceModel.Expr.makeIntValue(nCopies), SourceModel.Expr.makeCharValue(valueToReplicate)});
		}

		/**
		 * Name binding for function: charArray_replicate.
		 * @see #charArray_replicate(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName charArray_replicate = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"charArray_replicate");

		/**
		 * Helper binding method for function: charArray_reverse. 
		 * @param array
		 * @return the SourceModule.expr representing an application of charArray_reverse
		 */
		public static final SourceModel.Expr charArray_reverse(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_reverse), array});
		}

		/**
		 * Name binding for function: charArray_reverse.
		 * @see #charArray_reverse(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName charArray_reverse = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"charArray_reverse");

		/**
		 * Helper binding method for function: charArray_sort. 
		 * @param array
		 * @return the SourceModule.expr representing an application of charArray_sort
		 */
		public static final SourceModel.Expr charArray_sort(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_sort), array});
		}

		/**
		 * Name binding for function: charArray_sort.
		 * @see #charArray_sort(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName charArray_sort = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"charArray_sort");

		/**
		 * Helper binding method for function: charArray_subArray. 
		 * @param array
		 * @param fromIndex
		 * @param toIndex
		 * @return the SourceModule.expr representing an application of charArray_subArray
		 */
		public static final SourceModel.Expr charArray_subArray(SourceModel.Expr array, SourceModel.Expr fromIndex, SourceModel.Expr toIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_subArray), array, fromIndex, toIndex});
		}

		/**
		 * @see #charArray_subArray(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param fromIndex
		 * @param toIndex
		 * @return the SourceModel.Expr representing an application of charArray_subArray
		 */
		public static final SourceModel.Expr charArray_subArray(SourceModel.Expr array, int fromIndex, int toIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_subArray), array, SourceModel.Expr.makeIntValue(fromIndex), SourceModel.Expr.makeIntValue(toIndex)});
		}

		/**
		 * Name binding for function: charArray_subArray.
		 * @see #charArray_subArray(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName charArray_subArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"charArray_subArray");

		/**
		 * Helper binding method for function: charArray_subscript. 
		 * @param array
		 * @param index
		 * @return the SourceModule.expr representing an application of charArray_subscript
		 */
		public static final SourceModel.Expr charArray_subscript(SourceModel.Expr array, SourceModel.Expr index) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_subscript), array, index});
		}

		/**
		 * @see #charArray_subscript(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param index
		 * @return the SourceModel.Expr representing an application of charArray_subscript
		 */
		public static final SourceModel.Expr charArray_subscript(SourceModel.Expr array, int index) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_subscript), array, SourceModel.Expr.makeIntValue(index)});
		}

		/**
		 * Name binding for function: charArray_subscript.
		 * @see #charArray_subscript(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName charArray_subscript = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"charArray_subscript");

		/**
		 * Converts a Java array of primitive Java chars to a Java array of CAL <code>Cal.Core.Prelude.Char</code>s.
		 * @param array (CAL type: <code>Cal.Collections.ArrayPrimitives.JCharArray</code>)
		 * @return (CAL type: <code>Cal.Collections.ArrayPrimitives.JCalValueArray</code>) 
		 */
		public static final SourceModel.Expr charArray_toCalValueArray(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_toCalValueArray), array});
		}

		/**
		 * Name binding for function: charArray_toCalValueArray.
		 * @see #charArray_toCalValueArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName charArray_toCalValueArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"charArray_toCalValueArray");

		/**
		 * Helper binding method for function: charArray_toShowString. 
		 * @param array
		 * @return the SourceModule.expr representing an application of charArray_toShowString
		 */
		public static final SourceModel.Expr charArray_toShowString(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_toShowString), array});
		}

		/**
		 * Name binding for function: charArray_toShowString.
		 * @see #charArray_toShowString(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName charArray_toShowString = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"charArray_toShowString");

		/**
		 * Helper binding method for function: charArray_toString. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of charArray_toString
		 */
		public static final SourceModel.Expr charArray_toString(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_toString), arg_1});
		}

		/**
		 * Name binding for function: charArray_toString.
		 * @see #charArray_toString(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName charArray_toString = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"charArray_toString");

		/**
		 * Helper binding method for function: charArray_update. 
		 * @param array
		 * @param index
		 * @param newValue
		 * @return the SourceModule.expr representing an application of charArray_update
		 */
		public static final SourceModel.Expr charArray_update(SourceModel.Expr array, SourceModel.Expr index, SourceModel.Expr newValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_update), array, index, newValue});
		}

		/**
		 * @see #charArray_update(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param index
		 * @param newValue
		 * @return the SourceModel.Expr representing an application of charArray_update
		 */
		public static final SourceModel.Expr charArray_update(SourceModel.Expr array, int index, char newValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charArray_update), array, SourceModel.Expr.makeIntValue(index), SourceModel.Expr.makeCharValue(newValue)});
		}

		/**
		 * Name binding for function: charArray_update.
		 * @see #charArray_update(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName charArray_update = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"charArray_update");

		/**
		 * Helper binding method for function: doubleArray_append. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of doubleArray_append
		 */
		public static final SourceModel.Expr doubleArray_append(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_append), array1, array2});
		}

		/**
		 * Name binding for function: doubleArray_append.
		 * @see #doubleArray_append(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleArray_append = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"doubleArray_append");

		/**
		 * Helper binding method for function: doubleArray_array1. 
		 * @param value
		 * @return the SourceModule.expr representing an application of doubleArray_array1
		 */
		public static final SourceModel.Expr doubleArray_array1(SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_array1), value});
		}

		/**
		 * @see #doubleArray_array1(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value
		 * @return the SourceModel.Expr representing an application of doubleArray_array1
		 */
		public static final SourceModel.Expr doubleArray_array1(double value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_array1), SourceModel.Expr.makeDoubleValue(value)});
		}

		/**
		 * Name binding for function: doubleArray_array1.
		 * @see #doubleArray_array1(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleArray_array1 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"doubleArray_array1");

		/**
		 * Helper binding method for function: doubleArray_array2. 
		 * @param value1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of doubleArray_array2
		 */
		public static final SourceModel.Expr doubleArray_array2(SourceModel.Expr value1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_array2), value1, arg_2});
		}

		/**
		 * @see #doubleArray_array2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of doubleArray_array2
		 */
		public static final SourceModel.Expr doubleArray_array2(double value1, double arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_array2), SourceModel.Expr.makeDoubleValue(value1), SourceModel.Expr.makeDoubleValue(arg_2)});
		}

		/**
		 * Name binding for function: doubleArray_array2.
		 * @see #doubleArray_array2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleArray_array2 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"doubleArray_array2");

		/**
		 * Helper binding method for function: doubleArray_array3. 
		 * @param value1
		 * @param arg_2
		 * @param value2
		 * @return the SourceModule.expr representing an application of doubleArray_array3
		 */
		public static final SourceModel.Expr doubleArray_array3(SourceModel.Expr value1, SourceModel.Expr arg_2, SourceModel.Expr value2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_array3), value1, arg_2, value2});
		}

		/**
		 * @see #doubleArray_array3(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value1
		 * @param arg_2
		 * @param value2
		 * @return the SourceModel.Expr representing an application of doubleArray_array3
		 */
		public static final SourceModel.Expr doubleArray_array3(double value1, double arg_2, double value2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_array3), SourceModel.Expr.makeDoubleValue(value1), SourceModel.Expr.makeDoubleValue(arg_2), SourceModel.Expr.makeDoubleValue(value2)});
		}

		/**
		 * Name binding for function: doubleArray_array3.
		 * @see #doubleArray_array3(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleArray_array3 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"doubleArray_array3");

		/**
		 * Helper binding method for function: doubleArray_array4. 
		 * @param value1
		 * @param arg_2
		 * @param value2
		 * @param arg_4
		 * @return the SourceModule.expr representing an application of doubleArray_array4
		 */
		public static final SourceModel.Expr doubleArray_array4(SourceModel.Expr value1, SourceModel.Expr arg_2, SourceModel.Expr value2, SourceModel.Expr arg_4) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_array4), value1, arg_2, value2, arg_4});
		}

		/**
		 * @see #doubleArray_array4(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value1
		 * @param arg_2
		 * @param value2
		 * @param arg_4
		 * @return the SourceModel.Expr representing an application of doubleArray_array4
		 */
		public static final SourceModel.Expr doubleArray_array4(double value1, double arg_2, double value2, double arg_4) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_array4), SourceModel.Expr.makeDoubleValue(value1), SourceModel.Expr.makeDoubleValue(arg_2), SourceModel.Expr.makeDoubleValue(value2), SourceModel.Expr.makeDoubleValue(arg_4)});
		}

		/**
		 * Name binding for function: doubleArray_array4.
		 * @see #doubleArray_array4(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleArray_array4 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"doubleArray_array4");

		/**
		 * Helper binding method for function: doubleArray_array5. 
		 * @param value1
		 * @param arg_2
		 * @param value2
		 * @param arg_4
		 * @param value3
		 * @return the SourceModule.expr representing an application of doubleArray_array5
		 */
		public static final SourceModel.Expr doubleArray_array5(SourceModel.Expr value1, SourceModel.Expr arg_2, SourceModel.Expr value2, SourceModel.Expr arg_4, SourceModel.Expr value3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_array5), value1, arg_2, value2, arg_4, value3});
		}

		/**
		 * @see #doubleArray_array5(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value1
		 * @param arg_2
		 * @param value2
		 * @param arg_4
		 * @param value3
		 * @return the SourceModel.Expr representing an application of doubleArray_array5
		 */
		public static final SourceModel.Expr doubleArray_array5(double value1, double arg_2, double value2, double arg_4, double value3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_array5), SourceModel.Expr.makeDoubleValue(value1), SourceModel.Expr.makeDoubleValue(arg_2), SourceModel.Expr.makeDoubleValue(value2), SourceModel.Expr.makeDoubleValue(arg_4), SourceModel.Expr.makeDoubleValue(value3)});
		}

		/**
		 * Name binding for function: doubleArray_array5.
		 * @see #doubleArray_array5(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleArray_array5 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"doubleArray_array5");

		/**
		 * Helper binding method for function: doubleArray_array6. 
		 * @param value1
		 * @param arg_2
		 * @param value2
		 * @param arg_4
		 * @param value3
		 * @param arg_6
		 * @return the SourceModule.expr representing an application of doubleArray_array6
		 */
		public static final SourceModel.Expr doubleArray_array6(SourceModel.Expr value1, SourceModel.Expr arg_2, SourceModel.Expr value2, SourceModel.Expr arg_4, SourceModel.Expr value3, SourceModel.Expr arg_6) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_array6), value1, arg_2, value2, arg_4, value3, arg_6});
		}

		/**
		 * @see #doubleArray_array6(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value1
		 * @param arg_2
		 * @param value2
		 * @param arg_4
		 * @param value3
		 * @param arg_6
		 * @return the SourceModel.Expr representing an application of doubleArray_array6
		 */
		public static final SourceModel.Expr doubleArray_array6(double value1, double arg_2, double value2, double arg_4, double value3, double arg_6) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_array6), SourceModel.Expr.makeDoubleValue(value1), SourceModel.Expr.makeDoubleValue(arg_2), SourceModel.Expr.makeDoubleValue(value2), SourceModel.Expr.makeDoubleValue(arg_4), SourceModel.Expr.makeDoubleValue(value3), SourceModel.Expr.makeDoubleValue(arg_6)});
		}

		/**
		 * Name binding for function: doubleArray_array6.
		 * @see #doubleArray_array6(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleArray_array6 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"doubleArray_array6");

		/**
		 * Helper binding method for function: doubleArray_array7. 
		 * @param value1
		 * @param arg_2
		 * @param value2
		 * @param arg_4
		 * @param value3
		 * @param arg_6
		 * @param value4
		 * @return the SourceModule.expr representing an application of doubleArray_array7
		 */
		public static final SourceModel.Expr doubleArray_array7(SourceModel.Expr value1, SourceModel.Expr arg_2, SourceModel.Expr value2, SourceModel.Expr arg_4, SourceModel.Expr value3, SourceModel.Expr arg_6, SourceModel.Expr value4) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_array7), value1, arg_2, value2, arg_4, value3, arg_6, value4});
		}

		/**
		 * @see #doubleArray_array7(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value1
		 * @param arg_2
		 * @param value2
		 * @param arg_4
		 * @param value3
		 * @param arg_6
		 * @param value4
		 * @return the SourceModel.Expr representing an application of doubleArray_array7
		 */
		public static final SourceModel.Expr doubleArray_array7(double value1, double arg_2, double value2, double arg_4, double value3, double arg_6, double value4) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_array7), SourceModel.Expr.makeDoubleValue(value1), SourceModel.Expr.makeDoubleValue(arg_2), SourceModel.Expr.makeDoubleValue(value2), SourceModel.Expr.makeDoubleValue(arg_4), SourceModel.Expr.makeDoubleValue(value3), SourceModel.Expr.makeDoubleValue(arg_6), SourceModel.Expr.makeDoubleValue(value4)});
		}

		/**
		 * Name binding for function: doubleArray_array7.
		 * @see #doubleArray_array7(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleArray_array7 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"doubleArray_array7");

		/**
		 * Helper binding method for function: doubleArray_arrayToList. 
		 * @param array
		 * @return the SourceModule.expr representing an application of doubleArray_arrayToList
		 */
		public static final SourceModel.Expr doubleArray_arrayToList(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_arrayToList), array});
		}

		/**
		 * Name binding for function: doubleArray_arrayToList.
		 * @see #doubleArray_arrayToList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleArray_arrayToList = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"doubleArray_arrayToList");

		/**
		 * Helper binding method for function: doubleArray_binarySearch. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of doubleArray_binarySearch
		 */
		public static final SourceModel.Expr doubleArray_binarySearch(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_binarySearch), arg_1, arg_2});
		}

		/**
		 * @see #doubleArray_binarySearch(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of doubleArray_binarySearch
		 */
		public static final SourceModel.Expr doubleArray_binarySearch(SourceModel.Expr arg_1, double arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_binarySearch), arg_1, SourceModel.Expr.makeDoubleValue(arg_2)});
		}

		/**
		 * Name binding for function: doubleArray_binarySearch.
		 * @see #doubleArray_binarySearch(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleArray_binarySearch = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"doubleArray_binarySearch");

		/**
		 * Helper binding method for function: doubleArray_cloneReplacingNullArray. 
		 * @param array
		 * @return the SourceModule.expr representing an application of doubleArray_cloneReplacingNullArray
		 */
		public static final SourceModel.Expr doubleArray_cloneReplacingNullArray(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_cloneReplacingNullArray), array});
		}

		/**
		 * Name binding for function: doubleArray_cloneReplacingNullArray.
		 * @see #doubleArray_cloneReplacingNullArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleArray_cloneReplacingNullArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"doubleArray_cloneReplacingNullArray");

		/**
		 * Helper binding method for function: doubleArray_compare. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of doubleArray_compare
		 */
		public static final SourceModel.Expr doubleArray_compare(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_compare), array1, array2});
		}

		/**
		 * Name binding for function: doubleArray_compare.
		 * @see #doubleArray_compare(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleArray_compare = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"doubleArray_compare");

		/**
		 * Helper binding method for function: doubleArray_compareHelper. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of doubleArray_compareHelper
		 */
		public static final SourceModel.Expr doubleArray_compareHelper(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_compareHelper), array1, array2});
		}

		/**
		 * Name binding for function: doubleArray_compareHelper.
		 * @see #doubleArray_compareHelper(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleArray_compareHelper = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"doubleArray_compareHelper");

		/**
		 * Helper binding method for function: doubleArray_concat. 
		 * @param list
		 * @return the SourceModule.expr representing an application of doubleArray_concat
		 */
		public static final SourceModel.Expr doubleArray_concat(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_concat), list});
		}

		/**
		 * Name binding for function: doubleArray_concat.
		 * @see #doubleArray_concat(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleArray_concat = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"doubleArray_concat");

		/**
		 * Helper binding method for function: doubleArray_concatList. 
		 * @param list
		 * @return the SourceModule.expr representing an application of doubleArray_concatList
		 */
		public static final SourceModel.Expr doubleArray_concatList(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_concatList), list});
		}

		/**
		 * Name binding for function: doubleArray_concatList.
		 * @see #doubleArray_concatList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleArray_concatList = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"doubleArray_concatList");

		/**
		 * Helper binding method for function: doubleArray_empty. 
		 * @return the SourceModule.expr representing an application of doubleArray_empty
		 */
		public static final SourceModel.Expr doubleArray_empty() {
			return SourceModel.Expr.Var.make(Functions.doubleArray_empty);
		}

		/**
		 * Name binding for function: doubleArray_empty.
		 * @see #doubleArray_empty()
		 */
		public static final QualifiedName doubleArray_empty = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"doubleArray_empty");

		/**
		 * Helper binding method for function: doubleArray_equals. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of doubleArray_equals
		 */
		public static final SourceModel.Expr doubleArray_equals(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_equals), array1, array2});
		}

		/**
		 * Name binding for function: doubleArray_equals.
		 * @see #doubleArray_equals(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleArray_equals = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"doubleArray_equals");

		/**
		 * Converts a Java array of CAL <code>Cal.Core.Prelude.Double</code>s to a Java array of primitive Java doubles.
		 * @param calValueArray (CAL type: <code>Cal.Collections.ArrayPrimitives.JCalValueArray</code>)
		 * @return (CAL type: <code>Cal.Collections.ArrayPrimitives.JDoubleArray</code>) 
		 */
		public static final SourceModel.Expr doubleArray_fromCalValueArray(SourceModel.Expr calValueArray) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_fromCalValueArray), calValueArray});
		}

		/**
		 * Name binding for function: doubleArray_fromCalValueArray.
		 * @see #doubleArray_fromCalValueArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleArray_fromCalValueArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"doubleArray_fromCalValueArray");

		/**
		 * Helper binding method for function: doubleArray_greaterThan. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of doubleArray_greaterThan
		 */
		public static final SourceModel.Expr doubleArray_greaterThan(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_greaterThan), array1, array2});
		}

		/**
		 * Name binding for function: doubleArray_greaterThan.
		 * @see #doubleArray_greaterThan(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleArray_greaterThan = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"doubleArray_greaterThan");

		/**
		 * Helper binding method for function: doubleArray_greaterThanEquals. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of doubleArray_greaterThanEquals
		 */
		public static final SourceModel.Expr doubleArray_greaterThanEquals(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_greaterThanEquals), array1, array2});
		}

		/**
		 * Name binding for function: doubleArray_greaterThanEquals.
		 * @see #doubleArray_greaterThanEquals(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleArray_greaterThanEquals = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"doubleArray_greaterThanEquals");

		/**
		 * Helper binding method for function: doubleArray_indexOf. 
		 * @param array
		 * @param element
		 * @return the SourceModule.expr representing an application of doubleArray_indexOf
		 */
		public static final SourceModel.Expr doubleArray_indexOf(SourceModel.Expr array, SourceModel.Expr element) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_indexOf), array, element});
		}

		/**
		 * @see #doubleArray_indexOf(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param element
		 * @return the SourceModel.Expr representing an application of doubleArray_indexOf
		 */
		public static final SourceModel.Expr doubleArray_indexOf(SourceModel.Expr array, double element) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_indexOf), array, SourceModel.Expr.makeDoubleValue(element)});
		}

		/**
		 * Name binding for function: doubleArray_indexOf.
		 * @see #doubleArray_indexOf(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleArray_indexOf = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"doubleArray_indexOf");

		/**
		 * Helper binding method for function: doubleArray_indexOf2. 
		 * @param array
		 * @param element
		 * @param arg_3
		 * @return the SourceModule.expr representing an application of doubleArray_indexOf2
		 */
		public static final SourceModel.Expr doubleArray_indexOf2(SourceModel.Expr array, SourceModel.Expr element, SourceModel.Expr arg_3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_indexOf2), array, element, arg_3});
		}

		/**
		 * @see #doubleArray_indexOf2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param element
		 * @param arg_3
		 * @return the SourceModel.Expr representing an application of doubleArray_indexOf2
		 */
		public static final SourceModel.Expr doubleArray_indexOf2(SourceModel.Expr array, double element, int arg_3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_indexOf2), array, SourceModel.Expr.makeDoubleValue(element), SourceModel.Expr.makeIntValue(arg_3)});
		}

		/**
		 * Name binding for function: doubleArray_indexOf2.
		 * @see #doubleArray_indexOf2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleArray_indexOf2 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"doubleArray_indexOf2");

		/**
		 * Helper binding method for function: doubleArray_isEmpty. 
		 * @param array
		 * @return the SourceModule.expr representing an application of doubleArray_isEmpty
		 */
		public static final SourceModel.Expr doubleArray_isEmpty(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_isEmpty), array});
		}

		/**
		 * Name binding for function: doubleArray_isEmpty.
		 * @see #doubleArray_isEmpty(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleArray_isEmpty = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"doubleArray_isEmpty");

		/**
		 * Helper binding method for function: doubleArray_lastIndexOf. 
		 * @param array
		 * @param element
		 * @return the SourceModule.expr representing an application of doubleArray_lastIndexOf
		 */
		public static final SourceModel.Expr doubleArray_lastIndexOf(SourceModel.Expr array, SourceModel.Expr element) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_lastIndexOf), array, element});
		}

		/**
		 * @see #doubleArray_lastIndexOf(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param element
		 * @return the SourceModel.Expr representing an application of doubleArray_lastIndexOf
		 */
		public static final SourceModel.Expr doubleArray_lastIndexOf(SourceModel.Expr array, double element) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_lastIndexOf), array, SourceModel.Expr.makeDoubleValue(element)});
		}

		/**
		 * Name binding for function: doubleArray_lastIndexOf.
		 * @see #doubleArray_lastIndexOf(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleArray_lastIndexOf = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"doubleArray_lastIndexOf");

		/**
		 * Helper binding method for function: doubleArray_lastIndexOf2. 
		 * @param array
		 * @param element
		 * @param arg_3
		 * @return the SourceModule.expr representing an application of doubleArray_lastIndexOf2
		 */
		public static final SourceModel.Expr doubleArray_lastIndexOf2(SourceModel.Expr array, SourceModel.Expr element, SourceModel.Expr arg_3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_lastIndexOf2), array, element, arg_3});
		}

		/**
		 * @see #doubleArray_lastIndexOf2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param element
		 * @param arg_3
		 * @return the SourceModel.Expr representing an application of doubleArray_lastIndexOf2
		 */
		public static final SourceModel.Expr doubleArray_lastIndexOf2(SourceModel.Expr array, double element, int arg_3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_lastIndexOf2), array, SourceModel.Expr.makeDoubleValue(element), SourceModel.Expr.makeIntValue(arg_3)});
		}

		/**
		 * Name binding for function: doubleArray_lastIndexOf2.
		 * @see #doubleArray_lastIndexOf2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleArray_lastIndexOf2 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"doubleArray_lastIndexOf2");

		/**
		 * Helper binding method for function: doubleArray_length. 
		 * @param array
		 * @return the SourceModule.expr representing an application of doubleArray_length
		 */
		public static final SourceModel.Expr doubleArray_length(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_length), array});
		}

		/**
		 * Name binding for function: doubleArray_length.
		 * @see #doubleArray_length(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleArray_length = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"doubleArray_length");

		/**
		 * Helper binding method for function: doubleArray_lessThan. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of doubleArray_lessThan
		 */
		public static final SourceModel.Expr doubleArray_lessThan(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_lessThan), array1, array2});
		}

		/**
		 * Name binding for function: doubleArray_lessThan.
		 * @see #doubleArray_lessThan(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleArray_lessThan = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"doubleArray_lessThan");

		/**
		 * Helper binding method for function: doubleArray_lessThanEquals. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of doubleArray_lessThanEquals
		 */
		public static final SourceModel.Expr doubleArray_lessThanEquals(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_lessThanEquals), array1, array2});
		}

		/**
		 * Name binding for function: doubleArray_lessThanEquals.
		 * @see #doubleArray_lessThanEquals(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleArray_lessThanEquals = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"doubleArray_lessThanEquals");

		/**
		 * Helper binding method for function: doubleArray_listToArray. 
		 * @param list
		 * @return the SourceModule.expr representing an application of doubleArray_listToArray
		 */
		public static final SourceModel.Expr doubleArray_listToArray(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_listToArray), list});
		}

		/**
		 * Name binding for function: doubleArray_listToArray.
		 * @see #doubleArray_listToArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleArray_listToArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"doubleArray_listToArray");

		/**
		 * Helper binding method for function: doubleArray_listToArrayWithFirstElement. 
		 * @param firstElement
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of doubleArray_listToArrayWithFirstElement
		 */
		public static final SourceModel.Expr doubleArray_listToArrayWithFirstElement(SourceModel.Expr firstElement, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_listToArrayWithFirstElement), firstElement, arg_2});
		}

		/**
		 * @see #doubleArray_listToArrayWithFirstElement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param firstElement
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of doubleArray_listToArrayWithFirstElement
		 */
		public static final SourceModel.Expr doubleArray_listToArrayWithFirstElement(double firstElement, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_listToArrayWithFirstElement), SourceModel.Expr.makeDoubleValue(firstElement), arg_2});
		}

		/**
		 * Name binding for function: doubleArray_listToArrayWithFirstElement.
		 * @see #doubleArray_listToArrayWithFirstElement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleArray_listToArrayWithFirstElement = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"doubleArray_listToArrayWithFirstElement");

		/**
		 * Helper binding method for function: doubleArray_makeDefault. 
		 * @param size
		 * @return the SourceModule.expr representing an application of doubleArray_makeDefault
		 */
		public static final SourceModel.Expr doubleArray_makeDefault(SourceModel.Expr size) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_makeDefault), size});
		}

		/**
		 * @see #doubleArray_makeDefault(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param size
		 * @return the SourceModel.Expr representing an application of doubleArray_makeDefault
		 */
		public static final SourceModel.Expr doubleArray_makeDefault(int size) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_makeDefault), SourceModel.Expr.makeIntValue(size)});
		}

		/**
		 * Name binding for function: doubleArray_makeDefault.
		 * @see #doubleArray_makeDefault(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleArray_makeDefault = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"doubleArray_makeDefault");

		/**
		 * Helper binding method for function: doubleArray_max. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of doubleArray_max
		 */
		public static final SourceModel.Expr doubleArray_max(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_max), array1, array2});
		}

		/**
		 * Name binding for function: doubleArray_max.
		 * @see #doubleArray_max(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleArray_max = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"doubleArray_max");

		/**
		 * Helper binding method for function: doubleArray_min. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of doubleArray_min
		 */
		public static final SourceModel.Expr doubleArray_min(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_min), array1, array2});
		}

		/**
		 * Name binding for function: doubleArray_min.
		 * @see #doubleArray_min(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleArray_min = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"doubleArray_min");

		/**
		 * Helper binding method for function: doubleArray_notEquals. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of doubleArray_notEquals
		 */
		public static final SourceModel.Expr doubleArray_notEquals(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_notEquals), array1, array2});
		}

		/**
		 * Name binding for function: doubleArray_notEquals.
		 * @see #doubleArray_notEquals(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleArray_notEquals = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"doubleArray_notEquals");

		/**
		 * Helper binding method for function: doubleArray_removeRange. 
		 * @param array
		 * @param fromIndex
		 * @param toIndex
		 * @return the SourceModule.expr representing an application of doubleArray_removeRange
		 */
		public static final SourceModel.Expr doubleArray_removeRange(SourceModel.Expr array, SourceModel.Expr fromIndex, SourceModel.Expr toIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_removeRange), array, fromIndex, toIndex});
		}

		/**
		 * @see #doubleArray_removeRange(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param fromIndex
		 * @param toIndex
		 * @return the SourceModel.Expr representing an application of doubleArray_removeRange
		 */
		public static final SourceModel.Expr doubleArray_removeRange(SourceModel.Expr array, int fromIndex, int toIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_removeRange), array, SourceModel.Expr.makeIntValue(fromIndex), SourceModel.Expr.makeIntValue(toIndex)});
		}

		/**
		 * Name binding for function: doubleArray_removeRange.
		 * @see #doubleArray_removeRange(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleArray_removeRange = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"doubleArray_removeRange");

		/**
		 * Helper binding method for function: doubleArray_replace. 
		 * @param array
		 * @param oldElementValue
		 * @param arg_3
		 * @return the SourceModule.expr representing an application of doubleArray_replace
		 */
		public static final SourceModel.Expr doubleArray_replace(SourceModel.Expr array, SourceModel.Expr oldElementValue, SourceModel.Expr arg_3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_replace), array, oldElementValue, arg_3});
		}

		/**
		 * @see #doubleArray_replace(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param oldElementValue
		 * @param arg_3
		 * @return the SourceModel.Expr representing an application of doubleArray_replace
		 */
		public static final SourceModel.Expr doubleArray_replace(SourceModel.Expr array, double oldElementValue, double arg_3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_replace), array, SourceModel.Expr.makeDoubleValue(oldElementValue), SourceModel.Expr.makeDoubleValue(arg_3)});
		}

		/**
		 * Name binding for function: doubleArray_replace.
		 * @see #doubleArray_replace(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleArray_replace = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"doubleArray_replace");

		/**
		 * Helper binding method for function: doubleArray_replicate. 
		 * @param nCopies
		 * @param valueToReplicate
		 * @return the SourceModule.expr representing an application of doubleArray_replicate
		 */
		public static final SourceModel.Expr doubleArray_replicate(SourceModel.Expr nCopies, SourceModel.Expr valueToReplicate) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_replicate), nCopies, valueToReplicate});
		}

		/**
		 * @see #doubleArray_replicate(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param nCopies
		 * @param valueToReplicate
		 * @return the SourceModel.Expr representing an application of doubleArray_replicate
		 */
		public static final SourceModel.Expr doubleArray_replicate(int nCopies, double valueToReplicate) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_replicate), SourceModel.Expr.makeIntValue(nCopies), SourceModel.Expr.makeDoubleValue(valueToReplicate)});
		}

		/**
		 * Name binding for function: doubleArray_replicate.
		 * @see #doubleArray_replicate(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleArray_replicate = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"doubleArray_replicate");

		/**
		 * Helper binding method for function: doubleArray_reverse. 
		 * @param array
		 * @return the SourceModule.expr representing an application of doubleArray_reverse
		 */
		public static final SourceModel.Expr doubleArray_reverse(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_reverse), array});
		}

		/**
		 * Name binding for function: doubleArray_reverse.
		 * @see #doubleArray_reverse(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleArray_reverse = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"doubleArray_reverse");

		/**
		 * Helper binding method for function: doubleArray_sort. 
		 * @param array
		 * @return the SourceModule.expr representing an application of doubleArray_sort
		 */
		public static final SourceModel.Expr doubleArray_sort(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_sort), array});
		}

		/**
		 * Name binding for function: doubleArray_sort.
		 * @see #doubleArray_sort(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleArray_sort = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"doubleArray_sort");

		/**
		 * Helper binding method for function: doubleArray_subArray. 
		 * @param array
		 * @param fromIndex
		 * @param toIndex
		 * @return the SourceModule.expr representing an application of doubleArray_subArray
		 */
		public static final SourceModel.Expr doubleArray_subArray(SourceModel.Expr array, SourceModel.Expr fromIndex, SourceModel.Expr toIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_subArray), array, fromIndex, toIndex});
		}

		/**
		 * @see #doubleArray_subArray(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param fromIndex
		 * @param toIndex
		 * @return the SourceModel.Expr representing an application of doubleArray_subArray
		 */
		public static final SourceModel.Expr doubleArray_subArray(SourceModel.Expr array, int fromIndex, int toIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_subArray), array, SourceModel.Expr.makeIntValue(fromIndex), SourceModel.Expr.makeIntValue(toIndex)});
		}

		/**
		 * Name binding for function: doubleArray_subArray.
		 * @see #doubleArray_subArray(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleArray_subArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"doubleArray_subArray");

		/**
		 * Helper binding method for function: doubleArray_subscript. 
		 * @param array
		 * @param index
		 * @return the SourceModule.expr representing an application of doubleArray_subscript
		 */
		public static final SourceModel.Expr doubleArray_subscript(SourceModel.Expr array, SourceModel.Expr index) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_subscript), array, index});
		}

		/**
		 * @see #doubleArray_subscript(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param index
		 * @return the SourceModel.Expr representing an application of doubleArray_subscript
		 */
		public static final SourceModel.Expr doubleArray_subscript(SourceModel.Expr array, int index) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_subscript), array, SourceModel.Expr.makeIntValue(index)});
		}

		/**
		 * Name binding for function: doubleArray_subscript.
		 * @see #doubleArray_subscript(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleArray_subscript = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"doubleArray_subscript");

		/**
		 * Converts a Java array of primitive Java doubles to a Java array of CAL <code>Cal.Core.Prelude.Double</code>s.
		 * @param array (CAL type: <code>Cal.Collections.ArrayPrimitives.JDoubleArray</code>)
		 * @return (CAL type: <code>Cal.Collections.ArrayPrimitives.JCalValueArray</code>) 
		 */
		public static final SourceModel.Expr doubleArray_toCalValueArray(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_toCalValueArray), array});
		}

		/**
		 * Name binding for function: doubleArray_toCalValueArray.
		 * @see #doubleArray_toCalValueArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleArray_toCalValueArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"doubleArray_toCalValueArray");

		/**
		 * Helper binding method for function: doubleArray_toShowString. 
		 * @param array
		 * @return the SourceModule.expr representing an application of doubleArray_toShowString
		 */
		public static final SourceModel.Expr doubleArray_toShowString(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_toShowString), array});
		}

		/**
		 * Name binding for function: doubleArray_toShowString.
		 * @see #doubleArray_toShowString(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleArray_toShowString = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"doubleArray_toShowString");

		/**
		 * Helper binding method for function: doubleArray_update. 
		 * @param array
		 * @param index
		 * @param newValue
		 * @return the SourceModule.expr representing an application of doubleArray_update
		 */
		public static final SourceModel.Expr doubleArray_update(SourceModel.Expr array, SourceModel.Expr index, SourceModel.Expr newValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_update), array, index, newValue});
		}

		/**
		 * @see #doubleArray_update(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param index
		 * @param newValue
		 * @return the SourceModel.Expr representing an application of doubleArray_update
		 */
		public static final SourceModel.Expr doubleArray_update(SourceModel.Expr array, int index, double newValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleArray_update), array, SourceModel.Expr.makeIntValue(index), SourceModel.Expr.makeDoubleValue(newValue)});
		}

		/**
		 * Name binding for function: doubleArray_update.
		 * @see #doubleArray_update(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleArray_update = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"doubleArray_update");

		/**
		 * Helper binding method for function: emptyArray_isRemoveRangeOK. 
		 * @param fromIndex
		 * @param toIndex
		 * @return the SourceModule.expr representing an application of emptyArray_isRemoveRangeOK
		 */
		public static final SourceModel.Expr emptyArray_isRemoveRangeOK(SourceModel.Expr fromIndex, SourceModel.Expr toIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.emptyArray_isRemoveRangeOK), fromIndex, toIndex});
		}

		/**
		 * @see #emptyArray_isRemoveRangeOK(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param fromIndex
		 * @param toIndex
		 * @return the SourceModel.Expr representing an application of emptyArray_isRemoveRangeOK
		 */
		public static final SourceModel.Expr emptyArray_isRemoveRangeOK(int fromIndex, int toIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.emptyArray_isRemoveRangeOK), SourceModel.Expr.makeIntValue(fromIndex), SourceModel.Expr.makeIntValue(toIndex)});
		}

		/**
		 * Name binding for function: emptyArray_isRemoveRangeOK.
		 * @see #emptyArray_isRemoveRangeOK(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName emptyArray_isRemoveRangeOK = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"emptyArray_isRemoveRangeOK");

		/**
		 * Helper binding method for function: emptyArray_isSubArrayOK. 
		 * @param fromIndex
		 * @param toIndex
		 * @return the SourceModule.expr representing an application of emptyArray_isSubArrayOK
		 */
		public static final SourceModel.Expr emptyArray_isSubArrayOK(SourceModel.Expr fromIndex, SourceModel.Expr toIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.emptyArray_isSubArrayOK), fromIndex, toIndex});
		}

		/**
		 * @see #emptyArray_isSubArrayOK(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param fromIndex
		 * @param toIndex
		 * @return the SourceModel.Expr representing an application of emptyArray_isSubArrayOK
		 */
		public static final SourceModel.Expr emptyArray_isSubArrayOK(int fromIndex, int toIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.emptyArray_isSubArrayOK), SourceModel.Expr.makeIntValue(fromIndex), SourceModel.Expr.makeIntValue(toIndex)});
		}

		/**
		 * Name binding for function: emptyArray_isSubArrayOK.
		 * @see #emptyArray_isSubArrayOK(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName emptyArray_isSubArrayOK = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"emptyArray_isSubArrayOK");

		/**
		 * Helper binding method for function: emptyArray_subscript. 
		 * @param index
		 * @return the SourceModule.expr representing an application of emptyArray_subscript
		 */
		public static final SourceModel.Expr emptyArray_subscript(SourceModel.Expr index) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.emptyArray_subscript), index});
		}

		/**
		 * @see #emptyArray_subscript(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param index
		 * @return the SourceModel.Expr representing an application of emptyArray_subscript
		 */
		public static final SourceModel.Expr emptyArray_subscript(int index) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.emptyArray_subscript), SourceModel.Expr.makeIntValue(index)});
		}

		/**
		 * Name binding for function: emptyArray_subscript.
		 * @see #emptyArray_subscript(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName emptyArray_subscript = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"emptyArray_subscript");

		/**
		 * Helper binding method for function: emptyArray_throwArrayIndexOutOfBoundsException. 
		 * @param index
		 * @return the SourceModule.expr representing an application of emptyArray_throwArrayIndexOutOfBoundsException
		 */
		public static final SourceModel.Expr emptyArray_throwArrayIndexOutOfBoundsException(SourceModel.Expr index) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.emptyArray_throwArrayIndexOutOfBoundsException), index});
		}

		/**
		 * @see #emptyArray_throwArrayIndexOutOfBoundsException(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param index
		 * @return the SourceModel.Expr representing an application of emptyArray_throwArrayIndexOutOfBoundsException
		 */
		public static final SourceModel.Expr emptyArray_throwArrayIndexOutOfBoundsException(int index) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.emptyArray_throwArrayIndexOutOfBoundsException), SourceModel.Expr.makeIntValue(index)});
		}

		/**
		 * Name binding for function: emptyArray_throwArrayIndexOutOfBoundsException.
		 * @see #emptyArray_throwArrayIndexOutOfBoundsException(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName emptyArray_throwArrayIndexOutOfBoundsException = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"emptyArray_throwArrayIndexOutOfBoundsException");

		/**
		 * Helper binding method for function: emptyArray_update. 
		 * @param index
		 * @param value
		 * @return the SourceModule.expr representing an application of emptyArray_update
		 */
		public static final SourceModel.Expr emptyArray_update(SourceModel.Expr index, SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.emptyArray_update), index, value});
		}

		/**
		 * @see #emptyArray_update(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param index
		 * @param value
		 * @return the SourceModel.Expr representing an application of emptyArray_update
		 */
		public static final SourceModel.Expr emptyArray_update(int index, SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.emptyArray_update), SourceModel.Expr.makeIntValue(index), value});
		}

		/**
		 * Name binding for function: emptyArray_update.
		 * @see #emptyArray_update(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName emptyArray_update = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"emptyArray_update");

		/**
		 * Helper binding method for function: floatArray_append. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of floatArray_append
		 */
		public static final SourceModel.Expr floatArray_append(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_append), array1, array2});
		}

		/**
		 * Name binding for function: floatArray_append.
		 * @see #floatArray_append(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName floatArray_append = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"floatArray_append");

		/**
		 * Helper binding method for function: floatArray_array1. 
		 * @param value
		 * @return the SourceModule.expr representing an application of floatArray_array1
		 */
		public static final SourceModel.Expr floatArray_array1(SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_array1), value});
		}

		/**
		 * @see #floatArray_array1(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value
		 * @return the SourceModel.Expr representing an application of floatArray_array1
		 */
		public static final SourceModel.Expr floatArray_array1(float value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_array1), SourceModel.Expr.makeFloatValue(value)});
		}

		/**
		 * Name binding for function: floatArray_array1.
		 * @see #floatArray_array1(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName floatArray_array1 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"floatArray_array1");

		/**
		 * Helper binding method for function: floatArray_array2. 
		 * @param value1
		 * @param value2
		 * @return the SourceModule.expr representing an application of floatArray_array2
		 */
		public static final SourceModel.Expr floatArray_array2(SourceModel.Expr value1, SourceModel.Expr value2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_array2), value1, value2});
		}

		/**
		 * @see #floatArray_array2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value1
		 * @param value2
		 * @return the SourceModel.Expr representing an application of floatArray_array2
		 */
		public static final SourceModel.Expr floatArray_array2(float value1, float value2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_array2), SourceModel.Expr.makeFloatValue(value1), SourceModel.Expr.makeFloatValue(value2)});
		}

		/**
		 * Name binding for function: floatArray_array2.
		 * @see #floatArray_array2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName floatArray_array2 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"floatArray_array2");

		/**
		 * Helper binding method for function: floatArray_array3. 
		 * @param value1
		 * @param value2
		 * @param value3
		 * @return the SourceModule.expr representing an application of floatArray_array3
		 */
		public static final SourceModel.Expr floatArray_array3(SourceModel.Expr value1, SourceModel.Expr value2, SourceModel.Expr value3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_array3), value1, value2, value3});
		}

		/**
		 * @see #floatArray_array3(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value1
		 * @param value2
		 * @param value3
		 * @return the SourceModel.Expr representing an application of floatArray_array3
		 */
		public static final SourceModel.Expr floatArray_array3(float value1, float value2, float value3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_array3), SourceModel.Expr.makeFloatValue(value1), SourceModel.Expr.makeFloatValue(value2), SourceModel.Expr.makeFloatValue(value3)});
		}

		/**
		 * Name binding for function: floatArray_array3.
		 * @see #floatArray_array3(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName floatArray_array3 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"floatArray_array3");

		/**
		 * Helper binding method for function: floatArray_array4. 
		 * @param value1
		 * @param value2
		 * @param value3
		 * @param value4
		 * @return the SourceModule.expr representing an application of floatArray_array4
		 */
		public static final SourceModel.Expr floatArray_array4(SourceModel.Expr value1, SourceModel.Expr value2, SourceModel.Expr value3, SourceModel.Expr value4) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_array4), value1, value2, value3, value4});
		}

		/**
		 * @see #floatArray_array4(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value1
		 * @param value2
		 * @param value3
		 * @param value4
		 * @return the SourceModel.Expr representing an application of floatArray_array4
		 */
		public static final SourceModel.Expr floatArray_array4(float value1, float value2, float value3, float value4) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_array4), SourceModel.Expr.makeFloatValue(value1), SourceModel.Expr.makeFloatValue(value2), SourceModel.Expr.makeFloatValue(value3), SourceModel.Expr.makeFloatValue(value4)});
		}

		/**
		 * Name binding for function: floatArray_array4.
		 * @see #floatArray_array4(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName floatArray_array4 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"floatArray_array4");

		/**
		 * Helper binding method for function: floatArray_array5. 
		 * @param value1
		 * @param value2
		 * @param value3
		 * @param value4
		 * @param value5
		 * @return the SourceModule.expr representing an application of floatArray_array5
		 */
		public static final SourceModel.Expr floatArray_array5(SourceModel.Expr value1, SourceModel.Expr value2, SourceModel.Expr value3, SourceModel.Expr value4, SourceModel.Expr value5) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_array5), value1, value2, value3, value4, value5});
		}

		/**
		 * @see #floatArray_array5(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value1
		 * @param value2
		 * @param value3
		 * @param value4
		 * @param value5
		 * @return the SourceModel.Expr representing an application of floatArray_array5
		 */
		public static final SourceModel.Expr floatArray_array5(float value1, float value2, float value3, float value4, float value5) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_array5), SourceModel.Expr.makeFloatValue(value1), SourceModel.Expr.makeFloatValue(value2), SourceModel.Expr.makeFloatValue(value3), SourceModel.Expr.makeFloatValue(value4), SourceModel.Expr.makeFloatValue(value5)});
		}

		/**
		 * Name binding for function: floatArray_array5.
		 * @see #floatArray_array5(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName floatArray_array5 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"floatArray_array5");

		/**
		 * Helper binding method for function: floatArray_array6. 
		 * @param value1
		 * @param value2
		 * @param value3
		 * @param value4
		 * @param value5
		 * @param value6
		 * @return the SourceModule.expr representing an application of floatArray_array6
		 */
		public static final SourceModel.Expr floatArray_array6(SourceModel.Expr value1, SourceModel.Expr value2, SourceModel.Expr value3, SourceModel.Expr value4, SourceModel.Expr value5, SourceModel.Expr value6) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_array6), value1, value2, value3, value4, value5, value6});
		}

		/**
		 * @see #floatArray_array6(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value1
		 * @param value2
		 * @param value3
		 * @param value4
		 * @param value5
		 * @param value6
		 * @return the SourceModel.Expr representing an application of floatArray_array6
		 */
		public static final SourceModel.Expr floatArray_array6(float value1, float value2, float value3, float value4, float value5, float value6) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_array6), SourceModel.Expr.makeFloatValue(value1), SourceModel.Expr.makeFloatValue(value2), SourceModel.Expr.makeFloatValue(value3), SourceModel.Expr.makeFloatValue(value4), SourceModel.Expr.makeFloatValue(value5), SourceModel.Expr.makeFloatValue(value6)});
		}

		/**
		 * Name binding for function: floatArray_array6.
		 * @see #floatArray_array6(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName floatArray_array6 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"floatArray_array6");

		/**
		 * Helper binding method for function: floatArray_array7. 
		 * @param value1
		 * @param value2
		 * @param value3
		 * @param value4
		 * @param value5
		 * @param value6
		 * @param value7
		 * @return the SourceModule.expr representing an application of floatArray_array7
		 */
		public static final SourceModel.Expr floatArray_array7(SourceModel.Expr value1, SourceModel.Expr value2, SourceModel.Expr value3, SourceModel.Expr value4, SourceModel.Expr value5, SourceModel.Expr value6, SourceModel.Expr value7) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_array7), value1, value2, value3, value4, value5, value6, value7});
		}

		/**
		 * @see #floatArray_array7(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value1
		 * @param value2
		 * @param value3
		 * @param value4
		 * @param value5
		 * @param value6
		 * @param value7
		 * @return the SourceModel.Expr representing an application of floatArray_array7
		 */
		public static final SourceModel.Expr floatArray_array7(float value1, float value2, float value3, float value4, float value5, float value6, float value7) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_array7), SourceModel.Expr.makeFloatValue(value1), SourceModel.Expr.makeFloatValue(value2), SourceModel.Expr.makeFloatValue(value3), SourceModel.Expr.makeFloatValue(value4), SourceModel.Expr.makeFloatValue(value5), SourceModel.Expr.makeFloatValue(value6), SourceModel.Expr.makeFloatValue(value7)});
		}

		/**
		 * Name binding for function: floatArray_array7.
		 * @see #floatArray_array7(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName floatArray_array7 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"floatArray_array7");

		/**
		 * Helper binding method for function: floatArray_arrayToList. 
		 * @param array
		 * @return the SourceModule.expr representing an application of floatArray_arrayToList
		 */
		public static final SourceModel.Expr floatArray_arrayToList(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_arrayToList), array});
		}

		/**
		 * Name binding for function: floatArray_arrayToList.
		 * @see #floatArray_arrayToList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName floatArray_arrayToList = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"floatArray_arrayToList");

		/**
		 * Helper binding method for function: floatArray_binarySearch. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of floatArray_binarySearch
		 */
		public static final SourceModel.Expr floatArray_binarySearch(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_binarySearch), arg_1, arg_2});
		}

		/**
		 * @see #floatArray_binarySearch(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of floatArray_binarySearch
		 */
		public static final SourceModel.Expr floatArray_binarySearch(SourceModel.Expr arg_1, float arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_binarySearch), arg_1, SourceModel.Expr.makeFloatValue(arg_2)});
		}

		/**
		 * Name binding for function: floatArray_binarySearch.
		 * @see #floatArray_binarySearch(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName floatArray_binarySearch = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"floatArray_binarySearch");

		/**
		 * Helper binding method for function: floatArray_cloneReplacingNullArray. 
		 * @param array
		 * @return the SourceModule.expr representing an application of floatArray_cloneReplacingNullArray
		 */
		public static final SourceModel.Expr floatArray_cloneReplacingNullArray(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_cloneReplacingNullArray), array});
		}

		/**
		 * Name binding for function: floatArray_cloneReplacingNullArray.
		 * @see #floatArray_cloneReplacingNullArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName floatArray_cloneReplacingNullArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"floatArray_cloneReplacingNullArray");

		/**
		 * Helper binding method for function: floatArray_compare. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of floatArray_compare
		 */
		public static final SourceModel.Expr floatArray_compare(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_compare), array1, array2});
		}

		/**
		 * Name binding for function: floatArray_compare.
		 * @see #floatArray_compare(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName floatArray_compare = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"floatArray_compare");

		/**
		 * Helper binding method for function: floatArray_compareHelper. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of floatArray_compareHelper
		 */
		public static final SourceModel.Expr floatArray_compareHelper(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_compareHelper), array1, array2});
		}

		/**
		 * Name binding for function: floatArray_compareHelper.
		 * @see #floatArray_compareHelper(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName floatArray_compareHelper = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"floatArray_compareHelper");

		/**
		 * Helper binding method for function: floatArray_concat. 
		 * @param list
		 * @return the SourceModule.expr representing an application of floatArray_concat
		 */
		public static final SourceModel.Expr floatArray_concat(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_concat), list});
		}

		/**
		 * Name binding for function: floatArray_concat.
		 * @see #floatArray_concat(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName floatArray_concat = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"floatArray_concat");

		/**
		 * Helper binding method for function: floatArray_concatList. 
		 * @param list
		 * @return the SourceModule.expr representing an application of floatArray_concatList
		 */
		public static final SourceModel.Expr floatArray_concatList(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_concatList), list});
		}

		/**
		 * Name binding for function: floatArray_concatList.
		 * @see #floatArray_concatList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName floatArray_concatList = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"floatArray_concatList");

		/**
		 * Helper binding method for function: floatArray_empty. 
		 * @return the SourceModule.expr representing an application of floatArray_empty
		 */
		public static final SourceModel.Expr floatArray_empty() {
			return SourceModel.Expr.Var.make(Functions.floatArray_empty);
		}

		/**
		 * Name binding for function: floatArray_empty.
		 * @see #floatArray_empty()
		 */
		public static final QualifiedName floatArray_empty = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"floatArray_empty");

		/**
		 * Helper binding method for function: floatArray_equals. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of floatArray_equals
		 */
		public static final SourceModel.Expr floatArray_equals(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_equals), array1, array2});
		}

		/**
		 * Name binding for function: floatArray_equals.
		 * @see #floatArray_equals(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName floatArray_equals = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"floatArray_equals");

		/**
		 * Converts a Java array of CAL <code>Cal.Core.Prelude.Float</code>s to a Java array of primitive Java floats.
		 * @param calValueArray (CAL type: <code>Cal.Collections.ArrayPrimitives.JCalValueArray</code>)
		 * @return (CAL type: <code>Cal.Collections.ArrayPrimitives.JFloatArray</code>) 
		 */
		public static final SourceModel.Expr floatArray_fromCalValueArray(SourceModel.Expr calValueArray) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_fromCalValueArray), calValueArray});
		}

		/**
		 * Name binding for function: floatArray_fromCalValueArray.
		 * @see #floatArray_fromCalValueArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName floatArray_fromCalValueArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"floatArray_fromCalValueArray");

		/**
		 * Helper binding method for function: floatArray_greaterThan. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of floatArray_greaterThan
		 */
		public static final SourceModel.Expr floatArray_greaterThan(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_greaterThan), array1, array2});
		}

		/**
		 * Name binding for function: floatArray_greaterThan.
		 * @see #floatArray_greaterThan(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName floatArray_greaterThan = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"floatArray_greaterThan");

		/**
		 * Helper binding method for function: floatArray_greaterThanEquals. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of floatArray_greaterThanEquals
		 */
		public static final SourceModel.Expr floatArray_greaterThanEquals(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_greaterThanEquals), array1, array2});
		}

		/**
		 * Name binding for function: floatArray_greaterThanEquals.
		 * @see #floatArray_greaterThanEquals(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName floatArray_greaterThanEquals = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"floatArray_greaterThanEquals");

		/**
		 * Helper binding method for function: floatArray_indexOf. 
		 * @param array
		 * @param element
		 * @return the SourceModule.expr representing an application of floatArray_indexOf
		 */
		public static final SourceModel.Expr floatArray_indexOf(SourceModel.Expr array, SourceModel.Expr element) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_indexOf), array, element});
		}

		/**
		 * @see #floatArray_indexOf(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param element
		 * @return the SourceModel.Expr representing an application of floatArray_indexOf
		 */
		public static final SourceModel.Expr floatArray_indexOf(SourceModel.Expr array, float element) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_indexOf), array, SourceModel.Expr.makeFloatValue(element)});
		}

		/**
		 * Name binding for function: floatArray_indexOf.
		 * @see #floatArray_indexOf(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName floatArray_indexOf = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"floatArray_indexOf");

		/**
		 * Helper binding method for function: floatArray_indexOf2. 
		 * @param array
		 * @param element
		 * @param fromIndex
		 * @return the SourceModule.expr representing an application of floatArray_indexOf2
		 */
		public static final SourceModel.Expr floatArray_indexOf2(SourceModel.Expr array, SourceModel.Expr element, SourceModel.Expr fromIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_indexOf2), array, element, fromIndex});
		}

		/**
		 * @see #floatArray_indexOf2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param element
		 * @param fromIndex
		 * @return the SourceModel.Expr representing an application of floatArray_indexOf2
		 */
		public static final SourceModel.Expr floatArray_indexOf2(SourceModel.Expr array, float element, int fromIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_indexOf2), array, SourceModel.Expr.makeFloatValue(element), SourceModel.Expr.makeIntValue(fromIndex)});
		}

		/**
		 * Name binding for function: floatArray_indexOf2.
		 * @see #floatArray_indexOf2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName floatArray_indexOf2 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"floatArray_indexOf2");

		/**
		 * Helper binding method for function: floatArray_isEmpty. 
		 * @param array
		 * @return the SourceModule.expr representing an application of floatArray_isEmpty
		 */
		public static final SourceModel.Expr floatArray_isEmpty(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_isEmpty), array});
		}

		/**
		 * Name binding for function: floatArray_isEmpty.
		 * @see #floatArray_isEmpty(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName floatArray_isEmpty = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"floatArray_isEmpty");

		/**
		 * Helper binding method for function: floatArray_lastIndexOf. 
		 * @param array
		 * @param element
		 * @return the SourceModule.expr representing an application of floatArray_lastIndexOf
		 */
		public static final SourceModel.Expr floatArray_lastIndexOf(SourceModel.Expr array, SourceModel.Expr element) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_lastIndexOf), array, element});
		}

		/**
		 * @see #floatArray_lastIndexOf(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param element
		 * @return the SourceModel.Expr representing an application of floatArray_lastIndexOf
		 */
		public static final SourceModel.Expr floatArray_lastIndexOf(SourceModel.Expr array, float element) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_lastIndexOf), array, SourceModel.Expr.makeFloatValue(element)});
		}

		/**
		 * Name binding for function: floatArray_lastIndexOf.
		 * @see #floatArray_lastIndexOf(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName floatArray_lastIndexOf = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"floatArray_lastIndexOf");

		/**
		 * Helper binding method for function: floatArray_lastIndexOf2. 
		 * @param array
		 * @param element
		 * @param fromIndex
		 * @return the SourceModule.expr representing an application of floatArray_lastIndexOf2
		 */
		public static final SourceModel.Expr floatArray_lastIndexOf2(SourceModel.Expr array, SourceModel.Expr element, SourceModel.Expr fromIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_lastIndexOf2), array, element, fromIndex});
		}

		/**
		 * @see #floatArray_lastIndexOf2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param element
		 * @param fromIndex
		 * @return the SourceModel.Expr representing an application of floatArray_lastIndexOf2
		 */
		public static final SourceModel.Expr floatArray_lastIndexOf2(SourceModel.Expr array, float element, int fromIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_lastIndexOf2), array, SourceModel.Expr.makeFloatValue(element), SourceModel.Expr.makeIntValue(fromIndex)});
		}

		/**
		 * Name binding for function: floatArray_lastIndexOf2.
		 * @see #floatArray_lastIndexOf2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName floatArray_lastIndexOf2 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"floatArray_lastIndexOf2");

		/**
		 * Helper binding method for function: floatArray_length. 
		 * @param array
		 * @return the SourceModule.expr representing an application of floatArray_length
		 */
		public static final SourceModel.Expr floatArray_length(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_length), array});
		}

		/**
		 * Name binding for function: floatArray_length.
		 * @see #floatArray_length(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName floatArray_length = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"floatArray_length");

		/**
		 * Helper binding method for function: floatArray_lessThan. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of floatArray_lessThan
		 */
		public static final SourceModel.Expr floatArray_lessThan(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_lessThan), array1, array2});
		}

		/**
		 * Name binding for function: floatArray_lessThan.
		 * @see #floatArray_lessThan(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName floatArray_lessThan = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"floatArray_lessThan");

		/**
		 * Helper binding method for function: floatArray_lessThanEquals. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of floatArray_lessThanEquals
		 */
		public static final SourceModel.Expr floatArray_lessThanEquals(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_lessThanEquals), array1, array2});
		}

		/**
		 * Name binding for function: floatArray_lessThanEquals.
		 * @see #floatArray_lessThanEquals(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName floatArray_lessThanEquals = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"floatArray_lessThanEquals");

		/**
		 * Helper binding method for function: floatArray_listToArray. 
		 * @param list
		 * @return the SourceModule.expr representing an application of floatArray_listToArray
		 */
		public static final SourceModel.Expr floatArray_listToArray(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_listToArray), list});
		}

		/**
		 * Name binding for function: floatArray_listToArray.
		 * @see #floatArray_listToArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName floatArray_listToArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"floatArray_listToArray");

		/**
		 * Helper binding method for function: floatArray_listToArrayWithFirstElement. 
		 * @param firstElement
		 * @param list
		 * @return the SourceModule.expr representing an application of floatArray_listToArrayWithFirstElement
		 */
		public static final SourceModel.Expr floatArray_listToArrayWithFirstElement(SourceModel.Expr firstElement, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_listToArrayWithFirstElement), firstElement, list});
		}

		/**
		 * @see #floatArray_listToArrayWithFirstElement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param firstElement
		 * @param list
		 * @return the SourceModel.Expr representing an application of floatArray_listToArrayWithFirstElement
		 */
		public static final SourceModel.Expr floatArray_listToArrayWithFirstElement(float firstElement, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_listToArrayWithFirstElement), SourceModel.Expr.makeFloatValue(firstElement), list});
		}

		/**
		 * Name binding for function: floatArray_listToArrayWithFirstElement.
		 * @see #floatArray_listToArrayWithFirstElement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName floatArray_listToArrayWithFirstElement = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"floatArray_listToArrayWithFirstElement");

		/**
		 * Helper binding method for function: floatArray_makeDefault. 
		 * @param size
		 * @return the SourceModule.expr representing an application of floatArray_makeDefault
		 */
		public static final SourceModel.Expr floatArray_makeDefault(SourceModel.Expr size) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_makeDefault), size});
		}

		/**
		 * @see #floatArray_makeDefault(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param size
		 * @return the SourceModel.Expr representing an application of floatArray_makeDefault
		 */
		public static final SourceModel.Expr floatArray_makeDefault(int size) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_makeDefault), SourceModel.Expr.makeIntValue(size)});
		}

		/**
		 * Name binding for function: floatArray_makeDefault.
		 * @see #floatArray_makeDefault(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName floatArray_makeDefault = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"floatArray_makeDefault");

		/**
		 * Helper binding method for function: floatArray_max. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of floatArray_max
		 */
		public static final SourceModel.Expr floatArray_max(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_max), array1, array2});
		}

		/**
		 * Name binding for function: floatArray_max.
		 * @see #floatArray_max(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName floatArray_max = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"floatArray_max");

		/**
		 * Helper binding method for function: floatArray_min. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of floatArray_min
		 */
		public static final SourceModel.Expr floatArray_min(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_min), array1, array2});
		}

		/**
		 * Name binding for function: floatArray_min.
		 * @see #floatArray_min(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName floatArray_min = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"floatArray_min");

		/**
		 * Helper binding method for function: floatArray_notEquals. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of floatArray_notEquals
		 */
		public static final SourceModel.Expr floatArray_notEquals(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_notEquals), array1, array2});
		}

		/**
		 * Name binding for function: floatArray_notEquals.
		 * @see #floatArray_notEquals(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName floatArray_notEquals = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"floatArray_notEquals");

		/**
		 * Helper binding method for function: floatArray_removeRange. 
		 * @param array
		 * @param fromIndex
		 * @param toIndex
		 * @return the SourceModule.expr representing an application of floatArray_removeRange
		 */
		public static final SourceModel.Expr floatArray_removeRange(SourceModel.Expr array, SourceModel.Expr fromIndex, SourceModel.Expr toIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_removeRange), array, fromIndex, toIndex});
		}

		/**
		 * @see #floatArray_removeRange(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param fromIndex
		 * @param toIndex
		 * @return the SourceModel.Expr representing an application of floatArray_removeRange
		 */
		public static final SourceModel.Expr floatArray_removeRange(SourceModel.Expr array, int fromIndex, int toIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_removeRange), array, SourceModel.Expr.makeIntValue(fromIndex), SourceModel.Expr.makeIntValue(toIndex)});
		}

		/**
		 * Name binding for function: floatArray_removeRange.
		 * @see #floatArray_removeRange(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName floatArray_removeRange = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"floatArray_removeRange");

		/**
		 * Helper binding method for function: floatArray_replace. 
		 * @param array
		 * @param oldElementValue
		 * @param newElementValue
		 * @return the SourceModule.expr representing an application of floatArray_replace
		 */
		public static final SourceModel.Expr floatArray_replace(SourceModel.Expr array, SourceModel.Expr oldElementValue, SourceModel.Expr newElementValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_replace), array, oldElementValue, newElementValue});
		}

		/**
		 * @see #floatArray_replace(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param oldElementValue
		 * @param newElementValue
		 * @return the SourceModel.Expr representing an application of floatArray_replace
		 */
		public static final SourceModel.Expr floatArray_replace(SourceModel.Expr array, float oldElementValue, float newElementValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_replace), array, SourceModel.Expr.makeFloatValue(oldElementValue), SourceModel.Expr.makeFloatValue(newElementValue)});
		}

		/**
		 * Name binding for function: floatArray_replace.
		 * @see #floatArray_replace(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName floatArray_replace = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"floatArray_replace");

		/**
		 * Helper binding method for function: floatArray_replicate. 
		 * @param nCopies
		 * @param valueToReplicate
		 * @return the SourceModule.expr representing an application of floatArray_replicate
		 */
		public static final SourceModel.Expr floatArray_replicate(SourceModel.Expr nCopies, SourceModel.Expr valueToReplicate) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_replicate), nCopies, valueToReplicate});
		}

		/**
		 * @see #floatArray_replicate(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param nCopies
		 * @param valueToReplicate
		 * @return the SourceModel.Expr representing an application of floatArray_replicate
		 */
		public static final SourceModel.Expr floatArray_replicate(int nCopies, float valueToReplicate) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_replicate), SourceModel.Expr.makeIntValue(nCopies), SourceModel.Expr.makeFloatValue(valueToReplicate)});
		}

		/**
		 * Name binding for function: floatArray_replicate.
		 * @see #floatArray_replicate(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName floatArray_replicate = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"floatArray_replicate");

		/**
		 * Helper binding method for function: floatArray_reverse. 
		 * @param array
		 * @return the SourceModule.expr representing an application of floatArray_reverse
		 */
		public static final SourceModel.Expr floatArray_reverse(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_reverse), array});
		}

		/**
		 * Name binding for function: floatArray_reverse.
		 * @see #floatArray_reverse(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName floatArray_reverse = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"floatArray_reverse");

		/**
		 * Helper binding method for function: floatArray_sort. 
		 * @param array
		 * @return the SourceModule.expr representing an application of floatArray_sort
		 */
		public static final SourceModel.Expr floatArray_sort(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_sort), array});
		}

		/**
		 * Name binding for function: floatArray_sort.
		 * @see #floatArray_sort(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName floatArray_sort = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"floatArray_sort");

		/**
		 * Helper binding method for function: floatArray_subArray. 
		 * @param array
		 * @param fromIndex
		 * @param toIndex
		 * @return the SourceModule.expr representing an application of floatArray_subArray
		 */
		public static final SourceModel.Expr floatArray_subArray(SourceModel.Expr array, SourceModel.Expr fromIndex, SourceModel.Expr toIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_subArray), array, fromIndex, toIndex});
		}

		/**
		 * @see #floatArray_subArray(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param fromIndex
		 * @param toIndex
		 * @return the SourceModel.Expr representing an application of floatArray_subArray
		 */
		public static final SourceModel.Expr floatArray_subArray(SourceModel.Expr array, int fromIndex, int toIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_subArray), array, SourceModel.Expr.makeIntValue(fromIndex), SourceModel.Expr.makeIntValue(toIndex)});
		}

		/**
		 * Name binding for function: floatArray_subArray.
		 * @see #floatArray_subArray(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName floatArray_subArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"floatArray_subArray");

		/**
		 * Helper binding method for function: floatArray_subscript. 
		 * @param array
		 * @param index
		 * @return the SourceModule.expr representing an application of floatArray_subscript
		 */
		public static final SourceModel.Expr floatArray_subscript(SourceModel.Expr array, SourceModel.Expr index) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_subscript), array, index});
		}

		/**
		 * @see #floatArray_subscript(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param index
		 * @return the SourceModel.Expr representing an application of floatArray_subscript
		 */
		public static final SourceModel.Expr floatArray_subscript(SourceModel.Expr array, int index) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_subscript), array, SourceModel.Expr.makeIntValue(index)});
		}

		/**
		 * Name binding for function: floatArray_subscript.
		 * @see #floatArray_subscript(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName floatArray_subscript = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"floatArray_subscript");

		/**
		 * Converts a Java array of primitive Java floats to a Java array of CAL <code>Cal.Core.Prelude.Float</code>s.
		 * @param array (CAL type: <code>Cal.Collections.ArrayPrimitives.JFloatArray</code>)
		 * @return (CAL type: <code>Cal.Collections.ArrayPrimitives.JCalValueArray</code>) 
		 */
		public static final SourceModel.Expr floatArray_toCalValueArray(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_toCalValueArray), array});
		}

		/**
		 * Name binding for function: floatArray_toCalValueArray.
		 * @see #floatArray_toCalValueArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName floatArray_toCalValueArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"floatArray_toCalValueArray");

		/**
		 * Helper binding method for function: floatArray_toShowString. 
		 * @param array
		 * @return the SourceModule.expr representing an application of floatArray_toShowString
		 */
		public static final SourceModel.Expr floatArray_toShowString(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_toShowString), array});
		}

		/**
		 * Name binding for function: floatArray_toShowString.
		 * @see #floatArray_toShowString(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName floatArray_toShowString = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"floatArray_toShowString");

		/**
		 * Helper binding method for function: floatArray_update. 
		 * @param array
		 * @param index
		 * @param newValue
		 * @return the SourceModule.expr representing an application of floatArray_update
		 */
		public static final SourceModel.Expr floatArray_update(SourceModel.Expr array, SourceModel.Expr index, SourceModel.Expr newValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_update), array, index, newValue});
		}

		/**
		 * @see #floatArray_update(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param index
		 * @param newValue
		 * @return the SourceModel.Expr representing an application of floatArray_update
		 */
		public static final SourceModel.Expr floatArray_update(SourceModel.Expr array, int index, float newValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatArray_update), array, SourceModel.Expr.makeIntValue(index), SourceModel.Expr.makeFloatValue(newValue)});
		}

		/**
		 * Name binding for function: floatArray_update.
		 * @see #floatArray_update(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName floatArray_update = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"floatArray_update");

		/**
		 * Helper binding method for function: intArray_append. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of intArray_append
		 */
		public static final SourceModel.Expr intArray_append(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_append), array1, array2});
		}

		/**
		 * Name binding for function: intArray_append.
		 * @see #intArray_append(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intArray_append = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"intArray_append");

		/**
		 * Helper binding method for function: intArray_array1. 
		 * @param value
		 * @return the SourceModule.expr representing an application of intArray_array1
		 */
		public static final SourceModel.Expr intArray_array1(SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_array1), value});
		}

		/**
		 * @see #intArray_array1(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value
		 * @return the SourceModel.Expr representing an application of intArray_array1
		 */
		public static final SourceModel.Expr intArray_array1(int value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_array1), SourceModel.Expr.makeIntValue(value)});
		}

		/**
		 * Name binding for function: intArray_array1.
		 * @see #intArray_array1(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intArray_array1 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"intArray_array1");

		/**
		 * Helper binding method for function: intArray_array2. 
		 * @param value1
		 * @param value2
		 * @return the SourceModule.expr representing an application of intArray_array2
		 */
		public static final SourceModel.Expr intArray_array2(SourceModel.Expr value1, SourceModel.Expr value2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_array2), value1, value2});
		}

		/**
		 * @see #intArray_array2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value1
		 * @param value2
		 * @return the SourceModel.Expr representing an application of intArray_array2
		 */
		public static final SourceModel.Expr intArray_array2(int value1, int value2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_array2), SourceModel.Expr.makeIntValue(value1), SourceModel.Expr.makeIntValue(value2)});
		}

		/**
		 * Name binding for function: intArray_array2.
		 * @see #intArray_array2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intArray_array2 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"intArray_array2");

		/**
		 * Helper binding method for function: intArray_array3. 
		 * @param value1
		 * @param value2
		 * @param value3
		 * @return the SourceModule.expr representing an application of intArray_array3
		 */
		public static final SourceModel.Expr intArray_array3(SourceModel.Expr value1, SourceModel.Expr value2, SourceModel.Expr value3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_array3), value1, value2, value3});
		}

		/**
		 * @see #intArray_array3(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value1
		 * @param value2
		 * @param value3
		 * @return the SourceModel.Expr representing an application of intArray_array3
		 */
		public static final SourceModel.Expr intArray_array3(int value1, int value2, int value3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_array3), SourceModel.Expr.makeIntValue(value1), SourceModel.Expr.makeIntValue(value2), SourceModel.Expr.makeIntValue(value3)});
		}

		/**
		 * Name binding for function: intArray_array3.
		 * @see #intArray_array3(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intArray_array3 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"intArray_array3");

		/**
		 * Helper binding method for function: intArray_array4. 
		 * @param value1
		 * @param value2
		 * @param value3
		 * @param value4
		 * @return the SourceModule.expr representing an application of intArray_array4
		 */
		public static final SourceModel.Expr intArray_array4(SourceModel.Expr value1, SourceModel.Expr value2, SourceModel.Expr value3, SourceModel.Expr value4) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_array4), value1, value2, value3, value4});
		}

		/**
		 * @see #intArray_array4(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value1
		 * @param value2
		 * @param value3
		 * @param value4
		 * @return the SourceModel.Expr representing an application of intArray_array4
		 */
		public static final SourceModel.Expr intArray_array4(int value1, int value2, int value3, int value4) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_array4), SourceModel.Expr.makeIntValue(value1), SourceModel.Expr.makeIntValue(value2), SourceModel.Expr.makeIntValue(value3), SourceModel.Expr.makeIntValue(value4)});
		}

		/**
		 * Name binding for function: intArray_array4.
		 * @see #intArray_array4(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intArray_array4 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"intArray_array4");

		/**
		 * Helper binding method for function: intArray_array5. 
		 * @param value1
		 * @param value2
		 * @param value3
		 * @param value4
		 * @param value5
		 * @return the SourceModule.expr representing an application of intArray_array5
		 */
		public static final SourceModel.Expr intArray_array5(SourceModel.Expr value1, SourceModel.Expr value2, SourceModel.Expr value3, SourceModel.Expr value4, SourceModel.Expr value5) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_array5), value1, value2, value3, value4, value5});
		}

		/**
		 * @see #intArray_array5(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value1
		 * @param value2
		 * @param value3
		 * @param value4
		 * @param value5
		 * @return the SourceModel.Expr representing an application of intArray_array5
		 */
		public static final SourceModel.Expr intArray_array5(int value1, int value2, int value3, int value4, int value5) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_array5), SourceModel.Expr.makeIntValue(value1), SourceModel.Expr.makeIntValue(value2), SourceModel.Expr.makeIntValue(value3), SourceModel.Expr.makeIntValue(value4), SourceModel.Expr.makeIntValue(value5)});
		}

		/**
		 * Name binding for function: intArray_array5.
		 * @see #intArray_array5(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intArray_array5 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"intArray_array5");

		/**
		 * Helper binding method for function: intArray_array6. 
		 * @param value1
		 * @param value2
		 * @param value3
		 * @param value4
		 * @param value5
		 * @param value6
		 * @return the SourceModule.expr representing an application of intArray_array6
		 */
		public static final SourceModel.Expr intArray_array6(SourceModel.Expr value1, SourceModel.Expr value2, SourceModel.Expr value3, SourceModel.Expr value4, SourceModel.Expr value5, SourceModel.Expr value6) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_array6), value1, value2, value3, value4, value5, value6});
		}

		/**
		 * @see #intArray_array6(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value1
		 * @param value2
		 * @param value3
		 * @param value4
		 * @param value5
		 * @param value6
		 * @return the SourceModel.Expr representing an application of intArray_array6
		 */
		public static final SourceModel.Expr intArray_array6(int value1, int value2, int value3, int value4, int value5, int value6) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_array6), SourceModel.Expr.makeIntValue(value1), SourceModel.Expr.makeIntValue(value2), SourceModel.Expr.makeIntValue(value3), SourceModel.Expr.makeIntValue(value4), SourceModel.Expr.makeIntValue(value5), SourceModel.Expr.makeIntValue(value6)});
		}

		/**
		 * Name binding for function: intArray_array6.
		 * @see #intArray_array6(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intArray_array6 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"intArray_array6");

		/**
		 * Helper binding method for function: intArray_array7. 
		 * @param value1
		 * @param value2
		 * @param value3
		 * @param value4
		 * @param value5
		 * @param value6
		 * @param value7
		 * @return the SourceModule.expr representing an application of intArray_array7
		 */
		public static final SourceModel.Expr intArray_array7(SourceModel.Expr value1, SourceModel.Expr value2, SourceModel.Expr value3, SourceModel.Expr value4, SourceModel.Expr value5, SourceModel.Expr value6, SourceModel.Expr value7) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_array7), value1, value2, value3, value4, value5, value6, value7});
		}

		/**
		 * @see #intArray_array7(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value1
		 * @param value2
		 * @param value3
		 * @param value4
		 * @param value5
		 * @param value6
		 * @param value7
		 * @return the SourceModel.Expr representing an application of intArray_array7
		 */
		public static final SourceModel.Expr intArray_array7(int value1, int value2, int value3, int value4, int value5, int value6, int value7) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_array7), SourceModel.Expr.makeIntValue(value1), SourceModel.Expr.makeIntValue(value2), SourceModel.Expr.makeIntValue(value3), SourceModel.Expr.makeIntValue(value4), SourceModel.Expr.makeIntValue(value5), SourceModel.Expr.makeIntValue(value6), SourceModel.Expr.makeIntValue(value7)});
		}

		/**
		 * Name binding for function: intArray_array7.
		 * @see #intArray_array7(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intArray_array7 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"intArray_array7");

		/**
		 * Helper binding method for function: intArray_arrayToList. 
		 * @param array
		 * @return the SourceModule.expr representing an application of intArray_arrayToList
		 */
		public static final SourceModel.Expr intArray_arrayToList(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_arrayToList), array});
		}

		/**
		 * Name binding for function: intArray_arrayToList.
		 * @see #intArray_arrayToList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intArray_arrayToList = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"intArray_arrayToList");

		/**
		 * Helper binding method for function: intArray_binarySearch. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of intArray_binarySearch
		 */
		public static final SourceModel.Expr intArray_binarySearch(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_binarySearch), arg_1, arg_2});
		}

		/**
		 * @see #intArray_binarySearch(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of intArray_binarySearch
		 */
		public static final SourceModel.Expr intArray_binarySearch(SourceModel.Expr arg_1, int arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_binarySearch), arg_1, SourceModel.Expr.makeIntValue(arg_2)});
		}

		/**
		 * Name binding for function: intArray_binarySearch.
		 * @see #intArray_binarySearch(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intArray_binarySearch = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"intArray_binarySearch");

		/**
		 * Helper binding method for function: intArray_cloneReplacingNullArray. 
		 * @param array
		 * @return the SourceModule.expr representing an application of intArray_cloneReplacingNullArray
		 */
		public static final SourceModel.Expr intArray_cloneReplacingNullArray(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_cloneReplacingNullArray), array});
		}

		/**
		 * Name binding for function: intArray_cloneReplacingNullArray.
		 * @see #intArray_cloneReplacingNullArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intArray_cloneReplacingNullArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"intArray_cloneReplacingNullArray");

		/**
		 * Helper binding method for function: intArray_compare. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of intArray_compare
		 */
		public static final SourceModel.Expr intArray_compare(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_compare), array1, array2});
		}

		/**
		 * Name binding for function: intArray_compare.
		 * @see #intArray_compare(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intArray_compare = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"intArray_compare");

		/**
		 * Helper binding method for function: intArray_compareHelper. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of intArray_compareHelper
		 */
		public static final SourceModel.Expr intArray_compareHelper(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_compareHelper), array1, array2});
		}

		/**
		 * Name binding for function: intArray_compareHelper.
		 * @see #intArray_compareHelper(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intArray_compareHelper = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"intArray_compareHelper");

		/**
		 * Helper binding method for function: intArray_concat. 
		 * @param list
		 * @return the SourceModule.expr representing an application of intArray_concat
		 */
		public static final SourceModel.Expr intArray_concat(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_concat), list});
		}

		/**
		 * Name binding for function: intArray_concat.
		 * @see #intArray_concat(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intArray_concat = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"intArray_concat");

		/**
		 * Helper binding method for function: intArray_concatList. 
		 * @param list
		 * @return the SourceModule.expr representing an application of intArray_concatList
		 */
		public static final SourceModel.Expr intArray_concatList(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_concatList), list});
		}

		/**
		 * Name binding for function: intArray_concatList.
		 * @see #intArray_concatList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intArray_concatList = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"intArray_concatList");

		/**
		 * Helper binding method for function: intArray_empty. 
		 * @return the SourceModule.expr representing an application of intArray_empty
		 */
		public static final SourceModel.Expr intArray_empty() {
			return SourceModel.Expr.Var.make(Functions.intArray_empty);
		}

		/**
		 * Name binding for function: intArray_empty.
		 * @see #intArray_empty()
		 */
		public static final QualifiedName intArray_empty = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"intArray_empty");

		/**
		 * Helper binding method for function: intArray_equals. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of intArray_equals
		 */
		public static final SourceModel.Expr intArray_equals(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_equals), array1, array2});
		}

		/**
		 * Name binding for function: intArray_equals.
		 * @see #intArray_equals(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intArray_equals = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"intArray_equals");

		/**
		 * Converts a Java array of CAL <code>Cal.Core.Prelude.Int</code>s to a Java array of primitive Java ints.
		 * @param calValueArray (CAL type: <code>Cal.Collections.ArrayPrimitives.JCalValueArray</code>)
		 * @return (CAL type: <code>Cal.Collections.ArrayPrimitives.JIntArray</code>) 
		 */
		public static final SourceModel.Expr intArray_fromCalValueArray(SourceModel.Expr calValueArray) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_fromCalValueArray), calValueArray});
		}

		/**
		 * Name binding for function: intArray_fromCalValueArray.
		 * @see #intArray_fromCalValueArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intArray_fromCalValueArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"intArray_fromCalValueArray");

		/**
		 * Helper binding method for function: intArray_greaterThan. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of intArray_greaterThan
		 */
		public static final SourceModel.Expr intArray_greaterThan(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_greaterThan), array1, array2});
		}

		/**
		 * Name binding for function: intArray_greaterThan.
		 * @see #intArray_greaterThan(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intArray_greaterThan = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"intArray_greaterThan");

		/**
		 * Helper binding method for function: intArray_greaterThanEquals. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of intArray_greaterThanEquals
		 */
		public static final SourceModel.Expr intArray_greaterThanEquals(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_greaterThanEquals), array1, array2});
		}

		/**
		 * Name binding for function: intArray_greaterThanEquals.
		 * @see #intArray_greaterThanEquals(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intArray_greaterThanEquals = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"intArray_greaterThanEquals");

		/**
		 * Helper binding method for function: intArray_indexOf. 
		 * @param array
		 * @param element
		 * @return the SourceModule.expr representing an application of intArray_indexOf
		 */
		public static final SourceModel.Expr intArray_indexOf(SourceModel.Expr array, SourceModel.Expr element) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_indexOf), array, element});
		}

		/**
		 * @see #intArray_indexOf(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param element
		 * @return the SourceModel.Expr representing an application of intArray_indexOf
		 */
		public static final SourceModel.Expr intArray_indexOf(SourceModel.Expr array, int element) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_indexOf), array, SourceModel.Expr.makeIntValue(element)});
		}

		/**
		 * Name binding for function: intArray_indexOf.
		 * @see #intArray_indexOf(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intArray_indexOf = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"intArray_indexOf");

		/**
		 * Helper binding method for function: intArray_indexOf2. 
		 * @param array
		 * @param element
		 * @param fromIndex
		 * @return the SourceModule.expr representing an application of intArray_indexOf2
		 */
		public static final SourceModel.Expr intArray_indexOf2(SourceModel.Expr array, SourceModel.Expr element, SourceModel.Expr fromIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_indexOf2), array, element, fromIndex});
		}

		/**
		 * @see #intArray_indexOf2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param element
		 * @param fromIndex
		 * @return the SourceModel.Expr representing an application of intArray_indexOf2
		 */
		public static final SourceModel.Expr intArray_indexOf2(SourceModel.Expr array, int element, int fromIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_indexOf2), array, SourceModel.Expr.makeIntValue(element), SourceModel.Expr.makeIntValue(fromIndex)});
		}

		/**
		 * Name binding for function: intArray_indexOf2.
		 * @see #intArray_indexOf2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intArray_indexOf2 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"intArray_indexOf2");

		/**
		 * Helper binding method for function: intArray_isEmpty. 
		 * @param array
		 * @return the SourceModule.expr representing an application of intArray_isEmpty
		 */
		public static final SourceModel.Expr intArray_isEmpty(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_isEmpty), array});
		}

		/**
		 * Name binding for function: intArray_isEmpty.
		 * @see #intArray_isEmpty(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intArray_isEmpty = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"intArray_isEmpty");

		/**
		 * Helper binding method for function: intArray_lastIndexOf. 
		 * @param array
		 * @param element
		 * @return the SourceModule.expr representing an application of intArray_lastIndexOf
		 */
		public static final SourceModel.Expr intArray_lastIndexOf(SourceModel.Expr array, SourceModel.Expr element) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_lastIndexOf), array, element});
		}

		/**
		 * @see #intArray_lastIndexOf(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param element
		 * @return the SourceModel.Expr representing an application of intArray_lastIndexOf
		 */
		public static final SourceModel.Expr intArray_lastIndexOf(SourceModel.Expr array, int element) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_lastIndexOf), array, SourceModel.Expr.makeIntValue(element)});
		}

		/**
		 * Name binding for function: intArray_lastIndexOf.
		 * @see #intArray_lastIndexOf(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intArray_lastIndexOf = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"intArray_lastIndexOf");

		/**
		 * Helper binding method for function: intArray_lastIndexOf2. 
		 * @param array
		 * @param element
		 * @param fromIndex
		 * @return the SourceModule.expr representing an application of intArray_lastIndexOf2
		 */
		public static final SourceModel.Expr intArray_lastIndexOf2(SourceModel.Expr array, SourceModel.Expr element, SourceModel.Expr fromIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_lastIndexOf2), array, element, fromIndex});
		}

		/**
		 * @see #intArray_lastIndexOf2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param element
		 * @param fromIndex
		 * @return the SourceModel.Expr representing an application of intArray_lastIndexOf2
		 */
		public static final SourceModel.Expr intArray_lastIndexOf2(SourceModel.Expr array, int element, int fromIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_lastIndexOf2), array, SourceModel.Expr.makeIntValue(element), SourceModel.Expr.makeIntValue(fromIndex)});
		}

		/**
		 * Name binding for function: intArray_lastIndexOf2.
		 * @see #intArray_lastIndexOf2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intArray_lastIndexOf2 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"intArray_lastIndexOf2");

		/**
		 * Helper binding method for function: intArray_length. 
		 * @param array
		 * @return the SourceModule.expr representing an application of intArray_length
		 */
		public static final SourceModel.Expr intArray_length(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_length), array});
		}

		/**
		 * Name binding for function: intArray_length.
		 * @see #intArray_length(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intArray_length = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"intArray_length");

		/**
		 * Helper binding method for function: intArray_lessThan. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of intArray_lessThan
		 */
		public static final SourceModel.Expr intArray_lessThan(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_lessThan), array1, array2});
		}

		/**
		 * Name binding for function: intArray_lessThan.
		 * @see #intArray_lessThan(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intArray_lessThan = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"intArray_lessThan");

		/**
		 * Helper binding method for function: intArray_lessThanEquals. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of intArray_lessThanEquals
		 */
		public static final SourceModel.Expr intArray_lessThanEquals(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_lessThanEquals), array1, array2});
		}

		/**
		 * Name binding for function: intArray_lessThanEquals.
		 * @see #intArray_lessThanEquals(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intArray_lessThanEquals = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"intArray_lessThanEquals");

		/**
		 * Helper binding method for function: intArray_listToArray. 
		 * @param list
		 * @return the SourceModule.expr representing an application of intArray_listToArray
		 */
		public static final SourceModel.Expr intArray_listToArray(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_listToArray), list});
		}

		/**
		 * Name binding for function: intArray_listToArray.
		 * @see #intArray_listToArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intArray_listToArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"intArray_listToArray");

		/**
		 * Helper binding method for function: intArray_listToArrayWithFirstElement. 
		 * @param firstElement
		 * @param list
		 * @return the SourceModule.expr representing an application of intArray_listToArrayWithFirstElement
		 */
		public static final SourceModel.Expr intArray_listToArrayWithFirstElement(SourceModel.Expr firstElement, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_listToArrayWithFirstElement), firstElement, list});
		}

		/**
		 * @see #intArray_listToArrayWithFirstElement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param firstElement
		 * @param list
		 * @return the SourceModel.Expr representing an application of intArray_listToArrayWithFirstElement
		 */
		public static final SourceModel.Expr intArray_listToArrayWithFirstElement(int firstElement, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_listToArrayWithFirstElement), SourceModel.Expr.makeIntValue(firstElement), list});
		}

		/**
		 * Name binding for function: intArray_listToArrayWithFirstElement.
		 * @see #intArray_listToArrayWithFirstElement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intArray_listToArrayWithFirstElement = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"intArray_listToArrayWithFirstElement");

		/**
		 * Helper binding method for function: intArray_makeDefault. 
		 * @param size
		 * @return the SourceModule.expr representing an application of intArray_makeDefault
		 */
		public static final SourceModel.Expr intArray_makeDefault(SourceModel.Expr size) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_makeDefault), size});
		}

		/**
		 * @see #intArray_makeDefault(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param size
		 * @return the SourceModel.Expr representing an application of intArray_makeDefault
		 */
		public static final SourceModel.Expr intArray_makeDefault(int size) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_makeDefault), SourceModel.Expr.makeIntValue(size)});
		}

		/**
		 * Name binding for function: intArray_makeDefault.
		 * @see #intArray_makeDefault(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intArray_makeDefault = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"intArray_makeDefault");

		/**
		 * Helper binding method for function: intArray_max. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of intArray_max
		 */
		public static final SourceModel.Expr intArray_max(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_max), array1, array2});
		}

		/**
		 * Name binding for function: intArray_max.
		 * @see #intArray_max(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intArray_max = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"intArray_max");

		/**
		 * Helper binding method for function: intArray_min. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of intArray_min
		 */
		public static final SourceModel.Expr intArray_min(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_min), array1, array2});
		}

		/**
		 * Name binding for function: intArray_min.
		 * @see #intArray_min(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intArray_min = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"intArray_min");

		/**
		 * Helper binding method for function: intArray_notEquals. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of intArray_notEquals
		 */
		public static final SourceModel.Expr intArray_notEquals(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_notEquals), array1, array2});
		}

		/**
		 * Name binding for function: intArray_notEquals.
		 * @see #intArray_notEquals(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intArray_notEquals = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"intArray_notEquals");

		/**
		 * Helper binding method for function: intArray_removeRange. 
		 * @param array
		 * @param fromIndex
		 * @param toIndex
		 * @return the SourceModule.expr representing an application of intArray_removeRange
		 */
		public static final SourceModel.Expr intArray_removeRange(SourceModel.Expr array, SourceModel.Expr fromIndex, SourceModel.Expr toIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_removeRange), array, fromIndex, toIndex});
		}

		/**
		 * @see #intArray_removeRange(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param fromIndex
		 * @param toIndex
		 * @return the SourceModel.Expr representing an application of intArray_removeRange
		 */
		public static final SourceModel.Expr intArray_removeRange(SourceModel.Expr array, int fromIndex, int toIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_removeRange), array, SourceModel.Expr.makeIntValue(fromIndex), SourceModel.Expr.makeIntValue(toIndex)});
		}

		/**
		 * Name binding for function: intArray_removeRange.
		 * @see #intArray_removeRange(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intArray_removeRange = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"intArray_removeRange");

		/**
		 * Helper binding method for function: intArray_replace. 
		 * @param array
		 * @param oldElementValue
		 * @param newElementValue
		 * @return the SourceModule.expr representing an application of intArray_replace
		 */
		public static final SourceModel.Expr intArray_replace(SourceModel.Expr array, SourceModel.Expr oldElementValue, SourceModel.Expr newElementValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_replace), array, oldElementValue, newElementValue});
		}

		/**
		 * @see #intArray_replace(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param oldElementValue
		 * @param newElementValue
		 * @return the SourceModel.Expr representing an application of intArray_replace
		 */
		public static final SourceModel.Expr intArray_replace(SourceModel.Expr array, int oldElementValue, int newElementValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_replace), array, SourceModel.Expr.makeIntValue(oldElementValue), SourceModel.Expr.makeIntValue(newElementValue)});
		}

		/**
		 * Name binding for function: intArray_replace.
		 * @see #intArray_replace(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intArray_replace = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"intArray_replace");

		/**
		 * Helper binding method for function: intArray_replicate. 
		 * @param nCopies
		 * @param valueToReplicate
		 * @return the SourceModule.expr representing an application of intArray_replicate
		 */
		public static final SourceModel.Expr intArray_replicate(SourceModel.Expr nCopies, SourceModel.Expr valueToReplicate) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_replicate), nCopies, valueToReplicate});
		}

		/**
		 * @see #intArray_replicate(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param nCopies
		 * @param valueToReplicate
		 * @return the SourceModel.Expr representing an application of intArray_replicate
		 */
		public static final SourceModel.Expr intArray_replicate(int nCopies, int valueToReplicate) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_replicate), SourceModel.Expr.makeIntValue(nCopies), SourceModel.Expr.makeIntValue(valueToReplicate)});
		}

		/**
		 * Name binding for function: intArray_replicate.
		 * @see #intArray_replicate(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intArray_replicate = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"intArray_replicate");

		/**
		 * Helper binding method for function: intArray_reverse. 
		 * @param array
		 * @return the SourceModule.expr representing an application of intArray_reverse
		 */
		public static final SourceModel.Expr intArray_reverse(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_reverse), array});
		}

		/**
		 * Name binding for function: intArray_reverse.
		 * @see #intArray_reverse(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intArray_reverse = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"intArray_reverse");

		/**
		 * Helper binding method for function: intArray_sort. 
		 * @param array
		 * @return the SourceModule.expr representing an application of intArray_sort
		 */
		public static final SourceModel.Expr intArray_sort(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_sort), array});
		}

		/**
		 * Name binding for function: intArray_sort.
		 * @see #intArray_sort(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intArray_sort = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"intArray_sort");

		/**
		 * Helper binding method for function: intArray_subArray. 
		 * @param array
		 * @param fromIndex
		 * @param toIndex
		 * @return the SourceModule.expr representing an application of intArray_subArray
		 */
		public static final SourceModel.Expr intArray_subArray(SourceModel.Expr array, SourceModel.Expr fromIndex, SourceModel.Expr toIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_subArray), array, fromIndex, toIndex});
		}

		/**
		 * @see #intArray_subArray(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param fromIndex
		 * @param toIndex
		 * @return the SourceModel.Expr representing an application of intArray_subArray
		 */
		public static final SourceModel.Expr intArray_subArray(SourceModel.Expr array, int fromIndex, int toIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_subArray), array, SourceModel.Expr.makeIntValue(fromIndex), SourceModel.Expr.makeIntValue(toIndex)});
		}

		/**
		 * Name binding for function: intArray_subArray.
		 * @see #intArray_subArray(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intArray_subArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"intArray_subArray");

		/**
		 * Helper binding method for function: intArray_subscript. 
		 * @param array
		 * @param index
		 * @return the SourceModule.expr representing an application of intArray_subscript
		 */
		public static final SourceModel.Expr intArray_subscript(SourceModel.Expr array, SourceModel.Expr index) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_subscript), array, index});
		}

		/**
		 * @see #intArray_subscript(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param index
		 * @return the SourceModel.Expr representing an application of intArray_subscript
		 */
		public static final SourceModel.Expr intArray_subscript(SourceModel.Expr array, int index) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_subscript), array, SourceModel.Expr.makeIntValue(index)});
		}

		/**
		 * Name binding for function: intArray_subscript.
		 * @see #intArray_subscript(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intArray_subscript = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"intArray_subscript");

		/**
		 * Converts a Java array of primitive Java ints to a Java array of CAL <code>Cal.Core.Prelude.Int</code>s.
		 * @param array (CAL type: <code>Cal.Collections.ArrayPrimitives.JIntArray</code>)
		 * @return (CAL type: <code>Cal.Collections.ArrayPrimitives.JCalValueArray</code>) 
		 */
		public static final SourceModel.Expr intArray_toCalValueArray(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_toCalValueArray), array});
		}

		/**
		 * Name binding for function: intArray_toCalValueArray.
		 * @see #intArray_toCalValueArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intArray_toCalValueArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"intArray_toCalValueArray");

		/**
		 * Helper binding method for function: intArray_toShowString. 
		 * @param array
		 * @return the SourceModule.expr representing an application of intArray_toShowString
		 */
		public static final SourceModel.Expr intArray_toShowString(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_toShowString), array});
		}

		/**
		 * Name binding for function: intArray_toShowString.
		 * @see #intArray_toShowString(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intArray_toShowString = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"intArray_toShowString");

		/**
		 * Helper binding method for function: intArray_update. 
		 * @param array
		 * @param index
		 * @param newValue
		 * @return the SourceModule.expr representing an application of intArray_update
		 */
		public static final SourceModel.Expr intArray_update(SourceModel.Expr array, SourceModel.Expr index, SourceModel.Expr newValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_update), array, index, newValue});
		}

		/**
		 * @see #intArray_update(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param index
		 * @param newValue
		 * @return the SourceModel.Expr representing an application of intArray_update
		 */
		public static final SourceModel.Expr intArray_update(SourceModel.Expr array, int index, int newValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intArray_update), array, SourceModel.Expr.makeIntValue(index), SourceModel.Expr.makeIntValue(newValue)});
		}

		/**
		 * Name binding for function: intArray_update.
		 * @see #intArray_update(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intArray_update = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"intArray_update");

		/**
		 * Helper binding method for function: longArray_append. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of longArray_append
		 */
		public static final SourceModel.Expr longArray_append(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_append), array1, array2});
		}

		/**
		 * Name binding for function: longArray_append.
		 * @see #longArray_append(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName longArray_append = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"longArray_append");

		/**
		 * Helper binding method for function: longArray_array1. 
		 * @param value
		 * @return the SourceModule.expr representing an application of longArray_array1
		 */
		public static final SourceModel.Expr longArray_array1(SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_array1), value});
		}

		/**
		 * @see #longArray_array1(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value
		 * @return the SourceModel.Expr representing an application of longArray_array1
		 */
		public static final SourceModel.Expr longArray_array1(long value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_array1), SourceModel.Expr.makeLongValue(value)});
		}

		/**
		 * Name binding for function: longArray_array1.
		 * @see #longArray_array1(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName longArray_array1 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"longArray_array1");

		/**
		 * Helper binding method for function: longArray_array2. 
		 * @param value1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of longArray_array2
		 */
		public static final SourceModel.Expr longArray_array2(SourceModel.Expr value1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_array2), value1, arg_2});
		}

		/**
		 * @see #longArray_array2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of longArray_array2
		 */
		public static final SourceModel.Expr longArray_array2(long value1, long arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_array2), SourceModel.Expr.makeLongValue(value1), SourceModel.Expr.makeLongValue(arg_2)});
		}

		/**
		 * Name binding for function: longArray_array2.
		 * @see #longArray_array2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName longArray_array2 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"longArray_array2");

		/**
		 * Helper binding method for function: longArray_array3. 
		 * @param value1
		 * @param arg_2
		 * @param value2
		 * @return the SourceModule.expr representing an application of longArray_array3
		 */
		public static final SourceModel.Expr longArray_array3(SourceModel.Expr value1, SourceModel.Expr arg_2, SourceModel.Expr value2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_array3), value1, arg_2, value2});
		}

		/**
		 * @see #longArray_array3(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value1
		 * @param arg_2
		 * @param value2
		 * @return the SourceModel.Expr representing an application of longArray_array3
		 */
		public static final SourceModel.Expr longArray_array3(long value1, long arg_2, long value2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_array3), SourceModel.Expr.makeLongValue(value1), SourceModel.Expr.makeLongValue(arg_2), SourceModel.Expr.makeLongValue(value2)});
		}

		/**
		 * Name binding for function: longArray_array3.
		 * @see #longArray_array3(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName longArray_array3 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"longArray_array3");

		/**
		 * Helper binding method for function: longArray_array4. 
		 * @param value1
		 * @param arg_2
		 * @param value2
		 * @param arg_4
		 * @return the SourceModule.expr representing an application of longArray_array4
		 */
		public static final SourceModel.Expr longArray_array4(SourceModel.Expr value1, SourceModel.Expr arg_2, SourceModel.Expr value2, SourceModel.Expr arg_4) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_array4), value1, arg_2, value2, arg_4});
		}

		/**
		 * @see #longArray_array4(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value1
		 * @param arg_2
		 * @param value2
		 * @param arg_4
		 * @return the SourceModel.Expr representing an application of longArray_array4
		 */
		public static final SourceModel.Expr longArray_array4(long value1, long arg_2, long value2, long arg_4) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_array4), SourceModel.Expr.makeLongValue(value1), SourceModel.Expr.makeLongValue(arg_2), SourceModel.Expr.makeLongValue(value2), SourceModel.Expr.makeLongValue(arg_4)});
		}

		/**
		 * Name binding for function: longArray_array4.
		 * @see #longArray_array4(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName longArray_array4 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"longArray_array4");

		/**
		 * Helper binding method for function: longArray_array5. 
		 * @param value1
		 * @param arg_2
		 * @param value2
		 * @param arg_4
		 * @param value3
		 * @return the SourceModule.expr representing an application of longArray_array5
		 */
		public static final SourceModel.Expr longArray_array5(SourceModel.Expr value1, SourceModel.Expr arg_2, SourceModel.Expr value2, SourceModel.Expr arg_4, SourceModel.Expr value3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_array5), value1, arg_2, value2, arg_4, value3});
		}

		/**
		 * @see #longArray_array5(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value1
		 * @param arg_2
		 * @param value2
		 * @param arg_4
		 * @param value3
		 * @return the SourceModel.Expr representing an application of longArray_array5
		 */
		public static final SourceModel.Expr longArray_array5(long value1, long arg_2, long value2, long arg_4, long value3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_array5), SourceModel.Expr.makeLongValue(value1), SourceModel.Expr.makeLongValue(arg_2), SourceModel.Expr.makeLongValue(value2), SourceModel.Expr.makeLongValue(arg_4), SourceModel.Expr.makeLongValue(value3)});
		}

		/**
		 * Name binding for function: longArray_array5.
		 * @see #longArray_array5(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName longArray_array5 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"longArray_array5");

		/**
		 * Helper binding method for function: longArray_array6. 
		 * @param value1
		 * @param arg_2
		 * @param value2
		 * @param arg_4
		 * @param value3
		 * @param arg_6
		 * @return the SourceModule.expr representing an application of longArray_array6
		 */
		public static final SourceModel.Expr longArray_array6(SourceModel.Expr value1, SourceModel.Expr arg_2, SourceModel.Expr value2, SourceModel.Expr arg_4, SourceModel.Expr value3, SourceModel.Expr arg_6) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_array6), value1, arg_2, value2, arg_4, value3, arg_6});
		}

		/**
		 * @see #longArray_array6(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value1
		 * @param arg_2
		 * @param value2
		 * @param arg_4
		 * @param value3
		 * @param arg_6
		 * @return the SourceModel.Expr representing an application of longArray_array6
		 */
		public static final SourceModel.Expr longArray_array6(long value1, long arg_2, long value2, long arg_4, long value3, long arg_6) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_array6), SourceModel.Expr.makeLongValue(value1), SourceModel.Expr.makeLongValue(arg_2), SourceModel.Expr.makeLongValue(value2), SourceModel.Expr.makeLongValue(arg_4), SourceModel.Expr.makeLongValue(value3), SourceModel.Expr.makeLongValue(arg_6)});
		}

		/**
		 * Name binding for function: longArray_array6.
		 * @see #longArray_array6(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName longArray_array6 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"longArray_array6");

		/**
		 * Helper binding method for function: longArray_array7. 
		 * @param value1
		 * @param arg_2
		 * @param value2
		 * @param arg_4
		 * @param value3
		 * @param arg_6
		 * @param value4
		 * @return the SourceModule.expr representing an application of longArray_array7
		 */
		public static final SourceModel.Expr longArray_array7(SourceModel.Expr value1, SourceModel.Expr arg_2, SourceModel.Expr value2, SourceModel.Expr arg_4, SourceModel.Expr value3, SourceModel.Expr arg_6, SourceModel.Expr value4) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_array7), value1, arg_2, value2, arg_4, value3, arg_6, value4});
		}

		/**
		 * @see #longArray_array7(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value1
		 * @param arg_2
		 * @param value2
		 * @param arg_4
		 * @param value3
		 * @param arg_6
		 * @param value4
		 * @return the SourceModel.Expr representing an application of longArray_array7
		 */
		public static final SourceModel.Expr longArray_array7(long value1, long arg_2, long value2, long arg_4, long value3, long arg_6, long value4) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_array7), SourceModel.Expr.makeLongValue(value1), SourceModel.Expr.makeLongValue(arg_2), SourceModel.Expr.makeLongValue(value2), SourceModel.Expr.makeLongValue(arg_4), SourceModel.Expr.makeLongValue(value3), SourceModel.Expr.makeLongValue(arg_6), SourceModel.Expr.makeLongValue(value4)});
		}

		/**
		 * Name binding for function: longArray_array7.
		 * @see #longArray_array7(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName longArray_array7 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"longArray_array7");

		/**
		 * Helper binding method for function: longArray_arrayToList. 
		 * @param array
		 * @return the SourceModule.expr representing an application of longArray_arrayToList
		 */
		public static final SourceModel.Expr longArray_arrayToList(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_arrayToList), array});
		}

		/**
		 * Name binding for function: longArray_arrayToList.
		 * @see #longArray_arrayToList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName longArray_arrayToList = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"longArray_arrayToList");

		/**
		 * Helper binding method for function: longArray_binarySearch. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of longArray_binarySearch
		 */
		public static final SourceModel.Expr longArray_binarySearch(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_binarySearch), arg_1, arg_2});
		}

		/**
		 * @see #longArray_binarySearch(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of longArray_binarySearch
		 */
		public static final SourceModel.Expr longArray_binarySearch(SourceModel.Expr arg_1, long arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_binarySearch), arg_1, SourceModel.Expr.makeLongValue(arg_2)});
		}

		/**
		 * Name binding for function: longArray_binarySearch.
		 * @see #longArray_binarySearch(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName longArray_binarySearch = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"longArray_binarySearch");

		/**
		 * Helper binding method for function: longArray_cloneReplacingNullArray. 
		 * @param array
		 * @return the SourceModule.expr representing an application of longArray_cloneReplacingNullArray
		 */
		public static final SourceModel.Expr longArray_cloneReplacingNullArray(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_cloneReplacingNullArray), array});
		}

		/**
		 * Name binding for function: longArray_cloneReplacingNullArray.
		 * @see #longArray_cloneReplacingNullArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName longArray_cloneReplacingNullArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"longArray_cloneReplacingNullArray");

		/**
		 * Helper binding method for function: longArray_compare. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of longArray_compare
		 */
		public static final SourceModel.Expr longArray_compare(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_compare), array1, array2});
		}

		/**
		 * Name binding for function: longArray_compare.
		 * @see #longArray_compare(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName longArray_compare = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"longArray_compare");

		/**
		 * Helper binding method for function: longArray_compareHelper. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of longArray_compareHelper
		 */
		public static final SourceModel.Expr longArray_compareHelper(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_compareHelper), array1, array2});
		}

		/**
		 * Name binding for function: longArray_compareHelper.
		 * @see #longArray_compareHelper(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName longArray_compareHelper = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"longArray_compareHelper");

		/**
		 * Helper binding method for function: longArray_concat. 
		 * @param list
		 * @return the SourceModule.expr representing an application of longArray_concat
		 */
		public static final SourceModel.Expr longArray_concat(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_concat), list});
		}

		/**
		 * Name binding for function: longArray_concat.
		 * @see #longArray_concat(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName longArray_concat = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"longArray_concat");

		/**
		 * Helper binding method for function: longArray_concatList. 
		 * @param list
		 * @return the SourceModule.expr representing an application of longArray_concatList
		 */
		public static final SourceModel.Expr longArray_concatList(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_concatList), list});
		}

		/**
		 * Name binding for function: longArray_concatList.
		 * @see #longArray_concatList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName longArray_concatList = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"longArray_concatList");

		/**
		 * Helper binding method for function: longArray_empty. 
		 * @return the SourceModule.expr representing an application of longArray_empty
		 */
		public static final SourceModel.Expr longArray_empty() {
			return SourceModel.Expr.Var.make(Functions.longArray_empty);
		}

		/**
		 * Name binding for function: longArray_empty.
		 * @see #longArray_empty()
		 */
		public static final QualifiedName longArray_empty = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"longArray_empty");

		/**
		 * Helper binding method for function: longArray_equals. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of longArray_equals
		 */
		public static final SourceModel.Expr longArray_equals(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_equals), array1, array2});
		}

		/**
		 * Name binding for function: longArray_equals.
		 * @see #longArray_equals(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName longArray_equals = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"longArray_equals");

		/**
		 * Converts a Java array of CAL <code>Cal.Core.Prelude.Long</code>s to a Java array of primitive Java longs.
		 * @param calValueArray (CAL type: <code>Cal.Collections.ArrayPrimitives.JCalValueArray</code>)
		 * @return (CAL type: <code>Cal.Collections.ArrayPrimitives.JLongArray</code>) 
		 */
		public static final SourceModel.Expr longArray_fromCalValueArray(SourceModel.Expr calValueArray) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_fromCalValueArray), calValueArray});
		}

		/**
		 * Name binding for function: longArray_fromCalValueArray.
		 * @see #longArray_fromCalValueArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName longArray_fromCalValueArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"longArray_fromCalValueArray");

		/**
		 * Helper binding method for function: longArray_greaterThan. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of longArray_greaterThan
		 */
		public static final SourceModel.Expr longArray_greaterThan(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_greaterThan), array1, array2});
		}

		/**
		 * Name binding for function: longArray_greaterThan.
		 * @see #longArray_greaterThan(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName longArray_greaterThan = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"longArray_greaterThan");

		/**
		 * Helper binding method for function: longArray_greaterThanEquals. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of longArray_greaterThanEquals
		 */
		public static final SourceModel.Expr longArray_greaterThanEquals(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_greaterThanEquals), array1, array2});
		}

		/**
		 * Name binding for function: longArray_greaterThanEquals.
		 * @see #longArray_greaterThanEquals(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName longArray_greaterThanEquals = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"longArray_greaterThanEquals");

		/**
		 * Helper binding method for function: longArray_indexOf. 
		 * @param array
		 * @param element
		 * @return the SourceModule.expr representing an application of longArray_indexOf
		 */
		public static final SourceModel.Expr longArray_indexOf(SourceModel.Expr array, SourceModel.Expr element) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_indexOf), array, element});
		}

		/**
		 * @see #longArray_indexOf(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param element
		 * @return the SourceModel.Expr representing an application of longArray_indexOf
		 */
		public static final SourceModel.Expr longArray_indexOf(SourceModel.Expr array, long element) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_indexOf), array, SourceModel.Expr.makeLongValue(element)});
		}

		/**
		 * Name binding for function: longArray_indexOf.
		 * @see #longArray_indexOf(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName longArray_indexOf = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"longArray_indexOf");

		/**
		 * Helper binding method for function: longArray_indexOf2. 
		 * @param array
		 * @param element
		 * @param arg_3
		 * @return the SourceModule.expr representing an application of longArray_indexOf2
		 */
		public static final SourceModel.Expr longArray_indexOf2(SourceModel.Expr array, SourceModel.Expr element, SourceModel.Expr arg_3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_indexOf2), array, element, arg_3});
		}

		/**
		 * @see #longArray_indexOf2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param element
		 * @param arg_3
		 * @return the SourceModel.Expr representing an application of longArray_indexOf2
		 */
		public static final SourceModel.Expr longArray_indexOf2(SourceModel.Expr array, long element, int arg_3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_indexOf2), array, SourceModel.Expr.makeLongValue(element), SourceModel.Expr.makeIntValue(arg_3)});
		}

		/**
		 * Name binding for function: longArray_indexOf2.
		 * @see #longArray_indexOf2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName longArray_indexOf2 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"longArray_indexOf2");

		/**
		 * Helper binding method for function: longArray_isEmpty. 
		 * @param array
		 * @return the SourceModule.expr representing an application of longArray_isEmpty
		 */
		public static final SourceModel.Expr longArray_isEmpty(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_isEmpty), array});
		}

		/**
		 * Name binding for function: longArray_isEmpty.
		 * @see #longArray_isEmpty(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName longArray_isEmpty = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"longArray_isEmpty");

		/**
		 * Helper binding method for function: longArray_lastIndexOf. 
		 * @param array
		 * @param element
		 * @return the SourceModule.expr representing an application of longArray_lastIndexOf
		 */
		public static final SourceModel.Expr longArray_lastIndexOf(SourceModel.Expr array, SourceModel.Expr element) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_lastIndexOf), array, element});
		}

		/**
		 * @see #longArray_lastIndexOf(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param element
		 * @return the SourceModel.Expr representing an application of longArray_lastIndexOf
		 */
		public static final SourceModel.Expr longArray_lastIndexOf(SourceModel.Expr array, long element) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_lastIndexOf), array, SourceModel.Expr.makeLongValue(element)});
		}

		/**
		 * Name binding for function: longArray_lastIndexOf.
		 * @see #longArray_lastIndexOf(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName longArray_lastIndexOf = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"longArray_lastIndexOf");

		/**
		 * Helper binding method for function: longArray_lastIndexOf2. 
		 * @param array
		 * @param element
		 * @param arg_3
		 * @return the SourceModule.expr representing an application of longArray_lastIndexOf2
		 */
		public static final SourceModel.Expr longArray_lastIndexOf2(SourceModel.Expr array, SourceModel.Expr element, SourceModel.Expr arg_3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_lastIndexOf2), array, element, arg_3});
		}

		/**
		 * @see #longArray_lastIndexOf2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param element
		 * @param arg_3
		 * @return the SourceModel.Expr representing an application of longArray_lastIndexOf2
		 */
		public static final SourceModel.Expr longArray_lastIndexOf2(SourceModel.Expr array, long element, int arg_3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_lastIndexOf2), array, SourceModel.Expr.makeLongValue(element), SourceModel.Expr.makeIntValue(arg_3)});
		}

		/**
		 * Name binding for function: longArray_lastIndexOf2.
		 * @see #longArray_lastIndexOf2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName longArray_lastIndexOf2 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"longArray_lastIndexOf2");

		/**
		 * Helper binding method for function: longArray_length. 
		 * @param array
		 * @return the SourceModule.expr representing an application of longArray_length
		 */
		public static final SourceModel.Expr longArray_length(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_length), array});
		}

		/**
		 * Name binding for function: longArray_length.
		 * @see #longArray_length(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName longArray_length = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"longArray_length");

		/**
		 * Helper binding method for function: longArray_lessThan. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of longArray_lessThan
		 */
		public static final SourceModel.Expr longArray_lessThan(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_lessThan), array1, array2});
		}

		/**
		 * Name binding for function: longArray_lessThan.
		 * @see #longArray_lessThan(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName longArray_lessThan = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"longArray_lessThan");

		/**
		 * Helper binding method for function: longArray_lessThanEquals. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of longArray_lessThanEquals
		 */
		public static final SourceModel.Expr longArray_lessThanEquals(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_lessThanEquals), array1, array2});
		}

		/**
		 * Name binding for function: longArray_lessThanEquals.
		 * @see #longArray_lessThanEquals(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName longArray_lessThanEquals = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"longArray_lessThanEquals");

		/**
		 * Helper binding method for function: longArray_listToArray. 
		 * @param list
		 * @return the SourceModule.expr representing an application of longArray_listToArray
		 */
		public static final SourceModel.Expr longArray_listToArray(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_listToArray), list});
		}

		/**
		 * Name binding for function: longArray_listToArray.
		 * @see #longArray_listToArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName longArray_listToArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"longArray_listToArray");

		/**
		 * Helper binding method for function: longArray_listToArrayWithFirstElement. 
		 * @param firstElement
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of longArray_listToArrayWithFirstElement
		 */
		public static final SourceModel.Expr longArray_listToArrayWithFirstElement(SourceModel.Expr firstElement, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_listToArrayWithFirstElement), firstElement, arg_2});
		}

		/**
		 * @see #longArray_listToArrayWithFirstElement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param firstElement
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of longArray_listToArrayWithFirstElement
		 */
		public static final SourceModel.Expr longArray_listToArrayWithFirstElement(long firstElement, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_listToArrayWithFirstElement), SourceModel.Expr.makeLongValue(firstElement), arg_2});
		}

		/**
		 * Name binding for function: longArray_listToArrayWithFirstElement.
		 * @see #longArray_listToArrayWithFirstElement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName longArray_listToArrayWithFirstElement = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"longArray_listToArrayWithFirstElement");

		/**
		 * Helper binding method for function: longArray_makeDefault. 
		 * @param size
		 * @return the SourceModule.expr representing an application of longArray_makeDefault
		 */
		public static final SourceModel.Expr longArray_makeDefault(SourceModel.Expr size) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_makeDefault), size});
		}

		/**
		 * @see #longArray_makeDefault(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param size
		 * @return the SourceModel.Expr representing an application of longArray_makeDefault
		 */
		public static final SourceModel.Expr longArray_makeDefault(int size) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_makeDefault), SourceModel.Expr.makeIntValue(size)});
		}

		/**
		 * Name binding for function: longArray_makeDefault.
		 * @see #longArray_makeDefault(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName longArray_makeDefault = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"longArray_makeDefault");

		/**
		 * Helper binding method for function: longArray_max. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of longArray_max
		 */
		public static final SourceModel.Expr longArray_max(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_max), array1, array2});
		}

		/**
		 * Name binding for function: longArray_max.
		 * @see #longArray_max(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName longArray_max = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"longArray_max");

		/**
		 * Helper binding method for function: longArray_min. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of longArray_min
		 */
		public static final SourceModel.Expr longArray_min(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_min), array1, array2});
		}

		/**
		 * Name binding for function: longArray_min.
		 * @see #longArray_min(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName longArray_min = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"longArray_min");

		/**
		 * Helper binding method for function: longArray_notEquals. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of longArray_notEquals
		 */
		public static final SourceModel.Expr longArray_notEquals(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_notEquals), array1, array2});
		}

		/**
		 * Name binding for function: longArray_notEquals.
		 * @see #longArray_notEquals(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName longArray_notEquals = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"longArray_notEquals");

		/**
		 * Helper binding method for function: longArray_removeRange. 
		 * @param array
		 * @param fromIndex
		 * @param toIndex
		 * @return the SourceModule.expr representing an application of longArray_removeRange
		 */
		public static final SourceModel.Expr longArray_removeRange(SourceModel.Expr array, SourceModel.Expr fromIndex, SourceModel.Expr toIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_removeRange), array, fromIndex, toIndex});
		}

		/**
		 * @see #longArray_removeRange(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param fromIndex
		 * @param toIndex
		 * @return the SourceModel.Expr representing an application of longArray_removeRange
		 */
		public static final SourceModel.Expr longArray_removeRange(SourceModel.Expr array, int fromIndex, int toIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_removeRange), array, SourceModel.Expr.makeIntValue(fromIndex), SourceModel.Expr.makeIntValue(toIndex)});
		}

		/**
		 * Name binding for function: longArray_removeRange.
		 * @see #longArray_removeRange(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName longArray_removeRange = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"longArray_removeRange");

		/**
		 * Helper binding method for function: longArray_replace. 
		 * @param array
		 * @param oldElementValue
		 * @param arg_3
		 * @return the SourceModule.expr representing an application of longArray_replace
		 */
		public static final SourceModel.Expr longArray_replace(SourceModel.Expr array, SourceModel.Expr oldElementValue, SourceModel.Expr arg_3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_replace), array, oldElementValue, arg_3});
		}

		/**
		 * @see #longArray_replace(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param oldElementValue
		 * @param arg_3
		 * @return the SourceModel.Expr representing an application of longArray_replace
		 */
		public static final SourceModel.Expr longArray_replace(SourceModel.Expr array, long oldElementValue, long arg_3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_replace), array, SourceModel.Expr.makeLongValue(oldElementValue), SourceModel.Expr.makeLongValue(arg_3)});
		}

		/**
		 * Name binding for function: longArray_replace.
		 * @see #longArray_replace(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName longArray_replace = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"longArray_replace");

		/**
		 * Helper binding method for function: longArray_replicate. 
		 * @param nCopies
		 * @param valueToReplicate
		 * @return the SourceModule.expr representing an application of longArray_replicate
		 */
		public static final SourceModel.Expr longArray_replicate(SourceModel.Expr nCopies, SourceModel.Expr valueToReplicate) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_replicate), nCopies, valueToReplicate});
		}

		/**
		 * @see #longArray_replicate(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param nCopies
		 * @param valueToReplicate
		 * @return the SourceModel.Expr representing an application of longArray_replicate
		 */
		public static final SourceModel.Expr longArray_replicate(int nCopies, long valueToReplicate) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_replicate), SourceModel.Expr.makeIntValue(nCopies), SourceModel.Expr.makeLongValue(valueToReplicate)});
		}

		/**
		 * Name binding for function: longArray_replicate.
		 * @see #longArray_replicate(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName longArray_replicate = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"longArray_replicate");

		/**
		 * Helper binding method for function: longArray_reverse. 
		 * @param array
		 * @return the SourceModule.expr representing an application of longArray_reverse
		 */
		public static final SourceModel.Expr longArray_reverse(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_reverse), array});
		}

		/**
		 * Name binding for function: longArray_reverse.
		 * @see #longArray_reverse(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName longArray_reverse = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"longArray_reverse");

		/**
		 * Helper binding method for function: longArray_sort. 
		 * @param array
		 * @return the SourceModule.expr representing an application of longArray_sort
		 */
		public static final SourceModel.Expr longArray_sort(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_sort), array});
		}

		/**
		 * Name binding for function: longArray_sort.
		 * @see #longArray_sort(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName longArray_sort = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"longArray_sort");

		/**
		 * Helper binding method for function: longArray_subArray. 
		 * @param array
		 * @param fromIndex
		 * @param toIndex
		 * @return the SourceModule.expr representing an application of longArray_subArray
		 */
		public static final SourceModel.Expr longArray_subArray(SourceModel.Expr array, SourceModel.Expr fromIndex, SourceModel.Expr toIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_subArray), array, fromIndex, toIndex});
		}

		/**
		 * @see #longArray_subArray(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param fromIndex
		 * @param toIndex
		 * @return the SourceModel.Expr representing an application of longArray_subArray
		 */
		public static final SourceModel.Expr longArray_subArray(SourceModel.Expr array, int fromIndex, int toIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_subArray), array, SourceModel.Expr.makeIntValue(fromIndex), SourceModel.Expr.makeIntValue(toIndex)});
		}

		/**
		 * Name binding for function: longArray_subArray.
		 * @see #longArray_subArray(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName longArray_subArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"longArray_subArray");

		/**
		 * Helper binding method for function: longArray_subscript. 
		 * @param array
		 * @param index
		 * @return the SourceModule.expr representing an application of longArray_subscript
		 */
		public static final SourceModel.Expr longArray_subscript(SourceModel.Expr array, SourceModel.Expr index) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_subscript), array, index});
		}

		/**
		 * @see #longArray_subscript(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param index
		 * @return the SourceModel.Expr representing an application of longArray_subscript
		 */
		public static final SourceModel.Expr longArray_subscript(SourceModel.Expr array, int index) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_subscript), array, SourceModel.Expr.makeIntValue(index)});
		}

		/**
		 * Name binding for function: longArray_subscript.
		 * @see #longArray_subscript(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName longArray_subscript = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"longArray_subscript");

		/**
		 * Converts a Java array of primitive Java longs to a Java array of CAL <code>Cal.Core.Prelude.Long</code>s.
		 * @param array (CAL type: <code>Cal.Collections.ArrayPrimitives.JLongArray</code>)
		 * @return (CAL type: <code>Cal.Collections.ArrayPrimitives.JCalValueArray</code>) 
		 */
		public static final SourceModel.Expr longArray_toCalValueArray(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_toCalValueArray), array});
		}

		/**
		 * Name binding for function: longArray_toCalValueArray.
		 * @see #longArray_toCalValueArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName longArray_toCalValueArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"longArray_toCalValueArray");

		/**
		 * Helper binding method for function: longArray_toShowString. 
		 * @param array
		 * @return the SourceModule.expr representing an application of longArray_toShowString
		 */
		public static final SourceModel.Expr longArray_toShowString(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_toShowString), array});
		}

		/**
		 * Name binding for function: longArray_toShowString.
		 * @see #longArray_toShowString(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName longArray_toShowString = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"longArray_toShowString");

		/**
		 * Helper binding method for function: longArray_update. 
		 * @param array
		 * @param index
		 * @param newValue
		 * @return the SourceModule.expr representing an application of longArray_update
		 */
		public static final SourceModel.Expr longArray_update(SourceModel.Expr array, SourceModel.Expr index, SourceModel.Expr newValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_update), array, index, newValue});
		}

		/**
		 * @see #longArray_update(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param index
		 * @param newValue
		 * @return the SourceModel.Expr representing an application of longArray_update
		 */
		public static final SourceModel.Expr longArray_update(SourceModel.Expr array, int index, long newValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longArray_update), array, SourceModel.Expr.makeIntValue(index), SourceModel.Expr.makeLongValue(newValue)});
		}

		/**
		 * Name binding for function: longArray_update.
		 * @see #longArray_update(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName longArray_update = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"longArray_update");

		/**
		 * Helper binding method for function: objectArray_append. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of objectArray_append
		 */
		public static final SourceModel.Expr objectArray_append(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.objectArray_append), array1, array2});
		}

		/**
		 * Name binding for function: objectArray_append.
		 * @see #objectArray_append(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName objectArray_append = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"objectArray_append");

		/**
		 * Helper binding method for function: objectArray_array1. 
		 * @param value
		 * @return the SourceModule.expr representing an application of objectArray_array1
		 */
		public static final SourceModel.Expr objectArray_array1(SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.objectArray_array1), value});
		}

		/**
		 * Name binding for function: objectArray_array1.
		 * @see #objectArray_array1(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName objectArray_array1 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"objectArray_array1");

		/**
		 * Helper binding method for function: objectArray_array2. 
		 * @param value1
		 * @param value2
		 * @return the SourceModule.expr representing an application of objectArray_array2
		 */
		public static final SourceModel.Expr objectArray_array2(SourceModel.Expr value1, SourceModel.Expr value2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.objectArray_array2), value1, value2});
		}

		/**
		 * Name binding for function: objectArray_array2.
		 * @see #objectArray_array2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName objectArray_array2 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"objectArray_array2");

		/**
		 * Helper binding method for function: objectArray_array3. 
		 * @param value1
		 * @param value2
		 * @param value3
		 * @return the SourceModule.expr representing an application of objectArray_array3
		 */
		public static final SourceModel.Expr objectArray_array3(SourceModel.Expr value1, SourceModel.Expr value2, SourceModel.Expr value3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.objectArray_array3), value1, value2, value3});
		}

		/**
		 * Name binding for function: objectArray_array3.
		 * @see #objectArray_array3(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName objectArray_array3 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"objectArray_array3");

		/**
		 * Helper binding method for function: objectArray_array4. 
		 * @param value1
		 * @param value2
		 * @param value3
		 * @param value4
		 * @return the SourceModule.expr representing an application of objectArray_array4
		 */
		public static final SourceModel.Expr objectArray_array4(SourceModel.Expr value1, SourceModel.Expr value2, SourceModel.Expr value3, SourceModel.Expr value4) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.objectArray_array4), value1, value2, value3, value4});
		}

		/**
		 * Name binding for function: objectArray_array4.
		 * @see #objectArray_array4(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName objectArray_array4 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"objectArray_array4");

		/**
		 * Helper binding method for function: objectArray_array5. 
		 * @param value1
		 * @param value2
		 * @param value3
		 * @param value4
		 * @param value5
		 * @return the SourceModule.expr representing an application of objectArray_array5
		 */
		public static final SourceModel.Expr objectArray_array5(SourceModel.Expr value1, SourceModel.Expr value2, SourceModel.Expr value3, SourceModel.Expr value4, SourceModel.Expr value5) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.objectArray_array5), value1, value2, value3, value4, value5});
		}

		/**
		 * Name binding for function: objectArray_array5.
		 * @see #objectArray_array5(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName objectArray_array5 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"objectArray_array5");

		/**
		 * Helper binding method for function: objectArray_array6. 
		 * @param value1
		 * @param value2
		 * @param value3
		 * @param value4
		 * @param value5
		 * @param value6
		 * @return the SourceModule.expr representing an application of objectArray_array6
		 */
		public static final SourceModel.Expr objectArray_array6(SourceModel.Expr value1, SourceModel.Expr value2, SourceModel.Expr value3, SourceModel.Expr value4, SourceModel.Expr value5, SourceModel.Expr value6) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.objectArray_array6), value1, value2, value3, value4, value5, value6});
		}

		/**
		 * Name binding for function: objectArray_array6.
		 * @see #objectArray_array6(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName objectArray_array6 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"objectArray_array6");

		/**
		 * Helper binding method for function: objectArray_array7. 
		 * @param value1
		 * @param value2
		 * @param value3
		 * @param value4
		 * @param value5
		 * @param value6
		 * @param value7
		 * @return the SourceModule.expr representing an application of objectArray_array7
		 */
		public static final SourceModel.Expr objectArray_array7(SourceModel.Expr value1, SourceModel.Expr value2, SourceModel.Expr value3, SourceModel.Expr value4, SourceModel.Expr value5, SourceModel.Expr value6, SourceModel.Expr value7) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.objectArray_array7), value1, value2, value3, value4, value5, value6, value7});
		}

		/**
		 * Name binding for function: objectArray_array7.
		 * @see #objectArray_array7(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName objectArray_array7 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"objectArray_array7");

		/**
		 * Helper binding method for function: objectArray_arrayToList. 
		 * @param array
		 * @return the SourceModule.expr representing an application of objectArray_arrayToList
		 */
		public static final SourceModel.Expr objectArray_arrayToList(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.objectArray_arrayToList), array});
		}

		/**
		 * Name binding for function: objectArray_arrayToList.
		 * @see #objectArray_arrayToList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName objectArray_arrayToList = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"objectArray_arrayToList");

		/**
		 * Helper binding method for function: objectArray_cloneReplacingNullArray. 
		 * @param array
		 * @return the SourceModule.expr representing an application of objectArray_cloneReplacingNullArray
		 */
		public static final SourceModel.Expr objectArray_cloneReplacingNullArray(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.objectArray_cloneReplacingNullArray), array});
		}

		/**
		 * Name binding for function: objectArray_cloneReplacingNullArray.
		 * @see #objectArray_cloneReplacingNullArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName objectArray_cloneReplacingNullArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"objectArray_cloneReplacingNullArray");

		/**
		 * Helper binding method for function: objectArray_concat. 
		 * @param list
		 * @return the SourceModule.expr representing an application of objectArray_concat
		 */
		public static final SourceModel.Expr objectArray_concat(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.objectArray_concat), list});
		}

		/**
		 * Name binding for function: objectArray_concat.
		 * @see #objectArray_concat(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName objectArray_concat = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"objectArray_concat");

		/**
		 * Helper binding method for function: objectArray_concatList. 
		 * @param list
		 * @return the SourceModule.expr representing an application of objectArray_concatList
		 */
		public static final SourceModel.Expr objectArray_concatList(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.objectArray_concatList), list});
		}

		/**
		 * Name binding for function: objectArray_concatList.
		 * @see #objectArray_concatList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName objectArray_concatList = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"objectArray_concatList");

		/**
		 * Helper binding method for function: objectArray_empty. 
		 * @return the SourceModule.expr representing an application of objectArray_empty
		 */
		public static final SourceModel.Expr objectArray_empty() {
			return SourceModel.Expr.Var.make(Functions.objectArray_empty);
		}

		/**
		 * Name binding for function: objectArray_empty.
		 * @see #objectArray_empty()
		 */
		public static final QualifiedName objectArray_empty = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"objectArray_empty");

		/**
		 * Converts a Java array of CAL Objects to a Java array of primitive Java Objects.
		 * @param calValueArray (CAL type: <code>Cal.Collections.ArrayPrimitives.JCalValueArray</code>)
		 * @return (CAL type: <code>Cal.Collections.ArrayPrimitives.JObjectArray</code>) 
		 */
		public static final SourceModel.Expr objectArray_fromCalValueArray(SourceModel.Expr calValueArray) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.objectArray_fromCalValueArray), calValueArray});
		}

		/**
		 * Name binding for function: objectArray_fromCalValueArray.
		 * @see #objectArray_fromCalValueArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName objectArray_fromCalValueArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"objectArray_fromCalValueArray");

		/**
		 * Helper binding method for function: objectArray_isEmpty. 
		 * @param array
		 * @return the SourceModule.expr representing an application of objectArray_isEmpty
		 */
		public static final SourceModel.Expr objectArray_isEmpty(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.objectArray_isEmpty), array});
		}

		/**
		 * Name binding for function: objectArray_isEmpty.
		 * @see #objectArray_isEmpty(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName objectArray_isEmpty = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"objectArray_isEmpty");

		/**
		 * Helper binding method for function: objectArray_length. 
		 * @param array
		 * @return the SourceModule.expr representing an application of objectArray_length
		 */
		public static final SourceModel.Expr objectArray_length(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.objectArray_length), array});
		}

		/**
		 * Name binding for function: objectArray_length.
		 * @see #objectArray_length(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName objectArray_length = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"objectArray_length");

		/**
		 * Helper binding method for function: objectArray_listToArray. 
		 * @param list
		 * @return the SourceModule.expr representing an application of objectArray_listToArray
		 */
		public static final SourceModel.Expr objectArray_listToArray(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.objectArray_listToArray), list});
		}

		/**
		 * Name binding for function: objectArray_listToArray.
		 * @see #objectArray_listToArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName objectArray_listToArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"objectArray_listToArray");

		/**
		 * Helper binding method for function: objectArray_listToArrayWithFirstElement. 
		 * @param firstElement
		 * @param list
		 * @return the SourceModule.expr representing an application of objectArray_listToArrayWithFirstElement
		 */
		public static final SourceModel.Expr objectArray_listToArrayWithFirstElement(SourceModel.Expr firstElement, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.objectArray_listToArrayWithFirstElement), firstElement, list});
		}

		/**
		 * Name binding for function: objectArray_listToArrayWithFirstElement.
		 * @see #objectArray_listToArrayWithFirstElement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName objectArray_listToArrayWithFirstElement = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"objectArray_listToArrayWithFirstElement");

		/**
		 * Helper binding method for function: objectArray_makeDefault. 
		 * @param size
		 * @return the SourceModule.expr representing an application of objectArray_makeDefault
		 */
		public static final SourceModel.Expr objectArray_makeDefault(SourceModel.Expr size) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.objectArray_makeDefault), size});
		}

		/**
		 * @see #objectArray_makeDefault(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param size
		 * @return the SourceModel.Expr representing an application of objectArray_makeDefault
		 */
		public static final SourceModel.Expr objectArray_makeDefault(int size) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.objectArray_makeDefault), SourceModel.Expr.makeIntValue(size)});
		}

		/**
		 * Name binding for function: objectArray_makeDefault.
		 * @see #objectArray_makeDefault(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName objectArray_makeDefault = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"objectArray_makeDefault");

		/**
		 * Helper binding method for function: objectArray_removeRange. 
		 * @param array
		 * @param fromIndex
		 * @param toIndex
		 * @return the SourceModule.expr representing an application of objectArray_removeRange
		 */
		public static final SourceModel.Expr objectArray_removeRange(SourceModel.Expr array, SourceModel.Expr fromIndex, SourceModel.Expr toIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.objectArray_removeRange), array, fromIndex, toIndex});
		}

		/**
		 * @see #objectArray_removeRange(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param fromIndex
		 * @param toIndex
		 * @return the SourceModel.Expr representing an application of objectArray_removeRange
		 */
		public static final SourceModel.Expr objectArray_removeRange(SourceModel.Expr array, int fromIndex, int toIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.objectArray_removeRange), array, SourceModel.Expr.makeIntValue(fromIndex), SourceModel.Expr.makeIntValue(toIndex)});
		}

		/**
		 * Name binding for function: objectArray_removeRange.
		 * @see #objectArray_removeRange(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName objectArray_removeRange = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"objectArray_removeRange");

		/**
		 * Helper binding method for function: objectArray_replicate. 
		 * @param nCopies
		 * @param valueToReplicate
		 * @return the SourceModule.expr representing an application of objectArray_replicate
		 */
		public static final SourceModel.Expr objectArray_replicate(SourceModel.Expr nCopies, SourceModel.Expr valueToReplicate) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.objectArray_replicate), nCopies, valueToReplicate});
		}

		/**
		 * @see #objectArray_replicate(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param nCopies
		 * @param valueToReplicate
		 * @return the SourceModel.Expr representing an application of objectArray_replicate
		 */
		public static final SourceModel.Expr objectArray_replicate(int nCopies, SourceModel.Expr valueToReplicate) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.objectArray_replicate), SourceModel.Expr.makeIntValue(nCopies), valueToReplicate});
		}

		/**
		 * Name binding for function: objectArray_replicate.
		 * @see #objectArray_replicate(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName objectArray_replicate = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"objectArray_replicate");

		/**
		 * Helper binding method for function: objectArray_reverse. 
		 * @param array
		 * @return the SourceModule.expr representing an application of objectArray_reverse
		 */
		public static final SourceModel.Expr objectArray_reverse(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.objectArray_reverse), array});
		}

		/**
		 * Name binding for function: objectArray_reverse.
		 * @see #objectArray_reverse(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName objectArray_reverse = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"objectArray_reverse");

		/**
		 * Helper binding method for function: objectArray_sort. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of objectArray_sort
		 */
		public static final SourceModel.Expr objectArray_sort(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.objectArray_sort), arg_1, arg_2});
		}

		/**
		 * Name binding for function: objectArray_sort.
		 * @see #objectArray_sort(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName objectArray_sort = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"objectArray_sort");

		/**
		 * Helper binding method for function: objectArray_subArray. 
		 * @param array
		 * @param fromIndex
		 * @param toIndex
		 * @return the SourceModule.expr representing an application of objectArray_subArray
		 */
		public static final SourceModel.Expr objectArray_subArray(SourceModel.Expr array, SourceModel.Expr fromIndex, SourceModel.Expr toIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.objectArray_subArray), array, fromIndex, toIndex});
		}

		/**
		 * @see #objectArray_subArray(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param fromIndex
		 * @param toIndex
		 * @return the SourceModel.Expr representing an application of objectArray_subArray
		 */
		public static final SourceModel.Expr objectArray_subArray(SourceModel.Expr array, int fromIndex, int toIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.objectArray_subArray), array, SourceModel.Expr.makeIntValue(fromIndex), SourceModel.Expr.makeIntValue(toIndex)});
		}

		/**
		 * Name binding for function: objectArray_subArray.
		 * @see #objectArray_subArray(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName objectArray_subArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"objectArray_subArray");

		/**
		 * Helper binding method for function: objectArray_subscript. 
		 * @param array
		 * @param index
		 * @return the SourceModule.expr representing an application of objectArray_subscript
		 */
		public static final SourceModel.Expr objectArray_subscript(SourceModel.Expr array, SourceModel.Expr index) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.objectArray_subscript), array, index});
		}

		/**
		 * @see #objectArray_subscript(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param index
		 * @return the SourceModel.Expr representing an application of objectArray_subscript
		 */
		public static final SourceModel.Expr objectArray_subscript(SourceModel.Expr array, int index) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.objectArray_subscript), array, SourceModel.Expr.makeIntValue(index)});
		}

		/**
		 * Name binding for function: objectArray_subscript.
		 * @see #objectArray_subscript(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName objectArray_subscript = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"objectArray_subscript");

		/**
		 * Converts a Java array of primitive Java Objects to a Java array of CAL Objects.
		 * @param array (CAL type: <code>Cal.Collections.ArrayPrimitives.JObjectArray</code>)
		 * @return (CAL type: <code>Cal.Collections.ArrayPrimitives.JCalValueArray</code>) 
		 */
		public static final SourceModel.Expr objectArray_toCalValueArray(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.objectArray_toCalValueArray), array});
		}

		/**
		 * Name binding for function: objectArray_toCalValueArray.
		 * @see #objectArray_toCalValueArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName objectArray_toCalValueArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"objectArray_toCalValueArray");

		/**
		 * Helper binding method for function: objectArray_toShowString. 
		 * @param array
		 * @return the SourceModule.expr representing an application of objectArray_toShowString
		 */
		public static final SourceModel.Expr objectArray_toShowString(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.objectArray_toShowString), array});
		}

		/**
		 * Name binding for function: objectArray_toShowString.
		 * @see #objectArray_toShowString(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName objectArray_toShowString = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"objectArray_toShowString");

		/**
		 * Helper binding method for function: objectArray_update. 
		 * @param array
		 * @param index
		 * @param newValue
		 * @return the SourceModule.expr representing an application of objectArray_update
		 */
		public static final SourceModel.Expr objectArray_update(SourceModel.Expr array, SourceModel.Expr index, SourceModel.Expr newValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.objectArray_update), array, index, newValue});
		}

		/**
		 * @see #objectArray_update(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param index
		 * @param newValue
		 * @return the SourceModel.Expr representing an application of objectArray_update
		 */
		public static final SourceModel.Expr objectArray_update(SourceModel.Expr array, int index, SourceModel.Expr newValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.objectArray_update), array, SourceModel.Expr.makeIntValue(index), newValue});
		}

		/**
		 * Name binding for function: objectArray_update.
		 * @see #objectArray_update(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName objectArray_update = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"objectArray_update");

		/**
		 * Helper binding method for function: shortArray_append. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of shortArray_append
		 */
		public static final SourceModel.Expr shortArray_append(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_append), array1, array2});
		}

		/**
		 * Name binding for function: shortArray_append.
		 * @see #shortArray_append(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shortArray_append = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"shortArray_append");

		/**
		 * Helper binding method for function: shortArray_array1. 
		 * @param value
		 * @return the SourceModule.expr representing an application of shortArray_array1
		 */
		public static final SourceModel.Expr shortArray_array1(SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_array1), value});
		}

		/**
		 * @see #shortArray_array1(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value
		 * @return the SourceModel.Expr representing an application of shortArray_array1
		 */
		public static final SourceModel.Expr shortArray_array1(short value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_array1), SourceModel.Expr.makeShortValue(value)});
		}

		/**
		 * Name binding for function: shortArray_array1.
		 * @see #shortArray_array1(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shortArray_array1 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"shortArray_array1");

		/**
		 * Helper binding method for function: shortArray_array2. 
		 * @param value1
		 * @param value2
		 * @return the SourceModule.expr representing an application of shortArray_array2
		 */
		public static final SourceModel.Expr shortArray_array2(SourceModel.Expr value1, SourceModel.Expr value2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_array2), value1, value2});
		}

		/**
		 * @see #shortArray_array2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value1
		 * @param value2
		 * @return the SourceModel.Expr representing an application of shortArray_array2
		 */
		public static final SourceModel.Expr shortArray_array2(short value1, short value2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_array2), SourceModel.Expr.makeShortValue(value1), SourceModel.Expr.makeShortValue(value2)});
		}

		/**
		 * Name binding for function: shortArray_array2.
		 * @see #shortArray_array2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shortArray_array2 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"shortArray_array2");

		/**
		 * Helper binding method for function: shortArray_array3. 
		 * @param value1
		 * @param value2
		 * @param value3
		 * @return the SourceModule.expr representing an application of shortArray_array3
		 */
		public static final SourceModel.Expr shortArray_array3(SourceModel.Expr value1, SourceModel.Expr value2, SourceModel.Expr value3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_array3), value1, value2, value3});
		}

		/**
		 * @see #shortArray_array3(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value1
		 * @param value2
		 * @param value3
		 * @return the SourceModel.Expr representing an application of shortArray_array3
		 */
		public static final SourceModel.Expr shortArray_array3(short value1, short value2, short value3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_array3), SourceModel.Expr.makeShortValue(value1), SourceModel.Expr.makeShortValue(value2), SourceModel.Expr.makeShortValue(value3)});
		}

		/**
		 * Name binding for function: shortArray_array3.
		 * @see #shortArray_array3(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shortArray_array3 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"shortArray_array3");

		/**
		 * Helper binding method for function: shortArray_array4. 
		 * @param value1
		 * @param value2
		 * @param value3
		 * @param value4
		 * @return the SourceModule.expr representing an application of shortArray_array4
		 */
		public static final SourceModel.Expr shortArray_array4(SourceModel.Expr value1, SourceModel.Expr value2, SourceModel.Expr value3, SourceModel.Expr value4) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_array4), value1, value2, value3, value4});
		}

		/**
		 * @see #shortArray_array4(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value1
		 * @param value2
		 * @param value3
		 * @param value4
		 * @return the SourceModel.Expr representing an application of shortArray_array4
		 */
		public static final SourceModel.Expr shortArray_array4(short value1, short value2, short value3, short value4) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_array4), SourceModel.Expr.makeShortValue(value1), SourceModel.Expr.makeShortValue(value2), SourceModel.Expr.makeShortValue(value3), SourceModel.Expr.makeShortValue(value4)});
		}

		/**
		 * Name binding for function: shortArray_array4.
		 * @see #shortArray_array4(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shortArray_array4 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"shortArray_array4");

		/**
		 * Helper binding method for function: shortArray_array5. 
		 * @param value1
		 * @param value2
		 * @param value3
		 * @param value4
		 * @param value5
		 * @return the SourceModule.expr representing an application of shortArray_array5
		 */
		public static final SourceModel.Expr shortArray_array5(SourceModel.Expr value1, SourceModel.Expr value2, SourceModel.Expr value3, SourceModel.Expr value4, SourceModel.Expr value5) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_array5), value1, value2, value3, value4, value5});
		}

		/**
		 * @see #shortArray_array5(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value1
		 * @param value2
		 * @param value3
		 * @param value4
		 * @param value5
		 * @return the SourceModel.Expr representing an application of shortArray_array5
		 */
		public static final SourceModel.Expr shortArray_array5(short value1, short value2, short value3, short value4, short value5) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_array5), SourceModel.Expr.makeShortValue(value1), SourceModel.Expr.makeShortValue(value2), SourceModel.Expr.makeShortValue(value3), SourceModel.Expr.makeShortValue(value4), SourceModel.Expr.makeShortValue(value5)});
		}

		/**
		 * Name binding for function: shortArray_array5.
		 * @see #shortArray_array5(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shortArray_array5 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"shortArray_array5");

		/**
		 * Helper binding method for function: shortArray_array6. 
		 * @param value1
		 * @param value2
		 * @param value3
		 * @param value4
		 * @param value5
		 * @param value6
		 * @return the SourceModule.expr representing an application of shortArray_array6
		 */
		public static final SourceModel.Expr shortArray_array6(SourceModel.Expr value1, SourceModel.Expr value2, SourceModel.Expr value3, SourceModel.Expr value4, SourceModel.Expr value5, SourceModel.Expr value6) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_array6), value1, value2, value3, value4, value5, value6});
		}

		/**
		 * @see #shortArray_array6(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value1
		 * @param value2
		 * @param value3
		 * @param value4
		 * @param value5
		 * @param value6
		 * @return the SourceModel.Expr representing an application of shortArray_array6
		 */
		public static final SourceModel.Expr shortArray_array6(short value1, short value2, short value3, short value4, short value5, short value6) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_array6), SourceModel.Expr.makeShortValue(value1), SourceModel.Expr.makeShortValue(value2), SourceModel.Expr.makeShortValue(value3), SourceModel.Expr.makeShortValue(value4), SourceModel.Expr.makeShortValue(value5), SourceModel.Expr.makeShortValue(value6)});
		}

		/**
		 * Name binding for function: shortArray_array6.
		 * @see #shortArray_array6(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shortArray_array6 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"shortArray_array6");

		/**
		 * Helper binding method for function: shortArray_array7. 
		 * @param value1
		 * @param value2
		 * @param value3
		 * @param value4
		 * @param value5
		 * @param value6
		 * @param value7
		 * @return the SourceModule.expr representing an application of shortArray_array7
		 */
		public static final SourceModel.Expr shortArray_array7(SourceModel.Expr value1, SourceModel.Expr value2, SourceModel.Expr value3, SourceModel.Expr value4, SourceModel.Expr value5, SourceModel.Expr value6, SourceModel.Expr value7) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_array7), value1, value2, value3, value4, value5, value6, value7});
		}

		/**
		 * @see #shortArray_array7(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value1
		 * @param value2
		 * @param value3
		 * @param value4
		 * @param value5
		 * @param value6
		 * @param value7
		 * @return the SourceModel.Expr representing an application of shortArray_array7
		 */
		public static final SourceModel.Expr shortArray_array7(short value1, short value2, short value3, short value4, short value5, short value6, short value7) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_array7), SourceModel.Expr.makeShortValue(value1), SourceModel.Expr.makeShortValue(value2), SourceModel.Expr.makeShortValue(value3), SourceModel.Expr.makeShortValue(value4), SourceModel.Expr.makeShortValue(value5), SourceModel.Expr.makeShortValue(value6), SourceModel.Expr.makeShortValue(value7)});
		}

		/**
		 * Name binding for function: shortArray_array7.
		 * @see #shortArray_array7(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shortArray_array7 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"shortArray_array7");

		/**
		 * Helper binding method for function: shortArray_arrayToList. 
		 * @param array
		 * @return the SourceModule.expr representing an application of shortArray_arrayToList
		 */
		public static final SourceModel.Expr shortArray_arrayToList(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_arrayToList), array});
		}

		/**
		 * Name binding for function: shortArray_arrayToList.
		 * @see #shortArray_arrayToList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shortArray_arrayToList = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"shortArray_arrayToList");

		/**
		 * Helper binding method for function: shortArray_binarySearch. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of shortArray_binarySearch
		 */
		public static final SourceModel.Expr shortArray_binarySearch(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_binarySearch), arg_1, arg_2});
		}

		/**
		 * @see #shortArray_binarySearch(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of shortArray_binarySearch
		 */
		public static final SourceModel.Expr shortArray_binarySearch(SourceModel.Expr arg_1, short arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_binarySearch), arg_1, SourceModel.Expr.makeShortValue(arg_2)});
		}

		/**
		 * Name binding for function: shortArray_binarySearch.
		 * @see #shortArray_binarySearch(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shortArray_binarySearch = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"shortArray_binarySearch");

		/**
		 * Helper binding method for function: shortArray_cloneReplacingNullArray. 
		 * @param array
		 * @return the SourceModule.expr representing an application of shortArray_cloneReplacingNullArray
		 */
		public static final SourceModel.Expr shortArray_cloneReplacingNullArray(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_cloneReplacingNullArray), array});
		}

		/**
		 * Name binding for function: shortArray_cloneReplacingNullArray.
		 * @see #shortArray_cloneReplacingNullArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shortArray_cloneReplacingNullArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"shortArray_cloneReplacingNullArray");

		/**
		 * Helper binding method for function: shortArray_compare. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of shortArray_compare
		 */
		public static final SourceModel.Expr shortArray_compare(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_compare), array1, array2});
		}

		/**
		 * Name binding for function: shortArray_compare.
		 * @see #shortArray_compare(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shortArray_compare = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"shortArray_compare");

		/**
		 * Helper binding method for function: shortArray_compareHelper. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of shortArray_compareHelper
		 */
		public static final SourceModel.Expr shortArray_compareHelper(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_compareHelper), array1, array2});
		}

		/**
		 * Name binding for function: shortArray_compareHelper.
		 * @see #shortArray_compareHelper(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shortArray_compareHelper = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"shortArray_compareHelper");

		/**
		 * Helper binding method for function: shortArray_concat. 
		 * @param list
		 * @return the SourceModule.expr representing an application of shortArray_concat
		 */
		public static final SourceModel.Expr shortArray_concat(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_concat), list});
		}

		/**
		 * Name binding for function: shortArray_concat.
		 * @see #shortArray_concat(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shortArray_concat = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"shortArray_concat");

		/**
		 * Helper binding method for function: shortArray_concatList. 
		 * @param list
		 * @return the SourceModule.expr representing an application of shortArray_concatList
		 */
		public static final SourceModel.Expr shortArray_concatList(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_concatList), list});
		}

		/**
		 * Name binding for function: shortArray_concatList.
		 * @see #shortArray_concatList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shortArray_concatList = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"shortArray_concatList");

		/**
		 * Helper binding method for function: shortArray_empty. 
		 * @return the SourceModule.expr representing an application of shortArray_empty
		 */
		public static final SourceModel.Expr shortArray_empty() {
			return SourceModel.Expr.Var.make(Functions.shortArray_empty);
		}

		/**
		 * Name binding for function: shortArray_empty.
		 * @see #shortArray_empty()
		 */
		public static final QualifiedName shortArray_empty = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"shortArray_empty");

		/**
		 * Helper binding method for function: shortArray_equals. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of shortArray_equals
		 */
		public static final SourceModel.Expr shortArray_equals(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_equals), array1, array2});
		}

		/**
		 * Name binding for function: shortArray_equals.
		 * @see #shortArray_equals(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shortArray_equals = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"shortArray_equals");

		/**
		 * Converts a Java array of CAL <code>Cal.Core.Prelude.Short</code>s to a Java array of primitive Java shorts.
		 * @param calValueArray (CAL type: <code>Cal.Collections.ArrayPrimitives.JCalValueArray</code>)
		 * @return (CAL type: <code>Cal.Collections.ArrayPrimitives.JShortArray</code>) 
		 */
		public static final SourceModel.Expr shortArray_fromCalValueArray(SourceModel.Expr calValueArray) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_fromCalValueArray), calValueArray});
		}

		/**
		 * Name binding for function: shortArray_fromCalValueArray.
		 * @see #shortArray_fromCalValueArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shortArray_fromCalValueArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"shortArray_fromCalValueArray");

		/**
		 * Helper binding method for function: shortArray_greaterThan. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of shortArray_greaterThan
		 */
		public static final SourceModel.Expr shortArray_greaterThan(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_greaterThan), array1, array2});
		}

		/**
		 * Name binding for function: shortArray_greaterThan.
		 * @see #shortArray_greaterThan(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shortArray_greaterThan = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"shortArray_greaterThan");

		/**
		 * Helper binding method for function: shortArray_greaterThanEquals. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of shortArray_greaterThanEquals
		 */
		public static final SourceModel.Expr shortArray_greaterThanEquals(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_greaterThanEquals), array1, array2});
		}

		/**
		 * Name binding for function: shortArray_greaterThanEquals.
		 * @see #shortArray_greaterThanEquals(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shortArray_greaterThanEquals = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"shortArray_greaterThanEquals");

		/**
		 * Helper binding method for function: shortArray_indexOf. 
		 * @param array
		 * @param element
		 * @return the SourceModule.expr representing an application of shortArray_indexOf
		 */
		public static final SourceModel.Expr shortArray_indexOf(SourceModel.Expr array, SourceModel.Expr element) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_indexOf), array, element});
		}

		/**
		 * @see #shortArray_indexOf(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param element
		 * @return the SourceModel.Expr representing an application of shortArray_indexOf
		 */
		public static final SourceModel.Expr shortArray_indexOf(SourceModel.Expr array, short element) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_indexOf), array, SourceModel.Expr.makeShortValue(element)});
		}

		/**
		 * Name binding for function: shortArray_indexOf.
		 * @see #shortArray_indexOf(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shortArray_indexOf = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"shortArray_indexOf");

		/**
		 * Helper binding method for function: shortArray_indexOf2. 
		 * @param array
		 * @param element
		 * @param fromIndex
		 * @return the SourceModule.expr representing an application of shortArray_indexOf2
		 */
		public static final SourceModel.Expr shortArray_indexOf2(SourceModel.Expr array, SourceModel.Expr element, SourceModel.Expr fromIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_indexOf2), array, element, fromIndex});
		}

		/**
		 * @see #shortArray_indexOf2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param element
		 * @param fromIndex
		 * @return the SourceModel.Expr representing an application of shortArray_indexOf2
		 */
		public static final SourceModel.Expr shortArray_indexOf2(SourceModel.Expr array, short element, int fromIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_indexOf2), array, SourceModel.Expr.makeShortValue(element), SourceModel.Expr.makeIntValue(fromIndex)});
		}

		/**
		 * Name binding for function: shortArray_indexOf2.
		 * @see #shortArray_indexOf2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shortArray_indexOf2 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"shortArray_indexOf2");

		/**
		 * Helper binding method for function: shortArray_isEmpty. 
		 * @param array
		 * @return the SourceModule.expr representing an application of shortArray_isEmpty
		 */
		public static final SourceModel.Expr shortArray_isEmpty(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_isEmpty), array});
		}

		/**
		 * Name binding for function: shortArray_isEmpty.
		 * @see #shortArray_isEmpty(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shortArray_isEmpty = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"shortArray_isEmpty");

		/**
		 * Helper binding method for function: shortArray_lastIndexOf. 
		 * @param array
		 * @param element
		 * @return the SourceModule.expr representing an application of shortArray_lastIndexOf
		 */
		public static final SourceModel.Expr shortArray_lastIndexOf(SourceModel.Expr array, SourceModel.Expr element) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_lastIndexOf), array, element});
		}

		/**
		 * @see #shortArray_lastIndexOf(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param element
		 * @return the SourceModel.Expr representing an application of shortArray_lastIndexOf
		 */
		public static final SourceModel.Expr shortArray_lastIndexOf(SourceModel.Expr array, short element) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_lastIndexOf), array, SourceModel.Expr.makeShortValue(element)});
		}

		/**
		 * Name binding for function: shortArray_lastIndexOf.
		 * @see #shortArray_lastIndexOf(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shortArray_lastIndexOf = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"shortArray_lastIndexOf");

		/**
		 * Helper binding method for function: shortArray_lastIndexOf2. 
		 * @param array
		 * @param element
		 * @param fromIndex
		 * @return the SourceModule.expr representing an application of shortArray_lastIndexOf2
		 */
		public static final SourceModel.Expr shortArray_lastIndexOf2(SourceModel.Expr array, SourceModel.Expr element, SourceModel.Expr fromIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_lastIndexOf2), array, element, fromIndex});
		}

		/**
		 * @see #shortArray_lastIndexOf2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param element
		 * @param fromIndex
		 * @return the SourceModel.Expr representing an application of shortArray_lastIndexOf2
		 */
		public static final SourceModel.Expr shortArray_lastIndexOf2(SourceModel.Expr array, short element, int fromIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_lastIndexOf2), array, SourceModel.Expr.makeShortValue(element), SourceModel.Expr.makeIntValue(fromIndex)});
		}

		/**
		 * Name binding for function: shortArray_lastIndexOf2.
		 * @see #shortArray_lastIndexOf2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shortArray_lastIndexOf2 = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"shortArray_lastIndexOf2");

		/**
		 * Helper binding method for function: shortArray_length. 
		 * @param array
		 * @return the SourceModule.expr representing an application of shortArray_length
		 */
		public static final SourceModel.Expr shortArray_length(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_length), array});
		}

		/**
		 * Name binding for function: shortArray_length.
		 * @see #shortArray_length(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shortArray_length = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"shortArray_length");

		/**
		 * Helper binding method for function: shortArray_lessThan. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of shortArray_lessThan
		 */
		public static final SourceModel.Expr shortArray_lessThan(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_lessThan), array1, array2});
		}

		/**
		 * Name binding for function: shortArray_lessThan.
		 * @see #shortArray_lessThan(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shortArray_lessThan = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"shortArray_lessThan");

		/**
		 * Helper binding method for function: shortArray_lessThanEquals. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of shortArray_lessThanEquals
		 */
		public static final SourceModel.Expr shortArray_lessThanEquals(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_lessThanEquals), array1, array2});
		}

		/**
		 * Name binding for function: shortArray_lessThanEquals.
		 * @see #shortArray_lessThanEquals(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shortArray_lessThanEquals = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"shortArray_lessThanEquals");

		/**
		 * Helper binding method for function: shortArray_listToArray. 
		 * @param list
		 * @return the SourceModule.expr representing an application of shortArray_listToArray
		 */
		public static final SourceModel.Expr shortArray_listToArray(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_listToArray), list});
		}

		/**
		 * Name binding for function: shortArray_listToArray.
		 * @see #shortArray_listToArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shortArray_listToArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"shortArray_listToArray");

		/**
		 * Helper binding method for function: shortArray_listToArrayWithFirstElement. 
		 * @param firstElement
		 * @param list
		 * @return the SourceModule.expr representing an application of shortArray_listToArrayWithFirstElement
		 */
		public static final SourceModel.Expr shortArray_listToArrayWithFirstElement(SourceModel.Expr firstElement, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_listToArrayWithFirstElement), firstElement, list});
		}

		/**
		 * @see #shortArray_listToArrayWithFirstElement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param firstElement
		 * @param list
		 * @return the SourceModel.Expr representing an application of shortArray_listToArrayWithFirstElement
		 */
		public static final SourceModel.Expr shortArray_listToArrayWithFirstElement(short firstElement, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_listToArrayWithFirstElement), SourceModel.Expr.makeShortValue(firstElement), list});
		}

		/**
		 * Name binding for function: shortArray_listToArrayWithFirstElement.
		 * @see #shortArray_listToArrayWithFirstElement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shortArray_listToArrayWithFirstElement = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"shortArray_listToArrayWithFirstElement");

		/**
		 * Helper binding method for function: shortArray_makeDefault. 
		 * @param size
		 * @return the SourceModule.expr representing an application of shortArray_makeDefault
		 */
		public static final SourceModel.Expr shortArray_makeDefault(SourceModel.Expr size) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_makeDefault), size});
		}

		/**
		 * @see #shortArray_makeDefault(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param size
		 * @return the SourceModel.Expr representing an application of shortArray_makeDefault
		 */
		public static final SourceModel.Expr shortArray_makeDefault(int size) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_makeDefault), SourceModel.Expr.makeIntValue(size)});
		}

		/**
		 * Name binding for function: shortArray_makeDefault.
		 * @see #shortArray_makeDefault(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shortArray_makeDefault = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"shortArray_makeDefault");

		/**
		 * Helper binding method for function: shortArray_max. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of shortArray_max
		 */
		public static final SourceModel.Expr shortArray_max(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_max), array1, array2});
		}

		/**
		 * Name binding for function: shortArray_max.
		 * @see #shortArray_max(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shortArray_max = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"shortArray_max");

		/**
		 * Helper binding method for function: shortArray_min. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of shortArray_min
		 */
		public static final SourceModel.Expr shortArray_min(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_min), array1, array2});
		}

		/**
		 * Name binding for function: shortArray_min.
		 * @see #shortArray_min(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shortArray_min = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"shortArray_min");

		/**
		 * Helper binding method for function: shortArray_notEquals. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of shortArray_notEquals
		 */
		public static final SourceModel.Expr shortArray_notEquals(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_notEquals), array1, array2});
		}

		/**
		 * Name binding for function: shortArray_notEquals.
		 * @see #shortArray_notEquals(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shortArray_notEquals = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"shortArray_notEquals");

		/**
		 * Helper binding method for function: shortArray_removeRange. 
		 * @param array
		 * @param fromIndex
		 * @param toIndex
		 * @return the SourceModule.expr representing an application of shortArray_removeRange
		 */
		public static final SourceModel.Expr shortArray_removeRange(SourceModel.Expr array, SourceModel.Expr fromIndex, SourceModel.Expr toIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_removeRange), array, fromIndex, toIndex});
		}

		/**
		 * @see #shortArray_removeRange(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param fromIndex
		 * @param toIndex
		 * @return the SourceModel.Expr representing an application of shortArray_removeRange
		 */
		public static final SourceModel.Expr shortArray_removeRange(SourceModel.Expr array, int fromIndex, int toIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_removeRange), array, SourceModel.Expr.makeIntValue(fromIndex), SourceModel.Expr.makeIntValue(toIndex)});
		}

		/**
		 * Name binding for function: shortArray_removeRange.
		 * @see #shortArray_removeRange(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shortArray_removeRange = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"shortArray_removeRange");

		/**
		 * Helper binding method for function: shortArray_replace. 
		 * @param array
		 * @param oldElementValue
		 * @param newElementValue
		 * @return the SourceModule.expr representing an application of shortArray_replace
		 */
		public static final SourceModel.Expr shortArray_replace(SourceModel.Expr array, SourceModel.Expr oldElementValue, SourceModel.Expr newElementValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_replace), array, oldElementValue, newElementValue});
		}

		/**
		 * @see #shortArray_replace(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param oldElementValue
		 * @param newElementValue
		 * @return the SourceModel.Expr representing an application of shortArray_replace
		 */
		public static final SourceModel.Expr shortArray_replace(SourceModel.Expr array, short oldElementValue, short newElementValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_replace), array, SourceModel.Expr.makeShortValue(oldElementValue), SourceModel.Expr.makeShortValue(newElementValue)});
		}

		/**
		 * Name binding for function: shortArray_replace.
		 * @see #shortArray_replace(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shortArray_replace = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"shortArray_replace");

		/**
		 * Helper binding method for function: shortArray_replicate. 
		 * @param nCopies
		 * @param valueToReplicate
		 * @return the SourceModule.expr representing an application of shortArray_replicate
		 */
		public static final SourceModel.Expr shortArray_replicate(SourceModel.Expr nCopies, SourceModel.Expr valueToReplicate) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_replicate), nCopies, valueToReplicate});
		}

		/**
		 * @see #shortArray_replicate(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param nCopies
		 * @param valueToReplicate
		 * @return the SourceModel.Expr representing an application of shortArray_replicate
		 */
		public static final SourceModel.Expr shortArray_replicate(int nCopies, short valueToReplicate) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_replicate), SourceModel.Expr.makeIntValue(nCopies), SourceModel.Expr.makeShortValue(valueToReplicate)});
		}

		/**
		 * Name binding for function: shortArray_replicate.
		 * @see #shortArray_replicate(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shortArray_replicate = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"shortArray_replicate");

		/**
		 * Helper binding method for function: shortArray_reverse. 
		 * @param array
		 * @return the SourceModule.expr representing an application of shortArray_reverse
		 */
		public static final SourceModel.Expr shortArray_reverse(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_reverse), array});
		}

		/**
		 * Name binding for function: shortArray_reverse.
		 * @see #shortArray_reverse(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shortArray_reverse = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"shortArray_reverse");

		/**
		 * Helper binding method for function: shortArray_sort. 
		 * @param array
		 * @return the SourceModule.expr representing an application of shortArray_sort
		 */
		public static final SourceModel.Expr shortArray_sort(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_sort), array});
		}

		/**
		 * Name binding for function: shortArray_sort.
		 * @see #shortArray_sort(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shortArray_sort = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"shortArray_sort");

		/**
		 * Helper binding method for function: shortArray_subArray. 
		 * @param array
		 * @param fromIndex
		 * @param toIndex
		 * @return the SourceModule.expr representing an application of shortArray_subArray
		 */
		public static final SourceModel.Expr shortArray_subArray(SourceModel.Expr array, SourceModel.Expr fromIndex, SourceModel.Expr toIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_subArray), array, fromIndex, toIndex});
		}

		/**
		 * @see #shortArray_subArray(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param fromIndex
		 * @param toIndex
		 * @return the SourceModel.Expr representing an application of shortArray_subArray
		 */
		public static final SourceModel.Expr shortArray_subArray(SourceModel.Expr array, int fromIndex, int toIndex) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_subArray), array, SourceModel.Expr.makeIntValue(fromIndex), SourceModel.Expr.makeIntValue(toIndex)});
		}

		/**
		 * Name binding for function: shortArray_subArray.
		 * @see #shortArray_subArray(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shortArray_subArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"shortArray_subArray");

		/**
		 * Helper binding method for function: shortArray_subscript. 
		 * @param array
		 * @param index
		 * @return the SourceModule.expr representing an application of shortArray_subscript
		 */
		public static final SourceModel.Expr shortArray_subscript(SourceModel.Expr array, SourceModel.Expr index) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_subscript), array, index});
		}

		/**
		 * @see #shortArray_subscript(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param index
		 * @return the SourceModel.Expr representing an application of shortArray_subscript
		 */
		public static final SourceModel.Expr shortArray_subscript(SourceModel.Expr array, int index) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_subscript), array, SourceModel.Expr.makeIntValue(index)});
		}

		/**
		 * Name binding for function: shortArray_subscript.
		 * @see #shortArray_subscript(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shortArray_subscript = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"shortArray_subscript");

		/**
		 * Converts a Java array of primitive Java shorts to a Java array of CAL <code>Cal.Core.Prelude.Short</code>s.
		 * @param array (CAL type: <code>Cal.Collections.ArrayPrimitives.JShortArray</code>)
		 * @return (CAL type: <code>Cal.Collections.ArrayPrimitives.JCalValueArray</code>) 
		 */
		public static final SourceModel.Expr shortArray_toCalValueArray(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_toCalValueArray), array});
		}

		/**
		 * Name binding for function: shortArray_toCalValueArray.
		 * @see #shortArray_toCalValueArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shortArray_toCalValueArray = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"shortArray_toCalValueArray");

		/**
		 * Helper binding method for function: shortArray_toShowString. 
		 * @param array
		 * @return the SourceModule.expr representing an application of shortArray_toShowString
		 */
		public static final SourceModel.Expr shortArray_toShowString(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_toShowString), array});
		}

		/**
		 * Name binding for function: shortArray_toShowString.
		 * @see #shortArray_toShowString(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shortArray_toShowString = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"shortArray_toShowString");

		/**
		 * Helper binding method for function: shortArray_update. 
		 * @param array
		 * @param index
		 * @param newValue
		 * @return the SourceModule.expr representing an application of shortArray_update
		 */
		public static final SourceModel.Expr shortArray_update(SourceModel.Expr array, SourceModel.Expr index, SourceModel.Expr newValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_update), array, index, newValue});
		}

		/**
		 * @see #shortArray_update(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param index
		 * @param newValue
		 * @return the SourceModel.Expr representing an application of shortArray_update
		 */
		public static final SourceModel.Expr shortArray_update(SourceModel.Expr array, int index, short newValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortArray_update), array, SourceModel.Expr.makeIntValue(index), SourceModel.Expr.makeShortValue(newValue)});
		}

		/**
		 * Name binding for function: shortArray_update.
		 * @see #shortArray_update(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shortArray_update = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"shortArray_update");

		/**
		 * similar to <code>Cal.Core.Prelude.intToOrdering</code> but only handles the intValues -1, 0, 1. This is an optimization for some well-used
		 * functions implemented in terms of Java primitives where the underlying Java primitive is known to return one of
		 * -1, 0, or 1.
		 * @param intValue (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Ordering</code>) 
		 */
		public static final SourceModel.Expr signumIntToOrdering(SourceModel.Expr intValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.signumIntToOrdering), intValue});
		}

		/**
		 * @see #signumIntToOrdering(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param intValue
		 * @return the SourceModel.Expr representing an application of signumIntToOrdering
		 */
		public static final SourceModel.Expr signumIntToOrdering(int intValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.signumIntToOrdering), SourceModel.Expr.makeIntValue(intValue)});
		}

		/**
		 * Name binding for function: signumIntToOrdering.
		 * @see #signumIntToOrdering(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName signumIntToOrdering = 
			QualifiedName.make(
				CAL_ArrayPrimitives_internal.MODULE_NAME, 
				"signumIntToOrdering");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = 776534279;

}
