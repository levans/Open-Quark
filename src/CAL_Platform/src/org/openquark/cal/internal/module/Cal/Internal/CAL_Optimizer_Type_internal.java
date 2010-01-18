/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_Optimizer_Type_internal.java)
 * was generated from CAL module: Cal.Internal.Optimizer_Type.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Internal.Optimizer_Type module from Java code.
 *  
 * Creation date: Wed May 02 14:02:03 PDT 2007
 * --!>
 *  
 */

package org.openquark.cal.internal.module.Cal.Internal;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * WARNING WARNING WARNING WARNING WARNING WARNING WARNING WARNING WARNING WARNING WARNING WARNING WARNING WAR
 * <p>
 * This file is part of the compiler and not to be modified. 
 * <p>
 * ING WARNING WARNING WARNING WARNING WARNING WARNING WARNING WARNING WARNING WARNING WARNING WARNING WARNING
 * <p>
 * This file contains the definition for the Type abstract data type. Also included are supporting functions 
 * including all the code for type unification.
 * 
 * @author Greg McClement
 */
public final class CAL_Optimizer_Type_internal {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Internal.Optimizer_Type");

	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Internal.Optimizer_Type module.
	 */
	public static final class TypeConstructors {
		/** Name binding for TypeConsApp: JTypeConsApp. */
		public static final QualifiedName JTypeConsApp = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"JTypeConsApp");

		/** Name binding for TypeConsApp: JTypeExpr. */
		public static final QualifiedName JTypeExpr = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"JTypeExpr");

		/** Name binding for TypeConsApp: JTypeVar. */
		public static final QualifiedName JTypeVar = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"JTypeVar");

		/**
		 * Represents a TypeExpr in CAL.
		 */
		public static final QualifiedName Type = 
			QualifiedName.make(CAL_Optimizer_Type_internal.MODULE_NAME, "Type");

	}
	/**
	 * This inner class (DataConstructors) contains constants
	 * and methods related to binding to CAL DataConstructors in the Cal.Internal.Optimizer_Type module.
	 */
	public static final class DataConstructors {
		/*
		 * DataConstructors for the Cal.Internal.Optimizer_Type.Type data type.
		 */

		/**
		 * Represents CAL types suchs as Boolean or Int.
		 * @param type (CAL type: <code>Cal.Internal.Optimizer_Type.JTypeExpr</code>)
		 *          The java object that corresponds to the type.
		 * @param args (CAL type: <code>[Cal.Internal.Optimizer_Type.Type]</code>)
		 *          The types of the arguments.
		 * @param strict (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 *          Flag on the strictness of the argument.
		 * @param name (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          A nice name for the type object.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr TypeConst(SourceModel.Expr type, SourceModel.Expr args, SourceModel.Expr strict, SourceModel.Expr name) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.TypeConst), type, args, strict, name});
		}

		/**
		 * @see #TypeConst(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param type
		 * @param args
		 * @param strict
		 * @param name
		 * @return org.openquark.cal.compiler.SourceModel.Expr
		 */
		public static final SourceModel.Expr TypeConst(SourceModel.Expr type, SourceModel.Expr args, boolean strict, java.lang.String name) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.TypeConst), type, args, SourceModel.Expr.makeBooleanValue(strict), SourceModel.Expr.makeStringValue(name)});
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_Type.TypeConst.
		 * @see #TypeConst(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName TypeConst = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"TypeConst");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_Type.TypeConst.
		 * @see #TypeConst(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int TypeConst_ordinal = 0;

		/**
		 * This is used for writing examples.
		 * @param name (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          The name of the pretend type.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr TypeConstTest(SourceModel.Expr name) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.TypeConstTest), name});
		}

		/**
		 * @see #TypeConstTest(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param name
		 * @return org.openquark.cal.compiler.SourceModel.Expr
		 */
		public static final SourceModel.Expr TypeConstTest(java.lang.String name) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.TypeConstTest), SourceModel.Expr.makeStringValue(name)});
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_Type.TypeConstTest.
		 * @see #TypeConstTest(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName TypeConstTest = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"TypeConstTest");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_Type.TypeConstTest.
		 * @see #TypeConstTest(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int TypeConstTest_ordinal = 1;

		/**
		 * Represents a Java TypeVar object.
		 * @param typeId (CAL type: <code>Cal.Internal.Optimizer_Type.JTypeVar</code>)
		 * @param strict (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr TypeVar(SourceModel.Expr typeId, SourceModel.Expr strict) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.TypeVar), typeId, strict});
		}

		/**
		 * @see #TypeVar(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param typeId
		 * @param strict
		 * @return org.openquark.cal.compiler.SourceModel.Expr
		 */
		public static final SourceModel.Expr TypeVar(SourceModel.Expr typeId, boolean strict) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.TypeVar), typeId, SourceModel.Expr.makeBooleanValue(strict)});
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_Type.TypeVar.
		 * @see #TypeVar(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName TypeVar = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"TypeVar");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_Type.TypeVar.
		 * @see #TypeVar(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int TypeVar_ordinal = 2;

		/**
		 * Used to keep track of the type associated with an arguments when making a fusion function. 
		 * typeId of one is the first arguement, two is the second argument etc.
		 * @param typeId (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          The ordinal of the argument.
		 * @param strict (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 *          The strictness of the argument.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr TypeId(SourceModel.Expr typeId, SourceModel.Expr strict) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.TypeId), typeId, strict});
		}

		/**
		 * @see #TypeId(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param typeId
		 * @param strict
		 * @return org.openquark.cal.compiler.SourceModel.Expr
		 */
		public static final SourceModel.Expr TypeId(int typeId, boolean strict) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.TypeId), SourceModel.Expr.makeIntValue(typeId), SourceModel.Expr.makeBooleanValue(strict)});
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_Type.TypeId.
		 * @see #TypeId(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName TypeId = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"TypeId");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_Type.TypeId.
		 * @see #TypeId(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int TypeId_ordinal = 3;

		/**
		 * Binding for DataConstructor: Cal.Internal.Optimizer_Type.FunctionType.
		 * @param domain
		 * @param codomain
		 * @param strict
		 * @return the SourceModule.Expr representing an application of Cal.Internal.Optimizer_Type.FunctionType
		 */
		public static final SourceModel.Expr FunctionType(SourceModel.Expr domain, SourceModel.Expr codomain, SourceModel.Expr strict) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.FunctionType), domain, codomain, strict});
		}

		/**
		 * @see #FunctionType(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param domain
		 * @param codomain
		 * @param strict
		 * @return org.openquark.cal.compiler.SourceModel.Expr
		 */
		public static final SourceModel.Expr FunctionType(SourceModel.Expr domain, SourceModel.Expr codomain, boolean strict) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.FunctionType), domain, codomain, SourceModel.Expr.makeBooleanValue(strict)});
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_Type.FunctionType.
		 * @see #FunctionType(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName FunctionType = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"FunctionType");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_Type.FunctionType.
		 * @see #FunctionType(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int FunctionType_ordinal = 4;

		/**
		 * Binding for DataConstructor: Cal.Internal.Optimizer_Type.ListType.
		 * @param domain
		 * @param strict
		 * @return the SourceModule.Expr representing an application of Cal.Internal.Optimizer_Type.ListType
		 */
		public static final SourceModel.Expr ListType(SourceModel.Expr domain, SourceModel.Expr strict) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.ListType), domain, strict});
		}

		/**
		 * @see #ListType(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param domain
		 * @param strict
		 * @return org.openquark.cal.compiler.SourceModel.Expr
		 */
		public static final SourceModel.Expr ListType(SourceModel.Expr domain, boolean strict) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.ListType), domain, SourceModel.Expr.makeBooleanValue(strict)});
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_Type.ListType.
		 * @see #ListType(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName ListType = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"ListType");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_Type.ListType.
		 * @see #ListType(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int ListType_ordinal = 5;

		/**
		 * Binding for DataConstructor: Cal.Internal.Optimizer_Type.AppType.
		 * @param e1
		 * @param e2
		 * @return the SourceModule.Expr representing an application of Cal.Internal.Optimizer_Type.AppType
		 */
		public static final SourceModel.Expr AppType(SourceModel.Expr e1, SourceModel.Expr e2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.AppType), e1, e2});
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_Type.AppType.
		 * @see #AppType(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName AppType = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"AppType");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_Type.AppType.
		 * @see #AppType(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int AppType_ordinal = 6;

		/**
		 * Used to indicate that the type is the unification of t1 and t2. This arises currently in the fusion code
		 * where deep in the type calculation the two different types are known to apply to a varable. Doing the 
		 * unification at that point is not possible because that must be done at the top level of the type expression.
		 * <p>
		 * For example, AndType (TypeConst ... "Cal.Core.Prelude.Int") (TypeVar ...) is the type Cal.Core.Prelude.Int.
		 * 
		 * @param t1 (CAL type: <code>Cal.Internal.Optimizer_Type.Type</code>)
		 * @param t2 (CAL type: <code>Cal.Internal.Optimizer_Type.Type</code>)
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr AndType(SourceModel.Expr t1, SourceModel.Expr t2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.AndType), t1, t2});
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_Type.AndType.
		 * @see #AndType(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName AndType = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"AndType");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_Type.AndType.
		 * @see #AndType(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int AndType_ordinal = 7;

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Internal.Optimizer_Type module.
	 */
	public static final class Functions {
		/**
		 * Make one pass over the given equivalence relation (eqRel) and add equivelent values to 
		 * the given equivalence class.
		 * @param eqClass (CAL type: <code>Cal.Core.Prelude.Eq a => [a]</code>)
		 *          The equivalence class to augment.
		 * @param eqRelToDo (CAL type: <code>Cal.Core.Prelude.Eq a => [(a, a)]</code>)
		 *          The equivalence relation to use to get equivalent values.
		 * @param eqRelUnused (CAL type: <code>Cal.Core.Prelude.Eq a => [(a, a)]</code>)
		 *          The tuples of the equivalence relation that were not used.
		 * @param wasChanged (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 *          A flag indicating if the equilvalence class was changed (eqClass).
		 * @return (CAL type: <code>Cal.Core.Prelude.Eq a => ([a], [(a, a)], Cal.Core.Prelude.Boolean)</code>) 
		 *          A triple, the augmented equivalence class, the unused tuples of the equivalence relation, 
		 * and a flag indicating whether or not the equivalence class was changed.
		 */
		public static final SourceModel.Expr augmentEqClass(SourceModel.Expr eqClass, SourceModel.Expr eqRelToDo, SourceModel.Expr eqRelUnused, SourceModel.Expr wasChanged) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.augmentEqClass), eqClass, eqRelToDo, eqRelUnused, wasChanged});
		}

		/**
		 * @see #augmentEqClass(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param eqClass
		 * @param eqRelToDo
		 * @param eqRelUnused
		 * @param wasChanged
		 * @return the SourceModel.Expr representing an application of augmentEqClass
		 */
		public static final SourceModel.Expr augmentEqClass(SourceModel.Expr eqClass, SourceModel.Expr eqRelToDo, SourceModel.Expr eqRelUnused, boolean wasChanged) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.augmentEqClass), eqClass, eqRelToDo, eqRelUnused, SourceModel.Expr.makeBooleanValue(wasChanged)});
		}

		/**
		 * Name binding for function: augmentEqClass.
		 * @see #augmentEqClass(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName augmentEqClass = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"augmentEqClass");

		/**
		 * Makes a new copy of the type expression where the variables are all 'renamed'
		 * @param type (CAL type: <code>Cal.Internal.Optimizer_Type.Type</code>)
		 *          The type to make a copy of
		 * @return (CAL type: <code>Cal.Internal.Optimizer_Type.Type</code>) 
		 *          The original type with all the variables replaced with new variables keeping track of corresponces between variables.
		 */
		public static final SourceModel.Expr copyVars(SourceModel.Expr type) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.copyVars), type});
		}

		/**
		 * Name binding for function: copyVars.
		 * @see #copyVars(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName copyVars = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"copyVars");

		/**
		 * The input is an equivalence relation. The output is a set of equivance classes.  The 
		 * equivalence relation does not have to be the transitive closure.
		 * <p>
		 * TODO Look up the right algorithm, like one that uses sorting!
		 * 
		 * @param eqRel (CAL type: <code>Cal.Core.Prelude.Eq a => [(a, a)]</code>)
		 *          The equivalence relation.
		 * @return (CAL type: <code>Cal.Core.Prelude.Eq a => [[a]]</code>) 
		 *          The equilvalence classes for the given equivalence relation.
		 */
		public static final SourceModel.Expr equivalenceClasses(SourceModel.Expr eqRel) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.equivalenceClasses), eqRel});
		}

		/**
		 * Name binding for function: equivalenceClasses.
		 * @see #equivalenceClasses(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName equivalenceClasses = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"equivalenceClasses");

		/**
		 * Takes type value and returns a list of top level types.
		 * <p>
		 * Example: flattenTypes (AppType (AppType Int Char) Boolean) == [ Int, Char, Boolean ] 
		 * 
		 * @param type (CAL type: <code>Cal.Internal.Optimizer_Type.Type</code>)
		 *          The type to flatten.
		 * @return (CAL type: <code>[Cal.Internal.Optimizer_Type.Type]</code>) 
		 *          The type resulting from flattening the input type using AppType
		 */
		public static final SourceModel.Expr flattenAppType(SourceModel.Expr type) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.flattenAppType), type});
		}

		/**
		 * Name binding for function: flattenAppType.
		 * @see #flattenAppType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName flattenAppType = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"flattenAppType");

		/**
		 * Takes type value and returns a list of top level types.
		 * <p>
		 * Example: flattenTypes (FunctionType (FunctionType Int Char) Boolean) == [ Int, Char, Boolean ] 
		 * 
		 * @param type (CAL type: <code>Cal.Internal.Optimizer_Type.Type</code>)
		 *          The type to flatten.
		 * @return (CAL type: <code>[Cal.Internal.Optimizer_Type.Type]</code>) 
		 *          The type resulting from flattening the input type using AppType
		 */
		public static final SourceModel.Expr flattenType(SourceModel.Expr type) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.flattenType), type});
		}

		/**
		 * Name binding for function: flattenType.
		 * @see #flattenType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName flattenType = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"flattenType");

		/**
		 * Helper binding method for function: inputType. 
		 * @param jType
		 * @return the SourceModule.expr representing an application of inputType
		 */
		public static final SourceModel.Expr inputType(SourceModel.Expr jType) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inputType), jType});
		}

		/**
		 * Name binding for function: inputType.
		 * @see #inputType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName inputType = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"inputType");

		/**
		 * Helper binding method for function: jobjectToType. 
		 * @param jobj
		 * @return the SourceModule.expr representing an application of jobjectToType
		 */
		public static final SourceModel.Expr jobjectToType(SourceModel.Expr jobj) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jobjectToType), jobj});
		}

		/**
		 * Name binding for function: jobjectToType.
		 * @see #jobjectToType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jobjectToType = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"jobjectToType");

		/**
		 * Take the given eqClass set and augment it with values using the given eqRelation.
		 * @param eqClass (CAL type: <code>Cal.Core.Prelude.Eq a => [a]</code>)
		 *          The eqClass to augment.
		 * @param eqRel (CAL type: <code>Cal.Core.Prelude.Eq a => [(a, a)]</code>)
		 *          The equivalence relation to use to augment the given eqClass.
		 * @return (CAL type: <code>Cal.Core.Prelude.Eq a => ([a], [(a, a)])</code>) 
		 *          eqClass with all the values from eqRel that are equal.
		 */
		public static final SourceModel.Expr makeEqClass(SourceModel.Expr eqClass, SourceModel.Expr eqRel) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeEqClass), eqClass, eqRel});
		}

		/**
		 * Name binding for function: makeEqClass.
		 * @see #makeEqClass(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeEqClass = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"makeEqClass");

		/**
		 * Helper binding method for function: optimizerHelper_getTypeConstructorType. 
		 * @param type
		 * @return the SourceModule.expr representing an application of optimizerHelper_getTypeConstructorType
		 */
		public static final SourceModel.Expr optimizerHelper_getTypeConstructorType(SourceModel.Expr type) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_getTypeConstructorType), type});
		}

		/**
		 * Name binding for function: optimizerHelper_getTypeConstructorType.
		 * @see #optimizerHelper_getTypeConstructorType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName optimizerHelper_getTypeConstructorType = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"optimizerHelper_getTypeConstructorType");

		/**
		 * Helper binding method for function: optimizerHelper_getTypeType. 
		 * @param type
		 * @return the SourceModule.expr representing an application of optimizerHelper_getTypeType
		 */
		public static final SourceModel.Expr optimizerHelper_getTypeType(SourceModel.Expr type) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_getTypeType), type});
		}

		/**
		 * Name binding for function: optimizerHelper_getTypeType.
		 * @see #optimizerHelper_getTypeType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName optimizerHelper_getTypeType = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"optimizerHelper_getTypeType");

		/**
		 * Helper binding method for function: optimizerHelper_makeListType. 
		 * @param elementType
		 * @return the SourceModule.expr representing an application of optimizerHelper_makeListType
		 */
		public static final SourceModel.Expr optimizerHelper_makeListType(SourceModel.Expr elementType) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_makeListType), elementType});
		}

		/**
		 * Name binding for function: optimizerHelper_makeListType.
		 * @see #optimizerHelper_makeListType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName optimizerHelper_makeListType = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"optimizerHelper_makeListType");

		/**
		 * Helper binding method for function: optimizerHelper_newTypeVar. 
		 * @return the SourceModule.expr representing an application of optimizerHelper_newTypeVar
		 */
		public static final SourceModel.Expr optimizerHelper_newTypeVar() {
			return 
				SourceModel.Expr.Var.make(Functions.optimizerHelper_newTypeVar);
		}

		/**
		 * Name binding for function: optimizerHelper_newTypeVar.
		 * @see #optimizerHelper_newTypeVar()
		 */
		public static final QualifiedName optimizerHelper_newTypeVar = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"optimizerHelper_newTypeVar");

		/**
		 * Helper binding method for function: optimizerHelper_objectAsTypeExpr. 
		 * @param object
		 * @return the SourceModule.expr representing an application of optimizerHelper_objectAsTypeExpr
		 */
		public static final SourceModel.Expr optimizerHelper_objectAsTypeExpr(SourceModel.Expr object) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_objectAsTypeExpr), object});
		}

		/**
		 * Name binding for function: optimizerHelper_objectAsTypeExpr.
		 * @see #optimizerHelper_objectAsTypeExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName optimizerHelper_objectAsTypeExpr = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"optimizerHelper_objectAsTypeExpr");

		/**
		 * Helper binding method for function: optimizerHelper_typeExprAsJObject. 
		 * @param type
		 * @return the SourceModule.expr representing an application of optimizerHelper_typeExprAsJObject
		 */
		public static final SourceModel.Expr optimizerHelper_typeExprAsJObject(SourceModel.Expr type) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_typeExprAsJObject), type});
		}

		/**
		 * Name binding for function: optimizerHelper_typeExprAsJObject.
		 * @see #optimizerHelper_typeExprAsJObject(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName optimizerHelper_typeExprAsJObject = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"optimizerHelper_typeExprAsJObject");

		/**
		 * Helper binding method for function: optimizerHelper_typeExprAsJTypeVar. 
		 * @param type
		 * @return the SourceModule.expr representing an application of optimizerHelper_typeExprAsJTypeVar
		 */
		public static final SourceModel.Expr optimizerHelper_typeExprAsJTypeVar(SourceModel.Expr type) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_typeExprAsJTypeVar), type});
		}

		/**
		 * Name binding for function: optimizerHelper_typeExprAsJTypeVar.
		 * @see #optimizerHelper_typeExprAsJTypeVar(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName optimizerHelper_typeExprAsJTypeVar = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"optimizerHelper_typeExprAsJTypeVar");

		/**
		 * Helper binding method for function: optimizerHelper_typeExpr_asTypeConstructor. 
		 * @param typeExpr
		 * @return the SourceModule.expr representing an application of optimizerHelper_typeExpr_asTypeConstructor
		 */
		public static final SourceModel.Expr optimizerHelper_typeExpr_asTypeConstructor(SourceModel.Expr typeExpr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_typeExpr_asTypeConstructor), typeExpr});
		}

		/**
		 * Name binding for function: optimizerHelper_typeExpr_asTypeConstructor.
		 * @see #optimizerHelper_typeExpr_asTypeConstructor(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName optimizerHelper_typeExpr_asTypeConstructor = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"optimizerHelper_typeExpr_asTypeConstructor");

		/**
		 * Helper binding method for function: optimizerHelper_typeExpr_getName. 
		 * @param type
		 * @return the SourceModule.expr representing an application of optimizerHelper_typeExpr_getName
		 */
		public static final SourceModel.Expr optimizerHelper_typeExpr_getName(SourceModel.Expr type) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_typeExpr_getName), type});
		}

		/**
		 * Name binding for function: optimizerHelper_typeExpr_getName.
		 * @see #optimizerHelper_typeExpr_getName(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName optimizerHelper_typeExpr_getName = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"optimizerHelper_typeExpr_getName");

		/**
		 * Helper binding method for function: optimizerHelper_typeVar_hasClassConstraints. 
		 * @param typeVar
		 * @return the SourceModule.expr representing an application of optimizerHelper_typeVar_hasClassConstraints
		 */
		public static final SourceModel.Expr optimizerHelper_typeVar_hasClassConstraints(SourceModel.Expr typeVar) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_typeVar_hasClassConstraints), typeVar});
		}

		/**
		 * Name binding for function: optimizerHelper_typeVar_hasClassConstraints.
		 * @see #optimizerHelper_typeVar_hasClassConstraints(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName optimizerHelper_typeVar_hasClassConstraints = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"optimizerHelper_typeVar_hasClassConstraints");

		/**
		 * Helper binding method for function: outputType. 
		 * @param type
		 * @return the SourceModule.expr representing an application of outputType
		 */
		public static final SourceModel.Expr outputType(SourceModel.Expr type) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputType), type});
		}

		/**
		 * Name binding for function: outputType.
		 * @see #outputType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName outputType = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"outputType");

		/**
		 * Takes the given type expression and reduces it to an irreducable type expression. During the process
		 * pairs of types that must be unified are calculated. These are kept in an accumulator.
		 * @param current (CAL type: <code>([(Cal.Internal.Optimizer_Type.Type, Cal.Internal.Optimizer_Type.Type)], Cal.Internal.Optimizer_Type.Type)</code>)
		 *          A pair, the first element is an accumulator of type expression that are to be unifed and
		 * the second is the current type of the expression.
		 * @return (CAL type: <code>([(Cal.Internal.Optimizer_Type.Type, Cal.Internal.Optimizer_Type.Type)], Cal.Internal.Optimizer_Type.Type)</code>) 
		 *          A pair consisting of an accumulator of type expression pairs that must unify and the irreducable type expression corresponding to the input type expression.
		 */
		public static final SourceModel.Expr reduceType(SourceModel.Expr current) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.reduceType), current});
		}

		/**
		 * Name binding for function: reduceType.
		 * @see #reduceType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName reduceType = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"reduceType");

		/**
		 * Helper binding method for function: reduceTypeExamples. 
		 * @return the SourceModule.expr representing an application of reduceTypeExamples
		 */
		public static final SourceModel.Expr reduceTypeExamples() {
			return SourceModel.Expr.Var.make(Functions.reduceTypeExamples);
		}

		/**
		 * Name binding for function: reduceTypeExamples.
		 * @see #reduceTypeExamples()
		 */
		public static final QualifiedName reduceTypeExamples = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"reduceTypeExamples");

		/**
		 * There is a compound type of the form (AppType typeFunc typeArg). This is a type expression that
		 * means the type resulting from applying the function typeFunc to the argument typeArg. Reduce 
		 * takes a type expression of this form and generates a list of pairs of type expression that must 
		 * unify as a result of applying typeFunc to typeArg. For example, AppType (Int -&gt; Int) a, results in
		 * the new pair (Int, a) with a resulting type of Int.
		 * @param acc (CAL type: <code>[(Cal.Internal.Optimizer_Type.Type, Cal.Internal.Optimizer_Type.Type)]</code>)
		 *          The list of type expressions that must unify.
		 * @param typeE (CAL type: <code>Cal.Internal.Optimizer_Type.Type</code>)
		 *          The type expression to reduce in order to generate the new unification pairs and output type.
		 * @return (CAL type: <code>([(Cal.Internal.Optimizer_Type.Type, Cal.Internal.Optimizer_Type.Type)], Cal.Internal.Optimizer_Type.Type)</code>) 
		 *          A pair that is the accumulator of type expression to be unified and the actual output type of the input type expression.
		 */
		public static final SourceModel.Expr reduceTypeOnce(SourceModel.Expr acc, SourceModel.Expr typeE) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.reduceTypeOnce), acc, typeE});
		}

		/**
		 * Name binding for function: reduceTypeOnce.
		 * @see #reduceTypeOnce(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName reduceTypeOnce = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"reduceTypeOnce");

		/**
		 * Helper binding method for function: reduceTypeOnceExamples. 
		 * @return the SourceModule.expr representing an application of reduceTypeOnceExamples
		 */
		public static final SourceModel.Expr reduceTypeOnceExamples() {
			return SourceModel.Expr.Var.make(Functions.reduceTypeOnceExamples);
		}

		/**
		 * Name binding for function: reduceTypeOnceExamples.
		 * @see #reduceTypeOnceExamples()
		 */
		public static final QualifiedName reduceTypeOnceExamples = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"reduceTypeOnceExamples");

		/**
		 * Helper binding method for function: showType. 
		 * @param type
		 * @return the SourceModule.expr representing an application of showType
		 */
		public static final SourceModel.Expr showType(SourceModel.Expr type) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showType), type});
		}

		/**
		 * Name binding for function: showType.
		 * @see #showType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showType = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"showType");

		/**
		 * Helper binding method for function: tTest. 
		 * @return the SourceModule.expr representing an application of tTest
		 */
		public static final SourceModel.Expr tTest() {
			return SourceModel.Expr.Var.make(Functions.tTest);
		}

		/**
		 * Name binding for function: tTest.
		 * @see #tTest()
		 */
		public static final QualifiedName tTest = 
			QualifiedName.make(CAL_Optimizer_Type_internal.MODULE_NAME, "tTest");

		/**
		 * Helper binding method for function: test_type_applyBindings. 
		 * @return the SourceModule.expr representing an application of test_type_applyBindings
		 */
		public static final SourceModel.Expr test_type_applyBindings() {
			return SourceModel.Expr.Var.make(Functions.test_type_applyBindings);
		}

		/**
		 * Name binding for function: test_type_applyBindings.
		 * @see #test_type_applyBindings()
		 */
		public static final QualifiedName test_type_applyBindings = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"test_type_applyBindings");

		/**
		 * Traverse the given type and apply the given transformer. The transformation is done bottom up.
		 * @param acc (CAL type: <code>acc</code>)
		 *          Accumulator maintained through the traversal.
		 * @param type (CAL type: <code>Cal.Internal.Optimizer_Type.Type</code>)
		 *          The type to traverse
		 * @param transformer (CAL type: <code>acc -> Cal.Internal.Optimizer_Type.Type -> (acc, Cal.Internal.Optimizer_Type.Type)</code>)
		 *          The transformation function to apply.
		 * @return (CAL type: <code>(acc, Cal.Internal.Optimizer_Type.Type)</code>) 
		 *          The result of applying the transformer to the given type expression.
		 */
		public static final SourceModel.Expr transformType(SourceModel.Expr acc, SourceModel.Expr type, SourceModel.Expr transformer) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformType), acc, type, transformer});
		}

		/**
		 * Name binding for function: transformType.
		 * @see #transformType(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformType = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"transformType");

		/**
		 * Helper binding method for function: typeConsApp_getArg. 
		 * @param jTypeConsApp
		 * @param argN
		 * @return the SourceModule.expr representing an application of typeConsApp_getArg
		 */
		public static final SourceModel.Expr typeConsApp_getArg(SourceModel.Expr jTypeConsApp, SourceModel.Expr argN) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.typeConsApp_getArg), jTypeConsApp, argN});
		}

		/**
		 * @see #typeConsApp_getArg(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param jTypeConsApp
		 * @param argN
		 * @return the SourceModel.Expr representing an application of typeConsApp_getArg
		 */
		public static final SourceModel.Expr typeConsApp_getArg(SourceModel.Expr jTypeConsApp, int argN) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.typeConsApp_getArg), jTypeConsApp, SourceModel.Expr.makeIntValue(argN)});
		}

		/**
		 * Name binding for function: typeConsApp_getArg.
		 * @see #typeConsApp_getArg(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName typeConsApp_getArg = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"typeConsApp_getArg");

		/**
		 * Helper binding method for function: typeConsApp_getNArgs. 
		 * @param jTypeConsApp
		 * @return the SourceModule.expr representing an application of typeConsApp_getNArgs
		 */
		public static final SourceModel.Expr typeConsApp_getNArgs(SourceModel.Expr jTypeConsApp) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.typeConsApp_getNArgs), jTypeConsApp});
		}

		/**
		 * Name binding for function: typeConsApp_getNArgs.
		 * @see #typeConsApp_getNArgs(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName typeConsApp_getNArgs = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"typeConsApp_getNArgs");

		/**
		 * Helper binding method for function: typeToJObject. 
		 * @param qn
		 * @return the SourceModule.expr representing an application of typeToJObject
		 */
		public static final SourceModel.Expr typeToJObject(SourceModel.Expr qn) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.typeToJObject), qn});
		}

		/**
		 * Name binding for function: typeToJObject.
		 * @see #typeToJObject(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName typeToJObject = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"typeToJObject");

		/**
		 * Helper binding method for function: typeVar_hashCode. 
		 * @param jTypeVar
		 * @return the SourceModule.expr representing an application of typeVar_hashCode
		 */
		public static final SourceModel.Expr typeVar_hashCode(SourceModel.Expr jTypeVar) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.typeVar_hashCode), jTypeVar});
		}

		/**
		 * Name binding for function: typeVar_hashCode.
		 * @see #typeVar_hashCode(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName typeVar_hashCode = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"typeVar_hashCode");

		/**
		 * Helper binding method for function: typeVar_makeFunType. 
		 * @param domain
		 * @param codomain
		 * @return the SourceModule.expr representing an application of typeVar_makeFunType
		 */
		public static final SourceModel.Expr typeVar_makeFunType(SourceModel.Expr domain, SourceModel.Expr codomain) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.typeVar_makeFunType), domain, codomain});
		}

		/**
		 * Name binding for function: typeVar_makeFunType.
		 * @see #typeVar_makeFunType(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName typeVar_makeFunType = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"typeVar_makeFunType");

		/**
		 * Helper binding method for function: typeVar_makeListType. 
		 * @param elementType
		 * @return the SourceModule.expr representing an application of typeVar_makeListType
		 */
		public static final SourceModel.Expr typeVar_makeListType(SourceModel.Expr elementType) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.typeVar_makeListType), elementType});
		}

		/**
		 * Name binding for function: typeVar_makeListType.
		 * @see #typeVar_makeListType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName typeVar_makeListType = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"typeVar_makeListType");

		/**
		 * The bindings is a list that maps variables to values. The values may contain more variables. This function
		 * replaces the variables in the values side with their values.
		 * @param bindings (CAL type: <code>[(Cal.Internal.Optimizer_Type.Type, Cal.Internal.Optimizer_Type.Type)]</code>)
		 *          The list of bindings that associate a variable with a type value.
		 * @param defs (CAL type: <code>[(Cal.Internal.Optimizer_Type.Type, Cal.Internal.Optimizer_Type.Type)]</code>)
		 *          Each binding in def will have the variable replaced using the bindings from the binding list
		 * @return (CAL type: <code>[(Cal.Internal.Optimizer_Type.Type, Cal.Internal.Optimizer_Type.Type)]</code>) 
		 *          The defs list with all of the variables replaced by their values from the bindings list.
		 */
		public static final SourceModel.Expr type_applyBindings(SourceModel.Expr bindings, SourceModel.Expr defs) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.type_applyBindings), bindings, defs});
		}

		/**
		 * Name binding for function: type_applyBindings.
		 * @see #type_applyBindings(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName type_applyBindings = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"type_applyBindings");

		/**
		 * Helper binding method for function: type_applyStrictness. 
		 * @param type
		 * @param moreStrict
		 * @return the SourceModule.expr representing an application of type_applyStrictness
		 */
		public static final SourceModel.Expr type_applyStrictness(SourceModel.Expr type, SourceModel.Expr moreStrict) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.type_applyStrictness), type, moreStrict});
		}

		/**
		 * @see #type_applyStrictness(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param type
		 * @param moreStrict
		 * @return the SourceModel.Expr representing an application of type_applyStrictness
		 */
		public static final SourceModel.Expr type_applyStrictness(SourceModel.Expr type, boolean moreStrict) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.type_applyStrictness), type, SourceModel.Expr.makeBooleanValue(moreStrict)});
		}

		/**
		 * Name binding for function: type_applyStrictness.
		 * @see #type_applyStrictness(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName type_applyStrictness = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"type_applyStrictness");

		/**
		 * Helper binding method for function: type_atLeastAsStrict. 
		 * @param lessStrict
		 * @param atLeastAsStrict
		 * @return the SourceModule.expr representing an application of type_atLeastAsStrict
		 */
		public static final SourceModel.Expr type_atLeastAsStrict(SourceModel.Expr lessStrict, SourceModel.Expr atLeastAsStrict) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.type_atLeastAsStrict), lessStrict, atLeastAsStrict});
		}

		/**
		 * Name binding for function: type_atLeastAsStrict.
		 * @see #type_atLeastAsStrict(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName type_atLeastAsStrict = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"type_atLeastAsStrict");

		/**
		 * Helper binding method for function: type_hasStrictArguments. 
		 * @param type
		 * @return the SourceModule.expr representing an application of type_hasStrictArguments
		 */
		public static final SourceModel.Expr type_hasStrictArguments(SourceModel.Expr type) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.type_hasStrictArguments), type});
		}

		/**
		 * Name binding for function: type_hasStrictArguments.
		 * @see #type_hasStrictArguments(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName type_hasStrictArguments = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"type_hasStrictArguments");

		/**
		 * Helper binding method for function: type_isFunctionType. 
		 * @param type
		 * @return the SourceModule.expr representing an application of type_isFunctionType
		 */
		public static final SourceModel.Expr type_isFunctionType(SourceModel.Expr type) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.type_isFunctionType), type});
		}

		/**
		 * Name binding for function: type_isFunctionType.
		 * @see #type_isFunctionType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName type_isFunctionType = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"type_isFunctionType");

		/**
		 * Helper binding method for function: type_isStrict. 
		 * @param type
		 * @return the SourceModule.expr representing an application of type_isStrict
		 */
		public static final SourceModel.Expr type_isStrict(SourceModel.Expr type) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.type_isStrict), type});
		}

		/**
		 * Name binding for function: type_isStrict.
		 * @see #type_isStrict(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName type_isStrict = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"type_isStrict");

		/**
		 * Helper binding method for function: type_isVar. 
		 * @param type
		 * @return the SourceModule.expr representing an application of type_isVar
		 */
		public static final SourceModel.Expr type_isVar(SourceModel.Expr type) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.type_isVar), type});
		}

		/**
		 * Name binding for function: type_isVar.
		 * @see #type_isVar(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName type_isVar = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"type_isVar");

		/**
		 * Takes a list of bindings and returns a list of the top level types in order of arguments. The arguments
		 * are denoted by (TypeId typeId::Int) objects. The first argument has typeId of one and so on.
		 * @param list (CAL type: <code>[(Cal.Internal.Optimizer_Type.Type, Cal.Internal.Optimizer_Type.Type)]</code>)
		 *          The list of type and type value pairs to construct the type argument from.
		 * @return (CAL type: <code>[Cal.Internal.Optimizer_Type.Type]</code>) 
		 *          The list of types that correspond to arguments in the argument order.
		 */
		public static final SourceModel.Expr type_listToType(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.type_listToType), list});
		}

		/**
		 * Name binding for function: type_listToType.
		 * @see #type_listToType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName type_listToType = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"type_listToType");

		/**
		 * Helper binding method for function: type_listToTypeExamples. 
		 * @return the SourceModule.expr representing an application of type_listToTypeExamples
		 */
		public static final SourceModel.Expr type_listToTypeExamples() {
			return SourceModel.Expr.Var.make(Functions.type_listToTypeExamples);
		}

		/**
		 * Name binding for function: type_listToTypeExamples.
		 * @see #type_listToTypeExamples()
		 */
		public static final QualifiedName type_listToTypeExamples = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"type_listToTypeExamples");

		/**
		 * Select the most specific type from the given non-empty list. Variables are less specific than
		 * any other type. Variables with no constraints are less specific that variables with constraints.
		 * @param types (CAL type: <code>[Cal.Internal.Optimizer_Type.Type]</code>)
		 * @return (CAL type: <code>Cal.Internal.Optimizer_Type.Type</code>) 
		 */
		public static final SourceModel.Expr type_selectMostSpecificType(SourceModel.Expr types) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.type_selectMostSpecificType), types});
		}

		/**
		 * Name binding for function: type_selectMostSpecificType.
		 * @see #type_selectMostSpecificType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName type_selectMostSpecificType = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"type_selectMostSpecificType");

		/**
		 * Helper binding method for function: type_setStrictness. 
		 * @param type
		 * @param value
		 * @return the SourceModule.expr representing an application of type_setStrictness
		 */
		public static final SourceModel.Expr type_setStrictness(SourceModel.Expr type, SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.type_setStrictness), type, value});
		}

		/**
		 * @see #type_setStrictness(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param type
		 * @param value
		 * @return the SourceModel.Expr representing an application of type_setStrictness
		 */
		public static final SourceModel.Expr type_setStrictness(SourceModel.Expr type, boolean value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.type_setStrictness), type, SourceModel.Expr.makeBooleanValue(value)});
		}

		/**
		 * Name binding for function: type_setStrictness.
		 * @see #type_setStrictness(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName type_setStrictness = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"type_setStrictness");

		/**
		 * 
		 * @param generalTypes (CAL type: <code>[Cal.Internal.Optimizer_Type.Type]</code>)
		 *          The type that is to be specialized.
		 * @param argumentTypesIn (CAL type: <code>[Cal.Core.Prelude.Maybe Cal.Internal.Optimizer_Type.Type]</code>)
		 *          The type of the argument that is present.
		 * @return (CAL type: <code>[Cal.Internal.Optimizer_Type.Type]</code>) 
		 *          The specialization of the given getneral type using the given arguements.
		 */
		public static final SourceModel.Expr type_specializeArguments(SourceModel.Expr generalTypes, SourceModel.Expr argumentTypesIn) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.type_specializeArguments), generalTypes, argumentTypesIn});
		}

		/**
		 * Name binding for function: type_specializeArguments.
		 * @see #type_specializeArguments(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName type_specializeArguments = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"type_specializeArguments");

		/**
		 * This takes a list of type unification pairs and unifies the types that comprise the type expression. The
		 * input expression are decompose and their parts unified until this process can no longer be performed. The 
		 * resulting list will contain tuples where the first value is a type variable and the second is its value.
		 * @param todo (CAL type: <code>[(Cal.Internal.Optimizer_Type.Type, Cal.Internal.Optimizer_Type.Type)]</code>)
		 *          A list of unification pairs.
		 * @return (CAL type: <code>[(Cal.Internal.Optimizer_Type.Type, Cal.Internal.Optimizer_Type.Type)]</code>) 
		 */
		public static final SourceModel.Expr type_unify(SourceModel.Expr todo) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.type_unify), todo});
		}

		/**
		 * Name binding for function: type_unify.
		 * @see #type_unify(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName type_unify = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"type_unify");

		/**
		 * Helper binding method for function: type_unifyExamples. 
		 * @return the SourceModule.expr representing an application of type_unifyExamples
		 */
		public static final SourceModel.Expr type_unifyExamples() {
			return SourceModel.Expr.Var.make(Functions.type_unifyExamples);
		}

		/**
		 * Name binding for function: type_unifyExamples.
		 * @see #type_unifyExamples()
		 */
		public static final QualifiedName type_unifyExamples = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"type_unifyExamples");

		/**
		 * Helper binding method for function: type_unifyHelper. 
		 * @param todo
		 * @return the SourceModule.expr representing an application of type_unifyHelper
		 */
		public static final SourceModel.Expr type_unifyHelper(SourceModel.Expr todo) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.type_unifyHelper), todo});
		}

		/**
		 * Name binding for function: type_unifyHelper.
		 * @see #type_unifyHelper(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName type_unifyHelper = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"type_unifyHelper");

		/**
		 * Helper binding method for function: types_atLeastAsStrict. 
		 * @param lessStrict
		 * @param atLeastAsStrict
		 * @return the SourceModule.expr representing an application of types_atLeastAsStrict
		 */
		public static final SourceModel.Expr types_atLeastAsStrict(SourceModel.Expr lessStrict, SourceModel.Expr atLeastAsStrict) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.types_atLeastAsStrict), lessStrict, atLeastAsStrict});
		}

		/**
		 * Name binding for function: types_atLeastAsStrict.
		 * @see #types_atLeastAsStrict(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName types_atLeastAsStrict = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"types_atLeastAsStrict");

		/**
		 * Helper binding method for function: types_hasStrictArguments. 
		 * @param types
		 * @return the SourceModule.expr representing an application of types_hasStrictArguments
		 */
		public static final SourceModel.Expr types_hasStrictArguments(SourceModel.Expr types) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.types_hasStrictArguments), types});
		}

		/**
		 * Name binding for function: types_hasStrictArguments.
		 * @see #types_hasStrictArguments(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName types_hasStrictArguments = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"types_hasStrictArguments");

		/**
		 * Takes a list of types and converts then into a single type expression using AppType. This
		 * is left associative.
		 * <p>
		 * Example: unflattenTypes [ Int, Char, Boolean ] == (AppType (AppType Int Char) Boolean)
		 * 
		 * @param types (CAL type: <code>[Cal.Internal.Optimizer_Type.Type]</code>)
		 *          The list of types to unflatten.
		 * @return (CAL type: <code>Cal.Internal.Optimizer_Type.Type</code>) 
		 *          The type resulting from combining the input types using AppType.
		 */
		public static final SourceModel.Expr unflattenAppTypes(SourceModel.Expr types) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.unflattenAppTypes), types});
		}

		/**
		 * Name binding for function: unflattenAppTypes.
		 * @see #unflattenAppTypes(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName unflattenAppTypes = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"unflattenAppTypes");

		/**
		 * Takes a list of types and converts then into a single type expression using FunctionType. This
		 * is right associative.
		 * <p>
		 * Example: unflattenTypes [ Int, Char, Boolean ] == (FunctionType Int (FunctionType Char Boolean))
		 * 
		 * @param types (CAL type: <code>[Cal.Internal.Optimizer_Type.Type]</code>)
		 *          The list of types to unflatten.
		 * @return (CAL type: <code>Cal.Internal.Optimizer_Type.Type</code>) 
		 *          The type resulting from combining the input types using FunctionType.
		 */
		public static final SourceModel.Expr unflattenFunctionTypes(SourceModel.Expr types) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.unflattenFunctionTypes), types});
		}

		/**
		 * Name binding for function: unflattenFunctionTypes.
		 * @see #unflattenFunctionTypes(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName unflattenFunctionTypes = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"unflattenFunctionTypes");

		/**
		 * Helper binding method for function: unitTests. 
		 * @return the SourceModule.expr representing an application of unitTests
		 */
		public static final SourceModel.Expr unitTests() {
			return SourceModel.Expr.Var.make(Functions.unitTests);
		}

		/**
		 * Name binding for function: unitTests.
		 * @see #unitTests()
		 */
		public static final QualifiedName unitTests = 
			QualifiedName.make(
				CAL_Optimizer_Type_internal.MODULE_NAME, 
				"unitTests");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = -1338937226;

}
