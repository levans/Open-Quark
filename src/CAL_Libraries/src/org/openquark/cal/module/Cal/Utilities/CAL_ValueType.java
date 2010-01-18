/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_ValueType.java)
 * was generated from CAL module: Cal.Utilities.ValueType.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Utilities.ValueType module from Java code.
 *  
 * Creation date: Tue Aug 28 15:58:42 PDT 2007
 * --!>
 *  
 */

package org.openquark.cal.module.Cal.Utilities;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * This module defines an enumeration to represent several common types.
 * <p>
 * The types currently represented are:
 * <ul>
 *  <li>
 *   null
 *  </li>
 *  <li>
 *   string
 *  </li>
 *  <li>
 *   int
 *  </li>
 *  <li>
 *   double
 *  </li>
 *  <li>
 *   Boolean
 *  </li>
 *  <li>
 *   time
 *  </li>
 *  <li>
 *   binary (i.e. array of bytes)
 *  </li>
 * </ul>
 * <p>
 * <strong>NB:</strong> There is a Java type-safe enumeration <code>JValueType</code> that must be kept in synch with <code>Cal.Utilities.ValueType.ValueType</code>.
 * 
 * @author Richard Webster
 */
public final class CAL_ValueType {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Utilities.ValueType");

	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Utilities.ValueType module.
	 */
	public static final class TypeConstructors {
		/** Name binding for TypeConsApp: JValueType. */
		public static final QualifiedName JValueType = 
			QualifiedName.make(CAL_ValueType.MODULE_NAME, "JValueType");

		/** Name binding for TypeConsApp: ValueType. */
		public static final QualifiedName ValueType = 
			QualifiedName.make(CAL_ValueType.MODULE_NAME, "ValueType");

	}
	/**
	 * This inner class (DataConstructors) contains constants
	 * and methods related to binding to CAL DataConstructors in the Cal.Utilities.ValueType module.
	 */
	public static final class DataConstructors {
		/*
		 * DataConstructors for the Cal.Utilities.ValueType.ValueType data type.
		 */

		/**
		 * Binding for DataConstructor: Cal.Utilities.ValueType.NullType.
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.ValueType.NullType
		 */
		public static final SourceModel.Expr NullType() {
			return SourceModel.Expr.DataCons.make(DataConstructors.NullType);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.ValueType.NullType.
		 * @see #NullType()
		 */
		public static final QualifiedName NullType = 
			QualifiedName.make(CAL_ValueType.MODULE_NAME, "NullType");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.ValueType.NullType.
		 * @see #NullType()
		 */
		public static final int NullType_ordinal = 0;

		/**
		 * Binding for DataConstructor: Cal.Utilities.ValueType.StringType.
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.ValueType.StringType
		 */
		public static final SourceModel.Expr StringType() {
			return SourceModel.Expr.DataCons.make(DataConstructors.StringType);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.ValueType.StringType.
		 * @see #StringType()
		 */
		public static final QualifiedName StringType = 
			QualifiedName.make(CAL_ValueType.MODULE_NAME, "StringType");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.ValueType.StringType.
		 * @see #StringType()
		 */
		public static final int StringType_ordinal = 1;

		/**
		 * Binding for DataConstructor: Cal.Utilities.ValueType.IntType.
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.ValueType.IntType
		 */
		public static final SourceModel.Expr IntType() {
			return SourceModel.Expr.DataCons.make(DataConstructors.IntType);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.ValueType.IntType.
		 * @see #IntType()
		 */
		public static final QualifiedName IntType = 
			QualifiedName.make(CAL_ValueType.MODULE_NAME, "IntType");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.ValueType.IntType.
		 * @see #IntType()
		 */
		public static final int IntType_ordinal = 2;

		/**
		 * Binding for DataConstructor: Cal.Utilities.ValueType.DoubleType.
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.ValueType.DoubleType
		 */
		public static final SourceModel.Expr DoubleType() {
			return SourceModel.Expr.DataCons.make(DataConstructors.DoubleType);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.ValueType.DoubleType.
		 * @see #DoubleType()
		 */
		public static final QualifiedName DoubleType = 
			QualifiedName.make(CAL_ValueType.MODULE_NAME, "DoubleType");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.ValueType.DoubleType.
		 * @see #DoubleType()
		 */
		public static final int DoubleType_ordinal = 3;

		/**
		 * Binding for DataConstructor: Cal.Utilities.ValueType.BooleanType.
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.ValueType.BooleanType
		 */
		public static final SourceModel.Expr BooleanType() {
			return SourceModel.Expr.DataCons.make(DataConstructors.BooleanType);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.ValueType.BooleanType.
		 * @see #BooleanType()
		 */
		public static final QualifiedName BooleanType = 
			QualifiedName.make(CAL_ValueType.MODULE_NAME, "BooleanType");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.ValueType.BooleanType.
		 * @see #BooleanType()
		 */
		public static final int BooleanType_ordinal = 4;

		/**
		 * Binding for DataConstructor: Cal.Utilities.ValueType.TimeType.
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.ValueType.TimeType
		 */
		public static final SourceModel.Expr TimeType() {
			return SourceModel.Expr.DataCons.make(DataConstructors.TimeType);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.ValueType.TimeType.
		 * @see #TimeType()
		 */
		public static final QualifiedName TimeType = 
			QualifiedName.make(CAL_ValueType.MODULE_NAME, "TimeType");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.ValueType.TimeType.
		 * @see #TimeType()
		 */
		public static final int TimeType_ordinal = 5;

		/**
		 * Binding for DataConstructor: Cal.Utilities.ValueType.BinaryType.
		 * @return the SourceModule.Expr representing an application of Cal.Utilities.ValueType.BinaryType
		 */
		public static final SourceModel.Expr BinaryType() {
			return SourceModel.Expr.DataCons.make(DataConstructors.BinaryType);
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.ValueType.BinaryType.
		 * @see #BinaryType()
		 */
		public static final QualifiedName BinaryType = 
			QualifiedName.make(CAL_ValueType.MODULE_NAME, "BinaryType");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.ValueType.BinaryType.
		 * @see #BinaryType()
		 */
		public static final int BinaryType_ordinal = 6;

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Utilities.ValueType module.
	 */
	public static final class Functions {
		/**
		 * Returns the value type corresponding to the specified type rep.
		 * NullType is returned if there is no value type for the type rep.
		 * @param typeRep (CAL type: <code>Cal.Core.Prelude.TypeRep</code>)
		 * @return (CAL type: <code>Cal.Utilities.ValueType.ValueType</code>) 
		 */
		public static final SourceModel.Expr fromTypeRep(SourceModel.Expr typeRep) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromTypeRep), typeRep});
		}

		/**
		 * Name binding for function: fromTypeRep.
		 * @see #fromTypeRep(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fromTypeRep = 
			QualifiedName.make(CAL_ValueType.MODULE_NAME, "fromTypeRep");

		/**
		 * Returns whether the type is one of the numeric types.
		 * @param valueType (CAL type: <code>Cal.Utilities.ValueType.ValueType</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr isNumericType(SourceModel.Expr valueType) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isNumericType), valueType});
		}

		/**
		 * Name binding for function: isNumericType.
		 * @see #isNumericType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isNumericType = 
			QualifiedName.make(CAL_ValueType.MODULE_NAME, "isNumericType");

		/**
		 * Helper binding method for function: outputValueType. 
		 * @param valueType
		 * @return the SourceModule.expr representing an application of outputValueType
		 */
		public static final SourceModel.Expr outputValueType(SourceModel.Expr valueType) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputValueType), valueType});
		}

		/**
		 * Name binding for function: outputValueType.
		 * @see #outputValueType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName outputValueType = 
			QualifiedName.make(CAL_ValueType.MODULE_NAME, "outputValueType");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = -969839035;

}
