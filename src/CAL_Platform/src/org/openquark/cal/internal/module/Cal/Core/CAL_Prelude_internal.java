/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_Prelude_internal.java)
 * was generated from CAL module: Cal.Core.Prelude.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Core.Prelude module from Java code.
 *  
 * Creation date: Tue Jul 31 18:06:12 PDT 2007
 * --!>
 *  
 */

package org.openquark.cal.internal.module.Cal.Core;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * This is the CAL Prelude module. The Prelude module is the core module of CAL and must be
 * imported by every other CAL module.
 * <p>
 * The Prelude defines the primitive types <code>Cal.Core.Prelude.Char</code>, <code>Cal.Core.Prelude.Boolean</code>, <code>Cal.Core.Prelude.Byte</code>, <code>Cal.Core.Prelude.Short</code>,
 * <code>Cal.Core.Prelude.Int</code>, <code>Cal.Core.Prelude.Long</code>, <code>Cal.Core.Prelude.Float</code> and <code>Cal.Core.Prelude.Double</code> that correspond to the primitive
 * unboxed Java types. It also defines other important CAL types such as <code>Cal.Core.Prelude.String</code>, <code>Cal.Core.Prelude.Function</code>,
 * <code>Cal.Core.Prelude.List</code>, <code>Cal.Core.Prelude.Maybe</code>, <code>Cal.Core.Prelude.Either</code>, <code>Cal.Core.Prelude.Unit</code> and the built-in record and tuple types.
 * <p>
 * The Prelude defines many core type classes: <code>Cal.Core.Prelude.Eq</code>, <code>Cal.Core.Prelude.Ord</code>, <code>Cal.Core.Prelude.Num</code>,
 * <code>Cal.Core.Prelude.Inputable</code>, <code>Cal.Core.Prelude.Outputable</code>, <code>Cal.Core.Prelude.Appendable</code>, <code>Cal.Core.Prelude.Bounded</code>,
 * <code>Cal.Core.Prelude.Enum</code> and <code>Cal.Core.Prelude.Typeable</code>, as well as appropriate instances of these classes for the
 * types described above.
 * <p>
 * Finally, the Prelude contains the definitions of many functions generally useful in writing CAL 
 * programs and working with values of the above types. 
 * <p>
 * Note that although the <code>Cal.Core.Prelude.String</code> and <code>Cal.Core.Prelude.List</code> types are defined in the Prelude module,
 * most of the useful basic functions for working specifically with them are to be found in the
 * <code>Cal.Core.String</code> and <code>Cal.Collections.List</code> modules respectively.
 * <p>
 * It is not safe for users to alter the contents of the Prelude module. The Prelude module
 * contains many basic constructs essential to CAL's internal operation and altering these can result
 * in unpredictable failures.
 * <p>
 * The CAL language is a lazy strongly-typed functional language supporting close integration with the
 * Java platform. Many of the core CAL modules contain features that are adaptations of work specified in
 * the Haskell 98 Language and Libraries report <a href='http://www.haskell.org/onlinereport/'>http://www.haskell.org/onlinereport/</a>. 
 * <p>
 * Some optimized implementations of CAL functions are adapted from versions of the Haskell standard libraries
 * as implemented by the Hugs 98 Haskell interpreter <a href='http://www.haskell.org/hugs/'>http://www.haskell.org/hugs/</a> and the Glasgow Haskell
 * Compiler <a href='http://www.haskell.org/ghc/'>http://www.haskell.org/ghc/</a>.
 * <p>
 * See the file <code>ThirdPartyComponents/ThirdPartyComponents.txt</code> for the Hugs license and
 * the Glasgow Haskell Compiler license.
 * 
 * @author Bo Ilic
 */
public final class CAL_Prelude_internal {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Core.Prelude");

	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Core.Prelude module.
	 */
	public static final class TypeConstructors {
		/**
		 * This foreign type represents a CAL execution context.
		 * <p>
		 * The execution context provides an environment in which the execution of a CAL function occurs.
		 * One of the most important tasks of an execution context is to provide a key to the
		 * set of constant applicative form (CAF) values that are in use for the given execution.
		 * <p>
		 * Therefore, one must be careful that the execution context itself does not end up cached inside
		 * a CAF, for it may hinder with the timely cleanup of the cached CAFs for the execution context.
		 * <p>
		 * The execution context holds onto an immutable set of properties which is specified by client code
		 * on construction of the execution context, and can be accessed from within CAL. Some well-known properties
		 * are defined by the platform (e.g. the current locale).
		 */
		public static final QualifiedName ExecutionContext = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"ExecutionContext");

		/** Name binding for TypeConsApp: FieldName. */
		public static final QualifiedName FieldName = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "FieldName");

		/** Name binding for TypeConsApp: JBoolean. */
		public static final QualifiedName JBoolean = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "JBoolean");

		/** Name binding for TypeConsApp: JByte. */
		public static final QualifiedName JByte = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "JByte");

		/** Name binding for TypeConsApp: JChar. */
		public static final QualifiedName JChar = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "JChar");

		/** Name binding for TypeConsApp: JClass. */
		public static final QualifiedName JClass = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "JClass");

		/** Name binding for TypeConsApp: JComparable. */
		public static final QualifiedName JComparable = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "JComparable");

		/** Name binding for TypeConsApp: JComparator. */
		public static final QualifiedName JComparator = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "JComparator");

		/** Name binding for TypeConsApp: JDouble. */
		public static final QualifiedName JDouble = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "JDouble");

		/** Name binding for TypeConsApp: JEitherValue. */
		public static final QualifiedName JEitherValue = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "JEitherValue");

		/** Name binding for TypeConsApp: JEnumeration. */
		public static final QualifiedName JEnumeration = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "JEnumeration");

		/** Name binding for TypeConsApp: JEquivalenceRelation. */
		public static final QualifiedName JEquivalenceRelation = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"JEquivalenceRelation");

		/** Name binding for TypeConsApp: JFloat. */
		public static final QualifiedName JFloat = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "JFloat");

		/** Name binding for TypeConsApp: JInt. */
		public static final QualifiedName JInt = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "JInt");

		/** Name binding for TypeConsApp: JIterator. */
		public static final QualifiedName JIterator = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "JIterator");

		/** Name binding for TypeConsApp: JLong. */
		public static final QualifiedName JLong = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "JLong");

		/** Name binding for TypeConsApp: JMaybeValue. */
		public static final QualifiedName JMaybeValue = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "JMaybeValue");

		/** Name binding for TypeConsApp: JOrderingValue. */
		public static final QualifiedName JOrderingValue = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"JOrderingValue");

		/** Name binding for TypeConsApp: JShort. */
		public static final QualifiedName JShort = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "JShort");

		/** Name binding for TypeConsApp: JStringBuilder. */
		public static final QualifiedName JStringBuilder = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"JStringBuilder");

		/** Name binding for TypeConsApp: JUnitValue. */
		public static final QualifiedName JUnitValue = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "JUnitValue");

		/**
		 * A common interface that is implemented by the machine-internal record type dictionaries for both
		 * the lecc and g machines.  The <code>Cal.Core.Prelude.TypeRep</code> for record types (<code>Cal.Core.Prelude.RecordTypeRep</code>) has a
		 * <code>Cal.Core.Prelude.RecordType</code> field which is referred to for obtaining the type information for a record's fields.
		 */
		public static final QualifiedName RecordType = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "RecordType");

		/** Name binding for TypeConsApp: TypeRepArray. */
		public static final QualifiedName TypeRepArray = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "TypeRepArray");

	}
	/**
	 * This inner class (DataConstructors) contains constants
	 * and methods related to binding to CAL DataConstructors in the Cal.Core.Prelude module.
	 */
	public static final class DataConstructors {
		/*
		 * DataConstructors for the Cal.Core.Prelude.TypeRep data type.
		 */

		/**
		 * Used for algebraic type constructors not supported via special TypeRep data constructors.
		 * @param typeConsName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          root type constructor
		 * @param argTypes (CAL type: <code>Cal.Core.Prelude.TypeRepArray</code>)
		 *          type representation of the arguments
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr AlgebraicTypeRep(SourceModel.Expr typeConsName, SourceModel.Expr argTypes) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.AlgebraicTypeRep), typeConsName, argTypes});
		}

		/**
		 * @see #AlgebraicTypeRep(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param typeConsName
		 * @param argTypes
		 * @return org.openquark.cal.compiler.SourceModel.Expr
		 */
		public static final SourceModel.Expr AlgebraicTypeRep(java.lang.String typeConsName, SourceModel.Expr argTypes) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.AlgebraicTypeRep), SourceModel.Expr.makeStringValue(typeConsName), argTypes});
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Prelude.AlgebraicTypeRep.
		 * @see #AlgebraicTypeRep(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName AlgebraicTypeRep = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"AlgebraicTypeRep");

		/**
		 * Ordinal of DataConstructor Cal.Core.Prelude.AlgebraicTypeRep.
		 * @see #AlgebraicTypeRep(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int AlgebraicTypeRep_ordinal = 0;

		/**
		 * Used for foreign types i.e. the implementation type is a Java object type or a Java primitive
		 * type. Not used for <code>Cal.Core.Prelude.String</code>, <code>Cal.Core.Prelude.Char</code>, <code>Cal.Core.Prelude.Byte</code>, <code>Cal.Core.Prelude.Short</code>, <code>Cal.Core.Prelude.Int</code>,
		 * <code>Cal.Core.Prelude.Long</code>, <code>Cal.Core.Prelude.Float</code> and <code>Cal.Core.Prelude.Double</code> which have their own special data constructors
		 * even though they are technically foreign types.
		 * @param typeConsName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the name of the foreign type.
		 * @param foreignClass (CAL type: <code>Cal.Core.Prelude.JClass</code>)
		 *          the class corresponding to the foreign type.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr ForeignTypeRep(SourceModel.Expr typeConsName, SourceModel.Expr foreignClass) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.ForeignTypeRep), typeConsName, foreignClass});
		}

		/**
		 * @see #ForeignTypeRep(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param typeConsName
		 * @param foreignClass
		 * @return org.openquark.cal.compiler.SourceModel.Expr
		 */
		public static final SourceModel.Expr ForeignTypeRep(java.lang.String typeConsName, SourceModel.Expr foreignClass) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.ForeignTypeRep), SourceModel.Expr.makeStringValue(typeConsName), foreignClass});
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Prelude.ForeignTypeRep.
		 * @see #ForeignTypeRep(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName ForeignTypeRep = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"ForeignTypeRep");

		/**
		 * Ordinal of DataConstructor Cal.Core.Prelude.ForeignTypeRep.
		 * @see #ForeignTypeRep(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int ForeignTypeRep_ordinal = 1;

		/**
		 * Used for representing records.
		 * @param dictionary (CAL type: <code>Cal.Core.Prelude.RecordType</code>)
		 *          A <code>Cal.Core.Prelude.RecordType</code> object that points to a machine-specific <code>Cal.Core.Prelude.Typeable</code> dictionary for the 
		 * record in question.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr RecordTypeRep(SourceModel.Expr dictionary) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.RecordTypeRep), dictionary});
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Prelude.RecordTypeRep.
		 * @see #RecordTypeRep(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName RecordTypeRep = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"RecordTypeRep");

		/**
		 * Ordinal of DataConstructor Cal.Core.Prelude.RecordTypeRep.
		 * @see #RecordTypeRep(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int RecordTypeRep_ordinal = 2;

		/**
		 * Used for the <code>Cal.Core.Prelude.Function</code> type constructor "-&gt;" when fully saturated.
		 * @param domainType (CAL type: <code>Cal.Core.Prelude.TypeRep</code>)
		 * @param codomainType (CAL type: <code>Cal.Core.Prelude.TypeRep</code>)
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr FunctionTypeRep(SourceModel.Expr domainType, SourceModel.Expr codomainType) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.FunctionTypeRep), domainType, codomainType});
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Prelude.FunctionTypeRep.
		 * @see #FunctionTypeRep(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName FunctionTypeRep = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"FunctionTypeRep");

		/**
		 * Ordinal of DataConstructor Cal.Core.Prelude.FunctionTypeRep.
		 * @see #FunctionTypeRep(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int FunctionTypeRep_ordinal = 3;

		/**
		 * Used for the <code>Cal.Core.Prelude.List</code> type constructor when fully saturated.
		 * @param elementType (CAL type: <code>Cal.Core.Prelude.TypeRep</code>)
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr ListTypeRep(SourceModel.Expr elementType) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.ListTypeRep), elementType});
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Prelude.ListTypeRep.
		 * @see #ListTypeRep(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName ListTypeRep = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "ListTypeRep");

		/**
		 * Ordinal of DataConstructor Cal.Core.Prelude.ListTypeRep.
		 * @see #ListTypeRep(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int ListTypeRep_ordinal = 4;

		/**
		 * Used for the <code>Cal.Core.Prelude.Unit</code> type constructor.
		 * representation of the <code>()</code> type
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr UnitTypeRep() {
			return SourceModel.Expr.DataCons.make(DataConstructors.UnitTypeRep);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Prelude.UnitTypeRep.
		 * @see #UnitTypeRep()
		 */
		public static final QualifiedName UnitTypeRep = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "UnitTypeRep");

		/**
		 * Ordinal of DataConstructor Cal.Core.Prelude.UnitTypeRep.
		 * @see #UnitTypeRep()
		 */
		public static final int UnitTypeRep_ordinal = 5;

		/**
		 * Used for the <code>Cal.Core.Prelude.Boolean</code> type constructor.
		 * representation of the <code>Cal.Core.Prelude.Boolean</code> type
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr BooleanTypeRep() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.BooleanTypeRep);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Prelude.BooleanTypeRep.
		 * @see #BooleanTypeRep()
		 */
		public static final QualifiedName BooleanTypeRep = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"BooleanTypeRep");

		/**
		 * Ordinal of DataConstructor Cal.Core.Prelude.BooleanTypeRep.
		 * @see #BooleanTypeRep()
		 */
		public static final int BooleanTypeRep_ordinal = 6;

		/**
		 * Used for the <code>Cal.Core.Prelude.Int</code> type constructor.
		 * representation of the <code>Cal.Core.Prelude.Int</code> type
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr IntTypeRep() {
			return SourceModel.Expr.DataCons.make(DataConstructors.IntTypeRep);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Prelude.IntTypeRep.
		 * @see #IntTypeRep()
		 */
		public static final QualifiedName IntTypeRep = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "IntTypeRep");

		/**
		 * Ordinal of DataConstructor Cal.Core.Prelude.IntTypeRep.
		 * @see #IntTypeRep()
		 */
		public static final int IntTypeRep_ordinal = 7;

		/**
		 * Used for the <code>Cal.Core.Prelude.Byte</code> type constructor.
		 * representation of the <code>Cal.Core.Prelude.Byte</code> type
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr ByteTypeRep() {
			return SourceModel.Expr.DataCons.make(DataConstructors.ByteTypeRep);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Prelude.ByteTypeRep.
		 * @see #ByteTypeRep()
		 */
		public static final QualifiedName ByteTypeRep = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "ByteTypeRep");

		/**
		 * Ordinal of DataConstructor Cal.Core.Prelude.ByteTypeRep.
		 * @see #ByteTypeRep()
		 */
		public static final int ByteTypeRep_ordinal = 8;

		/**
		 * Used for the <code>Cal.Core.Prelude.Short</code> type constructor.
		 * representation of the <code>Cal.Core.Prelude.Short</code> type
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr ShortTypeRep() {
			return SourceModel.Expr.DataCons.make(DataConstructors.ShortTypeRep);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Prelude.ShortTypeRep.
		 * @see #ShortTypeRep()
		 */
		public static final QualifiedName ShortTypeRep = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "ShortTypeRep");

		/**
		 * Ordinal of DataConstructor Cal.Core.Prelude.ShortTypeRep.
		 * @see #ShortTypeRep()
		 */
		public static final int ShortTypeRep_ordinal = 9;

		/**
		 * Used for the <code>Cal.Core.Prelude.Long</code> type constructor.
		 * representation of the <code>Cal.Core.Prelude.Long</code> type
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr LongTypeRep() {
			return SourceModel.Expr.DataCons.make(DataConstructors.LongTypeRep);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Prelude.LongTypeRep.
		 * @see #LongTypeRep()
		 */
		public static final QualifiedName LongTypeRep = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "LongTypeRep");

		/**
		 * Ordinal of DataConstructor Cal.Core.Prelude.LongTypeRep.
		 * @see #LongTypeRep()
		 */
		public static final int LongTypeRep_ordinal = 10;

		/**
		 * Used for the <code>Cal.Core.Prelude.Float</code> type constructor.
		 * representation of the <code>Cal.Core.Prelude.Float</code> type
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr FloatTypeRep() {
			return SourceModel.Expr.DataCons.make(DataConstructors.FloatTypeRep);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Prelude.FloatTypeRep.
		 * @see #FloatTypeRep()
		 */
		public static final QualifiedName FloatTypeRep = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "FloatTypeRep");

		/**
		 * Ordinal of DataConstructor Cal.Core.Prelude.FloatTypeRep.
		 * @see #FloatTypeRep()
		 */
		public static final int FloatTypeRep_ordinal = 11;

		/**
		 * Used for the <code>Cal.Core.Prelude.Double</code> type constructor.
		 * representation of the <code>Cal.Core.Prelude.Double</code> type
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr DoubleTypeRep() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.DoubleTypeRep);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Prelude.DoubleTypeRep.
		 * @see #DoubleTypeRep()
		 */
		public static final QualifiedName DoubleTypeRep = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"DoubleTypeRep");

		/**
		 * Ordinal of DataConstructor Cal.Core.Prelude.DoubleTypeRep.
		 * @see #DoubleTypeRep()
		 */
		public static final int DoubleTypeRep_ordinal = 12;

		/**
		 * Used for the <code>Cal.Core.Prelude.Char</code> type constructor.
		 * representation of the <code>Cal.Core.Prelude.Char</code> type
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr CharTypeRep() {
			return SourceModel.Expr.DataCons.make(DataConstructors.CharTypeRep);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Prelude.CharTypeRep.
		 * @see #CharTypeRep()
		 */
		public static final QualifiedName CharTypeRep = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "CharTypeRep");

		/**
		 * Ordinal of DataConstructor Cal.Core.Prelude.CharTypeRep.
		 * @see #CharTypeRep()
		 */
		public static final int CharTypeRep_ordinal = 13;

		/**
		 * Used for the <code>Cal.Core.Prelude.String</code> type constructor.
		 * representation of the <code>Cal.Core.Prelude.String</code> type
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr StringTypeRep() {
			return 
				SourceModel.Expr.DataCons.make(DataConstructors.StringTypeRep);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Prelude.StringTypeRep.
		 * @see #StringTypeRep()
		 */
		public static final QualifiedName StringTypeRep = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"StringTypeRep");

		/**
		 * Ordinal of DataConstructor Cal.Core.Prelude.StringTypeRep.
		 * @see #StringTypeRep()
		 */
		public static final int StringTypeRep_ordinal = 14;

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Core.Prelude module.
	 */
	public static final class Functions {
		/**
		 * Helper binding method for function: absByte. 
		 * @param x
		 * @return the SourceModule.expr representing an application of absByte
		 */
		public static final SourceModel.Expr absByte(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.absByte), x});
		}

		/**
		 * @see #absByte(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @return the SourceModel.Expr representing an application of absByte
		 */
		public static final SourceModel.Expr absByte(byte x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.absByte), SourceModel.Expr.makeByteValue(x)});
		}

		/**
		 * Name binding for function: absByte.
		 * @see #absByte(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName absByte = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "absByte");

		/**
		 * Helper binding method for function: absDecimal. 
		 * @param decimal
		 * @return the SourceModule.expr representing an application of absDecimal
		 */
		public static final SourceModel.Expr absDecimal(SourceModel.Expr decimal) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.absDecimal), decimal});
		}

		/**
		 * Name binding for function: absDecimal.
		 * @see #absDecimal(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName absDecimal = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "absDecimal");

		/**
		 * Helper binding method for function: absDouble. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of absDouble
		 */
		public static final SourceModel.Expr absDouble(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.absDouble), arg_1});
		}

		/**
		 * @see #absDouble(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of absDouble
		 */
		public static final SourceModel.Expr absDouble(double arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.absDouble), SourceModel.Expr.makeDoubleValue(arg_1)});
		}

		/**
		 * Name binding for function: absDouble.
		 * @see #absDouble(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName absDouble = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "absDouble");

		/**
		 * Helper binding method for function: absFloat. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of absFloat
		 */
		public static final SourceModel.Expr absFloat(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.absFloat), arg_1});
		}

		/**
		 * @see #absFloat(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of absFloat
		 */
		public static final SourceModel.Expr absFloat(float arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.absFloat), SourceModel.Expr.makeFloatValue(arg_1)});
		}

		/**
		 * Name binding for function: absFloat.
		 * @see #absFloat(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName absFloat = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "absFloat");

		/**
		 * Helper binding method for function: absInt. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of absInt
		 */
		public static final SourceModel.Expr absInt(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.absInt), arg_1});
		}

		/**
		 * @see #absInt(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of absInt
		 */
		public static final SourceModel.Expr absInt(int arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.absInt), SourceModel.Expr.makeIntValue(arg_1)});
		}

		/**
		 * Name binding for function: absInt.
		 * @see #absInt(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName absInt = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "absInt");

		/**
		 * Helper binding method for function: absInteger. 
		 * @param integer
		 * @return the SourceModule.expr representing an application of absInteger
		 */
		public static final SourceModel.Expr absInteger(SourceModel.Expr integer) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.absInteger), integer});
		}

		/**
		 * Name binding for function: absInteger.
		 * @see #absInteger(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName absInteger = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "absInteger");

		/**
		 * Helper binding method for function: absLong. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of absLong
		 */
		public static final SourceModel.Expr absLong(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.absLong), arg_1});
		}

		/**
		 * @see #absLong(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of absLong
		 */
		public static final SourceModel.Expr absLong(long arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.absLong), SourceModel.Expr.makeLongValue(arg_1)});
		}

		/**
		 * Name binding for function: absLong.
		 * @see #absLong(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName absLong = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "absLong");

		/**
		 * Helper binding method for function: absShort. 
		 * @param x
		 * @return the SourceModule.expr representing an application of absShort
		 */
		public static final SourceModel.Expr absShort(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.absShort), x});
		}

		/**
		 * @see #absShort(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @return the SourceModel.Expr representing an application of absShort
		 */
		public static final SourceModel.Expr absShort(short x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.absShort), SourceModel.Expr.makeShortValue(x)});
		}

		/**
		 * Name binding for function: absShort.
		 * @see #absShort(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName absShort = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "absShort");

		/**
		 * Helper binding method for function: addByte. 
		 * @param x
		 * @param y
		 * @return the SourceModule.expr representing an application of addByte
		 */
		public static final SourceModel.Expr addByte(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.addByte), x, y});
		}

		/**
		 * @see #addByte(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @param y
		 * @return the SourceModel.Expr representing an application of addByte
		 */
		public static final SourceModel.Expr addByte(byte x, byte y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.addByte), SourceModel.Expr.makeByteValue(x), SourceModel.Expr.makeByteValue(y)});
		}

		/**
		 * Name binding for function: addByte.
		 * @see #addByte(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName addByte = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "addByte");

		/**
		 * Helper binding method for function: addDecimal. 
		 * @param decimal
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of addDecimal
		 */
		public static final SourceModel.Expr addDecimal(SourceModel.Expr decimal, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.addDecimal), decimal, arg_2});
		}

		/**
		 * Name binding for function: addDecimal.
		 * @see #addDecimal(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName addDecimal = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "addDecimal");

		/**
		 * Helper binding method for function: addDouble. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of addDouble
		 */
		public static final SourceModel.Expr addDouble(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.addDouble), arg_1, arg_2});
		}

		/**
		 * @see #addDouble(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of addDouble
		 */
		public static final SourceModel.Expr addDouble(double arg_1, double arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.addDouble), SourceModel.Expr.makeDoubleValue(arg_1), SourceModel.Expr.makeDoubleValue(arg_2)});
		}

		/**
		 * Name binding for function: addDouble.
		 * @see #addDouble(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName addDouble = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "addDouble");

		/**
		 * Helper binding method for function: addFloat. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of addFloat
		 */
		public static final SourceModel.Expr addFloat(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.addFloat), arg_1, arg_2});
		}

		/**
		 * @see #addFloat(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of addFloat
		 */
		public static final SourceModel.Expr addFloat(float arg_1, float arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.addFloat), SourceModel.Expr.makeFloatValue(arg_1), SourceModel.Expr.makeFloatValue(arg_2)});
		}

		/**
		 * Name binding for function: addFloat.
		 * @see #addFloat(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName addFloat = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "addFloat");

		/**
		 * Helper binding method for function: addInt. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of addInt
		 */
		public static final SourceModel.Expr addInt(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.addInt), arg_1, arg_2});
		}

		/**
		 * @see #addInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of addInt
		 */
		public static final SourceModel.Expr addInt(int arg_1, int arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.addInt), SourceModel.Expr.makeIntValue(arg_1), SourceModel.Expr.makeIntValue(arg_2)});
		}

		/**
		 * Name binding for function: addInt.
		 * @see #addInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName addInt = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "addInt");

		/**
		 * Helper binding method for function: addInteger. 
		 * @param integer
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of addInteger
		 */
		public static final SourceModel.Expr addInteger(SourceModel.Expr integer, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.addInteger), integer, arg_2});
		}

		/**
		 * Name binding for function: addInteger.
		 * @see #addInteger(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName addInteger = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "addInteger");

		/**
		 * Helper binding method for function: addLong. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of addLong
		 */
		public static final SourceModel.Expr addLong(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.addLong), arg_1, arg_2});
		}

		/**
		 * @see #addLong(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of addLong
		 */
		public static final SourceModel.Expr addLong(long arg_1, long arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.addLong), SourceModel.Expr.makeLongValue(arg_1), SourceModel.Expr.makeLongValue(arg_2)});
		}

		/**
		 * Name binding for function: addLong.
		 * @see #addLong(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName addLong = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "addLong");

		/**
		 * Helper binding method for function: addShort. 
		 * @param x
		 * @param y
		 * @return the SourceModule.expr representing an application of addShort
		 */
		public static final SourceModel.Expr addShort(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.addShort), x, y});
		}

		/**
		 * @see #addShort(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @param y
		 * @return the SourceModel.Expr representing an application of addShort
		 */
		public static final SourceModel.Expr addShort(short x, short y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.addShort), SourceModel.Expr.makeShortValue(x), SourceModel.Expr.makeShortValue(y)});
		}

		/**
		 * Name binding for function: addShort.
		 * @see #addShort(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName addShort = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "addShort");

		/**
		 * Helper binding method for function: appendList. 
		 * @param list1
		 * @param list2
		 * @return the SourceModule.expr representing an application of appendList
		 */
		public static final SourceModel.Expr appendList(SourceModel.Expr list1, SourceModel.Expr list2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.appendList), list1, list2});
		}

		/**
		 * Name binding for function: appendList.
		 * @see #appendList(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName appendList = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "appendList");

		/**
		 * Helper binding method for function: appendOrdering. 
		 * @param ord1
		 * @param ord2
		 * @return the SourceModule.expr representing an application of appendOrdering
		 */
		public static final SourceModel.Expr appendOrdering(SourceModel.Expr ord1, SourceModel.Expr ord2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.appendOrdering), ord1, ord2});
		}

		/**
		 * Name binding for function: appendOrdering.
		 * @see #appendOrdering(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName appendOrdering = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"appendOrdering");

		/**
		 * Helper binding method for function: appendString. 
		 * @param string
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of appendString
		 */
		public static final SourceModel.Expr appendString(SourceModel.Expr string, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.appendString), string, arg_2});
		}

		/**
		 * @see #appendString(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param string
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of appendString
		 */
		public static final SourceModel.Expr appendString(java.lang.String string, java.lang.String arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.appendString), SourceModel.Expr.makeStringValue(string), SourceModel.Expr.makeStringValue(arg_2)});
		}

		/**
		 * Name binding for function: appendString.
		 * @see #appendString(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName appendString = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "appendString");

		/**
		 * Helper binding method for function: appendStringExamples. 
		 * @return the SourceModule.expr representing an application of appendStringExamples
		 */
		public static final SourceModel.Expr appendStringExamples() {
			return SourceModel.Expr.Var.make(Functions.appendStringExamples);
		}

		/**
		 * Name binding for function: appendStringExamples.
		 * @see #appendStringExamples()
		 */
		public static final QualifiedName appendStringExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"appendStringExamples");

		/**
		 * Helper binding method for function: byteIntEnumExamples. 
		 * @return the SourceModule.expr representing an application of byteIntEnumExamples
		 */
		public static final SourceModel.Expr byteIntEnumExamples() {
			return SourceModel.Expr.Var.make(Functions.byteIntEnumExamples);
		}

		/**
		 * Name binding for function: byteIntEnumExamples.
		 * @see #byteIntEnumExamples()
		 */
		public static final QualifiedName byteIntEnumExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"byteIntEnumExamples");

		/**
		 * Helper binding method for function: byteToByte. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of byteToByte
		 */
		public static final SourceModel.Expr byteToByte(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteToByte), arg_1});
		}

		/**
		 * @see #byteToByte(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of byteToByte
		 */
		public static final SourceModel.Expr byteToByte(byte arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteToByte), SourceModel.Expr.makeByteValue(arg_1)});
		}

		/**
		 * Name binding for function: byteToByte.
		 * @see #byteToByte(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName byteToByte = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "byteToByte");

		/**
		 * Helper binding method for function: byteToChar. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of byteToChar
		 */
		public static final SourceModel.Expr byteToChar(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteToChar), arg_1});
		}

		/**
		 * @see #byteToChar(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of byteToChar
		 */
		public static final SourceModel.Expr byteToChar(byte arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteToChar), SourceModel.Expr.makeByteValue(arg_1)});
		}

		/**
		 * Name binding for function: byteToChar.
		 * @see #byteToChar(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName byteToChar = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "byteToChar");

		/**
		 * Helper binding method for function: byteToDecimal. 
		 * @param byteValue
		 * @return the SourceModule.expr representing an application of byteToDecimal
		 */
		public static final SourceModel.Expr byteToDecimal(SourceModel.Expr byteValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteToDecimal), byteValue});
		}

		/**
		 * @see #byteToDecimal(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param byteValue
		 * @return the SourceModel.Expr representing an application of byteToDecimal
		 */
		public static final SourceModel.Expr byteToDecimal(byte byteValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteToDecimal), SourceModel.Expr.makeByteValue(byteValue)});
		}

		/**
		 * Name binding for function: byteToDecimal.
		 * @see #byteToDecimal(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName byteToDecimal = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"byteToDecimal");

		/**
		 * Helper binding method for function: byteToDouble. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of byteToDouble
		 */
		public static final SourceModel.Expr byteToDouble(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteToDouble), arg_1});
		}

		/**
		 * @see #byteToDouble(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of byteToDouble
		 */
		public static final SourceModel.Expr byteToDouble(byte arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteToDouble), SourceModel.Expr.makeByteValue(arg_1)});
		}

		/**
		 * Name binding for function: byteToDouble.
		 * @see #byteToDouble(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName byteToDouble = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "byteToDouble");

		/**
		 * Helper binding method for function: byteToFloat. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of byteToFloat
		 */
		public static final SourceModel.Expr byteToFloat(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteToFloat), arg_1});
		}

		/**
		 * @see #byteToFloat(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of byteToFloat
		 */
		public static final SourceModel.Expr byteToFloat(byte arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteToFloat), SourceModel.Expr.makeByteValue(arg_1)});
		}

		/**
		 * Name binding for function: byteToFloat.
		 * @see #byteToFloat(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName byteToFloat = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "byteToFloat");

		/**
		 * Helper binding method for function: byteToInt. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of byteToInt
		 */
		public static final SourceModel.Expr byteToInt(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteToInt), arg_1});
		}

		/**
		 * @see #byteToInt(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of byteToInt
		 */
		public static final SourceModel.Expr byteToInt(byte arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteToInt), SourceModel.Expr.makeByteValue(arg_1)});
		}

		/**
		 * Name binding for function: byteToInt.
		 * @see #byteToInt(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName byteToInt = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "byteToInt");

		/**
		 * Helper binding method for function: byteToInteger. 
		 * @param byteValue
		 * @return the SourceModule.expr representing an application of byteToInteger
		 */
		public static final SourceModel.Expr byteToInteger(SourceModel.Expr byteValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteToInteger), byteValue});
		}

		/**
		 * @see #byteToInteger(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param byteValue
		 * @return the SourceModel.Expr representing an application of byteToInteger
		 */
		public static final SourceModel.Expr byteToInteger(byte byteValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteToInteger), SourceModel.Expr.makeByteValue(byteValue)});
		}

		/**
		 * Name binding for function: byteToInteger.
		 * @see #byteToInteger(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName byteToInteger = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"byteToInteger");

		/**
		 * Helper binding method for function: byteToLong. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of byteToLong
		 */
		public static final SourceModel.Expr byteToLong(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteToLong), arg_1});
		}

		/**
		 * @see #byteToLong(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of byteToLong
		 */
		public static final SourceModel.Expr byteToLong(byte arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteToLong), SourceModel.Expr.makeByteValue(arg_1)});
		}

		/**
		 * Name binding for function: byteToLong.
		 * @see #byteToLong(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName byteToLong = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "byteToLong");

		/**
		 * Helper binding method for function: byteToShort. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of byteToShort
		 */
		public static final SourceModel.Expr byteToShort(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteToShort), arg_1});
		}

		/**
		 * @see #byteToShort(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of byteToShort
		 */
		public static final SourceModel.Expr byteToShort(byte arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.byteToShort), SourceModel.Expr.makeByteValue(arg_1)});
		}

		/**
		 * Name binding for function: byteToShort.
		 * @see #byteToShort(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName byteToShort = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "byteToShort");

		/**
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Prelude.objectToCalValue
		 * </dl>
		 * 
		 * @param calValue (CAL type: <code>Cal.Core.Prelude.CalValue</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.JObject</code>) 
		 *          converts the <code>calValue</code> argument into a Java object that is suitable for external Java clients to use as a handle to the
		 * <code>calValue</code>. The <code>calValue</code> argument itself is not evaluated to weak-head normal form.
		 */
		public static final SourceModel.Expr calValueToObject(SourceModel.Expr calValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.calValueToObject), calValue});
		}

		/**
		 * Name binding for function: calValueToObject.
		 * @see #calValueToObject(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName calValueToObject = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"calValueToObject");

		/**
		 * Helper binding method for function: castExamples. 
		 * @return the SourceModule.expr representing an application of castExamples
		 */
		public static final SourceModel.Expr castExamples() {
			return SourceModel.Expr.Var.make(Functions.castExamples);
		}

		/**
		 * Name binding for function: castExamples.
		 * @see #castExamples()
		 */
		public static final QualifiedName castExamples = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "castExamples");

		/**
		 * Helper binding method for function: charIntEnumExamples. 
		 * @return the SourceModule.expr representing an application of charIntEnumExamples
		 */
		public static final SourceModel.Expr charIntEnumExamples() {
			return SourceModel.Expr.Var.make(Functions.charIntEnumExamples);
		}

		/**
		 * Name binding for function: charIntEnumExamples.
		 * @see #charIntEnumExamples()
		 */
		public static final QualifiedName charIntEnumExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"charIntEnumExamples");

		/**
		 * Helper binding method for function: charToByte. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of charToByte
		 */
		public static final SourceModel.Expr charToByte(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charToByte), arg_1});
		}

		/**
		 * @see #charToByte(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of charToByte
		 */
		public static final SourceModel.Expr charToByte(char arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charToByte), SourceModel.Expr.makeCharValue(arg_1)});
		}

		/**
		 * Name binding for function: charToByte.
		 * @see #charToByte(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName charToByte = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "charToByte");

		/**
		 * Helper binding method for function: charToChar. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of charToChar
		 */
		public static final SourceModel.Expr charToChar(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charToChar), arg_1});
		}

		/**
		 * @see #charToChar(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of charToChar
		 */
		public static final SourceModel.Expr charToChar(char arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charToChar), SourceModel.Expr.makeCharValue(arg_1)});
		}

		/**
		 * Name binding for function: charToChar.
		 * @see #charToChar(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName charToChar = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "charToChar");

		/**
		 * Helper binding method for function: charToDouble. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of charToDouble
		 */
		public static final SourceModel.Expr charToDouble(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charToDouble), arg_1});
		}

		/**
		 * @see #charToDouble(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of charToDouble
		 */
		public static final SourceModel.Expr charToDouble(char arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charToDouble), SourceModel.Expr.makeCharValue(arg_1)});
		}

		/**
		 * Name binding for function: charToDouble.
		 * @see #charToDouble(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName charToDouble = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "charToDouble");

		/**
		 * Helper binding method for function: charToFloat. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of charToFloat
		 */
		public static final SourceModel.Expr charToFloat(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charToFloat), arg_1});
		}

		/**
		 * @see #charToFloat(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of charToFloat
		 */
		public static final SourceModel.Expr charToFloat(char arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charToFloat), SourceModel.Expr.makeCharValue(arg_1)});
		}

		/**
		 * Name binding for function: charToFloat.
		 * @see #charToFloat(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName charToFloat = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "charToFloat");

		/**
		 * Helper binding method for function: charToInt. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of charToInt
		 */
		public static final SourceModel.Expr charToInt(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charToInt), arg_1});
		}

		/**
		 * @see #charToInt(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of charToInt
		 */
		public static final SourceModel.Expr charToInt(char arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charToInt), SourceModel.Expr.makeCharValue(arg_1)});
		}

		/**
		 * Name binding for function: charToInt.
		 * @see #charToInt(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName charToInt = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "charToInt");

		/**
		 * Helper binding method for function: charToLong. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of charToLong
		 */
		public static final SourceModel.Expr charToLong(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charToLong), arg_1});
		}

		/**
		 * @see #charToLong(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of charToLong
		 */
		public static final SourceModel.Expr charToLong(char arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charToLong), SourceModel.Expr.makeCharValue(arg_1)});
		}

		/**
		 * Name binding for function: charToLong.
		 * @see #charToLong(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName charToLong = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "charToLong");

		/**
		 * Helper binding method for function: charToShort. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of charToShort
		 */
		public static final SourceModel.Expr charToShort(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charToShort), arg_1});
		}

		/**
		 * @see #charToShort(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of charToShort
		 */
		public static final SourceModel.Expr charToShort(char arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charToShort), SourceModel.Expr.makeCharValue(arg_1)});
		}

		/**
		 * Name binding for function: charToShort.
		 * @see #charToShort(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName charToShort = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "charToShort");

		/**
		 * Converts the list of characters to a string.
		 * <p>
		 * This function is defined in the <code>Cal.Core.Prelude</code> module for implementation purposes only. 
		 * The public api version is <code>Cal.Core.String.fromList</code>.
		 * 
		 * @param listOfChars (CAL type: <code>[Cal.Core.Prelude.Char]</code>)
		 *          the list of characters.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          a string constructed from the list of characters.
		 */
		public static final SourceModel.Expr charactersToString(SourceModel.Expr listOfChars) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.charactersToString), listOfChars});
		}

		/**
		 * Name binding for function: charactersToString.
		 * @see #charactersToString(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName charactersToString = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"charactersToString");

		/**
		 * Helper binding method for function: class_String. 
		 * @return the SourceModule.expr representing an application of class_String
		 */
		public static final SourceModel.Expr class_String() {
			return SourceModel.Expr.Var.make(Functions.class_String);
		}

		/**
		 * Name binding for function: class_String.
		 * @see #class_String()
		 */
		public static final QualifiedName class_String = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "class_String");

		/**
		 * Helper binding method for function: class_byte. 
		 * @return the SourceModule.expr representing an application of class_byte
		 */
		public static final SourceModel.Expr class_byte() {
			return SourceModel.Expr.Var.make(Functions.class_byte);
		}

		/**
		 * Name binding for function: class_byte.
		 * @see #class_byte()
		 */
		public static final QualifiedName class_byte = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "class_byte");

		/**
		 * Helper binding method for function: class_char. 
		 * @return the SourceModule.expr representing an application of class_char
		 */
		public static final SourceModel.Expr class_char() {
			return SourceModel.Expr.Var.make(Functions.class_char);
		}

		/**
		 * Name binding for function: class_char.
		 * @see #class_char()
		 */
		public static final QualifiedName class_char = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "class_char");

		/**
		 * Helper binding method for function: class_double. 
		 * @return the SourceModule.expr representing an application of class_double
		 */
		public static final SourceModel.Expr class_double() {
			return SourceModel.Expr.Var.make(Functions.class_double);
		}

		/**
		 * Name binding for function: class_double.
		 * @see #class_double()
		 */
		public static final QualifiedName class_double = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "class_double");

		/**
		 * Helper binding method for function: class_float. 
		 * @return the SourceModule.expr representing an application of class_float
		 */
		public static final SourceModel.Expr class_float() {
			return SourceModel.Expr.Var.make(Functions.class_float);
		}

		/**
		 * Name binding for function: class_float.
		 * @see #class_float()
		 */
		public static final QualifiedName class_float = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "class_float");

		/**
		 * Helper binding method for function: class_int. 
		 * @return the SourceModule.expr representing an application of class_int
		 */
		public static final SourceModel.Expr class_int() {
			return SourceModel.Expr.Var.make(Functions.class_int);
		}

		/**
		 * Name binding for function: class_int.
		 * @see #class_int()
		 */
		public static final QualifiedName class_int = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "class_int");

		/**
		 * Helper binding method for function: class_isPrimitive. 
		 * @param jClass
		 * @return the SourceModule.expr representing an application of class_isPrimitive
		 */
		public static final SourceModel.Expr class_isPrimitive(SourceModel.Expr jClass) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.class_isPrimitive), jClass});
		}

		/**
		 * Name binding for function: class_isPrimitive.
		 * @see #class_isPrimitive(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName class_isPrimitive = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"class_isPrimitive");

		/**
		 * Helper binding method for function: class_long. 
		 * @return the SourceModule.expr representing an application of class_long
		 */
		public static final SourceModel.Expr class_long() {
			return SourceModel.Expr.Var.make(Functions.class_long);
		}

		/**
		 * Name binding for function: class_long.
		 * @see #class_long()
		 */
		public static final QualifiedName class_long = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "class_long");

		/**
		 * Helper binding method for function: class_short. 
		 * @return the SourceModule.expr representing an application of class_short
		 */
		public static final SourceModel.Expr class_short() {
			return SourceModel.Expr.Var.make(Functions.class_short);
		}

		/**
		 * Name binding for function: class_short.
		 * @see #class_short()
		 */
		public static final QualifiedName class_short = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "class_short");

		/**
		 * Helper binding method for function: class_toString. 
		 * @param jClass
		 * @return the SourceModule.expr representing an application of class_toString
		 */
		public static final SourceModel.Expr class_toString(SourceModel.Expr jClass) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.class_toString), jClass});
		}

		/**
		 * Name binding for function: class_toString.
		 * @see #class_toString(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName class_toString = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"class_toString");

		/**
		 * Helper binding method for function: collection_iterator. 
		 * @param jCollection
		 * @return the SourceModule.expr representing an application of collection_iterator
		 */
		public static final SourceModel.Expr collection_iterator(SourceModel.Expr jCollection) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.collection_iterator), jCollection});
		}

		/**
		 * Name binding for function: collection_iterator.
		 * @see #collection_iterator(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName collection_iterator = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"collection_iterator");

		/**
		 * Helper binding method for function: compareByte. 
		 * @param x
		 * @param y
		 * @return the SourceModule.expr representing an application of compareByte
		 */
		public static final SourceModel.Expr compareByte(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.compareByte), x, y});
		}

		/**
		 * @see #compareByte(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @param y
		 * @return the SourceModel.Expr representing an application of compareByte
		 */
		public static final SourceModel.Expr compareByte(byte x, byte y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.compareByte), SourceModel.Expr.makeByteValue(x), SourceModel.Expr.makeByteValue(y)});
		}

		/**
		 * Name binding for function: compareByte.
		 * @see #compareByte(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName compareByte = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "compareByte");

		/**
		 * Helper binding method for function: compareChar. 
		 * @param x
		 * @param y
		 * @return the SourceModule.expr representing an application of compareChar
		 */
		public static final SourceModel.Expr compareChar(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.compareChar), x, y});
		}

		/**
		 * @see #compareChar(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @param y
		 * @return the SourceModel.Expr representing an application of compareChar
		 */
		public static final SourceModel.Expr compareChar(char x, char y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.compareChar), SourceModel.Expr.makeCharValue(x), SourceModel.Expr.makeCharValue(y)});
		}

		/**
		 * Name binding for function: compareChar.
		 * @see #compareChar(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName compareChar = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "compareChar");

		/**
		 * Helper binding method for function: compareComparable. 
		 * @param x
		 * @param y
		 * @return the SourceModule.expr representing an application of compareComparable
		 */
		public static final SourceModel.Expr compareComparable(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.compareComparable), x, y});
		}

		/**
		 * Name binding for function: compareComparable.
		 * @see #compareComparable(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName compareComparable = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"compareComparable");

		/**
		 * Helper binding method for function: compareDecimal. 
		 * @param decimalValue1
		 * @param decimalValue2
		 * @return the SourceModule.expr representing an application of compareDecimal
		 */
		public static final SourceModel.Expr compareDecimal(SourceModel.Expr decimalValue1, SourceModel.Expr decimalValue2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.compareDecimal), decimalValue1, decimalValue2});
		}

		/**
		 * Name binding for function: compareDecimal.
		 * @see #compareDecimal(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName compareDecimal = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"compareDecimal");

		/**
		 * Helper binding method for function: compareDouble. 
		 * @param x
		 * @param y
		 * @return the SourceModule.expr representing an application of compareDouble
		 */
		public static final SourceModel.Expr compareDouble(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.compareDouble), x, y});
		}

		/**
		 * @see #compareDouble(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @param y
		 * @return the SourceModel.Expr representing an application of compareDouble
		 */
		public static final SourceModel.Expr compareDouble(double x, double y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.compareDouble), SourceModel.Expr.makeDoubleValue(x), SourceModel.Expr.makeDoubleValue(y)});
		}

		/**
		 * Name binding for function: compareDouble.
		 * @see #compareDouble(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName compareDouble = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"compareDouble");

		/**
		 * Helper binding method for function: compareFloat. 
		 * @param x
		 * @param y
		 * @return the SourceModule.expr representing an application of compareFloat
		 */
		public static final SourceModel.Expr compareFloat(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.compareFloat), x, y});
		}

		/**
		 * @see #compareFloat(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @param y
		 * @return the SourceModel.Expr representing an application of compareFloat
		 */
		public static final SourceModel.Expr compareFloat(float x, float y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.compareFloat), SourceModel.Expr.makeFloatValue(x), SourceModel.Expr.makeFloatValue(y)});
		}

		/**
		 * Name binding for function: compareFloat.
		 * @see #compareFloat(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName compareFloat = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "compareFloat");

		/**
		 * Helper binding method for function: compareInt. 
		 * @param x
		 * @param y
		 * @return the SourceModule.expr representing an application of compareInt
		 */
		public static final SourceModel.Expr compareInt(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.compareInt), x, y});
		}

		/**
		 * @see #compareInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @param y
		 * @return the SourceModel.Expr representing an application of compareInt
		 */
		public static final SourceModel.Expr compareInt(int x, int y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.compareInt), SourceModel.Expr.makeIntValue(x), SourceModel.Expr.makeIntValue(y)});
		}

		/**
		 * Name binding for function: compareInt.
		 * @see #compareInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName compareInt = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "compareInt");

		/**
		 * Helper binding method for function: compareInteger. 
		 * @param x
		 * @param y
		 * @return the SourceModule.expr representing an application of compareInteger
		 */
		public static final SourceModel.Expr compareInteger(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.compareInteger), x, y});
		}

		/**
		 * Name binding for function: compareInteger.
		 * @see #compareInteger(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName compareInteger = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"compareInteger");

		/**
		 * Helper binding method for function: compareLong. 
		 * @param x
		 * @param y
		 * @return the SourceModule.expr representing an application of compareLong
		 */
		public static final SourceModel.Expr compareLong(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.compareLong), x, y});
		}

		/**
		 * @see #compareLong(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @param y
		 * @return the SourceModel.Expr representing an application of compareLong
		 */
		public static final SourceModel.Expr compareLong(long x, long y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.compareLong), SourceModel.Expr.makeLongValue(x), SourceModel.Expr.makeLongValue(y)});
		}

		/**
		 * Name binding for function: compareLong.
		 * @see #compareLong(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName compareLong = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "compareLong");

		/**
		 * 
		 * @param r1 (CAL type: <code>Cal.Core.Prelude.Ord r => {r}</code>)
		 * @param r2 (CAL type: <code>Cal.Core.Prelude.Ord r => {r}</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Ordering</code>) 
		 *          comparison using field-name ordering.
		 */
		public static final SourceModel.Expr compareRecord(SourceModel.Expr r1, SourceModel.Expr r2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.compareRecord), r1, r2});
		}

		/**
		 * Name binding for function: compareRecord.
		 * @see #compareRecord(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName compareRecord = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"compareRecord");

		/**
		 * Helper binding method for function: compareShort. 
		 * @param x
		 * @param y
		 * @return the SourceModule.expr representing an application of compareShort
		 */
		public static final SourceModel.Expr compareShort(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.compareShort), x, y});
		}

		/**
		 * @see #compareShort(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @param y
		 * @return the SourceModel.Expr representing an application of compareShort
		 */
		public static final SourceModel.Expr compareShort(short x, short y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.compareShort), SourceModel.Expr.makeShortValue(x), SourceModel.Expr.makeShortValue(y)});
		}

		/**
		 * Name binding for function: compareShort.
		 * @see #compareShort(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName compareShort = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "compareShort");

		/**
		 * Example usage of the compose function, in its different forms (regular
		 * function application, backquoted operator, and the <code>#</code> operator).
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr composeExamples() {
			return SourceModel.Expr.Var.make(Functions.composeExamples);
		}

		/**
		 * Name binding for function: composeExamples.
		 * @see #composeExamples()
		 */
		public static final QualifiedName composeExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"composeExamples");

		/**
		 * Helper binding method for function: concatDefault. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of concatDefault
		 */
		public static final SourceModel.Expr concatDefault(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.concatDefault), arg_1});
		}

		/**
		 * Name binding for function: concatDefault.
		 * @see #concatDefault(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName concatDefault = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"concatDefault");

		/**
		 * Helper binding method for function: concatList. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of concatList
		 */
		public static final SourceModel.Expr concatList(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.concatList), arg_1});
		}

		/**
		 * Name binding for function: concatList.
		 * @see #concatList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName concatList = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "concatList");

		/**
		 * <code>concatMap mapFunction list</code> applies <code>mapFunction</code> to each element of list and then concatenates the resulting
		 * list. The result type of the <code>mapFunction</code> (<code>b</code> in the type declaration) is <code>Cal.Core.Prelude.Appendable</code> to allow for
		 * the concatenation.
		 * <p>
		 * This function is defined in the <code>Cal.Core.Prelude</code> module for implementation purposes only. 
		 * The public api version is <code>Cal.Collections.List.concatMap</code>.
		 * 
		 * @param mapFunction (CAL type: <code>Cal.Core.Prelude.Appendable b => a -> b</code>)
		 *          a function to be applied to the elements in the list.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list whose elements are to be mapped.
		 * @return (CAL type: <code>Cal.Core.Prelude.Appendable b => b</code>) 
		 *          the concatenation of the values obtained from mapping <code>mapFunction</code> to the elements in the list.
		 */
		public static final SourceModel.Expr concatMap(SourceModel.Expr mapFunction, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.concatMap), mapFunction, list});
		}

		/**
		 * Name binding for function: concatMap.
		 * @see #concatMap(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName concatMap = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "concatMap");

		/**
		 * Helper binding method for function: concatString. 
		 * @param listOfStrings
		 * @return the SourceModule.expr representing an application of concatString
		 */
		public static final SourceModel.Expr concatString(SourceModel.Expr listOfStrings) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.concatString), listOfStrings});
		}

		/**
		 * Name binding for function: concatString.
		 * @see #concatString(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName concatString = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "concatString");

		/**
		 * Helper binding method for function: decimalToByte. 
		 * @param decimal
		 * @return the SourceModule.expr representing an application of decimalToByte
		 */
		public static final SourceModel.Expr decimalToByte(SourceModel.Expr decimal) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.decimalToByte), decimal});
		}

		/**
		 * Name binding for function: decimalToByte.
		 * @see #decimalToByte(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName decimalToByte = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"decimalToByte");

		/**
		 * Helper binding method for function: decimalToDecimal. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of decimalToDecimal
		 */
		public static final SourceModel.Expr decimalToDecimal(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.decimalToDecimal), arg_1});
		}

		/**
		 * Name binding for function: decimalToDecimal.
		 * @see #decimalToDecimal(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName decimalToDecimal = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"decimalToDecimal");

		/**
		 * Attempts to convert a <code>Cal.Core.Prelude.Decimal</code> value to the nearest <code>Cal.Core.Prelude.Double</code> value.
		 * If the magnitude of the <code>Cal.Core.Prelude.Decimal</code> value is too large to represent
		 * as a <code>Cal.Core.Prelude.Double</code>, then the result will be either <code>Cal.Core.Prelude.positiveInfinity</code>
		 * (for positive values) or <code>Cal.Core.Prelude.negativeInfinity</code> (for negative values).
		 * Even when the return value is finite, there may be some loss of
		 * precision.
		 * @param decimalValue (CAL type: <code>Cal.Core.Prelude.Decimal</code>)
		 *          <code>Cal.Core.Prelude.Decimal</code> value to convert
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 *          the closest equivalent <code>Cal.Core.Prelude.Double</code> value to <code>decimalValue</code>, or 
		 * <code>Cal.Core.Prelude.positiveInfinity</code> or <code>Cal.Core.Prelude.negativeInfinity</code> (depending on 
		 * <code>decimalValue</code>'s sign) if the magnitude of <code>decimalValue</code> is
		 * too large to fit into a <code>Cal.Core.Prelude.Double</code>.
		 */
		public static final SourceModel.Expr decimalToDouble(SourceModel.Expr decimalValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.decimalToDouble), decimalValue});
		}

		/**
		 * Name binding for function: decimalToDouble.
		 * @see #decimalToDouble(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName decimalToDouble = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"decimalToDouble");

		/**
		 * Helper binding method for function: decimalToFloat. 
		 * @param decimal
		 * @return the SourceModule.expr representing an application of decimalToFloat
		 */
		public static final SourceModel.Expr decimalToFloat(SourceModel.Expr decimal) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.decimalToFloat), decimal});
		}

		/**
		 * Name binding for function: decimalToFloat.
		 * @see #decimalToFloat(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName decimalToFloat = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"decimalToFloat");

		/**
		 * Helper binding method for function: decimalToInt. 
		 * @param decimal
		 * @return the SourceModule.expr representing an application of decimalToInt
		 */
		public static final SourceModel.Expr decimalToInt(SourceModel.Expr decimal) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.decimalToInt), decimal});
		}

		/**
		 * Name binding for function: decimalToInt.
		 * @see #decimalToInt(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName decimalToInt = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "decimalToInt");

		/**
		 * Helper binding method for function: decimalToInteger. 
		 * @param decimal
		 * @return the SourceModule.expr representing an application of decimalToInteger
		 */
		public static final SourceModel.Expr decimalToInteger(SourceModel.Expr decimal) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.decimalToInteger), decimal});
		}

		/**
		 * Name binding for function: decimalToInteger.
		 * @see #decimalToInteger(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName decimalToInteger = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"decimalToInteger");

		/**
		 * Helper binding method for function: decimalToLong. 
		 * @param decimal
		 * @return the SourceModule.expr representing an application of decimalToLong
		 */
		public static final SourceModel.Expr decimalToLong(SourceModel.Expr decimal) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.decimalToLong), decimal});
		}

		/**
		 * Name binding for function: decimalToLong.
		 * @see #decimalToLong(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName decimalToLong = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"decimalToLong");

		/**
		 * Helper binding method for function: decimalToShort. 
		 * @param decimal
		 * @return the SourceModule.expr representing an application of decimalToShort
		 */
		public static final SourceModel.Expr decimalToShort(SourceModel.Expr decimal) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.decimalToShort), decimal});
		}

		/**
		 * Name binding for function: decimalToShort.
		 * @see #decimalToShort(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName decimalToShort = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"decimalToShort");

		/**
		 * Helper binding method for function: divideByte. 
		 * @param x
		 * @param y
		 * @return the SourceModule.expr representing an application of divideByte
		 */
		public static final SourceModel.Expr divideByte(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.divideByte), x, y});
		}

		/**
		 * @see #divideByte(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @param y
		 * @return the SourceModel.Expr representing an application of divideByte
		 */
		public static final SourceModel.Expr divideByte(byte x, byte y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.divideByte), SourceModel.Expr.makeByteValue(x), SourceModel.Expr.makeByteValue(y)});
		}

		/**
		 * Name binding for function: divideByte.
		 * @see #divideByte(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName divideByte = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "divideByte");

		/**
		 * Helper binding method for function: divideDecimal. 
		 * @param numerator
		 * @param denominator
		 * @return the SourceModule.expr representing an application of divideDecimal
		 */
		public static final SourceModel.Expr divideDecimal(SourceModel.Expr numerator, SourceModel.Expr denominator) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.divideDecimal), numerator, denominator});
		}

		/**
		 * Name binding for function: divideDecimal.
		 * @see #divideDecimal(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName divideDecimal = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"divideDecimal");

		/**
		 * Helper binding method for function: divideDouble. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of divideDouble
		 */
		public static final SourceModel.Expr divideDouble(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.divideDouble), arg_1, arg_2});
		}

		/**
		 * @see #divideDouble(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of divideDouble
		 */
		public static final SourceModel.Expr divideDouble(double arg_1, double arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.divideDouble), SourceModel.Expr.makeDoubleValue(arg_1), SourceModel.Expr.makeDoubleValue(arg_2)});
		}

		/**
		 * Name binding for function: divideDouble.
		 * @see #divideDouble(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName divideDouble = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "divideDouble");

		/**
		 * Helper binding method for function: divideFloat. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of divideFloat
		 */
		public static final SourceModel.Expr divideFloat(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.divideFloat), arg_1, arg_2});
		}

		/**
		 * @see #divideFloat(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of divideFloat
		 */
		public static final SourceModel.Expr divideFloat(float arg_1, float arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.divideFloat), SourceModel.Expr.makeFloatValue(arg_1), SourceModel.Expr.makeFloatValue(arg_2)});
		}

		/**
		 * Name binding for function: divideFloat.
		 * @see #divideFloat(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName divideFloat = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "divideFloat");

		/**
		 * Helper binding method for function: divideInt. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of divideInt
		 */
		public static final SourceModel.Expr divideInt(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.divideInt), arg_1, arg_2});
		}

		/**
		 * @see #divideInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of divideInt
		 */
		public static final SourceModel.Expr divideInt(int arg_1, int arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.divideInt), SourceModel.Expr.makeIntValue(arg_1), SourceModel.Expr.makeIntValue(arg_2)});
		}

		/**
		 * Name binding for function: divideInt.
		 * @see #divideInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName divideInt = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "divideInt");

		/**
		 * Helper binding method for function: divideInteger. 
		 * @param integer
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of divideInteger
		 */
		public static final SourceModel.Expr divideInteger(SourceModel.Expr integer, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.divideInteger), integer, arg_2});
		}

		/**
		 * Name binding for function: divideInteger.
		 * @see #divideInteger(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName divideInteger = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"divideInteger");

		/**
		 * Helper binding method for function: divideLong. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of divideLong
		 */
		public static final SourceModel.Expr divideLong(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.divideLong), arg_1, arg_2});
		}

		/**
		 * @see #divideLong(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of divideLong
		 */
		public static final SourceModel.Expr divideLong(long arg_1, long arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.divideLong), SourceModel.Expr.makeLongValue(arg_1), SourceModel.Expr.makeLongValue(arg_2)});
		}

		/**
		 * Name binding for function: divideLong.
		 * @see #divideLong(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName divideLong = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "divideLong");

		/**
		 * Helper binding method for function: divideShort. 
		 * @param x
		 * @param y
		 * @return the SourceModule.expr representing an application of divideShort
		 */
		public static final SourceModel.Expr divideShort(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.divideShort), x, y});
		}

		/**
		 * @see #divideShort(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @param y
		 * @return the SourceModel.Expr representing an application of divideShort
		 */
		public static final SourceModel.Expr divideShort(short x, short y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.divideShort), SourceModel.Expr.makeShortValue(x), SourceModel.Expr.makeShortValue(y)});
		}

		/**
		 * Name binding for function: divideShort.
		 * @see #divideShort(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName divideShort = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "divideShort");

		/**
		 * Helper binding method for function: doubleToByte. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of doubleToByte
		 */
		public static final SourceModel.Expr doubleToByte(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleToByte), arg_1});
		}

		/**
		 * @see #doubleToByte(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of doubleToByte
		 */
		public static final SourceModel.Expr doubleToByte(double arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleToByte), SourceModel.Expr.makeDoubleValue(arg_1)});
		}

		/**
		 * Name binding for function: doubleToByte.
		 * @see #doubleToByte(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleToByte = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "doubleToByte");

		/**
		 * Helper binding method for function: doubleToChar. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of doubleToChar
		 */
		public static final SourceModel.Expr doubleToChar(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleToChar), arg_1});
		}

		/**
		 * @see #doubleToChar(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of doubleToChar
		 */
		public static final SourceModel.Expr doubleToChar(double arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleToChar), SourceModel.Expr.makeDoubleValue(arg_1)});
		}

		/**
		 * Name binding for function: doubleToChar.
		 * @see #doubleToChar(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleToChar = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "doubleToChar");

		/**
		 * Converts a <code>Cal.Core.Prelude.Double</code> value to the equivalent <code>Cal.Core.Prelude.Decimal</code> value.
		 * An error is signalled if <code>doubleValue</code> is <code>Cal.Core.Prelude.notANumber</code>, <code>Cal.Core.Prelude.positiveInfinity</code>,
		 * or <code>Cal.Core.Prelude.negativeInfinity</code>.
		 * @param doubleValue (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 *          <code>Cal.Core.Prelude.Double</code> value to convert to a <code>Cal.Core.Prelude.Decimal</code>.  This should
		 * not be <code>Cal.Core.Prelude.notANumber</code>, <code>Cal.Core.Prelude.positiveInfinity</code>, or <code>Cal.Core.Prelude.negativeInfinity</code>.
		 * @return (CAL type: <code>Cal.Core.Prelude.Decimal</code>) 
		 *          the equivalent <code>Cal.Core.Prelude.Decimal</code> value to <code>doubleToDouble</code>
		 */
		public static final SourceModel.Expr doubleToDecimal(SourceModel.Expr doubleValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleToDecimal), doubleValue});
		}

		/**
		 * @see #doubleToDecimal(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param doubleValue
		 * @return the SourceModel.Expr representing an application of doubleToDecimal
		 */
		public static final SourceModel.Expr doubleToDecimal(double doubleValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleToDecimal), SourceModel.Expr.makeDoubleValue(doubleValue)});
		}

		/**
		 * Name binding for function: doubleToDecimal.
		 * @see #doubleToDecimal(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleToDecimal = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"doubleToDecimal");

		/**
		 * Helper binding method for function: doubleToDouble. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of doubleToDouble
		 */
		public static final SourceModel.Expr doubleToDouble(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleToDouble), arg_1});
		}

		/**
		 * @see #doubleToDouble(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of doubleToDouble
		 */
		public static final SourceModel.Expr doubleToDouble(double arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleToDouble), SourceModel.Expr.makeDoubleValue(arg_1)});
		}

		/**
		 * Name binding for function: doubleToDouble.
		 * @see #doubleToDouble(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleToDouble = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"doubleToDouble");

		/**
		 * Helper binding method for function: doubleToFloat. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of doubleToFloat
		 */
		public static final SourceModel.Expr doubleToFloat(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleToFloat), arg_1});
		}

		/**
		 * @see #doubleToFloat(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of doubleToFloat
		 */
		public static final SourceModel.Expr doubleToFloat(double arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleToFloat), SourceModel.Expr.makeDoubleValue(arg_1)});
		}

		/**
		 * Name binding for function: doubleToFloat.
		 * @see #doubleToFloat(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleToFloat = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"doubleToFloat");

		/**
		 * Helper binding method for function: doubleToInt. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of doubleToInt
		 */
		public static final SourceModel.Expr doubleToInt(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleToInt), arg_1});
		}

		/**
		 * @see #doubleToInt(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of doubleToInt
		 */
		public static final SourceModel.Expr doubleToInt(double arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleToInt), SourceModel.Expr.makeDoubleValue(arg_1)});
		}

		/**
		 * Name binding for function: doubleToInt.
		 * @see #doubleToInt(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleToInt = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "doubleToInt");

		/**
		 * Helper binding method for function: doubleToInteger. 
		 * @param doubleValue
		 * @return the SourceModule.expr representing an application of doubleToInteger
		 */
		public static final SourceModel.Expr doubleToInteger(SourceModel.Expr doubleValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleToInteger), doubleValue});
		}

		/**
		 * @see #doubleToInteger(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param doubleValue
		 * @return the SourceModel.Expr representing an application of doubleToInteger
		 */
		public static final SourceModel.Expr doubleToInteger(double doubleValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleToInteger), SourceModel.Expr.makeDoubleValue(doubleValue)});
		}

		/**
		 * Name binding for function: doubleToInteger.
		 * @see #doubleToInteger(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleToInteger = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"doubleToInteger");

		/**
		 * Helper binding method for function: doubleToLong. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of doubleToLong
		 */
		public static final SourceModel.Expr doubleToLong(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleToLong), arg_1});
		}

		/**
		 * @see #doubleToLong(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of doubleToLong
		 */
		public static final SourceModel.Expr doubleToLong(double arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleToLong), SourceModel.Expr.makeDoubleValue(arg_1)});
		}

		/**
		 * Name binding for function: doubleToLong.
		 * @see #doubleToLong(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleToLong = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "doubleToLong");

		/**
		 * Helper binding method for function: doubleToShort. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of doubleToShort
		 */
		public static final SourceModel.Expr doubleToShort(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleToShort), arg_1});
		}

		/**
		 * @see #doubleToShort(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of doubleToShort
		 */
		public static final SourceModel.Expr doubleToShort(double arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleToShort), SourceModel.Expr.makeDoubleValue(arg_1)});
		}

		/**
		 * Name binding for function: doubleToShort.
		 * @see #doubleToShort(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleToShort = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"doubleToShort");

		/**
		 * Helper binding method for function: eitherValue_getValueField. 
		 * @param jEitherValue
		 * @return the SourceModule.expr representing an application of eitherValue_getValueField
		 */
		public static final SourceModel.Expr eitherValue_getValueField(SourceModel.Expr jEitherValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.eitherValue_getValueField), jEitherValue});
		}

		/**
		 * Name binding for function: eitherValue_getValueField.
		 * @see #eitherValue_getValueField(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName eitherValue_getValueField = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"eitherValue_getValueField");

		/**
		 * Helper binding method for function: eitherValue_isLeft. 
		 * @param jEitherValue
		 * @return the SourceModule.expr representing an application of eitherValue_isLeft
		 */
		public static final SourceModel.Expr eitherValue_isLeft(SourceModel.Expr jEitherValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.eitherValue_isLeft), jEitherValue});
		}

		/**
		 * Name binding for function: eitherValue_isLeft.
		 * @see #eitherValue_isLeft(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName eitherValue_isLeft = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"eitherValue_isLeft");

		/**
		 * Helper binding method for function: eitherValue_makeLeft. 
		 * @param value
		 * @return the SourceModule.expr representing an application of eitherValue_makeLeft
		 */
		public static final SourceModel.Expr eitherValue_makeLeft(SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.eitherValue_makeLeft), value});
		}

		/**
		 * Name binding for function: eitherValue_makeLeft.
		 * @see #eitherValue_makeLeft(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName eitherValue_makeLeft = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"eitherValue_makeLeft");

		/**
		 * Helper binding method for function: eitherValue_makeRight. 
		 * @param value
		 * @return the SourceModule.expr representing an application of eitherValue_makeRight
		 */
		public static final SourceModel.Expr eitherValue_makeRight(SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.eitherValue_makeRight), value});
		}

		/**
		 * Name binding for function: eitherValue_makeRight.
		 * @see #eitherValue_makeRight(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName eitherValue_makeRight = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"eitherValue_makeRight");

		/**
		 * Helper binding method for function: emptyList. 
		 * @return the SourceModule.expr representing an application of emptyList
		 */
		public static final SourceModel.Expr emptyList() {
			return SourceModel.Expr.Var.make(Functions.emptyList);
		}

		/**
		 * Name binding for function: emptyList.
		 * @see #emptyList()
		 */
		public static final QualifiedName emptyList = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "emptyList");

		/**
		 * Helper binding method for function: emptyOrdering. 
		 * @return the SourceModule.expr representing an application of emptyOrdering
		 */
		public static final SourceModel.Expr emptyOrdering() {
			return SourceModel.Expr.Var.make(Functions.emptyOrdering);
		}

		/**
		 * Name binding for function: emptyOrdering.
		 * @see #emptyOrdering()
		 */
		public static final QualifiedName emptyOrdering = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"emptyOrdering");

		/**
		 * Helper binding method for function: emptyString. 
		 * @return the SourceModule.expr representing an application of emptyString
		 */
		public static final SourceModel.Expr emptyString() {
			return SourceModel.Expr.Var.make(Functions.emptyString);
		}

		/**
		 * Name binding for function: emptyString.
		 * @see #emptyString()
		 */
		public static final QualifiedName emptyString = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "emptyString");

		/**
		 * Helper binding method for function: equalsByte. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of equalsByte
		 */
		public static final SourceModel.Expr equalsByte(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.equalsByte), arg_1, arg_2});
		}

		/**
		 * @see #equalsByte(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of equalsByte
		 */
		public static final SourceModel.Expr equalsByte(byte arg_1, byte arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.equalsByte), SourceModel.Expr.makeByteValue(arg_1), SourceModel.Expr.makeByteValue(arg_2)});
		}

		/**
		 * Name binding for function: equalsByte.
		 * @see #equalsByte(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName equalsByte = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "equalsByte");

		/**
		 * Helper binding method for function: equalsChar. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of equalsChar
		 */
		public static final SourceModel.Expr equalsChar(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.equalsChar), arg_1, arg_2});
		}

		/**
		 * @see #equalsChar(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of equalsChar
		 */
		public static final SourceModel.Expr equalsChar(char arg_1, char arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.equalsChar), SourceModel.Expr.makeCharValue(arg_1), SourceModel.Expr.makeCharValue(arg_2)});
		}

		/**
		 * Name binding for function: equalsChar.
		 * @see #equalsChar(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName equalsChar = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "equalsChar");

		/**
		 * Helper binding method for function: equalsDecimal. 
		 * @param decimalValue1
		 * @param decimalValue2
		 * @return the SourceModule.expr representing an application of equalsDecimal
		 */
		public static final SourceModel.Expr equalsDecimal(SourceModel.Expr decimalValue1, SourceModel.Expr decimalValue2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.equalsDecimal), decimalValue1, decimalValue2});
		}

		/**
		 * Name binding for function: equalsDecimal.
		 * @see #equalsDecimal(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName equalsDecimal = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"equalsDecimal");

		/**
		 * Helper binding method for function: equalsDouble. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of equalsDouble
		 */
		public static final SourceModel.Expr equalsDouble(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.equalsDouble), arg_1, arg_2});
		}

		/**
		 * @see #equalsDouble(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of equalsDouble
		 */
		public static final SourceModel.Expr equalsDouble(double arg_1, double arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.equalsDouble), SourceModel.Expr.makeDoubleValue(arg_1), SourceModel.Expr.makeDoubleValue(arg_2)});
		}

		/**
		 * Name binding for function: equalsDouble.
		 * @see #equalsDouble(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName equalsDouble = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "equalsDouble");

		/**
		 * Helper binding method for function: equalsFloat. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of equalsFloat
		 */
		public static final SourceModel.Expr equalsFloat(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.equalsFloat), arg_1, arg_2});
		}

		/**
		 * @see #equalsFloat(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of equalsFloat
		 */
		public static final SourceModel.Expr equalsFloat(float arg_1, float arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.equalsFloat), SourceModel.Expr.makeFloatValue(arg_1), SourceModel.Expr.makeFloatValue(arg_2)});
		}

		/**
		 * Name binding for function: equalsFloat.
		 * @see #equalsFloat(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName equalsFloat = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "equalsFloat");

		/**
		 * equalsInt is used to define the instance method equals for derived Eq instances
		 * for foreign types where the underlying Java type is the primitive type "int".
		 * In particular, it is used by the derived Eq instance of Prelude.Int.
		 * @param arg_1 (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @param arg_2 (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr equalsInt(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.equalsInt), arg_1, arg_2});
		}

		/**
		 * @see #equalsInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of equalsInt
		 */
		public static final SourceModel.Expr equalsInt(int arg_1, int arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.equalsInt), SourceModel.Expr.makeIntValue(arg_1), SourceModel.Expr.makeIntValue(arg_2)});
		}

		/**
		 * Name binding for function: equalsInt.
		 * @see #equalsInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName equalsInt = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "equalsInt");

		/**
		 * Helper binding method for function: equalsListExamples. 
		 * @return the SourceModule.expr representing an application of equalsListExamples
		 */
		public static final SourceModel.Expr equalsListExamples() {
			return SourceModel.Expr.Var.make(Functions.equalsListExamples);
		}

		/**
		 * Name binding for function: equalsListExamples.
		 * @see #equalsListExamples()
		 */
		public static final QualifiedName equalsListExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"equalsListExamples");

		/**
		 * Helper binding method for function: equalsLong. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of equalsLong
		 */
		public static final SourceModel.Expr equalsLong(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.equalsLong), arg_1, arg_2});
		}

		/**
		 * @see #equalsLong(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of equalsLong
		 */
		public static final SourceModel.Expr equalsLong(long arg_1, long arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.equalsLong), SourceModel.Expr.makeLongValue(arg_1), SourceModel.Expr.makeLongValue(arg_2)});
		}

		/**
		 * Name binding for function: equalsLong.
		 * @see #equalsLong(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName equalsLong = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "equalsLong");

		/**
		 * Helper binding method for function: equalsObject. 
		 * @param jObject
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of equalsObject
		 */
		public static final SourceModel.Expr equalsObject(SourceModel.Expr jObject, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.equalsObject), jObject, arg_2});
		}

		/**
		 * Name binding for function: equalsObject.
		 * @see #equalsObject(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName equalsObject = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "equalsObject");

		/**
		 * Helper binding method for function: equalsRecord. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of equalsRecord
		 */
		public static final SourceModel.Expr equalsRecord(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.equalsRecord), arg_1, arg_2});
		}

		/**
		 * Name binding for function: equalsRecord.
		 * @see #equalsRecord(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName equalsRecord = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "equalsRecord");

		/**
		 * Helper binding method for function: equalsRecordExamples. 
		 * @return the SourceModule.expr representing an application of equalsRecordExamples
		 */
		public static final SourceModel.Expr equalsRecordExamples() {
			return SourceModel.Expr.Var.make(Functions.equalsRecordExamples);
		}

		/**
		 * Name binding for function: equalsRecordExamples.
		 * @see #equalsRecordExamples()
		 */
		public static final QualifiedName equalsRecordExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"equalsRecordExamples");

		/**
		 * Helper binding method for function: equalsRecordType. 
		 * @param dictionary1
		 * @param dictionary2
		 * @return the SourceModule.expr representing an application of equalsRecordType
		 */
		public static final SourceModel.Expr equalsRecordType(SourceModel.Expr dictionary1, SourceModel.Expr dictionary2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.equalsRecordType), dictionary1, dictionary2});
		}

		/**
		 * Name binding for function: equalsRecordType.
		 * @see #equalsRecordType(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName equalsRecordType = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"equalsRecordType");

		/**
		 * Helper binding method for function: equalsShort. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of equalsShort
		 */
		public static final SourceModel.Expr equalsShort(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.equalsShort), arg_1, arg_2});
		}

		/**
		 * @see #equalsShort(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of equalsShort
		 */
		public static final SourceModel.Expr equalsShort(short arg_1, short arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.equalsShort), SourceModel.Expr.makeShortValue(arg_1), SourceModel.Expr.makeShortValue(arg_2)});
		}

		/**
		 * Name binding for function: equalsShort.
		 * @see #equalsShort(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName equalsShort = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "equalsShort");

		/**
		 * Helper binding method for function: equalsTypeReps. 
		 * @param typeReps1
		 * @param typeReps2
		 * @return the SourceModule.expr representing an application of equalsTypeReps
		 */
		public static final SourceModel.Expr equalsTypeReps(SourceModel.Expr typeReps1, SourceModel.Expr typeReps2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.equalsTypeReps), typeReps1, typeReps2});
		}

		/**
		 * Name binding for function: equalsTypeReps.
		 * @see #equalsTypeReps(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName equalsTypeReps = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"equalsTypeReps");

		/**
		 * 
		 * @return (CAL type: <code>Cal.Core.Prelude.ExecutionContext</code>) 
		 *          the ExecutionContext that executed this call. Not a pure function.
		 */
		public static final SourceModel.Expr executionContext() {
			return SourceModel.Expr.Var.make(Functions.executionContext);
		}

		/**
		 * Name binding for function: executionContext.
		 * @see #executionContext()
		 */
		public static final QualifiedName executionContext = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"executionContext");

		/**
		 * 
		 * @param executionContext (CAL type: <code>Cal.Core.Prelude.ExecutionContext</code>)
		 * @param typeConsName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the fully-qualified type constructor name as a String e.g. "Cal.Core.Prelude.Maybe".
		 * @param foreignName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the name of the foreign class as returned by <code>Class.getName()</code>.
		 * @return (CAL type: <code>Cal.Core.Prelude.JClass</code>) 
		 *          the Java class object corresponding to this type for a foreign type. Otherwise null.
		 */
		public static final SourceModel.Expr executionContext_getForeignClass(SourceModel.Expr executionContext, SourceModel.Expr typeConsName, SourceModel.Expr foreignName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.executionContext_getForeignClass), executionContext, typeConsName, foreignName});
		}

		/**
		 * @see #executionContext_getForeignClass(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param executionContext
		 * @param typeConsName
		 * @param foreignName
		 * @return the SourceModel.Expr representing an application of executionContext_getForeignClass
		 */
		public static final SourceModel.Expr executionContext_getForeignClass(SourceModel.Expr executionContext, java.lang.String typeConsName, java.lang.String foreignName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.executionContext_getForeignClass), executionContext, SourceModel.Expr.makeStringValue(typeConsName), SourceModel.Expr.makeStringValue(foreignName)});
		}

		/**
		 * Name binding for function: executionContext_getForeignClass.
		 * @see #executionContext_getForeignClass(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName executionContext_getForeignClass = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"executionContext_getForeignClass");

		/**
		 * Helper binding method for function: fieldNameToString. 
		 * @param fieldName
		 * @return the SourceModule.expr representing an application of fieldNameToString
		 */
		public static final SourceModel.Expr fieldNameToString(SourceModel.Expr fieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fieldNameToString), fieldName});
		}

		/**
		 * Name binding for function: fieldNameToString.
		 * @see #fieldNameToString(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fieldNameToString = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"fieldNameToString");

		/**
		 * Helper binding method for function: floatToByte. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of floatToByte
		 */
		public static final SourceModel.Expr floatToByte(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatToByte), arg_1});
		}

		/**
		 * @see #floatToByte(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of floatToByte
		 */
		public static final SourceModel.Expr floatToByte(float arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatToByte), SourceModel.Expr.makeFloatValue(arg_1)});
		}

		/**
		 * Name binding for function: floatToByte.
		 * @see #floatToByte(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName floatToByte = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "floatToByte");

		/**
		 * Helper binding method for function: floatToChar. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of floatToChar
		 */
		public static final SourceModel.Expr floatToChar(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatToChar), arg_1});
		}

		/**
		 * @see #floatToChar(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of floatToChar
		 */
		public static final SourceModel.Expr floatToChar(float arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatToChar), SourceModel.Expr.makeFloatValue(arg_1)});
		}

		/**
		 * Name binding for function: floatToChar.
		 * @see #floatToChar(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName floatToChar = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "floatToChar");

		/**
		 * Helper binding method for function: floatToDecimal. 
		 * @param floatValue
		 * @return the SourceModule.expr representing an application of floatToDecimal
		 */
		public static final SourceModel.Expr floatToDecimal(SourceModel.Expr floatValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatToDecimal), floatValue});
		}

		/**
		 * @see #floatToDecimal(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param floatValue
		 * @return the SourceModel.Expr representing an application of floatToDecimal
		 */
		public static final SourceModel.Expr floatToDecimal(float floatValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatToDecimal), SourceModel.Expr.makeFloatValue(floatValue)});
		}

		/**
		 * Name binding for function: floatToDecimal.
		 * @see #floatToDecimal(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName floatToDecimal = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"floatToDecimal");

		/**
		 * Helper binding method for function: floatToDouble. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of floatToDouble
		 */
		public static final SourceModel.Expr floatToDouble(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatToDouble), arg_1});
		}

		/**
		 * @see #floatToDouble(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of floatToDouble
		 */
		public static final SourceModel.Expr floatToDouble(float arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatToDouble), SourceModel.Expr.makeFloatValue(arg_1)});
		}

		/**
		 * Name binding for function: floatToDouble.
		 * @see #floatToDouble(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName floatToDouble = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"floatToDouble");

		/**
		 * Helper binding method for function: floatToFloat. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of floatToFloat
		 */
		public static final SourceModel.Expr floatToFloat(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatToFloat), arg_1});
		}

		/**
		 * @see #floatToFloat(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of floatToFloat
		 */
		public static final SourceModel.Expr floatToFloat(float arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatToFloat), SourceModel.Expr.makeFloatValue(arg_1)});
		}

		/**
		 * Name binding for function: floatToFloat.
		 * @see #floatToFloat(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName floatToFloat = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "floatToFloat");

		/**
		 * Helper binding method for function: floatToInt. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of floatToInt
		 */
		public static final SourceModel.Expr floatToInt(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatToInt), arg_1});
		}

		/**
		 * @see #floatToInt(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of floatToInt
		 */
		public static final SourceModel.Expr floatToInt(float arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatToInt), SourceModel.Expr.makeFloatValue(arg_1)});
		}

		/**
		 * Name binding for function: floatToInt.
		 * @see #floatToInt(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName floatToInt = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "floatToInt");

		/**
		 * Helper binding method for function: floatToInteger. 
		 * @param floatValue
		 * @return the SourceModule.expr representing an application of floatToInteger
		 */
		public static final SourceModel.Expr floatToInteger(SourceModel.Expr floatValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatToInteger), floatValue});
		}

		/**
		 * @see #floatToInteger(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param floatValue
		 * @return the SourceModel.Expr representing an application of floatToInteger
		 */
		public static final SourceModel.Expr floatToInteger(float floatValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatToInteger), SourceModel.Expr.makeFloatValue(floatValue)});
		}

		/**
		 * Name binding for function: floatToInteger.
		 * @see #floatToInteger(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName floatToInteger = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"floatToInteger");

		/**
		 * Helper binding method for function: floatToLong. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of floatToLong
		 */
		public static final SourceModel.Expr floatToLong(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatToLong), arg_1});
		}

		/**
		 * @see #floatToLong(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of floatToLong
		 */
		public static final SourceModel.Expr floatToLong(float arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatToLong), SourceModel.Expr.makeFloatValue(arg_1)});
		}

		/**
		 * Name binding for function: floatToLong.
		 * @see #floatToLong(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName floatToLong = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "floatToLong");

		/**
		 * Helper binding method for function: floatToShort. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of floatToShort
		 */
		public static final SourceModel.Expr floatToShort(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatToShort), arg_1});
		}

		/**
		 * @see #floatToShort(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of floatToShort
		 */
		public static final SourceModel.Expr floatToShort(float arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatToShort), SourceModel.Expr.makeFloatValue(arg_1)});
		}

		/**
		 * Name binding for function: floatToShort.
		 * @see #floatToShort(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName floatToShort = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "floatToShort");

		/**
		 * This is the strict version of <code>Cal.Collections.List.foldLeft</code>. It is used for efficiency reasons in certain situations.
		 * the main purpose is that so the <code>Cal.Collections.List.length</code>, <code>Cal.Collections.List.sum</code> and <code>Cal.Collections.List.product</code> functions can be
		 * constant space functions.
		 * <p>
		 * This function is defined in the <code>Cal.Core.Prelude</code> module for implementation purposes only. 
		 * The public api version is <code>Cal.Collections.List.foldLeftStrict</code>.
		 * 
		 * @param foldFunction (CAL type: <code>a -> b -> a</code>)
		 *          the function to be used in folding the list.
		 * @param initialValue (CAL type: <code>a</code>)
		 *          the initial value for the folding process.
		 * @param list (CAL type: <code>[b]</code>)
		 *          the list to be folded over.
		 * @return (CAL type: <code>a</code>) 
		 *          the single result obtained from folding <code>foldFunction</code> over the list.
		 */
		public static final SourceModel.Expr foldLeftStrict(SourceModel.Expr foldFunction, SourceModel.Expr initialValue, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.foldLeftStrict), foldFunction, initialValue, list});
		}

		/**
		 * Name binding for function: foldLeftStrict.
		 * @see #foldLeftStrict(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName foldLeftStrict = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"foldLeftStrict");

		/**
		 * Similar to <code>Cal.Collections.List.foldLeft</code>, except that the folding process on the list is started with its rightmost element.
		 * Often the result of applying <code>Cal.Collections.List.foldLeft</code> or <code>Cal.Core.Prelude.foldRight</code> is the same, and the choice between them is a matter of
		 * efficiency. Which is better depends on the nature of the folding function. As a general rule, if the folding
		 * function is strict in both arguments, <code>Cal.Core.Prelude.foldLeftStrict</code> is a good choice. Otherwise <code>Cal.Core.Prelude.foldRight</code> is often best.
		 * <p>
		 * This function is defined in the <code>Cal.Core.Prelude</code> module for implementation purposes only. 
		 * The public api version is <code>Cal.Collections.List.foldRight</code>.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Prelude.foldLeftStrict
		 * </dl>
		 * 
		 * @param foldFunction (CAL type: <code>a -> b -> b</code>)
		 *          the function to be used in folding the list.
		 * @param initialValue (CAL type: <code>b</code>)
		 *          the initial value for the folding process.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list to be folded over.
		 * @return (CAL type: <code>b</code>) 
		 *          the single result obtained from folding <code>foldFunction</code> over the list.
		 */
		public static final SourceModel.Expr foldRight(SourceModel.Expr foldFunction, SourceModel.Expr initialValue, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.foldRight), foldFunction, initialValue, list});
		}

		/**
		 * Name binding for function: foldRight.
		 * @see #foldRight(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName foldRight = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "foldRight");

		/**
		 * 
		 * @param typeRep (CAL type: <code>Cal.Core.Prelude.TypeRep</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Core.Prelude.JClass</code>) 
		 *          <code>Cal.Core.Prelude.Just javaClass</code>, where <code>javaClass</code> is the underlying Java class object
		 * of the foreign type, if <code>Cal.Core.Prelude.TypeRep</code> represents a non-foreign type. Otherwise <code>Cal.Core.Prelude.Nothing</code>.
		 */
		public static final SourceModel.Expr foreignClass(SourceModel.Expr typeRep) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.foreignClass), typeRep});
		}

		/**
		 * Name binding for function: foreignClass.
		 * @see #foreignClass(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName foreignClass = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "foreignClass");

		/**
		 * Helper binding method for function: greaterThanByte. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of greaterThanByte
		 */
		public static final SourceModel.Expr greaterThanByte(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.greaterThanByte), arg_1, arg_2});
		}

		/**
		 * @see #greaterThanByte(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of greaterThanByte
		 */
		public static final SourceModel.Expr greaterThanByte(byte arg_1, byte arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.greaterThanByte), SourceModel.Expr.makeByteValue(arg_1), SourceModel.Expr.makeByteValue(arg_2)});
		}

		/**
		 * Name binding for function: greaterThanByte.
		 * @see #greaterThanByte(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName greaterThanByte = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"greaterThanByte");

		/**
		 * Helper binding method for function: greaterThanChar. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of greaterThanChar
		 */
		public static final SourceModel.Expr greaterThanChar(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.greaterThanChar), arg_1, arg_2});
		}

		/**
		 * @see #greaterThanChar(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of greaterThanChar
		 */
		public static final SourceModel.Expr greaterThanChar(char arg_1, char arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.greaterThanChar), SourceModel.Expr.makeCharValue(arg_1), SourceModel.Expr.makeCharValue(arg_2)});
		}

		/**
		 * Name binding for function: greaterThanChar.
		 * @see #greaterThanChar(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName greaterThanChar = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"greaterThanChar");

		/**
		 * Helper binding method for function: greaterThanComparable. 
		 * @param x
		 * @param y
		 * @return the SourceModule.expr representing an application of greaterThanComparable
		 */
		public static final SourceModel.Expr greaterThanComparable(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.greaterThanComparable), x, y});
		}

		/**
		 * Name binding for function: greaterThanComparable.
		 * @see #greaterThanComparable(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName greaterThanComparable = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"greaterThanComparable");

		/**
		 * Helper binding method for function: greaterThanDecimal. 
		 * @param decimalValue1
		 * @param decimalValue2
		 * @return the SourceModule.expr representing an application of greaterThanDecimal
		 */
		public static final SourceModel.Expr greaterThanDecimal(SourceModel.Expr decimalValue1, SourceModel.Expr decimalValue2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.greaterThanDecimal), decimalValue1, decimalValue2});
		}

		/**
		 * Name binding for function: greaterThanDecimal.
		 * @see #greaterThanDecimal(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName greaterThanDecimal = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"greaterThanDecimal");

		/**
		 * Helper binding method for function: greaterThanDefault. 
		 * @param x
		 * @param y
		 * @return the SourceModule.expr representing an application of greaterThanDefault
		 */
		public static final SourceModel.Expr greaterThanDefault(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.greaterThanDefault), x, y});
		}

		/**
		 * Name binding for function: greaterThanDefault.
		 * @see #greaterThanDefault(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName greaterThanDefault = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"greaterThanDefault");

		/**
		 * Helper binding method for function: greaterThanDouble. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of greaterThanDouble
		 */
		public static final SourceModel.Expr greaterThanDouble(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.greaterThanDouble), arg_1, arg_2});
		}

		/**
		 * @see #greaterThanDouble(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of greaterThanDouble
		 */
		public static final SourceModel.Expr greaterThanDouble(double arg_1, double arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.greaterThanDouble), SourceModel.Expr.makeDoubleValue(arg_1), SourceModel.Expr.makeDoubleValue(arg_2)});
		}

		/**
		 * Name binding for function: greaterThanDouble.
		 * @see #greaterThanDouble(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName greaterThanDouble = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"greaterThanDouble");

		/**
		 * Helper binding method for function: greaterThanEqualsByte. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of greaterThanEqualsByte
		 */
		public static final SourceModel.Expr greaterThanEqualsByte(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.greaterThanEqualsByte), arg_1, arg_2});
		}

		/**
		 * @see #greaterThanEqualsByte(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of greaterThanEqualsByte
		 */
		public static final SourceModel.Expr greaterThanEqualsByte(byte arg_1, byte arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.greaterThanEqualsByte), SourceModel.Expr.makeByteValue(arg_1), SourceModel.Expr.makeByteValue(arg_2)});
		}

		/**
		 * Name binding for function: greaterThanEqualsByte.
		 * @see #greaterThanEqualsByte(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName greaterThanEqualsByte = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"greaterThanEqualsByte");

		/**
		 * Helper binding method for function: greaterThanEqualsChar. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of greaterThanEqualsChar
		 */
		public static final SourceModel.Expr greaterThanEqualsChar(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.greaterThanEqualsChar), arg_1, arg_2});
		}

		/**
		 * @see #greaterThanEqualsChar(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of greaterThanEqualsChar
		 */
		public static final SourceModel.Expr greaterThanEqualsChar(char arg_1, char arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.greaterThanEqualsChar), SourceModel.Expr.makeCharValue(arg_1), SourceModel.Expr.makeCharValue(arg_2)});
		}

		/**
		 * Name binding for function: greaterThanEqualsChar.
		 * @see #greaterThanEqualsChar(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName greaterThanEqualsChar = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"greaterThanEqualsChar");

		/**
		 * Helper binding method for function: greaterThanEqualsComparable. 
		 * @param x
		 * @param y
		 * @return the SourceModule.expr representing an application of greaterThanEqualsComparable
		 */
		public static final SourceModel.Expr greaterThanEqualsComparable(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.greaterThanEqualsComparable), x, y});
		}

		/**
		 * Name binding for function: greaterThanEqualsComparable.
		 * @see #greaterThanEqualsComparable(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName greaterThanEqualsComparable = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"greaterThanEqualsComparable");

		/**
		 * Helper binding method for function: greaterThanEqualsDecimal. 
		 * @param decimalValue1
		 * @param decimalValue2
		 * @return the SourceModule.expr representing an application of greaterThanEqualsDecimal
		 */
		public static final SourceModel.Expr greaterThanEqualsDecimal(SourceModel.Expr decimalValue1, SourceModel.Expr decimalValue2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.greaterThanEqualsDecimal), decimalValue1, decimalValue2});
		}

		/**
		 * Name binding for function: greaterThanEqualsDecimal.
		 * @see #greaterThanEqualsDecimal(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName greaterThanEqualsDecimal = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"greaterThanEqualsDecimal");

		/**
		 * Helper binding method for function: greaterThanEqualsDefault. 
		 * @param x
		 * @param y
		 * @return the SourceModule.expr representing an application of greaterThanEqualsDefault
		 */
		public static final SourceModel.Expr greaterThanEqualsDefault(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.greaterThanEqualsDefault), x, y});
		}

		/**
		 * Name binding for function: greaterThanEqualsDefault.
		 * @see #greaterThanEqualsDefault(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName greaterThanEqualsDefault = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"greaterThanEqualsDefault");

		/**
		 * Helper binding method for function: greaterThanEqualsDouble. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of greaterThanEqualsDouble
		 */
		public static final SourceModel.Expr greaterThanEqualsDouble(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.greaterThanEqualsDouble), arg_1, arg_2});
		}

		/**
		 * @see #greaterThanEqualsDouble(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of greaterThanEqualsDouble
		 */
		public static final SourceModel.Expr greaterThanEqualsDouble(double arg_1, double arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.greaterThanEqualsDouble), SourceModel.Expr.makeDoubleValue(arg_1), SourceModel.Expr.makeDoubleValue(arg_2)});
		}

		/**
		 * Name binding for function: greaterThanEqualsDouble.
		 * @see #greaterThanEqualsDouble(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName greaterThanEqualsDouble = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"greaterThanEqualsDouble");

		/**
		 * Helper binding method for function: greaterThanEqualsFloat. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of greaterThanEqualsFloat
		 */
		public static final SourceModel.Expr greaterThanEqualsFloat(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.greaterThanEqualsFloat), arg_1, arg_2});
		}

		/**
		 * @see #greaterThanEqualsFloat(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of greaterThanEqualsFloat
		 */
		public static final SourceModel.Expr greaterThanEqualsFloat(float arg_1, float arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.greaterThanEqualsFloat), SourceModel.Expr.makeFloatValue(arg_1), SourceModel.Expr.makeFloatValue(arg_2)});
		}

		/**
		 * Name binding for function: greaterThanEqualsFloat.
		 * @see #greaterThanEqualsFloat(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName greaterThanEqualsFloat = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"greaterThanEqualsFloat");

		/**
		 * Helper binding method for function: greaterThanEqualsInt. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of greaterThanEqualsInt
		 */
		public static final SourceModel.Expr greaterThanEqualsInt(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.greaterThanEqualsInt), arg_1, arg_2});
		}

		/**
		 * @see #greaterThanEqualsInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of greaterThanEqualsInt
		 */
		public static final SourceModel.Expr greaterThanEqualsInt(int arg_1, int arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.greaterThanEqualsInt), SourceModel.Expr.makeIntValue(arg_1), SourceModel.Expr.makeIntValue(arg_2)});
		}

		/**
		 * Name binding for function: greaterThanEqualsInt.
		 * @see #greaterThanEqualsInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName greaterThanEqualsInt = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"greaterThanEqualsInt");

		/**
		 * Helper binding method for function: greaterThanEqualsInteger. 
		 * @param x
		 * @param y
		 * @return the SourceModule.expr representing an application of greaterThanEqualsInteger
		 */
		public static final SourceModel.Expr greaterThanEqualsInteger(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.greaterThanEqualsInteger), x, y});
		}

		/**
		 * Name binding for function: greaterThanEqualsInteger.
		 * @see #greaterThanEqualsInteger(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName greaterThanEqualsInteger = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"greaterThanEqualsInteger");

		/**
		 * Helper binding method for function: greaterThanEqualsLong. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of greaterThanEqualsLong
		 */
		public static final SourceModel.Expr greaterThanEqualsLong(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.greaterThanEqualsLong), arg_1, arg_2});
		}

		/**
		 * @see #greaterThanEqualsLong(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of greaterThanEqualsLong
		 */
		public static final SourceModel.Expr greaterThanEqualsLong(long arg_1, long arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.greaterThanEqualsLong), SourceModel.Expr.makeLongValue(arg_1), SourceModel.Expr.makeLongValue(arg_2)});
		}

		/**
		 * Name binding for function: greaterThanEqualsLong.
		 * @see #greaterThanEqualsLong(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName greaterThanEqualsLong = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"greaterThanEqualsLong");

		/**
		 * Helper binding method for function: greaterThanEqualsRecord. 
		 * @param r1
		 * @param r2
		 * @return the SourceModule.expr representing an application of greaterThanEqualsRecord
		 */
		public static final SourceModel.Expr greaterThanEqualsRecord(SourceModel.Expr r1, SourceModel.Expr r2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.greaterThanEqualsRecord), r1, r2});
		}

		/**
		 * Name binding for function: greaterThanEqualsRecord.
		 * @see #greaterThanEqualsRecord(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName greaterThanEqualsRecord = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"greaterThanEqualsRecord");

		/**
		 * Helper binding method for function: greaterThanEqualsShort. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of greaterThanEqualsShort
		 */
		public static final SourceModel.Expr greaterThanEqualsShort(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.greaterThanEqualsShort), arg_1, arg_2});
		}

		/**
		 * @see #greaterThanEqualsShort(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of greaterThanEqualsShort
		 */
		public static final SourceModel.Expr greaterThanEqualsShort(short arg_1, short arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.greaterThanEqualsShort), SourceModel.Expr.makeShortValue(arg_1), SourceModel.Expr.makeShortValue(arg_2)});
		}

		/**
		 * Name binding for function: greaterThanEqualsShort.
		 * @see #greaterThanEqualsShort(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName greaterThanEqualsShort = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"greaterThanEqualsShort");

		/**
		 * Helper binding method for function: greaterThanFloat. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of greaterThanFloat
		 */
		public static final SourceModel.Expr greaterThanFloat(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.greaterThanFloat), arg_1, arg_2});
		}

		/**
		 * @see #greaterThanFloat(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of greaterThanFloat
		 */
		public static final SourceModel.Expr greaterThanFloat(float arg_1, float arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.greaterThanFloat), SourceModel.Expr.makeFloatValue(arg_1), SourceModel.Expr.makeFloatValue(arg_2)});
		}

		/**
		 * Name binding for function: greaterThanFloat.
		 * @see #greaterThanFloat(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName greaterThanFloat = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"greaterThanFloat");

		/**
		 * Helper binding method for function: greaterThanInt. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of greaterThanInt
		 */
		public static final SourceModel.Expr greaterThanInt(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.greaterThanInt), arg_1, arg_2});
		}

		/**
		 * @see #greaterThanInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of greaterThanInt
		 */
		public static final SourceModel.Expr greaterThanInt(int arg_1, int arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.greaterThanInt), SourceModel.Expr.makeIntValue(arg_1), SourceModel.Expr.makeIntValue(arg_2)});
		}

		/**
		 * Name binding for function: greaterThanInt.
		 * @see #greaterThanInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName greaterThanInt = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"greaterThanInt");

		/**
		 * Helper binding method for function: greaterThanInteger. 
		 * @param x
		 * @param y
		 * @return the SourceModule.expr representing an application of greaterThanInteger
		 */
		public static final SourceModel.Expr greaterThanInteger(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.greaterThanInteger), x, y});
		}

		/**
		 * Name binding for function: greaterThanInteger.
		 * @see #greaterThanInteger(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName greaterThanInteger = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"greaterThanInteger");

		/**
		 * Helper binding method for function: greaterThanListExamples. 
		 * @return the SourceModule.expr representing an application of greaterThanListExamples
		 */
		public static final SourceModel.Expr greaterThanListExamples() {
			return SourceModel.Expr.Var.make(Functions.greaterThanListExamples);
		}

		/**
		 * Name binding for function: greaterThanListExamples.
		 * @see #greaterThanListExamples()
		 */
		public static final QualifiedName greaterThanListExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"greaterThanListExamples");

		/**
		 * Helper binding method for function: greaterThanLong. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of greaterThanLong
		 */
		public static final SourceModel.Expr greaterThanLong(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.greaterThanLong), arg_1, arg_2});
		}

		/**
		 * @see #greaterThanLong(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of greaterThanLong
		 */
		public static final SourceModel.Expr greaterThanLong(long arg_1, long arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.greaterThanLong), SourceModel.Expr.makeLongValue(arg_1), SourceModel.Expr.makeLongValue(arg_2)});
		}

		/**
		 * Name binding for function: greaterThanLong.
		 * @see #greaterThanLong(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName greaterThanLong = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"greaterThanLong");

		/**
		 * Helper binding method for function: greaterThanRecord. 
		 * @param r1
		 * @param r2
		 * @return the SourceModule.expr representing an application of greaterThanRecord
		 */
		public static final SourceModel.Expr greaterThanRecord(SourceModel.Expr r1, SourceModel.Expr r2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.greaterThanRecord), r1, r2});
		}

		/**
		 * Name binding for function: greaterThanRecord.
		 * @see #greaterThanRecord(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName greaterThanRecord = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"greaterThanRecord");

		/**
		 * Helper binding method for function: greaterThanRecordExamples. 
		 * @return the SourceModule.expr representing an application of greaterThanRecordExamples
		 */
		public static final SourceModel.Expr greaterThanRecordExamples() {
			return 
				SourceModel.Expr.Var.make(Functions.greaterThanRecordExamples);
		}

		/**
		 * Name binding for function: greaterThanRecordExamples.
		 * @see #greaterThanRecordExamples()
		 */
		public static final QualifiedName greaterThanRecordExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"greaterThanRecordExamples");

		/**
		 * Helper binding method for function: greaterThanShort. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of greaterThanShort
		 */
		public static final SourceModel.Expr greaterThanShort(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.greaterThanShort), arg_1, arg_2});
		}

		/**
		 * @see #greaterThanShort(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of greaterThanShort
		 */
		public static final SourceModel.Expr greaterThanShort(short arg_1, short arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.greaterThanShort), SourceModel.Expr.makeShortValue(arg_1), SourceModel.Expr.makeShortValue(arg_2)});
		}

		/**
		 * Name binding for function: greaterThanShort.
		 * @see #greaterThanShort(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName greaterThanShort = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"greaterThanShort");

		/**
		 * Used to implement the <code>Cal.Core.Prelude.input</code> instance function for derived instances of
		 * <code>Cal.Core.Prelude.Inputable</code> for foreign types having implementation type "boolean".
		 * If the argument is not actually a <code>java.lang.Boolean</code>, then we get a class-cast exception at runtime.
		 * @param object (CAL type: <code>Cal.Core.Prelude.JObject</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr inputBoolean(SourceModel.Expr object) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inputBoolean), object});
		}

		/**
		 * Name binding for function: inputBoolean.
		 * @see #inputBoolean(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName inputBoolean = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "inputBoolean");

		/**
		 * Used to implement the <code>Cal.Core.Prelude.input</code> instance function for derived instances of
		 * <code>Cal.Core.Prelude.Inputable</code> for foreign types having implementation type "byte".
		 * If the argument is not actually a <code>java.lang.Byte</code>, then we get a class-cast exception at runtime.
		 * @param object (CAL type: <code>Cal.Core.Prelude.JObject</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Byte</code>) 
		 */
		public static final SourceModel.Expr inputByte(SourceModel.Expr object) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inputByte), object});
		}

		/**
		 * Name binding for function: inputByte.
		 * @see #inputByte(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName inputByte = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "inputByte");

		/**
		 * Used to implement the <code>Cal.Core.Prelude.input</code> instance function for derived instances of
		 * <code>Cal.Core.Prelude.Inputable</code> for foreign types having implementation type "char".
		 * If the argument is not actually a <code>java.lang.Character</code>, then we get a class-cast exception at runtime.
		 * @param object (CAL type: <code>Cal.Core.Prelude.JObject</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Char</code>) 
		 */
		public static final SourceModel.Expr inputChar(SourceModel.Expr object) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inputChar), object});
		}

		/**
		 * Name binding for function: inputChar.
		 * @see #inputChar(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName inputChar = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "inputChar");

		/**
		 * Used to implement the <code>Cal.Core.Prelude.input</code> instance function for derived instances of
		 * <code>Cal.Core.Prelude.Inputable</code> for foreign types having implementation type "double". 
		 * If the argument is not actually a <code>java.lang.Double</code>, then we get a class-cast exception at runtime.
		 * @param object (CAL type: <code>Cal.Core.Prelude.JObject</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 */
		public static final SourceModel.Expr inputDouble(SourceModel.Expr object) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inputDouble), object});
		}

		/**
		 * Name binding for function: inputDouble.
		 * @see #inputDouble(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName inputDouble = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "inputDouble");

		/**
		 * Helper binding method for function: inputEither. 
		 * @param javaEitherValue
		 * @return the SourceModule.expr representing an application of inputEither
		 */
		public static final SourceModel.Expr inputEither(SourceModel.Expr javaEitherValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inputEither), javaEitherValue});
		}

		/**
		 * Name binding for function: inputEither.
		 * @see #inputEither(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName inputEither = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "inputEither");

		/**
		 * Used to implement the <code>Cal.Core.Prelude.input</code> instance function for derived instances of
		 * <code>Cal.Core.Prelude.Inputable</code> for foreign types having implementation type "float".
		 * If the argument is not actually a <code>java.lang.Float</code>, then we get a class-cast exception at runtime.
		 * @param object (CAL type: <code>Cal.Core.Prelude.JObject</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Float</code>) 
		 */
		public static final SourceModel.Expr inputFloat(SourceModel.Expr object) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inputFloat), object});
		}

		/**
		 * Name binding for function: inputFloat.
		 * @see #inputFloat(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName inputFloat = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "inputFloat");

		/**
		 * Used to implement the <code>Cal.Core.Prelude.input</code> instance function for derived instances of
		 * <code>Cal.Core.Prelude.Inputable</code> for foreign types having implementation type "int".
		 * If the argument is not actually a <code>java.lang.Integer</code>, then we get a class-cast exception at runtime.
		 * @param object (CAL type: <code>Cal.Core.Prelude.JObject</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 */
		public static final SourceModel.Expr inputInt(SourceModel.Expr object) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inputInt), object});
		}

		/**
		 * Name binding for function: inputInt.
		 * @see #inputInt(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName inputInt = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "inputInt");

		/**
		 * Helper binding method for function: inputListFromJObject. 
		 * @param object
		 * @return the SourceModule.expr representing an application of inputListFromJObject
		 */
		public static final SourceModel.Expr inputListFromJObject(SourceModel.Expr object) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inputListFromJObject), object});
		}

		/**
		 * Name binding for function: inputListFromJObject.
		 * @see #inputListFromJObject(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName inputListFromJObject = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"inputListFromJObject");

		/**
		 * Used to implement the <code>Cal.Core.Prelude.input</code> instance function for derived instances of
		 * <code>Cal.Core.Prelude.Inputable</code> for foreign types having implementation type "long". 
		 * If the argument is not actually a <code>java.lang.Long</code>, then we get a class-cast exception at runtime.
		 * @param object (CAL type: <code>Cal.Core.Prelude.JObject</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Long</code>) 
		 */
		public static final SourceModel.Expr inputLong(SourceModel.Expr object) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inputLong), object});
		}

		/**
		 * Name binding for function: inputLong.
		 * @see #inputLong(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName inputLong = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "inputLong");

		/**
		 * Helper binding method for function: inputMaybe. 
		 * @param javaMaybeValue
		 * @return the SourceModule.expr representing an application of inputMaybe
		 */
		public static final SourceModel.Expr inputMaybe(SourceModel.Expr javaMaybeValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inputMaybe), javaMaybeValue});
		}

		/**
		 * Name binding for function: inputMaybe.
		 * @see #inputMaybe(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName inputMaybe = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "inputMaybe");

		/**
		 * Helper binding method for function: inputOrdering. 
		 * @param javaOrderingValue
		 * @return the SourceModule.expr representing an application of inputOrdering
		 */
		public static final SourceModel.Expr inputOrdering(SourceModel.Expr javaOrderingValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inputOrdering), javaOrderingValue});
		}

		/**
		 * Name binding for function: inputOrdering.
		 * @see #inputOrdering(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName inputOrdering = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"inputOrdering");

		/**
		 * Helper binding method for function: inputRecord. 
		 * @param record
		 * @return the SourceModule.expr representing an application of inputRecord
		 */
		public static final SourceModel.Expr inputRecord(SourceModel.Expr record) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inputRecord), record});
		}

		/**
		 * Name binding for function: inputRecord.
		 * @see #inputRecord(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName inputRecord = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "inputRecord");

		/**
		 * Used to implement the <code>Cal.Core.Prelude.input</code> instance function for derived instances of
		 * <code>Cal.Core.Prelude.Inputable</code> for foreign types having implementation type "short".
		 * If the argument is not actually a <code>java.lang.Short</code>, then we get a class-cast exception at runtime.
		 * @param object (CAL type: <code>Cal.Core.Prelude.JObject</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Short</code>) 
		 */
		public static final SourceModel.Expr inputShort(SourceModel.Expr object) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inputShort), object});
		}

		/**
		 * Name binding for function: inputShort.
		 * @see #inputShort(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName inputShort = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "inputShort");

		/**
		 * Helper binding method for function: inputStringFromJObject. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of inputStringFromJObject
		 */
		public static final SourceModel.Expr inputStringFromJObject(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inputStringFromJObject), arg_1});
		}

		/**
		 * Name binding for function: inputStringFromJObject.
		 * @see #inputStringFromJObject(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName inputStringFromJObject = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"inputStringFromJObject");

		/**
		 * A type-specialized version of <code>Cal.Core.Prelude.input</code> for use as a marshalling function in a standalone JAR.
		 * @param object (CAL type: <code>Cal.Core.Prelude.JObject</code>)
		 *          a list of Strings as represented by a Java object of type <code>java.util.Collection</code>,
		 * <code>java.util.Iterator</code>, <code>java.util.Enumeration</code> or a Java <code>java.lang.String</code> array.
		 * @return (CAL type: <code>[Cal.Core.Prelude.String]</code>) 
		 *          the specified list as a CAL list of <code>Cal.Core.Prelude.String</code>s.
		 */
		public static final SourceModel.Expr inputStringList(SourceModel.Expr object) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inputStringList), object});
		}

		/**
		 * Name binding for function: inputStringList.
		 * @see #inputStringList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName inputStringList = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"inputStringList");

		/**
		 * Helper binding method for function: inputUnit. 
		 * @param value
		 * @return the SourceModule.expr representing an application of inputUnit
		 */
		public static final SourceModel.Expr inputUnit(SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inputUnit), value});
		}

		/**
		 * Name binding for function: inputUnit.
		 * @see #inputUnit(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName inputUnit = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "inputUnit");

		/**
		 * Helper binding method for function: intIntEnumExamples. 
		 * @return the SourceModule.expr representing an application of intIntEnumExamples
		 */
		public static final SourceModel.Expr intIntEnumExamples() {
			return SourceModel.Expr.Var.make(Functions.intIntEnumExamples);
		}

		/**
		 * Name binding for function: intIntEnumExamples.
		 * @see #intIntEnumExamples()
		 */
		public static final QualifiedName intIntEnumExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"intIntEnumExamples");

		/**
		 * Helper binding method for function: intToByte. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of intToByte
		 */
		public static final SourceModel.Expr intToByte(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intToByte), arg_1});
		}

		/**
		 * @see #intToByte(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of intToByte
		 */
		public static final SourceModel.Expr intToByte(int arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intToByte), SourceModel.Expr.makeIntValue(arg_1)});
		}

		/**
		 * Name binding for function: intToByte.
		 * @see #intToByte(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intToByte = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "intToByte");

		/**
		 * Helper binding method for function: intToChar. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of intToChar
		 */
		public static final SourceModel.Expr intToChar(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intToChar), arg_1});
		}

		/**
		 * @see #intToChar(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of intToChar
		 */
		public static final SourceModel.Expr intToChar(int arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intToChar), SourceModel.Expr.makeIntValue(arg_1)});
		}

		/**
		 * Name binding for function: intToChar.
		 * @see #intToChar(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intToChar = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "intToChar");

		/**
		 * Helper binding method for function: intToDecimal. 
		 * @param intValue
		 * @return the SourceModule.expr representing an application of intToDecimal
		 */
		public static final SourceModel.Expr intToDecimal(SourceModel.Expr intValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intToDecimal), intValue});
		}

		/**
		 * @see #intToDecimal(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param intValue
		 * @return the SourceModel.Expr representing an application of intToDecimal
		 */
		public static final SourceModel.Expr intToDecimal(int intValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intToDecimal), SourceModel.Expr.makeIntValue(intValue)});
		}

		/**
		 * Name binding for function: intToDecimal.
		 * @see #intToDecimal(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intToDecimal = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "intToDecimal");

		/**
		 * Helper binding method for function: intToDouble. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of intToDouble
		 */
		public static final SourceModel.Expr intToDouble(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intToDouble), arg_1});
		}

		/**
		 * @see #intToDouble(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of intToDouble
		 */
		public static final SourceModel.Expr intToDouble(int arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intToDouble), SourceModel.Expr.makeIntValue(arg_1)});
		}

		/**
		 * Name binding for function: intToDouble.
		 * @see #intToDouble(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intToDouble = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "intToDouble");

		/**
		 * The <code>Cal.Core.Prelude.intToEnum</code> instance function for the <code>Cal.Core.Prelude.Byte</code> type.
		 * @param intValue (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          The value to convert to a <code>Cal.Core.Prelude.Byte</code>.  If <code>intValue</code> is out of <code>Cal.Core.Prelude.Byte</code>'s range, an error will
		 * be raised.
		 * @return (CAL type: <code>Cal.Core.Prelude.Byte</code>) 
		 *          a <code>Cal.Core.Prelude.Byte</code> representing the same value as <code>intValue</code>.
		 */
		public static final SourceModel.Expr intToEnumByte(SourceModel.Expr intValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intToEnumByte), intValue});
		}

		/**
		 * @see #intToEnumByte(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param intValue
		 * @return the SourceModel.Expr representing an application of intToEnumByte
		 */
		public static final SourceModel.Expr intToEnumByte(int intValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intToEnumByte), SourceModel.Expr.makeIntValue(intValue)});
		}

		/**
		 * Name binding for function: intToEnumByte.
		 * @see #intToEnumByte(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intToEnumByte = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"intToEnumByte");

		/**
		 * The <code>Cal.Core.Prelude.intToEnum</code> instance function for the <code>Cal.Core.Prelude.Char</code> type.
		 * @param intValue (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          The value to convert to a <code>Cal.Core.Prelude.Char</code>.  If <code>intValue</code> is out of <code>Cal.Core.Prelude.Char</code>'s range, an error will
		 * be raised.
		 * @return (CAL type: <code>Cal.Core.Prelude.Char</code>) 
		 *          a <code>Cal.Core.Prelude.Char</code> representing the same value as <code>intValue</code>.
		 */
		public static final SourceModel.Expr intToEnumChar(SourceModel.Expr intValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intToEnumChar), intValue});
		}

		/**
		 * @see #intToEnumChar(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param intValue
		 * @return the SourceModel.Expr representing an application of intToEnumChar
		 */
		public static final SourceModel.Expr intToEnumChar(int intValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intToEnumChar), SourceModel.Expr.makeIntValue(intValue)});
		}

		/**
		 * Name binding for function: intToEnumChar.
		 * @see #intToEnumChar(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intToEnumChar = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"intToEnumChar");

		/**
		 * The <code>Cal.Core.Prelude.intToEnumChecked</code> instance function for the <code>Cal.Core.Prelude.Byte</code> type.
		 * @param intValue (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          The value to convert to a <code>Cal.Core.Prelude.Byte</code>.
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Core.Prelude.Byte</code>) 
		 *          <code>Cal.Core.Prelude.Just val</code> where <code>val</code> is a <code>Cal.Core.Prelude.Byte</code> representing the same value as <code>intValue</code> if
		 * <code>intValue</code> is within <code>Cal.Core.Prelude.Byte</code>'s range, or <code>Cal.Core.Prelude.Nothing</code> otherwise.
		 */
		public static final SourceModel.Expr intToEnumCheckedByte(SourceModel.Expr intValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intToEnumCheckedByte), intValue});
		}

		/**
		 * @see #intToEnumCheckedByte(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param intValue
		 * @return the SourceModel.Expr representing an application of intToEnumCheckedByte
		 */
		public static final SourceModel.Expr intToEnumCheckedByte(int intValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intToEnumCheckedByte), SourceModel.Expr.makeIntValue(intValue)});
		}

		/**
		 * Name binding for function: intToEnumCheckedByte.
		 * @see #intToEnumCheckedByte(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intToEnumCheckedByte = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"intToEnumCheckedByte");

		/**
		 * The <code>Cal.Core.Prelude.intToEnumChecked</code> instance function for the <code>Cal.Core.Prelude.Char</code> type.
		 * @param intValue (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          The value to convert to a <code>Cal.Core.Prelude.Char</code>.
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Core.Prelude.Char</code>) 
		 *          <code>Cal.Core.Prelude.Just val</code> where <code>val</code> is a <code>Cal.Core.Prelude.Char</code> representing the same value as <code>intValue</code> if
		 * <code>intValue</code> is within <code>Cal.Core.Prelude.Char</code>'s range, or <code>Cal.Core.Prelude.Nothing</code> otherwise.
		 */
		public static final SourceModel.Expr intToEnumCheckedChar(SourceModel.Expr intValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intToEnumCheckedChar), intValue});
		}

		/**
		 * @see #intToEnumCheckedChar(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param intValue
		 * @return the SourceModel.Expr representing an application of intToEnumCheckedChar
		 */
		public static final SourceModel.Expr intToEnumCheckedChar(int intValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intToEnumCheckedChar), SourceModel.Expr.makeIntValue(intValue)});
		}

		/**
		 * Name binding for function: intToEnumCheckedChar.
		 * @see #intToEnumCheckedChar(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intToEnumCheckedChar = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"intToEnumCheckedChar");

		/**
		 * The <code>Cal.Core.Prelude.intToEnum</code> instance function for the <code>Cal.Core.Prelude.Int</code> type.
		 * @param intValue (CAL type: <code>a</code>)
		 *          The value to "convert" to an <code>Cal.Core.Prelude.Int</code>
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe a</code>) 
		 *          <code>Cal.Core.Prelude.Just intValue</code> (since every <code>Cal.Core.Prelude.Int</code> maps to an <code>Cal.Core.Prelude.Int</code> value, we never return <code>Cal.Core.Prelude.Nothing</code>).
		 */
		public static final SourceModel.Expr intToEnumCheckedInt(SourceModel.Expr intValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intToEnumCheckedInt), intValue});
		}

		/**
		 * Name binding for function: intToEnumCheckedInt.
		 * @see #intToEnumCheckedInt(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intToEnumCheckedInt = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"intToEnumCheckedInt");

		/**
		 * The <code>Cal.Core.Prelude.intToEnumChecked</code> instance function for the <code>Cal.Core.Prelude.Ordering</code> type.
		 * <p>
		 * All <code>Cal.Core.Prelude.Int</code> values are valid (since all <code>Cal.Core.Prelude.Int</code> values are either negative, 0, or positive), 
		 * so we never return <code>Cal.Core.Prelude.Nothing</code>.
		 * 
		 * @param intValue (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          The <code>Cal.Core.Prelude.Int</code> value to be converted
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Core.Prelude.Ordering</code>) 
		 *          <code>Cal.Core.Prelude.Just orderingValue</code>, where <code>orderingValue</code> is <code>Cal.Core.Prelude.LT</code> for negative values
		 * of <code>intValue</code>, <code>Cal.Core.Prelude.EQ</code> when <code>intValue</code> is 0, and <code>Cal.Core.Prelude.GT</code> for positive values of <code>intValue</code>.
		 */
		public static final SourceModel.Expr intToEnumCheckedOrdering(SourceModel.Expr intValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intToEnumCheckedOrdering), intValue});
		}

		/**
		 * @see #intToEnumCheckedOrdering(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param intValue
		 * @return the SourceModel.Expr representing an application of intToEnumCheckedOrdering
		 */
		public static final SourceModel.Expr intToEnumCheckedOrdering(int intValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intToEnumCheckedOrdering), SourceModel.Expr.makeIntValue(intValue)});
		}

		/**
		 * Name binding for function: intToEnumCheckedOrdering.
		 * @see #intToEnumCheckedOrdering(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intToEnumCheckedOrdering = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"intToEnumCheckedOrdering");

		/**
		 * The <code>Cal.Core.Prelude.intToEnumChecked</code> instance function for the <code>Cal.Core.Prelude.Short</code> type.
		 * @param intValue (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          The value to convert to a <code>Cal.Core.Prelude.Short</code>.
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Core.Prelude.Short</code>) 
		 *          <code>Cal.Core.Prelude.Just val</code> where <code>val</code> is a <code>Cal.Core.Prelude.Short</code> representing the same value as <code>intValue</code> if
		 * <code>intValue</code> is within <code>Cal.Core.Prelude.Short</code>'s range, or <code>Cal.Core.Prelude.Nothing</code> otherwise.
		 */
		public static final SourceModel.Expr intToEnumCheckedShort(SourceModel.Expr intValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intToEnumCheckedShort), intValue});
		}

		/**
		 * @see #intToEnumCheckedShort(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param intValue
		 * @return the SourceModel.Expr representing an application of intToEnumCheckedShort
		 */
		public static final SourceModel.Expr intToEnumCheckedShort(int intValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intToEnumCheckedShort), SourceModel.Expr.makeIntValue(intValue)});
		}

		/**
		 * Name binding for function: intToEnumCheckedShort.
		 * @see #intToEnumCheckedShort(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intToEnumCheckedShort = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"intToEnumCheckedShort");

		/**
		 * Helper binding method for function: intToEnumDefault. 
		 * @param intValue
		 * @return the SourceModule.expr representing an application of intToEnumDefault
		 */
		public static final SourceModel.Expr intToEnumDefault(SourceModel.Expr intValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intToEnumDefault), intValue});
		}

		/**
		 * @see #intToEnumDefault(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param intValue
		 * @return the SourceModel.Expr representing an application of intToEnumDefault
		 */
		public static final SourceModel.Expr intToEnumDefault(int intValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intToEnumDefault), SourceModel.Expr.makeIntValue(intValue)});
		}

		/**
		 * Name binding for function: intToEnumDefault.
		 * @see #intToEnumDefault(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intToEnumDefault = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"intToEnumDefault");

		/**
		 * The <code>Cal.Core.Prelude.intToEnum</code> instance function for the <code>Cal.Core.Prelude.Short</code> type.
		 * @param intValue (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          The value to convert to a <code>Cal.Core.Prelude.Short</code>.  If <code>intValue</code> is out of <code>Cal.Core.Prelude.Short</code>'s range, an error will
		 * be raised.
		 * @return (CAL type: <code>Cal.Core.Prelude.Short</code>) 
		 *          a <code>Cal.Core.Prelude.Short</code> representing the same value as <code>intValue</code>.
		 */
		public static final SourceModel.Expr intToEnumShort(SourceModel.Expr intValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intToEnumShort), intValue});
		}

		/**
		 * @see #intToEnumShort(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param intValue
		 * @return the SourceModel.Expr representing an application of intToEnumShort
		 */
		public static final SourceModel.Expr intToEnumShort(int intValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intToEnumShort), SourceModel.Expr.makeIntValue(intValue)});
		}

		/**
		 * Name binding for function: intToEnumShort.
		 * @see #intToEnumShort(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intToEnumShort = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"intToEnumShort");

		/**
		 * Helper binding method for function: intToFloat. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of intToFloat
		 */
		public static final SourceModel.Expr intToFloat(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intToFloat), arg_1});
		}

		/**
		 * @see #intToFloat(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of intToFloat
		 */
		public static final SourceModel.Expr intToFloat(int arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intToFloat), SourceModel.Expr.makeIntValue(arg_1)});
		}

		/**
		 * Name binding for function: intToFloat.
		 * @see #intToFloat(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intToFloat = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "intToFloat");

		/**
		 * Helper binding method for function: intToInt. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of intToInt
		 */
		public static final SourceModel.Expr intToInt(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intToInt), arg_1});
		}

		/**
		 * @see #intToInt(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of intToInt
		 */
		public static final SourceModel.Expr intToInt(int arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intToInt), SourceModel.Expr.makeIntValue(arg_1)});
		}

		/**
		 * Name binding for function: intToInt.
		 * @see #intToInt(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intToInt = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "intToInt");

		/**
		 * Converts an <code>Cal.Core.Prelude.Int</code> value to the corresponding <code>Cal.Core.Prelude.Integer</code> value.
		 * @param intValue (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the <code>Cal.Core.Prelude.Int</code> value to be converted.
		 * @return (CAL type: <code>Cal.Core.Prelude.Integer</code>) 
		 *          the corresponding <code>Cal.Core.Prelude.Integer</code> value.
		 */
		public static final SourceModel.Expr intToInteger(SourceModel.Expr intValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intToInteger), intValue});
		}

		/**
		 * @see #intToInteger(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param intValue
		 * @return the SourceModel.Expr representing an application of intToInteger
		 */
		public static final SourceModel.Expr intToInteger(int intValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intToInteger), SourceModel.Expr.makeIntValue(intValue)});
		}

		/**
		 * Name binding for function: intToInteger.
		 * @see #intToInteger(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intToInteger = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "intToInteger");

		/**
		 * Helper binding method for function: intToLong. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of intToLong
		 */
		public static final SourceModel.Expr intToLong(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intToLong), arg_1});
		}

		/**
		 * @see #intToLong(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of intToLong
		 */
		public static final SourceModel.Expr intToLong(int arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intToLong), SourceModel.Expr.makeIntValue(arg_1)});
		}

		/**
		 * Name binding for function: intToLong.
		 * @see #intToLong(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intToLong = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "intToLong");

		/**
		 * Helper binding method for function: intToShort. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of intToShort
		 */
		public static final SourceModel.Expr intToShort(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intToShort), arg_1});
		}

		/**
		 * @see #intToShort(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of intToShort
		 */
		public static final SourceModel.Expr intToShort(int arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intToShort), SourceModel.Expr.makeIntValue(arg_1)});
		}

		/**
		 * Name binding for function: intToShort.
		 * @see #intToShort(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intToShort = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "intToShort");

		/**
		 * Helper binding method for function: integerToByte. 
		 * @param integer
		 * @return the SourceModule.expr representing an application of integerToByte
		 */
		public static final SourceModel.Expr integerToByte(SourceModel.Expr integer) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.integerToByte), integer});
		}

		/**
		 * Name binding for function: integerToByte.
		 * @see #integerToByte(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName integerToByte = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"integerToByte");

		/**
		 * Converts an <code>Cal.Core.Prelude.Integer</code> value to the equivalent <code>Cal.Core.Prelude.Decimal</code> value.
		 * The precision will be the number of digits in the <code>Cal.Core.Prelude.Integer</code> (ie, the
		 * scale will be 0).
		 * @param integerValue (CAL type: <code>Cal.Core.Prelude.Integer</code>)
		 *          An <code>Cal.Core.Prelude.Integer</code> value to convert to a <code>Cal.Core.Prelude.Decimal</code>
		 * @return (CAL type: <code>Cal.Core.Prelude.Decimal</code>) 
		 *          A <code>Cal.Core.Prelude.Decimal</code> value equivalent to <code>integerValue</code>.
		 */
		public static final SourceModel.Expr integerToDecimal(SourceModel.Expr integerValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.integerToDecimal), integerValue});
		}

		/**
		 * Name binding for function: integerToDecimal.
		 * @see #integerToDecimal(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName integerToDecimal = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"integerToDecimal");

		/**
		 * Helper binding method for function: integerToDouble. 
		 * @param integer
		 * @return the SourceModule.expr representing an application of integerToDouble
		 */
		public static final SourceModel.Expr integerToDouble(SourceModel.Expr integer) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.integerToDouble), integer});
		}

		/**
		 * Name binding for function: integerToDouble.
		 * @see #integerToDouble(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName integerToDouble = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"integerToDouble");

		/**
		 * Helper binding method for function: integerToFloat. 
		 * @param integer
		 * @return the SourceModule.expr representing an application of integerToFloat
		 */
		public static final SourceModel.Expr integerToFloat(SourceModel.Expr integer) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.integerToFloat), integer});
		}

		/**
		 * Name binding for function: integerToFloat.
		 * @see #integerToFloat(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName integerToFloat = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"integerToFloat");

		/**
		 * Converts an <code>Cal.Core.Prelude.Integer</code> value to the corresponding <code>Cal.Core.Prelude.Int</code> value.
		 * @param integerValue (CAL type: <code>Cal.Core.Prelude.Integer</code>)
		 *          the <code>Cal.Core.Prelude.Integer</code> value to be converted.
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the corresponding <code>Cal.Core.Prelude.Int</code> value.
		 */
		public static final SourceModel.Expr integerToInt(SourceModel.Expr integerValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.integerToInt), integerValue});
		}

		/**
		 * Name binding for function: integerToInt.
		 * @see #integerToInt(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName integerToInt = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "integerToInt");

		/**
		 * Helper binding method for function: integerToInteger. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of integerToInteger
		 */
		public static final SourceModel.Expr integerToInteger(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.integerToInteger), arg_1});
		}

		/**
		 * Name binding for function: integerToInteger.
		 * @see #integerToInteger(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName integerToInteger = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"integerToInteger");

		/**
		 * Converts an <code>Cal.Core.Prelude.Integer</code> value to the corresponding <code>Cal.Core.Prelude.Long</code> value.
		 * @param integerValue (CAL type: <code>Cal.Core.Prelude.Integer</code>)
		 *          the <code>Cal.Core.Prelude.Integer</code> value to be converted.
		 * @return (CAL type: <code>Cal.Core.Prelude.Long</code>) 
		 *          the corresponding <code>Cal.Core.Prelude.Long</code> value.
		 */
		public static final SourceModel.Expr integerToLong(SourceModel.Expr integerValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.integerToLong), integerValue});
		}

		/**
		 * Name binding for function: integerToLong.
		 * @see #integerToLong(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName integerToLong = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"integerToLong");

		/**
		 * Helper binding method for function: integerToShort. 
		 * @param integer
		 * @return the SourceModule.expr representing an application of integerToShort
		 */
		public static final SourceModel.Expr integerToShort(SourceModel.Expr integer) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.integerToShort), integer});
		}

		/**
		 * Name binding for function: integerToShort.
		 * @see #integerToShort(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName integerToShort = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"integerToShort");

		/**
		 * Helper binding method for function: integer_ONE. 
		 * @return the SourceModule.expr representing an application of integer_ONE
		 */
		public static final SourceModel.Expr integer_ONE() {
			return SourceModel.Expr.Var.make(Functions.integer_ONE);
		}

		/**
		 * Name binding for function: integer_ONE.
		 * @see #integer_ONE()
		 */
		public static final QualifiedName integer_ONE = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "integer_ONE");

		/**
		 * Helper binding method for function: integer_ZERO. 
		 * @return the SourceModule.expr representing an application of integer_ZERO
		 */
		public static final SourceModel.Expr integer_ZERO() {
			return SourceModel.Expr.Var.make(Functions.integer_ZERO);
		}

		/**
		 * Name binding for function: integer_ZERO.
		 * @see #integer_ZERO()
		 */
		public static final QualifiedName integer_ZERO = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "integer_ZERO");

		/**
		 * This function is for internal use to support derived <code>Cal.Core.Prelude.Inputable</code> and <code>Cal.Core.Prelude.Outputable</code> instances.
		 * <p>
		 * Returns the name of the data constructor corresponding to the <code>Cal.Core.Prelude.AlgebraicValue</code> expanded to a string.
		 * 
		 * @param algebraicValue (CAL type: <code>Cal.Core.Prelude.AlgebraicValue</code>)
		 *          the <code>Cal.Core.Prelude.AlgebraicValue</code>.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the name of the data constructor.
		 */
		public static final SourceModel.Expr internal_algebraicValue_getDataConstructorName(SourceModel.Expr algebraicValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.internal_algebraicValue_getDataConstructorName), algebraicValue});
		}

		/**
		 * Name binding for function: internal_algebraicValue_getDataConstructorName.
		 * @see #internal_algebraicValue_getDataConstructorName(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName internal_algebraicValue_getDataConstructorName = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"internal_algebraicValue_getDataConstructorName");

		/**
		 * This function is for internal use to support derived <code>Cal.Core.Prelude.Inputable</code> and <code>Cal.Core.Prelude.Outputable</code> instances.
		 * <p>
		 * Returns the ordinal of the data constructor corresponding to the <code>Cal.Core.Prelude.AlgebraicValue</code>.
		 * 
		 * @param algebraicValue (CAL type: <code>Cal.Core.Prelude.AlgebraicValue</code>)
		 *          the <code>Cal.Core.Prelude.AlgebraicValue</code>.
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the ordinal of the data constructor.
		 */
		public static final SourceModel.Expr internal_algebraicValue_getDataConstructorOrdinal(SourceModel.Expr algebraicValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.internal_algebraicValue_getDataConstructorOrdinal), algebraicValue});
		}

		/**
		 * Name binding for function: internal_algebraicValue_getDataConstructorOrdinal.
		 * @see #internal_algebraicValue_getDataConstructorOrdinal(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName internal_algebraicValue_getDataConstructorOrdinal = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"internal_algebraicValue_getDataConstructorOrdinal");

		/**
		 * This function is for internal use to support derived <code>Cal.Core.Prelude.Inputable</code> and <code>Cal.Core.Prelude.Outputable</code> instances.
		 * <p>
		 * Returns the number of arguments that the data constructor represented by the <code>Cal.Core.Prelude.AlgebraicValue</code> is holding.
		 * 
		 * @param algebraicValue (CAL type: <code>Cal.Core.Prelude.AlgebraicValue</code>)
		 *          the <code>Cal.Core.Prelude.AlgebraicValue</code>.
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the number of arguments that the data constructor is holding.
		 */
		public static final SourceModel.Expr internal_algebraicValue_getNArguments(SourceModel.Expr algebraicValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.internal_algebraicValue_getNArguments), algebraicValue});
		}

		/**
		 * Name binding for function: internal_algebraicValue_getNArguments.
		 * @see #internal_algebraicValue_getNArguments(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName internal_algebraicValue_getNArguments = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"internal_algebraicValue_getNArguments");

		/**
		 * This function is for internal use to support derived <code>Cal.Core.Prelude.Inputable</code> and <code>Cal.Core.Prelude.Outputable</code> instances.
		 * <p>
		 * Returns the argument value corresponding to the argNth argument held by the data constructor represented by the
		 * <code>Cal.Core.Prelude.AlgebraicValue</code>.
		 * 
		 * @param algebraicValue (CAL type: <code>Cal.Core.Prelude.AlgebraicValue</code>)
		 *          the <code>Cal.Core.Prelude.AlgebraicValue</code>.
		 * @param argN (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the zero-based index to an argument held by the data constructor represented by the <code>Cal.Core.Prelude.AlgebraicValue</code>.
		 * @return (CAL type: <code>Cal.Core.Prelude.JObject</code>) 
		 *          the argument value corresponding to the argNth argument.
		 */
		public static final SourceModel.Expr internal_algebraicValue_getNthArgument(SourceModel.Expr algebraicValue, SourceModel.Expr argN) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.internal_algebraicValue_getNthArgument), algebraicValue, argN});
		}

		/**
		 * @see #internal_algebraicValue_getNthArgument(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param algebraicValue
		 * @param argN
		 * @return the SourceModel.Expr representing an application of internal_algebraicValue_getNthArgument
		 */
		public static final SourceModel.Expr internal_algebraicValue_getNthArgument(SourceModel.Expr algebraicValue, int argN) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.internal_algebraicValue_getNthArgument), algebraicValue, SourceModel.Expr.makeIntValue(argN)});
		}

		/**
		 * Name binding for function: internal_algebraicValue_getNthArgument.
		 * @see #internal_algebraicValue_getNthArgument(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName internal_algebraicValue_getNthArgument = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"internal_algebraicValue_getNthArgument");

		/**
		 * This function is for internal use to support derived <code>Cal.Core.Prelude.Inputable</code> and <code>Cal.Core.Prelude.Outputable</code> instances.
		 * <p>
		 * Constructs a general <code>Cal.Core.Prelude.AlgebraicValue</code>.
		 * 
		 * @param dataConstructorName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the fully-qualified name of the data constructor (that constructs this value
		 * (eg <code>"Cal.Core.Prelude.Just"</code>).
		 * @param dataConstructorOrdinal (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the ordinal of the data constructor that constructs this value.
		 * @param argumentValues (CAL type: <code>Cal.Core.Prelude.JList</code>)
		 *          a <code>Cal.Core.Prelude.JList</code> of arguments to the data constructor.
		 * @return (CAL type: <code>Cal.Core.Prelude.AlgebraicValue</code>) 
		 *          an <code>Cal.Core.Prelude.AlgebraicValue</code> that represents the value returned by applying the specified arguments to the
		 * specified data constructor.
		 */
		public static final SourceModel.Expr internal_algebraicValue_new(SourceModel.Expr dataConstructorName, SourceModel.Expr dataConstructorOrdinal, SourceModel.Expr argumentValues) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.internal_algebraicValue_new), dataConstructorName, dataConstructorOrdinal, argumentValues});
		}

		/**
		 * @see #internal_algebraicValue_new(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param dataConstructorName
		 * @param dataConstructorOrdinal
		 * @param argumentValues
		 * @return the SourceModel.Expr representing an application of internal_algebraicValue_new
		 */
		public static final SourceModel.Expr internal_algebraicValue_new(java.lang.String dataConstructorName, int dataConstructorOrdinal, SourceModel.Expr argumentValues) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.internal_algebraicValue_new), SourceModel.Expr.makeStringValue(dataConstructorName), SourceModel.Expr.makeIntValue(dataConstructorOrdinal), argumentValues});
		}

		/**
		 * Name binding for function: internal_algebraicValue_new.
		 * @see #internal_algebraicValue_new(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName internal_algebraicValue_new = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"internal_algebraicValue_new");

		/**
		 * This function is for internal use to support derived <code>Cal.Core.Prelude.Inputable</code> and <code>Cal.Core.Prelude.Outputable</code> instances.
		 * <p>
		 * Constructs an <code>Cal.Core.Prelude.AlgebraicValue</code> representing a data constructor with no arguments.
		 * 
		 * @param dataConstructorName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the fully-qualified name of the data constructor (that constructs this value
		 * (eg <code>"Cal.Core.Prelude.Just"</code>).
		 * @param dataConstructorOrdinal (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the ordinal of the data constructor that constructs this value.
		 * @return (CAL type: <code>Cal.Core.Prelude.AlgebraicValue</code>) 
		 *          an <code>Cal.Core.Prelude.AlgebraicValue</code> that represents the value returned by the specified data constructor.
		 */
		public static final SourceModel.Expr internal_algebraicValue_new0(SourceModel.Expr dataConstructorName, SourceModel.Expr dataConstructorOrdinal) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.internal_algebraicValue_new0), dataConstructorName, dataConstructorOrdinal});
		}

		/**
		 * @see #internal_algebraicValue_new0(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param dataConstructorName
		 * @param dataConstructorOrdinal
		 * @return the SourceModel.Expr representing an application of internal_algebraicValue_new0
		 */
		public static final SourceModel.Expr internal_algebraicValue_new0(java.lang.String dataConstructorName, int dataConstructorOrdinal) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.internal_algebraicValue_new0), SourceModel.Expr.makeStringValue(dataConstructorName), SourceModel.Expr.makeIntValue(dataConstructorOrdinal)});
		}

		/**
		 * Name binding for function: internal_algebraicValue_new0.
		 * @see #internal_algebraicValue_new0(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName internal_algebraicValue_new0 = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"internal_algebraicValue_new0");

		/**
		 * This function is for internal use to support derived <code>Cal.Core.Prelude.Inputable</code> and <code>Cal.Core.Prelude.Outputable</code> instances.
		 * <p>
		 * Constructs an <code>Cal.Core.Prelude.AlgebraicValue</code> representing a data constructor with a single argument.
		 * 
		 * @param dataConstructorName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the fully-qualified name of the data constructor (that constructs this value (eg <code>"Cal.Core.Prelude.Just"</code>).
		 * @param dataConstructorOrdinal (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the ordinal of the data constructor that constructs this value.
		 * @param argumentValue (CAL type: <code>Cal.Core.Prelude.JObject</code>)
		 *          the single argument to the data constructor.
		 * @return (CAL type: <code>Cal.Core.Prelude.AlgebraicValue</code>) 
		 *          an <code>Cal.Core.Prelude.AlgebraicValue</code> that represents the value returned by applying the specified argument to the
		 * specified data constructor.
		 */
		public static final SourceModel.Expr internal_algebraicValue_new1(SourceModel.Expr dataConstructorName, SourceModel.Expr dataConstructorOrdinal, SourceModel.Expr argumentValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.internal_algebraicValue_new1), dataConstructorName, dataConstructorOrdinal, argumentValue});
		}

		/**
		 * @see #internal_algebraicValue_new1(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param dataConstructorName
		 * @param dataConstructorOrdinal
		 * @param argumentValue
		 * @return the SourceModel.Expr representing an application of internal_algebraicValue_new1
		 */
		public static final SourceModel.Expr internal_algebraicValue_new1(java.lang.String dataConstructorName, int dataConstructorOrdinal, SourceModel.Expr argumentValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.internal_algebraicValue_new1), SourceModel.Expr.makeStringValue(dataConstructorName), SourceModel.Expr.makeIntValue(dataConstructorOrdinal), argumentValue});
		}

		/**
		 * Name binding for function: internal_algebraicValue_new1.
		 * @see #internal_algebraicValue_new1(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName internal_algebraicValue_new1 = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"internal_algebraicValue_new1");

		/**
		 * This function takes an element and a list and "intersperses" that element
		 * between the elements of the list.
		 * <p>
		 * e.g. <code>intersperse 0 [1, 2, 3] = [1, 0, 2, 0, 3]</code>
		 * <p>
		 * This function is defined in the <code>Cal.Core.Prelude</code> module for implementation purposes only. 
		 * The public api version is <code>Cal.Collections.List.intersperse</code>.
		 * 
		 * @param separator (CAL type: <code>a</code>)
		 *          the element to be interspersed between the elements of the list.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list whose elements are to be interspersed by the separator.
		 * @return (CAL type: <code>[a]</code>) 
		 *          the interspersed list.
		 */
		public static final SourceModel.Expr intersperse(SourceModel.Expr separator, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intersperse), separator, list});
		}

		/**
		 * Name binding for function: intersperse.
		 * @see #intersperse(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intersperse = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "intersperse");

		/**
		 * Helper binding method for function: isEmptyList. 
		 * @param list
		 * @return the SourceModule.expr representing an application of isEmptyList
		 */
		public static final SourceModel.Expr isEmptyList(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isEmptyList), list});
		}

		/**
		 * Name binding for function: isEmptyList.
		 * @see #isEmptyList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isEmptyList = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "isEmptyList");

		/**
		 * Helper binding method for function: isEmptyOrdering. 
		 * @param ordering
		 * @return the SourceModule.expr representing an application of isEmptyOrdering
		 */
		public static final SourceModel.Expr isEmptyOrdering(SourceModel.Expr ordering) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isEmptyOrdering), ordering});
		}

		/**
		 * Name binding for function: isEmptyOrdering.
		 * @see #isEmptyOrdering(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isEmptyOrdering = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"isEmptyOrdering");

		/**
		 * Helper binding method for function: isEmptyString. 
		 * @param stringValue
		 * @return the SourceModule.expr representing an application of isEmptyString
		 */
		public static final SourceModel.Expr isEmptyString(SourceModel.Expr stringValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isEmptyString), stringValue});
		}

		/**
		 * @see #isEmptyString(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param stringValue
		 * @return the SourceModel.Expr representing an application of isEmptyString
		 */
		public static final SourceModel.Expr isEmptyString(java.lang.String stringValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isEmptyString), SourceModel.Expr.makeStringValue(stringValue)});
		}

		/**
		 * Name binding for function: isEmptyString.
		 * @see #isEmptyString(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isEmptyString = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"isEmptyString");

		/**
		 * 
		 * @param typeRep (CAL type: <code>Cal.Core.Prelude.TypeRep</code>)
		 *          runtime type representation of a value, typically obtained by calling <code>Cal.Core.Prelude.typeOf</code> on a value.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the type of the value is a foreign type, that corresponds to a foreign reference
		 * type i.e. not a primitive type such as "int".
		 */
		public static final SourceModel.Expr isForeignReferenceType(SourceModel.Expr typeRep) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isForeignReferenceType), typeRep});
		}

		/**
		 * Name binding for function: isForeignReferenceType.
		 * @see #isForeignReferenceType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isForeignReferenceType = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"isForeignReferenceType");

		/**
		 * Helper binding method for function: isForeignReferenceTypeExamples. 
		 * @return the SourceModule.expr representing an application of isForeignReferenceTypeExamples
		 */
		public static final SourceModel.Expr isForeignReferenceTypeExamples() {
			return 
				SourceModel.Expr.Var.make(
					Functions.isForeignReferenceTypeExamples);
		}

		/**
		 * Name binding for function: isForeignReferenceTypeExamples.
		 * @see #isForeignReferenceTypeExamples()
		 */
		public static final QualifiedName isForeignReferenceTypeExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"isForeignReferenceTypeExamples");

		/**
		 * 
		 * @param typeRep (CAL type: <code>Cal.Core.Prelude.TypeRep</code>)
		 *          runtime type representation of a value, typically obtained by calling <code>Cal.Core.Prelude.typeOf</code> on a value.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the type was declared with a foreign data declaration.
		 */
		public static final SourceModel.Expr isForeignType(SourceModel.Expr typeRep) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isForeignType), typeRep});
		}

		/**
		 * Name binding for function: isForeignType.
		 * @see #isForeignType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isForeignType = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"isForeignType");

		/**
		 * Helper binding method for function: isFunctionTypeExamples. 
		 * @return the SourceModule.expr representing an application of isFunctionTypeExamples
		 */
		public static final SourceModel.Expr isFunctionTypeExamples() {
			return SourceModel.Expr.Var.make(Functions.isFunctionTypeExamples);
		}

		/**
		 * Name binding for function: isFunctionTypeExamples.
		 * @see #isFunctionTypeExamples()
		 */
		public static final QualifiedName isFunctionTypeExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"isFunctionTypeExamples");

		/**
		 * Helper binding method for function: isJCollection. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of isJCollection
		 */
		public static final SourceModel.Expr isJCollection(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isJCollection), arg_1});
		}

		/**
		 * Name binding for function: isJCollection.
		 * @see #isJCollection(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isJCollection = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"isJCollection");

		/**
		 * Helper binding method for function: isJEnumeration. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of isJEnumeration
		 */
		public static final SourceModel.Expr isJEnumeration(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isJEnumeration), arg_1});
		}

		/**
		 * Name binding for function: isJEnumeration.
		 * @see #isJEnumeration(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isJEnumeration = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"isJEnumeration");

		/**
		 * Helper binding method for function: isJIterator. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of isJIterator
		 */
		public static final SourceModel.Expr isJIterator(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isJIterator), arg_1});
		}

		/**
		 * Name binding for function: isJIterator.
		 * @see #isJIterator(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isJIterator = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "isJIterator");

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
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "isJList");

		/**
		 * Helper binding method for function: isJMap. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of isJMap
		 */
		public static final SourceModel.Expr isJMap(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isJMap), arg_1});
		}

		/**
		 * Name binding for function: isJMap.
		 * @see #isJMap(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isJMap = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "isJMap");

		/**
		 * Helper binding method for function: isJavaArray. 
		 * @param object
		 * @return the SourceModule.expr representing an application of isJavaArray
		 */
		public static final SourceModel.Expr isJavaArray(SourceModel.Expr object) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isJavaArray), object});
		}

		/**
		 * Name binding for function: isJavaArray.
		 * @see #isJavaArray(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isJavaArray = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "isJavaArray");

		/**
		 * Helper binding method for function: isListTypeExamples. 
		 * @return the SourceModule.expr representing an application of isListTypeExamples
		 */
		public static final SourceModel.Expr isListTypeExamples() {
			return SourceModel.Expr.Var.make(Functions.isListTypeExamples);
		}

		/**
		 * Name binding for function: isListTypeExamples.
		 * @see #isListTypeExamples()
		 */
		public static final QualifiedName isListTypeExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"isListTypeExamples");

		/**
		 * Helper binding method for function: isMaybeTypeExamples. 
		 * @return the SourceModule.expr representing an application of isMaybeTypeExamples
		 */
		public static final SourceModel.Expr isMaybeTypeExamples() {
			return SourceModel.Expr.Var.make(Functions.isMaybeTypeExamples);
		}

		/**
		 * Name binding for function: isMaybeTypeExamples.
		 * @see #isMaybeTypeExamples()
		 */
		public static final QualifiedName isMaybeTypeExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"isMaybeTypeExamples");

		/**
		 * Helper binding method for function: isPreludeNumTypeExamples. 
		 * @return the SourceModule.expr representing an application of isPreludeNumTypeExamples
		 */
		public static final SourceModel.Expr isPreludeNumTypeExamples() {
			return SourceModel.Expr.Var.make(Functions.isPreludeNumTypeExamples);
		}

		/**
		 * Name binding for function: isPreludeNumTypeExamples.
		 * @see #isPreludeNumTypeExamples()
		 */
		public static final QualifiedName isPreludeNumTypeExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"isPreludeNumTypeExamples");

		/**
		 * Helper binding method for function: isRecordTypeExamples. 
		 * @return the SourceModule.expr representing an application of isRecordTypeExamples
		 */
		public static final SourceModel.Expr isRecordTypeExamples() {
			return SourceModel.Expr.Var.make(Functions.isRecordTypeExamples);
		}

		/**
		 * Name binding for function: isRecordTypeExamples.
		 * @see #isRecordTypeExamples()
		 */
		public static final QualifiedName isRecordTypeExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"isRecordTypeExamples");

		/**
		 * <code>iterate f x</code> returns the infinite list <code>[x, f x, f(f x), f(f(f x)), ...]</code>.
		 * <p>
		 * This function is defined in the <code>Cal.Core.Prelude</code> module for implementation purposes only. 
		 * The public api version is <code>Cal.Collections.List.iterate</code>.
		 * 
		 * @param iterationFunction (CAL type: <code>a -> a</code>)
		 *          the iteration function.
		 * @param initialValue (CAL type: <code>a</code>)
		 *          the initial value.
		 * @return (CAL type: <code>[a]</code>) 
		 *          the infinite list
		 * <code>[initialValue, iterationFunction initialValue, iterationFunction(iterationFunction initialValue), ...]</code>
		 */
		public static final SourceModel.Expr iterate(SourceModel.Expr iterationFunction, SourceModel.Expr initialValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.iterate), iterationFunction, initialValue});
		}

		/**
		 * Name binding for function: iterate.
		 * @see #iterate(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName iterate = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "iterate");

		/**
		 * Helper binding method for function: iterator_hasNext. 
		 * @param jIterator
		 * @return the SourceModule.expr representing an application of iterator_hasNext
		 */
		public static final SourceModel.Expr iterator_hasNext(SourceModel.Expr jIterator) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.iterator_hasNext), jIterator});
		}

		/**
		 * Name binding for function: iterator_hasNext.
		 * @see #iterator_hasNext(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName iterator_hasNext = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"iterator_hasNext");

		/**
		 * Helper binding method for function: iterator_next. 
		 * @param jIterator
		 * @return the SourceModule.expr representing an application of iterator_next
		 */
		public static final SourceModel.Expr iterator_next(SourceModel.Expr jIterator) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.iterator_next), jIterator});
		}

		/**
		 * Name binding for function: iterator_next.
		 * @see #iterator_next(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName iterator_next = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"iterator_next");

		/**
		 * Helper binding method for function: jArrayList_new. 
		 * @return the SourceModule.expr representing an application of jArrayList_new
		 */
		public static final SourceModel.Expr jArrayList_new() {
			return SourceModel.Expr.Var.make(Functions.jArrayList_new);
		}

		/**
		 * Name binding for function: jArrayList_new.
		 * @see #jArrayList_new()
		 */
		public static final QualifiedName jArrayList_new = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"jArrayList_new");

		/**
		 * Helper binding method for function: jComparableToJObject. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of jComparableToJObject
		 */
		public static final SourceModel.Expr jComparableToJObject(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jComparableToJObject), arg_1});
		}

		/**
		 * Name binding for function: jComparableToJObject.
		 * @see #jComparableToJObject(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jComparableToJObject = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"jComparableToJObject");

		/**
		 * Helper binding method for function: jCompareComparable. 
		 * @param x
		 * @param y
		 * @return the SourceModule.expr representing an application of jCompareComparable
		 */
		public static final SourceModel.Expr jCompareComparable(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jCompareComparable), x, y});
		}

		/**
		 * Name binding for function: jCompareComparable.
		 * @see #jCompareComparable(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jCompareComparable = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"jCompareComparable");

		/**
		 * Helper binding method for function: jCompareComparableHelper. 
		 * @param jComparable
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of jCompareComparableHelper
		 */
		public static final SourceModel.Expr jCompareComparableHelper(SourceModel.Expr jComparable, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jCompareComparableHelper), jComparable, arg_2});
		}

		/**
		 * Name binding for function: jCompareComparableHelper.
		 * @see #jCompareComparableHelper(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jCompareComparableHelper = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"jCompareComparableHelper");

		/**
		 * Helper binding method for function: jCompareDouble. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of jCompareDouble
		 */
		public static final SourceModel.Expr jCompareDouble(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jCompareDouble), arg_1, arg_2});
		}

		/**
		 * @see #jCompareDouble(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of jCompareDouble
		 */
		public static final SourceModel.Expr jCompareDouble(double arg_1, double arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jCompareDouble), SourceModel.Expr.makeDoubleValue(arg_1), SourceModel.Expr.makeDoubleValue(arg_2)});
		}

		/**
		 * Name binding for function: jCompareDouble.
		 * @see #jCompareDouble(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jCompareDouble = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"jCompareDouble");

		/**
		 * Helper binding method for function: jCompareFloat. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of jCompareFloat
		 */
		public static final SourceModel.Expr jCompareFloat(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jCompareFloat), arg_1, arg_2});
		}

		/**
		 * @see #jCompareFloat(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of jCompareFloat
		 */
		public static final SourceModel.Expr jCompareFloat(float arg_1, float arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jCompareFloat), SourceModel.Expr.makeFloatValue(arg_1), SourceModel.Expr.makeFloatValue(arg_2)});
		}

		/**
		 * Name binding for function: jCompareFloat.
		 * @see #jCompareFloat(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jCompareFloat = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"jCompareFloat");

		/**
		 * Helper binding method for function: jCompareInteger. 
		 * @param integer
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of jCompareInteger
		 */
		public static final SourceModel.Expr jCompareInteger(SourceModel.Expr integer, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jCompareInteger), integer, arg_2});
		}

		/**
		 * Name binding for function: jCompareInteger.
		 * @see #jCompareInteger(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jCompareInteger = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"jCompareInteger");

		/**
		 * Returns -1, 0, or 1.
		 * @param decimal (CAL type: <code>Cal.Core.Prelude.Decimal</code>)
		 * @param arg_2 (CAL type: <code>Cal.Core.Prelude.Decimal</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 */
		public static final SourceModel.Expr jCompareToDecimal(SourceModel.Expr decimal, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jCompareToDecimal), decimal, arg_2});
		}

		/**
		 * Name binding for function: jCompareToDecimal.
		 * @see #jCompareToDecimal(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jCompareToDecimal = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"jCompareToDecimal");

		/**
		 * Helper binding method for function: jDivideDecimal. 
		 * @param decimal
		 * @param arg_2
		 * @param arg_3
		 * @return the SourceModule.expr representing an application of jDivideDecimal
		 */
		public static final SourceModel.Expr jDivideDecimal(SourceModel.Expr decimal, SourceModel.Expr arg_2, SourceModel.Expr arg_3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jDivideDecimal), decimal, arg_2, arg_3});
		}

		/**
		 * @see #jDivideDecimal(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param decimal
		 * @param arg_2
		 * @param arg_3
		 * @return the SourceModel.Expr representing an application of jDivideDecimal
		 */
		public static final SourceModel.Expr jDivideDecimal(SourceModel.Expr decimal, SourceModel.Expr arg_2, int arg_3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jDivideDecimal), decimal, arg_2, SourceModel.Expr.makeIntValue(arg_3)});
		}

		/**
		 * Name binding for function: jDivideDecimal.
		 * @see #jDivideDecimal(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jDivideDecimal = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"jDivideDecimal");

		/**
		 * Helper binding method for function: jEitherToEither. 
		 * @param javaEitherValue
		 * @return the SourceModule.expr representing an application of jEitherToEither
		 */
		public static final SourceModel.Expr jEitherToEither(SourceModel.Expr javaEitherValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jEitherToEither), javaEitherValue});
		}

		/**
		 * Name binding for function: jEitherToEither.
		 * @see #jEitherToEither(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jEitherToEither = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"jEitherToEither");

		/**
		 * Helper binding method for function: jEnumerationToJIterator. 
		 * @param e
		 * @return the SourceModule.expr representing an application of jEnumerationToJIterator
		 */
		public static final SourceModel.Expr jEnumerationToJIterator(SourceModel.Expr e) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jEnumerationToJIterator), e});
		}

		/**
		 * Name binding for function: jEnumerationToJIterator.
		 * @see #jEnumerationToJIterator(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jEnumerationToJIterator = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"jEnumerationToJIterator");

		/**
		 * Helper binding method for function: jList0. 
		 * @return the SourceModule.expr representing an application of jList0
		 */
		public static final SourceModel.Expr jList0() {
			return SourceModel.Expr.Var.make(Functions.jList0);
		}

		/**
		 * Name binding for function: jList0.
		 * @see #jList0()
		 */
		public static final QualifiedName jList0 = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "jList0");

		/**
		 * Helper binding method for function: jList1. 
		 * @param e0
		 * @return the SourceModule.expr representing an application of jList1
		 */
		public static final SourceModel.Expr jList1(SourceModel.Expr e0) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jList1), e0});
		}

		/**
		 * Name binding for function: jList1.
		 * @see #jList1(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jList1 = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "jList1");

		/**
		 * Helper binding method for function: jList2. 
		 * @param e0
		 * @param e1
		 * @return the SourceModule.expr representing an application of jList2
		 */
		public static final SourceModel.Expr jList2(SourceModel.Expr e0, SourceModel.Expr e1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jList2), e0, e1});
		}

		/**
		 * Name binding for function: jList2.
		 * @see #jList2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jList2 = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "jList2");

		/**
		 * Helper binding method for function: jList3. 
		 * @param e0
		 * @param e1
		 * @param e2
		 * @return the SourceModule.expr representing an application of jList3
		 */
		public static final SourceModel.Expr jList3(SourceModel.Expr e0, SourceModel.Expr e1, SourceModel.Expr e2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jList3), e0, e1, e2});
		}

		/**
		 * Name binding for function: jList3.
		 * @see #jList3(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jList3 = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "jList3");

		/**
		 * Helper binding method for function: jList4. 
		 * @param e0
		 * @param e1
		 * @param e2
		 * @param e3
		 * @return the SourceModule.expr representing an application of jList4
		 */
		public static final SourceModel.Expr jList4(SourceModel.Expr e0, SourceModel.Expr e1, SourceModel.Expr e2, SourceModel.Expr e3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jList4), e0, e1, e2, e3});
		}

		/**
		 * Name binding for function: jList4.
		 * @see #jList4(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jList4 = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "jList4");

		/**
		 * Helper binding method for function: jList5. 
		 * @param e0
		 * @param e1
		 * @param e2
		 * @param e3
		 * @param e4
		 * @return the SourceModule.expr representing an application of jList5
		 */
		public static final SourceModel.Expr jList5(SourceModel.Expr e0, SourceModel.Expr e1, SourceModel.Expr e2, SourceModel.Expr e3, SourceModel.Expr e4) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jList5), e0, e1, e2, e3, e4});
		}

		/**
		 * Name binding for function: jList5.
		 * @see #jList5(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jList5 = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "jList5");

		/**
		 * Helper binding method for function: jList6. 
		 * @param e0
		 * @param e1
		 * @param e2
		 * @param e3
		 * @param e4
		 * @param e5
		 * @return the SourceModule.expr representing an application of jList6
		 */
		public static final SourceModel.Expr jList6(SourceModel.Expr e0, SourceModel.Expr e1, SourceModel.Expr e2, SourceModel.Expr e3, SourceModel.Expr e4, SourceModel.Expr e5) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jList6), e0, e1, e2, e3, e4, e5});
		}

		/**
		 * Name binding for function: jList6.
		 * @see #jList6(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jList6 = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "jList6");

		/**
		 * Helper binding method for function: jList7. 
		 * @param e0
		 * @param e1
		 * @param e2
		 * @param e3
		 * @param e4
		 * @param e5
		 * @param e6
		 * @return the SourceModule.expr representing an application of jList7
		 */
		public static final SourceModel.Expr jList7(SourceModel.Expr e0, SourceModel.Expr e1, SourceModel.Expr e2, SourceModel.Expr e3, SourceModel.Expr e4, SourceModel.Expr e5, SourceModel.Expr e6) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jList7), e0, e1, e2, e3, e4, e5, e6});
		}

		/**
		 * Name binding for function: jList7.
		 * @see #jList7(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jList7 = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "jList7");

		/**
		 * Helper binding method for function: jList_add. 
		 * @param jList
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of jList_add
		 */
		public static final SourceModel.Expr jList_add(SourceModel.Expr jList, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jList_add), jList, arg_2});
		}

		/**
		 * Name binding for function: jList_add.
		 * @see #jList_add(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jList_add = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "jList_add");

		/**
		 * Helper binding method for function: jMaybeToMaybe. 
		 * @param javaMaybeValue
		 * @return the SourceModule.expr representing an application of jMaybeToMaybe
		 */
		public static final SourceModel.Expr jMaybeToMaybe(SourceModel.Expr javaMaybeValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jMaybeToMaybe), javaMaybeValue});
		}

		/**
		 * Name binding for function: jMaybeToMaybe.
		 * @see #jMaybeToMaybe(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jMaybeToMaybe = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"jMaybeToMaybe");

		/**
		 * Helper binding method for function: jObjectToJList. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of jObjectToJList
		 */
		public static final SourceModel.Expr jObjectToJList(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jObjectToJList), arg_1});
		}

		/**
		 * Name binding for function: jObjectToJList.
		 * @see #jObjectToJList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jObjectToJList = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"jObjectToJList");

		/**
		 * Helper binding method for function: jObjectToJMap. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of jObjectToJMap
		 */
		public static final SourceModel.Expr jObjectToJMap(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jObjectToJMap), arg_1});
		}

		/**
		 * Name binding for function: jObjectToJMap.
		 * @see #jObjectToJMap(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jObjectToJMap = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"jObjectToJMap");

		/**
		 * Helper binding method for function: jOrderingToOrdering. 
		 * @param javaOrderingValue
		 * @return the SourceModule.expr representing an application of jOrderingToOrdering
		 */
		public static final SourceModel.Expr jOrderingToOrdering(SourceModel.Expr javaOrderingValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jOrderingToOrdering), javaOrderingValue});
		}

		/**
		 * Name binding for function: jOrderingToOrdering.
		 * @see #jOrderingToOrdering(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jOrderingToOrdering = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"jOrderingToOrdering");

		/**
		 * Helper binding method for function: jROUND_HALF_UP. 
		 * @return the SourceModule.expr representing an application of jROUND_HALF_UP
		 */
		public static final SourceModel.Expr jROUND_HALF_UP() {
			return SourceModel.Expr.Var.make(Functions.jROUND_HALF_UP);
		}

		/**
		 * Name binding for function: jROUND_HALF_UP.
		 * @see #jROUND_HALF_UP()
		 */
		public static final QualifiedName jROUND_HALF_UP = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"jROUND_HALF_UP");

		/**
		 * Helper binding method for function: jStringBuilderToString. 
		 * @param jStringBuilder
		 * @return the SourceModule.expr representing an application of jStringBuilderToString
		 */
		public static final SourceModel.Expr jStringBuilderToString(SourceModel.Expr jStringBuilder) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jStringBuilderToString), jStringBuilder});
		}

		/**
		 * Name binding for function: jStringBuilderToString.
		 * @see #jStringBuilderToString(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jStringBuilderToString = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"jStringBuilderToString");

		/**
		 * Helper binding method for function: jStringBuilder_append. 
		 * @param jStringBuilder
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of jStringBuilder_append
		 */
		public static final SourceModel.Expr jStringBuilder_append(SourceModel.Expr jStringBuilder, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jStringBuilder_append), jStringBuilder, arg_2});
		}

		/**
		 * @see #jStringBuilder_append(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param jStringBuilder
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of jStringBuilder_append
		 */
		public static final SourceModel.Expr jStringBuilder_append(SourceModel.Expr jStringBuilder, char arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jStringBuilder_append), jStringBuilder, SourceModel.Expr.makeCharValue(arg_2)});
		}

		/**
		 * Name binding for function: jStringBuilder_append.
		 * @see #jStringBuilder_append(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jStringBuilder_append = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"jStringBuilder_append");

		/**
		 * Helper binding method for function: jStringBuilder_append2. 
		 * @param jStringBuilder
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of jStringBuilder_append2
		 */
		public static final SourceModel.Expr jStringBuilder_append2(SourceModel.Expr jStringBuilder, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jStringBuilder_append2), jStringBuilder, arg_2});
		}

		/**
		 * @see #jStringBuilder_append2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param jStringBuilder
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of jStringBuilder_append2
		 */
		public static final SourceModel.Expr jStringBuilder_append2(SourceModel.Expr jStringBuilder, java.lang.String arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jStringBuilder_append2), jStringBuilder, SourceModel.Expr.makeStringValue(arg_2)});
		}

		/**
		 * Name binding for function: jStringBuilder_append2.
		 * @see #jStringBuilder_append2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jStringBuilder_append2 = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"jStringBuilder_append2");

		/**
		 * Helper binding method for function: jStringBuilder_new. 
		 * @return the SourceModule.expr representing an application of jStringBuilder_new
		 */
		public static final SourceModel.Expr jStringBuilder_new() {
			return SourceModel.Expr.Var.make(Functions.jStringBuilder_new);
		}

		/**
		 * Name binding for function: jStringBuilder_new.
		 * @see #jStringBuilder_new()
		 */
		public static final QualifiedName jStringBuilder_new = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"jStringBuilder_new");

		/**
		 * Helper binding method for function: jStringBuilder_toString. 
		 * @param jStringBuilder
		 * @return the SourceModule.expr representing an application of jStringBuilder_toString
		 */
		public static final SourceModel.Expr jStringBuilder_toString(SourceModel.Expr jStringBuilder) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jStringBuilder_toString), jStringBuilder});
		}

		/**
		 * Name binding for function: jStringBuilder_toString.
		 * @see #jStringBuilder_toString(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jStringBuilder_toString = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"jStringBuilder_toString");

		/**
		 * Helper binding method for function: javaArrayToJIterator. 
		 * @param array
		 * @return the SourceModule.expr representing an application of javaArrayToJIterator
		 */
		public static final SourceModel.Expr javaArrayToJIterator(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.javaArrayToJIterator), array});
		}

		/**
		 * Name binding for function: javaArrayToJIterator.
		 * @see #javaArrayToJIterator(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName javaArrayToJIterator = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"javaArrayToJIterator");

		/**
		 * Returns the length of the specified list. This function is O(n) in time, where n is the length of the list.
		 * <p>
		 * This function is defined in the <code>Cal.Core.Prelude</code> module for implementation purposes only. 
		 * The public api version is <code>Cal.Collections.List.length</code>.
		 * 
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list whose length is to be returned.
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the length of the list.
		 */
		public static final SourceModel.Expr length(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.length), list});
		}

		/**
		 * Name binding for function: length.
		 * @see #length(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName length = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "length");

		/**
		 * Returns the length of the string. The length is equal to the number of characters contained in the string.
		 * <p>
		 * This function is defined in the <code>Cal.Core.Prelude</code> module for implementation purposes only. 
		 * The public api version is <code>Cal.Core.String.length</code>.
		 * 
		 * @param string (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the string whose length is to be returned.
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the length of the string
		 */
		public static final SourceModel.Expr lengthString(SourceModel.Expr string) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lengthString), string});
		}

		/**
		 * @see #lengthString(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param string
		 * @return the SourceModel.Expr representing an application of lengthString
		 */
		public static final SourceModel.Expr lengthString(java.lang.String string) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lengthString), SourceModel.Expr.makeStringValue(string)});
		}

		/**
		 * Name binding for function: lengthString.
		 * @see #lengthString(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName lengthString = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "lengthString");

		/**
		 * Helper binding method for function: lengthTypeReps. 
		 * @param array
		 * @return the SourceModule.expr representing an application of lengthTypeReps
		 */
		public static final SourceModel.Expr lengthTypeReps(SourceModel.Expr array) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lengthTypeReps), array});
		}

		/**
		 * Name binding for function: lengthTypeReps.
		 * @see #lengthTypeReps(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName lengthTypeReps = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"lengthTypeReps");

		/**
		 * Helper binding method for function: lessThanByte. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of lessThanByte
		 */
		public static final SourceModel.Expr lessThanByte(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lessThanByte), arg_1, arg_2});
		}

		/**
		 * @see #lessThanByte(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of lessThanByte
		 */
		public static final SourceModel.Expr lessThanByte(byte arg_1, byte arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lessThanByte), SourceModel.Expr.makeByteValue(arg_1), SourceModel.Expr.makeByteValue(arg_2)});
		}

		/**
		 * Name binding for function: lessThanByte.
		 * @see #lessThanByte(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName lessThanByte = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "lessThanByte");

		/**
		 * Helper binding method for function: lessThanChar. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of lessThanChar
		 */
		public static final SourceModel.Expr lessThanChar(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lessThanChar), arg_1, arg_2});
		}

		/**
		 * @see #lessThanChar(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of lessThanChar
		 */
		public static final SourceModel.Expr lessThanChar(char arg_1, char arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lessThanChar), SourceModel.Expr.makeCharValue(arg_1), SourceModel.Expr.makeCharValue(arg_2)});
		}

		/**
		 * Name binding for function: lessThanChar.
		 * @see #lessThanChar(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName lessThanChar = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "lessThanChar");

		/**
		 * Helper binding method for function: lessThanComparable. 
		 * @param x
		 * @param y
		 * @return the SourceModule.expr representing an application of lessThanComparable
		 */
		public static final SourceModel.Expr lessThanComparable(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lessThanComparable), x, y});
		}

		/**
		 * Name binding for function: lessThanComparable.
		 * @see #lessThanComparable(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName lessThanComparable = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"lessThanComparable");

		/**
		 * Helper binding method for function: lessThanDecimal. 
		 * @param decimalValue1
		 * @param decimalValue2
		 * @return the SourceModule.expr representing an application of lessThanDecimal
		 */
		public static final SourceModel.Expr lessThanDecimal(SourceModel.Expr decimalValue1, SourceModel.Expr decimalValue2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lessThanDecimal), decimalValue1, decimalValue2});
		}

		/**
		 * Name binding for function: lessThanDecimal.
		 * @see #lessThanDecimal(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName lessThanDecimal = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"lessThanDecimal");

		/**
		 * Helper binding method for function: lessThanDefault. 
		 * @param x
		 * @param y
		 * @return the SourceModule.expr representing an application of lessThanDefault
		 */
		public static final SourceModel.Expr lessThanDefault(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lessThanDefault), x, y});
		}

		/**
		 * Name binding for function: lessThanDefault.
		 * @see #lessThanDefault(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName lessThanDefault = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"lessThanDefault");

		/**
		 * Helper binding method for function: lessThanDouble. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of lessThanDouble
		 */
		public static final SourceModel.Expr lessThanDouble(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lessThanDouble), arg_1, arg_2});
		}

		/**
		 * @see #lessThanDouble(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of lessThanDouble
		 */
		public static final SourceModel.Expr lessThanDouble(double arg_1, double arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lessThanDouble), SourceModel.Expr.makeDoubleValue(arg_1), SourceModel.Expr.makeDoubleValue(arg_2)});
		}

		/**
		 * Name binding for function: lessThanDouble.
		 * @see #lessThanDouble(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName lessThanDouble = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"lessThanDouble");

		/**
		 * Helper binding method for function: lessThanEqualsByte. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of lessThanEqualsByte
		 */
		public static final SourceModel.Expr lessThanEqualsByte(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lessThanEqualsByte), arg_1, arg_2});
		}

		/**
		 * @see #lessThanEqualsByte(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of lessThanEqualsByte
		 */
		public static final SourceModel.Expr lessThanEqualsByte(byte arg_1, byte arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lessThanEqualsByte), SourceModel.Expr.makeByteValue(arg_1), SourceModel.Expr.makeByteValue(arg_2)});
		}

		/**
		 * Name binding for function: lessThanEqualsByte.
		 * @see #lessThanEqualsByte(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName lessThanEqualsByte = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"lessThanEqualsByte");

		/**
		 * Helper binding method for function: lessThanEqualsChar. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of lessThanEqualsChar
		 */
		public static final SourceModel.Expr lessThanEqualsChar(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lessThanEqualsChar), arg_1, arg_2});
		}

		/**
		 * @see #lessThanEqualsChar(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of lessThanEqualsChar
		 */
		public static final SourceModel.Expr lessThanEqualsChar(char arg_1, char arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lessThanEqualsChar), SourceModel.Expr.makeCharValue(arg_1), SourceModel.Expr.makeCharValue(arg_2)});
		}

		/**
		 * Name binding for function: lessThanEqualsChar.
		 * @see #lessThanEqualsChar(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName lessThanEqualsChar = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"lessThanEqualsChar");

		/**
		 * Helper binding method for function: lessThanEqualsComparable. 
		 * @param x
		 * @param y
		 * @return the SourceModule.expr representing an application of lessThanEqualsComparable
		 */
		public static final SourceModel.Expr lessThanEqualsComparable(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lessThanEqualsComparable), x, y});
		}

		/**
		 * Name binding for function: lessThanEqualsComparable.
		 * @see #lessThanEqualsComparable(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName lessThanEqualsComparable = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"lessThanEqualsComparable");

		/**
		 * Helper binding method for function: lessThanEqualsDecimal. 
		 * @param decimalValue1
		 * @param decimalValue2
		 * @return the SourceModule.expr representing an application of lessThanEqualsDecimal
		 */
		public static final SourceModel.Expr lessThanEqualsDecimal(SourceModel.Expr decimalValue1, SourceModel.Expr decimalValue2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lessThanEqualsDecimal), decimalValue1, decimalValue2});
		}

		/**
		 * Name binding for function: lessThanEqualsDecimal.
		 * @see #lessThanEqualsDecimal(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName lessThanEqualsDecimal = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"lessThanEqualsDecimal");

		/**
		 * Helper binding method for function: lessThanEqualsDefault. 
		 * @param x
		 * @param y
		 * @return the SourceModule.expr representing an application of lessThanEqualsDefault
		 */
		public static final SourceModel.Expr lessThanEqualsDefault(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lessThanEqualsDefault), x, y});
		}

		/**
		 * Name binding for function: lessThanEqualsDefault.
		 * @see #lessThanEqualsDefault(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName lessThanEqualsDefault = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"lessThanEqualsDefault");

		/**
		 * Helper binding method for function: lessThanEqualsDouble. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of lessThanEqualsDouble
		 */
		public static final SourceModel.Expr lessThanEqualsDouble(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lessThanEqualsDouble), arg_1, arg_2});
		}

		/**
		 * @see #lessThanEqualsDouble(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of lessThanEqualsDouble
		 */
		public static final SourceModel.Expr lessThanEqualsDouble(double arg_1, double arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lessThanEqualsDouble), SourceModel.Expr.makeDoubleValue(arg_1), SourceModel.Expr.makeDoubleValue(arg_2)});
		}

		/**
		 * Name binding for function: lessThanEqualsDouble.
		 * @see #lessThanEqualsDouble(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName lessThanEqualsDouble = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"lessThanEqualsDouble");

		/**
		 * Helper binding method for function: lessThanEqualsFloat. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of lessThanEqualsFloat
		 */
		public static final SourceModel.Expr lessThanEqualsFloat(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lessThanEqualsFloat), arg_1, arg_2});
		}

		/**
		 * @see #lessThanEqualsFloat(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of lessThanEqualsFloat
		 */
		public static final SourceModel.Expr lessThanEqualsFloat(float arg_1, float arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lessThanEqualsFloat), SourceModel.Expr.makeFloatValue(arg_1), SourceModel.Expr.makeFloatValue(arg_2)});
		}

		/**
		 * Name binding for function: lessThanEqualsFloat.
		 * @see #lessThanEqualsFloat(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName lessThanEqualsFloat = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"lessThanEqualsFloat");

		/**
		 * Helper binding method for function: lessThanEqualsInt. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of lessThanEqualsInt
		 */
		public static final SourceModel.Expr lessThanEqualsInt(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lessThanEqualsInt), arg_1, arg_2});
		}

		/**
		 * @see #lessThanEqualsInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of lessThanEqualsInt
		 */
		public static final SourceModel.Expr lessThanEqualsInt(int arg_1, int arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lessThanEqualsInt), SourceModel.Expr.makeIntValue(arg_1), SourceModel.Expr.makeIntValue(arg_2)});
		}

		/**
		 * Name binding for function: lessThanEqualsInt.
		 * @see #lessThanEqualsInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName lessThanEqualsInt = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"lessThanEqualsInt");

		/**
		 * Helper binding method for function: lessThanEqualsInteger. 
		 * @param x
		 * @param y
		 * @return the SourceModule.expr representing an application of lessThanEqualsInteger
		 */
		public static final SourceModel.Expr lessThanEqualsInteger(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lessThanEqualsInteger), x, y});
		}

		/**
		 * Name binding for function: lessThanEqualsInteger.
		 * @see #lessThanEqualsInteger(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName lessThanEqualsInteger = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"lessThanEqualsInteger");

		/**
		 * Helper binding method for function: lessThanEqualsLong. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of lessThanEqualsLong
		 */
		public static final SourceModel.Expr lessThanEqualsLong(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lessThanEqualsLong), arg_1, arg_2});
		}

		/**
		 * @see #lessThanEqualsLong(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of lessThanEqualsLong
		 */
		public static final SourceModel.Expr lessThanEqualsLong(long arg_1, long arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lessThanEqualsLong), SourceModel.Expr.makeLongValue(arg_1), SourceModel.Expr.makeLongValue(arg_2)});
		}

		/**
		 * Name binding for function: lessThanEqualsLong.
		 * @see #lessThanEqualsLong(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName lessThanEqualsLong = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"lessThanEqualsLong");

		/**
		 * Helper binding method for function: lessThanEqualsRecord. 
		 * @param r1
		 * @param r2
		 * @return the SourceModule.expr representing an application of lessThanEqualsRecord
		 */
		public static final SourceModel.Expr lessThanEqualsRecord(SourceModel.Expr r1, SourceModel.Expr r2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lessThanEqualsRecord), r1, r2});
		}

		/**
		 * Name binding for function: lessThanEqualsRecord.
		 * @see #lessThanEqualsRecord(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName lessThanEqualsRecord = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"lessThanEqualsRecord");

		/**
		 * Helper binding method for function: lessThanEqualsShort. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of lessThanEqualsShort
		 */
		public static final SourceModel.Expr lessThanEqualsShort(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lessThanEqualsShort), arg_1, arg_2});
		}

		/**
		 * @see #lessThanEqualsShort(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of lessThanEqualsShort
		 */
		public static final SourceModel.Expr lessThanEqualsShort(short arg_1, short arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lessThanEqualsShort), SourceModel.Expr.makeShortValue(arg_1), SourceModel.Expr.makeShortValue(arg_2)});
		}

		/**
		 * Name binding for function: lessThanEqualsShort.
		 * @see #lessThanEqualsShort(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName lessThanEqualsShort = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"lessThanEqualsShort");

		/**
		 * Helper binding method for function: lessThanFloat. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of lessThanFloat
		 */
		public static final SourceModel.Expr lessThanFloat(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lessThanFloat), arg_1, arg_2});
		}

		/**
		 * @see #lessThanFloat(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of lessThanFloat
		 */
		public static final SourceModel.Expr lessThanFloat(float arg_1, float arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lessThanFloat), SourceModel.Expr.makeFloatValue(arg_1), SourceModel.Expr.makeFloatValue(arg_2)});
		}

		/**
		 * Name binding for function: lessThanFloat.
		 * @see #lessThanFloat(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName lessThanFloat = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"lessThanFloat");

		/**
		 * Helper binding method for function: lessThanInt. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of lessThanInt
		 */
		public static final SourceModel.Expr lessThanInt(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lessThanInt), arg_1, arg_2});
		}

		/**
		 * @see #lessThanInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of lessThanInt
		 */
		public static final SourceModel.Expr lessThanInt(int arg_1, int arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lessThanInt), SourceModel.Expr.makeIntValue(arg_1), SourceModel.Expr.makeIntValue(arg_2)});
		}

		/**
		 * Name binding for function: lessThanInt.
		 * @see #lessThanInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName lessThanInt = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "lessThanInt");

		/**
		 * Helper binding method for function: lessThanInteger. 
		 * @param x
		 * @param y
		 * @return the SourceModule.expr representing an application of lessThanInteger
		 */
		public static final SourceModel.Expr lessThanInteger(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lessThanInteger), x, y});
		}

		/**
		 * Name binding for function: lessThanInteger.
		 * @see #lessThanInteger(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName lessThanInteger = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"lessThanInteger");

		/**
		 * Helper binding method for function: lessThanLong. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of lessThanLong
		 */
		public static final SourceModel.Expr lessThanLong(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lessThanLong), arg_1, arg_2});
		}

		/**
		 * @see #lessThanLong(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of lessThanLong
		 */
		public static final SourceModel.Expr lessThanLong(long arg_1, long arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lessThanLong), SourceModel.Expr.makeLongValue(arg_1), SourceModel.Expr.makeLongValue(arg_2)});
		}

		/**
		 * Name binding for function: lessThanLong.
		 * @see #lessThanLong(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName lessThanLong = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "lessThanLong");

		/**
		 * Helper binding method for function: lessThanRecord. 
		 * @param r1
		 * @param r2
		 * @return the SourceModule.expr representing an application of lessThanRecord
		 */
		public static final SourceModel.Expr lessThanRecord(SourceModel.Expr r1, SourceModel.Expr r2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lessThanRecord), r1, r2});
		}

		/**
		 * Name binding for function: lessThanRecord.
		 * @see #lessThanRecord(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName lessThanRecord = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"lessThanRecord");

		/**
		 * Helper binding method for function: lessThanShort. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of lessThanShort
		 */
		public static final SourceModel.Expr lessThanShort(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lessThanShort), arg_1, arg_2});
		}

		/**
		 * @see #lessThanShort(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of lessThanShort
		 */
		public static final SourceModel.Expr lessThanShort(short arg_1, short arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lessThanShort), SourceModel.Expr.makeShortValue(arg_1), SourceModel.Expr.makeShortValue(arg_2)});
		}

		/**
		 * Name binding for function: lessThanShort.
		 * @see #lessThanShort(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName lessThanShort = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"lessThanShort");

		/**
		 * Converts a Java collection to a CAL list.
		 * <p>
		 * This function is defined in the <code>Cal.Core.Prelude</code> module for implementation purposes only. 
		 * The public api version is <code>Cal.Collections.List.fromJCollection</code>.
		 * 
		 * @param collection (CAL type: <code>Cal.Core.Prelude.JCollection</code>)
		 *          the Java collection.
		 * @return (CAL type: <code>Cal.Core.Prelude.Inputable a => [a]</code>) 
		 *          the corresponding CAL list.
		 */
		public static final SourceModel.Expr listFromJCollection(SourceModel.Expr collection) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.listFromJCollection), collection});
		}

		/**
		 * Name binding for function: listFromJCollection.
		 * @see #listFromJCollection(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName listFromJCollection = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"listFromJCollection");

		/**
		 * Converts a Java collection to a CAL list using the element mapping function <code>f</code> of type <code>Cal.Core.Prelude.JObject -&gt; a</code> 
		 * to convert elements of the Java list.
		 * <p>
		 * This function is defined in the <code>Cal.Core.Prelude</code> module for implementation purposes only. 
		 * The public api version is <code>Cal.Collections.List.fromJCollectionWith</code>.
		 * 
		 * @param javaCollection (CAL type: <code>Cal.Core.Prelude.JCollection</code>)
		 *          the Java collection.
		 * @param elementMappingFunction (CAL type: <code>Cal.Core.Prelude.JObject -> a</code>)
		 *          the mapping function converting elements of the Java list to CAL values.
		 * @return (CAL type: <code>[a]</code>) 
		 *          the corresponding CAL list.
		 */
		public static final SourceModel.Expr listFromJCollectionWith(SourceModel.Expr javaCollection, SourceModel.Expr elementMappingFunction) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.listFromJCollectionWith), javaCollection, elementMappingFunction});
		}

		/**
		 * Name binding for function: listFromJCollectionWith.
		 * @see #listFromJCollectionWith(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName listFromJCollectionWith = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"listFromJCollectionWith");

		/**
		 * Converts a Java iterator to a CAL list.
		 * <p>
		 * This function is defined in the <code>Cal.Core.Prelude</code> module for implementation purposes only. 
		 * The public api version is <code>Cal.Collections.List.fromJIterator</code>.
		 * 
		 * @param iterator (CAL type: <code>Cal.Core.Prelude.JIterator</code>)
		 *          the Java iterator
		 * @return (CAL type: <code>Cal.Core.Prelude.Inputable a => [a]</code>) 
		 *          the corresponding CAL list.
		 */
		public static final SourceModel.Expr listFromJIterator(SourceModel.Expr iterator) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.listFromJIterator), iterator});
		}

		/**
		 * Name binding for function: listFromJIterator.
		 * @see #listFromJIterator(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName listFromJIterator = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"listFromJIterator");

		/**
		 * Converts a Java iterator to a CAL list using the element mapping function <code>f</code> of 
		 * type <code>Cal.Core.Prelude.JObject -&gt; a</code> to convert iteration elements.
		 * <p>
		 * This function is defined in the <code>Cal.Core.Prelude</code> module for implementation purposes only. 
		 * The public api version is <code>Cal.Collections.List.fromJIteratorWith</code>.
		 * 
		 * @param iterator (CAL type: <code>Cal.Core.Prelude.JIterator</code>)
		 *          the Java iterator
		 * @param elementMappingFunction (CAL type: <code>Cal.Core.Prelude.JObject -> a</code>)
		 *          the mapping function converting iteration elements to CAL values.
		 * @return (CAL type: <code>[a]</code>) 
		 *          the corresponding CAL list.
		 */
		public static final SourceModel.Expr listFromJIteratorWith(SourceModel.Expr iterator, SourceModel.Expr elementMappingFunction) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.listFromJIteratorWith), iterator, elementMappingFunction});
		}

		/**
		 * Name binding for function: listFromJIteratorWith.
		 * @see #listFromJIteratorWith(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName listFromJIteratorWith = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"listFromJIteratorWith");

		/**
		 * Helper binding method for function: listToTypeReps. 
		 * @param typeRepList
		 * @return the SourceModule.expr representing an application of listToTypeReps
		 */
		public static final SourceModel.Expr listToTypeReps(SourceModel.Expr typeRepList) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.listToTypeReps), typeRepList});
		}

		/**
		 * Name binding for function: listToTypeReps.
		 * @see #listToTypeReps(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName listToTypeReps = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"listToTypeReps");

		/**
		 * Helper binding method for function: longToByte. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of longToByte
		 */
		public static final SourceModel.Expr longToByte(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longToByte), arg_1});
		}

		/**
		 * @see #longToByte(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of longToByte
		 */
		public static final SourceModel.Expr longToByte(long arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longToByte), SourceModel.Expr.makeLongValue(arg_1)});
		}

		/**
		 * Name binding for function: longToByte.
		 * @see #longToByte(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName longToByte = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "longToByte");

		/**
		 * Helper binding method for function: longToChar. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of longToChar
		 */
		public static final SourceModel.Expr longToChar(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longToChar), arg_1});
		}

		/**
		 * @see #longToChar(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of longToChar
		 */
		public static final SourceModel.Expr longToChar(long arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longToChar), SourceModel.Expr.makeLongValue(arg_1)});
		}

		/**
		 * Name binding for function: longToChar.
		 * @see #longToChar(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName longToChar = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "longToChar");

		/**
		 * Helper binding method for function: longToDecimal. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of longToDecimal
		 */
		public static final SourceModel.Expr longToDecimal(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longToDecimal), arg_1});
		}

		/**
		 * @see #longToDecimal(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of longToDecimal
		 */
		public static final SourceModel.Expr longToDecimal(long arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longToDecimal), SourceModel.Expr.makeLongValue(arg_1)});
		}

		/**
		 * Name binding for function: longToDecimal.
		 * @see #longToDecimal(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName longToDecimal = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"longToDecimal");

		/**
		 * Helper binding method for function: longToDouble. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of longToDouble
		 */
		public static final SourceModel.Expr longToDouble(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longToDouble), arg_1});
		}

		/**
		 * @see #longToDouble(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of longToDouble
		 */
		public static final SourceModel.Expr longToDouble(long arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longToDouble), SourceModel.Expr.makeLongValue(arg_1)});
		}

		/**
		 * Name binding for function: longToDouble.
		 * @see #longToDouble(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName longToDouble = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "longToDouble");

		/**
		 * Helper binding method for function: longToFloat. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of longToFloat
		 */
		public static final SourceModel.Expr longToFloat(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longToFloat), arg_1});
		}

		/**
		 * @see #longToFloat(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of longToFloat
		 */
		public static final SourceModel.Expr longToFloat(long arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longToFloat), SourceModel.Expr.makeLongValue(arg_1)});
		}

		/**
		 * Name binding for function: longToFloat.
		 * @see #longToFloat(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName longToFloat = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "longToFloat");

		/**
		 * Helper binding method for function: longToInt. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of longToInt
		 */
		public static final SourceModel.Expr longToInt(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longToInt), arg_1});
		}

		/**
		 * @see #longToInt(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of longToInt
		 */
		public static final SourceModel.Expr longToInt(long arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longToInt), SourceModel.Expr.makeLongValue(arg_1)});
		}

		/**
		 * Name binding for function: longToInt.
		 * @see #longToInt(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName longToInt = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "longToInt");

		/**
		 * Converts a <code>Cal.Core.Prelude.Long</code> value to the corresponding <code>Cal.Core.Prelude.Integer</code> value.
		 * @param longValue (CAL type: <code>Cal.Core.Prelude.Long</code>)
		 *          the <code>Cal.Core.Prelude.Long</code> value to be converted.
		 * @return (CAL type: <code>Cal.Core.Prelude.Integer</code>) 
		 *          the corresponding <code>Cal.Core.Prelude.Integer</code> value.
		 */
		public static final SourceModel.Expr longToInteger(SourceModel.Expr longValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longToInteger), longValue});
		}

		/**
		 * @see #longToInteger(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param longValue
		 * @return the SourceModel.Expr representing an application of longToInteger
		 */
		public static final SourceModel.Expr longToInteger(long longValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longToInteger), SourceModel.Expr.makeLongValue(longValue)});
		}

		/**
		 * Name binding for function: longToInteger.
		 * @see #longToInteger(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName longToInteger = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"longToInteger");

		/**
		 * Helper binding method for function: longToLong. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of longToLong
		 */
		public static final SourceModel.Expr longToLong(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longToLong), arg_1});
		}

		/**
		 * @see #longToLong(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of longToLong
		 */
		public static final SourceModel.Expr longToLong(long arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longToLong), SourceModel.Expr.makeLongValue(arg_1)});
		}

		/**
		 * Name binding for function: longToLong.
		 * @see #longToLong(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName longToLong = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "longToLong");

		/**
		 * Helper binding method for function: longToShort. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of longToShort
		 */
		public static final SourceModel.Expr longToShort(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longToShort), arg_1});
		}

		/**
		 * @see #longToShort(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of longToShort
		 */
		public static final SourceModel.Expr longToShort(long arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longToShort), SourceModel.Expr.makeLongValue(arg_1)});
		}

		/**
		 * Name binding for function: longToShort.
		 * @see #longToShort(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName longToShort = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "longToShort");

		/**
		 * Makes a comparator that uses the ordering function argument to implement the Java comparator's compare method.
		 * @param arg_1 (CAL type: <code>a -> a -> Cal.Core.Prelude.Ordering</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.JComparator</code>) 
		 */
		public static final SourceModel.Expr makeComparator(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeComparator), arg_1});
		}

		/**
		 * Name binding for function: makeComparator.
		 * @see #makeComparator(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeComparator = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"makeComparator");

		/**
		 * Helper binding method for function: makeEquivalenceRelation. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of makeEquivalenceRelation
		 */
		public static final SourceModel.Expr makeEquivalenceRelation(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeEquivalenceRelation), arg_1});
		}

		/**
		 * Name binding for function: makeEquivalenceRelation.
		 * @see #makeEquivalenceRelation(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeEquivalenceRelation = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"makeEquivalenceRelation");

		/**
		 * <code>map mapFunction list</code> applies the function <code>mapFunction</code> to each element of the list and returns
		 * the resulting list.
		 * <p>
		 * This function is defined in the <code>Cal.Core.Prelude</code> module for implementation purposes only. 
		 * The public api version is <code>Cal.Collections.List.map</code>.
		 * 
		 * @param mapFunction (CAL type: <code>a -> b</code>)
		 *          a function to be applied to each element of the list.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list.
		 * @return (CAL type: <code>[b]</code>) 
		 *          the list obtained by applying mapFunction to each element of the list.
		 */
		public static final SourceModel.Expr map(SourceModel.Expr mapFunction, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.map), mapFunction, list});
		}

		/**
		 * Name binding for function: map.
		 * @see #map(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName map = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "map");

		/**
		 * Helper binding method for function: maxBoundByte. 
		 * @return the SourceModule.expr representing an application of maxBoundByte
		 */
		public static final SourceModel.Expr maxBoundByte() {
			return SourceModel.Expr.Var.make(Functions.maxBoundByte);
		}

		/**
		 * Name binding for function: maxBoundByte.
		 * @see #maxBoundByte()
		 */
		public static final QualifiedName maxBoundByte = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "maxBoundByte");

		/**
		 * Helper binding method for function: maxBoundByteAsInt. 
		 * @return the SourceModule.expr representing an application of maxBoundByteAsInt
		 */
		public static final SourceModel.Expr maxBoundByteAsInt() {
			return SourceModel.Expr.Var.make(Functions.maxBoundByteAsInt);
		}

		/**
		 * Name binding for function: maxBoundByteAsInt.
		 * @see #maxBoundByteAsInt()
		 */
		public static final QualifiedName maxBoundByteAsInt = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"maxBoundByteAsInt");

		/**
		 * Helper binding method for function: maxBoundChar. 
		 * @return the SourceModule.expr representing an application of maxBoundChar
		 */
		public static final SourceModel.Expr maxBoundChar() {
			return SourceModel.Expr.Var.make(Functions.maxBoundChar);
		}

		/**
		 * Name binding for function: maxBoundChar.
		 * @see #maxBoundChar()
		 */
		public static final QualifiedName maxBoundChar = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "maxBoundChar");

		/**
		 * Helper binding method for function: maxBoundCharAsInt. 
		 * @return the SourceModule.expr representing an application of maxBoundCharAsInt
		 */
		public static final SourceModel.Expr maxBoundCharAsInt() {
			return SourceModel.Expr.Var.make(Functions.maxBoundCharAsInt);
		}

		/**
		 * Name binding for function: maxBoundCharAsInt.
		 * @see #maxBoundCharAsInt()
		 */
		public static final QualifiedName maxBoundCharAsInt = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"maxBoundCharAsInt");

		/**
		 * Helper binding method for function: maxBoundInt. 
		 * @return the SourceModule.expr representing an application of maxBoundInt
		 */
		public static final SourceModel.Expr maxBoundInt() {
			return SourceModel.Expr.Var.make(Functions.maxBoundInt);
		}

		/**
		 * Name binding for function: maxBoundInt.
		 * @see #maxBoundInt()
		 */
		public static final QualifiedName maxBoundInt = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "maxBoundInt");

		/**
		 * Helper binding method for function: maxBoundLong. 
		 * @return the SourceModule.expr representing an application of maxBoundLong
		 */
		public static final SourceModel.Expr maxBoundLong() {
			return SourceModel.Expr.Var.make(Functions.maxBoundLong);
		}

		/**
		 * Name binding for function: maxBoundLong.
		 * @see #maxBoundLong()
		 */
		public static final QualifiedName maxBoundLong = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "maxBoundLong");

		/**
		 * Helper binding method for function: maxBoundShort. 
		 * @return the SourceModule.expr representing an application of maxBoundShort
		 */
		public static final SourceModel.Expr maxBoundShort() {
			return SourceModel.Expr.Var.make(Functions.maxBoundShort);
		}

		/**
		 * Name binding for function: maxBoundShort.
		 * @see #maxBoundShort()
		 */
		public static final QualifiedName maxBoundShort = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"maxBoundShort");

		/**
		 * Helper binding method for function: maxBoundShortAsInt. 
		 * @return the SourceModule.expr representing an application of maxBoundShortAsInt
		 */
		public static final SourceModel.Expr maxBoundShortAsInt() {
			return SourceModel.Expr.Var.make(Functions.maxBoundShortAsInt);
		}

		/**
		 * Name binding for function: maxBoundShortAsInt.
		 * @see #maxBoundShortAsInt()
		 */
		public static final QualifiedName maxBoundShortAsInt = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"maxBoundShortAsInt");

		/**
		 * Helper binding method for function: maxByte. 
		 * @param x
		 * @param y
		 * @return the SourceModule.expr representing an application of maxByte
		 */
		public static final SourceModel.Expr maxByte(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.maxByte), x, y});
		}

		/**
		 * @see #maxByte(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @param y
		 * @return the SourceModel.Expr representing an application of maxByte
		 */
		public static final SourceModel.Expr maxByte(byte x, byte y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.maxByte), SourceModel.Expr.makeByteValue(x), SourceModel.Expr.makeByteValue(y)});
		}

		/**
		 * Name binding for function: maxByte.
		 * @see #maxByte(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName maxByte = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "maxByte");

		/**
		 * Helper binding method for function: maxChar. 
		 * @param x
		 * @param y
		 * @return the SourceModule.expr representing an application of maxChar
		 */
		public static final SourceModel.Expr maxChar(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.maxChar), x, y});
		}

		/**
		 * @see #maxChar(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @param y
		 * @return the SourceModel.Expr representing an application of maxChar
		 */
		public static final SourceModel.Expr maxChar(char x, char y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.maxChar), SourceModel.Expr.makeCharValue(x), SourceModel.Expr.makeCharValue(y)});
		}

		/**
		 * Name binding for function: maxChar.
		 * @see #maxChar(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName maxChar = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "maxChar");

		/**
		 * Helper binding method for function: maxComparable. 
		 * @param x
		 * @param y
		 * @return the SourceModule.expr representing an application of maxComparable
		 */
		public static final SourceModel.Expr maxComparable(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.maxComparable), x, y});
		}

		/**
		 * Name binding for function: maxComparable.
		 * @see #maxComparable(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName maxComparable = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"maxComparable");

		/**
		 * Helper binding method for function: maxDecimal. 
		 * @param decimal
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of maxDecimal
		 */
		public static final SourceModel.Expr maxDecimal(SourceModel.Expr decimal, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.maxDecimal), decimal, arg_2});
		}

		/**
		 * Name binding for function: maxDecimal.
		 * @see #maxDecimal(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName maxDecimal = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "maxDecimal");

		/**
		 * Helper binding method for function: maxDefault. 
		 * @param x
		 * @param y
		 * @return the SourceModule.expr representing an application of maxDefault
		 */
		public static final SourceModel.Expr maxDefault(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.maxDefault), x, y});
		}

		/**
		 * Name binding for function: maxDefault.
		 * @see #maxDefault(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName maxDefault = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "maxDefault");

		/**
		 * Helper binding method for function: maxDouble. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of maxDouble
		 */
		public static final SourceModel.Expr maxDouble(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.maxDouble), arg_1, arg_2});
		}

		/**
		 * @see #maxDouble(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of maxDouble
		 */
		public static final SourceModel.Expr maxDouble(double arg_1, double arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.maxDouble), SourceModel.Expr.makeDoubleValue(arg_1), SourceModel.Expr.makeDoubleValue(arg_2)});
		}

		/**
		 * Name binding for function: maxDouble.
		 * @see #maxDouble(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName maxDouble = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "maxDouble");

		/**
		 * Helper binding method for function: maxFloat. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of maxFloat
		 */
		public static final SourceModel.Expr maxFloat(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.maxFloat), arg_1, arg_2});
		}

		/**
		 * @see #maxFloat(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of maxFloat
		 */
		public static final SourceModel.Expr maxFloat(float arg_1, float arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.maxFloat), SourceModel.Expr.makeFloatValue(arg_1), SourceModel.Expr.makeFloatValue(arg_2)});
		}

		/**
		 * Name binding for function: maxFloat.
		 * @see #maxFloat(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName maxFloat = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "maxFloat");

		/**
		 * Helper binding method for function: maxInt. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of maxInt
		 */
		public static final SourceModel.Expr maxInt(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.maxInt), arg_1, arg_2});
		}

		/**
		 * @see #maxInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of maxInt
		 */
		public static final SourceModel.Expr maxInt(int arg_1, int arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.maxInt), SourceModel.Expr.makeIntValue(arg_1), SourceModel.Expr.makeIntValue(arg_2)});
		}

		/**
		 * Name binding for function: maxInt.
		 * @see #maxInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName maxInt = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "maxInt");

		/**
		 * Helper binding method for function: maxInteger. 
		 * @param integer
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of maxInteger
		 */
		public static final SourceModel.Expr maxInteger(SourceModel.Expr integer, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.maxInteger), integer, arg_2});
		}

		/**
		 * Name binding for function: maxInteger.
		 * @see #maxInteger(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName maxInteger = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "maxInteger");

		/**
		 * Helper binding method for function: maxLong. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of maxLong
		 */
		public static final SourceModel.Expr maxLong(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.maxLong), arg_1, arg_2});
		}

		/**
		 * @see #maxLong(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of maxLong
		 */
		public static final SourceModel.Expr maxLong(long arg_1, long arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.maxLong), SourceModel.Expr.makeLongValue(arg_1), SourceModel.Expr.makeLongValue(arg_2)});
		}

		/**
		 * Name binding for function: maxLong.
		 * @see #maxLong(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName maxLong = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "maxLong");

		/**
		 * Helper binding method for function: maxRecord. 
		 * @param r1
		 * @param r2
		 * @return the SourceModule.expr representing an application of maxRecord
		 */
		public static final SourceModel.Expr maxRecord(SourceModel.Expr r1, SourceModel.Expr r2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.maxRecord), r1, r2});
		}

		/**
		 * Name binding for function: maxRecord.
		 * @see #maxRecord(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName maxRecord = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "maxRecord");

		/**
		 * Helper binding method for function: maxShort. 
		 * @param x
		 * @param y
		 * @return the SourceModule.expr representing an application of maxShort
		 */
		public static final SourceModel.Expr maxShort(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.maxShort), x, y});
		}

		/**
		 * @see #maxShort(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @param y
		 * @return the SourceModel.Expr representing an application of maxShort
		 */
		public static final SourceModel.Expr maxShort(short x, short y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.maxShort), SourceModel.Expr.makeShortValue(x), SourceModel.Expr.makeShortValue(y)});
		}

		/**
		 * Name binding for function: maxShort.
		 * @see #maxShort(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName maxShort = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "maxShort");

		/**
		 * Helper binding method for function: maybeValue_getValueField. 
		 * @param jMaybeValue
		 * @return the SourceModule.expr representing an application of maybeValue_getValueField
		 */
		public static final SourceModel.Expr maybeValue_getValueField(SourceModel.Expr jMaybeValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.maybeValue_getValueField), jMaybeValue});
		}

		/**
		 * Name binding for function: maybeValue_getValueField.
		 * @see #maybeValue_getValueField(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName maybeValue_getValueField = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"maybeValue_getValueField");

		/**
		 * Helper binding method for function: maybeValue_isNothing. 
		 * @param jMaybeValue
		 * @return the SourceModule.expr representing an application of maybeValue_isNothing
		 */
		public static final SourceModel.Expr maybeValue_isNothing(SourceModel.Expr jMaybeValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.maybeValue_isNothing), jMaybeValue});
		}

		/**
		 * Name binding for function: maybeValue_isNothing.
		 * @see #maybeValue_isNothing(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName maybeValue_isNothing = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"maybeValue_isNothing");

		/**
		 * Helper binding method for function: maybeValue_makeJust. 
		 * @param value
		 * @return the SourceModule.expr representing an application of maybeValue_makeJust
		 */
		public static final SourceModel.Expr maybeValue_makeJust(SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.maybeValue_makeJust), value});
		}

		/**
		 * Name binding for function: maybeValue_makeJust.
		 * @see #maybeValue_makeJust(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName maybeValue_makeJust = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"maybeValue_makeJust");

		/**
		 * Helper binding method for function: maybeValue_makeNothing. 
		 * @return the SourceModule.expr representing an application of maybeValue_makeNothing
		 */
		public static final SourceModel.Expr maybeValue_makeNothing() {
			return SourceModel.Expr.Var.make(Functions.maybeValue_makeNothing);
		}

		/**
		 * Name binding for function: maybeValue_makeNothing.
		 * @see #maybeValue_makeNothing()
		 */
		public static final QualifiedName maybeValue_makeNothing = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"maybeValue_makeNothing");

		/**
		 * Helper binding method for function: minBoundByte. 
		 * @return the SourceModule.expr representing an application of minBoundByte
		 */
		public static final SourceModel.Expr minBoundByte() {
			return SourceModel.Expr.Var.make(Functions.minBoundByte);
		}

		/**
		 * Name binding for function: minBoundByte.
		 * @see #minBoundByte()
		 */
		public static final QualifiedName minBoundByte = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "minBoundByte");

		/**
		 * Helper binding method for function: minBoundByteAsInt. 
		 * @return the SourceModule.expr representing an application of minBoundByteAsInt
		 */
		public static final SourceModel.Expr minBoundByteAsInt() {
			return SourceModel.Expr.Var.make(Functions.minBoundByteAsInt);
		}

		/**
		 * Name binding for function: minBoundByteAsInt.
		 * @see #minBoundByteAsInt()
		 */
		public static final QualifiedName minBoundByteAsInt = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"minBoundByteAsInt");

		/**
		 * Helper binding method for function: minBoundChar. 
		 * @return the SourceModule.expr representing an application of minBoundChar
		 */
		public static final SourceModel.Expr minBoundChar() {
			return SourceModel.Expr.Var.make(Functions.minBoundChar);
		}

		/**
		 * Name binding for function: minBoundChar.
		 * @see #minBoundChar()
		 */
		public static final QualifiedName minBoundChar = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "minBoundChar");

		/**
		 * Helper binding method for function: minBoundCharAsInt. 
		 * @return the SourceModule.expr representing an application of minBoundCharAsInt
		 */
		public static final SourceModel.Expr minBoundCharAsInt() {
			return SourceModel.Expr.Var.make(Functions.minBoundCharAsInt);
		}

		/**
		 * Name binding for function: minBoundCharAsInt.
		 * @see #minBoundCharAsInt()
		 */
		public static final QualifiedName minBoundCharAsInt = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"minBoundCharAsInt");

		/**
		 * Helper binding method for function: minBoundInt. 
		 * @return the SourceModule.expr representing an application of minBoundInt
		 */
		public static final SourceModel.Expr minBoundInt() {
			return SourceModel.Expr.Var.make(Functions.minBoundInt);
		}

		/**
		 * Name binding for function: minBoundInt.
		 * @see #minBoundInt()
		 */
		public static final QualifiedName minBoundInt = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "minBoundInt");

		/**
		 * Helper binding method for function: minBoundLong. 
		 * @return the SourceModule.expr representing an application of minBoundLong
		 */
		public static final SourceModel.Expr minBoundLong() {
			return SourceModel.Expr.Var.make(Functions.minBoundLong);
		}

		/**
		 * Name binding for function: minBoundLong.
		 * @see #minBoundLong()
		 */
		public static final QualifiedName minBoundLong = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "minBoundLong");

		/**
		 * Helper binding method for function: minBoundShort. 
		 * @return the SourceModule.expr representing an application of minBoundShort
		 */
		public static final SourceModel.Expr minBoundShort() {
			return SourceModel.Expr.Var.make(Functions.minBoundShort);
		}

		/**
		 * Name binding for function: minBoundShort.
		 * @see #minBoundShort()
		 */
		public static final QualifiedName minBoundShort = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"minBoundShort");

		/**
		 * Helper binding method for function: minBoundShortAsInt. 
		 * @return the SourceModule.expr representing an application of minBoundShortAsInt
		 */
		public static final SourceModel.Expr minBoundShortAsInt() {
			return SourceModel.Expr.Var.make(Functions.minBoundShortAsInt);
		}

		/**
		 * Name binding for function: minBoundShortAsInt.
		 * @see #minBoundShortAsInt()
		 */
		public static final QualifiedName minBoundShortAsInt = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"minBoundShortAsInt");

		/**
		 * Helper binding method for function: minByte. 
		 * @param x
		 * @param y
		 * @return the SourceModule.expr representing an application of minByte
		 */
		public static final SourceModel.Expr minByte(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.minByte), x, y});
		}

		/**
		 * @see #minByte(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @param y
		 * @return the SourceModel.Expr representing an application of minByte
		 */
		public static final SourceModel.Expr minByte(byte x, byte y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.minByte), SourceModel.Expr.makeByteValue(x), SourceModel.Expr.makeByteValue(y)});
		}

		/**
		 * Name binding for function: minByte.
		 * @see #minByte(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName minByte = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "minByte");

		/**
		 * Helper binding method for function: minChar. 
		 * @param x
		 * @param y
		 * @return the SourceModule.expr representing an application of minChar
		 */
		public static final SourceModel.Expr minChar(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.minChar), x, y});
		}

		/**
		 * @see #minChar(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @param y
		 * @return the SourceModel.Expr representing an application of minChar
		 */
		public static final SourceModel.Expr minChar(char x, char y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.minChar), SourceModel.Expr.makeCharValue(x), SourceModel.Expr.makeCharValue(y)});
		}

		/**
		 * Name binding for function: minChar.
		 * @see #minChar(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName minChar = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "minChar");

		/**
		 * Helper binding method for function: minComparable. 
		 * @param x
		 * @param y
		 * @return the SourceModule.expr representing an application of minComparable
		 */
		public static final SourceModel.Expr minComparable(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.minComparable), x, y});
		}

		/**
		 * Name binding for function: minComparable.
		 * @see #minComparable(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName minComparable = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"minComparable");

		/**
		 * Helper binding method for function: minDecimal. 
		 * @param decimal
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of minDecimal
		 */
		public static final SourceModel.Expr minDecimal(SourceModel.Expr decimal, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.minDecimal), decimal, arg_2});
		}

		/**
		 * Name binding for function: minDecimal.
		 * @see #minDecimal(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName minDecimal = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "minDecimal");

		/**
		 * Helper binding method for function: minDefault. 
		 * @param x
		 * @param y
		 * @return the SourceModule.expr representing an application of minDefault
		 */
		public static final SourceModel.Expr minDefault(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.minDefault), x, y});
		}

		/**
		 * Name binding for function: minDefault.
		 * @see #minDefault(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName minDefault = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "minDefault");

		/**
		 * Helper binding method for function: minDouble. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of minDouble
		 */
		public static final SourceModel.Expr minDouble(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.minDouble), arg_1, arg_2});
		}

		/**
		 * @see #minDouble(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of minDouble
		 */
		public static final SourceModel.Expr minDouble(double arg_1, double arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.minDouble), SourceModel.Expr.makeDoubleValue(arg_1), SourceModel.Expr.makeDoubleValue(arg_2)});
		}

		/**
		 * Name binding for function: minDouble.
		 * @see #minDouble(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName minDouble = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "minDouble");

		/**
		 * Helper binding method for function: minFloat. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of minFloat
		 */
		public static final SourceModel.Expr minFloat(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.minFloat), arg_1, arg_2});
		}

		/**
		 * @see #minFloat(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of minFloat
		 */
		public static final SourceModel.Expr minFloat(float arg_1, float arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.minFloat), SourceModel.Expr.makeFloatValue(arg_1), SourceModel.Expr.makeFloatValue(arg_2)});
		}

		/**
		 * Name binding for function: minFloat.
		 * @see #minFloat(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName minFloat = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "minFloat");

		/**
		 * Helper binding method for function: minInt. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of minInt
		 */
		public static final SourceModel.Expr minInt(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.minInt), arg_1, arg_2});
		}

		/**
		 * @see #minInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of minInt
		 */
		public static final SourceModel.Expr minInt(int arg_1, int arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.minInt), SourceModel.Expr.makeIntValue(arg_1), SourceModel.Expr.makeIntValue(arg_2)});
		}

		/**
		 * Name binding for function: minInt.
		 * @see #minInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName minInt = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "minInt");

		/**
		 * Helper binding method for function: minInteger. 
		 * @param integer
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of minInteger
		 */
		public static final SourceModel.Expr minInteger(SourceModel.Expr integer, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.minInteger), integer, arg_2});
		}

		/**
		 * Name binding for function: minInteger.
		 * @see #minInteger(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName minInteger = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "minInteger");

		/**
		 * Helper binding method for function: minLong. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of minLong
		 */
		public static final SourceModel.Expr minLong(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.minLong), arg_1, arg_2});
		}

		/**
		 * @see #minLong(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of minLong
		 */
		public static final SourceModel.Expr minLong(long arg_1, long arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.minLong), SourceModel.Expr.makeLongValue(arg_1), SourceModel.Expr.makeLongValue(arg_2)});
		}

		/**
		 * Name binding for function: minLong.
		 * @see #minLong(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName minLong = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "minLong");

		/**
		 * Helper binding method for function: minRecord. 
		 * @param r1
		 * @param r2
		 * @return the SourceModule.expr representing an application of minRecord
		 */
		public static final SourceModel.Expr minRecord(SourceModel.Expr r1, SourceModel.Expr r2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.minRecord), r1, r2});
		}

		/**
		 * Name binding for function: minRecord.
		 * @see #minRecord(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName minRecord = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "minRecord");

		/**
		 * Helper binding method for function: minShort. 
		 * @param x
		 * @param y
		 * @return the SourceModule.expr representing an application of minShort
		 */
		public static final SourceModel.Expr minShort(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.minShort), x, y});
		}

		/**
		 * @see #minShort(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @param y
		 * @return the SourceModel.Expr representing an application of minShort
		 */
		public static final SourceModel.Expr minShort(short x, short y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.minShort), SourceModel.Expr.makeShortValue(x), SourceModel.Expr.makeShortValue(y)});
		}

		/**
		 * Name binding for function: minShort.
		 * @see #minShort(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName minShort = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "minShort");

		/**
		 * Helper binding method for function: multiplyByte. 
		 * @param x
		 * @param y
		 * @return the SourceModule.expr representing an application of multiplyByte
		 */
		public static final SourceModel.Expr multiplyByte(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.multiplyByte), x, y});
		}

		/**
		 * @see #multiplyByte(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @param y
		 * @return the SourceModel.Expr representing an application of multiplyByte
		 */
		public static final SourceModel.Expr multiplyByte(byte x, byte y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.multiplyByte), SourceModel.Expr.makeByteValue(x), SourceModel.Expr.makeByteValue(y)});
		}

		/**
		 * Name binding for function: multiplyByte.
		 * @see #multiplyByte(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName multiplyByte = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "multiplyByte");

		/**
		 * Helper binding method for function: multiplyDecimal. 
		 * @param decimal
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of multiplyDecimal
		 */
		public static final SourceModel.Expr multiplyDecimal(SourceModel.Expr decimal, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.multiplyDecimal), decimal, arg_2});
		}

		/**
		 * Name binding for function: multiplyDecimal.
		 * @see #multiplyDecimal(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName multiplyDecimal = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"multiplyDecimal");

		/**
		 * Helper binding method for function: multiplyDouble. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of multiplyDouble
		 */
		public static final SourceModel.Expr multiplyDouble(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.multiplyDouble), arg_1, arg_2});
		}

		/**
		 * @see #multiplyDouble(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of multiplyDouble
		 */
		public static final SourceModel.Expr multiplyDouble(double arg_1, double arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.multiplyDouble), SourceModel.Expr.makeDoubleValue(arg_1), SourceModel.Expr.makeDoubleValue(arg_2)});
		}

		/**
		 * Name binding for function: multiplyDouble.
		 * @see #multiplyDouble(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName multiplyDouble = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"multiplyDouble");

		/**
		 * Helper binding method for function: multiplyFloat. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of multiplyFloat
		 */
		public static final SourceModel.Expr multiplyFloat(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.multiplyFloat), arg_1, arg_2});
		}

		/**
		 * @see #multiplyFloat(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of multiplyFloat
		 */
		public static final SourceModel.Expr multiplyFloat(float arg_1, float arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.multiplyFloat), SourceModel.Expr.makeFloatValue(arg_1), SourceModel.Expr.makeFloatValue(arg_2)});
		}

		/**
		 * Name binding for function: multiplyFloat.
		 * @see #multiplyFloat(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName multiplyFloat = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"multiplyFloat");

		/**
		 * Helper binding method for function: multiplyInt. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of multiplyInt
		 */
		public static final SourceModel.Expr multiplyInt(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.multiplyInt), arg_1, arg_2});
		}

		/**
		 * @see #multiplyInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of multiplyInt
		 */
		public static final SourceModel.Expr multiplyInt(int arg_1, int arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.multiplyInt), SourceModel.Expr.makeIntValue(arg_1), SourceModel.Expr.makeIntValue(arg_2)});
		}

		/**
		 * Name binding for function: multiplyInt.
		 * @see #multiplyInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName multiplyInt = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "multiplyInt");

		/**
		 * Helper binding method for function: multiplyInteger. 
		 * @param integer
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of multiplyInteger
		 */
		public static final SourceModel.Expr multiplyInteger(SourceModel.Expr integer, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.multiplyInteger), integer, arg_2});
		}

		/**
		 * Name binding for function: multiplyInteger.
		 * @see #multiplyInteger(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName multiplyInteger = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"multiplyInteger");

		/**
		 * Helper binding method for function: multiplyLong. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of multiplyLong
		 */
		public static final SourceModel.Expr multiplyLong(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.multiplyLong), arg_1, arg_2});
		}

		/**
		 * @see #multiplyLong(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of multiplyLong
		 */
		public static final SourceModel.Expr multiplyLong(long arg_1, long arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.multiplyLong), SourceModel.Expr.makeLongValue(arg_1), SourceModel.Expr.makeLongValue(arg_2)});
		}

		/**
		 * Name binding for function: multiplyLong.
		 * @see #multiplyLong(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName multiplyLong = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "multiplyLong");

		/**
		 * Helper binding method for function: multiplyShort. 
		 * @param x
		 * @param y
		 * @return the SourceModule.expr representing an application of multiplyShort
		 */
		public static final SourceModel.Expr multiplyShort(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.multiplyShort), x, y});
		}

		/**
		 * @see #multiplyShort(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @param y
		 * @return the SourceModel.Expr representing an application of multiplyShort
		 */
		public static final SourceModel.Expr multiplyShort(short x, short y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.multiplyShort), SourceModel.Expr.makeShortValue(x), SourceModel.Expr.makeShortValue(y)});
		}

		/**
		 * Name binding for function: multiplyShort.
		 * @see #multiplyShort(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName multiplyShort = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"multiplyShort");

		/**
		 * Helper binding method for function: negateByte. 
		 * @param x
		 * @return the SourceModule.expr representing an application of negateByte
		 */
		public static final SourceModel.Expr negateByte(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.negateByte), x});
		}

		/**
		 * @see #negateByte(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @return the SourceModel.Expr representing an application of negateByte
		 */
		public static final SourceModel.Expr negateByte(byte x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.negateByte), SourceModel.Expr.makeByteValue(x)});
		}

		/**
		 * Name binding for function: negateByte.
		 * @see #negateByte(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName negateByte = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "negateByte");

		/**
		 * Helper binding method for function: negateDecimal. 
		 * @param decimal
		 * @return the SourceModule.expr representing an application of negateDecimal
		 */
		public static final SourceModel.Expr negateDecimal(SourceModel.Expr decimal) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.negateDecimal), decimal});
		}

		/**
		 * Name binding for function: negateDecimal.
		 * @see #negateDecimal(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName negateDecimal = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"negateDecimal");

		/**
		 * Helper binding method for function: negateDouble. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of negateDouble
		 */
		public static final SourceModel.Expr negateDouble(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.negateDouble), arg_1});
		}

		/**
		 * @see #negateDouble(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of negateDouble
		 */
		public static final SourceModel.Expr negateDouble(double arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.negateDouble), SourceModel.Expr.makeDoubleValue(arg_1)});
		}

		/**
		 * Name binding for function: negateDouble.
		 * @see #negateDouble(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName negateDouble = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "negateDouble");

		/**
		 * Helper binding method for function: negateFloat. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of negateFloat
		 */
		public static final SourceModel.Expr negateFloat(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.negateFloat), arg_1});
		}

		/**
		 * @see #negateFloat(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of negateFloat
		 */
		public static final SourceModel.Expr negateFloat(float arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.negateFloat), SourceModel.Expr.makeFloatValue(arg_1)});
		}

		/**
		 * Name binding for function: negateFloat.
		 * @see #negateFloat(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName negateFloat = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "negateFloat");

		/**
		 * Helper binding method for function: negateInt. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of negateInt
		 */
		public static final SourceModel.Expr negateInt(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.negateInt), arg_1});
		}

		/**
		 * @see #negateInt(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of negateInt
		 */
		public static final SourceModel.Expr negateInt(int arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.negateInt), SourceModel.Expr.makeIntValue(arg_1)});
		}

		/**
		 * Name binding for function: negateInt.
		 * @see #negateInt(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName negateInt = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "negateInt");

		/**
		 * Helper binding method for function: negateInteger. 
		 * @param integer
		 * @return the SourceModule.expr representing an application of negateInteger
		 */
		public static final SourceModel.Expr negateInteger(SourceModel.Expr integer) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.negateInteger), integer});
		}

		/**
		 * Name binding for function: negateInteger.
		 * @see #negateInteger(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName negateInteger = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"negateInteger");

		/**
		 * Helper binding method for function: negateLong. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of negateLong
		 */
		public static final SourceModel.Expr negateLong(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.negateLong), arg_1});
		}

		/**
		 * @see #negateLong(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of negateLong
		 */
		public static final SourceModel.Expr negateLong(long arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.negateLong), SourceModel.Expr.makeLongValue(arg_1)});
		}

		/**
		 * Name binding for function: negateLong.
		 * @see #negateLong(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName negateLong = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "negateLong");

		/**
		 * Helper binding method for function: negateShort. 
		 * @param x
		 * @return the SourceModule.expr representing an application of negateShort
		 */
		public static final SourceModel.Expr negateShort(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.negateShort), x});
		}

		/**
		 * @see #negateShort(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @return the SourceModel.Expr representing an application of negateShort
		 */
		public static final SourceModel.Expr negateShort(short x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.negateShort), SourceModel.Expr.makeShortValue(x)});
		}

		/**
		 * Name binding for function: negateShort.
		 * @see #negateShort(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName negateShort = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "negateShort");

		/**
		 * Helper binding method for function: notEqualsByte. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of notEqualsByte
		 */
		public static final SourceModel.Expr notEqualsByte(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.notEqualsByte), arg_1, arg_2});
		}

		/**
		 * @see #notEqualsByte(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of notEqualsByte
		 */
		public static final SourceModel.Expr notEqualsByte(byte arg_1, byte arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.notEqualsByte), SourceModel.Expr.makeByteValue(arg_1), SourceModel.Expr.makeByteValue(arg_2)});
		}

		/**
		 * Name binding for function: notEqualsByte.
		 * @see #notEqualsByte(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName notEqualsByte = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"notEqualsByte");

		/**
		 * Helper binding method for function: notEqualsChar. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of notEqualsChar
		 */
		public static final SourceModel.Expr notEqualsChar(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.notEqualsChar), arg_1, arg_2});
		}

		/**
		 * @see #notEqualsChar(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of notEqualsChar
		 */
		public static final SourceModel.Expr notEqualsChar(char arg_1, char arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.notEqualsChar), SourceModel.Expr.makeCharValue(arg_1), SourceModel.Expr.makeCharValue(arg_2)});
		}

		/**
		 * Name binding for function: notEqualsChar.
		 * @see #notEqualsChar(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName notEqualsChar = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"notEqualsChar");

		/**
		 * Helper binding method for function: notEqualsDecimal. 
		 * @param decimalValue1
		 * @param decimalValue2
		 * @return the SourceModule.expr representing an application of notEqualsDecimal
		 */
		public static final SourceModel.Expr notEqualsDecimal(SourceModel.Expr decimalValue1, SourceModel.Expr decimalValue2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.notEqualsDecimal), decimalValue1, decimalValue2});
		}

		/**
		 * Name binding for function: notEqualsDecimal.
		 * @see #notEqualsDecimal(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName notEqualsDecimal = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"notEqualsDecimal");

		/**
		 * Helper binding method for function: notEqualsDefault. 
		 * @param x
		 * @param y
		 * @return the SourceModule.expr representing an application of notEqualsDefault
		 */
		public static final SourceModel.Expr notEqualsDefault(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.notEqualsDefault), x, y});
		}

		/**
		 * Name binding for function: notEqualsDefault.
		 * @see #notEqualsDefault(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName notEqualsDefault = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"notEqualsDefault");

		/**
		 * Helper binding method for function: notEqualsDouble. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of notEqualsDouble
		 */
		public static final SourceModel.Expr notEqualsDouble(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.notEqualsDouble), arg_1, arg_2});
		}

		/**
		 * @see #notEqualsDouble(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of notEqualsDouble
		 */
		public static final SourceModel.Expr notEqualsDouble(double arg_1, double arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.notEqualsDouble), SourceModel.Expr.makeDoubleValue(arg_1), SourceModel.Expr.makeDoubleValue(arg_2)});
		}

		/**
		 * Name binding for function: notEqualsDouble.
		 * @see #notEqualsDouble(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName notEqualsDouble = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"notEqualsDouble");

		/**
		 * Helper binding method for function: notEqualsFloat. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of notEqualsFloat
		 */
		public static final SourceModel.Expr notEqualsFloat(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.notEqualsFloat), arg_1, arg_2});
		}

		/**
		 * @see #notEqualsFloat(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of notEqualsFloat
		 */
		public static final SourceModel.Expr notEqualsFloat(float arg_1, float arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.notEqualsFloat), SourceModel.Expr.makeFloatValue(arg_1), SourceModel.Expr.makeFloatValue(arg_2)});
		}

		/**
		 * Name binding for function: notEqualsFloat.
		 * @see #notEqualsFloat(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName notEqualsFloat = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"notEqualsFloat");

		/**
		 * Helper binding method for function: notEqualsInt. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of notEqualsInt
		 */
		public static final SourceModel.Expr notEqualsInt(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.notEqualsInt), arg_1, arg_2});
		}

		/**
		 * @see #notEqualsInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of notEqualsInt
		 */
		public static final SourceModel.Expr notEqualsInt(int arg_1, int arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.notEqualsInt), SourceModel.Expr.makeIntValue(arg_1), SourceModel.Expr.makeIntValue(arg_2)});
		}

		/**
		 * Name binding for function: notEqualsInt.
		 * @see #notEqualsInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName notEqualsInt = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "notEqualsInt");

		/**
		 * Helper binding method for function: notEqualsLong. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of notEqualsLong
		 */
		public static final SourceModel.Expr notEqualsLong(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.notEqualsLong), arg_1, arg_2});
		}

		/**
		 * @see #notEqualsLong(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of notEqualsLong
		 */
		public static final SourceModel.Expr notEqualsLong(long arg_1, long arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.notEqualsLong), SourceModel.Expr.makeLongValue(arg_1), SourceModel.Expr.makeLongValue(arg_2)});
		}

		/**
		 * Name binding for function: notEqualsLong.
		 * @see #notEqualsLong(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName notEqualsLong = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"notEqualsLong");

		/**
		 * Helper binding method for function: notEqualsObject. 
		 * @param x
		 * @param y
		 * @return the SourceModule.expr representing an application of notEqualsObject
		 */
		public static final SourceModel.Expr notEqualsObject(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.notEqualsObject), x, y});
		}

		/**
		 * Name binding for function: notEqualsObject.
		 * @see #notEqualsObject(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName notEqualsObject = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"notEqualsObject");

		/**
		 * Helper binding method for function: notEqualsRecord. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of notEqualsRecord
		 */
		public static final SourceModel.Expr notEqualsRecord(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.notEqualsRecord), arg_1, arg_2});
		}

		/**
		 * Name binding for function: notEqualsRecord.
		 * @see #notEqualsRecord(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName notEqualsRecord = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"notEqualsRecord");

		/**
		 * Helper binding method for function: notEqualsRecordType. 
		 * @param dictionary1
		 * @param dictionary2
		 * @return the SourceModule.expr representing an application of notEqualsRecordType
		 */
		public static final SourceModel.Expr notEqualsRecordType(SourceModel.Expr dictionary1, SourceModel.Expr dictionary2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.notEqualsRecordType), dictionary1, dictionary2});
		}

		/**
		 * Name binding for function: notEqualsRecordType.
		 * @see #notEqualsRecordType(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName notEqualsRecordType = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"notEqualsRecordType");

		/**
		 * Helper binding method for function: notEqualsShort. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of notEqualsShort
		 */
		public static final SourceModel.Expr notEqualsShort(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.notEqualsShort), arg_1, arg_2});
		}

		/**
		 * @see #notEqualsShort(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of notEqualsShort
		 */
		public static final SourceModel.Expr notEqualsShort(short arg_1, short arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.notEqualsShort), SourceModel.Expr.makeShortValue(arg_1), SourceModel.Expr.makeShortValue(arg_2)});
		}

		/**
		 * Name binding for function: notEqualsShort.
		 * @see #notEqualsShort(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName notEqualsShort = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"notEqualsShort");

		/**
		 * Helper binding method for function: notEqualsTypeReps. 
		 * @param typeReps1
		 * @param typeReps2
		 * @return the SourceModule.expr representing an application of notEqualsTypeReps
		 */
		public static final SourceModel.Expr notEqualsTypeReps(SourceModel.Expr typeReps1, SourceModel.Expr typeReps2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.notEqualsTypeReps), typeReps1, typeReps2});
		}

		/**
		 * Name binding for function: notEqualsTypeReps.
		 * @see #notEqualsTypeReps(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName notEqualsTypeReps = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"notEqualsTypeReps");

		/**
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Prelude.calValueToObject
		 * </dl>
		 * 
		 * @param object (CAL type: <code>Cal.Core.Prelude.JObject</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.CalValue</code>) 
		 *          checks that <code>object</code> is indeed a handle to a <code>Cal.Core.Prelude.CalValue</code> and then returns the underlying CAL value
		 * evaluating it to weak-head normal form.
		 */
		public static final SourceModel.Expr objectToCalValue(SourceModel.Expr object) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.objectToCalValue), object});
		}

		/**
		 * Name binding for function: objectToCalValue.
		 * @see #objectToCalValue(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName objectToCalValue = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"objectToCalValue");

		/**
		 * Helper binding method for function: orderingIntEnumExamples. 
		 * @return the SourceModule.expr representing an application of orderingIntEnumExamples
		 */
		public static final SourceModel.Expr orderingIntEnumExamples() {
			return SourceModel.Expr.Var.make(Functions.orderingIntEnumExamples);
		}

		/**
		 * Name binding for function: orderingIntEnumExamples.
		 * @see #orderingIntEnumExamples()
		 */
		public static final QualifiedName orderingIntEnumExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"orderingIntEnumExamples");

		/**
		 * Helper binding method for function: orderingValue_EQ. 
		 * @return the SourceModule.expr representing an application of orderingValue_EQ
		 */
		public static final SourceModel.Expr orderingValue_EQ() {
			return SourceModel.Expr.Var.make(Functions.orderingValue_EQ);
		}

		/**
		 * Name binding for function: orderingValue_EQ.
		 * @see #orderingValue_EQ()
		 */
		public static final QualifiedName orderingValue_EQ = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"orderingValue_EQ");

		/**
		 * Helper binding method for function: orderingValue_GT. 
		 * @return the SourceModule.expr representing an application of orderingValue_GT
		 */
		public static final SourceModel.Expr orderingValue_GT() {
			return SourceModel.Expr.Var.make(Functions.orderingValue_GT);
		}

		/**
		 * Name binding for function: orderingValue_GT.
		 * @see #orderingValue_GT()
		 */
		public static final QualifiedName orderingValue_GT = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"orderingValue_GT");

		/**
		 * Helper binding method for function: orderingValue_LT. 
		 * @return the SourceModule.expr representing an application of orderingValue_LT
		 */
		public static final SourceModel.Expr orderingValue_LT() {
			return SourceModel.Expr.Var.make(Functions.orderingValue_LT);
		}

		/**
		 * Name binding for function: orderingValue_LT.
		 * @see #orderingValue_LT()
		 */
		public static final QualifiedName orderingValue_LT = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"orderingValue_LT");

		/**
		 * Helper binding method for function: orderingValue_toInt. 
		 * @param jOrderingValue
		 * @return the SourceModule.expr representing an application of orderingValue_toInt
		 */
		public static final SourceModel.Expr orderingValue_toInt(SourceModel.Expr jOrderingValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.orderingValue_toInt), jOrderingValue});
		}

		/**
		 * Name binding for function: orderingValue_toInt.
		 * @see #orderingValue_toInt(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName orderingValue_toInt = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"orderingValue_toInt");

		/**
		 * This function should remain private. It is a helper intended for internal compiler use.
		 * The reason for this is that ordinalValue breaks the encapsulation of the data type by exposing
		 * implementation details about its data constructors.
		 * <p>
		 * The ordinal value is defined as follows:
		 * <ol>
		 *  <li>
		 *   for any data constructor defined in an algebraic data declaration, it is the zero-based ordinal
		 *   within the declaration. For example, <code>Cal.Core.Prelude.LT</code> = 0, <code>Cal.Core.Prelude.EQ</code> = 1, and <code>Cal.Core.Prelude.GT</code> = 2.
		 *  </li>
		 *  <li>
		 *   for an foreign type with Java implementation type int, byte, short or char,  the value is the underlying value,
		 *   converted to an <code>Cal.Core.Prelude.Int</code>. In particular, this is true of the <code>Cal.Core.Prelude.Int</code>, <code>Cal.Core.Prelude.Byte</code>,
		 *   <code>Cal.Core.Prelude.Short</code> and <code>Cal.Core.Prelude.Char</code> types.
		 *  </li>
		 *  <li>
		 *   For the built-in <code>Cal.Core.Prelude.Boolean</code> type: <code>Cal.Core.Prelude.False</code> = 0, <code>Cal.Core.Prelude.True</code> = 1.
		 *  </li>
		 * </ol>
		 * <p>
		 * For values of other types, such as <code>Cal.Core.Prelude.Long</code>, <code>Cal.Core.Prelude.Double</code>, foreign types etc. it throws an exception.      
		 * 
		 * @param arg_1 (CAL type: <code>a</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the ordinal value.
		 */
		public static final SourceModel.Expr ordinalValue(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.ordinalValue), arg_1});
		}

		/**
		 * Name binding for function: ordinalValue.
		 * @see #ordinalValue(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName ordinalValue = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "ordinalValue");

		/**
		 * Helper binding method for function: ordinalValueExamples. 
		 * @return the SourceModule.expr representing an application of ordinalValueExamples
		 */
		public static final SourceModel.Expr ordinalValueExamples() {
			return SourceModel.Expr.Var.make(Functions.ordinalValueExamples);
		}

		/**
		 * Name binding for function: ordinalValueExamples.
		 * @see #ordinalValueExamples()
		 */
		public static final QualifiedName ordinalValueExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"ordinalValueExamples");

		/**
		 * Used to implement the <code>Cal.Core.Prelude.output</code> instance function for derived instances of
		 * <code>Cal.Core.Prelude.Outputable</code> for foreign types having implementation type "boolean".
		 * @param arg_1 (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.JBoolean</code>) 
		 */
		public static final SourceModel.Expr outputBoolean(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputBoolean), arg_1});
		}

		/**
		 * @see #outputBoolean(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of outputBoolean
		 */
		public static final SourceModel.Expr outputBoolean(boolean arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputBoolean), SourceModel.Expr.makeBooleanValue(arg_1)});
		}

		/**
		 * Name binding for function: outputBoolean.
		 * @see #outputBoolean(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName outputBoolean = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"outputBoolean");

		/**
		 * Helper binding method for function: outputBooleanToJObject. 
		 * @param booleanValue
		 * @return the SourceModule.expr representing an application of outputBooleanToJObject
		 */
		public static final SourceModel.Expr outputBooleanToJObject(SourceModel.Expr booleanValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputBooleanToJObject), booleanValue});
		}

		/**
		 * @see #outputBooleanToJObject(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param booleanValue
		 * @return the SourceModel.Expr representing an application of outputBooleanToJObject
		 */
		public static final SourceModel.Expr outputBooleanToJObject(boolean booleanValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputBooleanToJObject), SourceModel.Expr.makeBooleanValue(booleanValue)});
		}

		/**
		 * Name binding for function: outputBooleanToJObject.
		 * @see #outputBooleanToJObject(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName outputBooleanToJObject = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"outputBooleanToJObject");

		/**
		 * Used to implement the <code>Cal.Core.Prelude.output</code> instance function for derived instances of
		 * <code>Cal.Core.Prelude.Outputable</code> for foreign types having implementation type "byte".
		 * @param arg_1 (CAL type: <code>Cal.Core.Prelude.Byte</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.JByte</code>) 
		 */
		public static final SourceModel.Expr outputByte(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputByte), arg_1});
		}

		/**
		 * @see #outputByte(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of outputByte
		 */
		public static final SourceModel.Expr outputByte(byte arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputByte), SourceModel.Expr.makeByteValue(arg_1)});
		}

		/**
		 * Name binding for function: outputByte.
		 * @see #outputByte(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName outputByte = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "outputByte");

		/**
		 * Used to implement the <code>Cal.Core.Prelude.output</code> instance function for derived instances of
		 * <code>Cal.Core.Prelude.Outputable</code> for foreign types having implementation type "char".
		 * @param arg_1 (CAL type: <code>Cal.Core.Prelude.Char</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.JChar</code>) 
		 */
		public static final SourceModel.Expr outputChar(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputChar), arg_1});
		}

		/**
		 * @see #outputChar(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of outputChar
		 */
		public static final SourceModel.Expr outputChar(char arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputChar), SourceModel.Expr.makeCharValue(arg_1)});
		}

		/**
		 * Name binding for function: outputChar.
		 * @see #outputChar(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName outputChar = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "outputChar");

		/**
		 * Used to implement the <code>Cal.Core.Prelude.output</code> instance function for derived instances of
		 * <code>Cal.Core.Prelude.Outputable</code> for foreign types having implementation type "double".
		 * @param arg_1 (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.JDouble</code>) 
		 */
		public static final SourceModel.Expr outputDouble(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputDouble), arg_1});
		}

		/**
		 * @see #outputDouble(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of outputDouble
		 */
		public static final SourceModel.Expr outputDouble(double arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputDouble), SourceModel.Expr.makeDoubleValue(arg_1)});
		}

		/**
		 * Name binding for function: outputDouble.
		 * @see #outputDouble(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName outputDouble = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "outputDouble");

		/**
		 * Helper binding method for function: outputEither. 
		 * @param eitherValue
		 * @return the SourceModule.expr representing an application of outputEither
		 */
		public static final SourceModel.Expr outputEither(SourceModel.Expr eitherValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputEither), eitherValue});
		}

		/**
		 * Name binding for function: outputEither.
		 * @see #outputEither(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName outputEither = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "outputEither");

		/**
		 * Used to implement the <code>Cal.Core.Prelude.output</code> instance function for derived instances of
		 * <code>Cal.Core.Prelude.Outputable</code> for foreign types having implementation type "float".
		 * @param arg_1 (CAL type: <code>Cal.Core.Prelude.Float</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.JFloat</code>) 
		 */
		public static final SourceModel.Expr outputFloat(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputFloat), arg_1});
		}

		/**
		 * @see #outputFloat(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of outputFloat
		 */
		public static final SourceModel.Expr outputFloat(float arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputFloat), SourceModel.Expr.makeFloatValue(arg_1)});
		}

		/**
		 * Name binding for function: outputFloat.
		 * @see #outputFloat(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName outputFloat = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "outputFloat");

		/**
		 * Used to implement the <code>Cal.Core.Prelude.output</code> instance function for derived instances of
		 * <code>Cal.Core.Prelude.Outputable</code> for foreign types having implementation type "int".
		 * @param arg_1 (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.JInt</code>) 
		 */
		public static final SourceModel.Expr outputInt(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputInt), arg_1});
		}

		/**
		 * @see #outputInt(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of outputInt
		 */
		public static final SourceModel.Expr outputInt(int arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputInt), SourceModel.Expr.makeIntValue(arg_1)});
		}

		/**
		 * Name binding for function: outputInt.
		 * @see #outputInt(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName outputInt = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "outputInt");

		/**
		 * Used to implement the <code>Cal.Core.Prelude.output</code> instance function for derived instances of
		 * <code>Cal.Core.Prelude.Outputable</code> for foreign types having implementation type that is not one of the 
		 * primitive Java types char, boolean, byte, short, int, long, float or double.
		 * @param x (CAL type: <code>Cal.Core.Prelude.JObject</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.JObject</code>) 
		 */
		public static final SourceModel.Expr outputJObject(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputJObject), x});
		}

		/**
		 * Name binding for function: outputJObject.
		 * @see #outputJObject(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName outputJObject = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"outputJObject");

		/**
		 * Converts a CAL list to a Java list.
		 * <p>
		 * This function is defined in the <code>Cal.Core.Prelude</code> module for implementation purposes only. 
		 * The public api version is <code>Cal.Collections.List.outputList</code>.
		 * 
		 * @param list (CAL type: <code>Cal.Core.Prelude.Outputable a => [a]</code>)
		 *          the CAL list.
		 * @return (CAL type: <code>Cal.Core.Prelude.JList</code>) 
		 *          the corresponding Java list.
		 */
		public static final SourceModel.Expr outputList(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputList), list});
		}

		/**
		 * Name binding for function: outputList.
		 * @see #outputList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName outputList = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "outputList");

		/**
		 * Helper binding method for function: outputListToJObject. 
		 * @param list
		 * @return the SourceModule.expr representing an application of outputListToJObject
		 */
		public static final SourceModel.Expr outputListToJObject(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputListToJObject), list});
		}

		/**
		 * Name binding for function: outputListToJObject.
		 * @see #outputListToJObject(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName outputListToJObject = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"outputListToJObject");

		/**
		 * Converts a CAL list to a Java list using the element mapping function <code>f</code> of type <code>a -&gt; Cal.Core.Prelude.JObject</code> 
		 * to convert elements of the CAL list.
		 * <p>
		 * This function is defined in the <code>Cal.Core.Prelude</code> module for implementation purposes only. 
		 * The public api version is <code>Cal.Collections.List.outputListWith</code>.
		 * 
		 * @param list (CAL type: <code>[a]</code>)
		 *          the CAL list.
		 * @param f (CAL type: <code>a -> Cal.Core.Prelude.JObject</code>)
		 *          the mapping function converting elements of the list to <code>Cal.Core.Prelude.JObject</code> values.
		 * @return (CAL type: <code>Cal.Core.Prelude.JList</code>) 
		 *          the corresponding <code>Cal.Core.Prelude.JList</code>.
		 */
		public static final SourceModel.Expr outputListWith(SourceModel.Expr list, SourceModel.Expr f) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputListWith), list, f});
		}

		/**
		 * Name binding for function: outputListWith.
		 * @see #outputListWith(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName outputListWith = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"outputListWith");

		/**
		 * Used to implement the <code>Cal.Core.Prelude.output</code> instance function for derived instances of
		 * <code>Cal.Core.Prelude.Outputable</code> for foreign types having implementation type "long".
		 * @param arg_1 (CAL type: <code>Cal.Core.Prelude.Long</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.JLong</code>) 
		 */
		public static final SourceModel.Expr outputLong(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputLong), arg_1});
		}

		/**
		 * @see #outputLong(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of outputLong
		 */
		public static final SourceModel.Expr outputLong(long arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputLong), SourceModel.Expr.makeLongValue(arg_1)});
		}

		/**
		 * Name binding for function: outputLong.
		 * @see #outputLong(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName outputLong = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "outputLong");

		/**
		 * Helper binding method for function: outputMaybe. 
		 * @param maybeValue
		 * @return the SourceModule.expr representing an application of outputMaybe
		 */
		public static final SourceModel.Expr outputMaybe(SourceModel.Expr maybeValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputMaybe), maybeValue});
		}

		/**
		 * Name binding for function: outputMaybe.
		 * @see #outputMaybe(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName outputMaybe = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "outputMaybe");

		/**
		 * Helper binding method for function: outputOrdering. 
		 * @param orderingValue
		 * @return the SourceModule.expr representing an application of outputOrdering
		 */
		public static final SourceModel.Expr outputOrdering(SourceModel.Expr orderingValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputOrdering), orderingValue});
		}

		/**
		 * Name binding for function: outputOrdering.
		 * @see #outputOrdering(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName outputOrdering = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"outputOrdering");

		/**
		 * Helper binding method for function: outputRecord. 
		 * @param record
		 * @return the SourceModule.expr representing an application of outputRecord
		 */
		public static final SourceModel.Expr outputRecord(SourceModel.Expr record) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputRecord), record});
		}

		/**
		 * Name binding for function: outputRecord.
		 * @see #outputRecord(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName outputRecord = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "outputRecord");

		/**
		 * Used to implement the <code>Cal.Core.Prelude.output</code> instance function for derived instances of
		 * <code>Cal.Core.Prelude.Outputable</code> for foreign types having implementation type "short".
		 * @param arg_1 (CAL type: <code>Cal.Core.Prelude.Short</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.JShort</code>) 
		 */
		public static final SourceModel.Expr outputShort(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputShort), arg_1});
		}

		/**
		 * @see #outputShort(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of outputShort
		 */
		public static final SourceModel.Expr outputShort(short arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputShort), SourceModel.Expr.makeShortValue(arg_1)});
		}

		/**
		 * Name binding for function: outputShort.
		 * @see #outputShort(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName outputShort = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "outputShort");

		/**
		 * Helper binding method for function: outputUnit. 
		 * @param value
		 * @return the SourceModule.expr representing an application of outputUnit
		 */
		public static final SourceModel.Expr outputUnit(SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputUnit), value});
		}

		/**
		 * Name binding for function: outputUnit.
		 * @see #outputUnit(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName outputUnit = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "outputUnit");

		/**
		 * The CAL record obtained by applying <code>Cal.Core.Prelude.input</code> to each element of the Java list. The nth element of the Java list is
		 * mapped to the nth field (in field-name order) in the CAL record. For example, if the Java list is <code>[x1, x2, x3]</code>, then the resulting
		 * CAL record is <code>{f1 = input x1, f2 = input x2, f3 = input x3}</code>. Note that the length of the Java list must be the same as the number
		 * of fields in the CAL record or an exception results. This condition is mainly intended to help users catch bugs in their code
		 * at an early stage.
		 * @param javaList (CAL type: <code>Cal.Core.Prelude.JList</code>)
		 *          must be a <code>java.util.List</code> of <code>java.lang.Object</code> values of the appropriate Java sub-types for marshaling to
		 * the result record.
		 * @return (CAL type: <code>Cal.Core.Prelude.Inputable r => {r}</code>) 
		 *          the CAL record obtained by applying <code>Cal.Core.Prelude.input</code> to each element of the Java list.
		 */
		public static final SourceModel.Expr recordFromJListPrimitive(SourceModel.Expr javaList) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.recordFromJListPrimitive), javaList});
		}

		/**
		 * Name binding for function: recordFromJListPrimitive.
		 * @see #recordFromJListPrimitive(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName recordFromJListPrimitive = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"recordFromJListPrimitive");

		/**
		 * The CAL record obtained by applying <code>Cal.Core.Prelude.input</code> to each value of the Java map from <code>org.openquark.cal.compiler.FieldName</code>
		 * to <code>java.lang.Object</code>. The nth element of the Java map (in field-name order) is mapped to the nth field (in field-name order) in
		 * the CAL record. For example, if the Java map is <code>{f1 = x1, f2 = x2, f3 = x3}</code>, then the resulting CAL record is
		 * <code>{f1 = input x1, f2 = input x2, f3 = input x3}</code>. Note that the size of the Java map must be the same as the number of fields 
		 * in the CAL record or an exception results. Similarly the field-names of the Java map must exactly match the field-names in the CAL record.
		 * These conditions are mainly intended to help users catch bugs in their code at an early stage.
		 * @param javaMap (CAL type: <code>Cal.Core.Prelude.JMap</code>)
		 *          must be a <code>java.util.Map</code> of <code>org.openquark.cal.compiler.FieldName</code> to <code>java.lang.Object</code>.
		 * @return (CAL type: <code>Cal.Core.Prelude.Inputable r => {r}</code>) 
		 *          the CAL record obtained by applying <code>Cal.Core.Prelude.input</code> to each value of the Java map.
		 */
		public static final SourceModel.Expr recordFromJMapPrimitive(SourceModel.Expr javaMap) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.recordFromJMapPrimitive), javaMap});
		}

		/**
		 * Name binding for function: recordFromJMapPrimitive.
		 * @see #recordFromJMapPrimitive(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName recordFromJMapPrimitive = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"recordFromJMapPrimitive");

		/**
		 * Returns a <code>java.util.List</code> value whose elements are obtained by applying <code>Cal.Core.Prelude.input</code> to each field in the CAL record 
		 * in field-name order. For example, if the CAL record is {f1 = x1, f2 = x2, f3 = x3}, then the resulting Java list
		 * would be [output x1, output x2, output x3].
		 * @param record (CAL type: <code>Cal.Core.Prelude.Outputable r => {r}</code>)
		 *          CAL record to marshal to a Java list
		 * @return (CAL type: <code>Cal.Core.Prelude.JList</code>) 
		 *          a Java list whose elements are obtained by applying <code>Cal.Core.Prelude.input</code> to each field in the CAL record in field-name order.
		 */
		public static final SourceModel.Expr recordToJListPrimitive(SourceModel.Expr record) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.recordToJListPrimitive), record});
		}

		/**
		 * Name binding for function: recordToJListPrimitive.
		 * @see #recordToJListPrimitive(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName recordToJListPrimitive = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"recordToJListPrimitive");

		/**
		 * Extracts the machine-internal type dictionary from a record.  This is used internally to do fast type operations
		 * on record values.
		 * @param arg_1 (CAL type: <code>Cal.Core.Prelude.Typeable r => {r}</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.RecordType</code>) 
		 */
		public static final SourceModel.Expr recordTypeDictionary(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.recordTypeDictionary), arg_1});
		}

		/**
		 * Name binding for function: recordTypeDictionary.
		 * @see #recordTypeDictionary(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName recordTypeDictionary = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"recordTypeDictionary");

		/**
		 * 
		 * @param dictionary (CAL type: <code>Cal.Core.Prelude.RecordType</code>)
		 *          a <code>Cal.Core.Prelude.RecordType</code> representing the type of a record value
		 * @return (CAL type: <code>[Cal.Core.Prelude.TypeRep]</code>) 
		 *          A list of <code>Cal.Core.Prelude.TypeRep</code>s representing the types of each field of the record value.
		 * The 0th element of the list represents the type of the 0th field of the record,
		 * and so forth.
		 */
		public static final SourceModel.Expr recordType_fieldTypes(SourceModel.Expr dictionary) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.recordType_fieldTypes), dictionary});
		}

		/**
		 * Name binding for function: recordType_fieldTypes.
		 * @see #recordType_fieldTypes(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName recordType_fieldTypes = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"recordType_fieldTypes");

		/**
		 * 
		 * @param recordType (CAL type: <code>Cal.Core.Prelude.RecordType</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          The number of fields that the record represented by the <code>Cal.Core.Prelude.RecordType</code> contains.
		 */
		public static final SourceModel.Expr recordType_getNFields(SourceModel.Expr recordType) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.recordType_getNFields), recordType});
		}

		/**
		 * Name binding for function: recordType_getNFields.
		 * @see #recordType_getNFields(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName recordType_getNFields = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"recordType_getNFields");

		/**
		 * 
		 * @param recordType (CAL type: <code>Cal.Core.Prelude.RecordType</code>)
		 * @param arg_2 (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.FieldName</code>) 
		 *          The name of the nth field of the record whose type is represented by the <code>Cal.Core.Prelude.RecordType</code>.
		 */
		public static final SourceModel.Expr recordType_getNthFieldName(SourceModel.Expr recordType, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.recordType_getNthFieldName), recordType, arg_2});
		}

		/**
		 * @see #recordType_getNthFieldName(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param recordType
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of recordType_getNthFieldName
		 */
		public static final SourceModel.Expr recordType_getNthFieldName(SourceModel.Expr recordType, int arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.recordType_getNthFieldName), recordType, SourceModel.Expr.makeIntValue(arg_2)});
		}

		/**
		 * Name binding for function: recordType_getNthFieldName.
		 * @see #recordType_getNthFieldName(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName recordType_getNthFieldName = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"recordType_getNthFieldName");

		/**
		 * 
		 * @param dictionary (CAL type: <code>Cal.Core.Prelude.RecordType</code>)
		 *          A <code>Cal.Core.Prelude.RecordType</code> that represents the type of a record value
		 * @param index (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          A 0-based index indicating the field to retrieve the <code>Cal.Core.Prelude.TypeRep</code> for
		 * @return (CAL type: <code>Cal.Core.Prelude.TypeRep</code>) 
		 *          a <code>Cal.Core.Prelude.TypeRep</code> representing the type of the nth field in the record whose type
		 * is represented by dictionary.
		 */
		public static final SourceModel.Expr recordType_getNthFieldType(SourceModel.Expr dictionary, SourceModel.Expr index) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.recordType_getNthFieldType), dictionary, index});
		}

		/**
		 * @see #recordType_getNthFieldType(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param dictionary
		 * @param index
		 * @return the SourceModel.Expr representing an application of recordType_getNthFieldType
		 */
		public static final SourceModel.Expr recordType_getNthFieldType(SourceModel.Expr dictionary, int index) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.recordType_getNthFieldType), dictionary, SourceModel.Expr.makeIntValue(index)});
		}

		/**
		 * Name binding for function: recordType_getNthFieldType.
		 * @see #recordType_getNthFieldType(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName recordType_getNthFieldType = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"recordType_getNthFieldType");

		/**
		 * 
		 * @param recordType (CAL type: <code>Cal.Core.Prelude.RecordType</code>)
		 * @param arg_2 (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.CalValue</code>) 
		 *          The nth value contained by the <code>Cal.Core.Prelude.RecordType</code> object.  This will be an internal CAL value representing a function
		 * that accepts a single, ignored argument and returns a <code>Cal.Core.Prelude.TypeRep</code> representing the type of
		 * the nth field of the record represented by the <code>Cal.Core.Prelude.RecordType</code>.
		 */
		public static final SourceModel.Expr recordType_getNthFieldValue(SourceModel.Expr recordType, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.recordType_getNthFieldValue), recordType, arg_2});
		}

		/**
		 * @see #recordType_getNthFieldValue(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param recordType
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of recordType_getNthFieldValue
		 */
		public static final SourceModel.Expr recordType_getNthFieldValue(SourceModel.Expr recordType, int arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.recordType_getNthFieldValue), recordType, SourceModel.Expr.makeIntValue(arg_2)});
		}

		/**
		 * Name binding for function: recordType_getNthFieldValue.
		 * @see #recordType_getNthFieldValue(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName recordType_getNthFieldValue = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"recordType_getNthFieldValue");

		/**
		 * 
		 * @param recordType (CAL type: <code>Cal.Core.Prelude.RecordType</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the record whose type is represented by the <code>Cal.Core.Prelude.RecordType</code> is a tuple with 2 or more
		 * fields, and <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr recordType_isTuple2OrMoreRecord(SourceModel.Expr recordType) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.recordType_isTuple2OrMoreRecord), recordType});
		}

		/**
		 * Name binding for function: recordType_isTuple2OrMoreRecord.
		 * @see #recordType_isTuple2OrMoreRecord(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName recordType_isTuple2OrMoreRecord = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"recordType_isTuple2OrMoreRecord");

		/**
		 * 
		 * @param recordType (CAL type: <code>Cal.Core.Prelude.RecordType</code>)
		 * @param arg_2 (CAL type: <code>Cal.Core.Prelude.RecordType</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the two <code>Cal.Core.Prelude.RecordType</code>s have the same set of field names (not necessarily field types).
		 * Used internally to implemented <code>Cal.Core.Prelude.sameRootType</code> for records.
		 */
		public static final SourceModel.Expr recordType_sameFields(SourceModel.Expr recordType, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.recordType_sameFields), recordType, arg_2});
		}

		/**
		 * Name binding for function: recordType_sameFields.
		 * @see #recordType_sameFields(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName recordType_sameFields = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"recordType_sameFields");

		/**
		 * Helper binding method for function: remainderByte. 
		 * @param x
		 * @param y
		 * @return the SourceModule.expr representing an application of remainderByte
		 */
		public static final SourceModel.Expr remainderByte(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.remainderByte), x, y});
		}

		/**
		 * @see #remainderByte(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @param y
		 * @return the SourceModel.Expr representing an application of remainderByte
		 */
		public static final SourceModel.Expr remainderByte(byte x, byte y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.remainderByte), SourceModel.Expr.makeByteValue(x), SourceModel.Expr.makeByteValue(y)});
		}

		/**
		 * Name binding for function: remainderByte.
		 * @see #remainderByte(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName remainderByte = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"remainderByte");

		/**
		 * Helper binding method for function: remainderDecimal. 
		 * @param decimal
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of remainderDecimal
		 */
		public static final SourceModel.Expr remainderDecimal(SourceModel.Expr decimal, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.remainderDecimal), decimal, arg_2});
		}

		/**
		 * Name binding for function: remainderDecimal.
		 * @see #remainderDecimal(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName remainderDecimal = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"remainderDecimal");

		/**
		 * Helper binding method for function: remainderDouble. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of remainderDouble
		 */
		public static final SourceModel.Expr remainderDouble(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.remainderDouble), arg_1, arg_2});
		}

		/**
		 * @see #remainderDouble(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of remainderDouble
		 */
		public static final SourceModel.Expr remainderDouble(double arg_1, double arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.remainderDouble), SourceModel.Expr.makeDoubleValue(arg_1), SourceModel.Expr.makeDoubleValue(arg_2)});
		}

		/**
		 * Name binding for function: remainderDouble.
		 * @see #remainderDouble(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName remainderDouble = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"remainderDouble");

		/**
		 * Helper binding method for function: remainderFloat. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of remainderFloat
		 */
		public static final SourceModel.Expr remainderFloat(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.remainderFloat), arg_1, arg_2});
		}

		/**
		 * @see #remainderFloat(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of remainderFloat
		 */
		public static final SourceModel.Expr remainderFloat(float arg_1, float arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.remainderFloat), SourceModel.Expr.makeFloatValue(arg_1), SourceModel.Expr.makeFloatValue(arg_2)});
		}

		/**
		 * Name binding for function: remainderFloat.
		 * @see #remainderFloat(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName remainderFloat = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"remainderFloat");

		/**
		 * Helper binding method for function: remainderInt. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of remainderInt
		 */
		public static final SourceModel.Expr remainderInt(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.remainderInt), arg_1, arg_2});
		}

		/**
		 * @see #remainderInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of remainderInt
		 */
		public static final SourceModel.Expr remainderInt(int arg_1, int arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.remainderInt), SourceModel.Expr.makeIntValue(arg_1), SourceModel.Expr.makeIntValue(arg_2)});
		}

		/**
		 * Name binding for function: remainderInt.
		 * @see #remainderInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName remainderInt = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "remainderInt");

		/**
		 * Helper binding method for function: remainderInteger. 
		 * @param integer
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of remainderInteger
		 */
		public static final SourceModel.Expr remainderInteger(SourceModel.Expr integer, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.remainderInteger), integer, arg_2});
		}

		/**
		 * Name binding for function: remainderInteger.
		 * @see #remainderInteger(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName remainderInteger = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"remainderInteger");

		/**
		 * Helper binding method for function: remainderLong. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of remainderLong
		 */
		public static final SourceModel.Expr remainderLong(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.remainderLong), arg_1, arg_2});
		}

		/**
		 * @see #remainderLong(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of remainderLong
		 */
		public static final SourceModel.Expr remainderLong(long arg_1, long arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.remainderLong), SourceModel.Expr.makeLongValue(arg_1), SourceModel.Expr.makeLongValue(arg_2)});
		}

		/**
		 * Name binding for function: remainderLong.
		 * @see #remainderLong(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName remainderLong = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"remainderLong");

		/**
		 * Helper binding method for function: remainderShort. 
		 * @param x
		 * @param y
		 * @return the SourceModule.expr representing an application of remainderShort
		 */
		public static final SourceModel.Expr remainderShort(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.remainderShort), x, y});
		}

		/**
		 * @see #remainderShort(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @param y
		 * @return the SourceModel.Expr representing an application of remainderShort
		 */
		public static final SourceModel.Expr remainderShort(short x, short y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.remainderShort), SourceModel.Expr.makeShortValue(x), SourceModel.Expr.makeShortValue(y)});
		}

		/**
		 * Name binding for function: remainderShort.
		 * @see #remainderShort(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName remainderShort = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"remainderShort");

		/**
		 * <code>repeat valueToRepeat</code> returns the infinite list <code>[valueToRepeat, valueToRepeat, valueToRepeat, ...]</code>.
		 * <p>
		 * This function is defined in the <code>Cal.Core.Prelude</code> module for implementation purposes only. 
		 * The public api version is <code>Cal.Collections.List.repeat</code>.
		 * 
		 * @param valueToRepeat (CAL type: <code>a</code>)
		 *          the value to be repeated.
		 * @return (CAL type: <code>[a]</code>) 
		 *          the infinite list <code>[valueToRepeat, valueToRepeat, valueToRepeat, ...]</code>
		 */
		public static final SourceModel.Expr repeat(SourceModel.Expr valueToRepeat) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.repeat), valueToRepeat});
		}

		/**
		 * Name binding for function: repeat.
		 * @see #repeat(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName repeat = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "repeat");

		/**
		 * <code>replicate nCopies valueToReplicate</code> is a list of length <code>nCopies</code>, with every element equal to
		 * <code>valueToReplicate</code>.
		 * <p>
		 * This function is defined in the <code>Cal.Core.Prelude</code> module for implementation purposes only. 
		 * The public api version is <code>Cal.Collections.List.replicate</code>.
		 * 
		 * @param nCopies (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the number of copies.
		 * @param valueToReplicate (CAL type: <code>a</code>)
		 *          the value to be replicated.
		 * @return (CAL type: <code>[a]</code>) 
		 *          a list of length <code>nCopies</code>, with every element equal to <code>valueToReplicate</code>.
		 */
		public static final SourceModel.Expr replicate(SourceModel.Expr nCopies, SourceModel.Expr valueToReplicate) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.replicate), nCopies, valueToReplicate});
		}

		/**
		 * @see #replicate(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param nCopies
		 * @param valueToReplicate
		 * @return the SourceModel.Expr representing an application of replicate
		 */
		public static final SourceModel.Expr replicate(int nCopies, SourceModel.Expr valueToReplicate) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.replicate), SourceModel.Expr.makeIntValue(nCopies), valueToReplicate});
		}

		/**
		 * Name binding for function: replicate.
		 * @see #replicate(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName replicate = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "replicate");

		/**
		 * Replicates a list for a specified number of times.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list.
		 * @param nCopies (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the number of copies to make.
		 * @return (CAL type: <code>[a]</code>) 
		 *          a list of <code>nCopies</code> of the list concatenated together.
		 * @deprecated use <code>Cal.Collections.List.replicateList</code>
		 */
		public static final SourceModel.Expr replicateList(SourceModel.Expr list, SourceModel.Expr nCopies) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.replicateList), list, nCopies});
		}

		/**
		 * @see #replicateList(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param list
		 * @param nCopies
		 * @return the SourceModel.Expr representing an application of replicateList
		 */
		public static final SourceModel.Expr replicateList(SourceModel.Expr list, int nCopies) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.replicateList), list, SourceModel.Expr.makeIntValue(nCopies)});
		}

		/**
		 * Name binding for function: replicateList.
		 * @see #replicateList(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName replicateList = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"replicateList");

		/**
		 * Helper binding method for function: sameRootTypeExamples. 
		 * @return the SourceModule.expr representing an application of sameRootTypeExamples
		 */
		public static final SourceModel.Expr sameRootTypeExamples() {
			return SourceModel.Expr.Var.make(Functions.sameRootTypeExamples);
		}

		/**
		 * Name binding for function: sameRootTypeExamples.
		 * @see #sameRootTypeExamples()
		 */
		public static final QualifiedName sameRootTypeExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"sameRootTypeExamples");

		/**
		 * Helper binding method for function: shortIntEnumExamples. 
		 * @return the SourceModule.expr representing an application of shortIntEnumExamples
		 */
		public static final SourceModel.Expr shortIntEnumExamples() {
			return SourceModel.Expr.Var.make(Functions.shortIntEnumExamples);
		}

		/**
		 * Name binding for function: shortIntEnumExamples.
		 * @see #shortIntEnumExamples()
		 */
		public static final QualifiedName shortIntEnumExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"shortIntEnumExamples");

		/**
		 * Helper binding method for function: shortToByte. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of shortToByte
		 */
		public static final SourceModel.Expr shortToByte(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortToByte), arg_1});
		}

		/**
		 * @see #shortToByte(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of shortToByte
		 */
		public static final SourceModel.Expr shortToByte(short arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortToByte), SourceModel.Expr.makeShortValue(arg_1)});
		}

		/**
		 * Name binding for function: shortToByte.
		 * @see #shortToByte(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shortToByte = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "shortToByte");

		/**
		 * Helper binding method for function: shortToChar. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of shortToChar
		 */
		public static final SourceModel.Expr shortToChar(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortToChar), arg_1});
		}

		/**
		 * @see #shortToChar(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of shortToChar
		 */
		public static final SourceModel.Expr shortToChar(short arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortToChar), SourceModel.Expr.makeShortValue(arg_1)});
		}

		/**
		 * Name binding for function: shortToChar.
		 * @see #shortToChar(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shortToChar = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "shortToChar");

		/**
		 * Helper binding method for function: shortToDecimal. 
		 * @param shortValue
		 * @return the SourceModule.expr representing an application of shortToDecimal
		 */
		public static final SourceModel.Expr shortToDecimal(SourceModel.Expr shortValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortToDecimal), shortValue});
		}

		/**
		 * @see #shortToDecimal(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param shortValue
		 * @return the SourceModel.Expr representing an application of shortToDecimal
		 */
		public static final SourceModel.Expr shortToDecimal(short shortValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortToDecimal), SourceModel.Expr.makeShortValue(shortValue)});
		}

		/**
		 * Name binding for function: shortToDecimal.
		 * @see #shortToDecimal(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shortToDecimal = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"shortToDecimal");

		/**
		 * Helper binding method for function: shortToDouble. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of shortToDouble
		 */
		public static final SourceModel.Expr shortToDouble(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortToDouble), arg_1});
		}

		/**
		 * @see #shortToDouble(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of shortToDouble
		 */
		public static final SourceModel.Expr shortToDouble(short arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortToDouble), SourceModel.Expr.makeShortValue(arg_1)});
		}

		/**
		 * Name binding for function: shortToDouble.
		 * @see #shortToDouble(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shortToDouble = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"shortToDouble");

		/**
		 * Helper binding method for function: shortToFloat. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of shortToFloat
		 */
		public static final SourceModel.Expr shortToFloat(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortToFloat), arg_1});
		}

		/**
		 * @see #shortToFloat(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of shortToFloat
		 */
		public static final SourceModel.Expr shortToFloat(short arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortToFloat), SourceModel.Expr.makeShortValue(arg_1)});
		}

		/**
		 * Name binding for function: shortToFloat.
		 * @see #shortToFloat(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shortToFloat = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "shortToFloat");

		/**
		 * Helper binding method for function: shortToInt. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of shortToInt
		 */
		public static final SourceModel.Expr shortToInt(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortToInt), arg_1});
		}

		/**
		 * @see #shortToInt(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of shortToInt
		 */
		public static final SourceModel.Expr shortToInt(short arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortToInt), SourceModel.Expr.makeShortValue(arg_1)});
		}

		/**
		 * Name binding for function: shortToInt.
		 * @see #shortToInt(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shortToInt = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "shortToInt");

		/**
		 * Helper binding method for function: shortToInteger. 
		 * @param shortValue
		 * @return the SourceModule.expr representing an application of shortToInteger
		 */
		public static final SourceModel.Expr shortToInteger(SourceModel.Expr shortValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortToInteger), shortValue});
		}

		/**
		 * @see #shortToInteger(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param shortValue
		 * @return the SourceModel.Expr representing an application of shortToInteger
		 */
		public static final SourceModel.Expr shortToInteger(short shortValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortToInteger), SourceModel.Expr.makeShortValue(shortValue)});
		}

		/**
		 * Name binding for function: shortToInteger.
		 * @see #shortToInteger(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shortToInteger = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"shortToInteger");

		/**
		 * Helper binding method for function: shortToLong. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of shortToLong
		 */
		public static final SourceModel.Expr shortToLong(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortToLong), arg_1});
		}

		/**
		 * @see #shortToLong(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of shortToLong
		 */
		public static final SourceModel.Expr shortToLong(short arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortToLong), SourceModel.Expr.makeShortValue(arg_1)});
		}

		/**
		 * Name binding for function: shortToLong.
		 * @see #shortToLong(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shortToLong = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "shortToLong");

		/**
		 * Helper binding method for function: shortToShort. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of shortToShort
		 */
		public static final SourceModel.Expr shortToShort(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortToShort), arg_1});
		}

		/**
		 * @see #shortToShort(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of shortToShort
		 */
		public static final SourceModel.Expr shortToShort(short arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shortToShort), SourceModel.Expr.makeShortValue(arg_1)});
		}

		/**
		 * Name binding for function: shortToShort.
		 * @see #shortToShort(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shortToShort = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "shortToShort");

		/**
		 * Helper binding method for function: signumByte. 
		 * @param x
		 * @return the SourceModule.expr representing an application of signumByte
		 */
		public static final SourceModel.Expr signumByte(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.signumByte), x});
		}

		/**
		 * @see #signumByte(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @return the SourceModel.Expr representing an application of signumByte
		 */
		public static final SourceModel.Expr signumByte(byte x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.signumByte), SourceModel.Expr.makeByteValue(x)});
		}

		/**
		 * Name binding for function: signumByte.
		 * @see #signumByte(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName signumByte = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "signumByte");

		/**
		 * Helper binding method for function: signumDecimal. 
		 * @param x
		 * @return the SourceModule.expr representing an application of signumDecimal
		 */
		public static final SourceModel.Expr signumDecimal(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.signumDecimal), x});
		}

		/**
		 * Name binding for function: signumDecimal.
		 * @see #signumDecimal(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName signumDecimal = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"signumDecimal");

		/**
		 * Helper binding method for function: signumDecimalAsInt. 
		 * @param decimal
		 * @return the SourceModule.expr representing an application of signumDecimalAsInt
		 */
		public static final SourceModel.Expr signumDecimalAsInt(SourceModel.Expr decimal) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.signumDecimalAsInt), decimal});
		}

		/**
		 * Name binding for function: signumDecimalAsInt.
		 * @see #signumDecimalAsInt(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName signumDecimalAsInt = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"signumDecimalAsInt");

		/**
		 * There are some tricky aspects here in how NaN and -0 are handled.
		 * <p>
		 * signum (positive double) --&gt; 1.0
		 * signum (negative double) --&gt; -1.0
		 * signum (0) --&gt; 0
		 * <p>
		 * special cases:
		 * signum (NaN) --&gt; NaN
		 * signum (-0.0) --&gt; -0.0
		 * 
		 * @param x (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 *          double value
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 *          signum of the double value x
		 */
		public static final SourceModel.Expr signumDouble(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.signumDouble), x});
		}

		/**
		 * @see #signumDouble(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @return the SourceModel.Expr representing an application of signumDouble
		 */
		public static final SourceModel.Expr signumDouble(double x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.signumDouble), SourceModel.Expr.makeDoubleValue(x)});
		}

		/**
		 * Name binding for function: signumDouble.
		 * @see #signumDouble(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName signumDouble = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "signumDouble");

		/**
		 * Helper binding method for function: signumFloat. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of signumFloat
		 */
		public static final SourceModel.Expr signumFloat(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.signumFloat), arg_1});
		}

		/**
		 * @see #signumFloat(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of signumFloat
		 */
		public static final SourceModel.Expr signumFloat(float arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.signumFloat), SourceModel.Expr.makeFloatValue(arg_1)});
		}

		/**
		 * Name binding for function: signumFloat.
		 * @see #signumFloat(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName signumFloat = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "signumFloat");

		/**
		 * Helper binding method for function: signumInt. 
		 * @param x
		 * @return the SourceModule.expr representing an application of signumInt
		 */
		public static final SourceModel.Expr signumInt(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.signumInt), x});
		}

		/**
		 * @see #signumInt(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @return the SourceModel.Expr representing an application of signumInt
		 */
		public static final SourceModel.Expr signumInt(int x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.signumInt), SourceModel.Expr.makeIntValue(x)});
		}

		/**
		 * Name binding for function: signumInt.
		 * @see #signumInt(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName signumInt = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "signumInt");

		/**
		 * similar to <code>Cal.Core.Prelude.intToOrdering</code> but only handles the intValues -1, 0, 1. This is an optimization for some well-used
		 * Prelude functions implemented in terms of Java primitives where the underlying Java primitive is known to return one of
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
				CAL_Prelude_internal.MODULE_NAME, 
				"signumIntToOrdering");

		/**
		 * Helper binding method for function: signumInteger. 
		 * @param x
		 * @return the SourceModule.expr representing an application of signumInteger
		 */
		public static final SourceModel.Expr signumInteger(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.signumInteger), x});
		}

		/**
		 * Name binding for function: signumInteger.
		 * @see #signumInteger(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName signumInteger = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"signumInteger");

		/**
		 * Helper binding method for function: signumIntegerAsInt. 
		 * @param integer
		 * @return the SourceModule.expr representing an application of signumIntegerAsInt
		 */
		public static final SourceModel.Expr signumIntegerAsInt(SourceModel.Expr integer) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.signumIntegerAsInt), integer});
		}

		/**
		 * Name binding for function: signumIntegerAsInt.
		 * @see #signumIntegerAsInt(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName signumIntegerAsInt = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"signumIntegerAsInt");

		/**
		 * Helper binding method for function: signumLong. 
		 * @param x
		 * @return the SourceModule.expr representing an application of signumLong
		 */
		public static final SourceModel.Expr signumLong(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.signumLong), x});
		}

		/**
		 * @see #signumLong(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @return the SourceModel.Expr representing an application of signumLong
		 */
		public static final SourceModel.Expr signumLong(long x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.signumLong), SourceModel.Expr.makeLongValue(x)});
		}

		/**
		 * Name binding for function: signumLong.
		 * @see #signumLong(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName signumLong = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "signumLong");

		/**
		 * Helper binding method for function: signumShort. 
		 * @param x
		 * @return the SourceModule.expr representing an application of signumShort
		 */
		public static final SourceModel.Expr signumShort(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.signumShort), x});
		}

		/**
		 * @see #signumShort(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @return the SourceModel.Expr representing an application of signumShort
		 */
		public static final SourceModel.Expr signumShort(short x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.signumShort), SourceModel.Expr.makeShortValue(x)});
		}

		/**
		 * Name binding for function: signumShort.
		 * @see #signumShort(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName signumShort = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "signumShort");

		/**
		 * Helper binding method for function: subscriptTypeReps. 
		 * @param typeReps
		 * @param index
		 * @return the SourceModule.expr representing an application of subscriptTypeReps
		 */
		public static final SourceModel.Expr subscriptTypeReps(SourceModel.Expr typeReps, SourceModel.Expr index) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.subscriptTypeReps), typeReps, index});
		}

		/**
		 * @see #subscriptTypeReps(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param typeReps
		 * @param index
		 * @return the SourceModel.Expr representing an application of subscriptTypeReps
		 */
		public static final SourceModel.Expr subscriptTypeReps(SourceModel.Expr typeReps, int index) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.subscriptTypeReps), typeReps, SourceModel.Expr.makeIntValue(index)});
		}

		/**
		 * Name binding for function: subscriptTypeReps.
		 * @see #subscriptTypeReps(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName subscriptTypeReps = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"subscriptTypeReps");

		/**
		 * Helper binding method for function: subtractByte. 
		 * @param x
		 * @param y
		 * @return the SourceModule.expr representing an application of subtractByte
		 */
		public static final SourceModel.Expr subtractByte(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.subtractByte), x, y});
		}

		/**
		 * @see #subtractByte(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @param y
		 * @return the SourceModel.Expr representing an application of subtractByte
		 */
		public static final SourceModel.Expr subtractByte(byte x, byte y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.subtractByte), SourceModel.Expr.makeByteValue(x), SourceModel.Expr.makeByteValue(y)});
		}

		/**
		 * Name binding for function: subtractByte.
		 * @see #subtractByte(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName subtractByte = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "subtractByte");

		/**
		 * Helper binding method for function: subtractDecimal. 
		 * @param decimal
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of subtractDecimal
		 */
		public static final SourceModel.Expr subtractDecimal(SourceModel.Expr decimal, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.subtractDecimal), decimal, arg_2});
		}

		/**
		 * Name binding for function: subtractDecimal.
		 * @see #subtractDecimal(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName subtractDecimal = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"subtractDecimal");

		/**
		 * Helper binding method for function: subtractDouble. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of subtractDouble
		 */
		public static final SourceModel.Expr subtractDouble(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.subtractDouble), arg_1, arg_2});
		}

		/**
		 * @see #subtractDouble(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of subtractDouble
		 */
		public static final SourceModel.Expr subtractDouble(double arg_1, double arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.subtractDouble), SourceModel.Expr.makeDoubleValue(arg_1), SourceModel.Expr.makeDoubleValue(arg_2)});
		}

		/**
		 * Name binding for function: subtractDouble.
		 * @see #subtractDouble(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName subtractDouble = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"subtractDouble");

		/**
		 * Helper binding method for function: subtractFloat. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of subtractFloat
		 */
		public static final SourceModel.Expr subtractFloat(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.subtractFloat), arg_1, arg_2});
		}

		/**
		 * @see #subtractFloat(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of subtractFloat
		 */
		public static final SourceModel.Expr subtractFloat(float arg_1, float arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.subtractFloat), SourceModel.Expr.makeFloatValue(arg_1), SourceModel.Expr.makeFloatValue(arg_2)});
		}

		/**
		 * Name binding for function: subtractFloat.
		 * @see #subtractFloat(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName subtractFloat = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"subtractFloat");

		/**
		 * Helper binding method for function: subtractInt. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of subtractInt
		 */
		public static final SourceModel.Expr subtractInt(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.subtractInt), arg_1, arg_2});
		}

		/**
		 * @see #subtractInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of subtractInt
		 */
		public static final SourceModel.Expr subtractInt(int arg_1, int arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.subtractInt), SourceModel.Expr.makeIntValue(arg_1), SourceModel.Expr.makeIntValue(arg_2)});
		}

		/**
		 * Name binding for function: subtractInt.
		 * @see #subtractInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName subtractInt = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "subtractInt");

		/**
		 * Helper binding method for function: subtractInteger. 
		 * @param integer
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of subtractInteger
		 */
		public static final SourceModel.Expr subtractInteger(SourceModel.Expr integer, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.subtractInteger), integer, arg_2});
		}

		/**
		 * Name binding for function: subtractInteger.
		 * @see #subtractInteger(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName subtractInteger = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"subtractInteger");

		/**
		 * Helper binding method for function: subtractLong. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of subtractLong
		 */
		public static final SourceModel.Expr subtractLong(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.subtractLong), arg_1, arg_2});
		}

		/**
		 * @see #subtractLong(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of subtractLong
		 */
		public static final SourceModel.Expr subtractLong(long arg_1, long arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.subtractLong), SourceModel.Expr.makeLongValue(arg_1), SourceModel.Expr.makeLongValue(arg_2)});
		}

		/**
		 * Name binding for function: subtractLong.
		 * @see #subtractLong(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName subtractLong = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "subtractLong");

		/**
		 * Helper binding method for function: subtractShort. 
		 * @param x
		 * @param y
		 * @return the SourceModule.expr representing an application of subtractShort
		 */
		public static final SourceModel.Expr subtractShort(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.subtractShort), x, y});
		}

		/**
		 * @see #subtractShort(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @param y
		 * @return the SourceModel.Expr representing an application of subtractShort
		 */
		public static final SourceModel.Expr subtractShort(short x, short y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.subtractShort), SourceModel.Expr.makeShortValue(x), SourceModel.Expr.makeShortValue(y)});
		}

		/**
		 * Name binding for function: subtractShort.
		 * @see #subtractShort(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName subtractShort = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"subtractShort");

		/**
		 * <code>take nElements list</code> returns a list consisting of the first <code>nElements</code> elements of <code>list</code>.
		 * If the list has fewer than <code>nElements</code> elements, it just returns the list.
		 * <p>
		 * This function is defined in the <code>Cal.Core.Prelude</code> module for implementation purposes only. 
		 * The public api version is <code>Cal.Collections.List.take</code>.
		 * 
		 * @param nElements (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the number of elements to take.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list from which elements are to be taken.
		 * @return (CAL type: <code>[a]</code>) 
		 *          a list of the requested elements from the list.
		 */
		public static final SourceModel.Expr take(SourceModel.Expr nElements, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.take), nElements, list});
		}

		/**
		 * @see #take(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param nElements
		 * @param list
		 * @return the SourceModel.Expr representing an application of take
		 */
		public static final SourceModel.Expr take(int nElements, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.take), SourceModel.Expr.makeIntValue(nElements), list});
		}

		/**
		 * Name binding for function: take.
		 * @see #take(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName take = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "take");

		/**
		 * <code>takeWhile takeWhileTrueFunction list</code> returns the longest prefix of the list for which <code>takeWhileTrueFunction</code>
		 * is <code>Cal.Core.Prelude.True</code> for each element.
		 * <p>
		 * e.g. <code>takeWhile Cal.Core.Prelude.isEven [6, 2, 1, 2] = [6, 2]</code>
		 * <p>
		 * This function is defined in the <code>Cal.Core.Prelude</code> module for implementation purposes only. 
		 * The public api version is <code>Cal.Collections.List.takeWhile</code>.
		 * 
		 * @param takeWhileTrueFunction (CAL type: <code>a -> Cal.Core.Prelude.Boolean</code>)
		 *          a predicate to be applied to the elements of the list.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list from which elements are to be taken.
		 * @return (CAL type: <code>[a]</code>) 
		 *          the longest prefix of the list for which <code>takeWhileTrueFunction</code> is <code>Cal.Core.Prelude.True</code> 
		 * for each element.
		 */
		public static final SourceModel.Expr takeWhile(SourceModel.Expr takeWhileTrueFunction, SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.takeWhile), takeWhileTrueFunction, list});
		}

		/**
		 * Name binding for function: takeWhile.
		 * @see #takeWhile(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName takeWhile = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "takeWhile");

		/**
		 * Helper binding method for function: testEnumBooleanInstance. 
		 * @return the SourceModule.expr representing an application of testEnumBooleanInstance
		 */
		public static final SourceModel.Expr testEnumBooleanInstance() {
			return SourceModel.Expr.Var.make(Functions.testEnumBooleanInstance);
		}

		/**
		 * Name binding for function: testEnumBooleanInstance.
		 * @see #testEnumBooleanInstance()
		 */
		public static final QualifiedName testEnumBooleanInstance = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"testEnumBooleanInstance");

		/**
		 * Helper binding method for function: testEnumByteInstance. 
		 * @return the SourceModule.expr representing an application of testEnumByteInstance
		 */
		public static final SourceModel.Expr testEnumByteInstance() {
			return SourceModel.Expr.Var.make(Functions.testEnumByteInstance);
		}

		/**
		 * Name binding for function: testEnumByteInstance.
		 * @see #testEnumByteInstance()
		 */
		public static final QualifiedName testEnumByteInstance = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"testEnumByteInstance");

		/**
		 * Helper binding method for function: testEnumCharInstance. 
		 * @return the SourceModule.expr representing an application of testEnumCharInstance
		 */
		public static final SourceModel.Expr testEnumCharInstance() {
			return SourceModel.Expr.Var.make(Functions.testEnumCharInstance);
		}

		/**
		 * Name binding for function: testEnumCharInstance.
		 * @see #testEnumCharInstance()
		 */
		public static final QualifiedName testEnumCharInstance = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"testEnumCharInstance");

		/**
		 * Helper binding method for function: testEnumDecimalInstance. 
		 * @return the SourceModule.expr representing an application of testEnumDecimalInstance
		 */
		public static final SourceModel.Expr testEnumDecimalInstance() {
			return SourceModel.Expr.Var.make(Functions.testEnumDecimalInstance);
		}

		/**
		 * Name binding for function: testEnumDecimalInstance.
		 * @see #testEnumDecimalInstance()
		 */
		public static final QualifiedName testEnumDecimalInstance = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"testEnumDecimalInstance");

		/**
		 * Helper binding method for function: testEnumDoubleInstance. 
		 * @return the SourceModule.expr representing an application of testEnumDoubleInstance
		 */
		public static final SourceModel.Expr testEnumDoubleInstance() {
			return SourceModel.Expr.Var.make(Functions.testEnumDoubleInstance);
		}

		/**
		 * Name binding for function: testEnumDoubleInstance.
		 * @see #testEnumDoubleInstance()
		 */
		public static final QualifiedName testEnumDoubleInstance = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"testEnumDoubleInstance");

		/**
		 * Helper binding method for function: testEnumFloatInstance. 
		 * @return the SourceModule.expr representing an application of testEnumFloatInstance
		 */
		public static final SourceModel.Expr testEnumFloatInstance() {
			return SourceModel.Expr.Var.make(Functions.testEnumFloatInstance);
		}

		/**
		 * Name binding for function: testEnumFloatInstance.
		 * @see #testEnumFloatInstance()
		 */
		public static final QualifiedName testEnumFloatInstance = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"testEnumFloatInstance");

		/**
		 * Helper binding method for function: testEnumInstances. 
		 * @return the SourceModule.expr representing an application of testEnumInstances
		 */
		public static final SourceModel.Expr testEnumInstances() {
			return SourceModel.Expr.Var.make(Functions.testEnumInstances);
		}

		/**
		 * Name binding for function: testEnumInstances.
		 * @see #testEnumInstances()
		 */
		public static final QualifiedName testEnumInstances = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"testEnumInstances");

		/**
		 * Helper binding method for function: testEnumIntInstance. 
		 * @return the SourceModule.expr representing an application of testEnumIntInstance
		 */
		public static final SourceModel.Expr testEnumIntInstance() {
			return SourceModel.Expr.Var.make(Functions.testEnumIntInstance);
		}

		/**
		 * Name binding for function: testEnumIntInstance.
		 * @see #testEnumIntInstance()
		 */
		public static final QualifiedName testEnumIntInstance = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"testEnumIntInstance");

		/**
		 * Helper binding method for function: testEnumIntegerInstance. 
		 * @return the SourceModule.expr representing an application of testEnumIntegerInstance
		 */
		public static final SourceModel.Expr testEnumIntegerInstance() {
			return SourceModel.Expr.Var.make(Functions.testEnumIntegerInstance);
		}

		/**
		 * Name binding for function: testEnumIntegerInstance.
		 * @see #testEnumIntegerInstance()
		 */
		public static final QualifiedName testEnumIntegerInstance = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"testEnumIntegerInstance");

		/**
		 * Helper binding method for function: testEnumLongInstance. 
		 * @return the SourceModule.expr representing an application of testEnumLongInstance
		 */
		public static final SourceModel.Expr testEnumLongInstance() {
			return SourceModel.Expr.Var.make(Functions.testEnumLongInstance);
		}

		/**
		 * Name binding for function: testEnumLongInstance.
		 * @see #testEnumLongInstance()
		 */
		public static final QualifiedName testEnumLongInstance = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"testEnumLongInstance");

		/**
		 * Helper binding method for function: testEnumOrderingInstance. 
		 * @return the SourceModule.expr representing an application of testEnumOrderingInstance
		 */
		public static final SourceModel.Expr testEnumOrderingInstance() {
			return SourceModel.Expr.Var.make(Functions.testEnumOrderingInstance);
		}

		/**
		 * Name binding for function: testEnumOrderingInstance.
		 * @see #testEnumOrderingInstance()
		 */
		public static final QualifiedName testEnumOrderingInstance = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"testEnumOrderingInstance");

		/**
		 * Helper binding method for function: testEnumShortInstance. 
		 * @return the SourceModule.expr representing an application of testEnumShortInstance
		 */
		public static final SourceModel.Expr testEnumShortInstance() {
			return SourceModel.Expr.Var.make(Functions.testEnumShortInstance);
		}

		/**
		 * Name binding for function: testEnumShortInstance.
		 * @see #testEnumShortInstance()
		 */
		public static final QualifiedName testEnumShortInstance = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"testEnumShortInstance");

		/**
		 * Helper binding method for function: testIntEnumInstances. 
		 * @return the SourceModule.expr representing an application of testIntEnumInstances
		 */
		public static final SourceModel.Expr testIntEnumInstances() {
			return SourceModel.Expr.Var.make(Functions.testIntEnumInstances);
		}

		/**
		 * Name binding for function: testIntEnumInstances.
		 * @see #testIntEnumInstances()
		 */
		public static final QualifiedName testIntEnumInstances = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"testIntEnumInstances");

		/**
		 * Helper binding method for function: testMaybeInstances. 
		 * @return the SourceModule.expr representing an application of testMaybeInstances
		 */
		public static final SourceModel.Expr testMaybeInstances() {
			return SourceModel.Expr.Var.make(Functions.testMaybeInstances);
		}

		/**
		 * Name binding for function: testMaybeInstances.
		 * @see #testMaybeInstances()
		 */
		public static final QualifiedName testMaybeInstances = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"testMaybeInstances");

		/**
		 * Helper binding method for function: typeArgumentsExamples. 
		 * @return the SourceModule.expr representing an application of typeArgumentsExamples
		 */
		public static final SourceModel.Expr typeArgumentsExamples() {
			return SourceModel.Expr.Var.make(Functions.typeArgumentsExamples);
		}

		/**
		 * Name binding for function: typeArgumentsExamples.
		 * @see #typeArgumentsExamples()
		 */
		public static final QualifiedName typeArgumentsExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"typeArgumentsExamples");

		/**
		 * Helper binding method for function: typeOfExamples. 
		 * @return the SourceModule.expr representing an application of typeOfExamples
		 */
		public static final SourceModel.Expr typeOfExamples() {
			return SourceModel.Expr.Var.make(Functions.typeOfExamples);
		}

		/**
		 * Name binding for function: typeOfExamples.
		 * @see #typeOfExamples()
		 */
		public static final QualifiedName typeOfExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"typeOfExamples");

		/**
		 * Helper binding method for function: typeOfRecord. 
		 * @param recordValue
		 * @return the SourceModule.expr representing an application of typeOfRecord
		 */
		public static final SourceModel.Expr typeOfRecord(SourceModel.Expr recordValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.typeOfRecord), recordValue});
		}

		/**
		 * Name binding for function: typeOfRecord.
		 * @see #typeOfRecord(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName typeOfRecord = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "typeOfRecord");

		/**
		 * Helper binding method for function: typeRepArray_new. 
		 * @param size
		 * @return the SourceModule.expr representing an application of typeRepArray_new
		 */
		public static final SourceModel.Expr typeRepArray_new(SourceModel.Expr size) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.typeRepArray_new), size});
		}

		/**
		 * @see #typeRepArray_new(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param size
		 * @return the SourceModel.Expr representing an application of typeRepArray_new
		 */
		public static final SourceModel.Expr typeRepArray_new(int size) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.typeRepArray_new), SourceModel.Expr.makeIntValue(size)});
		}

		/**
		 * Name binding for function: typeRepArray_new.
		 * @see #typeRepArray_new(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName typeRepArray_new = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"typeRepArray_new");

		/**
		 * Helper binding method for function: typeRepArray_subscript. 
		 * @param array
		 * @param index
		 * @return the SourceModule.expr representing an application of typeRepArray_subscript
		 */
		public static final SourceModel.Expr typeRepArray_subscript(SourceModel.Expr array, SourceModel.Expr index) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.typeRepArray_subscript), array, index});
		}

		/**
		 * @see #typeRepArray_subscript(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param index
		 * @return the SourceModel.Expr representing an application of typeRepArray_subscript
		 */
		public static final SourceModel.Expr typeRepArray_subscript(SourceModel.Expr array, int index) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.typeRepArray_subscript), array, SourceModel.Expr.makeIntValue(index)});
		}

		/**
		 * Name binding for function: typeRepArray_subscript.
		 * @see #typeRepArray_subscript(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName typeRepArray_subscript = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"typeRepArray_subscript");

		/**
		 * Helper binding method for function: typeRepArray_update. 
		 * @param array
		 * @param index
		 * @param newValue
		 * @return the SourceModule.expr representing an application of typeRepArray_update
		 */
		public static final SourceModel.Expr typeRepArray_update(SourceModel.Expr array, SourceModel.Expr index, SourceModel.Expr newValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.typeRepArray_update), array, index, newValue});
		}

		/**
		 * @see #typeRepArray_update(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param array
		 * @param index
		 * @param newValue
		 * @return the SourceModel.Expr representing an application of typeRepArray_update
		 */
		public static final SourceModel.Expr typeRepArray_update(SourceModel.Expr array, int index, SourceModel.Expr newValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.typeRepArray_update), array, SourceModel.Expr.makeIntValue(index), newValue});
		}

		/**
		 * Name binding for function: typeRepArray_update.
		 * @see #typeRepArray_update(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName typeRepArray_update = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"typeRepArray_update");

		/**
		 * Helper binding method for function: typeRepToStringExamples. 
		 * @return the SourceModule.expr representing an application of typeRepToStringExamples
		 */
		public static final SourceModel.Expr typeRepToStringExamples() {
			return SourceModel.Expr.Var.make(Functions.typeRepToStringExamples);
		}

		/**
		 * Name binding for function: typeRepToStringExamples.
		 * @see #typeRepToStringExamples()
		 */
		public static final QualifiedName typeRepToStringExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"typeRepToStringExamples");

		/**
		 * Helper binding method for function: typeRepsToList. 
		 * @param typeReps
		 * @return the SourceModule.expr representing an application of typeRepsToList
		 */
		public static final SourceModel.Expr typeRepsToList(SourceModel.Expr typeReps) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.typeRepsToList), typeReps});
		}

		/**
		 * Name binding for function: typeRepsToList.
		 * @see #typeRepsToList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName typeRepsToList = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"typeRepsToList");

		/**
		 * Helper binding method for function: unitValue_UNIT. 
		 * @return the SourceModule.expr representing an application of unitValue_UNIT
		 */
		public static final SourceModel.Expr unitValue_UNIT() {
			return SourceModel.Expr.Var.make(Functions.unitValue_UNIT);
		}

		/**
		 * Name binding for function: unitValue_UNIT.
		 * @see #unitValue_UNIT()
		 */
		public static final QualifiedName unitValue_UNIT = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"unitValue_UNIT");

		/**
		 * Helper binding method for function: upFromBoolean. 
		 * @param x
		 * @return the SourceModule.expr representing an application of upFromBoolean
		 */
		public static final SourceModel.Expr upFromBoolean(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromBoolean), x});
		}

		/**
		 * @see #upFromBoolean(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @return the SourceModel.Expr representing an application of upFromBoolean
		 */
		public static final SourceModel.Expr upFromBoolean(boolean x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromBoolean), SourceModel.Expr.makeBooleanValue(x)});
		}

		/**
		 * Name binding for function: upFromBoolean.
		 * @see #upFromBoolean(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromBoolean = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromBoolean");

		/**
		 * Helper binding method for function: upFromBooleanExamples. 
		 * @return the SourceModule.expr representing an application of upFromBooleanExamples
		 */
		public static final SourceModel.Expr upFromBooleanExamples() {
			return SourceModel.Expr.Var.make(Functions.upFromBooleanExamples);
		}

		/**
		 * Name binding for function: upFromBooleanExamples.
		 * @see #upFromBooleanExamples()
		 */
		public static final QualifiedName upFromBooleanExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromBooleanExamples");

		/**
		 * Helper binding method for function: upFromByDownToByte. 
		 * @param start
		 * @param step
		 * @param end
		 * @return the SourceModule.expr representing an application of upFromByDownToByte
		 */
		public static final SourceModel.Expr upFromByDownToByte(SourceModel.Expr start, SourceModel.Expr step, SourceModel.Expr end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromByDownToByte), start, step, end});
		}

		/**
		 * @see #upFromByDownToByte(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param start
		 * @param step
		 * @param end
		 * @return the SourceModel.Expr representing an application of upFromByDownToByte
		 */
		public static final SourceModel.Expr upFromByDownToByte(byte start, byte step, byte end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromByDownToByte), SourceModel.Expr.makeByteValue(start), SourceModel.Expr.makeByteValue(step), SourceModel.Expr.makeByteValue(end)});
		}

		/**
		 * Name binding for function: upFromByDownToByte.
		 * @see #upFromByDownToByte(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromByDownToByte = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromByDownToByte");

		/**
		 * Helper binding method for function: upFromByDownToChar. 
		 * @param start
		 * @param step
		 * @param end
		 * @return the SourceModule.expr representing an application of upFromByDownToChar
		 */
		public static final SourceModel.Expr upFromByDownToChar(SourceModel.Expr start, SourceModel.Expr step, SourceModel.Expr end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromByDownToChar), start, step, end});
		}

		/**
		 * @see #upFromByDownToChar(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param start
		 * @param step
		 * @param end
		 * @return the SourceModel.Expr representing an application of upFromByDownToChar
		 */
		public static final SourceModel.Expr upFromByDownToChar(char start, int step, char end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromByDownToChar), SourceModel.Expr.makeCharValue(start), SourceModel.Expr.makeIntValue(step), SourceModel.Expr.makeCharValue(end)});
		}

		/**
		 * Name binding for function: upFromByDownToChar.
		 * @see #upFromByDownToChar(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromByDownToChar = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromByDownToChar");

		/**
		 * Helper binding method for function: upFromByDownToDecimal. 
		 * @param start
		 * @param step
		 * @param end
		 * @return the SourceModule.expr representing an application of upFromByDownToDecimal
		 */
		public static final SourceModel.Expr upFromByDownToDecimal(SourceModel.Expr start, SourceModel.Expr step, SourceModel.Expr end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromByDownToDecimal), start, step, end});
		}

		/**
		 * Name binding for function: upFromByDownToDecimal.
		 * @see #upFromByDownToDecimal(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromByDownToDecimal = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromByDownToDecimal");

		/**
		 * Helper binding method for function: upFromByDownToInt. 
		 * @param start
		 * @param step
		 * @param end
		 * @return the SourceModule.expr representing an application of upFromByDownToInt
		 */
		public static final SourceModel.Expr upFromByDownToInt(SourceModel.Expr start, SourceModel.Expr step, SourceModel.Expr end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromByDownToInt), start, step, end});
		}

		/**
		 * @see #upFromByDownToInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param start
		 * @param step
		 * @param end
		 * @return the SourceModel.Expr representing an application of upFromByDownToInt
		 */
		public static final SourceModel.Expr upFromByDownToInt(int start, int step, int end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromByDownToInt), SourceModel.Expr.makeIntValue(start), SourceModel.Expr.makeIntValue(step), SourceModel.Expr.makeIntValue(end)});
		}

		/**
		 * Name binding for function: upFromByDownToInt.
		 * @see #upFromByDownToInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromByDownToInt = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromByDownToInt");

		/**
		 * Helper binding method for function: upFromByDownToInteger. 
		 * @param start
		 * @param step
		 * @param end
		 * @return the SourceModule.expr representing an application of upFromByDownToInteger
		 */
		public static final SourceModel.Expr upFromByDownToInteger(SourceModel.Expr start, SourceModel.Expr step, SourceModel.Expr end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromByDownToInteger), start, step, end});
		}

		/**
		 * Name binding for function: upFromByDownToInteger.
		 * @see #upFromByDownToInteger(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromByDownToInteger = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromByDownToInteger");

		/**
		 * Helper binding method for function: upFromByDownToLong. 
		 * @param start
		 * @param step
		 * @param end
		 * @return the SourceModule.expr representing an application of upFromByDownToLong
		 */
		public static final SourceModel.Expr upFromByDownToLong(SourceModel.Expr start, SourceModel.Expr step, SourceModel.Expr end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromByDownToLong), start, step, end});
		}

		/**
		 * @see #upFromByDownToLong(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param start
		 * @param step
		 * @param end
		 * @return the SourceModel.Expr representing an application of upFromByDownToLong
		 */
		public static final SourceModel.Expr upFromByDownToLong(long start, long step, long end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromByDownToLong), SourceModel.Expr.makeLongValue(start), SourceModel.Expr.makeLongValue(step), SourceModel.Expr.makeLongValue(end)});
		}

		/**
		 * Name binding for function: upFromByDownToLong.
		 * @see #upFromByDownToLong(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromByDownToLong = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromByDownToLong");

		/**
		 * Helper binding method for function: upFromByDownToShort. 
		 * @param start
		 * @param step
		 * @param end
		 * @return the SourceModule.expr representing an application of upFromByDownToShort
		 */
		public static final SourceModel.Expr upFromByDownToShort(SourceModel.Expr start, SourceModel.Expr step, SourceModel.Expr end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromByDownToShort), start, step, end});
		}

		/**
		 * @see #upFromByDownToShort(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param start
		 * @param step
		 * @param end
		 * @return the SourceModel.Expr representing an application of upFromByDownToShort
		 */
		public static final SourceModel.Expr upFromByDownToShort(short start, short step, short end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromByDownToShort), SourceModel.Expr.makeShortValue(start), SourceModel.Expr.makeShortValue(step), SourceModel.Expr.makeShortValue(end)});
		}

		/**
		 * Name binding for function: upFromByDownToShort.
		 * @see #upFromByDownToShort(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromByDownToShort = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromByDownToShort");

		/**
		 * 
		 * @param start (CAL type: <code>Cal.Core.Prelude.Byte</code>)
		 * @param step (CAL type: <code>Cal.Core.Prelude.Byte</code>)
		 *          may be <code>&lt; 0</code> in certain calls e.g. <code>Cal.Core.Prelude.upFromThenByte Cal.Core.Prelude.minBound Cal.Core.Prelude.maxBound</code>,
		 * then <code>step</code> is -1, but the function still works.
		 * @param end (CAL type: <code>Cal.Core.Prelude.Byte</code>)
		 * @return (CAL type: <code>[Cal.Core.Prelude.Byte]</code>) 
		 */
		public static final SourceModel.Expr upFromByUpToByte(SourceModel.Expr start, SourceModel.Expr step, SourceModel.Expr end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromByUpToByte), start, step, end});
		}

		/**
		 * @see #upFromByUpToByte(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param start
		 * @param step
		 * @param end
		 * @return the SourceModel.Expr representing an application of upFromByUpToByte
		 */
		public static final SourceModel.Expr upFromByUpToByte(byte start, byte step, byte end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromByUpToByte), SourceModel.Expr.makeByteValue(start), SourceModel.Expr.makeByteValue(step), SourceModel.Expr.makeByteValue(end)});
		}

		/**
		 * Name binding for function: upFromByUpToByte.
		 * @see #upFromByUpToByte(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromByUpToByte = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromByUpToByte");

		/**
		 * Helper binding method for function: upFromByUpToChar. 
		 * @param start
		 * @param step
		 * @param end
		 * @return the SourceModule.expr representing an application of upFromByUpToChar
		 */
		public static final SourceModel.Expr upFromByUpToChar(SourceModel.Expr start, SourceModel.Expr step, SourceModel.Expr end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromByUpToChar), start, step, end});
		}

		/**
		 * @see #upFromByUpToChar(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param start
		 * @param step
		 * @param end
		 * @return the SourceModel.Expr representing an application of upFromByUpToChar
		 */
		public static final SourceModel.Expr upFromByUpToChar(char start, int step, char end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromByUpToChar), SourceModel.Expr.makeCharValue(start), SourceModel.Expr.makeIntValue(step), SourceModel.Expr.makeCharValue(end)});
		}

		/**
		 * Name binding for function: upFromByUpToChar.
		 * @see #upFromByUpToChar(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromByUpToChar = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromByUpToChar");

		/**
		 * This function assumes that <code>step &gt;= 0</code>
		 * @param start (CAL type: <code>Cal.Core.Prelude.Decimal</code>)
		 * @param step (CAL type: <code>Cal.Core.Prelude.Decimal</code>)
		 * @param end (CAL type: <code>Cal.Core.Prelude.Decimal</code>)
		 * @return (CAL type: <code>[Cal.Core.Prelude.Decimal]</code>) 
		 */
		public static final SourceModel.Expr upFromByUpToDecimal(SourceModel.Expr start, SourceModel.Expr step, SourceModel.Expr end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromByUpToDecimal), start, step, end});
		}

		/**
		 * Name binding for function: upFromByUpToDecimal.
		 * @see #upFromByUpToDecimal(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromByUpToDecimal = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromByUpToDecimal");

		/**
		 * 
		 * @param start (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @param step (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          may be <code>&lt; 0</code> in certain calls e.g. <code>Cal.Core.Prelude.upFromThenInt Cal.Core.Prelude.minBound Cal.Core.Prelude.maxBound</code>,
		 * then <code>step</code> is -1, but the function still works.
		 * @param end (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @return (CAL type: <code>[Cal.Core.Prelude.Int]</code>) 
		 */
		public static final SourceModel.Expr upFromByUpToInt(SourceModel.Expr start, SourceModel.Expr step, SourceModel.Expr end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromByUpToInt), start, step, end});
		}

		/**
		 * @see #upFromByUpToInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param start
		 * @param step
		 * @param end
		 * @return the SourceModel.Expr representing an application of upFromByUpToInt
		 */
		public static final SourceModel.Expr upFromByUpToInt(int start, int step, int end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromByUpToInt), SourceModel.Expr.makeIntValue(start), SourceModel.Expr.makeIntValue(step), SourceModel.Expr.makeIntValue(end)});
		}

		/**
		 * Name binding for function: upFromByUpToInt.
		 * @see #upFromByUpToInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromByUpToInt = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromByUpToInt");

		/**
		 * This function assumes that <code>step &gt;= 0</code>.
		 * @param start (CAL type: <code>Cal.Core.Prelude.Integer</code>)
		 * @param step (CAL type: <code>Cal.Core.Prelude.Integer</code>)
		 * @param end (CAL type: <code>Cal.Core.Prelude.Integer</code>)
		 * @return (CAL type: <code>[Cal.Core.Prelude.Integer]</code>) 
		 */
		public static final SourceModel.Expr upFromByUpToInteger(SourceModel.Expr start, SourceModel.Expr step, SourceModel.Expr end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromByUpToInteger), start, step, end});
		}

		/**
		 * Name binding for function: upFromByUpToInteger.
		 * @see #upFromByUpToInteger(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromByUpToInteger = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromByUpToInteger");

		/**
		 * 
		 * @param start (CAL type: <code>Cal.Core.Prelude.Long</code>)
		 * @param step (CAL type: <code>Cal.Core.Prelude.Long</code>)
		 *          may be <code>&lt; 0</code> in certain calls e.g. <code>Cal.Core.Prelude.upFromThenLong Cal.Core.Prelude.minBound Cal.Core.Prelude.maxBound</code>,
		 * then <code>step</code> is -1, but the function still works.
		 * @param end (CAL type: <code>Cal.Core.Prelude.Long</code>)
		 * @return (CAL type: <code>[Cal.Core.Prelude.Long]</code>) 
		 */
		public static final SourceModel.Expr upFromByUpToLong(SourceModel.Expr start, SourceModel.Expr step, SourceModel.Expr end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromByUpToLong), start, step, end});
		}

		/**
		 * @see #upFromByUpToLong(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param start
		 * @param step
		 * @param end
		 * @return the SourceModel.Expr representing an application of upFromByUpToLong
		 */
		public static final SourceModel.Expr upFromByUpToLong(long start, long step, long end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromByUpToLong), SourceModel.Expr.makeLongValue(start), SourceModel.Expr.makeLongValue(step), SourceModel.Expr.makeLongValue(end)});
		}

		/**
		 * Name binding for function: upFromByUpToLong.
		 * @see #upFromByUpToLong(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromByUpToLong = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromByUpToLong");

		/**
		 * 
		 * @param start (CAL type: <code>Cal.Core.Prelude.Short</code>)
		 * @param step (CAL type: <code>Cal.Core.Prelude.Short</code>)
		 *          may be <code>&lt; 0</code> in certain calls e.g. <code>Cal.Core.Prelude.upFromThenShort Cal.Core.Prelude.minBound Cal.Core.Prelude.maxBound</code>,
		 * then <code>step</code> is -1, but the function still works.
		 * @param end (CAL type: <code>Cal.Core.Prelude.Short</code>)
		 * @return (CAL type: <code>[Cal.Core.Prelude.Short]</code>) 
		 */
		public static final SourceModel.Expr upFromByUpToShort(SourceModel.Expr start, SourceModel.Expr step, SourceModel.Expr end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromByUpToShort), start, step, end});
		}

		/**
		 * @see #upFromByUpToShort(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param start
		 * @param step
		 * @param end
		 * @return the SourceModel.Expr representing an application of upFromByUpToShort
		 */
		public static final SourceModel.Expr upFromByUpToShort(short start, short step, short end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromByUpToShort), SourceModel.Expr.makeShortValue(start), SourceModel.Expr.makeShortValue(step), SourceModel.Expr.makeShortValue(end)});
		}

		/**
		 * Name binding for function: upFromByUpToShort.
		 * @see #upFromByUpToShort(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromByUpToShort = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromByUpToShort");

		/**
		 * Helper binding method for function: upFromByte. 
		 * @param start
		 * @return the SourceModule.expr representing an application of upFromByte
		 */
		public static final SourceModel.Expr upFromByte(SourceModel.Expr start) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromByte), start});
		}

		/**
		 * @see #upFromByte(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param start
		 * @return the SourceModel.Expr representing an application of upFromByte
		 */
		public static final SourceModel.Expr upFromByte(byte start) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromByte), SourceModel.Expr.makeByteValue(start)});
		}

		/**
		 * Name binding for function: upFromByte.
		 * @see #upFromByte(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromByte = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "upFromByte");

		/**
		 * Helper binding method for function: upFromByteExamples. 
		 * @return the SourceModule.expr representing an application of upFromByteExamples
		 */
		public static final SourceModel.Expr upFromByteExamples() {
			return SourceModel.Expr.Var.make(Functions.upFromByteExamples);
		}

		/**
		 * Name binding for function: upFromByteExamples.
		 * @see #upFromByteExamples()
		 */
		public static final QualifiedName upFromByteExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromByteExamples");

		/**
		 * Helper binding method for function: upFromChar. 
		 * @param start
		 * @return the SourceModule.expr representing an application of upFromChar
		 */
		public static final SourceModel.Expr upFromChar(SourceModel.Expr start) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromChar), start});
		}

		/**
		 * @see #upFromChar(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param start
		 * @return the SourceModel.Expr representing an application of upFromChar
		 */
		public static final SourceModel.Expr upFromChar(char start) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromChar), SourceModel.Expr.makeCharValue(start)});
		}

		/**
		 * Name binding for function: upFromChar.
		 * @see #upFromChar(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromChar = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "upFromChar");

		/**
		 * Helper binding method for function: upFromCharExamples. 
		 * @return the SourceModule.expr representing an application of upFromCharExamples
		 */
		public static final SourceModel.Expr upFromCharExamples() {
			return SourceModel.Expr.Var.make(Functions.upFromCharExamples);
		}

		/**
		 * Name binding for function: upFromCharExamples.
		 * @see #upFromCharExamples()
		 */
		public static final QualifiedName upFromCharExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromCharExamples");

		/**
		 * Helper binding method for function: upFromDecimal. 
		 * @param start
		 * @return the SourceModule.expr representing an application of upFromDecimal
		 */
		public static final SourceModel.Expr upFromDecimal(SourceModel.Expr start) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromDecimal), start});
		}

		/**
		 * Name binding for function: upFromDecimal.
		 * @see #upFromDecimal(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromDecimal = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromDecimal");

		/**
		 * Helper binding method for function: upFromDecimalExamples. 
		 * @return the SourceModule.expr representing an application of upFromDecimalExamples
		 */
		public static final SourceModel.Expr upFromDecimalExamples() {
			return SourceModel.Expr.Var.make(Functions.upFromDecimalExamples);
		}

		/**
		 * Name binding for function: upFromDecimalExamples.
		 * @see #upFromDecimalExamples()
		 */
		public static final QualifiedName upFromDecimalExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromDecimalExamples");

		/**
		 * Helper binding method for function: upFromDouble. 
		 * @param start
		 * @return the SourceModule.expr representing an application of upFromDouble
		 */
		public static final SourceModel.Expr upFromDouble(SourceModel.Expr start) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromDouble), start});
		}

		/**
		 * @see #upFromDouble(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param start
		 * @return the SourceModel.Expr representing an application of upFromDouble
		 */
		public static final SourceModel.Expr upFromDouble(double start) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromDouble), SourceModel.Expr.makeDoubleValue(start)});
		}

		/**
		 * Name binding for function: upFromDouble.
		 * @see #upFromDouble(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromDouble = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "upFromDouble");

		/**
		 * Helper binding method for function: upFromDoubleExamples. 
		 * @return the SourceModule.expr representing an application of upFromDoubleExamples
		 */
		public static final SourceModel.Expr upFromDoubleExamples() {
			return SourceModel.Expr.Var.make(Functions.upFromDoubleExamples);
		}

		/**
		 * Name binding for function: upFromDoubleExamples.
		 * @see #upFromDoubleExamples()
		 */
		public static final QualifiedName upFromDoubleExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromDoubleExamples");

		/**
		 * Helper binding method for function: upFromFloat. 
		 * @param start
		 * @return the SourceModule.expr representing an application of upFromFloat
		 */
		public static final SourceModel.Expr upFromFloat(SourceModel.Expr start) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromFloat), start});
		}

		/**
		 * @see #upFromFloat(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param start
		 * @return the SourceModel.Expr representing an application of upFromFloat
		 */
		public static final SourceModel.Expr upFromFloat(float start) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromFloat), SourceModel.Expr.makeFloatValue(start)});
		}

		/**
		 * Name binding for function: upFromFloat.
		 * @see #upFromFloat(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromFloat = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "upFromFloat");

		/**
		 * Helper binding method for function: upFromFloatExamples. 
		 * @return the SourceModule.expr representing an application of upFromFloatExamples
		 */
		public static final SourceModel.Expr upFromFloatExamples() {
			return SourceModel.Expr.Var.make(Functions.upFromFloatExamples);
		}

		/**
		 * Name binding for function: upFromFloatExamples.
		 * @see #upFromFloatExamples()
		 */
		public static final QualifiedName upFromFloatExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromFloatExamples");

		/**
		 * Helper binding method for function: upFromInt. 
		 * @param start
		 * @return the SourceModule.expr representing an application of upFromInt
		 */
		public static final SourceModel.Expr upFromInt(SourceModel.Expr start) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromInt), start});
		}

		/**
		 * @see #upFromInt(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param start
		 * @return the SourceModel.Expr representing an application of upFromInt
		 */
		public static final SourceModel.Expr upFromInt(int start) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromInt), SourceModel.Expr.makeIntValue(start)});
		}

		/**
		 * Name binding for function: upFromInt.
		 * @see #upFromInt(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromInt = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "upFromInt");

		/**
		 * Helper binding method for function: upFromIntExamples. 
		 * @return the SourceModule.expr representing an application of upFromIntExamples
		 */
		public static final SourceModel.Expr upFromIntExamples() {
			return SourceModel.Expr.Var.make(Functions.upFromIntExamples);
		}

		/**
		 * Name binding for function: upFromIntExamples.
		 * @see #upFromIntExamples()
		 */
		public static final QualifiedName upFromIntExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromIntExamples");

		/**
		 * Helper binding method for function: upFromInteger. 
		 * @param start
		 * @return the SourceModule.expr representing an application of upFromInteger
		 */
		public static final SourceModel.Expr upFromInteger(SourceModel.Expr start) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromInteger), start});
		}

		/**
		 * Name binding for function: upFromInteger.
		 * @see #upFromInteger(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromInteger = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromInteger");

		/**
		 * Helper binding method for function: upFromIntegerExamples. 
		 * @return the SourceModule.expr representing an application of upFromIntegerExamples
		 */
		public static final SourceModel.Expr upFromIntegerExamples() {
			return SourceModel.Expr.Var.make(Functions.upFromIntegerExamples);
		}

		/**
		 * Name binding for function: upFromIntegerExamples.
		 * @see #upFromIntegerExamples()
		 */
		public static final QualifiedName upFromIntegerExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromIntegerExamples");

		/**
		 * Helper binding method for function: upFromLong. 
		 * @param start
		 * @return the SourceModule.expr representing an application of upFromLong
		 */
		public static final SourceModel.Expr upFromLong(SourceModel.Expr start) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromLong), start});
		}

		/**
		 * @see #upFromLong(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param start
		 * @return the SourceModel.Expr representing an application of upFromLong
		 */
		public static final SourceModel.Expr upFromLong(long start) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromLong), SourceModel.Expr.makeLongValue(start)});
		}

		/**
		 * Name binding for function: upFromLong.
		 * @see #upFromLong(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromLong = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "upFromLong");

		/**
		 * Helper binding method for function: upFromLongExamples. 
		 * @return the SourceModule.expr representing an application of upFromLongExamples
		 */
		public static final SourceModel.Expr upFromLongExamples() {
			return SourceModel.Expr.Var.make(Functions.upFromLongExamples);
		}

		/**
		 * Name binding for function: upFromLongExamples.
		 * @see #upFromLongExamples()
		 */
		public static final QualifiedName upFromLongExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromLongExamples");

		/**
		 * Helper binding method for function: upFromOrderingExamples. 
		 * @return the SourceModule.expr representing an application of upFromOrderingExamples
		 */
		public static final SourceModel.Expr upFromOrderingExamples() {
			return SourceModel.Expr.Var.make(Functions.upFromOrderingExamples);
		}

		/**
		 * Name binding for function: upFromOrderingExamples.
		 * @see #upFromOrderingExamples()
		 */
		public static final QualifiedName upFromOrderingExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromOrderingExamples");

		/**
		 * Helper binding method for function: upFromShort. 
		 * @param start
		 * @return the SourceModule.expr representing an application of upFromShort
		 */
		public static final SourceModel.Expr upFromShort(SourceModel.Expr start) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromShort), start});
		}

		/**
		 * @see #upFromShort(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param start
		 * @return the SourceModel.Expr representing an application of upFromShort
		 */
		public static final SourceModel.Expr upFromShort(short start) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromShort), SourceModel.Expr.makeShortValue(start)});
		}

		/**
		 * Name binding for function: upFromShort.
		 * @see #upFromShort(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromShort = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "upFromShort");

		/**
		 * Helper binding method for function: upFromShortExamples. 
		 * @return the SourceModule.expr representing an application of upFromShortExamples
		 */
		public static final SourceModel.Expr upFromShortExamples() {
			return SourceModel.Expr.Var.make(Functions.upFromShortExamples);
		}

		/**
		 * Name binding for function: upFromShortExamples.
		 * @see #upFromShortExamples()
		 */
		public static final QualifiedName upFromShortExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromShortExamples");

		/**
		 * Helper binding method for function: upFromThenBoolean. 
		 * @param start
		 * @param next
		 * @return the SourceModule.expr representing an application of upFromThenBoolean
		 */
		public static final SourceModel.Expr upFromThenBoolean(SourceModel.Expr start, SourceModel.Expr next) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromThenBoolean), start, next});
		}

		/**
		 * @see #upFromThenBoolean(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param start
		 * @param next
		 * @return the SourceModel.Expr representing an application of upFromThenBoolean
		 */
		public static final SourceModel.Expr upFromThenBoolean(boolean start, boolean next) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromThenBoolean), SourceModel.Expr.makeBooleanValue(start), SourceModel.Expr.makeBooleanValue(next)});
		}

		/**
		 * Name binding for function: upFromThenBoolean.
		 * @see #upFromThenBoolean(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromThenBoolean = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromThenBoolean");

		/**
		 * Helper binding method for function: upFromThenBooleanExamples. 
		 * @return the SourceModule.expr representing an application of upFromThenBooleanExamples
		 */
		public static final SourceModel.Expr upFromThenBooleanExamples() {
			return 
				SourceModel.Expr.Var.make(Functions.upFromThenBooleanExamples);
		}

		/**
		 * Name binding for function: upFromThenBooleanExamples.
		 * @see #upFromThenBooleanExamples()
		 */
		public static final QualifiedName upFromThenBooleanExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromThenBooleanExamples");

		/**
		 * Helper binding method for function: upFromThenByte. 
		 * @param start
		 * @param next
		 * @return the SourceModule.expr representing an application of upFromThenByte
		 */
		public static final SourceModel.Expr upFromThenByte(SourceModel.Expr start, SourceModel.Expr next) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromThenByte), start, next});
		}

		/**
		 * @see #upFromThenByte(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param start
		 * @param next
		 * @return the SourceModel.Expr representing an application of upFromThenByte
		 */
		public static final SourceModel.Expr upFromThenByte(byte start, byte next) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromThenByte), SourceModel.Expr.makeByteValue(start), SourceModel.Expr.makeByteValue(next)});
		}

		/**
		 * Name binding for function: upFromThenByte.
		 * @see #upFromThenByte(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromThenByte = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromThenByte");

		/**
		 * Helper binding method for function: upFromThenByteExamples. 
		 * @return the SourceModule.expr representing an application of upFromThenByteExamples
		 */
		public static final SourceModel.Expr upFromThenByteExamples() {
			return SourceModel.Expr.Var.make(Functions.upFromThenByteExamples);
		}

		/**
		 * Name binding for function: upFromThenByteExamples.
		 * @see #upFromThenByteExamples()
		 */
		public static final QualifiedName upFromThenByteExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromThenByteExamples");

		/**
		 * Helper binding method for function: upFromThenChar. 
		 * @param start
		 * @param next
		 * @return the SourceModule.expr representing an application of upFromThenChar
		 */
		public static final SourceModel.Expr upFromThenChar(SourceModel.Expr start, SourceModel.Expr next) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromThenChar), start, next});
		}

		/**
		 * @see #upFromThenChar(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param start
		 * @param next
		 * @return the SourceModel.Expr representing an application of upFromThenChar
		 */
		public static final SourceModel.Expr upFromThenChar(char start, char next) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromThenChar), SourceModel.Expr.makeCharValue(start), SourceModel.Expr.makeCharValue(next)});
		}

		/**
		 * Name binding for function: upFromThenChar.
		 * @see #upFromThenChar(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromThenChar = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromThenChar");

		/**
		 * Helper binding method for function: upFromThenCharExamples. 
		 * @return the SourceModule.expr representing an application of upFromThenCharExamples
		 */
		public static final SourceModel.Expr upFromThenCharExamples() {
			return SourceModel.Expr.Var.make(Functions.upFromThenCharExamples);
		}

		/**
		 * Name binding for function: upFromThenCharExamples.
		 * @see #upFromThenCharExamples()
		 */
		public static final QualifiedName upFromThenCharExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromThenCharExamples");

		/**
		 * Helper binding method for function: upFromThenDecimal. 
		 * @param start
		 * @param next
		 * @return the SourceModule.expr representing an application of upFromThenDecimal
		 */
		public static final SourceModel.Expr upFromThenDecimal(SourceModel.Expr start, SourceModel.Expr next) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromThenDecimal), start, next});
		}

		/**
		 * Name binding for function: upFromThenDecimal.
		 * @see #upFromThenDecimal(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromThenDecimal = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromThenDecimal");

		/**
		 * Helper binding method for function: upFromThenDecimalExamples. 
		 * @return the SourceModule.expr representing an application of upFromThenDecimalExamples
		 */
		public static final SourceModel.Expr upFromThenDecimalExamples() {
			return 
				SourceModel.Expr.Var.make(Functions.upFromThenDecimalExamples);
		}

		/**
		 * Name binding for function: upFromThenDecimalExamples.
		 * @see #upFromThenDecimalExamples()
		 */
		public static final QualifiedName upFromThenDecimalExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromThenDecimalExamples");

		/**
		 * Helper binding method for function: upFromThenDouble. 
		 * @param start
		 * @param next
		 * @return the SourceModule.expr representing an application of upFromThenDouble
		 */
		public static final SourceModel.Expr upFromThenDouble(SourceModel.Expr start, SourceModel.Expr next) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromThenDouble), start, next});
		}

		/**
		 * @see #upFromThenDouble(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param start
		 * @param next
		 * @return the SourceModel.Expr representing an application of upFromThenDouble
		 */
		public static final SourceModel.Expr upFromThenDouble(double start, double next) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromThenDouble), SourceModel.Expr.makeDoubleValue(start), SourceModel.Expr.makeDoubleValue(next)});
		}

		/**
		 * Name binding for function: upFromThenDouble.
		 * @see #upFromThenDouble(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromThenDouble = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromThenDouble");

		/**
		 * Helper binding method for function: upFromThenDoubleExamples. 
		 * @return the SourceModule.expr representing an application of upFromThenDoubleExamples
		 */
		public static final SourceModel.Expr upFromThenDoubleExamples() {
			return SourceModel.Expr.Var.make(Functions.upFromThenDoubleExamples);
		}

		/**
		 * Name binding for function: upFromThenDoubleExamples.
		 * @see #upFromThenDoubleExamples()
		 */
		public static final QualifiedName upFromThenDoubleExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromThenDoubleExamples");

		/**
		 * Helper binding method for function: upFromThenFloat. 
		 * @param start
		 * @param next
		 * @return the SourceModule.expr representing an application of upFromThenFloat
		 */
		public static final SourceModel.Expr upFromThenFloat(SourceModel.Expr start, SourceModel.Expr next) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromThenFloat), start, next});
		}

		/**
		 * @see #upFromThenFloat(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param start
		 * @param next
		 * @return the SourceModel.Expr representing an application of upFromThenFloat
		 */
		public static final SourceModel.Expr upFromThenFloat(float start, float next) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromThenFloat), SourceModel.Expr.makeFloatValue(start), SourceModel.Expr.makeFloatValue(next)});
		}

		/**
		 * Name binding for function: upFromThenFloat.
		 * @see #upFromThenFloat(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromThenFloat = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromThenFloat");

		/**
		 * Helper binding method for function: upFromThenFloatExamples. 
		 * @return the SourceModule.expr representing an application of upFromThenFloatExamples
		 */
		public static final SourceModel.Expr upFromThenFloatExamples() {
			return SourceModel.Expr.Var.make(Functions.upFromThenFloatExamples);
		}

		/**
		 * Name binding for function: upFromThenFloatExamples.
		 * @see #upFromThenFloatExamples()
		 */
		public static final QualifiedName upFromThenFloatExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromThenFloatExamples");

		/**
		 * Helper binding method for function: upFromThenInt. 
		 * @param start
		 * @param next
		 * @return the SourceModule.expr representing an application of upFromThenInt
		 */
		public static final SourceModel.Expr upFromThenInt(SourceModel.Expr start, SourceModel.Expr next) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromThenInt), start, next});
		}

		/**
		 * @see #upFromThenInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param start
		 * @param next
		 * @return the SourceModel.Expr representing an application of upFromThenInt
		 */
		public static final SourceModel.Expr upFromThenInt(int start, int next) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromThenInt), SourceModel.Expr.makeIntValue(start), SourceModel.Expr.makeIntValue(next)});
		}

		/**
		 * Name binding for function: upFromThenInt.
		 * @see #upFromThenInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromThenInt = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromThenInt");

		/**
		 * Helper binding method for function: upFromThenIntExamples. 
		 * @return the SourceModule.expr representing an application of upFromThenIntExamples
		 */
		public static final SourceModel.Expr upFromThenIntExamples() {
			return SourceModel.Expr.Var.make(Functions.upFromThenIntExamples);
		}

		/**
		 * Name binding for function: upFromThenIntExamples.
		 * @see #upFromThenIntExamples()
		 */
		public static final QualifiedName upFromThenIntExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromThenIntExamples");

		/**
		 * Helper binding method for function: upFromThenInteger. 
		 * @param start
		 * @param next
		 * @return the SourceModule.expr representing an application of upFromThenInteger
		 */
		public static final SourceModel.Expr upFromThenInteger(SourceModel.Expr start, SourceModel.Expr next) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromThenInteger), start, next});
		}

		/**
		 * Name binding for function: upFromThenInteger.
		 * @see #upFromThenInteger(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromThenInteger = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromThenInteger");

		/**
		 * Helper binding method for function: upFromThenIntegerExamples. 
		 * @return the SourceModule.expr representing an application of upFromThenIntegerExamples
		 */
		public static final SourceModel.Expr upFromThenIntegerExamples() {
			return 
				SourceModel.Expr.Var.make(Functions.upFromThenIntegerExamples);
		}

		/**
		 * Name binding for function: upFromThenIntegerExamples.
		 * @see #upFromThenIntegerExamples()
		 */
		public static final QualifiedName upFromThenIntegerExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromThenIntegerExamples");

		/**
		 * Helper binding method for function: upFromThenLong. 
		 * @param start
		 * @param next
		 * @return the SourceModule.expr representing an application of upFromThenLong
		 */
		public static final SourceModel.Expr upFromThenLong(SourceModel.Expr start, SourceModel.Expr next) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromThenLong), start, next});
		}

		/**
		 * @see #upFromThenLong(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param start
		 * @param next
		 * @return the SourceModel.Expr representing an application of upFromThenLong
		 */
		public static final SourceModel.Expr upFromThenLong(long start, long next) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromThenLong), SourceModel.Expr.makeLongValue(start), SourceModel.Expr.makeLongValue(next)});
		}

		/**
		 * Name binding for function: upFromThenLong.
		 * @see #upFromThenLong(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromThenLong = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromThenLong");

		/**
		 * Helper binding method for function: upFromThenLongExamples. 
		 * @return the SourceModule.expr representing an application of upFromThenLongExamples
		 */
		public static final SourceModel.Expr upFromThenLongExamples() {
			return SourceModel.Expr.Var.make(Functions.upFromThenLongExamples);
		}

		/**
		 * Name binding for function: upFromThenLongExamples.
		 * @see #upFromThenLongExamples()
		 */
		public static final QualifiedName upFromThenLongExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromThenLongExamples");

		/**
		 * Helper binding method for function: upFromThenOrderingExamples. 
		 * @return the SourceModule.expr representing an application of upFromThenOrderingExamples
		 */
		public static final SourceModel.Expr upFromThenOrderingExamples() {
			return 
				SourceModel.Expr.Var.make(Functions.upFromThenOrderingExamples);
		}

		/**
		 * Name binding for function: upFromThenOrderingExamples.
		 * @see #upFromThenOrderingExamples()
		 */
		public static final QualifiedName upFromThenOrderingExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromThenOrderingExamples");

		/**
		 * Helper binding method for function: upFromThenShort. 
		 * @param start
		 * @param next
		 * @return the SourceModule.expr representing an application of upFromThenShort
		 */
		public static final SourceModel.Expr upFromThenShort(SourceModel.Expr start, SourceModel.Expr next) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromThenShort), start, next});
		}

		/**
		 * @see #upFromThenShort(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param start
		 * @param next
		 * @return the SourceModel.Expr representing an application of upFromThenShort
		 */
		public static final SourceModel.Expr upFromThenShort(short start, short next) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromThenShort), SourceModel.Expr.makeShortValue(start), SourceModel.Expr.makeShortValue(next)});
		}

		/**
		 * Name binding for function: upFromThenShort.
		 * @see #upFromThenShort(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromThenShort = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromThenShort");

		/**
		 * Helper binding method for function: upFromThenShortExamples. 
		 * @return the SourceModule.expr representing an application of upFromThenShortExamples
		 */
		public static final SourceModel.Expr upFromThenShortExamples() {
			return SourceModel.Expr.Var.make(Functions.upFromThenShortExamples);
		}

		/**
		 * Name binding for function: upFromThenShortExamples.
		 * @see #upFromThenShortExamples()
		 */
		public static final QualifiedName upFromThenShortExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromThenShortExamples");

		/**
		 * Helper binding method for function: upFromThenToBoolean. 
		 * @param start
		 * @param next
		 * @param end
		 * @return the SourceModule.expr representing an application of upFromThenToBoolean
		 */
		public static final SourceModel.Expr upFromThenToBoolean(SourceModel.Expr start, SourceModel.Expr next, SourceModel.Expr end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromThenToBoolean), start, next, end});
		}

		/**
		 * @see #upFromThenToBoolean(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param start
		 * @param next
		 * @param end
		 * @return the SourceModel.Expr representing an application of upFromThenToBoolean
		 */
		public static final SourceModel.Expr upFromThenToBoolean(boolean start, boolean next, boolean end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromThenToBoolean), SourceModel.Expr.makeBooleanValue(start), SourceModel.Expr.makeBooleanValue(next), SourceModel.Expr.makeBooleanValue(end)});
		}

		/**
		 * Name binding for function: upFromThenToBoolean.
		 * @see #upFromThenToBoolean(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromThenToBoolean = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromThenToBoolean");

		/**
		 * Helper binding method for function: upFromThenToBooleanExamples. 
		 * @return the SourceModule.expr representing an application of upFromThenToBooleanExamples
		 */
		public static final SourceModel.Expr upFromThenToBooleanExamples() {
			return 
				SourceModel.Expr.Var.make(Functions.upFromThenToBooleanExamples);
		}

		/**
		 * Name binding for function: upFromThenToBooleanExamples.
		 * @see #upFromThenToBooleanExamples()
		 */
		public static final QualifiedName upFromThenToBooleanExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromThenToBooleanExamples");

		/**
		 * Helper binding method for function: upFromThenToByte. 
		 * @param start
		 * @param next
		 * @param end
		 * @return the SourceModule.expr representing an application of upFromThenToByte
		 */
		public static final SourceModel.Expr upFromThenToByte(SourceModel.Expr start, SourceModel.Expr next, SourceModel.Expr end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromThenToByte), start, next, end});
		}

		/**
		 * @see #upFromThenToByte(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param start
		 * @param next
		 * @param end
		 * @return the SourceModel.Expr representing an application of upFromThenToByte
		 */
		public static final SourceModel.Expr upFromThenToByte(byte start, byte next, byte end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromThenToByte), SourceModel.Expr.makeByteValue(start), SourceModel.Expr.makeByteValue(next), SourceModel.Expr.makeByteValue(end)});
		}

		/**
		 * Name binding for function: upFromThenToByte.
		 * @see #upFromThenToByte(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromThenToByte = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromThenToByte");

		/**
		 * Helper binding method for function: upFromThenToByteExamples. 
		 * @return the SourceModule.expr representing an application of upFromThenToByteExamples
		 */
		public static final SourceModel.Expr upFromThenToByteExamples() {
			return SourceModel.Expr.Var.make(Functions.upFromThenToByteExamples);
		}

		/**
		 * Name binding for function: upFromThenToByteExamples.
		 * @see #upFromThenToByteExamples()
		 */
		public static final QualifiedName upFromThenToByteExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromThenToByteExamples");

		/**
		 * Helper binding method for function: upFromThenToChar. 
		 * @param start
		 * @param next
		 * @param end
		 * @return the SourceModule.expr representing an application of upFromThenToChar
		 */
		public static final SourceModel.Expr upFromThenToChar(SourceModel.Expr start, SourceModel.Expr next, SourceModel.Expr end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromThenToChar), start, next, end});
		}

		/**
		 * @see #upFromThenToChar(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param start
		 * @param next
		 * @param end
		 * @return the SourceModel.Expr representing an application of upFromThenToChar
		 */
		public static final SourceModel.Expr upFromThenToChar(char start, char next, char end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromThenToChar), SourceModel.Expr.makeCharValue(start), SourceModel.Expr.makeCharValue(next), SourceModel.Expr.makeCharValue(end)});
		}

		/**
		 * Name binding for function: upFromThenToChar.
		 * @see #upFromThenToChar(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromThenToChar = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromThenToChar");

		/**
		 * Helper binding method for function: upFromThenToCharExamples. 
		 * @return the SourceModule.expr representing an application of upFromThenToCharExamples
		 */
		public static final SourceModel.Expr upFromThenToCharExamples() {
			return SourceModel.Expr.Var.make(Functions.upFromThenToCharExamples);
		}

		/**
		 * Name binding for function: upFromThenToCharExamples.
		 * @see #upFromThenToCharExamples()
		 */
		public static final QualifiedName upFromThenToCharExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromThenToCharExamples");

		/**
		 * Helper binding method for function: upFromThenToDecimal. 
		 * @param start
		 * @param next
		 * @param end
		 * @return the SourceModule.expr representing an application of upFromThenToDecimal
		 */
		public static final SourceModel.Expr upFromThenToDecimal(SourceModel.Expr start, SourceModel.Expr next, SourceModel.Expr end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromThenToDecimal), start, next, end});
		}

		/**
		 * Name binding for function: upFromThenToDecimal.
		 * @see #upFromThenToDecimal(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromThenToDecimal = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromThenToDecimal");

		/**
		 * Helper binding method for function: upFromThenToDecimalExamples. 
		 * @return the SourceModule.expr representing an application of upFromThenToDecimalExamples
		 */
		public static final SourceModel.Expr upFromThenToDecimalExamples() {
			return 
				SourceModel.Expr.Var.make(Functions.upFromThenToDecimalExamples);
		}

		/**
		 * Name binding for function: upFromThenToDecimalExamples.
		 * @see #upFromThenToDecimalExamples()
		 */
		public static final QualifiedName upFromThenToDecimalExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromThenToDecimalExamples");

		/**
		 * Helper binding method for function: upFromThenToDouble. 
		 * @param start
		 * @param next
		 * @param end
		 * @return the SourceModule.expr representing an application of upFromThenToDouble
		 */
		public static final SourceModel.Expr upFromThenToDouble(SourceModel.Expr start, SourceModel.Expr next, SourceModel.Expr end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromThenToDouble), start, next, end});
		}

		/**
		 * @see #upFromThenToDouble(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param start
		 * @param next
		 * @param end
		 * @return the SourceModel.Expr representing an application of upFromThenToDouble
		 */
		public static final SourceModel.Expr upFromThenToDouble(double start, double next, double end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromThenToDouble), SourceModel.Expr.makeDoubleValue(start), SourceModel.Expr.makeDoubleValue(next), SourceModel.Expr.makeDoubleValue(end)});
		}

		/**
		 * Name binding for function: upFromThenToDouble.
		 * @see #upFromThenToDouble(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromThenToDouble = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromThenToDouble");

		/**
		 * Helper binding method for function: upFromThenToDoubleExamples. 
		 * @return the SourceModule.expr representing an application of upFromThenToDoubleExamples
		 */
		public static final SourceModel.Expr upFromThenToDoubleExamples() {
			return 
				SourceModel.Expr.Var.make(Functions.upFromThenToDoubleExamples);
		}

		/**
		 * Name binding for function: upFromThenToDoubleExamples.
		 * @see #upFromThenToDoubleExamples()
		 */
		public static final QualifiedName upFromThenToDoubleExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromThenToDoubleExamples");

		/**
		 * Helper binding method for function: upFromThenToFloat. 
		 * @param start
		 * @param next
		 * @param end
		 * @return the SourceModule.expr representing an application of upFromThenToFloat
		 */
		public static final SourceModel.Expr upFromThenToFloat(SourceModel.Expr start, SourceModel.Expr next, SourceModel.Expr end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromThenToFloat), start, next, end});
		}

		/**
		 * @see #upFromThenToFloat(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param start
		 * @param next
		 * @param end
		 * @return the SourceModel.Expr representing an application of upFromThenToFloat
		 */
		public static final SourceModel.Expr upFromThenToFloat(float start, float next, float end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromThenToFloat), SourceModel.Expr.makeFloatValue(start), SourceModel.Expr.makeFloatValue(next), SourceModel.Expr.makeFloatValue(end)});
		}

		/**
		 * Name binding for function: upFromThenToFloat.
		 * @see #upFromThenToFloat(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromThenToFloat = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromThenToFloat");

		/**
		 * Helper binding method for function: upFromThenToFloatExamples. 
		 * @return the SourceModule.expr representing an application of upFromThenToFloatExamples
		 */
		public static final SourceModel.Expr upFromThenToFloatExamples() {
			return 
				SourceModel.Expr.Var.make(Functions.upFromThenToFloatExamples);
		}

		/**
		 * Name binding for function: upFromThenToFloatExamples.
		 * @see #upFromThenToFloatExamples()
		 */
		public static final QualifiedName upFromThenToFloatExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromThenToFloatExamples");

		/**
		 * Helper binding method for function: upFromThenToInt. 
		 * @param start
		 * @param next
		 * @param end
		 * @return the SourceModule.expr representing an application of upFromThenToInt
		 */
		public static final SourceModel.Expr upFromThenToInt(SourceModel.Expr start, SourceModel.Expr next, SourceModel.Expr end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromThenToInt), start, next, end});
		}

		/**
		 * @see #upFromThenToInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param start
		 * @param next
		 * @param end
		 * @return the SourceModel.Expr representing an application of upFromThenToInt
		 */
		public static final SourceModel.Expr upFromThenToInt(int start, int next, int end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromThenToInt), SourceModel.Expr.makeIntValue(start), SourceModel.Expr.makeIntValue(next), SourceModel.Expr.makeIntValue(end)});
		}

		/**
		 * Name binding for function: upFromThenToInt.
		 * @see #upFromThenToInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromThenToInt = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromThenToInt");

		/**
		 * Helper binding method for function: upFromThenToIntExamples. 
		 * @return the SourceModule.expr representing an application of upFromThenToIntExamples
		 */
		public static final SourceModel.Expr upFromThenToIntExamples() {
			return SourceModel.Expr.Var.make(Functions.upFromThenToIntExamples);
		}

		/**
		 * Name binding for function: upFromThenToIntExamples.
		 * @see #upFromThenToIntExamples()
		 */
		public static final QualifiedName upFromThenToIntExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromThenToIntExamples");

		/**
		 * Helper binding method for function: upFromThenToInteger. 
		 * @param start
		 * @param next
		 * @param end
		 * @return the SourceModule.expr representing an application of upFromThenToInteger
		 */
		public static final SourceModel.Expr upFromThenToInteger(SourceModel.Expr start, SourceModel.Expr next, SourceModel.Expr end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromThenToInteger), start, next, end});
		}

		/**
		 * Name binding for function: upFromThenToInteger.
		 * @see #upFromThenToInteger(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromThenToInteger = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromThenToInteger");

		/**
		 * Helper binding method for function: upFromThenToIntegerExamples. 
		 * @return the SourceModule.expr representing an application of upFromThenToIntegerExamples
		 */
		public static final SourceModel.Expr upFromThenToIntegerExamples() {
			return 
				SourceModel.Expr.Var.make(Functions.upFromThenToIntegerExamples);
		}

		/**
		 * Name binding for function: upFromThenToIntegerExamples.
		 * @see #upFromThenToIntegerExamples()
		 */
		public static final QualifiedName upFromThenToIntegerExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromThenToIntegerExamples");

		/**
		 * Helper binding method for function: upFromThenToLong. 
		 * @param start
		 * @param next
		 * @param end
		 * @return the SourceModule.expr representing an application of upFromThenToLong
		 */
		public static final SourceModel.Expr upFromThenToLong(SourceModel.Expr start, SourceModel.Expr next, SourceModel.Expr end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromThenToLong), start, next, end});
		}

		/**
		 * @see #upFromThenToLong(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param start
		 * @param next
		 * @param end
		 * @return the SourceModel.Expr representing an application of upFromThenToLong
		 */
		public static final SourceModel.Expr upFromThenToLong(long start, long next, long end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromThenToLong), SourceModel.Expr.makeLongValue(start), SourceModel.Expr.makeLongValue(next), SourceModel.Expr.makeLongValue(end)});
		}

		/**
		 * Name binding for function: upFromThenToLong.
		 * @see #upFromThenToLong(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromThenToLong = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromThenToLong");

		/**
		 * Helper binding method for function: upFromThenToLongExamples. 
		 * @return the SourceModule.expr representing an application of upFromThenToLongExamples
		 */
		public static final SourceModel.Expr upFromThenToLongExamples() {
			return SourceModel.Expr.Var.make(Functions.upFromThenToLongExamples);
		}

		/**
		 * Name binding for function: upFromThenToLongExamples.
		 * @see #upFromThenToLongExamples()
		 */
		public static final QualifiedName upFromThenToLongExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromThenToLongExamples");

		/**
		 * Helper binding method for function: upFromThenToOrderingExamples. 
		 * @return the SourceModule.expr representing an application of upFromThenToOrderingExamples
		 */
		public static final SourceModel.Expr upFromThenToOrderingExamples() {
			return 
				SourceModel.Expr.Var.make(
					Functions.upFromThenToOrderingExamples);
		}

		/**
		 * Name binding for function: upFromThenToOrderingExamples.
		 * @see #upFromThenToOrderingExamples()
		 */
		public static final QualifiedName upFromThenToOrderingExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromThenToOrderingExamples");

		/**
		 * Helper binding method for function: upFromThenToShort. 
		 * @param start
		 * @param next
		 * @param end
		 * @return the SourceModule.expr representing an application of upFromThenToShort
		 */
		public static final SourceModel.Expr upFromThenToShort(SourceModel.Expr start, SourceModel.Expr next, SourceModel.Expr end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromThenToShort), start, next, end});
		}

		/**
		 * @see #upFromThenToShort(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param start
		 * @param next
		 * @param end
		 * @return the SourceModel.Expr representing an application of upFromThenToShort
		 */
		public static final SourceModel.Expr upFromThenToShort(short start, short next, short end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromThenToShort), SourceModel.Expr.makeShortValue(start), SourceModel.Expr.makeShortValue(next), SourceModel.Expr.makeShortValue(end)});
		}

		/**
		 * Name binding for function: upFromThenToShort.
		 * @see #upFromThenToShort(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromThenToShort = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromThenToShort");

		/**
		 * Helper binding method for function: upFromThenToShortExamples. 
		 * @return the SourceModule.expr representing an application of upFromThenToShortExamples
		 */
		public static final SourceModel.Expr upFromThenToShortExamples() {
			return 
				SourceModel.Expr.Var.make(Functions.upFromThenToShortExamples);
		}

		/**
		 * Name binding for function: upFromThenToShortExamples.
		 * @see #upFromThenToShortExamples()
		 */
		public static final QualifiedName upFromThenToShortExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromThenToShortExamples");

		/**
		 * Helper binding method for function: upFromToBoolean. 
		 * @param start
		 * @param end
		 * @return the SourceModule.expr representing an application of upFromToBoolean
		 */
		public static final SourceModel.Expr upFromToBoolean(SourceModel.Expr start, SourceModel.Expr end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromToBoolean), start, end});
		}

		/**
		 * @see #upFromToBoolean(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param start
		 * @param end
		 * @return the SourceModel.Expr representing an application of upFromToBoolean
		 */
		public static final SourceModel.Expr upFromToBoolean(boolean start, boolean end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromToBoolean), SourceModel.Expr.makeBooleanValue(start), SourceModel.Expr.makeBooleanValue(end)});
		}

		/**
		 * Name binding for function: upFromToBoolean.
		 * @see #upFromToBoolean(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromToBoolean = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromToBoolean");

		/**
		 * Helper binding method for function: upFromToBooleanExamples. 
		 * @return the SourceModule.expr representing an application of upFromToBooleanExamples
		 */
		public static final SourceModel.Expr upFromToBooleanExamples() {
			return SourceModel.Expr.Var.make(Functions.upFromToBooleanExamples);
		}

		/**
		 * Name binding for function: upFromToBooleanExamples.
		 * @see #upFromToBooleanExamples()
		 */
		public static final QualifiedName upFromToBooleanExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromToBooleanExamples");

		/**
		 * Helper binding method for function: upFromToByte. 
		 * @param start
		 * @param end
		 * @return the SourceModule.expr representing an application of upFromToByte
		 */
		public static final SourceModel.Expr upFromToByte(SourceModel.Expr start, SourceModel.Expr end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromToByte), start, end});
		}

		/**
		 * @see #upFromToByte(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param start
		 * @param end
		 * @return the SourceModel.Expr representing an application of upFromToByte
		 */
		public static final SourceModel.Expr upFromToByte(byte start, byte end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromToByte), SourceModel.Expr.makeByteValue(start), SourceModel.Expr.makeByteValue(end)});
		}

		/**
		 * Name binding for function: upFromToByte.
		 * @see #upFromToByte(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromToByte = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "upFromToByte");

		/**
		 * Helper binding method for function: upFromToByteExamples. 
		 * @return the SourceModule.expr representing an application of upFromToByteExamples
		 */
		public static final SourceModel.Expr upFromToByteExamples() {
			return SourceModel.Expr.Var.make(Functions.upFromToByteExamples);
		}

		/**
		 * Name binding for function: upFromToByteExamples.
		 * @see #upFromToByteExamples()
		 */
		public static final QualifiedName upFromToByteExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromToByteExamples");

		/**
		 * Helper binding method for function: upFromToChar. 
		 * @param start
		 * @param end
		 * @return the SourceModule.expr representing an application of upFromToChar
		 */
		public static final SourceModel.Expr upFromToChar(SourceModel.Expr start, SourceModel.Expr end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromToChar), start, end});
		}

		/**
		 * @see #upFromToChar(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param start
		 * @param end
		 * @return the SourceModel.Expr representing an application of upFromToChar
		 */
		public static final SourceModel.Expr upFromToChar(char start, char end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromToChar), SourceModel.Expr.makeCharValue(start), SourceModel.Expr.makeCharValue(end)});
		}

		/**
		 * Name binding for function: upFromToChar.
		 * @see #upFromToChar(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromToChar = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "upFromToChar");

		/**
		 * Helper binding method for function: upFromToCharExamples. 
		 * @return the SourceModule.expr representing an application of upFromToCharExamples
		 */
		public static final SourceModel.Expr upFromToCharExamples() {
			return SourceModel.Expr.Var.make(Functions.upFromToCharExamples);
		}

		/**
		 * Name binding for function: upFromToCharExamples.
		 * @see #upFromToCharExamples()
		 */
		public static final QualifiedName upFromToCharExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromToCharExamples");

		/**
		 * Helper binding method for function: upFromToDecimal. 
		 * @param start
		 * @param end
		 * @return the SourceModule.expr representing an application of upFromToDecimal
		 */
		public static final SourceModel.Expr upFromToDecimal(SourceModel.Expr start, SourceModel.Expr end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromToDecimal), start, end});
		}

		/**
		 * Name binding for function: upFromToDecimal.
		 * @see #upFromToDecimal(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromToDecimal = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromToDecimal");

		/**
		 * Helper binding method for function: upFromToDecimalExamples. 
		 * @return the SourceModule.expr representing an application of upFromToDecimalExamples
		 */
		public static final SourceModel.Expr upFromToDecimalExamples() {
			return SourceModel.Expr.Var.make(Functions.upFromToDecimalExamples);
		}

		/**
		 * Name binding for function: upFromToDecimalExamples.
		 * @see #upFromToDecimalExamples()
		 */
		public static final QualifiedName upFromToDecimalExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromToDecimalExamples");

		/**
		 * Helper binding method for function: upFromToDouble. 
		 * @param start
		 * @param end
		 * @return the SourceModule.expr representing an application of upFromToDouble
		 */
		public static final SourceModel.Expr upFromToDouble(SourceModel.Expr start, SourceModel.Expr end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromToDouble), start, end});
		}

		/**
		 * @see #upFromToDouble(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param start
		 * @param end
		 * @return the SourceModel.Expr representing an application of upFromToDouble
		 */
		public static final SourceModel.Expr upFromToDouble(double start, double end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromToDouble), SourceModel.Expr.makeDoubleValue(start), SourceModel.Expr.makeDoubleValue(end)});
		}

		/**
		 * Name binding for function: upFromToDouble.
		 * @see #upFromToDouble(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromToDouble = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromToDouble");

		/**
		 * Helper binding method for function: upFromToDoubleExamples. 
		 * @return the SourceModule.expr representing an application of upFromToDoubleExamples
		 */
		public static final SourceModel.Expr upFromToDoubleExamples() {
			return SourceModel.Expr.Var.make(Functions.upFromToDoubleExamples);
		}

		/**
		 * Name binding for function: upFromToDoubleExamples.
		 * @see #upFromToDoubleExamples()
		 */
		public static final QualifiedName upFromToDoubleExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromToDoubleExamples");

		/**
		 * Helper binding method for function: upFromToFloat. 
		 * @param start
		 * @param end
		 * @return the SourceModule.expr representing an application of upFromToFloat
		 */
		public static final SourceModel.Expr upFromToFloat(SourceModel.Expr start, SourceModel.Expr end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromToFloat), start, end});
		}

		/**
		 * @see #upFromToFloat(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param start
		 * @param end
		 * @return the SourceModel.Expr representing an application of upFromToFloat
		 */
		public static final SourceModel.Expr upFromToFloat(float start, float end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromToFloat), SourceModel.Expr.makeFloatValue(start), SourceModel.Expr.makeFloatValue(end)});
		}

		/**
		 * Name binding for function: upFromToFloat.
		 * @see #upFromToFloat(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromToFloat = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromToFloat");

		/**
		 * Helper binding method for function: upFromToFloatExamples. 
		 * @return the SourceModule.expr representing an application of upFromToFloatExamples
		 */
		public static final SourceModel.Expr upFromToFloatExamples() {
			return SourceModel.Expr.Var.make(Functions.upFromToFloatExamples);
		}

		/**
		 * Name binding for function: upFromToFloatExamples.
		 * @see #upFromToFloatExamples()
		 */
		public static final QualifiedName upFromToFloatExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromToFloatExamples");

		/**
		 * Helper binding method for function: upFromToInt. 
		 * @param start
		 * @param end
		 * @return the SourceModule.expr representing an application of upFromToInt
		 */
		public static final SourceModel.Expr upFromToInt(SourceModel.Expr start, SourceModel.Expr end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromToInt), start, end});
		}

		/**
		 * @see #upFromToInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param start
		 * @param end
		 * @return the SourceModel.Expr representing an application of upFromToInt
		 */
		public static final SourceModel.Expr upFromToInt(int start, int end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromToInt), SourceModel.Expr.makeIntValue(start), SourceModel.Expr.makeIntValue(end)});
		}

		/**
		 * Name binding for function: upFromToInt.
		 * @see #upFromToInt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromToInt = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "upFromToInt");

		/**
		 * Helper binding method for function: upFromToIntExamples. 
		 * @return the SourceModule.expr representing an application of upFromToIntExamples
		 */
		public static final SourceModel.Expr upFromToIntExamples() {
			return SourceModel.Expr.Var.make(Functions.upFromToIntExamples);
		}

		/**
		 * Name binding for function: upFromToIntExamples.
		 * @see #upFromToIntExamples()
		 */
		public static final QualifiedName upFromToIntExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromToIntExamples");

		/**
		 * Helper binding method for function: upFromToInteger. 
		 * @param start
		 * @param end
		 * @return the SourceModule.expr representing an application of upFromToInteger
		 */
		public static final SourceModel.Expr upFromToInteger(SourceModel.Expr start, SourceModel.Expr end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromToInteger), start, end});
		}

		/**
		 * Name binding for function: upFromToInteger.
		 * @see #upFromToInteger(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromToInteger = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromToInteger");

		/**
		 * Helper binding method for function: upFromToIntegerExamples. 
		 * @return the SourceModule.expr representing an application of upFromToIntegerExamples
		 */
		public static final SourceModel.Expr upFromToIntegerExamples() {
			return SourceModel.Expr.Var.make(Functions.upFromToIntegerExamples);
		}

		/**
		 * Name binding for function: upFromToIntegerExamples.
		 * @see #upFromToIntegerExamples()
		 */
		public static final QualifiedName upFromToIntegerExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromToIntegerExamples");

		/**
		 * Helper binding method for function: upFromToLong. 
		 * @param start
		 * @param end
		 * @return the SourceModule.expr representing an application of upFromToLong
		 */
		public static final SourceModel.Expr upFromToLong(SourceModel.Expr start, SourceModel.Expr end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromToLong), start, end});
		}

		/**
		 * @see #upFromToLong(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param start
		 * @param end
		 * @return the SourceModel.Expr representing an application of upFromToLong
		 */
		public static final SourceModel.Expr upFromToLong(long start, long end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromToLong), SourceModel.Expr.makeLongValue(start), SourceModel.Expr.makeLongValue(end)});
		}

		/**
		 * Name binding for function: upFromToLong.
		 * @see #upFromToLong(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromToLong = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "upFromToLong");

		/**
		 * Helper binding method for function: upFromToLongExamples. 
		 * @return the SourceModule.expr representing an application of upFromToLongExamples
		 */
		public static final SourceModel.Expr upFromToLongExamples() {
			return SourceModel.Expr.Var.make(Functions.upFromToLongExamples);
		}

		/**
		 * Name binding for function: upFromToLongExamples.
		 * @see #upFromToLongExamples()
		 */
		public static final QualifiedName upFromToLongExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromToLongExamples");

		/**
		 * Helper binding method for function: upFromToOrderingExamples. 
		 * @return the SourceModule.expr representing an application of upFromToOrderingExamples
		 */
		public static final SourceModel.Expr upFromToOrderingExamples() {
			return SourceModel.Expr.Var.make(Functions.upFromToOrderingExamples);
		}

		/**
		 * Name binding for function: upFromToOrderingExamples.
		 * @see #upFromToOrderingExamples()
		 */
		public static final QualifiedName upFromToOrderingExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromToOrderingExamples");

		/**
		 * Helper binding method for function: upFromToShort. 
		 * @param start
		 * @param end
		 * @return the SourceModule.expr representing an application of upFromToShort
		 */
		public static final SourceModel.Expr upFromToShort(SourceModel.Expr start, SourceModel.Expr end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromToShort), start, end});
		}

		/**
		 * @see #upFromToShort(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param start
		 * @param end
		 * @return the SourceModel.Expr representing an application of upFromToShort
		 */
		public static final SourceModel.Expr upFromToShort(short start, short end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromToShort), SourceModel.Expr.makeShortValue(start), SourceModel.Expr.makeShortValue(end)});
		}

		/**
		 * Name binding for function: upFromToShort.
		 * @see #upFromToShort(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromToShort = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromToShort");

		/**
		 * Helper binding method for function: upFromToShortExamples. 
		 * @return the SourceModule.expr representing an application of upFromToShortExamples
		 */
		public static final SourceModel.Expr upFromToShortExamples() {
			return SourceModel.Expr.Var.make(Functions.upFromToShortExamples);
		}

		/**
		 * Name binding for function: upFromToShortExamples.
		 * @see #upFromToShortExamples()
		 */
		public static final QualifiedName upFromToShortExamples = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"upFromToShortExamples");

		/**
		 * Helper binding method for function: updateTypeReps. 
		 * @param typeReps
		 * @param index
		 * @param typeRep
		 * @return the SourceModule.expr representing an application of updateTypeReps
		 */
		public static final SourceModel.Expr updateTypeReps(SourceModel.Expr typeReps, SourceModel.Expr index, SourceModel.Expr typeRep) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.updateTypeReps), typeReps, index, typeRep});
		}

		/**
		 * @see #updateTypeReps(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param typeReps
		 * @param index
		 * @param typeRep
		 * @return the SourceModel.Expr representing an application of updateTypeReps
		 */
		public static final SourceModel.Expr updateTypeReps(SourceModel.Expr typeReps, int index, SourceModel.Expr typeRep) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.updateTypeReps), typeReps, SourceModel.Expr.makeIntValue(index), typeRep});
		}

		/**
		 * Name binding for function: updateTypeReps.
		 * @see #updateTypeReps(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName updateTypeReps = 
			QualifiedName.make(
				CAL_Prelude_internal.MODULE_NAME, 
				"updateTypeReps");

		/**
		 * Converts two lists into a list of corresponding pairs.
		 * If one input list is short, excess elements of the longer list are discarded.
		 * <p>
		 * e.g. <code>zip [6, 3] [10, 20, 30] = [(6,10), (3, 20)]</code>
		 * <p>
		 * This function is defined in the <code>Cal.Core.Prelude</code> module for implementation purposes only. 
		 * The public api version is <code>Cal.Collections.List.zip</code>.
		 * 
		 * @param list1 (CAL type: <code>[a]</code>)
		 *          the first list to be zipped.
		 * @param list2 (CAL type: <code>[b]</code>)
		 *          the second list to be zipped.
		 * @return (CAL type: <code>[(a, b)]</code>) 
		 *          a list of corresponding pairs.
		 */
		public static final SourceModel.Expr zip(SourceModel.Expr list1, SourceModel.Expr list2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.zip), list1, list2});
		}

		/**
		 * Name binding for function: zip.
		 * @see #zip(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName zip = 
			QualifiedName.make(CAL_Prelude_internal.MODULE_NAME, "zip");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = -1736191142;

}
