/*
 * Copyright (c) 2007 BUSINESS OBJECTS SOFTWARE LIMITED
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *  
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *  
 *     * Neither the name of Business Objects nor the names of its contributors
 *       may be used to endorse or promote products derived from this software
 *       without specific prior written permission.
 *  
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

/*
 * SourceModelCodeFormatter.java
 * Created: Feb 6, 2007
 * By: rcypher
 */

package org.openquark.cal.compiler;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.openquark.cal.compiler.SourceModel.ArgBindings;
import org.openquark.cal.compiler.SourceModel.CALDoc;
import org.openquark.cal.compiler.SourceModel.Constraint;
import org.openquark.cal.compiler.SourceModel.Expr;
import org.openquark.cal.compiler.SourceModel.TypeExprDefn.Function;
import org.openquark.cal.compiler.SourceModel.Name;
import org.openquark.cal.compiler.SourceModel.FieldPattern;
import org.openquark.cal.compiler.SourceModel.FunctionDefn;
import org.openquark.cal.compiler.SourceModel.FunctionTypeDeclaration;
import org.openquark.cal.compiler.SourceModel.Import;
import org.openquark.cal.compiler.SourceModel.InstanceDefn;
import org.openquark.cal.compiler.SourceModel.LocalDefn;
import org.openquark.cal.compiler.SourceModel.ModuleDefn;
import org.openquark.cal.compiler.SourceModel.Parameter;
import org.openquark.cal.compiler.SourceModel.Pattern;
import org.openquark.cal.compiler.SourceModel.SourceElement;
import org.openquark.cal.compiler.SourceModel.TypeClassDefn;
import org.openquark.cal.compiler.SourceModel.TypeConstructorDefn;
import org.openquark.cal.compiler.SourceModel.TypeExprDefn;
import org.openquark.cal.compiler.SourceModel.TypeSignature;
import org.openquark.cal.compiler.SourceModel.Expr.Associativity;
import org.openquark.cal.compiler.SourceModel.InstanceDefn.InstanceMethod;
import org.openquark.cal.compiler.SourceModel.TypeClassDefn.ClassMethodDefn;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.util.ArrayStack;

/**
 * This class can be used to generated formatted CAL source from a SourceModel
 * instance.
 * 
 * @author rcypher, mbyne
 * 
 */
public final class SourceModelCodeFormatter {

    /** System Line separator. */
    private static final String EOL = System.getProperty("line.separator");

    /** this adds debugging information which shows the formatting combinators */
    final private static boolean SHOW_DEBUG_LABELS = false;

    /** these are the default options for formatting */
    final public static Options DEFAULT_OPTIONS = new OptionsBuilder().build();

    /** this class is used to iterate through the embellishments */
    final private static class EmbellishmentSource {
        final private List<SourceEmbellishment> embellishments;
        Iterator<SourceEmbellishment> it;
        SourceEmbellishment current;

        /** construct embellishment source from a list of embellishments */
        EmbellishmentSource(List<SourceEmbellishment> embellishments) {
            this.embellishments = embellishments;
            it = this.embellishments.iterator();

            if (it.hasNext()) {
                current = it.next();
            } else {
                current = null;
            }
        }

        /** the current embellishment, or null if there are none left*/
        SourceEmbellishment current() {
            return current;
        }

        /** true if there are no more embellishments */
        boolean isEmpty() {
            return current == null;
        }

        /** advance to the next embellishment */
        SourceEmbellishment next() {
            if (it.hasNext()) {
                current = (SourceEmbellishment) it.next();
            } else {
                current = null;
            }
            return current;
        }
    }

    /**
     * Entry point for generating formatted CAL code from a source model
     * element. The text is appended to the supplied string builder.
     * 
     * @param element -
     *            the source model element
     * @param stringBuilder -
     *            the string builder to append the source to.
     * @param options -
     *            the CALFormatter options to use in generating the CAL source
     */
    final public static void formatCode(SourceModel.SourceElement element,
            StringBuilder stringBuilder, Options options,
            List<SourceEmbellishment> embellishments) {

        if (element == null) {
            throw new NullPointerException("Argument element cannot be null.");
        }
        if (stringBuilder == null) {
            throw new NullPointerException(
                    "Argument stringBuilder cannot be null.");
        }
        if (options == null) {
            throw new NullPointerException("Argument options cannot be null.");
        }
        if (embellishments == null) {
            throw new NullPointerException(
                    "Argument embellishments cannot be null.");
        }

        SourceTextNodeBuilder f = new SourceTextNodeBuilder(new EmbellishmentSource(embellishments));
        element.accept(f, options);
        
        SourceTextNode node = f.stack.peek();
        node.toFormattedText(stringBuilder, 0, 0, options);
    }

    /**
     * Formats code at specified indent level.
     * 
     * @param element
     *            source element to format
     * @param stringBuilder
     *            buffer to write formatted code to
     * @param options
     *            options for formatting
     * @param embellishments
     *            embellishments to include
     * @param indentLevel
     *            the indent level to start at
     */
    final static void formatCode(SourceModel.SourceElement element,
            StringBuilder stringBuilder, Options options,
            List<SourceEmbellishment> embellishments, int indentLevel) {

        if (element == null) {
            throw new NullPointerException("Argument element cannot be null.");
        }
        if (stringBuilder == null) {
            throw new NullPointerException(
                    "Argument stringBuilder cannot be null.");
        }
        if (options == null) {
            throw new NullPointerException("Argument options cannot be null.");
        }
        if (embellishments == null) {
            throw new NullPointerException(
                    "Argument embellishments cannot be null.");
        }

        if (indentLevel < 0) {
            throw new IllegalArgumentException("Indent level must be >= 0");
        }

        SourceTextNodeBuilder f = new SourceTextNodeBuilder(new EmbellishmentSource(embellishments));
        element.accept(f, options);
        
        SourceTextNode node = f.stack.peek();
        node.toFormattedText(stringBuilder, indentLevel / options.tabWidth, 0, options);
       
    }

    /**
     * Entry point for generating formatted CAL code from a source model
     * element, returns the formated code.
     * 
     * @param element -
     *            the source model element
     * @param options -
     *            the CALFormatter options to use in generating the CAL source
     * @return a String of CAL source
     */
    final public static String formatCode(SourceModel.SourceElement element,
            Options options, List<SourceEmbellishment> embellishments) {

        StringBuilder sb = new StringBuilder();

        if (options.getKeepParen()) {
            formatCode(element, sb, options, embellishments);
        } else {
            formatCode(element.accept(new SourceModelParenStripper(), null),
                    sb, options, embellishments);
        }
        return sb.toString().replaceAll(" +" + EOL, EOL);
    }

    /**
     * Entry point for generating formatted CAL code from a source model
     * element, returns the formated code. The code is formatted at the 
     * specified indent level - this is useful for formatting parts of
     * a module.
     * 
     * @param element
     *            the source model element
     * @param options
     *            the CALFormatter options to use in generating the CAL source
     * @return a String of CAL source
     */
    public static String formatCode(SourceModel.SourceElement element,
            Options options, List<SourceEmbellishment> embellishments,
            int indentLevel) {

        StringBuilder sb = new StringBuilder();

        if (options.getKeepParen()) {
            formatCode(element, sb, options, embellishments, indentLevel);
        } else {
            formatCode(element.accept(new SourceModelParenStripper(), null),
                    sb, options, embellishments, indentLevel);
        }
        return sb.toString().replaceAll(" +" + EOL, EOL);
    }

    /**
     * Emit an indent where the indent level is directly specified.
     * 
     * @param sb
     * @param indentLevel
     * @param options
     * @return StringBuilder sb, returned for convenience.
     */
    private static StringBuilder emitIndent(StringBuilder sb, int indentLevel,
            Options options) {
        for (int i = 0; i < indentLevel; i++) {
            sb.append(options.useSpacesForTabs ? options.indentString : "\t");
        }
        return sb;
    }

    /**
     * Emit a line feed. This compresses the vertical space so that there can
     * not be more than one consecutive blank lines.
     * 
     * @param sb
     *            the StringBuilder to which to add the EOL.
     */
    private static void emitEOL(StringBuilder sb) {
        int lastIndex = sb.length();

        if (sb.length() < EOL.length() * 2
                || !sb.subSequence(lastIndex - EOL.length() * 2, lastIndex)
                        .equals(EOL + EOL)) {
            sb.append(EOL);
        }
    }

    /**
     * Emit an indent, some text, and an EOL.
     * 
     * @param sb
     *            the StringBuilder to which to add an indent.
     * @param indent
     *            the number of indents to add.
     * @param options
     * @param text
     *            the text to add.
     */
    private static void emitIndentedText(StringBuilder sb, int indent,
            String text, Options options) {
        emitIndent(sb, indent, options);
        sb.append(text + EOL);
    }

    /**
     * The options builder is used to build formating options
     * 
     * @author Magnus Byne
     */
    public static final class OptionsBuilder {

        /** the line length for the Options that are built */
        private int lineLength = 80;

        private boolean keepParen = true;

        /**
         * If this is true non-CALDoc comments will be wrapped
         */
        private boolean wordWrapNonCalDocComments = true;

        /** create default options builder */
        public OptionsBuilder() {
        }

        /**
         * Set the line width. In general formatted source will not exceed this
         * amount, but certain comments and quoted strings, very long
         * identifiers and very heavily nested code may do so
         */
        final public OptionsBuilder withLineLength(int lineWidth) {
            this.lineLength = lineWidth;
            return this;
        }

        /**
         * If keepParen is true all the parentheses from the original source
         * will be preserved. Otherwise the formatted code will contain the
         * minimum necessary.
         */
        final public OptionsBuilder withKeepParentheses(boolean keep) {
            keepParen = keep;
            return this;
        }

        /** if this is true non CALDoc comments will be 
         * formatted to fit the line length. Note that CALDoc comments are always 
         * formatted. */
        final public OptionsBuilder withFormatComments(boolean format) {
            wordWrapNonCalDocComments = format;
            return this;
        }

        /** create the new options */
        final public Options build() {
            return new Options(lineLength, keepParen, wordWrapNonCalDocComments);
        }
    }

    /**
     * The settings used to generate formatted CAL source code from a source
     * model instance.
     * 
     * @author rcypher
     * 
     */
    public static final class Options {

        /**
         * Maximum number of columns to print before wrapping lines. In some
         * cases lines may exceed this, for example comments are not currently
         * wrapped, and quoted strings are not broken, identifier names could
         * exceed the line length, etc
         */
        final private int maxLineLength;

        /**
         * When splitting a binary operator expression across two lines place
         * the operator at the beginning of the second line.
         */
        final private boolean operatorsAtBeginningOfLine = true;

        /**
         * When this is true all of the parens from the original source
         * expressions will be preserved. If it is is false the minimum number
         * of parens will appear in the formated source.
         */
        final private boolean keepParen;

        /**
         * If true than the using clauses in an import statement will be grouped
         * so that there is only a single clause for each type, i.e. function,
         * typeclass, etc.
         */
        final private boolean groupImportUsingClauses = false;

        /**
         * this controls the maximum bracket nesting before a line will be split
         */
        final private int maxNestingDepthOnOneLine = 4;

        /**
         * A set of functions that should appear on their own line when used in
         * infix notation - typically this just includes seq
         */
        final private Set<String> infixFunctionsOnSeperateLine = Collections
                .singleton(CAL_Prelude.Functions.seq.getUnqualifiedName());

        /**
         * If true than the items in a using clause will be sorted
         * alphabetically.
         */
        final private boolean sortImportUsingClauseItems = false;

        /**
         * If true spaces will be used for indenting otherwise tab characters.
         */
        final private boolean useSpacesForTabs = true;

        /**
         * Width of an individual tab.
         */
        final private int tabWidth = 4;

        /**
         * String used for putting tabs at the beginning of a line of code.
         */
        final private String indentString = "    ";

        /** if this is true non caldoc comments will be word wrapped */
        final private boolean wordWrapNonCalDocComments;

        /**
         * Create an options instance with specified line width and keepParen
         */
        private Options(int lineWidth, boolean keepParen,
                boolean wordWrapNonCalDocComments) {
            maxLineLength = lineWidth;
            this.keepParen = keepParen;
            this.wordWrapNonCalDocComments = wordWrapNonCalDocComments;
        }

        /**
         * Creates a new options with restricted max columns
         * this is used for nested formatting - e.g. formatting
         * caldoc within a separate formatter.
         * @param maxColumns
         * @return the options with new max line length
         */
        Options withRestrictedLineLength(int maxColumns) {
            if (maxColumns >= maxLineLength || maxColumns < 1) {
                throw new IllegalArgumentException("The maxColumns parameter is invalid");
            }
            return new Options(maxColumns, keepParen, wordWrapNonCalDocComments);
        }
        
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Options:" + EOL);
            sb.append("maxColumns=" + maxLineLength + EOL);
            sb.append("tabWidth=" + tabWidth + EOL);
            sb.append("useSpacesForTabs=" + useSpacesForTabs + EOL);
            sb.append("sortImportUsingClauseItems="
                    + sortImportUsingClauseItems + EOL);
            sb.append("operatorsAtBeginningOfLine="
                    + operatorsAtBeginningOfLine + EOL);
            sb.append("groupImportUsingClauses=" + groupImportUsingClauses
                    + EOL);
            sb.append("infixFunctions formated on separate lines="
                    + infixFunctionsOnSeperateLine + EOL);
            sb.append("maxNestingDepthOnOneLine=" + maxNestingDepthOnOneLine
                    + EOL);
            sb.append("keepParen=" + keepParen + EOL);
            sb.append("wordWrapNonCalDocComments=" + wordWrapNonCalDocComments
                    + EOL);
            return sb.toString();
        }

        /**
         * @return the groupImportUsingClauses
         */
        public boolean isGroupImportUsingClauses() {
            return groupImportUsingClauses;
        }

        /**
         * @return the tabWidth
         */
        public int getTabWidth() {
            return tabWidth;
        }

        /**
         * @return keepParen
         */
        public boolean getKeepParen() {
            return keepParen;
        }

        /** true iff non cal doc comments should be wrapped */
        public boolean getWordWrapNonCalDocComments() {
            return wordWrapNonCalDocComments;
        }

        /**
         * true iff the given function should appear on its own line, this is
         * the convention for seq
         * 
         * @param function
         * @return true iff the given function should appear on its own line
         */
        public boolean putInfixFunctionOnSeperateLine(String function) {
            return infixFunctionsOnSeperateLine.contains(function);
        }

        /** return the string used for indenting*/
        public String getIndentString() {
            return indentString;
        }
        
        /**
         * Get the maximum number of columns before wrapping a line. NOTE: Some
         * source model elements may ignore this.
         * 
         * @return the maxColumns
         */
        public int getMaxColumns() {
            return maxLineLength;
        }

        /**
         * Returns true if the setting indicates that when splitting a binary
         * operator expression the operator should appear at the beginning of
         * the second line.
         * 
         * @return the operatorsAtBeginningOfLine
         */
        public boolean isOperatorsAtBeginningOfLine() {
            return operatorsAtBeginningOfLine;
        }

        /**
         * Get the value of the flag indicating that items in an import using
         * clause should be sorted alphabetically.
         * 
         * @return the sortImportUsingClauseItems
         */
        public boolean isSortImportUsingClauseItems() {
            return sortImportUsingClauseItems;
        }
    }

    /**
     * This class is used to build up a tree of SourceTextNode instances from a
     * source model element. The SourceTextNode tree can then be used to
     * generate formatted source where the formating/layout varies based on the
     * context in which the source occurs.
     * 
     * @author rcypher
     * 
     */
    public static final class SourceTextNodeBuilder extends
            SourceModelTraverser<Options, Void> {

        /**
         * Stack used to keep track of the 'current' node.
         */
        private final ArrayStack<SourceTextNode> stack = ArrayStack.make();

        /**
         * the embellishments used to augment the source model
         */
        private final EmbellishmentSource embellishments;

        /**
         * Create a new SourceTextNodeBuilder which will use the supplied
         * options.
         */
        SourceTextNodeBuilder(EmbellishmentSource embellishments) {
            stack.push(new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL));
            this.embellishments = embellishments;
        }

        /**
         * This adds a new node as a child of the node on the top of the stack.
         * It checks for embellishments and adds extra nodes if needed to
         * include them.
         * 
         * Embellishments are not stored in the source model as it is hard to
         * associate them with particular elements. For example comments are
         * often used as section breaks.
         * 
         * @param element
         * @param node
         * @return the original SourceTextNode that was passed
         */
        private SourceTextNode addNodeForSourceElementWithEmbellishments(
                SourceElement element, SourceTextNode node) {
            return addNodeForSourceElementWithEmbellishments(element
                    .getSourceRange(), node);
        }

        /**
         * This adds a new node as a child of the node on the top of the stack.
         * It checks for embellishments and adds extra nodes if needed to
         * include them.
         * 
         * Embellishments are not stored in the source model as it is hard to
         * associate them with particular elements. For example comments are
         * often used as section breaks.
         * 
         * @param range
         *            of the source element to embellish
         * @param node
         * @return the original SourceTextNode that was passed
         */
        private SourceTextNode addNodeForSourceElementWithEmbellishments(
                SourceRange range, SourceTextNode node) {

            // is there a comment that appears before this node
            SourceTextNode embellishmentNode = null;

            // add embellishments that come before this source element
            while (!embellishments.isEmpty()
                    && range != null
                    && embellishments.current().getSourceRange().getStartLine() < range
                            .getStartLine()) {

                if (embellishments.current() instanceof SourceEmbellishment.BlankLine) {
                    if (embellishmentNode == null) {
                        embellishmentNode = new SourceTextNode(
                                "",
                                SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
                        embellishmentNode.setForcedBreak();
                    }
                    embellishmentNode.addChild(new SourceTextNode("",
                            SourceTextNode.FormatStyle.EMBELLISHMENT));

                } else {
                    if (embellishmentNode == null) {
                        embellishmentNode = new SourceTextNode(
                                SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
                        embellishmentNode.setForcedBreak();
                    }
                    embellishmentNode.addChild(new SourceTextNode(
                            embellishments.current().getText().replaceAll(
                                    "(\r\n)|\n|\r", EOL),
                            SourceTextNode.FormatStyle.EMBELLISHMENT));
                }
                embellishments.next();
            }

            // add embellishments that come on the same line as the source
            // element
            if (!embellishments.isEmpty()
                    && (embellishments.current() instanceof SourceEmbellishment.SingleLineComment || embellishments
                            .current() instanceof SourceEmbellishment.MultiLineComment)
                    && range != null
                    && embellishments.current().getSourceRange().getStartLine() == range
                            .getStartLine()) {

                SourceTextNode top = new SourceTextNode(
                        SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
                top.setForcedBreak();
                
                top.addChild(new SourceTextNode(
                        SourceTextNode.FormatStyle.CODE_FOLLOWED_BY_COMMENT));
                top.getNthChild(0).addChild(node);
                top.getNthChild(0).addChild(
                        new SourceTextNode(embellishments.current().getText()
                                .replaceAll("(\r\n)|\n|\r", EOL),
                                SourceTextNode.FormatStyle.EMBELLISHMENT));
                embellishments.next();

                node = top;
            }

            if (embellishmentNode == null) {
                stack.peek().addChild(node);
            } else {
                SourceTextNode newRoot = new SourceTextNode(
                        SourceTextNode.FormatStyle.COMMENTS_FOLLOWED_BY_CODE);
                ((SourceTextNode)stack.peek()).addChild(newRoot);
                newRoot.addChild(embellishmentNode);
                newRoot.addChild(node);
            }
            return node;
        }

        @Override
        protected Void visit_CALDoc_Comment_Helper(CALDoc.Comment comment,
                Options options) {

            formatCALDocComment(comment);
            return null;
        }

        @Override
        public Void visit_Expr_Parenthesized(Expr.Parenthesized parenthesized,
                Options options) {

            SourceTextNode node = new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
            addNodeForSourceElementWithEmbellishments(parenthesized, node);
            stack.push(node);

            parenthesized.getExpression().accept(this, options);

            parenthesizeExpression(node.getNthChild(0));

            stack.pop();

            return null;
        }

        @Override
        public Void visit_Expr_Application(Expr.Application application,
                Options options) {

            SourceTextNode node = new SourceTextNode(
                    SourceTextNode.FormatStyle.APPLICATION);

            addNodeForSourceElementWithEmbellishments(application, node);
            stack.push(node);

            // If the leftmost expression is an application we want to flatten
            // it. This is
            // because we want to treat '(compose a b) c' as 'compose a b c'.
            List<Expr> children = new ArrayList<Expr>();
            for (int i = 0, nExprs = application.getNExpressions(); i < nExprs; ++i) {
                children.add(application.getNthExpression(i));
            }

            for (Expr child0 = children.get(0); child0 instanceof Expr.Application;) {
                children.remove(0);
                Expr.Application childApp = (Expr.Application) child0;
                for (int i = 0, n = childApp.getNExpressions(); i < n; ++i) {
                    children.add(i, childApp.getNthExpression(i));
                }

                child0 = children.get(0);
            }

            for (int i = 0, nExprs = children.size(); i < nExprs; ++i) {
                Expr expr = children.get(i);

                expr.accept(this, options);
                if (expr.precedenceLevel() <= application.precedenceLevel()
                        && (i != 0 || expr.precedenceLevel() != application
                                .precedenceLevel())) {
                    parenthesizeExpression(node
                            .getNthChild(node.getNChildren() - 1));
                }
            }

            stack.pop();

            return null;
        }

        @Override
        public Void visit_Expr_DataCons(Expr.DataCons cons, Options options) {

            return super.visit_Expr_DataCons(cons, options);
        }

        @Override
        public Void visit_Expr_Var(Expr.Var var, Options options) {

            SourceModel.verifyArg(var, "var");

            var.getVarName().accept(this, options);
            return null;
        }

        @Override
        public Void visit_Expr_Let(Expr.Let let, Options options) {

            SourceTextNode root = new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
            root.setForcedBreak();
            addNodeForSourceElementWithEmbellishments(let, root);

            SourceTextNode letNode = new SourceTextNode(
                    "let",
                    SourceTextNode.FormatStyle.CHILDREN_IN_ONE_LEVEL);
            letNode.setForcedBreak();
            root.addChild(letNode);
            stack.push(letNode);

            final int nLocalDefns = let.getNLocalDefinitions();
            for (int i = 0; i < nLocalDefns; i++) {
                LocalDefn localDefn = let.getNthLocalDefinition(i);

                localDefn.accept(this, options);
            }

            stack.pop();

            SourceTextNode inNode = new SourceTextNode(
                    "in",
                    SourceTextNode.FormatStyle.CHILDREN_IN_ONE_LEVEL);
            inNode.setForcedBreak();
            root.addChild(inNode);
            stack.push(inNode);

            let.getInExpr().accept(this, options);

            stack.pop();

            return null;
        }
        @Override
        public Void visit_Expr_Case(Expr.Case caseExpr, Options options) {

            SourceTextNode rootNode = new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
            rootNode.setForcedBreak();
            addNodeForSourceElementWithEmbellishments(caseExpr, rootNode);
            stack.push(rootNode);

            SourceTextNode casePart = new SourceTextNode(
                    SourceTextNode.FormatStyle.ALTERNATE_CHILDREN);

            rootNode.addChild(casePart);
            stack.push(casePart);
            casePart.addChild(new SourceTextNode("case",
                    SourceTextNode.FormatStyle.ATOMIC));
            caseExpr.getConditionExpr().accept(this, options);
            casePart.addChild(new SourceTextNode("of",
                    SourceTextNode.FormatStyle.ATOMIC));
            stack.pop();

            final int nCaseAlts = caseExpr.getNCaseAlts();
            for (int i = 0; i < nCaseAlts; i++) {
                caseExpr.getNthCaseAlt(i).accept(this, options);
            }

            stack.pop();

            return null;
        }

        @Override
        public Void visit_Expr_Case_Alt_Default(
                Expr.Case.Alt.Default defaultAlt, Options options) {

            SourceTextNode rootNode = new SourceTextNode("_ ->",
                    SourceTextNode.FormatStyle.CHILDREN_IN_ONE_LEVEL);
            addNodeForSourceElementWithEmbellishments(defaultAlt, rootNode);

            SourceTextNode altExprRoot = new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
            rootNode.addChild(altExprRoot);
            stack.push(altExprRoot);

            super.visit_Expr_Case_Alt_Default(defaultAlt, options);

            altExprRoot.addChild(new SourceTextNode(";",
                    SourceTextNode.FormatStyle.ATOMIC));

            stack.pop();

            return null;
        }

        /**
         * Adds specified parens to a source text node.
         * @param exprRoot the root node of the source to be parenthesized
         */
        private void parenthesizeExpression(SourceTextNode exprRoot) {
            parenthesizeExpression(exprRoot, "(", ")");
        }

        /**
         * Adds specified parens to a source text node.
         * @param exprRoot the root node of the source to be parenthesized
         * @param open the opening paren
         * @param close the closing paren
         */
        private void parenthesizeExpression(SourceTextNode exprRoot,
                String open, String close) {
            // we have a special case for applications, so that the function
            // name is placed on the same line as the open paren
            if (exprRoot.formatStyle == SourceTextNode.FormatStyle.APPLICATION
                    && !exprRoot.startsWithBracket()) {
                exprRoot.prefixText(open);
                exprRoot.addChild(new SourceTextNode(close,
                        SourceTextNode.FormatStyle.ATOMIC));
                exprRoot.formatStyle = SourceTextNode.FormatStyle.INNER_CHILDREN_IN_ONE_LEVEL;

            } else {
                SourceTextNode newExprRoot2 = new SourceTextNode(exprRoot
                        .getThisText(), exprRoot.getFormatType());
                newExprRoot2.forcedBreak = exprRoot.forcedBreak;
                exprRoot.forcedBreak = false;

                for (int i = 0, n = exprRoot.getNChildren(); i < n; ++i) {
                    newExprRoot2.addChild(exprRoot.getNthChild(i));
                }

                while (exprRoot.getNChildren() > 0) {
                    exprRoot.removeChild(0);
                }

                exprRoot.myText = "";
                exprRoot.addChild(new SourceTextNode(open,
                        SourceTextNode.FormatStyle.ATOMIC));
                exprRoot.addChild(newExprRoot2);
                exprRoot.addChild(new SourceTextNode(close,
                        SourceTextNode.FormatStyle.ATOMIC));

                exprRoot.formatStyle = SourceTextNode.FormatStyle.INNER_CHILDREN_IN_ONE_LEVEL;
            }
        }

        @Override
        public Void visit_Expr_Case_Alt_UnpackTuple(
                Expr.Case.Alt.UnpackTuple tuple, Options options) {

            SourceTextNode rootNode = new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
            addNodeForSourceElementWithEmbellishments(tuple, rootNode);
            stack.push(rootNode);

            SourceTextNode tupleRoot = new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
            rootNode.addChild(tupleRoot);
            stack.push(tupleRoot);

            for (int i = 0, nPatterns = tuple.getNPatterns(); i < nPatterns; i++) {
                tuple.getNthPattern(i).accept(this, options);
            }

            stack.pop();

            for (int i = 0, n = tupleRoot.getNChildren() - 1; i < n; ++i) {
                tupleRoot.getNthChild(i).suffixText(",");
            }
            parenthesizeExpression(tupleRoot);

            SourceTextNode arrow = new SourceTextNode("->",
                    SourceTextNode.FormatStyle.CHILDREN_IN_ONE_LEVEL);
            rootNode.addChild(arrow);

            stack.push(arrow);
            tuple.getAltExpr().accept(this, options);
            stack.pop();

            arrow.addChild(new SourceTextNode(";",
                    SourceTextNode.FormatStyle.ATOMIC));

            stack.pop();

            return null;
        }

        @Override
        public Void visit_Expr_Case_Alt_UnpackUnit(
                Expr.Case.Alt.UnpackUnit unit, Options options) {

            SourceTextNode rootNode = new SourceTextNode("() ->",
                    SourceTextNode.FormatStyle.CHILDREN_IN_ONE_LEVEL);
            addNodeForSourceElementWithEmbellishments(unit, rootNode);

            SourceTextNode altExprRoot = new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
            rootNode.addChild(altExprRoot);
            stack.push(altExprRoot);

            super.visit_Expr_Case_Alt_UnpackUnit(unit, options);

            altExprRoot.addChild(new SourceTextNode(";",
                    SourceTextNode.FormatStyle.ATOMIC));

            stack.pop();

            return null;
        }

        @Override
        public Void visit_Expr_Case_Alt_UnpackDataCons(
                Expr.Case.Alt.UnpackDataCons cons, Options options) {

            SourceTextNode rootNode = new SourceTextNode(
                    SourceTextNode.FormatStyle.FIRST_CHILD_AT_LEVEL);
            addNodeForSourceElementWithEmbellishments(cons, rootNode);
            stack.push(rootNode);

            SourceTextNode dcRoot = new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
            rootNode.addChild(dcRoot);
            stack.push(dcRoot);

            SourceTextNode dcRoot2 = new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
            dcRoot.addChild(dcRoot2);
            stack.push(dcRoot2);

            for (int i = 0, nDataConsNames = cons.getNDataConsNames(); i < nDataConsNames; i++) {
                cons.getNthDataConsName(i).accept(this, options);
            }

            if (dcRoot2.getNChildren() > 1 || cons.getParenthesized()) {
                for (int i = 1, n = dcRoot2.getNChildren(); i < n; ++i) {
                    dcRoot2.getNthChild(i).prefixText("| ");
                }

                parenthesizeExpression(dcRoot2);
            }

            stack.pop();
            cons.getArgBindings().accept(this, options);
            stack.pop();

            // SourceTextNode arrow = new SourceTextNode("->",
            // SourceTextNode.FormatStyle.CHILDREN_IN_ONE_LEVEL);
            // rootNode.addChild(arrow);
            rootNode.suffixText(" ->");

            SourceTextNode exprRoot = new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
            rootNode.addChild(exprRoot);
            stack.push(exprRoot);
            cons.getAltExpr().accept(this, options);
            stack.pop();

            exprRoot.addChild(new SourceTextNode(";",
                    SourceTextNode.FormatStyle.ATOMIC));

            stack.pop();

            return null;
        }

        @Override
        public Void visit_ArgBindings_Matching(
                ArgBindings.Matching argBindings, Options options) {

            SourceTextNode root = new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
            addNodeForSourceElementWithEmbellishments(argBindings, root);

            stack.push(root);

            super.visit_ArgBindings_Matching(argBindings, options);

            for (int i = 0, n = root.getNChildren() - 1; i < n; ++i) {
                root.getNthChild(i).suffixText(",");
            }

            parenthesizeExpression(root, "{", "}");

            stack.pop();

            return null;
        }

        @Override
        public Void visit_ArgBindings_Positional(
                ArgBindings.Positional argBindings, Options options) {

            if (argBindings.getNPatterns() == 0) {
                return null;
            }

            SourceTextNode root = new SourceTextNode(
                    SourceTextNode.FormatStyle.CONCAT_CHILDREN_TO_LINE_END_INDENT_SECOND_LINE);
            addNodeForSourceElementWithEmbellishments(argBindings, root);
            stack.push(root);

            super.visit_ArgBindings_Positional(argBindings, options);

            stack.pop();

            return null;
        }

        @Override
        public Void visit_Expr_Case_Alt_UnpackInt(
                Expr.Case.Alt.UnpackInt intAlt, Options options) {

            SourceTextNode root = new SourceTextNode(
                    SourceTextNode.FormatStyle.FIRST_CHILD_AT_LEVEL);
            addNodeForSourceElementWithEmbellishments(intAlt, root);
            stack.push(root);

            BigInteger[] bigIntValues = intAlt.getIntValues();
            StringBuilder sb = new StringBuilder();
            if (bigIntValues.length == 1) {
                sb.append(bigIntValues[0]);
                sb.append(" ->");

            } else {
                sb.append('(');
                for (int i = 0; i < bigIntValues.length; i++) {
                    if (i > 0) {
                        sb.append(" | ");
                    }
                    sb.append(bigIntValues[i]);
                }

                sb.append(") ->");
            }

            root.addChild(new SourceTextNode(sb.toString(),
                    SourceTextNode.FormatStyle.ATOMIC));

            SourceTextNode exprRoot = new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
            root.addChild(exprRoot);
            stack.push(exprRoot);

            super.visit_Expr_Case_Alt_UnpackInt(intAlt, options);
            exprRoot.addChild(new SourceTextNode(";",
                    SourceTextNode.FormatStyle.ATOMIC));

            stack.pop();
            stack.pop();

            return null;
        }

        @Override
        public Void visit_Expr_Case_Alt_UnpackChar(
                Expr.Case.Alt.UnpackChar charAlt, Options options) {

            SourceTextNode root = new SourceTextNode(
                    SourceTextNode.FormatStyle.FIRST_CHILD_AT_LEVEL);
            addNodeForSourceElementWithEmbellishments(charAlt, root);
            stack.push(root);

            StringBuilder sb = new StringBuilder();
            char[] charValues = charAlt.getCharValues();
            if (charValues.length == 1) {
                sb.append(StringEncoder.encodeChar(charValues[0]));
                sb.append(" ->");

            } else {
                sb.append('(');
                for (int i = 0; i < charValues.length; i++) {
                    if (i > 0) {
                        sb.append(" | ");
                    }
                    sb.append(StringEncoder.encodeChar(charValues[i]));
                }

                sb.append(") ->");
            }

            root.addChild(new SourceTextNode(sb.toString(),
                    SourceTextNode.FormatStyle.ATOMIC));

            SourceTextNode exprRoot = new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
            root.addChild(exprRoot);
            stack.push(exprRoot);

            super.visit_Expr_Case_Alt_UnpackChar(charAlt, options);
            exprRoot.addChild(new SourceTextNode(";",
                    SourceTextNode.FormatStyle.ATOMIC));

            stack.pop();
            stack.pop();

            return null;
        }

        @Override
        public Void visit_Expr_Case_Alt_UnpackListCons(
                Expr.Case.Alt.UnpackListCons cons, Options options) {

            // Root node for the case alt
            SourceTextNode root = new SourceTextNode(
                    SourceTextNode.FormatStyle.FIRST_CHILD_AT_LEVEL);
            addNodeForSourceElementWithEmbellishments(cons, root);
            stack.push(root);

            // Root node for the list left hand side. i.e. x : xs ->
            SourceTextNode listRoot = new SourceTextNode(
                    SourceTextNode.FormatStyle.ATOMIC);
            root.addChild(listRoot);
            stack.push(listRoot);

            cons.getHeadPattern().accept(this, options);
            cons.getTailPattern().accept(this, options);

            stack.pop();
            listRoot.getNthChild(0).suffixText(" :");
            listRoot.getNthChild(1).suffixText(" ->");

            // Root for the expression.
            SourceTextNode exprRoot = new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
            root.addChild(exprRoot);
            stack.push(exprRoot);

            cons.getAltExpr().accept(this, options);
            exprRoot.addChild(new SourceTextNode(";",
                    SourceTextNode.FormatStyle.ATOMIC));
            stack.pop();

            stack.pop();

            return null;
        }

        @Override
        public Void visit_Expr_Case_Alt_UnpackListNil(
                Expr.Case.Alt.UnpackListNil nil, Options options) {

            SourceTextNode rootNode = new SourceTextNode("[] ->",
                    SourceTextNode.FormatStyle.CHILDREN_IN_ONE_LEVEL);
            addNodeForSourceElementWithEmbellishments(nil, rootNode);

            SourceTextNode altExprRoot = new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
            rootNode.addChild(altExprRoot);
            stack.push(altExprRoot);

            super.visit_Expr_Case_Alt_UnpackListNil(nil, options);

            altExprRoot.addChild(new SourceTextNode(";",
                    SourceTextNode.FormatStyle.ATOMIC));

            stack.pop();
            return null;
        }

        @Override
        public Void visit_Expr_Case_Alt_UnpackRecord(
                Expr.Case.Alt.UnpackRecord record, Options options) {

            // Root node for the case alt
            SourceTextNode root = new SourceTextNode(
                    SourceTextNode.FormatStyle.FIRST_CHILD_AT_LEVEL);
            addNodeForSourceElementWithEmbellishments(record, root);
            stack.push(root);

            // Root for the record. i.e. { s | x = a, y = b} ->
            SourceTextNode recordRoot = new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
            root.addChild(recordRoot);
            stack.push(recordRoot);

            if (record.getBaseRecordPattern() != null) {
                record.getBaseRecordPattern().accept(this, options);
                recordRoot.suffixText(" | ");
            }

            final int nFieldPatterns = record.getNFieldPatterns();
            for (int i = 0; i < nFieldPatterns; i++) {
                record.getNthFieldPattern(i).accept(this, options);
                if (i < nFieldPatterns - 1) {
                    recordRoot.getNthChild(recordRoot.getNChildren() - 1)
                            .suffixText(",");
                }
            }

            stack.pop();
            parenthesizeExpression(recordRoot, "{", "}");
            recordRoot.suffixText(" ->");

            // Root node for the Alt expr.
            SourceTextNode exprRoot = new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
            root.addChild(exprRoot);
            stack.push(exprRoot);

            record.getAltExpr().accept(this, options);
            exprRoot.addChild(new SourceTextNode(";",
                    SourceTextNode.FormatStyle.ATOMIC));
            stack.pop();

            stack.pop();

            return null;
        }

        @Override
        public Void visit_FieldPattern(FieldPattern pattern, Options options) {

            SourceTextNode root = new SourceTextNode(getFieldNameString(pattern.getFieldName()), SourceTextNode.FormatStyle.ATOMIC);
            addNodeForSourceElementWithEmbellishments(pattern, root);
            stack.push(root);

            super.visit_FieldPattern(pattern, options);

            stack.pop();

            if (root.getNChildren() > 0) {
                root.getNthChild(0).prefixText("= ");
            }

            return null;
        }

        @Override
        public Void visit_Expr_Lambda(Expr.Lambda lambda, Options options) {

            SourceTextNode root = new SourceTextNode(
                    SourceTextNode.FormatStyle.FIRST_CHILD_AT_LEVEL);
            addNodeForSourceElementWithEmbellishments(lambda, root);
            stack.push(root);

            SourceTextNode argRoot = new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
            root.addChild(argRoot);
            stack.push(argRoot);

            for (int i = 0, nParameters = lambda.getNParameters(); i < nParameters; i++) {
                lambda.getNthParameter(i).accept(this, options);
            }

            stack.pop();
            argRoot.prefixText("\\");
            argRoot.suffixText(" ->");

            SourceTextNode exprRoot = new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
            root.addChild(exprRoot);
            stack.push(exprRoot);
            lambda.getDefiningExpr().accept(this, options);
            stack.pop();

            // pop root
            stack.pop();

            return null;
        }

        @Override
        public Void visit_Expr_If(Expr.If ifExpr, Options options) {

            SourceTextNode root = new SourceTextNode(
                    SourceTextNode.FormatStyle.ALTERNATE_CHILDREN);
            root.setForcedBreak();
            
            addNodeForSourceElementWithEmbellishments(ifExpr, root);
            stack.push(root);

            SourceTextNode ifPart = new SourceTextNode(
                    SourceTextNode.FormatStyle.ALTERNATE_CHILDREN);

            root.addChild(ifPart);
            stack.push(ifPart);
            ifPart.addChild(new SourceTextNode("if",
                    SourceTextNode.FormatStyle.ATOMIC));
            ifExpr.getConditionExpr().accept(this, options);
            ifPart.addChild(new SourceTextNode("then",
                    SourceTextNode.FormatStyle.ATOMIC));
            stack.pop();

            ifExpr.getThenExpr().accept(this, options);

            boolean elsePartIsIf = ifExpr.getElseExpr() instanceof Expr.If;
            if (!elsePartIsIf) {
                root.addChild(new SourceTextNode("else",
                        SourceTextNode.FormatStyle.ATOMIC));
            }

            ifExpr.getElseExpr().accept(this, options);

            if (elsePartIsIf) {
                root.getNthChild(root.getNChildren() - 1).prefixText("else ");
            }

            stack.pop();

            return null;
        }

        @Override
        public Void visit_Expr_Literal_Num(Expr.Literal.Num num, Options options) {

            addNodeForSourceElementWithEmbellishments(num, new SourceTextNode(
                    num.getNumValue().toString(),
                    SourceTextNode.FormatStyle.ATOMIC));

            return null;
        }

        @Override
        public Void visit_Expr_Literal_Double(
                Expr.Literal.Double doubleLiteral, Options options) {

            SourceModel.verifyArg(doubleLiteral, "doubleLiteral");

            String valueString;

            if (java.lang.Double.isNaN(doubleLiteral.getDoubleValue())) {
                valueString = CAL_Prelude.Functions.isNotANumber
                        .getQualifiedName();
            } else if (doubleLiteral.getDoubleValue() == java.lang.Double.POSITIVE_INFINITY) {
                valueString = CAL_Prelude.Functions.positiveInfinity
                        .getQualifiedName();
            } else if (doubleLiteral.getDoubleValue() == java.lang.Double.NEGATIVE_INFINITY) {
                valueString = CAL_Prelude.Functions.negativeInfinity
                        .getQualifiedName();
            } else {
                valueString = java.lang.Double.toString(doubleLiteral
                        .getDoubleValue());
            }

            addNodeForSourceElementWithEmbellishments(doubleLiteral,
                    new SourceTextNode(valueString,
                            SourceTextNode.FormatStyle.ATOMIC));
            return null;
        }

        @Override
        public Void visit_Expr_Literal_Float(Expr.Literal.Float floatLiteral,
                Options options) {

            SourceModel.verifyArg(floatLiteral, "floatLiteral");

            StringBuilder sb = new StringBuilder();

            sb.append("(").append(
                    CAL_Prelude.Functions.toFloat.getQualifiedName()).append(
                    " ");

            if (java.lang.Double.isNaN(floatLiteral.getFloatValue())) {
                sb
                        .append(CAL_Prelude.Functions.isNotANumber
                                .getQualifiedName());
            } else if (floatLiteral.getFloatValue() == java.lang.Double.POSITIVE_INFINITY) {
                sb.append(CAL_Prelude.Functions.positiveInfinity
                        .getQualifiedName());
            } else if (floatLiteral.getFloatValue() == java.lang.Double.NEGATIVE_INFINITY) {
                sb.append(CAL_Prelude.Functions.negativeInfinity
                        .getQualifiedName());
            } else if (floatLiteral.getFloatValue() < 0) {
                sb.append("(")
                        .append(
                                java.lang.Double.toString(floatLiteral
                                        .getFloatValue())).append(")");
            } else {
                sb.append(java.lang.Double.toString(floatLiteral
                        .getFloatValue()));
            }

            sb.append(")");

            addNodeForSourceElementWithEmbellishments(floatLiteral,
                    new SourceTextNode(sb.toString(),
                            SourceTextNode.FormatStyle.ATOMIC));

            return null;
        }

        @Override
        public Void visit_Expr_Literal_Char(Expr.Literal.Char charLiteral,
                Options options) {

            SourceModel.verifyArg(charLiteral, "charLiteral");

            addNodeForSourceElementWithEmbellishments(
                    charLiteral,
                    new SourceTextNode(StringEncoder.encodeChar(charLiteral
                            .getCharValue()), SourceTextNode.FormatStyle.ATOMIC));

            return null;
        }

        @Override
        public Void visit_Expr_Literal_StringLit(Expr.Literal.StringLit string,
                Options options) {

            SourceModel.verifyArg(string, "string");

            addNodeForSourceElementWithEmbellishments(string,
                    new SourceTextNode(StringEncoder.encodeString(string
                            .getStringValue()),
                            SourceTextNode.FormatStyle.ATOMIC));

            return null;
        }

        @Override
        public Void visit_Expr_UnaryOp_Negate(Expr.UnaryOp.Negate negate,
                Options options) {

            SourceTextNode root = new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
            addNodeForSourceElementWithEmbellishments(negate, root);
            stack.push(root);

            super.visit_Expr_UnaryOp_Negate(negate, options);

            if (negate.getExpr().precedenceLevel() <= negate.precedenceLevel()) {
                parenthesizeExpression(root);
            }

            root.prefixText(negate.getOpText());

            stack.pop();

            return null;
        }

        @Override
        public Void visit_Expr_BinaryOp_BackquotedOperator_Var(
                Expr.BinaryOp.BackquotedOperator.Var backquotedOperator,
                Options options) {

            SourceTextNode root;
            if (options.putInfixFunctionOnSeperateLine(backquotedOperator
                    .getOperatorVarExpr().getVarName().getUnqualifiedName())) {
                root = new SourceTextNode(
                        SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
                root.setForcedBreak();
            } else {
                root = new SourceTextNode(
                        SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
            }
            // SourceTextNode
            addNodeForSourceElementWithEmbellishments(backquotedOperator, root);
            stack.push(root);

            SourceModel.verifyArg(backquotedOperator, "backquotedOperator");

            backquotedOperator.getLeftExpr().accept(this, options);
            backquotedOperator.getOperatorVarExpr().accept(this, options);
            backquotedOperator.getRightExpr().accept(this, options);

            stack.pop();

            if (!(backquotedOperator.getLeftExpr().precedenceLevel() > backquotedOperator
                    .precedenceLevel() || backquotedOperator.associativity() == Associativity.LEFT
                    && backquotedOperator.getLeftExpr().precedenceLevel() == backquotedOperator
                            .precedenceLevel())) {
                // parenthesize if neccessary
                parenthesizeExpression(root.getNthChild(0));
            }

            root.getNthChild(1).prefixText("`");
            root.getNthChild(1).suffixText("`");

            if (!(backquotedOperator.getRightExpr().precedenceLevel() > backquotedOperator
                    .precedenceLevel() || backquotedOperator.associativity() == Associativity.RIGHT
                    && backquotedOperator.getRightExpr().precedenceLevel() == backquotedOperator
                            .precedenceLevel())) {
                // parenthesize if neccessary
                parenthesizeExpression(root.getNthChild(2));
            }

            return null;
        }

        @Override
        public Void visit_Expr_BinaryOp_BackquotedOperator_DataCons(
                Expr.BinaryOp.BackquotedOperator.DataCons backquotedOperator,
                Options options) {

            SourceTextNode root = new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
            addNodeForSourceElementWithEmbellishments(backquotedOperator, root);
            stack.push(root);

            SourceModel.verifyArg(backquotedOperator, "backquotedOperator");

            backquotedOperator.getLeftExpr().accept(this, options);
            backquotedOperator.getOperatorDataConsExpr().accept(this, options);
            backquotedOperator.getRightExpr().accept(this, options);

            stack.pop();

            if (!(backquotedOperator.getLeftExpr().precedenceLevel() > backquotedOperator
                    .precedenceLevel() || backquotedOperator.associativity() == Associativity.LEFT
                    && backquotedOperator.getLeftExpr().precedenceLevel() == backquotedOperator
                            .precedenceLevel())) {
                // parenthesize if necessary
                parenthesizeExpression(root.getNthChild(0));
            }

            root.getNthChild(1).prefixText(" `");
            root.getNthChild(1).suffixText("` ");

            if (!(backquotedOperator.getRightExpr().precedenceLevel() > backquotedOperator
                    .precedenceLevel() || backquotedOperator.associativity() == Associativity.RIGHT
                    && backquotedOperator.getRightExpr().precedenceLevel() == backquotedOperator
                            .precedenceLevel())) {
                // parenthesize if not necessary
                parenthesizeExpression(root.getNthChild(2));
            }

            return null;
        }

        @Override
        protected Void visit_Expr_BinaryOp_Helper(Expr.BinaryOp binop,
                Options options) {

            SourceTextNode root;
            root = new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
            addNodeForSourceElementWithEmbellishments(binop, root);
            stack.push(root);

            SourceModel.verifyArg(binop, "binop");

            binop.getLeftExpr().accept(this, options);

            binop.getRightExpr().accept(this, options);

            stack.pop();

            if (!(binop.getLeftExpr().precedenceLevel() > binop
                    .precedenceLevel() || binop.associativity() == Associativity.LEFT
                    && binop.getLeftExpr().precedenceLevel() == binop
                            .precedenceLevel())) {
                // parenthesize if necessary
                parenthesizeExpression(root.getNthChild(0));
            }

            if (!(binop.getRightExpr().precedenceLevel() > binop
                    .precedenceLevel() || binop.associativity() == Associativity.RIGHT
                    && binop.getRightExpr().precedenceLevel() == binop
                            .precedenceLevel())) {
                // parenthesize if necessary
                parenthesizeExpression(root.getNthChild(1));
            }

            // indent left based on precedence
            if ((binop.getLeftExpr() instanceof Expr.BinaryOp && ((Expr.BinaryOp) binop
                    .getLeftExpr()).precedenceLevel() > binop.precedenceLevel())) {
                SourceTextNode node = root.getNthChild(0)
                        .getNodeWithoutComment();
                node.formatStyle = SourceTextNode.FormatStyle.FIRST_CHILD_AT_LEVEL;

                // SourceTextNode node= root.getNthChild(0);
                Expr.BinaryOp child = (Expr.BinaryOp) binop.getLeftExpr();
                while (child.getLeftExpr() instanceof Expr.BinaryOp
                        && ((Expr.BinaryOp) binop.getLeftExpr()).getOpText()
                                .equals(child.getOpText())) {

                    node = node.getNthChild(0).getNodeWithoutComment();
                    node.formatStyle = SourceTextNode.FormatStyle.FIRST_CHILD_AT_LEVEL;

                    child = (Expr.BinaryOp) child.getLeftExpr();
                }
            }

            // indent right based on precedence
            if ((binop.getRightExpr() instanceof Expr.BinaryOp && ((Expr.BinaryOp) binop
                    .getRightExpr()).precedenceLevel() > binop
                    .precedenceLevel())) {
                SourceTextNode node = root.getNthChild(1)
                        .getNodeWithoutComment();
                node.formatStyle = SourceTextNode.FormatStyle.FIRST_CHILD_AT_LEVEL;

                Expr.BinaryOp child = (Expr.BinaryOp) binop.getRightExpr();
                while (child.getLeftExpr() instanceof Expr.BinaryOp
                        && ((Expr.BinaryOp) binop.getRightExpr()).getOpText()
                                .equals(child.getOpText())) {

                    node = node.getNthChild(0).getNodeWithoutComment();
                    node.formatStyle = SourceTextNode.FormatStyle.FIRST_CHILD_AT_LEVEL;

                    child = (Expr.BinaryOp) child.getLeftExpr();
                }
            }

            if (options.isOperatorsAtBeginningOfLine()) {
                root.getNthChild(1).prefixText(binop.getOpText() + " ");
            } else {
                root.getNthChild(0).suffixText(" " + binop.getOpText());
            }

            // here we pack the right expression into the same node if it has
            // the same operator
            // ie if the expression is a && b && c, we pack the right expression
            // (b && c)
            // into the a && node so that if it is broken all parts will be on
            // separate lines
            // otherwise a could be have "a" on one line and "&& b && c" could
            // be an one. We do this packing
            // to ensure that if a is on it's own line then b and c will have
            // their own lines too
            if (binop.getRightExpr() instanceof Expr.BinaryOp
                    && ((Expr.BinaryOp) binop.getRightExpr()).getOpText()
                            .equals(binop.getOpText())) {
                SourceTextNode right = root.getNthChild(1);
                root.removeChild(1);

                if (right.myText.length() > 0) {
                    root.addChild(new SourceTextNode(right.myText,
                            SourceTextNode.FormatStyle.ATOMIC));
                }

                for (int i = 0, n = right.getNChildren(); i < n; i++) {
                    root.addChild(right.getNthChild(i));
                }
            }

            return null;
        }

        @Override
        public Void visit_Expr_Unit(Expr.Unit unit, Options options) {

            addNodeForSourceElementWithEmbellishments(unit, new SourceTextNode(
                    "()", SourceTextNode.FormatStyle.ATOMIC));

            return null;
        }

        @Override
        public Void visit_Expr_Tuple(Expr.Tuple tuple, Options options) {

            SourceTextNode root = new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
            addNodeForSourceElementWithEmbellishments(tuple, root);
            stack.push(root);

            super.visit_Expr_Tuple(tuple, options);

            for (int i = 0, n = root.getNChildren() - 1; i < n; ++i) {
                root.getNthChild(i).suffixText(",");
            }

            parenthesizeExpression(root);

            stack.pop();

            return null;
        }

        @Override
        public Void visit_Expr_List(Expr.List list, Options options) {

            SourceTextNode root = new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
            addNodeForSourceElementWithEmbellishments(list, root);
            stack.push(root);

            super.visit_Expr_List(list, options);

            stack.pop();

            for (int i = 0, n = root.getNChildren() - 1; i < n; i++) {
                root.getNthChild(i).suffixText(",");
            }

            parenthesizeExpression(root, "[", "]");
            return null;
        }

        @Override
        public Void visit_Expr_Record(Expr.Record record, Options options) {

            SourceTextNode root = new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
            addNodeForSourceElementWithEmbellishments(record, root);
            stack.push(root);

            SourceTextNode contentRoot = new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
            root.addChild(contentRoot);
            stack.push(contentRoot);

            super.visit_Expr_Record(record, options);

            int firstExtension = 0;
            if (record.getBaseRecordExpr() != null) {
                contentRoot.getNthChild(0).suffixText(" |");
                firstExtension = 1;
            }

            for (int i = firstExtension, n = contentRoot.getNChildren() - 1; i < n; i++) {
                contentRoot.getNthChild(i).suffixText(",");
            }

            parenthesizeExpression(contentRoot, "{", "}");

            stack.pop();

            stack.pop();

            return null;
        }

        @Override
        public Void visit_Expr_Record_FieldModification_Extension(
                Expr.Record.FieldModification.Extension fieldExtension,
                Options options) {

            SourceTextNode root = new SourceTextNode(getFieldNameString(fieldExtension.getFieldName())
                    + " =", SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
            addNodeForSourceElementWithEmbellishments(fieldExtension, root);
            stack.push(root);

            super.visit_Expr_Record_FieldModification_Extension(fieldExtension,
                    options);

            stack.pop();

            return null;
        }

        @Override
        public Void visit_Expr_Record_FieldModification_Update(
                Expr.Record.FieldModification.Update fieldValueUpdate,
                Options options) {

            SourceTextNode root = new SourceTextNode(getFieldNameString(fieldValueUpdate.getFieldName())
                    + " :=", SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
            addNodeForSourceElementWithEmbellishments(fieldValueUpdate, root);
            stack.push(root);

            super.visit_Expr_Record_FieldModification_Update(fieldValueUpdate,
                    options);

            stack.pop();
            return null;
        }

        @Override
        public Void visit_Expr_SelectDataConsField(
                Expr.SelectDataConsField field, Options options) {

            SourceTextNode root = new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
            addNodeForSourceElementWithEmbellishments(field, root);
            stack.push(root);

            super.visit_Expr_SelectDataConsField(field, options);

            stack.pop();

            if (field.getDataConsValuedExpr().precedenceLevel() < field
                    .precedenceLevel()) {
                // parenthesize if not necessary. We're using the fact that . is
                // left associative here to put < instead of <=.
                parenthesizeExpression(root.getNthChild(0));
            }

            // We need to put the dot after the expression for the data
            // instance.
            // i.e. the first dot in expression.DataConstructor.field
            // root.getNthChild(0).suffixText(".");
            // root.getNthChild(0).consolidateTextRight();
            // Now put in the .field part
            root.getNthChild(1).prefixText(".");
            root.getNthChild(1).suffixText(".");
            root.getNthChild(1).suffixText(
                getFieldNameString(field.getFieldName()));

            return null;
        }

        @Override
        public Void visit_Expr_SelectRecordField(Expr.SelectRecordField field,
                Options options) {

            SourceTextNode root = new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
            addNodeForSourceElementWithEmbellishments(field, root);
            stack.push(root);

            super.visit_Expr_SelectRecordField(field, options);

            stack.pop();

            // do not parenthesize if not necessary. We're using the fact that .
            // is left associative here to put < instead of <=.
            if (field.getRecordValuedExpr().precedenceLevel() < field
                    .precedenceLevel()) {
                parenthesizeExpression(root.getNthChild(0));
            }
            root.suffixText(".");
            root.suffixText(getFieldNameString(field.getFieldName()));

            return null;
        }

        /**
         * this wraps part of a caldoc comment with a trailing star
         * it emulates the old behaviour of formatting caldoc parts for
         * insertion by the renamer - ultimately the renamer could be changed
         * to reformat the entire comment.
         * @param element the element to format
         * @param options the options to use
         */
        private void visitCalDocPart(SourceElement element, Options options) {
           StringBuilder commentBuffer = new StringBuilder();
            
            SourceModelCALDocFormatter form = new SourceModelCALDocFormatter(commentBuffer);
            element.accept(form, options);

            SourceTextNode root = new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
            //root.setForcedBreak();
            addNodeForSourceElementWithEmbellishments(element, root);
            
            String[] lines = commentBuffer.toString().split(EOL);
            for(String line : lines) {
                root.addChild(SourceTextNode.makeAtmoic(line + EOL + " * "));
            }
            
        }
        
        @Override
        public Void visit_CALDoc_TaggedBlock_See_Function(
                CALDoc.TaggedBlock.See.Function function, Options options) {

            visitCalDocPart(function, options);
            return null;
        }
        
        @Override
        public Void visit_CALDoc_TaggedBlock_See_Module(
                CALDoc.TaggedBlock.See.Module module, Options options) {

            visitCalDocPart(module, options);
            return null;
        }

        @Override
        public Void visit_CALDoc_TaggedBlock_See_TypeCons(
                CALDoc.TaggedBlock.See.TypeCons typeCons, Options options) {

            visitCalDocPart(typeCons, options);
            return null;
        }

        @Override
        public Void visit_CALDoc_TaggedBlock_See_DataCons(
                CALDoc.TaggedBlock.See.DataCons dataCons, Options options) {

            visitCalDocPart(dataCons, options);
            return null;
        }

        @Override
        public Void visit_CALDoc_TaggedBlock_See_TypeClass(
                CALDoc.TaggedBlock.See.TypeClass typeClass, Options options) {

            visitCalDocPart(typeClass, options);
            return null;
        }

        @Override
        public Void visit_CALDoc_TaggedBlock_See_WithoutContext(
                CALDoc.TaggedBlock.See.WithoutContext seeBlock, Options options) {
            visitCalDocPart(seeBlock, options);
            return null;
        }
        
        
        @Override
        public Void visit_ModuleDefn(ModuleDefn defn, Options options) {

            SourceModel.verifyArg(defn, "defn");

            if (defn.getCALDocComment() != null) {
                formatCALDocComment(defn.getCALDocComment());
            }

            SourceTextNode root = new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
            root.setForcedBreak();
            addNodeForSourceElementWithEmbellishments(defn, root);

            stack.push(root);
            defn.getModuleName().accept(this, options);
            stack.pop();
            
            root.getNthChild(0).prefixText("module ");
            root.getNthChild(0).suffixText(";");
            
            SourceTextNode topLevelRoot = new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
            topLevelRoot.setForcedBreak();
            
            root.addChild(topLevelRoot);
            stack.push(topLevelRoot);
            
            final int nImportedModules = defn.getNImportedModules();
            for (int i = 0; i < nImportedModules; i++) {
                defn.getNthImportedModule(i).accept(this, options);
               
            }
            
            final int nFriendModules = defn.getNFriendModules();
            for (int i = 0; i < nFriendModules; i++) {
                defn.getNthFriendModule(i).accept(this, options);
            }
            
            final int nTopLevelDefns = defn.getNTopLevelDefns();
            for (int i = 0; i < nTopLevelDefns; i++) {
                defn.getNthTopLevelDefn(i).accept(this, options);
            }

            //add all trailing embellishments
            SourceTextNode trailing = new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
            addNodeForSourceElementWithEmbellishments(new SourceRange(new SourcePosition(Integer.MAX_VALUE,1), new SourcePosition(Integer.MAX_VALUE,2)), trailing);

            stack.pop();
            
            if (defn.getCALDocComment() != null) {
                stack.pop();
            }

            return null;
        }

        
        @Override
        public Void visit_Expr_ExprTypeSignature(
                Expr.ExprTypeSignature signature, Options options) {

            SourceTextNode root = new SourceTextNode(
                    SourceTextNode.FormatStyle.FIRST_CHILD_AT_LEVEL);
            addNodeForSourceElementWithEmbellishments(signature, root);
            stack.push(root);

            super.visit_Expr_ExprTypeSignature(signature, options);

            stack.pop();

            if (signature.getExpr().precedenceLevel() <= signature
                    .precedenceLevel()) {
                // parenthesize
                parenthesizeExpression(root.getNthChild(0));
            }

            root.getNthChild(1).prefixText(":: ");

            return null;
        }

        
        @Override
        public Void visit_Friend(SourceModel.Friend friendDeclaration,
                Options options) {

            SourceModel.verifyArg(friendDeclaration, "friendDeclaration");

            SourceTextNode root = new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
            root.setForcedBreak();

            addNodeForSourceElementWithEmbellishments(friendDeclaration, root);
            
            stack.push(root);
            friendDeclaration.getFriendModuleName().accept(this, options);
            stack.pop();
            
            root.getNthChild(0).prefixText("friend ");
            root.getNthChild(0).suffixText(";");
            return null;
        }
        
        @Override
        public Void visit_FunctionDefn_Foreign(FunctionDefn.Foreign defn,
                Options options) {

            SourceModel.verifyArg(defn, "foreign");

            if (defn.getCALDocComment() != null) {
                formatCALDocComment(defn.getCALDocComment());
            }

            SourceTextNode root = new SourceTextNode(
                    SourceTextNode.FormatStyle.FIRST_CHILD_AT_LEVEL);
            addNodeForSourceElementWithEmbellishments(defn, root);

            // e.g. foreign unsafe import jvm "method Color.getBlue" private
            // getBlueFromColour :: Colour -> Int;
            SourceTextNode header = SourceTextNode.makeAtmoic("foreign unsafe import jvm " +
                    StringEncoder.encodeString(defn.getExternalName()));

            root.addChild(header);
            
            SourceTextNode body = new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_IN_ONE_LEVEL);
            
            root.addChild(body);
            
            body.setThisText(defn.getName() + " ::");
            stack.push(body);
            defn.getDeclaredType().accept(this, options);
            stack.pop();
            
            if (defn.isScopeExplicitlySpecified()) {
                body.prefixText(defn.getScope().toString()+' ');
            }
            
            //body.addChild(SourceTextNode.makeSemicolon());
            body.suffixText(";");

            if (defn.getCALDocComment() != null) {
                stack.pop();
            }
            
            return null;
        }

        @Override
        public Void visit_FunctionDefn_Primitive(
                FunctionDefn.Primitive primitive, Options options) {

            SourceModel.verifyArg(primitive, "primitive");

            if (primitive.getCALDocComment() != null) {
                formatCALDocComment(primitive.getCALDocComment());
            }

            SourceTextNode root = new SourceTextNode(
                    SourceTextNode.FormatStyle.FIRST_CHILD_AT_LEVEL);
            addNodeForSourceElementWithEmbellishments(primitive, root);

            root.setThisText(primitive.getName() + " ::");
            if (primitive.isScopeExplicitlySpecified()) {
                root.prefixText(primitive.getScope().toString()+' ');
            }
            root.prefixText("primitive ");
            
            stack.push(root);
            primitive.getDeclaredType().accept(this, options);
            stack.pop();
            
            root.addChild(SourceTextNode.makeSemicolon());
            
            if (primitive.getCALDocComment() != null) {
                stack.pop();
            }
            

            return null;
        }

        
        @Override
        public Void visit_Import(Import importStmt, Options options) {

            SourceModel.verifyArg(importStmt, "importStmt");

            SourceTextNode root = new SourceTextNode(
                    SourceTextNode.FormatStyle.FIRST_CHILD_AT_LEVEL);
            
            addNodeForSourceElementWithEmbellishments(importStmt, root);
            stack.push(root);
            importStmt.getImportedModuleName().accept(this, options);
            stack.pop();
            
            root.getNthChild(0).prefixText("import ");

            Import.UsingItem usingItems[] = importStmt.getUsingItems();
            if (usingItems.length > 0) {
                
                root.getNthChild(0).suffixText(" using");
                
                SourceTextNode usingRoot = new SourceTextNode(
                        SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
                root.setForcedBreak();
                usingRoot.setForcedBreak();
                root.addChild(usingRoot);
                
                class UsingItem {
                    String moduleName;
                    String itemType;
                    List<String> itemNames = new ArrayList<String>();
                }

                List<UsingItem> processedUsingItems = new ArrayList<UsingItem>();

                if (options.groupImportUsingClauses) {
                    List<SourceModel.Import.UsingItem> dataConstructors = new ArrayList<SourceModel.Import.UsingItem>();
                    List<SourceModel.Import.UsingItem> functions = new ArrayList<SourceModel.Import.UsingItem>();
                    List<SourceModel.Import.UsingItem> typeClasses = new ArrayList<SourceModel.Import.UsingItem>();
                    List<SourceModel.Import.UsingItem> typeConstructors = new ArrayList<SourceModel.Import.UsingItem>();

                    for (org.openquark.cal.compiler.SourceModel.Import.UsingItem element : usingItems) {
                        if (element instanceof Import.UsingItem.DataConstructor) {
                            dataConstructors.add(element);
                        } else if (element instanceof Import.UsingItem.Function) {
                            functions.add(element);
                        } else if (element instanceof Import.UsingItem.TypeClass) {
                            typeClasses.add(element);
                        } else if (element instanceof Import.UsingItem.TypeConstructor) {
                            typeConstructors.add(element);
                        } else {
                            throw new NullPointerException(
                                    "Unknown using item type.");
                        }
                    }

                    if (typeConstructors.size() > 0) {
                        UsingItem ui = new UsingItem();
                        ui.moduleName = importStmt.getImportedModuleName()
                                .toString();
                        processedUsingItems.add(ui);
                        for (int i = 0, n = typeConstructors.size(); i < n; ++i) {
                            Import.UsingItem iui = typeConstructors.get(i);
                            ui.itemType = iui.getUsingItemCategoryName();
                            String[] names = iui.getUsingNames();
                            for (int j = 0, nNames = names.length; j < nNames; ++j) {
                                ui.itemNames.add(names[j]);
                            }
                        }
                    }

                    if (dataConstructors.size() > 0) {
                        UsingItem ui = new UsingItem();
                        ui.moduleName = importStmt.getImportedModuleName()
                                .toString();
                        processedUsingItems.add(ui);
                        for (int i = 0, n = dataConstructors.size(); i < n; ++i) {
                            Import.UsingItem iui = dataConstructors.get(i);
                            ui.itemType = iui.getUsingItemCategoryName();
                            String[] names = iui.getUsingNames();
                            for (int j = 0, nNames = names.length; j < nNames; ++j) {
                                ui.itemNames.add(names[j]);
                            }
                        }
                    }

                    if (functions.size() > 0) {
                        UsingItem ui = new UsingItem();
                        ui.moduleName = importStmt.getImportedModuleName()
                                .toString();
                        processedUsingItems.add(ui);
                        for (int i = 0, n = functions.size(); i < n; ++i) {
                            Import.UsingItem iui = functions.get(i);
                            ui.itemType = iui.getUsingItemCategoryName();
                            String[] names = iui.getUsingNames();
                            for (int j = 0, nNames = names.length; j < nNames; ++j) {
                                ui.itemNames.add(names[j]);
                            }
                        }
                    }

                    if (typeClasses.size() > 0) {
                        UsingItem ui = new UsingItem();
                        ui.moduleName = importStmt.getImportedModuleName()
                                .toString();
                        processedUsingItems.add(ui);
                        for (int i = 0, n = typeClasses.size(); i < n; ++i) {
                            Import.UsingItem iui = typeClasses.get(i);
                            ui.itemType = iui.getUsingItemCategoryName();
                            String[] names = iui.getUsingNames();
                            for (int j = 0, nNames = names.length; j < nNames; ++j) {
                                ui.itemNames.add(names[j]);
                            }
                        }
                    }

                } else {
                    for (org.openquark.cal.compiler.SourceModel.Import.UsingItem iui : usingItems) {
                        UsingItem ui = new UsingItem();
                        ui.moduleName = importStmt.getImportedModuleName()
                                .toString();
                        processedUsingItems.add(ui);
                        ui.itemType = iui.getUsingItemCategoryName();
                        String[] names = iui.getUsingNames();
                        for (int j = 0, nNames = names.length; j < nNames; ++j) {
                            ui.itemNames.add(names[j]);
                        }
                    }
                }

                for (int i = 0, n = processedUsingItems.size(); i < n; ++i) {
                    UsingItem ui = processedUsingItems.get(i);
                    
                    final String itemCategory = ui.itemType;

                    SourceTextNode category = new SourceTextNode(SourceTextNode.FormatStyle.CONCAT_CHILDREN_TO_LINE_END_AFTER_NEWLINE);
                    usingRoot.addChild(category);
                    category.setThisText(itemCategory + " =");
                    category.separator=",";

                    for (int j = 0, nNames = ui.itemNames.size(); j < nNames; j++) {
                        category.addChild(new SourceTextNode(ui.itemNames.get(j), SourceTextNode.FormatStyle.ATOMIC));
                    }
                    category.suffixText(";");
                }
                root.addChild(SourceTextNode.makeSemicolon());
            } else {
                root.suffixText(";");
            }
            
            
            return null;
        }

        
        @Override
        public Void visit_InstanceDefn(InstanceDefn defn, Options options) {

            SourceModel.verifyArg(defn, "defn");

            if (defn.getCALDocComment() != null) {
                formatCALDocComment(defn.getCALDocComment());
            }

            SourceTextNode root = new SourceTextNode(
                    SourceTextNode.FormatStyle.FIRST_CHILD_AT_LEVEL);
            root.setForcedBreak();
            addNodeForSourceElementWithEmbellishments(defn.getSourceRangeOfName(), root);

            SourceTextNode header = new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
            
            stack.push(root);
            root.addChild(header);
            header.setThisText("instance");
            stack.push(header);
            
            final int nConstraints = defn.getNConstraints();
            if (nConstraints > 0 || defn.getParenthesizeConstraints()) {
                SourceTextNode constraintRoot = new SourceTextNode(
                        SourceTextNode.FormatStyle.FIRST_CHILD_AT_LEVEL);
                addNodeForSourceElementWithEmbellishments(defn,
                        constraintRoot);
                stack.push(constraintRoot);
                for (int i = 0; i < nConstraints; i++) {
                    defn.getNthConstraint(i).accept(this, options);
                }
                stack.pop();
                if (nConstraints > 1) {
                    for (int i = 0; i < constraintRoot.getNChildren() - 1; ++i) {
                        constraintRoot.getNthChild(i).suffixText(",");
                    }
                }

                if (nConstraints > 1 || defn.getParenthesizeConstraints()) {
                    parenthesizeExpression(constraintRoot);
                }

                constraintRoot.suffixText(" =>");
            }

 
            defn.getTypeClassName().accept(this, options);

            defn.getInstanceTypeCons().accept(this, options);
            
            header.addChild(SourceTextNode.makeAtmoic("where"));
            
            stack.pop();
            
            final int nInstanceMethods = defn.getNInstanceMethods();
            for (int i = 0; i < nInstanceMethods; i++) {
                InstanceMethod method = defn.getNthInstanceMethod(i);
                method.accept(this, options);
            }

            root.addChild(SourceTextNode.makeSemicolon());
            stack.pop();

            if (defn.getCALDocComment() != null) {
                stack.pop();
            }
            
            return null;
        }

        @Override
        public Void visit_InstanceDefn_InstanceTypeCons_Function(
                InstanceDefn.InstanceTypeCons.Function function, Options options) {

            SourceModel.verifyArg(function, "function");

            SourceTextNode root = new SourceTextNode(
                    SourceTextNode.FormatStyle.ATOMIC);
            addNodeForSourceElementWithEmbellishments(function, root);
            
            root.setThisText("(" + getTypeVarNameString(function.getDomainTypeVar()) + " -> " + getTypeVarNameString(function.getCodomainTypeVar()) + ")");

            return null;
        }

        @Override
        public Void visit_InstanceDefn_InstanceTypeCons_List(
                InstanceDefn.InstanceTypeCons.List list, Options options) {

            SourceModel.verifyArg(list, "list");

            SourceTextNode root = SourceTextNode.makeAtmoic("[" + getTypeVarNameString(list.getElemTypeVar()) + "]");
            addNodeForSourceElementWithEmbellishments(list, root);
            
            return null;
        }

        @Override
        public Void visit_InstanceDefn_InstanceTypeCons_Record(
                InstanceDefn.InstanceTypeCons.Record record, Options options) {

            SourceModel.verifyArg(record, "record");
            
            SourceTextNode root = new SourceTextNode(
                    SourceTextNode.FormatStyle.ATOMIC);
            addNodeForSourceElementWithEmbellishments(record, root);
            
            root.setThisText("{" + getTypeVarNameString(record.getElemTypeVar()) + "}");
            return null;
        }

        @Override
        public Void visit_InstanceDefn_InstanceTypeCons_TypeCons(
                InstanceDefn.InstanceTypeCons.TypeCons cons, Options options) {

            SourceModel.verifyArg(cons, "cons");

            SourceTextNode root = new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
            addNodeForSourceElementWithEmbellishments(cons, root);

            stack.push(root);
            
            Name.TypeVar typeVars[] = cons.getTypeVars();
            final int nTypeVars = typeVars.length;
            if (nTypeVars == 0 && !cons.getParenthesized()) {
                cons.getTypeConsName().accept(this, options);
            } else {
                cons.getTypeConsName().accept(this, options);
                for (int i = 0; i < nTypeVars; ++i) {
                    root.suffixText(" " + getTypeVarNameString(typeVars[i]));
                }

                parenthesizeExpression(root);

            }

            stack.pop();
            
            return null;
        }

        @Override
        public Void visit_InstanceDefn_InstanceTypeCons_Unit(
                InstanceDefn.InstanceTypeCons.Unit unit, Options options) {

            SourceModel.verifyArg(unit, "unit");
            
            SourceTextNode root = SourceTextNode.makeAtmoic("()");
            addNodeForSourceElementWithEmbellishments(unit, root);
            return null;
        }

        @Override
        public Void visit_InstanceDefn_InstanceMethod(
                InstanceDefn.InstanceMethod method, Options options) {

            SourceModel.verifyArg(method, "method");

            if (method.getCALDocComment() != null) {
                formatCALDocComment(method.getCALDocComment());
            }

            SourceTextNode root = new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
            addNodeForSourceElementWithEmbellishments(method, root);

            root.setThisText(method.getClassMethodName() + " =");
            stack.push(root);
            method.getResolvingFunctionName().accept(this, options);
            stack.pop();
            
            root.addChild(SourceTextNode.makeAtmoic(";"));

            if (method.getCALDocComment() != null) {
                stack.pop();
            }

            return null;
        }

        @Override
        public Void visit_TypeClassDefn(TypeClassDefn defn, Options options) {

            SourceModel.verifyArg(defn, "defn");

            if (defn.getCALDocComment() != null) {
                formatCALDocComment(defn.getCALDocComment());
            }

            SourceTextNode root = new SourceTextNode(
                    SourceTextNode.FormatStyle.FIRST_CHILD_AT_LEVEL);
            root.setForcedBreak();
            addNodeForSourceElementWithEmbellishments(defn, root);

            SourceTextNode header = new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
            
            stack.push(root);
            root.addChild(header);
            
            header.setThisText("class");

            if (defn.isScopeExplicitlySpecified()) {
                header.prefixText(defn.getScope().toString() + " ");
            }            
            
            stack.push(header);
            
            final int nConstraints = defn.getNParentClassConstraints();
            if (nConstraints > 0 || defn.getParenthesizeConstraints()) {
                SourceTextNode constraintRoot = new SourceTextNode(
                        SourceTextNode.FormatStyle.FIRST_CHILD_AT_LEVEL);
                addNodeForSourceElementWithEmbellishments(defn,
                        constraintRoot);
                stack.push(constraintRoot);
                for (int i = 0; i < nConstraints; i++) {
                    defn.getNthParentClassConstraint(i).accept(this, options);
                }
                stack.pop();
                if (nConstraints > 1) {
                    for (int i = 0; i < constraintRoot.getNChildren() - 1; ++i) {
                        constraintRoot.getNthChild(i).suffixText(",");
                    }
                }

                if (nConstraints > 1 || defn.getParenthesizeConstraints()) {
                    parenthesizeExpression(constraintRoot);
                }

                constraintRoot.suffixText(" =>");
            }

         
            header.addChild(SourceTextNode.makeAtmoic(defn.getTypeClassName() + " " + getTypeVarNameString(defn.getTypeVar()) + " where"));
            
            stack.pop();
            
            final int nClassMethodDefns = defn.getNClassMethodDefns();
            for (int i = 0; i < nClassMethodDefns; i++) {
                ClassMethodDefn method = defn.getNthClassMethodDefn(i);
                method.accept(this, options);
            }

            root.addChild(SourceTextNode.makeSemicolon());
            stack.pop();

            if (defn.getCALDocComment() != null) {
                stack.pop();
            }
            
            return null;
        }

        @Override
        public Void visit_TypeClassDefn_ClassMethodDefn(
                TypeClassDefn.ClassMethodDefn defn, Options options) {

            SourceModel.verifyArg(defn, "defn");

            if (defn.getCALDocComment() != null) {
                formatCALDocComment(defn.getCALDocComment());
            }

            SourceTextNode root = new SourceTextNode(
                    SourceTextNode.FormatStyle.FIRST_CHILD_AT_LEVEL);
            root.setForcedBreak();
            
            addNodeForSourceElementWithEmbellishments(defn, root);
            stack.push(root);
            

            SourceTextNode header = new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
           
            root.addChild(header);
            stack.push(header);
            
            header.setThisText(defn.getMethodName() + " ::");
            
            if (defn.isScopeExplicitlySpecified()) {
                header.prefixText(defn.getScope().toString() + " ");
            }

            defn.getTypeSignature().accept(this, options);

            stack.pop();
            
            if (defn.getDefaultClassMethodName() != null) {
                SourceTextNode defaultNode = new SourceTextNode(
                        SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
                root.addChild(defaultNode);
                defaultNode.setThisText("default ");
                stack.push(defaultNode);
                defn.getDefaultClassMethodName().accept(this, options);
                stack.pop();
            }

            root.suffixText(";");
            stack.pop();

            if (defn.getCALDocComment() != null) {
                stack.pop();
            }

            
            return null;
        }

        @Override
        public Void visit_TypeConstructorDefn_ForeignType(
                TypeConstructorDefn.ForeignType type, Options options) {

            SourceModel.verifyArg(type, "type");

            if (type.getCALDocComment() != null) {
                formatCALDocComment(type.getCALDocComment());
            }

            SourceTextNode root = new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_IN_ONE_LEVEL);
            
            addNodeForSourceElementWithEmbellishments(type, root);
            
            //root.setForcedBreak();
 
            root.setThisText("data foreign unsafe import jvm ");
            
            if (type.isImplementationScopeExplicitlySpecified()) {
                root.suffixText(type.getImplementationScope()+ " ");
            }
            root.suffixText(StringEncoder.encodeString(type.getExternalName()));
            
            
            SourceTextNode body = SourceTextNode.makeAtmoic(type.getTypeConsName());
            if (type.isScopeExplicitlySpecified()) {
                body.prefixText(type.getScope()+ " ");
            }
            root.addChild(body);
           

            if (type.getNDerivingClauseTypeClassNames() > 0) {
                SourceTextNode deriving = new SourceTextNode(
                        "deriving", SourceTextNode.FormatStyle.CONCAT_CHILDREN_TO_LINE_END_INDENT_SECOND_LINE);
                deriving.separator =",";
                body.addChild(deriving);
                stack.push(deriving);
                for (int i = 0, nDerivingClauseTypeClassNames = type
                        .getNDerivingClauseTypeClassNames(); i < nDerivingClauseTypeClassNames; i++) {
                    
                    type.getDerivingClauseTypeClassName(i).accept(this,
                            options);
                }
                stack.pop();
            }
 
            body.addChild(SourceTextNode.makeSemicolon());
            
            if (type.getCALDocComment() != null) {
                stack.pop();
            }

            return null;
        }

        
        @Override
        public Void visit_Name_DataCons(Name.DataCons cons, Options options) {

            SourceTextNode node = new SourceTextNode(
                    (cons.getModuleName() != null ? "." : "")
                            + cons.getUnqualifiedName(),
                    SourceTextNode.FormatStyle.ATOMIC);
            addNodeForSourceElementWithEmbellishments(cons, node);
            stack.push(node);

            super.visit_Name_DataCons(cons, options);

            node.consolidateTextLeft();

            stack.pop();

            return null;
        }

        @Override
        public Void visit_Name_Function(Name.Function function, Options options) {

            SourceTextNode node = new SourceTextNode(
                    (function.getModuleName() != null ? "." : "")
                            + function.getUnqualifiedName(),
                    SourceTextNode.FormatStyle.ATOMIC);
            addNodeForSourceElementWithEmbellishments(function, node);
            stack.push(node);

            super.visit_Name_Function(function, options);

            node.consolidateTextLeft();

            stack.pop();

            return null;
        }

        @Override
        public Void visit_Name_Module(Name.Module moduleName, Options options) {

            SourceTextNode node = new SourceTextNode((moduleName.getQualifier()
                    .getNComponents() > 0 ? "." : "")
                    + moduleName.getUnqualifiedModuleName(),
                    SourceTextNode.FormatStyle.ATOMIC);
            addNodeForSourceElementWithEmbellishments(moduleName, node);
            stack.push(node);

            super.visit_Name_Module(moduleName, options);

            node.consolidateTextLeft();

            stack.pop();

            return null;
        }

        @Override
        public Void visit_Name_Module_Qualifier(
                Name.Module.Qualifier qualifier, Options options) {

            StringBuilder sb = new StringBuilder();
            for (int i = 0, n = qualifier.getNComponents(); i < n; i++) {
                if (i > 0) {
                    sb.append('.');
                }
                sb.append(qualifier.getNthComponents(i));
            }

            SourceTextNode node = new SourceTextNode(sb.toString(),
                    SourceTextNode.FormatStyle.ATOMIC);
            addNodeForSourceElementWithEmbellishments(qualifier, node);
            stack.push(node);

            super.visit_Name_Module_Qualifier(qualifier, options);

            node.consolidateTextLeft();

            stack.pop();

            return null;
        }

        @Override
        public Void visit_Name_TypeClass(Name.TypeClass typeClass, Options options) {

            SourceTextNode node = new SourceTextNode(
                    (typeClass.getModuleName() != null ? "." : "")
                            + typeClass.getUnqualifiedName(),
                    SourceTextNode.FormatStyle.ATOMIC);
            addNodeForSourceElementWithEmbellishments(typeClass, node);
            stack.push(node);

            super.visit_Name_TypeClass(typeClass, options);

            node.consolidateTextLeft();

            stack.pop();

            return null;
        }

        @Override
        public Void visit_Name_TypeCons(Name.TypeCons cons, Options options) {

            SourceTextNode node = new SourceTextNode(
                    (cons.getModuleName() != null ? "." : "")
                            + cons.getUnqualifiedName(),
                    SourceTextNode.FormatStyle.ATOMIC);
            addNodeForSourceElementWithEmbellishments(cons, node);
            stack.push(node);

            super.visit_Name_TypeCons(cons, options);

            node.consolidateTextLeft();

            stack.pop();

            return null;
        }

        @Override
        public Void visit_Name_WithoutContextCons(Name.WithoutContextCons cons,
                Options options) {

            SourceTextNode node = new SourceTextNode(
                    (cons.getModuleName() != null ? "." : "")
                            + cons.getUnqualifiedName(),
                    SourceTextNode.FormatStyle.ATOMIC);
            addNodeForSourceElementWithEmbellishments(cons, node);
            stack.push(node);

            super.visit_Name_WithoutContextCons(cons, options);

            node.consolidateTextLeft();

            stack.pop();

            return null;
        }

        @Override
        public Void visit_Pattern_Var(Pattern.Var var, Options options) {

            SourceTextNode node = new SourceTextNode(var.getName(),
                    SourceTextNode.FormatStyle.ATOMIC);
            addNodeForSourceElementWithEmbellishments(var, node);

            return null;
        }

        @Override
        public Void visit_Pattern_Wildcard(Pattern.Wildcard wildcard,
                Options options) {

            SourceTextNode node = new SourceTextNode("_",
                    SourceTextNode.FormatStyle.ATOMIC);
            addNodeForSourceElementWithEmbellishments(wildcard, node);

            return null;
        }

        @Override
        public Void visit_FunctionTypeDeclaraction(
                FunctionTypeDeclaration declaration, Options options) {

            if (declaration.getCALDocComment() != null) {
                formatCALDocComment(declaration.getCALDocComment());
            }

            SourceModel.verifyArg(declaration, "declaration");

            SourceTextNode root;

            root = new SourceTextNode(declaration.getFunctionName() + " ::",
                    SourceTextNode.FormatStyle.CHILDREN_IN_ONE_LEVEL);

            addNodeForSourceElementWithEmbellishments(declaration, root);
            stack.push(root);

            declaration.getTypeSignature().accept(this, options);
            
            stack.pop();
            root.suffixText(";");

            if (declaration.getCALDocComment() != null) {
                stack.pop();
            }
            
            return null;
        }

        /**
         * this adds a SourceText node representing a CalDoc comment
         * @param comment the comment to add
         */
        private void formatCALDocComment(SourceModel.CALDoc.Comment comment) {
            SourceTextNode comment_declarationRoot = new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
            addNodeForSourceElementWithEmbellishments(comment,
                    comment_declarationRoot);
            stack.push(comment_declarationRoot);
            
            comment_declarationRoot.addCalDoc(new CALDocTextNode(comment));
        }

        @Override
        public Void visit_FunctionDefn_Algebraic(
                FunctionDefn.Algebraic algebraic, Options options) {

            if (algebraic.getCALDocComment() != null) {
                formatCALDocComment(algebraic.getCALDocComment());
            }

            SourceTextNode root = new SourceTextNode(
                    SourceTextNode.FormatStyle.FIRST_CHILD_AT_LEVEL);
            addNodeForSourceElementWithEmbellishments(algebraic
                    .getSourceRangeExcludingCaldoc(), root);
            stack.push(root);

            SourceTextNode nameParamRoot = new SourceTextNode(
                    algebraic.getName(),
                    SourceTextNode.FormatStyle.CONCAT_CHILDREN_TO_LINE_END_INDENT_SECOND_LINE);

            if (algebraic.isScopeExplicitlySpecified()) {
                nameParamRoot.prefixText(algebraic.getScope().toString() + " ");
            }

            root.addChild(nameParamRoot);
            stack.push(nameParamRoot);

            final int nParameters = algebraic.getNParameters();
            for (int i = 0; i < nParameters; i++) {
                algebraic.getNthParameter(i).accept(this, options);
            }

            stack.pop();
            nameParamRoot.addChild(new SourceTextNode("=",
                    SourceTextNode.FormatStyle.ATOMIC));

            SourceTextNode defExprRoot = new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
            root.addChild(defExprRoot);
            stack.push(defExprRoot);

            algebraic.getDefiningExpr().accept(this, options);

            stack.pop();
            stack.pop();
            if (algebraic.getCALDocComment() != null) {
                stack.pop();
            }

            defExprRoot.addChild(SourceTextNode.makeSemicolon());

            return null;
        }

        @Override
        public Void visit_LocalDefn_Function_Definition(
                LocalDefn.Function.Definition function, Options options) {

            if (function.getCALDocComment() != null) {
                formatCALDocComment(function.getCALDocComment());
            }

            SourceTextNode root = new SourceTextNode(
                    SourceTextNode.FormatStyle.FIRST_CHILD_AT_LEVEL);
            addNodeForSourceElementWithEmbellishments(function, root);
            stack.push(root);

            SourceTextNode nameParamRoot = new SourceTextNode(
                    function.getName(),
                    SourceTextNode.FormatStyle.CONCAT_CHILDREN_TO_LINE_END_INDENT_SECOND_LINE);
            root.addChild(nameParamRoot);
            stack.push(nameParamRoot);

            final int nParameters = function.getNParameters();
            for (int i = 0; i < nParameters; i++) {
                function.getNthParameter(i).accept(this, options);
            }

            stack.pop();
            nameParamRoot.suffixText(" =");

            SourceTextNode defExprRoot = new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
            root.addChild(defExprRoot);
            stack.push(defExprRoot);

            function.getDefiningExpr().accept(this, options);

            stack.pop();
            stack.pop();
            if (function.getCALDocComment() != null) {
                stack.pop();
            }

            defExprRoot.addChild(SourceTextNode.makeSemicolon());

            return null;
        }

        @Override
        public Void visit_LocalDefn_Function_TypeDeclaration(
                LocalDefn.Function.TypeDeclaration declaration, Options options) {

            if (declaration.getCALDocComment() != null) {
                formatCALDocComment(declaration.getCALDocComment());
            }

            SourceTextNode root;
            root = new SourceTextNode(declaration.getName() + " ::",
                    SourceTextNode.FormatStyle.CHILDREN_IN_ONE_LEVEL);
            addNodeForSourceElementWithEmbellishments(declaration, root);
            stack.push(root);

            declaration.getDeclaredType().accept(this, options);
            
            stack.pop();
            root.suffixText(";");

            if (declaration.getCALDocComment() != null) {
                stack.pop();
            }
            return null;
        }

        @Override
        public Void visit_LocalDefn_PatternMatch_UnpackDataCons(
                LocalDefn.PatternMatch.UnpackDataCons unpackDataCons,
                Options options) {

            SourceTextNode rootNode = new SourceTextNode(
                    SourceTextNode.FormatStyle.FIRST_CHILD_AT_LEVEL);
            addNodeForSourceElementWithEmbellishments(unpackDataCons, rootNode);
            stack.push(rootNode);

            SourceTextNode dcRoot = new SourceTextNode(
                    SourceTextNode.FormatStyle.FIRST_CHILD_AT_LEVEL);
            rootNode.addChild(dcRoot);
            stack.push(dcRoot);

            unpackDataCons.getDataConsName().accept(this, options);
            unpackDataCons.getArgBindings().accept(this, options);

            stack.pop();

            dcRoot.suffixText(" =");

            SourceTextNode defExprRoot = new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
            rootNode.addChild(defExprRoot);
            stack.push(defExprRoot);
            unpackDataCons.getDefiningExpr().accept(this, options);
            stack.pop();

            defExprRoot.addChild(SourceTextNode.makeSemicolon());

            stack.pop();

            return null;
        }

        @Override
        public Void visit_LocalDefn_PatternMatch_UnpackTuple(
                LocalDefn.PatternMatch.UnpackTuple unpackTuple, Options options) {

            SourceTextNode rootNode = new SourceTextNode(
                    SourceTextNode.FormatStyle.FIRST_CHILD_AT_LEVEL);
            addNodeForSourceElementWithEmbellishments(unpackTuple, rootNode);
            stack.push(rootNode);

            SourceTextNode tupleRoot = new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
            rootNode.addChild(tupleRoot);
            stack.push(tupleRoot);

            for (int i = 0, nPatterns = unpackTuple.getNPatterns(); i < nPatterns; i++) {
                unpackTuple.getNthPattern(i).accept(this, options);
            }

            stack.pop();

            for (int i = 0, n = tupleRoot.getNChildren() - 1; i < n; ++i) {
                tupleRoot.getNthChild(i).suffixText(",");
            }
            parenthesizeExpression(tupleRoot);
            tupleRoot.suffixText(" =");

            SourceTextNode defExprRoot = new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
            rootNode.addChild(defExprRoot);
            stack.push(defExprRoot);
            unpackTuple.getDefiningExpr().accept(this, options);
            stack.pop();

            defExprRoot.addChild(SourceTextNode.makeSemicolon());

            stack.pop();

            return null;
        }

        @Override
        public Void visit_LocalDefn_PatternMatch_UnpackListCons(
                LocalDefn.PatternMatch.UnpackListCons unpackListCons,
                Options options) {

            // Root node for the case alt
            SourceTextNode root = new SourceTextNode(
                    SourceTextNode.FormatStyle.FIRST_CHILD_AT_LEVEL);
            addNodeForSourceElementWithEmbellishments(unpackListCons, root);
            stack.push(root);

            // Root node for the list left hand side. i.e. x : xs =
            SourceTextNode listRoot = new SourceTextNode(
                    SourceTextNode.FormatStyle.ATOMIC);
            root.addChild(listRoot);
            stack.push(listRoot);

            unpackListCons.getHeadPattern().accept(this, options);
            unpackListCons.getTailPattern().accept(this, options);

            stack.pop();
            listRoot.getNthChild(0).suffixText(" :");
            listRoot.getNthChild(1).suffixText(" =");

            // Root for the expression.
            SourceTextNode defExprRoot = new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
            root.addChild(defExprRoot);
            stack.push(defExprRoot);

            unpackListCons.getDefiningExpr().accept(this, options);
            defExprRoot.addChild(SourceTextNode.makeSemicolon());
            stack.pop();

            stack.pop();

            return null;
        }

        @Override
        public Void visit_LocalDefn_PatternMatch_UnpackRecord(
                LocalDefn.PatternMatch.UnpackRecord unpackRecord, Options options) {

            // Root node for the case alt
            SourceTextNode root = new SourceTextNode(
                    SourceTextNode.FormatStyle.FIRST_CHILD_AT_LEVEL);
            addNodeForSourceElementWithEmbellishments(unpackRecord, root);
            stack.push(root);

            // Root for the record. i.e. {s | x = a, y = b} =
            SourceTextNode recordRoot = new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
            root.addChild(recordRoot);
            stack.push(recordRoot);

            if (unpackRecord.getBaseRecordPattern() != null) {
                unpackRecord.getBaseRecordPattern().accept(this, options);
                recordRoot.suffixText(" |");
            }

            final int nFieldPatterns = unpackRecord.getNFieldPatterns();
            for (int i = 0; i < nFieldPatterns; i++) {
                unpackRecord.getNthFieldPattern(i).accept(this, options);
                if (i < nFieldPatterns - 1) {
                    recordRoot.getNthChild(recordRoot.getNChildren() - 1)
                            .suffixText(",");
                }
            }

            stack.pop();
            parenthesizeExpression(recordRoot, "{", "}");
            recordRoot.suffixText(" =");

            // Root node for the defining expr.
            SourceTextNode defExprRoot = new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
            root.addChild(defExprRoot);
            stack.push(defExprRoot);

            unpackRecord.getDefiningExpr().accept(this, options);
            defExprRoot.addChild(SourceTextNode.makeSemicolon());
            stack.pop();

            stack.pop();

            return null;
        }

        @Override
        public Void visit_Constraint_Lacks(Constraint.Lacks lacks, Options options) {

            addNodeForSourceElementWithEmbellishments(lacks,
                    new SourceTextNode(getTypeVarNameString(lacks.getTypeVarName()) + "\\"
                            + getFieldNameString(lacks.getLacksField()),
                            SourceTextNode.FormatStyle.ATOMIC));

            return null;
        }

        @Override
        public Void visit_Constraint_TypeClass(Constraint.TypeClass typeClass,
                Options options) {

            SourceTextNode root = new SourceTextNode(" "
                    + getTypeVarNameString(typeClass.getTypeVarName()),
                    SourceTextNode.FormatStyle.ATOMIC);
            addNodeForSourceElementWithEmbellishments(typeClass, root);
            stack.push(root);

            super.visit_Constraint_TypeClass(typeClass, options);
            stack.pop();

            root.consolidateTextLeft();

            return null;
        }

        @Override
        public Void visit_TypeSignature(TypeSignature signature, Options options) {

            SourceModel.verifyArg(signature, "signature");

            final int nConstraints = signature.getNConstraints();
            if (nConstraints > 0 || signature.getConstraintsHaveParen()) {
                SourceTextNode constraintRoot = new SourceTextNode(
                        SourceTextNode.FormatStyle.FIRST_CHILD_AT_LEVEL);
                addNodeForSourceElementWithEmbellishments(signature,
                        constraintRoot);
                stack.push(constraintRoot);
                for (int i = 0; i < nConstraints; i++) {
                    signature.getNthConstraint(i).accept(this, options);
                }
                stack.pop();
                if (nConstraints > 1) {
                    for (int i = 0; i < constraintRoot.getNChildren() - 1; ++i) {
                        constraintRoot.getNthChild(i).suffixText(",");
                    }
                }

                if (nConstraints > 1 || signature.getConstraintsHaveParen()) {
                    parenthesizeExpression(constraintRoot);
                }

                constraintRoot.suffixText(" =>");
            }

            SourceTextNode typeExprRoot = new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
            addNodeForSourceElementWithEmbellishments(signature
                    .getTypeExprDefn(), typeExprRoot);
            stack.push(typeExprRoot);
            signature.getTypeExprDefn().accept(this, options);
            stack.pop();

            if (signature.getTypeExprDefn() instanceof TypeExprDefn.Function) {
                SourceTextNode root = (SourceTextNode) stack.peek();
                SourceTextNode functionRoot = root.getNthChild(root
                        .getNChildren() - 1);
                root.removeChild(root.getNChildren() - 1);
                for (int i = 0, n = functionRoot.getNChildren(); i < n; ++i) {
                    root.addChild(functionRoot.getNthChild(i));
                }
            }
            return null;
        }

        @Override
        public Void visit_TypeExprDefn_Parenthesized(
                TypeExprDefn.Parenthesized parenthesized, Options options) {

            SourceTextNode node = new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
            addNodeForSourceElementWithEmbellishments(parenthesized, node);
            stack.push(node);

            parenthesized.getTypeExprDefn().accept(this, options);

            parenthesizeExpression(node.getNthChild(0));

            stack.pop();

            return null;
        }

        @Override
        public Void visit_TypeExprDefn_Application(
                TypeExprDefn.Application application, Options options) {

            SourceTextNode root = new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
            addNodeForSourceElementWithEmbellishments(application, root);
            stack.push(root);

            super.visit_TypeExprDefn_Application(application, options);
            stack.pop();

            for (int i = 0, n = application.getNTypeExpressions(); i < n; ++i) {

                if (!application.getNthTypeExpression(i)
                        .neverNeedsParentheses()) {
                    parenthesizeExpression(root.getNthChild(i));
                }

            }

            return null;
        }

        @Override
        public Void visit_TypeExprDefn_Function(TypeExprDefn.Function function,
                Options options) {

            SourceTextNode root = new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
            addNodeForSourceElementWithEmbellishments(function, root);
            stack.push(root);

            super.visit_TypeExprDefn_Function(function, options);

            stack.pop();

            // we never need to parenthesize the type on the right hand side of
            // a "->".
            // This is because "->" is the lowest precedence operator in the
            // type grammar.
            // We only parenthesize the lhs if it is a "->".

            if (function.getDomain() instanceof Function) {
                parenthesizeExpression(root.getNthChild(0));
            }

            root.getNthChild(1).prefixText("-> ");

            if (function.getCodomain() instanceof TypeExprDefn.Function) {
                SourceTextNode co = root.getNthChild(1);
                root.removeChild(1);

                for (int i = 0, n = co.getNChildren(); i < n; ++i) {

                    root.addChild(co.getNthChild(i));
                }

            }

            return null;
        }

        @Override
        public Void visit_TypeExprDefn_List(TypeExprDefn.List list, Options options) {

            SourceModel.verifyArg(list, "list");

            SourceTextNode root = new SourceTextNode(
                    SourceTextNode.FormatStyle.FIRST_CHILD_AT_LEVEL);
            addNodeForSourceElementWithEmbellishments(list, root);
            stack.push(root);

            list.getElement().accept(this, options);
            stack.pop();

            parenthesizeExpression(root, "[", "]");

            return null;
        }

        @Override
        public Void visit_TypeExprDefn_Record(TypeExprDefn.Record record,
                Options options) {

            SourceTextNode root = new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
            addNodeForSourceElementWithEmbellishments(record, root);
            stack.push(root);

            super.visit_TypeExprDefn_Record(record, options);

            stack.pop();

            if (root.getNChildren() == 0) {
                root.setThisText("{}");
                return null;
            }

            int firstExtension = 0;
            if (record.getBaseRecordVar() != null && (root.getNChildren() > 1)) {
                root.getNthChild(0).suffixText(" |");
                firstExtension = 1;
            }

            for (int i = firstExtension, n = root.getNChildren() - 1; i < n; i++) {
                root.getNthChild(i).suffixText(",");
            }

            parenthesizeExpression(root, "{", "}");

            return null;
        }

        @Override
        public Void visit_TypeExprDefn_Record_FieldTypePair(
                TypeExprDefn.Record.FieldTypePair pair, Options options) {

            SourceModel.verifyArg(pair, "pair");

            SourceTextNode root = new SourceTextNode(getFieldNameString(pair.getFieldName())
                    + " ::", SourceTextNode.FormatStyle.CHILDREN_IN_ONE_LEVEL);
            addNodeForSourceElementWithEmbellishments(pair, root);
            stack.push(root);

            super.visit_TypeExprDefn_Record_FieldTypePair(pair, options);

            stack.pop();

            return null;
        }

        @Override
        public Void visit_TypeExprDefn_Tuple(TypeExprDefn.Tuple tuple,
                Options options) {

            SourceTextNode root = new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
            addNodeForSourceElementWithEmbellishments(tuple, root);
            stack.push(root);

            super.visit_TypeExprDefn_Tuple(tuple, options);
            stack.pop();

            for (int i = 0, n = root.getNChildren() - 1; i < n; ++i) {
                root.getNthChild(i).suffixText(",");
            }

            parenthesizeExpression(root);

            return null;
        }

        @Override
        public Void visit_TypeExprDefn_TypeCons(TypeExprDefn.TypeCons cons,
                Options options) {

            SourceModel.verifyArg(cons, "cons");

            cons.getTypeConsName().accept(this, options);
            return null;
        }

        @Override
        public Void visit_TypeExprDefn_TypeVar(TypeExprDefn.TypeVar var,
                Options options) {

            addNodeForSourceElementWithEmbellishments(var, new SourceTextNode(
                getTypeVarNameString(var.getTypeVarName()), SourceTextNode.FormatStyle.ATOMIC));

            return null;
        }

        @Override
        public Void visit_TypeExprDefn_Unit(TypeExprDefn.Unit unit, Options options) {

            addNodeForSourceElementWithEmbellishments(unit, new SourceTextNode(
                    "()", SourceTextNode.FormatStyle.ATOMIC));

            return null;
        }

        @Override
        public Void visit_Parameter(Parameter parameter, Options options) {

            addNodeForSourceElementWithEmbellishments(parameter,
                    new SourceTextNode((parameter.isStrict() ? "!" : "")
                            + parameter.getName(),
                            SourceTextNode.FormatStyle.ATOMIC));

            return null;
        }

        @Override
        public Void visit_TypeConstructorDefn_AlgebraicType_DataConsDefn(
                TypeConstructorDefn.AlgebraicType.DataConsDefn defn, Options options) {

            SourceModel.verifyArg(defn, "defn");

            if (defn.getCALDocComment() != null) {
                formatCALDocComment(defn.getCALDocComment());
            }

            SourceTextNode root = new SourceTextNode(
                    defn.getDataConsName(),
                    SourceTextNode.FormatStyle.CHILDREN_IN_ONE_LEVEL);
            
            if (defn.getNTypeArgs() > 1) {
                root.setForcedBreak();
            }
            
            addNodeForSourceElementWithEmbellishments(defn, root);
            stack.push(root);

            if (defn.isScopeExplicitlySpecified()) {
                root.prefixText(defn.getScope().toString() + " ");
            }

            for (int i = 0, nParams = defn.getNTypeArgs(); i < nParams; ++i) {
                defn.getNthTypeArg(i).accept(this, options);
            }

            stack.pop();

            if (defn.getCALDocComment() != null) {
                stack.pop();
            }

            return null;
        }

        @Override
        public Void visit_TypeConstructorDefn_AlgebraicType_DataConsDefn_TypeArgument(
                TypeConstructorDefn.AlgebraicType.DataConsDefn.TypeArgument argument,
                Options options) {

            SourceModel.verifyArg(argument, "argument");

            StringBuilder sb = new StringBuilder();
            sb.append(getFieldNameString(argument.getFieldName())).append(" ::");

            SourceTextNode root = new SourceTextNode(sb.toString(),
                    SourceTextNode.FormatStyle.CHILDREN_IN_ONE_LEVEL);
            addNodeForSourceElementWithEmbellishments(argument, root);
            stack.push(root);

            argument.getTypeExprDefn().accept(this, options);

            if (!argument.getTypeExprDefn().neverNeedsParentheses()) {
                parenthesizeExpression(root.getNthChild(0));
            }

            if (argument.isStrict()) {
                root.getNthChild(0).prefixText("!");
            }

            stack.pop();

            return null;
        }

        @Override
        public Void visit_TypeConstructorDefn_AlgebraicType(
                TypeConstructorDefn.AlgebraicType type, Options options) {

            if (type.getCALDocComment() != null) {
                formatCALDocComment(type.getCALDocComment());
            }

            
            StringBuilder text = new StringBuilder("data ");
            if (type.isScopeExplicitlySpecified()) {
                text.append(type.getScope().toString()).append(" ");
            }
            text.append(type.getTypeConsName());

            SourceModel.verifyArg(type, "type");
            SourceTextNode root = new SourceTextNode(text.toString(),
                    SourceTextNode.FormatStyle.CHILDREN_IN_ONE_LEVEL);
            addNodeForSourceElementWithEmbellishments(type, root);
            stack.push(root);

            Name.TypeVar typeParameters[] = type.getTypeParameters();
            for (int i = 0, nParams = typeParameters.length; i < nParams; ++i) {
                root.suffixText(' ' + getTypeVarNameString(typeParameters[i]));
            }
            root.suffixText(" =");

            SourceTextNode constructors = new SourceTextNode(
                    SourceTextNode.FormatStyle.CHILDREN_AT_SAME_LEVEL);
            constructors.setForcedBreak();
            constructors.separator = " |";
            root.addChild(constructors);
            stack.push(constructors);
            int nDataCons = type.getNDataConstructors();
            for (int i = 0; i < nDataCons; ++i) {
                type.getNthDataConstructor(i).accept(this, options);
            }
            stack.pop();

            final int nDerivingTypeClassNames = type
                    .getNDerivingClauseTypeClassNames();
            if (nDerivingTypeClassNames > 0) {
                SourceTextNode deriving = new SourceTextNode("deriving",
                        SourceTextNode.FormatStyle.CHILDREN_IN_ONE_LEVEL);
                addNodeForSourceElementWithEmbellishments(type, deriving);
                deriving.separator = ",";
                stack.push(deriving);
                for (int i = 0; i < nDerivingTypeClassNames; ++i) {
                    type.getDerivingClauseTypeClassName(i).accept(this, options);
                }
                stack.pop();
            }
            root.addChild(SourceTextNode.makeSemicolon());
            stack.pop();
            
            if (type.getCALDocComment() != null) {
                stack.pop();
            }

            return null;
        }
        
        /**
         * Returns the name of a SourceModel.Name.Field as a string.
         */
        // todo-jowong refactor this so that this visitor can properly have a visit_Name_Field method
        @Deprecated
        private static String getFieldNameString(final SourceModel.Name.Field fieldName) {
            return fieldName.getName().getCalSourceForm();
        }
        
        /**
         * Returns the name of a SourceModel.Name.TypeVar as a string.
         */
        // todo-jowong refactor this so that this visitor can properly have a visit_Name_TypeVar method
        @Deprecated
        private static String getTypeVarNameString(final SourceModel.Name.TypeVar typeVarName) {
            return typeVarName.getName();
        }

    }


    /** 
     * this node is used to represent a CalDoc comment
     * it can be associated with a SourceTextNode and is formatted when
     * the source text node is formatted. The actual formatting is deferred
     * to the CalDoc source formatter.
     * 
     * @author mbyne
     */
    private static final class CALDocTextNode {

        /** the comment that is to be formatted*/
        final private CALDoc.Comment comment;

        CALDocTextNode(CALDoc.Comment comment) {
            this.comment = comment;
        }
        
        /**
         * Format the caldoc text node to
         * text.
         * 
         * @param sb -
         *            the StringBuilder to put the formatted source in.
         * @param indentLevel
         * @param offset -
         *            offset from the current indent
         * @param options
         */
        public void toFormattedText(StringBuilder sb, int indentLevel,
                int offset, Options options) {
            
            StringBuilder commentBuffer = new StringBuilder();
            
            SourceModelCALDocFormatter form = new SourceModelCALDocFormatter(commentBuffer);
            comment.accept(form, options.withRestrictedLineLength(
                    options.maxLineLength - indentLevel
                    * options.indentString.length()
                    - 3));
            form.decorate(options);

            String[] lines = commentBuffer.toString().split(EOL);
            for(String line : lines) {
                emitIndentedText(sb, indentLevel, line, options);
            }
        }
    }
    
    /**
     * A SourceTextNode is a tree structure which can be converted to formated
     * text. Nodes in the tree contain text, a formatting style and children.
     * 
     * @author Magnus Byne
     */
    private static final class SourceTextNode {

        /** makes a source text node that is a semicolon */
        public static final SourceTextNode makeSemicolon() {
            return new SourceTextNode(";", SourceTextNode.FormatStyle.ATOMIC);
        }

        /** makes an atomic source text node with text*/
        public static final SourceTextNode makeAtmoic(String text) {
            return new SourceTextNode(text, SourceTextNode.FormatStyle.ATOMIC);
        }

        private boolean forcedBreak = false; 
        
        public void setForcedBreak() {
            forcedBreak = true;
        }
        
        /** The text associated with this node. */
        private String myText = "";

        /** caldoc may optionally be associated with the node */
        private CALDocTextNode caldoc = null;

        /** The style of formatting for this node. */
        private FormatStyle formatStyle;

        private String separator = "";

        /** Child nodes. */
        protected List<SourceTextNode> children = new ArrayList<SourceTextNode>();

        public int getNChildren() {
            return children.size();
        }

        public SourceTextNode getNthChild(int index) {
            return children.get(index);
        }

        public void removeChild(int i) {
            children.remove(i);
        }
        
        
        public void addCalDoc(CALDocTextNode caldoc) {
            this.caldoc= caldoc;
        }
        
        /**
         * Create an ExpressionTextNode
         * 
         * @param formatStyle
         */
        public SourceTextNode(FormatStyle formatStyle) {
            if (formatStyle == null) {
                throw new NullPointerException(
                        "Invalid null value for format type.");
            }

            this.formatStyle = formatStyle;
        }

        /**
         * Create an SourceTextNode
         * 
         * @param myText
         * @param formatStyle
         */
        public SourceTextNode(String myText, FormatStyle formatStyle) {
            if (formatStyle == null) {
                throw new NullPointerException(
                        "Invalid null value for formatStyle.");
            }

            this.myText = myText;
            this.formatStyle = formatStyle;
        }

        /** gets the text for this node - not including children */
        public String getThisText() {
            return myText;
        }

        public void setThisText(String newText) {
            myText = newText;
        }


        public SourceTextNode getNodeWithoutComment() {
            if (formatStyle == FormatStyle.COMMENTS_FOLLOWED_BY_CODE) {
                return getNthChild(1);
            } else {
                return this;
            }
        }

        /** the length of this node including all children */
        public int getTotalTextLength() {
            int totalLength = getThisText().length();
            if (totalLength > 0) {
                totalLength++;
            }
            for (Iterator<SourceTextNode> it = children.iterator(); it
                    .hasNext();) {
                totalLength += it.next().getTotalTextLength();
                if (it.hasNext()) {
                    totalLength += separator.length();
                }
            }
            return totalLength;
        }

        /**
         * get the maximum nesting on this node - this is used a measure of
         * complexity to see if it should be formatter on a single line or split
         */
        public int getMaximumNesting() {
            int max = 0;

            for (SourceTextNode sourceTextNode : children) {
                int d = sourceTextNode.getMaximumNesting();
                if (d > max) {
                    max = d;
                }
            }

            if (formatStyle == FormatStyle.INNER_CHILDREN_IN_ONE_LEVEL) {
                max++;
            }

            return max;
        }

        public void addChild(SourceTextNode node) {
            children.add(node);
        }

        /**
         * Prefix the supplied text to the text of this node, if there is any.
         * Otherwise prefix it to the leftmost child node with text. Text is not
         * prefixed to embellishment nodes.
         * 
         * @param prefixText
         */
        public boolean prefixText(String prefixText) {
            if (myText.length() > 0 && formatStyle != FormatStyle.EMBELLISHMENT) {
                myText = prefixText + myText;
                return true;
            } else if (formatStyle == FormatStyle.INNER_CHILDREN_IN_ONE_LEVEL) {
                // for parenthesized expressions we typically want to make sure
                // the brackets
                // are aligned - therefore we cannot simply prefix to the
                // opening parenthesis
                // however we must remove a trailing space that is inserted
                // between nodes by default

                myText = prefixText + myText;
                if (myText.charAt(myText.length() - 1) == ' ') {
                    myText = myText.substring(0, myText.length() - 1);
                }

                return true;
            } else {
                int i = 0;
                while (i < getNChildren()) {

                    if (getNthChild(i).prefixText(prefixText)) {
                        return true;
                    }
                    i++;
                }
                return false;
            }
        }

        /**
         * Suffix the given text to this nodes text if there are no children.
         * Otherwise suffix to the rightmost child (excluding comment nodes).
         * 
         * @param suffixText
         */
        public void suffixText(String suffixText) {
            if (getNChildren() == 0) {
                myText = myText + suffixText;
            } else {
                if (!suffixText.equals(EOL)
                        && formatStyle == FormatStyle.CODE_FOLLOWED_BY_COMMENT) {
                    children.get(0).suffixText(suffixText);
                } else {
                    children.get(children.size() - 1).suffixText(suffixText);
                }
            }
        }

        /**
         * Format the sourceTextNode Tree (ie this node and all children) to
         * text.
         * 
         * @param sb -
         *            the StringBuilder to put the formatted source in.
         * @param indentLevel
         * @param offset -
         *            offset from the current indent
         * @param options
         */
        public void toFormattedText(StringBuilder sb, int indentLevel,
                int offset, Options options) {

            if (caldoc != null) {
                caldoc.toFormattedText(sb, indentLevel, offset, options);
            }
            
            // Update the offset if necessary.
            if (offset == 0) {
                offset = sb.length() - sb.lastIndexOf(EOL) - EOL.length();
                if (offset < 0) {
                    offset = 0;
                }
            }

            // Find starting position on current line.
            int startPos = offset + (indentLevel * options.tabWidth);

            // If the expression fits on the remainder of the current line
            // we can simply append the text.
            if (getTotalTextLength() + startPos <= options.maxLineLength
                    && !hasForcedLineBreak()
                    && getMaximumNesting() <= options.maxNestingDepthOnOneLine) {

                // If we're at the beginning of a line we need to indent.
                if (offset == 0) {
                    emitIndent(sb, indentLevel, options);
                }

                appendExpressionText(sb);
                return;
            }

            // We need to break up the expression.

            // The expression is broken up based on the format type
            if (getFormatType().equals(FormatStyle.ATOMIC)) {
                if (offset == 0) {
                    emitIndent(sb, indentLevel, options);
                }

                appendExpressionText(sb);

            } else if (getFormatType().equals(FormatStyle.APPLICATION)) {
                if (SHOW_DEBUG_LABELS) {
                    emitIndentedText(sb, indentLevel, "APPLICATION", options);
                }

                if (getThisText().length() > 0) {
                    if (offset == 0) {
                        emitIndentedText(sb, indentLevel, getThisText(),
                                options);
                    } else {
                        sb.append(getThisText());
                        emitEOL(sb);
                        offset = 0;
                    }
                }

                // Format first child at the same indent level.
                getNthChild(0)
                        .toFormattedText(sb, indentLevel, offset, options);
                emitEOL(sb);

                // format other children (the arguments) in one level
                for (int i = 1, n = getNChildren(); i < n; ++i) {
                    getNthChild(i).toFormattedText(sb, indentLevel + 1, 0,
                            options);

                    if (i < n - 1) {
                        emitEOL(sb);
                    }
                }

            } else if (getFormatType().equals(
                    FormatStyle.CODE_FOLLOWED_BY_COMMENT)) {
                if (SHOW_DEBUG_LABELS) {
                    emitIndentedText(sb, indentLevel, "CODE_AND_COMMENT",
                            options);
                }

                assert (getNChildren() == 2);
                if (offset != 0) {
                    emitEOL(sb);
                    offset = 0;
                }

                // swap the children so that the comment precedes the code
                getNthChild(1).toFormattedText(sb, indentLevel, 0, options);
                emitEOL(sb);
                getNthChild(0).toFormattedText(sb, indentLevel, 0, options);

            } else if (getFormatType().equals(FormatStyle.NEW_LINE)) {
                if (SHOW_DEBUG_LABELS) {
                    emitIndentedText(sb, indentLevel, "NEWLINE", options);
                }
                emitEOL(sb);
            } else if (getFormatType().equals(FormatStyle.CONCAT_FIRST_CHILD)) {
                if (SHOW_DEBUG_LABELS) {
                    emitIndentedText(sb, indentLevel, "CONCAT_FIRST_CHILD",
                            options);
                }

                // See if the first child will fit on the line.
                if (getThisText().length() > 0) {
                    if (offset == 0) {
                        emitIndent(sb, indentLevel, options);
                    }
                    sb.append(getThisText());
                }

                int lastLineEnd = sb.lastIndexOf(EOL);
                if (lastLineEnd < 0) {
                    lastLineEnd = 0;
                }

                int linePos = sb.length() - lastLineEnd;
                int startChild = 0;
                if (linePos + getNthChild(0).getTotalTextLength() <= options
                        .getMaxColumns()) {
                    sb.append(' ');
                    getNthChild(0).appendExpressionText(sb);
                    emitEOL(sb);
                    offset = 0;
                    startChild = 1;
                }

                for (int i = startChild, n = getNChildren(); i < n; ++i) {
                    getNthChild(i).toFormattedText(sb, indentLevel, 0, options);
                    if (i < n - 1) {
                        emitEOL(sb);
                    }
                }
            } else if ( getFormatType().equals(
                    FormatStyle.CHILDREN_AT_SAME_LEVEL)
                    ||  getFormatType().equals(
                            FormatStyle.COMMENTS_FOLLOWED_BY_CODE)) {
                if (SHOW_DEBUG_LABELS) {
                    emitIndentedText(sb, indentLevel,
                            "CHILDREN_AT_SAME_LEVEL_FORCE_BREAK", options);
                }

                if (offset != 0) {
                    emitEOL(sb);
                }

                if (getThisText().length() > 0) {
                    if (offset == 0) {
                        emitIndent(sb, indentLevel, options);
                    }
                    sb.append(getThisText());
                    emitEOL(sb);
                    offset = 0;
                }
                for (int i = 0, n = getNChildren(); i < n; ++i) {
                    // this is to avoid writing out a blank line if the last
                    // line was blank
                    if (getNthChild(i).getTotalTextLength() > 0) {

                        if ((i < n - 1)) {
                            getNthChild(i).suffixText(separator);
                        }
                        getNthChild(i).toFormattedText(sb, indentLevel,
                                i == 0 ? offset : 0, options);
                        if (i < n - 1) {
                            emitEOL(sb);
                        }
                    } else if (sb.lastIndexOf(EOL + EOL) != sb.length()
                            - EOL.length() * 2) {
                        if (i < n - 1) {
                            emitEOL(sb);
                        }
                    }
                }

            } else if (getFormatType()
                    .equals(FormatStyle.CHILDREN_IN_ONE_LEVEL)) {
                if (SHOW_DEBUG_LABELS) {
                    emitIndentedText(sb, indentLevel, "CHILDREN_IN_ONE_LEVEL",
                            options);
                }

                if (getThisText().length() > 0) {
                    if (offset == 0) {
                        emitIndentedText(sb, indentLevel, getThisText(),
                                options);
                    } else {
                        sb.append(getThisText());
                        emitEOL(sb);
                        offset = 0;
                    }
                }

                // Format each child on a new line.
                for (int i = 0, n = getNChildren(); i < n; ++i) {
                    if (i < n - 1) {
                        getNthChild(i).suffixText(separator);
                    }
                    getNthChild(i).toFormattedText(sb, indentLevel + 1, 0,
                            options);
                    if (i < n - 1) {
                        emitEOL(sb);
                    }
                }
            } else if (getFormatType().equals(
                    FormatStyle.INNER_CHILDREN_IN_ONE_LEVEL)) {

                if (SHOW_DEBUG_LABELS) {
                    emitIndentedText(sb, indentLevel,
                            "INNER_CHILDREN_IN_ONE_LEVEL", options);
                }

                if (getThisText().length() > 0) {
                    if (offset == 0) {
                        emitIndentedText(sb, indentLevel, getThisText(),
                                options);
                    } else {
                        sb.append(getThisText());
                        emitEOL(sb);
                        offset = 0;
                    }
                }

                // Format each child on a new line.
                for (int i = 0, n = getNChildren(); i < n; ++i) {

                    getNthChild(i).toFormattedText(
                            sb,
                            (i > 0 && i < (n - 1)) ? indentLevel + 1
                                    : indentLevel, 0, options);
                    if (i < n - 1) {
                        emitEOL(sb);
                    }
                }
            } else if (getFormatType().equals(FormatStyle.FIRST_CHILD_AT_LEVEL)) {
                if (SHOW_DEBUG_LABELS) {
                    emitIndentedText(sb, indentLevel, "FIRST_CHILD_AT_LEVEL",
                            options);
                }

                if (getThisText().length() > 0) {
                    if (offset == 0) {
                        emitIndentedText(sb, indentLevel, getThisText(),
                                options);
                    } else {
                        sb.append(getThisText());
                        emitEOL(sb);
                        offset = 0;
                    }
                }

                // Format first child at the same indent level.
                getNthChild(0)
                        .toFormattedText(sb, indentLevel, offset, options);

                // Format other children on new lines.
                for (int i = 1, n = getNChildren(); i < n; ++i) {
                    emitEOL(sb);
                    getNthChild(i).toFormattedText(sb, indentLevel + 1, 0,
                            options);
                }
            } else if (getFormatType().equals(FormatStyle.ALTERNATE_CHILDREN)) {
                if (SHOW_DEBUG_LABELS) {
                    emitIndentedText(sb, indentLevel, "ALTERNATE_CHILDREN",
                            options);
                }

                if (getThisText().length() > 0) {
                    if (offset == 0) {
                        emitIndentedText(sb, indentLevel, getThisText(),
                                options);
                    } else {
                        sb.append(getThisText());
                        emitEOL(sb);
                        offset = 0;
                    }
                }

                // Format each child on a new line.
                for (int i = 0, n = getNChildren(); i < n; ++i) {
                    getNthChild(i).toFormattedText(sb,
                            ((i % 2) != 0) ? indentLevel + 1 : indentLevel, 0,
                            options);
                    if (i < n - 1) {
                        emitEOL(sb);
                    }
                }
            } else if (getFormatType().equals(
                    FormatStyle.CONCAT_CHILDREN_TO_LINE_END_AFTER_NEWLINE)) {
                if (SHOW_DEBUG_LABELS) {
                    emitIndentedText(sb, indentLevel,
                            "CONCAT_CHILDREN_TO_LINE_END_AFTER_NEWLINE",
                            options);
                }
                if (getThisText().length() > 0) {
                    if (offset == 0) {
                        emitIndent(sb, indentLevel, options);
                    }
                    sb.append(getThisText()).append(' ');
                }

                indentLevel ++;
                
                StringBuilder lineBuffer = new StringBuilder();
                emitIndent(lineBuffer, indentLevel, options);
                int linePos = indentLevel * options.getTabWidth();
                
                String sep = separator + ' ';

                for (int i = 0, n = getNChildren(); i < n; ++i) {
                    SourceTextNode child = getNthChild(i);
                    int childTextLength = child.getTotalTextLength()-1;

                    if (childTextLength + linePos + ( i< n-1 ? sep.length() : 0) > options.maxLineLength) {
                        emitEOL(lineBuffer);
                        emitIndent(lineBuffer, indentLevel, options);
                        linePos = indentLevel * options.getTabWidth();
                    }

                    child.appendExpressionText(lineBuffer);

                    if (i < n - 1) {
                        lineBuffer.append(sep);
                        linePos += sep.length();
                    }

                    linePos += childTextLength;

                }
                sb.append(EOL);
                sb.append(lineBuffer);
                indentLevel--;
            } else if (getFormatType().equals(
                    FormatStyle.CONCAT_CHILDREN_TO_LINE_END_INDENT_SECOND_LINE)) {
                if (SHOW_DEBUG_LABELS) {
                    emitIndentedText(sb, indentLevel,
                            "CONCAT_CHILDREN_TO_LINE_END_INDENT_SECOND_LINE",
                            options);
                }

                // This is used for laying out something like a function
                // declaration where the function
                // and argument names won't fit on a single line.
                // ex. myfunction a b c ....
                // d e f

                int lineStartPos = 0;
                int secondLineExtraIndent = 0;
                if (getThisText().length() > 0) {
                    if (offset == 0) {
                        emitIndent(sb, indentLevel, options);
                    }
                    sb.append(getThisText()).append(' ');
                    int lastNewline = sb.lastIndexOf(EOL);
                    if (lastNewline < 0) {
                        lastNewline = 0;
                    }
                    lineStartPos = sb.length() - lastNewline;
                    secondLineExtraIndent = getThisText().length() + 1;
                } else {
                    lineStartPos = indentLevel * options.tabWidth;
                }

                StringBuilder sbTemp = new StringBuilder();
                for (int i = 0; i < secondLineExtraIndent; ++i) {
                    sbTemp.append(' ');
                }
                String extraIndentString = sbTemp.toString();

                StringBuilder lineBuffer = new StringBuilder();
                int linePos = lineStartPos;

                for (int i = 0, n = getNChildren(); i < n; ++i) {
                    SourceTextNode child = getNthChild(i);
                    int childTextLength = child.getTotalTextLength();

                    if (childTextLength + linePos > options.maxLineLength) {
                        emitEOL(lineBuffer);
                        emitIndent(lineBuffer, indentLevel, options);
                        lineBuffer.append(extraIndentString);
                        lineStartPos = lineBuffer.length()
                                - lineBuffer.lastIndexOf(EOL);
                        linePos = lineStartPos;
                    }

                    child.appendExpressionText(lineBuffer);

                    if (child.hasForcedLineBreak()) {
                        emitEOL(lineBuffer);
                        emitIndent(lineBuffer, indentLevel, options);
                        lineBuffer.append(extraIndentString);
                        lineStartPos = lineBuffer.length()
                                - lineBuffer.lastIndexOf(EOL);
                        linePos = lineStartPos;
                    } else if (i < n - 1) {
                        lineBuffer.append(separator).append(' ');
                    }

                    linePos += childTextLength;

                }
                sb.append(lineBuffer);
            } else if (getFormatType().equals(FormatStyle.EMBELLISHMENT)) {
                if (SHOW_DEBUG_LABELS) {
                    emitIndentedText(sb, indentLevel, "EMBELLISHMENT", options);
                }

                // the comment must be split into multiple lines
                List<String> lines;
                if (myText.startsWith("//")) {
                    lines = SourceEmbellishment.SingleLineComment.format(
                            this.myText, options.maxLineLength - indentLevel);
                } else {
                    lines = SourceEmbellishment.MultiLineComment.format(
                            this.myText, options.maxLineLength - indentLevel);
                }

                for (int i = 0; i < lines.size(); i++) {
                    emitIndent(sb, indentLevel, options);
                    sb.append(lines.get(i));
                    if (i < lines.size() - 1) {
                        emitEOL(sb);
                    }
                }

            } else {
                if (SHOW_DEBUG_LABELS) {
                    emitIndentedText(sb, indentLevel, "DEFAULT", options);
                }

                // Default behavior is to place this nodes text on the current
                // line and then
                // format each child starting on a new line with the same
                // indent.
                if (getThisText().length() > 0) {
                    if (offset == 0) {
                        emitIndent(sb, indentLevel, options);
                    }
                    sb.append(getThisText());
                    // emitLine(sb);
                    offset = 0;
                }

                for (int i = 0, n = getNChildren(); i < n; ++i) {
                    SourceTextNode child = getNthChild(i);
                    child.toFormattedText(sb, indentLevel, offset, options);
                    sb.append(separator);
                    if (!sb.toString().endsWith(EOL)) {
                        sb.append(EOL);
                    }
                }
            }

        }

        /**
         * Build up the concatenation of the text contained in this node and all
         * its children.
         * 
         * @param sb
         */
        private void appendExpressionText(StringBuilder sb) {
            StringBuilder sb2 = new StringBuilder();
            appendExpressionText2(sb2);
            sb.append(sb2.toString());
        }

        private void appendExpressionText2(StringBuilder sb) {
            if (getThisText().length() > 0) {
                if (sb.length() > 0 && !getThisText().startsWith(";")
                        && !getThisText().startsWith(")")
                        && !getThisText().startsWith("}")
                        && !getThisText().startsWith("]")
                        && !getThisText().startsWith(".")) {

                    char lastChar = sb.charAt(sb.length() - 1);
                    if (lastChar != '(' && lastChar != '{' && lastChar != '!'
                            && lastChar != '[') {
                        sb.append(" ");
                    }
                }
                sb.append(getThisText());
            }

            for (int i = 0, n = getNChildren(); i < n; ++i) {
                SourceTextNode child = getNthChild(i);
                child.appendExpressionText2(sb);
                if (i < (n - 1)) {
                    sb.append(separator);
                }
            }

        }

        private FormatStyle getFormatType() {
            return formatStyle;
        }

        public boolean startsWithBracket() {
            if (formatStyle == FormatStyle.INNER_CHILDREN_IN_ONE_LEVEL) {
                return true;
            }

            if (getNChildren() > 0) {
                return getNthChild(0).startsWithBracket();
            }

            return false;
        }

        public SourceTextNode consolidateTextLeft() {
            for (int i = getNChildren() - 1; i >= 0; i--) {
                prefixText(getNthChild(i).consolidateTextLeft().getThisText());
            }

            children = new ArrayList<SourceTextNode>();

            return this;
        }

        public SourceTextNode consolidateTextRight() {
            if (myText.length() == 0) {
                myText = " ";
            }
            for (int i = 0, n = getNChildren(); i < n; ++i) {
                suffixText(getNthChild(i).consolidateTextRight().getThisText());
            }

            children = new ArrayList<SourceTextNode>();

            return this;
        }

        private boolean hasForcedLineBreak() {
            if (forcedBreak || 
                    getFormatType().equals(FormatStyle.NEW_LINE)
                    || getFormatType().equals(
                            FormatStyle.COMMENTS_FOLLOWED_BY_CODE)
                    || caldoc != null) {
                return true;
            }

            for (int i = 0, n = getNChildren(); i < n; ++i) {
                if (getNthChild(i).hasForcedLineBreak()) {
                    return true;
                }
            }

            return false;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            appendExpressionText(sb);
            return sb.toString();
        }

        /**
         * A class used to indicate the type of formatting associated with an
         * ExpressionTextNode.
         * 
         * @author rcypher
         */
        public static final class FormatStyle {
            /** The text of the sub-tree cannot be broken apart. */
            public static final FormatStyle ATOMIC = new FormatStyle("ATOMIC");

            /**
             * The text is an embellishment - this style stops code from being prefixed
             * or suffixed
             */
            public static final FormatStyle EMBELLISHMENT = new FormatStyle(
                    "EMBELLISHMENT");

            /**
             * The text of the sub-tree can be broken between nodes. The
             * children have the same indent level as the root.
             */
            public static final FormatStyle CHILDREN_AT_SAME_LEVEL = new FormatStyle(
                    "CHILDREN_AT_SAME_LEVEL");

            /**
             * The text of the sub-tree can be broken between nodes. The
             * children should indent one more level than the root.
             */
            public static final FormatStyle CHILDREN_IN_ONE_LEVEL = new FormatStyle(
                    "CHILDREN_IN_ONE_LEVEL");

            /**
             * The first and last child will be at the same level the inner
             * children will be indented one level. The sub tree may be
             * formatted on a single line if it will fit. This is used for
             * parenthesized expressions.
             */
            public static final FormatStyle INNER_CHILDREN_IN_ONE_LEVEL = new FormatStyle(
                    "INNER_CHILDREN_IN_ONE_LEVEL");

            /**
             * The text of the sub-tree can be broken between nodes. The first
             * child should have the same indent level as the root. Other
             * children should be indented an additional level.
             */
            public static final FormatStyle FIRST_CHILD_AT_LEVEL = new FormatStyle(
                    "FIRST_CHILD_AT_LEVEL");

            /**
             * Force a line break.
             */
            public static final FormatStyle NEW_LINE = new FormatStyle(
                    "NEW_LINE");

            /**
             * The indenting of the children will alternate. Odd numbered
             * children will be indented one level and even numbered children
             * will be at the same level as the root. If all children fit on a
             * single line that is acceptable.
             */
            public static final FormatStyle ALTERNATE_CHILDREN = new FormatStyle(
                    "ALTERNATE_CHILDREN");

            /**
             * If the source doesn't fit on the current line start the children
             * on a new line and concat to line end.
             */
            public static final FormatStyle CONCAT_CHILDREN_TO_LINE_END_AFTER_NEWLINE = new FormatStyle(
                    "CONCAT_CHILDREN_TO_LINE_END_AFTER_NEWLINE");

            /**
             * If the source doesn't fit on the current line fit the root text
             * and as many children on each line as possible. Indent starting on
             * the second line.
             */
            public static final FormatStyle CONCAT_CHILDREN_TO_LINE_END_INDENT_SECOND_LINE = new FormatStyle(
                    "CONCAT_CHILDREN_TO_LINE_END_INDENT_SECOND_LINE");

            /** Try to put the first child after the root if possible. */
            public static final FormatStyle CONCAT_FIRST_CHILD = new FormatStyle(
                    "CONCAT_FIRST_CHILD");

            /**
             * this format is used for applications - it is very similar to
             * CHILDREN_IN_ONE_LEVEL except that is has special conditions for
             * paren
             */
            public static final FormatStyle APPLICATION = new FormatStyle(
                    "APPLICATION");

            /**
             * This style is used when code is followed by a comment on the same
             * line When SourceTextNode of this style has to be broken across
             * lines, the comment is placed BEFORE the code This is unusual as
             * it thereby reorders lexemes.
             */
            public static final FormatStyle CODE_FOLLOWED_BY_COMMENT = new FormatStyle(
                    "CODE_AND_COMMENT");

            /**
             * This style is used when comments preceed some code. The comments
             * are always rendered on separate lines before the code at the same
             * indent level.
             */
            public static final FormatStyle COMMENTS_FOLLOWED_BY_CODE = new FormatStyle(
                    "COMMENTS_AND_CODE");

            /** String describing this instance of FormatStyle. */
            private final String description;

            private FormatStyle(String description) {
                this.description = description;
            }

            public String toString() {
                return description;
            }
        }

    }

}
