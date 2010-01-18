/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_Parser.java)
 * was generated from CAL module: Cal.Utilities.Parser.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Utilities.Parser module from Java code.
 *  
 * Creation date: Thu Sep 20 17:28:49 PDT 2007
 * --!>
 *  
 */

package org.openquark.cal.module.Cal.Utilities;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * This module defines a parser combinator library which can be used to define parsers for both
 * context-free and context-sensitive grammars.
 * <p>
 * <strong>A short introduction</strong>
 * <p>
 * <em>Terminology</em>
 * <p>
 * In this document, we follow the Haskell/Parsec convention of referring to functions that operate on parsers to
 * produce new parsers as <em>parser combinators</em> or <em>combinators</em> for short. By definition, "combinator"
 * is simply another word for a higher-order function.
 * <p>
 * <em>Defining a simple parser</em>
 * <p>
 * Let's start with a simple parser: a parser that recognizes the single character 'X':
 * <p>
 * 
 * <pre> simple :: Cal.Utilities.Parser.GenParser Char st Char
 *  simple = Cal.Utilities.Parser.char 'X';</pre>
 * 
 * <p>
 * The type of the parser 'simple' can be read as such: it is a parser that processes tokens of type <code>Char</code> (the first
 * Char in the type signature), that can deal with any user state of type <code>st</code>, and that returns a result of type
 * <code>Char</code> (the second Char).
 * <p>
 * <em>Sequencing</em>
 * <p>
 * To define a parser that recognizes the two-token sequence ['X', 'Y'], we can use the sequencing combinator <code>Cal.Utilities.Parser.pSeq</code>,
 * as in:
 * <p>
 * 
 * <pre> xy :: GenParser Char st Char
 *  xy = char 'X' `Cal.Utilities.Parser.pSeq` char 'Y';</pre>
 * 
 * <p>
 * <em>Alternatives</em>
 * <p>
 * To define a parser that recognizes either 'X' or 'Y', we can use the combinator <code>Cal.Utilities.Parser.pOr</code>:
 * <p>
 * 
 * <pre> xOrY :: GenParser Char st Char
 *  xOrY = char 'X' `Cal.Utilities.Parser.pOr` char 'Y';</pre>
 * 
 * <p>
 * <em>Predictive parsers</em>
 * <p>
 * For performance reasons, the <code>Cal.Utilities.Parser.pOr</code> combinator is <em>predictive</em>, meaning that it will only try its second
 * parser if the first parser has not consumed any input.
 * <p>
 * What this means is that a parser such as:
 * 
 * <pre> testOr = Cal.Utilities.Parser.exactString "(a)" `Cal.Utilities.Parser.pOr` Cal.Utilities.Parser.exactString "(b)";</pre>
 * 
 * will <em>not</em> accept the input <code>"(b)"</code>, since the first parser would have consumed the <code>'('</code> character
 * before failing, and thus <code>Cal.Utilities.Parser.pOr</code> would not have tried the parser <code>Cal.Utilities.Parser.exactString "(b)"</code>.
 * <p>
 * To make the parser accept both <code>"(a)"</code> and <code>"(b)"</code>, the <code>Cal.Utilities.Parser.try</code> combinator needs to be used for the
 * first parser, as in:
 * 
 * <pre> testOr = (Cal.Utilities.Parser.try Cal.Utilities.Parser.exactString "(a)") `Cal.Utilities.Parser.pOr` Cal.Utilities.Parser.exactString "(b)";</pre>
 * 
 * <p>
 * For any parser <code>p</code>, the augmented parser <code>(Cal.Utilities.Parser.try p)</code> behaves just like <code>p</code> except
 * that it pretends that it has not consumed any input whenever <code>p</code> fails. In combination with <code>Cal.Utilities.Parser.pOr</code>
 * this allows for infinite look-ahead in the grammar. 
 * <p>
 * Finally, we have the combinator <code>Cal.Utilities.Parser.pOrT</code> which provides an optimized implementation of this common scenario.
 * With it, the previous example can be rewritten as:
 * 
 * <pre> testOr = Cal.Utilities.Parser.exactString "(a)" `Cal.Utilities.Parser.pOrT` Cal.Utilities.Parser.exactString "(b)";</pre>
 * 
 * <p>
 * <em>Capturing values returned by parsers in a sequence</em>
 * <p>
 * When parsers are sequenced using <code>Cal.Utilities.Parser.pSeq</code>, only the result of the last parser is kept and returned -
 * the results of the preceding parsers are discarded. To capture the values returned by these intermediate
 * parsers, the <code>Cal.Utilities.Parser.pBind</code> combinator can be used.
 * <p>
 * For example, here is a parser that returns a pair containing the results of the two intermediate parsers:
 * 
 * <pre> parserPair p q =
 *      p `Cal.Utilities.Parser.pBind` (\x -&gt;
 *      q `Cal.Utilities.Parser.pBind` (\y -&gt;
 *      Cal.Utilities.Parser.pReturn (x, y)));</pre>
 * 
 * <p>
 * [Note: the above is the convention for formatting a sequence of parsers joined using <code>Cal.Utilities.Parser.pBind</code>.]
 * <p>
 * Here, the example can be read in an imperative fashion:
 * <ol>
 *  <li>
 *   First, parser <code>p</code> is applied, and its result is bound to <code>x</code>.
 *  </li>
 *  <li>
 *   Then, parser <code>q</code> is applied, and its result is bound to <code>y</code>.
 *  </li>
 *  <li>
 *   Finally, <code>Cal.Utilities.Parser.pReturn</code> returns the pair <code>(x, y)</code>.
 *  </li>
 * </ol>
 * <p>
 * <strong>Converting Parsec's Haskell syntax to CAL</strong>
 * <p>
 * Here are some rules for converting between the Haskell notation to the CAL notation.
 * <ul>
 *  <li>
 *   Haskell:
 *   
 *   <pre>    testOr1 = do{ char '('
 *                 ; char 'a' &lt;|&gt; char 'b'
 *                 ; char ')'
 *                 }
 * </pre>
 *   
 *   CAL:
 *   
 *   <pre>    testOr1 =
 *         Cal.Utilities.Parser.char '(' `Cal.Utilities.Parser.pSeq`                         
 *         (Cal.Utilities.Parser.char 'a' `Cal.Utilities.Parser.pOr` Cal.Utilities.Parser.char 'b') `Cal.Utilities.Parser.pSeq`
 *         Cal.Utilities.Parser.char ')'</pre>
 *  </li>
 *  <li>
 *   Haskell:
 *   
 *   <pre>    nesting :: Parser Int
 *     nesting = do{ char '('
 *                 ; n &lt;- nesting
 *                 ; char ')'
 *                 ; m &lt;- nesting
 *                 ; return (max (n+1) m)
 *                 }
 *             &lt;|&gt; return 0        
 * </pre>
 *   
 *   CAL:
 *   
 *   <pre>    nesting = 
 *         (
 *         Cal.Utilities.Parser.char '(' `Cal.Utilities.Parser.pSeq`
 *         nesting `Cal.Utilities.Parser.pBind` (\n -&gt;    // bind the output of nesting to n (analogous to n &lt;- nesting)
 *         Cal.Utilities.Parser.char ')' `Cal.Utilities.Parser.pSeq`
 *         nesting `Cal.Utilities.Parser.pBind` (\m -&gt;    // bind the output of nesting to m
 *         Cal.Utilities.Parser.pReturn (max (n+1) m)))
 *         ) `Cal.Utilities.Parser.pOr`
 *         Cal.Utilities.Parser.pReturn 0;</pre>
 *  </li>
 *  <li>
 *   Haskell:
 *   
 *   <pre>    word = many1 letter &lt;?&gt; "word"
 * </pre>
 *   
 *   CAL:
 *   
 *   <pre>    word = Cal.Utilities.Parser.many1 Cal.Utilities.Parser.letter `Cal.Utilities.Parser.label` "word"; // label the parser with the given string for error messages</pre>
 *  </li>
 * </ul>
 * <p>
 * <strong>Basic parser combinators</strong>
 * <ul>
 *  <li>
 *   <code>Cal.Utilities.Parser.pSeq</code> = used to sequence parsers
 *  </li>
 *  <li>
 *   <code>Cal.Utilities.Parser.pBind</code> = used to sequence parsers and access the values returned by the parser
 *  </li>
 *  <li>
 *   <code>Cal.Utilities.Parser.pReturn</code> = used to indicate a return value
 *  </li>
 *  <li>
 *   <code>Cal.Utilities.Parser.pFail</code> = fail the parse with the specified error message
 *  </li>
 *  <li>
 *   <code>Cal.Utilities.Parser.pOr</code> = operator for parser choice.
 *  </li>
 *  <li>
 *   <code>Cal.Utilities.Parser.label</code> = used to provide a label for error messages
 *  </li>
 * </ul>
 * <p>
 * This module is a port of the Haskell based <em>Parsec</em>
 * framework by Daan Leijen. Documentation for Parsec can be found at: <a href='http://www.cs.uu.nl/~daan/parsec.html'>http://www.cs.uu.nl/~daan/parsec.html</a>.
 * The use of the CAL version of Parsec is similar to that described in the manual.
 * See the file <code>ThirdPartyComponents/ThirdPartyComponents.txt</code> for the Parsec license.
 */
public final class CAL_Parser {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Utilities.Parser");

	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Utilities.Parser module.
	 */
	public static final class TypeConstructors {
		/**
		 * The <code>GenParser tok st a</code> type represents a parser that handles tokens of type <code>tok</code>
		 * with a user supplied state <code>st</code> and returns a value of type <code>a</code> on success.
		 */
		public static final QualifiedName GenParser = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "GenParser");

		/**
		 * This <code>Message</code> type represents parse error messages.
		 */
		public static final QualifiedName Message = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "Message");

		/**
		 * The abstract data type <code>ParseError</code> represents parse errors. It provides the source position
		 * (<code>Cal.Utilities.Parser.SourcePos</code>) of the error and a list of error messages (<code>Cal.Utilities.Parser.Message</code>).
		 * A <code>ParseError</code> can be returned by the function <code>Cal.Utilities.Parser.parse</code>.
		 */
		public static final QualifiedName ParseError = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "ParseError");

		/**
		 * The abstract data type <code>SourcePos</code> represents source positions. It contains the name of
		 * the source, a line number and a column number.
		 * <p>
		 * <code>SourcePos</code> has derived instances of the type classes <code>Cal.Core.Prelude.Eq</code> and <code>Cal.Core.Prelude.Ord</code>.
		 */
		public static final QualifiedName SourcePos = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "SourcePos");

		/**
		 * The abstract data type <code>State</code> represents the internal state of the parser. It contains the list of input
		 * tokens that is to be parsed, the current source position, and the current user state.
		 */
		public static final QualifiedName State = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "State");

		/**
		 * A sequence of tokens, supporting head, tail, and isEmpty. May be represented
		 * internally by a list of tokens, or in the case of TokenSequence Char, by a
		 * String. To construct a <code>TokenSequence</code>, use <code>Cal.Utilities.Parser.makeTSList</code> or
		 * <code>Cal.Utilities.Parser.makeTSString</code>.
		 */
		public static final QualifiedName TokenSequence = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "TokenSequence");

	}
	/**
	 * This inner class (DataConstructors) contains constants
	 * and methods related to binding to CAL DataConstructors in the Cal.Utilities.Parser module.
	 */
	public static final class DataConstructors {
		/*
		 * DataConstructors for the Cal.Utilities.Parser.Message data type.
		 */

		/**
		 * A <code>SysUnExpect</code> message is generated internally by the <code>Cal.Utilities.Parser.satisfy</code>
		 * combinator.
		 * @param msg (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the unexpected input.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr SysUnExpect(SourceModel.Expr msg) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.SysUnExpect), msg});
		}

		/**
		 * @see #SysUnExpect(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param msg
		 * @return org.openquark.cal.compiler.SourceModel.Expr
		 */
		public static final SourceModel.Expr SysUnExpect(java.lang.String msg) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.SysUnExpect), SourceModel.Expr.makeStringValue(msg)});
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.Parser.SysUnExpect.
		 * @see #SysUnExpect(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName SysUnExpect = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "SysUnExpect");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.Parser.SysUnExpect.
		 * @see #SysUnExpect(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int SysUnExpect_ordinal = 0;

		/**
		 * An <code>UnExpect</code> message is generated by the <code>Cal.Utilities.Parser.unexpected</code> combinator.
		 * @param msg (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          a description of the unexpected item.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr UnExpect(SourceModel.Expr msg) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.UnExpect), msg});
		}

		/**
		 * @see #UnExpect(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param msg
		 * @return org.openquark.cal.compiler.SourceModel.Expr
		 */
		public static final SourceModel.Expr UnExpect(java.lang.String msg) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.UnExpect), SourceModel.Expr.makeStringValue(msg)});
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.Parser.UnExpect.
		 * @see #UnExpect(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName UnExpect = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "UnExpect");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.Parser.UnExpect.
		 * @see #UnExpect(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int UnExpect_ordinal = 1;

		/**
		 * An <code>Expect</code> message is generated by the <code>Cal.Utilities.Parser.label</code> combinator.
		 * @param msg (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          a description of the expected item.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr Expect(SourceModel.Expr msg) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.Expect), msg});
		}

		/**
		 * @see #Expect(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param msg
		 * @return org.openquark.cal.compiler.SourceModel.Expr
		 */
		public static final SourceModel.Expr Expect(java.lang.String msg) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.Expect), SourceModel.Expr.makeStringValue(msg)});
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.Parser.Expect.
		 * @see #Expect(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName Expect = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "Expect");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.Parser.Expect.
		 * @see #Expect(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int Expect_ordinal = 2;

		/**
		 * A <code>Message</code> message is generated by the <code>Cal.Utilities.Parser.pFail</code> combinator.
		 * @param msg (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          a message string.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr Message(SourceModel.Expr msg) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.Message), msg});
		}

		/**
		 * @see #Message(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param msg
		 * @return org.openquark.cal.compiler.SourceModel.Expr
		 */
		public static final SourceModel.Expr Message(java.lang.String msg) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.Message), SourceModel.Expr.makeStringValue(msg)});
		}

		/**
		 * Name binding for DataConstructor: Cal.Utilities.Parser.Message.
		 * @see #Message(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName Message = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "Message");

		/**
		 * Ordinal of DataConstructor Cal.Utilities.Parser.Message.
		 * @see #Message(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int Message_ordinal = 3;

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Utilities.Parser module.
	 */
	public static final class Functions {
		/**
		 * Adds an error message to the given <code>Cal.Utilities.Parser.ParseError</code>.
		 * @param msg (CAL type: <code>Cal.Utilities.Parser.Message</code>)
		 * @param parser (CAL type: <code>Cal.Utilities.Parser.ParseError</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.ParseError</code>) 
		 */
		public static final SourceModel.Expr addErrorMessage(SourceModel.Expr msg, SourceModel.Expr parser) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.addErrorMessage), msg, parser});
		}

		/**
		 * Name binding for function: addErrorMessage.
		 * @see #addErrorMessage(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName addErrorMessage = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "addErrorMessage");

		/**
		 * A parser that parses a letter or digit (according to <code>Cal.Core.Char.isLetterOrDigit</code>) and returns the parsed character.
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser Cal.Core.Prelude.Char st Cal.Core.Prelude.Char</code>) 
		 */
		public static final SourceModel.Expr alphaNum() {
			return SourceModel.Expr.Var.make(Functions.alphaNum);
		}

		/**
		 * Name binding for function: alphaNum.
		 * @see #alphaNum()
		 */
		public static final QualifiedName alphaNum = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "alphaNum");

		/**
		 * A parser that succeeds for any character and returns the parsed character.
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser Cal.Core.Prelude.Char st Cal.Core.Prelude.Char</code>) 
		 */
		public static final SourceModel.Expr anyChar() {
			return SourceModel.Expr.Var.make(Functions.anyChar);
		}

		/**
		 * Name binding for function: anyChar.
		 * @see #anyChar()
		 */
		public static final QualifiedName anyChar = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "anyChar");

		/**
		 * The parser <code>Cal.Utilities.Parser.anyToken showTok</code> accepts any kind of token (that can
		 * be shown using <code>showTok</code>), and returns the accepted token.
		 * @param show (CAL type: <code>tok -> Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st tok</code>) 
		 */
		public static final SourceModel.Expr anyToken(SourceModel.Expr show) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.anyToken), show});
		}

		/**
		 * Name binding for function: anyToken.
		 * @see #anyToken(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName anyToken = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "anyToken");

		/**
		 * The parser <code>Cal.Utilities.Parser.between open close p</code> parses <code>open</code>, followed by <code>p</code> and <code>close</code>,
		 * and returns the value returned by <code>p</code>.
		 * @param open (CAL type: <code>Cal.Utilities.Parser.GenParser tok st open</code>)
		 * @param close (CAL type: <code>Cal.Utilities.Parser.GenParser tok st close</code>)
		 * @param p (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>) 
		 */
		public static final SourceModel.Expr between(SourceModel.Expr open, SourceModel.Expr close, SourceModel.Expr p) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.between), open, close, p});
		}

		/**
		 * Name binding for function: between.
		 * @see #between(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName between = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "between");

		/**
		 * The parser <code>Cal.Utilities.Parser.caseChar c</code> parses a single character <code>x</code> where <code>(Cal.Core.Char.toLowerCase x) == c</code>,
		 * and returns the parsed character (i.e. <code>x</code>).
		 * @param c (CAL type: <code>Cal.Core.Prelude.Char</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser Cal.Core.Prelude.Char st Cal.Core.Prelude.Char</code>) 
		 */
		public static final SourceModel.Expr caseChar(SourceModel.Expr c) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.caseChar), c});
		}

		/**
		 * @see #caseChar(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param c
		 * @return the SourceModel.Expr representing an application of caseChar
		 */
		public static final SourceModel.Expr caseChar(char c) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.caseChar), SourceModel.Expr.makeCharValue(c)});
		}

		/**
		 * Name binding for function: caseChar.
		 * @see #caseChar(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName caseChar = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "caseChar");

		/**
		 * The parser <code>Cal.Utilities.Parser.caseString s</code> parses a sequence of characters <code>chars</code> satisfying
		 * <code>(Cal.Collections.List.map Cal.Core.Char.toLowerCase chars) == s</code>, and returns the parsed characters in a list.
		 * @param s (CAL type: <code>[Cal.Core.Prelude.Char]</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser Cal.Core.Prelude.Char st [Cal.Core.Prelude.Char]</code>) 
		 */
		public static final SourceModel.Expr caseString(SourceModel.Expr s) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.caseString), s});
		}

		/**
		 * Name binding for function: caseString.
		 * @see #caseString(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName caseString = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "caseString");

		/**
		 * The parser <code>Cal.Utilities.Parser.caseStringU s</code> parses a sequence of characters <code>chars</code> satisfying
		 * <code>(Cal.Collections.List.map Cal.Core.Char.toUpperCase chars) == s</code>, and returns the parsed characters in a list.
		 * @param s (CAL type: <code>[Cal.Core.Prelude.Char]</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser Cal.Core.Prelude.Char st [Cal.Core.Prelude.Char]</code>) 
		 */
		public static final SourceModel.Expr caseStringU(SourceModel.Expr s) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.caseStringU), s});
		}

		/**
		 * Name binding for function: caseStringU.
		 * @see #caseStringU(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName caseStringU = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "caseStringU");

		/**
		 * The parser <code>Cal.Utilities.Parser.chainLeft p op x</code> parses <em>zero</em> or more occurrences of <code>p</code>,
		 * separated by <code>op</code>, and returns a value obtained by <em>left</em>-associative applications
		 * of the function returned by <code>op</code> to the values returned by <code>p</code>. If there are zero
		 * occurrences of <code>p</code>, the value <code>x</code> is returned.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.Parser.chainLeft1, Cal.Utilities.Parser.chainRight
		 * </dl>
		 * 
		 * @param p (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 * @param op (CAL type: <code>Cal.Utilities.Parser.GenParser tok st (a -> a -> a)</code>)
		 * @param x (CAL type: <code>a</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>) 
		 */
		public static final SourceModel.Expr chainLeft(SourceModel.Expr p, SourceModel.Expr op, SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.chainLeft), p, op, x});
		}

		/**
		 * Name binding for function: chainLeft.
		 * @see #chainLeft(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName chainLeft = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "chainLeft");

		/**
		 * The parser <code>Cal.Utilities.Parser.chainLeft1 p op</code> parses <em>one</em> or more occurrences of <code>p</code>,
		 * separated by <code>op</code>, and returns a value obtained by <em>left</em>-associative applications
		 * of the function returned by <code>op</code> to the values returned by <code>p</code>.
		 * <p>
		 * <code>Cal.Utilities.Parser.chainLeft1</code> is useful for eliminating left recursion which typically occurs in expression
		 * grammars. For example:
		 * 
		 * <pre> expr   = term   `Cal.Utilities.Parser.chainLeft1` mulop;
		 *  term   = factor `Cal.Utilities.Parser.chainLeft1` addop;
		 *  factor = (Cal.Utilities.Parser.between (char '(') (char ')') expr) `pOr`
		 *           (Cal.Utilities.Parser.many1 digit `pBind` (\digits -&gt;
		 *            pReturn $ stringToInt $ String.fromList digits));
		 *  
		 *  mulop  = (char '*' `pSeq` pReturn multiply) `pOr`
		 *           (char '/' `pSeq` pReturn divide);
		 *  addop  = (char '+' `pSeq` pReturn add) `pOr`
		 *           (char '-' `pSeq` pReturn subtract);</pre>
		 * 
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.Parser.chainLeft, Cal.Utilities.Parser.chainRight1
		 * </dl>
		 * 
		 * @param p (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 * @param op (CAL type: <code>Cal.Utilities.Parser.GenParser tok st (a -> a -> a)</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>) 
		 */
		public static final SourceModel.Expr chainLeft1(SourceModel.Expr p, SourceModel.Expr op) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.chainLeft1), p, op});
		}

		/**
		 * Name binding for function: chainLeft1.
		 * @see #chainLeft1(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName chainLeft1 = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "chainLeft1");

		/**
		 * The parser <code>Cal.Utilities.Parser.chainRight p op x</code> parses <em>zero</em> or more occurrences of <code>p</code>,
		 * separated by <code>op</code>, and returns a value obtained by <em>right</em>-associative applications
		 * of the function returned by <code>op</code> to the values returned by <code>p</code>. If there are zero
		 * occurrences of <code>p</code>, the value <code>x</code> is returned.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.Parser.chainRight1, Cal.Utilities.Parser.chainLeft
		 * </dl>
		 * 
		 * @param p (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 * @param op (CAL type: <code>Cal.Utilities.Parser.GenParser tok st (a -> a -> a)</code>)
		 * @param x (CAL type: <code>a</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>) 
		 */
		public static final SourceModel.Expr chainRight(SourceModel.Expr p, SourceModel.Expr op, SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.chainRight), p, op, x});
		}

		/**
		 * Name binding for function: chainRight.
		 * @see #chainRight(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName chainRight = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "chainRight");

		/**
		 * The parser <code>Cal.Utilities.Parser.chainRight1 p op</code> parses <em>one</em> or more occurrences of <code>p</code>,
		 * separated by <code>op</code>, and returns a value obtained by <em>right</em>-associative applications
		 * of the function returned by <code>op</code> to the values returned by <code>p</code>.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.Parser.chainRight, Cal.Utilities.Parser.chainLeft1
		 * </dl>
		 * 
		 * @param p (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 * @param op (CAL type: <code>Cal.Utilities.Parser.GenParser tok st (a -> a -> a)</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>) 
		 */
		public static final SourceModel.Expr chainRight1(SourceModel.Expr p, SourceModel.Expr op) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.chainRight1), p, op});
		}

		/**
		 * Name binding for function: chainRight1.
		 * @see #chainRight1(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName chainRight1 = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "chainRight1");

		/**
		 * The parser <code>char c</code> parses a single character <code>c</code>, and returns the parsed character (i.e. <code>c</code>).
		 * @param c (CAL type: <code>Cal.Core.Prelude.Char</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser Cal.Core.Prelude.Char st Cal.Core.Prelude.Char</code>) 
		 */
		public static final SourceModel.Expr char_(SourceModel.Expr c) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.char_), c});
		}

		/**
		 * @see #char_(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param c
		 * @return the SourceModel.Expr representing an application of char
		 */
		public static final SourceModel.Expr char_(char c) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.char_), SourceModel.Expr.makeCharValue(c)});
		}

		/**
		 * Name binding for function: char.
		 * @see #char_(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName char_ = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "char");

		/**
		 * The parser <code>Cal.Utilities.Parser.choice ps</code> tries to apply the parsers in the list <code>ps</code> in order using
		 * the <code>Cal.Utilities.Parser.pOr</code> combinator, until one of them succeeds. It returns the value of the succeeding parser.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.Parser.choiceT, Cal.Utilities.Parser.pOr
		 * </dl>
		 * 
		 * @param ps (CAL type: <code>[Cal.Utilities.Parser.GenParser tok st a]</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>) 
		 */
		public static final SourceModel.Expr choice(SourceModel.Expr ps) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.choice), ps});
		}

		/**
		 * Name binding for function: choice.
		 * @see #choice(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName choice = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "choice");

		/**
		 * The parser <code>Cal.Utilities.Parser.choiceT ps</code> tries to apply the parsers in the list <code>ps</code> in order using
		 * the <code>Cal.Utilities.Parser.pOrT</code> combinator, until one of them succeeds. It returns the value of the succeeding parser.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.Parser.choice, Cal.Utilities.Parser.pOrT
		 * </dl>
		 * 
		 * @param ps (CAL type: <code>[Cal.Utilities.Parser.GenParser tok st a]</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>) 
		 */
		public static final SourceModel.Expr choiceT(SourceModel.Expr ps) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.choiceT), ps});
		}

		/**
		 * Name binding for function: choiceT.
		 * @see #choiceT(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName choiceT = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "choiceT");

		/**
		 * The parser <code>Cal.Utilities.Parser.count n p</code> parses <code>n</code> occurrences of <code>p</code>. If <code>n &lt;= 0</code>,
		 * the parser returns the empty list <code>[]</code>, otherwise it returns a list of <code>n</code> values returned
		 * by applications of <code>p</code>.
		 * @param n (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @param p (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st [a]</code>) 
		 */
		public static final SourceModel.Expr count(SourceModel.Expr n, SourceModel.Expr p) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.count), n, p});
		}

		/**
		 * @see #count(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param n
		 * @param p
		 * @return the SourceModel.Expr representing an application of count
		 */
		public static final SourceModel.Expr count(int n, SourceModel.Expr p) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.count), SourceModel.Expr.makeIntValue(n), p});
		}

		/**
		 * Name binding for function: count.
		 * @see #count(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName count = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "count");

		/**
		 * A parser that parses a digit (according to <code>Cal.Core.Char.isDigit</code>) and returns the parsed character.
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser Cal.Core.Prelude.Char st Cal.Core.Prelude.Char</code>) 
		 */
		public static final SourceModel.Expr digit() {
			return SourceModel.Expr.Var.make(Functions.digit);
		}

		/**
		 * Name binding for function: digit.
		 * @see #digit()
		 */
		public static final QualifiedName digit = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "digit");

		/**
		 * The parser <code>Cal.Utilities.Parser.endBy p sep</code> parses <em>zero</em> or more occurrences of <code>p</code>,
		 * separated and ended by <code>sep</code>, and returns a list of values returned by the applications of <code>p</code>.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.Parser.endBy1, Cal.Utilities.Parser.sepBy, Cal.Utilities.Parser.sepEndBy
		 * </dl>
		 * 
		 * @param p (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 * @param sep (CAL type: <code>Cal.Utilities.Parser.GenParser tok st sep</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st [a]</code>) 
		 */
		public static final SourceModel.Expr endBy(SourceModel.Expr p, SourceModel.Expr sep) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.endBy), p, sep});
		}

		/**
		 * Name binding for function: endBy.
		 * @see #endBy(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName endBy = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "endBy");

		/**
		 * The parser <code>Cal.Utilities.Parser.endBy1 p sep</code> parses <em>one</em> or more occurrences of <code>p</code>,
		 * separated and ended by <code>sep</code>, and returns a list of values returned by the applications of <code>p</code>.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.Parser.endBy, Cal.Utilities.Parser.sepBy1, Cal.Utilities.Parser.sepEndBy1
		 * </dl>
		 * 
		 * @param p (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 * @param sep (CAL type: <code>Cal.Utilities.Parser.GenParser tok st sep</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st [a]</code>) 
		 */
		public static final SourceModel.Expr endBy1(SourceModel.Expr p, SourceModel.Expr sep) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.endBy1), p, sep});
		}

		/**
		 * Name binding for function: endBy1.
		 * @see #endBy1(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName endBy1 = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "endBy1");

		/**
		 * A parser that only succeeds at the end of the input.
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st ()</code>) 
		 */
		public static final SourceModel.Expr eof() {
			return SourceModel.Expr.Var.make(Functions.eof);
		}

		/**
		 * Name binding for function: eof.
		 * @see #eof()
		 */
		public static final QualifiedName eof = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "eof");

		/**
		 * 
		 * @param parser (CAL type: <code>Cal.Utilities.Parser.ParseError</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> iff the error is an unknown error.
		 */
		public static final SourceModel.Expr errorIsUnknown(SourceModel.Expr parser) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.errorIsUnknown), parser});
		}

		/**
		 * Name binding for function: errorIsUnknown.
		 * @see #errorIsUnknown(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName errorIsUnknown = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "errorIsUnknown");

		/**
		 * Extracts the list of error messages from the parse error, sorted according to ordering function <code>Cal.Utilities.Parser.messageCompare</code>.
		 * @param parser (CAL type: <code>Cal.Utilities.Parser.ParseError</code>)
		 * @return (CAL type: <code>[Cal.Utilities.Parser.Message]</code>) 
		 */
		public static final SourceModel.Expr errorMessages(SourceModel.Expr parser) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.errorMessages), parser});
		}

		/**
		 * Name binding for function: errorMessages.
		 * @see #errorMessages(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName errorMessages = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "errorMessages");

		/**
		 * Extracts the source position from the parse error.
		 * @param parser (CAL type: <code>Cal.Utilities.Parser.ParseError</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.SourcePos</code>) 
		 */
		public static final SourceModel.Expr errorPos(SourceModel.Expr parser) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.errorPos), parser});
		}

		/**
		 * Name binding for function: errorPos.
		 * @see #errorPos(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName errorPos = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "errorPos");

		/**
		 * The parser <code>Cal.Utilities.Parser.exactString s</code> parses a sequence of characters given by <code>s</code>, and returns
		 * the parsed characters in a list.
		 * @param s (CAL type: <code>[Cal.Core.Prelude.Char]</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser Cal.Core.Prelude.Char st [Cal.Core.Prelude.Char]</code>) 
		 */
		public static final SourceModel.Expr exactString(SourceModel.Expr s) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.exactString), s});
		}

		/**
		 * Name binding for function: exactString.
		 * @see #exactString(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName exactString = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "exactString");

		/**
		 * Same as <code>Cal.Utilities.Parser.getInputSeq</code>, but with a list of tokens instead of a token sequence.
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st [tok]</code>) 
		 */
		public static final SourceModel.Expr getInput() {
			return SourceModel.Expr.Var.make(Functions.getInput);
		}

		/**
		 * Name binding for function: getInput.
		 * @see #getInput()
		 */
		public static final QualifiedName getInput = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "getInput");

		/**
		 * A parser that returns the current input.
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st (Cal.Utilities.Parser.TokenSequence tok)</code>) 
		 */
		public static final SourceModel.Expr getInputSeq() {
			return SourceModel.Expr.Var.make(Functions.getInputSeq);
		}

		/**
		 * Name binding for function: getInputSeq.
		 * @see #getInputSeq()
		 */
		public static final QualifiedName getInputSeq = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "getInputSeq");

		/**
		 * A parser that returns the current complete state of the parser.
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st (Cal.Utilities.Parser.State tok st)</code>) 
		 */
		public static final SourceModel.Expr getParserState() {
			return SourceModel.Expr.Var.make(Functions.getParserState);
		}

		/**
		 * Name binding for function: getParserState.
		 * @see #getParserState()
		 */
		public static final QualifiedName getParserState = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "getParserState");

		/**
		 * A parser that returns the current source position.
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st Cal.Utilities.Parser.SourcePos</code>) 
		 */
		public static final SourceModel.Expr getPosition() {
			return SourceModel.Expr.Var.make(Functions.getPosition);
		}

		/**
		 * Name binding for function: getPosition.
		 * @see #getPosition()
		 */
		public static final QualifiedName getPosition = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "getPosition");

		/**
		 * A parser that returns the current user state.
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st st</code>) 
		 */
		public static final SourceModel.Expr getState() {
			return SourceModel.Expr.Var.make(Functions.getState);
		}

		/**
		 * Name binding for function: getState.
		 * @see #getState()
		 */
		public static final QualifiedName getState = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "getState");

		/**
		 * A parser that parses a hexadecimal digit (a <code>Cal.Utilities.Parser.digit</code> or a letter between <code>'a'</code> and <code>'f'</code> or
		 * <code>'A'</code> and <code>'F'</code>), and returns the parsed character.
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser Cal.Core.Prelude.Char st Cal.Core.Prelude.Char</code>) 
		 */
		public static final SourceModel.Expr hexDigit() {
			return SourceModel.Expr.Var.make(Functions.hexDigit);
		}

		/**
		 * Name binding for function: hexDigit.
		 * @see #hexDigit()
		 */
		public static final QualifiedName hexDigit = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "hexDigit");

		/**
		 * Increments the column number of a source position by the given amount.
		 * @param sp (CAL type: <code>Cal.Utilities.Parser.SourcePos</code>)
		 * @param n (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.SourcePos</code>) 
		 */
		public static final SourceModel.Expr incSourceColumn(SourceModel.Expr sp, SourceModel.Expr n) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.incSourceColumn), sp, n});
		}

		/**
		 * @see #incSourceColumn(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param sp
		 * @param n
		 * @return the SourceModel.Expr representing an application of incSourceColumn
		 */
		public static final SourceModel.Expr incSourceColumn(SourceModel.Expr sp, int n) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.incSourceColumn), sp, SourceModel.Expr.makeIntValue(n)});
		}

		/**
		 * Name binding for function: incSourceColumn.
		 * @see #incSourceColumn(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName incSourceColumn = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "incSourceColumn");

		/**
		 * Increments the line number of a source position by the given amount.
		 * @param sp (CAL type: <code>Cal.Utilities.Parser.SourcePos</code>)
		 * @param n (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.SourcePos</code>) 
		 */
		public static final SourceModel.Expr incSourceLine(SourceModel.Expr sp, SourceModel.Expr n) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.incSourceLine), sp, n});
		}

		/**
		 * @see #incSourceLine(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param sp
		 * @param n
		 * @return the SourceModel.Expr representing an application of incSourceLine
		 */
		public static final SourceModel.Expr incSourceLine(SourceModel.Expr sp, int n) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.incSourceLine), sp, SourceModel.Expr.makeIntValue(n)});
		}

		/**
		 * Name binding for function: incSourceLine.
		 * @see #incSourceLine(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName incSourceLine = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "incSourceLine");

		/**
		 * Constructs a <code>Cal.Utilities.Parser.SourcePos</code> with the given source name, for the position of line 1, column 1.
		 * @param sourceName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the name of the source.
		 * @return (CAL type: <code>Cal.Utilities.Parser.SourcePos</code>) 
		 */
		public static final SourceModel.Expr initialPos(SourceModel.Expr sourceName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.initialPos), sourceName});
		}

		/**
		 * @see #initialPos(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param sourceName
		 * @return the SourceModel.Expr representing an application of initialPos
		 */
		public static final SourceModel.Expr initialPos(java.lang.String sourceName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.initialPos), SourceModel.Expr.makeStringValue(sourceName)});
		}

		/**
		 * Name binding for function: initialPos.
		 * @see #initialPos(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName initialPos = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "initialPos");

		/**
		 * The parser <code>p `label` msg</code> behaves as the parser <code>p</code>, but whenever the parser <code>p</code>
		 * fails <em>without consuming any input</em>, it replaces the <em>expected</em> error messages with the message
		 * <code>msg</code>.
		 * <p>
		 * This is normally used at the end of a set of alternatives where it would be desirable to return an error
		 * message in terms of a higher level construct rather than returning all possible tokens.
		 * 
		 * @param p (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 * @param msg (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>) 
		 */
		public static final SourceModel.Expr label(SourceModel.Expr p, SourceModel.Expr msg) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.label), p, msg});
		}

		/**
		 * @see #label(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param p
		 * @param msg
		 * @return the SourceModel.Expr representing an application of label
		 */
		public static final SourceModel.Expr label(SourceModel.Expr p, java.lang.String msg) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.label), p, SourceModel.Expr.makeStringValue(msg)});
		}

		/**
		 * Name binding for function: label.
		 * @see #label(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName label = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "label");

		/**
		 * A parser that parses a letter (according to <code>Cal.Core.Char.isLetter</code>) and returns the parsed character.
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser Cal.Core.Prelude.Char st Cal.Core.Prelude.Char</code>) 
		 */
		public static final SourceModel.Expr letter() {
			return SourceModel.Expr.Var.make(Functions.letter);
		}

		/**
		 * Name binding for function: letter.
		 * @see #letter()
		 */
		public static final QualifiedName letter = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "letter");

		/**
		 * The parser <code>Cal.Utilities.Parser.lookAhead p</code> implements the functionality of lookahead: it applies parser <code>p</code> and
		 * captures its return value <em>without changing the parser's state and without consuming input</em>.
		 * @param p (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>) 
		 */
		public static final SourceModel.Expr lookAhead(SourceModel.Expr p) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lookAhead), p});
		}

		/**
		 * Name binding for function: lookAhead.
		 * @see #lookAhead(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName lookAhead = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "lookAhead");

		/**
		 * A parser that parses a lowercase letter (according to <code>Cal.Core.Char.isLowerCase</code>) and returns the parsed character.
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser Cal.Core.Prelude.Char st Cal.Core.Prelude.Char</code>) 
		 */
		public static final SourceModel.Expr lower() {
			return SourceModel.Expr.Var.make(Functions.lower);
		}

		/**
		 * Name binding for function: lower.
		 * @see #lower()
		 */
		public static final QualifiedName lower = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "lower");

		/**
		 * Constructs a token sequence using the input list as its internal representation.
		 * For character tokens, better performance is available by using <code>Cal.Utilities.Parser.makeTSString</code>
		 * if the input is available as a <code>Cal.Core.Prelude.String</code>.
		 * @param ts (CAL type: <code>[tok]</code>)
		 *          the list whose elements will be the tokens of the sequence.
		 * @return (CAL type: <code>Cal.Utilities.Parser.TokenSequence tok</code>) 
		 *          a token sequence providing access to the elements of the list.
		 */
		public static final SourceModel.Expr makeTSList(SourceModel.Expr ts) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeTSList), ts});
		}

		/**
		 * Name binding for function: makeTSList.
		 * @see #makeTSList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeTSList = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "makeTSList");

		/**
		 * Constructs a token sequence using the input string as its internal representation.
		 * This gives better performance than <code>Cal.Utilities.Parser.makeTSList</code> if the input is available as
		 * a <code>Cal.Core.Prelude.String</code>.
		 * @param s (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the string whose characters will be the tokens of the sequence.
		 * @return (CAL type: <code>Cal.Utilities.Parser.TokenSequence Cal.Core.Prelude.Char</code>) 
		 *          a token sequence providing access to the characters of the string.
		 */
		public static final SourceModel.Expr makeTSString(SourceModel.Expr s) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeTSString), s});
		}

		/**
		 * @see #makeTSString(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param s
		 * @return the SourceModel.Expr representing an application of makeTSString
		 */
		public static final SourceModel.Expr makeTSString(java.lang.String s) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeTSString), SourceModel.Expr.makeStringValue(s)});
		}

		/**
		 * Name binding for function: makeTSString.
		 * @see #makeTSString(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeTSString = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "makeTSString");

		/**
		 * The parser <code>Cal.Utilities.Parser.many p</code> applies the parser <code>p</code> <em>zero</em> or more times, and returns
		 * a list of the values returned by applications of <code>p</code>.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.Parser.many1
		 * </dl>
		 * 
		 * @param p (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st [a]</code>) 
		 */
		public static final SourceModel.Expr many(SourceModel.Expr p) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.many), p});
		}

		/**
		 * Name binding for function: many.
		 * @see #many(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName many = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "many");

		/**
		 * The parser <code>Cal.Utilities.Parser.many1 p</code> applies the parser <code>p</code> <em>one</em> or more times, and returns
		 * a list of the values returned by applications of <code>p</code>.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.Parser.many
		 * </dl>
		 * 
		 * @param p (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st [a]</code>) 
		 */
		public static final SourceModel.Expr many1(SourceModel.Expr p) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.many1), p});
		}

		/**
		 * Name binding for function: many1.
		 * @see #many1(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName many1 = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "many1");

		/**
		 * The parser <code>Cal.Utilities.Parser.manyTill p end</code> applies parser <code>p</code> <em>zero</em> or more times until parser
		 * <code>end</code> succeeds. It returns the list of values returned by applications of <code>p</code>.
		 * <p>
		 * This parser combinator is an optimized implementation of the following specification:
		 * 
		 * <pre> Cal.Utilities.Parser.manyTill p end =
		 *      (end `pSeq` pReturn []) `pOr`
		 *      (p `pBind` (\x -&gt;
		 *       Cal.Utilities.Parser.manyTill p end `pBind` (\xs -&gt;
		 *       pReturn (x:xs)
		 *      )));</pre>
		 * 
		 * 
		 * <p>
		 * An example for parsing comments:
		 * 
		 * <pre> xmlComment =
		 *      Cal.Utilities.Parser.exactString (String.toList "&lt;!--") `pSeq`
		 *      Cal.Utilities.Parser.manyTill Cal.Utilities.Parser.anyChar (Cal.Utilities.Parser.try (exactString (String.toList "--&gt;")));</pre>
		 * 
		 * 
		 * @param p (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 * @param end (CAL type: <code>Cal.Utilities.Parser.GenParser tok st end</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st [a]</code>) 
		 */
		public static final SourceModel.Expr manyTill(SourceModel.Expr p, SourceModel.Expr end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.manyTill), p, end});
		}

		/**
		 * Name binding for function: manyTill.
		 * @see #manyTill(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName manyTill = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "manyTill");

		/**
		 * The parser <code>Cal.Utilities.Parser.manyUntil p end</code> applies parser <code>p</code> <em>zero</em> or more times until either
		 * 1) the parser <code>end</code> would succeed if applied on the remaining input, or
		 * 2) the end of the input is reached.
		 * It returns the list of values returned by applications of <code>p</code>.
		 * <p>
		 * This parser combinator is exactly the same as:
		 * 
		 * <pre> Cal.Utilities.Parser.manyUntil p end =
		 *      Cal.Utilities.Parser.many ((Cal.Utilities.Parser.notFollowedBy end) `pSeq` p)</pre>
		 * 
		 * 
		 * 
		 * @param p (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 * @param end (CAL type: <code>Cal.Utilities.Parser.GenParser tok st end</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st [a]</code>) 
		 */
		public static final SourceModel.Expr manyUntil(SourceModel.Expr p, SourceModel.Expr end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.manyUntil), p, end});
		}

		/**
		 * Name binding for function: manyUntil.
		 * @see #manyUntil(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName manyUntil = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "manyUntil");

		/**
		 * Merges the two given <code>Cal.Utilities.Parser.ParseError</code> values into one.
		 * @param error1 (CAL type: <code>Cal.Utilities.Parser.ParseError</code>)
		 * @param error2 (CAL type: <code>Cal.Utilities.Parser.ParseError</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.ParseError</code>) 
		 */
		public static final SourceModel.Expr mergeError(SourceModel.Expr error1, SourceModel.Expr error2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.mergeError), error1, error2});
		}

		/**
		 * Name binding for function: mergeError.
		 * @see #mergeError(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName mergeError = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "mergeError");

		/**
		 * Compares two error messages without regards to their content. Only the
		 * data constructors are used in the ordering:
		 * <p>
		 * <code>Cal.Utilities.Parser.SysUnExpect</code> &lt; <code>Cal.Utilities.Parser.UnExpect</code> &lt; <code>Cal.Utilities.Parser.Expect</code> &lt; <code>Cal.Utilities.Parser.Message</code>
		 * 
		 * @param msg1 (CAL type: <code>Cal.Utilities.Parser.Message</code>)
		 * @param msg2 (CAL type: <code>Cal.Utilities.Parser.Message</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Ordering</code>) 
		 */
		public static final SourceModel.Expr messageCompare(SourceModel.Expr msg1, SourceModel.Expr msg2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.messageCompare), msg1, msg2});
		}

		/**
		 * Name binding for function: messageCompare.
		 * @see #messageCompare(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName messageCompare = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "messageCompare");

		/**
		 * <code>messageEq m1 m2</code> equals <code>Cal.Core.Prelude.True</code> if <code>Cal.Utilities.Parser.messageCompare m1 m2</code> equals <code>Cal.Core.Prelude.EQ</code>,
		 * in all other cases it equals <code>Cal.Core.Prelude.False</code>.
		 * @param msg1 (CAL type: <code>Cal.Utilities.Parser.Message</code>)
		 * @param msg2 (CAL type: <code>Cal.Utilities.Parser.Message</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr messageEq(SourceModel.Expr msg1, SourceModel.Expr msg2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.messageEq), msg1, msg2});
		}

		/**
		 * Name binding for function: messageEq.
		 * @see #messageEq(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName messageEq = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "messageEq");

		/**
		 * Extracts the message string from an error message.
		 * @param msg (CAL type: <code>Cal.Utilities.Parser.Message</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr messageString(SourceModel.Expr msg) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.messageString), msg});
		}

		/**
		 * Name binding for function: messageString.
		 * @see #messageString(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName messageString = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "messageString");

		/**
		 * Constructs a <code>Cal.Utilities.Parser.ParseError</code> representing an error at the given source position with the given message.
		 * @param msg (CAL type: <code>Cal.Utilities.Parser.Message</code>)
		 * @param pos (CAL type: <code>Cal.Utilities.Parser.SourcePos</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.ParseError</code>) 
		 */
		public static final SourceModel.Expr newErrorMessage(SourceModel.Expr msg, SourceModel.Expr pos) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.newErrorMessage), msg, pos});
		}

		/**
		 * Name binding for function: newErrorMessage.
		 * @see #newErrorMessage(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName newErrorMessage = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "newErrorMessage");

		/**
		 * Constructs a <code>Cal.Utilities.Parser.ParseError</code> representing an unknown error at the given source position.
		 * @param pos (CAL type: <code>Cal.Utilities.Parser.SourcePos</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.ParseError</code>) 
		 */
		public static final SourceModel.Expr newErrorUnknown(SourceModel.Expr pos) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.newErrorUnknown), pos});
		}

		/**
		 * Name binding for function: newErrorUnknown.
		 * @see #newErrorUnknown(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName newErrorUnknown = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "newErrorUnknown");

		/**
		 * Constructs a <code>Cal.Utilities.Parser.SourcePos</code> with the given source name, line and column.
		 * @param sourceName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the name of the source.
		 * @param line (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the line number.
		 * @param column (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the column number.
		 * @return (CAL type: <code>Cal.Utilities.Parser.SourcePos</code>) 
		 */
		public static final SourceModel.Expr newPos(SourceModel.Expr sourceName, SourceModel.Expr line, SourceModel.Expr column) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.newPos), sourceName, line, column});
		}

		/**
		 * @see #newPos(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param sourceName
		 * @param line
		 * @param column
		 * @return the SourceModel.Expr representing an application of newPos
		 */
		public static final SourceModel.Expr newPos(java.lang.String sourceName, int line, int column) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.newPos), SourceModel.Expr.makeStringValue(sourceName), SourceModel.Expr.makeIntValue(line), SourceModel.Expr.makeIntValue(column)});
		}

		/**
		 * Name binding for function: newPos.
		 * @see #newPos(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName newPos = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "newPos");

		/**
		 * A parser that parses a newline character (<code>'\n'</code>), and returns the parsed character.
		 * <p>
		 * Note this parser does <em>not</em> accept other variations on the concept of "newline", e.g.
		 * <code>'\r'</code> and <code>"\r\n"</code>.
		 * 
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser Cal.Core.Prelude.Char st Cal.Core.Prelude.Char</code>) 
		 */
		public static final SourceModel.Expr newline() {
			return SourceModel.Expr.Var.make(Functions.newline);
		}

		/**
		 * Name binding for function: newline.
		 * @see #newline()
		 */
		public static final QualifiedName newline = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "newline");

		/**
		 * The parser <code>noneOf cs</code> succeeds if the current character is <em>not</em> in the supplied list of characters
		 * <code>cs</code>, and returns the parsed character.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.Parser.satisfy
		 * </dl>
		 * 
		 * @param cs (CAL type: <code>[Cal.Core.Prelude.Char]</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser Cal.Core.Prelude.Char st Cal.Core.Prelude.Char</code>) 
		 */
		public static final SourceModel.Expr noneOf(SourceModel.Expr cs) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.noneOf), cs});
		}

		/**
		 * Name binding for function: noneOf.
		 * @see #noneOf(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName noneOf = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "noneOf");

		/**
		 * The parser <code>Cal.Utilities.Parser.notFollowedBy p</code> only succeeds when parser <code>p</code> fails.
		 * This parser does not consume any input.
		 * <p>
		 * This parser can be used to implement the <em>longest match</em> rule. For example, to recognize
		 * the keyword "let" but not the identifiers "lets" or "let3", the parser can be written as:
		 * 
		 * <pre> keywordLet = Cal.Utilities.Parser.try (Cal.Utilities.Parser.exactString (String.toList "let") `pSeq` Cal.Utilities.Parser.notFollowedBy Cal.Utilities.Parser.alphaNum);</pre>
		 * 
		 * 
		 * @param p (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st ()</code>) 
		 */
		public static final SourceModel.Expr notFollowedBy(SourceModel.Expr p) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.notFollowedBy), p});
		}

		/**
		 * Name binding for function: notFollowedBy.
		 * @see #notFollowedBy(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName notFollowedBy = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "notFollowedBy");

		/**
		 * A parser that parses an octal digit (a character between <code>'0'</code> and <code>'7'</code>), and returns the parsed
		 * character.
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser Cal.Core.Prelude.Char st Cal.Core.Prelude.Char</code>) 
		 */
		public static final SourceModel.Expr octDigit() {
			return SourceModel.Expr.Var.make(Functions.octDigit);
		}

		/**
		 * Name binding for function: octDigit.
		 * @see #octDigit()
		 */
		public static final QualifiedName octDigit = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "octDigit");

		/**
		 * The parser <code>oneOf cs</code> succeeds if the current character is in the supplied list of characters
		 * <code>cs</code>, and returns the parsed character.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.Parser.satisfy
		 * </dl>
		 * 
		 * @param cs (CAL type: <code>[Cal.Core.Prelude.Char]</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser Cal.Core.Prelude.Char st Cal.Core.Prelude.Char</code>) 
		 */
		public static final SourceModel.Expr oneOf(SourceModel.Expr cs) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.oneOf), cs});
		}

		/**
		 * Name binding for function: oneOf.
		 * @see #oneOf(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName oneOf = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "oneOf");

		/**
		 * The parser <code>Cal.Utilities.Parser.option x p</code> tries to apply parser <code>p</code>. If <code>p</code> fails without consuming
		 * input, it returns the value <code>x</code>, otherwise it returns the value returned by <code>p</code>.
		 * 
		 * @param x (CAL type: <code>a</code>)
		 * @param p (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>) 
		 */
		public static final SourceModel.Expr option(SourceModel.Expr x, SourceModel.Expr p) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.option), x, p});
		}

		/**
		 * Name binding for function: option.
		 * @see #option(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName option = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "option");

		/**
		 * The parser <code>Cal.Utilities.Parser.optional p</code> tries to apply parser <code>p</code>. If <code>p</code> fails without consuming
		 * input, it returns <code>Nothing</code>, otherwise it returns the application of <code>Cal.Core.Prelude.Just</code>
		 * to the value returned by <code>p</code>.
		 * @param p (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st (Cal.Core.Prelude.Maybe a)</code>) 
		 */
		public static final SourceModel.Expr optional(SourceModel.Expr p) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.optional), p});
		}

		/**
		 * Name binding for function: optional.
		 * @see #optional(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName optional = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "optional");

		/**
		 * The parser <code>p `pAppend` q</code> first applies the parser <code>p</code>, then applies the parser <code>q</code>,
		 * and finally returns the concatenation of the <code>Cal.Core.Prelude.Appendable</code> results of the parsers <code>p</code> and <code>q</code>.
		 * @param p1 (CAL type: <code>Cal.Core.Prelude.Appendable a => Cal.Utilities.Parser.GenParser tok st a</code>)
		 * @param p2 (CAL type: <code>Cal.Core.Prelude.Appendable a => Cal.Utilities.Parser.GenParser tok st a</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Appendable a => Cal.Utilities.Parser.GenParser tok st a</code>) 
		 */
		public static final SourceModel.Expr pAppend(SourceModel.Expr p1, SourceModel.Expr p2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.pAppend), p1, p2});
		}

		/**
		 * Name binding for function: pAppend.
		 * @see #pAppend(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName pAppend = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "pAppend");

		/**
		 * <code>pBind</code> implements a parser combinator for sequencing. The parser <code>p `pBind` f</code>
		 * first applies parser <code>p</code>, then applies <code>f</code> to the returned value of <code>p</code>,
		 * and finally applies the parser returned by <code>f</code>.
		 * <p>
		 * Since the second parser can depend on the result of the first parser, this allows <em>context
		 * sensitive</em> grammars to be parsed.
		 * 
		 * @param parser (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 *          the first parser in the sequence.
		 * @param f (CAL type: <code>a -> Cal.Utilities.Parser.GenParser tok st b</code>)
		 *          the function which is to consume the result returned by <code>parser</code> and then generate
		 * the second parser to be applied in the sequence.
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st b</code>) 
		 *          a parser which will apply the two parsers in sequence.
		 */
		public static final SourceModel.Expr pBind(SourceModel.Expr parser, SourceModel.Expr f) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.pBind), parser, f});
		}

		/**
		 * Name binding for function: pBind.
		 * @see #pBind(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName pBind = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "pBind");

		/**
		 * The parser <code>pFail msg</code> always fails with the error message <code>msg</code>
		 * without consuming any input.
		 * @param msg (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the error message.
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>) 
		 */
		public static final SourceModel.Expr pFail(SourceModel.Expr msg) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.pFail), msg});
		}

		/**
		 * @see #pFail(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param msg
		 * @return the SourceModel.Expr representing an application of pFail
		 */
		public static final SourceModel.Expr pFail(java.lang.String msg) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.pFail), SourceModel.Expr.makeStringValue(msg)});
		}

		/**
		 * Name binding for function: pFail.
		 * @see #pFail(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName pFail = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "pFail");

		/**
		 * The parser <code>pMap f parser</code> is an optimized implementation of
		 * 
		 * <pre> parser `pBind` (\x -&gt;
		 *  pReturn (f x))
		 * </pre>
		 * 
		 * @param f (CAL type: <code>a -> b</code>)
		 * @param parser (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st b</code>) 
		 */
		public static final SourceModel.Expr pMap(SourceModel.Expr f, SourceModel.Expr parser) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.pMap), f, parser});
		}

		/**
		 * Name binding for function: pMap.
		 * @see #pMap(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName pMap = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "pMap");

		/**
		 * This parser combinator implements an alternative. The parser <code>p `pOr` q</code> first applies
		 * <code>p</code>. If it succeeds, the value of <code>p</code> is returned. If <code>p</code> fails
		 * <em>without consuming any input</em>, then parser <code>q</code> is tried.
		 * <p>
		 * The parser is <em>predictive</em> since <code>q</code> is tried only when parser <code>p</code> does
		 * not consume any input. This non-backtracking behaviour allows for both an efficient implementation
		 * of the parser combinators and the generation of good error messages.
		 * 
		 * @param parser1 (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 *          the parser for the first alternative.
		 * @param parser2 (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 *          the parser for the second alternative, which is tried only if <code>parser1</code> fails.
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>) 
		 */
		public static final SourceModel.Expr pOr(SourceModel.Expr parser1, SourceModel.Expr parser2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.pOr), parser1, parser2});
		}

		/**
		 * Name binding for function: pOr.
		 * @see #pOr(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName pOr = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "pOr");

		/**
		 * The parser <code>p `Cal.Utilities.Parser.pOrT` q</code> is exactly the same as <code>(Cal.Utilities.Parser.try p) `Cal.Utilities.Parser.pOr` q</code>.
		 * In particular, the parser will attempt to apply parser <code>p</code>. If <code>p</code> fails, the parser <code>q</code> is attempted
		 * on the same input.
		 * <p>
		 * In the future, pOrT may be optimized.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.Parser.try, Cal.Utilities.Parser.pOr
		 * </dl>
		 * 
		 * @param parser1 (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 * @param parser2 (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>) 
		 */
		public static final SourceModel.Expr pOrT(SourceModel.Expr parser1, SourceModel.Expr parser2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.pOrT), parser1, parser2});
		}

		/**
		 * Name binding for function: pOrT.
		 * @see #pOrT(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName pOrT = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "pOrT");

		/**
		 * The parser <code>Cal.Utilities.Parser.pOrT3 p1 p2 p3</code> is exactly the same as
		 * 
		 * <pre> (Cal.Utilities.Parser.try p1) `Cal.Utilities.Parser.pOr`
		 *  (Cal.Utilities.Parser.try p2) `Cal.Utilities.Parser.pOr`
		 *  p3</pre>
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.Parser.try, Cal.Utilities.Parser.pOr
		 * </dl>
		 * 
		 * @param parser1 (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 * @param parser2 (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 * @param parser3 (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>) 
		 */
		public static final SourceModel.Expr pOrT3(SourceModel.Expr parser1, SourceModel.Expr parser2, SourceModel.Expr parser3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.pOrT3), parser1, parser2, parser3});
		}

		/**
		 * Name binding for function: pOrT3.
		 * @see #pOrT3(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName pOrT3 = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "pOrT3");

		/**
		 * The parser <code>Cal.Utilities.Parser.pOrT4 p1 p2 p3 p4</code> is exactly the same as
		 * 
		 * <pre> (Cal.Utilities.Parser.try p1) `Cal.Utilities.Parser.pOr`
		 *  (Cal.Utilities.Parser.try p2) `Cal.Utilities.Parser.pOr`
		 *  (Cal.Utilities.Parser.try p3) `Cal.Utilities.Parser.pOr`
		 *  p4</pre>
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.Parser.try, Cal.Utilities.Parser.pOr
		 * </dl>
		 * 
		 * @param parser1 (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 * @param parser2 (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 * @param parser3 (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 * @param parser4 (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>) 
		 */
		public static final SourceModel.Expr pOrT4(SourceModel.Expr parser1, SourceModel.Expr parser2, SourceModel.Expr parser3, SourceModel.Expr parser4) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.pOrT4), parser1, parser2, parser3, parser4});
		}

		/**
		 * Name binding for function: pOrT4.
		 * @see #pOrT4(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName pOrT4 = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "pOrT4");

		/**
		 * The parser <code>Cal.Utilities.Parser.pOrT5 p1 p2 p3 p4 p5</code> is exactly the same as
		 * 
		 * <pre> (Cal.Utilities.Parser.try p1) `Cal.Utilities.Parser.pOr`
		 *  (Cal.Utilities.Parser.try p2) `Cal.Utilities.Parser.pOr`
		 *  (Cal.Utilities.Parser.try p3) `Cal.Utilities.Parser.pOr`
		 *  (Cal.Utilities.Parser.try p4) `Cal.Utilities.Parser.pOr`
		 *  p5</pre>
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.Parser.try, Cal.Utilities.Parser.pOr
		 * </dl>
		 * 
		 * @param parser1 (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 * @param parser2 (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 * @param parser3 (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 * @param parser4 (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 * @param parser5 (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>) 
		 */
		public static final SourceModel.Expr pOrT5(SourceModel.Expr parser1, SourceModel.Expr parser2, SourceModel.Expr parser3, SourceModel.Expr parser4, SourceModel.Expr parser5) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.pOrT5), parser1, parser2, parser3, parser4, parser5});
		}

		/**
		 * Name binding for function: pOrT5.
		 * @see #pOrT5(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName pOrT5 = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "pOrT5");

		/**
		 * The parser <code>Cal.Utilities.Parser.pOrT6 p1 p2 p3 p4 p5 p6</code> is exactly the same as
		 * 
		 * <pre> (Cal.Utilities.Parser.try p1) `Cal.Utilities.Parser.pOr`
		 *  (Cal.Utilities.Parser.try p2) `Cal.Utilities.Parser.pOr`
		 *  (Cal.Utilities.Parser.try p3) `Cal.Utilities.Parser.pOr`
		 *  (Cal.Utilities.Parser.try p4) `Cal.Utilities.Parser.pOr`
		 *  (Cal.Utilities.Parser.try p5) `Cal.Utilities.Parser.pOr`
		 *  p6</pre>
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.Parser.try, Cal.Utilities.Parser.pOr
		 * </dl>
		 * 
		 * @param parser1 (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 * @param parser2 (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 * @param parser3 (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 * @param parser4 (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 * @param parser5 (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 * @param parser6 (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>) 
		 */
		public static final SourceModel.Expr pOrT6(SourceModel.Expr parser1, SourceModel.Expr parser2, SourceModel.Expr parser3, SourceModel.Expr parser4, SourceModel.Expr parser5, SourceModel.Expr parser6) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.pOrT6), parser1, parser2, parser3, parser4, parser5, parser6});
		}

		/**
		 * Name binding for function: pOrT6.
		 * @see #pOrT6(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName pOrT6 = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "pOrT6");

		/**
		 * The parser <code>Cal.Utilities.Parser.pOrT7 p1 p2 p3 p4 p5 p6 p7</code> is exactly the same as
		 * 
		 * <pre> (Cal.Utilities.Parser.try p1) `Cal.Utilities.Parser.pOr`
		 *  (Cal.Utilities.Parser.try p2) `Cal.Utilities.Parser.pOr`
		 *  (Cal.Utilities.Parser.try p3) `Cal.Utilities.Parser.pOr`
		 *  (Cal.Utilities.Parser.try p4) `Cal.Utilities.Parser.pOr`
		 *  (Cal.Utilities.Parser.try p5) `Cal.Utilities.Parser.pOr`
		 *  (Cal.Utilities.Parser.try p6) `Cal.Utilities.Parser.pOr`
		 *  p7</pre>
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.Parser.try, Cal.Utilities.Parser.pOr
		 * </dl>
		 * 
		 * @param parser1 (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 * @param parser2 (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 * @param parser3 (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 * @param parser4 (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 * @param parser5 (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 * @param parser6 (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 * @param parser7 (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>) 
		 */
		public static final SourceModel.Expr pOrT7(SourceModel.Expr parser1, SourceModel.Expr parser2, SourceModel.Expr parser3, SourceModel.Expr parser4, SourceModel.Expr parser5, SourceModel.Expr parser6, SourceModel.Expr parser7) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.pOrT7), parser1, parser2, parser3, parser4, parser5, parser6, parser7});
		}

		/**
		 * Name binding for function: pOrT7.
		 * @see #pOrT7(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName pOrT7 = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "pOrT7");

		/**
		 * The parser <code>pReturn x</code> always succeeds with value <code>x</code> without consuming any input.
		 * @param x (CAL type: <code>a</code>)
		 *          the value to be returned by the parser.
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>) 
		 */
		public static final SourceModel.Expr pReturn(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.pReturn), x});
		}

		/**
		 * Name binding for function: pReturn.
		 * @see #pReturn(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName pReturn = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "pReturn");

		/**
		 * <code>pSeq</code> implements a parser combinator for sequencing. The parser <code>p `pSeq` q</code>
		 * first applies the parser <code>p</code>, then applies the parser <code>q</code>, discarding the value
		 * returned by the first parser.
		 * <p>
		 * The parser <code>p `pSeq` q</code> is an optimized implementation of <code>p `pBind` (\ unused -&gt; q)</code>.
		 * 
		 * @param parser (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 *          the first parser in the sequence.
		 * @param k (CAL type: <code>Cal.Utilities.Parser.GenParser tok st b</code>)
		 *          the second parser in the sequence.
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st b</code>) 
		 *          a parser which will apply the two parsers in sequence.
		 */
		public static final SourceModel.Expr pSeq(SourceModel.Expr parser, SourceModel.Expr k) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.pSeq), parser, k});
		}

		/**
		 * Name binding for function: pSeq.
		 * @see #pSeq(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName pSeq = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "pSeq");

		/**
		 * The parser <code>pZero</code> always fails without consuming any input. To report an error with a message,
		 * use <code>Cal.Utilities.Parser.pFail</code> instead.
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>) 
		 */
		public static final SourceModel.Expr pZero() {
			return SourceModel.Expr.Var.make(Functions.pZero);
		}

		/**
		 * Name binding for function: pZero.
		 * @see #pZero()
		 */
		public static final QualifiedName pZero = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "pZero");

		/**
		 * Same as <code>Cal.Utilities.Parser.parseSeq</code>, but with a list of tokens instead of a token sequence.
		 * @param p (CAL type: <code>Cal.Utilities.Parser.GenParser tok () a</code>)
		 * @param name (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @param input (CAL type: <code>[tok]</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Either Cal.Utilities.Parser.ParseError a</code>) 
		 */
		public static final SourceModel.Expr parse(SourceModel.Expr p, SourceModel.Expr name, SourceModel.Expr input) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.parse), p, name, input});
		}

		/**
		 * @see #parse(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param p
		 * @param name
		 * @param input
		 * @return the SourceModel.Expr representing an application of parse
		 */
		public static final SourceModel.Expr parse(SourceModel.Expr p, java.lang.String name, SourceModel.Expr input) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.parse), p, SourceModel.Expr.makeStringValue(name), input});
		}

		/**
		 * Name binding for function: parse.
		 * @see #parse(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName parse = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "parse");

		/**
		 * <code>parseSeq p name input</code> runs a parser <code>p</code> without user state. The <code>name</code>
		 * is only used in error messages and may be the empty string.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.Parser.runParser
		 * </dl>
		 * 
		 * @param p (CAL type: <code>Cal.Utilities.Parser.GenParser tok () a</code>)
		 * @param name (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @param input (CAL type: <code>Cal.Utilities.Parser.TokenSequence tok</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Either Cal.Utilities.Parser.ParseError a</code>) 
		 *          either a <code>Cal.Utilities.Parser.ParseError</code> via <code>Cal.Core.Prelude.Left</code>, or a value of type <code>a</code> via <code>Cal.Core.Prelude.Right</code>.
		 */
		public static final SourceModel.Expr parseSeq(SourceModel.Expr p, SourceModel.Expr name, SourceModel.Expr input) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.parseSeq), p, name, input});
		}

		/**
		 * @see #parseSeq(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param p
		 * @param name
		 * @param input
		 * @return the SourceModel.Expr representing an application of parseSeq
		 */
		public static final SourceModel.Expr parseSeq(SourceModel.Expr p, java.lang.String name, SourceModel.Expr input) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.parseSeq), p, SourceModel.Expr.makeStringValue(name), input});
		}

		/**
		 * Name binding for function: parseSeq.
		 * @see #parseSeq(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName parseSeq = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "parseSeq");

		/**
		 * Same as <code>Cal.Utilities.Parser.parseTestSeq</code>, but with a list of tokens instead of a token sequence.
		 * @param p (CAL type: <code>Cal.Core.Debug.Show a => Cal.Utilities.Parser.GenParser tok () a</code>)
		 * @param input (CAL type: <code>[tok]</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr parseTest(SourceModel.Expr p, SourceModel.Expr input) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.parseTest), p, input});
		}

		/**
		 * Name binding for function: parseTest.
		 * @see #parseTest(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName parseTest = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "parseTest");

		/**
		 * <code>parseTestSeq p input</code> applies a parser <code>p</code> against input <code>input</code>
		 * and returns the result as a string. Useful for testing parsers.
		 * @param p (CAL type: <code>Cal.Core.Debug.Show a => Cal.Utilities.Parser.GenParser tok () a</code>)
		 * @param input (CAL type: <code>Cal.Utilities.Parser.TokenSequence tok</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr parseTestSeq(SourceModel.Expr p, SourceModel.Expr input) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.parseTestSeq), p, input});
		}

		/**
		 * Name binding for function: parseTestSeq.
		 * @see #parseTestSeq(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName parseTestSeq = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "parseTestSeq");

		/**
		 * Same as <code>Cal.Utilities.Parser.runParserSeq</code>, but with a list of tokens instead of a token sequence.
		 * @param p (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 * @param st (CAL type: <code>st</code>)
		 * @param name (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @param input (CAL type: <code>[tok]</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Either Cal.Utilities.Parser.ParseError a</code>) 
		 */
		public static final SourceModel.Expr runParser(SourceModel.Expr p, SourceModel.Expr st, SourceModel.Expr name, SourceModel.Expr input) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.runParser), p, st, name, input});
		}

		/**
		 * @see #runParser(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param p
		 * @param st
		 * @param name
		 * @param input
		 * @return the SourceModel.Expr representing an application of runParser
		 */
		public static final SourceModel.Expr runParser(SourceModel.Expr p, SourceModel.Expr st, java.lang.String name, SourceModel.Expr input) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.runParser), p, st, SourceModel.Expr.makeStringValue(name), input});
		}

		/**
		 * Name binding for function: runParser.
		 * @see #runParser(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName runParser = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "runParser");

		/**
		 * <code>runParserSeq p state name input</code> runs parser <code>p</code> on the input token sequence
		 * <code>input</code>, obtained from source named by <code>name</code> with the initial user state <code>st</code>.
		 * The <code>name</code> is only used in error messages and may be the empty string.
		 * @param p (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 * @param st (CAL type: <code>st</code>)
		 * @param name (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @param input (CAL type: <code>Cal.Utilities.Parser.TokenSequence tok</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Either Cal.Utilities.Parser.ParseError a</code>) 
		 *          either a <code>Cal.Utilities.Parser.ParseError</code> via <code>Cal.Core.Prelude.Left</code>, or a value of type <code>a</code> via <code>Cal.Core.Prelude.Right</code>.
		 */
		public static final SourceModel.Expr runParserSeq(SourceModel.Expr p, SourceModel.Expr st, SourceModel.Expr name, SourceModel.Expr input) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.runParserSeq), p, st, name, input});
		}

		/**
		 * @see #runParserSeq(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param p
		 * @param st
		 * @param name
		 * @param input
		 * @return the SourceModel.Expr representing an application of runParserSeq
		 */
		public static final SourceModel.Expr runParserSeq(SourceModel.Expr p, SourceModel.Expr st, java.lang.String name, SourceModel.Expr input) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.runParserSeq), p, st, SourceModel.Expr.makeStringValue(name), input});
		}

		/**
		 * Name binding for function: runParserSeq.
		 * @see #runParserSeq(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName runParserSeq = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "runParserSeq");

		/**
		 * The parser <code>satisfy f</code> succeeds for any character for which the supplied predicate
		 * <code>f</code> returns <code>Cal.Core.Prelude.True</code>, and returns the parsed character.
		 * <p>
		 * For example, <code>Cal.Utilities.Parser.oneOf</code> can be defined as:
		 * 
		 * <pre> oneOf = satisfy (\c -&gt; Cal.Collections.List.isElem c cs);</pre>
		 * 
		 * 
		 * @param f (CAL type: <code>Cal.Core.Prelude.Char -> Cal.Core.Prelude.Boolean</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser Cal.Core.Prelude.Char st Cal.Core.Prelude.Char</code>) 
		 */
		public static final SourceModel.Expr satisfy(SourceModel.Expr f) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.satisfy), f});
		}

		/**
		 * Name binding for function: satisfy.
		 * @see #satisfy(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName satisfy = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "satisfy");

		/**
		 * The parser <code>Cal.Utilities.Parser.sepBy p sep</code> parses <em>zero</em> or more occurrences of <code>p</code>,
		 * separated by <code>sep</code>, and returns a list of the values returned by the applications of <code>p</code>.
		 * <p>
		 * For example:
		 * 
		 * <pre> commaSep p = p `Cal.Utilities.Parser.sepBy` Cal.Utilities.Parser.char ',';</pre>
		 * 
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.Parser.sepBy1, Cal.Utilities.Parser.endBy, Cal.Utilities.Parser.sepEndBy
		 * </dl>
		 * 
		 * @param p (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 * @param sep (CAL type: <code>Cal.Utilities.Parser.GenParser tok st sep</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st [a]</code>) 
		 */
		public static final SourceModel.Expr sepBy(SourceModel.Expr p, SourceModel.Expr sep) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.sepBy), p, sep});
		}

		/**
		 * Name binding for function: sepBy.
		 * @see #sepBy(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName sepBy = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "sepBy");

		/**
		 * The parser <code>Cal.Utilities.Parser.sepBy1 p sep</code> parses <em>one</em> or more occurrences of <code>p</code>,
		 * separated by <code>sep</code>, and returns a list of the values returned by the applications of <code>p</code>.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.Parser.sepBy, Cal.Utilities.Parser.endBy1, Cal.Utilities.Parser.sepEndBy1
		 * </dl>
		 * 
		 * @param p (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 * @param sep (CAL type: <code>Cal.Utilities.Parser.GenParser tok st sep</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st [a]</code>) 
		 */
		public static final SourceModel.Expr sepBy1(SourceModel.Expr p, SourceModel.Expr sep) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.sepBy1), p, sep});
		}

		/**
		 * Name binding for function: sepBy1.
		 * @see #sepBy1(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName sepBy1 = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "sepBy1");

		/**
		 * The parser <code>Cal.Utilities.Parser.sepEndBy p sep</code> parses <em>zero</em> or more occurrences of <code>p</code>,
		 * separated and <em>optionally</em> ended by <code>sep</code>, and returns a list of values returned by the applications
		 * of <code>p</code>.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.Parser.sepEndBy1, Cal.Utilities.Parser.sepBy, Cal.Utilities.Parser.endBy
		 * </dl>
		 * 
		 * @param p (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 * @param sep (CAL type: <code>Cal.Utilities.Parser.GenParser tok st sep</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st [a]</code>) 
		 */
		public static final SourceModel.Expr sepEndBy(SourceModel.Expr p, SourceModel.Expr sep) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.sepEndBy), p, sep});
		}

		/**
		 * Name binding for function: sepEndBy.
		 * @see #sepEndBy(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName sepEndBy = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "sepEndBy");

		/**
		 * The parser <code>Cal.Utilities.Parser.sepEndBy1 p sep</code> parses <em>one</em> or more occurrences of <code>p</code>,
		 * separated and <em>optionally</em> ended by <code>sep</code>, and returns a list of values returned by the applications
		 * of <code>p</code>.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.Parser.sepEndBy, Cal.Utilities.Parser.sepBy1, Cal.Utilities.Parser.endBy1
		 * </dl>
		 * 
		 * @param p (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 * @param sep (CAL type: <code>Cal.Utilities.Parser.GenParser tok st sep</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st [a]</code>) 
		 */
		public static final SourceModel.Expr sepEndBy1(SourceModel.Expr p, SourceModel.Expr sep) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.sepEndBy1), p, sep});
		}

		/**
		 * Name binding for function: sepEndBy1.
		 * @see #sepEndBy1(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName sepEndBy1 = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "sepEndBy1");

		/**
		 * The parser <code>Cal.Utilities.Parser.sequence list</code> applies the parsers in <code>list</code> in sequence, and returns
		 * a list containing the values returned by each parser.
		 * @param list (CAL type: <code>[Cal.Utilities.Parser.GenParser tok st a]</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st [a]</code>) 
		 */
		public static final SourceModel.Expr sequence(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.sequence), list});
		}

		/**
		 * Name binding for function: sequence.
		 * @see #sequence(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName sequence = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "sequence");

		/**
		 * Sets the error message of the given <code>Cal.Utilities.Parser.ParseError</code>.
		 * @param msg (CAL type: <code>Cal.Utilities.Parser.Message</code>)
		 * @param parser (CAL type: <code>Cal.Utilities.Parser.ParseError</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.ParseError</code>) 
		 */
		public static final SourceModel.Expr setErrorMessage(SourceModel.Expr msg, SourceModel.Expr parser) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.setErrorMessage), msg, parser});
		}

		/**
		 * Name binding for function: setErrorMessage.
		 * @see #setErrorMessage(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName setErrorMessage = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "setErrorMessage");

		/**
		 * Sets the source position of the given <code>Cal.Utilities.Parser.ParseError</code>.
		 * @param pos (CAL type: <code>Cal.Utilities.Parser.SourcePos</code>)
		 * @param parser (CAL type: <code>Cal.Utilities.Parser.ParseError</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.ParseError</code>) 
		 */
		public static final SourceModel.Expr setErrorPos(SourceModel.Expr pos, SourceModel.Expr parser) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.setErrorPos), pos, parser});
		}

		/**
		 * Name binding for function: setErrorPos.
		 * @see #setErrorPos(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName setErrorPos = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "setErrorPos");

		/**
		 * Same as <code>Cal.Utilities.Parser.setInputSeq</code>, but with a list of tokens instead of a token sequence.
		 * @param input (CAL type: <code>[tok]</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st ()</code>) 
		 */
		public static final SourceModel.Expr setInput(SourceModel.Expr input) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.setInput), input});
		}

		/**
		 * Name binding for function: setInput.
		 * @see #setInput(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName setInput = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "setInput");

		/**
		 * The parser <code>setInput input</code> continues parsing with <code>input</code>. An example use for the
		 * <code>Cal.Utilities.Parser.getInput</code> and <code>setInput</code> functions is to deal with <em>include files</em>.
		 * @param input (CAL type: <code>Cal.Utilities.Parser.TokenSequence tok</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st ()</code>) 
		 */
		public static final SourceModel.Expr setInputSeq(SourceModel.Expr input) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.setInputSeq), input});
		}

		/**
		 * Name binding for function: setInputSeq.
		 * @see #setInputSeq(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName setInputSeq = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "setInputSeq");

		/**
		 * The parser <code>setParserState st</code> sets the parser's state to <code>st</code>.
		 * @param st (CAL type: <code>Cal.Utilities.Parser.State tok st</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st (Cal.Utilities.Parser.State tok st)</code>) 
		 */
		public static final SourceModel.Expr setParserState(SourceModel.Expr st) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.setParserState), st});
		}

		/**
		 * Name binding for function: setParserState.
		 * @see #setParserState(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName setParserState = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "setParserState");

		/**
		 * The parser <code>setPosition pos</code> sets the current source position to <code>pos</code>.
		 * @param pos (CAL type: <code>Cal.Utilities.Parser.SourcePos</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st ()</code>) 
		 */
		public static final SourceModel.Expr setPosition(SourceModel.Expr pos) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.setPosition), pos});
		}

		/**
		 * Name binding for function: setPosition.
		 * @see #setPosition(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName setPosition = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "setPosition");

		/**
		 * Sets the column number of a source position to the given value.
		 * @param sp (CAL type: <code>Cal.Utilities.Parser.SourcePos</code>)
		 * @param n (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.SourcePos</code>) 
		 */
		public static final SourceModel.Expr setSourceColumn(SourceModel.Expr sp, SourceModel.Expr n) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.setSourceColumn), sp, n});
		}

		/**
		 * @see #setSourceColumn(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param sp
		 * @param n
		 * @return the SourceModel.Expr representing an application of setSourceColumn
		 */
		public static final SourceModel.Expr setSourceColumn(SourceModel.Expr sp, int n) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.setSourceColumn), sp, SourceModel.Expr.makeIntValue(n)});
		}

		/**
		 * Name binding for function: setSourceColumn.
		 * @see #setSourceColumn(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName setSourceColumn = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "setSourceColumn");

		/**
		 * Sets the line number of a source position to the given value.
		 * @param sp (CAL type: <code>Cal.Utilities.Parser.SourcePos</code>)
		 * @param n (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.SourcePos</code>) 
		 */
		public static final SourceModel.Expr setSourceLine(SourceModel.Expr sp, SourceModel.Expr n) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.setSourceLine), sp, n});
		}

		/**
		 * @see #setSourceLine(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param sp
		 * @param n
		 * @return the SourceModel.Expr representing an application of setSourceLine
		 */
		public static final SourceModel.Expr setSourceLine(SourceModel.Expr sp, int n) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.setSourceLine), sp, SourceModel.Expr.makeIntValue(n)});
		}

		/**
		 * Name binding for function: setSourceLine.
		 * @see #setSourceLine(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName setSourceLine = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "setSourceLine");

		/**
		 * Sets the source name of a source position to the given string.
		 * @param sp (CAL type: <code>Cal.Utilities.Parser.SourcePos</code>)
		 * @param n (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.SourcePos</code>) 
		 */
		public static final SourceModel.Expr setSourceName(SourceModel.Expr sp, SourceModel.Expr n) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.setSourceName), sp, n});
		}

		/**
		 * @see #setSourceName(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param sp
		 * @param n
		 * @return the SourceModel.Expr representing an application of setSourceName
		 */
		public static final SourceModel.Expr setSourceName(SourceModel.Expr sp, java.lang.String n) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.setSourceName), sp, SourceModel.Expr.makeStringValue(n)});
		}

		/**
		 * Name binding for function: setSourceName.
		 * @see #setSourceName(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName setSourceName = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "setSourceName");

		/**
		 * The parser <code>setState st</code> sets the user state to <code>st</code>.
		 * @param st (CAL type: <code>st</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st ()</code>) 
		 */
		public static final SourceModel.Expr setState(SourceModel.Expr st) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.setState), st});
		}

		/**
		 * Name binding for function: setState.
		 * @see #setState(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName setState = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "setState");

		/**
		 * The standard function for showing error messages. Formats a list of error messages with
		 * the given set of keywords in the desired language.
		 * @param msgOr (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the string in the desired language equivalent to the English string "or".
		 * @param msgUnknown (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the string in the desired language equivalent to the English string "unknown parse error".
		 * @param msgExpecting (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the string in the desired language equivalent to the English string "expecting".
		 * @param msgUnExpected (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the string in the desired language equivalent to the English string "unexpected".
		 * @param msgEndOfInput (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the string in the desired language equivalent to the English string "end of input".
		 * @param msgs (CAL type: <code>[Cal.Utilities.Parser.Message]</code>)
		 *          the list of error messages.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the error messages formatted as a string.
		 */
		public static final SourceModel.Expr showErrorMessages(SourceModel.Expr msgOr, SourceModel.Expr msgUnknown, SourceModel.Expr msgExpecting, SourceModel.Expr msgUnExpected, SourceModel.Expr msgEndOfInput, SourceModel.Expr msgs) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showErrorMessages), msgOr, msgUnknown, msgExpecting, msgUnExpected, msgEndOfInput, msgs});
		}

		/**
		 * @see #showErrorMessages(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param msgOr
		 * @param msgUnknown
		 * @param msgExpecting
		 * @param msgUnExpected
		 * @param msgEndOfInput
		 * @param msgs
		 * @return the SourceModel.Expr representing an application of showErrorMessages
		 */
		public static final SourceModel.Expr showErrorMessages(java.lang.String msgOr, java.lang.String msgUnknown, java.lang.String msgExpecting, java.lang.String msgUnExpected, java.lang.String msgEndOfInput, SourceModel.Expr msgs) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showErrorMessages), SourceModel.Expr.makeStringValue(msgOr), SourceModel.Expr.makeStringValue(msgUnknown), SourceModel.Expr.makeStringValue(msgExpecting), SourceModel.Expr.makeStringValue(msgUnExpected), SourceModel.Expr.makeStringValue(msgEndOfInput), msgs});
		}

		/**
		 * Name binding for function: showErrorMessages.
		 * @see #showErrorMessages(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showErrorMessages = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "showErrorMessages");

		/**
		 * Formats the given <code>Cal.Utilities.Parser.ParseError</code> into a string for display.
		 * @param err (CAL type: <code>Cal.Utilities.Parser.ParseError</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr showParseError(SourceModel.Expr err) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showParseError), err});
		}

		/**
		 * Name binding for function: showParseError.
		 * @see #showParseError(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showParseError = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "showParseError");

		/**
		 * Formats the given <code>Cal.Utilities.Parser.SourcePos</code> into a string for display.
		 * @param sp (CAL type: <code>Cal.Utilities.Parser.SourcePos</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr showSourcePos(SourceModel.Expr sp) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.showSourcePos), sp});
		}

		/**
		 * Name binding for function: showSourcePos.
		 * @see #showSourcePos(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName showSourcePos = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "showSourcePos");

		/**
		 * The parser <code>Cal.Utilities.Parser.skipMany p</code> applies the parser <code>p</code> <em>zero</em> or more times, but discarding the results
		 * returned by <code>p</code>. In other words, it is a more efficient way of specifying:
		 * 
		 * <pre> Cal.Utilities.Parser.many p `pSeq`
		 *  pReturn ()</pre>
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.Parser.skipMany1
		 * </dl>
		 * 
		 * @param p (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st ()</code>) 
		 */
		public static final SourceModel.Expr skipMany(SourceModel.Expr p) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.skipMany), p});
		}

		/**
		 * Name binding for function: skipMany.
		 * @see #skipMany(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName skipMany = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "skipMany");

		/**
		 * The parser <code>Cal.Utilities.Parser.skipMany1 p</code> applies the parser <code>p</code> <em>one</em> or more times, but discarding the results
		 * returned by <code>p</code>. In other words, it is a more efficient way of specifying:
		 * 
		 * <pre> Cal.Utilities.Parser.many1 p `pSeq`
		 *  pReturn ()</pre>
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.Parser.skipMany
		 * </dl>
		 * 
		 * @param p (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st ()</code>) 
		 */
		public static final SourceModel.Expr skipMany1(SourceModel.Expr p) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.skipMany1), p});
		}

		/**
		 * Name binding for function: skipMany1.
		 * @see #skipMany1(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName skipMany1 = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "skipMany1");

		/**
		 * Extracts the column number from a source position.
		 * @param sp (CAL type: <code>Cal.Utilities.Parser.SourcePos</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 */
		public static final SourceModel.Expr sourceColumn(SourceModel.Expr sp) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.sourceColumn), sp});
		}

		/**
		 * Name binding for function: sourceColumn.
		 * @see #sourceColumn(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName sourceColumn = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "sourceColumn");

		/**
		 * Extracts the line number from a source position.
		 * @param sp (CAL type: <code>Cal.Utilities.Parser.SourcePos</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 */
		public static final SourceModel.Expr sourceLine(SourceModel.Expr sp) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.sourceLine), sp});
		}

		/**
		 * Name binding for function: sourceLine.
		 * @see #sourceLine(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName sourceLine = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "sourceLine");

		/**
		 * Extracts the name of the source from a source position.
		 * @param sp (CAL type: <code>Cal.Utilities.Parser.SourcePos</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr sourceName(SourceModel.Expr sp) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.sourceName), sp});
		}

		/**
		 * Name binding for function: sourceName.
		 * @see #sourceName(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName sourceName = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "sourceName");

		/**
		 * A parser that parses a whitespace character (according to <code>Cal.Core.Char.isWhitespace</code>), and returns the
		 * parsed character.
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser Cal.Core.Prelude.Char st Cal.Core.Prelude.Char</code>) 
		 */
		public static final SourceModel.Expr space() {
			return SourceModel.Expr.Var.make(Functions.space);
		}

		/**
		 * Name binding for function: space.
		 * @see #space()
		 */
		public static final QualifiedName space = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "space");

		/**
		 * A parser that parses <em>zero</em> or more whitespace characters and discards the result.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.Parser.skipMany, Cal.Utilities.Parser.space, Cal.Utilities.Parser.spaces1
		 * </dl>
		 * 
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser Cal.Core.Prelude.Char st ()</code>) 
		 */
		public static final SourceModel.Expr spaces() {
			return SourceModel.Expr.Var.make(Functions.spaces);
		}

		/**
		 * Name binding for function: spaces.
		 * @see #spaces()
		 */
		public static final QualifiedName spaces = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "spaces");

		/**
		 * A parser that parses <em>one</em> or more whitespace characters and discards the result.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.Parser.skipMany1, Cal.Utilities.Parser.space, Cal.Utilities.Parser.spaces
		 * </dl>
		 * 
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser Cal.Core.Prelude.Char st ()</code>) 
		 */
		public static final SourceModel.Expr spaces1() {
			return SourceModel.Expr.Var.make(Functions.spaces1);
		}

		/**
		 * Name binding for function: spaces1.
		 * @see #spaces1()
		 */
		public static final QualifiedName spaces1 = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "spaces1");

		/**
		 * A parser that parses a tab character (<code>'\t'</code>), and returns the parsed character.
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser Cal.Core.Prelude.Char st Cal.Core.Prelude.Char</code>) 
		 */
		public static final SourceModel.Expr tab() {
			return SourceModel.Expr.Var.make(Functions.tab);
		}

		/**
		 * Name binding for function: tab.
		 * @see #tab()
		 */
		public static final QualifiedName tab = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "tab");

		/**
		 * The parser <code>token showTok posFromTok testTok</code> accepts a token <code>t</code> with
		 * result <code>x</code> when the function <code>testTok t</code> returns <code>Cal.Core.Prelude.Just x</code>.
		 * The source position of <code>t</code> should be returned by <code>posFromTok t</code> and the token can be shown
		 * using <code>showTok t</code>.
		 * <p>
		 * This combinator is meant to be used as a building block for defining parsers that work on user-defined token streams.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.Parser.tokenPrim, Cal.Utilities.Parser.tokenPrimEx, Cal.Utilities.Parser.tokenSatisfy
		 * </dl>
		 * 
		 * @param show (CAL type: <code>tok -> Cal.Core.Prelude.String</code>)
		 * @param tokpos (CAL type: <code>tok -> Cal.Utilities.Parser.SourcePos</code>)
		 * @param test (CAL type: <code>tok -> Cal.Core.Prelude.Maybe a</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>) 
		 */
		public static final SourceModel.Expr token(SourceModel.Expr show, SourceModel.Expr tokpos, SourceModel.Expr test) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.token), show, tokpos, test});
		}

		/**
		 * Name binding for function: token.
		 * @see #token(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName token = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "token");

		/**
		 * Same as <code>Cal.Utilities.Parser.tokenPrimSeq</code>, but with a list of tokens instead of a token sequence.
		 * @param show (CAL type: <code>tok -> Cal.Core.Prelude.String</code>)
		 * @param nextpos (CAL type: <code>Cal.Utilities.Parser.SourcePos -> tok -> [tok] -> Cal.Utilities.Parser.SourcePos</code>)
		 * @param test (CAL type: <code>tok -> Cal.Core.Prelude.Maybe a</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>) 
		 */
		public static final SourceModel.Expr tokenPrim(SourceModel.Expr show, SourceModel.Expr nextpos, SourceModel.Expr test) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.tokenPrim), show, nextpos, test});
		}

		/**
		 * Name binding for function: tokenPrim.
		 * @see #tokenPrim(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName tokenPrim = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "tokenPrim");

		/**
		 * Same as <code>Cal.Utilities.Parser.tokenPrimExSeq</code>, but with a list of tokens instead of a token sequence.
		 * @param show (CAL type: <code>tok -> Cal.Core.Prelude.String</code>)
		 * @param nextpos (CAL type: <code>Cal.Utilities.Parser.SourcePos -> tok -> [tok] -> Cal.Utilities.Parser.SourcePos</code>)
		 * @param mbNextState (CAL type: <code>Cal.Core.Prelude.Maybe (Cal.Utilities.Parser.SourcePos -> tok -> [tok] -> st -> st)</code>)
		 * @param test (CAL type: <code>tok -> Cal.Core.Prelude.Maybe a</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>) 
		 */
		public static final SourceModel.Expr tokenPrimEx(SourceModel.Expr show, SourceModel.Expr nextpos, SourceModel.Expr mbNextState, SourceModel.Expr test) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.tokenPrimEx), show, nextpos, mbNextState, test});
		}

		/**
		 * Name binding for function: tokenPrimEx.
		 * @see #tokenPrimEx(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName tokenPrimEx = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "tokenPrimEx");

		/**
		 * The parser <code>tokenPrimEx show nextpos mbnextstate test</code>, recognises tokens when <code>test</code> returns
		 * <code>Cal.Core.Prelude.Just x</code> (and returns the value <code>x</code>). Tokens are shown in error messages using <code>show</code>.
		 * The position is calculated using <code>nextpos</code>, and finally, <code>mbnextstate</code>, can hold a function that updates
		 * the user state on every token recognised (e.g. for counting tokens).
		 * <p>
		 * The <code>mbNextState</code> function is packed into a <code>Cal.Core.Prelude.Maybe</code> type for performance reasons.
		 * <p>
		 * This is the most primitive combinator for accepting tokens, and is meant to be used as a building block for
		 * defining parsers that work on user-defined token streams.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.Parser.token, Cal.Utilities.Parser.tokenPrimSeq, Cal.Utilities.Parser.tokenSatisfy
		 * </dl>
		 * 
		 * @param show (CAL type: <code>tok -> Cal.Core.Prelude.String</code>)
		 * @param nextpos (CAL type: <code>Cal.Utilities.Parser.SourcePos -> tok -> Cal.Utilities.Parser.TokenSequence tok -> Cal.Utilities.Parser.SourcePos</code>)
		 * @param mbNextState (CAL type: <code>Cal.Core.Prelude.Maybe (Cal.Utilities.Parser.SourcePos -> tok -> Cal.Utilities.Parser.TokenSequence tok -> st -> st)</code>)
		 * @param test (CAL type: <code>tok -> Cal.Core.Prelude.Maybe a</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>) 
		 */
		public static final SourceModel.Expr tokenPrimExSeq(SourceModel.Expr show, SourceModel.Expr nextpos, SourceModel.Expr mbNextState, SourceModel.Expr test) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.tokenPrimExSeq), show, nextpos, mbNextState, test});
		}

		/**
		 * Name binding for function: tokenPrimExSeq.
		 * @see #tokenPrimExSeq(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName tokenPrimExSeq = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "tokenPrimExSeq");

		/**
		 * The parser <code>tokenPrim showTok nextPos testTok</code> accepts a token <code>t</code> with result
		 * <code>x</code> when the function <code>testTok t</code> returns <code>Cal.Core.Prelude.Just x</code>. The token can
		 * be shown using <code>showTok t</code>. The position of the <em>next</em> token should be returned when
		 * <code>nextPos</code> is called with the current source position <code>pos</code>, the current token <code>t</code>
		 * and the rest of the token <code>toks</code>, i.e. <code>(nextPos pos t toks)</code>.
		 * <p>
		 * This is one of the most primitive combinators for accepting tokens, and is meant to be used as a building block for
		 * defining parsers that work on user-defined token streams.
		 * <p>
		 * For example, the <code>Cal.Utilities.Parser.char</code> parser could be implemented as:
		 * 
		 * <pre> char :: Char -&gt; GenParser Char st Char
		 *  char c =
		 *      let
		 *          showChar x       = "'" ++ x ++ "'";
		 *          testChar x       = if (x == c) then Just x else Nothing;
		 *          nextPos pos x xs = Cal.Utilities.Parser.updatePosChar pos x;
		 *      in
		 *          tokenPrim showChar nextPos testChar;</pre>
		 * 
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.Parser.token, Cal.Utilities.Parser.tokenPrimEx, Cal.Utilities.Parser.tokenSatisfy
		 * </dl>
		 * 
		 * @param show (CAL type: <code>tok -> Cal.Core.Prelude.String</code>)
		 * @param nextpos (CAL type: <code>Cal.Utilities.Parser.SourcePos -> tok -> Cal.Utilities.Parser.TokenSequence tok -> Cal.Utilities.Parser.SourcePos</code>)
		 * @param test (CAL type: <code>tok -> Cal.Core.Prelude.Maybe a</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>) 
		 */
		public static final SourceModel.Expr tokenPrimSeq(SourceModel.Expr show, SourceModel.Expr nextpos, SourceModel.Expr test) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.tokenPrimSeq), show, nextpos, test});
		}

		/**
		 * Name binding for function: tokenPrimSeq.
		 * @see #tokenPrimSeq(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName tokenPrimSeq = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "tokenPrimSeq");

		/**
		 * Same as <code>Cal.Utilities.Parser.tokenSatisfySeq</code>, but with a list of tokens instead of a token sequence.
		 * @param show (CAL type: <code>tok -> Cal.Core.Prelude.String</code>)
		 * @param nextpos (CAL type: <code>Cal.Utilities.Parser.SourcePos -> tok -> [tok] -> Cal.Utilities.Parser.SourcePos</code>)
		 * @param f (CAL type: <code>tok -> Cal.Core.Prelude.Boolean</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st tok</code>) 
		 */
		public static final SourceModel.Expr tokenSatisfy(SourceModel.Expr show, SourceModel.Expr nextpos, SourceModel.Expr f) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.tokenSatisfy), show, nextpos, f});
		}

		/**
		 * Name binding for function: tokenSatisfy.
		 * @see #tokenSatisfy(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName tokenSatisfy = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "tokenSatisfy");

		/**
		 * The parser <code>tokenSatisfy showTok nextPos f</code> accepts and returns token <code>t</code> when the function
		 * <code>f t</code> returns <code>Cal.Core.Prelude.True</code>. The token can be shown using <code>showTok t</code>. The position of the
		 * <em>next</em> token should be returned when <code>nextPos</code> is called with the current source position
		 * <code>pos</code>, the current token <code>t</code> and the rest of the token <code>toks</code>, i.e. <code>(nextPos pos t toks)</code>.
		 * <p>
		 * <strong>Note:</strong> this parser combinator has slightly different error reporting semantics when compared to this
		 * alternate implementation:
		 * 
		 * <pre> tokenSat showTok nextPos f = tokenPrim showTok nextPos (\x -&gt; if (f x) then Just x else Nothing);
		 * </pre>
		 * 
		 * in that the position reported for an unexpected token is the position where the token is found, and not
		 * the starting position of the parser before the rule is applied.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.Parser.token, Cal.Utilities.Parser.tokenPrim, Cal.Utilities.Parser.tokenPrimEx
		 * </dl>
		 * 
		 * @param show (CAL type: <code>tok -> Cal.Core.Prelude.String</code>)
		 * @param nextpos (CAL type: <code>Cal.Utilities.Parser.SourcePos -> tok -> Cal.Utilities.Parser.TokenSequence tok -> Cal.Utilities.Parser.SourcePos</code>)
		 * @param f (CAL type: <code>tok -> Cal.Core.Prelude.Boolean</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st tok</code>) 
		 */
		public static final SourceModel.Expr tokenSatisfySeq(SourceModel.Expr show, SourceModel.Expr nextpos, SourceModel.Expr f) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.tokenSatisfySeq), show, nextpos, f});
		}

		/**
		 * Name binding for function: tokenSatisfySeq.
		 * @see #tokenSatisfySeq(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName tokenSatisfySeq = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "tokenSatisfySeq");

		/**
		 * Converts a token sequence to a list of tokens.
		 * @param tokseq (CAL type: <code>Cal.Utilities.Parser.TokenSequence tok</code>)
		 *          the token sequence to convert.
		 * @return (CAL type: <code>[tok]</code>) 
		 *          a list containing the tokens in the sequence.
		 */
		public static final SourceModel.Expr tokenSequenceToList(SourceModel.Expr tokseq) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.tokenSequenceToList), tokseq});
		}

		/**
		 * Name binding for function: tokenSequenceToList.
		 * @see #tokenSequenceToList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName tokenSequenceToList = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "tokenSequenceToList");

		/**
		 * This parser combinator is an optimized implementation of the following specification.
		 * 
		 * <pre> tokens shows nextposs s =
		 *      let
		 *          show c = shows [c];
		 *          nextpos pos c cs = nextposs pos [c];
		 *          testtok x = if (x == c) then Just c else Nothing;
		 *          
		 *          scan toks =
		 *              case toks of
		 *              []   -&gt; pReturn s;
		 *              c:cs -&gt; (Cal.Utilities.Parser.tokenPrim show nextpos testtok `label` shows s) `pSeq`
		 *                      (scan cs);
		 *              ;
		 *      in
		 *          scan s;</pre>
		 * 
		 * @param shows (CAL type: <code>Cal.Core.Prelude.Eq tok => [tok] -> Cal.Core.Prelude.String</code>)
		 * @param nextposs (CAL type: <code>Cal.Core.Prelude.Eq tok => Cal.Utilities.Parser.SourcePos -> [tok] -> Cal.Utilities.Parser.SourcePos</code>)
		 * @param s (CAL type: <code>Cal.Core.Prelude.Eq tok => [tok]</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Eq tok => Cal.Utilities.Parser.GenParser tok st [tok]</code>) 
		 */
		public static final SourceModel.Expr tokens(SourceModel.Expr shows, SourceModel.Expr nextposs, SourceModel.Expr s) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.tokens), shows, nextposs, s});
		}

		/**
		 * Name binding for function: tokens.
		 * @see #tokens(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName tokens = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "tokens");

		/**
		 * This parser combinator is an optimized implementation of the following specification.
		 * 
		 * <pre> tokensCompare shows nextposs compareToken s =
		 *      let
		 *          show c = shows [c];
		 *          nextpos pos c cs = nextposs pos [c];
		 *          testtok x = if (compareToken x c) then Just c else Nothing;
		 *          
		 *          scan toks =
		 *              case toks of
		 *              []   -&gt; pReturn s;
		 *              c:cs -&gt; (Cal.Utilities.Parser.tokenPrim show nextpos testtok `label` shows s) `pSeq`
		 *                      (scan cs);
		 *              ;
		 *      in
		 *          scan s;</pre>
		 * 
		 * @param shows (CAL type: <code>[tok] -> Cal.Core.Prelude.String</code>)
		 * @param nextposs (CAL type: <code>Cal.Utilities.Parser.SourcePos -> [tok] -> Cal.Utilities.Parser.SourcePos</code>)
		 * @param compareToken (CAL type: <code>tok -> tok -> Cal.Core.Prelude.Boolean</code>)
		 * @param s (CAL type: <code>[tok]</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st [tok]</code>) 
		 */
		public static final SourceModel.Expr tokensCompare(SourceModel.Expr shows, SourceModel.Expr nextposs, SourceModel.Expr compareToken, SourceModel.Expr s) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.tokensCompare), shows, nextposs, compareToken, s});
		}

		/**
		 * Name binding for function: tokensCompare.
		 * @see #tokensCompare(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName tokensCompare = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "tokensCompare");

		/**
		 * The parser <code>translateState inputFn outputFn parser</code> updates the user state by 
		 * applying the first function <code>inputFn</code> to it, applies the parser <code>p</code>, 
		 * updates the user state by applying the second function <code>outputFn</code> to the original
		 * state and the new state, then returns the value returned by <code>p</code>. In other words, 
		 * it is equivalent to the following (which does not type-check successfully because of
		 * the mismatched state types):
		 * <p>
		 * 
		 * <pre> Cal.Utilities.Parser.getState `pBind` (\stateBefore -&gt;
		 *  Cal.Utilities.Parser.setState (inputFn stateBefore) `pSeq`
		 *  p `pBind` (\x -&gt;
		 *  Cal.Utilities.Parser.getState `pBind` (\stateAfter -&gt;
		 *  Cal.Utilities.Parser.setState (outputFn stateBefore stateAfter) `pSeq`
		 *  Cal.Utilities.Parser.pReturn x)))</pre>
		 * 
		 * <p>
		 * This is the most primitive combinator for combining parsers with different user state types.
		 * One common approach is to ignore the outer state and pass a fixed initial state "s" to the
		 * wrapped parser "p", and then to ignore the inner state and reset the outer state afterwards, 
		 * effectively allowing the outer state to "pass through" the wrapped parser: 
		 * <p>
		 * 
		 * <pre> Cal.Utilities.Parser.translateState (Cal.Core.Prelude.const s) Cal.Core.Prelude.const p</pre>
		 * 
		 * <p>
		 * Alternatively, two complimentary functions "f" and "g" can be used to map the user state
		 * from one type to another and then back again, ignoring the original state when mapping back:
		 * <p>
		 * 
		 * <pre> Cal.Utilities.Parser.translateState f (Cal.Core.Prelude.const g) p</pre>
		 * 
		 * 
		 * @param inputFn (CAL type: <code>a -> b</code>)
		 * @param outputFn (CAL type: <code>a -> b -> a</code>)
		 * @param p (CAL type: <code>Cal.Utilities.Parser.GenParser tok b c</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok a c</code>) 
		 */
		public static final SourceModel.Expr translateState(SourceModel.Expr inputFn, SourceModel.Expr outputFn, SourceModel.Expr p) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.translateState), inputFn, outputFn, p});
		}

		/**
		 * Name binding for function: translateState.
		 * @see #translateState(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName translateState = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "translateState");

		/**
		 * The parser <code>try p</code> behaves like the parser <code>p</code>, except that it pretends that
		 * it has not consumed any input when an error occurs.
		 * <p>
		 * This combinator can be used whenever arbitrary lookahead is needed. A typical usage pattern
		 * is <code>(try parser1) `pOr` parser2</code>, although in practice it is more efficient to use the equivalent
		 * optimized form <code>parser1 `Cal.Utilities.Parser.pOrT` parser2</code>.
		 * <p>
		 * In the above example, if <code>parser1</code> fails, <code>(try parser1)</code> pretends that it has not consumed
		 * any input, and the <code>Cal.Utilities.Parser.pOr</code> combinator will then try <code>parser2</code>.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Utilities.Parser.pOrT
		 * </dl>
		 * 
		 * @param parser (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>) 
		 */
		public static final SourceModel.Expr try_(SourceModel.Expr parser) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.try_), parser});
		}

		/**
		 * Name binding for function: try.
		 * @see #try_(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName try_ = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "try");

		/**
		 * The parser <code>unexpected msg</code> always fails with an <em>unexpected</em> error message
		 * <code>msg</code> without consuming any input.
		 * <p>
		 * The functions <code>Cal.Utilities.Parser.pFail</code>, <code>Cal.Utilities.Parser.label</code> and <code>unexpected</code> are the three parser
		 * combinators used for generating error messages. Of these, <code>Cal.Utilities.Parser.label</code> is the one
		 * that is most commonly applicable.
		 * 
		 * @param msg (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st a</code>) 
		 */
		public static final SourceModel.Expr unexpected(SourceModel.Expr msg) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.unexpected), msg});
		}

		/**
		 * @see #unexpected(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param msg
		 * @return the SourceModel.Expr representing an application of unexpected
		 */
		public static final SourceModel.Expr unexpected(java.lang.String msg) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.unexpected), SourceModel.Expr.makeStringValue(msg)});
		}

		/**
		 * Name binding for function: unexpected.
		 * @see #unexpected(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName unexpected = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "unexpected");

		/**
		 * Updates the source position given a character.
		 * <ul>
		 *  <li>
		 *   If the character is a newline (<code>'\n'</code>) the line number is incremented by 1.
		 *   (Note <code>'\r'</code> and <code>"\r\n"</code> are not recognized as newlines.)
		 *  </li>
		 *  <li>
		 *   If the character is a tab (<code>'\t'</code>) the column number is incremented to the next nearest
		 *   multiple of 8, i.e. <code>(column + 8 - ((column-1) `mod` 8))</code>.
		 *  </li>
		 *  <li>
		 *   In all other cases, the column is incremented by 1.
		 *   
		 *  </li>
		 * </ul>
		 * 
		 * @param pos (CAL type: <code>Cal.Utilities.Parser.SourcePos</code>)
		 * @param c (CAL type: <code>Cal.Core.Prelude.Char</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.SourcePos</code>) 
		 */
		public static final SourceModel.Expr updatePosChar(SourceModel.Expr pos, SourceModel.Expr c) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.updatePosChar), pos, c});
		}

		/**
		 * @see #updatePosChar(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param pos
		 * @param c
		 * @return the SourceModel.Expr representing an application of updatePosChar
		 */
		public static final SourceModel.Expr updatePosChar(SourceModel.Expr pos, char c) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.updatePosChar), pos, SourceModel.Expr.makeCharValue(c)});
		}

		/**
		 * Name binding for function: updatePosChar.
		 * @see #updatePosChar(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName updatePosChar = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "updatePosChar");

		/**
		 * The expression <code>updatePosString pos s</code> updates the source position <code>pos</code>
		 * by calling <code>Cal.Utilities.Parser.updatePosChar</code> on every character in <code>s</code>.
		 * @param pos (CAL type: <code>Cal.Utilities.Parser.SourcePos</code>)
		 * @param aString (CAL type: <code>[Cal.Core.Prelude.Char]</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.SourcePos</code>) 
		 */
		public static final SourceModel.Expr updatePosString(SourceModel.Expr pos, SourceModel.Expr aString) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.updatePosString), pos, aString});
		}

		/**
		 * Name binding for function: updatePosString.
		 * @see #updatePosString(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName updatePosString = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "updatePosString");

		/**
		 * The parser <code>updateState f</code> updates the user state by applying the update function <code>f</code>
		 * on it. In other words, it is equivalent to:
		 * 
		 * <pre> Cal.Utilities.Parser.getState `pBind` (\st -&gt;
		 *  Cal.Utilities.Parser.setState (f st))</pre>
		 * 
		 * @param f (CAL type: <code>st -> st</code>)
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser tok st ()</code>) 
		 */
		public static final SourceModel.Expr updateState(SourceModel.Expr f) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.updateState), f});
		}

		/**
		 * Name binding for function: updateState.
		 * @see #updateState(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName updateState = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "updateState");

		/**
		 * A parser that parses an uppercase letter (according to <code>Cal.Core.Char.isUpperCase</code>) and returns the parsed character.
		 * @return (CAL type: <code>Cal.Utilities.Parser.GenParser Cal.Core.Prelude.Char st Cal.Core.Prelude.Char</code>) 
		 */
		public static final SourceModel.Expr upper() {
			return SourceModel.Expr.Var.make(Functions.upper);
		}

		/**
		 * Name binding for function: upper.
		 * @see #upper()
		 */
		public static final QualifiedName upper = 
			QualifiedName.make(CAL_Parser.MODULE_NAME, "upper");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = 308789783;

}
