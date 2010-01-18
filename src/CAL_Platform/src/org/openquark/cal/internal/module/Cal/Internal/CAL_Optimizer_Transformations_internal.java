/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_Optimizer_Transformations_internal.java)
 * was generated from CAL module: Cal.Internal.Optimizer_Transformations.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Internal.Optimizer_Transformations module from Java code.
 *  
 * Creation date: Thu Oct 18 09:02:10 PDT 2007
 * --!>
 *  
 */

package org.openquark.cal.internal.module.Cal.Internal;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * WARNING WARNING WARNING WARNING WARNING WARNING WARNING WARNING WARNING
 * WARNING WARNING WARNING WARNING WAR
 * <p>
 * This file is part of the compiler and not to be modified.
 * <p>
 * ING WARNING WARNING WARNING WARNING WARNING WARNING WARNING WARNING WARNING
 * WARNING WARNING WARNING WARNING
 * <p>
 * This module contains all functions that perform optimizing transformations of
 * expressions. Some of the transformations currently defined are
 * <p>
 * performFusion
 * <p>
 * This transformation can decide to create a new function that fuses two other
 * functions and in the process eliminates some data constructors.
 * <p>
 * transform_specialization
 * <p>
 * This transformation can decide to create a new specialized version of a
 * function with more specific types and possible eliminate arguments by
 * embeddeding given values.
 * <p>
 * transform_3_2_2
 * <p>
 * This transformation performs inlining of values.
 * <p>
 * There are a number of other transformations defined in the file, see below.
 * <p>
 * TODO rename all the tranformation so that they have some naming convention
 * that is consistent and so they can be easily found.
 * 
 * @author Greg McClement
 */
public final class CAL_Optimizer_Transformations_internal {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Internal.Optimizer_Transformations");

	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Internal.Optimizer_Transformations module.
	 */
	public static final class Functions {
		/**
		 * Updates the cached type of the let expression with a calculated type.
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 * @param expression (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>(Cal.Internal.Optimizer_State.TransformHistory, Cal.Core.Prelude.Boolean, Cal.Internal.Optimizer_Expression.Expression)</code>) 
		 */
		public static final SourceModel.Expr adjustTypeForLetExpression(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr expression) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.adjustTypeForLetExpression), state, history, expression});
		}

		/**
		 * Name binding for function: adjustTypeForLetExpression.
		 * @see #adjustTypeForLetExpression(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName adjustTypeForLetExpression = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"adjustTypeForLetExpression");

		/**
		 * Checks if the expression of this Alt has an known kind of value.
		 * @param alt (CAL type: <code>Cal.Internal.Optimizer_Expression.Alt</code>)
		 *          The Alt to check.
		 * @param recursiveCallChecker (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression -> Cal.Core.Prelude.Boolean</code>)
		 *          If the value of the inner case is a recursive call to the same function then it can be left unchanged in the body and the embedding will succeed. This is to handle the optimization of Prelude.isNothing (List.find (\x -&gt; x == 1) [1::Int]);
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          Returns true iff the alt returns a known kind of value.
		 */
		public static final SourceModel.Expr altHasExplicitDC(SourceModel.Expr alt, SourceModel.Expr recursiveCallChecker) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.altHasExplicitDC), alt, recursiveCallChecker});
		}

		/**
		 * Name binding for function: altHasExplicitDC.
		 * @see #altHasExplicitDC(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName altHasExplicitDC = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"altHasExplicitDC");

		/**
		 * Checks if the atls all return values of a known kind.
		 * @param alts (CAL type: <code>[Cal.Internal.Optimizer_Expression.Alt]</code>)
		 *          The alternatives to check.
		 * @param recursiveCallChecker (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression -> Cal.Core.Prelude.Boolean</code>)
		 *          If the value of the inner case is a recursive call to the same function then it can be left unchanged in the body and the embedding will succeed. This is to handle the optimization of Prelude.isNothing (List.find (\x -&gt; x == 1) [1::Int]);
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          Returns true iff the alls all return a known kind of value.
		 */
		public static final SourceModel.Expr altsAllHaveExplicitDC(SourceModel.Expr alts, SourceModel.Expr recursiveCallChecker) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.altsAllHaveExplicitDC), alts, recursiveCallChecker});
		}

		/**
		 * Name binding for function: altsAllHaveExplicitDC.
		 * @see #altsAllHaveExplicitDC(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName altsAllHaveExplicitDC = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"altsAllHaveExplicitDC");

		/**
		 * Take the given list of seq'ed expression and the given inner expression and build an expression of 
		 * the form (seq e1 (seq e2 (... )))
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param seqedExprs (CAL type: <code>[Cal.Internal.Optimizer_Expression.Expression]</code>)
		 * @param functor (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @param args (CAL type: <code>[Cal.Internal.Optimizer_Expression.Expression]</code>)
		 * @return (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>) 
		 */
		public static final SourceModel.Expr buildSeq(SourceModel.Expr state, SourceModel.Expr seqedExprs, SourceModel.Expr functor, SourceModel.Expr args) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.buildSeq), state, seqedExprs, functor, args});
		}

		/**
		 * Name binding for function: buildSeq.
		 * @see #buildSeq(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName buildSeq = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"buildSeq");

		/**
		 * Helper function to determine if selectStrictArguments will work when
		 * calling selectStrictArguments.
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 * @param dc (CAL type: <code>Cal.Internal.Optimizer_Expression.DataCons</code>)
		 * @param args (CAL type: <code>[Cal.Internal.Optimizer_Expression.Expression]</code>)
		 * @param counter (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @param answer (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr canSelectStrictArguments(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr dc, SourceModel.Expr args, SourceModel.Expr counter, SourceModel.Expr answer) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.canSelectStrictArguments), state, history, dc, args, counter, answer});
		}

		/**
		 * @see #canSelectStrictArguments(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param state
		 * @param history
		 * @param dc
		 * @param args
		 * @param counter
		 * @param answer
		 * @return the SourceModel.Expr representing an application of canSelectStrictArguments
		 */
		public static final SourceModel.Expr canSelectStrictArguments(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr dc, SourceModel.Expr args, int counter, SourceModel.Expr answer) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.canSelectStrictArguments), state, history, dc, args, SourceModel.Expr.makeIntValue(counter), answer});
		}

		/**
		 * Name binding for function: canSelectStrictArguments.
		 * @see #canSelectStrictArguments(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName canSelectStrictArguments = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"canSelectStrictArguments");

		/**
		 * For the function f a1 a2 a3 ... ak look in the body for recursive calls. The
		 * recursive called will be of the form f a1 a2' ... ai' ... ak . The non-primed
		 * arguments are constant throughout the call. This means that the values can be
		 * bound and the arguments removed the caller.
		 * <p>
		 * The constVars is a list with the same arity as f.
		 * 
		 * @param functionName (CAL type: <code>Cal.Internal.Optimizer_Expression.QualifiedName</code>)
		 * @param typeConstants (CAL type: <code>Cal.Internal.Optimizer_State.JPreludeTypeConstants</code>)
		 * @param functionArguments (CAL type: <code>[Cal.Internal.Optimizer_Expression.QualifiedName]</code>)
		 * @param functionBody (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>[Cal.Core.Prelude.Boolean]</code>) 
		 */
		public static final SourceModel.Expr constVars(SourceModel.Expr functionName, SourceModel.Expr typeConstants, SourceModel.Expr functionArguments, SourceModel.Expr functionBody) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.constVars), functionName, typeConstants, functionArguments, functionBody});
		}

		/**
		 * Name binding for function: constVars.
		 * @see #constVars(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName constVars = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"constVars");

		/**
		 * For any positional pattern make sure that all of the variables are defined. This is to 
		 * make pattern matching more successful in the transformation stage.
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 * @param alt (CAL type: <code>Cal.Internal.Optimizer_Expression.Alt</code>)
		 * @return (CAL type: <code>(Cal.Internal.Optimizer_State.TransformHistory, Cal.Core.Prelude.Boolean, Cal.Internal.Optimizer_Expression.Alt)</code>) 
		 */
		public static final SourceModel.Expr defineAllAltVariables(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr alt) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.defineAllAltVariables), state, history, alt});
		}

		/**
		 * Name binding for function: defineAllAltVariables.
		 * @see #defineAllAltVariables(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName defineAllAltVariables = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"defineAllAltVariables");

		/**
		 * Looks for expression of the form "&lt;DataConstructor&gt; arg1 arg2 ... argk",
		 * &lt;Literal&gt; or a switch where all the alternative have the former two patterns.
		 * @param expression (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The expression to check.
		 * @param recursiveCallChecker (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression -> Cal.Core.Prelude.Boolean</code>)
		 *          If the value of the inner case is a recursive call to the same function then it can be left unchanged in the body and the embedding will succeed. This is to handle the optimization of Prelude.isNothing (List.find (\x -&gt; x == 1) [1::Int]);
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          Returns true iff the expression returns a known kind of value.
		 */
		public static final SourceModel.Expr expressionIsExplicitDC(SourceModel.Expr expression, SourceModel.Expr recursiveCallChecker) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expressionIsExplicitDC), expression, recursiveCallChecker});
		}

		/**
		 * Name binding for function: expressionIsExplicitDC.
		 * @see #expressionIsExplicitDC(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expressionIsExplicitDC = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"expressionIsExplicitDC");

		/**
		 * Helper binding method for function: expressionMatchesBoolean. 
		 * @param expectedValue
		 * @param expression
		 * @return the SourceModule.expr representing an application of expressionMatchesBoolean
		 */
		public static final SourceModel.Expr expressionMatchesBoolean(SourceModel.Expr expectedValue, SourceModel.Expr expression) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expressionMatchesBoolean), expectedValue, expression});
		}

		/**
		 * @see #expressionMatchesBoolean(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param expectedValue
		 * @param expression
		 * @return the SourceModel.Expr representing an application of expressionMatchesBoolean
		 */
		public static final SourceModel.Expr expressionMatchesBoolean(boolean expectedValue, SourceModel.Expr expression) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expressionMatchesBoolean), SourceModel.Expr.makeBooleanValue(expectedValue), expression});
		}

		/**
		 * Name binding for function: expressionMatchesBoolean.
		 * @see #expressionMatchesBoolean(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expressionMatchesBoolean = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"expressionMatchesBoolean");

		/**
		 * Returns true if the expression contains a let/letinlinable expression. TODO
		 * is this necessary? Bugs arose compiling Format.cal
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr expression_containsLet(SourceModel.Expr state, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_containsLet), state, expr});
		}

		/**
		 * Name binding for function: expression_containsLet.
		 * @see #expression_containsLet(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expression_containsLet = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"expression_containsLet");

		/**
		 * Returns true if the expression contains a switch expression. TODO is this
		 * necessary? Bugs arose compiling Format.cal
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr expression_containsSwitch(SourceModel.Expr state, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_containsSwitch), state, expr});
		}

		/**
		 * Name binding for function: expression_containsSwitch.
		 * @see #expression_containsSwitch(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expression_containsSwitch = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"expression_containsSwitch");

		/**
		 * Search for an alternative in alts with the same functor as alt
		 * @param alti (CAL type: <code>Cal.Internal.Optimizer_Expression.Alt</code>)
		 *          The alternative to match.
		 * @param altso (CAL type: <code>[Cal.Internal.Optimizer_Expression.Alt]</code>)
		 *          The list of alternatives to search for a match.
		 * @return (CAL type: <code>Cal.Internal.Optimizer_Expression.Alt</code>) 
		 *          An alternative in altso that has the same functor as alti.
		 */
		public static final SourceModel.Expr findMatchingAlt(SourceModel.Expr alti, SourceModel.Expr altso) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.findMatchingAlt), alti, altso});
		}

		/**
		 * Name binding for function: findMatchingAlt.
		 * @see #findMatchingAlt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName findMatchingAlt = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"findMatchingAlt");

		/**
		 * TODO Remove this function.
		 * @param string (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @param contains (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr findString(SourceModel.Expr string, SourceModel.Expr contains) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.findString), string, contains});
		}

		/**
		 * @see #findString(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param string
		 * @param contains
		 * @return the SourceModel.Expr representing an application of findString
		 */
		public static final SourceModel.Expr findString(java.lang.String string, java.lang.String contains) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.findString), SourceModel.Expr.makeStringValue(string), SourceModel.Expr.makeStringValue(contains)});
		}

		/**
		 * Name binding for function: findString.
		 * @see #findString(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName findString = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"findString");

		/**
		 * Helper binding method for function: flattenSeqs. 
		 * @param expr
		 * @return the SourceModule.expr representing an application of flattenSeqs
		 */
		public static final SourceModel.Expr flattenSeqs(SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.flattenSeqs), expr});
		}

		/**
		 * Name binding for function: flattenSeqs.
		 * @see #flattenSeqs(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName flattenSeqs = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"flattenSeqs");

		/**
		 * Convert straight list of variables to a fieldNameToVarList.
		 * @param qns (CAL type: <code>[Cal.Core.Prelude.Maybe Cal.Internal.Optimizer_Expression.QualifiedName]</code>)
		 * @return (CAL type: <code>[(Cal.Internal.Optimizer_Expression.FieldName, Cal.Internal.Optimizer_Expression.QualifiedName)]</code>) 
		 */
		public static final SourceModel.Expr fromVars(SourceModel.Expr qns) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromVars), qns});
		}

		/**
		 * Name binding for function: fromVars.
		 * @see #fromVars(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fromVars = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"fromVars");

		/**
		 * Helper binding method for function: fromVarsHelper. 
		 * @param qns
		 * @param counter
		 * @return the SourceModule.expr representing an application of fromVarsHelper
		 */
		public static final SourceModel.Expr fromVarsHelper(SourceModel.Expr qns, SourceModel.Expr counter) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromVarsHelper), qns, counter});
		}

		/**
		 * @see #fromVarsHelper(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param qns
		 * @param counter
		 * @return the SourceModel.Expr representing an application of fromVarsHelper
		 */
		public static final SourceModel.Expr fromVarsHelper(SourceModel.Expr qns, int counter) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromVarsHelper), qns, SourceModel.Expr.makeIntValue(counter)});
		}

		/**
		 * Name binding for function: fromVarsHelper.
		 * @see #fromVarsHelper(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fromVarsHelper = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"fromVarsHelper");

		/**
		 * Helper binding method for function: functorMatches. 
		 * @param expr
		 * @param functorName
		 * @return the SourceModule.expr representing an application of functorMatches
		 */
		public static final SourceModel.Expr functorMatches(SourceModel.Expr expr, SourceModel.Expr functorName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.functorMatches), expr, functorName});
		}

		/**
		 * Name binding for function: functorMatches.
		 * @see #functorMatches(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName functorMatches = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"functorMatches");

		/**
		 * Try to figure out what data value corresponds to this value. Either the value is
		 * a data constructor or a literal or the type can be inferred from the context.
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @param functor (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @param args (CAL type: <code>[Cal.Internal.Optimizer_Expression.Expression]</code>)
		 * @return (CAL type: <code>(Cal.Internal.Optimizer_Expression.Expression, [Cal.Internal.Optimizer_Expression.Expression], [(Cal.Internal.Optimizer_Expression.FieldName, Cal.Internal.Optimizer_Expression.QualifiedName, Cal.Internal.Optimizer_Type.Type)], Cal.Core.Prelude.Boolean)</code>) 
		 */
		public static final SourceModel.Expr getForm(SourceModel.Expr state, SourceModel.Expr expr, SourceModel.Expr functor, SourceModel.Expr args) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getForm), state, expr, functor, args});
		}

		/**
		 * Name binding for function: getForm.
		 * @see #getForm(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getForm = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"getForm");

		/**
		 * If onlyConstArgs is set then the specialization will just occur at this point.
		 * If not onlyConstArgs then we will try specializing and then optimize the result
		 * to see if the recurive calls are eliminated if so then we will keep this try. See 
		 * note one for the top level function.
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 * @param onlyUseConstantArgs (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 * @param newFunctionName (CAL type: <code>Cal.Internal.Optimizer_Expression.QualifiedName</code>)
		 * @param letExprPrime (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Internal.Optimizer_Expression.Expression</code>) 
		 */
		public static final SourceModel.Expr getLetExpr(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr onlyUseConstantArgs, SourceModel.Expr newFunctionName, SourceModel.Expr letExprPrime) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getLetExpr), state, history, onlyUseConstantArgs, newFunctionName, letExprPrime});
		}

		/**
		 * @see #getLetExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param state
		 * @param history
		 * @param onlyUseConstantArgs
		 * @param newFunctionName
		 * @param letExprPrime
		 * @return the SourceModel.Expr representing an application of getLetExpr
		 */
		public static final SourceModel.Expr getLetExpr(SourceModel.Expr state, SourceModel.Expr history, boolean onlyUseConstantArgs, SourceModel.Expr newFunctionName, SourceModel.Expr letExprPrime) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getLetExpr), state, history, SourceModel.Expr.makeBooleanValue(onlyUseConstantArgs), newFunctionName, letExprPrime});
		}

		/**
		 * Name binding for function: getLetExpr.
		 * @see #getLetExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getLetExpr = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"getLetExpr");

		/**
		 * Convert the fieldNameToVar list to a straight list of variables.
		 * @param fnqns (CAL type: <code>[(Cal.Internal.Optimizer_Expression.FieldName, Cal.Internal.Optimizer_Expression.QualifiedName)]</code>)
		 * @return (CAL type: <code>[Cal.Core.Prelude.Maybe Cal.Internal.Optimizer_Expression.QualifiedName]</code>) 
		 */
		public static final SourceModel.Expr getVars(SourceModel.Expr fnqns) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getVars), fnqns});
		}

		/**
		 * Name binding for function: getVars.
		 * @see #getVars(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getVars = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"getVars");

		/**
		 * Helper binding method for function: getVarsHelper. 
		 * @param fnqns
		 * @param counter
		 * @return the SourceModule.expr representing an application of getVarsHelper
		 */
		public static final SourceModel.Expr getVarsHelper(SourceModel.Expr fnqns, SourceModel.Expr counter) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getVarsHelper), fnqns, counter});
		}

		/**
		 * @see #getVarsHelper(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param fnqns
		 * @param counter
		 * @return the SourceModel.Expr representing an application of getVarsHelper
		 */
		public static final SourceModel.Expr getVarsHelper(SourceModel.Expr fnqns, int counter) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getVarsHelper), fnqns, SourceModel.Expr.makeIntValue(counter)});
		}

		/**
		 * Name binding for function: getVarsHelper.
		 * @see #getVarsHelper(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getVarsHelper = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"getVarsHelper");

		/**
		 * Given a list of arguments passed to this call, inline the arguments that have
		 * a constant value.
		 * <p>
		 * TODO: Make this notice if the inlining is bad in that it can calculate values
		 * multiple times.
		 * 
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 *          The context of the transformation.
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 *          The history of the current transformation.
		 * @param wasChanged (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 *          An accumulator for keeping track of whether or not the expression was changed.
		 * @param isConstArgs (CAL type: <code>[Cal.Core.Prelude.Boolean]</code>)
		 *          A list with one entry for each argument. If the argument is
		 * constant in the recursive call then the corresponding element is True.
		 * @param actualArguments (CAL type: <code>[Cal.Internal.Optimizer_Expression.Expression]</code>)
		 *          The arguments used by the caller to an instance of this
		 * function.
		 * @param actualTypes (CAL type: <code>[Cal.Internal.Optimizer_Type.Type]</code>)
		 *          The types of the actual arguments.
		 * @param body (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The body of the function that is being specialized.
		 * @param unusedArgumentsR (CAL type: <code>[Cal.Internal.Optimizer_Expression.Expression]</code>)
		 *          The arguments that are not used (in reverse order).
		 * @param unusedTypesR (CAL type: <code>[Cal.Internal.Optimizer_Type.Type]</code>)
		 *          The corresponding type of the arguments unusedArgumentsR.
		 * @param unusedIsConstArgsR (CAL type: <code>[Cal.Core.Prelude.Boolean]</code>)
		 * @return (CAL type: <code>(Cal.Internal.Optimizer_State.TransformHistory, Cal.Core.Prelude.Boolean, ([Cal.Internal.Optimizer_Expression.Expression], Cal.Internal.Optimizer_Expression.Expression, [Cal.Internal.Optimizer_Type.Type], [Cal.Core.Prelude.Boolean]))</code>) 
		 */
		public static final SourceModel.Expr inlineConstantArguments(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr wasChanged, SourceModel.Expr isConstArgs, SourceModel.Expr actualArguments, SourceModel.Expr actualTypes, SourceModel.Expr body, SourceModel.Expr unusedArgumentsR, SourceModel.Expr unusedTypesR, SourceModel.Expr unusedIsConstArgsR) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inlineConstantArguments), state, history, wasChanged, isConstArgs, actualArguments, actualTypes, body, unusedArgumentsR, unusedTypesR, unusedIsConstArgsR});
		}

		/**
		 * @see #inlineConstantArguments(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param state
		 * @param history
		 * @param wasChanged
		 * @param isConstArgs
		 * @param actualArguments
		 * @param actualTypes
		 * @param body
		 * @param unusedArgumentsR
		 * @param unusedTypesR
		 * @param unusedIsConstArgsR
		 * @return the SourceModel.Expr representing an application of inlineConstantArguments
		 */
		public static final SourceModel.Expr inlineConstantArguments(SourceModel.Expr state, SourceModel.Expr history, boolean wasChanged, SourceModel.Expr isConstArgs, SourceModel.Expr actualArguments, SourceModel.Expr actualTypes, SourceModel.Expr body, SourceModel.Expr unusedArgumentsR, SourceModel.Expr unusedTypesR, SourceModel.Expr unusedIsConstArgsR) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inlineConstantArguments), state, history, SourceModel.Expr.makeBooleanValue(wasChanged), isConstArgs, actualArguments, actualTypes, body, unusedArgumentsR, unusedTypesR, unusedIsConstArgsR});
		}

		/**
		 * Name binding for function: inlineConstantArguments.
		 * @see #inlineConstantArguments(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName inlineConstantArguments = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"inlineConstantArguments");

		/**
		 * Inline the given lambda expression iff it is satured and the expression body
		 * is at least as strict as the function being inlined in the arguments of the
		 * function being inlined.
		 * @param functor (CAL type: <code>Cal.Internal.Optimizer_Expression.QualifiedName</code>)
		 *          The name of the variable corresponding to the lambda expression.
		 * @param expectedArity (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          The arguments of the expression corresponding to the let
		 * var.
		 * @param hasStrictArguments (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 *          Does the lamba expression have strict arguments. If
		 * so the inlining can only happen if the body is at least as strict for
		 * the arguments.
		 * @param forceInlining (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 *          Ignore heuristics and inline that lambda expression. This
		 * is used for fusion currently.
		 * @param inlinedExpr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The expression to inline.
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 *          The context of the current expression.
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 *          The history of the current transformation.
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The expression to perform the inlining in.
		 * @return (CAL type: <code>(Cal.Internal.Optimizer_State.TransformHistory, Cal.Core.Prelude.Boolean, Cal.Internal.Optimizer_Expression.Expression)</code>) 
		 *          The expression with the inlining performed.
		 * <p>
		 * TODO Should this be renaming variables?
		 */
		public static final SourceModel.Expr inlineLambda(SourceModel.Expr functor, SourceModel.Expr expectedArity, SourceModel.Expr hasStrictArguments, SourceModel.Expr forceInlining, SourceModel.Expr inlinedExpr, SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inlineLambda), functor, expectedArity, hasStrictArguments, forceInlining, inlinedExpr, state, history, expr});
		}

		/**
		 * @see #inlineLambda(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param functor
		 * @param expectedArity
		 * @param hasStrictArguments
		 * @param forceInlining
		 * @param inlinedExpr
		 * @param state
		 * @param history
		 * @param expr
		 * @return the SourceModel.Expr representing an application of inlineLambda
		 */
		public static final SourceModel.Expr inlineLambda(SourceModel.Expr functor, int expectedArity, boolean hasStrictArguments, boolean forceInlining, SourceModel.Expr inlinedExpr, SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inlineLambda), functor, SourceModel.Expr.makeIntValue(expectedArity), SourceModel.Expr.makeBooleanValue(hasStrictArguments), SourceModel.Expr.makeBooleanValue(forceInlining), inlinedExpr, state, history, expr});
		}

		/**
		 * Name binding for function: inlineLambda.
		 * @see #inlineLambda(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName inlineLambda = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"inlineLambda");

		/**
		 * A lambda expression can be safely inlined if for each lambda variable either that variable
		 * only occurs once in the lambda expression or the value(s) bound to that variable require 
		 * no further computation.
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param functor (CAL type: <code>Cal.Internal.Optimizer_Expression.QualifiedName</code>)
		 * @param arity (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @param lambdaExpr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @param inlineIntoExpr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr inliningSafeForLambda(SourceModel.Expr state, SourceModel.Expr functor, SourceModel.Expr arity, SourceModel.Expr lambdaExpr, SourceModel.Expr inlineIntoExpr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inliningSafeForLambda), state, functor, arity, lambdaExpr, inlineIntoExpr});
		}

		/**
		 * @see #inliningSafeForLambda(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param state
		 * @param functor
		 * @param arity
		 * @param lambdaExpr
		 * @param inlineIntoExpr
		 * @return the SourceModel.Expr representing an application of inliningSafeForLambda
		 */
		public static final SourceModel.Expr inliningSafeForLambda(SourceModel.Expr state, SourceModel.Expr functor, int arity, SourceModel.Expr lambdaExpr, SourceModel.Expr inlineIntoExpr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inliningSafeForLambda), state, functor, SourceModel.Expr.makeIntValue(arity), lambdaExpr, inlineIntoExpr});
		}

		/**
		 * Name binding for function: inliningSafeForLambda.
		 * @see #inliningSafeForLambda(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName inliningSafeForLambda = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"inliningSafeForLambda");

		/**
		 * Helper binding method for function: isErrorCall. 
		 * @param expr
		 * @return the SourceModule.expr representing an application of isErrorCall
		 */
		public static final SourceModel.Expr isErrorCall(SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isErrorCall), expr});
		}

		/**
		 * Name binding for function: isErrorCall.
		 * @see #isErrorCall(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isErrorCall = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"isErrorCall");

		/**
		 * Checks if the case constant (from the outer alternative) matches the return
		 * expression from the inner alternative.
		 * <p>
		 * Example True == isMatching (Cons a b) (CaseDataCons "Cons")
		 * 
		 * @param expri (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The inner expression to match
		 * @param cco (CAL type: <code>Cal.Internal.Optimizer_Expression.CaseConst</code>)
		 *          The case constant from the outer expression to match.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          True if the out case constant matches the inner return expression.
		 */
		public static final SourceModel.Expr isMatching(SourceModel.Expr expri, SourceModel.Expr cco) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isMatching), expri, cco});
		}

		/**
		 * Name binding for function: isMatching.
		 * @see #isMatching(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isMatching = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"isMatching");

		/**
		 * Helper binding method for function: isMatchingAlt. 
		 * @param alti
		 * @param alto
		 * @return the SourceModule.expr representing an application of isMatchingAlt
		 */
		public static final SourceModel.Expr isMatchingAlt(SourceModel.Expr alti, SourceModel.Expr alto) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isMatchingAlt), alti, alto});
		}

		/**
		 * Name binding for function: isMatchingAlt.
		 * @see #isMatchingAlt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isMatchingAlt = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"isMatchingAlt");

		/**
		 * Determines if expr is recursive.
		 * @param self (CAL type: <code>Cal.Internal.Optimizer_Expression.QualifiedName</code>)
		 *          The name of the variable that is defined as expr
		 * @param expression (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The functions that defines self.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          True iff expr calls self
		 */
		public static final SourceModel.Expr isRecursive(SourceModel.Expr self, SourceModel.Expr expression) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isRecursive), self, expression});
		}

		/**
		 * Name binding for function: isRecursive.
		 * @see #isRecursive(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isRecursive = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"isRecursive");

		/**
		 * Will the expression be evaluated first when the given expression is evaluated.
		 * @param isThisStrict (CAL type: <code>Cal.Internal.Optimizer_Expression.QualifiedName</code>)
		 * @param inThisExpression (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr isStrictInExpression(SourceModel.Expr isThisStrict, SourceModel.Expr inThisExpression) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isStrictInExpression), isThisStrict, inThisExpression});
		}

		/**
		 * Name binding for function: isStrictInExpression.
		 * @see #isStrictInExpression(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isStrictInExpression = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"isStrictInExpression");

		/**
		 * Build a Prelude.seq call with the given arguments.
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 * @param a (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @param b (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>) 
		 */
		public static final SourceModel.Expr makeSeqCall(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr a, SourceModel.Expr b) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeSeqCall), state, history, a, b});
		}

		/**
		 * Name binding for function: makeSeqCall.
		 * @see #makeSeqCall(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeSeqCall = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"makeSeqCall");

		/**
		 * Helper binding method for function: matchCaseConstAndDataConstructor. 
		 * @param cc
		 * @param dc
		 * @return the SourceModule.expr representing an application of matchCaseConstAndDataConstructor
		 */
		public static final SourceModel.Expr matchCaseConstAndDataConstructor(SourceModel.Expr cc, SourceModel.Expr dc) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.matchCaseConstAndDataConstructor), cc, dc});
		}

		/**
		 * Name binding for function: matchCaseConstAndDataConstructor.
		 * @see #matchCaseConstAndDataConstructor(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName matchCaseConstAndDataConstructor = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"matchCaseConstAndDataConstructor");

		/**
		 * Helper binding method for function: matchCaseConstAndLiteral. 
		 * @param cc
		 * @param literal
		 * @return the SourceModule.expr representing an application of matchCaseConstAndLiteral
		 */
		public static final SourceModel.Expr matchCaseConstAndLiteral(SourceModel.Expr cc, SourceModel.Expr literal) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.matchCaseConstAndLiteral), cc, literal});
		}

		/**
		 * Name binding for function: matchCaseConstAndLiteral.
		 * @see #matchCaseConstAndLiteral(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName matchCaseConstAndLiteral = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"matchCaseConstAndLiteral");

		/**
		 * Determine if the given pair of field names refer to the same field.
		 * @param dc (CAL type: <code>Cal.Internal.Optimizer_Expression.DataCons</code>)
		 * @param fn1 (CAL type: <code>Cal.Internal.Optimizer_Expression.FieldName</code>)
		 * @param fn2 (CAL type: <code>Cal.Internal.Optimizer_Expression.FieldName</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr matchFN(SourceModel.Expr dc, SourceModel.Expr fn1, SourceModel.Expr fn2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.matchFN), dc, fn1, fn2});
		}

		/**
		 * Name binding for function: matchFN.
		 * @see #matchFN(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName matchFN = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"matchFN");

		/**
		 * Check if the given alt matches the given value. This only works for Boolean values.
		 * @param expectedValue (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 *          The value to look for
		 * @param alt (CAL type: <code>Cal.Internal.Optimizer_Expression.Alt</code>)
		 *          The alt to check
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          True iff the given alt matches the given boolean value.
		 */
		public static final SourceModel.Expr matchesBoolean(SourceModel.Expr expectedValue, SourceModel.Expr alt) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.matchesBoolean), expectedValue, alt});
		}

		/**
		 * @see #matchesBoolean(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param expectedValue
		 * @param alt
		 * @return the SourceModel.Expr representing an application of matchesBoolean
		 */
		public static final SourceModel.Expr matchesBoolean(boolean expectedValue, SourceModel.Expr alt) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.matchesBoolean), SourceModel.Expr.makeBooleanValue(expectedValue), alt});
		}

		/**
		 * Name binding for function: matchesBoolean.
		 * @see #matchesBoolean(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName matchesBoolean = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"matchesBoolean");

		/**
		 * Helper binding method for function: matchesCaseConst. 
		 * @param expectedValue
		 * @param caseConst
		 * @return the SourceModule.expr representing an application of matchesCaseConst
		 */
		public static final SourceModel.Expr matchesCaseConst(SourceModel.Expr expectedValue, SourceModel.Expr caseConst) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.matchesCaseConst), expectedValue, caseConst});
		}

		/**
		 * @see #matchesCaseConst(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param expectedValue
		 * @param caseConst
		 * @return the SourceModel.Expr representing an application of matchesCaseConst
		 */
		public static final SourceModel.Expr matchesCaseConst(boolean expectedValue, SourceModel.Expr caseConst) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.matchesCaseConst), SourceModel.Expr.makeBooleanValue(expectedValue), caseConst});
		}

		/**
		 * Name binding for function: matchesCaseConst.
		 * @see #matchesCaseConst(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName matchesCaseConst = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"matchesCaseConst");

		/**
		 * Helper binding method for function: onlyRecursive_fusion. 
		 * @return the SourceModule.expr representing an application of onlyRecursive_fusion
		 */
		public static final SourceModel.Expr onlyRecursive_fusion() {
			return SourceModel.Expr.Var.make(Functions.onlyRecursive_fusion);
		}

		/**
		 * Name binding for function: onlyRecursive_fusion.
		 * @see #onlyRecursive_fusion()
		 */
		public static final QualifiedName onlyRecursive_fusion = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"onlyRecursive_fusion");

		/**
		 * Helper binding method for function: onlyRecursive_specialization. 
		 * @return the SourceModule.expr representing an application of onlyRecursive_specialization
		 */
		public static final SourceModel.Expr onlyRecursive_specialization() {
			return 
				SourceModel.Expr.Var.make(
					Functions.onlyRecursive_specialization);
		}

		/**
		 * Name binding for function: onlyRecursive_specialization.
		 * @see #onlyRecursive_specialization()
		 */
		public static final QualifiedName onlyRecursive_specialization = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"onlyRecursive_specialization");

		/**
		 * Performs a bottom up tranversal of the expression. The given set of
		 * transformations are performed on the sub-expressions. The expression is
		 * traverse nIterations number of times.
		 * @param transformations (CAL type: <code>[Cal.Internal.Optimizer_State.TransformState -> Cal.Internal.Optimizer_State.TransformHistory -> Cal.Internal.Optimizer_Expression.Expression -> (Cal.Internal.Optimizer_State.TransformHistory, Cal.Core.Prelude.Boolean, Cal.Internal.Optimizer_Expression.Expression)]</code>)
		 *          The set of transformations to perform on the
		 * sub-expressions.
		 * @param transformState (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 *          Information about the current context of the expression
		 * traversal.
		 * @param transformHistory (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 *          Information about the history of the current
		 * transformation.
		 * @param nIterations (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          The number of times to traverse the expression performing
		 * optimization. There could be fancier control but currently that seems no
		 * necessary.
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The expression to transform.
		 * @return (CAL type: <code>(Cal.Internal.Optimizer_State.TransformHistory, Cal.Core.Prelude.Boolean, Cal.Internal.Optimizer_Expression.Expression)</code>) 
		 *          The result of performing the given transformations on the input
		 * expression.
		 */
		public static final SourceModel.Expr optimizeExplicit(SourceModel.Expr transformations, SourceModel.Expr transformState, SourceModel.Expr transformHistory, SourceModel.Expr nIterations, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizeExplicit), transformations, transformState, transformHistory, nIterations, expr});
		}

		/**
		 * @see #optimizeExplicit(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param transformations
		 * @param transformState
		 * @param transformHistory
		 * @param nIterations
		 * @param expr
		 * @return the SourceModel.Expr representing an application of optimizeExplicit
		 */
		public static final SourceModel.Expr optimizeExplicit(SourceModel.Expr transformations, SourceModel.Expr transformState, SourceModel.Expr transformHistory, int nIterations, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizeExplicit), transformations, transformState, transformHistory, SourceModel.Expr.makeIntValue(nIterations), expr});
		}

		/**
		 * Name binding for function: optimizeExplicit.
		 * @see #optimizeExplicit(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName optimizeExplicit = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"optimizeExplicit");

		/**
		 * Helper binding method for function: optimizeExplicitTwoStage. 
		 * @param transformations
		 * @param stateTwo
		 * @param transformState
		 * @param transformHistory
		 * @param nIterations
		 * @param expr
		 * @return the SourceModule.expr representing an application of optimizeExplicitTwoStage
		 */
		public static final SourceModel.Expr optimizeExplicitTwoStage(SourceModel.Expr transformations, SourceModel.Expr stateTwo, SourceModel.Expr transformState, SourceModel.Expr transformHistory, SourceModel.Expr nIterations, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizeExplicitTwoStage), transformations, stateTwo, transformState, transformHistory, nIterations, expr});
		}

		/**
		 * @see #optimizeExplicitTwoStage(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param transformations
		 * @param stateTwo
		 * @param transformState
		 * @param transformHistory
		 * @param nIterations
		 * @param expr
		 * @return the SourceModel.Expr representing an application of optimizeExplicitTwoStage
		 */
		public static final SourceModel.Expr optimizeExplicitTwoStage(SourceModel.Expr transformations, SourceModel.Expr stateTwo, SourceModel.Expr transformState, SourceModel.Expr transformHistory, int nIterations, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizeExplicitTwoStage), transformations, stateTwo, transformState, transformHistory, SourceModel.Expr.makeIntValue(nIterations), expr});
		}

		/**
		 * Name binding for function: optimizeExplicitTwoStage.
		 * @see #optimizeExplicitTwoStage(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName optimizeExplicitTwoStage = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"optimizeExplicitTwoStage");

		/**
		 * 3.3.1 - Fusion (not in the original paper but this is where it seems it would
		 * go)
		 * <p>
		 * (f1 e1 e2 � ek (f2 ek+1 ek+2 � ek+m) ek+m+1 � ek+m+n
		 * <p>
		 * to
		 * <p>
		 * let f1$f2 = eBody�� in f1$f2 e1 e2 � ek ek+1 ek+2 � ek+m ek+m+1 � ek+m+n
		 * <p>
		 * The optimizer looks for patterns as shown in the input pattern. If found the
		 * optimizer makes an expression of the form
		 * <p>
		 * eBody: f1 a1 a2 � ak (f2 ak+1 ak+2 � ak+m) ak+m+1 � ak+m+n
		 * <p>
		 * a&lt;n&gt; is a newly created variable name. The expression eBody is then
		 * optimized. For all occurrences of the pattern eBody in eBody� a call to f1$f2
		 * is used instead. This produces the expression eBody��. If there are no calls
		 * to f1 or f2 in the expression eBody�� then the fusion is successful and the
		 * specified output is used as the new expression otherwise there is no change
		 * to the expression and the output equals the input.
		 * 
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param history0 (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>(Cal.Internal.Optimizer_State.TransformHistory, Cal.Core.Prelude.Boolean, Cal.Internal.Optimizer_Expression.Expression)</code>) 
		 */
		public static final SourceModel.Expr performFusion(SourceModel.Expr state, SourceModel.Expr history0, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.performFusion), state, history0, expr});
		}

		/**
		 * Name binding for function: performFusion.
		 * @see #performFusion(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName performFusion = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"performFusion");

		/**
		 * Helper binding method for function: performInlining. 
		 * @param state
		 * @param history
		 * @param letVar
		 * @param arguments
		 * @param letExpr
		 * @param letBody
		 * @param forceInlining
		 * @return the SourceModule.expr representing an application of performInlining
		 */
		public static final SourceModel.Expr performInlining(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr letVar, SourceModel.Expr arguments, SourceModel.Expr letExpr, SourceModel.Expr letBody, SourceModel.Expr forceInlining) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.performInlining), state, history, letVar, arguments, letExpr, letBody, forceInlining});
		}

		/**
		 * @see #performInlining(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param state
		 * @param history
		 * @param letVar
		 * @param arguments
		 * @param letExpr
		 * @param letBody
		 * @param forceInlining
		 * @return the SourceModel.Expr representing an application of performInlining
		 */
		public static final SourceModel.Expr performInlining(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr letVar, int arguments, SourceModel.Expr letExpr, SourceModel.Expr letBody, boolean forceInlining) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.performInlining), state, history, letVar, SourceModel.Expr.makeIntValue(arguments), letExpr, letBody, SourceModel.Expr.makeBooleanValue(forceInlining)});
		}

		/**
		 * Name binding for function: performInlining.
		 * @see #performInlining(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName performInlining = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"performInlining");

		/**
		 * Remove all the constant arguments from the recursive calls in the body since
		 * these value will be embedded in the specialized function.
		 * @param oldFunctor (CAL type: <code>Cal.Internal.Optimizer_Expression.QualifiedName</code>)
		 *          The functor of the function that is specialized.
		 * @param newFunctor (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The name of the new specialized function.
		 * @param isConstArgs (CAL type: <code>[Cal.Core.Prelude.Boolean]</code>)
		 *          Information about the arguments that are constants. The nth
		 * element of the list is True iff the nth argument is a constant.
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 *          The state pass during the transformation.
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 *          The history of the tranformation.
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The expression to remove the constant arguments from.
		 * @return (CAL type: <code>(Cal.Internal.Optimizer_State.TransformHistory, Cal.Core.Prelude.Boolean, Cal.Internal.Optimizer_Expression.Expression)</code>) 
		 */
		public static final SourceModel.Expr removeConstantArguments(SourceModel.Expr oldFunctor, SourceModel.Expr newFunctor, SourceModel.Expr isConstArgs, SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.removeConstantArguments), oldFunctor, newFunctor, isConstArgs, state, history, expr});
		}

		/**
		 * Name binding for function: removeConstantArguments.
		 * @see #removeConstantArguments(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName removeConstantArguments = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"removeConstantArguments");

		/**
		 * Remove any unused alt pattern variables.
		 * @param alt (CAL type: <code>Cal.Internal.Optimizer_Expression.Alt</code>)
		 * @return (CAL type: <code>Cal.Internal.Optimizer_Expression.Alt</code>) 
		 */
		public static final SourceModel.Expr removeUnusedAltVariables(SourceModel.Expr alt) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.removeUnusedAltVariables), alt});
		}

		/**
		 * Name binding for function: removeUnusedAltVariables.
		 * @see #removeUnusedAltVariables(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName removeUnusedAltVariables = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"removeUnusedAltVariables");

		/**
		 * Transformer for renaming case variables in an expression.
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 * @param expression (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>(Cal.Internal.Optimizer_State.TransformHistory, Cal.Core.Prelude.Boolean, Cal.Internal.Optimizer_Expression.Expression)</code>) 
		 */
		public static final SourceModel.Expr renameCaseVariables(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr expression) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.renameCaseVariables), state, history, expression});
		}

		/**
		 * Name binding for function: renameCaseVariables.
		 * @see #renameCaseVariables(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName renameCaseVariables = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"renameCaseVariables");

		/**
		 * Transformer for renaming lambda variables in an expression.
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 * @param expression (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>(Cal.Internal.Optimizer_State.TransformHistory, Cal.Core.Prelude.Boolean, Cal.Internal.Optimizer_Expression.Expression)</code>) 
		 */
		public static final SourceModel.Expr renameLambdaVariables(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr expression) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.renameLambdaVariables), state, history, expression});
		}

		/**
		 * Name binding for function: renameLambdaVariables.
		 * @see #renameLambdaVariables(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName renameLambdaVariables = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"renameLambdaVariables");

		/**
		 * Transformer for renaming let variables in an expression.
		 * @param updateOnlyKeepable (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 * @param expression (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>(Cal.Internal.Optimizer_State.TransformHistory, Cal.Core.Prelude.Boolean, Cal.Internal.Optimizer_Expression.Expression)</code>) 
		 */
		public static final SourceModel.Expr renameLetVariables(SourceModel.Expr updateOnlyKeepable, SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr expression) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.renameLetVariables), updateOnlyKeepable, state, history, expression});
		}

		/**
		 * @see #renameLetVariables(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param updateOnlyKeepable
		 * @param state
		 * @param history
		 * @param expression
		 * @return the SourceModel.Expr representing an application of renameLetVariables
		 */
		public static final SourceModel.Expr renameLetVariables(boolean updateOnlyKeepable, SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr expression) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.renameLetVariables), SourceModel.Expr.makeBooleanValue(updateOnlyKeepable), state, history, expression});
		}

		/**
		 * Name binding for function: renameLetVariables.
		 * @see #renameLetVariables(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName renameLetVariables = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"renameLetVariables");

		/**
		 * Helper binding method for function: renameVariables. 
		 * @param source
		 * @param state
		 * @param history
		 * @param expression
		 * @return the SourceModule.expr representing an application of renameVariables
		 */
		public static final SourceModel.Expr renameVariables(SourceModel.Expr source, SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr expression) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.renameVariables), source, state, history, expression});
		}

		/**
		 * @see #renameVariables(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param source
		 * @param state
		 * @param history
		 * @param expression
		 * @return the SourceModel.Expr representing an application of renameVariables
		 */
		public static final SourceModel.Expr renameVariables(java.lang.String source, SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr expression) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.renameVariables), SourceModel.Expr.makeStringValue(source), state, history, expression});
		}

		/**
		 * Name binding for function: renameVariables.
		 * @see #renameVariables(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName renameVariables = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"renameVariables");

		/**
		 * Takes the tail of a list where the tail of the empty list is the empty list.
		 * @param list (CAL type: <code>[a]</code>)
		 *          The list to take the tail of.
		 * @return (CAL type: <code>[a]</code>) 
		 *          If the list is empty the empty list is returned otherwise the tail of
		 * the list is returned. TODO Get rid of this function.
		 */
		public static final SourceModel.Expr safeTail(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.safeTail), list});
		}

		/**
		 * Name binding for function: safeTail.
		 * @see #safeTail(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName safeTail = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"safeTail");

		/**
		 * For the given list of args build a collection if Prelude.seq statements for the given
		 * type if necessary.
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 *          The context of the current transformation.
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 *          Information from the history of the transformation.
		 * @param dc (CAL type: <code>Cal.Internal.Optimizer_Expression.DataCons</code>)
		 *          The data constructor that the args are applied to.
		 * @param args (CAL type: <code>[Cal.Internal.Optimizer_Expression.Expression]</code>)
		 *          The args of the given data constructor.
		 * @param counter (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          The index of the first arg in the args list.
		 * @param answer (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The expression that is the value of the final expression.
		 * @return (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>) 
		 */
		public static final SourceModel.Expr selectStrictArguments(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr dc, SourceModel.Expr args, SourceModel.Expr counter, SourceModel.Expr answer) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.selectStrictArguments), state, history, dc, args, counter, answer});
		}

		/**
		 * @see #selectStrictArguments(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param state
		 * @param history
		 * @param dc
		 * @param args
		 * @param counter
		 * @param answer
		 * @return the SourceModel.Expr representing an application of selectStrictArguments
		 */
		public static final SourceModel.Expr selectStrictArguments(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr dc, SourceModel.Expr args, int counter, SourceModel.Expr answer) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.selectStrictArguments), state, history, dc, args, SourceModel.Expr.makeIntValue(counter), answer});
		}

		/**
		 * Name binding for function: selectStrictArguments.
		 * @see #selectStrictArguments(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName selectStrictArguments = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"selectStrictArguments");

		/**
		 * Helper binding method for function: simplifySeq_case4. 
		 * @param state
		 * @param history
		 * @param expression
		 * @return the SourceModule.expr representing an application of simplifySeq_case4
		 */
		public static final SourceModel.Expr simplifySeq_case4(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr expression) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.simplifySeq_case4), state, history, expression});
		}

		/**
		 * Name binding for function: simplifySeq_case4.
		 * @see #simplifySeq_case4(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName simplifySeq_case4 = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"simplifySeq_case4");

		/**
		 * Use the type of the lambda expression variable to try to make the type of the given expression
		 * more specific. Currently only working for expressions that are Var's.
		 * @param lambdaType (CAL type: <code>Cal.Internal.Optimizer_Type.Type</code>)
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>) 
		 */
		public static final SourceModel.Expr specializeType(SourceModel.Expr lambdaType, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.specializeType), lambdaType, expr});
		}

		/**
		 * Name binding for function: specializeType.
		 * @see #specializeType(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName specializeType = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"specializeType");

		/**
		 * Helper binding method for function: tShower. 
		 * @param label
		 * @param transform
		 * @param state
		 * @param history
		 * @param expression
		 * @return the SourceModule.expr representing an application of tShower
		 */
		public static final SourceModel.Expr tShower(SourceModel.Expr label, SourceModel.Expr transform, SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr expression) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.tShower), label, transform, state, history, expression});
		}

		/**
		 * @see #tShower(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param label
		 * @param transform
		 * @param state
		 * @param history
		 * @param expression
		 * @return the SourceModel.Expr representing an application of tShower
		 */
		public static final SourceModel.Expr tShower(java.lang.String label, SourceModel.Expr transform, SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr expression) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.tShower), SourceModel.Expr.makeStringValue(label), transform, state, history, expression});
		}

		/**
		 * Name binding for function: tShower.
		 * @see #tShower(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName tShower = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"tShower");

		/**
		 * Helper function for showing the input and output of a transformation if the
		 * transformation changes the expression.
		 * @param label (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @param transform (CAL type: <code>Cal.Internal.Optimizer_State.TransformState -> Cal.Internal.Optimizer_State.TransformHistory -> Cal.Internal.Optimizer_Expression.Expression -> (Cal.Internal.Optimizer_State.TransformHistory, Cal.Core.Prelude.Boolean, Cal.Internal.Optimizer_Expression.Expression)</code>)
		 * @param arg_3 (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param arg_4 (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 * @param arg_5 (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>(Cal.Internal.Optimizer_State.TransformHistory, Cal.Core.Prelude.Boolean, Cal.Internal.Optimizer_Expression.Expression)</code>) 
		 */
		public static final SourceModel.Expr tShower3(SourceModel.Expr label, SourceModel.Expr transform, SourceModel.Expr arg_3, SourceModel.Expr arg_4, SourceModel.Expr arg_5) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.tShower3), label, transform, arg_3, arg_4, arg_5});
		}

		/**
		 * @see #tShower3(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param label
		 * @param transform
		 * @param arg_3
		 * @param arg_4
		 * @param arg_5
		 * @return the SourceModel.Expr representing an application of tShower3
		 */
		public static final SourceModel.Expr tShower3(java.lang.String label, SourceModel.Expr transform, SourceModel.Expr arg_3, SourceModel.Expr arg_4, SourceModel.Expr arg_5) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.tShower3), SourceModel.Expr.makeStringValue(label), transform, arg_3, arg_4, arg_5});
		}

		/**
		 * Name binding for function: tShower3.
		 * @see #tShower3(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName tShower3 = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"tShower3");

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
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"trace2");

		/**
		 * 3.1 - Beta reduction
		 * <p>
		 * (\v -&gt; e) x
		 * <p>
		 * to
		 * <p>
		 * e (x/v)
		 * 
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 *          Information about the current expression.
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 *          Information about the history of the transformation.
		 * @param application (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          expression The expression to transform
		 * @return (CAL type: <code>(Cal.Internal.Optimizer_State.TransformHistory, Cal.Core.Prelude.Boolean, Cal.Internal.Optimizer_Expression.Expression)</code>) 
		 *          The transformed expression.
		 * <p>
		 * TODO: Check the if there are any strictness issues.
		 */
		public static final SourceModel.Expr transform_3_1(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr application) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transform_3_1), state, history, application});
		}

		/**
		 * Name binding for function: transform_3_1.
		 * @see #transform_3_1(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transform_3_1 = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"transform_3_1");

		/**
		 * Helper binding method for function: transform_3_1Examples. 
		 * @param typeConstants
		 * @return the SourceModule.expr representing an application of transform_3_1Examples
		 */
		public static final SourceModel.Expr transform_3_1Examples(SourceModel.Expr typeConstants) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transform_3_1Examples), typeConstants});
		}

		/**
		 * Name binding for function: transform_3_1Examples.
		 * @see #transform_3_1Examples(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transform_3_1Examples = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"transform_3_1Examples");

		/**
		 * 3.2.1 - Dead code removal
		 * <p>
		 * let v = ev in e (where v is not found in e)
		 * <p>
		 * to
		 * <p>
		 * e
		 * 
		 * @param removeOldOnesAsWell (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>(Cal.Internal.Optimizer_State.TransformHistory, Cal.Core.Prelude.Boolean, Cal.Internal.Optimizer_Expression.Expression)</code>) 
		 */
		public static final SourceModel.Expr transform_3_2_1(SourceModel.Expr removeOldOnesAsWell, SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transform_3_2_1), removeOldOnesAsWell, state, history, expr});
		}

		/**
		 * @see #transform_3_2_1(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param removeOldOnesAsWell
		 * @param state
		 * @param history
		 * @param expr
		 * @return the SourceModel.Expr representing an application of transform_3_2_1
		 */
		public static final SourceModel.Expr transform_3_2_1(boolean removeOldOnesAsWell, SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transform_3_2_1), SourceModel.Expr.makeBooleanValue(removeOldOnesAsWell), state, history, expr});
		}

		/**
		 * Name binding for function: transform_3_2_1.
		 * @see #transform_3_2_1(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transform_3_2_1 = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"transform_3_2_1");

		/**
		 * Helper binding method for function: transform_3_2_1Examples. 
		 * @param typeConstants
		 * @return the SourceModule.expr representing an application of transform_3_2_1Examples
		 */
		public static final SourceModel.Expr transform_3_2_1Examples(SourceModel.Expr typeConstants) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transform_3_2_1Examples), typeConstants});
		}

		/**
		 * Name binding for function: transform_3_2_1Examples.
		 * @see #transform_3_2_1Examples(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transform_3_2_1Examples = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"transform_3_2_1Examples");

		/**
		 * 3.2.1 - Inlining
		 * <p>
		 * let v = ev in e
		 * <p>
		 * to
		 * <p>
		 * let v = ev in e[ev/v]
		 * <p>
		 * NOTE:
		 * <p>
		 * 1. The inliner will only work for fully saturated cases of lambda
		 * expressions. This is temporary until the lambda lifter can handle code
		 * generated by the specialier.
		 * 
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>(Cal.Internal.Optimizer_State.TransformHistory, Cal.Core.Prelude.Boolean, Cal.Internal.Optimizer_Expression.Expression)</code>) 
		 */
		public static final SourceModel.Expr transform_3_2_2(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transform_3_2_2), state, history, expr});
		}

		/**
		 * Name binding for function: transform_3_2_2.
		 * @see #transform_3_2_2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transform_3_2_2 = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"transform_3_2_2");

		/**
		 * Helper binding method for function: transform_3_2_2Examples. 
		 * @param typeConstants
		 * @return the SourceModule.expr representing an application of transform_3_2_2Examples
		 */
		public static final SourceModel.Expr transform_3_2_2Examples(SourceModel.Expr typeConstants) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transform_3_2_2Examples), typeConstants});
		}

		/**
		 * Name binding for function: transform_3_2_2Examples.
		 * @see #transform_3_2_2Examples(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transform_3_2_2Examples = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"transform_3_2_2Examples");

		/**
		 * Lift let definitions higher up to open the expression up for other optimizations.
		 * <p>
		 * Input:
		 * <p>
		 * (e1 (let v=d in e2))
		 * <p>
		 * Output:
		 * <p>
		 * (let v=d in (e1 e2)) as long as v is not free in e1.
		 * <p>
		 * Input:
		 * <p>
		 * ((let v=d in e1) e2)
		 * <p>
		 * Output:
		 * <p>
		 * (let v=d in (e1 e2))
		 * 
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>(Cal.Internal.Optimizer_State.TransformHistory, Cal.Core.Prelude.Boolean, Cal.Internal.Optimizer_Expression.Expression)</code>) 
		 */
		public static final SourceModel.Expr transform_3_4_2(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transform_3_4_2), state, history, expr});
		}

		/**
		 * Name binding for function: transform_3_4_2.
		 * @see #transform_3_4_2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transform_3_4_2 = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"transform_3_4_2");

		/**
		 * 3.5.1 - Case float from App
		 * <p>
		 * (case ec of alt1 -&gt; e1; alt2 -&gt; e2; ...) v
		 * <p>
		 * to
		 * <p>
		 * case ec of alt1 -&gt; e1 v; alt2 -&gt; e2 v; ...
		 * 
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 *          Information about the current expression.
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 *          History of the transformation.
		 * @param expression (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          Expression to transform
		 * @return (CAL type: <code>(Cal.Internal.Optimizer_State.TransformHistory, Cal.Core.Prelude.Boolean, Cal.Internal.Optimizer_Expression.Expression)</code>) 
		 *          The transformed expression is applicable
		 * <p>
		 * TODO This should be renaming the alternative variables to avoid name capture.
		 */
		public static final SourceModel.Expr transform_3_5_1(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr expression) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transform_3_5_1), state, history, expression});
		}

		/**
		 * Name binding for function: transform_3_5_1.
		 * @see #transform_3_5_1(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transform_3_5_1 = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"transform_3_5_1");

		/**
		 * Embed the outer case into the inner case. See test 7-9 and 15 in Optimizer_Tests.cal
		 * <p>
		 * TODO: What about name capture. This should be safe now right with the renaming. Check it out.
		 * 
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 * @param expression (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>(Cal.Internal.Optimizer_State.TransformHistory, Cal.Core.Prelude.Boolean, Cal.Internal.Optimizer_Expression.Expression)</code>) 
		 */
		public static final SourceModel.Expr transform_3_5_2(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr expression) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transform_3_5_2), state, history, expression});
		}

		/**
		 * Name binding for function: transform_3_5_2.
		 * @see #transform_3_5_2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transform_3_5_2 = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"transform_3_5_2");

		/**
		 * let
		 * outerExpr1 = case innerExpr of &lt;innerPattern1&gt; -&gt; ...;      // Inner Switch
		 * in
		 * case outerExpr1 of          // Outer Switch
		 * &lt;outerPattern1&gt; -&gt; ... 
		 * &lt;outerPattern2&gt; -&gt; ... outerExpr1? ...
		 * <p>
		 * =&gt;
		 * <p>
		 * case innerExpr of
		 * &lt;innerPattern1&gt; -&gt;
		 * let 
		 * outerExpr1 = innerExpr1;
		 * in
		 * case outerExpr1 of
		 * &lt;outerPattern1&gt; -&gt; ...
		 * &lt;outerPattern2&gt; -&gt; ... outerExpr1? ...
		 * 
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 * @param expression (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>(Cal.Internal.Optimizer_State.TransformHistory, Cal.Core.Prelude.Boolean, Cal.Internal.Optimizer_Expression.Expression)</code>) 
		 */
		public static final SourceModel.Expr transform_3_5_2_withInlining(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr expression) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transform_3_5_2_withInlining), state, history, expression});
		}

		/**
		 * Name binding for function: transform_3_5_2_withInlining.
		 * @see #transform_3_5_2_withInlining(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transform_3_5_2_withInlining = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"transform_3_5_2_withInlining");

		/**
		 * 3.5.5 - Lambda floating through case. This is another one I made up and it
		 * not part of the original paper but would go here if it was.
		 * <p>
		 * case (\v1 v2 ... vk -&gt; &lt;expr&gt;) of &lt;alts&gt;
		 * <p>
		 * to
		 * <p>
		 * \v1 v2 ... vk -&gt; case expr of &lt;alts&gt;
		 * <p>
		 * TODO Rename the variables raised to avoid name capture.
		 * 
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 * @param caseExpr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>(Cal.Internal.Optimizer_State.TransformHistory, Cal.Core.Prelude.Boolean, Cal.Internal.Optimizer_Expression.Expression)</code>) 
		 */
		public static final SourceModel.Expr transform_3_5_5(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr caseExpr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transform_3_5_5), state, history, caseExpr});
		}

		/**
		 * Name binding for function: transform_3_5_5.
		 * @see #transform_3_5_5(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transform_3_5_5 = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"transform_3_5_5");

		/**
		 * The `seq` and the seq type expression have a different form. This converts all of the expression so that
		 * they have the same form, the seq form.
		 * <p>
		 * These are the transformation that are performed : 
		 * <p>
		 * 1. NOT VALID ((f (seq (seq x y) x)) y) =&gt; ((seq x (seq y (f x))) y) iff f is a variable and of type lambda expression
		 * 2. ((seq x y) z) =&gt; (seq x (y z))
		 * 3. (seq (seq x y) z) =&gt; (seq x (seq y z))
		 * 
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 * @param expression (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>(Cal.Internal.Optimizer_State.TransformHistory, Cal.Core.Prelude.Boolean, Cal.Internal.Optimizer_Expression.Expression)</code>) 
		 */
		public static final SourceModel.Expr transform_canonizeSeq(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr expression) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transform_canonizeSeq), state, history, expression});
		}

		/**
		 * Name binding for function: transform_canonizeSeq.
		 * @see #transform_canonizeSeq(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transform_canonizeSeq = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"transform_canonizeSeq");

		/**
		 * Look for expressions of the form
		 * <p>
		 * switch expr of True -&gt; altExpr1; False -&gt; altExpr2;
		 * <p>
		 * and convert them to If applications
		 * 
		 * @param expression (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The expression to check for switch to if conversions.
		 * <p>
		 * Note: Only does the conversion of the switch expression is not another switch expression.
		 * 
		 * @return (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>) 
		 */
		public static final SourceModel.Expr transform_convertToIfs(SourceModel.Expr expression) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transform_convertToIfs), expression});
		}

		/**
		 * Name binding for function: transform_convertToIfs.
		 * @see #transform_convertToIfs(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transform_convertToIfs = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"transform_convertToIfs");

		/**
		 * For patterns that do not have all the variables defined. Complete the patterns with dummy variables.
		 * This makes pattern matching easier. For example
		 * <p>
		 * data d1 x y x
		 * <p>
		 * case value of
		 * d1 {x} -&gt; ...
		 * <p>
		 * becomes
		 * <p>
		 * case value of
		 * d1 {x, y=dummy1, z=dummy2} -&gt;
		 * 
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 * @param expression (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>(Cal.Internal.Optimizer_State.TransformHistory, Cal.Core.Prelude.Boolean, Cal.Internal.Optimizer_Expression.Expression)</code>) 
		 */
		public static final SourceModel.Expr transform_defineAllAltVariables(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr expression) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transform_defineAllAltVariables), state, history, expression});
		}

		/**
		 * Name binding for function: transform_defineAllAltVariables.
		 * @see #transform_defineAllAltVariables(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transform_defineAllAltVariables = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"transform_defineAllAltVariables");

		/**
		 * Evaluate the given various arithmetic expression.
		 * <p>
		 * 1. Integer comparisons involving constants.
		 * 2. x + 0 
		 * <p>
		 * TODO Work with vars
		 * 
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>(Cal.Internal.Optimizer_State.TransformHistory, Cal.Core.Prelude.Boolean, Cal.Internal.Optimizer_Expression.Expression)</code>) 
		 */
		public static final SourceModel.Expr transform_evaluateArithmeticExpressions(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transform_evaluateArithmeticExpressions), state, history, expr});
		}

		/**
		 * Name binding for function: transform_evaluateArithmeticExpressions.
		 * @see #transform_evaluateArithmeticExpressions(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transform_evaluateArithmeticExpressions = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"transform_evaluateArithmeticExpressions");

		/**
		 * Evaluate case expression with known values. See tests 22-29, 64, 66 and 88 in Optimizer_Test.cal for examples.
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 * @param caseExpr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>(Cal.Internal.Optimizer_State.TransformHistory, Cal.Core.Prelude.Boolean, Cal.Internal.Optimizer_Expression.Expression)</code>) 
		 */
		public static final SourceModel.Expr transform_evaluateCase(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr caseExpr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transform_evaluateCase), state, history, caseExpr});
		}

		/**
		 * Name binding for function: transform_evaluateCase.
		 * @see #transform_evaluateCase(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transform_evaluateCase = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"transform_evaluateCase");

		/**
		 * Evaluate record selection if possible. See tests 17-21 in Optimizer_Tests.cal
		 * <p>
		 * For example, (Just x).Just.value =&gt; x.
		 * 
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 * @param expression (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>(Cal.Internal.Optimizer_State.TransformHistory, Cal.Core.Prelude.Boolean, Cal.Internal.Optimizer_Expression.Expression)</code>) 
		 */
		public static final SourceModel.Expr transform_evaluateRecordSelection(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr expression) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transform_evaluateRecordSelection), state, history, expression});
		}

		/**
		 * Name binding for function: transform_evaluateRecordSelection.
		 * @see #transform_evaluateRecordSelection(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transform_evaluateRecordSelection = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"transform_evaluateRecordSelection");

		/**
		 * This a hack. This will return true iff
		 * <p>
		 * 1. The expression contains non-top level cases. 
		 * 2. The expression contains lambda expressions.
		 * 
		 * @param isTopLevelExpr (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 *          Is this expression starting at the top level or lower. If lower then lambda's and case expressions are not allowed.
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The expression to check for bad structure.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr transform_hasBadStructure(SourceModel.Expr isTopLevelExpr, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transform_hasBadStructure), isTopLevelExpr, expr});
		}

		/**
		 * @see #transform_hasBadStructure(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param isTopLevelExpr
		 * @param expr
		 * @return the SourceModel.Expr representing an application of transform_hasBadStructure
		 */
		public static final SourceModel.Expr transform_hasBadStructure(boolean isTopLevelExpr, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transform_hasBadStructure), SourceModel.Expr.makeBooleanValue(isTopLevelExpr), expr});
		}

		/**
		 * Name binding for function: transform_hasBadStructure.
		 * @see #transform_hasBadStructure(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transform_hasBadStructure = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"transform_hasBadStructure");

		/**
		 * Convert if/then/else's into case expression. This is to avoid having to write
		 * special transformations for the 'if' expressions.
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 *          The context of the current expression.
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The expression to transforms
		 * @return (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>) 
		 *          The expression with if/then/else call changed to a case expression if
		 * applicable.
		 * <p>
		 * TODO - rewrite this using flattenExpression;
		 */
		public static final SourceModel.Expr transform_if_to_case(SourceModel.Expr state, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transform_if_to_case), state, expr});
		}

		/**
		 * Name binding for function: transform_if_to_case.
		 * @see #transform_if_to_case(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transform_if_to_case = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"transform_if_to_case");

		/**
		 * Lift let definitions higher up to open the expression up for other optimizations. (3.4.3)
		 * <p>
		 * Input:
		 * <p>
		 * case (let v = ev in e) of alts
		 * <p>
		 * Output:
		 * <p>
		 * let v = ev in (case e of alts) where v is not free in alts.
		 * 
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>(Cal.Internal.Optimizer_State.TransformHistory, Cal.Core.Prelude.Boolean, Cal.Internal.Optimizer_Expression.Expression)</code>) 
		 */
		public static final SourceModel.Expr transform_letFloatingFromCaseScrutinee(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transform_letFloatingFromCaseScrutinee), state, history, expr});
		}

		/**
		 * Name binding for function: transform_letFloatingFromCaseScrutinee.
		 * @see #transform_letFloatingFromCaseScrutinee(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transform_letFloatingFromCaseScrutinee = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"transform_letFloatingFromCaseScrutinee");

		/**
		 * This transformation is tested be test 74. Removed redundant case checks.
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 * @param expression (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>(Cal.Internal.Optimizer_State.TransformHistory, Cal.Core.Prelude.Boolean, Cal.Internal.Optimizer_Expression.Expression)</code>) 
		 */
		public static final SourceModel.Expr transform_redundantCases(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr expression) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transform_redundantCases), state, history, expression});
		}

		/**
		 * Name binding for function: transform_redundantCases.
		 * @see #transform_redundantCases(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transform_redundantCases = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"transform_redundantCases");

		/**
		 * Remove unused variables from Alt patterns.
		 * @param expression (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>) 
		 */
		public static final SourceModel.Expr transform_removeUnusedAltVariables(SourceModel.Expr expression) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transform_removeUnusedAltVariables), expression});
		}

		/**
		 * Name binding for function: transform_removeUnusedAltVariables.
		 * @see #transform_removeUnusedAltVariables(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transform_removeUnusedAltVariables = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"transform_removeUnusedAltVariables");

		/**
		 * case x of True -&gt; True; False -&gt; False; =&gt; x
		 * case error arg of ... =&gt; error arg
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 * @param caseExpr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>(Cal.Internal.Optimizer_State.TransformHistory, Cal.Core.Prelude.Boolean, Cal.Internal.Optimizer_Expression.Expression)</code>) 
		 */
		public static final SourceModel.Expr transform_simplifyCase(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr caseExpr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transform_simplifyCase), state, history, caseExpr});
		}

		/**
		 * Name binding for function: transform_simplifyCase.
		 * @see #transform_simplifyCase(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transform_simplifyCase = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"transform_simplifyCase");

		/**
		 * Simplify statements such as "x `seq` x" to "x". See tests 30-32 in Optimizer_Tests.cal. 
		 * Now also corresponds to tests 44 to 48, and 65, and 95.
		 * <p>
		 * The following transformations are performed.
		 * <p>
		 * 1. x `seq` x =&gt; x
		 * 2. Lambda x1 (Lambda x2 ... (prelude.seq x1 y) =&gt; Lambda !x1 (Lambda x2 ... y iff the intervening lambda vars are all not strict
		 * 3. (x `seq` case x of ...) =&gt; (case x of ...).
		 * 4. (seq x (seq y ((f x) y))) where f :: !a -&gt; !b -&gt; ... =&gt; (f x) y
		 * 5. y `seq` z =&gt; z iff y is known to be WHNF
		 * 6. (\!x -&gt; case x of ...) =&gt; (\x -&gt; case x of ...) 
		 * 
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 * @param expression (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>(Cal.Internal.Optimizer_State.TransformHistory, Cal.Core.Prelude.Boolean, Cal.Internal.Optimizer_Expression.Expression)</code>) 
		 */
		public static final SourceModel.Expr transform_simplifySeq(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr expression) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transform_simplifySeq), state, history, expression});
		}

		/**
		 * Name binding for function: transform_simplifySeq.
		 * @see #transform_simplifySeq(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transform_simplifySeq = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"transform_simplifySeq");

		/**
		 * Look for functions to create specializations of.
		 * <p>
		 * Input:
		 * <p>
		 * let f a1 a2 � ak = � (f a1 a2� � ai� � ak) � in � (f e1 e2 � ei � ek) �
		 * <p>
		 * The function f calls itself recursively. A number of arguments are passed
		 * unchanged so they are constant for any given call.
		 * <p>
		 * Output:
		 * <p>
		 * f� a2 �ai = � f a2� � ai� [e1/a1]�[ek/ak]
		 * <p>
		 * f� e2 � ei
		 * <p>
		 * Embed the constant values into the function f and remove the arguments from
		 * the calls. Change the call to call f1 instead of f.
		 * <p>
		 * Notes: 
		 * <p>
		 * This makes two passes at specialization. The first pass will try embedding arguments even if
		 * the argument is not constant in the recursive function. If the function body reduces so there is
		 * no recursive call then the new function will be kept. For example, 
		 * <p>
		 * test11 = test11_andListMap noStrictId [];
		 * <p>
		 * test11_andListMap nToTest !list =
		 * case list of
		 * [] -&gt; True;
		 * head : tail -&gt; nToTest head &amp;&amp; test11_andListMap nToTest tail;
		 * ;
		 * <p>
		 * answer11 = True;    
		 * <p>
		 * If the reduction fails then the old specialization will be applied.
		 * 
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>(Cal.Internal.Optimizer_State.TransformHistory, Cal.Core.Prelude.Boolean, Cal.Internal.Optimizer_Expression.Expression)</code>) 
		 */
		public static final SourceModel.Expr transform_specialization(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transform_specialization), state, history, expr});
		}

		/**
		 * Name binding for function: transform_specialization.
		 * @see #transform_specialization(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transform_specialization = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"transform_specialization");

		/**
		 * Helper binding method for function: transformations. 
		 * @return the SourceModule.expr representing an application of transformations
		 */
		public static final SourceModel.Expr transformations() {
			return SourceModel.Expr.Var.make(Functions.transformations);
		}

		/**
		 * Name binding for function: transformations.
		 * @see #transformations()
		 */
		public static final QualifiedName transformations = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"transformations");

		/**
		 * First pass at converting an expression to a type. This can be used to unify all the types and
		 * transfer type information across expressions.
		 * @param typeConstants (CAL type: <code>Cal.Internal.Optimizer_State.JPreludeTypeConstants</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr unitTests(SourceModel.Expr typeConstants) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.unitTests), typeConstants});
		}

		/**
		 * Name binding for function: unitTests.
		 * @see #unitTests(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName unitTests = 
			QualifiedName.make(
				CAL_Optimizer_Transformations_internal.MODULE_NAME, 
				"unitTests");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = 103300719;

}
