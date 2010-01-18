/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_System_internal.java)
 * was generated from CAL module: Cal.Core.System.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Core.System module from Java code.
 *  
 * Creation date: Tue Oct 23 09:49:53 PDT 2007
 * --!>
 *  
 */

package org.openquark.cal.internal.module.Cal.Core;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * This module contains functions for interacting with the current CAL
 * execution environment.
 * @author Joseph Wong
 */
public final class CAL_System_internal {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Core.System");

	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Core.System module.
	 */
	public static final class TypeConstructors {
		/**
		 * This foreign type represents the set of properties associated with an execution context.
		 * <p>
		 * This type encapsulates an <em>immutable</em> map of key-value pairs which are exposed in CAL as
		 * system properties. Only strings can be used as keys.
		 * <p>
		 * There are a number of keys whose values are constrained to be of a particular type, e.g.
		 * the value for the "cal.locale" property must be of type <code>java.util.Locale</code>. For these
		 * special keys, dedicated functions are available for accessing their values (e.g. <code>Cal.Utilities.Locale.currentLocale</code>).
		 * These properties are known as <em>system properties</em>.
		 * <p>
		 * For other keys, their conresponding values can be objects of any type. It is up to the client
		 * code to coordinate the setting of the property on the Java side with the retrieval on the CAL side.
		 */
		public static final QualifiedName ExecutionContextProperties = 
			QualifiedName.make(
				CAL_System_internal.MODULE_NAME, 
				"ExecutionContextProperties");

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Core.System module.
	 */
	public static final class Functions {
		/**
		 * Returns the <code>Cal.Core.System.ExecutionContextProperties</code> instance encapsulating the set of properties associated with
		 * the current execution context.
		 * @return (CAL type: <code>Cal.Core.System.ExecutionContextProperties</code>) 
		 *          the <code>Cal.Core.System.ExecutionContextProperties</code> instance for the current execution context.
		 */
		public static final SourceModel.Expr executionContextProperties() {
			return 
				SourceModel.Expr.Var.make(Functions.executionContextProperties);
		}

		/**
		 * Name binding for function: executionContextProperties.
		 * @see #executionContextProperties()
		 */
		public static final QualifiedName executionContextProperties = 
			QualifiedName.make(
				CAL_System_internal.MODULE_NAME, 
				"executionContextProperties");

		/**
		 * Returns the <code>Cal.Core.System.ExecutionContextProperties</code> instance encapsulating the set of properties associated with
		 * the given execution context.
		 * @param executionContext (CAL type: <code>Cal.Core.Prelude.ExecutionContext</code>)
		 *          the execution context whose properties are to be returned.
		 * @return (CAL type: <code>Cal.Core.System.ExecutionContextProperties</code>) 
		 *          the <code>Cal.Core.System.ExecutionContextProperties</code> instance for the given execution context.
		 */
		public static final SourceModel.Expr jGetProperties(SourceModel.Expr executionContext) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jGetProperties), executionContext});
		}

		/**
		 * Name binding for function: jGetProperties.
		 * @see #jGetProperties(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jGetProperties = 
			QualifiedName.make(
				CAL_System_internal.MODULE_NAME, 
				"jGetProperties");

		/**
		 * Looks up the specified property in the given <code>Cal.Core.System.ExecutionContextProperties</code> instance.
		 * @param executionContextProperties (CAL type: <code>Cal.Core.System.ExecutionContextProperties</code>)
		 *          the instance encapsulating the set of properties associated with an execution context.
		 * @param key (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the name of the property.
		 * @return (CAL type: <code>Cal.Core.Prelude.JObject</code>) 
		 *          the value of the property, or the Java <code>null</code> value if there is no property with that key.
		 */
		public static final SourceModel.Expr jGetProperty(SourceModel.Expr executionContextProperties, SourceModel.Expr key) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jGetProperty), executionContextProperties, key});
		}

		/**
		 * @see #jGetProperty(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param executionContextProperties
		 * @param key
		 * @return the SourceModel.Expr representing an application of jGetProperty
		 */
		public static final SourceModel.Expr jGetProperty(SourceModel.Expr executionContextProperties, java.lang.String key) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jGetProperty), executionContextProperties, SourceModel.Expr.makeStringValue(key)});
		}

		/**
		 * Name binding for function: jGetProperty.
		 * @see #jGetProperty(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jGetProperty = 
			QualifiedName.make(CAL_System_internal.MODULE_NAME, "jGetProperty");

		/**
		 * Returns the keys of the properties in the given <code>Cal.Core.System.ExecutionContextProperties</code> instance.
		 * @param executionContextProperties (CAL type: <code>Cal.Core.System.ExecutionContextProperties</code>)
		 *          the instance encapsulating the set of properties associated with an execution context.
		 * @return (CAL type: <code>Cal.Core.Prelude.JList</code>) 
		 *          a list of the keys.
		 */
		public static final SourceModel.Expr jGetPropertyKeys(SourceModel.Expr executionContextProperties) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jGetPropertyKeys), executionContextProperties});
		}

		/**
		 * Name binding for function: jGetPropertyKeys.
		 * @see #jGetPropertyKeys(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jGetPropertyKeys = 
			QualifiedName.make(
				CAL_System_internal.MODULE_NAME, 
				"jGetPropertyKeys");

		/**
		 * Returns whether the specified property is in the given <code>Cal.Core.System.ExecutionContextProperties</code> instance.
		 * @param executionContextProperties (CAL type: <code>Cal.Core.System.ExecutionContextProperties</code>)
		 *          the instance encapsulating the set of properties associated with an execution context.
		 * @param key (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the name of the property.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the specified property is defined; <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr jHasProperty(SourceModel.Expr executionContextProperties, SourceModel.Expr key) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jHasProperty), executionContextProperties, key});
		}

		/**
		 * @see #jHasProperty(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param executionContextProperties
		 * @param key
		 * @return the SourceModel.Expr representing an application of jHasProperty
		 */
		public static final SourceModel.Expr jHasProperty(SourceModel.Expr executionContextProperties, java.lang.String key) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jHasProperty), executionContextProperties, SourceModel.Expr.makeStringValue(key)});
		}

		/**
		 * Name binding for function: jHasProperty.
		 * @see #jHasProperty(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jHasProperty = 
			QualifiedName.make(CAL_System_internal.MODULE_NAME, "jHasProperty");

		/**
		 * Registers a cleanup hook to be run whenever the given execution context is cleaned up. Not a pure function.
		 * <p>
		 * If the cleanup hook is null, this function throws a NullPointerException. 
		 * 
		 * @param executionContext (CAL type: <code>Cal.Core.Prelude.ExecutionContext</code>)
		 *          the execution context with which the cleanup hook should be registered.
		 * @param cleanable (CAL type: <code>Cal.Core.System.Cleanable</code>)
		 *          the cleanup hook to be registered.
		 * @return (CAL type: <code>()</code>) 
		 */
		public static final SourceModel.Expr jRegisterCleanable(SourceModel.Expr executionContext, SourceModel.Expr cleanable) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jRegisterCleanable), executionContext, cleanable});
		}

		/**
		 * Name binding for function: jRegisterCleanable.
		 * @see #jRegisterCleanable(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jRegisterCleanable = 
			QualifiedName.make(
				CAL_System_internal.MODULE_NAME, 
				"jRegisterCleanable");

		/**
		 * Helper binding method for function: jRegisterCleanableFunction. 
		 * @param executionContext
		 * @param cleanupFunction
		 * @return the SourceModule.expr representing an application of jRegisterCleanableFunction
		 */
		public static final SourceModel.Expr jRegisterCleanableFunction(SourceModel.Expr executionContext, SourceModel.Expr cleanupFunction) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jRegisterCleanableFunction), executionContext, cleanupFunction});
		}

		/**
		 * Name binding for function: jRegisterCleanableFunction.
		 * @see #jRegisterCleanableFunction(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jRegisterCleanableFunction = 
			QualifiedName.make(
				CAL_System_internal.MODULE_NAME, 
				"jRegisterCleanableFunction");

		/**
		 * Helper binding method for function: showCleanable. 
		 * @param cleanable
		 * @return the SourceModule.expr representing an application of showCleanable
		 */
		public static final SourceModel.Expr showCleanable(SourceModel.Expr cleanable) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showCleanable), cleanable});
		}

		/**
		 * Name binding for function: showCleanable.
		 * @see #showCleanable(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showCleanable = 
			QualifiedName.make(CAL_System_internal.MODULE_NAME, "showCleanable");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = 1302459931;

}
