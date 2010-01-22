/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_Optimizer_Traversers_internal.java)
 * was generated from CAL module: Cal.Internal.Optimizer_Traversers.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Internal.Optimizer_Traversers module from Java code.
 *  
 * Creation date: Fri Jan 22 15:07:58 PST 2010
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
 * This module contains all functions that are used to traverse and modify expressions. The two 
 * traversers are transformAcc and transform. <code>Cal.Internal.Optimizer_Traversers.transformAcc</code> traverses and the expression bottom up 
 * and maintains an accumulator throughout the traversal. <code>Cal.Internal.Optimizer_Traversers.transform</code> traverses the expression bottom up 
 * and applies the given transformers. The difference is that transformAcc only maintains TransformState
 * but transform maintains TransformHistory as well.
 * <p>
 * Also included in this file are some functions for combining transformations. For example, combineTransforms 
 * takes two transformation functions and returns a single transformation function.
 * <p>
 * TODO: Get rid of transformAcc. 
 * 
 * @author Greg McClement
 */
public final class CAL_Optimizer_Traversers_internal {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Internal.Optimizer_Traversers");

	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Internal.Optimizer_Traversers module.
	 */
	public static final class Functions {
		/**
		 * Helper binding method for function: canConvertToType. 
		 * @param state
		 * @param expr
		 * @return the SourceModule.expr representing an application of canConvertToType
		 */
		public static final SourceModel.Expr canConvertToType(SourceModel.Expr state, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.canConvertToType), state, expr});
		}

		/**
		 * Name binding for function: canConvertToType.
		 * @see #canConvertToType(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName canConvertToType = 
			QualifiedName.make(
				CAL_Optimizer_Traversers_internal.MODULE_NAME, 
				"canConvertToType");

		/**
		 * Takes two transformations and combines then by performing the first and then the second.
		 * @param t1 (CAL type: <code>Cal.Internal.Optimizer_State.TransformState -> Cal.Internal.Optimizer_Expression.Expression -> Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The first transformation to combine.
		 * @param t2 (CAL type: <code>Cal.Internal.Optimizer_State.TransformState -> Cal.Internal.Optimizer_Expression.Expression -> Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The second transformation to combine.
		 * @param arg_3 (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param arg_4 (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>) 
		 *          A function that performs t2 and then performs t1 on the input expression.
		 */
		public static final SourceModel.Expr combineTransforms(SourceModel.Expr t1, SourceModel.Expr t2, SourceModel.Expr arg_3, SourceModel.Expr arg_4) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.combineTransforms), t1, t2, arg_3, arg_4});
		}

		/**
		 * Name binding for function: combineTransforms.
		 * @see #combineTransforms(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName combineTransforms = 
			QualifiedName.make(
				CAL_Optimizer_Traversers_internal.MODULE_NAME, 
				"combineTransforms");

		/**
		 * Helper binding method for function: combineTransformsWithHistory. 
		 * @param t1
		 * @param t2
		 * @param arg_3
		 * @param arg_4
		 * @param arg_5
		 * @return the SourceModule.expr representing an application of combineTransformsWithHistory
		 */
		public static final SourceModel.Expr combineTransformsWithHistory(SourceModel.Expr t1, SourceModel.Expr t2, SourceModel.Expr arg_3, SourceModel.Expr arg_4, SourceModel.Expr arg_5) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.combineTransformsWithHistory), t1, t2, arg_3, arg_4, arg_5});
		}

		/**
		 * Name binding for function: combineTransformsWithHistory.
		 * @see #combineTransformsWithHistory(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName combineTransformsWithHistory = 
			QualifiedName.make(
				CAL_Optimizer_Traversers_internal.MODULE_NAME, 
				"combineTransformsWithHistory");

		/**
		 * Convert the given expression into a type expression. A list of name to type can be used to lookup the type
		 * of an input expression after the unification is applied.
		 * <p>
		 * NOTE This is not fully implemented yet.
		 * 
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 *          The context of the current traversal.
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 *          The history of the current transformation.
		 * @param counter (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          A counter used to generate the TypeId objects.
		 * @param nToT (CAL type: <code>[(Cal.Internal.Optimizer_Expression.QualifiedName, Cal.Internal.Optimizer_Type.Type)]</code>)
		 *          A list that maps Var's to TypeId's. This can be used to look up the type of the variables.
		 * @param expression (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The expression to convert to a type.
		 * @return (CAL type: <code>(Cal.Core.Prelude.Int, [(Cal.Internal.Optimizer_Expression.QualifiedName, Cal.Internal.Optimizer_Type.Type)], Cal.Internal.Optimizer_Type.Type)</code>) 
		 *          A triplet of counter of the TypeId, a list that maps Var's to TypeId's, and the resulting type.
		 */
		public static final SourceModel.Expr convertToType(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr counter, SourceModel.Expr nToT, SourceModel.Expr expression) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.convertToType), state, history, counter, nToT, expression});
		}

		/**
		 * @see #convertToType(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param state
		 * @param history
		 * @param counter
		 * @param nToT
		 * @param expression
		 * @return the SourceModel.Expr representing an application of convertToType
		 */
		public static final SourceModel.Expr convertToType(SourceModel.Expr state, SourceModel.Expr history, int counter, SourceModel.Expr nToT, SourceModel.Expr expression) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.convertToType), state, history, SourceModel.Expr.makeIntValue(counter), nToT, expression});
		}

		/**
		 * Name binding for function: convertToType.
		 * @see #convertToType(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName convertToType = 
			QualifiedName.make(
				CAL_Optimizer_Traversers_internal.MODULE_NAME, 
				"convertToType");

		/**
		 * This will detect that variables are strict because they are arguments to a function that
		 * has them plinged. See test118 for an example.
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param switchExpr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>) 
		 */
		public static final SourceModel.Expr detectPlingedArguments(SourceModel.Expr state, SourceModel.Expr switchExpr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.detectPlingedArguments), state, switchExpr});
		}

		/**
		 * Name binding for function: detectPlingedArguments.
		 * @see #detectPlingedArguments(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName detectPlingedArguments = 
			QualifiedName.make(
				CAL_Optimizer_Traversers_internal.MODULE_NAME, 
				"detectPlingedArguments");

		/**
		 * Helper binding method for function: prelude_eager. 
		 * @return the SourceModule.expr representing an application of prelude_eager
		 */
		public static final SourceModel.Expr prelude_eager() {
			return SourceModel.Expr.Var.make(Functions.prelude_eager);
		}

		/**
		 * Name binding for function: prelude_eager.
		 * @see #prelude_eager()
		 */
		public static final QualifiedName prelude_eager = 
			QualifiedName.make(
				CAL_Optimizer_Traversers_internal.MODULE_NAME, 
				"prelude_eager");

		/**
		 * Helper binding method for function: trace2. 
		 * @param unused
		 * @param v
		 * @return the SourceModule.expr representing an application of trace2
		 */
		public static final SourceModel.Expr trace2(SourceModel.Expr unused, SourceModel.Expr v) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.trace2), unused, v});
		}

		/**
		 * @see #trace2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param unused
		 * @param v
		 * @return the SourceModel.Expr representing an application of trace2
		 */
		public static final SourceModel.Expr trace2(java.lang.String unused, SourceModel.Expr v) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.trace2), SourceModel.Expr.makeStringValue(unused), v});
		}

		/**
		 * Name binding for function: trace2.
		 * @see #trace2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName trace2 = 
			QualifiedName.make(
				CAL_Optimizer_Traversers_internal.MODULE_NAME, 
				"trace2");

		/**
		 * Traverses the given expression and applies the given transformations. The TransformState
		 * and TransformationHistory is maintained by this function.
		 * <p>
		 * TODO Go through this and make sure that the names are updated correctly.
		 * 
		 * @param currentState (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 *          Information about the context of the current subexpression. This is used to avoid inlining case expressions in strict contexts.
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 *          Information about the history of the current transformation.
		 * @param before (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The expression to transform.
		 * @param transformer (CAL type: <code>Cal.Internal.Optimizer_State.TransformState -> Cal.Internal.Optimizer_State.TransformHistory -> Cal.Internal.Optimizer_Expression.Expression -> (Cal.Internal.Optimizer_State.TransformHistory, Cal.Core.Prelude.Boolean, Cal.Internal.Optimizer_Expression.Expression)</code>)
		 *          The function to use to transform the input expression.
		 * @return (CAL type: <code>(Cal.Internal.Optimizer_State.TransformHistory, Cal.Core.Prelude.Boolean, Cal.Internal.Optimizer_Expression.Expression)</code>) 
		 *          The transformed expression.
		 */
		public static final SourceModel.Expr transform(SourceModel.Expr currentState, SourceModel.Expr history, SourceModel.Expr before, SourceModel.Expr transformer) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transform), currentState, history, before, transformer});
		}

		/**
		 * Name binding for function: transform.
		 * @see #transform(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transform = 
			QualifiedName.make(
				CAL_Optimizer_Traversers_internal.MODULE_NAME, 
				"transform");

		/**
		 * Traverse the tree in post-order (bottom-up) and apply the given transformation function.
		 * <p>
		 * TODO: Go over this and make sure that the bound names are updated correctly.
		 * 
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 *          Some state information for use by the transformations.
		 * @param acc (CAL type: <code>acc</code>)
		 *          The accumulator for the traversal.
		 * @param expression (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The expression being traversed.
		 * @param transformer (CAL type: <code>Cal.Internal.Optimizer_State.TransformState -> acc -> Cal.Internal.Optimizer_Expression.Expression -> (acc, Cal.Internal.Optimizer_Expression.Expression)</code>)
		 *          The function that transforms a not in the expression.
		 * @return (CAL type: <code>(acc, Cal.Internal.Optimizer_Expression.Expression)</code>) 
		 *          The result of applying the transformer function in post order to the before expression.
		 */
		public static final SourceModel.Expr transformAcc(SourceModel.Expr state, SourceModel.Expr acc, SourceModel.Expr expression, SourceModel.Expr transformer) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformAcc), state, acc, expression, transformer});
		}

		/**
		 * Name binding for function: transformAcc.
		 * @see #transformAcc(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformAcc = 
			QualifiedName.make(
				CAL_Optimizer_Traversers_internal.MODULE_NAME, 
				"transformAcc");

		/**
		 * Helper binding method for function: transformExtensionField. 
		 * @param state
		 * @param history
		 * @param transformer
		 * @param extensionField
		 * @return the SourceModule.expr representing an application of transformExtensionField
		 */
		public static final SourceModel.Expr transformExtensionField(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr transformer, SourceModel.Expr extensionField) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformExtensionField), state, history, transformer, extensionField});
		}

		/**
		 * Name binding for function: transformExtensionField.
		 * @see #transformExtensionField(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformExtensionField = 
			QualifiedName.make(
				CAL_Optimizer_Traversers_internal.MODULE_NAME, 
				"transformExtensionField");

		/**
		 * Apply the given transformer to the given ExtensionField.
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param accumulator (CAL type: <code>acc</code>)
		 * @param transformer (CAL type: <code>Cal.Internal.Optimizer_State.TransformState -> acc -> Cal.Internal.Optimizer_Expression.Expression -> (acc, Cal.Internal.Optimizer_Expression.Expression)</code>)
		 * @param extensionField (CAL type: <code>(Cal.Internal.Optimizer_Expression.FieldName, Cal.Internal.Optimizer_Expression.Expression)</code>)
		 * @return (CAL type: <code>(acc, (Cal.Internal.Optimizer_Expression.FieldName, Cal.Internal.Optimizer_Expression.Expression))</code>) 
		 */
		public static final SourceModel.Expr transformExtensionFieldAcc(SourceModel.Expr state, SourceModel.Expr accumulator, SourceModel.Expr transformer, SourceModel.Expr extensionField) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformExtensionFieldAcc), state, accumulator, transformer, extensionField});
		}

		/**
		 * Name binding for function: transformExtensionFieldAcc.
		 * @see #transformExtensionFieldAcc(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformExtensionFieldAcc = 
			QualifiedName.make(
				CAL_Optimizer_Traversers_internal.MODULE_NAME, 
				"transformExtensionFieldAcc");

		/**
		 * Helper binding method for function: transformExtensionFieldOnly. 
		 * @param transformer
		 * @param extensionField
		 * @return the SourceModule.expr representing an application of transformExtensionFieldOnly
		 */
		public static final SourceModel.Expr transformExtensionFieldOnly(SourceModel.Expr transformer, SourceModel.Expr extensionField) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformExtensionFieldOnly), transformer, extensionField});
		}

		/**
		 * Name binding for function: transformExtensionFieldOnly.
		 * @see #transformExtensionFieldOnly(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformExtensionFieldOnly = 
			QualifiedName.make(
				CAL_Optimizer_Traversers_internal.MODULE_NAME, 
				"transformExtensionFieldOnly");

		/**
		 * Helper binding method for function: transformExtensionFieldTopDown. 
		 * @param state
		 * @param history
		 * @param transformer
		 * @param extensionField
		 * @return the SourceModule.expr representing an application of transformExtensionFieldTopDown
		 */
		public static final SourceModel.Expr transformExtensionFieldTopDown(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr transformer, SourceModel.Expr extensionField) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformExtensionFieldTopDown), state, history, transformer, extensionField});
		}

		/**
		 * Name binding for function: transformExtensionFieldTopDown.
		 * @see #transformExtensionFieldTopDown(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformExtensionFieldTopDown = 
			QualifiedName.make(
				CAL_Optimizer_Traversers_internal.MODULE_NAME, 
				"transformExtensionFieldTopDown");

		/**
		 * Helper binding method for function: transformExtensionFieldTopDownSimple. 
		 * @param transformer
		 * @param extensionField
		 * @return the SourceModule.expr representing an application of transformExtensionFieldTopDownSimple
		 */
		public static final SourceModel.Expr transformExtensionFieldTopDownSimple(SourceModel.Expr transformer, SourceModel.Expr extensionField) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformExtensionFieldTopDownSimple), transformer, extensionField});
		}

		/**
		 * Name binding for function: transformExtensionFieldTopDownSimple.
		 * @see #transformExtensionFieldTopDownSimple(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformExtensionFieldTopDownSimple = 
			QualifiedName.make(
				CAL_Optimizer_Traversers_internal.MODULE_NAME, 
				"transformExtensionFieldTopDownSimple");

		/**
		 * Helper binding method for function: transformExtensionFields. 
		 * @param state
		 * @param history
		 * @param transformer
		 * @param extensionFields
		 * @return the SourceModule.expr representing an application of transformExtensionFields
		 */
		public static final SourceModel.Expr transformExtensionFields(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr transformer, SourceModel.Expr extensionFields) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformExtensionFields), state, history, transformer, extensionFields});
		}

		/**
		 * Name binding for function: transformExtensionFields.
		 * @see #transformExtensionFields(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformExtensionFields = 
			QualifiedName.make(
				CAL_Optimizer_Traversers_internal.MODULE_NAME, 
				"transformExtensionFields");

		/**
		 * Helper binding method for function: transformExtensionFieldsAcc. 
		 * @param state
		 * @param accumulator
		 * @param transformer
		 * @param extensionFields
		 * @return the SourceModule.expr representing an application of transformExtensionFieldsAcc
		 */
		public static final SourceModel.Expr transformExtensionFieldsAcc(SourceModel.Expr state, SourceModel.Expr accumulator, SourceModel.Expr transformer, SourceModel.Expr extensionFields) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformExtensionFieldsAcc), state, accumulator, transformer, extensionFields});
		}

		/**
		 * Name binding for function: transformExtensionFieldsAcc.
		 * @see #transformExtensionFieldsAcc(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformExtensionFieldsAcc = 
			QualifiedName.make(
				CAL_Optimizer_Traversers_internal.MODULE_NAME, 
				"transformExtensionFieldsAcc");

		/**
		 * Helper binding method for function: transformExtensionFieldsOnly. 
		 * @param transformer
		 * @param extensionFields
		 * @return the SourceModule.expr representing an application of transformExtensionFieldsOnly
		 */
		public static final SourceModel.Expr transformExtensionFieldsOnly(SourceModel.Expr transformer, SourceModel.Expr extensionFields) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformExtensionFieldsOnly), transformer, extensionFields});
		}

		/**
		 * Name binding for function: transformExtensionFieldsOnly.
		 * @see #transformExtensionFieldsOnly(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformExtensionFieldsOnly = 
			QualifiedName.make(
				CAL_Optimizer_Traversers_internal.MODULE_NAME, 
				"transformExtensionFieldsOnly");

		/**
		 * Helper binding method for function: transformExtensionFieldsTopDown. 
		 * @param state
		 * @param history
		 * @param transformer
		 * @param extensionFields
		 * @return the SourceModule.expr representing an application of transformExtensionFieldsTopDown
		 */
		public static final SourceModel.Expr transformExtensionFieldsTopDown(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr transformer, SourceModel.Expr extensionFields) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformExtensionFieldsTopDown), state, history, transformer, extensionFields});
		}

		/**
		 * Name binding for function: transformExtensionFieldsTopDown.
		 * @see #transformExtensionFieldsTopDown(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformExtensionFieldsTopDown = 
			QualifiedName.make(
				CAL_Optimizer_Traversers_internal.MODULE_NAME, 
				"transformExtensionFieldsTopDown");

		/**
		 * Helper binding method for function: transformExtensionFieldsTopDownSimple. 
		 * @param transformer
		 * @param extensionFields
		 * @return the SourceModule.expr representing an application of transformExtensionFieldsTopDownSimple
		 */
		public static final SourceModel.Expr transformExtensionFieldsTopDownSimple(SourceModel.Expr transformer, SourceModel.Expr extensionFields) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformExtensionFieldsTopDownSimple), transformer, extensionFields});
		}

		/**
		 * Name binding for function: transformExtensionFieldsTopDownSimple.
		 * @see #transformExtensionFieldsTopDownSimple(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformExtensionFieldsTopDownSimple = 
			QualifiedName.make(
				CAL_Optimizer_Traversers_internal.MODULE_NAME, 
				"transformExtensionFieldsTopDownSimple");

		/**
		 * Helper binding method for function: transformInner. 
		 * @param currentState
		 * @param history
		 * @param before
		 * @param transformer
		 * @return the SourceModule.expr representing an application of transformInner
		 */
		public static final SourceModel.Expr transformInner(SourceModel.Expr currentState, SourceModel.Expr history, SourceModel.Expr before, SourceModel.Expr transformer) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformInner), currentState, history, before, transformer});
		}

		/**
		 * Name binding for function: transformInner.
		 * @see #transformInner(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformInner = 
			QualifiedName.make(
				CAL_Optimizer_Traversers_internal.MODULE_NAME, 
				"transformInner");

		/**
		 * Helper binding method for function: transformNoHistory. 
		 * @param state
		 * @param before
		 * @param transformer
		 * @return the SourceModule.expr representing an application of transformNoHistory
		 */
		public static final SourceModel.Expr transformNoHistory(SourceModel.Expr state, SourceModel.Expr before, SourceModel.Expr transformer) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformNoHistory), state, before, transformer});
		}

		/**
		 * Name binding for function: transformNoHistory.
		 * @see #transformNoHistory(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformNoHistory = 
			QualifiedName.make(
				CAL_Optimizer_Traversers_internal.MODULE_NAME, 
				"transformNoHistory");

		/**
		 * Traverse the tree in post-order (bottom-up) and apply the given transformation function.
		 * @param expression (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The expression being traversed.
		 * @param transformer (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression -> Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The function that transforms a not in the expression.
		 * @return (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>) 
		 *          The result of applying the transformer function in post order to the before expression.
		 */
		public static final SourceModel.Expr transformOnly(SourceModel.Expr expression, SourceModel.Expr transformer) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformOnly), expression, transformer});
		}

		/**
		 * Name binding for function: transformOnly.
		 * @see #transformOnly(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformOnly = 
			QualifiedName.make(
				CAL_Optimizer_Traversers_internal.MODULE_NAME, 
				"transformOnly");

		/**
		 * Helper binding method for function: transformTopDown. 
		 * @param state
		 * @param history0
		 * @param expr0
		 * @param transformer
		 * @return the SourceModule.expr representing an application of transformTopDown
		 */
		public static final SourceModel.Expr transformTopDown(SourceModel.Expr state, SourceModel.Expr history0, SourceModel.Expr expr0, SourceModel.Expr transformer) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformTopDown), state, history0, expr0, transformer});
		}

		/**
		 * Name binding for function: transformTopDown.
		 * @see #transformTopDown(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformTopDown = 
			QualifiedName.make(
				CAL_Optimizer_Traversers_internal.MODULE_NAME, 
				"transformTopDown");

		/**
		 * Helper binding method for function: transformTopDownSimple. 
		 * @param expr0
		 * @param transformer
		 * @return the SourceModule.expr representing an application of transformTopDownSimple
		 */
		public static final SourceModel.Expr transformTopDownSimple(SourceModel.Expr expr0, SourceModel.Expr transformer) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformTopDownSimple), expr0, transformer});
		}

		/**
		 * Name binding for function: transformTopDownSimple.
		 * @see #transformTopDownSimple(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformTopDownSimple = 
			QualifiedName.make(
				CAL_Optimizer_Traversers_internal.MODULE_NAME, 
				"transformTopDownSimple");

		/**
		 * Helper binding method for function: transform_withHistory. 
		 * @param transform
		 * @param state
		 * @param history
		 * @param expression
		 * @return the SourceModule.expr representing an application of transform_withHistory
		 */
		public static final SourceModel.Expr transform_withHistory(SourceModel.Expr transform, SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr expression) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transform_withHistory), transform, state, history, expression});
		}

		/**
		 * Name binding for function: transform_withHistory.
		 * @see #transform_withHistory(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transform_withHistory = 
			QualifiedName.make(
				CAL_Optimizer_Traversers_internal.MODULE_NAME, 
				"transform_withHistory");

		/**
		 * Performs all of the given transformation on the given expression once.
		 * @param transformations (CAL type: <code>[Cal.Internal.Optimizer_State.TransformState -> Cal.Internal.Optimizer_State.TransformHistory -> Cal.Internal.Optimizer_Expression.Expression -> (Cal.Internal.Optimizer_State.TransformHistory, Cal.Core.Prelude.Boolean, Cal.Internal.Optimizer_Expression.Expression)]</code>)
		 *          The set of transformations to perform.
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 *          Keeps track of information about the context of the expression traversal.
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 *          Information about the history of the traversal.
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The expression to perform the transformations on.
		 * @return (CAL type: <code>(Cal.Internal.Optimizer_State.TransformHistory, Cal.Core.Prelude.Boolean, Cal.Internal.Optimizer_Expression.Expression)</code>) 
		 *          The result of apply the given transformation to the given expression.
		 */
		public static final SourceModel.Expr transformer(SourceModel.Expr transformations, SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformer), transformations, state, history, expr});
		}

		/**
		 * Name binding for function: transformer.
		 * @see #transformer(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformer = 
			QualifiedName.make(
				CAL_Optimizer_Traversers_internal.MODULE_NAME, 
				"transformer");

		/**
		 * Traverse the tree in post-order (bottom-up) and apply the given transformation function.
		 * <p>
		 * TODO: Go over this and make sure that the bound names are updated correctly.
		 * 
		 * @param acc (CAL type: <code>acc</code>)
		 *          The accumulator for the traversal.
		 * @param expression (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The expression being traversed.
		 * @param traverser (CAL type: <code>acc -> Cal.Internal.Optimizer_Expression.Expression -> acc</code>)
		 *          The function that transforms a not in the expression.
		 * @return (CAL type: <code>acc</code>) 
		 *          The result of applying the traverser function in post order to the before expression.
		 */
		public static final SourceModel.Expr traverse(SourceModel.Expr acc, SourceModel.Expr expression, SourceModel.Expr traverser) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.traverse), acc, expression, traverser});
		}

		/**
		 * Name binding for function: traverse.
		 * @see #traverse(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName traverse = 
			QualifiedName.make(
				CAL_Optimizer_Traversers_internal.MODULE_NAME, 
				"traverse");

		/**
		 * Helper binding method for function: traverseExtensionFieldAcc. 
		 * @param acc
		 * @param traverser
		 * @param extensionField
		 * @return the SourceModule.expr representing an application of traverseExtensionFieldAcc
		 */
		public static final SourceModel.Expr traverseExtensionFieldAcc(SourceModel.Expr acc, SourceModel.Expr traverser, SourceModel.Expr extensionField) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.traverseExtensionFieldAcc), acc, traverser, extensionField});
		}

		/**
		 * Name binding for function: traverseExtensionFieldAcc.
		 * @see #traverseExtensionFieldAcc(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName traverseExtensionFieldAcc = 
			QualifiedName.make(
				CAL_Optimizer_Traversers_internal.MODULE_NAME, 
				"traverseExtensionFieldAcc");

		/**
		 * Helper function for traverseTopDownAcc.
		 * @param acc (CAL type: <code>acc</code>)
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param extensionField (CAL type: <code>(Cal.Internal.Optimizer_Expression.FieldName, Cal.Internal.Optimizer_Expression.Expression)</code>)
		 * @param traverser (CAL type: <code>acc -> Cal.Internal.Optimizer_State.TransformState -> Cal.Internal.Optimizer_Expression.Expression -> (Cal.Core.Prelude.Boolean, acc)</code>)
		 * @return (CAL type: <code>acc</code>) 
		 */
		public static final SourceModel.Expr traverseExtensionFieldTopDown(SourceModel.Expr acc, SourceModel.Expr state, SourceModel.Expr extensionField, SourceModel.Expr traverser) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.traverseExtensionFieldTopDown), acc, state, extensionField, traverser});
		}

		/**
		 * Name binding for function: traverseExtensionFieldTopDown.
		 * @see #traverseExtensionFieldTopDown(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName traverseExtensionFieldTopDown = 
			QualifiedName.make(
				CAL_Optimizer_Traversers_internal.MODULE_NAME, 
				"traverseExtensionFieldTopDown");

		/**
		 * Helper binding method for function: traverseExtensionFieldsAcc. 
		 * @param acc
		 * @param traverser
		 * @param extensionFields
		 * @return the SourceModule.expr representing an application of traverseExtensionFieldsAcc
		 */
		public static final SourceModel.Expr traverseExtensionFieldsAcc(SourceModel.Expr acc, SourceModel.Expr traverser, SourceModel.Expr extensionFields) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.traverseExtensionFieldsAcc), acc, traverser, extensionFields});
		}

		/**
		 * Name binding for function: traverseExtensionFieldsAcc.
		 * @see #traverseExtensionFieldsAcc(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName traverseExtensionFieldsAcc = 
			QualifiedName.make(
				CAL_Optimizer_Traversers_internal.MODULE_NAME, 
				"traverseExtensionFieldsAcc");

		/**
		 * Helper function for traverseTopDownAcc.
		 * @param acc (CAL type: <code>acc</code>)
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param extensionFields (CAL type: <code>[(Cal.Internal.Optimizer_Expression.FieldName, Cal.Internal.Optimizer_Expression.Expression)]</code>)
		 * @param traverser (CAL type: <code>acc -> Cal.Internal.Optimizer_State.TransformState -> Cal.Internal.Optimizer_Expression.Expression -> (Cal.Core.Prelude.Boolean, acc)</code>)
		 * @return (CAL type: <code>acc</code>) 
		 */
		public static final SourceModel.Expr traverseExtensionFieldsTopDown(SourceModel.Expr acc, SourceModel.Expr state, SourceModel.Expr extensionFields, SourceModel.Expr traverser) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.traverseExtensionFieldsTopDown), acc, state, extensionFields, traverser});
		}

		/**
		 * Name binding for function: traverseExtensionFieldsTopDown.
		 * @see #traverseExtensionFieldsTopDown(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName traverseExtensionFieldsTopDown = 
			QualifiedName.make(
				CAL_Optimizer_Traversers_internal.MODULE_NAME, 
				"traverseExtensionFieldsTopDown");

		/**
		 * Traverse the tree in post-order (top down) and apply the given transformation function.
		 * @param acc (CAL type: <code>acc</code>)
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param expression (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @param traverser (CAL type: <code>acc -> Cal.Internal.Optimizer_State.TransformState -> Cal.Internal.Optimizer_Expression.Expression -> (Cal.Core.Prelude.Boolean, acc)</code>)
		 * @return (CAL type: <code>acc</code>) 
		 */
		public static final SourceModel.Expr traverseTopDownAcc(SourceModel.Expr acc, SourceModel.Expr state, SourceModel.Expr expression, SourceModel.Expr traverser) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.traverseTopDownAcc), acc, state, expression, traverser});
		}

		/**
		 * Name binding for function: traverseTopDownAcc.
		 * @see #traverseTopDownAcc(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName traverseTopDownAcc = 
			QualifiedName.make(
				CAL_Optimizer_Traversers_internal.MODULE_NAME, 
				"traverseTopDownAcc");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = 1902146735;

}
