/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_SqlType.java)
 * was generated from CAL module: Cal.Data.SqlType.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Data.SqlType module from Java code.
 *  
 * Creation date: Wed Aug 08 16:18:44 PDT 2007
 * --!>
 *  
 */

package org.openquark.cal.module.Cal.Data;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * This module contains a type describing type data types of SQL database values.
 * The types are more fine-grained than in <code>ValueType</code>.
 * Also, some of the types include additional information, such as the precision/scale or maximum character length.
 */
public final class CAL_SqlType {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Data.SqlType");

	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Data.SqlType module.
	 */
	public static final class TypeConstructors {
		/**
		 * Foreign type for org.openquark.util.database.SqlType.
		 * Cal.Data.SqlType.SqlType outputs to and inputs from this type.
		 */
		public static final QualifiedName JSqlType = 
			QualifiedName.make(CAL_SqlType.MODULE_NAME, "JSqlType");

		/**
		 * SqlType includes a classification of a SQL data type, as well as more detailed information (such as length or precision) of 
		 * certain data types.
		 * This corresponds to the <code>java.sql.Types</code> JDBC type constants.
		 */
		public static final QualifiedName SqlType = 
			QualifiedName.make(CAL_SqlType.MODULE_NAME, "SqlType");

	}
	/**
	 * This inner class (DataConstructors) contains constants
	 * and methods related to binding to CAL DataConstructors in the Cal.Data.SqlType module.
	 */
	public static final class DataConstructors {
		/*
		 * DataConstructors for the Cal.Data.SqlType.SqlType data type.
		 */

		/**
		 * The SmallInt type is for integer values (typically 8-bit).
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr SqlType_TinyInt() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.SqlType_TinyInt);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.SqlType.SqlType_TinyInt.
		 * @see #SqlType_TinyInt()
		 */
		public static final QualifiedName SqlType_TinyInt = 
			QualifiedName.make(CAL_SqlType.MODULE_NAME, "SqlType_TinyInt");

		/**
		 * Ordinal of DataConstructor Cal.Data.SqlType.SqlType_TinyInt.
		 * @see #SqlType_TinyInt()
		 */
		public static final int SqlType_TinyInt_ordinal = 0;

		/**
		 * The SmallInt type is for integer values (typically 16-bit).
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr SqlType_SmallInt() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.SqlType_SmallInt);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.SqlType.SqlType_SmallInt.
		 * @see #SqlType_SmallInt()
		 */
		public static final QualifiedName SqlType_SmallInt = 
			QualifiedName.make(CAL_SqlType.MODULE_NAME, "SqlType_SmallInt");

		/**
		 * Ordinal of DataConstructor Cal.Data.SqlType.SqlType_SmallInt.
		 * @see #SqlType_SmallInt()
		 */
		public static final int SqlType_SmallInt_ordinal = 1;

		/**
		 * The Integer type is for integer values (typically 32-bit).
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr SqlType_Integer() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.SqlType_Integer);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.SqlType.SqlType_Integer.
		 * @see #SqlType_Integer()
		 */
		public static final QualifiedName SqlType_Integer = 
			QualifiedName.make(CAL_SqlType.MODULE_NAME, "SqlType_Integer");

		/**
		 * Ordinal of DataConstructor Cal.Data.SqlType.SqlType_Integer.
		 * @see #SqlType_Integer()
		 */
		public static final int SqlType_Integer_ordinal = 2;

		/**
		 * The Integer type is for integer values (typically 64-bit).
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr SqlType_BigInt() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.SqlType_BigInt);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.SqlType.SqlType_BigInt.
		 * @see #SqlType_BigInt()
		 */
		public static final QualifiedName SqlType_BigInt = 
			QualifiedName.make(CAL_SqlType.MODULE_NAME, "SqlType_BigInt");

		/**
		 * Ordinal of DataConstructor Cal.Data.SqlType.SqlType_BigInt.
		 * @see #SqlType_BigInt()
		 */
		public static final int SqlType_BigInt_ordinal = 3;

		/**
		 * The Decimal type is a fixed-precision numeric type.
		 * The total number of digits (precision) and the number of digits to the right of the decimal point (scale) can be specified.
		 * Decimal values are intended to be stored with the specified precision or higher (whereas Numeric values should use exactly 
		 * the specified precision).
		 * @param precision (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the total number of digits in the value
		 * @param scale (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the number of digits to the right of the decimal point (cannot be greater than the precision value)
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr SqlType_Decimal(SourceModel.Expr precision, SourceModel.Expr scale) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.SqlType_Decimal), precision, scale});
		}

		/**
		 * @see #SqlType_Decimal(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param precision
		 * @param scale
		 * @return org.openquark.cal.compiler.SourceModel.Expr
		 */
		public static final SourceModel.Expr SqlType_Decimal(int precision, int scale) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.SqlType_Decimal), SourceModel.Expr.makeIntValue(precision), SourceModel.Expr.makeIntValue(scale)});
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.SqlType.SqlType_Decimal.
		 * @see #SqlType_Decimal(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName SqlType_Decimal = 
			QualifiedName.make(CAL_SqlType.MODULE_NAME, "SqlType_Decimal");

		/**
		 * Ordinal of DataConstructor Cal.Data.SqlType.SqlType_Decimal.
		 * @see #SqlType_Decimal(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int SqlType_Decimal_ordinal = 4;

		/**
		 * The Numeric type is a fixed-precision numeric type.
		 * The total number of digits (precision) and the number of digits to the right of the decimal point (scale) can be specified.
		 * Numeric values are intended to be stored with exactly the specified precision (whereas Double values can use the specified 
		 * precision or higher).
		 * @param precision (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the total number of digits in the value
		 * @param scale (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the number of digits to the right of the decimal point (cannot be greater than the precision value)
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr SqlType_Numeric(SourceModel.Expr precision, SourceModel.Expr scale) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.SqlType_Numeric), precision, scale});
		}

		/**
		 * @see #SqlType_Numeric(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param precision
		 * @param scale
		 * @return org.openquark.cal.compiler.SourceModel.Expr
		 */
		public static final SourceModel.Expr SqlType_Numeric(int precision, int scale) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.SqlType_Numeric), SourceModel.Expr.makeIntValue(precision), SourceModel.Expr.makeIntValue(scale)});
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.SqlType.SqlType_Numeric.
		 * @see #SqlType_Numeric(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName SqlType_Numeric = 
			QualifiedName.make(CAL_SqlType.MODULE_NAME, "SqlType_Numeric");

		/**
		 * Ordinal of DataConstructor Cal.Data.SqlType.SqlType_Numeric.
		 * @see #SqlType_Numeric(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int SqlType_Numeric_ordinal = 5;

		/**
		 * The Real type is a floating-point numeric type (typically with 7 digits of mantissa).
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr SqlType_Real() {
			return SourceModel.Expr.DataCons.make(DataConstructors.SqlType_Real);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.SqlType.SqlType_Real.
		 * @see #SqlType_Real()
		 */
		public static final QualifiedName SqlType_Real = 
			QualifiedName.make(CAL_SqlType.MODULE_NAME, "SqlType_Real");

		/**
		 * Ordinal of DataConstructor Cal.Data.SqlType.SqlType_Real.
		 * @see #SqlType_Real()
		 */
		public static final int SqlType_Real_ordinal = 6;

		/**
		 * The Float type is a floating-point numeric type (typically with 15 digits of mantissa).
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr SqlType_Float() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.SqlType_Float);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.SqlType.SqlType_Float.
		 * @see #SqlType_Float()
		 */
		public static final QualifiedName SqlType_Float = 
			QualifiedName.make(CAL_SqlType.MODULE_NAME, "SqlType_Float");

		/**
		 * Ordinal of DataConstructor Cal.Data.SqlType.SqlType_Float.
		 * @see #SqlType_Float()
		 */
		public static final int SqlType_Float_ordinal = 7;

		/**
		 * The Double type is a floating-point numeric type (typically with 15 digits of mantissa).
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr SqlType_Double() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.SqlType_Double);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.SqlType.SqlType_Double.
		 * @see #SqlType_Double()
		 */
		public static final QualifiedName SqlType_Double = 
			QualifiedName.make(CAL_SqlType.MODULE_NAME, "SqlType_Double");

		/**
		 * Ordinal of DataConstructor Cal.Data.SqlType.SqlType_Double.
		 * @see #SqlType_Double()
		 */
		public static final int SqlType_Double_ordinal = 8;

		/**
		 * The Bit type handles a single bit values (on/off).
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr SqlType_Bit() {
			return SourceModel.Expr.DataCons.make(DataConstructors.SqlType_Bit);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.SqlType.SqlType_Bit.
		 * @see #SqlType_Bit()
		 */
		public static final QualifiedName SqlType_Bit = 
			QualifiedName.make(CAL_SqlType.MODULE_NAME, "SqlType_Bit");

		/**
		 * Ordinal of DataConstructor Cal.Data.SqlType.SqlType_Bit.
		 * @see #SqlType_Bit()
		 */
		public static final int SqlType_Bit_ordinal = 9;

		/**
		 * The Boolean type is for True/False values.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr SqlType_Boolean() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.SqlType_Boolean);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.SqlType.SqlType_Boolean.
		 * @see #SqlType_Boolean()
		 */
		public static final QualifiedName SqlType_Boolean = 
			QualifiedName.make(CAL_SqlType.MODULE_NAME, "SqlType_Boolean");

		/**
		 * Ordinal of DataConstructor Cal.Data.SqlType.SqlType_Boolean.
		 * @see #SqlType_Boolean()
		 */
		public static final int SqlType_Boolean_ordinal = 10;

		/**
		 * The Char type is a fixed-length character string type.
		 * The fixed number of characters is specified.
		 * Shorter values will be padded with trailing spaces.
		 * @param length (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the number of characters in the string
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr SqlType_Char(SourceModel.Expr length) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.SqlType_Char), length});
		}

		/**
		 * @see #SqlType_Char(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param length
		 * @return org.openquark.cal.compiler.SourceModel.Expr
		 */
		public static final SourceModel.Expr SqlType_Char(int length) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.SqlType_Char), SourceModel.Expr.makeIntValue(length)});
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.SqlType.SqlType_Char.
		 * @see #SqlType_Char(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName SqlType_Char = 
			QualifiedName.make(CAL_SqlType.MODULE_NAME, "SqlType_Char");

		/**
		 * Ordinal of DataConstructor Cal.Data.SqlType.SqlType_Char.
		 * @see #SqlType_Char(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int SqlType_Char_ordinal = 11;

		/**
		 * The VarChar type is a variable-length character string type.
		 * The maximum number of characters is specified.
		 * @param length (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the maximum number of characters in the string
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr SqlType_VarChar(SourceModel.Expr length) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.SqlType_VarChar), length});
		}

		/**
		 * @see #SqlType_VarChar(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param length
		 * @return org.openquark.cal.compiler.SourceModel.Expr
		 */
		public static final SourceModel.Expr SqlType_VarChar(int length) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.SqlType_VarChar), SourceModel.Expr.makeIntValue(length)});
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.SqlType.SqlType_VarChar.
		 * @see #SqlType_VarChar(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName SqlType_VarChar = 
			QualifiedName.make(CAL_SqlType.MODULE_NAME, "SqlType_VarChar");

		/**
		 * Ordinal of DataConstructor Cal.Data.SqlType.SqlType_VarChar.
		 * @see #SqlType_VarChar(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int SqlType_VarChar_ordinal = 12;

		/**
		 * The LongVarChar type handles variable-length character strings which could be very long.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr SqlType_LongVarChar() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.SqlType_LongVarChar);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.SqlType.SqlType_LongVarChar.
		 * @see #SqlType_LongVarChar()
		 */
		public static final QualifiedName SqlType_LongVarChar = 
			QualifiedName.make(CAL_SqlType.MODULE_NAME, "SqlType_LongVarChar");

		/**
		 * Ordinal of DataConstructor Cal.Data.SqlType.SqlType_LongVarChar.
		 * @see #SqlType_LongVarChar()
		 */
		public static final int SqlType_LongVarChar_ordinal = 13;

		/**
		 * The CLOB (Character Large Object) type handles variable-length character strings which could be very long.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr SqlType_Clob() {
			return SourceModel.Expr.DataCons.make(DataConstructors.SqlType_Clob);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.SqlType.SqlType_Clob.
		 * @see #SqlType_Clob()
		 */
		public static final QualifiedName SqlType_Clob = 
			QualifiedName.make(CAL_SqlType.MODULE_NAME, "SqlType_Clob");

		/**
		 * Ordinal of DataConstructor Cal.Data.SqlType.SqlType_Clob.
		 * @see #SqlType_Clob()
		 */
		public static final int SqlType_Clob_ordinal = 14;

		/**
		 * The Binary type is a fixed-length binary type
		 * @param length (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the number of bytes in the binary values
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr SqlType_Binary(SourceModel.Expr length) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.SqlType_Binary), length});
		}

		/**
		 * @see #SqlType_Binary(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param length
		 * @return org.openquark.cal.compiler.SourceModel.Expr
		 */
		public static final SourceModel.Expr SqlType_Binary(int length) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.SqlType_Binary), SourceModel.Expr.makeIntValue(length)});
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.SqlType.SqlType_Binary.
		 * @see #SqlType_Binary(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName SqlType_Binary = 
			QualifiedName.make(CAL_SqlType.MODULE_NAME, "SqlType_Binary");

		/**
		 * Ordinal of DataConstructor Cal.Data.SqlType.SqlType_Binary.
		 * @see #SqlType_Binary(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int SqlType_Binary_ordinal = 15;

		/**
		 * The VarBinary type is a variable-length binary type.
		 * @param length (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the maximum number of bytes in the binary values
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr SqlType_VarBinary(SourceModel.Expr length) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.SqlType_VarBinary), length});
		}

		/**
		 * @see #SqlType_VarBinary(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param length
		 * @return org.openquark.cal.compiler.SourceModel.Expr
		 */
		public static final SourceModel.Expr SqlType_VarBinary(int length) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.SqlType_VarBinary), SourceModel.Expr.makeIntValue(length)});
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.SqlType.SqlType_VarBinary.
		 * @see #SqlType_VarBinary(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName SqlType_VarBinary = 
			QualifiedName.make(CAL_SqlType.MODULE_NAME, "SqlType_VarBinary");

		/**
		 * Ordinal of DataConstructor Cal.Data.SqlType.SqlType_VarBinary.
		 * @see #SqlType_VarBinary(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int SqlType_VarBinary_ordinal = 16;

		/**
		 * The LongVarBinary type handles variable-length binary values which could be very long.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr SqlType_LongVarBinary() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.SqlType_LongVarBinary);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.SqlType.SqlType_LongVarBinary.
		 * @see #SqlType_LongVarBinary()
		 */
		public static final QualifiedName SqlType_LongVarBinary = 
			QualifiedName.make(CAL_SqlType.MODULE_NAME, "SqlType_LongVarBinary");

		/**
		 * Ordinal of DataConstructor Cal.Data.SqlType.SqlType_LongVarBinary.
		 * @see #SqlType_LongVarBinary()
		 */
		public static final int SqlType_LongVarBinary_ordinal = 17;

		/**
		 * The Blob type handles variable-length binary values which could be very long.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr SqlType_Blob() {
			return SourceModel.Expr.DataCons.make(DataConstructors.SqlType_Blob);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.SqlType.SqlType_Blob.
		 * @see #SqlType_Blob()
		 */
		public static final QualifiedName SqlType_Blob = 
			QualifiedName.make(CAL_SqlType.MODULE_NAME, "SqlType_Blob");

		/**
		 * Ordinal of DataConstructor Cal.Data.SqlType.SqlType_Blob.
		 * @see #SqlType_Blob()
		 */
		public static final int SqlType_Blob_ordinal = 18;

		/**
		 * The Date type handles Year/Month/Day values.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr SqlType_Date() {
			return SourceModel.Expr.DataCons.make(DataConstructors.SqlType_Date);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.SqlType.SqlType_Date.
		 * @see #SqlType_Date()
		 */
		public static final QualifiedName SqlType_Date = 
			QualifiedName.make(CAL_SqlType.MODULE_NAME, "SqlType_Date");

		/**
		 * Ordinal of DataConstructor Cal.Data.SqlType.SqlType_Date.
		 * @see #SqlType_Date()
		 */
		public static final int SqlType_Date_ordinal = 19;

		/**
		 * The Time type handles Hour/Minute/Second values (and possibly fractions of a second).
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr SqlType_Time() {
			return SourceModel.Expr.DataCons.make(DataConstructors.SqlType_Time);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.SqlType.SqlType_Time.
		 * @see #SqlType_Time()
		 */
		public static final QualifiedName SqlType_Time = 
			QualifiedName.make(CAL_SqlType.MODULE_NAME, "SqlType_Time");

		/**
		 * Ordinal of DataConstructor Cal.Data.SqlType.SqlType_Time.
		 * @see #SqlType_Time()
		 */
		public static final int SqlType_Time_ordinal = 20;

		/**
		 * The TimeStamp type combines the Date an Time type information, handling Year/Month/Day and Hour/Minute/Second.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr SqlType_TimeStamp() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.SqlType_TimeStamp);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.SqlType.SqlType_TimeStamp.
		 * @see #SqlType_TimeStamp()
		 */
		public static final QualifiedName SqlType_TimeStamp = 
			QualifiedName.make(CAL_SqlType.MODULE_NAME, "SqlType_TimeStamp");

		/**
		 * Ordinal of DataConstructor Cal.Data.SqlType.SqlType_TimeStamp.
		 * @see #SqlType_TimeStamp()
		 */
		public static final int SqlType_TimeStamp_ordinal = 21;

		/**
		 * The Array type is used for handling arrays of values.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr SqlType_Array() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.SqlType_Array);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.SqlType.SqlType_Array.
		 * @see #SqlType_Array()
		 */
		public static final QualifiedName SqlType_Array = 
			QualifiedName.make(CAL_SqlType.MODULE_NAME, "SqlType_Array");

		/**
		 * Ordinal of DataConstructor Cal.Data.SqlType.SqlType_Array.
		 * @see #SqlType_Array()
		 */
		public static final int SqlType_Array_ordinal = 22;

		/**
		 * The DataLink type is used for referencing values external to the database (using URLs or the like).
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr SqlType_Datalink() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.SqlType_Datalink);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.SqlType.SqlType_Datalink.
		 * @see #SqlType_Datalink()
		 */
		public static final QualifiedName SqlType_Datalink = 
			QualifiedName.make(CAL_SqlType.MODULE_NAME, "SqlType_Datalink");

		/**
		 * Ordinal of DataConstructor Cal.Data.SqlType.SqlType_Datalink.
		 * @see #SqlType_Datalink()
		 */
		public static final int SqlType_Datalink_ordinal = 23;

		/**
		 * The Distinct type is used for handling distinct values of some other type.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr SqlType_Distinct() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.SqlType_Distinct);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.SqlType.SqlType_Distinct.
		 * @see #SqlType_Distinct()
		 */
		public static final QualifiedName SqlType_Distinct = 
			QualifiedName.make(CAL_SqlType.MODULE_NAME, "SqlType_Distinct");

		/**
		 * Ordinal of DataConstructor Cal.Data.SqlType.SqlType_Distinct.
		 * @see #SqlType_Distinct()
		 */
		public static final int SqlType_Distinct_ordinal = 24;

		/**
		 * The JavaObject type is used for handling serialized Java Objects.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr SqlType_JavaObject() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.SqlType_JavaObject);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.SqlType.SqlType_JavaObject.
		 * @see #SqlType_JavaObject()
		 */
		public static final QualifiedName SqlType_JavaObject = 
			QualifiedName.make(CAL_SqlType.MODULE_NAME, "SqlType_JavaObject");

		/**
		 * Ordinal of DataConstructor Cal.Data.SqlType.SqlType_JavaObject.
		 * @see #SqlType_JavaObject()
		 */
		public static final int SqlType_JavaObject_ordinal = 25;

		/**
		 * The Ref type is used for referencing a instance of a Struct type.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr SqlType_Ref() {
			return SourceModel.Expr.DataCons.make(DataConstructors.SqlType_Ref);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.SqlType.SqlType_Ref.
		 * @see #SqlType_Ref()
		 */
		public static final QualifiedName SqlType_Ref = 
			QualifiedName.make(CAL_SqlType.MODULE_NAME, "SqlType_Ref");

		/**
		 * Ordinal of DataConstructor Cal.Data.SqlType.SqlType_Ref.
		 * @see #SqlType_Ref()
		 */
		public static final int SqlType_Ref_ordinal = 26;

		/**
		 * The Struct type is used for handling SQL structured types, consisting of a number of attributes.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr SqlType_Struct() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.SqlType_Struct);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.SqlType.SqlType_Struct.
		 * @see #SqlType_Struct()
		 */
		public static final QualifiedName SqlType_Struct = 
			QualifiedName.make(CAL_SqlType.MODULE_NAME, "SqlType_Struct");

		/**
		 * Ordinal of DataConstructor Cal.Data.SqlType.SqlType_Struct.
		 * @see #SqlType_Struct()
		 */
		public static final int SqlType_Struct_ordinal = 27;

		/**
		 * The Null type can be used in cases where there is no value.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr SqlType_Null() {
			return SourceModel.Expr.DataCons.make(DataConstructors.SqlType_Null);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.SqlType.SqlType_Null.
		 * @see #SqlType_Null()
		 */
		public static final QualifiedName SqlType_Null = 
			QualifiedName.make(CAL_SqlType.MODULE_NAME, "SqlType_Null");

		/**
		 * Ordinal of DataConstructor Cal.Data.SqlType.SqlType_Null.
		 * @see #SqlType_Null()
		 */
		public static final int SqlType_Null_ordinal = 28;

		/**
		 * The Other type is used for any type not otherwise listed here.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr SqlType_Other() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.SqlType_Other);
		}

		/**
		 * Name binding for DataConstructor: Cal.Data.SqlType.SqlType_Other.
		 * @see #SqlType_Other()
		 */
		public static final QualifiedName SqlType_Other = 
			QualifiedName.make(CAL_SqlType.MODULE_NAME, "SqlType_Other");

		/**
		 * Ordinal of DataConstructor Cal.Data.SqlType.SqlType_Other.
		 * @see #SqlType_Other()
		 */
		public static final int SqlType_Other_ordinal = 29;

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Data.SqlType module.
	 */
	public static final class Functions {
		/**
		 * Returns the SqlType for a specified JDBC type code and other info.
		 * @param jdbcTypeCode (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @param columnSize (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @param decimalDigits (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @return (CAL type: <code>Cal.Data.SqlType.SqlType</code>) 
		 */
		public static final SourceModel.Expr fromJdbcMetadata(SourceModel.Expr jdbcTypeCode, SourceModel.Expr columnSize, SourceModel.Expr decimalDigits) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromJdbcMetadata), jdbcTypeCode, columnSize, decimalDigits});
		}

		/**
		 * @see #fromJdbcMetadata(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param jdbcTypeCode
		 * @param columnSize
		 * @param decimalDigits
		 * @return the SourceModel.Expr representing an application of fromJdbcMetadata
		 */
		public static final SourceModel.Expr fromJdbcMetadata(int jdbcTypeCode, int columnSize, int decimalDigits) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromJdbcMetadata), SourceModel.Expr.makeIntValue(jdbcTypeCode), SourceModel.Expr.makeIntValue(columnSize), SourceModel.Expr.makeIntValue(decimalDigits)});
		}

		/**
		 * Name binding for function: fromJdbcMetadata.
		 * @see #fromJdbcMetadata(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fromJdbcMetadata = 
			QualifiedName.make(CAL_SqlType.MODULE_NAME, "fromJdbcMetadata");

		/**
		 * Returns a sensible SQL type for the specified ValueType.
		 * A length (for strings) may optionally be specified.
		 * @param valueType (CAL type: <code>Cal.Utilities.ValueType.ValueType</code>)
		 * @param maybeLength (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Core.Prelude.Int</code>)
		 * @return (CAL type: <code>Cal.Data.SqlType.SqlType</code>) 
		 */
		public static final SourceModel.Expr fromValueType(SourceModel.Expr valueType, SourceModel.Expr maybeLength) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromValueType), valueType, maybeLength});
		}

		/**
		 * Name binding for function: fromValueType.
		 * @see #fromValueType(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fromValueType = 
			QualifiedName.make(CAL_SqlType.MODULE_NAME, "fromValueType");

		/**
		 * Input an instance of SqlType.
		 * Translates an instance of JSqlType to
		 * an instance of SqlType.
		 * @param jSqlType_ (CAL type: <code>Cal.Data.SqlType.JSqlType</code>)
		 * @return (CAL type: <code>Cal.Data.SqlType.SqlType</code>) 
		 */
		public static final SourceModel.Expr inputSqlType(SourceModel.Expr jSqlType_) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inputSqlType), jSqlType_});
		}

		/**
		 * Name binding for function: inputSqlType.
		 * @see #inputSqlType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName inputSqlType = 
			QualifiedName.make(CAL_SqlType.MODULE_NAME, "inputSqlType");

		/**
		 * Returns whether the SQL type corresponds to one of the 'long' types (LongVarChar, LongVarBinary, Blob, or Clob).
		 * @param sqlType (CAL type: <code>Cal.Data.SqlType.SqlType</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr isLongType(SourceModel.Expr sqlType) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isLongType), sqlType});
		}

		/**
		 * Name binding for function: isLongType.
		 * @see #isLongType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isLongType = 
			QualifiedName.make(CAL_SqlType.MODULE_NAME, "isLongType");

		/**
		 * Output an instance of SqlType.
		 * Translates an instance of SqlType to
		 * an instance of JSqlType.
		 * @param dcInstance (CAL type: <code>Cal.Data.SqlType.SqlType</code>)
		 * @return (CAL type: <code>Cal.Data.SqlType.JSqlType</code>) 
		 */
		public static final SourceModel.Expr outputSqlType(SourceModel.Expr dcInstance) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputSqlType), dcInstance});
		}

		/**
		 * Name binding for function: outputSqlType.
		 * @see #outputSqlType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName outputSqlType = 
			QualifiedName.make(CAL_SqlType.MODULE_NAME, "outputSqlType");

		/**
		 * Returns the <code>Cal.Utilities.ValueType.ValueType</code> which best matches the SQL type.
		 * NullType will be returned if there are no other appropriate ValueType mappings.
		 * @param sqlType (CAL type: <code>Cal.Data.SqlType.SqlType</code>)
		 * @return (CAL type: <code>Cal.Utilities.ValueType.ValueType</code>) 
		 */
		public static final SourceModel.Expr toValueType(SourceModel.Expr sqlType) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toValueType), sqlType});
		}

		/**
		 * Name binding for function: toValueType.
		 * @see #toValueType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName toValueType = 
			QualifiedName.make(CAL_SqlType.MODULE_NAME, "toValueType");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = 1563932590;

}
