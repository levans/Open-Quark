/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_System.java)
 * was generated from CAL module: Cal.Core.System.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Core.System module from Java code.
 *  
 * Creation date: Wed Oct 24 10:53:51 PDT 2007
 * --!>
 *  
 */

package org.openquark.cal.module.Cal.Core;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * This module contains functions for interacting with the current CAL
 * execution environment.
 * @author Joseph Wong
 */
public final class CAL_System {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Core.System");

	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Core.System module.
	 */
	public static final class TypeConstructors {
		/**
		 * Represents a Java interface for cleaning up resources (sessions, connections, files, etc.).
		 */
		public static final QualifiedName Cleanable = 
			QualifiedName.make(CAL_System.MODULE_NAME, "Cleanable");

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Core.System module.
	 */
	public static final class Functions {
		/**
		 * If true, the CAL run-time supports concurrent reduction of CAL programs on a single 
		 * execution context. Note that the runtime is thread-safe even when this flag is false in the sense
		 * that concurrent evaluation of CAL entry points on separate execution contexts that do not share graph
		 * is thread-safe (assuming that referenced foreign entities that are shared are thread-safe).
		 * <p>
		 * When this flag is true, the run-time supports concurrent threads doing CAL evaluation on a single execution context.
		 * The concurrent run-time is enabled by using the LECC machine and setting the sytem property 
		 * org.openquark.cal.machine.lecc.concurrent_runtime.
		 * 
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr concurrentRuntime() {
			return SourceModel.Expr.Var.make(Functions.concurrentRuntime);
		}

		/**
		 * Name binding for function: concurrentRuntime.
		 * @see #concurrentRuntime()
		 */
		public static final QualifiedName concurrentRuntime = 
			QualifiedName.make(CAL_System.MODULE_NAME, "concurrentRuntime");

		/**
		 * Looks up the specified system property of the current execution context.
		 * @param key (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the name of the property.
		 * @return (CAL type: <code>Cal.Core.Prelude.JObject</code>) 
		 *          the value of the property, or the Java <code>null</code> value if there is no property with that key.
		 */
		public static final SourceModel.Expr getProperty(SourceModel.Expr key) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getProperty), key});
		}

		/**
		 * @see #getProperty(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param key
		 * @return the SourceModel.Expr representing an application of getProperty
		 */
		public static final SourceModel.Expr getProperty(java.lang.String key) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getProperty), SourceModel.Expr.makeStringValue(key)});
		}

		/**
		 * Name binding for function: getProperty.
		 * @see #getProperty(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getProperty = 
			QualifiedName.make(CAL_System.MODULE_NAME, "getProperty");

		/**
		 * Returns whether the specified system property is defined in the current execution context.
		 * @param key (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the name of the property.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the specified property is defined; <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr hasProperty(SourceModel.Expr key) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.hasProperty), key});
		}

		/**
		 * @see #hasProperty(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param key
		 * @return the SourceModel.Expr representing an application of hasProperty
		 */
		public static final SourceModel.Expr hasProperty(java.lang.String key) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.hasProperty), SourceModel.Expr.makeStringValue(key)});
		}

		/**
		 * Name binding for function: hasProperty.
		 * @see #hasProperty(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName hasProperty = 
			QualifiedName.make(CAL_System.MODULE_NAME, "hasProperty");

		/**
		 * Returns the keys of the system properties defined in the current execution context.
		 * @return (CAL type: <code>[Cal.Core.Prelude.String]</code>) 
		 *          a list of the keys.
		 */
		public static final SourceModel.Expr propertyKeys() {
			return SourceModel.Expr.Var.make(Functions.propertyKeys);
		}

		/**
		 * Name binding for function: propertyKeys.
		 * @see #propertyKeys()
		 */
		public static final QualifiedName propertyKeys = 
			QualifiedName.make(CAL_System.MODULE_NAME, "propertyKeys");

		/**
		 * Registers a cleanup hook to be run whenever the current execution context is cleaned up. Not a pure function.
		 * <p>
		 * If the cleanup hook is null, this function throws a NullPointerException.
		 * <p>
		 * When the cleanup hooks for the execution context are run, they are run in the order in which they were
		 * originally registered. If a cleanup hook is registered multiple times, it will also be run the same number of times.
		 * 
		 * @param cleanable (CAL type: <code>Cal.Core.System.Cleanable</code>)
		 *          the cleanup hook to be registered.
		 * @return (CAL type: <code>()</code>) 
		 */
		public static final SourceModel.Expr registerCleanable(SourceModel.Expr cleanable) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.registerCleanable), cleanable});
		}

		/**
		 * Name binding for function: registerCleanable.
		 * @see #registerCleanable(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName registerCleanable = 
			QualifiedName.make(CAL_System.MODULE_NAME, "registerCleanable");

		/**
		 * Registers a cleanup hook based on a CAL to be run whenever the current execution context is cleaned up. 
		 * Not a pure function.
		 * <p>
		 * When the cleanup hooks for the execution context are run, they are run in the order in which they were
		 * originally registered. If a cleanup hook is registered multiple times, it will also be run the same number of times.
		 * 
		 * @param cleanupFn (CAL type: <code>a -> ()</code>)
		 *          the CAL clean up function for the specified value
		 * @param value (CAL type: <code>a</code>)
		 *          the value to be registered as cleanable
		 * @return (CAL type: <code>a</code>) 
		 *          the original value, once the registration is complete
		 */
		public static final SourceModel.Expr registerCleanableFunction(SourceModel.Expr cleanupFn, SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.registerCleanableFunction), cleanupFn, value});
		}

		/**
		 * Name binding for function: registerCleanableFunction.
		 * @see #registerCleanableFunction(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName registerCleanableFunction = 
			QualifiedName.make(
				CAL_System.MODULE_NAME, 
				"registerCleanableFunction");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = -492345752;

}
