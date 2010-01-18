/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_Console.java)
 * was generated from CAL module: Cal.IO.Console.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.IO.Console module from Java code.
 *  
 * Creation date: Wed Jul 18 16:25:38 PDT 2007
 * --!>
 *  
 */

package org.openquark.cal.module.Cal.IO;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * The Console module defines basic functions for working with 
 * Console input and output.
 * @author Magnus Byne
 */
public final class CAL_Console {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.IO.Console");

	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.IO.Console module.
	 */
	public static final class TypeConstructors {
		/** Name binding for TypeConsApp: JBufferedReader. */
		public static final QualifiedName JBufferedReader = 
			QualifiedName.make(CAL_Console.MODULE_NAME, "JBufferedReader");

		/** Name binding for TypeConsApp: JInputStream. */
		public static final QualifiedName JInputStream = 
			QualifiedName.make(CAL_Console.MODULE_NAME, "JInputStream");

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.IO.Console module.
	 */
	public static final class Functions {
		/**
		 * Returns <code>Cal.Core.Prelude.True</code> iff a string is null.
		 * @param string (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          to check.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the string is null, otherwise <code>Cal.Core.Prelude.False</code>.
		 */
		public static final SourceModel.Expr isNullString(SourceModel.Expr string) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isNullString), string});
		}

		/**
		 * @see #isNullString(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param string
		 * @return the SourceModel.Expr representing an application of isNullString
		 */
		public static final SourceModel.Expr isNullString(java.lang.String string) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isNullString), SourceModel.Expr.makeStringValue(string)});
		}

		/**
		 * Name binding for function: isNullString.
		 * @see #isNullString(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isNullString = 
			QualifiedName.make(CAL_Console.MODULE_NAME, "isNullString");

		/**
		 * Creates a buffered reader from an input stream.
		 * @param input (CAL type: <code>Cal.IO.Console.JInputStream</code>)
		 *          the input stream.
		 * @return (CAL type: <code>Cal.IO.Console.JBufferedReader</code>) 
		 *          the buffered reader.
		 */
		public static final SourceModel.Expr makeBufferedReader(SourceModel.Expr input) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeBufferedReader), input});
		}

		/**
		 * Name binding for function: makeBufferedReader.
		 * @see #makeBufferedReader(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeBufferedReader = 
			QualifiedName.make(CAL_Console.MODULE_NAME, "makeBufferedReader");

		/**
		 * Prints a string to <code>Cal.IO.Console.stdout</code>.
		 * @param string (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the string to print.
		 * @return (CAL type: <code>()</code>) 
		 */
		public static final SourceModel.Expr print(SourceModel.Expr string) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.print), string});
		}

		/**
		 * @see #print(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param string
		 * @return the SourceModel.Expr representing an application of print
		 */
		public static final SourceModel.Expr print(java.lang.String string) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.print), SourceModel.Expr.makeStringValue(string)});
		}

		/**
		 * Name binding for function: print.
		 * @see #print(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName print = 
			QualifiedName.make(CAL_Console.MODULE_NAME, "print");

		/**
		 * Prints a string to <code>Cal.IO.Console.stdout</code>, and terminates the line.
		 * @param string (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the string to print.
		 * @return (CAL type: <code>()</code>) 
		 */
		public static final SourceModel.Expr printLine(SourceModel.Expr string) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.printLine), string});
		}

		/**
		 * @see #printLine(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param string
		 * @return the SourceModel.Expr representing an application of printLine
		 */
		public static final SourceModel.Expr printLine(java.lang.String string) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.printLine), SourceModel.Expr.makeStringValue(string)});
		}

		/**
		 * Name binding for function: printLine.
		 * @see #printLine(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName printLine = 
			QualifiedName.make(CAL_Console.MODULE_NAME, "printLine");

		/**
		 * Prints a list of strings to <code>Cal.IO.Console.stdout</code>, each string is printed
		 * on a sperate line.
		 * @param lines (CAL type: <code>[Cal.Core.Prelude.String]</code>)
		 *          the list of strings to print.
		 * @return (CAL type: <code>()</code>) 
		 */
		public static final SourceModel.Expr printLines(SourceModel.Expr lines) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.printLines), lines});
		}

		/**
		 * Name binding for function: printLines.
		 * @see #printLines(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName printLines = 
			QualifiedName.make(CAL_Console.MODULE_NAME, "printLines");

		/**
		 * Process lines from <code>Cal.IO.Console.stdin</code>. This can be used to write basic interactive console
		 * programs.
		 * <p>
		 * e.g.
		 * 
		 * <pre> processLines 
		 *      (0::Int) 
		 *      (\i -&gt; (show i) ++ "&gt; ") 
		 *      (\i s -&gt; s == "exit") 
		 *      (\i s -&gt; Cal.IO.Console.printLine ("You entered: " ++ s) `seq` i + 1);</pre>
		 * 
		 * 
		 * @param state (CAL type: <code>a</code>)
		 *          the state that is used and updated during processing.
		 * @param prompt (CAL type: <code>a -> Cal.Core.Prelude.String</code>)
		 *          the function that is used to prompt for each line of input.
		 * @param end (CAL type: <code>a -> Cal.Core.Prelude.String -> Cal.Core.Prelude.Boolean</code>)
		 *          the function that is used to determine when this function will finish.
		 * @param processLine (CAL type: <code>a -> Cal.Core.Prelude.String -> a</code>)
		 *          the function that is called to process each line that is entered.
		 * @return (CAL type: <code>a</code>) 
		 *          the state at the end of the processing.
		 */
		public static final SourceModel.Expr processLines(SourceModel.Expr state, SourceModel.Expr prompt, SourceModel.Expr end, SourceModel.Expr processLine) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.processLines), state, prompt, end, processLine});
		}

		/**
		 * Name binding for function: processLines.
		 * @see #processLines(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName processLines = 
			QualifiedName.make(CAL_Console.MODULE_NAME, "processLines");

		/**
		 * Reads a line from a buffered reader.
		 * @param buffered (CAL type: <code>Cal.IO.Console.JBufferedReader</code>)
		 *          reader.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the string.
		 */
		public static final SourceModel.Expr readLine(SourceModel.Expr buffered) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.readLine), buffered});
		}

		/**
		 * Name binding for function: readLine.
		 * @see #readLine(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName readLine = 
			QualifiedName.make(CAL_Console.MODULE_NAME, "readLine");

		/**
		 * Reads a list of strings from a buffered reader.
		 * @param reader (CAL type: <code>Cal.IO.Console.JBufferedReader</code>)
		 *          the reader to read lines from.
		 * @return (CAL type: <code>[Cal.Core.Prelude.String]</code>) 
		 *          the list of strings from the buffered reader.
		 */
		public static final SourceModel.Expr readLines(SourceModel.Expr reader) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.readLines), reader});
		}

		/**
		 * Name binding for function: readLines.
		 * @see #readLines(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName readLines = 
			QualifiedName.make(CAL_Console.MODULE_NAME, "readLines");

		/**
		 * The system input stream.
		 * @return (CAL type: <code>Cal.IO.Console.JInputStream</code>) 
		 */
		public static final SourceModel.Expr stdin() {
			return SourceModel.Expr.Var.make(Functions.stdin);
		}

		/**
		 * Name binding for function: stdin.
		 * @see #stdin()
		 */
		public static final QualifiedName stdin = 
			QualifiedName.make(CAL_Console.MODULE_NAME, "stdin");

		/**
		 * The system output stream.
		 * @return (CAL type: <code>Cal.IO.Console.JPrintStream</code>) 
		 */
		public static final SourceModel.Expr stdout() {
			return SourceModel.Expr.Var.make(Functions.stdout);
		}

		/**
		 * Name binding for function: stdout.
		 * @see #stdout()
		 */
		public static final QualifiedName stdout = 
			QualifiedName.make(CAL_Console.MODULE_NAME, "stdout");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = -1265071478;

}
