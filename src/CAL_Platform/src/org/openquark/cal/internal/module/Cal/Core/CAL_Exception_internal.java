/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_Exception_internal.java)
 * was generated from CAL module: Cal.Core.Exception.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Core.Exception module from Java code.
 *  
 * Creation date: Fri Mar 16 13:11:57 PST 2007
 * --!>
 *  
 */

package org.openquark.cal.internal.module.Cal.Core;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * Provides core support for handling exceptions in CAL.
 * <p>
 * Exceptions can arise in CAL in a number of ways:
 * <ol>
 *  <li>
 *   a Java exception is thrown by a call to a foreign function or a primitive function
 *  </li>
 *  <li>
 *   a call to the <code>Cal.Core.Prelude.error</code> function
 *  </li>
 *  <li>
 *   a pattern matching failure in a case expression or data constructor field selection expression
 *   e.g. <code>(case Just 10.0 of Nothing -&gt; "abc";)</code> and <code>(Left 10.0).Right.value)</code>.
 *  </li>
 *  <li>
 *   a call to the <code>Cal.Core.Exception.throw</code> function
 *  </li>
 * </ol>
 * <p>
 * Exceptions in categories 1-3 are called Java exceptions because they are not associated with a specific
 * CAL type. They can be caught with handlers for type <code>Cal.Core.Exception.JThrowable</code>. They can also be caught with any
 * CAL foreign type that is an instance of <code>Cal.Core.Exception.Exception</code> such that the foreign implementation
 * type is a Java subclass of java.lang.Throwable, and such that the exception is assignment compatible with the
 * implementation type. For example, if a foreign function throws a java.lang.NullPointerException, then this can
 * be caught by a handler for the CAL types JThowable, JRuntimeException, JNullPointerException, but not a handler 
 * for JIllegalStateException (assuming the natural implied data declarations for these CAL types).
 * <p>
 * Exceptions in category 4 are called CAL exceptions because they are associated with a specific CAL type, namely
 * the type at which the exception was thrown using the <code>Cal.Core.Exception.throw</code> function. They must be caught at that precise
 * type.
 * 
 * @author Bo Ilic
 */
public final class CAL_Exception_internal {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Core.Exception");

	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Core.Exception module.
	 */
	public static final class TypeConstructors {
		/**
		 * Type used as a holder for CAL values that are members of the Exception type class. Since the foreign class is a subtype of
		 * java.lang.Exception it can be used with the exception handling primitives primThrow and primCatch.
		 */
		public static final QualifiedName JCalException = 
			QualifiedName.make(
				CAL_Exception_internal.MODULE_NAME, 
				"JCalException");

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Core.Exception module.
	 */
	public static final class Functions {
		/**
		 * Helper binding method for function: calException_getCALValue. 
		 * @param jCalException
		 * @return the SourceModule.expr representing an application of calException_getCALValue
		 */
		public static final SourceModel.Expr calException_getCALValue(SourceModel.Expr jCalException) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.calException_getCALValue), jCalException});
		}

		/**
		 * Name binding for function: calException_getCALValue.
		 * @see #calException_getCALValue(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName calException_getCALValue = 
			QualifiedName.make(
				CAL_Exception_internal.MODULE_NAME, 
				"calException_getCALValue");

		/**
		 * Helper binding method for function: calException_make. 
		 * @param calValue
		 * @return the SourceModule.expr representing an application of calException_make
		 */
		public static final SourceModel.Expr calException_make(SourceModel.Expr calValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.calException_make), calValue});
		}

		/**
		 * Name binding for function: calException_make.
		 * @see #calException_make(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName calException_make = 
			QualifiedName.make(
				CAL_Exception_internal.MODULE_NAME, 
				"calException_make");

		/**
		 * Helper binding method for function: class_isAssignableFrom. 
		 * @param jClass
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of class_isAssignableFrom
		 */
		public static final SourceModel.Expr class_isAssignableFrom(SourceModel.Expr jClass, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.class_isAssignableFrom), jClass, arg_2});
		}

		/**
		 * Name binding for function: class_isAssignableFrom.
		 * @see #class_isAssignableFrom(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName class_isAssignableFrom = 
			QualifiedName.make(
				CAL_Exception_internal.MODULE_NAME, 
				"class_isAssignableFrom");

		/**
		 * Helper binding method for function: fromJThrowable. 
		 * @param throwable
		 * @return the SourceModule.expr representing an application of fromJThrowable
		 */
		public static final SourceModel.Expr fromJThrowable(SourceModel.Expr throwable) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromJThrowable), throwable});
		}

		/**
		 * Name binding for function: fromJThrowable.
		 * @see #fromJThrowable(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fromJThrowable = 
			QualifiedName.make(
				CAL_Exception_internal.MODULE_NAME, 
				"fromJThrowable");

		/**
		 * Helper binding method for function: isCalException. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of isCalException
		 */
		public static final SourceModel.Expr isCalException(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isCalException), arg_1});
		}

		/**
		 * Name binding for function: isCalException.
		 * @see #isCalException(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isCalException = 
			QualifiedName.make(
				CAL_Exception_internal.MODULE_NAME, 
				"isCalException");

		/**
		 * Primitive function for catching and handling exceptions.
		 * <p>
		 * It take an argument <code>expr</code> and evaluates it to weak-head normal form. If an exception is raised, then the 
		 * handler function is applied to the raised exception, and the resulting value is returned. Otherwise, the 
		 * <code>expr</code> argument is simply returned.
		 * 
		 * @param expr (CAL type: <code>a</code>)
		 *          the expression to evaluate to weak-head normal form
		 * @param handler (CAL type: <code>Cal.Core.Exception.JThrowable -> a</code>)
		 *          the exception handler to call if an exception is raised
		 * @return (CAL type: <code>a</code>) 
		 *          <code>expr</code>, if no exception is raised while evaluating <code>expr</code> to weak-head normal form. Otherwise, the result
		 * of applying the <code>handler</code> function on the exception that was raised.
		 */
		public static final SourceModel.Expr primCatch(SourceModel.Expr expr, SourceModel.Expr handler) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.primCatch), expr, handler});
		}

		/**
		 * Name binding for function: primCatch.
		 * @see #primCatch(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName primCatch = 
			QualifiedName.make(CAL_Exception_internal.MODULE_NAME, "primCatch");

		/**
		 * Primitive for raising an exception. Because the return type is polymorphic, an exception can be raised in any expression
		 * in CAL.
		 * @param exception (CAL type: <code>Cal.Core.Exception.JThrowable</code>)
		 *          the exception to raise
		 * @return (CAL type: <code>a</code>) 
		 *          nothing is actually returned since the exception is raised
		 */
		public static final SourceModel.Expr primThrow(SourceModel.Expr exception) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.primThrow), exception});
		}

		/**
		 * Name binding for function: primThrow.
		 * @see #primThrow(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName primThrow = 
			QualifiedName.make(CAL_Exception_internal.MODULE_NAME, "primThrow");

		/**
		 * Helper binding method for function: throwable_getClass. 
		 * @param jThrowable
		 * @return the SourceModule.expr representing an application of throwable_getClass
		 */
		public static final SourceModel.Expr throwable_getClass(SourceModel.Expr jThrowable) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.throwable_getClass), jThrowable});
		}

		/**
		 * Name binding for function: throwable_getClass.
		 * @see #throwable_getClass(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName throwable_getClass = 
			QualifiedName.make(
				CAL_Exception_internal.MODULE_NAME, 
				"throwable_getClass");

		/**
		 * Helper binding method for function: toJThrowable. 
		 * @param exception
		 * @return the SourceModule.expr representing an application of toJThrowable
		 */
		public static final SourceModel.Expr toJThrowable(SourceModel.Expr exception) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toJThrowable), exception});
		}

		/**
		 * Name binding for function: toJThrowable.
		 * @see #toJThrowable(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName toJThrowable = 
			QualifiedName.make(
				CAL_Exception_internal.MODULE_NAME, 
				"toJThrowable");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = 552541249;

}
