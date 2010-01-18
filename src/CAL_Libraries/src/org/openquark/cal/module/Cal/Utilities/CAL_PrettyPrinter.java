/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_PrettyPrinter.java)
 * was generated from CAL module: Cal.Utilities.PrettyPrinter.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Utilities.PrettyPrinter module from Java code.
 *  
 * Creation date: Tue Aug 28 15:58:40 PDT 2007
 * --!>
 *  
 */

package org.openquark.cal.module.Cal.Utilities;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * Functions for pretty-printing text.
 * This is based on the paper 'A prettier printer' by Philip Wadler.
 * (<a href='http://homepages.inf.ed.ac.uk/wadler/papers/prettier/prettier.pdf'>http://homepages.inf.ed.ac.uk/wadler/papers/prettier/prettier.pdf</a>)
 * @author Richard Webster
 */
public final class CAL_PrettyPrinter {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Utilities.PrettyPrinter");

	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Utilities.PrettyPrinter module.
	 */
	public static final class TypeConstructors {
		/** Name binding for TypeConsApp: Document. */
		public static final QualifiedName Document = 
			QualifiedName.make(CAL_PrettyPrinter.MODULE_NAME, "Document");

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Utilities.PrettyPrinter module.
	 */
	public static final class Functions {
		/**
		 * Helper binding method for function: bracket. 
		 * @param l
		 * @param x
		 * @param r
		 * @return the SourceModule.expr representing an application of bracket
		 */
		public static final SourceModel.Expr bracket(SourceModel.Expr l, SourceModel.Expr x, SourceModel.Expr r) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.bracket), l, x, r});
		}

		/**
		 * @see #bracket(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param l
		 * @param x
		 * @param r
		 * @return the SourceModel.Expr representing an application of bracket
		 */
		public static final SourceModel.Expr bracket(java.lang.String l, SourceModel.Expr x, java.lang.String r) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.bracket), SourceModel.Expr.makeStringValue(l), x, SourceModel.Expr.makeStringValue(r)});
		}

		/**
		 * Name binding for function: bracket.
		 * @see #bracket(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName bracket = 
			QualifiedName.make(CAL_PrettyPrinter.MODULE_NAME, "bracket");

		/**
		 * Helper binding method for function: fill. 
		 * @param docs
		 * @return the SourceModule.expr representing an application of fill
		 */
		public static final SourceModel.Expr fill(SourceModel.Expr docs) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fill), docs});
		}

		/**
		 * Name binding for function: fill.
		 * @see #fill(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fill = 
			QualifiedName.make(CAL_PrettyPrinter.MODULE_NAME, "fill");

		/**
		 * Similar to fill, except that the specified separator text will be inserted between the items.
		 * The <code>trailingSeparator</code> flag controls whether the separator appears after each item (except the last) 
		 * or before each item (except the first).
		 * @param trailingSeparator (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 * @param separator (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @param docs (CAL type: <code>[Cal.Utilities.PrettyPrinter.Document]</code>)
		 * @return (CAL type: <code>Cal.Utilities.PrettyPrinter.Document</code>) 
		 */
		public static final SourceModel.Expr fillSeparatedList(SourceModel.Expr trailingSeparator, SourceModel.Expr separator, SourceModel.Expr docs) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fillSeparatedList), trailingSeparator, separator, docs});
		}

		/**
		 * @see #fillSeparatedList(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param trailingSeparator
		 * @param separator
		 * @param docs
		 * @return the SourceModel.Expr representing an application of fillSeparatedList
		 */
		public static final SourceModel.Expr fillSeparatedList(boolean trailingSeparator, java.lang.String separator, SourceModel.Expr docs) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fillSeparatedList), SourceModel.Expr.makeBooleanValue(trailingSeparator), SourceModel.Expr.makeStringValue(separator), docs});
		}

		/**
		 * Name binding for function: fillSeparatedList.
		 * @see #fillSeparatedList(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fillSeparatedList = 
			QualifiedName.make(
				CAL_PrettyPrinter.MODULE_NAME, 
				"fillSeparatedList");

		/**
		 * Helper binding method for function: fillwords. 
		 * @param str
		 * @return the SourceModule.expr representing an application of fillwords
		 */
		public static final SourceModel.Expr fillwords(SourceModel.Expr str) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fillwords), str});
		}

		/**
		 * @see #fillwords(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param str
		 * @return the SourceModel.Expr representing an application of fillwords
		 */
		public static final SourceModel.Expr fillwords(java.lang.String str) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fillwords), SourceModel.Expr.makeStringValue(str)});
		}

		/**
		 * Name binding for function: fillwords.
		 * @see #fillwords(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fillwords = 
			QualifiedName.make(CAL_PrettyPrinter.MODULE_NAME, "fillwords");

		/**
		 * Helper binding method for function: folddoc. 
		 * @param f
		 * @param docs
		 * @return the SourceModule.expr representing an application of folddoc
		 */
		public static final SourceModel.Expr folddoc(SourceModel.Expr f, SourceModel.Expr docs) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.folddoc), f, docs});
		}

		/**
		 * Name binding for function: folddoc.
		 * @see #folddoc(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName folddoc = 
			QualifiedName.make(CAL_PrettyPrinter.MODULE_NAME, "folddoc");

		/**
		 * Helper binding method for function: group. 
		 * @param x
		 * @return the SourceModule.expr representing an application of group
		 */
		public static final SourceModel.Expr group(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.group), x});
		}

		/**
		 * Name binding for function: group.
		 * @see #group(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName group = 
			QualifiedName.make(CAL_PrettyPrinter.MODULE_NAME, "group");

		/**
		 * Helper binding method for function: line. 
		 * @return the SourceModule.expr representing an application of line
		 */
		public static final SourceModel.Expr line() {
			return SourceModel.Expr.Var.make(Functions.line);
		}

		/**
		 * Name binding for function: line.
		 * @see #line()
		 */
		public static final QualifiedName line = 
			QualifiedName.make(CAL_PrettyPrinter.MODULE_NAME, "line");

		/**
		 * Helper binding method for function: lineSep. 
		 * @param x
		 * @param y
		 * @return the SourceModule.expr representing an application of lineSep
		 */
		public static final SourceModel.Expr lineSep(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lineSep), x, y});
		}

		/**
		 * Name binding for function: lineSep.
		 * @see #lineSep(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName lineSep = 
			QualifiedName.make(CAL_PrettyPrinter.MODULE_NAME, "lineSep");

		/**
		 * Breaks the text into multiple lines (based on the <code>'\n'</code> char) and creates a document item for each line.
		 * @param s (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Utilities.PrettyPrinter.Document</code>) 
		 */
		public static final SourceModel.Expr multilineText(SourceModel.Expr s) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.multilineText), s});
		}

		/**
		 * @see #multilineText(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param s
		 * @return the SourceModel.Expr representing an application of multilineText
		 */
		public static final SourceModel.Expr multilineText(java.lang.String s) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.multilineText), SourceModel.Expr.makeStringValue(s)});
		}

		/**
		 * Name binding for function: multilineText.
		 * @see #multilineText(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName multilineText = 
			QualifiedName.make(CAL_PrettyPrinter.MODULE_NAME, "multilineText");

		/**
		 * Helper binding method for function: nest. 
		 * @param i
		 * @param x
		 * @return the SourceModule.expr representing an application of nest
		 */
		public static final SourceModel.Expr nest(SourceModel.Expr i, SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.nest), i, x});
		}

		/**
		 * @see #nest(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param i
		 * @param x
		 * @return the SourceModel.Expr representing an application of nest
		 */
		public static final SourceModel.Expr nest(int i, SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.nest), SourceModel.Expr.makeIntValue(i), x});
		}

		/**
		 * Name binding for function: nest.
		 * @see #nest(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName nest = 
			QualifiedName.make(CAL_PrettyPrinter.MODULE_NAME, "nest");

		/**
		 * Helper binding method for function: pretty. 
		 * @param maxWidth
		 * @param doc
		 * @return the SourceModule.expr representing an application of pretty
		 */
		public static final SourceModel.Expr pretty(SourceModel.Expr maxWidth, SourceModel.Expr doc) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.pretty), maxWidth, doc});
		}

		/**
		 * @see #pretty(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param maxWidth
		 * @param doc
		 * @return the SourceModel.Expr representing an application of pretty
		 */
		public static final SourceModel.Expr pretty(int maxWidth, SourceModel.Expr doc) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.pretty), SourceModel.Expr.makeIntValue(maxWidth), doc});
		}

		/**
		 * Name binding for function: pretty.
		 * @see #pretty(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName pretty = 
			QualifiedName.make(CAL_PrettyPrinter.MODULE_NAME, "pretty");

		/**
		 * Helper binding method for function: singleSpace. 
		 * @return the SourceModule.expr representing an application of singleSpace
		 */
		public static final SourceModel.Expr singleSpace() {
			return SourceModel.Expr.Var.make(Functions.singleSpace);
		}

		/**
		 * Name binding for function: singleSpace.
		 * @see #singleSpace()
		 */
		public static final QualifiedName singleSpace = 
			QualifiedName.make(CAL_PrettyPrinter.MODULE_NAME, "singleSpace");

		/**
		 * Helper binding method for function: spaceOrLineSep. 
		 * @param x
		 * @param y
		 * @return the SourceModule.expr representing an application of spaceOrLineSep
		 */
		public static final SourceModel.Expr spaceOrLineSep(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.spaceOrLineSep), x, y});
		}

		/**
		 * Name binding for function: spaceOrLineSep.
		 * @see #spaceOrLineSep(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName spaceOrLineSep = 
			QualifiedName.make(CAL_PrettyPrinter.MODULE_NAME, "spaceOrLineSep");

		/**
		 * Helper binding method for function: spaceSep. 
		 * @param x
		 * @param y
		 * @return the SourceModule.expr representing an application of spaceSep
		 */
		public static final SourceModel.Expr spaceSep(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.spaceSep), x, y});
		}

		/**
		 * Name binding for function: spaceSep.
		 * @see #spaceSep(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName spaceSep = 
			QualifiedName.make(CAL_PrettyPrinter.MODULE_NAME, "spaceSep");

		/**
		 * Helper binding method for function: spread. 
		 * @param docs
		 * @return the SourceModule.expr representing an application of spread
		 */
		public static final SourceModel.Expr spread(SourceModel.Expr docs) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.spread), docs});
		}

		/**
		 * Name binding for function: spread.
		 * @see #spread(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName spread = 
			QualifiedName.make(CAL_PrettyPrinter.MODULE_NAME, "spread");

		/**
		 * Helper binding method for function: stack. 
		 * @param docs
		 * @return the SourceModule.expr representing an application of stack
		 */
		public static final SourceModel.Expr stack(SourceModel.Expr docs) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.stack), docs});
		}

		/**
		 * Name binding for function: stack.
		 * @see #stack(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName stack = 
			QualifiedName.make(CAL_PrettyPrinter.MODULE_NAME, "stack");

		/**
		 * Helper binding method for function: text. 
		 * @param s
		 * @return the SourceModule.expr representing an application of text
		 */
		public static final SourceModel.Expr text(SourceModel.Expr s) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.text), s});
		}

		/**
		 * @see #text(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param s
		 * @return the SourceModel.Expr representing an application of text
		 */
		public static final SourceModel.Expr text(java.lang.String s) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.text), SourceModel.Expr.makeStringValue(s)});
		}

		/**
		 * Name binding for function: text.
		 * @see #text(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName text = 
			QualifiedName.make(CAL_PrettyPrinter.MODULE_NAME, "text");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = 608479550;

}
