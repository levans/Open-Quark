/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_Exception.java)
 * was generated from CAL module: Cal.Core.Exception.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Core.Exception module from Java code.
 *  
 * Creation date: Wed Apr 11 16:34:15 PDT 2007
 * --!>
 *  
 */

package org.openquark.cal.module.Cal.Core;

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
public final class CAL_Exception {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Core.Exception");

	/**
	 * This inner class (TypeClasses) contains constants
	 * and methods related to binding to CAL TypeClasses in the Cal.Core.Exception module.
	 */
	public static final class TypeClasses {
		/**
		 * The <code>Cal.Core.Exception.Exception</code> type class defines the types whose values can be thrown or caught
		 * using the <code>Cal.Core.Exception.throw</code> and <code>Cal.Core.Exception.catch</code> functions.
		 * <p>
		 * The class has no class methods so declaring instances is easy. It is used to indicate programmer 
		 * intent that a type will be used to represent exception values.
		 */
		public static final QualifiedName Exception = 
			QualifiedName.make(CAL_Exception.MODULE_NAME, "Exception");

	}
	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Core.Exception module.
	 */
	public static final class TypeConstructors {
		/**
		 * Exceptions from foreign and primitive function calls can be caught as values of the JThrowable type.
		 */
		public static final QualifiedName JThrowable = 
			QualifiedName.make(CAL_Exception.MODULE_NAME, "JThrowable");

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Core.Exception module.
	 */
	public static final class Functions {
		/**
		 * <code>Cal.Core.Exception.bracket</code> is used to acquire a resource, do some work with it, and then release the resource even
		 * if an exception is raised while doing the work with the resource.
		 * <p>
		 * If an exception is raised, then <code>bracket</code> will re-raise the exception (after performing the release).
		 * 
		 * @param initializer (CAL type: <code>a</code>)
		 *          expression to first evaluate to weak-head normal form e.g. to acquire a resource
		 * @param exprFunction (CAL type: <code>a -> c</code>)
		 *          (exprFunction initializer) is evaluated to weak-head normal form e.g. this is using the resource.
		 * @param finalizingFunction (CAL type: <code>a -> b</code>)
		 *          (exprFunction initializer) is evaluated last e.g. to release the resource required in initializer.
		 * Will be evaluated even if evalating (exprFunction initializer) raises an exception.
		 * @return (CAL type: <code>c</code>) 
		 *          the result of applying exprFunction to initializer.
		 */
		public static final SourceModel.Expr bracket(SourceModel.Expr initializer, SourceModel.Expr exprFunction, SourceModel.Expr finalizingFunction) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.bracket), initializer, exprFunction, finalizingFunction});
		}

		/**
		 * Name binding for function: bracket.
		 * @see #bracket(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName bracket = 
			QualifiedName.make(CAL_Exception.MODULE_NAME, "bracket");

		/**
		 * Core function for catching and handling exceptions.
		 * <p>
		 * It take an argument <code>expr</code> and evaluates it to weak-head normal form. 
		 * If no exception is raised, then the <code>expr</code> argument is simply returned.
		 * If an exception is raised, and the handler function is of the appropriate type to handle the exception,
		 * then the handler function is applied to the raised exception, and the resulting value is returned.
		 * Otherwise, the exception is simply not caught and propagated ownward.
		 * 
		 * @param expr (CAL type: <code>a</code>)
		 *          the expression to evaluate to weak-head normal form
		 * @param handler (CAL type: <code>Cal.Core.Exception.Exception e => e -> a</code>)
		 *          the exception handler to call if an exception is raised of the appropriate type
		 * @return (CAL type: <code>a</code>) 
		 *          <code>expr</code>, if no exception is raised while evaluating <code>expr</code> to weak-head normal form. Otherwise, the result
		 * of applying the <code>handler</code> function on the exception that was raised if the exception is of the appropriate type.
		 * Otherwise the expression is not caught but propagated onward.
		 */
		public static final SourceModel.Expr catch_(SourceModel.Expr expr, SourceModel.Expr handler) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.catch_), expr, handler});
		}

		/**
		 * Name binding for function: catch.
		 * @see #catch_(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName catch_ = 
			QualifiedName.make(CAL_Exception.MODULE_NAME, "catch");

		/**
		 * <code>Cal.Core.Exception.finally</code> evaluates the argument <code>expr</code> to weak-head normal form.
		 * If an exception is raised while doing this, then <code>Cal.Core.Exception.finally</code> evaluates the argument <code>finalizer</code>
		 * to weak-head normal form, before re-raising the exception.
		 * Otherwise, <code>Cal.Core.Exception.finally</code> will still evaluate <code>finalizer</code> to weak-head normal form before returning <code>expr</code>.
		 * @param expr (CAL type: <code>a</code>)
		 *          expression to evaluate to weak-head normal form
		 * @param finalizer (CAL type: <code>b</code>)
		 *          expression to evaluate to weak-head normal form, even if an exception was raised while evaluating expr.
		 * @return (CAL type: <code>a</code>) 
		 *          expr
		 */
		public static final SourceModel.Expr finally_(SourceModel.Expr expr, SourceModel.Expr finalizer) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.finally_), expr, finalizer});
		}

		/**
		 * Name binding for function: finally.
		 * @see #finally_(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName finally_ = 
			QualifiedName.make(CAL_Exception.MODULE_NAME, "finally");

		/**
		 * 
		 * @param throwable (CAL type: <code>Cal.Core.Exception.JThrowable</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if throwable was raised through a call to the <code>Cal.Core.Prelude.error</code> function.
		 */
		public static final SourceModel.Expr isError(SourceModel.Expr throwable) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isError), throwable});
		}

		/**
		 * Name binding for function: isError.
		 * @see #isError(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isError = 
			QualifiedName.make(CAL_Exception.MODULE_NAME, "isError");

		/**
		 * 
		 * @param exception (CAL type: <code>Cal.Core.Exception.JThrowable</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          true if exception is actually of the java.lang.ArithmeticException subtype.
		 */
		public static final SourceModel.Expr isJavaArithmeticException(SourceModel.Expr exception) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isJavaArithmeticException), exception});
		}

		/**
		 * Name binding for function: isJavaArithmeticException.
		 * @see #isJavaArithmeticException(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isJavaArithmeticException = 
			QualifiedName.make(
				CAL_Exception.MODULE_NAME, 
				"isJavaArithmeticException");

		/**
		 * 
		 * @param exception (CAL type: <code>Cal.Core.Exception.JThrowable</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          true if exception is actually of the java.lang.Error subtype.
		 */
		public static final SourceModel.Expr isJavaError(SourceModel.Expr exception) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isJavaError), exception});
		}

		/**
		 * Name binding for function: isJavaError.
		 * @see #isJavaError(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isJavaError = 
			QualifiedName.make(CAL_Exception.MODULE_NAME, "isJavaError");

		/**
		 * 
		 * @param exception (CAL type: <code>Cal.Core.Exception.JThrowable</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          true if exception is actually of the java.lang.Exception subtype.
		 */
		public static final SourceModel.Expr isJavaException(SourceModel.Expr exception) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isJavaException), exception});
		}

		/**
		 * Name binding for function: isJavaException.
		 * @see #isJavaException(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isJavaException = 
			QualifiedName.make(CAL_Exception.MODULE_NAME, "isJavaException");

		/**
		 * 
		 * @param exception (CAL type: <code>Cal.Core.Exception.JThrowable</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          true if exception is actually of the java.lang.IllegalArgumentException subtype.
		 */
		public static final SourceModel.Expr isJavaIllegalArgumentException(SourceModel.Expr exception) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isJavaIllegalArgumentException), exception});
		}

		/**
		 * Name binding for function: isJavaIllegalArgumentException.
		 * @see #isJavaIllegalArgumentException(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isJavaIllegalArgumentException = 
			QualifiedName.make(
				CAL_Exception.MODULE_NAME, 
				"isJavaIllegalArgumentException");

		/**
		 * 
		 * @param exception (CAL type: <code>Cal.Core.Exception.JThrowable</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          true if exception is actually of the java.lang.IllegalStateException subtype.
		 */
		public static final SourceModel.Expr isJavaIllegalStateException(SourceModel.Expr exception) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isJavaIllegalStateException), exception});
		}

		/**
		 * Name binding for function: isJavaIllegalStateException.
		 * @see #isJavaIllegalStateException(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isJavaIllegalStateException = 
			QualifiedName.make(
				CAL_Exception.MODULE_NAME, 
				"isJavaIllegalStateException");

		/**
		 * 
		 * @param exception (CAL type: <code>Cal.Core.Exception.JThrowable</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          true if exception is actually of the java.lang.NullPointerException subtype.
		 */
		public static final SourceModel.Expr isJavaNullPointerException(SourceModel.Expr exception) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isJavaNullPointerException), exception});
		}

		/**
		 * Name binding for function: isJavaNullPointerException.
		 * @see #isJavaNullPointerException(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isJavaNullPointerException = 
			QualifiedName.make(
				CAL_Exception.MODULE_NAME, 
				"isJavaNullPointerException");

		/**
		 * 
		 * @param exception (CAL type: <code>Cal.Core.Exception.JThrowable</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          true if exception is actually of the java.lang.RuntimeException subtype.
		 */
		public static final SourceModel.Expr isJavaRuntimeException(SourceModel.Expr exception) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isJavaRuntimeException), exception});
		}

		/**
		 * Name binding for function: isJavaRuntimeException.
		 * @see #isJavaRuntimeException(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isJavaRuntimeException = 
			QualifiedName.make(
				CAL_Exception.MODULE_NAME, 
				"isJavaRuntimeException");

		/**
		 * Pattern matching failures arise via an unhandled case alternative e.g.
		 * <code>(case Just 'a' of Nothing -&gt; True;)</code> or a data constructor field selection failure e.g.
		 * <code>(Left 'a' :: Either Char Char).Right.value)</code>.
		 * @param throwable (CAL type: <code>Cal.Core.Exception.JThrowable</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if throwable was raised because of a pattern matching failure.
		 */
		public static final SourceModel.Expr isPatternMatchingFailure(SourceModel.Expr throwable) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isPatternMatchingFailure), throwable});
		}

		/**
		 * Name binding for function: isPatternMatchingFailure.
		 * @see #isPatternMatchingFailure(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isPatternMatchingFailure = 
			QualifiedName.make(
				CAL_Exception.MODULE_NAME, 
				"isPatternMatchingFailure");

		/**
		 * Raise a CAL user-defined exception i.e. one that has a specific CAL type that is a member of the <code>Cal.Core.Exception.Exception</code>
		 * type class. Such exceptions must be caught at their specific CAL type.
		 * <p>
		 * Because the return type is polymorphic, an exception can be raised in any expression
		 * in CAL.
		 * 
		 * @param exception (CAL type: <code>Cal.Core.Exception.Exception e => e</code>)
		 *          the exception to raise
		 * @return (CAL type: <code>a</code>) 
		 *          nothing is actually returned since the exception is raised
		 */
		public static final SourceModel.Expr throw_(SourceModel.Expr exception) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.throw_), exception});
		}

		/**
		 * Name binding for function: throw.
		 * @see #throw_(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName throw_ = 
			QualifiedName.make(CAL_Exception.MODULE_NAME, "throw");

		/**
		 * 
		 * @param expr (CAL type: <code>a</code>)
		 *          expression to evaluate to weak-head normal form
		 * @return (CAL type: <code>Cal.Core.Exception.Exception e => Cal.Core.Prelude.Either e a</code>) 
		 *          <code>Right expr</code> if no exception was raised while evaluating <code>expr</code>, otherwise, <code>Left (the exception raised)</code>
		 */
		public static final SourceModel.Expr try_(SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.try_), expr});
		}

		/**
		 * Name binding for function: try.
		 * @see #try_(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName try_ = 
			QualifiedName.make(CAL_Exception.MODULE_NAME, "try");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = -373372265;

}
