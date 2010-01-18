/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_Optimizer_State_internal.java)
 * was generated from CAL module: Cal.Internal.Optimizer_State.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Internal.Optimizer_State module from Java code.
 *  
 * Creation date: Thu Oct 18 09:02:37 PDT 2007
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
 * The file contains data objects that hold state for the traversal functions. There are two main types. 
 * <p>
 * TransformState -&gt; This contains the information about the context of the traversal. For 
 * example when processing a given expression this will contain information about the 
 * type of given symbols if known. This variable is not threaded but maintained on the stack.
 * <p>
 * TransformHistory -&gt; This is threaded throughout the call and contains information about the history of the
 * current traversal. For example, if a fusion of two functions has been attempted but had failed then
 * there is information in the history about this failure to avoid repeating the attempt.
 * <p>
 * This file also contains helper functions that involve types, expression and the current state. For example,
 * getting the type an expression requires the current state that contains information mapping symbols to types.
 * 
 * @author Greg McClement
 */
public final class CAL_Optimizer_State_internal {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Internal.Optimizer_State");

	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Internal.Optimizer_State module.
	 */
	public static final class TypeConstructors {
		/**
		 * Information about the fusion being attempted.
		 */
		public static final QualifiedName FusionContext = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"FusionContext");

		/**
		 * Used to determine if an expression containing a case can be inlined.
		 */
		public static final QualifiedName InlinableState = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"InlinableState");

		/** Name binding for TypeConsApp: InlinableTransition. */
		public static final QualifiedName InlinableTransition = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"InlinableTransition");

		/**
		 * PreludeTypeConstants definitions and associated functions.
		 */
		public static final QualifiedName JPreludeTypeConstants = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"JPreludeTypeConstants");

		/**
		 * Information kept about the history of the transformation attempt.
		 */
		public static final QualifiedName TransformHistory = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"TransformHistory");

		/** Name binding for TypeConsApp: TransformState. */
		public static final QualifiedName TransformState = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"TransformState");

	}
	/**
	 * This inner class (DataConstructors) contains constants
	 * and methods related to binding to CAL DataConstructors in the Cal.Internal.Optimizer_State module.
	 */
	public static final class DataConstructors {
		/*
		 * DataConstructors for the Cal.Internal.Optimizer_State.FusionContext data type.
		 */

		/**
		 * Binding for DataConstructor: Cal.Internal.Optimizer_State.FusionContext.
		 * @param isFusionCall
		 * @param f1_name
		 * @param f2_name
		 * @return the SourceModule.Expr representing an application of Cal.Internal.Optimizer_State.FusionContext
		 */
		public static final SourceModel.Expr FusionContext(SourceModel.Expr isFusionCall, SourceModel.Expr f1_name, SourceModel.Expr f2_name) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.FusionContext), isFusionCall, f1_name, f2_name});
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_State.FusionContext.
		 * @see #FusionContext(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName FusionContext = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"FusionContext");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_State.FusionContext.
		 * @see #FusionContext(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int FusionContext_ordinal = 0;

		/*
		 * DataConstructors for the Cal.Internal.Optimizer_State.InlinableState data type.
		 */

		/**
		 * Binding for DataConstructor: Cal.Internal.Optimizer_State.TopLevel.
		 * @return the SourceModule.Expr representing an application of Cal.Internal.Optimizer_State.TopLevel
		 */
		public static final SourceModel.Expr TopLevel() {
			return SourceModel.Expr.DataCons.make(DataConstructors.TopLevel);
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_State.TopLevel.
		 * @see #TopLevel()
		 */
		public static final QualifiedName TopLevel = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"TopLevel");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_State.TopLevel.
		 * @see #TopLevel()
		 */
		public static final int TopLevel_ordinal = 0;

		/**
		 * Binding for DataConstructor: Cal.Internal.Optimizer_State.InApp.
		 * @return the SourceModule.Expr representing an application of Cal.Internal.Optimizer_State.InApp
		 */
		public static final SourceModel.Expr InApp() {
			return SourceModel.Expr.DataCons.make(DataConstructors.InApp);
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_State.InApp.
		 * @see #InApp()
		 */
		public static final QualifiedName InApp = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"InApp");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_State.InApp.
		 * @see #InApp()
		 */
		public static final int InApp_ordinal = 1;

		/**
		 * Binding for DataConstructor: Cal.Internal.Optimizer_State.InCaseAlt.
		 * @return the SourceModule.Expr representing an application of Cal.Internal.Optimizer_State.InCaseAlt
		 */
		public static final SourceModel.Expr InCaseAlt() {
			return SourceModel.Expr.DataCons.make(DataConstructors.InCaseAlt);
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_State.InCaseAlt.
		 * @see #InCaseAlt()
		 */
		public static final QualifiedName InCaseAlt = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"InCaseAlt");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_State.InCaseAlt.
		 * @see #InCaseAlt()
		 */
		public static final int InCaseAlt_ordinal = 2;

		/**
		 * Binding for DataConstructor: Cal.Internal.Optimizer_State.InCaseExpr.
		 * @return the SourceModule.Expr representing an application of Cal.Internal.Optimizer_State.InCaseExpr
		 */
		public static final SourceModel.Expr InCaseExpr() {
			return SourceModel.Expr.DataCons.make(DataConstructors.InCaseExpr);
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_State.InCaseExpr.
		 * @see #InCaseExpr()
		 */
		public static final QualifiedName InCaseExpr = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"InCaseExpr");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_State.InCaseExpr.
		 * @see #InCaseExpr()
		 */
		public static final int InCaseExpr_ordinal = 3;

		/**
		 * Binding for DataConstructor: Cal.Internal.Optimizer_State.NotAllowed.
		 * @return the SourceModule.Expr representing an application of Cal.Internal.Optimizer_State.NotAllowed
		 */
		public static final SourceModel.Expr NotAllowed() {
			return SourceModel.Expr.DataCons.make(DataConstructors.NotAllowed);
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_State.NotAllowed.
		 * @see #NotAllowed()
		 */
		public static final QualifiedName NotAllowed = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"NotAllowed");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_State.NotAllowed.
		 * @see #NotAllowed()
		 */
		public static final int NotAllowed_ordinal = 4;

		/*
		 * DataConstructors for the Cal.Internal.Optimizer_State.InlinableTransition data type.
		 */

		/**
		 * Binding for DataConstructor: Cal.Internal.Optimizer_State.SeeAlt.
		 * @return the SourceModule.Expr representing an application of Cal.Internal.Optimizer_State.SeeAlt
		 */
		public static final SourceModel.Expr SeeAlt() {
			return SourceModel.Expr.DataCons.make(DataConstructors.SeeAlt);
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_State.SeeAlt.
		 * @see #SeeAlt()
		 */
		public static final QualifiedName SeeAlt = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"SeeAlt");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_State.SeeAlt.
		 * @see #SeeAlt()
		 */
		public static final int SeeAlt_ordinal = 0;

		/**
		 * Binding for DataConstructor: Cal.Internal.Optimizer_State.SeeCaseExpr.
		 * @return the SourceModule.Expr representing an application of Cal.Internal.Optimizer_State.SeeCaseExpr
		 */
		public static final SourceModel.Expr SeeCaseExpr() {
			return SourceModel.Expr.DataCons.make(DataConstructors.SeeCaseExpr);
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_State.SeeCaseExpr.
		 * @see #SeeCaseExpr()
		 */
		public static final QualifiedName SeeCaseExpr = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"SeeCaseExpr");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_State.SeeCaseExpr.
		 * @see #SeeCaseExpr()
		 */
		public static final int SeeCaseExpr_ordinal = 1;

		/**
		 * Binding for DataConstructor: Cal.Internal.Optimizer_State.SeeLeftApp.
		 * @return the SourceModule.Expr representing an application of Cal.Internal.Optimizer_State.SeeLeftApp
		 */
		public static final SourceModel.Expr SeeLeftApp() {
			return SourceModel.Expr.DataCons.make(DataConstructors.SeeLeftApp);
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_State.SeeLeftApp.
		 * @see #SeeLeftApp()
		 */
		public static final QualifiedName SeeLeftApp = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"SeeLeftApp");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_State.SeeLeftApp.
		 * @see #SeeLeftApp()
		 */
		public static final int SeeLeftApp_ordinal = 2;

		/**
		 * Binding for DataConstructor: Cal.Internal.Optimizer_State.SeeRightApp.
		 * @return the SourceModule.Expr representing an application of Cal.Internal.Optimizer_State.SeeRightApp
		 */
		public static final SourceModel.Expr SeeRightApp() {
			return SourceModel.Expr.DataCons.make(DataConstructors.SeeRightApp);
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_State.SeeRightApp.
		 * @see #SeeRightApp()
		 */
		public static final QualifiedName SeeRightApp = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"SeeRightApp");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_State.SeeRightApp.
		 * @see #SeeRightApp()
		 */
		public static final int SeeRightApp_ordinal = 3;

		/**
		 * Binding for DataConstructor: Cal.Internal.Optimizer_State.SeeKeepableOldLetDef.
		 * @return the SourceModule.Expr representing an application of Cal.Internal.Optimizer_State.SeeKeepableOldLetDef
		 */
		public static final SourceModel.Expr SeeKeepableOldLetDef() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.SeeKeepableOldLetDef);
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_State.SeeKeepableOldLetDef.
		 * @see #SeeKeepableOldLetDef()
		 */
		public static final QualifiedName SeeKeepableOldLetDef = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"SeeKeepableOldLetDef");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_State.SeeKeepableOldLetDef.
		 * @see #SeeKeepableOldLetDef()
		 */
		public static final int SeeKeepableOldLetDef_ordinal = 4;

		/**
		 * Binding for DataConstructor: Cal.Internal.Optimizer_State.Other.
		 * @return the SourceModule.Expr representing an application of Cal.Internal.Optimizer_State.Other
		 */
		public static final SourceModel.Expr Other() {
			return SourceModel.Expr.DataCons.make(DataConstructors.Other);
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_State.Other.
		 * @see #Other()
		 */
		public static final QualifiedName Other = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"Other");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_State.Other.
		 * @see #Other()
		 */
		public static final int Other_ordinal = 5;

		/*
		 * DataConstructors for the Cal.Internal.Optimizer_State.TransformHistory data type.
		 */

		/**
		 * 
		 * @param counter (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          Used to generate unique variable names.
		 * @param currentIteration (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          The number of times that the optimizer has run.
		 * @param failedFusions (CAL type: <code>[(Cal.Internal.Optimizer_Expression.QualifiedName, Cal.Internal.Optimizer_Expression.QualifiedName)]</code>)
		 *          Functions that are known to not be fusable.
		 * @param recursiveFunctions (CAL type: <code>[(Cal.Internal.Optimizer_Expression.QualifiedName, Cal.Internal.Optimizer_Expression.Expression, Cal.Core.Prelude.Maybe [Cal.Internal.Optimizer_Type.Type], Cal.Core.Prelude.Boolean, [Cal.Core.Prelude.Boolean])]</code>)
		 *          Newly created recursive functions used by fusion and specialization code.
		 * @param liftedLetFunctions (CAL type: <code>[Cal.Internal.Optimizer_Expression.QualifiedName]</code>)
		 *          Let functions that are created by lifting
		 * @param newCoreFunctions (CAL type: <code>[Cal.Internal.Optimizer_Expression.CoreFunction]</code>)
		 *          List of all newly created helper functions.
		 * @param startTime (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Core.Prelude.Long</code>)
		 *          The time that the transformation was start.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr TransformHistory(SourceModel.Expr counter, SourceModel.Expr currentIteration, SourceModel.Expr failedFusions, SourceModel.Expr recursiveFunctions, SourceModel.Expr liftedLetFunctions, SourceModel.Expr newCoreFunctions, SourceModel.Expr startTime) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.TransformHistory), counter, currentIteration, failedFusions, recursiveFunctions, liftedLetFunctions, newCoreFunctions, startTime});
		}

		/**
		 * @see #TransformHistory(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param counter
		 * @param currentIteration
		 * @param failedFusions
		 * @param recursiveFunctions
		 * @param liftedLetFunctions
		 * @param newCoreFunctions
		 * @param startTime
		 * @return org.openquark.cal.compiler.SourceModel.Expr
		 */
		public static final SourceModel.Expr TransformHistory(int counter, int currentIteration, SourceModel.Expr failedFusions, SourceModel.Expr recursiveFunctions, SourceModel.Expr liftedLetFunctions, SourceModel.Expr newCoreFunctions, SourceModel.Expr startTime) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.TransformHistory), SourceModel.Expr.makeIntValue(counter), SourceModel.Expr.makeIntValue(currentIteration), failedFusions, recursiveFunctions, liftedLetFunctions, newCoreFunctions, startTime});
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_State.TransformHistory.
		 * @see #TransformHistory(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName TransformHistory = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"TransformHistory");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_State.TransformHistory.
		 * @see #TransformHistory(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int TransformHistory_ordinal = 0;

		/*
		 * DataConstructors for the Cal.Internal.Optimizer_State.TransformState data type.
		 */

		/**
		 * 
		 * @param name (CAL type: <code>Cal.Internal.Optimizer_Expression.QualifiedName</code>)
		 *          The name of the function being optimized
		 * @param currentFunctionName (CAL type: <code>Cal.Internal.Optimizer_Expression.QualifiedName</code>)
		 *          The name last function being defined. TODO Remove this.
		 * @param recursiveFunctions (CAL type: <code>[(Cal.Internal.Optimizer_Expression.QualifiedName, Cal.Internal.Optimizer_Expression.Expression, Cal.Core.Prelude.Maybe [Cal.Internal.Optimizer_Type.Type], Cal.Core.Prelude.Boolean, [Cal.Core.Prelude.Boolean])]</code>)
		 *          Core function that are recursive. (QualifiedName, Expression, Maybe Type, [Int]) == (functionName, functionBody, expressionType, trueIfArgumentIsConstant).
		 * @param topLevelBoundNames (CAL type: <code>[Cal.Internal.Optimizer_Expression.QualifiedName]</code>)
		 *          Names that are known to be bound at the top level scope. This is for the lambda lifter. Expressions that are moving to the top level can only assume that these names are bound.
		 * @param inContextBoundNames (CAL type: <code>[Cal.Internal.Optimizer_Expression.QualifiedName]</code>)
		 *          Names that are known to be bound in the current expression context. These can be assumed to be bound in components that are not being lifted to the top level.
		 * @param nameToTypeList (CAL type: <code>[(Cal.Internal.Optimizer_Expression.QualifiedName, Cal.Internal.Optimizer_Type.Type)]</code>)
		 *          Maps function name to type.
		 * @param debugPath (CAL type: <code>[Cal.Internal.Optimizer_Expression.QualifiedName]</code>)
		 *          A hierarchy of the function names from the root expression to the current one.
		 * @param typeConstants (CAL type: <code>Cal.Internal.Optimizer_State.JPreludeTypeConstants</code>)
		 *          TypeConstants object used to get type expressions for some basic types.
		 * @param traverseCoreFunctions (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 *          Core functions will be traversed only if this flag is set.
		 * @param safeForInliningVars (CAL type: <code>[Cal.Internal.Optimizer_Expression.QualifiedName]</code>)
		 *          Names that are lambda, case, or let vars. This is used to help the inliner know that certain variables can be inlined because they have no side effects. TODO Have the program compute if a variable thatis a CAL is also okay
		 * @param fusionContext (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Internal.Optimizer_State.FusionContext</code>)
		 *          The context of the fusion being performed if any.
		 * @param knownForms (CAL type: <code>[(Cal.Internal.Optimizer_Expression.Expression, Cal.Internal.Optimizer_Expression.CaseConst, [(Cal.Internal.Optimizer_Expression.FieldName, Cal.Internal.Optimizer_Expression.QualifiedName, Cal.Internal.Optimizer_Type.Type)])]</code>)
		 *          Maps variable name to a data constructor or value that the name is known to have. This case arise after traversing a switch statement. In the Alt the form of the case variable is known.
		 * @param knownToNotBeForms (CAL type: <code>[(Cal.Internal.Optimizer_Expression.Expression, [Cal.Internal.Optimizer_Expression.CaseConst])]</code>)
		 *          The expression is know to now have any of the given forms. This is used to eliminate case alternatives when possible.
		 * @param parentExpr (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The parent expression of the current expression.
		 * @param grandParentExpr (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The grandparent expression of the current expression.
		 * @param inlinedContext (CAL type: <code>[Cal.Internal.Optimizer_Expression.QualifiedName]</code>)
		 *          Information about the functions that this expression was inlined from.
		 * @param knownToBeWHNF (CAL type: <code>[Cal.Internal.Optimizer_Expression.Expression]</code>)
		 *          The given expression is known to be in WHNF. This is used to eliminate unused seq's.
		 * @param alreadySeqed (CAL type: <code>[Cal.Internal.Optimizer_Expression.Expression]</code>)
		 *          List of expressions that are already seq'ed and do not need to be sequed again.
		 * @param nonCalFunctions (CAL type: <code>[Cal.Internal.Optimizer_Expression.QualifiedName]</code>)
		 *          The name of primitive or foreign functions.
		 * @param inlinableState (CAL type: <code>Cal.Internal.Optimizer_State.InlinableState</code>)
		 *          If the current expression is going to be evaluated
		 * strictly. This is used to avoid inlining case expression in the
		 * wrong spot. Problems can arise using just the Johnsonn names for
		 * this case (f a1 &amp;&amp; f a2). Each instance of f will create a new
		 * specialization and the Johnsonn names are the same. So there will be
		 * two different functions with the same name. The uniqueNumberPath is
		 * used to make the name unique. If you imagine the expression as a
		 * tree each element in the path is a number indicating the child
		 * number of the node to take in order to reach the current node. For
		 * example the first occurence of f is path [1,1] and the second is
		 * [2,1]
		 * <p>
		 * TODO Change recursive function to a core function with extra bits.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr TransformState(SourceModel.Expr name, SourceModel.Expr currentFunctionName, SourceModel.Expr recursiveFunctions, SourceModel.Expr topLevelBoundNames, SourceModel.Expr inContextBoundNames, SourceModel.Expr nameToTypeList, SourceModel.Expr debugPath, SourceModel.Expr typeConstants, SourceModel.Expr traverseCoreFunctions, SourceModel.Expr safeForInliningVars, SourceModel.Expr fusionContext, SourceModel.Expr knownForms, SourceModel.Expr knownToNotBeForms, SourceModel.Expr parentExpr, SourceModel.Expr grandParentExpr, SourceModel.Expr inlinedContext, SourceModel.Expr knownToBeWHNF, SourceModel.Expr alreadySeqed, SourceModel.Expr nonCalFunctions, SourceModel.Expr inlinableState) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.TransformState), name, currentFunctionName, recursiveFunctions, topLevelBoundNames, inContextBoundNames, nameToTypeList, debugPath, typeConstants, traverseCoreFunctions, safeForInliningVars, fusionContext, knownForms, knownToNotBeForms, parentExpr, grandParentExpr, inlinedContext, knownToBeWHNF, alreadySeqed, nonCalFunctions, inlinableState});
		}

		/**
		 * @see #TransformState(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param name
		 * @param currentFunctionName
		 * @param recursiveFunctions
		 * @param topLevelBoundNames
		 * @param inContextBoundNames
		 * @param nameToTypeList
		 * @param debugPath
		 * @param typeConstants
		 * @param traverseCoreFunctions
		 * @param safeForInliningVars
		 * @param fusionContext
		 * @param knownForms
		 * @param knownToNotBeForms
		 * @param parentExpr
		 * @param grandParentExpr
		 * @param inlinedContext
		 * @param knownToBeWHNF
		 * @param alreadySeqed
		 * @param nonCalFunctions
		 * @param inlinableState
		 * @return org.openquark.cal.compiler.SourceModel.Expr
		 */
		public static final SourceModel.Expr TransformState(SourceModel.Expr name, SourceModel.Expr currentFunctionName, SourceModel.Expr recursiveFunctions, SourceModel.Expr topLevelBoundNames, SourceModel.Expr inContextBoundNames, SourceModel.Expr nameToTypeList, SourceModel.Expr debugPath, SourceModel.Expr typeConstants, boolean traverseCoreFunctions, SourceModel.Expr safeForInliningVars, SourceModel.Expr fusionContext, SourceModel.Expr knownForms, SourceModel.Expr knownToNotBeForms, SourceModel.Expr parentExpr, SourceModel.Expr grandParentExpr, SourceModel.Expr inlinedContext, SourceModel.Expr knownToBeWHNF, SourceModel.Expr alreadySeqed, SourceModel.Expr nonCalFunctions, SourceModel.Expr inlinableState) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.TransformState), name, currentFunctionName, recursiveFunctions, topLevelBoundNames, inContextBoundNames, nameToTypeList, debugPath, typeConstants, SourceModel.Expr.makeBooleanValue(traverseCoreFunctions), safeForInliningVars, fusionContext, knownForms, knownToNotBeForms, parentExpr, grandParentExpr, inlinedContext, knownToBeWHNF, alreadySeqed, nonCalFunctions, inlinableState});
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_State.TransformState.
		 * @see #TransformState(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName TransformState = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"TransformState");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_State.TransformState.
		 * @see #TransformState(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int TransformState_ordinal = 0;

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Internal.Optimizer_State module.
	 */
	public static final class Functions {
		/**
		 * Take the given list of seq'ed expression and the given inner expression and build an expression of 
		 * the form (seq e1 (seq e2 (... )))
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param seqedExprs (CAL type: <code>[Cal.Internal.Optimizer_Expression.Expression]</code>)
		 * @param innerExpr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>) 
		 */
		public static final SourceModel.Expr buildSeq2(SourceModel.Expr state, SourceModel.Expr seqedExprs, SourceModel.Expr innerExpr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.buildSeq2), state, seqedExprs, innerExpr});
		}

		/**
		 * Name binding for function: buildSeq2.
		 * @see #buildSeq2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName buildSeq2 = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"buildSeq2");

		/**
		 * Helper binding method for function: buildSeq3. 
		 * @param state
		 * @param seqedExprs
		 * @return the SourceModule.expr representing an application of buildSeq3
		 */
		public static final SourceModel.Expr buildSeq3(SourceModel.Expr state, SourceModel.Expr seqedExprs) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.buildSeq3), state, seqedExprs});
		}

		/**
		 * Name binding for function: buildSeq3.
		 * @see #buildSeq3(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName buildSeq3 = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"buildSeq3");

		/**
		 * Helper binding method for function: compareNameToType. 
		 * @param qnt1
		 * @param qnt2
		 * @return the SourceModule.expr representing an application of compareNameToType
		 */
		public static final SourceModel.Expr compareNameToType(SourceModel.Expr qnt1, SourceModel.Expr qnt2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.compareNameToType), qnt1, qnt2});
		}

		/**
		 * Name binding for function: compareNameToType.
		 * @see #compareNameToType(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName compareNameToType = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"compareNameToType");

		/**
		 * Helper binding method for function: convertAlt. 
		 * @param state
		 * @param history
		 * @param alt
		 * @return the SourceModule.expr representing an application of convertAlt
		 */
		public static final SourceModel.Expr convertAlt(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr alt) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.convertAlt), state, history, alt});
		}

		/**
		 * Name binding for function: convertAlt.
		 * @see #convertAlt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName convertAlt = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"convertAlt");

		/**
		 * Helper binding method for function: convertAlts. 
		 * @param state
		 * @param history
		 * @param wasChanged
		 * @param alts
		 * @return the SourceModule.expr representing an application of convertAlts
		 */
		public static final SourceModel.Expr convertAlts(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr wasChanged, SourceModel.Expr alts) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.convertAlts), state, history, wasChanged, alts});
		}

		/**
		 * @see #convertAlts(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param state
		 * @param history
		 * @param wasChanged
		 * @param alts
		 * @return the SourceModel.Expr representing an application of convertAlts
		 */
		public static final SourceModel.Expr convertAlts(SourceModel.Expr state, SourceModel.Expr history, boolean wasChanged, SourceModel.Expr alts) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.convertAlts), state, history, SourceModel.Expr.makeBooleanValue(wasChanged), alts});
		}

		/**
		 * Name binding for function: convertAlts.
		 * @see #convertAlts(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName convertAlts = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"convertAlts");

		/**
		 * Helper binding method for function: convertQualifiedName. 
		 * @param state
		 * @param history
		 * @param qn
		 * @return the SourceModule.expr representing an application of convertQualifiedName
		 */
		public static final SourceModel.Expr convertQualifiedName(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr qn) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.convertQualifiedName), state, history, qn});
		}

		/**
		 * Name binding for function: convertQualifiedName.
		 * @see #convertQualifiedName(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName convertQualifiedName = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"convertQualifiedName");

		/**
		 * Converts the variables named "_" in case expression to unique names.
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>(Cal.Internal.Optimizer_State.TransformHistory, Cal.Core.Prelude.Boolean, Cal.Internal.Optimizer_Expression.Expression)</code>) 
		 */
		public static final SourceModel.Expr convertToUniqueName(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.convertToUniqueName), state, history, expr});
		}

		/**
		 * Name binding for function: convertToUniqueName.
		 * @see #convertToUniqueName(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName convertToUniqueName = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"convertToUniqueName");

		/**
		 * Helper binding method for function: convertVar. 
		 * @param state
		 * @param history
		 * @param var
		 * @return the SourceModule.expr representing an application of convertVar
		 */
		public static final SourceModel.Expr convertVar(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr var) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.convertVar), state, history, var});
		}

		/**
		 * Name binding for function: convertVar.
		 * @see #convertVar(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName convertVar = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"convertVar");

		/**
		 * Helper binding method for function: convertVars. 
		 * @param state
		 * @param history
		 * @param vars
		 * @param wasChanged
		 * @return the SourceModule.expr representing an application of convertVars
		 */
		public static final SourceModel.Expr convertVars(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr vars, SourceModel.Expr wasChanged) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.convertVars), state, history, vars, wasChanged});
		}

		/**
		 * @see #convertVars(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param state
		 * @param history
		 * @param vars
		 * @param wasChanged
		 * @return the SourceModel.Expr representing an application of convertVars
		 */
		public static final SourceModel.Expr convertVars(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr vars, boolean wasChanged) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.convertVars), state, history, vars, SourceModel.Expr.makeBooleanValue(wasChanged)});
		}

		/**
		 * Name binding for function: convertVars.
		 * @see #convertVars(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName convertVars = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"convertVars");

		/**
		 * Helper binding method for function: dp. 
		 * @param ts
		 * @param cp
		 * @return the SourceModule.expr representing an application of dp
		 */
		public static final SourceModel.Expr dp(SourceModel.Expr ts, SourceModel.Expr cp) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.dp), ts, cp});
		}

		/**
		 * Name binding for function: dp.
		 * @see #dp(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName dp = 
			QualifiedName.make(CAL_Optimizer_State_internal.MODULE_NAME, "dp");

		/**
		 * Could have side effect when evaluated to WHNF.
		 * @param appsAreAllowed (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr expression_couldHaveSideEffects(SourceModel.Expr appsAreAllowed, SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_couldHaveSideEffects), appsAreAllowed, state, history, expr});
		}

		/**
		 * @see #expression_couldHaveSideEffects(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param appsAreAllowed
		 * @param state
		 * @param history
		 * @param expr
		 * @return the SourceModel.Expr representing an application of expression_couldHaveSideEffects
		 */
		public static final SourceModel.Expr expression_couldHaveSideEffects(boolean appsAreAllowed, SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_couldHaveSideEffects), SourceModel.Expr.makeBooleanValue(appsAreAllowed), state, history, expr});
		}

		/**
		 * Name binding for function: expression_couldHaveSideEffects.
		 * @see #expression_couldHaveSideEffects(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expression_couldHaveSideEffects = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"expression_couldHaveSideEffects");

		/**
		 * Return the type of the given expression if possible.
		 * <p>
		 * TODO This does not do everything but enough for specialization.
		 * TODO This does not update the type information in the state during traversal.
		 * 
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Internal.Optimizer_Type.Type</code>) 
		 */
		public static final SourceModel.Expr expression_getType(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_getType), state, history, expr});
		}

		/**
		 * Name binding for function: expression_getType.
		 * @see #expression_getType(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expression_getType = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"expression_getType");

		/**
		 * Is the given expression an unsatured lambda expression.
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 *          The context of the current transformation.
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 *          The history of the current transformation.
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The expression to check if it is an unsaturated lambda expression.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          True iff the expression is an unsatured lambda expression.
		 */
		public static final SourceModel.Expr expression_isUnsaturatedExpression(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_isUnsaturatedExpression), state, history, expr});
		}

		/**
		 * Name binding for function: expression_isUnsaturatedExpression.
		 * @see #expression_isUnsaturatedExpression(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expression_isUnsaturatedExpression = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"expression_isUnsaturatedExpression");

		/**
		 * Check if case expression can be inlined.
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.InlinableState</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr inlinableState_canInlineCaseExpression(SourceModel.Expr state) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inlinableState_canInlineCaseExpression), state});
		}

		/**
		 * Name binding for function: inlinableState_canInlineCaseExpression.
		 * @see #inlinableState_canInlineCaseExpression(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName inlinableState_canInlineCaseExpression = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"inlinableState_canInlineCaseExpression");

		/**
		 * Case expression can only be inlined at the top level or in the case alternative body.
		 * @param ts (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.InlinableState</code>)
		 * @return (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>) 
		 */
		public static final SourceModel.Expr inlinableState_set(SourceModel.Expr ts, SourceModel.Expr state) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inlinableState_set), ts, state});
		}

		/**
		 * Name binding for function: inlinableState_set.
		 * @see #inlinableState_set(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName inlinableState_set = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"inlinableState_set");

		/**
		 * Case expression can only be inlined at the top level or in the case alternative body.
		 * @param ts (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param transition (CAL type: <code>Cal.Internal.Optimizer_State.InlinableTransition</code>)
		 * @return (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>) 
		 */
		public static final SourceModel.Expr inlinableState_transition(SourceModel.Expr ts, SourceModel.Expr transition) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inlinableState_transition), ts, transition});
		}

		/**
		 * Name binding for function: inlinableState_transition.
		 * @see #inlinableState_transition(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName inlinableState_transition = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"inlinableState_transition");

		/**
		 * Update the inlinableness of case expression state
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.InlinableState</code>)
		 * @param transition (CAL type: <code>Cal.Internal.Optimizer_State.InlinableTransition</code>)
		 * @return (CAL type: <code>Cal.Internal.Optimizer_State.InlinableState</code>) 
		 */
		public static final SourceModel.Expr inlinableState_update(SourceModel.Expr state, SourceModel.Expr transition) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inlinableState_update), state, transition});
		}

		/**
		 * Name binding for function: inlinableState_update.
		 * @see #inlinableState_update(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName inlinableState_update = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"inlinableState_update");

		/**
		 * Helper binding method for function: literal_getBooleanType. 
		 * @param jPreludeTypeConstants
		 * @return the SourceModule.expr representing an application of literal_getBooleanType
		 */
		public static final SourceModel.Expr literal_getBooleanType(SourceModel.Expr jPreludeTypeConstants) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.literal_getBooleanType), jPreludeTypeConstants});
		}

		/**
		 * Name binding for function: literal_getBooleanType.
		 * @see #literal_getBooleanType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName literal_getBooleanType = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"literal_getBooleanType");

		/**
		 * Helper binding method for function: literal_getByteType. 
		 * @param jPreludeTypeConstants
		 * @return the SourceModule.expr representing an application of literal_getByteType
		 */
		public static final SourceModel.Expr literal_getByteType(SourceModel.Expr jPreludeTypeConstants) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.literal_getByteType), jPreludeTypeConstants});
		}

		/**
		 * Name binding for function: literal_getByteType.
		 * @see #literal_getByteType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName literal_getByteType = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"literal_getByteType");

		/**
		 * Helper binding method for function: literal_getCharType. 
		 * @param jPreludeTypeConstants
		 * @return the SourceModule.expr representing an application of literal_getCharType
		 */
		public static final SourceModel.Expr literal_getCharType(SourceModel.Expr jPreludeTypeConstants) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.literal_getCharType), jPreludeTypeConstants});
		}

		/**
		 * Name binding for function: literal_getCharType.
		 * @see #literal_getCharType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName literal_getCharType = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"literal_getCharType");

		/**
		 * Helper binding method for function: literal_getDoubleType. 
		 * @param jPreludeTypeConstants
		 * @return the SourceModule.expr representing an application of literal_getDoubleType
		 */
		public static final SourceModel.Expr literal_getDoubleType(SourceModel.Expr jPreludeTypeConstants) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.literal_getDoubleType), jPreludeTypeConstants});
		}

		/**
		 * Name binding for function: literal_getDoubleType.
		 * @see #literal_getDoubleType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName literal_getDoubleType = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"literal_getDoubleType");

		/**
		 * Helper binding method for function: literal_getFloatType. 
		 * @param jPreludeTypeConstants
		 * @return the SourceModule.expr representing an application of literal_getFloatType
		 */
		public static final SourceModel.Expr literal_getFloatType(SourceModel.Expr jPreludeTypeConstants) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.literal_getFloatType), jPreludeTypeConstants});
		}

		/**
		 * Name binding for function: literal_getFloatType.
		 * @see #literal_getFloatType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName literal_getFloatType = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"literal_getFloatType");

		/**
		 * Helper binding method for function: literal_getIntType. 
		 * @param jPreludeTypeConstants
		 * @return the SourceModule.expr representing an application of literal_getIntType
		 */
		public static final SourceModel.Expr literal_getIntType(SourceModel.Expr jPreludeTypeConstants) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.literal_getIntType), jPreludeTypeConstants});
		}

		/**
		 * Name binding for function: literal_getIntType.
		 * @see #literal_getIntType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName literal_getIntType = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"literal_getIntType");

		/**
		 * Helper binding method for function: literal_getIntegerType. 
		 * @param jPreludeTypeConstants
		 * @return the SourceModule.expr representing an application of literal_getIntegerType
		 */
		public static final SourceModel.Expr literal_getIntegerType(SourceModel.Expr jPreludeTypeConstants) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.literal_getIntegerType), jPreludeTypeConstants});
		}

		/**
		 * Name binding for function: literal_getIntegerType.
		 * @see #literal_getIntegerType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName literal_getIntegerType = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"literal_getIntegerType");

		/**
		 * Helper binding method for function: literal_getLongType. 
		 * @param jPreludeTypeConstants
		 * @return the SourceModule.expr representing an application of literal_getLongType
		 */
		public static final SourceModel.Expr literal_getLongType(SourceModel.Expr jPreludeTypeConstants) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.literal_getLongType), jPreludeTypeConstants});
		}

		/**
		 * Name binding for function: literal_getLongType.
		 * @see #literal_getLongType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName literal_getLongType = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"literal_getLongType");

		/**
		 * Helper binding method for function: literal_getShortType. 
		 * @param jPreludeTypeConstants
		 * @return the SourceModule.expr representing an application of literal_getShortType
		 */
		public static final SourceModel.Expr literal_getShortType(SourceModel.Expr jPreludeTypeConstants) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.literal_getShortType), jPreludeTypeConstants});
		}

		/**
		 * Name binding for function: literal_getShortType.
		 * @see #literal_getShortType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName literal_getShortType = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"literal_getShortType");

		/**
		 * Helper binding method for function: literal_getStringType. 
		 * @param jPreludeTypeConstants
		 * @return the SourceModule.expr representing an application of literal_getStringType
		 */
		public static final SourceModel.Expr literal_getStringType(SourceModel.Expr jPreludeTypeConstants) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.literal_getStringType), jPreludeTypeConstants});
		}

		/**
		 * Name binding for function: literal_getStringType.
		 * @see #literal_getStringType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName literal_getStringType = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"literal_getStringType");

		/**
		 * Maybe returns the type of the given literal.
		 * @param jPreludeTypeConstants (CAL type: <code>Cal.Internal.Optimizer_State.JPreludeTypeConstants</code>)
		 *          The PreludeTypeConstants argument to use to get CAL type objects.
		 * @param literal (CAL type: <code>Cal.Internal.Optimizer_Expression.Literal</code>)
		 *          The listeral to get the type of.
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Internal.Optimizer_Type.Type</code>) 
		 *          Maybe the type of the given literal.
		 */
		public static final SourceModel.Expr literal_getType(SourceModel.Expr jPreludeTypeConstants, SourceModel.Expr literal) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.literal_getType), jPreludeTypeConstants, literal});
		}

		/**
		 * Name binding for function: literal_getType.
		 * @see #literal_getType(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName literal_getType = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"literal_getType");

		/**
		 * Generates a unique variable name from the given name.
		 * @param ts (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 *          The context of the current expression.
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 *          The history of the current transformation.
		 * @param oldVar (CAL type: <code>Cal.Internal.Optimizer_Expression.QualifiedName</code>)
		 *          The name of the variable to use to generate the new variable name
		 * @param extraNameBits (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          String that will be included as part of the name.
		 * @return (CAL type: <code>(Cal.Internal.Optimizer_State.TransformHistory, Cal.Internal.Optimizer_Expression.QualifiedName)</code>) 
		 *          A new variable name that is unique and generated off the old variable name.
		 */
		public static final SourceModel.Expr newVar(SourceModel.Expr ts, SourceModel.Expr history, SourceModel.Expr oldVar, SourceModel.Expr extraNameBits) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.newVar), ts, history, oldVar, extraNameBits});
		}

		/**
		 * @see #newVar(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param ts
		 * @param history
		 * @param oldVar
		 * @param extraNameBits
		 * @return the SourceModel.Expr representing an application of newVar
		 */
		public static final SourceModel.Expr newVar(SourceModel.Expr ts, SourceModel.Expr history, SourceModel.Expr oldVar, java.lang.String extraNameBits) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.newVar), ts, history, oldVar, SourceModel.Expr.makeStringValue(extraNameBits)});
		}

		/**
		 * Name binding for function: newVar.
		 * @see #newVar(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName newVar = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"newVar");

		/**
		 * Helper binding method for function: preludeTypeConstants_getBooleanType. 
		 * @param jPreludeTypeConstants
		 * @return the SourceModule.expr representing an application of preludeTypeConstants_getBooleanType
		 */
		public static final SourceModel.Expr preludeTypeConstants_getBooleanType(SourceModel.Expr jPreludeTypeConstants) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.preludeTypeConstants_getBooleanType), jPreludeTypeConstants});
		}

		/**
		 * Name binding for function: preludeTypeConstants_getBooleanType.
		 * @see #preludeTypeConstants_getBooleanType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName preludeTypeConstants_getBooleanType = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"preludeTypeConstants_getBooleanType");

		/**
		 * Helper binding method for function: preludeTypeConstants_getByteType. 
		 * @param jPreludeTypeConstants
		 * @return the SourceModule.expr representing an application of preludeTypeConstants_getByteType
		 */
		public static final SourceModel.Expr preludeTypeConstants_getByteType(SourceModel.Expr jPreludeTypeConstants) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.preludeTypeConstants_getByteType), jPreludeTypeConstants});
		}

		/**
		 * Name binding for function: preludeTypeConstants_getByteType.
		 * @see #preludeTypeConstants_getByteType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName preludeTypeConstants_getByteType = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"preludeTypeConstants_getByteType");

		/**
		 * Helper binding method for function: preludeTypeConstants_getCharType. 
		 * @param jPreludeTypeConstants
		 * @return the SourceModule.expr representing an application of preludeTypeConstants_getCharType
		 */
		public static final SourceModel.Expr preludeTypeConstants_getCharType(SourceModel.Expr jPreludeTypeConstants) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.preludeTypeConstants_getCharType), jPreludeTypeConstants});
		}

		/**
		 * Name binding for function: preludeTypeConstants_getCharType.
		 * @see #preludeTypeConstants_getCharType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName preludeTypeConstants_getCharType = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"preludeTypeConstants_getCharType");

		/**
		 * Helper binding method for function: preludeTypeConstants_getDoubleType. 
		 * @param jPreludeTypeConstants
		 * @return the SourceModule.expr representing an application of preludeTypeConstants_getDoubleType
		 */
		public static final SourceModel.Expr preludeTypeConstants_getDoubleType(SourceModel.Expr jPreludeTypeConstants) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.preludeTypeConstants_getDoubleType), jPreludeTypeConstants});
		}

		/**
		 * Name binding for function: preludeTypeConstants_getDoubleType.
		 * @see #preludeTypeConstants_getDoubleType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName preludeTypeConstants_getDoubleType = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"preludeTypeConstants_getDoubleType");

		/**
		 * Helper binding method for function: preludeTypeConstants_getFloatType. 
		 * @param jPreludeTypeConstants
		 * @return the SourceModule.expr representing an application of preludeTypeConstants_getFloatType
		 */
		public static final SourceModel.Expr preludeTypeConstants_getFloatType(SourceModel.Expr jPreludeTypeConstants) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.preludeTypeConstants_getFloatType), jPreludeTypeConstants});
		}

		/**
		 * Name binding for function: preludeTypeConstants_getFloatType.
		 * @see #preludeTypeConstants_getFloatType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName preludeTypeConstants_getFloatType = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"preludeTypeConstants_getFloatType");

		/**
		 * Helper binding method for function: preludeTypeConstants_getIntType. 
		 * @param jPreludeTypeConstants
		 * @return the SourceModule.expr representing an application of preludeTypeConstants_getIntType
		 */
		public static final SourceModel.Expr preludeTypeConstants_getIntType(SourceModel.Expr jPreludeTypeConstants) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.preludeTypeConstants_getIntType), jPreludeTypeConstants});
		}

		/**
		 * Name binding for function: preludeTypeConstants_getIntType.
		 * @see #preludeTypeConstants_getIntType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName preludeTypeConstants_getIntType = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"preludeTypeConstants_getIntType");

		/**
		 * Helper binding method for function: preludeTypeConstants_getIntegerType. 
		 * @param jPreludeTypeConstants
		 * @return the SourceModule.expr representing an application of preludeTypeConstants_getIntegerType
		 */
		public static final SourceModel.Expr preludeTypeConstants_getIntegerType(SourceModel.Expr jPreludeTypeConstants) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.preludeTypeConstants_getIntegerType), jPreludeTypeConstants});
		}

		/**
		 * Name binding for function: preludeTypeConstants_getIntegerType.
		 * @see #preludeTypeConstants_getIntegerType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName preludeTypeConstants_getIntegerType = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"preludeTypeConstants_getIntegerType");

		/**
		 * Helper binding method for function: preludeTypeConstants_getLongType. 
		 * @param jPreludeTypeConstants
		 * @return the SourceModule.expr representing an application of preludeTypeConstants_getLongType
		 */
		public static final SourceModel.Expr preludeTypeConstants_getLongType(SourceModel.Expr jPreludeTypeConstants) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.preludeTypeConstants_getLongType), jPreludeTypeConstants});
		}

		/**
		 * Name binding for function: preludeTypeConstants_getLongType.
		 * @see #preludeTypeConstants_getLongType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName preludeTypeConstants_getLongType = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"preludeTypeConstants_getLongType");

		/**
		 * Helper binding method for function: preludeTypeConstants_getShortType. 
		 * @param jPreludeTypeConstants
		 * @return the SourceModule.expr representing an application of preludeTypeConstants_getShortType
		 */
		public static final SourceModel.Expr preludeTypeConstants_getShortType(SourceModel.Expr jPreludeTypeConstants) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.preludeTypeConstants_getShortType), jPreludeTypeConstants});
		}

		/**
		 * Name binding for function: preludeTypeConstants_getShortType.
		 * @see #preludeTypeConstants_getShortType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName preludeTypeConstants_getShortType = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"preludeTypeConstants_getShortType");

		/**
		 * Helper binding method for function: preludeTypeConstants_getStringType. 
		 * @param jPreludeTypeConstants
		 * @return the SourceModule.expr representing an application of preludeTypeConstants_getStringType
		 */
		public static final SourceModel.Expr preludeTypeConstants_getStringType(SourceModel.Expr jPreludeTypeConstants) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.preludeTypeConstants_getStringType), jPreludeTypeConstants});
		}

		/**
		 * Name binding for function: preludeTypeConstants_getStringType.
		 * @see #preludeTypeConstants_getStringType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName preludeTypeConstants_getStringType = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"preludeTypeConstants_getStringType");

		/**
		 * Helper binding method for function: prelude_seq_expr. 
		 * @param state
		 * @return the SourceModule.expr representing an application of prelude_seq_expr
		 */
		public static final SourceModel.Expr prelude_seq_expr(SourceModel.Expr state) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.prelude_seq_expr), state});
		}

		/**
		 * Name binding for function: prelude_seq_expr.
		 * @see #prelude_seq_expr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName prelude_seq_expr = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"prelude_seq_expr");

		/**
		 * Look for the type of the given symbol in the state and history.
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 *          The context of the current traversal
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 *          Information from the history of the current traversal.
		 * @param name (CAL type: <code>Cal.Internal.Optimizer_Expression.QualifiedName</code>)
		 *          The name to find the type for.
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Internal.Optimizer_Type.Type</code>) 
		 *          Maybe the type of the symbols if available.
		 */
		public static final SourceModel.Expr qualifiedName_getType(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr name) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.qualifiedName_getType), state, history, name});
		}

		/**
		 * Name binding for function: qualifiedName_getType.
		 * @see #qualifiedName_getType(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName qualifiedName_getType = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"qualifiedName_getType");

		/**
		 * Helper binding method for function: showFusionContext. 
		 * @param fc
		 * @return the SourceModule.expr representing an application of showFusionContext
		 */
		public static final SourceModel.Expr showFusionContext(SourceModel.Expr fc) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showFusionContext), fc});
		}

		/**
		 * Name binding for function: showFusionContext.
		 * @see #showFusionContext(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showFusionContext = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"showFusionContext");

		/**
		 * Helper binding method for function: showTransformState. 
		 * @param state
		 * @return the SourceModule.expr representing an application of showTransformState
		 */
		public static final SourceModel.Expr showTransformState(SourceModel.Expr state) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showTransformState), state});
		}

		/**
		 * Name binding for function: showTransformState.
		 * @see #showTransformState(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showTransformState = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"showTransformState");

		/**
		 * Replace the given variable with the given expression in the main expression. This function
		 * will notice if the variable is redefined and not perform the replacement under such contexts.
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 *          The context of the current expression.
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 *          The history of the current transformation.
		 * @param var (CAL type: <code>Cal.Internal.Optimizer_Expression.QualifiedName</code>)
		 *          The variable to replace.
		 * @param varExpr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The value to replace Var with.
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The expression to perform the substitution in.
		 * @return (CAL type: <code>(Cal.Internal.Optimizer_State.TransformHistory, Cal.Internal.Optimizer_Expression.Expression)</code>) 
		 *          The expression to replace the variable in.
		 */
		public static final SourceModel.Expr substitute(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr var, SourceModel.Expr varExpr, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.substitute), state, history, var, varExpr, expr});
		}

		/**
		 * Name binding for function: substitute.
		 * @see #substitute(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName substitute = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"substitute");

		/**
		 * Helper binding method for function: substituteHelper. 
		 * @param state
		 * @param history
		 * @param var
		 * @param varExpr
		 * @param expr
		 * @return the SourceModule.expr representing an application of substituteHelper
		 */
		public static final SourceModel.Expr substituteHelper(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr var, SourceModel.Expr varExpr, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.substituteHelper), state, history, var, varExpr, expr});
		}

		/**
		 * Name binding for function: substituteHelper.
		 * @see #substituteHelper(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName substituteHelper = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"substituteHelper");

		/**
		 * Helper function to do a substitute on the given extension field.
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 * @param var (CAL type: <code>Cal.Internal.Optimizer_Expression.QualifiedName</code>)
		 * @param varExpr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @param ef (CAL type: <code>(Cal.Internal.Optimizer_Expression.FieldName, Cal.Internal.Optimizer_Expression.Expression)</code>)
		 * @return (CAL type: <code>(Cal.Internal.Optimizer_State.TransformHistory, Cal.Core.Prelude.Boolean, (Cal.Internal.Optimizer_Expression.FieldName, Cal.Internal.Optimizer_Expression.Expression))</code>) 
		 */
		public static final SourceModel.Expr substitute_extensionField(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr var, SourceModel.Expr varExpr, SourceModel.Expr ef) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.substitute_extensionField), state, history, var, varExpr, ef});
		}

		/**
		 * Name binding for function: substitute_extensionField.
		 * @see #substitute_extensionField(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName substitute_extensionField = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"substitute_extensionField");

		/**
		 * Helper function to do a substitute on the given list of extension fields.
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 * @param var (CAL type: <code>Cal.Internal.Optimizer_Expression.QualifiedName</code>)
		 * @param varExpr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @param extensionFieldsMap (CAL type: <code>[(Cal.Internal.Optimizer_Expression.FieldName, Cal.Internal.Optimizer_Expression.Expression)]</code>)
		 * @return (CAL type: <code>(Cal.Internal.Optimizer_State.TransformHistory, Cal.Core.Prelude.Boolean, [(Cal.Internal.Optimizer_Expression.FieldName, Cal.Internal.Optimizer_Expression.Expression)])</code>) 
		 */
		public static final SourceModel.Expr substitute_extensionFieldsMap(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr var, SourceModel.Expr varExpr, SourceModel.Expr extensionFieldsMap) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.substitute_extensionFieldsMap), state, history, var, varExpr, extensionFieldsMap});
		}

		/**
		 * Name binding for function: substitute_extensionFieldsMap.
		 * @see #substitute_extensionFieldsMap(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName substitute_extensionFieldsMap = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"substitute_extensionFieldsMap");

		/**
		 * Helper binding method for function: system_currentTimeMillis. 
		 * @return the SourceModule.expr representing an application of system_currentTimeMillis
		 */
		public static final SourceModel.Expr system_currentTimeMillis() {
			return SourceModel.Expr.Var.make(Functions.system_currentTimeMillis);
		}

		/**
		 * Name binding for function: system_currentTimeMillis.
		 * @see #system_currentTimeMillis()
		 */
		public static final QualifiedName system_currentTimeMillis = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"system_currentTimeMillis");

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
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"trace2");

		/**
		 * Helper binding method for function: transformHistory_addCoreFunction. 
		 * @param history
		 * @param cf
		 * @return the SourceModule.expr representing an application of transformHistory_addCoreFunction
		 */
		public static final SourceModel.Expr transformHistory_addCoreFunction(SourceModel.Expr history, SourceModel.Expr cf) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformHistory_addCoreFunction), history, cf});
		}

		/**
		 * Name binding for function: transformHistory_addCoreFunction.
		 * @see #transformHistory_addCoreFunction(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformHistory_addCoreFunction = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformHistory_addCoreFunction");

		/**
		 * Add the given function to the list of recursive functions.
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 * @return (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>) 
		 */
		public static final SourceModel.Expr transformHistory_addRecursiveFunction(SourceModel.Expr history) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformHistory_addRecursiveFunction), history});
		}

		/**
		 * Name binding for function: transformHistory_addRecursiveFunction.
		 * @see #transformHistory_addRecursiveFunction(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformHistory_addRecursiveFunction = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformHistory_addRecursiveFunction");

		/**
		 * Check if the fusion between the given functions had been attempted before and failed.
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 *          The transformation history.
		 * @param f1 (CAL type: <code>Cal.Internal.Optimizer_Expression.QualifiedName</code>)
		 *          The name of one of the functions used by the fusion attempt.
		 * @param f2 (CAL type: <code>Cal.Internal.Optimizer_Expression.QualifiedName</code>)
		 *          The name of one of the functions used by the fusion attempt.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          True if the fusion of f1 and f2 had failed before.
		 */
		public static final SourceModel.Expr transformHistory_didFusionFail(SourceModel.Expr history, SourceModel.Expr f1, SourceModel.Expr f2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformHistory_didFusionFail), history, f1, f2});
		}

		/**
		 * Name binding for function: transformHistory_didFusionFail.
		 * @see #transformHistory_didFusionFail(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformHistory_didFusionFail = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformHistory_didFusionFail");

		/**
		 * Lookup the type of the name of the function if present.
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 *          The history of the transformation.
		 * @param name (CAL type: <code>Cal.Internal.Optimizer_Expression.QualifiedName</code>)
		 *          The name to get the type of.
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe [Cal.Internal.Optimizer_Type.Type]</code>) 
		 */
		public static final SourceModel.Expr transformHistory_findType(SourceModel.Expr history, SourceModel.Expr name) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformHistory_findType), history, name});
		}

		/**
		 * Name binding for function: transformHistory_findType.
		 * @see #transformHistory_findType(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformHistory_findType = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformHistory_findType");

		/**
		 * Add a function to the list of fusions that failed.
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 *          The history of the transformation attempt
		 * @param f1 (CAL type: <code>Cal.Internal.Optimizer_Expression.QualifiedName</code>)
		 *          The name of one of the functions used by the failed fusion attempt.
		 * @param f2 (CAL type: <code>Cal.Internal.Optimizer_Expression.QualifiedName</code>)
		 *          The name of one of the functions used by the failed fusion attempt.
		 * @return (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>) 
		 *          The new history with information about the failed attemp.
		 */
		public static final SourceModel.Expr transformHistory_fusionFailed(SourceModel.Expr history, SourceModel.Expr f1, SourceModel.Expr f2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformHistory_fusionFailed), history, f1, f2});
		}

		/**
		 * Name binding for function: transformHistory_fusionFailed.
		 * @see #transformHistory_fusionFailed(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformHistory_fusionFailed = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformHistory_fusionFailed");

		/**
		 * Get the current value of the identifier counter.
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 *          The transformation history.
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          The current value of the indentifier counter.
		 */
		public static final SourceModel.Expr transformHistory_getCounter(SourceModel.Expr history) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformHistory_getCounter), history});
		}

		/**
		 * Name binding for function: transformHistory_getCounter.
		 * @see #transformHistory_getCounter(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformHistory_getCounter = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformHistory_getCounter");

		/**
		 * Return the type of the given name if present.
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 *          The history of the current transformation.
		 * @param name (CAL type: <code>Cal.Internal.Optimizer_Expression.QualifiedName</code>)
		 *          The name of the function to get the type of
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Internal.Optimizer_Type.Type</code>) 
		 *          Maybe the type of the given name.
		 */
		public static final SourceModel.Expr transformHistory_getType(SourceModel.Expr history, SourceModel.Expr name) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformHistory_getType), history, name});
		}

		/**
		 * Name binding for function: transformHistory_getType.
		 * @see #transformHistory_getType(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformHistory_getType = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformHistory_getType");

		/**
		 * Increment the identifier counter
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 *          The transform history to increment the identified counter in.
		 * @return (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>) 
		 *          The new history with incremented counter.
		 */
		public static final SourceModel.Expr transformHistory_incrementCounter(SourceModel.Expr history) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformHistory_incrementCounter), history});
		}

		/**
		 * Name binding for function: transformHistory_incrementCounter.
		 * @see #transformHistory_incrementCounter(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformHistory_incrementCounter = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformHistory_incrementCounter");

		/**
		 * Increment the iteration counter
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 *          The transform history to increment the identified counter in.
		 * @return (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>) 
		 *          The new history with incremented counter.
		 */
		public static final SourceModel.Expr transformHistory_incrementCurrentIteration(SourceModel.Expr history) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformHistory_incrementCurrentIteration), history});
		}

		/**
		 * Name binding for function: transformHistory_incrementCurrentIteration.
		 * @see #transformHistory_incrementCurrentIteration(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformHistory_incrementCurrentIteration = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformHistory_incrementCurrentIteration");

		/**
		 * Helper binding method for function: transformHistory_init. 
		 * @return the SourceModule.expr representing an application of transformHistory_init
		 */
		public static final SourceModel.Expr transformHistory_init() {
			return SourceModel.Expr.Var.make(Functions.transformHistory_init);
		}

		/**
		 * Name binding for function: transformHistory_init.
		 * @see #transformHistory_init()
		 */
		public static final QualifiedName transformHistory_init = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformHistory_init");

		/**
		 * Helper binding method for function: transformHistory_isLiftedLetVar. 
		 * @param history
		 * @param var
		 * @return the SourceModule.expr representing an application of transformHistory_isLiftedLetVar
		 */
		public static final SourceModel.Expr transformHistory_isLiftedLetVar(SourceModel.Expr history, SourceModel.Expr var) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformHistory_isLiftedLetVar), history, var});
		}

		/**
		 * Name binding for function: transformHistory_isLiftedLetVar.
		 * @see #transformHistory_isLiftedLetVar(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformHistory_isLiftedLetVar = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformHistory_isLiftedLetVar");

		/**
		 * Helper binding method for function: transformHistory_setCoreFunction. 
		 * @param history
		 * @param cfs
		 * @return the SourceModule.expr representing an application of transformHistory_setCoreFunction
		 */
		public static final SourceModel.Expr transformHistory_setCoreFunction(SourceModel.Expr history, SourceModel.Expr cfs) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformHistory_setCoreFunction), history, cfs});
		}

		/**
		 * Name binding for function: transformHistory_setCoreFunction.
		 * @see #transformHistory_setCoreFunction(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformHistory_setCoreFunction = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformHistory_setCoreFunction");

		/**
		 * Set the iteration counter
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 *          The transform history to increment the identified counter in.
		 * @param ic (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @return (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>) 
		 *          The new history with given counter.
		 */
		public static final SourceModel.Expr transformHistory_setCurrentIteration(SourceModel.Expr history, SourceModel.Expr ic) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformHistory_setCurrentIteration), history, ic});
		}

		/**
		 * @see #transformHistory_setCurrentIteration(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param history
		 * @param ic
		 * @return the SourceModel.Expr representing an application of transformHistory_setCurrentIteration
		 */
		public static final SourceModel.Expr transformHistory_setCurrentIteration(SourceModel.Expr history, int ic) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformHistory_setCurrentIteration), history, SourceModel.Expr.makeIntValue(ic)});
		}

		/**
		 * Name binding for function: transformHistory_setCurrentIteration.
		 * @see #transformHistory_setCurrentIteration(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformHistory_setCurrentIteration = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformHistory_setCurrentIteration");

		/**
		 * Set the time that the optimization starts. This is used to stop transforming after too much time
		 * has passed.
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 * @param startTime (CAL type: <code>Cal.Core.Prelude.Long</code>)
		 * @return (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>) 
		 */
		public static final SourceModel.Expr transformHistory_setStartTime(SourceModel.Expr history, SourceModel.Expr startTime) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformHistory_setStartTime), history, startTime});
		}

		/**
		 * @see #transformHistory_setStartTime(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param history
		 * @param startTime
		 * @return the SourceModel.Expr representing an application of transformHistory_setStartTime
		 */
		public static final SourceModel.Expr transformHistory_setStartTime(SourceModel.Expr history, long startTime) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformHistory_setStartTime), history, SourceModel.Expr.makeLongValue(startTime)});
		}

		/**
		 * Name binding for function: transformHistory_setStartTime.
		 * @see #transformHistory_setStartTime(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformHistory_setStartTime = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformHistory_setStartTime");

		/**
		 * Helper binding method for function: transformHistory_takingTooLong. 
		 * @param history
		 * @return the SourceModule.expr representing an application of transformHistory_takingTooLong
		 */
		public static final SourceModel.Expr transformHistory_takingTooLong(SourceModel.Expr history) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformHistory_takingTooLong), history});
		}

		/**
		 * Name binding for function: transformHistory_takingTooLong.
		 * @see #transformHistory_takingTooLong(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformHistory_takingTooLong = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformHistory_takingTooLong");

		/**
		 * Add the given function to the list of currently defined symbols
		 * @param var (CAL type: <code>Cal.Internal.Optimizer_Expression.QualifiedName</code>)
		 *          Name of the variable defined.
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The expression that defined the variable.
		 * @param type (CAL type: <code>Cal.Core.Prelude.Maybe [Cal.Internal.Optimizer_Type.Type]</code>)
		 *          The type of the expression.
		 * @param isConstArgs (CAL type: <code>[Cal.Core.Prelude.Boolean]</code>)
		 *          Flags indicating if the correspond argument of the expression is passes unchanged to recursive calls of the function.
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 *          The context of the current expression.
		 * @return (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>) 
		 *          The context with this function definition added.
		 */
		public static final SourceModel.Expr transformHistory_update(SourceModel.Expr var, SourceModel.Expr expr, SourceModel.Expr type, SourceModel.Expr isConstArgs, SourceModel.Expr history) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformHistory_update), var, expr, type, isConstArgs, history});
		}

		/**
		 * Name binding for function: transformHistory_update.
		 * @see #transformHistory_update(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformHistory_update = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformHistory_update");

		/**
		 * Helper binding method for function: transformHistory_updateLiftedLetVar. 
		 * @param history
		 * @param var
		 * @return the SourceModule.expr representing an application of transformHistory_updateLiftedLetVar
		 */
		public static final SourceModel.Expr transformHistory_updateLiftedLetVar(SourceModel.Expr history, SourceModel.Expr var) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformHistory_updateLiftedLetVar), history, var});
		}

		/**
		 * Name binding for function: transformHistory_updateLiftedLetVar.
		 * @see #transformHistory_updateLiftedLetVar(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformHistory_updateLiftedLetVar = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformHistory_updateLiftedLetVar");

		/**
		 * Helper binding method for function: transformState_addAlreadySeqed. 
		 * @param expr
		 * @param state
		 * @return the SourceModule.expr representing an application of transformState_addAlreadySeqed
		 */
		public static final SourceModel.Expr transformState_addAlreadySeqed(SourceModel.Expr expr, SourceModel.Expr state) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformState_addAlreadySeqed), expr, state});
		}

		/**
		 * Name binding for function: transformState_addAlreadySeqed.
		 * @see #transformState_addAlreadySeqed(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformState_addAlreadySeqed = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformState_addAlreadySeqed");

		/**
		 * This is for the case of going into the _ alternative.
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param name (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @param form (CAL type: <code>Cal.Internal.Optimizer_Expression.CaseConst</code>)
		 * @param vars (CAL type: <code>[(Cal.Internal.Optimizer_Expression.FieldName, Cal.Internal.Optimizer_Expression.QualifiedName, Cal.Internal.Optimizer_Type.Type)]</code>)
		 * @return (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>) 
		 */
		public static final SourceModel.Expr transformState_addKnownForm(SourceModel.Expr state, SourceModel.Expr name, SourceModel.Expr form, SourceModel.Expr vars) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformState_addKnownForm), state, name, form, vars});
		}

		/**
		 * Name binding for function: transformState_addKnownForm.
		 * @see #transformState_addKnownForm(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformState_addKnownForm = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformState_addKnownForm");

		/**
		 * Helper binding method for function: transformState_addKnownToBeWHFN. 
		 * @param expr
		 * @param state
		 * @return the SourceModule.expr representing an application of transformState_addKnownToBeWHFN
		 */
		public static final SourceModel.Expr transformState_addKnownToBeWHFN(SourceModel.Expr expr, SourceModel.Expr state) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformState_addKnownToBeWHFN), expr, state});
		}

		/**
		 * Name binding for function: transformState_addKnownToBeWHFN.
		 * @see #transformState_addKnownToBeWHFN(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformState_addKnownToBeWHFN = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformState_addKnownToBeWHFN");

		/**
		 * Helper binding method for function: transformState_addKnownToBeWHFNs. 
		 * @param exprs
		 * @param state
		 * @return the SourceModule.expr representing an application of transformState_addKnownToBeWHFNs
		 */
		public static final SourceModel.Expr transformState_addKnownToBeWHFNs(SourceModel.Expr exprs, SourceModel.Expr state) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformState_addKnownToBeWHFNs), exprs, state});
		}

		/**
		 * Name binding for function: transformState_addKnownToBeWHFNs.
		 * @see #transformState_addKnownToBeWHFNs(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformState_addKnownToBeWHFNs = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformState_addKnownToBeWHFNs");

		/**
		 * This is for the case of going into the _ alternative.
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param expression (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @param forms (CAL type: <code>[Cal.Internal.Optimizer_Expression.CaseConst]</code>)
		 * @return (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>) 
		 */
		public static final SourceModel.Expr transformState_addKnownToNotBeForm(SourceModel.Expr state, SourceModel.Expr expression, SourceModel.Expr forms) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformState_addKnownToNotBeForm), state, expression, forms});
		}

		/**
		 * Name binding for function: transformState_addKnownToNotBeForm.
		 * @see #transformState_addKnownToNotBeForm(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformState_addKnownToNotBeForm = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformState_addKnownToNotBeForm");

		/**
		 * Helper binding method for function: transformState_clearInContextBoundNames. 
		 * @param state
		 * @return the SourceModule.expr representing an application of transformState_clearInContextBoundNames
		 */
		public static final SourceModel.Expr transformState_clearInContextBoundNames(SourceModel.Expr state) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformState_clearInContextBoundNames), state});
		}

		/**
		 * Name binding for function: transformState_clearInContextBoundNames.
		 * @see #transformState_clearInContextBoundNames(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformState_clearInContextBoundNames = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformState_clearInContextBoundNames");

		/**
		 * Helper binding method for function: transformState_clearLambdaVars. 
		 * @param state
		 * @return the SourceModule.expr representing an application of transformState_clearLambdaVars
		 */
		public static final SourceModel.Expr transformState_clearLambdaVars(SourceModel.Expr state) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformState_clearLambdaVars), state});
		}

		/**
		 * Name binding for function: transformState_clearLambdaVars.
		 * @see #transformState_clearLambdaVars(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformState_clearLambdaVars = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformState_clearLambdaVars");

		/**
		 * Keeps track of the 'path' of names to the current context. This is used to generate
		 * new variable names.
		 * @param ts (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param qn (CAL type: <code>Cal.Internal.Optimizer_Expression.QualifiedName</code>)
		 * @return (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>) 
		 */
		public static final SourceModel.Expr transformState_deeper(SourceModel.Expr ts, SourceModel.Expr qn) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformState_deeper), ts, qn});
		}

		/**
		 * Name binding for function: transformState_deeper.
		 * @see #transformState_deeper(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformState_deeper = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformState_deeper");

		/**
		 * Helper binding method for function: transformState_findDef. 
		 * @param state
		 * @param history
		 * @param varName
		 * @return the SourceModule.expr representing an application of transformState_findDef
		 */
		public static final SourceModel.Expr transformState_findDef(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr varName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformState_findDef), state, history, varName});
		}

		/**
		 * Name binding for function: transformState_findDef.
		 * @see #transformState_findDef(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformState_findDef = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformState_findDef");

		/**
		 * See if the given name has a known data constructor.
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 *          The current state of the transformation.
		 * @param name (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The name of the variable to search for the data constructor of.
		 * @param value (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          A value that defines the expression.
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Internal.Optimizer_Expression.DataCons</code>) 
		 */
		public static final SourceModel.Expr transformState_findKnownDC(SourceModel.Expr state, SourceModel.Expr name, SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformState_findKnownDC), state, name, value});
		}

		/**
		 * Name binding for function: transformState_findKnownDC.
		 * @see #transformState_findKnownDC(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformState_findKnownDC = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformState_findKnownDC");

		/**
		 * Helper binding method for function: transformState_findKnownForm. 
		 * @param state
		 * @param name
		 * @return the SourceModule.expr representing an application of transformState_findKnownForm
		 */
		public static final SourceModel.Expr transformState_findKnownForm(SourceModel.Expr state, SourceModel.Expr name) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformState_findKnownForm), state, name});
		}

		/**
		 * Name binding for function: transformState_findKnownForm.
		 * @see #transformState_findKnownForm(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformState_findKnownForm = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformState_findKnownForm");

		/**
		 * Helper binding method for function: transformState_findKnownToNotBeForm. 
		 * @param state
		 * @param name
		 * @return the SourceModule.expr representing an application of transformState_findKnownToNotBeForm
		 */
		public static final SourceModel.Expr transformState_findKnownToNotBeForm(SourceModel.Expr state, SourceModel.Expr name) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformState_findKnownToNotBeForm), state, name});
		}

		/**
		 * Name binding for function: transformState_findKnownToNotBeForm.
		 * @see #transformState_findKnownToNotBeForm(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformState_findKnownToNotBeForm = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformState_findKnownToNotBeForm");

		/**
		 * Helper binding method for function: transformState_findType. 
		 * @param state
		 * @param history
		 * @param varName
		 * @return the SourceModule.expr representing an application of transformState_findType
		 */
		public static final SourceModel.Expr transformState_findType(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr varName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformState_findType), state, history, varName});
		}

		/**
		 * Name binding for function: transformState_findType.
		 * @see #transformState_findType(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformState_findType = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformState_findType");

		/**
		 * Helper binding method for function: transformState_getBoundNames. 
		 * @param state
		 * @return the SourceModule.expr representing an application of transformState_getBoundNames
		 */
		public static final SourceModel.Expr transformState_getBoundNames(SourceModel.Expr state) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformState_getBoundNames), state});
		}

		/**
		 * Name binding for function: transformState_getBoundNames.
		 * @see #transformState_getBoundNames(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformState_getBoundNames = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformState_getBoundNames");

		/**
		 * Helper binding method for function: transformState_getCurrentFunctionName. 
		 * @param state
		 * @return the SourceModule.expr representing an application of transformState_getCurrentFunctionName
		 */
		public static final SourceModel.Expr transformState_getCurrentFunctionName(SourceModel.Expr state) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformState_getCurrentFunctionName), state});
		}

		/**
		 * Name binding for function: transformState_getCurrentFunctionName.
		 * @see #transformState_getCurrentFunctionName(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformState_getCurrentFunctionName = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformState_getCurrentFunctionName");

		/**
		 * Helper binding method for function: transformState_getInContextBoundNames. 
		 * @param state
		 * @return the SourceModule.expr representing an application of transformState_getInContextBoundNames
		 */
		public static final SourceModel.Expr transformState_getInContextBoundNames(SourceModel.Expr state) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformState_getInContextBoundNames), state});
		}

		/**
		 * Name binding for function: transformState_getInContextBoundNames.
		 * @see #transformState_getInContextBoundNames(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformState_getInContextBoundNames = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformState_getInContextBoundNames");

		/**
		 * Get the current module name.
		 * @param ts (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr transformState_getModuleName(SourceModel.Expr ts) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformState_getModuleName), ts});
		}

		/**
		 * Name binding for function: transformState_getModuleName.
		 * @see #transformState_getModuleName(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformState_getModuleName = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformState_getModuleName");

		/**
		 * Helper binding method for function: transformState_getName. 
		 * @param state
		 * @return the SourceModule.expr representing an application of transformState_getName
		 */
		public static final SourceModel.Expr transformState_getName(SourceModel.Expr state) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformState_getName), state});
		}

		/**
		 * Name binding for function: transformState_getName.
		 * @see #transformState_getName(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformState_getName = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformState_getName");

		/**
		 * Helper binding method for function: transformState_getTopLevelBoundNames. 
		 * @param state
		 * @return the SourceModule.expr representing an application of transformState_getTopLevelBoundNames
		 */
		public static final SourceModel.Expr transformState_getTopLevelBoundNames(SourceModel.Expr state) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformState_getTopLevelBoundNames), state});
		}

		/**
		 * Name binding for function: transformState_getTopLevelBoundNames.
		 * @see #transformState_getTopLevelBoundNames(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformState_getTopLevelBoundNames = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformState_getTopLevelBoundNames");

		/**
		 * Helper binding method for function: transformState_getType. 
		 * @param state
		 * @param name
		 * @return the SourceModule.expr representing an application of transformState_getType
		 */
		public static final SourceModel.Expr transformState_getType(SourceModel.Expr state, SourceModel.Expr name) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformState_getType), state, name});
		}

		/**
		 * Name binding for function: transformState_getType.
		 * @see #transformState_getType(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformState_getType = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformState_getType");

		/**
		 * Helper binding method for function: transformState_init. 
		 * @param name
		 * @param typeConstants
		 * @param nameToTypeList
		 * @param topLevelBoundNames
		 * @param safeForInliningVars
		 * @param topLevelExpr
		 * @param knownToBeWHNF
		 * @param nonCalFunctions
		 * @return the SourceModule.expr representing an application of transformState_init
		 */
		public static final SourceModel.Expr transformState_init(SourceModel.Expr name, SourceModel.Expr typeConstants, SourceModel.Expr nameToTypeList, SourceModel.Expr topLevelBoundNames, SourceModel.Expr safeForInliningVars, SourceModel.Expr topLevelExpr, SourceModel.Expr knownToBeWHNF, SourceModel.Expr nonCalFunctions) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformState_init), name, typeConstants, nameToTypeList, topLevelBoundNames, safeForInliningVars, topLevelExpr, knownToBeWHNF, nonCalFunctions});
		}

		/**
		 * Name binding for function: transformState_init.
		 * @see #transformState_init(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformState_init = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformState_init");

		/**
		 * Helper binding method for function: transformState_isAlreadySeqed. 
		 * @param testExpr
		 * @param state
		 * @return the SourceModule.expr representing an application of transformState_isAlreadySeqed
		 */
		public static final SourceModel.Expr transformState_isAlreadySeqed(SourceModel.Expr testExpr, SourceModel.Expr state) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformState_isAlreadySeqed), testExpr, state});
		}

		/**
		 * Name binding for function: transformState_isAlreadySeqed.
		 * @see #transformState_isAlreadySeqed(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformState_isAlreadySeqed = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformState_isAlreadySeqed");

		/**
		 * Helper binding method for function: transformState_isBeingFused. 
		 * @param state
		 * @param name
		 * @return the SourceModule.expr representing an application of transformState_isBeingFused
		 */
		public static final SourceModel.Expr transformState_isBeingFused(SourceModel.Expr state, SourceModel.Expr name) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformState_isBeingFused), state, name});
		}

		/**
		 * Name binding for function: transformState_isBeingFused.
		 * @see #transformState_isBeingFused(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformState_isBeingFused = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformState_isBeingFused");

		/**
		 * Is the given name know to be bound.
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param name (CAL type: <code>Cal.Internal.Optimizer_Expression.QualifiedName</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr transformState_isBoundName(SourceModel.Expr state, SourceModel.Expr name) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformState_isBoundName), state, name});
		}

		/**
		 * Name binding for function: transformState_isBoundName.
		 * @see #transformState_isBoundName(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformState_isBoundName = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformState_isBoundName");

		/**
		 * Helper binding method for function: transformState_isInContextBoundNames. 
		 * @param state
		 * @param name
		 * @return the SourceModule.expr representing an application of transformState_isInContextBoundNames
		 */
		public static final SourceModel.Expr transformState_isInContextBoundNames(SourceModel.Expr state, SourceModel.Expr name) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformState_isInContextBoundNames), state, name});
		}

		/**
		 * Name binding for function: transformState_isInContextBoundNames.
		 * @see #transformState_isInContextBoundNames(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformState_isInContextBoundNames = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformState_isInContextBoundNames");

		/**
		 * Helper binding method for function: transformState_isKnownToBeWHNF. 
		 * @param testExpr
		 * @param state
		 * @return the SourceModule.expr representing an application of transformState_isKnownToBeWHNF
		 */
		public static final SourceModel.Expr transformState_isKnownToBeWHNF(SourceModel.Expr testExpr, SourceModel.Expr state) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformState_isKnownToBeWHNF), testExpr, state});
		}

		/**
		 * Name binding for function: transformState_isKnownToBeWHNF.
		 * @see #transformState_isKnownToBeWHNF(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformState_isKnownToBeWHNF = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformState_isKnownToBeWHNF");

		/**
		 * Helper binding method for function: transformState_isTopLevelBoundName. 
		 * @param state
		 * @param name
		 * @return the SourceModule.expr representing an application of transformState_isTopLevelBoundName
		 */
		public static final SourceModel.Expr transformState_isTopLevelBoundName(SourceModel.Expr state, SourceModel.Expr name) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformState_isTopLevelBoundName), state, name});
		}

		/**
		 * Name binding for function: transformState_isTopLevelBoundName.
		 * @see #transformState_isTopLevelBoundName(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformState_isTopLevelBoundName = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformState_isTopLevelBoundName");

		/**
		 * Helper binding method for function: transformState_removeTypes. 
		 * @param state
		 * @param names
		 * @return the SourceModule.expr representing an application of transformState_removeTypes
		 */
		public static final SourceModel.Expr transformState_removeTypes(SourceModel.Expr state, SourceModel.Expr names) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformState_removeTypes), state, names});
		}

		/**
		 * Name binding for function: transformState_removeTypes.
		 * @see #transformState_removeTypes(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformState_removeTypes = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformState_removeTypes");

		/**
		 * Helper binding method for function: transformState_setCurrentFunctionName. 
		 * @param state
		 * @param name
		 * @return the SourceModule.expr representing an application of transformState_setCurrentFunctionName
		 */
		public static final SourceModel.Expr transformState_setCurrentFunctionName(SourceModel.Expr state, SourceModel.Expr name) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformState_setCurrentFunctionName), state, name});
		}

		/**
		 * Name binding for function: transformState_setCurrentFunctionName.
		 * @see #transformState_setCurrentFunctionName(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformState_setCurrentFunctionName = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformState_setCurrentFunctionName");

		/**
		 * Helper binding method for function: transformState_setInContextBoundNames. 
		 * @param name
		 * @param state
		 * @return the SourceModule.expr representing an application of transformState_setInContextBoundNames
		 */
		public static final SourceModel.Expr transformState_setInContextBoundNames(SourceModel.Expr name, SourceModel.Expr state) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformState_setInContextBoundNames), name, state});
		}

		/**
		 * Name binding for function: transformState_setInContextBoundNames.
		 * @see #transformState_setInContextBoundNames(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformState_setInContextBoundNames = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformState_setInContextBoundNames");

		/**
		 * Helper binding method for function: transformState_setParentExpr. 
		 * @param state
		 * @param parentExpr
		 * @return the SourceModule.expr representing an application of transformState_setParentExpr
		 */
		public static final SourceModel.Expr transformState_setParentExpr(SourceModel.Expr state, SourceModel.Expr parentExpr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformState_setParentExpr), state, parentExpr});
		}

		/**
		 * Name binding for function: transformState_setParentExpr.
		 * @see #transformState_setParentExpr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformState_setParentExpr = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformState_setParentExpr");

		/**
		 * Helper binding method for function: transformState_setPerformingFusion. 
		 * @param state
		 * @param test
		 * @param f1
		 * @param f2
		 * @return the SourceModule.expr representing an application of transformState_setPerformingFusion
		 */
		public static final SourceModel.Expr transformState_setPerformingFusion(SourceModel.Expr state, SourceModel.Expr test, SourceModel.Expr f1, SourceModel.Expr f2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformState_setPerformingFusion), state, test, f1, f2});
		}

		/**
		 * Name binding for function: transformState_setPerformingFusion.
		 * @see #transformState_setPerformingFusion(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformState_setPerformingFusion = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformState_setPerformingFusion");

		/**
		 * Helper binding method for function: transformState_setTraverseCoreFunctions. 
		 * @param state
		 * @param value
		 * @return the SourceModule.expr representing an application of transformState_setTraverseCoreFunctions
		 */
		public static final SourceModel.Expr transformState_setTraverseCoreFunctions(SourceModel.Expr state, SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformState_setTraverseCoreFunctions), state, value});
		}

		/**
		 * @see #transformState_setTraverseCoreFunctions(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param state
		 * @param value
		 * @return the SourceModel.Expr representing an application of transformState_setTraverseCoreFunctions
		 */
		public static final SourceModel.Expr transformState_setTraverseCoreFunctions(SourceModel.Expr state, boolean value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformState_setTraverseCoreFunctions), state, SourceModel.Expr.makeBooleanValue(value)});
		}

		/**
		 * Name binding for function: transformState_setTraverseCoreFunctions.
		 * @see #transformState_setTraverseCoreFunctions(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformState_setTraverseCoreFunctions = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformState_setTraverseCoreFunctions");

		/**
		 * Add the give name and type pair to the list in the transform state.
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 *          The state to update.
		 * @param name (CAL type: <code>Cal.Internal.Optimizer_Expression.QualifiedName</code>)
		 *          The name of the variable that has the given type.
		 * @param type (CAL type: <code>Cal.Internal.Optimizer_Type.Type</code>)
		 *          The type of the variable with the given name.
		 * @return (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>) 
		 *          A state where the name, name has the type, type.
		 */
		public static final SourceModel.Expr transformState_setType(SourceModel.Expr state, SourceModel.Expr name, SourceModel.Expr type) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformState_setType), state, name, type});
		}

		/**
		 * Name binding for function: transformState_setType.
		 * @see #transformState_setType(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformState_setType = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformState_setType");

		/**
		 * Add the given function to the list of currently defined symbols
		 * @param var (CAL type: <code>Cal.Internal.Optimizer_Expression.QualifiedName</code>)
		 *          Name of the variable defined.
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The expression that defined the variable.
		 * @param type (CAL type: <code>Cal.Core.Prelude.Maybe [Cal.Internal.Optimizer_Type.Type]</code>)
		 *          The type of the expression.
		 * @param isConstArgs (CAL type: <code>[Cal.Core.Prelude.Boolean]</code>)
		 *          Flags indicating if the correspond argument of the expression is passes unchanged to recursive calls of the function.
		 * @param isTopLevelName (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 *          Determines is the name is added to the top level name list of the inContext name list.
		 * @param ts (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 *          The context of the current expression.
		 * @return (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>) 
		 *          The context with this function definition added.
		 */
		public static final SourceModel.Expr transformState_update(SourceModel.Expr var, SourceModel.Expr expr, SourceModel.Expr type, SourceModel.Expr isConstArgs, SourceModel.Expr isTopLevelName, SourceModel.Expr ts) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformState_update), var, expr, type, isConstArgs, isTopLevelName, ts});
		}

		/**
		 * @see #transformState_update(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param var
		 * @param expr
		 * @param type
		 * @param isConstArgs
		 * @param isTopLevelName
		 * @param ts
		 * @return the SourceModel.Expr representing an application of transformState_update
		 */
		public static final SourceModel.Expr transformState_update(SourceModel.Expr var, SourceModel.Expr expr, SourceModel.Expr type, SourceModel.Expr isConstArgs, boolean isTopLevelName, SourceModel.Expr ts) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformState_update), var, expr, type, isConstArgs, SourceModel.Expr.makeBooleanValue(isTopLevelName), ts});
		}

		/**
		 * Name binding for function: transformState_update.
		 * @see #transformState_update(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformState_update = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformState_update");

		/**
		 * Helper binding method for function: transformState_updateDerivation. 
		 * @param name
		 * @param state
		 * @return the SourceModule.expr representing an application of transformState_updateDerivation
		 */
		public static final SourceModel.Expr transformState_updateDerivation(SourceModel.Expr name, SourceModel.Expr state) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformState_updateDerivation), name, state});
		}

		/**
		 * Name binding for function: transformState_updateDerivation.
		 * @see #transformState_updateDerivation(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformState_updateDerivation = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformState_updateDerivation");

		/**
		 * Helper binding method for function: transformState_updateInContextBoundNames. 
		 * @param name
		 * @param state
		 * @return the SourceModule.expr representing an application of transformState_updateInContextBoundNames
		 */
		public static final SourceModel.Expr transformState_updateInContextBoundNames(SourceModel.Expr name, SourceModel.Expr state) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformState_updateInContextBoundNames), name, state});
		}

		/**
		 * Name binding for function: transformState_updateInContextBoundNames.
		 * @see #transformState_updateInContextBoundNames(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformState_updateInContextBoundNames = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformState_updateInContextBoundNames");

		/**
		 * Helper binding method for function: transformState_updateLambdaVars. 
		 * @param name
		 * @param type
		 * @param state
		 * @return the SourceModule.expr representing an application of transformState_updateLambdaVars
		 */
		public static final SourceModel.Expr transformState_updateLambdaVars(SourceModel.Expr name, SourceModel.Expr type, SourceModel.Expr state) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformState_updateLambdaVars), name, type, state});
		}

		/**
		 * Name binding for function: transformState_updateLambdaVars.
		 * @see #transformState_updateLambdaVars(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformState_updateLambdaVars = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformState_updateLambdaVars");

		/**
		 * Helper binding method for function: transformState_updateSafeForInliningVars. 
		 * @param name
		 * @param state
		 * @return the SourceModule.expr representing an application of transformState_updateSafeForInliningVars
		 */
		public static final SourceModel.Expr transformState_updateSafeForInliningVars(SourceModel.Expr name, SourceModel.Expr state) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformState_updateSafeForInliningVars), name, state});
		}

		/**
		 * Name binding for function: transformState_updateSafeForInliningVars.
		 * @see #transformState_updateSafeForInliningVars(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformState_updateSafeForInliningVars = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformState_updateSafeForInliningVars");

		/**
		 * Add the given name to the list of defined names. This is used for the lambda lifter
		 * to determine if a variable is free.
		 * @param name (CAL type: <code>Cal.Internal.Optimizer_Expression.QualifiedName</code>)
		 *          The name of the bound name.
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 *          The current expression context.
		 * @return (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>) 
		 *          The state with the newly bound name.
		 */
		public static final SourceModel.Expr transformState_updateTopLevelBoundNames(SourceModel.Expr name, SourceModel.Expr state) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformState_updateTopLevelBoundNames), name, state});
		}

		/**
		 * Name binding for function: transformState_updateTopLevelBoundNames.
		 * @see #transformState_updateTopLevelBoundNames(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformState_updateTopLevelBoundNames = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformState_updateTopLevelBoundNames");

		/**
		 * Helper binding method for function: transformState_updateType. 
		 * @param name
		 * @param type
		 * @param state
		 * @return the SourceModule.expr representing an application of transformState_updateType
		 */
		public static final SourceModel.Expr transformState_updateType(SourceModel.Expr name, SourceModel.Expr type, SourceModel.Expr state) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transformState_updateType), name, type, state});
		}

		/**
		 * Name binding for function: transformState_updateType.
		 * @see #transformState_updateType(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transformState_updateType = 
			QualifiedName.make(
				CAL_Optimizer_State_internal.MODULE_NAME, 
				"transformState_updateType");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = 1901260698;

}
