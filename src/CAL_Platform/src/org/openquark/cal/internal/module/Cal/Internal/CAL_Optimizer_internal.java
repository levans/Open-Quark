/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_Optimizer_internal.java)
 * was generated from CAL module: Cal.Internal.Optimizer.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Internal.Optimizer module from Java code.
 *  
 * Creation date: Thu Oct 18 09:19:21 PDT 2007
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
 * There are five modules that comprise the optimizer.
 * <p>
 * <code>Cal.Internal.Optimizer</code>
 * This module contains the code that applies the optimizating transformations to the given expression. 
 * <code>Cal.Internal.Optimizer.optimize</code> is the entry point to the optimization process.
 * <p>
 * <code>Cal.Internal.Optimizer_Type</code>
 * This module contains the definitions for type expressions in CAL. This file contains helper functions
 * including all the code for type unifications.
 * <p>
 * <code>Cal.Internal.Optimizer_Expression</code>
 * This module contains all the definition for expression objects in CAL. 
 * <p>
 * <code>Cal.Internal.Optimizer_State</code>
 * This module contains the definition for TransformState and TransformHistory as well as supporting functions. 
 * These data values are used by the traversal and transformation functions.
 * <p>
 * <code>Cal.Internal.Optimizer_Traversers</code>
 * This module contains functions for traversing expressions and performing transformations.
 * <p>
 * <code>Cal.Internal.Optimizer_Transformations</code>
 * This module contains all of the optimizing transformations as well as supporting functions.
 * <p>
 * TODO Add some of the fancy new cal type elements. 
 * 
 * @author Greg McClement
 */
public final class CAL_Optimizer_internal {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Internal.Optimizer");

	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Internal.Optimizer module.
	 */
	public static final class Functions {
		/**
		 * Helper binding method for function: arrangeCoreFunctions. 
		 * @param cfs
		 * @return the SourceModule.expr representing an application of arrangeCoreFunctions
		 */
		public static final SourceModel.Expr arrangeCoreFunctions(SourceModel.Expr cfs) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.arrangeCoreFunctions), cfs});
		}

		/**
		 * Name binding for function: arrangeCoreFunctions.
		 * @see #arrangeCoreFunctions(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName arrangeCoreFunctions = 
			QualifiedName.make(
				CAL_Optimizer_internal.MODULE_NAME, 
				"arrangeCoreFunctions");

		/**
		 * Convert expressions of the form
		 * <p>
		 * x `seq` y `seq` &lt;expression&gt;
		 * <p>
		 * to
		 * <p>
		 * let
		 * f = x `seq` y `seq` &lt;expression&gt;
		 * in
		 * f;
		 * 
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 * @param arguments (CAL type: <code>[Cal.Internal.Optimizer_Expression.QualifiedName]</code>)
		 * @param expression (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>(Cal.Internal.Optimizer_State.TransformHistory, Cal.Core.Prelude.Boolean, Cal.Internal.Optimizer_Expression.Expression)</code>) 
		 */
		public static final SourceModel.Expr convertSeqExpressions(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr arguments, SourceModel.Expr expression) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.convertSeqExpressions), state, history, arguments, expression});
		}

		/**
		 * Name binding for function: convertSeqExpressions.
		 * @see #convertSeqExpressions(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName convertSeqExpressions = 
			QualifiedName.make(
				CAL_Optimizer_internal.MODULE_NAME, 
				"convertSeqExpressions");

		/**
		 * Convert the chain of seq'ed expression to
		 * <p>
		 * Input
		 * <p>
		 * e1 `seq` e2 ... `seq` ek `seq` e
		 * <p>
		 * Output (CASE 1) 
		 * <p>
		 * let
		 * f = (\!x1 -&gt; (\!x2 -&gt; ... (\!xk -&gt; e) ... ));
		 * in 
		 * f e1 e2 ... ek;
		 * <p>
		 * Only do this if all of the ei's are variables.
		 * <p>
		 * If all the e1 are not variables do
		 * <p>
		 * Output (CASE 2)
		 * <p>
		 * let
		 * f !e1 = e2 `seq` ... ek
		 * in 
		 * f e1 `seq` e
		 * <p>
		 * Output (CASE 3)
		 * <p>
		 * let
		 * f !a1 =
		 * e2 `seq` ...
		 * ek;
		 * in
		 * f e1;        
		 * 
		 * @param arguments (CAL type: <code>[Cal.Internal.Optimizer_Expression.QualifiedName]</code>)
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 * @param expression (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>(Cal.Internal.Optimizer_State.TransformHistory, Cal.Core.Prelude.Boolean, Cal.Internal.Optimizer_Expression.Expression)</code>) 
		 */
		public static final SourceModel.Expr convertSeqExpressions_createFunction(SourceModel.Expr arguments, SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr expression) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.convertSeqExpressions_createFunction), arguments, state, history, expression});
		}

		/**
		 * Name binding for function: convertSeqExpressions_createFunction.
		 * @see #convertSeqExpressions_createFunction(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName convertSeqExpressions_createFunction = 
			QualifiedName.make(
				CAL_Optimizer_internal.MODULE_NAME, 
				"convertSeqExpressions_createFunction");

		/**
		 * Helper binding method for function: convertSeqExpressions_toChain. 
		 * @param state
		 * @param history
		 * @param expression
		 * @return the SourceModule.expr representing an application of convertSeqExpressions_toChain
		 */
		public static final SourceModel.Expr convertSeqExpressions_toChain(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr expression) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.convertSeqExpressions_toChain), state, history, expression});
		}

		/**
		 * Name binding for function: convertSeqExpressions_toChain.
		 * @see #convertSeqExpressions_toChain(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName convertSeqExpressions_toChain = 
			QualifiedName.make(
				CAL_Optimizer_internal.MODULE_NAME, 
				"convertSeqExpressions_toChain");

		/**
		 * If a function had an unsafeCoerce then the optimizer should not touch them because the coerce is 
		 * mostly lost at this point. 
		 * TODO Change the code so this embedding is not needed.
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param cf (CAL type: <code>Cal.Internal.Optimizer_Expression.CoreFunction</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr coreFunction_probablyHasUnsafeCoerce(SourceModel.Expr state, SourceModel.Expr cf) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.coreFunction_probablyHasUnsafeCoerce), state, cf});
		}

		/**
		 * Name binding for function: coreFunction_probablyHasUnsafeCoerce.
		 * @see #coreFunction_probablyHasUnsafeCoerce(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName coreFunction_probablyHasUnsafeCoerce = 
			QualifiedName.make(
				CAL_Optimizer_internal.MODULE_NAME, 
				"coreFunction_probablyHasUnsafeCoerce");

		/**
		 * Embed the strictness information in the types. 
		 * TODO Change the code so this embedding is not needed.
		 * @param cf (CAL type: <code>Cal.Internal.Optimizer_Expression.CoreFunction</code>)
		 * @return (CAL type: <code>Cal.Internal.Optimizer_Expression.CoreFunction</code>) 
		 */
		public static final SourceModel.Expr coreFunction_setStrictnessInTypes(SourceModel.Expr cf) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.coreFunction_setStrictnessInTypes), cf});
		}

		/**
		 * Name binding for function: coreFunction_setStrictnessInTypes.
		 * @see #coreFunction_setStrictnessInTypes(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName coreFunction_setStrictnessInTypes = 
			QualifiedName.make(
				CAL_Optimizer_internal.MODULE_NAME, 
				"coreFunction_setStrictnessInTypes");

		/**
		 * Takes the set of input definitions and a start expression and embeds the expressions
		 * using let in the given expression. Note: This will change in the future to use a different
		 * data structure because of problems with co-recursion.
		 * <p>
		 * TODO: Change the way that the core functions are embedded.
		 * 
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param coreFunctions (CAL type: <code>[Cal.Internal.Optimizer_Expression.CoreFunction]</code>)
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>) 
		 */
		public static final SourceModel.Expr embedCoreFunctions(SourceModel.Expr state, SourceModel.Expr coreFunctions, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.embedCoreFunctions), state, coreFunctions, expr});
		}

		/**
		 * Name binding for function: embedCoreFunctions.
		 * @see #embedCoreFunctions(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName embedCoreFunctions = 
			QualifiedName.make(
				CAL_Optimizer_internal.MODULE_NAME, 
				"embedCoreFunctions");

		/**
		 * Helper binding method for function: enclose. 
		 * @param adjacencyList
		 * @param nodes
		 * @return the SourceModule.expr representing an application of enclose
		 */
		public static final SourceModel.Expr enclose(SourceModel.Expr adjacencyList, SourceModel.Expr nodes) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.enclose), adjacencyList, nodes});
		}

		/**
		 * Name binding for function: enclose.
		 * @see #enclose(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName enclose = 
			QualifiedName.make(CAL_Optimizer_internal.MODULE_NAME, "enclose");

		/**
		 * Removes any functions that use "unsafeCoerce" from the given list of core functions.
		 * @param list (CAL type: <code>[Cal.Internal.Optimizer_Expression.CoreFunction]</code>)
		 * @return (CAL type: <code>[Cal.Internal.Optimizer_Expression.CoreFunction]</code>) 
		 */
		public static final SourceModel.Expr filterOutUnsafeCoerce(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.filterOutUnsafeCoerce), list});
		}

		/**
		 * Name binding for function: filterOutUnsafeCoerce.
		 * @see #filterOutUnsafeCoerce(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName filterOutUnsafeCoerce = 
			QualifiedName.make(
				CAL_Optimizer_internal.MODULE_NAME, 
				"filterOutUnsafeCoerce");

		/**
		 * Used to search the expression for free variables. This should be called by a traversal function 
		 * using transformAcc.
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 * @param functionsToBeLifted (CAL type: <code>[Cal.Internal.Optimizer_Expression.QualifiedName]</code>)
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param names (CAL type: <code>[(Cal.Internal.Optimizer_Expression.Expression, Cal.Core.Prelude.Maybe Cal.Internal.Optimizer_Type.Type)]</code>)
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>([(Cal.Internal.Optimizer_Expression.Expression, Cal.Core.Prelude.Maybe Cal.Internal.Optimizer_Type.Type)], Cal.Internal.Optimizer_Expression.Expression)</code>) 
		 */
		public static final SourceModel.Expr findFreeVariable(SourceModel.Expr history, SourceModel.Expr functionsToBeLifted, SourceModel.Expr state, SourceModel.Expr names, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.findFreeVariable), history, functionsToBeLifted, state, names, expr});
		}

		/**
		 * Name binding for function: findFreeVariable.
		 * @see #findFreeVariable(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName findFreeVariable = 
			QualifiedName.make(
				CAL_Optimizer_internal.MODULE_NAME, 
				"findFreeVariable");

		/**
		 * Search for a string within another string.
		 * @param string (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          The string to search.
		 * @param contains (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          The string to search for.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          True iff the string contains the search for string.
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
			QualifiedName.make(CAL_Optimizer_internal.MODULE_NAME, "findString");

		/**
		 * If the case variable name is not from this module the name must
		 * be changed to the current module otherwise when this is converted
		 * back into Java Expression and the module name is removed from the
		 * case variable problems will occur
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 *          The context of the current transformation.
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The expression for the case alternative.
		 * @param qn (CAL type: <code>Cal.Internal.Optimizer_Expression.QualifiedName</code>)
		 *          The current name of the case variable.
		 * @return (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>) 
		 *          The alternative expression with the case variable (possibly) renamed.
		 */
		public static final SourceModel.Expr fixUpName(SourceModel.Expr state, SourceModel.Expr expr, SourceModel.Expr qn) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fixUpName), state, expr, qn});
		}

		/**
		 * Name binding for function: fixUpName.
		 * @see #fixUpName(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fixUpName = 
			QualifiedName.make(CAL_Optimizer_internal.MODULE_NAME, "fixUpName");

		/**
		 * For core functions where later arguments are plings move them to the left. For example,
		 * change (f x y !z) into (f !z x y).
		 * @param cf (CAL type: <code>Cal.Internal.Optimizer_Expression.CoreFunction</code>)
		 * @param todo (CAL type: <code>[Cal.Internal.Optimizer_Expression.CoreFunction]</code>)
		 * @param done (CAL type: <code>[Cal.Internal.Optimizer_Expression.CoreFunction]</code>)
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe (Cal.Internal.Optimizer_Expression.CoreFunction, [Cal.Internal.Optimizer_Expression.CoreFunction], [Cal.Internal.Optimizer_Expression.CoreFunction], Cal.Internal.Optimizer_Expression.Expression)</code>) 
		 */
		public static final SourceModel.Expr floatStrictArguments(SourceModel.Expr cf, SourceModel.Expr todo, SourceModel.Expr done, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floatStrictArguments), cf, todo, done, expr});
		}

		/**
		 * Name binding for function: floatStrictArguments.
		 * @see #floatStrictArguments(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName floatStrictArguments = 
			QualifiedName.make(
				CAL_Optimizer_internal.MODULE_NAME, 
				"floatStrictArguments");

		/**
		 * Gets the arity of an expression.
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The expression to get the arity from.
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          The arity of the expression.
		 */
		public static final SourceModel.Expr getArity(SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getArity), expr});
		}

		/**
		 * Name binding for function: getArity.
		 * @see #getArity(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getArity = 
			QualifiedName.make(CAL_Optimizer_internal.MODULE_NAME, "getArity");

		/**
		 * Helper binding method for function: getArityExamples. 
		 * @return the SourceModule.expr representing an application of getArityExamples
		 */
		public static final SourceModel.Expr getArityExamples() {
			return SourceModel.Expr.Var.make(Functions.getArityExamples);
		}

		/**
		 * Name binding for function: getArityExamples.
		 * @see #getArityExamples()
		 */
		public static final QualifiedName getArityExamples = 
			QualifiedName.make(
				CAL_Optimizer_internal.MODULE_NAME, 
				"getArityExamples");

		/**
		 * Make a list of the names of the functions that need to be lifted to the top level (arg1)
		 * and a list of functions that need helpers created. For example, the q in the following
		 * should not be lifted to the top level because 'q' is referenced in spiral more than once.
		 * <p>
		 * let
		 * q =
		 * case qs of
		 * listHead : _ -&gt; listHead; 
		 * []  -&gt; error "Empty list.";
		 * ;
		 * sp = Nofib.spiral ws (List.tail ps) (List.tail qs);
		 * spiral = (Prelude.seq ns (Prelude.foldRight (turn0 q sp) (roll q sp s ns ms s) ns));
		 * in
		 * spiral;
		 * 
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 * @param expression (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>(Cal.Internal.Optimizer_State.TransformHistory, [Cal.Internal.Optimizer_Expression.QualifiedName], Cal.Internal.Optimizer_Expression.Expression)</code>) 
		 */
		public static final SourceModel.Expr getFunctionsToLift(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr expression) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getFunctionsToLift), state, history, expression});
		}

		/**
		 * Name binding for function: getFunctionsToLift.
		 * @see #getFunctionsToLift(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getFunctionsToLift = 
			QualifiedName.make(
				CAL_Optimizer_internal.MODULE_NAME, 
				"getFunctionsToLift");

		/**
		 * Helper binding method for function: getMoreLeaves. 
		 * @param adjacencyList
		 * @return the SourceModule.expr representing an application of getMoreLeaves
		 */
		public static final SourceModel.Expr getMoreLeaves(SourceModel.Expr adjacencyList) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getMoreLeaves), adjacencyList});
		}

		/**
		 * Name binding for function: getMoreLeaves.
		 * @see #getMoreLeaves(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getMoreLeaves = 
			QualifiedName.make(
				CAL_Optimizer_internal.MODULE_NAME, 
				"getMoreLeaves");

		/**
		 * Helper binding method for function: isLeaf. 
		 * @param aj
		 * @return the SourceModule.expr representing an application of isLeaf
		 */
		public static final SourceModel.Expr isLeaf(SourceModel.Expr aj) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isLeaf), aj});
		}

		/**
		 * Name binding for function: isLeaf.
		 * @see #isLeaf(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isLeaf = 
			QualifiedName.make(CAL_Optimizer_internal.MODULE_NAME, "isLeaf");

		/**
		 * Helper binding method for function: isNotLeaf. 
		 * @param aj
		 * @return the SourceModule.expr representing an application of isNotLeaf
		 */
		public static final SourceModel.Expr isNotLeaf(SourceModel.Expr aj) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isNotLeaf), aj});
		}

		/**
		 * Name binding for function: isNotLeaf.
		 * @see #isNotLeaf(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isNotLeaf = 
			QualifiedName.make(CAL_Optimizer_internal.MODULE_NAME, "isNotLeaf");

		/**
		 * If the arguments of a functions are `seq` in the body then instead pling the arguments and remove the
		 * seq. This is more efficient. The strictness is needed so that a test such as "test105 x !y = x `seq` y"
		 * will not result in x being plinged because the order of the x and y evaluation would be changed.
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param parameters (CAL type: <code>[Cal.Internal.Optimizer_Expression.QualifiedName]</code>)
		 *          The names of the parameters of the function that has the given body.
		 * @param extantStrictness (CAL type: <code>[Cal.Core.Prelude.Boolean]</code>)
		 *          The strictness of the given parameters.
		 * @param argIsWHNF (CAL type: <code>[Cal.Core.Prelude.Boolean]</code>)
		 *          The argument was marked as strict because it is in WHFN. This is used to allow an argument
		 * that appears earlier in the list to be marked as strict based on being seq'ed. This is safe because no out
		 * of order evaluation will occur.
		 * @param expression (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The expression to search for seq'ed that can be used to update the strictness array.
		 * @return (CAL type: <code>([Cal.Core.Prelude.Boolean], Cal.Internal.Optimizer_Expression.Expression)</code>) 
		 */
		public static final SourceModel.Expr liftArgumentStrictness(SourceModel.Expr state, SourceModel.Expr parameters, SourceModel.Expr extantStrictness, SourceModel.Expr argIsWHNF, SourceModel.Expr expression) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.liftArgumentStrictness), state, parameters, extantStrictness, argIsWHNF, expression});
		}

		/**
		 * Name binding for function: liftArgumentStrictness.
		 * @see #liftArgumentStrictness(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName liftArgumentStrictness = 
			QualifiedName.make(
				CAL_Optimizer_internal.MODULE_NAME, 
				"liftArgumentStrictness");

		/**
		 * If there are any strict arguments in the expression that can be moved to plinged arguments
		 * then do that.
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param cf (CAL type: <code>Cal.Internal.Optimizer_Expression.CoreFunction</code>)
		 * @return (CAL type: <code>Cal.Internal.Optimizer_Expression.CoreFunction</code>) 
		 */
		public static final SourceModel.Expr liftArgumentStrictnessCoreFunction(SourceModel.Expr state, SourceModel.Expr cf) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.liftArgumentStrictnessCoreFunction), state, cf});
		}

		/**
		 * Name binding for function: liftArgumentStrictnessCoreFunction.
		 * @see #liftArgumentStrictnessCoreFunction(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName liftArgumentStrictnessCoreFunction = 
			QualifiedName.make(
				CAL_Optimizer_internal.MODULE_NAME, 
				"liftArgumentStrictnessCoreFunction");

		/**
		 * Helper binding method for function: liftArgumentStrictnessCoreFunctions. 
		 * @param state
		 * @param todo
		 * @param done
		 * @param expr
		 * @return the SourceModule.expr representing an application of liftArgumentStrictnessCoreFunctions
		 */
		public static final SourceModel.Expr liftArgumentStrictnessCoreFunctions(SourceModel.Expr state, SourceModel.Expr todo, SourceModel.Expr done, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.liftArgumentStrictnessCoreFunctions), state, todo, done, expr});
		}

		/**
		 * Name binding for function: liftArgumentStrictnessCoreFunctions.
		 * @see #liftArgumentStrictnessCoreFunctions(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName liftArgumentStrictnessCoreFunctions = 
			QualifiedName.make(
				CAL_Optimizer_internal.MODULE_NAME, 
				"liftArgumentStrictnessCoreFunctions");

		/**
		 * For a given let definition decide if the function needs to have free variables added as parameters
		 * so that when the function is made into a top level function all the variabled will be properly defined.
		 * @param functionsToBeLifted (CAL type: <code>[Cal.Internal.Optimizer_Expression.QualifiedName]</code>)
		 *          A list of functions that are to be lifted.
		 * @param topLevelFunctions (CAL type: <code>[Cal.Internal.Optimizer_Expression.QualifiedName]</code>)
		 *          A list of function names known to be top level.
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 *          The context of the current expression. Including all of the bound names.
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 *          The history of the current transformation.
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The expression to check for lambda lifting.
		 * @return (CAL type: <code>(Cal.Internal.Optimizer_State.TransformHistory, Cal.Core.Prelude.Boolean, Cal.Internal.Optimizer_Expression.Expression)</code>) 
		 *          The expression with lambda lifting performed if necessary.
		 * <p>
		 * Note: This is not general purpose lambda lifting. This is what is needed for getting the fusion and
		 * specialized function out.
		 * <p>
		 * TODO Make sure this corresponds with Johnsson style lambda lifting.
		 */
		public static final SourceModel.Expr liftLambdas(SourceModel.Expr functionsToBeLifted, SourceModel.Expr topLevelFunctions, SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.liftLambdas), functionsToBeLifted, topLevelFunctions, state, history, expr});
		}

		/**
		 * Name binding for function: liftLambdas.
		 * @see #liftLambdas(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName liftLambdas = 
			QualifiedName.make(
				CAL_Optimizer_internal.MODULE_NAME, 
				"liftLambdas");

		/**
		 * Helper binding method for function: lookForUnsafeCoerces. 
		 * @param state
		 * @param history
		 * @param expr
		 * @return the SourceModule.expr representing an application of lookForUnsafeCoerces
		 */
		public static final SourceModel.Expr lookForUnsafeCoerces(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lookForUnsafeCoerces), state, history, expr});
		}

		/**
		 * Name binding for function: lookForUnsafeCoerces.
		 * @see #lookForUnsafeCoerces(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName lookForUnsafeCoerces = 
			QualifiedName.make(
				CAL_Optimizer_internal.MODULE_NAME, 
				"lookForUnsafeCoerces");

		/**
		 * This function performs transformational optimization on the given expression.
		 * @param name (CAL type: <code>Cal.Internal.Optimizer_Expression.QualifiedName</code>)
		 *          The name of the expression being optimized.
		 * @param arguments (CAL type: <code>[Cal.Internal.Optimizer_Expression.QualifiedName]</code>)
		 *          The names of the arguments of the functions whose body this is the expression of.
		 * @param argumentStrictness (CAL type: <code>[Cal.Core.Prelude.Boolean]</code>)
		 *          The strictness array for the given arguments.
		 * @param startTime (CAL type: <code>Cal.Core.Prelude.Long</code>)
		 *          The time that the optimizer is starting at.
		 * @param typeConstants (CAL type: <code>Cal.Internal.Optimizer_State.JPreludeTypeConstants</code>)
		 *          Used to lookup the type expression for various basic types (String, Int, Double ...)
		 * @param nameToTypeList1 (CAL type: <code>[(Cal.Internal.Optimizer_Expression.QualifiedName, [Cal.Internal.Optimizer_Type.Type], [Cal.Core.Prelude.Boolean])]</code>)
		 *          Mapping for variable name to type for some pre-known values. The [Boolean] is the argument strictness.
		 * @param coreFunctionsIn (CAL type: <code>[Cal.Internal.Optimizer_Expression.CoreFunction]</code>)
		 *          The definitions for the functions used in the given expression.
		 * @param nonCalFunctions (CAL type: <code>[Cal.Internal.Optimizer_Expression.QualifiedName]</code>)
		 *          The names of primitive and foreign functions.
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The expression to optimize.
		 * @return (CAL type: <code>([Cal.Internal.Optimizer_Expression.CoreFunction], [Cal.Core.Prelude.Boolean], Cal.Internal.Optimizer_Expression.Expression)</code>) 
		 *          The optimizer version of the given expression and new helper functions along with an array flagging which arguments are strict.
		 */
		public static final SourceModel.Expr optimize(SourceModel.Expr name, SourceModel.Expr arguments, SourceModel.Expr argumentStrictness, SourceModel.Expr startTime, SourceModel.Expr typeConstants, SourceModel.Expr nameToTypeList1, SourceModel.Expr coreFunctionsIn, SourceModel.Expr nonCalFunctions, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimize), name, arguments, argumentStrictness, startTime, typeConstants, nameToTypeList1, coreFunctionsIn, nonCalFunctions, expr});
		}

		/**
		 * @see #optimize(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param name
		 * @param arguments
		 * @param argumentStrictness
		 * @param startTime
		 * @param typeConstants
		 * @param nameToTypeList1
		 * @param coreFunctionsIn
		 * @param nonCalFunctions
		 * @param expr
		 * @return the SourceModel.Expr representing an application of optimize
		 */
		public static final SourceModel.Expr optimize(SourceModel.Expr name, SourceModel.Expr arguments, SourceModel.Expr argumentStrictness, long startTime, SourceModel.Expr typeConstants, SourceModel.Expr nameToTypeList1, SourceModel.Expr coreFunctionsIn, SourceModel.Expr nonCalFunctions, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimize), name, arguments, argumentStrictness, SourceModel.Expr.makeLongValue(startTime), typeConstants, nameToTypeList1, coreFunctionsIn, nonCalFunctions, expr});
		}

		/**
		 * Name binding for function: optimize.
		 * @see #optimize(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName optimize = 
			QualifiedName.make(CAL_Optimizer_internal.MODULE_NAME, "optimize");

		/**
		 * Helper binding method for function: reduce. 
		 * @param adjacencyList
		 * @param sortedList1
		 * @return the SourceModule.expr representing an application of reduce
		 */
		public static final SourceModel.Expr reduce(SourceModel.Expr adjacencyList, SourceModel.Expr sortedList1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.reduce), adjacencyList, sortedList1});
		}

		/**
		 * Name binding for function: reduce.
		 * @see #reduce(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName reduce = 
			QualifiedName.make(CAL_Optimizer_internal.MODULE_NAME, "reduce");

		/**
		 * Remove the functions that need to be top level from the optimized expression. There is a loop
		 * because if a function is lifted then the functions that is depends on must be lifted as well.
		 * @param functionsToLift (CAL type: <code>[Cal.Internal.Optimizer_Expression.QualifiedName]</code>)
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 * @param expression (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>(Cal.Internal.Optimizer_State.TransformHistory, Cal.Core.Prelude.Boolean, Cal.Internal.Optimizer_Expression.Expression)</code>) 
		 */
		public static final SourceModel.Expr removeFunctionDefs(SourceModel.Expr functionsToLift, SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr expression) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.removeFunctionDefs), functionsToLift, state, history, expression});
		}

		/**
		 * Name binding for function: removeFunctionDefs.
		 * @see #removeFunctionDefs(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName removeFunctionDefs = 
			QualifiedName.make(
				CAL_Optimizer_internal.MODULE_NAME, 
				"removeFunctionDefs");

		/**
		 * Helper binding method for function: removeLeaves. 
		 * @param moreLeaves
		 * @param adjacencyList
		 * @return the SourceModule.expr representing an application of removeLeaves
		 */
		public static final SourceModel.Expr removeLeaves(SourceModel.Expr moreLeaves, SourceModel.Expr adjacencyList) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.removeLeaves), moreLeaves, adjacencyList});
		}

		/**
		 * Name binding for function: removeLeaves.
		 * @see #removeLeaves(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName removeLeaves = 
			QualifiedName.make(
				CAL_Optimizer_internal.MODULE_NAME, 
				"removeLeaves");

		/**
		 * Helper binding method for function: removeNode. 
		 * @param nodeToRemove
		 * @param aj
		 * @return the SourceModule.expr representing an application of removeNode
		 */
		public static final SourceModel.Expr removeNode(SourceModel.Expr nodeToRemove, SourceModel.Expr aj) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.removeNode), nodeToRemove, aj});
		}

		/**
		 * Name binding for function: removeNode.
		 * @see #removeNode(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName removeNode = 
			QualifiedName.make(CAL_Optimizer_internal.MODULE_NAME, "removeNode");

		/**
		 * Helper binding method for function: removeNodes. 
		 * @param nodesToRemove
		 * @param aj
		 * @return the SourceModule.expr representing an application of removeNodes
		 */
		public static final SourceModel.Expr removeNodes(SourceModel.Expr nodesToRemove, SourceModel.Expr aj) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.removeNodes), nodesToRemove, aj});
		}

		/**
		 * Name binding for function: removeNodes.
		 * @see #removeNodes(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName removeNodes = 
			QualifiedName.make(
				CAL_Optimizer_internal.MODULE_NAME, 
				"removeNodes");

		/**
		 * Rename the arguments of the function so that they look like they belong to the current module. This
		 * is needed because the argument names in the CoreFunction do not have module name. The current module is
		 * assumed.
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 *          The context of the current expression.
		 * @param cf (CAL type: <code>Cal.Internal.Optimizer_Expression.CoreFunction</code>)
		 *          The core function to update.
		 * @return (CAL type: <code>Cal.Internal.Optimizer_Expression.CoreFunction</code>) 
		 *          The core function with the arguments renamed so they are in the current module.
		 */
		public static final SourceModel.Expr renameCoreFunctionForThisModule(SourceModel.Expr state, SourceModel.Expr cf) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.renameCoreFunctionForThisModule), state, cf});
		}

		/**
		 * Name binding for function: renameCoreFunctionForThisModule.
		 * @see #renameCoreFunctionForThisModule(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName renameCoreFunctionForThisModule = 
			QualifiedName.make(
				CAL_Optimizer_internal.MODULE_NAME, 
				"renameCoreFunctionForThisModule");

		/**
		 * Make a list of all the calls in the given expression that have the given variable as an argument.
		 * @param name (CAL type: <code>Cal.Internal.Optimizer_Expression.QualifiedName</code>)
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>[(Cal.Internal.Optimizer_State.TransformState, Cal.Internal.Optimizer_Expression.Expression)]</code>) 
		 */
		public static final SourceModel.Expr selectCalls(SourceModel.Expr name, SourceModel.Expr state, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.selectCalls), name, state, expr});
		}

		/**
		 * Name binding for function: selectCalls.
		 * @see #selectCalls(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName selectCalls = 
			QualifiedName.make(
				CAL_Optimizer_internal.MODULE_NAME, 
				"selectCalls");

		/**
		 * Helper binding method for function: selectJoinPoints. 
		 * @param smallestNeighbourSet
		 * @param adjacencyList
		 * @param unused
		 * @param leaves
		 * @return the SourceModule.expr representing an application of selectJoinPoints
		 */
		public static final SourceModel.Expr selectJoinPoints(SourceModel.Expr smallestNeighbourSet, SourceModel.Expr adjacencyList, SourceModel.Expr unused, SourceModel.Expr leaves) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.selectJoinPoints), smallestNeighbourSet, adjacencyList, unused, leaves});
		}

		/**
		 * @see #selectJoinPoints(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param smallestNeighbourSet
		 * @param adjacencyList
		 * @param unused
		 * @param leaves
		 * @return the SourceModel.Expr representing an application of selectJoinPoints
		 */
		public static final SourceModel.Expr selectJoinPoints(int smallestNeighbourSet, SourceModel.Expr adjacencyList, SourceModel.Expr unused, SourceModel.Expr leaves) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.selectJoinPoints), SourceModel.Expr.makeIntValue(smallestNeighbourSet), adjacencyList, unused, leaves});
		}

		/**
		 * Name binding for function: selectJoinPoints.
		 * @see #selectJoinPoints(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName selectJoinPoints = 
			QualifiedName.make(
				CAL_Optimizer_internal.MODULE_NAME, 
				"selectJoinPoints");

		/**
		 * Remove the nodes in adjacency list that are leaves.
		 * @param adjacencyList (CAL type: <code>(Cal.Core.Debug.Show a, Cal.Core.Prelude.Eq a, Cal.Core.Debug.Show b) => [(a, b, [a])]</code>)
		 * @param unused (CAL type: <code>(Cal.Core.Debug.Show a, Cal.Core.Prelude.Eq a, Cal.Core.Debug.Show b) => [(a, b, [a])]</code>)
		 * @param leaves (CAL type: <code>Cal.Core.Debug.Show b => [b]</code>)
		 * @param wasChanged (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 * @return (CAL type: <code>(Cal.Core.Debug.Show a, Cal.Core.Prelude.Eq a, Cal.Core.Debug.Show b) => ([(a, b, [a])], Cal.Core.Prelude.Boolean, [b])</code>) 
		 */
		public static final SourceModel.Expr selectLeaves(SourceModel.Expr adjacencyList, SourceModel.Expr unused, SourceModel.Expr leaves, SourceModel.Expr wasChanged) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.selectLeaves), adjacencyList, unused, leaves, wasChanged});
		}

		/**
		 * @see #selectLeaves(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param adjacencyList
		 * @param unused
		 * @param leaves
		 * @param wasChanged
		 * @return the SourceModel.Expr representing an application of selectLeaves
		 */
		public static final SourceModel.Expr selectLeaves(SourceModel.Expr adjacencyList, SourceModel.Expr unused, SourceModel.Expr leaves, boolean wasChanged) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.selectLeaves), adjacencyList, unused, leaves, SourceModel.Expr.makeBooleanValue(wasChanged)});
		}

		/**
		 * Name binding for function: selectLeaves.
		 * @see #selectLeaves(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName selectLeaves = 
			QualifiedName.make(
				CAL_Optimizer_internal.MODULE_NAME, 
				"selectLeaves");

		/**
		 * Should the given let expression be lifted to the file scope.
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The expression to check
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          Whether or not the given expression should be lifted.
		 */
		public static final SourceModel.Expr shouldBeLifted(SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.shouldBeLifted), expr});
		}

		/**
		 * Name binding for function: shouldBeLifted.
		 * @see #shouldBeLifted(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName shouldBeLifted = 
			QualifiedName.make(
				CAL_Optimizer_internal.MODULE_NAME, 
				"shouldBeLifted");

		/**
		 * Helper binding method for function: toAdjacencyList. 
		 * @param cfs
		 * @return the SourceModule.expr representing an application of toAdjacencyList
		 */
		public static final SourceModel.Expr toAdjacencyList(SourceModel.Expr cfs) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toAdjacencyList), cfs});
		}

		/**
		 * Name binding for function: toAdjacencyList.
		 * @see #toAdjacencyList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName toAdjacencyList = 
			QualifiedName.make(
				CAL_Optimizer_internal.MODULE_NAME, 
				"toAdjacencyList");

		/**
		 * Helper binding method for function: toTransitiveClosure. 
		 * @param adjacencyList
		 * @return the SourceModule.expr representing an application of toTransitiveClosure
		 */
		public static final SourceModel.Expr toTransitiveClosure(SourceModel.Expr adjacencyList) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toTransitiveClosure), adjacencyList});
		}

		/**
		 * Name binding for function: toTransitiveClosure.
		 * @see #toTransitiveClosure(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName toTransitiveClosure = 
			QualifiedName.make(
				CAL_Optimizer_internal.MODULE_NAME, 
				"toTransitiveClosure");

		/**
		 * Helper binding method for function: topologicalSort. 
		 * @param adjacencyList
		 * @return the SourceModule.expr representing an application of topologicalSort
		 */
		public static final SourceModel.Expr topologicalSort(SourceModel.Expr adjacencyList) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.topologicalSort), adjacencyList});
		}

		/**
		 * Name binding for function: topologicalSort.
		 * @see #topologicalSort(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName topologicalSort = 
			QualifiedName.make(
				CAL_Optimizer_internal.MODULE_NAME, 
				"topologicalSort");

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
			QualifiedName.make(CAL_Optimizer_internal.MODULE_NAME, "trace2");

		/**
		 * The Alt and Let variables have "" as the module name because conversion is done without a context. This 
		 * code set the module name correctly.
		 * <p>
		 * The names of case variables are all converted to be in this module. Otherwise the code that maps back
		 * to Java will assume that the names are in this module although they are not.
		 * <p>
		 * TODO: Make sure there can be no name clashing.
		 * 
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>) 
		 */
		public static final SourceModel.Expr transform_addModuleNameToVariables(SourceModel.Expr state, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transform_addModuleNameToVariables), state, expr});
		}

		/**
		 * Name binding for function: transform_addModuleNameToVariables.
		 * @see #transform_addModuleNameToVariables(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transform_addModuleNameToVariables = 
			QualifiedName.make(
				CAL_Optimizer_internal.MODULE_NAME, 
				"transform_addModuleNameToVariables");

		/**
		 * Helper binding method for function: transform_addType. 
		 * @param state
		 * @param expr
		 * @return the SourceModule.expr representing an application of transform_addType
		 */
		public static final SourceModel.Expr transform_addType(SourceModel.Expr state, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transform_addType), state, expr});
		}

		/**
		 * Name binding for function: transform_addType.
		 * @see #transform_addType(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transform_addType = 
			QualifiedName.make(
				CAL_Optimizer_internal.MODULE_NAME, 
				"transform_addType");

		/**
		 * Helper binding method for function: transform_addTypeToLet. 
		 * @param state
		 * @param history
		 * @param expr
		 * @return the SourceModule.expr representing an application of transform_addTypeToLet
		 */
		public static final SourceModel.Expr transform_addTypeToLet(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transform_addTypeToLet), state, history, expr});
		}

		/**
		 * Name binding for function: transform_addTypeToLet.
		 * @see #transform_addTypeToLet(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transform_addTypeToLet = 
			QualifiedName.make(
				CAL_Optimizer_internal.MODULE_NAME, 
				"transform_addTypeToLet");

		/**
		 * Convert Alts to Alt type expressions. Alts arise during conversion from Java to Cal.
		 * see inputAlt. This also converts matching type alts to positional alts.
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>) 
		 */
		public static final SourceModel.Expr transform_convertAltsToAlt(SourceModel.Expr state, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transform_convertAltsToAlt), state, expr});
		}

		/**
		 * Name binding for function: transform_convertAltsToAlt.
		 * @see #transform_convertAltsToAlt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transform_convertAltsToAlt = 
			QualifiedName.make(
				CAL_Optimizer_internal.MODULE_NAME, 
				"transform_convertAltsToAlt");

		/**
		 * If the given structure cannot appear in an expression passed to the source code generator then
		 * change the structure to conform. Example include
		 * <p>
		 * 1. Lambda expressio not appear in expressions.
		 * 2. Case's not not appear inside an application or record selection expression. Case are converted
		 * in the following way since this is most optimal
		 * case arg of ...
		 * <p>
		 * let
		 * f !x = case x of ...;
		 * in
		 * f arg ...
		 * 
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>(Cal.Internal.Optimizer_State.TransformHistory, Cal.Core.Prelude.Boolean, Cal.Internal.Optimizer_Expression.Expression)</code>) 
		 */
		public static final SourceModel.Expr transform_convertLambdaAndCaseToLet(SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transform_convertLambdaAndCaseToLet), state, history, expr});
		}

		/**
		 * Name binding for function: transform_convertLambdaAndCaseToLet.
		 * @see #transform_convertLambdaAndCaseToLet(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transform_convertLambdaAndCaseToLet = 
			QualifiedName.make(
				CAL_Optimizer_internal.MODULE_NAME, 
				"transform_convertLambdaAndCaseToLet");

		/**
		 * 
		 * @param ensureTheseAreTopLevel (CAL type: <code>[Cal.Internal.Optimizer_Expression.QualifiedName]</code>)
		 *          Lift of symbols that should end up at the top level. 
		 * This list is created from other expressions that are being lifted to the top 
		 * level and reference these symbols.
		 * // * &#64;arg state The context of the expression.
		 * // * &#64;arg liftThese The expression to lift.
		 * // * &#64;arg expr The expression to remove function definitions from.
		 * // * &#64;return A pair, the elements in the first list are pairs. The first element is the function name. If
		 * the expression is present the name is a function to lift, otherwise the name is a function that should have
		 * a helper function made.
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param liftThese (CAL type: <code>(Cal.Internal.Optimizer_State.TransformHistory, [Cal.Internal.Optimizer_Expression.QualifiedName])</code>)
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>((Cal.Internal.Optimizer_State.TransformHistory, [Cal.Internal.Optimizer_Expression.QualifiedName]), Cal.Internal.Optimizer_Expression.Expression)</code>) 
		 */
		public static final SourceModel.Expr transform_getFunctionToLift(SourceModel.Expr ensureTheseAreTopLevel, SourceModel.Expr state, SourceModel.Expr liftThese, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transform_getFunctionToLift), ensureTheseAreTopLevel, state, liftThese, expr});
		}

		/**
		 * Name binding for function: transform_getFunctionToLift.
		 * @see #transform_getFunctionToLift(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transform_getFunctionToLift = 
			QualifiedName.make(
				CAL_Optimizer_internal.MODULE_NAME, 
				"transform_getFunctionToLift");

		/**
		 * Removes any Let definitions that were added to contain functions definitions to use for optimization. Functions
		 * that were created by fusion or specialization are collected in put in the list of new core functions.
		 * @param ensureTheseAreTopLevel (CAL type: <code>[Cal.Internal.Optimizer_Expression.QualifiedName]</code>)
		 *          Lift of symbols that should end up at the top level. 
		 * This list is created from other expressions that are being lifted to the top 
		 * level and reference these symbols.
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 *          The context of the expression.
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 *          The history of the current transformation.
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The expression to remove function definitions from.
		 * @return (CAL type: <code>(Cal.Internal.Optimizer_State.TransformHistory, Cal.Core.Prelude.Boolean, Cal.Internal.Optimizer_Expression.Expression)</code>) 
		 *          A pair, the first element is a list of new core functions and the second is the optimized expression.
		 */
		public static final SourceModel.Expr transform_removeFunctionDefs(SourceModel.Expr ensureTheseAreTopLevel, SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transform_removeFunctionDefs), ensureTheseAreTopLevel, state, history, expr});
		}

		/**
		 * Name binding for function: transform_removeFunctionDefs.
		 * @see #transform_removeFunctionDefs(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transform_removeFunctionDefs = 
			QualifiedName.make(
				CAL_Optimizer_internal.MODULE_NAME, 
				"transform_removeFunctionDefs");

		/**
		 * The variable names in the alternative of a case expression do not have the module name 
		 * (in the Java expression representation) however
		 * the variables when used in the alternative expression do have the module name. The optimizer
		 * can inline a case expression from a different module into the current module. This causes
		 * a problem because the case is in a different module and so the compiler assumed the module name
		 * for the case variables is the current module but the body is using the original module name.
		 * This transformation renames the variables in the alternative expression to match the current module 
		 * for any case variables. In the CAL representation of an expression the module name is present in
		 * the case variable so this can be used to determine if the case came from a different module and
		 * needs to have its arguments renamed.
		 * <p>
		 * TODO: Do this renaming in java during the construction of the input data.
		 * 
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>) 
		 */
		public static final SourceModel.Expr transform_renameCaseVariables(SourceModel.Expr state, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transform_renameCaseVariables), state, expr});
		}

		/**
		 * Name binding for function: transform_renameCaseVariables.
		 * @see #transform_renameCaseVariables(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transform_renameCaseVariables = 
			QualifiedName.make(
				CAL_Optimizer_internal.MODULE_NAME, 
				"transform_renameCaseVariables");

		/**
		 * Move the module of any let definition to be this module so the java code builder
		 * does not get confused. All let vars with now be prefixed with the module name to avoid
		 * name clash.
		 * @param moduleName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @param state (CAL type: <code>Cal.Internal.Optimizer_State.TransformState</code>)
		 * @param history (CAL type: <code>Cal.Internal.Optimizer_State.TransformHistory</code>)
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>(Cal.Internal.Optimizer_State.TransformHistory, Cal.Core.Prelude.Boolean, Cal.Internal.Optimizer_Expression.Expression)</code>) 
		 */
		public static final SourceModel.Expr transform_updateModule(SourceModel.Expr moduleName, SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transform_updateModule), moduleName, state, history, expr});
		}

		/**
		 * @see #transform_updateModule(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param moduleName
		 * @param state
		 * @param history
		 * @param expr
		 * @return the SourceModel.Expr representing an application of transform_updateModule
		 */
		public static final SourceModel.Expr transform_updateModule(java.lang.String moduleName, SourceModel.Expr state, SourceModel.Expr history, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.transform_updateModule), SourceModel.Expr.makeStringValue(moduleName), state, history, expr});
		}

		/**
		 * Name binding for function: transform_updateModule.
		 * @see #transform_updateModule(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName transform_updateModule = 
			QualifiedName.make(
				CAL_Optimizer_internal.MODULE_NAME, 
				"transform_updateModule");

		/**
		 * Helper binding method for function: unions. 
		 * @param xss
		 * @param acc
		 * @return the SourceModule.expr representing an application of unions
		 */
		public static final SourceModel.Expr unions(SourceModel.Expr xss, SourceModel.Expr acc) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.unions), xss, acc});
		}

		/**
		 * Name binding for function: unions.
		 * @see #unions(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName unions = 
			QualifiedName.make(CAL_Optimizer_internal.MODULE_NAME, "unions");

		/**
		 * Helper binding method for function: unitTests. 
		 * @param typeConstants
		 * @return the SourceModule.expr representing an application of unitTests
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
			QualifiedName.make(CAL_Optimizer_internal.MODULE_NAME, "unitTests");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = -323389398;

}
