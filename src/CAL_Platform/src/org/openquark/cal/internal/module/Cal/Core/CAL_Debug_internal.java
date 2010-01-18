/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_Debug_internal.java)
 * was generated from CAL module: Cal.Core.Debug.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Core.Debug module from Java code.
 *  
 * Creation date: Mon Oct 15 14:57:09 PDT 2007
 * --!>
 *  
 */

package org.openquark.cal.internal.module.Cal.Core;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * Defines some useful functions and type classes for use when debugging CAL programs.
 * <p>
 * In general, the functions in this module should not be relied upon in production code. They are
 * intended for debugging purposes only, and their precise semantics and operation are subject to change.
 * <p>
 * <code>Cal.Core.Debug.Show</code> instances are provided for the <code>Cal.Core.Prelude</code> types.  Show instances for non-Prelude
 * types should be added to their home modules, not to this one.
 * 
 * @author Bo Ilic
 */
public final class CAL_Debug_internal {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Core.Debug");

	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Core.Debug module.
	 */
	public static final class TypeConstructors {
		/** Name binding for TypeConsApp: InternalValueStats. */
		public static final QualifiedName InternalValueStats = 
			QualifiedName.make(
				CAL_Debug_internal.MODULE_NAME, 
				"InternalValueStats");

		/** Name binding for TypeConsApp: JMachineType. */
		public static final QualifiedName JMachineType = 
			QualifiedName.make(CAL_Debug_internal.MODULE_NAME, "JMachineType");

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Core.Debug module.
	 */
	public static final class Functions {
		/**
		 * Helper binding method for function: debug_getMachineType. 
		 * @param calValue
		 * @return the SourceModule.expr representing an application of debug_getMachineType
		 */
		public static final SourceModel.Expr debug_getMachineType(SourceModel.Expr calValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.debug_getMachineType), calValue});
		}

		/**
		 * Name binding for function: debug_getMachineType.
		 * @see #debug_getMachineType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName debug_getMachineType = 
			QualifiedName.make(
				CAL_Debug_internal.MODULE_NAME, 
				"debug_getMachineType");

		/**
		 * Helper binding method for function: executionContext_addTracedFunction. 
		 * @param executionContext
		 * @param tracedFunction
		 * @return the SourceModule.expr representing an application of executionContext_addTracedFunction
		 */
		public static final SourceModel.Expr executionContext_addTracedFunction(SourceModel.Expr executionContext, SourceModel.Expr tracedFunction) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.executionContext_addTracedFunction), executionContext, tracedFunction});
		}

		/**
		 * @see #executionContext_addTracedFunction(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param executionContext
		 * @param tracedFunction
		 * @return the SourceModel.Expr representing an application of executionContext_addTracedFunction
		 */
		public static final SourceModel.Expr executionContext_addTracedFunction(SourceModel.Expr executionContext, java.lang.String tracedFunction) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.executionContext_addTracedFunction), executionContext, SourceModel.Expr.makeStringValue(tracedFunction)});
		}

		/**
		 * Name binding for function: executionContext_addTracedFunction.
		 * @see #executionContext_addTracedFunction(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName executionContext_addTracedFunction = 
			QualifiedName.make(
				CAL_Debug_internal.MODULE_NAME, 
				"executionContext_addTracedFunction");

		/**
		 * Helper binding method for function: executionContext_getTracedFunctions. 
		 * @param executionContext
		 * @return the SourceModule.expr representing an application of executionContext_getTracedFunctions
		 */
		public static final SourceModel.Expr executionContext_getTracedFunctions(SourceModel.Expr executionContext) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.executionContext_getTracedFunctions), executionContext});
		}

		/**
		 * Name binding for function: executionContext_getTracedFunctions.
		 * @see #executionContext_getTracedFunctions(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName executionContext_getTracedFunctions = 
			QualifiedName.make(
				CAL_Debug_internal.MODULE_NAME, 
				"executionContext_getTracedFunctions");

		/**
		 * Helper binding method for function: executionContext_isTracingEnabled. 
		 * @param executionContext
		 * @return the SourceModule.expr representing an application of executionContext_isTracingEnabled
		 */
		public static final SourceModel.Expr executionContext_isTracingEnabled(SourceModel.Expr executionContext) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.executionContext_isTracingEnabled), executionContext});
		}

		/**
		 * Name binding for function: executionContext_isTracingEnabled.
		 * @see #executionContext_isTracingEnabled(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName executionContext_isTracingEnabled = 
			QualifiedName.make(
				CAL_Debug_internal.MODULE_NAME, 
				"executionContext_isTracingEnabled");

		/**
		 * Helper binding method for function: executionContext_removeTracedFunction. 
		 * @param executionContext
		 * @param tracedFunction
		 * @return the SourceModule.expr representing an application of executionContext_removeTracedFunction
		 */
		public static final SourceModel.Expr executionContext_removeTracedFunction(SourceModel.Expr executionContext, SourceModel.Expr tracedFunction) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.executionContext_removeTracedFunction), executionContext, tracedFunction});
		}

		/**
		 * @see #executionContext_removeTracedFunction(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param executionContext
		 * @param tracedFunction
		 * @return the SourceModel.Expr representing an application of executionContext_removeTracedFunction
		 */
		public static final SourceModel.Expr executionContext_removeTracedFunction(SourceModel.Expr executionContext, java.lang.String tracedFunction) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.executionContext_removeTracedFunction), executionContext, SourceModel.Expr.makeStringValue(tracedFunction)});
		}

		/**
		 * Name binding for function: executionContext_removeTracedFunction.
		 * @see #executionContext_removeTracedFunction(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName executionContext_removeTracedFunction = 
			QualifiedName.make(
				CAL_Debug_internal.MODULE_NAME, 
				"executionContext_removeTracedFunction");

		/**
		 * Helper binding method for function: executionContext_setTraceShowsFunctionArgs. 
		 * @param executionContext
		 * @param traceShowsFunctionArgs
		 * @return the SourceModule.expr representing an application of executionContext_setTraceShowsFunctionArgs
		 */
		public static final SourceModel.Expr executionContext_setTraceShowsFunctionArgs(SourceModel.Expr executionContext, SourceModel.Expr traceShowsFunctionArgs) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.executionContext_setTraceShowsFunctionArgs), executionContext, traceShowsFunctionArgs});
		}

		/**
		 * @see #executionContext_setTraceShowsFunctionArgs(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param executionContext
		 * @param traceShowsFunctionArgs
		 * @return the SourceModel.Expr representing an application of executionContext_setTraceShowsFunctionArgs
		 */
		public static final SourceModel.Expr executionContext_setTraceShowsFunctionArgs(SourceModel.Expr executionContext, boolean traceShowsFunctionArgs) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.executionContext_setTraceShowsFunctionArgs), executionContext, SourceModel.Expr.makeBooleanValue(traceShowsFunctionArgs)});
		}

		/**
		 * Name binding for function: executionContext_setTraceShowsFunctionArgs.
		 * @see #executionContext_setTraceShowsFunctionArgs(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName executionContext_setTraceShowsFunctionArgs = 
			QualifiedName.make(
				CAL_Debug_internal.MODULE_NAME, 
				"executionContext_setTraceShowsFunctionArgs");

		/**
		 * Helper binding method for function: executionContext_setTraceShowsThreadName. 
		 * @param executionContext
		 * @param traceShowsThreadName
		 * @return the SourceModule.expr representing an application of executionContext_setTraceShowsThreadName
		 */
		public static final SourceModel.Expr executionContext_setTraceShowsThreadName(SourceModel.Expr executionContext, SourceModel.Expr traceShowsThreadName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.executionContext_setTraceShowsThreadName), executionContext, traceShowsThreadName});
		}

		/**
		 * @see #executionContext_setTraceShowsThreadName(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param executionContext
		 * @param traceShowsThreadName
		 * @return the SourceModel.Expr representing an application of executionContext_setTraceShowsThreadName
		 */
		public static final SourceModel.Expr executionContext_setTraceShowsThreadName(SourceModel.Expr executionContext, boolean traceShowsThreadName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.executionContext_setTraceShowsThreadName), executionContext, SourceModel.Expr.makeBooleanValue(traceShowsThreadName)});
		}

		/**
		 * Name binding for function: executionContext_setTraceShowsThreadName.
		 * @see #executionContext_setTraceShowsThreadName(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName executionContext_setTraceShowsThreadName = 
			QualifiedName.make(
				CAL_Debug_internal.MODULE_NAME, 
				"executionContext_setTraceShowsThreadName");

		/**
		 * Helper binding method for function: executionContext_setTracingEnabled. 
		 * @param executionContext
		 * @param tracingEnabled
		 * @return the SourceModule.expr representing an application of executionContext_setTracingEnabled
		 */
		public static final SourceModel.Expr executionContext_setTracingEnabled(SourceModel.Expr executionContext, SourceModel.Expr tracingEnabled) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.executionContext_setTracingEnabled), executionContext, tracingEnabled});
		}

		/**
		 * @see #executionContext_setTracingEnabled(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param executionContext
		 * @param tracingEnabled
		 * @return the SourceModel.Expr representing an application of executionContext_setTracingEnabled
		 */
		public static final SourceModel.Expr executionContext_setTracingEnabled(SourceModel.Expr executionContext, boolean tracingEnabled) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.executionContext_setTracingEnabled), executionContext, SourceModel.Expr.makeBooleanValue(tracingEnabled)});
		}

		/**
		 * Name binding for function: executionContext_setTracingEnabled.
		 * @see #executionContext_setTracingEnabled(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName executionContext_setTracingEnabled = 
			QualifiedName.make(
				CAL_Debug_internal.MODULE_NAME, 
				"executionContext_setTracingEnabled");

		/**
		 * Helper binding method for function: executionContext_traceShowsFunctionArgs. 
		 * @param executionContext
		 * @return the SourceModule.expr representing an application of executionContext_traceShowsFunctionArgs
		 */
		public static final SourceModel.Expr executionContext_traceShowsFunctionArgs(SourceModel.Expr executionContext) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.executionContext_traceShowsFunctionArgs), executionContext});
		}

		/**
		 * Name binding for function: executionContext_traceShowsFunctionArgs.
		 * @see #executionContext_traceShowsFunctionArgs(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName executionContext_traceShowsFunctionArgs = 
			QualifiedName.make(
				CAL_Debug_internal.MODULE_NAME, 
				"executionContext_traceShowsFunctionArgs");

		/**
		 * Helper binding method for function: executionContext_traceShowsThreadName. 
		 * @param executionContext
		 * @return the SourceModule.expr representing an application of executionContext_traceShowsThreadName
		 */
		public static final SourceModel.Expr executionContext_traceShowsThreadName(SourceModel.Expr executionContext) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.executionContext_traceShowsThreadName), executionContext});
		}

		/**
		 * Name binding for function: executionContext_traceShowsThreadName.
		 * @see #executionContext_traceShowsThreadName(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName executionContext_traceShowsThreadName = 
			QualifiedName.make(
				CAL_Debug_internal.MODULE_NAME, 
				"executionContext_traceShowsThreadName");

		/**
		 * Helper binding method for function: inputMachineType. 
		 * @param jMachineType
		 * @return the SourceModule.expr representing an application of inputMachineType
		 */
		public static final SourceModel.Expr inputMachineType(SourceModel.Expr jMachineType) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inputMachineType), jMachineType});
		}

		/**
		 * Name binding for function: inputMachineType.
		 * @see #inputMachineType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName inputMachineType = 
			QualifiedName.make(
				CAL_Debug_internal.MODULE_NAME, 
				"inputMachineType");

		/**
		 * Helper binding method for function: inputMachineTypeFromJObject. 
		 * @param jMachineType
		 * @return the SourceModule.expr representing an application of inputMachineTypeFromJObject
		 */
		public static final SourceModel.Expr inputMachineTypeFromJObject(SourceModel.Expr jMachineType) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inputMachineTypeFromJObject), jMachineType});
		}

		/**
		 * Name binding for function: inputMachineTypeFromJObject.
		 * @see #inputMachineTypeFromJObject(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName inputMachineTypeFromJObject = 
			QualifiedName.make(
				CAL_Debug_internal.MODULE_NAME, 
				"inputMachineTypeFromJObject");

		/**
		 * Helper binding method for function: internalValueStatsHelper. 
		 * @param calValue
		 * @return the SourceModule.expr representing an application of internalValueStatsHelper
		 */
		public static final SourceModel.Expr internalValueStatsHelper(SourceModel.Expr calValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.internalValueStatsHelper), calValue});
		}

		/**
		 * Name binding for function: internalValueStatsHelper.
		 * @see #internalValueStatsHelper(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName internalValueStatsHelper = 
			QualifiedName.make(
				CAL_Debug_internal.MODULE_NAME, 
				"internalValueStatsHelper");

		/**
		 * Helper binding method for function: jMachineType_G. 
		 * @return the SourceModule.expr representing an application of jMachineType_G
		 */
		public static final SourceModel.Expr jMachineType_G() {
			return SourceModel.Expr.Var.make(Functions.jMachineType_G);
		}

		/**
		 * Name binding for function: jMachineType_G.
		 * @see #jMachineType_G()
		 */
		public static final QualifiedName jMachineType_G = 
			QualifiedName.make(CAL_Debug_internal.MODULE_NAME, "jMachineType_G");

		/**
		 * Helper binding method for function: jMachineType_Lecc. 
		 * @return the SourceModule.expr representing an application of jMachineType_Lecc
		 */
		public static final SourceModel.Expr jMachineType_Lecc() {
			return SourceModel.Expr.Var.make(Functions.jMachineType_Lecc);
		}

		/**
		 * Name binding for function: jMachineType_Lecc.
		 * @see #jMachineType_Lecc()
		 */
		public static final QualifiedName jMachineType_Lecc = 
			QualifiedName.make(
				CAL_Debug_internal.MODULE_NAME, 
				"jMachineType_Lecc");

		/**
		 * Helper binding method for function: jPrintToStandardError. 
		 * @param message
		 * @return the SourceModule.expr representing an application of jPrintToStandardError
		 */
		public static final SourceModel.Expr jPrintToStandardError(SourceModel.Expr message) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jPrintToStandardError), message});
		}

		/**
		 * @see #jPrintToStandardError(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param message
		 * @return the SourceModel.Expr representing an application of jPrintToStandardError
		 */
		public static final SourceModel.Expr jPrintToStandardError(java.lang.String message) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jPrintToStandardError), SourceModel.Expr.makeStringValue(message)});
		}

		/**
		 * Name binding for function: jPrintToStandardError.
		 * @see #jPrintToStandardError(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jPrintToStandardError = 
			QualifiedName.make(
				CAL_Debug_internal.MODULE_NAME, 
				"jPrintToStandardError");

		/**
		 * Helper binding method for function: jPrintToStandardOut. 
		 * @param message
		 * @return the SourceModule.expr representing an application of jPrintToStandardOut
		 */
		public static final SourceModel.Expr jPrintToStandardOut(SourceModel.Expr message) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jPrintToStandardOut), message});
		}

		/**
		 * @see #jPrintToStandardOut(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param message
		 * @return the SourceModel.Expr representing an application of jPrintToStandardOut
		 */
		public static final SourceModel.Expr jPrintToStandardOut(java.lang.String message) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jPrintToStandardOut), SourceModel.Expr.makeStringValue(message)});
		}

		/**
		 * Name binding for function: jPrintToStandardOut.
		 * @see #jPrintToStandardOut(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jPrintToStandardOut = 
			QualifiedName.make(
				CAL_Debug_internal.MODULE_NAME, 
				"jPrintToStandardOut");

		/**
		 * Helper binding method for function: outputMachineTypeToJObject. 
		 * @param machineType
		 * @return the SourceModule.expr representing an application of outputMachineTypeToJObject
		 */
		public static final SourceModel.Expr outputMachineTypeToJObject(SourceModel.Expr machineType) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputMachineTypeToJObject), machineType});
		}

		/**
		 * Name binding for function: outputMachineTypeToJObject.
		 * @see #outputMachineTypeToJObject(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName outputMachineTypeToJObject = 
			QualifiedName.make(
				CAL_Debug_internal.MODULE_NAME, 
				"outputMachineTypeToJObject");

		/**
		 * Calculates the difference between two values as a percentage of the first value
		 * and rounds the result to two decimal places.
		 * @param x (CAL type: <code>Cal.Core.Prelude.Long</code>)
		 * @param y (CAL type: <code>Cal.Core.Prelude.Long</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 */
		public static final SourceModel.Expr percentDif(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.percentDif), x, y});
		}

		/**
		 * @see #percentDif(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @param y
		 * @return the SourceModel.Expr representing an application of percentDif
		 */
		public static final SourceModel.Expr percentDif(long x, long y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.percentDif), SourceModel.Expr.makeLongValue(x), SourceModel.Expr.makeLongValue(y)});
		}

		/**
		 * Name binding for function: percentDif.
		 * @see #percentDif(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName percentDif = 
			QualifiedName.make(CAL_Debug_internal.MODULE_NAME, "percentDif");

		/**
		 * Helper binding method for function: showBoolean. 
		 * @param x
		 * @return the SourceModule.expr representing an application of showBoolean
		 */
		public static final SourceModel.Expr showBoolean(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showBoolean), x});
		}

		/**
		 * @see #showBoolean(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @return the SourceModel.Expr representing an application of showBoolean
		 */
		public static final SourceModel.Expr showBoolean(boolean x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showBoolean), SourceModel.Expr.makeBooleanValue(x)});
		}

		/**
		 * Name binding for function: showBoolean.
		 * @see #showBoolean(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showBoolean = 
			QualifiedName.make(CAL_Debug_internal.MODULE_NAME, "showBoolean");

		/**
		 * Used for derived Show instances for foreign types with implementation type byte.
		 * @param arg_1 (CAL type: <code>Cal.Core.Prelude.Byte</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr showByte(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showByte), arg_1});
		}

		/**
		 * @see #showByte(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of showByte
		 */
		public static final SourceModel.Expr showByte(byte arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showByte), SourceModel.Expr.makeByteValue(arg_1)});
		}

		/**
		 * Name binding for function: showByte.
		 * @see #showByte(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showByte = 
			QualifiedName.make(CAL_Debug_internal.MODULE_NAME, "showByte");

		/**
		 * 
		 * @param c (CAL type: <code>Cal.Core.Prelude.Char</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the Char value, surrounded by single quotes.
		 */
		public static final SourceModel.Expr showChar(SourceModel.Expr c) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showChar), c});
		}

		/**
		 * @see #showChar(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param c
		 * @return the SourceModel.Expr representing an application of showChar
		 */
		public static final SourceModel.Expr showChar(char c) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showChar), SourceModel.Expr.makeCharValue(c)});
		}

		/**
		 * Name binding for function: showChar.
		 * @see #showChar(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showChar = 
			QualifiedName.make(CAL_Debug_internal.MODULE_NAME, "showChar");

		/**
		 * handles the case where the Decimal object is null
		 * @param decimalValue (CAL type: <code>Cal.Core.Prelude.Decimal</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr showDecimal(SourceModel.Expr decimalValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showDecimal), decimalValue});
		}

		/**
		 * Name binding for function: showDecimal.
		 * @see #showDecimal(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showDecimal = 
			QualifiedName.make(CAL_Debug_internal.MODULE_NAME, "showDecimal");

		/**
		 * Helper binding method for function: showDefault. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of showDefault
		 */
		public static final SourceModel.Expr showDefault(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showDefault), arg_1});
		}

		/**
		 * Name binding for function: showDefault.
		 * @see #showDefault(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showDefault = 
			QualifiedName.make(CAL_Debug_internal.MODULE_NAME, "showDefault");

		/**
		 * Used for derived Show instances for foreign types with implementation type double.
		 * @param arg_1 (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr showDouble(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showDouble), arg_1});
		}

		/**
		 * @see #showDouble(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of showDouble
		 */
		public static final SourceModel.Expr showDouble(double arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showDouble), SourceModel.Expr.makeDoubleValue(arg_1)});
		}

		/**
		 * Name binding for function: showDouble.
		 * @see #showDouble(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showDouble = 
			QualifiedName.make(CAL_Debug_internal.MODULE_NAME, "showDouble");

		/**
		 * Helper binding method for function: showEither. 
		 * @param x
		 * @return the SourceModule.expr representing an application of showEither
		 */
		public static final SourceModel.Expr showEither(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showEither), x});
		}

		/**
		 * Name binding for function: showEither.
		 * @see #showEither(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showEither = 
			QualifiedName.make(CAL_Debug_internal.MODULE_NAME, "showEither");

		/**
		 * Used for derived Show instances for foreign types with implementation type float.
		 * @param arg_1 (CAL type: <code>Cal.Core.Prelude.Float</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr showFloat(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showFloat), arg_1});
		}

		/**
		 * @see #showFloat(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of showFloat
		 */
		public static final SourceModel.Expr showFloat(float arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showFloat), SourceModel.Expr.makeFloatValue(arg_1)});
		}

		/**
		 * Name binding for function: showFloat.
		 * @see #showFloat(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showFloat = 
			QualifiedName.make(CAL_Debug_internal.MODULE_NAME, "showFloat");

		/**
		 * Used for derived Show instances for foreign types with implementation type boolean.
		 * @param arg_1 (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr showForeignBoolean(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showForeignBoolean), arg_1});
		}

		/**
		 * @see #showForeignBoolean(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of showForeignBoolean
		 */
		public static final SourceModel.Expr showForeignBoolean(boolean arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showForeignBoolean), SourceModel.Expr.makeBooleanValue(arg_1)});
		}

		/**
		 * Name binding for function: showForeignBoolean.
		 * @see #showForeignBoolean(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showForeignBoolean = 
			QualifiedName.make(
				CAL_Debug_internal.MODULE_NAME, 
				"showForeignBoolean");

		/**
		 * Used for derived Show instances for foreign types with implementation type char.
		 * @param arg_1 (CAL type: <code>Cal.Core.Prelude.Char</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr showForeignChar(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showForeignChar), arg_1});
		}

		/**
		 * @see #showForeignChar(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of showForeignChar
		 */
		public static final SourceModel.Expr showForeignChar(char arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showForeignChar), SourceModel.Expr.makeCharValue(arg_1)});
		}

		/**
		 * Name binding for function: showForeignChar.
		 * @see #showForeignChar(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showForeignChar = 
			QualifiedName.make(
				CAL_Debug_internal.MODULE_NAME, 
				"showForeignChar");

		/**
		 * Used for derived Show instances for foreign types with implementation type int.
		 * @param arg_1 (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr showInt(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showInt), arg_1});
		}

		/**
		 * @see #showInt(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of showInt
		 */
		public static final SourceModel.Expr showInt(int arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showInt), SourceModel.Expr.makeIntValue(arg_1)});
		}

		/**
		 * Name binding for function: showInt.
		 * @see #showInt(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showInt = 
			QualifiedName.make(CAL_Debug_internal.MODULE_NAME, "showInt");

		/**
		 * handles the case where the Integer object is null
		 * @param integerValue (CAL type: <code>Cal.Core.Prelude.Integer</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr showInteger(SourceModel.Expr integerValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showInteger), integerValue});
		}

		/**
		 * Name binding for function: showInteger.
		 * @see #showInteger(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showInteger = 
			QualifiedName.make(CAL_Debug_internal.MODULE_NAME, "showInteger");

		/**
		 * Helper binding method for function: showInternalValue. 
		 * @param calValue
		 * @return the SourceModule.expr representing an application of showInternalValue
		 */
		public static final SourceModel.Expr showInternalValue(SourceModel.Expr calValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showInternalValue), calValue});
		}

		/**
		 * Name binding for function: showInternalValue.
		 * @see #showInternalValue(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showInternalValue = 
			QualifiedName.make(
				CAL_Debug_internal.MODULE_NAME, 
				"showInternalValue");

		/**
		 * Helper binding method for function: showInternalValueGraph. 
		 * @param calValue
		 * @return the SourceModule.expr representing an application of showInternalValueGraph
		 */
		public static final SourceModel.Expr showInternalValueGraph(SourceModel.Expr calValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showInternalValueGraph), calValue});
		}

		/**
		 * Name binding for function: showInternalValueGraph.
		 * @see #showInternalValueGraph(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showInternalValueGraph = 
			QualifiedName.make(
				CAL_Debug_internal.MODULE_NAME, 
				"showInternalValueGraph");

		/**
		 * handles the case where the JCollection object is null
		 * @param collection (CAL type: <code>Cal.Core.Prelude.JCollection</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr showJCollection(SourceModel.Expr collection) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showJCollection), collection});
		}

		/**
		 * Name binding for function: showJCollection.
		 * @see #showJCollection(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showJCollection = 
			QualifiedName.make(
				CAL_Debug_internal.MODULE_NAME, 
				"showJCollection");

		/**
		 * handles the case where the JList object is null
		 * @param list (CAL type: <code>Cal.Core.Prelude.JList</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr showJList(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showJList), list});
		}

		/**
		 * Name binding for function: showJList.
		 * @see #showJList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showJList = 
			QualifiedName.make(CAL_Debug_internal.MODULE_NAME, "showJList");

		/**
		 * handles the case where the JMap object is null
		 * @param map (CAL type: <code>Cal.Core.Prelude.JMap</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr showJMap(SourceModel.Expr map) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showJMap), map});
		}

		/**
		 * Name binding for function: showJMap.
		 * @see #showJMap(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showJMap = 
			QualifiedName.make(CAL_Debug_internal.MODULE_NAME, "showJMap");

		/**
		 * Used for derived Show instances for foreign types with an object implementation type.
		 * note: we want to handle the null object values here without terminating in an error
		 * and thus don't implement as java.lang.Object.toString().
		 * @param arg_1 (CAL type: <code>Cal.Core.Prelude.JObject</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr showJObject(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showJObject), arg_1});
		}

		/**
		 * Name binding for function: showJObject.
		 * @see #showJObject(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showJObject = 
			QualifiedName.make(CAL_Debug_internal.MODULE_NAME, "showJObject");

		/**
		 * Helper binding method for function: showList. 
		 * @param list
		 * @return the SourceModule.expr representing an application of showList
		 */
		public static final SourceModel.Expr showList(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showList), list});
		}

		/**
		 * Name binding for function: showList.
		 * @see #showList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showList = 
			QualifiedName.make(CAL_Debug_internal.MODULE_NAME, "showList");

		/**
		 * Used for derived Show instances for foreign types with implementation type long.
		 * @param arg_1 (CAL type: <code>Cal.Core.Prelude.Long</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr showLong(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showLong), arg_1});
		}

		/**
		 * @see #showLong(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of showLong
		 */
		public static final SourceModel.Expr showLong(long arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showLong), SourceModel.Expr.makeLongValue(arg_1)});
		}

		/**
		 * Name binding for function: showLong.
		 * @see #showLong(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showLong = 
			QualifiedName.make(CAL_Debug_internal.MODULE_NAME, "showLong");

		/**
		 * Helper binding method for function: showMaybe. 
		 * @param maybe
		 * @return the SourceModule.expr representing an application of showMaybe
		 */
		public static final SourceModel.Expr showMaybe(SourceModel.Expr maybe) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showMaybe), maybe});
		}

		/**
		 * Name binding for function: showMaybe.
		 * @see #showMaybe(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showMaybe = 
			QualifiedName.make(CAL_Debug_internal.MODULE_NAME, "showMaybe");

		/**
		 * Helper binding method for function: showOrdering. 
		 * @param x
		 * @return the SourceModule.expr representing an application of showOrdering
		 */
		public static final SourceModel.Expr showOrdering(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showOrdering), x});
		}

		/**
		 * Name binding for function: showOrdering.
		 * @see #showOrdering(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showOrdering = 
			QualifiedName.make(CAL_Debug_internal.MODULE_NAME, "showOrdering");

		/**
		 * Helper binding method for function: showRecord. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of showRecord
		 */
		public static final SourceModel.Expr showRecord(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showRecord), arg_1});
		}

		/**
		 * Name binding for function: showRecord.
		 * @see #showRecord(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showRecord = 
			QualifiedName.make(CAL_Debug_internal.MODULE_NAME, "showRecord");

		/**
		 * Used for derived Show instances for foreign types with implementation type short.
		 * @param arg_1 (CAL type: <code>Cal.Core.Prelude.Short</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr showShort(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showShort), arg_1});
		}

		/**
		 * @see #showShort(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of showShort
		 */
		public static final SourceModel.Expr showShort(short arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showShort), SourceModel.Expr.makeShortValue(arg_1)});
		}

		/**
		 * Name binding for function: showShort.
		 * @see #showShort(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showShort = 
			QualifiedName.make(CAL_Debug_internal.MODULE_NAME, "showShort");

		/**
		 * 
		 * @param s (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the String value, surrounded by double quotes if not null, and "null" otherwise
		 */
		public static final SourceModel.Expr showString(SourceModel.Expr s) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showString), s});
		}

		/**
		 * @see #showString(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param s
		 * @return the SourceModel.Expr representing an application of showString
		 */
		public static final SourceModel.Expr showString(java.lang.String s) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showString), SourceModel.Expr.makeStringValue(s)});
		}

		/**
		 * Name binding for function: showString.
		 * @see #showString(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showString = 
			QualifiedName.make(CAL_Debug_internal.MODULE_NAME, "showString");

		/**
		 * Helper binding method for function: showUnit. 
		 * @param x
		 * @return the SourceModule.expr representing an application of showUnit
		 */
		public static final SourceModel.Expr showUnit(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showUnit), x});
		}

		/**
		 * Name binding for function: showUnit.
		 * @see #showUnit(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showUnit = 
			QualifiedName.make(CAL_Debug_internal.MODULE_NAME, "showUnit");

		/**
		 * Helper binding method for function: thread_sleep. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of thread_sleep
		 */
		public static final SourceModel.Expr thread_sleep(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.thread_sleep), arg_1});
		}

		/**
		 * @see #thread_sleep(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @return the SourceModel.Expr representing an application of thread_sleep
		 */
		public static final SourceModel.Expr thread_sleep(long arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.thread_sleep), SourceModel.Expr.makeLongValue(arg_1)});
		}

		/**
		 * Name binding for function: thread_sleep.
		 * @see #thread_sleep(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName thread_sleep = 
			QualifiedName.make(CAL_Debug_internal.MODULE_NAME, "thread_sleep");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = 1441503181;

}
