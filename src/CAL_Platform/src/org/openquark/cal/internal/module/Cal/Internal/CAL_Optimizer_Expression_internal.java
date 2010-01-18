/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_Optimizer_Expression_internal.java)
 * was generated from CAL module: Cal.Internal.Optimizer_Expression.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Internal.Optimizer_Expression module from Java code.
 *  
 * Creation date: Thu Oct 18 09:01:33 PDT 2007
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
 * The definition of the Expression data type and related functions. These data objects correspond to the Java
 * expression type.
 * 
 * @author Greg McClement
 */
public final class CAL_Optimizer_Expression_internal {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Internal.Optimizer_Expression");

	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Internal.Optimizer_Expression module.
	 */
	public static final class TypeConstructors {
		/**
		 * Represents a case alternative.
		 */
		public static final QualifiedName Alt = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"Alt");

		/**
		 * Corresponds to Expression.SwitchAlt.altTag.
		 */
		public static final QualifiedName CaseConst = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"CaseConst");

		/**
		 * Represents a Java core function.
		 */
		public static final QualifiedName CoreFunction = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"CoreFunction");

		/**
		 * Represents data constructors.
		 */
		public static final QualifiedName DataCons = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"DataCons");

		/**
		 * Expression represents a CAL expression.
		 */
		public static final QualifiedName Expression = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"Expression");

		/**
		 * FieldName represents a field name in a record selection expression
		 */
		public static final QualifiedName FieldName = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"FieldName");

		/**
		 * RecordType definitions
		 */
		public static final QualifiedName JCompiler_RecordType_RecordType = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"JCompiler_RecordType_RecordType");

		/** Name binding for TypeConsApp: JDataConstructor. */
		public static final QualifiedName JDataConstructor = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"JDataConstructor");

		/** Name binding for TypeConsApp: JExpression. */
		public static final QualifiedName JExpression = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"JExpression");

		/** Name binding for TypeConsApp: JExpression_Appl. */
		public static final QualifiedName JExpression_Appl = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"JExpression_Appl");

		/** Name binding for TypeConsApp: JExpression_DataConsSelection. */
		public static final QualifiedName JExpression_DataConsSelection = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"JExpression_DataConsSelection");

		/** Name binding for TypeConsApp: JExpression_ErrorInfo. */
		public static final QualifiedName JExpression_ErrorInfo = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"JExpression_ErrorInfo");

		/** Name binding for TypeConsApp: JExpression_Let. */
		public static final QualifiedName JExpression_Let = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"JExpression_Let");

		/** Name binding for TypeConsApp: JExpression_LetNonRec. */
		public static final QualifiedName JExpression_LetNonRec = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"JExpression_LetNonRec");

		/** Name binding for TypeConsApp: JExpression_LetRec. */
		public static final QualifiedName JExpression_LetRec = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"JExpression_LetRec");

		/** Name binding for TypeConsApp: JExpression_Let_LetDefn. */
		public static final QualifiedName JExpression_Let_LetDefn = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"JExpression_Let_LetDefn");

		/** Name binding for TypeConsApp: JExpression_Literal. */
		public static final QualifiedName JExpression_Literal = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"JExpression_Literal");

		/**
		 * Expression.RecordCase definitions
		 */
		public static final QualifiedName JExpression_RecordCase = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"JExpression_RecordCase");

		/**
		 * RecordExtension definitions
		 */
		public static final QualifiedName JExpression_RecordExtension = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"JExpression_RecordExtension");

		/** Name binding for TypeConsApp: JExpression_RecordSelection. */
		public static final QualifiedName JExpression_RecordSelection = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"JExpression_RecordSelection");

		/** Name binding for TypeConsApp: JExpression_Switch. */
		public static final QualifiedName JExpression_Switch = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"JExpression_Switch");

		/**
		 * The code for inputing and outputing Alt
		 */
		public static final QualifiedName JExpression_Switch_SwitchAlt = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"JExpression_Switch_SwitchAlt");

		/**
		 * Expression.Switch.SwitchAlt definitions
		 */
		public static final QualifiedName JExpression_Switch_SwitchAltArray = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"JExpression_Switch_SwitchAltArray");

		/**
		 * Expression.Switch.SwitchAlt.Matching definitions.
		 */
		public static final QualifiedName JExpression_Switch_SwitchAlt_Matching = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"JExpression_Switch_SwitchAlt_Matching");

		/**
		 * Expression.Switch.SwitchAlt.Positional definitions.
		 */
		public static final QualifiedName JExpression_Switch_SwitchAlt_Positional = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"JExpression_Switch_SwitchAlt_Positional");

		/** Name binding for TypeConsApp: JExpression_TailRecursiveCall. */
		public static final QualifiedName JExpression_TailRecursiveCall = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"JExpression_TailRecursiveCall");

		/** Name binding for TypeConsApp: JExpression_Var. */
		public static final QualifiedName JExpression_Var = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"JExpression_Var");

		/**
		 * The code for inputing and outputing FieldName
		 */
		public static final QualifiedName JFieldName = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"JFieldName");

		/** Name binding for TypeConsApp: JFunctionalAgent. */
		public static final QualifiedName JFunctionalAgent = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"JFunctionalAgent");

		/**
		 * Java Iterator definitions.
		 */
		public static final QualifiedName JIterator = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"JIterator");

		/**
		 * Java Map definitions.
		 */
		public static final QualifiedName JMap = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"JMap");

		/**
		 * Java Map.Entry definitions.
		 */
		public static final QualifiedName JMap_Entry = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"JMap_Entry");

		/** Name binding for TypeConsApp: JModuleName. */
		public static final QualifiedName JModuleName = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"JModuleName");

		/** Name binding for TypeConsApp: JQualifiedName. */
		public static final QualifiedName JQualifiedName = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"JQualifiedName");

		/**
		 * Java Set definitions.
		 */
		public static final QualifiedName JSet = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"JSet");

		/**
		 * Java SortedMap definition
		 */
		public static final QualifiedName JSortedMap = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"JSortedMap");

		/**
		 * Represents literal values.
		 */
		public static final QualifiedName Literal = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"Literal");

		/** Name binding for TypeConsApp: Occurs. */
		public static final QualifiedName Occurs = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"Occurs");

		/**
		 * Represensts a qualified CAL name.
		 */
		public static final QualifiedName QualifiedName_ = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"QualifiedName");

		/** Name binding for TypeConsApp: Tags. */
		public static final QualifiedName Tags = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"Tags");

	}
	/**
	 * This inner class (DataConstructors) contains constants
	 * and methods related to binding to CAL DataConstructors in the Cal.Internal.Optimizer_Expression module.
	 */
	public static final class DataConstructors {
		/*
		 * DataConstructors for the Cal.Internal.Optimizer_Expression.Alt data type.
		 */

		/**
		 * Binding for DataConstructor: Cal.Internal.Optimizer_Expression.Alt.
		 * @param caseConst
		 * @param isPositional
		 * @param vars
		 * @param expr
		 * @return the SourceModule.Expr representing an application of Cal.Internal.Optimizer_Expression.Alt
		 */
		public static final SourceModel.Expr Alt(SourceModel.Expr caseConst, SourceModel.Expr isPositional, SourceModel.Expr vars, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.Alt), caseConst, isPositional, vars, expr});
		}

		/**
		 * @see #Alt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param caseConst
		 * @param isPositional
		 * @param vars
		 * @param expr
		 * @return org.openquark.cal.compiler.SourceModel.Expr
		 */
		public static final SourceModel.Expr Alt(SourceModel.Expr caseConst, boolean isPositional, SourceModel.Expr vars, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.Alt), caseConst, SourceModel.Expr.makeBooleanValue(isPositional), vars, expr});
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_Expression.Alt.
		 * @see #Alt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName Alt = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"Alt");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_Expression.Alt.
		 * @see #Alt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int Alt_ordinal = 0;

		/**
		 * Binding for DataConstructor: Cal.Internal.Optimizer_Expression.Alts.
		 * @param caseConst
		 * @param isPositional
		 * @param vars
		 * @param expr
		 * @return the SourceModule.Expr representing an application of Cal.Internal.Optimizer_Expression.Alts
		 */
		public static final SourceModel.Expr Alts(SourceModel.Expr caseConst, SourceModel.Expr isPositional, SourceModel.Expr vars, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.Alts), caseConst, isPositional, vars, expr});
		}

		/**
		 * @see #Alts(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param caseConst
		 * @param isPositional
		 * @param vars
		 * @param expr
		 * @return org.openquark.cal.compiler.SourceModel.Expr
		 */
		public static final SourceModel.Expr Alts(SourceModel.Expr caseConst, boolean isPositional, SourceModel.Expr vars, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.Alts), caseConst, SourceModel.Expr.makeBooleanValue(isPositional), vars, expr});
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_Expression.Alts.
		 * @see #Alts(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName Alts = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"Alts");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_Expression.Alts.
		 * @see #Alts(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int Alts_ordinal = 1;

		/*
		 * DataConstructors for the Cal.Internal.Optimizer_Expression.CaseConst data type.
		 */

		/**
		 * Binding for DataConstructor: Cal.Internal.Optimizer_Expression.CaseLiteral.
		 * @param value
		 * @return the SourceModule.Expr representing an application of Cal.Internal.Optimizer_Expression.CaseLiteral
		 */
		public static final SourceModel.Expr CaseLiteral(SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.CaseLiteral), value});
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_Expression.CaseLiteral.
		 * @see #CaseLiteral(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName CaseLiteral = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"CaseLiteral");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_Expression.CaseLiteral.
		 * @see #CaseLiteral(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int CaseLiteral_ordinal = 0;

		/**
		 * Binding for DataConstructor: Cal.Internal.Optimizer_Expression.CaseDataCons.
		 * @param value
		 * @return the SourceModule.Expr representing an application of Cal.Internal.Optimizer_Expression.CaseDataCons
		 */
		public static final SourceModel.Expr CaseDataCons(SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.CaseDataCons), value});
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_Expression.CaseDataCons.
		 * @see #CaseDataCons(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName CaseDataCons = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"CaseDataCons");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_Expression.CaseDataCons.
		 * @see #CaseDataCons(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int CaseDataCons_ordinal = 1;

		/*
		 * DataConstructors for the Cal.Internal.Optimizer_Expression.CoreFunction data type.
		 */

		/**
		 * 
		 * @param name (CAL type: <code>Cal.Internal.Optimizer_Expression.QualifiedName</code>)
		 *          The name of the core function.
		 * @param args (CAL type: <code>[Cal.Internal.Optimizer_Expression.QualifiedName]</code>)
		 *          The arguments of the core function.
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The expression that defines the core function.
		 * @param type (CAL type: <code>[Cal.Internal.Optimizer_Type.Type]</code>)
		 *          A list of the argument and return types of the core function.
		 * @param strictness (CAL type: <code>[Cal.Core.Prelude.Boolean]</code>)
		 *          The stricness of the type parameters
		 * @param argIsWHNF (CAL type: <code>[Cal.Core.Prelude.Boolean]</code>)
		 *          The argument is marked strict becuase the caller only passes arguments in WHNF
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr CoreFunction(SourceModel.Expr name, SourceModel.Expr args, SourceModel.Expr expr, SourceModel.Expr type, SourceModel.Expr strictness, SourceModel.Expr argIsWHNF) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.CoreFunction), name, args, expr, type, strictness, argIsWHNF});
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_Expression.CoreFunction.
		 * @see #CoreFunction(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName CoreFunction = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"CoreFunction");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_Expression.CoreFunction.
		 * @see #CoreFunction(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int CoreFunction_ordinal = 0;

		/*
		 * DataConstructors for the Cal.Internal.Optimizer_Expression.DataCons data type.
		 */

		/**
		 * Binding for DataConstructor: Cal.Internal.Optimizer_Expression.DataCons.
		 * @param name
		 * @param dataCons
		 * @return the SourceModule.Expr representing an application of Cal.Internal.Optimizer_Expression.DataCons
		 */
		public static final SourceModel.Expr DataCons(SourceModel.Expr name, SourceModel.Expr dataCons) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.DataCons), name, dataCons});
		}

		/**
		 * @see #DataCons(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param name
		 * @param dataCons
		 * @return org.openquark.cal.compiler.SourceModel.Expr
		 */
		public static final SourceModel.Expr DataCons(java.lang.String name, SourceModel.Expr dataCons) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.DataCons), SourceModel.Expr.makeStringValue(name), dataCons});
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_Expression.DataCons.
		 * @see #DataCons(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName DataCons = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"DataCons");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_Expression.DataCons.
		 * @see #DataCons(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int DataCons_ordinal = 0;

		/*
		 * DataConstructors for the Cal.Internal.Optimizer_Expression.Expression data type.
		 */

		/**
		 * Represents a varible object.
		 * @param name (CAL type: <code>Cal.Internal.Optimizer_Expression.QualifiedName</code>)
		 *          The name of the variable.
		 * @param inliningContext (CAL type: <code>[Cal.Internal.Optimizer_Expression.QualifiedName]</code>)
		 *          If the variable was part of an expression that was inlined then 
		 * this contains the name of the original expression. This is used to prevent inlining 
		 * of functions that are co recursive.
		 * @param entity (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Internal.Optimizer_Expression.JFunctionalAgent</code>)
		 * @param type (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Internal.Optimizer_Type.Type</code>)
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr Var(SourceModel.Expr name, SourceModel.Expr inliningContext, SourceModel.Expr entity, SourceModel.Expr type) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.Var), name, inliningContext, entity, type});
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_Expression.Var.
		 * @see #Var(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName Var = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"Var");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_Expression.Var.
		 * @see #Var(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int Var_ordinal = 0;

		/**
		 * Binding for DataConstructor: Cal.Internal.Optimizer_Expression.Literal.
		 * @param value
		 * @return the SourceModule.Expr representing an application of Cal.Internal.Optimizer_Expression.Literal
		 */
		public static final SourceModel.Expr Literal(SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.Literal), value});
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_Expression.Literal.
		 * @see #Literal(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName Literal = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"Literal");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_Expression.Literal.
		 * @see #Literal(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int Literal_ordinal = 1;

		/**
		 * Binding for DataConstructor: Cal.Internal.Optimizer_Expression.App.
		 * @param expr1
		 * @param expr2
		 * @return the SourceModule.Expr representing an application of Cal.Internal.Optimizer_Expression.App
		 */
		public static final SourceModel.Expr App(SourceModel.Expr expr1, SourceModel.Expr expr2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.App), expr1, expr2});
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_Expression.App.
		 * @see #App(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName App = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"App");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_Expression.App.
		 * @see #App(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int App_ordinal = 2;

		/**
		 * 
		 * @param var (CAL type: <code>Cal.Internal.Optimizer_Expression.QualifiedName</code>)
		 *          The name of the lambda variable.
		 * @param type (CAL type: <code>Cal.Internal.Optimizer_Type.Type</code>)
		 *          The type of the lambda variable.
		 * @param argIsWHNF (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 *          The strictness of the type is due to the argument know to be WHFN. This means when
		 * the core function is plinging arguments this argument being strict will not block higher up arguments
		 * from being plinged.
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          the body of the lambda expression.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr Lambda(SourceModel.Expr var, SourceModel.Expr type, SourceModel.Expr argIsWHNF, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.Lambda), var, type, argIsWHNF, expr});
		}

		/**
		 * @see #Lambda(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param var
		 * @param type
		 * @param argIsWHNF
		 * @param expr
		 * @return org.openquark.cal.compiler.SourceModel.Expr
		 */
		public static final SourceModel.Expr Lambda(SourceModel.Expr var, SourceModel.Expr type, boolean argIsWHNF, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.Lambda), var, type, SourceModel.Expr.makeBooleanValue(argIsWHNF), expr});
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_Expression.Lambda.
		 * @see #Lambda(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName Lambda = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"Lambda");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_Expression.Lambda.
		 * @see #Lambda(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int Lambda_ordinal = 3;

		/**
		 * 
		 * @param variable (CAL type: <code>Cal.Internal.Optimizer_Expression.QualifiedName</code>)
		 *          The name of the variable being defined.
		 * @param isNew (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 *          The let expression was created by fusion and should be added
		 * to the list of top level function in Java.
		 * @param isKeepable (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 *          The let expression was part of the original expression.
		 * The other two cases are that the function may have been added by the
		 * fusion transformation or the function may have been inserted so the
		 * inliner had access to the definition.
		 * @param isTopLevel (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 *          The function will be top level and so cannot use any context defined variables that
		 * are not top level.
		 * @param type (CAL type: <code>Cal.Core.Prelude.Maybe [Cal.Internal.Optimizer_Type.Type]</code>)
		 *          The types of the arguments and return value.
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The expression that defined the variable.
		 * @param body (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The 'in' expression.
		 * @param isRecursive (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 *          True iff the let expression should correspond to
		 * LetNonRec. Otherwise the corresponds to a LetRec expression.
		 * @param arity (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          The arity of the expression.
		 * @param isCoreFunction (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 *          A flag indicating if this let definition corresponds to a core function.
		 * @param constArgs (CAL type: <code>[Cal.Core.Prelude.Boolean]</code>)
		 *          The ordinals of the arguments that are constant through all recursive calls of the function.
		 * @param bodyWasChangedAt (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          Used to optimize the inliner so it will not attempt inlining when the function body has not changed.
		 * @param inliningWasPerformed (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 *          A flag so the inliner will not bother inlining if the body and expr has not changed.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr Let(SourceModel.Expr variable, SourceModel.Expr isNew, SourceModel.Expr isKeepable, SourceModel.Expr isTopLevel, SourceModel.Expr type, SourceModel.Expr expr, SourceModel.Expr body, SourceModel.Expr isRecursive, SourceModel.Expr arity, SourceModel.Expr isCoreFunction, SourceModel.Expr constArgs, SourceModel.Expr bodyWasChangedAt, SourceModel.Expr inliningWasPerformed) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.Let), variable, isNew, isKeepable, isTopLevel, type, expr, body, isRecursive, arity, isCoreFunction, constArgs, bodyWasChangedAt, inliningWasPerformed});
		}

		/**
		 * @see #Let(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param variable
		 * @param isNew
		 * @param isKeepable
		 * @param isTopLevel
		 * @param type
		 * @param expr
		 * @param body
		 * @param isRecursive
		 * @param arity
		 * @param isCoreFunction
		 * @param constArgs
		 * @param bodyWasChangedAt
		 * @param inliningWasPerformed
		 * @return org.openquark.cal.compiler.SourceModel.Expr
		 */
		public static final SourceModel.Expr Let(SourceModel.Expr variable, boolean isNew, boolean isKeepable, boolean isTopLevel, SourceModel.Expr type, SourceModel.Expr expr, SourceModel.Expr body, boolean isRecursive, int arity, boolean isCoreFunction, SourceModel.Expr constArgs, int bodyWasChangedAt, boolean inliningWasPerformed) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.Let), variable, SourceModel.Expr.makeBooleanValue(isNew), SourceModel.Expr.makeBooleanValue(isKeepable), SourceModel.Expr.makeBooleanValue(isTopLevel), type, expr, body, SourceModel.Expr.makeBooleanValue(isRecursive), SourceModel.Expr.makeIntValue(arity), SourceModel.Expr.makeBooleanValue(isCoreFunction), constArgs, SourceModel.Expr.makeIntValue(bodyWasChangedAt), SourceModel.Expr.makeBooleanValue(inliningWasPerformed)});
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_Expression.Let.
		 * @see #Let(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName Let = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"Let");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_Expression.Let.
		 * @see #Let(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int Let_ordinal = 4;

		/**
		 * Binding for DataConstructor: Cal.Internal.Optimizer_Expression.Switch.
		 * @param expr
		 * @param alts
		 * @return the SourceModule.Expr representing an application of Cal.Internal.Optimizer_Expression.Switch
		 */
		public static final SourceModel.Expr Switch(SourceModel.Expr expr, SourceModel.Expr alts) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.Switch), expr, alts});
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_Expression.Switch.
		 * @see #Switch(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName Switch = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"Switch");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_Expression.Switch.
		 * @see #Switch(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int Switch_ordinal = 5;

		/**
		 * Binding for DataConstructor: Cal.Internal.Optimizer_Expression.RecordCase.
		 * @param expr
		 * @param baseRecordPatternVarName
		 * @param fieldBindingVar
		 * @param resultExpr
		 * @return the SourceModule.Expr representing an application of Cal.Internal.Optimizer_Expression.RecordCase
		 */
		public static final SourceModel.Expr RecordCase(SourceModel.Expr expr, SourceModel.Expr baseRecordPatternVarName, SourceModel.Expr fieldBindingVar, SourceModel.Expr resultExpr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.RecordCase), expr, baseRecordPatternVarName, fieldBindingVar, resultExpr});
		}

		/**
		 * @see #RecordCase(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param expr
		 * @param baseRecordPatternVarName
		 * @param fieldBindingVar
		 * @param resultExpr
		 * @return org.openquark.cal.compiler.SourceModel.Expr
		 */
		public static final SourceModel.Expr RecordCase(SourceModel.Expr expr, java.lang.String baseRecordPatternVarName, SourceModel.Expr fieldBindingVar, SourceModel.Expr resultExpr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.RecordCase), expr, SourceModel.Expr.makeStringValue(baseRecordPatternVarName), fieldBindingVar, resultExpr});
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_Expression.RecordCase.
		 * @see #RecordCase(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName RecordCase = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"RecordCase");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_Expression.RecordCase.
		 * @see #RecordCase(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int RecordCase_ordinal = 6;

		/**
		 * Binding for DataConstructor: Cal.Internal.Optimizer_Expression.Opaque.
		 * @param value
		 * @return the SourceModule.Expr representing an application of Cal.Internal.Optimizer_Expression.Opaque
		 */
		public static final SourceModel.Expr Opaque(SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.Opaque), value});
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_Expression.Opaque.
		 * @see #Opaque(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName Opaque = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"Opaque");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_Expression.Opaque.
		 * @see #Opaque(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int Opaque_ordinal = 7;

		/**
		 * Binding for DataConstructor: Cal.Internal.Optimizer_Expression.RecordSelection.
		 * @param expr
		 * @param fieldName
		 * @return the SourceModule.Expr representing an application of Cal.Internal.Optimizer_Expression.RecordSelection
		 */
		public static final SourceModel.Expr RecordSelection(SourceModel.Expr expr, SourceModel.Expr fieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.RecordSelection), expr, fieldName});
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_Expression.RecordSelection.
		 * @see #RecordSelection(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName RecordSelection = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"RecordSelection");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_Expression.RecordSelection.
		 * @see #RecordSelection(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int RecordSelection_ordinal = 8;

		/**
		 * Binding for DataConstructor: Cal.Internal.Optimizer_Expression.RecordExtensionLiteral.
		 * @param fieldsMap
		 * @return the SourceModule.Expr representing an application of Cal.Internal.Optimizer_Expression.RecordExtensionLiteral
		 */
		public static final SourceModel.Expr RecordExtensionLiteral(SourceModel.Expr fieldsMap) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.RecordExtensionLiteral), fieldsMap});
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_Expression.RecordExtensionLiteral.
		 * @see #RecordExtensionLiteral(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName RecordExtensionLiteral = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"RecordExtensionLiteral");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_Expression.RecordExtensionLiteral.
		 * @see #RecordExtensionLiteral(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int RecordExtensionLiteral_ordinal = 9;

		/**
		 * Binding for DataConstructor: Cal.Internal.Optimizer_Expression.RecordExtensionPolymorphic.
		 * @param expr
		 * @param fieldsMap
		 * @return the SourceModule.Expr representing an application of Cal.Internal.Optimizer_Expression.RecordExtensionPolymorphic
		 */
		public static final SourceModel.Expr RecordExtensionPolymorphic(SourceModel.Expr expr, SourceModel.Expr fieldsMap) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.RecordExtensionPolymorphic), expr, fieldsMap});
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_Expression.RecordExtensionPolymorphic.
		 * @see #RecordExtensionPolymorphic(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName RecordExtensionPolymorphic = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"RecordExtensionPolymorphic");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_Expression.RecordExtensionPolymorphic.
		 * @see #RecordExtensionPolymorphic(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int RecordExtensionPolymorphic_ordinal = 10;

		/**
		 * Binding for DataConstructor: Cal.Internal.Optimizer_Expression.DataConsSelection.
		 * @param dcValueExpr
		 * @param dc
		 * @param fieldIndex
		 * @param errorInfo
		 * @return the SourceModule.Expr representing an application of Cal.Internal.Optimizer_Expression.DataConsSelection
		 */
		public static final SourceModel.Expr DataConsSelection(SourceModel.Expr dcValueExpr, SourceModel.Expr dc, SourceModel.Expr fieldIndex, SourceModel.Expr errorInfo) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.DataConsSelection), dcValueExpr, dc, fieldIndex, errorInfo});
		}

		/**
		 * @see #DataConsSelection(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param dcValueExpr
		 * @param dc
		 * @param fieldIndex
		 * @param errorInfo
		 * @return org.openquark.cal.compiler.SourceModel.Expr
		 */
		public static final SourceModel.Expr DataConsSelection(SourceModel.Expr dcValueExpr, SourceModel.Expr dc, int fieldIndex, SourceModel.Expr errorInfo) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.DataConsSelection), dcValueExpr, dc, SourceModel.Expr.makeIntValue(fieldIndex), errorInfo});
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_Expression.DataConsSelection.
		 * @see #DataConsSelection(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName DataConsSelection = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"DataConsSelection");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_Expression.DataConsSelection.
		 * @see #DataConsSelection(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int DataConsSelection_ordinal = 11;

		/**
		 * Binding for DataConstructor: Cal.Internal.Optimizer_Expression.DataConstructor.
		 * @param dc
		 * @return the SourceModule.Expr representing an application of Cal.Internal.Optimizer_Expression.DataConstructor
		 */
		public static final SourceModel.Expr DataConstructor(SourceModel.Expr dc) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.DataConstructor), dc});
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_Expression.DataConstructor.
		 * @see #DataConstructor(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName DataConstructor = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"DataConstructor");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_Expression.DataConstructor.
		 * @see #DataConstructor(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int DataConstructor_ordinal = 12;

		/**
		 * This is a recursive function that is flagged for inlining by the fusion code. Normally recursive functions
		 * would not be inlined.
		 * @param variable (CAL type: <code>Cal.Internal.Optimizer_Expression.QualifiedName</code>)
		 *          The name of the function being defined.
		 * @param type (CAL type: <code>Cal.Core.Prelude.Maybe [Cal.Internal.Optimizer_Type.Type]</code>)
		 *          The type of the expression.
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The expression that defined variable.
		 * @param body (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The 'in' expression.
		 * @param arity (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          The arity of expr.
		 * @param counter (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          testing
		 * @param constArgs (CAL type: <code>[Cal.Core.Prelude.Boolean]</code>)
		 *          For each argument there is a flag indicating if the argument is passed unchanged in recursive calls.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr LetInlinable(SourceModel.Expr variable, SourceModel.Expr type, SourceModel.Expr expr, SourceModel.Expr body, SourceModel.Expr arity, SourceModel.Expr counter, SourceModel.Expr constArgs) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.LetInlinable), variable, type, expr, body, arity, counter, constArgs});
		}

		/**
		 * @see #LetInlinable(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param variable
		 * @param type
		 * @param expr
		 * @param body
		 * @param arity
		 * @param counter
		 * @param constArgs
		 * @return org.openquark.cal.compiler.SourceModel.Expr
		 */
		public static final SourceModel.Expr LetInlinable(SourceModel.Expr variable, SourceModel.Expr type, SourceModel.Expr expr, SourceModel.Expr body, int arity, int counter, SourceModel.Expr constArgs) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.LetInlinable), variable, type, expr, body, SourceModel.Expr.makeIntValue(arity), SourceModel.Expr.makeIntValue(counter), constArgs});
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_Expression.LetInlinable.
		 * @see #LetInlinable(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName LetInlinable = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"LetInlinable");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_Expression.LetInlinable.
		 * @see #LetInlinable(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int LetInlinable_ordinal = 13;

		/**
		 * Binding for DataConstructor: Cal.Internal.Optimizer_Expression.ChainOfSeqs.
		 * @param seqs
		 * @param expression
		 * @return the SourceModule.Expr representing an application of Cal.Internal.Optimizer_Expression.ChainOfSeqs
		 */
		public static final SourceModel.Expr ChainOfSeqs(SourceModel.Expr seqs, SourceModel.Expr expression) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.ChainOfSeqs), seqs, expression});
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_Expression.ChainOfSeqs.
		 * @see #ChainOfSeqs(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName ChainOfSeqs = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"ChainOfSeqs");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_Expression.ChainOfSeqs.
		 * @see #ChainOfSeqs(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int ChainOfSeqs_ordinal = 14;

		/**
		 * Binding for DataConstructor: Cal.Internal.Optimizer_Expression.ErrorInfo.
		 * @param value
		 * @return the SourceModule.Expr representing an application of Cal.Internal.Optimizer_Expression.ErrorInfo
		 */
		public static final SourceModel.Expr ErrorInfo(SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.ErrorInfo), value});
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_Expression.ErrorInfo.
		 * @see #ErrorInfo(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName ErrorInfo = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"ErrorInfo");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_Expression.ErrorInfo.
		 * @see #ErrorInfo(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int ErrorInfo_ordinal = 15;

		/*
		 * DataConstructors for the Cal.Internal.Optimizer_Expression.FieldName data type.
		 */

		/**
		 * Binding for DataConstructor: Cal.Internal.Optimizer_Expression.FNOrdinal.
		 * @param ordinal
		 * @return the SourceModule.Expr representing an application of Cal.Internal.Optimizer_Expression.FNOrdinal
		 */
		public static final SourceModel.Expr FNOrdinal(SourceModel.Expr ordinal) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.FNOrdinal), ordinal});
		}

		/**
		 * @see #FNOrdinal(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param ordinal
		 * @return org.openquark.cal.compiler.SourceModel.Expr
		 */
		public static final SourceModel.Expr FNOrdinal(int ordinal) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.FNOrdinal), SourceModel.Expr.makeIntValue(ordinal)});
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_Expression.FNOrdinal.
		 * @see #FNOrdinal(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName FNOrdinal = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"FNOrdinal");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_Expression.FNOrdinal.
		 * @see #FNOrdinal(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int FNOrdinal_ordinal = 0;

		/**
		 * Binding for DataConstructor: Cal.Internal.Optimizer_Expression.FNTextual.
		 * @param name
		 * @return the SourceModule.Expr representing an application of Cal.Internal.Optimizer_Expression.FNTextual
		 */
		public static final SourceModel.Expr FNTextual(SourceModel.Expr name) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.FNTextual), name});
		}

		/**
		 * @see #FNTextual(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param name
		 * @return org.openquark.cal.compiler.SourceModel.Expr
		 */
		public static final SourceModel.Expr FNTextual(java.lang.String name) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.FNTextual), SourceModel.Expr.makeStringValue(name)});
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_Expression.FNTextual.
		 * @see #FNTextual(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName FNTextual = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"FNTextual");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_Expression.FNTextual.
		 * @see #FNTextual(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int FNTextual_ordinal = 1;

		/*
		 * DataConstructors for the Cal.Internal.Optimizer_Expression.Literal data type.
		 */

		/**
		 * Binding for DataConstructor: Cal.Internal.Optimizer_Expression.LitString.
		 * @param value
		 * @return the SourceModule.Expr representing an application of Cal.Internal.Optimizer_Expression.LitString
		 */
		public static final SourceModel.Expr LitString(SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.LitString), value});
		}

		/**
		 * @see #LitString(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value
		 * @return org.openquark.cal.compiler.SourceModel.Expr
		 */
		public static final SourceModel.Expr LitString(java.lang.String value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.LitString), SourceModel.Expr.makeStringValue(value)});
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_Expression.LitString.
		 * @see #LitString(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName LitString = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"LitString");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_Expression.LitString.
		 * @see #LitString(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int LitString_ordinal = 0;

		/**
		 * Binding for DataConstructor: Cal.Internal.Optimizer_Expression.LitInt.
		 * @param value
		 * @return the SourceModule.Expr representing an application of Cal.Internal.Optimizer_Expression.LitInt
		 */
		public static final SourceModel.Expr LitInt(SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.LitInt), value});
		}

		/**
		 * @see #LitInt(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value
		 * @return org.openquark.cal.compiler.SourceModel.Expr
		 */
		public static final SourceModel.Expr LitInt(int value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.LitInt), SourceModel.Expr.makeIntValue(value)});
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_Expression.LitInt.
		 * @see #LitInt(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName LitInt = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"LitInt");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_Expression.LitInt.
		 * @see #LitInt(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int LitInt_ordinal = 1;

		/**
		 * Binding for DataConstructor: Cal.Internal.Optimizer_Expression.LitShort.
		 * @param value
		 * @return the SourceModule.Expr representing an application of Cal.Internal.Optimizer_Expression.LitShort
		 */
		public static final SourceModel.Expr LitShort(SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.LitShort), value});
		}

		/**
		 * @see #LitShort(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value
		 * @return org.openquark.cal.compiler.SourceModel.Expr
		 */
		public static final SourceModel.Expr LitShort(short value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.LitShort), SourceModel.Expr.makeShortValue(value)});
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_Expression.LitShort.
		 * @see #LitShort(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName LitShort = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"LitShort");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_Expression.LitShort.
		 * @see #LitShort(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int LitShort_ordinal = 2;

		/**
		 * Binding for DataConstructor: Cal.Internal.Optimizer_Expression.LitFloat.
		 * @param value
		 * @return the SourceModule.Expr representing an application of Cal.Internal.Optimizer_Expression.LitFloat
		 */
		public static final SourceModel.Expr LitFloat(SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.LitFloat), value});
		}

		/**
		 * @see #LitFloat(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value
		 * @return org.openquark.cal.compiler.SourceModel.Expr
		 */
		public static final SourceModel.Expr LitFloat(float value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.LitFloat), SourceModel.Expr.makeFloatValue(value)});
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_Expression.LitFloat.
		 * @see #LitFloat(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName LitFloat = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"LitFloat");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_Expression.LitFloat.
		 * @see #LitFloat(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int LitFloat_ordinal = 3;

		/**
		 * Binding for DataConstructor: Cal.Internal.Optimizer_Expression.LitInteger.
		 * @param value
		 * @return the SourceModule.Expr representing an application of Cal.Internal.Optimizer_Expression.LitInteger
		 */
		public static final SourceModel.Expr LitInteger(SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.LitInteger), value});
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_Expression.LitInteger.
		 * @see #LitInteger(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName LitInteger = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"LitInteger");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_Expression.LitInteger.
		 * @see #LitInteger(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int LitInteger_ordinal = 4;

		/**
		 * Binding for DataConstructor: Cal.Internal.Optimizer_Expression.LitBoolean.
		 * @param value
		 * @return the SourceModule.Expr representing an application of Cal.Internal.Optimizer_Expression.LitBoolean
		 */
		public static final SourceModel.Expr LitBoolean(SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.LitBoolean), value});
		}

		/**
		 * @see #LitBoolean(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value
		 * @return org.openquark.cal.compiler.SourceModel.Expr
		 */
		public static final SourceModel.Expr LitBoolean(boolean value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.LitBoolean), SourceModel.Expr.makeBooleanValue(value)});
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_Expression.LitBoolean.
		 * @see #LitBoolean(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName LitBoolean = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"LitBoolean");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_Expression.LitBoolean.
		 * @see #LitBoolean(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int LitBoolean_ordinal = 5;

		/**
		 * Binding for DataConstructor: Cal.Internal.Optimizer_Expression.LitDouble.
		 * @param value
		 * @return the SourceModule.Expr representing an application of Cal.Internal.Optimizer_Expression.LitDouble
		 */
		public static final SourceModel.Expr LitDouble(SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.LitDouble), value});
		}

		/**
		 * @see #LitDouble(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value
		 * @return org.openquark.cal.compiler.SourceModel.Expr
		 */
		public static final SourceModel.Expr LitDouble(double value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.LitDouble), SourceModel.Expr.makeDoubleValue(value)});
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_Expression.LitDouble.
		 * @see #LitDouble(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName LitDouble = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"LitDouble");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_Expression.LitDouble.
		 * @see #LitDouble(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int LitDouble_ordinal = 6;

		/**
		 * Binding for DataConstructor: Cal.Internal.Optimizer_Expression.LitChar.
		 * @param value
		 * @return the SourceModule.Expr representing an application of Cal.Internal.Optimizer_Expression.LitChar
		 */
		public static final SourceModel.Expr LitChar(SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.LitChar), value});
		}

		/**
		 * @see #LitChar(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value
		 * @return org.openquark.cal.compiler.SourceModel.Expr
		 */
		public static final SourceModel.Expr LitChar(char value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.LitChar), SourceModel.Expr.makeCharValue(value)});
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_Expression.LitChar.
		 * @see #LitChar(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName LitChar = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"LitChar");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_Expression.LitChar.
		 * @see #LitChar(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int LitChar_ordinal = 7;

		/**
		 * Binding for DataConstructor: Cal.Internal.Optimizer_Expression.LitOpaque.
		 * @param value
		 * @return the SourceModule.Expr representing an application of Cal.Internal.Optimizer_Expression.LitOpaque
		 */
		public static final SourceModel.Expr LitOpaque(SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.LitOpaque), value});
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_Expression.LitOpaque.
		 * @see #LitOpaque(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName LitOpaque = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"LitOpaque");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_Expression.LitOpaque.
		 * @see #LitOpaque(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int LitOpaque_ordinal = 8;

		/**
		 * Binding for DataConstructor: Cal.Internal.Optimizer_Expression.LitByte.
		 * @param value
		 * @return the SourceModule.Expr representing an application of Cal.Internal.Optimizer_Expression.LitByte
		 */
		public static final SourceModel.Expr LitByte(SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.LitByte), value});
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_Expression.LitByte.
		 * @see #LitByte(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName LitByte = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"LitByte");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_Expression.LitByte.
		 * @see #LitByte(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int LitByte_ordinal = 9;

		/**
		 * Binding for DataConstructor: Cal.Internal.Optimizer_Expression.LitLong.
		 * @param value
		 * @return the SourceModule.Expr representing an application of Cal.Internal.Optimizer_Expression.LitLong
		 */
		public static final SourceModel.Expr LitLong(SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.LitLong), value});
		}

		/**
		 * @see #LitLong(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value
		 * @return org.openquark.cal.compiler.SourceModel.Expr
		 */
		public static final SourceModel.Expr LitLong(long value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.LitLong), SourceModel.Expr.makeLongValue(value)});
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_Expression.LitLong.
		 * @see #LitLong(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName LitLong = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"LitLong");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_Expression.LitLong.
		 * @see #LitLong(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int LitLong_ordinal = 10;

		/*
		 * DataConstructors for the Cal.Internal.Optimizer_Expression.Occurs data type.
		 */

		/**
		 * Binding for DataConstructor: Cal.Internal.Optimizer_Expression.Occurs_ZeroTimes.
		 * @return the SourceModule.Expr representing an application of Cal.Internal.Optimizer_Expression.Occurs_ZeroTimes
		 */
		public static final SourceModel.Expr Occurs_ZeroTimes() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.Occurs_ZeroTimes);
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_Expression.Occurs_ZeroTimes.
		 * @see #Occurs_ZeroTimes()
		 */
		public static final QualifiedName Occurs_ZeroTimes = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"Occurs_ZeroTimes");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_Expression.Occurs_ZeroTimes.
		 * @see #Occurs_ZeroTimes()
		 */
		public static final int Occurs_ZeroTimes_ordinal = 0;

		/**
		 * Binding for DataConstructor: Cal.Internal.Optimizer_Expression.Occurs_Once.
		 * @return the SourceModule.Expr representing an application of Cal.Internal.Optimizer_Expression.Occurs_Once
		 */
		public static final SourceModel.Expr Occurs_Once() {
			return SourceModel.Expr.DataCons.make(DataConstructors.Occurs_Once);
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_Expression.Occurs_Once.
		 * @see #Occurs_Once()
		 */
		public static final QualifiedName Occurs_Once = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"Occurs_Once");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_Expression.Occurs_Once.
		 * @see #Occurs_Once()
		 */
		public static final int Occurs_Once_ordinal = 1;

		/**
		 * Binding for DataConstructor: Cal.Internal.Optimizer_Expression.Occurs_MoreThanOnce.
		 * @return the SourceModule.Expr representing an application of Cal.Internal.Optimizer_Expression.Occurs_MoreThanOnce
		 */
		public static final SourceModel.Expr Occurs_MoreThanOnce() {
			return 
				SourceModel.Expr.DataCons.make(
					DataConstructors.Occurs_MoreThanOnce);
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_Expression.Occurs_MoreThanOnce.
		 * @see #Occurs_MoreThanOnce()
		 */
		public static final QualifiedName Occurs_MoreThanOnce = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"Occurs_MoreThanOnce");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_Expression.Occurs_MoreThanOnce.
		 * @see #Occurs_MoreThanOnce()
		 */
		public static final int Occurs_MoreThanOnce_ordinal = 2;

		/*
		 * DataConstructors for the Cal.Internal.Optimizer_Expression.QualifiedName data type.
		 */

		/**
		 * Binding for DataConstructor: Cal.Internal.Optimizer_Expression.QN.
		 * @param moduleName
		 * @param functionName
		 * @return the SourceModule.Expr representing an application of Cal.Internal.Optimizer_Expression.QN
		 */
		public static final SourceModel.Expr QN(SourceModel.Expr moduleName, SourceModel.Expr functionName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.QN), moduleName, functionName});
		}

		/**
		 * @see #QN(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param moduleName
		 * @param functionName
		 * @return org.openquark.cal.compiler.SourceModel.Expr
		 */
		public static final SourceModel.Expr QN(java.lang.String moduleName, java.lang.String functionName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.QN), SourceModel.Expr.makeStringValue(moduleName), SourceModel.Expr.makeStringValue(functionName)});
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_Expression.QN.
		 * @see #QN(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName QN = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"QN");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_Expression.QN.
		 * @see #QN(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int QN_ordinal = 0;

		/*
		 * DataConstructors for the Cal.Internal.Optimizer_Expression.Tags data type.
		 */

		/**
		 * Used for containing meta-information about the expression.
		 * @param inlinedFrom (CAL type: <code>Cal.Internal.Optimizer_Expression.QualifiedName</code>)
		 *          The subexpression came from inlining the given expression. 
		 * This is used to stop inlining is co-recursive function.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr Tags(SourceModel.Expr inlinedFrom) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.Tags), inlinedFrom});
		}

		/**
		 * Name binding for DataConstructor: Cal.Internal.Optimizer_Expression.Tags.
		 * @see #Tags(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName Tags = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"Tags");

		/**
		 * Ordinal of DataConstructor Cal.Internal.Optimizer_Expression.Tags.
		 * @see #Tags(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int Tags_ordinal = 0;

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Internal.Optimizer_Expression module.
	 */
	public static final class Functions {
		/**
		 * Take the expression and embed it in a lambda expression 
		 * on the given variables.
		 * @param vars (CAL type: <code>[(Cal.Internal.Optimizer_Expression.QualifiedName, Cal.Internal.Optimizer_Type.Type)]</code>)
		 *          The lambda variables.
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The expression to embed.
		 * @return (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>) 
		 *          The new lambda expression.
		 */
		public static final SourceModel.Expr addLambdaVars(SourceModel.Expr vars, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.addLambdaVars), vars, expr});
		}

		/**
		 * Name binding for function: addLambdaVars.
		 * @see #addLambdaVars(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName addLambdaVars = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"addLambdaVars");

		/**
		 * Helper binding method for function: addOccurs. 
		 * @param o1
		 * @param o2
		 * @return the SourceModule.expr representing an application of addOccurs
		 */
		public static final SourceModel.Expr addOccurs(SourceModel.Expr o1, SourceModel.Expr o2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.addOccurs), o1, o2});
		}

		/**
		 * Name binding for function: addOccurs.
		 * @see #addOccurs(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName addOccurs = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"addOccurs");

		/**
		 * Helper function for inputing AltTags
		 * @param jAltTag (CAL type: <code>Cal.Core.Prelude.JObject</code>)
		 * @return (CAL type: <code>Cal.Internal.Optimizer_Expression.CaseConst</code>) 
		 */
		public static final SourceModel.Expr altTagToCaseConst(SourceModel.Expr jAltTag) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.altTagToCaseConst), jAltTag});
		}

		/**
		 * Name binding for function: altTagToCaseConst.
		 * @see #altTagToCaseConst(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName altTagToCaseConst = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"altTagToCaseConst");

		/**
		 * Helper function for inputting switch alts.
		 * @param jAlt (CAL type: <code>Cal.Internal.Optimizer_Expression.JExpression_Switch_SwitchAlt</code>)
		 * @param jEnvEntity (CAL type: <code>Cal.Internal.Optimizer_Expression.JFunctionalAgent</code>)
		 * @return (CAL type: <code>Cal.Internal.Optimizer_Expression.CaseConst</code>) 
		 */
		public static final SourceModel.Expr altToCaseConst(SourceModel.Expr jAlt, SourceModel.Expr jEnvEntity) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.altToCaseConst), jAlt, jEnvEntity});
		}

		/**
		 * Name binding for function: altToCaseConst.
		 * @see #altToCaseConst(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName altToCaseConst = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"altToCaseConst");

		/**
		 * Helper binding method for function: altToJObject. 
		 * @param qn
		 * @return the SourceModule.expr representing an application of altToJObject
		 */
		public static final SourceModel.Expr altToJObject(SourceModel.Expr qn) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.altToJObject), qn});
		}

		/**
		 * Name binding for function: altToJObject.
		 * @see #altToJObject(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName altToJObject = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"altToJObject");

		/**
		 * Get the expression of a given case alt.
		 * @param alt (CAL type: <code>Cal.Internal.Optimizer_Expression.Alt</code>)
		 * @return (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>) 
		 */
		public static final SourceModel.Expr alt_getExpr(SourceModel.Expr alt) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.alt_getExpr), alt});
		}

		/**
		 * Name binding for function: alt_getExpr.
		 * @see #alt_getExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName alt_getExpr = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"alt_getExpr");

		/**
		 * Helper binding method for function: buildCaseConstList. 
		 * @param jIterator
		 * @return the SourceModule.expr representing an application of buildCaseConstList
		 */
		public static final SourceModel.Expr buildCaseConstList(SourceModel.Expr jIterator) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.buildCaseConstList), jIterator});
		}

		/**
		 * Name binding for function: buildCaseConstList.
		 * @see #buildCaseConstList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName buildCaseConstList = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"buildCaseConstList");

		/**
		 * Helper function for converting an iterator over a map of field names to qualified names 
		 * into a CAL data type.
		 * @param jIterator (CAL type: <code>Cal.Internal.Optimizer_Expression.JIterator</code>)
		 * @return (CAL type: <code>[(Cal.Internal.Optimizer_Expression.FieldName, Cal.Internal.Optimizer_Expression.QualifiedName, Cal.Internal.Optimizer_Type.Type)]</code>) 
		 */
		public static final SourceModel.Expr buildFieldNameToQualifiedNameList(SourceModel.Expr jIterator) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.buildFieldNameToQualifiedNameList), jIterator});
		}

		/**
		 * Name binding for function: buildFieldNameToQualifiedNameList.
		 * @see #buildFieldNameToQualifiedNameList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName buildFieldNameToQualifiedNameList = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"buildFieldNameToQualifiedNameList");

		/**
		 * TODO pick a better name
		 * @param jIterator (CAL type: <code>Cal.Internal.Optimizer_Expression.JIterator</code>)
		 * @return (CAL type: <code>[(Cal.Internal.Optimizer_Expression.FieldName, Cal.Internal.Optimizer_Expression.QualifiedName, Cal.Internal.Optimizer_Type.Type)]</code>) 
		 */
		public static final SourceModel.Expr buildFieldNameToQualifiedNameList2(SourceModel.Expr jIterator) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.buildFieldNameToQualifiedNameList2), jIterator});
		}

		/**
		 * Name binding for function: buildFieldNameToQualifiedNameList2.
		 * @see #buildFieldNameToQualifiedNameList2(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName buildFieldNameToQualifiedNameList2 = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"buildFieldNameToQualifiedNameList2");

		/**
		 * Helper functions for converting an iterator over FieldName's to a list of CAL field names.
		 * @param jIterator (CAL type: <code>Cal.Internal.Optimizer_Expression.JIterator</code>)
		 * @return (CAL type: <code>[(Cal.Internal.Optimizer_Expression.FieldName, Cal.Core.Prelude.String)]</code>) 
		 */
		public static final SourceModel.Expr buildFieldNameToStringList(SourceModel.Expr jIterator) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.buildFieldNameToStringList), jIterator});
		}

		/**
		 * Name binding for function: buildFieldNameToStringList.
		 * @see #buildFieldNameToStringList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName buildFieldNameToStringList = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"buildFieldNameToStringList");

		/**
		 * Helper binding method for function: caseConst_getType. 
		 * @param caseConst
		 * @return the SourceModule.expr representing an application of caseConst_getType
		 */
		public static final SourceModel.Expr caseConst_getType(SourceModel.Expr caseConst) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.caseConst_getType), caseConst});
		}

		/**
		 * Name binding for function: caseConst_getType.
		 * @see #caseConst_getType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName caseConst_getType = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"caseConst_getType");

		/**
		 * Helper binding method for function: compiler_RecordType_RecordType_getHasFieldType. 
		 * @param jCompiler_RecordType_RecordType
		 * @param fieldName
		 * @return the SourceModule.expr representing an application of compiler_RecordType_RecordType_getHasFieldType
		 */
		public static final SourceModel.Expr compiler_RecordType_RecordType_getHasFieldType(SourceModel.Expr jCompiler_RecordType_RecordType, SourceModel.Expr fieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.compiler_RecordType_RecordType_getHasFieldType), jCompiler_RecordType_RecordType, fieldName});
		}

		/**
		 * Name binding for function: compiler_RecordType_RecordType_getHasFieldType.
		 * @see #compiler_RecordType_RecordType_getHasFieldType(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName compiler_RecordType_RecordType_getHasFieldType = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"compiler_RecordType_RecordType_getHasFieldType");

		/**
		 * Returns true iff then given expression contains the given variable. 
		 * The variable must be may or may not be free in expr.
		 * @param var (CAL type: <code>Cal.Internal.Optimizer_Expression.QualifiedName</code>)
		 *          The variable to look for in the given expression.
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The expression to search for the given variable.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          True iff var is in expr.
		 */
		public static final SourceModel.Expr contains(SourceModel.Expr var, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.contains), var, expr});
		}

		/**
		 * Name binding for function: contains.
		 * @see #contains(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName contains = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"contains");

		/**
		 * Does the given list of alts contains an alt that uses the given name.
		 * @param var (CAL type: <code>Cal.Internal.Optimizer_Expression.QualifiedName</code>)
		 *          The name to search for
		 * @param alts (CAL type: <code>[Cal.Internal.Optimizer_Expression.Alt]</code>)
		 *          The list of alts to search
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          True iff the an alt in the list of alts contains the given var.
		 */
		public static final SourceModel.Expr containsAlt(SourceModel.Expr var, SourceModel.Expr alts) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.containsAlt), var, alts});
		}

		/**
		 * Name binding for function: containsAlt.
		 * @see #containsAlt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName containsAlt = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"containsAlt");

		/**
		 * Helper binding method for function: containsAnyAlt. 
		 * @param vars
		 * @param alts
		 * @return the SourceModule.expr representing an application of containsAnyAlt
		 */
		public static final SourceModel.Expr containsAnyAlt(SourceModel.Expr vars, SourceModel.Expr alts) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.containsAnyAlt), vars, alts});
		}

		/**
		 * Name binding for function: containsAnyAlt.
		 * @see #containsAnyAlt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName containsAnyAlt = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"containsAnyAlt");

		/**
		 * Returns true iff then given expression contains the given variable. 
		 * The variable must be free in expr.
		 * @param vars (CAL type: <code>[Cal.Internal.Optimizer_Expression.QualifiedName]</code>)
		 *          The variables to look for in the given expression.
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The expression to search for the given variable.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          True iff var is in expr.
		 */
		public static final SourceModel.Expr containsAnyFree(SourceModel.Expr vars, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.containsAnyFree), vars, expr});
		}

		/**
		 * Name binding for function: containsAnyFree.
		 * @see #containsAnyFree(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName containsAnyFree = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"containsAnyFree");

		/**
		 * Returns true iff then given expression contains the given variable. 
		 * The variable must be free in expr.
		 * @param var (CAL type: <code>Cal.Internal.Optimizer_Expression.QualifiedName</code>)
		 *          The variable to look for in the given expression.
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The expression to search for the given variable.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          True iff var is in expr.
		 */
		public static final SourceModel.Expr containsFree(SourceModel.Expr var, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.containsFree), var, expr});
		}

		/**
		 * Name binding for function: containsFree.
		 * @see #containsFree(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName containsFree = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"containsFree");

		/**
		 * Returns true iff then given expression contains the given variable. 
		 * The variable must be free in expr.
		 * <p>
		 * TODO pick a better name
		 * 
		 * @param test (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression -> Cal.Core.Prelude.Boolean</code>)
		 *          The test to look for in the given expression.
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The expression to search for the given variable.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          True iff var is in expr.
		 */
		public static final SourceModel.Expr containsMatching(SourceModel.Expr test, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.containsMatching), test, expr});
		}

		/**
		 * Name binding for function: containsMatching.
		 * @see #containsMatching(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName containsMatching = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"containsMatching");

		/**
		 * Convert the given Alt to a positional alt. May result in no change if the Alt already is positional.
		 * @param alt (CAL type: <code>Cal.Internal.Optimizer_Expression.Alt</code>)
		 * @return (CAL type: <code>Cal.Internal.Optimizer_Expression.Alt</code>) 
		 */
		public static final SourceModel.Expr convertToPositional(SourceModel.Expr alt) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.convertToPositional), alt});
		}

		/**
		 * Name binding for function: convertToPositional.
		 * @see #convertToPositional(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName convertToPositional = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"convertToPositional");

		/**
		 * Helper binding method for function: dataCons_getArity. 
		 * @param dc
		 * @return the SourceModule.expr representing an application of dataCons_getArity
		 */
		public static final SourceModel.Expr dataCons_getArity(SourceModel.Expr dc) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.dataCons_getArity), dc});
		}

		/**
		 * Name binding for function: dataCons_getArity.
		 * @see #dataCons_getArity(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName dataCons_getArity = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"dataCons_getArity");

		/**
		 * Helper binding method for function: dataCons_getFieldIndex. 
		 * @param dc
		 * @param fieldName
		 * @return the SourceModule.expr representing an application of dataCons_getFieldIndex
		 */
		public static final SourceModel.Expr dataCons_getFieldIndex(SourceModel.Expr dc, SourceModel.Expr fieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.dataCons_getFieldIndex), dc, fieldName});
		}

		/**
		 * Name binding for function: dataCons_getFieldIndex.
		 * @see #dataCons_getFieldIndex(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName dataCons_getFieldIndex = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"dataCons_getFieldIndex");

		/**
		 * Helper binding method for function: dataCons_getNumberOfTypes. 
		 * @param dc
		 * @return the SourceModule.expr representing an application of dataCons_getNumberOfTypes
		 */
		public static final SourceModel.Expr dataCons_getNumberOfTypes(SourceModel.Expr dc) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.dataCons_getNumberOfTypes), dc});
		}

		/**
		 * Name binding for function: dataCons_getNumberOfTypes.
		 * @see #dataCons_getNumberOfTypes(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName dataCons_getNumberOfTypes = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"dataCons_getNumberOfTypes");

		/**
		 * Helper binding method for function: dataCons_getOrdinal. 
		 * @param dc
		 * @return the SourceModule.expr representing an application of dataCons_getOrdinal
		 */
		public static final SourceModel.Expr dataCons_getOrdinal(SourceModel.Expr dc) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.dataCons_getOrdinal), dc});
		}

		/**
		 * Name binding for function: dataCons_getOrdinal.
		 * @see #dataCons_getOrdinal(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName dataCons_getOrdinal = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"dataCons_getOrdinal");

		/**
		 * Get the type of the given data constructor.
		 * @param dc (CAL type: <code>Cal.Internal.Optimizer_Expression.DataCons</code>)
		 *          The data constructor to get the type of.
		 * @return (CAL type: <code>Cal.Internal.Optimizer_Type.Type</code>) 
		 *          The type of the given data constructor.
		 */
		public static final SourceModel.Expr dataCons_getType(SourceModel.Expr dc) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.dataCons_getType), dc});
		}

		/**
		 * Name binding for function: dataCons_getType.
		 * @see #dataCons_getType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName dataCons_getType = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"dataCons_getType");

		/**
		 * Is the given data constructor strict on the given argument.
		 * @param dc (CAL type: <code>Cal.Internal.Optimizer_Expression.DataCons</code>)
		 * @param index (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr dataCons_isStrict(SourceModel.Expr dc, SourceModel.Expr index) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.dataCons_isStrict), dc, index});
		}

		/**
		 * @see #dataCons_isStrict(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param dc
		 * @param index
		 * @return the SourceModel.Expr representing an application of dataCons_isStrict
		 */
		public static final SourceModel.Expr dataCons_isStrict(SourceModel.Expr dc, int index) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.dataCons_isStrict), dc, SourceModel.Expr.makeIntValue(index)});
		}

		/**
		 * Name binding for function: dataCons_isStrict.
		 * @see #dataCons_isStrict(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName dataCons_isStrict = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"dataCons_isStrict");

		/**
		 * Check if the given data constructor is either true or false.
		 * @param dc (CAL type: <code>Cal.Internal.Optimizer_Expression.DataCons</code>)
		 *          The data constructor to check the value of
		 * @param value (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 *          The value to see if the data constructor matches.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          True iff the data constructor is either Prelude.True or Prelude.False corresponding to the match value.
		 */
		public static final SourceModel.Expr dataCons_matches(SourceModel.Expr dc, SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.dataCons_matches), dc, value});
		}

		/**
		 * @see #dataCons_matches(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param dc
		 * @param value
		 * @return the SourceModel.Expr representing an application of dataCons_matches
		 */
		public static final SourceModel.Expr dataCons_matches(SourceModel.Expr dc, boolean value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.dataCons_matches), dc, SourceModel.Expr.makeBooleanValue(value)});
		}

		/**
		 * Name binding for function: dataCons_matches.
		 * @see #dataCons_matches(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName dataCons_matches = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"dataCons_matches");

		/**
		 * Helper binding method for function: dataConstructor_getArity. 
		 * @param jDataConstructor
		 * @return the SourceModule.expr representing an application of dataConstructor_getArity
		 */
		public static final SourceModel.Expr dataConstructor_getArity(SourceModel.Expr jDataConstructor) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.dataConstructor_getArity), jDataConstructor});
		}

		/**
		 * Name binding for function: dataConstructor_getArity.
		 * @see #dataConstructor_getArity(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName dataConstructor_getArity = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"dataConstructor_getArity");

		/**
		 * Helper binding method for function: dataConstructor_getFieldIndex. 
		 * @param jDataConstructor
		 * @param fieldName
		 * @return the SourceModule.expr representing an application of dataConstructor_getFieldIndex
		 */
		public static final SourceModel.Expr dataConstructor_getFieldIndex(SourceModel.Expr jDataConstructor, SourceModel.Expr fieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.dataConstructor_getFieldIndex), jDataConstructor, fieldName});
		}

		/**
		 * Name binding for function: dataConstructor_getFieldIndex.
		 * @see #dataConstructor_getFieldIndex(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName dataConstructor_getFieldIndex = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"dataConstructor_getFieldIndex");

		/**
		 * Helper binding method for function: dataConstructor_getOrdinal. 
		 * @param jDataConstructor
		 * @return the SourceModule.expr representing an application of dataConstructor_getOrdinal
		 */
		public static final SourceModel.Expr dataConstructor_getOrdinal(SourceModel.Expr jDataConstructor) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.dataConstructor_getOrdinal), jDataConstructor});
		}

		/**
		 * Name binding for function: dataConstructor_getOrdinal.
		 * @see #dataConstructor_getOrdinal(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName dataConstructor_getOrdinal = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"dataConstructor_getOrdinal");

		/**
		 * Helper binding method for function: dataConstructor_isArgStrict. 
		 * @param jDataConstructor
		 * @param argN
		 * @return the SourceModule.expr representing an application of dataConstructor_isArgStrict
		 */
		public static final SourceModel.Expr dataConstructor_isArgStrict(SourceModel.Expr jDataConstructor, SourceModel.Expr argN) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.dataConstructor_isArgStrict), jDataConstructor, argN});
		}

		/**
		 * @see #dataConstructor_isArgStrict(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param jDataConstructor
		 * @param argN
		 * @return the SourceModel.Expr representing an application of dataConstructor_isArgStrict
		 */
		public static final SourceModel.Expr dataConstructor_isArgStrict(SourceModel.Expr jDataConstructor, int argN) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.dataConstructor_isArgStrict), jDataConstructor, SourceModel.Expr.makeIntValue(argN)});
		}

		/**
		 * Name binding for function: dataConstructor_isArgStrict.
		 * @see #dataConstructor_isArgStrict(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName dataConstructor_isArgStrict = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"dataConstructor_isArgStrict");

		/**
		 * Helper binding method for function: envEntity_getName. 
		 * @param jFunctionalAgent
		 * @return the SourceModule.expr representing an application of envEntity_getName
		 */
		public static final SourceModel.Expr envEntity_getName(SourceModel.Expr jFunctionalAgent) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.envEntity_getName), jFunctionalAgent});
		}

		/**
		 * Name binding for function: envEntity_getName.
		 * @see #envEntity_getName(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName envEntity_getName = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"envEntity_getName");

		/**
		 * Helper binding method for function: envEntity_getQualifiedName. 
		 * @param fa
		 * @return the SourceModule.expr representing an application of envEntity_getQualifiedName
		 */
		public static final SourceModel.Expr envEntity_getQualifiedName(SourceModel.Expr fa) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.envEntity_getQualifiedName), fa});
		}

		/**
		 * Name binding for function: envEntity_getQualifiedName.
		 * @see #envEntity_getQualifiedName(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName envEntity_getQualifiedName = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"envEntity_getQualifiedName");

		/**
		 * Helper binding method for function: envEntity_getTypeExpr. 
		 * @param jFunctionalAgent
		 * @return the SourceModule.expr representing an application of envEntity_getTypeExpr
		 */
		public static final SourceModel.Expr envEntity_getTypeExpr(SourceModel.Expr jFunctionalAgent) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.envEntity_getTypeExpr), jFunctionalAgent});
		}

		/**
		 * Name binding for function: envEntity_getTypeExpr.
		 * @see #envEntity_getTypeExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName envEntity_getTypeExpr = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"envEntity_getTypeExpr");

		/**
		 * Helper binding method for function: equalsDataCons. 
		 * @param dc1
		 * @param dc2
		 * @return the SourceModule.expr representing an application of equalsDataCons
		 */
		public static final SourceModel.Expr equalsDataCons(SourceModel.Expr dc1, SourceModel.Expr dc2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.equalsDataCons), dc1, dc2});
		}

		/**
		 * Name binding for function: equalsDataCons.
		 * @see #equalsDataCons(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName equalsDataCons = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"equalsDataCons");

		/**
		 * Helper binding method for function: equalsLiteral. 
		 * @param l1
		 * @param l2
		 * @return the SourceModule.expr representing an application of equalsLiteral
		 */
		public static final SourceModel.Expr equalsLiteral(SourceModel.Expr l1, SourceModel.Expr l2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.equalsLiteral), l1, l2});
		}

		/**
		 * Name binding for function: equalsLiteral.
		 * @see #equalsLiteral(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName equalsLiteral = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"equalsLiteral");

		/**
		 * Checks two expression for equality. Note, this is not robust and currently only used for debugging and 
		 * unit test code.
		 * @param expr1 (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          Expression to compare.
		 * @param expr2 (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          Expression to compare.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          A boolean indication if the given expressions are equals.
		 */
		public static final SourceModel.Expr expressionEquals(SourceModel.Expr expr1, SourceModel.Expr expr2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expressionEquals), expr1, expr2});
		}

		/**
		 * Name binding for function: expressionEquals.
		 * @see #expressionEquals(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expressionEquals = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"expressionEquals");

		/**
		 * Checks two expression are the same except that variables names might be different. The only variables
		 * considered for renaming are variables defined in the expression.
		 * @param expr1 (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          Expression to compare.
		 * @param expr2 (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          Expression to compare.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          A boolean indication if the given expressions are expressionIsomorphic in the sense that there exists 
		 * a renaming of the variables of expression one such that expr1 equals expr2.
		 */
		public static final SourceModel.Expr expressionIsomorphic(SourceModel.Expr expr1, SourceModel.Expr expr2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expressionIsomorphic), expr1, expr2});
		}

		/**
		 * Name binding for function: expressionIsomorphic.
		 * @see #expressionIsomorphic(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expressionIsomorphic = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"expressionIsomorphic");

		/**
		 * Helper binding method for function: expressionIsomorphicHelper. 
		 * @param expr1
		 * @param expr2
		 * @param renaming
		 * @return the SourceModule.expr representing an application of expressionIsomorphicHelper
		 */
		public static final SourceModel.Expr expressionIsomorphicHelper(SourceModel.Expr expr1, SourceModel.Expr expr2, SourceModel.Expr renaming) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expressionIsomorphicHelper), expr1, expr2, renaming});
		}

		/**
		 * Name binding for function: expressionIsomorphicHelper.
		 * @see #expressionIsomorphicHelper(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expressionIsomorphicHelper = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"expressionIsomorphicHelper");

		/**
		 * Helper binding method for function: expressionNotEquals. 
		 * @param e1
		 * @param e2
		 * @return the SourceModule.expr representing an application of expressionNotEquals
		 */
		public static final SourceModel.Expr expressionNotEquals(SourceModel.Expr e1, SourceModel.Expr e2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expressionNotEquals), e1, e2});
		}

		/**
		 * Name binding for function: expressionNotEquals.
		 * @see #expressionNotEquals(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expressionNotEquals = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"expressionNotEquals");

		/**
		 * Helper binding method for function: expressionToJObject. 
		 * @param qn
		 * @return the SourceModule.expr representing an application of expressionToJObject
		 */
		public static final SourceModel.Expr expressionToJObject(SourceModel.Expr qn) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expressionToJObject), qn});
		}

		/**
		 * Name binding for function: expressionToJObject.
		 * @see #expressionToJObject(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expressionToJObject = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"expressionToJObject");

		/**
		 * Helper binding method for function: expression_Appl_getE1. 
		 * @param jExpression_Appl
		 * @return the SourceModule.expr representing an application of expression_Appl_getE1
		 */
		public static final SourceModel.Expr expression_Appl_getE1(SourceModel.Expr jExpression_Appl) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_Appl_getE1), jExpression_Appl});
		}

		/**
		 * Name binding for function: expression_Appl_getE1.
		 * @see #expression_Appl_getE1(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expression_Appl_getE1 = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"expression_Appl_getE1");

		/**
		 * Helper binding method for function: expression_Appl_getE2. 
		 * @param jExpression_Appl
		 * @return the SourceModule.expr representing an application of expression_Appl_getE2
		 */
		public static final SourceModel.Expr expression_Appl_getE2(SourceModel.Expr jExpression_Appl) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_Appl_getE2), jExpression_Appl});
		}

		/**
		 * Name binding for function: expression_Appl_getE2.
		 * @see #expression_Appl_getE2(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expression_Appl_getE2 = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"expression_Appl_getE2");

		/**
		 * Helper binding method for function: expression_DataConsSelection_getDCValueExpr. 
		 * @param jExpression_DataConsSelection
		 * @return the SourceModule.expr representing an application of expression_DataConsSelection_getDCValueExpr
		 */
		public static final SourceModel.Expr expression_DataConsSelection_getDCValueExpr(SourceModel.Expr jExpression_DataConsSelection) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_DataConsSelection_getDCValueExpr), jExpression_DataConsSelection});
		}

		/**
		 * Name binding for function: expression_DataConsSelection_getDCValueExpr.
		 * @see #expression_DataConsSelection_getDCValueExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expression_DataConsSelection_getDCValueExpr = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"expression_DataConsSelection_getDCValueExpr");

		/**
		 * Helper binding method for function: expression_DataConsSelection_getDataConstructor. 
		 * @param jExpression_DataConsSelection
		 * @return the SourceModule.expr representing an application of expression_DataConsSelection_getDataConstructor
		 */
		public static final SourceModel.Expr expression_DataConsSelection_getDataConstructor(SourceModel.Expr jExpression_DataConsSelection) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_DataConsSelection_getDataConstructor), jExpression_DataConsSelection});
		}

		/**
		 * Name binding for function: expression_DataConsSelection_getDataConstructor.
		 * @see #expression_DataConsSelection_getDataConstructor(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expression_DataConsSelection_getDataConstructor = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"expression_DataConsSelection_getDataConstructor");

		/**
		 * Helper binding method for function: expression_DataConsSelection_getErrorInfo. 
		 * @param jExpression_DataConsSelection
		 * @return the SourceModule.expr representing an application of expression_DataConsSelection_getErrorInfo
		 */
		public static final SourceModel.Expr expression_DataConsSelection_getErrorInfo(SourceModel.Expr jExpression_DataConsSelection) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_DataConsSelection_getErrorInfo), jExpression_DataConsSelection});
		}

		/**
		 * Name binding for function: expression_DataConsSelection_getErrorInfo.
		 * @see #expression_DataConsSelection_getErrorInfo(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expression_DataConsSelection_getErrorInfo = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"expression_DataConsSelection_getErrorInfo");

		/**
		 * Helper binding method for function: expression_DataConsSelection_getFieldIndex. 
		 * @param jExpression_DataConsSelection
		 * @return the SourceModule.expr representing an application of expression_DataConsSelection_getFieldIndex
		 */
		public static final SourceModel.Expr expression_DataConsSelection_getFieldIndex(SourceModel.Expr jExpression_DataConsSelection) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_DataConsSelection_getFieldIndex), jExpression_DataConsSelection});
		}

		/**
		 * Name binding for function: expression_DataConsSelection_getFieldIndex.
		 * @see #expression_DataConsSelection_getFieldIndex(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expression_DataConsSelection_getFieldIndex = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"expression_DataConsSelection_getFieldIndex");

		/**
		 * Helper binding method for function: expression_LetNonRec_getBody. 
		 * @param jExpression_LetNonRec
		 * @return the SourceModule.expr representing an application of expression_LetNonRec_getBody
		 */
		public static final SourceModel.Expr expression_LetNonRec_getBody(SourceModel.Expr jExpression_LetNonRec) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_LetNonRec_getBody), jExpression_LetNonRec});
		}

		/**
		 * Name binding for function: expression_LetNonRec_getBody.
		 * @see #expression_LetNonRec_getBody(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expression_LetNonRec_getBody = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"expression_LetNonRec_getBody");

		/**
		 * Helper binding method for function: expression_LetNonRec_getDefn. 
		 * @param jExpression_LetNonRec
		 * @return the SourceModule.expr representing an application of expression_LetNonRec_getDefn
		 */
		public static final SourceModel.Expr expression_LetNonRec_getDefn(SourceModel.Expr jExpression_LetNonRec) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_LetNonRec_getDefn), jExpression_LetNonRec});
		}

		/**
		 * Name binding for function: expression_LetNonRec_getDefn.
		 * @see #expression_LetNonRec_getDefn(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expression_LetNonRec_getDefn = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"expression_LetNonRec_getDefn");

		/**
		 * Helper binding method for function: expression_Let_LetDefn_getExpr. 
		 * @param jExpression_Let_LetDefn
		 * @return the SourceModule.expr representing an application of expression_Let_LetDefn_getExpr
		 */
		public static final SourceModel.Expr expression_Let_LetDefn_getExpr(SourceModel.Expr jExpression_Let_LetDefn) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_Let_LetDefn_getExpr), jExpression_Let_LetDefn});
		}

		/**
		 * Name binding for function: expression_Let_LetDefn_getExpr.
		 * @see #expression_Let_LetDefn_getExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expression_Let_LetDefn_getExpr = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"expression_Let_LetDefn_getExpr");

		/**
		 * Helper binding method for function: expression_Let_LetDefn_getVar. 
		 * @param jExpression_Let_LetDefn
		 * @return the SourceModule.expr representing an application of expression_Let_LetDefn_getVar
		 */
		public static final SourceModel.Expr expression_Let_LetDefn_getVar(SourceModel.Expr jExpression_Let_LetDefn) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_Let_LetDefn_getVar), jExpression_Let_LetDefn});
		}

		/**
		 * Name binding for function: expression_Let_LetDefn_getVar.
		 * @see #expression_Let_LetDefn_getVar(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expression_Let_LetDefn_getVar = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"expression_Let_LetDefn_getVar");

		/**
		 * Helper binding method for function: expression_Let_LetDefn_getVarType. 
		 * @param jExpression_Let_LetDefn
		 * @return the SourceModule.expr representing an application of expression_Let_LetDefn_getVarType
		 */
		public static final SourceModel.Expr expression_Let_LetDefn_getVarType(SourceModel.Expr jExpression_Let_LetDefn) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_Let_LetDefn_getVarType), jExpression_Let_LetDefn});
		}

		/**
		 * Name binding for function: expression_Let_LetDefn_getVarType.
		 * @see #expression_Let_LetDefn_getVarType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expression_Let_LetDefn_getVarType = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"expression_Let_LetDefn_getVarType");

		/**
		 * Helper binding method for function: expression_Let_asLetNonRec. 
		 * @param jExpression_Let
		 * @return the SourceModule.expr representing an application of expression_Let_asLetNonRec
		 */
		public static final SourceModel.Expr expression_Let_asLetNonRec(SourceModel.Expr jExpression_Let) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_Let_asLetNonRec), jExpression_Let});
		}

		/**
		 * Name binding for function: expression_Let_asLetNonRec.
		 * @see #expression_Let_asLetNonRec(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expression_Let_asLetNonRec = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"expression_Let_asLetNonRec");

		/**
		 * Helper binding method for function: expression_Let_asLetRec. 
		 * @param jExpression_Let
		 * @return the SourceModule.expr representing an application of expression_Let_asLetRec
		 */
		public static final SourceModel.Expr expression_Let_asLetRec(SourceModel.Expr jExpression_Let) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_Let_asLetRec), jExpression_Let});
		}

		/**
		 * Name binding for function: expression_Let_asLetRec.
		 * @see #expression_Let_asLetRec(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expression_Let_asLetRec = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"expression_Let_asLetRec");

		/**
		 * Helper binding method for function: expression_Let_getBody. 
		 * @param jExpression_Let
		 * @return the SourceModule.expr representing an application of expression_Let_getBody
		 */
		public static final SourceModel.Expr expression_Let_getBody(SourceModel.Expr jExpression_Let) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_Let_getBody), jExpression_Let});
		}

		/**
		 * Name binding for function: expression_Let_getBody.
		 * @see #expression_Let_getBody(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expression_Let_getBody = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"expression_Let_getBody");

		/**
		 * Helper binding method for function: expression_Literal_getLiteral. 
		 * @param jExpression_Literal
		 * @return the SourceModule.expr representing an application of expression_Literal_getLiteral
		 */
		public static final SourceModel.Expr expression_Literal_getLiteral(SourceModel.Expr jExpression_Literal) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_Literal_getLiteral), jExpression_Literal});
		}

		/**
		 * Name binding for function: expression_Literal_getLiteral.
		 * @see #expression_Literal_getLiteral(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expression_Literal_getLiteral = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"expression_Literal_getLiteral");

		/**
		 * Helper binding method for function: expression_RecordCase_getBaseRecordPatternVarName. 
		 * @param jExpression_RecordCase
		 * @return the SourceModule.expr representing an application of expression_RecordCase_getBaseRecordPatternVarName
		 */
		public static final SourceModel.Expr expression_RecordCase_getBaseRecordPatternVarName(SourceModel.Expr jExpression_RecordCase) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_RecordCase_getBaseRecordPatternVarName), jExpression_RecordCase});
		}

		/**
		 * Name binding for function: expression_RecordCase_getBaseRecordPatternVarName.
		 * @see #expression_RecordCase_getBaseRecordPatternVarName(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expression_RecordCase_getBaseRecordPatternVarName = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"expression_RecordCase_getBaseRecordPatternVarName");

		/**
		 * Helper binding method for function: expression_RecordCase_getConditionExpr. 
		 * @param jExpression_RecordCase
		 * @return the SourceModule.expr representing an application of expression_RecordCase_getConditionExpr
		 */
		public static final SourceModel.Expr expression_RecordCase_getConditionExpr(SourceModel.Expr jExpression_RecordCase) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_RecordCase_getConditionExpr), jExpression_RecordCase});
		}

		/**
		 * Name binding for function: expression_RecordCase_getConditionExpr.
		 * @see #expression_RecordCase_getConditionExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expression_RecordCase_getConditionExpr = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"expression_RecordCase_getConditionExpr");

		/**
		 * Helper binding method for function: expression_RecordCase_getFieldBindingVarMap. 
		 * @param jExpression_RecordCase
		 * @return the SourceModule.expr representing an application of expression_RecordCase_getFieldBindingVarMap
		 */
		public static final SourceModel.Expr expression_RecordCase_getFieldBindingVarMap(SourceModel.Expr jExpression_RecordCase) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_RecordCase_getFieldBindingVarMap), jExpression_RecordCase});
		}

		/**
		 * Name binding for function: expression_RecordCase_getFieldBindingVarMap.
		 * @see #expression_RecordCase_getFieldBindingVarMap(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expression_RecordCase_getFieldBindingVarMap = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"expression_RecordCase_getFieldBindingVarMap");

		/**
		 * Helper binding method for function: expression_RecordCase_getResultExpr. 
		 * @param jExpression_RecordCase
		 * @return the SourceModule.expr representing an application of expression_RecordCase_getResultExpr
		 */
		public static final SourceModel.Expr expression_RecordCase_getResultExpr(SourceModel.Expr jExpression_RecordCase) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_RecordCase_getResultExpr), jExpression_RecordCase});
		}

		/**
		 * Name binding for function: expression_RecordCase_getResultExpr.
		 * @see #expression_RecordCase_getResultExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expression_RecordCase_getResultExpr = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"expression_RecordCase_getResultExpr");

		/**
		 * Helper binding method for function: expression_RecordExtension_getBaseRecordExpr. 
		 * @param jExpression_RecordExtension
		 * @return the SourceModule.expr representing an application of expression_RecordExtension_getBaseRecordExpr
		 */
		public static final SourceModel.Expr expression_RecordExtension_getBaseRecordExpr(SourceModel.Expr jExpression_RecordExtension) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_RecordExtension_getBaseRecordExpr), jExpression_RecordExtension});
		}

		/**
		 * Name binding for function: expression_RecordExtension_getBaseRecordExpr.
		 * @see #expression_RecordExtension_getBaseRecordExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expression_RecordExtension_getBaseRecordExpr = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"expression_RecordExtension_getBaseRecordExpr");

		/**
		 * Helper binding method for function: expression_RecordExtension_getExtensionFieldsMap. 
		 * @param jExpression_RecordExtension
		 * @return the SourceModule.expr representing an application of expression_RecordExtension_getExtensionFieldsMap
		 */
		public static final SourceModel.Expr expression_RecordExtension_getExtensionFieldsMap(SourceModel.Expr jExpression_RecordExtension) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_RecordExtension_getExtensionFieldsMap), jExpression_RecordExtension});
		}

		/**
		 * Name binding for function: expression_RecordExtension_getExtensionFieldsMap.
		 * @see #expression_RecordExtension_getExtensionFieldsMap(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expression_RecordExtension_getExtensionFieldsMap = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"expression_RecordExtension_getExtensionFieldsMap");

		/**
		 * Helper binding method for function: expression_RecordSelection_getFieldName. 
		 * @param jExpression_RecordSelection
		 * @return the SourceModule.expr representing an application of expression_RecordSelection_getFieldName
		 */
		public static final SourceModel.Expr expression_RecordSelection_getFieldName(SourceModel.Expr jExpression_RecordSelection) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_RecordSelection_getFieldName), jExpression_RecordSelection});
		}

		/**
		 * Name binding for function: expression_RecordSelection_getFieldName.
		 * @see #expression_RecordSelection_getFieldName(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expression_RecordSelection_getFieldName = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"expression_RecordSelection_getFieldName");

		/**
		 * Helper binding method for function: expression_RecordSelection_getRecordExpr. 
		 * @param jExpression_RecordSelection
		 * @return the SourceModule.expr representing an application of expression_RecordSelection_getRecordExpr
		 */
		public static final SourceModel.Expr expression_RecordSelection_getRecordExpr(SourceModel.Expr jExpression_RecordSelection) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_RecordSelection_getRecordExpr), jExpression_RecordSelection});
		}

		/**
		 * Name binding for function: expression_RecordSelection_getRecordExpr.
		 * @see #expression_RecordSelection_getRecordExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expression_RecordSelection_getRecordExpr = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"expression_RecordSelection_getRecordExpr");

		/**
		 * Helper binding method for function: expression_Switch_SwitchAlt_Matching_getFieldNameToVarNameMap. 
		 * @param jExpression_Switch_SwitchAlt_Matching
		 * @return the SourceModule.expr representing an application of expression_Switch_SwitchAlt_Matching_getFieldNameToVarNameMap
		 */
		public static final SourceModel.Expr expression_Switch_SwitchAlt_Matching_getFieldNameToVarNameMap(SourceModel.Expr jExpression_Switch_SwitchAlt_Matching) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_Switch_SwitchAlt_Matching_getFieldNameToVarNameMap), jExpression_Switch_SwitchAlt_Matching});
		}

		/**
		 * Name binding for function: expression_Switch_SwitchAlt_Matching_getFieldNameToVarNameMap.
		 * @see #expression_Switch_SwitchAlt_Matching_getFieldNameToVarNameMap(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expression_Switch_SwitchAlt_Matching_getFieldNameToVarNameMap = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"expression_Switch_SwitchAlt_Matching_getFieldNameToVarNameMap");

		/**
		 * Helper binding method for function: expression_Switch_SwitchAlt_Positional_getPositionToVarNameMap. 
		 * @param jExpression_Switch_SwitchAlt_Positional
		 * @return the SourceModule.expr representing an application of expression_Switch_SwitchAlt_Positional_getPositionToVarNameMap
		 */
		public static final SourceModel.Expr expression_Switch_SwitchAlt_Positional_getPositionToVarNameMap(SourceModel.Expr jExpression_Switch_SwitchAlt_Positional) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_Switch_SwitchAlt_Positional_getPositionToVarNameMap), jExpression_Switch_SwitchAlt_Positional});
		}

		/**
		 * Name binding for function: expression_Switch_SwitchAlt_Positional_getPositionToVarNameMap.
		 * @see #expression_Switch_SwitchAlt_Positional_getPositionToVarNameMap(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expression_Switch_SwitchAlt_Positional_getPositionToVarNameMap = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"expression_Switch_SwitchAlt_Positional_getPositionToVarNameMap");

		/**
		 * Helper binding method for function: expression_Switch_SwitchAlt_getAltExpr. 
		 * @param jExpression_Switch_SwitchAlt
		 * @return the SourceModule.expr representing an application of expression_Switch_SwitchAlt_getAltExpr
		 */
		public static final SourceModel.Expr expression_Switch_SwitchAlt_getAltExpr(SourceModel.Expr jExpression_Switch_SwitchAlt) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_Switch_SwitchAlt_getAltExpr), jExpression_Switch_SwitchAlt});
		}

		/**
		 * Name binding for function: expression_Switch_SwitchAlt_getAltExpr.
		 * @see #expression_Switch_SwitchAlt_getAltExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expression_Switch_SwitchAlt_getAltExpr = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"expression_Switch_SwitchAlt_getAltExpr");

		/**
		 * Helper binding method for function: expression_Switch_SwitchAlt_getAltTags. 
		 * @param jExpression_Switch_SwitchAlt
		 * @return the SourceModule.expr representing an application of expression_Switch_SwitchAlt_getAltTags
		 */
		public static final SourceModel.Expr expression_Switch_SwitchAlt_getAltTags(SourceModel.Expr jExpression_Switch_SwitchAlt) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_Switch_SwitchAlt_getAltTags), jExpression_Switch_SwitchAlt});
		}

		/**
		 * Name binding for function: expression_Switch_SwitchAlt_getAltTags.
		 * @see #expression_Switch_SwitchAlt_getAltTags(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expression_Switch_SwitchAlt_getAltTags = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"expression_Switch_SwitchAlt_getAltTags");

		/**
		 * Helper binding method for function: expression_Switch_getAlt. 
		 * @param jExpression_Switch
		 * @param i
		 * @return the SourceModule.expr representing an application of expression_Switch_getAlt
		 */
		public static final SourceModel.Expr expression_Switch_getAlt(SourceModel.Expr jExpression_Switch, SourceModel.Expr i) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_Switch_getAlt), jExpression_Switch, i});
		}

		/**
		 * @see #expression_Switch_getAlt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param jExpression_Switch
		 * @param i
		 * @return the SourceModel.Expr representing an application of expression_Switch_getAlt
		 */
		public static final SourceModel.Expr expression_Switch_getAlt(SourceModel.Expr jExpression_Switch, int i) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_Switch_getAlt), jExpression_Switch, SourceModel.Expr.makeIntValue(i)});
		}

		/**
		 * Name binding for function: expression_Switch_getAlt.
		 * @see #expression_Switch_getAlt(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expression_Switch_getAlt = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"expression_Switch_getAlt");

		/**
		 * Helper binding method for function: expression_Switch_getNAlts. 
		 * @param jExpression_Switch
		 * @return the SourceModule.expr representing an application of expression_Switch_getNAlts
		 */
		public static final SourceModel.Expr expression_Switch_getNAlts(SourceModel.Expr jExpression_Switch) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_Switch_getNAlts), jExpression_Switch});
		}

		/**
		 * Name binding for function: expression_Switch_getNAlts.
		 * @see #expression_Switch_getNAlts(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expression_Switch_getNAlts = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"expression_Switch_getNAlts");

		/**
		 * Helper binding method for function: expression_Switch_getSwitchExpr. 
		 * @param jExpression_Switch
		 * @return the SourceModule.expr representing an application of expression_Switch_getSwitchExpr
		 */
		public static final SourceModel.Expr expression_Switch_getSwitchExpr(SourceModel.Expr jExpression_Switch) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_Switch_getSwitchExpr), jExpression_Switch});
		}

		/**
		 * Name binding for function: expression_Switch_getSwitchExpr.
		 * @see #expression_Switch_getSwitchExpr(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expression_Switch_getSwitchExpr = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"expression_Switch_getSwitchExpr");

		/**
		 * Helper binding method for function: expression_TailRecursiveCall_getApplForm. 
		 * @param jExpression_TailRecursiveCall
		 * @return the SourceModule.expr representing an application of expression_TailRecursiveCall_getApplForm
		 */
		public static final SourceModel.Expr expression_TailRecursiveCall_getApplForm(SourceModel.Expr jExpression_TailRecursiveCall) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_TailRecursiveCall_getApplForm), jExpression_TailRecursiveCall});
		}

		/**
		 * Name binding for function: expression_TailRecursiveCall_getApplForm.
		 * @see #expression_TailRecursiveCall_getApplForm(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expression_TailRecursiveCall_getApplForm = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"expression_TailRecursiveCall_getApplForm");

		/**
		 * Helper binding method for function: expression_Var_getFunctionalAgent. 
		 * @param jExpression_Var
		 * @return the SourceModule.expr representing an application of expression_Var_getFunctionalAgent
		 */
		public static final SourceModel.Expr expression_Var_getFunctionalAgent(SourceModel.Expr jExpression_Var) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_Var_getFunctionalAgent), jExpression_Var});
		}

		/**
		 * Name binding for function: expression_Var_getFunctionalAgent.
		 * @see #expression_Var_getFunctionalAgent(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expression_Var_getFunctionalAgent = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"expression_Var_getFunctionalAgent");

		/**
		 * Helper binding method for function: expression_Var_getName. 
		 * @param jExpression_Var
		 * @return the SourceModule.expr representing an application of expression_Var_getName
		 */
		public static final SourceModel.Expr expression_Var_getName(SourceModel.Expr jExpression_Var) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_Var_getName), jExpression_Var});
		}

		/**
		 * Name binding for function: expression_Var_getName.
		 * @see #expression_Var_getName(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expression_Var_getName = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"expression_Var_getName");

		/**
		 * Helper binding method for function: expression_asAppl. 
		 * @param jExpression
		 * @return the SourceModule.expr representing an application of expression_asAppl
		 */
		public static final SourceModel.Expr expression_asAppl(SourceModel.Expr jExpression) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_asAppl), jExpression});
		}

		/**
		 * Name binding for function: expression_asAppl.
		 * @see #expression_asAppl(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expression_asAppl = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"expression_asAppl");

		/**
		 * Helper binding method for function: expression_asDataConsSelection. 
		 * @param jExpression
		 * @return the SourceModule.expr representing an application of expression_asDataConsSelection
		 */
		public static final SourceModel.Expr expression_asDataConsSelection(SourceModel.Expr jExpression) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_asDataConsSelection), jExpression});
		}

		/**
		 * Name binding for function: expression_asDataConsSelection.
		 * @see #expression_asDataConsSelection(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expression_asDataConsSelection = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"expression_asDataConsSelection");

		/**
		 * Helper binding method for function: expression_asLet. 
		 * @param jExpression
		 * @return the SourceModule.expr representing an application of expression_asLet
		 */
		public static final SourceModel.Expr expression_asLet(SourceModel.Expr jExpression) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_asLet), jExpression});
		}

		/**
		 * Name binding for function: expression_asLet.
		 * @see #expression_asLet(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expression_asLet = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"expression_asLet");

		/**
		 * Helper binding method for function: expression_asLiteral. 
		 * @param jExpression
		 * @return the SourceModule.expr representing an application of expression_asLiteral
		 */
		public static final SourceModel.Expr expression_asLiteral(SourceModel.Expr jExpression) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_asLiteral), jExpression});
		}

		/**
		 * Name binding for function: expression_asLiteral.
		 * @see #expression_asLiteral(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expression_asLiteral = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"expression_asLiteral");

		/**
		 * Helper binding method for function: expression_asRecordCase. 
		 * @param jExpression
		 * @return the SourceModule.expr representing an application of expression_asRecordCase
		 */
		public static final SourceModel.Expr expression_asRecordCase(SourceModel.Expr jExpression) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_asRecordCase), jExpression});
		}

		/**
		 * Name binding for function: expression_asRecordCase.
		 * @see #expression_asRecordCase(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expression_asRecordCase = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"expression_asRecordCase");

		/**
		 * Helper binding method for function: expression_asRecordExtension. 
		 * @param jExpression
		 * @return the SourceModule.expr representing an application of expression_asRecordExtension
		 */
		public static final SourceModel.Expr expression_asRecordExtension(SourceModel.Expr jExpression) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_asRecordExtension), jExpression});
		}

		/**
		 * Name binding for function: expression_asRecordExtension.
		 * @see #expression_asRecordExtension(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expression_asRecordExtension = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"expression_asRecordExtension");

		/**
		 * Helper binding method for function: expression_asRecordSelection. 
		 * @param jExpression
		 * @return the SourceModule.expr representing an application of expression_asRecordSelection
		 */
		public static final SourceModel.Expr expression_asRecordSelection(SourceModel.Expr jExpression) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_asRecordSelection), jExpression});
		}

		/**
		 * Name binding for function: expression_asRecordSelection.
		 * @see #expression_asRecordSelection(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expression_asRecordSelection = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"expression_asRecordSelection");

		/**
		 * Helper binding method for function: expression_asSwitch. 
		 * @param jExpression
		 * @return the SourceModule.expr representing an application of expression_asSwitch
		 */
		public static final SourceModel.Expr expression_asSwitch(SourceModel.Expr jExpression) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_asSwitch), jExpression});
		}

		/**
		 * Name binding for function: expression_asSwitch.
		 * @see #expression_asSwitch(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expression_asSwitch = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"expression_asSwitch");

		/**
		 * Helper binding method for function: expression_asTailRecursiveCall. 
		 * @param jExpression
		 * @return the SourceModule.expr representing an application of expression_asTailRecursiveCall
		 */
		public static final SourceModel.Expr expression_asTailRecursiveCall(SourceModel.Expr jExpression) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_asTailRecursiveCall), jExpression});
		}

		/**
		 * Name binding for function: expression_asTailRecursiveCall.
		 * @see #expression_asTailRecursiveCall(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expression_asTailRecursiveCall = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"expression_asTailRecursiveCall");

		/**
		 * Helper binding method for function: expression_asVar. 
		 * @param jExpression
		 * @return the SourceModule.expr representing an application of expression_asVar
		 */
		public static final SourceModel.Expr expression_asVar(SourceModel.Expr jExpression) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_asVar), jExpression});
		}

		/**
		 * Name binding for function: expression_asVar.
		 * @see #expression_asVar(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expression_asVar = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"expression_asVar");

		/**
		 * Get the variables from a lambda expression.
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The expression to get the lambda variables from.
		 * @return (CAL type: <code>[Cal.Internal.Optimizer_Expression.QualifiedName]</code>) 
		 *          A list of the lambda variables (at the top level) for the given expression.
		 */
		public static final SourceModel.Expr expression_getArguments(SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_getArguments), expr});
		}

		/**
		 * Name binding for function: expression_getArguments.
		 * @see #expression_getArguments(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expression_getArguments = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"expression_getArguments");

		/**
		 * Get the arity from the expression by counting the number of lambda vars before a 'real' expression.
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The expression to get the arity of.
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          The arity of the given expression.
		 */
		public static final SourceModel.Expr expression_getArity(SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_getArity), expr});
		}

		/**
		 * Name binding for function: expression_getArity.
		 * @see #expression_getArity(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expression_getArity = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"expression_getArity");

		/**
		 * Skip all the outer let definitions and get just the function body.
		 * @param expression (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>) 
		 */
		public static final SourceModel.Expr expression_getBody(SourceModel.Expr expression) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_getBody), expression});
		}

		/**
		 * Name binding for function: expression_getBody.
		 * @see #expression_getBody(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expression_getBody = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"expression_getBody");

		/**
		 * TODO fix this to have better space usage. Maybe get rid of it too.
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @param acc (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 */
		public static final SourceModel.Expr expression_getDepth(SourceModel.Expr expr, SourceModel.Expr acc) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_getDepth), expr, acc});
		}

		/**
		 * @see #expression_getDepth(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param expr
		 * @param acc
		 * @return the SourceModel.Expr representing an application of expression_getDepth
		 */
		public static final SourceModel.Expr expression_getDepth(SourceModel.Expr expr, int acc) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_getDepth), expr, SourceModel.Expr.makeIntValue(acc)});
		}

		/**
		 * Name binding for function: expression_getDepth.
		 * @see #expression_getDepth(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expression_getDepth = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"expression_getDepth");

		/**
		 * Determine if the given expression has any strict top level lambda variables.
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The expression to check.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          Whether or not the given expression has strict top level lambda variables.
		 */
		public static final SourceModel.Expr expression_hasStrictArguments(SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_hasStrictArguments), expr});
		}

		/**
		 * Name binding for function: expression_hasStrictArguments.
		 * @see #expression_hasStrictArguments(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expression_hasStrictArguments = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"expression_hasStrictArguments");

		/**
		 * Helper binding method for function: expression_isConstant. 
		 * @param expr
		 * @return the SourceModule.expr representing an application of expression_isConstant
		 */
		public static final SourceModel.Expr expression_isConstant(SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_isConstant), expr});
		}

		/**
		 * Name binding for function: expression_isConstant.
		 * @see #expression_isConstant(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expression_isConstant = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"expression_isConstant");

		/**
		 * Returns a flag indication whether or not the expression would need to be reduced.
		 * <p>
		 * TODO this is not the correct definition of whnf. Change the function name to a more appropriate one.
		 * 
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr expression_isWHNF(SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_isWHNF), expr});
		}

		/**
		 * Name binding for function: expression_isWHNF.
		 * @see #expression_isWHNF(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expression_isWHNF = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"expression_isWHNF");

		/**
		 * Helper binding method for function: expression_setType. 
		 * @param expr
		 * @param type
		 * @return the SourceModule.expr representing an application of expression_setType
		 */
		public static final SourceModel.Expr expression_setType(SourceModel.Expr expr, SourceModel.Expr type) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.expression_setType), expr, type});
		}

		/**
		 * Name binding for function: expression_setType.
		 * @see #expression_setType(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName expression_setType = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"expression_setType");

		/**
		 * Helper binding method for function: fieldNameToJObject. 
		 * @param qn
		 * @return the SourceModule.expr representing an application of fieldNameToJObject
		 */
		public static final SourceModel.Expr fieldNameToJObject(SourceModel.Expr qn) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fieldNameToJObject), qn});
		}

		/**
		 * Name binding for function: fieldNameToJObject.
		 * @see #fieldNameToJObject(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fieldNameToJObject = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"fieldNameToJObject");

		/**
		 * Helper binding method for function: fieldName_getCalSourceForm. 
		 * @param jFieldName
		 * @return the SourceModule.expr representing an application of fieldName_getCalSourceForm
		 */
		public static final SourceModel.Expr fieldName_getCalSourceForm(SourceModel.Expr jFieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fieldName_getCalSourceForm), jFieldName});
		}

		/**
		 * Name binding for function: fieldName_getCalSourceForm.
		 * @see #fieldName_getCalSourceForm(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fieldName_getCalSourceForm = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"fieldName_getCalSourceForm");

		/**
		 * Converts an expression into a list of expressions where the first expression is the functor followed by
		 * all the arguments of the call.
		 * <p>
		 * Example: flatten ((App f1 a1) a2) == [f1, a1, a2]
		 * 
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The expression to flatten.
		 * @param resultList (CAL type: <code>[Cal.Internal.Optimizer_Expression.Expression]</code>)
		 *          Accumulator for the terms of the expression. Pass empty list in usually.
		 * @return (CAL type: <code>[Cal.Internal.Optimizer_Expression.Expression]</code>) 
		 */
		public static final SourceModel.Expr flattenExpression(SourceModel.Expr expr, SourceModel.Expr resultList) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.flattenExpression), expr, resultList});
		}

		/**
		 * Name binding for function: flattenExpression.
		 * @see #flattenExpression(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName flattenExpression = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"flattenExpression");

		/**
		 * Helper binding method for function: flattenExpressionExamples. 
		 * @return the SourceModule.expr representing an application of flattenExpressionExamples
		 */
		public static final SourceModel.Expr flattenExpressionExamples() {
			return 
				SourceModel.Expr.Var.make(Functions.flattenExpressionExamples);
		}

		/**
		 * Name binding for function: flattenExpressionExamples.
		 * @see #flattenExpressionExamples()
		 */
		public static final QualifiedName flattenExpressionExamples = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"flattenExpressionExamples");

		/**
		 * Pick off all the seq'ed expression from the from of the expression. Return a list of expressions
		 * where the first expression is the innermost one followed by the seq'ed expression in reverse order.
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>(Cal.Internal.Optimizer_Expression.Expression, [Cal.Internal.Optimizer_Expression.Expression])</code>) 
		 */
		public static final SourceModel.Expr flattenSeqs2(SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.flattenSeqs2), expr});
		}

		/**
		 * Name binding for function: flattenSeqs2.
		 * @see #flattenSeqs2(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName flattenSeqs2 = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"flattenSeqs2");

		/**
		 * getFunctions expr == head (flattenExpression expr [])
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>) 
		 */
		public static final SourceModel.Expr getFunctor(SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getFunctor), expr});
		}

		/**
		 * Name binding for function: getFunctor.
		 * @see #getFunctor(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getFunctor = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"getFunctor");

		/**
		 * Get the functor and arity from the given expression.
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>(Cal.Internal.Optimizer_Expression.Expression, Cal.Core.Prelude.Int)</code>) 
		 */
		public static final SourceModel.Expr getFunctorAndArity(SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getFunctorAndArity), expr});
		}

		/**
		 * Name binding for function: getFunctorAndArity.
		 * @see #getFunctorAndArity(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getFunctorAndArity = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"getFunctorAndArity");

		/**
		 * Count the number of times that an expression would be calculated if inlined in the given expression.
		 * @param name (CAL type: <code>Cal.Internal.Optimizer_Expression.QualifiedName</code>)
		 *          Variable to look for.
		 * @param dc (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Internal.Optimizer_Expression.DataCons</code>)
		 *          If the name has a known data constructor then this is it. This is used in the switch case. Look there for an explanation.
		 * @param expression (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The expression to search.
		 * @return (CAL type: <code>Cal.Internal.Optimizer_Expression.Occurs</code>) 
		 *          Zero if the variable does not occur. One if the variable would be calculated once. Two if the variable would be calculated more than once.
		 */
		public static final SourceModel.Expr getOccurs(SourceModel.Expr name, SourceModel.Expr dc, SourceModel.Expr expression) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getOccurs), name, dc, expression});
		}

		/**
		 * Name binding for function: getOccurs.
		 * @see #getOccurs(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getOccurs = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"getOccurs");

		/**
		 * If the given expression is a call to Prelude.seq return the two arguments otherwise return nothing.
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe (Cal.Internal.Optimizer_Expression.Expression, Cal.Internal.Optimizer_Expression.Expression)</code>) 
		 */
		public static final SourceModel.Expr getSeq(SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getSeq), expr});
		}

		/**
		 * Name binding for function: getSeq.
		 * @see #getSeq(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getSeq = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"getSeq");

		/**
		 * Helper binding method for function: inputAlt. 
		 * @param jAlt
		 * @return the SourceModule.expr representing an application of inputAlt
		 */
		public static final SourceModel.Expr inputAlt(SourceModel.Expr jAlt) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inputAlt), jAlt});
		}

		/**
		 * Name binding for function: inputAlt.
		 * @see #inputAlt(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName inputAlt = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"inputAlt");

		/**
		 * Helper binding method for function: inputExpression. 
		 * @param jExpression
		 * @return the SourceModule.expr representing an application of inputExpression
		 */
		public static final SourceModel.Expr inputExpression(SourceModel.Expr jExpression) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inputExpression), jExpression});
		}

		/**
		 * Name binding for function: inputExpression.
		 * @see #inputExpression(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName inputExpression = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"inputExpression");

		/**
		 * Helper binding method for function: inputFieldName. 
		 * @param jFieldName
		 * @return the SourceModule.expr representing an application of inputFieldName
		 */
		public static final SourceModel.Expr inputFieldName(SourceModel.Expr jFieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inputFieldName), jFieldName});
		}

		/**
		 * Name binding for function: inputFieldName.
		 * @see #inputFieldName(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName inputFieldName = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"inputFieldName");

		/**
		 * Helper binding method for function: inputLiteral. 
		 * @param jLiteral
		 * @return the SourceModule.expr representing an application of inputLiteral
		 */
		public static final SourceModel.Expr inputLiteral(SourceModel.Expr jLiteral) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inputLiteral), jLiteral});
		}

		/**
		 * Name binding for function: inputLiteral.
		 * @see #inputLiteral(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName inputLiteral = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"inputLiteral");

		/**
		 * Helper binding method for function: inputQualifiedName. 
		 * @param jQN
		 * @return the SourceModule.expr representing an application of inputQualifiedName
		 */
		public static final SourceModel.Expr inputQualifiedName(SourceModel.Expr jQN) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inputQualifiedName), jQN});
		}

		/**
		 * Name binding for function: inputQualifiedName.
		 * @see #inputQualifiedName(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName inputQualifiedName = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"inputQualifiedName");

		/**
		 * Returns true iff the given expression is a lambda expression.
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The expression to check.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr isLambdaExpression(SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isLambdaExpression), expr});
		}

		/**
		 * Name binding for function: isLambdaExpression.
		 * @see #isLambdaExpression(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isLambdaExpression = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"isLambdaExpression");

		/**
		 * Returns whether or not the given expression is a Literal type data object.
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The expression to test.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          True iff the given expression is a Literal.
		 */
		public static final SourceModel.Expr isLiteral(SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isLiteral), expr});
		}

		/**
		 * Name binding for function: isLiteral.
		 * @see #isLiteral(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isLiteral = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"isLiteral");

		/**
		 * Checks if the given literal would match the given data constructor. This is
		 * currently only implemented for Booleans
		 * @param dc (CAL type: <code>Cal.Internal.Optimizer_Expression.DataCons</code>)
		 *          The data constructor
		 * @param l (CAL type: <code>Cal.Internal.Optimizer_Expression.Literal</code>)
		 *          The listeral
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          True if the given literal matches the data constructor.
		 */
		public static final SourceModel.Expr isMatchingDataConsLiteral(SourceModel.Expr dc, SourceModel.Expr l) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isMatchingDataConsLiteral), dc, l});
		}

		/**
		 * Name binding for function: isMatchingDataConsLiteral.
		 * @see #isMatchingDataConsLiteral(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isMatchingDataConsLiteral = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"isMatchingDataConsLiteral");

		/**
		 * Helper binding method for function: isSeq. 
		 * @param expr
		 * @return the SourceModule.expr representing an application of isSeq
		 */
		public static final SourceModel.Expr isSeq(SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isSeq), expr});
		}

		/**
		 * Name binding for function: isSeq.
		 * @see #isSeq(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isSeq = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"isSeq");

		/**
		 * Returns whether or not the given expression is a Var type data object.
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The expression to test
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          True iff the given expression is a Var.
		 */
		public static final SourceModel.Expr isVar(SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isVar), expr});
		}

		/**
		 * Name binding for function: isVar.
		 * @see #isVar(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isVar = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"isVar");

		/**
		 * Helper binding method for function: isZero. 
		 * @param literal
		 * @return the SourceModule.expr representing an application of isZero
		 */
		public static final SourceModel.Expr isZero(SourceModel.Expr literal) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isZero), literal});
		}

		/**
		 * Name binding for function: isZero.
		 * @see #isZero(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isZero = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"isZero");

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
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"iterator_hasNext");

		/**
		 * foreign method java.util.Iterator.next
		 * @param jIterator (CAL type: <code>Cal.Internal.Optimizer_Expression.JIterator</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.JObject</code>) 
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
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"iterator_next");

		/**
		 * Helper binding method for function: jList_iterator. 
		 * @param jList
		 * @return the SourceModule.expr representing an application of jList_iterator
		 */
		public static final SourceModel.Expr jList_iterator(SourceModel.Expr jList) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jList_iterator), jList});
		}

		/**
		 * Name binding for function: jList_iterator.
		 * @see #jList_iterator(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jList_iterator = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"jList_iterator");

		/**
		 * java.util.List definitions.
		 * @param jList (CAL type: <code>Cal.Core.Prelude.JList</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 */
		public static final SourceModel.Expr jList_size(SourceModel.Expr jList) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jList_size), jList});
		}

		/**
		 * Name binding for function: jList_size.
		 * @see #jList_size(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jList_size = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"jList_size");

		/**
		 * Helper binding method for function: jModuleName_toString. 
		 * @param jModuleName
		 * @return the SourceModule.expr representing an application of jModuleName_toString
		 */
		public static final SourceModel.Expr jModuleName_toString(SourceModel.Expr jModuleName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jModuleName_toString), jModuleName});
		}

		/**
		 * Name binding for function: jModuleName_toString.
		 * @see #jModuleName_toString(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jModuleName_toString = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"jModuleName_toString");

		/**
		 * Helper binding method for function: jQualifiedName_getModuleName. 
		 * @param jQualifiedName
		 * @return the SourceModule.expr representing an application of jQualifiedName_getModuleName
		 */
		public static final SourceModel.Expr jQualifiedName_getModuleName(SourceModel.Expr jQualifiedName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jQualifiedName_getModuleName), jQualifiedName});
		}

		/**
		 * Name binding for function: jQualifiedName_getModuleName.
		 * @see #jQualifiedName_getModuleName(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jQualifiedName_getModuleName = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"jQualifiedName_getModuleName");

		/**
		 * Helper binding method for function: jQualifiedName_getUnqualifiedName. 
		 * @param jQualifiedName
		 * @return the SourceModule.expr representing an application of jQualifiedName_getUnqualifiedName
		 */
		public static final SourceModel.Expr jQualifiedName_getUnqualifiedName(SourceModel.Expr jQualifiedName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jQualifiedName_getUnqualifiedName), jQualifiedName});
		}

		/**
		 * Name binding for function: jQualifiedName_getUnqualifiedName.
		 * @see #jQualifiedName_getUnqualifiedName(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jQualifiedName_getUnqualifiedName = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"jQualifiedName_getUnqualifiedName");

		/**
		 * Helper binding method for function: jQualifiedName_make. 
		 * @param moduleName
		 * @param unqualifiedName
		 * @return the SourceModule.expr representing an application of jQualifiedName_make
		 */
		public static final SourceModel.Expr jQualifiedName_make(SourceModel.Expr moduleName, SourceModel.Expr unqualifiedName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jQualifiedName_make), moduleName, unqualifiedName});
		}

		/**
		 * @see #jQualifiedName_make(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param moduleName
		 * @param unqualifiedName
		 * @return the SourceModel.Expr representing an application of jQualifiedName_make
		 */
		public static final SourceModel.Expr jQualifiedName_make(java.lang.String moduleName, java.lang.String unqualifiedName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jQualifiedName_make), SourceModel.Expr.makeStringValue(moduleName), SourceModel.Expr.makeStringValue(unqualifiedName)});
		}

		/**
		 * Name binding for function: jQualifiedName_make.
		 * @see #jQualifiedName_make(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jQualifiedName_make = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"jQualifiedName_make");

		/**
		 * Helper binding method for function: jobjectToAlt. 
		 * @param jobj
		 * @return the SourceModule.expr representing an application of jobjectToAlt
		 */
		public static final SourceModel.Expr jobjectToAlt(SourceModel.Expr jobj) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jobjectToAlt), jobj});
		}

		/**
		 * Name binding for function: jobjectToAlt.
		 * @see #jobjectToAlt(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jobjectToAlt = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"jobjectToAlt");

		/**
		 * Helper binding method for function: jobjectToExpression. 
		 * @param jobj
		 * @return the SourceModule.expr representing an application of jobjectToExpression
		 */
		public static final SourceModel.Expr jobjectToExpression(SourceModel.Expr jobj) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jobjectToExpression), jobj});
		}

		/**
		 * Name binding for function: jobjectToExpression.
		 * @see #jobjectToExpression(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jobjectToExpression = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"jobjectToExpression");

		/**
		 * Helper binding method for function: jobjectToFieldName. 
		 * @param jobj
		 * @return the SourceModule.expr representing an application of jobjectToFieldName
		 */
		public static final SourceModel.Expr jobjectToFieldName(SourceModel.Expr jobj) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jobjectToFieldName), jobj});
		}

		/**
		 * Name binding for function: jobjectToFieldName.
		 * @see #jobjectToFieldName(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jobjectToFieldName = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"jobjectToFieldName");

		/**
		 * Helper binding method for function: jobjectToLiteral. 
		 * @param jobject
		 * @return the SourceModule.expr representing an application of jobjectToLiteral
		 */
		public static final SourceModel.Expr jobjectToLiteral(SourceModel.Expr jobject) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jobjectToLiteral), jobject});
		}

		/**
		 * Name binding for function: jobjectToLiteral.
		 * @see #jobjectToLiteral(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jobjectToLiteral = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"jobjectToLiteral");

		/**
		 * Helper binding method for function: jobjectToQualifiedName. 
		 * @param jobj
		 * @return the SourceModule.expr representing an application of jobjectToQualifiedName
		 */
		public static final SourceModel.Expr jobjectToQualifiedName(SourceModel.Expr jobj) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jobjectToQualifiedName), jobj});
		}

		/**
		 * Name binding for function: jobjectToQualifiedName.
		 * @see #jobjectToQualifiedName(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jobjectToQualifiedName = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"jobjectToQualifiedName");

		/**
		 * Helper binding method for function: literalToJObject. 
		 * @param literal
		 * @return the SourceModule.expr representing an application of literalToJObject
		 */
		public static final SourceModel.Expr literalToJObject(SourceModel.Expr literal) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.literalToJObject), literal});
		}

		/**
		 * Name binding for function: literalToJObject.
		 * @see #literalToJObject(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName literalToJObject = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"literalToJObject");

		/**
		 * Helper binding method for function: map_Entry_getKey. 
		 * @param jMap_Entry
		 * @return the SourceModule.expr representing an application of map_Entry_getKey
		 */
		public static final SourceModel.Expr map_Entry_getKey(SourceModel.Expr jMap_Entry) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.map_Entry_getKey), jMap_Entry});
		}

		/**
		 * Name binding for function: map_Entry_getKey.
		 * @see #map_Entry_getKey(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName map_Entry_getKey = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"map_Entry_getKey");

		/**
		 * Helper binding method for function: map_Entry_getValue. 
		 * @param jMap_Entry
		 * @return the SourceModule.expr representing an application of map_Entry_getValue
		 */
		public static final SourceModel.Expr map_Entry_getValue(SourceModel.Expr jMap_Entry) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.map_Entry_getValue), jMap_Entry});
		}

		/**
		 * Name binding for function: map_Entry_getValue.
		 * @see #map_Entry_getValue(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName map_Entry_getValue = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"map_Entry_getValue");

		/**
		 * Helper binding method for function: map_entrySet. 
		 * @param jMap
		 * @return the SourceModule.expr representing an application of map_entrySet
		 */
		public static final SourceModel.Expr map_entrySet(SourceModel.Expr jMap) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.map_entrySet), jMap});
		}

		/**
		 * Name binding for function: map_entrySet.
		 * @see #map_entrySet(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName map_entrySet = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"map_entrySet");

		/**
		 * Select the larger of the two occurs values.
		 * @param o1 (CAL type: <code>Cal.Internal.Optimizer_Expression.Occurs</code>)
		 * @param o2 (CAL type: <code>Cal.Internal.Optimizer_Expression.Occurs</code>)
		 * @return (CAL type: <code>Cal.Internal.Optimizer_Expression.Occurs</code>) 
		 */
		public static final SourceModel.Expr maxOccurs(SourceModel.Expr o1, SourceModel.Expr o2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.maxOccurs), o1, o2});
		}

		/**
		 * Name binding for function: maxOccurs.
		 * @see #maxOccurs(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName maxOccurs = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"maxOccurs");

		/**
		 * Helper binding method for function: notEqualsDataCons. 
		 * @param dc1
		 * @param dc2
		 * @return the SourceModule.expr representing an application of notEqualsDataCons
		 */
		public static final SourceModel.Expr notEqualsDataCons(SourceModel.Expr dc1, SourceModel.Expr dc2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.notEqualsDataCons), dc1, dc2});
		}

		/**
		 * Name binding for function: notEqualsDataCons.
		 * @see #notEqualsDataCons(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName notEqualsDataCons = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"notEqualsDataCons");

		/**
		 * Helper binding method for function: notEqualsLiteral. 
		 * @param l1
		 * @param l2
		 * @return the SourceModule.expr representing an application of notEqualsLiteral
		 */
		public static final SourceModel.Expr notEqualsLiteral(SourceModel.Expr l1, SourceModel.Expr l2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.notEqualsLiteral), l1, l2});
		}

		/**
		 * Name binding for function: notEqualsLiteral.
		 * @see #notEqualsLiteral(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName notEqualsLiteral = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"notEqualsLiteral");

		/**
		 * Returns true if the given name occurs more that once in the given expression. This is slightly 
		 * different that the obvious definition. This is used for inlining so occurs more than once means
		 * if the variable were inlined would the resulting expression be calculated more than once.
		 * @param name (CAL type: <code>Cal.Internal.Optimizer_Expression.QualifiedName</code>)
		 *          The variable to look for.
		 * @param dc (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Internal.Optimizer_Expression.DataCons</code>)
		 *          If the name has a known data constructor then this is it. This is used in the switch case. Look there for an explanation.
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The expression to search
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          True if the given name would be calculated more than once if inlined in the given expression.
		 */
		public static final SourceModel.Expr occursMoreThanOnce(SourceModel.Expr name, SourceModel.Expr dc, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.occursMoreThanOnce), name, dc, expr});
		}

		/**
		 * Name binding for function: occursMoreThanOnce.
		 * @see #occursMoreThanOnce(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName occursMoreThanOnce = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"occursMoreThanOnce");

		/**
		 * Helper binding method for function: optimizerHelper_alt_getFirstTag_asJFunctionalAgent. 
		 * @param alt
		 * @return the SourceModule.expr representing an application of optimizerHelper_alt_getFirstTag_asJFunctionalAgent
		 */
		public static final SourceModel.Expr optimizerHelper_alt_getFirstTag_asJFunctionalAgent(SourceModel.Expr alt) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_alt_getFirstTag_asJFunctionalAgent), alt});
		}

		/**
		 * Name binding for function: optimizerHelper_alt_getFirstTag_asJFunctionalAgent.
		 * @see #optimizerHelper_alt_getFirstTag_asJFunctionalAgent(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName optimizerHelper_alt_getFirstTag_asJFunctionalAgent = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"optimizerHelper_alt_getFirstTag_asJFunctionalAgent");

		/**
		 * Helper binding method for function: optimizerHelper_alt_getFirstTag_asJLiteral. 
		 * @param alt
		 * @return the SourceModule.expr representing an application of optimizerHelper_alt_getFirstTag_asJLiteral
		 */
		public static final SourceModel.Expr optimizerHelper_alt_getFirstTag_asJLiteral(SourceModel.Expr alt) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_alt_getFirstTag_asJLiteral), alt});
		}

		/**
		 * Name binding for function: optimizerHelper_alt_getFirstTag_asJLiteral.
		 * @see #optimizerHelper_alt_getFirstTag_asJLiteral(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName optimizerHelper_alt_getFirstTag_asJLiteral = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"optimizerHelper_alt_getFirstTag_asJLiteral");

		/**
		 * Helper binding method for function: optimizerHelper_alt_getNArguments. 
		 * @param alt
		 * @return the SourceModule.expr representing an application of optimizerHelper_alt_getNArguments
		 */
		public static final SourceModel.Expr optimizerHelper_alt_getNArguments(SourceModel.Expr alt) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_alt_getNArguments), alt});
		}

		/**
		 * Name binding for function: optimizerHelper_alt_getNArguments.
		 * @see #optimizerHelper_alt_getNArguments(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName optimizerHelper_alt_getNArguments = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"optimizerHelper_alt_getNArguments");

		/**
		 * Helper binding method for function: optimizerHelper_alt_getPositionArguments. 
		 * @param altObject
		 * @param index
		 * @return the SourceModule.expr representing an application of optimizerHelper_alt_getPositionArguments
		 */
		public static final SourceModel.Expr optimizerHelper_alt_getPositionArguments(SourceModel.Expr altObject, SourceModel.Expr index) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_alt_getPositionArguments), altObject, index});
		}

		/**
		 * @see #optimizerHelper_alt_getPositionArguments(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param altObject
		 * @param index
		 * @return the SourceModel.Expr representing an application of optimizerHelper_alt_getPositionArguments
		 */
		public static final SourceModel.Expr optimizerHelper_alt_getPositionArguments(SourceModel.Expr altObject, int index) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_alt_getPositionArguments), altObject, SourceModel.Expr.makeIntValue(index)});
		}

		/**
		 * Name binding for function: optimizerHelper_alt_getPositionArguments.
		 * @see #optimizerHelper_alt_getPositionArguments(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName optimizerHelper_alt_getPositionArguments = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"optimizerHelper_alt_getPositionArguments");

		/**
		 * Helper binding method for function: optimizerHelper_alt_new. 
		 * @param switchTag
		 * @param isPositional
		 * @param varsObject
		 * @param altExpr
		 * @return the SourceModule.expr representing an application of optimizerHelper_alt_new
		 */
		public static final SourceModel.Expr optimizerHelper_alt_new(SourceModel.Expr switchTag, SourceModel.Expr isPositional, SourceModel.Expr varsObject, SourceModel.Expr altExpr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_alt_new), switchTag, isPositional, varsObject, altExpr});
		}

		/**
		 * @see #optimizerHelper_alt_new(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param switchTag
		 * @param isPositional
		 * @param varsObject
		 * @param altExpr
		 * @return the SourceModel.Expr representing an application of optimizerHelper_alt_new
		 */
		public static final SourceModel.Expr optimizerHelper_alt_new(SourceModel.Expr switchTag, boolean isPositional, SourceModel.Expr varsObject, SourceModel.Expr altExpr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_alt_new), switchTag, SourceModel.Expr.makeBooleanValue(isPositional), varsObject, altExpr});
		}

		/**
		 * Name binding for function: optimizerHelper_alt_new.
		 * @see #optimizerHelper_alt_new(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName optimizerHelper_alt_new = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"optimizerHelper_alt_new");

		/**
		 * Helper binding method for function: optimizerHelper_alt_tagIsDataConstructor. 
		 * @param alt
		 * @return the SourceModule.expr representing an application of optimizerHelper_alt_tagIsDataConstructor
		 */
		public static final SourceModel.Expr optimizerHelper_alt_tagIsDataConstructor(SourceModel.Expr alt) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_alt_tagIsDataConstructor), alt});
		}

		/**
		 * Name binding for function: optimizerHelper_alt_tagIsDataConstructor.
		 * @see #optimizerHelper_alt_tagIsDataConstructor(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName optimizerHelper_alt_tagIsDataConstructor = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"optimizerHelper_alt_tagIsDataConstructor");

		/**
		 * Helper binding method for function: optimizerHelper_dataCons_isFalse. 
		 * @param functionalAgent
		 * @return the SourceModule.expr representing an application of optimizerHelper_dataCons_isFalse
		 */
		public static final SourceModel.Expr optimizerHelper_dataCons_isFalse(SourceModel.Expr functionalAgent) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_dataCons_isFalse), functionalAgent});
		}

		/**
		 * Name binding for function: optimizerHelper_dataCons_isFalse.
		 * @see #optimizerHelper_dataCons_isFalse(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName optimizerHelper_dataCons_isFalse = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"optimizerHelper_dataCons_isFalse");

		/**
		 * Helper binding method for function: optimizerHelper_dataCons_isTrue. 
		 * @param functionalAgent
		 * @return the SourceModule.expr representing an application of optimizerHelper_dataCons_isTrue
		 */
		public static final SourceModel.Expr optimizerHelper_dataCons_isTrue(SourceModel.Expr functionalAgent) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_dataCons_isTrue), functionalAgent});
		}

		/**
		 * Name binding for function: optimizerHelper_dataCons_isTrue.
		 * @see #optimizerHelper_dataCons_isTrue(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName optimizerHelper_dataCons_isTrue = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"optimizerHelper_dataCons_isTrue");

		/**
		 * Helper binding method for function: optimizerHelper_dataConstructor_asFunctionalAgent. 
		 * @param functionalAgent
		 * @return the SourceModule.expr representing an application of optimizerHelper_dataConstructor_asFunctionalAgent
		 */
		public static final SourceModel.Expr optimizerHelper_dataConstructor_asFunctionalAgent(SourceModel.Expr functionalAgent) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_dataConstructor_asFunctionalAgent), functionalAgent});
		}

		/**
		 * Name binding for function: optimizerHelper_dataConstructor_asFunctionalAgent.
		 * @see #optimizerHelper_dataConstructor_asFunctionalAgent(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName optimizerHelper_dataConstructor_asFunctionalAgent = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"optimizerHelper_dataConstructor_asFunctionalAgent");

		/**
		 * Helper binding method for function: optimizerHelper_dataConstructor_getNumberOfDataTypes. 
		 * @param dc
		 * @return the SourceModule.expr representing an application of optimizerHelper_dataConstructor_getNumberOfDataTypes
		 */
		public static final SourceModel.Expr optimizerHelper_dataConstructor_getNumberOfDataTypes(SourceModel.Expr dc) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_dataConstructor_getNumberOfDataTypes), dc});
		}

		/**
		 * Name binding for function: optimizerHelper_dataConstructor_getNumberOfDataTypes.
		 * @see #optimizerHelper_dataConstructor_getNumberOfDataTypes(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName optimizerHelper_dataConstructor_getNumberOfDataTypes = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"optimizerHelper_dataConstructor_getNumberOfDataTypes");

		/**
		 * Helper binding method for function: optimizerHelper_expression_isNull. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of optimizerHelper_expression_isNull
		 */
		public static final SourceModel.Expr optimizerHelper_expression_isNull(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_expression_isNull), arg_1});
		}

		/**
		 * Name binding for function: optimizerHelper_expression_isNull.
		 * @see #optimizerHelper_expression_isNull(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName optimizerHelper_expression_isNull = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"optimizerHelper_expression_isNull");

		/**
		 * Helper binding method for function: optimizerHelper_expression_new_appl. 
		 * @param expr1
		 * @param expr2
		 * @return the SourceModule.expr representing an application of optimizerHelper_expression_new_appl
		 */
		public static final SourceModel.Expr optimizerHelper_expression_new_appl(SourceModel.Expr expr1, SourceModel.Expr expr2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_expression_new_appl), expr1, expr2});
		}

		/**
		 * Name binding for function: optimizerHelper_expression_new_appl.
		 * @see #optimizerHelper_expression_new_appl(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName optimizerHelper_expression_new_appl = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"optimizerHelper_expression_new_appl");

		/**
		 * Helper binding method for function: optimizerHelper_expression_new_dataConsSelection. 
		 * @param dcValueExpr
		 * @param dataConstructor0
		 * @param fieldIndex
		 * @param errorInfo
		 * @return the SourceModule.expr representing an application of optimizerHelper_expression_new_dataConsSelection
		 */
		public static final SourceModel.Expr optimizerHelper_expression_new_dataConsSelection(SourceModel.Expr dcValueExpr, SourceModel.Expr dataConstructor0, SourceModel.Expr fieldIndex, SourceModel.Expr errorInfo) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_expression_new_dataConsSelection), dcValueExpr, dataConstructor0, fieldIndex, errorInfo});
		}

		/**
		 * @see #optimizerHelper_expression_new_dataConsSelection(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param dcValueExpr
		 * @param dataConstructor0
		 * @param fieldIndex
		 * @param errorInfo
		 * @return the SourceModel.Expr representing an application of optimizerHelper_expression_new_dataConsSelection
		 */
		public static final SourceModel.Expr optimizerHelper_expression_new_dataConsSelection(SourceModel.Expr dcValueExpr, SourceModel.Expr dataConstructor0, int fieldIndex, SourceModel.Expr errorInfo) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_expression_new_dataConsSelection), dcValueExpr, dataConstructor0, SourceModel.Expr.makeIntValue(fieldIndex), errorInfo});
		}

		/**
		 * Name binding for function: optimizerHelper_expression_new_dataConsSelection.
		 * @see #optimizerHelper_expression_new_dataConsSelection(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName optimizerHelper_expression_new_dataConsSelection = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"optimizerHelper_expression_new_dataConsSelection");

		/**
		 * Helper binding method for function: optimizerHelper_expression_new_dataConstructor. 
		 * @param dataConstructor0
		 * @return the SourceModule.expr representing an application of optimizerHelper_expression_new_dataConstructor
		 */
		public static final SourceModel.Expr optimizerHelper_expression_new_dataConstructor(SourceModel.Expr dataConstructor0) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_expression_new_dataConstructor), dataConstructor0});
		}

		/**
		 * Name binding for function: optimizerHelper_expression_new_dataConstructor.
		 * @see #optimizerHelper_expression_new_dataConstructor(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName optimizerHelper_expression_new_dataConstructor = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"optimizerHelper_expression_new_dataConstructor");

		/**
		 * Helper binding method for function: optimizerHelper_expression_new_let. 
		 * @param unqualifiedName
		 * @param defExpr
		 * @param bodyExpr
		 * @param isRecursive
		 * @param varType
		 * @return the SourceModule.expr representing an application of optimizerHelper_expression_new_let
		 */
		public static final SourceModel.Expr optimizerHelper_expression_new_let(SourceModel.Expr unqualifiedName, SourceModel.Expr defExpr, SourceModel.Expr bodyExpr, SourceModel.Expr isRecursive, SourceModel.Expr varType) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_expression_new_let), unqualifiedName, defExpr, bodyExpr, isRecursive, varType});
		}

		/**
		 * @see #optimizerHelper_expression_new_let(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param unqualifiedName
		 * @param defExpr
		 * @param bodyExpr
		 * @param isRecursive
		 * @param varType
		 * @return the SourceModel.Expr representing an application of optimizerHelper_expression_new_let
		 */
		public static final SourceModel.Expr optimizerHelper_expression_new_let(java.lang.String unqualifiedName, SourceModel.Expr defExpr, SourceModel.Expr bodyExpr, boolean isRecursive, SourceModel.Expr varType) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_expression_new_let), SourceModel.Expr.makeStringValue(unqualifiedName), defExpr, bodyExpr, SourceModel.Expr.makeBooleanValue(isRecursive), varType});
		}

		/**
		 * Name binding for function: optimizerHelper_expression_new_let.
		 * @see #optimizerHelper_expression_new_let(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName optimizerHelper_expression_new_let = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"optimizerHelper_expression_new_let");

		/**
		 * Helper binding method for function: optimizerHelper_expression_new_literal. 
		 * @param literal
		 * @return the SourceModule.expr representing an application of optimizerHelper_expression_new_literal
		 */
		public static final SourceModel.Expr optimizerHelper_expression_new_literal(SourceModel.Expr literal) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_expression_new_literal), literal});
		}

		/**
		 * Name binding for function: optimizerHelper_expression_new_literal.
		 * @see #optimizerHelper_expression_new_literal(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName optimizerHelper_expression_new_literal = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"optimizerHelper_expression_new_literal");

		/**
		 * Helper binding method for function: optimizerHelper_expression_new_opaque. 
		 * @param expression
		 * @return the SourceModule.expr representing an application of optimizerHelper_expression_new_opaque
		 */
		public static final SourceModel.Expr optimizerHelper_expression_new_opaque(SourceModel.Expr expression) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_expression_new_opaque), expression});
		}

		/**
		 * Name binding for function: optimizerHelper_expression_new_opaque.
		 * @see #optimizerHelper_expression_new_opaque(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName optimizerHelper_expression_new_opaque = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"optimizerHelper_expression_new_opaque");

		/**
		 * Helper binding method for function: optimizerHelper_expression_new_recordCase. 
		 * @param conditionExpr
		 * @param baseRecordPatternVarName
		 * @param fieldBindingVars
		 * @param resultExpr
		 * @return the SourceModule.expr representing an application of optimizerHelper_expression_new_recordCase
		 */
		public static final SourceModel.Expr optimizerHelper_expression_new_recordCase(SourceModel.Expr conditionExpr, SourceModel.Expr baseRecordPatternVarName, SourceModel.Expr fieldBindingVars, SourceModel.Expr resultExpr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_expression_new_recordCase), conditionExpr, baseRecordPatternVarName, fieldBindingVars, resultExpr});
		}

		/**
		 * @see #optimizerHelper_expression_new_recordCase(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param conditionExpr
		 * @param baseRecordPatternVarName
		 * @param fieldBindingVars
		 * @param resultExpr
		 * @return the SourceModel.Expr representing an application of optimizerHelper_expression_new_recordCase
		 */
		public static final SourceModel.Expr optimizerHelper_expression_new_recordCase(SourceModel.Expr conditionExpr, java.lang.String baseRecordPatternVarName, SourceModel.Expr fieldBindingVars, SourceModel.Expr resultExpr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_expression_new_recordCase), conditionExpr, SourceModel.Expr.makeStringValue(baseRecordPatternVarName), fieldBindingVars, resultExpr});
		}

		/**
		 * Name binding for function: optimizerHelper_expression_new_recordCase.
		 * @see #optimizerHelper_expression_new_recordCase(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName optimizerHelper_expression_new_recordCase = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"optimizerHelper_expression_new_recordCase");

		/**
		 * Helper binding method for function: optimizerHelper_expression_new_recordExtensionLiteral. 
		 * @param extensionFields
		 * @return the SourceModule.expr representing an application of optimizerHelper_expression_new_recordExtensionLiteral
		 */
		public static final SourceModel.Expr optimizerHelper_expression_new_recordExtensionLiteral(SourceModel.Expr extensionFields) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_expression_new_recordExtensionLiteral), extensionFields});
		}

		/**
		 * Name binding for function: optimizerHelper_expression_new_recordExtensionLiteral.
		 * @see #optimizerHelper_expression_new_recordExtensionLiteral(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName optimizerHelper_expression_new_recordExtensionLiteral = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"optimizerHelper_expression_new_recordExtensionLiteral");

		/**
		 * Helper binding method for function: optimizerHelper_expression_new_recordExtensionPolymorphic. 
		 * @param baseRecordExpr
		 * @param extensionFields
		 * @return the SourceModule.expr representing an application of optimizerHelper_expression_new_recordExtensionPolymorphic
		 */
		public static final SourceModel.Expr optimizerHelper_expression_new_recordExtensionPolymorphic(SourceModel.Expr baseRecordExpr, SourceModel.Expr extensionFields) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_expression_new_recordExtensionPolymorphic), baseRecordExpr, extensionFields});
		}

		/**
		 * Name binding for function: optimizerHelper_expression_new_recordExtensionPolymorphic.
		 * @see #optimizerHelper_expression_new_recordExtensionPolymorphic(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName optimizerHelper_expression_new_recordExtensionPolymorphic = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"optimizerHelper_expression_new_recordExtensionPolymorphic");

		/**
		 * Helper binding method for function: optimizerHelper_expression_new_recordSelection. 
		 * @param expression
		 * @param fieldName
		 * @return the SourceModule.expr representing an application of optimizerHelper_expression_new_recordSelection
		 */
		public static final SourceModel.Expr optimizerHelper_expression_new_recordSelection(SourceModel.Expr expression, SourceModel.Expr fieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_expression_new_recordSelection), expression, fieldName});
		}

		/**
		 * Name binding for function: optimizerHelper_expression_new_recordSelection.
		 * @see #optimizerHelper_expression_new_recordSelection(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName optimizerHelper_expression_new_recordSelection = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"optimizerHelper_expression_new_recordSelection");

		/**
		 * Helper binding method for function: optimizerHelper_expression_new_switch. 
		 * @param expression
		 * @param altsObject
		 * @return the SourceModule.expr representing an application of optimizerHelper_expression_new_switch
		 */
		public static final SourceModel.Expr optimizerHelper_expression_new_switch(SourceModel.Expr expression, SourceModel.Expr altsObject) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_expression_new_switch), expression, altsObject});
		}

		/**
		 * Name binding for function: optimizerHelper_expression_new_switch.
		 * @see #optimizerHelper_expression_new_switch(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName optimizerHelper_expression_new_switch = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"optimizerHelper_expression_new_switch");

		/**
		 * Helper binding method for function: optimizerHelper_expression_new_var_entity. 
		 * @param entity
		 * @return the SourceModule.expr representing an application of optimizerHelper_expression_new_var_entity
		 */
		public static final SourceModel.Expr optimizerHelper_expression_new_var_entity(SourceModel.Expr entity) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_expression_new_var_entity), entity});
		}

		/**
		 * Name binding for function: optimizerHelper_expression_new_var_entity.
		 * @see #optimizerHelper_expression_new_var_entity(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName optimizerHelper_expression_new_var_entity = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"optimizerHelper_expression_new_var_entity");

		/**
		 * Helper binding method for function: optimizerHelper_expression_new_var_name. 
		 * @param name
		 * @return the SourceModule.expr representing an application of optimizerHelper_expression_new_var_name
		 */
		public static final SourceModel.Expr optimizerHelper_expression_new_var_name(SourceModel.Expr name) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_expression_new_var_name), name});
		}

		/**
		 * Name binding for function: optimizerHelper_expression_new_var_name.
		 * @see #optimizerHelper_expression_new_var_name(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName optimizerHelper_expression_new_var_name = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"optimizerHelper_expression_new_var_name");

		/**
		 * Helper binding method for function: optimizerHelper_fieldName_getOrdinal. 
		 * @param fn
		 * @return the SourceModule.expr representing an application of optimizerHelper_fieldName_getOrdinal
		 */
		public static final SourceModel.Expr optimizerHelper_fieldName_getOrdinal(SourceModel.Expr fn) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_fieldName_getOrdinal), fn});
		}

		/**
		 * Name binding for function: optimizerHelper_fieldName_getOrdinal.
		 * @see #optimizerHelper_fieldName_getOrdinal(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName optimizerHelper_fieldName_getOrdinal = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"optimizerHelper_fieldName_getOrdinal");

		/**
		 * Helper binding method for function: optimizerHelper_fieldName_ordinal_new. 
		 * @param ordinal
		 * @return the SourceModule.expr representing an application of optimizerHelper_fieldName_ordinal_new
		 */
		public static final SourceModel.Expr optimizerHelper_fieldName_ordinal_new(SourceModel.Expr ordinal) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_fieldName_ordinal_new), ordinal});
		}

		/**
		 * @see #optimizerHelper_fieldName_ordinal_new(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param ordinal
		 * @return the SourceModel.Expr representing an application of optimizerHelper_fieldName_ordinal_new
		 */
		public static final SourceModel.Expr optimizerHelper_fieldName_ordinal_new(int ordinal) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_fieldName_ordinal_new), SourceModel.Expr.makeIntValue(ordinal)});
		}

		/**
		 * Name binding for function: optimizerHelper_fieldName_ordinal_new.
		 * @see #optimizerHelper_fieldName_ordinal_new(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName optimizerHelper_fieldName_ordinal_new = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"optimizerHelper_fieldName_ordinal_new");

		/**
		 * Helper binding method for function: optimizerHelper_fieldName_textual_new. 
		 * @param text
		 * @return the SourceModule.expr representing an application of optimizerHelper_fieldName_textual_new
		 */
		public static final SourceModel.Expr optimizerHelper_fieldName_textual_new(SourceModel.Expr text) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_fieldName_textual_new), text});
		}

		/**
		 * @see #optimizerHelper_fieldName_textual_new(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param text
		 * @return the SourceModel.Expr representing an application of optimizerHelper_fieldName_textual_new
		 */
		public static final SourceModel.Expr optimizerHelper_fieldName_textual_new(java.lang.String text) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_fieldName_textual_new), SourceModel.Expr.makeStringValue(text)});
		}

		/**
		 * Name binding for function: optimizerHelper_fieldName_textual_new.
		 * @see #optimizerHelper_fieldName_textual_new(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName optimizerHelper_fieldName_textual_new = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"optimizerHelper_fieldName_textual_new");

		/**
		 * Helper binding method for function: optimizerHelper_functionalAgent_asDataConstructor. 
		 * @param functionalAgent
		 * @return the SourceModule.expr representing an application of optimizerHelper_functionalAgent_asDataConstructor
		 */
		public static final SourceModel.Expr optimizerHelper_functionalAgent_asDataConstructor(SourceModel.Expr functionalAgent) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_functionalAgent_asDataConstructor), functionalAgent});
		}

		/**
		 * Name binding for function: optimizerHelper_functionalAgent_asDataConstructor.
		 * @see #optimizerHelper_functionalAgent_asDataConstructor(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName optimizerHelper_functionalAgent_asDataConstructor = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"optimizerHelper_functionalAgent_asDataConstructor");

		/**
		 * Helper binding method for function: optimizerHelper_functionalAgent_getTypeExprExact. 
		 * @param functionalAgent
		 * @return the SourceModule.expr representing an application of optimizerHelper_functionalAgent_getTypeExprExact
		 */
		public static final SourceModel.Expr optimizerHelper_functionalAgent_getTypeExprExact(SourceModel.Expr functionalAgent) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_functionalAgent_getTypeExprExact), functionalAgent});
		}

		/**
		 * Name binding for function: optimizerHelper_functionalAgent_getTypeExprExact.
		 * @see #optimizerHelper_functionalAgent_getTypeExprExact(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName optimizerHelper_functionalAgent_getTypeExprExact = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"optimizerHelper_functionalAgent_getTypeExprExact");

		/**
		 * Helper binding method for function: optimizerHelper_getAltType. 
		 * @param alt
		 * @return the SourceModule.expr representing an application of optimizerHelper_getAltType
		 */
		public static final SourceModel.Expr optimizerHelper_getAltType(SourceModel.Expr alt) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_getAltType), alt});
		}

		/**
		 * Name binding for function: optimizerHelper_getAltType.
		 * @see #optimizerHelper_getAltType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName optimizerHelper_getAltType = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"optimizerHelper_getAltType");

		/**
		 * Helper binding method for function: optimizerHelper_getExpressionType. 
		 * @param expression
		 * @return the SourceModule.expr representing an application of optimizerHelper_getExpressionType
		 */
		public static final SourceModel.Expr optimizerHelper_getExpressionType(SourceModel.Expr expression) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_getExpressionType), expression});
		}

		/**
		 * Name binding for function: optimizerHelper_getExpressionType.
		 * @see #optimizerHelper_getExpressionType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName optimizerHelper_getExpressionType = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"optimizerHelper_getExpressionType");

		/**
		 * Helper binding method for function: optimizerHelper_getFieldNameType. 
		 * @param fieldName
		 * @return the SourceModule.expr representing an application of optimizerHelper_getFieldNameType
		 */
		public static final SourceModel.Expr optimizerHelper_getFieldNameType(SourceModel.Expr fieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_getFieldNameType), fieldName});
		}

		/**
		 * Name binding for function: optimizerHelper_getFieldNameType.
		 * @see #optimizerHelper_getFieldNameType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName optimizerHelper_getFieldNameType = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"optimizerHelper_getFieldNameType");

		/**
		 * Helper binding method for function: optimizerHelper_getLiteralType. 
		 * @param literal
		 * @return the SourceModule.expr representing an application of optimizerHelper_getLiteralType
		 */
		public static final SourceModel.Expr optimizerHelper_getLiteralType(SourceModel.Expr literal) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_getLiteralType), literal});
		}

		/**
		 * Name binding for function: optimizerHelper_getLiteralType.
		 * @see #optimizerHelper_getLiteralType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName optimizerHelper_getLiteralType = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"optimizerHelper_getLiteralType");

		/**
		 * Helper binding method for function: optimizerHelper_let_getDef. 
		 * @param letObject
		 * @param index
		 * @return the SourceModule.expr representing an application of optimizerHelper_let_getDef
		 */
		public static final SourceModel.Expr optimizerHelper_let_getDef(SourceModel.Expr letObject, SourceModel.Expr index) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_let_getDef), letObject, index});
		}

		/**
		 * @see #optimizerHelper_let_getDef(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param letObject
		 * @param index
		 * @return the SourceModel.Expr representing an application of optimizerHelper_let_getDef
		 */
		public static final SourceModel.Expr optimizerHelper_let_getDef(SourceModel.Expr letObject, int index) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_let_getDef), letObject, SourceModel.Expr.makeIntValue(index)});
		}

		/**
		 * Name binding for function: optimizerHelper_let_getDef.
		 * @see #optimizerHelper_let_getDef(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName optimizerHelper_let_getDef = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"optimizerHelper_let_getDef");

		/**
		 * Helper binding method for function: optimizerHelper_let_getDefType. 
		 * @param letObject
		 * @param index
		 * @return the SourceModule.expr representing an application of optimizerHelper_let_getDefType
		 */
		public static final SourceModel.Expr optimizerHelper_let_getDefType(SourceModel.Expr letObject, SourceModel.Expr index) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_let_getDefType), letObject, index});
		}

		/**
		 * @see #optimizerHelper_let_getDefType(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param letObject
		 * @param index
		 * @return the SourceModel.Expr representing an application of optimizerHelper_let_getDefType
		 */
		public static final SourceModel.Expr optimizerHelper_let_getDefType(SourceModel.Expr letObject, int index) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_let_getDefType), letObject, SourceModel.Expr.makeIntValue(index)});
		}

		/**
		 * Name binding for function: optimizerHelper_let_getDefType.
		 * @see #optimizerHelper_let_getDefType(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName optimizerHelper_let_getDefType = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"optimizerHelper_let_getDefType");

		/**
		 * Helper binding method for function: optimizerHelper_let_getVar. 
		 * @param letObject
		 * @param index
		 * @return the SourceModule.expr representing an application of optimizerHelper_let_getVar
		 */
		public static final SourceModel.Expr optimizerHelper_let_getVar(SourceModel.Expr letObject, SourceModel.Expr index) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_let_getVar), letObject, index});
		}

		/**
		 * @see #optimizerHelper_let_getVar(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param letObject
		 * @param index
		 * @return the SourceModel.Expr representing an application of optimizerHelper_let_getVar
		 */
		public static final SourceModel.Expr optimizerHelper_let_getVar(SourceModel.Expr letObject, int index) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_let_getVar), letObject, SourceModel.Expr.makeIntValue(index)});
		}

		/**
		 * Name binding for function: optimizerHelper_let_getVar.
		 * @see #optimizerHelper_let_getVar(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName optimizerHelper_let_getVar = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"optimizerHelper_let_getVar");

		/**
		 * Helper binding method for function: optimizerHelper_object_isDataConstructor. 
		 * @param object
		 * @return the SourceModule.expr representing an application of optimizerHelper_object_isDataConstructor
		 */
		public static final SourceModel.Expr optimizerHelper_object_isDataConstructor(SourceModel.Expr object) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_object_isDataConstructor), object});
		}

		/**
		 * Name binding for function: optimizerHelper_object_isDataConstructor.
		 * @see #optimizerHelper_object_isDataConstructor(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName optimizerHelper_object_isDataConstructor = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"optimizerHelper_object_isDataConstructor");

		/**
		 * Helper binding method for function: optimizerHelper_type_asRecordType. 
		 * @param type
		 * @return the SourceModule.expr representing an application of optimizerHelper_type_asRecordType
		 */
		public static final SourceModel.Expr optimizerHelper_type_asRecordType(SourceModel.Expr type) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_type_asRecordType), type});
		}

		/**
		 * Name binding for function: optimizerHelper_type_asRecordType.
		 * @see #optimizerHelper_type_asRecordType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName optimizerHelper_type_asRecordType = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"optimizerHelper_type_asRecordType");

		/**
		 * Helper binding method for function: optimizerHelper_var_getType. 
		 * @param var
		 * @return the SourceModule.expr representing an application of optimizerHelper_var_getType
		 */
		public static final SourceModel.Expr optimizerHelper_var_getType(SourceModel.Expr var) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optimizerHelper_var_getType), var});
		}

		/**
		 * Name binding for function: optimizerHelper_var_getType.
		 * @see #optimizerHelper_var_getType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName optimizerHelper_var_getType = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"optimizerHelper_var_getType");

		/**
		 * Optimizes the case where a single alt value exists.
		 * @param alt (CAL type: <code>Cal.Internal.Optimizer_Expression.Alt</code>)
		 * @return (CAL type: <code>Cal.Internal.Optimizer_Expression.JExpression_Switch_SwitchAlt</code>) 
		 */
		public static final SourceModel.Expr outputAlt(SourceModel.Expr alt) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputAlt), alt});
		}

		/**
		 * Name binding for function: outputAlt.
		 * @see #outputAlt(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName outputAlt = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"outputAlt");

		/**
		 * Optimizes the case where a single expression value exists.
		 * @param expression (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>Cal.Internal.Optimizer_Expression.JExpression</code>) 
		 */
		public static final SourceModel.Expr outputExpression(SourceModel.Expr expression) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputExpression), expression});
		}

		/**
		 * Name binding for function: outputExpression.
		 * @see #outputExpression(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName outputExpression = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"outputExpression");

		/**
		 * Optimizes the case where a single fieldName value exists.
		 * @param fieldName (CAL type: <code>Cal.Internal.Optimizer_Expression.FieldName</code>)
		 * @return (CAL type: <code>Cal.Internal.Optimizer_Expression.JFieldName</code>) 
		 */
		public static final SourceModel.Expr outputFieldName(SourceModel.Expr fieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputFieldName), fieldName});
		}

		/**
		 * Name binding for function: outputFieldName.
		 * @see #outputFieldName(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName outputFieldName = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"outputFieldName");

		/**
		 * Optimizes the case where a single literal value exists.
		 * @param literal (CAL type: <code>Cal.Internal.Optimizer_Expression.Literal</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.JObject</code>) 
		 */
		public static final SourceModel.Expr outputLiteral(SourceModel.Expr literal) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputLiteral), literal});
		}

		/**
		 * Name binding for function: outputLiteral.
		 * @see #outputLiteral(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName outputLiteral = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"outputLiteral");

		/**
		 * Optimizes the case where a single qualifiedName value exists.
		 * @param qn (CAL type: <code>Cal.Internal.Optimizer_Expression.QualifiedName</code>)
		 * @return (CAL type: <code>Cal.Internal.Optimizer_Expression.JQualifiedName</code>) 
		 */
		public static final SourceModel.Expr outputQualifiedName(SourceModel.Expr qn) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputQualifiedName), qn});
		}

		/**
		 * Name binding for function: outputQualifiedName.
		 * @see #outputQualifiedName(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName outputQualifiedName = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"outputQualifiedName");

		/**
		 * Helper binding method for function: prelude_error. 
		 * @return the SourceModule.expr representing an application of prelude_error
		 */
		public static final SourceModel.Expr prelude_error() {
			return SourceModel.Expr.Var.make(Functions.prelude_error);
		}

		/**
		 * Name binding for function: prelude_error.
		 * @see #prelude_error()
		 */
		public static final QualifiedName prelude_error = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"prelude_error");

		/**
		 * Helper binding method for function: prelude_seq. 
		 * @return the SourceModule.expr representing an application of prelude_seq
		 */
		public static final SourceModel.Expr prelude_seq() {
			return SourceModel.Expr.Var.make(Functions.prelude_seq);
		}

		/**
		 * Name binding for function: prelude_seq.
		 * @see #prelude_seq()
		 */
		public static final QualifiedName prelude_seq = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"prelude_seq");

		/**
		 * Helper binding method for function: qualifiedNameToJObject. 
		 * @param qn
		 * @return the SourceModule.expr representing an application of qualifiedNameToJObject
		 */
		public static final SourceModel.Expr qualifiedNameToJObject(SourceModel.Expr qn) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.qualifiedNameToJObject), qn});
		}

		/**
		 * Name binding for function: qualifiedNameToJObject.
		 * @see #qualifiedNameToJObject(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName qualifiedNameToJObject = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"qualifiedNameToJObject");

		/**
		 * Helper binding method for function: qualifiedName_compare. 
		 * @param qn1
		 * @param qn2
		 * @return the SourceModule.expr representing an application of qualifiedName_compare
		 */
		public static final SourceModel.Expr qualifiedName_compare(SourceModel.Expr qn1, SourceModel.Expr qn2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.qualifiedName_compare), qn1, qn2});
		}

		/**
		 * Name binding for function: qualifiedName_compare.
		 * @see #qualifiedName_compare(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName qualifiedName_compare = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"qualifiedName_compare");

		/**
		 * Helper binding method for function: qualifiedName_equals. 
		 * @param qn1
		 * @param qn2
		 * @return the SourceModule.expr representing an application of qualifiedName_equals
		 */
		public static final SourceModel.Expr qualifiedName_equals(SourceModel.Expr qn1, SourceModel.Expr qn2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.qualifiedName_equals), qn1, qn2});
		}

		/**
		 * Name binding for function: qualifiedName_equals.
		 * @see #qualifiedName_equals(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName qualifiedName_equals = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"qualifiedName_equals");

		/**
		 * Helper binding method for function: qualifiedName_getName. 
		 * @param qn
		 * @return the SourceModule.expr representing an application of qualifiedName_getName
		 */
		public static final SourceModel.Expr qualifiedName_getName(SourceModel.Expr qn) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.qualifiedName_getName), qn});
		}

		/**
		 * Name binding for function: qualifiedName_getName.
		 * @see #qualifiedName_getName(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName qualifiedName_getName = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"qualifiedName_getName");

		/**
		 * Returns true iff the given qualified name is a top level name.
		 * @param qn (CAL type: <code>Cal.Internal.Optimizer_Expression.QualifiedName</code>)
		 *          The qualified name to test.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr qualifiedName_isTopLevel(SourceModel.Expr qn) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.qualifiedName_isTopLevel), qn});
		}

		/**
		 * Name binding for function: qualifiedName_isTopLevel.
		 * @see #qualifiedName_isTopLevel(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName qualifiedName_isTopLevel = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"qualifiedName_isTopLevel");

		/**
		 * Helper binding method for function: qualifiedName_notEquals. 
		 * @param qn1
		 * @param qn2
		 * @return the SourceModule.expr representing an application of qualifiedName_notEquals
		 */
		public static final SourceModel.Expr qualifiedName_notEquals(SourceModel.Expr qn1, SourceModel.Expr qn2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.qualifiedName_notEquals), qn1, qn2});
		}

		/**
		 * Name binding for function: qualifiedName_notEquals.
		 * @see #qualifiedName_notEquals(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName qualifiedName_notEquals = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"qualifiedName_notEquals");

		/**
		 * Helper binding method for function: qualifiedName_toString. 
		 * @param qn
		 * @return the SourceModule.expr representing an application of qualifiedName_toString
		 */
		public static final SourceModel.Expr qualifiedName_toString(SourceModel.Expr qn) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.qualifiedName_toString), qn});
		}

		/**
		 * Name binding for function: qualifiedName_toString.
		 * @see #qualifiedName_toString(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName qualifiedName_toString = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"qualifiedName_toString");

		/**
		 * Remove the most outer lambda variables from the expression.
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 *          The expression to remove the most outer lambda variables from.
		 * @return (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>) 
		 *          The expression that results from removing the most outer lambda variables from the given expression.
		 */
		public static final SourceModel.Expr removeLambdaVars(SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.removeLambdaVars), expr});
		}

		/**
		 * Name binding for function: removeLambdaVars.
		 * @see #removeLambdaVars(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName removeLambdaVars = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"removeLambdaVars");

		/**
		 * Helper binding method for function: set_iterator. 
		 * @param jSet
		 * @return the SourceModule.expr representing an application of set_iterator
		 */
		public static final SourceModel.Expr set_iterator(SourceModel.Expr jSet) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.set_iterator), jSet});
		}

		/**
		 * Name binding for function: set_iterator.
		 * @see #set_iterator(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName set_iterator = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"set_iterator");

		/**
		 * Helper binding method for function: showAlt. 
		 * @param alt
		 * @return the SourceModule.expr representing an application of showAlt
		 */
		public static final SourceModel.Expr showAlt(SourceModel.Expr alt) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showAlt), alt});
		}

		/**
		 * Name binding for function: showAlt.
		 * @see #showAlt(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showAlt = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"showAlt");

		/**
		 * Helper binding method for function: showAlt2. 
		 * @param alt
		 * @param maxDepth
		 * @return the SourceModule.expr representing an application of showAlt2
		 */
		public static final SourceModel.Expr showAlt2(SourceModel.Expr alt, SourceModel.Expr maxDepth) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showAlt2), alt, maxDepth});
		}

		/**
		 * @see #showAlt2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param alt
		 * @param maxDepth
		 * @return the SourceModel.Expr representing an application of showAlt2
		 */
		public static final SourceModel.Expr showAlt2(SourceModel.Expr alt, int maxDepth) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showAlt2), alt, SourceModel.Expr.makeIntValue(maxDepth)});
		}

		/**
		 * Name binding for function: showAlt2.
		 * @see #showAlt2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showAlt2 = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"showAlt2");

		/**
		 * Helper binding method for function: showAltHelper. 
		 * @param showExpression
		 * @param caseConsts
		 * @param vars
		 * @param expr
		 * @return the SourceModule.expr representing an application of showAltHelper
		 */
		public static final SourceModel.Expr showAltHelper(SourceModel.Expr showExpression, SourceModel.Expr caseConsts, SourceModel.Expr vars, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showAltHelper), showExpression, caseConsts, vars, expr});
		}

		/**
		 * Name binding for function: showAltHelper.
		 * @see #showAltHelper(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showAltHelper = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"showAltHelper");

		/**
		 * Helper binding method for function: showAltStructure. 
		 * @param alt
		 * @return the SourceModule.expr representing an application of showAltStructure
		 */
		public static final SourceModel.Expr showAltStructure(SourceModel.Expr alt) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showAltStructure), alt});
		}

		/**
		 * Name binding for function: showAltStructure.
		 * @see #showAltStructure(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showAltStructure = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"showAltStructure");

		/**
		 * Helper binding method for function: showCaseConst. 
		 * @param cc
		 * @return the SourceModule.expr representing an application of showCaseConst
		 */
		public static final SourceModel.Expr showCaseConst(SourceModel.Expr cc) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showCaseConst), cc});
		}

		/**
		 * Name binding for function: showCaseConst.
		 * @see #showCaseConst(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showCaseConst = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"showCaseConst");

		/**
		 * Helper binding method for function: showDataCons. 
		 * @param dc
		 * @return the SourceModule.expr representing an application of showDataCons
		 */
		public static final SourceModel.Expr showDataCons(SourceModel.Expr dc) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showDataCons), dc});
		}

		/**
		 * Name binding for function: showDataCons.
		 * @see #showDataCons(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showDataCons = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"showDataCons");

		/**
		 * Helper binding method for function: showExpression. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of showExpression
		 */
		public static final SourceModel.Expr showExpression(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showExpression), arg_1});
		}

		/**
		 * Name binding for function: showExpression.
		 * @see #showExpression(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showExpression = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"showExpression");

		/**
		 * Helper binding method for function: showExpression2. 
		 * @param expression
		 * @param maxDepth
		 * @return the SourceModule.expr representing an application of showExpression2
		 */
		public static final SourceModel.Expr showExpression2(SourceModel.Expr expression, SourceModel.Expr maxDepth) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showExpression2), expression, maxDepth});
		}

		/**
		 * @see #showExpression2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param expression
		 * @param maxDepth
		 * @return the SourceModel.Expr representing an application of showExpression2
		 */
		public static final SourceModel.Expr showExpression2(SourceModel.Expr expression, int maxDepth) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showExpression2), expression, SourceModel.Expr.makeIntValue(maxDepth)});
		}

		/**
		 * Name binding for function: showExpression2.
		 * @see #showExpression2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showExpression2 = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"showExpression2");

		/**
		 * Helper binding method for function: showExpressionNice. 
		 * @param expression
		 * @return the SourceModule.expr representing an application of showExpressionNice
		 */
		public static final SourceModel.Expr showExpressionNice(SourceModel.Expr expression) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showExpressionNice), expression});
		}

		/**
		 * Name binding for function: showExpressionNice.
		 * @see #showExpressionNice(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showExpressionNice = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"showExpressionNice");

		/**
		 * Helper binding method for function: showExpressionStructure. 
		 * @param expression
		 * @return the SourceModule.expr representing an application of showExpressionStructure
		 */
		public static final SourceModel.Expr showExpressionStructure(SourceModel.Expr expression) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showExpressionStructure), expression});
		}

		/**
		 * Name binding for function: showExpressionStructure.
		 * @see #showExpressionStructure(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showExpressionStructure = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"showExpressionStructure");

		/**
		 * Helper binding method for function: showLiteral. 
		 * @param literal
		 * @return the SourceModule.expr representing an application of showLiteral
		 */
		public static final SourceModel.Expr showLiteral(SourceModel.Expr literal) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showLiteral), literal});
		}

		/**
		 * Name binding for function: showLiteral.
		 * @see #showLiteral(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showLiteral = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"showLiteral");

		/**
		 * Helper binding method for function: showQualifiedName. 
		 * @param qn
		 * @return the SourceModule.expr representing an application of showQualifiedName
		 */
		public static final SourceModel.Expr showQualifiedName(SourceModel.Expr qn) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showQualifiedName), qn});
		}

		/**
		 * Name binding for function: showQualifiedName.
		 * @see #showQualifiedName(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showQualifiedName = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"showQualifiedName");

		/**
		 * Helper binding method for function: sortedMap_entrySet. 
		 * @param jSortedMap
		 * @return the SourceModule.expr representing an application of sortedMap_entrySet
		 */
		public static final SourceModel.Expr sortedMap_entrySet(SourceModel.Expr jSortedMap) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.sortedMap_entrySet), jSortedMap});
		}

		/**
		 * Name binding for function: sortedMap_entrySet.
		 * @see #sortedMap_entrySet(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName sortedMap_entrySet = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"sortedMap_entrySet");

		/**
		 * Same as substitute but it is known what varExpr does not contain any variables that might 
		 * be bound in the expression.
		 * @param var (CAL type: <code>Cal.Internal.Optimizer_Expression.QualifiedName</code>)
		 * @param varExpr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @param expr (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>)
		 * @return (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>) 
		 */
		public static final SourceModel.Expr substituteNoRename(SourceModel.Expr var, SourceModel.Expr varExpr, SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.substituteNoRename), var, varExpr, expr});
		}

		/**
		 * Name binding for function: substituteNoRename.
		 * @see #substituteNoRename(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName substituteNoRename = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"substituteNoRename");

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
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"trace2");

		/**
		 * The inverse of flatten. Takes a list of expression and makes an expression where
		 * the first element of the list is the functor, the second is the first arguement ...
		 * <p>
		 * Example: unflatten [f1, a1, a2] == ((App f1 a1) a2)
		 * 
		 * @param exprs (CAL type: <code>[Cal.Internal.Optimizer_Expression.Expression]</code>)
		 *          The list of expressions to unflatten into a single expression.
		 * @return (CAL type: <code>Cal.Internal.Optimizer_Expression.Expression</code>) 
		 *          The result of flattening the given expression.
		 */
		public static final SourceModel.Expr unflattenExpression(SourceModel.Expr exprs) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.unflattenExpression), exprs});
		}

		/**
		 * Name binding for function: unflattenExpression.
		 * @see #unflattenExpression(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName unflattenExpression = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"unflattenExpression");

		/**
		 * Helper binding method for function: unflattenExpressionExamples. 
		 * @return the SourceModule.expr representing an application of unflattenExpressionExamples
		 */
		public static final SourceModel.Expr unflattenExpressionExamples() {
			return 
				SourceModel.Expr.Var.make(Functions.unflattenExpressionExamples);
		}

		/**
		 * Name binding for function: unflattenExpressionExamples.
		 * @see #unflattenExpressionExamples()
		 */
		public static final QualifiedName unflattenExpressionExamples = 
			QualifiedName.make(
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"unflattenExpressionExamples");

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
				CAL_Optimizer_Expression_internal.MODULE_NAME, 
				"unitTests");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = 314708422;

}
