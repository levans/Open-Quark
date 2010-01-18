/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_Array_internal.java)
 * was generated from CAL module: Cal.Collections.Array.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Collections.Array module from Java code.
 *  
 * Creation date: Fri Mar 16 13:11:57 PST 2007
 * --!>
 *  
 */

package org.openquark.cal.internal.module.Cal.Collections;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * Defines the abstract data type <code>Cal.Collections.Array.Array</code> along with a variety of functions and instances for the type.
 * <p>
 * <code>Cal.Collections.Array.Array</code> is a polymorphic, immutable (i.e. purely functional) array type.
 * It is strict in its element values i.e. when an <code>Cal.Collections.Array.Array a</code> value is
 * created, all elements of the array are evaluated to weak-head normal form.
 * <p>
 * <code>Cal.Collections.Array.Array</code> offers constant time access to its elements. In addition,
 * <code>Cal.Collections.Array.Array</code> uses an unboxed representation of elements whenever possible. So for example, for
 * <code>Cal.Collections.Array.Array Cal.Core.Prelude.Int</code>, the underlying representation is the Java primitive array
 * <code>[int]</code>. Also, for an array of Java foreign objects, such as
 * <code>Cal.Collections.Array.Array Cal.Core.Prelude.String</code>, the underlying representation is a Java array
 * <code>[java.lang.String]</code>, rather than an array of CAL internal values holding onto java.lang.Strings
 * i.e. it is more space efficient.
 * 
 * @author Bo Ilic
 */
public final class CAL_Array_internal {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Collections.Array");

	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Collections.Array module.
	 */
	public static final class TypeConstructors {
		/**
		 * An internal implementation type. This is useful for functions that construct new arrays that are the same
		 * element type as another array, but without adding a Typeable constraint to the function.
		 */
		public static final QualifiedName ElementType = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "ElementType");

	}
	/**
	 * This inner class (DataConstructors) contains constants
	 * and methods related to binding to CAL DataConstructors in the Cal.Collections.Array module.
	 */
	public static final class DataConstructors {
		/*
		 * DataConstructors for the Cal.Collections.Array.Array data type.
		 */

		/**
		 * Used for arrays of values of CAL foreign types having Java implementation type "char"   
		 * such as the <code>Cal.Core.Prelude.Char</code> type. Cannot be an empty array.
		 * @param values (CAL type: <code>Cal.Collections.ArrayPrimitives.JCharArray</code>)
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr CharArray(SourceModel.Expr values) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.CharArray), values});
		}

		/**
		 * Name binding for DataConstructor: Cal.Collections.Array.CharArray.
		 * @see #CharArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName CharArray = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "CharArray");

		/**
		 * Ordinal of DataConstructor Cal.Collections.Array.CharArray.
		 * @see #CharArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int CharArray_ordinal = 0;

		/**
		 * Used for arrays of values of CAL foreign types having Java implementation type "boolean".
		 * Also used for arrays of values of the <code>Cal.Core.Prelude.Boolean</code> type. Cannot be an empty array.
		 * @param values (CAL type: <code>Cal.Collections.ArrayPrimitives.JBooleanArray</code>)
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr BooleanArray(SourceModel.Expr values) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.BooleanArray), values});
		}

		/**
		 * Name binding for DataConstructor: Cal.Collections.Array.BooleanArray.
		 * @see #BooleanArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName BooleanArray = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "BooleanArray");

		/**
		 * Ordinal of DataConstructor Cal.Collections.Array.BooleanArray.
		 * @see #BooleanArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int BooleanArray_ordinal = 1;

		/**
		 * Used for arrays of values of CAL foreign types having Java implementation type "byte"   
		 * such as the <code>Cal.Core.Prelude.Byte</code> type. Cannot be an empty array.
		 * @param values (CAL type: <code>Cal.Collections.ArrayPrimitives.JByteArray</code>)
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr ByteArray(SourceModel.Expr values) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.ByteArray), values});
		}

		/**
		 * Name binding for DataConstructor: Cal.Collections.Array.ByteArray.
		 * @see #ByteArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName ByteArray = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "ByteArray");

		/**
		 * Ordinal of DataConstructor Cal.Collections.Array.ByteArray.
		 * @see #ByteArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int ByteArray_ordinal = 2;

		/**
		 * Used for arrays of values of CAL foreign types having Java implementation type "short"   
		 * such as the <code>Cal.Core.Prelude.Short</code> type. Cannot be an empty array.
		 * @param values (CAL type: <code>Cal.Collections.ArrayPrimitives.JShortArray</code>)
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr ShortArray(SourceModel.Expr values) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.ShortArray), values});
		}

		/**
		 * Name binding for DataConstructor: Cal.Collections.Array.ShortArray.
		 * @see #ShortArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName ShortArray = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "ShortArray");

		/**
		 * Ordinal of DataConstructor Cal.Collections.Array.ShortArray.
		 * @see #ShortArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int ShortArray_ordinal = 3;

		/**
		 * Used for arrays of values of CAL foreign types having Java implementation type "int"   
		 * such as the <code>Cal.Core.Prelude.Int</code> type. Also used for arrays of values of CAL enumerated types
		 * (these are non-parametric algebraic types all of whose data constructors have 0-arity with the
		 * exception of the Prelude.Boolean type). Cannot be an empty array.
		 * @param values (CAL type: <code>Cal.Collections.ArrayPrimitives.JIntArray</code>)
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr IntArray(SourceModel.Expr values) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.IntArray), values});
		}

		/**
		 * Name binding for DataConstructor: Cal.Collections.Array.IntArray.
		 * @see #IntArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName IntArray = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "IntArray");

		/**
		 * Ordinal of DataConstructor Cal.Collections.Array.IntArray.
		 * @see #IntArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int IntArray_ordinal = 4;

		/**
		 * Used for arrays of values of CAL foreign types having Java implementation type "short"   
		 * such as the <code>Cal.Core.Prelude.Short</code> type. Cannot be an empty array.
		 * @param values (CAL type: <code>Cal.Collections.ArrayPrimitives.JLongArray</code>)
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr LongArray(SourceModel.Expr values) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.LongArray), values});
		}

		/**
		 * Name binding for DataConstructor: Cal.Collections.Array.LongArray.
		 * @see #LongArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName LongArray = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "LongArray");

		/**
		 * Ordinal of DataConstructor Cal.Collections.Array.LongArray.
		 * @see #LongArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int LongArray_ordinal = 5;

		/**
		 * Used for arrays of values of CAL foreign types having Java implementation type "float"   
		 * such as the <code>Cal.Core.Prelude.Float</code> type. Cannot be an empty array.
		 * @param values (CAL type: <code>Cal.Collections.ArrayPrimitives.JFloatArray</code>)
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr FloatArray(SourceModel.Expr values) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.FloatArray), values});
		}

		/**
		 * Name binding for DataConstructor: Cal.Collections.Array.FloatArray.
		 * @see #FloatArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName FloatArray = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "FloatArray");

		/**
		 * Ordinal of DataConstructor Cal.Collections.Array.FloatArray.
		 * @see #FloatArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int FloatArray_ordinal = 6;

		/**
		 * Used for arrays of values of CAL foreign types having Java implementation type "double"   
		 * such as the <code>Cal.Core.Prelude.Double</code> type. Cannot be an empty array.
		 * @param values (CAL type: <code>Cal.Collections.ArrayPrimitives.JDoubleArray</code>)
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr DoubleArray(SourceModel.Expr values) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.DoubleArray), values});
		}

		/**
		 * Name binding for DataConstructor: Cal.Collections.Array.DoubleArray.
		 * @see #DoubleArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName DoubleArray = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "DoubleArray");

		/**
		 * Ordinal of DataConstructor Cal.Collections.Array.DoubleArray.
		 * @see #DoubleArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int DoubleArray_ordinal = 7;

		/**
		 * Used for arrays of values of CAL foreign reference types. Note that this does not include foreign primitive
		 * types such as "int". Cannot be an empty array.
		 * @param values (CAL type: <code>Cal.Collections.ArrayPrimitives.JObjectArray</code>)
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr ObjectArray(SourceModel.Expr values) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.ObjectArray), values});
		}

		/**
		 * Name binding for DataConstructor: Cal.Collections.Array.ObjectArray.
		 * @see #ObjectArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName ObjectArray = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "ObjectArray");

		/**
		 * Ordinal of DataConstructor Cal.Collections.Array.ObjectArray.
		 * @see #ObjectArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int ObjectArray_ordinal = 8;

		/**
		 * Used for arrays of values of CAL types other than the ones above. These do not have a direct representation as
		 * Java objects or values of a Java primitive type. Cannot be an empty array.
		 * @param values (CAL type: <code>Cal.Collections.ArrayPrimitives.JCalValueArray</code>)
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr CalValueArray(SourceModel.Expr values) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.CalValueArray), values});
		}

		/**
		 * Name binding for DataConstructor: Cal.Collections.Array.CalValueArray.
		 * @see #CalValueArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName CalValueArray = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "CalValueArray");

		/**
		 * Ordinal of DataConstructor Cal.Collections.Array.CalValueArray.
		 * @see #CalValueArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int CalValueArray_ordinal = 9;

		/**
		 * Used for an empty array.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr EmptyArray() {
			return SourceModel.Expr.DataCons.make(DataConstructors.EmptyArray);
		}

		/**
		 * Name binding for DataConstructor: Cal.Collections.Array.EmptyArray.
		 * @see #EmptyArray()
		 */
		public static final QualifiedName EmptyArray = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "EmptyArray");

		/**
		 * Ordinal of DataConstructor Cal.Collections.Array.EmptyArray.
		 * @see #EmptyArray()
		 */
		public static final int EmptyArray_ordinal = 10;

		/*
		 * DataConstructors for the Cal.Collections.Array.ElementType data type.
		 */

		/**
		 * Binding for DataConstructor: Cal.Collections.Array.CharType.
		 * @return the SourceModule.Expr representing an application of Cal.Collections.Array.CharType
		 */
		public static final SourceModel.Expr CharType() {
			return SourceModel.Expr.DataCons.make(DataConstructors.CharType);
		}

		/**
		 * Name binding for DataConstructor: Cal.Collections.Array.CharType.
		 * @see #CharType()
		 */
		public static final QualifiedName CharType = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "CharType");

		/**
		 * Ordinal of DataConstructor Cal.Collections.Array.CharType.
		 * @see #CharType()
		 */
		public static final int CharType_ordinal = 0;

		/**
		 * Binding for DataConstructor: Cal.Collections.Array.BooleanType.
		 * @return the SourceModule.Expr representing an application of Cal.Collections.Array.BooleanType
		 */
		public static final SourceModel.Expr BooleanType() {
			return SourceModel.Expr.DataCons.make(DataConstructors.BooleanType);
		}

		/**
		 * Name binding for DataConstructor: Cal.Collections.Array.BooleanType.
		 * @see #BooleanType()
		 */
		public static final QualifiedName BooleanType = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "BooleanType");

		/**
		 * Ordinal of DataConstructor Cal.Collections.Array.BooleanType.
		 * @see #BooleanType()
		 */
		public static final int BooleanType_ordinal = 1;

		/**
		 * Binding for DataConstructor: Cal.Collections.Array.ByteType.
		 * @return the SourceModule.Expr representing an application of Cal.Collections.Array.ByteType
		 */
		public static final SourceModel.Expr ByteType() {
			return SourceModel.Expr.DataCons.make(DataConstructors.ByteType);
		}

		/**
		 * Name binding for DataConstructor: Cal.Collections.Array.ByteType.
		 * @see #ByteType()
		 */
		public static final QualifiedName ByteType = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "ByteType");

		/**
		 * Ordinal of DataConstructor Cal.Collections.Array.ByteType.
		 * @see #ByteType()
		 */
		public static final int ByteType_ordinal = 2;

		/**
		 * Binding for DataConstructor: Cal.Collections.Array.ShortType.
		 * @return the SourceModule.Expr representing an application of Cal.Collections.Array.ShortType
		 */
		public static final SourceModel.Expr ShortType() {
			return SourceModel.Expr.DataCons.make(DataConstructors.ShortType);
		}

		/**
		 * Name binding for DataConstructor: Cal.Collections.Array.ShortType.
		 * @see #ShortType()
		 */
		public static final QualifiedName ShortType = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "ShortType");

		/**
		 * Ordinal of DataConstructor Cal.Collections.Array.ShortType.
		 * @see #ShortType()
		 */
		public static final int ShortType_ordinal = 3;

		/**
		 * Binding for DataConstructor: Cal.Collections.Array.IntType.
		 * @return the SourceModule.Expr representing an application of Cal.Collections.Array.IntType
		 */
		public static final SourceModel.Expr IntType() {
			return SourceModel.Expr.DataCons.make(DataConstructors.IntType);
		}

		/**
		 * Name binding for DataConstructor: Cal.Collections.Array.IntType.
		 * @see #IntType()
		 */
		public static final QualifiedName IntType = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "IntType");

		/**
		 * Ordinal of DataConstructor Cal.Collections.Array.IntType.
		 * @see #IntType()
		 */
		public static final int IntType_ordinal = 4;

		/**
		 * Binding for DataConstructor: Cal.Collections.Array.LongType.
		 * @return the SourceModule.Expr representing an application of Cal.Collections.Array.LongType
		 */
		public static final SourceModel.Expr LongType() {
			return SourceModel.Expr.DataCons.make(DataConstructors.LongType);
		}

		/**
		 * Name binding for DataConstructor: Cal.Collections.Array.LongType.
		 * @see #LongType()
		 */
		public static final QualifiedName LongType = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "LongType");

		/**
		 * Ordinal of DataConstructor Cal.Collections.Array.LongType.
		 * @see #LongType()
		 */
		public static final int LongType_ordinal = 5;

		/**
		 * Binding for DataConstructor: Cal.Collections.Array.FloatType.
		 * @return the SourceModule.Expr representing an application of Cal.Collections.Array.FloatType
		 */
		public static final SourceModel.Expr FloatType() {
			return SourceModel.Expr.DataCons.make(DataConstructors.FloatType);
		}

		/**
		 * Name binding for DataConstructor: Cal.Collections.Array.FloatType.
		 * @see #FloatType()
		 */
		public static final QualifiedName FloatType = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "FloatType");

		/**
		 * Ordinal of DataConstructor Cal.Collections.Array.FloatType.
		 * @see #FloatType()
		 */
		public static final int FloatType_ordinal = 6;

		/**
		 * Binding for DataConstructor: Cal.Collections.Array.DoubleType.
		 * @return the SourceModule.Expr representing an application of Cal.Collections.Array.DoubleType
		 */
		public static final SourceModel.Expr DoubleType() {
			return SourceModel.Expr.DataCons.make(DataConstructors.DoubleType);
		}

		/**
		 * Name binding for DataConstructor: Cal.Collections.Array.DoubleType.
		 * @see #DoubleType()
		 */
		public static final QualifiedName DoubleType = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "DoubleType");

		/**
		 * Ordinal of DataConstructor Cal.Collections.Array.DoubleType.
		 * @see #DoubleType()
		 */
		public static final int DoubleType_ordinal = 7;

		/**
		 * Binding for DataConstructor: Cal.Collections.Array.ObjectType.
		 * @return the SourceModule.Expr representing an application of Cal.Collections.Array.ObjectType
		 */
		public static final SourceModel.Expr ObjectType() {
			return SourceModel.Expr.DataCons.make(DataConstructors.ObjectType);
		}

		/**
		 * Name binding for DataConstructor: Cal.Collections.Array.ObjectType.
		 * @see #ObjectType()
		 */
		public static final QualifiedName ObjectType = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "ObjectType");

		/**
		 * Ordinal of DataConstructor Cal.Collections.Array.ObjectType.
		 * @see #ObjectType()
		 */
		public static final int ObjectType_ordinal = 8;

		/**
		 * Binding for DataConstructor: Cal.Collections.Array.CalValueType.
		 * @return the SourceModule.Expr representing an application of Cal.Collections.Array.CalValueType
		 */
		public static final SourceModel.Expr CalValueType() {
			return SourceModel.Expr.DataCons.make(DataConstructors.CalValueType);
		}

		/**
		 * Name binding for DataConstructor: Cal.Collections.Array.CalValueType.
		 * @see #CalValueType()
		 */
		public static final QualifiedName CalValueType = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "CalValueType");

		/**
		 * Ordinal of DataConstructor Cal.Collections.Array.CalValueType.
		 * @see #CalValueType()
		 */
		public static final int CalValueType_ordinal = 9;

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Collections.Array module.
	 */
	public static final class Functions {
		/**
		 * Helper binding method for function: allExamples. 
		 * @return the SourceModule.expr representing an application of allExamples
		 */
		public static final SourceModel.Expr allExamples() {
			return SourceModel.Expr.Var.make(Functions.allExamples);
		}

		/**
		 * Name binding for function: allExamples.
		 * @see #allExamples()
		 */
		public static final QualifiedName allExamples = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "allExamples");

		/**
		 * Helper binding method for function: alternateExamples. 
		 * @return the SourceModule.expr representing an application of alternateExamples
		 */
		public static final SourceModel.Expr alternateExamples() {
			return SourceModel.Expr.Var.make(Functions.alternateExamples);
		}

		/**
		 * Name binding for function: alternateExamples.
		 * @see #alternateExamples()
		 */
		public static final QualifiedName alternateExamples = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"alternateExamples");

		/**
		 * Helper binding method for function: andArrayExamples. 
		 * @return the SourceModule.expr representing an application of andArrayExamples
		 */
		public static final SourceModel.Expr andArrayExamples() {
			return SourceModel.Expr.Var.make(Functions.andArrayExamples);
		}

		/**
		 * Name binding for function: andArrayExamples.
		 * @see #andArrayExamples()
		 */
		public static final QualifiedName andArrayExamples = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"andArrayExamples");

		/**
		 * Helper binding method for function: anyExamples. 
		 * @return the SourceModule.expr representing an application of anyExamples
		 */
		public static final SourceModel.Expr anyExamples() {
			return SourceModel.Expr.Var.make(Functions.anyExamples);
		}

		/**
		 * Name binding for function: anyExamples.
		 * @see #anyExamples()
		 */
		public static final QualifiedName anyExamples = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "anyExamples");

		/**
		 * Helper binding method for function: appendArray. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of appendArray
		 */
		public static final SourceModel.Expr appendArray(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.appendArray), array1, array2});
		}

		/**
		 * Name binding for function: appendArray.
		 * @see #appendArray(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName appendArray = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "appendArray");

		/**
		 * Helper binding method for function: appendExamples. 
		 * @return the SourceModule.expr representing an application of appendExamples
		 */
		public static final SourceModel.Expr appendExamples() {
			return SourceModel.Expr.Var.make(Functions.appendExamples);
		}

		/**
		 * Name binding for function: appendExamples.
		 * @see #appendExamples()
		 */
		public static final QualifiedName appendExamples = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "appendExamples");

		/**
		 * Helper binding method for function: arbitraryArray. 
		 * @return the SourceModule.expr representing an application of arbitraryArray
		 */
		public static final SourceModel.Expr arbitraryArray() {
			return SourceModel.Expr.Var.make(Functions.arbitraryArray);
		}

		/**
		 * Name binding for function: arbitraryArray.
		 * @see #arbitraryArray()
		 */
		public static final QualifiedName arbitraryArray = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "arbitraryArray");

		/**
		 * Helper binding method for function: array1Internal. 
		 * @param elementType
		 * @param item
		 * @return the SourceModule.expr representing an application of array1Internal
		 */
		public static final SourceModel.Expr array1Internal(SourceModel.Expr elementType, SourceModel.Expr item) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.array1Internal), elementType, item});
		}

		/**
		 * Name binding for function: array1Internal.
		 * @see #array1Internal(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName array1Internal = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "array1Internal");

		/**
		 * Helper binding method for function: array2Internal. 
		 * @param elementType
		 * @param item1
		 * @param item2
		 * @return the SourceModule.expr representing an application of array2Internal
		 */
		public static final SourceModel.Expr array2Internal(SourceModel.Expr elementType, SourceModel.Expr item1, SourceModel.Expr item2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.array2Internal), elementType, item1, item2});
		}

		/**
		 * Name binding for function: array2Internal.
		 * @see #array2Internal(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName array2Internal = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "array2Internal");

		/**
		 * Helper binding method for function: array3Internal. 
		 * @param elementType
		 * @param item1
		 * @param item2
		 * @param item3
		 * @return the SourceModule.expr representing an application of array3Internal
		 */
		public static final SourceModel.Expr array3Internal(SourceModel.Expr elementType, SourceModel.Expr item1, SourceModel.Expr item2, SourceModel.Expr item3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.array3Internal), elementType, item1, item2, item3});
		}

		/**
		 * Name binding for function: array3Internal.
		 * @see #array3Internal(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName array3Internal = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "array3Internal");

		/**
		 * Helper binding method for function: array4Internal. 
		 * @param elementType
		 * @param item1
		 * @param item2
		 * @param item3
		 * @param item4
		 * @return the SourceModule.expr representing an application of array4Internal
		 */
		public static final SourceModel.Expr array4Internal(SourceModel.Expr elementType, SourceModel.Expr item1, SourceModel.Expr item2, SourceModel.Expr item3, SourceModel.Expr item4) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.array4Internal), elementType, item1, item2, item3, item4});
		}

		/**
		 * Name binding for function: array4Internal.
		 * @see #array4Internal(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName array4Internal = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "array4Internal");

		/**
		 * Helper binding method for function: array5Internal. 
		 * @param elementType
		 * @param item1
		 * @param item2
		 * @param item3
		 * @param item4
		 * @param item5
		 * @return the SourceModule.expr representing an application of array5Internal
		 */
		public static final SourceModel.Expr array5Internal(SourceModel.Expr elementType, SourceModel.Expr item1, SourceModel.Expr item2, SourceModel.Expr item3, SourceModel.Expr item4, SourceModel.Expr item5) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.array5Internal), elementType, item1, item2, item3, item4, item5});
		}

		/**
		 * Name binding for function: array5Internal.
		 * @see #array5Internal(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName array5Internal = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "array5Internal");

		/**
		 * Helper binding method for function: array6Internal. 
		 * @param elementType
		 * @param item1
		 * @param item2
		 * @param item3
		 * @param item4
		 * @param item5
		 * @param item6
		 * @return the SourceModule.expr representing an application of array6Internal
		 */
		public static final SourceModel.Expr array6Internal(SourceModel.Expr elementType, SourceModel.Expr item1, SourceModel.Expr item2, SourceModel.Expr item3, SourceModel.Expr item4, SourceModel.Expr item5, SourceModel.Expr item6) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.array6Internal), elementType, item1, item2, item3, item4, item5, item6});
		}

		/**
		 * Name binding for function: array6Internal.
		 * @see #array6Internal(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName array6Internal = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "array6Internal");

		/**
		 * Helper binding method for function: array7Internal. 
		 * @param elementType
		 * @param item1
		 * @param item2
		 * @param item3
		 * @param item4
		 * @param item5
		 * @param item6
		 * @param item7
		 * @return the SourceModule.expr representing an application of array7Internal
		 */
		public static final SourceModel.Expr array7Internal(SourceModel.Expr elementType, SourceModel.Expr item1, SourceModel.Expr item2, SourceModel.Expr item3, SourceModel.Expr item4, SourceModel.Expr item5, SourceModel.Expr item6, SourceModel.Expr item7) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.array7Internal), elementType, item1, item2, item3, item4, item5, item6, item7});
		}

		/**
		 * Name binding for function: array7Internal.
		 * @see #array7Internal(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName array7Internal = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "array7Internal");

		/**
		 * Helper binding method for function: binarySearchByExamples. 
		 * @return the SourceModule.expr representing an application of binarySearchByExamples
		 */
		public static final SourceModel.Expr binarySearchByExamples() {
			return SourceModel.Expr.Var.make(Functions.binarySearchByExamples);
		}

		/**
		 * Name binding for function: binarySearchByExamples.
		 * @see #binarySearchByExamples()
		 */
		public static final QualifiedName binarySearchByExamples = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"binarySearchByExamples");

		/**
		 * Helper binding method for function: binarySearchExamples. 
		 * @return the SourceModule.expr representing an application of binarySearchExamples
		 */
		public static final SourceModel.Expr binarySearchExamples() {
			return SourceModel.Expr.Var.make(Functions.binarySearchExamples);
		}

		/**
		 * Name binding for function: binarySearchExamples.
		 * @see #binarySearchExamples()
		 */
		public static final QualifiedName binarySearchExamples = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"binarySearchExamples");

		/**
		 * Searches the array for the specified value using the binary search algorithm. The array must be
		 * sorted (via the ordering defined by <code>Cal.Core.Prelude.compare</code>) for the result to be well-defined. 
		 * If the array contains multiple elements that are equal to the specified value, there is no guarantee
		 * which specific one will be found.
		 * <p>
		 * If the array does not contain the specified value, a negative value will be returned. This negative value
		 * is equal to <code>(-insertionPoint - 1)</code> where <code>insertionPoint</code> is the point at which the value would need to
		 * be inserted into the array to maintain a sorted array.
		 * <p>
		 * This should yield better performance for primitive arrays then the <code>Cal.Collections.Array.binarySearch</code> function.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.Array.binarySearch, Cal.Collections.Array.binarySearchBy
		 * </dl>
		 * 
		 * @param array (CAL type: <code>(Cal.Core.Prelude.Ord a, Cal.Core.Prelude.Typeable a) => Cal.Collections.Array.Array a</code>)
		 *          sorted array to search
		 * @param value (CAL type: <code>(Cal.Core.Prelude.Ord a, Cal.Core.Prelude.Typeable a) => a</code>)
		 *          the value to search for
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the index of value in the array, or a negative value if value does not occur in the array.
		 */
		public static final SourceModel.Expr binarySearchPrimitive(SourceModel.Expr array, SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.binarySearchPrimitive), array, value});
		}

		/**
		 * Name binding for function: binarySearchPrimitive.
		 * @see #binarySearchPrimitive(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName binarySearchPrimitive = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"binarySearchPrimitive");

		/**
		 * Helper binding method for function: breakAfterExamples. 
		 * @return the SourceModule.expr representing an application of breakAfterExamples
		 */
		public static final SourceModel.Expr breakAfterExamples() {
			return SourceModel.Expr.Var.make(Functions.breakAfterExamples);
		}

		/**
		 * Name binding for function: breakAfterExamples.
		 * @see #breakAfterExamples()
		 */
		public static final QualifiedName breakAfterExamples = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"breakAfterExamples");

		/**
		 * Helper binding method for function: breakExamples. 
		 * @return the SourceModule.expr representing an application of breakExamples
		 */
		public static final SourceModel.Expr breakExamples() {
			return SourceModel.Expr.Var.make(Functions.breakExamples);
		}

		/**
		 * Name binding for function: breakExamples.
		 * @see #breakExamples()
		 */
		public static final QualifiedName breakExamples = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "breakExamples");

		/**
		 * Helper binding method for function: chopExamples. 
		 * @return the SourceModule.expr representing an application of chopExamples
		 */
		public static final SourceModel.Expr chopExamples() {
			return SourceModel.Expr.Var.make(Functions.chopExamples);
		}

		/**
		 * Name binding for function: chopExamples.
		 * @see #chopExamples()
		 */
		public static final QualifiedName chopExamples = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "chopExamples");

		/**
		 * Helper binding method for function: coarbitraryArray. 
		 * @param array
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of coarbitraryArray
		 */
		public static final SourceModel.Expr coarbitraryArray(SourceModel.Expr array, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.coarbitraryArray), array, arg_2});
		}

		/**
		 * Name binding for function: coarbitraryArray.
		 * @see #coarbitraryArray(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName coarbitraryArray = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"coarbitraryArray");

		/**
		 * Helper binding method for function: compareArray. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of compareArray
		 */
		public static final SourceModel.Expr compareArray(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.compareArray), array1, array2});
		}

		/**
		 * Name binding for function: compareArray.
		 * @see #compareArray(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName compareArray = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "compareArray");

		/**
		 * Returns an <code>Cal.Core.Prelude.Ordering</code> based on how the first array compares to the second array.
		 * <p>
		 * This should yield better performance for primitive arrays then the <code>Cal.Core.Prelude.compare</code> class method for
		 * <code>Cal.Collections.Array.Array</code>.
		 * 
		 * @param array1 (CAL type: <code>(Cal.Core.Prelude.Ord a, Cal.Core.Prelude.Typeable a) => Cal.Collections.Array.Array a</code>)
		 *          the first argument to compare.
		 * @param array2 (CAL type: <code>(Cal.Core.Prelude.Ord a, Cal.Core.Prelude.Typeable a) => Cal.Collections.Array.Array a</code>)
		 *          the second argument to compare.
		 * @return (CAL type: <code>Cal.Core.Prelude.Ordering</code>) 
		 *          <code>Cal.Core.Prelude.LT</code>, <code>Cal.Core.Prelude.EQ</code>, or <code>Cal.Core.Prelude.GT</code> if <code>array1</code> is respectively less than, equal to, or greater than
		 * <code>array2</code>.
		 */
		public static final SourceModel.Expr comparePrimitive(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.comparePrimitive), array1, array2});
		}

		/**
		 * Name binding for function: comparePrimitive.
		 * @see #comparePrimitive(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName comparePrimitive = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"comparePrimitive");

		/**
		 * Helper binding method for function: comparePrimitiveExamples. 
		 * @return the SourceModule.expr representing an application of comparePrimitiveExamples
		 */
		public static final SourceModel.Expr comparePrimitiveExamples() {
			return SourceModel.Expr.Var.make(Functions.comparePrimitiveExamples);
		}

		/**
		 * Name binding for function: comparePrimitiveExamples.
		 * @see #comparePrimitiveExamples()
		 */
		public static final QualifiedName comparePrimitiveExamples = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"comparePrimitiveExamples");

		/**
		 * Helper binding method for function: concatArray. 
		 * @param listOfArrays
		 * @return the SourceModule.expr representing an application of concatArray
		 */
		public static final SourceModel.Expr concatArray(SourceModel.Expr listOfArrays) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.concatArray), listOfArrays});
		}

		/**
		 * Name binding for function: concatArray.
		 * @see #concatArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName concatArray = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "concatArray");

		/**
		 * Helper binding method for function: concatArrayExamples. 
		 * @return the SourceModule.expr representing an application of concatArrayExamples
		 */
		public static final SourceModel.Expr concatArrayExamples() {
			return SourceModel.Expr.Var.make(Functions.concatArrayExamples);
		}

		/**
		 * Name binding for function: concatArrayExamples.
		 * @see #concatArrayExamples()
		 */
		public static final QualifiedName concatArrayExamples = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"concatArrayExamples");

		/**
		 * Helper binding method for function: concatExamples. 
		 * @return the SourceModule.expr representing an application of concatExamples
		 */
		public static final SourceModel.Expr concatExamples() {
			return SourceModel.Expr.Var.make(Functions.concatExamples);
		}

		/**
		 * Name binding for function: concatExamples.
		 * @see #concatExamples()
		 */
		public static final QualifiedName concatExamples = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "concatExamples");

		/**
		 * Helper binding method for function: concatNonEmptyArrayInternal. 
		 * @param elementType
		 * @param listOfNonEmptyArrays
		 * @return the SourceModule.expr representing an application of concatNonEmptyArrayInternal
		 */
		public static final SourceModel.Expr concatNonEmptyArrayInternal(SourceModel.Expr elementType, SourceModel.Expr listOfNonEmptyArrays) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.concatNonEmptyArrayInternal), elementType, listOfNonEmptyArrays});
		}

		/**
		 * Name binding for function: concatNonEmptyArrayInternal.
		 * @see #concatNonEmptyArrayInternal(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName concatNonEmptyArrayInternal = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"concatNonEmptyArrayInternal");

		/**
		 * Helper binding method for function: deleteAtExamples. 
		 * @return the SourceModule.expr representing an application of deleteAtExamples
		 */
		public static final SourceModel.Expr deleteAtExamples() {
			return SourceModel.Expr.Var.make(Functions.deleteAtExamples);
		}

		/**
		 * Name binding for function: deleteAtExamples.
		 * @see #deleteAtExamples()
		 */
		public static final QualifiedName deleteAtExamples = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"deleteAtExamples");

		/**
		 * Helper binding method for function: deleteByExamples. 
		 * @return the SourceModule.expr representing an application of deleteByExamples
		 */
		public static final SourceModel.Expr deleteByExamples() {
			return SourceModel.Expr.Var.make(Functions.deleteByExamples);
		}

		/**
		 * Name binding for function: deleteByExamples.
		 * @see #deleteByExamples()
		 */
		public static final QualifiedName deleteByExamples = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"deleteByExamples");

		/**
		 * Helper binding method for function: deleteExamples. 
		 * @return the SourceModule.expr representing an application of deleteExamples
		 */
		public static final SourceModel.Expr deleteExamples() {
			return SourceModel.Expr.Var.make(Functions.deleteExamples);
		}

		/**
		 * Name binding for function: deleteExamples.
		 * @see #deleteExamples()
		 */
		public static final QualifiedName deleteExamples = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "deleteExamples");

		/**
		 * Helper binding method for function: deleteRangeExamples. 
		 * @return the SourceModule.expr representing an application of deleteRangeExamples
		 */
		public static final SourceModel.Expr deleteRangeExamples() {
			return SourceModel.Expr.Var.make(Functions.deleteRangeExamples);
		}

		/**
		 * Name binding for function: deleteRangeExamples.
		 * @see #deleteRangeExamples()
		 */
		public static final QualifiedName deleteRangeExamples = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"deleteRangeExamples");

		/**
		 * Helper binding method for function: dropExamples. 
		 * @return the SourceModule.expr representing an application of dropExamples
		 */
		public static final SourceModel.Expr dropExamples() {
			return SourceModel.Expr.Var.make(Functions.dropExamples);
		}

		/**
		 * Name binding for function: dropExamples.
		 * @see #dropExamples()
		 */
		public static final QualifiedName dropExamples = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "dropExamples");

		/**
		 * Helper binding method for function: dropWhileExamples. 
		 * @return the SourceModule.expr representing an application of dropWhileExamples
		 */
		public static final SourceModel.Expr dropWhileExamples() {
			return SourceModel.Expr.Var.make(Functions.dropWhileExamples);
		}

		/**
		 * Name binding for function: dropWhileExamples.
		 * @see #dropWhileExamples()
		 */
		public static final QualifiedName dropWhileExamples = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"dropWhileExamples");

		/**
		 * Helper binding method for function: elemIndexExamples. 
		 * @return the SourceModule.expr representing an application of elemIndexExamples
		 */
		public static final SourceModel.Expr elemIndexExamples() {
			return SourceModel.Expr.Var.make(Functions.elemIndexExamples);
		}

		/**
		 * Name binding for function: elemIndexExamples.
		 * @see #elemIndexExamples()
		 */
		public static final QualifiedName elemIndexExamples = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"elemIndexExamples");

		/**
		 * Helper binding method for function: elemIndicesExamples. 
		 * @return the SourceModule.expr representing an application of elemIndicesExamples
		 */
		public static final SourceModel.Expr elemIndicesExamples() {
			return SourceModel.Expr.Var.make(Functions.elemIndicesExamples);
		}

		/**
		 * Name binding for function: elemIndicesExamples.
		 * @see #elemIndicesExamples()
		 */
		public static final QualifiedName elemIndicesExamples = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"elemIndicesExamples");

		/**
		 * Returns the element type for the underlying value type of the first (internal) value in
		 * a non-empty array.
		 * @param array (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          the non-empty array.
		 * @return (CAL type: <code>Cal.Collections.Array.ElementType</code>) 
		 *          the element type.
		 */
		public static final SourceModel.Expr elementType_fromNonEmptyArray(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.elementType_fromNonEmptyArray), array});
		}

		/**
		 * Name binding for function: elementType_fromNonEmptyArray.
		 * @see #elementType_fromNonEmptyArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName elementType_fromNonEmptyArray = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"elementType_fromNonEmptyArray");

		/**
		 * Returns the element type for the underlying value type of an internal value.
		 * @param value (CAL type: <code>a</code>)
		 *          the internal value.
		 * @return (CAL type: <code>Cal.Collections.Array.ElementType</code>) 
		 *          the element type.
		 */
		public static final SourceModel.Expr elementType_fromValue(SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.elementType_fromValue), value});
		}

		/**
		 * Name binding for function: elementType_fromValue.
		 * @see #elementType_fromValue(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName elementType_fromValue = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"elementType_fromValue");

		/**
		 * Helper binding method for function: emptyArray. 
		 * @return the SourceModule.expr representing an application of emptyArray
		 */
		public static final SourceModel.Expr emptyArray() {
			return SourceModel.Expr.Var.make(Functions.emptyArray);
		}

		/**
		 * Name binding for function: emptyArray.
		 * @see #emptyArray()
		 */
		public static final QualifiedName emptyArray = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "emptyArray");

		/**
		 * Helper binding method for function: eqArrayInstanceExamples. 
		 * @return the SourceModule.expr representing an application of eqArrayInstanceExamples
		 */
		public static final SourceModel.Expr eqArrayInstanceExamples() {
			return SourceModel.Expr.Var.make(Functions.eqArrayInstanceExamples);
		}

		/**
		 * Name binding for function: eqArrayInstanceExamples.
		 * @see #eqArrayInstanceExamples()
		 */
		public static final QualifiedName eqArrayInstanceExamples = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"eqArrayInstanceExamples");

		/**
		 * Helper binding method for function: equalsArray. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of equalsArray
		 */
		public static final SourceModel.Expr equalsArray(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.equalsArray), array1, array2});
		}

		/**
		 * Name binding for function: equalsArray.
		 * @see #equalsArray(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName equalsArray = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "equalsArray");

		/**
		 * Returns whether the two arrays are equal.
		 * <p>
		 * This should yield better performance for primitive arrays then the <code>Cal.Core.Prelude.equals</code> class method for
		 * <code>Cal.Collections.Array.Array</code>.
		 * 
		 * @param array1 (CAL type: <code>(Cal.Core.Prelude.Eq a, Cal.Core.Prelude.Typeable a) => Cal.Collections.Array.Array a</code>)
		 *          the first argument to compare.
		 * @param array2 (CAL type: <code>(Cal.Core.Prelude.Eq a, Cal.Core.Prelude.Typeable a) => Cal.Collections.Array.Array a</code>)
		 *          the second argument to compare.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if <code>array1</code> and <code>array2</code> are equal; <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr equalsPrimitive(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.equalsPrimitive), array1, array2});
		}

		/**
		 * Name binding for function: equalsPrimitive.
		 * @see #equalsPrimitive(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName equalsPrimitive = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"equalsPrimitive");

		/**
		 * Helper binding method for function: equalsPrimitiveExamples. 
		 * @return the SourceModule.expr representing an application of equalsPrimitiveExamples
		 */
		public static final SourceModel.Expr equalsPrimitiveExamples() {
			return SourceModel.Expr.Var.make(Functions.equalsPrimitiveExamples);
		}

		/**
		 * Name binding for function: equalsPrimitiveExamples.
		 * @see #equalsPrimitiveExamples()
		 */
		public static final QualifiedName equalsPrimitiveExamples = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"equalsPrimitiveExamples");

		/**
		 * Helper binding method for function: filterExamples. 
		 * @return the SourceModule.expr representing an application of filterExamples
		 */
		public static final SourceModel.Expr filterExamples() {
			return SourceModel.Expr.Var.make(Functions.filterExamples);
		}

		/**
		 * Name binding for function: filterExamples.
		 * @see #filterExamples()
		 */
		public static final QualifiedName filterExamples = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "filterExamples");

		/**
		 * Helper binding method for function: filterIndexedExamples. 
		 * @return the SourceModule.expr representing an application of filterIndexedExamples
		 */
		public static final SourceModel.Expr filterIndexedExamples() {
			return SourceModel.Expr.Var.make(Functions.filterIndexedExamples);
		}

		/**
		 * Name binding for function: filterIndexedExamples.
		 * @see #filterIndexedExamples()
		 */
		public static final QualifiedName filterIndexedExamples = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"filterIndexedExamples");

		/**
		 * Helper binding method for function: findExamples. 
		 * @return the SourceModule.expr representing an application of findExamples
		 */
		public static final SourceModel.Expr findExamples() {
			return SourceModel.Expr.Var.make(Functions.findExamples);
		}

		/**
		 * Name binding for function: findExamples.
		 * @see #findExamples()
		 */
		public static final QualifiedName findExamples = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "findExamples");

		/**
		 * Helper binding method for function: findIndexExamples. 
		 * @return the SourceModule.expr representing an application of findIndexExamples
		 */
		public static final SourceModel.Expr findIndexExamples() {
			return SourceModel.Expr.Var.make(Functions.findIndexExamples);
		}

		/**
		 * Name binding for function: findIndexExamples.
		 * @see #findIndexExamples()
		 */
		public static final QualifiedName findIndexExamples = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"findIndexExamples");

		/**
		 * Helper binding method for function: findIndicesExamples. 
		 * @return the SourceModule.expr representing an application of findIndicesExamples
		 */
		public static final SourceModel.Expr findIndicesExamples() {
			return SourceModel.Expr.Var.make(Functions.findIndicesExamples);
		}

		/**
		 * Name binding for function: findIndicesExamples.
		 * @see #findIndicesExamples()
		 */
		public static final QualifiedName findIndicesExamples = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"findIndicesExamples");

		/**
		 * WARNING- keep private, this is for explanation and benchmarking purposes only.
		 * <p>
		 * This is another implementation of <code>Cal.Collections.Array.foldLeftStrict</code> that pushes the recursion to the Java array level.
		 * In this case, it doesn't benefit performance much. This is good news, since it means pushing to the level
		 * of Java arrays is only useful if there is a specific reason for it e.g.
		 * <ul>
		 *  <li>
		 *   an especially efficient implementation for a specific primitive array type
		 *  </li>
		 *  <li>
		 *   delegation to a foreign function to do most of the work
		 *  </li>
		 *  <li>
		 *   does not neet to intertwine with CAL functions at a fine-grained level (unlike with <code>Cal.Collections.Array.foldLeftStrict</code>, which
		 *   has its folding function).
		 *  </li>
		 * </ul>
		 * <p>
		 * <code>Cal.Test.General.M2.sumArray 100000</code> went from 7.6s to 7.5s using this version of <code>Cal.Collections.Array.foldLeftStrict</code>. This is not
		 * enough benefit to justify the extra coding here.
		 * 
		 * @param foldFunction (CAL type: <code>a -> b -> a</code>)
		 * @param initialValue (CAL type: <code>a</code>)
		 * @param array (CAL type: <code>Cal.Collections.Array.Array b</code>)
		 * @return (CAL type: <code>a</code>) 
		 */
		public static final SourceModel.Expr foldLeftStrictAlt(SourceModel.Expr foldFunction, SourceModel.Expr initialValue, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.foldLeftStrictAlt), foldFunction, initialValue, array});
		}

		/**
		 * Name binding for function: foldLeftStrictAlt.
		 * @see #foldLeftStrictAlt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName foldLeftStrictAlt = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"foldLeftStrictAlt");

		/**
		 * Helper binding method for function: foldRight1Examples. 
		 * @return the SourceModule.expr representing an application of foldRight1Examples
		 */
		public static final SourceModel.Expr foldRight1Examples() {
			return SourceModel.Expr.Var.make(Functions.foldRight1Examples);
		}

		/**
		 * Name binding for function: foldRight1Examples.
		 * @see #foldRight1Examples()
		 */
		public static final QualifiedName foldRight1Examples = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"foldRight1Examples");

		/**
		 * Helper binding method for function: foldRightExamples. 
		 * @return the SourceModule.expr representing an application of foldRightExamples
		 */
		public static final SourceModel.Expr foldRightExamples() {
			return SourceModel.Expr.Var.make(Functions.foldRightExamples);
		}

		/**
		 * Name binding for function: foldRightExamples.
		 * @see #foldRightExamples()
		 */
		public static final QualifiedName foldRightExamples = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"foldRightExamples");

		/**
		 * Helper function used in the implementation of <code>foldRight</code> and <code>foldRight1</code>
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.Array.foldRight, Cal.Collections.Array.foldRight1
		 * <dd><b>Type Constructors:</b> Cal.Collections.Array.Array
		 * </dl>
		 * 
		 * @param array (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 * @param foldFunction (CAL type: <code>a -> b -> b</code>)
		 * @param index (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @param value (CAL type: <code>b</code>)
		 * @return (CAL type: <code>b</code>) 
		 */
		public static final SourceModel.Expr foldRightHelper(SourceModel.Expr array, SourceModel.Expr foldFunction, SourceModel.Expr index, SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.foldRightHelper), array, foldFunction, index, value});
		}

		/**
		 * @see #foldRightHelper(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param foldFunction
		 * @param index
		 * @param value
		 * @return the SourceModel.Expr representing an application of foldRightHelper
		 */
		public static final SourceModel.Expr foldRightHelper(SourceModel.Expr array, SourceModel.Expr foldFunction, int index, SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.foldRightHelper), array, foldFunction, SourceModel.Expr.makeIntValue(index), value});
		}

		/**
		 * Name binding for function: foldRightHelper.
		 * @see #foldRightHelper(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName foldRightHelper = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"foldRightHelper");

		/**
		 * Helper binding method for function: fromListWithExamples. 
		 * @return the SourceModule.expr representing an application of fromListWithExamples
		 */
		public static final SourceModel.Expr fromListWithExamples() {
			return SourceModel.Expr.Var.make(Functions.fromListWithExamples);
		}

		/**
		 * Name binding for function: fromListWithExamples.
		 * @see #fromListWithExamples()
		 */
		public static final QualifiedName fromListWithExamples = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"fromListWithExamples");

		/**
		 * Helper binding method for function: fromListWithInternal. 
		 * @param firstElement
		 * @param f
		 * @param tail
		 * @return the SourceModule.expr representing an application of fromListWithInternal
		 */
		public static final SourceModel.Expr fromListWithInternal(SourceModel.Expr firstElement, SourceModel.Expr f, SourceModel.Expr tail) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromListWithInternal), firstElement, f, tail});
		}

		/**
		 * Name binding for function: fromListWithInternal.
		 * @see #fromListWithInternal(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fromListWithInternal = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"fromListWithInternal");

		/**
		 * Helper binding method for function: fromNonEmptyListInternal. 
		 * @param elementType
		 * @param nonEmptyList
		 * @return the SourceModule.expr representing an application of fromNonEmptyListInternal
		 */
		public static final SourceModel.Expr fromNonEmptyListInternal(SourceModel.Expr elementType, SourceModel.Expr nonEmptyList) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromNonEmptyListInternal), elementType, nonEmptyList});
		}

		/**
		 * Name binding for function: fromNonEmptyListInternal.
		 * @see #fromNonEmptyListInternal(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fromNonEmptyListInternal = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"fromNonEmptyListInternal");

		/**
		 * Helper binding method for function: fromStringExamples. 
		 * @return the SourceModule.expr representing an application of fromStringExamples
		 */
		public static final SourceModel.Expr fromStringExamples() {
			return SourceModel.Expr.Var.make(Functions.fromStringExamples);
		}

		/**
		 * Name binding for function: fromStringExamples.
		 * @see #fromStringExamples()
		 */
		public static final QualifiedName fromStringExamples = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"fromStringExamples");

		/**
		 * Helper binding method for function: greaterThanArray. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of greaterThanArray
		 */
		public static final SourceModel.Expr greaterThanArray(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.greaterThanArray), array1, array2});
		}

		/**
		 * Name binding for function: greaterThanArray.
		 * @see #greaterThanArray(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName greaterThanArray = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"greaterThanArray");

		/**
		 * Helper binding method for function: greaterThanEqualsArray. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of greaterThanEqualsArray
		 */
		public static final SourceModel.Expr greaterThanEqualsArray(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.greaterThanEqualsArray), array1, array2});
		}

		/**
		 * Name binding for function: greaterThanEqualsArray.
		 * @see #greaterThanEqualsArray(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName greaterThanEqualsArray = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"greaterThanEqualsArray");

		/**
		 * 
		 * @param predicate (CAL type: <code>a -> Cal.Core.Prelude.Boolean</code>)
		 * @param array (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          index of the first element for which predicate returns True, or
		 * -1 if predicate returns False for all elements
		 */
		public static final SourceModel.Expr indexOfBy(SourceModel.Expr predicate, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.indexOfBy), predicate, array});
		}

		/**
		 * Name binding for function: indexOfBy.
		 * @see #indexOfBy(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName indexOfBy = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "indexOfBy");

		/**
		 * Helper binding method for function: inputArray. 
		 * @param object
		 * @return the SourceModule.expr representing an application of inputArray
		 */
		public static final SourceModel.Expr inputArray(SourceModel.Expr object) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inputArray), object});
		}

		/**
		 * Name binding for function: inputArray.
		 * @see #inputArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName inputArray = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "inputArray");

		/**
		 * Helper binding method for function: inputArrayExamples. 
		 * @return the SourceModule.expr representing an application of inputArrayExamples
		 */
		public static final SourceModel.Expr inputArrayExamples() {
			return SourceModel.Expr.Var.make(Functions.inputArrayExamples);
		}

		/**
		 * Name binding for function: inputArrayExamples.
		 * @see #inputArrayExamples()
		 */
		public static final QualifiedName inputArrayExamples = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"inputArrayExamples");

		/**
		 * Helper binding method for function: inputArrayFromJObjectArray. 
		 * @param objectArray
		 * @return the SourceModule.expr representing an application of inputArrayFromJObjectArray
		 */
		public static final SourceModel.Expr inputArrayFromJObjectArray(SourceModel.Expr objectArray) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inputArrayFromJObjectArray), objectArray});
		}

		/**
		 * Name binding for function: inputArrayFromJObjectArray.
		 * @see #inputArrayFromJObjectArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName inputArrayFromJObjectArray = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"inputArrayFromJObjectArray");

		/**
		 * Helper binding method for function: inputPrimitiveExamples. 
		 * @return the SourceModule.expr representing an application of inputPrimitiveExamples
		 */
		public static final SourceModel.Expr inputPrimitiveExamples() {
			return SourceModel.Expr.Var.make(Functions.inputPrimitiveExamples);
		}

		/**
		 * Name binding for function: inputPrimitiveExamples.
		 * @see #inputPrimitiveExamples()
		 */
		public static final QualifiedName inputPrimitiveExamples = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"inputPrimitiveExamples");

		/**
		 * Helper binding method for function: insertArrayAtExamples. 
		 * @return the SourceModule.expr representing an application of insertArrayAtExamples
		 */
		public static final SourceModel.Expr insertArrayAtExamples() {
			return SourceModel.Expr.Var.make(Functions.insertArrayAtExamples);
		}

		/**
		 * Name binding for function: insertArrayAtExamples.
		 * @see #insertArrayAtExamples()
		 */
		public static final QualifiedName insertArrayAtExamples = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"insertArrayAtExamples");

		/**
		 * Helper binding method for function: insertByExamples. 
		 * @return the SourceModule.expr representing an application of insertByExamples
		 */
		public static final SourceModel.Expr insertByExamples() {
			return SourceModel.Expr.Var.make(Functions.insertByExamples);
		}

		/**
		 * Name binding for function: insertByExamples.
		 * @see #insertByExamples()
		 */
		public static final QualifiedName insertByExamples = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"insertByExamples");

		/**
		 * Helper binding method for function: insertExamples. 
		 * @return the SourceModule.expr representing an application of insertExamples
		 */
		public static final SourceModel.Expr insertExamples() {
			return SourceModel.Expr.Var.make(Functions.insertExamples);
		}

		/**
		 * Name binding for function: insertExamples.
		 * @see #insertExamples()
		 */
		public static final QualifiedName insertExamples = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "insertExamples");

		/**
		 * Converts a failure code of -1 into a <code>Cal.Core.Prelude.Nothing</code> value.
		 * @param intValue (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Core.Prelude.Int</code>) 
		 */
		public static final SourceModel.Expr intToMaybe(SourceModel.Expr intValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intToMaybe), intValue});
		}

		/**
		 * @see #intToMaybe(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param intValue
		 * @return the SourceModel.Expr representing an application of intToMaybe
		 */
		public static final SourceModel.Expr intToMaybe(int intValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intToMaybe), SourceModel.Expr.makeIntValue(intValue)});
		}

		/**
		 * Name binding for function: intToMaybe.
		 * @see #intToMaybe(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intToMaybe = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "intToMaybe");

		/**
		 * Exposes the values array held onto by each object as a Java array of CAL values. The returned Java array
		 * is a copy in all cases (including <code>Cal.Collections.Array.CalValueArray</code>).
		 * @param array (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 * @return (CAL type: <code>Cal.Collections.ArrayPrimitives.JCalValueArray</code>) 
		 */
		public static final SourceModel.Expr internalArrayValues(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.internalArrayValues), array});
		}

		/**
		 * Name binding for function: internalArrayValues.
		 * @see #internalArrayValues(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName internalArrayValues = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"internalArrayValues");

		/**
		 * Helper binding method for function: isElemByExamples. 
		 * @return the SourceModule.expr representing an application of isElemByExamples
		 */
		public static final SourceModel.Expr isElemByExamples() {
			return SourceModel.Expr.Var.make(Functions.isElemByExamples);
		}

		/**
		 * Name binding for function: isElemByExamples.
		 * @see #isElemByExamples()
		 */
		public static final QualifiedName isElemByExamples = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"isElemByExamples");

		/**
		 * Helper binding method for function: isEmptyArray. 
		 * @param array
		 * @return the SourceModule.expr representing an application of isEmptyArray
		 */
		public static final SourceModel.Expr isEmptyArray(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isEmptyArray), array});
		}

		/**
		 * Name binding for function: isEmptyArray.
		 * @see #isEmptyArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isEmptyArray = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "isEmptyArray");

		/**
		 * Helper binding method for function: isJList. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of isJList
		 */
		public static final SourceModel.Expr isJList(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isJList), arg_1});
		}

		/**
		 * Name binding for function: isJList.
		 * @see #isJList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isJList = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "isJList");

		/**
		 * Returns the integer representation of the enumeration value (of type <code>Cal.Collections.Array.ElementType</code>)
		 * for the underlying value type of an internal value.
		 * @param value (CAL type: <code>Cal.Core.Prelude.CalValue</code>)
		 *          the internal value.
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the element type as an integer.
		 */
		public static final SourceModel.Expr jGetElementType(SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jGetElementType), value});
		}

		/**
		 * Name binding for function: jGetElementType.
		 * @see #jGetElementType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jGetElementType = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"jGetElementType");

		/**
		 * Helper binding method for function: jListToJObjectArray. 
		 * @param jList
		 * @return the SourceModule.expr representing an application of jListToJObjectArray
		 */
		public static final SourceModel.Expr jListToJObjectArray(SourceModel.Expr jList) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jListToJObjectArray), jList});
		}

		/**
		 * Name binding for function: jListToJObjectArray.
		 * @see #jListToJObjectArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jListToJObjectArray = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"jListToJObjectArray");

		/**
		 * Helper binding method for function: lastExamples. 
		 * @return the SourceModule.expr representing an application of lastExamples
		 */
		public static final SourceModel.Expr lastExamples() {
			return SourceModel.Expr.Var.make(Functions.lastExamples);
		}

		/**
		 * Name binding for function: lastExamples.
		 * @see #lastExamples()
		 */
		public static final QualifiedName lastExamples = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "lastExamples");

		/**
		 * Helper binding method for function: lastIndexOfExamples. 
		 * @return the SourceModule.expr representing an application of lastIndexOfExamples
		 */
		public static final SourceModel.Expr lastIndexOfExamples() {
			return SourceModel.Expr.Var.make(Functions.lastIndexOfExamples);
		}

		/**
		 * Name binding for function: lastIndexOfExamples.
		 * @see #lastIndexOfExamples()
		 */
		public static final QualifiedName lastIndexOfExamples = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"lastIndexOfExamples");

		/**
		 * Helper binding method for function: lengthExamples. 
		 * @return the SourceModule.expr representing an application of lengthExamples
		 */
		public static final SourceModel.Expr lengthExamples() {
			return SourceModel.Expr.Var.make(Functions.lengthExamples);
		}

		/**
		 * Name binding for function: lengthExamples.
		 * @see #lengthExamples()
		 */
		public static final QualifiedName lengthExamples = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "lengthExamples");

		/**
		 * Helper binding method for function: lessThanArray. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of lessThanArray
		 */
		public static final SourceModel.Expr lessThanArray(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lessThanArray), array1, array2});
		}

		/**
		 * Name binding for function: lessThanArray.
		 * @see #lessThanArray(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName lessThanArray = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "lessThanArray");

		/**
		 * Helper binding method for function: lessThanEqualsArray. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of lessThanEqualsArray
		 */
		public static final SourceModel.Expr lessThanEqualsArray(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lessThanEqualsArray), array1, array2});
		}

		/**
		 * Name binding for function: lessThanEqualsArray.
		 * @see #lessThanEqualsArray(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName lessThanEqualsArray = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"lessThanEqualsArray");

		/**
		 * Helper binding method for function: makeArrayFromCalValues. 
		 * @param elementType
		 * @param array
		 * @return the SourceModule.expr representing an application of makeArrayFromCalValues
		 */
		public static final SourceModel.Expr makeArrayFromCalValues(SourceModel.Expr elementType, SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeArrayFromCalValues), elementType, array});
		}

		/**
		 * Name binding for function: makeArrayFromCalValues.
		 * @see #makeArrayFromCalValues(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeArrayFromCalValues = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"makeArrayFromCalValues");

		/**
		 * Helper binding method for function: makeArrayFromIndicesExamples. 
		 * @return the SourceModule.expr representing an application of makeArrayFromIndicesExamples
		 */
		public static final SourceModel.Expr makeArrayFromIndicesExamples() {
			return 
				SourceModel.Expr.Var.make(
					Functions.makeArrayFromIndicesExamples);
		}

		/**
		 * Name binding for function: makeArrayFromIndicesExamples.
		 * @see #makeArrayFromIndicesExamples()
		 */
		public static final QualifiedName makeArrayFromIndicesExamples = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"makeArrayFromIndicesExamples");

		/**
		 * Creates a default array of the given size. Generally, used in combination with the update function to then
		 * initialize the array properly. This must be kept private, because it violates the public contract of the
		 * <code>Cal.Collections.Array.Array</code> type.
		 * @param elementType (CAL type: <code>Cal.Collections.Array.ElementType</code>)
		 * @param size (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @return (CAL type: <code>Cal.Collections.Array.Array a</code>) 
		 */
		public static final SourceModel.Expr makeDefaultInternal(SourceModel.Expr elementType, SourceModel.Expr size) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeDefaultInternal), elementType, size});
		}

		/**
		 * @see #makeDefaultInternal(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param elementType
		 * @param size
		 * @return the SourceModel.Expr representing an application of makeDefaultInternal
		 */
		public static final SourceModel.Expr makeDefaultInternal(SourceModel.Expr elementType, int size) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeDefaultInternal), elementType, SourceModel.Expr.makeIntValue(size)});
		}

		/**
		 * Name binding for function: makeDefaultInternal.
		 * @see #makeDefaultInternal(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeDefaultInternal = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"makeDefaultInternal");

		/**
		 * Helper binding method for function: mapExamples. 
		 * @return the SourceModule.expr representing an application of mapExamples
		 */
		public static final SourceModel.Expr mapExamples() {
			return SourceModel.Expr.Var.make(Functions.mapExamples);
		}

		/**
		 * Name binding for function: mapExamples.
		 * @see #mapExamples()
		 */
		public static final QualifiedName mapExamples = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "mapExamples");

		/**
		 * Helper binding method for function: mapIndexedExamples. 
		 * @return the SourceModule.expr representing an application of mapIndexedExamples
		 */
		public static final SourceModel.Expr mapIndexedExamples() {
			return SourceModel.Expr.Var.make(Functions.mapIndexedExamples);
		}

		/**
		 * Name binding for function: mapIndexedExamples.
		 * @see #mapIndexedExamples()
		 */
		public static final QualifiedName mapIndexedExamples = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"mapIndexedExamples");

		/**
		 * Helper binding method for function: maxArray. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of maxArray
		 */
		public static final SourceModel.Expr maxArray(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.maxArray), array1, array2});
		}

		/**
		 * Name binding for function: maxArray.
		 * @see #maxArray(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName maxArray = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "maxArray");

		/**
		 * Helper binding method for function: maximumByExamples. 
		 * @return the SourceModule.expr representing an application of maximumByExamples
		 */
		public static final SourceModel.Expr maximumByExamples() {
			return SourceModel.Expr.Var.make(Functions.maximumByExamples);
		}

		/**
		 * Name binding for function: maximumByExamples.
		 * @see #maximumByExamples()
		 */
		public static final QualifiedName maximumByExamples = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"maximumByExamples");

		/**
		 * Helper binding method for function: maximumExamples. 
		 * @return the SourceModule.expr representing an application of maximumExamples
		 */
		public static final SourceModel.Expr maximumExamples() {
			return SourceModel.Expr.Var.make(Functions.maximumExamples);
		}

		/**
		 * Name binding for function: maximumExamples.
		 * @see #maximumExamples()
		 */
		public static final QualifiedName maximumExamples = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"maximumExamples");

		/**
		 * Helper binding method for function: mergeByExamples. 
		 * @return the SourceModule.expr representing an application of mergeByExamples
		 */
		public static final SourceModel.Expr mergeByExamples() {
			return SourceModel.Expr.Var.make(Functions.mergeByExamples);
		}

		/**
		 * Name binding for function: mergeByExamples.
		 * @see #mergeByExamples()
		 */
		public static final QualifiedName mergeByExamples = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"mergeByExamples");

		/**
		 * Helper binding method for function: mergeExamples. 
		 * @return the SourceModule.expr representing an application of mergeExamples
		 */
		public static final SourceModel.Expr mergeExamples() {
			return SourceModel.Expr.Var.make(Functions.mergeExamples);
		}

		/**
		 * Name binding for function: mergeExamples.
		 * @see #mergeExamples()
		 */
		public static final QualifiedName mergeExamples = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "mergeExamples");

		/**
		 * Helper binding method for function: minArray. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of minArray
		 */
		public static final SourceModel.Expr minArray(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.minArray), array1, array2});
		}

		/**
		 * Name binding for function: minArray.
		 * @see #minArray(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName minArray = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "minArray");

		/**
		 * Helper binding method for function: minimumByExamples. 
		 * @return the SourceModule.expr representing an application of minimumByExamples
		 */
		public static final SourceModel.Expr minimumByExamples() {
			return SourceModel.Expr.Var.make(Functions.minimumByExamples);
		}

		/**
		 * Name binding for function: minimumByExamples.
		 * @see #minimumByExamples()
		 */
		public static final QualifiedName minimumByExamples = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"minimumByExamples");

		/**
		 * Helper binding method for function: minimumExamples. 
		 * @return the SourceModule.expr representing an application of minimumExamples
		 */
		public static final SourceModel.Expr minimumExamples() {
			return SourceModel.Expr.Var.make(Functions.minimumExamples);
		}

		/**
		 * Name binding for function: minimumExamples.
		 * @see #minimumExamples()
		 */
		public static final QualifiedName minimumExamples = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"minimumExamples");

		/**
		 * Performs the analogous functionality to <code>System.arraycopy</code> in Java.
		 * This function does destructive updates on the destination array argument and so must remain private.
		 * @param source (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          the source array to copy elements from
		 * @param sourcePos (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          index in the source array from which to start copying
		 * @param dest (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 *          the destination array to copy elements to
		 * @param destPos (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          index in the destination array at which to start copying
		 * @param nElemsToCopy (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the number of elements to copy from source to dest
		 * @return (CAL type: <code>Cal.Collections.Array.Array a</code>) 
		 *          the destination array <code>dest</code>, which is destructively modified by this function.
		 */
		public static final SourceModel.Expr nonEmptyArrayCopy(SourceModel.Expr source, SourceModel.Expr sourcePos, SourceModel.Expr dest, SourceModel.Expr destPos, SourceModel.Expr nElemsToCopy) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.nonEmptyArrayCopy), source, sourcePos, dest, destPos, nElemsToCopy});
		}

		/**
		 * @see #nonEmptyArrayCopy(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param source
		 * @param sourcePos
		 * @param dest
		 * @param destPos
		 * @param nElemsToCopy
		 * @return the SourceModel.Expr representing an application of nonEmptyArrayCopy
		 */
		public static final SourceModel.Expr nonEmptyArrayCopy(SourceModel.Expr source, int sourcePos, SourceModel.Expr dest, int destPos, int nElemsToCopy) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.nonEmptyArrayCopy), source, SourceModel.Expr.makeIntValue(sourcePos), dest, SourceModel.Expr.makeIntValue(destPos), SourceModel.Expr.makeIntValue(nElemsToCopy)});
		}

		/**
		 * Name binding for function: nonEmptyArrayCopy.
		 * @see #nonEmptyArrayCopy(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName nonEmptyArrayCopy = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"nonEmptyArrayCopy");

		/**
		 * Exposes the primitive array values held by the array argument. Note: the exposed array is not a copy.
		 * It is typed as a <code>Cal.Core.Prelude.JObject</code> since that is the common super-type of primitive arrays and object arrays.
		 * @param array (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.JObject</code>) 
		 */
		public static final SourceModel.Expr nonEmptyArrayValues(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.nonEmptyArrayValues), array});
		}

		/**
		 * Name binding for function: nonEmptyArrayValues.
		 * @see #nonEmptyArrayValues(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName nonEmptyArrayValues = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"nonEmptyArrayValues");

		/**
		 * Helper binding method for function: notEqualsArray. 
		 * @param array1
		 * @param array2
		 * @return the SourceModule.expr representing an application of notEqualsArray
		 */
		public static final SourceModel.Expr notEqualsArray(SourceModel.Expr array1, SourceModel.Expr array2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.notEqualsArray), array1, array2});
		}

		/**
		 * Name binding for function: notEqualsArray.
		 * @see #notEqualsArray(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName notEqualsArray = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "notEqualsArray");

		/**
		 * Helper binding method for function: orArrayExamples. 
		 * @return the SourceModule.expr representing an application of orArrayExamples
		 */
		public static final SourceModel.Expr orArrayExamples() {
			return SourceModel.Expr.Var.make(Functions.orArrayExamples);
		}

		/**
		 * Name binding for function: orArrayExamples.
		 * @see #orArrayExamples()
		 */
		public static final QualifiedName orArrayExamples = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"orArrayExamples");

		/**
		 * Helper binding method for function: outputArray. 
		 * @param array
		 * @return the SourceModule.expr representing an application of outputArray
		 */
		public static final SourceModel.Expr outputArray(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputArray), array});
		}

		/**
		 * Name binding for function: outputArray.
		 * @see #outputArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName outputArray = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "outputArray");

		/**
		 * Helper binding method for function: outputArrayToJObjectArray. 
		 * @param array
		 * @return the SourceModule.expr representing an application of outputArrayToJObjectArray
		 */
		public static final SourceModel.Expr outputArrayToJObjectArray(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputArrayToJObjectArray), array});
		}

		/**
		 * Name binding for function: outputArrayToJObjectArray.
		 * @see #outputArrayToJObjectArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName outputArrayToJObjectArray = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"outputArrayToJObjectArray");

		/**
		 * Helper binding method for function: periodizeExamples. 
		 * @return the SourceModule.expr representing an application of periodizeExamples
		 */
		public static final SourceModel.Expr periodizeExamples() {
			return SourceModel.Expr.Var.make(Functions.periodizeExamples);
		}

		/**
		 * Name binding for function: periodizeExamples.
		 * @see #periodizeExamples()
		 */
		public static final QualifiedName periodizeExamples = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"periodizeExamples");

		/**
		 * Helper binding method for function: replaceAllExamples. 
		 * @return the SourceModule.expr representing an application of replaceAllExamples
		 */
		public static final SourceModel.Expr replaceAllExamples() {
			return SourceModel.Expr.Var.make(Functions.replaceAllExamples);
		}

		/**
		 * Name binding for function: replaceAllExamples.
		 * @see #replaceAllExamples()
		 */
		public static final QualifiedName replaceAllExamples = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"replaceAllExamples");

		/**
		 * Helper binding method for function: replaceAtExamples. 
		 * @return the SourceModule.expr representing an application of replaceAtExamples
		 */
		public static final SourceModel.Expr replaceAtExamples() {
			return SourceModel.Expr.Var.make(Functions.replaceAtExamples);
		}

		/**
		 * Name binding for function: replaceAtExamples.
		 * @see #replaceAtExamples()
		 */
		public static final QualifiedName replaceAtExamples = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"replaceAtExamples");

		/**
		 * Helper binding method for function: replicateArrayExamples. 
		 * @return the SourceModule.expr representing an application of replicateArrayExamples
		 */
		public static final SourceModel.Expr replicateArrayExamples() {
			return SourceModel.Expr.Var.make(Functions.replicateArrayExamples);
		}

		/**
		 * Name binding for function: replicateArrayExamples.
		 * @see #replicateArrayExamples()
		 */
		public static final QualifiedName replicateArrayExamples = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"replicateArrayExamples");

		/**
		 * Helper binding method for function: showArray. 
		 * @param array
		 * @return the SourceModule.expr representing an application of showArray
		 */
		public static final SourceModel.Expr showArray(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showArray), array});
		}

		/**
		 * Name binding for function: showArray.
		 * @see #showArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showArray = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "showArray");

		/**
		 * Helper binding method for function: showArrayInstanceExamples. 
		 * @return the SourceModule.expr representing an application of showArrayInstanceExamples
		 */
		public static final SourceModel.Expr showArrayInstanceExamples() {
			return 
				SourceModel.Expr.Var.make(Functions.showArrayInstanceExamples);
		}

		/**
		 * Name binding for function: showArrayInstanceExamples.
		 * @see #showArrayInstanceExamples()
		 */
		public static final QualifiedName showArrayInstanceExamples = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"showArrayInstanceExamples");

		/**
		 * Helper binding method for function: sortByExamples. 
		 * @return the SourceModule.expr representing an application of sortByExamples
		 */
		public static final SourceModel.Expr sortByExamples() {
			return SourceModel.Expr.Var.make(Functions.sortByExamples);
		}

		/**
		 * Name binding for function: sortByExamples.
		 * @see #sortByExamples()
		 */
		public static final QualifiedName sortByExamples = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "sortByExamples");

		/**
		 * Helper binding method for function: sortExamples. 
		 * @return the SourceModule.expr representing an application of sortExamples
		 */
		public static final SourceModel.Expr sortExamples() {
			return SourceModel.Expr.Var.make(Functions.sortExamples);
		}

		/**
		 * Name binding for function: sortExamples.
		 * @see #sortExamples()
		 */
		public static final QualifiedName sortExamples = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "sortExamples");

		/**
		 * Sorts the specified array. All order comparisons are done using the
		 * <code>Cal.Core.Prelude.compare</code> class method.
		 * <p>
		 * This function implements a stable sort in that items which evaluate to <code>Cal.Core.Prelude.EQ</code>
		 * under the <code>Cal.Core.Prelude.compare</code> class method preserve their original ordering in the original
		 * array.
		 * <p>
		 * This should yield better performance for primitive arrays then the <code>Cal.Collections.Array.sort</code> function.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Collections.Array.sort, Cal.Collections.Array.sortBy
		 * </dl>
		 * 
		 * @param array (CAL type: <code>(Cal.Core.Prelude.Ord a, Cal.Core.Prelude.Typeable a) => Cal.Collections.Array.Array a</code>)
		 *          the array to be sorted.
		 * @return (CAL type: <code>(Cal.Core.Prelude.Ord a, Cal.Core.Prelude.Typeable a) => Cal.Collections.Array.Array a</code>) 
		 *          the sorted array.
		 */
		public static final SourceModel.Expr sortPrimitive(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.sortPrimitive), array});
		}

		/**
		 * Name binding for function: sortPrimitive.
		 * @see #sortPrimitive(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName sortPrimitive = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "sortPrimitive");

		/**
		 * Helper binding method for function: spanExamples. 
		 * @return the SourceModule.expr representing an application of spanExamples
		 */
		public static final SourceModel.Expr spanExamples() {
			return SourceModel.Expr.Var.make(Functions.spanExamples);
		}

		/**
		 * Name binding for function: spanExamples.
		 * @see #spanExamples()
		 */
		public static final QualifiedName spanExamples = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "spanExamples");

		/**
		 * Helper binding method for function: spanInclusiveExamples. 
		 * @return the SourceModule.expr representing an application of spanInclusiveExamples
		 */
		public static final SourceModel.Expr spanInclusiveExamples() {
			return SourceModel.Expr.Var.make(Functions.spanInclusiveExamples);
		}

		/**
		 * Name binding for function: spanInclusiveExamples.
		 * @see #spanInclusiveExamples()
		 */
		public static final QualifiedName spanInclusiveExamples = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"spanInclusiveExamples");

		/**
		 * Helper binding method for function: startsWithByExamples. 
		 * @return the SourceModule.expr representing an application of startsWithByExamples
		 */
		public static final SourceModel.Expr startsWithByExamples() {
			return SourceModel.Expr.Var.make(Functions.startsWithByExamples);
		}

		/**
		 * Name binding for function: startsWithByExamples.
		 * @see #startsWithByExamples()
		 */
		public static final QualifiedName startsWithByExamples = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"startsWithByExamples");

		/**
		 * Helper binding method for function: startsWithExamples. 
		 * @return the SourceModule.expr representing an application of startsWithExamples
		 */
		public static final SourceModel.Expr startsWithExamples() {
			return SourceModel.Expr.Var.make(Functions.startsWithExamples);
		}

		/**
		 * Name binding for function: startsWithExamples.
		 * @see #startsWithExamples()
		 */
		public static final QualifiedName startsWithExamples = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"startsWithExamples");

		/**
		 * Helper binding method for function: subscriptExamples. 
		 * @return the SourceModule.expr representing an application of subscriptExamples
		 */
		public static final SourceModel.Expr subscriptExamples() {
			return SourceModel.Expr.Var.make(Functions.subscriptExamples);
		}

		/**
		 * Name binding for function: subscriptExamples.
		 * @see #subscriptExamples()
		 */
		public static final QualifiedName subscriptExamples = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"subscriptExamples");

		/**
		 * Helper binding method for function: sumExamples. 
		 * @return the SourceModule.expr representing an application of sumExamples
		 */
		public static final SourceModel.Expr sumExamples() {
			return SourceModel.Expr.Var.make(Functions.sumExamples);
		}

		/**
		 * Name binding for function: sumExamples.
		 * @see #sumExamples()
		 */
		public static final QualifiedName sumExamples = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "sumExamples");

		/**
		 * Helper binding method for function: system_arraycopy. 
		 * @param arg_1
		 * @param arg_2
		 * @param arg_3
		 * @param arg_4
		 * @param arg_5
		 * @return the SourceModule.expr representing an application of system_arraycopy
		 */
		public static final SourceModel.Expr system_arraycopy(SourceModel.Expr arg_1, SourceModel.Expr arg_2, SourceModel.Expr arg_3, SourceModel.Expr arg_4, SourceModel.Expr arg_5) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.system_arraycopy), arg_1, arg_2, arg_3, arg_4, arg_5});
		}

		/**
		 * @see #system_arraycopy(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @param arg_3
		 * @param arg_4
		 * @param arg_5
		 * @return the SourceModel.Expr representing an application of system_arraycopy
		 */
		public static final SourceModel.Expr system_arraycopy(SourceModel.Expr arg_1, int arg_2, SourceModel.Expr arg_3, int arg_4, int arg_5) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.system_arraycopy), arg_1, SourceModel.Expr.makeIntValue(arg_2), arg_3, SourceModel.Expr.makeIntValue(arg_4), SourceModel.Expr.makeIntValue(arg_5)});
		}

		/**
		 * Name binding for function: system_arraycopy.
		 * @see #system_arraycopy(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName system_arraycopy = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"system_arraycopy");

		/**
		 * Helper binding method for function: takeExamples. 
		 * @return the SourceModule.expr representing an application of takeExamples
		 */
		public static final SourceModel.Expr takeExamples() {
			return SourceModel.Expr.Var.make(Functions.takeExamples);
		}

		/**
		 * Name binding for function: takeExamples.
		 * @see #takeExamples()
		 */
		public static final QualifiedName takeExamples = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "takeExamples");

		/**
		 * Helper binding method for function: takeWhileExamples. 
		 * @return the SourceModule.expr representing an application of takeWhileExamples
		 */
		public static final SourceModel.Expr takeWhileExamples() {
			return SourceModel.Expr.Var.make(Functions.takeWhileExamples);
		}

		/**
		 * Name binding for function: takeWhileExamples.
		 * @see #takeWhileExamples()
		 */
		public static final QualifiedName takeWhileExamples = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"takeWhileExamples");

		/**
		 * Helper binding method for function: toListExamples. 
		 * @return the SourceModule.expr representing an application of toListExamples
		 */
		public static final SourceModel.Expr toListExamples() {
			return SourceModel.Expr.Var.make(Functions.toListExamples);
		}

		/**
		 * Name binding for function: toListExamples.
		 * @see #toListExamples()
		 */
		public static final QualifiedName toListExamples = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "toListExamples");

		/**
		 * Helper binding method for function: toListWithExamples. 
		 * @return the SourceModule.expr representing an application of toListWithExamples
		 */
		public static final SourceModel.Expr toListWithExamples() {
			return SourceModel.Expr.Var.make(Functions.toListWithExamples);
		}

		/**
		 * Name binding for function: toListWithExamples.
		 * @see #toListWithExamples()
		 */
		public static final QualifiedName toListWithExamples = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"toListWithExamples");

		/**
		 * Helper binding method for function: toStringExamples. 
		 * @return the SourceModule.expr representing an application of toStringExamples
		 */
		public static final SourceModel.Expr toStringExamples() {
			return SourceModel.Expr.Var.make(Functions.toStringExamples);
		}

		/**
		 * Name binding for function: toStringExamples.
		 * @see #toStringExamples()
		 */
		public static final QualifiedName toStringExamples = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"toStringExamples");

		/**
		 * Helper binding method for function: typeableElementType_fromTypeRep. 
		 * @param elemType
		 * @return the SourceModule.expr representing an application of typeableElementType_fromTypeRep
		 */
		public static final SourceModel.Expr typeableElementType_fromTypeRep(SourceModel.Expr elemType) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.typeableElementType_fromTypeRep), elemType});
		}

		/**
		 * Name binding for function: typeableElementType_fromTypeRep.
		 * @see #typeableElementType_fromTypeRep(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName typeableElementType_fromTypeRep = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"typeableElementType_fromTypeRep");

		/**
		 * It is important that this function does not touch the value argument.
		 * @param value (CAL type: <code>Cal.Core.Prelude.Typeable a => a</code>)
		 * @return (CAL type: <code>Cal.Collections.Array.ElementType</code>) 
		 */
		public static final SourceModel.Expr typeableElementType_fromValue(SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.typeableElementType_fromValue), value});
		}

		/**
		 * Name binding for function: typeableElementType_fromValue.
		 * @see #typeableElementType_fromValue(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName typeableElementType_fromValue = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"typeableElementType_fromValue");

		/**
		 * Helper binding method for function: unzip3Examples. 
		 * @return the SourceModule.expr representing an application of unzip3Examples
		 */
		public static final SourceModel.Expr unzip3Examples() {
			return SourceModel.Expr.Var.make(Functions.unzip3Examples);
		}

		/**
		 * Name binding for function: unzip3Examples.
		 * @see #unzip3Examples()
		 */
		public static final QualifiedName unzip3Examples = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "unzip3Examples");

		/**
		 * Helper binding method for function: unzip4Examples. 
		 * @return the SourceModule.expr representing an application of unzip4Examples
		 */
		public static final SourceModel.Expr unzip4Examples() {
			return SourceModel.Expr.Var.make(Functions.unzip4Examples);
		}

		/**
		 * Name binding for function: unzip4Examples.
		 * @see #unzip4Examples()
		 */
		public static final QualifiedName unzip4Examples = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "unzip4Examples");

		/**
		 * Helper binding method for function: unzipExamples. 
		 * @return the SourceModule.expr representing an application of unzipExamples
		 */
		public static final SourceModel.Expr unzipExamples() {
			return SourceModel.Expr.Var.make(Functions.unzipExamples);
		}

		/**
		 * Name binding for function: unzipExamples.
		 * @see #unzipExamples()
		 */
		public static final QualifiedName unzipExamples = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "unzipExamples");

		/**
		 * Destructive array update function. This must be kept private, because it violates the public contract of the
		 * <code>Cal.Collections.Array.Array</code> type. However, it is useful for implementing a variety of functions.
		 * @param array (CAL type: <code>Cal.Collections.Array.Array a</code>)
		 * @param index (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @param value (CAL type: <code>a</code>)
		 * @return (CAL type: <code>a</code>) 
		 */
		public static final SourceModel.Expr update(SourceModel.Expr array, SourceModel.Expr index, SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.update), array, index, value});
		}

		/**
		 * @see #update(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param index
		 * @param value
		 * @return the SourceModel.Expr representing an application of update
		 */
		public static final SourceModel.Expr update(SourceModel.Expr array, int index, SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.update), array, SourceModel.Expr.makeIntValue(index), value});
		}

		/**
		 * Name binding for function: update.
		 * @see #update(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName update = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "update");

		/**
		 * Helper binding method for function: updateAtExamples. 
		 * @return the SourceModule.expr representing an application of updateAtExamples
		 */
		public static final SourceModel.Expr updateAtExamples() {
			return SourceModel.Expr.Var.make(Functions.updateAtExamples);
		}

		/**
		 * Name binding for function: updateAtExamples.
		 * @see #updateAtExamples()
		 */
		public static final QualifiedName updateAtExamples = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"updateAtExamples");

		/**
		 * Helper binding method for function: zip3Examples. 
		 * @return the SourceModule.expr representing an application of zip3Examples
		 */
		public static final SourceModel.Expr zip3Examples() {
			return SourceModel.Expr.Var.make(Functions.zip3Examples);
		}

		/**
		 * Name binding for function: zip3Examples.
		 * @see #zip3Examples()
		 */
		public static final QualifiedName zip3Examples = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "zip3Examples");

		/**
		 * Helper binding method for function: zip4Examples. 
		 * @return the SourceModule.expr representing an application of zip4Examples
		 */
		public static final SourceModel.Expr zip4Examples() {
			return SourceModel.Expr.Var.make(Functions.zip4Examples);
		}

		/**
		 * Name binding for function: zip4Examples.
		 * @see #zip4Examples()
		 */
		public static final QualifiedName zip4Examples = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "zip4Examples");

		/**
		 * Helper binding method for function: zipExamples. 
		 * @return the SourceModule.expr representing an application of zipExamples
		 */
		public static final SourceModel.Expr zipExamples() {
			return SourceModel.Expr.Var.make(Functions.zipExamples);
		}

		/**
		 * Name binding for function: zipExamples.
		 * @see #zipExamples()
		 */
		public static final QualifiedName zipExamples = 
			QualifiedName.make(CAL_Array_internal.MODULE_NAME, "zipExamples");

		/**
		 * Helper binding method for function: zipWith3Examples. 
		 * @return the SourceModule.expr representing an application of zipWith3Examples
		 */
		public static final SourceModel.Expr zipWith3Examples() {
			return SourceModel.Expr.Var.make(Functions.zipWith3Examples);
		}

		/**
		 * Name binding for function: zipWith3Examples.
		 * @see #zipWith3Examples()
		 */
		public static final QualifiedName zipWith3Examples = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"zipWith3Examples");

		/**
		 * Helper binding method for function: zipWith4Examples. 
		 * @return the SourceModule.expr representing an application of zipWith4Examples
		 */
		public static final SourceModel.Expr zipWith4Examples() {
			return SourceModel.Expr.Var.make(Functions.zipWith4Examples);
		}

		/**
		 * Name binding for function: zipWith4Examples.
		 * @see #zipWith4Examples()
		 */
		public static final QualifiedName zipWith4Examples = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"zipWith4Examples");

		/**
		 * Helper binding method for function: zipWithExamples. 
		 * @return the SourceModule.expr representing an application of zipWithExamples
		 */
		public static final SourceModel.Expr zipWithExamples() {
			return SourceModel.Expr.Var.make(Functions.zipWithExamples);
		}

		/**
		 * Name binding for function: zipWithExamples.
		 * @see #zipWithExamples()
		 */
		public static final QualifiedName zipWithExamples = 
			QualifiedName.make(
				CAL_Array_internal.MODULE_NAME, 
				"zipWithExamples");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = 200354352;

}
